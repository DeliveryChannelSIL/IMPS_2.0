package com.sil.commonswitch;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.pmw.tinylog.Logger;

import com.sil.constants.Code;
import com.sil.constants.ResponseCodes;
import com.sil.domain.QRUPIRequest;
import com.sil.domain.UPIRequest;
import com.sil.hbm.D009040;
import com.sil.hbm.D350037;
import com.sil.hbm.D350037Id;
import com.sil.hbm.D350059;
import com.sil.hbm.UPITransaction;
import com.sil.hbm.UPITransactionId;
import com.sil.util.DateUtil;
import com.sil.util.DateUtility;
import com.sil.util.HBUtil;

public class UPITransactionEntry {
	public	static org.apache.log4j.Logger logger=org.apache.log4j.Logger.getLogger(UPITransactionEntry.class);
	public static String saveUPITrans(int lbrCode, String crAcctId, UPIRequest request, HashMap<String, String> result,String status, String statusDes) throws Exception {
		String drLbrCode=result.get("DrBrCode");
		if(drLbrCode==null || "".equalsIgnoreCase(drLbrCode.trim()))
			drLbrCode="0";
		UPITransaction upiTrans = new UPITransaction();
		upiTrans.setRrnNo(request.getRrn());
		upiTrans.setCrAcctId(crAcctId);
		upiTrans.setCrBrCode(lbrCode);
		upiTrans.setDrAcctId(result.get("DrAcctId"));
		upiTrans.setResponseCd(status);
		upiTrans.setResponseDesc(statusDes);
		upiTrans.setStatus((short) '2');
		upiTrans.setDrBrCode(drLbrCode);
		upiTrans.setTranAmt(request.getAmt());
		upiTrans.setTxnid(request.getTxnId());
		upiTrans.setSource("");
		upiTrans.setTransactionDate(new Date());
		UPITransactionId id = new UPITransactionId();
		id.setBatchCd(result.get("Batch"));
		id.setEntryDate(DateUtil.getCurrentDateNew(DataUtils.getOpenDate(lbrCode)));
		id.setEntryTime(new Date());
		id.setLbrCode(Integer.parseInt(drLbrCode));
		id.setSetNo(Integer.parseInt(result.get(Code.SETNO)));
		id.setScrollNo(Integer.parseInt(result.get(Code.SCROLLNO)));
		upiTrans.setId(id);
		
		
		upiTrans.setDrcr("C");
		Session session=HBUtil.getSessionFactory().openSession();
		Transaction t=session.beginTransaction();
		try {
			session.save(upiTrans);
			t.commit();
			Logger.info("P2A D350037 Ended");
			return ResponseCodes.SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ERROR:",e);
			return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
		}finally
		{
			session.close();
			session=null;
			t=null;
		}
	}
	
