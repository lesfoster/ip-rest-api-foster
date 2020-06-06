package com.project.challenge.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implements the state service transitioning states of IP addresses within CIDR range.
 */
@Service
public class IpStateServiceImpl implements IpStateService {
    private Ipv4ConversionService conversionService;
    private CidrStateService cidrStateService;

    private static Logger logger = LogManager.getLogger(IpStateServiceImpl.class);

    /**
     * Construct with all required inputs.
     * @param conversionService - util for remarshalling
     * @param cidrStateService - has the CIDR block in use.
     */
    @Autowired
    public IpStateServiceImpl(Ipv4ConversionService conversionService, CidrStateService cidrStateService) {
        this.conversionService = conversionService;
        this.cidrStateService = cidrStateService;
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

        logger.debug("IP address {} is within range, and will be marked as acquired if it is not already acquired.");
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

        logger.debug("IP address {} is within range, and will be marked as freed if it is not already free.");
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

}
