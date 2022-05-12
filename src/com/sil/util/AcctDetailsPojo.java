package com.sil.util;

public class AcctDetailsPojo
{
	int lbrCode;
	String prdAcctid;
	String longName;
	int custNo;
	String panNo;
	String add1;
	String add2;
	String add3;
	String pinCode;
	String mobileNo;
	double ledgerBal;
	double availableBal;
	int isActive;
	
	public AcctDetailsPojo(int lbrCode, String prdAcctid, String longName, int custNo, String panNo, String add1,
			String add2, String add3, String pinCode, String mobileNo, double ledgerBal, double availableBal, int isActive) {
		super();
		this.lbrCode = lbrCode;
		this.prdAcctid = prdAcctid;
		this.longName = longName;
		this.custNo = custNo;
		this.panNo = panNo;
		this.add1 = add1;
		this.add2 = add2;
		this.add3 = add3;
		this.pinCode = pinCode;
		this.mobileNo = mobileNo;
		this.ledgerBal = ledgerBal;
		this.availableBal = availableBal;
		this.isActive = isActive;
	}

	public int getLbrCode() {
		return lbrCode;
	}

	public void setLbrCode(int lbrCode) {
		this.lbrCode = lbrCode;
	}

	public String getPrdAcctid() {
		return prdAcctid;
	}

	public void setPrdAcctid(String prdAcctid) {
		this.prdAcctid = prdAcctid;
	}

	public String getLongName() {
		return longName;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	public int getCustNo() {
		return custNo;
	}

	public void setCustNo(int custNo) {
		this.custNo = custNo;
	}

	public String getPanNo() {
		return panNo;
	}

	public void setPanNo(String panNo) {
		this.panNo = panNo;
	}

	public String getAdd1() {
		return add1;
	}

	public void setAdd1(String add1) {
		this.add1 = add1;
	}

	public String getAdd2() {
		return add2;
	}

	public void setAdd2(String add2) {
		this.add2 = add2;
	}

	public String getAdd3() {
		return add3;
	}

	public void setAdd3(String add3) {
		this.add3 = add3;
	}

	public String getPinCode() {
		return pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public double getLedgerBal() {
		return ledgerBal;
	}

	public void setLedgerBal(double ledgerBal) {
		this.ledgerBal = ledgerBal;
	}

	public double getAvailableBal() {
		return availableBal;
	}

	public void setAvailableBal(double availableBal) {
		this.availableBal = availableBal;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	@Override
	public String toString() {
		return "AcctDetailsPojo [lbrCode=" + lbrCode + ", prdAcctid=" + prdAcctid + ", longName=" + longName
				+ ", custNo=" + custNo + ", panNo=" + panNo + ", add1=" + add1 + ", add2=" + add2 + ", add3=" + add3
				+ ", pinCode=" + pinCode + ", mobileNo=" + mobileNo + ", ledgerBal=" + ledgerBal + ", availableBal="
				+ availableBal + ", isActive=" + isActive + "]";
	}
	
	
	
	
}
