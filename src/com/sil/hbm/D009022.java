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

@Entity
@Table(name = "D009022")
@DynamicInsert
@DynamicUpdate
public class D009022 implements java.io.Serializable {

	private static final long serialVersionUID = -59864764065984796L;
	private D009022Id id;
	private String nameTitle;
	private String longName;
	private String curCd;
	private int ledgerNo;
	private String openUser;
	private Date dateOpen;
	private String closedUser;
	private Date dateClosed;
	private byte acctType;
	private byte acctStat;
	private byte minorType;
	private byte modeOprn;
	private double intOffset;
	private double intTaxOffset;
	private double penalOffSet;
	private byte freezeType;
	private byte frzReasonCd;
	private int custNo;
	private String splInstr1;
	private String splInstr2;
	private Date dtOfBirth;
	private String fcyScheme;
	private double inClrgBalFcy;
	private double unClrEffFcy;
	private double shdClrBalFcy;
	private double shdTotBalFcy;
	private double actClrBalFcy;
	private double actTotBalFcy;
	private double actTotBalLcy;
	private double totalLienFcy;
	private double chgHoldAmtFcy;
	private Date lastDrDate;
	private Date lastCrDate;
	private Date lastCustDrDate;
	private Date lastCustCrDate;
	private char chqBookYn;
	private char dpYn;
	private Date lastIntAppDate;
	private char plrLinkYn;
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
	private int mstrAuthMask;
	private double flexiBalance;
	private double flexiLienBal;
	private String docFileNo;
	private short custAccessCat;

	public D009022() {
	}

	public D009022(D009022Id id, String nameTitle, String longName, String curCd, int ledgerNo, String openUser,
			Date dateOpen, String closedUser, Date dateClosed, byte acctType, byte acctStat, byte minorType,
			byte modeOprn, double intOffset, double intTaxOffset, double penalOffSet, byte freezeType, byte frzReasonCd,
			int custNo, String splInstr1, String splInstr2, Date dtOfBirth, String fcyScheme, double inClrgBalFcy,
			double unClrEffFcy, double shdClrBalFcy, double shdTotBalFcy, double actClrBalFcy, double actTotBalFcy,
			double actTotBalLcy, double totalLienFcy, double chgHoldAmtFcy, Date lastDrDate, Date lastCrDate,
			Date lastCustDrDate, Date lastCustCrDate, char chqBookYn, char dpYn, Date lastIntAppDate, char plrLinkYn,
			int dbtrAddMk, int dbtrAddMb, short dbtrAddMs, Date dbtrAddMd, Date dbtrAddMt, int dbtrAddCk, int dbtrAddCb,
			short dbtrAddCs, Date dbtrAddCd, Date dbtrAddCt, int dbtrLupdMk, int dbtrLupdMb, short dbtrLupdMs,
			Date dbtrLupdMd, Date dbtrLupdMt, int dbtrLupdCk, int dbtrLupdCb, short dbtrLupdCs, Date dbtrLupdCd,
			Date dbtrLupdCt, short dbtrTauthDone, byte dbtrRecStat, byte dbtrAuthDone, byte dbtrAuthNeeded,
			short dbtrUpdtChkId, int dbtrLhisTrnNo, int mstrAuthMask, double flexiBalance, double flexiLienBal,
			String docFileNo, short custAccessCat) {
		this.id = id;
		this.nameTitle = nameTitle;
		this.longName = longName;
		this.curCd = curCd;
		this.ledgerNo = ledgerNo;
		this.openUser = openUser;
		this.dateOpen = dateOpen;
		this.closedUser = closedUser;
		this.dateClosed = dateClosed;
		this.acctType = acctType;
		this.acctStat = acctStat;
		this.minorType = minorType;
		this.modeOprn = modeOprn;
		this.intOffset = intOffset;
		this.intTaxOffset = intTaxOffset;
		this.penalOffSet = penalOffSet;
		this.freezeType = freezeType;
		this.frzReasonCd = frzReasonCd;
		this.custNo = custNo;
		this.splInstr1 = splInstr1;
		this.splInstr2 = splInstr2;
		this.dtOfBirth = dtOfBirth;
		this.fcyScheme = fcyScheme;
		this.inClrgBalFcy = inClrgBalFcy;
		this.unClrEffFcy = unClrEffFcy;
		this.shdClrBalFcy = shdClrBalFcy;
		this.shdTotBalFcy = shdTotBalFcy;
		this.actClrBalFcy = actClrBalFcy;
		this.actTotBalFcy = actTotBalFcy;
		this.actTotBalLcy = actTotBalLcy;
		this.totalLienFcy = totalLienFcy;
		this.chgHoldAmtFcy = chgHoldAmtFcy;
		this.lastDrDate = lastDrDate;
		this.lastCrDate = lastCrDate;
		this.lastCustDrDate = lastCustDrDate;
		this.lastCustCrDate = lastCustCrDate;
		this.chqBookYn = chqBookYn;
		this.dpYn = dpYn;
		this.lastIntAppDate = lastIntAppDate;
		this.plrLinkYn = plrLinkYn;
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
		this.mstrAuthMask = mstrAuthMask;
		this.flexiBalance = flexiBalance;
		this.flexiLienBal = flexiLienBal;
		this.docFileNo = docFileNo;
		this.custAccessCat = custAccessCat;
	}

