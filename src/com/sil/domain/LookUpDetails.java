package com.sil.domain;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LookUpDetails {
	private ArrayList<LookUpResponse> titleList=new ArrayList<>();
	private ArrayList<LookUpResponse> areaList=new ArrayList<>();
	private ArrayList<LookUpResponse> cityList=new ArrayList<>();
	private ArrayList<LookUpResponse> contryList=new ArrayList<>();
	private ArrayList<LookUpResponse> individualList=new ArrayList<>();
	private ArrayList<LookUpResponse> modeOfOperationList=new ArrayList<>();
	private ArrayList<LookUpResponse> panList=new ArrayList<>();
	private ArrayList<LookUpResponse> prodCodeList=new ArrayList<>();
	private ArrayList<LookUpResponse> stateList=new ArrayList<>();
	private ArrayList<LookUpResponse> acctypeList=new ArrayList<>();
	private String response;
	private String errorMsg;
	
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	@XmlElement()
	public ArrayList<LookUpResponse> getTitleList() {
		return titleList;
	}
	public void setTitleList(ArrayList<LookUpResponse> titleList) {
		this.titleList = titleList;
	}
	@XmlElement()
	public ArrayList<LookUpResponse> getAreaList() {
		return areaList;
	}
	public void setAreaList(ArrayList<LookUpResponse> areaList) {
		this.areaList = areaList;
	}
	@XmlElement()
	public ArrayList<LookUpResponse> getCityList() {
		return cityList;
	}
	public void setCityList(ArrayList<LookUpResponse> cityList) {
		this.cityList = cityList;
	}
	@XmlElement()
	public ArrayList<LookUpResponse> getContryList() {
		return contryList;
	}
	public void setContryList(ArrayList<LookUpResponse> contryList) {
		this.contryList = contryList;
	}
	@XmlElement()
	public ArrayList<LookUpResponse> getIndividualList() {
		return individualList;
	}
	public void setIndividualList(ArrayList<LookUpResponse> individualList) {
		this.individualList = individualList;
	}
	@XmlElement()
	public ArrayList<LookUpResponse> getModeOfOperationList() {
		return modeOfOperationList;
	}
	public void setModeOfOperationList(ArrayList<LookUpResponse> modeOfOperationList) {
		this.modeOfOperationList = modeOfOperationList;
	}
	@XmlElement()
	public ArrayList<LookUpResponse> getPanList() {
		return panList;
	}
	public void setPanList(ArrayList<LookUpResponse> panList) {
		this.panList = panList;
	}
	@XmlElement()
	public ArrayList<LookUpResponse> getProdCodeList() {
		return prodCodeList;
	}
	public void setProdCodeList(ArrayList<LookUpResponse> prodCodeList) {
		this.prodCodeList = prodCodeList;
	}
	@XmlElement()
	public ArrayList<LookUpResponse> getStateList() {
		return stateList;
	}
	public void setStateList(ArrayList<LookUpResponse> stateList) {
		this.stateList = stateList;
	}
	@XmlElement()
	public ArrayList<LookUpResponse> getAcctypeList() {
		return acctypeList;
	}
	public void setAcctypeList(ArrayList<LookUpResponse> acctypeList) {
		this.acctypeList = acctypeList;
	}
}
