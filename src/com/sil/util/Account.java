package com.sil.util;

import java.util.Date;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.sil.commonswitch.P2AReversal;
import com.sil.hbm.D001002;
import com.sil.hbm.D001002Id;
public class Account {
	public  Logger logger=Logger.getLogger(P2AReversal.class);		
	private int lbrCode;
	private String acc32;
	private String acc15;
	private String name;
	private int status;
	private int freezeStatus;
	private int custNo;
	private Date dtOfBirth;
	private double actClrBalFcy;
	private double totLienBalFcy;
	private double unClrEffFcy;
	private int authNeeded;
	
	public int getLbrCode() {
		return lbrCode;
	}
	public void setLbrCode(int lbrCode) {
		this.lbrCode = lbrCode;
	}
	public String getAcc32() {
		return acc32;
	}
	public void setAcc32(String acc32) {
		this.acc32 = acc32;
	}
	public String getAcc15() {
		
		try (Session session = HBUtil.getSessionFactory().openSession()){
			D001002Id id = new D001002Id();
			id.setCodeType(11080);
			id.setCode(acc32.substring(0, 8).trim());
			Transaction tx = session.beginTransaction();
			D001002 code = session.get(D001002.class, id);
			tx.commit();
			session.close();
			
			if(code == null) return "";
			else{
				return String.format("%03d", lbrCode)+code.getCodeDesc().trim()+acc32.substring(16, 24);
			}
		} catch (Exception e) {
		}
		return acc15;
	}
	public void setAcc15(String acc15) {
		this.acc15 = acc15;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getFreezeStatus() {
		return freezeStatus;
	}
	public void setFreezeStatus(int freezeStatus) {
		this.freezeStatus = freezeStatus;
	}
	public int getCustNo() {
		return custNo;
	}
	public void setCustNo(int custNo) {
		this.custNo = custNo;
	}
	public Date getDtOfBirth() {
		return dtOfBirth;
	}
	public void setDtOfBirth(Date dtOfBirth) {
		this.dtOfBirth = dtOfBirth;
	}
	public double getActClrBalFcy() {
		return actClrBalFcy;
	}
	public void setActClrBalFcy(double actClrBalFcy) {
		this.actClrBalFcy = actClrBalFcy;
	}
	public double getTotLienBalFcy() {
		return totLienBalFcy;
	}
	public void setTotLienBalFcy(double totLienBalFcy) {
		this.totLienBalFcy = totLienBalFcy;
	}
	public double getUnClrEffFcy() {
		return unClrEffFcy;
	}
	public void setUnClrEffFcy(double unClrEffFcy) {
		this.unClrEffFcy = unClrEffFcy;
	}
	public int getAuthNeeded() {
		return authNeeded;
	}
	public void setAuthNeeded(int authNeeded) {
		this.authNeeded = authNeeded;
	}
	
	
	
}
