package com.sil.commonswitch;

import java.io.Serializable;
import java.util.Date;

public class IMPSTransactionDetail implements Serializable {

	private static final long serialVersionUID = -2367768777770652468L;
	private Date entryDate;
	private String batchCd;
	private int setNo;
	private int scrollNo;
	private String fromMobNo;
	private String fromMMID;
	private String toMobno;
	private String toMMID;
	private double amount;
	private String responseCode;
	private String refNo;
	private String drcr;

	public Date getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

	public String getBatchCd() {
		return batchCd;
	}

	public void setBatchCd(String batchCd) {
		this.batchCd = batchCd;
	}

	public int getSetNo() {
		return setNo;
	}

	public void setSetNo(int setNo) {
		this.setNo = setNo;
	}

	public int getScrollNo() {
		return scrollNo;
	}

	public void setScrollNo(int scrollNo) {
		this.scrollNo = scrollNo;
	}

	public String getFromMobNo() {
		return fromMobNo;
	}

	public void setFromMobNo(String fromMobNo) {
		this.fromMobNo = fromMobNo;
	}

	public String getFromMMID() {
		return fromMMID;
	}

	public void setFromMMID(String fromMMID) {
		this.fromMMID = fromMMID;
	}

	public String getToMobno() {
		return toMobno;
	}

	public void setToMobno(String toMobno) {
		this.toMobno = toMobno;
	}

	public String getToMMID() {
		return toMMID;
	}

	public void setToMMID(String toMMID) {
		this.toMMID = toMMID;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public String getDrcr() {
		return drcr;
	}

	public void setDrcr(String drcr) {
		this.drcr = drcr;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
