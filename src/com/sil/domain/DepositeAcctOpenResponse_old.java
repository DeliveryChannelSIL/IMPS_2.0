package com.sil.domain;


import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonPropertyOrder({ "accNo", "custNo", "errorMessage", "response", "Success" })
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY, getterVisibility=JsonAutoDetect.Visibility.NONE, setterVisibility=JsonAutoDetect.Visibility.NONE, creatorVisibility=JsonAutoDetect.Visibility.NONE)
public class DepositeAcctOpenResponse_old {

	
	private String accNo;
	
	private String custNo;
	
	private String errorMessage;
	
	private String response;
	
	private Boolean success;
	
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("accNo")
	public String getAccNo() {
		return accNo;
	}

	
	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}

	@JsonProperty("custNo")
	public String getCustNo() {
		return custNo;
	}

	
	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	@JsonProperty("errorMessage")
	public String getErrorMessage() {
		return errorMessage;
	}

	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@JsonProperty("responses")
	public String getResponse() {
		return response;
	}

	
	public void setResponse(String response) {
		this.response = response;
	}

	@JsonProperty("Success")
	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
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
