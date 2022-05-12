package com.sil.hbm;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the D048004 database table.
 * 
 */
@Entity
@Table(name="D048004")
//@NamedQuery(name="D048004.findAll", query="SELECT d FROM D048004 d")
public class D048004 implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private D048004Id id;

	@Column(name="CancelDate")
	private Timestamp cancelDate;

	@Column(name="CancelReasonCd")
	private short cancelReasonCd;

	@Column(name="CashBkFolio")
	private int cashBkFolio;

	@Column(name="CertNo")
	private String certNo;

	@Column(name="CloseDate")
	private Timestamp closeDate;

	@Column(name="CustAcctId")
	private String custAcctId;

	@Column(name="CustBrCode")
	private int custBrCode;

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

	@Column(name="DivAllowYN")
	private String divAllowYN;

	@Column(name="DivTrAcctId")
	private String divTrAcctId;

	@Column(name="DivTrBrCode")
	private int divTrBrCode;

	@Column(name="FromDistNo")
	private String fromDistNo;

	@Column(name="IssRemPayAt")
	private String issRemPayAt;

	@Column(name="IssRemPAyTo1")
	private String issRemPAyTo1;

	@Column(name="IssRemPAyTo2")
	private String issRemPAyTo2;

	@Column(name="LedgerNo")
	private int ledgerNo;

	@Column(name="LstDivPaidDt")
	private Timestamp lstDivPaidDt;

	@Column(name="LstDivProvDt")
	private Timestamp lstDivProvDt;

	@Column(name="MaskRecieptNo")
	private String maskRecieptNo;

	@Column(name="MemDate")
	private Timestamp memDate;

	@Column(name="MemRefNo")
	private String memRefNo;

	@Column(name="MemType")
	private String memType;

	@Column(name="NoCertPrint")
	private short noCertPrint;

	@Column(name="NomMemFee")
	private double nomMemFee;

	@Column(name="NomMemStatus")
	private short nomMemStatus;

	@Column(name="NoofShares")
	private int noofShares;

	@Column(name="PerShareVal")
	private double perShareVal;

	@Column(name="PreMemDt")
	private Timestamp preMemDt;

	@Column(name="PreMemFees")
	private double preMemFees;

	@Column(name="PrnSrNo")
	private int prnSrNo;

	@Column(name="ReasonCd")
	private short reasonCd;

	@Column(name="RecomBrCode")
	private int recomBrCode;

	@Column(name="ResConfRefDt")
	private Timestamp resConfRefDt;

	@Column(name="ResConfRefNo")
	private String resConfRefNo;

	@Column(name="ResRefDt")
	private Timestamp resRefDt;

	@Column(name="ResRefNo")
	private String resRefNo;

	@Column(name="ShrApplNo")
	private int shrApplNo;

	@Column(name="ShrDivPaid")
	private double shrDivPaid;

	@Column(name="ShrDivPrvd")
	private double shrDivPrvd;

	@Column(name="ShrStatus")
	private short shrStatus;

	@Column(name="ShrSubBal")
	private double shrSubBal;

	@Column(name="ToDistNo")
	private String toDistNo;

	public D048004() {
	}

	public D048004Id getId() {
		return this.id;
	}

	public void setId(D048004Id id) {
		this.id = id;
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

	public String getCertNo() {
		return this.certNo;
	}

	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}

	public Timestamp getCloseDate() {
		return this.closeDate;
	}

	public void setCloseDate(Timestamp closeDate) {
		this.closeDate = closeDate;
	}

	public String getCustAcctId() {
		return this.custAcctId;
	}

	public void setCustAcctId(String custAcctId) {
		this.custAcctId = custAcctId;
	}

	public int getCustBrCode() {
		return this.custBrCode;
	}

	public void setCustBrCode(int custBrCode) {
		this.custBrCode = custBrCode;
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

	public String getDivAllowYN() {
		return this.divAllowYN;
	}

	public void setDivAllowYN(String divAllowYN) {
		this.divAllowYN = divAllowYN;
	}

	public String getDivTrAcctId() {
		return this.divTrAcctId;
	}

	public void setDivTrAcctId(String divTrAcctId) {
		this.divTrAcctId = divTrAcctId;
	}

	public int getDivTrBrCode() {
		return this.divTrBrCode;
	}

	public void setDivTrBrCode(int divTrBrCode) {
		this.divTrBrCode = divTrBrCode;
	}

	public String getFromDistNo() {
		return this.fromDistNo;
	}

	public void setFromDistNo(String fromDistNo) {
		this.fromDistNo = fromDistNo;
	}

	public String getIssRemPayAt() {
		return this.issRemPayAt;
	}

	public void setIssRemPayAt(String issRemPayAt) {
		this.issRemPayAt = issRemPayAt;
	}

	public String getIssRemPAyTo1() {
		return this.issRemPAyTo1;
	}

	public void setIssRemPAyTo1(String issRemPAyTo1) {
		this.issRemPAyTo1 = issRemPAyTo1;
	}

	public String getIssRemPAyTo2() {
		return this.issRemPAyTo2;
	}

	public void setIssRemPAyTo2(String issRemPAyTo2) {
		this.issRemPAyTo2 = issRemPAyTo2;
	}

	public int getLedgerNo() {
		return this.ledgerNo;
	}

	public void setLedgerNo(int ledgerNo) {
		this.ledgerNo = ledgerNo;
	}

	public Timestamp getLstDivPaidDt() {
		return this.lstDivPaidDt;
	}

	public void setLstDivPaidDt(Timestamp lstDivPaidDt) {
		this.lstDivPaidDt = lstDivPaidDt;
	}

	public Timestamp getLstDivProvDt() {
		return this.lstDivProvDt;
	}

	public void setLstDivProvDt(Timestamp lstDivProvDt) {
		this.lstDivProvDt = lstDivProvDt;
	}

	public String getMaskRecieptNo() {
		return this.maskRecieptNo;
	}

	public void setMaskRecieptNo(String maskRecieptNo) {
		this.maskRecieptNo = maskRecieptNo;
	}

	public Timestamp getMemDate() {
		return this.memDate;
	}

	public void setMemDate(Timestamp memDate) {
		this.memDate = memDate;
	}

	public String getMemRefNo() {
		return this.memRefNo;
	}

	public void setMemRefNo(String memRefNo) {
		this.memRefNo = memRefNo;
	}

	public String getMemType() {
		return this.memType;
	}

	public void setMemType(String memType) {
		this.memType = memType;
	}

	public short getNoCertPrint() {
		return this.noCertPrint;
	}

	public void setNoCertPrint(short noCertPrint) {
		this.noCertPrint = noCertPrint;
	}

	public double getNomMemFee() {
		return this.nomMemFee;
	}

	public void setNomMemFee(double nomMemFee) {
		this.nomMemFee = nomMemFee;
	}

	public short getNomMemStatus() {
		return this.nomMemStatus;
	}

	public void setNomMemStatus(short nomMemStatus) {
		this.nomMemStatus = nomMemStatus;
	}

	public int getNoofShares() {
		return this.noofShares;
	}

	public void setNoofShares(int noofShares) {
		this.noofShares = noofShares;
	}

	public double getPerShareVal() {
		return this.perShareVal;
	}

	public void setPerShareVal(double perShareVal) {
		this.perShareVal = perShareVal;
	}

	public Timestamp getPreMemDt() {
		return this.preMemDt;
	}

	public void setPreMemDt(Timestamp preMemDt) {
		this.preMemDt = preMemDt;
	}

	public double getPreMemFees() {
		return this.preMemFees;
	}

	public void setPreMemFees(double preMemFees) {
		this.preMemFees = preMemFees;
	}

	public int getPrnSrNo() {
		return this.prnSrNo;
	}

	public void setPrnSrNo(int prnSrNo) {
		this.prnSrNo = prnSrNo;
	}

	public short getReasonCd() {
		return this.reasonCd;
	}

	public void setReasonCd(short reasonCd) {
		this.reasonCd = reasonCd;
	}

	public int getRecomBrCode() {
		return this.recomBrCode;
	}

	public void setRecomBrCode(int recomBrCode) {
		this.recomBrCode = recomBrCode;
	}

	public Timestamp getResConfRefDt() {
		return this.resConfRefDt;
	}

	public void setResConfRefDt(Timestamp resConfRefDt) {
		this.resConfRefDt = resConfRefDt;
	}

	public String getResConfRefNo() {
		return this.resConfRefNo;
	}

	public void setResConfRefNo(String resConfRefNo) {
		this.resConfRefNo = resConfRefNo;
	}

	public Timestamp getResRefDt() {
		return this.resRefDt;
	}

	public void setResRefDt(Timestamp resRefDt) {
		this.resRefDt = resRefDt;
	}

	public String getResRefNo() {
		return this.resRefNo;
	}

	public void setResRefNo(String resRefNo) {
		this.resRefNo = resRefNo;
	}

	public int getShrApplNo() {
		return this.shrApplNo;
	}

	public void setShrApplNo(int shrApplNo) {
		this.shrApplNo = shrApplNo;
	}

	public double getShrDivPaid() {
		return this.shrDivPaid;
	}

	public void setShrDivPaid(double shrDivPaid) {
		this.shrDivPaid = shrDivPaid;
	}

	public double getShrDivPrvd() {
		return this.shrDivPrvd;
	}

	public void setShrDivPrvd(double shrDivPrvd) {
		this.shrDivPrvd = shrDivPrvd;
	}

	public short getShrStatus() {
		return this.shrStatus;
	}

	public void setShrStatus(short shrStatus) {
		this.shrStatus = shrStatus;
	}

	public double getShrSubBal() {
		return this.shrSubBal;
	}

	public void setShrSubBal(double shrSubBal) {
		this.shrSubBal = shrSubBal;
	}

	public String getToDistNo() {
		return this.toDistNo;
	}

	public void setToDistNo(String toDistNo) {
		this.toDistNo = toDistNo;
	}

}