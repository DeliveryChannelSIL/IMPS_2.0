package com.sil.domain;

import java.io.Serializable;

public class ATMTransactionResponse implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7552619546104200834L;
	private String response;
	private String errorMsg;
	private String respCode;
	private String rrn;
	
	public String getRespCode() {
		return respCode;
	}
	public void setRespCode(String respCode) {
		this.respCode = respCode;
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
	public String getRrn() {
		return rrn;
	}
	public void setRrn(String rrn) {
		this.rrn = rrn;
	}
	
}
