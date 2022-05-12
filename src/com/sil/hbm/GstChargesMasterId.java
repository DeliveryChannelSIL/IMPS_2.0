package com.sil.hbm;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * D130231Id entity.
 * 
 * @author MyEclipse Persistence Tools
 */
@Embeddable
public class GstChargesMasterId implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long chgtype;
	private Date effdate;

	// Constructors

	/** default constructor */
	public GstChargesMasterId() {
	}

	/** full constructor */
	public GstChargesMasterId(Long chgtype, Date effdate) {
		this.chgtype = chgtype;
		this.effdate = effdate;
	}

	// Property accessors

	@Column(name = "ChgType", nullable = false, precision = 2, scale = 0)
	public Long getChgtype() {
		return this.chgtype;
	}

	public void setChgtype(Long chgtype) {
		this.chgtype = chgtype;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "EffDate", nullable = false, length = 7)
	public Date getEffdate() {
		return this.effdate;
	}

	public void setEffdate(Date effdate) {
		this.effdate = effdate;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof GstChargesMasterId))
			return false;
		GstChargesMasterId castOther = (GstChargesMasterId) other;

		return ((this.getChgtype() == castOther.getChgtype()) || (this
				.getChgtype() != null
				&& castOther.getChgtype() != null && this.getChgtype().equals(
				castOther.getChgtype())))
				&& ((this.getEffdate() == castOther.getEffdate()) || (this
						.getEffdate() != null
						&& castOther.getEffdate() != null && this.getEffdate()
						.equals(castOther.getEffdate())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getChgtype() == null ? 0 : this.getChgtype().hashCode());
		result = 37 * result
				+ (getEffdate() == null ? 0 : this.getEffdate().hashCode());
		return result;
	}

}