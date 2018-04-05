package org.b612foundation.adam.opm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * State Vector Components. The coordinate system is given in the metadata.
 * https://public.ccsds.org/Pubs/502x0b2c1.pdf
 */
public class StateVector implements Serializable {
	/** Optional comments. */
	private List<String> comments = new ArrayList<>();
	/** For state vector and Keplerian elements. */
	private String epoch = null;
	/** Coordinates in km */
	private double x, y, z;
	/** Velocity in km/s */
	private double xDot, yDot, zDot;

  public StateVector deepCopy() {
    StateVector res = new StateVector();
    for (String c : comments)
      res.addComment(new String(c));
    if (epoch != null)
      res.setEpoch(new String(epoch));
    res.setX(x);
    res.setY(y);
    res.setZ(z);
    res.setX_dot(xDot);
    res.setY_dot(yDot);
    res.setZ_dot(zDot);
    return res;
  }

	public List<String> getComments() {
		return comments;
	}

	public StateVector setComments(List<String> comments) {
		this.comments = comments;
		return this;
	}

	public StateVector addComment(String comment) {
		this.comments.add(comment);
		return this;
	}

	public String getEpoch() {
		return epoch;
	}

	public StateVector setEpoch(String epoch) {
		this.epoch = epoch;
		return this;
	}

	public double getX() {
		return x;
	}

	public StateVector setX(double x) {
		this.x = x;
		return this;
	}

	public double getY() {
		return y;
	}

	public StateVector setY(double y) {
		this.y = y;
		return this;
	}

	public double getZ() {
		return z;
	}

	public StateVector setZ(double z) {
		this.z = z;
		return this;
	}

	public double getX_dot() {
		return xDot;
	}

	public StateVector setX_dot(double xDot) {
		this.xDot = xDot;
		return this;
	}

	public double getY_dot() {
		return yDot;
	}

	public StateVector setY_dot(double yDot) {
		this.yDot = yDot;
		return this;
	}

	public double getZ_dot() {
		return zDot;
	}

	public StateVector setZ_dot(double zDot) {
		this.zDot = zDot;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((comments == null) ? 0 : comments.hashCode());
		result = prime * result + ((epoch == null) ? 0 : epoch.hashCode());
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(xDot);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(yDot);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(zDot);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StateVector other = (StateVector) obj;
		if (comments == null) {
			if (other.comments != null)
				return false;
		} else if (!comments.equals(other.comments))
			return false;
		if (epoch == null) {
			if (other.epoch != null)
				return false;
		} else if (!epoch.equals(other.epoch))
			return false;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(xDot) != Double.doubleToLongBits(other.xDot))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		if (Double.doubleToLongBits(yDot) != Double.doubleToLongBits(other.yDot))
			return false;
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
			return false;
		if (Double.doubleToLongBits(zDot) != Double.doubleToLongBits(other.zDot))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StateVector [comments=").append(comments).append(", epoch=").append(epoch).append(", x=").append(x)
				.append(", y=").append(y).append(", z=").append(z).append(", xDot=").append(xDot).append(", yDot=").append(yDot)
				.append(", zDot=").append(zDot).append("]");
		return builder.toString();
	}
}
