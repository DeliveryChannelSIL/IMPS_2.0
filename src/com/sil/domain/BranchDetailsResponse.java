package com.sil.domain;
import java.io.Serializable;
import java.util.ArrayList;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

@javax.xml.bind.annotation.XmlRootElement
public class BranchDetailsResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	private String response = " ";
	private String errorMessage = " ";
	private String errorCode = " ";
	private Long lbrCode = 0l;
	private String custNo = " ";
	private String[] arrayOutput;
	private Map<String, String> mapOutput = new LinkedHashMap<String, String>();
	private List<String> listOutput = new ArrayList<String>();
	private List<BranchDetailsResponse> branchDetailsResponseList = new ArrayList<BranchDetailsResponse>();
	private String bankName = " ";
	private String city = " ";
	private String contactNo = " ";
	private String contactPerson = " ";
	private String email = " ";
	private String ifscCode = " ";
	private String language = " ";
	private String latitude = " ";
	private String longitude = " ";
	private String telephone = " ";
	private String title = " ";
	private List<IFSCDetailsResponse> ifscDetailsResponseList = new ArrayList<IFSCDetailsResponse>();

	public BranchDetailsResponse() {

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

	@XmlElement()
	public String[] getArrayOutput() {
		return arrayOutput;
	}

	public void setArrayOutput(String[] arrayOutput) {
		this.arrayOutput = arrayOutput;
	}

	public Map<String, String> getMapOutput() {
		return mapOutput;
	}

	public void setMapOutput(Map<String, String> mapOutput) {
		this.mapOutput = mapOutput;
	}

	public List<String> getListOutput() {
		return listOutput;
	}

	public void setListOutput(List<String> listOutput) {
		this.listOutput = listOutput;
	}

	@XmlElement()
	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	@XmlElement()
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@XmlElement()
	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	@XmlElement()
	public String getContactPerson() {
		return contactPerson;
	}

	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	@XmlElement()
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@XmlElement()
	public String getIfscCode() {
		return ifscCode;
	}

	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	@XmlElement()
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	@XmlElement()
	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	@XmlElement()
	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	@XmlElement()
	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	@XmlElement()
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<BranchDetailsResponse> getBranchDetailsResponseList() {
		return branchDetailsResponseList;
	}

	public void setBranchDetailsResponseList(List<BranchDetailsResponse> branchDetailsResponseList) {
		this.branchDetailsResponseList = branchDetailsResponseList;
	}

	public List<IFSCDetailsResponse> getIfscDetailsResponseList() {
		return ifscDetailsResponseList;
	}

	public void setIfscDetailsResponseList(List<IFSCDetailsResponse> ifscDetailsResponseList) {
		this.ifscDetailsResponseList = ifscDetailsResponseList;
	}
}
