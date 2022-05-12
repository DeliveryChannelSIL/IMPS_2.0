package com.sil.hbm;
// Generated 14 Mar, 2017 6:08:16 PM by Hibernate Tools 5.1.0.Beta1

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * D010014Id generated by hbm2java
 */
@Embeddable
public class D010014Id implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3839674218705714647L;
	private int lbrCode;
	private String prdAcctId;
	private Date cblDate;

	public D010014Id() {
	}

	public D010014Id(int lbrCode, String prdAcctId, Date cblDate) {
		this.lbrCode = lbrCode;
		this.prdAcctId = prdAcctId;
		this.cblDate = cblDate;
	}

	@Column(name = "LBrCode", nullable = false)
	public int getLbrCode() {
		return this.lbrCode;
	}

	public void setLbrCode(int lbrCode) {
		this.lbrCode = lbrCode;
	}

	@Column(name = "PrdAcctId", nullable = false, length = 32)
	public String getPrdAcctId() {
		return this.prdAcctId;
	}

	public void setPrdAcctId(String prdAcctId) {
		this.prdAcctId = prdAcctId;
	}

	@Column(name = "CblDate", nullable = false, length = 23)
	public Date getCblDate() {
		return this.cblDate;
	}

	public void setCblDate(Date cblDate) {
		this.cblDate = cblDate;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof D010014Id))
			return false;
		D010014Id castOther = (D010014Id) other;

		return (this.getLbrCode() == castOther.getLbrCode())
				&& ((this.getPrdAcctId() == castOther.getPrdAcctId()) || (this.getPrdAcctId() != null
						&& castOther.getPrdAcctId() != null && this.getPrdAcctId().equals(castOther.getPrdAcctId())))
				&& ((this.getCblDate() == castOther.getCblDate()) || (this.getCblDate() != null
						&& castOther.getCblDate() != null && this.getCblDate().equals(castOther.getCblDate())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getLbrCode();
		result = 37 * result + (getPrdAcctId() == null ? 0 : this.getPrdAcctId().hashCode());
		result = 37 * result + (getCblDate() == null ? 0 : this.getCblDate().hashCode());
		return result;
	}

}
