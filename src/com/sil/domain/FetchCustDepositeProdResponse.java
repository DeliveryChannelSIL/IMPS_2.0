package com.sil.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;



 
//@JsonPropertyOrder({ "products", "customerNo", "errorMessage", "errorCode", "response" })
@XmlRootElement
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class FetchCustDepositeProdResponse{


	//@JsonProperty("products")
	private List<ProductResponse> prdNo;
	//@JsonProperty("customerNo")
	private String custNo;
	//@JsonProperty("errorMessage")
	private String errorMessage;
	//@JsonProperty("errorCode")
	private String errorCode;
	//@JsonProperty("response")
	private String response;
	
	
	//@XmlElement(name="customerNo")
	public String getCustNo() {
		return custNo;
	}

	
	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	
	public String getErrorMessage() {
		return errorMessage;
	}

	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	
	public String getResponse() {
		return response;
	}

	
	public void setResponse(String response) {
		this.response = response;
	}

	public List<ProductResponse> getPrdNo() {
		return prdNo;
	}

	public void setPrdNo(List<ProductResponse> prdNo) {
		this.prdNo = prdNo;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	@Override
	public String toString() {
		return "FetchCustDepositeProdResponse [prdNo=" + prdNo + ", custNo=" + custNo + ", errorMessage=" + errorMessage
				+ ", errorCode=" + errorCode + ", response=" + response + "]";
	}

	

}
