package org.b612foundation.adam.batches;

import org.junit.Test;

import static org.b612foundation.adam.batches.StateCovarianceNormalDistribution.DEFAULT_TOLERANCE;
import static org.junit.Assert.assertEquals;

public class StateCovarianceNormalDistributionTest {
  final int n = 500000;
  final double sampledValueTolerance = 1e-2;

  @Test
  public void testPositiveDefiniteCovariance() {
    double[] mean =
        new double[] {
          1.58742232686,
          0.429795888482,
          26.007089460445,
          4.735934234028,
          350.637412636631,
          250.39674106714
        };
    double[][] covariance =
        new double[][] {
          {
            1.76208738E-13,
            -9.37625373E-13,
            -1.94760551E-12,
            -2.56572222E-11,
            -7.30093964E-11,
            -1.98340808E-09
          },
          {
            -9.37625373E-13,
            5.00812620E-12,
            1.06017205E-11,
            1.40431472E-10,
            3.62452521E-10,
            1.05830167E-08
          },
          {
            -1.94760551E-12,
            1.06017205E-11,
            3.15658331E-11,
            2.32155752E-09,
            -1.53067748E-09,
            2.23110293E-08
          },
          {
            -2.56572222E-11,
            1.40431472E-10,
            2.32155752E-09,
            8.81161492E-07,
            -8.70304198E-07,
            2.93564832E-07
          },
          {
            -7.30093964E-11,
            3.62452521E-10,
            -1.53067748E-09,
            -8.70304198E-07,
            9.42413982E-07,
            7.81029359E-07
          },
          {
            -1.98340808E-09,
            1.05830167E-08,
            2.23110293E-08,
            2.93564832E-07,
            7.81029359E-07,
            2.23721205E-05
          }
        };

    testDistribution(mean, covariance, n, sampledValueTolerance);
  }

  @Test
  public void testNumericallySlightlyNonPositiveDefiniteCovariance() {
    double[] mean =
        new double[] {
          1.58742232686,
          0.429795888482,
          26.007089460445,
          4.735934234028,
          350.637412636631,
          250.39674106714
        };
    double[][] covariance =
        new double[][] {
          {
            3.202324389968706E-7,
            1.2923375059417622E-7,
            7.72289328564638E-7,
            -1.555005867396374E-8,
            -9.799794525283014E-7,
            -9.185557222428368E-5
          },
          {
            1.2923375059417622E-7,
            5.21538938E-8,
            3.11667411E-7,
            -6.27420946E-9,
            -3.95490448E-7,
            -3.70694474E-5
          },
          {
            7.72289328564638E-7,
            3.11667411E-7,
            1.86254288E-6,
            -3.7443273E-8,
            -2.36359792E-6,
            -2.21523664E-4
          },
          {
            -1.555005867396374E-8,
            -6.27420946E-9,
            -3.7443273E-8,
            8.81646243E-10,
            4.70864023E-8,
            4.46048322E-6
          },
          {
            -9.799794525283014E-7,
            -3.95490448E-7,
            -2.36359792E-6,
            4.70864023E-8,
            3.00195616E-6,
            2.81096913E-4
          },
          {
            -9.185557222428368E-5,
            -3.70694474E-5,
            -2.21523664E-4,
            4.46048322E-6,
            2.81096913E-4,
            0.026347881
          }
        };

    testDistribution(mean, covariance, n, sampledValueTolerance);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNonPositiveDefiniteMatrix() {
    double[] mean =
        new double[] {
          1.58742232686,
          0.429795888482,
          26.007089460445,
          4.735934234028,
          350.637412636631,
          250.39674106714
        };
    double[][] covariance =
        new double[][] {
          {
            3.202324389968706E-7,
            1.2923375059417622E-7,
            7.72289328564638E-7,
            -1.555005867396374E-8,
            -9.799794525283014E-7,
            -9
          },
          {
            1.2923375059417622E-7,
            5.21538938E-8,
            3.11667411E-7,
            -6.27420946E-9,
            -3.95490448E-7,
            -3.70694474E-5
          },
          {
            7.72289328564638E-7,
            3.11667411E-7,
            1.86254288E-6,
            -3.7443273E-8,
            -2.36359792E-6,
            -2.21523664E-4
          },
          {
            -1.555005867396374E-8,
            -6.27420946E-9,
            -3.7443273E-8,
            8.81646243E-10,
            4.70864023E-8,
            4.46048322E-6
          },
          {
            -9.799794525283014E-7,
            -3.95490448E-7,
            -2.36359792E-6,
            4.70864023E-8,
            3.00195616E-6,
            2.81096913E-4
          },
          {-9, -3.70694474E-5, -2.21523664E-4, 4.46048322E-6, 2.81096913E-4, 0.026347881}
        };

    testDistribution(mean, covariance, n, sampledValueTolerance);
  }

  @Test
  public void hipparchusSamplingTestCase() {
    final double[] mu = {-1.5, 2};
    final double[][] sigma = {{2, -1.1}, {-1.1, 2}};
    testDistribution(mu, sigma, n, sampledValueTolerance);
  }

  private void testDistribution(
      double[] mean, double[][] covariance, int sampleCount, double meanTolerance) {
    final StateCovarianceNormalDistribution d =
        new StateCovarianceNormalDistribution(mean, covariance, DEFAULT_TOLERANCE);
    d.reseedRandomGenerator(50);
    final double[][] samples = d.sample(sampleCount);
    final int dim = d.getDimension();
    final double[] sampleMeans = new double[dim];

    for (int i = 0; i < samples.length; i++) {
      for (int j = 0; j < dim; j++) {
        sampleMeans[j] += samples[i][j];
      }
    }

    for (int j = 0; j < dim; j++) {
      sampleMeans[j] /= samples.length;
      assertEquals(mean[j], sampleMeans[j], meanTolerance);
    }
  }
}
