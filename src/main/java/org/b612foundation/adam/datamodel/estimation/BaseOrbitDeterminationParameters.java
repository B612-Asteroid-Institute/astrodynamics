package org.b612foundation.adam.datamodel.estimation;

import lombok.*;
import org.b612foundation.adam.datamodel.AdamObject;

import java.util.List;

/**
 * The parameters to use for a specific run of the OD software which includes things like the space object ID,
 * measurements to process, etc.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BaseOrbitDeterminationParameters extends AdamObject {
    /** Specific executor to be used, e.g. OpenOrb, Orekit, etc. The behavior is up to the server implementation. */
    private String executor;

    /** Logging level for output of forensic data during OD run **/
    private int loggingLevel;

    /** Format of the passed in measurements string **/
    private String measurementsFormatType;

    /** String representation of the measurements to be processed in this OD.  Format needs to correspond
     * to accepted formats and match the passed in measurement format. This is often Base64 encoded. Examples include:
     *  - Base64 encoded ZTF-Avro format file
     *  - Base64 DES File
     **/
    private String measurements;

    /** Settings for the numeric propagator - the ID. */
    private String orbit_determination_uuid;


    /** The ID in the observations file that we are doing the OD against **/
    private String spaceObjectId;

    /** The ID(s) of the observers that are in the file **/
    @Singular
    private List<String> observerIds;

    /** The Coordinate frame that the resulting orbit should be calculated in **/
    private String outputFrame;



}
