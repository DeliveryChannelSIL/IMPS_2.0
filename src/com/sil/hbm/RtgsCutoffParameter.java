package com.sil.hbm;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the RTGS_CUTOFF_PARAMETER database table.
 * 
 */
@Entity
@Table(name="RTGS_CUTOFF_PARAMETER")
@NamedQuery(name="RtgsCutoffParameter.findAll", query="SELECT r FROM RtgsCutoffParameter r")
public class RtgsCutoffParameter implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RtgsCutoffParameterId id;

	@Column(name="CUTOFFTIMEBEGIN")
	private double cutofftimebegin;

	@Column(name="ORDIFSCCODE")
	private String ordifsccode;

	@Column(name="PRDCD")
	private String prdcd;

	@Column(name="TRSRYBRCODE")
	private int trsrybrcode;

	public RtgsCutoffParameter() {
	}

	public RtgsCutoffParameterId getId() {
		return this.id;
	}

	public void setId(RtgsCutoffParameterId id) {
		this.id = id;
	}

	public double getCutofftimebegin() {
		return this.cutofftimebegin;
	}

	public void setCutofftimebegin(double cutofftimebegin) {
		this.cutofftimebegin = cutofftimebegin;
	}

	public String getOrdifsccode() {
		return this.ordifsccode;
	}

	public void setOrdifsccode(String ordifsccode) {
		this.ordifsccode = ordifsccode;
	}

	public String getPrdcd() {
		return this.prdcd;
	}

	public void setPrdcd(String prdcd) {
		this.prdcd = prdcd;
	}

	public int getTrsrybrcode() {
		return this.trsrybrcode;
	}

	public void setTrsrybrcode(int trsrybrcode) {
		this.trsrybrcode = trsrybrcode;
	}

}