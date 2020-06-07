package com.project.challenge.model;

/**
 * Immutable Data Transfer Object for status of IP address.  The address can be identified,
 * and its state understood, in terms of the system.
 */
public class IpStatus {
    private String ipAddress;
    private IpCheckoutState checkoutState;

    public IpStatus(String ipAddress, IpCheckoutState checkoutState) {
        this.ipAddress = ipAddress;
        this.checkoutState = checkoutState;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public IpCheckoutState getCheckoutState() {
        return checkoutState;
    }
}
