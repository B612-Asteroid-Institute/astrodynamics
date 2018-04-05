package org.b612foundation.adam.opm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Position/Velocity Covariance Matrix (6x6 Lower Triangular Form. None or all
 * parameters of the matrix must be given.
 * https://public.ccsds.org/Pubs/502x0b2c1.pdf
 */
public class CovarianceMatrix implements Serializable {
	/** Optional comments. */
	private List<String> comments = new ArrayList<>();
	/** Separate Epoch for covariance, used only in OEM. */
	private String epoch;
	/** Reference frame. If omited, same as metadata */
	private OdmCommonMetadata.ReferenceFrame covRefFrame = null;
	/**
	 * Covariance matrix. The variables are in order x,y,z,x',y',z'. Units are km
	 * and km/s, with corresponding products.
	 */
	private double cXX;
	private double CYX, CYY;
	private double CZX, CZY, CZZ;
	private double CXdotX, CXdotY, CXdotZ, CXdotXdot;
	private double CYdotX, CYdotY, CYdotZ, CYdotXdot, CYdotYdot;
	private double CZdotX, CZdotY, CZdotZ, CZdotXdot, CZdotYdot, CZdotZdot;

	public List<String> getComments() {
		return comments;
	}

	public CovarianceMatrix setComments(List<String> comments) {
		this.comments = comments;
		return this;
	}

	public CovarianceMatrix addComment(String comment) {
		this.comments.add(comment);
		return this;
	}

	public String getEpoch() {
		return epoch;
	}

	public CovarianceMatrix setEpoch(String epoch) {
		this.epoch = epoch;
		return this;
	}

	public OdmCommonMetadata.ReferenceFrame getCov_ref_frame() {
		return covRefFrame;
	}

	public CovarianceMatrix setCov_ref_frame(OdmCommonMetadata.ReferenceFrame covRefFrame) {
		this.covRefFrame = covRefFrame;
		return this;
	}

	public double getCx_x() {
		return cXX;
	}

	public CovarianceMatrix setCx_x(double cXX) {
		this.cXX = cXX;
		return this;
	}

	public double getCy_x() {
		return CYX;
	}

	public CovarianceMatrix setCy_x(double cYX) {
		CYX = cYX;
		return this;
	}

	public double getCy_y() {
		return CYY;
	}

	public CovarianceMatrix setCy_y(double cYY) {
		CYY = cYY;
		return this;
	}

	public double getCz_x() {
		return CZX;
	}

	public CovarianceMatrix setCz_x(double cZX) {
		CZX = cZX;
		return this;
	}

	public double getCz_y() {
		return CZY;
	}

	public CovarianceMatrix setCz_y(double cZY) {
		CZY = cZY;
		return this;
	}

	public double getCz_z() {
		return CZZ;
	}

	public CovarianceMatrix setCz_z(double cZZ) {
		CZZ = cZZ;
		return this;
	}

	public double getCx_dot_x() {
		return CXdotX;
	}

	public CovarianceMatrix setCx_dot_x(double cXdotX) {
		CXdotX = cXdotX;
		return this;
	}

	public double getCx_dot_y() {
		return CXdotY;
	}

	public CovarianceMatrix setCx_dot_y(double cXdotY) {
		CXdotY = cXdotY;
		return this;
	}

	public double getCx_dot_z() {
		return CXdotZ;
	}

	public CovarianceMatrix setCx_dot_z(double cXdotZ) {
		CXdotZ = cXdotZ;
		return this;
	}

	public double getCx_dot_x_dot() {
		return CXdotXdot;
	}

	public CovarianceMatrix setCx_dot_x_dot(double cXdotXdot) {
		CXdotXdot = cXdotXdot;
		return this;
	}

	public double getCy_dot_x() {
		return CYdotX;
	}

	public CovarianceMatrix setCy_dot_x(double cYdotX) {
		CYdotX = cYdotX;
		return this;
	}

	public double getCy_dot_y() {
		return CYdotY;
	}

	public CovarianceMatrix setCy_dot_y(double cYdotY) {
		CYdotY = cYdotY;
		return this;
	}

	public double getCy_dot_z() {
		return CYdotZ;
	}

	public CovarianceMatrix setCy_dot_z(double cYdotZ) {
		CYdotZ = cYdotZ;
		return this;
	}

	public double getCy_dot_x_dot() {
		return CYdotXdot;
	}

	public CovarianceMatrix setCy_dot_x_dot(double cYdotXdot) {
		CYdotXdot = cYdotXdot;
		return this;
	}

	public double getCy_dot_y_dot() {
		return CYdotYdot;
	}

	public CovarianceMatrix setCy_dot_y_dot(double cYdotYdot) {
		CYdotYdot = cYdotYdot;
		return this;
	}

	public double getCz_dot_x() {
		return CZdotX;
	}

	public CovarianceMatrix setCz_dot_x(double cZdotX) {
		CZdotX = cZdotX;
		return this;
	}

	public double getCz_dot_y() {
		return CZdotY;
	}

	public CovarianceMatrix setCz_dot_y(double cZdotY) {
		CZdotY = cZdotY;
		return this;
	}

	public double getCz_dot_z() {
		return CZdotZ;
	}

	public CovarianceMatrix setCz_dot_z(double cZdotZ) {
		CZdotZ = cZdotZ;
		return this;
	}

	public double getCz_dot_x_dot() {
		return CZdotXdot;
	}

	public CovarianceMatrix setCz_dot_x_dot(double cZdotXdot) {
		CZdotXdot = cZdotXdot;
		return this;
	}

	public double getCz_dot_y_dot() {
		return CZdotYdot;
	}

	public CovarianceMatrix setCz_dot_y_dot(double cZdotYdot) {
		CZdotYdot = cZdotYdot;
		return this;
	}

	public double getCz_dot_z_dot() {
		return CZdotZdot;
	}

	public CovarianceMatrix setCz_dot_z_dot(double cZdotZdot) {
		CZdotZdot = cZdotZdot;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(CXdotX);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(CXdotXdot);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(CXdotY);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(CXdotZ);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(CYX);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(CYY);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(CYdotX);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(CYdotXdot);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(CYdotY);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(CYdotYdot);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(CYdotZ);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(CZX);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(CZY);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(CZZ);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(CZdotX);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(CZdotXdot);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(CZdotY);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(CZdotYdot);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(CZdotZ);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(CZdotZdot);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(cXX);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((comments == null) ? 0 : comments.hashCode());
		result = prime * result + ((covRefFrame == null) ? 0 : covRefFrame.hashCode());
		result = prime * result + ((epoch == null) ? 0 : epoch.hashCode());
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
		CovarianceMatrix other = (CovarianceMatrix) obj;
		if (Double.doubleToLongBits(CXdotX) != Double.doubleToLongBits(other.CXdotX))
			return false;
		if (Double.doubleToLongBits(CXdotXdot) != Double.doubleToLongBits(other.CXdotXdot))
			return false;
		if (Double.doubleToLongBits(CXdotY) != Double.doubleToLongBits(other.CXdotY))
			return false;
		if (Double.doubleToLongBits(CXdotZ) != Double.doubleToLongBits(other.CXdotZ))
			return false;
		if (Double.doubleToLongBits(CYX) != Double.doubleToLongBits(other.CYX))
			return false;
		if (Double.doubleToLongBits(CYY) != Double.doubleToLongBits(other.CYY))
			return false;
		if (Double.doubleToLongBits(CYdotX) != Double.doubleToLongBits(other.CYdotX))
			return false;
		if (Double.doubleToLongBits(CYdotXdot) != Double.doubleToLongBits(other.CYdotXdot))
			return false;
		if (Double.doubleToLongBits(CYdotY) != Double.doubleToLongBits(other.CYdotY))
			return false;
		if (Double.doubleToLongBits(CYdotYdot) != Double.doubleToLongBits(other.CYdotYdot))
			return false;
		if (Double.doubleToLongBits(CYdotZ) != Double.doubleToLongBits(other.CYdotZ))
			return false;
		if (Double.doubleToLongBits(CZX) != Double.doubleToLongBits(other.CZX))
			return false;
		if (Double.doubleToLongBits(CZY) != Double.doubleToLongBits(other.CZY))
			return false;
		if (Double.doubleToLongBits(CZZ) != Double.doubleToLongBits(other.CZZ))
			return false;
		if (Double.doubleToLongBits(CZdotX) != Double.doubleToLongBits(other.CZdotX))
			return false;
		if (Double.doubleToLongBits(CZdotXdot) != Double.doubleToLongBits(other.CZdotXdot))
			return false;
		if (Double.doubleToLongBits(CZdotY) != Double.doubleToLongBits(other.CZdotY))
			return false;
		if (Double.doubleToLongBits(CZdotYdot) != Double.doubleToLongBits(other.CZdotYdot))
			return false;
		if (Double.doubleToLongBits(CZdotZ) != Double.doubleToLongBits(other.CZdotZ))
			return false;
		if (Double.doubleToLongBits(CZdotZdot) != Double.doubleToLongBits(other.CZdotZdot))
			return false;
		if (Double.doubleToLongBits(cXX) != Double.doubleToLongBits(other.cXX))
			return false;
		if (comments == null) {
			if (other.comments != null)
				return false;
		} else if (!comments.equals(other.comments))
			return false;
		if (covRefFrame != other.covRefFrame)
			return false;
		if (epoch == null) {
			if (other.epoch != null)
				return false;
		} else if (!epoch.equals(other.epoch))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CovarianceMatrix [comments=").append(comments).append(", epoch=").append(epoch)
				.append(", covRefFrame=").append(covRefFrame).append(", cXX=").append(cXX).append(", CYX=").append(CYX)
				.append(", CYY=").append(CYY).append(", CZX=").append(CZX).append(", CZY=").append(CZY).append(", CZZ=")
				.append(CZZ).append(", CXdotX=").append(CXdotX).append(", CXdotY=").append(CXdotY).append(", CXdotZ=")
				.append(CXdotZ).append(", CXdotXdot=").append(CXdotXdot).append(", CYdotX=").append(CYdotX).append(", CYdotY=")
				.append(CYdotY).append(", CYdotZ=").append(CYdotZ).append(", CYdotXdot=").append(CYdotXdot)
				.append(", CYdotYdot=").append(CYdotYdot).append(", CZdotX=").append(CZdotX).append(", CZdotY=").append(CZdotY)
				.append(", CZdotZ=").append(CZdotZ).append(", CZdotXdot=").append(CZdotXdot).append(", CZdotYdot=")
				.append(CZdotYdot).append(", CZdotZdot=").append(CZdotZdot).append("]");
		return builder.toString();
	}
}
