package com.sil.commonswitch;


public class IMPSTransactionsImpl {
	public static void main(String[] args) {
		System.out.println("");
	}
}

/*	private static final SimpleDateFormat sdf = new SimpleDateFormat("0yyyyMMdd");
	private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmssSS");
	private static final String abbAcc = "ABB     000000000000000000000000";
	private static final String abbbr = "ABB     000000000000000000000000";
	private static boolean isAborted = false;
	
	public static void initiateTransaction(int remBrCode,String remPrdAcctId,String transType ,String narration,double amount,String rrn)
	{
		Logger.info("Inter Branch Transaction.");
		int remSetNo = CoreTransactionMPOS.getNextSetNo();
		int benSetNo = CoreTransactionMPOS.getNextSetNo();
		debit(remBrCode, remPrdAcctId, transType, remSetNo, CoreTransactionMPOS.getNextScrollNo(), narration,amount,rrn);
		if(!isAborted){
			Logger.info("Rem Debit OK");
			creditABB(remBrCode, abbAcc, transType, remSetNo, CoreTransactionMPOS.getNextScrollNo(), narration, abbbr,amount,rrn);
			if(!isAborted){
				Logger.info("RemABB Credit OK");
				debitABB(abbbr, abbAcc, "ABB", benSetNo,CoreTransactionMPOS.getNextScrollNo(), narration);
				if(!isAborted){
					Logger.info("BenABB Debit OK");
					credit(benBrCode, benPrdAcctId, "ABB", benSetNo, getNextScrollNo(), narration);
					if(!isAborted){
						Logger.info("Ben Credit OK");
						getReconNo(remBrCode);
						if(!isAborted)
						{
							updateBatch();
							if(!isAborted){
								Logger.info("Recon OK");
								balance();
								if(!isAborted){
									balanceABB();
									if(!isAborted){
										if(bitdata[3].equalsIgnoreCase("860000"))
										{
											P2P(bitdata);
											if(!isAborted)
											{
												tx.commit();
											}
											else{
												tx.rollback();
											}
										}
										else if(bitdata[3].equalsIgnoreCase("870000")){
											P2A(bitdata);
											if(!isAborted)
											{
												tx.commit();
											}
											else{
												tx.rollback();
											}
										}
									}
									else{
										tx.rollback();
									}
								}
								else{
									tx.rollback();
								}
							}
							else{
								tx.rollback();
							}
						}
						else{
							tx.rollback();
						}
					}
					else{
						tx.rollback();
					}
				}else{
					tx.rollback();
				}
			}else{
				tx.rollback();
			}
		}
		else{
			tx.rollback();
		}
	
		
	}
	
	
	
	public static void debit(int brCode, String acctId, String tType, int setNo, int scrollNo, String narration,double amount,String rrn)
	{
		int sourceSetNo = setNo;
		
		try{
			
			Date openDate =CoreTransactionMPOS.getOpenDate(brCode); // Get Open Date
			Date remEntryDate = openDate;
			if(openDate == null){
				Logger.error("Open Date Not Found. Aborting Transaction");
//				isAborted = true;
//				abortCode = Code.REMOPENDATEERROR;
//				abortReason = "Open Date Not Found";
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
//				isAborted = true;
//				abortCode = Code.REMBATCHCODEERROR;
//				abortReason = "Batch Codes Not Found in Properties File.";
				return;
			}
			
			/// Get Selected Batch.
			D010004 selectedBatch = CoreTransactionMPOS.getSelectedBatch(brCode, batchCodes, openDate);
			if(selectedBatch == null){
				Logger.error("No Active Batch Found.");
//				isAborted = true;
//				abortCode = Code.REMBATCHERROR;
//				abortReason = "No Active Batch Found.";
				return;
			}
			D010004 remSelectedBatch = selectedBatch; // For future Use.
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
			
			String bookType  = CoreTransactionMPOS.getBookType(brCode, selectedBatch.getId().getBatchCd());
			if(bookType == null){
				Logger.error("BookType Not Found.");
//				isAborted = true;
//				abortCode = Code.REMBOOKTYPEERROR;
//				abortReason = "BookType Not Found.";
				return;
			}
			Logger.info("BookType is : "+bookType);
			d40.setBookType(bookType); //SELECT BookType FROM D010001 WHERE LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('D');
			d40.setVcrAcctId(acctId);
			d40.setMainAcctId(acctId);
			
			short moduleType = CoreTransactionMPOS.getModuleType(brCode, acctId.substring(0,8).trim());
			if(moduleType == 0){
				Logger.error("ModuleType Not Found.");
//				isAborted = true;
//				abortCode = Code.REMMODULETYPEERROR;
//				abortReason = "ModuleType Not Found.";
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
			
			int  usrCode2 = CoreTransactionMPOS.getUsrCode("WEB");
			
			if(usrCode2 == 0){
				Logger.error("UsrCode Not Found.");
//				isAborted = true;
//				abortCode = Code.REMUSRCODEERROR;
//				abortReason = "UsrCode Not Found.";
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
			
			Session session=HBUtil.getSessionFactory().openSession();
			Transaction t=session.beginTransaction();
			session.save(d40);
			t.commit();
			session.close();
			
		} catch (Exception e) {
//			isAborted = true;
//			abortCode = "EX";
//			abortReason = "Exception in Debit.";
			e.printStackTrace();
		}		
		
	}
	
	public static void creditABB(int brCode, String acctId, String tType, int setNo, int scrollNo, String narration, int tbrCode, double amount,String rrn)
	{
		int sourceScrollNo = scrollNo;
		try{
			
			Date openDate = CoreTransactionMPOS.getOpenDate(brCode); // Get Open Date
			if(openDate == null){
				Logger.error("Open Date Not Found. Aborting Transaction");
				isAborted = true;
//				abortCode = Code.BENOPENDATEERROR;
//				abortReason = "Open Date Not Found";
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
//				abortCode = Code.BENBATCHCODEERROR;
//				abortReason = "Batch Codes Not Found in Properties File.";
				return;
			}
			
			/// Get Selected Batch.
			D010004 selectedBatch = CoreTransactionMPOS.getSelectedBatch(brCode, batchCodes, openDate);
			if(selectedBatch == null){
				Logger.error("No Active Batch Found.");
				isAborted = true;
//				abortCode = Code.BENBATCHERROR;
//				abortReason = "No Active Batch Found.";
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
			
			String bookType  = CoreTransactionMPOS.getBookType(brCode, selectedBatch.getId().getBatchCd());
			if(bookType == null){
				Logger.error("BookType Not Found.");
				isAborted = true;
//				abortCode = Code.BENBOOKTYPEERROR;
//				abortReason = "BookType Not Found.";
				return;
			}
			Logger.info("BookType is : "+bookType);
			d40.setBookType(bookType); //SELECT BookType FROM D010001 WHERE LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('C');
			d40.setVcrAcctId(acctId);
			d40.setMainAcctId(acctId);
			
			short moduleType = CoreTransactionMPOS.getModuleType(brCode, acctId.substring(0,8).trim());
			if(moduleType == 0){
				Logger.error("ModuleType Not Found.");
				isAborted = true;
//				abortCode = Code.BENMODULETYPEERROR;
//				abortReason = "ModuleType Not Found.";
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
			
			int  usrCode2 = CoreTransactionMPOS.getUsrCode("WEB");
			if(usrCode2 == 0){
				Logger.error("UsrCode Not Found.");
				isAborted = true;
//				abortCode = Code.BENUSRCODEERROR;
//				abortReason = "UsrCode Not Found.";
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
			Session session=HBUtil.getSessionFactory().openSession();
			Transaction t=session.beginTransaction();
			session.save(d40);
			t.commit();
			session.flush();
			session.close();
//			crABB = d40;
		} catch (Exception e) {
			isAborted = true;
//			abortCode = "EX";
//			abortReason = "Exception in Credit.";
			e.printStackTrace();
		}		
		
	}	
	public static void main(String[] args) {
		
		IMPSTransactionsImpl.debit(2, "SB      000000000000579200000000", "IMPS", CoreTransactionMPOS.getNextSetNo(), CoreTransactionMPOS.getNextScrollNo(), "IMPS Mobile Recharge ", 100.0, "111111111199");
		
	}
*/


