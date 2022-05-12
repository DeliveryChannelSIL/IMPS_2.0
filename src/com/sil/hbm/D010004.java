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

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "D010004")
public class D010004 implements java.io.Serializable {

	private static final long serialVersionUID = 5606432242364315712L;
	private D010004Id id;
	private int totalDrVcrs;
	private double totalDrAmtLcy;
	private int totalCrVcrs;
	private double totalCrAmtLcy;
	private byte stat;
	private Date postDate;
	private Date feffDate;
	private byte vcEntryMngrType;
	private char authFlag;
	private char postFlag;
	private char feffFlag;
	private int postedBy;
	private int feffBy;
	private char inProcFlag;
	private int dbtrAddMk;
	private int dbtrAddMb;
	private short dbtrAddMs;
	private Date dbtrAddMd;
	private Date dbtrAddMt;
	private int dbtrAddCk;
	private int dbtrAddCb;
	private short dbtrAddCs;
	private Date dbtrAddCd;
	private Date dbtrAddCt;
	private int dbtrLupdMk;
	private int dbtrLupdMb;
	private short dbtrLupdMs;
	private Date dbtrLupdMd;
	private Date dbtrLupdMt;
	private int dbtrLupdCk;
	private int dbtrLupdCb;
	private short dbtrLupdCs;
	private Date dbtrLupdCd;
	private Date dbtrLupdCt;
	private short dbtrTauthDone;
	private byte dbtrRecStat;
	private byte dbtrAuthDone;
	private byte dbtrAuthNeeded;
	private short dbtrUpdtChkId;
	private int dbtrLhisTrnNo;
	private Date postingDate;
	private int postingTime;
	private Date fundingDate;
	private int fundingTime;

