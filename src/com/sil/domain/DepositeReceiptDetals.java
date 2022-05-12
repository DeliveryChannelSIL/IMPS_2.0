package com.sil.domain;

import java.io.Serializable;
import java.util.Date;
import javax.xml.bind.annotation.XmlElement;

@javax.xml.bind.annotation.XmlRootElement
public class DepositeReceiptDetals implements Serializable {
	private static final long serialVersionUID = 1L;
	private String receiptNo = "";
	private Date dateOfReceipt = null;
	private Double balance = 0.0;
	private Double interestRate = 0.0;
	private Double maturityAmt = 0.0;
	private Date maturityDate = null;
	private Double lienAmount = 0.0;
	private Date asOfDate = null;
	public DepositeReceiptDetals() {
	}

	@XmlElement()
	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	@XmlElement()
	public Date getDateOfReceipt() {
		return dateOfReceipt;
	}

	public void setDateOfReceipt(Date dateOfReceipt) {
		this.dateOfReceipt = dateOfReceipt;
	}

	@XmlElement()
	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	@XmlElement()
	public Double getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(Double interestRate) {
		this.interestRate = interestRate;
	}

	@XmlElement()
	public Double getMaturityAmt() {
		return maturityAmt;
	}

	public void setMaturityAmt(Double maturityAmt) {
		this.maturityAmt = maturityAmt;
	}

	@XmlElement()
	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	@XmlElement()
	public Double getLienAmount() {
		return lienAmount;
	}

	public void setLienAmount(Double lienAmount) {
		this.lienAmount = lienAmount;
	}

	@XmlElement()
	public Date getAsOfDate() {
		return asOfDate;
	}

	public void setAsOfDate(Date asOfDate) {
		this.asOfDate = asOfDate;
	}

}
