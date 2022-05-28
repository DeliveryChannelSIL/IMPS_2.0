package com.sil.domain;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImpsValidateReq {

	@JsonProperty("payeeAccount")
	private String payeeAccount;
	@JsonProperty("amount")
	private String amount;
	@JsonProperty("txntype")
	private String txntype;
	@JsonProperty("payeeIfsc")
	private String payeeIfsc;
	@JsonProperty("payeeAccountType")
	private String payeeAccountType;
	@JsonProperty("txnId")
	private String txnId;
	@JsonProperty("custRef")
	private String custRef;
	@JsonProperty("note")
	private String note;
	@JsonProperty("payerDeviceDetails")
	private String payerDeviceDetails;
	@JsonProperty("payerDetails")
	private String payerDetails;
	
	@JsonProperty("payeeAccount")
	public String getPayeeAccount() {
		return payeeAccount;
	}

	@JsonProperty("payeeAccount")
	public void setPayeeAccount(String payeeAccount) {
		this.payeeAccount = payeeAccount;
	}

	@JsonProperty("amount")
	public String getAmount() {
		return amount;
	}

	@JsonProperty("amount")
	public void setAmount(String amount) {
		this.amount = amount;
	}

	@JsonProperty("txntype")
	public String getTxntype() {
		return txntype;
	}

	@JsonProperty("txntype")
	public void setTxntype(String txntype) {
		this.txntype = txntype;
	}

	@JsonProperty("payeeIfsc")
	public String getPayeeIfsc() {
		return payeeIfsc;
	}

	@JsonProperty("payeeIfsc")
	public void setPayeeIfsc(String payeeIfsc) {
		this.payeeIfsc = payeeIfsc;
	}

	@JsonProperty("payeeAccountType")
	public String getPayeeAccountType() {
		return payeeAccountType;
	}

	@JsonProperty("payeeAccountType")
	public void setPayeeAccountType(String payeeAccountType) {
		this.payeeAccountType = payeeAccountType;
	}

	@JsonProperty("txnId")
	public String getTxnId() {
		return txnId;
	}

	@JsonProperty("txnId")
	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	@JsonProperty("custRef")
	public String getCustRef() {
		return custRef;
	}

	@JsonProperty("custRef")
	public void setCustRef(String custRef) {
		this.custRef = custRef;
	}

	@JsonProperty("note")
	public String getNote() {
		return note;
	}

	@JsonProperty("note")
	public void setNote(String note) {
		this.note = note;
	}

	@JsonProperty("payerDeviceDetails")
	public String getPayerDeviceDetails() {
		return payerDeviceDetails;
	}

	@JsonProperty("payerDeviceDetails")
	public void setPayerDeviceDetails(String payerDeviceDetails) {
		this.payerDeviceDetails = payerDeviceDetails;
	}

	@JsonProperty("payerDetails")
	public String getPayerDetails() {
		return payerDetails;
	}

	@JsonProperty("payerDetails")
	public void setPayerDetails(String payerDetails) {
		this.payerDetails = payerDetails;
	}

	@Override
	public String toString() {
		return "ImpsValidateReq [payeeAccount=" + payeeAccount + ", amount=" + amount + ", txntype=" + txntype
				+ ", payeeIfsc=" + payeeIfsc + ", payeeAccountType=" + payeeAccountType + ", txnId=" + txnId
				+ ", custRef=" + custRef + ", note=" + note + ", payerDeviceDetails=" + payerDeviceDetails
				+ ", payerDetails=" + payerDetails + "]";
	}

	

}