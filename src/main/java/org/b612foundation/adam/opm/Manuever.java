package org.b612foundation.adam.opm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Description of an individual manuever (burn). Used in OPM.
 * https://public.ccsds.org/Pubs/502x0b2c1.pdf
 */
public class Manuever implements Serializable {
	/** Optional comments. */
	private List<String> comments = new ArrayList<>();
	/** Epoch of ignition. */
	private String manEpochIgnition;
	/** Durations in seconds, >=0. */
	private double duration;
	/** Mass change in kg, <=0. */
	private double deltaMass;
	/** Coordinate system for velocity increment vector. */
	private OdmCommonMetadata.ReferenceFrame manRefFrame;
	/** Velocity increment vector components, km/s */
	private double manDv1, manDv2, manDv3;

  public Manuever deepCopy() {
    Manuever res = new Manuever();
    for (String c : comments)
      res.addComment(new String(c));
    if (manEpochIgnition != null)
      res.setMan_epoch_ignition(new String(manEpochIgnition));
    res.setDuration(duration);
    res.setDelta_mass(deltaMass);
    res.setMan_ref_frame(manRefFrame);
    res.setMan_dv_1(manDv1);
    res.setMan_dv_2(manDv2);
    res.setMan_dv_3(manDv3);
    return res;
  }

	public List<String> getComments() {
		return comments;
	}

	public Manuever setComments(List<String> comments) {
		this.comments = comments;
		return this;
	}

	public Manuever addComment(String comment) {
		this.comments.add(comment);
		return this;
	}

	public String getMan_epoch_ignition() {
		return manEpochIgnition;
	}

	public Manuever setMan_epoch_ignition(String manEpochIgnition) {
		this.manEpochIgnition = manEpochIgnition;
		return this;
	}

	public double getDuration() {
		return duration;
	}

	public Manuever setDuration(double duration) {
		this.duration = duration;
		return this;
	}

	public double getDelta_mass() {
		return deltaMass;
	}

	public Manuever setDelta_mass(double deltaMass) {
		this.deltaMass = deltaMass;
		return this;
	}

	public OdmCommonMetadata.ReferenceFrame getMan_ref_frame() {
		return manRefFrame;
	}

	public Manuever setMan_ref_frame(OdmCommonMetadata.ReferenceFrame manRefFrame) {
		this.manRefFrame = manRefFrame;
		return this;
	}

	public double getMan_dv_1() {
		return manDv1;
	}

	public Manuever setMan_dv_1(double manDv1) {
		this.manDv1 = manDv1;
		return this;
	}

	public double getMan_dv_2() {
		return manDv2;
	}

	public Manuever setMan_dv_2(double manDv2) {
		this.manDv2 = manDv2;
		return this;
	}

	public double getMan_dv_3() {
		return manDv3;
	}

	public Manuever setMan_dv_3(double manDv3) {
		this.manDv3 = manDv3;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((comments == null) ? 0 : comments.hashCode());
		long temp;
		temp = Double.doubleToLongBits(deltaMass);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(duration);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(manDv1);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(manDv2);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(manDv3);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((manEpochIgnition == null) ? 0 : manEpochIgnition.hashCode());
		result = prime * result + ((manRefFrame == null) ? 0 : manRefFrame.hashCode());
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
		Manuever other = (Manuever) obj;
		if (comments == null) {
			if (other.comments != null)
				return false;
		} else if (!comments.equals(other.comments))
			return false;
		if (Double.doubleToLongBits(deltaMass) != Double.doubleToLongBits(other.deltaMass))
			return false;
		if (Double.doubleToLongBits(duration) != Double.doubleToLongBits(other.duration))
			return false;
		if (Double.doubleToLongBits(manDv1) != Double.doubleToLongBits(other.manDv1))
			return false;
		if (Double.doubleToLongBits(manDv2) != Double.doubleToLongBits(other.manDv2))
			return false;
		if (Double.doubleToLongBits(manDv3) != Double.doubleToLongBits(other.manDv3))
			return false;
		if (manEpochIgnition == null) {
			if (other.manEpochIgnition != null)
				return false;
		} else if (!manEpochIgnition.equals(other.manEpochIgnition))
			return false;
		if (manRefFrame != other.manRefFrame)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Manuever [comments=").append(comments).append(", manEpochIgnition=").append(manEpochIgnition)
				.append(", duration=").append(duration).append(", deltaMass=").append(deltaMass).append(", manRefFrame=")
				.append(manRefFrame).append(", manDv1=").append(manDv1).append(", manDv2=").append(manDv2).append(", manDv3=")
				.append(manDv3).append("]");
		return builder.toString();
	}
}
