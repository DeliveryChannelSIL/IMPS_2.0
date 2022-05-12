package com.sil.domain;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "schemeCode", "welcomeKitFlag", "customerSubType", "debitAccount", "panCode", "cityCode",
		"agentAccNo", "brcode", "mobNo", "customerType", "modeOfOperation", "accType", "add2", "add1", "email", "add3",
		"depositAmount", "custNo", "nameTitle", "accNo", "isNew", "prodCode", "areaCode", "month", "accHolderType",
		"pinCode", "name", "days", "isPigMeAcc", "panCardNo" })
public class DepositeAcctOpenRequest {

	@JsonProperty("schemeCode")
	private String schemeCode;
	@JsonProperty("welcomeKitFlag")
	private String welcomeKitFlag;
	@JsonProperty("customerSubType")
	private String customerSubType;
	@JsonProperty("debitAccount")
	private String debitAccount;
	@JsonProperty("panCode")
	private String panCode;
	@JsonProperty("cityCode")
	private String cityCode;
	@JsonProperty("agentAccNo")
	private String agentAccNo;
	@JsonProperty("brcode")
	private String brcode;
	@JsonProperty("mobNo")
	private String mobNo;
	@JsonProperty("customerType")
	private String customerType;
	@JsonProperty("modeOfOperation")
	private String modeOfOperation;
	@JsonProperty("accType")
	private String accType;
	@JsonProperty("add2")
	private String add2;
	@JsonProperty("add1")
	private String add1;
	@JsonProperty("email")
	private String email;
	@JsonProperty("add3")
	private String add3;
	@JsonProperty("depositAmount")
	private String depositAmount;
	@JsonProperty("custNo")
	private String custNo;
	@JsonProperty("nameTitle")
	private String nameTitle;
	@JsonProperty("accNo")
	private String accNo;
	@JsonProperty("isNew")
	private String isNew;
	@JsonProperty("prodCode")
	private String prodCode;
	@JsonProperty("areaCode")
	private String areaCode;
	@JsonProperty("month")
	private String month;
	@JsonProperty("accHolderType")
	private String accHolderType;
	@JsonProperty("pinCode")
	private String pinCode;
	@JsonProperty("name")
	private String name;
	@JsonProperty("days")
	private String days;
	@JsonProperty("isPigMeAcc")
	private String isPigMeAcc;
	@JsonProperty("panCardNo")
	private String panCardNo;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("schemeCode")
	public String getSchemeCode() {
		return schemeCode;
	}

	@JsonProperty("schemeCode")
	public void setSchemeCode(String schemeCode) {
		this.schemeCode = schemeCode;
	}

	@JsonProperty("welcomeKitFlag")
	public String getWelcomeKitFlag() {
		return welcomeKitFlag;
	}

	@JsonProperty("welcomeKitFlag")
	public void setWelcomeKitFlag(String welcomeKitFlag) {
		this.welcomeKitFlag = welcomeKitFlag;
	}

	@JsonProperty("customerSubType")
	public String getCustomerSubType() {
		return customerSubType;
	}

	@JsonProperty("customerSubType")
	public void setCustomerSubType(String customerSubType) {
		this.customerSubType = customerSubType;
	}

	@JsonProperty("debitAccount")
	public String getDebitAccount() {
		return debitAccount;
	}

	@JsonProperty("debitAccount")
	public void setDebitAccount(String debitAccount) {
		this.debitAccount = debitAccount;
	}

	@JsonProperty("panCode")
	public String getPanCode() {
		return panCode;
	}

	@JsonProperty("panCode")
	public void setPanCode(String panCode) {
		this.panCode = panCode;
	}

	@JsonProperty("cityCode")
	public String getCityCode() {
		return cityCode;
	}

	@JsonProperty("cityCode")
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	@JsonProperty("agentAccNo")
	public String getAgentAccNo() {
		return agentAccNo;
	}

	@JsonProperty("agentAccNo")
	public void setAgentAccNo(String agentAccNo) {
		this.agentAccNo = agentAccNo;
	}

	@JsonProperty("brcode")
	public String getBrcode() {
		return brcode;
	}

