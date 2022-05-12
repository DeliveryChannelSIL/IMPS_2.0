package com.sil.hbm;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the D020018 database table.
 * 
 */
@Embeddable
public class TDProdWiseInterestId implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="PrdCd")
	private String prdCd;

	@Column(name="CurCd")
	private String curCd;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="IntEffDt")
	private java.util.Date intEffDt;

	public TDProdWiseInterestId() {
	}
	public String getPrdCd() {
		return this.prdCd;
	}
	public void setPrdCd(String prdCd) {
		this.prdCd = prdCd;
	}
	public String getCurCd() {
		return this.curCd;
	}
	public void setCurCd(String curCd) {
		this.curCd = curCd;
	}
	public java.util.Date getIntEffDt() {
		return this.intEffDt;
	}
	public void setIntEffDt(java.util.Date intEffDt) {
		this.intEffDt = intEffDt;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof TDProdWiseInterestId)) {
			return false;
		}
		TDProdWiseInterestId castOther = (TDProdWiseInterestId)other;
		return 
			this.prdCd.equals(castOther.prdCd)
			&& this.curCd.equals(castOther.curCd)
			&& this.intEffDt.equals(castOther.intEffDt);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.prdCd.hashCode();
		hash = hash * prime + this.curCd.hashCode();
		hash = hash * prime + this.intEffDt.hashCode();
		
		return hash;
	}
}