package com.sil.hbm;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "SIM_SE_PAY_TRANSACTION")
public class SimSePayTrancation implements java.io.Serializable {
	private static final long serialVersionUID = -3238508623638996532L;
	private String rrn;
	private int brcode;
	private String accountno;
	private Date entrydate;
	private Date entrytime;
	private Double amount;
	private String respcode;
	private String drcr;
	private String errorMessage1;
	private String errorMessage2;
	private String errorMessage3;
	private String reconno;
	private String bankcode;

	public SimSePayTrancation() {
	}

	public SimSePayTrancation(String rrn, Date entrydate, Date entrytime) {
		this.rrn = rrn;
		this.entrydate = entrydate;
		this.entrytime = entrytime;
	}

	public SimSePayTrancation(String rrn, String accountno, Date entrydate, Date entrytime, Double amount,
			String cardno, String cardinsno, String respcode, String errorMessage1, String errorMessage2,
			String errorMessage3) {
		this.rrn = rrn;
		this.accountno = accountno;
		this.entrydate = entrydate;
		this.entrytime = entrytime;
		this.amount = amount;
		this.respcode = respcode;
		this.errorMessage1 = errorMessage1;
		this.errorMessage2 = errorMessage2;
		this.errorMessage3 = errorMessage3;
	}

	@Id

	@Column(name = "RRN", unique = true, nullable = false, length = 12)
	public String getRrn() {
		return this.rrn;
	}

	public void setRrn(String rrn) {
		this.rrn = rrn;
	}

	@Column(name = "ACCOUNTNO", length = 32)
	public String getAccountno() {
		return this.accountno;
	}

	public void setAccountno(String accountno) {
		this.accountno = accountno;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ENTRYDATE", nullable = false, length = 23)
	public Date getEntrydate() {
		return this.entrydate;
	}

	public void setEntrydate(Date entrydate) {
		this.entrydate = entrydate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ENTRYTIME", nullable = false, length = 23)
	public Date getEntrytime() {
		return this.entrytime;
	}

	public void setEntrytime(Date entrytime) {
		this.entrytime = entrytime;
	}

	@Column(name = "AMOUNT", precision = 53, scale = 0)
	public Double getAmount() {
		return this.amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Column(name = "RESPCODE", length = 2)
	public String getRespcode() {
		return this.respcode;
	}

	public void setRespcode(String respcode) {
		this.respcode = respcode;
	}

	@Column(name = "ERROR_MESSAGE1", length = 250)
	public String getErrorMessage1() {
		return this.errorMessage1;
	}

	public void setErrorMessage1(String errorMessage1) {
		this.errorMessage1 = errorMessage1;
	}

	@Column(name = "ERROR_MESSAGE2", length = 250)
	public String getErrorMessage2() {
		return this.errorMessage2;
	}

	public void setErrorMessage2(String errorMessage2) {
		this.errorMessage2 = errorMessage2;
	}

	@Column(name = "ERROR_MESSAGE3", length = 250)
	public String getErrorMessage3() {
		return this.errorMessage3;
	}

	public void setErrorMessage3(String errorMessage3) {
		this.errorMessage3 = errorMessage3;
	}

	@Column(name = "BRCODE")
	public int getBrcode() {
		return brcode;
	}

	public void setBrcode(int brcode) {
		this.brcode = brcode;
	}

	@Column(name = "DRCR")
	public String getDrcr() {
		return drcr;
	}

	public void setDrcr(String drcr) {
		this.drcr = drcr;
	}

	@Column(name = "RECONNO")
	public String getReconno() {
		return reconno;
	}

	public void setReconno(String reconno) {
		this.reconno = reconno;
	}

	@Column(name = "BANKCODE")
	public String getBankcode() {
		return bankcode;
	}

	public void setBankcode(String bankcode) {
		this.bankcode = bankcode;
	}

}
