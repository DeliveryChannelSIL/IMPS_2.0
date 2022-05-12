package com.sil.domain;

import java.io.Serializable;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;

@javax.xml.bind.annotation.XmlRootElement
public class LoanAccountDetails implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long lbrCode = 0l;
	private String accountNo = "";
	private Date sansactionDate = null;
	private String totalSansactionLimit = "0.00";
	private String instlAmt = "0.00";
	private String expDate = null;
	private Long accStatus = 0L;
	private Long customerno = 0L;
	private String longName = "";
	private String balance = "0.0";
	private String effDate = null;
	private String intRate = "0.00";
	public LoanAccountDetails() {

	}

	@XmlElement()
	public Long getLbrCode() {
		return lbrCode;
	}

	public void setLbrCode(Long lbrCode) {
		this.lbrCode = lbrCode;
	}

	@XmlElement()
	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	@XmlElement()
	public Date getSansactionDate() {
		return sansactionDate;
	}

	public void setSansactionDate(Date sansactionDate) {
		this.sansactionDate = sansactionDate;
	}

	@XmlElement()
	public String getTotalSansactionLimit() {
		return totalSansactionLimit;
	}

	public void setTotalSansactionLimit(String totalSansactionLimit) {
		this.totalSansactionLimit = totalSansactionLimit;
	}

	@XmlElement()
	public String getInstlAmt() {
		return instlAmt;
	}

	public void setInstlAmt(String instlAmt) {
		this.instlAmt = instlAmt;
	}

	@XmlElement()
	public String getExpDate() {
		return expDate;
	}

	public void setExpDate(String expDate) {
		this.expDate = expDate;
	}

	@XmlElement()
	public Long getAccStatus() {
		return accStatus;
	}

	public void setAccStatus(Long accStatus) {
		this.accStatus = accStatus;
	}

	@XmlElement()
	public Long getCustomerno() {
		return customerno;
	}

	public void setCustomerno(Long customerno) {
		this.customerno = customerno;
	}

	@XmlElement()
	public String getLongName() {
		return longName;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	@XmlElement()
	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public String getEffDate() {
		return effDate;
	}

	public void setEffDate(String effDate) {
		this.effDate = effDate;
	}

	public String getIntRate() {
		return intRate;
	}

	public void setIntRate(String intRate) {
		this.intRate = intRate;
	}

	@Override
	public String toString() {
		return "LoanAccountDetails [lbrCode=" + lbrCode + ", accountNo=" + accountNo + ", sansactionDate="
				+ sansactionDate + ", totalSansactionLimit=" + totalSansactionLimit + ", instlAmt=" + instlAmt
				+ ", expDate=" + expDate + ", accStatus=" + accStatus + ", customerno=" + customerno + ", longName="
				+ longName + ", balance=" + balance + ", effDate=" + effDate + ", intRate=" + intRate + "]";
	}

	
}
