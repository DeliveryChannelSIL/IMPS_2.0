package com.sil.hbm;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * D010010Id generated by hbm2java
 */
@Embeddable
public class D010010Id implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4698578507751234414L;
	private int lbrCode;
	private String issuedTo;
	private short insType;
	private String insNo;

	public D010010Id() {
	}

	public D010010Id(int lbrCode, String issuedTo, short insType, String insNo) {
		this.lbrCode = lbrCode;
		this.issuedTo = issuedTo;
		this.insType = insType;
		this.insNo = insNo;
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

	@Column(name = "InsNo", nullable = false, length = 12)
	public String getInsNo() {
		return this.insNo;
	}

	public void setInsNo(String insNo) {
		this.insNo = insNo;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof D010010Id))
			return false;
		D010010Id castOther = (D010010Id) other;

		return (this.getLbrCode() == castOther.getLbrCode())
				&& ((this.getIssuedTo() == castOther.getIssuedTo()) || (this.getIssuedTo() != null
						&& castOther.getIssuedTo() != null && this.getIssuedTo().equals(castOther.getIssuedTo())))
				&& (this.getInsType() == castOther.getInsType())
				&& ((this.getInsNo() == castOther.getInsNo()) || (this.getInsNo() != null
						&& castOther.getInsNo() != null && this.getInsNo().equals(castOther.getInsNo())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getLbrCode();
		result = 37 * result + (getIssuedTo() == null ? 0 : this.getIssuedTo().hashCode());
		result = 37 * result + this.getInsType();
		result = 37 * result + (getInsNo() == null ? 0 : this.getInsNo().hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "D010010Id [lbrCode=" + lbrCode + ", issuedTo=" + issuedTo + ", insType=" + insType + ", insNo=" + insNo
				+ "]";
	}

}
