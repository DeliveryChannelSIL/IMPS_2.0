package com.sil.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement
public class DepositePrdCdList {

	private List<DepositeParameters> schemeCodelist;
	private boolean Success=false;
	private String response="";
	private String errorMsg="";
	
	public List<DepositeParameters> getSchemeCodelist() {
		return schemeCodelist;
	}

	public void setSchemeCodelist(List<DepositeParameters> schemeCodelist) {
		this.schemeCodelist = schemeCodelist;
	}

	@XmlElement(name="Success")
	public boolean isSuccess() {
		return Success;
	}

	public void setSuccess(boolean success) {
		Success = success;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	@Override
	public String toString() {
		return "DepositePrdCdList [schemeCodelist=" + schemeCodelist + ", Success=" + Success + ", response=" + response
				+ ", errorMsg=" + errorMsg + "]";
	}
	
	
	
}
