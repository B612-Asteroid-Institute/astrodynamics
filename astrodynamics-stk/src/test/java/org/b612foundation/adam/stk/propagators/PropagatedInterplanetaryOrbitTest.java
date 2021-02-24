package org.b612foundation.adam.stk.propagators;

import agi.foundation.time.Duration;
import agi.foundation.time.JulianDate;
import agi.foundation.time.TimeStandard;
import org.b612foundation.adam.datamodel.PropagationConfigurationFactory;
import org.b612foundation.adam.datamodel.PropagatorConfiguration;
import org.b612foundation.adam.opm.*;
import org.b612foundation.stk.StkLicense;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.b612foundation.adam.stk.PropagatorTestHelper.getOpm;
import static org.b612foundation.adam.stk.StkPropagationHelper.parseUtcAsJulian;
import static org.junit.Assert.assertNotNull;

public class PropagatedInterplanetaryOrbitTest {

  @Before
  public void addLicense() throws IOException {
    StkLicense.activate();
  }

  @Test
  public void testEpochParsingDecimals() throws Exception {
    String opm = OdmScenarioBuilder.getSimpleOpm();
    // Drop decimals in the epoch.
    opm = opm.replaceAll("EPOCH(.*)\\.\\d*", "EPOCH$1Z");
    OrbitParameterMessage parsed = OdmFormatter.parseOpmString(opm);

    Duration step = Duration.fromSeconds(86400);
    JulianDate startDate =
        new JulianDate(
            ZonedDateTime.parse("2000-01-01T11:59:27Z"), TimeStandard.getInternationalAtomicTime());
    JulianDate endDate =
        new JulianDate(
            ZonedDateTime.parse("2000-01-07T11:59:27.816Z"),
            TimeStandard.getInternationalAtomicTime());

    PropagatorConfiguration config = PropagationConfigurationFactory.getAllMajorBodiesConfig();
    PropagatedInterplanetaryOrbit orbit =
        PropagatedInterplanetaryOrbit.fromOpm(parsed, endDate, config);
    assertNotNull(orbit.getEphemeris(startDate, endDate, step));
  }

  @Test
  public void testParseMilliseconds() {
    // For extra paranoia after the datetime library replacement, make sure all
    // these parse without exceptions.
    JulianDate d1 = parseUtcAsJulian("2000-01-01T11:59:27Z");
    JulianDate d2 = parseUtcAsJulian("2000-01-01T11:59:27.816Z");
    JulianDate d3 = parseUtcAsJulian("2000-01-01T11:59:27");
    JulianDate d4 = parseUtcAsJulian("2000-01-01T11:59:27.816");
    Assert.assertEquals("No milliseconds, with or without Z", d1, d3);
    Assert.assertEquals("With milliseconds, with or without Z", d2, d4);
    Assert.assertEquals("Addition of milliseconds", d1.secondsDifference(d2), 0.816, 1e-5);
  }

  @Test
  public void testOutputtingOemFiles() throws OdmParseException {
    String opm = OdmScenarioBuilder.getSimpleOpm();
    // Drop decimals in the epoch.
    opm = opm.replaceAll("EPOCH(.*)\\.\\d*", "EPOCH$1Z");
    OrbitParameterMessage parsed = OdmFormatter.parseOpmString(opm);

    Duration step = Duration.fromSeconds(86400);
    JulianDate startDate =
        new JulianDate(
            ZonedDateTime.parse("2000-01-01T11:59:27Z"), TimeStandard.getInternationalAtomicTime());
    JulianDate endDate =
        new JulianDate(
            ZonedDateTime.parse("2000-01-07T11:59:27.816Z"),
            TimeStandard.getInternationalAtomicTime());
    // Default configuration is all planets.
    PropagatorConfiguration config = new PropagatorConfiguration();
    PropagatedInterplanetaryOrbit orbit =
        PropagatedInterplanetaryOrbit.fromOpm(parsed, endDate, config);
    OrbitEphemerisMessage oem = orbit.exportOrbitEphemerisMessage();
    assertNotNull(oem);
  }

  @Test
  public void testStkPropagation_finalStateVectorIsCorrect() {
    // initial state vector, in m and m/s:
    // [130347560.13690618, -74407287.6018632, -35247598.541470632,
    //  23.935241263310683, 27.146279819258538, 10.346605942591514]
    String epoch = "2017-10-04T00:00:00.000Z";
    StateVector stateVector =
        new StateVector()
            .setEpoch(epoch)
            .setX(130347560.13690618)
            .setY(-74407287.6018632)
            .setZ(-35247598.541470632)
            .setX_dot(23.935241263310683)
            .setY_dot(27.146279819258538)
            .setZ_dot(10.346605942591514);
    OrbitParameterMessage opm = getOpm("testObject", stateVector);
    long stepSizeInSec = 86400;
    long propagationDurationDays = 7;
    ZonedDateTime startDateZonedDateTime = ZonedDateTime.parse(epoch);
    ZonedDateTime endDateZonedDateTime = startDateZonedDateTime.plusDays(propagationDurationDays);
    JulianDate startDate = new JulianDate(startDateZonedDateTime);
    JulianDate stopDate = new JulianDate(endDateZonedDateTime);
    PropagatorConfiguration config = PropagationConfigurationFactory.getAllMajorBodiesConfig();
    Double[] expectedPosition =
        new Double[] {143947724.26470003, -57542002.25428, -28774871.060080003};
    Double[] expectedVelocity =
        new Double[] {21.02344057526, 28.549821594860003, 11.027405485920001};

    PropagatedInterplanetaryOrbit orbit =
        PropagatedInterplanetaryOrbit.fromOpm(opm, stopDate, config);
    OrbitEphemerisMessage oem =
        orbit.exportOrbitEphemerisMessage(startDate, stopDate, Duration.fromSeconds(stepSizeInSec));
    List<OemDataBlock> blocks = oem.getBlocks();
    List<OemDataLine> lines = blocks.get(blocks.size() - 1).getLines();
    OemDataLine finalState = lines.get(lines.size() - 1);
    // final state vector (from STK) is:
    // [1.439477242647e+11 -5.754200225428e+10 -2.877487106008e+10
    // 2.102344057526e+04 2.854982159486e+04 1.102740548592e+04]
    double[] finalPoint = finalState.getPoint();
    double[] actualPosition = new double[] {finalPoint[0], finalPoint[1], finalPoint[2]};
    double[] actualVelocity = new double[] {finalPoint[3], finalPoint[4], finalPoint[5]};

    // TODO: These tolerances aren't really good, try to find how to make them closer.
    assertThat(actualPosition)
        .usingTolerance(1.0e-4)
        .containsExactlyElementsIn(expectedPosition)
        .inOrder();
    assertThat(actualVelocity)
        .usingTolerance(1.0e-11)
        .containsExactlyElementsIn(expectedVelocity)
        .inOrder();
  }
}
