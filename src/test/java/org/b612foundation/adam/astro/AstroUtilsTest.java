package org.b612foundation.adam.astro;

import org.b612foundation.adam.astro.AstroUtils;
import org.junit.Test;

import java.time.LocalDateTime;

import static junit.framework.TestCase.assertEquals;

public class AstroUtilsTest {

    @Test
    public void testLocalDateTimeFromMJD() {
        double mjd = 49987;
        LocalDateTime expected = LocalDateTime.of(1995, 9, 27, 0, 0, 0);
        LocalDateTime actual = AstroUtils.localDateTimefromMJD(mjd);
        assertEquals(expected, actual);

        mjd = 54617.46681714;
        expected = LocalDateTime.of(2008, 5, 31, 11, 12, 13);
        actual = AstroUtils.localDateTimefromMJD(mjd);
        assertEquals(expected, actual);

        mjd = 58485.000800741;
        expected = LocalDateTime.of(2019, 1, 2, 0, 1, 9);
        actual = AstroUtils.localDateTimefromMJD(mjd);
        assertEquals(expected.toString(), actual.toString());

    }
}
