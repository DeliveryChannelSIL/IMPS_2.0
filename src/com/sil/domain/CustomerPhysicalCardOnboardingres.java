package com.sil.domain;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CustomerPhysicalCardOnboardingres {
	private String CardAlias;

	private String customerid;

	private String status;

	private String bcagent;

	private String CardDescription;

	private String CrdProduct;

	public String getCardAlias() {
		return CardAlias;
	}

	public void setCardAlias(String CardAlias) {
		this.CardAlias = CardAlias;
	}

	public String getCustomerid() {
		return customerid;
	}

	public void setCustomerid(String customerid) {
		this.customerid = customerid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBcagent() {
		return bcagent;
	}

	public void setBcagent(String bcagent) {
		this.bcagent = bcagent;
	}

	public String getCardDescription() {
		return CardDescription;
	}

	public void setCardDescription(String CardDescription) {
		this.CardDescription = CardDescription;
	}

	public String getCrdProduct() {
		return CrdProduct;
	}

	public void setCrdProduct(String CrdProduct) {
		this.CrdProduct = CrdProduct;
	}

	@Override
	public String toString() {
		return "ClassPojo [CardAlias = " + CardAlias + ", customerid = " + customerid + ", status = " + status
				+ ", bcagent = " + bcagent + ", CardDescription = " + CardDescription + ", CrdProduct = " + CrdProduct
				+ "]";
	}
}