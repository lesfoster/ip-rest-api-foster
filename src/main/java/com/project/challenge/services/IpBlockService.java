package com.project.challenge.services;

import com.project.challenge.model.CIDR;
import com.project.challenge.model.IpBlockDescriptor;

/**
 * Implementations will provide a means for working with blocks of bits that represent the states of IP
 * addresses within CIDR blocks.
 */
public interface IpBlockService {
    /**
     * Create the descriptor for accessing a bit that represents the IP address given.
     *
     * @param cidrBlock This block is base for descriptor.
     * @param ipAddr looking this up.
     * @return where is this IP address' state represented in the block store?
     * @throws InvalidFormatException if bad IP address given.
     */
    IpBlockDescriptor getBitBlockDescriptor(CIDR cidrBlock, String ipAddr) throws InvalidFormatException;

    /**
     * How many blocks will it take to hold the currently-set CIDR block?
     *
     * @param cidrBlock find block count for this CIDR block.
     * @return the block count.
     */
    int getBlockCount(CIDR cidrBlock);

    /**
     * Size of each block in bits.
     *
     * @return bits in block
     */
    long getBlockSize();

    /**
     * Create and return an empty block of the size appropriate to match an encoded
     * block of bits that are all zero.
     *
     * @return blank block of size as given in
     * @see long IpBlockService.getBlockSize()
     */
    String getEmptyBlock();
}
