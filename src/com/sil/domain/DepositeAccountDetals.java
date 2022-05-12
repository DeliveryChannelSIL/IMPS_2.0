package com.sil.domain;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;

@javax.xml.bind.annotation.XmlRootElement
public class DepositeAccountDetals implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long lbrCode = 0l;
	private String accountNo = "";

	public DepositeAccountDetals() {

	}

	@XmlElement()
	public Long getLbrCode() {
		return lbrCode;
	}

	public void setLbrCode(Long lbrCode) {
		this.lbrCode = lbrCode;
	}

	@XmlElement()
	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

}
