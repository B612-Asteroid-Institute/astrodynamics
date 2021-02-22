package org.b612foundation.adam.batches;

import lombok.SneakyThrows;
import org.hipparchus.distribution.multivariate.AbstractMultivariateRealDistribution;
import org.hipparchus.exception.LocalizedCoreFormats;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.linear.EigenDecomposition;
import org.hipparchus.linear.RealMatrix;
import org.hipparchus.random.RandomGenerator;
import org.hipparchus.random.Well19937c;
import org.hipparchus.util.FastMath;

/**
 * Implementation of the multivariate normal (Gaussian) distribution around covariance matrices.
 * This is heavily based on the <a href="https://github.com/Hipparchus-Math/hipparchus">Hipparchus
 * Math Project</a>
 *
 * <p><a
 * href="https://github.com/Hipparchus-Math/hipparchus/blob/master/hipparchus-core/src/main/java/org/hipparchus/distribution/multivariate/MultivariateNormalDistribution.java">
 * MultivariateNormalDistribution</a> class. Our class differs in that it only supports sampling and
 * it has a "near zero" check for just less than zero eigenvalues that can happen with precision
 * issues in covariance matrices. For general use cases it is recommended to use the Hipparchus
 * classes. However to deal with the numerical issues with the covariance matrices this can be used
 * for sampling instead.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Multivariate_normal_distribution">Multivariate normal
 *     distribution (Wikipedia)</a>
 * @see <a href="http://mathworld.wolfram.com/MultivariateNormalDistribution.html">Multivariate
 *     normal distribution (MathWorld)</a>
 */
class StateCovarianceNormalDistribution extends AbstractMultivariateRealDistribution {
  /** Default singular matrix tolerance check value * */
  public static final double DEFAULT_TOLERANCE = 1e-14;

  /** Vector of means. */
  private final double[] means;
  /** Covariance matrix. */
  private final RealMatrix covarianceMatrix;
  /** Matrix used in computation of samples. */
  private final RealMatrix samplingMatrix;
  /** Inverse singular check tolerance when testing if invertable * */
  private final double singularMatrixCheckTolerance;

  /**
   * Creates a multivariate normal distribution with the given mean vector and covariance matrix.
   * <br>
   * The number of dimensions is equal to the length of the mean vector and to the number of rows
   * and columns of the covariance matrix. It is frequently written as "p" in formulae.
   *
   * <p><b>Note:</b> this constructor will implicitly create an instance of {@link Well19937c} as
   * random generator to be used for sampling only (see {@link #sample()} and {@link #sample(int)}).
   * In case no sampling is needed for the created distribution, it is advised to pass {@code null}
   * as random generator via the appropriate constructors to avoid the additional initialisation
   * overhead.
   *
   * @param means Vector of means.
   * @param covariances Covariance matrix.
   * @param singularMatrixCheckTolerance Tolerance used during the singular matrix check before
   *     inversion
   * @throws MathIllegalArgumentException if the arrays length are inconsistent.
   * @throws MathIllegalArgumentException if the eigenvalue decomposition cannot be performed on the
   *     provided covariance matrix.
   * @throws MathIllegalArgumentException if any of the eigenvalues is negative.
   */
  public StateCovarianceNormalDistribution(
      final double[] means, final double[][] covariances, final double singularMatrixCheckTolerance)
      throws MathIllegalArgumentException {
    this(new Well19937c(), means, covariances, singularMatrixCheckTolerance);
  }

