package com.sil.domain;


import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;

@javax.xml.bind.annotation.XmlRootElement
public class DepositeInterestRateDetals implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long month = 0L;
	private Long days = 0L;
	private Double interestRate = 0.0;
	private Date interestDate = null;
	private String desc = "";
	private String depositeType = "";

	public DepositeInterestRateDetals() {

	}

	@XmlElement()
	public Long getMonth() {
		return month;
	}

	public void setMonth(Long month) {
		this.month = month;
	}

	@XmlElement()
	public Long getDays() {
		return days;
	}

	public void setDays(Long days) {
		this.days = days;
	}

	@XmlElement()
	public Double getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(Double interestRate) {
		this.interestRate = interestRate;
	}

	@XmlElement()
	public Date getInterestDate() {
		return interestDate;
	}

	public void setInterestDate(Date interestDate) {
		this.interestDate = interestDate;
	}

	@XmlElement()
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@XmlElement()
	public String getDepositeType() {
		return depositeType;
	}

	public void setDepositeType(String depositeType) {
		this.depositeType = depositeType;
	}

}
