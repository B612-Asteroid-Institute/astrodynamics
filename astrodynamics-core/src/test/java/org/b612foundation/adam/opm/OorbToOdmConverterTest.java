package org.b612foundation.adam.opm;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.b612foundation.adam.astro.AstroConstants.AU_PER_DAY_TO_KM_PER_SEC;
import static org.b612foundation.adam.astro.AstroConstants.AU_TO_KM;
import static org.junit.Assert.assertEquals;

public class OorbToOdmConverterTest {
  @Test
  public void testOorbOrbitToOpmConversion() {
    final double posTolerance = 1e-15;
    final double velTolerance = 1e-15;
    final double covTolerance = 1e-15;
    final OdmCommonMetadata.ReferenceFrame expectedRefFrame =
        OdmCommonMetadata.ReferenceFrame.J2000_IAU76ECLIP;
    final OdmCommonMetadata.TimeSystem expectedTimeSys = OdmCommonMetadata.TimeSystem.TT;
    final String expectedObjectId = "5021";
    final String expectedEpoch = "2059-12-25T00:00:00";
    final double expectedX = 0.59654514357699E-01 * AU_TO_KM;
    final double expectedY = -0.27361945126639E+01 * AU_TO_KM;
    final double expectedZ = -0.27774936502626E+00 * AU_TO_KM;
    final double expectedXdot = 0.70760512465527E-02 * AU_PER_DAY_TO_KM_PER_SEC;
    final double expectedYdot = 0.22455411147103E-02 * AU_PER_DAY_TO_KM_PER_SEC;
    final double expectedZdot = 0.19783428192193E-02 * AU_PER_DAY_TO_KM_PER_SEC;
    final double expectedCovXX = 0.9733632E-05 * AU_TO_KM * AU_TO_KM;
    final double expectedCovYY = 0.8131753E-05 * AU_TO_KM * AU_TO_KM;
    final double expectedCovZZ = 0.5220416E-05 * AU_TO_KM * AU_TO_KM;
    final double expectedCovVxVx =
        0.6160844E-07 * AU_PER_DAY_TO_KM_PER_SEC * AU_PER_DAY_TO_KM_PER_SEC;
    final double expectedCovVyVy =
        0.3574345E-07 * AU_PER_DAY_TO_KM_PER_SEC * AU_PER_DAY_TO_KM_PER_SEC;
    final double expectedCovVzVz =
        0.1843793E-07 * AU_PER_DAY_TO_KM_PER_SEC * AU_PER_DAY_TO_KM_PER_SEC;
    final double expectedCovYX = 0.2857906E+00 * AU_TO_KM * AU_TO_KM;
    final double expectedCovZX = 0.4624086E+00 * AU_TO_KM * AU_TO_KM;
    final double expectedCovVxX = 0.1496698E+00 * AU_TO_KM * AU_PER_DAY_TO_KM_PER_SEC;
    final double expectedCovVyX = -0.5459364E+00 * AU_TO_KM * AU_PER_DAY_TO_KM_PER_SEC;
    final double expectedCovVzX = -0.2757571E+00 * AU_TO_KM * AU_PER_DAY_TO_KM_PER_SEC;
    final double expectedCovZY = 0.9126070E+00 * AU_TO_KM * AU_TO_KM;
    final double expectedCovVxY = -0.8804816E+00 * AU_TO_KM * AU_PER_DAY_TO_KM_PER_SEC;
    final double expectedCovVyY = -0.9426819E+00 * AU_TO_KM * AU_PER_DAY_TO_KM_PER_SEC;
    final double expectedCovVzY = -0.9066317E+00 * AU_TO_KM * AU_PER_DAY_TO_KM_PER_SEC;
    final double expectedCovVxZ = -0.7133995E+00 * AU_TO_KM * AU_PER_DAY_TO_KM_PER_SEC;
    final double expectedCovVyZ = -0.9073508E+00 * AU_TO_KM * AU_PER_DAY_TO_KM_PER_SEC;
    final double expectedCovVzZ = -0.9215130E+00 * AU_TO_KM * AU_PER_DAY_TO_KM_PER_SEC;
    final double expectedCovVyVx =
        0.6956380E+00 * AU_PER_DAY_TO_KM_PER_SEC * AU_PER_DAY_TO_KM_PER_SEC;
    final double expectedCovVzVx =
        0.8103546E+00 * AU_PER_DAY_TO_KM_PER_SEC * AU_PER_DAY_TO_KM_PER_SEC;
    final double expectedCovVzVy =
        0.8520601E+00 * AU_PER_DAY_TO_KM_PER_SEC * AU_PER_DAY_TO_KM_PER_SEC;
    final String expectedComment =
        "Generated by org.b612foundation.adam.opm.OorbToOdmConverter.oorbFullOrbitToOpm method";
    final int expectedCommentSize = 1;
    String stateLine =
        "5021              0.59654514357699E-01 -0.27361945126639E+01 -0.27774936502626E+00  0.70760512465527E-02  0.22455411147103E-02  0.19783428192193E-02   73452.00000000   0.9733632E-05   0.8131753E-05   0.5220416E-05   0.6160844E-07   0.3574345E-07   0.1843793E-07   0.2857906E+00   0.4624086E+00   0.1496698E+00  -0.5459364E+00  -0.2757571E+00   0.9126070E+00  -0.8804816E+00  -0.9426819E+00  -0.9066317E+00  -0.7133995E+00  -0.9073508E+00  -0.9215130E+00   0.6956380E+00   0.8103546E+00   0.8520601E+00  20.00004  0.150000\n";
    OrbitParameterMessage opm = OorbToOdmConverter.oorbFullOrbitToOpm(stateLine);
    OdmCommonMetadata metadata = opm.getMetadata();
    assertEquals(expectedRefFrame, metadata.getRef_frame());
    assertEquals(expectedTimeSys, metadata.getTime_system());
    assertEquals(expectedObjectId, metadata.getObject_id());
    assertEquals(expectedObjectId, metadata.getObject_name());
    assertEquals(expectedCommentSize, metadata.getComments().size());
    assertEquals(expectedComment, metadata.getComments().get(0));

    final StateVector state = opm.getState_vector();
    assertEquals(expectedCommentSize, state.getComments().size());
    assertEquals(expectedComment, state.getComments().get(0));
    assertEquals(expectedEpoch, state.getEpoch());
    assertEquals(expectedX, state.getX(), posTolerance);
    assertEquals(expectedY, state.getY(), posTolerance);
    assertEquals(expectedZ, state.getZ(), posTolerance);
    assertEquals(expectedXdot, state.getX_dot(), velTolerance);
    assertEquals(expectedYdot, state.getY_dot(), velTolerance);
    assertEquals(expectedZdot, state.getZ_dot(), velTolerance);

    final CartesianCovariance cov = opm.getCartesianCovariance();
    assertEquals(expectedCommentSize, cov.getComments().size());
    assertEquals(expectedComment, cov.getComments().get(0));
    assertEquals(expectedEpoch, cov.getEpoch());
    assertEquals(expectedRefFrame, cov.getCov_ref_frame());
    assertEquals(expectedCovXX, cov.getCx_x(), Math.abs(expectedCovXX) * covTolerance);
    assertEquals(expectedCovYY, cov.getCy_y(), Math.abs(expectedCovYY) * covTolerance);
    assertEquals(expectedCovZZ, cov.getCz_z(), Math.abs(expectedCovZZ) * covTolerance);
    assertEquals(expectedCovVxVx, cov.getCx_dot_x_dot(), Math.abs(expectedCovVxVx) * covTolerance);
    assertEquals(expectedCovVyVy, cov.getCy_dot_y_dot(), Math.abs(expectedCovVyVy) * covTolerance);
    assertEquals(expectedCovVzVz, cov.getCz_dot_z_dot(), Math.abs(expectedCovVzVz) * covTolerance);
    assertEquals(expectedCovYX, cov.getCy_x(), Math.abs(expectedCovYX) * covTolerance);
    assertEquals(expectedCovZX, cov.getCz_x(), Math.abs(expectedCovZX) * covTolerance);
    assertEquals(expectedCovVxX, cov.getCx_dot_x(), Math.abs(expectedCovVxX) * covTolerance);
    assertEquals(expectedCovVyX, cov.getCy_dot_x(), Math.abs(expectedCovVyX) * covTolerance);
    assertEquals(expectedCovVzX, cov.getCz_dot_x(), Math.abs(expectedCovVzX) * covTolerance);
    assertEquals(expectedCovZY, cov.getCz_y(), Math.abs(expectedCovZY) * covTolerance);
    assertEquals(expectedCovVxY, cov.getCx_dot_y(), Math.abs(expectedCovVxY) * covTolerance);
    assertEquals(expectedCovVyY, cov.getCy_dot_y(), Math.abs(expectedCovVyY) * covTolerance);
    assertEquals(expectedCovVzY, cov.getCz_dot_y(), Math.abs(expectedCovVzY) * covTolerance);
    assertEquals(expectedCovVxZ, cov.getCx_dot_z(), Math.abs(expectedCovVxZ) * covTolerance);
    assertEquals(expectedCovVyZ, cov.getCy_dot_z(), Math.abs(expectedCovVyZ) * covTolerance);
    assertEquals(expectedCovVzZ, cov.getCz_dot_z(), Math.abs(expectedCovVzZ) * covTolerance);
    assertEquals(expectedCovVyVx, cov.getCy_dot_x_dot(), Math.abs(expectedCovVyVx) * covTolerance);
    assertEquals(expectedCovVzVx, cov.getCz_dot_x_dot(), Math.abs(expectedCovVzVx) * covTolerance);
    assertEquals(expectedCovVzVy, cov.getCz_dot_y_dot(), Math.abs(expectedCovVzVy) * covTolerance);
  }

