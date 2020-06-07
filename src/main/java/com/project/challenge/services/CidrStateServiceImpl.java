package com.project.challenge.services;

import com.project.challenge.entities.CidrBitBlock;
import com.project.challenge.model.CIDR;
import com.project.challenge.repositories.BlockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This service more-or-less caches the state of the CIDR block.  Prior to establishing a CIDR block,
 * which is kept here, there will not be one.  After it is established, it needs to be stored here.
 */
@Service
public class CidrStateServiceImpl implements CidrStateService {
    private CIDR cidrBlock = null;
    private Ipv4ConversionService conversionService;
    private IpBlockService blockService;
    private BlockRepository blockRepository;

    /**
     * Construct with all injected services.
     *
     * @param conversionService for handling cidr block format.
     * @param blockService for calculating block-related things
     * @param blockRepository for serializing.
     */
    @Autowired
    public CidrStateServiceImpl(
            Ipv4ConversionService conversionService,
            IpBlockService blockService,
            BlockRepository blockRepository
    ) {
        this.conversionService = conversionService;
        this.blockService = blockService;
        this.blockRepository = blockRepository;
    }

    /**
     * Return the cidr block.  May return null.
     * @return CIDR block.
     */
    public CIDR getCidrBlock() {
        return cidrBlock;
    }

    /**
     * Establish the CIDR block.
     * @param cidrBlockStr populate w/ this one.
     */
    public void setCidrBlock(String cidrBlockStr) throws CidrExistsException, InvalidFormatException {
        setCidrBlock(conversionService.toCidr(cidrBlockStr));
    }

    /**
     * Establish the CIDR block.
     * @param cidrBlock populate w/ this one.
     */
    public void setCidrBlock(CIDR cidrBlock) throws CidrExistsException {
        if (isPopulated()) {
            throw new CidrExistsException();
        }
        this.cidrBlock = cidrBlock;
        int blockCount = blockService.getBlockCount(this.cidrBlock);
        String emptyBlockStr = blockService.getEmptyBlock();

        // Make empty blocks with hardcoded identifiers.
        for (int i = 0; i < blockCount; i++) {
            // Write one block for each.
            CidrBitBlock bitBlock = new CidrBitBlock();
            bitBlock.setId(i);
            bitBlock.setEncodedBits(emptyBlockStr);
            blockRepository.save(bitBlock);
        }

    }

    /**
     * Assert populated state based on presence of CIDR block.
     *
     * @return T = already populated
     */
    @Override
    public boolean isPopulated() {
        return this.cidrBlock != null;
    }

}
