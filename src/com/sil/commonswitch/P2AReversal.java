package com.sil.commonswitch;

import java.util.Date;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.sil.constants.ResponseCodes;
import com.sil.hbm.D350059;
import com.sil.hbm.D350059Id;
import com.sil.util.DateUtil;
import com.sil.util.HBUtil;

public class P2AReversal {
	public	static org.apache.log4j.Logger logger=org.apache.log4j.Logger.getLogger(P2AReversal.class);
	
	public static String transactionEntry(String lbrCode,String mobNo1,String mmid1,String accNo,String ifsc,String amount,String rrn,String drcr,int setNo,int scrollNo,String batch,String respCd,String respDesc, String stan)
	{
		System.out.println("Branch code::>>"+lbrCode);
		System.out.println("Mob No::>>"+mobNo1);
		System.out.println("MMID::>>"+mmid1);
		System.out.println("Account No::>>"+accNo);
		System.out.println("IFSC::>>"+ifsc);
		System.out.println("Amount::>>"+amount);
		D350059Id d350059Id =new D350059Id();
		d350059Id.setAccNo(accNo);
		d350059Id.setBatchCd(batch);
		d350059Id.setDrcr(drcr);
		d350059Id.setEntryDate(DateUtil.getCurrentDateNew(DataUtils.getOpenDate(Integer.valueOf(lbrCode))));
		d350059Id.setEntryTime(new Date());
		d350059Id.setIfscCd(ifsc);
		d350059Id.setLbrCode(Integer.valueOf(lbrCode));
		d350059Id.setMmid1(mmid1);
		d350059Id.setMobNo1(mobNo1);
		d350059Id.setResponseCd(respCd);
		d350059Id.setResponseDesc(respDesc);
		d350059Id.setRrnNo(rrn);
		d350059Id.setScrollNo(scrollNo);
		d350059Id.setSetNo(setNo);
		if(stan!=null && !stan.trim().isEmpty())
			d350059Id.setStan(stan);
		else
			d350059Id.setStan(rrn!=null && !rrn.trim().isEmpty()?rrn.substring(6):"0");
		d350059Id.setTranAmt(Double.valueOf(amount).intValue());
		d350059Id.setTransactionDate(new Date());
		D350059  d350059=new D350059();
		d350059.setId(d350059Id);
		Session session=HBUtil.getSessionFactory().openSession();
		Transaction t=session.beginTransaction();
		try {
			session.saveOrUpdate(d350059);
			t.commit();
			session.close();
			session=null;
			t=null;
			logger.error("Inserted into D350059........!!!!!!");
			return ResponseCodes.SUCCESS;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			session.close();
			session=null;
			t=null;
			return ResponseCodes.EXCEPTION_OCCURED;
		}
	}
	
