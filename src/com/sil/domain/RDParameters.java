package com.sil.domain;

import java.util.Date;

public class RDParameters {

	private int lbrCode;
	private Double mainBal;
	private int reciptNo;
	private int noInst;
	private int pendInst=0;
	private Double IntRate;
	private Double InstAmt;
	private String prcdAcctId;
	private Date matDate;
	private String custName;
	private String custNo;
	
	public int getLbrCode() {
		return lbrCode;
	}
	public void setLbrCode(int lbrCode) {
		this.lbrCode = lbrCode;
	}
	public Double getMainBal() {
		return mainBal;
	}
	public void setMainBal(Double mainBal) {
		this.mainBal = mainBal;
	}
	public int getReciptNo() {
		return reciptNo;
	}
	public void setReciptNo(int reciptNo) {
		this.reciptNo = reciptNo;
	}
	public int getNoInst() {
		return noInst;
	}
	public void setNoInst(int noInst) {
		this.noInst = noInst;
	}
	public int getPendInst() {
		return pendInst;
	}
	public void setPendInst(int pendInst) {
		this.pendInst = pendInst;
	}
	public Double getIntRate() {
		return IntRate;
	}
	public void setIntRate(Double intRate) {
		IntRate = intRate;
	}
	public Double getInstAmt() {
		return InstAmt;
	}
	public void setInstAmt(Double instAmt) {
		InstAmt = instAmt;
	}
	public String getPrcdAcctId() {
		return prcdAcctId;
	}
	public void setPrcdAcctId(String prcdAcctId) {
		this.prcdAcctId = prcdAcctId;
	}
	public Date getMatDate() {
		return matDate;
	}
	public void setMatDate(Date matDate) {
		this.matDate = matDate;
	}
	
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	
	public String getCustNo() {
		return custNo;
	}
	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}
	@Override
	public String toString() {
		return "RDParameters [lbrCode=" + lbrCode + ", mainBal=" + mainBal + ", reciptNo=" + reciptNo + ", noInst="
				+ noInst + ", pendInst=" + pendInst + ", IntRate=" + IntRate + ", InstAmt=" + InstAmt + ", prcdAcctId="
				+ prcdAcctId + ", matDate=" + matDate + ", custName=" + custName + "]";
	}
}
