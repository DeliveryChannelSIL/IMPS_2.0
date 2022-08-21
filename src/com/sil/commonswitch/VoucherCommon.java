package com.sil.commonswitch;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.pmw.tinylog.Logger;
import com.sil.constants.Code;
import com.sil.constants.MSGConstants;
import com.sil.constants.RtgsNeftHostToHostConstants;
import com.sil.domain.TransactionValidationResponse;
import com.sil.hbm.D001004;
import com.sil.hbm.D001004Id;
import com.sil.hbm.D002001;
import com.sil.hbm.D009021;
import com.sil.hbm.D009021Id;
import com.sil.hbm.D009022;
import com.sil.hbm.D009022Id;
import com.sil.hbm.D009040;
import com.sil.hbm.D009040Id;
import com.sil.hbm.D009122;
import com.sil.hbm.D009122Id;
import com.sil.hbm.D010001;
import com.sil.hbm.D010001Id;
import com.sil.hbm.D010004;
import com.sil.hbm.D010004Id;
import com.sil.hbm.D030002;
import com.sil.hbm.D030003;
import com.sil.hbm.D030003Id;
import com.sil.hbm.D100001;
import com.sil.hbm.D100002;
import com.sil.hbm.D130001;
import com.sil.hbm.D130008;
import com.sil.hbm.D130008Id;
import com.sil.hbm.D130014;
import com.sil.hbm.D130031;
import com.sil.hbm.D946020;
import com.sil.hbm.D946124;
import com.sil.hbm.D946124Id;
import com.sil.hbm.D946320;
import com.sil.hbm.D946320Id;
import com.sil.hbm.GstChargesMaster;
import com.sil.hbm.GstTransactionHistory;
import com.sil.hbm.GstTransactionHistoryId;
import com.sil.hbm.MBTRSCROLLSEQ;
import com.sil.hbm.MBTRSETSEQ;
import com.sil.hbm.RtgsMessageSecondaryDatFile;
import com.sil.hbm.RtgsMessageSecondaryDatFileId;
import com.sil.loan.LoanServiceImpl;
import com.sil.prop.ConfigurationLoader;
import com.sil.service.TransactionServiceImpl;
import com.sil.util.DateUtil;
import com.sil.util.HBUtil;
import com.sil.util.Props;

public class VoucherCommon {
	public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(VoucherCommon.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("0yyyyMMdd");
	private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmssSS");
	private static String flag = "Y";
	public boolean isAborted = false;
	public static double amt = 0;
	public static String firstBatch = "";
	public static String secondBatch = "";

	public static int getNextSetNo() {
		if (ConfigurationLoader.getParameters(false).getProperty("SQL_SERVER_VERSION").equalsIgnoreCase("2008")) {
			Session session = HBUtil.getSessionFactory().openSession();
			Transaction t = session.beginTransaction();
			try {
				D001004Id id = new D001004Id();
				id.setCode(MSGConstants.MBTR_SETNO);
				id.setLbrCode(0);
				D001004 d001004 = session.get(D001004.class, id);
				System.out.println("d001004::>>" + d001004);
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
				logger.error("", e);
				return 1;
			} finally {
				session.close();
				session = null;
				t = null;
			}
		} else {
			MBTRSETSEQ setSeq = new MBTRSETSEQ();
			try (Session session = HBUtil.getSessionFactory().openSession()) {
				session.save(setSeq);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return setSeq.getId();
		}
	}

	public static int getNextScrollNo() {
		if (ConfigurationLoader.getParameters(false).getProperty("SQL_SERVER_VERSION").equalsIgnoreCase("2008")) {
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
				logger.error("", e);
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
		MBTRSCROLLSEQ scrollSeq = new MBTRSCROLLSEQ();
		try (Session session = HBUtil.getSessionFactory().openSession()) {
			session.save(scrollSeq);
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("", e);
		}
		return scrollSeq.getId();
	}

	public static Date getOpenDate(int brCode) {
		Session session = HBUtil.getSessionFactory().openSession();
		try {
			D001004Id id = new D001004Id();
			id.setCode("LASTOPENDATE");
			id.setLbrCode(brCode);
			D001004 d04 = session.get(D001004.class, id);
			if (d04 != null) {
				return sdf.parse(d04.getValue().trim());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen())
				session.close();
			session = null;
		}
		return null;
	}

	public static Date getOpenDateNew(int brCode, Session session) {
		// Session session = HBUtil.getSessionFactory().openSession();
		try {
			D001004Id id = new D001004Id();
			id.setCode("LASTOPENDATE");
			id.setLbrCode(brCode);
			D001004 d04 = session.get(D001004.class, id);
			if (d04 != null) {
				return sdf.parse(d04.getValue().trim());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		 * finally { if(session!=null && session.isOpen()) session.close();
		 * session=null; }
		 */
		return null;
	}

	public static String getBatchNameFromBatchCode(String batchCode) {
		Session session = HBUtil.getSessionFactory().openSession();
		D001004 d04 = null;
		try {
			D001004Id id = new D001004Id();
			id.setCode(batchCode);
			id.setLbrCode(0);
			d04 = session.get(D001004.class, id);
			if (d04 != null) {
				return d04.getValue().trim();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session.isOpen())
				session.close();
			session = null;
		}
		return d04 == null ? null : d04.getValue().trim();
	}

	public static String getBatchNameFromBatchCodeNew(String batchCode, Session session) {
		D001004 d04 = null;
		try {
			D001004Id id = new D001004Id();
			id.setCode(batchCode);
			id.setLbrCode(0);
			d04 = session.get(D001004.class, id);
			if (d04 != null) {
				return d04.getValue().trim();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return d04 == null ? null : d04.getValue().trim();
	}

	public static D010004 getBatch(int lbrCode, String batchName, Date entryDate) {
		D010004 d04 = null;
		Session session = HBUtil.getSessionFactory().openSession();
		try {
			D010004Id id = new D010004Id();
			id.setBatchCd(batchName);
			id.setEntryDate(entryDate);
			id.setLbrCode(lbrCode);
			d04 = session.get(D010004.class, id);
			session.close();
			session = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null && session.isOpen())
				session.close();
			session = null;
		}
		return d04;
	}

	public static D010004 getBatchNew(int lbrCode, String batchName, Date entryDate, Session session) {
		D010004 d04 = null;
		try {
			D010004Id id = new D010004Id();
			id.setBatchCd(batchName);
			id.setEntryDate(entryDate);
			id.setLbrCode(lbrCode);
			d04 = session.get(D010004.class, id);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception:-",e);
			d04 = null;
		}
		return d04;
	}

	public static D010004 getSelectedBatch(int lbrCode, String[] batchCodes, Date entryDate) {
		Session session = HBUtil.getSessionFactory().openSession();
		D010004 batch = null;
		try {
			for (String batchCode : batchCodes) {
				String batchName = getBatchNameFromBatchCode(batchCode);
				if (batchName == null)
					continue;
				else {
					logger.error("Current Batch Name : " + batchName);
					batch = getBatch(lbrCode, batchName, entryDate);
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
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session.isOpen())
				session.close();
			session = null;
		}
		return batch;
	}

	public static D010004 getSelectedBatchNew(int lbrCode, String[] batchCodes, Date entryDate, Session session) {
		D010004 batch = null;
		try {
			for (String batchCode : batchCodes) {
				String batchName = getBatchNameFromBatchCodeNew(batchCode, session);
				if (batchName == null)
					continue;
				else {
					logger.error("Current Batch Name : " + batchName);
					batch = getBatchNew(lbrCode, batchName, entryDate, session);
					if (batch == null) {
						logger.error("Batch with Batch Name " + batchName + " Not found");
						batch = null;
					} else {
						logger.error("Selected batch is befor Status Check " + batchName);
						if (batch.getStat() == Byte.valueOf("1") || batch.getStat() == Byte.valueOf("2")) {
							logger.error("Selected batch is " + batchName);
							return batch;
						} else {
							batch = null;
							continue;
						}
					}
				}
			}
			return batch;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception ",e);
			return null;
		}
	}

	public static String getBookType(int lbrCode, String BatchCd) {
		// SELECT BookType FROM D010001 WHERE LBrCode = 9 AND Code = 'ABBTR'
		D010001 d001 = null;
		Session session = HBUtil.getSessionFactory().openSession();
		try {
			D010001Id id = new D010001Id();
			id.setCode(BatchCd);
			id.setLbrCode(lbrCode);
			d001 = session.get(D010001.class, id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session.isOpen())
				session.close();
			session = null;
		}
		if (d001 != null)
			return d001.getBookType();
		else
			return null;
	}

	public static String getBookTypeNew(int lbrCode, String BatchCd, Session session) {
		// SELECT BookType FROM D010001 WHERE LBrCode = 9 AND Code = 'ABBTR'
		D010001 d001 = null;
		// Session session = HBUtil.getSessionFactory().openSession();
		try {
			D010001Id id = new D010001Id();
			id.setCode(BatchCd);
			id.setLbrCode(lbrCode);
			d001 = session.get(D010001.class, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (d001 != null)
			return d001.getBookType();
		else
			return null;
	}

	public static short getModuleType(int lbrCode, String prdCd) {
		// SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB
		D009021 d21 = null;
		Session session = HBUtil.getSessionFactory().openSession();
		try {
			D009021Id id = new D009021Id();
			id.setLbrCode(lbrCode);
			id.setPrdCd(prdCd);
			d21 = session.get(D009021.class, id);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session.isOpen())
				session.close();
			session = null;
		}
		if (d21 != null)
			return d21.getModuleType();
		return 0;
	}

	public static short getModuleTypeNew(int lbrCode, String prdCd, Session session) {
		// SELECT ModuleType FROM D009021 WHERE LBrCode =9 AND PrdCd = 'SB
		System.out.println("<<<< :: getModuleTypeNew ::>>>>");
		System.out.println("lbrCode::>>" + lbrCode);
		System.out.println("prdCd::>>" + prdCd);
		System.out.println("Session::>>" + session);
		D009021 d21 = null;
		// Session session = HBUtil.getSessionFactory().openSession();
		try {
			D009021Id id = new D009021Id();
			id.setLbrCode(lbrCode);
			id.setPrdCd(prdCd);
			d21 = session.get(D009021.class, id);

		} catch (Exception e) {
			e.printStackTrace();
		} /*
			 * finally { if(session.isOpen()) session.close(); session=null; }
			 */
		if (d21 != null)
			return d21.getModuleType();
		return 0;
	}

	public static int getUsrCode(String usrCode1) {
		D002001 d201 = null;
		Session session = HBUtil.getSessionFactory().openSession();
		try {
			d201 = session.get(D002001.class, usrCode1);
			if (d201 != null)
				return d201.getUsrCode2();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session.isOpen())
				session.close();
			session = null;
		}
		return 0;
	}

	public static int getUsrCodeNew(String usrCode1, Session session) {
		D002001 d201 = null;
		// Session session = HBUtil.getSessionFactory().openSession();
		try {
			d201 = session.get(D002001.class, usrCode1);
			if (d201 != null)
				return d201.getUsrCode2();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	public void debit(int brCode, String acctId, String tType, int setNo, int scrollNo, String narration, double amount,
			String rrn, Session session) {
		try {
			Date openDate = getOpenDateNew(brCode, session); // Get Open Date
			if (openDate == null) {
				Logger.error("Open Date Not Found. Aborting Transaction");
				isAborted = true;
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
				return;
			}
			/// Get Selected Batch.
			D010004 selectedBatch = getSelectedBatchNew(brCode, batchCodes, openDate, session);
			if (selectedBatch == null) {
				Logger.error("No Active Batch Found.");
				isAborted = true;
				return;
			}
			logger.error("Selected Batch : " + selectedBatch.getId().getBatchCd());
			firstBatch = selectedBatch.getId().getBatchCd();
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

			String bookType = getBookTypeNew(brCode, selectedBatch.getId().getBatchCd(), session);
			if (bookType == null) {
				Logger.error("BookType Not Found.");
				isAborted = true;
				return;
			}

			logger.error("BookType is : " + bookType);
			d40.setBookType(bookType); // SELECT BookType FROM D010001 WHERE
										// LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('D');
			d40.setVcrAcctId(acctId);
			d40.setMainAcctId(acctId);

			short moduleType = getModuleTypeNew(brCode, acctId.substring(0, 8).trim(), session);
			if (moduleType == 0) {
				Logger.error("ModuleType Not Found.");
				isAborted = true;
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
			d40.setParticulars(narration.length() > 70 ? narration.substring(0, 70) : narration.trim()); // param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); //
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');

			int usrCode2 = Integer.parseInt(ConfigurationLoader.getParameters(false).getProperty("IMPS_USER"));

			if (usrCode2 == 0) {
				Logger.error("UsrCode Not Found.");
				isAborted = true;
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

			d40.setNoAuthPending((byte) 0);
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
			System.out.println("Updating Debit Batch balance=" + d40.toString());
			if (updateBatchBalance(session, amount, selectedBatch, brCode, MSGConstants.DR))
				isAborted = false;
			else
				isAborted = true;
			System.out.println("isAborted::>>" + isAborted);
		} catch (Exception e) {
			isAborted = true;
			e.printStackTrace();
		}
	}

	public void debitSameBranch(int brCode, String acctId, String tType, int setNo, int scrollNo, String narration,
			double amount, String rrn, int mainScrollNo, Session session) {
		try {

			String cashFlowType = "";
			if (tType.contains("~")) {
				String[] type = tType.split("~");
				cashFlowType = type[0];
				tType = type[1];
			}

			Date openDate = getOpenDateNew(brCode, session); // Get Open Date
			if (openDate == null) {
				Logger.error("Open Date Not Found. Aborting Transaction");
				isAborted = true;
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
				return;
			}
			/// Get Selected Batch.
			D010004 selectedBatch = getSelectedBatchNew(brCode, batchCodes, openDate, session);
			if (selectedBatch == null) {
				Logger.error("No Active Batch Found.");
				isAborted = true;
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
			d40.setMainScrollNo(mainScrollNo); // Scroll No
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

			String bookType = getBookTypeNew(brCode, selectedBatch.getId().getBatchCd(), session);
			if (bookType == null) {
				Logger.error("BookType Not Found.");
				isAborted = true;
				return;
			}

			logger.error("BookType is : " + bookType);
			d40.setBookType(bookType); // SELECT BookType FROM D010001 WHERE
										// LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('D');
			d40.setVcrAcctId(acctId);
			d40.setMainAcctId(acctId);

			short moduleType = getModuleTypeNew(brCode, acctId.substring(0, 8).trim(), session);
			if (moduleType == 0) {
				Logger.error("ModuleType Not Found.");
				isAborted = true;
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
			d40.setParticulars(narration.length() > 70 ? narration.substring(0, 70) : narration.trim()); // param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); //
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');

			int usrCode2 = Integer.parseInt(ConfigurationLoader.getParameters(false).getProperty("IMPS_USER"));
			if (usrCode2 == 0) {
				Logger.error("UsrCode Not Found.");
				isAborted = true;
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
			// ====================Below Code is commented on 25/10/2017 for
			// Code Modification ======================
			// Query query = session.createQuery("UPDATE D010004 SET
			// TotalDrVcrs=TotalDrVcrs+1, TotalDrAmtLcy = TotalDrAmtLcy +:amt
			// WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd =
			// :batchcd");
			// query.setParameter("amt", amount);
			// query.setParameter("lbrcode", brCode);
			// query.setParameter("entrydate",
			// selectedBatch.getId().getEntryDate());
			// query.setParameter("batchcd",
			// selectedBatch.getId().getBatchCd());
			// query.executeUpdate();

			if (updateBatchBalance(session, amount, selectedBatch, brCode, MSGConstants.DR))
				isAborted = false;
			else
				isAborted = true;
			System.out.println("VoucherCommon.debitSameBranch() updated ");
		} catch (Exception e) {
			isAborted = true;
			e.printStackTrace();
		}
	}

	public void debitSameBranchDDS(int brCode, String acctId, String tType, int setNo, int scrollNo, String narration,
			double amount, String rrn, int mainScrollNo, Session session, D010004 d010004) {
		try {
			D010004 selectedBatch = d010004;
			if (selectedBatch == null) {
				Logger.error("No Active Batch Found.");
				isAborted = true;
				return;
			}
			logger.error("Selected Batch : " + selectedBatch.getId().getBatchCd());
			D009040 d40 = new D009040();
			D009040Id id40 = new D009040Id();
			id40.setBatchCd(selectedBatch.getId().getBatchCd());
			id40.setEntryDate(selectedBatch.getId().getEntryDate());
			id40.setLbrCode(brCode);
			id40.setSetNo(setNo);
			id40.setScrollNo(scrollNo);
			d40.setId(id40);
			d40.setMainScrollNo(mainScrollNo); // Scroll No
			d40.setPostDate(selectedBatch.getPostDate());
			d40.setFeffDate(selectedBatch.getFeffDate());
			d40.setValueDate(selectedBatch.getPostDate());
			String bookType = getBookTypeNew(brCode, selectedBatch.getId().getBatchCd(), session);
			if (bookType == null) {
				Logger.error("BookType Not Found.");
				isAborted = true;
				return;
			}

			logger.error("BookType is : " + bookType);
			d40.setBookType(bookType);
			d40.setDrCr('D');
			d40.setVcrAcctId(acctId);
			d40.setMainAcctId(acctId);

			short moduleType = getModuleTypeNew(brCode, acctId.substring(0, 8).trim(), session);
			if (moduleType == 0) {
				Logger.error("ModuleType Not Found.");
				isAborted = true;
				return;
			}

			d40.setActivityType("DR");
			if (moduleType == 47)
				d40.setCashFlowType("DDSDR");
			else
				d40.setCashFlowType("DR");
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
			d40.setParticulars(narration.length() > 70 ? narration.substring(0, 70) : narration.trim()); // param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); //
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');

			int usrCode2 = Integer.parseInt(ConfigurationLoader.getParameters(false).getProperty("IMPS_USER"));
			if (usrCode2 == 0) {
				Logger.error("UsrCode Not Found.");
				isAborted = true;
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
			String batchFlag = ConfigurationLoader.getParameters(false).getProperty("UPDATE_BATCH_BAL") != null
					? ConfigurationLoader.getParameters(false).getProperty("UPDATE_BATCH_BAL")
					: "N";
			if (batchFlag.equalsIgnoreCase(MSGConstants.YES)) {
				if (updateBatchBalance(session, amount, selectedBatch, brCode, MSGConstants.DR))
					isAborted = false;
				else
					isAborted = true;
			}
		} catch (Exception e) {
			isAborted = true;
			e.printStackTrace();
		}
	}

	public void stopChequeDr(int brCode, String acctId, String tType, int setNo, int scrollNo, String narration,
			double amount, String rrn, int mainScrollNo, Session session) {
		try {
			Date openDate = getOpenDate(brCode); // Get Open Date
			if (openDate == null) {
				Logger.error("Open Date Not Found. Aborting Transaction");
				isAborted = true;
				return;
			}
			logger.error("Open Date is " + openDate);

			// Get BatchCodes from properties file.
			String batchCode = Props.getBatchProperty(tType);
			logger.error("Batch Code Form Properties File : " + batchCode);
			String batchCodes[] = batchCode.split("~");
			logger.error("Number of Batches is " + batchCodes.length + "\t Names are " + Arrays.toString(batchCodes));
			/*
			 * if(batchCodes == null || batchCodes.length <1){
			 * Logger.error("Batch Codes Not Found in Properties File."); isAborted = true;
			 * return; }
			 */
			/// Get Selected Batch.
			D010004 selectedBatch = getSelectedBatch(brCode, batchCodes, openDate);
			if (selectedBatch == null) {
				Logger.error("No Active Batch Found.");
				isAborted = true;
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
			d40.setMainScrollNo(mainScrollNo); // Scroll No
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
			d40.setParticulars(narration.length() > 70 ? narration.substring(0, 70) : narration.trim()); // param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); //
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');

			int usrCode2 = getUsrCode("WEB");
			if (usrCode2 == 0) {
				Logger.error("UsrCode Not Found.");
				isAborted = true;
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
			// balance(brCode, acctId, amount, "D");
			session.save(d40);
			session.flush();
		} catch (Exception e) {
			isAborted = true;
			e.printStackTrace();
		}

	}

	public void credit(int brCode, String acctId, String tType, int setNo, int scrollNo, String narration,
			double amount, String rrn, Session session) {
		try {

			String cashFlowType = "";
			if (tType.contains("~")) {
				String[] type = tType.split("~");
				cashFlowType = type[0];
				tType = type[1];
			}

			Date openDate = getOpenDateNew(brCode, session); // Get Open Date
			if (openDate == null) {
				Logger.error("Open Date Not Found. Aborting Transaction");
				isAborted = true;
				return;
			}
			logger.error("Open Date is " + openDate);
			// Get BatchCodes from properties file.
			System.out.println("tType:>>>" + tType);
			String batchCode = Props.getBatchProperty(tType.trim());
			logger.error("Batch Code Form Properties File : " + batchCode);
			String batchCodes[] = batchCode.split("~");
			logger.error("Number of Batches is " + batchCodes.length + "\t Names are " + Arrays.toString(batchCodes));
			if (batchCodes == null || batchCodes.length < 1) {
				Logger.error("Batch Codes Not Found in Properties File.");
				isAborted = true;
				return;
			}
			/// Get Selected Batch.
			D010004 selectedBatch = getSelectedBatchNew(brCode, batchCodes, openDate, session);
			if (selectedBatch == null) {
				Logger.error("No Active Batch Found.");
				isAborted = true;
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
			if ("RD".equalsIgnoreCase(cashFlowType)) {
				d40.setCashFlowType("TDPRCR");
				d40.setActivityType("TDINSTCR");
			} else if ("QR".equalsIgnoreCase(cashFlowType)) {
				d40.setActivityType("CR");
				d40.setCashFlowType("DDSCR");
			}else {
				d40.setActivityType("CR");
				d40.setCashFlowType("CR");
			}
			d40.setValueDate(selectedBatch.getPostDate()); // postdate SELECT *
															// FROM D010004
															// WHERE LBrCode =9
															// AND EntryDate =
															// '19-APR-2016' AND
															// BatchCd = 'ABBTR'

			String bookType = getBookTypeNew(brCode, selectedBatch.getId().getBatchCd(), session);
			if (bookType == null) {
				logger.error("BookType Not Found.");
				isAborted = true;
				return;
			}
			logger.error("BookType is : " + bookType);
			d40.setBookType(bookType); // SELECT BookType FROM D010001 WHERE
										// LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('C');
			d40.setVcrAcctId(acctId);
			d40.setMainAcctId(acctId);

			short moduleType = getModuleTypeNew(brCode, acctId.substring(0, 8).trim(), session);
			if (moduleType == 0) {
				Logger.error("ModuleType Not Found.");
				isAborted = true;
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
			d40.setParticulars(narration.length() > 70 ? narration.substring(0, 70) : narration.trim()); // param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); //
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');

			int usrCode2 = getUsrCode("WEB");
			if (usrCode2 == 0) {
				Logger.error("UsrCode Not Found.");
				isAborted = true;
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

			d40.setNoAuthPending((byte) 0);
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
			if (updateBatchBalance(session, amount, selectedBatch, brCode, MSGConstants.CR))
				isAborted = false;
			else
				isAborted = true;
		} catch (Exception e) {
			isAborted = true;
			e.printStackTrace();
		}

	}

	public void creditSameBranch(int brCode, String acctId, String tType, int setNo, int scrollNo, String narration,
			double amount, String rrn, int mainSrollNo, Session session) {
		try {

			String cashFlowType = "";
			if (tType.contains("~")) {
				String[] type = tType.split("~");
				cashFlowType = type[0];
				tType = type[1];
			}

			Date openDate = getOpenDateNew(brCode, session); // Get Open Date
			if (openDate == null) {
				Logger.error("Open Date Not Found. Aborting Transaction");
				isAborted = true;
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
				return;
			}

			/// Get Selected Batch.
			D010004 selectedBatch = getSelectedBatchNew(brCode, batchCodes, openDate, session);
			if (selectedBatch == null) {
				Logger.error("No Active Batch Found.");
				isAborted = true;
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
			d40.setMainScrollNo(mainSrollNo); // Scroll No
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
			if ("RD".equalsIgnoreCase(cashFlowType)) {
				d40.setCashFlowType("TDPRCR");
				d40.setActivityType("TDINSTCR");
			} else if ("QR".equalsIgnoreCase(cashFlowType)) {
				d40.setActivityType("CR");
				d40.setCashFlowType("DDSCR");
			}else {
				d40.setActivityType("CR");
				d40.setCashFlowType("CR");
			}
			d40.setValueDate(selectedBatch.getPostDate()); // postdate SELECT *
															// FROM D010004
															// WHERE LBrCode =9
															// AND EntryDate =
															// '19-APR-2016' AND
															// BatchCd = 'ABBTR'

			String bookType = getBookTypeNew(brCode, selectedBatch.getId().getBatchCd(), session);
			if (bookType == null) {
				Logger.error("BookType Not Found.");
				isAborted = true;
				return;
			}
			logger.error("BookType is : " + bookType);
			d40.setBookType(bookType); // SELECT BookType FROM D010001 WHERE
										// LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('C');
			d40.setVcrAcctId(acctId);
			d40.setMainAcctId(acctId);

			short moduleType = getModuleTypeNew(brCode, acctId.substring(0, 8).trim(), session);
			if (moduleType == 0) {
				Logger.error("ModuleType Not Found.");
				isAborted = true;
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
			d40.setParticulars(narration.length() > 70 ? narration.substring(0, 70) : narration.trim()); // param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); //
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');

			int usrCode2 = Integer.parseInt(ConfigurationLoader.getParameters(false).getProperty("IMPS_USER"));
			if (usrCode2 == 0) {
				Logger.error("UsrCode Not Found.");
				isAborted = true;
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
			if (updateBatchBalance(session, amount, selectedBatch, brCode, MSGConstants.CR))
				isAborted = false;
			else
				isAborted = true;
			/*
			 * session.flush(); Query query = session.
			 * createQuery("UPDATE D010004 SET TotalCrVcrs=TotalCrVcrs+1, TotalCrAmtLcy = TotalCrAmtLcy +:amt WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd"
			 * ); query.setParameter("amt", amount); query.setParameter("lbrcode", brCode);
			 * query.setParameter("entrydate", selectedBatch.getId().getEntryDate());
			 * query.setParameter("batchcd", selectedBatch.getId().getBatchCd());
			 * query.executeUpdate(); session.flush();
			 */
		} catch (Exception e) {
			isAborted = true;
			e.printStackTrace();
		}
	}

	public void creditSameBranchDDS(int brCode, String acctId, String tType, int setNo, int scrollNo, String narration,
			double amount, String rrn, int mainSrollNo, Session session, D010004 d010004) {
		try {
			D010004 selectedBatch = d010004;
			if (selectedBatch == null) {
				Logger.error("No Active Batch Found.");
				isAborted = true;
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
			d40.setMainScrollNo(mainSrollNo); // Scroll No
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
			// d40.setActivityType("CR");
			// d40.setCashFlowType("DDSCR");
			d40.setValueDate(selectedBatch.getPostDate()); // postdate SELECT *
															// FROM D010004
															// WHERE LBrCode =9
															// AND EntryDate =
															// '19-APR-2016' AND
															// BatchCd = 'ABBTR'

			String bookType = getBookTypeNew(brCode, selectedBatch.getId().getBatchCd(), session);
			if (bookType == null) {
				Logger.error("BookType Not Found.");
				isAborted = true;
				return;
			}
			logger.error("BookType is : " + bookType);
			d40.setBookType(bookType); // SELECT BookType FROM D010001 WHERE
										// LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('C');
			d40.setVcrAcctId(acctId);
			d40.setMainAcctId(acctId);

			short moduleType = getModuleTypeNew(brCode, acctId.substring(0, 8).trim(), session);
			if (moduleType == 0) {
				Logger.error("ModuleType Not Found.");
				isAborted = true;
				return;
			}
			d40.setActivityType("CR");
			if (moduleType == 47)
				d40.setCashFlowType("DDSCR");
			else
				d40.setCashFlowType("CR");
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
			d40.setParticulars(narration.length() > 70 ? narration.substring(0, 70) : narration.trim()); // param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); //
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');

			int usrCode2 = Integer.parseInt(ConfigurationLoader.getParameters(false).getProperty("IMPS_USER"));
			if (usrCode2 == 0) {
				Logger.error("UsrCode Not Found.");
				isAborted = true;
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
			String batchFlag = ConfigurationLoader.getParameters(false).getProperty("UPDATE_BATCH_BAL") != null
					? ConfigurationLoader.getParameters(false).getProperty("UPDATE_BATCH_BAL")
					: "N";
			if (batchFlag.equalsIgnoreCase(MSGConstants.YES)) {
				if (updateBatchBalance(session, amount, selectedBatch, brCode, MSGConstants.CR))
					isAborted = false;
				else
					isAborted = true;
			}
		} catch (Exception e) {
			isAborted = true;
			e.printStackTrace();
		}
	}

	public void creditABB(int brCode, String acctId, String tType, int setNo, int scrollNo, String narration,
			int tbrCode, double amount, String rrn, int reconNo, Session session) {
		try {
			Date openDate = getOpenDateNew(brCode, session); // Get Open Date
			if (openDate == null) {
				Logger.error("Open Date Not Found. Aborting Transaction");
				isAborted = true;
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
				return;
			}

			/// Get Selected Batch.
			D010004 selectedBatch = getSelectedBatchNew(brCode, batchCodes, openDate, session);
			if (selectedBatch == null) {
				Logger.error("No Active Batch Found.");
				isAborted = true;
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

			String bookType = getBookTypeNew(brCode, selectedBatch.getId().getBatchCd(), session);
			if (bookType == null) {
				Logger.error("BookType Not Found.");
				isAborted = true;
				return;
			}
			logger.error("BookType is : " + bookType);
			d40.setBookType(bookType); // SELECT BookType FROM D010001 WHERE
										// LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('C');
			d40.setVcrAcctId(acctId);
			d40.setMainAcctId(acctId);

			short moduleType = getModuleTypeNew(brCode, acctId.substring(0, 8).trim(), session);

			if (moduleType == 0) {
				Logger.error("ModuleType Not Found.");
				moduleType = 100;
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
			d40.setParticulars("ReconNo=" + reconNo + " ToBrCode=" + tbrCode); // param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); //
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');
			d40.setTokenNo(reconNo);

			int usrCode2 = Integer.parseInt(ConfigurationLoader.getParameters(false).getProperty("IMPS_USER"));
			if (usrCode2 == 0) {
				Logger.error("UsrCode Not Found.");
				isAborted = true;
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

			// updateBatchIntraBr(amount,brCode
			// ,selectedBatch.getId().getBatchCd() ,openDate );
			session.save(d40);
			System.out.println("<<<<<----------Updating CreditABB vouchers--------------->>>>>>" + d40.toString());
			if (updateBatchBalance(session, amount, selectedBatch, brCode, MSGConstants.CR))
				isAborted = false;
			else
				isAborted = true;
			System.out.println("isAborted::>>" + isAborted);
		} catch (Exception e) {
			isAborted = true;
			logger.error("ABORTED::>>" + e);
			e.printStackTrace();
		}
	}

	public void debitABB(int brCode, String acctId, String tType, int setNo, int scrollNo, String narration,
			int benBrcode, double amount, String rrn, int reconNo, Session session) {
		try {
			Date openDate = getOpenDateNew(brCode, session); // Get Open Date
			if (openDate == null) {
				Logger.error("Open Date Not Found. Aborting Transaction");
				isAborted = true;
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
				return;
			}

			/// Get Selected Batch.
			D010004 selectedBatch = getSelectedBatchNew(brCode, batchCodes, openDate, session);
			if (selectedBatch == null) {
				Logger.error("No Active Batch Found.");
				isAborted = true;
				return;
			}

			logger.error("Selected Batch : " + selectedBatch.getId().getBatchCd());
			secondBatch = selectedBatch.getId().getBatchCd();
			D009040 d40 = new D009040();
			D009040Id id40 = new D009040Id();
			id40.setBatchCd(selectedBatch.getId().getBatchCd());

			id40.setEntryDate(selectedBatch.getId().getEntryDate());
			id40.setLbrCode(brCode);
			id40.setSetNo(setNo);

			id40.setScrollNo(scrollNo);

			d40.setId(id40);
			d40.setMainScrollNo(scrollNo); // Scroll No
			d40.setPostDate(selectedBatch.getPostDate());
			d40.setFeffDate(selectedBatch.getFeffDate());
			d40.setActivityType("ABBREM");
			d40.setCashFlowType("ABBDR");
			d40.setValueDate(selectedBatch.getPostDate());

			String bookType = getBookTypeNew(brCode, selectedBatch.getId().getBatchCd(), session);
			if (bookType == null) {
				Logger.error("BookType Not Found.");
				isAborted = true;
				return;
			}
			logger.error("BookType is : " + bookType);
			d40.setBookType(bookType);
			d40.setDrCr('D');
			d40.setVcrAcctId(acctId);
			d40.setMainAcctId(acctId);

			short moduleType = getModuleTypeNew(brCode, acctId.substring(0, 8).trim(), session);
			if (moduleType == 0) {
				Logger.error("ModuleType Not Found.");
				isAborted = true;
				return;
			}
			logger.error("ModuleType is : " + moduleType);
			d40.setMainModType(moduleType);

			d40.setVcrModType(moduleType);

			d40.setTrnCurCd("INR");
			d40.setFcyTrnAmt(amount);
			d40.setLcyConvRate(1);
			d40.setLcyTrnAmt(amount);
			d40.setInstrBankCd((short) benBrcode);
			d40.setInstrBranchCd((short) brCode);
			d40.setInstrType((short) 99); // Depend on chanel
			d40.setInstrNo(rrn); // RRN
			d40.setInstrDate(new Date()); // Blank
			d40.setParticulars("ReconNo=" + reconNo + " ToBrCode=" + brCode); // param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); //
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');
			d40.setTokenNo(reconNo);
			int usrCode2 = Integer.parseInt(ConfigurationLoader.getParameters(false).getProperty("IMPS_USER"));
			if (usrCode2 == 0) {
				Logger.error("UsrCode Not Found.");
				isAborted = true;
				return;
			}
			logger.error("UsrCode is : " + usrCode2);
			d40.setMaker(usrCode2);

			d40.setMakerDate(selectedBatch.getId().getEntryDate()); // EntryDate
			d40.setMakerTime(Integer.parseInt(timeFormat.format(new Date()).substring(0, 8)));
			d40.setChecker1(usrCode2);
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
			d40.setPostTime(Integer.parseInt(timeFormat.format(new Date()).substring(0, 8))); // Time
																								// HHMMSSTT
			session.save(d40);
			System.out.println("Debit ABB=" + d40.toString());
			if (updateBatchBalance(session, amount, selectedBatch, brCode, MSGConstants.DR))
				isAborted = false;
			else
				isAborted = true;
		} catch (Exception e) {
			isAborted = true;
			e.printStackTrace();
		}

	}

	private void updateBatchIntraBr(double amount, int lbrCode, String batch, Date entryDate) {
		System.out.println("<<<<<<< :::  VoucherCommon.updateBatchIntraBr() ::>>>>>>>>>");
		System.out.println("amount::>>" + amount);
		System.out.println("lbrCode::>>" + lbrCode);
		System.out.println("batch:::>>" + batch);
		System.out.println("entryDate::>>" + entryDate);
		try {
			Session session = HBUtil.getSessionFactory().openSession();
			Transaction t = session.beginTransaction();
			Query query = session.createQuery(
					"UPDATE D010004 SET TotalDrVcrs=TotalDrVcrs+1, TotalDrAmtLcy = TotalDrAmtLcy +:amt , TotalCrVcrs = TotalCrVcrs+1, TotalCrAmtLcy= TotalCrAmtLcy + :amt  WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd");
			query.setParameter("amt", amount);
			query.setParameter("lbrcode", lbrCode);
			query.setParameter("entrydate", entryDate);
			query.setParameter("batchcd", batch);
			query.executeUpdate();
			t.commit();
			session.close();
			session = null;

		} catch (Exception e) {
			e.printStackTrace();
			isAborted = true;
		}
	}

	@SuppressWarnings("deprecation")
	/*
	 * private int getNextReconNo(int brCode) { int nextReconNo = -1; try(Session
	 * sessionLocal = HBUtil.getSessionFactory().openSession()) { D001005 d05 =
	 * (D001005) sessionLocal.createCriteria(D001005.class).add(Restrictions.eq(
	 * "id.catType", "ABBRECON")).add(Restrictions.eq("id.lbrCode",
	 * brCode)).setMaxResults(1).uniqueResult(); sessionLocal.close(); if(d05 ==
	 * null){ isAborted = true; return nextReconNo; } else{ int lastNo =
	 * d05.getLastNo(); logger.error("Original LastNo : "+lastNo ); lastNo = lastNo
	 * +1; logger.error("Incremented LastNo : "+lastNo ); d05.setLastNo(lastNo);
	 * 
	 * Session session=HBUtil.getSessionFactory().openSession(); Transaction
	 * t=session.beginTransaction();
	 * session.createQuery("update D001005 set LastNo = "
	 * +lastNo+" where LBrCode = "+brCode+" and CatType = 'ABBRECON'").
	 * executeUpdate();
	 * 
	 * //sessionLocal.update(d05); nextReconNo = d05.getLastNo(); t.commit();
	 * session.close(); session=null; t=null; } } catch (Exception e) {
	 * e.printStackTrace(); return nextReconNo; } return nextReconNo; }
	 */
	/*
	 * public int getNextReconNo(int brCode){ RECONSEQ setSeq = new RECONSEQ();
	 * try(Session session = HBUtil.getSessionFactory().openSession()){
	 * session.save(setSeq); session.close(); }catch(Exception e) {
	 * e.printStackTrace(); } return setSeq.getId(); }
	 */
	public static int getNextReconNo(int brCode) {

		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		try {

			String hql = "select lastNo from D001005 where id.lbrCode = :lbrCode and id.catType='ABBRECON'";
			List<Long> seqNo = (List<Long>) session.createQuery(hql).setParameter("lbrCode", brCode).getResultList();
			logger.info(seqNo.get(0));
			if (seqNo != null && seqNo.size() > 0) {

				Long count = Long.parseLong(seqNo.get(0) + "") + 1;
				while (true) {
					D100001 reconRecord = reconNoCheck(count, brCode, session);
					if (reconRecord == null) {
						t.commit();
						return count.intValue();
					} else
						count = count + 1;
				}
			} else
				return 1;

			// session.update(d001004);

			// return null;
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

	public boolean balance(int remBrCode, String remPrdAcctId, double amount, String drcr) {
		logger.error("Transaction Amount : " + amount);
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		D009022Id remId = new D009022Id();
		remId.setLbrCode(remBrCode);
		remId.setPrdAcctId(remPrdAcctId);
		D009022 remAcct = session.get(D009022.class, remId);
		try {
			if (drcr.equalsIgnoreCase("D")) {
				logger.error("Rem Amount Before : " + remAcct.getActClrBalFcy());
				if ((remAcct.getActClrBalFcy() - remAcct.getTotalLienFcy()) > amount) {
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
					return true;
				} else {
					logger.error("Insufficient Funds ");
					isAborted = true;
					session.close();
					session = null;
					t = null;
					return false;
				}
			} else if (drcr.equalsIgnoreCase("C")) {
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
				isAborted = true;
				return true;
			} else {
				session.close();
				t = null;
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			isAborted = true;
			return false;
		}
	}

	public static HashMap<String, String> otherBankVoucherEntry(int lbrCode, String accNo, String transType,
			String narration, double amount, String rrn) {
		VoucherCommon common = new VoucherCommon();
		HashMap<String, String> resultMap = new HashMap<>();
		int setNo = VoucherCommon.getNextSetNo();
		int scrollNo = VoucherCommon.getNextScrollNo();
		int reconNo = VoucherCommon.getNextReconNo(lbrCode);
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();// amar Charges
		boolean flag = true;
		if (ConfigurationLoader.getParameters(false).getProperty("IMPS_CHG_FLAG").trim()
				.equalsIgnoreCase(MSGConstants.YES)
				&& Double.valueOf(ConfigurationLoader.getParameters(flag).getProperty("CHRG_AMOUNT_LIMIT")) < amount) {
			double chgAmt = Double
					.valueOf(ConfigurationLoader.getParameters(false).getProperty("IMPS_CHG_AMOUNT").trim());
			common.debit(lbrCode, accNo, transType.toUpperCase(), setNo, scrollNo, "IMPS Charges/" + chgAmt, chgAmt,
					rrn, session);
			if (!common.isAborted) {
				if (VoucherMPOS.updateProductBalances(chgAmt, lbrCode, "D", accNo.substring(0, 8).trim(), session)
						.equalsIgnoreCase(MSGConstants.SUCCESS)) {
					if (!common.isAborted) {
						if (VoucherMPOS.updateProductBalances(chgAmt, lbrCode, "C",
								ConfigurationLoader.getParameters(false).getProperty("IMPS_CHG_PRODUCT"), session)
								.equalsIgnoreCase(MSGConstants.SUCCESS)) {
							String bal = CoreTransactionMPOS.balance(lbrCode, accNo, chgAmt, "D", session);
							if (bal != null && bal.trim().length() > 0 && !bal.equalsIgnoreCase("51")
									&& !bal.equalsIgnoreCase("99")) {
								common.debit(lbrCode, accNo, transType.toUpperCase(), setNo,
										VoucherCommon.getNextScrollNo(), narration, amount, rrn, session);
								if (!common.isAborted) {
									if (VoucherMPOS.updateProductBalances(amount, lbrCode, "D",
											accNo.substring(0, 8).trim(), session)
											.equalsIgnoreCase(MSGConstants.SUCCESS)) {
										if (!common.isAborted) {
											logger.error("Transaction successful");
											int setNoABB = VoucherCommon.getNextSetNo();
											int scrollNoAbb = VoucherCommon.getNextScrollNo();
											int scrollNoAbb1 = VoucherCommon.getNextScrollNo();
											int scrollNoAbb2 = VoucherCommon.getNextScrollNo();
											common.creditABB(lbrCode, MSGConstants.ABB_ACC, transType.toUpperCase(),
													setNo, scrollNoAbb, narration, lbrCode, amount, rrn, reconNo,
													session);
											if (!common.isAborted) {
												if (VoucherMPOS
														.updateProductBalances(amount, lbrCode, "C", MSGConstants.ABB,
																session)
														.trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
													if (!common.isAborted) {
														logger.error("ABB Transaction successful");
														String crAccno = get32DigitAcctNo(
																getSysParameter(MSGConstants.MBRNCRACT).trim(), 0, 0);
														// + " 000000000000000000000000";
														int crBrCode = Integer
																.valueOf(getSysParameter(MSGConstants.MBBRANCH).trim());
														common.credit(crBrCode, crAccno, MSGConstants.ABB, setNoABB,
																scrollNoAbb1, narration, amount, rrn, session);
														if (!common.isAborted) {
															VoucherMPOS.updateProductBalances(amount, crBrCode, "C",
																	crAccno.substring(0, 8).trim(), session);
															if (!common.isAborted) {
																logger.error("Other Bank GL Transaction successful");
																common.debitABB(crBrCode, MSGConstants.ABB_ACC,
																		MSGConstants.ABB, setNoABB, scrollNoAbb2,
																		narration, 2, amount, rrn, reconNo, session);
																if (!common.isAborted) {
																	if (VoucherMPOS
																			.updateProductBalances(amount, crBrCode,
																					"D",
																					MSGConstants.ABB_ACC.substring(0, 8)
																							.trim(),
																					session)
																			.trim()
																			.equalsIgnoreCase(MSGConstants.SUCCESS)) {
																		Date opdate = DataUtils.getOpenDate(lbrCode);
																		String batchCodes[] = Props
																				.getBatchProperty("IMPS").split("~");
																		D001004 d04OnlineBatchName = VoucherMPOS
																				.getBatchNameFromBatchCode(
																						batchCodes[0]);
																		String onlineBatchName = d04OnlineBatchName
																				.getValue().trim();
																		String batchCodes1[] = Props
																				.getBatchProperty("ABB").split("~");
																		D001004 d001004 = VoucherMPOS
																				.getBatchNameFromBatchCode(
																						batchCodes1[0]);
																		String benBatchCode = d001004.getValue().trim();

																		D100001 d100001 = VoucherMPOS.prepareReconObj(
																				lbrCode, reconNo, opdate, 999999,
																				onlineBatchName, benBatchCode, setNo,
																				scrollNoAbb, setNoABB, scrollNoAbb1,
																				scrollNoAbb2, crBrCode, "" + crAccno,
																				Double.valueOf(amount), "D");
																		System.out.println("d100001::>>>" + d100001);

																		D100002 d100002 = VoucherMPOS.prepareRecon2Obj(
																				lbrCode, reconNo, opdate, 999999,
																				onlineBatchName, benBatchCode, setNo,
																				scrollNoAbb, setNoABB, scrollNoAbb1,
																				scrollNoAbb2, crBrCode, "" + crAccno,
																				Double.valueOf(amount), "D");//
																		System.out.println("d100002::>>>" + d100002);
																		CoreTransactionMPOS.balance(lbrCode, accNo,
																				amount, "D", session);
																		try {
																			session.save(d100001);
																			session.save(d100002);
																			session.flush();
																			t.commit();
																			session.close();
																			session = null;
																			t = null;
																			resultMap.put(Code.RESULT, Code.SUCCESS);
																			resultMap.put(Code.SETNO,
																					String.valueOf(setNo));
																			resultMap.put(Code.SCROLLNO,
																					String.valueOf(scrollNo));
																			t = null;
																			return resultMap;
																		} catch (Exception e) {
																			// TODO:
																			// handle
																			// exception
																			e.printStackTrace();
																			try {
																				t.rollback();
																			} catch (Exception e2) {
																				// TODO:
																				// handle
																				// exception
																				e2.printStackTrace();
																			}
																			resultMap.put(Code.RESULT, Code.ERROR);
																			resultMap.put(Code.SETNO, "");
																			resultMap.put(Code.SCROLLNO, "");
																			session.close();
																			session = null;
																			t = null;
																			return resultMap;
																		}
																	} else {
																		logger.error("ABB Transaction unsuccessful");
																		resultMap.put(Code.RESULT, Code.ERROR);
																		resultMap.put(Code.SETNO, "");
																		resultMap.put(Code.SCROLLNO, "");
																		session.close();
																		session = null;
																		t = null;
																		return resultMap;
																	}
																} else {
																	logger.error("ABB Transaction unsuccessful");
																	resultMap.put(Code.RESULT, Code.ERROR);
																	resultMap.put(Code.SETNO, "");
																	resultMap.put(Code.SCROLLNO, "");
																	session.close();
																	session = null;
																	t = null;
																	return resultMap;
																}
															} else {
																logger.error("ABB Transaction unsuccessful");
																resultMap.put(Code.RESULT, Code.ERROR);
																resultMap.put(Code.SETNO, "");
																resultMap.put(Code.SCROLLNO, "");
																session.close();
																session = null;
																t = null;
																return resultMap;
															}
														} else {
															logger.error("ABB Transaction unsuccessful");
															resultMap.put(Code.RESULT, Code.ERROR);
															resultMap.put(Code.SETNO, "");
															resultMap.put(Code.SCROLLNO, "");
															session.close();
															session = null;
															t = null;
															return resultMap;
														}
													} else {
														logger.error("ABB Transaction unsuccessful");
														resultMap.put(Code.RESULT, Code.ERROR);
														resultMap.put(Code.SETNO, "");
														resultMap.put(Code.SCROLLNO, "");
														session.close();
														session = null;
														t = null;
														return resultMap;
													}
												} else {
													logger.error("ABB Transaction unsuccessful");
													resultMap.put(Code.RESULT, Code.ERROR);
													resultMap.put(Code.SETNO, "");
													resultMap.put(Code.SCROLLNO, "");
													session.close();
													session = null;
													t = null;
													return resultMap;
												}
											} else {
												logger.error("ABB Transaction unsuccessful");
												resultMap.put(Code.RESULT, Code.ERROR);
												resultMap.put(Code.SETNO, "");
												resultMap.put(Code.SCROLLNO, "");
												session.close();
												session = null;
												t = null;
												return resultMap;
											}
										} else {
											logger.error("Transaction unsuccessful");
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "");
											resultMap.put(Code.SCROLLNO, "");
											session.close();
											session = null;
											t = null;
											return resultMap;
										}
									} else {
										logger.error("ABB Transaction unsuccessful");
										resultMap.put(Code.RESULT, Code.ERROR);
										resultMap.put(Code.SETNO, "");
										resultMap.put(Code.SCROLLNO, "");
										session.close();
										session = null;
										t = null;
										return resultMap;
									}
								} else {
									logger.error("Transaction unsuccessful");
									resultMap.put(Code.RESULT, Code.ERROR);
									resultMap.put(Code.SETNO, "");
									resultMap.put(Code.SCROLLNO, "");
									session.close();
									session = null;
									t = null;
									return resultMap;
								}
							} else {
								flag = false;
								logger.error("Transaction unsuccessful");
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "");
								resultMap.put(Code.SCROLLNO, "");
								session.close();
								session = null;
								t = null;
								return resultMap;
							}
						} else {
							flag = false;
							logger.error("Transaction unsuccessful");
							resultMap.put(Code.RESULT, Code.ERROR);
							resultMap.put(Code.SETNO, "");
							resultMap.put(Code.SCROLLNO, "");
							session.close();
							session = null;
							t = null;
							return resultMap;
						}
					} else {
						flag = false;
						logger.error("Transaction unsuccessful");
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "");
						resultMap.put(Code.SCROLLNO, "");
						session.close();
						session = null;
						t = null;
						return resultMap;
					}
				} else {
					flag = false;
					logger.error("Transaction unsuccessful");
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "");
					resultMap.put(Code.SCROLLNO, "");
					session.close();
					session = null;
					t = null;
					return resultMap;
				}
			} else {
				logger.error("Transaction unsuccessful");
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "");
				resultMap.put(Code.SCROLLNO, "");
				session.close();
				session = null;
				t = null;
				return resultMap;
			}
		} else {
			common.debit(lbrCode, accNo, transType.toUpperCase(), setNo, scrollNo, narration, amount, rrn, session);
			if (!common.isAborted) {
				if (VoucherMPOS.updateProductBalances(amount, lbrCode, "D", accNo.substring(0, 8).trim(), session)
						.equalsIgnoreCase(MSGConstants.SUCCESS)) {
					if (!common.isAborted) {
						logger.error("Transaction successful");
						int setNoABB = VoucherCommon.getNextSetNo();
						int scrollNoAbb = VoucherCommon.getNextScrollNo();
						int scrollNoAbb1 = VoucherCommon.getNextScrollNo();
						int scrollNoAbb2 = VoucherCommon.getNextScrollNo();
						common.creditABB(lbrCode, MSGConstants.ABB_ACC, transType.toUpperCase(), setNo, scrollNoAbb,
								narration, lbrCode, amount, rrn, reconNo, session);
						if (!common.isAborted) {
							if (VoucherMPOS.updateProductBalances(amount, lbrCode, "C", MSGConstants.ABB, session)
									.trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
								if (!common.isAborted) {
									logger.error("ABB Transaction successful");
									String crAccno = get32DigitAcctNo(getSysParameter(MSGConstants.MBRNCRACT).trim(), 0,
											0);
									// + " 000000000000000000000000";
									int crBrCode = Integer.valueOf(getSysParameter(MSGConstants.MBBRANCH).trim());
									common.credit(crBrCode, crAccno, MSGConstants.ABB, setNoABB, scrollNoAbb1,
											narration, amount, rrn, session);
									if (!common.isAborted) {
										VoucherMPOS.updateProductBalances(amount, crBrCode, "C",
												crAccno.substring(0, 8).trim(), session);
										if (!common.isAborted) {
											logger.error("Other Bank GL Transaction successful");
											common.debitABB(crBrCode, MSGConstants.ABB_ACC, MSGConstants.ABB, setNoABB,
													scrollNoAbb2, narration, 2, amount, rrn, reconNo, session);
											if (!common.isAborted) {
												if (VoucherMPOS
														.updateProductBalances(amount, crBrCode, "D",
																MSGConstants.ABB_ACC.substring(0, 8).trim(), session)
														.trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
													Date opdate = DataUtils.getOpenDate(lbrCode);
													String batchCodes[] = Props.getBatchProperty("IMPS").split("~");
													D001004 d04OnlineBatchName = VoucherMPOS
															.getBatchNameFromBatchCode(batchCodes[0]);
													String onlineBatchName = d04OnlineBatchName.getValue().trim();
													String batchCodes1[] = Props.getBatchProperty("ABB").split("~");
													D001004 d001004 = VoucherMPOS
															.getBatchNameFromBatchCode(batchCodes1[0]);
													String benBatchCode = d001004.getValue().trim();

													D100001 d100001 = VoucherMPOS.prepareReconObj(lbrCode, reconNo,
															opdate, 999999, onlineBatchName, benBatchCode, setNo,
															scrollNoAbb, setNoABB, scrollNoAbb1, scrollNoAbb2, crBrCode,
															"" + crAccno, Double.valueOf(amount), "D");
													System.out.println("d100001::>>>" + d100001);

													D100002 d100002 = VoucherMPOS.prepareRecon2Obj(lbrCode, reconNo,
															opdate, 999999, onlineBatchName, benBatchCode, setNo,
															scrollNoAbb, setNoABB, scrollNoAbb1, scrollNoAbb2, crBrCode,
															"" + crAccno, Double.valueOf(amount), "D");//
													System.out.println("d100002::>>>" + d100002);
													CoreTransactionMPOS.balance(lbrCode, accNo, amount, "D", session);
													try {
														session.save(d100001);
														session.save(d100002);
														session.flush();
														t.commit();
														session.close();
														session = null;
														t = null;
														resultMap.put(Code.RESULT, Code.SUCCESS);
														resultMap.put(Code.SETNO, String.valueOf(setNo));
														resultMap.put(Code.SCROLLNO, String.valueOf(scrollNo));
														t = null;
														return resultMap;
													} catch (Exception e) {
														// TODO: handle
														// exception
														e.printStackTrace();
														try {
															t.rollback();
														} catch (Exception e2) {
															// TODO: handle
															// exception
															e2.printStackTrace();
														}
														resultMap.put(Code.RESULT, Code.ERROR);
														resultMap.put(Code.SETNO, "");
														resultMap.put(Code.SCROLLNO, "");
														session.close();
														session = null;
														t = null;
														return resultMap;
													}
												} else {
													logger.error("ABB Transaction unsuccessful");
													resultMap.put(Code.RESULT, Code.ERROR);
													resultMap.put(Code.SETNO, "");
													resultMap.put(Code.SCROLLNO, "");
													session.close();
													session = null;
													t = null;
													return resultMap;
												}
											} else {
												logger.error("ABB Transaction unsuccessful");
												resultMap.put(Code.RESULT, Code.ERROR);
												resultMap.put(Code.SETNO, "");
												resultMap.put(Code.SCROLLNO, "");
												session.close();
												session = null;
												t = null;
												return resultMap;
											}
										} else {
											logger.error("ABB Transaction unsuccessful");
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "");
											resultMap.put(Code.SCROLLNO, "");
											session.close();
											session = null;
											t = null;
											return resultMap;
										}
									} else {
										logger.error("ABB Transaction unsuccessful");
										resultMap.put(Code.RESULT, Code.ERROR);
										resultMap.put(Code.SETNO, "");
										resultMap.put(Code.SCROLLNO, "");
										session.close();
										session = null;
										t = null;
										return resultMap;
									}
								} else {
									logger.error("ABB Transaction unsuccessful");
									resultMap.put(Code.RESULT, Code.ERROR);
									resultMap.put(Code.SETNO, "");
									resultMap.put(Code.SCROLLNO, "");
									session.close();
									session = null;
									t = null;
									return resultMap;
								}
							} else {
								logger.error("ABB Transaction unsuccessful");
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "");
								resultMap.put(Code.SCROLLNO, "");
								session.close();
								session = null;
								t = null;
								return resultMap;
							}
						} else {
							logger.error("ABB Transaction unsuccessful");
							resultMap.put(Code.RESULT, Code.ERROR);
							resultMap.put(Code.SETNO, "");
							resultMap.put(Code.SCROLLNO, "");
							session.close();
							session = null;
							t = null;
							return resultMap;
						}
					} else {
						logger.error("Transaction unsuccessful");
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "");
						resultMap.put(Code.SCROLLNO, "");
						session.close();
						session = null;
						t = null;
						return resultMap;
					}
				} else {
					logger.error("ABB Transaction unsuccessful");
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "");
					resultMap.put(Code.SCROLLNO, "");
					session.close();
					session = null;
					t = null;
					return resultMap;
				}
			} else {
				logger.error("Transaction unsuccessful");
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "");
				resultMap.put(Code.SCROLLNO, "");
				session.close();
				session = null;
				t = null;
				return resultMap;
			}
		}
	}

	public static HashMap<String, String> otherBankVoucherEntry(int lbrCode, String accNo, String transType,
			String narration, double amount, String rrn, Session session) {
		VoucherCommon common = new VoucherCommon();
		HashMap<String, String> resultMap = new HashMap<>();
		int setNo = VoucherCommon.getNextSetNo();
		int scrollNo = VoucherCommon.getNextScrollNo();
		int reconNo = getNextReconNo(lbrCode);
		boolean flag = true;
		if (ConfigurationLoader.getParameters(false).getProperty("IMPS_CHG_FLAG").trim()
				.equalsIgnoreCase(MSGConstants.YES)
				&& Double.valueOf(ConfigurationLoader.getParameters(flag).getProperty("CHRG_AMOUNT_LIMIT")) < amount) {
			double chgAmt = Double
					.valueOf(ConfigurationLoader.getParameters(false).getProperty("IMPS_CHG_AMOUNT").trim());
			common.debit(lbrCode, accNo, transType.toUpperCase(), setNo, scrollNo, "IMPS Charges/" + chgAmt, chgAmt,
					rrn, session);
			if (!common.isAborted) {
				if (VoucherMPOS.updateProductBalances(chgAmt, lbrCode, "D", accNo.substring(0, 8).trim(), session)
						.equalsIgnoreCase(MSGConstants.SUCCESS)) {
					if (!common.isAborted) {
						if (VoucherMPOS.updateProductBalances(chgAmt, lbrCode, "C",
								ConfigurationLoader.getParameters(false).getProperty("IMPS_CHG_PRODUCT"), session)
								.equalsIgnoreCase(MSGConstants.SUCCESS)) {
							String bal = CoreTransactionMPOS.balanceOld(lbrCode, accNo, chgAmt, "D", session);
							if (bal != null && bal.trim().length() > 0 && !bal.equalsIgnoreCase("51")
									&& !bal.equalsIgnoreCase("99")) {
								common.debit(lbrCode, accNo, transType.toUpperCase(), setNo,
										VoucherCommon.getNextScrollNo(), narration, amount, rrn, session);
								if (!common.isAborted) {
									if (VoucherMPOS.updateProductBalances(amount, lbrCode, "D",
											accNo.substring(0, 8).trim(), session)
											.equalsIgnoreCase(MSGConstants.SUCCESS)) {
										if (!common.isAborted) {
											logger.error("Transaction successful");
											int setNoABB = VoucherCommon.getNextSetNo();
											int scrollNoAbb = VoucherCommon.getNextScrollNo();
											int scrollNoAbb1 = VoucherCommon.getNextScrollNo();
											int scrollNoAbb2 = VoucherCommon.getNextScrollNo();
											common.creditABB(lbrCode, MSGConstants.ABB_ACC, transType.toUpperCase(),
													setNo, scrollNoAbb, narration, lbrCode, amount, rrn, reconNo,
													session);
											if (!common.isAborted) {
												if (VoucherMPOS
														.updateProductBalances(amount, lbrCode, "C", MSGConstants.ABB,
																session)
														.trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
													if (!common.isAborted) {
														logger.error("ABB Transaction successful");
														String crAccno = get32DigitAcctNo(
																getSysParameter(MSGConstants.MBRNCRACT).trim(), 0, 0);
														// + " 000000000000000000000000";
														int crBrCode = Integer
																.valueOf(getSysParameter(MSGConstants.MBBRANCH).trim());
														common.credit(crBrCode, crAccno, MSGConstants.ABB, setNoABB,
																scrollNoAbb1, narration, amount, rrn, session);
														if (!common.isAborted) {
															VoucherMPOS.updateProductBalances(amount, crBrCode, "C",
																	crAccno.substring(0, 8).trim(), session);
															if (!common.isAborted) {
																logger.error("Other Bank GL Transaction successful");
																common.debitABB(crBrCode, MSGConstants.ABB_ACC,
																		MSGConstants.ABB, setNoABB, scrollNoAbb2,
																		narration, 2, amount, rrn, reconNo, session);
																if (!common.isAborted) {
																	if (VoucherMPOS
																			.updateProductBalances(amount, crBrCode,
																					"D",
																					MSGConstants.ABB_ACC.substring(0, 8)
																							.trim(),
																					session)
																			.trim()
																			.equalsIgnoreCase(MSGConstants.SUCCESS)) {
																		Date opdate = DataUtils.getOpenDate(lbrCode);
																		String batchCodes[] = Props
																				.getBatchProperty("IMPS").split("~");
																		D001004 d04OnlineBatchName = VoucherMPOS
																				.getBatchNameFromBatchCode(
																						batchCodes[0]);
																		String onlineBatchName = d04OnlineBatchName
																				.getValue().trim();
																		String batchCodes1[] = Props
																				.getBatchProperty("ABB").split("~");
																		D001004 d001004 = VoucherMPOS
																				.getBatchNameFromBatchCode(
																						batchCodes1[0]);
																		String benBatchCode = d001004.getValue().trim();

																		D100001 d100001 = VoucherMPOS.prepareReconObj(
																				lbrCode, reconNo, opdate, 999999,
																				VoucherCommon.firstBatch,
																				VoucherCommon.secondBatch, setNo,
																				scrollNoAbb, setNoABB, scrollNoAbb1,
																				scrollNoAbb2, crBrCode, "" + crAccno,
																				Double.valueOf(amount), "D");

																		D100002 d100002 = VoucherMPOS.prepareRecon2Obj(
																				lbrCode, reconNo, opdate, 999999,
																				VoucherCommon.firstBatch,
																				VoucherCommon.secondBatch, setNo,
																				scrollNoAbb, setNoABB, scrollNoAbb1,
																				scrollNoAbb2, crBrCode, "" + crAccno,
																				Double.valueOf(amount), "D");//
																		CoreTransactionMPOS.balanceOld(lbrCode, accNo,
																				amount, "D", session);
																		try {
																			session.save(d100001);
																			session.save(d100002);

																			resultMap.put(Code.RESULT, Code.SUCCESS);
																			resultMap.put(Code.SETNO,
																					String.valueOf(setNo));
																			resultMap.put(Code.SCROLLNO,
																					String.valueOf(scrollNo));
																			return resultMap;
																		} catch (Exception e) {
																			// TODO:
																			// handle
																			// exception
																			e.printStackTrace();
																			resultMap.put(Code.RESULT, Code.ERROR);
																			resultMap.put(Code.SETNO, "");
																			resultMap.put(Code.SCROLLNO, "");
																			return resultMap;
																		}
																	} else {
																		logger.error("ABB Transaction unsuccessful");
																		resultMap.put(Code.RESULT, Code.ERROR);
																		resultMap.put(Code.SETNO, "");
																		resultMap.put(Code.SCROLLNO, "");
																		return resultMap;
																	}
																} else {
																	logger.error("ABB Transaction unsuccessful");
																	resultMap.put(Code.RESULT, Code.ERROR);
																	resultMap.put(Code.SETNO, "");
																	resultMap.put(Code.SCROLLNO, "");
																	return resultMap;
																}
															} else {
																logger.error("ABB Transaction unsuccessful");
																resultMap.put(Code.RESULT, Code.ERROR);
																resultMap.put(Code.SETNO, "");
																resultMap.put(Code.SCROLLNO, "");
																return resultMap;
															}
														} else {
															logger.error("ABB Transaction unsuccessful");
															resultMap.put(Code.RESULT, Code.ERROR);
															resultMap.put(Code.SETNO, "");
															resultMap.put(Code.SCROLLNO, "");
															return resultMap;
														}
													} else {
														logger.error("ABB Transaction unsuccessful");
														resultMap.put(Code.RESULT, Code.ERROR);
														resultMap.put(Code.SETNO, "");
														resultMap.put(Code.SCROLLNO, "");
														return resultMap;
													}
												} else {
													logger.error("ABB Transaction unsuccessful");
													resultMap.put(Code.RESULT, Code.ERROR);
													resultMap.put(Code.SETNO, "");
													resultMap.put(Code.SCROLLNO, "");
													return resultMap;
												}
											} else {
												logger.error("ABB Transaction unsuccessful");
												resultMap.put(Code.RESULT, Code.ERROR);
												resultMap.put(Code.SETNO, "");
												resultMap.put(Code.SCROLLNO, "");
												return resultMap;
											}
										} else {
											logger.error("Transaction unsuccessful");
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "");
											resultMap.put(Code.SCROLLNO, "");
											return resultMap;
										}
									} else {
										logger.error("ABB Transaction unsuccessful");
										resultMap.put(Code.RESULT, Code.ERROR);
										resultMap.put(Code.SETNO, "");
										resultMap.put(Code.SCROLLNO, "");
										return resultMap;
									}
								} else {
									logger.error("Transaction unsuccessful");
									resultMap.put(Code.RESULT, Code.ERROR);
									resultMap.put(Code.SETNO, "");
									resultMap.put(Code.SCROLLNO, "");
									return resultMap;
								}
							} else {
								flag = false;
								logger.error("Transaction unsuccessful");
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "");
								resultMap.put(Code.SCROLLNO, "");
								return resultMap;
							}
						} else {
							flag = false;
							logger.error("Transaction unsuccessful");
							resultMap.put(Code.RESULT, Code.ERROR);
							resultMap.put(Code.SETNO, "");
							resultMap.put(Code.SCROLLNO, "");
							return resultMap;
						}
					} else {
						flag = false;
						logger.error("Transaction unsuccessful");
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "");
						resultMap.put(Code.SCROLLNO, "");
						return resultMap;
					}
				} else {
					flag = false;
					logger.error("Transaction unsuccessful");
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "");
					resultMap.put(Code.SCROLLNO, "");
					return resultMap;
				}
			} else {
				logger.error("Transaction unsuccessful");
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "");
				resultMap.put(Code.SCROLLNO, "");
				return resultMap;
			}
		} else {

			if ("Y".equalsIgnoreCase(ConfigurationLoader.getParameters(false).getProperty("SAME_BRANCH_VOUCHER_YN"))) {

				String crAccno = get32DigitAcctNo(getSysParameter(MSGConstants.MBRNCRACT).trim(), 0, 0);
				// + " 000000000000000000000000";
				try {
					resultMap = acctToGlVoucherNew(lbrCode, crAccno, transType, narration, amount, rrn, accNo, common,
							session);
				} catch (Exception e) {
					session.getTransaction().rollback();
					// TODO Auto-generated catch block
					e.printStackTrace();
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "");
					resultMap.put(Code.SCROLLNO, "");
				}
				return resultMap;
			} else {

				common.debit(lbrCode, accNo, transType.toUpperCase(), setNo, scrollNo, narration, amount, rrn, session);
				if (!common.isAborted) {
					if (VoucherMPOS.updateProductBalances(amount, lbrCode, "D", accNo.substring(0, 8).trim(), session)
							.equalsIgnoreCase(MSGConstants.SUCCESS)) {

						if (!common.isAborted) {
							logger.error("Transaction successful");
							int setNoABB = VoucherCommon.getNextSetNo();
							int scrollNoAbb = VoucherCommon.getNextScrollNo();
							int scrollNoAbb1 = VoucherCommon.getNextScrollNo();
							int scrollNoAbb2 = VoucherCommon.getNextScrollNo();
							common.creditABB(lbrCode, MSGConstants.ABB_ACC, transType.toUpperCase(), setNo, scrollNoAbb,
									narration, lbrCode, amount, rrn, reconNo, session);
							if (!common.isAborted) {
								if (VoucherMPOS.updateProductBalances(amount, lbrCode, "C", MSGConstants.ABB, session)
										.trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
									if (!common.isAborted) {
										logger.error("ABB Transaction successful");
										String crAccno = get32DigitAcctNo(
												getSysParameter(MSGConstants.MBRNCRACT).trim(), 0, 0);
										// + " 000000000000000000000000";
										int crBrCode = Integer.valueOf(getSysParameter(MSGConstants.MBBRANCH).trim());
										String crAcct ="IMPS/P2A/"  + lbrCode+"-"+accNo.substring(0, 8).trim()+"-"+String.valueOf(Integer.parseInt(accNo.substring(16, 24))) + "/"+ DateUtil.getcurrentDateStringDDMMYYYY()+ "/"+ rrn.trim() ;
										common.credit(crBrCode, crAccno, MSGConstants.ABB, setNoABB, scrollNoAbb1,
												crAcct, amount, rrn, session);
										if (!common.isAborted) {
											VoucherMPOS.updateProductBalances(amount, crBrCode, "C",
													crAccno.substring(0, 8).trim(), session);
											if (!common.isAborted) {
												logger.error("Other Bank GL Transaction successful");
												common.debitABB(crBrCode, MSGConstants.ABB_ACC, MSGConstants.ABB,
														setNoABB, scrollNoAbb2, narration, 2, amount, rrn, reconNo,
														session);
												if (!common.isAborted) {
													if (VoucherMPOS.updateProductBalances(amount, crBrCode, "D",
															MSGConstants.ABB_ACC.substring(0, 8).trim(), session).trim()
															.equalsIgnoreCase(MSGConstants.SUCCESS)) {
														Date opdate = DataUtils.getOpenDate(lbrCode);
														String batchCodes[] = Props.getBatchProperty("IMPS").split("~");
														D001004 d04OnlineBatchName = VoucherMPOS
																.getBatchNameFromBatchCode(batchCodes[0]);
														String onlineBatchName = d04OnlineBatchName.getValue().trim();
														String batchCodes1[] = Props.getBatchProperty("ABB").split("~");
														D001004 d001004 = VoucherMPOS
																.getBatchNameFromBatchCode(batchCodes1[0]);
														String benBatchCode = d001004.getValue().trim();

														D100001 d100001 = VoucherMPOS.prepareReconObj(lbrCode, reconNo,
																opdate, 999999, VoucherCommon.firstBatch,
																VoucherCommon.secondBatch, setNo, scrollNoAbb, setNoABB,
																scrollNoAbb1, scrollNoAbb2, crBrCode, "" + crAccno,
																Double.valueOf(amount), "D");
														logger.error("d100001::>>>" + d100001);

														D100002 d100002 = VoucherMPOS.prepareRecon2Obj(lbrCode, reconNo,
																opdate, 999999, VoucherCommon.firstBatch,
																VoucherCommon.secondBatch, setNo, scrollNoAbb, setNoABB,
																scrollNoAbb1, scrollNoAbb2, crBrCode, "" + crAccno,
																Double.valueOf(amount), "D");//
														logger.error("d100002::>>>" + d100002);
														String finalBalannce = CoreTransactionMPOS.balance(lbrCode,
																accNo, amount, "D", session);
														logger.error("Final Balance:=" + finalBalannce);
														try {
															if(!finalBalannce.equalsIgnoreCase("99")) {
																session.save(d100001);
																session.save(d100002);
																// session.flush();
																resultMap.put(Code.RESULT, Code.SUCCESS);
																resultMap.put(Code.SETNO, String.valueOf(setNo));
																resultMap.put(Code.SCROLLNO, String.valueOf(scrollNo));
																return resultMap;
															}else {
																logger.error("ABB Transaction unsuccessful");
																resultMap.put(Code.RESULT, Code.ERROR);
																resultMap.put(Code.SETNO, "");
																resultMap.put(Code.SCROLLNO, "");
																return resultMap;
															}
														} catch (Exception e) {
															// TODO: handle
															// exception
															e.printStackTrace();
															resultMap.put(Code.RESULT, Code.ERROR);
															resultMap.put(Code.SETNO, "");
															resultMap.put(Code.SCROLLNO, "");
															return resultMap;
														}
													} else {
														logger.error("ABB Transaction unsuccessful");
														resultMap.put(Code.RESULT, Code.ERROR);
														resultMap.put(Code.SETNO, "");
														resultMap.put(Code.SCROLLNO, "");
														return resultMap;
													}
												} else {
													logger.error("ABB Transaction unsuccessful");
													resultMap.put(Code.RESULT, Code.ERROR);
													resultMap.put(Code.SETNO, "");
													resultMap.put(Code.SCROLLNO, "");
													return resultMap;
												}
											} else {
												logger.error("ABB Transaction unsuccessful");
												resultMap.put(Code.RESULT, Code.ERROR);
												resultMap.put(Code.SETNO, "");
												resultMap.put(Code.SCROLLNO, "");
												return resultMap;
											}
										} else {
											logger.error("ABB Transaction unsuccessful");
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "");
											resultMap.put(Code.SCROLLNO, "");
											return resultMap;
										}
									} else {
										logger.error("ABB Transaction unsuccessful");
										resultMap.put(Code.RESULT, Code.ERROR);
										resultMap.put(Code.SETNO, "");
										resultMap.put(Code.SCROLLNO, "");
										return resultMap;
									}
								} else {
									logger.error("ABB Transaction unsuccessful");
									resultMap.put(Code.RESULT, Code.ERROR);
									resultMap.put(Code.SETNO, "");
									resultMap.put(Code.SCROLLNO, "");
									return resultMap;
								}
							} else {
								logger.error("ABB Transaction unsuccessful");
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "");
								resultMap.put(Code.SCROLLNO, "");
								return resultMap;
							}
						} else {
							logger.error("Transaction unsuccessful");
							resultMap.put(Code.RESULT, Code.ERROR);
							resultMap.put(Code.SETNO, "");
							resultMap.put(Code.SCROLLNO, "");
							return resultMap;
						}
					} else {
						logger.error("ABB Transaction unsuccessful");
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "");
						resultMap.put(Code.SCROLLNO, "");
						return resultMap;
					}
				} else {
					logger.error("Transaction unsuccessful");
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "");
					resultMap.put(Code.SCROLLNO, "");
					return resultMap;
				}
			}
		}
	}

	public static HashMap<String, String> billPaymentVoucherEntry(int lbrCode, String accNo, String transType,
			String narration, double amount, String rrn) {
		VoucherCommon common = new VoucherCommon();
		HashMap<String, String> resultMap = new HashMap<>();
		int setNo = VoucherCommon.getNextSetNo();
		int scrollNo = VoucherCommon.getNextScrollNo();
		int reconNo = VoucherCommon.getNextReconNo(lbrCode);
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		try {
			common.debit(lbrCode, accNo, transType.toUpperCase(), setNo, scrollNo, narration, amount, rrn, session);
			if (!common.isAborted) {
				if (VoucherMPOS.updateProductBalances(amount, lbrCode, "D", accNo.substring(0, 8).trim(), session)
						.equalsIgnoreCase(MSGConstants.SUCCESS)) {
					if (!common.isAborted) {
						logger.error("Transaction successful");
						int setNoABB = VoucherCommon.getNextSetNo();
						int scrollNoAbb = VoucherCommon.getNextScrollNo();
						int scrollNoAbb1 = VoucherCommon.getNextScrollNo();
						int scrollNoAbb2 = VoucherCommon.getNextScrollNo();
						common.creditABB(lbrCode, MSGConstants.ABB_ACC, transType.toUpperCase(), setNo, scrollNoAbb,
								narration, lbrCode, amount, rrn, reconNo, session);
						if (!common.isAborted) {
							if (VoucherMPOS
									.updateProductBalances(amount, lbrCode, "C",
											MSGConstants.ABB_ACC.substring(0, 8).trim(), session)
									.equalsIgnoreCase(MSGConstants.SUCCESS)) {
								if (!common.isAborted) {
									logger.error("ABB Transaction successful");
									String crAccno = get32DigitAcctNo(
											getSysParameter(MSGConstants.MBBBILLPAY_CR_ACC).trim(), 0, 0);
									// + " 000000000000000000000000";
									int crBrCode = Integer.valueOf(getSysParameter(MSGConstants.MBBRANCH).trim());
									common.credit(crBrCode, crAccno, MSGConstants.ABB, setNoABB, scrollNoAbb1,
											narration, amount, rrn, session);
									if (!common.isAborted) {
										if (VoucherMPOS
												.updateProductBalances(amount, crBrCode, "C",
														crAccno.substring(0, 8).trim(), session)
												.equalsIgnoreCase(MSGConstants.SUCCESS)) {
											if (!common.isAborted) {
												logger.error("Other Bank GL Transaction successful");
												common.debitABB(crBrCode, MSGConstants.ABB_ACC, MSGConstants.ABB,
														setNoABB, scrollNoAbb2, narration, 2, amount, rrn, reconNo,
														session);
												if (!common.isAborted) {
													if (VoucherMPOS.updateProductBalances(amount, crBrCode, "D",
															MSGConstants.ABB_ACC.substring(0, 8).trim(), session).trim()
															.equalsIgnoreCase(MSGConstants.SUCCESS)) {
														Date opdate = DataUtils.getOpenDate(lbrCode);
														String batchCodes[] = Props.getBatchProperty("IMPS").split("~");
														D001004 d04OnlineBatchName = VoucherMPOS
																.getBatchNameFromBatchCode(batchCodes[0]);
														String onlineBatchName = d04OnlineBatchName.getValue().trim();
														String batchCodes1[] = Props.getBatchProperty("ABB").split("~");
														D001004 d001004 = VoucherMPOS
																.getBatchNameFromBatchCode(batchCodes1[0]);
														String benBatchCode = d001004.getValue().trim();

														D100001 d100001 = VoucherMPOS.prepareReconObj(lbrCode, reconNo,
																opdate, 999999, onlineBatchName, benBatchCode, setNo,
																scrollNoAbb, setNoABB, scrollNoAbb1, scrollNoAbb2,
																crBrCode, "" + crAccno, Double.valueOf(amount), "D");
														System.out.println("d100001::>>>" + d100001);

														D100002 d100002 = VoucherMPOS.prepareRecon2Obj(lbrCode, reconNo,
																opdate, 999999, onlineBatchName, benBatchCode, setNo,
																scrollNoAbb, setNoABB, scrollNoAbb1, scrollNoAbb2,
																crBrCode, "" + crAccno, Double.valueOf(amount), "D");//
														System.out.println("d100002::>>>" + d100002);
														String bal = CoreTransactionMPOS.balance(lbrCode, accNo, amount,
																"D", session);
														if (bal != null && bal.trim().length() > 0
																&& !bal.equalsIgnoreCase("51")
																&& !bal.equalsIgnoreCase("99")) {
															try {
																session.save(d100001);
																session.save(d100002);
																session.flush();
																t.commit();
																resultMap.put(Code.RESULT, Code.SUCCESS);
																resultMap.put(Code.SETNO, String.valueOf(setNo));
																resultMap.put(Code.SCROLLNO, String.valueOf(scrollNo));
																return resultMap;
															} catch (Exception e) {
																// TODO: handle
																// exception
																e.printStackTrace();
																if (t.isActive())
																	t.rollback();
																resultMap.put(Code.RESULT, Code.ERROR);
																resultMap.put(Code.SETNO, "");
																resultMap.put(Code.SCROLLNO, "");
																return resultMap;
															}

														} else {
															if (t.isActive())
																t.rollback();
															logger.error("ABB Transaction unsuccessful");
															resultMap.put(Code.RESULT, Code.ERROR);
															resultMap.put(Code.SETNO, "");
															resultMap.put(Code.SCROLLNO, "");
															return resultMap;
														}
													} else {
														if (t.isActive())
															t.rollback();
														logger.error("ABB Transaction unsuccessful");
														resultMap.put(Code.RESULT, Code.ERROR);
														resultMap.put(Code.SETNO, "");
														resultMap.put(Code.SCROLLNO, "");
														return resultMap;
													}
												} else {
													if (t.isActive())
														t.rollback();
													logger.error("ABB Transaction unsuccessful");
													resultMap.put(Code.RESULT, Code.ERROR);
													resultMap.put(Code.SETNO, "");
													resultMap.put(Code.SCROLLNO, "");
													return resultMap;
												}
											} else {
												if (t.isActive())
													t.rollback();
												logger.error("ABB Transaction unsuccessful");
												resultMap.put(Code.RESULT, Code.ERROR);
												resultMap.put(Code.SETNO, "");
												resultMap.put(Code.SCROLLNO, "");
												return resultMap;
											}
										} else {
											if (t.isActive())
												t.rollback();
											logger.error("ABB Transaction unsuccessful");
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "");
											resultMap.put(Code.SCROLLNO, "");
											return resultMap;
										}
									} else {
										if (t.isActive())
											t.rollback();
										logger.error("ABB Transaction unsuccessful");
										resultMap.put(Code.RESULT, Code.ERROR);
										resultMap.put(Code.SETNO, "");
										resultMap.put(Code.SCROLLNO, "");
										return resultMap;
									}
								} else {
									if (t.isActive())
										t.rollback();
									logger.error("ABB Transaction unsuccessful");
									resultMap.put(Code.RESULT, Code.ERROR);
									resultMap.put(Code.SETNO, "");
									resultMap.put(Code.SCROLLNO, "");
									return resultMap;
								}
							} else {
								if (t.isActive())
									t.rollback();
								logger.error("ABB Transaction unsuccessful");
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "");
								resultMap.put(Code.SCROLLNO, "");
								return resultMap;
							}
						} else {
							if (t.isActive())
								t.rollback();
							logger.error("ABB Transaction unsuccessful");
							resultMap.put(Code.RESULT, Code.ERROR);
							resultMap.put(Code.SETNO, "");
							resultMap.put(Code.SCROLLNO, "");
							return resultMap;
						}

					} else {
						if (t.isActive())
							t.rollback();
						logger.error("Transaction unsuccessful");
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "");
						resultMap.put(Code.SCROLLNO, "");
						return resultMap;
					}
				} else {
					if (t.isActive())
						t.rollback();
					logger.error("Transaction unsuccessful");
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "");
					resultMap.put(Code.SCROLLNO, "");
					return resultMap;
				}
			} else {
				if (t.isActive())
					t.rollback();
				logger.error("Transaction unsuccessful");
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "");
				resultMap.put(Code.SCROLLNO, "");
				return resultMap;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			if (t.isActive())
				t.rollback();
			resultMap.put(Code.RESULT, Code.ERROR);
			resultMap.put(Code.SETNO, "");
			resultMap.put(Code.SCROLLNO, "");
			return resultMap;
		} finally {
			session.close();
			session = null;
			t = null;
		}

	}

	public static HashMap<String, String> otherBankReverseVoucherEntry(int lbrCode, String accNo, String transType,
			String narration, double amount, String rrn) {
		VoucherCommon common = new VoucherCommon();
		HashMap<String, String> resultMap = new HashMap<>();
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		try {
			int setNo = VoucherCommon.getNextSetNo();
			int scrollNo = VoucherCommon.getNextScrollNo();
			//String drAccno = get32DigitAcctNo(getSysParameter(MSGConstants.MBRNCRACT).trim(),0,0);
			
			String drAccno = get32DigitAcctNo(getSysParameter(ConfigurationLoader.getParameters(false).getProperty("REV_GL")).trim(),0,0);
			int crBrcode = Integer.valueOf(getSysParameter(MSGConstants.MBBRANCH).trim());
			
			if("Y".equalsIgnoreCase(ConfigurationLoader.getParameters(false).getProperty("SAME_BRANCH_VOUCHER_YN"))) {
					resultMap = acctToGlVoucherNew(lbrCode, accNo, transType,narration, amount, rrn,drAccno, common,session, t);
					return resultMap;
			}
			
			int reconNo = getNextReconNo(crBrcode);
			logger.error("accNo::>>" + accNo);
			logger.error("lbrCode::>>" + lbrCode);
			if (ConfigurationLoader.getParameters(false).getProperty("IMPS_CHG_FLAG").trim()
					.equalsIgnoreCase(MSGConstants.YES)
					&& Double.valueOf(
							ConfigurationLoader.getParameters(false).getProperty("CHRG_AMOUNT_LIMIT")) < amount) {
				double chgAmt = Double
						.valueOf(ConfigurationLoader.getParameters(false).getProperty("IMPS_CHG_AMOUNT").trim());
				common.credit(lbrCode, accNo, transType.toUpperCase(), setNo, scrollNo, "IMPS Charges/" + chgAmt,
						chgAmt, rrn, session);
				if (!common.isAborted) {
					if (VoucherMPOS.updateProductBalances(chgAmt, lbrCode, "C", accNo.substring(0, 8).trim(), session)
							.equalsIgnoreCase(MSGConstants.SUCCESS)) {
						if (!common.isAborted) {
							if (VoucherMPOS.updateProductBalances(chgAmt, lbrCode, "D",
									ConfigurationLoader.getParameters(false).getProperty("IMPS_CHG_PRODUCT"), session)
									.equalsIgnoreCase(MSGConstants.SUCCESS)) {

								common.credit(lbrCode, accNo, MSGConstants.ABB, setNo, scrollNo, narration, amount, rrn,
										session);
								if (!common.isAborted) {
									if (VoucherMPOS.updateProductBalances(amount, lbrCode, "C",
											accNo.substring(0, 8).trim(), session).trim()
											.equalsIgnoreCase(MSGConstants.SUCCESS)) {
										String bal = CoreTransactionMPOS.balance(lbrCode, accNo, amount, "C", session);
										String bal1 = CoreTransactionMPOS.balance(lbrCode, accNo, chgAmt, "C", session);
										if ((bal != null && bal.trim().length() > 0 && !bal.equalsIgnoreCase("51")
												&& !bal.equalsIgnoreCase("99"))&& (bal1 != null && bal1.trim().length() > 0 && !bal1.equalsIgnoreCase("51")
														&& !bal1.equalsIgnoreCase("99"))) {
											if (!common.isAborted) {
												int setNoABB = VoucherCommon.getNextSetNo();
												int scrollNoAbb = VoucherCommon.getNextScrollNo();
												int scrollNoAbb1 = VoucherCommon.getNextScrollNo();
												int scrollNoAbb2 = VoucherCommon.getNextScrollNo();
												common.debitABB(lbrCode, MSGConstants.ABB_ACC, MSGConstants.ABB, setNo,
														scrollNoAbb, narration, crBrcode, amount, rrn, reconNo,
														session);
												if (!common.isAborted) {
													if (VoucherMPOS.updateProductBalances(amount, lbrCode, "D",
															MSGConstants.ABB_ACC.substring(0, 8).trim(), session).trim()
															.equalsIgnoreCase(MSGConstants.SUCCESS)) {
														if (!common.isAborted) {
															logger.error("ABB Transaction successful");
															common.debit(crBrcode, drAccno,
																	transType.trim().toUpperCase(), setNoABB,
																	scrollNoAbb1, narration, amount, rrn, session);
															if (!common.isAborted) {
																if (VoucherMPOS
																		.updateProductBalances(amount, crBrcode, "D",
																				drAccno.substring(0, 8).trim(), session)
																		.trim()
																		.equalsIgnoreCase(MSGConstants.SUCCESS)) {
																	if (!common.isAborted) {
																		logger.error(
																				"Other Bank GL Transaction successful");
																		common.creditABB(crBrcode, MSGConstants.ABB_ACC,
																				transType.trim().toUpperCase(),
																				setNoABB, scrollNoAbb2, narration,
																				lbrCode, amount, rrn, reconNo, session);
																		VoucherMPOS.updateProductBalances(amount,
																				crBrcode,
																				"C", MSGConstants.ABB_ACC
																						.substring(0, 8).trim(),
																				session);

																		Date opdate = DataUtils.getOpenDate(lbrCode);

																		String batchCodes[] = Props
																				.getBatchProperty("IMPS").split("~");
																		D001004 d04OnlineBatchName = VoucherMPOS
																				.getBatchNameFromBatchCode(
																						batchCodes[0]);
																		String onlineBatchName = d04OnlineBatchName
																				.getValue().trim();
																		String batchCodes1[] = Props
																				.getBatchProperty("ABB").split("~");
																		D001004 d001004 = VoucherMPOS
																				.getBatchNameFromBatchCode(
																						batchCodes1[0]);
																		String benBatchCode = d001004.getValue().trim();

																		D100001 d100001 = VoucherMPOS.prepareReconObj(
																				crBrcode, reconNo, opdate, 999999,
																				onlineBatchName, benBatchCode, setNoABB,
																				scrollNoAbb2, setNo, scrollNo,
																				scrollNoAbb, lbrCode, "" + accNo,
																				Double.valueOf(amount), "D");
																		D100002 d100002 = VoucherMPOS.prepareRecon2Obj(
																				crBrcode, reconNo, opdate, 999999,
																				onlineBatchName, benBatchCode, setNoABB,
																				scrollNoAbb2, setNo, scrollNo,
																				scrollNoAbb, lbrCode, "" + accNo,
																				Double.valueOf(amount), "D");

																		session.save(d100001);
																		session.save(d100002);
																		t.commit();
																		resultMap.put(Code.RESULT, Code.SUCCESS);
																		resultMap.put(Code.SETNO,
																				String.valueOf(setNo));
																		resultMap.put(Code.SCROLLNO,
																				String.valueOf(scrollNo));
																		return resultMap;
																	} else {
																		logger.error("ABB Transaction unsuccessful");
																		if(t.isActive())t.rollback();
																		resultMap.put(Code.RESULT, Code.ERROR);
																		resultMap.put(Code.SETNO, "");
																		resultMap.put(Code.SCROLLNO, "");
																		common = null;
																		return resultMap;
																	}
																} else {
																	if(t.isActive())t.rollback();
																	logger.error("ABB Transaction unsuccessful");
																	resultMap.put(Code.RESULT, Code.ERROR);
																	resultMap.put(Code.SETNO, "");
																	resultMap.put(Code.SCROLLNO, "");
																	common = null;
																	return resultMap;
																}
															} else {
																if(t.isActive())t.rollback();
																logger.error("ABB Transaction unsuccessful");
																resultMap.put(Code.RESULT, Code.ERROR);
																resultMap.put(Code.SETNO, "");
																resultMap.put(Code.SCROLLNO, "");
																common = null;
																return resultMap;
															}
														} else {
															if(t.isActive())t.rollback();
															logger.error("ABB Transaction unsuccessful");
															resultMap.put(Code.RESULT, Code.ERROR);
															resultMap.put(Code.SETNO, "");
															resultMap.put(Code.SCROLLNO, "");
															common = null;
															return resultMap;
														}
													} else {
														if(t.isActive())t.rollback();
														logger.error("ABB Transaction unsuccessful");
														resultMap.put(Code.RESULT, Code.ERROR);
														resultMap.put(Code.SETNO, "");
														resultMap.put(Code.SCROLLNO, "");
														common = null;
														return resultMap;
													}
												} else {
													if(t.isActive())t.rollback();
													logger.error("ABB Transaction unsuccessful");
													resultMap.put(Code.RESULT, Code.ERROR);
													resultMap.put(Code.SETNO, "");
													resultMap.put(Code.SCROLLNO, "");
													common = null;
													return resultMap;
												}
											} else {
												if(t.isActive())t.rollback();
												logger.error("Transaction unsuccessful");
												resultMap.put(Code.RESULT, Code.ERROR);
												resultMap.put(Code.SETNO, "");
												resultMap.put(Code.SCROLLNO, "");
												common = null;
												return resultMap;
											}
										} else {
											if(t.isActive())t.rollback();
											logger.error("Transaction unsuccessful");
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "");
											resultMap.put(Code.SCROLLNO, "");
											common = null;
											return resultMap;
										}
									} else {
										if(t.isActive())t.rollback();
										logger.error("Transaction unsuccessful");
										resultMap.put(Code.RESULT, Code.ERROR);
										resultMap.put(Code.SETNO, "");
										resultMap.put(Code.SCROLLNO, "");
										common = null;
										return resultMap;
									}

								} else {
									if(t.isActive())t.rollback();
									logger.error("Transaction unsuccessful");
									resultMap.put(Code.RESULT, Code.ERROR);
									resultMap.put(Code.SETNO, "");
									resultMap.put(Code.SCROLLNO, "");
									common = null;
									return resultMap;
								}

							} else {
								if(t.isActive())t.rollback();
								logger.error("Transaction unsuccessful");
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "");
								resultMap.put(Code.SCROLLNO, "");
								common = null;
								return resultMap;
							}
						} else {
							if(t.isActive())t.rollback();
							logger.error("Transaction unsuccessful");
							resultMap.put(Code.RESULT, Code.ERROR);
							resultMap.put(Code.SETNO, "");
							resultMap.put(Code.SCROLLNO, "");
							common = null;
							return resultMap;
						}
					} else {
						if(t.isActive())t.rollback();
						logger.error("Transaction unsuccessful");
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "");
						resultMap.put(Code.SCROLLNO, "");
						common = null;
						return resultMap;
					}
				} else {
					if(t.isActive())t.rollback();
					logger.error("Transaction unsuccessful");
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "");
					resultMap.put(Code.SCROLLNO, "");
					common = null;
					return resultMap;
				}
			} else {
				common.credit(lbrCode, accNo, MSGConstants.ABB, setNo, scrollNo, narration, amount, rrn, session);
				if (!common.isAborted) {
					if (VoucherMPOS.updateProductBalances(amount, lbrCode, "C", accNo.substring(0, 8).trim(), session)
							.trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
						String bal = CoreTransactionMPOS.balance(lbrCode, accNo, amount, "C", session);
						if (bal != null && bal.trim().length() > 0 && !bal.equalsIgnoreCase("51")
								&& !bal.equalsIgnoreCase("99")) {
							if (!common.isAborted) {
								int setNoABB =VoucherCommon. getNextSetNo();
								int scrollNoAbb = VoucherCommon.getNextScrollNo();
								int scrollNoAbb1 = VoucherCommon.getNextScrollNo();
								int scrollNoAbb2 = VoucherCommon.getNextScrollNo();
								common.debitABB(lbrCode, MSGConstants.ABB_ACC, MSGConstants.ABB, setNo, scrollNoAbb,
										narration, crBrcode, amount, rrn, reconNo, session);
								if (!common.isAborted) {
									if (VoucherMPOS
											.updateProductBalances(amount, lbrCode, "D",
													MSGConstants.ABB_ACC.substring(0, 8).trim(), session)
											.trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
										if (!common.isAborted) {
											logger.error("ABB Transaction successful");
											// String
											// drAccno=getSysParameter("MBBILLPAY").trim()+"
											// 000000000000000000000000";
											common.debit(crBrcode, drAccno, transType.trim().toUpperCase(), setNoABB,
													scrollNoAbb1, narration, amount, rrn, session);
											if (!common.isAborted) {
												if (VoucherMPOS
														.updateProductBalances(amount, crBrcode, "D",
																drAccno.substring(0, 8).trim(), session)
														.trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
													if (!common.isAborted) {
														logger.error("Other Bank GL Transaction successful");
														common.creditABB(crBrcode, MSGConstants.ABB_ACC,
																transType.trim().toUpperCase(), setNoABB, scrollNoAbb2,
																narration, lbrCode, amount, rrn, reconNo, session);
														VoucherMPOS.updateProductBalances(amount, crBrcode, "C",
																MSGConstants.ABB_ACC.substring(0, 8).trim(), session);

														Date opdate = DataUtils.getOpenDate(lbrCode);

														String batchCodes[] = Props.getBatchProperty("IMPS").split("~");
														D001004 d04OnlineBatchName = VoucherMPOS
																.getBatchNameFromBatchCode(batchCodes[0]);
														String onlineBatchName = d04OnlineBatchName.getValue().trim();
														String batchCodes1[] = Props.getBatchProperty("ABB").split("~");
														D001004 d001004 = VoucherMPOS
																.getBatchNameFromBatchCode(batchCodes1[0]);
														String benBatchCode = d001004.getValue().trim();

														D100001 d100001 = VoucherMPOS.prepareReconObj(crBrcode, reconNo,
																opdate, 999999, onlineBatchName, benBatchCode, setNoABB,
																scrollNoAbb2, setNo, scrollNo, scrollNoAbb, lbrCode,
																"" + accNo, Double.valueOf(amount), "D");
														System.out.println("d100001::>>>" + d100001);
														D100002 d100002 = VoucherMPOS.prepareRecon2Obj(crBrcode,
																reconNo, opdate, 999999, onlineBatchName, benBatchCode,
																setNoABB, scrollNoAbb2, setNo, scrollNo, scrollNoAbb,
																lbrCode, "" + accNo, Double.valueOf(amount), "D");
														System.out.println("d100002::>>>" + d100002);
															session.save(d100001);
															session.save(d100002);
															t.commit();
														resultMap.put(Code.RESULT, Code.SUCCESS);
														resultMap.put(Code.SETNO, String.valueOf(setNo));
														resultMap.put(Code.SCROLLNO, String.valueOf(scrollNo));
														return resultMap;
													} else {
														logger.error("ABB Transaction unsuccessful");
														if(t.isActive())t.rollback();
														resultMap.put(Code.RESULT, Code.ERROR);
														resultMap.put(Code.SETNO, "");
														resultMap.put(Code.SCROLLNO, "");
														common = null;
														return resultMap;
													}
												} else {
													if(t.isActive())t.rollback();
													logger.error("ABB Transaction unsuccessful");
													resultMap.put(Code.RESULT, Code.ERROR);
													resultMap.put(Code.SETNO, "");
													resultMap.put(Code.SCROLLNO, "");
													common = null;
													return resultMap;
												}
											} else {
												if(t.isActive())t.rollback();
												logger.error("ABB Transaction unsuccessful");
												resultMap.put(Code.RESULT, Code.ERROR);
												resultMap.put(Code.SETNO, "");
												resultMap.put(Code.SCROLLNO, "");
												common = null;
												return resultMap;
											}
										} else {
											if(t.isActive())t.rollback();
											logger.error("ABB Transaction unsuccessful");
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "");
											resultMap.put(Code.SCROLLNO, "");
											common = null;
											return resultMap;
										}
									} else {
										if(t.isActive())t.rollback();
										logger.error("ABB Transaction unsuccessful");
										resultMap.put(Code.RESULT, Code.ERROR);
										resultMap.put(Code.SETNO, "");
										resultMap.put(Code.SCROLLNO, "");
										common = null;
										return resultMap;
									}
								} else {
									if(t.isActive())t.rollback();
									logger.error("ABB Transaction unsuccessful");
									resultMap.put(Code.RESULT, Code.ERROR);
									resultMap.put(Code.SETNO, "");
									resultMap.put(Code.SCROLLNO, "");
									common = null;
									return resultMap;
								}
							} else {
								if(t.isActive())t.rollback();
								logger.error("Transaction unsuccessful");
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "");
								resultMap.put(Code.SCROLLNO, "");
								common = null;
								return resultMap;
							}
						} else {
							if(t.isActive())t.rollback();
							logger.error("Transaction unsuccessful");
							resultMap.put(Code.RESULT, Code.ERROR);
							resultMap.put(Code.SETNO, "");
							resultMap.put(Code.SCROLLNO, "");
							common = null;
							return resultMap;
						}
					} else {
						if(t.isActive())t.rollback();
						logger.error("Transaction unsuccessful");
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "");
						resultMap.put(Code.SCROLLNO, "");
						common = null;
						return resultMap;
					}

				} else {
					if(t.isActive())t.rollback();
					logger.error("Transaction unsuccessful");
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "");
					resultMap.put(Code.SCROLLNO, "");
					common = null;
					return resultMap;
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			if(t.isActive())t.rollback();
			e.printStackTrace();
			resultMap.put(Code.RESULT, Code.ERROR);
			resultMap.put(Code.SETNO, "");
			resultMap.put(Code.SCROLLNO, "");
			logger.error("ERROR", e);
			return resultMap;
		} finally {
			session.close();
			session = null;
			t=null;
		}
	}

	public static HashMap<String, String> otherBankCreditVoucherEntryNew(int lbrCode, String accNo, String transType,
			String narration, double amount, String rrn) throws Exception {
		logger.error("<<<<<::: otherBankCreditVoucherEntryNew.service() :::>>>>>");
		logger.error("Parameters received are :>> Bracnh Code:" + lbrCode + " Account No:" + accNo + " transType: "
				+ transType + " narration: " + narration + " amount:" + amount + " RRN:" + rrn);
		VoucherCommon common = new VoucherCommon();
		HashMap<String, String> resultMap = new HashMap<>();
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();

		try {
			t.setTimeout(30);
			// VoucherCommon common = new VoucherCommon();
			// HashMap<String, String> resultMap = new HashMap<>();
			// Transaction tx=session.beginTransaction();
			String debAccNo = get32DigitAcctNo(ConfigurationLoader.getParameters(false).getProperty("Default_MBBEN_GL"),
					0, 0);// + " 000000000000000000000000";
			try {
				if ("UPI".equalsIgnoreCase(transType))
					debAccNo = get32DigitAcctNo(
							getSysParameter(ConfigurationLoader.getParameters(false).getProperty("UPI_GL")).trim(), 0,
							0);// + " 000000000000000000000000";
				else
					debAccNo = get32DigitAcctNo(getSysParameter(MSGConstants.MBBENCRACC).trim(), 0, 0);// + "
																										// 000000000000000000000000";
			} catch (Exception ex) {
				logger.error("Exception", ex);
			}
			if ("Y".equalsIgnoreCase(ConfigurationLoader.getParameters(false).getProperty("SAME_BRANCH_VOUCHER_YN"))) {
				resultMap = acctToGlVoucherNew(lbrCode, accNo, transType, narration, amount, rrn, debAccNo, common,
						session, t);
				return resultMap;
			} else {
				int debBrCode = Integer.valueOf(getSysParameter(MSGConstants.MBBRANCH).trim());
				resultMap.put("DrBrCode", debBrCode + "");
				resultMap.put("DrAcctId", debAccNo);
				if (lbrCode == debBrCode) {

					logger.error("Same Branch Fund Transfer....");
					int setNo = VoucherCommon.getNextSetNo();
					int mainScrollNo = VoucherCommon.getNextScrollNo();
					common.debitSameBranch(debBrCode, debAccNo, transType.toUpperCase(), setNo,
							VoucherCommon.getNextScrollNo(), narration, amount, rrn, mainScrollNo, session);
					System.out.println("VoucherCommon.otherBranchVouchers() common.isAborted " + common.isAborted);
					if (!common.isAborted) {
						if (VoucherMPOS
								.updateProductBalances(amount, debBrCode, "D", debAccNo.substring(0, 8).trim(), session)
								.equalsIgnoreCase(MSGConstants.SUCCESS)) {
							String balResponse = CoreTransactionMPOS.balance(debBrCode, debAccNo.trim(), amount, "D",
									session);
							if (balResponse != null && balResponse.trim().length() > 0
									&& !balResponse.trim().equalsIgnoreCase("99")
									&& !balResponse.trim().equalsIgnoreCase("51")) {
								if (!common.isAborted) {
									/*** Added By Aniket Desai on 28 Aug, 2019 for Loan Changes:---Starts--- ***/
									logger.error("Transaction successful");
									logger.error("accNo::>>" + accNo);
									logger.error("lbrCode::>>" + lbrCode);
									D009022 creditAccount = session.get(D009022.class, new D009022Id(lbrCode, accNo));

									D009021 creditProductMaster = session.get(D009021.class,
											new D009021Id(lbrCode, accNo.substring(0, 8).trim()));
									if (RtgsNeftHostToHostConstants.LOAN.getMessage()
											.contains(creditProductMaster.getModuleType() + "")) {
										logger.error("LOAN Transaction");
										D009022 debiteAccount = session.get(D009022.class,
												new D009022Id(debBrCode, debAccNo));
										if (creditAccount.getCustNo() != debiteAccount.getCustNo()) {
											logger.error("Transaction unsuccessful " + creditAccount.getCustNo() + "!="
													+ debiteAccount.getCustNo());
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "0");
											resultMap.put(Code.SCROLLNO, "0");
											return resultMap;
										}
										LoanServiceImpl loanService = new LoanServiceImpl();
										String status = loanService.numberOfVoucherOnLoanType(creditAccount,
												creditProductMaster, lbrCode,
												creditAccount.getId().getPrdAcctId().substring(0, 8).trim(), accNo,
												transType, narration, amount, rrn, setNo,
												VoucherCommon.getNextScrollNo(), session);
										logger.error("Loan Transaction Status:" + status);
										if (status.equalsIgnoreCase("True"))
											common.isAborted = true;
									} else if (!RtgsNeftHostToHostConstants.LOAN.getMessage()
											.contains(creditProductMaster.getModuleType() + "")) {

										common.creditSameBranch(lbrCode, accNo, transType, setNo,
												VoucherCommon.getNextScrollNo(), narration, amount, rrn, mainScrollNo,
												session);
									}
									/*
									 * common.creditSameBranch(lbrCode, benAccNo, transType, setNo,
									 * VoucherCommon.getNextScrollNo(), narration, amount, rrn, mainScrollNo,
									 * session);
									 */
									/*** Added By Aniket Desai on 28 Aug, 2019 for Loan Changes:---END--- ***/

									if (!common.isAborted) {

										/***
										 * Added By Aniket Desai on 3rd Sep, 2019 for Loan Voucher(Double CR To Loan
										 * Account):--Start--
										 ***/
										String bal = null;
										if (RtgsNeftHostToHostConstants.LOAN.getMessage()
												.contains(creditProductMaster.getModuleType() + ""))
											bal = amount + "";
										else if (VoucherMPOS
												.updateProductBalances(amount, lbrCode, "C",
														accNo.substring(0, 8).trim(), session)
												.equalsIgnoreCase(MSGConstants.SUCCESS))
											bal = CoreTransactionMPOS.balanceOld(lbrCode, accNo.trim(), amount, "C",
													session);

										/*
										 * if (VoucherMPOS.updateProductBalances(amount, lbrCode, "C",
										 * benAccNo.substring(0, 8).trim(), session)
										 * .equalsIgnoreCase(MSGConstants.SUCCESS)) { String bal =
										 * CoreTransactionMPOS.balance(lbrCode, benAccNo.trim(), amount, "C", session);
										 */

										/***
										 * Added By Aniket Desai on 3rd Sep, 2019 for Loan Voucher(Double CR To Loan
										 * Account):--End--
										 ***/
										if (bal != null && bal.trim().length() > 0 && !bal.trim().equalsIgnoreCase("51")
												&& !bal.trim().equalsIgnoreCase("99")) {
											// tx.commit();
											// session.close();

											if (transType.contains("~")) {
												String[] type = transType.split("~");
												String cashFlowType = type[0];
												transType = type[1];
											}
											String batchCodes[] = Props.getBatchProperty(transType).split("~");
											/***
											 * Added and commited by Aniket Desai on 23rd Oct, 2019 for issue #48692
											 ***/
											// D001004 d04OnlineBatchName =
											// VoucherMPOS.getBatchNameFromBatchCode(batchCodes[0]);

											Date openDate = getOpenDateNew(lbrCode, session);
											D010004 d04OnlineBatchName = getSelectedBatchNew(lbrCode, batchCodes,
													openDate, session);
											String onlineBatchName = d04OnlineBatchName.getId().getBatchCd().trim();
											resultMap.put(Code.RESULT, Code.SUCCESS);
											resultMap.put("Batch", onlineBatchName);
											resultMap.put(Code.SETNO, String.valueOf(setNo));
											resultMap.put(Code.SCROLLNO, String.valueOf(mainScrollNo));
											// throw new Exception("Testing");
											t.commit();
											session.close();
											return resultMap;
										} /*
											 * else { logger.error("Transaction unsuccessful");
											 * resultMap.put(Code.RESULT, Code.ERROR); resultMap.put(Code.SETNO, "0");
											 * resultMap.put(Code.SCROLLNO, "0"); return resultMap; } }
											 */ else {
											logger.error("Transaction unsuccessful");
											t.rollback();
											// session.close();
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "0");
											resultMap.put(Code.SCROLLNO, "0");
											session.close();
											return resultMap;
										}
									} else {
										logger.error("Transaction unsuccessful");
										t.rollback();
										// session.close();
										resultMap.put(Code.RESULT, Code.ERROR);
										resultMap.put(Code.SETNO, "0");
										resultMap.put(Code.SCROLLNO, "0");
										session.close();
										return resultMap;
									}
								} else {
									logger.error("Transaction unsuccessful");
									t.rollback();
									// session.close();
									resultMap.put(Code.RESULT, Code.ERROR);
									resultMap.put(Code.SETNO, "0");
									resultMap.put(Code.SCROLLNO, "0");
									session.close();
									return resultMap;
								}
							} else {
								logger.error("Transaction unsuccessful");
								t.rollback();
								// session.close();
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "0");
								resultMap.put(Code.SCROLLNO, "0");
								session.close();
								return resultMap;
							}
						} else {
							logger.error("Transaction unsuccessful");
							t.rollback();
							// session.close();
							resultMap.put(Code.RESULT, Code.ERROR);
							resultMap.put(Code.SETNO, "0");
							resultMap.put(Code.SCROLLNO, "0");
							session.close();
							return resultMap;
						}
					} else {
						logger.error("Transaction unsuccessful");
						t.rollback();
						// session.close();
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "0");
						resultMap.put(Code.SCROLLNO, "0");
						session.close();
						return resultMap;
					}

				} else {
					int setNo = VoucherCommon.getNextSetNo();
					int scrollNo = VoucherCommon.getNextScrollNo();
					String drAccno = debAccNo; // getSysParameter(MSGConstants.MBBENCRACC).trim() + "
												// 000000000000000000000000";
					int crBrcode = Integer.valueOf(getSysParameter(MSGConstants.MBBRANCH).trim());
					int reconNo = getNextReconNo(crBrcode);
					logger.error("accNo::>>" + accNo);
					logger.error("lbrCode::>>" + lbrCode);
					D009022 creditAccount = session.get(D009022.class, new D009022Id(lbrCode, accNo));
					D009021 creditProductMaster = session.get(D009021.class,
							new D009021Id(lbrCode, accNo.substring(0, 8).trim()));
					if (RtgsNeftHostToHostConstants.LOAN.getMessage()
							.contains(creditProductMaster.getModuleType() + "")) {
						LoanServiceImpl loanService = new LoanServiceImpl();
						String status = loanService.numberOfVoucherOnLoanType(creditAccount, creditProductMaster,
								lbrCode, creditAccount.getId().getPrdAcctId().substring(0, 8).trim(), accNo,
								MSGConstants.ABB, narration, amount, rrn, setNo, scrollNo, session);
						if (status.equalsIgnoreCase("True"))
							common.isAborted = true;
					} else if ("20".contains(creditProductMaster.getModuleType() + ""))
						common.creditVoucherForTD(lbrCode, accNo, MSGConstants.ABB, setNo, scrollNo, narration, amount,
								rrn, session);
					else if (!RtgsNeftHostToHostConstants.LOAN.getMessage()
							.contains(creditProductMaster.getModuleType() + "")) {

						common.credit(lbrCode, accNo, MSGConstants.ABB, setNo, scrollNo, narration, amount, rrn,
								session);
					}
					if (!common.isAborted) {
						/***
						 * Added By Aniket Desai on 3rd Sep, 2019 for Loan Voucher(Double CR To Loan
						 * Account):--Start--
						 ***/
						String bal = null;
						if (RtgsNeftHostToHostConstants.LOAN.getMessage()
								.contains(creditProductMaster.getModuleType() + ""))
							bal = amount + "";
						else if (VoucherMPOS
								.updateProductBalances(amount, lbrCode, "C", accNo.substring(0, 8).trim(), session)
								.trim().equalsIgnoreCase(MSGConstants.SUCCESS))
							bal = CoreTransactionMPOS.balance(lbrCode, accNo, amount, "C", session);

						/*
						 * if (VoucherMPOS.updateProductBalances(amount, lbrCode, "C",
						 * accNo.substring(0, 8).trim(), session)
						 * .trim().equalsIgnoreCase(MSGConstants.SUCCESS)) { String bal =
						 * CoreTransactionMPOS.balance(lbrCode, accNo, amount, "C", session);
						 */
						/***
						 * Added By Aniket Desai on 3rd Sep, 2019 for Loan Voucher(Double CR To Loan
						 * Account):--End--
						 ***/

						if (bal != null && bal.trim().length() > 0 && !bal.trim().equalsIgnoreCase("51")
								&& !bal.trim().equalsIgnoreCase("99")) {
							if (!common.isAborted) {
								int setNoABB = VoucherCommon.getNextSetNo();
								int scrollNoAbb = VoucherCommon.getNextScrollNo();
								int scrollNoAbb1 = VoucherCommon.getNextScrollNo();
								int scrollNoAbb2 = VoucherCommon.getNextScrollNo();
								logger.error("Transaction successful");
								common.debitABB(lbrCode, MSGConstants.ABB_ACC, MSGConstants.ABB, setNo, scrollNoAbb,
										narration, crBrcode, amount, rrn, reconNo, session);
								if (!common.isAborted) {
									if (VoucherMPOS
											.updateProductBalances(amount, lbrCode, "D",
													MSGConstants.ABB_ACC.substring(0, 8).trim(), session)
											.equalsIgnoreCase(MSGConstants.SUCCESS)) {
										if (!common.isAborted) {
											logger.error("ABB Transaction successful");
											common.debit(crBrcode, drAccno, transType.trim().toUpperCase(), setNoABB,
													scrollNoAbb1, narration, amount, rrn, session);
											if (!common.isAborted) {

												if (VoucherMPOS
														.updateProductBalances(amount, crBrcode, "D",
																drAccno.substring(0, 8).trim(), session)
														.equalsIgnoreCase(MSGConstants.SUCCESS)) {
													if (!common.isAborted) {
														logger.error("Other Bank GL Transaction successful");
														common.creditABB(crBrcode, MSGConstants.ABB_ACC,
																transType.trim().toUpperCase(), setNoABB, scrollNoAbb2,
																narration, lbrCode, amount, rrn, reconNo, session);
														if (!common.isAborted) {
															if (VoucherMPOS
																	.updateProductBalances(amount, crBrcode, "C",
																			MSGConstants.ABB_ACC.substring(0, 8).trim(),
																			session)
																	.trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
																Date opdate = DataUtils.getOpenDate(crBrcode);
																String batchCodes[] = Props
																		.getBatchProperty(
																				transType.trim().toUpperCase())
																		.split("~");
																/***
																 * Added and commited by Aniket Desai on 23rd Oct, 2019
																 * for issue #48692
																 ***/
																// D001004 d04OnlineBatchName =
																// VoucherMPOS.getBatchNameFromBatchCode(batchCodes[0]);
																/*
																 * D001004 d04OnlineBatchName = VoucherMPOS
																 * .getBatchNameFromBatchCodeNew(batchCodes[0],session);
																 */
																D010004 d04OnlineBatchName = getSelectedBatchNew(
																		crBrcode, batchCodes, opdate, session);

																String onlineBatchName = d04OnlineBatchName.getId()
																		.getBatchCd().trim();

																Date openDate = getOpenDateNew(lbrCode, session);
																String batchCodes1[] = Props.getBatchProperty(
																		MSGConstants.ABB_ACC.substring(0, 8).trim())
																		.split("~");
																D010004 d04OnlineBatchName1 = getSelectedBatchNew(
																		lbrCode, batchCodes1, openDate, session);

																/***
																 * Added and commited by Aniket Desai on 23rd Oct, 2019
																 * for issue #48692
																 ***/
																// D001004 d001004 =
																// VoucherMPOS.getBatchNameFromBatchCode(batchCodes1[0]);
																// D001004 d001004 =
																// VoucherMPOS.getBatchNameFromBatchCodeNew(batchCodes1[0],session);
																String benBatchCode = d04OnlineBatchName1.getId()
																		.getBatchCd().trim();

																D100001 d100001 = VoucherMPOS.prepareReconObj(crBrcode,
																		reconNo, opdate, 999999, onlineBatchName,
																		benBatchCode, setNoABB, scrollNoAbb2, setNo,
																		scrollNo, scrollNoAbb, lbrCode, "" + accNo,
																		Double.valueOf(amount), "D");
																d100001.setToBrEntryDate(openDate);
																logger.error("d100001::>>>" + d100001);
																D100002 d100002 = VoucherMPOS.prepareRecon2Obj(crBrcode,
																		reconNo, opdate, 999999, onlineBatchName,
																		benBatchCode, setNoABB, scrollNoAbb2, setNo,
																		scrollNo, scrollNoAbb, lbrCode, "" + accNo,
																		Double.valueOf(amount), "D");
																d100002.setToBrEntryDate(openDate);
																logger.error("d100002::>>>" + d100002);
																session.save(d100001);
																session.save(d100002);
																// throw new Exception();
																t.commit();
																resultMap.put(Code.RESULT, Code.SUCCESS);
																resultMap.put("Batch", benBatchCode);
																resultMap.put(Code.SETNO, String.valueOf(setNo));
																resultMap.put(Code.SCROLLNO, String.valueOf(scrollNo));
																session.close();
																return resultMap;
															} else {
																//if (t.isActive())
																	t.rollback();
																logger.error("ABB Transaction unsuccessful");
																resultMap.put(Code.RESULT, Code.ERROR);
																resultMap.put(Code.SETNO, "");
																resultMap.put(Code.SCROLLNO, "");
																session.close();
																return resultMap;
															}
														} else {
															//if (t.isActive())
																t.rollback();
															logger.error("ABB Transaction unsuccessful");
															resultMap.put(Code.RESULT, Code.ERROR);
															resultMap.put(Code.SETNO, "");
															resultMap.put(Code.SCROLLNO, "");
															session.close();
															return resultMap;
														}
													} else {
														//if (t.isActive())
															t.rollback();
														logger.error("ABB Transaction unsuccessful");
														resultMap.put(Code.RESULT, Code.ERROR);
														resultMap.put(Code.SETNO, "");
														resultMap.put(Code.SCROLLNO, "");
														session.close();
														return resultMap;
													}
												} else {
													//if (t.isActive())
														t.rollback();
													logger.error("ABB Transaction unsuccessful");
													resultMap.put(Code.RESULT, Code.ERROR);
													resultMap.put(Code.SETNO, "");
													resultMap.put(Code.SCROLLNO, "");
													session.close();
													return resultMap;
												}
											} else {
												//if (t.isActive())
													t.rollback();
												logger.error("ABB Transaction unsuccessful");
												resultMap.put(Code.RESULT, Code.ERROR);
												resultMap.put(Code.SETNO, "");
												resultMap.put(Code.SCROLLNO, "");
												session.close();
												return resultMap;
											}
										} else {
											//if (t.isActive())
												t.rollback();
											logger.error("ABB Transaction unsuccessful");
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "");
											resultMap.put(Code.SCROLLNO, "");
											common = null;
											session.close();
											return resultMap;
										}
									} else {
										//if (t.isActive())
											t.rollback();
										logger.error("ABB Transaction unsuccessful");
										resultMap.put(Code.RESULT, Code.ERROR);
										resultMap.put(Code.SETNO, "");
										resultMap.put(Code.SCROLLNO, "");
										common = null;
										session.close();
										return resultMap;
									}
								} else {
									//if (t.isActive())
										t.rollback();
									logger.error("ABB Transaction unsuccessful");
									resultMap.put(Code.RESULT, Code.ERROR);
									resultMap.put(Code.SETNO, "");
									resultMap.put(Code.SCROLLNO, "");
									common = null;
									session.close();
									return resultMap;
								}
							} else {
								//if (t.isActive())
									t.rollback();
								logger.error("Transaction unsuccessful");
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "");
								resultMap.put(Code.SCROLLNO, "");
								common = null;
								session.close();
								return resultMap;
							}
						} else {
							//if (t.isActive())
								t.rollback();
							logger.error("Transaction unsuccessful");
							resultMap.put(Code.RESULT, Code.ERROR);
							resultMap.put(Code.SETNO, "");
							resultMap.put(Code.SCROLLNO, "");
							common = null;
							session.close();
							return resultMap;
						}
					} /*
						 * else { if(t.isActive())t.rollback();
						 * logger.error("Transaction unsuccessful"); resultMap.put(Code.RESULT,
						 * Code.ERROR); resultMap.put(Code.SETNO, ""); resultMap.put(Code.SCROLLNO, "");
						 * common = null; return resultMap; } }
						 */ else {
						//if (t.isActive())
							t.rollback();
						logger.error("Transaction unsuccessful");
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "");
						resultMap.put(Code.SCROLLNO, "");
						common = null;
						session.close();
						return resultMap;
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			//if (t.isActive())
				t.rollback();
			session.close();
			logger.error("ERROR:", e);
			resultMap.put(Code.RESULT, Code.ERROR);
			resultMap.put(Code.SETNO, "");
			resultMap.put(Code.SCROLLNO, "");
			common = null;
			throw new Exception("Waiting..... and will re-initiate");
			// return resultMap;
		} finally {
			session.close();
			session = null;
			t = null;
		}
	}

	public static HashMap<String, String> otherBankCreditVoucherEntry(int lbrCode, String accNo, String transType,
			String narration, double amount, String rrn) {
		VoucherCommon common = new VoucherCommon();
		HashMap<String, String> resultMap = new HashMap<>();
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		int setNo = VoucherCommon.getNextSetNo();
		int scrollNo = VoucherCommon.getNextScrollNo();
		String drAccno = get32DigitAcctNo(getSysParameter(MSGConstants.MBRNCRACT).trim(), 0, 0);// + "
																								// 000000000000000000000000";
		int crBrcode = Integer.valueOf(getSysParameter(MSGConstants.MBBRANCH).trim());
		int reconNo = getNextReconNo(lbrCode);

		logger.error("accNo::>>" + accNo);
		logger.error("lbrCode::>>" + lbrCode);
		common.credit(lbrCode, accNo, MSGConstants.ABB, setNo, scrollNo, narration, amount, rrn, session);
		VoucherMPOS.updateProductBalances(amount, lbrCode, "C", accNo.substring(0, 8).trim(), session);
		CoreTransactionMPOS.balance(lbrCode, accNo, amount, "C", session);
		if (!common.isAborted) {
			int setNoABB = VoucherCommon.getNextSetNo();
			int scrollNoAbb = VoucherCommon.getNextScrollNo();
			int scrollNoAbb1 = VoucherCommon.getNextScrollNo();
			int scrollNoAbb2 = VoucherCommon.getNextScrollNo();
			System.out.println("setNo::>>" + setNo);
			System.out.println("setNo::>>" + scrollNo);
			System.out.println("setNoABB::>>" + setNoABB);
			System.out.println("scrollNoAbb::>>>" + scrollNoAbb);
			System.out.println("scrollNoAbb1::>>>" + scrollNoAbb1);
			System.out.println("scrollNoAbb2::>>>" + scrollNoAbb2);
			logger.error("Transaction successful");
			common.debitABB(lbrCode, MSGConstants.ABB_ACC, MSGConstants.ABB, setNo, scrollNoAbb, narration, crBrcode,
					amount, rrn, reconNo, session);
			VoucherMPOS.updateProductBalances(amount, lbrCode, "D", MSGConstants.ABB_ACC.substring(0, 8).trim(),
					session);
			if (!common.isAborted) {
				logger.error("ABB Transaction successful");
				common.debit(crBrcode, drAccno, transType.trim().toUpperCase(), setNoABB, scrollNoAbb1, narration,
						amount, rrn, session);
				VoucherMPOS.updateProductBalances(amount, crBrcode, "D", drAccno.substring(0, 8).trim(), session);
				if (!common.isAborted) {
					logger.error("Other Bank GL Transaction successful");
					common.creditABB(crBrcode, MSGConstants.ABB_ACC, transType.trim().toUpperCase(), setNoABB,
							scrollNoAbb2, narration, lbrCode, amount, rrn, reconNo, session);
					VoucherMPOS.updateProductBalances(amount, crBrcode, "C",
							MSGConstants.ABB_ACC.substring(0, 8).trim(), session);

					Date opdate = DataUtils.getOpenDate(lbrCode);

					String batchCodes[] = Props.getBatchProperty("IMPS").split("~");
					D001004 d04OnlineBatchName = VoucherMPOS.getBatchNameFromBatchCode(batchCodes[0]);
					String onlineBatchName = d04OnlineBatchName.getValue().trim();
					String batchCodes1[] = Props.getBatchProperty("ABB").split("~");
					D001004 d001004 = VoucherMPOS.getBatchNameFromBatchCode(batchCodes1[0]);
					String benBatchCode = d001004.getValue().trim();

					D100001 d100001 = VoucherMPOS.prepareReconObj(crBrcode, reconNo, opdate, 999999, onlineBatchName,
							benBatchCode, setNoABB, scrollNoAbb2, setNo, scrollNo, scrollNoAbb, lbrCode, "" + accNo,
							Double.valueOf(amount), "D");
					System.out.println("d100001::>>>" + d100001);
					D100002 d100002 = VoucherMPOS.prepareRecon2Obj(crBrcode, reconNo, opdate, 999999, onlineBatchName,
							benBatchCode, setNoABB, scrollNoAbb2, setNo, scrollNo, scrollNoAbb, lbrCode, "" + accNo,
							Double.valueOf(amount), "D");
					System.out.println("d100002::>>>" + d100002);
					try {
						session.save(d100001);
						session.save(d100002);
						session.flush();
						t.commit();
						session.close();
						session.close();
						session = null;
						t = null;
					} catch (Exception e) {
						e.printStackTrace();

					}
					resultMap.put(Code.RESULT, Code.SUCCESS);
					resultMap.put(Code.SETNO, String.valueOf(setNo));
					resultMap.put(Code.SCROLLNO, String.valueOf(scrollNo));
					return resultMap;
				} else {
					logger.error("ABB Transaction unsuccessful");
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "");
					resultMap.put(Code.SCROLLNO, "");
					common = null;
					session.close();
					session = null;
					t = null;
					return resultMap;
				}
			} else {
				logger.error("ABB Transaction unsuccessful");
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "");
				resultMap.put(Code.SCROLLNO, "");
				common = null;

				session.close();
				session = null;
				t = null;
				return resultMap;
			}
		} else {
			logger.error("Transaction unsuccessful");
			resultMap.put(Code.RESULT, Code.ERROR);
			resultMap.put(Code.SETNO, "");
			resultMap.put(Code.SCROLLNO, "");
			common = null;
			t.commit();
			session.close();
			session = null;
			t = null;
			return resultMap;
		}
	}

	public static String getSysParameter(String code) {
		try (Session session = HBUtil.getSessionFactory().openSession()) {
			D001004Id id = new D001004Id();
			id.setCode(code.trim().toUpperCase());
			id.setLbrCode(0);
			D001004 d04 = session.get(D001004.class, id);
			session.close();
			if (d04 != null) {
				return d04.getValue();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("System Parameter Exception:", e);
		}
		return null;
	}

	public static HashMap<String, String> otherBranchVouchers(int lbrCode, String accNo, int benBrCode, String benAccNo,
			String transType, String narration, double amount, String rrn) {
		logger.error("Parametrs Received as lbrCode::>>" + lbrCode + " accNo::>>" + accNo + " benBrCode::>>>"
				+ benBrCode + " benAccNo::>>" + benAccNo + " transType::>>" + transType + " narration::>>" + narration
				+ " amount::>>" + amount + " RRN::>>" + rrn);
		System.out.println("Parametrs Received as lbrCode::>>" + lbrCode + " accNo::>>" + accNo + " benBrCode::>>>"
				+ benBrCode + " benAccNo::>>" + benAccNo + " transType::>>" + transType + " narration::>>" + narration
				+ " amount::>>" + amount + " RRN::>>" + rrn);

		VoucherCommon common = new VoucherCommon();
		HashMap<String, String> resultMap = new HashMap<>();
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		if (lbrCode == benBrCode) {
			logger.error("Same Branch Fund Transfer....");
			int setNo = VoucherCommon.getNextSetNo();
			int mainScrollNo = VoucherCommon.getNextScrollNo();
			common.debitSameBranch(lbrCode, accNo, transType.toUpperCase(), setNo, VoucherCommon.getNextScrollNo(),
					narration, amount, rrn, mainScrollNo, session);
			if (!common.isAborted) {
				if (VoucherMPOS.updateProductBalances(amount, lbrCode, "D", accNo.substring(0, 8).trim(), session)
						.equalsIgnoreCase(MSGConstants.SUCCESS)) {
					String balResponse = "";
					if (transType.contains("RD~")) {
						balResponse = CoreTransactionMPOS.balanceShivKrupa(lbrCode, accNo.trim(), amount, "D", session);
					} else
						balResponse = CoreTransactionMPOS.balance(lbrCode, accNo.trim(), amount, "D", session);
					if (balResponse != null && balResponse.trim().length() > 0
							&& !balResponse.trim().equalsIgnoreCase("99")
							&& !balResponse.trim().equalsIgnoreCase("51")) {
						if (!common.isAborted) {
							logger.error("Transaction successful");
							common.creditSameBranch(benBrCode, benAccNo, transType, setNo,
									VoucherCommon.getNextScrollNo(), narration, amount, rrn, mainScrollNo, session);
							if (!common.isAborted) {
								String bal = "";
								if (VoucherMPOS.updateProductBalances(amount, benBrCode, "C",
										benAccNo.substring(0, 8).trim(), session)
										.equalsIgnoreCase(MSGConstants.SUCCESS)) {
									if (transType.contains("RD~")) {
										bal = "01";
									} else
										bal = CoreTransactionMPOS.balance(benBrCode, benAccNo.trim(), amount, "C",
												session);
									if (bal != null && bal.trim().length() > 0 && !bal.trim().equalsIgnoreCase("51")
											&& !bal.trim().equalsIgnoreCase("99")) {
										t.commit();
										session.close();
										session = null;
										t = null;
										resultMap.put(Code.RESULT, Code.SUCCESS);
										resultMap.put(Code.SETNO, String.valueOf(setNo));
										resultMap.put(Code.SCROLLNO, String.valueOf(mainScrollNo));
										return resultMap;
									} else {
										session.close();
										session = null;
										t = null;
										logger.error("Transaction unsuccessful");
										resultMap.put(Code.RESULT, Code.ERROR);
										resultMap.put(Code.SETNO, "0");
										resultMap.put(Code.SCROLLNO, "0");
										return resultMap;
									}
								} else {
									session.close();
									session = null;
									t = null;
									logger.error("Transaction unsuccessful");
									resultMap.put(Code.RESULT, Code.ERROR);
									resultMap.put(Code.SETNO, "0");
									resultMap.put(Code.SCROLLNO, "0");
									return resultMap;
								}
							} else {
								session.close();
								session = null;
								t = null;
								logger.error("Transaction unsuccessful");
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "0");
								resultMap.put(Code.SCROLLNO, "0");
								return resultMap;
							}
						} else {
							session.close();
							session = null;
							t = null;
							logger.error("Transaction unsuccessful");
							resultMap.put(Code.RESULT, Code.ERROR);
							resultMap.put(Code.SETNO, "0");
							resultMap.put(Code.SCROLLNO, "0");
							return resultMap;
						}
					} else {
						session.close();
						session = null;
						t = null;
						logger.error("Transaction unsuccessful");
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "0");
						resultMap.put(Code.SCROLLNO, "0");
						return resultMap;
					}
				} else {
					session.close();
					session = null;
					t = null;
					logger.error("Transaction unsuccessful");
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "0");
					resultMap.put(Code.SCROLLNO, "0");
					return resultMap;

				}
			} else {
				session.close();
				session = null;
				t = null;
				logger.error("Transaction unsuccessful");
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "0");
				resultMap.put(Code.SCROLLNO, "0");
				return resultMap;
			}
		} else {
			logger.error("Other Branch Fund Transfer....");
			int setNo = VoucherCommon.getNextSetNo();
			int reconNo = VoucherCommon.getNextReconNo(lbrCode);
			int scrollNo = VoucherCommon.getNextScrollNo();

			String traType = "", rdType = "";
			if (transType.contains("~")) {
				String[] type = transType.split("~");
				rdType = type[0];
				traType = type[1];
			} else
				traType = transType;

			common.debit(lbrCode, accNo, traType.toUpperCase(), setNo, scrollNo, narration, amount, rrn, session);
			if (!common.isAborted) {
				if (VoucherMPOS.updateProductBalances(amount, lbrCode, "D", accNo.substring(0, 8).trim(), session)
						.equalsIgnoreCase(MSGConstants.SUCCESS)) {
					String balResponse = "";
					if (transType.contains("RD~"))
						balResponse = CoreTransactionMPOS.balanceShivKrupa(lbrCode, accNo, amount, "D", session);
					else
						balResponse = CoreTransactionMPOS.balance(lbrCode, accNo, amount, "D", session);
					if (balResponse != null && balResponse.trim().length() > 0 && !balResponse.equalsIgnoreCase("99")
							&& !balResponse.equalsIgnoreCase("51")) {
						if (!common.isAborted) {
							int setNoABB = VoucherCommon.getNextSetNo();
							int scrollNoAbb = VoucherCommon.getNextScrollNo();
							int scrollNoAbb1 = VoucherCommon.getNextScrollNo();
							int scrollNoAbb2 = VoucherCommon.getNextScrollNo();
							logger.error("Transaction successful");
							common.creditABB(lbrCode, MSGConstants.ABB_ACC, transType.toUpperCase(), setNo, scrollNoAbb,
									narration, benBrCode, amount, rrn, reconNo, session);
							if (!common.isAborted) {
								if (VoucherMPOS
										.updateProductBalances(amount, lbrCode, "C", MSGConstants.ABB.trim(), session)
										.equalsIgnoreCase(MSGConstants.SUCCESS)) {
									if (!common.isAborted) {
										logger.error("ABB Transaction successful");
										if ("RD".equalsIgnoreCase(rdType))
											common.credit(benBrCode, benAccNo, "RD~" + MSGConstants.ABB, setNoABB,
													scrollNoAbb1, narration, amount, rrn, session);
										else
											common.credit(benBrCode, benAccNo, MSGConstants.ABB, setNoABB, scrollNoAbb1,
													narration, amount, rrn, session);
										if (!common.isAborted) {
											String bal = "";
											if (VoucherMPOS
													.updateProductBalances(amount, benBrCode, "C",
															benAccNo.substring(0, 8).trim(), session)
													.equalsIgnoreCase(MSGConstants.SUCCESS)) {
												if ("RD".equalsIgnoreCase(rdType)) {
													bal = "01";
												} else
													bal = CoreTransactionMPOS.balance(benBrCode, benAccNo, amount, "C",
															session);
												if (bal != null && bal.trim().length() > 0
														&& !bal.trim().equalsIgnoreCase("51")
														&& !bal.trim().equalsIgnoreCase("99")) {
													if (!common.isAborted) {
														logger.error("Other Bank GL Transaction successful");
														common.debitABB(benBrCode, MSGConstants.ABB_ACC, "ABB",
																setNoABB, scrollNoAbb2, narration, lbrCode, amount, rrn,
																reconNo, session);
														if (!common.isAborted) {
															if (VoucherMPOS
																	.updateProductBalances(amount, benBrCode, "D",
																			MSGConstants.ABB_ACC.substring(0, 8).trim(),
																			session)
																	.equalsIgnoreCase(MSGConstants.SUCCESS)) {
																Date opdate = DataUtils.getOpenDate(lbrCode);
																String batchCodes[] = Props.getBatchProperty("IMPS")
																		.split("~");
																D001004 d04OnlineBatchName = VoucherMPOS
																		.getBatchNameFromBatchCode(batchCodes[0]);
																String onlineBatchName = d04OnlineBatchName.getValue()
																		.trim();
																String batchCodes1[] = Props.getBatchProperty("ABB")
																		.split("~");
																D001004 d001004 = VoucherMPOS
																		.getBatchNameFromBatchCode(batchCodes1[0]);
																String benBatchCode = d001004.getValue().trim();

																D100001 d100001 = VoucherMPOS.prepareReconObj(lbrCode,
																		reconNo, opdate, 999999, onlineBatchName,
																		benBatchCode, setNo, scrollNoAbb, setNoABB,
																		scrollNoAbb1, scrollNoAbb2, benBrCode,
																		"" + benAccNo, Double.valueOf(amount), "D");
																System.out.println("d100001::>>>" + d100001);

																D100002 d100002 = VoucherMPOS.prepareRecon2Obj(lbrCode,
																		reconNo, opdate, 999999, onlineBatchName,
																		benBatchCode, setNo, scrollNoAbb, setNoABB,
																		scrollNoAbb1, scrollNoAbb2, benBrCode,
																		"" + benAccNo, Double.valueOf(amount), "D");//
																System.out.println("d100002::>>>" + d100002);
																try {
																	session.save(d100001);
																	session.save(d100002);
																	// session.flush();
																	t.commit();
																	session.close();
																	session.close();
																	session = null;
																	t = null;
																	resultMap.put(Code.RESULT, Code.SUCCESS);
																	resultMap.put(Code.SETNO, String.valueOf(setNo));
																	resultMap.put(Code.SCROLLNO,
																			String.valueOf(scrollNo));
																	return resultMap;
																} catch (Exception e) {
																	// TODO:
																	// handle
																	// exception
																	t.rollback();
																	session.close();
																	session = null;
																	t = null;
																	e.printStackTrace();
																	logger.error(e);
																	resultMap.put(Code.RESULT, Code.ERROR);
																	resultMap.put(Code.SETNO, "");
																	resultMap.put(Code.SCROLLNO, "");
																	return resultMap;
																}

															} else {
																t.rollback();
																session.close();
																session = null;
																t = null;
																logger.error("common.isAborted::>>" + common.isAborted);
																logger.error("ABB Transaction unsuccessful");
																resultMap.put(Code.RESULT, Code.ERROR);
																resultMap.put(Code.SETNO, "");
																resultMap.put(Code.SCROLLNO, "");
																return resultMap;
															}
														} else {
															t.rollback();
															session.close();
															session = null;
															t = null;
															logger.error("common.isAborted::>>" + common.isAborted);
															logger.error("ABB Transaction unsuccessful");
															resultMap.put(Code.RESULT, Code.ERROR);
															resultMap.put(Code.SETNO, "");
															resultMap.put(Code.SCROLLNO, "");
															return resultMap;
														}
													} else {
														t.rollback();
														session.close();
														session = null;
														t = null;
														logger.error("common.isAborted::>>" + common.isAborted);
														logger.error("ABB Transaction unsuccessful");
														resultMap.put(Code.RESULT, Code.ERROR);
														resultMap.put(Code.SETNO, "");
														resultMap.put(Code.SCROLLNO, "");
														return resultMap;
													}
												} else {
													t.rollback();
													session.close();
													session = null;
													t = null;
													logger.error("common.isAborted::>>" + common.isAborted);
													logger.error("ABB Transaction unsuccessful");
													resultMap.put(Code.RESULT, Code.ERROR);
													resultMap.put(Code.SETNO, "");
													resultMap.put(Code.SCROLLNO, "");
													return resultMap;
												}
											} else {
												t.rollback();
												session.close();
												session = null;
												t = null;
												logger.error("common.isAborted::>>" + common.isAborted);
												logger.error("ABB Transaction unsuccessful");
												resultMap.put(Code.RESULT, Code.ERROR);
												resultMap.put(Code.SETNO, "");
												resultMap.put(Code.SCROLLNO, "");
												return resultMap;
											}
										} else {
											t.rollback();
											session.close();
											session = null;
											t = null;
											logger.error("common.isAborted::>>" + common.isAborted);
											logger.error("ABB Transaction unsuccessful");
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "");
											resultMap.put(Code.SCROLLNO, "");
											return resultMap;
										}
									} else {
										t.rollback();
										session.close();
										session = null;
										t = null;
										logger.error("common.isAborted::>>" + common.isAborted);
										logger.error("ABB Transaction unsuccessful");
										resultMap.put(Code.RESULT, Code.ERROR);
										resultMap.put(Code.SETNO, "");
										resultMap.put(Code.SCROLLNO, "");
										return resultMap;
									}
								} else {
									logger.error("ABB Transaction unsuccessful");
									t.rollback();
									session.close();
									session = null;
									t = null;
									resultMap.put(Code.RESULT, Code.ERROR);
									resultMap.put(Code.SETNO, "");
									resultMap.put(Code.SCROLLNO, "");
									return resultMap;
								}
							} else {
								logger.error("ABB Transaction unsuccessful");
								t.rollback();
								session.close();
								session = null;
								t = null;
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "");
								resultMap.put(Code.SCROLLNO, "");
								return resultMap;
							}
						} else {
							logger.error("Transaction unsuccessful");
							session.close();
							session = null;
							t = null;
							resultMap.put(Code.RESULT, Code.ERROR);
							resultMap.put(Code.SETNO, "");
							resultMap.put(Code.SCROLLNO, "");
							return resultMap;
						}
					} else {
						logger.error("Transaction unsuccessful");
						session.close();
						session = null;
						t = null;
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "");
						resultMap.put(Code.SCROLLNO, "");
						return resultMap;
					}
				} else {
					logger.error("Transaction unsuccessful");
					session.close();
					session = null;
					t = null;
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "");
					resultMap.put(Code.SCROLLNO, "");
					return resultMap;
				}
			} else {
				logger.error("Transaction unsuccessful");
				session.close();
				session = null;
				t = null;
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "");
				resultMap.put(Code.SCROLLNO, "");
				return resultMap;
			}
		}
	}

	public static HashMap<String, String> otherBranchVouchers(int lbrCode, String accNo, int benBrCode, String benAccNo,
			String transType, String narration, String narrationCr, double amount, String rrn, Session session)
			throws Exception {
		logger.error("Parametrs Received as lbrCode::>>" + lbrCode + " accNo::>>" + accNo + " benBrCode::>>>"
				+ benBrCode + " benAccNo::>>" + benAccNo + " transType::>>" + transType + " narration::>>" + narration
				+ " amount::>>" + amount + " RRN::>>" + rrn);
		System.out.println("Parametrs Received as lbrCode::>>" + lbrCode + " accNo::>>" + accNo + " benBrCode::>>>"
				+ benBrCode + " benAccNo::>>" + benAccNo + " transType::>>" + transType + " narration::>>" + narration
				+ " amount::>>" + amount + " RRN::>>" + rrn);
		VoucherCommon common = new VoucherCommon();
		HashMap<String, String> resultMap = new HashMap<>();
		// Transaction tx=session.beginTransaction();
		D009022 debiteAccount = session.get(D009022.class, new D009022Id(lbrCode, accNo));
		if (lbrCode == benBrCode) {
			logger.error("Same Branch Fund Transfer....");
			int setNo = VoucherCommon.getNextSetNo();
			int mainScrollNo = VoucherCommon.getNextScrollNo();
			common.debitSameBranch(lbrCode, accNo, transType.toUpperCase(), setNo, VoucherCommon.getNextScrollNo(),
					narration, amount, rrn, mainScrollNo, session);
			System.out.println("VoucherCommon.otherBranchVouchers() common.isAborted " + common.isAborted);
			if (!common.isAborted) {
				if (VoucherMPOS.updateProductBalances(amount, lbrCode, "D", accNo.substring(0, 8).trim(), session)
						.equalsIgnoreCase(MSGConstants.SUCCESS)) {
					String balResponse = CoreTransactionMPOS.balance(lbrCode, accNo.trim(), amount, "D", session);
					if (balResponse != null && balResponse.trim().length() > 0
							&& !balResponse.trim().equalsIgnoreCase("99")
							&& !balResponse.trim().equalsIgnoreCase("51")) {
						if (!common.isAborted) {
							/*** Added By Aniket Desai on 28 Aug, 2019 for Loan Changes:---Starts--- ***/
							logger.error("Transaction successful");
							logger.error("accNo::>>" + benAccNo);
							logger.error("lbrCode::>>" + benBrCode);
							D009022 creditAccount = session.get(D009022.class, new D009022Id(benBrCode, benAccNo));

							D009021 creditProductMaster = session.get(D009021.class,
									new D009021Id(benBrCode, benAccNo.substring(0, 8).trim()));
							if (RtgsNeftHostToHostConstants.LOAN.getMessage()
									.contains(creditProductMaster.getModuleType() + "")) {
								logger.error("LOAN Transaction");
								if (creditAccount.getCustNo() != debiteAccount.getCustNo()) {
									logger.error("Transaction unsuccessful " + creditAccount.getCustNo() + "!="
											+ debiteAccount.getCustNo());
									resultMap.put(Code.RESULT, Code.ERROR);
									resultMap.put(Code.SETNO, "0");
									resultMap.put(Code.SCROLLNO, "0");
									return resultMap;
								}
								LoanServiceImpl loanService = new LoanServiceImpl();
								String status = loanService.numberOfVoucherOnLoanType(creditAccount,
										creditProductMaster, benBrCode,
										creditAccount.getId().getPrdAcctId().substring(0, 8).trim(), benAccNo,
										transType, narration, amount, rrn, setNo, VoucherCommon.getNextScrollNo(),
										session);
								logger.error("Loan Transaction Status:" + status);
								if (status.equalsIgnoreCase("True"))
									common.isAborted = true;
							} else if (!RtgsNeftHostToHostConstants.LOAN.getMessage()
									.contains(creditProductMaster.getModuleType() + "")) {

								common.creditSameBranch(benBrCode, benAccNo, transType, setNo,
										VoucherCommon.getNextScrollNo(), narrationCr, amount, rrn, mainScrollNo,
										session);
							}
							/*
							 * common.creditSameBranch(benBrCode, benAccNo, transType, setNo,
							 * VoucherCommon.getNextScrollNo(), narration, amount, rrn, mainScrollNo,
							 * session);
							 */
							/*** Added By Aniket Desai on 28 Aug, 2019 for Loan Changes:---END--- ***/

							if (!common.isAborted) {

								/***
								 * Added By Aniket Desai on 3rd Sep, 2019 for Loan Voucher(Double CR To Loan
								 * Account):--Start--
								 ***/
								String bal = null;
								if (RtgsNeftHostToHostConstants.LOAN.getMessage()
										.contains(creditProductMaster.getModuleType() + ""))
									bal = amount + "";
								else if (VoucherMPOS.updateProductBalances(amount, benBrCode, "C",
										benAccNo.substring(0, 8).trim(), session)
										.equalsIgnoreCase(MSGConstants.SUCCESS))
									bal = CoreTransactionMPOS.balance(benBrCode, benAccNo.trim(), amount, "C", session);

								/*
								 * if (VoucherMPOS.updateProductBalances(amount, benBrCode, "C",
								 * benAccNo.substring(0, 8).trim(), session)
								 * .equalsIgnoreCase(MSGConstants.SUCCESS)) { String bal =
								 * CoreTransactionMPOS.balance(benBrCode, benAccNo.trim(), amount, "C",
								 * session);
								 */

								/***
								 * Added By Aniket Desai on 3rd Sep, 2019 for Loan Voucher(Double CR To Loan
								 * Account):--End--
								 ***/
								if (bal != null && bal.trim().length() > 0 && !bal.trim().equalsIgnoreCase("51")
										&& !bal.trim().equalsIgnoreCase("99")) {
									// tx.commit();
									// session.close();
									resultMap.put(Code.RESULT, Code.SUCCESS);
									resultMap.put(Code.SETNO, String.valueOf(setNo));
									resultMap.put(Code.SCROLLNO, String.valueOf(mainScrollNo));
									return resultMap;
								} /*
									 * else { logger.error("Transaction unsuccessful"); resultMap.put(Code.RESULT,
									 * Code.ERROR); resultMap.put(Code.SETNO, "0"); resultMap.put(Code.SCROLLNO,
									 * "0"); return resultMap; } }
									 */ else {
									logger.error("Transaction unsuccessful");
									// tx.rollback();
									// session.close();
									resultMap.put(Code.RESULT, Code.ERROR);
									resultMap.put(Code.SETNO, "0");
									resultMap.put(Code.SCROLLNO, "0");
									return resultMap;
								}
							} else {
								logger.error("Transaction unsuccessful");
								// tx.rollback();
								// session.close();
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "0");
								resultMap.put(Code.SCROLLNO, "0");
								return resultMap;
							}
						} else {
							logger.error("Transaction unsuccessful");
							// tx.rollback();
							// session.close();
							resultMap.put(Code.RESULT, Code.ERROR);
							resultMap.put(Code.SETNO, "0");
							resultMap.put(Code.SCROLLNO, "0");
							return resultMap;
						}
					} else {
						logger.error("Transaction unsuccessful");
						// tx.rollback();
						// session.close();
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "0");
						resultMap.put(Code.SCROLLNO, "0");
						return resultMap;
					}
				} else {
					logger.error("Transaction unsuccessful");
					// tx.rollback();
					// session.close();
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "0");
					resultMap.put(Code.SCROLLNO, "0");
					return resultMap;
				}
			} else {
				logger.error("Transaction unsuccessful");
				// tx.rollback();
				// session.close();
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "0");
				resultMap.put(Code.SCROLLNO, "0");
				return resultMap;
			}
		} else {
			logger.error("Other Branch Fund Transfer....");
			int setNo = VoucherCommon.getNextSetNo();
			int reconNo = VoucherCommon.getNextReconNo(lbrCode);
			int scrollNo = VoucherCommon.getNextScrollNo();
			common.debit(lbrCode, accNo, transType.toUpperCase(), setNo, scrollNo, narration, amount, rrn, session);
			if (!common.isAborted) {
				if (VoucherMPOS.updateProductBalances(amount, lbrCode, "D", accNo.substring(0, 8).trim(), session)
						.equalsIgnoreCase(MSGConstants.SUCCESS)) {
					String balResponse = CoreTransactionMPOS.balance(lbrCode, accNo, amount, "D", session);
					if (balResponse != null && balResponse.trim().length() > 0 && !balResponse.equalsIgnoreCase("99")
							&& !balResponse.equalsIgnoreCase("51")) {
						if (!common.isAborted) {
							int setNoABB = VoucherCommon.getNextSetNo();
							int scrollNoAbb = VoucherCommon.getNextScrollNo();
							int scrollNoAbb1 = VoucherCommon.getNextScrollNo();
							int scrollNoAbb2 = VoucherCommon.getNextScrollNo();
							logger.error("Transaction successful");
							common.creditABB(lbrCode, MSGConstants.ABB_ACC, transType.toUpperCase(), setNo, scrollNoAbb,
									narration, benBrCode, amount, rrn, reconNo, session);
							if (!common.isAborted) {
								if (VoucherMPOS
										.updateProductBalances(amount, lbrCode, "C", MSGConstants.ABB.trim(), session)
										.equalsIgnoreCase(MSGConstants.SUCCESS)) {
									if (!common.isAborted) {
										logger.error("ABB Transaction successful");
										/*** Added By Aniket Desai on 28 Aug, 2019 for Loan Changes:---Starts--- ***/
										logger.error("accNo::>>" + benAccNo);
										logger.error("lbrCode::>>" + benBrCode);
										D009022 creditAccount = session.get(D009022.class,
												new D009022Id(benBrCode, benAccNo));
										D009021 creditProductMaster = session.get(D009021.class,
												new D009021Id(benBrCode, benAccNo.substring(0, 8).trim()));
										if (RtgsNeftHostToHostConstants.LOAN.getMessage()
												.contains(creditProductMaster.getModuleType() + "")) {

											if (creditAccount.getCustNo() != debiteAccount.getCustNo()) {
												logger.error("Transaction unsuccessful " + creditAccount.getCustNo()
														+ "!=" + debiteAccount.getCustNo());
												resultMap.put(Code.RESULT, Code.ERROR);
												resultMap.put(Code.SETNO, "0");
												resultMap.put(Code.SCROLLNO, "0");
												return resultMap;
											}

											LoanServiceImpl loanService = new LoanServiceImpl();
											String status = loanService.numberOfVoucherOnLoanType(creditAccount,
													creditProductMaster, benBrCode,
													creditAccount.getId().getPrdAcctId().substring(0, 8).trim(),
													benAccNo, MSGConstants.ABB, narration, amount, rrn, setNoABB,
													scrollNoAbb1, session);
											if (status.equalsIgnoreCase("True"))
												common.isAborted = true;
										} else if (!RtgsNeftHostToHostConstants.LOAN.getMessage()
												.contains(creditProductMaster.getModuleType() + "")) {

											common.credit(benBrCode, benAccNo, MSGConstants.ABB, setNoABB, scrollNoAbb1,
													narrationCr, amount, rrn, session);
										}
										/*
										 * common.credit(benBrCode, benAccNo, MSGConstants.ABB, setNoABB, scrollNoAbb1,
										 * narration, amount, rrn, session);
										 */
										/*** Added By Aniket Desai on 28 Aug, 2019 for Loan Changes:---END--- ***/
										if (!common.isAborted) {
											/***
											 * Added By Aniket Desai on 3rd Sep, 2019 for Loan Voucher(Double CR To Loan
											 * Account):--Start--
											 ***/
											String bal = null;
											if (RtgsNeftHostToHostConstants.LOAN.getMessage()
													.contains(creditProductMaster.getModuleType() + ""))
												bal = amount + "";
											else if (VoucherMPOS
													.updateProductBalances(amount, benBrCode, "C",
															benAccNo.substring(0, 8).trim(), session)
													.equalsIgnoreCase(MSGConstants.SUCCESS))
												bal = CoreTransactionMPOS.balance(benBrCode, benAccNo.trim(), amount,
														"C", session);

											/*
											 * if (VoucherMPOS.updateProductBalances(amount, benBrCode, "C",
											 * benAccNo.substring(0, 8).trim(), session)
											 * .equalsIgnoreCase(MSGConstants.SUCCESS)) { String bal =
											 * CoreTransactionMPOS.balance(benBrCode, benAccNo, amount, "C", session);
											 */

											/***
											 * Added By Aniket Desai on 3rd Sep, 2019 for Loan Voucher(Double CR To Loan
											 * Account):--End--
											 ***/

											if (bal != null && bal.trim().length() > 0
													&& !bal.trim().equalsIgnoreCase("51")
													&& !bal.trim().equalsIgnoreCase("99")) {
												if (!common.isAborted) {
													logger.error("Other Bank GL Transaction successful");
													common.debitABB(benBrCode, MSGConstants.ABB_ACC, MSGConstants.ABB,
															setNoABB, scrollNoAbb2, narration, lbrCode, amount, rrn,
															reconNo, session);
													if (!common.isAborted) {
														if (VoucherMPOS.updateProductBalances(amount, benBrCode, "D",
																MSGConstants.ABB_ACC.substring(0, 8).trim(), session)
																.equalsIgnoreCase(MSGConstants.SUCCESS)) {
															Date opdate = DataUtils.getOpenDate(lbrCode);
															String batchCodes[] = Props
																	.getBatchProperty(transType.toUpperCase())
																	.split("~");
															D001004 d04OnlineBatchName = VoucherMPOS
																	.getBatchNameFromBatchCode(batchCodes[0]);
															String onlineBatchName = d04OnlineBatchName.getValue()
																	.trim();
															String batchCodes1[] = Props.getBatchProperty("ABB")
																	.split("~");
															D001004 d001004 = VoucherMPOS
																	.getBatchNameFromBatchCode(batchCodes1[0]);
															String benBatchCode = d001004.getValue().trim();

															D100001 d100001 = VoucherMPOS.prepareReconObj(lbrCode,
																	reconNo, opdate, 999999,
																	VoucherCommon.firstBatch.trim(),
																	VoucherCommon.secondBatch.trim(), setNo,
																	scrollNoAbb, setNoABB, scrollNoAbb1, scrollNoAbb2,
																	benBrCode, "" + benAccNo, Double.valueOf(amount),
																	"D");
															System.out.println("d100001::>>>" + d100001);

															D100002 d100002 = VoucherMPOS.prepareRecon2Obj(lbrCode,
																	reconNo, opdate, 999999, common.firstBatch.trim(),
																	common.secondBatch.trim(), setNo, scrollNoAbb,
																	setNoABB, scrollNoAbb1, scrollNoAbb2, benBrCode,
																	"" + benAccNo, Double.valueOf(amount), "D");//
															System.out.println("d100002::>>>" + d100002);
															try {
																session.save(d100001);
																session.save(d100002);
																// tx.commit();
																// session.close();
																resultMap.put(Code.RESULT, Code.SUCCESS);
																resultMap.put(Code.SETNO, String.valueOf(setNo));
																resultMap.put(Code.SCROLLNO, String.valueOf(scrollNo));
																return resultMap;
															} catch (Exception e) {
																e.printStackTrace();
																// tx.rollback();
																// session.close();
																logger.error("ERROR:", e);
																resultMap.put(Code.RESULT, Code.ERROR);
																resultMap.put(Code.SETNO, "");
																resultMap.put(Code.SCROLLNO, "");
																return resultMap;
															}
														} else {
															logger.error("common.isAborted::>>" + common.isAborted);
															logger.error("ABB Transaction unsuccessful");
															// tx.rollback();
															// session.close();
															resultMap.put(Code.RESULT, Code.ERROR);
															resultMap.put(Code.SETNO, "");
															resultMap.put(Code.SCROLLNO, "");
															return resultMap;
														}
													} else {
														logger.error("common.isAborted::>>" + common.isAborted);
														logger.error("ABB Transaction unsuccessful");
														// tx.rollback();
														// session.close();
														resultMap.put(Code.RESULT, Code.ERROR);
														resultMap.put(Code.SETNO, "");
														resultMap.put(Code.SCROLLNO, "");
														return resultMap;
													}
												} else {
													logger.error("common.isAborted::>>" + common.isAborted);
													logger.error("ABB Transaction unsuccessful");
													// tx.rollback();
													// session.close();
													resultMap.put(Code.RESULT, Code.ERROR);
													resultMap.put(Code.SETNO, "");
													resultMap.put(Code.SCROLLNO, "");
													return resultMap;
												}
											} /*
												 * else { logger.error("common.isAborted::>>" + common.isAborted);
												 * logger.error("ABB Transaction unsuccessful");
												 * resultMap.put(Code.RESULT, Code.ERROR); resultMap.put(Code.SETNO,
												 * ""); resultMap.put(Code.SCROLLNO, ""); return resultMap; } }
												 */ else {
												logger.error("common.isAborted::>>" + common.isAborted);
												logger.error("ABB Transaction unsuccessful");
												// tx.rollback();
												// session.close();
												resultMap.put(Code.RESULT, Code.ERROR);
												resultMap.put(Code.SETNO, "");
												resultMap.put(Code.SCROLLNO, "");
												return resultMap;
											}
										} else {
											logger.error("common.isAborted::>>" + common.isAborted);
											logger.error("ABB Transaction unsuccessful");
											// tx.rollback();
											// session.close();
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "");
											resultMap.put(Code.SCROLLNO, "");
											return resultMap;
										}
									} else {
										logger.error("common.isAborted::>>" + common.isAborted);
										logger.error("ABB Transaction unsuccessful");
										// tx.rollback();
										// session.close();
										resultMap.put(Code.RESULT, Code.ERROR);
										resultMap.put(Code.SETNO, "");
										resultMap.put(Code.SCROLLNO, "");
										return resultMap;
									}
								} else {
									logger.error("ABB Transaction unsuccessful");
									// tx.rollback();
									// session.close();
									resultMap.put(Code.RESULT, Code.ERROR);
									resultMap.put(Code.SETNO, "");
									resultMap.put(Code.SCROLLNO, "");
									return resultMap;
								}
							} else {
								logger.error("ABB Transaction unsuccessful");
								// tx.rollback();
								// session.close();
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "");
								resultMap.put(Code.SCROLLNO, "");
								return resultMap;
							}
						} else {
							logger.error("Transaction unsuccessful");
							// tx.rollback();
							// session.close();
							resultMap.put(Code.RESULT, Code.ERROR);
							resultMap.put(Code.SETNO, "");
							resultMap.put(Code.SCROLLNO, "");
							return resultMap;
						}
					} else {
						logger.error("Transaction unsuccessful");
						// tx.rollback();
						// session.close();
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "");
						resultMap.put(Code.SCROLLNO, "");
						return resultMap;
					}
				} else {
					logger.error("Transaction unsuccessful");
					// tx.rollback();
					// session.close();
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "");
					resultMap.put(Code.SCROLLNO, "");
					return resultMap;
				}
			} else {
				logger.error("Transaction unsuccessful");
				// tx.rollback();
				// session.close();
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "");
				resultMap.put(Code.SCROLLNO, "");
				return resultMap;
			}
		}
	}

	public static HashMap<String, String> otherBranchReversal(int lbrCode, String accNo, int benBrCode, String benAccNo,
			String transType, String narration, double amount, String rrn) {
		VoucherCommon common = new VoucherCommon();
		HashMap<String, String> resultMap = new HashMap<>();
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();

		if (lbrCode == benBrCode) {
			logger.error("Same Branch Fund Transfer....");
			int setNo = VoucherCommon.getNextSetNo();
			int mainScrollNo = VoucherCommon.getNextScrollNo();
			common.creditSameBranch(lbrCode, accNo, transType.toUpperCase(), setNo, VoucherCommon.getNextScrollNo(),
					narration, amount, rrn, mainScrollNo, session);
			if (!common.isAborted) {
				if (VoucherMPOS.updateProductBalances(amount, lbrCode, "C", accNo.substring(0, 8).trim(), session)
						.trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
					String bal = CoreTransactionMPOS.balance(lbrCode, accNo, amount, "C", session);
					if (bal != null && bal.trim().length() > 0 && !bal.equalsIgnoreCase("51")
							&& !bal.equalsIgnoreCase("99")) {
						logger.error("Transaction successful");
						common.debitSameBranch(benBrCode, benAccNo, transType, setNo, VoucherCommon.getNextScrollNo(),
								narration, amount, rrn, mainScrollNo, session);
						if (!common.isAborted) {
							if (VoucherMPOS.updateProductBalances(amount, benBrCode, "D",
									benAccNo.substring(0, 8).trim(), session).trim()
									.equalsIgnoreCase(MSGConstants.SUCCESS)) {
								String balAgent = CoreTransactionMPOS.balance(benBrCode, benAccNo, amount, "D",
										session);
								if (balAgent != null && balAgent.trim().length() < 1 && !balAgent.equalsIgnoreCase("51")
										&& !balAgent.equalsIgnoreCase("99")) {
									t.commit();
									session.close();
									session = null;
									t = null;
									resultMap.put(Code.RESULT, Code.SUCCESS);
									resultMap.put(Code.SETNO, String.valueOf(setNo));
									resultMap.put(Code.SCROLLNO, String.valueOf(mainScrollNo));
									return resultMap;
								} else {
									logger.error("Transaction unsuccessful");
									session.close();
									session = null;
									t = null;
									resultMap.put(Code.RESULT, Code.ERROR);
									resultMap.put(Code.SETNO, "");
									resultMap.put(Code.SCROLLNO, "");
									return resultMap;
								}
							} else {
								logger.error("Transaction unsuccessful");
								session.close();
								session = null;
								t = null;
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "");
								resultMap.put(Code.SCROLLNO, "");
								return resultMap;
							}
						} else {
							logger.error("Transaction unsuccessful");
							session.close();
							session = null;
							t = null;
							resultMap.put(Code.RESULT, Code.ERROR);
							resultMap.put(Code.SETNO, "");
							resultMap.put(Code.SCROLLNO, "");
							return resultMap;
						}
					} else {
						logger.error("Transaction unsuccessful");
						session.close();
						session = null;
						t = null;
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "");
						resultMap.put(Code.SCROLLNO, "");
						return resultMap;
					}

				} else {
					logger.error("Transaction unsuccessful");
					session.close();
					session = null;
					t = null;
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "");
					resultMap.put(Code.SCROLLNO, "");
					return resultMap;
				}
			} else {
				logger.error("Transaction unsuccessful");
				session.close();
				session = null;
				t = null;
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "");
				resultMap.put(Code.SCROLLNO, "");
				return resultMap;
			}
		} else {
			logger.error("Other Branch Fund Transfer....");
			int setNo = VoucherCommon.getNextSetNo();
			int reconNo = VoucherCommon.getNextReconNo(lbrCode);
			int scrollNo = VoucherCommon.getNextScrollNo();
			common.credit(lbrCode, accNo, transType.toUpperCase(), setNo, VoucherCommon.getNextScrollNo(), narration,
					amount, rrn, session);
			if (!common.isAborted) {
				if (VoucherMPOS.updateProductBalances(amount, lbrCode, "C", accNo.substring(0, 8).trim(), session)
						.trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
					String bal = CoreTransactionMPOS.balance(lbrCode, accNo, amount, "C", session);
					if (bal != null && bal.trim().length() > 0 && !bal.trim().equalsIgnoreCase("99")
							&& !bal.trim().equalsIgnoreCase("51")) {
						if (!common.isAborted) {
							int setNoABB = VoucherCommon.getNextSetNo();
							int scrollNoAbb = VoucherCommon.getNextScrollNo();
							int scrollNoAbb1 = VoucherCommon.getNextScrollNo();
							int scrollNoAbb2 = VoucherCommon.getNextScrollNo();
							logger.error("Transaction successful");
							common.debitABB(lbrCode, MSGConstants.ABB_ACC, "ABB", setNo, scrollNoAbb, narration,
									benBrCode, amount, rrn, reconNo, session);
							if (!common.isAborted) {
								if (VoucherMPOS
										.updateProductBalances(amount, lbrCode, "D",
												MSGConstants.ABB_ACC.substring(0, 8).trim(), session)
										.trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
									if (!common.isAborted) {
										logger.error("ABB Transaction successful");
										common.debit(benBrCode, benAccNo, transType, setNoABB, scrollNoAbb1, narration,
												amount, rrn, session);
										if (!common.isAborted) {
											if (VoucherMPOS
													.updateProductBalances(amount, benBrCode, "D",
															benAccNo.substring(0, 8).trim(), session)
													.trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
												String balNew = CoreTransactionMPOS
														.balance(benBrCode, benAccNo, amount, "D", session).trim();
												if (balNew != null && balNew.trim().length() > 0
														&& !balNew.trim().equalsIgnoreCase("99")
														&& !balNew.trim().equalsIgnoreCase("51")) {
													if (!common.isAborted) {
														logger.error("Other Bank GL Transaction successful");
														common.creditABB(benBrCode, MSGConstants.ABB_ACC, "ABB",
																setNoABB, scrollNoAbb2, narration, benBrCode, amount,
																rrn, reconNo, session);
														if (!common.isAborted) {
															if (VoucherMPOS
																	.updateProductBalances(amount, benBrCode, "C",
																			MSGConstants.ABB_ACC.substring(0, 8).trim(),
																			session)
																	.trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
																Date opdate = DataUtils.getOpenDate(lbrCode);
																String batchCodes[] = Props.getBatchProperty("IMPS")
																		.split("~");
																D001004 d04OnlineBatchName = VoucherMPOS
																		.getBatchNameFromBatchCode(batchCodes[0]);
																String onlineBatchName = d04OnlineBatchName.getValue()
																		.trim();
																String batchCodes1[] = Props.getBatchProperty("ABB")
																		.split("~");
																D001004 d001004 = VoucherMPOS
																		.getBatchNameFromBatchCode(batchCodes1[0]);
																String benBatchCode = d001004.getValue().trim();

																D100001 d100001 = VoucherMPOS.prepareReconObj(lbrCode,
																		reconNo, opdate, 999999, onlineBatchName,
																		benBatchCode, setNo, scrollNoAbb, setNoABB,
																		scrollNoAbb1, scrollNoAbb2, benBrCode,
																		"" + accNo, Double.valueOf(amount), "D");
																System.out.println("d100001::>>>" + d100001);

																D100002 d100002 = VoucherMPOS.prepareRecon2Obj(lbrCode,
																		reconNo, opdate, 999999, onlineBatchName,
																		benBatchCode, setNo, scrollNoAbb, setNoABB,
																		scrollNoAbb1, scrollNoAbb2, benBrCode,
																		"" + accNo, Double.valueOf(amount), "D");//
																System.out.println("d100002::>>>" + d100002);
																try {
																	session.save(d100001);
																	session.save(d100002);
																	session.flush();
																	t.commit();
																	session.close();
																	session.close();
																	session = null;
																	t = null;
																} catch (Exception e) {
																	// TODO:
																	// handle
																	// exception
																	e.printStackTrace();
																}
																resultMap.put(Code.RESULT, Code.SUCCESS);
																resultMap.put(Code.SETNO, String.valueOf(setNo));
																resultMap.put(Code.SCROLLNO, String.valueOf(scrollNo));
																return resultMap;
															} else {
																logger.error("ABB Transaction unsuccessful");
																session.close();
																session = null;
																t = null;
																resultMap.put(Code.RESULT, Code.ERROR);
																resultMap.put(Code.SETNO, "");
																resultMap.put(Code.SCROLLNO, "");
																return resultMap;
															}
														} else {
															logger.error("ABB Transaction unsuccessful");
															session.close();
															session = null;
															t = null;
															resultMap.put(Code.RESULT, Code.ERROR);
															resultMap.put(Code.SETNO, "");
															resultMap.put(Code.SCROLLNO, "");
															return resultMap;
														}
													} else {
														logger.error("ABB Transaction unsuccessful");
														session.close();
														session = null;
														t = null;
														resultMap.put(Code.RESULT, Code.ERROR);
														resultMap.put(Code.SETNO, "");
														resultMap.put(Code.SCROLLNO, "");
														return resultMap;
													}
												} else {
													logger.error("ABB Transaction unsuccessful");
													session.close();
													session = null;
													t = null;
													resultMap.put(Code.RESULT, Code.ERROR);
													resultMap.put(Code.SETNO, "");
													resultMap.put(Code.SCROLLNO, "");
													return resultMap;
												}
											} else {
												logger.error("ABB Transaction unsuccessful");
												session.close();
												session = null;
												t = null;
												resultMap.put(Code.RESULT, Code.ERROR);
												resultMap.put(Code.SETNO, "");
												resultMap.put(Code.SCROLLNO, "");
												return resultMap;

											}

										} else {
											logger.error("ABB Transaction unsuccessful");
											session.close();
											session = null;
											t = null;
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "");
											resultMap.put(Code.SCROLLNO, "");
											return resultMap;
										}

									} else {
										logger.error("ABB Transaction unsuccessful");
										session.close();
										session = null;
										t = null;
										resultMap.put(Code.RESULT, Code.ERROR);
										resultMap.put(Code.SETNO, "");
										resultMap.put(Code.SCROLLNO, "");
										return resultMap;
									}
								} else {
									logger.error("Transaction unsuccessful");
									session.close();
									session = null;
									t = null;
									resultMap.put(Code.RESULT, Code.ERROR);
									resultMap.put(Code.SETNO, "");
									resultMap.put(Code.SCROLLNO, "");
									return resultMap;
								}
							} else {
								logger.error("ABB Transaction unsuccessful");
								session.close();
								session = null;
								t = null;
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "");
								resultMap.put(Code.SCROLLNO, "");
								return resultMap;
							}
						} else {
							logger.error("Transaction unsuccessful");
							session.close();
							session = null;
							t = null;
							resultMap.put(Code.RESULT, Code.ERROR);
							resultMap.put(Code.SETNO, "");
							resultMap.put(Code.SCROLLNO, "");
							return resultMap;
						}
					} else {
						logger.error("Transaction unsuccessful");
						session.close();
						session = null;
						t = null;
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "");
						resultMap.put(Code.SCROLLNO, "");
						return resultMap;
					}
				} else {
					logger.error("Transaction unsuccessful");
					session.close();
					session = null;
					t = null;
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "");
					resultMap.put(Code.SCROLLNO, "");
					return resultMap;
				}

			} else {
				logger.error("Transaction unsuccessful");
				session.close();
				session = null;
				t = null;
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "");
				resultMap.put(Code.SCROLLNO, "");
				return resultMap;
			}

		}
	}

	public static void main(String[] args) {
		// System.out.println(getNextSetNo());
		// System.out.println(getNextScrollNo());
//		System.out.println(getNextReconNo(1));
//		System.out.println(sdf.format(new Date()).substring(0, 8));
		System.out.println("Setno::>>" + VoucherCommon.getNextSetNo());
		System.out.println("Scrollno::>>" + VoucherCommon.getNextScrollNo());

		// new VoucherCommon().balance(3, "SB 000000000000670700000000", 10,
		// "C");
		// HashMap<String, String> result2=otherBranchReversal(3, "SB
		// 000000000000670700000000", 3, "SB 000000000000274000000000",
		// "IMPS","TT", 21, "888888999999");
		// logger.error("result2::>>"+result2);
		// VoucherCommon common=new VoucherCommon();
		// HashMap<String, String> result=otherBranchVouchers(3, "SB
		// 000000000000670700000000", 9, "SB 000000000000462800000000",
		// "IMPS","TT", 21.21, DataUtils.getNextRRN());
		// HashMap<String, String> result1=otherBranchVouchers(3, "SB
		// 000000000000670700000000", 3, "SB 000000000000274000000000",
		// "IMPS","TT", 21.21, "777788555555");
		// logger.error("result::>>"+result);
		// logger.error("result1::>>"+result1);
		// String rrn=DataUtils.getNextRRN();
		// System.out.println("RRN::>>>"+rrn);
		// logger.error("Dr Voucher ::>>"+otherBankVoucherEntry(3, "SB
		// 000000000000670700000000", "IMPS", "tt", 22.2,rrn));;
		// logger.error("Cr Voucher ::>>"+otherBankReverseVoucherEntry(3, "SB
		// 000000000000670700000000", "IMPS", "tt", 21.2, "444444444444"));

		// VoucherCommon common=new VoucherCommon();
		// int setNo=common.getNextSetNo();
		// logger.error("Recon No::>>"+common.getNextReconNo(2));;

		/*
		 * VoucherCommon common=new VoucherCommon(); int setNo=common.getNextSetNo();
		 * int reconNo=common.getNextReconNo(2); common.debit(3,
		 * "SB      000000000000670700000000", "IMPS", setNo,
		 * common.getNextScrollNo(),"tt", 21.2, "111111111111"); if(!common.isAborted) {
		 * logger.error("Transaction successful"); common.creditABB(3,
		 * "ABB     0000000000000000000", "IMPS", setNo, common.getNextScrollNo(), "tt",
		 * 2, 21.2, "111111111111",reconNo);
		 * 
		 * if(!common.isAborted) { int setNoABB=common.getNextSetNo();
		 * logger.error("ABB Transaction successful"); common.credit(2,
		 * "3264    000000000000000000000000", "ABB",setNoABB ,
		 * common.getNextScrollNo(), "tt", 21.2, "111111111111"); if(!common.isAborted)
		 * { logger.error("Other Bank GL Transaction successful"); common.debitABB(2,
		 * "ABB     0000000000000000000", "ABB", setNoABB,
		 * common.getNextScrollNo(),"tt", 21.2, "111111111111",reconNo); tx.commit();
		 * }else { logger.error("ABB Transaction unsuccessful"); }
		 * 
		 * }else { logger.error("ABB Transaction unsuccessful"); common.tx.rollback(); }
		 * }else { logger.error("Transaction unsuccessful"); common.tx.rollback(); }
		 */
	}

	public static String balanceUpdate(int remBrCode, String remPrdAcctId, double amount, String type) {
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
				} else if ((remAcct.getActClrBalFcy() - remAcct.getTotalLienFcy()) >= amount) {
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
					// session.flush();
				} else {
					logger.error("Insufficient Funds ");
					// isAborted = true;
					// abortCode = "51";
					// abortReason = "Insufficient Funds";
					return "51";
				}
				return "" + remAcct.getActClrBalFcy();
			} catch (Exception e) {
				e.printStackTrace();
				// isAborted = true;
				// abortCode = "EX";
				// abortReason = "Exception in Balance Effect";
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

	public static HashMap<String, String> debitBankVoucherEntry(int lbrCode, String accNo, String transType,
			String narration, double amount, String rrn) {
		VoucherCommon common = new VoucherCommon();
		HashMap<String, String> resultMap = new HashMap<>();
		int setNo = VoucherCommon.getNextSetNo();
		int scrollNo = VoucherCommon.getNextScrollNo();
		int reconNo = VoucherCommon.getNextReconNo(lbrCode);
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		common.debit(lbrCode, accNo, transType.toUpperCase(), setNo, scrollNo, narration, amount, rrn, session);
		if (!common.isAborted) {
			if (VoucherMPOS.updateProductBalances(amount, lbrCode, "D", accNo.substring(0, 8).trim(), session)
					.equalsIgnoreCase(MSGConstants.SUCCESS)) {
				if (!common.isAborted) {
					logger.error("Transaction successful");
					int setNoABB = VoucherCommon.getNextSetNo();
					int scrollNoAbb = VoucherCommon.getNextScrollNo();
					int scrollNoAbb1 = VoucherCommon.getNextScrollNo();
					int scrollNoAbb2 = VoucherCommon.getNextScrollNo();
					common.creditABB(lbrCode, MSGConstants.ABB_ACC, transType.toUpperCase(), setNo, scrollNoAbb,
							narration, lbrCode, amount, rrn, reconNo, session);
					if (!common.isAborted) {
						if (VoucherMPOS.updateProductBalances(amount, lbrCode, "D", MSGConstants.ABB, session)
								.equalsIgnoreCase(MSGConstants.SUCCESS)) {
							if (!common.isAborted) {
								logger.error("ABB Transaction successful");
								String crAccno = String.format("%-8s",
										getSysParameter(MSGConstants.SIMPAYCRACCT).trim()) + "000000000000000000000000";
								int crBrCode = Integer.valueOf(getSysParameter(MSGConstants.SIMPAYCRBRANCH).trim());
								common.credit(crBrCode, crAccno, MSGConstants.ABB, setNoABB, scrollNoAbb1, narration,
										amount, rrn, session);
								if (!common.isAborted) {
									if (VoucherMPOS
											.updateProductBalances(amount, crBrCode, "C",
													crAccno.substring(0, 8).trim(), session)
											.equalsIgnoreCase(MSGConstants.SUCCESS)) {
										logger.error("Other Bank GL Transaction successful");
										common.debitABB(crBrCode, MSGConstants.ABB_ACC, MSGConstants.ABB, setNoABB,
												scrollNoAbb2, narration, 2, amount, rrn, reconNo, session);
										if (!common.isAborted) {
											if (VoucherMPOS
													.updateProductBalances(amount, crBrCode, "C",
															MSGConstants.ABB_ACC.substring(0, 8).trim(), session)
													.equalsIgnoreCase(MSGConstants.SUCCESS)) {
												Date opdate = DataUtils.getOpenDate(lbrCode);
												String batchCodes[] = Props.getBatchProperty(MSGConstants.SIM_SAY_PAY)
														.split("~");
												D001004 d04OnlineBatchName = VoucherMPOS
														.getBatchNameFromBatchCode(batchCodes[0]);
												String onlineBatchName = d04OnlineBatchName.getValue().trim();
												String batchCodes1[] = Props.getBatchProperty("ABB").split("~");
												D001004 d001004 = VoucherMPOS.getBatchNameFromBatchCode(batchCodes1[0]);
												String benBatchCode = d001004.getValue().trim();

												D100001 d100001 = VoucherMPOS.prepareReconObj(lbrCode, reconNo, opdate,
														999999, onlineBatchName, benBatchCode, setNo, scrollNoAbb,
														setNoABB, scrollNoAbb1, scrollNoAbb2, crBrCode, "" + crAccno,
														Double.valueOf(amount), "D");
												System.out.println("d100001::>>>" + d100001);

												D100002 d100002 = VoucherMPOS.prepareRecon2Obj(lbrCode, reconNo, opdate,
														999999, onlineBatchName, benBatchCode, setNo, scrollNoAbb,
														setNoABB, scrollNoAbb1, scrollNoAbb2, crBrCode, "" + crAccno,
														Double.valueOf(amount), "D");//
												System.out.println("d100002::>>>" + d100002);
												String bal = CoreTransactionMPOS.balance(lbrCode, accNo, amount, "D",
														session);
												if (bal != null && bal.trim().length() > 0
														&& !bal.trim().equalsIgnoreCase("99")
														&& !bal.trim().equalsIgnoreCase("51")) {
													try {
														session.save(d100001);
														session.save(d100002);
														session.flush();
														t.commit();
														session.close();
														session = null;
														t = null;
														resultMap.put(Code.RESULT, Code.SUCCESS);
														resultMap.put(Code.SETNO, String.valueOf(setNo));
														resultMap.put(Code.SCROLLNO, String.valueOf(scrollNo));
														t = null;
														return resultMap;
													} catch (Exception e) {
														e.printStackTrace();
														try {
															t.rollback();
														} catch (Exception e2) {
															e2.printStackTrace();
														}
														resultMap.put(Code.RESULT, Code.ERROR);
														resultMap.put(Code.SETNO, "");
														resultMap.put(Code.SCROLLNO, "");
														session.close();
														session = null;
														t = null;
														return resultMap;
													}
												} else {
													logger.error("ABB Transaction unsuccessful");
													resultMap.put(Code.RESULT, Code.ERROR);
													resultMap.put(Code.SETNO, "");
													resultMap.put(Code.SCROLLNO, "");
													session.close();
													session = null;
													t = null;
													return resultMap;
												}
											} else {
												logger.error("ABB Transaction unsuccessful");
												resultMap.put(Code.RESULT, Code.ERROR);
												resultMap.put(Code.SETNO, "");
												resultMap.put(Code.SCROLLNO, "");
												session.close();
												session = null;
												t = null;
												return resultMap;
											}
										} else {
											logger.error("ABB Transaction unsuccessful");
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "");
											resultMap.put(Code.SCROLLNO, "");
											session.close();
											session = null;
											t = null;
											return resultMap;
										}
									} else {
										logger.error("ABB Transaction unsuccessful");
										resultMap.put(Code.RESULT, Code.ERROR);
										resultMap.put(Code.SETNO, "");
										resultMap.put(Code.SCROLLNO, "");
										session.close();
										session = null;
										t = null;
										return resultMap;
									}
								} else {
									logger.error("ABB Transaction unsuccessful");
									resultMap.put(Code.RESULT, Code.ERROR);
									resultMap.put(Code.SETNO, "");
									resultMap.put(Code.SCROLLNO, "");
									session.close();
									session = null;
									t = null;
									return resultMap;
								}
							} else {
								logger.error("ABB Transaction unsuccessful");
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "");
								resultMap.put(Code.SCROLLNO, "");
								session.close();
								session = null;
								t = null;
								return resultMap;
							}
						} else {
							logger.error("ABB Transaction unsuccessful");
							resultMap.put(Code.RESULT, Code.ERROR);
							resultMap.put(Code.SETNO, "");
							resultMap.put(Code.SCROLLNO, "");
							session.close();
							session = null;
							t = null;
							return resultMap;
						}
					} else {
						logger.error("ABB Transaction unsuccessful");
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "");
						resultMap.put(Code.SCROLLNO, "");
						session.close();
						session = null;
						t = null;
						return resultMap;
					}
				} else {
					logger.error("Transaction unsuccessful");
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "");
					resultMap.put(Code.SCROLLNO, "");
					session.close();
					session = null;
					t = null;
					return resultMap;
				}
			} else {
				logger.error("Transaction unsuccessful");
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "");
				resultMap.put(Code.SCROLLNO, "");
				session.close();
				session = null;
				t = null;
				return resultMap;
			}
		} else {
			logger.error("Transaction unsuccessful");
			resultMap.put(Code.RESULT, Code.ERROR);
			resultMap.put(Code.SETNO, "");
			resultMap.put(Code.SCROLLNO, "");
			session.close();
			session = null;
			t = null;
			return resultMap;
		}

	}

	public static boolean updateBatchBalance(Session session, double amount, D010004 selectedBatch, int brCode,
			String type) {
		try {
			logger.error("<<:: -------------updateBatchBalance---------------::>>");
			logger.error("amount::>>" + amount);
			logger.error("selectedBatch.getId().getBatchCd()::>>" + selectedBatch.getId().getBatchCd());
			logger.error("brCode:>>" + brCode);
			logger.error("type:>>" + type);
			logger.error("selectedBatch.getId().getBatchCd()::>>" + selectedBatch.toString());
			if (type.trim().equalsIgnoreCase("D")) {

				Query query = session.createQuery(
						"UPDATE D010004 SET TotalDrVcrs=TotalDrVcrs+1, TotalDrAmtLcy = TotalDrAmtLcy +:amt WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd");
				query.setParameter("amt", amount);
				query.setParameter("lbrcode", selectedBatch.getId().getLbrCode());
				query.setParameter("entrydate", selectedBatch.getId().getEntryDate());
				query.setParameter("batchcd", selectedBatch.getId().getBatchCd());
				query.executeUpdate();

				/*
				 * selectedBatch.setTotalDrVcrs(1 + selectedBatch.getTotalDrVcrs());
				 * selectedBatch.setTotalDrAmtLcy(amount + selectedBatch.getTotalDrAmtLcy());
				 * System.out.println("d010004 DR::>>" + selectedBatch);
				 * session.update(selectedBatch);
				 */
			} else {

				Query query = session.createQuery(
						"UPDATE D010004 SET TotalCrVcrs = TotalCrVcrs+1, TotalCrAmtLcy= TotalCrAmtLcy + :amt  WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd");
				query.setParameter("amt", amount);
				query.setParameter("lbrcode", selectedBatch.getId().getLbrCode());
				query.setParameter("entrydate", selectedBatch.getId().getEntryDate());
				query.setParameter("batchcd", selectedBatch.getId().getBatchCd());
				query.executeUpdate();

				/*
				 * selectedBatch.setTotalCrVcrs(1 + selectedBatch.getTotalCrVcrs());
				 * selectedBatch.setTotalCrAmtLcy(amount + selectedBatch.getTotalCrAmtLcy());
				 * session.update(selectedBatch);
				 */
			}
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("", e);
			return false;
		}
	}

	public static HashMap<String, String> reverseBillpayment(int lbrCode, String accNo, String transType,
			String narration, double amount, String rrn) {
		VoucherCommon common = new VoucherCommon();
		HashMap<String, String> resultMap = new HashMap<>();
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		int setNo = VoucherCommon.getNextSetNo();
		int scrollNo = VoucherCommon.getNextScrollNo();
		String drAccno = get32DigitAcctNo(getSysParameter(MSGConstants.MBBBILLPAY_DR_ACC).trim(), 0, 0);// + "
																										// 000000000000000000000000";
		int crBrcode = Integer.valueOf(getSysParameter(MSGConstants.MBBRANCH).trim());
		int reconNo = getNextReconNo(crBrcode);
		logger.error("accNo::>>" + accNo);
		logger.error("lbrCode::>>" + lbrCode);
		common.credit(lbrCode, accNo, MSGConstants.ABB, setNo, scrollNo, narration, amount, rrn, session);
		if (!common.isAborted) {
			if (VoucherMPOS.updateProductBalances(amount, lbrCode, "C", accNo.substring(0, 8).trim(), session)
					.equalsIgnoreCase(MSGConstants.SUCCESS)) {
				String bal = CoreTransactionMPOS.balance(lbrCode, accNo, amount, "C", session);
				if (bal != null && bal.trim().length() > 0 && !bal.trim().equalsIgnoreCase("99")
						&& !bal.trim().equalsIgnoreCase("51")) {
					if (!common.isAborted) {
						int setNoABB = VoucherCommon.getNextSetNo();
						int scrollNoAbb = VoucherCommon.getNextScrollNo();
						int scrollNoAbb1 = VoucherCommon.getNextScrollNo();
						int scrollNoAbb2 = VoucherCommon.getNextScrollNo();
						logger.error("setNo::>>" + setNo + " scrollNo::>>" + scrollNo + " setNoABB::>>" + setNoABB
								+ " scrollNoAbb::>>>" + scrollNoAbb + " scrollNoAbb1::>>>" + scrollNoAbb1
								+ " scrollNoAbb2::>>>" + scrollNoAbb2);
						common.debitABB(lbrCode, MSGConstants.ABB_ACC, MSGConstants.ABB, setNo, scrollNoAbb, narration,
								crBrcode, amount, rrn, reconNo, session);
						if (!common.isAborted) {
							if (VoucherMPOS
									.updateProductBalances(amount, lbrCode, "D",
											MSGConstants.ABB_ACC.substring(0, 8).trim(), session)
									.trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
								if (!common.isAborted) {
									logger.error("ABB Transaction successful");
									// String
									// drAccno=getSysParameter("MBBILLPAY").trim()+"
									// 000000000000000000000000";
									common.debit(crBrcode, drAccno, transType.trim().toUpperCase(), setNoABB,
											scrollNoAbb1, narration, amount, rrn, session);
									if (!common.isAborted) {
										if (VoucherMPOS
												.updateProductBalances(amount, crBrcode, "D",
														drAccno.substring(0, 8).trim(), session)
												.equalsIgnoreCase(MSGConstants.SUCCESS)) {
											if (!common.isAborted) {
												logger.error("Other Bank GL Transaction successful");
												common.creditABB(crBrcode, MSGConstants.ABB_ACC,
														transType.trim().toUpperCase(), setNoABB, scrollNoAbb2,
														narration, lbrCode, amount, rrn, reconNo, session);
												if (!common.isAborted) {
													if (VoucherMPOS.updateProductBalances(amount, crBrcode, "C",
															MSGConstants.ABB_ACC.substring(0, 8).trim(), session).trim()
															.equalsIgnoreCase(MSGConstants.SUCCESS)) {
														Date opdate = DataUtils.getOpenDate(lbrCode);
														String batchCodes[] = Props.getBatchProperty("IMPS").split("~");
														D001004 d04OnlineBatchName = VoucherMPOS
																.getBatchNameFromBatchCode(batchCodes[0]);
														String onlineBatchName = d04OnlineBatchName.getValue().trim();
														String batchCodes1[] = Props.getBatchProperty("ABB").split("~");
														D001004 d001004 = VoucherMPOS
																.getBatchNameFromBatchCode(batchCodes1[0]);
														String benBatchCode = d001004.getValue().trim();

														D100001 d100001 = VoucherMPOS.prepareReconObj(crBrcode, reconNo,
																opdate, 999999, onlineBatchName, benBatchCode, setNoABB,
																scrollNoAbb2, setNo, scrollNo, scrollNoAbb, lbrCode,
																"" + accNo, Double.valueOf(amount), "D");
														System.out.println("d100001::>>>" + d100001);
														D100002 d100002 = VoucherMPOS.prepareRecon2Obj(crBrcode,
																reconNo, opdate, 999999, onlineBatchName, benBatchCode,
																setNoABB, scrollNoAbb2, setNo, scrollNo, scrollNoAbb,
																lbrCode, "" + accNo, Double.valueOf(amount), "D");
														System.out.println("d100002::>>>" + d100002);
														try {
															session.save(d100001);
															session.save(d100002);
															t.commit();
															session.close();
															session = null;
															t = null;
															resultMap.put(Code.RESULT, Code.SUCCESS);
															resultMap.put(Code.SETNO, String.valueOf(setNo));
															resultMap.put(Code.SCROLLNO, String.valueOf(scrollNo));
															return resultMap;
														} catch (Exception e) {
															e.printStackTrace();
															t.rollback();
															t = null;
															session = null;
															resultMap.put(Code.RESULT, Code.ERROR);
															resultMap.put(Code.SETNO, "0");
															resultMap.put(Code.SCROLLNO, "0");
															return resultMap;
														}
													} else {
														logger.error("ABB Transaction unsuccessful");
														resultMap.put(Code.RESULT, Code.ERROR);
														resultMap.put(Code.SETNO, "");
														resultMap.put(Code.SCROLLNO, "");
														common = null;
														session.close();
														session = null;
														t = null;
														return resultMap;
													}
												} else {
													logger.error("ABB Transaction unsuccessful");
													resultMap.put(Code.RESULT, Code.ERROR);
													resultMap.put(Code.SETNO, "");
													resultMap.put(Code.SCROLLNO, "");
													common = null;
													session.close();
													session = null;
													t = null;
													return resultMap;
												}
											} else {
												logger.error("ABB Transaction unsuccessful");
												resultMap.put(Code.RESULT, Code.ERROR);
												resultMap.put(Code.SETNO, "");
												resultMap.put(Code.SCROLLNO, "");
												common = null;
												session.close();
												session = null;
												t = null;
												return resultMap;
											}
										} else {
											logger.error("ABB Transaction unsuccessful");
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "");
											resultMap.put(Code.SCROLLNO, "");
											common = null;
											session.close();
											session = null;
											t = null;
											return resultMap;
										}
									} else {
										logger.error("ABB Transaction unsuccessful");
										resultMap.put(Code.RESULT, Code.ERROR);
										resultMap.put(Code.SETNO, "");
										resultMap.put(Code.SCROLLNO, "");
										common = null;
										session.close();
										session = null;
										t = null;
										return resultMap;
									}
								} else {
									logger.error("ABB Transaction unsuccessful");
									resultMap.put(Code.RESULT, Code.ERROR);
									resultMap.put(Code.SETNO, "");
									resultMap.put(Code.SCROLLNO, "");
									common = null;
									t.commit();
									session.close();
									session = null;
									t = null;
									return resultMap;
								}
							} else {
								logger.error("ABB Transaction unsuccessful");
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "");
								resultMap.put(Code.SCROLLNO, "");
								common = null;
								t.commit();
								session.close();
								session = null;
								t = null;
								return resultMap;
							}

						} else {
							logger.error("ABB Transaction unsuccessful");
							resultMap.put(Code.RESULT, Code.ERROR);
							resultMap.put(Code.SETNO, "");
							resultMap.put(Code.SCROLLNO, "");
							common = null;
							t.commit();
							session.close();
							session = null;
							t = null;
							return resultMap;
						}

					} else {
						logger.error("Transaction unsuccessful");
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "");
						resultMap.put(Code.SCROLLNO, "");
						common = null;
						t.commit();
						session.close();
						session = null;
						t = null;
						return resultMap;
					}
				} else {
					logger.error("Transaction unsuccessful");
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "");
					resultMap.put(Code.SCROLLNO, "");
					common = null;
					t.commit();
					session.close();
					session = null;
					t = null;
					return resultMap;
				}

			} else {
				logger.error("Transaction unsuccessful");
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "");
				resultMap.put(Code.SCROLLNO, "");
				common = null;
				t.commit();
				session.close();
				session = null;
				t = null;
				return resultMap;
			}
		} else {
			logger.error("Transaction unsuccessful");
			resultMap.put(Code.RESULT, Code.ERROR);
			resultMap.put(Code.SETNO, "");
			resultMap.put(Code.SCROLLNO, "");
			common = null;
			t.commit();
			session.close();
			session = null;
			t = null;
			return resultMap;
		}
	}

	public static HashMap<String, String> prepaidCardVoucherEntry(int lbrCode, String accNo, String transType,
			String narration, double amount, String rrn) {
		VoucherCommon common = new VoucherCommon();
		HashMap<String, String> resultMap = new HashMap<>();
		int setNo = VoucherCommon.getNextSetNo();
		int scrollNo = VoucherCommon.getNextScrollNo();
		int reconNo = VoucherCommon.getNextReconNo(lbrCode);
		double cgstAmt = amount
				* (Double.valueOf(ConfigurationLoader.getParameters(false).getProperty("CGST_PER").trim())) / 100;
		double sgstAmt = amount
				* (Double.valueOf(ConfigurationLoader.getParameters(false).getProperty("SGST_PER").trim())) / 100;
		double igstAmt = amount
				* (Double.valueOf(ConfigurationLoader.getParameters(false).getProperty("IGST_PER").trim())) / 100;
		double chgAmt = Double.valueOf(ConfigurationLoader.getParameters(false).getProperty("CHG_AMOUNT").trim());
		double finalAmount = 0;
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		try {
			common.debit(lbrCode, accNo, transType.toUpperCase(), setNo, scrollNo, narration, amount, rrn, session);
			if (!common.isAborted) {
				if (VoucherMPOS.updateProductBalances(amount, lbrCode, "D", accNo.substring(0, 8).trim(), session)
						.equalsIgnoreCase(MSGConstants.SUCCESS)) {
					if (ConfigurationLoader.getParameters(false).getProperty("CGST_FLAG").equalsIgnoreCase("Y")) {
						finalAmount = finalAmount + cgstAmt;
						common.debitCreditGST(lbrCode,
								String.format("%-8s", MSGConstants.CGST) + "000000000000000000000000", transType, setNo,
								VoucherCommon.getNextScrollNo(), narration, cgstAmt, rrn, session,
								MSGConstants.CGST_CASH_FLOW_CR, MSGConstants.CR);
						if (!common.isAborted) {
							String response = VoucherMPOS.updateProductBalances(cgstAmt, lbrCode, "C",
									MSGConstants.CGST, session);
							logger.error("response::>>" + response);
							if (response.equalsIgnoreCase(MSGConstants.SUCCESS))
								flag = MSGConstants.YES;
							else {
								logger.error("Product 'CGST' of branch" + lbrCode + "balance upddation fails");
								flag = MSGConstants.NO;
							}
						} else
							flag = MSGConstants.NO;
					}
					if (flag.equalsIgnoreCase(MSGConstants.YES)) {
						if (ConfigurationLoader.getParameters(false).getProperty("SGST_FLAG").equalsIgnoreCase("Y")) {
							finalAmount = finalAmount + sgstAmt;
							common.debitCreditGST(lbrCode,
									String.format("%-8s", MSGConstants.SGST) + "000000000000000000000000", transType,
									setNo, VoucherCommon.getNextScrollNo(), narration, sgstAmt, rrn, session,
									MSGConstants.SGST_CASH_FLOW_CR, MSGConstants.CR);
							if (!common.isAborted) {
								String response = VoucherMPOS.updateProductBalances(sgstAmt, lbrCode, "C",
										MSGConstants.SGST, session);
								logger.error("response::>>" + response);
								if (response.equalsIgnoreCase(MSGConstants.SUCCESS))
									flag = MSGConstants.YES;
								else {
									logger.error("Product 'SGST' of branch" + lbrCode + "balance upddation fails");
									flag = MSGConstants.NO;
								}
							} else
								flag = MSGConstants.NO;
						}
					}
					if (flag.equalsIgnoreCase(MSGConstants.YES)) {
						if (ConfigurationLoader.getParameters(false).getProperty("IGST_FLAG").equalsIgnoreCase("Y")) {
							finalAmount = finalAmount + igstAmt;
							common.debitCreditGST(lbrCode,
									String.format("%-8s", MSGConstants.IGST) + "000000000000000000000000", transType,
									setNo, VoucherCommon.getNextScrollNo(), narration, igstAmt, rrn, session,
									MSGConstants.IGST_CASH_FLOW_CR, MSGConstants.CR);
							if (!common.isAborted) {
								String response = VoucherMPOS.updateProductBalances(igstAmt, lbrCode, "C",
										MSGConstants.IGST, session);
								logger.error("response::>>" + response);
								if (response.equalsIgnoreCase(MSGConstants.SUCCESS))
									flag = MSGConstants.YES;
								else {
									logger.error("Product 'IGST' of branch" + lbrCode + "balance upddation fails");
									flag = MSGConstants.NO;
								}
							} else
								flag = MSGConstants.NO;
						}
					}
					if (flag.equalsIgnoreCase(MSGConstants.YES)) {
						if (ConfigurationLoader.getParameters(false).getProperty("CHG_FLAG").equalsIgnoreCase("Y")) {
							finalAmount = finalAmount + chgAmt;
							common.credit(lbrCode, MSGConstants.PREPAID_LOAD_BALANCE_CHARGES_ACCOUNT, transType, setNo,
									VoucherCommon.getNextScrollNo(), narration, chgAmt, rrn, session);
							if (!common.isAborted) {
								String response = VoucherMPOS.updateProductBalances(chgAmt, lbrCode, "C",
										MSGConstants.PREPAID_LOAD_BALANCE_CHARGES_ACCOUNT.substring(0, 8).trim(),
										session);
								logger.error("response::>>" + response);
								if (response.equalsIgnoreCase(MSGConstants.SUCCESS))
									flag = MSGConstants.YES;
								else {
									logger.error("Product 'PL' of branch" + lbrCode + "balance upddation fails");
									flag = MSGConstants.NO;
								}
							} else
								flag = MSGConstants.NO;
						}
					}
					if (flag.equalsIgnoreCase(MSGConstants.YES)) {
						finalAmount = finalAmount + amount;
						System.out.println("finalAmount to be debited::>>" + finalAmount);
						System.out.println("FLAG::>>" + flag);
						logger.error("finalAmount to be debited::>>" + finalAmount);
						if (!common.isAborted && flag.equalsIgnoreCase(MSGConstants.YES)) {
							logger.error("Transaction successful");
							int setNoABB = VoucherCommon.getNextSetNo();
							int scrollNoAbb = VoucherCommon.getNextScrollNo();
							int scrollNoAbb1 = VoucherCommon.getNextScrollNo();
							int scrollNoAbb2 = VoucherCommon.getNextScrollNo();
							common.creditABB(lbrCode, MSGConstants.ABB_ACC, transType.toUpperCase(), setNo, scrollNoAbb,
									narration, lbrCode, amount, rrn, reconNo, session);
							if (!common.isAborted) {
								if (VoucherMPOS
										.updateProductBalances(amount, lbrCode, "C",
												MSGConstants.ABB_ACC.substring(0, 8).trim(), session)
										.equalsIgnoreCase(MSGConstants.SUCCESS)) {
									if (!common.isAborted) {
										logger.error("ABB Transaction successful");
										String crAccno = get32DigitAcctNo(
												getSysParameter(MSGConstants.PREPAID_CARD_ACC).trim(), 0, 0);//
										// + " 000000000000000000000000";
										int crBrCode = Integer.valueOf(getSysParameter(MSGConstants.MBBRANCH).trim());
										common.credit(crBrCode, crAccno, MSGConstants.ABB, setNoABB, scrollNoAbb1,
												narration, amount, rrn, session);
										if (!common.isAborted) {
											if (VoucherMPOS
													.updateProductBalances(amount, crBrCode, "C",
															crAccno.substring(0, 8).trim(), session)
													.equalsIgnoreCase(MSGConstants.SUCCESS)) {
												if (!common.isAborted) {
													logger.error("Other Bank GL Transaction successful");
													common.debitABB(crBrCode, MSGConstants.ABB_ACC, MSGConstants.ABB,
															setNoABB, scrollNoAbb2, narration, 2, amount, rrn, reconNo,
															session);
													if (!common.isAborted) {
														if (VoucherMPOS.updateProductBalances(amount, crBrCode, "D",
																MSGConstants.ABB_ACC.substring(0, 8).trim(), session)
																.trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
															Date opdate = DataUtils.getOpenDate(lbrCode);
															String batchCodes[] = Props
																	.getBatchProperty(transType.trim().toUpperCase())
																	.split("~");
															D001004 d04OnlineBatchName = VoucherMPOS
																	.getBatchNameFromBatchCode(batchCodes[0]);
															String onlineBatchName = d04OnlineBatchName.getValue()
																	.trim();
															String batchCodes1[] = Props.getBatchProperty("ABB")
																	.split("~");
															D001004 d001004 = VoucherMPOS
																	.getBatchNameFromBatchCode(batchCodes1[0]);
															String benBatchCode = d001004.getValue().trim();

															D100001 d100001 = VoucherMPOS.prepareReconObj(lbrCode,
																	reconNo, opdate, 999999, onlineBatchName,
																	benBatchCode, setNo, scrollNoAbb, setNoABB,
																	scrollNoAbb1, scrollNoAbb2, crBrCode, "" + crAccno,
																	Double.valueOf(amount), "D");
															System.out.println("d100001::>>>" + d100001);

															D100002 d100002 = VoucherMPOS.prepareRecon2Obj(lbrCode,
																	reconNo, opdate, 999999, onlineBatchName,
																	benBatchCode, setNo, scrollNoAbb, setNoABB,
																	scrollNoAbb1, scrollNoAbb2, crBrCode, "" + crAccno,
																	Double.valueOf(amount), "D");//
															System.out.println("d100002::>>>" + d100002);
															String bal = CoreTransactionMPOS.balance(lbrCode, accNo,
																	finalAmount, "D", session);
															if (bal != null && bal.trim().length() > 0
																	&& !bal.equalsIgnoreCase("51")
																	&& !bal.equalsIgnoreCase("99")) {
																String plAccBal = CoreTransactionMPOS.balance(lbrCode,
																		MSGConstants.PREPAID_LOAD_BALANCE_CHARGES_ACCOUNT,
																		finalAmount, "C", session);
																if (plAccBal != null && plAccBal.trim().length() > 0
																		&& !plAccBal.equalsIgnoreCase("51")
																		&& !plAccBal.equalsIgnoreCase("99")) {
																	session.save(d100001);
																	session.save(d100002);
																	session.flush();
																	t.commit();
																	resultMap.put(Code.RESULT, Code.SUCCESS);
																	resultMap.put(Code.SETNO, String.valueOf(setNo));
																	resultMap.put(Code.SCROLLNO,
																			String.valueOf(scrollNo));
																	return resultMap;
																} else {
																	logger.error("PL Account Balance Updation Fail");
																	resultMap.put(Code.RESULT, Code.ERROR);
																	resultMap.put(Code.SETNO, "");
																	resultMap.put(Code.SCROLLNO, "");
																	return resultMap;
																}
															} else {
																logger.error("Customer Account Balance Updation fail");
																resultMap.put(Code.RESULT, Code.ERROR);
																resultMap.put(Code.SETNO, "");
																resultMap.put(Code.SCROLLNO, "");
																return resultMap;
															}
														} else {
															logger.error("ABB Account Product balance Updation Fails.");
															resultMap.put(Code.RESULT, Code.ERROR);
															resultMap.put(Code.SETNO, "");
															resultMap.put(Code.SCROLLNO, "");
															return resultMap;
														}
													} else {
														logger.error("During Debit ABB Transaction fails");
														resultMap.put(Code.RESULT, Code.ERROR);
														resultMap.put(Code.SETNO, "");
														resultMap.put(Code.SCROLLNO, "");
														return resultMap;
													}
												} else {
													logger.error("Credit Account product balance updation Fails");
													resultMap.put(Code.RESULT, Code.ERROR);
													resultMap.put(Code.SETNO, "");
													resultMap.put(Code.SCROLLNO, "");
													return resultMap;
												}
											} else {
												logger.error("Product updation Fails");
												resultMap.put(Code.RESULT, Code.ERROR);
												resultMap.put(Code.SETNO, "");
												resultMap.put(Code.SCROLLNO, "");
												return resultMap;
											}
										} else {
											logger.error("Credit Transaction fails");
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "");
											resultMap.put(Code.SCROLLNO, "");
											return resultMap;
										}
									} else {
										logger.error("product updation fails");
										resultMap.put(Code.RESULT, Code.ERROR);
										resultMap.put(Code.SETNO, "");
										resultMap.put(Code.SCROLLNO, "");
										return resultMap;
									}
								} else {
									logger.error("product balance updation fails");
									resultMap.put(Code.RESULT, Code.ERROR);
									resultMap.put(Code.SETNO, "");
									resultMap.put(Code.SCROLLNO, "");
									return resultMap;
								}
							} else {
								logger.error("Credit ABB Transaction unsuccessful");
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "");
								resultMap.put(Code.SCROLLNO, "");
								return resultMap;
							}
						} else {
							logger.error("debit Transaction fail");
							resultMap.put(Code.RESULT, Code.ERROR);
							resultMap.put(Code.SETNO, "");
							resultMap.put(Code.SCROLLNO, "");
							return resultMap;
						}
					} else {
						logger.error("Transaction unsuccessful");
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "");
						resultMap.put(Code.SCROLLNO, "");
						return resultMap;
					}
				} else {
					logger.error("Transaction unsuccessful");
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "");
					resultMap.put(Code.SCROLLNO, "");
					return resultMap;
				}
			} else {
				logger.error("Transaction unsuccessful");
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "");
				resultMap.put(Code.SCROLLNO, "");
				return resultMap;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("ERROR:", e);
			resultMap.put(Code.RESULT, Code.ERROR);
			resultMap.put(Code.SETNO, "");
			resultMap.put(Code.SCROLLNO, "");
			return resultMap;
		} finally {
			session.close();
			session = null;
			t = null;
		}
	}

	public static HashMap<String, String> otherBranchLoanVouchers(int lbrCode, String accNo, int benBrCode,
			String benAccNo, String transType, String narration, double amount, String rrn, String benf15DigitAcctNo) {
		logger.error("<<<<< ============otherBranchLoanVouchers.service============ >>>>>>");
		VoucherCommon common = new VoucherCommon();
		HashMap<String, String> resultMap = new HashMap<>();
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		try {
			if (lbrCode == benBrCode) {
				Double mainBalance = 0.0, osBalance = 0.0;
				logger.error("Same Branch Fund Transfer....");
				int setNo = VoucherCommon.getNextSetNo();
				int mainScrollNo = VoucherCommon.getNextScrollNo();
				common.debitSameBranch(lbrCode, accNo, transType.trim().toUpperCase(), setNo, mainScrollNo,
						narration + "/Loan/DR/" + rrn + "/" + benf15DigitAcctNo, amount, rrn, mainScrollNo, session);
				if (!common.isAborted) {
					if (VoucherMPOS.updateProductBalances(amount, lbrCode, "D", accNo.substring(0, 8).trim(), session)
							.equalsIgnoreCase(MSGConstants.SUCCESS)) {
						String balResponse = CoreTransactionMPOS.balanceShivKrupa(lbrCode, accNo.trim(), amount, "D",
								session);
						if (balResponse != null && balResponse.trim().length() > 0
								&& !balResponse.trim().equalsIgnoreCase("99")
								&& !balResponse.trim().equalsIgnoreCase("51")) {
							if (!common.isAborted) {
								logger.error("Transaction successful");
								if (DataUtils.isLoanAccount(benBrCode, benAccNo.substring(0, 8).trim(), session)) {
									// ====================LOAN Account Voucher ==============================
									D030003 d030003 = DataUtils.getLoanCharges(benBrCode, benAccNo, session);
									if (d030003 == null) {
										if (t.isActive())
											t.rollback();
										logger.error("Loan Parameters not found in D030003");
										resultMap.put(Code.RESULT, Code.ERROR);
										resultMap.put(Code.SETNO, "0");
										resultMap.put(Code.SCROLLNO, "0");
										return resultMap;
									} else {
										Date openDate = getOpenDateNew(lbrCode, session);
										mainBalance = d030003.getMainBalFcy();
										osBalance = (mainBalance * -1)
												+ (d030003.getIntPrvdFcy() - d030003.getIntPaidFcy())
												+ (d030003.getOthChgPrvdFcy() - d030003.getOthChgPaidFcy())
												+ (d030003.getPenalPrvdFcy() - d030003.getPenalPaidFcy())
												+ (d030003.getTaxPrvdFcy() - d030003.getTaxPaidFcy());

										if (openDate == null) {
											if (t.isActive())
												t.rollback();
											logger.error("Open Date Not Found. Aborting Transaction");
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "0");
											resultMap.put(Code.SCROLLNO, "0");
											return resultMap;
										}
										String batchCode = Props.getBatchProperty(transType);
										if (batchCode == null) {
											if (t.isActive())
												t.rollback();
											logger.error(transType + " parameter not found in batch.properties");
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "0");
											resultMap.put(Code.SCROLLNO, "0");
											return resultMap;
										}
										logger.error("Batch Code Form Properties File : " + batchCode);
										String batchCodes[] = batchCode.split("~");
										logger.error("Number of Batches is " + batchCodes.length + "\t Names are "
												+ Arrays.toString(batchCodes));
										if (batchCodes == null || batchCodes.length < 1) {
											if (t.isActive())
												t.rollback();
											logger.error("Batch Codes Not Found in Properties File.");
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "0");
											resultMap.put(Code.SCROLLNO, "0");
											return resultMap;
										}
										D010004 selectedBatch = getSelectedBatchNew(lbrCode, batchCodes, openDate,
												session);
										if (selectedBatch == null) {
											if (t.isActive())
												t.rollback();
											logger.error("No Active Batch Found.");
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "0");
											resultMap.put(Code.SCROLLNO, "0");
											return resultMap;
										}
										/*
										 * D001004 d04OnlineBatchName = VoucherMPOS
										 * .getBatchNameFromBatchCodeNew(batchCodes[0], session);
										 * if(d04OnlineBatchName==null) { if(t.isActive())t.rollback();
										 * logger.error("System parameters not set properly");
										 * resultMap.put(Code.RESULT, Code.ERROR); resultMap.put(Code.SETNO, "0");
										 * resultMap.put(Code.SCROLLNO, "0"); return resultMap; } String onlineBatchName
										 * = d04OnlineBatchName.getValue().trim(); if(onlineBatchName==null) {
										 * if(t.isActive())t.rollback();
										 * logger.error("System parameters value is null or blank");
										 * resultMap.put(Code.RESULT, Code.ERROR); resultMap.put(Code.SETNO, "0");
										 * resultMap.put(Code.SCROLLNO, "0"); return resultMap; } D010004 onlineBatch =
										 * getD010004(lbrCode, onlineBatchName, openDate); if(onlineBatch==null) {
										 * if(t.isActive())t.rollback();
										 * logger.error("online batch not found in batch master.");
										 * resultMap.put(Code.RESULT, Code.ERROR); resultMap.put(Code.SETNO, "0");
										 * resultMap.put(Code.SCROLLNO, "0"); return resultMap; }
										 */
										amt = amount;
										logger.error("Starting Amount::>>" + amt);
										if (d030003.getOthChgPrvdFcy() > d030003.getOthChgPaidFcy() && amt > 0) {
											// && amt > d030003.getOthChgPrvdFcy() - d030003.getOthChgPaidFcy()) {
											Double vrAmt = 0.0;
											if (amt > d030003.getOthChgPrvdFcy() - d030003.getOthChgPaidFcy()) {
												amt = amt - (d030003.getOthChgPrvdFcy() - d030003.getOthChgPaidFcy());
												vrAmt = d030003.getOthChgPrvdFcy() - d030003.getOthChgPaidFcy();
											} else {

												vrAmt = amt;
												amt = 0.0;
											}

											D009040 d009040LnoCH = prepareD009040LoanObject(selectedBatch,
													selectedBatch, benBrCode, setNo, VoucherCommon.getNextScrollNo(),
													mainScrollNo, benAccNo, session, vrAmt, narration, MSGConstants.CR,
													rrn, MSGConstants.LNOCHCR, MSGConstants.LOAN_ACTIVITY,
													MSGConstants.LOAN_TYPE_1);
											// if(d009040LnoCH.getVcrAcctId().substring(0,
											// 6).trim().equalsIgnoreCase(""))
											d009040LnoCH.setVcrAcctId(d009040LnoCH.getMainAcctId());
											session.save(d009040LnoCH);
											osBalance = osBalance - vrAmt;
											D030003 d0300003 = updateBal(benAccNo, benBrCode, session,
													MSGConstants.LOAN_TYPE_1, vrAmt);
											if (d0300003 != null)
												session.update(d0300003);
											logger.error("OthChg Voucher::>>" + (vrAmt));
											logger.error("Left Amount::>>" + amt);
										}
										if (d030003.getPenalPrvdFcy() > d030003.getPenalPaidFcy() && amt > 0) {
											// && amt > d030003.getPenalPrvdFcy() - d030003.getPenalPaidFcy()) {

											Double vrAmt = 0.0;
											if (amt > d030003.getPenalPrvdFcy() - d030003.getPenalPaidFcy()) {
												amt = amt - (d030003.getPenalPrvdFcy() - d030003.getPenalPaidFcy());
												vrAmt = d030003.getPenalPrvdFcy() - d030003.getPenalPaidFcy();
											} else {

												vrAmt = amt;
												amt = 0.0;
											}

											D009040 d009040LnoCH = prepareD009040LoanObject(selectedBatch,
													selectedBatch, lbrCode, setNo, VoucherCommon.getNextScrollNo(),
													mainScrollNo, benAccNo, session, vrAmt, narration, MSGConstants.CR,
													rrn, MSGConstants.LNPINTCR, MSGConstants.LOAN_ACTIVITY,
													MSGConstants.LOAN_TYPE_3);

											if (d009040LnoCH.getVcrAcctId().substring(0, 6).trim()
													.equalsIgnoreCase("")) {
												if (t.isActive())
													t.rollback();
												logger.error("Account Not found for Penal");
												resultMap.put(Code.RESULT, Code.ERROR);
												resultMap.put(Code.SETNO, "0");
												resultMap.put(Code.SCROLLNO, "0");
												return resultMap;
											}

											session.save(d009040LnoCH);
											osBalance = osBalance - vrAmt;
											D030003 d0300003 = updateBal(benAccNo, benBrCode, session,
													MSGConstants.LOAN_TYPE_3, vrAmt);
											if (d0300003 != null)
												session.update(d0300003);
											logger.error("PenalPrvd Voucher::>>" + vrAmt);
											logger.error("Left Amount::>>" + amt);
										}
										if (d030003.getTaxPrvdFcy() > d030003.getTaxPaidFcy() && amt > 0) {
											// && amt > d030003.getTaxPrvdFcy() - d030003.getTaxPaidFcy()) {

											Double vrAmt = 0.0;
											if (amt > d030003.getTaxPrvdFcy() - d030003.getTaxPaidFcy()) {
												amt = amt - (d030003.getTaxPrvdFcy() - d030003.getTaxPaidFcy());
												vrAmt = d030003.getTaxPrvdFcy() - d030003.getTaxPaidFcy();
											} else {

												vrAmt = amt;
												amt = 0.0;
											}

											D009040 d009040LnoCH = prepareD009040LoanObject(selectedBatch,
													selectedBatch, benBrCode, setNo, VoucherCommon.getNextScrollNo(),
													mainScrollNo, benAccNo, session, vrAmt, narration, MSGConstants.CR,
													rrn, MSGConstants.LNTAXCR, MSGConstants.LOAN_ACTIVITY,
													MSGConstants.LOAN_TYPE_2);

											if (d009040LnoCH.getVcrAcctId().substring(0, 6).trim().equalsIgnoreCase(""))
												d009040LnoCH.setVcrAcctId(d009040LnoCH.getMainAcctId());

											session.save(d009040LnoCH);
											osBalance = osBalance - vrAmt;
											D030003 d0300003 = updateBal(benAccNo, benBrCode, session,
													MSGConstants.LOAN_TYPE_2, vrAmt);
											if (d0300003 != null)
												session.update(d0300003);
											logger.error("TaxPrvd Voucher::>>" + vrAmt);
											logger.error("Left Amount::>>" + amt);
										}
										if (d030003.getIntPrvdFcy() > d030003.getIntPaidFcy() && amt > 0) {
											// && amt > d030003.getIntPrvdFcy() - d030003.getIntPaidFcy()) {

											Double vrAmt = 0.0;
											if (amt > (d030003.getIntPrvdFcy() - d030003.getIntPaidFcy())) {
												amt = amt - (d030003.getIntPrvdFcy() - d030003.getIntPaidFcy());
												vrAmt = d030003.getIntPrvdFcy() - d030003.getIntPaidFcy();
											} else {

												vrAmt = amt;
												amt = 0.0;
											}

											D009040 d009040LnoCH = prepareD009040LoanObject(selectedBatch,
													selectedBatch, benBrCode, setNo, VoucherCommon.getNextScrollNo(),
													mainScrollNo, benAccNo, session, vrAmt, narration, MSGConstants.CR,
													rrn, "LNINTCR", MSGConstants.LOAN_ACTIVITY,
													MSGConstants.LOAN_TYPE_2);

											if (d009040LnoCH.getVcrAcctId().substring(0, 6).trim().equalsIgnoreCase(""))
												d009040LnoCH.setVcrAcctId(d009040LnoCH.getMainAcctId());

											session.save(d009040LnoCH);
											osBalance = osBalance - vrAmt;
											D030003 d0300003 = updateBal(benAccNo, benBrCode, session, "5", vrAmt);
											if (d0300003 != null) {
												session.update(d0300003);

											}
											logger.error("IntPrvd Voucher::>>"
													+ (d030003.getIntPrvdFcy() - d030003.getIntPaidFcy()));
											logger.error("Left Amount::>>" + amt);
										}
										if (amt > 0) {

											if ((mainBalance + amt) > 0 || mainBalance > 0) {
												if (t.isActive())
													t.rollback();
												logger.error("Loan Amount in Positive");
												resultMap.put("ErrorMsg",
														"Deposit Amount is greater than Loan Amount. Contact Branch");
												resultMap.put(Code.RESULT, Code.ERROR);
												resultMap.put(Code.SETNO, "0");
												resultMap.put(Code.SCROLLNO, "0");
												return resultMap;
											} else {
												D009040 d009040LnoCH = prepareD009040LoanObject(selectedBatch,
														selectedBatch, benBrCode, setNo,
														VoucherCommon.getNextScrollNo(), mainScrollNo, benAccNo,
														session, amt, narration, MSGConstants.CR, rrn,
														MSGConstants.LNPCR, MSGConstants.LOAN_ACTIVITY,
														MSGConstants.LOAN_TYPE_4);

												session.save(d009040LnoCH);
												D030003 d0300003 = updateBal(benAccNo, benBrCode, session, "4", amt);
												if (d0300003 != null) {

													session.update(d0300003);
													mainBalance = mainBalance + amt;
													osBalance = osBalance - amt;
												}
											}
										}
										logger.error("Left Amount::>>" + amt);
									}
								}
								if (!common.isAborted) {
									if (VoucherMPOS
											.updateProductBalances(amount, benBrCode, "C",
													benAccNo.substring(0, 8).trim(), session)
											.equalsIgnoreCase(MSGConstants.SUCCESS)) {
										String bal = CoreTransactionMPOS.balanceShivKrupa(benBrCode, benAccNo.trim(),
												amount, "C", session);
										if (bal != null && bal.trim().length() > 0 && !bal.trim().equalsIgnoreCase("51")
												&& !bal.trim().equalsIgnoreCase("99")) {
											t.commit();
											resultMap.put(Code.RESULT, Code.SUCCESS);
											resultMap.put(Code.SETNO, String.valueOf(setNo));
											resultMap.put(Code.SCROLLNO, String.valueOf(mainScrollNo));
											if (osBalance > 0)
												resultMap.put("MainBalance", String.valueOf(osBalance) + " DR");
											else
												resultMap.put("MainBalance", String.valueOf(osBalance) + " CR");
											// resultMap.put("MainBalance", String.valueOf(mainBalance));
											return resultMap;
										} else {
											if (t.isActive())
												t.rollback();
											logger.error("Transaction unsuccessful");
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "0");
											resultMap.put(Code.SCROLLNO, "0");
											return resultMap;
										}
									} else {
										if (t.isActive())
											t.rollback();
										logger.error("Transaction unsuccessful");
										resultMap.put(Code.RESULT, Code.ERROR);
										resultMap.put(Code.SETNO, "0");
										resultMap.put(Code.SCROLLNO, "0");
										return resultMap;
									}
								} else {
									if (t.isActive())
										t.rollback();
									logger.error("Transaction unsuccessful");
									resultMap.put(Code.RESULT, Code.ERROR);
									resultMap.put(Code.SETNO, "0");
									resultMap.put(Code.SCROLLNO, "0");
									return resultMap;
								}
							} else {
								if (t.isActive())
									t.rollback();
								logger.error("Transaction unsuccessful");
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "0");
								resultMap.put(Code.SCROLLNO, "0");
								return resultMap;
							}
						} else {
							if (t.isActive())
								t.rollback();
							logger.error("Transaction unsuccessful");
							resultMap.put(Code.RESULT, Code.ERROR);
							resultMap.put(Code.SETNO, "0");
							resultMap.put(Code.SCROLLNO, "0");
							return resultMap;
						}
					} else {
						if (t.isActive())
							t.rollback();
						logger.error("Transaction unsuccessful");
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "0");
						resultMap.put(Code.SCROLLNO, "0");
						return resultMap;
					}
				} else {
					if (t.isActive())
						t.rollback();
					logger.error("Transaction unsuccessful");
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "0");
					resultMap.put(Code.SCROLLNO, "0");
					return resultMap;
				}
			} else {
				logger.error("Other LOAN Branch Fund Transfer....");
				int setNo = VoucherCommon.getNextSetNo();
				int reconNo = VoucherCommon.getNextReconNo(lbrCode);
				int scrollNo = VoucherCommon.getNextScrollNo();
				D010004 selectedBatch = null;
				D010004 debitBatch = null;
				Double mainBalance = 0.0, osBalance = 0.0;
				common.debit(lbrCode, accNo, transType.toUpperCase(), setNo, scrollNo,
						narration + "/Loan/DR/" + rrn + "/" + benf15DigitAcctNo, amount, rrn, session);
				if (!common.isAborted) {
					if (VoucherMPOS.updateProductBalances(amount, lbrCode, "D", accNo.substring(0, 8).trim(), session)
							.equalsIgnoreCase(MSGConstants.SUCCESS)) {
						String balResponse = CoreTransactionMPOS.balanceShivKrupa(lbrCode, accNo, amount, "D", session);
						if (balResponse != null && balResponse.trim().length() > 0
								&& !balResponse.equalsIgnoreCase("99") && !balResponse.equalsIgnoreCase("51")) {
							if (!common.isAborted) {
								int setNoABB = VoucherCommon.getNextSetNo();
								int scrollNoAbb2 = VoucherCommon.getNextScrollNo();
								int scrollNoAbb = VoucherCommon.getNextScrollNo();
								int scrollNoAbb1 = 0;

								common.creditABB(lbrCode, MSGConstants.ABB_ACC, transType.toUpperCase(), setNo,
										scrollNoAbb2, narration, benBrCode, amount, rrn, reconNo, session);
								if (!common.isAborted) {
									if (VoucherMPOS.updateProductBalances(amount, lbrCode, "C", MSGConstants.ABB.trim(),
											session).equalsIgnoreCase(MSGConstants.SUCCESS)) {
										if (!common.isAborted) {
											logger.error("ABB Transaction successful");
											if (DataUtils.isLoanAccount(benBrCode, benAccNo.substring(0, 8).trim(),
													session)) {

												// ====================LOAN Account Voucher
												// ==============================
												D030003 d030003 = DataUtils.getLoanCharges(benBrCode, benAccNo,
														session);
												if (d030003 == null) {
													if (t.isActive())
														t.rollback();
													logger.error("Loan Parameters not found in D030003");
													resultMap.put(Code.RESULT, Code.ERROR);
													resultMap.put(Code.SETNO, "0");
													resultMap.put(Code.SCROLLNO, "0");
													return resultMap;
												} else {
													Date openDate = getOpenDateNew(lbrCode, session);
													mainBalance = d030003.getMainBalFcy();
													osBalance = (mainBalance * -1)
															+ (d030003.getIntPrvdFcy() - d030003.getIntPaidFcy())
															+ (d030003.getOthChgPrvdFcy() - d030003.getOthChgPaidFcy())
															+ (d030003.getPenalPrvdFcy() - d030003.getPenalPaidFcy())
															+ (d030003.getTaxPrvdFcy() - d030003.getTaxPaidFcy());
													if (openDate == null) {
														if (t.isActive())
															t.rollback();
														logger.error("Open Date Not Found. Aborting Transaction");
														resultMap.put(Code.RESULT, Code.ERROR);
														resultMap.put(Code.SETNO, "0");
														resultMap.put(Code.SCROLLNO, "0");
														return resultMap;
													}
													String batchCode = Props.getBatchProperty(MSGConstants.ABB);
													if (batchCode == null) {
														if (t.isActive())
															t.rollback();
														logger.error(
																transType + " parameter not found in batch.properties");
														resultMap.put(Code.RESULT, Code.ERROR);
														resultMap.put(Code.SETNO, "0");
														resultMap.put(Code.SCROLLNO, "0");
														return resultMap;
													}
													logger.error("Batch Code Form Properties File : " + batchCode);
													String batchCodes[] = batchCode.split("~");
													logger.error("Number of Batches is " + batchCodes.length
															+ "\t Names are " + Arrays.toString(batchCodes));
													if (batchCodes == null || batchCodes.length < 1) {
														if (t.isActive())
															t.rollback();
														logger.error("Batch Codes Not Found in Properties File.");
														resultMap.put(Code.RESULT, Code.ERROR);
														resultMap.put(Code.SETNO, "0");
														resultMap.put(Code.SCROLLNO, "0");
														return resultMap;
													}
													selectedBatch = getSelectedBatchNew(lbrCode, batchCodes, openDate,
															session);
													if (selectedBatch == null) {
														if (t.isActive())
															t.rollback();
														logger.error("No Active Batch Found.");
														resultMap.put(Code.RESULT, Code.ERROR);
														resultMap.put(Code.SETNO, "0");
														resultMap.put(Code.SCROLLNO, "0");
														return resultMap;
													}
													/*
													 * D001004 d04OnlineBatchName = VoucherMPOS
													 * .getBatchNameFromBatchCodeNew(batchCodes[0], session);
													 * if(d04OnlineBatchName==null) { if(t.isActive())t.rollback();
													 * logger.error("System parameters not set properly");
													 * resultMap.put(Code.RESULT, Code.ERROR); resultMap.put(Code.SETNO,
													 * "0"); resultMap.put(Code.SCROLLNO, "0"); return resultMap; }
													 * String onlineBatchName = d04OnlineBatchName.getValue().trim();
													 * if(onlineBatchName==null) { if(t.isActive())t.rollback();
													 * logger.error("System parameters value is null or blank");
													 * resultMap.put(Code.RESULT, Code.ERROR); resultMap.put(Code.SETNO,
													 * "0"); resultMap.put(Code.SCROLLNO, "0"); return resultMap; }
													 * D010004 onlineBatch = getD010004(lbrCode, onlineBatchName,
													 * openDate); if(onlineBatch==null) { if(t.isActive())t.rollback();
													 * logger.error("online batch not found in batch master.");
													 * resultMap.put(Code.RESULT, Code.ERROR); resultMap.put(Code.SETNO,
													 * "0"); resultMap.put(Code.SCROLLNO, "0"); return resultMap; }
													 */
													amt = amount;
													System.out.println("Starting Amount::>>" + amt);
													if (d030003.getOthChgPrvdFcy() > d030003.getOthChgPaidFcy()
															&& amt > 0) {
														// && amt > d030003.getOthChgPrvdFcy() -
														// d030003.getOthChgPaidFcy()) {

														Double vrAmt = 0.0;
														if (amt > d030003.getOthChgPrvdFcy()
																- d030003.getOthChgPaidFcy()) {
															amt = amt - (d030003.getOthChgPrvdFcy()
																	- d030003.getOthChgPaidFcy());
															vrAmt = d030003.getOthChgPrvdFcy()
																	- d030003.getOthChgPaidFcy();
														} else {

															vrAmt = amt;
															amt = 0.0;
														}

														D009040 d009040LnoCH = prepareD009040LoanObject(selectedBatch,
																selectedBatch, benBrCode, setNoABB,
																VoucherCommon.getNextScrollNo(), scrollNoAbb, benAccNo,
																session, vrAmt, narration, MSGConstants.CR, rrn,
																MSGConstants.LNOCHCR, MSGConstants.LOAN_ACTIVITY,
																MSGConstants.LOAN_TYPE_1);

														// if(d009040LnoCH.getVcrAcctId().substring(0,
														// 6).trim().equalsIgnoreCase(""))
														d009040LnoCH.setVcrAcctId(d009040LnoCH.getMainAcctId());

														session.save(d009040LnoCH);
														osBalance = osBalance - vrAmt;

														scrollNoAbb1 = d009040LnoCH.getId().getScrollNo();
														D030003 d0300003 = updateBal(benAccNo, benBrCode, session,
																MSGConstants.LOAN_TYPE_1, vrAmt);
														if (d0300003 != null)
															session.update(d0300003);

													}
													if (d030003.getPenalPrvdFcy() > d030003.getPenalPaidFcy()
															&& amt > 0) {
														// && amt > d030003.getPenalPrvdFcy() -
														// d030003.getPenalPaidFcy()) {

														Double vrAmt = 0.0;
														if (amt > d030003.getPenalPrvdFcy()
																- d030003.getPenalPaidFcy()) {
															amt = amt - (d030003.getPenalPrvdFcy()
																	- d030003.getPenalPaidFcy());
															vrAmt = d030003.getPenalPrvdFcy()
																	- d030003.getPenalPaidFcy();
														} else {

															vrAmt = amt;
															amt = 0.0;
														}

														D009040 d009040LnoCH = prepareD009040LoanObject(selectedBatch,
																selectedBatch, lbrCode, setNoABB,
																VoucherCommon.getNextScrollNo(), scrollNoAbb, benAccNo,
																session, vrAmt, narration, MSGConstants.CR, rrn,
																MSGConstants.LNPCR, MSGConstants.LOAN_ACTIVITY,
																MSGConstants.LOAN_TYPE_2);

														if (d009040LnoCH.getVcrAcctId().substring(0, 6).trim()
																.equalsIgnoreCase("")) {
															if (t.isActive())
																t.rollback();
															logger.error("Account Not found for Penal");
															resultMap.put("ErrorMsg", "Account Not found for Penal");
															resultMap.put(Code.RESULT, Code.ERROR);
															resultMap.put(Code.SETNO, "0");
															resultMap.put(Code.SCROLLNO, "0");
															return resultMap;
														}

														session.save(d009040LnoCH);
														osBalance = osBalance - vrAmt;
														if (scrollNoAbb1 == 0)
															scrollNoAbb1 = d009040LnoCH.getId().getScrollNo();
														D030003 d0300003 = updateBal(benAccNo, benBrCode, session,
																MSGConstants.LOAN_TYPE_2, vrAmt);
														if (d0300003 != null)
															session.update(d0300003);
													}
													if (d030003.getTaxPrvdFcy() > d030003.getTaxPaidFcy() && amt > 0) {
														// && amt > d030003.getTaxPrvdFcy() - d030003.getTaxPaidFcy()) {

														Double vrAmt = 0.0;
														if (amt > d030003.getTaxPrvdFcy() - d030003.getTaxPaidFcy()) {
															amt = amt - (d030003.getTaxPrvdFcy()
																	- d030003.getTaxPaidFcy());
															vrAmt = d030003.getTaxPrvdFcy() - d030003.getTaxPaidFcy();
														} else {

															vrAmt = amt;
															amt = 0.0;
														}

														D009040 d009040LnoCH = prepareD009040LoanObject(selectedBatch,
																selectedBatch, benBrCode, setNoABB,
																VoucherCommon.getNextScrollNo(), scrollNoAbb, benAccNo,
																session, vrAmt, narration, MSGConstants.CR, rrn,
																MSGConstants.LNTAXCR, MSGConstants.LOAN_ACTIVITY,
																MSGConstants.LOAN_TYPE_3);

														if (d009040LnoCH.getVcrAcctId().substring(0, 6).trim()
																.equalsIgnoreCase(""))
															d009040LnoCH.setVcrAcctId(d009040LnoCH.getMainAcctId());

														session.save(d009040LnoCH);
														osBalance = osBalance - vrAmt;
														if (scrollNoAbb1 == 0)
															scrollNoAbb1 = d009040LnoCH.getId().getScrollNo();
														D030003 d0300003 = updateBal(benAccNo, benBrCode, session,
																MSGConstants.LOAN_TYPE_3, vrAmt);
														if (d0300003 != null)
															session.update(d0300003);
													}
													if (d030003.getIntPrvdFcy() > d030003.getIntPaidFcy() && amt > 0) {
														// && amt > d030003.getIntPrvdFcy() - d030003.getIntPaidFcy()) {

														Double vrAmt = 0.0;
														if (amt > d030003.getIntPrvdFcy() - d030003.getIntPaidFcy()) {
															amt = amt - (d030003.getIntPrvdFcy()
																	- d030003.getIntPaidFcy());
															vrAmt = d030003.getIntPrvdFcy() - d030003.getIntPaidFcy();
														} else {

															vrAmt = amt;
															amt = 0.0;
														}

														D009040 d009040LnoCH = prepareD009040LoanObject(selectedBatch,
																selectedBatch, benBrCode, setNoABB,
																VoucherCommon.getNextScrollNo(), scrollNoAbb, benAccNo,
																session, vrAmt, narration, MSGConstants.CR, rrn,
																"LNINTCR", MSGConstants.LOAN_ACTIVITY,
																MSGConstants.LOAN_TYPE_3);

														if (d009040LnoCH.getVcrAcctId().substring(0, 6).trim()
																.equalsIgnoreCase(""))
															d009040LnoCH.setVcrAcctId(d009040LnoCH.getMainAcctId());

														session.save(d009040LnoCH);
														osBalance = osBalance - vrAmt;
														if (scrollNoAbb1 == 0)
															scrollNoAbb1 = d009040LnoCH.getId().getScrollNo();
														D030003 d0300003 = updateBal(benAccNo, benBrCode, session, "5",
																vrAmt);
														if (d0300003 != null)
															session.update(d0300003);
													}
													if (amt > 0) {

														if ((mainBalance + amt) > 0 || mainBalance > 0) {
															if (t.isActive())
																t.rollback();
															logger.error("Loan Amount in Positive");
															resultMap.put("ErrorMsg",
																	"Deposit Amount is greater than Loan Amount. Contact Branch");
															resultMap.put(Code.RESULT, Code.ERROR);
															resultMap.put(Code.SETNO, "0");
															resultMap.put(Code.SCROLLNO, "0");
															return resultMap;
														} else {
															D009040 d009040LnoCH = prepareD009040LoanObject(
																	selectedBatch, selectedBatch, benBrCode, setNoABB,
																	VoucherCommon.getNextScrollNo(), scrollNoAbb,
																	benAccNo, session, amt, narration, MSGConstants.CR,
																	rrn, MSGConstants.LNPCR, MSGConstants.LOAN_ACTIVITY,
																	MSGConstants.LOAN_TYPE_4);
															session.save(d009040LnoCH);
															if (scrollNoAbb1 == 0)
																scrollNoAbb1 = d009040LnoCH.getId().getScrollNo();
															D030003 d0300003 = updateBal(benAccNo, benBrCode, session,
																	"4", amt);
															if (d0300003 != null) {
																session.update(d0300003);
																mainBalance = mainBalance + amt;
																osBalance = osBalance - amt;
															}
														}
													}
												}

											}
											/*
											 * common.credit(benBrCode, benAccNo, MSGConstants.ABB, setNoABB,
											 * scrollNoAbb1, narration, amount, rrn, session);
											 */
											if (!common.isAborted) {
												if (VoucherMPOS
														.updateProductBalances(amount, benBrCode, "C",
																benAccNo.substring(0, 8).trim(), session)
														.equalsIgnoreCase(MSGConstants.SUCCESS)) {
													String bal = CoreTransactionMPOS.balanceShivKrupa(benBrCode,
															benAccNo, amount, "C", session);
													if (bal != null && bal.trim().length() > 0
															&& !bal.trim().equalsIgnoreCase("51")
															&& !bal.trim().equalsIgnoreCase("99")) {
														if (!common.isAborted) {
															logger.error("Other Bank GL Transaction successful");
															common.debitABB(benBrCode, MSGConstants.ABB_ACC,
																	MSGConstants.ABB, setNoABB, scrollNoAbb, narration,
																	lbrCode, amount, rrn, reconNo, session);
															if (!common.isAborted) {
																if (VoucherMPOS
																		.updateProductBalances(amount, benBrCode, "D",
																				MSGConstants.ABB_ACC.substring(0, 8)
																						.trim(),
																				session)
																		.equalsIgnoreCase(MSGConstants.SUCCESS)) {
																	Date opdate = DataUtils.getOpenDate(lbrCode);
																	if (opdate == null) {
																		if (t.isActive())
																			t.rollback();
																		logger.error(
																				"Operaton Date not found for branch "
																						+ lbrCode);
																		resultMap.put(Code.RESULT, Code.ERROR);
																		resultMap.put(Code.SETNO, "");
																		resultMap.put(Code.SCROLLNO, "");
																		return resultMap;
																	}
																	Date drOpdate = DataUtils.getOpenDate(benBrCode);
																	if (drOpdate == null) {
																		if (t.isActive())
																			t.rollback();
																		logger.error(
																				"Operaton Date not found for branch "
																						+ drOpdate);
																		resultMap.put(Code.RESULT, Code.ERROR);
																		resultMap.put(Code.SETNO, "");
																		resultMap.put(Code.SCROLLNO, "");
																		return resultMap;
																	}
																	/*
																	 * String batchCodes[] =
																	 * Props.getBatchProperty(transType.toUpperCase())
																	 * .split("~"); if(batchCodes==null ||
																	 * batchCodes.length==0) {
																	 * if(t.isActive())t.rollback(); logger.
																	 * error("Batch code not in batch.properties "
																	 * +lbrCode); resultMap.put(Code.RESULT,
																	 * Code.ERROR); resultMap.put(Code.SETNO, "");
																	 * resultMap.put(Code.SCROLLNO, ""); return
																	 * resultMap; } D001004 d04OnlineBatchName =
																	 * VoucherMPOS
																	 * .getBatchNameFromBatchCode(batchCodes[0]);
																	 * if(d04OnlineBatchName==null) {
																	 * if(t.isActive())t.rollback();
																	 * logger.error("System parameter not found for "
																	 * +batchCodes[0]); resultMap.put(Code.RESULT,
																	 * Code.ERROR); resultMap.put(Code.SETNO, "");
																	 * resultMap.put(Code.SCROLLNO, ""); return
																	 * resultMap; } String onlineBatchName =
																	 * d04OnlineBatchName .getValue().trim();
																	 * if(onlineBatchName==null) {
																	 * if(t.isActive())t.rollback();
																	 * logger.error("System parameter not found for "
																	 * +batchCodes[0]); resultMap.put(Code.RESULT,
																	 * Code.ERROR); resultMap.put(Code.SETNO, "");
																	 * resultMap.put(Code.SCROLLNO, ""); return
																	 * resultMap; } String batchCodes1[] =
																	 * Props.getBatchProperty("ABB") .split("~");
																	 * if(batchCodes1==null || batchCodes1.length<1) {
																	 * if(t.isActive())t.rollback(); logger.
																	 * error("Batch Code not found in properties file. "
																	 * ); resultMap.put(Code.RESULT, Code.ERROR);
																	 * resultMap.put(Code.SETNO, "");
																	 * resultMap.put(Code.SCROLLNO, ""); return
																	 * resultMap; } D001004 d001004 = VoucherMPOS
																	 * .getBatchNameFromBatchCode(batchCodes1[0]);
																	 * if(d001004==null) { if(t.isActive())t.rollback();
																	 * logger.error(batchCodes1[0]
																	 * +" System parameter not found");
																	 * resultMap.put(Code.RESULT, Code.ERROR);
																	 * resultMap.put(Code.SETNO, "");
																	 * resultMap.put(Code.SCROLLNO, ""); return
																	 * resultMap; } String benBatchCode =
																	 * d001004.getValue().trim(); if(benBatchCode==null
																	 * || benBatchCode.trim().isEmpty()) {
																	 * if(t.isActive())t.rollback();
																	 * logger.error("System parameter"+batchCodes1[0]
																	 * +" value is null or blank");
																	 * resultMap.put(Code.RESULT, Code.ERROR);
																	 * resultMap.put(Code.SETNO, "");
																	 * resultMap.put(Code.SCROLLNO, ""); return
																	 * resultMap; }
																	 */

																	String debitbatchCode = Props
																			.getBatchProperty(MSGConstants.ABB);
																	if (debitbatchCode == null) {
																		if (t.isActive())
																			t.rollback();
																		logger.error(transType
																				+ " parameter not found in batch.properties");
																		resultMap.put(Code.RESULT, Code.ERROR);
																		resultMap.put(Code.SETNO, "0");
																		resultMap.put(Code.SCROLLNO, "0");
																		return resultMap;
																	}
																	logger.error("Batch Code Form Properties File : "
																			+ debitbatchCode);
																	String batchCodes1[] = debitbatchCode.split("~");
																	logger.error("Number of Batches is "
																			+ batchCodes1.length + "\t Names are "
																			+ Arrays.toString(batchCodes1));
																	if (batchCodes1 == null || batchCodes1.length < 1) {
																		if (t.isActive())
																			t.rollback();
																		logger.error(
																				"Batch Codes Not Found in Properties File.");
																		resultMap.put(Code.RESULT, Code.ERROR);
																		resultMap.put(Code.SETNO, "0");
																		resultMap.put(Code.SCROLLNO, "0");
																		return resultMap;
																	}
																	debitBatch = getSelectedBatchNew(lbrCode,
																			batchCodes1, drOpdate, session);

																	D100001 d100001 = VoucherMPOS.prepareReconObj(
																			lbrCode, reconNo, opdate, 999999,
																			debitBatch.getId().getBatchCd(),
																			selectedBatch.getId().getBatchCd(), setNo,
																			scrollNo, setNoABB, scrollNoAbb,
																			scrollNoAbb1, benBrCode, "" + benAccNo,
																			Double.valueOf(amount), "D");
																	if (d100001 == null) {
																		if (t.isActive())
																			t.rollback();
																		logger.error(
																				"unable to prepare recon object (D100001)");
																		resultMap.put(Code.RESULT, Code.ERROR);
																		resultMap.put(Code.SETNO, "");
																		resultMap.put(Code.SCROLLNO, "");
																		return resultMap;
																	}
																	D100002 d100002 = VoucherMPOS.prepareRecon2Obj(
																			lbrCode, reconNo, opdate, 999999,
																			debitBatch.getId().getBatchCd(),
																			selectedBatch.getId().getBatchCd(), setNo,
																			scrollNo, setNoABB, scrollNoAbb,
																			scrollNoAbb1, benBrCode, "" + benAccNo,
																			Double.valueOf(amount), "D");//
																	if (d100002 == null) {
																		if (t.isActive())
																			t.rollback();
																		logger.error(
																				"unable to prepare recon object (D100002)");
																		resultMap.put(Code.RESULT, Code.ERROR);
																		resultMap.put(Code.SETNO, "");
																		resultMap.put(Code.SCROLLNO, "");
																		return resultMap;
																	}
																	session.save(d100001);
																	session.save(d100002);
																	session.flush();
																	t.commit();
																	resultMap.put(Code.RESULT, Code.SUCCESS);
																	resultMap.put(Code.SETNO, String.valueOf(setNo));
																	resultMap.put(Code.SCROLLNO,
																			String.valueOf(scrollNo));
																	if (osBalance > 0)
																		resultMap.put("MainBalance",
																				String.valueOf(osBalance) + " DR");
																	else
																		resultMap.put("MainBalance",
																				String.valueOf(osBalance) + " CR");

																	return resultMap;
																} else {
																	if (t.isActive())
																		t.rollback();
																	logger.error(
																			"common.isAborted::>>" + common.isAborted);
																	logger.error("ABB Transaction unsuccessful");
																	resultMap.put(Code.RESULT, Code.ERROR);
																	resultMap.put(Code.SETNO, "");
																	resultMap.put(Code.SCROLLNO, "");
																	return resultMap;
																}
															} else {
																if (t.isActive())
																	t.rollback();
																logger.error("common.isAborted::>>" + common.isAborted);
																logger.error("ABB Transaction unsuccessful");
																resultMap.put(Code.RESULT, Code.ERROR);
																resultMap.put(Code.SETNO, "");
																resultMap.put(Code.SCROLLNO, "");
																return resultMap;
															}
														} else {
															if (t.isActive())
																t.rollback();
															logger.error("common.isAborted::>>" + common.isAborted);
															logger.error("ABB Transaction unsuccessful");
															resultMap.put(Code.RESULT, Code.ERROR);
															resultMap.put(Code.SETNO, "");
															resultMap.put(Code.SCROLLNO, "");
															return resultMap;
														}
													} else {
														if (t.isActive())
															t.rollback();
														logger.error("common.isAborted::>>" + common.isAborted);
														logger.error("ABB Transaction unsuccessful");
														resultMap.put(Code.RESULT, Code.ERROR);
														resultMap.put(Code.SETNO, "");
														resultMap.put(Code.SCROLLNO, "");
														return resultMap;
													}
												} else {
													if (t.isActive())
														t.rollback();
													logger.error("common.isAborted::>>" + common.isAborted);
													logger.error("ABB Transaction unsuccessful");
													resultMap.put(Code.RESULT, Code.ERROR);
													resultMap.put(Code.SETNO, "");
													resultMap.put(Code.SCROLLNO, "");
													return resultMap;
												}
											} else {
												if (t.isActive())
													t.rollback();
												logger.error("common.isAborted::>>" + common.isAborted);
												logger.error("ABB Transaction unsuccessful");
												resultMap.put(Code.RESULT, Code.ERROR);
												resultMap.put(Code.SETNO, "");
												resultMap.put(Code.SCROLLNO, "");
												return resultMap;
											}
										} else {
											if (t.isActive())
												t.rollback();
											logger.error("common.isAborted::>>" + common.isAborted);
											logger.error("ABB Transaction unsuccessful");
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "");
											resultMap.put(Code.SCROLLNO, "");
											return resultMap;
										}
									} else {
										if (t.isActive())
											t.rollback();
										logger.error("ABB Transaction unsuccessful");
										resultMap.put(Code.RESULT, Code.ERROR);
										resultMap.put(Code.SETNO, "");
										resultMap.put(Code.SCROLLNO, "");
										return resultMap;
									}
								} else {
									if (t.isActive())
										t.rollback();
									logger.error("ABB Transaction unsuccessful");
									resultMap.put(Code.RESULT, Code.ERROR);
									resultMap.put(Code.SETNO, "");
									resultMap.put(Code.SCROLLNO, "");
									return resultMap;
								}
							} else {
								if (t.isActive())
									t.rollback();
								logger.error("Transaction unsuccessful");
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "");
								resultMap.put(Code.SCROLLNO, "");
								return resultMap;
							}
						} else {
							if (t.isActive())
								t.rollback();
							logger.error("Transaction unsuccessful");
							resultMap.put(Code.RESULT, Code.ERROR);
							resultMap.put(Code.SETNO, "");
							resultMap.put(Code.SCROLLNO, "");
							return resultMap;
						}
					} else {
						if (t.isActive())
							t.rollback();
						logger.error("Transaction unsuccessful");
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "");
						resultMap.put(Code.SCROLLNO, "");
						return resultMap;
					}
				} else {
					if (t.isActive())
						t.rollback();
					logger.error("Transaction unsuccessful");
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "");
					resultMap.put(Code.SCROLLNO, "");
					return resultMap;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			if (t.isActive())
				t.rollback();
			e.printStackTrace();
			logger.error("ERROR", e);
			resultMap.put(Code.RESULT, Code.ERROR);
			resultMap.put(Code.SETNO, "");
			resultMap.put(Code.SCROLLNO, "");
			return resultMap;
		} finally {
			session.close();
			session = null;
			t = null;
		}
	}

	public static HashMap<String, String> simSePayReverseVoucherEntry(int lbrCode, String accNo, String transType,
			String narration, double amount, String rrn) {
		VoucherCommon common = new VoucherCommon();
		HashMap<String, String> resultMap = new HashMap<>();
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		int setNo = VoucherCommon.getNextSetNo();
		int scrollNo = VoucherCommon.getNextScrollNo();
		String drAccno = String.format("%-8s", VoucherCommon.getSysParameter(MSGConstants.SIMPAYCRACCT).trim())
				+ "000000000000000000000000";
		;
		int crBrcode = Integer.valueOf(getSysParameter(MSGConstants.SIMPAYCRBRANCH).trim());
		int reconNo = VoucherCommon.getNextReconNo(crBrcode);
		logger.error("accNo::>>" + accNo);
		logger.error("lbrCode::>>" + lbrCode);
		common.credit(lbrCode, accNo, MSGConstants.ABB, setNo, scrollNo, narration, amount, rrn, session);
		if (!common.isAborted) {
			if (VoucherMPOS.updateProductBalances(amount, lbrCode, "C", accNo.substring(0, 8).trim(), session).trim()
					.equalsIgnoreCase(MSGConstants.SUCCESS)) {
				String bal = CoreTransactionMPOS.balance(lbrCode, accNo, amount, "C", session);
				if (bal != null && bal.trim().length() > 0 && !bal.equalsIgnoreCase("51")
						&& !bal.equalsIgnoreCase("99")) {
					if (!common.isAborted) {
						int setNoABB = VoucherCommon.getNextSetNo();
						int scrollNoAbb = VoucherCommon.getNextScrollNo();
						int scrollNoAbb1 = VoucherCommon.getNextScrollNo();
						int scrollNoAbb2 = VoucherCommon.getNextScrollNo();
						common.debitABB(lbrCode, MSGConstants.ABB_ACC, MSGConstants.ABB, setNo, scrollNoAbb, narration,
								crBrcode, amount, rrn, reconNo, session);
						if (!common.isAborted) {
							if (VoucherMPOS
									.updateProductBalances(amount, lbrCode, "D",
											MSGConstants.ABB_ACC.substring(0, 8).trim(), session)
									.trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
								if (!common.isAborted) {
									logger.error("ABB Transaction successful");
									// String
									// drAccno=getSysParameter("MBBILLPAY").trim()+"
									// 000000000000000000000000";
									common.debit(crBrcode, drAccno, MSGConstants.SIM_SAY_PAY, setNoABB, scrollNoAbb1,
											narration, amount, rrn, session);
									if (!common.isAborted) {
										if (VoucherMPOS
												.updateProductBalances(amount, crBrcode, "D",
														drAccno.substring(0, 8).trim(), session)
												.trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
											if (!common.isAborted) {
												logger.error("Other Bank GL Transaction successful");
												common.creditABB(crBrcode, MSGConstants.ABB_ACC,
														transType.trim().toUpperCase(), setNoABB, scrollNoAbb2,
														narration, lbrCode, amount, rrn, reconNo, session);
												VoucherMPOS.updateProductBalances(amount, crBrcode, "C",
														MSGConstants.ABB_ACC.substring(0, 8).trim(), session);

												Date opdate = DataUtils.getOpenDate(lbrCode);

												String batchCodes[] = Props.getBatchProperty(MSGConstants.SIM_SAY_PAY)
														.split("~");
												D001004 d04OnlineBatchName = VoucherMPOS
														.getBatchNameFromBatchCode(batchCodes[0]);
												String onlineBatchName = d04OnlineBatchName.getValue().trim();
												String batchCodes1[] = Props.getBatchProperty("ABB").split("~");
												D001004 d001004 = VoucherMPOS.getBatchNameFromBatchCode(batchCodes1[0]);
												String benBatchCode = d001004.getValue().trim();

												D100001 d100001 = VoucherMPOS.prepareReconObj(crBrcode, reconNo, opdate,
														999999, onlineBatchName, benBatchCode, setNoABB, scrollNoAbb2,
														setNo, scrollNo, scrollNoAbb, lbrCode, "" + accNo,
														Double.valueOf(amount), "D");
												System.out.println("d100001::>>>" + d100001);
												D100002 d100002 = VoucherMPOS.prepareRecon2Obj(crBrcode, reconNo,
														opdate, 999999, onlineBatchName, benBatchCode, setNoABB,
														scrollNoAbb2, setNo, scrollNo, scrollNoAbb, lbrCode, "" + accNo,
														Double.valueOf(amount), "D");
												System.out.println("d100002::>>>" + d100002);
												try {
													session.save(d100001);
													session.save(d100002);
													t.commit();
													session.close();
													session = null;
													t = null;
												} catch (Exception e) {
													e.printStackTrace();
													t.rollback();
													t = null;
													session = null;
												}
												resultMap.put(Code.RESULT, Code.SUCCESS);
												resultMap.put(Code.SETNO, String.valueOf(setNo));
												resultMap.put(Code.SCROLLNO, String.valueOf(scrollNo));
												return resultMap;
											} else {
												logger.error("ABB Transaction unsuccessful");
												resultMap.put(Code.RESULT, Code.ERROR);
												resultMap.put(Code.SETNO, "");
												resultMap.put(Code.SCROLLNO, "");
												common = null;
												session.close();
												session = null;
												t = null;
												return resultMap;
											}
										} else {
											logger.error("ABB Transaction unsuccessful");
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "");
											resultMap.put(Code.SCROLLNO, "");
											common = null;
											session.close();
											session = null;
											t = null;
											return resultMap;
										}
									} else {
										logger.error("ABB Transaction unsuccessful");
										resultMap.put(Code.RESULT, Code.ERROR);
										resultMap.put(Code.SETNO, "");
										resultMap.put(Code.SCROLLNO, "");
										common = null;
										session.close();
										session = null;
										t = null;
										return resultMap;
									}
								} else {
									logger.error("ABB Transaction unsuccessful");
									resultMap.put(Code.RESULT, Code.ERROR);
									resultMap.put(Code.SETNO, "");
									resultMap.put(Code.SCROLLNO, "");
									common = null;
									session.close();
									session = null;
									t = null;
									return resultMap;
								}
							} else {
								logger.error("ABB Transaction unsuccessful");
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "");
								resultMap.put(Code.SCROLLNO, "");
								common = null;
								session.close();
								session = null;
								t = null;
								return resultMap;
							}
						} else {
							logger.error("ABB Transaction unsuccessful");
							resultMap.put(Code.RESULT, Code.ERROR);
							resultMap.put(Code.SETNO, "");
							resultMap.put(Code.SCROLLNO, "");
							common = null;
							t.commit();
							session.close();
							session = null;
							t = null;
							return resultMap;
						}
					} else {
						logger.error("Transaction unsuccessful");
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "");
						resultMap.put(Code.SCROLLNO, "");
						common = null;
						t.commit();
						session.close();
						session = null;
						t = null;
						return resultMap;
					}
				} else {
					logger.error("Transaction unsuccessful");
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "");
					resultMap.put(Code.SCROLLNO, "");
					common = null;
					t.commit();
					session.close();
					session = null;
					t = null;
					return resultMap;
				}
			} else {
				logger.error("Transaction unsuccessful");
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "");
				resultMap.put(Code.SCROLLNO, "");
				common = null;
				t.commit();
				session.close();
				session = null;
				t = null;
				return resultMap;
			}

		} else {
			logger.error("Transaction unsuccessful");
			resultMap.put(Code.RESULT, Code.ERROR);
			resultMap.put(Code.SETNO, "");
			resultMap.put(Code.SCROLLNO, "");
			common = null;
			t.commit();
			session.close();
			session = null;
			t = null;
			return resultMap;
		}
	}

	public static D009040 prepareD009040LoanObject(D010004 selectedBatch, D010004 onlineBatch, int lbrCode, int setNo,
			int scrollNo, int mainScrollNo, String prdAcctId, Session session, double amt, String particulars,
			String drCr, String rrn, String cashFlowType, String activity, String flag) {
		int usrCode = 0;
		// ==========Cash Flow Type===
		// LNOCHCR
		// LNTAXCR
		// LNPINTCR
		// LNPCR
		char debitCredit = ' ';
		if (drCr.trim().equalsIgnoreCase("D"))
			debitCredit = 'D';
		if (drCr.trim().equalsIgnoreCase("C"))
			debitCredit = 'C';

		D009040 d40 = new D009040();
		D009040Id id40 = new D009040Id();

		D030002 d030002 = DataUtils.getLoanChargesAcc(lbrCode, prdAcctId.substring(0, 8).trim(), session);
		if (d030002 == null)
			logger.error("Loan Account not defined in d030002.");

		if (flag.equalsIgnoreCase("1"))
			d40.setVcrAcctId(d030002.getDrCrPrdAcctId());

		if (flag.equalsIgnoreCase("2")) {
			d40.setVcrAcctId(d030002.getPenalChgDrId());
			// D030002 d030002=
		}
		if (flag.equalsIgnoreCase("3"))
			d40.setVcrAcctId(d030002.getIrPrdAcctId());

		if (flag.equalsIgnoreCase("4"))
			d40.setVcrAcctId(prdAcctId);

		id40.setBatchCd(selectedBatch.getId().getBatchCd()); // SELECT * FROM
																// D010004 WHERE
																// LBrCode =9
																// AND EntryDate
																// =
																// '19-APR-2016'
		id40.setEntryDate(selectedBatch.getId().getEntryDate()); // SELECT Value
																	// FROM
																	// D001004
																	// WHERE
																	// LBrCode =
																	// 9 AND
																	// Code =
																	// 'LASTOPENDATE'
		id40.setLbrCode(lbrCode);
		id40.setSetNo((int) setNo);
		id40.setScrollNo((int) scrollNo);
		d40.setId(id40);
		d40.setMainScrollNo((int) mainScrollNo); // Scroll No
		d40.setPostDate(onlineBatch.getPostDate()); // SELECT * FROM D010004
													// WHERE LBrCode =9 AND
													// EntryDate = '19-APR-2016'
													// AND BatchCd = 'ABBTR'
		d40.setFeffDate(onlineBatch.getFeffDate()); // SELECT * FROM D010004
													// WHERE LBrCode =9 AND
													// EntryDate = '19-APR-2016'
													// AND BatchCd = 'ABBTR'
		d40.setActivityType(activity);
		d40.setCashFlowType(cashFlowType);
		d40.setValueDate(onlineBatch.getPostDate()); // postdate SELECT * FROM
														// D010004 WHERE LBrCode
														// =9 AND EntryDate =
														// '19-APR-2016' AND
														// BatchCd = 'ABBTR'

		logger.error("Calling D010001");
		D010001 d001 = getD001(lbrCode, selectedBatch.getId().getBatchCd());
		String booktype = d001.getBookType();
		d40.setBookType(booktype); // SELECT BookType FROM D010001 WHERE LBrCode
									// = 9 AND Code = 'ABBTR'
		d40.setDrCr((char) debitCredit);
		// d40.setVcrAcctId(prdAcctId);
		d40.setMainAcctId(prdAcctId);
		D009021 d009021 = getD009021(lbrCode, prdAcctId.substring(0, 8).trim());
		d40.setMainModType(d009021.getModuleType()); // SELECT ModuleType FROM
														// D009021 WHERE LBrCode
														// =9 AND PrdCd = 'SB
		d40.setVcrModType(d009021.getModuleType()); // SELECT ModuleType FROM
													// D009021 WHERE LBrCode =9
													// AND PrdCd = 'SB
		d40.setTrnCurCd("INR");
		d40.setFcyTrnAmt(amt);
		d40.setLcyConvRate(1);
		d40.setLcyTrnAmt(amt);
		d40.setInstrBankCd((short) 0);//
		d40.setInstrBranchCd((short) 0);
		d40.setInstrType((short) 99); // Depend on chanel
		d40.setInstrNo(rrn); // RRN
		d40.setInstrDate(new Date()); // Blank
		String cashFlow = "";
		if ("LNOCHCR".equalsIgnoreCase(cashFlowType))
			cashFlow = "Other Charges";
		else if ("LNPINTCR".equalsIgnoreCase(cashFlowType))
			cashFlow = "Interest Receivable";
		else if ("LNPCR".equalsIgnoreCase(cashFlowType))
			cashFlow = "Main Balance";
		else if ("LNTAXCR".equalsIgnoreCase(cashFlowType))
			cashFlow = "Tax Charges";
		else if ("LNINTCR".equalsIgnoreCase(cashFlowType))
			cashFlow = "Interest";

		d40.setParticulars(particulars + "/" + cashFlow + "/" + rrn + "/"); // param
		d40.setSysGenVcr((byte) 0); // value 0
		d40.setShTotFlag('Y'); //
		d40.setShClrFlag('Y');
		d40.setAcTotFlag('Y');
		d40.setAcClrFlag('Y');

		D002001 d002001 = getD002001("WEB");
		usrCode = d002001.getUsrCode2();
		d40.setMaker(d002001.getUsrCode2()); // SELECT UsrCode2 FROM D002001
												// WHERE UsrCode1 = 'WEB'
		d40.setMakerDate(selectedBatch.getId().getEntryDate()); // EntryDate
		d40.setMakerTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8)));
		d40.setChecker1(d002001.getUsrCode2()); // SELECT UsrCode2 FROM D002001
												// WHERE UsrCode1 = 'WEB'
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
		d40.setPostTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8))); // Time
																					// HHMMSSTT
		return d40;
	}

	public static D010001 getD001(int lbrCode, String BatchCd) {
		// SELECT BookType FROM D010001 WHERE LBrCode = 9 AND Code = 'ABBTR'
		System.out.println("lbrCode:>>>" + lbrCode);
		System.out.println("BatchCd::>>" + BatchCd);
		D010001 d001 = null;
		try (Session session = HBUtil.getSessionFactory().openSession()) {
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

	public static D002001 getD002001(String usrCode1) {
		D002001 d201 = null;
		try (Session session = HBUtil.getSessionFactory().openSession()) {
			d201 = session.get(D002001.class, usrCode1);
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return d201;
	}

	public static D009021 getD009021(int lbrCode, String prdCd) {
		D009021 d21 = null;
		try (Session session = HBUtil.getSessionFactory().openSession()) {
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

	public static D010004 getD010004(int lbrCode, String BatchCd, Date entryDate) {
		// SELECT * FROM D010004 WHERE LBrCode =9 AND EntryDate = '19-APR-2016'
		// AND BatchCd = 'ABBTR'
		D010004 d004 = null;
		try (Session session = HBUtil.getSessionFactory().openSession()) {
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

	public static D030003 updateBal(String accNo, int brCode, Session session, String flag, double amount) {
		D030003Id id = new D030003Id();
		id.setLbrCode(brCode);
		id.setPrdAcctId(accNo);
		D030003 d030003 = session.get(D030003.class, id);
		if (d030003 != null) {
			if (flag.equalsIgnoreCase("1"))
				d030003.setOthChgPaidFcy(d030003.getOthChgPaidFcy() + amount);
			else if (flag.equalsIgnoreCase("2"))
				d030003.setTaxPaidFcy(d030003.getTaxPaidFcy() + amount);
			else if (flag.equalsIgnoreCase("3"))
				d030003.setPenalPaidFcy(d030003.getPenalPaidFcy() + amount);
			else if (flag.equalsIgnoreCase("4")) {
				d030003.setMainBalFcy(d030003.getMainBalFcy() + amount);
				d030003.setMainBalLcy(d030003.getMainBalLcy() + amount);
			} else if (flag.equalsIgnoreCase("5")) {
				d030003.setIntPaidFcy(d030003.getIntPaidFcy() + amount);
			}
		}
		return d030003;
	}

	public void debitCreditGST(int brCode, String acctId, String tType, int setNo, int scrollNo, String narration,
			double amount, String rrn, Session session, String cashFlowType, String drCr) {
		try {
			Date openDate = getOpenDateNew(brCode, session);
			if (openDate == null) {
				Logger.error("Open Date Not Found. Aborting Transaction");
				isAborted = true;
				return;
			}
			logger.error("Open Date is " + openDate);

			String batchCode = Props.getBatchProperty(tType);
			logger.error("Batch Code Form Properties File : " + batchCode);
			String batchCodes[] = batchCode.split("~");
			logger.error("Number of Batches is " + batchCodes.length + "\t Names are " + Arrays.toString(batchCodes));
			if (batchCodes == null || batchCodes.length < 1) {
				Logger.error("Batch Codes Not Found in Properties File.");
				isAborted = true;
				return;
			}
			/// Get Selected Batch.
			D010004 selectedBatch = getSelectedBatchNew(brCode, batchCodes, openDate, session);
			if (selectedBatch == null) {
				Logger.error("No Active Batch Found.");
				isAborted = true;
				return;
			}
			logger.error("Selected Batch : " + selectedBatch.getId().getBatchCd());

			D009040 d40 = new D009040();

			if (drCr.equalsIgnoreCase(MSGConstants.DR)) {
				d40.setActivityType("DR");
				d40.setCashFlowType(cashFlowType);
				d40.setDrCr('D');
			} else {
				d40.setActivityType("CR");
				d40.setCashFlowType(cashFlowType);
				d40.setDrCr('C');
			}
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
			d40.setFeffDate(selectedBatch.getFeffDate());

			d40.setValueDate(selectedBatch.getPostDate());

			String bookType = getBookTypeNew(brCode, selectedBatch.getId().getBatchCd(), session);
			if (bookType == null) {
				Logger.error("BookType Not Found.");
				isAborted = true;
				return;
			}

			logger.error("BookType is : " + bookType);
			d40.setBookType(bookType); // SELECT BookType FROM D010001 WHERE
										// LBrCode = 9 AND Code = 'ABBTR'
			d40.setVcrAcctId(acctId);
			d40.setMainAcctId(acctId);

			short moduleType = getModuleTypeNew(brCode, acctId.substring(0, 8).trim(), session);
			if (moduleType == 0) {
				Logger.error("ModuleType Not Found.");
				isAborted = true;
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
			d40.setParticulars(narration.length() > 70 ? narration.substring(0, 70) : narration.trim()); // param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); //
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');

			int usrCode2 = Integer.parseInt(ConfigurationLoader.getParameters(false).getProperty("IMPS_USER"));

			if (usrCode2 == 0) {
				Logger.error("UsrCode Not Found.");
				isAborted = true;
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

			d40.setNoAuthPending((byte) 0);
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
			System.out.println("Updating Debit Batch balance");
			if (updateBatchBalance(session, amount, selectedBatch, brCode, MSGConstants.DR))
				isAborted = false;
			else
				isAborted = true;
			System.out.println("isAborted::>>" + isAborted);
		} catch (Exception e) {
			isAborted = true;
			e.printStackTrace();
		}
	}

	public static HashMap<String, String> reversePrepaidCardVoucherEntry(int lbrCode, String accNo, String transType,
			String narration, double amount, String rrn) {
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		VoucherCommon common = new VoucherCommon();
		HashMap<String, String> resultMap = new HashMap<>();
		try {
			int setNo = VoucherCommon.getNextSetNo();
			int scrollNo = VoucherCommon.getNextScrollNo();
			int reconNo = VoucherCommon.getNextReconNo(lbrCode);
			double cgstAmt = amount
					* (Double.valueOf(ConfigurationLoader.getParameters(false).getProperty("CGST_PER").trim())) / 100;
			double sgstAmt = amount
					* (Double.valueOf(ConfigurationLoader.getParameters(false).getProperty("SGST_PER").trim())) / 100;
			double igstAmt = amount
					* (Double.valueOf(ConfigurationLoader.getParameters(false).getProperty("IGST_PER").trim())) / 100;
			double chgAmt = Double.valueOf(ConfigurationLoader.getParameters(false).getProperty("CHG_AMOUNT").trim());
			double finalAmount = 0;

			common.credit(lbrCode, accNo, transType.toUpperCase(), setNo, scrollNo, narration, amount, rrn, session);
			if (!common.isAborted) {
				if (VoucherMPOS.updateProductBalances(amount, lbrCode, "C", accNo.substring(0, 8).trim(), session)
						.equalsIgnoreCase(MSGConstants.SUCCESS)) {
					if (ConfigurationLoader.getParameters(false).getProperty("CGST_FLAG").equalsIgnoreCase("Y")) {
						finalAmount = finalAmount + cgstAmt;
						common.debitCreditGST(lbrCode,
								String.format("%-8s", MSGConstants.CGST) + "000000000000000000000000", transType, setNo,
								VoucherCommon.getNextScrollNo(), narration, cgstAmt, rrn, session,
								MSGConstants.CGST_CASH_FLOW_CR, MSGConstants.DR);
						if (!common.isAborted) {
							String response = VoucherMPOS.updateProductBalances(cgstAmt, lbrCode, "D",
									MSGConstants.CGST, session);
							logger.error("response::>>" + response);
							if (response.equalsIgnoreCase(MSGConstants.SUCCESS))
								flag = MSGConstants.YES;
							else {
								logger.error("Product 'CGST' of branch" + lbrCode + "balance upddation fails");
								flag = MSGConstants.NO;
							}
						} else
							flag = MSGConstants.NO;
					}
					if (flag.equalsIgnoreCase(MSGConstants.YES)) {
						if (ConfigurationLoader.getParameters(false).getProperty("SGST_FLAG").equalsIgnoreCase("Y")) {
							finalAmount = finalAmount + sgstAmt;
							common.debitCreditGST(lbrCode,
									String.format("%-8s", MSGConstants.SGST) + "000000000000000000000000", transType,
									setNo, VoucherCommon.getNextScrollNo(), narration, sgstAmt, rrn, session,
									MSGConstants.SGST_CASH_FLOW_CR, MSGConstants.DR);
							if (!common.isAborted) {
								String response = VoucherMPOS.updateProductBalances(sgstAmt, lbrCode, "D",
										MSGConstants.SGST, session);
								logger.error("response::>>" + response);
								if (response.equalsIgnoreCase(MSGConstants.SUCCESS))
									flag = MSGConstants.YES;
								else {
									logger.error("Product 'SGST' of branch" + lbrCode + "balance upddation fails");
									flag = MSGConstants.NO;
								}
							} else
								flag = MSGConstants.NO;
						}
					}
					if (flag.equalsIgnoreCase(MSGConstants.YES)) {
						if (ConfigurationLoader.getParameters(false).getProperty("IGST_FLAG").equalsIgnoreCase("Y")) {
							finalAmount = finalAmount + igstAmt;
							common.debitCreditGST(lbrCode,
									String.format("%-8s", MSGConstants.IGST) + "000000000000000000000000", transType,
									setNo, VoucherCommon.getNextScrollNo(), narration, igstAmt, rrn, session,
									MSGConstants.IGST_CASH_FLOW_CR, MSGConstants.DR);
							if (!common.isAborted) {
								String response = VoucherMPOS.updateProductBalances(igstAmt, lbrCode, "D",
										MSGConstants.IGST, session);
								logger.error("response::>>" + response);
								if (response.equalsIgnoreCase(MSGConstants.SUCCESS))
									flag = MSGConstants.YES;
								else {
									logger.error("Product 'IGST' of branch" + lbrCode + "balance upddation fails");
									flag = MSGConstants.NO;
								}
							} else
								flag = MSGConstants.NO;
						}
					}
					if (flag.equalsIgnoreCase(MSGConstants.YES)) {
						if (ConfigurationLoader.getParameters(false).getProperty("CHG_FLAG").equalsIgnoreCase("Y")) {
							finalAmount = finalAmount + chgAmt;
							common.debit(lbrCode, MSGConstants.PREPAID_LOAD_BALANCE_CHARGES_ACCOUNT, transType, setNo,
									VoucherCommon.getNextScrollNo(), narration, chgAmt, rrn, session);
							if (!common.isAborted) {
								String response = VoucherMPOS.updateProductBalances(chgAmt, lbrCode, "D",
										MSGConstants.PREPAID_LOAD_BALANCE_CHARGES_ACCOUNT.substring(0, 8).trim(),
										session);
								logger.error("response::>>" + response);
								if (response.equalsIgnoreCase(MSGConstants.SUCCESS))
									flag = MSGConstants.YES;
								else {
									logger.error("Product 'PL' of branch" + lbrCode + "balance upddation fails");
									flag = MSGConstants.NO;
								}
							} else
								flag = MSGConstants.NO;
						}
					}
					if (flag.equalsIgnoreCase(MSGConstants.YES)) {
						finalAmount = finalAmount + amount;
						System.out.println("finalAmount to be debited::>>" + finalAmount);
						System.out.println("FLAG::>>" + flag);
						logger.error("finalAmount to be debited::>>" + finalAmount);
						if (!common.isAborted && flag.equalsIgnoreCase(MSGConstants.YES)) {
							logger.error("Transaction successful");
							int setNoABB = VoucherCommon.getNextSetNo();
							int scrollNoAbb = VoucherCommon.getNextScrollNo();
							int scrollNoAbb1 = VoucherCommon.getNextScrollNo();
							int scrollNoAbb2 = VoucherCommon.getNextScrollNo();
							common.debitABB(lbrCode, MSGConstants.ABB_ACC, transType.toUpperCase(), setNo, scrollNoAbb,
									narration, lbrCode, amount, rrn, reconNo, session);
							if (!common.isAborted) {
								if (VoucherMPOS
										.updateProductBalances(amount, lbrCode, "D",
												MSGConstants.ABB_ACC.substring(0, 8).trim(), session)
										.equalsIgnoreCase(MSGConstants.SUCCESS)) {
									if (!common.isAborted) {
										logger.error("ABB Transaction successful");
										String crAccno = get32DigitAcctNo(
												getSysParameter(MSGConstants.PREPAID_CARD_ACC).trim(), 0, 0);
										// + " 000000000000000000000000";
										int crBrCode = Integer.valueOf(getSysParameter(MSGConstants.MBBRANCH).trim());
										common.debit(crBrCode, crAccno, MSGConstants.ABB, setNoABB, scrollNoAbb1,
												narration, amount, rrn, session);
										if (!common.isAborted) {
											if (VoucherMPOS
													.updateProductBalances(amount, crBrCode, "D",
															crAccno.substring(0, 8).trim(), session)
													.equalsIgnoreCase(MSGConstants.SUCCESS)) {
												if (!common.isAborted) {
													logger.error("Other Bank GL Transaction successful");
													common.creditABB(crBrCode, MSGConstants.ABB_ACC, MSGConstants.ABB,
															setNoABB, scrollNoAbb2, narration, 2, amount, rrn, reconNo,
															session);
													if (!common.isAborted) {
														if (VoucherMPOS.updateProductBalances(amount, crBrCode, "C",
																MSGConstants.ABB_ACC.substring(0, 8).trim(), session)
																.trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
															Date opdate = DataUtils.getOpenDate(lbrCode);
															String batchCodes[] = Props.getBatchProperty("IMPS")
																	.split("~");
															D001004 d04OnlineBatchName = VoucherMPOS
																	.getBatchNameFromBatchCode(batchCodes[0]);
															String onlineBatchName = d04OnlineBatchName.getValue()
																	.trim();
															String batchCodes1[] = Props.getBatchProperty("ABB")
																	.split("~");
															D001004 d001004 = VoucherMPOS
																	.getBatchNameFromBatchCode(batchCodes1[0]);
															String benBatchCode = d001004.getValue().trim();

															D100001 d100001 = VoucherMPOS.prepareReconObj(lbrCode,
																	reconNo, opdate, 999999, onlineBatchName,
																	benBatchCode, setNo, scrollNoAbb, setNoABB,
																	scrollNoAbb1, scrollNoAbb2, crBrCode, "" + crAccno,
																	Double.valueOf(amount), "D");
															System.out.println("d100001::>>>" + d100001);

															D100002 d100002 = VoucherMPOS.prepareRecon2Obj(lbrCode,
																	reconNo, opdate, 999999, onlineBatchName,
																	benBatchCode, setNo, scrollNoAbb, setNoABB,
																	scrollNoAbb1, scrollNoAbb2, crBrCode, "" + crAccno,
																	Double.valueOf(amount), "D");
															System.out.println("d100002::>>>" + d100002);
															String bal = CoreTransactionMPOS.balance(lbrCode, accNo,
																	finalAmount, "C", session);
															if (bal != null && bal.trim().length() > 0
																	&& !bal.equalsIgnoreCase("51")
																	&& !bal.equalsIgnoreCase("99")) {
																String plAccBal = CoreTransactionMPOS.balance(lbrCode,
																		MSGConstants.PREPAID_LOAD_BALANCE_CHARGES_ACCOUNT,
																		finalAmount, "D", session);
																if (plAccBal != null && plAccBal.trim().length() > 0
																		&& !plAccBal.equalsIgnoreCase("51")
																		&& !plAccBal.equalsIgnoreCase("99")) {
																	try {
																		session.save(d100001);
																		session.save(d100002);
																		session.flush();
																		t.commit();
																		resultMap.put(Code.RESULT, Code.SUCCESS);
																		resultMap.put(Code.SETNO,
																				String.valueOf(setNo));
																		resultMap.put(Code.SCROLLNO,
																				String.valueOf(scrollNo));
																		return resultMap;
																	} catch (Exception e) {
																		e.printStackTrace();
																		logger.error("ABB Transaction unsuccessful");
																		resultMap.put(Code.RESULT, Code.ERROR);
																		resultMap.put(Code.SETNO, "");
																		resultMap.put(Code.SCROLLNO, "");
																		return resultMap;
																	}
																} else {

																	logger.error("ABB Transaction unsuccessful");
																	resultMap.put(Code.RESULT, Code.ERROR);
																	resultMap.put(Code.SETNO, "");
																	resultMap.put(Code.SCROLLNO, "");
																	return resultMap;
																}
															} else {
																logger.error("ABB Transaction unsuccessful");
																resultMap.put(Code.RESULT, Code.ERROR);
																resultMap.put(Code.SETNO, "");
																resultMap.put(Code.SCROLLNO, "");
																return resultMap;
															}
														} else {
															logger.error("ABB Transaction unsuccessful");
															resultMap.put(Code.RESULT, Code.ERROR);
															resultMap.put(Code.SETNO, "");
															resultMap.put(Code.SCROLLNO, "");
															return resultMap;
														}
													} else {
														logger.error("ABB Transaction unsuccessful");
														resultMap.put(Code.RESULT, Code.ERROR);
														resultMap.put(Code.SETNO, "");
														resultMap.put(Code.SCROLLNO, "");
														return resultMap;
													}
												} else {
													logger.error("ABB Transaction unsuccessful");
													resultMap.put(Code.RESULT, Code.ERROR);
													resultMap.put(Code.SETNO, "");
													resultMap.put(Code.SCROLLNO, "");
													return resultMap;
												}
											} else {
												logger.error("ABB Transaction unsuccessful");
												resultMap.put(Code.RESULT, Code.ERROR);
												resultMap.put(Code.SETNO, "");
												resultMap.put(Code.SCROLLNO, "");
												return resultMap;
											}
										} else {
											logger.error("ABB Transaction unsuccessful");
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "");
											resultMap.put(Code.SCROLLNO, "");
											return resultMap;
										}
									} else {
										logger.error("ABB Transaction unsuccessful");
										resultMap.put(Code.RESULT, Code.ERROR);
										resultMap.put(Code.SETNO, "");
										resultMap.put(Code.SCROLLNO, "");
										return resultMap;
									}
								} else {
									logger.error("ABB Transaction unsuccessful");
									resultMap.put(Code.RESULT, Code.ERROR);
									resultMap.put(Code.SETNO, "");
									resultMap.put(Code.SCROLLNO, "");
									return resultMap;
								}
							} else {
								logger.error("ABB Transaction unsuccessful");
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "");
								resultMap.put(Code.SCROLLNO, "");
								return resultMap;
							}
						} else {
							logger.error("Transaction unsuccessful");
							resultMap.put(Code.RESULT, Code.ERROR);
							resultMap.put(Code.SETNO, "");
							resultMap.put(Code.SCROLLNO, "");
							return resultMap;
						}
					} else {
						logger.error("Transaction unsuccessful");
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "");
						resultMap.put(Code.SCROLLNO, "");
						return resultMap;
					}
				} else {
					logger.error("Transaction unsuccessful");
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "");
					resultMap.put(Code.SCROLLNO, "");
					return resultMap;
				}
			} else {
				logger.error("Transaction unsuccessful");
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "");
				resultMap.put(Code.SCROLLNO, "");
				return resultMap;
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ERROR:", e);
			resultMap.put(Code.RESULT, Code.ERROR);
			resultMap.put(Code.SETNO, "");
			resultMap.put(Code.SCROLLNO, "");
			return resultMap;
		} finally {
			session.close();
			session = null;
			t = null;
		}
	}

	public static HashMap<String, String> otherBankLoanCreditVouchers(int lbrCode, String accNo, int benBrCode,
			String benAccNo, String transType, String narration, double amount, String rrn) {
		logger.error("<<<<< ============otherBranchLoanVouchers.service============ >>>>>>");
		VoucherCommon common = new VoucherCommon();
		HashMap<String, String> resultMap = new HashMap<>();
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		try {
			if (lbrCode == benBrCode) {
				logger.error("Same Branch Fund Transfer....");
				int setNo = VoucherCommon.getNextSetNo();
				int mainScrollNo = VoucherCommon.getNextScrollNo();
				common.debitSameBranch(lbrCode, accNo, transType.trim().toUpperCase(), setNo,
						VoucherCommon.getNextScrollNo(), narration, amount, rrn, mainScrollNo, session);
				if (!common.isAborted) {
					if (VoucherMPOS.updateProductBalances(amount, lbrCode, "D", accNo.substring(0, 8).trim(), session)
							.equalsIgnoreCase(MSGConstants.SUCCESS)) {
						String balResponse = CoreTransactionMPOS.balance(lbrCode, accNo.trim(), amount, "D", session);
						if (balResponse != null && balResponse.trim().length() > 0
								&& !balResponse.trim().equalsIgnoreCase("99")
								&& !balResponse.trim().equalsIgnoreCase("51")) {
							if (!common.isAborted) {
								logger.error("Transaction successful");
								if (DataUtils.isLoanAccount(benBrCode, benAccNo.substring(0, 8).trim(), session)) {
									// ====================LOAN Account Voucher ==============================
									D030003 d030003 = DataUtils.getLoanCharges(benBrCode, benAccNo, session);
									if (d030003 == null) {
										if (t.isActive())
											t.rollback();
										logger.error("Loan Parameters not found in D030003");
										resultMap.put(Code.RESULT, Code.ERROR);
										resultMap.put(Code.SETNO, "0");
										resultMap.put(Code.SCROLLNO, "0");
										return resultMap;
									} else {
										Date openDate = getOpenDateNew(lbrCode, session);
										if (openDate == null) {
											if (t.isActive())
												t.rollback();
											logger.error("Open Date Not Found. Aborting Transaction");
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "0");
											resultMap.put(Code.SCROLLNO, "0");
											return resultMap;
										}
										String batchCode = Props.getBatchProperty(transType);
										if (batchCode == null) {
											if (t.isActive())
												t.rollback();
											logger.error(transType + " parameter not found in batch.properties");
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "0");
											resultMap.put(Code.SCROLLNO, "0");
											return resultMap;
										}
										logger.error("Batch Code Form Properties File : " + batchCode);
										String batchCodes[] = batchCode.split("~");
										logger.error("Number of Batches is " + batchCodes.length + "\t Names are "
												+ Arrays.toString(batchCodes));
										if (batchCodes == null || batchCodes.length < 1) {
											if (t.isActive())
												t.rollback();
											logger.error("Batch Codes Not Found in Properties File.");
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "0");
											resultMap.put(Code.SCROLLNO, "0");
											return resultMap;
										}
										D010004 selectedBatch = getSelectedBatchNew(lbrCode, batchCodes, openDate,
												session);
										if (selectedBatch == null) {
											if (t.isActive())
												t.rollback();
											logger.error("No Active Batch Found.");
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "0");
											resultMap.put(Code.SCROLLNO, "0");
											return resultMap;
										}
										D001004 d04OnlineBatchName = VoucherMPOS
												.getBatchNameFromBatchCodeNew(batchCodes[0], session);
										if (d04OnlineBatchName == null) {
											if (t.isActive())
												t.rollback();
											logger.error("System parameters not set properly");
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "0");
											resultMap.put(Code.SCROLLNO, "0");
											return resultMap;
										}
										String onlineBatchName = d04OnlineBatchName.getValue().trim();
										if (onlineBatchName == null) {
											if (t.isActive())
												t.rollback();
											logger.error("System parameters value is null or blank");
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "0");
											resultMap.put(Code.SCROLLNO, "0");
											return resultMap;
										}
										D010004 onlineBatch = getD010004(lbrCode, onlineBatchName, openDate);
										if (onlineBatch == null) {
											if (t.isActive())
												t.rollback();
											logger.error("online batch not found in batch master.");
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "0");
											resultMap.put(Code.SCROLLNO, "0");
											return resultMap;
										}
										amt = amount;
										System.out.println("Starting Amount::>>" + amt);
										if (d030003.getOthChgPrvdFcy() - d030003.getOthChgPaidFcy() != 0
												&& amt > d030003.getOthChgPrvdFcy() - d030003.getOthChgPaidFcy()) {
											amt = amt - d030003.getOthChgPrvdFcy() - d030003.getOthChgPaidFcy();
											D009040 d009040LnoCH = prepareD009040LoanObject(selectedBatch, onlineBatch,
													benBrCode, setNo, VoucherCommon.getNextScrollNo(), mainScrollNo,
													benAccNo, session,
													d030003.getOthChgPrvdFcy() - d030003.getOthChgPaidFcy(), narration,
													MSGConstants.CR, rrn, MSGConstants.LNOCHCR,
													MSGConstants.LOAN_ACTIVITY, MSGConstants.LOAN_TYPE_1);
											session.save(d009040LnoCH);
											D030003 d0300003 = updateBal(benAccNo, benBrCode, session,
													MSGConstants.LOAN_TYPE_1,
													d030003.getOthChgPrvdFcy() - d030003.getOthChgPaidFcy());
											if (d0300003 != null)
												session.update(d0300003);

										}
										if (d030003.getTaxPrvdFcy() - d030003.getTaxPaidFcy() != 0
												&& amt > d030003.getTaxPrvdFcy() - d030003.getTaxPaidFcy()) {
											amt = amt - d030003.getTaxPrvdFcy() - d030003.getTaxPaidFcy();
											D009040 d009040LnoCH = prepareD009040LoanObject(selectedBatch, onlineBatch,
													benBrCode, setNo, VoucherCommon.getNextScrollNo(), mainScrollNo,
													benAccNo, session,
													d030003.getTaxPrvdFcy() - d030003.getTaxPaidFcy(), narration,
													MSGConstants.CR, rrn, MSGConstants.LNTAXCR,
													MSGConstants.LOAN_ACTIVITY, MSGConstants.LOAN_TYPE_2);
											session.save(d009040LnoCH);
											D030003 d0300003 = updateBal(benAccNo, benBrCode, session,
													MSGConstants.LOAN_TYPE_2,
													d030003.getTaxPrvdFcy() - d030003.getTaxPaidFcy());
											if (d0300003 != null)
												session.update(d0300003);
										}
										if (d030003.getPenalPrvdFcy() - d030003.getPenalPaidFcy() != 0
												&& amt > d030003.getPenalPrvdFcy() - d030003.getPenalPaidFcy()) {
											amt = amt - d030003.getPenalPrvdFcy() - d030003.getPenalPaidFcy();
											D009040 d009040LnoCH = prepareD009040LoanObject(selectedBatch, onlineBatch,
													lbrCode, setNo, VoucherCommon.getNextScrollNo(), mainScrollNo,
													benAccNo, session,
													d030003.getPenalPrvdFcy() - d030003.getPenalPaidFcy(), narration,
													MSGConstants.CR, rrn, MSGConstants.LNPINTCR,
													MSGConstants.LOAN_ACTIVITY, MSGConstants.LOAN_TYPE_3);
											session.save(d009040LnoCH);
											D030003 d0300003 = updateBal(benAccNo, benBrCode, session,
													MSGConstants.LOAN_TYPE_3,
													d030003.getPenalPrvdFcy() - d030003.getPenalPaidFcy());
											if (d0300003 != null)
												session.update(d0300003);
										}
										if (amt > 0) {
											D009040 d009040LnoCH = prepareD009040LoanObject(selectedBatch, onlineBatch,
													benBrCode, setNo, VoucherCommon.getNextScrollNo(), mainScrollNo,
													benAccNo, session, amt, narration, MSGConstants.CR, rrn,
													MSGConstants.LNPCR, MSGConstants.LOAN_ACTIVITY,
													MSGConstants.LOAN_TYPE_4);
											session.save(d009040LnoCH);
											D030003 d0300003 = updateBal(benAccNo, benBrCode, session, "4", amt);
											if (d0300003 != null)
												session.update(d0300003);
										}
									}
								}
								if (!common.isAborted) {
									if (VoucherMPOS
											.updateProductBalances(amount, benBrCode, "C",
													benAccNo.substring(0, 8).trim(), session)
											.equalsIgnoreCase(MSGConstants.SUCCESS)) {
										String bal = CoreTransactionMPOS.balance(benBrCode, benAccNo.trim(), amount,
												"C", session);
										if (bal != null && bal.trim().length() > 0 && !bal.trim().equalsIgnoreCase("51")
												&& !bal.trim().equalsIgnoreCase("99")) {
											t.commit();
											resultMap.put(Code.RESULT, Code.SUCCESS);
											resultMap.put(Code.SETNO, String.valueOf(setNo));
											resultMap.put(Code.SCROLLNO, String.valueOf(mainScrollNo));
											return resultMap;
										} else {
											if (t.isActive())
												t.rollback();
											logger.error("Transaction unsuccessful");
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "0");
											resultMap.put(Code.SCROLLNO, "0");
											return resultMap;
										}
									} else {
										if (t.isActive())
											t.rollback();
										logger.error("Transaction unsuccessful");
										resultMap.put(Code.RESULT, Code.ERROR);
										resultMap.put(Code.SETNO, "0");
										resultMap.put(Code.SCROLLNO, "0");
										return resultMap;
									}
								} else {
									if (t.isActive())
										t.rollback();
									logger.error("Transaction unsuccessful");
									resultMap.put(Code.RESULT, Code.ERROR);
									resultMap.put(Code.SETNO, "0");
									resultMap.put(Code.SCROLLNO, "0");
									return resultMap;
								}
							} else {
								if (t.isActive())
									t.rollback();
								logger.error("Transaction unsuccessful");
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "0");
								resultMap.put(Code.SCROLLNO, "0");
								return resultMap;
							}
						} else {
							if (t.isActive())
								t.rollback();
							logger.error("Transaction unsuccessful");
							resultMap.put(Code.RESULT, Code.ERROR);
							resultMap.put(Code.SETNO, "0");
							resultMap.put(Code.SCROLLNO, "0");
							return resultMap;
						}
					} else {
						if (t.isActive())
							t.rollback();
						logger.error("Transaction unsuccessful");
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "0");
						resultMap.put(Code.SCROLLNO, "0");
						return resultMap;
					}
				} else {
					if (t.isActive())
						t.rollback();
					logger.error("Transaction unsuccessful");
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "0");
					resultMap.put(Code.SCROLLNO, "0");
					return resultMap;
				}
			} else {
				logger.error("Other Branch Fund Transfer....");
				int setNo = VoucherCommon.getNextSetNo();
				int reconNo = VoucherCommon.getNextReconNo(lbrCode);
				int scrollNo = VoucherCommon.getNextScrollNo();
				/*
				 * String drAccno = getSysParameter(MSGConstants.MBBENCRACC).trim() +
				 * "    000000000000000000000000"; int crBrcode =
				 * Integer.valueOf(getSysParameter(MSGConstants.MBBRANCH).trim()); int reconNo =
				 * getNextReconNo(crBrcode); logger.error("accNo::>>" + accNo);
				 * logger.error("lbrCode::>>" + lbrCode); common.credit(lbrCode, accNo,
				 * MSGConstants.ABB, setNo, scrollNo, narration, amount, rrn, session);
				 */

				common.debit(lbrCode, accNo, transType.toUpperCase(), setNo, scrollNo, narration, amount, rrn, session);
				if (!common.isAborted) {
					if (VoucherMPOS.updateProductBalances(amount, lbrCode, "D", accNo.substring(0, 8).trim(), session)
							.equalsIgnoreCase(MSGConstants.SUCCESS)) {
						String balResponse = CoreTransactionMPOS.balance(lbrCode, accNo, amount, "D", session);
						if (balResponse != null && balResponse.trim().length() > 0
								&& !balResponse.equalsIgnoreCase("99") && !balResponse.equalsIgnoreCase("51")) {
							if (!common.isAborted) {
								int setNoABB = VoucherCommon.getNextSetNo();
								int scrollNoAbb = VoucherCommon.getNextScrollNo();
								int scrollNoAbb1 = VoucherCommon.getNextScrollNo();
								int scrollNoAbb2 = VoucherCommon.getNextScrollNo();
								common.creditABB(lbrCode, MSGConstants.ABB_ACC, transType.toUpperCase(), setNo,
										scrollNoAbb, narration, benBrCode, amount, rrn, reconNo, session);
								if (!common.isAborted) {
									if (VoucherMPOS.updateProductBalances(amount, lbrCode, "C", MSGConstants.ABB.trim(),
											session).equalsIgnoreCase(MSGConstants.SUCCESS)) {
										if (!common.isAborted) {
											logger.error("ABB Transaction successful");
											common.credit(benBrCode, benAccNo, MSGConstants.ABB, setNoABB, scrollNoAbb1,
													narration, amount, rrn, session);
											if (!common.isAborted) {
												if (VoucherMPOS
														.updateProductBalances(amount, benBrCode, "C",
																benAccNo.substring(0, 8).trim(), session)
														.equalsIgnoreCase(MSGConstants.SUCCESS)) {
													String bal = CoreTransactionMPOS.balance(benBrCode, benAccNo,
															amount, "C", session);
													if (bal != null && bal.trim().length() > 0
															&& !bal.trim().equalsIgnoreCase("51")
															&& !bal.trim().equalsIgnoreCase("99")) {
														if (!common.isAborted) {
															logger.error("Other Bank GL Transaction successful");
															common.debitABB(benBrCode, MSGConstants.ABB_ACC, "ABB",
																	setNoABB, scrollNoAbb2, narration, lbrCode, amount,
																	rrn, reconNo, session);
															if (!common.isAborted) {
																if (VoucherMPOS
																		.updateProductBalances(amount, benBrCode, "D",
																				MSGConstants.ABB_ACC.substring(0, 8)
																						.trim(),
																				session)
																		.equalsIgnoreCase(MSGConstants.SUCCESS)) {
																	Date opdate = DataUtils.getOpenDate(lbrCode);
																	if (opdate == null) {
																		if (t.isActive())
																			t.rollback();
																		logger.error(
																				"Operaton Date not found for branch "
																						+ lbrCode);
																		resultMap.put(Code.RESULT, Code.ERROR);
																		resultMap.put(Code.SETNO, "");
																		resultMap.put(Code.SCROLLNO, "");
																		return resultMap;
																	}
																	String batchCodes[] = Props.getBatchProperty("IMPS")
																			.split("~");
																	if (batchCodes == null || batchCodes.length == 0) {
																		if (t.isActive())
																			t.rollback();
																		logger.error(
																				"Batch code not in batch.properties "
																						+ lbrCode);
																		resultMap.put(Code.RESULT, Code.ERROR);
																		resultMap.put(Code.SETNO, "");
																		resultMap.put(Code.SCROLLNO, "");
																		return resultMap;
																	}
																	D001004 d04OnlineBatchName = VoucherMPOS
																			.getBatchNameFromBatchCode(batchCodes[0]);
																	if (d04OnlineBatchName == null) {
																		if (t.isActive())
																			t.rollback();
																		logger.error("System parameter not found for "
																				+ batchCodes[0]);
																		resultMap.put(Code.RESULT, Code.ERROR);
																		resultMap.put(Code.SETNO, "");
																		resultMap.put(Code.SCROLLNO, "");
																		return resultMap;
																	}
																	String onlineBatchName = d04OnlineBatchName
																			.getValue().trim();
																	if (onlineBatchName == null) {
																		if (t.isActive())
																			t.rollback();
																		logger.error("System parameter not found for "
																				+ batchCodes[0]);
																		resultMap.put(Code.RESULT, Code.ERROR);
																		resultMap.put(Code.SETNO, "");
																		resultMap.put(Code.SCROLLNO, "");
																		return resultMap;
																	}
																	String batchCodes1[] = Props.getBatchProperty("ABB")
																			.split("~");
																	if (batchCodes1 == null || batchCodes1.length < 1) {
																		if (t.isActive())
																			t.rollback();
																		logger.error(
																				"Batch Code not found in properties file. ");
																		resultMap.put(Code.RESULT, Code.ERROR);
																		resultMap.put(Code.SETNO, "");
																		resultMap.put(Code.SCROLLNO, "");
																		return resultMap;
																	}
																	D001004 d001004 = VoucherMPOS
																			.getBatchNameFromBatchCode(batchCodes1[0]);
																	if (d001004 == null) {
																		if (t.isActive())
																			t.rollback();
																		logger.error(batchCodes1[0]
																				+ " System parameter not found");
																		resultMap.put(Code.RESULT, Code.ERROR);
																		resultMap.put(Code.SETNO, "");
																		resultMap.put(Code.SCROLLNO, "");
																		return resultMap;
																	}
																	String benBatchCode = d001004.getValue().trim();
																	if (benBatchCode == null
																			|| benBatchCode.trim().isEmpty()) {
																		if (t.isActive())
																			t.rollback();
																		logger.error("System parameter" + batchCodes1[0]
																				+ " value is null or blank");
																		resultMap.put(Code.RESULT, Code.ERROR);
																		resultMap.put(Code.SETNO, "");
																		resultMap.put(Code.SCROLLNO, "");
																		return resultMap;
																	}
																	D100001 d100001 = VoucherMPOS.prepareReconObj(
																			lbrCode, reconNo, opdate, 999999,
																			onlineBatchName, benBatchCode, setNo,
																			scrollNoAbb, setNoABB, scrollNoAbb1,
																			scrollNoAbb2, benBrCode, "" + benAccNo,
																			Double.valueOf(amount), "D");
																	if (d100001 == null) {
																		if (t.isActive())
																			t.rollback();
																		logger.error(
																				"unable to prepare recon object (D100001)");
																		resultMap.put(Code.RESULT, Code.ERROR);
																		resultMap.put(Code.SETNO, "");
																		resultMap.put(Code.SCROLLNO, "");
																		return resultMap;
																	}
																	D100002 d100002 = VoucherMPOS.prepareRecon2Obj(
																			lbrCode, reconNo, opdate, 999999,
																			onlineBatchName, benBatchCode, setNo,
																			scrollNoAbb, setNoABB, scrollNoAbb1,
																			scrollNoAbb2, benBrCode, "" + benAccNo,
																			Double.valueOf(amount), "D");//
																	if (d100002 == null) {
																		if (t.isActive())
																			t.rollback();
																		logger.error(
																				"unable to prepare recon object (D100002)");
																		resultMap.put(Code.RESULT, Code.ERROR);
																		resultMap.put(Code.SETNO, "");
																		resultMap.put(Code.SCROLLNO, "");
																		return resultMap;
																	}
																	session.save(d100001);
																	session.save(d100002);
																	session.flush();
																	t.commit();
																	resultMap.put(Code.RESULT, Code.SUCCESS);
																	resultMap.put(Code.SETNO, String.valueOf(setNo));
																	resultMap.put(Code.SCROLLNO,
																			String.valueOf(scrollNo));
																	return resultMap;
																} else {
																	if (t.isActive())
																		t.rollback();
																	logger.error(
																			"common.isAborted::>>" + common.isAborted);
																	logger.error("ABB Transaction unsuccessful");
																	resultMap.put(Code.RESULT, Code.ERROR);
																	resultMap.put(Code.SETNO, "");
																	resultMap.put(Code.SCROLLNO, "");
																	return resultMap;
																}
															} else {
																if (t.isActive())
																	t.rollback();
																logger.error("common.isAborted::>>" + common.isAborted);
																logger.error("ABB Transaction unsuccessful");
																resultMap.put(Code.RESULT, Code.ERROR);
																resultMap.put(Code.SETNO, "");
																resultMap.put(Code.SCROLLNO, "");
																return resultMap;
															}
														} else {
															if (t.isActive())
																t.rollback();
															logger.error("common.isAborted::>>" + common.isAborted);
															logger.error("ABB Transaction unsuccessful");
															resultMap.put(Code.RESULT, Code.ERROR);
															resultMap.put(Code.SETNO, "");
															resultMap.put(Code.SCROLLNO, "");
															return resultMap;
														}
													} else {
														if (t.isActive())
															t.rollback();
														logger.error("common.isAborted::>>" + common.isAborted);
														logger.error("ABB Transaction unsuccessful");
														resultMap.put(Code.RESULT, Code.ERROR);
														resultMap.put(Code.SETNO, "");
														resultMap.put(Code.SCROLLNO, "");
														return resultMap;
													}
												} else {
													if (t.isActive())
														t.rollback();
													logger.error("common.isAborted::>>" + common.isAborted);
													logger.error("ABB Transaction unsuccessful");
													resultMap.put(Code.RESULT, Code.ERROR);
													resultMap.put(Code.SETNO, "");
													resultMap.put(Code.SCROLLNO, "");
													return resultMap;
												}
											} else {
												if (t.isActive())
													t.rollback();
												logger.error("common.isAborted::>>" + common.isAborted);
												logger.error("ABB Transaction unsuccessful");
												resultMap.put(Code.RESULT, Code.ERROR);
												resultMap.put(Code.SETNO, "");
												resultMap.put(Code.SCROLLNO, "");
												return resultMap;
											}
										} else {
											if (t.isActive())
												t.rollback();
											logger.error("common.isAborted::>>" + common.isAborted);
											logger.error("ABB Transaction unsuccessful");
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "");
											resultMap.put(Code.SCROLLNO, "");
											return resultMap;
										}
									} else {
										if (t.isActive())
											t.rollback();
										logger.error("ABB Transaction unsuccessful");
										resultMap.put(Code.RESULT, Code.ERROR);
										resultMap.put(Code.SETNO, "");
										resultMap.put(Code.SCROLLNO, "");
										return resultMap;
									}
								} else {
									if (t.isActive())
										t.rollback();
									logger.error("ABB Transaction unsuccessful");
									resultMap.put(Code.RESULT, Code.ERROR);
									resultMap.put(Code.SETNO, "");
									resultMap.put(Code.SCROLLNO, "");
									return resultMap;
								}
							} else {
								if (t.isActive())
									t.rollback();
								logger.error("Transaction unsuccessful");
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "");
								resultMap.put(Code.SCROLLNO, "");
								return resultMap;
							}
						} else {
							if (t.isActive())
								t.rollback();
							logger.error("Transaction unsuccessful");
							resultMap.put(Code.RESULT, Code.ERROR);
							resultMap.put(Code.SETNO, "");
							resultMap.put(Code.SCROLLNO, "");
							return resultMap;
						}
					} else {
						if (t.isActive())
							t.rollback();
						logger.error("Transaction unsuccessful");
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "");
						resultMap.put(Code.SCROLLNO, "");
						return resultMap;
					}
				} else {
					if (t.isActive())
						t.rollback();
					logger.error("Transaction unsuccessful");
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "");
					resultMap.put(Code.SCROLLNO, "");
					return resultMap;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			if (t.isActive())
				t.rollback();
			e.printStackTrace();
			logger.error("ERROR", e);
			resultMap.put(Code.RESULT, Code.ERROR);
			resultMap.put(Code.SETNO, "");
			resultMap.put(Code.SCROLLNO, "");
			return resultMap;
		} finally {
			session.close();
			session = null;
			t = null;
		}
	}

	public static HashMap<String, String> rtgsNeftVouchers(int lbrCode, String accNo, int benBrCode, String benAccNo,
			String transType, String narration, double amount, String rrn, Session session, D946020 rtgsMessages) {
		logger.error("Parametrs Received as lbrCode::>>" + lbrCode + " accNo::>>" + accNo + " benBrCode::>>>"
				+ benBrCode + " benAccNo::>>" + benAccNo + " transType::>>" + transType + " narration::>>" + narration
				+ " amount::>>" + amount + " RRN::>>" + rrn);
		System.out.println("Parametrs Received as lbrCode::>>" + lbrCode + " accNo::>>" + accNo + " benBrCode::>>>"
				+ benBrCode + " benAccNo::>>" + benAccNo + " transType::>>" + transType + " narration::>>" + narration
				+ " amount::>>" + amount + " RRN::>>" + rrn);
		rrn = rrn.substring(4);
		VoucherCommon common = new VoucherCommon();
		HashMap<String, String> resultMap = new HashMap<>();
		if (lbrCode == benBrCode) {
			logger.error("Same Branch Fund Transfer....");
			int setNo = VoucherMPOS.getNextSetNo();
			int mainScrollNo = VoucherMPOS.getNextScrollNo();
			D009040 debitVoucher, creditVoucher;
			debitVoucher = common.rtgsNeftDebitSameBranch(lbrCode, accNo, transType.toUpperCase(), setNo,
					VoucherCommon.getNextScrollNo(), narration, amount, rrn, mainScrollNo, session);
			System.out.println("VoucherCommon.otherBranchVouchers() common.isAborted " + common.isAborted);
			if (!common.isAborted) {
				if (VoucherMPOS.updateProductBalances(amount, lbrCode, "D", accNo.substring(0, 8).trim(), session)
						.equalsIgnoreCase(MSGConstants.SUCCESS)) {
					D009021 prdCdDbtAcc = session.get(D009021.class,
							new D009021Id(lbrCode, accNo.substring(0, 8).trim())); // product level accounts should not
																					// be processed for balance
																					// update.change by Manish
					String balResponse = "";
					if (!"P".equalsIgnoreCase(prdCdDbtAcc.getAcctOpenLevel() + "")) {
						balResponse = CoreTransactionMPOS.balance(lbrCode, accNo.trim(), amount, "D", session);
					}
					if ((balResponse != null && balResponse.trim().length() > 0
							&& !balResponse.trim().equalsIgnoreCase("99") && !balResponse.trim().equalsIgnoreCase("51"))
							|| "P".equalsIgnoreCase(prdCdDbtAcc.getAcctOpenLevel() + "")) {
						if (!common.isAborted) {
							logger.error("Transaction successful");
							creditVoucher = common.rtgsNeftcreditSameBranch(benBrCode, benAccNo,
									transType.toUpperCase(), setNo, VoucherCommon.getNextScrollNo(), narration, amount,
									rrn, mainScrollNo, session);
							if (!common.isAborted) {
								if (VoucherMPOS.updateProductBalances(amount, benBrCode, "C",
										benAccNo.substring(0, 8).trim(), session)
										.equalsIgnoreCase(MSGConstants.SUCCESS)) {
									D009021 prdCd = session.get(D009021.class,
											new D009021Id(benBrCode, benAccNo.substring(0, 8).trim())); // change By
																										// Manish.
																										// balace
																										// updation
																										// method should
																										// not be called
																										// on
																										// Productlevel
																										// Accounts
									String bal = "";
									if (!"P".equalsIgnoreCase(prdCd.getAcctOpenLevel() + "")) {
										bal = CoreTransactionMPOS.balance(benBrCode, benAccNo.trim(), amount, "C",
												session);
									}

									if ((bal != null && bal.trim().length() > 0 && !bal.trim().equalsIgnoreCase("51")
											&& !bal.trim().equalsIgnoreCase("99"))
											|| "P".equalsIgnoreCase(prdCd.getAcctOpenLevel() + "")) {

										RtgsMessageSecondaryDatFile rtgsDataFile = common
												.setRtgsMessageSecondaryDatFile(creditVoucher, debitVoucher,
														creditVoucher, rtgsMessages);
										D946320Id id = new D946320Id(rtgsMessages.getId().getObrCode(),
												rtgsMessages.getId().getIwOwMsg() + "",
												rtgsMessages.getId().getMsgStype(), rtgsMessages.getId().getMsgDate(),
												rtgsMessages.getId().getRefNo());
										D946320 rtgsBkupData = new D946320(id, " ", " ", " ", rtgsMessages.getUtrno(),
												rtgsMessages.getUtrseqNo());
										try {
											session.save(rtgsDataFile);
											session.save(rtgsBkupData);
											resultMap.put(Code.RESULT, Code.SUCCESS);
											resultMap.put(Code.SETNO, String.valueOf(setNo));
											resultMap.put(Code.SCROLLNO, String.valueOf(mainScrollNo));
											return resultMap;
										} catch (Exception e) {
											session.getTransaction().rollback();
											e.printStackTrace();
											logger.error("ERROR:", e);
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "");
											resultMap.put(Code.SCROLLNO, "");
											return resultMap;
										}

									} else {
										logger.error("Transaction unsuccessful");
										session.getTransaction().rollback();
										resultMap.put(Code.RESULT, Code.ERROR);
										resultMap.put(Code.SETNO, "0");
										resultMap.put(Code.SCROLLNO, "0");
										return resultMap;
									}

								} else {
									logger.error("Transaction unsuccessful");
									session.getTransaction().rollback();
									resultMap.put(Code.RESULT, Code.ERROR);
									resultMap.put(Code.SETNO, "0");
									resultMap.put(Code.SCROLLNO, "0");
									return resultMap;
								}
							} else {
								logger.error("Transaction unsuccessful");
								session.getTransaction().rollback();
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "0");
								resultMap.put(Code.SCROLLNO, "0");
								return resultMap;
							}
						} else {
							logger.error("Transaction unsuccessful");
							session.getTransaction().rollback();
							resultMap.put(Code.RESULT, Code.ERROR);
							resultMap.put(Code.SETNO, "0");
							resultMap.put(Code.SCROLLNO, "0");
							return resultMap;
						}
					} else {
						logger.error("Transaction unsuccessful");
						session.getTransaction().rollback();
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "0");
						resultMap.put(Code.SCROLLNO, "0");
						return resultMap;
					}
				} else {
					logger.error("Transaction unsuccessful");
					session.getTransaction().rollback();
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "0");
					resultMap.put(Code.SCROLLNO, "0");
					return resultMap;
				}
			} else {
				logger.error("Transaction unsuccessful");
				session.getTransaction().rollback();
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "0");
				resultMap.put(Code.SCROLLNO, "0");
				return resultMap;
			}
		} else {
			logger.error("Other Branch Fund Transfer....");
			int setNo = VoucherMPOS.getNextSetNo();
			int reconNo = VoucherMPOS.getNextReconNo(lbrCode);
			int scrollNo = VoucherMPOS.getNextScrollNo();
			D009040 debitVoucher = null, creditVoucher = null, creditABBVoucher = null, debitABBVoucher = null;
			debitVoucher = common.rtgsNeftdebit(lbrCode, accNo, transType.toUpperCase(), setNo, scrollNo, narration,
					amount, rrn, session);
			if (!common.isAborted) {
				if (VoucherMPOS.updateProductBalances(amount, lbrCode, "D", accNo.substring(0, 8).trim(), session)
						.equalsIgnoreCase(MSGConstants.SUCCESS)) {
					logger.error("------------Debit Voucher Details::>>> " + debitVoucher.getId().toString());
					D009021 prdCdDbtAcc = session.get(D009021.class,
							new D009021Id(lbrCode, accNo.substring(0, 8).trim()));
					String balResponse = "";
					if (!"P".equalsIgnoreCase(prdCdDbtAcc.getAcctOpenLevel() + "")) {
						balResponse = CoreTransactionMPOS.balance(lbrCode, accNo, amount, "D", session);
					}

					if ((balResponse != null && balResponse.trim().length() > 0 && !balResponse.equalsIgnoreCase("99")
							&& !balResponse.equalsIgnoreCase("51"))
							|| "P".equalsIgnoreCase(prdCdDbtAcc.getAcctOpenLevel() + "")) {
						if (!common.isAborted) {
							int setNoABB = VoucherMPOS.getNextSetNo();
							int scrollNoAbb = VoucherMPOS.getNextScrollNo();
							int scrollNoAbb1 = VoucherMPOS.getNextScrollNo();
							int scrollNoAbb2 = VoucherMPOS.getNextScrollNo();
							logger.error("Transaction successful");
							creditABBVoucher = common.rtgsNeftCreditABB(lbrCode, MSGConstants.ABB_ACC,
									transType.toUpperCase(), setNo, scrollNoAbb, narration, benBrCode, amount, rrn,
									reconNo, session);
							if (!common.isAborted) {
								if (VoucherMPOS
										.updateProductBalances(amount, lbrCode, "C", MSGConstants.ABB.trim(), session)
										.equalsIgnoreCase(MSGConstants.SUCCESS)) {
									if (!common.isAborted) {
										logger.error("ABB Transaction successful");

										creditVoucher = common.rtgsNeftCredit(benBrCode, benAccNo, MSGConstants.ABB,
												setNoABB, scrollNoAbb1, "OBr=" + lbrCode + "-"+accNo.substring(0,8).trim()+"-"+Integer.parseInt(accNo.substring(16, 24))+"/" + narration, amount, rrn,
												session);
										if (!common.isAborted) {
											/*** Added By Aniket Desai for 24X7 on 3rd Jan, 2020 ***/
											// if(debitVoucher.getValueDate().compareTo(creditVoucher.getValueDate())==0)
											// {

											if (VoucherMPOS
													.updateProductBalances(amount, benBrCode, "C",
															benAccNo.substring(0, 8).trim(), session)
													.equalsIgnoreCase(MSGConstants.SUCCESS)) {
												logger.error("------------Credit Voucher Details::>>> "
														+ creditVoucher.getId().toString());
												D009021 prdCdBen = session.get(D009021.class,
														new D009021Id(benBrCode, benAccNo.substring(0, 8).trim()));
												String bal = "";
												if (!"P".equalsIgnoreCase(prdCdBen.getAcctOpenLevel() + "")) {
													bal = CoreTransactionMPOS.balance(benBrCode, benAccNo, amount, "C",
															session);
												}

												if ((bal != null && bal.trim().length() > 0
														&& !bal.trim().equalsIgnoreCase("51")
														&& !bal.trim().equalsIgnoreCase("99"))
														|| "P".equalsIgnoreCase(prdCdBen.getAcctOpenLevel() + "")) {
													if (!common.isAborted) {
														logger.error("Other Bank GL Transaction successful");
														debitABBVoucher = common.rtgsNeftDebitABB(benBrCode,
																MSGConstants.ABB_ACC, "ABB", setNoABB, scrollNoAbb2,
																"AcctId=" + benAccNo + " FromBrCd=" + lbrCode
																		+ " ReconNo=" + reconNo,
																lbrCode, amount, rrn, reconNo, session);
														if (!common.isAborted) {
															if (VoucherMPOS
																	.updateProductBalances(amount, benBrCode, "D",
																			MSGConstants.ABB_ACC.substring(0, 8).trim(),
																			session)
																	.equalsIgnoreCase(MSGConstants.SUCCESS)) {
																Date opdate = DataUtils.getOpenDate(lbrCode);
																String onlineBatchName = creditABBVoucher.getId()
																		.getBatchCd().trim();
																String benBatchCode = debitABBVoucher.getId()
																		.getBatchCd().trim();

																D100001 d100001 = VoucherMPOS.prepareReconObj(lbrCode,
																		reconNo, opdate, 999999, onlineBatchName,
																		benBatchCode, setNo, scrollNoAbb, setNoABB,
																		scrollNoAbb1, scrollNoAbb2, benBrCode,
																		"" + benAccNo, Double.valueOf(amount), "D");
																System.out.println("d100001::>>>" + d100001);

																D100002 d100002 = VoucherMPOS.prepareRecon2Obj(lbrCode,
																		reconNo, opdate, 999999, onlineBatchName,
																		benBatchCode, setNo, scrollNoAbb, setNoABB,
																		scrollNoAbb1, scrollNoAbb2, benBrCode,
																		"" + benAccNo, Double.valueOf(amount), "D");//
																System.out.println("d100002::>>>" + d100002);
																RtgsMessageSecondaryDatFile rtgsDataFile = common
																		.setRtgsMessageSecondaryDatFile(
																				creditABBVoucher, debitABBVoucher,
																				creditVoucher, rtgsMessages);
																D946320Id id = new D946320Id(
																		rtgsMessages.getId().getObrCode(),
																		rtgsMessages.getId().getIwOwMsg() + "",
																		rtgsMessages.getId().getMsgStype(),
																		rtgsMessages.getId().getMsgDate(),
																		rtgsMessages.getId().getRefNo());
																D946320 rtgsBkupData = new D946320(id, " ", " ", " ",
																		rtgsMessages.getUtrno(),
																		rtgsMessages.getUtrseqNo());
																try {
																	session.save(d100001);
																	session.save(d100002);
																	session.save(rtgsDataFile);
																	session.save(rtgsBkupData);
																	resultMap.put(Code.RESULT, Code.SUCCESS);
																	resultMap.put(Code.SETNO, String.valueOf(setNo));
																	resultMap.put(Code.SCROLLNO,
																			String.valueOf(scrollNo));
																	return resultMap;
																} catch (Exception e) {
																	session.getTransaction().rollback();
																	e.printStackTrace();
																	logger.error("ERROR:", e);
																	resultMap.put(Code.RESULT, Code.ERROR);
																	resultMap.put(Code.SETNO, "");
																	resultMap.put(Code.SCROLLNO, "");
																	return resultMap;
																}
															} else {
																session.getTransaction().rollback();
																logger.error("common.isAborted::>>" + common.isAborted);
																logger.error("ABB Transaction unsuccessful");
																resultMap.put(Code.RESULT, Code.ERROR);
																resultMap.put(Code.SETNO, "");
																resultMap.put(Code.SCROLLNO, "");
																return resultMap;
															}
														} else {
															session.getTransaction().rollback();
															logger.error("common.isAborted::>>" + common.isAborted);
															logger.error("ABB Transaction unsuccessful");
															resultMap.put(Code.RESULT, Code.ERROR);
															resultMap.put(Code.SETNO, "");
															resultMap.put(Code.SCROLLNO, "");
															return resultMap;
														}
													} else {
														session.getTransaction().rollback();
														logger.error("common.isAborted::>>" + common.isAborted);
														logger.error("ABB Transaction unsuccessful");
														resultMap.put(Code.RESULT, Code.ERROR);
														resultMap.put(Code.SETNO, "");
														resultMap.put(Code.SCROLLNO, "");
														return resultMap;
													}
												} else {
													session.getTransaction().rollback();
													logger.error("common.isAborted::>>" + common.isAborted);
													logger.error("ABB Transaction unsuccessful");
													resultMap.put(Code.RESULT, Code.ERROR);
													resultMap.put(Code.SETNO, "");
													resultMap.put(Code.SCROLLNO, "");
													return resultMap;
												}
											} else {
												logger.error("common.isAborted::>>" + common.isAborted);
												logger.error("ABB Transaction unsuccessful");
												session.getTransaction().rollback();
												resultMap.put(Code.RESULT, Code.ERROR);
												resultMap.put(Code.SETNO, "");
												resultMap.put(Code.SCROLLNO, "");
												return resultMap;
											}
											/*
											 * } else { logger.error("common.isAborted::>>" + common.isAborted);
											 * session.getTransaction().rollback();
											 * logger.error("ABB Transaction unsuccessful"); resultMap.put(Code.RESULT,
											 * Code.ERROR); resultMap.put(Code.SETNO, ""); resultMap.put(Code.SCROLLNO,
											 * ""); return resultMap; }
											 */
										} else {
											logger.error("common.isAborted::>>" + common.isAborted);
											session.getTransaction().rollback();
											logger.error("Batches Post Date Not Same");
											/****
											 * change By Manish Bolbanda on 08-01-2020.if PostDates arent same then
											 * result is set as below.
											 ****/
											resultMap.put(Code.RESULT, "postDateUnmatched");
											resultMap.put(Code.SETNO, "");
											resultMap.put(Code.SCROLLNO, "");

											return resultMap;
										}
									} else {
										logger.error("common.isAborted::>>" + common.isAborted);
										logger.error("ABB Transaction unsuccessful");
										session.getTransaction().rollback();
										resultMap.put(Code.RESULT, Code.ERROR);
										resultMap.put(Code.SETNO, "");
										resultMap.put(Code.SCROLLNO, "");
										return resultMap;
									}
								} else {
									logger.error("ABB Transaction unsuccessful");
									session.getTransaction().rollback();
									resultMap.put(Code.RESULT, Code.ERROR);
									resultMap.put(Code.SETNO, "");
									resultMap.put(Code.SCROLLNO, "");
									return resultMap;
								}
							} else {
								logger.error("ABB Transaction unsuccessful");
								session.getTransaction().rollback();
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "");
								resultMap.put(Code.SCROLLNO, "");
								return resultMap;
							}
						} else {
							logger.error("Transaction unsuccessful");
							session.getTransaction().rollback();
							resultMap.put(Code.RESULT, Code.ERROR);
							resultMap.put(Code.SETNO, "");
							resultMap.put(Code.SCROLLNO, "");
							return resultMap;
						}
					} else {
						logger.error("Transaction unsuccessful");
						session.getTransaction().rollback();
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "");
						resultMap.put(Code.SCROLLNO, "");
						return resultMap;
					}
				} else {
					logger.error("Transaction unsuccessful");
					session.getTransaction().rollback();
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "");
					resultMap.put(Code.SCROLLNO, "");
					return resultMap;
				}
			} else {
				logger.error("Transaction unsuccessful");
				session.getTransaction().rollback();
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "");
				resultMap.put(Code.SCROLLNO, "");
				return resultMap;
			}
		}
	}

	public D009040 rtgsNeftcreditSameBranch(int brCode, String acctId, String tType, int setNo, int scrollNo,
			String narration, double amount, String rrn, int mainSrollNo, Session session) {
		try {
			Date openDate = getOpenDateNew(brCode, session); // Get Open Date
			if (openDate == null) {
				Logger.error("Open Date Not Found. Aborting Transaction");
				session.getTransaction().rollback();
				isAborted = true;
				return null;
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
				session.getTransaction().rollback();
				return null;
			}

			/// Get Selected Batch.
			D010004 selectedBatch = getSelectedBatchNew(brCode, batchCodes, openDate, session);
			if (selectedBatch == null) {
				Logger.error("No Active Batch Found.");
				isAborted = true;
				session.getTransaction().rollback();
				return null;
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
			d40.setMainScrollNo(mainSrollNo); // Scroll No
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
			d40.setActivityType("RTGSOW");
			d40.setCashFlowType("CR");
			d40.setValueDate(selectedBatch.getPostDate()); // postdate SELECT *
															// FROM D010004
															// WHERE LBrCode =9
															// AND EntryDate =
															// '19-APR-2016' AND
															// BatchCd = 'ABBTR'

			String bookType = getBookTypeNew(brCode, selectedBatch.getId().getBatchCd(), session);
			if (bookType == null) {
				Logger.error("BookType Not Found.");
				isAborted = true;
				return null;
			}
			logger.error("BookType is : " + bookType);
			d40.setBookType(bookType); // SELECT BookType FROM D010001 WHERE
										// LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('C');
			d40.setVcrAcctId(acctId);
			d40.setMainAcctId(acctId);

			short moduleType = getModuleTypeNew(brCode, acctId.substring(0, 8).trim(), session);
			if (moduleType == 0) {
				Logger.error("ModuleType Not Found.");
				isAborted = true;
				session.getTransaction().rollback();
				return null;
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
			d40.setInstrNo(rrn.length() > 12 ? rrn.substring(rrn.length() - 12, rrn.length()) : rrn.trim()); // RRN
			d40.setInstrDate(new Date()); // Blank
			d40.setParticulars(narration.length() > 70 ? narration.substring(0, 70) : narration.trim()); // param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); //
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');

			int usrCode2 = Integer.parseInt(ConfigurationLoader.getParameters(false).getProperty("IMPS_USER"));
			if (usrCode2 == 0) {
				Logger.error("UsrCode Not Found.");
				isAborted = true;
				return null;
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
			if (updateBatchBalance(session, amount, selectedBatch, brCode, MSGConstants.CR))
				isAborted = false;
			else
				isAborted = true;
			/*
			 * session.flush(); Query query = session.
			 * createQuery("UPDATE D010004 SET TotalCrVcrs=TotalCrVcrs+1, TotalCrAmtLcy = TotalCrAmtLcy +:amt WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd"
			 * ); query.setParameter("amt", amount); query.setParameter("lbrcode", brCode);
			 * query.setParameter("entrydate", selectedBatch.getId().getEntryDate());
			 * query.setParameter("batchcd", selectedBatch.getId().getBatchCd());
			 * query.executeUpdate(); session.flush();
			 */
			return d40;
		} catch (Exception e) {
			isAborted = true;
			e.printStackTrace();
		}
		return null;
	}

	public D009040 rtgsNeftDebitSameBranch(int brCode, String acctId, String tType, int setNo, int scrollNo,
			String narration, double amount, String rrn, int mainScrollNo, Session session) {
		try {
			Date openDate = getOpenDateNew(brCode, session); // Get Open Date
			if (openDate == null) {
				Logger.error("Open Date Not Found. Aborting Transaction");
				isAborted = true;
				return null;
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
				return null;
			}
			/// Get Selected Batch.
			D010004 selectedBatch = getSelectedBatchNew(brCode, batchCodes, openDate, session);
			if (selectedBatch == null) {
				Logger.error("No Active Batch Found.");
				isAborted = true;
				return null;
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
			d40.setMainScrollNo(mainScrollNo); // Scroll No
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
			d40.setActivityType("RTGSOW");
			d40.setCashFlowType("DR");
			d40.setValueDate(selectedBatch.getPostDate()); // postdate SELECT *
															// FROM D010004
															// WHERE LBrCode =9
															// AND EntryDate =
															// '19-APR-2016' AND
															// BatchCd = 'ABBTR'

			String bookType = getBookTypeNew(brCode, selectedBatch.getId().getBatchCd(), session);
			if (bookType == null) {
				Logger.error("BookType Not Found.");
				isAborted = true;
				return null;
			}

			logger.error("BookType is : " + bookType);
			d40.setBookType(bookType); // SELECT BookType FROM D010001 WHERE
										// LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('D');
			d40.setVcrAcctId(acctId);
			d40.setMainAcctId(acctId);

			short moduleType = getModuleTypeNew(brCode, acctId.substring(0, 8).trim(), session);
			if (moduleType == 0) {
				Logger.error("ModuleType Not Found.");
				isAborted = true;
				return null;
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
			d40.setInstrNo(rrn.length() > 12 ? rrn.substring(rrn.length() - 12, rrn.length()) : rrn.trim()); // RRN
			d40.setInstrDate(new Date()); // Blank
			d40.setParticulars(narration.length() > 70 ? narration.substring(0, 70) : narration.trim()); // param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); //
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');

			int usrCode2 = Integer.parseInt(ConfigurationLoader.getParameters(false).getProperty("IMPS_USER"));
			if (usrCode2 == 0) {
				Logger.error("UsrCode Not Found.");
				isAborted = true;
				return null;
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
			logger.error("Debit Voucjher Id:  " + d40.getId());
			// ====================Below Code is commented on 25/10/2017 for
			// Code Modification ======================
			// Query query = session.createQuery("UPDATE D010004 SET
			// TotalDrVcrs=TotalDrVcrs+1, TotalDrAmtLcy = TotalDrAmtLcy +:amt
			// WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd =
			// :batchcd");
			// query.setParameter("amt", amount);
			// query.setParameter("lbrcode", brCode);
			// query.setParameter("entrydate",
			// selectedBatch.getId().getEntryDate());
			// query.setParameter("batchcd",
			// selectedBatch.getId().getBatchCd());
			// query.executeUpdate();

			if (updateBatchBalance(session, amount, selectedBatch, brCode, MSGConstants.DR))
				isAborted = false;
			else
				isAborted = true;
			System.out.println("VoucherCommon.debitSameBranch() updated ");
			return d40;
		} catch (Exception e) {
			isAborted = true;
			e.printStackTrace();
		}
		return null;
	}

	public void rtgsNeftChargesVoucher(D946020 rtgsMessages, Session session) {
		D946124Id id = new D946124Id();

	}

	public D009040 rtgsNeftCredit(int brCode, String acctId, String tType, int setNo, int scrollNo, String narration,
			double amount, String rrn, Session session) {
		try {
			Date openDate = getOpenDateNew(brCode, session); // Get Open Date
			if (openDate == null) {
				Logger.error("Open Date Not Found. Aborting Transaction");
				isAborted = true;
				return null;
			}
			logger.error("Open Date is " + openDate);
			// Get BatchCodes from properties file.
			System.out.println("tType:>>>" + tType);
			String batchCode = Props.getBatchProperty(tType.trim());
			logger.error("Batch Code Form Properties File : " + batchCode);
			String batchCodes[] = batchCode.split("~");
			logger.error("Number of Batches is " + batchCodes.length + "\t Names are " + Arrays.toString(batchCodes));
			if (batchCodes == null || batchCodes.length < 1) {
				Logger.error("Batch Codes Not Found in Properties File.");
				isAborted = true;
				return null;
			}
			/// Get Selected Batch.
			D010004 selectedBatch = getSelectedBatchNew(brCode, batchCodes, openDate, session);
			if (selectedBatch == null) {
				Logger.error("No Active Batch Found.");
				isAborted = true;
				return null;
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
			d40.setActivityType("RTGSOW");
			d40.setCashFlowType("CR");
			d40.setValueDate(selectedBatch.getPostDate()); // postdate SELECT *
															// FROM D010004
															// WHERE LBrCode =9
															// AND EntryDate =
															// '19-APR-2016' AND
															// BatchCd = 'ABBTR'

			String bookType = getBookTypeNew(brCode, selectedBatch.getId().getBatchCd(), session);
			if (bookType == null) {
				logger.error("BookType Not Found.");
				isAborted = true;
				return null;
			}
			logger.error("BookType is : " + bookType);
			d40.setBookType(bookType); // SELECT BookType FROM D010001 WHERE
										// LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('C');
			d40.setVcrAcctId(acctId);
			d40.setMainAcctId(acctId);

			short moduleType = getModuleTypeNew(brCode, acctId.substring(0, 8).trim(), session);
			if (moduleType == 0) {
				Logger.error("ModuleType Not Found.");
				isAborted = true;
				return null;
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
			d40.setInstrNo(rrn.length() > 12 ? rrn.substring(rrn.length() - 12, rrn.length()) : rrn.trim()); // RRN
			d40.setInstrDate(new Date()); // Blank
			d40.setParticulars(narration.length() > 70 ? narration.substring(0, 70) : narration.trim()); // param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); //
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');

			int usrCode2 = getUsrCode("WEB");
			if (usrCode2 == 0) {
				Logger.error("UsrCode Not Found.");
				isAborted = true;
				return null;
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

			d40.setNoAuthPending((byte) 0);
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

			System.out.println("rtgsNeftCredit=" + d40.toString());
			session.save(d40);
			if (updateBatchBalance(session, amount, selectedBatch, brCode, MSGConstants.CR))
				isAborted = false;
			else
				isAborted = true;
			return d40;
		} catch (Exception e) {
			isAborted = true;
			e.printStackTrace();
		}
		return null;
	}

	public RtgsMessageSecondaryDatFile setRtgsMessageSecondaryDatFile(D009040 fromAbbVoucher, D009040 toAbbVoucher,
			D009040 toVoucher, D946020 rtgsMessages) {
		RtgsMessageSecondaryDatFile rtgsMessageSecondaryDatFile = new RtgsMessageSecondaryDatFile();
		rtgsMessageSecondaryDatFile.setId(new RtgsMessageSecondaryDatFileId());
		rtgsMessageSecondaryDatFile.getId().setOBrCode(rtgsMessages.getId().getObrCode());
		rtgsMessageSecondaryDatFile.getId().setIwOwMsg(rtgsMessages.getId().getIwOwMsg() + "");
		rtgsMessageSecondaryDatFile.getId().setMsgSType(rtgsMessages.getId().getMsgStype());
		rtgsMessageSecondaryDatFile.getId().setMsgDate(rtgsMessages.getId().getMsgDate());
		rtgsMessageSecondaryDatFile.getId().setRefNo(rtgsMessages.getId().getRefNo());
		rtgsMessageSecondaryDatFile.setTBrCode(toAbbVoucher.getId().getLbrCode());
		rtgsMessageSecondaryDatFile.setTDate(toAbbVoucher.getId().getEntryDate());
		rtgsMessageSecondaryDatFile.setTBatchCd(toAbbVoucher.getId().getBatchCd());
		rtgsMessageSecondaryDatFile.setTSetNo(toAbbVoucher.getId().getSetNo());
		// rtgsMessageSecondaryDatFile.setTscrollno(toAbbVoucher.getScrollno());
		rtgsMessageSecondaryDatFile.setTScrollNo(toVoucher.getId().getScrollNo());
		// by sachin
		rtgsMessageSecondaryDatFile.setCBatchCd(fromAbbVoucher.getId().getBatchCd());
		rtgsMessageSecondaryDatFile.setCSetNo(fromAbbVoucher.getId().getSetNo());
		rtgsMessageSecondaryDatFile.setCScrollNo(fromAbbVoucher.getMainScrollNo());
		rtgsMessageSecondaryDatFile.setCDate(fromAbbVoucher.getId().getEntryDate());
		// by sachin

		rtgsMessageSecondaryDatFile.setCBrCode(0);
		rtgsMessageSecondaryDatFile.setMsgMakeUserCd(" ");
		return rtgsMessageSecondaryDatFile;
	}

	public D009040 rtgsNeftdebit(int brCode, String acctId, String tType, int setNo, int scrollNo, String narration,
			double amount, String rrn, Session session) {
		try {
			Date openDate = getOpenDateNew(brCode, session); // Get Open Date
			if (openDate == null) {
				Logger.error("Open Date Not Found. Aborting Transaction");
				isAborted = true;
				return null;
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
				return null;
			}
			/// Get Selected Batch.
			D010004 selectedBatch = getSelectedBatchNew(brCode, batchCodes, openDate, session);
			if (selectedBatch == null) {
				Logger.error("No Active Batch Found.");
				isAborted = true;
				return null;
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
			d40.setActivityType("DR");
			d40.setCashFlowType("DR");
			d40.setValueDate(selectedBatch.getPostDate()); // postdate SELECT *
															// FROM D010004
															// WHERE LBrCode =9
															// AND EntryDate =
															// '19-APR-2016' AND
															// BatchCd = 'ABBTR'

			String bookType = getBookTypeNew(brCode, selectedBatch.getId().getBatchCd(), session);
			if (bookType == null) {
				Logger.error("BookType Not Found.");
				isAborted = true;
				return null;
			}

			logger.error("BookType is : " + bookType);
			d40.setBookType(bookType); // SELECT BookType FROM D010001 WHERE
										// LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('D');
			d40.setVcrAcctId(acctId);
			d40.setMainAcctId(acctId);

			short moduleType = getModuleTypeNew(brCode, acctId.substring(0, 8).trim(), session);
			if (moduleType == 0) {
				Logger.error("ModuleType Not Found.");
				isAborted = true;
				return null;
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
			d40.setInstrNo(rrn.length() > 12 ? rrn.substring(rrn.length() - 12, rrn.length()) : rrn.trim()); // RRN
			d40.setInstrDate(new Date()); // Blank
			d40.setParticulars(narration.length() > 70 ? narration.substring(0, 70) : narration.trim()); // param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); //
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');

			int usrCode2 = Integer.parseInt(ConfigurationLoader.getParameters(false).getProperty("IMPS_USER"));

			if (usrCode2 == 0) {
				Logger.error("UsrCode Not Found.");
				isAborted = true;
				return null;
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

			d40.setNoAuthPending((byte) 0);
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

			System.out.println("Updating Debit Batch balance=" + d40.toString());
			session.save(d40);
			if (updateBatchBalance(session, amount, selectedBatch, brCode, MSGConstants.DR))
				isAborted = false;
			else
				isAborted = true;
			System.out.println("isAborted::>>" + isAborted);
			return d40;
		} catch (Exception e) {
			isAborted = true;
			e.printStackTrace();
		}
		return null;
	}

	public D009040 rtgsNeftCreditABB(int brCode, String acctId, String tType, int setNo, int scrollNo, String narration,
			int tbrCode, double amount, String rrn, int reconNo, Session session) {
		try {
			Date openDate = getOpenDateNew(brCode, session); // Get Open Date
			if (openDate == null) {
				Logger.error("Open Date Not Found. Aborting Transaction");
				isAborted = true;
				return null;
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
				return null;
			}

			/// Get Selected Batch.
			D010004 selectedBatch = getSelectedBatchNew(brCode, batchCodes, openDate, session);
			if (selectedBatch == null) {
				Logger.error("No Active Batch Found.");
				isAborted = true;
				return null;
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

			String bookType = getBookTypeNew(brCode, selectedBatch.getId().getBatchCd(), session);
			if (bookType == null) {
				Logger.error("BookType Not Found.");
				isAborted = true;
				return null;
			}
			logger.error("BookType is : " + bookType);
			d40.setBookType(bookType); // SELECT BookType FROM D010001 WHERE
										// LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('C');
			d40.setVcrAcctId(acctId);
			d40.setMainAcctId(acctId);

			short moduleType = getModuleTypeNew(brCode, acctId.substring(0, 8).trim(), session);

			if (moduleType == 0) {
				Logger.error("ModuleType Not Found.");
				moduleType = 100;
				return null;
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
			d40.setInstrNo(rrn.length() > 12 ? rrn.substring(rrn.length() - 12, rrn.length()) : rrn.trim()); // RRN
			d40.setInstrDate(new Date()); // Blank
			d40.setParticulars("ReconNo=" + reconNo + " ToBrCode=" + tbrCode); // param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); //
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');
			d40.setTokenNo(reconNo);

			int usrCode2 = Integer.parseInt(ConfigurationLoader.getParameters(false).getProperty("IMPS_USER"));
			if (usrCode2 == 0) {
				Logger.error("UsrCode Not Found.");
				isAborted = true;
				return null;
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

			// updateBatchIntraBr(amount,brCode
			// ,selectedBatch.getId().getBatchCd() ,openDate );

			System.out.println("<<<<<----------Updating CreditABB vouchers--------------->>>>>>" + d40.toString());
			session.save(d40);
			if (updateBatchBalance(session, amount, selectedBatch, brCode, MSGConstants.CR))
				isAborted = false;
			else
				isAborted = true;
			System.out.println("isAborted::>>" + isAborted);
			return d40;
		} catch (Exception e) {
			isAborted = true;
			logger.error("ABORTED::>>" + e);
			e.printStackTrace();
		}
		return null;
	}

	public D009040 rtgsNeftDebitABB(int brCode, String acctId, String tType, int setNo, int scrollNo, String narration,
			int benBrcode, double amount, String rrn, int reconNo, Session session) {
		try {
			Date openDate = getOpenDateNew(brCode, session); // Get Open Date
			if (openDate == null) {
				Logger.error("Open Date Not Found. Aborting Transaction");
				isAborted = true;
				return null;
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
				return null;
			}

			/// Get Selected Batch.
			D010004 selectedBatch = getSelectedBatchNew(brCode, batchCodes, openDate, session);
			if (selectedBatch == null) {
				Logger.error("No Active Batch Found.");
				isAborted = true;
				return null;
			}

			logger.error("Selected Batch : " + selectedBatch.getId().getBatchCd());

			D009040 d40 = new D009040();
			D009040Id id40 = new D009040Id();
			id40.setBatchCd(selectedBatch.getId().getBatchCd());

			id40.setEntryDate(selectedBatch.getId().getEntryDate());
			id40.setLbrCode(brCode);
			id40.setSetNo(setNo);

			id40.setScrollNo(scrollNo);

			d40.setId(id40);
			d40.setMainScrollNo(scrollNo); // Scroll No
			d40.setPostDate(selectedBatch.getPostDate());
			d40.setFeffDate(selectedBatch.getFeffDate());
			d40.setActivityType("ABBREM");
			d40.setCashFlowType("ABBDR");
			d40.setValueDate(selectedBatch.getPostDate());

			String bookType = getBookTypeNew(brCode, selectedBatch.getId().getBatchCd(), session);
			if (bookType == null) {
				Logger.error("BookType Not Found.");
				isAborted = true;
				return null;
			}
			logger.error("BookType is : " + bookType);
			d40.setBookType(bookType);
			d40.setDrCr('D');
			d40.setVcrAcctId(acctId);
			d40.setMainAcctId(acctId);

			short moduleType = getModuleTypeNew(brCode, acctId.substring(0, 8).trim(), session);
			if (moduleType == 0) {
				Logger.error("ModuleType Not Found.");
				isAborted = true;
				return null;
			}
			logger.error("ModuleType is : " + moduleType);
			d40.setMainModType(moduleType);

			d40.setVcrModType(moduleType);

			d40.setTrnCurCd("INR");
			d40.setFcyTrnAmt(amount);
			d40.setLcyConvRate(1);
			d40.setLcyTrnAmt(amount);
			d40.setInstrBankCd((short) benBrcode);
			d40.setInstrBranchCd((short) brCode);
			d40.setInstrType((short) 99); // Depend on chanel
			d40.setInstrNo(rrn.length() > 12 ? rrn.substring(rrn.length() - 12, rrn.length()) : rrn.trim()); // RRN
			d40.setInstrDate(new Date()); // Blank
			d40.setParticulars(narration.length() > 70 ? narration.substring(0, 70) : narration.trim()); // param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); //
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');
			d40.setTokenNo(reconNo);
			int usrCode2 = Integer.parseInt(ConfigurationLoader.getParameters(false).getProperty("IMPS_USER"));
			if (usrCode2 == 0) {
				Logger.error("UsrCode Not Found.");
				isAborted = true;
				return null;
			}
			logger.error("UsrCode is : " + usrCode2);
			d40.setMaker(usrCode2);

			d40.setMakerDate(selectedBatch.getId().getEntryDate()); // EntryDate
			d40.setMakerTime(Integer.parseInt(timeFormat.format(new Date()).substring(0, 8)));
			d40.setChecker1(usrCode2);
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
			d40.setPostTime(Integer.parseInt(timeFormat.format(new Date()).substring(0, 8))); // Time
																								// HHMMSSTT
			System.out.println("Debit ABB=" + d40.toString());
			session.save(d40);

			if (updateBatchBalance(session, amount, selectedBatch, brCode, MSGConstants.DR))
				isAborted = false;
			else
				isAborted = true;
			return d40;
		} catch (Exception e) {
			isAborted = true;
			e.printStackTrace();
		}
		return null;
	}

	public void creditVoucherForTD(int brCode, String acctId, String tType, int setNo, int scrollNo, String narration,
			double amount, String rrn, Session session) {
		try {
			Date openDate = getOpenDateNew(brCode, session); // Get Open Date
			if (openDate == null) {
				Logger.error("Open Date Not Found. Aborting Transaction");
				isAborted = true;
				return;
			}
			logger.error("Open Date is " + openDate);
			// Get BatchCodes from properties file.
			System.out.println("tType:>>>" + tType);
			String batchCode = Props.getBatchProperty(tType.trim());
			logger.error("Batch Code Form Properties File : " + batchCode);
			String batchCodes[] = batchCode.split("~");
			logger.error("Number of Batches is " + batchCodes.length + "\t Names are " + Arrays.toString(batchCodes));
			if (batchCodes == null || batchCodes.length < 1) {
				Logger.error("Batch Codes Not Found in Properties File.");
				isAborted = true;
				return;
			}
			/// Get Selected Batch.
			D010004 selectedBatch = getSelectedBatchNew(brCode, batchCodes, openDate, session);
			if (selectedBatch == null) {
				Logger.error("No Active Batch Found.");
				isAborted = true;
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
			d40.setActivityType("TDPRCR");
			d40.setCashFlowType("TDPRCR");
			d40.setValueDate(selectedBatch.getPostDate()); // postdate SELECT *
															// FROM D010004
															// WHERE LBrCode =9
															// AND EntryDate =
															// '19-APR-2016' AND
															// BatchCd = 'ABBTR'

			String bookType = getBookTypeNew(brCode, selectedBatch.getId().getBatchCd(), session);
			if (bookType == null) {
				logger.error("BookType Not Found.");
				isAborted = true;
				return;
			}
			logger.error("BookType is : " + bookType);
			d40.setBookType(bookType); // SELECT BookType FROM D010001 WHERE
										// LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('C');
			d40.setVcrAcctId(acctId);
			d40.setMainAcctId(acctId);

			short moduleType = getModuleTypeNew(brCode, acctId.substring(0, 8).trim(), session);
			if (moduleType == 0) {
				Logger.error("ModuleType Not Found.");
				isAborted = true;
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
			d40.setParticulars(narration.length() > 70 ? narration.substring(0, 70) : narration.trim()); // param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); //
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');

			int usrCode2 = getUsrCode("WEB");
			if (usrCode2 == 0) {
				Logger.error("UsrCode Not Found.");
				isAborted = true;
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

			d40.setNoAuthPending((byte) 0);
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
			if (updateBatchBalance(session, amount, selectedBatch, brCode, MSGConstants.CR))
				isAborted = false;
			else
				isAborted = true;
		} catch (Exception e) {
			isAborted = true;
			e.printStackTrace();
		}

	}

	public void creditSameBranchDPA(int brCode, String acctId, String tType, int setNo, int scrollNo, String narration,
			double amount, String rrn, int mainSrollNo, Session session) {
		try {
			Date openDate = getOpenDateNew(brCode, session); // Get Open Date
			if (openDate == null) {
				Logger.error("Open Date Not Found. Aborting Transaction");
				isAborted = true;
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
				return;
			}

			/// Get Selected Batch.
			D010004 selectedBatch = getSelectedBatchNew(brCode, batchCodes, openDate, session);
			if (selectedBatch == null) {
				Logger.error("No Active Batch Found.");
				isAborted = true;
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
			d40.setMainScrollNo(mainSrollNo); // Scroll No
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
			d40.setActivityType("TDPRCR");
			d40.setCashFlowType("TDPRCR");
			d40.setValueDate(selectedBatch.getPostDate()); // postdate SELECT *
															// FROM D010004
															// WHERE LBrCode =9
															// AND EntryDate =
															// '19-APR-2016' AND
															// BatchCd = 'ABBTR'

			String bookType = getBookTypeNew(brCode, selectedBatch.getId().getBatchCd(), session);
			if (bookType == null) {
				Logger.error("BookType Not Found.");
				isAborted = true;
				return;
			}
			logger.error("BookType is : " + bookType);
			d40.setBookType(bookType); // SELECT BookType FROM D010001 WHERE
										// LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('C');
			d40.setVcrAcctId(acctId);
			d40.setMainAcctId(acctId);

			short moduleType = getModuleTypeNew(brCode, acctId.substring(0, 8).trim(), session);
			if (moduleType == 0) {
				Logger.error("ModuleType Not Found.");
				isAborted = true;
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
			d40.setParticulars(narration.length() > 70 ? narration.substring(0, 70) : narration.trim()); // param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); //
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');

			int usrCode2 = Integer.parseInt(ConfigurationLoader.getParameters(false).getProperty("IMPS_USER"));
			if (usrCode2 == 0) {
				Logger.error("UsrCode Not Found.");
				isAborted = true;
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
			if (updateBatchBalance(session, amount, selectedBatch, brCode, MSGConstants.CR))
				isAborted = false;
			else
				isAborted = true;
			/*
			 * session.flush(); Query query = session.
			 * createQuery("UPDATE D010004 SET TotalCrVcrs=TotalCrVcrs+1, TotalCrAmtLcy = TotalCrAmtLcy +:amt WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd"
			 * ); query.setParameter("amt", amount); query.setParameter("lbrcode", brCode);
			 * query.setParameter("entrydate", selectedBatch.getId().getEntryDate());
			 * query.setParameter("batchcd", selectedBatch.getId().getBatchCd());
			 * query.executeUpdate(); session.flush();
			 */
		} catch (Exception e) {
			isAborted = true;
			e.printStackTrace();
		}
	}

	public void creditSameBranchDDS(int brCode, String acctId, String tType, int setNo, int scrollNo, String narration,
			double amount, String rrn, int mainSrollNo, Session session, D010004 d010004, String agentAcct) {
		try {
			D010004 selectedBatch = d010004;
			if (selectedBatch == null) {
				Logger.error("No Active Batch Found.");
				isAborted = true;
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
			d40.setMainScrollNo(mainSrollNo); // Scroll No
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
			// d40.setActivityType("CR");
			// d40.setCashFlowType("DDSCR");
			d40.setValueDate(selectedBatch.getPostDate()); // postdate SELECT *
															// FROM D010004
															// WHERE LBrCode =9
															// AND EntryDate =
															// '19-APR-2016' AND
															// BatchCd = 'ABBTR'

			String bookType = getBookTypeNew(brCode, selectedBatch.getId().getBatchCd(), session);
			if (bookType == null) {
				Logger.error("BookType Not Found.");
				isAborted = true;
				return;
			}
			logger.error("BookType is : " + bookType);
			d40.setBookType(bookType); // SELECT BookType FROM D010001 WHERE
										// LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('C');
			d40.setVcrAcctId(acctId);
			d40.setMainAcctId(agentAcct);

			short moduleType = getModuleTypeNew(brCode, acctId.substring(0, 8).trim(), session);
			if (moduleType == 0) {
				Logger.error("ModuleType Not Found.");
				isAborted = true;
				return;
			}
			d40.setActivityType("CR");
			if (moduleType == 47)
				d40.setCashFlowType("DDSCR");
			else
				d40.setCashFlowType("CR");
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
			d40.setParticulars(narration.length() > 70 ? narration.substring(0, 70) : narration.trim()); // param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); //
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');

			int usrCode2 = Integer.parseInt(ConfigurationLoader.getParameters(false).getProperty("IMPS_USER"));
			if (usrCode2 == 0) {
				Logger.error("UsrCode Not Found.");
				isAborted = true;
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
			String batchFlag = ConfigurationLoader.getParameters(false).getProperty("UPDATE_BATCH_BAL") != null
					? ConfigurationLoader.getParameters(false).getProperty("UPDATE_BATCH_BAL")
					: "N";
			if (batchFlag.equalsIgnoreCase(MSGConstants.YES)) {
				if (updateBatchBalance(session, amount, selectedBatch, brCode, MSGConstants.CR))
					isAborted = false;
				else
					isAborted = true;
			}
		} catch (Exception e) {
			isAborted = true;
			e.printStackTrace();
		}
	}

	public void credit(int brCode, String acctId, String tType, int setNo, int scrollNo, String narration,
			double amount, String rrn, Session session, String agentAcct) {
		try {
			Date openDate = getOpenDateNew(brCode, session); // Get Open Date
			if (openDate == null) {
				Logger.error("Open Date Not Found. Aborting Transaction");
				isAborted = true;
				return;
			}
			logger.error("Open Date is " + openDate);
			// Get BatchCodes from properties file.
			System.out.println("tType:>>>" + tType);
			String batchCode = Props.getBatchProperty(tType.trim());
			logger.error("Batch Code Form Properties File : " + batchCode);
			String batchCodes[] = batchCode.split("~");
			logger.error("Number of Batches is " + batchCodes.length + "\t Names are " + Arrays.toString(batchCodes));
			if (batchCodes == null || batchCodes.length < 1) {
				Logger.error("Batch Codes Not Found in Properties File.");
				isAborted = true;
				return;
			}
			/// Get Selected Batch.
			D010004 selectedBatch = getSelectedBatchNew(brCode, batchCodes, openDate, session);
			if (selectedBatch == null) {
				Logger.error("No Active Batch Found.");
				isAborted = true;
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
			d40.setActivityType("CR");
			d40.setCashFlowType("CR");
			d40.setValueDate(selectedBatch.getPostDate()); // postdate SELECT *
															// FROM D010004
															// WHERE LBrCode =9
															// AND EntryDate =
															// '19-APR-2016' AND
															// BatchCd = 'ABBTR'

			String bookType = getBookTypeNew(brCode, selectedBatch.getId().getBatchCd(), session);
			if (bookType == null) {
				logger.error("BookType Not Found.");
				isAborted = true;
				return;
			}
			logger.error("BookType is : " + bookType);
			d40.setBookType(bookType); // SELECT BookType FROM D010001 WHERE
										// LBrCode = 9 AND Code = 'ABBTR'
			d40.setDrCr('C');
			d40.setVcrAcctId(acctId);
			d40.setMainAcctId(agentAcct);

			short moduleType = getModuleTypeNew(brCode, acctId.substring(0, 8).trim(), session);
			if (moduleType == 0) {
				Logger.error("ModuleType Not Found.");
				isAborted = true;
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
			d40.setParticulars(narration.length() > 70 ? narration.substring(0, 70) : narration.trim()); // param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); //
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');

			int usrCode2 = getUsrCode("WEB");
			if (usrCode2 == 0) {
				Logger.error("UsrCode Not Found.");
				isAborted = true;
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

			d40.setNoAuthPending((byte) 0);
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
			if (updateBatchBalance(session, amount, selectedBatch, brCode, MSGConstants.CR))
				isAborted = false;
			else
				isAborted = true;
		} catch (Exception e) {
			isAborted = true;
			e.printStackTrace();
		}

	}

	public static D100001 reconNoCheck(Long seqNo, int lbrCode, Session session) {
		String hqlUpdate = "update D001005 set  lastNo=:lastNo where id.lbrCode = :lbrCode and id.catType='ABBRECON'";
		logger.info(hqlUpdate);
		try {
			Integer result = session.createQuery(hqlUpdate).setParameter("lastNo", seqNo.intValue())
					.setParameter("lbrCode", lbrCode).executeUpdate();
			logger.info(result + " Updated Recon no for Branch " + lbrCode + " =" + seqNo);

			String checkQuery = "from D100001 where id.lbrCode =:lbrCode and id.reconNo =:lastNo";
			logger.info(checkQuery);

			D100001 reconRecord = (D100001) session.createQuery(checkQuery).setParameter("lbrCode", lbrCode)
					.setParameter("lastNo", seqNo.intValue()).getSingleResult();
			return reconRecord;
		} catch (NoResultException em) {
			return null;
		} catch (Exception ex) {
			return null;
		}

	}

	public static HashMap<String, String> acctToGlVoucherNew(int lbrCode, String accNo, String transType,
			String narration, double amount, String rrn, String debAccNo, VoucherCommon common, Session session,
			Transaction t) throws Exception {

		HashMap<String, String> resultMap = new HashMap<>();
		logger.error("Same Branch Fund Transfer NEW Process....");
		int setNo = VoucherCommon.getNextSetNo();
		int mainScrollNo = VoucherCommon.getNextScrollNo();
		resultMap.put("DrBrCode", lbrCode + "");
		resultMap.put("DrAcctId", debAccNo);
		common.debitSameBranch(lbrCode, debAccNo, transType.toUpperCase(), setNo, mainScrollNo, narration, amount, rrn,
				mainScrollNo, session);
		System.out.println("VoucherCommon.otherBranchVouchers() common.isAborted " + common.isAborted);
		if (!common.isAborted) {
			if (VoucherMPOS.updateProductBalances(amount, lbrCode, "D", debAccNo.substring(0, 8).trim(), session)
					.equalsIgnoreCase(MSGConstants.SUCCESS)) {
				String balResponse = CoreTransactionMPOS.balance(lbrCode, debAccNo.trim(), amount, "D", session);
				if (balResponse != null && balResponse.trim().length() > 0 && !balResponse.trim().equalsIgnoreCase("99")
						&& !balResponse.trim().equalsIgnoreCase("51")) {
					if (!common.isAborted) {
						/*** Added By Aniket Desai on 28 Aug, 2019 for Loan Changes:---Starts--- ***/
						logger.error("Transaction successful");
						logger.error("accNo::>>" + accNo);
						logger.error("lbrCode::>>" + lbrCode);
						D009022 creditAccount = session.get(D009022.class, new D009022Id(lbrCode, accNo));

						D009021 creditProductMaster = session.get(D009021.class,
								new D009021Id(lbrCode, accNo.substring(0, 8).trim()));
						if (RtgsNeftHostToHostConstants.LOAN.getMessage()
								.contains(creditProductMaster.getModuleType() + "")) {
							logger.error("LOAN Transaction");
							D009022 debiteAccount = session.get(D009022.class, new D009022Id(lbrCode, debAccNo));
							if (creditAccount.getCustNo() != debiteAccount.getCustNo()) {
								logger.error("Transaction unsuccessful " + creditAccount.getCustNo() + "!="
										+ debiteAccount.getCustNo());
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "0");
								resultMap.put(Code.SCROLLNO, "0");
								return resultMap;
							}
							LoanServiceImpl loanService = new LoanServiceImpl();
							String status = loanService.numberOfVoucherOnLoanType(creditAccount, creditProductMaster,
									lbrCode, creditAccount.getId().getPrdAcctId().substring(0, 8).trim(), accNo,
									transType, narration, amount, rrn, setNo, VoucherCommon.getNextScrollNo(), session);
							logger.error("Loan Transaction Status:" + status);
							if (status.equalsIgnoreCase("True"))
								common.isAborted = true;
						} else if (!RtgsNeftHostToHostConstants.LOAN.getMessage()
								.contains(creditProductMaster.getModuleType() + "")) {

							common.creditSameBranch(lbrCode, accNo, transType, setNo, VoucherCommon.getNextScrollNo(),
									narration, amount, rrn, mainScrollNo, session);
						}
						/*
						 * common.creditSameBranch(lbrCode, benAccNo, transType, setNo,
						 * VoucherCommon.getNextScrollNo(), narration, amount, rrn, mainScrollNo,
						 * session);
						 */
						/*** Added By Aniket Desai on 28 Aug, 2019 for Loan Changes:---END--- ***/

						if (!common.isAborted) {

							/***
							 * Added By Aniket Desai on 3rd Sep, 2019 for Loan Voucher(Double CR To Loan
							 * Account):--Start--
							 ***/
							String bal = null;
							if (RtgsNeftHostToHostConstants.LOAN.getMessage()
									.contains(creditProductMaster.getModuleType() + ""))
								bal = amount + "";
							else if (VoucherMPOS
									.updateProductBalances(amount, lbrCode, "C", accNo.substring(0, 8).trim(), session)
									.equalsIgnoreCase(MSGConstants.SUCCESS))
								bal = CoreTransactionMPOS.balanceOld(lbrCode, accNo.trim(), amount, "C", session);

							/*
							 * if (VoucherMPOS.updateProductBalances(amount, lbrCode, "C",
							 * benAccNo.substring(0, 8).trim(), session)
							 * .equalsIgnoreCase(MSGConstants.SUCCESS)) { String bal =
							 * CoreTransactionMPOS.balance(lbrCode, benAccNo.trim(), amount, "C", session);
							 */

							/***
							 * Added By Aniket Desai on 3rd Sep, 2019 for Loan Voucher(Double CR To Loan
							 * Account):--End--
							 ***/
							if (bal != null && bal.trim().length() > 0 && !bal.trim().equalsIgnoreCase("51")
									&& !bal.trim().equalsIgnoreCase("99")) {
								// tx.commit();
								// session.close();

								if (transType.contains("~")) {
									String[] type = transType.split("~");
									String cashFlowType = type[0];
									transType = type[1];
								}
								String batchCodes[] = Props.getBatchProperty(transType).split("~");
								/*** Added and commited by Aniket Desai on 23rd Oct, 2019 for issue #48692 ***/
								// D001004 d04OnlineBatchName =
								// VoucherMPOS.getBatchNameFromBatchCode(batchCodes[0]);

								Date openDate = getOpenDateNew(lbrCode, session);
								D010004 d04OnlineBatchName = getSelectedBatchNew(lbrCode, batchCodes, openDate,
										session);
								String onlineBatchName = d04OnlineBatchName.getId().getBatchCd().trim();
								resultMap.put(Code.RESULT, Code.SUCCESS);
								resultMap.put("Batch", onlineBatchName);
								resultMap.put(Code.SETNO, String.valueOf(setNo));
								resultMap.put(Code.SCROLLNO, String.valueOf(mainScrollNo));
								t.commit();
								return resultMap;
							} /*
								 * else { logger.error("Transaction unsuccessful"); resultMap.put(Code.RESULT,
								 * Code.ERROR); resultMap.put(Code.SETNO, "0"); resultMap.put(Code.SCROLLNO,
								 * "0"); return resultMap; } }
								 */ else {
								logger.error("Transaction unsuccessful");
								t.rollback();
								// session.close();
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "0");
								resultMap.put(Code.SCROLLNO, "0");
								return resultMap;
							}
						} else {
							logger.error("Transaction unsuccessful");
							t.rollback();
							// session.close();
							resultMap.put(Code.RESULT, Code.ERROR);
							resultMap.put(Code.SETNO, "0");
							resultMap.put(Code.SCROLLNO, "0");
							return resultMap;
						}
					} else {
						logger.error("Transaction unsuccessful");
						t.rollback();
						// session.close();
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "0");
						resultMap.put(Code.SCROLLNO, "0");
						return resultMap;
					}
				} else {
					logger.error("Transaction unsuccessful");
					t.rollback();
					// session.close();
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "0");
					resultMap.put(Code.SCROLLNO, "0");
					return resultMap;
				}
			} else {
				logger.error("Transaction unsuccessful");
				t.rollback();
				// session.close();
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "0");
				resultMap.put(Code.SCROLLNO, "0");
				return resultMap;
			}
		} else {
			logger.error("Transaction unsuccessful");
			t.rollback();
			// session.close();
			resultMap.put(Code.RESULT, Code.ERROR);
			resultMap.put(Code.SETNO, "0");
			resultMap.put(Code.SCROLLNO, "0");
			return resultMap;
		}

	}

	public static HashMap<String, String> acctToGlVoucherNew(int lbrCode, String accNo, String transType,
			String narration, double amount, String rrn, String debAccNo, VoucherCommon common, Session session)
			throws Exception {

		HashMap<String, String> resultMap = new HashMap<>();
		logger.error("Same Branch Fund Transfer NEW Process....");
		int setNo = VoucherCommon.getNextSetNo();
		int mainScrollNo = VoucherCommon.getNextScrollNo();
		common.debitSameBranch(lbrCode, debAccNo, transType.toUpperCase(), setNo, mainScrollNo, narration, amount, rrn,
				mainScrollNo, session);
		System.out.println("VoucherCommon.otherBranchVouchers() common.isAborted " + common.isAborted);
		if (!common.isAborted) {
			if (VoucherMPOS.updateProductBalances(amount, lbrCode, "D", debAccNo.substring(0, 8).trim(), session)
					.equalsIgnoreCase(MSGConstants.SUCCESS)) {
				String balResponse = CoreTransactionMPOS.balance(lbrCode, debAccNo.trim(), amount, "D", session);
				if (balResponse != null && balResponse.trim().length() > 0 && !balResponse.trim().equalsIgnoreCase("99")
						&& !balResponse.trim().equalsIgnoreCase("51")) {
					if (!common.isAborted) {
						/*** Added By Aniket Desai on 28 Aug, 2019 for Loan Changes:---Starts--- ***/
						logger.error("Transaction successful");
						logger.error("accNo::>>" + accNo);
						logger.error("lbrCode::>>" + lbrCode);
						D009022 creditAccount = session.get(D009022.class, new D009022Id(lbrCode, accNo));

						D009021 creditProductMaster = session.get(D009021.class,
								new D009021Id(lbrCode, accNo.substring(0, 8).trim()));
						if (RtgsNeftHostToHostConstants.LOAN.getMessage()
								.contains(creditProductMaster.getModuleType() + "")) {
							logger.error("LOAN Transaction");
							D009022 debiteAccount = session.get(D009022.class, new D009022Id(lbrCode, debAccNo));
							if (creditAccount.getCustNo() != debiteAccount.getCustNo()) {
								logger.error("Transaction unsuccessful " + creditAccount.getCustNo() + "!="
										+ debiteAccount.getCustNo());
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "0");
								resultMap.put(Code.SCROLLNO, "0");
								return resultMap;
							}
							LoanServiceImpl loanService = new LoanServiceImpl();
							String status = loanService.numberOfVoucherOnLoanType(creditAccount, creditProductMaster,
									lbrCode, creditAccount.getId().getPrdAcctId().substring(0, 8).trim(), accNo,
									transType, narration, amount, rrn, setNo, VoucherCommon.getNextScrollNo(), session);
							logger.error("Loan Transaction Status:" + status);
							if (status.equalsIgnoreCase("True"))
								common.isAborted = true;
						} else if (!RtgsNeftHostToHostConstants.LOAN.getMessage()
								.contains(creditProductMaster.getModuleType() + "")) {

							common.creditSameBranch(lbrCode, accNo, transType, setNo, VoucherCommon.getNextScrollNo(),
									narration, amount, rrn, mainScrollNo, session);
						}
						/*
						 * common.creditSameBranch(lbrCode, benAccNo, transType, setNo,
						 * VoucherCommon.getNextScrollNo(), narration, amount, rrn, mainScrollNo,
						 * session);
						 */
						/*** Added By Aniket Desai on 28 Aug, 2019 for Loan Changes:---END--- ***/

						if (!common.isAborted) {

							/***
							 * Added By Aniket Desai on 3rd Sep, 2019 for Loan Voucher(Double CR To Loan
							 * Account):--Start--
							 ***/
							String bal = null;
							if (RtgsNeftHostToHostConstants.LOAN.getMessage()
									.contains(creditProductMaster.getModuleType() + ""))
								bal = amount + "";
							else if (VoucherMPOS
									.updateProductBalances(amount, lbrCode, "C", accNo.substring(0, 8).trim(), session)
									.equalsIgnoreCase(MSGConstants.SUCCESS))
								bal = CoreTransactionMPOS.balanceOld(lbrCode, accNo.trim(), amount, "C", session);

							/*
							 * if (VoucherMPOS.updateProductBalances(amount, lbrCode, "C",
							 * benAccNo.substring(0, 8).trim(), session)
							 * .equalsIgnoreCase(MSGConstants.SUCCESS)) { String bal =
							 * CoreTransactionMPOS.balance(lbrCode, benAccNo.trim(), amount, "C", session);
							 */

							/***
							 * Added By Aniket Desai on 3rd Sep, 2019 for Loan Voucher(Double CR To Loan
							 * Account):--End--
							 ***/
							if (bal != null && bal.trim().length() > 0 && !bal.trim().equalsIgnoreCase("51")
									&& !bal.trim().equalsIgnoreCase("99")) {
								// tx.commit();
								// session.close();

								if (transType.contains("~")) {
									String[] type = transType.split("~");
									String cashFlowType = type[0];
									transType = type[1];
								}
								String batchCodes[] = Props.getBatchProperty(transType).split("~");
								/*** Added and commited by Aniket Desai on 23rd Oct, 2019 for issue #48692 ***/
								// D001004 d04OnlineBatchName =
								// VoucherMPOS.getBatchNameFromBatchCode(batchCodes[0]);

								Date openDate = getOpenDateNew(lbrCode, session);
								D010004 d04OnlineBatchName = getSelectedBatchNew(lbrCode, batchCodes, openDate,
										session);
								String onlineBatchName = d04OnlineBatchName.getId().getBatchCd().trim();
								resultMap.put(Code.RESULT, Code.SUCCESS);
								resultMap.put("Batch", onlineBatchName);
								resultMap.put(Code.SETNO, String.valueOf(setNo));
								resultMap.put(Code.SCROLLNO, String.valueOf(mainScrollNo));
								// t.commit();
								return resultMap;
							} /*
								 * else { logger.error("Transaction unsuccessful"); resultMap.put(Code.RESULT,
								 * Code.ERROR); resultMap.put(Code.SETNO, "0"); resultMap.put(Code.SCROLLNO,
								 * "0"); return resultMap; } }
								 */ else {
								logger.error("Transaction unsuccessful");
								// t.rollback();
								// session.close();
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "0");
								resultMap.put(Code.SCROLLNO, "0");
								return resultMap;
							}
						} else {
							logger.error("Transaction unsuccessful");
							// t.rollback();
							// session.close();
							resultMap.put(Code.RESULT, Code.ERROR);
							resultMap.put(Code.SETNO, "0");
							resultMap.put(Code.SCROLLNO, "0");
							return resultMap;
						}
					} else {
						logger.error("Transaction unsuccessful");
						// t.rollback();
						// session.close();
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "0");
						resultMap.put(Code.SCROLLNO, "0");
						return resultMap;
					}
				} else {
					logger.error("Transaction unsuccessful");
					// t.rollback();
					// session.close();
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "0");
					resultMap.put(Code.SCROLLNO, "0");
					return resultMap;
				}
			} else {
				logger.error("Transaction unsuccessful");
				// t.rollback();
				// session.close();
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "0");
				resultMap.put(Code.SCROLLNO, "0");
				return resultMap;
			}
		} else {
			logger.error("Transaction unsuccessful");
			// t.rollback();
			// session.close();
			resultMap.put(Code.RESULT, Code.ERROR);
			resultMap.put(Code.SETNO, "0");
			resultMap.put(Code.SCROLLNO, "0");
			return resultMap;
		}

	}

	public static String get32DigitAcctNo(String prdCd, int acctNo, int subAcctNo) {
		String acctNo32Digit = String.format("%-8s", prdCd) + "00000000" + String.format("%08d", acctNo)
				+ String.format("%08d", subAcctNo);
		return acctNo32Digit;
	}

	@SuppressWarnings("deprecation")
	public static int getTransactionsInAMonth(String mobileNo, String mmid, Session session) {
		int result = 0;
		String queryString = "";
		if (ConfigurationLoader.getParameters(false).getProperty("DATABASE").equalsIgnoreCase("ORACLE")) {
			queryString = "SELECT count(*) FROM D350037 T\r\n" + "	left JOIN \r\n"
					+ "		D350059 R ON T.LbrCode =R.LbrCode AND T.MobNo1=R.MobNo1 AND T.MMID1=R.MMID1 AND T.ResponseCd=R.ResponseCd AND T.RrnNo=R.RrnNo\r\n"
					+ "	WHERE T.ResponseCd='00' AND T.DRCR='DR' AND T.MobNo1=:mobNo AND T.MMID1=:mmid AND R.LbrCode IS NULL AND T.TransactionDate=:date";
		} else {
			queryString = "SELECT count(*) FROM D350037 T\r\n" + "	left JOIN \r\n"
					+ "		D350059 R ON T.LbrCode =R.LbrCode AND T.MobNo1=R.MobNo1 AND T.MMID1=R.MMID1 AND T.ResponseCd=R.ResponseCd AND T.RrnNo=R.RrnNo\r\n"
					+ "	WHERE T.ResponseCd='00' AND T.DRCR='DR' AND T.MobNo1=:mobNo AND T.MMID1=:mmid AND R.LbrCode IS NULL AND T.TransactionDate>:date";
		}

		logger.error("Query= " + queryString);

		try {
			Calendar c = Calendar.getInstance(); // this takes current date
			c.set(Calendar.DAY_OF_MONTH, 1);
			Date monthDate = DateUtil.convertDateFormat(c.getTime());
			System.out.println(c.getTime());
			Query q = session.createSQLQuery(queryString);
			q.setParameter("mobNo", mobileNo);
			q.setParameter("mmid", mmid);
			q.setParameter("date", monthDate);

			if (ConfigurationLoader.getParameters(false).getProperty("DATABASE").equalsIgnoreCase("ORACLE"))
				result = ((BigDecimal) q.uniqueResult()).intValue();
			else
				result = (int) q.uniqueResult();
			System.out.println(result);
			// session.close();
			// session = null;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			// session.close();
		}
		return result;
	}

	public static HashMap<String, String> gstVoucherEntry(int lbrCode, String rrn, Session session, String accNo,
			String narration, Double amount, Double trnAmt) {

		HashMap<String, String> resultMap = new HashMap<>();

		// double amount = 0.0;
		double serTaxRate = 0.0;
		double eduCharges = 0.0;
		double finalAmt = 0.0;
		double lastAmt = 0.0;
		double osAmt = 0.0;
		String batch = "";
		String plCrAccNo = "";
		String stat = "";
		String batchCode = "";
		double sgstCharge = 0;
		double cgstCharge = 0;
		Double sgstRate = 0D;
		Double cgstRate = 0D;
		D010004 onlineBatch = null;

		Date drOperationDate = getOpenDateNew(lbrCode, session);

		D001004 READSCHGREC = DataUtils.getSystemParameter(lbrCode, "READSCHGREC");

		String ChargeType = "";
		int insType;
		String gstChargeType = "";
		if("NEFT".equals(narration.substring(0, 4))) {
			ChargeType = ConfigurationLoader.getParameters(false).getProperty("NEFT_CHARGE_TYPE").trim();
			insType = Integer
					.parseInt(ConfigurationLoader.getParameters(false).getProperty("NEFT_CHG_INST_TYPE").trim());
			gstChargeType = ConfigurationLoader.getParameters(false).getProperty("NEFT_GST_CHARGE_TYPE").trim();
		}else {
			ChargeType = ConfigurationLoader.getParameters(false).getProperty("IMPS_CHARGE_TYPE").trim();
			insType = Integer
					.parseInt(ConfigurationLoader.getParameters(false).getProperty("IMPS_CHG_INST_TYPE").trim());

			gstChargeType = ConfigurationLoader.getParameters(false).getProperty("IMPS_GST_CHARGE_TYPE").trim();

		}
			
		
		
		
		if (READSCHGREC.getValue().trim().equalsIgnoreCase("N")) {

			D130001 d130001 = DataUtils.getstopChequeCharges(lbrCode, ChargeType, insType + "");// chgType=2
			// for // stop // cheque logger.error("d130001::>>>" +
			if (d130001 == null) {
				logger.error(MSGConstants.SERVICE_CHARGES_NOT_FOUND + " in D130001.");
				logger.error("Transaction unsuccessful");
				resultMap.put(Code.RESULT, Code.ERROR);
				// stopPaymentRes.setErrorMessage(MSGConstants.SERVICE_CHARGES_NOT_FOUND);
				return resultMap;
			}

			// amount = d130001.getFlatRate();
			batch = d130001.getBatchCd();

			logger.error("drOperationDate::>>" + drOperationDate);
			logger.error("batch::>>" + batch);
			logger.error("lbrCode::>>" + lbrCode);
			onlineBatch = VoucherMPOS.getD010004(lbrCode, batch.trim(), drOperationDate);
			logger.error("onlineBatch::>>" + onlineBatch);
			logger.error("onlineBatch.getStat()::>>" + onlineBatch.getStat());
			stat = onlineBatch.getStat() + "";
			logger.error("Status::>>" + stat);

			plCrAccNo = d130001.getPlCrAcctId();
			logger.error("amount::>>" + amount);
			logger.error("batch::>>" + batch);
			logger.error("plCrAccNo::>>" + plCrAccNo);

			/*
			 * serTaxRate = d130031.getSerTaxRate() / 100; eduCharges
			 * =d130031.getEduCesRate() / 100; logger.error("serTaxRate::>>" + serTaxRate);
			 * logger.error("eduCharges::>>" + eduCharges);
			 */
			List<GstChargesMaster> gstChargesMasters = DataUtils.getLatestCharge(gstChargeType, drOperationDate);

			if (!gstChargesMasters.isEmpty()) {
				GstChargesMaster gstChargesMaster = gstChargesMasters.get(0);
				sgstRate = gstChargesMaster.getSgstrate();
				cgstRate = gstChargesMaster.getCgstrate();
				sgstCharge = amount * (sgstRate / 100);
				cgstCharge = amount * (cgstRate / 100);
				sgstCharge = Math.round(sgstCharge * 100.0) / 100.0;
				cgstCharge = Math.round(cgstCharge * 100.0) / 100.0;
			}

			finalAmt = sgstCharge;// SGSTAMount
			lastAmt = cgstCharge;// CGSTAMount
			osAmt = (finalAmt + lastAmt + amount);

		} else {

			D130014 d130014 = DataUtils.getIMPSChargeType(lbrCode, Integer.parseInt(ChargeType));// chgType=2
			// for
			// stop
			// cheque

			if (d130014 == null) {

				logger.error(MSGConstants.SERVICE_CHARGES_NOT_FOUND + " in D130001.");
				logger.error("Transaction unsuccessful");
				resultMap.put(Code.RESULT, Code.ERROR);
				return resultMap;
			} else {
				logger.error("d130014::>>>" + d130014);
			}
			// amount = d130014.getFlatRate();

			/*
			 * batch = d130014.getBatchCd(); logger.error("drOperationDate::>>" +
			 * drOperationDate); logger.error("batch::>>" + batch);
			 * logger.error("lbrCode::>>" + lbrCode); onlineBatch =
			 * VoucherMPOS.getD010004(lbrCode, batch.trim(), drOperationDate);
			 */

			batchCode = Props.getBatchProperty("IMPS");
			logger.error("Batch Code Form Properties File : " + batchCode);
			String batchCodes[] = batchCode.split("~");
			logger.error("Number of Batches is " + batchCodes.length + "\t Names are " + Arrays.toString(batchCodes));

			/// Get Selected Batch.
			onlineBatch = getSelectedBatchNew(lbrCode, batchCodes, drOperationDate, session);
			logger.error("onlineBatch::>>" + onlineBatch);
			logger.error("onlineBatch.getStat()::>>" + onlineBatch.getStat());
			stat = onlineBatch.getStat() + "";
			logger.error("Status::>>" + stat);

			plCrAccNo = d130014.getPlCrAcctId();
			logger.error("amount::>>" + amount);
			logger.error("batch::>>" + batch);
			logger.error("plCrAccNo::>>" + plCrAccNo);
			batch = onlineBatch.getId().getBatchCd();
			/*
			 * serTaxRate = d130031.getSerTaxRate() / 100; eduCharges =
			 * d130031.getEduCesRate() / 100; logger.error("serTaxRate::>>" + serTaxRate);
			 * logger.error("eduCharges::>>" + eduCharges);
			 */

			List<GstChargesMaster> gstChargesMasters = DataUtils.getLatestCharge(gstChargeType, drOperationDate);
			Map<String, Object> responseMap = new HashMap();
			// responseMap.put("sgstCharge", 0);
			// responseMap.put("cgstCharge", 0);

			if (!gstChargesMasters.isEmpty()) {
				GstChargesMaster gstChargesMaster = gstChargesMasters.get(0);
				sgstRate = gstChargesMaster.getSgstrate();
				cgstRate = gstChargesMaster.getCgstrate();
				sgstCharge = amount * (sgstRate / 100);
				cgstCharge = amount * (cgstRate / 100);
				sgstCharge = Math.round(sgstCharge * 100.0) / 100.0;
				cgstCharge = Math.round(cgstCharge * 100.0) / 100.0;
			}

			finalAmt = sgstCharge;// SGSTAMount
			lastAmt = cgstCharge;// CGSTAMount
			osAmt = (finalAmt + lastAmt + amount);

		}

		logger.error("Total outstanding Amount::>>" + osAmt);
		D009022 sourceAccount = DataUtils.getAccountMaster(lbrCode, accNo);// lbrCode,
		// acctNo
		logger.error("sourceAccount ::>>" + sourceAccount);
		if (sourceAccount == null) {
			logger.error("Transaction unsuccessful");
			resultMap.put(Code.RESULT, Code.ERROR);
			return resultMap;
		}
		TransactionValidationResponse response = TransactionServiceImpl.validateAccount(sourceAccount, "" + osAmt, "D");
		boolean voucherFlag = true;
		logger.error("response.getResponse():>>" + response.getResponse());
		logger.error("response.getErrorMsg()::>>>" + response.getErrorMsg());

		if ((stat.equalsIgnoreCase("1") || stat.equalsIgnoreCase("2"))
				&& (response.getResponse().equalsIgnoreCase(MSGConstants.SUCCESS))) {
			logger.error("Batch is open");

			String bal = CoreTransactionMPOS.balance(lbrCode, accNo, osAmt, "D", session);
			logger.error("bal::>>" + bal);
		} else {

			logger.error("Batch is not open OR Account is not valid");
			logger.error("Online Batch not Open charges is placed in recovery master. OR Account is not valid");
			logger.error("lbrCode::>>" + lbrCode);
			logger.error("acctNo::>>>" + accNo);
			D130008Id id = new D130008Id();
			id.setChgType(Byte.valueOf(ChargeType));
			id.setLbrCode(lbrCode);
			id.setPrdAcctId(accNo);
			D130008 d130008Obj = session.get(D130008.class, id);
			logger.error("d130008Obj::>>" + d130008Obj);
			if (d130008Obj == null) {
				logger.error("d130008Obj is " + d130008Obj);
				D130008 d130008 = TransactionServiceImpl.prepareRecoveryChgmaster(drOperationDate, osAmt, ChargeType,
						lbrCode, accNo);
				session.save(d130008);
				// session.flush();
			} else {
				logger.error("d130008Obj is not null");
				double amt = d130008Obj.getOsAmt() + osAmt;
				d130008Obj.setOsAmt(amt);
				session.update(d130008Obj);
				// session.flush();
			}
			/*
			 * D010010 d010010 = prepareStopChequeEntry(lbrCode, accNo,
			 * String.valueOf(insType), chequeNo, drAccount.getLongName(), "Stop Cheque/" +
			 * chequeNo, drOperationDate); logger.error("stopped"); session.save(d010010);
			 */

			/*
			 * Query query = session.createSQLQuery(
			 * "UPDATE D009044 SET Status = 1 WHERE LBrCode =" + lbrCode +
			 * " AND IssuedTo = '" + acctNo + "' AND InsType = '" + insType +
			 * "' AND InstruNo = '" + chequeNo + "'"); // query2.setParameter("amt", osAmt);
			 * // query2.setParameter("lbrcode", lbrCode); //
			 * query2.setParameter("entrydate", drOperationDate); //
			 * query2.setParameter("batchcd", batch); query.executeUpdate(); t.commit();
			 * session.close(); session = null; t = null;
			 */
			/*
			 * stopPaymentRes.setResponse(MSGConstants.SUCCESS);
			 * stopPaymentRes.setErrorMessage(
			 * "Dear customer,as per your request cheque number " + chequeNo +
			 * " is stopped successfully.");//
			 * stopPaymentRes.setTransactionId(DataUtils.getNextRRN());
			 */
			logger.error("Transaction successful");
			resultMap.put(Code.RESULT, Code.SUCCESS);
			return resultMap;
		}

		// logger.error("finalAmt::>>"+finalAmt);
		// logger.error("lastAmt::>>>"+lastAmt);

		/*
		 * String staxAccNo = d130031.getSerTaxAcctId(); String sbcAccNo =
		 * d130031.getEduCesAcctId();
		 * 
		 * logger.error("staxAccNo::>>" + staxAccNo); logger.error("sbcAccNo::>>>" +
		 * sbcAccNo);
		 */

		String sgstCreditAcctId = null, cgstCreditAcctId = null;
		String cgstCreditPrdCd = null;
		List<GstChargesMaster> gstChargesMasters = DataUtils.getLatestCharge(gstChargeType, drOperationDate);
		if (!gstChargesMasters.isEmpty()) {
			GstChargesMaster gstChargesMaster = gstChargesMasters.get(0);
			sgstCreditAcctId = gstChargesMaster.getSgstacctid();
			cgstCreditAcctId = gstChargesMaster.getCgstacctid();
			// String cgstCreditPrdCd = cgstCreditAcctId.substring(0, 8).trim();
		}

		logger.error("sourceAccount::>>" + sourceAccount);

		int scrollNo = VoucherMPOS.getNextScrollNo();
		int setNo = VoucherMPOS.getNextSetNo();
		logger.error("setNo::>" + setNo);
		logger.error("scrollNo::>>" + scrollNo);
		// String rrn = DataUtils.getNextRRN();

		D009040 d009040Dr = TransactionServiceImpl.prepareD009040Object(batch, drOperationDate, lbrCode, setNo,
				scrollNo, scrollNo, accNo, session, osAmt, narration, "DR", rrn);
		d009040Dr.setActivityType("CHRGDR");
		d009040Dr.setPostDate(onlineBatch.getPostDate());
		d009040Dr.setValueDate(new Date());
		d009040Dr.setFeffDate(onlineBatch.getFeffDate());
		
		VoucherMPOS.updateProductBalances(osAmt + trnAmt, lbrCode, "D", accNo.substring(0, 8).trim(), session);

		D009040 d009040CrSGST = TransactionServiceImpl.prepareD009040Object(batch, drOperationDate, lbrCode, setNo,
				VoucherMPOS.getNextScrollNo(), scrollNo, sgstCreditAcctId, session, finalAmt, "SGST for " + narration,
				"CR", rrn);
		d009040CrSGST.setMainAcctId(accNo);
		d009040CrSGST.setCashFlowType("CGSTCHCR");
		d009040CrSGST.setPostDate(onlineBatch.getPostDate());
		d009040CrSGST.setValueDate(new Date());
		d009040CrSGST.setFeffDate(onlineBatch.getFeffDate());
		
		D009021 prdSGST = DataUtils.getProductMaster(String.valueOf(lbrCode), sgstCreditAcctId.substring(0, 8).trim());
		if ('P' != prdSGST.getAcctOpenLevel()) {
			String plSGST = CoreTransactionMPOS.balance(lbrCode, sgstCreditAcctId, finalAmt, "C", session);
			logger.error("bal::>>" + plSGST);
		}
		VoucherMPOS.updateProductBalances(finalAmt, lbrCode, "C", sgstCreditAcctId.substring(0, 8).trim(), session);

		// d009040CrSGST.setActivityType("CHRGCR");
		D009040 d009040CrCGST = TransactionServiceImpl.prepareD009040Object(batch, drOperationDate, lbrCode, setNo,
				VoucherMPOS.getNextScrollNo(), scrollNo, cgstCreditAcctId, session, lastAmt, "CGST for " + narration,
				"CR", rrn);
		d009040CrCGST.setMainAcctId(accNo);
		d009040CrCGST.setPostDate(onlineBatch.getPostDate());
		d009040CrCGST.setValueDate(new Date());
		d009040CrCGST.setFeffDate(onlineBatch.getFeffDate());
		
		D009021 prdCGST = DataUtils.getProductMaster(String.valueOf(lbrCode), cgstCreditAcctId.substring(0, 8).trim());
		if ('P' != prdCGST.getAcctOpenLevel()) {
			String plCGST = CoreTransactionMPOS.balance(lbrCode, cgstCreditAcctId, lastAmt, "C", session);
			logger.error("bal::>>" + plCGST);
		}

		VoucherMPOS.updateProductBalances(lastAmt, lbrCode, "C", cgstCreditAcctId.substring(0, 8).trim(), session);
		d009040CrCGST.setCashFlowType("CGSTCHCR");
		D009040 d009040PLACC = TransactionServiceImpl.prepareD009040Object(batch, drOperationDate, lbrCode, setNo,
				VoucherMPOS.getNextScrollNo(), scrollNo, plCrAccNo, session, amount, narration, "CR", rrn);
		String plAcct = CoreTransactionMPOS.balance(lbrCode, plCrAccNo, amount, "C", session);
		logger.error("bal::>>" + plAcct);
		d009040PLACC.setMainAcctId(accNo);
		d009040PLACC.setPostDate(onlineBatch.getPostDate());
		d009040PLACC.setValueDate(new Date());
		d009040PLACC.setFeffDate(onlineBatch.getFeffDate());
		
		VoucherMPOS.updateProductBalances(amount, lbrCode, "C", plCrAccNo.substring(0, 8).trim(), session);

		d009040PLACC.setActivityType("CHRGCR");
		/*
		 * D010010 d010010 = prepareStopChequeEntry(lbrCode, acctNo,
		 * String.valueOf(insType), chequeNo, drAccount.getLongName(),
		 * "Stop Cheque Request", drOperationDate); logger.error("d010010::>>>" +
		 * d010010); logger.error("voucherFlag::>>" + voucherFlag);
		 * session.save(d010010);
		 */
		if (voucherFlag) {
			logger.error("d009040PLACC::>>" + d009040PLACC);
			logger.error("d009040Dr::>>" + d009040Dr);

			logger.error("d009040CrSTAX::>>" + d009040CrSGST);
			logger.error("d009040CrSBC::>>" + d009040CrCGST);

			session.save(d009040PLACC);
			session.save(d009040Dr);
			if (finalAmt > 0)
				session.save(d009040CrSGST);
			if (lastAmt > 0)
				session.save(d009040CrCGST);

			if (finalAmt > 0 || lastAmt > 0) {
				GstTransactionHistory tranHistory = new GstTransactionHistory();
				tranHistory.setId(new GstTransactionHistoryId());
				tranHistory.getId().setSetno(setNo);
				tranHistory.getId().setBatchcd(batch);
				tranHistory.getId().setEntrydate(drOperationDate);
				tranHistory.getId().setLbrcode(lbrCode);
				tranHistory.getId().setScrollno(scrollNo);
				tranHistory.getId().setUniquerefno((lbrCode + DateUtil.getcurrentDateForPDF()).substring(0, 18));
				tranHistory.setActivitytype("DR");
				tranHistory.setBooktype(d009040Dr.getBookType());
				tranHistory.setCashflowtype("CGSTCHCR");
				tranHistory.setDrcr("C");
				tranHistory.setCurrdate(new Date());
				tranHistory.setCustno(sourceAccount.getCustNo());
				tranHistory.setFcytrnamt(sgstCharge + cgstCharge);
				tranHistory.setParticulars("CGST Charges");
				tranHistory.setSgstrate(sgstRate);
				tranHistory.setSgstamt(sgstCharge);
				tranHistory.setCgstrate(cgstRate);
				tranHistory.setCgstamt(cgstCharge);
				tranHistory.setCessamt(0D);
				tranHistory.setCessrate(0D);
				tranHistory.setCanceledflag(" ");
				tranHistory.setCgst(" ");
				tranHistory.setSgst(" ");
				tranHistory.setIgst(" ");
				tranHistory.setGstno(" ");
				tranHistory.setVcracctid(cgstCreditAcctId);
				tranHistory.setMainacctid(accNo);
				tranHistory.setFromgstno(DataUtils.getSystemParameter(lbrCode, "GSTNINNO").getValue());
				tranHistory.setSaccode(DataUtils.getSystemParameter(0, "SACCODE").getValue());

				D009122Id id = new D009122Id(lbrCode, accNo);
				D009122 d009122 = session.get(D009122.class, id);
				tranHistory.setTogstno(d009122.getGstInNo());

				tranHistory.setUploaddate(drOperationDate);
				tranHistory.setHsncode(" ");
				tranHistory.setIgstamt(0D);
				tranHistory.setIgst(" ");
				tranHistory.setIgstrate(0D);
				tranHistory.setInvoicedate(drOperationDate);
				tranHistory.setServicetax(0D);
				tranHistory.setServicetaxamt(0D);
				tranHistory.setMainscrollno(scrollNo);
				try {
					String stateCd = (String) session

							.createNativeQuery("SELECT B.StateCd FROM D001003 A, D500028 B WHERE A.CityCd="
									+ "B.PlaceCd AND A.CityCd=(SELECT CityCd FROM D009011 WHERE CustNo="
									+ sourceAccount.getCustNo() + ") AND A.PBrCode=" + lbrCode)
							.getSingleResult();
					tranHistory.setDeststate(stateCd);
					tranHistory.setSourcestate(stateCd);
				} catch (Exception ec) {
					tranHistory.setDeststate(" ");
					tranHistory.setSourcestate(" ");
				}

				tranHistory.setChgType(Integer.parseInt(ChargeType));
				logger.error(tranHistory.toString());
				session.save(tranHistory);

			}

			Query query = session.createSQLQuery(
					"UPDATE D010004 SET TotalDrVcrs=TotalDrVcrs+1, TotalDrAmtLcy = TotalDrAmtLcy +:amt WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd");
			query.setParameter("amt", osAmt);
			query.setParameter("lbrcode", lbrCode);
			query.setParameter("entrydate", drOperationDate);
			query.setParameter("batchcd", batch);
			query.executeUpdate();

			Query query1 = session.createSQLQuery(
					"UPDATE D010004 SET TotalCrVcrs=TotalCrVcrs+3, TotalCrAmtLcy = TotalCrAmtLcy +:amt WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd");
			query1.setParameter("amt", osAmt);
			query1.setParameter("lbrcode", lbrCode);
			query1.setParameter("entrydate", drOperationDate);
			query1.setParameter("batchcd", batch);
			query1.executeUpdate();

			/*
			 * Query query2 = session.createSQLQuery(
			 * "UPDATE D009044 SET Status = 1 WHERE LBrCode =" + lbrCode +
			 * " AND IssuedTo = '" + acctNo + "' AND InsType = '" + insType +
			 * "' AND InstruNo = '" + chequeNo + "'");
			 */
			// query2.setParameter("amt", osAmt);
			// query2.setParameter("lbrcode", lbrCode);
			// query2.setParameter("entrydate", drOperationDate);
			// query2.setParameter("batchcd", batch);
			// query2.executeUpdate();
			logger.error("Transaction successful");
			resultMap.put(Code.RESULT, Code.SUCCESS);
		}

		// return stopPaymentRes;

		return resultMap;
	}

	@SuppressWarnings("deprecation")
	public static HashMap<String, String> gstRevVoucherEntry(int lbrCode, String rrn, Session session, String accNo,
			String narration, Double amount) {

		HashMap<String, String> resultMap = new HashMap<>();

		// double amount = 0.0;
		double serTaxRate = 0.0;
		double eduCharges = 0.0;
		double finalAmt = 0.0;
		double lastAmt = 0.0;
		double osAmt = 0.0;
		String batch = "";
		String plCrAccNo = "";
		String stat = "";
		String batchCode = "";
		double sgstCharge = 0;
		double cgstCharge = 0;
		Double sgstRate = 0D;
		Double cgstRate = 0D;
		D010004 onlineBatch = null;

		Date drOperationDate = getOpenDateNew(lbrCode, session);

		D001004 READSCHGREC = DataUtils.getSystemParameter(lbrCode, "READSCHGREC");
		String ChargeType = "";
		int insType;
		String gstChargeType = "";
		if("NEFT".equals(narration.substring(0, 4))) {
			ChargeType = ConfigurationLoader.getParameters(false).getProperty("NEFT_CHARGE_TYPE").trim();
			insType = Integer
					.parseInt(ConfigurationLoader.getParameters(false).getProperty("NEFT_CHG_INST_TYPE").trim());
			gstChargeType = ConfigurationLoader.getParameters(false).getProperty("NEFT_GST_CHARGE_TYPE").trim();
		}else {
			ChargeType = ConfigurationLoader.getParameters(false).getProperty("IMPS_CHARGE_TYPE").trim();
			insType = Integer
					.parseInt(ConfigurationLoader.getParameters(false).getProperty("IMPS_CHG_INST_TYPE").trim());


			gstChargeType = ConfigurationLoader.getParameters(false).getProperty("IMPS_GST_CHARGE_TYPE").trim();

		}
		 
		
		if (READSCHGREC.getValue().trim().equalsIgnoreCase("N")) {

			D130001 d130001 = DataUtils.getstopChequeCharges(lbrCode, ChargeType, insType + "");// chgType=2
			// for // stop // cheque logger.error("d130001::>>>" +
			if (d130001 == null) {
				logger.error(MSGConstants.SERVICE_CHARGES_NOT_FOUND + " in D130001.");
				logger.error("Transaction unsuccessful");
				resultMap.put(Code.RESULT, Code.ERROR);
				// stopPaymentRes.setErrorMessage(MSGConstants.SERVICE_CHARGES_NOT_FOUND);
				return resultMap;
			}

			// amount = d130001.getFlatRate();
			batch = d130001.getBatchCd();

			logger.error("drOperationDate::>>" + drOperationDate);
			logger.error("batch::>>" + batch);
			logger.error("lbrCode::>>" + lbrCode);
			onlineBatch = VoucherMPOS.getD010004(lbrCode, batch.trim(), drOperationDate);
			logger.error("onlineBatch::>>" + onlineBatch);
			logger.error("onlineBatch.getStat()::>>" + onlineBatch.getStat());
			stat = onlineBatch.getStat() + "";
			logger.error("Status::>>" + stat);

			plCrAccNo = d130001.getPlCrAcctId();
			logger.error("amount::>>" + amount);
			logger.error("batch::>>" + batch);
			logger.error("plCrAccNo::>>" + plCrAccNo);

			/*
			 * serTaxRate = d130031.getSerTaxRate() / 100; eduCharges
			 * =d130031.getEduCesRate() / 100; logger.error("serTaxRate::>>" + serTaxRate);
			 * logger.error("eduCharges::>>" + eduCharges);
			 */
			List<GstChargesMaster> gstChargesMasters = DataUtils.getLatestCharge(gstChargeType, drOperationDate);

			if (!gstChargesMasters.isEmpty()) {
				GstChargesMaster gstChargesMaster = gstChargesMasters.get(0);
				sgstRate = gstChargesMaster.getSgstrate();
				cgstRate = gstChargesMaster.getCgstrate();
				sgstCharge = amount * (sgstRate / 100);
				cgstCharge = amount * (cgstRate / 100);
				sgstCharge = Math.round(sgstCharge * 100.0) / 100.0;
				cgstCharge = Math.round(cgstCharge * 100.0) / 100.0;
			}

			finalAmt = sgstCharge;// SGSTAMount
			lastAmt = cgstCharge;// CGSTAMount
			osAmt = (finalAmt + lastAmt + amount);

		} else {

			D130014 d130014 = DataUtils.getIMPSChargeType(lbrCode, Integer.parseInt(ChargeType));// chgType=2
			// for
			// stop
			// cheque

			if (d130014 == null) {

				logger.error(MSGConstants.SERVICE_CHARGES_NOT_FOUND + " in D130001.");
				logger.error("Transaction unsuccessful");
				resultMap.put(Code.RESULT, Code.ERROR);
				return resultMap;
			} else {
				logger.error("d130014::>>>" + d130014);
			}
			// amount = d130014.getFlatRate();

			/*
			 * batch = d130014.getBatchCd(); logger.error("drOperationDate::>>" +
			 * drOperationDate); logger.error("batch::>>" + batch);
			 * logger.error("lbrCode::>>" + lbrCode); onlineBatch =
			 * VoucherMPOS.getD010004(lbrCode, batch.trim(), drOperationDate);
			 */

			batchCode = Props.getBatchProperty("IMPS");
			logger.error("Batch Code Form Properties File : " + batchCode);
			String batchCodes[] = batchCode.split("~");
			logger.error("Number of Batches is " + batchCodes.length + "\t Names are " + Arrays.toString(batchCodes));

			/// Get Selected Batch.
			onlineBatch = getSelectedBatchNew(lbrCode, batchCodes, drOperationDate, session);

			logger.error("onlineBatch::>>" + onlineBatch);
			logger.error("onlineBatch.getStat()::>>" + onlineBatch.getStat());
			stat = onlineBatch.getStat() + "";
			logger.error("Status::>>" + stat);

			plCrAccNo = d130014.getPlCrAcctId();
			logger.error("amount::>>" + amount);
			logger.error("batch::>>" + batch);
			logger.error("plCrAccNo::>>" + plCrAccNo);

			/*
			 * serTaxRate = d130031.getSerTaxRate() / 100; eduCharges =
			 * d130031.getEduCesRate() / 100; logger.error("serTaxRate::>>" + serTaxRate);
			 * logger.error("eduCharges::>>" + eduCharges);
			 */

			List<GstChargesMaster> gstChargesMasters = DataUtils.getLatestCharge(gstChargeType, drOperationDate);
			Map<String, Object> responseMap = new HashMap();
			// responseMap.put("sgstCharge", 0);
			// responseMap.put("cgstCharge", 0);

			if (!gstChargesMasters.isEmpty()) {
				GstChargesMaster gstChargesMaster = gstChargesMasters.get(0);
				sgstRate = gstChargesMaster.getSgstrate();
				cgstRate = gstChargesMaster.getCgstrate();
				sgstCharge = amount * (sgstRate / 100);
				cgstCharge = amount * (cgstRate / 100);
				sgstCharge = Math.round(sgstCharge * 100.0) / 100.0;
				cgstCharge = Math.round(cgstCharge * 100.0) / 100.0;
			}

			finalAmt = sgstCharge;// SGSTAMount
			lastAmt = cgstCharge;// CGSTAMount
			osAmt = (finalAmt + lastAmt + amount);

		}

		logger.error("Total outstanding Amount::>>" + osAmt);
		D009022 sourceAccount = DataUtils.getAccountMaster(lbrCode, accNo);// lbrCode,
		// acctNo
		logger.error("sourceAccount ::>>" + sourceAccount);
		if (sourceAccount == null) {
			logger.error("Transaction unsuccessful");
			resultMap.put(Code.RESULT, Code.ERROR);
			return resultMap;
		}
		TransactionValidationResponse response = TransactionServiceImpl.validateAccount(sourceAccount, "" + osAmt, "C");
		boolean voucherFlag = true;
		logger.error("response.getResponse():>>" + response.getResponse());
		logger.error("response.getErrorMsg()::>>>" + response.getErrorMsg());

		if ((stat.equalsIgnoreCase("1") || stat.equalsIgnoreCase("2"))
				&& (response.getResponse().equalsIgnoreCase(MSGConstants.SUCCESS))) {
			logger.error("Batch is open");

			String bal = CoreTransactionMPOS.balance(lbrCode, accNo, osAmt, "C", session);
			logger.error("bal::>>" + bal);
		} else {

			logger.error("Batch is not open OR Account is not valid");
			logger.error("Online Batch not Open charges is placed in recovery master. OR Account is not valid");
			logger.error("lbrCode::>>" + lbrCode);
			logger.error("acctNo::>>>" + accNo);
			D130008Id id = new D130008Id();
			id.setChgType(Byte.valueOf(ChargeType));
			id.setLbrCode(lbrCode);
			id.setPrdAcctId(accNo);
			D130008 d130008Obj = session.get(D130008.class, id);
			logger.error("d130008Obj::>>" + d130008Obj);
			if (d130008Obj == null) {
				logger.error("d130008Obj is " + d130008Obj);
				D130008 d130008 = TransactionServiceImpl.prepareRecoveryChgmaster(drOperationDate, osAmt, ChargeType,
						lbrCode, accNo);
				session.save(d130008);
				// session.flush();
			} else {
				logger.error("d130008Obj is not null");
				double amt = d130008Obj.getOsAmt() + osAmt;
				d130008Obj.setOsAmt(amt);
				session.update(d130008Obj);
				// session.flush();
			}
			/*
			 * D010010 d010010 = prepareStopChequeEntry(lbrCode, accNo,
			 * String.valueOf(insType), chequeNo, drAccount.getLongName(), "Stop Cheque/" +
			 * chequeNo, drOperationDate); logger.error("stopped"); session.save(d010010);
			 */

			/*
			 * Query query = session.createSQLQuery(
			 * "UPDATE D009044 SET Status = 1 WHERE LBrCode =" + lbrCode +
			 * " AND IssuedTo = '" + acctNo + "' AND InsType = '" + insType +
			 * "' AND InstruNo = '" + chequeNo + "'"); // query2.setParameter("amt", osAmt);
			 * // query2.setParameter("lbrcode", lbrCode); //
			 * query2.setParameter("entrydate", drOperationDate); //
			 * query2.setParameter("batchcd", batch); query.executeUpdate(); t.commit();
			 * session.close(); session = null; t = null;
			 */
			/*
			 * stopPaymentRes.setResponse(MSGConstants.SUCCESS);
			 * stopPaymentRes.setErrorMessage(
			 * "Dear customer,as per your request cheque number " + chequeNo +
			 * " is stopped successfully.");//
			 * stopPaymentRes.setTransactionId(DataUtils.getNextRRN());
			 */
			logger.error("Transaction successful");
			resultMap.put(Code.RESULT, Code.SUCCESS);
			return resultMap;
		}

		// logger.error("finalAmt::>>"+finalAmt);
		// logger.error("lastAmt::>>>"+lastAmt);

		/*
		 * String staxAccNo = d130031.getSerTaxAcctId(); String sbcAccNo =
		 * d130031.getEduCesAcctId();
		 * 
		 * logger.error("staxAccNo::>>" + staxAccNo); logger.error("sbcAccNo::>>>" +
		 * sbcAccNo);
		 */

		String sgstCreditAcctId = null, cgstCreditAcctId = null;
		String cgstCreditPrdCd = null;
		List<GstChargesMaster> gstChargesMasters = DataUtils.getLatestCharge(gstChargeType, drOperationDate);
		if (!gstChargesMasters.isEmpty()) {
			GstChargesMaster gstChargesMaster = gstChargesMasters.get(0);
			sgstCreditAcctId = gstChargesMaster.getSgstacctid();
			cgstCreditAcctId = gstChargesMaster.getCgstacctid();
			// String cgstCreditPrdCd = cgstCreditAcctId.substring(0, 8).trim();
		}

		logger.error("sourceAccount::>>" + sourceAccount);

		int scrollNo = VoucherMPOS.getNextScrollNo();
		int setNo = VoucherMPOS.getNextSetNo();
		logger.error("setNo::>" + setNo);
		logger.error("scrollNo::>>" + scrollNo);
		// String rrn = DataUtils.getNextRRN();

		D009040 d009040Dr = TransactionServiceImpl.prepareD009040Object(batch, drOperationDate, lbrCode, setNo,
				scrollNo, scrollNo, accNo, session, osAmt, narration, "CR", rrn);
		d009040Dr.setActivityType("CHRGCR");
		
		d009040Dr.setPostDate(onlineBatch.getPostDate());
		d009040Dr.setValueDate(new Date());
		d009040Dr.setFeffDate(onlineBatch.getFeffDate());
		
		VoucherMPOS.updateProductBalances(osAmt, lbrCode, "C", accNo.substring(0, 8).trim(), session);

		D009040 d009040CrSGST = TransactionServiceImpl.prepareD009040Object(batch, drOperationDate, lbrCode, setNo,
				VoucherMPOS.getNextScrollNo(), scrollNo, sgstCreditAcctId, session, finalAmt, "SGST for " + narration,
				"DR", rrn);
		d009040CrSGST.setMainAcctId(accNo);
		d009040CrSGST.setCashFlowType("CGSTCHDR");
		
		d009040CrSGST.setPostDate(onlineBatch.getPostDate());
		d009040CrSGST.setValueDate(new Date());
		d009040CrSGST.setFeffDate(onlineBatch.getFeffDate());
		
		D009021 prdSGST = DataUtils.getProductMaster(String.valueOf(lbrCode), sgstCreditAcctId.substring(0, 8).trim());
		if ('P' != prdSGST.getAcctOpenLevel()) {
			String plSGST = CoreTransactionMPOS.balance(lbrCode, sgstCreditAcctId, finalAmt, "D", session);
			logger.error("bal::>>" + plSGST);
		}
		VoucherMPOS.updateProductBalances(finalAmt, lbrCode, "D", sgstCreditAcctId.substring(0, 8).trim(), session);

		// d009040CrSGST.setActivityType("CHRGCR");
		D009040 d009040CrCGST = TransactionServiceImpl.prepareD009040Object(batch, drOperationDate, lbrCode, setNo,
				VoucherMPOS.getNextScrollNo(), scrollNo, cgstCreditAcctId, session, lastAmt, "CGST for " + narration,
				"DR", rrn);
		d009040CrCGST.setMainAcctId(accNo);
		
		d009040CrCGST.setPostDate(onlineBatch.getPostDate());
		d009040CrCGST.setValueDate(new Date());
		d009040CrCGST.setFeffDate(onlineBatch.getFeffDate());
		
		D009021 prdCGST = DataUtils.getProductMaster(String.valueOf(lbrCode), cgstCreditAcctId.substring(0, 8).trim());
		if ('P' != prdCGST.getAcctOpenLevel()) {
			String plCGST = CoreTransactionMPOS.balance(lbrCode, cgstCreditAcctId, lastAmt, "D", session);
			logger.error("bal::>>" + plCGST);
		}

		VoucherMPOS.updateProductBalances(lastAmt, lbrCode, "D", cgstCreditAcctId.substring(0, 8).trim(), session);
		d009040CrCGST.setCashFlowType("CGSTCHCR");
		D009040 d009040PLACC = TransactionServiceImpl.prepareD009040Object(batch, drOperationDate, lbrCode, setNo,
				VoucherMPOS.getNextScrollNo(), scrollNo, plCrAccNo, session, amount, narration, "DR", rrn);
		String plAcct = CoreTransactionMPOS.balance(lbrCode, plCrAccNo, amount, "D", session);
		logger.error("bal::>>" + plAcct);
		d009040PLACC.setMainAcctId(accNo);
		
		d009040PLACC.setPostDate(onlineBatch.getPostDate());
		d009040PLACC.setValueDate(new Date());
		d009040PLACC.setFeffDate(onlineBatch.getFeffDate());
		
		VoucherMPOS.updateProductBalances(amount, lbrCode, "D", plCrAccNo.substring(0, 8).trim(), session);

		d009040PLACC.setActivityType("CHRGDR");
		/*
		 * D010010 d010010 = prepareStopChequeEntry(lbrCode, acctNo,
		 * String.valueOf(insType), chequeNo, drAccount.getLongName(),
		 * "Stop Cheque Request", drOperationDate); logger.error("d010010::>>>" +
		 * d010010); logger.error("voucherFlag::>>" + voucherFlag);
		 * session.save(d010010);
		 */
		if (voucherFlag) {
			logger.error("d009040PLACC::>>" + d009040PLACC);
			logger.error("d009040Dr::>>" + d009040Dr);

			logger.error("d009040CrSTAX::>>" + d009040CrSGST);
			logger.error("d009040CrSBC::>>" + d009040CrCGST);

			session.save(d009040PLACC);
			session.save(d009040Dr);
			if (finalAmt > 0)
				session.save(d009040CrSGST);
			if (lastAmt > 0)
				session.save(d009040CrCGST);

			if (finalAmt > 0 || lastAmt > 0) {
				GstTransactionHistory tranHistory = new GstTransactionHistory();
				tranHistory.setId(new GstTransactionHistoryId());
				tranHistory.getId().setSetno(setNo);
				tranHistory.getId().setBatchcd(batch);
				tranHistory.getId().setEntrydate(drOperationDate);
				tranHistory.getId().setLbrcode(lbrCode);
				tranHistory.getId().setScrollno(scrollNo);
				tranHistory.getId().setUniquerefno((lbrCode + DateUtil.getcurrentDateForPDF()).substring(0, 18));
				tranHistory.setActivitytype("CR");
				tranHistory.setBooktype(d009040Dr.getBookType());
				tranHistory.setCashflowtype("CGSTCHDR");
				tranHistory.setDrcr("D");
				tranHistory.setCurrdate(new Date());
				tranHistory.setCustno(sourceAccount.getCustNo());
				tranHistory.setFcytrnamt(sgstCharge + cgstCharge);
				tranHistory.setParticulars("CGST Charges");
				tranHistory.setSgstrate(sgstRate);
				tranHistory.setSgstamt(sgstCharge);
				tranHistory.setCgstrate(cgstRate);
				tranHistory.setCgstamt(cgstCharge);
				tranHistory.setCessamt(0D);
				tranHistory.setCessrate(0D);
				tranHistory.setCanceledflag(" ");
				tranHistory.setCgst(" ");
				tranHistory.setSgst(" ");
				tranHistory.setIgst(" ");
				tranHistory.setGstno(" ");
				tranHistory.setVcracctid(cgstCreditAcctId);
				tranHistory.setMainacctid(accNo);
				tranHistory.setFromgstno(DataUtils.getSystemParameter(lbrCode, "GSTNINNO").getValue());
				tranHistory.setSaccode(DataUtils.getSystemParameter(0, "SACCODE").getValue());

				D009122Id id = new D009122Id(lbrCode, accNo);
				D009122 d009122 = session.get(D009122.class, id);
				tranHistory.setTogstno(d009122.getGstInNo());

				tranHistory.setUploaddate(drOperationDate);
				tranHistory.setHsncode(" ");
				tranHistory.setIgstamt(0D);
				tranHistory.setIgst(" ");
				tranHistory.setIgstrate(0D);
				tranHistory.setInvoicedate(drOperationDate);
				tranHistory.setServicetax(0D);
				tranHistory.setServicetaxamt(0D);
				tranHistory.setMainscrollno(scrollNo);
				try {
					String stateCd = (String) session

							.createNativeQuery("SELECT B.StateCd FROM D001003 A, D500028 B WHERE A.CityCd="
									+ "B.PlaceCd AND A.CityCd=(SELECT CityCd FROM D009011 WHERE CustNo="
									+ sourceAccount.getCustNo() + ") AND A.PBrCode=" + lbrCode)
							.getSingleResult();
					tranHistory.setDeststate(stateCd);
					tranHistory.setSourcestate(stateCd);
				} catch (Exception ec) {
					tranHistory.setDeststate(" ");
					tranHistory.setSourcestate(" ");
				}

				tranHistory.setChgType(Integer.parseInt(ChargeType));
				logger.error(tranHistory.toString());
				session.save(tranHistory);

			}

			Query query = session.createSQLQuery(
					"UPDATE D010004 SET TotalDrVcrs=TotalDrVcrs+3, TotalDrAmtLcy = TotalDrAmtLcy +:amt WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd");
			query.setParameter("amt", osAmt);
			query.setParameter("lbrcode", lbrCode);
			query.setParameter("entrydate", drOperationDate);
			query.setParameter("batchcd", batch);
			query.executeUpdate();

			Query query1 = session.createSQLQuery(
					"UPDATE D010004 SET TotalCrVcrs=TotalCrVcrs+1, TotalCrAmtLcy = TotalCrAmtLcy +:amt WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd");
			query1.setParameter("amt", osAmt);
			query1.setParameter("lbrcode", lbrCode);
			query1.setParameter("entrydate", drOperationDate);
			query1.setParameter("batchcd", batch);
			query1.executeUpdate();

			/*
			 * Query query2 = session.createSQLQuery(
			 * "UPDATE D009044 SET Status = 1 WHERE LBrCode =" + lbrCode +
			 * " AND IssuedTo = '" + acctNo + "' AND InsType = '" + insType +
			 * "' AND InstruNo = '" + chequeNo + "'");
			 */
			// query2.setParameter("amt", osAmt);
			// query2.setParameter("lbrcode", lbrCode);
			// query2.setParameter("entrydate", drOperationDate);
			// query2.setParameter("batchcd", batch);
			// query2.executeUpdate();
			logger.error("Transaction successful");
			resultMap.put(Code.RESULT, Code.SUCCESS);
		}

		// return stopPaymentRes;

		return resultMap;
	}

	public static HashMap<String, String> serviceChargeVoucherEntry(int lbrCode, String rrn, Session session,
			String accNo, String narration, Double amount, Double trnAmt) {

		HashMap<String, String> resultMap = new HashMap<>();

		// double amount = 0.0;
		double serTaxRate = 0.0;
		double eduCharges = 0.0;
		double finalAmt = 0.0;
		double lastAmt = 0.0;
		double osAmt = 0.0;
		String batch = "";
		String plCrAccNo = "";
		String stat = "";
		String batchCode = "";
		double sgstCharge = 0;
		Double sgstRate = 0D;
		D010004 onlineBatch = null;
		try {
			Date drOperationDate = getOpenDateNew(lbrCode, session);

			D001004 READSCHGREC = DataUtils.getSystemParameter(lbrCode, "READSCHGREC");
			String ChargeType = "";
			int insType;
			
			if("NEFT".equals(narration.substring(0, 4))) {
				ChargeType = ConfigurationLoader.getParameters(false).getProperty("NEFT_CHARGE_TYPE").trim();
				insType = Integer
						.parseInt(ConfigurationLoader.getParameters(false).getProperty("NEFT_CHG_INST_TYPE").trim());
				
			}else {
				ChargeType = ConfigurationLoader.getParameters(false).getProperty("IMPS_CHARGE_TYPE").trim();
				insType = Integer
						.parseInt(ConfigurationLoader.getParameters(false).getProperty("IMPS_CHG_INST_TYPE").trim());

			}
			

			if (READSCHGREC.getValue().trim().equalsIgnoreCase("N")) {

				D130001 d130001 = DataUtils.getstopChequeCharges(lbrCode, ChargeType, insType + "");// chgType=2
				// for // stop // cheque logger.error("d130001::>>>" +
				if (d130001 == null) {
					logger.error(MSGConstants.SERVICE_CHARGES_NOT_FOUND + " in D130001.");
					logger.error("Transaction unsuccessful");
					resultMap.put(Code.RESULT, Code.ERROR);
					// stopPaymentRes.setErrorMessage(MSGConstants.SERVICE_CHARGES_NOT_FOUND);
					return resultMap;
				}

				// amount = d130001.getFlatRate();
				batch = d130001.getBatchCd();

				logger.error("drOperationDate::>>" + drOperationDate);
				logger.error("batch::>>" + batch);
				logger.error("lbrCode::>>" + lbrCode);
				onlineBatch = VoucherMPOS.getD010004(lbrCode, batch.trim(), drOperationDate);
				logger.error("onlineBatch::>>" + onlineBatch);
				logger.error("onlineBatch.getStat()::>>" + onlineBatch.getStat());
				stat = onlineBatch.getStat() + "";
				logger.error("Status::>>" + stat);

				plCrAccNo = d130001.getPlCrAcctId();
				logger.error("amount::>>" + amount);
				logger.error("batch::>>" + batch);
				logger.error("plCrAccNo::>>" + plCrAccNo);

				D130031 d130031 = DataUtils.getServiceCharges(ChargeType);
				logger.error("d130031::>>" + d130031);

				if (d130031 != null) {
					serTaxRate = d130031.getSerTaxRate() / 100;
					eduCharges = d130031.getEduCesRate() / 100;
					logger.error("serTaxRate::>>" + serTaxRate);
					logger.error("eduCharges::>>" + eduCharges);

					sgstCharge = amount * (d130031.getSerTaxRate() / 100);

					sgstCharge = Math.round(sgstCharge * 100.0) / 100.0;

				}

				finalAmt = sgstCharge;// SGSTAMount

				osAmt = (finalAmt + amount);

			} else {

				D130014 d130014 = DataUtils.getIMPSChargeType(lbrCode, Integer.parseInt(ChargeType));// chgType=2
				// for
				// stop
				// cheque

				if (d130014 == null) {

					logger.error(MSGConstants.SERVICE_CHARGES_NOT_FOUND + " in D130001.");
					logger.error("Transaction unsuccessful");
					resultMap.put(Code.RESULT, Code.ERROR);
					return resultMap;
				} else {
					logger.error("d130014::>>>" + d130014);
				}
				// amount = d130014.getFlatRate();

				batch = d130014.getBatchCd();
				logger.error("drOperationDate::>>" + drOperationDate);
				logger.error("batch::>>" + batch);
				logger.error("lbrCode::>>" + lbrCode);
				onlineBatch = VoucherMPOS.getD010004(lbrCode, batch.trim(), drOperationDate, session);
				logger.error("onlineBatch::>>" + onlineBatch);
				logger.error("onlineBatch.getStat()::>>" + onlineBatch.getStat());
				stat = onlineBatch.getStat() + "";
				logger.error("Status::>>" + stat);

				plCrAccNo = d130014.getPlCrAcctId();
				logger.error("amount::>>" + amount);
				logger.error("batch::>>" + batch);
				logger.error("plCrAccNo::>>" + plCrAccNo);

				/*
				 * serTaxRate = d130031.getSerTaxRate() / 100; eduCharges =
				 * d130031.getEduCesRate() / 100; logger.error("serTaxRate::>>" + serTaxRate);
				 * logger.error("eduCharges::>>" + eduCharges);
				 */

				D130031 d130031 = DataUtils.getServiceCharges(ChargeType);
				logger.error("d130031::>>" + d130031);

				if (d130031 != null) {
					serTaxRate = d130031.getSerTaxRate() / 100;
					eduCharges = d130031.getEduCesRate() / 100;
					logger.error("serTaxRate::>>" + serTaxRate);
					logger.error("eduCharges::>>" + eduCharges);

					sgstCharge = amount * (d130031.getSerTaxRate() / 100);

					sgstCharge = Math.round(sgstCharge * 100.0) / 100.0;

				}

				finalAmt = sgstCharge;// SGSTAMount

				osAmt = (finalAmt + amount);

			}

			logger.error("Total outstanding Amount::>>" + osAmt);
			D009022 sourceAccount = DataUtils.getAccountMaster(lbrCode, accNo);// lbrCode,
			// acctNo
			logger.error("sourceAccount ::>>" + sourceAccount);
			if (sourceAccount == null) {
				logger.error("Transaction unsuccessful");
				resultMap.put(Code.RESULT, Code.ERROR);
				return resultMap;
			}
			TransactionValidationResponse response = TransactionServiceImpl.validateAccount(sourceAccount,
					"" + (osAmt + trnAmt), "D");
			boolean voucherFlag = true;
			logger.error("response.getResponse():>>" + response.getResponse());
			logger.error("response.getErrorMsg()::>>>" + response.getErrorMsg());

			if ((stat.equalsIgnoreCase("1") || stat.equalsIgnoreCase("2"))
					&& (response.getResponse().equalsIgnoreCase(MSGConstants.SUCCESS))) {
				logger.error("Batch is open");

				String bal = CoreTransactionMPOS.balance(lbrCode, accNo, osAmt + trnAmt, "D", session);
				logger.error("bal::>>" + bal);
			} else {

				logger.error("Batch is not open OR Account is not valid");
				logger.error("Online Batch not Open charges is placed in recovery master. OR Account is not valid");
				logger.error("lbrCode::>>" + lbrCode);
				logger.error("acctNo::>>>" + accNo);
				D130008Id id = new D130008Id();
				id.setChgType(Byte.valueOf(ChargeType));
				id.setLbrCode(lbrCode);
				id.setPrdAcctId(accNo);
				D130008 d130008Obj = session.get(D130008.class, id);
				logger.error("d130008Obj::>>" + d130008Obj);
				if (d130008Obj == null) {
					logger.error("d130008Obj is " + d130008Obj);
					D130008 d130008 = TransactionServiceImpl.prepareRecoveryChgmaster(drOperationDate, osAmt,
							ChargeType, lbrCode, accNo);
					session.save(d130008);
					// session.flush();
				} else {
					logger.error("d130008Obj is not null");
					double amt = d130008Obj.getOsAmt() + osAmt;
					d130008Obj.setOsAmt(amt);
					session.update(d130008Obj);
					// session.flush();
				}

				String bal = CoreTransactionMPOS.balance(lbrCode, accNo, trnAmt, "D", session);
				logger.error("bal::>>" + bal);

				VoucherMPOS.updateProductBalances(trnAmt, lbrCode, "D", accNo.substring(0, 8).trim(), session);

				/*
				 * D010010 d010010 = prepareStopChequeEntry(lbrCode, accNo,
				 * String.valueOf(insType), chequeNo, drAccount.getLongName(), "Stop Cheque/" +
				 * chequeNo, drOperationDate); logger.error("stopped"); session.save(d010010);
				 */

				/*
				 * Query query = session.createSQLQuery(
				 * "UPDATE D009044 SET Status = 1 WHERE LBrCode =" + lbrCode +
				 * " AND IssuedTo = '" + acctNo + "' AND InsType = '" + insType +
				 * "' AND InstruNo = '" + chequeNo + "'"); // query2.setParameter("amt", osAmt);
				 * // query2.setParameter("lbrcode", lbrCode); //
				 * query2.setParameter("entrydate", drOperationDate); //
				 * query2.setParameter("batchcd", batch); query.executeUpdate(); t.commit();
				 * session.close(); session = null; t = null;
				 */
				/*
				 * stopPaymentRes.setResponse(MSGConstants.SUCCESS);
				 * stopPaymentRes.setErrorMessage(
				 * "Dear customer,as per your request cheque number " + chequeNo +
				 * " is stopped successfully.");//
				 * stopPaymentRes.setTransactionId(DataUtils.getNextRRN());
				 */
				logger.error("Transaction successful");
				resultMap.put(Code.RESULT, Code.SUCCESS);
				return resultMap;
			}

			// logger.error("finalAmt::>>"+finalAmt);
			// logger.error("lastAmt::>>>"+lastAmt);

			/*
			 * String staxAccNo = d130031.getSerTaxAcctId(); String sbcAccNo =
			 * d130031.getEduCesAcctId();
			 * 
			 * logger.error("staxAccNo::>>" + staxAccNo); logger.error("sbcAccNo::>>>" +
			 * sbcAccNo);
			 */

			String sgstCreditAcctId = null;
			String cgstCreditPrdCd = null;
			List<D130031> gstChargesMasters = DataUtils.getLatestServiceCharge(ChargeType, drOperationDate);
			if (!gstChargesMasters.isEmpty()) {
				D130031 gstChargesMaster = gstChargesMasters.get(0);
				sgstCreditAcctId = gstChargesMaster.getSerTaxAcctId();
				// String cgstCreditPrdCd = cgstCreditAcctId.substring(0, 8).trim();
			}

			logger.error("sourceAccount::>>" + sourceAccount);

			int scrollNo = VoucherMPOS.getNextScrollNo();
			int setNo = VoucherMPOS.getNextSetNo();
			logger.error("setNo::>" + setNo);
			logger.error("scrollNo::>>" + scrollNo);
			// String rrn = DataUtils.getNextRRN();

			D009040 d009040Dr = TransactionServiceImpl.prepareD009040Object(batch, drOperationDate, lbrCode, setNo,
					scrollNo, scrollNo, accNo, session, osAmt, narration, "DR", rrn);
			d009040Dr.setActivityType("CHRGDR");

			VoucherMPOS.updateProductBalances(osAmt + trnAmt, lbrCode, "D", accNo.substring(0, 8).trim(), session);

			D009040 d009040CrSGST = TransactionServiceImpl.prepareD009040Object(batch, drOperationDate, lbrCode, setNo,
					VoucherMPOS.getNextScrollNo(), scrollNo, sgstCreditAcctId, session, finalAmt,
					"SGST for " + narration, "CR", rrn);
			d009040CrSGST.setMainAcctId(accNo);
			d009040CrSGST.setCashFlowType("CGSTCHCR");
			D009021 prdSGST = DataUtils.getProductMaster(String.valueOf(lbrCode),
					sgstCreditAcctId.substring(0, 8).trim());
			if ('P' != prdSGST.getAcctOpenLevel()) {
				String plSGST = CoreTransactionMPOS.balance(lbrCode, sgstCreditAcctId, finalAmt, "C", session);
				logger.error("bal::>>" + plSGST);
			}
			VoucherMPOS.updateProductBalances(finalAmt, lbrCode, "C", sgstCreditAcctId.substring(0, 8).trim(), session);

			D009040 d009040PLACC = TransactionServiceImpl.prepareD009040Object(batch, drOperationDate, lbrCode, setNo,
					VoucherMPOS.getNextScrollNo(), scrollNo, plCrAccNo, session, amount, narration, "CR", rrn);
			String plAcct = CoreTransactionMPOS.balance(lbrCode, plCrAccNo, amount, "C", session);
			logger.error("bal::>>" + plAcct);
			d009040PLACC.setMainAcctId(accNo);
			VoucherMPOS.updateProductBalances(amount, lbrCode, "C", plCrAccNo.substring(0, 8).trim(), session);

			d009040PLACC.setActivityType("CHRGCR");
			/*
			 * D010010 d010010 = prepareStopChequeEntry(lbrCode, acctNo,
			 * String.valueOf(insType), chequeNo, drAccount.getLongName(),
			 * "Stop Cheque Request", drOperationDate); logger.error("d010010::>>>" +
			 * d010010); logger.error("voucherFlag::>>" + voucherFlag);
			 * session.save(d010010);
			 */
			if (voucherFlag) {
				logger.error("d009040PLACC::>>" + d009040PLACC);
				logger.error("d009040Dr::>>" + d009040Dr);

				logger.error("d009040CrSTAX::>>" + d009040CrSGST);
				// logger.error("d009040CrSBC::>>" + d009040CrCGST);

				session.save(d009040PLACC);
				session.save(d009040Dr);
				if (finalAmt > 0)
					session.save(d009040CrSGST);

				/*
				 * if (finalAmt > 0 || lastAmt > 0) { GstTransactionHistory tranHistory = new
				 * GstTransactionHistory(); tranHistory.setId(new GstTransactionHistoryId());
				 * tranHistory.getId().setSetno(setNo); tranHistory.getId().setBatchcd(batch);
				 * tranHistory.getId().setEntrydate(drOperationDate);
				 * tranHistory.getId().setLbrcode(lbrCode);
				 * tranHistory.getId().setScrollno(scrollNo);
				 * tranHistory.getId().setUniquerefno((lbrCode +
				 * DateUtil.getcurrentDateForPDF()).substring(0, 18));
				 * tranHistory.setActivitytype("DR");
				 * tranHistory.setBooktype(d009040Dr.getBookType());
				 * tranHistory.setCashflowtype("CGSTCHCR"); tranHistory.setDrcr("C");
				 * tranHistory.setCurrdate(new Date());
				 * tranHistory.setCustno(sourceAccount.getCustNo());
				 * tranHistory.setFcytrnamt(sgstCharge);
				 * tranHistory.setParticulars("CGST Charges");
				 * tranHistory.setSgstrate(sgstRate); tranHistory.setSgstamt(sgstCharge);
				 * tranHistory.setCgstrate(cgstRate); tranHistory.setCgstamt(cgstCharge);
				 * tranHistory.setCessamt(0D); tranHistory.setCessrate(0D);
				 * tranHistory.setCanceledflag(" "); tranHistory.setCgst(" ");
				 * tranHistory.setSgst(" "); tranHistory.setIgst(" ");
				 * tranHistory.setGstno(" "); tranHistory.setVcracctid(cgstCreditAcctId);
				 * tranHistory.setMainacctid(accNo);
				 * tranHistory.setFromgstno(DataUtils.getSystemParameter(lbrCode,
				 * "GSTNINNO").getValue());
				 * tranHistory.setSaccode(DataUtils.getSystemParameter(0,
				 * "SACCODE").getValue());
				 * 
				 * D009122Id id = new D009122Id(lbrCode, accNo); D009122 d009122 =
				 * session.get(D009122.class, id); tranHistory.setTogstno(d009122.getGstInNo());
				 * 
				 * tranHistory.setUploaddate(drOperationDate); tranHistory.setHsncode(" ");
				 * tranHistory.setIgstamt(0D); tranHistory.setIgst(" ");
				 * tranHistory.setIgstrate(0D); tranHistory.setInvoicedate(drOperationDate);
				 * tranHistory.setServicetax(0D); tranHistory.setServicetaxamt(0D);
				 * tranHistory.setMainscrollno(scrollNo); try{ String stateCd = (String) session
				 * 
				 * .createNativeQuery("SELECT B.StateCd FROM D001003 A, D500028 B WHERE A.CityCd="
				 * + "B.PlaceCd AND A.CityCd=(SELECT CityCd FROM D009011 WHERE CustNo=" +
				 * sourceAccount.getCustNo() + ") AND A.PBrCode=" + lbrCode) .getSingleResult();
				 * tranHistory.setDeststate(stateCd); tranHistory.setSourcestate(stateCd);
				 * }catch(Exception ec) { tranHistory.setDeststate(" ");
				 * tranHistory.setSourcestate(" "); }
				 * 
				 * tranHistory.setChgType(Integer.parseInt(ChargeType));
				 * logger.error(tranHistory.toString()); session.save(tranHistory);
				 * 
				 * }
				 */

				Query query = session.createSQLQuery(
						"UPDATE D010004 SET TotalDrVcrs=TotalDrVcrs+1, TotalDrAmtLcy = TotalDrAmtLcy +:amt WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd");
				query.setParameter("amt", osAmt);
				query.setParameter("lbrcode", lbrCode);
				query.setParameter("entrydate", drOperationDate);
				query.setParameter("batchcd", batch);
				query.executeUpdate();

				Query query1 = session.createSQLQuery(
						"UPDATE D010004 SET TotalCrVcrs=TotalCrVcrs+2, TotalCrAmtLcy = TotalCrAmtLcy +:amt WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd");
				query1.setParameter("amt", osAmt);
				query1.setParameter("lbrcode", lbrCode);
				query1.setParameter("entrydate", drOperationDate);
				query1.setParameter("batchcd", batch);
				query1.executeUpdate();

				/*
				 * Query query2 = session.createSQLQuery(
				 * "UPDATE D009044 SET Status = 1 WHERE LBrCode =" + lbrCode +
				 * " AND IssuedTo = '" + acctNo + "' AND InsType = '" + insType +
				 * "' AND InstruNo = '" + chequeNo + "'");
				 */
				// query2.setParameter("amt", osAmt);
				// query2.setParameter("lbrcode", lbrCode);
				// query2.setParameter("entrydate", drOperationDate);
				// query2.setParameter("batchcd", batch);
				// query2.executeUpdate();
				logger.error("Transaction successful");
				resultMap.put(Code.RESULT, Code.SUCCESS);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
		}
		// return stopPaymentRes;

		return resultMap;
	}

	public static HashMap<String, String> serviceChgRevVoucherEntry(int lbrCode, String rrn, Session session,
			String accNo, String narration, Double amount) {

		HashMap<String, String> resultMap = new HashMap<>();

		// double amount = 0.0;
		double serTaxRate = 0.0;
		double eduCharges = 0.0;
		double finalAmt = 0.0;
		double lastAmt = 0.0;
		double osAmt = 0.0;
		String batch = "";
		String plCrAccNo = "";
		String stat = "";
		String batchCode = "";
		double sgstCharge = 0;
		Double sgstRate = 0D;
		D010004 onlineBatch = null;

		Date drOperationDate = getOpenDateNew(lbrCode, session);

		D001004 READSCHGREC = DataUtils.getSystemParameter(lbrCode, "READSCHGREC");
		String ChargeType = ConfigurationLoader.getParameters(false).getProperty("IMPS_CHARGE_TYPE").trim();
		int insType = Integer
				.parseInt(ConfigurationLoader.getParameters(false).getProperty("IMPS_CHG_INST_TYPE").trim());
		
		if("NEFT".equals(narration.substring(0, 4))) {
			ChargeType = ConfigurationLoader.getParameters(false).getProperty("NEFT_CHARGE_TYPE").trim();
			insType = Integer
					.parseInt(ConfigurationLoader.getParameters(false).getProperty("NEFT_CHG_INST_TYPE").trim());
			
		}

		if (READSCHGREC.getValue().trim().equalsIgnoreCase("N")) {

			D130001 d130001 = DataUtils.getstopChequeCharges(lbrCode, ChargeType, insType + "");// chgType=2
			// for // stop // cheque logger.error("d130001::>>>" +
			if (d130001 == null) {
				logger.error(MSGConstants.SERVICE_CHARGES_NOT_FOUND + " in D130001.");
				logger.error("Transaction unsuccessful");
				resultMap.put(Code.RESULT, Code.ERROR);
				// stopPaymentRes.setErrorMessage(MSGConstants.SERVICE_CHARGES_NOT_FOUND);
				return resultMap;
			}

			// amount = d130001.getFlatRate();
			batch = d130001.getBatchCd();

			logger.error("drOperationDate::>>" + drOperationDate);
			logger.error("batch::>>" + batch);
			logger.error("lbrCode::>>" + lbrCode);
			onlineBatch = VoucherMPOS.getD010004(lbrCode, batch.trim(), drOperationDate);
			logger.error("onlineBatch::>>" + onlineBatch);
			logger.error("onlineBatch.getStat()::>>" + onlineBatch.getStat());
			stat = onlineBatch.getStat() + "";
			logger.error("Status::>>" + stat);

			plCrAccNo = d130001.getPlCrAcctId();
			logger.error("amount::>>" + amount);
			logger.error("batch::>>" + batch);
			logger.error("plCrAccNo::>>" + plCrAccNo);

			D130031 d130031 = DataUtils.getServiceCharges(ChargeType);

			if (d130031 != null) {
				logger.error("d130031::>>" + d130031);
				serTaxRate = d130031.getSerTaxRate() / 100;
				eduCharges = d130031.getEduCesRate() / 100;
				logger.error("serTaxRate::>>" + serTaxRate);
				logger.error("eduCharges::>>" + eduCharges);
				sgstCharge = amount * (serTaxRate);
				sgstCharge = Math.round(sgstCharge * 100.0) / 100.0;

			}

			finalAmt = sgstCharge;// SGSTAMount
			osAmt = (finalAmt + amount);

		} else {

			D130014 d130014 = DataUtils.getIMPSChargeType(lbrCode, Integer.parseInt(ChargeType));// chgType=2
			// for
			// stop
			// cheque

			if (d130014 == null) {

				logger.error(MSGConstants.SERVICE_CHARGES_NOT_FOUND + " in D130001.");
				logger.error("Transaction unsuccessful");
				resultMap.put(Code.RESULT, Code.ERROR);
				return resultMap;
			} else {
				logger.error("d130014::>>>" + d130014);
			}
			// amount = d130014.getFlatRate();

			batch = d130014.getBatchCd();
			logger.error("drOperationDate::>>" + drOperationDate);
			logger.error("batch::>>" + batch);
			logger.error("lbrCode::>>" + lbrCode);
			onlineBatch = VoucherMPOS.getD010004(lbrCode, batch.trim(), drOperationDate);
			logger.error("onlineBatch::>>" + onlineBatch);
			logger.error("onlineBatch.getStat()::>>" + onlineBatch.getStat());
			stat = onlineBatch.getStat() + "";
			logger.error("Status::>>" + stat);

			plCrAccNo = d130014.getPlCrAcctId();
			logger.error("amount::>>" + amount);
			logger.error("batch::>>" + batch);
			logger.error("plCrAccNo::>>" + plCrAccNo);

			D130031 d130031 = DataUtils.getServiceCharges(ChargeType);

			if (d130031 != null) {
				logger.error("d130031::>>" + d130031);

				serTaxRate = d130031.getSerTaxRate() / 100;
				eduCharges = d130031.getEduCesRate() / 100;
				logger.error("serTaxRate::>>" + serTaxRate);
				logger.error("eduCharges::>>" + eduCharges);
				sgstCharge = amount * (serTaxRate);
				sgstCharge = Math.round(sgstCharge * 100.0) / 100.0;
			}

			finalAmt = sgstCharge;// SGSTAMount
			osAmt = (finalAmt + amount);

		}

		logger.error("Total outstanding Amount::>>" + osAmt);
		D009022 sourceAccount = DataUtils.getAccountMaster(lbrCode, accNo);// lbrCode,
		// acctNo
		logger.error("sourceAccount ::>>" + sourceAccount);
		if (sourceAccount == null) {
			logger.error("Transaction unsuccessful");
			resultMap.put(Code.RESULT, Code.ERROR);
			return resultMap;
		}
		// TransactionValidationResponse response = validateAccount(sourceAccount, "" +
		// osAmt, "D", session);
		boolean voucherFlag = true;
		// logger.error("response.getResponse():>>" + response.getResponse());
		// logger.error("response.getErrorMsg()::>>>" + response.getErrorMsg());

		if ((stat.equalsIgnoreCase("1") || stat.equalsIgnoreCase("2"))) {
			logger.error("Batch is open");

			String bal = CoreTransactionMPOS.balance(lbrCode, accNo, osAmt, "C", session);
			logger.error("bal::>>" + bal);
		} else {

			logger.error("Batch is not open OR Account is not valid");
			logger.error("Online Batch not Open charges is placed in recovery master. OR Account is not valid");
			logger.error("lbrCode::>>" + lbrCode);
			logger.error("acctNo::>>>" + accNo);
			D130008Id id = new D130008Id();
			id.setChgType(Byte.valueOf(ChargeType));
			id.setLbrCode(lbrCode);
			id.setPrdAcctId(accNo);
			D130008 d130008Obj = session.get(D130008.class, id);
			logger.error("d130008Obj::>>" + d130008Obj);
			if (d130008Obj == null) {
				logger.error("d130008Obj is " + d130008Obj);
				D130008 d130008 = TransactionServiceImpl.prepareRecoveryChgmaster(drOperationDate, osAmt, ChargeType,
						lbrCode, accNo);
				session.save(d130008);
				// session.flush();
			} else {
				logger.error("d130008Obj is not null");
				double amt = d130008Obj.getOsAmt() + osAmt;
				d130008Obj.setOsAmt(amt);
				session.update(d130008Obj);
				// session.flush();
			}
			/*
			 * D010010 d010010 = prepareStopChequeEntry(lbrCode, accNo,
			 * String.valueOf(insType), chequeNo, drAccount.getLongName(), "Stop Cheque/" +
			 * chequeNo, drOperationDate); logger.error("stopped"); session.save(d010010);
			 */

			/*
			 * Query query = session.createSQLQuery(
			 * "UPDATE D009044 SET Status = 1 WHERE LBrCode =" + lbrCode +
			 * " AND IssuedTo = '" + acctNo + "' AND InsType = '" + insType +
			 * "' AND InstruNo = '" + chequeNo + "'"); // query2.setParameter("amt", osAmt);
			 * // query2.setParameter("lbrcode", lbrCode); //
			 * query2.setParameter("entrydate", drOperationDate); //
			 * query2.setParameter("batchcd", batch); query.executeUpdate(); t.commit();
			 * session.close(); session = null; t = null;
			 */
			/*
			 * stopPaymentRes.setResponse(MSGConstants.SUCCESS);
			 * stopPaymentRes.setErrorMessage(
			 * "Dear customer,as per your request cheque number " + chequeNo +
			 * " is stopped successfully.");//
			 * stopPaymentRes.setTransactionId(DataUtils.getNextRRN());
			 */
			logger.error("Transaction successful");
			resultMap.put(Code.RESULT, Code.SUCCESS);
			return resultMap;
		}

		// logger.error("finalAmt::>>"+finalAmt);
		// logger.error("lastAmt::>>>"+lastAmt);

		/*
		 * String staxAccNo = d130031.getSerTaxAcctId(); String sbcAccNo =
		 * d130031.getEduCesAcctId();
		 * 
		 * logger.error("staxAccNo::>>" + staxAccNo); logger.error("sbcAccNo::>>>" +
		 * sbcAccNo);
		 */

		String sgstCreditAcctId = null;
		List<D130031> gstChargesMasters = DataUtils.getLatestServiceCharge(ChargeType, drOperationDate);
		if (!gstChargesMasters.isEmpty()) {
			D130031 gstChargesMaster = gstChargesMasters.get(0);
			sgstCreditAcctId = gstChargesMaster.getSerTaxAcctId();

			// String cgstCreditPrdCd = cgstCreditAcctId.substring(0, 8).trim();
		}

		logger.error("sourceAccount::>>" + sourceAccount);

		int scrollNo = VoucherMPOS.getNextScrollNo();
		int setNo = VoucherMPOS.getNextSetNo();
		logger.error("setNo::>" + setNo);
		logger.error("scrollNo::>>" + scrollNo);
		// String rrn = DataUtils.getNextRRN();

		D009040 d009040Dr = TransactionServiceImpl.prepareD009040Object(batch, drOperationDate, lbrCode, setNo,
				scrollNo, scrollNo, accNo, session, osAmt, narration, "CR", rrn);
		d009040Dr.setActivityType("CHRGCR");

		VoucherMPOS.updateProductBalances(osAmt, lbrCode, "C", accNo.substring(0, 8).trim(), session);

		D009040 d009040CrSGST = TransactionServiceImpl.prepareD009040Object(batch, drOperationDate, lbrCode, setNo,
				VoucherMPOS.getNextScrollNo(), scrollNo, sgstCreditAcctId, session, finalAmt, "SGST for " + narration,
				"DR", rrn);
		d009040CrSGST.setMainAcctId(accNo);
		d009040CrSGST.setCashFlowType("CGSTCHDR");
		D009021 prdSGST = DataUtils.getProductMaster(String.valueOf(lbrCode), sgstCreditAcctId.substring(0, 8).trim());
		if ('P' != prdSGST.getAcctOpenLevel()) {
			String plSGST = CoreTransactionMPOS.balance(lbrCode, sgstCreditAcctId, finalAmt, "D", session);
			logger.error("bal::>>" + plSGST);
		}
		VoucherMPOS.updateProductBalances(finalAmt, lbrCode, "D", sgstCreditAcctId.substring(0, 8).trim(), session);

		// d009040CrSGST.setActivityType("CHRGCR");

		D009040 d009040PLACC = TransactionServiceImpl.prepareD009040Object(batch, drOperationDate, lbrCode, setNo,
				VoucherMPOS.getNextScrollNo(), scrollNo, plCrAccNo, session, amount, narration, "DR", rrn);
		String plAcct = CoreTransactionMPOS.balance(lbrCode, plCrAccNo, amount, "D", session);
		logger.error("bal::>>" + plAcct);
		d009040PLACC.setMainAcctId(accNo);
		VoucherMPOS.updateProductBalances(amount, lbrCode, "D", plCrAccNo.substring(0, 8).trim(), session);

		d009040PLACC.setActivityType("CHRGDR");
		/*
		 * D010010 d010010 = prepareStopChequeEntry(lbrCode, acctNo,
		 * String.valueOf(insType), chequeNo, drAccount.getLongName(),
		 * "Stop Cheque Request", drOperationDate); logger.error("d010010::>>>" +
		 * d010010); logger.error("voucherFlag::>>" + voucherFlag);
		 * session.save(d010010);
		 */
		if (voucherFlag) {
			logger.error("d009040PLACC::>>" + d009040PLACC);
			logger.error("d009040Dr::>>" + d009040Dr);

			logger.error("d009040CrSTAX::>>" + d009040CrSGST);
			session.save(d009040PLACC);
			session.save(d009040Dr);
			if (finalAmt > 0)
				session.save(d009040CrSGST);

			/*
			 * if (finalAmt > 0 || lastAmt > 0) { GstTransactionHistory tranHistory = new
			 * GstTransactionHistory(); tranHistory.setId(new GstTransactionHistoryId());
			 * tranHistory.getId().setSetno(setNo); tranHistory.getId().setBatchcd(batch);
			 * tranHistory.getId().setEntrydate(drOperationDate);
			 * tranHistory.getId().setLbrcode(lbrCode);
			 * tranHistory.getId().setScrollno(scrollNo);
			 * tranHistory.getId().setUniquerefno((lbrCode +
			 * DateUtil.getcurrentDateForPDF()).substring(0, 18));
			 * tranHistory.setActivitytype("CR");
			 * tranHistory.setBooktype(d009040Dr.getBookType());
			 * tranHistory.setCashflowtype("CGSTCHDR"); tranHistory.setDrcr("D");
			 * tranHistory.setCurrdate(new Date());
			 * tranHistory.setCustno(sourceAccount.getCustNo());
			 * tranHistory.setFcytrnamt(sgstCharge + cgstCharge);
			 * tranHistory.setParticulars("CGST Charges");
			 * tranHistory.setSgstrate(sgstRate); tranHistory.setSgstamt(sgstCharge);
			 * tranHistory.setCgstrate(cgstRate); tranHistory.setCgstamt(cgstCharge);
			 * tranHistory.setCessamt(0D); tranHistory.setCessrate(0D);
			 * tranHistory.setCanceledflag(" "); tranHistory.setCgst(" ");
			 * tranHistory.setSgst(" "); tranHistory.setIgst(" ");
			 * tranHistory.setGstno(" "); tranHistory.setVcracctid(cgstCreditAcctId);
			 * tranHistory.setMainacctid(accNo);
			 * tranHistory.setFromgstno(DataUtils.getSystemParameter(lbrCode,
			 * "GSTNINNO").getValue());
			 * tranHistory.setSaccode(DataUtils.getSystemParameter(0,
			 * "SACCODE").getValue());
			 * 
			 * D009122Id id = new D009122Id(lbrCode, accNo); D009122 d009122 =
			 * session.get(D009122.class, id); tranHistory.setTogstno(d009122.getGstInNo());
			 * 
			 * tranHistory.setUploaddate(drOperationDate); tranHistory.setHsncode(" ");
			 * tranHistory.setIgstamt(0D); tranHistory.setIgst(" ");
			 * tranHistory.setIgstrate(0D); tranHistory.setInvoicedate(drOperationDate);
			 * tranHistory.setServicetax(0D); tranHistory.setServicetaxamt(0D);
			 * tranHistory.setMainscrollno(scrollNo); try{ String stateCd = (String) session
			 * 
			 * .createNativeQuery("SELECT B.StateCd FROM D001003 A, D500028 B WHERE A.CityCd="
			 * + "B.PlaceCd AND A.CityCd=(SELECT CityCd FROM D009011 WHERE CustNo=" +
			 * sourceAccount.getCustNo() + ") AND A.PBrCode=" + lbrCode) .getSingleResult();
			 * tranHistory.setDeststate(stateCd); tranHistory.setSourcestate(stateCd);
			 * }catch(Exception ec) { tranHistory.setDeststate(" ");
			 * tranHistory.setSourcestate(" "); }
			 * 
			 * tranHistory.setChgType(Integer.parseInt(ChargeType));
			 * logger.error(tranHistory.toString()); session.save(tranHistory);
			 * 
			 * }
			 */

			Query query = session.createSQLQuery(
					"UPDATE D010004 SET TotalDrVcrs=TotalDrVcrs+2, TotalDrAmtLcy = TotalDrAmtLcy +:amt WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd");
			query.setParameter("amt", osAmt);
			query.setParameter("lbrcode", lbrCode);
			query.setParameter("entrydate", drOperationDate);
			query.setParameter("batchcd", batch);
			query.executeUpdate();

			Query query1 = session.createSQLQuery(
					"UPDATE D010004 SET TotalCrVcrs=TotalCrVcrs+1, TotalCrAmtLcy = TotalCrAmtLcy +:amt WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd");
			query1.setParameter("amt", osAmt);
			query1.setParameter("lbrcode", lbrCode);
			query1.setParameter("entrydate", drOperationDate);
			query1.setParameter("batchcd", batch);
			query1.executeUpdate();

			/*
			 * Query query2 = session.createSQLQuery(
			 * "UPDATE D009044 SET Status = 1 WHERE LBrCode =" + lbrCode +
			 * " AND IssuedTo = '" + acctNo + "' AND InsType = '" + insType +
			 * "' AND InstruNo = '" + chequeNo + "'");
			 */
			// query2.setParameter("amt", osAmt);
			// query2.setParameter("lbrcode", lbrCode);
			// query2.setParameter("entrydate", drOperationDate);
			// query2.setParameter("batchcd", batch);
			// query2.executeUpdate();
			logger.error("Transaction successful");
			resultMap.put(Code.RESULT, Code.SUCCESS);
		}

		// return stopPaymentRes;

		return resultMap;
	}

	public static HashMap<String, String> qrCodeUPITransactions(int lbrCode, String accNo, Double amount, String rrn,
			Session session, Transaction t, String narrationCr, int drBrcode, String drAccno, String narrationDr,
			String transType, String glType) {

		VoucherCommon common = new VoucherCommon();
		HashMap<String, String> resultMap = new HashMap<>();
		// Session session = HBUtil.getSessionFactory().openSession();
		// Transaction t = session.beginTransaction();
		int setNo = VoucherCommon.getNextSetNo();
		int scrollNo = VoucherCommon.getNextScrollNo();
		int reconNo = getNextReconNo(drBrcode);
		if (lbrCode != drBrcode) {                                                                                                          
			common.credit(lbrCode, accNo, "QR~"+MSGConstants.ABB, setNo, scrollNo, narrationCr, amount, rrn, session);
			if (!common.isAborted) {
				if (VoucherMPOS.updateProductBalances(amount, lbrCode, "C", accNo.substring(0, 8).trim(), session)
						.trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
					String bal = CoreTransactionMPOS.balance(lbrCode, accNo, amount, "C", session);
					if (bal != null && bal.trim().length() > 0 && !bal.equalsIgnoreCase("51")
							&& !bal.equalsIgnoreCase("99")) {
						if (!common.isAborted) {
							int setNoABB = VoucherCommon.getNextSetNo();
							int scrollNoAbb = VoucherCommon.getNextScrollNo();
							int scrollNoAbb1 = VoucherCommon.getNextScrollNo();
							int scrollNoAbb2 = VoucherCommon.getNextScrollNo();
							common.debitABB(lbrCode, MSGConstants.ABB_ACC, MSGConstants.ABB, setNo, scrollNoAbb,
									narrationCr, drBrcode, amount, rrn, reconNo, session);
							if (!common.isAborted) {
								if (VoucherMPOS
										.updateProductBalances(amount, lbrCode, "D",
												MSGConstants.ABB_ACC.substring(0, 8).trim(), session)
										.trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
									if (!common.isAborted) {
										logger.error("ABB Transaction successful");
										// String
										// drAccno=getSysParameter("MBBILLPAY").trim()+"
										// 000000000000000000000000";
										common.debit(drBrcode, drAccno, transType.trim().toUpperCase(), setNoABB,
												scrollNoAbb1, narrationDr, amount, rrn, session);
										if (!common.isAborted) {
											if (VoucherMPOS
													.updateProductBalances(amount, drBrcode, "D",
															drAccno.substring(0, 8).trim(), session)
													.trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
												String bal2 = CoreTransactionMPOS.balance(drBrcode, drAccno, amount, "D", session);
												if (bal2 != null && bal2.trim().length() > 0 && !bal2.equalsIgnoreCase("51")
														&& !bal2.equalsIgnoreCase("99")) {
													
												
												if (!common.isAborted) {
													logger.error("Other Bank GL Transaction successful");
													common.creditABB(drBrcode, MSGConstants.ABB_ACC,
															transType.trim().toUpperCase(), setNoABB, scrollNoAbb2,
															narrationDr, lbrCode, amount, rrn, reconNo, session);
													VoucherMPOS.updateProductBalances(amount, drBrcode, "C",
															MSGConstants.ABB_ACC.substring(0, 8).trim(), session);

													Date opdate = DataUtils.getOpenDate(lbrCode);

													String batchCodes[] = Props.getBatchProperty(transType).split("~");
													D001004 d04OnlineBatchName = VoucherMPOS
															.getBatchNameFromBatchCode(batchCodes[0]);
													String onlineBatchName = d04OnlineBatchName.getValue().trim();
													String batchCodes1[] = Props.getBatchProperty("ABB").split("~");
													D001004 d001004 = VoucherMPOS
															.getBatchNameFromBatchCode(batchCodes1[0]);
													String benBatchCode = d001004.getValue().trim();

													D100001 d100001 = VoucherMPOS.prepareReconObj(drBrcode, reconNo,
															opdate, 999999, onlineBatchName, benBatchCode, setNoABB,
															scrollNoAbb2, setNo, scrollNo, scrollNoAbb, lbrCode,
															"" + accNo, Double.valueOf(amount), "D");
													System.out.println("d100001::>>>" + d100001);
													D100002 d100002 = VoucherMPOS.prepareRecon2Obj(drBrcode, reconNo,
															opdate, 999999, onlineBatchName, benBatchCode, setNoABB,
															scrollNoAbb2, setNo, scrollNo, scrollNoAbb, lbrCode,
															"" + accNo, Double.valueOf(amount), "D");
													System.out.println("d100002::>>>" + d100002);
													session.save(d100001);
													session.save(d100002);
													//t.commit();
													resultMap.put(Code.RESULT, Code.SUCCESS);
													resultMap.put(Code.SETNO, String.valueOf(setNo));
													resultMap.put(Code.SCROLLNO, String.valueOf(scrollNo));
													resultMap.put("Batch", onlineBatchName);
													return resultMap;
												} else {
													logger.error("ABB Transaction unsuccessful");
													if (t.isActive())
														t.rollback();
													resultMap.put(Code.RESULT, Code.ERROR);
													resultMap.put(Code.SETNO, "");
													resultMap.put(Code.SCROLLNO, "");
													common = null;
													return resultMap;
												}
											} else {
												logger.error("ABB Transaction unsuccessful");
												if (t.isActive())
													t.rollback();
												resultMap.put(Code.RESULT, Code.ERROR);
												resultMap.put(Code.SETNO, "");
												resultMap.put(Code.SCROLLNO, "");
												common = null;
												return resultMap;
											}
											} else {
												if (t.isActive())
													t.rollback();
												logger.error("ABB Transaction unsuccessful");
												resultMap.put(Code.RESULT, Code.ERROR);
												resultMap.put(Code.SETNO, "");
												resultMap.put(Code.SCROLLNO, "");
												common = null;
												return resultMap;
											}
										} else {
											if (t.isActive())
												t.rollback();
											logger.error("ABB Transaction unsuccessful");
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "");
											resultMap.put(Code.SCROLLNO, "");
											common = null;
											return resultMap;
										}
									} else {
										if (t.isActive())
											t.rollback();
										logger.error("ABB Transaction unsuccessful");
										resultMap.put(Code.RESULT, Code.ERROR);
										resultMap.put(Code.SETNO, "");
										resultMap.put(Code.SCROLLNO, "");
										common = null;
										return resultMap;
									}
								} else {
									if (t.isActive())
										t.rollback();
									logger.error("ABB Transaction unsuccessful");
									resultMap.put(Code.RESULT, Code.ERROR);
									resultMap.put(Code.SETNO, "");
									resultMap.put(Code.SCROLLNO, "");
									common = null;
									return resultMap;
								}
							} else {
								if (t.isActive())
									t.rollback();
								logger.error("ABB Transaction unsuccessful");
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "");
								resultMap.put(Code.SCROLLNO, "");
								common = null;
								return resultMap;
							}
						} else {
							if (t.isActive())
								t.rollback();
							logger.error("Transaction unsuccessful");
							resultMap.put(Code.RESULT, Code.ERROR);
							resultMap.put(Code.SETNO, "");
							resultMap.put(Code.SCROLLNO, "");
							common = null;
							return resultMap;
						}
					} else {
						if (t.isActive())
							t.rollback();
						logger.error("Transaction unsuccessful");
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "");
						resultMap.put(Code.SCROLLNO, "");
						common = null;
						return resultMap;
					}
				} else {
					if (t.isActive())
						t.rollback();
					logger.error("Transaction unsuccessful");
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "");
					resultMap.put(Code.SCROLLNO, "");
					common = null;
					return resultMap;
				}

			} else {
				if (t.isActive())
					t.rollback();
				logger.error("Transaction unsuccessful");
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "");
				resultMap.put(Code.SCROLLNO, "");
				common = null;
				return resultMap;
			}
		}else {


			logger.error("Same Branch Fund Transfer....");
			
			common.debitSameBranch(drBrcode, drAccno, transType.toUpperCase(), setNo,
					scrollNo, narrationDr, amount, rrn, scrollNo, session);
			System.out.println("VoucherCommon.otherBranchVouchers() common.isAborted " + common.isAborted);
			if (!common.isAborted) {
				if (VoucherMPOS
						.updateProductBalances(amount, drBrcode, "D", drAccno.substring(0, 8).trim(), session)
						.equalsIgnoreCase(MSGConstants.SUCCESS)) {
					String balResponse = CoreTransactionMPOS.balance(drBrcode, drAccno.trim(), amount, "D",
							session);
					if (balResponse != null && balResponse.trim().length() > 0
							&& !balResponse.trim().equalsIgnoreCase("99")
							&& !balResponse.trim().equalsIgnoreCase("51")) {
						if (!common.isAborted) {
							/*** Added By Aniket Desai on 28 Aug, 2019 for Loan Changes:---Starts--- ***/
							logger.error("Transaction successful");
							logger.error("accNo::>>" + accNo);
							logger.error("lbrCode::>>" + lbrCode);
							D009022 creditAccount = session.get(D009022.class, new D009022Id(lbrCode, accNo));

							D009021 creditProductMaster = session.get(D009021.class,
									new D009021Id(lbrCode, accNo.substring(0, 8).trim()));
							

								common.creditSameBranch(lbrCode, accNo, "QR~"+transType, setNo,
										VoucherCommon.getNextScrollNo(), narrationCr, amount, rrn, scrollNo,
										session);
							
							/*
							 * common.creditSameBranch(lbrCode, benAccNo, transType, setNo,
							 * VoucherCommon.getNextScrollNo(), narration, amount, rrn, mainScrollNo,
							 * session);
							 */
							/*** Added By Aniket Desai on 28 Aug, 2019 for Loan Changes:---END--- ***/

							if (!common.isAborted) {

								/***
								 * Added By Aniket Desai on 3rd Sep, 2019 for Loan Voucher(Double CR To Loan
								 * Account):--Start--
								 ***/
								String bal = null;
								if (VoucherMPOS
										.updateProductBalances(amount, lbrCode, "C",
												accNo.substring(0, 8).trim(), session)
										.equalsIgnoreCase(MSGConstants.SUCCESS))
									bal = CoreTransactionMPOS.balance(lbrCode, accNo.trim(), amount, "C",
											session);

								/*
								 * if (VoucherMPOS.updateProductBalances(amount, lbrCode, "C",
								 * benAccNo.substring(0, 8).trim(), session)
								 * .equalsIgnoreCase(MSGConstants.SUCCESS)) { String bal =
								 * CoreTransactionMPOS.balance(lbrCode, benAccNo.trim(), amount, "C", session);
								 */

								/***
								 * Added By Aniket Desai on 3rd Sep, 2019 for Loan Voucher(Double CR To Loan
								 * Account):--End--
								 ***/
								if (bal != null && bal.trim().length() > 0 && !bal.trim().equalsIgnoreCase("51")
										&& !bal.trim().equalsIgnoreCase("99")) {
									// tx.commit();
									// session.close();

									if (transType.contains("~")) {
										String[] type = transType.split("~");
										String cashFlowType = type[0];
										transType = type[1];
									}
									String batchCodes[] = Props.getBatchProperty(transType).split("~");
									/***
									 * Added and commited by Aniket Desai on 23rd Oct, 2019 for issue #48692
									 ***/
									// D001004 d04OnlineBatchName =
									// VoucherMPOS.getBatchNameFromBatchCode(batchCodes[0]);

									Date openDate = getOpenDateNew(lbrCode, session);
									D010004 d04OnlineBatchName = getSelectedBatchNew(lbrCode, batchCodes,
											openDate, session);
									String onlineBatchName = d04OnlineBatchName.getId().getBatchCd().trim();
									resultMap.put(Code.RESULT, Code.SUCCESS);
									resultMap.put("Batch", onlineBatchName);
									resultMap.put(Code.SETNO, String.valueOf(setNo));
									resultMap.put(Code.SCROLLNO, String.valueOf(scrollNo));
									// throw new Exception("Testing");
									//t.commit();
									//session.close();
									return resultMap;
								} /*
									 * else { logger.error("Transaction unsuccessful");
									 * resultMap.put(Code.RESULT, Code.ERROR); resultMap.put(Code.SETNO, "0");
									 * resultMap.put(Code.SCROLLNO, "0"); return resultMap; } }
									 */ else {
									logger.error("Transaction unsuccessful");
									t.rollback();
									// session.close();
									resultMap.put(Code.RESULT, Code.ERROR);
									resultMap.put(Code.SETNO, "0");
									resultMap.put(Code.SCROLLNO, "0");
									session.close();
									return resultMap;
								}
							} else {
								logger.error("Transaction unsuccessful");
								t.rollback();
								// session.close();
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "0");
								resultMap.put(Code.SCROLLNO, "0");
								session.close();
								return resultMap;
							}
						} else {
							logger.error("Transaction unsuccessful");
							t.rollback();
							// session.close();
							resultMap.put(Code.RESULT, Code.ERROR);
							resultMap.put(Code.SETNO, "0");
							resultMap.put(Code.SCROLLNO, "0");
							session.close();
							return resultMap;
						}
					} else {
						logger.error("Transaction unsuccessful");
						t.rollback();
						// session.close();
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "0");
						resultMap.put(Code.SCROLLNO, "0");
						session.close();
						return resultMap;
					}
				} else {
					logger.error("Transaction unsuccessful");
					t.rollback();
					// session.close();
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "0");
					resultMap.put(Code.SCROLLNO, "0");
					session.close();
					return resultMap;
				}
			} else {
				logger.error("Transaction unsuccessful");
				t.rollback();
				// session.close();
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "0");
				resultMap.put(Code.SCROLLNO, "0");
				session.close();
				return resultMap;
			}

		
		}
		//return resultMap;
	}
	
	public static HashMap<String, String> billDeskVoucherEntry(int lbrCode, String accNo, String transType,
			String narration, double amount, String rrn) {
		VoucherCommon common = new VoucherCommon();
		HashMap<String, String> resultMap = new HashMap<>();
		int setNo = VoucherCommon.getNextSetNo();
		int scrollNo = VoucherCommon.getNextScrollNo();
		int reconNo = VoucherCommon.getNextReconNo(lbrCode);
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		try {
			common.debit(lbrCode, accNo, transType.toUpperCase(), setNo, scrollNo, narration, amount, rrn, session);
			if (!common.isAborted) {
				if (VoucherMPOS.updateProductBalances(amount, lbrCode, "D", accNo.substring(0, 8).trim(), session)
						.equalsIgnoreCase(MSGConstants.SUCCESS)) {
					if (!common.isAborted) {
						logger.error("Transaction successful");
						int setNoABB = VoucherCommon.getNextSetNo();
						int scrollNoAbb = VoucherCommon.getNextScrollNo();
						int scrollNoAbb1 = VoucherCommon.getNextScrollNo();
						int scrollNoAbb2 = VoucherCommon.getNextScrollNo();
						common.creditABB(lbrCode, MSGConstants.ABB_ACC, transType.toUpperCase(), setNo, scrollNoAbb,
								narration, lbrCode, amount, rrn, reconNo, session);
						if (!common.isAborted) {
							if (VoucherMPOS
									.updateProductBalances(amount, lbrCode, "C",
											MSGConstants.ABB_ACC.substring(0, 8).trim(), session)
									.equalsIgnoreCase(MSGConstants.SUCCESS)) {
								if (!common.isAborted) {
									logger.error("ABB Transaction successful");
									String crAccno = get32DigitAcctNo(
											getSysParameter("MBBILLDESKCRACC").trim(), 0, 0);
									// + " 000000000000000000000000";
									int crBrCode = Integer.valueOf(getSysParameter(MSGConstants.MBBRANCH).trim());
									common.credit(crBrCode, crAccno, MSGConstants.ABB, setNoABB, scrollNoAbb1,
											narration, amount, rrn, session);
									if (!common.isAborted) {
										if (VoucherMPOS
												.updateProductBalances(amount, crBrCode, "C",
														crAccno.substring(0, 8).trim(), session)
												.equalsIgnoreCase(MSGConstants.SUCCESS)) {
											if (!common.isAborted) {
												logger.error("Other Bank GL Transaction successful");
												common.debitABB(crBrCode, MSGConstants.ABB_ACC, MSGConstants.ABB,
														setNoABB, scrollNoAbb2, narration, 2, amount, rrn, reconNo,
														session);
												if (!common.isAborted) {
													if (VoucherMPOS.updateProductBalances(amount, crBrCode, "D",
															MSGConstants.ABB_ACC.substring(0, 8).trim(), session).trim()
															.equalsIgnoreCase(MSGConstants.SUCCESS)) {
														Date opdate = DataUtils.getOpenDate(lbrCode);
														String batchCodes[] = Props.getBatchProperty("IMPS").split("~");
														D001004 d04OnlineBatchName = VoucherMPOS
																.getBatchNameFromBatchCode(batchCodes[0]);
														String onlineBatchName = d04OnlineBatchName.getValue().trim();
														String batchCodes1[] = Props.getBatchProperty("ABB").split("~");
														D001004 d001004 = VoucherMPOS
																.getBatchNameFromBatchCode(batchCodes1[0]);
														String benBatchCode = d001004.getValue().trim();

														D100001 d100001 = VoucherMPOS.prepareReconObj(lbrCode, reconNo,
																opdate, 999999, onlineBatchName, benBatchCode, setNo,
																scrollNoAbb, setNoABB, scrollNoAbb1, scrollNoAbb2,
																crBrCode, "" + crAccno, Double.valueOf(amount), "D");
														System.out.println("d100001::>>>" + d100001);

														D100002 d100002 = VoucherMPOS.prepareRecon2Obj(lbrCode, reconNo,
																opdate, 999999, onlineBatchName, benBatchCode, setNo,
																scrollNoAbb, setNoABB, scrollNoAbb1, scrollNoAbb2,
																crBrCode, "" + crAccno, Double.valueOf(amount), "D");//
														System.out.println("d100002::>>>" + d100002);
														String bal = CoreTransactionMPOS.balance(lbrCode, accNo, amount,
																"D", session);
														if (bal != null && bal.trim().length() > 0
																&& !bal.equalsIgnoreCase("51")
																&& !bal.equalsIgnoreCase("99")) {
															try {
																session.save(d100001);
																session.save(d100002);
																session.flush();
																t.commit();
																resultMap.put(Code.RESULT, Code.SUCCESS);
																resultMap.put(Code.SETNO, String.valueOf(setNo));
																resultMap.put(Code.SCROLLNO, String.valueOf(scrollNo));
																//resultMap.put("Batch", onlineBatchName);
																return resultMap;
															} catch (Exception e) {
																// TODO: handle
																// exception
																e.printStackTrace();
																if (t.isActive())
																	t.rollback();
																resultMap.put(Code.RESULT, Code.ERROR);
																resultMap.put(Code.SETNO, "");
																resultMap.put(Code.SCROLLNO, "");
																return resultMap;
															}

														} else {
															if (t.isActive())
																t.rollback();
															logger.error("ABB Transaction unsuccessful");
															resultMap.put(Code.RESULT, Code.ERROR);
															resultMap.put(Code.SETNO, "");
															resultMap.put(Code.SCROLLNO, "");
															return resultMap;
														}
													} else {
														if (t.isActive())
															t.rollback();
														logger.error("ABB Transaction unsuccessful");
														resultMap.put(Code.RESULT, Code.ERROR);
														resultMap.put(Code.SETNO, "");
														resultMap.put(Code.SCROLLNO, "");
														return resultMap;
													}
												} else {
													if (t.isActive())
														t.rollback();
													logger.error("ABB Transaction unsuccessful");
													resultMap.put(Code.RESULT, Code.ERROR);
													resultMap.put(Code.SETNO, "");
													resultMap.put(Code.SCROLLNO, "");
													return resultMap;
												}
											} else {
												if (t.isActive())
													t.rollback();
												logger.error("ABB Transaction unsuccessful");
												resultMap.put(Code.RESULT, Code.ERROR);
												resultMap.put(Code.SETNO, "");
												resultMap.put(Code.SCROLLNO, "");
												return resultMap;
											}
										} else {
											if (t.isActive())
												t.rollback();
											logger.error("ABB Transaction unsuccessful");
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "");
											resultMap.put(Code.SCROLLNO, "");
											return resultMap;
										}
									} else {
										if (t.isActive())
											t.rollback();
										logger.error("ABB Transaction unsuccessful");
										resultMap.put(Code.RESULT, Code.ERROR);
										resultMap.put(Code.SETNO, "");
										resultMap.put(Code.SCROLLNO, "");
										return resultMap;
									}
								} else {
									if (t.isActive())
										t.rollback();
									logger.error("ABB Transaction unsuccessful");
									resultMap.put(Code.RESULT, Code.ERROR);
									resultMap.put(Code.SETNO, "");
									resultMap.put(Code.SCROLLNO, "");
									return resultMap;
								}
							} else {
								if (t.isActive())
									t.rollback();
								logger.error("ABB Transaction unsuccessful");
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "");
								resultMap.put(Code.SCROLLNO, "");
								return resultMap;
							}
						} else {
							if (t.isActive())
								t.rollback();
							logger.error("ABB Transaction unsuccessful");
							resultMap.put(Code.RESULT, Code.ERROR);
							resultMap.put(Code.SETNO, "");
							resultMap.put(Code.SCROLLNO, "");
							return resultMap;
						}

					} else {
						if (t.isActive())
							t.rollback();
						logger.error("Transaction unsuccessful");
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "");
						resultMap.put(Code.SCROLLNO, "");
						return resultMap;
					}
				} else {
					if (t.isActive())
						t.rollback();
					logger.error("Transaction unsuccessful");
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "");
					resultMap.put(Code.SCROLLNO, "");
					return resultMap;
				}
			} else {
				if (t.isActive())
					t.rollback();
				logger.error("Transaction unsuccessful");
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "");
				resultMap.put(Code.SCROLLNO, "");
				return resultMap;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			if (t.isActive())
				t.rollback();
			resultMap.put(Code.RESULT, Code.ERROR);
			resultMap.put(Code.SETNO, "");
			resultMap.put(Code.SCROLLNO, "");
			return resultMap;
		} finally {
			session.close();
			session = null;
			t = null;
		}

	}
	
	
	public static HashMap<String, String> reverseBillDesk(int lbrCode, String accNo, String transType,
			String narration, double amount, String rrn) {
		VoucherCommon common = new VoucherCommon();
		HashMap<String, String> resultMap = new HashMap<>();
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		int setNo = VoucherCommon.getNextSetNo();
		int scrollNo = VoucherCommon.getNextScrollNo();
		String drAccno = get32DigitAcctNo(getSysParameter("MBBILLDESKDRACC").trim(), 0, 0);// + "
																										// 000000000000000000000000";
		int crBrcode = Integer.valueOf(getSysParameter(MSGConstants.MBBRANCH).trim());
		int reconNo = getNextReconNo(crBrcode);
		logger.error("accNo::>>" + accNo);
		logger.error("lbrCode::>>" + lbrCode);
		common.credit(lbrCode, accNo, MSGConstants.ABB, setNo, scrollNo, narration, amount, rrn, session);
		if (!common.isAborted) {
			if (VoucherMPOS.updateProductBalances(amount, lbrCode, "C", accNo.substring(0, 8).trim(), session)
					.equalsIgnoreCase(MSGConstants.SUCCESS)) {
				String bal = CoreTransactionMPOS.balance(lbrCode, accNo, amount, "C", session);
				if (bal != null && bal.trim().length() > 0 && !bal.trim().equalsIgnoreCase("99")
						&& !bal.trim().equalsIgnoreCase("51")) {
					if (!common.isAborted) {
						int setNoABB = VoucherCommon.getNextSetNo();
						int scrollNoAbb = VoucherCommon.getNextScrollNo();
						int scrollNoAbb1 = VoucherCommon.getNextScrollNo();
						int scrollNoAbb2 = VoucherCommon.getNextScrollNo();
						logger.error("setNo::>>" + setNo + " scrollNo::>>" + scrollNo + " setNoABB::>>" + setNoABB
								+ " scrollNoAbb::>>>" + scrollNoAbb + " scrollNoAbb1::>>>" + scrollNoAbb1
								+ " scrollNoAbb2::>>>" + scrollNoAbb2);
						common.debitABB(lbrCode, MSGConstants.ABB_ACC, MSGConstants.ABB, setNo, scrollNoAbb, narration,
								crBrcode, amount, rrn, reconNo, session);
						if (!common.isAborted) {
							if (VoucherMPOS
									.updateProductBalances(amount, lbrCode, "D",
											MSGConstants.ABB_ACC.substring(0, 8).trim(), session)
									.trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
								if (!common.isAborted) {
									logger.error("ABB Transaction successful");
									// String
									// drAccno=getSysParameter("MBBILLPAY").trim()+"
									// 000000000000000000000000";
									common.debit(crBrcode, drAccno, transType.trim().toUpperCase(), setNoABB,
											scrollNoAbb1, narration, amount, rrn, session);
									if (!common.isAborted) {
										if (VoucherMPOS
												.updateProductBalances(amount, crBrcode, "D",
														drAccno.substring(0, 8).trim(), session)
												.equalsIgnoreCase(MSGConstants.SUCCESS)) {
											if (!common.isAborted) {
												logger.error("Other Bank GL Transaction successful");
												common.creditABB(crBrcode, MSGConstants.ABB_ACC,
														transType.trim().toUpperCase(), setNoABB, scrollNoAbb2,
														narration, lbrCode, amount, rrn, reconNo, session);
												if (!common.isAborted) {
													if (VoucherMPOS.updateProductBalances(amount, crBrcode, "C",
															MSGConstants.ABB_ACC.substring(0, 8).trim(), session).trim()
															.equalsIgnoreCase(MSGConstants.SUCCESS)) {
														Date opdate = DataUtils.getOpenDate(lbrCode);
														String batchCodes[] = Props.getBatchProperty("IMPS").split("~");
														D001004 d04OnlineBatchName = VoucherMPOS
																.getBatchNameFromBatchCode(batchCodes[0]);
														String onlineBatchName = d04OnlineBatchName.getValue().trim();
														String batchCodes1[] = Props.getBatchProperty("ABB").split("~");
														D001004 d001004 = VoucherMPOS
																.getBatchNameFromBatchCode(batchCodes1[0]);
														String benBatchCode = d001004.getValue().trim();

														D100001 d100001 = VoucherMPOS.prepareReconObj(crBrcode, reconNo,
																opdate, 999999, onlineBatchName, benBatchCode, setNoABB,
																scrollNoAbb2, setNo, scrollNo, scrollNoAbb, lbrCode,
																"" + accNo, Double.valueOf(amount), "D");
														System.out.println("d100001::>>>" + d100001);
														D100002 d100002 = VoucherMPOS.prepareRecon2Obj(crBrcode,
																reconNo, opdate, 999999, onlineBatchName, benBatchCode,
																setNoABB, scrollNoAbb2, setNo, scrollNo, scrollNoAbb,
																lbrCode, "" + accNo, Double.valueOf(amount), "D");
														System.out.println("d100002::>>>" + d100002);
														try {
															session.save(d100001);
															session.save(d100002);
															t.commit();
															session.close();
															session = null;
															t = null;
															resultMap.put(Code.RESULT, Code.SUCCESS);
															resultMap.put(Code.SETNO, String.valueOf(setNo));
															resultMap.put(Code.SCROLLNO, String.valueOf(scrollNo));
															return resultMap;
														} catch (Exception e) {
															e.printStackTrace();
															t.rollback();
															t = null;
															session = null;
															resultMap.put(Code.RESULT, Code.ERROR);
															resultMap.put(Code.SETNO, "0");
															resultMap.put(Code.SCROLLNO, "0");
															return resultMap;
														}
													} else {
														logger.error("ABB Transaction unsuccessful");
														resultMap.put(Code.RESULT, Code.ERROR);
														resultMap.put(Code.SETNO, "");
														resultMap.put(Code.SCROLLNO, "");
														common = null;
														session.close();
														session = null;
														t = null;
														return resultMap;
													}
												} else {
													logger.error("ABB Transaction unsuccessful");
													resultMap.put(Code.RESULT, Code.ERROR);
													resultMap.put(Code.SETNO, "");
													resultMap.put(Code.SCROLLNO, "");
													common = null;
													session.close();
													session = null;
													t = null;
													return resultMap;
												}
											} else {
												logger.error("ABB Transaction unsuccessful");
												resultMap.put(Code.RESULT, Code.ERROR);
												resultMap.put(Code.SETNO, "");
												resultMap.put(Code.SCROLLNO, "");
												common = null;
												session.close();
												session = null;
												t = null;
												return resultMap;
											}
										} else {
											logger.error("ABB Transaction unsuccessful");
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "");
											resultMap.put(Code.SCROLLNO, "");
											common = null;
											session.close();
											session = null;
											t = null;
											return resultMap;
										}
									} else {
										logger.error("ABB Transaction unsuccessful");
										resultMap.put(Code.RESULT, Code.ERROR);
										resultMap.put(Code.SETNO, "");
										resultMap.put(Code.SCROLLNO, "");
										common = null;
										session.close();
										session = null;
										t = null;
										return resultMap;
									}
								} else {
									logger.error("ABB Transaction unsuccessful");
									resultMap.put(Code.RESULT, Code.ERROR);
									resultMap.put(Code.SETNO, "");
									resultMap.put(Code.SCROLLNO, "");
									common = null;
									t.commit();
									session.close();
									session = null;
									t = null;
									return resultMap;
								}
							} else {
								logger.error("ABB Transaction unsuccessful");
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "");
								resultMap.put(Code.SCROLLNO, "");
								common = null;
								t.commit();
								session.close();
								session = null;
								t = null;
								return resultMap;
							}

						} else {
							logger.error("ABB Transaction unsuccessful");
							resultMap.put(Code.RESULT, Code.ERROR);
							resultMap.put(Code.SETNO, "");
							resultMap.put(Code.SCROLLNO, "");
							common = null;
							t.commit();
							session.close();
							session = null;
							t = null;
							return resultMap;
						}

					} else {
						logger.error("Transaction unsuccessful");
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "");
						resultMap.put(Code.SCROLLNO, "");
						common = null;
						t.commit();
						session.close();
						session = null;
						t = null;
						return resultMap;
					}
				} else {
					logger.error("Transaction unsuccessful");
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "");
					resultMap.put(Code.SCROLLNO, "");
					common = null;
					t.commit();
					session.close();
					session = null;
					t = null;
					return resultMap;
				}

			} else {
				logger.error("Transaction unsuccessful");
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "");
				resultMap.put(Code.SCROLLNO, "");
				common = null;
				t.commit();
				session.close();
				session = null;
				t = null;
				return resultMap;
			}
		} else {
			logger.error("Transaction unsuccessful");
			resultMap.put(Code.RESULT, Code.ERROR);
			resultMap.put(Code.SETNO, "");
			resultMap.put(Code.SCROLLNO, "");
			common = null;
			t.commit();
			session.close();
			session = null;
			t = null;
			return resultMap;
		}
	}
	
	
	
	
	public static HashMap<String, String> vpaVoucherEntry(int lbrCode, String accNo, String transType,
			String narration, double amount, String rrn) {
		VoucherCommon common = new VoucherCommon();
		HashMap<String, String> resultMap = new HashMap<>();
		int setNo = VoucherCommon.getNextSetNo();
		int scrollNo = VoucherCommon.getNextScrollNo();
		int reconNo = VoucherCommon.getNextReconNo(lbrCode);
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		try {
			common.debit(lbrCode, accNo, transType.toUpperCase(), setNo, scrollNo, narration, amount, rrn, session);
			if (!common.isAborted) {
				if (VoucherMPOS.updateProductBalances(amount, lbrCode, "D", accNo.substring(0, 8).trim(), session)
						.equalsIgnoreCase(MSGConstants.SUCCESS)) {
					if (!common.isAborted) {
						logger.error("Transaction successful");
						int setNoABB = VoucherCommon.getNextSetNo();
						int scrollNoAbb = VoucherCommon.getNextScrollNo();
						int scrollNoAbb1 = VoucherCommon.getNextScrollNo();
						int scrollNoAbb2 = VoucherCommon.getNextScrollNo();
						common.creditABB(lbrCode, MSGConstants.ABB_ACC, transType.toUpperCase(), setNo, scrollNoAbb,
								narration, lbrCode, amount, rrn, reconNo, session);
						if (!common.isAborted) {
							if (VoucherMPOS
									.updateProductBalances(amount, lbrCode, "C",
											MSGConstants.ABB_ACC.substring(0, 8).trim(), session)
									.equalsIgnoreCase(MSGConstants.SUCCESS)) {
								if (!common.isAborted) {
									logger.error("ABB Transaction successful");
									String crAccno = get32DigitAcctNo(
											getSysParameter("MBVPACRACC").trim(), 0, 0);
									// + " 000000000000000000000000";
									int crBrCode = Integer.valueOf(getSysParameter(MSGConstants.MBBRANCH).trim());
									common.credit(crBrCode, crAccno, MSGConstants.ABB, setNoABB, scrollNoAbb1,
											narration, amount, rrn, session);
									if (!common.isAborted) {
										if (VoucherMPOS
												.updateProductBalances(amount, crBrCode, "C",
														crAccno.substring(0, 8).trim(), session)
												.equalsIgnoreCase(MSGConstants.SUCCESS)) {
											if (!common.isAborted) {
												logger.error("Other Bank GL Transaction successful");
												common.debitABB(crBrCode, MSGConstants.ABB_ACC, MSGConstants.ABB,
														setNoABB, scrollNoAbb2, narration, 2, amount, rrn, reconNo,
														session);
												if (!common.isAborted) {
													if (VoucherMPOS.updateProductBalances(amount, crBrCode, "D",
															MSGConstants.ABB_ACC.substring(0, 8).trim(), session).trim()
															.equalsIgnoreCase(MSGConstants.SUCCESS)) {
														Date opdate = DataUtils.getOpenDate(lbrCode);
														String batchCodes[] = Props.getBatchProperty("IMPS").split("~");
														D001004 d04OnlineBatchName = VoucherMPOS
																.getBatchNameFromBatchCode(batchCodes[0]);
														String onlineBatchName = d04OnlineBatchName.getValue().trim();
														String batchCodes1[] = Props.getBatchProperty("ABB").split("~");
														D001004 d001004 = VoucherMPOS
																.getBatchNameFromBatchCode(batchCodes1[0]);
														String benBatchCode = d001004.getValue().trim();

														D100001 d100001 = VoucherMPOS.prepareReconObj(lbrCode, reconNo,
																opdate, 999999, onlineBatchName, benBatchCode, setNo,
																scrollNoAbb, setNoABB, scrollNoAbb1, scrollNoAbb2,
																crBrCode, "" + crAccno, Double.valueOf(amount), "D");
														System.out.println("d100001::>>>" + d100001);

														D100002 d100002 = VoucherMPOS.prepareRecon2Obj(lbrCode, reconNo,
																opdate, 999999, onlineBatchName, benBatchCode, setNo,
																scrollNoAbb, setNoABB, scrollNoAbb1, scrollNoAbb2,
																crBrCode, "" + crAccno, Double.valueOf(amount), "D");//
														System.out.println("d100002::>>>" + d100002);
														String bal = CoreTransactionMPOS.balance(lbrCode, accNo, amount,
																"D", session);
														if (bal != null && bal.trim().length() > 0
																&& !bal.equalsIgnoreCase("51")
																&& !bal.equalsIgnoreCase("99")) {
															try {
																session.save(d100001);
																session.save(d100002);
																session.flush();
																t.commit();
																resultMap.put(Code.RESULT, Code.SUCCESS);
																resultMap.put(Code.SETNO, String.valueOf(setNo));
																resultMap.put(Code.SCROLLNO, String.valueOf(scrollNo));
																//resultMap.put("Batch", onlineBatchName);
																return resultMap;
															} catch (Exception e) {
																// TODO: handle
																// exception
																e.printStackTrace();
																if (t.isActive())
																	t.rollback();
																resultMap.put(Code.RESULT, Code.ERROR);
																resultMap.put(Code.SETNO, "");
																resultMap.put(Code.SCROLLNO, "");
																return resultMap;
															}

														} else {
															if (t.isActive())
																t.rollback();
															logger.error("ABB Transaction unsuccessful");
															resultMap.put(Code.RESULT, Code.ERROR);
															resultMap.put(Code.SETNO, "");
															resultMap.put(Code.SCROLLNO, "");
															return resultMap;
														}
													} else {
														if (t.isActive())
															t.rollback();
														logger.error("ABB Transaction unsuccessful");
														resultMap.put(Code.RESULT, Code.ERROR);
														resultMap.put(Code.SETNO, "");
														resultMap.put(Code.SCROLLNO, "");
														return resultMap;
													}
												} else {
													if (t.isActive())
														t.rollback();
													logger.error("ABB Transaction unsuccessful");
													resultMap.put(Code.RESULT, Code.ERROR);
													resultMap.put(Code.SETNO, "");
													resultMap.put(Code.SCROLLNO, "");
													return resultMap;
												}
											} else {
												if (t.isActive())
													t.rollback();
												logger.error("ABB Transaction unsuccessful");
												resultMap.put(Code.RESULT, Code.ERROR);
												resultMap.put(Code.SETNO, "");
												resultMap.put(Code.SCROLLNO, "");
												return resultMap;
											}
										} else {
											if (t.isActive())
												t.rollback();
											logger.error("ABB Transaction unsuccessful");
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "");
											resultMap.put(Code.SCROLLNO, "");
											return resultMap;
										}
									} else {
										if (t.isActive())
											t.rollback();
										logger.error("ABB Transaction unsuccessful");
										resultMap.put(Code.RESULT, Code.ERROR);
										resultMap.put(Code.SETNO, "");
										resultMap.put(Code.SCROLLNO, "");
										return resultMap;
									}
								} else {
									if (t.isActive())
										t.rollback();
									logger.error("ABB Transaction unsuccessful");
									resultMap.put(Code.RESULT, Code.ERROR);
									resultMap.put(Code.SETNO, "");
									resultMap.put(Code.SCROLLNO, "");
									return resultMap;
								}
							} else {
								if (t.isActive())
									t.rollback();
								logger.error("ABB Transaction unsuccessful");
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "");
								resultMap.put(Code.SCROLLNO, "");
								return resultMap;
							}
						} else {
							if (t.isActive())
								t.rollback();
							logger.error("ABB Transaction unsuccessful");
							resultMap.put(Code.RESULT, Code.ERROR);
							resultMap.put(Code.SETNO, "");
							resultMap.put(Code.SCROLLNO, "");
							return resultMap;
						}

					} else {
						if (t.isActive())
							t.rollback();
						logger.error("Transaction unsuccessful");
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "");
						resultMap.put(Code.SCROLLNO, "");
						return resultMap;
					}
				} else {
					if (t.isActive())
						t.rollback();
					logger.error("Transaction unsuccessful");
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "");
					resultMap.put(Code.SCROLLNO, "");
					return resultMap;
				}
			} else {
				if (t.isActive())
					t.rollback();
				logger.error("Transaction unsuccessful");
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "");
				resultMap.put(Code.SCROLLNO, "");
				return resultMap;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			if (t.isActive())
				t.rollback();
			resultMap.put(Code.RESULT, Code.ERROR);
			resultMap.put(Code.SETNO, "");
			resultMap.put(Code.SCROLLNO, "");
			return resultMap;
		} finally {
			session.close();
			session = null;
			t = null;
		}

	}
	
	
	public static HashMap<String, String> reverseVPAPayment(int lbrCode, String accNo, String transType,
			String narration, double amount, String rrn) {
		VoucherCommon common = new VoucherCommon();
		HashMap<String, String> resultMap = new HashMap<>();
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		int setNo = VoucherCommon.getNextSetNo();
		int scrollNo = VoucherCommon.getNextScrollNo();
		String drAccno = get32DigitAcctNo(getSysParameter("MBVPADRACC").trim(), 0, 0);// + "
																										// 000000000000000000000000";
		int crBrcode = Integer.valueOf(getSysParameter(MSGConstants.MBBRANCH).trim());
		int reconNo = getNextReconNo(crBrcode);
		logger.error("accNo::>>" + accNo);
		logger.error("lbrCode::>>" + lbrCode);
		common.credit(lbrCode, accNo, MSGConstants.ABB, setNo, scrollNo, narration, amount, rrn, session);
		if (!common.isAborted) {
			if (VoucherMPOS.updateProductBalances(amount, lbrCode, "C", accNo.substring(0, 8).trim(), session)
					.equalsIgnoreCase(MSGConstants.SUCCESS)) {
				String bal = CoreTransactionMPOS.balance(lbrCode, accNo, amount, "C", session);
				if (bal != null && bal.trim().length() > 0 && !bal.trim().equalsIgnoreCase("99")
						&& !bal.trim().equalsIgnoreCase("51")) {
					if (!common.isAborted) {
						int setNoABB = VoucherCommon.getNextSetNo();
						int scrollNoAbb = VoucherCommon.getNextScrollNo();
						int scrollNoAbb1 = VoucherCommon.getNextScrollNo();
						int scrollNoAbb2 = VoucherCommon.getNextScrollNo();
						logger.error("setNo::>>" + setNo + " scrollNo::>>" + scrollNo + " setNoABB::>>" + setNoABB
								+ " scrollNoAbb::>>>" + scrollNoAbb + " scrollNoAbb1::>>>" + scrollNoAbb1
								+ " scrollNoAbb2::>>>" + scrollNoAbb2);
						common.debitABB(lbrCode, MSGConstants.ABB_ACC, MSGConstants.ABB, setNo, scrollNoAbb, narration,
								crBrcode, amount, rrn, reconNo, session);
						if (!common.isAborted) {
							if (VoucherMPOS
									.updateProductBalances(amount, lbrCode, "D",
											MSGConstants.ABB_ACC.substring(0, 8).trim(), session)
									.trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
								if (!common.isAborted) {
									logger.error("ABB Transaction successful");
									// String
									// drAccno=getSysParameter("MBBILLPAY").trim()+"
									// 000000000000000000000000";
									common.debit(crBrcode, drAccno, transType.trim().toUpperCase(), setNoABB,
											scrollNoAbb1, narration, amount, rrn, session);
									if (!common.isAborted) {
										if (VoucherMPOS
												.updateProductBalances(amount, crBrcode, "D",
														drAccno.substring(0, 8).trim(), session)
												.equalsIgnoreCase(MSGConstants.SUCCESS)) {
											if (!common.isAborted) {
												logger.error("Other Bank GL Transaction successful");
												common.creditABB(crBrcode, MSGConstants.ABB_ACC,
														transType.trim().toUpperCase(), setNoABB, scrollNoAbb2,
														narration, lbrCode, amount, rrn, reconNo, session);
												if (!common.isAborted) {
													if (VoucherMPOS.updateProductBalances(amount, crBrcode, "C",
															MSGConstants.ABB_ACC.substring(0, 8).trim(), session).trim()
															.equalsIgnoreCase(MSGConstants.SUCCESS)) {
														Date opdate = DataUtils.getOpenDate(lbrCode);
														String batchCodes[] = Props.getBatchProperty("IMPS").split("~");
														D001004 d04OnlineBatchName = VoucherMPOS
																.getBatchNameFromBatchCode(batchCodes[0]);
														String onlineBatchName = d04OnlineBatchName.getValue().trim();
														String batchCodes1[] = Props.getBatchProperty("ABB").split("~");
														D001004 d001004 = VoucherMPOS
																.getBatchNameFromBatchCode(batchCodes1[0]);
														String benBatchCode = d001004.getValue().trim();

														D100001 d100001 = VoucherMPOS.prepareReconObj(crBrcode, reconNo,
																opdate, 999999, onlineBatchName, benBatchCode, setNoABB,
																scrollNoAbb2, setNo, scrollNo, scrollNoAbb, lbrCode,
																"" + accNo, Double.valueOf(amount), "D");
														System.out.println("d100001::>>>" + d100001);
														D100002 d100002 = VoucherMPOS.prepareRecon2Obj(crBrcode,
																reconNo, opdate, 999999, onlineBatchName, benBatchCode,
																setNoABB, scrollNoAbb2, setNo, scrollNo, scrollNoAbb,
																lbrCode, "" + accNo, Double.valueOf(amount), "D");
														System.out.println("d100002::>>>" + d100002);
														try {
															session.save(d100001);
															session.save(d100002);
															t.commit();
															session.close();
															session = null;
															t = null;
															resultMap.put(Code.RESULT, Code.SUCCESS);
															resultMap.put(Code.SETNO, String.valueOf(setNo));
															resultMap.put(Code.SCROLLNO, String.valueOf(scrollNo));
															return resultMap;
														} catch (Exception e) {
															e.printStackTrace();
															t.rollback();
															t = null;
															session = null;
															resultMap.put(Code.RESULT, Code.ERROR);
															resultMap.put(Code.SETNO, "0");
															resultMap.put(Code.SCROLLNO, "0");
															return resultMap;
														}
													} else {
														logger.error("ABB Transaction unsuccessful");
														resultMap.put(Code.RESULT, Code.ERROR);
														resultMap.put(Code.SETNO, "");
														resultMap.put(Code.SCROLLNO, "");
														common = null;
														session.close();
														session = null;
														t = null;
														return resultMap;
													}
												} else {
													logger.error("ABB Transaction unsuccessful");
													resultMap.put(Code.RESULT, Code.ERROR);
													resultMap.put(Code.SETNO, "");
													resultMap.put(Code.SCROLLNO, "");
													common = null;
													session.close();
													session = null;
													t = null;
													return resultMap;
												}
											} else {
												logger.error("ABB Transaction unsuccessful");
												resultMap.put(Code.RESULT, Code.ERROR);
												resultMap.put(Code.SETNO, "");
												resultMap.put(Code.SCROLLNO, "");
												common = null;
												session.close();
												session = null;
												t = null;
												return resultMap;
											}
										} else {
											logger.error("ABB Transaction unsuccessful");
											resultMap.put(Code.RESULT, Code.ERROR);
											resultMap.put(Code.SETNO, "");
											resultMap.put(Code.SCROLLNO, "");
											common = null;
											session.close();
											session = null;
											t = null;
											return resultMap;
										}
									} else {
										logger.error("ABB Transaction unsuccessful");
										resultMap.put(Code.RESULT, Code.ERROR);
										resultMap.put(Code.SETNO, "");
										resultMap.put(Code.SCROLLNO, "");
										common = null;
										session.close();
										session = null;
										t = null;
										return resultMap;
									}
								} else {
									logger.error("ABB Transaction unsuccessful");
									resultMap.put(Code.RESULT, Code.ERROR);
									resultMap.put(Code.SETNO, "");
									resultMap.put(Code.SCROLLNO, "");
									common = null;
									t.commit();
									session.close();
									session = null;
									t = null;
									return resultMap;
								}
							} else {
								logger.error("ABB Transaction unsuccessful");
								resultMap.put(Code.RESULT, Code.ERROR);
								resultMap.put(Code.SETNO, "");
								resultMap.put(Code.SCROLLNO, "");
								common = null;
								t.commit();
								session.close();
								session = null;
								t = null;
								return resultMap;
							}

						} else {
							logger.error("ABB Transaction unsuccessful");
							resultMap.put(Code.RESULT, Code.ERROR);
							resultMap.put(Code.SETNO, "");
							resultMap.put(Code.SCROLLNO, "");
							common = null;
							t.commit();
							session.close();
							session = null;
							t = null;
							return resultMap;
						}

					} else {
						logger.error("Transaction unsuccessful");
						resultMap.put(Code.RESULT, Code.ERROR);
						resultMap.put(Code.SETNO, "");
						resultMap.put(Code.SCROLLNO, "");
						common = null;
						t.commit();
						session.close();
						session = null;
						t = null;
						return resultMap;
					}
				} else {
					logger.error("Transaction unsuccessful");
					resultMap.put(Code.RESULT, Code.ERROR);
					resultMap.put(Code.SETNO, "");
					resultMap.put(Code.SCROLLNO, "");
					common = null;
					t.commit();
					session.close();
					session = null;
					t = null;
					return resultMap;
				}

			} else {
				logger.error("Transaction unsuccessful");
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "");
				resultMap.put(Code.SCROLLNO, "");
				common = null;
				t.commit();
				session.close();
				session = null;
				t = null;
				return resultMap;
			}
		} else {
			logger.error("Transaction unsuccessful");
			resultMap.put(Code.RESULT, Code.ERROR);
			resultMap.put(Code.SETNO, "");
			resultMap.put(Code.SCROLLNO, "");
			common = null;
			t.commit();
			session.close();
			session = null;
			t = null;
			return resultMap;
		}
	}
}