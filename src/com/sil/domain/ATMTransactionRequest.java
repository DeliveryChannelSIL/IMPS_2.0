package com.sil.domain;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ATMTransactionRequest implements Serializable {
	private static final long serialVersionUID = -7636918810267940456L;
	private String atmid;
	private String rrn;
	private String acqId;
	private String cardNo;
	private double amount;
	private String atmAccId;
	private String toAccId;
	private String brCode;
	private String toBrcode;
	private String atmAuthNo;
	private String networkId;
	private String transType;
	
	public String getAtmid() {
		return atmid;
	}

	public void setAtmid(String atmid) {
		this.atmid = atmid;
	}

	public String getRrn() {
		return rrn;
	}

	public void setRrn(String rrn) {
		this.rrn = rrn;
	}

	public String getAcqId() {
		return acqId;
	}

	public void setAcqId(String acqId) {
		this.acqId = acqId;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getAtmAccId() {
		return atmAccId;
	}

	public void setAtmAccId(String atmAccId) {
		this.atmAccId = atmAccId;
	}

	public String getToAccId() {
		return toAccId;
	}

	public void setToAccId(String toAccId) {
		this.toAccId = toAccId;
	}

	public String getBrCode() {
		return brCode;
	}

	public void setBrCode(String brCode) {
		this.brCode = brCode;
	}

	public String getToBrcode() {
		return toBrcode;
	}

	public void setToBrcode(String toBrcode) {
		this.toBrcode = toBrcode;
	}

	public String getAtmAuthNo() {
		return atmAuthNo;
	}

	public void setAtmAuthNo(String atmAuthNo) {
		this.atmAuthNo = atmAuthNo;
	}

	public String getNetworkId() {
		return networkId;
	}

	public void setNetworkId(String networkId) {
		this.networkId = networkId;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	@Override
	public String toString() {
		return "ATMTransactionRequest [atmid=" + atmid + ", rrn=" + rrn + ", acqId=" + acqId + ", cardNo=" + cardNo
				+ ", amount=" + amount + ", atmAccId=" + atmAccId + ", toAccId=" + toAccId + ", brCode=" + brCode
				+ ", toBrcode=" + toBrcode + ", atmAuthNo=" + atmAuthNo + ", networkId=" + networkId + ", transType="
				+ transType + "]";
	}

	
}
