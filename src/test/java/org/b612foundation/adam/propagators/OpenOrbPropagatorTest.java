package org.b612foundation.adam.propagators;

import org.b612foundation.adam.datamodel.PropagationParameters;
import org.b612foundation.adam.datamodel.PropagatorConfiguration;
import org.b612foundation.adam.datamodel.PropagatorConfiguration.PlanetGravityMode;
import org.b612foundation.adam.opm.OdmCommonMetadata;
import org.b612foundation.adam.opm.OrbitEphemerisMessage;
import org.b612foundation.adam.opm.OrbitParameterMessage;
import org.b612foundation.adam.opm.StateVector;
import org.b612foundation.adam.runnable.AdamRunnableException;
import org.b612foundation.stk.StkLicense;
import org.b612foundation.adam.opm.OdmCommonMetadata.CenterName;
import org.b612foundation.adam.opm.OdmCommonMetadata.ReferenceFrame;
import org.b612foundation.adam.opm.OdmCommonMetadata.TimeSystem;
import org.junit.Test;

import agi.foundation.celestial.JplDECentralBody;
import agi.foundation.coordinates.Cartesian;
import agi.foundation.time.Duration;
import agi.foundation.time.JulianDate;
import agi.foundation.time.TimeStandard;

import java.nio.file.Files;

public class OpenOrbPropagatorTest {

    private PropagatorConfiguration allMajorBodiesConfig =
            new PropagatorConfiguration()
                    .setSun(PlanetGravityMode.POINT_MASS)
                    .setMercury(PlanetGravityMode.POINT_MASS)
                    .setVenus(PlanetGravityMode.POINT_MASS)
                    .setEarth(PlanetGravityMode.POINT_MASS)
                    .setMars(PlanetGravityMode.POINT_MASS)
                    .setJupiter(PlanetGravityMode.POINT_MASS)
                    .setSaturn(PlanetGravityMode.POINT_MASS)
                    .setUranus(PlanetGravityMode.POINT_MASS)
                    .setNeptune(PlanetGravityMode.POINT_MASS)
                    .setPluto(PlanetGravityMode.POINT_MASS)
                    .setMoon(PlanetGravityMode.POINT_MASS);

    private PropagatorConfiguration justTheSunConfig =
            new PropagatorConfiguration()
                    .setSun(PlanetGravityMode.POINT_MASS)
                    .setMercury(PlanetGravityMode.OMIT)
                    .setVenus(PlanetGravityMode.OMIT)
                    .setEarth(PlanetGravityMode.OMIT)
                    .setMars(PlanetGravityMode.OMIT)
                    .setJupiter(PlanetGravityMode.OMIT)
                    .setSaturn(PlanetGravityMode.OMIT)
                    .setUranus(PlanetGravityMode.OMIT)
                    .setNeptune(PlanetGravityMode.OMIT)
                    .setPluto(PlanetGravityMode.OMIT)
                    .setMoon(PlanetGravityMode.OMIT);

  /** Input Cartesians are in m and m/s, OPM wants km and km/s. */
  private OrbitParameterMessage getOpm(String name, JulianDate epoch, Cartesian startPositionSunIcrf,
      Cartesian startVelocitySunIcrf) {
    return
        new OrbitParameterMessage()
            .setCcsds_opm_vers("2.0")
            .setMetadata(
                new OdmCommonMetadata()
                    .setObject_name(name)
                    .setObject_id(name)
                    .setCenter_name(CenterName.SUN)
                    .setRef_frame(ReferenceFrame.ICRF)
                    .setTime_system(TimeSystem.UTC))
            .setState_vector(
                new StateVector()
                    .setEpoch(epoch.toDateTime(TimeStandard.getCoordinatedUniversalTime()).toString())
                    .setX(startPositionSunIcrf.getX() / 1000)
                    .setY(startPositionSunIcrf.getY() / 1000)
                    .setZ(startPositionSunIcrf.getZ() / 1000)
                    .setX_dot(startVelocitySunIcrf.getX() / 1000)
                    .setY_dot(startVelocitySunIcrf.getY() / 1000)
                    .setZ_dot(startVelocitySunIcrf.getZ() / 1000));

  }

