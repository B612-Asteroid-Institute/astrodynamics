package org.b612foundation.adam.opm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Description of a spacecraft. May be included in OPM or OMM.
 * https://public.ccsds.org/Pubs/502x0b2c1.pdf
 */
public class SpacecraftParameters implements Serializable {
	/** Optional comments. */
	private List<String> comments = new ArrayList<>();
	/** Mass, kg */
	private double mass;
	/** Solar radiation pressure area, m^2. */
	private double solarRadArea;
	/** Solar radiation pressure coefficient. */
	private double solarRadCoeff;
	/** Drag area, m^2. */
	private double dragArea;
	/** Drag coefficient. */
	private double dragCoeff;

	public List<String> getComments() {
		return comments;
	}

	public SpacecraftParameters setComments(List<String> comments) {
		this.comments = comments;
		return this;
	}

	public SpacecraftParameters addComment(String comment) {
		this.comments.add(comment);
		return this;
	}

	public double getMass() {
		return mass;
	}

	public SpacecraftParameters setMass(double mass) {
		this.mass = mass;
		return this;
	}

	public double getSolar_rad_area() {
		return solarRadArea;
	}

	public SpacecraftParameters setSolar_rad_area(double solarRadArea) {
		this.solarRadArea = solarRadArea;
		return this;
	}

	public double getSolar_rad_coeff() {
		return solarRadCoeff;
	}

	public SpacecraftParameters setSolar_rad_coeff(double solarRadCoeff) {
		this.solarRadCoeff = solarRadCoeff;
		return this;
	}

	public double getDrag_area() {
		return dragArea;
	}

	public SpacecraftParameters setDrag_area(double dragArea) {
		this.dragArea = dragArea;
		return this;
	}

	public double getDrag_coeff() {
		return dragCoeff;
	}

	public SpacecraftParameters setDrag_coeff(double dragCoeff) {
		this.dragCoeff = dragCoeff;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((comments == null) ? 0 : comments.hashCode());
		long temp;
		temp = Double.doubleToLongBits(dragArea);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(dragCoeff);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(mass);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(solarRadArea);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(solarRadCoeff);
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
		SpacecraftParameters other = (SpacecraftParameters) obj;
		if (comments == null) {
			if (other.comments != null)
				return false;
		} else if (!comments.equals(other.comments))
			return false;
		if (Double.doubleToLongBits(dragArea) != Double.doubleToLongBits(other.dragArea))
			return false;
		if (Double.doubleToLongBits(dragCoeff) != Double.doubleToLongBits(other.dragCoeff))
			return false;
		if (Double.doubleToLongBits(mass) != Double.doubleToLongBits(other.mass))
			return false;
		if (Double.doubleToLongBits(solarRadArea) != Double.doubleToLongBits(other.solarRadArea))
			return false;
		if (Double.doubleToLongBits(solarRadCoeff) != Double.doubleToLongBits(other.solarRadCoeff))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SpacecraftParameters [comments=").append(comments).append(", mass=").append(mass)
				.append(", solarRadArea=").append(solarRadArea).append(", solarRadCoeff=").append(solarRadCoeff)
				.append(", dragArea=").append(dragArea).append(", dragCoeff=").append(dragCoeff).append("]");
		return builder.toString();
	}
}
