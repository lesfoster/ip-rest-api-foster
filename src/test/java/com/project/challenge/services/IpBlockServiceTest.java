package com.project.challenge.services;

import com.project.challenge.model.CIDR;
import com.project.challenge.model.IpBlockDescriptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

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

    private IpBlockService blockServiceSmall;
    private IpBlockService blockServiceLarge;
    private static Logger logger = LogManager.getLogger(IpBlockServiceTest.class);

    @Before
    public void setup() throws Exception {
        blockServiceSmall = getBlockService(SMALL_CIDR);
        blockServiceLarge = getBlockService(LARGE_CIDR);
    }

    @Test
    public void testDescriptorCreationLowEdgeCase() throws Exception {

        // Exercise SUT
        IpBlockDescriptor bitBlockDescriptor = blockServiceSmall.getBitBlockDescriptor(IP_FOR_TEST );

        // Assert against SUT
        Assert.assertEquals("Unexpected bit block num", 0, bitBlockDescriptor.getBlockNum());
        Assert.assertEquals("Unexpected bit block offset", 0, bitBlockDescriptor.getBlockOffset());
        Assert.assertEquals("Unexpected IP: what happened?", IP_FOR_TEST, bitBlockDescriptor.getIpAddr());
    }

    @Test
    public void testDescriptorCreationHighEdgeCase() throws Exception {

        // Exercise SUT
        IpBlockDescriptor bitBlockDescriptor = blockServiceSmall.getBitBlockDescriptor(IP_FOR_MAX_IN_SMALL_RANGE);

        // Assert against SUT
        Assert.assertEquals("Unexpected bit block num", 0, bitBlockDescriptor.getBlockNum());
        Assert.assertEquals("Unexpected bit block offset", 253, bitBlockDescriptor.getBlockOffset());
        Assert.assertEquals("Unexpected IP: what happened?", IP_FOR_MAX_IN_SMALL_RANGE, bitBlockDescriptor.getIpAddr());
    }

    @Test
    public void testDescriptorCreationLargeCIDR() throws Exception {

        // Exercise SUT
        IpBlockDescriptor bitBlockDescriptor = blockServiceLarge.getBitBlockDescriptor(IP_FOR_MAX_IN_LARGE_RANGE);

        // Assert against SUT.  64K range.
        Assert.assertEquals("Unexpected bit block num", 63, bitBlockDescriptor.getBlockNum());
        Assert.assertEquals("Unexpected bit block offset", 1023, bitBlockDescriptor.getBlockOffset());
        Assert.assertEquals("Unexpected IP: what happened?", IP_FOR_MAX_IN_LARGE_RANGE, bitBlockDescriptor.getIpAddr());
        Assert.assertEquals("Unexpected block count", 63, blockServiceLarge.getBlockCount());
    }

    @Test
    public void cidrCrawlTest() throws Exception {
        String ipBase = "10.0.0.";
        IpBlockService crawlService = getBlockService(ipBase + "1/24");
        long prevOffset = -1;
        boolean success = true;
        for (int i = 1; i < 253; i++) {
            String nextIp = ipBase + i;
            IpBlockDescriptor bitBlockDescriptor = crawlService.getBitBlockDescriptor(nextIp);
            long blockOffset = bitBlockDescriptor.getBlockOffset();
            if (blockOffset - prevOffset != 1) {
                logger.warn("Unexpected offset in series {} .. {}", prevOffset, blockOffset);
                success = false;
            }
            prevOffset = blockOffset;
        }
        Assert.assertTrue("Crawl sequence failed", success);

        IpBlockDescriptor highDescriptor = crawlService.getBitBlockDescriptor("10.0.1.0");
        Assert.assertEquals("Unexpected offset", 255, highDescriptor.getBlockOffset());
        Assert.assertEquals("Unexpected blocknum", 0, highDescriptor.getBlockNum());

        IpBlockDescriptor beyondDescriptor = crawlService.getBitBlockDescriptor("10.1.0.0");
        Assert.assertEquals("Unexpected offset", 1023, beyondDescriptor.getBlockOffset());
        Assert.assertEquals("Unexpected blocknum", 63, beyondDescriptor.getBlockNum());
    }

    private IpBlockService getBlockService(String cidr) throws InvalidFormatException {
        CidrStateService css = Mockito.mock(CidrStateService.class);
        Ipv4ConversionService ics = new Ipv4ConversionServiceImpl();
        IpBlockService service = new IpBlockService(css, ics);

        CIDR mockBlock = ics.toCidr(cidr);
        when(css.getCidrBlock()).thenReturn(mockBlock);
        return service;
    }
}
