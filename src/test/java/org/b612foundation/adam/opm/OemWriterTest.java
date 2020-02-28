package org.b612foundation.adam.opm;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertEquals;

public class OemWriterTest {

  @Test
  public void testStkEphemerisHappyPath() throws NoSuchAlgorithmException {
    String sha256Expected = "de1612faa48c08b58e2a8f4119cf90c79f87866f234c9f20bdfdd64b6a19fda3";
    OrbitEphemerisMessage oem = OdmScenarioBuilder.buildOemWithCovariance();
    String actualStkString = OemWriter.toStkEphemerisString(oem);
    String sha256Actual = getSha256HashString(actualStkString);
    assertEquals(sha256Expected, sha256Actual);
  }

  @Test
  public void testCcsdsEphemerisHappyPath() throws NoSuchAlgorithmException {
    String sha256Expected = "dcdcb1dc70b2e95bd9e875c69bad63b7949ec273569ebd63e256f9950b66066c";
    OrbitEphemerisMessage oem = OdmScenarioBuilder.buildOemWithCovariance();
    String ccsdsOemString = OemWriter.toCcsdsOemString(oem);
    String sha256Actual = getSha256HashString(ccsdsOemString);
    assertEquals(sha256Expected, sha256Actual);
  }


  private static String getSha256HashString(String string) throws NoSuchAlgorithmException {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] encodedhash = digest.digest(
        string.getBytes(StandardCharsets.UTF_8));

    StringBuilder hexString = new StringBuilder();
    for (byte b : encodedhash) {
      String hex = Integer.toHexString(0xff & b);
      if (hex.length() == 1) hexString.append('0');
      hexString.append(hex);
    }
    return hexString.toString();
  }
}
