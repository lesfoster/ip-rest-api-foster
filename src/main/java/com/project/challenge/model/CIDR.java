package com.project.challenge.model;

/**
 * CIDR - CIDR Block with notational description conforming to https://tools.ietf.org/html/rfc4632
 * This class provides start/end range and whether it is inclusive.  Convenience class to hold
 * pre-calculated values.
 */
public class CIDR {
    public static final int MAX_SIZE = 65_536;
    private String cidrBlockNotation;
    private boolean inclusiveRange;
    private long startingAddr;
    private long endingAddr;

    /**
     * Construct with everything needed to describe the block.  This class will be immutable.
     *
     * @param cidrBlockNotation string like NNN.NNN.NNN.NNN/MM
     * @param startingAddr where does this start in integer terms? Using long to avoid sign issues.
     * @param endingAddr where does this start in integer terms? Using long to avoid sign issues.
     */
    public CIDR(String cidrBlockNotation, long startingAddr, long endingAddr) {
        this(cidrBlockNotation, startingAddr, endingAddr, false);
    }

    public CIDR(String cidrBlockNotation, long startingAddr, long endingAddr, boolean inclusiveRange) {
        this.cidrBlockNotation = cidrBlockNotation;
        this.inclusiveRange = inclusiveRange;
        this.startingAddr = startingAddr;
        this.endingAddr = endingAddr;
    }

    public boolean isInclusiveRange() {
        return inclusiveRange;
    }

    public void setInclusiveRange(boolean inclusiveRange) {
        this.inclusiveRange = inclusiveRange;
    }

    public long getStartingAddrLong() {
        return startingAddr;
    }

    public long getEndingAddrLong() {
        return endingAddr;
    }

    public String getCidrBlockNotation() {
        return cidrBlockNotation;
    }
}
