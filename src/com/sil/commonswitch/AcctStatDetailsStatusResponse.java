package com.sil.commonswitch;

import java.io.Serializable;
import java.util.Arrays;
import javax.xml.bind.annotation.XmlElement;

@javax.xml.bind.annotation.XmlRootElement
public class AcctStatDetailsStatusResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	private String refNo;
	private int status;
	private String errorMsg = " ";
	private String resp = " ";
	private boolean flag = false;
	
	public String getRefNo() {
		return refNo;
	}
	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public String getResp() {
		return resp;
	}
	public void setResp(String resp) {
		this.resp = resp;
	}
	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	@Override
	public String toString() {
		return "AcctStatDetailsStatusResponse [status=" + status + ", errorMsg=" + errorMsg + ", resp=" + resp
				+ ", flag=" + flag + "]";
	}
	

	
}
