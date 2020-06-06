package com.project.challenge.services;

import com.project.challenge.model.CIDR;
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

    /**
     * Construct with all injected services.
     *
     * @param conversionService for handling cidr block format.
     */
    @Autowired
    public CidrStateServiceImpl(Ipv4ConversionService conversionService) {
        this.conversionService = conversionService;
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
        if (isPopulated()) {
            throw new CidrExistsException();
        }
        this.cidrBlock = conversionService.toCidr(cidrBlockStr);
    }

    /**
     * Establish the CIDR block.
     * @param cidrBlock populate w/ this one.
     */
    public void setCidrBlock(CIDR cidrBlock) {
        this.cidrBlock = cidrBlock;
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
