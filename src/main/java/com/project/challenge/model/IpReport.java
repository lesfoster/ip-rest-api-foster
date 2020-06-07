package com.project.challenge.model;

import java.util.Map;

public class IpReport {
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
}
