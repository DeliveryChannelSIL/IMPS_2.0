package com.sil.hbm;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the D946220 database table.
 * 
 */
@Embeddable
public class RtgsMessageSecondaryDatFileId implements Serializable {
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

	public RtgsMessageSecondaryDatFileId() {
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
		if (!(other instanceof RtgsMessageSecondaryDatFileId)) {
			return false;
		}
		RtgsMessageSecondaryDatFileId castOther = (RtgsMessageSecondaryDatFileId)other;
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
}