package com.sil.hbm;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.sql.Timestamp;


/**
 * The persistent class for the D009046 database table.
 * 
 */
@Entity
@Table(name = "D009046")
@DynamicUpdate
@DynamicInsert
public class D009046 implements Serializable {
	private static final long serialVersionUID = 1L;

	//@EmbeddedId
	private D009046Id id;

	@Column(name="BookDebtsLimit")
	private double bookDebtsLimit;

	@Column(name="BookDebtsMargin")
	private double bookDebtsMargin;

	@Column(name="BookDebtsValue")
	private double bookDebtsValue;

	@Column(name="CrdMargin")
	private double crdMargin;

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

	@Column(name="ExpiryDate")
	private Timestamp expiryDate;

	@Column(name="FinishedGdsLimit")
	private double finishedGdsLimit;

	@Column(name="FinishedGdsMargin")
	private double finishedGdsMargin;

	@Column(name="FinishedGdsValue")
	private double finishedGdsValue;

	@Column(name="GdsInTransitLimit")
	private double gdsInTransitLimit;

	@Column(name="GdsInTransitMargin")
	private double gdsInTransitMargin;

	@Column(name="GdsInTransitValue")
	private double gdsInTransitValue;

	@Column(name="InspectDate")
	private Timestamp inspectDate;

	@Column(name="MiscSecuLimit")
	private double miscSecuLimit;

	@Column(name="MiscSecuMargin")
	private double miscSecuMargin;

	@Column(name="MiscSecuValue")
	private double miscSecuValue;

	@Column(name="MnthlyPurchase")
	private double mnthlyPurchase;

	@Column(name="MnthlySales")
	private double mnthlySales;

	@Column(name="RmLimit")
	private double rmLimit;

	@Column(name="RmMargin")
	private double rmMargin;

	@Column(name="RmValue")
	private double rmValue;

	@Column(name="StkcrdDebMargin")
	private double stkcrdDebMargin;

	@Column(name="StkcrdMargin")
	private double stkcrdMargin;

	@Column(name="StkMargin")
	private double stkMargin;

	@Column(name="SubmitDate")
	private Timestamp submitDate;

	@Column(name="SundryCrsGdsLimit")
	private double sundryCrsGdsLimit;

	@Column(name="SundryCrsGdsMargin")
	private double sundryCrsGdsMargin;

	@Column(name="SundryCrsGdsValue")
	private double sundryCrsGdsValue;

	@Column(name="SundryCrsOthLimit")
	private double sundryCrsOthLimit;

	@Column(name="SundryCrsOthMargin")
	private double sundryCrsOthMargin;

	@Column(name="SundryCrsOthValue")
	private double sundryCrsOthValue;

	@Column(name="TotalDpAllowed")
	private double totalDpAllowed;

	@Column(name="TotalDpArrived")
	private double totalDpArrived;

	@Column(name="VerifiedBy")
	private String verifiedBy;

	private double WIPOtherLimit;

	private double WIPOtherMargin;

	private double WIPOtherValue;

	private double WIPProcOutLimit;

	private double WIPProcOutMargin;

	private double WIPProcOutValue;

	public D009046() {
	}

