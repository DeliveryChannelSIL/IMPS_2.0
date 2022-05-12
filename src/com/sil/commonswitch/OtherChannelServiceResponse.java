package com.sil.commonswitch;

import java.io.Serializable;
import java.util.Arrays;
import javax.xml.bind.annotation.XmlElement;

@javax.xml.bind.annotation.XmlRootElement
public class OtherChannelServiceResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	private String response = " ";
	private String errorMessage = " ";
	private String errorCode = " ";
	private String transactionId = " ";
	private String message = " ";
	private String[] output;
	private boolean valid = false;
	private long moduleType;
	private String custNo = " ";
	private String mmid = " ";
	private String mobileNo = " ";
	private String name = " ";
	private String emailId = " ";

	public OtherChannelServiceResponse() {

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
	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	@XmlElement()
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@XmlElement()
	public String[] getOutput() {
		return output;
	}

	public void setOutput(String[] output) {
		this.output = output;
	}

	@XmlElement()
	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	@XmlElement()
	public long getModuleType() {
		return moduleType;
	}

	public void setModuleType(long moduleType) {
		this.moduleType = moduleType;
	}

	@XmlElement()
	public String getCustNo() {
		return custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	@XmlElement()
	public String getMmid() {
		return mmid;
	}

	public void setMmid(String mmid) {
		this.mmid = mmid;
	}

	@XmlElement()
	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	@XmlElement()
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement()
	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	@Override
	public String toString() {
		return "OtherChannelServiceResponse [response=" + response + ", errorMessage=" + errorMessage + ", errorCode="
				+ errorCode + ", transactionId=" + transactionId + ", message=" + message + ", output="
				+ Arrays.toString(output) + ", valid=" + valid + ", moduleType=" + moduleType + ", custNo=" + custNo
				+ ", mmid=" + mmid + ", mobileNo=" + mobileNo + ", name=" + name + ", emailId=" + emailId + "]";
	}
	
}
