package com.sil.domain;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

@javax.xml.bind.annotation.XmlRootElement
public class CustomerInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String response = " ";
	private String errorMessage = " ";
	private String errorCode = " ";
	private String mobileNo = " ";
	private String emailId = " ";
	private Long custNo = 0l;
	private Long lbrCode = 0l;
	private String nameTitle = " ";
	private String longName = " ";
	private String add1 = " ";
	private String add2 = " ";
	private String add3 = " ";
	private String cityCode = " ";
	private String pinCode = " ";
	private String panNoDesc = " ";
	private String minDepAmt;
	private String maxDepAmt;
	private String minPeriod;
	private String maxPeriod;
	private String periodType;

	public CustomerInfo() {

	}

	@XmlElement()
	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	@XmlElement()
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@XmlElement()
	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	@XmlElement()
	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	@XmlElement()
	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	@XmlElement()
	public Long getCustNo() {
		return custNo;
	}

	public void setCustNo(Long custNo) {
		this.custNo = custNo;
	}

	@XmlElement()
	public String getNameTitle() {
		return nameTitle;
	}

	public void setNameTitle(String nameTitle) {
		this.nameTitle = nameTitle;
	}

	@XmlElement()
	public String getLongName() {
		return longName;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	@XmlElement()
	public String getAdd1() {
		return add1;
	}

	public void setAdd1(String add1) {
		this.add1 = add1;
	}

	@XmlElement()
	public String getAdd2() {
		return add2;
	}

	public void setAdd2(String add2) {
		this.add2 = add2;
	}

	@XmlElement()
	public String getAdd3() {
		return add3;
	}

	public void setAdd3(String add3) {
		this.add3 = add3;
	}

	@XmlElement()
	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	@XmlElement()
	public String getPinCode() {
		return pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	@XmlElement()
	public String getPanNoDesc() {
		return panNoDesc;
	}

	public void setPanNoDesc(String panNoDesc) {
		this.panNoDesc = panNoDesc;
	}

	@XmlElement()
	public String getMinDepAmt() {
		return minDepAmt;
	}

	public void setMinDepAmt(String minDepAmt) {
		this.minDepAmt = minDepAmt;
	}

	@XmlElement()
	public String getMaxDepAmt() {
		return maxDepAmt;
	}

	public void setMaxDepAmt(String maxDepAmt) {
		this.maxDepAmt = maxDepAmt;
	}

	@XmlElement()
	public String getMinPeriod() {
		return minPeriod;
	}

	public void setMinPeriod(String minPeriod) {
		this.minPeriod = minPeriod;
	}

	@XmlElement()
	public String getMaxPeriod() {
		return maxPeriod;
	}

	public void setMaxPeriod(String maxPeriod) {
		this.maxPeriod = maxPeriod;
	}

	@XmlElement()
	public String getPeriodType() {
		return periodType;
	}

	public void setPeriodType(String periodType) {
		this.periodType = periodType;
	}

	@XmlElement()
	public Long getLbrCode() {
		return lbrCode;
	}

	public void setLbrCode(Long lbrCode) {
		this.lbrCode = lbrCode;
	}
}
