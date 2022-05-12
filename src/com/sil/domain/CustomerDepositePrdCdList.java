package com.sil.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlRootElement
public class CustomerDepositePrdCdList {

	private List<ProductResponse> products;
	//private boolean Success=false;
	private String response="";
	private String errorMessage="";
	private String customerNo="";
	private String errorCode="";
	
	public List<ProductResponse> getProducts() {
		return products;
	}

	public void setProducts(List<ProductResponse> products) {
		this.products = products;
	}

	
	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMsg) {
		this.errorMessage = errorMsg;
	}
	
	public String getCustomerNo() {
		return customerNo;
	}

	public void setCustomerNo(String customerNo) {
		this.customerNo = customerNo;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	@Override
	public String toString() {
		return "DepositePrdCdList [Products=" + products + ", response=" + response
				+ ", errorMsg=" + errorMessage + "]";
	}
	
	
	
}
