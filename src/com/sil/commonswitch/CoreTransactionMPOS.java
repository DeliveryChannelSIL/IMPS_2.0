package com.sil.commonswitch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.Logger;
import org.pmw.tinylog.writers.FileWriter;

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
import com.sil.hbm.D100001Id;
import com.sil.hbm.D350036;
import com.sil.hbm.D350036Id;
import com.sil.hbm.D350037;
import com.sil.hbm.D350037Id;
import com.sil.hbm.MBTRSCROLLSEQ;
import com.sil.hbm.MBTRSETSEQ;
import com.sil.hbm.RECONSEQ;
import com.sil.prop.ConfigurationLoader;
import com.sil.util.HBUtil;
import com.sil.util.Props;

public class CoreTransactionMPOS {
	public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(CoreTransactionMPOS.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("0yyyyMMdd");
	private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmssSS");
	private static final String abbAcc = MSGConstants.ABB_ACC;

	private final int remBrCode;
	private final String remPrdAcctId;
	private final int benBrCode;
	// private final String benPrdAcctId;

	private final double amount;
	private final String rrn;
	private final String transType;
	private final String narration;

	private final Session session;
	private final Transaction tx;

	private boolean isAborted = false;
	private String abortCode = "00";
	private String abortReason = "Normal Transaction";
	private D010004 remSelectedBatch = null;
	private D010004 benSelectedBatch = null;
	private Date remEntryDate = null;
	private Date benEntryDate = null;
	private int usrCode = 0;
	private int sourceSetNo = 0;
	private int destSetNo = 0;
	private int sourceScrollNo = 0;
	private int destScrollNo = 0;
	private D009040 drABB = null;
	private D009040 crABB = null;

	public CoreTransactionMPOS(int remBrCode, String remPrdAcctId, int benBrCode, String benPrdAcctId, double amount,
			String rrn, String transType, String narration) {
		// super();
		this.remBrCode = remBrCode;
		this.remPrdAcctId = remPrdAcctId;
		this.benBrCode = benBrCode;
		// this.benPrdAcctId = benPrdAcctId;
		this.amount = amount;
		this.rrn = rrn;
		this.transType = transType;
		this.narration = narration;
		session = HBUtil.getSessionFactory().openSession();
		tx = session.beginTransaction();

		Configurator.defaultConfig().writer(new FileWriter("D:/log.txt")).level(Level.TRACE).activate();
	}

	public static int getNextSetNo() {
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
			try (Session session = HBUtil.getSessionFactory().openSession()) {
				session.save(setSeq);
				session.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
			return setSeq.getId();
		}
	}

	public static int getNextScrollNo() {
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
					try (Session session = HBUtil.getSessionFactory().openSession()) {
						session.save(scrollSeq);
						session.close();
			
					} catch (Exception e) {
						e.printStackTrace();
					}
					return scrollSeq.getId();
				}
	}

