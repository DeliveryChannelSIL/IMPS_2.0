package com.sil.hbm;
// Generated 14 Sep, 2016 5:56:05 PM by Hibernate Tools 5.1.0.Beta1

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * D009021Id generated by hbm2java
 */
@Embeddable
public class D009021Id implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 527529790804847035L;
	private int lbrCode;
	private String prdCd;

	public D009021Id() {
	}

	public D009021Id(int lbrCode, String prdCd) {
		this.lbrCode = lbrCode;
		this.prdCd = prdCd;
	}

	@Column(name = "LBrCode", nullable = false)
	public int getLbrCode() {
		return this.lbrCode;
	}

	public void setLbrCode(int lbrCode) {
		this.lbrCode = lbrCode;
	}

	@Column(name = "PrdCd", nullable = false, length = 8)
	public String getPrdCd() {
		return this.prdCd;
	}

	public void setPrdCd(String prdCd) {
		this.prdCd = prdCd;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof D009021Id))
			return false;
		D009021Id castOther = (D009021Id) other;

		return (this.getLbrCode() == castOther.getLbrCode())
				&& ((this.getPrdCd() == castOther.getPrdCd()) || (this.getPrdCd() != null
						&& castOther.getPrdCd() != null && this.getPrdCd().equals(castOther.getPrdCd())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getLbrCode();
		result = 37 * result + (getPrdCd() == null ? 0 : this.getPrdCd().hashCode());
		return result;
	}

}
