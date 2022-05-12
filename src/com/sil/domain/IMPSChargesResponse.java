package com.sil.domain;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class IMPSChargesResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	private String nBin;
	private String URL = " ";
	private String accountNo = " ";
	private String category = " ";
	private String custNo = "0";
	private String errorCode = null;
	private String errorMessage = null;
	private Long lbrCode = Long.valueOf(0L);
	private String mobileNo = " ";
	private String nickNameCredit = " ";
	private String nickNameDebit = " ";
	private String response = "";
	private String rrnNo = " ";
	private String stan = " ";
	private boolean valid = false;

	public String getResponse() {
		return this.response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return this.errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getRrnNo() {
		return this.rrnNo;
	}

	public void setRrnNo(String rrnNo) {
		this.rrnNo = rrnNo;
	}

	public String getStan() {
		return this.stan;
	}

	public void setStan(String stan) {
		this.stan = stan;
	}

	public String getNickNameDebit() {
		return this.nickNameDebit;
	}

	public void setNickNameDebit(String nickNameDebit) {
		this.nickNameDebit = nickNameDebit;
	}

	public String getNickNameCredit() {
		return this.nickNameCredit;
	}

	public void setNickNameCredit(String nickNameCredit) {
		this.nickNameCredit = nickNameCredit;
	}

	public String getCategory() {
		return this.category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getURL() {
		return this.URL;
	}

	public void setURL(String url) {
		this.URL = url;
	}

	public String getNBin() {
		return this.nBin;
	}

	public void setNBin(String bin) {
		this.nBin = bin;
	}

	public String getCustNo() {
		return this.custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	public Long getLbrCode() {
		return this.lbrCode;
	}

	public void setLbrCode(Long lbrCode) {
		this.lbrCode = lbrCode;
	}

	public String getMobileNo() {
		return this.mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getAccountNo() {
		return this.accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getnBin() {
		return nBin;
	}

	public void setnBin(String nBin) {
		this.nBin = nBin;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	@Override
	public String toString() {
		return "IMPSChargesResponse [nBin=" + nBin + ", URL=" + URL + ", accountNo=" + accountNo + ", category="
				+ category + ", custNo=" + custNo + ", errorCode=" + errorCode + ", errorMessage=" + errorMessage
				+ ", lbrCode=" + lbrCode + ", mobileNo=" + mobileNo + ", nickNameCredit=" + nickNameCredit
				+ ", nickNameDebit=" + nickNameDebit + ", response=" + response + ", rrnNo=" + rrnNo + ", stan=" + stan
				+ ", valid=" + valid + "]";
	}

}