	public D009046(D009046Id id, double bookDebtsLimit, double bookDebtsMargin, double bookDebtsValue, double crdMargin,
			int dbtrAddCb, Timestamp dbtrAddCd, int dbtrAddCk, short dbtrAddCs, Timestamp dbtrAddCt, int dbtrAddMb,
			Timestamp dbtrAddMd, int dbtrAddMk, short dbtrAddMs, Timestamp dbtrAddMt, short dbtrAuthDone,
			short dbtrAuthNeeded, int dbtrLHisTrnNo, int dbtrLupdCb, Timestamp dbtrLupdCd, int dbtrLupdCk,
			short dbtrLupdCs, Timestamp dbtrLupdCt, int dbtrLupdMb, Timestamp dbtrLupdMd, int dbtrLupdMk,
			short dbtrLupdMs, Timestamp dbtrLupdMt, short dbtrRecStat, short dbtrTAuthDone, short dbtrUpdtChkId,
			Timestamp expiryDate, double finishedGdsLimit, double finishedGdsMargin, double finishedGdsValue,
			double gdsInTransitLimit, double gdsInTransitMargin, double gdsInTransitValue, Timestamp inspectDate,
			double miscSecuLimit, double miscSecuMargin, double miscSecuValue, double mnthlyPurchase,
			double mnthlySales, double rmLimit, double rmMargin, double rmValue, double stkcrdDebMargin,
			double stkcrdMargin, double stkMargin, Timestamp submitDate, double sundryCrsGdsLimit,
			double sundryCrsGdsMargin, double sundryCrsGdsValue, double sundryCrsOthLimit, double sundryCrsOthMargin,
			double sundryCrsOthValue, double totalDpAllowed, double totalDpArrived, String verifiedBy,
			double wIPOtherLimit, double wIPOtherMargin, double wIPOtherValue, double wIPProcOutLimit,
			double wIPProcOutMargin, double wIPProcOutValue) {
		super();
		this.id = id;
		this.bookDebtsLimit = bookDebtsLimit;
		this.bookDebtsMargin = bookDebtsMargin;
		this.bookDebtsValue = bookDebtsValue;
		this.crdMargin = crdMargin;
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
		this.expiryDate = expiryDate;
		this.finishedGdsLimit = finishedGdsLimit;
		this.finishedGdsMargin = finishedGdsMargin;
		this.finishedGdsValue = finishedGdsValue;
		this.gdsInTransitLimit = gdsInTransitLimit;
		this.gdsInTransitMargin = gdsInTransitMargin;
		this.gdsInTransitValue = gdsInTransitValue;
		this.inspectDate = inspectDate;
		this.miscSecuLimit = miscSecuLimit;
		this.miscSecuMargin = miscSecuMargin;
		this.miscSecuValue = miscSecuValue;
		this.mnthlyPurchase = mnthlyPurchase;
		this.mnthlySales = mnthlySales;
		this.rmLimit = rmLimit;
		this.rmMargin = rmMargin;
		this.rmValue = rmValue;
		this.stkcrdDebMargin = stkcrdDebMargin;
		this.stkcrdMargin = stkcrdMargin;
		this.stkMargin = stkMargin;
		this.submitDate = submitDate;
		this.sundryCrsGdsLimit = sundryCrsGdsLimit;
		this.sundryCrsGdsMargin = sundryCrsGdsMargin;
		this.sundryCrsGdsValue = sundryCrsGdsValue;
		this.sundryCrsOthLimit = sundryCrsOthLimit;
		this.sundryCrsOthMargin = sundryCrsOthMargin;
		this.sundryCrsOthValue = sundryCrsOthValue;
		this.totalDpAllowed = totalDpAllowed;
		this.totalDpArrived = totalDpArrived;
		this.verifiedBy = verifiedBy;
		WIPOtherLimit = wIPOtherLimit;
		WIPOtherMargin = wIPOtherMargin;
		WIPOtherValue = wIPOtherValue;
		WIPProcOutLimit = wIPProcOutLimit;
		WIPProcOutMargin = wIPProcOutMargin;
		WIPProcOutValue = wIPProcOutValue;
	}

	
	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "lBrCode", column = @Column(name = "LBrCode", nullable = false)),
			@AttributeOverride(name = "prdAcctId", column = @Column(name = "PrdAcctId", nullable = false, length = 32)),
			@AttributeOverride(name = "dpDate", column = @Column(name = "DpDate", nullable = false, length = 23)) })
	public D009046Id getId() {
		return this.id;
	}

	
	public void setId(D009046Id id) {
		this.id = id;
	}

	@Column(name="BookDebtsLimit")
	public double getBookDebtsLimit() {
		return this.bookDebtsLimit;
	}

	public void setBookDebtsLimit(double bookDebtsLimit) {
		this.bookDebtsLimit = bookDebtsLimit;
	}

	@Column(name="BookDebtsMargin")
	public double getBookDebtsMargin() {
		return this.bookDebtsMargin;
	}

	public void setBookDebtsMargin(double bookDebtsMargin) {
		this.bookDebtsMargin = bookDebtsMargin;
	}

	@Column(name="BookDebtsValue")
	public double getBookDebtsValue() {
		return this.bookDebtsValue;
	}

	public void setBookDebtsValue(double bookDebtsValue) {
		this.bookDebtsValue = bookDebtsValue;
	}

	@Column(name="CrdMargin")
	public double getCrdMargin() {
		return this.crdMargin;
	}

	public void setCrdMargin(double crdMargin) {
		this.crdMargin = crdMargin;
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

	@Column(name="ExpiryDate")
	public Timestamp getExpiryDate() {
		return this.expiryDate;
	}

	public void setExpiryDate(Timestamp expiryDate) {
		this.expiryDate = expiryDate;
	}

	@Column(name="FinishedGdsLimit")
	public double getFinishedGdsLimit() {
		return this.finishedGdsLimit;
	}

	public void setFinishedGdsLimit(double finishedGdsLimit) {
		this.finishedGdsLimit = finishedGdsLimit;
	}

	@Column(name="FinishedGdsMargin")
	public double getFinishedGdsMargin() {
		return this.finishedGdsMargin;
	}

	public void setFinishedGdsMargin(double finishedGdsMargin) {
		this.finishedGdsMargin = finishedGdsMargin;
	}

	@Column(name="FinishedGdsValue")
	public double getFinishedGdsValue() {
		return this.finishedGdsValue;
	}

	public void setFinishedGdsValue(double finishedGdsValue) {
		this.finishedGdsValue = finishedGdsValue;
	}

	@Column(name="GdsInTransitLimit")
	public double getGdsInTransitLimit() {
		return this.gdsInTransitLimit;
	}

	public void setGdsInTransitLimit(double gdsInTransitLimit) {
		this.gdsInTransitLimit = gdsInTransitLimit;
	}

	@Column(name="GdsInTransitMargin")
	public double getGdsInTransitMargin() {
		return this.gdsInTransitMargin;
	}

	public void setGdsInTransitMargin(double gdsInTransitMargin) {
		this.gdsInTransitMargin = gdsInTransitMargin;
	}

	@Column(name="GdsInTransitValue")
	public double getGdsInTransitValue() {
		return this.gdsInTransitValue;
	}

	public void setGdsInTransitValue(double gdsInTransitValue) {
		this.gdsInTransitValue = gdsInTransitValue;
	}

	@Column(name="InspectDate")
	public Timestamp getInspectDate() {
		return this.inspectDate;
	}

	public void setInspectDate(Timestamp inspectDate) {
		this.inspectDate = inspectDate;
	}

	@Column(name="MiscSecuLimit")
	public double getMiscSecuLimit() {
		return this.miscSecuLimit;
	}

	public void setMiscSecuLimit(double miscSecuLimit) {
		this.miscSecuLimit = miscSecuLimit;
	}

	@Column(name="MiscSecuMargin")
	public double getMiscSecuMargin() {
		return this.miscSecuMargin;
	}

	public void setMiscSecuMargin(double miscSecuMargin) {
		this.miscSecuMargin = miscSecuMargin;
	}

	@Column(name="MiscSecuValue")
	public double getMiscSecuValue() {
		return this.miscSecuValue;
	}

	public void setMiscSecuValue(double miscSecuValue) {
		this.miscSecuValue = miscSecuValue;
	}

	@Column(name="MnthlyPurchase")
	public double getMnthlyPurchase() {
		return this.mnthlyPurchase;
	}

	public void setMnthlyPurchase(double mnthlyPurchase) {
		this.mnthlyPurchase = mnthlyPurchase;
	}

	@Column(name="MnthlySales")
	public double getMnthlySales() {
		return this.mnthlySales;
	}

	public void setMnthlySales(double mnthlySales) {
		this.mnthlySales = mnthlySales;
	}

	@Column(name="RmLimit")
	public double getRmLimit() {
		return this.rmLimit;
	}

	public void setRmLimit(double rmLimit) {
		this.rmLimit = rmLimit;
	}

	@Column(name="RmMargin")
	public double getRmMargin() {
		return this.rmMargin;
	}

	public void setRmMargin(double rmMargin) {
		this.rmMargin = rmMargin;
	}

	@Column(name="RmValue")
	public double getRmValue() {
		return this.rmValue;
	}

	public void setRmValue(double rmValue) {
		this.rmValue = rmValue;
	}

	@Column(name="StkcrdDebMargin")
	public double getStkcrdDebMargin() {
		return this.stkcrdDebMargin;
	}

	public void setStkcrdDebMargin(double stkcrdDebMargin) {
		this.stkcrdDebMargin = stkcrdDebMargin;
	}

	@Column(name="StkcrdMargin")
	public double getStkcrdMargin() {
		return this.stkcrdMargin;
	}

	public void setStkcrdMargin(double stkcrdMargin) {
		this.stkcrdMargin = stkcrdMargin;
	}

	@Column(name="StkMargin")
	public double getStkMargin() {
		return this.stkMargin;
	}

	public void setStkMargin(double stkMargin) {
		this.stkMargin = stkMargin;
	}

	@Column(name="SubmitDate")
	public Timestamp getSubmitDate() {
		return this.submitDate;
	}

	public void setSubmitDate(Timestamp submitDate) {
		this.submitDate = submitDate;
	}

	@Column(name="SundryCrsGdsLimit")
	public double getSundryCrsGdsLimit() {
		return this.sundryCrsGdsLimit;
	}

	public void setSundryCrsGdsLimit(double sundryCrsGdsLimit) {
		this.sundryCrsGdsLimit = sundryCrsGdsLimit;
	}

	@Column(name="SundryCrsGdsMargin")
	public double getSundryCrsGdsMargin() {
		return this.sundryCrsGdsMargin;
	}

	public void setSundryCrsGdsMargin(double sundryCrsGdsMargin) {
		this.sundryCrsGdsMargin = sundryCrsGdsMargin;
	}

	@Column(name="SundryCrsGdsValue")
	public double getSundryCrsGdsValue() {
		return this.sundryCrsGdsValue;
	}

	public void setSundryCrsGdsValue(double sundryCrsGdsValue) {
		this.sundryCrsGdsValue = sundryCrsGdsValue;
	}

	@Column(name="SundryCrsOthLimit")
	public double getSundryCrsOthLimit() {
		return this.sundryCrsOthLimit;
	}

	public void setSundryCrsOthLimit(double sundryCrsOthLimit) {
		this.sundryCrsOthLimit = sundryCrsOthLimit;
	}

	@Column(name="SundryCrsOthMargin")
	public double getSundryCrsOthMargin() {
		return this.sundryCrsOthMargin;
	}

	public void setSundryCrsOthMargin(double sundryCrsOthMargin) {
		this.sundryCrsOthMargin = sundryCrsOthMargin;
	}

	@Column(name="SundryCrsOthValue")
	public double getSundryCrsOthValue() {
		return this.sundryCrsOthValue;
	}

	public void setSundryCrsOthValue(double sundryCrsOthValue) {
		this.sundryCrsOthValue = sundryCrsOthValue;
	}

	@Column(name="TotalDpAllowed")
	public double getTotalDpAllowed() {
		return this.totalDpAllowed;
	}

	public void setTotalDpAllowed(double totalDpAllowed) {
		this.totalDpAllowed = totalDpAllowed;
	}

	@Column(name="TotalDpArrived")
	public double getTotalDpArrived() {
		return this.totalDpArrived;
	}

	public void setTotalDpArrived(double totalDpArrived) {
		this.totalDpArrived = totalDpArrived;
	}

	@Column(name="VerifiedBy")
	public String getVerifiedBy() {
		return this.verifiedBy;
	}

	public void setVerifiedBy(String verifiedBy) {
		this.verifiedBy = verifiedBy;
	}

	public double getWIPOtherLimit() {
		return this.WIPOtherLimit;
	}

	public void setWIPOtherLimit(double WIPOtherLimit) {
		this.WIPOtherLimit = WIPOtherLimit;
	}

	public double getWIPOtherMargin() {
		return this.WIPOtherMargin;
	}

	public void setWIPOtherMargin(double WIPOtherMargin) {
		this.WIPOtherMargin = WIPOtherMargin;
	}

	public double getWIPOtherValue() {
		return this.WIPOtherValue;
	}

	public void setWIPOtherValue(double WIPOtherValue) {
		this.WIPOtherValue = WIPOtherValue;
	}

	public double getWIPProcOutLimit() {
		return this.WIPProcOutLimit;
	}

	public void setWIPProcOutLimit(double WIPProcOutLimit) {
		this.WIPProcOutLimit = WIPProcOutLimit;
	}

	public double getWIPProcOutMargin() {
		return this.WIPProcOutMargin;
	}

	public void setWIPProcOutMargin(double WIPProcOutMargin) {
		this.WIPProcOutMargin = WIPProcOutMargin;
	}

	public double getWIPProcOutValue() {
		return this.WIPProcOutValue;
	}

	public void setWIPProcOutValue(double WIPProcOutValue) {
		this.WIPProcOutValue = WIPProcOutValue;
	}

}