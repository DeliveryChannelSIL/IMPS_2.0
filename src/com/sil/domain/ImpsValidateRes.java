package com.sil.domain;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "responseCode", "responseMessage", "txnId", "payeeName" })
@Generated("jsonschema2pojo")
public class ImpsValidateRes {

	@JsonProperty("responseCode")
	private String responseCode;
	@JsonProperty("responseMessage")
	private String responseMessage;
	@JsonProperty("txnId")
	private String txnId;
	@JsonProperty("payeeName")
	private String payeeName;
	
	@JsonProperty("responseCode")
	public String getResponseCode() {
		return responseCode;
	}

	@JsonProperty("responseCode")
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	@JsonProperty("responseMessage")
	public String getResponseMessage() {
		return responseMessage;
	}

	@JsonProperty("responseMessage")
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	@JsonProperty("txnId")
	public String getTxnId() {
		return txnId;
	}

	@JsonProperty("txnId")
	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}

	@JsonProperty("payeeName")
	public String getPayeeName() {
		return payeeName;
	}

	@JsonProperty("payeeName")
	public void setPayeeName(String payeeName) {
		this.payeeName = payeeName;
	}

	@Override
	public String toString() {
		return "ImpsValidateRes [responseCode=" + responseCode + ", responseMessage=" + responseMessage + ", txnId="
				+ txnId + ", payeeName=" + payeeName + "]";
	}

	

}