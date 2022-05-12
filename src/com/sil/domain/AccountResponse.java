package com.sil.domain;

import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class AccountResponse implements Serializable{
	private static final long serialVersionUID = -4853688510414450463L;
	private String reponse;
	private String errorMsg;
	private String mobNo;
	private String add1;
	private String add2;
	private String add3;
	private String email;
	
	public String getAdd1() {
		return add1;
	}
	public void setAdd1(String add1) {
		this.add1 = add1;
	}
	public String getAdd2() {
		return add2;
	}
	public void setAdd2(String add2) {
		this.add2 = add2;
	}
	public String getAdd3() {
		return add3;
	}
	public void setAdd3(String add3) {
		this.add3 = add3;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMobNo() {
		return mobNo;
	}
	public void setMobNo(String mobNo) {
		this.mobNo = mobNo;
	}
	private ArrayList<AccountDetails> accountDetails=new ArrayList<AccountDetails>();
	public String getReponse() {
		return reponse;
	}
	public void setReponse(String reponse) {
		this.reponse = reponse;
	}
	
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	@XmlElement()
	public ArrayList<AccountDetails> getAccountDetails() {
		return accountDetails;
	}
	public void setAccountDetails(ArrayList<AccountDetails> accountDetails) {
		this.accountDetails = accountDetails;
	}
}
