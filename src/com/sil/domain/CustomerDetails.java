package com.sil.domain;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import com.sil.hbm.D009042;
import com.sil.hbm.D009047;

@XmlRootElement
public class CustomerDetails {
	private String custNo;
	private String nameTitle;
	private String longName;
	private String add1;
	private String add2;	
	private String add3;
	private String cityCode;
	private String pinCode;
	private String mobileNo;
	private String LbrCode;
	private String panNo;
	private String countryCode;
	private String mainCustNo;
	private String emailId;
	private String output;
	private String response;
	private String errorMsg;
	private String accNo;
	private D009042 odLimit;
	private D009047 odAdhocLimit;
	private String adharNo;
	private String kycStatus;
	private String kycDate;
	
	public D009047 getOdAdhocLimit() {
		return odAdhocLimit;
	}
	public void setOdAdhocLimit(D009047 odAdhocLimit) {
		this.odAdhocLimit = odAdhocLimit;
	}
	public D009042 getOdLimit() {
		return odLimit;
	}
	public void setOdLimit(D009042 odLimit) {
		this.odLimit = odLimit;
	}
	public String getAccNo() {
		return accNo;
	}
	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

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
	private List<CustomerDetails> custList;

	public List<CustomerDetails> getCustList() {
		return custList;
	}
	
	public void setCustList(List<CustomerDetails> customerDetails) {
		this.custList = customerDetails;
	}
	
	public String getCustNo() {
		return custNo;
	}
	
	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}
	
	public String getNameTitle() {
		return nameTitle;
	}
	
	public void setNameTitle(String nameTitle) {
		this.nameTitle = nameTitle;
	}
	
	public String getLongName() {
		return longName;
	}
	
	public void setLongName(String longName) {
		this.longName = longName;
	}
	
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
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getLbrCode() {
		return LbrCode;
	}
	public void setLbrCode(String lbrCode) {
		LbrCode = lbrCode;
	}
	public String getPanNo() {
		return panNo;
	}
	public void setPanNo(String panNo) {
		this.panNo = panNo;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getMainCustNo() {
		return mainCustNo;
	}
	public void setMainCustNo(String mainCustNo) {
		this.mainCustNo = mainCustNo;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	
	public String getAdharNo() {
		return adharNo;
	}
	public void setAdharNo(String adharNo) {
		this.adharNo = adharNo;
	}
	public String getKycStatus() {
		return kycStatus;
	}
	public void setKycStatus(String kycStatus) {
		this.kycStatus = kycStatus;
	}
	public String getKycDate() {
		return kycDate;
	}
	public void setKycDate(String kycDate) {
		this.kycDate = kycDate;
	}
	@Override
	public String toString() {
		return "CustomerDetails [custNo=" + custNo + ", nameTitle=" + nameTitle + ", longName=" + longName + ", add1="
				+ add1 + ", add2=" + add2 + ", add3=" + add3 + ", cityCode=" + cityCode + ", pinCode=" + pinCode
				+ ", mobileNo=" + mobileNo + ", LbrCode=" + LbrCode + ", panNo=" + panNo + ", countryCode="
				+ countryCode + ", mainCustNo=" + mainCustNo + ", emailId=" + emailId + ", output=" + output
				+ ", response=" + response + ", errorMsg=" + errorMsg + ", accNo=" + accNo + ", odLimit=" + odLimit
				+ ", odAdhocLimit=" + odAdhocLimit + ", custList=" + custList + "]";
	}

}
