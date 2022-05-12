package com.sil.domain;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class IMPSTransactionRequest implements Serializable {
	private static final long serialVersionUID = 1L;
	private String remitterMobile = " ";
	private String remitterMMID = " ";
	private String benfMobile = " ";
	private String benfMMID = " ";
	private String benfAccNo = " ";
	private String benfIFSC = " ";
	private Double transAmt = 0.0;
	private String narration = " ";
	private String transPin = " ";
	private String RRNNo = " ";
	private String passOption = " ";
	private String accountNo;
	private String ifscCode;
	private String reversal;
	private String transType;
	private String remitterAccNo;
	private String consumerNo;
	private String operator;
	private String cardAliaceNo;
	public String getBenfAccNo() {
		return benfAccNo;
	}

	public void setBenfAccNo(String benfAccNo) {
		this.benfAccNo = benfAccNo;
	}

	public String getBenfIFSC() {
		return benfIFSC;
	}

	public void setBenfIFSC(String benfIFSC) {
		this.benfIFSC = benfIFSC;
	}

	public String getRemitterAccNo() {
		return remitterAccNo;
	}

	public void setRemitterAccNo(String remitterAccNo) {
		this.remitterAccNo = remitterAccNo;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public IMPSTransactionRequest() {

	}

	public String getRemitterMobile() {
		if(null != remitterMobile && remitterMobile.length() == 10){
			remitterMobile = "91"+remitterMobile;
		}
		
		return remitterMobile;
	}

	public void setRemitterMobile(String remitterMobile) {
		this.remitterMobile = remitterMobile;
	}

	
	public String getRemitterMMID() {
		return remitterMMID;
	}

	public void setRemitterMMID(String remitterMMID) {
		this.remitterMMID = remitterMMID;
	}

	
	public String getBenfMobile() {
		if(null != benfMobile && benfMobile.length()  == 10){
			benfMobile = "91"+benfMobile;
		}
		
		return benfMobile;
	}

	public void setBenfMobile(String benfMobile) {
		this.benfMobile = benfMobile;
	}
	public String getBenfMMID() {
		return benfMMID;
	}

	public void setBenfMMID(String benfMMID) {
		this.benfMMID = benfMMID;
	}

	public Double getTransAmt() {
		return transAmt;
	}

	public void setTransAmt(Double transAmt) {
		this.transAmt = transAmt;
	}

	public String getNarration() {
		return narration;
	}
	public void setNarration(String narration) {
		this.narration = narration;
	}
	public String getTransPin() {
		return transPin;
	}
	public void setTransPin(String transPin) {
		this.transPin = transPin;
	}
	public String getRRNNo() {
		return RRNNo;
	}
	public void setRRNNo(String no) {
		RRNNo = no;
	}
	public String getPassOption() {
		return passOption;
	}
	public void setPassOption(String passOption) {
		this.passOption = passOption;
	}
	public String getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}
	public String getIfscCode() {
		return ifscCode;
	}
	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}
	public String getReversal() {
		return reversal;
	}
	public void setReversal(String reversal) {
		this.reversal = reversal;
	}

	public String getConsumerNo() {
		return consumerNo;
	}

	public void setConsumerNo(String consumerNo) {
		this.consumerNo = consumerNo;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getCardAliaceNo() {
		return cardAliaceNo;
	}

	public void setCardAliaceNo(String cardAliaceNo) {
		this.cardAliaceNo = cardAliaceNo;
	}

	@Override
	public String toString() {
		return "IMPSTransactionRequest [remitterMobile=" + remitterMobile + ", remitterMMID=" + remitterMMID
				+ ", benfMobile=" + benfMobile + ", benfMMID=" + benfMMID + ", benfAccNo=" + benfAccNo + ", benfIFSC="
				+ benfIFSC + ", transAmt=" + transAmt + ", narration=" + narration + ", transPin=" + transPin
				+ ", RRNNo=" + RRNNo + ", passOption=" + passOption + ", accountNo=" + accountNo + ", ifscCode="
				+ ifscCode + ", reversal=" + reversal + ", transType=" + transType + ", remitterAccNo=" + remitterAccNo
				+ ", consumerNo=" + consumerNo + ", operator=" + operator + ", cardAliaceNo=" + cardAliaceNo + "]";
	}
	
}
