package com.sil.hbm;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "D020004")
@DynamicInsert
@DynamicUpdate
public class D020004 implements java.io.Serializable {
	private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	private static final long serialVersionUID = -4156435843420427444L;
	private D020004Id id;
	private String nameTitle;
	private String longName;
	private String curCd;
	private double offSetRate;
	private Date certDate;
	private Date asOffdate;
	private short noInst;
	private short noOfMonths;
	private short noOfDays;
	private double intRate;
	private double instOrPrincAmt;
	private double matVal;
	private Date matDate;
	private double totalLien;
	private double periodicIntAmt;
	private double brokenPeriodInt;
	private byte receiptStatus;
	private String trfrAcctId;
	private char intPayableType;
	private int noOfUnits;
	private short noOfRenewals;
	private Date lastRepayDate;
	private short monthsDelay;
	private double clIntPaidAmt;
	private double clIntRate;
	private double clNotionalRate;
	private byte noCertPrint;
	private String maskRecieptNo;
	private int prnSrNo;
	private Date closedDate;
	private String remarks;
	private double inClBalFcy;
	private double unClBalFcy;
	private double mainBalFcy;
	private double intPrvdAmtFcy;
	private double intPaidAmtFcy;
	private double tdsAmtFcy;
	private double mainBalLcy;
	private double intPrvdAmtLcy;
	private double intPaidAmtLcy;
	private double tdsAmtLcy;
	private char tdsYn;
	private short tdsReasonCd;
	private double accPrvdAmtFcy;
	private double accPrvdAmtLcy;
	private byte noticeType;
	private byte sourceOfFunds;
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
	private int trfrLbrCode;
	private double lastAmtPaid;
	private double totAmtPaid;
	private Date lastUnitPaidDt;
	private short lockPeriod;
	private double intPayPer;
	private char intPayFreq;
	private Date lockEndDate;
	private double intPayAmt;
	private Date nextPayOutDt;
	private Date lastPayOutDate;
	
	
	//For calc
		
		private Double tdsprojected = new Double(0);
		@Transient
		private Double intprojected = new Double(0);
		@Transient
		private Double tdsDefAmt = new Double(0);
		@Transient
		private Date tdsDate;
		@Transient
		private Double tdsRate;
		@Transient
		private Double intprovision = new Double(0);
		@Transient
		private Double totalRate = new Double(0);
		@Transient
		private Double intrateCalc = new Double(0);
		@Transient
		private Double intEarnedAmount;
		@Transient
		private String renewalAmtWords;
		@Transient
		private D009500 currentProjection;
		@Transient
		private CentrelisedBrwiseCustTDSFile centrelisedBrwiseCustTDSFile;

	public D020004() {
	}