	public static String transactionUpdate(String lbrCode,String mobNo1,String mmid1,String accNo,String ifsc,String amount,String rrn,String drcr,int setNo,int scrollNo,String batch,String respCd,String respDesc)
	{
		Session session=HBUtil.getSessionFactory().openSession();
		Transaction td=session.beginTransaction();
		System.out.println("Branch code::>>"+lbrCode);
		System.out.println("Mob No::>>"+mobNo1);
		System.out.println("MMID::>>"+mmid1);
		System.out.println("Account No::>>"+accNo);
		System.out.println("IFSC::>>"+ifsc);
		System.out.println("Amount::>>"+amount);
		
		try {
			String query = "UPDATE D350059 SET SetNo =:setNo ,ScrollNo =:scrollNo ,MobNo1 =:mobNo1, MMID1 =:mmid1, AccNo =:accNo"
					+",IfscCd =:ifsc " 
					+",TranAmt =:amount "
					+",ResponseCd =:respCd " 
					+",ResponseDesc =:respDesc "
					+",TransactionDate =:newDate "
					+",BatchCd =:batch "
					+ "WHERE LbrCode =:lbrCode  "
					+" AND BatchCd = 'MBTR' and TranAmt =:amount AND ResponseCd = '91' AND ResponseDesc = 'PENDING' AND RrnNo = :rrn";
			//session.saveOrUpdate(d350059);
			System.out.println("Query:="+query);
			Query update = session.createQuery(query);
			update.setParameter("setNo", setNo);
			update.setParameter("scrollNo", scrollNo);
			update.setParameter("mobNo1", mobNo1);
			update.setParameter("mmid1", mmid1);
			update.setParameter("accNo",accNo);
			update.setParameter("ifsc", ifsc);
			update.setParameter("amount", Double.valueOf(amount).intValue());
			update.setParameter("respCd", respCd);
			update.setParameter("respDesc",respDesc);
			update.setParameter("lbrCode", Integer.valueOf(lbrCode));
			//update.setParameter("date", DateUtil.getCurrentDateNew(DataUtils.getOpenDate(Integer.valueOf(lbrCode))));
			update.setParameter("rrn", String.format("%1$-20s",rrn));
			update.setParameter("batch", batch);
			update.setParameter("newDate", new Date());
			
			update.executeUpdate();
			
			td.commit();
			session.close();
			session=null;
			td=null;
			logger.error("Updated D350059 for IMPS P2A Credit Transaction");
			return ResponseCodes.SUCCESS;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			session.close();
			session=null;
			td=null;
			return ResponseCodes.EXCEPTION_OCCURED;
		}
	}
	
	
	public static String transactionUpdateToPending(String lbrCode,String mobNo1,String mmid1,String accNo,String ifsc,String amount,String rrn,String drcr,int setNo,int scrollNo,String batch,String respCd,String respDesc)
	{
		Session session=HBUtil.getSessionFactory().openSession();
		Transaction td=session.beginTransaction();
		System.out.println("Branch code::>>"+lbrCode);
		System.out.println("Mob No::>>"+mobNo1);
		System.out.println("MMID::>>"+mmid1);
		System.out.println("Account No::>>"+accNo);
		System.out.println("IFSC::>>"+ifsc);
		System.out.println("Amount::>>"+amount);
		
		try {
			String query = "UPDATE D350059 SET SetNo =:setNo ,ScrollNo =:scrollNo ,MobNo1 =:mobNo1, MMID1 =:mmid1, AccNo =:accNo"
					+",IfscCd =:ifsc " 
					+",TranAmt =:amount "
					+",ResponseCd =:respCd " 
					+",ResponseDesc =:respDesc "
					+",TransactionDate =:newDate "
					+",BatchCd =:batch "
					+ "WHERE LbrCode =:lbrCode "
					+" AND BatchCd = 'MBTR    ' and TranAmt =:amount AND ResponseCd in ('98','91) AND ResponseDesc = 'Transaction fails.' AND RrnNo = :rrn";
			//session.saveOrUpdate(d350059);
			System.out.println("Query:="+query);
			Query update = session.createQuery(query);
			update.setParameter("setNo", setNo);
			update.setParameter("scrollNo", scrollNo);
			update.setParameter("mobNo1", mobNo1);
			update.setParameter("mmid1", mmid1);
			update.setParameter("accNo",accNo);
			update.setParameter("ifsc", ifsc);
			update.setParameter("amount", Double.valueOf(amount).intValue());
			update.setParameter("respCd", respCd);
			update.setParameter("respDesc",respDesc);
			update.setParameter("lbrCode", Integer.valueOf(lbrCode));
			//update.setParameter("date", DateUtil.getCurrentDateNew(DataUtils.getOpenDate(Integer.valueOf(lbrCode))));
			update.setParameter("rrn", String.format("%1$-20s",rrn));
			update.setParameter("batch", batch);
			update.setParameter("newDate", new Date());
			
			update.executeUpdate();
			
			td.commit();
			session.close();
			session=null;
			td=null;
			logger.error("Updated D350059 for IMPS P2A Credit Transaction");
			return ResponseCodes.SUCCESS;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			session.close();
			session=null;
			td=null;
			return ResponseCodes.EXCEPTION_OCCURED;
		}
	}
	
	public static void main(String[] args) {
		logger.error(transactionEntry("3", "918983389108", "9088001", "043200100003296","SRCB0000043", "10", "999999999999", "D", 1, 1, "MBTR", "00",ResponseCodes.SUCCESS_DESC,""));
	}
}
