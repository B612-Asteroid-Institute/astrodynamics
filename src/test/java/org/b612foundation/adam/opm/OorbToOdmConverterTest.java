package org.b612foundation.adam.opm;

import org.junit.Test;

import static org.b612foundation.adam.astro.AstroConstants.AU_PER_DAY_TO_KM_PER_SEC;
import static org.b612foundation.adam.astro.AstroConstants.AU_TO_KM;
import static org.junit.Assert.assertEquals;

public class OorbToOdmConverterTest {
    @Test
    public void testOorbOrbitToOpmConversion() {
        final double expectedX = 0.59654514357699E-01 * AU_TO_KM;
        final double expectedY = -0.27361945126639E+01 * AU_TO_KM;
        final double expectedZ = -0.27774936502626E+00 * AU_TO_KM;
        final double expectedXdot = 0.70760512465527E-02 * AU_PER_DAY_TO_KM_PER_SEC;
        final double expectedYdot = 0.22455411147103E-02 * AU_PER_DAY_TO_KM_PER_SEC;
        final double expectedZdot = 0.19783428192193E-02 * AU_PER_DAY_TO_KM_PER_SEC;
        final double posTolerance = 1e-15;
        final double velTolerance = 1e-15;
        String stateLine = "5021              0.59654514357699E-01 -0.27361945126639E+01 -0.27774936502626E+00  0.70760512465527E-02  0.22455411147103E-02  0.19783428192193E-02   73452.00000000   0.9733632E-05   0.8131753E-05   0.5220416E-05   0.6160844E-07   0.3574345E-07   0.1843793E-07   0.2857906E+00   0.4624086E+00   0.1496698E+00  -0.5459364E+00  -0.2757571E+00   0.9126070E+00  -0.8804816E+00  -0.9426819E+00  -0.9066317E+00  -0.7133995E+00  -0.9073508E+00  -0.9215130E+00   0.6956380E+00   0.8103546E+00   0.8520601E+00  20.00004  0.150000\n";
        OrbitParameterMessage opm = OorbToOdmConverter.oorbFullOrbitToOpm(stateLine);
        assertEquals(expectedX, opm.getState_vector().getX(), posTolerance);
        assertEquals(expectedY, opm.getState_vector().getY(), posTolerance);
        assertEquals(expectedZ, opm.getState_vector().getZ(), posTolerance);
        assertEquals(expectedXdot, opm.getState_vector().getX_dot(), velTolerance);
        assertEquals(expectedYdot, opm.getState_vector().getY_dot(), velTolerance);
        assertEquals(expectedZdot, opm.getState_vector().getZ_dot(), velTolerance);
        assertEquals("2059-12-25T00:00:00", opm.getState_vector().getEpoch());
        //TODO confirm covariance conversion
    }

    @Test
    public void testOpmToOorbFullOrbFormat() {
        String originalOorbLine = "5021              5.96545143576990E-02 -2.73619451266390E+00 -2.77749365026260E-01  7.07605124655270E-03  2.24554111471030E-03  1.97834281921930E-03   73452.00000000   9.7336320E-06   8.1317530E-06   5.2204160E-06   6.1608440E-08   3.5743450E-08   1.8437930E-08   2.8579060E-01   4.6240860E-01   1.4966980E-01  -5.4593640E-01  -2.7575710E-01   9.1260700E-01  -8.8048160E-01  -9.4268190E-01  -9.0663170E-01  -7.1339950E-01  -9.0735080E-01  -9.2151300E-01   6.9563800E-01   8.1035460E-01   8.5206010E-01  20.00004  0.150000";
        OrbitParameterMessage opm = OorbToOdmConverter.oorbFullOrbitToOpm(originalOorbLine);
        String convertedLine = OorbToOdmConverter.opmToOorbFullOrbit(opm, 20.00004,  0.150000);
        assertEquals(originalOorbLine, convertedLine);
    }
}
