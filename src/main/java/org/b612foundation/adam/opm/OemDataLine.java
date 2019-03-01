package org.b612foundation.adam.opm;

import java.util.Arrays;
import java.util.Objects;

/** Data line of OrbitEphemerisMessage: date, x, y, x, vx, vy, vz. Accelerations are optional, ignore them for now. */
public class OemDataLine {
  private String date;
  private double[] point = new double[6];

  public OemDataLine(String date, double x, double y, double z, double vx, double vy, double vz) {
    this.date = date;
    this.point[0] = x;
    this.point[1] = y;
    this.point[2] = z;
    this.point[3] = vx;
    this.point[4] = vy;
    this.point[5] = vz;
  }

  public String getDate() {
    return date;
  }

  public double[] getPoint() {
    return point;
  }

  @Override
  public int hashCode() {
    return Objects.hash(date, point);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    OemDataLine other = (OemDataLine) obj;
    return Objects.equals(date, other.date) && Arrays.equals(point, other.point);
  }

  @Override
  public String toString() {
    return date + " " + this.point[0] + " " + this.point[1] + " " + this.point[2] + " " + this.point[3] + " "
        + this.point[4] + " " + this.point[5] + "\n";
  }
}
