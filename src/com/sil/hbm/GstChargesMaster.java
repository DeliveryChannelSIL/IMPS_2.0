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
 * D130231 entity.
 * 
 * @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "D130231")
public class GstChargesMaster implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GstChargesMasterId id;
	private Double sgstrate;
	private Double cgstrate;
	private Double igstrate;
	private Double cessrate;
	private Double servicetax;
	private Double cessamt;
	private Double servicetaxamt;
	private String sgstacctid;
	private String cgstacctid;
	private String igstacctid;
	private String sgstroffopt;
	private String cgstroffopt;
	private String igstroffopt;
	private String inctaxintotchg;
	private String chrgyn;
	private Long dbtraddmk;
	private Long dbtraddmb;
	private Long dbtraddms;
	private Date dbtraddmd;
	private Date dbtraddmt;
	private Long dbtraddck;
	private Long dbtraddcb;
	private Long dbtraddcs;
	private Date dbtraddcd;
	private Date dbtraddct;
	private Long dbtrlupdmk;
	private Long dbtrlupdmb;
	private Long dbtrlupdms;
	private Date dbtrlupdmd;
	private Date dbtrlupdmt;
	private Long dbtrlupdck;
	private Long dbtrlupdcb;
	private Long dbtrlupdcs;
	private Date dbtrlupdcd;
	private Date dbtrlupdct;
	private Long dbtrtauthdone;
	private Long dbtrrecstat;
	private Long dbtrauthdone;
	private Long dbtrauthneeded;
	private Long dbtrupdtchkid;
	private Long dbtrlhistrnno;

	// Constructors

	/** default constructor */
	public GstChargesMaster() {
	}

	public GstChargesMaster(GstChargesMasterId id, Double sgstrate, Double cgstrate, String sgstacctid,
			String cgstacctid) {
		super();
		this.id = id;
		this.sgstrate = sgstrate;
		this.cgstrate = cgstrate;
		this.sgstacctid = sgstacctid;
		this.cgstacctid = cgstacctid;
	}

	// Property accessors
	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "chgtype", column = @Column(name = "ChgType", nullable = false, precision = 2, scale = 0)),
			@AttributeOverride(name = "effdate", column = @Column(name = "EffDate", nullable = false, length = 7)) })
	public GstChargesMasterId getId() {
		return this.id;
	}

	public void setId(GstChargesMasterId id) {
		this.id = id;
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

	@Column(name = "SGSTAcctId", nullable = false, length = 32)
	public String getSgstacctid() {
		return this.sgstacctid;
	}

	public void setSgstacctid(String sgstacctid) {
		this.sgstacctid = sgstacctid;
	}

	@Column(name = "CGSTAcctId", nullable = false, length = 32)
	public String getCgstacctid() {
		return this.cgstacctid;
	}

	public void setCgstacctid(String cgstacctid) {
		this.cgstacctid = cgstacctid;
	}

	@Column(name = "IGSTAcctId", nullable = false, length = 32)
	public String getIgstacctid() {
		return this.igstacctid;
	}

	public void setIgstacctid(String igstacctid) {
		this.igstacctid = igstacctid;
	}

	@Column(name = "SGSTROffOpt", nullable = false, length = 1)
	public String getSgstroffopt() {
		return this.sgstroffopt;
	}

	public void setSgstroffopt(String sgstroffopt) {
		this.sgstroffopt = sgstroffopt;
	}

	@Column(name = "CGSTROffOpt", nullable = false, length = 1)
	public String getCgstroffopt() {
		return this.cgstroffopt;
	}

	public void setCgstroffopt(String cgstroffopt) {
		this.cgstroffopt = cgstroffopt;
	}

	@Column(name = "IGSTROffOpt", nullable = false, length = 1)
	public String getIgstroffopt() {
		return this.igstroffopt;
	}

	public void setIgstroffopt(String igstroffopt) {
		this.igstroffopt = igstroffopt;
	}

	@Column(name = "IncTaxInTotChg", nullable = false, length = 1)
	public String getInctaxintotchg() {
		return this.inctaxintotchg;
	}

	public void setInctaxintotchg(String inctaxintotchg) {
		this.inctaxintotchg = inctaxintotchg;
	}

	@Column(name = "ChrgYN", nullable = false, length = 1)
	public String getChrgyn() {
		return this.chrgyn;
	}

	public void setChrgyn(String chrgyn) {
		this.chrgyn = chrgyn;
	}

	@Column(name = "DbtrAddMk", nullable = false, precision = 9, scale = 0)
	public Long getDbtraddmk() {
		return this.dbtraddmk;
	}

	public void setDbtraddmk(Long dbtraddmk) {
		this.dbtraddmk = dbtraddmk;
	}

	@Column(name = "DbtrAddMb", nullable = false, precision = 6, scale = 0)
	public Long getDbtraddmb() {
		return this.dbtraddmb;
	}

	public void setDbtraddmb(Long dbtraddmb) {
		this.dbtraddmb = dbtraddmb;
	}

	@Column(name = "DbtrAddMs", nullable = false, precision = 4, scale = 0)
	public Long getDbtraddms() {
		return this.dbtraddms;
	}

	public void setDbtraddms(Long dbtraddms) {
		this.dbtraddms = dbtraddms;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DbtrAddMd", nullable = false, length = 7)
	public Date getDbtraddmd() {
		return this.dbtraddmd;
	}

	public void setDbtraddmd(Date dbtraddmd) {
		this.dbtraddmd = dbtraddmd;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DbtrAddMt", nullable = false, length = 7)
	public Date getDbtraddmt() {
		return this.dbtraddmt;
	}

	public void setDbtraddmt(Date dbtraddmt) {
		this.dbtraddmt = dbtraddmt;
	}

	@Column(name = "DbtrAddCk", nullable = false, precision = 9, scale = 0)
	public Long getDbtraddck() {
		return this.dbtraddck;
	}

	public void setDbtraddck(Long dbtraddck) {
		this.dbtraddck = dbtraddck;
	}

	@Column(name = "DbtrAddCb", nullable = false, precision = 6, scale = 0)
	public Long getDbtraddcb() {
		return this.dbtraddcb;
	}

	public void setDbtraddcb(Long dbtraddcb) {
		this.dbtraddcb = dbtraddcb;
	}

	@Column(name = "DbtrAddCs", nullable = false, precision = 4, scale = 0)
	public Long getDbtraddcs() {
		return this.dbtraddcs;
	}

	public void setDbtraddcs(Long dbtraddcs) {
		this.dbtraddcs = dbtraddcs;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DbtrAddCd", nullable = false, length = 7)
	public Date getDbtraddcd() {
		return this.dbtraddcd;
	}

	public void setDbtraddcd(Date dbtraddcd) {
		this.dbtraddcd = dbtraddcd;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DbtrAddCt", nullable = false, length = 7)
	public Date getDbtraddct() {
		return this.dbtraddct;
	}

	public void setDbtraddct(Date dbtraddct) {
		this.dbtraddct = dbtraddct;
	}

	@Column(name = "DbtrLupdMk", nullable = false, precision = 9, scale = 0)
	public Long getDbtrlupdmk() {
		return this.dbtrlupdmk;
	}

	public void setDbtrlupdmk(Long dbtrlupdmk) {
		this.dbtrlupdmk = dbtrlupdmk;
	}

	@Column(name = "DbtrLupdMb", nullable = false, precision = 6, scale = 0)
	public Long getDbtrlupdmb() {
		return this.dbtrlupdmb;
	}

	public void setDbtrlupdmb(Long dbtrlupdmb) {
		this.dbtrlupdmb = dbtrlupdmb;
	}

	@Column(name = "DbtrLupdMs", nullable = false, precision = 4, scale = 0)
	public Long getDbtrlupdms() {
		return this.dbtrlupdms;
	}

	public void setDbtrlupdms(Long dbtrlupdms) {
		this.dbtrlupdms = dbtrlupdms;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DbtrLupdMd", nullable = false, length = 7)
	public Date getDbtrlupdmd() {
		return this.dbtrlupdmd;
	}

	public void setDbtrlupdmd(Date dbtrlupdmd) {
		this.dbtrlupdmd = dbtrlupdmd;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DbtrLupdMt", nullable = false, length = 7)
	public Date getDbtrlupdmt() {
		return this.dbtrlupdmt;
	}

	public void setDbtrlupdmt(Date dbtrlupdmt) {
		this.dbtrlupdmt = dbtrlupdmt;
	}

	@Column(name = "DbtrLupdCk", nullable = false, precision = 9, scale = 0)
	public Long getDbtrlupdck() {
		return this.dbtrlupdck;
	}

	public void setDbtrlupdck(Long dbtrlupdck) {
		this.dbtrlupdck = dbtrlupdck;
	}

	@Column(name = "DbtrLupdCb", nullable = false, precision = 6, scale = 0)
	public Long getDbtrlupdcb() {
		return this.dbtrlupdcb;
	}

	public void setDbtrlupdcb(Long dbtrlupdcb) {
		this.dbtrlupdcb = dbtrlupdcb;
	}

	@Column(name = "DbtrLupdCs", nullable = false, precision = 4, scale = 0)
	public Long getDbtrlupdcs() {
		return this.dbtrlupdcs;
	}

	public void setDbtrlupdcs(Long dbtrlupdcs) {
		this.dbtrlupdcs = dbtrlupdcs;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DbtrLupdCd", nullable = false, length = 7)
	public Date getDbtrlupdcd() {
		return this.dbtrlupdcd;
	}

	public void setDbtrlupdcd(Date dbtrlupdcd) {
		this.dbtrlupdcd = dbtrlupdcd;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DbtrLupdCt", nullable = false, length = 7)
	public Date getDbtrlupdct() {
		return this.dbtrlupdct;
	}

	public void setDbtrlupdct(Date dbtrlupdct) {
		this.dbtrlupdct = dbtrlupdct;
	}

	@Column(name = "DbtrTAuthDone", nullable = false, precision = 4, scale = 0)
	public Long getDbtrtauthdone() {
		return this.dbtrtauthdone;
	}

	public void setDbtrtauthdone(Long dbtrtauthdone) {
		this.dbtrtauthdone = dbtrtauthdone;
	}

	@Column(name = "DbtrRecStat", nullable = false, precision = 2, scale = 0)
	public Long getDbtrrecstat() {
		return this.dbtrrecstat;
	}

	public void setDbtrrecstat(Long dbtrrecstat) {
		this.dbtrrecstat = dbtrrecstat;
	}

	@Column(name = "DbtrAuthDone", nullable = false, precision = 2, scale = 0)
	public Long getDbtrauthdone() {
		return this.dbtrauthdone;
	}

	public void setDbtrauthdone(Long dbtrauthdone) {
		this.dbtrauthdone = dbtrauthdone;
	}

	@Column(name = "DbtrAuthNeeded", nullable = false, precision = 2, scale = 0)
	public Long getDbtrauthneeded() {
		return this.dbtrauthneeded;
	}

	public void setDbtrauthneeded(Long dbtrauthneeded) {
		this.dbtrauthneeded = dbtrauthneeded;
	}

	@Column(name = "DbtrUpdtChkId", nullable = false, precision = 4, scale = 0)
	public Long getDbtrupdtchkid() {
		return this.dbtrupdtchkid;
	}

	public void setDbtrupdtchkid(Long dbtrupdtchkid) {
		this.dbtrupdtchkid = dbtrupdtchkid;
	}

	@Column(name = "DbtrLHisTrnNo", nullable = false, precision = 9, scale = 0)
	public Long getDbtrlhistrnno() {
		return this.dbtrlhistrnno;
	}

	public void setDbtrlhistrnno(Long dbtrlhistrnno) {
		this.dbtrlhistrnno = dbtrlhistrnno;
	}

}