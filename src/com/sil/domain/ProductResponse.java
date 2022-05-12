package com.sil.domain;

import javax.xml.bind.annotation.XmlElement;

public class ProductResponse {

	
	private String code;
	
	private String name;
	
	@XmlElement(name="Code")
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@XmlElement(name="Name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "ProductResponse [code=" + code + ", name=" + name + "]";
	}
	
	
	

}
