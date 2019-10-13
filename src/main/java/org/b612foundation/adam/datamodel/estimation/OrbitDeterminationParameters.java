package org.b612foundation.adam.datamodel.estimation;

import lombok.*;
import org.b612foundation.adam.datamodel.AdamObject;
import org.b612foundation.adam.opm.OrbitParameterMessage;

import java.util.List;
import java.util.Map;

/**
 * The parameters to use for a specific run of the OD software which includes things like the space object ID,
 * measurements to process, etc.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrbitDeterminationParameters extends AdamObject {

    /** Logging level for output of forensic data during OD run **/
    private int loggingLevel;

    /** Settings which may be specific to an OD calculation at initialization time, like initial guesses which
     * are state specific **/
    @Singular
    private Map<String, String> initializationSettings;

    /** An initial state which can be used for initializing the OD system **/
    private OrbitParameterMessage initialStateEstimate;

    /** The SRP coefficient for the space object **/
    private double initialStateEstimateCr;

    /** The SRP coefficient for the space object **/
    private double initialStateEstimateSrpArea;

    /** The initial state estimate of the mass of the space object **/
    private double initialStateEstimateMass;

    /** Format of the passed in measurements string **/
    private String measurementsFormatType;

    /** String representation of the measurements to be processed in this OD.  Format needs to correspond
     * to accepted formats and match the passed in measurement format. This is often Base64 encoded. Examples include:
     *  - Base64 encoded ZTF-Avro format file
     *  - Base64 DES File
     **/
    private String measurements;

    /** Settings for the numeric propagator - the ID. */
    private String orbit_determination_config_uuid;


    /** The ID in the observations file that we are doing the OD against **/
    private String spaceObjectId;

    /** The ID(s) of the observers that are in the file **/
    @Singular
    private List<String> observerIds;

    /** The Coordinate frame that the resulting orbit should be calculated in **/
    private String outputFrame;

    private OdType type;

    public enum OdType {
        Initial,
        Full
    }

    public enum MeasurumentType {
        LsstCsvFilePath
    }
}
