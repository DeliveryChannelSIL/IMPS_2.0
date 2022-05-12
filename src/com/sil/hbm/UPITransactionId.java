package com.sil.hbm;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the D350110 database table.
 * 
 */
@Embeddable
public class UPITransactionId implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="LbrCode")
	private int lbrCode;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EntryDate")
	private java.util.Date entryDate;

	@Column(name="BatchCd")
	private String batchCd;

	@Column(name="SetNo")
	private int setNo;

	@Column(name="ScrollNo")
	private int scrollNo;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EntryTime")
	private java.util.Date entryTime;

	
	public UPITransactionId() {
	}
	public int getLbrCode() {
		return this.lbrCode;
	}
	public void setLbrCode(int lbrCode) {
		this.lbrCode = lbrCode;
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
	public java.util.Date getEntryTime() {
		return this.entryTime;
	}
	public void setEntryTime(java.util.Date entryTime) {
		this.entryTime = entryTime;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof UPITransactionId)) {
			return false;
		}
		UPITransactionId castOther = (UPITransactionId)other;
		return 
			(this.lbrCode == castOther.lbrCode)
			&& this.entryDate.equals(castOther.entryDate)
			&& this.batchCd.equals(castOther.batchCd)
			&& (this.setNo == castOther.setNo)
			&& (this.scrollNo == castOther.scrollNo)
			&& this.entryTime.equals(castOther.entryTime);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.lbrCode;
		hash = hash * prime + this.entryDate.hashCode();
		hash = hash * prime + this.batchCd.hashCode();
		hash = hash * prime + this.setNo;
		hash = hash * prime + this.scrollNo;
		hash = hash * prime + this.entryTime.hashCode();
		
		return hash;
	}
}