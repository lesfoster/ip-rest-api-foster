package com.project.challenge.services;

import com.project.challenge.model.CIDR;
import com.project.challenge.model.IpBlockDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

public class IpBlockService {
    private static final long BIT_BLOCK_SIZE = 1024;

    private CidrStateService cidrStateService;
    private Ipv4ConversionService conversionService;

    @Autowired
    public IpBlockService(CidrStateService cidrStateService, Ipv4ConversionService conversionService) {
        this.cidrStateService = cidrStateService;
        this.conversionService = conversionService;
    }

    /**
     * Create the descriptor for accessing a bit that represents the IP address given.
     *
     * @param ipAddr looking this up.
     * @return where is this IP address' state represented in the block store?
     * @throws InvalidFormatException if bad IP address given.
     */
    public IpBlockDescriptor getBitBlockDescriptor(String ipAddr) throws InvalidFormatException {
        long ipLocation = Integer.toUnsignedLong(conversionService.getIpAsInt(ipAddr));
        long offsetLoc = getIpAddrOffsetWithinCidr(ipLocation);
        long blockNum = getBitBlockNumber(offsetLoc);
        long blockOffset = getBlockOffset(offsetLoc);

        return new IpBlockDescriptor(ipAddr, blockNum, blockOffset);
    }

    /**
     * How many blocks will it take to hold the currently-set CIDR block?
     *
     * @return the block count.
     */
    public int getBlockCount() {
        final CIDR cidrBlock = cidrStateService.getCidrBlock();
        if (cidrBlock == null) {
            return 0;
        }
        return (int)(cidrBlock.getSize() / BIT_BLOCK_SIZE);
    }

    /**
     * A CIDR range has a starting and ending position.  This method takes the "integer location" of
     * this IP address and subtracts from that the start of the CIDR block.
     *
     * @param ipLocation absolute unsigned int location of IP address.
     * @return location within CIDR block.
     */
    private long getIpAddrOffsetWithinCidr(long ipLocation) {
        return ipLocation - cidrStateService.getCidrBlock().getStartingAddrLong();
    }

    /**
     * All acquired/free states of IP addresses are represented as bit values.  This tells location
     * within the block containing it.
     *
     * @param offsetLoc location within the CIDR's space
     * @return offset within the block.
     */
    private long getBlockOffset(long offsetLoc) {
        return offsetLoc % BIT_BLOCK_SIZE;
    }

    /**
     * All acquired/free states of IP addresses are represented as bit values.  This tells which
     * block contains this one.
     *
     * @param offsetLoc offset within CIDR's space
     * @return which block (given the chosen block size).
     */
    private long getBitBlockNumber(long offsetLoc) {
        return offsetLoc / BIT_BLOCK_SIZE;
    }
}
