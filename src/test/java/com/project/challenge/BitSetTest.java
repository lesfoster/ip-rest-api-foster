package com.project.challenge;

import org.apache.commons.net.util.Base64;
import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.BitSet;

public class BitSetTest {

    @Test
    public void testBitSet64K() throws  Exception {
        BitSet bs = new BitSet(65_536);
        bs.set(6_000);
        bs.set(60_000);
        bs.set(600);
        bs.set(60);
        bs.set(6);

        System.out.println(bs.get(6_000) + " " + bs.get(60_000));
        System.out.println(bs.get(5_000) + " " + bs.get(50_000));
        byte[] bytes = bs.toByteArray();
        System.out.println("Makes a byte array of size " + bytes.length);
        int nonzCt = 0;
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            if (b != 0) {
                nonzCt ++;
                System.out.println("Found non-zero value at " + i);
            }
        }
        Assert.assertNotEquals("No encoded values found.", 0, nonzCt);
    }

    @Test
    public void testBitSet256() throws  Exception {
        BitSet bs = new BitSet(256);
        bs.set(250);
        bs.set(25);
        bs.set(2);
        bs.set(5);
        bs.set(1);

        System.out.println(bs.get(250) + " " + bs.get(25));
        System.out.println(bs.get(200) + " " + bs.get(20));
        byte[] bytes = bs.toByteArray();
        System.out.println("Makes a byte array of size " + bytes.length);
        int nonzCt = 0;
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            if (b != 0) {
                System.out.println("Found non-zero value at " + i);
                nonzCt ++;
            }
        }
        Assert.assertNotEquals("No encoded values found.", 0, nonzCt);
    }

    @Test
    public void testBitSet1024() throws  Exception {
        BitSet bs = getKBitSet();

        System.out.println(bs.get(1020) + " " + bs.get(25));
        System.out.println(bs.get(200) + " " + bs.get(20));
        byte[] bytes = bs.toByteArray();
        System.out.println("Makes a byte array of size " + bytes.length);
        int nonzCt = 0;
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            if (b != 0) {
                System.out.println("Found non-zero value at " + i);
                nonzCt ++;
            }
        }

        Base64 base64 = new Base64();
        byte[] encoded = base64.encode(bytes);
        System.out.println("Encoded is " + new String(encoded));

        byte[] redecoded = base64.decode(new String(encoded));
        for (int i = 0; i < redecoded.length; i++) {
            byte b = redecoded[i];
            if (b != 0) {
                System.out.println("Found non-zero value at " + i);
            }
        }
        byte[] reencoded = base64.encode(redecoded);
        Assert.assertEquals("Encode-decode-encode test fails.", new String(reencoded), new String(encoded));
        System.out.println("Encoded length is " + reencoded.length);

        Assert.assertNotEquals("No encoded values found.", 0, nonzCt);
    }

    @Test
    public void speedTrial() {
        Instant start = Instant.now();

        // Note: timings gave 5M at ~14 seconds.
        int iterations = 200_000;
        for (int i = 0; i < iterations; i++) {
            BitSet bs = getKBitSet();
            byte[] bytes = bs.toByteArray();
            Base64 base64 = new Base64();

            byte[] encoded = base64.encode(bytes);
            String encStr = new String(encoded);
            byte[] decoded = base64.decode(encStr);
            BitSet bs2 = BitSet.valueOf(decoded);
            if (! bs2.get(1020)) {
                Assert.fail("Failed recreation test from decoded bytes.");
            }
        }
        Instant end = Instant.now();
        Duration elapsedTime = Duration.between(start, end);

        System.out.println(String.format("Elapsed time is %d milliseconds for %,d iterations", elapsedTime.toMillis(), iterations));
        Assert.assertTrue("Long time to convert.", elapsedTime.toMillis() < 1500);
    }

    private BitSet getKBitSet() {
        BitSet bs = new BitSet(1024);
        bs.set(1020);
        bs.set(512);
        bs.set(250);
        bs.set(25);
        bs.set(2);
        bs.set(5);
        bs.set(1);
        return bs;
    }

}
