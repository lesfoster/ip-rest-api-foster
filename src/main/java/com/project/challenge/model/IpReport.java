package com.project.challenge.model;

import java.util.List;

public class IpReport {
    private String cidr;
    private List<IpStatus> ipStatusList;

    public String getCidr() {
        return cidr;
    }

    public void setCidr(String cidr) {
        this.cidr = cidr;
    }

    public List<IpStatus> getIpStatusList() {
        return ipStatusList;
    }

    public void setIpStatusList(List<IpStatus> ipStatusList) {
        this.ipStatusList = ipStatusList;
    }
}
