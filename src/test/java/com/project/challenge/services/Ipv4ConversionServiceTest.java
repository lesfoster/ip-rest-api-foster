package com.project.challenge.services;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests efficiency of the converter.  Expected conformity of IP addresses per:
 * https://searchnetworking.techtarget.com/answer/Can-you-assign-an-IP-address-ending-in-0-or-255
 * Expecting exclusive IP range by default.
 */
public class Ipv4ConversionServiceTest {
    private static final String VALID_IP = "128.72.53.1";
    private static final String VALID_IP_NETADDR = "128.72.53.0";
    private static final String VALID_IP_BRDCSTADDR = "128.72.53.255";
    private static final String VALID_CIDR = VALID_IP_NETADDR + "/24";
    private static final String BELOW_RANGE_VALID_IP = "128.72.52.255";
    private static final String ABOVE_RANGE_VALID_IP = "128.72.54.0";

    private static final String GENERIC_TEST_CONSTANT = "TEST";

    private Ipv4ConversionService service;

    @Before
    public void setup() {
        this.service = new Ipv4ConversionService();
    }

    @Test
    public void testValidIp() {
        Assert.assertTrue("Valid IP flagged invalid", service.isValidIpAddress(VALID_IP));
    }

    @Test
    public void testValidCIDR() {
        Assert.assertTrue("Valid CIDR flagged invalid", service.isValidCidr(VALID_CIDR));
    }

    @Test
    public void testInvalidIp() {
        Assert.assertFalse("Invalid IP flagged valid", service.isValidIpAddress(GENERIC_TEST_CONSTANT));
    }

    @Test
    public void testInvalidCIDR() {
        Assert.assertFalse("Invalid CIDR flagged valid", service.isValidCidr(GENERIC_TEST_CONSTANT));
    }

    @Test
    public void testRangeCheck() throws Exception {
        Assert.assertTrue("IP in range, flagged out of range", service.isIpInCidrRange(VALID_IP, VALID_CIDR));
        Assert.assertFalse("Low IP NOT in range, flagged in range", service.isIpInCidrRange(BELOW_RANGE_VALID_IP, VALID_CIDR));
        Assert.assertFalse("High IP NOT in range, flagged in range", service.isIpInCidrRange(ABOVE_RANGE_VALID_IP, VALID_CIDR));
        Assert.assertFalse("Network Address in Exclusive Range", service.isIpInCidrRange(VALID_IP_NETADDR, VALID_CIDR));
        Assert.assertFalse("Broadcast Address in Exclusive Range", service.isIpInCidrRange(VALID_IP_BRDCSTADDR, VALID_CIDR));
    }

    @Test
    public void testInclusiveCount() throws Exception {
        Ipv4ConversionService inclusiveService = new Ipv4ConversionService();
        inclusiveService.setInclusiveHostCount(true);
        Assert.assertTrue("Network Address not in Inclusive Range", inclusiveService.isIpInCidrRange(VALID_IP_NETADDR, VALID_CIDR));
        Assert.assertTrue("Broadcast Address not in Inclusive Range", inclusiveService.isIpInCidrRange(VALID_IP_BRDCSTADDR, VALID_CIDR));
    }
}
