package com.sil.hbm;
// Generated Feb 7, 2018 12:57:33 PM by Hibernate Tools 5.2.0.Beta1

import java.util.Date;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Atmcharges generated by hbm2java
 */
@Entity
@Table(name = "ATMCHARGES")
public class Atmcharges implements java.io.Serializable {

	private static final long serialVersionUID = 7517097817647765573L;
	private AtmchargesId id;
	private String cargetype;
	private Date entrytime;
	private double amount;
	private String accno;
	private String placcno;
	private int branchcode;
	private String status;
	private String errormsg;

	public Atmcharges() {
	}

	public Atmcharges(AtmchargesId id, String cargetype, Date entrytime, double amount, String accno, String placcno,
			int branchcode, String status) {
		this.id = id;
		this.cargetype = cargetype;
		this.entrytime = entrytime;
		this.amount = amount;
		this.accno = accno;
		this.placcno = placcno;
		this.branchcode = branchcode;
		this.status = status;
	}

	@EmbeddedId

	@AttributeOverrides({
			@AttributeOverride(name = "rrn", column = @Column(name = "RRN", nullable = false, length = 12)),
			@AttributeOverride(name = "cardno", column = @Column(name = "CARDNO", nullable = false, length = 19)),
			@AttributeOverride(name = "entrydate", column = @Column(name = "ENTRYDATE", nullable = false, length = 10)) })
	public AtmchargesId getId() {
		return this.id;
	}

	public void setId(AtmchargesId id) {
		this.id = id;
	}

	@Column(name = "CARGETYPE", nullable = false, length = 50)
	public String getCargetype() {
		return this.cargetype;
	}

	public void setCargetype(String cargetype) {
		this.cargetype = cargetype;
	}

	@Temporal(TemporalType.TIME)
	@Column(name = "ENTRYTIME", nullable = false, length = 16)
	public Date getEntrytime() {
		return this.entrytime;
	}

	public void setEntrytime(Date entrytime) {
		this.entrytime = entrytime;
	}

	@Column(name = "AMOUNT", nullable = false, precision = 53, scale = 0)
	public double getAmount() {
		return this.amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	@Column(name = "ACCNO", nullable = false, length = 32)
	public String getAccno() {
		return this.accno;
	}

	public void setAccno(String accno) {
		this.accno = accno;
	}

	@Column(name = "PLACCNO", nullable = false, length = 32)
	public String getPlaccno() {
		return this.placcno;
	}

	public void setPlaccno(String placcno) {
		this.placcno = placcno;
	}

	@Column(name = "BRANCHCODE", nullable = false)
	public int getBranchcode() {
		return this.branchcode;
	}

	public void setBranchcode(int branchcode) {
		this.branchcode = branchcode;
	}

	@Column(name = "STATUS", nullable = false, length = 2)
	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	@Column(name = "ERRORMSG", nullable = false, length = 200)
	public String getErrormsg() {
		return errormsg;
	}

	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}

}
