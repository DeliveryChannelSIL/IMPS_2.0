package com.sil.domain;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class BalanceResponse {
	private String accTitle;
	private String accName;	
	private String accActualBalance;
	private String accClearBalance;
	private String formattedAcc;
	private String errorMsg;
	private String response;
	
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public String getFormattedAcc() {
		return formattedAcc;
	}
	public void setFormattedAcc(String formattedAcc) {
		this.formattedAcc = formattedAcc;
	}
	public String getAccTitle() {
		return accTitle;
	}
	public void setAccTitle(String accTitle) {
		this.accTitle = accTitle;
	}
	public String getAccName() {
		return accName;
	}
	public void setAccName(String accName) {
		this.accName = accName;
	}
	public String getAccActualBalance() {
		return accActualBalance;
	}
	public void setAccActualBalance(String accActualBalance) {
		this.accActualBalance = accActualBalance;
	}
	public String getAccClearBalance() {
		return accClearBalance;
	}
	public void setAccClearBalance(String accClearBalance) {
		this.accClearBalance = accClearBalance;
	}
	@Override
	public String toString() {
		return "BalanceResponse [accTitle=" + accTitle + ", accName=" + accName + ", accActualBalance="
				+ accActualBalance + ", accClearBalance=" + accClearBalance + ", formattedAcc=" + formattedAcc + "]";
	}
}