	@EmbeddedId

	@AttributeOverrides({ @AttributeOverride(name = "lbrCode", column = @Column(name = "LBrCode", nullable = false)),
			@AttributeOverride(name = "prdAcctId", column = @Column(name = "PrdAcctId", nullable = false, length = 32)) })
	public D009022Id getId() {
		return this.id;
	}

	public void setId(D009022Id id) {
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

	@Column(name = "LedgerNo", nullable = false)
	public int getLedgerNo() {
		return this.ledgerNo;
	}

	public void setLedgerNo(int ledgerNo) {
		this.ledgerNo = ledgerNo;
	}

	@Column(name = "OpenUser", nullable = false, length = 6)
	public String getOpenUser() {
		return this.openUser;
	}

	public void setOpenUser(String openUser) {
		this.openUser = openUser;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DateOpen", nullable = false, length = 23)
	public Date getDateOpen() {
		return this.dateOpen;
	}

	public void setDateOpen(Date dateOpen) {
		this.dateOpen = dateOpen;
	}

	@Column(name = "ClosedUser", nullable = false, length = 6)
	public String getClosedUser() {
		return this.closedUser;
	}

	public void setClosedUser(String closedUser) {
		this.closedUser = closedUser;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DateClosed", nullable = false, length = 23)
	public Date getDateClosed() {
		return this.dateClosed;
	}

	public void setDateClosed(Date dateClosed) {
		this.dateClosed = dateClosed;
	}

	@Column(name = "AcctType", nullable = false)
	public byte getAcctType() {
		return this.acctType;
	}

	public void setAcctType(byte acctType) {
		this.acctType = acctType;
	}

	@Column(name = "AcctStat", nullable = false)
	public byte getAcctStat() {
		return this.acctStat;
	}

	public void setAcctStat(byte acctStat) {
		this.acctStat = acctStat;
	}

	@Column(name = "MinorType", nullable = false)
	public byte getMinorType() {
		return this.minorType;
	}

	public void setMinorType(byte minorType) {
		this.minorType = minorType;
	}

	@Column(name = "ModeOprn", nullable = false)
	public byte getModeOprn() {
		return this.modeOprn;
	}

	public void setModeOprn(byte modeOprn) {
		this.modeOprn = modeOprn;
	}

	@Column(name = "IntOffset", nullable = false, precision = 53, scale = 0)
	public double getIntOffset() {
		return this.intOffset;
	}

	public void setIntOffset(double intOffset) {
		this.intOffset = intOffset;
	}

	@Column(name = "IntTaxOffset", nullable = false, precision = 53, scale = 0)
	public double getIntTaxOffset() {
		return this.intTaxOffset;
	}

	public void setIntTaxOffset(double intTaxOffset) {
		this.intTaxOffset = intTaxOffset;
	}

	@Column(name = "PenalOffSet", nullable = false, precision = 53, scale = 0)
	public double getPenalOffSet() {
		return this.penalOffSet;
	}

	public void setPenalOffSet(double penalOffSet) {
		this.penalOffSet = penalOffSet;
	}

	@Column(name = "FreezeType", nullable = false)
	public byte getFreezeType() {
		return this.freezeType;
	}

	public void setFreezeType(byte freezeType) {
		this.freezeType = freezeType;
	}

	@Column(name = "FrzReasonCd", nullable = false)
	public byte getFrzReasonCd() {
		return this.frzReasonCd;
	}

	public void setFrzReasonCd(byte frzReasonCd) {
		this.frzReasonCd = frzReasonCd;
	}

	@Column(name = "CustNo", nullable = false)
	public int getCustNo() {
		return this.custNo;
	}

	public void setCustNo(int custNo) {
		this.custNo = custNo;
	}

	@Column(name = "SplInstr1", nullable = false, length = 60)
	public String getSplInstr1() {
		return this.splInstr1;
	}

	public void setSplInstr1(String splInstr1) {
		this.splInstr1 = splInstr1;
	}

	@Column(name = "SplInstr2", nullable = false, length = 60)
	public String getSplInstr2() {
		return this.splInstr2;
	}

	public void setSplInstr2(String splInstr2) {
		this.splInstr2 = splInstr2;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DtOfBirth", nullable = false, length = 23)
	public Date getDtOfBirth() {
		return this.dtOfBirth;
	}

	public void setDtOfBirth(Date dtOfBirth) {
		this.dtOfBirth = dtOfBirth;
	}

	@Column(name = "FcyScheme", nullable = false, length = 4)
	public String getFcyScheme() {
		return this.fcyScheme;
	}

	public void setFcyScheme(String fcyScheme) {
		this.fcyScheme = fcyScheme;
	}

	@Column(name = "InClrgBalFcy", nullable = false, precision = 53, scale = 0)
	public double getInClrgBalFcy() {
		return this.inClrgBalFcy;
	}

	public void setInClrgBalFcy(double inClrgBalFcy) {
		this.inClrgBalFcy = inClrgBalFcy;
	}

	@Column(name = "UnClrEffFcy", nullable = false, precision = 53, scale = 0)
	public double getUnClrEffFcy() {
		return this.unClrEffFcy;
	}

	public void setUnClrEffFcy(double unClrEffFcy) {
		this.unClrEffFcy = unClrEffFcy;
	}

	@Column(name = "ShdClrBalFcy", nullable = false, precision = 53, scale = 0)
	public double getShdClrBalFcy() {
		return this.shdClrBalFcy;
	}

	public void setShdClrBalFcy(double shdClrBalFcy) {
		this.shdClrBalFcy = shdClrBalFcy;
	}

	@Column(name = "ShdTotBalFcy", nullable = false, precision = 53, scale = 0)
	public double getShdTotBalFcy() {
		return this.shdTotBalFcy;
	}

	public void setShdTotBalFcy(double shdTotBalFcy) {
		this.shdTotBalFcy = shdTotBalFcy;
	}

	@Column(name = "ActClrBalFcy", nullable = false, precision = 53, scale = 0)
	public double getActClrBalFcy() {
		return this.actClrBalFcy;
	}

	public void setActClrBalFcy(double actClrBalFcy) {
		this.actClrBalFcy = actClrBalFcy;
	}

	@Column(name = "ActTotBalFcy", nullable = false, precision = 53, scale = 0)
	public double getActTotBalFcy() {
		return this.actTotBalFcy;
	}

	public void setActTotBalFcy(double actTotBalFcy) {
		this.actTotBalFcy = actTotBalFcy;
	}

	@Column(name = "ActTotBalLcy", nullable = false, precision = 53, scale = 0)
	public double getActTotBalLcy() {
		return this.actTotBalLcy;
	}

	public void setActTotBalLcy(double actTotBalLcy) {
		this.actTotBalLcy = actTotBalLcy;
	}

	@Column(name = "TotalLienFcy", nullable = false, precision = 53, scale = 0)
	public double getTotalLienFcy() {
		return this.totalLienFcy;
	}

	public void setTotalLienFcy(double totalLienFcy) {
		this.totalLienFcy = totalLienFcy;
	}

	@Column(name = "ChgHoldAmtFcy", nullable = false, precision = 53, scale = 0)
	public double getChgHoldAmtFcy() {
		return this.chgHoldAmtFcy;
	}

	public void setChgHoldAmtFcy(double chgHoldAmtFcy) {
		this.chgHoldAmtFcy = chgHoldAmtFcy;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LastDrDate", nullable = false, length = 23)
	public Date getLastDrDate() {
		return this.lastDrDate;
	}

	public void setLastDrDate(Date lastDrDate) {
		this.lastDrDate = lastDrDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LastCrDate", nullable = false, length = 23)
	public Date getLastCrDate() {
		return this.lastCrDate;
	}

	public void setLastCrDate(Date lastCrDate) {
		this.lastCrDate = lastCrDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LastCustDrDate", nullable = false, length = 23)
	public Date getLastCustDrDate() {
		return this.lastCustDrDate;
	}

	public void setLastCustDrDate(Date lastCustDrDate) {
		this.lastCustDrDate = lastCustDrDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LastCustCrDate", nullable = false, length = 23)
	public Date getLastCustCrDate() {
		return this.lastCustCrDate;
	}

	public void setLastCustCrDate(Date lastCustCrDate) {
		this.lastCustCrDate = lastCustCrDate;
	}

	@Column(name = "ChqBookYN", nullable = false, length = 1)
	public char getChqBookYn() {
		return this.chqBookYn;
	}

	public void setChqBookYn(char chqBookYn) {
		this.chqBookYn = chqBookYn;
	}

	@Column(name = "DpYN", nullable = false, length = 1)
	public char getDpYn() {
		return this.dpYn;
	}

	public void setDpYn(char dpYn) {
		this.dpYn = dpYn;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LastIntAppDate", nullable = false, length = 23)
	public Date getLastIntAppDate() {
		return this.lastIntAppDate;
	}

	public void setLastIntAppDate(Date lastIntAppDate) {
		this.lastIntAppDate = lastIntAppDate;
	}

	@Column(name = "PlrLinkYN", nullable = false, length = 1)
	public char getPlrLinkYn() {
		return this.plrLinkYn;
	}

	public void setPlrLinkYn(char plrLinkYn) {
		this.plrLinkYn = plrLinkYn;
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

	@Column(name = "MstrAuthMask", nullable = false)
	public int getMstrAuthMask() {
		return this.mstrAuthMask;
	}

	public void setMstrAuthMask(int mstrAuthMask) {
		this.mstrAuthMask = mstrAuthMask;
	}

	@Column(name = "FlexiBalance", nullable = false, precision = 53, scale = 0)
	public double getFlexiBalance() {
		return this.flexiBalance;
	}

	public void setFlexiBalance(double flexiBalance) {
		this.flexiBalance = flexiBalance;
	}

	@Column(name = "FlexiLienBal", nullable = false, precision = 53, scale = 0)
	public double getFlexiLienBal() {
		return this.flexiLienBal;
	}

	public void setFlexiLienBal(double flexiLienBal) {
		this.flexiLienBal = flexiLienBal;
	}

	@Column(name = "DocFileNo", nullable = false, length = 15)
	public String getDocFileNo() {
		return this.docFileNo;
	}

	public void setDocFileNo(String docFileNo) {
		this.docFileNo = docFileNo;
	}

	@Column(name = "CustAccessCat", nullable = false)
	public short getCustAccessCat() {
		return this.custAccessCat;
	}

	public void setCustAccessCat(short custAccessCat) {
		this.custAccessCat = custAccessCat;
	}

	@Override
	public String toString() {
		return "D009022 [id=" + id + ", nameTitle=" + nameTitle + ", longName=" + longName + ", curCd=" + curCd
				+ ", ledgerNo=" + ledgerNo + ", openUser=" + openUser + ", dateOpen=" + dateOpen + ", closedUser="
				+ closedUser + ", dateClosed=" + dateClosed + ", acctType=" + acctType + ", acctStat=" + acctStat
				+ ", minorType=" + minorType + ", modeOprn=" + modeOprn + ", intOffset=" + intOffset + ", intTaxOffset="
				+ intTaxOffset + ", penalOffSet=" + penalOffSet + ", freezeType=" + freezeType + ", frzReasonCd="
				+ frzReasonCd + ", custNo=" + custNo + ", splInstr1=" + splInstr1 + ", splInstr2=" + splInstr2
				+ ", dtOfBirth=" + dtOfBirth + ", fcyScheme=" + fcyScheme + ", inClrgBalFcy=" + inClrgBalFcy
				+ ", unClrEffFcy=" + unClrEffFcy + ", shdClrBalFcy=" + shdClrBalFcy + ", shdTotBalFcy=" + shdTotBalFcy
				+ ", actClrBalFcy=" + actClrBalFcy + ", actTotBalFcy=" + actTotBalFcy + ", actTotBalLcy=" + actTotBalLcy
				+ ", totalLienFcy=" + totalLienFcy + ", chgHoldAmtFcy=" + chgHoldAmtFcy + ", lastDrDate=" + lastDrDate
				+ ", lastCrDate=" + lastCrDate + ", lastCustDrDate=" + lastCustDrDate + ", lastCustCrDate="
				+ lastCustCrDate + ", chqBookYn=" + chqBookYn + ", dpYn=" + dpYn + ", lastIntAppDate=" + lastIntAppDate
				+ ", plrLinkYn=" + plrLinkYn + ", dbtrAddMk=" + dbtrAddMk + ", dbtrAddMb=" + dbtrAddMb + ", dbtrAddMs="
				+ dbtrAddMs + ", dbtrAddMd=" + dbtrAddMd + ", dbtrAddMt=" + dbtrAddMt + ", dbtrAddCk=" + dbtrAddCk
				+ ", dbtrAddCb=" + dbtrAddCb + ", dbtrAddCs=" + dbtrAddCs + ", dbtrAddCd=" + dbtrAddCd + ", dbtrAddCt="
				+ dbtrAddCt + ", dbtrLupdMk=" + dbtrLupdMk + ", dbtrLupdMb=" + dbtrLupdMb + ", dbtrLupdMs=" + dbtrLupdMs
				+ ", dbtrLupdMd=" + dbtrLupdMd + ", dbtrLupdMt=" + dbtrLupdMt + ", dbtrLupdCk=" + dbtrLupdCk
				+ ", dbtrLupdCb=" + dbtrLupdCb + ", dbtrLupdCs=" + dbtrLupdCs + ", dbtrLupdCd=" + dbtrLupdCd
				+ ", dbtrLupdCt=" + dbtrLupdCt + ", dbtrTauthDone=" + dbtrTauthDone + ", dbtrRecStat=" + dbtrRecStat
				+ ", dbtrAuthDone=" + dbtrAuthDone + ", dbtrAuthNeeded=" + dbtrAuthNeeded + ", dbtrUpdtChkId="
				+ dbtrUpdtChkId + ", dbtrLhisTrnNo=" + dbtrLhisTrnNo + ", mstrAuthMask=" + mstrAuthMask
				+ ", flexiBalance=" + flexiBalance + ", flexiLienBal=" + flexiLienBal + ", docFileNo=" + docFileNo
				+ ", custAccessCat=" + custAccessCat + "]";
	}

	
}