	public static int getNextRecNo() {
		RECONSEQ setSeq = new RECONSEQ();
		try (Session session = HBUtil.getSessionFactory().openSession()) {
			session.save(setSeq);
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return setSeq.getId();
	}

	public static int getNextRecNoNew(int brCode) {
		int reconNo = -1;
		try (Session session = HBUtil.getSessionFactory().openSession()) {
			Query q = session.createNativeQuery("SELECT NEXT VALUE FOR RECON_SEQUENCE_IMPS_" + brCode + "");
			// System.out.println("Recon No::>>"+q.getSingleResult());;
			reconNo = Integer.valueOf(q.getSingleResult() + "");
			session.close();
			return reconNo;
		} catch (Exception e) {
			e.printStackTrace();
			return reconNo;
		}
	}

	public static Date getOpenDate(int brCode) {
		try (Session session = HBUtil.getSessionFactory().openSession()) {
			D001004Id id = new D001004Id();
			id.setCode("LASTOPENDATE");
			id.setLbrCode(brCode);
			D001004 d04 = session.get(D001004.class, id);
			session.close();

			if (d04 != null) {
				return sdf.parse(d04.getValue().trim());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getBatchNameFromBatchCode(String batchCode) {
		try (Session session = HBUtil.getSessionFactory().openSession()) {
			D001004Id id = new D001004Id();
			id.setCode(batchCode);
			id.setLbrCode(0);
			D001004 d04 = session.get(D001004.class, id);
			session.close();

			if (d04 != null) {
				return d04.getValue().trim();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static D010004 getBatch(int lbrCode, String batchName, Date entryDate) {
		try (Session session = HBUtil.getSessionFactory().openSession()) {
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

	public static D010004 getSelectedBatch(int lbrCode, String[] batchCodes, Date entryDate) {
		try (Session session = HBUtil.getSessionFactory().openSession()) {
			for (String batchCode : batchCodes) {
				String batchName = getBatchNameFromBatchCode(batchCode);
				if (batchName == null)
					continue;
				else {
					logger.error("Current Batch Name : " + batchName);
					D010004 batch = getBatch(lbrCode, batchName, entryDate);
					if (batch == null) {
						logger.error("Batch with Batch Name " + batchName + " Not found");
					} else {
						if (batch.getStat() == 1 || batch.getStat() == 2) {
							logger.error("Selected batch is " + batchName);
							return batch;
						} else
							continue;
					}
				}
			}
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getBookType(int lbrCode, String BatchCd) {
		// SELECT BookType FROM D010001 WHERE LBrCode = 9 AND Code = 'ABBTR'
		D010001 d001 = null;
		try (Session session = HBUtil.getSessionFactory().openSession()) {
			D010001Id id = new D010001Id();
			id.setCode(BatchCd);
			id.setLbrCode(lbrCode);
			d001 = session.get(D010001.class, id);
			session.close();
			if (d001 != null)
				return d001.getBookType();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static short getModuleType(int lbrCode, String prdCd) {
		// SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB
		D009021 d21 = null;
		try (Session session = HBUtil.getSessionFactory().openSession()) {
			D009021Id id = new D009021Id();
			id.setLbrCode(lbrCode);
			id.setPrdCd(prdCd);
			d21 = session.get(D009021.class, id);
			session.close();
			if (d21 != null)
				return d21.getModuleType();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static int getUsrCode(String usrCode1) {
		D002001 d201 = null;
		try (Session session = HBUtil.getSessionFactory().openSession()) {
			d201 = session.get(D002001.class, usrCode1);
			session.close();
			if (d201 != null)
				return d201.getUsrCode2();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void debit(int brCode, String acctId, String tType, int setNo, int scrollNo, String narration) {
		sourceSetNo = setNo;

		try {

			Date openDate = getOpenDate(brCode); // Get Open Date
			remEntryDate = openDate;
			if (openDate == null) {
				Logger.error("Open Date Not Found. Aborting Transaction");
				isAborted = true;
				abortCode = Code.REMOPENDATEERROR;
				abortReason = "Open Date Not Found";
				return;
			}
			logger.error("Open Date is " + openDate);

			// Get BatchCodes from properties file.
			String batchCode = Props.getBatchProperty(tType);
			logger.error("Batch Code Form Properties File : " + batchCode);
			String batchCodes[] = batchCode.split("~");
			logger.error("Number of Batches is " + batchCodes.length + "\t Names are " + Arrays.toString(batchCodes));
			if (batchCodes == null || batchCodes.length < 1) {
				Logger.error("Batch Codes Not Found in Properties File.");
				isAborted = true;
				abortCode = Code.REMBATCHCODEERROR;
				abortReason = "Batch Codes Not Found in Properties File.";
				return;
			}

			/// Get Selected Batch.
			D010004 selectedBatch = getSelectedBatch(brCode, batchCodes, openDate);
			if (selectedBatch == null) {
				Logger.error("No Active Batch Found.");
				isAborted = true;
				abortCode = Code.REMBATCHERROR;
				abortReason = "No Active Batch Found.";
				return;
			}
			remSelectedBatch = selectedBatch; // For future Use.
			logger.error("Selected Batch : " + selectedBatch.getId().getBatchCd());

			D009040 d40 = new D009040();
			D009040Id id40 = new D009040Id();
			id40.setBatchCd(selectedBatch.getId().getBatchCd()); // SELECT *
																	// FROM
																	// D010004
																	// WHERE
																	// LBrCode
																	// =9 AND
																	// EntryDate
																	// =
																	// '19-APR-2016'

			id40.setEntryDate(selectedBatch.getId().getEntryDate()); // SELECT
																		// Value
																		// FROM
																		// D001004
																		// WHERE
																		// LBrCode
																		// = 9
																		// AND
																		// Code
																		// =
																		// 'LASTOPENDATE'
			id40.setLbrCode(brCode);
			id40.setSetNo(setNo);

			id40.setScrollNo(scrollNo);

			d40.setId(id40);
			d40.setMainScrollNo(scrollNo); // Scroll No
			d40.setPostDate(selectedBatch.getPostDate()); // SELECT * FROM
															// D010004 WHERE
															// LBrCode =9 AND
															// EntryDate =
															// '19-APR-2016' AND
															// BatchCd = 'ABBTR'
			d40.setFeffDate(selectedBatch.getFeffDate()); // SELECT * FROM
															// D010004 WHERE
															// LBrCode =9 AND
															// EntryDate =
															// '19-APR-2016' AND
															// BatchCd = 'ABBTR'
			d40.setActivityType("DR");
			d40.setCashFlowType("DR");
			d40.setValueDate(selectedBatch.getPostDate()); // postdate SELECT *
															// FROM D010004
															// WHERE LBrCode =9
															// AND EntryDate =
															// '19-APR-2016' AND
															// BatchCd = 'ABBTR'

			String bookType = getBookType(brCode, selectedBatch.getId().getBatchCd());
			if (bookType == null) {
				Logger.error("BookType Not Found.");
				isAborted = true;
				abortCode = Code.REMBOOKTYPEERROR;
				abortReason = "BookType Not Found.";
				return;
			}
			logger.error("BookType is : " + bookType);
			d40.setBookType(bookType); // SELECT BookType FROM D010001 WHERE
										// LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('D');
			d40.setVcrAcctId(acctId);
			d40.setMainAcctId(acctId);

			short moduleType = getModuleType(brCode, acctId.substring(0, 8).trim());
			if (moduleType == 0) {
				Logger.error("ModuleType Not Found.");
				isAborted = true;
				abortCode = Code.REMMODULETYPEERROR;
				abortReason = "ModuleType Not Found.";
				return;
			}
			logger.error("ModuleType is : " + moduleType);
			d40.setMainModType(moduleType); // SELECT ModuleType FROM D009021
											// WHERE LBrCode =9 AND PrdCd = 'SB
			d40.setVcrModType(moduleType); // SELECT ModuleType FROM D009021
											// WHERE LBrCode =9 AND PrdCd = 'SB
			d40.setTrnCurCd("INR");
			d40.setFcyTrnAmt(amount);
			d40.setLcyConvRate(1);
			d40.setLcyTrnAmt(amount);
			d40.setInstrBankCd((short) 0);
			d40.setInstrBranchCd((short) 0);
			d40.setInstrType((short) 99); // Depend on chanel
			d40.setInstrNo(rrn); // RRN
			d40.setInstrDate(new Date()); // Blank
			d40.setParticulars(narration); // param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); //
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');

			int usrCode2 = getUsrCode("WEB");
			this.usrCode = usrCode2;
			if (usrCode2 == 0) {
				Logger.error("UsrCode Not Found.");
				isAborted = true;
				abortCode = Code.REMUSRCODEERROR;
				abortReason = "UsrCode Not Found.";
				return;
			}
			logger.error("UsrCode is : " + usrCode2);
			d40.setMaker(usrCode2); // SELECT UsrCode2 FROM D002001 WHERE
									// UsrCode1 = 'WEB'
			d40.setMakerDate(selectedBatch.getId().getEntryDate()); // EntryDate
			d40.setMakerTime(Integer.parseInt(timeFormat.format(new Date()).substring(0, 8)));
			d40.setChecker1(usrCode2); // SELECT UsrCode2 FROM D002001 WHERE
										// UsrCode1 = 'WEB'
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
			d40.setPostTime(Integer.parseInt(timeFormat.format(new Date()).substring(0, 8))); // Time
																								// HHMMSSTT

			session.save(d40);
		//	session.flush();
		} catch (Exception e) {
			isAborted = true;
			abortCode = "EX";
			abortReason = "Exception in Debit.";
			e.printStackTrace();
		}

	}

	private void credit(int brCode, String acctId, String tType, int setNo, int scrollNo, String narration) {
		destSetNo = setNo;
		destScrollNo = scrollNo;

		try {

			Date openDate = getOpenDate(brCode); // Get Open Date
			if (openDate == null) {
				Logger.error("Open Date Not Found. Aborting Transaction");
				isAborted = true;
				abortCode = Code.BENOPENDATEERROR;
				abortReason = "Open Date Not Found";
				return;
			}
			logger.error("Open Date is " + openDate);
			benEntryDate = openDate;

			// Get BatchCodes from properties file.
			String batchCode = Props.getBatchProperty(tType);
			logger.error("Batch Code Form Properties File : " + batchCode);
			String batchCodes[] = batchCode.split("~");
			logger.error("Number of Batches is " + batchCodes.length + "\t Names are " + Arrays.toString(batchCodes));
			if (batchCodes == null || batchCodes.length < 1) {
				Logger.error("Batch Codes Not Found in Properties File.");
				isAborted = true;
				abortCode = Code.BENBATCHCODEERROR;
				abortReason = "Batch Codes Not Found in Properties File.";
				return;
			}

			/// Get Selected Batch.
			D010004 selectedBatch = getSelectedBatch(brCode, batchCodes, openDate);
			if (selectedBatch == null) {
				Logger.error("No Active Batch Found.");
				isAborted = true;
				abortCode = Code.BENBATCHERROR;
				abortReason = "No Active Batch Found.";
				return;
			}

			logger.error("Selected Batch : " + selectedBatch.getId().getBatchCd());
			benSelectedBatch = selectedBatch;

			D009040 d40 = new D009040();
			D009040Id id40 = new D009040Id();
			id40.setBatchCd(selectedBatch.getId().getBatchCd()); // SELECT *
																	// FROM
																	// D010004
																	// WHERE
																	// LBrCode
																	// =9 AND
																	// EntryDate
																	// =
																	// '19-APR-2016'

			id40.setEntryDate(selectedBatch.getId().getEntryDate()); // SELECT
																		// Value
																		// FROM
																		// D001004
																		// WHERE
																		// LBrCode
																		// = 9
																		// AND
																		// Code
																		// =
																		// 'LASTOPENDATE'
			id40.setLbrCode(brCode);
			id40.setSetNo(setNo);

			id40.setScrollNo(scrollNo);

			d40.setId(id40);
			d40.setMainScrollNo(scrollNo); // Scroll No
			d40.setPostDate(selectedBatch.getPostDate()); // SELECT * FROM
															// D010004 WHERE
															// LBrCode =9 AND
															// EntryDate =
															// '19-APR-2016' AND
															// BatchCd = 'ABBTR'
			d40.setFeffDate(selectedBatch.getFeffDate()); // SELECT * FROM
															// D010004 WHERE
															// LBrCode =9 AND
															// EntryDate =
															// '19-APR-2016' AND
															// BatchCd = 'ABBTR'
			d40.setActivityType("CR");
			d40.setCashFlowType("CR");
			d40.setValueDate(selectedBatch.getPostDate()); // postdate SELECT *
															// FROM D010004
															// WHERE LBrCode =9
															// AND EntryDate =
															// '19-APR-2016' AND
															// BatchCd = 'ABBTR'

			String bookType = getBookType(brCode, selectedBatch.getId().getBatchCd());
			if (bookType == null) {
				Logger.error("BookType Not Found.");
				isAborted = true;
				abortCode = Code.BENBOOKTYPEERROR;
				abortReason = "BookType Not Found.";
				return;
			}
			logger.error("BookType is : " + bookType);
			d40.setBookType(bookType); // SELECT BookType FROM D010001 WHERE
										// LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('C');
			d40.setVcrAcctId(acctId);
			d40.setMainAcctId(acctId);

			short moduleType = getModuleType(brCode, acctId.substring(0, 8).trim());
			if (moduleType == 0) {
				Logger.error("ModuleType Not Found.");
				isAborted = true;
				abortCode = Code.BENMODULETYPEERROR;
				abortReason = "ModuleType Not Found.";
				return;
			}
			logger.error("ModuleType is : " + moduleType);
			d40.setMainModType(moduleType); // SELECT ModuleType FROM D009021
											// WHERE LBrCode =9 AND PrdCd = 'SB
			d40.setVcrModType(moduleType); // SELECT ModuleType FROM D009021
											// WHERE LBrCode =9 AND PrdCd = 'SB
			d40.setTrnCurCd("INR");
			d40.setFcyTrnAmt(amount);
			d40.setLcyConvRate(1);
			d40.setLcyTrnAmt(amount);
			d40.setInstrBankCd((short) 0);
			d40.setInstrBranchCd((short) 0);
			d40.setInstrType((short) 99); // Depend on chanel
			d40.setInstrNo(rrn); // RRN
			d40.setInstrDate(new Date()); // Blank
			d40.setParticulars(narration); // param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); //
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');

			int usrCode2 = getUsrCode("WEB");
			if (usrCode2 == 0) {
				Logger.error("UsrCode Not Found.");
				isAborted = true;
				abortCode = Code.BENUSRCODEERROR;
				abortReason = "UsrCode Not Found.";
				return;
			}
			logger.error("UsrCode is : " + usrCode2);
			d40.setMaker(usrCode2); // SELECT UsrCode2 FROM D002001 WHERE
									// UsrCode1 = 'WEB'
			d40.setMakerDate(selectedBatch.getId().getEntryDate()); // EntryDate
			d40.setMakerTime(Integer.parseInt(timeFormat.format(new Date()).substring(0, 8)));
			d40.setChecker1(usrCode2); // SELECT UsrCode2 FROM D002001 WHERE
										// UsrCode1 = 'WEB'
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
			d40.setPostTime(Integer.parseInt(timeFormat.format(new Date()).substring(0, 8))); // Time
																								// HHMMSSTT

			session.save(d40);
			//session.flush();
		} catch (Exception e) {
			isAborted = true;
			abortCode = "EX";
			abortReason = "Exception in Credit.";
			e.printStackTrace();
		}

	}

	private void creditABB(int brCode, String acctId, String tType, int setNo, int scrollNo, String narration,
			int tbrCode) {
		sourceScrollNo = scrollNo;
		try {

			Date openDate = getOpenDate(brCode); // Get Open Date
			if (openDate == null) {
				Logger.error("Open Date Not Found. Aborting Transaction");
				isAborted = true;
				abortCode = Code.BENOPENDATEERROR;
				abortReason = "Open Date Not Found";
				return;
			}
			logger.error("Open Date is " + openDate);

			// Get BatchCodes from properties file.
			String batchCode = Props.getBatchProperty(tType);
			logger.error("Batch Code Form Properties File : " + batchCode);
			String batchCodes[] = batchCode.split("~");
			logger.error("Number of Batches is " + batchCodes.length + "\t Names are " + Arrays.toString(batchCodes));
			if (batchCodes == null || batchCodes.length < 1) {
				Logger.error("Batch Codes Not Found in Properties File.");
				isAborted = true;
				abortCode = Code.BENBATCHCODEERROR;
				abortReason = "Batch Codes Not Found in Properties File.";
				return;
			}

			/// Get Selected Batch.
			D010004 selectedBatch = getSelectedBatch(brCode, batchCodes, openDate);
			if (selectedBatch == null) {
				Logger.error("No Active Batch Found.");
				isAborted = true;
				abortCode = Code.BENBATCHERROR;
				abortReason = "No Active Batch Found.";
				return;
			}

			logger.error("Selected Batch : " + selectedBatch.getId().getBatchCd());

			D009040 d40 = new D009040();
			D009040Id id40 = new D009040Id();
			id40.setBatchCd(selectedBatch.getId().getBatchCd()); // SELECT *
																	// FROM
																	// D010004
																	// WHERE
																	// LBrCode
																	// =9 AND
																	// EntryDate
																	// =
																	// '19-APR-2016'

			id40.setEntryDate(selectedBatch.getId().getEntryDate()); // SELECT
																		// Value
																		// FROM
																		// D001004
																		// WHERE
																		// LBrCode
																		// = 9
																		// AND
																		// Code
																		// =
																		// 'LASTOPENDATE'
			id40.setLbrCode(brCode);
			id40.setSetNo(setNo);

			id40.setScrollNo(scrollNo);

			d40.setId(id40);
			d40.setMainScrollNo(scrollNo); // Scroll No
			d40.setPostDate(selectedBatch.getPostDate()); // SELECT * FROM
															// D010004 WHERE
															// LBrCode =9 AND
															// EntryDate =
															// '19-APR-2016' AND
															// BatchCd = 'ABBTR'
			d40.setFeffDate(selectedBatch.getFeffDate()); // SELECT * FROM
															// D010004 WHERE
															// LBrCode =9 AND
															// EntryDate =
															// '19-APR-2016' AND
															// BatchCd = 'ABBTR'
			d40.setActivityType("ABB");
			d40.setCashFlowType("ABBCR");
			d40.setValueDate(selectedBatch.getPostDate()); // postdate SELECT *
															// FROM D010004
															// WHERE LBrCode =9
															// AND EntryDate =
															// '19-APR-2016' AND
															// BatchCd = 'ABBTR'

			String bookType = getBookType(brCode, selectedBatch.getId().getBatchCd());
			if (bookType == null) {
				Logger.error("BookType Not Found.");
				isAborted = true;
				abortCode = Code.BENBOOKTYPEERROR;
				abortReason = "BookType Not Found.";
				return;
			}
			logger.error("BookType is : " + bookType);
			d40.setBookType(bookType); // SELECT BookType FROM D010001 WHERE
										// LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('C');
			d40.setVcrAcctId(acctId);
			d40.setMainAcctId(acctId);

			short moduleType = getModuleType(brCode, acctId.substring(0, 8).trim());
			if (moduleType == 0) {
				Logger.error("ModuleType Not Found.");
				isAborted = true;
				abortCode = Code.BENMODULETYPEERROR;
				abortReason = "ModuleType Not Found.";
				return;
			}
			logger.error("ModuleType is : " + moduleType);
			d40.setMainModType(moduleType); // SELECT ModuleType FROM D009021
											// WHERE LBrCode =9 AND PrdCd = 'SB
			d40.setVcrModType(moduleType); // SELECT ModuleType FROM D009021
											// WHERE LBrCode =9 AND PrdCd = 'SB
			d40.setTrnCurCd("INR");
			d40.setFcyTrnAmt(amount);
			d40.setLcyConvRate(1);
			d40.setLcyTrnAmt(amount);
			d40.setInstrBankCd((short) brCode);
			d40.setInstrBranchCd((short) tbrCode);
			d40.setInstrType((short) 99); // Depend on chanel
			d40.setInstrNo(rrn); // RRN
			d40.setInstrDate(new Date()); // Blank
			d40.setParticulars(narration); // param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); //
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');

			int usrCode2 = getUsrCode("WEB");
			if (usrCode2 == 0) {
				Logger.error("UsrCode Not Found.");
				isAborted = true;
				abortCode = Code.BENUSRCODEERROR;
				abortReason = "UsrCode Not Found.";
				return;
			}
			logger.error("UsrCode is : " + usrCode2);
			d40.setMaker(usrCode2); // SELECT UsrCode2 FROM D002001 WHERE
									// UsrCode1 = 'WEB'
			d40.setMakerDate(selectedBatch.getId().getEntryDate()); // EntryDate
			d40.setMakerTime(Integer.parseInt(timeFormat.format(new Date()).substring(0, 8)));
			d40.setChecker1(usrCode2); // SELECT UsrCode2 FROM D002001 WHERE
										// UsrCode1 = 'WEB'
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
			d40.setPostTime(Integer.parseInt(timeFormat.format(new Date()).substring(0, 8))); // Time
																								// HHMMSSTT

			session.save(d40);
		//	session.flush();
			crABB = d40;
		} catch (Exception e) {
			isAborted = true;
			abortCode = "EX";
			abortReason = "Exception in Credit.";
			e.printStackTrace();
		}

	}

	private void debitABB(int brCode, String acctId, String tType, int setNo, int scrollNo, String narration) {
		try {

			Date openDate = getOpenDate(brCode); // Get Open Date
			if (openDate == null) {
				Logger.error("Open Date Not Found. Aborting Transaction");
				isAborted = true;
				abortCode = Code.REMOPENDATEERROR;
				abortReason = "Open Date Not Found";
				return;
			}
			logger.error("Open Date is " + openDate);

			// Get BatchCodes from properties file.
			String batchCode = Props.getBatchProperty(tType);
			logger.error("Batch Code Form Properties File : " + batchCode);
			String batchCodes[] = batchCode.split("~");
			logger.error("Number of Batches is " + batchCodes.length + "\t Names are " + Arrays.toString(batchCodes));
			if (batchCodes == null || batchCodes.length < 1) {
				Logger.error("Batch Codes Not Found in Properties File.");
				isAborted = true;
				abortCode = Code.REMBATCHCODEERROR;
				abortReason = "Batch Codes Not Found in Properties File.";
				return;
			}

			/// Get Selected Batch.
			D010004 selectedBatch = getSelectedBatch(brCode, batchCodes, openDate);
			if (selectedBatch == null) {
				Logger.error("No Active Batch Found.");
				isAborted = true;
				abortCode = Code.REMBATCHERROR;
				abortReason = "No Active Batch Found.";
				return;
			}

			logger.error("Selected Batch : " + selectedBatch.getId().getBatchCd());

			D009040 d40 = new D009040();
			D009040Id id40 = new D009040Id();
			id40.setBatchCd(selectedBatch.getId().getBatchCd()); // SELECT *
																	// FROM
																	// D010004
																	// WHERE
																	// LBrCode
																	// =9 AND
																	// EntryDate
																	// =
																	// '19-APR-2016'

			id40.setEntryDate(selectedBatch.getId().getEntryDate()); // SELECT
																		// Value
																		// FROM
																		// D001004
																		// WHERE
																		// LBrCode
																		// = 9
																		// AND
																		// Code
																		// =
																		// 'LASTOPENDATE'
			id40.setLbrCode(brCode);
			id40.setSetNo(setNo);

			id40.setScrollNo(scrollNo);

			d40.setId(id40);
			d40.setMainScrollNo(scrollNo); // Scroll No
			d40.setPostDate(selectedBatch.getPostDate()); // SELECT * FROM
															// D010004 WHERE
															// LBrCode =9 AND
															// EntryDate =
															// '19-APR-2016' AND
															// BatchCd = 'ABBTR'
			d40.setFeffDate(selectedBatch.getFeffDate()); // SELECT * FROM
															// D010004 WHERE
															// LBrCode =9 AND
															// EntryDate =
															// '19-APR-2016' AND
															// BatchCd = 'ABBTR'
			d40.setActivityType("ABBREM");
			d40.setCashFlowType("ABBDR");
			d40.setValueDate(selectedBatch.getPostDate()); // postdate SELECT *
															// FROM D010004
															// WHERE LBrCode =9
															// AND EntryDate =
															// '19-APR-2016' AND
															// BatchCd = 'ABBTR'

			String bookType = getBookType(brCode, selectedBatch.getId().getBatchCd());
			if (bookType == null) {
				Logger.error("BookType Not Found.");
				isAborted = true;
				abortCode = Code.REMBOOKTYPEERROR;
				abortReason = "BookType Not Found.";
				return;
			}
			logger.error("BookType is : " + bookType);
			d40.setBookType(bookType); // SELECT BookType FROM D010001 WHERE
										// LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('D');
			d40.setVcrAcctId(acctId);
			d40.setMainAcctId(acctId);

			short moduleType = getModuleType(brCode, acctId.substring(0, 8).trim());
			if (moduleType == 0) {
				Logger.error("ModuleType Not Found.");
				isAborted = true;
				abortCode = Code.REMMODULETYPEERROR;
				abortReason = "ModuleType Not Found.";
				return;
			}
			logger.error("ModuleType is : " + moduleType);
			d40.setMainModType(moduleType); // SELECT ModuleType FROM D009021
											// WHERE LBrCode =9 AND PrdCd = 'SB
			d40.setVcrModType(moduleType); // SELECT ModuleType FROM D009021
											// WHERE LBrCode =9 AND PrdCd = 'SB
			d40.setTrnCurCd("INR");
			d40.setFcyTrnAmt(amount);
			d40.setLcyConvRate(1);
			d40.setLcyTrnAmt(amount);
			d40.setInstrBankCd((short) 0);
			d40.setInstrBranchCd((short) 0);
			d40.setInstrType((short) 99); // Depend on chanel
			d40.setInstrNo(rrn); // RRN
			d40.setInstrDate(new Date()); // Blank
			d40.setParticulars(narration); // param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); //
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');

			int usrCode2 = getUsrCode("WEB");
			if (usrCode2 == 0) {
				Logger.error("UsrCode Not Found.");
				isAborted = true;
				abortCode = Code.REMUSRCODEERROR;
				abortReason = "UsrCode Not Found.";
				return;
			}
			logger.error("UsrCode is : " + usrCode2);
			d40.setMaker(usrCode2); // SELECT UsrCode2 FROM D002001 WHERE
									// UsrCode1 = 'WEB'
			d40.setMakerDate(selectedBatch.getId().getEntryDate()); // EntryDate
			d40.setMakerTime(Integer.parseInt(timeFormat.format(new Date()).substring(0, 8)));
			d40.setChecker1(usrCode2); // SELECT UsrCode2 FROM D002001 WHERE
										// UsrCode1 = 'WEB'
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
			d40.setPostTime(Integer.parseInt(timeFormat.format(new Date()).substring(0, 8))); // Time
																								// HHMMSSTT

			session.save(d40);
		//	session.flush();
			drABB = d40;
		} catch (Exception e) {
			isAborted = true;
			abortCode = "EX";
			abortReason = "Exception in Debit.";
			e.printStackTrace();
		}

	}

	public boolean transact(String[] bitdata) {

		/*
		 * if(remBrCode == benBrCode) {
		 * logger.error("Intra Branch Transaction."); int setNo = getNextSetNo();
		 * debit(remBrCode, remPrdAcctId, "IMPS", setNo, getNextScrollNo(),
		 * narration); if(!isAborted){ credit(benBrCode, abbAcc, "IMPS", setNo,
		 * getNextScrollNo(), narration); if(!isAborted) { balance();
		 * if(!isAborted){ logger.error("Balance OK");
		 * if(bitdata[3].equalsIgnoreCase("860000")) { P2P(bitdata);
		 * if(!isAborted){ updateBatchIntraBr(); if(!isAborted){ tx.commit(); }
		 * else{ tx.rollback(); } } else{ tx.rollback(); } } else
		 * if(bitdata[3].equalsIgnoreCase("870000")){ P2A(bitdata);
		 * if(!isAborted){ updateBatchIntraBr(); if(!isAborted){ tx.commit(); }
		 * else{ tx.rollback(); } } else{ tx.rollback(); } } } else{
		 * tx.rollback(); } } else{ tx.rollback(); } } }
		 */
		/*
		 * else {
		 */
		logger.error("Inter Branch Transaction.");
		int remSetNo = getNextSetNo();
		int benSetNo = getNextSetNo();
		debit(remBrCode, remPrdAcctId, transType, remSetNo, getNextScrollNo(), narration);
		if (!isAborted) {
			logger.error("Rem Debit OK");
			creditABB(remBrCode, abbAcc, transType, remSetNo, getNextScrollNo(), narration, benBrCode);
			if (!isAborted) {
				logger.error("RemABB Credit OK");
				debitABB(benBrCode, abbAcc, "ABB", benSetNo, getNextScrollNo(), narration);
				if (!isAborted) {
					logger.error("BenABB Debit OK");
					credit(benBrCode, abbAcc, "ABB", benSetNo, getNextScrollNo(), narration);
					if (!isAborted) {
						logger.error("Ben Credit OK");
						getReconNo(remBrCode);
						if (!isAborted) {
							updateBatch();
							if (!isAborted) {
								logger.error("Recon OK");
								balance();
								if (!isAborted) {
									balanceABB();
									if (!isAborted) {
										if (bitdata[3].equalsIgnoreCase("860000")) {
											P2P(bitdata);
											if (!isAborted) {
												tx.commit();
											} else {
												tx.rollback();
											}
										} else if (bitdata[3].equalsIgnoreCase("870000")) {
											P2A(bitdata);
											if (!isAborted) {
												tx.commit();
											} else {
												tx.rollback();
											}
										}
									} else {
										tx.rollback();
									}
								} else {
									tx.rollback();
								}
							} else {
								tx.rollback();
							}
						} else {
							tx.rollback();
						}
					} else {
						tx.rollback();
					}
				} else {
					tx.rollback();
				}
			} else {
				tx.rollback();
			}
		} else {
			tx.rollback();
		}
		// }

		return !isAborted;
	}

	public void P2P(String[] bitdata) {
		logger.error("P2P D350036 Started");
		try {
			logger.error("BitData[2] : " + bitdata[2]);

			String mmid1 = bitdata[2].substring(2, 9);
			String mobNo1 = bitdata[2].substring(11);
			logger.error("Source MMID : " + mmid1 + "\tSource Mobile : " + mobNo1);
			logger.error("BitData[104] : " + bitdata[104]);
			String mmid2 = bitdata[104].substring(2, 9);
			String mobNo2 = bitdata[104].substring(11);

			D350036Id id = new D350036Id();
			id.setBatchCd(remSelectedBatch.getId().getBatchCd());
			id.setEntryDate(remSelectedBatch.getId().getEntryDate());
			id.setEntryTime(new Date());
			id.setLbrCode(remBrCode);
			id.setScrollNo(sourceScrollNo);
			id.setSetNo(sourceSetNo);
			D350036 d36 = new D350036();
			d36.setId(id);

			d36.setDrcr("DR");
			d36.setMerchMessage("");
			d36.setMerchRespCd("");
			d36.setMmid1(mmid1);
			d36.setMmid2(mmid2);
			d36.setMobNo1(mobNo1);
			d36.setMobNo2(mobNo2);
			if (!isAborted)
				d36.setResponseCd("00");
			else
				d36.setResponseCd("01");
			d36.setResponseDesc(abortReason);
			d36.setRrnNo(bitdata[37]);
			d36.setStan(bitdata[12]);
			d36.setTranAmt(amount);
			d36.setTransactionDate(new Date());

			session.save(d36);
		//	session.flush();
		} catch (Exception e) {
			isAborted = true;
			abortCode = "36";
			abortReason = "Exception in saving D350036";
			e.printStackTrace();
		}
		logger.error("P2P D350036 Ended");
	}

	public void P2A(String[] bitdata) {
		logger.error("P2A D350037 Started");
		try {
			String mmid1 = bitdata[2].substring(2, 9);
			String mobNo1 = bitdata[2].substring(11);
			logger.error("Source MMID : " + mmid1 + "\tSource Mobile : " + mobNo1);
			String acctID = bitdata[103].substring(2);
			String ifsc = bitdata[104].substring(3);
			logger.error("Source Acct : " + acctID + "\tSource IFSC : " + ifsc);
			D350037Id id = new D350037Id();
			id.setBatchCd(remSelectedBatch.getId().getBatchCd());
			id.setEntryDate(remSelectedBatch.getId().getEntryDate());
			id.setEntryTime(new Date());
			id.setLbrCode(remBrCode);
			id.setScrollNo(sourceScrollNo);
			id.setSetNo(sourceSetNo);
			D350037 d37 = new D350037();
			d37.setId(id);

			d37.setDrcr("DR");
			d37.setMmid1(mmid1);
			d37.setMobNo1(mobNo1);
			d37.setAccNo(acctID);
			d37.setIfscCd(ifsc);
			if (!isAborted)
				d37.setResponseCd("00");
			else
				d37.setResponseCd("01");
			d37.setResponseDesc("ABCD");
			d37.setRrnNo(bitdata[37]);
			d37.setStan(bitdata[12]);
			d37.setTranAmt(amount);
			d37.setTransactionDate(new Date());
			d37.setRrsponseCd(" ");

			session.save(d37);
		//	session.flush();
			logger.error("P2A D350037 Ended");
		} catch (Exception e) {
			e.printStackTrace();
			abortCode = "37";
			abortReason = "Exception in saving D350037";
			e.printStackTrace();
		}
	}

	public void billPay(String[] bitdata) {
		logger.error("billPay D350037 Started");
		try {
			String mmid1 = bitdata[2].substring(2, 9);
			String mobNo1 = bitdata[2].substring(11);
			logger.error("Source MMID : " + mmid1 + "\tSource Mobile : " + mobNo1);
			String acctID = bitdata[103].substring(2);
			String ifsc = bitdata[104].substring(3);
			logger.error("Source Acct : " + acctID + "\tSource IFSC : " + ifsc);
			D350037Id id = new D350037Id();
			id.setBatchCd(remSelectedBatch.getId().getBatchCd());
			id.setEntryDate(remSelectedBatch.getId().getEntryDate());
			id.setEntryTime(new Date());
			id.setLbrCode(remBrCode);
			id.setScrollNo(sourceScrollNo);
			id.setSetNo(sourceSetNo);
			D350037 d37 = new D350037();
			d37.setId(id);

			d37.setDrcr("DR");
			d37.setMmid1(mmid1);
			d37.setMobNo1(mobNo1);
			d37.setAccNo(acctID);
			d37.setIfscCd(ifsc);
			if (!isAborted)
				d37.setResponseCd("00");
			else
				d37.setResponseCd("01");
			d37.setResponseDesc("ABCD");
			d37.setRrnNo(bitdata[37]);
			d37.setStan(bitdata[12]);
			d37.setTranAmt(amount);
			d37.setTransactionDate(new Date());
			d37.setRrsponseCd(" ");

			session.save(d37);
		//	session.flush();
			logger.error("P2A D350037 Ended");
		} catch (Exception e) {
			e.printStackTrace();
			abortCode = "37";
			abortReason = "Exception in saving D350037";
			e.printStackTrace();
		}
	}

	public void balance() {
		logger.error("Transaction Amount : " + amount);
		try {
			D009022Id remId = new D009022Id();
			remId.setLbrCode(remBrCode);
			remId.setPrdAcctId(remPrdAcctId);
			D009022 remAcct = session.get(D009022.class, remId);

			logger.error("Rem Amount Before : " + remAcct.getActClrBalFcy());

			if ((remAcct.getActClrBalFcy() - remAcct.getTotalLienFcy()) > amount) {
				logger.error("Final Amount : " + (remAcct.getActClrBalFcy() - amount));
				remAcct.setActClrBalFcy(remAcct.getActClrBalFcy() - amount);
				remAcct.setShdClrBalFcy(remAcct.getShdClrBalFcy() - amount);
				remAcct.setShdTotBalFcy(remAcct.getShdTotBalFcy() - amount);
				remAcct.setActTotBalFcy(remAcct.getActTotBalFcy() - amount);
				remAcct.setActTotBalLcy(remAcct.getActTotBalLcy() - amount);

				session.update(remAcct);
			//	session.flush();

			} else {
				logger.error("Insufficient Funds ");
				isAborted = true;
				abortCode = "51";
				abortReason = "Insufficient Funds";
				return;
			}

			logger.error("Rem Amount After : " + remAcct.getActClrBalFcy());
			/*
			 * D009022Id benId = new D009022Id(); benId.setLbrCode(benBrCode);
			 * benId.setPrdAcctId(benPrdAcctId);
			 * 
			 * 
			 * D009022 benAcct = session.get(D009022.class, benId);
			 * logger.error("Ben Amount Before : "+benAcct.getActClrBalFcy());
			 * benAcct.setActClrBalFcy(benAcct.getActClrBalFcy()+amount);
			 * benAcct.setShdClrBalFcy(benAcct.getShdClrBalFcy()+amount);
			 * benAcct.setShdTotBalFcy(benAcct.getShdTotBalFcy()+amount);
			 * benAcct.setActTotBalFcy(benAcct.getActTotBalFcy()+amount);
			 * benAcct.setActTotBalLcy(benAcct.getActTotBalLcy()+amount);
			 * logger.error("Ben Amount After : "+benAcct.getActClrBalFcy());
			 * session.update(benAcct); session.flush();
			 */

		} catch (Exception e) {
			e.printStackTrace();
			isAborted = true;
			abortCode = "EX";
			abortReason = "Exception in Balance Effect";
			return;
		}
	}

	public static String balance(int remBrCode, String remPrdAcctId, double amount, String type) {
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		if (type.equalsIgnoreCase("D")) {
			logger.error("Transaction Amount : " + amount);
			try {
				D009022Id remId = new D009022Id();
				remId.setLbrCode(remBrCode);
				remId.setPrdAcctId(remPrdAcctId);
				D009022 remAcct = session.get(D009022.class, remId);

				logger.error("Rem Amount Before : " + remAcct.getActClrBalFcy());

				if (DataUtils.isOverDraftAccount(remBrCode, remPrdAcctId)) {
					logger.error("Final Amount : " + (remAcct.getActClrBalFcy() - amount));
					remAcct.setActClrBalFcy(remAcct.getActClrBalFcy() - amount);
					remAcct.setShdClrBalFcy(remAcct.getShdClrBalFcy() - amount);
					remAcct.setShdTotBalFcy(remAcct.getShdTotBalFcy() - amount);
					remAcct.setActTotBalFcy(remAcct.getActTotBalFcy() - amount);
					remAcct.setActTotBalLcy(remAcct.getActTotBalLcy() - amount);
					session.update(remAcct);
					t.commit();
					session.close();
					session = null;
					t = null;

				} else

				if ((remAcct.getActClrBalFcy() - remAcct.getTotalLienFcy()) >= amount) {
					logger.error("Final Amount : " + (remAcct.getActClrBalFcy() - amount));
					remAcct.setActClrBalFcy(remAcct.getActClrBalFcy() - amount);
					remAcct.setShdClrBalFcy(remAcct.getShdClrBalFcy() - amount);
					remAcct.setShdTotBalFcy(remAcct.getShdTotBalFcy() - amount);
					remAcct.setActTotBalFcy(remAcct.getActTotBalFcy() - amount);
					remAcct.setActTotBalLcy(remAcct.getActTotBalLcy() - amount);
					session.update(remAcct);
					t.commit();
					session.close();
					session = null;
					t = null;
				} else {
					session.close();
					logger.error("Insufficient Funds ");
					return "51";
				}
				return "" + remAcct.getActClrBalFcy();
			} catch (Exception e) {
				session.close();
				e.printStackTrace();
				return "99";
			}
		} else {
			logger.error("Transaction Amount : " + amount);
			try {
				D009022Id remId = new D009022Id();
				remId.setLbrCode(remBrCode);
				remId.setPrdAcctId(remPrdAcctId);
				D009022 remAcct = session.get(D009022.class, remId);

				logger.error("Rem Amount Before : " + remAcct.getActClrBalFcy());

				logger.error("Final Amount : " + (remAcct.getActClrBalFcy() + amount));
				remAcct.setActClrBalFcy(remAcct.getActClrBalFcy() + amount);
				remAcct.setShdClrBalFcy(remAcct.getShdClrBalFcy() + amount);
				remAcct.setShdTotBalFcy(remAcct.getShdTotBalFcy() + amount);
				remAcct.setActTotBalFcy(remAcct.getActTotBalFcy() + amount);
				remAcct.setActTotBalLcy(remAcct.getActTotBalLcy() + amount);

				session.update(remAcct);
				t.commit();
				session.close();
				session = null;
				t = null;

				return "" + remAcct.getActClrBalFcy();
			} catch (Exception e) {
				e.printStackTrace();
				return "99";
			}
		}

	}
	public static String balanceOld(int remBrCode, String remPrdAcctId, double amount, String type, Session session) {
		logger.error(" remBrCode::>>" + remBrCode+" remPrdAcctId::>>" + remPrdAcctId);
		try {
			D009021 creditProductMaster = session.get(D009021.class, new D009021Id(remBrCode, remPrdAcctId.substring(0, 8).trim()));
			if('P'!=creditProductMaster.getAcctOpenLevel()) {
			D009022Id remId = new D009022Id();
			remId.setLbrCode(remBrCode);
			remId.setPrdAcctId(remPrdAcctId);
			D009022 remAcct = session.get(D009022.class, remId);
			ArrayList<Double> lists = new ArrayList<>();
			lists.add(Double.valueOf(remAcct.getActClrBalFcy()));
			lists.add(Double.valueOf(remAcct.getActTotBalFcy()));
			lists.add(Double.valueOf(remAcct.getActTotBalLcy()));
			lists.add(Double.valueOf(remAcct.getShdClrBalFcy()));
			lists.add(Double.valueOf(remAcct.getShdTotBalFcy()));
			double availBal = Collections.min(lists) - remAcct.getTotalLienFcy();
			if (type.equalsIgnoreCase("D")) {
				logger.error("Transaction Amount : " + amount);
					logger.error("Rem Amount Before : " + remAcct.getActClrBalFcy());
					if (DataUtils.isOverDraftAccount(remBrCode, remPrdAcctId, creditProductMaster)) {
						logger.error("Final Amount : " + (remAcct.getActClrBalFcy() - amount));
						String Q = "UPDATE D009022 SET ShdClrBalFcy = ShdClrBalFcy-" + amount
								+ "	, ShdTotBalFcy = ShdTotBalFcy-" + amount + "	, ActClrBalFcy = ActClrBalFcy-" + amount
								+ "	, ActTotBalFcy = ActTotBalFcy-" + amount + "	, ActTotBalLcy = ActTotBalLcy-" + amount
								+ " WHERE LBrCode = '" + remBrCode + "' AND PrdAcctId = '" + remPrdAcctId + "'";
						Query<D009022> query = session.createQuery(Q);
						if(query.executeUpdate()>0)
							return (remAcct.getActClrBalFcy()-remAcct.getTotalLienFcy()-amount)+"";
						return "99" ;
					} else if (availBal >= amount) {
						logger.error("Final Amount : " + (remAcct.getActClrBalFcy() - amount));
						String Q = "UPDATE D009022 SET ShdClrBalFcy = ShdClrBalFcy-" + amount
								+ "	, ShdTotBalFcy = ShdTotBalFcy-" + amount + "	, ActClrBalFcy = ActClrBalFcy-" + amount
								+ "	, ActTotBalFcy = ActTotBalFcy-" + amount + "	, ActTotBalLcy = ActTotBalLcy-" + amount
								+ " WHERE LBrCode = '" + remBrCode + "' AND PrdAcctId = '" + remPrdAcctId + "'";
						Query<D009022> query = session.createQuery(Q);
						if(query.executeUpdate()>0)
							return (remAcct.getActClrBalFcy()-remAcct.getTotalLienFcy()-amount)+"";
						return "99" ;
					} else {
						logger.error("Insufficient Funds ");
						return "51";
					}
			} else {
				logger.error("Transaction Amount : " + amount);
					logger.error("Rem Amount Before : " + remAcct.getActClrBalFcy());
					logger.error("Final Amount : " + (remAcct.getActClrBalFcy() + amount));
					String Q = "UPDATE D009022 SET ShdClrBalFcy = ShdClrBalFcy+" + amount
							+ "	, ShdTotBalFcy = ShdTotBalFcy+" + amount + "	, ActClrBalFcy = ActClrBalFcy+" + amount
							+ "	, ActTotBalFcy = ActTotBalFcy+" + amount + "	, ActTotBalLcy = ActTotBalLcy+" + amount
							+ " WHERE LBrCode = '" + remBrCode + "' AND PrdAcctId = '" + remPrdAcctId + "'";
					Query<D009022> query = session.createQuery(Q);
					if(query.executeUpdate()>0)
						return (remAcct.getActClrBalFcy()-remAcct.getTotalLienFcy()-amount)+"";
					return "99" ;
			}	
			}return amount+"";
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("",e);
			return "99";
		}
	}
	public static String balance(int remBrCode, String remPrdAcctId, double amount, String type, Session session) {
		logger.error(" remBrCode::>>" + remBrCode+" remPrdAcctId::>>" + remPrdAcctId);
		try {
			D009021 creditProductMaster = session.get(D009021.class, new D009021Id(remBrCode, remPrdAcctId.substring(0, 8).trim()));
			if('P'!=creditProductMaster.getAcctOpenLevel()) {
			D009022Id remId = new D009022Id();
			remId.setLbrCode(remBrCode);
			remId.setPrdAcctId(remPrdAcctId);
			D009022 remAcct = session.get(D009022.class, remId);
			ArrayList<Double> lists = new ArrayList<>();
			lists.add(Double.valueOf(remAcct.getActClrBalFcy()));
			lists.add(Double.valueOf(remAcct.getActTotBalFcy()));
			lists.add(Double.valueOf(remAcct.getActTotBalLcy()));
			lists.add(Double.valueOf(remAcct.getShdClrBalFcy()));
			lists.add(Double.valueOf(remAcct.getShdTotBalFcy()));
			double availBal = Collections.min(lists) - remAcct.getTotalLienFcy();
			if (type.equalsIgnoreCase("D")) {
				logger.error("Transaction Amount : " + amount);
					logger.error("Rem Amount Before : " + remAcct.getActClrBalFcy());
					if (DataUtils.isOverDraftAccount(remBrCode, remPrdAcctId, creditProductMaster)) {
						logger.error("Final Amount : " + (remAcct.getActClrBalFcy() - amount));
						remAcct.setShdClrBalFcy(remAcct.getShdClrBalFcy()-amount);
						remAcct.setShdTotBalFcy(remAcct.getShdTotBalFcy()-amount);
						remAcct.setActClrBalFcy(remAcct.getActClrBalFcy()-amount);
						remAcct.setActTotBalFcy(remAcct.getActTotBalFcy()-amount);
						remAcct.setActTotBalLcy(remAcct.getActTotBalLcy()-amount);
						
						session.update(remAcct);
							return (remAcct.getActClrBalFcy()-remAcct.getTotalLienFcy()-amount)+"";
					} else if (availBal >= amount) {
						logger.error("Final Amount : " + (remAcct.getActClrBalFcy() - amount));
						remAcct.setShdClrBalFcy(remAcct.getShdClrBalFcy()-amount);
						remAcct.setShdTotBalFcy(remAcct.getShdTotBalFcy()-amount);
						remAcct.setActClrBalFcy(remAcct.getActClrBalFcy()-amount);
						remAcct.setActTotBalFcy(remAcct.getActTotBalFcy()-amount);
						remAcct.setActTotBalLcy(remAcct.getActTotBalLcy()-amount);
						
						session.update(remAcct);
							return (remAcct.getActClrBalFcy()-remAcct.getTotalLienFcy()-amount)+"";
					} else {
						logger.error("Insufficient Funds ");
						return "51";
					}
			} else {
				logger.error("Transaction Amount : " + amount);
					logger.error("Rem Amount Before : " + remAcct.getActClrBalFcy());
					logger.error("Final Amount : " + (remAcct.getActClrBalFcy() + amount));
					
					remAcct.setShdClrBalFcy(remAcct.getShdClrBalFcy()+amount);
					remAcct.setShdTotBalFcy(remAcct.getShdTotBalFcy()+amount);
					remAcct.setActClrBalFcy(remAcct.getActClrBalFcy()+amount);
					remAcct.setActTotBalFcy(remAcct.getActTotBalFcy()+amount);
					remAcct.setActTotBalLcy(remAcct.getActTotBalLcy()+amount);
					
					session.update(remAcct);
					return (remAcct.getActClrBalFcy()-remAcct.getTotalLienFcy()-amount)+"";
			}	
			
			}
			return amount+"";
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("",e);
			return "99";
		}
	}

	/*
	 * public static String balanceShivKrupa(int remBrCode,String
	 * remPrdAcctId,double amount,String type,Session session) {
	 * System.out.println("remBrCode::>>"+remBrCode);
	 * System.out.println("remPrdAcctId::>>"+remPrdAcctId);
	 * if(type.equalsIgnoreCase("D")) {
	 * logger.error("Transaction Amount : "+amount); try { D009022Id remId = new
	 * D009022Id(); remId.setLbrCode(remBrCode);
	 * remId.setPrdAcctId(remPrdAcctId); D009022 remAcct =
	 * session.get(D009022.class, remId);
	 * logger.error("Rem Amount Before : "+remAcct.getActClrBalFcy());
	 * if(DataUtils.isOverDraftAccount(remBrCode, remPrdAcctId)) {
	 * logger.error("Final Amount : "+(remAcct.getActClrBalFcy()-amount));
	 * remAcct.setActClrBalFcy(remAcct.getActClrBalFcy()-amount);
	 * remAcct.setShdClrBalFcy(remAcct.getShdClrBalFcy()-amount);
	 * remAcct.setShdTotBalFcy(remAcct.getShdTotBalFcy()-amount);
	 * remAcct.setActTotBalFcy(remAcct.getActTotBalFcy()-amount);
	 * remAcct.setActTotBalLcy(remAcct.getActTotBalLcy()-amount);
	 * session.update(remAcct); session.flush(); return
	 * ""+remAcct.getActClrBalFcy(); }else {
	 * logger.error("Final Amount : "+(remAcct.getActClrBalFcy()-amount));
	 * remAcct.setActClrBalFcy(remAcct.getActClrBalFcy()-amount);
	 * remAcct.setShdClrBalFcy(remAcct.getShdClrBalFcy()-amount);
	 * remAcct.setShdTotBalFcy(remAcct.getShdTotBalFcy()-amount);
	 * remAcct.setActTotBalFcy(remAcct.getActTotBalFcy()-amount);
	 * remAcct.setActTotBalLcy(remAcct.getActTotBalLcy()-amount);
	 * session.update(remAcct); return ""+remAcct.getActClrBalFcy(); } } catch
	 * (Exception e) { e.printStackTrace(); return "99"; } }else {
	 * logger.error("Transaction Amount : "+amount); try { D009022Id remId = new
	 * D009022Id(); remId.setLbrCode(remBrCode);
	 * remId.setPrdAcctId(remPrdAcctId); D009022 remAcctCr =
	 * session.get(D009022.class, remId);
	 * 
	 * logger.error("Rem Amount Before : "+remAcctCr.getActClrBalFcy());
	 * logger.error("Final Amount : "+(remAcctCr.getActClrBalFcy()+amount));
	 * remAcctCr.setActClrBalFcy(remAcctCr.getActClrBalFcy()+amount);
	 * remAcctCr.setShdClrBalFcy(remAcctCr.getShdClrBalFcy()+amount);
	 * remAcctCr.setShdTotBalFcy(remAcctCr.getShdTotBalFcy()+amount);
	 * remAcctCr.setActTotBalFcy(remAcctCr.getActTotBalFcy()+amount);
	 * remAcctCr.setActTotBalLcy(remAcctCr.getActTotBalLcy()+amount);
	 * session.update(remAcctCr); return ""+remAcctCr.getActClrBalFcy(); } catch
	 * (Exception e) { e.printStackTrace(); return "99"; } } }
	 */
	public static String balanceShivKrupa(int remBrCode, String remPrdAcctId, double amount, String type,
			Session session) {
		try {
			logger.error(" balanceShivKrupa()::> remBrCode::>>" + remBrCode + " remPrdAcctId::>>" + remPrdAcctId);
			D009022Id id=new D009022Id();
			id.setLbrCode(remBrCode);
			id.setPrdAcctId(remPrdAcctId);
			D009022 d009022=session.get(D009022.class, id);
			if (type.equalsIgnoreCase("D")) {
				String Q = "UPDATE D009022 SET ShdClrBalFcy = ShdClrBalFcy-" + amount
						+ "	, ShdTotBalFcy = ShdTotBalFcy-" + amount + "	, ActClrBalFcy = ActClrBalFcy-" + amount
						+ "	, ActTotBalFcy = ActTotBalFcy-" + amount + "	, ActTotBalLcy = ActTotBalLcy-" + amount
						+ " WHERE LBrCode = '" + remBrCode + "' AND PrdAcctId = '" + remPrdAcctId + "'";
				Query<D009022> query = session.createQuery(Q);
				if(query.executeUpdate()>0)
//					return (d009022.getActClrBalFcy()-d009022.getTotalLienFcy()-amount)+"";
					return (d009022.getActClrBalFcy()-amount)+"";
				return "99" ;
			} else {
				String Q = "UPDATE D009022 SET ShdClrBalFcy = ShdClrBalFcy+" + amount
						+ "	, ShdTotBalFcy = ShdTotBalFcy+" + amount + "	, ActClrBalFcy = ActClrBalFcy+" + amount
						+ "	, ActTotBalFcy = ActTotBalFcy+" + amount + "	, ActTotBalLcy = ActTotBalLcy+" + amount
						+ " WHERE LBrCode = '" + remBrCode + "' AND PrdAcctId = '" + remPrdAcctId + "'";
				Query<D009022> query = session.createQuery(Q);
				if(query.executeUpdate()>0)
//					return (d009022.getActClrBalFcy()-d009022.getTotalLienFcy()+amount)+"";
				return (d009022.getActClrBalFcy()+amount)+"";
				return "99" ;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("", e);
			return "99";
		}

	}

	private void balanceABB() {
		try {
			Query query = session.createQuery(
					"update D009021 set LcyBal = LcyBal +:amount , FcyBal = FcyBal + :amount where LBrCode = :lbrcode and PrdCd = 'ABB'");
			query.setParameter("amount", amount);
			query.setParameter("lbrcode", remSelectedBatch.getId().getLbrCode());
			query.executeUpdate();

			query = session.createQuery(
					"update D009021 set LcyBal = LcyBal -:amount , FcyBal = FcyBal - :amount where LBrCode = :lbrcode and PrdCd = 'ABB'");
			query.setParameter("amount", amount);
			query.setParameter("lbrcode", benSelectedBatch.getId().getLbrCode());
			query.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			abortCode = "ex";
			abortReason = "Exception in Updation of Balance in ABB Gl Account";
			isAborted = true;
		}
	}

	@SuppressWarnings("deprecation")
	private int getNextReconNo(int brCode) {
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

	public void getReconNo(int brCode) {
		try {
			logger.error("Recon Started.");
			@SuppressWarnings("deprecation")
			/*
			 * Criteria criteria =
			 * session.createCriteria(D100001.class).add(Restrictions.eq(
			 * "id.lbrCode",
			 * brCode)).setProjection(Projections.max("id.reconNo")); Integer
			 * topRecon = (Integer)criteria.uniqueResult();
			 */

			int nextRecon = getNextReconNo(brCode);
			logger.error("Next Recon No : " + nextRecon);

			if (nextRecon == -1) {
				isAborted = true;
				abortCode = "37";
				abortReason = "Recon Error.";
				logger.error("Invalid Recon Number");
				return;
			}

			D100001 d = new D100001();
			D100001Id id = new D100001Id();
			id.setLbrCode(brCode);
			id.setReconNo(nextRecon);
			d.setId(id);
			d.setRequestDate(remEntryDate);
			d.setRequestTime(new Date());
			d.setAbbTrType((short) 1); // 1
			d.setUserCd(this.usrCode); // chan ge Latter
			d.setPendingForDate(new Date()); // change Latter
			d.setHandledFlag((byte) 5); // Change Latter
			d.setEntryDate(remEntryDate); // change Latter
			d.setBatchCd(remSelectedBatch.getId().getBatchCd());
			d.setSetNo(sourceSetNo);
			d.setMainScrollNo(sourceScrollNo);
			d.setStatus('E'); // Change Latter
			d.setToBrCode(benBrCode);
			d.setToBrEntryDate(benEntryDate);
			d.setToBrBatchCd(benSelectedBatch.getId().getBatchCd());
			d.setToBrSetNo(destSetNo);
			d.setToBrMainScrollNo(destScrollNo);
			d.setToBrMainScrollNo1(destScrollNo);
			d.setToBrAcctId(abbAcc);
			d.setToBrAcctAct("ATMDR");
			d.setToBrDrCr('C');
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

			session.save(d);
			//session.flush();

			drABB.setTokenNo(nextRecon);
			String debPart = (("ReconNo = " + nextRecon + "\tFromBrCode = " + remBrCode + "\tAcctId = " + abbAcc)
					.length() > 70)
							? ("ReconNo = " + nextRecon + "\tFromBrCode = " + remBrCode + "\tAcctId = " + abbAcc)
									.substring(0, 70)
							: ("ReconNo = " + nextRecon + "\tFromBrCode = " + remBrCode + "\tAcctId = " + abbAcc);
			drABB.setParticulars(debPart);
			crABB.setTokenNo(nextRecon);
			String crdPart = (("ReconNo = " + nextRecon + "\tToBrCode = " + benBrCode + "\tAcctId = " + remPrdAcctId)
					.length() > 70)
							? ("ReconNo = " + nextRecon + "\tToBrCode = " + benBrCode + "\tAcctId = " + remPrdAcctId)
									.substring(0, 70)
							: ("ReconNo = " + nextRecon + "\tToBrCode = " + benBrCode + "\tAcctId = " + remPrdAcctId);
			crABB.setParticulars(crdPart);
			session.update(drABB);
			//session.flush();
			session.update(crABB);
		//	session.flush();

			logger.error("Recon Completed.");

		} catch (Exception e) {
			e.printStackTrace();
			isAborted = true;
			abortCode = "EX";
			abortReason = "Aborted due to error on recon Generation";
		}

	}

	private void updateBatch() {
		try {
			Query query = session.createQuery(
					"UPDATE D010004 SET TotalDrVcrs=TotalDrVcrs+1, TotalDrAmtLcy = TotalDrAmtLcy +:amt , TotalCrVcrs = TotalCrVcrs+1, TotalCrAmtLcy= TotalCrAmtLcy + :amt  WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd");
			query.setParameter("amt", amount);
			query.setParameter("lbrcode", remSelectedBatch.getId().getLbrCode());
			query.setParameter("entrydate", remSelectedBatch.getId().getEntryDate());
			query.setParameter("batchcd", remSelectedBatch.getId().getBatchCd());
			query.executeUpdate();

			logger.error("Ben Selected Batch :" + benSelectedBatch);
			query = session.createQuery(
					"UPDATE D010004 SET TotalDrVcrs=TotalDrVcrs+1, TotalDrAmtLcy = TotalDrAmtLcy +:amt , TotalCrVcrs = TotalCrVcrs+1, TotalCrAmtLcy= TotalCrAmtLcy + :amt  WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd");
			query.setParameter("amt", amount);
			query.setParameter("lbrcode", benSelectedBatch.getId().getLbrCode());
			query.setParameter("entrydate", benSelectedBatch.getId().getEntryDate());
			query.setParameter("batchcd", benSelectedBatch.getId().getBatchCd());
			query.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			isAborted = true;
			abortCode = "ex";
			abortReason = "Exception in BatchUpdate";
		}
	}

	private void updateBatchIntraBr() {
		try {
			Query query = session.createQuery(
					"UPDATE D010004 SET TotalDrVcrs=TotalDrVcrs+1, TotalDrAmtLcy = TotalDrAmtLcy +:amt , TotalCrVcrs = TotalCrVcrs+1, TotalCrAmtLcy= TotalCrAmtLcy + :amt  WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd");
			query.setParameter("amt", amount);
			query.setParameter("lbrcode", remSelectedBatch.getId().getLbrCode());
			query.setParameter("entrydate", remSelectedBatch.getId().getEntryDate());
			query.setParameter("batchcd", remSelectedBatch.getId().getBatchCd());
			query.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			isAborted = true;
			abortCode = "ex";
			abortReason = "Exception in BatchUpdate";
		}
	}

	public boolean isAborted() {
		return isAborted;
	}

	public String getAbortCode() {
		return abortCode;
	}

	public String getAbortReason() {
		return abortReason;
	}

	public static void main(String[] args) {
		// logger.error(new java.sql.Date(0));
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
		// sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			for (int i = 0; i < 1000; i++) {
				Session session = HBUtil.getSessionFactory().openSession();
				Transaction t = session.beginTransaction();
				System.out.println(
						"Balance::>>" + balanceShivKrupa(3, "SB      000000000000670700000000", 10, "D", session));
				System.out.println(
						"Balance::>>" + balanceShivKrupa(3, "SB      000000000000406200000000", 10, "C", session));
				t.commit();
				session.close();
				session = null;
				t = null;
			}

			// System.out.println("Recon No::>>"+getNextRecNoNew(1));;
			// logger.error(sdf.parse("1970-01-01-00:00:00"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
}
