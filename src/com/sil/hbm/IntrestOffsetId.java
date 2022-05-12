package com.sil.hbm;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * D001118Id entity.
 * 
 * @author MyEclipse Persistence Tools
 */
@SuppressWarnings("all")
@Embeddable
public class IntrestOffsetId implements java.io.Serializable {

	// Fields

	private Long lbrcode;
	private String prdacctid;
	private Date efffromdate;
	private Long srno;

	// Constructors

	/** default constructor */
	public IntrestOffsetId() {
	}

	/** full constructor */
	public IntrestOffsetId(Long lbrcode, String prdacctid, Date efffromdate, Long srno) {
		this.lbrcode = lbrcode;
		this.prdacctid = prdacctid;
		this.efffromdate = efffromdate;
		this.srno = srno;
	}

	// Property accessors

	@Column(name = "LBrCode", nullable = false, precision = 6, scale = 0)
	public Long getLbrcode() {
		return this.lbrcode;
	}

	public void setLbrcode(Long lbrcode) {
		this.lbrcode = lbrcode;
	}

	@Column(name = "PrdAcctId", nullable = false, length = 32)
	public String getPrdacctid() {
		return this.prdacctid;
	}

	public void setPrdacctid(String prdacctid) {
		this.prdacctid = prdacctid;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "EffFromDate", nullable = false, length = 7)
	public Date getEfffromdate() {
		return this.efffromdate;
	}

	public void setEfffromdate(Date efffromdate) {
		this.efffromdate = efffromdate;
	}

	@Column(name = "SrNo", nullable = false, precision = 4, scale = 0)
	public Long getSrno() {
		return this.srno;
	}

	public void setSrno(Long srno) {
		this.srno = srno;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof IntrestOffsetId))
			return false;
		IntrestOffsetId castOther = (IntrestOffsetId) other;

		return ((this.getLbrcode() == castOther.getLbrcode()) || (this
				.getLbrcode() != null
				&& castOther.getLbrcode() != null && this.getLbrcode().equals(
				castOther.getLbrcode())))
				&& ((this.getPrdacctid() == castOther.getPrdacctid()) || (this
						.getPrdacctid() != null
						&& castOther.getPrdacctid() != null && this
						.getPrdacctid().equals(castOther.getPrdacctid())))
				&& ((this.getEfffromdate() == castOther.getEfffromdate()) || (this
						.getEfffromdate() != null
						&& castOther.getEfffromdate() != null && this
						.getEfffromdate().equals(castOther.getEfffromdate())))
				&& ((this.getSrno() == castOther.getSrno()) || (this.getSrno() != null
						&& castOther.getSrno() != null && this.getSrno()
						.equals(castOther.getSrno())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getLbrcode() == null ? 0 : this.getLbrcode().hashCode());
		result = 37 * result
				+ (getPrdacctid() == null ? 0 : this.getPrdacctid().hashCode());
		result = 37
				* result
				+ (getEfffromdate() == null ? 0 : this.getEfffromdate()
						.hashCode());
		result = 37 * result
				+ (getSrno() == null ? 0 : this.getSrno().hashCode());
		return result;
	}

}