  @Test
  public void testOpmToOorbFullOrbFormat() {
    String originalOorbLine =
        "5021              5.96545143576990E-02 -2.73619451266390E+00 -2.77749365026260E-01  7.07605124655270E-03  2.24554111471030E-03  1.97834281921930E-03   73452.00000000   9.7336320E-06   8.1317530E-06   5.2204160E-06   6.1608440E-08   3.5743450E-08   1.8437930E-08   2.8579060E-01   4.6240860E-01   1.4966980E-01  -5.4593640E-01  -2.7575710E-01   9.1260700E-01  -8.8048160E-01  -9.4268190E-01  -9.0663170E-01  -7.1339950E-01  -9.0735080E-01  -9.2151300E-01   6.9563800E-01   8.1035460E-01   8.5206010E-01  20.00004  0.150000";
    OrbitParameterMessage opm = OorbToOdmConverter.oorbFullOrbitToOpm(originalOorbLine);
    String convertedLine = OorbToOdmConverter.opmToOorbFullOrbit(opm, 20.00004, 0.150000);
    assertEquals(originalOorbLine, convertedLine);
  }

  @Test
  public void testOpmToFile() throws IOException {
    String originalOorbLine =
        "5021              5.96545143576990E-02 -2.73619451266390E+00 -2.77749365026260E-01  7.07605124655270E-03  2.24554111471030E-03  1.97834281921930E-03   73452.00000000   9.7336320E-06   8.1317530E-06   5.2204160E-06   6.1608440E-08   3.5743450E-08   1.8437930E-08   2.8579060E-01   4.6240860E-01   1.4966980E-01  -5.4593640E-01  -2.7575710E-01   9.1260700E-01  -8.8048160E-01  -9.4268190E-01  -9.0663170E-01  -7.1339950E-01  -9.0735080E-01  -9.2151300E-01   6.9563800E-01   8.1035460E-01   8.5206010E-01  20.00004  0.150000";
    OrbitParameterMessage opm = OorbToOdmConverter.oorbFullOrbitToOpm(originalOorbLine);
    Path orbFile = Files.createTempFile("oorbStateExample", ".orb");
    OorbToOdmConverter.opmToOorbFile(opm, 20.00004, 0.150000, orbFile);
    List<String> orbFileLines = Files.readAllLines(orbFile);
    assertEquals(5, orbFileLines.size());
    assertEquals(originalOorbLine, orbFileLines.get(orbFileLines.size() - 1));
  }
}
