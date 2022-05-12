package com.sil.commonswitch;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.sil.constants.MSGConstants;
import com.sil.constants.ResponseCodes;
import com.sil.hbm.SimSePayTrancation;
import com.sil.util.DateUtil;
import com.sil.util.HBUtil;
public class SimSePayTransactionEntry {

	public static void main(String[] args) {
		simSePayTrnEntry("003001000006707", 100, 3, MSGConstants.SUCCESS_MSG, MSGConstants.SUCCESS_MSG, MSGConstants.SUCCESS_MSG, ResponseCodes.SUCCESS, DataUtils.getNextRRN(),MSGConstants.DR,"123456789","SRCB");
	}
	public static void simSePayTrnEntry(String accountno,double amount,int brcode,String errorMSg,String errorMSg1,String errorMSg2,String respcode ,String rrn,String drcr,String reconno,String bankcode)
	{
		Session session=HBUtil.getSessionFactory().openSession();
		Transaction t=session.beginTransaction();
		try {
			SimSePayTrancation payTrancation=prepareSimSePayTransactionObj(accountno, amount, brcode, errorMSg, errorMSg1, errorMSg2, respcode, rrn,drcr,reconno,bankcode);
			session.save(payTrancation);
			t.commit();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			session.close();
			session=null;
			t=null;
		}
	}
	public static SimSePayTrancation prepareSimSePayTransactionObj(String accountno,double amount,int brcode,String errorMSg,String errorMSg1,String errorMSg2,String respcode ,String rrn,String drcr,String reconno,String bankcode)
	{
		SimSePayTrancation trancation=new SimSePayTrancation();
		trancation.setAmount(amount);
		trancation.setAccountno(accountno);
		trancation.setBrcode(brcode);
		trancation.setEntrydate(DateUtil.getCurrentDate());
		trancation.setEntrytime(new Date());
		trancation.setErrorMessage1(errorMSg);
		trancation.setErrorMessage2(errorMSg1);
		trancation.setErrorMessage3(errorMSg2);
		trancation.setRespcode(respcode);
		trancation.setRrn(rrn);
		trancation.setDrcr(drcr);
		trancation.setReconno(reconno);
		trancation.setBankcode(bankcode);
		return trancation;
	}
}
