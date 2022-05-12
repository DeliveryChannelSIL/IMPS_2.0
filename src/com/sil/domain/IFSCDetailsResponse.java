package com.sil.domain;
import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
@javax.xml.bind.annotation.XmlRootElement
public class IFSCDetailsResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	private String response = " ";
	private String errorMessage = " ";
	private String errorCode = " ";
	private Long lbrCode = 0l;
	private String custNo = " ";
	private String ifsccd = " ";
	private String bankname = " ";
	private String branchname = " ";
	private Long bankrbicd = new Long(0);
	private Long branchrbicd = new Long(0);
	private String tr = " ";
	private Date uploaddate = new Date(0);
	private Long rtgsneftcd = new Long(0);
	private String addr1 = " ";
	private String addr2 = " ";
	private String addr3 = " ";
	private String city = " ";
	private String state = " ";
	private String area = " ";
	
	public IFSCDetailsResponse() {

	}

	@XmlElement()
	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	@XmlElement()
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@XmlElement()
	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	@XmlElement()
	public String getCustNo() {
		return custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	@XmlElement()
	public Long getLbrCode() {
		return lbrCode;
	}

	public void setLbrCode(Long lbrCode) {
		this.lbrCode = lbrCode;
	}

	public String getIfsccd() {
		return ifsccd;
	}

	public void setIfsccd(String ifsccd) {
		this.ifsccd = ifsccd;
	}

	public String getBankname() {
		return bankname;
	}

	public void setBankname(String bankname) {
		this.bankname = bankname;
	}

	public String getBranchname() {
		return branchname;
	}

	public void setBranchname(String branchname) {
		this.branchname = branchname;
	}

	public Long getBankrbicd() {
		return bankrbicd;
	}

	public void setBankrbicd(Long bankrbicd) {
		this.bankrbicd = bankrbicd;
	}

	public Long getBranchrbicd() {
		return branchrbicd;
	}

	public void setBranchrbicd(Long branchrbicd) {
		this.branchrbicd = branchrbicd;
	}

	public String getTr() {
		return tr;
	}

	public void setTr(String tr) {
		this.tr = tr;
	}

	public Date getUploaddate() {
		return uploaddate;
	}

	public void setUploaddate(Date uploaddate) {
		this.uploaddate = uploaddate;
	}

	public Long getRtgsneftcd() {
		return rtgsneftcd;
	}

	public void setRtgsneftcd(Long rtgsneftcd) {
		this.rtgsneftcd = rtgsneftcd;
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

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}
}
