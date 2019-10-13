package org.b612foundation.adam.opm;

import org.b612foundation.adam.astro.AstroUtils;

import javax.swing.plaf.nimbus.State;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.b612foundation.adam.astro.AstroConstants.AU_PER_DAY_TO_KM_PER_SEC;
import static org.b612foundation.adam.astro.AstroConstants.AU_TO_KM;

public class OorbToOdmConverter {

    public static OrbitParameterMessage oorbFullOrbitToOpm(String orbitLine) {
        final int objectIdIndex = 0;
        final int xIndex = 1;
        final int yIndex = 2;
        final int zIndex = 3;
        final int vxIndex = 4;
        final int vyIndex = 5;
        final int vzIndex = 6;
        final int mjdIndex = 7;
        final int covXXIndex = 8;
        final int covYYIndex = 9;
        final int covZZIndex = 10;
        final int covXdotXdotIndex = 11;
        final int covYdotYdotIndex = 12;
        final int covZdotZdotIndex = 13;
        final int covXYIndex = 14;
        final int covXZIndex = 15;
        final int covXXdotIndex = 16;
        final int covXYdotIndex = 17;
        final int covXZdotIndex = 18;
        final int covYZIndex = 19;
        final int covYXdotIndex = 20;
        final int covYYdotIndex = 21;
        final int covYZdotIndex = 22;
        final int covZXdotIndex = 23;
        final int covZYdotIndex = 24;
        final int covZZdotIndex = 25;
        final int covXdotYdotIndex = 26;
        final int covXdotZdotIndex = 27;
        final int covYdotZdotIndex = 28;
        final int expectedElementsCount = 31;
        String[] elems = orbitLine.trim().split("\\s+");
        if (elems.length != expectedElementsCount) {
            throw new IllegalArgumentException("Incorrect number of arguments on line: " + elems.length);
        }
        StateVector stateVector = new StateVector();
        stateVector.setX(Double.parseDouble(elems[xIndex]) * AU_TO_KM);
        stateVector.setY(Double.parseDouble(elems[yIndex]) * AU_TO_KM);
        stateVector.setZ(Double.parseDouble(elems[zIndex]) * AU_TO_KM);
        stateVector.setX_dot(Double.parseDouble(elems[vxIndex]) * AU_PER_DAY_TO_KM_PER_SEC);
        stateVector.setY_dot(Double.parseDouble(elems[vyIndex]) * AU_PER_DAY_TO_KM_PER_SEC);
        stateVector.setZ_dot(Double.parseDouble(elems[vzIndex]) * AU_PER_DAY_TO_KM_PER_SEC);
        LocalDateTime epoch = AstroUtils.localDateTimefromMJD(Double.parseDouble(elems[mjdIndex]));
        stateVector.setEpoch(epoch.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        CovarianceMatrix covarianceMatrix = new CovarianceMatrix();
        covarianceMatrix.setCx_x(Double.parseDouble(elems[covXXIndex]) * AU_TO_KM * AU_TO_KM);
        covarianceMatrix.setCy_y(Double.parseDouble(elems[covYYIndex]) * AU_TO_KM * AU_TO_KM);
        covarianceMatrix.setCz_z(Double.parseDouble(elems[covZZIndex]) * AU_TO_KM * AU_TO_KM);
        covarianceMatrix.setCx_dot_x_dot(Double.parseDouble(elems[covXdotXdotIndex]) * AU_PER_DAY_TO_KM_PER_SEC * AU_PER_DAY_TO_KM_PER_SEC);
        covarianceMatrix.setCy_dot_y_dot(Double.parseDouble(elems[covYdotYdotIndex]) * AU_PER_DAY_TO_KM_PER_SEC * AU_PER_DAY_TO_KM_PER_SEC);
        covarianceMatrix.setCz_dot_z_dot(Double.parseDouble(elems[covZdotZdotIndex]) * AU_PER_DAY_TO_KM_PER_SEC * AU_PER_DAY_TO_KM_PER_SEC);

        covarianceMatrix.setCy_x(Double.parseDouble(elems[covXYIndex]) * AU_TO_KM * AU_TO_KM);
        covarianceMatrix.setCz_x(Double.parseDouble(elems[covXZIndex]) * AU_TO_KM * AU_TO_KM);
        covarianceMatrix.setCx_dot_x(Double.parseDouble(elems[covXXdotIndex]) * AU_TO_KM * AU_PER_DAY_TO_KM_PER_SEC);
        covarianceMatrix.setCy_dot_x(Double.parseDouble(elems[covXYdotIndex]) * AU_TO_KM * AU_PER_DAY_TO_KM_PER_SEC);
        covarianceMatrix.setCz_dot_x(Double.parseDouble(elems[covXZdotIndex]) * AU_TO_KM * AU_PER_DAY_TO_KM_PER_SEC);

        covarianceMatrix.setCz_y(Double.parseDouble(elems[covYZIndex]) * AU_TO_KM * AU_TO_KM);
        covarianceMatrix.setCx_dot_y(Double.parseDouble(elems[covYXdotIndex]) * AU_TO_KM * AU_PER_DAY_TO_KM_PER_SEC);
        covarianceMatrix.setCy_dot_y(Double.parseDouble(elems[covYYdotIndex]) * AU_TO_KM * AU_PER_DAY_TO_KM_PER_SEC);
        covarianceMatrix.setCz_dot_y(Double.parseDouble(elems[covYZdotIndex]) * AU_TO_KM * AU_PER_DAY_TO_KM_PER_SEC);

        covarianceMatrix.setCx_dot_z(Double.parseDouble(elems[covZXdotIndex]) * AU_TO_KM * AU_PER_DAY_TO_KM_PER_SEC);
        covarianceMatrix.setCy_dot_z(Double.parseDouble(elems[covZYdotIndex]) * AU_TO_KM * AU_PER_DAY_TO_KM_PER_SEC);
        covarianceMatrix.setCz_dot_z(Double.parseDouble(elems[covZZdotIndex]) * AU_TO_KM * AU_PER_DAY_TO_KM_PER_SEC);

        covarianceMatrix.setCy_dot_x_dot(Double.parseDouble(elems[covXdotYdotIndex]) * AU_PER_DAY_TO_KM_PER_SEC * AU_PER_DAY_TO_KM_PER_SEC);
        covarianceMatrix.setCz_dot_x_dot(Double.parseDouble(elems[covXdotZdotIndex]) * AU_PER_DAY_TO_KM_PER_SEC * AU_PER_DAY_TO_KM_PER_SEC);

        covarianceMatrix.setCz_dot_y_dot(Double.parseDouble(elems[covYdotZdotIndex]) * AU_PER_DAY_TO_KM_PER_SEC * AU_PER_DAY_TO_KM_PER_SEC);

        OdmCommonHeader header = new OdmCommonHeader();
        header.setCreation_date(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        OdmCommonMetadata metadata = new OdmCommonMetadata();
        metadata.setRef_frame(OdmCommonMetadata.ReferenceFrame.GCRF);
        metadata.setTime_system(OdmCommonMetadata.TimeSystem.UTC);
        metadata.setCenter_name(OdmCommonMetadata.CenterName.SUN);
        metadata.setObject_id(elems[objectIdIndex]);
        metadata.setObject_name(metadata.getObject_id());
        OrbitParameterMessage opm = new OrbitParameterMessage();
        opm.setState_vector(stateVector);
        opm.setCovariance(covarianceMatrix);
        opm.setHeader(header);
        opm.setMetadata(metadata);

        return opm;
    }

    public static String opmToOorbFullOrbit(OrbitParameterMessage opm, double H, double G) {
        //"5021              0.59654514357699E-01 -0.27361945126639E+01 -0.27774936502626E+00  0.70760512465527E-02  0.22455411147103E-02  0.19783428192193E-02   73452.00000000   0.9733632E-05   0.8131753E-05   0.5220416E-05   0.6160844E-07   0.3574345E-07   0.1843793E-07   0.2857906E+00   0.4624086E+00   0.1496698E+00  -0.5459364E+00  -0.2757571E+00   0.9126070E+00  -0.8804816E+00  -0.9426819E+00  -0.9066317E+00  -0.7133995E+00  -0.9073508E+00  -0.9215130E+00   0.6956380E+00   0.8103546E+00   0.8520601E+00  20.00004  0.150000\n";
        String fmt = "%-17s%21.14E %21.14E %21.14E %21.14E %21.14E %21.14E %16.8f %15.7E %15.7E %15.7E %15.7E %15.7E %15.7E %15.7E %15.7E %15.7E %15.7E %15.7E %15.7E %15.7E %15.7E %15.7E %15.7E %15.7E %15.7E %15.7E %15.7E %15.7E %9.5f %9.6f";
        OdmCommonMetadata m = opm.getMetadata();
        StateVector pv = opm.getState_vector();
        CovarianceMatrix c = opm.getCovariance();
        LocalDateTime epoch = LocalDateTime.parse(opm.getState_vector().getEpoch());
        double mjdEpoch = AstroUtils.mjdFromLocalDateTime(epoch);
        String rval = String.format(fmt,m.getObject_id(),
                pv.getX() / AU_TO_KM,
                pv.getY() / AU_TO_KM,
                pv.getZ() / AU_TO_KM,
                pv.getX_dot() / AU_PER_DAY_TO_KM_PER_SEC,
                pv.getY_dot() / AU_PER_DAY_TO_KM_PER_SEC,
                pv.getZ_dot() / AU_PER_DAY_TO_KM_PER_SEC,
                mjdEpoch,
                c.getCx_x() / (AU_TO_KM * AU_TO_KM),
                c.getCy_y() / (AU_TO_KM * AU_TO_KM),
                c.getCz_z() / (AU_TO_KM * AU_TO_KM),
                c.getCx_dot_x_dot() / (AU_PER_DAY_TO_KM_PER_SEC * AU_PER_DAY_TO_KM_PER_SEC),
                c.getCy_dot_y_dot() / (AU_PER_DAY_TO_KM_PER_SEC * AU_PER_DAY_TO_KM_PER_SEC),
                c.getCz_dot_z_dot() / (AU_PER_DAY_TO_KM_PER_SEC * AU_PER_DAY_TO_KM_PER_SEC),
                c.getCy_x() / (AU_TO_KM * AU_TO_KM),
                c.getCz_x() / (AU_TO_KM * AU_TO_KM),
                c.getCx_dot_x() / (AU_TO_KM * AU_PER_DAY_TO_KM_PER_SEC),
                c.getCy_dot_x() / (AU_TO_KM * AU_PER_DAY_TO_KM_PER_SEC),
                c.getCz_dot_x() / (AU_TO_KM * AU_PER_DAY_TO_KM_PER_SEC),
                c.getCz_y() / (AU_TO_KM * AU_TO_KM),
                c.getCx_dot_y() / (AU_TO_KM * AU_PER_DAY_TO_KM_PER_SEC),
                c.getCy_dot_y() / (AU_TO_KM * AU_PER_DAY_TO_KM_PER_SEC),
                c.getCz_dot_y() / (AU_TO_KM * AU_PER_DAY_TO_KM_PER_SEC),
                c.getCx_dot_z() / (AU_TO_KM * AU_PER_DAY_TO_KM_PER_SEC),
                c.getCy_dot_z() / (AU_TO_KM * AU_PER_DAY_TO_KM_PER_SEC),
                c.getCz_dot_z() / (AU_TO_KM * AU_PER_DAY_TO_KM_PER_SEC),
                c.getCy_dot_x_dot() / (AU_PER_DAY_TO_KM_PER_SEC * AU_PER_DAY_TO_KM_PER_SEC),
                c.getCz_dot_x_dot() / (AU_PER_DAY_TO_KM_PER_SEC * AU_PER_DAY_TO_KM_PER_SEC),
                c.getCz_dot_y_dot() / (AU_PER_DAY_TO_KM_PER_SEC * AU_PER_DAY_TO_KM_PER_SEC),
                H,
                G
        );
        return rval;
    }
}
