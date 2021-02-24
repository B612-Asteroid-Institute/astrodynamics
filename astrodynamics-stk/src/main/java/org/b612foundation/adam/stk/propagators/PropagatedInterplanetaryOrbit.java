package org.b612foundation.adam.stk.propagators;

import static org.b612foundation.adam.astro.AstroConstants.M_TO_KM;
import static org.b612foundation.adam.stk.StkPropagationHelper.parseUtcAsJulian;

import agi.foundation.DateMotionCollection1;
import agi.foundation.EvaluatorGroup;
import agi.foundation.Motion1;
import agi.foundation.MotionEvaluator1;
import agi.foundation.compatibility.EventHandler;
import agi.foundation.coordinates.Cartesian;
import agi.foundation.geometry.PointInterpolator;
import agi.foundation.geometry.ReferenceFrame;
import agi.foundation.geometry.Scalar;
import agi.foundation.numericalmethods.IntegrationSense;
import agi.foundation.numericalmethods.InterpolationAlgorithmType;
import agi.foundation.propagators.NumericalPropagator;
import agi.foundation.propagators.NumericalPropagatorDefinition;
import agi.foundation.propagators.PropagationEventArgs;
import agi.foundation.propagators.PropagationNewtonianPoint;
import agi.foundation.propagators.StoppableNumericalPropagatorResults;
import agi.foundation.stk.StkEphemerisFile;
import agi.foundation.stoppingconditions.DurationStoppingCondition;
import agi.foundation.stoppingconditions.StoppingConditionEvaluator;
import agi.foundation.time.Duration;
import agi.foundation.time.JulianDate;
import agi.foundation.time.TimeStandard;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.b612foundation.adam.datamodel.PropagatorConfiguration;
import org.b612foundation.adam.opm.OdmCommonHeader;
import org.b612foundation.adam.opm.OdmCommonMetadata;
import org.b612foundation.adam.opm.OemDataBlock;
import org.b612foundation.adam.opm.OemMetadata;
import org.b612foundation.adam.opm.OrbitEphemerisMessage;
import org.b612foundation.adam.opm.OrbitParameterMessage;
import org.b612foundation.adam.stk.StkOpmHelper;
import org.b612foundation.adam.stk.propagators.ForceModelHelper;

/**
 * Numerically propagated orbit of an object in the Solar System.
 *
 * <p>Note: TimeStandard.getInternationalAtomicTime() matches TAIG from STK Desktop. When creating
 * JulianDate, pass that time standard as the second argument. When creating Joda DateTime from a
 * String, make sure the string has "Z" in the end, otherwise the time zones get screwy.
 *
 * <p>: Will probably want to add some sort of progress monitors to longer running functions.
 *
 * <p>TODO: Move to astrodynamics.
 */
public final class PropagatedInterplanetaryOrbit extends PropagatedOrbit {
  private static Logger log = Logger.getLogger(PropagatedInterplanetaryOrbit.class.getName());

  /**
   * Object name as known to various components. We use it, essentially, as a hash key to talk to
   * STK. Since we are only propagating one object [so far], we can use a constant here instead of
   * deriving it from the OPM.
   */
  private final String objectId = "myObject";

  /** Reference frame we are using. */
  private ReferenceFrame referenceFrame;

  /** The point-object whose orbit is being propagated. */
  private PropagationNewtonianPoint object;

  /**
   * Propagator is what does the math. Propagator is set up in the constructor with the knowledge of
   * forces and initial state. It can propagate for a certain duration of time or until a condition,
   * and can continue from the previous state. Propagator can also be restarted with new initial
   * state and initial conditions.
   */
  private NumericalPropagator propagator;

  /**
   * Evaluators within the same group can reuse computation according to the AGI documentation.
   * Further, we cannot even construct an evaluator without passing a group, so here it is.
   */
  private final EvaluatorGroup evaluatorGroup = new EvaluatorGroup();

  /**
   * Interpolator digests data into form visible to the user. Propagator produces ephemeris, which
   * is then interpolated to get final results.
   */
  private PointInterpolator interpolator;

  /**
   * STK Ephemerides insist on interpolating, so we pull uninterpolated values manually. These are
   * current times and values (6 doubles for position and velocity) for each integration step. The
   * step size is adjusted by the integrator based on tolerances.
   */
  private List<JulianDate> rawDates = new ArrayList<>();

  private List<double[]> rawValues = new ArrayList<>();

  private PropagatedInterplanetaryOrbit() {}

