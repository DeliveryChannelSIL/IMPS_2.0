package com.sil.hbm;

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
 * D130108 entity.
 * 
 * @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "D130108")
public class GstTransactionHistory implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GstTransactionHistoryId id;
	private Integer custno;
	private Integer mainscrollno;
	private String activitytype;
	private String cashflowtype;
	private String booktype;
	private String drcr;
	private String vcracctid;
	private String mainacctid;
	private Double fcytrnamt;
	private String particulars;
	private String canceledflag;
	private String gstno;
	private String igst;
	private String sgst;
	private String cgst;
	private Double sgstrate;
	private Double cgstrate;
	private Double igstrate;
	private Double cessrate;
	private Double servicetax;
	private Double sgstamt;
	private Double cgstamt;
	private Double igstamt;
	private Double cessamt;
	private Double servicetaxamt;
	private String fromgstno;
	private String togstno;
	private String saccode;
	private String hsncode;
	private Date currdate;
	private String sourcestate;
	private String deststate;
	private Date invoicedate;
	private Date uploaddate;
	private Integer chgType;

	// Constructors

	/** default constructor */
	public GstTransactionHistory() {
	}

	public GstTransactionHistory(GstTransactionHistoryId id) {
		super();
		this.id = id;
	}

	public GstTransactionHistory(GstTransactionHistoryId id, Integer custno, Integer mainscrollno, String activitytype,
			String cashflowtype, String booktype, String drcr, String vcracctid, String mainacctid, Double fcytrnamt,
			String particulars, String canceledflag, String gstno, String igst, String sgst, String cgst,
			Double sgstrate, Double cgstrate, Double igstrate, Double cessrate, Double servicetax, Double sgstamt,
			Double cgstamt, Double igstamt, Double cessamt, Double servicetaxamt, String fromgstno, String togstno,
			String saccode, String hsncode, Date currdate, String sourcestate, String deststate, Date invoicedate,
			Date uploaddate, Integer chgType) {
		super();
		this.id = id;
		this.custno = custno;
		this.mainscrollno = mainscrollno;
		this.activitytype = activitytype;
		this.cashflowtype = cashflowtype;
		this.booktype = booktype;
		this.drcr = drcr;
		this.vcracctid = vcracctid;
		this.mainacctid = mainacctid;
		this.fcytrnamt = fcytrnamt;
		this.particulars = particulars;
		this.canceledflag = canceledflag;
		this.gstno = gstno;
		this.igst = igst;
		this.sgst = sgst;
		this.cgst = cgst;
		this.sgstrate = sgstrate;
		this.cgstrate = cgstrate;
		this.igstrate = igstrate;
		this.cessrate = cessrate;
		this.servicetax = servicetax;
		this.sgstamt = sgstamt;
		this.cgstamt = cgstamt;
		this.igstamt = igstamt;
		this.cessamt = cessamt;
		this.servicetaxamt = servicetaxamt;
		this.fromgstno = fromgstno;
		this.togstno = togstno;
		this.saccode = saccode;
		this.hsncode = hsncode;
		this.currdate = currdate;
		this.sourcestate = sourcestate;
		this.deststate = deststate;
		this.invoicedate = invoicedate;
		this.uploaddate = uploaddate;
		this.chgType=chgType;
	}

	// Property accessors
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "lbrcode", column = @Column(name = "LBrCode", nullable = false, precision = 6, scale = 0)),
			@AttributeOverride(name = "entrydate", column = @Column(name = "EntryDate", nullable = false, length = 7)),
			@AttributeOverride(name = "batchcd", column = @Column(name = "BatchCd", nullable = false, length = 8)),
			@AttributeOverride(name = "setno", column = @Column(name = "SetNo", nullable = false, precision = 8, scale = 0)),
			@AttributeOverride(name = "scrollno", column = @Column(name = "ScrollNo", nullable = false, precision = 8, scale = 0)),
			@AttributeOverride(name = "uniquerefno", column = @Column(name = "UniqueRefNo", nullable = false, length = 50)) })
	public GstTransactionHistoryId getId() {
		return this.id;
	}

	public void setId(GstTransactionHistoryId id) {
		this.id = id;
	}

	@Column(name = "CustNo", nullable = false, precision = 9, scale = 0)
	public Integer getCustno() {
		return this.custno;
	}

	public void setCustno(Integer custno) {
		this.custno = custno;
	}

	@Column(name = "MainScrollNo", nullable = false, precision = 8, scale = 0)
	public Integer getMainscrollno() {
		return this.mainscrollno;
	}

	public void setMainscrollno(Integer mainscrollno) {
		this.mainscrollno = mainscrollno;
	}

	@Column(name = "ActivityType", nullable = false, length = 8)
	public String getActivitytype() {
		return this.activitytype;
	}

	public void setActivitytype(String activitytype) {
		this.activitytype = activitytype;
	}

	@Column(name = "CashFlowType", nullable = false, length = 8)
	public String getCashflowtype() {
		return this.cashflowtype;
	}

	public void setCashflowtype(String cashflowtype) {
		this.cashflowtype = cashflowtype;
	}

	@Column(name = "BookType", nullable = false, length = 2)
	public String getBooktype() {
		return this.booktype;
	}

	public void setBooktype(String booktype) {
		this.booktype = booktype;
	}

	@Column(name = "DrCr", nullable = false, length = 1)
	public String getDrcr() {
		return this.drcr;
	}

	public void setDrcr(String drcr) {
		this.drcr = drcr;
	}

	@Column(name = "VcrAcctId", nullable = false, length = 32)
	public String getVcracctid() {
		return this.vcracctid;
	}

	public void setVcracctid(String vcracctid) {
		this.vcracctid = vcracctid;
	}

	@Column(name = "MainAcctId", nullable = false, length = 32)
	public String getMainacctid() {
		return this.mainacctid;
	}

	public void setMainacctid(String mainacctid) {
		this.mainacctid = mainacctid;
	}

	@Column(name = "FcyTrnAmt", nullable = false, precision = 13)
	public Double getFcytrnamt() {
		return this.fcytrnamt;
	}

	public void setFcytrnamt(Double fcytrnamt) {
		this.fcytrnamt = fcytrnamt;
	}

	@Column(name = "Particulars", nullable = false, length = 70)
	public String getParticulars() {
		return this.particulars;
	}

	public void setParticulars(String particulars) {
		this.particulars = particulars;
	}

	@Column(name = "CanceledFlag", nullable = false, length = 1)
	public String getCanceledflag() {
		return this.canceledflag;
	}

	public void setCanceledflag(String canceledflag) {
		this.canceledflag = canceledflag;
	}

	@Column(name = "GSTNo", nullable = false, length = 50)
	public String getGstno() {
		return this.gstno;
	}

	public void setGstno(String gstno) {
		this.gstno = gstno;
	}

	@Column(name = "IGST", nullable = false, length = 50)
	public String getIgst() {
		return this.igst;
	}

	public void setIgst(String igst) {
		this.igst = igst;
	}

	@Column(name = "SGST", nullable = false, length = 50)
	public String getSgst() {
		return this.sgst;
	}

	public void setSgst(String sgst) {
		this.sgst = sgst;
	}

	@Column(name = "CGST", nullable = false, length = 50)
	public String getCgst() {
		return this.cgst;
	}

	public void setCgst(String cgst) {
		this.cgst = cgst;
	}

	@Column(name = "SGSTRate", nullable = false, precision = 8, scale = 6)
	public Double getSgstrate() {
		return this.sgstrate;
	}

	public void setSgstrate(Double sgstrate) {
		this.sgstrate = sgstrate;
	}

	@Column(name = "CGSTRate", nullable = false, precision = 8, scale = 6)
	public Double getCgstrate() {
		return this.cgstrate;
	}

	public void setCgstrate(Double cgstrate) {
		this.cgstrate = cgstrate;
	}

	@Column(name = "IGSTRate", nullable = false, precision = 8, scale = 6)
	public Double getIgstrate() {
		return this.igstrate;
	}

	public void setIgstrate(Double igstrate) {
		this.igstrate = igstrate;
	}

	@Column(name = "CessRate", nullable = false, precision = 8, scale = 6)
	public Double getCessrate() {
		return this.cessrate;
	}

	public void setCessrate(Double cessrate) {
		this.cessrate = cessrate;
	}

	@Column(name = "ServiceTax", nullable = false, precision = 8, scale = 6)
	public Double getServicetax() {
		return this.servicetax;
	}

	public void setServicetax(Double servicetax) {
		this.servicetax = servicetax;
	}

	@Column(name = "SGSTAmt", nullable = false, precision = 13)
	public Double getSgstamt() {
		return this.sgstamt;
	}

	public void setSgstamt(Double sgstamt) {
		this.sgstamt = sgstamt;
	}

	@Column(name = "CGSTAmt", nullable = false, precision = 13)
	public Double getCgstamt() {
		return this.cgstamt;
	}

	public void setCgstamt(Double cgstamt) {
		this.cgstamt = cgstamt;
	}

	@Column(name = "IGSTAmt", nullable = false, precision = 13)
	public Double getIgstamt() {
		return this.igstamt;
	}

	public void setIgstamt(Double igstamt) {
		this.igstamt = igstamt;
	}

	@Column(name = "CessAmt", nullable = false, precision = 13)
	public Double getCessamt() {
		return this.cessamt;
	}

	public void setCessamt(Double cessamt) {
		this.cessamt = cessamt;
	}

	@Column(name = "ServiceTaxAmt", nullable = false, precision = 13)
	public Double getServicetaxamt() {
		return this.servicetaxamt;
	}

	public void setServicetaxamt(Double servicetaxamt) {
		this.servicetaxamt = servicetaxamt;
	}

	@Column(name = "FromGSTNo", nullable = false, length = 15)
	public String getFromgstno() {
		return this.fromgstno;
	}

	public void setFromgstno(String fromgstno) {
		this.fromgstno = fromgstno;
	}

	@Column(name = "ToGSTNo", nullable = false, length = 15)
	public String getTogstno() {
		return this.togstno;
	}

	public void setTogstno(String togstno) {
		this.togstno = togstno;
	}

	@Column(name = "SACCODE", nullable = false, length = 20)
	public String getSaccode() {
		return this.saccode;
	}

	public void setSaccode(String saccode) {
		this.saccode = saccode;
	}

	@Column(name = "HSNCODE", nullable = false, length = 20)
	public String getHsncode() {
		return this.hsncode;
	}

	public void setHsncode(String hsncode) {
		this.hsncode = hsncode;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "CurrDate", nullable = false, length = 7)
	public Date getCurrdate() {
		return this.currdate;
	}

	public void setCurrdate(Date currdate) {
		this.currdate = currdate;
	}

	@Column(name = "SourceState", nullable = false, length = 3)
	public String getSourcestate() {
		return this.sourcestate;
	}

	public void setSourcestate(String sourcestate) {
		this.sourcestate = sourcestate;
	}

	@Column(name = "DestState", nullable = false, length = 3)
	public String getDeststate() {
		return this.deststate;
	}

	public void setDeststate(String deststate) {
		this.deststate = deststate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "InvoiceDate", nullable = false, length = 7)
	public Date getInvoicedate() {
		return this.invoicedate;
	}

	public void setInvoicedate(Date invoicedate) {
		this.invoicedate = invoicedate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "UploadDate", nullable = false, length = 7)
	public Date getUploaddate() {
		return this.uploaddate;
	}

	public void setUploaddate(Date uploaddate) {
		this.uploaddate = uploaddate;
	}
	
	@Column(name = "ChgType", nullable = false, precision = 9, scale = 0)
	public Integer getChgType() {
		return this.chgType;
	}

	public void setChgType(Integer chgType) {
		this.chgType = chgType;
	}

	@Override
	public String toString() {
		return "GstTransactionHistory [id=" + id + ", custno=" + custno + ", mainscrollno=" + mainscrollno
				+ ", activitytype=" + activitytype + ", cashflowtype=" + cashflowtype + ", booktype=" + booktype
				+ ", drcr=" + drcr + ", vcracctid=" + vcracctid + ", mainacctid=" + mainacctid + ", fcytrnamt="
				+ fcytrnamt + ", particulars=" + particulars + ", canceledflag=" + canceledflag + ", gstno=" + gstno
				+ ", igst=" + igst + ", sgst=" + sgst + ", cgst=" + cgst + ", sgstrate=" + sgstrate + ", cgstrate="
				+ cgstrate + ", igstrate=" + igstrate + ", cessrate=" + cessrate + ", servicetax=" + servicetax
				+ ", sgstamt=" + sgstamt + ", cgstamt=" + cgstamt + ", igstamt=" + igstamt + ", cessamt=" + cessamt
				+ ", servicetaxamt=" + servicetaxamt + ", fromgstno=" + fromgstno + ", togstno=" + togstno
				+ ", saccode=" + saccode + ", hsncode=" + hsncode + ", currdate=" + currdate + ", sourcestate="
				+ sourcestate + ", deststate=" + deststate + ", invoicedate=" + invoicedate + ", uploaddate="
				+ uploaddate + ", chgType=" + chgType + "]";
	}

	
}