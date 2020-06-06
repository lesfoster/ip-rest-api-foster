package com.project.challenge.services;

import com.google.common.net.InetAddresses;
import com.project.challenge.model.CIDR;
import org.apache.commons.net.util.SubnetUtils;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * IP addresses are often handed around as period-jointed strings.  However, that is simply a human-readable
 * way to reference what is essentially a four-byte (in IPv4) integer.
 *
 * This service is to support any conversions between convenient forms.
 *
 * Unless configured in app props, not using inclusive host count.
 */
@Service
public class Ipv4ConversionServiceImpl implements Ipv4ConversionService {
    // Borrowed from Subnet Utils.
    private static final Pattern CIDR_PATTERN = Pattern.compile("(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})/(\\d{1,3})");

    public static final String INVALID_PI_FMT = "Invalid IP: %s";
    public static final String INVALID_CIDR_FMT = "Invalid CIDR block: %s";

    private InetAddressValidator validator = new InetAddressValidator();

    // By default, excluding .0 and .255 from IP ranges in CIDR blocks.
    private boolean inclusiveHostCount = false;

    @Value("${ipcalc.inclusivehostcount:false}")
    public void setInclusiveHostCount(Boolean flag) {
        this.inclusiveHostCount = flag;
    }

    /**
     * Checks address as valid IP.
     * ex: 128.5.3.22
     *
     * @return true if so; false otherwise.
     */
    public boolean isValidIpAddress(String ipAddress) {
        return ipAddress != null  &&  validator.isValid(ipAddress);
    }

    /**
     * Returns int-conversion of the four 8-bit representations making up the IPv4 address.
     *
     * @param ipAddress must be valid IP address.
     * @return integer version.  Signed Java style integer.
     * @throws InvalidFormatException
     */
    @Override
    public Integer getIpAsInt(String ipAddress) throws InvalidFormatException {
        if (isValidIpAddress(ipAddress)) {
            return InetAddresses.coerceToInteger(InetAddresses.forString(ipAddress));
        } else {
            throw new InvalidFormatException(INVALID_PI_FMT, ipAddress);
        }
    }

    /**
     * Test for validity of CIDR block.
     * ex: 128.5.3.22/24
     *
     * @param cidr should be in required format
     * @return true if so; false otherwise
     */
    @Override
    public boolean isValidCidr(String cidr) {
        Matcher matcher = CIDR_PATTERN.matcher(cidr);
        return matcher.matches();
    }

    /**
     * Checks whether the IP address given falls within the subnet represented by the CIDR block
     * given as other param.
     *
     * @param ipAddress is this in range?
     * @param cidr this is the range to check.
     * @return true if in range; false otherwise
     * @throws InvalidFormatException if either IP or CIDR is invalid format.
     */
    @Override
    public boolean isIpInCidrRange(String ipAddress, String cidr) throws InvalidFormatException {
        if (! isValidCidr(cidr)) {
            throw new InvalidFormatException(INVALID_CIDR_FMT, cidr);
        }
        if (! isValidIpAddress(ipAddress)) {
            throw new InvalidFormatException(INVALID_PI_FMT, ipAddress);
        }
        final SubnetUtils subnetUtils = new SubnetUtils(cidr);
        subnetUtils.setInclusiveHostCount(inclusiveHostCount);

        final SubnetUtils.SubnetInfo subnetInfo = subnetUtils.getInfo();
        return subnetInfo.isInRange(ipAddress);
    }

    /**
     * Given CIDR notation input string, convert to a convenience class.
     *
     * @param cidrStr in CIDR format
     * @return fully-constructed CIDR representation.
     * @throws InvalidFormatException
     */
    @Override
    public CIDR toCidr(String cidrStr) throws InvalidFormatException {
        if (! isValidCidr(cidrStr)) {
            throw new InvalidFormatException(INVALID_CIDR_FMT, cidrStr);
        }
        final SubnetUtils subnetUtils = new SubnetUtils(cidrStr);
        subnetUtils.setInclusiveHostCount(inclusiveHostCount);
        final SubnetUtils.SubnetInfo subnetInfo = subnetUtils.getInfo();
        Integer lowAddrInt = getIpAsInt(subnetInfo.getLowAddress());
        Integer highAddrInt = getIpAsInt(subnetInfo.getHighAddress());
        return new CIDR(
                cidrStr,
                Integer.toUnsignedLong(lowAddrInt),
                Integer.toUnsignedLong(highAddrInt),
                inclusiveHostCount);
    }

}
