package com.sil.hbm;
// Generated Jan 24, 2017 11:07:08 AM by Hibernate Tools 5.2.0.Beta1

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * D350032Id generated by hbm2java
 */
@Embeddable
public class D350032Id implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -590249978037495802L;
	private String custNo;
	private String mobileNo;
	private String mmid;

	public D350032Id() {
	}

	public D350032Id(String custNo, String mobileNo, String mmid) {
		this.custNo = custNo;
		this.mobileNo = mobileNo;
		this.mmid = mmid;
	}

	@Column(name = "CustNo", nullable = false, length = 19)
	public String getCustNo() {
		return this.custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	@Column(name = "MobileNo", nullable = false, length = 12)
	public String getMobileNo() {
		return this.mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	@Column(name = "MMID", nullable = false, length = 7)
	public String getMmid() {
		return this.mmid;
	}

	public void setMmid(String mmid) {
		this.mmid = mmid;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof D350032Id))
			return false;
		D350032Id castOther = (D350032Id) other;

		return ((this.getCustNo() == castOther.getCustNo()) || (this.getCustNo() != null
				&& castOther.getCustNo() != null && this.getCustNo().equals(castOther.getCustNo())))
				&& ((this.getMobileNo() == castOther.getMobileNo()) || (this.getMobileNo() != null
						&& castOther.getMobileNo() != null && this.getMobileNo().equals(castOther.getMobileNo())))
				&& ((this.getMmid() == castOther.getMmid()) || (this.getMmid() != null && castOther.getMmid() != null
						&& this.getMmid().equals(castOther.getMmid())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (getCustNo() == null ? 0 : this.getCustNo().hashCode());
		result = 37 * result + (getMobileNo() == null ? 0 : this.getMobileNo().hashCode());
		result = 37 * result + (getMmid() == null ? 0 : this.getMmid().hashCode());
		return result;
	}

}
