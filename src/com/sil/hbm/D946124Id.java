package com.sil.hbm;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the D946124 database table.
 * 
 */
@Embeddable
public class D946124Id implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private int LBrCode;

	@Column(name="PrdCd")
	private String prdCd;

	@Column(name="MsgSType")
	private String msgSType;

	@Column(name="IwOwMsg")
	private String iwOwMsg;

	@Column(name="ExpType")
	private short expType;

	@Column(name="AcctType")
	private short acctType;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EffDate")
	private java.util.Date effDate;

	@Column(name="SrNo")
	private short srNo;

	public D946124Id() {
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
	public String getMsgSType() {
		return this.msgSType;
	}
	public void setMsgSType(String msgSType) {
		this.msgSType = msgSType;
	}
	public String getIwOwMsg() {
		return this.iwOwMsg;
	}
	public void setIwOwMsg(String iwOwMsg) {
		this.iwOwMsg = iwOwMsg;
	}
	public short getExpType() {
		return this.expType;
	}
	public void setExpType(short expType) {
		this.expType = expType;
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
	public short getSrNo() {
		return this.srNo;
	}
	public void setSrNo(short srNo) {
		this.srNo = srNo;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof D946124Id)) {
			return false;
		}
		D946124Id castOther = (D946124Id)other;
		return 
			(this.LBrCode == castOther.LBrCode)
			&& this.prdCd.equals(castOther.prdCd)
			&& this.msgSType.equals(castOther.msgSType)
			&& this.iwOwMsg.equals(castOther.iwOwMsg)
			&& (this.expType == castOther.expType)
			&& (this.acctType == castOther.acctType)
			&& this.effDate.equals(castOther.effDate)
			&& (this.srNo == castOther.srNo);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.LBrCode;
		hash = hash * prime + this.prdCd.hashCode();
		hash = hash * prime + this.msgSType.hashCode();
		hash = hash * prime + this.iwOwMsg.hashCode();
		hash = hash * prime + ((int) this.expType);
		hash = hash * prime + ((int) this.acctType);
		hash = hash * prime + this.effDate.hashCode();
		hash = hash * prime + ((int) this.srNo);
		
		return hash;
	}
}