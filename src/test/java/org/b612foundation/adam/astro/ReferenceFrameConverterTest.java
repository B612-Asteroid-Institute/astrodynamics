package org.b612foundation.adam.astro;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ReferenceFrameConverterTest {
    private double[] jplIcrf = {
            -3.02798551406142116e+08, 2.55329323370542288e+08, 1.28988134638909295e+08,
            -1.23882760442102402e+01, -9.12629930051682159e+00, -5.28447139928818110e+00
    };

    private double[] jplEcliptic = {
            -3.02798551406142116e+08, 2.85568607458362281e+08, 1.67801276746095717e+07,
            -1.23882760442102402e+01, -1.04752579051110999e+01, -1.21817433642154005e+00
    };

    @Test
    public void testIcrfToJplEcliptic() {
        double[] actualEcliptic = ReferenceFrameConverter.convertICRFToJplEcliptic(jplIcrf);
        testPosVel(jplEcliptic, actualEcliptic, 1.5e-7, 1e-14);
    }

    @Test
    public void testJplEclipticToIcrf() {
        double[] actualIcrf = ReferenceFrameConverter.convertJplEclipticToICRF(jplEcliptic);
        testPosVel(jplIcrf, actualIcrf, 1.5e-7, 1e-14);
    }

    private void testPosVel(double[] expected, double[] actual, double posTolerance, double velTolerance) {
        for(int i = 0; i < expected.length; i++) {
            double tolerance = i <= 2 ? posTolerance : velTolerance;
            double delta = Math.abs(expected[i] - actual[i]);
            String message = String.format("%d index exceeds tolerance %e > %e", i, delta, tolerance);
            assertEquals(message, expected[i], actual[i], tolerance);
        }
    }
}
