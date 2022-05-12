package com.sil.hbm;

import java.io.Serializable;
import javax.persistence.*;

import com.sil.util.DateUtil;

import java.util.Date;


/**
 * The persistent class for the D946220 database table.
 * 
 */
@Entity
@Table(name="D946220")
public class RtgsMessageSecondaryDatFile implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RtgsMessageSecondaryDatFileId id;

	private String CBatchCd;

	private int CBrCode;

	private Date CDate;

	private int CScrollNo;

	private int CSetNo;

	@Column(name="DbtrAddCb")
	private int dbtrAddCb=0;

	@Column(name="DbtrAddCd")
	private Date dbtrAddCd= DateUtil.getDateFromStringNew("19000101");

	@Column(name="DbtrAddCk")
	private int dbtrAddCk=0;

	@Column(name="DbtrAddCs")
	private short dbtrAddCs=0;

	@Column(name="DbtrAddCt")
	private Date dbtrAddCt= DateUtil.getDateFromStringNew("19000101");

	@Column(name="DbtrAddMb")
	private int dbtrAddMb=0;

	@Column(name="DbtrAddMd")
	private Date dbtrAddMd= DateUtil.getDateFromStringNew("19000101");

	@Column(name="DbtrAddMk")
	private int dbtrAddMk=0;

	@Column(name="DbtrAddMs")
	private short dbtrAddMs=0;

	@Column(name="DbtrAddMt")
	private Date dbtrAddMt= DateUtil.getDateFromStringNew("19000101");

	@Column(name="DbtrAuthDone")
	private short dbtrAuthDone=0;

	@Column(name="DbtrAuthNeeded")
	private short dbtrAuthNeeded=0;

	@Column(name="DbtrLHisTrnNo")
	private int dbtrLHisTrnNo=0;

	@Column(name="DbtrLupdCb")
	private int dbtrLupdCb=0;

	@Column(name="DbtrLupdCd")
	private Date dbtrLupdCd= DateUtil.getDateFromStringNew("19000101");

	@Column(name="DbtrLupdCk")
	private int dbtrLupdCk=0;

	@Column(name="DbtrLupdCs")
	private short dbtrLupdCs=0;

	@Column(name="DbtrLupdCt")
	private Date dbtrLupdCt= DateUtil.getDateFromStringNew("19000101");

	@Column(name="DbtrLupdMb")
	private int dbtrLupdMb=0;

	@Column(name="DbtrLupdMd")
	private Date dbtrLupdMd= DateUtil.getDateFromStringNew("19000101");

	@Column(name="DbtrLupdMk")
	private int dbtrLupdMk=0;

	@Column(name="DbtrLupdMs")
	private short dbtrLupdMs=0;

	@Column(name="DbtrLupdMt")
	private Date dbtrLupdMt= DateUtil.getDateFromStringNew("19000101");

	@Column(name="DbtrRecStat")
	private short dbtrRecStat=0;

	@Column(name="DbtrTAuthDone")
	private short dbtrTAuthDone=0;

	@Column(name="DbtrUpdtChkId")
	private short dbtrUpdtChkId=0;

	@Column(name="MsgAuthTime")
	private Date msgAuthTime= DateUtil.getDateFromStringNew("19000101");

	@Column(name="MsgAuthUserCd")
	private String msgAuthUserCd=" ";

	@Column(name="MsgBatchTime")
	private Date msgBatchTime= DateUtil.getDateFromStringNew("19000101");

	@Column(name="MsgBnkAckTime")
	private Date msgBnkAckTime= DateUtil.getDateFromStringNew("19000101");

	@Column(name="MsgMakeTime")
	private Date msgMakeTime= DateUtil.getDateFromStringNew("19000101");

	@Column(name="MsgMakeUserCd")
	private String msgMakeUserCd=" ";

	@Column(name="MsgPIAckTime")
	private Date msgPIAckTime= DateUtil.getDateFromStringNew("19000101");

	@Column(name="MsgRSAuthTime")
	private Date msgRSAuthTime= DateUtil.getDateFromStringNew("19000101");

	@Column(name="MsgRSAuthUserCd")
	private String msgRSAuthUserCd=" ";

	@Column(name="MsgRSTime")
	private Date msgRSTime= DateUtil.getDateFromStringNew("19000101");

	@Column(name="MsgRSUserCd")
	private String msgRSUserCd=" ";

	@Column(name="MsgSendTime")
	private Date msgSendTime= DateUtil.getDateFromStringNew("19000101");

	@Column(name="MsgSendUserCd")
	private String msgSendUserCd=" ";

	@Column(name="MsgStlInd")
	private String msgStlInd=" ";

	private String TBatchCd;

	private int TBrCode;

	private Date TDate;

	private int TScrollNo;

	private int TSetNo;

	public RtgsMessageSecondaryDatFile() {
	}

	public RtgsMessageSecondaryDatFileId getId() {
		return this.id;
	}

	public void setId(RtgsMessageSecondaryDatFileId id) {
		this.id = id;
	}

	public String getCBatchCd() {
		return this.CBatchCd;
	}

	public void setCBatchCd(String CBatchCd) {
		this.CBatchCd = CBatchCd;
	}

	public int getCBrCode() {
		return this.CBrCode;
	}

	public void setCBrCode(int CBrCode) {
		this.CBrCode = CBrCode;
	}

	public Date getCDate() {
		return this.CDate;
	}

	public void setCDate(Date CDate) {
		this.CDate = CDate;
	}

	public int getCScrollNo() {
		return this.CScrollNo;
	}

	public void setCScrollNo(int CScrollNo) {
		this.CScrollNo = CScrollNo;
	}

	public int getCSetNo() {
		return this.CSetNo;
	}

	public void setCSetNo(int CSetNo) {
		this.CSetNo = CSetNo;
	}

	public int getDbtrAddCb() {
		return this.dbtrAddCb;
	}

	public void setDbtrAddCb(int dbtrAddCb) {
		this.dbtrAddCb = dbtrAddCb;
	}

	public Date getDbtrAddCd() {
		return this.dbtrAddCd;
	}

	public void setDbtrAddCd(Date dbtrAddCd) {
		this.dbtrAddCd = dbtrAddCd;
	}

	public int getDbtrAddCk() {
		return this.dbtrAddCk;
	}

	public void setDbtrAddCk(int dbtrAddCk) {
		this.dbtrAddCk = dbtrAddCk;
	}

	public short getDbtrAddCs() {
		return this.dbtrAddCs;
	}

	public void setDbtrAddCs(short dbtrAddCs) {
		this.dbtrAddCs = dbtrAddCs;
	}

	public Date getDbtrAddCt() {
		return this.dbtrAddCt;
	}

	public void setDbtrAddCt(Date dbtrAddCt) {
		this.dbtrAddCt = dbtrAddCt;
	}

	public int getDbtrAddMb() {
		return this.dbtrAddMb;
	}

	public void setDbtrAddMb(int dbtrAddMb) {
		this.dbtrAddMb = dbtrAddMb;
	}

	public Date getDbtrAddMd() {
		return this.dbtrAddMd;
	}

	public void setDbtrAddMd(Date dbtrAddMd) {
		this.dbtrAddMd = dbtrAddMd;
	}

	public int getDbtrAddMk() {
		return this.dbtrAddMk;
	}

	public void setDbtrAddMk(int dbtrAddMk) {
		this.dbtrAddMk = dbtrAddMk;
	}

	public short getDbtrAddMs() {
		return this.dbtrAddMs;
	}

	public void setDbtrAddMs(short dbtrAddMs) {
		this.dbtrAddMs = dbtrAddMs;
	}

	public Date getDbtrAddMt() {
		return this.dbtrAddMt;
	}

	public void setDbtrAddMt(Date dbtrAddMt) {
		this.dbtrAddMt = dbtrAddMt;
	}

	public short getDbtrAuthDone() {
		return this.dbtrAuthDone;
	}

	public void setDbtrAuthDone(short dbtrAuthDone) {
		this.dbtrAuthDone = dbtrAuthDone;
	}

	public short getDbtrAuthNeeded() {
		return this.dbtrAuthNeeded;
	}

	public void setDbtrAuthNeeded(short dbtrAuthNeeded) {
		this.dbtrAuthNeeded = dbtrAuthNeeded;
	}

	public int getDbtrLHisTrnNo() {
		return this.dbtrLHisTrnNo;
	}

	public void setDbtrLHisTrnNo(int dbtrLHisTrnNo) {
		this.dbtrLHisTrnNo = dbtrLHisTrnNo;
	}

	public int getDbtrLupdCb() {
		return this.dbtrLupdCb;
	}

	public void setDbtrLupdCb(int dbtrLupdCb) {
		this.dbtrLupdCb = dbtrLupdCb;
	}

	public Date getDbtrLupdCd() {
		return this.dbtrLupdCd;
	}

	public void setDbtrLupdCd(Date dbtrLupdCd) {
		this.dbtrLupdCd = dbtrLupdCd;
	}

	public int getDbtrLupdCk() {
		return this.dbtrLupdCk;
	}

	public void setDbtrLupdCk(int dbtrLupdCk) {
		this.dbtrLupdCk = dbtrLupdCk;
	}

	public short getDbtrLupdCs() {
		return this.dbtrLupdCs;
	}

	public void setDbtrLupdCs(short dbtrLupdCs) {
		this.dbtrLupdCs = dbtrLupdCs;
	}

	public Date getDbtrLupdCt() {
		return this.dbtrLupdCt;
	}

	public void setDbtrLupdCt(Date dbtrLupdCt) {
		this.dbtrLupdCt = dbtrLupdCt;
	}

	public int getDbtrLupdMb() {
		return this.dbtrLupdMb;
	}

	public void setDbtrLupdMb(int dbtrLupdMb) {
		this.dbtrLupdMb = dbtrLupdMb;
	}

	public Date getDbtrLupdMd() {
		return this.dbtrLupdMd;
	}

	public void setDbtrLupdMd(Date dbtrLupdMd) {
		this.dbtrLupdMd = dbtrLupdMd;
	}

	public int getDbtrLupdMk() {
		return this.dbtrLupdMk;
	}

	public void setDbtrLupdMk(int dbtrLupdMk) {
		this.dbtrLupdMk = dbtrLupdMk;
	}

	public short getDbtrLupdMs() {
		return this.dbtrLupdMs;
	}

	public void setDbtrLupdMs(short dbtrLupdMs) {
		this.dbtrLupdMs = dbtrLupdMs;
	}

	public Date getDbtrLupdMt() {
		return this.dbtrLupdMt;
	}

	public void setDbtrLupdMt(Date dbtrLupdMt) {
		this.dbtrLupdMt = dbtrLupdMt;
	}

	public short getDbtrRecStat() {
		return this.dbtrRecStat;
	}

	public void setDbtrRecStat(short dbtrRecStat) {
		this.dbtrRecStat = dbtrRecStat;
	}

	public short getDbtrTAuthDone() {
		return this.dbtrTAuthDone;
	}

	public void setDbtrTAuthDone(short dbtrTAuthDone) {
		this.dbtrTAuthDone = dbtrTAuthDone;
	}

	public short getDbtrUpdtChkId() {
		return this.dbtrUpdtChkId;
	}

	public void setDbtrUpdtChkId(short dbtrUpdtChkId) {
		this.dbtrUpdtChkId = dbtrUpdtChkId;
	}

	public Date getMsgAuthTime() {
		return this.msgAuthTime;
	}

	public void setMsgAuthTime(Date msgAuthTime) {
		this.msgAuthTime = msgAuthTime;
	}

	public String getMsgAuthUserCd() {
		return this.msgAuthUserCd;
	}

	public void setMsgAuthUserCd(String msgAuthUserCd) {
		this.msgAuthUserCd = msgAuthUserCd;
	}

	public Date getMsgBatchTime() {
		return this.msgBatchTime;
	}

	public void setMsgBatchTime(Date msgBatchTime) {
		this.msgBatchTime = msgBatchTime;
	}

	public Date getMsgBnkAckTime() {
		return this.msgBnkAckTime;
	}

	public void setMsgBnkAckTime(Date msgBnkAckTime) {
		this.msgBnkAckTime = msgBnkAckTime;
	}

	public Date getMsgMakeTime() {
		return this.msgMakeTime;
	}

	public void setMsgMakeTime(Date msgMakeTime) {
		this.msgMakeTime = msgMakeTime;
	}

	public String getMsgMakeUserCd() {
		return this.msgMakeUserCd;
	}

	public void setMsgMakeUserCd(String msgMakeUserCd) {
		this.msgMakeUserCd = msgMakeUserCd;
	}

	public Date getMsgPIAckTime() {
		return this.msgPIAckTime;
	}

	public void setMsgPIAckTime(Date msgPIAckTime) {
		this.msgPIAckTime = msgPIAckTime;
	}

	public Date getMsgRSAuthTime() {
		return this.msgRSAuthTime;
	}

	public void setMsgRSAuthTime(Date msgRSAuthTime) {
		this.msgRSAuthTime = msgRSAuthTime;
	}

	public String getMsgRSAuthUserCd() {
		return this.msgRSAuthUserCd;
	}

	public void setMsgRSAuthUserCd(String msgRSAuthUserCd) {
		this.msgRSAuthUserCd = msgRSAuthUserCd;
	}

	public Date getMsgRSTime() {
		return this.msgRSTime;
	}

	public void setMsgRSTime(Date msgRSTime) {
		this.msgRSTime = msgRSTime;
	}

	public String getMsgRSUserCd() {
		return this.msgRSUserCd;
	}

	public void setMsgRSUserCd(String msgRSUserCd) {
		this.msgRSUserCd = msgRSUserCd;
	}

	public Date getMsgSendTime() {
		return this.msgSendTime;
	}

	public void setMsgSendTime(Date msgSendTime) {
		this.msgSendTime = msgSendTime;
	}

	public String getMsgSendUserCd() {
		return this.msgSendUserCd;
	}

	public void setMsgSendUserCd(String msgSendUserCd) {
		this.msgSendUserCd = msgSendUserCd;
	}

	public String getMsgStlInd() {
		return this.msgStlInd;
	}

	public void setMsgStlInd(String msgStlInd) {
		this.msgStlInd = msgStlInd;
	}

	public String getTBatchCd() {
		return this.TBatchCd;
	}

	public void setTBatchCd(String TBatchCd) {
		this.TBatchCd = TBatchCd;
	}

	public int getTBrCode() {
		return this.TBrCode;
	}

	public void setTBrCode(int TBrCode) {
		this.TBrCode = TBrCode;
	}

	public Date getTDate() {
		return this.TDate;
	}

	public void setTDate(Date TDate) {
		this.TDate = TDate;
	}

	public int getTScrollNo() {
		return this.TScrollNo;
	}

	public void setTScrollNo(int TScrollNo) {
		this.TScrollNo = TScrollNo;
	}

	public int getTSetNo() {
		return this.TSetNo;
	}

	public void setTSetNo(int TSetNo) {
		this.TSetNo = TSetNo;
	}

}