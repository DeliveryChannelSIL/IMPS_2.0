package com.sil.hbm;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class D010004Id implements java.io.Serializable {

	private static final long serialVersionUID = 5645482883235255246L;
	private int lbrCode;
	private Date entryDate;
	private String batchCd;

	public D010004Id() {
	}

	public D010004Id(int lbrCode, Date entryDate, String batchCd) {
		this.lbrCode = lbrCode;
		this.entryDate = entryDate;
		this.batchCd = batchCd;
	}

	@Column(name = "LBrCode", nullable = false)
	public int getLbrCode() {
		return this.lbrCode;
	}

	public void setLbrCode(int lbrCode) {
		this.lbrCode = lbrCode;
	}

	@Column(name = "EntryDate", nullable = false, length = 23)
	public Date getEntryDate() {
		return this.entryDate;
	}

	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

	@Column(name = "BatchCd", nullable = false, length = 8)
	public String getBatchCd() {
		return this.batchCd;
	}

	public void setBatchCd(String batchCd) {
		this.batchCd = batchCd;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof D010004Id))
			return false;
		D010004Id castOther = (D010004Id) other;

		return (this.getLbrCode() == castOther.getLbrCode())
				&& ((this.getEntryDate() == castOther.getEntryDate()) || (this.getEntryDate() != null
						&& castOther.getEntryDate() != null && this.getEntryDate().equals(castOther.getEntryDate())))
				&& ((this.getBatchCd() == castOther.getBatchCd()) || (this.getBatchCd() != null
						&& castOther.getBatchCd() != null && this.getBatchCd().equals(castOther.getBatchCd())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getLbrCode();
		result = 37 * result + (getEntryDate() == null ? 0 : this.getEntryDate().hashCode());
		result = 37 * result + (getBatchCd() == null ? 0 : this.getBatchCd().hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "D010004Id [lbrCode=" + lbrCode + ", entryDate=" + entryDate + ", batchCd=" + batchCd + "]";
	}

	
}
