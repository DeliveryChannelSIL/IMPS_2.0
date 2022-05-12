package com.sil.hbm;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the D020020 database table.
 * 
 */
@Embeddable
public class TDSMaintananceForTDId implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private int LBrCode;

	@Column(name="PrdAcctId")
	private String prdAcctId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EntryDate")
	private java.util.Date entryDate;

	@Column(name="BatchCd")
	private String batchCd;

	@Column(name="SetNo")
	private int setNo;

	@Column(name="ScrollNo")
	private int scrollNo;

	public TDSMaintananceForTDId() {
	}
	public int getLBrCode() {
		return this.LBrCode;
	}
	public void setLBrCode(int LBrCode) {
		this.LBrCode = LBrCode;
	}
	public String getPrdAcctId() {
		return this.prdAcctId;
	}
	public void setPrdAcctId(String prdAcctId) {
		this.prdAcctId = prdAcctId;
	}
	public java.util.Date getEntryDate() {
		return this.entryDate;
	}
	public void setEntryDate(java.util.Date entryDate) {
		this.entryDate = entryDate;
	}
	public String getBatchCd() {
		return this.batchCd;
	}
	public void setBatchCd(String batchCd) {
		this.batchCd = batchCd;
	}
	public int getSetNo() {
		return this.setNo;
	}
	public void setSetNo(int setNo) {
		this.setNo = setNo;
	}
	public int getScrollNo() {
		return this.scrollNo;
	}
	public void setScrollNo(int scrollNo) {
		this.scrollNo = scrollNo;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof TDSMaintananceForTDId)) {
			return false;
		}
		TDSMaintananceForTDId castOther = (TDSMaintananceForTDId)other;
		return 
			(this.LBrCode == castOther.LBrCode)
			&& this.prdAcctId.equals(castOther.prdAcctId)
			&& this.entryDate.equals(castOther.entryDate)
			&& this.batchCd.equals(castOther.batchCd)
			&& (this.setNo == castOther.setNo)
			&& (this.scrollNo == castOther.scrollNo);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.LBrCode;
		hash = hash * prime + this.prdAcctId.hashCode();
		hash = hash * prime + this.entryDate.hashCode();
		hash = hash * prime + this.batchCd.hashCode();
		hash = hash * prime + this.setNo;
		hash = hash * prime + this.scrollNo;
		
		return hash;
	}
}