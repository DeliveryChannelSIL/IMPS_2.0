package com.sil.domain;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

public class RtgsNeftTransactionResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private String RRN = " ";

	private boolean Success = false;

	private String Message = null;

	private String ResponseCode = null;

	private String ResponseMessage = " ";

	private String InnerErrorMessage = " ";

	private String TransactionMessage = " ";

	public RtgsNeftTransactionResponse() {
		// TODO Auto-generated constructor stub
	}

	public boolean isSuccess() {
		return Success;
	}

	@XmlElement(name = "Success")
	public void setSuccess(boolean success) {
		Success = success;
	}

	public String getRRN() {
		return RRN;
	}

	public void setRRN(String rRN) {
		RRN = rRN;
	}

	public String getMessage() {
		return Message;
	}

	@XmlElement(name = "ResponseMessage")
	public void setMessage(String message) {
		Message = message;
	}

	public String getResponseCode() {
		return ResponseCode;
	}

	@XmlElement(name = "ResponseCode")
	public void setResponseCode(String responseCode) {
		ResponseCode = responseCode;
	}

	public String getResponseMessage() {
		return ResponseMessage;
	}

	@XmlElement(name = "InnerErrorMessage")
	public void setResponseMessage(String responseMessage) {
		ResponseMessage = responseMessage;
	}

	public String getInnerErrorMessage() {
		return InnerErrorMessage;
	}

	@XmlElement(name = "Message")
	public void setInnerErrorMessage(String innerErrorMessage) {
		InnerErrorMessage = innerErrorMessage;
	}

	public String getTransactionMessage() {
		return TransactionMessage;
	}

	@XmlElement(name = "TransactionMessage")
	public void setTransactionMessage(String transactionMessage) {
		TransactionMessage = transactionMessage;
	}

	@Override
	public String toString() {
		return "RtgsNeftTransactionResponse [RRN=" + RRN + ", Success=" + Success + ", Message=" + Message
				+ ", ResponseCode=" + ResponseCode + ", ResponseMessage=" + ResponseMessage + ", InnerErrorMessage="
				+ InnerErrorMessage + ", TransactionMessage=" + TransactionMessage + "]";
	}

}
