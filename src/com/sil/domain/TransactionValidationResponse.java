package com.sil.domain;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class TransactionValidationResponse {
	private String response;
	private String errorMsg;
	private String rrn;
	private String respCode;
	private String output;
	private String batchCode;
	private String setNo;;
	private String scrollNo;
	private String balance;
	private String mobNo;
	private String name;
	
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	public String getBatchCode() {
		return batchCode;
	}
	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}
	public String getSetNo() {
		return setNo;
	}
	public void setSetNo(String setNo) {
		this.setNo = setNo;
	}
	public String getScrollNo() {
		return scrollNo;
	}
	public void setScrollNo(String scrollNo) {
		this.scrollNo = scrollNo;
	}
	public String getOutput() {
		return output;
	}
	public void setOutput(String output) {
		this.output = output;
	}
	public String getRrn() {
		return rrn;
	}
	public void setRrn(String rrn) {
		this.rrn = rrn;
	}
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
	public String getMobNo() {
		return mobNo;
	}
	public void setMobNo(String mobNo) {
		this.mobNo = mobNo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "TransactionValidationResponse [response=" + response + ", errorMsg=" + errorMsg + ", rrn=" + rrn
				+ ", respCode=" + respCode + ", output=" + output + ", batchCode=" + batchCode + ", setNo=" + setNo
				+ ", scrollNo=" + scrollNo + ", balance=" + balance + ", mobNo=" + mobNo + ", name=" + name + "]";
	}
 }
