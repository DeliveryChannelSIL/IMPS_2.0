package com.sil.util;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class Customer {
	private String custno;
	private String name;
	private List<Account> accounts;
	private String pan;
	private String addr1;
	private String addr2;
	private String addr3;
	private String cityCode;
	private String pinCode;
	private String mobno;
	private String isActiveCustomer;
	private String email;
	
	private Double tdsPercentage;
	private char tdsYn;
	
	public String getCustno() {
		return custno;
	}
	public void setCustno(String custno) {
		this.custno = custno;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Account> getAccounts() {
		return accounts;
	}
	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}
	public String getPan() {
		return pan;
	}
	public void setPan(String pan) {
		this.pan = pan;
	}
	public String getAddr1() {
		return addr1;
	}
	public void setAddr1(String addr1) {
		this.addr1 = addr1;
	}
	public String getAddr2() {
		return addr2;
	}
	public void setAddr2(String addr2) {
		this.addr2 = addr2;
	}
	public String getAddr3() {
		return addr3;
	}
	public void setAddr3(String addr3) {
		this.addr3 = addr3;
	}
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	public String getPinCode() {
		return pinCode;
	}
	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}
	public String getMobno() {
		return mobno;
	}
	public void setMobno(String mobno) {
		this.mobno = mobno;
	}
	public String getIsActiveCustomer() {
		return isActiveCustomer;
	}
	public void setIsActiveCustomer(String isActiveCustomer) {
		this.isActiveCustomer = isActiveCustomer;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Double getTdsPercentage() {
		return tdsPercentage;
	}
	public void setTdsPercentage(Double tdsPercentage) {
		this.tdsPercentage = tdsPercentage;
	}
	public char getTdsYn() {
		return tdsYn;
	}
	public void setTdsYn(char tdsYn) {
		this.tdsYn = tdsYn;
	}
	
}
