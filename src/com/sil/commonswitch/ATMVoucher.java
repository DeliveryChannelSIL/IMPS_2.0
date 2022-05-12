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
import org.pmw.tinylog.Logger;
import com.sil.constants.Code;
import com.sil.constants.MSGConstants;
import com.sil.hbm.D001004;
import com.sil.hbm.D001004Id;
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
import com.sil.hbm.D030002;
import com.sil.hbm.D030003;
import com.sil.hbm.D030003Id;
import com.sil.hbm.D100001;
import com.sil.hbm.D100002;
import com.sil.hbm.MBTRSCROLLSEQ;
import com.sil.hbm.MBTRSETSEQ;
import com.sil.prop.ConfigurationLoader;
import com.sil.util.HBUtil;
import com.sil.util.Props;

public class ATMVoucher {
	public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ATMVoucher.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("0yyyyMMdd");
	private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmssSS");
	private static String flag = "Y";
	public boolean isAborted = false;
	public static double amt = 0;

	public static int getNextSetNo() {
		if (ConfigurationLoader.getParameters(false).getProperty("SQL_SERVER_VERSION").equalsIgnoreCase("2008")) {
			Session session = HBUtil.getSessionFactory().openSession();
			Transaction t = session.beginTransaction();
			try {
				D001004Id id = new D001004Id();
				id.setCode(MSGConstants.MBTR_SETNO);
				D001004 d001004 = session.get(D001004.class, id);
				if (d001004 == null)
					return 1;
				d001004.setValue(String.valueOf(Integer.valueOf(d001004.getValue().trim()) + 1));
				session.update(d001004);
				t.commit();
				return Integer.valueOf(d001004.getValue().trim());
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
		} else {
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
		if (ConfigurationLoader.getParameters(false).getProperty("SQL_SERVER_VERSION").equalsIgnoreCase("2008")) {
			Session session = HBUtil.getSessionFactory().openSession();
			Transaction t = session.beginTransaction();
			try {
				D001004Id id = new D001004Id();
				id.setCode(MSGConstants.MBTR_SROLLNO);
				D001004 d001004 = session.get(D001004.class, id);
				if (d001004 == null)
					return 1;
				d001004.setValue(String.valueOf(Integer.valueOf(d001004.getValue().trim()) + 1));
				session.update(d001004);
				t.commit();
				return Integer.valueOf(d001004.getValue().trim());
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
		MBTRSCROLLSEQ scrollSeq = new MBTRSCROLLSEQ();
		try (Session session = HBUtil.getSessionFactory().openSession()) {
			session.save(scrollSeq);
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
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
					Logger.info("Current Batch Name : " + batchName);
					batch = getBatch(lbrCode, batchName, entryDate);
					if (batch == null) {
						Logger.info("Batch with Batch Name " + batchName + " Not found");
					} else {
						if (batch.getStat() == 1 || batch.getStat() == 2) {
							Logger.info("Selected batch is " + batchName);
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
					Logger.info("Current Batch Name : " + batchName);
					batch = getBatchNew(lbrCode, batchName, entryDate, session);
					if (batch == null) {
						Logger.info("Batch with Batch Name " + batchName + " Not found");
						batch = null;
					} else {
						if (batch.getStat() == Byte.valueOf("1") || batch.getStat() == Byte.valueOf("2")) {
							Logger.info("Selected batch is " + batchName);
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
		D009021 d21 = null;
		// Session session = HBUtil.getSessionFactory().openSession();
		System.out.println("lbrCode::>>>" + lbrCode);
		System.out.println("prdCd::>>" + prdCd);
		try {
			D009021Id id = new D009021Id();
			id.setLbrCode(lbrCode);
			id.setPrdCd(prdCd.trim());
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
			Logger.info("Open Date is " + openDate);
			System.out.println("tType::>>>" + tType);
			// Get BatchCodes from properties file.
			String batchCode = Props.getBatchProperty(tType);
			Logger.info("Batch Code Form Properties File : " + batchCode);
			String batchCodes[] = batchCode.split("~");
			Logger.info("Number of Batches is " + batchCodes.length + "\t Names are " + Arrays.toString(batchCodes));
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
			Logger.info("Selected Batch : " + selectedBatch.getId().getBatchCd());

			D009040 d40 = new D009040();
			D009040Id id40 = new D009040Id();
			id40.setBatchCd(selectedBatch.getId().getBatchCd());
			id40.setEntryDate(selectedBatch.getId().getEntryDate());
			id40.setLbrCode(brCode);
			id40.setSetNo(setNo);
			id40.setScrollNo(scrollNo);

			d40.setId(id40);
			d40.setMainScrollNo(scrollNo);
			d40.setPostDate(selectedBatch.getPostDate());
			d40.setFeffDate(selectedBatch.getFeffDate());
			d40.setActivityType("ATMDR");
			d40.setCashFlowType("ATMDR");
			d40.setValueDate(selectedBatch.getPostDate());
			String bookType = getBookTypeNew(brCode, selectedBatch.getId().getBatchCd(), session);
			if (bookType == null) {
				Logger.error("BookType Not Found.");
				isAborted = true;
				return;
			}

			Logger.info("BookType is : " + bookType);
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
			Logger.info("ModuleType is : " + moduleType);
			d40.setMainModType(moduleType);
			d40.setVcrModType(moduleType);
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

			int usrCode2 = getUsrCodeNew("WEB", session);

			if (usrCode2 == 0) {
				Logger.error("UsrCode Not Found.");
				isAborted = true;
				return;
			}
			Logger.info("UsrCode is : " + usrCode2);
			d40.setMaker(usrCode2);

			d40.setMakerDate(selectedBatch.getId().getEntryDate());
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
			d40.setPostTime(Integer.parseInt(timeFormat.format(new Date()).substring(0, 8)));

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

	public void debitSameBranch(int brCode, String acctId, String tType, int setNo, int scrollNo, String narration,
			double amount, String rrn, int mainScrollNo, Session session) {
		try {
			Date openDate = getOpenDateNew(brCode, session); // Get Open Date
			if (openDate == null) {
				Logger.error("Open Date Not Found. Aborting Transaction");
				isAborted = true;
				return;
			}
			Logger.info("Open Date is " + openDate);

			// Get BatchCodes from properties file.
			String batchCode = Props.getBatchProperty(tType);
			Logger.info("Batch Code Form Properties File : " + batchCode);
			String batchCodes[] = batchCode.split("~");
			Logger.info("Number of Batches is " + batchCodes.length + "\t Names are " + Arrays.toString(batchCodes));
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
			Logger.info("Selected Batch : " + selectedBatch.getId().getBatchCd());

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

			Logger.info("BookType is : " + bookType);
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
			Logger.info("ModuleType is : " + moduleType);
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

			int usrCode2 = getUsrCodeNew("WEB", session);
			if (usrCode2 == 0) {
				Logger.error("UsrCode Not Found.");
				isAborted = true;
				return;
			}
			Logger.info("UsrCode is : " + usrCode2);
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

		} catch (Exception e) {
			isAborted = true;
			e.printStackTrace();
		}
	}

	public void debitSameBranchDDS(int brCode, String acctId, String tType, int setNo, int scrollNo, String narration,
			double amount, String rrn, int mainScrollNo, Session session, D010004 d010004) {
		try {
			/// Get Selected Batch.
			D010004 selectedBatch = d010004;// getSelectedBatch(brCode,
											// batchCodes, openDate);
			if (selectedBatch == null) {
				Logger.error("No Active Batch Found.");
				isAborted = true;
				return;
			}
			Logger.info("Selected Batch : " + selectedBatch.getId().getBatchCd());
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

			Logger.info("BookType is : " + bookType);
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

			d40.setActivityType("DR");
			if (moduleType == 47)
				d40.setCashFlowType("DDSDR");
			else
				d40.setCashFlowType("DR");
			Logger.info("ModuleType is : " + moduleType);
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

			int usrCode2 = getUsrCodeNew("WEB", session);
			if (usrCode2 == 0) {
				Logger.error("UsrCode Not Found.");
				isAborted = true;
				return;
			}
			Logger.info("UsrCode is : " + usrCode2);
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
			if (updateBatchBalance(session, amount, selectedBatch, brCode, MSGConstants.DR))
				isAborted = false;
			else
				isAborted = true;
			// session.flush();
			/*
			 * Query query = session.
			 * createQuery("UPDATE D010004 SET TotalDrVcrs=TotalDrVcrs+1, TotalDrAmtLcy = TotalDrAmtLcy +:amt WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd"
			 * ); query.setParameter("amt", amount);
			 * query.setParameter("lbrcode", brCode);
			 * query.setParameter("entrydate",
			 * selectedBatch.getId().getEntryDate());
			 * query.setParameter("batchcd",
			 * selectedBatch.getId().getBatchCd()); query.executeUpdate();
			 */
			// session.flush();
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
			Logger.info("Open Date is " + openDate);

			// Get BatchCodes from properties file.
			String batchCode = Props.getBatchProperty(tType);
			Logger.info("Batch Code Form Properties File : " + batchCode);
			String batchCodes[] = batchCode.split("~");
			Logger.info("Number of Batches is " + batchCodes.length + "\t Names are " + Arrays.toString(batchCodes));
			/*
			 * if(batchCodes == null || batchCodes.length <1){
			 * Logger.error("Batch Codes Not Found in Properties File.");
			 * isAborted = true; return; }
			 */
			/// Get Selected Batch.
			D010004 selectedBatch = getSelectedBatch(brCode, batchCodes, openDate);
			if (selectedBatch == null) {
				Logger.error("No Active Batch Found.");
				isAborted = true;
				return;
			}
			Logger.info("Selected Batch : " + selectedBatch.getId().getBatchCd());

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
			Logger.info("BookType is : " + bookType);
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
			Logger.info("ModuleType is : " + moduleType);
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
				return;
			}
			Logger.info("UsrCode is : " + usrCode2);
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
			Date openDate = getOpenDateNew(brCode, session); // Get Open Date
			if (openDate == null) {
				Logger.error("Open Date Not Found. Aborting Transaction");
				isAborted = true;
				return;
			}
			Logger.info("Open Date is " + openDate);
			// Get BatchCodes from properties file.
			System.out.println("tType:>>>" + tType);
			String batchCode = Props.getBatchProperty(tType.trim());
			Logger.info("Batch Code Form Properties File : " + batchCode);
			String batchCodes[] = batchCode.split("~");
			Logger.info("Number of Batches is " + batchCodes.length + "\t Names are " + Arrays.toString(batchCodes));
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

			Logger.info("Selected Batch : " + selectedBatch.getId().getBatchCd());
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
			d40.setActivityType("ATMCR");
			d40.setCashFlowType("ATMCR");
			d40.setValueDate(selectedBatch.getPostDate());
			String bookType = getBookTypeNew(brCode, selectedBatch.getId().getBatchCd(), session);
			if (bookType == null) {
				logger.error("BookType Not Found.");
				isAborted = true;
				return;
			}
			Logger.info("BookType is : " + bookType);
			d40.setBookType(bookType);

			d40.setDrCr('C');
			d40.setVcrAcctId(acctId);
			d40.setMainAcctId(acctId);

			short moduleType = getModuleTypeNew(brCode, acctId.substring(0, 8).trim(), session);
			if (moduleType == 0) {
				Logger.error("ModuleType Not Found.");
				isAborted = true;
				return;
			}
			Logger.info("ModuleType is : " + moduleType);
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
				return;
			}
			Logger.info("UsrCode is : " + usrCode2);
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
			Date openDate = getOpenDateNew(brCode, session); // Get Open Date
			if (openDate == null) {
				Logger.error("Open Date Not Found. Aborting Transaction");
				isAborted = true;
				return;
			}
			Logger.info("Open Date is " + openDate);

			// Get BatchCodes from properties file.
			String batchCode = Props.getBatchProperty(tType);
			Logger.info("Batch Code Form Properties File : " + batchCode);
			String batchCodes[] = batchCode.split("~");
			Logger.info("Number of Batches is " + batchCodes.length + "\t Names are " + Arrays.toString(batchCodes));
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

			Logger.info("Selected Batch : " + selectedBatch.getId().getBatchCd());

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
				Logger.error("BookType Not Found.");
				isAborted = true;
				return;
			}
			Logger.info("BookType is : " + bookType);
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
			Logger.info("ModuleType is : " + moduleType);
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

			int usrCode2 = getUsrCodeNew("WEB", session);
			if (usrCode2 == 0) {
				Logger.error("UsrCode Not Found.");
				isAborted = true;
				return;
			}
			Logger.info("UsrCode is : " + usrCode2);
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
			 * ); query.setParameter("amt", amount);
			 * query.setParameter("lbrcode", brCode);
			 * query.setParameter("entrydate",
			 * selectedBatch.getId().getEntryDate());
			 * query.setParameter("batchcd",
			 * selectedBatch.getId().getBatchCd()); query.executeUpdate();
			 * session.flush();
			 */
		} catch (Exception e) {
			isAborted = true;
			e.printStackTrace();
		}
	}

	public void creditSameBranchDDS(int brCode, String acctId, String tType, int setNo, int scrollNo, String narration,
			double amount, String rrn, int mainSrollNo, Session session, D010004 d010004) {
		try {
			/*
			 * Date openDate = getOpenDate(brCode); // Get Open Date if(openDate
			 * == null){
			 * Logger.error("Open Date Not Found. Aborting Transaction");
			 * isAborted = true; return; }
			 * Logger.info("Open Date is "+openDate);
			 * 
			 * //Get BatchCodes from properties file. String batchCode =
			 * Props.getBatchProperty(tType);
			 * Logger.info("Batch Code Form Properties File : "+batchCode);
			 * String batchCodes[] = batchCode.split("~");
			 * Logger.info("Number of Batches is "+batchCodes.
			 * length+"\t Names are "+Arrays.toString(batchCodes));
			 * if(batchCodes == null || batchCodes.length <1){
			 * Logger.error("Batch Codes Not Found in Properties File.");
			 * isAborted = true; return; }
			 */

			/// Get Selected Batch.
			D010004 selectedBatch = d010004;// getSelectedBatch(brCode,
											// batchCodes, openDate);
			if (selectedBatch == null) {
				Logger.error("No Active Batch Found.");
				isAborted = true;
				return;
			}

			Logger.info("Selected Batch : " + selectedBatch.getId().getBatchCd());

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
			Logger.info("BookType is : " + bookType);
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
			Logger.info("ModuleType is : " + moduleType);
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

			int usrCode2 = getUsrCodeNew("WEB", session);
			if (usrCode2 == 0) {
				Logger.error("UsrCode Not Found.");
				isAborted = true;
				return;
			}
			Logger.info("UsrCode is : " + usrCode2);
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
			 * Query query = session.
			 * createQuery("UPDATE D010004 SET TotalCrVcrs=TotalCrVcrs+1, TotalCrAmtLcy = TotalCrAmtLcy +:amt WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd"
			 * ); query.setParameter("amt", amount);
			 * query.setParameter("lbrcode", brCode);
			 * query.setParameter("entrydate",
			 * selectedBatch.getId().getEntryDate());
			 * query.setParameter("batchcd",
			 * selectedBatch.getId().getBatchCd()); query.executeUpdate();
			 * session.flush();
			 */
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
			Logger.info("Open Date is " + openDate);

			// Get BatchCodes from properties file.
			String batchCode = Props.getBatchProperty(tType);
			Logger.info("Batch Code Form Properties File : " + batchCode);
			String batchCodes[] = batchCode.split("~");
			Logger.info("Number of Batches is " + batchCodes.length + "\t Names are " + Arrays.toString(batchCodes));
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

			Logger.info("Selected Batch : " + selectedBatch.getId().getBatchCd());

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
			d40.setCashFlowType("ABBCR");
			d40.setValueDate(selectedBatch.getPostDate());
			String bookType = getBookTypeNew(brCode, selectedBatch.getId().getBatchCd(), session);
			if (bookType == null) {
				Logger.error("BookType Not Found.");
				isAborted = true;
				return;
			}
			Logger.info("BookType is : " + bookType);
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
			Logger.info("ModuleType is : " + moduleType);
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
			d40.setTokenNo(reconNo);

			int usrCode2 = getUsrCodeNew("WEB", session);
			if (usrCode2 == 0) {
				Logger.error("UsrCode Not Found.");
				isAborted = true;
				return;
			}
			Logger.info("UsrCode is : " + usrCode2);
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
			System.out.println("<<<<<----------Updating CreditABB vouchers--------------->>>>>>");
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
			Logger.info("Open Date is " + openDate);
			// Get BatchCodes from properties file.
			String batchCode = Props.getBatchProperty(tType);
			Logger.info("Batch Code Form Properties File : " + batchCode);
			String batchCodes[] = batchCode.split("~");
			Logger.info("Number of Batches is " + batchCodes.length + "\t Names are " + Arrays.toString(batchCodes));
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

			Logger.info("Selected Batch : " + selectedBatch.getId().getBatchCd());

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
			d40.setActivityType("ABB");
			d40.setCashFlowType("ABBDR");
			d40.setValueDate(selectedBatch.getPostDate());

			String bookType = getBookTypeNew(brCode, selectedBatch.getId().getBatchCd(), session);
			if (bookType == null) {
				Logger.error("BookType Not Found.");
				isAborted = true;
				return;
			}
			Logger.info("BookType is : " + bookType);
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
			Logger.info("ModuleType is : " + moduleType);
			d40.setMainModType(moduleType); // SELECT ModuleType FROM D009021
											// WHERE LBrCode =9 AND PrdCd = 'SB
			d40.setVcrModType(moduleType); // SELECT ModuleType FROM D009021
											// WHERE LBrCode =9 AND PrdCd = 'SB
			d40.setTrnCurCd("INR");
			d40.setFcyTrnAmt(amount);
			d40.setLcyConvRate(1);
			d40.setLcyTrnAmt(amount);
			d40.setInstrBankCd((short) benBrcode);
			d40.setInstrBranchCd((short) brCode);
			d40.setInstrType((short) 99); // Depend on chanel
			d40.setInstrNo(rrn); // RRN
			d40.setInstrDate(new Date()); // Blank
			d40.setParticulars(narration); // param
			d40.setSysGenVcr((byte) 0); // value 0
			d40.setShTotFlag('Y'); //
			d40.setShClrFlag('Y');
			d40.setAcTotFlag('Y');
			d40.setAcClrFlag('Y');
			d40.setTokenNo(reconNo);
			int usrCode2 = getUsrCodeNew("WEB", session);
			if (usrCode2 == 0) {
				Logger.error("UsrCode Not Found.");
				isAborted = true;
				return;
			}
			Logger.info("UsrCode is : " + usrCode2);
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
	 * private int getNextReconNo(int brCode) { int nextReconNo = -1;
	 * try(Session sessionLocal = HBUtil.getSessionFactory().openSession()) {
	 * D001005 d05 = (D001005)
	 * sessionLocal.createCriteria(D001005.class).add(Restrictions.eq(
	 * "id.catType", "ABBRECON")).add(Restrictions.eq("id.lbrCode",
	 * brCode)).setMaxResults(1).uniqueResult(); sessionLocal.close(); if(d05 ==
	 * null){ isAborted = true; return nextReconNo; } else{ int lastNo =
	 * d05.getLastNo(); Logger.info("Original LastNo : "+lastNo ); lastNo =
	 * lastNo +1; Logger.info("Incremented LastNo : "+lastNo );
	 * d05.setLastNo(lastNo);
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

	public boolean balance(int remBrCode, String remPrdAcctId, double amount, String drcr) {
		Logger.info("Transaction Amount : " + amount);
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		D009022Id remId = new D009022Id();
		remId.setLbrCode(remBrCode);
		remId.setPrdAcctId(remPrdAcctId);
		D009022 remAcct = session.get(D009022.class, remId);
		try {
			if (drcr.equalsIgnoreCase("D")) {
				Logger.info("Rem Amount Before : " + remAcct.getActClrBalFcy());
				if ((remAcct.getActClrBalFcy() - remAcct.getTotalLienFcy()) > amount) {
					Logger.info("Final Amount : " + (remAcct.getActClrBalFcy() - amount));
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
				Logger.info("Rem Amount Before : " + remAcct.getActClrBalFcy());
				Logger.info("Final Amount : " + (remAcct.getActClrBalFcy() + amount));

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
		ATMVoucher common = new ATMVoucher();
		HashMap<String, String> resultMap = new HashMap<>();
		int setNo = getNextSetNo();
		int scrollNo = getNextScrollNo();
		int reconNo = common.getNextReconNo(lbrCode);
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		common.debit(lbrCode, accNo, transType.toUpperCase(), setNo, scrollNo, narration, amount, rrn, session);
		if (!common.isAborted) {
			if (VoucherMPOS.updateProductBalances(amount, lbrCode, "D", accNo.substring(0, 8).trim(), session)
					.equalsIgnoreCase(MSGConstants.SUCCESS)) {
				if (!common.isAborted) {
					logger.error("Transaction successful");
					int setNoABB = getNextSetNo();
					int scrollNoAbb = getNextScrollNo();
					int scrollNoAbb1 = getNextScrollNo();
					int scrollNoAbb2 = getNextScrollNo();
					common.creditABB(lbrCode, MSGConstants.ABB_ACC, transType.toUpperCase(), setNo, scrollNoAbb,
							narration, lbrCode, amount, rrn, reconNo, session);
					if (!common.isAborted) {
						if (VoucherMPOS.updateProductBalances(amount, lbrCode, "C", MSGConstants.ABB, session).trim()
								.equalsIgnoreCase(MSGConstants.SUCCESS)) {
							if (!common.isAborted) {
								logger.error("ABB Transaction successful");
								String crAccno = getSysParameter(MSGConstants.MBRNCRACT).trim()
										+ "    000000000000000000000000";
								int crBrCode = Integer.valueOf(getSysParameter(MSGConstants.MBBRANCH).trim());
								common.credit(crBrCode, crAccno, MSGConstants.ABB, setNoABB, scrollNoAbb1, narration,
										amount, rrn, session);
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
													// TODO: handle exception
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

	public static HashMap<String, String> billPaymentVoucherEntry(int lbrCode, String accNo, String transType,
			String narration, double amount, String rrn) {
		ATMVoucher common = new ATMVoucher();
		HashMap<String, String> resultMap = new HashMap<>();
		int setNo = getNextSetNo();
		int scrollNo = getNextScrollNo();
		int reconNo = common.getNextReconNo(lbrCode);
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		common.debit(lbrCode, accNo, transType.toUpperCase(), setNo, scrollNo, narration, amount, rrn, session);
		if (!common.isAborted) {
			if (VoucherMPOS.updateProductBalances(amount, lbrCode, "D", accNo.substring(0, 8).trim(), session)
					.equalsIgnoreCase(MSGConstants.SUCCESS)) {
				if (!common.isAborted) {
					logger.error("Transaction successful");
					int setNoABB = getNextSetNo();
					int scrollNoAbb = getNextScrollNo();
					int scrollNoAbb1 = getNextScrollNo();
					int scrollNoAbb2 = getNextScrollNo();
					common.creditABB(lbrCode, MSGConstants.ABB_ACC, transType.toUpperCase(), setNo, scrollNoAbb,
							narration, lbrCode, amount, rrn, reconNo, session);
					if (!common.isAborted) {
						if (VoucherMPOS
								.updateProductBalances(amount, lbrCode, "D",
										MSGConstants.ABB_ACC.substring(0, 8).trim(), session)
								.equalsIgnoreCase(MSGConstants.SUCCESS)) {
							if (!common.isAborted) {
								logger.error("ABB Transaction successful");
								String crAccno = getSysParameter(MSGConstants.MBBBILLPAY_CR_ACC).trim()
										+ "    000000000000000000000000";
								int crBrCode = Integer.valueOf(getSysParameter(MSGConstants.MBBRANCH).trim());
								common.credit(crBrCode, crAccno, MSGConstants.ABB, setNoABB, scrollNoAbb1, narration,
										amount, rrn, session);
								if (!common.isAborted) {
									if (VoucherMPOS
											.updateProductBalances(amount, crBrCode, "C",
													crAccno.substring(0, 8).trim(), session)
											.equalsIgnoreCase(MSGConstants.SUCCESS)) {
										if (!common.isAborted) {
											logger.error("Other Bank GL Transaction successful");
											common.debitABB(crBrCode, MSGConstants.ABB_ACC, MSGConstants.ABB, setNoABB,
													scrollNoAbb2, narration, 2, amount, rrn, reconNo, session);
											if (!common.isAborted) {
												if (VoucherMPOS
														.updateProductBalances(amount, crBrCode, "C",
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
															session.close();
															session.close();
															session = null;
															t = null;
														} catch (Exception e) {
															// TODO: handle
															// exception
															e.printStackTrace();
															t.rollback();
															session.close();
															session = null;
															t = null;
														}
														resultMap.put(Code.RESULT, Code.SUCCESS);
														resultMap.put(Code.SETNO, String.valueOf(setNo));
														resultMap.put(Code.SCROLLNO, String.valueOf(scrollNo));
														t = null;
														return resultMap;
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

	public static HashMap<String, String> otherBankReverseVoucherEntry(int lbrCode, String accNo, String transType,
			String narration, double amount, String rrn) {
		ATMVoucher common = new ATMVoucher();
		HashMap<String, String> resultMap = new HashMap<>();
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		int setNo = getNextSetNo();
		int scrollNo = getNextScrollNo();
		String drAccno = getSysParameter(MSGConstants.MBRNCRACT).trim() + "    000000000000000000000000";
		;
		int crBrcode = Integer.valueOf(getSysParameter(MSGConstants.MBBRANCH).trim());
		int reconNo = common.getNextReconNo(crBrcode);
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
						int setNoABB = getNextSetNo();
						int scrollNoAbb = getNextScrollNo();
						int scrollNoAbb1 = getNextScrollNo();
						int scrollNoAbb2 = getNextScrollNo();
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

	public static HashMap<String, String> otherBankCreditVoucherEntryNew(int lbrCode, String accNo, String transType,
			String narration, double amount, String rrn) {
		ATMVoucher common = new ATMVoucher();
		HashMap<String, String> resultMap = new HashMap<>();
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		int setNo = getNextSetNo();
		int scrollNo = getNextScrollNo();
		String drAccno = getSysParameter(MSGConstants.MBRNCRACT).trim() + "    000000000000000000000000";
		;
		int crBrcode = Integer.valueOf(getSysParameter(MSGConstants.MBBRANCH).trim());
		int reconNo = common.getNextReconNo(crBrcode);

		logger.error("accNo::>>" + accNo);
		logger.error("lbrCode::>>" + lbrCode);
		common.credit(lbrCode, accNo, MSGConstants.ABB, setNo, scrollNo, narration, amount, rrn, session);
		if (!common.isAborted) {
			if (VoucherMPOS.updateProductBalances(amount, lbrCode, "C", accNo.substring(0, 8).trim(), session).trim()
					.equalsIgnoreCase(MSGConstants.SUCCESS)) {
				String bal = CoreTransactionMPOS.balance(lbrCode, accNo, amount, "C", session);
				if (bal != null && bal.trim().length() > 0 && !bal.trim().equalsIgnoreCase("51")
						&& !bal.trim().equalsIgnoreCase("99")) {
					if (!common.isAborted) {
						int setNoABB = getNextSetNo();
						int scrollNoAbb = getNextScrollNo();
						int scrollNoAbb1 = getNextScrollNo();
						int scrollNoAbb2 = getNextScrollNo();
						System.out.println("setNo::>>" + setNo);
						System.out.println("setNo::>>" + scrollNo);
						System.out.println("setNoABB::>>" + setNoABB);
						System.out.println("scrollNoAbb::>>>" + scrollNoAbb);
						System.out.println("scrollNoAbb1::>>>" + scrollNoAbb1);
						System.out.println("scrollNoAbb2::>>>" + scrollNoAbb2);
						logger.error("Transaction successful");
						common.debitABB(lbrCode, MSGConstants.ABB_ACC, MSGConstants.ABB, setNo, scrollNoAbb, narration,
								crBrcode, amount, rrn, reconNo, session);
						if (!common.isAborted) {
							if (VoucherMPOS
									.updateProductBalances(amount, lbrCode, "D",
											MSGConstants.ABB_ACC.substring(0, 8).trim(), session)
									.equalsIgnoreCase(MSGConstants.SUCCESS)) {
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
															session.flush();
															t.commit();
															session.close();
															session = null;
															t = null;
															resultMap.put(Code.RESULT, Code.SUCCESS);
															resultMap.put(Code.SETNO, String.valueOf(setNo));
															resultMap.put(Code.SCROLLNO, String.valueOf(scrollNo));
														} catch (Exception e) {
															e.printStackTrace();
															t.rollback();
															resultMap.put(Code.RESULT, Code.ERROR);
															resultMap.put(Code.SETNO, "");
															resultMap.put(Code.SCROLLNO, "");
														}
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
						logger.error("Transaction unsuccessful");
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
			session.close();
			session = null;
			t = null;
			return resultMap;
		}
	}

	public static HashMap<String, String> otherBankCreditVoucherEntry(int lbrCode, String accNo, String transType,
			String narration, double amount, String rrn) {
		ATMVoucher common = new ATMVoucher();
		HashMap<String, String> resultMap = new HashMap<>();
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		int setNo = getNextSetNo();
		int scrollNo = getNextScrollNo();
		String drAccno = getSysParameter(MSGConstants.MBRNCRACT).trim() + "    000000000000000000000000";
		;
		int crBrcode = Integer.valueOf(getSysParameter(MSGConstants.MBBRANCH).trim());
		int reconNo = common.getNextReconNo(lbrCode);

		logger.error("accNo::>>" + accNo);
		logger.error("lbrCode::>>" + lbrCode);
		common.credit(lbrCode, accNo, MSGConstants.ABB, setNo, scrollNo, narration, amount, rrn, session);
		VoucherMPOS.updateProductBalances(amount, lbrCode, "C", accNo.substring(0, 8).trim(), session);
		CoreTransactionMPOS.balance(lbrCode, accNo, amount, "C", session);
		if (!common.isAborted) {
			int setNoABB = getNextSetNo();
			int scrollNoAbb = getNextScrollNo();
			int scrollNoAbb1 = getNextScrollNo();
			int scrollNoAbb2 = getNextScrollNo();
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
						t.rollback();
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
		}
		return null;
	}

	public static HashMap<String, String> otherBranchVouchers(int lbrCode, String accNo, int benBrCode, String benAccNo,
			String transType, String narration, double amount, String rrn) {
		ATMVoucher common = new ATMVoucher();
		HashMap<String, String> resultMap = new HashMap<>();
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		if (lbrCode == benBrCode) {
			logger.error("Same Branch Fund Transfer....");
			int setNo = getNextSetNo();
			int mainScrollNo = getNextScrollNo();
			common.debitSameBranch(lbrCode, accNo, transType.toUpperCase(), setNo, getNextScrollNo(), narration, amount,
					rrn, mainScrollNo, session);
			if (!common.isAborted) {
				if (VoucherMPOS.updateProductBalances(amount, lbrCode, "D", accNo.substring(0, 8).trim(), session)
						.equalsIgnoreCase(MSGConstants.SUCCESS)) {
					String balResponse = CoreTransactionMPOS.balance(lbrCode, accNo.trim(), amount, "D", session);
					if (balResponse != null && balResponse.trim().length() > 0
							&& !balResponse.trim().equalsIgnoreCase("99")
							&& !balResponse.trim().equalsIgnoreCase("51")) {
						if (!common.isAborted) {
							logger.error("Transaction successful");
							common.creditSameBranch(benBrCode, benAccNo, transType, setNo, getNextScrollNo(), narration,
									amount, rrn, mainScrollNo, session);
							if (!common.isAborted) {
								if (VoucherMPOS.updateProductBalances(amount, benBrCode, "C",
										benAccNo.substring(0, 8).trim(), session)
										.equalsIgnoreCase(MSGConstants.SUCCESS)) {
									String bal = CoreTransactionMPOS.balance(benBrCode, benAccNo.trim(), amount, "C",
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
			int setNo = getNextSetNo();
			int reconNo = common.getNextReconNo(lbrCode);
			int scrollNo = getNextScrollNo();
			common.debit(lbrCode, accNo, transType.toUpperCase(), setNo, scrollNo, narration, amount, rrn, session);
			if (!common.isAborted) {
				if (VoucherMPOS.updateProductBalances(amount, lbrCode, "D", accNo.substring(0, 8).trim(), session)
						.equalsIgnoreCase(MSGConstants.SUCCESS)) {
					String balResponse = CoreTransactionMPOS.balance(lbrCode, accNo, amount, "D", session);
					if (balResponse != null && balResponse.trim().length() > 0 && !balResponse.equalsIgnoreCase("99")
							&& !balResponse.equalsIgnoreCase("51")) {
						if (!common.isAborted) {
							int setNoABB = getNextSetNo();
							int scrollNoAbb = getNextScrollNo();
							int scrollNoAbb1 = getNextScrollNo();
							int scrollNoAbb2 = getNextScrollNo();
							logger.error("Transaction successful");
							common.creditABB(lbrCode, MSGConstants.ABB_ACC, transType.toUpperCase(), setNo, scrollNoAbb,
									narration, benBrCode, amount, rrn, reconNo, session);
							if (!common.isAborted) {
								if (VoucherMPOS
										.updateProductBalances(amount, lbrCode, "C", MSGConstants.ABB.trim(), session)
										.equalsIgnoreCase(MSGConstants.SUCCESS)) {
									if (!common.isAborted) {
										logger.error("ABB Transaction successful");
										common.credit(benBrCode, benAccNo, MSGConstants.ABB, setNoABB, scrollNoAbb1,
												narration, amount, rrn, session);
										if (!common.isAborted) {
											if (VoucherMPOS
													.updateProductBalances(amount, benBrCode, "C",
															benAccNo.substring(0, 8).trim(), session)
													.equalsIgnoreCase(MSGConstants.SUCCESS)) {
												String bal = CoreTransactionMPOS.balance(benBrCode, benAccNo, amount,
														"C", session);
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

	public static HashMap<String, String> otherBranchReversal(int lbrCode, String accNo, int benBrCode, String benAccNo,
			String transType, String narration, double amount, String rrn) {
		ATMVoucher common = new ATMVoucher();
		HashMap<String, String> resultMap = new HashMap<>();
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		if (lbrCode == benBrCode) {
			logger.error("Same Branch Fund Transfer....");
			int setNo = getNextSetNo();
			int mainScrollNo = getNextScrollNo();
			common.creditSameBranch(lbrCode, accNo, transType.toUpperCase(), setNo, getNextScrollNo(), narration,
					amount, rrn, mainScrollNo, session);
			if (!common.isAborted) {
				if (VoucherMPOS.updateProductBalances(amount, lbrCode, "C", accNo.substring(0, 8).trim(), session)
						.trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
					String bal = CoreTransactionMPOS.balance(lbrCode, accNo, amount, "C", session);
					if (bal != null && bal.trim().length() > 0 && !bal.equalsIgnoreCase("51")
							&& !bal.equalsIgnoreCase("99")) {
						logger.error("Transaction successful");
						common.debitSameBranch(benBrCode, benAccNo, transType, setNo, getNextScrollNo(), narration,
								amount, rrn, mainScrollNo, session);
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
			int setNo = getNextSetNo();
			int reconNo = common.getNextReconNo(lbrCode);
			int scrollNo = getNextScrollNo();
			common.credit(lbrCode, accNo, transType.toUpperCase(), setNo, getNextScrollNo(), narration, amount, rrn,
					session);
			if (!common.isAborted) {
				if (VoucherMPOS.updateProductBalances(amount, lbrCode, "C", accNo.substring(0, 8).trim(), session)
						.trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
					String bal = CoreTransactionMPOS.balance(lbrCode, accNo, amount, "C", session);
					if (bal != null && bal.trim().length() > 0 && !bal.trim().equalsIgnoreCase("99")
							&& !bal.trim().equalsIgnoreCase("51")) {
						if (!common.isAborted) {
							int setNoABB = getNextSetNo();
							int scrollNoAbb = getNextScrollNo();
							int scrollNoAbb1 = getNextScrollNo();
							int scrollNoAbb2 = getNextScrollNo();
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
		System.out.println(getNextReconNo(1));
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
		 * VoucherCommon common=new VoucherCommon(); int
		 * setNo=common.getNextSetNo(); int reconNo=common.getNextReconNo(2);
		 * common.debit(3, "SB      000000000000670700000000", "IMPS", setNo,
		 * common.getNextScrollNo(),"tt", 21.2, "111111111111");
		 * if(!common.isAborted) { logger.error("Transaction successful");
		 * common.creditABB(3, "ABB     0000000000000000000", "IMPS", setNo,
		 * common.getNextScrollNo(), "tt", 2, 21.2, "111111111111",reconNo);
		 * 
		 * if(!common.isAborted) { int setNoABB=common.getNextSetNo();
		 * logger.error("ABB Transaction successful"); common.credit(2,
		 * "3264    000000000000000000000000", "ABB",setNoABB ,
		 * common.getNextScrollNo(), "tt", 21.2, "111111111111");
		 * if(!common.isAborted) {
		 * logger.error("Other Bank GL Transaction successful");
		 * common.debitABB(2, "ABB     0000000000000000000", "ABB", setNoABB,
		 * common.getNextScrollNo(),"tt", 21.2, "111111111111",reconNo);
		 * tx.commit(); }else { logger.error("ABB Transaction unsuccessful"); }
		 * 
		 * }else { logger.error("ABB Transaction unsuccessful");
		 * common.tx.rollback(); } }else {
		 * logger.error("Transaction unsuccessful"); common.tx.rollback(); }
		 */
	}

	public static String balanceUpdate(int remBrCode, String remPrdAcctId, double amount, String type) {
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		if (type.equalsIgnoreCase("D")) {
			Logger.info("Transaction Amount : " + amount);
			try {
				D009022Id remId = new D009022Id();
				remId.setLbrCode(remBrCode);
				remId.setPrdAcctId(remPrdAcctId);
				D009022 remAcct = session.get(D009022.class, remId);

				Logger.info("Rem Amount Before : " + remAcct.getActClrBalFcy());
				if (DataUtils.isOverDraftAccount(remBrCode, remPrdAcctId)) {
					Logger.info("Final Amount : " + (remAcct.getActClrBalFcy() - amount));
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
					Logger.info("Final Amount : " + (remAcct.getActClrBalFcy() - amount));
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
			Logger.info("Transaction Amount : " + amount);
			try {
				D009022Id remId = new D009022Id();
				remId.setLbrCode(remBrCode);
				remId.setPrdAcctId(remPrdAcctId);
				D009022 remAcct = session.get(D009022.class, remId);

				Logger.info("Rem Amount Before : " + remAcct.getActClrBalFcy());
				Logger.info("Final Amount : " + (remAcct.getActClrBalFcy() + amount));
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
		ATMVoucher common = new ATMVoucher();
		HashMap<String, String> resultMap = new HashMap<>();
		int setNo = getNextSetNo();
		int scrollNo = getNextScrollNo();
		int reconNo = getNextReconNo(lbrCode);
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		common.debit(lbrCode, accNo, transType.toUpperCase(), setNo, scrollNo, narration, amount, rrn, session);
		if (!common.isAborted) {
			if (VoucherMPOS.updateProductBalances(amount, lbrCode, "D", accNo.substring(0, 8).trim(), session)
					.equalsIgnoreCase(MSGConstants.SUCCESS)) {
				if (!common.isAborted) {
					logger.error("Transaction successful");
					int setNoABB = getNextSetNo();
					int scrollNoAbb = getNextScrollNo();
					int scrollNoAbb1 = getNextScrollNo();
					int scrollNoAbb2 = getNextScrollNo();
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
			System.out.println("<<:: -------------updateBatchBalance---------------::>>");
			System.out.println("session::>>" + session);
			System.out.println("amount::>>" + amount);
			System.out.println("selectedBatch.getId().getBatchCd()::>>" + selectedBatch.getId().getBatchCd());
			System.out.println("brCode:>>" + brCode);
			Query query = null;
			if (type.trim().equalsIgnoreCase("D")) {
				query = session.createQuery(
						"UPDATE D010004 SET TotalDrVcrs=TotalDrVcrs+1, TotalDrAmtLcy = TotalDrAmtLcy +:amt WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd");
				query.setParameter("amt", amount);
				query.setParameter("lbrcode", brCode);
				query.setParameter("entrydate", selectedBatch.getId().getEntryDate());
				query.setParameter("batchcd", selectedBatch.getId().getBatchCd());
			} else {
				query = session.createQuery(
						"UPDATE D010004 SET TotalCrVcrs=TotalCrVcrs+1, TotalCrAmtLcy = TotalCrAmtLcy +:amt WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd");//
				query.setParameter("amt", amount);
				query.setParameter("lbrcode", brCode);
				query.setParameter("entrydate", selectedBatch.getId().getEntryDate());
				query.setParameter("batchcd", selectedBatch.getId().getBatchCd());
			}
			int rows = query.executeUpdate();
			if (rows > 0)
				return true;
			return false;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}

	public static HashMap<String, String> reverseBillpayment(int lbrCode, String accNo, String transType,
			String narration, double amount, String rrn) {
		ATMVoucher common = new ATMVoucher();
		HashMap<String, String> resultMap = new HashMap<>();
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		int setNo = getNextSetNo();
		int scrollNo = getNextScrollNo();
		String drAccno = getSysParameter(MSGConstants.MBBBILLPAY_DR_ACC).trim() + "    000000000000000000000000";
		int crBrcode = Integer.valueOf(getSysParameter(MSGConstants.MBBRANCH).trim());
		int reconNo = common.getNextReconNo(crBrcode);
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
						int setNoABB = getNextSetNo();
						int scrollNoAbb = getNextScrollNo();
						int scrollNoAbb1 = getNextScrollNo();
						int scrollNoAbb2 = getNextScrollNo();
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
		ATMVoucher common = new ATMVoucher();
		HashMap<String, String> resultMap = new HashMap<>();
		int setNo = getNextSetNo();
		int scrollNo = getNextScrollNo();
		int reconNo = common.getNextReconNo(lbrCode);
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
		common.debit(lbrCode, accNo, transType.toUpperCase(), setNo, scrollNo, narration, amount, rrn, session);
		if (!common.isAborted) {
			if (VoucherMPOS.updateProductBalances(amount, lbrCode, "D", accNo.substring(0, 8).trim(), session)
					.equalsIgnoreCase(MSGConstants.SUCCESS)) {
				if (ConfigurationLoader.getParameters(false).getProperty("CGST_FLAG").equalsIgnoreCase("Y")) {
					finalAmount = finalAmount + cgstAmt;
					common.debitCreditGST(lbrCode,
							String.format("%-8s", MSGConstants.CGST) + "000000000000000000000000", transType, setNo,
							getNextScrollNo(), narration, cgstAmt, rrn, session, MSGConstants.CGST_CASH_FLOW_CR,
							MSGConstants.CR);
					if (!common.isAborted) {
						String response = VoucherMPOS.updateProductBalances(cgstAmt, lbrCode, "C", MSGConstants.CGST,
								session);
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
								String.format("%-8s", MSGConstants.SGST) + "000000000000000000000000", transType, setNo,
								getNextScrollNo(), narration, sgstAmt, rrn, session, MSGConstants.SGST_CASH_FLOW_CR,
								MSGConstants.CR);
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
								String.format("%-8s", MSGConstants.IGST) + "000000000000000000000000", transType, setNo,
								getNextScrollNo(), narration, igstAmt, rrn, session, MSGConstants.IGST_CASH_FLOW_CR,
								MSGConstants.CR);
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
								getNextScrollNo(), narration, chgAmt, rrn, session);
						if (!common.isAborted) {
							String response = VoucherMPOS.updateProductBalances(chgAmt, lbrCode, "C",
									MSGConstants.PREPAID_LOAD_BALANCE_CHARGES_ACCOUNT.substring(0, 8).trim(), session);
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
						int setNoABB = getNextSetNo();
						int scrollNoAbb = getNextScrollNo();
						int scrollNoAbb1 = getNextScrollNo();
						int scrollNoAbb2 = getNextScrollNo();
						common.creditABB(lbrCode, MSGConstants.ABB_ACC, transType.toUpperCase(), setNo, scrollNoAbb,
								narration, lbrCode, amount, rrn, reconNo, session);
						if (!common.isAborted) {
							if (VoucherMPOS
									.updateProductBalances(amount, lbrCode, "D",
											MSGConstants.ABB_ACC.substring(0, 8).trim(), session)
									.equalsIgnoreCase(MSGConstants.SUCCESS)) {
								if (!common.isAborted) {
									logger.error("ABB Transaction successful");
									String crAccno = getSysParameter(MSGConstants.PREPAID_CARD_ACC).trim()
											+ "    000000000000000000000000";
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
													if (VoucherMPOS.updateProductBalances(amount, crBrCode, "C",
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
														String bal = CoreTransactionMPOS.balance(lbrCode, accNo,
																finalAmount, "D", session);
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
																	session.close();
																	session.close();
																	session = null;
																	t = null;
																	resultMap.put(Code.RESULT, Code.SUCCESS);
																	resultMap.put(Code.SETNO, String.valueOf(setNo));
																	resultMap.put(Code.SCROLLNO,
																			String.valueOf(scrollNo));
																	t = null;
																	return resultMap;
																} catch (Exception e) {
																	// TODO:
																	// handle
																	// exception
																	e.printStackTrace();
																	t.rollback();
																	session.close();
																	session = null;
																	t = null;

																	logger.error("ABB Transaction unsuccessful");
																	resultMap.put(Code.RESULT, Code.ERROR);
																	resultMap.put(Code.SETNO, "");
																	resultMap.put(Code.SCROLLNO, "");
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

	public static HashMap<String, String> otherBranchLoanVouchers(int lbrCode, String accNo, int benBrCode,
			String benAccNo, String transType, String narration, double amount, String rrn) {
		logger.error("<<<<< ============otherBranchLoanVouchers.service============ >>>>>>");
		ATMVoucher common = new ATMVoucher();
		HashMap<String, String> resultMap = new HashMap<>();
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		if (lbrCode == benBrCode) {
			logger.error("Same Branch Fund Transfer....");
			int setNo = getNextSetNo();
			int mainScrollNo = getNextScrollNo();
			common.debitSameBranch(lbrCode, accNo, transType.trim().toUpperCase(), setNo, getNextScrollNo(), narration,
					amount, rrn, mainScrollNo, session);
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
								// ====================LOAN Account Voucher
								// Process Start===================
								D030003 d030003 = DataUtils.getLoanCharges(benBrCode, benAccNo, session);
								if (d030003 == null) {
									session.close();
									session = null;
									t = null;
									logger.error("Loan Parameters not found in D030003");
									resultMap.put(Code.RESULT, Code.ERROR);
									resultMap.put(Code.SETNO, "0");
									resultMap.put(Code.SCROLLNO, "0");
									return resultMap;
								} else {
									Date openDate = getOpenDateNew(lbrCode, session); // Get
																						// Open
																						// Date
									if (openDate == null) {
										logger.error("Open Date Not Found. Aborting Transaction");
										resultMap.put(Code.RESULT, Code.ERROR);
										resultMap.put(Code.SETNO, "0");
										resultMap.put(Code.SCROLLNO, "0");
										return resultMap;
									}
									String batchCode = Props.getBatchProperty(transType);
									logger.error("Batch Code Form Properties File : " + batchCode);
									String batchCodes[] = batchCode.split("~");
									logger.error("Number of Batches is " + batchCodes.length + "\t Names are "
											+ Arrays.toString(batchCodes));
									if (batchCodes == null || batchCodes.length < 1) {
										logger.error("Batch Codes Not Found in Properties File.");
										resultMap.put(Code.RESULT, Code.ERROR);
										resultMap.put(Code.SETNO, "0");
										resultMap.put(Code.SCROLLNO, "0");
										return resultMap;
									}
									D010004 selectedBatch = getSelectedBatchNew(lbrCode, batchCodes, openDate, session);
									if (selectedBatch == null) {
										logger.error("No Active Batch Found.");
										resultMap.put(Code.RESULT, Code.ERROR);
										resultMap.put(Code.SETNO, "0");
										resultMap.put(Code.SCROLLNO, "0");
										return resultMap;
									}
									D001004 d04OnlineBatchName = VoucherMPOS.getBatchNameFromBatchCodeNew(batchCodes[0],
											session);
									String onlineBatchName = d04OnlineBatchName.getValue().trim();

									D010004 onlineBatch = getD010004(lbrCode, onlineBatchName, openDate);
									amt = amount;
									System.out.println("Starting Amount::>>" + amt);
									if (d030003.getOthChgPrvdFcy() - d030003.getOthChgPaidFcy() != 0
											&& amt > d030003.getOthChgPrvdFcy() - d030003.getOthChgPaidFcy()) {
										amt = amt - d030003.getOthChgPrvdFcy() - d030003.getOthChgPaidFcy();
										D009040 d009040LnoCH = prepareD009040LoanObject(selectedBatch, onlineBatch,
												benBrCode, setNo, getNextScrollNo(), mainScrollNo, benAccNo, session,
												d030003.getOthChgPrvdFcy() - d030003.getOthChgPaidFcy(), narration,
												MSGConstants.CR, rrn, MSGConstants.LNOCHCR, MSGConstants.LOAN_ACTIVITY,
												MSGConstants.LOAN_TYPE_1);
										session.save(d009040LnoCH);
										D030003 d0300003 = updateBal(benAccNo, benBrCode, session,
												MSGConstants.LOAN_TYPE_1,
												d030003.getOthChgPrvdFcy() - d030003.getOthChgPaidFcy());
										if (d0300003 != null)
											session.update(d0300003);

									}
									System.out.println("Amount-->1::>>" + amt);
									if (d030003.getTaxPrvdFcy() - d030003.getTaxPaidFcy() != 0
											&& amt > d030003.getTaxPrvdFcy() - d030003.getTaxPaidFcy()) {
										amt = amt - d030003.getTaxPrvdFcy() - d030003.getTaxPaidFcy();
										D009040 d009040LnoCH = prepareD009040LoanObject(selectedBatch, onlineBatch,
												benBrCode, setNo, getNextScrollNo(), mainScrollNo, benAccNo, session,
												d030003.getTaxPrvdFcy() - d030003.getTaxPaidFcy(), narration,
												MSGConstants.CR, rrn, MSGConstants.LNTAXCR, MSGConstants.LOAN_ACTIVITY,
												MSGConstants.LOAN_TYPE_2);
										session.save(d009040LnoCH);
										D030003 d0300003 = updateBal(benAccNo, benBrCode, session,
												MSGConstants.LOAN_TYPE_2,
												d030003.getTaxPrvdFcy() - d030003.getTaxPaidFcy());
										if (d0300003 != null)
											session.update(d0300003);
									}
									System.out.println("Amount-->2::>>" + amt);
									if (d030003.getPenalPrvdFcy() - d030003.getPenalPaidFcy() != 0
											&& amt > d030003.getPenalPrvdFcy() - d030003.getPenalPaidFcy()) {
										amt = amt - d030003.getPenalPrvdFcy() - d030003.getPenalPaidFcy();
										D009040 d009040LnoCH = prepareD009040LoanObject(selectedBatch, onlineBatch,
												lbrCode, setNo, getNextScrollNo(), mainScrollNo, benAccNo, session,
												d030003.getPenalPrvdFcy() - d030003.getPenalPaidFcy(), narration,
												MSGConstants.CR, rrn, MSGConstants.LNPINTCR, MSGConstants.LOAN_ACTIVITY,
												MSGConstants.LOAN_TYPE_3);
										session.save(d009040LnoCH);
										D030003 d0300003 = updateBal(benAccNo, benBrCode, session,
												MSGConstants.LOAN_TYPE_3,
												d030003.getPenalPrvdFcy() - d030003.getPenalPaidFcy());
										if (d0300003 != null)
											session.update(d0300003);
									}
									System.out.println("Amount-->3::>>" + amt);
									if (amt > 0) {
										D009040 d009040LnoCH = prepareD009040LoanObject(selectedBatch, onlineBatch,
												benBrCode, setNo, getNextScrollNo(), mainScrollNo, benAccNo, session,
												amt, narration, MSGConstants.CR, rrn, MSGConstants.LNPCR,
												MSGConstants.LOAN_ACTIVITY, MSGConstants.LOAN_TYPE_4);
										session.save(d009040LnoCH);
										D030003 d0300003 = updateBal(benAccNo, benBrCode, session, "4", amt);
										if (d0300003 != null)
											session.update(d0300003);
										// amt+=d030003.getPenalPrvdFcy()-d030003.getPenalPaidFcy();
									}
								}
							}
							// common.creditSameBranch(benBrCode, benAccNo,
							// transType, setNo , getNextScrollNo(), narration,
							// amount, rrn,mainScrollNo,session);
							if (!common.isAborted) {
								if (VoucherMPOS.updateProductBalances(amount, benBrCode, "C",
										benAccNo.substring(0, 8).trim(), session)
										.equalsIgnoreCase(MSGConstants.SUCCESS)) {
									String bal = CoreTransactionMPOS.balance(benBrCode, benAccNo.trim(), amount, "C",
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
			int setNo = getNextSetNo();
			int reconNo = common.getNextReconNo(lbrCode);
			int scrollNo = getNextScrollNo();
			common.debit(lbrCode, accNo, transType.toUpperCase(), setNo, scrollNo, narration, amount, rrn, session);
			if (!common.isAborted) {
				if (VoucherMPOS.updateProductBalances(amount, lbrCode, "D", accNo.substring(0, 8).trim(), session)
						.equalsIgnoreCase(MSGConstants.SUCCESS)) {
					String balResponse = CoreTransactionMPOS.balance(lbrCode, accNo, amount, "D", session);
					if (balResponse != null && balResponse.trim().length() > 0 && !balResponse.equalsIgnoreCase("99")
							&& !balResponse.equalsIgnoreCase("51")) {
						if (!common.isAborted) {
							int setNoABB = getNextSetNo();
							int scrollNoAbb = getNextScrollNo();
							int scrollNoAbb1 = getNextScrollNo();
							int scrollNoAbb2 = getNextScrollNo();
							logger.error("Transaction successful");
							common.creditABB(lbrCode, MSGConstants.ABB_ACC, transType.toUpperCase(), setNo, scrollNoAbb,
									narration, benBrCode, amount, rrn, reconNo, session);
							if (!common.isAborted) {
								if (VoucherMPOS
										.updateProductBalances(amount, lbrCode, "C", MSGConstants.ABB.trim(), session)
										.equalsIgnoreCase(MSGConstants.SUCCESS)) {
									if (!common.isAborted) {
										logger.error("ABB Transaction successful");
										common.credit(benBrCode, benAccNo, MSGConstants.ABB, setNoABB, scrollNoAbb1,
												narration, amount, rrn, session);
										if (!common.isAborted) {
											if (VoucherMPOS
													.updateProductBalances(amount, benBrCode, "C",
															benAccNo.substring(0, 8).trim(), session)
													.equalsIgnoreCase(MSGConstants.SUCCESS)) {
												String bal = CoreTransactionMPOS.balance(benBrCode, benAccNo, amount,
														"C", session);
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

	public static HashMap<String, String> simSePayReverseVoucherEntry(int lbrCode, String accNo, String transType,
			String narration, double amount, String rrn) {
		ATMVoucher common = new ATMVoucher();
		HashMap<String, String> resultMap = new HashMap<>();
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		int setNo = getNextSetNo();
		int scrollNo = getNextScrollNo();
		String drAccno = String.format("%-8s", ATMVoucher.getSysParameter(MSGConstants.SIMPAYCRACCT).trim())
				+ "000000000000000000000000";
		;
		int crBrcode = Integer.valueOf(getSysParameter(MSGConstants.SIMPAYCRBRANCH).trim());
		int reconNo = common.getNextReconNo(crBrcode);
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
						int setNoABB = getNextSetNo();
						int scrollNoAbb = getNextScrollNo();
						int scrollNoAbb1 = getNextScrollNo();
						int scrollNoAbb2 = getNextScrollNo();
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
			d40.setVcrAcctId(prdAcctId);

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
		d40.setParticulars(particulars); // param
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
			else if (flag.equalsIgnoreCase("4"))
				d030003.setMainBalFcy(d030003.getMainBalFcy() + amount);
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
			Logger.info("Open Date is " + openDate);

			String batchCode = Props.getBatchProperty(tType);
			Logger.info("Batch Code Form Properties File : " + batchCode);
			String batchCodes[] = batchCode.split("~");
			Logger.info("Number of Batches is " + batchCodes.length + "\t Names are " + Arrays.toString(batchCodes));
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
			Logger.info("Selected Batch : " + selectedBatch.getId().getBatchCd());

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
			d40.setFeffDate(selectedBatch.getFeffDate()); // SELECT * FROM
															// D010004 WHERE
															// LBrCode =9 AND
															// EntryDate =
															// '19-APR-2016' AND
															// BatchCd = 'ABBTR'

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

			Logger.info("BookType is : " + bookType);
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
			Logger.info("ModuleType is : " + moduleType);
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

			int usrCode2 = getUsrCodeNew("WEB", session);

			if (usrCode2 == 0) {
				Logger.error("UsrCode Not Found.");
				isAborted = true;
				return;
			}
			Logger.info("UsrCode is : " + usrCode2);
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
