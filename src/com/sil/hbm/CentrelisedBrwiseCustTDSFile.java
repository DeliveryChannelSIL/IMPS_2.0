package com.sil.hbm;

import java.io.Serializable;
import javax.persistence.*;

import com.sil.util.DateUtil;

import java.util.Date;


/**
 * The persistent class for the D009501 database table.
 * 
 */
@Entity
@Table(name = "D009501")
//@NamedQuery(name="D009501.findAll", query="SELECT d FROM D009501 d")
public class CentrelisedBrwiseCustTDSFile implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="MainCustNo")
	private int mainCustNo;
	
	@Column(name="DbtrAddCb")
	private int dbtrAddCb = 0;

	@Column(name="DbtrAddCd")
	private Date dbtrAddCd = DateUtil.getDateFromStringNew("19000101");

	@Column(name="DbtrAddCk")
	private int dbtrAddCk = 0;

	@Column(name="DbtrAddCs")
	private short dbtrAddCs = 0;

	@Column(name="DbtrAddCt")
	private Date dbtrAddCt = DateUtil.getDateFromStringNew("19000101");;

	@Column(name="DbtrAddMb")
	private int dbtrAddMb = 0;

	@Column(name="DbtrAddMd")
	private Date dbtrAddMd = DateUtil.getDateFromStringNew("19000101");;

	@Column(name="DbtrAddMk")
	private int dbtrAddMk = 0;

	@Column(name="DbtrAddMs")
	private short dbtrAddMs = 0;

	@Column(name="DbtrAddMt")
	private Date dbtrAddMt = DateUtil.getDateFromStringNew("19000101");;

	@Column(name="DbtrAuthDone")
	private short dbtrAuthDone = 0;

	@Column(name="DbtrAuthNeeded")
	private short dbtrAuthNeeded = 0;

	@Column(name="DbtrLHisTrnNo")
	private int dbtrLHisTrnNo=0;

	@Column(name="DbtrLupdCb")
	private int dbtrLupdCb = 0;

	@Column(name="DbtrLupdCd")
	private Date dbtrLupdCd= DateUtil.getDateFromStringNew("19000101");;

	@Column(name="DbtrLupdCk")
	private int dbtrLupdCk = 0;

	@Column(name="DbtrLupdCs")
	private short dbtrLupdCs = 0;

	@Column(name="DbtrLupdCt")
	private Date dbtrLupdCt= DateUtil.getDateFromStringNew("19000101");;

	@Column(name="DbtrLupdMb")
	private int dbtrLupdMb = 0;

	@Column(name="DbtrLupdMd")
	private Date dbtrLupdMd= DateUtil.getDateFromStringNew("19000101");;

	@Column(name="DbtrLupdMk")
	private int dbtrLupdMk = 0;

	@Column(name="DbtrLupdMs")
	private short dbtrLupdMs = 0;

	@Column(name="DbtrLupdMt")
	private Date dbtrLupdMt= DateUtil.getDateFromStringNew("19000101");;

	@Column(name="DbtrRecStat")
	private short dbtrRecStat = 0;

	@Column(name="DbtrTAuthDone")
	private short dbtrTAuthDone = 0;

	@Column(name="DbtrUpdtChkId")
	private short dbtrUpdtChkId = 0;

	@Column(name="IntProjected")
	private double intProjected;

	@Column(name="IntProvision")
	private double intProvision;

	@Column(name="TdsProjected")
	private double tdsProjected;

	@Column(name="TdsProvision")
	private double tdsProvision;

	public CentrelisedBrwiseCustTDSFile() {
	}

	public int getMainCustNo() {
		return this.mainCustNo;
	}

	public void setMainCustNo(int mainCustNo) {
		this.mainCustNo = mainCustNo;
	}

	public int getDbtrAddCb() {
		return this.dbtrAddCb;
	}

	public void setDbtrAddCb(int dbtrAddCb) {
		this.dbtrAddCb = dbtrAddCb;
	}

	public Date getDbtrAddCd() {
		return this.dbtrAddCd;
	}

	public void setDbtrAddCd(Date dbtrAddCd) {
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

	public Date getDbtrAddCt() {
		return this.dbtrAddCt;
	}

	public void setDbtrAddCt(Date dbtrAddCt) {
		this.dbtrAddCt = dbtrAddCt;
	}

	public int getDbtrAddMb() {
		return this.dbtrAddMb;
	}

	public void setDbtrAddMb(int dbtrAddMb) {
		this.dbtrAddMb = dbtrAddMb;
	}

	public Date getDbtrAddMd() {
		return this.dbtrAddMd;
	}

	public void setDbtrAddMd(Date dbtrAddMd) {
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

	public Date getDbtrAddMt() {
		return this.dbtrAddMt;
	}

	public void setDbtrAddMt(Date dbtrAddMt) {
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

	public Date getDbtrLupdCd() {
		return this.dbtrLupdCd;
	}

	public void setDbtrLupdCd(Date dbtrLupdCd) {
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

	public Date getDbtrLupdCt() {
		return this.dbtrLupdCt;
	}

	public void setDbtrLupdCt(Date dbtrLupdCt) {
		this.dbtrLupdCt = dbtrLupdCt;
	}

	public int getDbtrLupdMb() {
		return this.dbtrLupdMb;
	}

	public void setDbtrLupdMb(int dbtrLupdMb) {
		this.dbtrLupdMb = dbtrLupdMb;
	}

	public Date getDbtrLupdMd() {
		return this.dbtrLupdMd;
	}

	public void setDbtrLupdMd(Date dbtrLupdMd) {
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

	public Date getDbtrLupdMt() {
		return this.dbtrLupdMt;
	}

	public void setDbtrLupdMt(Date dbtrLupdMt) {
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

	public double getIntProjected() {
		return this.intProjected;
	}

	public void setIntProjected(double intProjected) {
		this.intProjected = intProjected;
	}

	public double getIntProvision() {
		return this.intProvision;
	}

	public void setIntProvision(double intProvision) {
		this.intProvision = intProvision;
	}

	public double getTdsProjected() {
		return this.tdsProjected;
	}

	public void setTdsProjected(double tdsProjected) {
		this.tdsProjected = tdsProjected;
	}

	public double getTdsProvision() {
		return this.tdsProvision;
	}

	public void setTdsProvision(double tdsProvision) {
		this.tdsProvision = tdsProvision;
	}

}