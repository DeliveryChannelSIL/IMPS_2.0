package com.sil.domain;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LookUpResponse implements Serializable{

	private static final long serialVersionUID = 9014470302255931850L;
	private String code;
	private String value;
	
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
	@Override
	public String toString() {
		return "LookUpResponse [code=" + code + ", value=" + value + "]";
	}
}
