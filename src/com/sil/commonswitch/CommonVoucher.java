package com.sil.commonswitch;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.NoResultException;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.pmw.tinylog.Logger;
import com.sil.constants.Code;
import com.sil.constants.MSGConstants;
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
import com.sil.hbm.D100001;
import com.sil.hbm.MBTRSCROLLSEQ;
import com.sil.hbm.MBTRSETSEQ;
import com.sil.prop.ConfigurationLoader;
import com.sil.util.HBUtil;
import com.sil.util.Props;
public class CommonVoucher {
	public	static org.apache.log4j.Logger logger=org.apache.log4j.Logger.getLogger(CommonVoucher.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("0yyyyMMdd");
	private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmssSS");
	private static final String abbAcc = MSGConstants.ABB_ACC;
	public  boolean isAborted = false;

	public static int getNextSetNo(){
		if (ConfigurationLoader.getParameters(false).getProperty("SQL_SERVER_VERSION").equalsIgnoreCase("2008") 
				&& ConfigurationLoader.getParameters(false).getProperty("DATABASE").equalsIgnoreCase("SQL")) {
			Session session = HBUtil.getSessionFactory().openSession();
			Transaction t = session.beginTransaction();
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
			} finally {
				session.close();
				session = null;
				t = null;
			}
		}else {
			MBTRSETSEQ setSeq = new MBTRSETSEQ();
			try(Session session = HBUtil.getSessionFactory().openSession()){
				session.save(setSeq);
				session.close();
//			session=null;
			}catch(Exception e)
			{
				e.printStackTrace();
			}		
			return setSeq.getId();
		}
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
					try(Session session = HBUtil.getSessionFactory().openSession()) {
						session.save(scrollSeq);
						session.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
					return scrollSeq.getId();	
				}
	}
	
