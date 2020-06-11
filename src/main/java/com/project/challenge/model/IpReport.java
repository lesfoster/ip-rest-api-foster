package com.project.challenge.model;

import java.util.Map;

public class IpReport {
    private static final String FLAT_FIELD_SEP = "\t";
    private static final String FLAT_LINE_SEP = "\n";

    private String cidr;
    private Map<String,IpCheckoutState> ipStatus;

    public String getCidr() {
        return cidr;
    }

    public void setCidr(String cidr) {
        this.cidr = cidr;
    }

    public Map<String,IpCheckoutState> getIpStatus() {
        return ipStatus;
    }

    public void setIpStatus(Map<String,IpCheckoutState> ipStatus) {
        this.ipStatus = ipStatus;
    }

    /** Returns the entire report as a string. */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(cidr).append(FLAT_FIELD_SEP).append("status").append(FLAT_LINE_SEP);
        ipStatus.forEach((ip, state) -> sb.append(ip).append(FLAT_FIELD_SEP).append(state).append(FLAT_LINE_SEP));
        return sb.toString();
    }
}
