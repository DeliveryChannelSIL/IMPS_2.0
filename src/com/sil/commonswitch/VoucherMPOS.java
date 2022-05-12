package com.sil.commonswitch;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.pmw.tinylog.Logger;

import com.sil.constants.MSGConstants;
import com.sil.domain.TransactionValidationResponse;
import com.sil.hbm.D001004;
import com.sil.hbm.D001004Id;
import com.sil.hbm.D001005;
import com.sil.hbm.D002001;
import com.sil.hbm.D009021;
import com.sil.hbm.D009021Id;
import com.sil.hbm.D009022;
import com.sil.hbm.D009022Id;
import com.sil.hbm.D009040;
import com.sil.hbm.D009040Id;
import com.sil.hbm.D010001;
import com.sil.hbm.D010001Id;
import com.sil.hbm.D010004;
import com.sil.hbm.D010004Id;
import com.sil.hbm.D047003;
import com.sil.hbm.D047003Id;
import com.sil.hbm.D100001;
import com.sil.hbm.D100001Id;
import com.sil.hbm.D100002;
import com.sil.hbm.D100002Id;
import com.sil.hbm.MBTRSCROLLSEQ;
import com.sil.hbm.MBTRSETSEQ;
import com.sil.prop.ConfigurationLoader;
import com.sil.util.DateUtil;
import com.sil.util.HBUtil;
import com.sil.util.Props;

public class VoucherMPOS {
	public	static org.apache.log4j.Logger logger=org.apache.log4j.Logger.getLogger(VoucherMPOS.class);
	public static final SimpleDateFormat sdf = new SimpleDateFormat("HHmmssSS");
	public static final SimpleDateFormat sdf1 = new SimpleDateFormat("0yyyyMMdd");
	Session sessionDebit;
	//Session sessionCredit = HBUtil.getSessionFactory().openSession();
	Date dt = new Date();

	public Transaction txDebit;
	//Transaction txCredit;


	int remLbrCode = 0;
	int benLbrCode = 0;
	int reconNo = 0;
	Date benDate;
	Date remDate;
	Date benTime;
	Date remTime;
	static int usrCode;
	String remBatchCode;
	String benBatchCode;
	int remSetNo;
	int benSetNo;
	int remMainScrollNo;
	int benMainScrollNo;

	String remAcctId;
	String benAcctId;

	double toBramt;

	D009040 drABB = null;
	D009040 crABB = null;


	public VoucherMPOS()
	{
		/*sessionDebit = HBUtil.getSessionFactory().openSession();
		txDebit = sessionDebit.beginTransaction();*/
	}


	public static TransactionValidationResponse debitBranch(String prdAcctId, int lbrCode, double amt,String particulars, String transType,int setNo,int scrollNo,int mainScrollNo,String rrn,Session session){
		TransactionValidationResponse response=new TransactionValidationResponse();
//		Session session = HBUtil.getSessionFactory().openSession();
//		Transaction tx = session.beginTransaction();
		try {
			D001004 d001004 = getD001004(lbrCode); //Get entry Date
			if(null==d001004 )
			{
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.SYSTEM_PARAMETER_NOT_FOUND);
				return response;
			}
			String entryDateStr = d001004.getValue().trim();	

			Date entryDate = sdf1.parse(entryDateStr);
			String batchCodes[] = Props.getBatchProperty(transType).split("~");
			
			D001004 d04OnlineBatchName = getBatchNameFromBatchCode(batchCodes[0]);
			if(d04OnlineBatchName==null)
			{
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ONLINE_BATCH_NOT_FOUND);
				return response;
			}
			String onlineBatchName = d04OnlineBatchName.getValue().trim();
			
			D001004 d04OfflineBatchName = getBatchNameFromBatchCode(batchCodes[1]);
			String offlineBatchName = d04OfflineBatchName.getValue().trim();

			D010004 selectedBatch = null; 			
			D010004 onlineBatch = getD010004(lbrCode, onlineBatchName, entryDate);
			
			logger.error("Entry Date  : "+entryDate);
			System.out.println("Entry Date  : "+entryDate);
			logger.error("BatchCodes : "+batchCodes.length+"\t"+batchCodes[0]+"\t"+batchCodes[1]);
			System.out.println("BatchCodes : "+batchCodes.length+"\t"+batchCodes[0]+"\t"+batchCodes[1]);
			logger.error("Batch Name : "+onlineBatchName);
			System.out.println("Batch Name : "+onlineBatchName);
			logger.error("Batch Name : "+offlineBatchName);
			System.out.println("Batch Name : "+offlineBatchName);
		
			if(entryDateStr==null || entryDateStr.trim().length()<1)
			{
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.OPERATION_DATE_NOT_FOUND);
				return response;
			}	
			if(entryDate==null)
			{
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ENTRY_DATE_UNABLE_TO_PARSE);
				return response;
			}
			if(batchCodes==null || batchCodes.length<1)
			{
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.BATCH_CODE_PARAMETERS_NOT_SET);
				return response;
			}
			if(onlineBatch.getStat() == 1 || onlineBatch.getStat() == 2){
				logger.error("Online Batch Open ");
				selectedBatch = onlineBatch;
			}
			else{
				D010004 offlineBatch = getD010004(lbrCode, offlineBatchName, entryDate);
				if(offlineBatch.getStat() == 1 || offlineBatch.getStat() == 2){
					logger.error("Offline Batch Open ");
					selectedBatch = offlineBatch;
				}
				else{
					logger.error("Both Batches Closed");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.ONLINE_BATCH_NOT_FOUND);
					return response;
				}
			}
			logger.error("Selected batch : "+selectedBatch.getId().getBatchCd());
			
			D009040 d40 = new D009040();
			D009040Id id40 = new D009040Id();
			id40.setBatchCd(selectedBatch.getId().getBatchCd()); //SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016'

			id40.setEntryDate(selectedBatch.getId().getEntryDate()); // SELECT Value FROM D001004 WHERE LBrCode = 9 AND Code = 'LASTOPENDATE'
			id40.setLbrCode(lbrCode); 
			id40.setSetNo((int) setNo);
			id40.setScrollNo((int) scrollNo);
			d40.setId(id40);
			d40.setMainScrollNo((int) scrollNo); //Scroll No
			d40.setPostDate(onlineBatch.getPostDate()); //SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
			d40.setFeffDate(onlineBatch.getFeffDate()); // SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
			d40.setActivityType("DR"); 
			d40.setCashFlowType("DR");
			d40.setValueDate(onlineBatch.getPostDate()); // postdate SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'

			logger.error("Calling D010001");
			D010001 d001 = getD001(lbrCode, selectedBatch.getId().getBatchCd());
			if(null==d001)
			{
				logger.error("Both Batches Closed");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.BOOK_TYPE_NOT_FOUND);
				return response;
			}
//			D009021Id id = new D009021Id();
//			id.setLbrCode(lbrCode);
//			id.setPrdCd(prdAcctId.substring(0,8));		
//			D009021 d21 = session.get(D009021.class, id);
			
			String booktype=d001.getBookType();
			d40.setBookType(booktype); //SELECT BookType FROM D010001 WHERE LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('D');
			d40.setVcrAcctId(prdAcctId);
			d40.setMainAcctId(prdAcctId);
			D009021 d009021 = getD009021(lbrCode, prdAcctId.substring(0,8).trim());
			d40.setMainModType(d009021.getModuleType()); // SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB      
			d40.setVcrModType(d009021.getModuleType()); // SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB      
			d40.setTrnCurCd("INR");
			d40.setFcyTrnAmt(amt);
			d40.setLcyConvRate(1);
			d40.setLcyTrnAmt(amt);
			d40.setInstrBankCd((short) 0);
			d40.setInstrBranchCd((short) 0);
			d40.setInstrType((short) 99); // Depend on chanel
			d40.setInstrNo(rrn);	// RRN
			d40.setInstrDate(new Date()); //Blank
			d40.setParticulars(particulars); //param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('N'); // 
			d40.setShClrFlag('N');
			d40.setAcTotFlag('N');
			d40.setAcClrFlag('N');

			D002001 d002001 = getD002001("WEB");
			if(null==d002001)
			{
				logger.error("Both Batches Closed");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.USER_CODE_NOT_FOUND);
				return response;
			}
			usrCode = d002001.getUsrCode2();
			d40.setMaker(d002001.getUsrCode2()); // SELECT UsrCode2 FROM D002001 WHERE UsrCode1 = 'WEB' 
			d40.setMakerDate(selectedBatch.getId().getEntryDate()); // EntryDate 
			d40.setMakerTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8))); 
			d40.setChecker1(d002001.getUsrCode2()); // SELECT UsrCode2 FROM D002001 WHERE UsrCode1 = 'WEB' 
			d40.setChecker2(0);
			d40.setChecker3(0);
			d40.setChecker4(0);
			d40.setCheckerDate(selectedBatch.getId().getEntryDate()); // EntryDate
			d40.setCheckerTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8)));
			d40.setNoAuthPending((byte) 0);  
			d40.setNoAuthOver((byte) 1); 
			d40.setPostFlag('P'); 
			d40.setAuthFlag('A'); //Authorization flag removed as discussed with vinayak sir 
			d40.setFeffFlag(' ');
			d40.setCanceledFlag(' '); 
			d40.setPostAuthFeffCncl((byte) 0); 
			d40.setUpdtChkId((short) 0); 
			d40.setPartClearAmt(0); 
			d40.setPostTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8))); // Time HHMMSSTT
			try {
				session.save(d40);
//				updating Batch Balance
				Query query = session.createQuery("UPDATE D010004 SET TotalDrVcrs=TotalDrVcrs+1, TotalDrAmtLcy = TotalDrAmtLcy +:amt WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd");
				query.setParameter("amt", amt);
				query.setParameter("lbrcode", lbrCode);
				query.setParameter("entrydate", entryDate);
				query.setParameter("batchcd", onlineBatchName);
				query.executeUpdate();
				response.setBatchCode(onlineBatchName);
				response.setResponse(MSGConstants.SUCCESS);
				response.setErrorMsg(MSGConstants.SUCCESS_MSG);
//				tx.commit();
//				session.close();
//				session=null;
//				tx=null;
				return response;		
			} catch (Exception e) {
				e.printStackTrace();
//				tx.rollback();
//				session=null;
//				tx=null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.EXCEPTION_OCCURED);
				return response;
			}

		} catch (Exception e) {
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.EXCEPTION_OCCURED);
			return response;
		}		
	}

	public static TransactionValidationResponse debitABB(String prdAcctId, int lbrCode, double amt,String particulars, String transType,int setNo,int scrollNo,int mainScrollNo,String rrn,int tokenNo,String flag,Session session){
		System.out.println("<<<<<::   in debitABB() :>>>>>>"+transType);
		
		TransactionValidationResponse response=new TransactionValidationResponse();
//		Session session = HBUtil.getSessionFactory().openSession();
//		Transaction tx = session.beginTransaction();
		try {
			D001004 d001004 = getD001004(lbrCode); //Get entry Date
			if(null==d001004 )
			{
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.SYSTEM_PARAMETER_NOT_FOUND);
				return response;
			}
			String entryDateStr = d001004.getValue().trim();	

			Date entryDate = sdf1.parse(entryDateStr);
			String batchCodes[] = Props.getBatchProperty(transType).split("~");
			
			D001004 d04OnlineBatchName = getBatchNameFromBatchCode(batchCodes[0]);
			if(d04OnlineBatchName==null)
			{
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ONLINE_BATCH_NOT_FOUND);
				return response;
			}
			String onlineBatchName = d04OnlineBatchName.getValue().trim();
			
			D001004 d04OfflineBatchName = getBatchNameFromBatchCode(batchCodes[1]);
			String offlineBatchName = d04OfflineBatchName.getValue().trim();

			D010004 selectedBatch = null; 			
			D010004 onlineBatch = getD010004(lbrCode, onlineBatchName, entryDate);
			
			logger.error("Entry Date  : "+entryDate);
			System.out.println("Entry Date  : "+entryDate);
			logger.error("BatchCodes : "+batchCodes.length+"\t"+batchCodes[0]+"\t"+batchCodes[1]);
			System.out.println("BatchCodes : "+batchCodes.length+"\t"+batchCodes[0]+"\t"+batchCodes[1]);
			logger.error("Batch Name : "+onlineBatchName);
			System.out.println("Batch Name : "+onlineBatchName);
			logger.error("Batch Name : "+offlineBatchName);
			System.out.println("Batch Name : "+offlineBatchName);
			if(entryDateStr==null || entryDateStr.trim().length()<1)
			{
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.OPERATION_DATE_NOT_FOUND);
				return response;
			}	
			if(entryDate==null)
			{
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ENTRY_DATE_UNABLE_TO_PARSE);
				return response;
			}
			if(batchCodes==null || batchCodes.length<1)
			{
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.BATCH_CODE_PARAMETERS_NOT_SET);
				return response;
			}
			
			if(onlineBatch.getStat() == 1 || onlineBatch.getStat() == 2){
				logger.error("Online Batch Open ");
				selectedBatch = onlineBatch;
			}
			else{
				D010004 offlineBatch = getD010004(lbrCode, offlineBatchName, entryDate);
				if(offlineBatch.getStat() == 1 || offlineBatch.getStat() == 2){
					logger.error("Offline Batch Open ");
					selectedBatch = offlineBatch;
				}
				else{
					logger.error("Both Batches Closed");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.ONLINE_BATCH_NOT_FOUND);
					return response;
				}
			}
			logger.error("Selected batch : "+selectedBatch.getId().getBatchCd());
			
			D009040 d40 = new D009040();
			D009040Id id40 = new D009040Id();
			id40.setBatchCd(selectedBatch.getId().getBatchCd()); //SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016'

			id40.setEntryDate(selectedBatch.getId().getEntryDate()); // SELECT Value FROM D001004 WHERE LBrCode = 9 AND Code = 'LASTOPENDATE'
			id40.setLbrCode(lbrCode); 
			id40.setSetNo((int) setNo);
			id40.setScrollNo((int) scrollNo);
			d40.setId(id40);
			d40.setMainScrollNo((int) scrollNo); //Scroll No
			d40.setPostDate(onlineBatch.getPostDate()); //SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
			d40.setFeffDate(onlineBatch.getFeffDate()); // SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
		
			if(flag.equalsIgnoreCase("DEPOSIT"))//ABBREM	ABBDR
			{
				d40.setActivityType("ABBREM"); 
				d40.setCashFlowType("ABBDR");	
			}
			if(flag.equalsIgnoreCase("WITHDRAW"))
			{
				d40.setActivityType("ABB"); 
				d40.setCashFlowType("ABBDR");	
			}
			d40.setValueDate(onlineBatch.getPostDate()); // postdate SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'

			logger.error("Calling D010001");
			D010001 d001 = getD001(lbrCode, selectedBatch.getId().getBatchCd());
			if(null==d001)
			{
				logger.error("Both Batches Closed");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.BOOK_TYPE_NOT_FOUND);
				return response;
			}
