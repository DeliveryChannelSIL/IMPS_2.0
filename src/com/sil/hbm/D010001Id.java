package com.sil.hbm;
// Generated 16 Sep, 2016 12:40:53 PM by Hibernate Tools 5.1.0.Beta1

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * D010001Id generated by hbm2java
 */
@Embeddable
public class D010001Id implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6304451513508674539L;
	private int lbrCode;
	private String code;

	public D010001Id() {
	}

	public D010001Id(int lbrCode, String code) {
		this.lbrCode = lbrCode;
		this.code = code;
	}

	@Column(name = "LBrCode", nullable = false)
	public int getLbrCode() {
		return this.lbrCode;
	}

	public void setLbrCode(int lbrCode) {
		this.lbrCode = lbrCode;
	}

	@Column(name = "Code", nullable = false, length = 8)
	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof D010001Id))
			return false;
		D010001Id castOther = (D010001Id) other;

		return (this.getLbrCode() == castOther.getLbrCode())
				&& ((this.getCode() == castOther.getCode()) || (this.getCode() != null && castOther.getCode() != null
						&& this.getCode().equals(castOther.getCode())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getLbrCode();
		result = 37 * result + (getCode() == null ? 0 : this.getCode().hashCode());
		return result;
	}

}
