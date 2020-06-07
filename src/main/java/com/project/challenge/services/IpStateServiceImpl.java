package com.project.challenge.services;

import com.project.challenge.entities.CidrBitBlock;
import com.project.challenge.model.*;
import com.project.challenge.repositories.BlockRepository;
import org.apache.commons.net.util.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Implements the state service transitioning states of IP addresses within CIDR range.
 */
@Service
public class IpStateServiceImpl implements IpStateService {
    private Ipv4ConversionService conversionService;
    private CidrStateService cidrStateService;
    private IpBlockService blockService;
    private BlockRepository blockRepository;

    private Base64 base64 = new Base64();

    private static Logger logger = LogManager.getLogger(IpStateServiceImpl.class);

    /**
     * Construct with all required inputs.
     * @param conversionService - util for remarshalling
     * @param cidrStateService - has the CIDR block in use.
     */
    @Autowired
    public IpStateServiceImpl(
            Ipv4ConversionService conversionService,
            CidrStateService cidrStateService,
            IpBlockService blockService,
            BlockRepository blockRepository
    ) {
        this.conversionService = conversionService;
        this.cidrStateService = cidrStateService;
        this.blockRepository = blockRepository;
        this.blockService = blockService;
    }

    /**
     * Push the state of the IP address to "acquired" - out of pool.
     *
     * @param ipAddr standard IP address format.
     * @throws IpStateServiceException in event of bad inputs or bad CDIR state.
     */
    @Override
    public void setIpStateAcquired(String ipAddr) throws IpStateServiceException {
        checkCidrState();
        checkInputValidity(ipAddr);

        logger.debug(
                "IP address {} is within range, and will be marked as acquired if it is not already acquired.",
                ipAddr
        );

        setIpAddressState(ipAddr, true);
    }

    /**
     * Push the state of the IP address to "freed" - back into pool.
     *
     * @param ipAddr standard IP address format.
     * @throws IpStateServiceException in event of bad inputs or bad CDIR state.
     */
    @Override
    public void setIpStateReleased(String ipAddr) throws IpStateServiceException {
        checkCidrState();
        checkInputValidity(ipAddr);

        logger.debug(
                "IP address {} is within range, and will be marked as freed if it is not already free.",
                ipAddr
        );

        setIpAddressState(ipAddr, false);
    }

    /**
     * Return full list of all managed IPs vs each one's status.
     *
     * @return full report
     * @throws IpStateServiceException if nothing to report.
     */
    @Override
    public IpReport getIpReport() throws IpStateServiceException {
        checkCidrState();

        return buildReport();
    }

    /**
     * Logic of how to set state disposition.  If has been acquired and acquired is being requested, then this
     * will not be allowed.
     *
     * @param ipAddr which IP to acquire (or free).
     * @param targetAcquiredState T=acquire; F=free
     * @throws IpStateServiceException thrown if acquire is requested, but already acquired.
     */
    private void setIpAddressState(String ipAddr, boolean targetAcquiredState) throws IpStateServiceException {
        // Need to figure out where this thing is.
        IpBlockDescriptor descriptor = getBlockDescriptor(ipAddr);
        if (descriptor == null) {
            throw new IpStateServiceException(new IllegalArgumentException("No descriptor found for Ip Address"));
        }
        Optional<CidrBitBlock> bitBlockOptional = blockRepository.findById((int)descriptor.getBlockNum());
        if (bitBlockOptional.isPresent()) {
            CidrBitBlock bitBlock = bitBlockOptional.get();
            BitSet bitSet = BitSet.valueOf(base64.decode(bitBlock.getEncodedBits()));
            final int blockOffset = (int) descriptor.getBlockOffset();
            boolean hasBeenAcquired = bitSet.get( blockOffset );

            if (targetAcquiredState   &&   hasBeenAcquired) {
                // Too late!  This one is already in use.
                throw new IpStateServiceException(new IllegalArgumentException("IP address already acquired: " + ipAddr));
            } else {
                bitSet.set( blockOffset, !hasBeenAcquired );

                bitBlock.setEncodedBits( base64.encodeToString( bitSet.toByteArray() ) );
                blockRepository.save(bitBlock);
            }

        } else {
            throw new IpStateServiceException(new NullPointerException("No block found for " + ipAddr));
        }
    }

