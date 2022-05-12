package com.sil.domain;

public class DepositeParameters {

	private String code;
	private String value;
	private int maxTenure;
	private int minTenure;
	private int maxTenureType=0;
	private int minTenureType=0;
	private Long maxAmount;
	private Double minAmount;
	private String productType;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getMaxTenure() {
		return maxTenure;
	}
	public void setMaxTenure(int maxTenure) {
		this.maxTenure = maxTenure;
	}
	public int getMinTenure() {
		return minTenure;
	}
	public void setMinTenure(int minTenure) {
		this.minTenure = minTenure;
	}
	public int getMaxTenureType() {
		return maxTenureType;
	}
	public void setMaxTenureType(int maxTenureType) {
		this.maxTenureType = maxTenureType;
	}
	public int getMinTenureType() {
		return minTenureType;
	}
	public void setMinTenureType(int minTenureType) {
		this.minTenureType = minTenureType;
	}
	public Long getMaxAmount() {
		return maxAmount;
	}
	public void setMaxAmount(Long maxAmount) {
		this.maxAmount = maxAmount;
	}
	public Double getMinAmount() {
		return minAmount;
	}
	public void setMinAmount(Double minAmount) {
		this.minAmount = minAmount;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	@Override
	public String toString() {
		return "DepositeParameters [code=" + code + ", value=" + value + ", maxTenure=" + maxTenure + ", minTenure="
				+ minTenure + ", maxTenureType=" + maxTenureType + ", minTenureType=" + minTenureType + ", maxAmount="
				+ maxAmount + ", minAmount=" + minAmount + ", productType=" + productType + "]";
	}
	
	
}
