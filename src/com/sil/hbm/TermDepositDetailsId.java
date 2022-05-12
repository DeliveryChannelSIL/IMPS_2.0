package com.sil.hbm;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class TermDepositDetailsId implements java.io.Serializable {

	private static final long serialVersionUID = 7611864251771155938L;
	private int srNo;
	private int lbrcode;
	private String prdacctid;
	private Date certDate;

	public TermDepositDetailsId() {
	}

	public TermDepositDetailsId(int srNo, int lbrcode, String prdacctid, Date certDate) {
		this.srNo = srNo;
		this.lbrcode = lbrcode;
		this.prdacctid = prdacctid;
		this.certDate = certDate;
	}

	@Column(name = "SR_NO", nullable = false)
	public int getSrNo() {
		return this.srNo;
	}

	public void setSrNo(int srNo) {
		this.srNo = srNo;
	}

	@Column(name = "LBRCODE", nullable = false)
	public int getLbrcode() {
		return this.lbrcode;
	}

	public void setLbrcode(int lbrcode) {
		this.lbrcode = lbrcode;
	}

	@Column(name = "PRDACCTID", nullable = false, length = 1)
	public String getPrdacctid() {
		return this.prdacctid;
	}

	public void setPrdacctid(String prdacctid) {
		this.prdacctid = prdacctid;
	}

	@Column(name = "CERT_DATE", nullable = false, length = 10)
	public Date getCertDate() {
		return this.certDate;
	}

	public void setCertDate(Date certDate) {
		this.certDate = certDate;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof TermDepositDetailsId))
			return false;
		TermDepositDetailsId castOther = (TermDepositDetailsId) other;

		return (this.getSrNo() == castOther.getSrNo()) && (this.getLbrcode() == castOther.getLbrcode())
				&& ((this.getPrdacctid() == castOther.getPrdacctid()) || (this.getPrdacctid() != null
						&& castOther.getPrdacctid() != null && this.getPrdacctid().equals(castOther.getPrdacctid())))
				&& ((this.getCertDate() == castOther.getCertDate()) || (this.getCertDate() != null
						&& castOther.getCertDate() != null && this.getCertDate().equals(castOther.getCertDate())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getSrNo();
		result = 37 * result + this.getLbrcode();
		result = 37 * result + (getPrdacctid() == null ? 0 : this.getPrdacctid().hashCode());
		result = 37 * result + (getCertDate() == null ? 0 : this.getCertDate().hashCode());
		return result;
	}

}
