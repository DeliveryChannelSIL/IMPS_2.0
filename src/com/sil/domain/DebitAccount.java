package com.sil.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = {"version", "txnId", "bankCode", "accountNo", "txnAmount","bankTxnId","txnStatus","txnDesc","freeText1","freeText2","freeText3"})
@XmlRootElement(name="DebitAccount")
public class DebitAccount
{
    private String Version;
    private String TxnId;
    private String BankCode;
    private String AccountNo;
    private String TxnAmount;
    private String BankTxnId;
    private String TxnStatus;
    private String TxnDesc;
    private String FreeText1;
	private String FreeText2;
    private String FreeText3;
    
    @XmlElement(name = "Version")
	public String getVersion() {
		return Version;
	}
	public void setVersion(String version) {
		Version = version;
	}

	@XmlElement(name = "FreeText1")
	public String getFreeText1() {
		return FreeText1;
	}
	public void setFreeText1(String freeText1) {
		FreeText1 = freeText1;
	}

	@XmlElement(name = "FreeText2")
	public String getFreeText2() {
		return FreeText2;
	}

	public void setFreeText2(String freeText2) {
		FreeText2 = freeText2;
	}

	
	@XmlElement(name = "TxnStatus")
	public String getTxnStatus() {
		return TxnStatus;
	}


	public void setTxnStatus(String txnStatus) {
		TxnStatus = txnStatus;
	}

	@XmlElement(name = "BankTxnId")
	public String getBankTxnId() {
		return BankTxnId;
	}


	public void setBankTxnId(String bankTxnId) {
		BankTxnId = bankTxnId;
	}

	@XmlElement(name = "FreeText3")
	public String getFreeText3() {
		return FreeText3;
	}


	public void setFreeText3(String freeText3) {
		FreeText3 = freeText3;
	}

	@XmlElement(name = "BankCode")
	public String getBankCode() {
		return BankCode;
	}


	public void setBankCode(String bankCode) {
		BankCode = bankCode;
	}

	@XmlElement(name = "AccountNo")
	public String getAccountNo() {
		return AccountNo;
	}


	public void setAccountNo(String accountNo) {
		AccountNo = accountNo;
	}

	
	@XmlElement(name = "TxnAmount")
	public String getTxnAmount() {
		return TxnAmount;
	}


	public void setTxnAmount(String txnAmount) {
		TxnAmount = txnAmount;
	}

	@XmlElement(name = "TxnId")
	public String getTxnId() {
		return TxnId;
	}


	public void setTxnId(String txnId) {
		TxnId = txnId;
	}

	@XmlElement(name = "TxnDesc")
	public String getTxnDesc() {
		return TxnDesc;
	}


	public void setTxnDesc(String txnDesc) {
		TxnDesc = txnDesc;
	}


	@Override
	public String toString() {
		return "DebitAccount [FreeText2=" + FreeText2 + ", FreeText1=" + FreeText1 + ", TxnStatus=" + TxnStatus
				+ ", BankTxnId=" + BankTxnId + ", FreeText3=" + FreeText3 + ", BankCode=" + BankCode + ", AccountNo="
				+ AccountNo + ", Version=" + Version + ", TxnAmount=" + TxnAmount + ", TxnId=" + TxnId + ", TxnDesc="
				+ TxnDesc + "]";
	}
}
