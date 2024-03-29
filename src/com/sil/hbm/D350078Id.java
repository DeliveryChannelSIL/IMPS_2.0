package com.sil.hbm;

// default package
// Generated Sep 8, 2016 12:22:49 PM by Hibernate Tools 4.3.4.Final

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * D350078Id generated by hbm2java
 */
@Embeddable
public class D350078Id implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3616965634361192949L;
	private String custNo;
	private String mobileNo;

	public D350078Id() {
	}

	public D350078Id(String custNo, String mobileNo) {
		this.custNo = custNo;
		this.mobileNo = mobileNo;
	}

	@Column(name = "CustNo", nullable = false, length = 19)
	public String getCustNo() {
		return this.custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	@Column(name = "MobileNo", unique = true, nullable = false, length = 13)
	public String getMobileNo() {
		return this.mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof D350078Id))
			return false;
		D350078Id castOther = (D350078Id) other;

		return ((this.getCustNo() == castOther.getCustNo()) || (this.getCustNo() != null
				&& castOther.getCustNo() != null && this.getCustNo().equals(castOther.getCustNo())))
				&& ((this.getMobileNo() == castOther.getMobileNo()) || (this.getMobileNo() != null
						&& castOther.getMobileNo() != null && this.getMobileNo().equals(castOther.getMobileNo())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (getCustNo() == null ? 0 : this.getCustNo().hashCode());
		result = 37 * result + (getMobileNo() == null ? 0 : this.getMobileNo().hashCode());
		return result;
	}

}