//			D009021Id id = new D009021Id();
//			id.setLbrCode(lbrCode);
//			id.setPrdCd(prdAcctId.substring(0,8));		
//			D009021 d21 = session.get(D009021.class, id);
			
			String booktype=d001.getBookType();
			d40.setBookType(booktype); //SELECT BookType FROM D010001 WHERE LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('D');
			d40.setVcrAcctId(prdAcctId);
			d40.setMainAcctId(prdAcctId);
			D009021 d009021 = getD009021(lbrCode, prdAcctId.substring(0,8).trim());
			d40.setMainModType(d009021.getModuleType()); // SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB      
			d40.setVcrModType(d009021.getModuleType()); // SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB      
			d40.setTrnCurCd("INR");
			d40.setFcyTrnAmt(amt);
			d40.setLcyConvRate(1);
			d40.setLcyTrnAmt(amt);
			d40.setInstrBankCd((short) 0);
			d40.setInstrBranchCd((short) 0);
			d40.setInstrType((short) 99); // Depend on chanel
			d40.setInstrNo(rrn);	// RRN
			d40.setInstrDate(new Date()); //Blank
			d40.setParticulars(particulars); //param
			d40.setSysGenVcr((byte) 2); // value 0
			d40.setShTotFlag('Y');  
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');
			d40.setTokenNo(tokenNo);
			D002001 d002001 = getD002001("WEB");
			if(null==d002001)
			{
				logger.error("Both Batches Closed");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.USER_CODE_NOT_FOUND);
				return response;
			}
			usrCode = d002001.getUsrCode2();
			d40.setMaker(d002001.getUsrCode2()); // SELECT UsrCode2 FROM D002001 WHERE UsrCode1 = 'WEB' 
			d40.setMakerDate(selectedBatch.getId().getEntryDate()); // EntryDate 
			d40.setMakerTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8))); 
			d40.setChecker1(d002001.getUsrCode2()); // SELECT UsrCode2 FROM D002001 WHERE UsrCode1 = 'WEB' 
			d40.setChecker2(0);
			d40.setChecker3(0);
			d40.setChecker4(0);
			d40.setCheckerDate(selectedBatch.getId().getEntryDate()); // EntryDate
			d40.setCheckerTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8)));
			d40.setNoAuthPending((byte) 0);  
			d40.setNoAuthOver((byte) 1); 
			d40.setPostFlag('P'); 
			d40.setAuthFlag('A');//Authorization flag removed as discussed with Vinayak Sir on  
			d40.setFeffFlag(' ');
			d40.setCanceledFlag(' '); 
			d40.setPostAuthFeffCncl((byte) 0); 
			d40.setUpdtChkId((short) 0); 
			d40.setPartClearAmt(0);
			d40.setPostTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8))); // Time HHMMSSTT
			try {
				session.save(d40);
//				updating Batch Balance
				Query query = session.createQuery("UPDATE D010004 SET TotalDrVcrs=TotalDrVcrs+1, TotalDrAmtLcy = TotalDrAmtLcy +:amt WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd");
				query.setParameter("amt", amt);
				query.setParameter("lbrcode", lbrCode);
				query.setParameter("entrydate", entryDate);
				query.setParameter("batchcd", onlineBatchName);
				query.executeUpdate();
				response.setBatchCode(onlineBatchName);
				response.setResponse(MSGConstants.SUCCESS);
				response.setErrorMsg(MSGConstants.SUCCESS_MSG);
				return response;		
			} catch (Exception e) {
				e.printStackTrace();
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.EXCEPTION_OCCURED);
				return response;
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.EXCEPTION_OCCURED);
			return response;
		}		
	}
	
	public static TransactionValidationResponse creditABB(String prdAcctId, int lbrCode, double amt,String particulars, String transType,int setNo,int scrollNo,int mainScrollNo,String rrn,int reconNo,String flag,Session session){
		
		System.out.println("<<<<<::   in creditABB() :>>>>>>");
		TransactionValidationResponse response=new TransactionValidationResponse();
//		Session session = HBUtil.getSessionFactory().openSession();
//		Transaction tx = session.beginTransaction();
		
		try {
			D001004 d001004 = getD001004(lbrCode); //Get entry Date
			if(null==d001004 )
			{
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.SYSTEM_PARAMETER_NOT_FOUND);
				return response;
			}
			String entryDateStr = d001004.getValue().trim();	

			Date entryDate = sdf1.parse(entryDateStr);
			String batchCodes[] = Props.getBatchProperty(transType).split("~");
			
			D001004 d04OnlineBatchName = getBatchNameFromBatchCode(batchCodes[0]);
			if(d04OnlineBatchName==null)
			{
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ONLINE_BATCH_NOT_FOUND);
				return response;
			}
			String onlineBatchName = d04OnlineBatchName.getValue().trim();
			
			D001004 d04OfflineBatchName = getBatchNameFromBatchCode(batchCodes[1]);
			String offlineBatchName = d04OfflineBatchName.getValue().trim();

			D010004 selectedBatch = null; 			
			D010004 onlineBatch = getD010004(lbrCode, onlineBatchName, entryDate);
			
			logger.error("Entry Date  : "+entryDate);
			System.out.println("Entry Date  : "+entryDate);
			logger.error("BatchCodes : "+batchCodes.length+"\t"+batchCodes[0]+"\t"+batchCodes[1]);
			System.out.println("BatchCodes : "+batchCodes.length+"\t"+batchCodes[0]+"\t"+batchCodes[1]);
			logger.error("Batch Name : "+onlineBatchName);
			System.out.println("Batch Name : "+onlineBatchName);
			logger.error("Batch Name : "+offlineBatchName);
			System.out.println("Batch Name : "+offlineBatchName);

			if(entryDateStr==null || entryDateStr.trim().length()<1)
			{
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.OPERATION_DATE_NOT_FOUND);
				return response;
			}	
			if(entryDate==null)
			{
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ENTRY_DATE_UNABLE_TO_PARSE);
				return response;
			}
			if(batchCodes==null || batchCodes.length<1)
			{
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.BATCH_CODE_PARAMETERS_NOT_SET);
				return response;
			}
			
			if(onlineBatch.getStat() == 1 || onlineBatch.getStat() == 2){
				logger.error("Online Batch Open ");
				selectedBatch = onlineBatch;
			}
			else{
				D010004 offlineBatch = getD010004(lbrCode, offlineBatchName, entryDate);
				if(offlineBatch.getStat() == 1 || offlineBatch.getStat() == 2){
					logger.error("Offline Batch Open ");
					selectedBatch = offlineBatch;
				}
				else{
					logger.error("Both Batches Closed");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.ONLINE_BATCH_NOT_FOUND);
					return response;
				}
			}
			logger.error("Selected batch : "+selectedBatch.getId().getBatchCd());
			
			D009040 d40 = new D009040();
			D009040Id id40 = new D009040Id();
			id40.setBatchCd(selectedBatch.getId().getBatchCd()); //SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016'

			id40.setEntryDate(selectedBatch.getId().getEntryDate()); // SELECT Value FROM D001004 WHERE LBrCode = 9 AND Code = 'LASTOPENDATE'
			id40.setLbrCode(lbrCode); 
			id40.setSetNo((int) setNo);
			id40.setScrollNo((int) scrollNo);
			d40.setId(id40);
			d40.setMainScrollNo((int) scrollNo); //Scroll No
			d40.setPostDate(onlineBatch.getPostDate()); //SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
			d40.setFeffDate(onlineBatch.getFeffDate()); // SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
			
			if(flag.equalsIgnoreCase("DEPOSIT"))//ABBREM	ABBDR
			{
				d40.setActivityType("CR"); 
				d40.setCashFlowType("CR");	
			}
			if(flag.equalsIgnoreCase("WITHDRAW"))//ABBREM	ABBCR
			{
				d40.setActivityType("ABBREM"); 
				d40.setCashFlowType("ABBCR");	
			}
			if(flag.equalsIgnoreCase("ABB"))//ABBREM	ABBCR
			{
				d40.setActivityType("ABB"); 
				d40.setCashFlowType("ABBCR");	
			}
//			d40.setActivityType("ABBREM"); 
//			d40.setCashFlowType("ABBCR");
			d40.setValueDate(onlineBatch.getPostDate()); // postdate SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'

			logger.error("Calling D010001");
			D010001 d001 = getD001(lbrCode, selectedBatch.getId().getBatchCd());
			if(null==d001)
			{
				logger.error("Both Batches Closed");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.BOOK_TYPE_NOT_FOUND);
				return response;
			}
//			D009021Id id = new D009021Id();
//			id.setLbrCode(lbrCode);
//			id.setPrdCd(prdAcctId.substring(0,8));		
//			D009021 d21 = session.get(D009021.class, id);
			
			String booktype=d001.getBookType();
			d40.setBookType(booktype); //SELECT BookType FROM D010001 WHERE LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('C');
			d40.setVcrAcctId(prdAcctId);
			d40.setMainAcctId(prdAcctId);
			D009021 d009021 = getD009021(lbrCode, prdAcctId.substring(0,8).trim());
			d40.setMainModType(d009021.getModuleType()); // SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB      
			d40.setVcrModType(d009021.getModuleType()); // SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB      
			d40.setTrnCurCd("INR");
			d40.setFcyTrnAmt(amt);
			d40.setLcyConvRate(1);
			d40.setLcyTrnAmt(amt);
			d40.setInstrBankCd((short) 0);
			d40.setInstrBranchCd((short) 0);
			d40.setInstrType((short) 99); // Depend on chanel
			d40.setInstrNo(rrn);	// RRN
			d40.setInstrDate(new Date()); //Blank
			d40.setParticulars(particulars); //param
			d40.setSysGenVcr((byte) 2); // value 0
			d40.setShTotFlag('Y'); // 
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');

			D002001 d002001 = getD002001("WEB");
			if(null==d002001)
			{
				logger.error("Both Batches Closed");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.USER_CODE_NOT_FOUND);
				return response;
			}
			usrCode = d002001.getUsrCode2();
			d40.setMaker(d002001.getUsrCode2()); // SELECT UsrCode2 FROM D002001 WHERE UsrCode1 = 'WEB' 
			d40.setMakerDate(selectedBatch.getId().getEntryDate()); // EntryDate 
			d40.setMakerTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8))); 
			d40.setChecker1(d002001.getUsrCode2()); // SELECT UsrCode2 FROM D002001 WHERE UsrCode1 = 'WEB' 
			d40.setChecker2(0);
			d40.setChecker3(0);
			d40.setChecker4(0);
			d40.setCheckerDate(selectedBatch.getId().getEntryDate()); // EntryDate
			d40.setCheckerTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8)));
			d40.setNoAuthPending((byte) 0);  
			d40.setNoAuthOver((byte) 1); 
			d40.setPostFlag('P'); 
			d40.setAuthFlag('A'); //Authorization falg removed as discussed with Vinayak Sir
			d40.setFeffFlag(' ');
			d40.setCanceledFlag(' '); 
			d40.setPostAuthFeffCncl((byte) 0); 
			d40.setUpdtChkId((short) 0); 
			d40.setPartClearAmt(0);
			d40.setTokenNo(reconNo);
			d40.setPostTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8))); // Time HHMMSSTT
			try {
				session.save(d40);
//				updating Batch Balance
				Query query = session.createQuery("UPDATE D010004 SET TotalCrVcrs=TotalCrVcrs+1, TotalCrAmtLcy = TotalCrAmtLcy +:amt WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd");
				query.setParameter("amt", amt);
				query.setParameter("lbrcode", lbrCode);
				query.setParameter("entrydate", entryDate);
				query.setParameter("batchcd", onlineBatchName);
				query.executeUpdate();
				response.setBatchCode(onlineBatchName);
				response.setResponse(MSGConstants.SUCCESS);
				response.setErrorMsg(MSGConstants.SUCCESS_MSG);
				return response;		
			} catch (Exception e) {
				e.printStackTrace();
				session=null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.EXCEPTION_OCCURED);
				return response;
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.EXCEPTION_OCCURED);
			return response;
		}		
	}
	public static TransactionValidationResponse creditBranch(String prdAcctId, int lbrCode, double amt,String particulars, String transType,int setNo,int scrollNo,int mainScrollNo,String rrn,Session session)
	{
		TransactionValidationResponse response=new TransactionValidationResponse();
		try {
			D001004 d001004 = getD001004(lbrCode); //Get entry Date
			
			if(null==d001004 )
			{
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.SYSTEM_PARAMETER_NOT_FOUND);
				return response;
			}
			String entryDateStr = d001004.getValue().trim();	

			Date entryDate = sdf1.parse(entryDateStr);
			String batchCodes[] = Props.getBatchProperty(transType).split("~");

			D001004 d04OnlineBatchName = getBatchNameFromBatchCode(batchCodes[0]);
			if(d04OnlineBatchName==null)
			{
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ONLINE_BATCH_NOT_FOUND);
				return response;
			}
			String onlineBatchName = d04OnlineBatchName.getValue().trim();

			D001004 d04OfflineBatchName = getBatchNameFromBatchCode(batchCodes[1]);
			String offlineBatchName = d04OfflineBatchName.getValue().trim();

			D010004 selectedBatch = null; 			
			D010004 onlineBatch = getD010004(lbrCode, onlineBatchName, entryDate);
			
			logger.error("Entry Date  : "+entryDate);
			System.out.println("Entry Date  : "+entryDate);
			logger.error("BatchCodes : "+batchCodes.length+"\t"+batchCodes[0]+"\t"+batchCodes[1]);
			System.out.println("BatchCodes : "+batchCodes.length+"\t"+batchCodes[0]+"\t"+batchCodes[1]);
			logger.error("Batch Name : "+onlineBatchName);
			System.out.println("Batch Name : "+onlineBatchName);
			logger.error("Batch Name : "+offlineBatchName);
			System.out.println("Batch Name : "+offlineBatchName);
			
			if(entryDateStr==null || entryDateStr.trim().length()<1)
			{
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.OPERATION_DATE_NOT_FOUND);
				return response;
			}	
			if(entryDate==null)
			{
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ENTRY_DATE_UNABLE_TO_PARSE);
				return response;
			}
			if(batchCodes==null || batchCodes.length<1)
			{
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.BATCH_CODE_PARAMETERS_NOT_SET);
				return response;
			}
			if(onlineBatch.getStat() == 1 || onlineBatch.getStat() == 2){
				logger.error("Online Batch Open ");
				selectedBatch = onlineBatch;
			}
			else{
				D010004 offlineBatch = getD010004(lbrCode, offlineBatchName, entryDate);
				if(offlineBatch.getStat() == 1 || offlineBatch.getStat() == 2){
					logger.error("Offline Batch Open ");
					selectedBatch = offlineBatch;
				}
				else{
					logger.error("Both Batches Closed");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.ONLINE_BATCH_NOT_FOUND);
					return response;
				}
			}
			logger.error("Selected batch : "+selectedBatch.getId().getBatchCd());

			D009040 d40 = new D009040();
			D009040Id id40 = new D009040Id();
			id40.setBatchCd(selectedBatch.getId().getBatchCd()); //SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016'

			id40.setEntryDate(selectedBatch.getId().getEntryDate()); // SELECT Value FROM D001004 WHERE LBrCode = 9 AND Code = 'LASTOPENDATE'
			id40.setLbrCode(lbrCode); 
			id40.setSetNo((int) setNo);
			id40.setScrollNo((int) scrollNo);
			d40.setId(id40);
			d40.setMainScrollNo((int) scrollNo); //Scroll No
			d40.setPostDate(onlineBatch.getPostDate()); //SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
			d40.setFeffDate(onlineBatch.getFeffDate()); // SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
			d40.setActivityType("CR"); 
			d40.setCashFlowType("CR");
			d40.setValueDate(onlineBatch.getPostDate()); // postdate SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'

			logger.error("Calling D010001");
			D010001 d001 = getD001(lbrCode, selectedBatch.getId().getBatchCd());
			if(null==d001)
			{
				logger.error("Both Batches Closed");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.BOOK_TYPE_NOT_FOUND);
				return response;
			}
