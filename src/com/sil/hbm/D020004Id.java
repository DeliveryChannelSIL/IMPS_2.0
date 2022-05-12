package com.sil.hbm;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class D020004Id implements java.io.Serializable {

	private static final long serialVersionUID = 9046319737291043137L;
	private int lbrCode;
	private String prdAcctId;

	public D020004Id() {
	}

	public D020004Id(int lbrCode, String prdAcctId) {
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
		if (!(other instanceof D020004Id))
			return false;
		D020004Id castOther = (D020004Id) other;

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

}
