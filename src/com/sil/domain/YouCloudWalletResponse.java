package com.sil.domain;

import java.io.Serializable;

public class YouCloudWalletResponse implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3747386871991888442L;
	private String response;
	private String respDesc;
	private String respCode;
	private String balance;

	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public String getRespDesc() {
		return respDesc;
	}
	public void setRespDesc(String respDesc) {
		this.respDesc = respDesc;
	}
	public String getRespCode() {
		return respCode;
	}
	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	
	
}