	public static String p2aTransactionEntryM0(String mmid1,String mob1,String accNo,String ifsc, double amount,int setNo,int scrollNo,String batch,int lbrcode,String respCd,String respDesc,String rrn)
	{
		Session session=HBUtil.getSessionFactory().openSession();
		Transaction t=session.beginTransaction();
		Logger.info("P2A D350037 Started");
		String stan=" ";
		if(rrn.trim().length()>0)
			stan=rrn.substring(6);
		try {
			Logger.info("Source MMID : "+mmid1+"\tSource Mobile : "+mob1);
			Logger.info("Source Acct : "+accNo+"\tSource IFSC : "+ifsc);
			logger.error("Source MMID : "+mmid1+"\tSource Mobile : "+mob1);
			logger.error("Source Acct : "+accNo+"\tSource IFSC : "+ifsc);
			D350037Id id = new D350037Id();
			id.setBatchCd(batch);
			id.setEntryDate(DataUtils.getOpenDate(lbrcode));
			id.setEntryTime(new Date());
			id.setLbrCode(lbrcode);
			id.setScrollNo(scrollNo);
			id.setSetNo(setNo);
			
			Criteria criteria = session.createCriteria(D350037.class);
			criteria.add(Restrictions.eq("id.lbrCode", lbrcode));
			//criteria.add(Restrictions.eq("id.batchCd", "MBTR"));
			criteria.add(Restrictions.eq("id.rrnNo", rrn));
			criteria.add(Restrictions.eq("id.tranAmt", amount));
			criteria.add(Restrictions.eq("id.drcr", "CR"));
			criteria.add(Restrictions.eq("id.mobNo1", mob1));
			criteria.add(Restrictions.eq("id.mmid1", mmid1));
			criteria.add(Restrictions.eq("id.accNo", accNo));
			criteria.add(Restrictions.eq("id.ifscCd", ifsc));
			//criteria.add(Restrictions.eq("id.responseCd", "00"));
			criteria.add(Restrictions.eq("id.entryDate", DataUtils.getOpenDate(lbrcode)));
			List<D350037> list = criteria.list();
			
			if (list == null || list.isEmpty()) {
			
				D350037 d37 = new D350037();
				d37.setId(id);
				
				d37.setDrcr("CR");
				d37.setMmid1(mmid1);
				d37.setMobNo1(mob1);
				d37.setAccNo(accNo);
				d37.setIfscCd(ifsc);
				d37.setResponseCd(respCd);
				d37.setResponseDesc(respDesc);
				d37.setRrnNo(rrn);
				d37.setStan(stan);
				d37.setTranAmt(amount);
				d37.setTransactionDate(new Date());
				d37.setRrsponseCd(" ");
				d37.setSource(" ");
				session.save(d37);
			}else {
				
				String hqlUpdate = "update D350037 set responseCd = :respCd where id.lbrCode = :lbrCode and id.rrnNo =:rrnNo and id.tranAmt =:tranAmt and id.drcr=:drcr and id.mobNo1=:mobNo1"
						+ "and id.accNo=:accNo and id.ifscCd=:ifscCd and id.entryDate=:entryDate and id.mmid1=:mmid1";
				// or String hqlUpdate = "update Customer set name = :newName where name = :oldName";
				int updatedEntities = session.createQuery( hqlUpdate )
				        .setParameter("respCd", respCd )
				        .setParameter("lbrCode", lbrcode )
				        .setParameter("rrnNo", rrn )
				        .setParameter("tranAmt", amount )
				        .setParameter("drcr", "CR" )
				        .setParameter("mobNo1", mob1 )
				        .setParameter("accNo", accNo )
				        .setParameter("ifscCd", ifsc )
				        .setParameter("entryDate", DataUtils.getOpenDate(lbrcode) )
				        .setParameter("mmid1", mmid1 )
				        .executeUpdate();
				
			}
			t.commit();
			Logger.info("P2A D350037 Ended");
			return ResponseCodes.SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ERROR:",e);
			return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
		}finally
		{
			session.close();
			session=null;
			t=null;
		}
	}
	
	public static String saveQRUPITrans(int lbrCode, String crAcctId, int drLbrCode, String drAcctId,QRUPIRequest request, 
			HashMap<String, String> result,String status, String statusDes, Transaction t,Session session) throws Exception {
		
		
		UPITransaction upiTrans = new UPITransaction();
		upiTrans.setRrnNo(request.getRrn());
		upiTrans.setCrAcctId(crAcctId);
		upiTrans.setCrBrCode(lbrCode);
		upiTrans.setDrAcctId(drAcctId);
		upiTrans.setResponseCd(status);
		upiTrans.setResponseDesc(statusDes);
		upiTrans.setStatus((short) '2');
		upiTrans.setDrBrCode(drLbrCode+"");
		upiTrans.setTranAmt(request.getAmt());
		upiTrans.setTxnid(request.getVpa());
		upiTrans.setSource("");
		upiTrans.setTransactionDate(new Date());
		UPITransactionId id = new UPITransactionId();
		id.setBatchCd(result.get("Batch"));
		id.setEntryDate(DateUtil.getCurrentDateNew(DataUtils.getOpenDate(lbrCode)));
		id.setEntryTime(new Date());
		id.setLbrCode(drLbrCode);
		id.setSetNo(Integer.parseInt(result.get(Code.SETNO)));
		id.setScrollNo(Integer.parseInt(result.get(Code.SCROLLNO)));
		upiTrans.setId(id);
		
		
		upiTrans.setDrcr("C");
		/*Session session=HBUtil.getSessionFactory().openSession();
		Transaction t=session.beginTransaction();*/
		try {
			session.save(upiTrans);
			t.commit();
			Logger.info("P2A D350037 Ended");
			return ResponseCodes.SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ERROR:",e);
			return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
		}finally
		{
			session.close();
			session=null;
			t=null;
		}
	}
	
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		
		//logger.error(new UPITransactionEntry().p2aTransactionEntry("9088001", "8983389108", "043203100003296", "SRCB0000043", 10, 14565, 54611, "MBTR", 3, "00", "SUCCESSFUL QUERY", "882242121155"));
		
	}
}
