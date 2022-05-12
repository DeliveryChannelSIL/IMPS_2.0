package com.sil.domain;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

@javax.xml.bind.annotation.XmlRootElement
public class IMPSTermDepositRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1002516439726691863L;
	private String customerNo = "";
	private String accountNo = "";
	private String productCode = "";
	private Long lbrCode = 0L;
	private Long noOfMonths = 0L;
	private Long noOfDays = 0L;
	private Double amount = 0.0;
	private String activity = "";

	public IMPSTermDepositRequest() {
	}

	@XmlElement()
	public String getCustomerNo() {
		return customerNo;
	}

	public void setCustomerNo(String customerNo) {
		this.customerNo = customerNo;
	}

	@XmlElement()
	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	@XmlElement()
	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	@XmlElement()
	public Long getLbrCode() {
		return lbrCode;
	}

	@XmlElement()
	public Long getNoOfMonths() {
		return noOfMonths;
	}

	public void setNoOfMonths(Long noOfMonths) {
		this.noOfMonths = noOfMonths;
	}

	@XmlElement()
	public Long getNoOfDays() {
		return noOfDays;
	}

	public void setNoOfDays(Long noOfDays) {
		this.noOfDays = noOfDays;
	}

	@XmlElement()
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@XmlElement()
	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public void setLbrCode(Long lbrCode) {
		this.lbrCode = lbrCode;
	}

}
