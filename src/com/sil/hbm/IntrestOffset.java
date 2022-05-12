package com.sil.hbm;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

/**
 * D001118 entity.
 * 
 * @author MyEclipse Persistence Tools
 */
@SuppressWarnings("all")
@Entity
@Table(name = "D001118")
@Immutable
public class IntrestOffset implements java.io.Serializable {

	// Fields

	private IntrestOffsetId id;
	private Double slabamt;
	private Double offset;
	private Long mldbtrbijidx;
	private Long mldbtruaeidx;

	// Constructors

	/** default constructor */
	public IntrestOffset() {
	}

	/** minimal constructor */
	public IntrestOffset(IntrestOffsetId id, Double slabamt, Double offset,
			Long mldbtrbijidx, Long mldbtruaeidx) {
		this.id = id;
		this.slabamt = slabamt;
		this.offset = offset;
		this.mldbtrbijidx = mldbtrbijidx;
		this.mldbtruaeidx = mldbtruaeidx;
	}
	
	public IntrestOffset(Double offset) {
		super();
		this.offset = offset;
	}

	// Property accessors
	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "lbrcode", column = @Column(name = "LBrCode", nullable = false, precision = 6, scale = 0)),
			@AttributeOverride(name = "prdacctid", column = @Column(name = "PrdAcctId", nullable = false, length = 32)),
			@AttributeOverride(name = "efffromdate", column = @Column(name = "EffFromDate", nullable = false, length = 7)),
			@AttributeOverride(name = "srno", column = @Column(name = "SrNo", nullable = false, precision = 4, scale = 0)) })
	public IntrestOffsetId getId() {
		return this.id;
	}

	public void setId(IntrestOffsetId id) {
		this.id = id;
	}

	@Column(name = "SlabAmt", nullable = false, precision = 13)
	public Double getSlabamt() {
		return this.slabamt;
	}

	public void setSlabamt(Double slabamt) {
		this.slabamt = slabamt;
	}

	@Column(name = "OffSet", nullable = false, precision = 5)
	public Double getOffset() {
		return this.offset;
	}

	public void setOffset(Double offset) {
		this.offset = offset;
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