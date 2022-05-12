package com.sil.commonswitch;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.pmw.tinylog.Logger;

import com.sil.constants.ResponseCodes;
import com.sil.hbm.D350036;
import com.sil.hbm.D350036Id;
import com.sil.util.HBUtil;

public class P2PTransactionEntry {
	public	static org.apache.log4j.Logger logger=org.apache.log4j.Logger.getLogger(P2PTransactionEntry.class);
	
	public static String  p2pTransactionEntry(String mmid1,String mob1,String mob2,String mmid2, double amount,int setNo,int scrollNo,String batch,int lbrcode,String respCd,String respDesc,String rrn)
	{
		Logger.info("P2P D350036 Started");
		Session session=HBUtil.getSessionFactory().openSession();
		Transaction t=session.beginTransaction();
		String stan=" ";
		if(rrn.trim().length()>0)
			stan=rrn.substring(6);
		
		try {
			Logger.info("Source MMID : "+mmid1+"\tSource Mobile : "+mob1);
			logger.error("Source MMID : "+mmid1+"\tSource Mobile : "+mob1);
			D350036Id id = new D350036Id();
			id.setBatchCd(batch);
			id.setEntryDate(new Date());
			id.setEntryTime(new Date());
			id.setLbrCode(lbrcode);
			id.setScrollNo(scrollNo);
			id.setSetNo(setNo);
			D350036 d36 = new D350036();
			d36.setId(id);
			d36.setDrcr("DR");
			d36.setMerchMessage("");
			d36.setMerchRespCd("");
			d36.setMmid1(mmid1);
			d36.setMmid2(mmid2);
			d36.setMobNo1(mob1);
			d36.setMobNo2(mob2);
			d36.setResponseCd(respCd);
			d36.setResponseDesc(respDesc);
			d36.setRrnNo(rrn);
			d36.setStan(stan);
			d36.setTranAmt(amount);
			d36.setTransactionDate(new Date());
			d36.setSource(" ");
			session.save(d36);
			t.commit();
			session.close();//
			session=null;
			t=null;
			return ResponseCodes.SUCCESS;
		} catch (Exception e) {
			session=null;
			t=null;
			e.printStackTrace();
			return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
		}
	}
	public static void main(String[] args) {
		logger.error(new P2PTransactionEntry().p2pTransactionEntry("9088001", "8983389108", "9920209434", "9088001", 10, 14565, 54611, "MBTR", 3, "00", "SUCCESSFUL QUERY", "882242121155"));
		
	}
}
