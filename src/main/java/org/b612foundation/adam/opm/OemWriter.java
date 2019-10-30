package org.b612foundation.adam.opm;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.b612foundation.adam.astro.AstroConstants.KM_TO_M;

/**
 * Given an OEM file write output to an STK Ephemeris File string format
 */
public class OemWriter {
    private static final DateTimeFormatter STK_GREG_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss.SSSSSS");

    public static String toStkEphemerisString(OrbitEphemerisMessage oem) {
        StringBuilder sb = new StringBuilder();
        sb.append("stk.v.11.0\n");
        sb.append("BEGIN Ephemeris\n");
        validateBlocks(oem.getBlocks());
        OemMetadata firstBlockMetadata = oem.getBlocks().get(0).getMetadata();
        String startString = firstBlockMetadata.getStart_time();
        LocalDateTime startEpoch = LocalDateTime.parse(startString);
        sb.append("ScenarioEpoch " + startEpoch.format(STK_GREG_FORMATTER) + "\n");
        sb.append("CentralBody " + firstBlockMetadata.getCenter_name() + "\n");
        sb.append("CoordinateSystem " + oemToStkCoordinateSystem(firstBlockMetadata.getRef_frame()) + "\n");
        sb.append("InterpolationMethod " + firstBlockMetadata.getInterpolation() + "\n");
        sb.append("InterpolationOrder " + firstBlockMetadata.getInterpolation_degree() + "\n");

        int ephemPointCount = 0;
        int covariancePointCount = 0;
        for(OemDataBlock block : oem.getBlocks()) {
            ephemPointCount += block.getLines().size();
            covariancePointCount += block.getCovariances().size();
        }

        sb.append("NumberOfEphemerisPoints " + ephemPointCount + "\n");

        if (covariancePointCount > 0) {
            sb.append("NumberOfCovariancePoints " + covariancePointCount + "\n");
            sb.append("CovarianceFormat LowerTriangular\n");
        }

        if (oem.getBlocks().size() > 1) {
            sb.append("BEGIN SegmentBoundaryTimes\n");
            for(OemDataBlock block : oem.getBlocks()) {
                sb.append(dateStringToEpochSec(block.getMetadata().getStart_time(),startEpoch) + "\n");
            }
            sb.append("END SegmentBoundaryTimes\n");
        }

        sb.append("\n");
        sb.append("EphemerisTimePosVel\n");
        for(OemDataBlock block : oem.getBlocks()) {
            for(OemDataLine line : block.getLines()) {
                double dateEpochSec = dateStringToEpochSec(line.getDate(), startEpoch);
                double[] pv = line.getPoint();
                String stkLine = String.format("%14.12e %14.12e %14.12e %14.12e %14.12e %14.12e %14.12e\n",
                        dateEpochSec, pv[0] * KM_TO_M, pv[1]  * KM_TO_M, pv[2]  * KM_TO_M, pv[3] * KM_TO_M, pv[4] * KM_TO_M, pv[5] * KM_TO_M);
                sb.append(stkLine);
            }
        }
        sb.append("\n");
        if(covariancePointCount > 0) {
            sb.append("CovarianceTimePosVel\n");
            for(OemDataBlock block : oem.getBlocks()) {
                for(CovarianceMatrix cov : block.getCovariances()) {
                    double dateEpochSec = dateStringToEpochSec(cov.getEpoch(), startEpoch);
                    sb.append(String.format("%14.12e ", dateEpochSec));
                    sb.append(String.format("%14.12e ",
                            cov.getCx_x()));
                    sb.append(String.format("%14.12e %14.12e ",
                            cov.getCy_x(),cov.getCy_y()));
                    sb.append(String.format("%14.12e %14.12e %14.12e ",
                            cov.getCz_x(), cov.getCz_y(), cov.getCz_z()));
                    sb.append(String.format("%14.12e %14.12e %14.12e %14.12e ",
                            cov.getCx_dot_x(), cov.getCx_dot_y(), cov.getCx_dot_z(), cov.getCx_dot_x_dot()));
                    sb.append(String.format("%14.12e %14.12e %14.12e %14.12e %14.12e ",
                            cov.getCy_dot_x(), cov.getCy_dot_y(), cov.getCy_dot_z(),  cov.getCy_dot_x_dot(), cov.getCy_dot_y_dot()));
                    sb.append(String.format("%14.12e %14.12e %14.12e %14.12e %14.12e %14.12e\n",
                            cov.getCz_dot_x(), cov.getCz_dot_y(), cov.getCz_dot_z(), cov.getCz_dot_x_dot(), cov.getCz_dot_y_dot(), cov.getCz_dot_z_dot()));
                }
            }
        }
        sb.append("\n");
        sb.append("END Ephemeris");

        return sb.toString();
    }

    private static String oemToStkCoordinateSystem(OdmCommonMetadata.ReferenceFrame ref_frame) {
        switch(ref_frame) {
            case EME2000:
                return "J2000";
            case ICRF:
                return "ICRF";
            default:
                throw new IllegalArgumentException("Unknown conversion for OEM Coordinate Systm: " + ref_frame);
        }
    }

    public OemWriter() {
    }

    private static void validateBlocks(List<OemDataBlock> blocks) {
        OdmCommonMetadata.CenterName centerName = OdmCommonMetadata.CenterName.SUN;
        OdmCommonMetadata.TimeSystem timeSystem = OdmCommonMetadata.TimeSystem.TT;
        OdmCommonMetadata.ReferenceFrame refName = OdmCommonMetadata.ReferenceFrame.EME2000;
        boolean firstPass = true;

        for(OemDataBlock block : blocks) {
            OemMetadata metadata = block.getMetadata();
            if(firstPass) {
                centerName = metadata.getCenter_name();
                timeSystem = metadata.getTime_system();
                refName = metadata.getRef_frame();
                firstPass = false;
                continue;
            }

            if (metadata.getCenter_name() != centerName) {
                throw new IllegalArgumentException("STK Ephemeris files do not support multiple CentralBodies");
            }

            if (metadata.getTime_system() != timeSystem) {
                throw new IllegalArgumentException("STk Ephemeris files do not support multiple time systems");
            }

            if (metadata.getRef_frame() != refName) {
                throw new IllegalArgumentException("STK Ephemeris files do not support multiple coordinate systems");
            }
        }
    }

    private static double dateStringToEpochSec(String dateString, LocalDateTime epoch) {
        LocalDateTime date = LocalDateTime.parse(dateString);
        return ChronoUnit.MILLIS.between(epoch, date) / 1000.0;
    }
}
