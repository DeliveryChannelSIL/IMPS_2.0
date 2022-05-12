package com.sil.hbm;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;


/**
 * The persistent class for the D009127 database table.
 * 
 */
@Entity
@Table(name = "D009127")
//@NamedQuery(name="D009127.findAll", query="SELECT d FROM D009127 d")
public class PeriodWiseOffset implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private PeriodWiseOffsetId id;

	@Column(name="Days")
	private short days;

	@Column(name="MlDbtrBijIdx")
	private short mlDbtrBijIdx;

	@Column(name="MlDbtrUaeIdx")
	private short mlDbtrUaeIdx;

	@Column(name="Months")
	private short months;

	@Column(name="OffSetIntRate")
	private double offSetIntRate;

	@Column(name="UptoAmt")
	private double uptoAmt;
	
	@Transient
	private Date toMatDate;

	public PeriodWiseOffset() {
	}

	public PeriodWiseOffsetId getId() {
		return this.id;
	}

	public void setId(PeriodWiseOffsetId id) {
		this.id = id;
	}

	public short getDays() {
		return this.days;
	}

	public void setDays(short days) {
		this.days = days;
	}

	public short getMlDbtrBijIdx() {
		return this.mlDbtrBijIdx;
	}

	public void setMlDbtrBijIdx(short mlDbtrBijIdx) {
		this.mlDbtrBijIdx = mlDbtrBijIdx;
	}

	public short getMlDbtrUaeIdx() {
		return this.mlDbtrUaeIdx;
	}

	public void setMlDbtrUaeIdx(short mlDbtrUaeIdx) {
		this.mlDbtrUaeIdx = mlDbtrUaeIdx;
	}

	public short getMonths() {
		return this.months;
	}

	public void setMonths(short months) {
		this.months = months;
	}

	public double getOffSetIntRate() {
		return this.offSetIntRate;
	}

	public void setOffSetIntRate(double offSetIntRate) {
		this.offSetIntRate = offSetIntRate;
	}

	public double getUptoAmt() {
		return this.uptoAmt;
	}

	public void setUptoAmt(double uptoAmt) {
		this.uptoAmt = uptoAmt;
	}

	public Date getToMatDate() {
		return toMatDate;
	}

	public void setToMatDate(Date toMatDate) {
		this.toMatDate = toMatDate;
	}
	
	

}