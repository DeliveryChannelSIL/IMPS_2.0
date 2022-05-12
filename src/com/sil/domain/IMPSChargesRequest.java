package com.sil.domain;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class IMPSChargesRequest implements Serializable {
	private static final long serialVersionUID = 1L;
	private String remitterMobile = " ";
	private String remitterMMID = " ";
	private String benfMobile = " ";
	private String benfMMID = " ";
	private String benfAccNo = " ";
	private String benfIFSC = " ";
	private Double transAmt;
	private Double chargesAmount=0D;
	private String narration = " ";
	private String remitterAccNo;
	private String transType;
	private Long RequestType;
	private Long BranchCode;
	private Long ConsumerNo;
	private String RRNNo = " ";
	private String MessageType;
	private String param1 = "";
	private String param2 = "";
	private String param3 = "";
	private String param4 = "";
	private String param5 = "";
	private String param6 = "";
	private String reversal;
	private Long ApiType;
	private Date EntryDate;
	public String getRemitterMobile() {
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
	public Double getTransAmt() {
		return transAmt;
	}
	public void setTransAmt(Double transAmt) {
		this.transAmt = transAmt;
	}
	public Double getChargesAmount() {
		return chargesAmount;
	}
	public void setChargesAmount(Double chargesAmount) {
		this.chargesAmount = chargesAmount;
	}
	public String getNarration() {
		return narration;
	}
	public void setNarration(String narration) {
		this.narration = narration;
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
	public Long getRequestType() {
		return RequestType;
	}
	public void setRequestType(Long requestType) {
		RequestType = requestType;
	}
	public Long getBranchCode() {
		return BranchCode;
	}
	public void setBranchCode(Long branchCode) {
		BranchCode = branchCode;
	}
	public Long getConsumerNo() {
		return ConsumerNo;
	}
	public void setConsumerNo(Long consumerNo) {
		ConsumerNo = consumerNo;
	}
	public String getRRNNo() {
		return RRNNo;
	}
	public void setRRNNo(String rRNNo) {
		RRNNo = rRNNo;
	}
	public String getMessageType() {
		return MessageType;
	}
	public void setMessageType(String messageType) {
		MessageType = messageType;
	}
	public String getParam1() {
		return param1;
	}
	public void setParam1(String param1) {
		this.param1 = param1;
	}
	public String getParam2() {
		return param2;
	}
	public void setParam2(String param2) {
		this.param2 = param2;
	}
	public String getParam3() {
		return param3;
	}
	public void setParam3(String param3) {
		this.param3 = param3;
	}
	public String getParam4() {
		return param4;
	}
	public void setParam4(String param4) {
		this.param4 = param4;
	}
	public String getParam5() {
		return param5;
	}
	public void setParam5(String param5) {
		this.param5 = param5;
	}
	public String getParam6() {
		return param6;
	}
	public void setParam6(String param6) {
		this.param6 = param6;
	}
	public String getReversal() {
		return reversal;
	}
	public void setReversal(String reversal) {
		this.reversal = reversal;
	}
	public Long getApiType() {
		return ApiType;
	}
	public void setApiType(Long apiType) {
		ApiType = apiType;
	}
	public Date getEntryDate() {
		return EntryDate;
	}
	public void setEntryDate(Date entryDate) {
		EntryDate = entryDate;
	}
	@Override
	public String toString() {
		return "IMPSTransactionRequest [remitterMobile=" + remitterMobile + ", remitterMMID=" + remitterMMID
				+ ", benfMobile=" + benfMobile + ", benfMMID=" + benfMMID + ", benfAccNo=" + benfAccNo + ", benfIFSC="
				+ benfIFSC + ", transAmt=" + transAmt + ", ChargesAmount=" + chargesAmount + ", narration=" + narration
				+ ", remitterAccNo=" + remitterAccNo + ", transType=" + transType + ", RequestType=" + RequestType
				+ ", BranchCode=" + BranchCode + ", ConsumerNo=" + ConsumerNo + ", RRNNo=" + RRNNo + ", MessageType="
				+ MessageType + ", param1=" + param1 + ", param2=" + param2 + ", param3=" + param3 + ", param4="
				+ param4 + ", param5=" + param5 + ", param6=" + param6 + ", reversal=" + reversal + ", ApiType="
				+ ApiType + ", EntryDate=" + EntryDate + "]";
	}
	
	
}