	public D020004(D020004Id id, String nameTitle, String longName, String curCd, double offSetRate, Date certDate,
			Date asOffdate, short noInst, short noOfMonths, short noOfDays, double intRate, double instOrPrincAmt,
			double matVal, Date matDate, double totalLien, double periodicIntAmt, double brokenPeriodInt,
			byte receiptStatus, String trfrAcctId, char intPayableType, int noOfUnits, short noOfRenewals,
			Date lastRepayDate, short monthsDelay, double clIntPaidAmt, double clIntRate, double clNotionalRate,
			byte noCertPrint, String maskRecieptNo, int prnSrNo, Date closedDate, String remarks, double inClBalFcy,
			double unClBalFcy, double mainBalFcy, double intPrvdAmtFcy, double intPaidAmtFcy, double tdsAmtFcy,
			double mainBalLcy, double intPrvdAmtLcy, double intPaidAmtLcy, double tdsAmtLcy, char tdsYn,
			short tdsReasonCd, double accPrvdAmtFcy, double accPrvdAmtLcy, byte noticeType, byte sourceOfFunds,
			int dbtrAddMk, int dbtrAddMb, short dbtrAddMs, Date dbtrAddMd, Date dbtrAddMt, int dbtrAddCk, int dbtrAddCb,
			short dbtrAddCs, Date dbtrAddCd, Date dbtrAddCt, int dbtrLupdMk, int dbtrLupdMb, short dbtrLupdMs,
			Date dbtrLupdMd, Date dbtrLupdMt, int dbtrLupdCk, int dbtrLupdCb, short dbtrLupdCs, Date dbtrLupdCd,
			Date dbtrLupdCt, short dbtrTauthDone, byte dbtrRecStat, byte dbtrAuthDone, byte dbtrAuthNeeded,
			short dbtrUpdtChkId, int dbtrLhisTrnNo, int trfrLbrCode, double lastAmtPaid, double totAmtPaid,
			Date lastUnitPaidDt, short lockPeriod, double intPayPer, char intPayFreq, Date lockEndDate,
			double intPayAmt, Date nextPayOutDt, Date lastPayOutDate) {
		this.id = id;
		this.nameTitle = nameTitle;
		this.longName = longName;
		this.curCd = curCd;
		this.offSetRate = offSetRate;
		this.certDate = certDate;
		this.asOffdate = asOffdate;
		this.noInst = noInst;
		this.noOfMonths = noOfMonths;
		this.noOfDays = noOfDays;
		this.intRate = intRate;
		this.instOrPrincAmt = instOrPrincAmt;
		this.matVal = matVal;
		this.matDate = matDate;
		this.totalLien = totalLien;
		this.periodicIntAmt = periodicIntAmt;
		this.brokenPeriodInt = brokenPeriodInt;
		this.receiptStatus = receiptStatus;
		this.trfrAcctId = trfrAcctId;
		this.intPayableType = intPayableType;
		this.noOfUnits = noOfUnits;
		this.noOfRenewals = noOfRenewals;
		this.lastRepayDate = lastRepayDate;
		this.monthsDelay = monthsDelay;
		this.clIntPaidAmt = clIntPaidAmt;
		this.clIntRate = clIntRate;
		this.clNotionalRate = clNotionalRate;
		this.noCertPrint = noCertPrint;
		this.maskRecieptNo = maskRecieptNo;
		this.prnSrNo = prnSrNo;
		this.closedDate = closedDate;
		this.remarks = remarks;
		this.inClBalFcy = inClBalFcy;
		this.unClBalFcy = unClBalFcy;
		this.mainBalFcy = mainBalFcy;
		this.intPrvdAmtFcy = intPrvdAmtFcy;
		this.intPaidAmtFcy = intPaidAmtFcy;
		this.tdsAmtFcy = tdsAmtFcy;
		this.mainBalLcy = mainBalLcy;
		this.intPrvdAmtLcy = intPrvdAmtLcy;
		this.intPaidAmtLcy = intPaidAmtLcy;
		this.tdsAmtLcy = tdsAmtLcy;
		this.tdsYn = tdsYn;
		this.tdsReasonCd = tdsReasonCd;
		this.accPrvdAmtFcy = accPrvdAmtFcy;
		this.accPrvdAmtLcy = accPrvdAmtLcy;
		this.noticeType = noticeType;
		this.sourceOfFunds = sourceOfFunds;
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
		this.trfrLbrCode = trfrLbrCode;
		this.lastAmtPaid = lastAmtPaid;
		this.totAmtPaid = totAmtPaid;
		this.lastUnitPaidDt = lastUnitPaidDt;
		this.lockPeriod = lockPeriod;
		this.intPayPer = intPayPer;
		this.intPayFreq = intPayFreq;
		this.lockEndDate = lockEndDate;
		this.intPayAmt = intPayAmt;
		this.nextPayOutDt = nextPayOutDt;
		this.lastPayOutDate = lastPayOutDate;
	}

	@EmbeddedId

	@AttributeOverrides({ @AttributeOverride(name = "lbrCode", column = @Column(name = "LBrCode", nullable = false)),
			@AttributeOverride(name = "prdAcctId", column = @Column(name = "PrdAcctId", nullable = false, length = 32)) })
	public D020004Id getId() {
		return this.id;
	}

	public void setId(D020004Id id) {
		this.id = id;
	}

	@Column(name = "NameTitle", nullable = false, length = 4)
	public String getNameTitle() {
		return this.nameTitle;
	}

	public void setNameTitle(String nameTitle) {
		this.nameTitle = nameTitle;
	}

