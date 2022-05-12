package com.sil.commonswitch;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class AccountInfo implements Serializable{
	private static final long serialVersionUID = 2744830461656937863L;
	private String accNo;
	private String brCode;
	private String aadharNo;
	private String response;
	private String errorMsg; 
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
	
	public String getAadharNo() {
		return aadharNo;
	}
	public void setAadharNo(String aadharNo) {
		this.aadharNo = aadharNo;
	}
	@Override
	public String toString() {
		return "AccountInfo [accNo=" + accNo + ", brCode=" + brCode + ", response=" + response + ", errorMsg="
				+ errorMsg + "]";
	}
	
}
