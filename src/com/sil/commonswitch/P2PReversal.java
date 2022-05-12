package com.sil.commonswitch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.sil.constants.ResponseCodes;
import com.sil.hbm.D001004;
import com.sil.hbm.D010004;
import com.sil.hbm.D350044;
import com.sil.hbm.D350044Id;
import com.sil.util.DateUtil;
import com.sil.util.HBUtil;
import com.sil.util.Props;

public class P2PReversal {
	public	static org.apache.log4j.Logger logger=org.apache.log4j.Logger.getLogger(P2PReversal.class);
	public static final SimpleDateFormat sdf1 = new SimpleDateFormat("0yyyyMMdd");	
	public static String reverseTransactionEntry(String lbrCode,String mobNo1,String mmid1,String mobNo2,String mmid2,String amount,String rrn,String drcr,int setNo,int scrollNo)
	{
		System.out.println("mobNo1::>>"+mobNo1);
		System.out.println("mobNo2::>>"+mobNo2);
		System.out.println("MMID1::>>"+mmid1);
		System.out.println("MMID2::>>"+mmid2);
		System.out.println("Amount::>>"+amount);
		System.out.println("DRCR::>>"+drcr);
		System.out.println("rrn::>>"+rrn);
		String transType="IMPS";
		logger.error("Property : "+Props.getBatchProperty(transType));
		String batchCodes[] = Props.getBatchProperty(transType).split("~");
		logger.error("BatchCodes : "+batchCodes.length+"\t"+batchCodes[0]+"\t"+batchCodes[1]);
		D001004 d04OnlineBatchName = VoucherMPOS.getBatchNameFromBatchCode(batchCodes[0]);
		String onlineBatchName = d04OnlineBatchName.getValue().trim();
		logger.error("Batch Name : "+onlineBatchName);
		D001004 d001004 = VoucherMPOS.getD001004(Integer.valueOf(lbrCode)); //Get entry Date
		String entryDateStr = d001004.getValue().trim();
		Date entryDate = null;
		try {
			entryDate = sdf1.parse(entryDateStr);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		D010004 onlineBatch = VoucherMPOS.getD010004(Integer.valueOf(lbrCode), onlineBatchName, entryDate);
		D010004 selectedBatch = null; 	
		D001004 d04OfflineBatchName = VoucherMPOS.getBatchNameFromBatchCode(batchCodes[1]);
		String offlineBatchName = d04OfflineBatchName.getValue().trim();
		logger.error("Batch Name : "+offlineBatchName);	
		if(onlineBatch.getStat() == 1 || onlineBatch.getStat() == 2){
			logger.error("Online Batch Open ");
			selectedBatch = onlineBatch;
		}
		else{
			D010004 offlineBatch =VoucherMPOS.getD010004(Integer.valueOf(lbrCode), offlineBatchName, entryDate);
			if(offlineBatch.getStat() == 1 || offlineBatch.getStat() == 2){
				logger.error("Offline Batch Open ");
				selectedBatch = offlineBatch;
			}
			else{
				logger.error("Both Batches Closed");
			}
		}
		D350044 d350044=new D350044();
		D350044Id d350044Id=new D350044Id();
		d350044Id.setBatchCd(onlineBatchName);
		d350044Id.setDrcr(drcr);
		d350044Id.setEntryDate(DateUtil.getCurrentDateNew(DataUtils.getOpenDate(Integer.valueOf(lbrCode))));
		d350044Id.setEntryTime(new Date());
		d350044Id.setLbrCode(Integer.valueOf(lbrCode));
		d350044Id.setMmid1(mmid1);
		d350044Id.setMmid2(mmid2);
		d350044Id.setMobNo1(mobNo1);
		d350044Id.setMobNo2(mobNo2);
		d350044Id.setResponseCd(ResponseCodes.SUCCESS);
		d350044Id.setResponseDesc(ResponseCodes.SUCCESS_DESC);
		d350044Id.setRrnNo(rrn);
		d350044Id.setScrollNo(scrollNo);
		d350044Id.setSetNo(setNo);
		d350044Id.setStan(rrn.substring(6));
		
		d350044Id.setTranAmt(Double.valueOf(amount).intValue());
		d350044Id.setTransactionDate(entryDate);
		d350044.setId(d350044Id);
		
		Session session=HBUtil.getSessionFactory().openSession();
		Transaction t=session.beginTransaction();
		try {
			session.save(d350044);
			t.commit();
			session.close();
			t=null;
			return "SUCCESS";
		}	catch (Exception e) {
			e.printStackTrace();
			return "ERROR";
		}
	}
	
	public static void main(String[] args) {
		
		
		System.out.println("Result::>>"+reverseTransactionEntry("3", "918983389108", "9088001", "919820548383", "9088002", "111", "652445454546", "D", 123456, 123458));
		
	}
}
