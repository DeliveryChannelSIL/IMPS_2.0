package com.sil.hbm;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the D946005 database table.
 * 
 */
@Embeddable
public class RtgsNeftRefNoId implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private int LBrCode;

	@Column(name="Cat")
	private String cat;

	@Column(name="CatType")
	private String catType;

	@Column(name="Code1")
	private String code1;

	@Column(name="Code2")
	private String code2;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="Lnodate")
	private java.util.Date lnodate;

	public RtgsNeftRefNoId() {
	}
	public int getLBrCode() {
		return this.LBrCode;
	}
	public void setLBrCode(int LBrCode) {
		this.LBrCode = LBrCode;
	}
	public String getCat() {
		return this.cat;
	}
	public void setCat(String cat) {
		this.cat = cat;
	}
	public String getCatType() {
		return this.catType;
	}
	public void setCatType(String catType) {
		this.catType = catType;
	}
	public String getCode1() {
		return this.code1;
	}
	public void setCode1(String code1) {
		this.code1 = code1;
	}
	public String getCode2() {
		return this.code2;
	}
	public void setCode2(String code2) {
		this.code2 = code2;
	}
	public java.util.Date getLnodate() {
		return this.lnodate;
	}
	public void setLnodate(java.util.Date lnodate) {
		this.lnodate = lnodate;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RtgsNeftRefNoId)) {
			return false;
		}
		RtgsNeftRefNoId castOther = (RtgsNeftRefNoId)other;
		return 
			(this.LBrCode == castOther.LBrCode)
			&& this.cat.equals(castOther.cat)
			&& this.catType.equals(castOther.catType)
			&& this.code1.equals(castOther.code1)
			&& this.code2.equals(castOther.code2)
			&& this.lnodate.equals(castOther.lnodate);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.LBrCode;
		hash = hash * prime + this.cat.hashCode();
		hash = hash * prime + this.catType.hashCode();
		hash = hash * prime + this.code1.hashCode();
		hash = hash * prime + this.code2.hashCode();
		hash = hash * prime + this.lnodate.hashCode();
		
		return hash;
	}
}