package org.b612foundation.adam.astro;

import static java.lang.Math.*;

public class ReferenceFrameConverter {
  private static final double J2000_TO_JPL_ECLIPTIC_OBLIQUITY = toRadians(84381.448 / 3600.0);

  /**
   * Rotates a given position/velocity vector array from ICRF to JPL Horizon's Ecliptic definition
   *
   * @param posVel 6-element vector [x, y, z, vx, vy, vz]
   * @return a new 6-element vector rotated into the new frame [x, y, z, vx, vy, vz]
   */
  public static double[] convertICRFToJplEcliptic(double[] posVel) {
    return applyXRotation(J2000_TO_JPL_ECLIPTIC_OBLIQUITY, posVel);
  }

  /**
   * Rotates a given position/velocity vector array from JPL Horizon's Ecliptic definition to ICRF
   *
   * @param posVel 6-element vector [x, y, z, vx, vy, vz]
   * @return a new 6-element vector rotated into the new frame [x, y, z, vx, vy, vz]
   */
  public static double[] convertJplEclipticToICRF(double[] posVel) {
    return applyXRotation(-J2000_TO_JPL_ECLIPTIC_OBLIQUITY, posVel);
  }

  private static double[] applyXRotation(double phi, double[] posVel) {
    final int xi = 0;
    final int yi = 1;
    final int zi = 2;
    final int vxi = 3;
    final int vyi = 4;
    final int vzi = 5;
    double[] rval = new double[6];
       /*
        vec_f.X  =  vec_i.X
        vec_f.Y  =  vec_i.Y*np.cos(phi) + vec_i.Z*np.sin(phi)
        vec_f.Z  = -vec_i.Y*np.sin(phi) + vec_i.Z*np.cos(phi)

        vec_f.VX =  vec_i.VX
        vec_f.VY =  vec_i.VY*np.cos(phi) + vec_i.VZ*np.sin(phi)
        vec_f.VZ = -vec_i.VY*np.sin(phi) + vec_i.VZ*np.cos(phi)
         */
    rval[xi] = posVel[xi];
    rval[yi] = posVel[yi] * cos(phi) + posVel[zi] * sin(phi);
    rval[zi] = -posVel[yi] * sin(phi) + posVel[zi] * cos(phi);

    rval[vxi] = posVel[vxi];
    rval[vyi] = posVel[vyi] * cos(phi) + posVel[vzi] * sin(phi);
    rval[vzi] = -posVel[vyi] * sin(phi) + posVel[vzi] * cos(phi);

    return rval;
  }
}