	@JsonProperty("brcode")
	public void setBrcode(String brcode) {
		this.brcode = brcode;
	}

	@JsonProperty("mobNo")
	public String getMobNo() {
		return mobNo;
	}

	@JsonProperty("mobNo")
	public void setMobNo(String mobNo) {
		this.mobNo = mobNo;
	}

	@JsonProperty("customerType")
	public String getCustomerType() {
		return customerType;
	}

	@JsonProperty("customerType")
	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	@JsonProperty("modeOfOperation")
	public String getModeOfOperation() {
		return modeOfOperation;
	}

	@JsonProperty("modeOfOperation")
	public void setModeOfOperation(String modeOfOperation) {
		this.modeOfOperation = modeOfOperation;
	}

	@JsonProperty("accType")
	public String getAccType() {
		return accType;
	}

	@JsonProperty("accType")
	public void setAccType(String accType) {
		this.accType = accType;
	}

	@JsonProperty("add2")
	public String getAdd2() {
		return add2;
	}

	@JsonProperty("add2")
	public void setAdd2(String add2) {
		this.add2 = add2;
	}

	@JsonProperty("add1")
	public String getAdd1() {
		return add1;
	}

	@JsonProperty("add1")
	public void setAdd1(String add1) {
		this.add1 = add1;
	}

	@JsonProperty("email")
	public String getEmail() {
		return email;
	}

	@JsonProperty("email")
	public void setEmail(String email) {
		this.email = email;
	}

	@JsonProperty("add3")
	public String getAdd3() {
		return add3;
	}

	@JsonProperty("add3")
	public void setAdd3(String add3) {
		this.add3 = add3;
	}

	@JsonProperty("depositAmount")
	public String getDepositAmount() {
		return depositAmount;
	}

	@JsonProperty("depositAmount")
	public void setDepositAmount(String depositAmount) {
		this.depositAmount = depositAmount;
	}

	@JsonProperty("custNo")
	public String getCustNo() {
		return custNo;
	}

	@JsonProperty("custNo")
	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	@JsonProperty("nameTitle")
	public String getNameTitle() {
		return nameTitle;
	}

	@JsonProperty("nameTitle")
	public void setNameTitle(String nameTitle) {
		this.nameTitle = nameTitle;
	}

	@JsonProperty("accNo")
	public String getAccNo() {
		return accNo;
	}

	@JsonProperty("accNo")
	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}

	@JsonProperty("isNew")
	public String getIsNew() {
		return isNew;
	}

	@JsonProperty("isNew")
	public void setIsNew(String isNew) {
		this.isNew = isNew;
	}

	@JsonProperty("prodCode")
	public String getProdCode() {
		return prodCode;
	}

	@JsonProperty("prodCode")
	public void setProdCode(String prodCode) {
		this.prodCode = prodCode;
	}

	@JsonProperty("areaCode")
	public String getAreaCode() {
		return areaCode;
	}

	@JsonProperty("areaCode")
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	@JsonProperty("month")
	public String getMonth() {
		return month;
	}

	@JsonProperty("month")
	public void setMonth(String month) {
		this.month = month;
	}

	@JsonProperty("accHolderType")
	public String getAccHolderType() {
		return accHolderType;
	}

	@JsonProperty("accHolderType")
	public void setAccHolderType(String accHolderType) {
		this.accHolderType = accHolderType;
	}

	@JsonProperty("pinCode")
	public String getPinCode() {
		return pinCode;
	}

	@JsonProperty("pinCode")
	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("days")
	public String getDays() {
		return days;
	}

	@JsonProperty("days")
	public void setDays(String days) {
		this.days = days;
	}

	@JsonProperty("isPigMeAcc")
	public String getIsPigMeAcc() {
		return isPigMeAcc;
	}

	@JsonProperty("isPigMeAcc")
	public void setIsPigMeAcc(String isPigMeAcc) {
		this.isPigMeAcc = isPigMeAcc;
	}

	@JsonProperty("panCardNo")
	public String getPanCardNo() {
		return panCardNo;
	}

	@JsonProperty("panCardNo")
	public void setPanCardNo(String panCardNo) {
		this.panCardNo = panCardNo;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

}
