package com.sil.commonswitch;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.sil.constants.MSGConstants;
import com.sil.hbm.D001004;
import com.sil.hbm.D001004Id;
import com.sil.hbm.D002001;
import com.sil.hbm.D009021;
import com.sil.hbm.D009021Id;
import com.sil.hbm.D009040;
import com.sil.hbm.D009040Id;
import com.sil.hbm.D010001;
import com.sil.hbm.D010001Id;
import com.sil.hbm.D010004;
import com.sil.hbm.D010004Id;
import com.sil.hbm.MBTRSCROLLSEQ;
import com.sil.hbm.MBTRSETSEQ;
import com.sil.prop.ConfigurationLoader;
import com.sil.util.HBUtil;
import com.sil.util.Props;

public class Voucher2 {
	public	static org.apache.log4j.Logger logger=org.apache.log4j.Logger.getLogger(Voucher2.class);
	public static final SimpleDateFormat sdf = new SimpleDateFormat("HHmmssSS");
	public static final SimpleDateFormat sdf1 = new SimpleDateFormat("0yyyyMMdd");
	Session sessionDebit;
	//Session sessionCredit = HBUtil.getSessionFactory().openSession();
	Date dt = new Date();
	
	public Transaction txDebit;
	//Transaction txCredit;
	
	
	public Voucher2()
	{
		sessionDebit = HBUtil.getSessionFactory().openSession();
		txDebit = sessionDebit.beginTransaction();
	}
	
	
	
	
	
	
	/*public static void main(String[] args) {
		Voucher vc = new Voucher();
		vc.txDebit = vc.sessionDebit.beginTransaction();
		boolean debitResult = vc.debit();
		boolean creditResult = vc.credit();
		
		vc.txDebit.commit();
		vc.sessionDebit.close();
		HBUtil.getSessionFactory().close();
	}*/
	
	public boolean debit(String prdAcctId, int lbrCode, double amt, String actType, String transType, String instrNo, String particulars, long setNo, long scrollNo){
		
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
			D001004 d04OnlineBatchName = getBatchNameFromBatchCode(batchCodes[0], lbrCode);
			String onlineBatchName = d04OnlineBatchName.getValue().trim();
			logger.error("Batch Name : "+onlineBatchName);
			D001004 d04OfflineBatchName = getBatchNameFromBatchCode(batchCodes[1], lbrCode);
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
			d40.setActivityType("DR"); 
			d40.setCashFlowType("DR");
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
			
			sessionDebit.save(d40);
			sessionDebit.flush();
		} catch (Exception e) {
			e.printStackTrace();
			//if(tx != null) tx.rollback();
			return false;
		}		
		
		return true;		
		
	}
	
	
	public boolean credit(String prdAcctId, int lbrCode, double amt, String actType, String transType, String instrNo, String particulars, long setNo, long scrollNo){
		
		//txCredit = sessionCredit.beginTransaction();
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
			D001004 d04OnlineBatchName = getBatchNameFromBatchCode(batchCodes[0], lbrCode);
			String onlineBatchName = d04OnlineBatchName.getValue().trim();
			logger.error("Batch Name : "+onlineBatchName);
			D001004 d04OfflineBatchName = getBatchNameFromBatchCode(batchCodes[1], lbrCode);
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
			
			logger.error("");
			
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
			
			logger.error("BookType : "+d001.getBookType());
			
			d40.setBookType(d001.getBookType()); //SELECT BookType FROM D010001 WHERE LBrCode = 9 AND Code = 'ABBTR'
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
	
	
	public static D001004 getBatchNameFromBatchCode(String batchCode, int lbrCode)
	{	
		
		D001004 d04 = null;
		try(Session session = HBUtil.getSessionFactory().openSession()) {
			D001004Id id = new D001004Id();
			id.setCode(batchCode);
			id.setLbrCode(lbrCode);
			d04 = session.get(D001004.class, id);	
			session.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return d04;	
	}
	

	public static long getNextSetNo(){
		MBTRSETSEQ setSeq = new MBTRSETSEQ();
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
				return Long.valueOf(d001004.getValue().trim());
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
		try(Session session = HBUtil.getSessionFactory().openSession()){
			session.save(setSeq);
			session.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}		
		return setSeq.getId();	
		}
	}
	
	
	public static long getNextScrollNo(){
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
	
}
