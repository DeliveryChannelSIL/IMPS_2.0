package com.sil.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "GetStatus")
@XmlType(propOrder = { "version", "txnId", "bankCode", "accountNo", "txnAmount", "bankTxnId", "txnStatus", "txnDesc",
		"freeText1", "freeText2", "freeText3" })
public class GetStatus {
	private String FreeText2;
	private String FreeText1;
	private String TxnStatus;
	private String BankTxnId;
	private String FreeText3;
	private String BankCode;
	private String AccountNo;
	private String Version;
	private String TxnAmount;
	private String TxnId;
	private String TxnDesc;

	@XmlElement(name = "FreeText2")
	public String getFreeText2() {
		return FreeText2;
	}

	public void setFreeText2(String FreeText2) {
		this.FreeText2 = FreeText2;
	}

	@XmlElement(name = "FreeText1")
	public String getFreeText1() {
		return FreeText1;
	}

	public void setFreeText1(String FreeText1) {
		this.FreeText1 = FreeText1;
	}

	@XmlElement(name = "TxnStatus")
	public String getTxnStatus() {
		return TxnStatus;
	}

	public void setTxnStatus(String TxnStatus) {
		this.TxnStatus = TxnStatus;
	}

	@XmlElement(name = "BankTxnId")
	public String getBankTxnId() {
		return BankTxnId;
	}

	public void setBankTxnId(String BankTxnId) {
		this.BankTxnId = BankTxnId;
	}

	@XmlElement(name = "FreeText3")
	public String getFreeText3() {
		return FreeText3;
	}

	public void setFreeText3(String FreeText3) {
		this.FreeText3 = FreeText3;
	}

	@XmlElement(name = "BankCode")
	public String getBankCode() {
		return BankCode;
	}

	public void setBankCode(String BankCode) {
		this.BankCode = BankCode;
	}

	@XmlElement(name = "AccountNo")
	public String getAccountNo() {
		return AccountNo;
	}

	public void setAccountNo(String AccountNo) {
		this.AccountNo = AccountNo;
	}

	@XmlElement(name = "Version")
	public String getVersion() {
		return Version;
	}

	public void setVersion(String Version) {
		this.Version = Version;
	}

	@XmlElement(name = "TxnAmount")
	public String getTxnAmount() {
		return TxnAmount;
	}

	public void setTxnAmount(String TxnAmount) {
		this.TxnAmount = TxnAmount;
	}

	@XmlElement(name = "TxnId")
	public String getTxnId() {
		return TxnId;
	}

	public void setTxnId(String TxnId) {
		this.TxnId = TxnId;
	}

	@XmlElement(name = "TxnDesc")
	public String getTxnDesc() {
		return TxnDesc;
	}

	public void setTxnDesc(String TxnDesc) {
		this.TxnDesc = TxnDesc;
	}

	@Override
	public String toString() {
		return "ClassPojo [FreeText2 = " + FreeText2 + ", FreeText1 = " + FreeText1 + ", TxnStatus = " + TxnStatus
				+ ", BankTxnId = " + BankTxnId + ", FreeText3 = " + FreeText3 + ", BankCode = " + BankCode
				+ ", AccountNo = " + AccountNo + ", Version = " + Version + ", TxnAmount = " + TxnAmount + ", TxnId = "
				+ TxnId + ", TxnDesc = " + TxnDesc + "]";
	}
}
