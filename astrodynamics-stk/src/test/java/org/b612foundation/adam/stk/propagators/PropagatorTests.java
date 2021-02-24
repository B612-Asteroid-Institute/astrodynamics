package org.b612foundation.adam.stk.propagators;

import agi.foundation.coordinates.Cartesian;
import org.b612foundation.adam.datamodel.PropagationConfigurationFactory;
import org.b612foundation.adam.datamodel.PropagationParameters;
import org.b612foundation.adam.datamodel.PropagatorConfiguration;
import org.b612foundation.adam.exceptions.AdamPropagationException;
import org.b612foundation.adam.opm.OrbitEphemerisMessage;
import org.b612foundation.adam.propagators.OrbitPropagator;
import org.junit.Test;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static junit.framework.TestCase.assertNotNull;
import static org.b612foundation.adam.stk.PropagatorTestHelper.getOpm;

public abstract class PropagatorTests {

  @Test
  public void testPropagate() throws AdamPropagationException, IOException {
    // Ceres at 01/02/2000 23:59:37.8159999
    // 1.29642000000000e+005 -3.56739757814307e+011 7.94196610361205e+010 1.10007062491749e+011
    // -6.09566061804876e+003 -1.70715035989933e+004 -6.79473715143830e+003
    Cartesian position =
        new Cartesian(-3.56739757814307e+011, 7.94196610361205e+010, 1.10007062491749e+011);
    Cartesian velocity =
        new Cartesian(-6.09566061804876e+003, -1.70715035989933e+004, -6.79473715143830e+003);

    long stepSizeInSec = 86400 * 10;
    long propagationDurationDays = 50 * 365;
    String epochString = "2000-01-02T23:59:37.816Z";
    ZonedDateTime startDate = ZonedDateTime.parse(epochString);
    ZonedDateTime stopDate = startDate.plusDays(propagationDurationDays);

    PropagationParameters propParams = new PropagationParameters();
    propParams.setStart_time(startDate.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
    propParams.setEnd_time(stopDate.format(DateTimeFormatter.ISO_ZONED_DATE_TIME));
    propParams.setStep_duration_sec(stepSizeInSec);
    propParams.setPropagator_uuid(UUID.randomUUID().toString());
    propParams.setExecutor("test");
    propParams.setOpm(getOpm("ceres", epochString, position, velocity));

    PropagatorConfiguration config = PropagationConfigurationFactory.getAllMajorBodiesConfig();

    OrbitEphemerisMessage oem = this.getPropagator().propagate(propParams, config, "testPropagate");
    assertNotNull(oem);
    // System.out.println(OemWriter.toCcsdsOemString(oem));
  }

  abstract OrbitPropagator getPropagator() throws IOException;
}
