package com.sil.domain;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AccountOpenResponse implements Serializable
{
	private static final long serialVersionUID = 1L;
	private String response = " ";
	private String errorMessage = " ";
	private String accNo;
	private String custNo;
	private String brCode;
	
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getAccNo() {
		return accNo;
	}
	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}
	public String getCustNo() {
		return custNo;
	}
	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}
	public String getBrCode() {
		return brCode;
	}
	public void setBrCode(String brCode) {
		this.brCode = brCode;
	}
	@Override
	public String toString() {
		return "AccountOpenResponse [response=" + response + ", errorMessage=" + errorMessage + ", accNo=" + accNo
				+ ", custNo=" + custNo + ", brCode=" + brCode + "]";
	}
	
}
