package com.sil.hbm;
// Generated 14 Sep, 2016 5:56:05 PM by Hibernate Tools 5.1.0.Beta1

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * D100001Id generated by hbm2java
 */
@Embeddable
public class D100001Id implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5922721887892288162L;
	private int lbrCode;
	private int reconNo;

	public D100001Id() {
	}

	public D100001Id(int lbrCode, int reconNo) {
		this.lbrCode = lbrCode;
		this.reconNo = reconNo;
	}

	@Column(name = "LBrCode", nullable = false)
	public int getLbrCode() {
		return this.lbrCode;
	}

	public void setLbrCode(int lbrCode) {
		this.lbrCode = lbrCode;
	}

	@Column(name = "ReconNo", nullable = false)
	public int getReconNo() {
		return this.reconNo;
	}

	public void setReconNo(int reconNo) {
		this.reconNo = reconNo;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof D100001Id))
			return false;
		D100001Id castOther = (D100001Id) other;

		return (this.getLbrCode() == castOther.getLbrCode()) && (this.getReconNo() == castOther.getReconNo());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getLbrCode();
		result = 37 * result + this.getReconNo();
		return result;
	}

}
