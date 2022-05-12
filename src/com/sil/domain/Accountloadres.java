package com.sil.domain;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Accountloadres {
	private String desc;
	private String customerid;
	private String status;
	private String BankRefID;

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getCustomerid() {
		return customerid;
	}

	public void setCustomerid(String customerid) {
		this.customerid = customerid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBankRefID() {
		return BankRefID;
	}

	public void setBankRefID(String BankRefID) {
		this.BankRefID = BankRefID;
	}

	@Override
	public String toString() {
		return "ClassPojo [desc = " + desc + ", customerid = " + customerid + ", status = " + status + ", BankRefID = "
				+ BankRefID + "]";
	}
}
