package org.b612foundation.adam.propagators;

import org.b612foundation.adam.runnable.AdamRunnableException;
import org.junit.Assert;
import org.junit.Test;

import agi.foundation.time.JulianDate;
import agi.foundation.time.TimeStandard;

public class TimeHelperTest {

  @Test
  public void testTimeHelper() throws AdamRunnableException {
    String date = "2007-07-22T09:45:20.382Z";

    // Check that round-tripping works for a couple different time standards.
    Assert.assertEquals(date,
        TimeHelper.toIsoFormat(TimeHelper.fromIsoFormat(date, TimeStandard.getCoordinatedUniversalTime())));
    Assert.assertEquals(date,
        TimeHelper.toIsoFormat(TimeHelper.fromIsoFormat(date, TimeStandard.getInternationalAtomicTime())));
    Assert.assertEquals(date,
        TimeHelper.toIsoFormat(TimeHelper.fromIsoFormat(date, TimeStandard.getTerrestrialTime())));
    
    // Check that parsing without a 'Z' works.
    String noZ = "2007-07-22T09:45:20.382";
    Assert.assertEquals(noZ + 'Z',
        TimeHelper.toIsoFormat(TimeHelper.fromIsoFormat(noZ, TimeStandard.getCoordinatedUniversalTime())));
    
    // Check that dates maintain their time standards.
    JulianDate jd = TimeHelper.fromIsoFormat(date, TimeStandard.getCoordinatedUniversalTime());
    JulianDate jd2 = jd.addDays(1);
    Assert.assertEquals(jd.getStandard(), jd2.getStandard());
    Assert.assertEquals(date, TimeHelper.toIsoFormat(jd2.subtractDays(1)));
    
    // Check that a timestamp with more than microseconds precision gets cut off at microseconds when being stringified.
    JulianDate tooPrecise = jd.addSeconds(.0000001);
    Assert.assertEquals(date, TimeHelper.toIsoFormat(tooPrecise));
  }

}
