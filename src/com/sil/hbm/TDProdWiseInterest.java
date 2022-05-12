package com.sil.hbm;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the D020018 database table.
 * 
 */
@Entity
@Table(name="D020018")
public class TDProdWiseInterest implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private TDProdWiseInterestId id;

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

	@Column(name="Remarks")
	private String remarks;

	public TDProdWiseInterest() {
	}

	public TDProdWiseInterestId getId() {
		return this.id;
	}

	public void setId(TDProdWiseInterestId id) {
		this.id = id;
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

	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}