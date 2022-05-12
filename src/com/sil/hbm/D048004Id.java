package com.sil.hbm;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the D048004 database table.
 * 
 */
@Embeddable
public class D048004Id implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="LbrCode")
	private int lbrCode;

	@Column(name="PrdAcctId")
	private String prdAcctId;

	public D048004Id() {
	}
	public int getLbrCode() {
		return this.lbrCode;
	}
	public void setLbrCode(int lbrCode) {
		this.lbrCode = lbrCode;
	}
	public String getPrdAcctId() {
		return this.prdAcctId;
	}
	public void setPrdAcctId(String prdAcctId) {
		this.prdAcctId = prdAcctId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof D048004Id)) {
			return false;
		}
		D048004Id castOther = (D048004Id)other;
		return 
			(this.lbrCode == castOther.lbrCode)
			&& this.prdAcctId.equals(castOther.prdAcctId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.lbrCode;
		hash = hash * prime + this.prdAcctId.hashCode();
		
		return hash;
	}
}