	public static Date getOpenDate(int brCode,Session session)
	{
		Session session2=HBUtil.getSessionFactory().openSession();
		try {
			D001004Id id = new D001004Id();
			id.setCode("LASTOPENDATE");
			id.setLbrCode(brCode);			
			D001004 d04 = session2.get(D001004.class, id);
			System.out.println("d04::>>"+d04);
			session2.close();
			session2=null;
			
			if(d04 != null) {
				return sdf.parse(d04.getValue().trim());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getBatchNameFromBatchCode(String batchCode)
	{	
		try(Session session = HBUtil.getSessionFactory().openSession()) {
			D001004Id id = new D001004Id();
			id.setCode(batchCode);
			id.setLbrCode(0);
			D001004 d04 = session.get(D001004.class, id);
			session.close();
			
			if(d04 != null) {
				return d04.getValue().trim();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;	
	}
	
	public static D010004 getBatch(int lbrCode, String batchName, Date entryDate)
	{
		try(Session session = HBUtil.getSessionFactory().openSession()) {
			D010004Id id = new D010004Id();
			id.setBatchCd(batchName);
			id.setEntryDate(entryDate);
			id.setLbrCode(lbrCode);
			D010004 d04 = session.get(D010004.class, id);
			session.close();
			return d04;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static D010004 getSelectedBatch(int lbrCode, String[] batchCodes, Date entryDate,Session session)
	{
		try {
			for(String batchCode : batchCodes)
			{
				String batchName = getBatchNameFromBatchCode(batchCode);
				if(batchName == null) continue;
				else{
					Logger.info("Current Batch Name : "+batchName);
					D010004 batch = getBatch(lbrCode, batchName, entryDate);
					if(batch == null)
					{
						Logger.info("Batch with Batch Name "+batchName+" Not found");
					}
					else
					{
						if(batch.getStat() ==1 || batch.getStat() == 2){
							Logger.info("Selected batch is "+batchName);
							return batch;
						}
						else continue;
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String getBookType(int lbrCode, String BatchCd)
	{
		//SELECT BookType FROM D010001 WHERE LBrCode = 9 AND Code = 'ABBTR'
		D010001 d001 = null;
		try(Session session = HBUtil.getSessionFactory().openSession()) {
			D010001Id id = new D010001Id();
			id.setCode(BatchCd);
			id.setLbrCode(lbrCode);			
			d001 = session.get(D010001.class, id);
			session.close();
			if(d001 != null) return d001.getBookType();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static short getModuleType(int lbrCode, String prdCd)
	{
		//SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB  
		D009021 d21 = null;
		try(Session session = HBUtil.getSessionFactory().openSession()) {
			D009021Id id = new D009021Id();
			id.setLbrCode(lbrCode);
			id.setPrdCd(prdCd);		
			d21 = session.get(D009021.class, id);
			session.close();
			if(d21 != null) return d21.getModuleType();
 		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static int getUsrCode(String usrCode1)
	{
		D002001 d201 = null;
		try(Session session = HBUtil.getSessionFactory().openSession()) {
			d201 = session.get(D002001.class, usrCode1);
			session.close();
			if(d201 != null) return d201.getUsrCode2();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	
	public void debit(int brCode, String acctId, String tType, int setNo, int scrollNo, String narration, double amount,String rrn,Session session,Transaction t)
	{
		logger.error("<<<<<<<<<<<:::Recieved parameters for debit transaction:::>>>>>>>>>");
		logger.error("brCode::>>>"+brCode);
		logger.error("acctId::>>"+acctId);
		logger.error("tType::>>"+tType);
		logger.error("setNo::>>"+setNo);
		logger.error("scrollNo::>>"+scrollNo);
		logger.error("narration::>>"+narration);
		logger.error("amount::>>"+amount);
		logger.error("RRN::>>"+rrn);
		logger.error("session::>>"+session);
		logger.error("Transaction::>>>"+t);
		try{
			Date openDate = getOpenDate(brCode,session); // Get Open Date
			if(openDate == null){
				Logger.error("Open Date Not Found. Aborting Transaction");
				isAborted = true;
				return;
			}
			Logger.info("Open Date is "+openDate);
			
			//Get BatchCodes from properties file.			
			String batchCode = Props.getBatchProperty(tType);
			Logger.info("Batch Code Form Properties File : "+batchCode);
			String batchCodes[] = batchCode.split("~");
			Logger.info("Number of Batches is "+batchCodes.length+"\t Names are "+Arrays.toString(batchCodes));
			if(batchCodes == null || batchCodes.length <1){
				Logger.error("Batch Codes Not Found in Properties File.");
				isAborted = true;
				return;
			}
			
			/// Get Selected Batch.
			D010004 selectedBatch = getSelectedBatch(brCode, batchCodes, openDate,session);
			if(selectedBatch == null){
				Logger.error("No Active Batch Found.");
				isAborted = true;
				return;
			}
			Logger.info("Selected Batch : "+selectedBatch.getId().getBatchCd());
		
			D009040 d40 = new D009040();
			D009040Id id40 = new D009040Id();
			id40.setBatchCd(selectedBatch.getId().getBatchCd()); //SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016'
			id40.setEntryDate(selectedBatch.getId().getEntryDate()); // SELECT Value FROM D001004 WHERE LBrCode = 9 AND Code = 'LASTOPENDATE'
			id40.setLbrCode(brCode); 
			id40.setSetNo(setNo);
			id40.setScrollNo(scrollNo);
			
			
			d40.setId(id40);
			d40.setMainScrollNo(scrollNo); //Scroll No
			d40.setPostDate(selectedBatch.getPostDate()); //SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
			d40.setFeffDate(selectedBatch.getFeffDate()); // SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
			d40.setActivityType("DR"); 
			d40.setCashFlowType("DR");
			d40.setValueDate(selectedBatch.getPostDate()); // postdate SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
			
			String bookType  = getBookType(brCode, selectedBatch.getId().getBatchCd());
			if(bookType == null){
				Logger.error("BookType Not Found.");
				isAborted = true;
				return;
			}
			
			Logger.info("BookType is : "+bookType);
			d40.setBookType(bookType); //SELECT BookType FROM D010001 WHERE LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('D');
			d40.setVcrAcctId(acctId);
			d40.setMainAcctId(acctId);
			
			short moduleType = getModuleType(brCode, acctId.substring(0,8).trim());
			if(moduleType == 0){
				Logger.error("ModuleType Not Found.");
				isAborted = true;
				return;
			}
			Logger.info("ModuleType is : "+moduleType);
			d40.setMainModType(moduleType); // SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB      
			d40.setVcrModType(moduleType); // SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB      
			d40.setTrnCurCd("INR");
			d40.setFcyTrnAmt(amount);
			d40.setLcyConvRate(1);
			d40.setLcyTrnAmt(amount);
			d40.setInstrBankCd((short) 0);
			d40.setInstrBranchCd((short) 0);
			d40.setInstrType((short) 99); // Depend on chanel
			d40.setInstrNo(rrn);	// RRN
			d40.setInstrDate(new Date()); //Blank
			d40.setParticulars(narration); //param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); // 
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');
			
			int  usrCode2 = getUsrCode("WEB");

			if(usrCode2 == 0){
				Logger.error("UsrCode Not Found.");
				isAborted = true;
				return;
			}
			Logger.info("UsrCode is : "+usrCode2);
			d40.setMaker(usrCode2); // SELECT UsrCode2 FROM D002001 WHERE UsrCode1 = 'WEB' 
			d40.setMakerDate(selectedBatch.getId().getEntryDate()); // EntryDate 
			d40.setMakerTime(Integer.parseInt(timeFormat.format(new Date()).substring(0, 8))); 
			d40.setChecker1(usrCode2); // SELECT UsrCode2 FROM D002001 WHERE UsrCode1 = 'WEB' 
			d40.setChecker2(0);
			d40.setChecker3(0);
			d40.setChecker4(0);
			d40.setCheckerDate(selectedBatch.getId().getEntryDate()); // EntryDate
			d40.setCheckerTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8)));
			
			d40.setNoAuthPending((byte) 0);  
			d40.setNoAuthOver((byte) 1); 
			d40.setPostFlag('P'); 
			d40.setAuthFlag('A'); 
			d40.setFeffFlag('F'); 
			d40.setCanceledFlag(' '); 
			d40.setPostAuthFeffCncl((byte) 0); 
			d40.setUpdtChkId((short) 0); 
			d40.setPartClearAmt(0); 
			d40.setPostTime(Integer.parseInt(timeFormat.format(new Date()).substring(0, 8))); // Time HHMMSSTT
//			balance(brCode, acctId, amount, "D");
			session.save(d40);
			session.flush();
	
		} catch (Exception e) {
			isAborted = true;
			e.printStackTrace();
		}		
		
	}
	
	public void debitSameBranch(int brCode, String acctId, String tType, int setNo, int scrollNo, String narration, double amount,String rrn,int mainScrollNo,Session session,Transaction t)
	{
		try{
			Date openDate = getOpenDate(brCode,session); 
			if(openDate == null){
				Logger.error("Open Date Not Found. Aborting Transaction");
				isAborted = true;
				return;
			}
			Logger.info("Open Date is "+openDate);
			
			//Get BatchCodes from properties file.			
			String batchCode = Props.getBatchProperty(tType);
			Logger.info("Batch Code Form Properties File : "+batchCode);
			String batchCodes[] = batchCode.split("~");
			Logger.info("Number of Batches is "+batchCodes.length+"\t Names are "+Arrays.toString(batchCodes));
			if(batchCodes == null || batchCodes.length <1){
				Logger.error("Batch Codes Not Found in Properties File.");
				isAborted = true;
				return;
			}
			/// Get Selected Batch.
			D010004 selectedBatch = getSelectedBatch(brCode, batchCodes, openDate,session);
			if(selectedBatch == null){
				Logger.error("No Active Batch Found.");
				isAborted = true;
				return;
			}
			Logger.info("Selected Batch : "+selectedBatch.getId().getBatchCd());
		
			D009040 d40 = new D009040();
			D009040Id id40 = new D009040Id();
			id40.setBatchCd(selectedBatch.getId().getBatchCd()); //SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016'
			id40.setEntryDate(selectedBatch.getId().getEntryDate()); // SELECT Value FROM D001004 WHERE LBrCode = 9 AND Code = 'LASTOPENDATE'
			id40.setLbrCode(brCode); 
			id40.setSetNo(setNo);
			id40.setScrollNo(scrollNo);
			d40.setId(id40);
			d40.setMainScrollNo(mainScrollNo); //Scroll No
			d40.setPostDate(selectedBatch.getPostDate()); //SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
			d40.setFeffDate(selectedBatch.getFeffDate()); // SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
			d40.setActivityType("DR"); 
			d40.setCashFlowType("DR");
			d40.setValueDate(selectedBatch.getPostDate()); // postdate SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
			
			String bookType  = getBookType(brCode, selectedBatch.getId().getBatchCd());
			if(bookType == null){
				Logger.error("BookType Not Found.");
				isAborted = true;
				return;
			}
			
			Logger.info("BookType is : "+bookType);
			d40.setBookType(bookType); //SELECT BookType FROM D010001 WHERE LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('D');
			d40.setVcrAcctId(acctId);
			d40.setMainAcctId(acctId);
			
			short moduleType = getModuleType(brCode, acctId.substring(0,8).trim());
			if(moduleType == 0){
				Logger.error("ModuleType Not Found.");
				isAborted = true;
				return;
			}
			Logger.info("ModuleType is : "+moduleType);
			d40.setMainModType(moduleType); // SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB      
			d40.setVcrModType(moduleType); // SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB      
			d40.setTrnCurCd("INR");
			d40.setFcyTrnAmt(amount);
			d40.setLcyConvRate(1);
			d40.setLcyTrnAmt(amount);
			d40.setInstrBankCd((short) 0);
			d40.setInstrBranchCd((short) 0);
			d40.setInstrType((short) 99); // Depend on chanel
			d40.setInstrNo(rrn);	// RRN
			d40.setInstrDate(new Date()); //Blank
			d40.setParticulars(narration); //param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); // 
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');
			
			int  usrCode2 = getUsrCode("WEB");
			if(usrCode2 == 0){
				Logger.error("UsrCode Not Found.");
				isAborted = true;
				return;
			}
			Logger.info("UsrCode is : "+usrCode2);
			d40.setMaker(usrCode2); // SELECT UsrCode2 FROM D002001 WHERE UsrCode1 = 'WEB' 
			d40.setMakerDate(selectedBatch.getId().getEntryDate()); // EntryDate 
			d40.setMakerTime(Integer.parseInt(timeFormat.format(new Date()).substring(0, 8))); 
			d40.setChecker1(usrCode2); // SELECT UsrCode2 FROM D002001 WHERE UsrCode1 = 'WEB' 
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
			d40.setPostTime(Integer.parseInt(timeFormat.format(new Date()).substring(0, 8))); // Time HHMMSSTT
			session.save(d40);
			session.flush();
			
			Query query = session.createQuery("UPDATE D010004 SET TotalDrVcrs=TotalDrVcrs+1, TotalDrAmtLcy = TotalDrAmtLcy +:amt , TotalCrVcrs = TotalCrVcrs+1, TotalCrAmtLcy= TotalCrAmtLcy + :amt  WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd");
			query.setParameter("amt", amount);
			query.setParameter("lbrcode", brCode);
			query.setParameter("entrydate", openDate);
			query.setParameter("batchcd", selectedBatch.getId().getBatchCd());
			query.executeUpdate();
			session.flush();
		} catch (Exception e) {
			isAborted = true;
			e.printStackTrace();
		}		
		
	}
	private void credit(int brCode, String acctId, String tType, int setNo, int scrollNo, String narration,double amount,String rrn,Session session,Transaction t)
	{
		logger.error("<<<<<<<<<<<:::Recieved Credit parameters:::>>>>>>>>>");
		logger.error("brCode::>>>"+brCode);
		logger.error("acctId::>>"+acctId);
		logger.error("tType::>>"+tType);
		logger.error("setNo::>>"+setNo);
		logger.error("scrollNo::>>"+scrollNo);
		logger.error("narration::>>"+narration);
		logger.error("amount::>>"+amount);
		logger.error("RRN::>>"+rrn);
		logger.error("session::>>"+session);
		logger.error("Transaction::>>>"+t);
		try{
			Date openDate = getOpenDate(brCode,session); // Get Open Date
			if(openDate == null){
				Logger.error("Open Date Not Found. Aborting Transaction");
				isAborted = true;
				return;
			}
			Logger.info("Open Date is "+openDate);
			//Get BatchCodes from properties file.			
			String batchCode = Props.getBatchProperty(tType);
			Logger.info("Batch Code Form Properties File : "+batchCode);
			String batchCodes[] = batchCode.split("~");
			Logger.info("Number of Batches is "+batchCodes.length+"\t Names are "+Arrays.toString(batchCodes));
			if(batchCodes == null || batchCodes.length <1){
				Logger.error("Batch Codes Not Found in Properties File.");
				isAborted = true;
				return;
			}
			
			/// Get Selected Batch.
			D010004 selectedBatch = getSelectedBatch(brCode, batchCodes, openDate,session);
			if(selectedBatch == null){
				Logger.error("No Active Batch Found.");
				isAborted = true;
				return;
			}
			
			Logger.info("Selected Batch : "+selectedBatch.getId().getBatchCd());
			D009040 d40 = new D009040();
			D009040Id id40 = new D009040Id();
			id40.setBatchCd(selectedBatch.getId().getBatchCd()); //SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016'
			
			id40.setEntryDate(selectedBatch.getId().getEntryDate()); // SELECT Value FROM D001004 WHERE LBrCode = 9 AND Code = 'LASTOPENDATE'
			id40.setLbrCode(brCode); 
			id40.setSetNo(setNo);
			id40.setScrollNo(scrollNo);

			d40.setId(id40);
			d40.setMainScrollNo(scrollNo); //Scroll No
			d40.setPostDate(selectedBatch.getPostDate()); //SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
			d40.setFeffDate(selectedBatch.getFeffDate()); // SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
			d40.setActivityType("CR"); 
			d40.setCashFlowType("CR");
			d40.setValueDate(selectedBatch.getPostDate()); // postdate SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
			
			String bookType  = getBookType(brCode, selectedBatch.getId().getBatchCd());
			if(bookType == null){
				Logger.error("BookType Not Found.");
				isAborted = true;
				return;
			}
			Logger.info("BookType is : "+bookType);
			d40.setBookType(bookType); //SELECT BookType FROM D010001 WHERE LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('C');
			d40.setVcrAcctId(acctId);
			d40.setMainAcctId(acctId);
			
			short moduleType = getModuleType(brCode, acctId.substring(0,8).trim());
			if(moduleType == 0){
				Logger.error("ModuleType Not Found.");
				isAborted = true;
				return;
			}
			Logger.info("ModuleType is : "+moduleType);
			d40.setMainModType(moduleType); // SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB      
			d40.setVcrModType(moduleType); // SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB      
			d40.setTrnCurCd("INR");
			d40.setFcyTrnAmt(amount);
			d40.setLcyConvRate(1);
			d40.setLcyTrnAmt(amount);
			d40.setInstrBankCd((short) 0);
			d40.setInstrBranchCd((short) 0);
			d40.setInstrType((short) 99); // Depend on chanel
			d40.setInstrNo(rrn);	// RRN
			d40.setInstrDate(new Date()); //Blank
			d40.setParticulars(narration); //param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); // 
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');
			
			int  usrCode2 = getUsrCode("WEB");
			if(usrCode2 == 0){
				Logger.error("UsrCode Not Found.");
				isAborted = true;
				return;
			}
			Logger.info("UsrCode is : "+usrCode2);
			d40.setMaker(usrCode2); // SELECT UsrCode2 FROM D002001 WHERE UsrCode1 = 'WEB' 
			d40.setMakerDate(selectedBatch.getId().getEntryDate()); // EntryDate 
			d40.setMakerTime(Integer.parseInt(timeFormat.format(new Date()).substring(0, 8))); 
			d40.setChecker1(usrCode2); // SELECT UsrCode2 FROM D002001 WHERE UsrCode1 = 'WEB' 
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
			d40.setPostTime(Integer.parseInt(timeFormat.format(new Date()).substring(0, 8)));
//			balance(brCode, acctId, amount, "C");
			System.out.println("credit isAborted::>>"+isAborted);
			logger.error("credit isAborted::>>"+isAborted);
			session.save(d40);
			session.flush(); 
		} catch (Exception e) {
			isAborted = true;
			e.printStackTrace();
		}		
		
	}
	
	public void creditSameBranch(int brCode, String acctId, String tType, int setNo, int scrollNo, String narration,double amount,String rrn,int mainSrollNo,Session session,Transaction t)
	{
		try{
			Date openDate = getOpenDate(brCode,session); // Get Open Date
			if(openDate == null){
				Logger.error("Open Date Not Found. Aborting Transaction");
				isAborted = true;
				return;
			}
			Logger.info("Open Date is "+openDate);

			//Get BatchCodes from properties file.			
			String batchCode = Props.getBatchProperty(tType);
			Logger.info("Batch Code Form Properties File : "+batchCode);
			String batchCodes[] = batchCode.split("~");
			Logger.info("Number of Batches is "+batchCodes.length+"\t Names are "+Arrays.toString(batchCodes));
			if(batchCodes == null || batchCodes.length <1){
				Logger.error("Batch Codes Not Found in Properties File.");
				isAborted = true;
				return;
			}

			/// Get Selected Batch.
			D010004 selectedBatch = getSelectedBatch(brCode, batchCodes, openDate,session);
			if(selectedBatch == null){
				Logger.error("No Active Batch Found.");
				isAborted = true;
				return;
			}

			Logger.info("Selected Batch : "+selectedBatch.getId().getBatchCd());

			D009040 d40 = new D009040();
			D009040Id id40 = new D009040Id();
			id40.setBatchCd(selectedBatch.getId().getBatchCd()); //SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016'

			id40.setEntryDate(selectedBatch.getId().getEntryDate()); // SELECT Value FROM D001004 WHERE LBrCode = 9 AND Code = 'LASTOPENDATE'
			id40.setLbrCode(brCode); 
			id40.setSetNo(setNo);
			id40.setScrollNo(scrollNo);
			d40.setId(id40);
			d40.setMainScrollNo(mainSrollNo); //Scroll No
			d40.setPostDate(selectedBatch.getPostDate()); //SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
			d40.setFeffDate(selectedBatch.getFeffDate()); // SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
			d40.setActivityType("CR"); 
			d40.setCashFlowType("CR");
			d40.setValueDate(selectedBatch.getPostDate()); // postdate SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'

			String bookType  = getBookType(brCode, selectedBatch.getId().getBatchCd());
			if(bookType == null){
				Logger.error("BookType Not Found.");
				isAborted = true;
				return;
			}
			Logger.info("BookType is : "+bookType);
			d40.setBookType(bookType); //SELECT BookType FROM D010001 WHERE LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('C');
			d40.setVcrAcctId(acctId);
			d40.setMainAcctId(acctId);

			short moduleType = getModuleType(brCode, acctId.substring(0,8).trim());
			if(moduleType == 0){
				Logger.error("ModuleType Not Found.");
				isAborted = true;
				return;
			}
			Logger.info("ModuleType is : "+moduleType);
			d40.setMainModType(moduleType); // SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB      
			d40.setVcrModType(moduleType); // SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB      
			d40.setTrnCurCd("INR");
			d40.setFcyTrnAmt(amount);
			d40.setLcyConvRate(1);
			d40.setLcyTrnAmt(amount);
			d40.setInstrBankCd((short) 0);
			d40.setInstrBranchCd((short) 0);
			d40.setInstrType((short) 99); // Depend on chanel
			d40.setInstrNo(rrn);	// RRN
			d40.setInstrDate(new Date()); //Blank
			d40.setParticulars(narration); //param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); // 
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');

			int  usrCode2 = getUsrCode("WEB");
			if(usrCode2 == 0){
				Logger.error("UsrCode Not Found.");
				isAborted = true;
				return;
			}
			Logger.info("UsrCode is : "+usrCode2);
			d40.setMaker(usrCode2); // SELECT UsrCode2 FROM D002001 WHERE UsrCode1 = 'WEB' 
			d40.setMakerDate(selectedBatch.getId().getEntryDate()); // EntryDate 
			d40.setMakerTime(Integer.parseInt(timeFormat.format(new Date()).substring(0, 8))); 
			d40.setChecker1(usrCode2); // SELECT UsrCode2 FROM D002001 WHERE UsrCode1 = 'WEB' 
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
			d40.setPostTime(Integer.parseInt(timeFormat.format(new Date()).substring(0, 8))); // Time HHMMSSTT
			session.save(d40);
			session.flush();
		} catch (Exception e) {
			isAborted = true;
			e.printStackTrace();
		}		
	}
	
	private void creditABB(int brCode, String acctId, String tType, int setNo, int scrollNo, String narration, int tbrCode,double amount,String rrn,int reconNo,Session session,Transaction t)
	{
		try{
			Date openDate = getOpenDate(brCode,session); // Get Open Date
			if(openDate == null){
				Logger.error("Open Date Not Found. Aborting Transaction");
				isAborted = true;
				return;
			}
			Logger.info("Open Date is "+openDate);
			//Get BatchCodes from properties file.			
			String batchCode = Props.getBatchProperty(tType);
			Logger.info("Batch Code Form Properties File : "+batchCode);
			String batchCodes[] = batchCode.split("~");
			Logger.info("Number of Batches is "+batchCodes.length+"\t Names are "+Arrays.toString(batchCodes));
			if(batchCodes == null || batchCodes.length <1){
				Logger.error("Batch Codes Not Found in Properties File.");
				isAborted = true;
				return;
			}

			/// Get Selected Batch.
			D010004 selectedBatch = getSelectedBatch(brCode, batchCodes, openDate,session);
			if(selectedBatch == null){
				Logger.error("No Active Batch Found.");
				isAborted = true;
				return;
			}

			Logger.info("Selected Batch : "+selectedBatch.getId().getBatchCd());

			D009040 d40 = new D009040();
			D009040Id id40 = new D009040Id();
			id40.setBatchCd(selectedBatch.getId().getBatchCd()); //SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016'

			id40.setEntryDate(selectedBatch.getId().getEntryDate()); // SELECT Value FROM D001004 WHERE LBrCode = 9 AND Code = 'LASTOPENDATE'
			id40.setLbrCode(brCode); 
			id40.setSetNo(setNo);
			id40.setScrollNo(scrollNo);

			d40.setId(id40);
			d40.setMainScrollNo(scrollNo); //Scroll No
			d40.setPostDate(selectedBatch.getPostDate()); //SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
			d40.setFeffDate(selectedBatch.getFeffDate()); // SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
			d40.setActivityType("ABB"); 
			d40.setCashFlowType("ABBCR");
			d40.setValueDate(selectedBatch.getPostDate()); // postdate SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'

			String bookType  = getBookType(brCode, selectedBatch.getId().getBatchCd());
			if(bookType == null){
				Logger.error("BookType Not Found.");
				isAborted = true;
				return;
			}
			Logger.info("BookType is : "+bookType);
			d40.setBookType(bookType); //SELECT BookType FROM D010001 WHERE LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('C');
			d40.setVcrAcctId(acctId);
			d40.setMainAcctId(acctId);

			short moduleType = getModuleType(brCode, acctId.substring(0,8).trim());

			if(moduleType == 0){
				Logger.error("ModuleType Not Found.");
				moduleType=100;
				return;
			}
			Logger.info("ModuleType is : "+moduleType);
			d40.setMainModType(moduleType); // SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB      
			d40.setVcrModType(moduleType); // SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB      
			d40.setTrnCurCd("INR");
			d40.setFcyTrnAmt(amount);
			d40.setLcyConvRate(1);
			d40.setLcyTrnAmt(amount);
			d40.setInstrBankCd((short) brCode);
			d40.setInstrBranchCd((short) tbrCode);
			d40.setInstrType((short) 99); // Depend on chanel
			d40.setInstrNo(rrn);	// RRN
			d40.setInstrDate(new Date()); //Blank
			d40.setParticulars(narration); //param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); // 
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');
			d40.setTokenNo(reconNo);

			int  usrCode2 = getUsrCode("WEB");
			if(usrCode2 == 0){
				Logger.error("UsrCode Not Found.");
				isAborted = true;
				return;
			}
			Logger.info("UsrCode is : "+usrCode2);
			d40.setMaker(usrCode2); // SELECT UsrCode2 FROM D002001 WHERE UsrCode1 = 'WEB' 
			d40.setMakerDate(selectedBatch.getId().getEntryDate()); // EntryDate 
			d40.setMakerTime(Integer.parseInt(timeFormat.format(new Date()).substring(0, 8))); 
			d40.setChecker1(usrCode2); // SELECT UsrCode2 FROM D002001 WHERE UsrCode1 = 'WEB' 
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
			d40.setPostTime(Integer.parseInt(timeFormat.format(new Date()).substring(0, 8))); // Time HHMMSSTT

			updateBatchIntraBr(amount,brCode ,selectedBatch.getId().getBatchCd() ,openDate );
			session.save(d40);
			session.flush();
		} catch (Exception e) {
			isAborted = true;
			e.printStackTrace();
		}		
	}
	
	
	private void debitABB(int brCode, String acctId, String tType, int setNo, int scrollNo, String narration,double amount,String rrn ,int reconNo,Session session,Transaction t)
	{
		logger.error("<<<<<<<<<<<:::Recieved debitABB parameters:::>>>>>>>>>");
		logger.error("brCode::>>>"+brCode);
		logger.error("acctId::>>"+acctId);
		logger.error("tType::>>"+tType);
		logger.error("setNo::>>"+setNo);
		logger.error("scrollNo::>>"+scrollNo);
		logger.error("narration::>>"+narration);
		logger.error("amount::>>"+amount);
		logger.error("RRN::>>"+rrn);
		logger.error("session::>>"+session);
		logger.error("Transaction::>>>"+t);
		try{
			Date openDate = getOpenDate(brCode,session); // Get Open Date
			if(openDate == null){
				Logger.error("Open Date Not Found. Aborting Transaction");
				isAborted = true;
				return;
			}
			Logger.info("Open Date is "+openDate);
			String batchCode = Props.getBatchProperty(tType);
			Logger.info("Batch Code Form Properties File : "+batchCode);
			String batchCodes[] = batchCode.split("~");
			Logger.info("Number of Batches is "+batchCodes.length+"\t Names are "+Arrays.toString(batchCodes));
			if(batchCodes == null || batchCodes.length <1){
				Logger.error("Batch Codes Not Found in Properties File.");
				isAborted = true;
				return;
			}
			
			/// Get Selected Batch.
			D010004 selectedBatch = getSelectedBatch(brCode, batchCodes, openDate,session);
			if(selectedBatch == null){
				Logger.error("No Active Batch Found.");
				isAborted = true;
				return;
			}
			
			Logger.info("Selected Batch : "+selectedBatch.getId().getBatchCd());
			
			D009040 d40 = new D009040();
			D009040Id id40 = new D009040Id();
			id40.setBatchCd(selectedBatch.getId().getBatchCd()); //SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016'
			
			
			
			id40.setEntryDate(selectedBatch.getId().getEntryDate()); // SELECT Value FROM D001004 WHERE LBrCode = 9 AND Code = 'LASTOPENDATE'
			id40.setLbrCode(brCode); 
			id40.setSetNo(setNo);
			
			id40.setScrollNo(scrollNo);
			
			
			d40.setId(id40);
			d40.setMainScrollNo(scrollNo); //Scroll No
			d40.setPostDate(selectedBatch.getPostDate()); //SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
			d40.setFeffDate(selectedBatch.getFeffDate()); // SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
			d40.setActivityType("ABBREM"); 
			d40.setCashFlowType("ABBDR");
			d40.setValueDate(selectedBatch.getPostDate()); // postdate SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016' AND BatchCd = 'ABBTR'
			
			String bookType  = getBookType(brCode, selectedBatch.getId().getBatchCd());
			if(bookType == null){
				Logger.error("BookType Not Found.");
				isAborted = true;
				return;
			}
			Logger.info("BookType is : "+bookType);
			d40.setBookType(bookType); //SELECT BookType FROM D010001 WHERE LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('D');
			d40.setVcrAcctId(acctId);
			d40.setMainAcctId(acctId);
			
			short moduleType = getModuleType(brCode, acctId.substring(0,8).trim());
			if(moduleType == 0){
				Logger.error("ModuleType Not Found.");
				isAborted = true;
				return;
			}
			Logger.info("ModuleType is : "+moduleType);
			d40.setMainModType(moduleType); // SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB      
			d40.setVcrModType(moduleType); // SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB      
			d40.setTrnCurCd("INR");
			d40.setFcyTrnAmt(amount);
			d40.setLcyConvRate(1);
			d40.setLcyTrnAmt(amount);
			d40.setInstrBankCd((short) 0);
			d40.setInstrBranchCd((short) 0);
			d40.setInstrType((short) 99); // Depend on chanel
			d40.setInstrNo(rrn);	// RRN
			d40.setInstrDate(new Date()); //Blank
			d40.setParticulars(narration); //param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); // 
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');
			d40.setTokenNo(reconNo);
			int  usrCode2 = getUsrCode("WEB");
			if(usrCode2 == 0){
				Logger.error("UsrCode Not Found.");
				isAborted = true;
				return;
			}
			Logger.info("UsrCode is : "+usrCode2);
			d40.setMaker(usrCode2); // SELECT UsrCode2 FROM D002001 WHERE UsrCode1 = 'WEB' 
			d40.setMakerDate(selectedBatch.getId().getEntryDate()); // EntryDate 
			d40.setMakerTime(Integer.parseInt(timeFormat.format(new Date()).substring(0, 8))); 
			d40.setChecker1(usrCode2); // SELECT UsrCode2 FROM D002001 WHERE UsrCode1 = 'WEB' 
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
			d40.setPostTime(Integer.parseInt(timeFormat.format(new Date()).substring(0, 8))); // Time HHMMSSTT
			session.save(d40);
			session.flush();
		} catch (Exception e) {
			isAborted = true;
			e.printStackTrace();
		}		
		
	}
	private void updateBatchIntraBr(double amount,int lbrCode,String batch,Date entryDate)
	{
		try {
			Session session=HBUtil.getSessionFactory().openSession();
			Transaction t=session.beginTransaction();
			Query query = session.createQuery("UPDATE D010004 SET TotalDrVcrs=TotalDrVcrs+1, TotalDrAmtLcy = TotalDrAmtLcy +:amt , TotalCrVcrs = TotalCrVcrs+1, TotalCrAmtLcy= TotalCrAmtLcy + :amt  WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd");
			query.setParameter("amt", amount);
			query.setParameter("lbrcode", lbrCode);
			query.setParameter("entrydate", entryDate);
			query.setParameter("batchcd", batch);
			query.executeUpdate();
			t.commit();
			session.close();
			session=null;
			
		} catch (Exception e) {
			e.printStackTrace();
			isAborted = true;
		}
	}

	@SuppressWarnings("deprecation")
	private int getNextReconNo(int brCode)
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

	
	public boolean balance(int remBrCode,String remPrdAcctId,double amount,String drcr)
	{
		Logger.info("Transaction Amount : "+amount);
		Session session=HBUtil.getSessionFactory().openSession();
		Transaction t=session.beginTransaction();
		D009022Id remId = new D009022Id();
		remId.setLbrCode(remBrCode);
		remId.setPrdAcctId(remPrdAcctId);
		D009022 remAcct = session.get(D009022.class, remId);
		try {
			if(drcr.equalsIgnoreCase("D"))
			{
				Logger.info("Rem Amount Before : "+remAcct.getActClrBalFcy());
				if((remAcct.getActClrBalFcy()-remAcct.getTotalLienFcy()) > amount){
					Logger.info("Final Amount : "+(remAcct.getActClrBalFcy()-amount));
					remAcct.setActClrBalFcy(remAcct.getActClrBalFcy()-amount);
					remAcct.setShdClrBalFcy(remAcct.getShdClrBalFcy()-amount);
					remAcct.setShdTotBalFcy(remAcct.getShdTotBalFcy()-amount);
					remAcct.setActTotBalFcy(remAcct.getActTotBalFcy()-amount);
					remAcct.setActTotBalLcy(remAcct.getActTotBalLcy()-amount);
					session.update(remAcct);
					t.commit();
					session.close();
					session=null;
					t=null;
					return true;
				}
				else{
					logger.error("Insufficient Funds ");
					isAborted = true;
					session.close();
					session=null;
					t=null;
					return false;
				}	
			}else if(drcr.equalsIgnoreCase("C"))
			{
				Logger.info("Rem Amount Before : "+remAcct.getActClrBalFcy());
				Logger.info("Final Amount : "+(remAcct.getActClrBalFcy()+amount));

				remAcct.setActClrBalFcy(remAcct.getActClrBalFcy()+amount);
				remAcct.setShdClrBalFcy(remAcct.getShdClrBalFcy()+amount);
				remAcct.setShdTotBalFcy(remAcct.getShdTotBalFcy()+amount);
				remAcct.setActTotBalFcy(remAcct.getActTotBalFcy()+amount);
				remAcct.setActTotBalLcy(remAcct.getActTotBalLcy()+amount);
				session.update(remAcct);
				t.commit();
				session.close();
				session=null;
				t=null;
				isAborted = true;
				return true;
			}else
			{
				session.close();
				t=null;
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			isAborted = true;
			return false;
		}
	}
	public static HashMap<String, String> otherBankVoucherEntry(int lbrCode,String accNo,String transType,String narration,double amount,String rrn,Session session,Transaction t)
	{
		CommonVoucher common=new CommonVoucher(); 
		HashMap<String, String> resultMap=new HashMap<>();
		int setNo=getNextSetNo();
		int scrollNo=getNextScrollNo();
		int reconNo=common.getNextReconNo(2);
		common.debit(lbrCode, accNo, transType.toUpperCase(), setNo,scrollNo ,narration, amount, rrn,session,t);
		if(!common.isAborted)
		{
			logger.error("Transaction successful");
			common.creditABB(lbrCode, MSGConstants.ABB_ACC, "ABB", setNo, getNextScrollNo(), narration, lbrCode, amount, rrn,reconNo,session,t);
			if(!common.isAborted)
			{
				int setNoABB=getNextSetNo();
				logger.error("ABB Transaction successful");
				String crAccno=get32DigitAcctNo(getSysParameter(MSGConstants.MBRNCRACT,session).trim(),0,0);//+"    000000000000000000000000";
				common.credit(2, crAccno, transType,setNoABB , getNextScrollNo(), narration, amount, rrn,session,t);
				if(!common.isAborted)
				{
					logger.error("Other Bank GL Transaction successful");
					common.debitABB(2, MSGConstants.ABB_ACC, "ABB", setNoABB, getNextScrollNo(),narration, amount, rrn,reconNo,session,t);
					resultMap.put(Code.RESULT, Code.SUCCESS);
					resultMap.put(Code.SETNO, String.valueOf(setNo));
					resultMap.put(Code.SCROLLNO, String.valueOf(scrollNo));
//					new VoucherCommon().balance(lbrCode, accNo, amount, "D");
					CommonVoucher.balanceUpdate(lbrCode, accNo, amount, "D");
					return resultMap;
				}else
				{
					logger.error("ABB Transaction unsuccessful");
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "");
					resultMap.put(Code.SCROLLNO, "");
					return resultMap;
				}
			}else
			{
				logger.error("ABB Transaction unsuccessful");
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "");
				resultMap.put(Code.SCROLLNO, "");
				return resultMap;
			}
		}else
		{
			logger.error("Transaction unsuccessful");
			resultMap.put(Code.RESULT, Code.ERROR);
			resultMap.put(Code.SETNO, "");
			resultMap.put(Code.SCROLLNO, "");
			return resultMap;
		}
	}
	public static HashMap<String, String> otherBankCreditVoucherEntry(int lbrCode,String accNo,String transType,String narration,
			double amount,String rrn,Session session,Transaction t)
	{
		logger.error("<<<<<<<<<<::: otherBankCreditVoucherEntry :::>>>>>>>>>>>>>");
		logger.error("lbrCode:>>>"+lbrCode+" accNo::>>>"+accNo+" transType::>>>"+transType+" narration::>>>"+narration+" amount::>>"+amount+" rrn::>>>"+rrn+" session:>>>"+session+" Transaction ::>>"+t+" session.isOpen()::>>>"+session.isOpen()+" t.isActive()"+t.isActive());

		CommonVoucher common=new CommonVoucher();
		HashMap<String, String> resultMap=new HashMap<>();
		if(lbrCode==2)
		{
			logger.error("Same Branch Fund Transfer....");
			int setNo=getNextSetNo();
			int mainScrollNo=getNextScrollNo();
			common.creditSameBranch(lbrCode, accNo, transType.toUpperCase(), setNo, getNextScrollNo(),narration, amount, rrn,mainScrollNo,session,t);
			CoreTransactionMPOS.balance(lbrCode,accNo.trim(), amount, "C",session);
			if(!common.isAborted)
			{
				logger.error("Transaction successful");
				String crAccno=get32DigitAcctNo(getSysParameter(MSGConstants.MBBBILLPAY_CR_ACC,session).trim(),0,0);//+"    000000000000000000000000";
				common.debitSameBranch(lbrCode, crAccno, transType, setNo , getNextScrollNo(), narration, amount, rrn,mainScrollNo,session,t);
				resultMap.put(Code.RESULT, Code.SUCCESS);
				resultMap.put(Code.SETNO, String.valueOf(setNo));
				resultMap.put(Code.SCROLLNO, String.valueOf(mainScrollNo));
//				return resultMap;
			}else
			{
				logger.error("Transaction unsuccessful");
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "");
				resultMap.put(Code.SCROLLNO,"");
//				return resultMap;
			}
		}
		else
		{
			logger.error("Other Branch transaction");
			int setNo=getNextSetNo();
			int scrollNo=getNextScrollNo();
			int reconNo=common.getNextReconNo(2);
			common.credit(lbrCode, accNo, transType.toUpperCase(), setNo,scrollNo ,narration, amount, rrn,session,t);
			System.out.println("common.isAborted::>>"+common.isAborted);
			if(!common.isAborted)
			{
				logger.error("Transaction successful");
				common.debitABB(lbrCode, MSGConstants.ABB_ACC, "ABB", setNo, getNextScrollNo(), narration, amount, rrn,reconNo,session,t);
				logger.error("common.isAborted::>>"+common.isAborted);
				if(!common.isAborted)
				{
					int setNoABB=getNextSetNo();
					logger.error("ABB Transaction successful");
					System.out.println(" ABB common.isAborted::>>"+common.isAborted);
					String crAccno=get32DigitAcctNo(getSysParameter(MSGConstants.MBRNCRACT,session).trim(),0,0);//+"    000000000000000000000000";
					common.debit(2, crAccno, transType,setNoABB , getNextScrollNo(), narration, amount, rrn,session,t);
					logger.error("common.isAborted::>>"+common.isAborted);
					System.out.println("common.isAborted::>>>"+common.isAborted);
					if(!common.isAborted)
					{
						logger.error("Other Bank GL Transaction successful");
						common.creditABB(2, MSGConstants.ABB_ACC, "ABB", setNoABB, getNextScrollNo(),narration, 2,amount, rrn,reconNo,session,t);
						t.commit();
						session.close();
						session=null;
						t=null;
						resultMap.put(Code.RESULT, Code.SUCCESS);
						resultMap.put(Code.SETNO, String.valueOf(setNo));
						resultMap.put(Code.SCROLLNO, String.valueOf(scrollNo));
//						CommonVoucher.balanceUpdate(lbrCode, accNo, amount, "D");
						System.out.println("resultMap::>>"+resultMap);
						logger.error("resultMap::>>>"+resultMap);
//						return resultMap;
					}else
					{
						logger.error("ABB Transaction unsuccessful");
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "");
						resultMap.put(Code.SCROLLNO, "");
//						return resultMap;
					}
				}else
				{
					logger.error("ABB Transaction unsuccessful");
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "");
					resultMap.put(Code.SCROLLNO, "");
//					return resultMap;
				}
			}else
			{
				logger.error("Transaction unsuccessful");
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "");
				resultMap.put(Code.SCROLLNO, "");
//				return resultMap;
			}
		}
		logger.error(" Final Voucher Result::>>>"+resultMap);
		return resultMap;
	}
	public static HashMap<String, String> otherBankReverseVoucherEntry(int lbrCode,String accNo,String transType,String narration,double amount,String rrn)
	{
		Session session=HBUtil.getSessionFactory().openSession();
		Transaction t=session.beginTransaction();
		CommonVoucher common=new CommonVoucher();
		HashMap<String, String> resultMap=new HashMap<>();
		int setNo=getNextSetNo();
		int scrollNo=getNextScrollNo();
		int drBrcode=Integer.valueOf(getSysParameter("MBBILLPAYBR",session).trim());
		int crBrcode=Integer.valueOf(getSysParameter("MBBILLPAYBRCR",session).trim());
		int reconNo=common.getNextReconNo(2);
//		System.out.println("lbrCode::>>"+lbrCode);
//		System.out.println("accNo::>>"+accNo);
		logger.error("accNo::>>"+accNo);
		logger.error("lbrCode::>>"+lbrCode);
		common.credit(lbrCode, accNo, transType.toUpperCase(), setNo, scrollNo,narration, amount, rrn,session,t);
		if(!common.isAborted)
		{
			logger.error("Transaction successful");
			common.debitABB(lbrCode, MSGConstants.ABB_ACC, "ABB", setNo, scrollNo, narration, amount, rrn,reconNo,session,t);
			if(!common.isAborted)
			{
				int setNoABB=getNextSetNo();
				logger.error("ABB Transaction successful");
				String drAccno=get32DigitAcctNo(getSysParameter("MBBILLPAY",session).trim(),0,0);//+"    000000000000000000000000";
				common.debit(drBrcode, drAccno, transType.toUpperCase(),setNoABB , getNextScrollNo(), narration, amount, rrn,session,t);
				if(!common.isAborted)
				{
					logger.error("Other Bank GL Transaction successful");
					common.creditABB(crBrcode, MSGConstants.ABB_ACC, "ABB", setNoABB, getNextScrollNo(),narration,lbrCode, amount, rrn,reconNo,session,t);
					resultMap.put(Code.RESULT, Code.SUCCESS);
					resultMap.put(Code.SETNO, String.valueOf(setNo));
					resultMap.put(Code.SCROLLNO, String.valueOf(scrollNo));
					common=null;
//					new VoucherCommon().balance(lbrCode, accNo, amount, "C");
					System.out.println(CommonVoucher.balanceUpdate(lbrCode, accNo, amount, "C"));
					
					return resultMap;
				}else
				{
					logger.error("ABB Transaction unsuccessful");
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "");
					resultMap.put(Code.SCROLLNO, "");
					common=null;
					return resultMap;
				}
			}else
			{
				logger.error("ABB Transaction unsuccessful");
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "");
				resultMap.put(Code.SCROLLNO, "");
				common=null;
				return resultMap;
			}
		}else
		{
			logger.error("Transaction unsuccessful");
			resultMap.put(Code.RESULT, Code.ERROR);
			resultMap.put(Code.SETNO, "");
			resultMap.put(Code.SCROLLNO, "");
			common=null;
			return resultMap;
		}
	}
	
	public static HashMap<String, String> otherBankCreditVoucherEntry(int lbrCode,String accNo,String transType,String narration,double amount,String rrn)
	{
		Session session=HBUtil.getSessionFactory().openSession();
		Transaction t=session.beginTransaction();
		CommonVoucher common=new CommonVoucher();
		HashMap<String, String> resultMap=new HashMap<>();
		int setNo=getNextSetNo();
		int scrollNo=getNextScrollNo();
		int drBrcode=Integer.valueOf(getSysParameter("MBBILLPAYBR",session).trim());
		int crBrcode=Integer.valueOf(getSysParameter("MBBILLPAYBRCR",session).trim());
		int reconNo=common.getNextReconNo(2);
//		System.out.println("lbrCode::>>"+lbrCode);
//		System.out.println("accNo::>>"+accNo);
		logger.error("accNo::>>"+accNo);
		logger.error("lbrCode::>>"+lbrCode);
		common.credit(lbrCode, accNo, transType.toUpperCase(), setNo, scrollNo,narration, amount, rrn,session,t);
		if(!common.isAborted)
		{
			logger.error("Transaction successful");
			common.debitABB(lbrCode, MSGConstants.ABB_ACC, "ABB", setNo, scrollNo, narration, amount, rrn,reconNo,session,t);
			if(!common.isAborted)
			{
				int setNoABB=getNextSetNo();
				logger.error("ABB Transaction successful");
				String drAccno=get32DigitAcctNo(getSysParameter("MBBILLPAY",session).trim(),0,0);//+"    000000000000000000000000";
				common.debit(drBrcode, drAccno, transType.toUpperCase(),setNoABB , getNextScrollNo(), narration, amount, rrn,session,t);
				if(!common.isAborted)
				{
					logger.error("Other Bank GL Transaction successful");
					common.creditABB(crBrcode, MSGConstants.ABB_ACC, "ABB", setNoABB, getNextScrollNo(),narration,lbrCode, amount, rrn,reconNo,session,t);
					resultMap.put(Code.RESULT, Code.SUCCESS);
					resultMap.put(Code.SETNO, String.valueOf(setNo));
					resultMap.put(Code.SCROLLNO, String.valueOf(scrollNo));
					common=null;
//					new VoucherCommon().balance(lbrCode, accNo, amount, "C");
					System.out.println(CommonVoucher.balanceUpdate(lbrCode, accNo, amount, "C"));
					
					return resultMap;
				}else
				{
					logger.error("ABB Transaction unsuccessful");
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "");
					resultMap.put(Code.SCROLLNO, "");
					common=null;
					return resultMap;
				}
			}else
			{
				logger.error("ABB Transaction unsuccessful");
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "");
				resultMap.put(Code.SCROLLNO, "");
				common=null;
				return resultMap;
			}
		}else
		{
			logger.error("Transaction unsuccessful");
			resultMap.put(Code.RESULT, Code.ERROR);
			resultMap.put(Code.SETNO, "");
			resultMap.put(Code.SCROLLNO, "");
			common=null;
			return resultMap;
		}
	}
	
	
	public static HashMap<String, String> otherBankDebitVoucherEntry(int lbrCode,String accNo,String transType,String narration,
			double amount,String rrn,Session session,Transaction t)
	{
		logger.error("<<<<<<<<<<::: otherBankCreditVoucherEntry :::>>>>>>>>>>>>>");
		logger.error("lbrCode:>>>"+lbrCode);
		logger.error("accNo::>>>"+accNo);
		logger.error("transType::>>>"+transType);
		logger.error("narration::>>>"+narration);
		logger.error("amount::>>"+amount);
		logger.error("rrn::>>>"+rrn);
		logger.error("session:>>>"+session);
		logger.error("Transaction ::>>"+t);
		logger.error("session.isOpen()::>>>"+session.isOpen());
		logger.error("t.isActive()"+t.isActive());
		CommonVoucher common=new CommonVoucher();
		HashMap<String, String> resultMap=new HashMap<>();
		if(lbrCode==2)
		{
			logger.error("Same Branch Fund Transfer....");
			int setNo=getNextSetNo();
			int mainScrollNo=getNextScrollNo();
			common.debitSameBranch(lbrCode, accNo, transType.toUpperCase(), setNo, getNextScrollNo(),narration, amount, rrn,mainScrollNo,session,t);
			CoreTransactionMPOS.balance(lbrCode,accNo.trim(), amount, "C");
			if(!common.isAborted)
			{
				logger.error("Transaction successful");
				String crAccno=get32DigitAcctNo(getSysParameter(MSGConstants.MBBBILLPAY_CR_ACC,session).trim(),0,0);//+"    000000000000000000000000";
				common.debitSameBranch(lbrCode, crAccno, transType, setNo , getNextScrollNo(), narration, amount, rrn,mainScrollNo,session,t);
				resultMap.put(Code.RESULT, Code.SUCCESS);
				resultMap.put(Code.SETNO, String.valueOf(setNo));
				resultMap.put(Code.SCROLLNO, String.valueOf(mainScrollNo));
//				return resultMap;
			}else
			{
				logger.error("Transaction unsuccessful");
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "");
				resultMap.put(Code.SCROLLNO,"");
//				return resultMap;
			}
		}
		else
		{
			logger.error("Other Branch transaction");
			int setNo=getNextSetNo();
			int scrollNo=getNextScrollNo();
			int reconNo=common.getNextReconNo(2);
			common.credit(lbrCode, accNo, transType.toUpperCase(), setNo,scrollNo ,narration, amount, rrn,session,t);
			System.out.println("common.isAborted::>>"+common.isAborted);
			if(!common.isAborted)
			{
				logger.error("Transaction successful");
				common.debitABB(lbrCode, MSGConstants.ABB_ACC, "ABB", setNo, getNextScrollNo(), narration, amount, rrn,reconNo,session,t);
				logger.error("common.isAborted::>>"+common.isAborted);
				if(!common.isAborted)
				{
					int setNoABB=getNextSetNo();
					logger.error("ABB Transaction successful");
					System.out.println(" ABB common.isAborted::>>"+common.isAborted);
					String crAccno=get32DigitAcctNo(getSysParameter(MSGConstants.MBBBILLPAY_CR_ACC,session).trim(),0,0);//+"    000000000000000000000000";
					common.debit(2, crAccno, transType,setNoABB , getNextScrollNo(), narration, amount, rrn,session,t);
					logger.error("common.isAborted::>>"+common.isAborted);
					System.out.println("common.isAborted::>>>"+common.isAborted);
					if(!common.isAborted)
					{
						logger.error("Other Bank GL Transaction successful");
						common.creditABB(2, MSGConstants.ABB_ACC, "ABB", setNoABB, getNextScrollNo(),narration, 2,amount, rrn,reconNo,session,t);
						t.commit();
						session.close();
						session=null;
						t=null;
						resultMap.put(Code.RESULT, Code.SUCCESS);
						resultMap.put(Code.SETNO, String.valueOf(setNo));
						resultMap.put(Code.SCROLLNO, String.valueOf(scrollNo));
//						CommonVoucher.balanceUpdate(lbrCode, accNo, amount, "D");
						System.out.println("resultMap::>>"+resultMap);
						logger.error("resultMap::>>>"+resultMap);
//						return resultMap;
					}else
					{
						logger.error("ABB Transaction unsuccessful");
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "");
						resultMap.put(Code.SCROLLNO, "");
//						return resultMap;
					}
				}else
				{
					logger.error("ABB Transaction unsuccessful");
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "");
					resultMap.put(Code.SCROLLNO, "");
//					return resultMap;
				}
			}else
			{
				logger.error("Transaction unsuccessful");
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "");
				resultMap.put(Code.SCROLLNO, "");
//				return resultMap;
			}
		}
		logger.error(" Final Voucher Result::>>>"+resultMap);
		return resultMap;
	}
	public static String getSysParameter(String code,Session session)
	{
		try {
			D001004Id id = new D001004Id();
			id.setCode(code.trim().toUpperCase());
			id.setLbrCode(0);			
			D001004 d04 = session.get(D001004.class, id);
			if(d04 != null) {
				return d04.getValue();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static HashMap<String, String> otherBranchVouchers(int lbrCode,String accNo,int benBrCode,String benAccNo,String transType,String narration,double amount,String rrn,Session session,Transaction t)
	{
		CommonVoucher common=new CommonVoucher();
		HashMap<String, String> resultMap=new HashMap<>();
		if(lbrCode==benBrCode)
		{
			logger.error("Same Branch Fund Transfer....");
			int setNo=getNextSetNo();
			int mainScrollNo=getNextScrollNo();
			common.debitSameBranch(lbrCode, accNo, transType.toUpperCase(), setNo, getNextScrollNo(),narration, amount, rrn,mainScrollNo,session,t);
//			common.balance(lbrCode, accNo, amount, "D");
			CoreTransactionMPOS.balance(lbrCode,accNo.trim(), amount, "D");
			if(!common.isAborted)
			{
				logger.error("Transaction successful");
				common.creditSameBranch(benBrCode, benAccNo, transType, setNo , getNextScrollNo(), narration, amount, rrn,mainScrollNo,session,t);
				CoreTransactionMPOS.balance(benBrCode,benAccNo.trim(), amount, "C");
				resultMap.put(Code.RESULT, Code.SUCCESS);
				resultMap.put(Code.SETNO, String.valueOf(setNo));
				resultMap.put(Code.SCROLLNO, String.valueOf(mainScrollNo));
				return resultMap;
			}else
			{
				logger.error("Transaction unsuccessful");
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "");
				resultMap.put(Code.SCROLLNO,"");
				return resultMap;
			}
		}else
		{
			logger.error("Other Branch Fund Transfer....");
			int setNo=getNextSetNo();
			int reconNo=common.getNextReconNo(2);
			int scrollNo=getNextScrollNo();
			common.debit(lbrCode, accNo, transType.toUpperCase(), setNo, scrollNo,narration, amount, rrn,session,t);
//			common.balance(lbrCode, accNo, amount, "D");
			CoreTransactionMPOS.balance(lbrCode, accNo, amount, "D");
			if(!common.isAborted)
			{
				logger.error("Transaction successful");
				common.creditABB(lbrCode, MSGConstants.ABB_ACC, "ABB", setNo, getNextScrollNo(), narration, lbrCode, amount, rrn,reconNo,session,t);
				if(!common.isAborted)
				{
					int setNoABB=getNextSetNo();
					logger.error("ABB Transaction successful");
//					String crAccno=getSysParameter("MBBILLPAY").trim()+"    000000000000000000000000";
					common.credit(benBrCode, benAccNo, transType,setNoABB , getNextScrollNo(), narration, amount, rrn,session,t);
//					common.balance(benBrCode, benAccNo, amount, "C");
					CoreTransactionMPOS.balance(benBrCode, benAccNo, amount, "C");
					if(!common.isAborted)
					{
						logger.error("Other Bank GL Transaction successful");
						common.debitABB(benBrCode, MSGConstants.ABB_ACC, "ABB", setNoABB,getNextScrollNo(),narration, amount, rrn,reconNo,session,t);
						resultMap.put(Code.RESULT, Code.SUCCESS);
						resultMap.put(Code.SETNO, String.valueOf(setNo));
						resultMap.put(Code.SCROLLNO, String.valueOf(scrollNo));
						return resultMap;
					}else
					{
						t.commit();
						session.close();
						session=null;
						logger.error("ABB Transaction unsuccessful");
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "");
						resultMap.put(Code.SCROLLNO,"");
						return resultMap;
					}
				}else
				{
					t.commit();
					session.close();
					session=null;
					logger.error("ABB Transaction unsuccessful");
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "");
					resultMap.put(Code.SCROLLNO,"");
					return resultMap;
				}
			}else
			{
				t.commit();
				session.close();
				session=null;
				logger.error("Transaction unsuccessful");
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "");
				resultMap.put(Code.SCROLLNO,"");
				return resultMap;
			}
		}
	}
	
	public static HashMap<String, String> otherBranchReversal(int lbrCode,String accNo,int benBrCode,String benAccNo,String transType,String narration,double amount,String rrn)
	{
		CommonVoucher common=new CommonVoucher();
		HashMap<String, String> resultMap=new HashMap<>();
		Session  session=HBUtil.getSessionFactory().openSession();
		Transaction t=session.beginTransaction();
		if(lbrCode==benBrCode)
		{
			logger.error("Same Branch Fund Transfer....");
			int setNo=getNextSetNo();
			int mainScrollNo=getNextScrollNo();
			common.creditSameBranch(lbrCode, accNo, transType.toUpperCase(), setNo, getNextScrollNo(),narration, amount, rrn,mainScrollNo,session,t);
//			common.balance(lbrCode, accNo, amount, "C");
			CommonVoucher.balanceUpdate(lbrCode, accNo, amount, "C");
			if(!common.isAborted)
			{
				logger.error("Transaction successful");
				common.debitSameBranch(benBrCode, benAccNo, transType, setNo , getNextScrollNo(), narration, amount, rrn,mainScrollNo,session,t);
//				common.balance(benBrCode, benAccNo, amount, "D");
				CommonVoucher.balanceUpdate(benBrCode, benAccNo, amount, "D");
				session.close();
				session=null;
				resultMap.put(Code.RESULT, Code.SUCCESS);
				resultMap.put(Code.SETNO, String.valueOf(setNo));
				resultMap.put(Code.SCROLLNO, String.valueOf(mainScrollNo));
				return resultMap;
			}else
			{
				logger.error("Transaction unsuccessful");
				session.close();
				session=null;
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "");
				resultMap.put(Code.SCROLLNO,"");
				return resultMap;
			}
		}else
		{
			logger.error("Other Branch Fund Transfer....");
			int setNo=getNextSetNo();
			int reconNo=common.getNextReconNo(2);
			int scrollNo=getNextScrollNo();
			common.credit(lbrCode, accNo, transType.toUpperCase(), setNo,getNextScrollNo() ,narration, amount, rrn,session,t);
//			common.balance(lbrCode, accNo, amount, "C");
			CommonVoucher.balanceUpdate(lbrCode, accNo, amount, "C");
			if(!common.isAborted)
			{
				logger.error("Transaction successful");
				common.debitABB(lbrCode, MSGConstants.ABB_ACC, "ABB", setNo, getNextScrollNo(), narration,  amount, rrn,reconNo,session,t);
				if(!common.isAborted)
				{
					int setNoABB=getNextSetNo();
					logger.error("ABB Transaction successful");
//					String crAccno=getSysParameter("MBBILLPAY",session).trim()+"    000000000000000000000000";
					common.debit(benBrCode, benAccNo, transType,setNoABB , getNextScrollNo(), narration, amount, rrn,session,t);
//					common.balance(benBrCode, benAccNo, amount, "D");
					CommonVoucher.balanceUpdate(benBrCode, benAccNo, amount, "D");
					if(!common.isAborted)
					{
						logger.error("Other Bank GL Transaction successful");
//						common.debitABB(benBrCode, MSGConstants.ABB_ACC, "ABB", setNoABB,getNextScrollNo(),narration, amount, rrn,reconNo);
//						common.debitABB(benBrCode, MSGConstants.ABB_ACC, "ABB", setNoABB,getNextScrollNo(),narration, amount, rrn,reconNo);
						common.creditABB(benBrCode, MSGConstants.ABB_ACC, "ABB", setNoABB, scrollNo, narration, benBrCode, amount, rrn, reconNo,session,t);
						session.close();
						session=null;
						resultMap.put(Code.RESULT, Code.SUCCESS);
						resultMap.put(Code.SETNO, String.valueOf(setNo));
						resultMap.put(Code.SCROLLNO, String.valueOf(scrollNo));
						return resultMap;
					}else
					{
						logger.error("ABB Transaction unsuccessful");
						session.close();
						session=null;
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "");
						resultMap.put(Code.SCROLLNO,"");
						return resultMap;
					}
				}else
				{
					logger.error("ABB Transaction unsuccessful");
					session.close();
					session=null;
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "");
					resultMap.put(Code.SCROLLNO,"");
					return resultMap;
				}
			}else
			{
				logger.error("Transaction unsuccessful");
				session.close();
				session=null;
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "");
				resultMap.put(Code.SCROLLNO,"");
				return resultMap;
			}
		}
	}
	public static void main(String[] args) {
		
		Session session=HBUtil.getSessionFactory().openSession();
		Transaction t=session.beginTransaction();
//		new VoucherCommon().balance(3, "SB      000000000000670700000000", 10, "C"); 
//		HashMap<String, String> result2=otherBranchReversal(3, "SB      000000000000670700000000", 3, "SB      000000000000274000000000", "IMPS","TT", 21, "888888999999");
//		logger.error("result2::>>"+result2);
//		System.out.println("result2::>>"+result2);
//		VoucherCommon common=new VoucherCommon();
//		HashMap<String, String> result=otherBranchVouchers(3, "SB      000000000000670700000000", 9, "SB      000000000000462800000000", "IMPS","TT", 21.21, DataUtils.getNextRRN(),session,t);
		
//		HashMap<String, String> result1=otherBranchVouchers(3, "SB      000000000000670700000000", 3, "SB      000000000000274000000000", "IMPS","TT", 21.21, "777788555555");
//		logger.error("result::>>"+result);
//		logger.error("result1::>>"+result1);
		
		String rrn=DataUtils.getNextRRN();
		System.out.println("RRN::>>>>"+rrn);
		System.out.println("Dr Voucher ::>>"+otherBankCreditVoucherEntry(3, "SB      000000000000670700000000", "IMPS", "tt", 22.2, rrn,session,t));;

//		t.commit();
//		session.close();
//		session=null;
		
//		logger.error("Cr Voucher ::>>"+otherBankReverseVoucherEntry(3, "SB      000000000000670700000000", "IMPS", "tt", 21.2, "444444444444"));
		
//		VoucherCommon common=new VoucherCommon();
//		int setNo=common.getNextSetNo();
//		logger.error("Recon No::>>"+common.getNextReconNo(2));;
		
		/*VoucherCommon common=new VoucherCommon();
		int setNo=common.getNextSetNo();
		int reconNo=common.getNextReconNo(2);
		common.debit(3, "SB      000000000000670700000000", "IMPS", setNo, common.getNextScrollNo(),"tt", 21.2, "111111111111");
		if(!common.isAborted)
		{
			logger.error("Transaction successful");
			common.creditABB(3, MSGConstants.ABB_ACC, "IMPS", setNo, common.getNextScrollNo(), "tt", 2, 21.2, "111111111111",reconNo);
			
			if(!common.isAborted)
			{
				int setNoABB=common.getNextSetNo();
				logger.error("ABB Transaction successful");
				common.credit(2, "3264    000000000000000000000000", "ABB",setNoABB , common.getNextScrollNo(), "tt", 21.2, "111111111111");
				if(!common.isAborted)
				{
					logger.error("Other Bank GL Transaction successful");
					common.debitABB(2, MSGConstants.ABB_ACC, "ABB", setNoABB, common.getNextScrollNo(),"tt", 21.2, "111111111111",reconNo);
					tx.commit();
				}else
				{
					logger.error("ABB Transaction unsuccessful");
				}
				
			}else
			{
				logger.error("ABB Transaction unsuccessful");
				common.tx.rollback();
			}
		}else
		{
			logger.error("Transaction unsuccessful");
			common.tx.rollback();
		}*/
	}
	
	public static String balanceUpdate(int remBrCode,String remPrdAcctId,double amount,String type)
	{
		Session session=HBUtil.getSessionFactory().openSession();
		Transaction t=session.beginTransaction();
		if(type.equalsIgnoreCase("D"))
		{
			Logger.info("Transaction Amount : "+amount);
			try {
				D009022Id remId = new D009022Id();
				remId.setLbrCode(remBrCode);
				remId.setPrdAcctId(remPrdAcctId);
				D009022 remAcct = session.get(D009022.class, remId);
				
				Logger.info("Rem Amount Before : "+remAcct.getActClrBalFcy());
				if(DataUtils.isOverDraftAccount(remBrCode, remPrdAcctId))
				{
					Logger.info("Final Amount : "+(remAcct.getActClrBalFcy()-amount));
					remAcct.setActClrBalFcy(remAcct.getActClrBalFcy()-amount);
					remAcct.setShdClrBalFcy(remAcct.getShdClrBalFcy()-amount);
					remAcct.setShdTotBalFcy(remAcct.getShdTotBalFcy()-amount);
					remAcct.setActTotBalFcy(remAcct.getActTotBalFcy()-amount);
					remAcct.setActTotBalLcy(remAcct.getActTotBalLcy()-amount);
					session.update(remAcct);
					t.commit();
					session.close();
					session=null;
					t=null;
				}
				else			
				if((remAcct.getActClrBalFcy()-remAcct.getTotalLienFcy()) >= amount){
					Logger.info("Final Amount : "+(remAcct.getActClrBalFcy()-amount));
					remAcct.setActClrBalFcy(remAcct.getActClrBalFcy()-amount);
					remAcct.setShdClrBalFcy(remAcct.getShdClrBalFcy()-amount);
					remAcct.setShdTotBalFcy(remAcct.getShdTotBalFcy()-amount);
					remAcct.setActTotBalFcy(remAcct.getActTotBalFcy()-amount);
					remAcct.setActTotBalLcy(remAcct.getActTotBalLcy()-amount);
					session.update(remAcct);
					t.commit();
					session.close();
					session=null;
					t=null;
//					session.flush();
				}
				else{
					logger.error("Insufficient Funds ");
//					isAborted = true;
//					abortCode = "51";
//					abortReason = "Insufficient Funds";
					return "51";
				}
				return ""+remAcct.getActClrBalFcy();
			} catch (Exception e) {
				e.printStackTrace();
//				isAborted = true;
//				abortCode = "EX";
//				abortReason = "Exception in Balance Effect";
				return "99";
			}
		}else
		{
			Logger.info("Transaction Amount : "+amount);
			try {
				D009022Id remId = new D009022Id();
				remId.setLbrCode(remBrCode);
				remId.setPrdAcctId(remPrdAcctId);
				D009022 remAcct = session.get(D009022.class, remId);

				Logger.info("Rem Amount Before : "+remAcct.getActClrBalFcy());
				Logger.info("Final Amount : "+(remAcct.getActClrBalFcy()+amount));
				remAcct.setActClrBalFcy(remAcct.getActClrBalFcy()+amount);
				remAcct.setShdClrBalFcy(remAcct.getShdClrBalFcy()+amount);
				remAcct.setShdTotBalFcy(remAcct.getShdTotBalFcy()+amount);
				remAcct.setActTotBalFcy(remAcct.getActTotBalFcy()+amount);
				remAcct.setActTotBalLcy(remAcct.getActTotBalLcy()+amount);

				session.update(remAcct);
				t.commit();
				session.close();
				session=null;
				t=null;
				//					session.flush();
				return ""+remAcct.getActClrBalFcy();	

			} catch (Exception e) {
				e.printStackTrace();
				return "99";
			}
		}
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
	
	public static String get32DigitAcctNo(String prdCd, int acctNo, int subAcctNo) {
		String acctNo32Digit = String.format("%-8s", prdCd) + "00000000" + String.format("%08d", acctNo)
				+ String.format("%08d", subAcctNo);
		return acctNo32Digit;
	}
}

