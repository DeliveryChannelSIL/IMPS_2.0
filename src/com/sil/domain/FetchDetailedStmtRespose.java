package com.sil.domain;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sil.hbm.D001003;
import com.sil.hbm.D009011;

@XmlRootElement
public class FetchDetailedStmtRespose {

	private boolean flag=false;
	private String resp="";
	private String errorMsg="";
	private D001003 stmtDtls;
	private List<String> stmtLst;
	//private String custNo="";
	private String errorCode="";
	private String custId="";
	private String acctNo;
	private Date frmdt;
	private Date toDt;
	private String lbrCd;
	private String creditTtl;
	private String debitTtl;
	private String opStr;
	private D009011 customerDtls;
	
	
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
	@XmlElement(name="d001003")
	public D001003 getStmtDtls() {
		return stmtDtls;
	}
	public void setStmtDtls(D001003 stmtDtls) {
		this.stmtDtls = stmtDtls;
	}
	public List<String> getStmtLst() {
		return stmtLst;
	}
	public void setStmtLst(List<String> stmtLst) {
		this.stmtLst = stmtLst;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
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
	
	public Date getFrmdt() {
		return frmdt;
	}
	public void setFrmdt(Date frmdt) {
		this.frmdt = frmdt;
	}
	public Date getToDt() {
		return toDt;
	}
	public void setToDt(Date toDt) {
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
	@XmlElement(name="d009011")
	public D009011 getCustomerDtls() {
		return customerDtls;
	}
	public void setCustomerDtls(D009011 customerDtls) {
		this.customerDtls = customerDtls;
	}
	@Override
	public String toString() {
		return "FetchDetailedStmtRespose [flag=" + flag + ", resp=" + resp + ", errorMsg=" + errorMsg + ", stmtDtls="
				+ stmtDtls + ", errorCode=" + errorCode + ", custId=" + custId + ", acctNo="
				+ acctNo + ", frmdt=" + frmdt + ", toDt=" + toDt + ", lbrCd=" + lbrCd + ", creditTtl=" + creditTtl
				+ ", debitTtl=" + debitTtl + ", opStr=" + opStr + ", customerDtls=" + customerDtls + "]";
	}
		
	
}
