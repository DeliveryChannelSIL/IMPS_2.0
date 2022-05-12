package com.sil.hbm;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

/**
 * D001116 entity.
 * 
 * @author MyEclipse Persistence Tools
 */
@SuppressWarnings("all")
@Entity
@Table(name = "D001116")
@Immutable
public class IntrestRate implements java.io.Serializable {

	// Fields

	private IntrestRateId id;
	private Double toamt;
	private Double intrate;
	private Long mldbtrbijidx;
	private Long mldbtruaeidx;

	// Constructors

	/** default constructor */
	public IntrestRate() {
	}

	/** full constructor */
	public IntrestRate(IntrestRateId id, Double toamt, Double intrate,
			Long mldbtrbijidx, Long mldbtruaeidx) {
		this.id = id;
		this.toamt = toamt;
		this.intrate = intrate;
		this.mldbtrbijidx = mldbtrbijidx;
		this.mldbtruaeidx = mldbtruaeidx;
	}
	
	public IntrestRate(Double toamt, Double intrate) {
		super();
		this.toamt = toamt;
		this.intrate = intrate;
	}

	// Property accessors
	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "prdcd", column = @Column(name = "PrdCd", nullable = false, length = 8)),
			@AttributeOverride(name = "curcd", column = @Column(name = "CurCd", nullable = false, length = 3)),
			@AttributeOverride(name = "inteffdt", column = @Column(name = "IntEffDt", nullable = false, length = 7)),
			@AttributeOverride(name = "srno", column = @Column(name = "SrNo", nullable = false, precision = 2, scale = 0)) })
	public IntrestRateId getId() {
		return this.id;
	}

	public void setId(IntrestRateId id) {
		this.id = id;
	}

	@Column(name = "ToAmt", nullable = false, precision = 13)
	public Double getToamt() {
		return this.toamt;
	}

	public void setToamt(Double toamt) {
		this.toamt = toamt;
	}

	@Column(name = "IntRate", nullable = false, precision = 8, scale = 6)
	public Double getIntrate() {
		return this.intrate;
	}

	public void setIntrate(Double intrate) {
		this.intrate = intrate;
	}

	@Column(name = "MlDbtrBijIdx", nullable = false, precision = 4, scale = 0)
	public Long getMldbtrbijidx() {
		return this.mldbtrbijidx;
	}

	public void setMldbtrbijidx(Long mldbtrbijidx) {
		this.mldbtrbijidx = mldbtrbijidx;
	}

	@Column(name = "MlDbtrUaeIdx", nullable = false, precision = 4, scale = 0)
	public Long getMldbtruaeidx() {
		return this.mldbtruaeidx;
	}

	public void setMldbtruaeidx(Long mldbtruaeidx) {
		this.mldbtruaeidx = mldbtruaeidx;
	}

}