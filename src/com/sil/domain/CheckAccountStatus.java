package com.sil.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "CheckAccountStatus")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "version", "txnId", "bankCode", "accountNo", "accountStatus", "mobileNumber", "customerName",
		"freeText1", "freeText2", "freeText3" })
public class CheckAccountStatus {
	private String FreeText2;
	private String FreeText1;
	private String AccountStatus;
	private String FreeText3;
	private String BankCode;
	private String AccountNo;
	private String Version;
	private String TxnId;
	private String CustomerName;
	private String MobileNumber;

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

	@XmlElement(name = "AccountStatus")
	public String getAccountStatus() {
		return AccountStatus;
	}

	public void setAccountStatus(String AccountStatus) {
		this.AccountStatus = AccountStatus;
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

	@XmlElement(name = "TxnId")
	public String getTxnId() {
		return TxnId;
	}

	public void setTxnId(String TxnId) {
		this.TxnId = TxnId;
	}

	@XmlElement(name = "CustomerName")
	public String getCustomerName() {
		return CustomerName;
	}

	public void setCustomerName(String CustomerName) {
		this.CustomerName = CustomerName;
	}

	@XmlElement(name = "MobileNumber")
	public String getMobileNumber() {
		return MobileNumber;
	}

	public void setMobileNumber(String MobileNumber) {
		this.MobileNumber = MobileNumber;
	}

	@Override
	public String toString() {
		return "ClassPojo [FreeText2 = " + FreeText2 + ", FreeText1 = " + FreeText1 + ", AccountStatus = "
				+ AccountStatus + ", FreeText3 = " + FreeText3 + ", BankCode = " + BankCode + ", AccountNo = "
				+ AccountNo + ", Version = " + Version + ", TxnId = " + TxnId + ", CustomerName = " + CustomerName
				+ ", MobileNumber = " + MobileNumber + "]";
	}
}
