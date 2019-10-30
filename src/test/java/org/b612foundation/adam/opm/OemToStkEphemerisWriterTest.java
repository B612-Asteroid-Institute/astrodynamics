package org.b612foundation.adam.opm;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertEquals;

public class OemToStkEphemerisWriterTest {

    @Test
    public void testHappyPath() throws NoSuchAlgorithmException {
        String sha256Expected = "de1612faa48c08b58e2a8f4119cf90c79f87866f234c9f20bdfdd64b6a19fda3";
        OrbitEphemerisMessage oem = OdmScenarioBuilder.buildOemWithCovariance();
        String actualStkString = OemToStkEphemerisWriter.toStkEphemerisString(oem);
        String sha256Actual = getSha256HashString(actualStkString);
        assertEquals(sha256Expected, sha256Actual);
    }

    private static String getSha256HashString(String string) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(
                string.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte b : encodedhash) {
            String hex = Integer.toHexString(0xff & b);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
