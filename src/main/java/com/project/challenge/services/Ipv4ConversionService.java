package com.project.challenge.services;

import com.project.challenge.model.CIDR;
import org.springframework.stereotype.Service;

/**
 * IP addresses are often handed around as period-jointed strings.  However, that is simply a human-readable
 * way to reference what is essentially a four-byte (in IPv4) integer.
 *
 * This service is to support any conversions between convenient forms.
 *
 * Unless configured in app props, not using inclusive host count.
 */
@Service
public interface Ipv4ConversionService {

    /**
     * Checks address as valid IP.
     * ex: 128.5.3.22
     *
     * @return true if so; false otherwise.
     */
    boolean isValidIpAddress(String ipAddress);
    /**
     * Returns int-conversion of the four 8-bit representations making up the IPv4 address.
     *
     * @param ipAddress must be valid IP address.
     * @return integer version.  Signed Java style integer.
     * @throws InvalidFormatException
     */
    Integer getIpAsInt(String ipAddress) throws InvalidFormatException;

    /**
     * Converts an integer-version of the IP address back into a string-wise tuple.
     *
     * @param intAddress using long for convenience with caller.
     * @return IP tuple of format NNN.NNN.NNN.NNN
     */
    String getLongAsIp(Long intAddress);

    /**
     * Test for validity of CIDR block.
     * ex: 128.5.3.22/24
     *
     * @param cidr should be in required format
     * @return true if so; false otherwise
     */
    boolean isValidCidr(String cidr);

    /**
     * Checks whether the IP address given falls within the subnet represented by the CIDR block
     * given as other param.
     *
     * @param ipAddress is this in range?
     * @param cidr this is the range to check.
     * @return true if in range; false otherwise
     * @throws InvalidFormatException if either IP or CIDR is invalid format.
     */
    boolean isIpInCidrRange(String ipAddress, String cidr) throws InvalidFormatException;

    /**
     * Converts to internal representation "convenience" class.
     *
     * @param cidrStr in CIDR format
     * @return converted object.
     * @throws InvalidFormatException if not expected format.
     */
    CIDR toCidr(String cidrStr) throws InvalidFormatException;
}
