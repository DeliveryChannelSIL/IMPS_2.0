package com.sil.domain;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class AccountDetails implements Serializable{
	private static final long serialVersionUID = -3341160208071732401L;
	private String brachCode;
	private String accCode;
	private String mmid;
	private String mobNo;
	private String formattedAccount;
	private String name;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBrachCode() {
		return brachCode;
	}
	public void setBrachCode(String brachCode) {
		this.brachCode = brachCode;
	}

	public String getAccCode() {
		return accCode;
	}
	public void setAccCode(String accCode) {
		this.accCode = accCode;
	}

	public String getMmid() {
		return mmid;
	}
	public void setMmid(String mmid) {
		this.mmid = mmid;
	}

	public String getMobNo() {
		return mobNo;
	}
	public void setMobNo(String mobNo) {
		this.mobNo = mobNo;
	}

	public String getFormattedAccount() {
		return formattedAccount;
	}
	public void setFormattedAccount(String formattedAccount) {
		this.formattedAccount = formattedAccount;
	}
}