	public D010004() {
	}

	

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "lbrCode", column = @Column(name = "LBrCode", nullable = false)),
			@AttributeOverride(name = "entryDate", column = @Column(name = "EntryDate", nullable = false, length = 23)),
			@AttributeOverride(name = "batchCd", column = @Column(name = "BatchCd", nullable = false, length = 8)) })
	public D010004Id getId() {
		return this.id;
	}

	public void setId(D010004Id id) {
		this.id = id;
	}

	@Column(name = "TotalDrVcrs", nullable = false)
	public int getTotalDrVcrs() {
		return this.totalDrVcrs;
	}

	public void setTotalDrVcrs(int totalDrVcrs) {
		this.totalDrVcrs = totalDrVcrs;
	}

	@Column(name = "TotalDrAmtLcy", nullable = false, precision = 53, scale = 0)
	public double getTotalDrAmtLcy() {
		return this.totalDrAmtLcy;
	}

	public void setTotalDrAmtLcy(double totalDrAmtLcy) {
		this.totalDrAmtLcy = totalDrAmtLcy;
	}

	@Column(name = "TotalCrVcrs", nullable = false)
	public int getTotalCrVcrs() {
		return this.totalCrVcrs;
	}

	public void setTotalCrVcrs(int totalCrVcrs) {
		this.totalCrVcrs = totalCrVcrs;
	}

	@Column(name = "TotalCrAmtLcy", nullable = false, precision = 53, scale = 0)
	public double getTotalCrAmtLcy() {
		return this.totalCrAmtLcy;
	}

	public void setTotalCrAmtLcy(double totalCrAmtLcy) {
		this.totalCrAmtLcy = totalCrAmtLcy;
	}

	@Column(name = "Stat", nullable = false)
	public byte getStat() {
		return this.stat;
	}

	public void setStat(byte stat) {
		this.stat = stat;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PostDate", nullable = false, length = 23)
	public Date getPostDate() {
		return this.postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "FeffDate", nullable = false, length = 23)
	public Date getFeffDate() {
		return this.feffDate;
	}

	public void setFeffDate(Date feffDate) {
		this.feffDate = feffDate;
	}

	@Column(name = "VcEntryMngrType", nullable = false)
	public byte getVcEntryMngrType() {
		return this.vcEntryMngrType;
	}

	public void setVcEntryMngrType(byte vcEntryMngrType) {
		this.vcEntryMngrType = vcEntryMngrType;
	}

	@Column(name = "AuthFlag", nullable = false, length = 1)
	public char getAuthFlag() {
		return this.authFlag;
	}

	public void setAuthFlag(char authFlag) {
		this.authFlag = authFlag;
	}

	@Column(name = "PostFlag", nullable = false, length = 1)
	public char getPostFlag() {
		return this.postFlag;
	}

	public void setPostFlag(char postFlag) {
		this.postFlag = postFlag;
	}

	@Column(name = "FeffFlag", nullable = false, length = 1)
	public char getFeffFlag() {
		return this.feffFlag;
	}

	public void setFeffFlag(char feffFlag) {
		this.feffFlag = feffFlag;
	}

	@Column(name = "PostedBy", nullable = false)
	public int getPostedBy() {
		return this.postedBy;
	}

	public void setPostedBy(int postedBy) {
		this.postedBy = postedBy;
	}

	@Column(name = "FeffBy", nullable = false)
	public int getFeffBy() {
		return this.feffBy;
	}

	public void setFeffBy(int feffBy) {
		this.feffBy = feffBy;
	}

	@Column(name = "InProcFlag", nullable = false, length = 1)
	public char getInProcFlag() {
		return this.inProcFlag;
	}

	public void setInProcFlag(char inProcFlag) {
		this.inProcFlag = inProcFlag;
	}

	@Column(name = "DbtrAddMk", nullable = false)
	public int getDbtrAddMk() {
		return this.dbtrAddMk;
	}

	public void setDbtrAddMk(int dbtrAddMk) {
		this.dbtrAddMk = dbtrAddMk;
	}

	@Column(name = "DbtrAddMb", nullable = false)
	public int getDbtrAddMb() {
		return this.dbtrAddMb;
	}

	public void setDbtrAddMb(int dbtrAddMb) {
		this.dbtrAddMb = dbtrAddMb;
	}

	@Column(name = "DbtrAddMs", nullable = false)
	public short getDbtrAddMs() {
		return this.dbtrAddMs;
	}

	public void setDbtrAddMs(short dbtrAddMs) {
		this.dbtrAddMs = dbtrAddMs;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DbtrAddMd", nullable = false, length = 23)
	public Date getDbtrAddMd() {
		return this.dbtrAddMd;
	}

	public void setDbtrAddMd(Date dbtrAddMd) {
		this.dbtrAddMd = dbtrAddMd;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DbtrAddMt", nullable = false, length = 23)
	public Date getDbtrAddMt() {
		return this.dbtrAddMt;
	}

	public void setDbtrAddMt(Date dbtrAddMt) {
		this.dbtrAddMt = dbtrAddMt;
	}

	@Column(name = "DbtrAddCk", nullable = false)
	public int getDbtrAddCk() {
		return this.dbtrAddCk;
	}

	public void setDbtrAddCk(int dbtrAddCk) {
		this.dbtrAddCk = dbtrAddCk;
	}

	@Column(name = "DbtrAddCb", nullable = false)
	public int getDbtrAddCb() {
		return this.dbtrAddCb;
	}

	public void setDbtrAddCb(int dbtrAddCb) {
		this.dbtrAddCb = dbtrAddCb;
	}

	@Column(name = "DbtrAddCs", nullable = false)
	public short getDbtrAddCs() {
		return this.dbtrAddCs;
	}

	public void setDbtrAddCs(short dbtrAddCs) {
		this.dbtrAddCs = dbtrAddCs;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DbtrAddCd", nullable = false, length = 23)
	public Date getDbtrAddCd() {
		return this.dbtrAddCd;
	}

	public void setDbtrAddCd(Date dbtrAddCd) {
		this.dbtrAddCd = dbtrAddCd;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DbtrAddCt", nullable = false, length = 23)
	public Date getDbtrAddCt() {
		return this.dbtrAddCt;
	}

	public void setDbtrAddCt(Date dbtrAddCt) {
		this.dbtrAddCt = dbtrAddCt;
	}

	@Column(name = "DbtrLupdMk", nullable = false)
	public int getDbtrLupdMk() {
		return this.dbtrLupdMk;
	}

	public void setDbtrLupdMk(int dbtrLupdMk) {
		this.dbtrLupdMk = dbtrLupdMk;
	}

	@Column(name = "DbtrLupdMb", nullable = false)
	public int getDbtrLupdMb() {
		return this.dbtrLupdMb;
	}

	public void setDbtrLupdMb(int dbtrLupdMb) {
		this.dbtrLupdMb = dbtrLupdMb;
	}

	@Column(name = "DbtrLupdMs", nullable = false)
	public short getDbtrLupdMs() {
		return this.dbtrLupdMs;
	}

	public void setDbtrLupdMs(short dbtrLupdMs) {
		this.dbtrLupdMs = dbtrLupdMs;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DbtrLupdMd", nullable = false, length = 23)
	public Date getDbtrLupdMd() {
		return this.dbtrLupdMd;
	}

	public void setDbtrLupdMd(Date dbtrLupdMd) {
		this.dbtrLupdMd = dbtrLupdMd;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DbtrLupdMt", nullable = false, length = 23)
	public Date getDbtrLupdMt() {
		return this.dbtrLupdMt;
	}

	public void setDbtrLupdMt(Date dbtrLupdMt) {
		this.dbtrLupdMt = dbtrLupdMt;
	}

	@Column(name = "DbtrLupdCk", nullable = false)
	public int getDbtrLupdCk() {
		return this.dbtrLupdCk;
	}

	public void setDbtrLupdCk(int dbtrLupdCk) {
		this.dbtrLupdCk = dbtrLupdCk;
	}

	@Column(name = "DbtrLupdCb", nullable = false)
	public int getDbtrLupdCb() {
		return this.dbtrLupdCb;
	}

	public void setDbtrLupdCb(int dbtrLupdCb) {
		this.dbtrLupdCb = dbtrLupdCb;
	}

	@Column(name = "DbtrLupdCs", nullable = false)
	public short getDbtrLupdCs() {
		return this.dbtrLupdCs;
	}

	public void setDbtrLupdCs(short dbtrLupdCs) {
		this.dbtrLupdCs = dbtrLupdCs;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DbtrLupdCd", nullable = false, length = 23)
	public Date getDbtrLupdCd() {
		return this.dbtrLupdCd;
	}

	public void setDbtrLupdCd(Date dbtrLupdCd) {
		this.dbtrLupdCd = dbtrLupdCd;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DbtrLupdCt", nullable = false, length = 23)
	public Date getDbtrLupdCt() {
		return this.dbtrLupdCt;
	}

	public void setDbtrLupdCt(Date dbtrLupdCt) {
		this.dbtrLupdCt = dbtrLupdCt;
	}

	@Column(name = "DbtrTAuthDone", nullable = false)
	public short getDbtrTauthDone() {
		return this.dbtrTauthDone;
	}

	public void setDbtrTauthDone(short dbtrTauthDone) {
		this.dbtrTauthDone = dbtrTauthDone;
	}

	@Column(name = "DbtrRecStat", nullable = false)
	public byte getDbtrRecStat() {
		return this.dbtrRecStat;
	}

	public void setDbtrRecStat(byte dbtrRecStat) {
		this.dbtrRecStat = dbtrRecStat;
	}

	@Column(name = "DbtrAuthDone", nullable = false)
	public byte getDbtrAuthDone() {
		return this.dbtrAuthDone;
	}

	public void setDbtrAuthDone(byte dbtrAuthDone) {
		this.dbtrAuthDone = dbtrAuthDone;
	}

	@Column(name = "DbtrAuthNeeded", nullable = false)
	public byte getDbtrAuthNeeded() {
		return this.dbtrAuthNeeded;
	}

	public void setDbtrAuthNeeded(byte dbtrAuthNeeded) {
		this.dbtrAuthNeeded = dbtrAuthNeeded;
	}

	@Column(name = "DbtrUpdtChkId", nullable = false)
	public short getDbtrUpdtChkId() {
		return this.dbtrUpdtChkId;
	}

	public void setDbtrUpdtChkId(short dbtrUpdtChkId) {
		this.dbtrUpdtChkId = dbtrUpdtChkId;
	}

	@Column(name = "DbtrLHisTrnNo", nullable = false)
	public int getDbtrLhisTrnNo() {
		return this.dbtrLhisTrnNo;
	}

	public void setDbtrLhisTrnNo(int dbtrLhisTrnNo) {
		this.dbtrLhisTrnNo = dbtrLhisTrnNo;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "PostingDate", nullable = false, length = 23)
	public Date getPostingDate() {
		return this.postingDate;
	}

	public void setPostingDate(Date postingDate) {
		this.postingDate = postingDate;
	}

	@Column(name = "PostingTime", nullable = false)
	public int getPostingTime() {
		return this.postingTime;
	}

	public void setPostingTime(int postingTime) {
		this.postingTime = postingTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "FundingDate", nullable = false, length = 23)
	public Date getFundingDate() {
		return this.fundingDate;
	}

	public void setFundingDate(Date fundingDate) {
		this.fundingDate = fundingDate;
	}

	@Column(name = "FundingTime", nullable = false)
	public int getFundingTime() {
		return this.fundingTime;
	}

	public void setFundingTime(int fundingTime) {
		this.fundingTime = fundingTime;
	}



	@Override
	public String toString() {
		return "D010004 [id=" + id + ", totalDrVcrs=" + totalDrVcrs + ", totalDrAmtLcy=" + totalDrAmtLcy
				+ ", totalCrVcrs=" + totalCrVcrs + ", totalCrAmtLcy=" + totalCrAmtLcy + ", stat=" + stat + ", postDate="
				+ postDate + ", feffDate=" + feffDate + ", vcEntryMngrType=" + vcEntryMngrType + ", authFlag="
				+ authFlag + ", postFlag=" + postFlag + ", feffFlag=" + feffFlag + ", postedBy=" + postedBy
				+ ", feffBy=" + feffBy + ", inProcFlag=" + inProcFlag + ", dbtrAddMk=" + dbtrAddMk + ", dbtrAddMb="
				+ dbtrAddMb + ", dbtrAddMs=" + dbtrAddMs + ", dbtrAddMd=" + dbtrAddMd + ", dbtrAddMt=" + dbtrAddMt
				+ ", dbtrAddCk=" + dbtrAddCk + ", dbtrAddCb=" + dbtrAddCb + ", dbtrAddCs=" + dbtrAddCs + ", dbtrAddCd="
				+ dbtrAddCd + ", dbtrAddCt=" + dbtrAddCt + ", dbtrLupdMk=" + dbtrLupdMk + ", dbtrLupdMb=" + dbtrLupdMb
				+ ", dbtrLupdMs=" + dbtrLupdMs + ", dbtrLupdMd=" + dbtrLupdMd + ", dbtrLupdMt=" + dbtrLupdMt
				+ ", dbtrLupdCk=" + dbtrLupdCk + ", dbtrLupdCb=" + dbtrLupdCb + ", dbtrLupdCs=" + dbtrLupdCs
				+ ", dbtrLupdCd=" + dbtrLupdCd + ", dbtrLupdCt=" + dbtrLupdCt + ", dbtrTauthDone=" + dbtrTauthDone
				+ ", dbtrRecStat=" + dbtrRecStat + ", dbtrAuthDone=" + dbtrAuthDone + ", dbtrAuthNeeded="
				+ dbtrAuthNeeded + ", dbtrUpdtChkId=" + dbtrUpdtChkId + ", dbtrLhisTrnNo=" + dbtrLhisTrnNo
				+ ", postingDate=" + postingDate + ", postingTime=" + postingTime + ", fundingDate=" + fundingDate
				+ ", fundingTime=" + fundingTime + "]";
	}

	
	
}
