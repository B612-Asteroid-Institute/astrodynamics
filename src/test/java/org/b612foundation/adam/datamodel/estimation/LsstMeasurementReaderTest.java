package org.b612foundation.adam.datamodel.estimation;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class LsstMeasurementReaderTest {

    @Test
    public void testReadSingleObjectCsv() throws IOException {
        var expectedSize = 2666;
        var tol = 1e-15;
        var csvFile = Paths.get("src/test/resources/single_object_lsst.csv");
        var measurements = LsstMeasurementReader.readSingleObjectCsvFile(csvFile);
        assertEquals(expectedSize, measurements.size());

        var m = measurements.get(0);
        assertEquals("5021", m.getObjectId());
        assertEquals(83215, m.getVisitId());
        assertEquals(29824.568521193,m.getMjd(),tol);
        assertEquals(322.11442157590005, m.getRaDeg(), tol);
        assertEquals(-8.389105854602517, m.getDecDeg(), tol);
        assertEquals(-0.250840048829013, m.getVra(), tol);
        assertEquals(0.12363744774679032, m.getVdec(), tol);
        assertEquals(14.79412792018423, m.getPhase(), tol);
        assertEquals(150.41490763847375, m.getSolarElon(), tol);
        assertEquals(1.965296988023812, m.getHelioD(), tol);
        assertEquals(1.0161971888638317, m.getGeoD(), tol);
        assertEquals(22.331780014225988, m.getVmag(), tol);
        assertEquals(234.93065003540593, m.getTrueAnomaly(), tol);
        assertEquals(20.0, m.getH(), tol);
        assertEquals(0.15, m.getG(), tol);
        assertEquals(322.36605299999997, m.getRaFieldDeg(), tol);
        assertEquals(-9.930417, m.getDecFieldDeg(), tol);
        assertEquals(29824.568521193, m.getMjdField(), tol);
        assertEquals("i", m.getFilter());
        assertEquals(34.0, m.getVisitTime(), tol);
        assertEquals(30.0, m.getVisitExposureTime(), tol);
        assertEquals(1.02169638990029, m.getSeeingFwhmEff(), tol);
        assertEquals(23.4682753451754, m.getFiveSigmaDepth(), tol);
        assertEquals("S", m.getOrbitClass());
        assertEquals(0.22505039463986154, m.getPV(), tol);
        assertEquals(22.786780014225986, m.getMag(), tol);
        assertEquals(1.0, m.getDetected(), tol);
        assertEquals(0.28014642938427686, m.getDkm(), tol);
     }
}