	@Column(name = "LongName", nullable = false, length = 50)
	public String getLongName() {
		return this.longName;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	@Column(name = "CurCd", nullable = false, length = 3)
	public String getCurCd() {
		return this.curCd;
	}

	public void setCurCd(String curCd) {
		this.curCd = curCd;
	}

	@Column(name = "OffSetRate", nullable = false, precision = 53, scale = 0)
	public double getOffSetRate() {
		return this.offSetRate;
	}

	public void setOffSetRate(double offSetRate) {
		this.offSetRate = offSetRate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CertDate", nullable = false, length = 23)
	public Date getCertDate() {
		return this.certDate;
	}

	public void setCertDate(Date certDate) {
		this.certDate = certDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "AsOffdate", nullable = false, length = 23)
	public Date getAsOffdate() {
		return this.asOffdate;
	}

	public void setAsOffdate(Date asOffdate) {
		this.asOffdate = asOffdate;
	}

	@Column(name = "NoInst", nullable = false)
	public short getNoInst() {
		return this.noInst;
	}

	public void setNoInst(short noInst) {
		this.noInst = noInst;
	}

	@Column(name = "NoOfMonths", nullable = false)
	public short getNoOfMonths() {
		return this.noOfMonths;
	}

	public void setNoOfMonths(short noOfMonths) {
		this.noOfMonths = noOfMonths;
	}

	@Column(name = "NoOfDays", nullable = false)
	public short getNoOfDays() {
		return this.noOfDays;
	}

	public void setNoOfDays(short noOfDays) {
		this.noOfDays = noOfDays;
	}

	@Column(name = "IntRate", nullable = false, precision = 53, scale = 0)
	public double getIntRate() {
		return this.intRate;
	}

	public void setIntRate(double intRate) {
		this.intRate = intRate;
	}

	@Column(name = "InstOrPrincAmt", nullable = false, precision = 53, scale = 0)
	public double getInstOrPrincAmt() {
		return this.instOrPrincAmt;
	}

	public void setInstOrPrincAmt(double instOrPrincAmt) {
		this.instOrPrincAmt = instOrPrincAmt;
	}

	@Column(name = "MatVal", nullable = false, precision = 53, scale = 0)
	public double getMatVal() {
		return this.matVal;
	}

	public void setMatVal(double matVal) {
		this.matVal = matVal;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MatDate", nullable = false, length = 23)
	public Date getMatDate() {
		return this.matDate;
	}

	public void setMatDate(Date matDate) {
		this.matDate = matDate;
	}

	@Column(name = "TotalLien", nullable = false, precision = 53, scale = 0)
	public double getTotalLien() {
		return this.totalLien;
	}

	public void setTotalLien(double totalLien) {
		this.totalLien = totalLien;
	}

	@Column(name = "PeriodicIntAmt", nullable = false, precision = 53, scale = 0)
	public double getPeriodicIntAmt() {
		return this.periodicIntAmt;
	}

	public void setPeriodicIntAmt(double periodicIntAmt) {
		this.periodicIntAmt = periodicIntAmt;
	}

	@Column(name = "BrokenPeriodInt", nullable = false, precision = 53, scale = 0)
	public double getBrokenPeriodInt() {
		return this.brokenPeriodInt;
	}

	public void setBrokenPeriodInt(double brokenPeriodInt) {
		this.brokenPeriodInt = brokenPeriodInt;
	}

	@Column(name = "ReceiptStatus", nullable = false)
	public byte getReceiptStatus() {
		return this.receiptStatus;
	}

	public void setReceiptStatus(byte receiptStatus) {
		this.receiptStatus = receiptStatus;
	}

	@Column(name = "TrfrAcctId", nullable = false, length = 32)
	public String getTrfrAcctId() {
		return this.trfrAcctId;
	}

	public void setTrfrAcctId(String trfrAcctId) {
		this.trfrAcctId = trfrAcctId;
	}

	@Column(name = "IntPayableType", nullable = false, length = 1)
	public char getIntPayableType() {
		return this.intPayableType;
	}

	public void setIntPayableType(char intPayableType) {
		this.intPayableType = intPayableType;
	}

	@Column(name = "NoOfUnits", nullable = false)
	public int getNoOfUnits() {
		return this.noOfUnits;
	}

	public void setNoOfUnits(int noOfUnits) {
		this.noOfUnits = noOfUnits;
	}

	@Column(name = "NoOfRenewals", nullable = false)
	public short getNoOfRenewals() {
		return this.noOfRenewals;
	}

	public void setNoOfRenewals(short noOfRenewals) {
		this.noOfRenewals = noOfRenewals;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LastRepayDate", nullable = false, length = 23)
	public Date getLastRepayDate() {
		return this.lastRepayDate;
	}

	public void setLastRepayDate(Date lastRepayDate) {
		this.lastRepayDate = lastRepayDate;
	}

	@Column(name = "MonthsDelay", nullable = false)
	public short getMonthsDelay() {
		return this.monthsDelay;
	}

	public void setMonthsDelay(short monthsDelay) {
		this.monthsDelay = monthsDelay;
	}

	@Column(name = "ClIntPaidAmt", nullable = false, precision = 53, scale = 0)
	public double getClIntPaidAmt() {
		return this.clIntPaidAmt;
	}

	public void setClIntPaidAmt(double clIntPaidAmt) {
		this.clIntPaidAmt = clIntPaidAmt;
	}

	@Column(name = "ClIntRate", nullable = false, precision = 53, scale = 0)
	public double getClIntRate() {
		return this.clIntRate;
	}

	public void setClIntRate(double clIntRate) {
		this.clIntRate = clIntRate;
	}

	@Column(name = "ClNotionalRate", nullable = false, precision = 53, scale = 0)
	public double getClNotionalRate() {
		return this.clNotionalRate;
	}

	public void setClNotionalRate(double clNotionalRate) {
		this.clNotionalRate = clNotionalRate;
	}

	@Column(name = "NoCertPrint", nullable = false)
	public byte getNoCertPrint() {
		return this.noCertPrint;
	}

	public void setNoCertPrint(byte noCertPrint) {
		this.noCertPrint = noCertPrint;
	}

	@Column(name = "MaskRecieptNo", nullable = false, length = 10)
	public String getMaskRecieptNo() {
		return this.maskRecieptNo;
	}

	public void setMaskRecieptNo(String maskRecieptNo) {
		this.maskRecieptNo = maskRecieptNo;
	}

	@Column(name = "PrnSrNo", nullable = false)
	public int getPrnSrNo() {
		return this.prnSrNo;
	}

	public void setPrnSrNo(int prnSrNo) {
		this.prnSrNo = prnSrNo;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ClosedDate", nullable = false, length = 23)
	public Date getClosedDate() {
		return this.closedDate;
	}

	public void setClosedDate(Date closedDate) {
		this.closedDate = closedDate;
	}

	@Column(name = "Remarks", nullable = false, length = 50)
	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Column(name = "InClBalFcy", nullable = false, precision = 53, scale = 0)
	public double getInClBalFcy() {
		return this.inClBalFcy;
	}

	public void setInClBalFcy(double inClBalFcy) {
		this.inClBalFcy = inClBalFcy;
	}

	@Column(name = "UnClBalFcy", nullable = false, precision = 53, scale = 0)
	public double getUnClBalFcy() {
		return this.unClBalFcy;
	}

	public void setUnClBalFcy(double unClBalFcy) {
		this.unClBalFcy = unClBalFcy;
	}

	@Column(name = "MainBalFcy", nullable = false, precision = 53, scale = 0)
	public double getMainBalFcy() {
		return this.mainBalFcy;
	}

	public void setMainBalFcy(double mainBalFcy) {
		this.mainBalFcy = mainBalFcy;
	}

	@Column(name = "IntPrvdAmtFcy", nullable = false, precision = 53, scale = 0)
	public double getIntPrvdAmtFcy() {
		return this.intPrvdAmtFcy;
	}

	public void setIntPrvdAmtFcy(double intPrvdAmtFcy) {
		this.intPrvdAmtFcy = intPrvdAmtFcy;
	}

	@Column(name = "IntPaidAmtFcy", nullable = false, precision = 53, scale = 0)
	public double getIntPaidAmtFcy() {
		return this.intPaidAmtFcy;
	}

	public void setIntPaidAmtFcy(double intPaidAmtFcy) {
		this.intPaidAmtFcy = intPaidAmtFcy;
	}

	@Column(name = "TdsAmtFcy", nullable = false, precision = 53, scale = 0)
	public double getTdsAmtFcy() {
		return this.tdsAmtFcy;
	}

	public void setTdsAmtFcy(double tdsAmtFcy) {
		this.tdsAmtFcy = tdsAmtFcy;
	}

	@Column(name = "MainBalLcy", nullable = false, precision = 53, scale = 0)
	public double getMainBalLcy() {
		return this.mainBalLcy;
	}

	public void setMainBalLcy(double mainBalLcy) {
		this.mainBalLcy = mainBalLcy;
	}

	@Column(name = "IntPrvdAmtLcy", nullable = false, precision = 53, scale = 0)
	public double getIntPrvdAmtLcy() {
		return this.intPrvdAmtLcy;
	}

	public void setIntPrvdAmtLcy(double intPrvdAmtLcy) {
		this.intPrvdAmtLcy = intPrvdAmtLcy;
	}

	@Column(name = "IntPaidAmtLcy", nullable = false, precision = 53, scale = 0)
	public double getIntPaidAmtLcy() {
		return this.intPaidAmtLcy;
	}

	public void setIntPaidAmtLcy(double intPaidAmtLcy) {
		this.intPaidAmtLcy = intPaidAmtLcy;
	}

	@Column(name = "TdsAmtLcy", nullable = false, precision = 53, scale = 0)
	public double getTdsAmtLcy() {
		return this.tdsAmtLcy;
	}

	public void setTdsAmtLcy(double tdsAmtLcy) {
		this.tdsAmtLcy = tdsAmtLcy;
	}

	@Column(name = "TdsYN", nullable = false, length = 1)
	public char getTdsYn() {
		return this.tdsYn;
	}

	public void setTdsYn(char tdsYn) {
		this.tdsYn = tdsYn;
	}

	@Column(name = "TdsReasonCd", nullable = false)
	public short getTdsReasonCd() {
		return this.tdsReasonCd;
	}

	public void setTdsReasonCd(short tdsReasonCd) {
		this.tdsReasonCd = tdsReasonCd;
	}

	@Column(name = "AccPrvdAmtFcy", nullable = false, precision = 53, scale = 0)
	public double getAccPrvdAmtFcy() {
		return this.accPrvdAmtFcy;
	}

	public void setAccPrvdAmtFcy(double accPrvdAmtFcy) {
		this.accPrvdAmtFcy = accPrvdAmtFcy;
	}

	@Column(name = "AccPrvdAmtLcy", nullable = false, precision = 53, scale = 0)
	public double getAccPrvdAmtLcy() {
		return this.accPrvdAmtLcy;
	}

	public void setAccPrvdAmtLcy(double accPrvdAmtLcy) {
		this.accPrvdAmtLcy = accPrvdAmtLcy;
	}

	@Column(name = "NoticeType", nullable = false)
	public byte getNoticeType() {
		return this.noticeType;
	}

	public void setNoticeType(byte noticeType) {
		this.noticeType = noticeType;
	}

	@Column(name = "SourceOfFunds", nullable = false)
	public byte getSourceOfFunds() {
		return this.sourceOfFunds;
	}

	public void setSourceOfFunds(byte sourceOfFunds) {
		this.sourceOfFunds = sourceOfFunds;
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

	@Column(name = "TrfrLBrCode", nullable = false)
	public int getTrfrLbrCode() {
		return this.trfrLbrCode;
	}

	public void setTrfrLbrCode(int trfrLbrCode) {
		this.trfrLbrCode = trfrLbrCode;
	}

	@Column(name = "LastAmtPaid", nullable = false, precision = 53, scale = 0)
	public double getLastAmtPaid() {
		return this.lastAmtPaid;
	}

	public void setLastAmtPaid(double lastAmtPaid) {
		this.lastAmtPaid = lastAmtPaid;
	}

	@Column(name = "TotAmtPaid", nullable = false, precision = 53, scale = 0)
	public double getTotAmtPaid() {
		return this.totAmtPaid;
	}

	public void setTotAmtPaid(double totAmtPaid) {
		this.totAmtPaid = totAmtPaid;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LastUnitPaidDt", nullable = false, length = 23)
	public Date getLastUnitPaidDt() {
		return this.lastUnitPaidDt;
	}

	public void setLastUnitPaidDt(Date lastUnitPaidDt) {
		this.lastUnitPaidDt = lastUnitPaidDt;
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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LockEndDate", nullable = false, length = 23)
	public Date getLockEndDate() {
		return this.lockEndDate;
	}

	public void setLockEndDate(Date lockEndDate) {
		this.lockEndDate = lockEndDate;
	}

	@Column(name = "IntPayAmt", nullable = false, precision = 53, scale = 0)
	public double getIntPayAmt() {
		return this.intPayAmt;
	}

	public void setIntPayAmt(double intPayAmt) {
		this.intPayAmt = intPayAmt;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "NextPayOutDt", nullable = false, length = 23)
	public Date getNextPayOutDt() {
		return this.nextPayOutDt;
	}

	public void setNextPayOutDt(Date nextPayOutDt) {
		this.nextPayOutDt = nextPayOutDt;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LastPayOutDate", nullable = false, length = 23)
	public Date getLastPayOutDate() {
		return this.lastPayOutDate;
	}

	public void setLastPayOutDate(Date lastPayOutDate) {
		this.lastPayOutDate = lastPayOutDate;
	}

	@Transient
	public SimpleDateFormat getSdf() {
		return sdf;
	}

	public void setSdf(SimpleDateFormat sdf) {
		this.sdf = sdf;
	}
	@Transient
	public Double getTdsprojected() {
		return tdsprojected;
	}

	public void setTdsprojected(Double tdsprojected) {
		this.tdsprojected = tdsprojected;
	}
	@Transient
	public Double getIntprojected() {
		return intprojected;
	}

	public void setIntprojected(Double intprojected) {
		this.intprojected = intprojected;
	}
	@Transient
	public Double getTdsDefAmt() {
		return tdsDefAmt;
	}

	public void setTdsDefAmt(Double tdsDefAmt) {
		this.tdsDefAmt = tdsDefAmt;
	}
	@Transient
	public Date getTdsDate() {
		return tdsDate;
	}

	public void setTdsDate(Date tdsDate) {
		this.tdsDate = tdsDate;
	}
	@Transient
	public Double getTdsRate() {
		return tdsRate;
	}

	public void setTdsRate(Double tdsRate) {
		this.tdsRate = tdsRate;
	}
	@Transient
	public Double getIntprovision() {
		return intprovision;
	}

	public void setIntprovision(Double intprovision) {
		this.intprovision = intprovision;
	}
	@Transient
	public Double getTotalRate() {
		return totalRate;
	}

	public void setTotalRate(Double totalRate) {
		this.totalRate = totalRate;
	}
	@Transient
	public Double getIntrateCalc() {
		return intrateCalc;
	}

	public void setIntrateCalc(Double intrateCalc) {
		this.intrateCalc = intrateCalc;
	}
	@Transient
	public Double getIntEarnedAmount() {
		return intEarnedAmount;
	}

	public void setIntEarnedAmount(Double intEarnedAmount) {
		this.intEarnedAmount = intEarnedAmount;
	}
	@Transient
	public String getRenewalAmtWords() {
		return renewalAmtWords;
	}

	public void setRenewalAmtWords(String renewalAmtWords) {
		this.renewalAmtWords = renewalAmtWords;
	}
	@Transient
	public D009500 getCurrentProjection() {
		return currentProjection;
	}

	public void setCurrentProjection(D009500 currentProjection) {
		this.currentProjection = currentProjection;
	}
	@Transient
	public CentrelisedBrwiseCustTDSFile getCentrelisedBrwiseCustTDSFile() {
		return centrelisedBrwiseCustTDSFile;
	}

	public void setCentrelisedBrwiseCustTDSFile(CentrelisedBrwiseCustTDSFile centrelisedBrwiseCustTDSFile) {
		this.centrelisedBrwiseCustTDSFile = centrelisedBrwiseCustTDSFile;
	}

	@Override
	public String toString()
	{
		return String.format("%03d", id.getLbrCode())+"~"+id.getPrdAcctId().substring(24, 32)+"~"+
		sdf.format(certDate)+"~"+sdf.format(asOffdate)+"~"+sdf.format(matDate)+"~"+matVal+"~"+instOrPrincAmt+"~"+
				totalLien;
	}
}
