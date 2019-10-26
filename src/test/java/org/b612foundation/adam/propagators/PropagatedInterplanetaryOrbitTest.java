package org.b612foundation.adam.propagators;

import java.time.ZonedDateTime;

import org.b612foundation.adam.datamodel.PropagatorConfiguration;
import org.b612foundation.adam.opm.OdmFormatter;
import org.b612foundation.adam.opm.OdmScenarioBuilder;
import org.b612foundation.adam.opm.OrbitParameterMessage;
import org.b612foundation.stk.StkLicense;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import agi.foundation.time.Duration;
import agi.foundation.time.JulianDate;
import agi.foundation.time.TimeStandard;

public class PropagatedInterplanetaryOrbitTest {

  @Before
  public void addLicense() {
    StkLicense.Activate();
  }

  @Test
  public void testEpochParsingDecimals() throws Exception {
    String opm = OdmScenarioBuilder.getSimpleOpm();
    // Drop decimals in the epoch.
    opm = opm.replaceAll("EPOCH(.*)\\.\\d*", "EPOCH$1Z");
    OrbitParameterMessage parsed = OdmFormatter.parseOpmString(opm);

    Duration step = Duration.fromSeconds(86400);
    JulianDate startDate = new JulianDate(ZonedDateTime.parse("2000-01-01T11:59:27Z"),
        TimeStandard.getInternationalAtomicTime());
    JulianDate endDate = new JulianDate(ZonedDateTime.parse("2000-01-07T11:59:27.816Z"),
        TimeStandard.getInternationalAtomicTime());
    // Default configuration is all planets.
    PropagatorConfiguration config = new PropagatorConfiguration();
    PropagatedInterplanetaryOrbit orbit = PropagatedInterplanetaryOrbit.fromOpm(parsed, endDate, config);
    Assert.assertNotNull(orbit.getEphemeris(startDate, endDate, step));
  }

  @Test
  public void testParseMilliseconds() {
    // For extra paranoia after the datetime library replacement, make sure all
    // these parse without exceptions.
    JulianDate d1 = PropagatedInterplanetaryOrbit.parseUtc("2000-01-01T11:59:27Z");
    JulianDate d2 = PropagatedInterplanetaryOrbit.parseUtc("2000-01-01T11:59:27.816Z");
    JulianDate d3 = PropagatedInterplanetaryOrbit.parseUtc("2000-01-01T11:59:27");
    JulianDate d4 = PropagatedInterplanetaryOrbit.parseUtc("2000-01-01T11:59:27.816");
    Assert.assertEquals("No milliseconds, with or without Z", d1, d3);
    Assert.assertEquals("With milliseconds, with or without Z", d2, d4);
    Assert.assertEquals("Addition of milliseconds", d1.secondsDifference(d2), 0.816, 1e-5);
  }

}
