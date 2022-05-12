package com.sil.domain;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Accountloadreq {
	private String amount;
	private String customerid;
	private String RefNo;
	private String accnumber;
	private Header header;

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCustomerid() {
		return customerid;
	}

	public void setCustomerid(String customerid) {
		this.customerid = customerid;
	}

	public String getRefNo() {
		return RefNo;
	}

	public void setRefNo(String RefNo) {
		this.RefNo = RefNo;
	}

	public String getAccnumber() {
		return accnumber;
	}

	public void setAccnumber(String accnumber) {
		this.accnumber = accnumber;
	}

	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	@Override
	public String toString() {
		return "ClassPojo [amount = " + amount + ", customerid = " + customerid + ", RefNo = " + RefNo
				+ ", accnumber = " + accnumber + ", header = " + header + "]";
	}
}