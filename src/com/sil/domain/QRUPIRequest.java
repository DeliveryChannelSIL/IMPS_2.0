package com.sil.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QRUPIRequest {

	
	private String transactionType;
	
	private String accountNo;
	
	private String agentAcctNo;
	
	private String rrn;
	
	private String remark;
	
	
	private Double amt= 0.0;
	
	private String vpa;

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getRrn() {
		return rrn;
	}

	public void setRrn(String rrn) {
		this.rrn = rrn;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Double getAmt() {
		return amt;
	}

	public void setAmt(Double amt) {
		this.amt = amt;
	}

	public String getVpa() {
		return vpa;
	}

	public void setVpa(String vpa) {
		this.vpa = vpa;
	}

	public String getAgentAcctNo() {
		return agentAcctNo;
	}

	public void setAgentAcctNo(String agentAcctNo) {
		this.agentAcctNo = agentAcctNo;
	}

	@Override
	public String toString() {
		return "UPIRequest [transactionType=" + transactionType + ", accountNo=" + accountNo + ", rrn=" + rrn
				+ ", remark=" + remark + ", amt=" + amt + "]";
	}
	
	
}
