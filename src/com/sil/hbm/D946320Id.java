package com.sil.hbm;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

/**
 * The primary key class for the D946320 database table.
 * 
 */
@Embeddable
public class D946320Id implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private int OBrCode;

	@Column(name="IwOwMsg")
	private String iwOwMsg;

	@Column(name="MsgSType")
	private String msgSType;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="MsgDate")
	private java.util.Date msgDate;

	@Column(name="RefNo")
	private String refNo;

	public D946320Id() {
	}
	public int getOBrCode() {
		return this.OBrCode;
	}
	public void setOBrCode(int OBrCode) {
		this.OBrCode = OBrCode;
	}
	public String getIwOwMsg() {
		return this.iwOwMsg;
	}
	public void setIwOwMsg(String iwOwMsg) {
		this.iwOwMsg = iwOwMsg;
	}
	public String getMsgSType() {
		return this.msgSType;
	}
	public void setMsgSType(String msgSType) {
		this.msgSType = msgSType;
	}
	public java.util.Date getMsgDate() {
		return this.msgDate;
	}
	public void setMsgDate(java.util.Date msgDate) {
		this.msgDate = msgDate;
	}
	public String getRefNo() {
		return this.refNo;
	}
	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof D946320Id)) {
			return false;
		}
		D946320Id castOther = (D946320Id)other;
		return 
			(this.OBrCode == castOther.OBrCode)
			&& this.iwOwMsg.equals(castOther.iwOwMsg)
			&& this.msgSType.equals(castOther.msgSType)
			&& this.msgDate.equals(castOther.msgDate)
			&& this.refNo.equals(castOther.refNo);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.OBrCode;
		hash = hash * prime + this.iwOwMsg.hashCode();
		hash = hash * prime + this.msgSType.hashCode();
		hash = hash * prime + this.msgDate.hashCode();
		hash = hash * prime + this.refNo.hashCode();
		
		return hash;
	}
	
	
	
	
	public D946320Id(int oBrCode, String iwOwMsg, String msgSType, Date msgDate, String refNo) {
		super();
		OBrCode = oBrCode;
		this.iwOwMsg = iwOwMsg;
		this.msgSType = msgSType;
		this.msgDate = msgDate;
		this.refNo = refNo;
	}
	@Override
	public String toString() {
		return "D946320Id [OBrCode=" + OBrCode + ", iwOwMsg=" + iwOwMsg + ", msgSType=" + msgSType + ", msgDate="
				+ msgDate + ", refNo=" + refNo + "]";
	}
	
	
}