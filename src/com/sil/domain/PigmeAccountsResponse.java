package com.sil.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class PigmeAccountsResponse implements Serializable{
	private static final long serialVersionUID = -1236189271574975048L;
	private String accNo;
	private String brCode;
	private String formattedAcc;
	private String name;
	private String custNo;
	private String balance;
	private String response;
	private String errorMsg;
	private Date accOpnDt; 
	private String accOpnDtStr;
	private String accNo15;
	
	public String getAccOpnDtStr() {
		return accOpnDtStr;
	}
	public void setAccOpnDtStr(String accOpnDtStr) {
		this.accOpnDtStr = accOpnDtStr;
	}
	private ArrayList<PigmeAccountsResponse> accList=new ArrayList<>();
	public String getBalance() {
		return balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}
	public ArrayList<PigmeAccountsResponse> getAccList() {
		return accList;
	}
	public void setAccList(ArrayList<PigmeAccountsResponse> accList) {
		this.accList = accList;
	}
	public String getAccNo() {
		return accNo;
	}
	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}
	public String getBrCode() {
		return brCode;
	}
	public void setBrCode(String brCode) {
		this.brCode = brCode;
	}
	public String getFormattedAcc() {
		return formattedAcc;
	}
	public void setFormattedAcc(String formattedAcc) {
		this.formattedAcc = formattedAcc;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCustNo() {
		return custNo;
	}
	public void setCustNo(String custNo) {
		this.custNo = custNo;
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
	public Date getAccOpnDt() {
		return accOpnDt;
	}
	public void setAccOpnDt(Date accOpnDt) {
		this.accOpnDt = accOpnDt;
	}
	
	public String getAccNo15() {
		return accNo15;
	}
	public void setAccNo15(String accNo15) {
		this.accNo15 = accNo15;
	}
	@Override
	public String toString() {
		return "PigmeAccountsResponse [accNo=" + accNo + ", brCode=" + brCode + ", formattedAcc=" + formattedAcc
				+ ", name=" + name + ", custNo=" + custNo + ", balance=" + balance + ", response=" + response
				+ ", errorMsg=" + errorMsg + ", accOpnDt=" + accOpnDt + ", accOpnDtStr=" + accOpnDtStr + ", accNo15="
				+ accNo15 + ", accList=" + accList + "]";
	}
	
	
	
}
