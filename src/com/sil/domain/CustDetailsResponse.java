package com.sil.domain;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class CustDetailsResponse {
	private String response;
	private String errorMsg;
	private List<CustomerDetails> custList;
	
	@XmlElement()
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	@XmlElement()
	public List<CustomerDetails> getCustList() {
		return custList;
	}
	public void setCustList(List<CustomerDetails> custList) {
		this.custList = custList;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	@Override
	public String toString() {
		return "CustDetailsResponse [response=" + response + ", errorMsg=" + errorMsg + ", custList=" + custList + "]";
	}
	
}
