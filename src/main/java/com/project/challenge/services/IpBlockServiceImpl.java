package com.project.challenge.services;

import com.project.challenge.model.CIDR;
import com.project.challenge.model.IpBlockDescriptor;
import org.apache.commons.net.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.BitSet;

public class IpBlockServiceImpl implements IpBlockService {
    private static final long BIT_BLOCK_SIZE = 1024;

    private Ipv4ConversionService conversionService;
    private String emptyBlock;

    @Autowired
    public IpBlockServiceImpl(Ipv4ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    /**
     * Create the descriptor for accessing a bit that represents the IP address given.
     *
     * @param cidrBlock This block is base for descriptor.
     * @param ipAddr looking this up.
     * @return where is this IP address' state represented in the block store?
     * @throws InvalidFormatException if bad IP address given.
     */
    @Override
    public IpBlockDescriptor getBitBlockDescriptor(CIDR cidrBlock, String ipAddr) throws InvalidFormatException {
        long ipLocation = Integer.toUnsignedLong(conversionService.getIpAsInt(ipAddr));
        long offsetLoc = getIpAddrOffsetWithinCidr(cidrBlock, ipLocation);
        long blockNum = getBitBlockNumber(offsetLoc);
        long blockOffset = getBlockOffset(offsetLoc);

        return new IpBlockDescriptor(ipAddr, blockNum, blockOffset);
    }

    /**
     * How many blocks will it take to hold the currently-set CIDR block?
     *
     * @param cidrBlock find block count for this CIDR block.
     * @return the block count.
     */
    @Override
    public int getBlockCount(CIDR cidrBlock) {
        if (cidrBlock == null) {
            return 0;
        }
        return (int)(cidrBlock.getSize() / BIT_BLOCK_SIZE);
    }

    /**
     * Tell how big the bit blocks shall be, in bits.
     *
     * @return block size
     */
    @Override
    public long getBlockSize() {
        return BIT_BLOCK_SIZE;
    }

    @Override
    public String getEmptyBlock() {
        if (emptyBlock == null) {
            BitSet bitSet = new BitSet((int)getBlockSize());
            emptyBlock = Base64.encodeBase64String(bitSet.toByteArray());
        }

        return emptyBlock;
    }

    /**
     * A CIDR range has a starting and ending position.  This method takes the "integer location" of
     * this IP address and subtracts from that the start of the CIDR block.
     *
     * @param cidrBlock find where IP address goes in this cidr block.
     * @param ipLocation absolute unsigned int location of IP address.
     * @return location within CIDR block.
     */
    private long getIpAddrOffsetWithinCidr(CIDR cidrBlock, long ipLocation) {
        return ipLocation - cidrBlock.getStartingAddrLong();
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