//			D009021Id id = new D009021Id();
//			id.setLbrCode(lbrCode);
//			id.setPrdCd(prdAcctId.substring(0,8));		
//			D009021 d21 = session.get(D009021.class, id);
			String booktype=d001.getBookType();
			d40.setBookType(booktype); //SELECT BookType FROM D010001 WHERE LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('C');
			d40.setVcrAcctId(prdAcctId);
			d40.setMainAcctId(prdAcctId);
			D009021 d009021 = getD009021(lbrCode, prdAcctId.substring(0,8).trim());
			d40.setMainModType(d009021.getModuleType()); // SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB      
			d40.setVcrModType(d009021.getModuleType()); // SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB      
			d40.setTrnCurCd("INR");
			d40.setFcyTrnAmt(amt);
			d40.setLcyConvRate(1);
			d40.setLcyTrnAmt(amt);
			d40.setInstrBankCd((short) 0);
			d40.setInstrBranchCd((short) 0);
			d40.setInstrType((short) 99); // Depend on chanel
			d40.setInstrNo(rrn);;	// RRN
			d40.setInstrDate(new Date()); //Blank
			d40.setParticulars(particulars); //param
			d40.setSysGenVcr((byte) 2); // value 0
			d40.setShTotFlag('N'); // 
			d40.setShClrFlag('N');
			d40.setAcTotFlag('N');
			d40.setAcClrFlag('N');

			D002001 d002001 = getD002001("WEB");
			if(null==d002001)
			{
				logger.error("Both Batches Closed");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.USER_CODE_NOT_FOUND);
				return response;
			}
			usrCode = d002001.getUsrCode2();
			d40.setMaker(d002001.getUsrCode2()); // SELECT UsrCode2 FROM D002001 WHERE UsrCode1 = 'WEB' 
			d40.setMakerDate(selectedBatch.getId().getEntryDate()); // EntryDate 
			d40.setMakerTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8))); 
			d40.setChecker1(d002001.getUsrCode2()); // SELECT UsrCode2 FROM D002001 WHERE UsrCode1 = 'WEB' 
			d40.setChecker2(0);
			d40.setChecker3(0);
			d40.setChecker4(0);
			d40.setCheckerDate(selectedBatch.getId().getEntryDate()); // EntryDate
			d40.setCheckerTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8)));
			d40.setNoAuthPending((byte) 0); 
			d40.setNoAuthOver((byte) 1); 
			d40.setPostFlag('P'); 
			d40.setAuthFlag('A');//Authorisation flag removed as discussed with vinayak Sir 
			d40.setFeffFlag(' ');
			d40.setCanceledFlag(' '); 
			d40.setPostAuthFeffCncl((byte) 0); 
			d40.setUpdtChkId((short) 0); 
			d40.setPartClearAmt(0); 
			d40.setPostTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8))); // Time HHMMSSTT
			try {
				session.save(d40);
				//				updating Batch Balance
				Query query = session.createQuery("UPDATE D010004 SET TotalCrVcrs = TotalCrVcrs+1, TotalCrAmtLcy= TotalCrAmtLcy + :amt  WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd");
				query.setParameter("amt", amt);
				query.setParameter("lbrcode", lbrCode);
				query.setParameter("entrydate", entryDate);
				query.setParameter("batchcd", onlineBatchName);
				query.executeUpdate();
				response.setBatchCode(onlineBatchName);
				response.setResponse(MSGConstants.SUCCESS);
				response.setErrorMsg(MSGConstants.SUCCESS_MSG);
				return response;		
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.EXCEPTION_OCCURED);
				return response;
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.EXCEPTION_OCCURED);
			return response;
		}		
	}
	
	public TransactionValidationResponse drCrMerchantOrBC(String prdAcctId, int lbrCode,String destAcc,int destBrCode, double amt,String particulars, String transType,int setNo,int scrollNo,int mainScrollNo,String rrn)
	{
		TransactionValidationResponse response=new TransactionValidationResponse();
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		try {
			if(lbrCode!=destBrCode)
			{
				D001004 d001004 = getD001004(lbrCode); //Get entry Date
				if(d001004==null )
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.SYSTEM_PARAMETER_NOT_FOUND);
					return response;
				}
				String entryDateStr = d001004.getValue().trim();	
				Date entryDate = sdf1.parse(entryDateStr);
				String batchCodes[] = Props.getBatchProperty(transType).split("~");

				D001004 d04OnlineBatchName = getBatchNameFromBatchCode(batchCodes[0]);
				if(d04OnlineBatchName==null)
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.ONLINE_BATCH_NOT_FOUND);
					return response;
				}
				String onlineBatchName = d04OnlineBatchName.getValue().trim();

				D001004 d04OfflineBatchName = getBatchNameFromBatchCode(batchCodes[1]);
				
				String offlineBatchName = d04OfflineBatchName.getValue().trim();

				D010004 selectedBatch = null; 			
				D010004 onlineBatch = getD010004(lbrCode, onlineBatchName, entryDate);

				logger.error("Entry Date  : "+entryDate);
				System.out.println("Entry Date  : "+entryDate);
				logger.error("BatchCodes : "+batchCodes.length+"\t"+batchCodes[0]+"\t"+batchCodes[1]);
				System.out.println("BatchCodes : "+batchCodes.length+"\t"+batchCodes[0]+"\t"+batchCodes[1]);
				logger.error("Batch Name : "+onlineBatchName);
				System.out.println("Batch Name : "+onlineBatchName);
				logger.error("Batch Name : "+offlineBatchName);
				System.out.println("Batch Name : "+offlineBatchName);

				if(entryDateStr==null || entryDateStr.trim().length()<1)
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.OPERATION_DATE_NOT_FOUND);
					return response;
				}	
				if(entryDate==null)
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.ENTRY_DATE_UNABLE_TO_PARSE);
					return response;
				}
				if(batchCodes==null || batchCodes.length<1)
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.BATCH_CODE_PARAMETERS_NOT_SET);
					return response;
				}
				

				if(onlineBatch.getStat() == 1 || onlineBatch.getStat() == 2){
					logger.error("Online Batch Open ");
					selectedBatch = onlineBatch;
				}
				else{
					D010004 offlineBatch = getD010004(lbrCode, offlineBatchName, entryDate);
					if(offlineBatch.getStat() == 1 || offlineBatch.getStat() == 2){
						logger.error("Offline Batch Open ");
						selectedBatch = offlineBatch;
					}
					else{
						logger.error("Both Batches Closed");
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMsg(MSGConstants.ONLINE_BATCH_NOT_FOUND);
						return response;
					}
				}
				logger.error("Selected batch : "+selectedBatch.getId().getBatchCd());
				D009040 d40=prepareD009040Object(selectedBatch, onlineBatch, lbrCode, VoucherMPOS.getNextSetNo(),VoucherMPOS.getNextScrollNo(), VoucherMPOS.getNextScrollNo(), prdAcctId, session, amt, particulars,"DR",rrn);
				if(d40==null)
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.OBJECT_CREATION_FAIL);
					return response;
				}
			
				D009040 d40Dest=prepareD009040Object(selectedBatch, onlineBatch, destBrCode, VoucherMPOS.getNextSetNo(),VoucherMPOS.getNextScrollNo(), VoucherMPOS.getNextScrollNo(), destAcc, session, amt, particulars,"CR",rrn);
				if(d40Dest==null)
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.OBJECT_CREATION_FAIL);
					return response;
				}
				
				String batchCodesABB[] = Props.getBatchProperty(MSGConstants.ABB).split("~");

				D001004 d04OnlineABBBatchName = getBatchNameFromBatchCode(batchCodesABB[0]);
				String onlineABBBatchName = d04OnlineABBBatchName.getValue().trim();
				
				D010004 onlineABBBatch = getD010004(lbrCode, onlineABBBatchName, entryDate);
			
				D009040 d40ABB=prepareD009040Object(selectedBatch, onlineABBBatch, lbrCode, VoucherMPOS.getNextSetNo(),VoucherMPOS.getNextScrollNo(), mainScrollNo, MSGConstants.ABB_ACC, session, amt, particulars,"CR",rrn);
				D009040 d40DestABB=prepareD009040Object(selectedBatch, onlineABBBatch, destBrCode, VoucherMPOS.getNextSetNo(),VoucherMPOS.getNextScrollNo(), mainScrollNo,MSGConstants.ABB_ACC, session, amt, particulars,"DR",rrn);
				if(d40ABB==null || d40DestABB==null)
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.OBJECT_CREATION_FAIL);
					return response;
				}
				
				try {
					session.save(d40);
					session.save(d40ABB);
					session.save(d40DestABB);
					session.save(d40Dest);
					//				updating Batch Balance
					Query query = session.createQuery("UPDATE D010004 SET TotalDrVcrs=TotalDrVcrs+1, TotalDrAmtLcy = TotalDrAmtLcy +:amt , TotalCrVcrs = TotalCrVcrs+1, TotalCrAmtLcy= TotalCrAmtLcy + :amt  WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd");
					query.setParameter("amt", amt);
					query.setParameter("lbrcode", lbrCode);
					query.setParameter("entrydate", entryDate);
					query.setParameter("batchcd", onlineBatchName);
					query.executeUpdate();
					
					Query queryABB = session.createQuery("UPDATE D010004 SET TotalDrVcrs=TotalDrVcrs+1, TotalDrAmtLcy = TotalDrAmtLcy +:amt , TotalCrVcrs = TotalCrVcrs+1, TotalCrAmtLcy= TotalCrAmtLcy + :amt  WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd");
					queryABB.setParameter("amt", amt);
					queryABB.setParameter("lbrcode", lbrCode);
					queryABB.setParameter("entrydate", entryDate);
					queryABB.setParameter("batchcd", onlineABBBatchName);
					queryABB.executeUpdate();
					
					response.setBatchCode(onlineBatchName);
					response.setResponse(MSGConstants.SUCCESS);
					response.setErrorMsg(MSGConstants.SUCCESS_MSG);
					tx.commit();
					session.close();
					session=null;
					tx=null;
					return response;		
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					tx.rollback();
					session=null;
					tx=null;
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.EXCEPTION_OCCURED);
					return response;
				}
			}else if(lbrCode==destBrCode)
			{
				D001004 d001004 = getD001004(lbrCode); //Get entry Date
				if(null==d001004 )
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.SYSTEM_PARAMETER_NOT_FOUND);
					return response;
				}
				String entryDateStr = d001004.getValue().trim();	
				if(entryDateStr==null || entryDateStr.trim().length()<1)
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.OPERATION_DATE_NOT_FOUND);
					return response;
				}
				Date entryDate = sdf1.parse(entryDateStr);
				if(entryDate==null)
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.ENTRY_DATE_UNABLE_TO_PARSE);
					return response;
				}
				String batchCodes[] = Props.getBatchProperty(transType).split("~");
				if(batchCodes==null || batchCodes.length<1)
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.BATCH_CODE_PARAMETERS_NOT_SET);
					return response;
				}
				D001004 d04OnlineBatchName = getBatchNameFromBatchCode(batchCodes[0]);
				if(d04OnlineBatchName==null)
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.ONLINE_BATCH_NOT_FOUND);
					return response;
				}

				String onlineBatchName = d04OnlineBatchName.getValue().trim();

				D001004 d04OfflineBatchName = getBatchNameFromBatchCode(batchCodes[1]);
				String offlineBatchName = d04OfflineBatchName.getValue().trim();

				D010004 selectedBatch = null; 			
				D010004 onlineBatch = getD010004(lbrCode, onlineBatchName, entryDate);

				logger.error("Entry Date  : "+entryDate);
				System.out.println("Entry Date  : "+entryDate);
				logger.error("BatchCodes : "+batchCodes.length+"\t"+batchCodes[0]+"\t"+batchCodes[1]);
				System.out.println("BatchCodes : "+batchCodes.length+"\t"+batchCodes[0]+"\t"+batchCodes[1]);
				logger.error("Batch Name : "+onlineBatchName);
				System.out.println("Batch Name : "+onlineBatchName);
				logger.error("Batch Name : "+offlineBatchName);
				System.out.println("Batch Name : "+offlineBatchName);

				if(onlineBatch.getStat() == 1 || onlineBatch.getStat() == 2){
					logger.error("Online Batch Open ");
					selectedBatch = onlineBatch;
				}
				else{
					D010004 offlineBatch = getD010004(lbrCode, offlineBatchName, entryDate);
					if(offlineBatch.getStat() == 1 || offlineBatch.getStat() == 2){
						logger.error("Offline Batch Open ");
						selectedBatch = offlineBatch;
					}
					else{
						logger.error("Both Batches Closed");
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMsg(MSGConstants.ONLINE_BATCH_NOT_FOUND);
						return response;
					}
				}
				System.out.println("Source ACC::>>"+prdAcctId);
				System.out.println("Dest ACC::>>"+destAcc);
				logger.error("Selected batch : "+selectedBatch.getId().getBatchCd());
				D009040 d40=prepareD009040Object(selectedBatch, onlineBatch, lbrCode, setNo,VoucherMPOS.getNextScrollNo(), mainScrollNo, prdAcctId, session, amt, particulars,"DR",rrn);
				if(d40==null)
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.OBJECT_CREATION_FAIL);
					return response;
				}	
			
				D009040 d40Dest=prepareD009040Object(selectedBatch, onlineBatch, destBrCode, setNo,VoucherMPOS.getNextScrollNo(), mainScrollNo, destAcc, session, amt, particulars,"CR",rrn);
				if(d40Dest==null)
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.OBJECT_CREATION_FAIL);
					return response;
				}
				try {
					session.save(d40);
					session.save(d40Dest);
	//				updating Batch Balance
					Query query = session.createQuery("UPDATE D010004 SET TotalDrVcrs=TotalDrVcrs+1, TotalDrAmtLcy = TotalDrAmtLcy +:amt , TotalCrVcrs = TotalCrVcrs+1, TotalCrAmtLcy= TotalCrAmtLcy + :amt  WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd");
					query.setParameter("amt", amt);
					query.setParameter("lbrcode", lbrCode);
					query.setParameter("entrydate", entryDate);
					query.setParameter("batchcd", onlineBatchName);
					query.executeUpdate();
					
					response.setBatchCode(onlineBatchName);
					response.setResponse(MSGConstants.SUCCESS);
					response.setErrorMsg(MSGConstants.SUCCESS_MSG);
					tx.commit();
					session.close();
					session=null;
					tx=null;
					return response;		
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					tx.rollback();
					session=null;
					tx=null;
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.EXCEPTION_OCCURED);
					return response;
				}
			}
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			if(tx != null) tx.rollback();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.EXCEPTION_OCCURED);
			return response;
		}		
	}
	
	public TransactionValidationResponse stopChequeVoucher(String prdAcctId, int lbrCode,String destAcc,int destBrCode, double amt,String particulars, String transType,int setNo,int scrollNo,int mainScrollNo,String rrn)
	{
		TransactionValidationResponse response=new TransactionValidationResponse();
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		try {
		/*	if(lbrCode!=destBrCode)
			{}else */
			if(lbrCode==destBrCode)
			{
				D001004 d001004 = getD001004(lbrCode); //Get entry Date
				String entryDateStr = d001004.getValue().trim();	
				Date entryDate = sdf1.parse(entryDateStr);
				String batchCodes[] = Props.getBatchProperty(transType).split("~");

				D001004 d04OnlineBatchName = getBatchNameFromBatchCode(batchCodes[0]);
				String onlineBatchName = d04OnlineBatchName.getValue().trim();

				D001004 d04OfflineBatchName = getBatchNameFromBatchCode(batchCodes[1]);
				String offlineBatchName = d04OfflineBatchName.getValue().trim();

				D010004 selectedBatch = null; 			
				D010004 onlineBatch = getD010004(lbrCode, onlineBatchName, entryDate);

				logger.error("Entry Date  : "+entryDate);
				System.out.println("Entry Date  : "+entryDate);
				logger.error("BatchCodes : "+batchCodes.length+"\t"+batchCodes[0]+"\t"+batchCodes[1]);
				System.out.println("BatchCodes : "+batchCodes.length+"\t"+batchCodes[0]+"\t"+batchCodes[1]);
				logger.error("Batch Name : "+onlineBatchName);
				System.out.println("Batch Name : "+onlineBatchName);
				logger.error("Batch Name : "+offlineBatchName);
				System.out.println("Batch Name : "+offlineBatchName);

			/*	if(null==d001004 )
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.SYSTEM_PARAMETER_NOT_FOUND);
					return response;
				}*/
				
				if(entryDateStr==null || entryDateStr.trim().length()<1)
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.OPERATION_DATE_NOT_FOUND);
					return response;
				}	
				if(entryDate==null)
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.ENTRY_DATE_UNABLE_TO_PARSE);
					return response;
				}
				if(batchCodes==null || batchCodes.length<1)
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.BATCH_CODE_PARAMETERS_NOT_SET);
					return response;
				}
				/*if(d04OnlineBatchName==null)
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.ONLINE_BATCH_NOT_FOUND);
					return response;
				}*/

				if(onlineBatch.getStat() == 1 || onlineBatch.getStat() == 2){
					logger.error("Online Batch Open ");
					selectedBatch = onlineBatch;
				}
				else{
					D010004 offlineBatch = getD010004(lbrCode, offlineBatchName, entryDate);
					if(offlineBatch.getStat() == 1 || offlineBatch.getStat() == 2){
						logger.error("Offline Batch Open ");
						selectedBatch = offlineBatch;
					}
					else{
						logger.error("Both Batches Closed");
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMsg(MSGConstants.ONLINE_BATCH_NOT_FOUND);
						return response;
					}
				}
				System.out.println("Source ACC::>>"+prdAcctId);
				System.out.println("Dest ACC::>>"+destAcc);
				
				logger.error("Selected batch : "+selectedBatch.getId().getBatchCd());
				D009040 d40=prepareD009040Object(selectedBatch, onlineBatch, lbrCode, setNo,VoucherMPOS.getNextScrollNo(), mainScrollNo, prdAcctId, session, amt, particulars,"DR",rrn);
				if(d40==null)
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.OBJECT_CREATION_FAIL);
					return response;
				}	
			
				D009040 d40Dest=prepareD009040Object(selectedBatch, onlineBatch, destBrCode, setNo,VoucherMPOS.getNextScrollNo(), mainScrollNo, destAcc, session, amt, particulars,"CR",rrn);
				if(d40Dest==null)
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.OBJECT_CREATION_FAIL);
					return response;
				}
				try {
					session.save(d40);
					session.save(d40Dest);
	//				updating Batch Balance
					Query query = session.createQuery("UPDATE D010004 SET TotalDrVcrs=TotalDrVcrs+1, TotalDrAmtLcy = TotalDrAmtLcy +:amt , TotalCrVcrs = TotalCrVcrs+1, TotalCrAmtLcy= TotalCrAmtLcy + :amt  WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd");
					query.setParameter("amt", amt);
					query.setParameter("lbrcode", lbrCode);
					query.setParameter("entrydate", entryDate);
					query.setParameter("batchcd", onlineBatchName);
					query.executeUpdate();
					
					response.setBatchCode(onlineBatchName);
					response.setResponse(MSGConstants.SUCCESS);
					response.setErrorMsg(MSGConstants.SUCCESS_MSG);
					tx.commit();
					session.close();
					session=null;
					tx=null;
					return response;		
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					tx.rollback();
					session=null;
					tx=null;
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.EXCEPTION_OCCURED);
					return response;
				}
			}
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			if(tx != null) tx.rollback();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.EXCEPTION_OCCURED);
			return response;
		}		
	}
	
	public TransactionValidationResponse creditMerchantOrBC(String prdAcctId, int lbrCode,String destAcc,int destBrCode, double amt,String particulars, String transType,int setNo,int scrollNo,int mainScrollNo,String rrn)
	{
		TransactionValidationResponse response=new TransactionValidationResponse();
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		try {
			if(lbrCode!=destBrCode)
			{
				D001004 d001004 = getD001004(lbrCode); //Get entry Date
				String entryDateStr = d001004.getValue().trim();	
				Date entryDate = sdf1.parse(entryDateStr);
				String batchCodes[] = Props.getBatchProperty(transType).split("~");

				D001004 d04OnlineBatchName = getBatchNameFromBatchCode(batchCodes[0]);
				String onlineBatchName = d04OnlineBatchName.getValue().trim();

				D001004 d04OfflineBatchName = getBatchNameFromBatchCode(batchCodes[1]);
				String offlineBatchName = d04OfflineBatchName.getValue().trim();

				D010004 selectedBatch = null; 			
				D010004 onlineBatch = getD010004(lbrCode, onlineBatchName, entryDate);

				logger.error("Entry Date  : "+entryDate);
				System.out.println("Entry Date  : "+entryDate);
				logger.error("BatchCodes : "+batchCodes.length+"\t"+batchCodes[0]+"\t"+batchCodes[1]);
				System.out.println("BatchCodes : "+batchCodes.length+"\t"+batchCodes[0]+"\t"+batchCodes[1]);
				logger.error("Batch Name : "+onlineBatchName);
				System.out.println("Batch Name : "+onlineBatchName);
				logger.error("Batch Name : "+offlineBatchName);
				System.out.println("Batch Name : "+offlineBatchName);

				if(null==d001004 )
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.SYSTEM_PARAMETER_NOT_FOUND);
					return response;
				}
				if(entryDateStr==null || entryDateStr.trim().length()<1)
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.OPERATION_DATE_NOT_FOUND);
					return response;
				}	
				if(entryDate==null)
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.ENTRY_DATE_UNABLE_TO_PARSE);
					return response;
				}
				if(batchCodes==null || batchCodes.length<1)
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.BATCH_CODE_PARAMETERS_NOT_SET);
					return response;
				}
				if(d04OnlineBatchName==null)
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.ONLINE_BATCH_NOT_FOUND);
					return response;
				}

				if(onlineBatch.getStat() == 1 || onlineBatch.getStat() == 2){
					logger.error("Online Batch Open ");
					selectedBatch = onlineBatch;
				}
				else{
					D010004 offlineBatch = getD010004(lbrCode, offlineBatchName, entryDate);
					if(offlineBatch.getStat() == 1 || offlineBatch.getStat() == 2){
						logger.error("Offline Batch Open ");
						selectedBatch = offlineBatch;
					}
					else{
						logger.error("Both Batches Closed");
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMsg(MSGConstants.ONLINE_BATCH_NOT_FOUND);
						return response;
					}
				}
				logger.error("Selected batch : "+selectedBatch.getId().getBatchCd());
				D009040 d40=prepareD009040Object(selectedBatch, onlineBatch, lbrCode, VoucherMPOS.getNextSetNo(),VoucherMPOS.getNextScrollNo(), VoucherMPOS.getNextScrollNo(), prdAcctId, session, amt, particulars,"CR",rrn);
				if(d40==null)
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.OBJECT_CREATION_FAIL);
					return response;
				}
			
				D009040 d40Dest=prepareD009040Object(selectedBatch, onlineBatch, destBrCode, VoucherMPOS.getNextSetNo(),VoucherMPOS.getNextScrollNo(), VoucherMPOS.getNextScrollNo(), destAcc, session, amt, particulars,"DR",rrn);
				if(d40Dest==null)
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.OBJECT_CREATION_FAIL);
					return response;
				}
				
				String batchCodesABB[] = Props.getBatchProperty(MSGConstants.ABB).split("~");

				D001004 d04OnlineABBBatchName = getBatchNameFromBatchCode(batchCodesABB[0]);
				String onlineABBBatchName = d04OnlineABBBatchName.getValue().trim();

				D010004 onlineABBBatch = getD010004(lbrCode, onlineABBBatchName, entryDate);
			
				D009040 d40ABB=prepareD009040Object(selectedBatch, onlineABBBatch, lbrCode, VoucherMPOS.getNextSetNo(),VoucherMPOS.getNextScrollNo(), mainScrollNo, MSGConstants.ABB_ACC, session, amt, particulars,"DR",rrn);
				D009040 d40DestABB=prepareD009040Object(selectedBatch, onlineABBBatch, destBrCode, VoucherMPOS.getNextSetNo(),VoucherMPOS.getNextScrollNo(), mainScrollNo,MSGConstants.ABB_ACC, session, amt, particulars,"CR",rrn);
				if(d40ABB==null || d40DestABB==null)
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.OBJECT_CREATION_FAIL);
					return response;
				}

				try {
					session.save(d40);
					session.save(d40ABB);
					session.save(d40DestABB);
					session.save(d40Dest);
					//				updating Batch Balance
					Query query = session.createQuery("UPDATE D010004 SET TotalDrVcrs=TotalDrVcrs+2, TotalDrAmtLcy = TotalDrAmtLcy +:amt , TotalCrVcrs = TotalCrVcrs+1, TotalCrAmtLcy= TotalCrAmtLcy + :amt  WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd");
					query.setParameter("amt", amt);
					query.setParameter("lbrcode", lbrCode);
					query.setParameter("entrydate", entryDate);
					query.setParameter("batchcd", onlineBatchName);
					query.executeUpdate();
					
					Query queryABB = session.createQuery("UPDATE D010004 SET TotalDrVcrs=TotalDrVcrs+2, TotalDrAmtLcy = TotalDrAmtLcy +:amt , TotalCrVcrs = TotalCrVcrs+1, TotalCrAmtLcy= TotalCrAmtLcy + :amt  WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd");
					queryABB.setParameter("amt", amt);
					queryABB.setParameter("lbrcode", lbrCode);
					queryABB.setParameter("entrydate", entryDate);
					queryABB.setParameter("batchcd", onlineABBBatchName);
					queryABB.executeUpdate();
					
					response.setBatchCode(onlineBatchName);
					response.setResponse(MSGConstants.SUCCESS);
					response.setErrorMsg(MSGConstants.SUCCESS_MSG);
					tx.commit();
					session.close();
					session=null;
					tx=null;
					return response;		
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					tx.rollback();
					session=null;
					tx=null;
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.EXCEPTION_OCCURED);
					return response;
				}
			}else if(lbrCode==destBrCode)
			{
				D001004 d001004 = getD001004(lbrCode); //Get entry Date
				String entryDateStr = d001004.getValue().trim();	
				Date entryDate = sdf1.parse(entryDateStr);
				String batchCodes[] = Props.getBatchProperty(transType).split("~");

				D001004 d04OnlineBatchName = getBatchNameFromBatchCode(batchCodes[0]);
				String onlineBatchName = d04OnlineBatchName.getValue().trim();

				D001004 d04OfflineBatchName = getBatchNameFromBatchCode(batchCodes[1]);
				String offlineBatchName = d04OfflineBatchName.getValue().trim();

				D010004 selectedBatch = null; 			
				D010004 onlineBatch = getD010004(lbrCode, onlineBatchName, entryDate);

				logger.error("Entry Date  : "+entryDate);
				System.out.println("Entry Date  : "+entryDate);
				logger.error("BatchCodes : "+batchCodes.length+"\t"+batchCodes[0]+"\t"+batchCodes[1]);
				System.out.println("BatchCodes : "+batchCodes.length+"\t"+batchCodes[0]+"\t"+batchCodes[1]);
				logger.error("Batch Name : "+onlineBatchName);
				System.out.println("Batch Name : "+onlineBatchName);
				logger.error("Batch Name : "+offlineBatchName);
				System.out.println("Batch Name : "+offlineBatchName);

				if(null==d001004 )
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.SYSTEM_PARAMETER_NOT_FOUND);
					return response;
				}
				if(entryDateStr==null || entryDateStr.trim().length()<1)
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.OPERATION_DATE_NOT_FOUND);
					return response;
				}	
				if(entryDate==null)
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.ENTRY_DATE_UNABLE_TO_PARSE);
					return response;
				}
				if(batchCodes==null || batchCodes.length<1)
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.BATCH_CODE_PARAMETERS_NOT_SET);
					return response;
				}
				if(d04OnlineBatchName==null)
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.ONLINE_BATCH_NOT_FOUND);
					return response;
				}

				if(onlineBatch.getStat() == 1 || onlineBatch.getStat() == 2){
					logger.error("Online Batch Open ");
					selectedBatch = onlineBatch;
				}
				else{
					D010004 offlineBatch = getD010004(lbrCode, offlineBatchName, entryDate);
					if(offlineBatch.getStat() == 1 || offlineBatch.getStat() == 2){
						logger.error("Offline Batch Open ");
						selectedBatch = offlineBatch;
					}
					else{
						logger.error("Both Batches Closed");
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMsg(MSGConstants.ONLINE_BATCH_NOT_FOUND);
						return response;
					}
				}
				System.out.println("Source ACC::>>"+prdAcctId);
				System.out.println("Dest ACC::>>"+destAcc);
				
				logger.error("Selected batch : "+selectedBatch.getId().getBatchCd());
				D009040 d40=prepareD009040Object(selectedBatch, onlineBatch, lbrCode, setNo,VoucherMPOS.getNextScrollNo(), mainScrollNo, prdAcctId, session, amt, particulars,"DR",rrn);
				if(d40==null)
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.OBJECT_CREATION_FAIL);
					return response;
				}
			
				D009040 d40Dest=prepareD009040Object(selectedBatch, onlineBatch, destBrCode, setNo,VoucherMPOS.getNextScrollNo(), mainScrollNo, destAcc, session, amt, particulars,"CR",rrn);
				if(d40Dest==null)
				{
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.OBJECT_CREATION_FAIL);
					return response;
				}
				try {
					session.save(d40);
					session.save(d40Dest);
	//				updating Batch Balance
					Query query = session.createQuery("UPDATE D010004 SET TotalDrVcrs=TotalDrVcrs+2, TotalDrAmtLcy = TotalDrAmtLcy +:amt , TotalCrVcrs = TotalCrVcrs+1, TotalCrAmtLcy= TotalCrAmtLcy + :amt  WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd");
					query.setParameter("amt", amt);
					query.setParameter("lbrcode", lbrCode);
					query.setParameter("entrydate", entryDate);
					query.setParameter("batchcd", onlineBatchName);
					query.executeUpdate();
					
					response.setBatchCode(onlineBatchName);
					response.setResponse(MSGConstants.SUCCESS);
					response.setErrorMsg(MSGConstants.SUCCESS_MSG);
					tx.commit();
					session.close();
					session=null;
					tx=null;
					return response;		
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					tx.rollback();
					session=null;
					tx=null;
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.EXCEPTION_OCCURED);
					return response;
				}
			
				
			}
			
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			if(tx != null) tx.rollback();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.EXCEPTION_OCCURED);
			return response;
		}		
	}
	public boolean debitBcOrMerchant(String prdAcctId, int lbrCode, double amt, String actType, String transType, String instrNo, String particulars, long setNo, long scrollNo){

		//		txDebit = sessionDebit.beginTransaction();		

		this.remLbrCode = lbrCode;
		this.remAcctId = prdAcctId;

		Session sessionDebit1 = HBUtil.getSessionFactory().openSession();
		Transaction txDebit1 = sessionDebit1.beginTransaction();

		try {

			// Get Last Operation Date
			D001004 d001004 = getD001004(lbrCode); //Get entry Date
			String entryDateStr = d001004.getValue().trim();
			Date entryDate = sdf1.parse(entryDateStr);
			remDate = entryDate;	//for Recon
			remTime = new Date();	//for Recon
			logger.error("Entry Date  : "+entryDate);
			// Get Operation Date End


			//Get Batch Name Form Batch Code
			logger.error("Property : "+Props.getBatchProperty(transType));
			String batchCodes[] = Props.getBatchProperty(transType).split("~");
			logger.error("BatchCodes : "+batchCodes.length+"\t"+batchCodes[0]+"\t"+batchCodes[1]);
			D001004 d04OnlineBatchName = getBatchNameFromBatchCode(batchCodes[0]);
			String onlineBatchName = d04OnlineBatchName.getValue().trim();
			logger.error("Batch Name : "+onlineBatchName);
			D001004 d04OfflineBatchName = getBatchNameFromBatchCode(batchCodes[1]);
			String offlineBatchName = d04OfflineBatchName.getValue().trim();
			logger.error("Batch Name : "+offlineBatchName);			
			//Get Batch Name Form Batch Code End

			D010004 selectedBatch = null; 			

			//Get Batch From Batch Name
			D010004 onlineBatch = getD010004(lbrCode, onlineBatchName, entryDate);
			if(onlineBatch.getStat() == 1 || onlineBatch.getStat() == 2){
				logger.error("Online Batch Open ");
				selectedBatch = onlineBatch;
			}
			else{
				D010004 offlineBatch = getD010004(lbrCode, offlineBatchName, entryDate);
				if(offlineBatch.getStat() == 1 || offlineBatch.getStat() == 2){
					logger.error("Offline Batch Open ");
					selectedBatch = offlineBatch;
				}
				else{
					logger.error("Both Batches Closed");
				}
			}
			logger.error("Selected batch : "+selectedBatch.getId().getBatchCd());

			D009040 d40 = new D009040();
			D009040Id id40 = new D009040Id();
			id40.setBatchCd(selectedBatch.getId().getBatchCd()); //SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016'

			remBatchCode = selectedBatch.getId().getBatchCd();
			remSetNo = (int) setNo;
			remMainScrollNo = (int) scrollNo;


			id40.setEntryDate(selectedBatch.getId().getEntryDate()); // SELECT Value FROM D001004 WHERE LBrCode = 9 AND Code = 'LASTOPENDATE'
			id40.setLbrCode(lbrCode); 
			id40.setSetNo((int) setNo);
			id40.setScrollNo((int) scrollNo);


			d40.setId(id40);
			d40.setMainScrollNo((int) scrollNo); //Scroll No
			d40.setPostDate(onlineBatch.getPostDate()); //SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
			d40.setFeffDate(onlineBatch.getFeffDate()); // SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
			d40.setActivityType("DR"); 
			d40.setCashFlowType("DR");
			d40.setValueDate(onlineBatch.getPostDate()); // postdate SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'

			logger.error("Calling D010001");
			D010001 d001 = getD001(lbrCode, selectedBatch.getId().getBatchCd());
			String booktype="ZZ";
			if(d001==null)
				 booktype="00";
			else
				booktype=d001.getBookType();
			d40.setBookType(booktype); //SELECT BookType FROM D010001 WHERE LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('D');
			d40.setVcrAcctId(prdAcctId);
			d40.setMainAcctId(prdAcctId);

			D009021 d009021 = getD009021(lbrCode, prdAcctId.substring(0,8).trim());

			d40.setMainModType(d009021.getModuleType()); // SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB      
			d40.setVcrModType(d009021.getModuleType()); // SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB      
			d40.setTrnCurCd("INR");
			d40.setFcyTrnAmt(amt);
			d40.setLcyConvRate(1);
			d40.setLcyTrnAmt(amt);
			d40.setInstrBankCd((short) 0);
			d40.setInstrBranchCd((short) 0);
			d40.setInstrType((short) 99); // Depend on chanel
			d40.setInstrNo(instrNo);	// RRN
			d40.setInstrDate(new Date()); //Blank
			d40.setParticulars(particulars); //param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); // 
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');

			D002001 d002001 = getD002001("WEB");
			usrCode = d002001.getUsrCode2();
			d40.setMaker(d002001.getUsrCode2()); // SELECT UsrCode2 FROM D002001 WHERE UsrCode1 = 'WEB' 
			d40.setMakerDate(selectedBatch.getId().getEntryDate()); // EntryDate 
			d40.setMakerTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8))); 
			d40.setChecker1(d002001.getUsrCode2()); // SELECT UsrCode2 FROM D002001 WHERE UsrCode1 = 'WEB' 
			d40.setChecker2(0);
			d40.setChecker3(0);
			d40.setChecker4(0);
			d40.setCheckerDate(selectedBatch.getId().getEntryDate()); // EntryDate
			d40.setCheckerTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8)));

			d40.setNoAuthPending((byte) 0); // 
			d40.setNoAuthOver((byte) 1); 
			d40.setPostFlag('P'); 
			d40.setAuthFlag('A'); 
			d40.setFeffFlag('F');
			d40.setCanceledFlag(' '); 
			d40.setPostAuthFeffCncl((byte) 0); 
			d40.setUpdtChkId((short) 0); 
			d40.setPartClearAmt(0); 
			d40.setPostTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8))); // Time HHMMSSTT
			try {
				sessionDebit1.save(d40);
				txDebit1.commit();
				//				sessionDebit1.flush();	
				sessionDebit1.close();
				return true;		
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			//if(tx != null) tx.rollback();
			return false;
		}		


	}

	public boolean debitABB(String prdAcctId, int lbrCode, double amt, String actType, String transType, String instrNo, String particulars, long setNo, long scrollNo){

		//txDebit = sessionDebit.beginTransaction();		

		try {

			// Get Last Operation Date
			D001004 d001004 = getD001004(lbrCode); //Get entry Date
			String entryDateStr = d001004.getValue().trim();
			Date entryDate = sdf1.parse(entryDateStr);
			logger.error("Entry Date  : "+entryDate);
			// Get Operation Date End

			//Get Batch Name Form Batch Code
			logger.error("Property : "+Props.getBatchProperty(transType));
			String batchCodes[] =  Props.getBatchProperty(transType).split("~");
			logger.error("BatchCodes : "+batchCodes.length+"\t"+batchCodes[0]+"\t"+batchCodes[1]);
			D001004 d04OnlineBatchName = getBatchNameFromBatchCode(batchCodes[0]);
			String onlineBatchName = d04OnlineBatchName.getValue().trim();
			logger.error("Batch Name : "+onlineBatchName);
			D001004 d04OfflineBatchName = getBatchNameFromBatchCode(batchCodes[1]);
			String offlineBatchName = d04OfflineBatchName.getValue().trim();
			logger.error("Batch Name : "+offlineBatchName);			
			//Get Batch Name Form Batch Code End

			D010004 selectedBatch = null; 			

			//Get Batch From Batch Name
			D010004 onlineBatch = getD010004(lbrCode, onlineBatchName, entryDate);
			if(onlineBatch.getStat() == 1 || onlineBatch.getStat() == 2){
				logger.error("Online Batch Open ");
				selectedBatch = onlineBatch;
			}
			else{
				D010004 offlineBatch = getD010004(lbrCode, offlineBatchName, entryDate);
				if(offlineBatch.getStat() == 1 || offlineBatch.getStat() == 2){
					logger.error("Offline Batch Open ");
					selectedBatch = offlineBatch;
				}
				else{
					logger.error("Both Batches Closed");
				}
			}

			logger.error("Selected batch : "+selectedBatch.getId().getBatchCd());

			D009040 d40 = new D009040();
			D009040Id id40 = new D009040Id();
			id40.setBatchCd(selectedBatch.getId().getBatchCd()); //SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016'

			id40.setEntryDate(selectedBatch.getId().getEntryDate()); // SELECT Value FROM D001004 WHERE LBrCode = 9 AND Code = 'LASTOPENDATE'
			id40.setLbrCode(lbrCode); 
			id40.setSetNo((int) setNo);
			id40.setScrollNo((int) scrollNo);


			d40.setId(id40);
			d40.setMainScrollNo((int) scrollNo); //Scroll No
			d40.setPostDate(onlineBatch.getPostDate()); //SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
			d40.setFeffDate(onlineBatch.getFeffDate()); // SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
			d40.setActivityType("ABBREM"); 
			d40.setCashFlowType("ABBDR");
			d40.setValueDate(onlineBatch.getPostDate()); // postdate SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'

			logger.error("Calling D010001");
			D010001 d001 = getD001(lbrCode, selectedBatch.getId().getBatchCd());

			d40.setBookType(d001.getBookType()); //SELECT BookType FROM D010001 WHERE LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('D');
			d40.setVcrAcctId(prdAcctId);
			d40.setMainAcctId(prdAcctId);

			D009021 d009021 = getD009021(lbrCode, prdAcctId.substring(0,8).trim());

			d40.setMainModType(d009021.getModuleType()); // SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB      
			d40.setVcrModType(d009021.getModuleType()); // SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB      
			d40.setTrnCurCd("INR");
			d40.setFcyTrnAmt(amt);
			d40.setLcyConvRate(1);
			d40.setLcyTrnAmt(amt);
			d40.setInstrBankCd((short) 0);
			d40.setInstrBranchCd((short) 0);
			d40.setInstrType((short) 99); // Depend on chanel
			d40.setInstrNo(instrNo);	// RRN
			d40.setInstrDate(new Date()); //Blank
			d40.setParticulars(particulars); //param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); // 
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');

			D002001 d002001 = getD002001("WEB");

			d40.setMaker(d002001.getUsrCode2()); // SELECT UsrCode2 FROM D002001 WHERE UsrCode1 = 'WEB' 
			d40.setMakerDate(selectedBatch.getId().getEntryDate()); // EntryDate 
			d40.setMakerTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8))); 
			d40.setChecker1(d002001.getUsrCode2()); // SELECT UsrCode2 FROM D002001 WHERE UsrCode1 = 'WEB' 
			d40.setChecker2(0);
			d40.setChecker3(0);
			d40.setChecker4(0);
			d40.setCheckerDate(selectedBatch.getId().getEntryDate()); // EntryDate
			d40.setCheckerTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8)));

			d40.setNoAuthPending((byte) 0); // 
			d40.setNoAuthOver((byte) 1); 
			d40.setPostFlag('P'); 
			d40.setAuthFlag('A'); 
			d40.setFeffFlag('F'); 
			d40.setCanceledFlag(' '); 
			d40.setPostAuthFeffCncl((byte) 0); 
			d40.setUpdtChkId((short) 0); 
			d40.setPartClearAmt(0); 
			d40.setPostTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8))); // Time HHMMSSTT

			drABB = d40;
			sessionDebit.save(d40);
			sessionDebit.flush();
		} catch (Exception e) {
			e.printStackTrace();
			//if(tx != null) tx.rollback();
			return false;
		}		

		return true;		

	}

	public boolean creditABB(String prdAcctId, int slbrCode,int tlbrCode,  double amt, String actType, String transType, String instrNo, String particulars, long setNo, long scrollNo){
		//txCredit = sessionCredit.beginTransaction();
		try {
			// Get Last Operation Date
			D001004 d001004 = getD001004(slbrCode); //Get entry Date
			String entryDateStr = d001004.getValue().trim();
			Date entryDate = sdf1.parse(entryDateStr);
			logger.error("Entry Date  : "+entryDate);
			// Get Operation Date End

			//Get Batch Name Form Batch Code
			logger.error("Property : "+Props.getBatchProperty(transType));
			String batchCodes[] =  Props.getBatchProperty(transType).split("~");
			logger.error("BatchCodes : "+batchCodes.length+"\t"+batchCodes[0]+"\t"+batchCodes[1]);
			D001004 d04OnlineBatchName = getBatchNameFromBatchCode(batchCodes[0]);
			String onlineBatchName = d04OnlineBatchName.getValue().trim();
			logger.error("Batch Name : "+onlineBatchName);
			D001004 d04OfflineBatchName = getBatchNameFromBatchCode(batchCodes[1]);
			String offlineBatchName = d04OfflineBatchName.getValue().trim();
			logger.error("Batch Name : "+offlineBatchName);			
			//Get Batch Name Form Batch Code End

			D010004 selectedBatch = null; 			

			//Get Batch From Batch Name
			D010004 onlineBatch = getD010004(slbrCode, onlineBatchName, entryDate);
			if(onlineBatch.getStat() == 1 || onlineBatch.getStat() == 2){
				logger.error("Online Batch Open ");
				selectedBatch = onlineBatch;
			}
			else{
				D010004 offlineBatch = getD010004(slbrCode, offlineBatchName, entryDate);
				if(offlineBatch.getStat() == 1 || offlineBatch.getStat() == 2){
					logger.error("Offline Batch Open ");
					selectedBatch = offlineBatch;
				}
				else{
					logger.error("Both Batches Closed");
				}
			}

			logger.error("Selected batch : "+selectedBatch.getId().getBatchCd());

			D009040 d40 = new D009040();
			D009040Id id40 = new D009040Id();
			id40.setBatchCd(selectedBatch.getId().getBatchCd()); //SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016'

			logger.error("");

			id40.setEntryDate(selectedBatch.getId().getEntryDate()); // SELECT Value FROM D001004 WHERE LBrCode = 9 AND Code = 'LASTOPENDATE'
			id40.setLbrCode(slbrCode); 
			id40.setSetNo((int) setNo);
			id40.setScrollNo((int) scrollNo);


			d40.setId(id40);
			d40.setMainScrollNo((int)scrollNo); //Scroll No
			d40.setPostDate(onlineBatch.getPostDate()); //SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
			d40.setFeffDate(onlineBatch.getFeffDate()); // SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
			d40.setActivityType("ABB"); 
			d40.setCashFlowType("ABBCR");
			d40.setValueDate(onlineBatch.getPostDate()); // postdate SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'

			logger.error("Calling D010001");
			D010001 d001 = getD001(slbrCode, selectedBatch.getId().getBatchCd());

			logger.error("BookType : "+d001.getBookType());

			d40.setBookType(d001.getBookType()); //SELECT BookType FROM D010001 WHERE LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('C');
			d40.setVcrAcctId(prdAcctId);
			d40.setMainAcctId(prdAcctId);

			D009021 d009021 = getD009021(slbrCode, prdAcctId.substring(0,8).trim());

			logger.error("D21 : "+d009021);

			d40.setMainModType(d009021.getModuleType()); // SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB      
			d40.setVcrModType(d009021.getModuleType()); // SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB      
			d40.setTrnCurCd("INR");
			d40.setFcyTrnAmt(amt);
			d40.setLcyConvRate(1);
			d40.setLcyTrnAmt(amt);
			d40.setInstrBankCd((short) slbrCode);
			d40.setInstrBranchCd((short) tlbrCode);
			d40.setInstrType((short) 99); // Depend on chanel
			d40.setInstrNo(instrNo);	// RRN
			d40.setInstrDate(new Date()); //Blank
			d40.setParticulars(particulars); //param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); // 
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');

			D002001 d002001 = getD002001("WEB");

			logger.error("D002001 : "+d002001);

			d40.setMaker(d002001.getUsrCode2()); // SELECT UsrCode2 FROM D002001 WHERE UsrCode1 = 'WEB' 
			d40.setMakerDate(selectedBatch.getId().getEntryDate()); // EntryDate 
			d40.setMakerTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8))); 
			d40.setChecker1(d002001.getUsrCode2()); // SELECT UsrCode2 FROM D002001 WHERE UsrCode1 = 'WEB' 
			d40.setChecker2(0);
			d40.setChecker3(0);
			d40.setChecker4(0);
			d40.setCheckerDate(selectedBatch.getId().getEntryDate()); // EntryDate
			d40.setCheckerTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8)));

			logger.error("In 1");

			d40.setNoAuthPending((byte) 0); // 
			d40.setNoAuthOver((byte) 1); 
			d40.setPostFlag('P'); 
			d40.setAuthFlag('A'); 
			d40.setFeffFlag(' '); 
			d40.setCanceledFlag(' '); 
			d40.setPostAuthFeffCncl((byte) 0); 
			d40.setUpdtChkId((short) 0); 
			d40.setPartClearAmt(0);
			d40.setPostTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8))); // Time HHMMSSTT


			logger.error("In 2");

			crABB = d40;
			sessionDebit.save(d40);
			sessionDebit.flush();


			logger.error("In 3");
		} catch (Exception e) {
			e.printStackTrace();

			logger.error("Exeption in Credit");
			//if(tx != null) tx.rollback();
			return false;
		}		

		return true;		
	}



	public boolean credit(String prdAcctId, int lbrCode, double amt, String actType, String transType, String instrNo, String particulars, long setNo, long scrollNo){

		benAcctId = prdAcctId;
		benLbrCode = lbrCode;


		//txCredit = sessionCredit.beginTransaction();
		try {

			// Get Last Operation Date
			D001004 d001004 = getD001004(lbrCode); //Get entry Date
			String entryDateStr = d001004.getValue().trim();
			Date entryDate = sdf1.parse(entryDateStr);
			benDate = entryDate;
			logger.error("Entry Date  : "+entryDate);
			// Get Operation Date End


			//Get Batch Name Form Batch Code
			logger.error("Property : "+Props.getBatchProperty(transType));
			String batchCodes[] =  Props.getBatchProperty(transType).split("~");
			logger.error("BatchCodes : "+batchCodes.length+"\t"+batchCodes[0]+"\t"+batchCodes[1]);
			D001004 d04OnlineBatchName = getBatchNameFromBatchCode(batchCodes[0]);
			String onlineBatchName = d04OnlineBatchName.getValue().trim();
			logger.error("Batch Name : "+onlineBatchName);
			D001004 d04OfflineBatchName = getBatchNameFromBatchCode(batchCodes[1]);
			String offlineBatchName = d04OfflineBatchName.getValue().trim();
			logger.error("Batch Name : "+offlineBatchName);			
			//Get Batch Name Form Batch Code End

			D010004 selectedBatch = null; 			

			//Get Batch From Batch Name
			D010004 onlineBatch = getD010004(lbrCode, onlineBatchName, entryDate);
			if(onlineBatch.getStat() == 1 || onlineBatch.getStat() == 2){
				logger.error("Online Batch Open ");
				selectedBatch = onlineBatch;
			}
			else{
				D010004 offlineBatch = getD010004(lbrCode, offlineBatchName, entryDate);
				if(offlineBatch.getStat() == 1 || offlineBatch.getStat() == 2){
					logger.error("Offline Batch Open ");
					selectedBatch = offlineBatch;
				}
				else{
					logger.error("Both Batches Closed");
				}
			}

			logger.error("Selected batch : "+selectedBatch.getId().getBatchCd());

			D009040 d40 = new D009040();
			D009040Id id40 = new D009040Id();
			id40.setBatchCd(selectedBatch.getId().getBatchCd()); //SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016'

			benBatchCode = selectedBatch.getId().getBatchCd();
			benSetNo = (int) setNo;
			benMainScrollNo = (int) scrollNo;
			toBramt = amt;

			id40.setEntryDate(selectedBatch.getId().getEntryDate()); // SELECT Value FROM D001004 WHERE LBrCode = 9 AND Code = 'LASTOPENDATE'
			id40.setLbrCode(lbrCode); 
			id40.setSetNo((int) setNo);
			id40.setScrollNo((int) scrollNo);


			d40.setId(id40);
			d40.setMainScrollNo((int)scrollNo); //Scroll No
			d40.setPostDate(onlineBatch.getPostDate()); //SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
			d40.setFeffDate(onlineBatch.getFeffDate()); // SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
			d40.setActivityType("CR"); 
			d40.setCashFlowType("CR");
			d40.setValueDate(onlineBatch.getPostDate()); // postdate SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'

			logger.error("Calling D010001");
			D010001 d001 = getD001(lbrCode, selectedBatch.getId().getBatchCd());

//			logger.error("BookType : "+d001.getBookType());
			String bookType="";
			if(d001==null)
				bookType="ZZ";
			else
				bookType=d001.getBookType();
			d40.setBookType(bookType); //SELECT BookType FROM D010001 WHERE LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('C');
			d40.setVcrAcctId(prdAcctId);
			d40.setMainAcctId(prdAcctId);

			D009021 d009021 = getD009021(lbrCode, prdAcctId.substring(0,8).trim());

			logger.error("D21 : "+d009021);

			d40.setMainModType(d009021.getModuleType()); // SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB      
			d40.setVcrModType(d009021.getModuleType()); // SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB      
			d40.setTrnCurCd("INR");
			d40.setFcyTrnAmt(amt);
			d40.setLcyConvRate(1);
			d40.setLcyTrnAmt(amt);
			d40.setInstrBankCd((short) 0);
			d40.setInstrBranchCd((short) 0);
			d40.setInstrType((short) 99); // Depend on chanel
			d40.setInstrNo(instrNo);	// RRN
			d40.setInstrDate(new Date()); //Blank
			d40.setParticulars(particulars); //param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); // 
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');

			D002001 d002001 = getD002001("WEB");

			logger.error("D002001 : "+d002001);

			d40.setMaker(d002001.getUsrCode2()); // SELECT UsrCode2 FROM D002001 WHERE UsrCode1 = 'WEB' 
			d40.setMakerDate(selectedBatch.getId().getEntryDate()); // EntryDate 
			d40.setMakerTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8))); 
			d40.setChecker1(d002001.getUsrCode2()); // SELECT UsrCode2 FROM D002001 WHERE UsrCode1 = 'WEB' 
			d40.setChecker2(0);
			d40.setChecker3(0);
			d40.setChecker4(0);
			d40.setCheckerDate(selectedBatch.getId().getEntryDate()); // EntryDate
			d40.setCheckerTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8)));

			logger.error("In 1");

			d40.setNoAuthPending((byte) 0); 
			d40.setNoAuthOver((byte) 1); 
			d40.setPostFlag('P'); 
			d40.setAuthFlag('A'); 
			d40.setFeffFlag(' '); 
			d40.setCanceledFlag(' '); 
			d40.setPostAuthFeffCncl((byte) 0); 
			d40.setUpdtChkId((short) 0); 
			d40.setPartClearAmt(0); 
			d40.setPostTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8))); // Time HHMMSSTT


			logger.error("In 2");

			Session sessionDebit1=HBUtil.getSessionFactory().openSession();
			Transaction txDebit1=sessionDebit1.beginTransaction();		
			try {
				sessionDebit1.save(d40);
				txDebit1.commit();
				//				sessionDebit1.flush();	
				sessionDebit1.close();
				return true;		
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();

			logger.error("Exeption in Credit");
			//if(tx != null) tx.rollback();
			return false;
		}		

	}
	
	public boolean creditBCOrMC(String prdAcctId, int lbrCode, double amt, String actType, String transType, String instrNo, String particulars, long setNo, long scrollNo){

		benAcctId = prdAcctId;
		benLbrCode = lbrCode;


		//txCredit = sessionCredit.beginTransaction();
		try {

			// Get Last Operation Date
			D001004 d001004 = getD001004(lbrCode); //Get entry Date
			String entryDateStr = d001004.getValue().trim();
			Date entryDate = sdf1.parse(entryDateStr);
			benDate = entryDate;
			logger.error("Entry Date  : "+entryDate);
			// Get Operation Date End


			//Get Batch Name Form Batch Code
			logger.error("Property : "+Props.getBatchProperty(transType));
			String batchCodes[] =  Props.getBatchProperty(transType).split("~");
			logger.error("BatchCodes : "+batchCodes.length+"\t"+batchCodes[0]+"\t"+batchCodes[1]);
			D001004 d04OnlineBatchName = getBatchNameFromBatchCode(batchCodes[0]);
			String onlineBatchName = d04OnlineBatchName.getValue().trim();
			logger.error("Batch Name : "+onlineBatchName);
			D001004 d04OfflineBatchName = getBatchNameFromBatchCode(batchCodes[1]);
			String offlineBatchName = d04OfflineBatchName.getValue().trim();
			logger.error("Batch Name : "+offlineBatchName);			
			//Get Batch Name Form Batch Code End

			D010004 selectedBatch = null; 			

			//Get Batch From Batch Name
			D010004 onlineBatch = getD010004(lbrCode, onlineBatchName, entryDate);
			if(onlineBatch.getStat() == 1 || onlineBatch.getStat() == 2){
				logger.error("Online Batch Open ");
				selectedBatch = onlineBatch;
			}
			else{
				D010004 offlineBatch = getD010004(lbrCode, offlineBatchName, entryDate);
				if(offlineBatch.getStat() == 1 || offlineBatch.getStat() == 2){
					logger.error("Offline Batch Open ");
					selectedBatch = offlineBatch;
				}
				else{
					logger.error("Both Batches Closed");
				}
			}

			logger.error("Selected batch : "+selectedBatch.getId().getBatchCd());

			D009040 d40 = new D009040();
			D009040Id id40 = new D009040Id();
			id40.setBatchCd(selectedBatch.getId().getBatchCd()); //SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016'

			benBatchCode = selectedBatch.getId().getBatchCd();
			benSetNo = (int) setNo;
			benMainScrollNo = (int) scrollNo;
			toBramt = amt;

			id40.setEntryDate(selectedBatch.getId().getEntryDate()); // SELECT Value FROM D001004 WHERE LBrCode = 9 AND Code = 'LASTOPENDATE'
			id40.setLbrCode(lbrCode); 
			id40.setSetNo((int) setNo);
			id40.setScrollNo((int) scrollNo);


			d40.setId(id40);
			d40.setMainScrollNo((int)scrollNo); //Scroll No
			d40.setPostDate(onlineBatch.getPostDate()); //SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
			d40.setFeffDate(onlineBatch.getFeffDate()); // SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
			d40.setActivityType("CR"); 
			d40.setCashFlowType("CR");
			d40.setValueDate(onlineBatch.getPostDate()); // postdate SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'

			logger.error("Calling D010001");
			D010001 d001 = getD001(lbrCode, selectedBatch.getId().getBatchCd());

//			logger.error("BookType : "+d001.getBookType());
			String bookType="";
			if(d001==null)
				bookType="ZZ";
			else
				bookType=d001.getBookType();
			d40.setBookType(bookType); //SELECT BookType FROM D010001 WHERE LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('C');
			d40.setVcrAcctId(prdAcctId);
			d40.setMainAcctId(prdAcctId);

			D009021 d009021 = getD009021(lbrCode, prdAcctId.substring(0,8).trim());

			logger.error("D21 : "+d009021);

			d40.setMainModType(d009021.getModuleType()); // SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB      
			d40.setVcrModType(d009021.getModuleType()); // SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB      
			d40.setTrnCurCd("INR");
			d40.setFcyTrnAmt(amt);
			d40.setLcyConvRate(1);
			d40.setLcyTrnAmt(amt);
			d40.setInstrBankCd((short) 0);
			d40.setInstrBranchCd((short) 0);
			d40.setInstrType((short) 99); // Depend on chanel
			d40.setInstrNo(instrNo);	// RRN
			d40.setInstrDate(new Date()); //Blank
			d40.setParticulars(particulars); //param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); // 
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');

			D002001 d002001 = getD002001("WEB");

			logger.error("D002001 : "+d002001);

			d40.setMaker(d002001.getUsrCode2()); // SELECT UsrCode2 FROM D002001 WHERE UsrCode1 = 'WEB' 
			d40.setMakerDate(selectedBatch.getId().getEntryDate()); // EntryDate 
			d40.setMakerTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8))); 
			d40.setChecker1(d002001.getUsrCode2()); // SELECT UsrCode2 FROM D002001 WHERE UsrCode1 = 'WEB' 
			d40.setChecker2(0);
			d40.setChecker3(0);
			d40.setChecker4(0);
			d40.setCheckerDate(selectedBatch.getId().getEntryDate()); // EntryDate
			d40.setCheckerTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8)));

			logger.error("In 1");

			d40.setNoAuthPending((byte) 0); 
			d40.setNoAuthOver((byte) 1); 
			d40.setPostFlag('P'); 
			d40.setAuthFlag('A'); 
			d40.setFeffFlag('F'); 
			d40.setCanceledFlag(' '); 
			d40.setPostAuthFeffCncl((byte) 0); 
			d40.setUpdtChkId((short) 0); 
			d40.setPartClearAmt(0); 
			d40.setPostTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8))); // Time HHMMSSTT


			logger.error("In 2");

			Session sessionDebit1=HBUtil.getSessionFactory().openSession();
			Transaction txDebit1=sessionDebit1.beginTransaction();		
			try {
				sessionDebit1.save(d40);
				txDebit1.commit();
				//				sessionDebit1.flush();	
				sessionDebit1.close();
				return true;		
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();

			logger.error("Exeption in Credit");
			//if(tx != null) tx.rollback();
			return false;
		}		

	}
	public boolean creditBcOrMerchant(String prdAcctId, int lbrCode, double amt, String actType, String transType, String instrNo, String particulars, long setNo, long scrollNo){

		benAcctId = prdAcctId;
		benLbrCode = lbrCode;


		//txCredit = sessionCredit.beginTransaction();
		try {

			// Get Last Operation Date
			D001004 d001004 = getD001004(lbrCode); //Get entry Date
			String entryDateStr = d001004.getValue().trim();
			Date entryDate = sdf1.parse(entryDateStr);
			benDate = entryDate;
			logger.error("Entry Date  : "+entryDate);
			// Get Operation Date End


			//Get Batch Name Form Batch Code
			logger.error("Property : "+Props.getBatchProperty(transType));
			String batchCodes[] =  Props.getBatchProperty(transType).split("~");
			logger.error("BatchCodes : "+batchCodes.length+"\t"+batchCodes[0]+"\t"+batchCodes[1]);
			D001004 d04OnlineBatchName = getBatchNameFromBatchCode(batchCodes[0]);
			String onlineBatchName = d04OnlineBatchName.getValue().trim();
			logger.error("Batch Name : "+onlineBatchName);
			D001004 d04OfflineBatchName = getBatchNameFromBatchCode(batchCodes[1]);
			String offlineBatchName = d04OfflineBatchName.getValue().trim();
			logger.error("Batch Name : "+offlineBatchName);			
			//Get Batch Name Form Batch Code End

			D010004 selectedBatch = null; 			

			//Get Batch From Batch Name
			D010004 onlineBatch = getD010004(lbrCode, onlineBatchName, entryDate);
			if(onlineBatch.getStat() == 1 || onlineBatch.getStat() == 2){
				logger.error("Online Batch Open ");
				selectedBatch = onlineBatch;
			}
			else{
				D010004 offlineBatch = getD010004(lbrCode, offlineBatchName, entryDate);
				if(offlineBatch.getStat() == 1 || offlineBatch.getStat() == 2){
					logger.error("Offline Batch Open ");
					selectedBatch = offlineBatch;
				}
				else{
					logger.error("Both Batches Closed");
				}
			}

			logger.error("Selected batch : "+selectedBatch.getId().getBatchCd());

			D009040 d40 = new D009040();
			D009040Id id40 = new D009040Id();
			id40.setBatchCd(selectedBatch.getId().getBatchCd()); //SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016'

			benBatchCode = selectedBatch.getId().getBatchCd();
			benSetNo = (int) setNo;
			benMainScrollNo = (int) scrollNo;
			toBramt = amt;

			id40.setEntryDate(selectedBatch.getId().getEntryDate()); // SELECT Value FROM D001004 WHERE LBrCode = 9 AND Code = 'LASTOPENDATE'
			id40.setLbrCode(lbrCode); 
			id40.setSetNo((int) setNo);
			id40.setScrollNo((int) scrollNo);


			d40.setId(id40);
			d40.setMainScrollNo((int)scrollNo); //Scroll No
			d40.setPostDate(onlineBatch.getPostDate()); //SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
			d40.setFeffDate(onlineBatch.getFeffDate()); // SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
			d40.setActivityType("CR"); 
			d40.setCashFlowType("CR");
			d40.setValueDate(onlineBatch.getPostDate()); // postdate SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'

			logger.error("Calling D010001");
			D010001 d001 = getD001(lbrCode, selectedBatch.getId().getBatchCd());

//			logger.error("BookType : "+d001.getBookType());
			String bookType="";
			if(d001==null)
				bookType="ZZ";
			else
				bookType=d001.getBookType();
			d40.setBookType(bookType); //SELECT BookType FROM D010001 WHERE LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('C');
			d40.setVcrAcctId(prdAcctId);
			d40.setMainAcctId(prdAcctId);

			D009021 d009021 = getD009021(lbrCode, prdAcctId.substring(0,8).trim());

			logger.error("D21 : "+d009021);

			d40.setMainModType(d009021.getModuleType()); // SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB      
			d40.setVcrModType(d009021.getModuleType()); // SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB      
			d40.setTrnCurCd("INR");
			d40.setFcyTrnAmt(amt);
			d40.setLcyConvRate(1);
			d40.setLcyTrnAmt(amt);
			d40.setInstrBankCd((short) 0);
			d40.setInstrBranchCd((short) 0);
			d40.setInstrType((short) 99); // Depend on chanel
			d40.setInstrNo(instrNo);	// RRN
			d40.setInstrDate(new Date()); //Blank
			d40.setParticulars(particulars); //param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); // 
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');

			D002001 d002001 = getD002001("WEB");

			logger.error("D002001 : "+d002001);

			d40.setMaker(d002001.getUsrCode2()); // SELECT UsrCode2 FROM D002001 WHERE UsrCode1 = 'WEB' 
			d40.setMakerDate(selectedBatch.getId().getEntryDate()); // EntryDate 
			d40.setMakerTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8))); 
			d40.setChecker1(d002001.getUsrCode2()); // SELECT UsrCode2 FROM D002001 WHERE UsrCode1 = 'WEB' 
			d40.setChecker2(0);
			d40.setChecker3(0);
			d40.setChecker4(0);
			d40.setCheckerDate(selectedBatch.getId().getEntryDate()); // EntryDate
			d40.setCheckerTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8)));

			logger.error("In 1");

			d40.setNoAuthPending((byte) 0); // 
			d40.setNoAuthOver((byte) 1); 
			d40.setPostFlag('P'); 
			d40.setAuthFlag('A'); 
			d40.setFeffFlag('F'); 
			d40.setCanceledFlag(' '); 
			d40.setPostAuthFeffCncl((byte) 0); 
			d40.setUpdtChkId((short) 0); 
			d40.setPartClearAmt(0); 
			d40.setPostTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8))); // Time HHMMSSTT


			logger.error("In 2");

			Session sessionDebit1=HBUtil.getSessionFactory().openSession();
			Transaction txDebit1=sessionDebit1.beginTransaction();		
			try {
				sessionDebit1.save(d40);
				txDebit1.commit();
				//				sessionDebit1.flush();	
				sessionDebit1.close();
				return true;		
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();

			logger.error("Exeption in Credit");
			//if(tx != null) tx.rollback();
			return false;
		}		

	}

	public static D010004 getD010004(int lbrCode, String BatchCd, Date entryDate)
	{	
		//SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
		D010004 d004 = null;
		try(Session session = HBUtil.getSessionFactory().openSession()) {
			D010004Id id = new D010004Id();
			id.setBatchCd(BatchCd);
			id.setEntryDate(entryDate);
			id.setLbrCode(lbrCode);
			d004 = session.get(D010004.class, id);
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return d004;
	}


	public static D001004 getD001004(int brCode)
	{	D001004 d04 = null;
	try(Session session = HBUtil.getSessionFactory().openSession()) {
		D001004Id id = new D001004Id();
		id.setCode("LASTOPENDATE");
		id.setLbrCode(brCode);			
		d04 = session.get(D001004.class, id);
		session.close();
	} catch (Exception e) {
		e.printStackTrace();
	}
	return d04;

	}

	public static D010001 getD001(int lbrCode, String BatchCd)
	{
		//SELECT BookType FROM D010001 WHERE LBrCode = 9 AND Code = 'ABBTR'
		System.out.println("lbrCode:>>>"+lbrCode);
		System.out.println("BatchCd::>>"+BatchCd);
		D010001 d001 = null;
		try(Session session = HBUtil.getSessionFactory().openSession()) {
			D010001Id id = new D010001Id();
			id.setCode(BatchCd);
			id.setLbrCode(lbrCode);			
			d001 = session.get(D010001.class, id);
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return d001;
	}


	public static D009021 getD009021(int lbrCode, String prdCd)
	{
		//SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB  
		D009021 d21 = null;
		try(Session session = HBUtil.getSessionFactory().openSession()) {
			D009021Id id = new D009021Id();
			id.setLbrCode(lbrCode);
			id.setPrdCd(prdCd);		
			d21 = session.get(D009021.class, id);
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return d21;
	}



	public static D002001 getD002001(String usrCode1)
	{
		D002001 d201 = null;
		try(Session session = HBUtil.getSessionFactory().openSession()) {
			d201 = session.get(D002001.class, usrCode1);
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return d201;
	}
	public static D001004 getBatchNameFromBatchCode(String batchCode)
	{	
		D001004 d04 = null;
		try(Session session = HBUtil.getSessionFactory().openSession()) {
			D001004Id id = new D001004Id();
			id.setCode(batchCode);
			id.setLbrCode(0);
			d04 = session.get(D001004.class, id);	
			session.close();
		} catch (Exception e) {
			logger.error("getBatchNameFromBatchCode >>batchCode"+batchCode+"\n"+e.getMessage());
			e.printStackTrace();
		}
		return d04;	
	}
	public static D001004 getBatchNameFromBatchCodeNew(String batchCode,Session session)
	{	
		D001004 d04 = null;
		try {
			D001004Id id = new D001004Id();
			id.setCode(batchCode);
			id.setLbrCode(0);
			d04 = session.get(D001004.class, id);	
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("getBatchNameFromBatchCodeNew >>batchCode"+batchCode+"\n"+e.getMessage());
			logger.error(e);
		}
		return d04;	
	}
	public static int getNextSetNo(){
		MBTRSETSEQ setSeq = new MBTRSETSEQ();
		int setNo=1;
		try(Session session = HBUtil.getSessionFactory().openSession()){
			Transaction t = session.beginTransaction();
			if(ConfigurationLoader.getParameters(false).getProperty("DATABASE").equalsIgnoreCase("ORACLE")) {
				Query q = session.createNativeQuery("select MBTRSETSEQ.nextval from dual ");
				setNo = Integer.valueOf(q.getSingleResult() + "");
				t.commit();
			}else {
				if (ConfigurationLoader.getParameters(false).getProperty("SQL_SERVER_VERSION").equalsIgnoreCase("2008")) {
					//Session session = HBUtil.getSessionFactory().openSession();
					
					try {
						D001004Id id = new D001004Id();
						id.setCode(MSGConstants.MBTR_SETNO);
						id.setLbrCode(0);
						D001004 d001004 = session.get(D001004.class, id);
						System.out.println("d001004::>>"+d001004);
						if (d001004 == null)
							return 1;
						d001004.setValue(String.valueOf(Integer.valueOf(d001004.getValue().trim()) + 1));
						session.update(d001004);
						t.commit();
						return Integer.valueOf(d001004.getValue().trim());
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						e.printStackTrace();
						logger.error("",e);
						return 1;
					}finally {
						session.close();
						//session = null;
						t = null;
					}	 
				}else {
					setNo = ((BigInteger) session.createNativeQuery("SELECT NEXT VALUE FOR MBTRSETSEQ").uniqueResult()).intValue();
					t.commit();
				}
			}
			
			//session.save(setSeq);
			//session.close();
		}catch(Exception e)
		{
			e.printStackTrace();
			logger.error(e);
		}	
		//setNo= setSeq.getId();
		/*if(setSeq.getId()==null)
		{
			Session session = HBUtil.getSessionFactory().openSession();
			try{
				session.save(setSeq);
				setNo= setSeq.getId();
			}catch(Exception e)
			{
				e.printStackTrace();
			}finally {
				session.close();
				session=null;
				
			}
		}*/
		return setNo;
	}

	public static int getNextScrollNo(){
		if (ConfigurationLoader.getParameters(false).getProperty("SQL_SERVER_VERSION").equalsIgnoreCase("2008")
				&& ConfigurationLoader.getParameters(false).getProperty("DATABASE").equalsIgnoreCase("SQL")) {
					Session session = HBUtil.getSessionFactory().openSession();
					Transaction t = session.beginTransaction();
					try {
						D001004Id id = new D001004Id();
						id.setCode(MSGConstants.MBTR_SROLLNO);
						id.setLbrCode(0);
						D001004 d001004 = session.get(D001004.class, id);
						if (d001004 == null)
							return 1;
						d001004.setValue(String.valueOf(Integer.valueOf(d001004.getValue().trim()) + 1));
						session.update(d001004);
						t.commit();
						return Integer.valueOf(d001004.getValue().trim());
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						logger.error("",e);
						session.close();
						session = null;
						t = null;
						e.printStackTrace();
						return 1;
					} finally {
						session.close();
						session = null;
						t = null;
					}
				}else {
					MBTRSCROLLSEQ scrollSeq = new MBTRSCROLLSEQ();
					int scrollNo=1;
					try(Session session = HBUtil.getSessionFactory().openSession()) {
						session.getTransaction().begin();
						if(ConfigurationLoader.getParameters(false).getProperty("DATABASE").equalsIgnoreCase("ORACLE")) {
							Query q = session.createNativeQuery("select MBTRSCROLLSEQ.nextval from dual ");
							scrollNo = Integer.valueOf(q.getSingleResult() + "");
						}else {
							scrollNo = ((BigInteger) session.createNativeQuery("SELECT NEXT VALUE FOR MBTRSCROLLSEQ").uniqueResult()).intValue();
						}
						//session.save(scrollSeq);
						session.getTransaction().commit();
						//session.close();
					} catch (Exception e) {
						e.printStackTrace();
						logger.error(e);
					}
					//scrollNo=scrollSeq.getId();		
					scrollSeq=null;
					return scrollNo;
				}
	}


	@SuppressWarnings("deprecation")
	public boolean getReconNo()
	{
		try(Session session = HBUtil.getSessionFactory().openSession()) {
			logger.error("RemLbrCode : "+remLbrCode);
			Criteria criteria = session.createCriteria(D100001.class).add(Restrictions.eq("id.lbrCode", this.remLbrCode)).setProjection(Projections.max("id.reconNo"));
			Integer topRecon = (Integer)criteria.uniqueResult();
			session.close();
			logger.error("TopRecon : "+topRecon);
			reconNo = topRecon+1;
			D100001 d = new D100001();
			D100001Id id = new D100001Id();
			id.setLbrCode(this.remLbrCode);
			id.setReconNo(reconNo);
			d.setId(id);
			d.setRequestDate(remDate);
			d.setRequestTime(new Date());
			d.setAbbTrType((short) 1); //1
			d.setUserCd(usrCode); //chan	ge Latter
			d.setPendingForDate(new Date()); //change Latter
			d.setHandledFlag((byte) 5); //Change Latter
			d.setEntryDate(remDate); //change Latter
			d.setBatchCd(remBatchCode);
			d.setSetNo(remSetNo);
			d.setMainScrollNo(remMainScrollNo);
			d.setStatus('E'); //Change Latter
			d.setToBrCode(benLbrCode);
			d.setToBrEntryDate(benDate);
			d.setToBrBatchCd(benBatchCode);
			d.setToBrSetNo(benSetNo);
			d.setToBrMainScrollNo(benMainScrollNo);
			d.setToBrMainScrollNo1(this.benMainScrollNo);
			d.setToBrAcctId(this.benAcctId);
			d.setToBrAcctAct("ATMDR");
			d.setToBrDrCr('C');
			d.setToBrNetAmt(this.toBramt);
			d.setDbtrAddMk(usrCode);
			d.setDbtrAddMb(0);
			d.setDbtrAddMs((short) 0);
			d.setDbtrAddMd(new Date());
			d.setDbtrAddMt(new Date());

			d.setDbtrAddCk(0);
			d.setDbtrAddCb(0);
			d.setDbtrAddCs((short) 0);
			d.setDbtrAddCd(new Date());
			d.setDbtrAddCt(new Date());

			d.setDbtrLupdMk(0);
			d.setDbtrLupdMb(0);
			d.setDbtrLupdMs((short) 0);
			d.setDbtrLupdMd((new Date()));
			d.setDbtrLupdMt((new Date()));

			d.setDbtrLupdCk(0);
			d.setDbtrLupdCb(0);
			d.setDbtrLupdCs((short) 0);
			d.setDbtrLupdCd(new Date());
			d.setDbtrLupdCt(new Date());

			d.setDbtrTauthDone((short) 0);
			d.setDbtrRecStat((byte) 0);
			d.setDbtrAuthDone((byte) 0);
			d.setDbtrAuthNeeded((byte) 0);
			d.setDbtrUpdtChkId((short) 2);
			d.setDbtrLhisTrnNo(0);

			sessionDebit.save(d);
			sessionDebit.flush();
			drABB.setTokenNo(reconNo);
			String debPart = (("ReconNo = "+reconNo+"\tFromBrCode = "+remLbrCode+"\tAcctId = "+benAcctId).length() > 70) ? ("ReconNo = "+reconNo+"\tFromBrCode = "+remLbrCode+"\tAcctId = "+benAcctId).substring(0,70) : ("ReconNo = "+reconNo+"\tFromBrCode = "+remLbrCode+"\tAcctId = "+benAcctId);
			drABB.setParticulars(debPart);
			crABB.setTokenNo(reconNo);
			String crdPart = (("ReconNo = "+reconNo+"\tToBrCode = "+benLbrCode+"\tAcctId = "+remAcctId).length() > 70) ? ("ReconNo = "+reconNo+"\tToBrCode = "+benLbrCode+"\tAcctId = "+remAcctId).substring(0, 70) : ("ReconNo = "+reconNo+"\tToBrCode = "+benLbrCode+"\tAcctId = "+remAcctId);
			crABB.setParticulars(crdPart);
			sessionDebit.update(drABB);
			sessionDebit.flush();
			sessionDebit.update(crABB);
			sessionDebit.flush();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;		

	}

	public boolean transact()
	{
		try {
			D009022Id remId = new D009022Id();
			remId.setLbrCode(remLbrCode);
			remId.setPrdAcctId(remAcctId);
			D009022 remAcct = sessionDebit.get(D009022.class, remId);

			if((remAcct.getActClrBalFcy()-remAcct.getTotalLienFcy()) > toBramt){
				remAcct.setActClrBalFcy(remAcct.getActClrBalFcy()-toBramt);
				remAcct.setShdClrBalFcy(remAcct.getShdClrBalFcy()-toBramt);
				remAcct.setShdTotBalFcy(remAcct.getShdTotBalFcy()-toBramt);
				remAcct.setActTotBalFcy(remAcct.getActTotBalFcy()-toBramt);
				remAcct.setActTotBalLcy(remAcct.getActTotBalLcy()-toBramt);

				sessionDebit.update(remAcct);
				sessionDebit.flush();

			}
			else{
				logger.error("Insufficient Funds ");
				return false;
			}

			D009022Id benId = new D009022Id();
			benId.setLbrCode(benLbrCode);
			benId.setPrdAcctId(benAcctId);

			D009022 benAcct = sessionDebit.get(D009022.class, benId);
			benAcct.setActClrBalFcy(benAcct.getActClrBalFcy()+toBramt);
			benAcct.setShdClrBalFcy(benAcct.getShdClrBalFcy()+toBramt);
			benAcct.setShdTotBalFcy(benAcct.getShdTotBalFcy()+toBramt);
			benAcct.setActTotBalFcy(benAcct.getActTotBalFcy()+toBramt);
			benAcct.setActTotBalLcy(benAcct.getActTotBalLcy()+toBramt);

			sessionDebit.update(benAcct);
			sessionDebit.flush();


			return true;		
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public static short getModuleType(int lbrCode, String prdCd)
	{
		D009021 d21 = null;
		try(Session session = HBUtil.getSessionFactory().openSession()) {
			D009021Id id = new D009021Id();
			id.setLbrCode(lbrCode);
			id.setPrdCd(prdCd);		
			d21 = session.get(D009021.class, id);
			if(d21 != null) return d21.getModuleType();
 		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	public static D009040 prepareD009040Object(D010004 selectedBatch,D010004 onlineBatch,int lbrCode,int setNo,int scrollNo,int mainScrollNo,String prdAcctId,Session session,double amt,String particulars,String drCr,String rrn)
	{
		System.out.println("prdAcctId::>>"+prdAcctId);
		char debitCredit=' ';
		String activity="";
		if(drCr.trim().equalsIgnoreCase("DR"))
		{
			debitCredit='D';
			activity="DR";
		}
		if(drCr.trim().equalsIgnoreCase("CR"))
		{
			debitCredit='C';
			activity="CR";
		}
		D009040 d40 = new D009040();
		D009040Id id40 = new D009040Id();
		id40.setBatchCd(selectedBatch.getId().getBatchCd()); //SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016'

		id40.setEntryDate(selectedBatch.getId().getEntryDate()); // SELECT Value FROM D001004 WHERE LBrCode = 9 AND Code = 'LASTOPENDATE'
		id40.setLbrCode(lbrCode); 
		id40.setSetNo((int) setNo);
		id40.setScrollNo((int) scrollNo);
		d40.setId(id40);
		d40.setMainScrollNo((int) mainScrollNo); //Scroll No
		d40.setPostDate(onlineBatch.getPostDate()); //SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
		d40.setFeffDate(onlineBatch.getFeffDate()); // SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
		d40.setActivityType(activity); 
		d40.setCashFlowType(activity);
		d40.setValueDate(onlineBatch.getPostDate()); // postdate SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'

		logger.error("Calling D010001");
		D010001 d001 = getD001(lbrCode, selectedBatch.getId().getBatchCd());
		
		D009021Id id = new D009021Id();
		id.setLbrCode(lbrCode);
		id.setPrdCd(prdAcctId.substring(0,8));		
		D009021 d21 = session.get(D009021.class, id);
		String booktype=d001.getBookType();
		d40.setBookType(booktype); //SELECT BookType FROM D010001 WHERE LBrCode = 9 AND Code = 'ABBTR'
		d40.setDrCr((char)debitCredit);
		d40.setVcrAcctId(prdAcctId);
		d40.setMainAcctId(prdAcctId);
		D009021 d009021 = getD009021(lbrCode, prdAcctId.substring(0,8).trim());
		d40.setMainModType(d009021.getModuleType()); // SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB      
		d40.setVcrModType(d009021.getModuleType()); // SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB      
		d40.setTrnCurCd("INR");
		d40.setFcyTrnAmt(amt);
		d40.setLcyConvRate(1);
		d40.setLcyTrnAmt(amt);
		d40.setInstrBankCd((short) 0);
		d40.setInstrBranchCd((short) 0);
		d40.setInstrType((short) 99); // Depend on chanel
		d40.setInstrNo(rrn);	// RRN
		d40.setInstrDate(new Date()); //Blank
		d40.setParticulars(particulars); //param
		d40.setSysGenVcr((byte) 0); // value 0
		d40.setShTotFlag('Y'); // 
		d40.setShClrFlag('Y');
		d40.setAcTotFlag('Y');
		d40.setAcClrFlag('Y');

		D002001 d002001 = getD002001("WEB");
		
		usrCode = d002001.getUsrCode2();
		d40.setMaker(d002001.getUsrCode2()); // SELECT UsrCode2 FROM D002001 WHERE UsrCode1 = 'WEB' 
		d40.setMakerDate(selectedBatch.getId().getEntryDate()); // EntryDate 
		d40.setMakerTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8))); 
		d40.setChecker1(d002001.getUsrCode2()); // SELECT UsrCode2 FROM D002001 WHERE UsrCode1 = 'WEB' 
		d40.setChecker2(0);
		d40.setChecker3(0);
		d40.setChecker4(0);
		d40.setCheckerDate(selectedBatch.getId().getEntryDate()); // EntryDate
		d40.setCheckerTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8)));
		d40.setNoAuthPending((byte) 0); // 
		d40.setNoAuthOver((byte) 1); 
		d40.setPostFlag('P'); 
		d40.setAuthFlag('A'); 
		d40.setFeffFlag('F');
		d40.setCanceledFlag(' '); 
		d40.setPostAuthFeffCncl((byte) 0); 
		d40.setUpdtChkId((short) 0); 
		d40.setPartClearAmt(0); 
		d40.setPostTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8))); // Time HHMMSSTT
		return d40;
	}

	public static int getNextReconNo(int brCode)
	{
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		try {
			
			String hql = "select lastNo from D001005 where id.lbrCode = :lbrCode and id.catType='ABBRECON'";
			List<Long> seqNo = (List<Long>) session.createQuery(hql).setParameter("lbrCode", brCode).getResultList();
			logger.info(seqNo.get(0));
			if (seqNo != null && seqNo.size() > 0) {
				
				
				Long count = Long.parseLong(seqNo.get(0)+"") + 1;
				while (true) {
					D100001 reconRecord = reconNoCheck(count, brCode, session);
					if (reconRecord == null) {
						t.commit();
						return count.intValue();
					}else
						count = count + 1;
				}
			}else return 1;

			//session.update(d001004);
			
			//return null;
		} catch (Exception e) {
			// TODO: handle exception
			session.close();
			session = null;
			t = null;
			e.printStackTrace();
			return 1;
		} finally {
			session.close();
			session = null;
			t = null;
		}
	}

	public static D100001 prepareReconObj(int brCode,int reconNo,Date opdate,int usrCode,String sourceBatchCode,String benBatchCode,int sourceSetNo,
			int sourceScrollNo,int destSetNo,int destScrollNo,int destScrollNo1,int benBrCode,String abbAcc,double  amount,String drCr)
	{
		D100001 d = new D100001();
		D100001Id id = new D100001Id();
		id.setLbrCode(brCode);
		id.setReconNo(reconNo);
		d.setId(id);
		d.setRequestDate(opdate);
		d.setRequestTime(new Date());
		d.setAbbTrType((short) 1); //1
		d.setUserCd(usrCode); //chan	ge Latter
		d.setPendingForDate(new Date()); //change Latter
		d.setHandledFlag((byte) 5); //Change Latter
		d.setEntryDate(opdate); //change Latter
		d.setBatchCd(sourceBatchCode);
		d.setSetNo(sourceSetNo);
		d.setMainScrollNo(sourceScrollNo);
		d.setStatus('E'); //Change Latter
		d.setToBrCode(benBrCode);
		d.setToBrEntryDate(opdate);
		d.setToBrBatchCd(benBatchCode);
		d.setToBrSetNo(destSetNo);
		d.setToBrMainScrollNo(destScrollNo);
		d.setToBrMainScrollNo1(destScrollNo1);
		d.setToBrAcctId(abbAcc);
		
		if(drCr.equalsIgnoreCase("D"))
		{
			d.setToBrAcctAct("ATMDR");
			d.setToBrDrCr('D');	
		}else
		{
			d.setToBrAcctAct("ATMCR");
			d.setToBrDrCr('C');	
		}
		d.setToBrNetAmt(amount);
		d.setDbtrAddMk(usrCode);
		d.setDbtrAddMb(0);
		d.setDbtrAddMs((short) 0);
		d.setDbtrAddMd(new Date());
		d.setDbtrAddMt(new Date());

		d.setDbtrAddCk(0);
		d.setDbtrAddCb(0);
		d.setDbtrAddCs((short) 0);
		d.setDbtrAddCd(new Date());
		d.setDbtrAddCt(new Date());

		d.setDbtrLupdMk(0);
		d.setDbtrLupdMb(0);
		d.setDbtrLupdMs((short) 0);
		d.setDbtrLupdMd((new Date()));
		d.setDbtrLupdMt((new Date()));

		d.setDbtrLupdCk(0);
		d.setDbtrLupdCb(0);
		d.setDbtrLupdCs((short) 0);
		d.setDbtrLupdCd(new Date());
		d.setDbtrLupdCt(new Date());

		d.setDbtrTauthDone((short) 0);
		d.setDbtrRecStat((byte) 0);
		d.setDbtrAuthDone((byte) 0);
		d.setDbtrAuthNeeded((byte) 0);
		d.setDbtrUpdtChkId((short) 2);
		d.setDbtrLhisTrnNo(0);
		return d;
	}
	
	public static D100002 prepareRecon2Obj(int brCode,int reconNo,Date opdate,int usrCode,String sourceBatchCode,String benBatchCode,int sourceSetNo,
			int sourceScrollNo,int destSetNo,int destScrollNo,int destScrollNo1,int benBrCode,String abbAcc,double  amount,String drCr)
	{
		System.out.println("abbAcc::>>"+abbAcc);
		D100002 d = new D100002();
		D100002Id id = new D100002Id();
		id.setFromBrCode(brCode);
		id.setFromBrReconNo(reconNo);
		d.setId(id);
		d.setStatus('A');
		d.setToBrAcctId(abbAcc);
		d.setToBrBatchCd(benBatchCode);
		d.setToBrCode(benBrCode);
		d.setToBrEntryDate(opdate);
		d.setToBrMainScrollNo(destScrollNo);
		d.setToBrMainScrollNo1(destScrollNo1);
		d.setToBrNetAmt(amount);
		d.setToBrSetNo(destSetNo);
		if(drCr.equalsIgnoreCase("D"))
		{
			d.setToBrAcctAct("ATMDR");
			d.setToBrDrCr('D');	
		}else
		{
			d.setToBrAcctAct("ATMCR");
			d.setToBrDrCr('C');	
		}
		d.setToBrNetAmt(amount);
		return d;
	}

	public static D100001 prepareReconObjBranch(int brCode,int reconNo,Date opdate,int usrCode,String sourceBatchCode,String benBatchCode,int sourceSetNo,
			int sourceScrollNo,int destSetNo,int destScrollNo,int destScrollNo1,int benBrCode,String abbAcc,double  amount,String drCr)
	{
		D100001 d = new D100001();
		D100001Id id = new D100001Id();
		id.setLbrCode(brCode);
		id.setReconNo(reconNo);
		d.setId(id);
		d.setRequestDate(opdate);
		d.setRequestTime(new Date());
		d.setAbbTrType((short) 1); //1
		d.setUserCd(usrCode); //chan	ge Latter
		d.setPendingForDate(new Date()); //change Latter
		d.setHandledFlag((byte) 5); //Change Latter
		d.setEntryDate(opdate); //change Latter
		d.setBatchCd(sourceBatchCode);
		d.setSetNo(sourceSetNo);
		d.setMainScrollNo(sourceScrollNo);
		d.setStatus('A'); //Change Latter
		d.setToBrCode(benBrCode);
		d.setToBrEntryDate(opdate);
		d.setToBrBatchCd(benBatchCode);
		d.setToBrSetNo(destSetNo);
		d.setToBrMainScrollNo(destScrollNo);
		d.setToBrMainScrollNo1(destScrollNo1);
		d.setToBrAcctId(abbAcc);
		
		if(drCr.equalsIgnoreCase("D"))
		{
			d.setToBrAcctAct("DR");
			d.setToBrDrCr('C');	
		}else
		{
			d.setToBrAcctAct("CR");
			d.setToBrDrCr('D');	
		}
		d.setToBrNetAmt(amount);
		d.setDbtrAddMk(usrCode);
		d.setDbtrAddMb(0);
		d.setDbtrAddMs((short) 0);
		d.setDbtrAddMd(new Date());
		d.setDbtrAddMt(new Date());

		d.setDbtrAddCk(0);
		d.setDbtrAddCb(0);
		d.setDbtrAddCs((short) 0);
		d.setDbtrAddCd(new Date());
		d.setDbtrAddCt(new Date());

		d.setDbtrLupdMk(0);
		d.setDbtrLupdMb(0);
		d.setDbtrLupdMs((short) 0);
		d.setDbtrLupdMd((new Date()));
		d.setDbtrLupdMt((new Date()));

		d.setDbtrLupdCk(0);
		d.setDbtrLupdCb(0);
		d.setDbtrLupdCs((short) 0);
		d.setDbtrLupdCd(new Date());
		d.setDbtrLupdCt(new Date());

		d.setDbtrTauthDone((short) 0);
		d.setDbtrRecStat((byte) 0);
		d.setDbtrAuthDone((byte) 0);
		d.setDbtrAuthNeeded((byte) 0);
		d.setDbtrUpdtChkId((short) 2);
		d.setDbtrLhisTrnNo(0);
		return d;
	}
	
	public static D100002 prepareRecon2ObjBranch(int brCode,int reconNo,Date opdate,int usrCode,String sourceBatchCode,String benBatchCode,int sourceSetNo,
			int sourceScrollNo,int destSetNo,int destScrollNo,int destScrollNo1,int benBrCode,String abbAcc,double  amount,String drCr)
	{
		System.out.println("abbAcc::>>"+abbAcc);
		D100002 d = new D100002();
		D100002Id id = new D100002Id();
		id.setFromBrCode(brCode);
		id.setFromBrReconNo(reconNo);
		d.setId(id);
		d.setStatus('A');
		d.setToBrAcctId(abbAcc);
		d.setToBrBatchCd(benBatchCode);
		d.setToBrCode(benBrCode);
		d.setToBrEntryDate(opdate);
		d.setToBrMainScrollNo(destScrollNo);
		d.setToBrMainScrollNo1(destScrollNo1);
		d.setToBrNetAmt(amount);
		d.setToBrSetNo(destSetNo);
		if(drCr.equalsIgnoreCase("D"))
		{
			d.setToBrAcctAct("DR");
			d.setToBrDrCr('C');	
		}else
		{
			d.setToBrAcctAct("CR");
			d.setToBrDrCr('D');	
		}
		d.setToBrNetAmt(amount);
		return d;
	}
	public static String updateBalanceABB(double amount,int brCode,String type,String pCode)
	{
		try {
			Session session=HBUtil.getSessionFactory().openSession();
			Transaction t=session.beginTransaction();
			Query query=null;
			if(type.equalsIgnoreCase("C"))
			{
				query= session.createQuery("update D009021 set LcyBal = LcyBal +:amount , FcyBal = FcyBal + :amount where LBrCode = :lbrcode and PrdCd = '"+pCode+"'");
				query.setParameter("amount", amount);
				query.setParameter("lbrcode", brCode);
			}
			else
			{
				query = session.createQuery("update D009021 set LcyBal = LcyBal -:amount , FcyBal = FcyBal - :amount where LBrCode = :lbrcode and PrdCd = '"+pCode+"'");
				query.setParameter("amount", amount);
				query.setParameter("lbrcode", brCode);
			}
			int rows=query.executeUpdate();
			t.commit();
			session.close();
			session=null;
			if(rows>0)
				return "SUCCESS";
			else
				return "ERROR";
		} catch (Exception e) {
			e.printStackTrace();
			return "ERROR";
		}
	}
	public static String updateProductBalances(double amount,int brCode,String type,String pCode,Session session)
	{
		try {
			System.out.println("VoucherMPOS.updateProductBalances() brCode >>>>>>> "+brCode);
			System.out.println("VoucherMPOS.updateProductBalances() pCode >>>>>>> "+pCode);
			logger.error("VoucherMPOS.updateProductBalances() brCode >>>>>>> "+brCode);
			logger.error("VoucherMPOS.updateProductBalances() pCode >>>>>>> "+pCode);
			Query query=null;
			if(type.equalsIgnoreCase("C"))
			{
				query= session.createQuery("update D009021 set lcyBal = lcyBal +:amount , fcyBal = fcyBal + :amount where id.lbrCode = :lbrcode and id.prdCd =:pCode");
				query.setParameter("amount", amount);
				query.setParameter("lbrcode", brCode);
				query.setParameter("pCode", pCode);
			}
			else
			{
				query = session.createQuery("update D009021 set lcyBal = lcyBal -:amount , fcyBal = fcyBal - :amount where id.lbrCode = :lbrcode and id.prdCd =:pCode");
				query.setParameter("amount", amount);
				query.setParameter("lbrcode", brCode);
				query.setParameter("pCode", pCode);
			}
			int rows=query.executeUpdate();
			if(rows>0)
				return MSGConstants.SUCCESS;
			else
				return  MSGConstants.ERROR;
			/*D009021Id id=new D009021Id();
			id.setLbrCode(brCode);
			id.setPrdCd(pCode);
			D009021 d009021=session.get(D009021.class, id);
			id=null;
			if(d009021==null)
				return MSGConstants.ERROR;
			if(type.equalsIgnoreCase(MSGConstants.CR))
			{
				double lcyBal=d009021.getLcyBal()+amount;
				double fcyBal=d009021.getFcyBal()+amount;
				logger.error("VoucherMPOS.updateProductBalances() lcyBal >>>>>>> "+lcyBal);
				logger.error("VoucherMPOS.updateProductBalances() fcyBal >>>>>>> "+fcyBal);
				System.out.println("VoucherMPOS.updateProductBalances() lcyBal >>>>>>> "+lcyBal);
				System.out.println("VoucherMPOS.updateProductBalances() fcyBal >>>>>>> "+fcyBal);
				d009021.setLcyBal(lcyBal);
				d009021.setFcyBal(fcyBal);
				session.update(d009021);
				return MSGConstants.SUCCESS;
			}else if(type.equalsIgnoreCase(MSGConstants.DR))
			{
				double lcyBal=d009021.getLcyBal()-amount;
				double fcyBal=d009021.getFcyBal()-amount;
				System.out.println("VoucherMPOS.updateProductBalances() lcyBal >>>>>>> "+lcyBal);
				System.out.println("VoucherMPOS.updateProductBalances() fcyBal >>>>>>> "+fcyBal);
				logger.error("VoucherMPOS.updateProductBalances() lcyBal >>>>>>> "+lcyBal);
				logger.error("VoucherMPOS.updateProductBalances() fcyBal >>>>>>> "+fcyBal);
				d009021.setLcyBal(lcyBal);
				d009021.setFcyBal(fcyBal);
				session.update(d009021);
				return MSGConstants.SUCCESS;
			}else
				return MSGConstants.ERROR;*/
			
		} catch (Exception e) {
			e.printStackTrace();
			return MSGConstants.ERROR;
		}
	}

	/*public static String updateProductBalancesNew(double amount,int brCode,String type,String pCode,Session session)
	{
		try {
			System.out.println("amount::>>"+amount);
			System.out.println("brCode::>>"+brCode);
			System.out.println("type::>>"+type);
			System.out.println("pCode::>>"+pCode);
			Query query=null;
			if(type.equalsIgnoreCase("C"))
			{
				query= session.createQuery("update D009021 set LcyBal = LcyBal +:amount , FcyBal = FcyBal + :amount where LBrCode = :lbrcode and PrdCd = '"+pCode+"'");
				query.setParameter("amount", amount);
				query.setParameter("lbrcode", brCode);
			}
			else
			{
				query = session.createQuery("update D009021 set LcyBal = LcyBal -:amount , FcyBal = FcyBal - :amount where LBrCode = :lbrcode and PrdCd = '"+pCode+"'");
				query.setParameter("amount", amount);
				query.setParameter("lbrcode", brCode);
			}
			int rows=query.executeUpdate();
			System.out.println("amar Rows::>>"+rows);
//			session.flush();
			if(rows>0)
				return "SUCCESS";
			else
				return "ERROR";
		} catch (Exception e) {
			e.printStackTrace();
			return "ERROR";
		}
	}*/
	public static TransactionValidationResponse otherBranchVouchers(int lbrCode,String accNo,int benBrCode,String benAccNo,String transType,String narration,double amount,String rrn)
	{
		TransactionValidationResponse response=new TransactionValidationResponse(); 
		VoucherCommon common=new VoucherCommon();
		Session session=HBUtil.getSessionFactory().openSession();
		String balance="";
		try {
			if(lbrCode==benBrCode)
			{
				logger.error("Same Branch Fund Transfer....");
				Date openDate = VoucherCommon.getOpenDateNew(lbrCode,session); // Get Open Date
				if(openDate == null){
//					if(t.isActive())t.rollback();
					Logger.error("Open Date Not Found. Aborting Transaction");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.OPERATION_DATE_NOT_FOUND);
					return response;
				}
				Logger.info("Open Date is "+openDate);
				//Get BatchCodes from properties file.			
				String batchCode = Props.getBatchProperty(transType);
				Logger.info("Batch Code Form Properties File : "+batchCode);
				String batchCodes[] = batchCode.split("~");
				Logger.info("Number of Batches is "+batchCodes.length+"\t Names are "+Arrays.toString(batchCodes));
				if(batchCodes == null || batchCodes.length <1){
					Logger.error("Batch Codes Not Found in Properties File.");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.BATCH_CODE_NOT_FOUND);
					return response;
				}
				/// Get Selected Batch.
				D010004 selectedBatch = VoucherCommon.getSelectedBatchNew(lbrCode, batchCodes, openDate,session);
				if(selectedBatch == null){
//					if(t.isActive())t.rollback();
					Logger.error("No Active Batch Found.");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg("No Active Batch Found.");
					return response;
				}
				int setNo=getNextSetNo();
				int mainScrollNo=getNextScrollNo();//DDSCR
				//Date/MPOS/Agent Account
				narration="MPOS/"+DateUtil.getcurrentDateString()+"/";
				/*** Added by Aniket Desai on 5th Feb, 2020 for Pigmi Daily Limit  ***/
				Transaction t=session.beginTransaction();
				D001004 system = DataUtils.getSystemParameter(lbrCode, "LASTOPENDATE");
				Date date = DateUtil.getDateFromStringNew(system.getValue().trim().substring(1));
				if(getPigAtmValid(benAccNo,lbrCode,date,amount, "C", session)) {
					Logger.error("Exceeds Daily Limit:-");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg("Exceeds Daily Limit");
					return response;
				}else {
					Query query = session.createQuery(
							"update D047003 set TranDate=:tranDate, TranAmount = TranAmount + :amount where LBrCode="+lbrCode+" AND CustPrdAcctId='"+benAccNo+"'");
					query.setParameter("amount", amount);
					query.setParameter("tranDate", date);
					query.executeUpdate();
				}
				common.debitSameBranchDDS(lbrCode, accNo, transType.toUpperCase(), setNo, getNextScrollNo(),narration+Integer.valueOf((benAccNo==null ||benAccNo.trim().length()!=32)?"0":benAccNo.substring(16,24).trim()), amount, rrn,mainScrollNo,session,selectedBatch);
				if(!common.isAborted)
				{
					if(VoucherMPOS.updateProductBalances(amount,lbrCode,"D",accNo.substring(0,8).trim(),session).equalsIgnoreCase(MSGConstants.SUCCESS)) {//commented this condition as we for same branch product not update
					boolean flag=true;
					if(flag)
					{
						String bal=CoreTransactionMPOS.balanceShivKrupa(lbrCode,accNo.trim(), amount, "D",session);
						logger.error("common.isAborted::>>"+common.isAborted);
						if(bal!=null && bal.trim().length()>0 && !bal.trim().equalsIgnoreCase("51") && !bal.trim().equalsIgnoreCase("99"))
						{
							logger.error("Transaction successful");
							common.creditSameBranchDDS(benBrCode, benAccNo, transType, setNo , getNextScrollNo(), narration+Integer.valueOf((accNo==null ||accNo.trim().length()!=32)?"0":accNo.substring(16,24).trim()), amount, rrn,mainScrollNo,session,selectedBatch,accNo);
							if(!common.isAborted)
							{
								if(VoucherMPOS.updateProductBalances(amount,benBrCode,"C",benAccNo.substring(0,8).trim(),session).trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
								if(flag)
								{
									balance=CoreTransactionMPOS.balanceShivKrupa(benBrCode,benAccNo.trim(), amount, "C",session);
									
									if(balance!=null && balance.trim().length()>0 && !balance.trim().equalsIgnoreCase("51") && !balance.trim().equalsIgnoreCase("99"))
									{
										
										
										logger.error("balance::>>"+balance);
										t.commit();
										response.setResponse(MSGConstants.SUCCESS);
										response.setErrorMsg(MSGConstants.SUCCESS_MSG);
										response.setBatchCode(String.valueOf(selectedBatch.getId().getBatchCd()+""));
										response.setSetNo(setNo+"");
										response.setScrollNo(String.valueOf(mainScrollNo));
										response.setBalance(balance);
										return response;		
									}else
									{
										if(t.isActive())t.rollback();
										logger.error("Transaction unsuccessful");
										response.setResponse(MSGConstants.ERROR);
										response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
										response.setBatchCode("");
										response.setScrollNo("");
										return response;
									}
								}else
								{
									if(t.isActive())t.rollback();
									logger.error("Transaction unsuccessful");
									response.setResponse(MSGConstants.ERROR);
									response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
									response.setBatchCode("");
									response.setScrollNo("");
									return response;
								}
							}else
							{
								if(t.isActive())t.rollback();
								logger.error("Transaction unsuccessful");
								response.setResponse(MSGConstants.ERROR);
								response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
								response.setBatchCode("");
								response.setScrollNo("");
								return response;
							}
							}else
							{
								if(t.isActive())t.rollback();
								logger.error("Transaction unsuccessful");
								response.setResponse(MSGConstants.ERROR);
								response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
								response.setBatchCode("");
								response.setScrollNo("");
								return response;
							}
						}else
						{
							if(t.isActive())t.rollback();
							logger.error("Transaction unsuccessful");
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
							response.setBatchCode("");
							response.setScrollNo("");
							return response;
						}
					}else
					{
						if(t.isActive())t.rollback();
						logger.error("Transaction unsuccessful");
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
						response.setBatchCode("");
						response.setScrollNo("");
						return response;
					}
				}else
				{
					if(t.isActive())t.rollback();
					logger.error("Transaction unsuccessful");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
					response.setBatchCode("");
					response.setScrollNo("");
					return response;
				}
				}
				else
				{
					if(t.isActive())t.rollback();
					logger.error("Transaction unsuccessful");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
					response.setBatchCode("");
					response.setScrollNo("");
					return response;
				}
			}else
			{
				logger.error("Other Branch Fund Transfer....");
				int setNo=getNextSetNo();
				int reconNo=getNextReconNo(lbrCode);
				int scrollNo=getNextScrollNo();
				Transaction t=session.beginTransaction();
				D001004 system = DataUtils.getSystemParameter(lbrCode, "LASTOPENDATE");
				Date date = DateUtil.getDateFromStringNew(system.getValue().trim().substring(1));
				if(getPigAtmValid(benAccNo,lbrCode,date,amount, "C", session)) {
					Logger.error("Exceeds Daily Limit");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg("Exceeds Daily Limit");
					return response;
				}else {
					Query query = session.createQuery(
							"update D047003 set TranDate=:tranDate, TranAmount = TranAmount + :amount where LBrCode="+lbrCode+" AND CustPrdAcctId='"+benAccNo+"'");
					query.setParameter("amount", amount);
					query.setParameter("tranDate", date);
					query.executeUpdate();
				}
				common.debit(lbrCode, accNo, transType.toUpperCase(), setNo, scrollNo,narration, amount, rrn,session);
				VoucherMPOS.updateProductBalances(amount,lbrCode,"D",accNo.substring(0,8).trim(),session);
				CoreTransactionMPOS.balance(lbrCode, accNo, amount, "D",session);
				if(!common.isAborted)
				{
					int setNoABB=getNextSetNo();
					int scrollNoAbb=getNextScrollNo();
					int scrollNoAbb1=getNextScrollNo();
					int scrollNoAbb2=getNextScrollNo();
					logger.error("Transaction successful");
					//					common.creditABB(lbrCode, MSGConstants.ABB_ACC, "ABB", setNo, getNextScrollNo(), narration, lbrCode, amount, rrn,reconNo,session);
					
					common.creditABB(lbrCode, MSGConstants.ABB_ACC, transType.toUpperCase(), setNo, scrollNoAbb, narration, benBrCode, amount, rrn,reconNo,session);
					VoucherMPOS.updateProductBalances(amount,lbrCode,"C",MSGConstants.ABB_ACC.substring(0,8).trim(),session);
					if(!common.isAborted)
					{
						logger.error("ABB Transaction successful");
						
						common.credit(benBrCode, benAccNo, MSGConstants.ABB,setNoABB , scrollNoAbb1, narration, amount, rrn,session,accNo);
						//common.credit(benBrCode, benAccNo, MSGConstants.ABB,setNoABB , scrollNoAbb1, narration, amount, rrn,session);
						
						VoucherMPOS.updateProductBalances(amount,benBrCode,"C",benAccNo.substring(0,8).trim(),session);
						balance=CoreTransactionMPOS.balance(benBrCode, benAccNo, amount, "C",session);
						
						if(!common.isAborted)
						{
							logger.error("Other Bank GL Transaction successful");
							common.debitABB(benBrCode, MSGConstants.ABB_ACC, MSGConstants.ABB, setNoABB,scrollNoAbb2,narration,lbrCode,amount, rrn,reconNo,session);
							VoucherMPOS.updateProductBalances(amount,benBrCode,"D",MSGConstants.ABB_ACC.substring(0,8).trim(),session);

							Date opdate=DataUtils.getOpenDate(lbrCode);

							String batchCodes[] = Props.getBatchProperty("MPOS").split("~");
							D001004 d04OnlineBatchName = VoucherMPOS.getBatchNameFromBatchCode(batchCodes[0]);
							String onlineBatchName = d04OnlineBatchName.getValue().trim();
							String batchCodes1[] = Props.getBatchProperty("ABB").split("~");
							D001004 d001004 = VoucherMPOS.getBatchNameFromBatchCode(batchCodes1[0]);
							String benBatchCode = d001004.getValue().trim();

							D100001 d100001=VoucherMPOS.prepareReconObj(lbrCode, reconNo, opdate, 999999, onlineBatchName, benBatchCode, setNo, scrollNoAbb, setNoABB, scrollNoAbb1,scrollNoAbb2, benBrCode,""+benAccNo ,Double.valueOf(amount),"D");
							System.out.println("d100001::>>>"+d100001);

							D100002 d100002=VoucherMPOS.prepareRecon2Obj(lbrCode, reconNo, opdate, 999999, onlineBatchName, benBatchCode, setNo, scrollNoAbb, setNoABB,scrollNoAbb1,scrollNoAbb2, benBrCode,""+benAccNo ,Double.valueOf(amount),"D");//
							System.out.println("d100002::>>>"+d100002);
							session.save(d100001);
							session.save(d100002);
							t.commit();
							response.setResponse(MSGConstants.SUCCESS);
							response.setErrorMsg(MSGConstants.SUCCESS_MSG);
							response.setBatchCode(String.valueOf(setNo));
							response.setScrollNo(String.valueOf(scrollNo));
							response.setBalance(balance);
							return response;
						}else
						{
							if(t.isActive())t.rollback();
							logger.error("ABB Transaction unsuccessful");
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
							response.setBatchCode("");
							response.setScrollNo("");
							response.setBalance(balance);
							return response;
						}
					}else
					{
						if(t.isActive())t.rollback();
						logger.error("ABB Transaction unsuccessful");
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
						response.setBatchCode("");
						response.setScrollNo("");
						return response;
					}
				}else
				{
					if(t.isActive())t.rollback();
					logger.error("Transaction unsuccessful");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
					response.setBatchCode("");
					response.setScrollNo("");
					return response;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			response.setBatchCode("");
			response.setScrollNo("");
			return response;
		}
		finally {
			session.close();
			session=null;
		}
	}
	
	/*** Added by Aniket Desai on 5th Feb, 2020 for Pigmi Daily Limit  ***/
	public static boolean getPigAtmValid(String prodCode, int lbrCode, Date date,Double depositAmt, String trType, Session session) {
		
		
		
		String operationDate = DateUtil.getDateFormat_ddMMMyyyy(date);
		
		Double amt = 0.0;
		
		//String query2 ="select * FROM D047003 WHERE LBrCode="+lbrCode+" AND CustPrdAcctId='"+prodCode+"'";
		D047003Id id = new D047003Id(lbrCode,prodCode);
		D047003 data = session.get(D047003.class, id);
		Double limitAmt = data.getDepositAmt();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String dateString = "19000101";
		if(data.getTranDate()!=null)
			dateString = sdf.format(data.getTranDate());
		
		Date date2= null;
		try {
			date2 = sdf.parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(date.compareTo(date2)==0) {
			amt=data.getTranAmt();
		}else {
			amt=0.0;
			Query query = session.createQuery(
					"update D047003 set TranAmount =:amount where LBrCode="+lbrCode+" AND CustPrdAcctId='"+prodCode+"'");
			query.setParameter("amount", amt);
			//query.setParameter("tranDate", date);
			query.executeUpdate();
		}
		int no = 3;
		try {
			no = Integer.parseInt(ConfigurationLoader.getParameters(false).getProperty("PIGMI_Daily_Amt_valid_no"));
			if(no==0)no=3;
		}catch(Exception ex) {
			
		}
		if(limitAmt*no>=amt+depositAmt)
			return false;
		else
			return true;
	}
	
	public static boolean isMultiple(Double num1, Double num2){

        Double remainder = num1 % num2;
        boolean multiple;

        if (remainder != 0){
            multiple = false;
        }
        else{
            multiple = true;
        }

        return multiple;
    }
	
	public static D100001 reconNoCheck(Long seqNo, int lbrCode, Session session) {
		String hqlUpdate = "update D001005 set  lastNo=:lastNo where id.lbrCode = :lbrCode and id.catType='ABBRECON'";
		logger.info(hqlUpdate);
		try {
		Integer result = session.createQuery(hqlUpdate).setParameter("lastNo", seqNo.intValue())
				.setParameter("lbrCode", lbrCode).executeUpdate();
		logger.info(result +" Updated Recon no for Branch "+lbrCode+" ="+ seqNo);

		String checkQuery = "from D100001 where id.lbrCode =:lbrCode and id.reconNo =:lastNo";
		logger.info(checkQuery);
		
			D100001 reconRecord = (D100001) session.createQuery(checkQuery)
					.setParameter("lbrCode", lbrCode).setParameter("lastNo", seqNo.intValue()).getSingleResult();
			return reconRecord;
		}catch(NoResultException em) {
			return null;
		}catch(Exception ex) {
			return null;
		}
		
	}
}
