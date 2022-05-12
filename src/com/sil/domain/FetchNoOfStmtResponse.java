package com.sil.domain;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sil.hbm.D350023;

@XmlRootElement
public class FetchNoOfStmtResponse {

	private List<D350023> d350023s;
	//private boolean Success=false;
	private boolean flag=false;
	private String resp="";
	private String errorMsg="";
	private String custId="0";
	private String acctNo="0";
	private String frmdt="0";
	private String toDt="0";
	private String lbrCd="0";
	private String creditTtl="0";
	private String debitTtl="0";
	private String opStr="0";
	
	
	public List<D350023> getD350023s() {
		return d350023s;
	}
	public void setD350023s(List<D350023> d350023s) {
		this.d350023s = d350023s;
	}
	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	public String getResp() {
		return resp;
	}
	public void setResp(String resp) {
		this.resp = resp;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getAcctNo() {
		return acctNo;
	}
	public void setAcctNo(String acctNo) {
		this.acctNo = acctNo;
	}
	public String getFrmdt() {
		return frmdt;
	}
	public void setFrmdt(String frmdt) {
		this.frmdt = frmdt;
	}
	public String getToDt() {
		return toDt;
	}
	public void setToDt(String toDt) {
		this.toDt = toDt;
	}
	public String getLbrCd() {
		return lbrCd;
	}
	public void setLbrCd(String lbrCd) {
		this.lbrCd = lbrCd;
	}
	public String getCreditTtl() {
		return creditTtl;
	}
	public void setCreditTtl(String creditTtl) {
		this.creditTtl = creditTtl;
	}
	public String getDebitTtl() {
		return debitTtl;
	}
	public void setDebitTtl(String debitTtl) {
		this.debitTtl = debitTtl;
	}
	public String getOpStr() {
		return opStr;
	}
	public void setOpStr(String opStr) {
		this.opStr = opStr;
	}
	@Override
	public String toString() {
		return "FetchNoOfStmtResponse [d350023s=" + d350023s + ", flag=" + flag + ", resp=" + resp + ", errorMsg="
				+ errorMsg + ", custId=" + custId + ", acctNo=" + acctNo + ", frmdt=" + frmdt + ", toDt=" + toDt
				+ ", lbrCd=" + lbrCd + ", creditTtl=" + creditTtl + ", debitTtl=" + debitTtl + ", opStr=" + opStr + "]";
	}
	
	
	
	
	
}
