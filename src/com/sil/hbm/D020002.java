package com.sil.hbm;
// Generated Mar 7, 2017 1:37:54 PM by Hibernate Tools 5.2.0.Beta1

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
 * D020002 generated by hbm2java
 */
@Entity
@Table(name = "D020002")
@DynamicUpdate
@DynamicInsert
public class D020002 implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6609169179981838031L;
	private D020002Id id;
	private String defBatchCd;
	private int lastReceiptNo;
	private char intRoffOpt;
	private char matperiodYn;
	private char intFreq;
	private double rdPenRate;
	private double rdPenAmt;
	private double minDepAmt;
	private double maxDepAmt;
	private char unitsAllowYn;
	private int unitValue;
	private double prematPenRate;
	private Date lastIntAppDt;
	private String lastIntAppAcId;
	private char renewalYn;
	private short noOfRenewal;
	private char cumIntYn;
	private String plDrAcctId;
	private String intCrAcctId;
	private String tdsacctId;
	private char periodType;
	private char cfYn;
	private char formulaType;
	private char instOrPrinc;
	private String instOrPrincDesc;
	private short minPeriod;
	private short maxPeriod;
	private char withInt;
	private String stdtrFrId;
	private char intPaidYn;
	private double maxAmtThruCash;
	private short noOfDaysInYear;
	private byte clIntCalcType;
	private char custFrequency;
	private char taxProjection;
	private double intTaxPer;
	private double tdsAmount;
	private char tdsAutoDeduct;
	private char addResDaysYn;
	private char plAcctHomeCurrYn;
	private short instlGraceDd;
	private short matGraceDd;
	private short shortTermDays;
	private short shortTermMths;
	private short minIntDays;
	private short rnewBckIntRateDays;
	private short rnewCurIntRateDays;
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
	private String penalCrAcctId;
	private short penalOffSetPeriod;
	private String mtdPrdAcctId;
	private short closeOdIntPeriod;
	private String cashPayAcctId;
	private short lockPeriod;
	private double intPayPer;
	private char intPayFreq;
	private double depAmt;
	private char rdInstFreq;
	private int princInUnits;
	private int matValInUnits;
	private int minNoOfInstls;
	private char preMatClosureYn;
	private Double thrshld;

	public D020002() {
	}

	public D020002(D020002Id id, String defBatchCd, int lastReceiptNo, char intRoffOpt, char matperiodYn, char intFreq,
			double rdPenRate, double rdPenAmt, double minDepAmt, double maxDepAmt, char unitsAllowYn, int unitValue,
			double prematPenRate, Date lastIntAppDt, String lastIntAppAcId, char renewalYn, short noOfRenewal,
			char cumIntYn, String plDrAcctId, String intCrAcctId, String tdsacctId, char periodType, char cfYn,
			char formulaType, char instOrPrinc, String instOrPrincDesc, short minPeriod, short maxPeriod, char withInt,
			String stdtrFrId, char intPaidYn, double maxAmtThruCash, short noOfDaysInYear, byte clIntCalcType,
			char custFrequency, char taxProjection, double intTaxPer, double tdsAmount, char tdsAutoDeduct,
			char addResDaysYn, char plAcctHomeCurrYn, short instlGraceDd, short matGraceDd, short shortTermDays,
			short shortTermMths, short minIntDays, short rnewBckIntRateDays, short rnewCurIntRateDays, int dbtrAddMk,
			int dbtrAddMb, short dbtrAddMs, Date dbtrAddMd, Date dbtrAddMt, int dbtrAddCk, int dbtrAddCb,
			short dbtrAddCs, Date dbtrAddCd, Date dbtrAddCt, int dbtrLupdMk, int dbtrLupdMb, short dbtrLupdMs,
			Date dbtrLupdMd, Date dbtrLupdMt, int dbtrLupdCk, int dbtrLupdCb, short dbtrLupdCs, Date dbtrLupdCd,
			Date dbtrLupdCt, short dbtrTauthDone, byte dbtrRecStat, byte dbtrAuthDone, byte dbtrAuthNeeded,
			short dbtrUpdtChkId, int dbtrLhisTrnNo, String penalCrAcctId, short penalOffSetPeriod, String mtdPrdAcctId,
			short closeOdIntPeriod, String cashPayAcctId, short lockPeriod, double intPayPer, char intPayFreq,
			double depAmt, char rdInstFreq, int princInUnits, int matValInUnits, int minNoOfInstls,
			char preMatClosureYn) {
		this.id = id;
		this.defBatchCd = defBatchCd;
		this.lastReceiptNo = lastReceiptNo;
		this.intRoffOpt = intRoffOpt;
		this.matperiodYn = matperiodYn;
		this.intFreq = intFreq;
		this.rdPenRate = rdPenRate;
		this.rdPenAmt = rdPenAmt;
		this.minDepAmt = minDepAmt;
		this.maxDepAmt = maxDepAmt;
		this.unitsAllowYn = unitsAllowYn;
		this.unitValue = unitValue;
		this.prematPenRate = prematPenRate;
		this.lastIntAppDt = lastIntAppDt;
		this.lastIntAppAcId = lastIntAppAcId;
		this.renewalYn = renewalYn;
		this.noOfRenewal = noOfRenewal;
		this.cumIntYn = cumIntYn;
		this.plDrAcctId = plDrAcctId;
		this.intCrAcctId = intCrAcctId;
		this.tdsacctId = tdsacctId;
		this.periodType = periodType;
		this.cfYn = cfYn;
		this.formulaType = formulaType;
		this.instOrPrinc = instOrPrinc;
		this.instOrPrincDesc = instOrPrincDesc;
		this.minPeriod = minPeriod;
		this.maxPeriod = maxPeriod;
		this.withInt = withInt;
		this.stdtrFrId = stdtrFrId;
		this.intPaidYn = intPaidYn;
		this.maxAmtThruCash = maxAmtThruCash;
		this.noOfDaysInYear = noOfDaysInYear;
		this.clIntCalcType = clIntCalcType;
		this.custFrequency = custFrequency;
		this.taxProjection = taxProjection;
		this.intTaxPer = intTaxPer;
		this.tdsAmount = tdsAmount;
		this.tdsAutoDeduct = tdsAutoDeduct;
		this.addResDaysYn = addResDaysYn;
		this.plAcctHomeCurrYn = plAcctHomeCurrYn;
		this.instlGraceDd = instlGraceDd;
		this.matGraceDd = matGraceDd;
		this.shortTermDays = shortTermDays;
		this.shortTermMths = shortTermMths;
		this.minIntDays = minIntDays;
		this.rnewBckIntRateDays = rnewBckIntRateDays;
		this.rnewCurIntRateDays = rnewCurIntRateDays;
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
		this.penalCrAcctId = penalCrAcctId;
		this.penalOffSetPeriod = penalOffSetPeriod;
		this.mtdPrdAcctId = mtdPrdAcctId;
		this.closeOdIntPeriod = closeOdIntPeriod;
		this.cashPayAcctId = cashPayAcctId;
		this.lockPeriod = lockPeriod;
		this.intPayPer = intPayPer;
		this.intPayFreq = intPayFreq;
		this.depAmt = depAmt;
		this.rdInstFreq = rdInstFreq;
		this.princInUnits = princInUnits;
		this.matValInUnits = matValInUnits;
		this.minNoOfInstls = minNoOfInstls;
		this.preMatClosureYn = preMatClosureYn;
	}

	public D020002(D020002Id id, String defBatchCd, int lastReceiptNo, char intRoffOpt, char matperiodYn, char intFreq,
			double rdPenRate, double rdPenAmt, double minDepAmt, double maxDepAmt, char unitsAllowYn, int unitValue,
			double prematPenRate, Date lastIntAppDt, String lastIntAppAcId, char renewalYn, short noOfRenewal,
			char cumIntYn, String plDrAcctId, String intCrAcctId, String tdsacctId, char periodType, char cfYn,
			char formulaType, char instOrPrinc, String instOrPrincDesc, short minPeriod, short maxPeriod, char withInt,
			String stdtrFrId, char intPaidYn, double maxAmtThruCash, short noOfDaysInYear, byte clIntCalcType,
			char custFrequency, char taxProjection, double intTaxPer, double tdsAmount, char tdsAutoDeduct,
			char addResDaysYn, char plAcctHomeCurrYn, short instlGraceDd, short matGraceDd, short shortTermDays,
			short shortTermMths, short minIntDays, short rnewBckIntRateDays, short rnewCurIntRateDays, int dbtrAddMk,
			int dbtrAddMb, short dbtrAddMs, Date dbtrAddMd, Date dbtrAddMt, int dbtrAddCk, int dbtrAddCb,
			short dbtrAddCs, Date dbtrAddCd, Date dbtrAddCt, int dbtrLupdMk, int dbtrLupdMb, short dbtrLupdMs,
			Date dbtrLupdMd, Date dbtrLupdMt, int dbtrLupdCk, int dbtrLupdCb, short dbtrLupdCs, Date dbtrLupdCd,
			Date dbtrLupdCt, short dbtrTauthDone, byte dbtrRecStat, byte dbtrAuthDone, byte dbtrAuthNeeded,
			short dbtrUpdtChkId, int dbtrLhisTrnNo, String penalCrAcctId, short penalOffSetPeriod, String mtdPrdAcctId,
			short closeOdIntPeriod, String cashPayAcctId, short lockPeriod, double intPayPer, char intPayFreq,
			double depAmt, char rdInstFreq, int princInUnits, int matValInUnits, int minNoOfInstls,
			char preMatClosureYn, Double thrshld) {
		this.id = id;
		this.defBatchCd = defBatchCd;
		this.lastReceiptNo = lastReceiptNo;
		this.intRoffOpt = intRoffOpt;
		this.matperiodYn = matperiodYn;
		this.intFreq = intFreq;
		this.rdPenRate = rdPenRate;
		this.rdPenAmt = rdPenAmt;
		this.minDepAmt = minDepAmt;
		this.maxDepAmt = maxDepAmt;
		this.unitsAllowYn = unitsAllowYn;
		this.unitValue = unitValue;
		this.prematPenRate = prematPenRate;
		this.lastIntAppDt = lastIntAppDt;
		this.lastIntAppAcId = lastIntAppAcId;
		this.renewalYn = renewalYn;
		this.noOfRenewal = noOfRenewal;
		this.cumIntYn = cumIntYn;
		this.plDrAcctId = plDrAcctId;
		this.intCrAcctId = intCrAcctId;
		this.tdsacctId = tdsacctId;
		this.periodType = periodType;
		this.cfYn = cfYn;
		this.formulaType = formulaType;
		this.instOrPrinc = instOrPrinc;
		this.instOrPrincDesc = instOrPrincDesc;
		this.minPeriod = minPeriod;
		this.maxPeriod = maxPeriod;
		this.withInt = withInt;
		this.stdtrFrId = stdtrFrId;
		this.intPaidYn = intPaidYn;
		this.maxAmtThruCash = maxAmtThruCash;
		this.noOfDaysInYear = noOfDaysInYear;
		this.clIntCalcType = clIntCalcType;
		this.custFrequency = custFrequency;
		this.taxProjection = taxProjection;
		this.intTaxPer = intTaxPer;
		this.tdsAmount = tdsAmount;
		this.tdsAutoDeduct = tdsAutoDeduct;
		this.addResDaysYn = addResDaysYn;
		this.plAcctHomeCurrYn = plAcctHomeCurrYn;
		this.instlGraceDd = instlGraceDd;
		this.matGraceDd = matGraceDd;
		this.shortTermDays = shortTermDays;
		this.shortTermMths = shortTermMths;
		this.minIntDays = minIntDays;
		this.rnewBckIntRateDays = rnewBckIntRateDays;
		this.rnewCurIntRateDays = rnewCurIntRateDays;
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
		this.penalCrAcctId = penalCrAcctId;
		this.penalOffSetPeriod = penalOffSetPeriod;
		this.mtdPrdAcctId = mtdPrdAcctId;
		this.closeOdIntPeriod = closeOdIntPeriod;
		this.cashPayAcctId = cashPayAcctId;
		this.lockPeriod = lockPeriod;
		this.intPayPer = intPayPer;
		this.intPayFreq = intPayFreq;
		this.depAmt = depAmt;
		this.rdInstFreq = rdInstFreq;
		this.princInUnits = princInUnits;
		this.matValInUnits = matValInUnits;
		this.minNoOfInstls = minNoOfInstls;
		this.preMatClosureYn = preMatClosureYn;
		this.thrshld = thrshld;
	}

	@EmbeddedId

	@AttributeOverrides({ @AttributeOverride(name = "lbrCode", column = @Column(name = "LBrCode", nullable = false)),
			@AttributeOverride(name = "prdCd", column = @Column(name = "PrdCd", nullable = false, length = 8)) })
	public D020002Id getId() {
		return this.id;
	}

	public void setId(D020002Id id) {
		this.id = id;
	}

	@Column(name = "DefBatchCd", nullable = false, length = 8)
	public String getDefBatchCd() {
		return this.defBatchCd;
	}

	public void setDefBatchCd(String defBatchCd) {
		this.defBatchCd = defBatchCd;
	}

	@Column(name = "LastReceiptNo", nullable = false)
	public int getLastReceiptNo() {
		return this.lastReceiptNo;
	}

	public void setLastReceiptNo(int lastReceiptNo) {
		this.lastReceiptNo = lastReceiptNo;
	}

	@Column(name = "IntRoffOpt", nullable = false, length = 1)
	public char getIntRoffOpt() {
		return this.intRoffOpt;
	}

	public void setIntRoffOpt(char intRoffOpt) {
		this.intRoffOpt = intRoffOpt;
	}

	@Column(name = "MatperiodYN", nullable = false, length = 1)
	public char getMatperiodYn() {
		return this.matperiodYn;
	}

	public void setMatperiodYn(char matperiodYn) {
		this.matperiodYn = matperiodYn;
	}

	@Column(name = "IntFreq", nullable = false, length = 1)
	public char getIntFreq() {
		return this.intFreq;
	}

	public void setIntFreq(char intFreq) {
		this.intFreq = intFreq;
	}

	@Column(name = "RdPenRate", nullable = false, precision = 53, scale = 0)
	public double getRdPenRate() {
		return this.rdPenRate;
	}

	public void setRdPenRate(double rdPenRate) {
		this.rdPenRate = rdPenRate;
	}

	@Column(name = "RdPenAmt", nullable = false, precision = 53, scale = 0)
	public double getRdPenAmt() {
		return this.rdPenAmt;
	}

	public void setRdPenAmt(double rdPenAmt) {
		this.rdPenAmt = rdPenAmt;
	}

	@Column(name = "MinDepAmt", nullable = false, precision = 53, scale = 0)
	public double getMinDepAmt() {
		return this.minDepAmt;
	}

	public void setMinDepAmt(double minDepAmt) {
		this.minDepAmt = minDepAmt;
	}

	@Column(name = "MaxDepAmt", nullable = false, precision = 53, scale = 0)
	public double getMaxDepAmt() {
		return this.maxDepAmt;
	}

	public void setMaxDepAmt(double maxDepAmt) {
		this.maxDepAmt = maxDepAmt;
	}

	@Column(name = "UnitsAllowYN", nullable = false, length = 1)
	public char getUnitsAllowYn() {
		return this.unitsAllowYn;
	}

	public void setUnitsAllowYn(char unitsAllowYn) {
		this.unitsAllowYn = unitsAllowYn;
	}

	@Column(name = "UnitValue", nullable = false)
	public int getUnitValue() {
		return this.unitValue;
	}

	public void setUnitValue(int unitValue) {
		this.unitValue = unitValue;
	}

	@Column(name = "PrematPenRate", nullable = false, precision = 53, scale = 0)
	public double getPrematPenRate() {
		return this.prematPenRate;
	}

	public void setPrematPenRate(double prematPenRate) {
		this.prematPenRate = prematPenRate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LastIntAppDt", nullable = false, length = 23)
	public Date getLastIntAppDt() {
		return this.lastIntAppDt;
	}

	public void setLastIntAppDt(Date lastIntAppDt) {
		this.lastIntAppDt = lastIntAppDt;
	}

	@Column(name = "LastIntAppAcId", nullable = false, length = 32)
	public String getLastIntAppAcId() {
		return this.lastIntAppAcId;
	}

	public void setLastIntAppAcId(String lastIntAppAcId) {
		this.lastIntAppAcId = lastIntAppAcId;
	}

	@Column(name = "RenewalYN", nullable = false, length = 1)
	public char getRenewalYn() {
		return this.renewalYn;
	}

	public void setRenewalYn(char renewalYn) {
		this.renewalYn = renewalYn;
	}

	@Column(name = "NoOfRenewal", nullable = false)
	public short getNoOfRenewal() {
		return this.noOfRenewal;
	}

	public void setNoOfRenewal(short noOfRenewal) {
		this.noOfRenewal = noOfRenewal;
	}

	@Column(name = "CumIntYN", nullable = false, length = 1)
	public char getCumIntYn() {
		return this.cumIntYn;
	}

	public void setCumIntYn(char cumIntYn) {
		this.cumIntYn = cumIntYn;
	}

	@Column(name = "PlDrAcctId", nullable = false, length = 32)
	public String getPlDrAcctId() {
		return this.plDrAcctId;
	}

	public void setPlDrAcctId(String plDrAcctId) {
		this.plDrAcctId = plDrAcctId;
	}

	@Column(name = "IntCrAcctId", nullable = false, length = 32)
	public String getIntCrAcctId() {
		return this.intCrAcctId;
	}

	public void setIntCrAcctId(String intCrAcctId) {
		this.intCrAcctId = intCrAcctId;
	}

	@Column(name = "TDSAcctId", nullable = false, length = 32)
	public String getTdsacctId() {
		return this.tdsacctId;
	}

	public void setTdsacctId(String tdsacctId) {
		this.tdsacctId = tdsacctId;
	}

	@Column(name = "PeriodType", nullable = false, length = 1)
	public char getPeriodType() {
		return this.periodType;
	}

	public void setPeriodType(char periodType) {
		this.periodType = periodType;
	}

	@Column(name = "CfYN", nullable = false, length = 1)
	public char getCfYn() {
		return this.cfYn;
	}

	public void setCfYn(char cfYn) {
		this.cfYn = cfYn;
	}

	@Column(name = "FormulaType", nullable = false, length = 1)
	public char getFormulaType() {
		return this.formulaType;
	}

	public void setFormulaType(char formulaType) {
		this.formulaType = formulaType;
	}

	@Column(name = "InstOrPrinc", nullable = false, length = 1)
	public char getInstOrPrinc() {
		return this.instOrPrinc;
	}

	public void setInstOrPrinc(char instOrPrinc) {
		this.instOrPrinc = instOrPrinc;
	}

	@Column(name = "InstOrPrincDesc", nullable = false, length = 15)
	public String getInstOrPrincDesc() {
		return this.instOrPrincDesc;
	}

	public void setInstOrPrincDesc(String instOrPrincDesc) {
		this.instOrPrincDesc = instOrPrincDesc;
	}

	@Column(name = "MinPeriod", nullable = false)
	public short getMinPeriod() {
		return this.minPeriod;
	}

	public void setMinPeriod(short minPeriod) {
		this.minPeriod = minPeriod;
	}

	@Column(name = "MaxPeriod", nullable = false)
	public short getMaxPeriod() {
		return this.maxPeriod;
	}

	public void setMaxPeriod(short maxPeriod) {
		this.maxPeriod = maxPeriod;
	}

	@Column(name = "WithInt", nullable = false, length = 1)
	public char getWithInt() {
		return this.withInt;
	}

	public void setWithInt(char withInt) {
		this.withInt = withInt;
	}

	@Column(name = "StdtrFrId", nullable = false, length = 32)
	public String getStdtrFrId() {
		return this.stdtrFrId;
	}

	public void setStdtrFrId(String stdtrFrId) {
		this.stdtrFrId = stdtrFrId;
	}

	@Column(name = "IntPaidYN", nullable = false, length = 1)
	public char getIntPaidYn() {
		return this.intPaidYn;
	}

	public void setIntPaidYn(char intPaidYn) {
		this.intPaidYn = intPaidYn;
	}

	@Column(name = "MaxAmtThruCash", nullable = false, precision = 53, scale = 0)
	public double getMaxAmtThruCash() {
		return this.maxAmtThruCash;
	}

	public void setMaxAmtThruCash(double maxAmtThruCash) {
		this.maxAmtThruCash = maxAmtThruCash;
	}

	@Column(name = "NoOfDaysInYear", nullable = false)
	public short getNoOfDaysInYear() {
		return this.noOfDaysInYear;
	}

	public void setNoOfDaysInYear(short noOfDaysInYear) {
		this.noOfDaysInYear = noOfDaysInYear;
	}

	@Column(name = "ClIntCalcType", nullable = false)
	public byte getClIntCalcType() {
		return this.clIntCalcType;
	}

	public void setClIntCalcType(byte clIntCalcType) {
		this.clIntCalcType = clIntCalcType;
	}

	@Column(name = "CustFrequency", nullable = false, length = 1)
	public char getCustFrequency() {
		return this.custFrequency;
	}

	public void setCustFrequency(char custFrequency) {
		this.custFrequency = custFrequency;
	}

	@Column(name = "TaxProjection", nullable = false, length = 1)
	public char getTaxProjection() {
		return this.taxProjection;
	}

	public void setTaxProjection(char taxProjection) {
		this.taxProjection = taxProjection;
	}

	@Column(name = "IntTaxPer", nullable = false, precision = 53, scale = 0)
	public double getIntTaxPer() {
		return this.intTaxPer;
	}

	public void setIntTaxPer(double intTaxPer) {
		this.intTaxPer = intTaxPer;
	}

	@Column(name = "TdsAmount", nullable = false, precision = 53, scale = 0)
	public double getTdsAmount() {
		return this.tdsAmount;
	}

	public void setTdsAmount(double tdsAmount) {
		this.tdsAmount = tdsAmount;
	}

	@Column(name = "TdsAutoDeduct", nullable = false, length = 1)
	public char getTdsAutoDeduct() {
		return this.tdsAutoDeduct;
	}

	public void setTdsAutoDeduct(char tdsAutoDeduct) {
		this.tdsAutoDeduct = tdsAutoDeduct;
	}

	@Column(name = "AddResDaysYN", nullable = false, length = 1)
	public char getAddResDaysYn() {
		return this.addResDaysYn;
	}

	public void setAddResDaysYn(char addResDaysYn) {
		this.addResDaysYn = addResDaysYn;
	}

	@Column(name = "PlAcctHomeCurrYN", nullable = false, length = 1)
	public char getPlAcctHomeCurrYn() {
		return this.plAcctHomeCurrYn;
	}

	public void setPlAcctHomeCurrYn(char plAcctHomeCurrYn) {
		this.plAcctHomeCurrYn = plAcctHomeCurrYn;
	}

	@Column(name = "InstlGraceDD", nullable = false)
	public short getInstlGraceDd() {
		return this.instlGraceDd;
	}

	public void setInstlGraceDd(short instlGraceDd) {
		this.instlGraceDd = instlGraceDd;
	}

	@Column(name = "MatGraceDD", nullable = false)
	public short getMatGraceDd() {
		return this.matGraceDd;
	}

	public void setMatGraceDd(short matGraceDd) {
		this.matGraceDd = matGraceDd;
	}

	@Column(name = "ShortTermDays", nullable = false)
	public short getShortTermDays() {
		return this.shortTermDays;
	}

	public void setShortTermDays(short shortTermDays) {
		this.shortTermDays = shortTermDays;
	}

	@Column(name = "ShortTermMths", nullable = false)
	public short getShortTermMths() {
		return this.shortTermMths;
	}

	public void setShortTermMths(short shortTermMths) {
		this.shortTermMths = shortTermMths;
	}

	@Column(name = "MinIntDays", nullable = false)
	public short getMinIntDays() {
		return this.minIntDays;
	}

	public void setMinIntDays(short minIntDays) {
		this.minIntDays = minIntDays;
	}

	@Column(name = "RnewBckIntRateDays", nullable = false)
	public short getRnewBckIntRateDays() {
		return this.rnewBckIntRateDays;
	}

	public void setRnewBckIntRateDays(short rnewBckIntRateDays) {
		this.rnewBckIntRateDays = rnewBckIntRateDays;
	}

	@Column(name = "RnewCurIntRateDays", nullable = false)
	public short getRnewCurIntRateDays() {
		return this.rnewCurIntRateDays;
	}

	public void setRnewCurIntRateDays(short rnewCurIntRateDays) {
		this.rnewCurIntRateDays = rnewCurIntRateDays;
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

	@Column(name = "PenalCrAcctId", nullable = false, length = 32)
	public String getPenalCrAcctId() {
		return this.penalCrAcctId;
	}

	public void setPenalCrAcctId(String penalCrAcctId) {
		this.penalCrAcctId = penalCrAcctId;
	}

	@Column(name = "PenalOffSetPeriod", nullable = false)
	public short getPenalOffSetPeriod() {
		return this.penalOffSetPeriod;
	}

	public void setPenalOffSetPeriod(short penalOffSetPeriod) {
		this.penalOffSetPeriod = penalOffSetPeriod;
	}

	@Column(name = "MtdPrdAcctId", nullable = false, length = 32)
	public String getMtdPrdAcctId() {
		return this.mtdPrdAcctId;
	}

	public void setMtdPrdAcctId(String mtdPrdAcctId) {
		this.mtdPrdAcctId = mtdPrdAcctId;
	}

	@Column(name = "CloseOdIntPeriod", nullable = false)
	public short getCloseOdIntPeriod() {
		return this.closeOdIntPeriod;
	}

	public void setCloseOdIntPeriod(short closeOdIntPeriod) {
		this.closeOdIntPeriod = closeOdIntPeriod;
	}

	@Column(name = "CashPayAcctId", nullable = false, length = 32)
	public String getCashPayAcctId() {
		return this.cashPayAcctId;
	}

	public void setCashPayAcctId(String cashPayAcctId) {
		this.cashPayAcctId = cashPayAcctId;
	}

	@Column(name = "LockPeriod", nullable = false)
	public short getLockPeriod() {
		return this.lockPeriod;
	}

	public void setLockPeriod(short lockPeriod) {
		this.lockPeriod = lockPeriod;
	}

	@Column(name = "IntPayPer", nullable = false, precision = 53, scale = 0)
	public double getIntPayPer() {
		return this.intPayPer;
	}

	public void setIntPayPer(double intPayPer) {
		this.intPayPer = intPayPer;
	}

	@Column(name = "IntPayFreq", nullable = false, length = 1)
	public char getIntPayFreq() {
		return this.intPayFreq;
	}

	public void setIntPayFreq(char intPayFreq) {
		this.intPayFreq = intPayFreq;
	}

	@Column(name = "DepAmt", nullable = false, precision = 53, scale = 0)
	public double getDepAmt() {
		return this.depAmt;
	}

	public void setDepAmt(double depAmt) {
		this.depAmt = depAmt;
	}

	@Column(name = "RdInstFreq", nullable = false, length = 1)
	public char getRdInstFreq() {
		return this.rdInstFreq;
	}

	public void setRdInstFreq(char rdInstFreq) {
		this.rdInstFreq = rdInstFreq;
	}

	@Column(name = "PrincInUnits", nullable = false)
	public int getPrincInUnits() {
		return this.princInUnits;
	}

	public void setPrincInUnits(int princInUnits) {
		this.princInUnits = princInUnits;
	}

	@Column(name = "MatValInUnits", nullable = false)
	public int getMatValInUnits() {
		return this.matValInUnits;
	}

	public void setMatValInUnits(int matValInUnits) {
		this.matValInUnits = matValInUnits;
	}

	@Column(name = "MinNoOfInstls", nullable = false)
	public int getMinNoOfInstls() {
		return this.minNoOfInstls;
	}

	public void setMinNoOfInstls(int minNoOfInstls) {
		this.minNoOfInstls = minNoOfInstls;
	}

	@Column(name = "PreMatClosureYN", nullable = false, length = 1)
	public char getPreMatClosureYn() {
		return this.preMatClosureYn;
	}

	public void setPreMatClosureYn(char preMatClosureYn) {
		this.preMatClosureYn = preMatClosureYn;
	}

	@Column(name = "THRSHLD", precision = 53, scale = 0)
	public Double getThrshld() {
		return this.thrshld;
	}

	public void setThrshld(Double thrshld) {
		this.thrshld = thrshld;
	}

}
