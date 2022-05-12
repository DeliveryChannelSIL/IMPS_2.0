package com.sil.hbm;

import javax.xml.bind.annotation.XmlRootElement;
import com.sil.domain.Header;

@XmlRootElement
public class GetCustDetailsreq {
	private String mobilenumber;
	private Header header;

	public String getMobilenumber() {
		return mobilenumber;
	}
	
	public void setMobilenumber(String mobilenumber) {
		this.mobilenumber = mobilenumber;
	}
	public Header getHeader() {
		return header;
	}
	public void setHeader(Header header) {
		this.header = header;
	}
	@Override
	public String toString() {
		return "ClassPojo [mobilenumber = " + mobilenumber + ", header = " + header + "]";
	}
}
