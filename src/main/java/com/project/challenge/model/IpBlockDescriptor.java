package com.project.challenge.model;

/**
 * Descriptor to tell how to access an IP address within the block store..
 */
public class IpBlockDescriptor {
    private long blockNum;
    private long blockOffset;
    private String ipAddr;

    public IpBlockDescriptor(String ipAddr, long blockNum, long blockOffset) {
        this.ipAddr = ipAddr;
        this.blockNum = blockNum;
        this.blockOffset = blockOffset;
    }

    public long getBlockNum() {
        return blockNum;
    }

    public long getBlockOffset() {
        return blockOffset;
    }

    public String getIpAddr() {
        return ipAddr;
    }
}
