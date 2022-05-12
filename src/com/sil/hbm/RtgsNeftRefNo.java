package com.sil.hbm;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;


/**
 * The persistent class for the D946005 database table.
 * 
 */
@Entity
@Table(name = "D946005")
@DynamicUpdate @DynamicInsert
public class RtgsNeftRefNo implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RtgsNeftRefNoId id;

	@Column(name="Descr")
	private String descr;

	@Column(name="LastNo")
	private int lastNo;

	public RtgsNeftRefNo() {
	}

	public RtgsNeftRefNoId getId() {
		return this.id;
	}

	public void setId(RtgsNeftRefNoId id) {
		this.id = id;
	}

	public String getDescr() {
		return this.descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public int getLastNo() {
		return this.lastNo;
	}

	public void setLastNo(int lastNo) {
		this.lastNo = lastNo;
	}

}