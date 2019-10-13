package org.b612foundation.adam.datamodel.estimation;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class OorbMeasurementWriterTest {

    @Test
    public void testLsstMeasurementConvert() throws IOException {
        var expectedLine = "5021 29824.5685211930 O 322.1144215759  -8.3891058546  22.3317800142 X I11   0.0010000000   0.0010000000  -1.0000000000  0.1000000E+01 X";
        var csvFile = Paths.get("src/test/resources/single_object_lsst.csv");
        var measurements = LsstMeasurementReader.readSingleObjectCsvFile(csvFile, "I11  ");
        var actual = new OorbMeasurementWriter(0.001, 0.001, -1.0).convertWriteLsstToDesFormat(measurements.get(0));
        assertEquals(expectedLine, actual);
    }
    @Test
    public void testLsstMeasurementsToDesFileWriter() throws IOException {
        var expectedLine = "5021 29824.5685211930 O 322.1144215759  -8.3891058546  22.3317800142 X I11   1.0000000000   1.0000000000   1.0000000000  0.1000000E+01 X";
        var csvFile = Paths.get("src/test/resources/single_object_lsst.csv");
        var measurements = LsstMeasurementReader.readSingleObjectCsvFile(csvFile, "I11  ");
        var tmpDesFile = Files.createTempFile("scratchObs", ".des");

        new OorbMeasurementWriter().writeLsstMeasurementsToDesFile(tmpDesFile.toAbsolutePath(), measurements);
        var lines = Files.readAllLines(tmpDesFile);
        assertEquals(2666, lines.size());
        assertEquals(expectedLine, lines.get(0));
    }
}