  /**
   * Builds an orbit from CCSDS OPM message and propagates it up to the specified date.
   *
   * @throws IllegalArgumentException if some parts of the message are not supported.
   */
  public static PropagatedInterplanetaryOrbit fromOpm(
      OrbitParameterMessage opm, JulianDate endDate, PropagatorConfiguration config) {
    ForceModelHelper.loadStandardObjects();

    JulianDate epoch = parseUtcAsJulian(opm.getState_vector().getEpoch());
    PropagatedInterplanetaryOrbit orbit = new PropagatedInterplanetaryOrbit();
    orbit.setReferenceFrame(opm.getMetadata());
    orbit.initializeObject(opm, orbit.referenceFrame);
    if (opm.getSpacecraft() != null) {
      orbit.object.setMass(Scalar.toScalar(opm.getSpacecraft().getMass()));
    } else {
      orbit.object.setMass(Scalar.toScalar(0));
    }
    switch (opm.getMetadata().getCenter_name()) {
      case EARTH:
        ForceModelHelper.initializeEarthCenteredForces(config, orbit.object);
        break;
      case SUN:
        ForceModelHelper.initializeSunCenteredForces(config, orbit.object);
        break;
      default:
        throw new IllegalArgumentException(
            "Can't initialize forces for central body " + opm.getMetadata().getCenter_name());
    }
    orbit.initializeAndRunPropagator(epoch, endDate);
    return orbit;
  }

  /** Initializes the object we will track using given reference frame. */
  private void initializeObject(OrbitParameterMessage opm, ReferenceFrame referenceFrame) {
    Motion1<Cartesian> cartesianElements = StkOpmHelper.getCartesianStateVector(opm);
    object =
        new PropagationNewtonianPoint(
            objectId,
            referenceFrame,
            cartesianElements.getValue(),
            cartesianElements.getFirstDerivative());
  }

  /**
   * Configures the numeric propagator part assuming the object and forces acting on it have been
   * initialized. Propagates until the given end date.
   */
  private void initializeAndRunPropagator(JulianDate epoch, JulianDate endDate) {
    NumericalPropagatorDefinition state = new NumericalPropagatorDefinition();
    state.setEpoch(epoch);
    state.getIntegrationElements().add(object);
    state.setIntegrator(ForceModelHelper.getRungeKuttaFehlberg78Integrator());
    propagator = state.createPropagator();
    propagator.addStepTaken(
        new EventHandler<PropagationEventArgs>() {
          @Override
          public void invoke(Object sender, PropagationEventArgs args) {
            // Extract non-interpolated ephemerides.
            rawDates.add(args.getPreviousTime());
            // Integrator state interleaves positions and velocities, so we reorder to get all
            // positions first. We need to make a copy of data anyway, because the integrator
            // modifies it in place.
            double[] cur = args.getPreviousState();
            double[] reordered = {cur[0], cur[2], cur[4], cur[1], cur[3], cur[5]};
            rawValues.add(reordered);
          }
        });
    // 1 indicates to output after every integration step, 2 every other step, etc
    int outputSparsity = 1; // TODO: Move this to solver config?
    Duration propagationTime = endDate.subtract(epoch);

    List<StoppingConditionEvaluator> conditions = new ArrayList<>();
    DurationStoppingCondition byDuration = new DurationStoppingCondition(propagationTime);
    conditions.add(byDuration.getEvaluator(evaluatorGroup));
    boolean increasing = JulianDate.lessThanOrEqual(epoch, endDate);
    StoppableNumericalPropagatorResults results =
        propagator.propagateUntilStop(
            conditions,
            increasing ? IntegrationSense.INCREASING : IntegrationSense.DECREASING,
            true /* initialize conditions */,
            outputSparsity,
            null /* tracker */);
    log.info(
        "Propagating in direction "
            + propagator.getPropagationDirection().toString()
            + " from "
            + epoch.toString()
            + " to "
            + endDate.toString());
    // Recover the position and velocity. OPM does not include acceleration data, so we won't get it
    // here.
    DateMotionCollection1<Cartesian> ephemeris =
        results.getPropagationHistory().getDateMotionCollection(objectId);
    interpolator =
        new PointInterpolator(
            object.getIntegrationFrame(), InterpolationAlgorithmType.HERMITE, 6, ephemeris);
  }

  @Override
  protected MotionEvaluator1<Cartesian> getEvaluator() {
    return interpolator.getEvaluator(evaluatorGroup);
  }

  @Override
  public ReferenceFrame getReferenceFrame() {
    return interpolator.getReferenceFrame();
  }

  /**
   * Sets the STK CentralBody and ReferenceFrame according to metadata, throws if not supported.
   * Currently ignores ReferenceFrame epoch.
   */
  private void setReferenceFrame(OdmCommonMetadata metadata) {
    ReferenceFrameGenerator generator = new ReferenceFrameGenerator();
    referenceFrame =
        generator.getReferenceFrame(metadata.getRef_frame(), metadata.getCenter_name());
  }

