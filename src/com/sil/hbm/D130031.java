package com.sil.hbm;
// Generated Feb 2, 2017 1:30:07 PM by Hibernate Tools 5.2.0.Beta1

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
 * D130031 generated by hbm2java
 */
@Entity
@Table(name = "D130031")
@DynamicInsert
@DynamicUpdate
public class D130031 implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6605141591488970357L;
	private D130031Id id;
	private double serTaxRate;
	private double eduCesRate;
	private String serTaxAcctId;
	private String eduCesAcctId;
	private char taxRoffOpt;
	private char cessRoffOpt;
	private char incTaxInTotChg;
	private char chrgYn;
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

	public D130031() {
	}

	public D130031(D130031Id id, double serTaxRate, double eduCesRate, String serTaxAcctId, String eduCesAcctId,
			char taxRoffOpt, char cessRoffOpt, char incTaxInTotChg, char chrgYn, int dbtrAddMk, int dbtrAddMb,
			short dbtrAddMs, Date dbtrAddMd, Date dbtrAddMt, int dbtrAddCk, int dbtrAddCb, short dbtrAddCs,
			Date dbtrAddCd, Date dbtrAddCt, int dbtrLupdMk, int dbtrLupdMb, short dbtrLupdMs, Date dbtrLupdMd,
			Date dbtrLupdMt, int dbtrLupdCk, int dbtrLupdCb, short dbtrLupdCs, Date dbtrLupdCd, Date dbtrLupdCt,
			short dbtrTauthDone, byte dbtrRecStat, byte dbtrAuthDone, byte dbtrAuthNeeded, short dbtrUpdtChkId,
			int dbtrLhisTrnNo) {
		this.id = id;
		this.serTaxRate = serTaxRate;
		this.eduCesRate = eduCesRate;
		this.serTaxAcctId = serTaxAcctId;
		this.eduCesAcctId = eduCesAcctId;
		this.taxRoffOpt = taxRoffOpt;
		this.cessRoffOpt = cessRoffOpt;
		this.incTaxInTotChg = incTaxInTotChg;
		this.chrgYn = chrgYn;
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

	@AttributeOverrides({ @AttributeOverride(name = "chgType", column = @Column(name = "ChgType", nullable = false)),
			@AttributeOverride(name = "effDate", column = @Column(name = "EffDate", nullable = false, length = 23)) })
	public D130031Id getId() {
		return this.id;
	}

	public void setId(D130031Id id) {
		this.id = id;
	}

	@Column(name = "SerTaxRate", nullable = false, precision = 53, scale = 0)
	public double getSerTaxRate() {
		return this.serTaxRate;
	}

	public void setSerTaxRate(double serTaxRate) {
		this.serTaxRate = serTaxRate;
	}

	@Column(name = "EduCesRate", nullable = false, precision = 53, scale = 0)
	public double getEduCesRate() {
		return this.eduCesRate;
	}

	public void setEduCesRate(double eduCesRate) {
		this.eduCesRate = eduCesRate;
	}

	@Column(name = "SerTaxAcctId", nullable = false, length = 32)
	public String getSerTaxAcctId() {
		return this.serTaxAcctId;
	}

	public void setSerTaxAcctId(String serTaxAcctId) {
		this.serTaxAcctId = serTaxAcctId;
	}

	@Column(name = "EduCesAcctId", nullable = false, length = 32)
	public String getEduCesAcctId() {
		return this.eduCesAcctId;
	}

	public void setEduCesAcctId(String eduCesAcctId) {
		this.eduCesAcctId = eduCesAcctId;
	}

	@Column(name = "TaxROffOpt", nullable = false, length = 1)
	public char getTaxRoffOpt() {
		return this.taxRoffOpt;
	}

	public void setTaxRoffOpt(char taxRoffOpt) {
		this.taxRoffOpt = taxRoffOpt;
	}

	@Column(name = "CessROffOpt", nullable = false, length = 1)
	public char getCessRoffOpt() {
		return this.cessRoffOpt;
	}

	public void setCessRoffOpt(char cessRoffOpt) {
		this.cessRoffOpt = cessRoffOpt;
	}

	@Column(name = "IncTaxInTotChg", nullable = false, length = 1)
	public char getIncTaxInTotChg() {
		return this.incTaxInTotChg;
	}

	public void setIncTaxInTotChg(char incTaxInTotChg) {
		this.incTaxInTotChg = incTaxInTotChg;
	}

	@Column(name = "ChrgYN", nullable = false, length = 1)
	public char getChrgYn() {
		return this.chrgYn;
	}

	public void setChrgYn(char chrgYn) {
		this.chrgYn = chrgYn;
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

}