  @Test
  public void smoke() throws AdamRunnableException {
    // Need STK license to use cartesian. 
    StkLicense.Activate();

    // Assume the object is in circular orbit around the Sun, and this is the radius in meters.
    double radius = 224400000e3;
    double gm = ForceModelHelper.JPL_DE.getGravitationalParameter(JplDECentralBody.SUN);
    // Then, given the value of GM, these would be the orbital velocity and period.
    double speed = Math.sqrt(gm / radius);
    double periodDays = 2 * Math.PI * Math.sqrt(radius * radius * radius / gm) / 86400;

    Cartesian position = new Cartesian(radius, 0, 0);
    Cartesian velocity = new Cartesian(0, speed, 0);

    String epochString = "2019-01-01T00:00:00.000Z";
    JulianDate epoch = TimeHelper.fromIsoFormat(epochString, TimeStandard.getCoordinatedUniversalTime());
    // Check the values after 10 complete orbits.
    JulianDate endTime = epoch.add(Duration.fromDays(periodDays * 10));
    System.out.println("Start date " + epoch.toDateTime(TimeStandard.getCoordinatedUniversalTime()).toString());
    System.out.println("End date " + endTime.toDateTime(TimeStandard.getCoordinatedUniversalTime()).toString());

    PropagationParameters propParams = new PropagationParameters();
    propParams.setStart_time("start");
    propParams.setEnd_time(endTime.toDateTime(TimeStandard.getCoordinatedUniversalTime()).toString());
    propParams.setStep_duration_sec(86400);
    propParams.setPropagator_uuid("propagator_uuid");
    propParams.setExecutor("OORB");
    propParams.setOpm(getOpm("smoke", epoch, position, velocity));

    OpenOrbPropagator propagator = new OpenOrbPropagator();
    String ephem = propagator.propagate(propParams, justTheSunConfig, "around_the_sun");
    System.out.println("\n==== 10 orbits with radius " + radius + " m (" + (radius / OpenOrbPropagator.AU_TO_KM / 1e3)
        + " AU) and speed " + speed + " m/s ("
        + (speed / OpenOrbPropagator.AU_TO_KM / 1e3 * OpenOrbPropagator.SECONDS_IN_DAY) + " AU/s), period " + periodDays
        + " days");
    System.out.println(ephem);
  }

    @Test
    public void testJplHorizonsMatching() throws AdamRunnableException {
      //2458736.500000000, A.D. 2019-Sep-10 00:00:00.0000, -2.097017518186574E+07, -2.484194451032126E+08, -3.009256199094209E+07,  2.026913801769565E+01, -5.571435760196263E+00,  2.602291911915483E+00,  8.376214280239176E+02,  2.511125867807603E+08,  3.507175747628797E+00,
        Cartesian position = new Cartesian(-2.097017518186574E+10, -2.484194451032126E+11, -3.009256199094209E+10);
        Cartesian velocity = new Cartesian(2.026913801769565E+04, -5.571435760196263E+03,  2.602291911915483E+03);

        String epochString = "2019-09-10T00:00:00.000Z";
        JulianDate epoch = TimeHelper.fromIsoFormat(epochString, TimeStandard.getCoordinatedUniversalTime());
        // Check the values after 10 complete orbits.
        JulianDate endTime = epoch.add(Duration.fromDays(365*50));
        System.out.println("Start date " + epoch.toDateTime(TimeStandard.getCoordinatedUniversalTime()).toString());
        System.out.println("End date " + endTime.toDateTime(TimeStandard.getCoordinatedUniversalTime()).toString());

        PropagationParameters propParams = new PropagationParameters();
        propParams.setStart_time("start");
        propParams.setEnd_time(endTime.toDateTime(TimeStandard.getCoordinatedUniversalTime()).toString());
        propParams.setStep_duration_sec(86400 * 30);
        propParams.setPropagator_uuid("propagator_uuid");
        propParams.setExecutor("OORB");
        propParams.setOpm(getOpm("eros", epoch, position, velocity));

        OpenOrbPropagator propagator = new OpenOrbPropagator();
        String ephem = propagator.propagate(propParams, allMajorBodiesConfig, "eros_all_bodies");
        System.out.println(ephem);
    }
}
