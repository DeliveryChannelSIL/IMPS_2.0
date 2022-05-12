package com.sil.hbm;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;


/**
 * The persistent class for the D946320 database table.
 * 
 */
@Entity
@Table(name = "D946320")
@DynamicUpdate
@DynamicInsert
public class D946320 implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private D946320Id id;

	private String NUTRNo;

	private String RBIUTRNo;

	@Column(name="TransId")
	private String transId;

	private String UTRNo;

	private int UTRSeqNo;

	public D946320() {
	}

	public D946320Id getId() {
		return this.id;
	}

	public void setId(D946320Id id) {
		this.id = id;
	}

	public String getNUTRNo() {
		return this.NUTRNo;
	}

	public void setNUTRNo(String NUTRNo) {
		this.NUTRNo = NUTRNo;
	}

	public String getRBIUTRNo() {
		return this.RBIUTRNo;
	}

	public void setRBIUTRNo(String RBIUTRNo) {
		this.RBIUTRNo = RBIUTRNo;
	}

	public String getTransId() {
		return this.transId;
	}

	public void setTransId(String transId) {
		this.transId = transId;
	}

	public String getUTRNo() {
		return this.UTRNo;
	}

	public void setUTRNo(String UTRNo) {
		this.UTRNo = UTRNo;
	}

	public int getUTRSeqNo() {
		return this.UTRSeqNo;
	}

	public void setUTRSeqNo(int UTRSeqNo) {
		this.UTRSeqNo = UTRSeqNo;
	}

	public D946320(D946320Id id, String nUTRNo, String rBIUTRNo, String transId, String uTRNo, int uTRSeqNo) {
		super();
		this.id = id;
		NUTRNo = nUTRNo;
		RBIUTRNo = rBIUTRNo;
		this.transId = transId;
		UTRNo = uTRNo;
		UTRSeqNo = uTRSeqNo;
	}

	@Override
	public String toString() {
		return "D946320 [id=" + id.toString() + ", NUTRNo=" + NUTRNo + ", RBIUTRNo=" + RBIUTRNo + ", transId=" + transId
				+ ", UTRNo=" + UTRNo + ", UTRSeqNo=" + UTRSeqNo + "]";
	}

	
}