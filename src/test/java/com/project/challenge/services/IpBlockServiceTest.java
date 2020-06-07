package com.project.challenge.services;

import com.project.challenge.model.CIDR;
import com.project.challenge.model.IpBlockDescriptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Shaking down the block service.
 */
public class IpBlockServiceTest {
    private static final String IP_FOR_TEST = "128.0.77.1";
    private static final String IP_FOR_LARGE_RANGE_TEST = "72.10.0.0";
    private static final String IP_FOR_MAX_IN_LARGE_RANGE = "72.11.0.0";
    private static final String IP_FOR_MAX_IN_SMALL_RANGE = "128.0.77.254";
    private static final String SMALL_CIDR = IP_FOR_TEST + "/24";
    private static final String LARGE_CIDR = IP_FOR_LARGE_RANGE_TEST + "/16";

    private Ipv4ConversionService conversionService = new Ipv4ConversionServiceImpl();
    private IpBlockService blockServiceSmall;
    private IpBlockService blockServiceLarge;
    private CIDR smallCidr;
    private CIDR largeCidr;

    private static Logger logger = LogManager.getLogger(IpBlockServiceTest.class);

    @Before
    public void setup() throws Exception {
        smallCidr = conversionService.toCidr(SMALL_CIDR);
        largeCidr = conversionService.toCidr(LARGE_CIDR);
        blockServiceSmall = getBlockService();
        blockServiceLarge = getBlockService();
    }

    @Test
    public void testDescriptorCreationLowEdgeCase() throws Exception {

        // Exercise SUT
        IpBlockDescriptor bitBlockDescriptor = blockServiceSmall.getBitBlockDescriptor(smallCidr, IP_FOR_TEST );

        // Assert against SUT
        Assert.assertEquals("Unexpected bit block num", 0, bitBlockDescriptor.getBlockNum());
        Assert.assertEquals("Unexpected bit block offset", 0, bitBlockDescriptor.getBlockOffset());
        Assert.assertEquals("Unexpected IP: what happened?", IP_FOR_TEST, bitBlockDescriptor.getIpAddr());
    }

    @Test
    public void testDescriptorCreationHighEdgeCase() throws Exception {

        // Exercise SUT
        IpBlockDescriptor bitBlockDescriptor = blockServiceSmall.getBitBlockDescriptor(smallCidr, IP_FOR_MAX_IN_SMALL_RANGE);

        // Assert against SUT
        Assert.assertEquals("Unexpected bit block num", 0, bitBlockDescriptor.getBlockNum());
        Assert.assertEquals("Unexpected bit block offset", 253, bitBlockDescriptor.getBlockOffset());
        Assert.assertEquals("Unexpected IP: what happened?", IP_FOR_MAX_IN_SMALL_RANGE, bitBlockDescriptor.getIpAddr());
    }

    @Test
    public void testDescriptorCreationLargeCIDR() throws Exception {

        // Exercise SUT
        IpBlockDescriptor bitBlockDescriptor = blockServiceLarge.getBitBlockDescriptor(largeCidr, IP_FOR_MAX_IN_LARGE_RANGE);

        // Assert against SUT.  64K range.
        Assert.assertEquals("Unexpected bit block num", 63, bitBlockDescriptor.getBlockNum());
        Assert.assertEquals("Unexpected bit block offset", 1023, bitBlockDescriptor.getBlockOffset());
        Assert.assertEquals("Unexpected IP: what happened?", IP_FOR_MAX_IN_LARGE_RANGE, bitBlockDescriptor.getIpAddr());
        Assert.assertEquals("Unexpected block count", 63, blockServiceLarge.getBlockCount(largeCidr));
    }

    @Test
    public void cidrCrawlTest() throws Exception {
        String ipBase = "10.0.0.";
        IpBlockService crawlService = getBlockService();
        CIDR cidr = conversionService.toCidr(ipBase + "1/24");
        long prevOffset = -1;
        boolean success = true;
        for (int i = 1; i < 253; i++) {
            String nextIp = ipBase + i;
            IpBlockDescriptor bitBlockDescriptor = crawlService.getBitBlockDescriptor(cidr, nextIp);
            long blockOffset = bitBlockDescriptor.getBlockOffset();
            if (blockOffset - prevOffset != 1) {
                logger.warn("Unexpected offset in series {} .. {}", prevOffset, blockOffset);
                success = false;
            }
            prevOffset = blockOffset;
        }
        Assert.assertTrue("Crawl sequence failed", success);

        IpBlockDescriptor highDescriptor = crawlService.getBitBlockDescriptor(cidr, "10.0.1.0");
        Assert.assertEquals("Unexpected offset", 255, highDescriptor.getBlockOffset());
        Assert.assertEquals("Unexpected blocknum", 0, highDescriptor.getBlockNum());

        IpBlockDescriptor beyondDescriptor = crawlService.getBitBlockDescriptor(cidr, "10.1.0.0");
        Assert.assertEquals("Unexpected offset", 1023, beyondDescriptor.getBlockOffset());
        Assert.assertEquals("Unexpected blocknum", 63, beyondDescriptor.getBlockNum());
    }

    private IpBlockService getBlockService() throws InvalidFormatException {
        Ipv4ConversionService ics = new Ipv4ConversionServiceImpl();
        IpBlockService service = new IpBlockServiceImpl(ics);

        return service;
    }
}
