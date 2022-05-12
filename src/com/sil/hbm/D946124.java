package com.sil.hbm;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the D946124 database table.
 * 
 */
@Entity
@NamedQuery(name="D946124.findAll", query="SELECT d FROM D946124 d")
public class D946124 implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private D946124Id id;

	@Column(name="FlatPerValue")
	private String flatPerValue;

	@Column(name="FlatRateAmt")
	private double flatRateAmt;

	@Column(name="MaxChgAmt")
	private double maxChgAmt;

	@Column(name="MinChgAmt")
	private double minChgAmt;

	@Column(name="MlDbtrBijIdx")
	private short mlDbtrBijIdx;

	@Column(name="MlDbtrUaeIdx")
	private short mlDbtrUaeIdx;

	@Column(name="PerValueAmt")
	private double perValueAmt;

	@Column(name="ReqAuthNos")
	private short reqAuthNos;

	@Column(name="UptoAmt")
	private double uptoAmt;

	public D946124() {
	}

	public D946124Id getId() {
		return this.id;
	}

	public void setId(D946124Id id) {
		this.id = id;
	}

	public String getFlatPerValue() {
		return this.flatPerValue;
	}

	public void setFlatPerValue(String flatPerValue) {
		this.flatPerValue = flatPerValue;
	}

	public double getFlatRateAmt() {
		return this.flatRateAmt;
	}

	public void setFlatRateAmt(double flatRateAmt) {
		this.flatRateAmt = flatRateAmt;
	}

	public double getMaxChgAmt() {
		return this.maxChgAmt;
	}

	public void setMaxChgAmt(double maxChgAmt) {
		this.maxChgAmt = maxChgAmt;
	}

	public double getMinChgAmt() {
		return this.minChgAmt;
	}

	public void setMinChgAmt(double minChgAmt) {
		this.minChgAmt = minChgAmt;
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

	public double getPerValueAmt() {
		return this.perValueAmt;
	}

	public void setPerValueAmt(double perValueAmt) {
		this.perValueAmt = perValueAmt;
	}

	public short getReqAuthNos() {
		return this.reqAuthNos;
	}

	public void setReqAuthNos(short reqAuthNos) {
		this.reqAuthNos = reqAuthNos;
	}

	public double getUptoAmt() {
		return this.uptoAmt;
	}

	public void setUptoAmt(double uptoAmt) {
		this.uptoAmt = uptoAmt;
	}

}