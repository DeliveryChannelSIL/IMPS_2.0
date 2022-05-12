package com.sil.hbm;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.sql.Timestamp;


/**
 * The persistent class for the D009193 database table.
 * 
 */
@Entity
@Table(name="D009193")
@DynamicUpdate
@DynamicInsert
@NamedQuery(name="KYCMaster.findAll", query="SELECT k FROM KYCMaster k")
public class KYCMaster implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="CustNo")
	private int custNo;

	@Column(name="AcctTypeDesc")
	private String acctTypeDesc;

	@Column(name="Antiact")
	private double antiact;

	private String ATurnOver;

	private short ATurnOverCode;

	@Column(name="BnkBrName")
	private short bnkBrName;

	@Column(name="BusinessType")
	private short businessType;

	@Column(name="CkycAssDate")
	private Timestamp ckycAssDate;

	@Column(name="CkycNo")
	private String ckycNo;

	@Column(name="CkycRecordId")
	private Integer ckycRecordId;

	@Column(name="Clientloc")
	private short clientloc;

	@Column(name="CntryCd")
	private String cntryCd;

	@Column(name="CreditCrdYN")
	private String creditCrdYN;

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

	@Column(name="DealOthBnk")
	private String dealOthBnk;

	@Column(name="DOB")
	private Timestamp dob;

	@Column(name="EduQual")
	private String eduQual;

	@Column(name="EduQualDesc")
	private String eduQualDesc;

	@Column(name="EntryDate")
	private Timestamp entryDate;

	@Column(name="FathHusbName")
	private String fathHusbName;

	@Column(name="FundSource")
	private String fundSource;

	@Column(name="HighRiskCountYN")
	private String highRiskCountYN;

	@Column(name="IntermediateYN")
	private String intermediateYN;

	@Column(name="IntermedType")
	private String intermedType;

	@Column(name="MaritalStatus")
	private short maritalStatus;

	@Column(name="MnthlyIncome")
	private short mnthlyIncome;

	@Column(name="NextKYCDueDate")
	private Timestamp nextKYCDueDate;

	@Column(name="NoofTrans")
	private short noofTrans;

	@Column(name="NoOfVisitInAbroad")
	private short noOfVisitInAbroad;

	@Column(name="NPOYN")
	private String npoyn;

	@Column(name="Occupation")
	private String occupation;

	@Column(name="OverallBkg")
	private String overallBkg;

	@Column(name="Place")
	private String place;

	@Column(name="PoliticalAffiYN")
	private String politicalAffiYN;

	@Column(name="ProQual")
	private String proQual;

	@Column(name="ProQualDesc")
	private String proQualDesc;

	@Column(name="Remarks")
	private String remarks;

	@Column(name="RiskCatDate")
	private Timestamp riskCatDate;

	@Column(name="SelfEmplyd")
	private short selfEmplyd;

	@Column(name="SourceOfFunds")
	private short sourceOfFunds;

	@Column(name="SpouseEduQual")
	private String spouseEduQual;

	@Column(name="SpouseOccupation")
	private String spouseOccupation;

	@Column(name="SpouseProQual")
	private String spouseProQual;

	@Column(name="TotAsset")
	private double totAsset;

	@Column(name="WillfulDefa")
	private String willfulDefa;

	public KYCMaster() {
	}

	
	public KYCMaster(int custNo, String acctTypeDesc, double antiact, String aTurnOver, short aTurnOverCode,
			short bnkBrName, short businessType, Timestamp ckycAssDate, String ckycNo, Integer ckycRecordId,
			short clientloc, String cntryCd, String creditCrdYN, int dbtrAddCb, Timestamp dbtrAddCd, int dbtrAddCk,
			short dbtrAddCs, Timestamp dbtrAddCt, int dbtrAddMb, Timestamp dbtrAddMd, int dbtrAddMk, short dbtrAddMs,
			Timestamp dbtrAddMt, short dbtrAuthDone, short dbtrAuthNeeded, int dbtrLHisTrnNo, int dbtrLupdCb,
			Timestamp dbtrLupdCd, int dbtrLupdCk, short dbtrLupdCs, Timestamp dbtrLupdCt, int dbtrLupdMb,
			Timestamp dbtrLupdMd, int dbtrLupdMk, short dbtrLupdMs, Timestamp dbtrLupdMt, short dbtrRecStat,
			short dbtrTAuthDone, short dbtrUpdtChkId, String dealOthBnk, Timestamp dob, String eduQual,
			String eduQualDesc, Timestamp entryDate, String fathHusbName, String fundSource, String highRiskCountYN,
			String intermediateYN, String intermedType, short maritalStatus, short mnthlyIncome,
			Timestamp nextKYCDueDate, short noofTrans, short noOfVisitInAbroad, String npoyn, String occupation,
			String overallBkg, String place, String politicalAffiYN, String proQual, String proQualDesc, String remarks,
			Timestamp riskCatDate, short selfEmplyd, short sourceOfFunds, String spouseEduQual, String spouseOccupation,
			String spouseProQual, double totAsset, String willfulDefa) {
		super();
		this.custNo = custNo;
		this.acctTypeDesc = acctTypeDesc;
		this.antiact = antiact;
		ATurnOver = aTurnOver;
		ATurnOverCode = aTurnOverCode;
		this.bnkBrName = bnkBrName;
		this.businessType = businessType;
		this.ckycAssDate = ckycAssDate;
		this.ckycNo = ckycNo;
		this.ckycRecordId = ckycRecordId;
		this.clientloc = clientloc;
		this.cntryCd = cntryCd;
		this.creditCrdYN = creditCrdYN;
		this.dbtrAddCb = dbtrAddCb;
		this.dbtrAddCd = dbtrAddCd;
		this.dbtrAddCk = dbtrAddCk;
		this.dbtrAddCs = dbtrAddCs;
		this.dbtrAddCt = dbtrAddCt;
		this.dbtrAddMb = dbtrAddMb;
		this.dbtrAddMd = dbtrAddMd;
		this.dbtrAddMk = dbtrAddMk;
		this.dbtrAddMs = dbtrAddMs;
		this.dbtrAddMt = dbtrAddMt;
		this.dbtrAuthDone = dbtrAuthDone;
		this.dbtrAuthNeeded = dbtrAuthNeeded;
		this.dbtrLHisTrnNo = dbtrLHisTrnNo;
		this.dbtrLupdCb = dbtrLupdCb;
		this.dbtrLupdCd = dbtrLupdCd;
		this.dbtrLupdCk = dbtrLupdCk;
		this.dbtrLupdCs = dbtrLupdCs;
		this.dbtrLupdCt = dbtrLupdCt;
		this.dbtrLupdMb = dbtrLupdMb;
		this.dbtrLupdMd = dbtrLupdMd;
		this.dbtrLupdMk = dbtrLupdMk;
		this.dbtrLupdMs = dbtrLupdMs;
		this.dbtrLupdMt = dbtrLupdMt;
		this.dbtrRecStat = dbtrRecStat;
		this.dbtrTAuthDone = dbtrTAuthDone;
		this.dbtrUpdtChkId = dbtrUpdtChkId;
		this.dealOthBnk = dealOthBnk;
		this.dob = dob;
		this.eduQual = eduQual;
		this.eduQualDesc = eduQualDesc;
		this.entryDate = entryDate;
		this.fathHusbName = fathHusbName;
		this.fundSource = fundSource;
		this.highRiskCountYN = highRiskCountYN;
		this.intermediateYN = intermediateYN;
		this.intermedType = intermedType;
		this.maritalStatus = maritalStatus;
		this.mnthlyIncome = mnthlyIncome;
		this.nextKYCDueDate = nextKYCDueDate;
		this.noofTrans = noofTrans;
		this.noOfVisitInAbroad = noOfVisitInAbroad;
		this.npoyn = npoyn;
		this.occupation = occupation;
		this.overallBkg = overallBkg;
		this.place = place;
		this.politicalAffiYN = politicalAffiYN;
		this.proQual = proQual;
		this.proQualDesc = proQualDesc;
		this.remarks = remarks;
		this.riskCatDate = riskCatDate;
		this.selfEmplyd = selfEmplyd;
		this.sourceOfFunds = sourceOfFunds;
		this.spouseEduQual = spouseEduQual;
		this.spouseOccupation = spouseOccupation;
		this.spouseProQual = spouseProQual;
		this.totAsset = totAsset;
		this.willfulDefa = willfulDefa;
	}


	@Id
	@Column(name="CustNo")
	public int getCustNo() {
		return this.custNo;
	}

	public void setCustNo(int custNo) {
		this.custNo = custNo;
	}

	@Column(name="AcctTypeDesc")
	public String getAcctTypeDesc() {
		return this.acctTypeDesc;
	}

	public void setAcctTypeDesc(String acctTypeDesc) {
		this.acctTypeDesc = acctTypeDesc;
	}
	@Column(name="Antiact")
	public double getAntiact() {
		return this.antiact;
	}

	public void setAntiact(double antiact) {
		this.antiact = antiact;
	}

	public String getATurnOver() {
		return this.ATurnOver;
	}

	public void setATurnOver(String ATurnOver) {
		this.ATurnOver = ATurnOver;
	}

	public short getATurnOverCode() {
		return this.ATurnOverCode;
	}

	public void setATurnOverCode(short ATurnOverCode) {
		this.ATurnOverCode = ATurnOverCode;
	}
	
	@Column(name="BnkBrName")
	public short getBnkBrName() {
		return this.bnkBrName;
	}

	public void setBnkBrName(short bnkBrName) {
		this.bnkBrName = bnkBrName;
	}
	
	@Column(name="BusinessType")
    public short getBusinessType() {
		return this.businessType;
	}

	public void setBusinessType(short businessType) {
		this.businessType = businessType;
	}
	
	@Column(name="CkycAssDate")
	public Timestamp getCkycAssDate() {
		return this.ckycAssDate;
	}

	public void setCkycAssDate(Timestamp ckycAssDate) {
		this.ckycAssDate = ckycAssDate;
	}
	
	@Column(name="CkycNo")
	public String getCkycNo() {
		return this.ckycNo;
	}

	public void setCkycNo(String ckycNo) {
		this.ckycNo = ckycNo;
	}
	@Column(name="CkycRecordId")
	public Integer getCkycRecordId() {
		return this.ckycRecordId;
	}

	public void setCkycRecordId(Integer ckycRecordId) {
		this.ckycRecordId = ckycRecordId;
	}
	
	@Column(name="Clientloc")
	public short getClientloc() {
		return this.clientloc;
	}

	public void setClientloc(short clientloc) {
		this.clientloc = clientloc;
	}
	
	@Column(name="CntryCd")
	public String getCntryCd() {
		return this.cntryCd;
	}

	public void setCntryCd(String cntryCd) {
		this.cntryCd = cntryCd;
	}
	
	@Column(name="CreditCrdYN")
	public String getCreditCrdYN() {
		return this.creditCrdYN;
	}

	public void setCreditCrdYN(String creditCrdYN) {
		this.creditCrdYN = creditCrdYN;
	}
	
	@Column(name="DbtrAddCb")
	public int getDbtrAddCb() {
		return this.dbtrAddCb;
	}

	public void setDbtrAddCb(int dbtrAddCb) {
		this.dbtrAddCb = dbtrAddCb;
	}
	@Column(name="DbtrAddCd")
	public Timestamp getDbtrAddCd() {
		return this.dbtrAddCd;
	}

	public void setDbtrAddCd(Timestamp dbtrAddCd) {
		this.dbtrAddCd = dbtrAddCd;
	}
	
	@Column(name="DbtrAddCk")
	public int getDbtrAddCk() {
		return this.dbtrAddCk;
	}

	public void setDbtrAddCk(int dbtrAddCk) {
		this.dbtrAddCk = dbtrAddCk;
	}
	
	@Column(name="DbtrAddCs")
	public short getDbtrAddCs() {
		return this.dbtrAddCs;
	}

	public void setDbtrAddCs(short dbtrAddCs) {
		this.dbtrAddCs = dbtrAddCs;
	}
	@Column(name="DbtrAddCt")
	public Timestamp getDbtrAddCt() {
		return this.dbtrAddCt;
	}

	public void setDbtrAddCt(Timestamp dbtrAddCt) {
		this.dbtrAddCt = dbtrAddCt;
	}
	@Column(name="DbtrAddMb")
	public int getDbtrAddMb() {
		return this.dbtrAddMb;
	}

	public void setDbtrAddMb(int dbtrAddMb) {
		this.dbtrAddMb = dbtrAddMb;
	}
	
	@Column(name="DbtrAddMd")
	public Timestamp getDbtrAddMd() {
		return this.dbtrAddMd;
	}

	public void setDbtrAddMd(Timestamp dbtrAddMd) {
		this.dbtrAddMd = dbtrAddMd;
	}
	
	@Column(name="DbtrAddMk")
	public int getDbtrAddMk() {
		return this.dbtrAddMk;
	}

	public void setDbtrAddMk(int dbtrAddMk) {
		this.dbtrAddMk = dbtrAddMk;
	}
	
	@Column(name="DbtrAddMs")
	public short getDbtrAddMs() {
		return this.dbtrAddMs;
	}

	public void setDbtrAddMs(short dbtrAddMs) {
		this.dbtrAddMs = dbtrAddMs;
	}
	
	@Column(name="DbtrAddMt")
	public Timestamp getDbtrAddMt() {
		return this.dbtrAddMt;
	}

	public void setDbtrAddMt(Timestamp dbtrAddMt) {
		this.dbtrAddMt = dbtrAddMt;
	}
	
	@Column(name="DbtrAuthDone")
	public short getDbtrAuthDone() {
		return this.dbtrAuthDone;
	}

	public void setDbtrAuthDone(short dbtrAuthDone) {
		this.dbtrAuthDone = dbtrAuthDone;
	}
	
	@Column(name="DbtrAuthNeeded")
	public short getDbtrAuthNeeded() {
		return this.dbtrAuthNeeded;
	}

	public void setDbtrAuthNeeded(short dbtrAuthNeeded) {
		this.dbtrAuthNeeded = dbtrAuthNeeded;
	}
	
	@Column(name="DbtrLHisTrnNo")
	public int getDbtrLHisTrnNo() {
		return this.dbtrLHisTrnNo;
	}

	public void setDbtrLHisTrnNo(int dbtrLHisTrnNo) {
		this.dbtrLHisTrnNo = dbtrLHisTrnNo;
	}
	
	@Column(name="DbtrLupdCb")
	public int getDbtrLupdCb() {
		return this.dbtrLupdCb;
	}
	
	public void setDbtrLupdCb(int dbtrLupdCb) {
		this.dbtrLupdCb = dbtrLupdCb;
	}
	@Column(name="DbtrLupdCd")
	public Timestamp getDbtrLupdCd() {
		return this.dbtrLupdCd;
	}

	public void setDbtrLupdCd(Timestamp dbtrLupdCd) {
		this.dbtrLupdCd = dbtrLupdCd;
	}
	@Column(name="DbtrLupdCk")
	public int getDbtrLupdCk() {
		return this.dbtrLupdCk;
	}

	public void setDbtrLupdCk(int dbtrLupdCk) {
		this.dbtrLupdCk = dbtrLupdCk;
	}
	
	@Column(name="DbtrLupdCs")
	public short getDbtrLupdCs() {
		return this.dbtrLupdCs;
	}

	public void setDbtrLupdCs(short dbtrLupdCs) {
		this.dbtrLupdCs = dbtrLupdCs;
	}
	@Column(name="DbtrLupdCt")
	public Timestamp getDbtrLupdCt() {
		return this.dbtrLupdCt;
	}

	public void setDbtrLupdCt(Timestamp dbtrLupdCt) {
		this.dbtrLupdCt = dbtrLupdCt;
	}
	
	@Column(name="DbtrLupdMb")
	public int getDbtrLupdMb() {
		return this.dbtrLupdMb;
	}

	public void setDbtrLupdMb(int dbtrLupdMb) {
		this.dbtrLupdMb = dbtrLupdMb;
	}
	@Column(name="DbtrLupdMd")
	public Timestamp getDbtrLupdMd() {
		return this.dbtrLupdMd;
	}

	public void setDbtrLupdMd(Timestamp dbtrLupdMd) {
		this.dbtrLupdMd = dbtrLupdMd;
	}
	
	@Column(name="DbtrLupdMk")
	public int getDbtrLupdMk() {
		return this.dbtrLupdMk;
	}

	public void setDbtrLupdMk(int dbtrLupdMk) {
		this.dbtrLupdMk = dbtrLupdMk;
	}
	@Column(name="DbtrLupdMs")
	public short getDbtrLupdMs() {
		return this.dbtrLupdMs;
	}

	public void setDbtrLupdMs(short dbtrLupdMs) {
		this.dbtrLupdMs = dbtrLupdMs;
	}
	@Column(name="DbtrLupdMt")
	public Timestamp getDbtrLupdMt() {
		return this.dbtrLupdMt;
	}

	public void setDbtrLupdMt(Timestamp dbtrLupdMt) {
		this.dbtrLupdMt = dbtrLupdMt;
	}
	
	@Column(name="DbtrRecStat")
	public short getDbtrRecStat() {
		return this.dbtrRecStat;
	}

	public void setDbtrRecStat(short dbtrRecStat) {
		this.dbtrRecStat = dbtrRecStat;
	}
	
	@Column(name="DbtrTAuthDone")
	public short getDbtrTAuthDone() {
		return this.dbtrTAuthDone;
	}

	public void setDbtrTAuthDone(short dbtrTAuthDone) {
		this.dbtrTAuthDone = dbtrTAuthDone;
	}
	
	@Column(name="DbtrUpdtChkId")
	public short getDbtrUpdtChkId() {
		return this.dbtrUpdtChkId;
	}

	public void setDbtrUpdtChkId(short dbtrUpdtChkId) {
		this.dbtrUpdtChkId = dbtrUpdtChkId;
	}
	
	@Column(name="DealOthBnk")
	public String getDealOthBnk() {
		return this.dealOthBnk;
	}

	public void setDealOthBnk(String dealOthBnk) {
		this.dealOthBnk = dealOthBnk;
	}
	@Column(name="DOB")
	public Timestamp getDob() {
		return this.dob;
	}

	public void setDob(Timestamp dob) {
		this.dob = dob;
	}
	
	@Column(name="EduQual")
	public String getEduQual() {
		return this.eduQual;
	}

	public void setEduQual(String eduQual) {
		this.eduQual = eduQual;
	}
	
	@Column(name="EduQualDesc")
	public String getEduQualDesc() {
		return this.eduQualDesc;
	}

	public void setEduQualDesc(String eduQualDesc) {
		this.eduQualDesc = eduQualDesc;
	}
	@Column(name="EntryDate")
	public Timestamp getEntryDate() {
		return this.entryDate;
	}

	public void setEntryDate(Timestamp entryDate) {
		this.entryDate = entryDate;
	}
	
	@Column(name="FathHusbName")
	public String getFathHusbName() {
		return this.fathHusbName;
	}

	public void setFathHusbName(String fathHusbName) {
		this.fathHusbName = fathHusbName;
	}
	
	@Column(name="FundSource")
	public String getFundSource() {
		return this.fundSource;
	}

	public void setFundSource(String fundSource) {
		this.fundSource = fundSource;
	}
	
	@Column(name="HighRiskCountYN")
	public String getHighRiskCountYN() {
		return this.highRiskCountYN;
	}

	public void setHighRiskCountYN(String highRiskCountYN) {
		this.highRiskCountYN = highRiskCountYN;
	}
	
	@Column(name="IntermediateYN")
	public String getIntermediateYN() {
		return this.intermediateYN;
	}

	public void setIntermediateYN(String intermediateYN) {
		this.intermediateYN = intermediateYN;
	}
	@Column(name="IntermedType")
	public String getIntermedType() {
		return this.intermedType;
	}

	public void setIntermedType(String intermedType) {
		this.intermedType = intermedType;
	}
	@Column(name="MaritalStatus")
	public short getMaritalStatus() {
		return this.maritalStatus;
	}

	public void setMaritalStatus(short maritalStatus) {
		this.maritalStatus = maritalStatus;
	}
	@Column(name="MnthlyIncome")
	public short getMnthlyIncome() {
		return this.mnthlyIncome;
	}

	public void setMnthlyIncome(short mnthlyIncome) {
		this.mnthlyIncome = mnthlyIncome;
	}
	@Column(name="NextKYCDueDate")
	public Timestamp getNextKYCDueDate() {
		return this.nextKYCDueDate;
	}

	public void setNextKYCDueDate(Timestamp nextKYCDueDate) {
		this.nextKYCDueDate = nextKYCDueDate;
	}
	@Column(name="NoofTrans")
	public short getNoofTrans() {
		return this.noofTrans;
	}

	public void setNoofTrans(short noofTrans) {
		this.noofTrans = noofTrans;
	}
	@Column(name="NoOfVisitInAbroad")
	public short getNoOfVisitInAbroad() {
		return this.noOfVisitInAbroad;
	}

	public void setNoOfVisitInAbroad(short noOfVisitInAbroad) {
		this.noOfVisitInAbroad = noOfVisitInAbroad;
	}
	@Column(name="NPOYN")
	public String getNpoyn() {
		return this.npoyn;
	}

	public void setNpoyn(String npoyn) {
		this.npoyn = npoyn;
	}
	
	@Column(name="Occupation")
	public String getOccupation() {
		return this.occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}
	
	@Column(name="OverallBkg")
	public String getOverallBkg() {
		return this.overallBkg;
	}

	public void setOverallBkg(String overallBkg) {
		this.overallBkg = overallBkg;
	}
	@Column(name="Place")
	public String getPlace() {
		return this.place;
	}

	public void setPlace(String place) {
		this.place = place;
	}
	@Column(name="PoliticalAffiYN")
	public String getPoliticalAffiYN() {
		return this.politicalAffiYN;
	}

	public void setPoliticalAffiYN(String politicalAffiYN) {
		this.politicalAffiYN = politicalAffiYN;
	}
	
	@Column(name="ProQual")
	public String getProQual() {
		return this.proQual;
	}

	public void setProQual(String proQual) {
		this.proQual = proQual;
	}
	
	@Column(name="ProQualDesc")
	public String getProQualDesc() {
		return this.proQualDesc;
	}

	public void setProQualDesc(String proQualDesc) {
		this.proQualDesc = proQualDesc;
	}
	
	@Column(name="Remarks")
	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	@Column(name="RiskCatDate")
	public Timestamp getRiskCatDate() {
		return this.riskCatDate;
	}

	public void setRiskCatDate(Timestamp riskCatDate) {
		this.riskCatDate = riskCatDate;
	}
	@Column(name="SelfEmplyd")
	public short getSelfEmplyd() {
		return this.selfEmplyd;
	}

	public void setSelfEmplyd(short selfEmplyd) {
		this.selfEmplyd = selfEmplyd;
	}
	@Column(name="SourceOfFunds")
	public short getSourceOfFunds() {
		return this.sourceOfFunds;
	}

	public void setSourceOfFunds(short sourceOfFunds) {
		this.sourceOfFunds = sourceOfFunds;
	}
	@Column(name="SpouseEduQual")
	public String getSpouseEduQual() {
		return this.spouseEduQual;
	}

	public void setSpouseEduQual(String spouseEduQual) {
		this.spouseEduQual = spouseEduQual;
	}
	
	@Column(name="SpouseOccupation")
	public String getSpouseOccupation() {
		return this.spouseOccupation;
	}

	public void setSpouseOccupation(String spouseOccupation) {
		this.spouseOccupation = spouseOccupation;
	}
	
	@Column(name="SpouseProQual")
	public String getSpouseProQual() {
		return this.spouseProQual;
	}

	public void setSpouseProQual(String spouseProQual) {
		this.spouseProQual = spouseProQual;
	}
	
	@Column(name="TotAsset")
	public double getTotAsset() {
		return this.totAsset;
	}

	public void setTotAsset(double totAsset) {
		this.totAsset = totAsset;
	}
	
	@Column(name="WillfulDefa")
	public String getWillfulDefa() {
		return this.willfulDefa;
	}

	public void setWillfulDefa(String willfulDefa) {
		this.willfulDefa = willfulDefa;
	}

}