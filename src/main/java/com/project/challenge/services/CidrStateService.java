package com.project.challenge.services;

import com.project.challenge.model.CIDR;

/**
 * This service more-or-less caches the state of the CIDR block.  Prior to establishing a CIDR block,
 * which is kept here, there will not be one.  After it is established, it needs to be stored here.
 */
public interface CidrStateService {
    /**
     * Return the cidr block.  May return null.
     * @return CIDR block - constructed as convenience obj.
     */
    CIDR getCidrBlock();
    /**
     * Establish the CIDR block.
     * @param cidrBlock
     */
    void setCidrBlock(CIDR cidrBlock) throws CidrExistsException;

    /**
     * Establish CIDR block.
     *
     * @param cidrBlockNotation string rep of CIDR block.
     */
    void setCidrBlock(String cidrBlockNotation) throws CidrExistsException, InvalidFormatException;

    /**
     * Return whether any CIDR block has been populated.
     *
     * @return T=populated
     */
    boolean isPopulated();
}