  /**
   * Write non-interpolated data in STK's .e format. Components' StkEphemerisFile insists on
   * interpolating, so to avoid interpolation we have to write data directly.
   */
  public void writeRawEphemeris(Writer writer) throws IOException {
    int last = validateDatesAndReturnValidSize(rawDates);
    JulianDate epoch = rawDates.get(0);

    // Header.
    writer.write("stk.v.9.0\n# WrittenBy\tPropagatedInterplanetaryOrbit\n\nBEGIN Ephemeris\n\n");
    writer.write(String.format("NumberOfEphemerisPoints\t%d\nScenarioEpoch\t", last + 1));
    writer.write(epoch.toDateTime(TimeStandard.getCoordinatedUniversalTime()).toString());
    writer.write("\nInterpolationMethod\tLagrange\nInterpolationSamplesM1\t7");
    writer.write("\nCentralBody\tSun\nCoordinateSystem\tICRF\n\nEphemerisTimePosVel\n\n");
    for (int i = 0; i <= last; i++) {
      double time = epoch.secondsDifference(rawDates.get(i));
      writer.write(String.format("%f", time));
      for (double v : rawValues.get(i)) {
        writer.write(String.format(" %f", v));
      }
      writer.write("\n");
    }
    // Footer.
    writer.write("\nEND Ephemeris\n");
  }

  public OrbitEphemerisMessage exportOrbitEphemerisMessage(
      JulianDate startDate, JulianDate stopDate, Duration timeStep) {
    StkEphemerisFile ephemeris = getEphemeris(startDate, stopDate, timeStep);
    StkEphemerisFile.EphemerisTimePosVel posVelData =
        (StkEphemerisFile.EphemerisTimePosVel) ephemeris.getData();
    int numberEphemPoints = posVelData.getEphemerisData().getCount();
    List<JulianDate> dates = new ArrayList<>();
    List<double[]> posVel = new ArrayList<>();
    for (int i = 0; i < numberEphemPoints; i++) {
      JulianDate date = posVelData.getEphemerisData().getDates().get(i);
      Cartesian position = posVelData.getEphemerisData().getValues().get(i);
      Cartesian velocity = posVelData.getEphemerisData().getFirstDerivatives().get(i);
      dates.add(date);
      posVel.add(
          new double[] {
            position.getX(),
            position.getY(),
            position.getZ(),
            velocity.getX(),
            velocity.getY(),
            velocity.getZ()
          });
    }

    return exportOrbitEphemerisMessage(dates, posVel);
  }

  public OrbitEphemerisMessage exportOrbitEphemerisMessage() {
    return exportOrbitEphemerisMessage(rawDates, rawValues);
  }

  private OrbitEphemerisMessage exportOrbitEphemerisMessage(
      List<JulianDate> dates, List<double[]> posVelValues) {
    int last = validateDatesAndReturnValidSize(dates);
    OdmCommonHeader header = new OdmCommonHeader();
    header.setCreation_date(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    header.setOriginator("ADAM AGI-components based propagator");

    String startDateString =
        dates.get(0).toDateTime(TimeStandard.getCoordinatedUniversalTime()).toString();
    String stopDateString =
        dates.get(last).toDateTime(TimeStandard.getCoordinatedUniversalTime()).toString();
    OemMetadata metadata = new OemMetadata();
    metadata.setInterpolation("HERMITE");
    metadata.setInterpolation_degree(5);
    metadata.setStart_time(startDateString);
    metadata.setUsable_start_time(metadata.getStart_time());
    metadata.setStop_time(stopDateString);
    metadata.setUsable_stop_time(metadata.getStop_time());
    metadata.setObject_id(objectId);
    metadata.setObject_name(objectId);
    metadata.setTime_system(OdmCommonMetadata.TimeSystem.UTC);
    metadata.setCenter_name(OdmCommonMetadata.CenterName.SUN);
    metadata.setRef_frame(OdmCommonMetadata.ReferenceFrame.ICRF);

    OemDataBlock block = new OemDataBlock();
    block.getComments().add("ADAM AGI-components based propagation");
    block.setMetadata(metadata);

    for (int i = 0; i <= last; i++) {
      String lineDateString =
          dates.get(i).toDateTime(TimeStandard.getCoordinatedUniversalTime()).toString();
      double[] posVel = posVelValues.get(i);
      block.addLine(
          lineDateString,
          posVel[0] * M_TO_KM,
          posVel[1] * M_TO_KM,
          posVel[2] * M_TO_KM,
          posVel[3] * M_TO_KM,
          posVel[4] * M_TO_KM,
          posVel[5] * M_TO_KM);
    }

    OrbitEphemerisMessage oem = new OrbitEphemerisMessage();
    oem.setHeader(header);
    oem.addBlock(block);

    return oem;
  }

  /** Returns the list of dates corresponding to integrator steps, not interpolated. */
  public List<JulianDate> getRawDates() {
    return rawDates;
  }

  /**
   * Returns the list of raw values from integrator steps, not interpolated. Each 6-value array
   * contains position followed by velocity.
   */
  public List<double[]> getRawValues() {
    return rawValues;
  }

  private int validateDatesAndReturnValidSize(List<JulianDate> dates) {
    Preconditions.checkState(!dates.isEmpty());
    // Data. We are pulling previous values in the callback, not current, so the last element may
    // get duplicated.
    int last = dates.size() - 1;
    if (last > 1 && dates.get(last).equals(dates.get(last - 1))) {
      last--;
    }

    return last;
  }
}
