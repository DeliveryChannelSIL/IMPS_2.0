package com.sil.hbm;

// default package
// Generated Sep 2, 2016 12:13:59 PM by Hibernate Tools 4.3.4.Final

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * D009022Id generated by hbm2java
 */
@Embeddable
public class D009022Id implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8384908424381194673L;
	private int lbrCode;
	private String prdAcctId;

	public D009022Id() {
	}

	public D009022Id(int lbrCode, String prdAcctId) {
		this.lbrCode = lbrCode;
		this.prdAcctId = prdAcctId;
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

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof D009022Id))
			return false;
		D009022Id castOther = (D009022Id) other;

		return (this.getLbrCode() == castOther.getLbrCode())
				&& ((this.getPrdAcctId() == castOther.getPrdAcctId()) || (this.getPrdAcctId() != null
						&& castOther.getPrdAcctId() != null && this.getPrdAcctId().equals(castOther.getPrdAcctId())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getLbrCode();
		result = 37 * result + (getPrdAcctId() == null ? 0 : this.getPrdAcctId().hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "D009022Id [lbrCode=" + lbrCode + ", prdAcctId=" + prdAcctId + "]";
	}

	
	
}
