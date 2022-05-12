package com.sil.commonswitch;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.sil.constants.ResponseCodes;
import com.sil.hbm.Billpayment;
import com.sil.hbm.BillpaymentId;
import com.sil.util.HBUtil;

public class BillPaymentTransactionEntry {

	public	static org.apache.log4j.Logger logger=org.apache.log4j.Logger.getLogger(BillPaymentTransactionEntry.class);
	public static String transactionEntryBillPay(int brCode,String accNo,String batchCode,int setNo,int scrollNo,String consumerNo,String operator,double amount,String respCode,String respDesc,String rrn,String drCr)
	{
		BillpaymentId billpaymentId =new BillpaymentId();
		billpaymentId.setBatchcode(batchCode);
		billpaymentId.setEntrydate(new Date());
		billpaymentId.setLbrcode(brCode);
		billpaymentId.setRrrnno(rrn);
		billpaymentId.setScrollno(scrollNo);
		billpaymentId.setSetno(setNo);
		Billpayment billpayment=new Billpayment();
		billpayment.setAccountno(accNo);
		billpayment.setAmount(amount);
		billpayment.setConsumerno(consumerNo);
		billpayment.setDrcr(drCr);
		billpayment.setEntrytime(new Date());
		billpayment.setId(billpaymentId);
		billpayment.setMerchantrespcode(" ");
		billpayment.setMerchantrespdesc("  ");
		billpayment.setOperator(operator);
		billpayment.setResponsecode(respCode);
		billpayment.setResponsedesc(respDesc);
//		billpayment.setRefno(refno);
		billpayment.setTransactiondate(new Date());
		try {
			Session session=HBUtil.getSessionFactory().openSession();
			Transaction t=session.beginTransaction();
			session.save(billpayment);
			t.commit();
			session.close();
			session=null;
			t=null;
			billpayment=null;
			billpaymentId=null;
			return ResponseCodes.SUCCESS;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			billpayment=null;
			billpaymentId=null;
		}
		return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
	}
	
	public static void main(String[] args) {
		logger.error("Response::>>"+transactionEntryBillPay(3, "SB      000000000000670700000000", "MBTR", 1, 1, "12345", "Airtel_Prepaid", 100, "00", "SUCCESSFUL TRANSACTION", "111111111111", "D")); 
		
	}
}
