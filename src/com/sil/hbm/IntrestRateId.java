package com.sil.hbm;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * D001116Id entity.
 * 
 * @author MyEclipse Persistence Tools
 */
@SuppressWarnings("all")
@Embeddable
public class IntrestRateId implements java.io.Serializable {

	// Fields

	private String prdcd;
	private String curcd;
	private Date inteffdt;
	private Long srno;

	// Constructors

	/** default constructor */
	public IntrestRateId() {
	}

	/** full constructor */
	public IntrestRateId(String prdcd, String curcd, Date inteffdt, Long srno) {
		this.prdcd = prdcd;
		this.curcd = curcd;
		this.inteffdt = inteffdt;
		this.srno = srno;
	}

	// Property accessors

	@Column(name = "PrdCd", nullable = false, length = 8)
	public String getPrdcd() {
		return this.prdcd;
	}

	public void setPrdcd(String prdcd) {
		this.prdcd = prdcd;
	}

	@Column(name = "CurCd", nullable = false, length = 3)
	public String getCurcd() {
		return this.curcd;
	}

	public void setCurcd(String curcd) {
		this.curcd = curcd;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "IntEffDt", nullable = false, length = 7)
	public Date getInteffdt() {
		return this.inteffdt;
	}

	public void setInteffdt(Date inteffdt) {
		this.inteffdt = inteffdt;
	}

	@Column(name = "SrNo", nullable = false, precision = 2, scale = 0)
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
		if (!(other instanceof IntrestRateId))
			return false;
		IntrestRateId castOther = (IntrestRateId) other;

		return ((this.getPrdcd() == castOther.getPrdcd()) || (this.getPrdcd() != null
				&& castOther.getPrdcd() != null && this.getPrdcd().equals(
				castOther.getPrdcd())))
				&& ((this.getCurcd() == castOther.getCurcd()) || (this
						.getCurcd() != null
						&& castOther.getCurcd() != null && this.getCurcd()
						.equals(castOther.getCurcd())))
				&& ((this.getInteffdt() == castOther.getInteffdt()) || (this
						.getInteffdt() != null
						&& castOther.getInteffdt() != null && this
						.getInteffdt().equals(castOther.getInteffdt())))
				&& ((this.getSrno() == castOther.getSrno()) || (this.getSrno() != null
						&& castOther.getSrno() != null && this.getSrno()
						.equals(castOther.getSrno())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getPrdcd() == null ? 0 : this.getPrdcd().hashCode());
		result = 37 * result
				+ (getCurcd() == null ? 0 : this.getCurcd().hashCode());
		result = 37 * result
				+ (getInteffdt() == null ? 0 : this.getInteffdt().hashCode());
		result = 37 * result
				+ (getSrno() == null ? 0 : this.getSrno().hashCode());
		return result;
	}

}