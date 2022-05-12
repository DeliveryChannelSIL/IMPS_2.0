package com.sil.commonswitch;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.sil.hbm.PrepaidCardLoadBalance;
import com.sil.hbm.ReverseLoadBalance;
import com.sil.service.TransactionServiceImpl;
import com.sil.util.HBUtil;

public class PrepaidCardLoadBalanceEntry {
	private static Logger logger=Logger.getLogger(PrepaidCardLoadBalanceEntry.class);
	public static void main(String[] args) {
//		storePrepaidcardEntry(1,"003001000006707","918983389108", 10d, "","0000001",MSGConstants.SUCCESS_MSG, MSGConstants.SUCCESS_MSG, MSGConstants.SUCCESS_MSG, "00",DataUtils.getNextRRN());
	}
	public static void storePrepaidcardEntry(int brCode,String accountno,String mobileno,double amount,String cardNo,String cardinsno,String errorMessage1,String errorMessage2,String errorMessage3,String respcode,String rrn)
	{
		Session session=HBUtil.getSessionFactory().openSession();
		Transaction t= session.beginTransaction();
		try {
			PrepaidCardLoadBalance obj=TransactionServiceImpl.prepaidCardLoadBalanceObj(brCode,accountno,mobileno,amount,cardNo, cardinsno, errorMessage1, errorMessage2, errorMessage3, respcode, rrn);
			if(obj!=null)
			{
				session.save(obj);
				t.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ERROR:",e);
		}finally {
			session.close();
			session=null;
			t=null;
		}
	}
	public static void storeReversePrepaidcardEntry(int brCode,String accountno,String mobileno,double amount,String cardNo,String cardinsno,String errorMessage1,String errorMessage2,String errorMessage3,String respcode,String rrn)
	{
		Session session=HBUtil.getSessionFactory().openSession();
		Transaction t= session.beginTransaction();
		try {
			ReverseLoadBalance obj=TransactionServiceImpl.reversePrepaidCardLoadBalanceObj(brCode,accountno,mobileno,amount,cardNo, cardinsno, errorMessage1, errorMessage2, errorMessage3, respcode, rrn);
			if(obj!=null)
			{
				session.save(obj);
				t.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ERROR:",e);
		}finally {
			session.close();
			session=null;
			t=null;
		}
	}
}
