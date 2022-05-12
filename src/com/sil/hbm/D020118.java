package com.sil.hbm;
// Generated Nov 18, 2016 3:06:08 PM by Hibernate Tools 4.3.1.Final

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * D020118 generated by hbm2java
 */
@Entity
@Table(name = "D020118")
@DynamicUpdate
@DynamicInsert
public class D020118 implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5945561999668360228L;
	private D020118Id id;
	private short days;
	private short months;
	private double intRate;
	private double penalIntRate;
	private short mlDbtrBijIdx;
	private short mlDbtrUaeIdx;

	
	private Date toMatDate;
	
	public D020118() {
	}

	public D020118(D020118Id id, short days, short months, double intRate, double penalIntRate, short mlDbtrBijIdx,
			short mlDbtrUaeIdx) {
		this.id = id;
		this.days = days;
		this.months = months;
		this.intRate = intRate;
		this.penalIntRate = penalIntRate;
		this.mlDbtrBijIdx = mlDbtrBijIdx;
		this.mlDbtrUaeIdx = mlDbtrUaeIdx;
	}

	@EmbeddedId

	@AttributeOverrides({
			@AttributeOverride(name = "prdCd", column = @Column(name = "PrdCd", nullable = false, length = 8)),
			@AttributeOverride(name = "curCd", column = @Column(name = "CurCd", nullable = false, length = 3)),
			@AttributeOverride(name = "intEffDt", column = @Column(name = "IntEffDt", nullable = false, length = 23)),
			@AttributeOverride(name = "srNo", column = @Column(name = "SrNo", nullable = false)) })
	public D020118Id getId() {
		return this.id;
	}

	public void setId(D020118Id id) {
		this.id = id;
	}

	@Column(name = "Days", nullable = false)
	public short getDays() {
		return this.days;
	}

	public void setDays(short days) {
		this.days = days;
	}

	@Column(name = "Months", nullable = false)
	public short getMonths() {
		return this.months;
	}

	public void setMonths(short months) {
		this.months = months;
	}

	@Column(name = "IntRate", nullable = false, precision = 53, scale = 0)
	public double getIntRate() {
		return this.intRate;
	}

	public void setIntRate(double intRate) {
		this.intRate = intRate;
	}

	@Column(name = "PenalIntRate", nullable = false, precision = 53, scale = 0)
	public double getPenalIntRate() {
		return this.penalIntRate;
	}

	public void setPenalIntRate(double penalIntRate) {
		this.penalIntRate = penalIntRate;
	}

	@Column(name = "MlDbtrBijIdx", nullable = false)
	public short getMlDbtrBijIdx() {
		return this.mlDbtrBijIdx;
	}

	public void setMlDbtrBijIdx(short mlDbtrBijIdx) {
		this.mlDbtrBijIdx = mlDbtrBijIdx;
	}

	@Column(name = "MlDbtrUaeIdx", nullable = false)
	public short getMlDbtrUaeIdx() {
		return this.mlDbtrUaeIdx;
	}

	public void setMlDbtrUaeIdx(short mlDbtrUaeIdx) {
		this.mlDbtrUaeIdx = mlDbtrUaeIdx;
	}

	@Transient
	public Date getToMatDate() {
		return toMatDate;
	}

	public void setToMatDate(Date toMatDate) {
		this.toMatDate = toMatDate;
	}

	@Override
	public String toString() {
		return "D020118 [id=" + id + ", days=" + days + ", months=" + months + ", intRate=" + intRate
				+ ", penalIntRate=" + penalIntRate + ", mlDbtrBijIdx=" + mlDbtrBijIdx + ", mlDbtrUaeIdx=" + mlDbtrUaeIdx
				+ "]";
	}
	
	

}
