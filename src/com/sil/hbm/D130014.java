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
 * D130014 generated by hbm2java
 */
@Entity
@Table(name = "D130014")
@DynamicInsert
@DynamicUpdate
public class D130014 implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9200542265579314822L;
	private D130014Id id;
	private double flatRate;
	private double chgFlatRate;
	private double avgBalLimit;
	private double amtSancLimit;
	private String trAcctId;
	private double minChgAmt;
	private double maxChgAmt;
	private String batchCd;
	private short noOfFreeLeaves;
	private String plCrAcctId;
	private Date lastApplDate;
	private String lastApplAcctId;
	private char processFlag;
	private String particulars;
	private double minBalLimit;
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
	private char allowTodyn;

	public D130014() {
	}

	public D130014(D130014Id id, double flatRate, double chgFlatRate, double avgBalLimit, double amtSancLimit,
			String trAcctId, double minChgAmt, double maxChgAmt, String batchCd, short noOfFreeLeaves,
			String plCrAcctId, Date lastApplDate, String lastApplAcctId, char processFlag, String particulars,
			double minBalLimit, int dbtrAddMk, int dbtrAddMb, short dbtrAddMs, Date dbtrAddMd, Date dbtrAddMt,
			int dbtrAddCk, int dbtrAddCb, short dbtrAddCs, Date dbtrAddCd, Date dbtrAddCt, int dbtrLupdMk,
			int dbtrLupdMb, short dbtrLupdMs, Date dbtrLupdMd, Date dbtrLupdMt, int dbtrLupdCk, int dbtrLupdCb,
			short dbtrLupdCs, Date dbtrLupdCd, Date dbtrLupdCt, short dbtrTauthDone, byte dbtrRecStat,
			byte dbtrAuthDone, byte dbtrAuthNeeded, short dbtrUpdtChkId, int dbtrLhisTrnNo, char allowTodyn) {
		this.id = id;
		this.flatRate = flatRate;
		this.chgFlatRate = chgFlatRate;
		this.avgBalLimit = avgBalLimit;
		this.amtSancLimit = amtSancLimit;
		this.trAcctId = trAcctId;
		this.minChgAmt = minChgAmt;
		this.maxChgAmt = maxChgAmt;
		this.batchCd = batchCd;
		this.noOfFreeLeaves = noOfFreeLeaves;
		this.plCrAcctId = plCrAcctId;
		this.lastApplDate = lastApplDate;
		this.lastApplAcctId = lastApplAcctId;
		this.processFlag = processFlag;
		this.particulars = particulars;
		this.minBalLimit = minBalLimit;
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
		this.allowTodyn = allowTodyn;
	}

	@EmbeddedId

	@AttributeOverrides({ @AttributeOverride(name = "lbrCode", column = @Column(name = "LBrCode", nullable = false)),
			@AttributeOverride(name = "prdCd", column = @Column(name = "PrdCd", nullable = false, length = 8)),
			@AttributeOverride(name = "chgType", column = @Column(name = "ChgType", nullable = false)),
			@AttributeOverride(name = "insType", column = @Column(name = "InsType", nullable = false)),
			@AttributeOverride(name = "acctType", column = @Column(name = "AcctType", nullable = false)),
			@AttributeOverride(name = "effDate", column = @Column(name = "EffDate", nullable = false, length = 23)) })
	public D130014Id getId() {
		return this.id;
	}

	public void setId(D130014Id id) {
		this.id = id;
	}

	@Column(name = "FlatRate", nullable = false, precision = 53, scale = 0)
	public double getFlatRate() {
		return this.flatRate;
	}

	public void setFlatRate(double flatRate) {
		this.flatRate = flatRate;
	}

	@Column(name = "ChgFlatRate", nullable = false, precision = 53, scale = 0)
	public double getChgFlatRate() {
		return this.chgFlatRate;
	}

	public void setChgFlatRate(double chgFlatRate) {
		this.chgFlatRate = chgFlatRate;
	}

	@Column(name = "AvgBalLimit", nullable = false, precision = 53, scale = 0)
	public double getAvgBalLimit() {
		return this.avgBalLimit;
	}

	public void setAvgBalLimit(double avgBalLimit) {
		this.avgBalLimit = avgBalLimit;
	}

	@Column(name = "AmtSancLimit", nullable = false, precision = 53, scale = 0)
	public double getAmtSancLimit() {
		return this.amtSancLimit;
	}

	public void setAmtSancLimit(double amtSancLimit) {
		this.amtSancLimit = amtSancLimit;
	}

	@Column(name = "TrAcctId", nullable = false, length = 32)
	public String getTrAcctId() {
		return this.trAcctId;
	}

	public void setTrAcctId(String trAcctId) {
		this.trAcctId = trAcctId;
	}

	@Column(name = "MinChgAmt", nullable = false, precision = 53, scale = 0)
	public double getMinChgAmt() {
		return this.minChgAmt;
	}

	public void setMinChgAmt(double minChgAmt) {
		this.minChgAmt = minChgAmt;
	}

	@Column(name = "MaxChgAmt", nullable = false, precision = 53, scale = 0)
	public double getMaxChgAmt() {
		return this.maxChgAmt;
	}

	public void setMaxChgAmt(double maxChgAmt) {
		this.maxChgAmt = maxChgAmt;
	}

	@Column(name = "BatchCd", nullable = false, length = 8)
	public String getBatchCd() {
		return this.batchCd;
	}

	public void setBatchCd(String batchCd) {
		this.batchCd = batchCd;
	}

	@Column(name = "NoOfFreeLeaves", nullable = false)
	public short getNoOfFreeLeaves() {
		return this.noOfFreeLeaves;
	}

	public void setNoOfFreeLeaves(short noOfFreeLeaves) {
		this.noOfFreeLeaves = noOfFreeLeaves;
	}

	@Column(name = "PlCrAcctId", nullable = false, length = 32)
	public String getPlCrAcctId() {
		return this.plCrAcctId;
	}

	public void setPlCrAcctId(String plCrAcctId) {
		this.plCrAcctId = plCrAcctId;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LastApplDate", nullable = false, length = 23)
	public Date getLastApplDate() {
		return this.lastApplDate;
	}

	public void setLastApplDate(Date lastApplDate) {
		this.lastApplDate = lastApplDate;
	}

	@Column(name = "LastApplAcctID", nullable = false, length = 32)
	public String getLastApplAcctId() {
		return this.lastApplAcctId;
	}

	public void setLastApplAcctId(String lastApplAcctId) {
		this.lastApplAcctId = lastApplAcctId;
	}

	@Column(name = "ProcessFlag", nullable = false, length = 1)
	public char getProcessFlag() {
		return this.processFlag;
	}

	public void setProcessFlag(char processFlag) {
		this.processFlag = processFlag;
	}

	@Column(name = "Particulars", nullable = false, length = 35)
	public String getParticulars() {
		return this.particulars;
	}

	public void setParticulars(String particulars) {
		this.particulars = particulars;
	}

	@Column(name = "MinBalLimit", nullable = false, precision = 53, scale = 0)
	public double getMinBalLimit() {
		return this.minBalLimit;
	}

	public void setMinBalLimit(double minBalLimit) {
		this.minBalLimit = minBalLimit;
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

	@Column(name = "AllowTODYN", nullable = false, length = 1)
	public char getAllowTodyn() {
		return this.allowTodyn;
	}

	public void setAllowTodyn(char allowTodyn) {
		this.allowTodyn = allowTodyn;
	}

}
