package com.sil.hbm;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.sql.Timestamp;
import java.util.Date;


/**
 * The persistent class for the D350110 database table.
 * 
 */
@Entity
@Table(name="D350110")
@DynamicUpdate@DynamicInsert
public class UPITransaction implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private UPITransactionId id;

	@Column(name="CrAcctId")
	private String crAcctId;

	@Column(name="CrBrCode")
	private int crBrCode;

	@Column(name="DrAcctId")
	private String drAcctId;

	@Column(name="DrBrCode")
	private String drBrCode;

	@Column(name="DRCR")
	private String drcr;

	@Column(name="ResponseCd")
	private String responseCd;

	@Column(name="ResponseDesc")
	private String responseDesc;

	@Column(name="RrnNo")
	private String rrnNo;

	@Column(name="SOURCE")
	private String source;

	@Column(name="STATUS")
	private short status;

	@Column(name="TranAmt")
	private double tranAmt;

	@Column(name="TransactionDate")
	private Date transactionDate;

	@Column(name="TXNID")
	private String txnid;

	public UPITransaction() {
	}

	public UPITransactionId getId() {
		return this.id;
	}

	public void setId(UPITransactionId id) {
		this.id = id;
	}

	public String getCrAcctId() {
		return this.crAcctId;
	}

	public void setCrAcctId(String crAcctId) {
		this.crAcctId = crAcctId;
	}

	public int getCrBrCode() {
		return this.crBrCode;
	}

	public void setCrBrCode(int crBrCode) {
		this.crBrCode = crBrCode;
	}

	public String getDrAcctId() {
		return this.drAcctId;
	}

	public void setDrAcctId(String drAcctId) {
		this.drAcctId = drAcctId;
	}

	public String getDrBrCode() {
		return this.drBrCode;
	}

	public void setDrBrCode(String drBrCode) {
		this.drBrCode = drBrCode;
	}

	public String getDrcr() {
		return this.drcr;
	}

	public void setDrcr(String drcr) {
		this.drcr = drcr;
	}

	public String getResponseCd() {
		return this.responseCd;
	}

	public void setResponseCd(String responseCd) {
		this.responseCd = responseCd;
	}

	public String getResponseDesc() {
		return this.responseDesc;
	}

	public void setResponseDesc(String responseDesc) {
		this.responseDesc = responseDesc;
	}

	public String getRrnNo() {
		return this.rrnNo;
	}

	public void setRrnNo(String rrnNo) {
		this.rrnNo = rrnNo;
	}

	public String getSource() {
		return this.source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public short getStatus() {
		return this.status;
	}

	public void setStatus(short status) {
		this.status = status;
	}

	public double getTranAmt() {
		return this.tranAmt;
	}

	public void setTranAmt(double tranAmt) {
		this.tranAmt = tranAmt;
	}

	public Date getTransactionDate() {
		return this.transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getTxnid() {
		return this.txnid;
	}

	public void setTxnid(String txnid) {
		this.txnid = txnid;
	}

}