    /**
     * Logic of extracting all IP addresses from all blocks.
     */
    private IpReport buildReport() {
        // Need to figure out where this thing is.
        List<CidrBitBlock> allCidrBlocks = blockRepository.findAll();

        final CIDR cidrBlock = cidrStateService.getCidrBlock();

        Long startingAddressOfCidr = cidrBlock.getStartingAddrLong();
        Long endingAddressOfCidr = cidrBlock.getEndingAddrLong();
        Long addressSpanOfCidr = endingAddressOfCidr - startingAddressOfCidr;

        final long blockSize = blockService.getBlockSize();

        IpReport report = new IpReport();
        report.setCidr(cidrBlock.getCidrBlockNotation());
        Map<String,IpCheckoutState> ipStatusMap = new HashMap<>();
        report.setIpStatus(ipStatusMap);

        // Going through all blocks found.
        allCidrBlocks.forEach(bitBlock -> {
            // Must decode this block, and iterate through all its IP addresses.
            int blockNumber = bitBlock.getId();
            Long startingAddressInBlock = startingAddressOfCidr + (blockNumber * blockSize);
            long bitsInBlock = blockSize;
            if (startingAddressInBlock + blockSize > addressSpanOfCidr) {
                bitsInBlock = addressSpanOfCidr % blockSize;
            }

            BitSet bitSet = BitSet.valueOf(base64.decode(bitBlock.getEncodedBits()));
            Long endingAddressInBlock = startingAddressInBlock + bitsInBlock;

            int blockOffset = 0;
            for (long l = startingAddressInBlock; l <= endingAddressInBlock; l++) {
                ipStatusMap.put( conversionService.getLongAsIp(l), checkoutState( bitSet.get( blockOffset ) ) );
                blockOffset ++;
            }
        });

        return report;
    }

    /** Only managed, valid IP addresses can have states modified. */
    private void checkInputValidity(String ipAddress) throws IpStateServiceException {
        try {
            if (! conversionService.isIpInCidrRange(ipAddress, this.cidrStateService.getCidrBlock().getCidrBlockNotation())) {
                throw new IpStateServiceException(new IllegalArgumentException("Cannot change state of IP address not managed by this application."));
            }
        } catch (InvalidFormatException ife) {
            throw new IpStateServiceException(ife);
        }
    }

    /** Only managed, valid IP addresses can have states modified. */
    private void checkCidrState() throws IpStateServiceException {
        if (! cidrStateService.isPopulated()) {
            throw new IpStateServiceException(new IllegalStateException("CIDR block not populated"));
        }
    }

    /** Decode booleans into checkout state. */
    private IpCheckoutState checkoutState(boolean isCheckedOut) {
        if (isCheckedOut) {
            return IpCheckoutState.ACQUIRED;
        } else {
            return IpCheckoutState.FREE;
        }
    }

    /**
     * Get the descriptor telling all about where this IP address falls within the blocks of the
     * repo.
     *
     * @param ipAddr IP address within range.  Validate at caller.
     * @return bean with metadata.
     */
    private IpBlockDescriptor getBlockDescriptor(String ipAddr) {
        IpBlockDescriptor ipBlockDescriptor = null;
        try {
            ipBlockDescriptor = blockService.getBitBlockDescriptor(cidrStateService.getCidrBlock(), ipAddr);
        } catch (InvalidFormatException ife) {
            // Should not happen.  Should validated by caller.
            logger.warn(ife);
        }
        return ipBlockDescriptor;
    }

}
