package com.sil.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;

@javax.xml.bind.annotation.XmlRootElement
public class IMPSFetchLoanAccountDetailsResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	private String response = " ";
	private String errorMessage = " ";
	private String errorCode = " ";
	private String customerNo = " ";
	private List<LoanAccountDetails> accountList = new ArrayList<LoanAccountDetails>();

	public IMPSFetchLoanAccountDetailsResponse() {

	}

	@XmlElement()
	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	@XmlElement()
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@XmlElement()
	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	@XmlElement()
	public String getCustomerNo() {
		return customerNo;
	}

	public void setCustomerNo(String customerNo) {
		this.customerNo = customerNo;
	}

	@XmlElement()
	public List<LoanAccountDetails> getAccountList() {
		return accountList;
	}

	public void setAccountList(List<LoanAccountDetails> accountList) {
		this.accountList = accountList;
	}

	@Override
	public String toString() {
		return "IMPSFetchLoanAccountDetailsResponse [response=" + response + ", errorMessage=" + errorMessage
				+ ", errorCode=" + errorCode + ", customerNo=" + customerNo + ", accountList=" + accountList + "]";
	}

}