  /**
   * Creates a multivariate normal distribution with the given mean vector and covariance matrix.
   * <br>
   * The number of dimensions is equal to the length of the mean vector and to the number of rows
   * and columns of the covariance matrix. It is frequently written as "p" in formulae.
   *
   * @param rng Random Number Generator.
   * @param means Vector of means.
   * @param covariances Covariance matrix.
   * @param singularMatrixCheckTolerance Tolerance used during the singular matrix check before
   *     inversion
   * @throws MathIllegalArgumentException if the arrays length are inconsistent.
   * @throws MathIllegalArgumentException if the eigenvalue decomposition cannot be performed on the
   *     provided covariance matrix.
   * @throws MathIllegalArgumentException if any of the eigenvalues is negative.
   */
  public StateCovarianceNormalDistribution(
      RandomGenerator rng,
      final double[] means,
      final double[][] covariances,
      final double singularMatrixCheckTolerance)
      throws MathIllegalArgumentException {
    super(rng, means.length);

    final int dim = means.length;

    if (covariances.length != dim) {
      throw new MathIllegalArgumentException(
          LocalizedCoreFormats.DIMENSIONS_MISMATCH, covariances.length, dim);
    }

    for (int i = 0; i < dim; i++) {
      if (dim != covariances[i].length) {
        throw new MathIllegalArgumentException(
            LocalizedCoreFormats.DIMENSIONS_MISMATCH, covariances[i].length, dim);
      }
    }

    this.means = means.clone();
    this.singularMatrixCheckTolerance = singularMatrixCheckTolerance;

    covarianceMatrix = new Array2DRowRealMatrix(covariances);

    // Covariance matrix eigen decomposition.
    final EigenDecomposition covMatDec =
        new EigenDecomposition(covarianceMatrix, singularMatrixCheckTolerance);

    // Eigenvalues of the covariance matrix.
    final double[] covMatEigenvalues = covMatDec.getRealEigenvalues();

    for (int i = 0; i < covMatEigenvalues.length; i++) {
      if (covMatEigenvalues[i] < 0) {
        if (Math.abs(covMatEigenvalues[i]) > singularMatrixCheckTolerance) {
          throw new IllegalArgumentException(
              "Failed positive definite matrix check with negative eigenvalue above zero threshold("
                  + singularMatrixCheckTolerance
                  + "): "
                  + covMatEigenvalues[i]);
        }
        covMatEigenvalues[i] *= -1;
      }
    }

    // Matrix where each column is an eigenvector of the covariance matrix.
    final Array2DRowRealMatrix covMatEigenvectors = new Array2DRowRealMatrix(dim, dim);
    for (int v = 0; v < dim; v++) {
      final double[] evec = covMatDec.getEigenvector(v).toArray();
      covMatEigenvectors.setColumn(v, evec);
    }

    final RealMatrix tmpMatrix = covMatEigenvectors.transpose();

    // Scale each eigenvector by the square root of its eigenvalue.
    for (int row = 0; row < dim; row++) {
      final double factor = FastMath.sqrt(covMatEigenvalues[row]);
      for (int col = 0; col < dim; col++) {
        tmpMatrix.multiplyEntry(row, col, factor);
      }
    }

    samplingMatrix = covMatEigenvectors.multiply(tmpMatrix);
  }

  /**
   * Gets the mean vector.
   *
   * @return the mean vector.
   */
  public double[] getMeans() {
    return means.clone();
  }

  /**
   * Gets the covariance matrix.
   *
   * @return the covariance matrix.
   */
  public RealMatrix getCovariances() {
    return covarianceMatrix.copy();
  }

  /**
   * Gets the current setting for the tolerance check used during singular checks before inversion
   */
  public double getSingularMatrixCheckTolerance() {
    return singularMatrixCheckTolerance;
  }

  @SneakyThrows
  @Override
  public double density(double[] x) {
    throw new Exception("Not implemented");
  }

  /** {@inheritDoc} */
  @Override
  public double[] sample() {
    final int dim = getDimension();
    final double[] normalVals = new double[dim];

    for (int i = 0; i < dim; i++) {
      normalVals[i] = random.nextGaussian();
    }

    final double[] vals = samplingMatrix.operate(normalVals);

    for (int i = 0; i < dim; i++) {
      vals[i] += means[i];
    }

    return vals;
  }
}
