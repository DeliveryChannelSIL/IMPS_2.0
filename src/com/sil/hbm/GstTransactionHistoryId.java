package com.sil.hbm;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * D130108Id entity.
 * 
 * @author MyEclipse Persistence Tools
 */
@Embeddable
public class GstTransactionHistoryId implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer lbrcode;
	private Date entrydate;
	private String batchcd;
	private Integer setno;
	private Integer scrollno;
	private String uniquerefno;

	// Constructors

	/** default constructor */
	public GstTransactionHistoryId() {
	}

	/** full constructor */
	public GstTransactionHistoryId(Integer lbrcode, Date entrydate, String batchcd, Integer setno,
			Integer scrollno, String uniquerefno) {
		this.lbrcode = lbrcode;
		this.entrydate = entrydate;
		this.batchcd = batchcd;
		this.setno = setno;
		this.scrollno = scrollno;
		this.uniquerefno = uniquerefno;
	}

	// Property accessors

	@Column(name = "LBrCode", nullable = false, precision = 6, scale = 0)
	public Integer getLbrcode() {
		return this.lbrcode;
	}

	public void setLbrcode(Integer lbrcode) {
		this.lbrcode = lbrcode;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "EntryDate", nullable = false, length = 7)
	public Date getEntrydate() {
		return this.entrydate;
	}

	public void setEntrydate(Date entrydate) {
		this.entrydate = entrydate;
	}

	@Column(name = "BatchCd", nullable = false, length = 8)
	public String getBatchcd() {
		return this.batchcd;
	}

	public void setBatchcd(String batchcd) {
		this.batchcd = batchcd;
	}

	@Column(name = "SetNo", nullable = false, precision = 8, scale = 0)
	public Integer getSetno() {
		return this.setno;
	}

	public void setSetno(Integer setno) {
		this.setno = setno;
	}

	@Column(name = "ScrollNo", nullable = false, precision = 8, scale = 0)
	public Integer getScrollno() {
		return this.scrollno;
	}

	public void setScrollno(Integer scrollno) {
		this.scrollno = scrollno;
	}

	@Column(name = "UniqueRefNo", nullable = false, length = 50)
	public String getUniquerefno() {
		return this.uniquerefno;
	}

	public void setUniquerefno(String uniquerefno) {
		this.uniquerefno = uniquerefno;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof GstTransactionHistoryId))
			return false;
		GstTransactionHistoryId castOther = (GstTransactionHistoryId) other;

		return ((this.getLbrcode() == castOther.getLbrcode()) || (this
				.getLbrcode() != null
				&& castOther.getLbrcode() != null && this.getLbrcode().equals(
				castOther.getLbrcode())))
				&& ((this.getEntrydate() == castOther.getEntrydate()) || (this
						.getEntrydate() != null
						&& castOther.getEntrydate() != null && this
						.getEntrydate().equals(castOther.getEntrydate())))
				&& ((this.getBatchcd() == castOther.getBatchcd()) || (this
						.getBatchcd() != null
						&& castOther.getBatchcd() != null && this.getBatchcd()
						.equals(castOther.getBatchcd())))
				&& ((this.getSetno() == castOther.getSetno()) || (this
						.getSetno() != null
						&& castOther.getSetno() != null && this.getSetno()
						.equals(castOther.getSetno())))
				&& ((this.getScrollno() == castOther.getScrollno()) || (this
						.getScrollno() != null
						&& castOther.getScrollno() != null && this
						.getScrollno().equals(castOther.getScrollno())))
				&& ((this.getUniquerefno() == castOther.getUniquerefno()) || (this
						.getUniquerefno() != null
						&& castOther.getUniquerefno() != null && this
						.getUniquerefno().equals(castOther.getUniquerefno())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getLbrcode() == null ? 0 : this.getLbrcode().hashCode());
		result = 37 * result
				+ (getEntrydate() == null ? 0 : this.getEntrydate().hashCode());
		result = 37 * result
				+ (getBatchcd() == null ? 0 : this.getBatchcd().hashCode());
		result = 37 * result
				+ (getSetno() == null ? 0 : this.getSetno().hashCode());
		result = 37 * result
				+ (getScrollno() == null ? 0 : this.getScrollno().hashCode());
		result = 37
				* result
				+ (getUniquerefno() == null ? 0 : this.getUniquerefno()
						.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "GstTransactionHistoryId [lbrcode=" + lbrcode + ", entrydate=" + entrydate + ", batchcd=" + batchcd
				+ ", setno=" + setno + ", scrollno=" + scrollno + ", uniquerefno=" + uniquerefno + "]";
	}

	
}