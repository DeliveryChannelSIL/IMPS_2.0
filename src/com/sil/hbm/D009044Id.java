package com.sil.hbm;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class D009044Id implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1736664794557392719L;
	private int lbrCode;
	private String issuedTo;
	private short insType;
	private String instruNo;

	public D009044Id() {
	}

	public D009044Id(int lbrCode, String issuedTo, short insType, String instruNo) {
		this.lbrCode = lbrCode;
		this.issuedTo = issuedTo;
		this.insType = insType;
		this.instruNo = instruNo;
	}

	@Column(name = "LBrCode", nullable = false)
	public int getLbrCode() {
		return this.lbrCode;
	}

	public void setLbrCode(int lbrCode) {
		this.lbrCode = lbrCode;
	}

	@Column(name = "IssuedTo", nullable = false, length = 32)
	public String getIssuedTo() {
		return this.issuedTo;
	}

	public void setIssuedTo(String issuedTo) {
		this.issuedTo = issuedTo;
	}

	@Column(name = "InsType", nullable = false)
	public short getInsType() {
		return this.insType;
	}

	public void setInsType(short insType) {
		this.insType = insType;
	}

	@Column(name = "InstruNo", nullable = false, length = 12)
	public String getInstruNo() {
		return this.instruNo;
	}

	public void setInstruNo(String instruNo) {
		this.instruNo = instruNo;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof D009044Id))
			return false;
		D009044Id castOther = (D009044Id) other;

		return (this.getLbrCode() == castOther.getLbrCode())
				&& ((this.getIssuedTo() == castOther.getIssuedTo()) || (this.getIssuedTo() != null
						&& castOther.getIssuedTo() != null && this.getIssuedTo().equals(castOther.getIssuedTo())))
				&& (this.getInsType() == castOther.getInsType())
				&& ((this.getInstruNo() == castOther.getInstruNo()) || (this.getInstruNo() != null
						&& castOther.getInstruNo() != null && this.getInstruNo().equals(castOther.getInstruNo())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getLbrCode();
		result = 37 * result + (getIssuedTo() == null ? 0 : this.getIssuedTo().hashCode());
		result = 37 * result + this.getInsType();
		result = 37 * result + (getInstruNo() == null ? 0 : this.getInstruNo().hashCode());
		return result;
	}

}
