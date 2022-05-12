package com.sil.hbm;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the D048005 database table.
 * 
 */
@Entity
@Table(name="D048005")
//@NamedQuery(name="D048005.findAll", query="SELECT d FROM D048005 d")
public class D048005 implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ApplNo")
	private int applNo;

	@Column(name="Address1")
	private String address1;

	@Column(name="Address2")
	private String address2;

	@Column(name="Address3")
	private String address3;

	@Column(name="AdviceNo")
	private String adviceNo;

	@Column(name="Age")
	private short age;

	@Column(name="AllotDate")
	private Timestamp allotDate;

	@Column(name="ApplDate")
	private Timestamp applDate;

	@Column(name="ApplStat")
	private short applStat;

	@Column(name="BnkRefApplNo")
	private int bnkRefApplNo;

	@Column(name="BrCode")
	private int brCode;

	@Column(name="CancelDate")
	private Timestamp cancelDate;

	@Column(name="CancelReasonCd")
	private short cancelReasonCd;

	@Column(name="CashBkFolio")
	private int cashBkFolio;

	@Column(name="CasteType")
	private short casteType;

	@Column(name="CertNo")
	private String certNo;

	@Column(name="CityCd")
	private String cityCd;

	@Column(name="CustNo")
	private int custNo;

	@Column(name="DbtrAddCb")
	private int dbtrAddCb;

	@Column(name="DbtrAddCd")
	private Timestamp dbtrAddCd;

	@Column(name="DbtrAddCk")
	private int dbtrAddCk;

	@Column(name="DbtrAddCs")
	private short dbtrAddCs;

	@Column(name="DbtrAddCt")
	private Timestamp dbtrAddCt;

	@Column(name="DbtrAddMb")
	private int dbtrAddMb;

	@Column(name="DbtrAddMd")
	private Timestamp dbtrAddMd;

	@Column(name="DbtrAddMk")
	private int dbtrAddMk;

	@Column(name="DbtrAddMs")
	private short dbtrAddMs;

	@Column(name="DbtrAddMt")
	private Timestamp dbtrAddMt;

	@Column(name="DbtrAuthDone")
	private short dbtrAuthDone;

	@Column(name="DbtrAuthNeeded")
	private short dbtrAuthNeeded;

	@Column(name="DbtrLHisTrnNo")
	private int dbtrLHisTrnNo;

	@Column(name="DbtrLupdCb")
	private int dbtrLupdCb;

	@Column(name="DbtrLupdCd")
	private Timestamp dbtrLupdCd;

	@Column(name="DbtrLupdCk")
	private int dbtrLupdCk;

	@Column(name="DbtrLupdCs")
	private short dbtrLupdCs;

	@Column(name="DbtrLupdCt")
	private Timestamp dbtrLupdCt;

	@Column(name="DbtrLupdMb")
	private int dbtrLupdMb;

	@Column(name="DbtrLupdMd")
	private Timestamp dbtrLupdMd;

	@Column(name="DbtrLupdMk")
	private int dbtrLupdMk;

	@Column(name="DbtrLupdMs")
	private short dbtrLupdMs;

	@Column(name="DbtrLupdMt")
	private Timestamp dbtrLupdMt;

	@Column(name="DbtrRecStat")
	private short dbtrRecStat;

	@Column(name="DbtrTAuthDone")
	private short dbtrTAuthDone;

	@Column(name="DbtrUpdtChkId")
	private short dbtrUpdtChkId;

	@Column(name="EntryType")
	private short entryType;

	@Column(name="IntroName")
	private String introName;

	@Column(name="IntroName2")
	private String introName2;

	@Column(name="IntroRefNo")
	private String introRefNo;

	@Column(name="IntroRefNo2")
	private String introRefNo2;

	@Column(name="LedgerNo")
	private int ledgerNo;

	@Column(name="LoanPrdAcctId")
	private String loanPrdAcctId;

	@Column(name="LongName")
	private String longName;

	@Column(name="MainArea")
	private int mainArea;

	@Column(name="MemNo")
	private String memNo;

	@Column(name="MemType")
	private String memType;

	@Column(name="NameTitle")
	private String nameTitle;

	@Column(name="NoOfShares")
	private int noOfShares;

	@Column(name="PinCode")
	private String pinCode;

	@Column(name="ShrPurpose")
	private short shrPurpose;

	@Column(name="SubArea")
	private short subArea;

	@Column(name="TelNo")
	private String telNo;

	@Column(name="TotalAmtRecd")
	private double totalAmtRecd;

	public D048005() {
	}

	public int getApplNo() {
		return this.applNo;
	}

	public void setApplNo(int applNo) {
		this.applNo = applNo;
	}

	public String getAddress1() {
		return this.address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return this.address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return this.address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getAdviceNo() {
		return this.adviceNo;
	}

	public void setAdviceNo(String adviceNo) {
		this.adviceNo = adviceNo;
	}

	public short getAge() {
		return this.age;
	}

	public void setAge(short age) {
		this.age = age;
	}

	public Timestamp getAllotDate() {
		return this.allotDate;
	}

	public void setAllotDate(Timestamp allotDate) {
		this.allotDate = allotDate;
	}

	public Timestamp getApplDate() {
		return this.applDate;
	}

	public void setApplDate(Timestamp applDate) {
		this.applDate = applDate;
	}

	public short getApplStat() {
		return this.applStat;
	}

	public void setApplStat(short applStat) {
		this.applStat = applStat;
	}

	public int getBnkRefApplNo() {
		return this.bnkRefApplNo;
	}

	public void setBnkRefApplNo(int bnkRefApplNo) {
		this.bnkRefApplNo = bnkRefApplNo;
	}

	public int getBrCode() {
		return this.brCode;
	}

	public void setBrCode(int brCode) {
		this.brCode = brCode;
	}

	public Timestamp getCancelDate() {
		return this.cancelDate;
	}

	public void setCancelDate(Timestamp cancelDate) {
		this.cancelDate = cancelDate;
	}

	public short getCancelReasonCd() {
		return this.cancelReasonCd;
	}

	public void setCancelReasonCd(short cancelReasonCd) {
		this.cancelReasonCd = cancelReasonCd;
	}

	public int getCashBkFolio() {
		return this.cashBkFolio;
	}

	public void setCashBkFolio(int cashBkFolio) {
		this.cashBkFolio = cashBkFolio;
	}

	public short getCasteType() {
		return this.casteType;
	}

	public void setCasteType(short casteType) {
		this.casteType = casteType;
	}

	public String getCertNo() {
		return this.certNo;
	}

	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}

	public String getCityCd() {
		return this.cityCd;
	}

	public void setCityCd(String cityCd) {
		this.cityCd = cityCd;
	}

	public int getCustNo() {
		return this.custNo;
	}

	public void setCustNo(int custNo) {
		this.custNo = custNo;
	}

	public int getDbtrAddCb() {
		return this.dbtrAddCb;
	}

	public void setDbtrAddCb(int dbtrAddCb) {
		this.dbtrAddCb = dbtrAddCb;
	}

	public Timestamp getDbtrAddCd() {
		return this.dbtrAddCd;
	}

	public void setDbtrAddCd(Timestamp dbtrAddCd) {
		this.dbtrAddCd = dbtrAddCd;
	}

	public int getDbtrAddCk() {
		return this.dbtrAddCk;
	}

	public void setDbtrAddCk(int dbtrAddCk) {
		this.dbtrAddCk = dbtrAddCk;
	}

	public short getDbtrAddCs() {
		return this.dbtrAddCs;
	}

	public void setDbtrAddCs(short dbtrAddCs) {
		this.dbtrAddCs = dbtrAddCs;
	}

	public Timestamp getDbtrAddCt() {
		return this.dbtrAddCt;
	}

	public void setDbtrAddCt(Timestamp dbtrAddCt) {
		this.dbtrAddCt = dbtrAddCt;
	}

	public int getDbtrAddMb() {
		return this.dbtrAddMb;
	}

	public void setDbtrAddMb(int dbtrAddMb) {
		this.dbtrAddMb = dbtrAddMb;
	}

	public Timestamp getDbtrAddMd() {
		return this.dbtrAddMd;
	}

	public void setDbtrAddMd(Timestamp dbtrAddMd) {
		this.dbtrAddMd = dbtrAddMd;
	}

	public int getDbtrAddMk() {
		return this.dbtrAddMk;
	}

	public void setDbtrAddMk(int dbtrAddMk) {
		this.dbtrAddMk = dbtrAddMk;
	}

	public short getDbtrAddMs() {
		return this.dbtrAddMs;
	}

	public void setDbtrAddMs(short dbtrAddMs) {
		this.dbtrAddMs = dbtrAddMs;
	}

	public Timestamp getDbtrAddMt() {
		return this.dbtrAddMt;
	}

	public void setDbtrAddMt(Timestamp dbtrAddMt) {
		this.dbtrAddMt = dbtrAddMt;
	}

	public short getDbtrAuthDone() {
		return this.dbtrAuthDone;
	}

	public void setDbtrAuthDone(short dbtrAuthDone) {
		this.dbtrAuthDone = dbtrAuthDone;
	}

	public short getDbtrAuthNeeded() {
		return this.dbtrAuthNeeded;
	}

	public void setDbtrAuthNeeded(short dbtrAuthNeeded) {
		this.dbtrAuthNeeded = dbtrAuthNeeded;
	}

	public int getDbtrLHisTrnNo() {
		return this.dbtrLHisTrnNo;
	}

	public void setDbtrLHisTrnNo(int dbtrLHisTrnNo) {
		this.dbtrLHisTrnNo = dbtrLHisTrnNo;
	}

	public int getDbtrLupdCb() {
		return this.dbtrLupdCb;
	}

	public void setDbtrLupdCb(int dbtrLupdCb) {
		this.dbtrLupdCb = dbtrLupdCb;
	}

	public Timestamp getDbtrLupdCd() {
		return this.dbtrLupdCd;
	}

	public void setDbtrLupdCd(Timestamp dbtrLupdCd) {
		this.dbtrLupdCd = dbtrLupdCd;
	}

	public int getDbtrLupdCk() {
		return this.dbtrLupdCk;
	}

	public void setDbtrLupdCk(int dbtrLupdCk) {
		this.dbtrLupdCk = dbtrLupdCk;
	}

	public short getDbtrLupdCs() {
		return this.dbtrLupdCs;
	}

	public void setDbtrLupdCs(short dbtrLupdCs) {
		this.dbtrLupdCs = dbtrLupdCs;
	}

	public Timestamp getDbtrLupdCt() {
		return this.dbtrLupdCt;
	}

	public void setDbtrLupdCt(Timestamp dbtrLupdCt) {
		this.dbtrLupdCt = dbtrLupdCt;
	}

	public int getDbtrLupdMb() {
		return this.dbtrLupdMb;
	}

	public void setDbtrLupdMb(int dbtrLupdMb) {
		this.dbtrLupdMb = dbtrLupdMb;
	}

	public Timestamp getDbtrLupdMd() {
		return this.dbtrLupdMd;
	}

	public void setDbtrLupdMd(Timestamp dbtrLupdMd) {
		this.dbtrLupdMd = dbtrLupdMd;
	}

	public int getDbtrLupdMk() {
		return this.dbtrLupdMk;
	}

	public void setDbtrLupdMk(int dbtrLupdMk) {
		this.dbtrLupdMk = dbtrLupdMk;
	}

	public short getDbtrLupdMs() {
		return this.dbtrLupdMs;
	}

	public void setDbtrLupdMs(short dbtrLupdMs) {
		this.dbtrLupdMs = dbtrLupdMs;
	}

	public Timestamp getDbtrLupdMt() {
		return this.dbtrLupdMt;
	}

	public void setDbtrLupdMt(Timestamp dbtrLupdMt) {
		this.dbtrLupdMt = dbtrLupdMt;
	}

	public short getDbtrRecStat() {
		return this.dbtrRecStat;
	}

	public void setDbtrRecStat(short dbtrRecStat) {
		this.dbtrRecStat = dbtrRecStat;
	}

	public short getDbtrTAuthDone() {
		return this.dbtrTAuthDone;
	}

	public void setDbtrTAuthDone(short dbtrTAuthDone) {
		this.dbtrTAuthDone = dbtrTAuthDone;
	}

	public short getDbtrUpdtChkId() {
		return this.dbtrUpdtChkId;
	}

	public void setDbtrUpdtChkId(short dbtrUpdtChkId) {
		this.dbtrUpdtChkId = dbtrUpdtChkId;
	}

	public short getEntryType() {
		return this.entryType;
	}

	public void setEntryType(short entryType) {
		this.entryType = entryType;
	}

	public String getIntroName() {
		return this.introName;
	}

	public void setIntroName(String introName) {
		this.introName = introName;
	}

	public String getIntroName2() {
		return this.introName2;
	}

	public void setIntroName2(String introName2) {
		this.introName2 = introName2;
	}

	public String getIntroRefNo() {
		return this.introRefNo;
	}

	public void setIntroRefNo(String introRefNo) {
		this.introRefNo = introRefNo;
	}

	public String getIntroRefNo2() {
		return this.introRefNo2;
	}

	public void setIntroRefNo2(String introRefNo2) {
		this.introRefNo2 = introRefNo2;
	}

	public int getLedgerNo() {
		return this.ledgerNo;
	}

	public void setLedgerNo(int ledgerNo) {
		this.ledgerNo = ledgerNo;
	}

	public String getLoanPrdAcctId() {
		return this.loanPrdAcctId;
	}

	public void setLoanPrdAcctId(String loanPrdAcctId) {
		this.loanPrdAcctId = loanPrdAcctId;
	}

	public String getLongName() {
		return this.longName;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	public int getMainArea() {
		return this.mainArea;
	}

	public void setMainArea(int mainArea) {
		this.mainArea = mainArea;
	}

	public String getMemNo() {
		return this.memNo;
	}

	public void setMemNo(String memNo) {
		this.memNo = memNo;
	}

	public String getMemType() {
		return this.memType;
	}

	public void setMemType(String memType) {
		this.memType = memType;
	}

	public String getNameTitle() {
		return this.nameTitle;
	}

	public void setNameTitle(String nameTitle) {
		this.nameTitle = nameTitle;
	}

	public int getNoOfShares() {
		return this.noOfShares;
	}

	public void setNoOfShares(int noOfShares) {
		this.noOfShares = noOfShares;
	}

	public String getPinCode() {
		return this.pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	public short getShrPurpose() {
		return this.shrPurpose;
	}

	public void setShrPurpose(short shrPurpose) {
		this.shrPurpose = shrPurpose;
	}

	public short getSubArea() {
		return this.subArea;
	}

	public void setSubArea(short subArea) {
		this.subArea = subArea;
	}

	public String getTelNo() {
		return this.telNo;
	}

	public void setTelNo(String telNo) {
		this.telNo = telNo;
	}

	public double getTotalAmtRecd() {
		return this.totalAmtRecd;
	}

	public void setTotalAmtRecd(double totalAmtRecd) {
		this.totalAmtRecd = totalAmtRecd;
	}

}