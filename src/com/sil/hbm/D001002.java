package com.sil.hbm;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * D001002 generated by hbm2java
 */
@Entity
@Table(name = "D001002")
@DynamicUpdate
@DynamicInsert
public class D001002 implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4026143190944053281L;
	/**
	 * 
	 */
	
	private D001002Id id;
	private String codeDesc;
	private String fld1value;
	private char secYn;
	private int dbtrAddMk;
	private int dbtrAddMb;
	private short dbtrAddMs;
	private Date dbtrAddMd;
	private Date dbtrAddMt;
	private int dbtrAddCk;
	private int dbtrAddCb;
	private short dbtrAddCs;
	private Date dbtrAddCd;
	private Date dbtrAddCt;
	private int dbtrLupdMk;
	private int dbtrLupdMb;
	private short dbtrLupdMs;
	private Date dbtrLupdMd;
	private Date dbtrLupdMt;
	private int dbtrLupdCk;
	private int dbtrLupdCb;
	private short dbtrLupdCs;
	private Date dbtrLupdCd;
	private Date dbtrLupdCt;
	private short dbtrTauthDone;
	private byte dbtrRecStat;
	private byte dbtrAuthDone;
	private byte dbtrAuthNeeded;
	private short dbtrUpdtChkId;
	private int dbtrLhisTrnNo;

	public D001002() {
	}

	public D001002(D001002Id id, String codeDesc, String fld1value, char secYn, int dbtrAddMk, int dbtrAddMb,
			short dbtrAddMs, Date dbtrAddMd, Date dbtrAddMt, int dbtrAddCk, int dbtrAddCb, short dbtrAddCs,
			Date dbtrAddCd, Date dbtrAddCt, int dbtrLupdMk, int dbtrLupdMb, short dbtrLupdMs, Date dbtrLupdMd,
			Date dbtrLupdMt, int dbtrLupdCk, int dbtrLupdCb, short dbtrLupdCs, Date dbtrLupdCd, Date dbtrLupdCt,
			short dbtrTauthDone, byte dbtrRecStat, byte dbtrAuthDone, byte dbtrAuthNeeded, short dbtrUpdtChkId,
			int dbtrLhisTrnNo) {
		this.id = id;
		this.codeDesc = codeDesc;
		this.fld1value = fld1value;
		this.secYn = secYn;
		this.dbtrAddMk = dbtrAddMk;
		this.dbtrAddMb = dbtrAddMb;
		this.dbtrAddMs = dbtrAddMs;
		this.dbtrAddMd = dbtrAddMd;
		this.dbtrAddMt = dbtrAddMt;
		this.dbtrAddCk = dbtrAddCk;
		this.dbtrAddCb = dbtrAddCb;
		this.dbtrAddCs = dbtrAddCs;
		this.dbtrAddCd = dbtrAddCd;
		this.dbtrAddCt = dbtrAddCt;
		this.dbtrLupdMk = dbtrLupdMk;
		this.dbtrLupdMb = dbtrLupdMb;
		this.dbtrLupdMs = dbtrLupdMs;
		this.dbtrLupdMd = dbtrLupdMd;
		this.dbtrLupdMt = dbtrLupdMt;
		this.dbtrLupdCk = dbtrLupdCk;
		this.dbtrLupdCb = dbtrLupdCb;
		this.dbtrLupdCs = dbtrLupdCs;
		this.dbtrLupdCd = dbtrLupdCd;
		this.dbtrLupdCt = dbtrLupdCt;
		this.dbtrTauthDone = dbtrTauthDone;
		this.dbtrRecStat = dbtrRecStat;
		this.dbtrAuthDone = dbtrAuthDone;
		this.dbtrAuthNeeded = dbtrAuthNeeded;
		this.dbtrUpdtChkId = dbtrUpdtChkId;
		this.dbtrLhisTrnNo = dbtrLhisTrnNo;
	}

	@EmbeddedId

	@AttributeOverrides({ @AttributeOverride(name = "codeType", column = @Column(name = "CodeType", nullable = false)),
			@AttributeOverride(name = "code", column = @Column(name = "Code", nullable = false, length = 30)) })
	public D001002Id getId() {
		return this.id;
	}

	public void setId(D001002Id id) {
		this.id = id;
	}

	@Column(name = "CodeDesc", nullable = false, length = 35)
	public String getCodeDesc() {
		return this.codeDesc;
	}

	public void setCodeDesc(String codeDesc) {
		this.codeDesc = codeDesc;
	}

	@Column(name = "Fld1Value", nullable = false, length = 30)
	public String getFld1value() {
		return this.fld1value;
	}

	public void setFld1value(String fld1value) {
		this.fld1value = fld1value;
	}

	@Column(name = "SecYN", nullable = false, length = 1)
	public char getSecYn() {
		return this.secYn;
	}

	public void setSecYn(char secYn) {
		this.secYn = secYn;
	}

	@Column(name = "DbtrAddMk", nullable = false)
	public int getDbtrAddMk() {
		return this.dbtrAddMk;
	}

	public void setDbtrAddMk(int dbtrAddMk) {
		this.dbtrAddMk = dbtrAddMk;
	}

	@Column(name = "DbtrAddMb", nullable = false)
	public int getDbtrAddMb() {
		return this.dbtrAddMb;
	}

	public void setDbtrAddMb(int dbtrAddMb) {
		this.dbtrAddMb = dbtrAddMb;
	}

	@Column(name = "DbtrAddMs", nullable = false)
	public short getDbtrAddMs() {
		return this.dbtrAddMs;
	}

	public void setDbtrAddMs(short dbtrAddMs) {
		this.dbtrAddMs = dbtrAddMs;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DbtrAddMd", nullable = false, length = 23)
	public Date getDbtrAddMd() {
		return this.dbtrAddMd;
	}

	public void setDbtrAddMd(Date dbtrAddMd) {
		this.dbtrAddMd = dbtrAddMd;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DbtrAddMt", nullable = false, length = 23)
	public Date getDbtrAddMt() {
		return this.dbtrAddMt;
	}

	public void setDbtrAddMt(Date dbtrAddMt) {
		this.dbtrAddMt = dbtrAddMt;
	}

	@Column(name = "DbtrAddCk", nullable = false)
	public int getDbtrAddCk() {
		return this.dbtrAddCk;
	}

	public void setDbtrAddCk(int dbtrAddCk) {
		this.dbtrAddCk = dbtrAddCk;
	}

	@Column(name = "DbtrAddCb", nullable = false)
	public int getDbtrAddCb() {
		return this.dbtrAddCb;
	}

	public void setDbtrAddCb(int dbtrAddCb) {
		this.dbtrAddCb = dbtrAddCb;
	}

	@Column(name = "DbtrAddCs", nullable = false)
	public short getDbtrAddCs() {
		return this.dbtrAddCs;
	}

	public void setDbtrAddCs(short dbtrAddCs) {
		this.dbtrAddCs = dbtrAddCs;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DbtrAddCd", nullable = false, length = 23)
	public Date getDbtrAddCd() {
		return this.dbtrAddCd;
	}

	public void setDbtrAddCd(Date dbtrAddCd) {
		this.dbtrAddCd = dbtrAddCd;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DbtrAddCt", nullable = false, length = 23)
	public Date getDbtrAddCt() {
		return this.dbtrAddCt;
	}

	public void setDbtrAddCt(Date dbtrAddCt) {
		this.dbtrAddCt = dbtrAddCt;
	}

	@Column(name = "DbtrLupdMk", nullable = false)
	public int getDbtrLupdMk() {
		return this.dbtrLupdMk;
	}

	public void setDbtrLupdMk(int dbtrLupdMk) {
		this.dbtrLupdMk = dbtrLupdMk;
	}

	@Column(name = "DbtrLupdMb", nullable = false)
	public int getDbtrLupdMb() {
		return this.dbtrLupdMb;
	}

	public void setDbtrLupdMb(int dbtrLupdMb) {
		this.dbtrLupdMb = dbtrLupdMb;
	}

	@Column(name = "DbtrLupdMs", nullable = false)
	public short getDbtrLupdMs() {
		return this.dbtrLupdMs;
	}

	public void setDbtrLupdMs(short dbtrLupdMs) {
		this.dbtrLupdMs = dbtrLupdMs;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DbtrLupdMd", nullable = false, length = 23)
	public Date getDbtrLupdMd() {
		return this.dbtrLupdMd;
	}

	public void setDbtrLupdMd(Date dbtrLupdMd) {
		this.dbtrLupdMd = dbtrLupdMd;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DbtrLupdMt", nullable = false, length = 23)
	public Date getDbtrLupdMt() {
		return this.dbtrLupdMt;
	}

	public void setDbtrLupdMt(Date dbtrLupdMt) {
		this.dbtrLupdMt = dbtrLupdMt;
	}

	@Column(name = "DbtrLupdCk", nullable = false)
	public int getDbtrLupdCk() {
		return this.dbtrLupdCk;
	}

	public void setDbtrLupdCk(int dbtrLupdCk) {
		this.dbtrLupdCk = dbtrLupdCk;
	}

	@Column(name = "DbtrLupdCb", nullable = false)
	public int getDbtrLupdCb() {
		return this.dbtrLupdCb;
	}

	public void setDbtrLupdCb(int dbtrLupdCb) {
		this.dbtrLupdCb = dbtrLupdCb;
	}

	@Column(name = "DbtrLupdCs", nullable = false)
	public short getDbtrLupdCs() {
		return this.dbtrLupdCs;
	}

	public void setDbtrLupdCs(short dbtrLupdCs) {
		this.dbtrLupdCs = dbtrLupdCs;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DbtrLupdCd", nullable = false, length = 23)
	public Date getDbtrLupdCd() {
		return this.dbtrLupdCd;
	}

	public void setDbtrLupdCd(Date dbtrLupdCd) {
		this.dbtrLupdCd = dbtrLupdCd;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DbtrLupdCt", nullable = false, length = 23)
	public Date getDbtrLupdCt() {
		return this.dbtrLupdCt;
	}

	public void setDbtrLupdCt(Date dbtrLupdCt) {
		this.dbtrLupdCt = dbtrLupdCt;
	}

	@Column(name = "DbtrTAuthDone", nullable = false)
	public short getDbtrTauthDone() {
		return this.dbtrTauthDone;
	}

	public void setDbtrTauthDone(short dbtrTauthDone) {
		this.dbtrTauthDone = dbtrTauthDone;
	}

	@Column(name = "DbtrRecStat", nullable = false)
	public byte getDbtrRecStat() {
		return this.dbtrRecStat;
	}

	public void setDbtrRecStat(byte dbtrRecStat) {
		this.dbtrRecStat = dbtrRecStat;
	}

	@Column(name = "DbtrAuthDone", nullable = false)
	public byte getDbtrAuthDone() {
		return this.dbtrAuthDone;
	}

	public void setDbtrAuthDone(byte dbtrAuthDone) {
		this.dbtrAuthDone = dbtrAuthDone;
	}

	@Column(name = "DbtrAuthNeeded", nullable = false)
	public byte getDbtrAuthNeeded() {
		return this.dbtrAuthNeeded;
	}

	public void setDbtrAuthNeeded(byte dbtrAuthNeeded) {
		this.dbtrAuthNeeded = dbtrAuthNeeded;
	}

	@Column(name = "DbtrUpdtChkId", nullable = false)
	public short getDbtrUpdtChkId() {
		return this.dbtrUpdtChkId;
	}

	public void setDbtrUpdtChkId(short dbtrUpdtChkId) {
		this.dbtrUpdtChkId = dbtrUpdtChkId;
	}

	@Column(name = "DbtrLHisTrnNo", nullable = false)
	public int getDbtrLhisTrnNo() {
		return this.dbtrLhisTrnNo;
	}

	public void setDbtrLhisTrnNo(int dbtrLhisTrnNo) {
		this.dbtrLhisTrnNo = dbtrLhisTrnNo;
	}

	@Override
	public String toString() {
		return "D001002 [id=" + id + ", codeDesc=" + codeDesc + ", fld1value=" + fld1value + ", secYn=" + secYn
				+ ", dbtrAddMk=" + dbtrAddMk + ", dbtrAddMb=" + dbtrAddMb + ", dbtrAddMs=" + dbtrAddMs + ", dbtrAddMd="
				+ dbtrAddMd + ", dbtrAddMt=" + dbtrAddMt + ", dbtrAddCk=" + dbtrAddCk + ", dbtrAddCb=" + dbtrAddCb
				+ ", dbtrAddCs=" + dbtrAddCs + ", dbtrAddCd=" + dbtrAddCd + ", dbtrAddCt=" + dbtrAddCt + ", dbtrLupdMk="
				+ dbtrLupdMk + ", dbtrLupdMb=" + dbtrLupdMb + ", dbtrLupdMs=" + dbtrLupdMs + ", dbtrLupdMd="
				+ dbtrLupdMd + ", dbtrLupdMt=" + dbtrLupdMt + ", dbtrLupdCk=" + dbtrLupdCk + ", dbtrLupdCb="
				+ dbtrLupdCb + ", dbtrLupdCs=" + dbtrLupdCs + ", dbtrLupdCd=" + dbtrLupdCd + ", dbtrLupdCt="
				+ dbtrLupdCt + ", dbtrTauthDone=" + dbtrTauthDone + ", dbtrRecStat=" + dbtrRecStat + ", dbtrAuthDone="
				+ dbtrAuthDone + ", dbtrAuthNeeded=" + dbtrAuthNeeded + ", dbtrUpdtChkId=" + dbtrUpdtChkId
				+ ", dbtrLhisTrnNo=" + dbtrLhisTrnNo + "]";
	}

}
