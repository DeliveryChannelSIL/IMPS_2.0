package com.sil.domain;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AccountOpenRequest {
	private String brcode;
	private String prodCode;
	private String accType;
	private String modeOfOperation;
	private String nameTitle;
	private String name;
	private String add1;
	private String add2;
	private String add3;
	private String panCode;
	private String panCardNo;
	private String areaCode;
	private String cityCode;
	private String pinCode;
	private String mobNo;
	private String email;
	private String custNo;
	private String isNew;
	private String accHolderType;
	private String welcomeKitFlag;
	private String accNo;
	private String isPigMeAcc;
	private String month;
	private String days="0";
	private String depositAmount;
	private String agentAccNo;
	
	public String getAgentAccNo() {
		return agentAccNo;
	}
	public void setAgentAccNo(String agentAccNo) {
		this.agentAccNo = agentAccNo;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getDays() {
		return days;
	}
	public void setDays(String days) {
		this.days = days;
	}
	public String getDepositAmount() {
		return depositAmount;
	}
	public void setDepositAmount(String depositAmount) {
		this.depositAmount = depositAmount;
	}
	public String getIsPigMeAcc() {
		return isPigMeAcc;
	}
	public void setIsPigMeAcc(String isPigMeAcc) {
		this.isPigMeAcc = isPigMeAcc;
	}
	public String getAccNo() {
		return accNo;
	}
	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}
	public String getWelcomeKitFlag() {
		return welcomeKitFlag;
	}
	public void setWelcomeKitFlag(String welcomeKitFlag) {
		this.welcomeKitFlag = welcomeKitFlag;
	}
	public String getAccHolderType() {
		return accHolderType;
	}
	public void setAccHolderType(String accHolderType) {
		this.accHolderType = accHolderType;
	}
	public String getCustNo() {
		return custNo;
	}
	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}
	public String getIsNew() {
		return isNew;
	}
	public void setIsNew(String isNew) {
		this.isNew = isNew;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getBrcode() {
		return brcode;
	}
	public void setBrcode(String brcode) {
		this.brcode = brcode;
	}
	
	public String getProdCode() {
		return prodCode;
	}
	public void setProdCode(String prodCode) {
		this.prodCode = prodCode;
	}
	public String getAccType() {
		return accType;
	}
	public void setAccType(String accType) {
		this.accType = accType;
	}
	public String getModeOfOperation() {
		return modeOfOperation;
	}
	public void setModeOfOperation(String modeOfOperation) {
		this.modeOfOperation = modeOfOperation;
	}
	public String getNameTitle() {
		return nameTitle;
	}
	public void setNameTitle(String nameTitle) {
		this.nameTitle = nameTitle;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getPanCode() {
		return panCode;
	}
	public void setPanCode(String panCode) {
		this.panCode = panCode;
	}
	public String getPanCardNo() {
		return panCardNo;
	}
	public void setPanCardNo(String panCardNo) {
		this.panCardNo = panCardNo;
	}
	public String getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	public String getPinCode() {
		return pinCode;
	}
	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}
	public String getMobNo() {
		return mobNo;
	}
	public void setMobNo(String mobNo) {
		this.mobNo = mobNo;
	}
	@Override
	public String toString() {
		return "AccountOpenRequest [brcode=" + brcode + ", prodCode=" + prodCode + ", accType=" + accType
				+ ", modeOfOperation=" + modeOfOperation + ", nameTitle=" + nameTitle + ", name=" + name + ", add1="
				+ add1 + ", add2=" + add2 + ", add3=" + add3 + ", panCode=" + panCode + ", panCardNo=" + panCardNo
				+ ", areaCode=" + areaCode + ", cityCode=" + cityCode + ", pinCode=" + pinCode + ", mobNo=" + mobNo
				+ ", email=" + email + ", custNo=" + custNo + ", isNew=" + isNew + ", accHolderType=" + accHolderType
				+ ", welcomeKitFlag=" + welcomeKitFlag + ", accNo=" + accNo + ", isPigMeAcc=" + isPigMeAcc + ", month="
				+ month + ", days=" + days + ", depositAmount=" + depositAmount + ", agentAccNo=" + agentAccNo + "]";
	}
}
