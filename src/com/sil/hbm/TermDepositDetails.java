package com.sil.hbm;

import java.math.BigDecimal;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "TERM_DEPOSIT_DETAILS")
public class TermDepositDetails implements java.io.Serializable {

	private static final long serialVersionUID = -7814566176364387808L;
	private TermDepositDetailsId id;
	private int custno;
	private int noofmonths;
	private int noofdays;
	private BigDecimal amount;
	private int crDrLbrcode;
	private String crDrPrdacctid;
	private String activity;
	private int status;
	private String statusMsg;
	private String remarks;

	public TermDepositDetails() {
	}

	public TermDepositDetails(TermDepositDetailsId id, int custno, int noofmonths, int noofdays, BigDecimal amount,
			int crDrLbrcode, String crDrPrdacctid, String activity, int status, String statusMsg, String remarks) {
		this.id = id;
		this.custno = custno;
		this.noofmonths = noofmonths;
		this.noofdays = noofdays;
		this.amount = amount;
		this.crDrLbrcode = crDrLbrcode;
		this.crDrPrdacctid = crDrPrdacctid;
		this.activity = activity;
		this.status = status;
		this.statusMsg = statusMsg;
		this.remarks = remarks;
	}

	@EmbeddedId

	@AttributeOverrides({ @AttributeOverride(name = "srNo", column = @Column(name = "SR_NO", nullable = false)),
			@AttributeOverride(name = "lbrcode", column = @Column(name = "LBRCODE", nullable = false)),
			@AttributeOverride(name = "prdacctid", column = @Column(name = "PRDACCTID", nullable = false, length = 1)),
			@AttributeOverride(name = "certDate", column = @Column(name = "CERT_DATE", nullable = false, length = 10)) })
	public TermDepositDetailsId getId() {
		return this.id;
	}

	public void setId(TermDepositDetailsId id) {
		this.id = id;
	}

	@Column(name = "CUSTNO", nullable = false)
	public int getCustno() {
		return this.custno;
	}

	public void setCustno(int custno) {
		this.custno = custno;
	}

	@Column(name = "NOOFMONTHS", nullable = false)
	public int getNoofmonths() {
		return this.noofmonths;
	}

	public void setNoofmonths(int noofmonths) {
		this.noofmonths = noofmonths;
	}

	@Column(name = "NOOFDAYS", nullable = false)
	public int getNoofdays() {
		return this.noofdays;
	}

	public void setNoofdays(int noofdays) {
		this.noofdays = noofdays;
	}

	@Column(name = "AMOUNT", nullable = false, precision = 13)
	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Column(name = "CR_DR_LBRCODE", nullable = false)
	public int getCrDrLbrcode() {
		return this.crDrLbrcode;
	}

	public void setCrDrLbrcode(int crDrLbrcode) {
		this.crDrLbrcode = crDrLbrcode;
	}

	@Column(name = "CR_DR_PRDACCTID", nullable = false, length = 1)
	public String getCrDrPrdacctid() {
		return this.crDrPrdacctid;
	}

	public void setCrDrPrdacctid(String crDrPrdacctid) {
		this.crDrPrdacctid = crDrPrdacctid;
	}

	@Column(name = "ACTIVITY", nullable = false, length = 1)
	public String getActivity() {
		return this.activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	@Column(name = "STATUS", nullable = false)
	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Column(name = "STATUS_MSG", nullable = false, length = 1)
	public String getStatusMsg() {
		return this.statusMsg;
	}

	public void setStatusMsg(String statusMsg) {
		this.statusMsg = statusMsg;
	}

	@Column(name = "REMARKS", nullable = false, length = 1)
	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
