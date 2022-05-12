package com.sil.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Success", "Message" })
public class CustNewMobileDetails implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private boolean success = false;
	@JsonProperty("Message")
	private String message = " ";
	
	

	public CustNewMobileDetails() {

	}

	
	@JsonProperty(value="Success")
	public boolean isSuccess() {
		return success;
	}



	public void setSuccess(boolean success) {
		this.success = success;
	}


	@JsonProperty(value="Message")
	public String getMessage() {
		return message;
	}



	public void setMessage(String message) {
		this.message = message;
	}



	@Override
	public String toString() {
		return "CustNewMobileDetails [Success=" + success + ", Message=" + message + "]";
	}

	
}
