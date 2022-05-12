package com.sil.hbm;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the D009027 database table.
 * 
 */
@Embeddable
public class OffsetIntTDId implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private int LBrCode;

	@Column(name="PrdCd")
	private String prdCd;

	@Column(name="AcctType")
	private short acctType;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EffDate")
	private java.util.Date effDate;

	public OffsetIntTDId() {
	}
	public int getLBrCode() {
		return this.LBrCode;
	}
	public void setLBrCode(int LBrCode) {
		this.LBrCode = LBrCode;
	}
	public String getPrdCd() {
		return this.prdCd;
	}
	public void setPrdCd(String prdCd) {
		this.prdCd = prdCd;
	}
	public short getAcctType() {
		return this.acctType;
	}
	public void setAcctType(short acctType) {
		this.acctType = acctType;
	}
	public java.util.Date getEffDate() {
		return this.effDate;
	}
	public void setEffDate(java.util.Date effDate) {
		this.effDate = effDate;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof OffsetIntTDId)) {
			return false;
		}
		OffsetIntTDId castOther = (OffsetIntTDId)other;
		return 
			(this.LBrCode == castOther.LBrCode)
			&& this.prdCd.equals(castOther.prdCd)
			&& (this.acctType == castOther.acctType)
			&& this.effDate.equals(castOther.effDate);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.LBrCode;
		hash = hash * prime + this.prdCd.hashCode();
		hash = hash * prime + ((int) this.acctType);
		hash = hash * prime + this.effDate.hashCode();
		
		return hash;
	}
}