package com.sil.hbm;

// default package
// Generated Sep 3, 2016 4:39:54 PM by Hibernate Tools 4.3.4.Final

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * D009011 generated by hbm2java
 */
@Entity
@Table(name = "D009011")
@DynamicUpdate
@DynamicInsert
@XmlRootElement
public class D009011 implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6684114035841689672L;
	private int custNo;
	private String nameTitle;
	private String longname;
	private String add1;
	private String add2;
	private String add3;
	private String cityCd;
	private String pinCode;
	private String phone;
	private String fax;
	private String pagerNo;
	private byte indOth;
	private byte freezeType;
	private byte frzReasonCd;
	private String splInstr1;
	private String splInstr2;
	private String relOff;
	private byte rating;
	private int lbrCode;
	private double tdsPercentage;
	private double tdsProjected;
	private double intProjected;
	private double tdsProvision;
	private double intProvision;
	private byte panNo;
	private String panNoDesc;
	private String counCd;
	private int mainCustNo;
	private char tdsYn;
	private short tdsReasonCd;
	private Date tdsFrm15subDt;
	private short relOffCode;
	private String bsrCode;
	private String emailId;
	private String shortName;
	private byte custCategory;
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
	private String custId;
	private short custIdSrNo;
	private int area;
	private char introducerCon;
	private short custAccessCat;

	public D009011() {
	}

	public D009011(int custNo, String nameTitle, String longname, String add1, String add2, String add3, String cityCd,
			String pinCode, String phone, String fax, String pagerNo, byte indOth, byte freezeType, byte frzReasonCd,
			String splInstr1, String splInstr2, String relOff, byte rating, int lbrCode, double tdsPercentage,
			double tdsProjected, double intProjected, double tdsProvision, double intProvision, byte panNo,
			String panNoDesc, String counCd, int mainCustNo, char tdsYn, short tdsReasonCd, Date tdsFrm15subDt,
			short relOffCode, String bsrCode, String emailId, String shortName, byte custCategory, int dbtrAddMk,
			int dbtrAddMb, short dbtrAddMs, Date dbtrAddMd, Date dbtrAddMt, int dbtrAddCk, int dbtrAddCb,
			short dbtrAddCs, Date dbtrAddCd, Date dbtrAddCt, int dbtrLupdMk, int dbtrLupdMb, short dbtrLupdMs,
			Date dbtrLupdMd, Date dbtrLupdMt, int dbtrLupdCk, int dbtrLupdCb, short dbtrLupdCs, Date dbtrLupdCd,
			Date dbtrLupdCt, short dbtrTauthDone, byte dbtrRecStat, byte dbtrAuthDone, byte dbtrAuthNeeded,
			short dbtrUpdtChkId, int dbtrLhisTrnNo, String custId, short custIdSrNo, int area, char introducerCon,
			short custAccessCat) {
		this.custNo = custNo;
		this.nameTitle = nameTitle;
		this.longname = longname;
		this.add1 = add1;
		this.add2 = add2;
		this.add3 = add3;
		this.cityCd = cityCd;
		this.pinCode = pinCode;
		this.phone = phone;
		this.fax = fax;
		this.pagerNo = pagerNo;
		this.indOth = indOth;
		this.freezeType = freezeType;
		this.frzReasonCd = frzReasonCd;
		this.splInstr1 = splInstr1;
		this.splInstr2 = splInstr2;
		this.relOff = relOff;
		this.rating = rating;
		this.lbrCode = lbrCode;
		this.tdsPercentage = tdsPercentage;
		this.tdsProjected = tdsProjected;
		this.intProjected = intProjected;
		this.tdsProvision = tdsProvision;
		this.intProvision = intProvision;
		this.panNo = panNo;
		this.panNoDesc = panNoDesc;
		this.counCd = counCd;
		this.mainCustNo = mainCustNo;
		this.tdsYn = tdsYn;
		this.tdsReasonCd = tdsReasonCd;
		this.tdsFrm15subDt = tdsFrm15subDt;
		this.relOffCode = relOffCode;
		this.bsrCode = bsrCode;
		this.emailId = emailId;
		this.shortName = shortName;
		this.custCategory = custCategory;
		this.dbtrAddMk = dbtrAddMk;
		this.dbtrAddMb = dbtrAddMb;
		this.dbtrAddMs = dbtrAddMs;
		this.dbtrAddMd = dbtrAddMd;
		this.dbtrAddMt = dbtrAddMt;
		this.dbtrAddCk = dbtrAddCk;
		this.dbtrAddCb = dbtrAddCb;
		this.dbtrAddCs = dbtrAddCs;
		this.dbtrAddCd = dbtrAddCd;
		this.dbtrAddCt = dbtrAddCt;
		this.dbtrLupdMk = dbtrLupdMk;
		this.dbtrLupdMb = dbtrLupdMb;
		this.dbtrLupdMs = dbtrLupdMs;
		this.dbtrLupdMd = dbtrLupdMd;
		this.dbtrLupdMt = dbtrLupdMt;
		this.dbtrLupdCk = dbtrLupdCk;
		this.dbtrLupdCb = dbtrLupdCb;
		this.dbtrLupdCs = dbtrLupdCs;
		this.dbtrLupdCd = dbtrLupdCd;
		this.dbtrLupdCt = dbtrLupdCt;
		this.dbtrTauthDone = dbtrTauthDone;
		this.dbtrRecStat = dbtrRecStat;
		this.dbtrAuthDone = dbtrAuthDone;
		this.dbtrAuthNeeded = dbtrAuthNeeded;
		this.dbtrUpdtChkId = dbtrUpdtChkId;
		this.dbtrLhisTrnNo = dbtrLhisTrnNo;
		this.custId = custId;
		this.custIdSrNo = custIdSrNo;
		this.area = area;
		this.introducerCon = introducerCon;
		this.custAccessCat = custAccessCat;
	}

	@Id

	@Column(name = "CustNo", unique = true, nullable = false)
	public int getCustNo() {
		return this.custNo;
	}

	public void setCustNo(int custNo) {
		this.custNo = custNo;
	}

	@Column(name = "NameTitle", nullable = false, length = 4)
	public String getNameTitle() {
		return this.nameTitle;
	}

	public void setNameTitle(String nameTitle) {
		this.nameTitle = nameTitle;
	}

	@Column(name = "Longname", nullable = false, length = 50)
	public String getLongname() {
		return this.longname;
	}

	public void setLongname(String longname) {
		this.longname = longname;
	}

	@Column(name = "Add1", nullable = false, length = 35)
	public String getAdd1() {
		return this.add1;
	}

	public void setAdd1(String add1) {
		this.add1 = add1;
	}

	@Column(name = "Add2", nullable = false, length = 35)
	public String getAdd2() {
		return this.add2;
	}

	public void setAdd2(String add2) {
		this.add2 = add2;
	}

	@Column(name = "Add3", nullable = false, length = 35)
	public String getAdd3() {
		return this.add3;
	}

	public void setAdd3(String add3) {
		this.add3 = add3;
	}

	@Column(name = "CityCd", nullable = false, length = 3)
	public String getCityCd() {
		return this.cityCd;
	}

	public void setCityCd(String cityCd) {
		this.cityCd = cityCd;
	}

	@Column(name = "PinCode", nullable = false, length = 8)
	public String getPinCode() {
		return this.pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	@Column(name = "Phone", nullable = false, length = 15)
	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(name = "Fax", nullable = false, length = 15)
	public String getFax() {
		return this.fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	@Column(name = "PagerNo", nullable = false, length = 15)
	public String getPagerNo() {
		return this.pagerNo;
	}

	public void setPagerNo(String pagerNo) {
		this.pagerNo = pagerNo;
	}

	@Column(name = "IndOth", nullable = false)
	public byte getIndOth() {
		return this.indOth;
	}

	public void setIndOth(byte indOth) {
		this.indOth = indOth;
	}

	@Column(name = "FreezeType", nullable = false)
	public byte getFreezeType() {
		return this.freezeType;
	}

	public void setFreezeType(byte freezeType) {
		this.freezeType = freezeType;
	}

	@Column(name = "FrzReasonCd", nullable = false)
	public byte getFrzReasonCd() {
		return this.frzReasonCd;
	}

	public void setFrzReasonCd(byte frzReasonCd) {
		this.frzReasonCd = frzReasonCd;
	}

	@Column(name = "SplInstr1", nullable = false, length = 60)
	public String getSplInstr1() {
		return this.splInstr1;
	}

	public void setSplInstr1(String splInstr1) {
		this.splInstr1 = splInstr1;
	}

	@Column(name = "SplInstr2", nullable = false, length = 60)
	public String getSplInstr2() {
		return this.splInstr2;
	}

	public void setSplInstr2(String splInstr2) {
		this.splInstr2 = splInstr2;
	}

	@Column(name = "RelOff", nullable = false, length = 35)
	public String getRelOff() {
		return this.relOff;
	}

	public void setRelOff(String relOff) {
		this.relOff = relOff;
	}

	@Column(name = "Rating", nullable = false)
	public byte getRating() {
		return this.rating;
	}

	public void setRating(byte rating) {
		this.rating = rating;
	}

	@Column(name = "LBrCode", nullable = false)
	public int getLbrCode() {
		return this.lbrCode;
	}

	public void setLbrCode(int lbrCode) {
		this.lbrCode = lbrCode;
	}

	@Column(name = "TdsPercentage", nullable = false, precision = 53, scale = 0)
	public double getTdsPercentage() {
		return this.tdsPercentage;
	}

	public void setTdsPercentage(double tdsPercentage) {
		this.tdsPercentage = tdsPercentage;
	}

	@Column(name = "TdsProjected", nullable = false, precision = 53, scale = 0)
	public double getTdsProjected() {
		return this.tdsProjected;
	}

	public void setTdsProjected(double tdsProjected) {
		this.tdsProjected = tdsProjected;
	}

	@Column(name = "IntProjected", nullable = false, precision = 53, scale = 0)
	public double getIntProjected() {
		return this.intProjected;
	}

	public void setIntProjected(double intProjected) {
		this.intProjected = intProjected;
	}

	@Column(name = "TdsProvision", nullable = false, precision = 53, scale = 0)
	public double getTdsProvision() {
		return this.tdsProvision;
	}

	public void setTdsProvision(double tdsProvision) {
		this.tdsProvision = tdsProvision;
	}

	@Column(name = "IntProvision", nullable = false, precision = 53, scale = 0)
	public double getIntProvision() {
		return this.intProvision;
	}

	public void setIntProvision(double intProvision) {
		this.intProvision = intProvision;
	}

	@Column(name = "PanNo", nullable = false)
	public byte getPanNo() {
		return this.panNo;
	}

	public void setPanNo(byte panNo) {
		this.panNo = panNo;
	}

	@Column(name = "PanNoDesc", nullable = false, length = 15)
	public String getPanNoDesc() {
		return this.panNoDesc;
	}

	public void setPanNoDesc(String panNoDesc) {
		this.panNoDesc = panNoDesc;
	}

	@Column(name = "CounCd", nullable = false, length = 3)
	public String getCounCd() {
		return this.counCd;
	}

	public void setCounCd(String counCd) {
		this.counCd = counCd;
	}

	@Column(name = "MainCustNo", nullable = false)
	public int getMainCustNo() {
		return this.mainCustNo;
	}

	public void setMainCustNo(int mainCustNo) {
		this.mainCustNo = mainCustNo;
	}

	@Column(name = "TdsYN", nullable = false, length = 1)
	public char getTdsYn() {
		return this.tdsYn;
	}

	public void setTdsYn(char tdsYn) {
		this.tdsYn = tdsYn;
	}

	@Column(name = "TdsReasonCd", nullable = false)
	public short getTdsReasonCd() {
		return this.tdsReasonCd;
	}

	public void setTdsReasonCd(short tdsReasonCd) {
		this.tdsReasonCd = tdsReasonCd;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TdsFrm15SubDt", nullable = false, length = 23)
	public Date getTdsFrm15subDt() {
		return this.tdsFrm15subDt;
	}

	public void setTdsFrm15subDt(Date tdsFrm15subDt) {
		this.tdsFrm15subDt = tdsFrm15subDt;
	}

	@Column(name = "RelOffCode", nullable = false)
	public short getRelOffCode() {
		return this.relOffCode;
	}

	public void setRelOffCode(short relOffCode) {
		this.relOffCode = relOffCode;
	}

	@Column(name = "BsrCode", nullable = false, length = 20)
	public String getBsrCode() {
		return this.bsrCode;
	}

	public void setBsrCode(String bsrCode) {
		this.bsrCode = bsrCode;
	}

	@Column(name = "EmailId", nullable = false, length = 35)
	public String getEmailId() {
		return this.emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	@Column(name = "ShortName", nullable = false, length = 8)
	public String getShortName() {
		return this.shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	@Column(name = "CustCategory", nullable = false)
	public byte getCustCategory() {
		return this.custCategory;
	}

	public void setCustCategory(byte custCategory) {
		this.custCategory = custCategory;
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

	@Column(name = "CustId", nullable = false, length = 40)
	public String getCustId() {
		return this.custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	@Column(name = "CustIdSrNo", nullable = false)
	public short getCustIdSrNo() {
		return this.custIdSrNo;
	}

	public void setCustIdSrNo(short custIdSrNo) {
		this.custIdSrNo = custIdSrNo;
	}

	@Column(name = "Area", nullable = false)
	public int getArea() {
		return this.area;
	}

	public void setArea(int area) {
		this.area = area;
	}

	@Column(name = "IntroducerCon", nullable = false, length = 1)
	public char getIntroducerCon() {
		return this.introducerCon;
	}

	public void setIntroducerCon(char introducerCon) {
		this.introducerCon = introducerCon;
	}

	@Column(name = "CustAccessCat", nullable = false)
	public short getCustAccessCat() {
		return this.custAccessCat;
	}

	public void setCustAccessCat(short custAccessCat) {
		this.custAccessCat = custAccessCat;
	}

	@Override
	public String toString() {
		return "D009011 [custNo=" + custNo + ", nameTitle=" + nameTitle + ", longname=" + longname + ", add1=" + add1
				+ ", add2=" + add2 + ", add3=" + add3 + ", cityCd=" + cityCd + ", pinCode=" + pinCode + ", phone="
				+ phone + ", fax=" + fax + ", pagerNo=" + pagerNo + ", indOth=" + indOth + ", freezeType=" + freezeType
				+ ", frzReasonCd=" + frzReasonCd + ", splInstr1=" + splInstr1 + ", splInstr2=" + splInstr2 + ", relOff="
				+ relOff + ", rating=" + rating + ", lbrCode=" + lbrCode + ", tdsPercentage=" + tdsPercentage
				+ ", tdsProjected=" + tdsProjected + ", intProjected=" + intProjected + ", tdsProvision=" + tdsProvision
				+ ", intProvision=" + intProvision + ", panNo=" + panNo + ", panNoDesc=" + panNoDesc + ", counCd="
				+ counCd + ", mainCustNo=" + mainCustNo + ", tdsYn=" + tdsYn + ", tdsReasonCd=" + tdsReasonCd
				+ ", tdsFrm15subDt=" + tdsFrm15subDt + ", relOffCode=" + relOffCode + ", bsrCode=" + bsrCode
				+ ", emailId=" + emailId + ", shortName=" + shortName + ", custCategory=" + custCategory
				+ ", dbtrAddMk=" + dbtrAddMk + ", dbtrAddMb=" + dbtrAddMb + ", dbtrAddMs=" + dbtrAddMs + ", dbtrAddMd="
				+ dbtrAddMd + ", dbtrAddMt=" + dbtrAddMt + ", dbtrAddCk=" + dbtrAddCk + ", dbtrAddCb=" + dbtrAddCb
				+ ", dbtrAddCs=" + dbtrAddCs + ", dbtrAddCd=" + dbtrAddCd + ", dbtrAddCt=" + dbtrAddCt + ", dbtrLupdMk="
				+ dbtrLupdMk + ", dbtrLupdMb=" + dbtrLupdMb + ", dbtrLupdMs=" + dbtrLupdMs + ", dbtrLupdMd="
				+ dbtrLupdMd + ", dbtrLupdMt=" + dbtrLupdMt + ", dbtrLupdCk=" + dbtrLupdCk + ", dbtrLupdCb="
				+ dbtrLupdCb + ", dbtrLupdCs=" + dbtrLupdCs + ", dbtrLupdCd=" + dbtrLupdCd + ", dbtrLupdCt="
				+ dbtrLupdCt + ", dbtrTauthDone=" + dbtrTauthDone + ", dbtrRecStat=" + dbtrRecStat + ", dbtrAuthDone="
				+ dbtrAuthDone + ", dbtrAuthNeeded=" + dbtrAuthNeeded + ", dbtrUpdtChkId=" + dbtrUpdtChkId
				+ ", dbtrLhisTrnNo=" + dbtrLhisTrnNo + ", custId=" + custId + ", custIdSrNo=" + custIdSrNo + ", area="
				+ area + ", introducerCon=" + introducerCon + ", custAccessCat=" + custAccessCat + "]";
	}

	
	
	
}
