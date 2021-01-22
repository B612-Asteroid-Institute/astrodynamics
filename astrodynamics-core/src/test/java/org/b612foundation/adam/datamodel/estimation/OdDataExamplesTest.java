package org.b612foundation.adam.datamodel.estimation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.var;
import org.b612foundation.adam.datamodel.PropagatorConfiguration;
import org.b612foundation.adam.opm.OrbitParameterMessage;
import org.junit.Test;

import java.util.Base64;

public class OdDataExamplesTest {

  @Test
  public void configuringInputDataExample() throws JsonProcessingException {
    // Example of turning a raw DES OBS file into a base64 encoded one that could be passed safely
    // to a REST service
    // @formatter:off
    var desRawObsFileText =
        "433     54617.0000000000 O  78.2887200000  26.5336200000  13.5600000000 X  568   0.0010000000   0.0000000000  -1.0000000000 -0.1000000E+01 X\n"
            + "433     54624.0000000000 O  85.2759500000  26.3621300000  13.5200000000 X  568   0.0010000000   0.0000000000  -1.0000000000 -0.1000000E+01 X\n"
            + "433     54631.0000000000 O  92.2911900000  25.8549400000  13.4800000000 X  568   0.0010000000   0.0010000000  -1.0000000000 -0.1000000E+01 X\n"
            + "433     54638.0000000000 O  99.2839000000  25.0127500000  13.4500000000 X  568   0.0010000000   0.0010000000  -1.0000000000 -0.1000000E+01 X\n"
            + "433     54645.0000000000 O 106.2096100000  23.8451900000  13.4300000000 X  568   0.0010000000   0.0010000000  -1.0000000000 -0.1000000E+01 X\n"
            + "433     54652.0000000000 O 113.0300000000  22.3700600000  13.4100000000 X  568   0.0010000000   0.0010000000  -1.0000000000 -0.1000000E+01 X\n"
            + "433     54659.0000000000 O 119.7126800000  20.6130900000  13.4000000000 X  568   0.0010000000   0.0010000000  -1.0000000000 -0.1000000E+01 X\n"
            + "433     54666.0000000000 O 126.2366900000  18.6056500000  13.4000000000 X  568   0.0010000000   0.0010000000  -1.0000000000 -0.1000000E+01 X\n"
            + "433     54673.0000000000 O 132.5936500000  16.3820000000  13.4100000000 X  568   0.0010000000   0.0010000000  -1.0000000000 -0.1000000E+01 X";
    // @formatter:on
    var base64ObsFile = Base64.getEncoder().encodeToString(desRawObsFileText.getBytes());
    // String desObsFileFromBase64 = new String(Base64.getDecoder().decode(base64ObsFile));

    // Example of configuring a propagator that would be referenced by UUID in the force models of
    // the OD config
    var propagatorConfig =
        new PropagatorConfiguration()
            .setEarth(PropagatorConfiguration.PlanetGravityMode.POINT_MASS)
            .setSun(PropagatorConfiguration.PlanetGravityMode.POINT_MASS)
            .setMoon(PropagatorConfiguration.PlanetGravityMode.POINT_MASS);

    // Example of two separate IOD configurations for two different IOD systems
    // which should be swappable transparently hopefully/hypothetically.  One is for the Thor IOD
    // capabilities
    // the other is for OpenOrb Ranging IOD capabilities (these aren't actual settings to be used on
    // above data
    // but representative ones to show the variety of system-specific elements that can be
    // configured
    var thorIodConfig =
        OrbitDeterminationConfiguration.builder()
            .executor("Thor")
            .propagatorConfigUuid(propagatorConfig.getUuid())
            .measurementSetting("samplingSetting", "first+middle+last")
            .executionSetting("method", "gibbs")
            .build();

    var oorbIodConfig =
        OrbitDeterminationConfiguration.builder()
            .executor("OpenOrb")
            .measurementSetting("dchi2_rejection", "T")
            .measurementSetting("dchi2.max", "30")
            .measurementSetting("os.sampling_type", "1")
            .executionSetting("sor.two_point_method", "continued fraction")
            .executionSetting("sor.type", "2")
            .executionSetting("sor.norb", "2000")
            .executionSetting("sor.ntrial", "100000")
            .convergenceSetting("sor.genwin.offset", "0.0 0.0 0.0 0.0")
            .convergenceSetting("sor.iterate_bounds", "T T T T")
            .convergenceSetting("apriori.a.min", "0.5")
            .convergenceSetting("apriori.a.max", "10")
            .propagatorConfigUuid(propagatorConfig.getUuid())
            .build();

    var iodParams =
        OrbitDeterminationParameters.builder()
            .orbitDeterminationConfigUuid(thorIodConfig.getUuid())
            .loggingLevel(5)
            .observerId("568")
            .observerId("807")
            .spaceObjectId("433")
            .outputFrame("ICRF")
            .measurementsFormatType("DES")
            .measurements(base64ObsFile)
            .type(OrbitDeterminationParameters.OdType.INITIAL)
            .build();

    // A call to the REST service ran the IOD and generated IOD values, let's say so now have OPM...
    // pretend this came from the IOD
    var initialOrbitGuess = new OrbitParameterMessage();

    // Example of two separate IOD configurations for two different IOD systems
    // which should be swappable transparently hopefully/hypothetically.  One is for the OpenOrb OD
    // capabilities
    // the other is for Orekit OD capabilities (these aren't actual settings to be used on above
    // data
    // but representative ones to show the variety of system-specific elements that can be
    // configured
    var oorbFullOdConfig =
        OrbitDeterminationConfiguration.builder()
            .executor("OpenOrb")
            .propagatorConfigUuid(propagatorConfig.getUuid())
            .measurementSetting("stdev.ra", "1.0")
            .measurementSetting("stdev.dec", "1.0")
            .measurementSetting("outlier_rejection", "T")
            .measurementSetting("outlier.multiplier", "4.0")
            .measurementSetting("obs.mask", "F")
            .executionSetting("ls.correction_factor", "0.2")
            .executionSetting("ls.rchi2.acceptable", "1.2")
            .executionSetting("pp.H_estimation", "T")
            .convergenceSetting("ls.niter_major.max", "20")
            .convergenceSetting("ls.niter_major.min", "2")
            .build();

    var orekitFullOdConfig =
        OrbitDeterminationConfiguration.builder()
            .executor("Orekit")
            .propagatorConfigUuid(propagatorConfig.getUuid())
            .measurementSetting("ra.dec.outlier.rejection.multiplier", "6")
            .measurementSetting("ra.dec.outlier.rejection.starting.iteration", "2")
            .measurementSetting("ra.measurements.base.weight", "1.0")
            .measurementSetting("dec.measurements.base.weight", "1.0")
            .executionSetting("estimator.orbital.parameters.position.scale", "100")
            .executionSetting("estimator.optimization.engine", "Levenberg-Marquardt")
            .executionSetting("estimator.Levenberg.Marquardt.initial.step.bound.factor", "1.0e-6")
            .convergenceSetting("estimator.normalized.parameters.convergence.threshold", "1.0e-3")
            .convergenceSetting("estimator.max.iterations", "20")
            .convergenceSetting("estimator.max.evaluations", "25")
            .build();

    var fullOdParams =
        OrbitDeterminationParameters.builder()
            .orbitDeterminationConfigUuid(oorbFullOdConfig.getUuid())
            .loggingLevel(5)
            .observerId("568")
            .observerId("807")
            .spaceObjectId("433")
            .outputFrame("ICRF")
            .measurementsFormatType("DES")
            .measurements(base64ObsFile)
            .initialStateEstimate(initialOrbitGuess)
            .initialStateEstimateCr(1.3)
            .initialStateEstimateMass(9.3e20)
            .initialStateEstimateSrpArea(2.95e6)
            .type(OrbitDeterminationParameters.OdType.FULL)
            .build();

    ObjectMapper om = new ObjectMapper();
    om.configure(SerializationFeature.INDENT_OUTPUT, true);

    // TODO: make assertions to ensure correct behavior of whatever is being tested
    System.out.println("IOD Configuration");
    System.out.println(om.writeValueAsString(thorIodConfig));
    System.out.println("IOD Run Parameters");
    System.out.println(om.writeValueAsString(iodParams));
    System.out.println(
        "-----------------------------------------------------------------------------");
    System.out.println("Full OD Configuration");
    System.out.println(om.writeValueAsString(oorbFullOdConfig));
    System.out.println("Full OD Run Parameters");
    System.out.println(om.writeValueAsString(fullOdParams));
  }
}
