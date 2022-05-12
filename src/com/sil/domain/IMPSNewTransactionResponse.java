package com.sil.domain;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class IMPSNewTransactionResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	private String batchCode = "";
	private String errorMsg = null;
	private String name = null;
	private String output = " ";
	private String response = " ";
	private String rrn = " ";
	private String scrollNo = " ";
	private String setNo = " ";

	private boolean Success = false;
	private String resCode;
	private String respCode;

	public String getResponse() {
		return this.response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getBatchCode() {
		return batchCode;
	}

	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getScrollNo() {
		return scrollNo;
	}

	public void setScrollNo(String scrollNo) {
		this.scrollNo = scrollNo;
	}

	public String getSetNo() {
		return setNo;
	}

	public void setSetNo(String setNo) {
		this.setNo = setNo;
	}

	@XmlElement(name="Success")
	public boolean isSuccess() {
		return Success;
	}

	public void setSuccess(boolean success) {
		Success = success;
	}

	public String getResCode() {
		return resCode;
	}

	public void setResCode(String resCode) {
		this.resCode = resCode;
	}

	public String getRespCode() {
		return respCode;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}

	@Override
	public String toString() {
		return "IMPSNewTransactionResponse [batchCode=" + batchCode + ", errorMsg=" + errorMsg + ", name=" + name
				+ ", output=" + output + ", response=" + response + ", rrn=" + rrn + ", scrollNo=" + scrollNo
				+ ", setNo=" + setNo + ", Success=" + Success + ", resCode=" + resCode + ", respCode=" + respCode + "]";
	}

}
