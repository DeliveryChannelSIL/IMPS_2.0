package com.sil.hbm;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
@javax.xml.bind.annotation.XmlRootElement
public class IBFundTransactionRequest implements Serializable {
	private static final long serialVersionUID = 1L;
	private String fromAccNo;
	private String toAccNo;
	private Double transAmnt;
	private String encryptedOltpPwd;
	private String narration;
	private String trType;
	private String freqType;
	private Long freqInDays;
	private Long noOfPayments;
	private String startDate;
	private String endDate;
	private String custNo;
	private String toIfscCode;
	private String benNickName;
	private String benAdd1;
	private String benAdd2;
	private String benMobileNo;
	private String rtgsNEFT;
	private Double chrgAmnt;
	private String cutOffYN;
	private String nextTrnxDate;
	private String passOption = " ";

	public IBFundTransactionRequest() {

	}

	@XmlElement
	public String getFromAccNo() {
		return fromAccNo;
	}

	public void setFromAccNo(String fromBrAccNo) {
		this.fromAccNo = fromBrAccNo;
	}

	@XmlElement
	public String getToAccNo() {
		return toAccNo;
	}

	public void setToAccNo(String toAccNo) {
		this.toAccNo = toAccNo;
	}

	@XmlElement
	public Double getTransAmnt() {
		return transAmnt;
	}

	public void setTransAmnt(Double transAmnt) {
		this.transAmnt = transAmnt;
	}

	@XmlElement
	public String getEncryptedOltpPwd() {
		return encryptedOltpPwd;
	}

	public void setEncryptedOltpPwd(String encryptedOltpPwd) {
		this.encryptedOltpPwd = encryptedOltpPwd;
	}

	@XmlElement
	public String getNarration() {
		return narration;
	}

	public void setNarration(String narration) {
		this.narration = narration;
	}

	@XmlElement
	public String getTrType() {
		return trType;
	}

	public void setTrType(String trType) {
		this.trType = trType;
	}

	@XmlElement
	public String getFreqType() {
		return freqType;
	}

	public void setFreqType(String freqType) {
		this.freqType = freqType;
	}

	@XmlElement
	public Long getFreqInDays() {
		return freqInDays;
	}

	public void setFreqInDays(Long freqInDays) {
		this.freqInDays = freqInDays;
	}

	@XmlElement
	public Long getNoOfPayments() {
		return noOfPayments;
	}

	public void setNoOfPayments(Long noOfPayments) {
		this.noOfPayments = noOfPayments;
	}

	@XmlElement
	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	@XmlElement
	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	@XmlElement
	public String getCustNo() {
		return custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	@XmlElement
	public String getToIfscCode() {
		return toIfscCode;
	}

	public void setToIfscCode(String benIfscCode) {
		this.toIfscCode = benIfscCode;
	}

	@XmlElement
	public String getBenNickName() {
		return benNickName;
	}

	public void setBenNickName(String benNickName) {
		this.benNickName = benNickName;
	}

	@XmlElement
	public String getBenAdd1() {
		return benAdd1;
	}

	public void setBenAdd1(String benAdd1) {
		this.benAdd1 = benAdd1;
	}

	@XmlElement
	public String getBenAdd2() {
		return benAdd2;
	}

	public void setBenAdd2(String benAdd2) {
		this.benAdd2 = benAdd2;
	}

	@XmlElement
	public String getBenMobileNo() {
		return benMobileNo;
	}

	public void setBenMobileNo(String benMobileNo) {
		this.benMobileNo = benMobileNo;
	}

	@XmlElement
	public String getRtgsNEFT() {
		return rtgsNEFT;
	}

	public void setRtgsNEFT(String rtgsNEFT) {
		this.rtgsNEFT = rtgsNEFT;
	}

	@XmlElement
	public Double getChrgAmnt() {
		return chrgAmnt;
	}

	public void setChrgAmnt(Double chrgAmnt) {
		this.chrgAmnt = chrgAmnt;
	}

	@XmlElement
	public String getCutOffYN() {
		return cutOffYN;
	}

	public void setCutOffYN(String cutOffYN) {
		this.cutOffYN = cutOffYN;
	}

	@XmlElement
	public String getNextTrnxDate() {
		return nextTrnxDate;
	}

	public void setNextTrnxDate(String nextTrnxDate) {
		this.nextTrnxDate = nextTrnxDate;
	}

	@XmlElement
	public String getPassOption() {
		return passOption;
	}

	public void setPassOption(String passOption) {
		this.passOption = passOption;
	}

	@Override
	public String toString() {
		return "IBFundTransactionRequest [fromBrAccNo=" + fromAccNo + ", toAccNo=" + toAccNo + ", transAmnt="
				+ transAmnt + ", encryptedOltpPwd=" + encryptedOltpPwd + ", narration=" + narration + ", trType="
				+ trType + ", freqType=" + freqType + ", freqInDays=" + freqInDays + ", noOfPayments=" + noOfPayments
				+ ", startDate=" + startDate + ", endDate=" + endDate + ", custNo=" + custNo + ", toIfscCode="
				+ toIfscCode + ", benNickName=" + benNickName + ", benAdd1=" + benAdd1 + ", benAdd2=" + benAdd2
				+ ", benMobileNo=" + benMobileNo + ", rtgsNEFT=" + rtgsNEFT + ", chrgAmnt=" + chrgAmnt + ", cutOffYN="
				+ cutOffYN + ", nextTrnxDate=" + nextTrnxDate + ", passOption=" + passOption + "]";
	}
	
}
