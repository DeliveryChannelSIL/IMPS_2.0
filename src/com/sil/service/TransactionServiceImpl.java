package com.sil.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import com.sil.commonswitch.ATMVoucher;
import com.sil.commonswitch.CommonVoucher;
import com.sil.commonswitch.CoreTransactionMPOS;
import com.sil.commonswitch.DataUtils;
import com.sil.commonswitch.OtherChannelServiceResponse;
import com.sil.commonswitch.P2AReversal;
import com.sil.commonswitch.P2ATransaction;
import com.sil.commonswitch.P2PReversal;
import com.sil.commonswitch.P2PTransactionEntry;
import com.sil.commonswitch.PrepaidCardLoadBalanceEntry;
import com.sil.commonswitch.SimSePayTransactionEntry;
import com.sil.commonswitch.UPITransactionEntry;
import com.sil.commonswitch.VoucherCommon;
import com.sil.commonswitch.VoucherMPOS;
import com.sil.constants.Code;
import com.sil.constants.MSGConstants;
import com.sil.constants.ResponseCodes;
import com.sil.constants.RtgsNeftHostToHostConstants;
import com.sil.domain.CustomerDetails;
import com.sil.domain.DebitAccount;
import com.sil.domain.IMPSNewTransactionResponse;
import com.sil.domain.IMPSTransactionRequest;
import com.sil.domain.IMPSTransactionResponse;
import com.sil.domain.QRUPIRequest;
import com.sil.domain.ReverseDebitAccount;
import com.sil.domain.TransactionValidationResponse;
import com.sil.domain.UPIRequest;
import com.sil.hbm.Billpayment;
import com.sil.hbm.D001004;
import com.sil.hbm.D002001;
import com.sil.hbm.D009011;
import com.sil.hbm.D009021;
import com.sil.hbm.D009021Id;
import com.sil.hbm.D009022;
import com.sil.hbm.D009022Id;
import com.sil.hbm.D009040;
import com.sil.hbm.D009040Id;
import com.sil.hbm.D009044;
import com.sil.hbm.D009122;
import com.sil.hbm.D009122Id;
import com.sil.hbm.D010001;
import com.sil.hbm.D010004;
import com.sil.hbm.D010010;
import com.sil.hbm.D010010Id;
import com.sil.hbm.D010054;
import com.sil.hbm.D047003;
import com.sil.hbm.D100001;
import com.sil.hbm.D100002;
import com.sil.hbm.D130001;
import com.sil.hbm.D130008;
import com.sil.hbm.D130008Id;
import com.sil.hbm.D130014;
import com.sil.hbm.D130031;
import com.sil.hbm.D350036;
import com.sil.hbm.D350037;
import com.sil.hbm.D350038;
import com.sil.hbm.D350059;
import com.sil.hbm.D350078;
import com.sil.hbm.D390077;
import com.sil.hbm.D946022;
import com.sil.hbm.GstChargesMaster;
import com.sil.hbm.GstTransactionHistory;
import com.sil.hbm.GstTransactionHistoryId;
import com.sil.hbm.PrepaidCardLoadBalance;
import com.sil.hbm.ReverseLoadBalance;
import com.sil.hbm.SimSePayTrancation;
import com.sil.operation.CoreBankingOperationImpl;
import com.sil.prop.ConfigurationLoader;
import com.sil.util.AccountDetailsUtil;
import com.sil.util.DateUtil;
import com.sil.util.HBUtil;
import com.sil.util.Props;

public class TransactionServiceImpl {
	public static Logger logger = Logger.getLogger(TransactionServiceImpl.class);
	public static final SimpleDateFormat sdf = new SimpleDateFormat("HHmmssSS");

	public static TransactionValidationResponse doMPOSWithDrawalTransaction(String sourceAcc, String destAcc,
			String custType, String amount, String rrn) {
		logger.error("<<<<<<<<:::: TransactionServiceImpl.doMPOSWithDrawalTransaction() ::>>>>>>>>" + rrn);
		logger.error("<<<<<<<<:::: TransactionServiceImpl.doMPOSWithDrawalTransaction() ::>>>>>>>>" + rrn);
		TransactionValidationResponse res = new TransactionValidationResponse();
		D009022 sourceAccount = null;
		sourceAccount = DataUtils.getAccount(sourceAcc);
		D009022 destAccount = DataUtils.getAccount(destAcc);
		if (custType.equalsIgnoreCase("BR")) {
		} else {
			res = validateAccount(sourceAccount, amount, "D");
			if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR))
				return res;
		}
		if (!custType.equalsIgnoreCase(MSGConstants.BRANCH_USER)) {
			res = validateAccount(destAccount, amount, "C");
			if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR))
				return res;
		}
		if (custType != null && !custType.trim().equalsIgnoreCase("")) {
			if (custType.equalsIgnoreCase(MSGConstants.BRANCH_USER)) {
				int setNo = VoucherMPOS.getNextSetNo();
				int scrollNo = VoucherMPOS.getNextScrollNo();
				Session session = HBUtil.getSessionFactory().openSession();
				Transaction t = session.beginTransaction();
				if (Integer.valueOf(sourceAcc.substring(0, 3).trim()) == Integer
						.valueOf(destAcc.substring(0, 3).trim())) {
					logger.error("<<<<<<<<<::::::::: Same Branch Transaction :::::::>>>>>>>>>");
					TransactionValidationResponse response = VoucherMPOS.debitBranch(
							"" + sourceAccount.getId().getPrdAcctId(), sourceAccount.getId().getLbrCode(),
							Double.parseDouble(amount.trim()), MSGConstants.MPOS_CASH_WITHDRAW, "MPOS", setNo, scrollNo,
							scrollNo, rrn, session);
					if (response.getResponse().trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
						D350078 d350078 = DataUtils.getMobNo(sourceAccount.getCustNo() + "", session);
						res.setMobNo(d350078 != null ? d350078.getId().getMobileNo() : "0");
						t.commit();
						session.close();
						session = null;
						t = null;
						String value = (String.valueOf(setNo) + "~" + String.valueOf(scrollNo) + "~"
								+ response.getBatchCode() + "~0");
						res.setOutput(value);
						res.setBatchCode(response.getBatchCode());
						res.setResponse(MSGConstants.SUCCESS);
						res.setErrorMsg(MSGConstants.SUCCESS_MSG);
						res.setSetNo(String.valueOf(setNo));
						res.setScrollNo(String.valueOf(scrollNo));
						res.setRrn(rrn);

						response = null;
						return res;
					} else {
						session.close();
						session = null;
						t = null;
						res.setResponse(MSGConstants.ERROR);
						res.setErrorMsg(MSGConstants.TRANSACTION_FAILURE);
						return res;
					}
				}
				Date opdate = DataUtils.getOpenDate(Integer.valueOf(sourceAcc.substring(0, 3)));
				logger.error("<<<<<<<<<<<::: Other branch withdraw Transaction :::>>>>>>>>>>>>>>");
				int reconNo = VoucherMPOS.getNextReconNo(Integer.valueOf(destAcc.substring(0, 3).trim()));
				logger.error("reconNo::>>" + reconNo);

				int benSetNo = VoucherMPOS.getNextSetNo();
				int benScrollNo = VoucherMPOS.getNextScrollNo();
				int scrollNoNew = VoucherMPOS.getNextScrollNo();

				String perticulars = "ReconNo =" + reconNo + " FromBrCode =" + Integer.valueOf(destAcc.substring(0, 3))
						+ " AcctId = " + sourceAccount.getId().getPrdAcctId().substring(0, 24) + "";
				TransactionValidationResponse resp1 = VoucherMPOS.debitABB("" + MSGConstants.ABB_ACC,
						Integer.valueOf(destAcc.substring(0, 3)), Double.valueOf(amount.trim()), perticulars, "MPOS",
						setNo, scrollNo, scrollNo, rrn, reconNo, "WITHDRAW", session);
				TransactionValidationResponse resp3 = VoucherMPOS.debitBranch("" + sourceAccount.getId().getPrdAcctId(),
						sourceAccount.getId().getLbrCode(), Double.valueOf(amount.trim()),
						"FromBrCode =" + Integer.valueOf(destAcc.substring(0, 3)) + "/"
								+ MSGConstants.MPOS_CASH_WITHDRAW,
						MSGConstants.ABBMPOS, benSetNo, scrollNoNew, scrollNoNew, rrn, session);
				TransactionValidationResponse resp2 = VoucherMPOS.creditABB("" + MSGConstants.ABB_ACC,
						Integer.valueOf(sourceAcc.substring(0, 3)), Double.valueOf(amount.trim()), perticulars,
						MSGConstants.ABBMPOS, benSetNo, benScrollNo, benScrollNo, rrn, reconNo, "WITHDRAW", session);

				VoucherMPOS.updateBalanceABB(Double.valueOf(amount.trim()), Integer.valueOf(destAcc.substring(0, 3)),
						"D", "ABB");
				VoucherMPOS.updateBalanceABB(Double.valueOf(amount.trim()), Integer.valueOf(sourceAcc.substring(0, 3)),
						"C", "ABB");
				VoucherMPOS.updateBalanceABB(Double.valueOf(amount.trim()), sourceAccount.getId().getLbrCode(), "D",
						"" + sourceAccount.getId().getPrdAcctId().substring(0, 8).trim());

				if (resp1.getResponse().trim().equalsIgnoreCase(MSGConstants.SUCCESS)
						&& resp2.getResponse().trim().equalsIgnoreCase(MSGConstants.SUCCESS)
						&& resp3.getResponse().trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
					String batchCodes[] = Props.getBatchProperty("MPOS").split("~");
					D001004 d04OnlineBatchName = VoucherMPOS.getBatchNameFromBatchCode(batchCodes[0]);
					String onlineBatchName = d04OnlineBatchName.getValue().trim();
					String batchCodes1[] = Props.getBatchProperty(MSGConstants.ABBMPOS).split("~");
					D001004 d001004 = VoucherMPOS.getBatchNameFromBatchCode(batchCodes1[0]);
					String benBatchCode = d001004.getValue().trim();

					D100001 d100001 = VoucherMPOS.prepareReconObj(Integer.valueOf(destAcc.substring(0, 3)), reconNo,
							opdate, 999999, onlineBatchName, benBatchCode, setNo, scrollNo, benSetNo, benScrollNo,
							benScrollNo, Integer.valueOf(sourceAcc.substring(0, 3)),
							"" + sourceAccount.getId().getPrdAcctId(), Double.valueOf(amount), "D");
					logger.error("d100001::>>>" + d100001);

					D100002 d100002 = VoucherMPOS.prepareRecon2Obj(Integer.valueOf(destAcc.substring(0, 3)), reconNo,
							opdate, 999999, onlineBatchName, benBatchCode, setNo, scrollNo, benSetNo, scrollNoNew,
							scrollNoNew, Integer.valueOf(sourceAcc.substring(0, 3)),
							"" + sourceAccount.getId().getPrdAcctId(), Double.valueOf(amount), "D");
					logger.error("d100001::>>>" + d100002);
					try {
						session.save(d100001);
						session.save(d100002);
						t.commit();
						session.close();
						session = null;
						t = null;
					} catch (Exception e) {
						session.close();
						session = null;
						t = null;
						e.printStackTrace();
					}
					String value = (String.valueOf(setNo) + "~" + String.valueOf(scrollNo) + "~" + resp3.getBatchCode()
							+ "~0");
					D350078 d350078 = DataUtils.getMobNo(sourceAccount.getCustNo() + "", session);
					res.setMobNo(d350078 != null ? d350078.getId().getMobileNo() : "0");
					res.setOutput(value);
					res.setBatchCode(resp3.getBatchCode());
					res.setResponse(MSGConstants.SUCCESS);
					res.setErrorMsg(MSGConstants.SUCCESS_MSG);
					res.setSetNo(String.valueOf(setNo));
					res.setScrollNo(String.valueOf(scrollNo));
					res.setRrn(rrn);
					resp1 = null;
					resp2 = null;
					resp3 = null;
					return res;
				} else {
					session.close();
					session = null;
					t = null;
					res.setResponse(MSGConstants.ERROR);
					res.setErrorMsg(MSGConstants.TRANSACTION_FAILURE);
					return res;
				}
			} else if (custType.equalsIgnoreCase(MSGConstants.BUSINESS_CORRESPONDANCE)) {
				int setNo = VoucherMPOS.getNextSetNo();
				int scrollNo = VoucherMPOS.getNextScrollNo();
				TransactionValidationResponse response = new VoucherMPOS().drCrMerchantOrBC(
						sourceAccount.getId().getPrdAcctId(), sourceAccount.getId().getLbrCode(),
						destAccount.getId().getPrdAcctId(), destAccount.getId().getLbrCode(), Double.valueOf(amount),
						MSGConstants.MPOS_CASH_WITHDRAW, "MPOS", setNo, scrollNo, VoucherMPOS.getNextScrollNo(), rrn);
				if (response.getResponse().trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
					String bal = CoreTransactionMPOS.balance(sourceAccount.getId().getLbrCode(),
							sourceAccount.getId().getPrdAcctId(), Double.parseDouble(amount), "D");
					logger.error("Balance::>>" + bal);
					logger.error("Balance::>>" + CoreTransactionMPOS.balance(destAccount.getId().getLbrCode(),
							destAccount.getId().getPrdAcctId(), Double.parseDouble(amount), "C"));
					String value = (String.valueOf(setNo) + "~" + String.valueOf(scrollNo) + "~"
							+ response.getBatchCode() + "~" + bal);
					Session session = HBUtil.getSessionFactory().openSession();

					D350078 d350078 = DataUtils.getMobNo(sourceAccount.getCustNo() + "", session);
					session.close();
					session = null;
					res.setMobNo(d350078 != null ? d350078.getId().getMobileNo() : "0");
					res.setOutput(value);
					res.setBatchCode(response.getBatchCode());
					res.setResponse(MSGConstants.SUCCESS);
					res.setErrorMsg(MSGConstants.SUCCESS_MSG);
					res.setSetNo(String.valueOf(setNo));
					res.setScrollNo(String.valueOf(scrollNo));
					res.setRrn(rrn);
					response = null;
					return res;
				} else {
					res.setResponse(MSGConstants.ERROR);
					res.setErrorMsg(MSGConstants.TRANSACTION_FAILURE);
					res.setRrn(rrn);
					return res;
				}
			} else if (custType.equalsIgnoreCase(MSGConstants.MERCHANT)) {
				int setNo = VoucherMPOS.getNextSetNo();
				int scrollNo = VoucherMPOS.getNextScrollNo();
				TransactionValidationResponse response = new VoucherMPOS().drCrMerchantOrBC(
						sourceAccount.getId().getPrdAcctId(), sourceAccount.getId().getLbrCode(),
						destAccount.getId().getPrdAcctId(), destAccount.getId().getLbrCode(), Double.valueOf(amount),
						MSGConstants.MPOS_CASH_WITHDRAW, "MPOS", setNo, scrollNo, VoucherMPOS.getNextScrollNo(), rrn);
				if (response.getResponse().trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
					String bal = CoreTransactionMPOS.balance(sourceAccount.getId().getLbrCode(),
							sourceAccount.getId().getPrdAcctId(), Double.parseDouble(amount), "D");
					logger.error("Balance::>>" + bal);
					logger.error("Balance::>>" + CoreTransactionMPOS.balance(destAccount.getId().getLbrCode(),
							destAccount.getId().getPrdAcctId(), Double.parseDouble(amount), "C"));
					Session session = HBUtil.getSessionFactory().openSession();
					D350078 d350078 = DataUtils.getMobNo(sourceAccount.getCustNo() + "", session);
					session.close();
					session = null;
					res.setMobNo(d350078 != null ? d350078.getId().getMobileNo() : "0");
					String value = (String.valueOf(setNo) + "~" + String.valueOf(scrollNo) + "~"
							+ response.getBatchCode() + "~" + bal);
					res.setOutput(value);
					res.setBatchCode(response.getBatchCode());
					res.setResponse(MSGConstants.SUCCESS);
					res.setErrorMsg(MSGConstants.SUCCESS_MSG);
					res.setSetNo(String.valueOf(setNo));
					res.setScrollNo(String.valueOf(scrollNo));
					res.setRrn(rrn);
					response = null;
					return res;
				} else {
					res.setResponse(MSGConstants.ERROR);
					res.setErrorMsg(MSGConstants.TRANSACTION_FAILURE);
					return res;
				}
			}
			res.setResponse(MSGConstants.ERROR);
			res.setErrorMsg(MSGConstants.INVALID_TRANSACTION_TYPE);
			return res;
		}
		res.setResponse(MSGConstants.ERROR);
		res.setErrorMsg(MSGConstants.INVALID_TRANSACTION_TYPE);
		return res;
	}

	public static TransactionValidationResponse doMPOSDepositTransaction(String sourceAcc, String destAcc,
			String custType, String amount, String rrn, String forceTransactionYn, String ownerId, String deviceId) {
		logger.error("<<<<<<<<:::: TransactionServiceImpl.doMPOSDepositTransaction() ::>>>>>>>>" + rrn);
		logger.error("<<<<<<<<:::: TransactionServiceImpl.doMPOSDepositTransaction() ::>>>>>>>>" + rrn);
		logger.error("sourceAcc::>>" + sourceAcc);
		logger.error("destAcc::>>>" + destAcc);
		logger.error("sourceAcc::>>" + sourceAcc + " destAcc::>>>" + destAcc + " custType::>>" + custType
				+ " amount::>>" + amount + " RRN::>>" + rrn);
		TransactionValidationResponse res = new TransactionValidationResponse();
		D009022 sourceAccount = null;
		if (!custType.equalsIgnoreCase(MSGConstants.BRANCH_USER)) {
			sourceAccount = DataUtils.getAccount(sourceAcc);
			if (sourceAccount == null) {
				res.setResponse(MSGConstants.ERROR);
				res.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
				return res;
			}
		}
		D009022 destAccount = null;// DataUtils.getAccount(destAcc);
		if (sourceAccount != null) {
			if (custType.toUpperCase().contains("PGM")) {
				logger.error("PGM Account");
				destAccount = DataUtils.getAccount(destAcc);
				if (destAccount == null) {
					res.setResponse(MSGConstants.ERROR);
					res.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
					return res;
				}
				
				List<D047003> list6 = null;
 				Session session = HBUtil.getSessionFactory().openSession();
				Criteria criteria6 = session.createCriteria(D047003.class);
				criteria6.add(Restrictions.eq("id.lbrCode", sourceAccount.getId().getLbrCode()));
				criteria6.add(Restrictions.eq("agtPrdAcctId", sourceAccount.getId().getPrdAcctId()));
				criteria6.add(Restrictions.eq("id.custPrdAcctId", destAccount.getId().getPrdAcctId()));
				// criteria5.add(Restrictions.ne("acctStat", Byte.valueOf("3")));
				// criteria5.add(Restrictions.gt("matDate", new Date()));
				list6 = criteria6.list();
				
				if(ConfigurationLoader.getParameters(false).getProperty("AG_Maturity_Date_Check_YN")
				.equalsIgnoreCase(MSGConstants.YES)) {
					if (list6.get(0).getMatDate().before(new Date())) {
						res.setResponse(MSGConstants.ERROR);
						res.setErrorMsg("Maturity Date of the Account has Expired"); 
				 		return res; 
				 	}
				}

				
				res = validateAccount(sourceAccount, amount, "AGENT");
				if (res != null) {
					if (res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
						res.setName(destAccount.getLongName());
						return res;
					}

				}
				logger.error("PGM Account");

				res = validateAccount(destAccount, amount, "C");
				if (res != null) {
					if (res.getResponse().equalsIgnoreCase(MSGConstants.ERROR))
						return res;
				}
				if (!custType.equalsIgnoreCase(MSGConstants.BRANCH_USER)) {
					destAccount = DataUtils.getAccount(destAcc);
					res = validateAccount(destAccount, amount, "C");
					if (res.getResponse().equalsIgnoreCase(MSGConstants.ERROR))
						return res;
				}
			} else {
				if (!custType.equalsIgnoreCase(MSGConstants.BRANCH_USER)) {
					res = validateAccount(sourceAccount, amount, "D");
					if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
						if(custType.equalsIgnoreCase(MSGConstants.LOAN_ACC)) {
							if(res.getErrorMsg().contains("Remitter"))
								res.setErrorMsg(res.getErrorMsg().replace("Remitter", "Agent"));
							else
								res.setErrorMsg("Agent "+ res.getErrorMsg());
						}
						return res;
					}
						
				}
			}
		}
		if (custType.equalsIgnoreCase(MSGConstants.BRANCH_USER)) {
			destAccount = DataUtils.getAccount(destAcc);
			if (destAccount == null) {
				res.setResponse(MSGConstants.ERROR);
				res.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
				return res;
			}
			res = validateAccount(destAccount, amount, "C");
			if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR))
				return res;
		}
		if (!custType.equalsIgnoreCase(MSGConstants.BRANCH_USER)) {
			destAccount = DataUtils.getAccount(destAcc);
			res = validateAccount(destAccount, amount, "C");
			if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
				if(custType.equalsIgnoreCase(MSGConstants.LOAN_ACC))
					res.setErrorMsg("Loan "+ res.getErrorMsg());
				return res;
			}
		}
		if (custType != null && !custType.trim().equalsIgnoreCase("")) {
			Session session = HBUtil.getSessionFactory().openSession();
			Transaction t = session.beginTransaction();
			if (custType.equalsIgnoreCase(MSGConstants.BRANCH_USER)) {
				logger.error("Source Branch Code::>>" + Integer.valueOf(sourceAcc.substring(0, 3)));
				logger.error("Destination Branch Code::>>" + Integer.valueOf(destAcc.substring(0, 3)));
				if (Integer.valueOf(sourceAcc.substring(0, 3)) == Integer.valueOf(destAcc.substring(0, 3))) {
					logger.error("<<<<<<<<<<<::: Same branch deposit transaction :::>>>>>>>>>>>>>>");
					int setNo = VoucherMPOS.getNextSetNo();
					int scrollNo = VoucherMPOS.getNextScrollNo();

					TransactionValidationResponse response = VoucherMPOS.creditBranch(
							"" + destAccount.getId().getPrdAcctId(), destAccount.getId().getLbrCode(),
							Double.parseDouble(amount.trim()), MSGConstants.MPOS_CASH_DEPOSIT, "MPOS", setNo, scrollNo,
							scrollNo, rrn, session);
					// TransactionValidationResponse
					// response=VoucherMPOS.creditBranch(""+sourceAccount.getId().getPrdAcctId(),
					// sourceAccount.getId().getLbrCode(),
					// Double.parseDouble(amount.trim()),
					// MSGConstants.MPOS_CASH_DEPOSIT, "MPOS",
					// setNo,scrollNo,scrollNo,rrn);
					if (response.getResponse().trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
						D350078 d350078 = DataUtils.getMobNo(destAccount.getCustNo() + "", session);
						res.setMobNo(d350078 != null ? d350078.getId().getMobileNo() : "0");
						t.commit();
						session = null;
						t = null;
						String value = (String.valueOf(setNo) + "~" + String.valueOf(scrollNo) + "~"
								+ response.getBatchCode() + "~0");
						res.setOutput(value);
						res.setBatchCode(response.getBatchCode());
						res.setResponse(MSGConstants.SUCCESS);
						res.setErrorMsg(MSGConstants.SUCCESS_MSG);
						res.setSetNo(String.valueOf(setNo));
						res.setScrollNo(String.valueOf(scrollNo));
						res.setRrn(rrn);
						response = null;
						return res;
					} else {
						session.close();
						session = null;
						t = null;
						res.setResponse(MSGConstants.ERROR);
						res.setErrorMsg(MSGConstants.TRANSACTION_FAILURE);
						return res;
					}
				}
				Date opdate = DataUtils.getOpenDate(Integer.valueOf(sourceAcc.substring(0, 3)));
				logger.error("<<<<<<<<<<<::: Other branch deposit Transaction :::>>>>>>>>>>>>>>");
				int setNo = VoucherMPOS.getNextSetNo();
				int scrollNo = VoucherMPOS.getNextScrollNo();
				int reconNo = VoucherMPOS.getNextReconNo(Integer.valueOf(sourceAcc.substring(0, 3).trim()));

				int setNoABB = VoucherMPOS.getNextSetNo();
				int scrollNoAbb = VoucherMPOS.getNextScrollNo();
				int scrollNoAbb1 = VoucherMPOS.getNextScrollNo();
				int scrollNoAbb2 = VoucherMPOS.getNextScrollNo();

				logger.error("reconNo::>>" + reconNo);

				int benSetNo = VoucherMPOS.getNextSetNo();
				int benScrollNo = VoucherMPOS.getNextScrollNo();
				int scrollNoNew = VoucherMPOS.getNextScrollNo();
				String perticulars = "ReconNo =" + reconNo + " FromBrCode ="
						+ Integer.valueOf(sourceAcc.substring(0, 3).trim()) + " AcctId = "
						+ destAccount.getId().getPrdAcctId().substring(0, 24) + "";
				logger.error("perticulars::>>" + perticulars);
				// TransactionValidationResponse
				// resp2=VoucherMPOS.creditABB(""+MSGConstants.ABB_ACC,
				// Integer.valueOf(sourceAcc.substring(0,3).trim()),
				// Double.valueOf(amount), perticulars,
				// "MPOS",setNo,scrollNo,scrollNo, rrn,reconNo,"ABB");
				// TransactionValidationResponse
				// resp3=VoucherMPOS.debitABB(""+MSGConstants.ABB_ACC,
				// Integer.valueOf(destAcc.substring(0,3).trim()),
				// Double.valueOf(amount),perticulars,
				// MSGConstants.ABBMPOS,benSetNo,scrollNoNew,scrollNoNew,
				// rrn,reconNo,"DEPOSIT");
				// TransactionValidationResponse
				// resp1=VoucherMPOS.creditBranch(""+destAccount.getId().getPrdAcctId(),
				// destAccount.getId().getLbrCode(),
				// Double.parseDouble(amount.trim()),
				// "FromBrcode="+Integer.valueOf(sourceAcc.substring(0,3).trim())+"/"+MSGConstants.MPOS_CASH_DEPOSIT,
				// MSGConstants.ABBMPOS, benSetNo,benScrollNo,benScrollNo,rrn);

				TransactionValidationResponse resp2 = VoucherMPOS.creditABB("" + MSGConstants.ABB_ACC,
						Integer.valueOf(sourceAcc.substring(0, 3).trim()), Double.valueOf(amount), perticulars, "MPOS",
						setNo, scrollNoAbb, scrollNoAbb, rrn, reconNo, "ABB", session);
				TransactionValidationResponse resp3 = VoucherMPOS.debitABB("" + MSGConstants.ABB_ACC,
						Integer.valueOf(destAcc.substring(0, 3).trim()), Double.valueOf(amount), perticulars,
						MSGConstants.ABBMPOS, setNoABB, scrollNoAbb2, scrollNoAbb2, rrn, reconNo, "DEPOSIT", session);
				TransactionValidationResponse resp1 = VoucherMPOS.creditBranch("" + destAccount.getId().getPrdAcctId(),
						destAccount.getId().getLbrCode(), Double.parseDouble(amount.trim()),
						"FromBrcode=" + Integer.valueOf(sourceAcc.substring(0, 3).trim()) + "/"
								+ MSGConstants.MPOS_CASH_DEPOSIT,
						MSGConstants.ABBMPOS, setNoABB, scrollNoAbb1, scrollNoAbb1, rrn, session);

				VoucherMPOS.updateBalanceABB(Double.valueOf(amount.trim()),
						Integer.valueOf(sourceAcc.substring(0, 3).trim()), "C", "ABB");
				VoucherMPOS.updateBalanceABB(Double.valueOf(amount.trim()),
						Integer.valueOf(destAcc.substring(0, 3).trim()), "D", "ABB");
				VoucherMPOS.updateBalanceABB(Double.valueOf(amount.trim()), destAccount.getId().getLbrCode(), "C",
						"" + destAccount.getId().getPrdAcctId().substring(0, 8).trim());
				if (resp1.getResponse().trim().equalsIgnoreCase(MSGConstants.SUCCESS)
						&& resp2.getResponse().trim().equalsIgnoreCase(MSGConstants.SUCCESS)
						&& resp3.getResponse().trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
					String batchCodes[] = Props.getBatchProperty("MPOS").split("~");
					D001004 d04OnlineBatchName = VoucherMPOS.getBatchNameFromBatchCode(batchCodes[0]);
					String onlineBatchName = d04OnlineBatchName.getValue().trim();
					String batchCodes1[] = Props.getBatchProperty(MSGConstants.ABBMPOS).split("~");
					D001004 d001004 = VoucherMPOS.getBatchNameFromBatchCode(batchCodes1[0]);
					String benBatchCode = d001004.getValue().trim();
					logger.error("sourceAcc::>>" + sourceAcc);
					logger.error("DestACc::>>" + destAcc);
					logger.error("Amount::>>>" + amount);
					logger.error("reconNo::>>" + reconNo);
					logger.error("Opdate::>>>" + opdate);
					logger.error("onlineBatchName::>>" + onlineBatchName);
					logger.error("benBatchCode::>>" + benBatchCode);
					logger.error("setNo::>>" + setNo);
					logger.error("scrollNo::>>" + scrollNo);
					logger.error("benSetNo:>>" + benSetNo);
					logger.error("benScrollNo::>>" + benScrollNo);
					// logger.error("sourceAccount.getId().getPrdAcctId()::>>"+sourceAccount.getId().getPrdAcctId());
					// D100001
					// d100001=VoucherMPOS.prepareReconObj(Integer.valueOf(sourceAcc.substring(0,3)),
					// reconNo, opdate, 999999, onlineBatchName, benBatchCode,
					// setNo, scrollNo, benSetNo, scrollNoNew,
					// scrollNoNew,Integer.valueOf(destAcc.substring(0,3)),""+destAccount.getId().getPrdAcctId()
					// ,Double.valueOf(amount),"C");
					D100001 d100001 = VoucherMPOS.prepareReconObjBranch(Integer.valueOf(sourceAcc.substring(0, 3)),
							reconNo, opdate, 999999, onlineBatchName, benBatchCode, setNo, scrollNoAbb, setNoABB,
							scrollNoAbb1, scrollNoAbb2, Integer.valueOf(destAcc.substring(0, 3)),
							"" + destAccount.getId().getPrdAcctId(), Double.valueOf(amount), "C");
					logger.error("d100001::>>>" + d100001);

					D100002 d100002 = VoucherMPOS.prepareRecon2ObjBranch(Integer.valueOf(sourceAcc.substring(0, 3)),
							reconNo, opdate, 999999, onlineBatchName, benBatchCode, setNo, scrollNoAbb, setNoABB,
							scrollNoAbb1, scrollNoAbb2, Integer.valueOf(destAcc.substring(0, 3)),
							"" + destAccount.getId().getPrdAcctId(), Double.valueOf(amount), "C");
					try {
						session.save(d100001);
						session.save(d100002);
						D350078 d350078 = DataUtils.getMobNo(sourceAccount.getCustNo() + "", session);
						session.close();
						session = null;
						res.setMobNo(d350078 != null ? d350078.getId().getMobileNo() : "0");
						t.commit();
						t = null;
					} catch (Exception e) {
						e.printStackTrace();
					}
					String value = (String.valueOf(setNo) + "~" + String.valueOf(scrollNo) + "~" + resp1.getBatchCode()
							+ "~0");
					res.setOutput(value);
					res.setBatchCode(resp1.getBatchCode());
					res.setResponse(MSGConstants.SUCCESS);
					res.setErrorMsg(MSGConstants.SUCCESS_MSG);
					res.setSetNo(String.valueOf(setNo));
					res.setScrollNo(String.valueOf(scrollNo));
					res.setRrn(rrn);
					resp1 = null;
					resp2 = null;
					resp3 = null;
					t.commit();
					session.close();
					session = null;
					t = null;
					return res;
				} else {
					t.commit();
					session.close();
					session = null;
					t = null;
					res.setResponse(MSGConstants.ERROR);
					res.setErrorMsg(MSGConstants.TRANSACTION_FAILURE);
					return res;
				}
			} else if (custType.equalsIgnoreCase(MSGConstants.BUSINESS_CORRESPONDANCE)) {
				int setNo = VoucherMPOS.getNextSetNo();
				int scrollNo = VoucherMPOS.getNextScrollNo();
				TransactionValidationResponse response = new VoucherMPOS().drCrMerchantOrBC(
						sourceAccount.getId().getPrdAcctId(), sourceAccount.getId().getLbrCode(),
						destAccount.getId().getPrdAcctId(), destAccount.getId().getLbrCode(), Double.valueOf(amount),
						MSGConstants.MPOS_CASH_DEPOSIT, "MPOS", setNo, scrollNo, VoucherMPOS.getNextScrollNo(), rrn);
				// logger.error("Balance::>>"+CoreTransactionMPOS.balance(sourceAccount.getId().getLbrCode(),sourceAccount.getId().getPrdAcctId(),
				// Double.parseDouble(amount), "D"));
				// logger.error("Balance::>>"+CoreTransactionMPOS.balance(destAccount.getId().getLbrCode(),destAccount.getId().getPrdAcctId(),
				// Double.parseDouble(amount), "C"));

				if (response.getResponse().trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
					String bal = CoreTransactionMPOS.balance(sourceAccount.getId().getLbrCode(),
							sourceAccount.getId().getPrdAcctId(), Double.parseDouble(amount), "D");
					logger.error("Balance::>>" + bal);
					logger.error("Balance::>>" + CoreTransactionMPOS.balance(destAccount.getId().getLbrCode(),
							destAccount.getId().getPrdAcctId(), Double.parseDouble(amount), "C"));
					D350078 d350078 = DataUtils.getMobNo(sourceAccount.getCustNo() + "", session);
					session.close();
					session = null;
					res.setMobNo(d350078 != null ? d350078.getId().getMobileNo() : "0");
					String value = (String.valueOf(setNo) + "~" + String.valueOf(scrollNo) + "~"
							+ response.getBatchCode() + "~" + bal);
					res.setOutput(value);
					res.setBatchCode(response.getBatchCode());
					res.setResponse(MSGConstants.SUCCESS);
					res.setErrorMsg(MSGConstants.SUCCESS_MSG);
					res.setSetNo(String.valueOf(setNo));
					res.setScrollNo(String.valueOf(scrollNo));
					res.setRrn(rrn);
					response = null;
					return res;
				} else {
					res.setResponse(MSGConstants.ERROR);
					res.setErrorMsg(MSGConstants.TRANSACTION_FAILURE);
					return res;
				}
			} else if (custType.equalsIgnoreCase(MSGConstants.MERCHANT)) {
				int setNo = VoucherMPOS.getNextSetNo();
				int scrollNo = VoucherMPOS.getNextScrollNo();
				TransactionValidationResponse response = new VoucherMPOS().drCrMerchantOrBC(
						sourceAccount.getId().getPrdAcctId(), sourceAccount.getId().getLbrCode(),
						destAccount.getId().getPrdAcctId(), destAccount.getId().getLbrCode(), Double.valueOf(amount),
						MSGConstants.MPOS_CASH_DEPOSIT, "MPOS", setNo, scrollNo, VoucherMPOS.getNextScrollNo(), rrn);

				if (response.getResponse().trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
					String bal = CoreTransactionMPOS.balance(sourceAccount.getId().getLbrCode(),
							sourceAccount.getId().getPrdAcctId(), Double.parseDouble(amount), "D");
					logger.error("Balance::>>" + bal);
					logger.error("Balance::>>" + CoreTransactionMPOS.balance(destAccount.getId().getLbrCode(),
							destAccount.getId().getPrdAcctId(), Double.parseDouble(amount), "C"));

					D350078 d350078 = DataUtils.getMobNo(sourceAccount.getCustNo() + "", session);
					session.close();
					session = null;
					res.setMobNo(d350078 != null ? d350078.getId().getMobileNo() : "0");

					String value = (String.valueOf(setNo) + "~" + String.valueOf(scrollNo) + "~"
							+ response.getBatchCode() + "~" + bal);
					res.setOutput(value);
					res.setBatchCode(response.getBatchCode());
					res.setResponse(MSGConstants.SUCCESS);
					res.setErrorMsg(MSGConstants.SUCCESS_MSG);
					res.setSetNo(String.valueOf(setNo));
					res.setScrollNo(String.valueOf(scrollNo));
					res.setRrn(rrn);
					logger.error("Value::>>" + value);
					response = null;
					return res;
				} else {
					res.setResponse(MSGConstants.ERROR);
					res.setErrorMsg(MSGConstants.TRANSACTION_FAILURE);
					res.setRrn(rrn);
					return res;
				}
			} else if (custType.trim().equalsIgnoreCase(MSGConstants.PIG_ME_DEPOSIT)) {
				int setNo = VoucherMPOS.getNextSetNo();
				int scrollNo = VoucherMPOS.getNextScrollNo();
				try {
					/*Criteria criteria1 = session.createCriteria(D047003.class);
					criteria1.add(Restrictions.eq("id.lbrCode", destAccount.getId().getLbrCode()));
					criteria1.add(Restrictions.eq("id.custPrdAcctId", destAccount.getId().getPrdAcctId()));
					List<D047003> list = criteria1.list();
					if (list != null && list.size() > 0) {
						if (Double.valueOf(amount) % list.get(0).getDepositAmt() != 0) {
							res.setResponse(MSGConstants.ERROR);
							res.setErrorMsg(MSGConstants.INVALID_AMOUNT_NEW);
							return res;
						}
					}*/
					if (forceTransactionYn.equalsIgnoreCase(MSGConstants.YES)) {
						TransactionValidationResponse response = VoucherMPOS.otherBranchVouchers(
								sourceAccount.getId().getLbrCode(), sourceAccount.getId().getPrdAcctId(),
								destAccount.getId().getLbrCode(), destAccount.getId().getPrdAcctId(), "PIGMEDEPOSIT",
								MSGConstants.MPOS_PIGME_DEPOSIT, Double.valueOf(amount.trim()), rrn);
						if (response.getResponse().trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
							String value = (response.getSetNo() + "~" + response.getScrollNo() + "~"
									+ response.getBatchCode() + "~" + response.getBalance());
							res.setOutput(value);
							res.setBatchCode(response.getBatchCode());
							res.setResponse(MSGConstants.SUCCESS);
							res.setErrorMsg(MSGConstants.SUCCESS_MSG);
							res.setSetNo(String.valueOf(setNo));
							res.setScrollNo(String.valueOf(scrollNo));
							res.setName(destAccount.getLongName().trim());
							res.setRrn(rrn);
							DataUtils.getAgencyBankiTrnObj(amount, MSGConstants.SUCCESFUL_TRN,
									destAccount.getLongName(), sourceAcc, ResponseCodes.SUCCESS, rrn, destAcc, ownerId,
									deviceId);
							response = null;
							return res;
						} else {
							res.setResponse(MSGConstants.ERROR);
							//res.setErrorMsg(MSGConstants.TRANSACTION_FAILURE);
							logger.error(response.getErrorMsg());
							res.setErrorMsg(response.getErrorMsg());
							res.setName(destAccount.getLongName().trim());
							DataUtils.getAgencyBankiTrnObj(amount, response.getErrorMsg(),
									destAccount.getLongName(), sourceAcc, ResponseCodes.SYSTEM_ERROR, rrn, destAcc,
									ownerId, deviceId);
							return res;
						}
					} else {
						if (DataUtils.checkAgencyBankingTrn(sourceAcc, destAcc, amount)) {
							res.setResponse(MSGConstants.ERROR);
							res.setErrorMsg(MSGConstants.ALREADY_DONE_THIS_TRANSACTION);
							res.setRespCode(ResponseCodes.ALREADY_DONE_THIS_TRANSACTION);
							res.setName(destAccount.getLongName().trim());
//							DataUtils.getAgencyBankiTrnObj(amount, MSGConstants.ALREADY_DONE_THIS_TRANSACTION,destAccount.getLongName(), sourceAcc, ResponseCodes.ALREADY_DONE_THIS_TRANSACTION, rrn, destAcc);
							return res;
						}
						TransactionValidationResponse response = VoucherMPOS.otherBranchVouchers(
								sourceAccount.getId().getLbrCode(), sourceAccount.getId().getPrdAcctId(),
								destAccount.getId().getLbrCode(), destAccount.getId().getPrdAcctId(), "PIGMEDEPOSIT",
								MSGConstants.MPOS_PIGME_DEPOSIT, Double.valueOf(amount.trim()), rrn);
						if (response.getResponse().trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
							String value = (response.getSetNo() + "~" + response.getScrollNo() + "~"
									+ response.getBatchCode() + "~" + response.getBalance());
							res.setOutput(value);
							res.setBatchCode(response.getBatchCode());
							res.setResponse(MSGConstants.SUCCESS);
							res.setErrorMsg(MSGConstants.SUCCESS_MSG);
							res.setSetNo(String.valueOf(setNo));
							res.setScrollNo(String.valueOf(scrollNo));
							res.setRrn(rrn);
							res.setName(destAccount.getLongName().trim());
							DataUtils.getAgencyBankiTrnObj(amount, MSGConstants.SUCCESFUL_TRN,
									destAccount.getLongName(), sourceAcc, ResponseCodes.SUCCESS, rrn, destAcc, ownerId,
									deviceId);
							response = null;
							return res;
						} else {
							res.setResponse(MSGConstants.ERROR);
							logger.error(response.getErrorMsg());
							res.setErrorMsg(response.getErrorMsg());
							res.setName(destAccount.getLongName().trim());
							DataUtils.getAgencyBankiTrnObj(amount, res.getErrorMsg(),
									destAccount.getLongName(), sourceAcc, ResponseCodes.SYSTEM_ERROR, rrn, destAcc,
									ownerId, deviceId);
							return res;
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e);
					res.setResponse(MSGConstants.ERROR);
					res.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
					res.setRrn(rrn);
					return res;
				} finally {
					if (session != null && session.isOpen())
						session.close();
					session = null;
				}
			} else if (custType.trim().equalsIgnoreCase(MSGConstants.LOAN_ACC)) {
				// int setNo=VoucherMPOS.getNextSetNo();
				// int scrollNo=VoucherMPOS.getNextScrollNo();
				try {
					HashMap<String, String> response = VoucherCommon.otherBranchLoanVouchers(
							sourceAccount.getId().getLbrCode(), sourceAccount.getId().getPrdAcctId(),
							destAccount.getId().getLbrCode(), destAccount.getId().getPrdAcctId(),
							MSGConstants.PIGMEDEPOSIT, "MPOS", Double.valueOf(amount), rrn, destAcc);
					if (response.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
						String value = (response.get(Code.SETNO) + "~" + response.get(Code.SCROLLNO) + "~MPOS~" + "0");
						res.setOutput(value);
						res.setBatchCode("MPOS");
						res.setResponse(MSGConstants.SUCCESS);
						res.setErrorMsg(MSGConstants.SUCCESS_MSG);
						res.setSetNo(String.valueOf(response.get(Code.SETNO)));
						res.setScrollNo(String.valueOf(response.get(Code.SETNO)));
						res.setRrn(rrn);
						res.setBalance(response.get("MainBalance"));
						response = null;
						return res;
					} else {
						res.setResponse(MSGConstants.ERROR);
						if(response.get("ErrorMsg")!=null && !"".equalsIgnoreCase(response.get("ErrorMsg").trim()))
							res.setErrorMsg(response.get("ErrorMsg").trim());
						else
							res.setErrorMsg(MSGConstants.TRANSACTION_FAILURE);
						return res;
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e);
					res.setResponse(MSGConstants.ERROR);
					res.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
					res.setRrn(rrn);
					return res;
				} finally {
					if (session != null && session.isOpen())
						session.close();
					session = null;
				}

			}
			res.setResponse(MSGConstants.ERROR);
			res.setErrorMsg(MSGConstants.INVALID_TRANSACTION_TYPE);
			res.setRrn(rrn);
			return res;
		}
		res.setResponse(MSGConstants.ERROR);
		res.setErrorMsg(MSGConstants.INVALID_TRANSACTION_TYPE);
		res.setRrn(rrn);
		return res;
	}

	public static TransactionValidationResponse validateAccount(D009022 sourceAccount, String amount, String drCr) {
		TransactionValidationResponse response = new TransactionValidationResponse();
		if (drCr.equalsIgnoreCase("D")) {
			if (sourceAccount == null) {
				logger.error("source Account is ::>>" + sourceAccount);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
				response.setRespCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				return response;
			}
			logger.error("Account Status::>>" + sourceAccount.getAcctStat());
			response.setName(sourceAccount.getLongName().trim());
			if (Integer.valueOf(sourceAccount.getAcctStat()) == 3) {
				logger.error("ACCOUNT_CLOSED");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.REM_ACCOUNT_CLOSED);
				response.setRespCode(ResponseCodes.CLOSED_ACCOUNT);
				return response;
			}
			if (Integer.valueOf(sourceAccount.getAcctStat()) == 4) {
				logger.error("ACCOUNT_INOPERATIVE");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.REM_ACCOUNT_INOPERATIVE);
				response.setRespCode(ResponseCodes.CLOSED_ACCOUNT);
				return response;
			}
			if (Integer.valueOf(sourceAccount.getAcctStat()) == 5) {
				logger.error("ACCOUNT_INOPERATIVE");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.REM_ACCOUNT_DORMANT);
				response.setRespCode(ResponseCodes.CLOSED_ACCOUNT);
				return response;
			}
			if (Integer.valueOf(sourceAccount.getAcctStat()) == 11) {
				logger.error("ACCOUNT_INOPERATIVE");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.REM_NPA_ACC_NOT_ALLOWED);
				response.setRespCode(ResponseCodes.CLOSED_ACCOUNT);
				return response;
			}
			logger.error("Freez type::>>" + sourceAccount.getFreezeType());

			if (drCr.equalsIgnoreCase("D")) {
				if (Integer.valueOf(sourceAccount.getFreezeType()) == 2) {
					logger.error("ACCOUNT_FREEZED");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.REM_ACCOUNT_DR_FREEZED);
					response.setRespCode(ResponseCodes.ACCOUNT_FREEZED);
					return response;
				}
			}
			if (drCr.equalsIgnoreCase("C")) {
				if (Integer.valueOf(sourceAccount.getFreezeType()) == 3) {
					logger.error("ACCOUNT_FREEZED");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.BEN_ACCOUNT_CR_FREEZED);
					response.setRespCode(ResponseCodes.ACCOUNT_FREEZED);
					return response;
				}
			}
			if (Integer.valueOf(sourceAccount.getFreezeType()) == 4) {
				logger.error("ACCOUNT_TOTAL_FREEZED");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.REM_ACCOUNT_TOTAL_FREEZED);
				response.setRespCode(ResponseCodes.ACCOUNT_FREEZED);
				return response;
			}
			if (Integer.valueOf(sourceAccount.getFreezeType()) == 5) {
				logger.error("ACCOUNT_special_FREEZED");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.REM_ACCOUNT_SPECIAL_FREEZED);
				response.setRespCode(ResponseCodes.ACCOUNT_FREEZED);
				return response;
			}
			if (Integer.valueOf(sourceAccount.getDbtrAuthNeeded()) != 0
					&& Integer.valueOf(sourceAccount.getDbtrAuthDone()) != 1) {
				logger.error("ACCOUNT_UNAUTHERISED");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.REM_ACCOUNT_UNAUTHERISED);
				response.setRespCode(ResponseCodes.ACCOUNT_UNAUTHRISED);
				return response;
			}
			/*
			 * logger.error("Before address authentication"); D010054 d010054 =
			 * DataUtils.getAccountAddress(sourceAccount.getId().getLbrCode(),
			 * sourceAccount.getId().getPrdAcctId()); logger.error("d010054::>>" +
			 * d010054); if (d010054 == null) { response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.ACCOUNT_ADDRESS_NOT_FOUND); return
			 * response; } if (d010054 != null) { if
			 * (Integer.valueOf(d010054.getDbtrAuthNeeded()) != 0 &&
			 * Integer.valueOf(d010054.getDbtrAuthDone()) != 1) {
			 * logger.error("ACCOUNT_ADDRESS_UNAUTHERISED");
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.REM_ACCOUNT_ADDRESS_UNAUTHERISED);
			 * response.setRespCode(ResponseCodes.ACCOUNT_ADDR_UNAUTHORISED); return
			 * response; } }
			 */
			if (sourceAccount.getAcctType() == Byte.valueOf("6")) {
				logger.error("NRE_ACCOUNT");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NRE_ACCOUNT);
				response.setRespCode(ResponseCodes.NRE_ACCOUNT);
				return response;
			}
			if (sourceAccount.getAcctType() == Byte.valueOf("7")) {
				logger.error("NRO_ACCOUNT");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NRO_ACCOUNT);
				response.setRespCode(ResponseCodes.NRE_ACCOUNT);
				return response;
			}

			if (sourceAccount.getAcctStat() == Byte.valueOf("9")) {
				logger.error("ACCOUNT_FREEZED");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.REM_ACCOUNT_OD_CALLED_BACK);
				response.setRespCode(ResponseCodes.ACC_STAT_CALLED_BACK);
				return response;
			}
			if (!new ArrayList<Integer>(Arrays.asList(1, 2, 12))
					.contains(Integer.valueOf(sourceAccount.getAcctStat()))) {
				logger.error("ACCOUNT_STATUS_NOT_NORMAL");
				response.setResponse(MSGConstants.ERROR);
				response.setRespCode(ResponseCodes.CLOSED_ACCOUNT);
				response.setErrorMsg(MSGConstants.ACCOUNT_NOT_NORMAL);
				return response;
			}
			if (drCr.equalsIgnoreCase("D")) {
				D001004 systemParam = DataUtils.getSystemParameter(0, ConfigurationLoader.getParameters(false).getProperty("IMPS_SKIP_DDS_PRD"));
				if(systemParam!=null) {
					String[] skipPrds = systemParam.getValue().trim().split(",");
					ArrayList<String> skipList = new ArrayList<String>(Arrays.asList(skipPrds));
					logger.error("Skip Product List:-"+ skipList.toString());
					try {
						String allowType = DataUtils.getSystemParameter(0, ConfigurationLoader.getParameters(false).getProperty("IMPS_DDS_P2A_DCB")).getValue();
						if (("D".equalsIgnoreCase(allowType.trim()) || "B".equalsIgnoreCase(allowType.trim()))
								&& skipList.contains(sourceAccount.getId().getPrdAcctId().substring(0, 8).trim()))
						{
							logger.error("Transaction Not Allow for this type of accounts.");
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMsg("Transaction Not Allow for this type of accounts.");
							response.setRespCode(ResponseCodes.INSUFFICIENT_FUNDS);
							return response;
						}
					}catch (Exception e) {
						// TODO: handle exception
						logger.error("Exception:-"+e);
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMsg("Technical Error Occured");
						response.setRespCode(ResponseCodes.EXCEPTION_OCCURED);
						return response;
					}
				}
				String isOdAcc = "";
				ArrayList<Double> lists = new ArrayList<>();
				lists.add(Double.valueOf(sourceAccount.getActClrBalFcy()));
				lists.add(Double.valueOf(sourceAccount.getActTotBalFcy()));
				lists.add(Double.valueOf(sourceAccount.getActTotBalLcy()));
				lists.add(Double.valueOf(sourceAccount.getShdClrBalFcy()));
				lists.add(Double.valueOf(sourceAccount.getShdTotBalFcy()));
				double availBal = Collections.min(lists) - sourceAccount.getTotalLienFcy();
				double transAmount = Double.parseDouble(amount);
				
				D009021 productMaster = DataUtils.getProductMaster(sourceAccount.getId().getLbrCode() + "",
						sourceAccount.getId().getPrdAcctId().substring(0, 8).trim());
				
				
				if (DataUtils.isOverDraftAccount(sourceAccount.getId().getLbrCode(),
						sourceAccount.getId().getPrdAcctId()))
					isOdAcc = "Y";
				logger.error("isOdAcc::>>" + isOdAcc);
				if (isOdAcc.equalsIgnoreCase("Y")) {
					CustomerDetails res = DataUtils.validateOdAccount(sourceAccount.getId().getLbrCode(),
							sourceAccount.getId().getPrdAcctId());
					logger.error("res.getResponse()::>>" + res.getResponse());
					String ccDpExpGraceYN = DataUtils.getSystemParameter(0, "CCDPEXPGRACEYN").getValue();
					
					
					if (res.getResponse().equalsIgnoreCase(MSGConstants.SUCCESS)) {
						
						availBal = availBal
								+ res.getOdLimit().getTotSancLimit();
	
						logger.error("Available Bal::>>" + availBal);
						logger.error("Transaction Amount::>>>" + transAmount);
						CustomerDetails res1 = new CustomerDetails();
						if("N".equalsIgnoreCase(sourceAccount.getDpYn()+""))
							res1.setResponse(MSGConstants.SUCCESS);
						else {
							
							res1 = DataUtils.validateOdAccountDp(sourceAccount.getId().getLbrCode(),
								sourceAccount.getId().getPrdAcctId());
							if(!res1.getResponse().equalsIgnoreCase(MSGConstants.SUCCESS) && (ccDpExpGraceYN!=null && "Y".equalsIgnoreCase(ccDpExpGraceYN.trim()))) {
								res1 = DataUtils.validateDpExpGrace(sourceAccount.getId().getLbrCode(),	sourceAccount.getId().getPrdAcctId());
							}
						}
						
						if(res1.getResponse().equalsIgnoreCase(MSGConstants.SUCCESS)){
							if (availBal < transAmount) {
								CustomerDetails resp = DataUtils.validateOdAdhoclimit(sourceAccount.getId().getLbrCode(),
										sourceAccount.getId().getPrdAcctId());
								logger.error("resp.getErrorMsg()::>>" + resp.getErrorMsg());
								logger.error("resp.getErrorMsg()::>>" + resp.getResponse());
								if (resp.getResponse().equalsIgnoreCase(MSGConstants.SUCCESS)) {
	//								availBal = Double
	//										.valueOf(Double.valueOf(sourceAccount.getActClrBalFcy())
	//												- Double.valueOf(sourceAccount.getTotalLienFcy()))
	//										+ res.getOdLimit().getTotSancLimit() + resp.getOdAdhocLimit().getTotSancLimit();
									availBal = availBal + resp.getOdAdhocLimit().getTotSancLimit();
									logger.error("Available Bal::>>" + availBal);
									logger.error("Transaction Amount::>>>" + transAmount);
									if (availBal < transAmount) {
										response.setResponse(MSGConstants.ERROR);
										response.setErrorMsg(MSGConstants.INSUFFICIENT_FUNDS);
										response.setRespCode(ResponseCodes.INSUFFICIENT_FUNDS);
										return response;
									}
									response.setResponse(MSGConstants.SUCCESS);
									response.setRespCode(ResponseCodes.SIMSEPAY_SUCCESS);
									response.setErrorMsg(MSGConstants.SUCCESS_MSG);
									return response;
								}
								response.setResponse(MSGConstants.ERROR);
								response.setErrorMsg(resp.getErrorMsg());
								return response;
							}
						}else {
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMsg(res1.getErrorMsg());
							return response;
						}
						logger.error("INSUFFICIENT_FUNDS");
						response.setResponse(MSGConstants.SUCCESS);
						response.setErrorMsg(MSGConstants.SUCCESS_MSG);
						return response;
					}
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(res.getErrorMsg());
					return response;
				}else {
//			---------Commented Below Code----------------
				
//				double availBal = Double.valueOf(Double.valueOf(sourceAccount.getActClrBalFcy())
//						- Double.valueOf(sourceAccount.getTotalLienFcy()));
					availBal = availBal - getMinBal(sourceAccount, productMaster);
				}
				logger.error("Available Bal::>>" + availBal + " Transaction Amount::>>>" + transAmount);
				
				if (availBal < transAmount) {
					logger.error("INSUFFICIENT_FUNDS");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.INSUFFICIENT_FUNDS);
					response.setRespCode(ResponseCodes.INSUFFICIENT_FUNDS);
					return response;
				}
				
			}
		} else if (drCr.equalsIgnoreCase("C")) {
			
			D001004 systemParam = DataUtils.getSystemParameter(0, ConfigurationLoader.getParameters(false).getProperty("IMPS_SKIP_DDS_PRD"));
			if(systemParam!=null) {
				String[] skipPrds = systemParam.getValue().trim().split(",");
				ArrayList<String> skipList = new ArrayList<String>(Arrays.asList(skipPrds));
				logger.error("Skip Product List:-"+ skipList.toString());
				try {
					String allowType = DataUtils.getSystemParameter(0, ConfigurationLoader.getParameters(false).getProperty("IMPS_DDS_P2A_DCB")).getValue();
					if (("C".equalsIgnoreCase(allowType.trim()) || "B".equalsIgnoreCase(allowType.trim()))
							&& skipList.contains(sourceAccount.getId().getPrdAcctId().substring(0, 8).trim()))
					{
						logger.error("Transaction Not Allow for this type of accounts.");
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMsg("Transaction Not Allow for this type of accounts.");
						response.setRespCode(ResponseCodes.INSUFFICIENT_FUNDS);
						return response;
					}
				}catch (Exception e) {
					// TODO: handle exception
					logger.error("Exception:-"+e);
				}
			}
			
			if (sourceAccount == null) {
				logger.error("source Account is ::>>" + sourceAccount);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
				response.setRespCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				return response;
			}
			logger.error("Account Status::>>" + sourceAccount.getAcctStat());
			response.setName(sourceAccount.getLongName().trim());
			if (Integer.valueOf(sourceAccount.getAcctStat()) == 3) {
				logger.error("ACCOUNT_CLOSED");
				response.setResponse(MSGConstants.ERROR);
				response.setRespCode(ResponseCodes.CLOSED_ACCOUNT);
				response.setErrorMsg(MSGConstants.BEN_ACCOUNT_CLOSED);
				return response;
			}

			if (Integer.valueOf(sourceAccount.getAcctStat()) == 4) {
				logger.error("ACCOUNT_INOPERATIVE");
				response.setResponse(MSGConstants.ERROR);
				response.setRespCode(ResponseCodes.CLOSED_ACCOUNT);
				response.setErrorMsg(MSGConstants.BEN_ACCOUNT_INOPERATIVE);
				return response;
			}
			if (Integer.valueOf(sourceAccount.getAcctStat()) == 5) {
				logger.error("ACCOUNT_INOPERATIVE");
				response.setResponse(MSGConstants.ERROR);
				response.setRespCode(ResponseCodes.CLOSED_ACCOUNT);
				response.setErrorMsg(MSGConstants.BEN_ACCOUNT_DORMANT);
				return response;
			}
			if (Integer.valueOf(sourceAccount.getAcctStat()) == 11) {
				logger.error("ACCOUNT_INOPERATIVE");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.BEN_NPA_ACC_NOT_ALLOWED);
				response.setRespCode(ResponseCodes.CLOSED_ACCOUNT);
				return response;
			}
			if (sourceAccount.getAcctStat() == Byte.valueOf("9")) {
				logger.error("ACCOUNT_FREEZED");
				response.setResponse(MSGConstants.ERROR);
				response.setRespCode(ResponseCodes.NRE_ACCOUNT);
				response.setErrorMsg(MSGConstants.BEN_ACCOUNT_OD_CALLED_BACK);
				return response;
			}
			if (!new ArrayList<Integer>(Arrays.asList(1, 2, 12))
					.contains(Integer.valueOf(sourceAccount.getAcctStat()))) {
				logger.error("ACCOUNT_STATUS_NOT_NORMAL");
				response.setResponse(MSGConstants.ERROR);
				response.setRespCode(ResponseCodes.CLOSED_ACCOUNT);
				response.setErrorMsg(MSGConstants.ACCOUNT_NOT_NORMAL);
				return response;
			}
			logger.error("Freez type::>>" + sourceAccount.getFreezeType());
			if (drCr.equalsIgnoreCase("D")) {
				if (Integer.valueOf(sourceAccount.getFreezeType()) == 2) {
					logger.error("ACCOUNT_FREEZED");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.REM_ACCOUNT_DR_FREEZED);
					return response;
				}
			}
			if (drCr.equalsIgnoreCase("C")) {
				if (Integer.valueOf(sourceAccount.getFreezeType()) == 3) {
					logger.error("ACCOUNT_FREEZED");
					response.setResponse(MSGConstants.ERROR);
					response.setRespCode(ResponseCodes.ACCOUNT_FREEZED);
					response.setErrorMsg(MSGConstants.BEN_ACCOUNT_CR_FREEZED);
					return response;
				}
				
			}
			if (Integer.valueOf(sourceAccount.getFreezeType()) == 4) {
				logger.error("ACCOUNT_TOTAL_FREEZED");
				response.setResponse(MSGConstants.ERROR);
				response.setRespCode(ResponseCodes.ACCOUNT_FREEZED);
				response.setErrorMsg(MSGConstants.BEN_ACCOUNT_TOTAL_FREEZED);
				return response;
			}
			if (Integer.valueOf(sourceAccount.getFreezeType()) == 5) {
				logger.error("ACCOUNT_TOTAL_FREEZED");
				response.setResponse(MSGConstants.ERROR);
				response.setRespCode(ResponseCodes.ACCOUNT_FREEZED);
				response.setErrorMsg(MSGConstants.BEN_ACCOUNT_TOTAL_FREEZED);
				return response;
			}
			if (Integer.valueOf(sourceAccount.getDbtrAuthNeeded()) != 0
					&& Integer.valueOf(sourceAccount.getDbtrAuthDone()) != 1) {
				logger.error("ACCOUNT_UNAUTHERISED");
				response.setResponse(MSGConstants.ERROR);
				response.setRespCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				response.setErrorMsg(MSGConstants.BEN_ACCOUNT_UNAUTHERISED);
				return response;
			}
			if (sourceAccount.getAcctStat() == Byte.valueOf("9")) {
				logger.error(MSGConstants.REM_ACCOUNT_OD_CALLED_BACK);
				response.setResponse(MSGConstants.ERROR);
				response.setRespCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				response.setErrorMsg(MSGConstants.REM_ACCOUNT_OD_CALLED_BACK);
				return response;
			}
			logger.error("Before address authentication");
			// D010054
			// d010054=DataUtils.getAccountAddress(sourceAccount.getId().getLbrCode(),
			// sourceAccount.getId().getPrdAcctId());
			// logger.error("d010054::>>"+d010054);
			/*
			 * if(d010054==null) { response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.ACCOUNT_ADDRESS_NOT_FOUND); return
			 * response; }
			 */
			/*
			 * if(d010054!=null) { if(Integer.valueOf(d010054.getDbtrAuthNeeded())!=0 &&
			 * Integer.valueOf(d010054.getDbtrAuthDone())!=1) {
			 * logger.error("ACCOUNT_ADDRESS_UNAUTHERISED");
			 * response.setResponse(MSGConstants.ERROR); response.setErrorMsg(MSGConstants.
			 * BEN_ACCOUNT_ADDRESS_UNAUTHERISED); return response; } }
			 */
			if (sourceAccount.getAcctType() == Byte.valueOf("6")) {
				logger.error("NRE_ACCOUNT");
				response.setResponse(MSGConstants.ERROR);
				response.setRespCode(ResponseCodes.NRE_ACCOUNT);
				response.setErrorMsg(MSGConstants.NRE_ACCOUNT);
				return response;
			}
			if (sourceAccount.getAcctType() == Byte.valueOf("7")) {
				logger.error("NRE_ACCOUNT");
				response.setResponse(MSGConstants.ERROR);
				response.setRespCode(ResponseCodes.NRE_ACCOUNT);
				response.setErrorMsg(MSGConstants.NRO_ACCOUNT);
				return response;
			}
			D009021 d009021 = DataUtils.getProductMaster(sourceAccount.getId().getLbrCode() + "",
					sourceAccount.getId().getPrdAcctId().substring(0, 8).trim());
			if (d009021 == null) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.PRODUCT_NOT_FOUND);
				response.setRespCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				return response;
			}
			//Added and Commented by Aniket Desai on 8th Aug, 2019 for Loan Account
			/*if (d009021.getModuleType() == Short.valueOf(30 + "") || d009021.getModuleType() == Short.valueOf(31 + "")
					|| d009021.getModuleType() == Short.valueOf(20 + "")) {*/
			//Added by Aniket Desai on 11th Feb, 2022 for Gold Loan
			D001004 goldLoan = DataUtils.getSystemParameter(0, ConfigurationLoader.getParameters(false).getProperty("Allow_Gold_Loan_Payment"));
			logger.error("Gold Loan::>>");
			if(goldLoan!=null && "Y".equalsIgnoreCase(goldLoan.getValue().trim())) {
				logger.error("Gold Loan::>>Inside "+goldLoan.toString());
				if (d009021.getModuleType() == Short.valueOf(20 + "")) {
					logger.error("Gold Loan::>>Inside-1 "+d009021.getModuleType());
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.FASCILITY_NOT_AVAILABILITY);
					response.setRespCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
					response.setOutput("RD");
					return response;
				}
			}else {
				if (d009021.getModuleType() == Short.valueOf(31 + "")
						|| d009021.getModuleType() == Short.valueOf(20 + "")) {
					logger.error("Gold Loan::>>Inside-1 "+d009021.getModuleType());
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.FASCILITY_NOT_AVAILABILITY);
					response.setRespCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
					response.setOutput("RD");
					return response;
				}
			}

			if (drCr.equalsIgnoreCase("D")) {
				String isOdAcc = "";
				if (DataUtils.isOverDraftAccount(sourceAccount.getId().getLbrCode(),
						sourceAccount.getId().getPrdAcctId()))
					isOdAcc = "Y";
				logger.error("isOdAcc::>>" + isOdAcc);
				if (isOdAcc.equalsIgnoreCase("Y")) {
					CustomerDetails res = DataUtils.validateOdAccount(sourceAccount.getId().getLbrCode(),
							sourceAccount.getId().getPrdAcctId());
					logger.error("res.getResponse()::>>" + res.getResponse());
					if (res.getResponse().equalsIgnoreCase(MSGConstants.SUCCESS)) {
						double transAmount = Double.parseDouble(amount);
						double availBal = Double
								.valueOf(Double.valueOf(sourceAccount.getActClrBalFcy())
										- Double.valueOf(sourceAccount.getTotalLienFcy()))
								+ res.getOdLimit().getTotSancLimit();
						logger.error("Available Bal::>>" + availBal);
						logger.error("Transaction Amount::>>>" + transAmount);
						if (availBal <= transAmount) {
							CustomerDetails resp = DataUtils.validateOdAdhoclimit(sourceAccount.getId().getLbrCode(),
									sourceAccount.getId().getPrdAcctId());
							logger.error("resp.getErrorMsg()::>>" + resp.getErrorMsg());
							logger.error("resp.getErrorMsg()::>>" + resp.getResponse());
							if (resp.getResponse().equalsIgnoreCase(MSGConstants.SUCCESS)) {
								availBal = Double
										.valueOf(Double.valueOf(sourceAccount.getActClrBalFcy())
												- Double.valueOf(sourceAccount.getTotalLienFcy()))
										+ res.getOdLimit().getTotSancLimit() + resp.getOdAdhocLimit().getTotSancLimit();
								logger.error("Available Bal::>>" + availBal);
								logger.error("Transaction Amount::>>>" + transAmount);
								if (availBal <= transAmount) {
									response.setResponse(MSGConstants.ERROR);
									response.setErrorMsg(MSGConstants.INSUFFICIENT_FUNDS);
									return response;
								}
								response.setResponse(MSGConstants.SUCCESS);
								response.setErrorMsg(MSGConstants.SUCCESS_MSG);
								return response;
							}
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMsg(resp.getErrorMsg());
							return response;
						}
						logger.error("INSUFFICIENT_FUNDS");
						response.setResponse(MSGConstants.SUCCESS);
						response.setErrorMsg(MSGConstants.SUCCESS_MSG);
						return response;
					}
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(res.getErrorMsg());
					return response;
				}
				double transAmount = Double.parseDouble(amount);
				double availBal = Double.valueOf(Double.valueOf(sourceAccount.getActClrBalFcy())
						- Double.valueOf(sourceAccount.getTotalLienFcy()));
				logger.error("Available Bal::>>" + availBal);
				logger.error("Transaction Amount::>>>" + transAmount);
				if (availBal < transAmount) {
					logger.error("INSUFFICIENT_FUNDS");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.INSUFFICIENT_FUNDS);
					return response;
				}
			}
		} else if (drCr.equalsIgnoreCase("A")) {
			if (sourceAccount == null) {
				logger.error("source Account is ::>>" + sourceAccount);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
				response.setRespCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				return response;
			}
			logger.error("Account Status::>>" + sourceAccount.getAcctStat());
			response.setName(sourceAccount.getLongName().trim());
			if (Integer.valueOf(sourceAccount.getAcctStat()) == 3) {
				logger.error("ACCOUNT_CLOSED");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_CLOSED);
				response.setRespCode(ResponseCodes.CLOSED_ACCOUNT);
				return response;
			}
			if (Integer.valueOf(sourceAccount.getAcctStat()) == 4) {
				logger.error("ACCOUNT_INOPERATIVE");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_INOPERATIVE);
				response.setRespCode(ResponseCodes.CLOSED_ACCOUNT);
				return response;
			}
			if (Integer.valueOf(sourceAccount.getAcctStat()) == 5) {
				logger.error("ACCOUNT_INOPERATIVE");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_DORMANT);
				response.setRespCode(ResponseCodes.CLOSED_ACCOUNT);
				return response;
			}
			logger.error("Freez type::>>" + sourceAccount.getFreezeType());

			/*
			 * if(drCr.equalsIgnoreCase("D")) {
			 * if(Integer.valueOf(sourceAccount.getFreezeType()) == 2) {
			 * logger.error("ACCOUNT_FREEZED");
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.REM_ACCOUNT_DR_FREEZED);
			 * response.setRespCode(ResponseCodes.ACCOUNT_FREEZED); return response; } }
			 * if(drCr.equalsIgnoreCase("C")) {
			 * if(Integer.valueOf(sourceAccount.getFreezeType()) == 3) {
			 * logger.error("ACCOUNT_FREEZED");
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.BEN_ACCOUNT_CR_FREEZED);
			 * response.setRespCode(ResponseCodes.ACCOUNT_FREEZED); return response; } }
			 */
			if (Integer.valueOf(sourceAccount.getFreezeType()) == 2) {
				logger.error("ACCOUNT_FREEZED");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_DR_FREEZED);
				response.setRespCode(ResponseCodes.ACCOUNT_FREEZED);
				return response;
			}
			if (Integer.valueOf(sourceAccount.getFreezeType()) == 3) {
				logger.error("ACCOUNT_FREEZED");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_CR_FREEZED);
				response.setRespCode(ResponseCodes.ACCOUNT_FREEZED);
				return response;
			}
			if (Integer.valueOf(sourceAccount.getFreezeType()) == 4) {
				logger.error("ACCOUNT_TOTAL_FREEZED");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_TOTAL_FREEZED);
				response.setRespCode(ResponseCodes.ACCOUNT_FREEZED);
				return response;
			}
			if (Integer.valueOf(sourceAccount.getDbtrAuthNeeded()) != 0
					&& Integer.valueOf(sourceAccount.getDbtrAuthDone()) != 1) {
				logger.error("ACCOUNT_UNAUTHERISED");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_UNAUTHERISED);
				response.setRespCode(ResponseCodes.ACCOUNT_UNAUTHRISED);
				return response;
			}
			logger.error("Before address authentication");
			D010054 d010054 = DataUtils.getAccountAddress(sourceAccount.getId().getLbrCode(),
					sourceAccount.getId().getPrdAcctId());
			logger.error("d010054::>>" + d010054);
			if (d010054 == null) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_ADDRESS_NOT_FOUND);
				return response;
			}
			/*if (d010054 != null) {
				if (Integer.valueOf(d010054.getDbtrAuthNeeded()) != 0
						&& Integer.valueOf(d010054.getDbtrAuthDone()) != 1) {
					logger.error("ACCOUNT_ADDRESS_UNAUTHERISED");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.ACCOUNT_ADDRESS_UNAUTHERISED);
					response.setRespCode(ResponseCodes.ACCOUNT_ADDR_UNAUTHORISED);
					return response;
				}
			}*/
			if (sourceAccount.getAcctType() == Byte.valueOf("6")) {
				logger.error("NRE_ACCOUNT");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NRE_ACCOUNT);
				response.setRespCode(ResponseCodes.NRE_ACCOUNT);
				return response;
			}
			if (sourceAccount.getAcctType() == Byte.valueOf("7")) {
				logger.error("NRE_ACCOUNT");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NRO_ACCOUNT);
				response.setRespCode(ResponseCodes.NRE_ACCOUNT);
				return response;
			}
			if (sourceAccount.getAcctStat() == Byte.valueOf("9")) {
				logger.error("ACCOUNT_FREEZED");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.REM_ACCOUNT_OD_CALLED_BACK);
				response.setRespCode(ResponseCodes.ACC_STAT_CALLED_BACK);
				return response;
			}
			if (drCr.equalsIgnoreCase("D")) {
				String isOdAcc = "";
				if (DataUtils.isOverDraftAccount(sourceAccount.getId().getLbrCode(),
						sourceAccount.getId().getPrdAcctId()))
					isOdAcc = "Y";
				logger.error("isOdAcc::>>" + isOdAcc);
				if (isOdAcc.equalsIgnoreCase("Y")) {
					CustomerDetails res = DataUtils.validateOdAccount(sourceAccount.getId().getLbrCode(),
							sourceAccount.getId().getPrdAcctId());
					logger.error("res.getResponse()::>>" + res.getResponse());
					if (res.getResponse().equalsIgnoreCase(MSGConstants.SUCCESS)) {
						double transAmount = Double.parseDouble(amount);
						double availBal = Double
								.valueOf(Double.valueOf(sourceAccount.getActClrBalFcy())
										- Double.valueOf(sourceAccount.getTotalLienFcy()))
								+ res.getOdLimit().getTotSancLimit();
						logger.error("Available Bal::>>" + availBal);
						logger.error("Transaction Amount::>>>" + transAmount);
						if (availBal <= transAmount) {
							CustomerDetails resp = DataUtils.validateOdAdhoclimit(sourceAccount.getId().getLbrCode(),
									sourceAccount.getId().getPrdAcctId());
							logger.error("resp.getErrorMsg()::>>" + resp.getErrorMsg());
							logger.error("resp.getErrorMsg()::>>" + resp.getResponse());
							if (resp.getResponse().equalsIgnoreCase(MSGConstants.SUCCESS)) {
								availBal = Double
										.valueOf(Double.valueOf(sourceAccount.getActClrBalFcy())
												- Double.valueOf(sourceAccount.getTotalLienFcy()))
										+ res.getOdLimit().getTotSancLimit() + resp.getOdAdhocLimit().getTotSancLimit();
								logger.error("Available Bal::>>" + availBal);
								logger.error("Transaction Amount::>>>" + transAmount);
								if (availBal <= transAmount) {
									response.setResponse(MSGConstants.ERROR);
									response.setErrorMsg(MSGConstants.INSUFFICIENT_FUNDS);
									return response;
								}
								response.setResponse(MSGConstants.SUCCESS);
								response.setErrorMsg(MSGConstants.SUCCESS_MSG);
								return response;
							}
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMsg(resp.getErrorMsg());
							return response;
						}
						logger.error("INSUFFICIENT_FUNDS");
						response.setResponse(MSGConstants.SUCCESS);
						response.setErrorMsg(MSGConstants.SUCCESS_MSG);
						return response;
					}
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(res.getErrorMsg());
					return response;
				}
				double transAmount = Double.parseDouble(amount);
				double availBal = Double.valueOf(Double.valueOf(sourceAccount.getActClrBalFcy())
						- Double.valueOf(sourceAccount.getTotalLienFcy()));
				logger.error("Available Bal::>>" + availBal);
				logger.error("Transaction Amount::>>>" + transAmount);
				if (availBal < transAmount) {
					logger.error("INSUFFICIENT_FUNDS");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.INSUFFICIENT_FUNDS);
					return response;
				}
			}
		} else if (drCr.equalsIgnoreCase("AGENT")) {
			if (sourceAccount == null) {
				logger.error("source Account is ::>>" + sourceAccount);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
				response.setRespCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				return response;
			}
			logger.error("Account Status::>>" + sourceAccount.getAcctStat());

			if (Integer.valueOf(sourceAccount.getAcctStat()) == 3) {
				logger.error("ACCOUNT_CLOSED");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.AGENT_ACCOUNT_CLOSED);
				response.setRespCode(ResponseCodes.CLOSED_ACCOUNT);
				return response;
			}
			if (Integer.valueOf(sourceAccount.getAcctStat()) == 4) {
				logger.error("ACCOUNT_INOPERATIVE");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.AGENT_ACCOUNT_INOPERATIVE);
				response.setRespCode(ResponseCodes.CLOSED_ACCOUNT);
				return response;
			}
			if (Integer.valueOf(sourceAccount.getAcctStat()) == 5) {
				logger.error("ACCOUNT_INOPERATIVE");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.AGENT_ACCOUNT_DORMANT);
				response.setRespCode(ResponseCodes.CLOSED_ACCOUNT);
				return response;
			}
			logger.error("Freez type::>>" + sourceAccount.getFreezeType());

			/*
			 * if(drCr.equalsIgnoreCase("D")) {
			 * if(Integer.valueOf(sourceAccount.getFreezeType()) == 2) {
			 * logger.error("ACCOUNT_FREEZED");
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.REM_ACCOUNT_DR_FREEZED);
			 * response.setRespCode(ResponseCodes.ACCOUNT_FREEZED); return response; } }
			 * if(drCr.equalsIgnoreCase("C")) {
			 * if(Integer.valueOf(sourceAccount.getFreezeType()) == 3) {
			 * logger.error("ACCOUNT_FREEZED");
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.BEN_ACCOUNT_CR_FREEZED);
			 * response.setRespCode(ResponseCodes.ACCOUNT_FREEZED); return response; } }
			 */
			if (Integer.valueOf(sourceAccount.getFreezeType()) == 2) {
				logger.error("ACCOUNT_FREEZED");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.AGENT_ACCOUNT_DR_FREEZED);
				response.setRespCode(ResponseCodes.ACCOUNT_FREEZED);
				return response;
			}
			/*
			 * if(Integer.valueOf(sourceAccount.getFreezeType()) == 3) {
			 * logger.error("ACCOUNT_FREEZED");
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.AGENT_ACCOUNT_CR_FREEZED);
			 * response.setRespCode(ResponseCodes.ACCOUNT_FREEZED); return response; }
			 */
			if (Integer.valueOf(sourceAccount.getFreezeType()) == 4) {
				logger.error("ACCOUNT_TOTAL_FREEZED");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.AGENT_ACCOUNT_TOTAL_FREEZED);
				response.setRespCode(ResponseCodes.ACCOUNT_FREEZED);
				return response;
			}
			if (Integer.valueOf(sourceAccount.getDbtrAuthNeeded()) != 0
					&& Integer.valueOf(sourceAccount.getDbtrAuthDone()) != 1) {
				logger.error("ACCOUNT_UNAUTHERISED");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.AGENT_ACCOUNT_UNAUTHERISED);
				response.setRespCode(ResponseCodes.ACCOUNT_UNAUTHRISED);
				return response;
			}
			/*
			 * logger.error("Before address authentication"); D010054
			 * d010054=DataUtils.getAccountAddress(sourceAccount.getId(). getLbrCode(),
			 * sourceAccount.getId().getPrdAcctId());
			 * logger.error("d010054::>>"+d010054); if(d010054==null) {
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.AGENT_ACCOUNT_ADDRESS_NOT_FOUND ); return
			 * response; } if(d010054!=null) {
			 * if(Integer.valueOf(d010054.getDbtrAuthNeeded())!=0 &&
			 * Integer.valueOf(d010054.getDbtrAuthDone())!=1) {
			 * logger.error("ACCOUNT_ADDRESS_UNAUTHERISED");
			 * response.setResponse(MSGConstants.ERROR); response.setErrorMsg(MSGConstants.
			 * AGENT_ACCOUNT_ADDRESS_UNAUTHERISED);
			 * response.setRespCode(ResponseCodes.ACCOUNT_ADDR_UNAUTHORISED); return
			 * response; } }
			 */
			if (sourceAccount.getAcctType() == Byte.valueOf("6")) {
				logger.error("NRE_ACCOUNT");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NRE_ACCOUNT);
				response.setRespCode(ResponseCodes.NRE_ACCOUNT);
				return response;
			}
			if (sourceAccount.getAcctType() == Byte.valueOf("7")) {
				logger.error("NRE_ACCOUNT");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NRO_ACCOUNT);
				response.setRespCode(ResponseCodes.NRE_ACCOUNT);
				return response;
			}
			if (sourceAccount.getAcctStat() == Byte.valueOf("9")) {
				logger.error("ACCOUNT_FREEZED");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.REM_ACCOUNT_OD_CALLED_BACK);
				response.setRespCode(ResponseCodes.ACC_STAT_CALLED_BACK);
				return response;
			}
			
			D001004 system = DataUtils.getSystemParameter(0, "DDSALLOWDRBALYN");
			String value="N";
			if(system!=null)
				value=system.getValue();
			
			if ((drCr.equalsIgnoreCase("D") || drCr.equalsIgnoreCase("AGENT")) && !"Y".equalsIgnoreCase(value.trim())) {
				String isOdAcc = "";
				if (DataUtils.isOverDraftAccount(sourceAccount.getId().getLbrCode(),
						sourceAccount.getId().getPrdAcctId()))
					isOdAcc = "Y";
				logger.error("isOdAcc::>>" + isOdAcc);
				if (isOdAcc.equalsIgnoreCase("Y")) {
					CustomerDetails res = DataUtils.validateOdAccount(sourceAccount.getId().getLbrCode(),
							sourceAccount.getId().getPrdAcctId());
					logger.error("res.getResponse()::>>" + res.getResponse());
					if (res.getResponse().equalsIgnoreCase(MSGConstants.SUCCESS)) {
						double transAmount = Double.parseDouble(amount);
						double availBal = Double
								.valueOf(Double.valueOf(sourceAccount.getActClrBalFcy())
										- Double.valueOf(sourceAccount.getTotalLienFcy()))
								+ res.getOdLimit().getTotSancLimit();
						logger.error("Available Bal::>>" + availBal);
						logger.error("Transaction Amount::>>>" + transAmount);
						if (availBal <= transAmount) {
							CustomerDetails resp = DataUtils.validateOdAdhoclimit(sourceAccount.getId().getLbrCode(),
									sourceAccount.getId().getPrdAcctId());
							logger.error("resp.getErrorMsg()::>>" + resp.getErrorMsg());
							logger.error("resp.getErrorMsg()::>>" + resp.getResponse());
							if (resp.getResponse().equalsIgnoreCase(MSGConstants.SUCCESS)) {
								availBal = Double
										.valueOf(Double.valueOf(sourceAccount.getActClrBalFcy())
												- Double.valueOf(sourceAccount.getTotalLienFcy()))
										+ res.getOdLimit().getTotSancLimit() + resp.getOdAdhocLimit().getTotSancLimit();
								logger.error("Available Bal::>>" + availBal);
								logger.error("Transaction Amount::>>>" + transAmount);
								if (availBal <= transAmount) {
									response.setResponse(MSGConstants.ERROR);
									response.setErrorMsg(MSGConstants.INSUFFICIENT_FUNDS);
									return response;
								}
								response.setResponse(MSGConstants.SUCCESS);
								response.setErrorMsg(MSGConstants.SUCCESS_MSG);
								return response;
							}
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMsg(resp.getErrorMsg());
							return response;
						}
						logger.error("INSUFFICIENT_FUNDS");
						response.setResponse(MSGConstants.SUCCESS);
						response.setErrorMsg(MSGConstants.SUCCESS_MSG);
						return response;
					}
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(res.getErrorMsg());
					return response;
				}
				double transAmount = Double.parseDouble(amount);
				double availBal = Double.valueOf(Double.valueOf(sourceAccount.getActClrBalFcy())
						- Double.valueOf(sourceAccount.getTotalLienFcy()));
				logger.error("Available Bal::>>" + availBal);
				logger.error("Transaction Amount::>>>" + transAmount);
				if (availBal < transAmount) {
					logger.error("INSUFFICIENT_FUNDS");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.INSUFFICIENT_FUNDS);
					return response;
				}
			}
		} else if (drCr.equalsIgnoreCase("CB"))// cheque book request
		{
			if (sourceAccount == null) {
				logger.error("source Account is ::>>" + sourceAccount);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
				response.setRespCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				return response;
			}
			logger.error("Account Status::>>" + sourceAccount.getAcctStat());

			if (Integer.valueOf(sourceAccount.getAcctStat()) == 3) {
				logger.error("ACCOUNT_CLOSED");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_CLOSED);
				response.setRespCode(ResponseCodes.CLOSED_ACCOUNT);
				return response;
			}
			if (Integer.valueOf(sourceAccount.getAcctStat()) == 4) {
				logger.error("ACCOUNT_INOPERATIVE");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_INOPERATIVE);
				response.setRespCode(ResponseCodes.CLOSED_ACCOUNT);
				return response;
			}
			if (Integer.valueOf(sourceAccount.getAcctStat()) == 5) {
				logger.error("ACCOUNT_INOPERATIVE");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_DORMANT);
				response.setRespCode(ResponseCodes.CLOSED_ACCOUNT);
				return response;
			}
			logger.error("Freez type::>>" + sourceAccount.getFreezeType());

			/*
			 * if(drCr.equalsIgnoreCase("D")) {
			 * if(Integer.valueOf(sourceAccount.getFreezeType()) == 2) {
			 * logger.error("ACCOUNT_FREEZED");
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.REM_ACCOUNT_DR_FREEZED);
			 * response.setRespCode(ResponseCodes.ACCOUNT_FREEZED); return response; } }
			 * if(drCr.equalsIgnoreCase("C")) {
			 * if(Integer.valueOf(sourceAccount.getFreezeType()) == 3) {
			 * logger.error("ACCOUNT_FREEZED");
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.BEN_ACCOUNT_CR_FREEZED);
			 * response.setRespCode(ResponseCodes.ACCOUNT_FREEZED); return response; } }
			 */
			if (Integer.valueOf(sourceAccount.getFreezeType()) == 2) {
				logger.error("ACCOUNT_FREEZED");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_DR_FREEZED);
				response.setRespCode(ResponseCodes.ACCOUNT_FREEZED);
				return response;
			}
			/*
			 * if(Integer.valueOf(sourceAccount.getFreezeType()) == 3) {
			 * logger.error("ACCOUNT_FREEZED");
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.ACCOUNT_CR_FREEZED);
			 * response.setRespCode(ResponseCodes.ACCOUNT_FREEZED); return response; }
			 */
			if (Integer.valueOf(sourceAccount.getFreezeType()) == 4) {
				logger.error("ACCOUNT_TOTAL_FREEZED");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_TOTAL_FREEZED);
				response.setRespCode(ResponseCodes.ACCOUNT_FREEZED);
				return response;
			}
			if (Integer.valueOf(sourceAccount.getDbtrAuthNeeded()) != 0
					&& Integer.valueOf(sourceAccount.getDbtrAuthDone()) != 1) {
				logger.error("ACCOUNT_UNAUTHERISED");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_UNAUTHERISED);
				response.setRespCode(ResponseCodes.ACCOUNT_UNAUTHRISED);
				return response;
			}
			logger.error("Before address authentication");
			D010054 d010054 = DataUtils.getAccountAddress(sourceAccount.getId().getLbrCode(),
					sourceAccount.getId().getPrdAcctId());
			logger.error("d010054::>>" + d010054);
			if (d010054 == null) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_ADDRESS_NOT_FOUND);
				return response;
			}
			if (d010054 != null) {
				if (Integer.valueOf(d010054.getDbtrAuthNeeded()) != 0
						&& Integer.valueOf(d010054.getDbtrAuthDone()) != 1) {
					logger.error("ACCOUNT_ADDRESS_UNAUTHERISED");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.ACCOUNT_ADDRESS_UNAUTHERISED);
					response.setRespCode(ResponseCodes.ACCOUNT_ADDR_UNAUTHORISED);
					return response;
				}
			}
			if (sourceAccount.getAcctType() == Byte.valueOf("6")) {
				logger.error("NRE_ACCOUNT");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NRE_ACCOUNT);
				response.setRespCode(ResponseCodes.NRE_ACCOUNT);
				return response;
			}
			if (sourceAccount.getAcctType() == Byte.valueOf("7")) {
				logger.error("NRE_ACCOUNT");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NRO_ACCOUNT);
				response.setRespCode(ResponseCodes.NRE_ACCOUNT);
				return response;
			}
			if (sourceAccount.getAcctStat() == Byte.valueOf("9")) {
				logger.error("ACCOUNT_FREEZED");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.REM_ACCOUNT_OD_CALLED_BACK);
				response.setRespCode(ResponseCodes.ACC_STAT_CALLED_BACK);
				return response;
			}
			if (drCr.equalsIgnoreCase("D")) {
				String isOdAcc = "";
				if (DataUtils.isOverDraftAccount(sourceAccount.getId().getLbrCode(),
						sourceAccount.getId().getPrdAcctId()))
					isOdAcc = "Y";
				logger.error("isOdAcc::>>" + isOdAcc);
				if (isOdAcc.equalsIgnoreCase("Y")) {
					CustomerDetails res = DataUtils.validateOdAccount(sourceAccount.getId().getLbrCode(),
							sourceAccount.getId().getPrdAcctId());
					logger.error("res.getResponse()::>>" + res.getResponse());
					if (res.getResponse().equalsIgnoreCase(MSGConstants.SUCCESS)) {
						double transAmount = Double.parseDouble(amount);
						double availBal = Double
								.valueOf(Double.valueOf(sourceAccount.getActClrBalFcy())
										- Double.valueOf(sourceAccount.getTotalLienFcy()))
								+ res.getOdLimit().getTotSancLimit();
						logger.error("Available Bal::>>" + availBal);
						logger.error("Transaction Amount::>>>" + transAmount);
						if (availBal <= transAmount) {
							CustomerDetails resp = DataUtils.validateOdAdhoclimit(sourceAccount.getId().getLbrCode(),
									sourceAccount.getId().getPrdAcctId());
							logger.error("resp.getErrorMsg()::>>" + resp.getErrorMsg());
							logger.error("resp.getErrorMsg()::>>" + resp.getResponse());
							if (resp.getResponse().equalsIgnoreCase(MSGConstants.SUCCESS)) {
								availBal = Double
										.valueOf(Double.valueOf(sourceAccount.getActClrBalFcy())
												- Double.valueOf(sourceAccount.getTotalLienFcy()))
										+ res.getOdLimit().getTotSancLimit() + resp.getOdAdhocLimit().getTotSancLimit();
								logger.error("Available Bal::>>" + availBal);
								logger.error("Transaction Amount::>>>" + transAmount);
								if (availBal <= transAmount) {
									response.setResponse(MSGConstants.ERROR);
									response.setErrorMsg(MSGConstants.INSUFFICIENT_FUNDS);
									return response;
								}
								response.setResponse(MSGConstants.SUCCESS);
								response.setErrorMsg(MSGConstants.SUCCESS_MSG);
								return response;
							}
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMsg(resp.getErrorMsg());
							return response;
						}
						logger.error("INSUFFICIENT_FUNDS");
						response.setResponse(MSGConstants.SUCCESS);
						response.setErrorMsg(MSGConstants.SUCCESS_MSG);
						return response;
					}
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(res.getErrorMsg());
					return response;
				}
				double transAmount = Double.parseDouble(amount);
				double availBal = Double.valueOf(Double.valueOf(sourceAccount.getActClrBalFcy())
						- Double.valueOf(sourceAccount.getTotalLienFcy()));
				logger.error("Available Bal::>>" + availBal);
				logger.error("Transaction Amount::>>>" + transAmount);
				if (availBal < transAmount) {
					logger.error("INSUFFICIENT_FUNDS");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.INSUFFICIENT_FUNDS);
					return response;
				}
			}
		} else if (drCr.equalsIgnoreCase("AS"))// account Statement
		{
			if (sourceAccount == null) {
				logger.error("source Account is ::>>" + sourceAccount);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
				response.setRespCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				return response;
			}
			logger.error("Account Status::>>" + sourceAccount.getAcctStat());

			if (Integer.valueOf(sourceAccount.getAcctStat()) == 3) {
				logger.error("ACCOUNT_CLOSED");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_CLOSED);
				response.setRespCode(ResponseCodes.CLOSED_ACCOUNT);
				return response;
			}
			if (Integer.valueOf(sourceAccount.getAcctStat()) == 4) {
				logger.error("ACCOUNT_INOPERATIVE");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_INOPERATIVE);
				response.setRespCode(ResponseCodes.CLOSED_ACCOUNT);
				return response;
			}
			if (Integer.valueOf(sourceAccount.getAcctStat()) == 5) {
				logger.error("ACCOUNT_INOPERATIVE");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_DORMANT);
				response.setRespCode(ResponseCodes.CLOSED_ACCOUNT);
				return response;
			}
			logger.error("Freez type::>>" + sourceAccount.getFreezeType());

			/*
			 * if(drCr.equalsIgnoreCase("D")) {
			 * if(Integer.valueOf(sourceAccount.getFreezeType()) == 2) {
			 * logger.error("ACCOUNT_FREEZED");
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.REM_ACCOUNT_DR_FREEZED);
			 * response.setRespCode(ResponseCodes.ACCOUNT_FREEZED); return response; } }
			 * if(drCr.equalsIgnoreCase("C")) {
			 * if(Integer.valueOf(sourceAccount.getFreezeType()) == 3) {
			 * logger.error("ACCOUNT_FREEZED");
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.BEN_ACCOUNT_CR_FREEZED);
			 * response.setRespCode(ResponseCodes.ACCOUNT_FREEZED); return response; } }
			 */
			/*
			 * if(Integer.valueOf(sourceAccount.getFreezeType()) == 2) {
			 * logger.error("ACCOUNT_FREEZED");
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.ACCOUNT_DR_FREEZED);
			 * response.setRespCode(ResponseCodes.ACCOUNT_FREEZED); return response; }
			 */
			/*
			 * if(Integer.valueOf(sourceAccount.getFreezeType()) == 3) {
			 * logger.error("ACCOUNT_FREEZED");
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.ACCOUNT_CR_FREEZED);
			 * response.setRespCode(ResponseCodes.ACCOUNT_FREEZED); return response; }
			 */
			/*
			 * if(Integer.valueOf(sourceAccount.getFreezeType()) == 4) {
			 * logger.error("ACCOUNT_TOTAL_FREEZED");
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.ACCOUNT_TOTAL_FREEZED);
			 * response.setRespCode(ResponseCodes.ACCOUNT_FREEZED); return response; }
			 */
			/*
			 * if(Integer.valueOf(sourceAccount.getDbtrAuthNeeded())!=0 &&
			 * Integer.valueOf(sourceAccount.getDbtrAuthDone())!=1) {
			 * logger.error("ACCOUNT_UNAUTHERISED");
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.ACCOUNT_UNAUTHERISED);
			 * response.setRespCode(ResponseCodes.ACCOUNT_UNAUTHRISED); return response; }
			 */
			/*
			 * logger.error("Before address authentication"); D010054
			 * d010054=DataUtils.getAccountAddress(sourceAccount.getId(). getLbrCode(),
			 * sourceAccount.getId().getPrdAcctId());
			 * logger.error("d010054::>>"+d010054); if(d010054==null) {
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.ACCOUNT_ADDRESS_NOT_FOUND); return
			 * response; } if(d010054!=null) {
			 * if(Integer.valueOf(d010054.getDbtrAuthNeeded())!=0 &&
			 * Integer.valueOf(d010054.getDbtrAuthDone())!=1) {
			 * logger.error("ACCOUNT_ADDRESS_UNAUTHERISED");
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.ACCOUNT_ADDRESS_UNAUTHERISED);
			 * response.setRespCode(ResponseCodes.ACCOUNT_ADDR_UNAUTHORISED); return
			 * response; } }
			 */
			if (sourceAccount.getAcctType() == Byte.valueOf("6")) {
				logger.error("NRE_ACCOUNT");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NRE_ACCOUNT);
				response.setRespCode(ResponseCodes.NRE_ACCOUNT);
				return response;
			}
			if (sourceAccount.getAcctType() == Byte.valueOf("7")) {
				logger.error("NRE_ACCOUNT");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NRO_ACCOUNT);
				response.setRespCode(ResponseCodes.NRE_ACCOUNT);
				return response;
			}
			if (sourceAccount.getAcctStat() == Byte.valueOf("9")) {
				logger.error("ACCOUNT_FREEZED");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.REM_ACCOUNT_OD_CALLED_BACK);
				response.setRespCode(ResponseCodes.ACC_STAT_CALLED_BACK);
				return response;
			}
			if (drCr.equalsIgnoreCase("D")) {
				String isOdAcc = "";
				if (DataUtils.isOverDraftAccount(sourceAccount.getId().getLbrCode(),
						sourceAccount.getId().getPrdAcctId()))
					isOdAcc = "Y";
				logger.error("isOdAcc::>>" + isOdAcc);
				if (isOdAcc.equalsIgnoreCase("Y")) {
					CustomerDetails res = DataUtils.validateOdAccount(sourceAccount.getId().getLbrCode(),
							sourceAccount.getId().getPrdAcctId());
					logger.error("res.getResponse()::>>" + res.getResponse());
					if (res.getResponse().equalsIgnoreCase(MSGConstants.SUCCESS)) {
						double transAmount = Double.parseDouble(amount);
						double availBal = Double
								.valueOf(Double.valueOf(sourceAccount.getActClrBalFcy())
										- Double.valueOf(sourceAccount.getTotalLienFcy()))
								+ res.getOdLimit().getTotSancLimit();
						logger.error("Available Bal::>>" + availBal);
						logger.error("Transaction Amount::>>>" + transAmount);
						if (availBal <= transAmount) {
							CustomerDetails resp = DataUtils.validateOdAdhoclimit(sourceAccount.getId().getLbrCode(),
									sourceAccount.getId().getPrdAcctId());
							logger.error("resp.getErrorMsg()::>>" + resp.getErrorMsg());
							logger.error("resp.getErrorMsg()::>>" + resp.getResponse());
							if (resp.getResponse().equalsIgnoreCase(MSGConstants.SUCCESS)) {
								availBal = Double
										.valueOf(Double.valueOf(sourceAccount.getActClrBalFcy())
												- Double.valueOf(sourceAccount.getTotalLienFcy()))
										+ res.getOdLimit().getTotSancLimit() + resp.getOdAdhocLimit().getTotSancLimit();
								logger.error("Available Bal::>>" + availBal);
								logger.error("Transaction Amount::>>>" + transAmount);
								if (availBal <= transAmount) {
									response.setResponse(MSGConstants.ERROR);
									response.setErrorMsg(MSGConstants.INSUFFICIENT_FUNDS);
									return response;
								}
								response.setResponse(MSGConstants.SUCCESS);
								response.setErrorMsg(MSGConstants.SUCCESS_MSG);
								return response;
							}
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMsg(resp.getErrorMsg());
							return response;
						}
						logger.error("INSUFFICIENT_FUNDS");
						response.setResponse(MSGConstants.SUCCESS);
						response.setErrorMsg(MSGConstants.SUCCESS_MSG);
						return response;
					}
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(res.getErrorMsg());
					return response;
				}
				double transAmount = Double.parseDouble(amount);
				double availBal = Double.valueOf(Double.valueOf(sourceAccount.getActClrBalFcy())
						- Double.valueOf(sourceAccount.getTotalLienFcy()));
				logger.error("Available Bal::>>" + availBal);
				logger.error("Transaction Amount::>>>" + transAmount);
				if (availBal < transAmount) {
					logger.error("INSUFFICIENT_FUNDS");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.INSUFFICIENT_FUNDS);
					return response;
				}
			}
		} else if (drCr.equalsIgnoreCase("MB"))// Mobile Number Update
		{
			if (sourceAccount == null) {
				logger.error("source Account is ::>>" + sourceAccount);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
				response.setRespCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				return response;
			}
			logger.error("Account Status::>>" + sourceAccount.getAcctStat());

			if (Integer.valueOf(sourceAccount.getAcctStat()) == 3) {
				logger.error("ACCOUNT_CLOSED");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_CLOSED);
				response.setRespCode(ResponseCodes.CLOSED_ACCOUNT);
				return response;
			}
			if (Integer.valueOf(sourceAccount.getAcctStat()) == 4) {
				logger.error("ACCOUNT_INOPERATIVE");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_INOPERATIVE);
				response.setRespCode(ResponseCodes.CLOSED_ACCOUNT);
				return response;
			}
			if (Integer.valueOf(sourceAccount.getAcctStat()) == 5) {
				logger.error("ACCOUNT_INOPERATIVE");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_DORMANT);
				response.setRespCode(ResponseCodes.CLOSED_ACCOUNT);
				return response;
			}
			logger.error("Freez type::>>" + sourceAccount.getFreezeType());

			/*
			 * if(drCr.equalsIgnoreCase("D")) {
			 * if(Integer.valueOf(sourceAccount.getFreezeType()) == 2) {
			 * logger.error("ACCOUNT_FREEZED");
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.REM_ACCOUNT_DR_FREEZED);
			 * response.setRespCode(ResponseCodes.ACCOUNT_FREEZED); return response; } }
			 * if(drCr.equalsIgnoreCase("C")) {
			 * if(Integer.valueOf(sourceAccount.getFreezeType()) == 3) {
			 * logger.error("ACCOUNT_FREEZED");
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.BEN_ACCOUNT_CR_FREEZED);
			 * response.setRespCode(ResponseCodes.ACCOUNT_FREEZED); return response; } }
			 */
			/*
			 * if(Integer.valueOf(sourceAccount.getFreezeType()) == 2) {
			 * logger.error("ACCOUNT_FREEZED");
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.ACCOUNT_DR_FREEZED);
			 * response.setRespCode(ResponseCodes.ACCOUNT_FREEZED); return response; }
			 */
			/*
			 * if(Integer.valueOf(sourceAccount.getFreezeType()) == 3) {
			 * logger.error("ACCOUNT_FREEZED");
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.ACCOUNT_CR_FREEZED);
			 * response.setRespCode(ResponseCodes.ACCOUNT_FREEZED); return response; }
			 */
			/*
			 * if(Integer.valueOf(sourceAccount.getFreezeType()) == 4) {
			 * logger.error("ACCOUNT_TOTAL_FREEZED");
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.ACCOUNT_TOTAL_FREEZED);
			 * response.setRespCode(ResponseCodes.ACCOUNT_FREEZED); return response; }
			 */
			if (Integer.valueOf(sourceAccount.getDbtrAuthNeeded()) != 0
					&& Integer.valueOf(sourceAccount.getDbtrAuthDone()) != 1) {
				logger.error("ACCOUNT_UNAUTHERISED");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_UNAUTHERISED);
				response.setRespCode(ResponseCodes.ACCOUNT_UNAUTHRISED);
				return response;
			}
			logger.error("Before address authentication");
			D010054 d010054 = DataUtils.getAccountAddress(sourceAccount.getId().getLbrCode(),
					sourceAccount.getId().getPrdAcctId());
			logger.error("d010054::>>" + d010054);
			if (d010054 == null) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_ADDRESS_NOT_FOUND);
				return response;
			}
			/*if (d010054 != null) {
				if (Integer.valueOf(d010054.getDbtrAuthNeeded()) != 0
						&& Integer.valueOf(d010054.getDbtrAuthDone()) != 1) {
					logger.error("ACCOUNT_ADDRESS_UNAUTHERISED");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.ACCOUNT_ADDRESS_UNAUTHERISED);
					response.setRespCode(ResponseCodes.ACCOUNT_ADDR_UNAUTHORISED);
					return response;
				}
			}*/
			if (sourceAccount.getAcctType() == Byte.valueOf("6")) {
				logger.error("NRE_ACCOUNT");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NRE_ACCOUNT);
				response.setRespCode(ResponseCodes.NRE_ACCOUNT);
				return response;
			}
			if (sourceAccount.getAcctType() == Byte.valueOf("7")) {
				logger.error("NRE_ACCOUNT");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NRO_ACCOUNT);
				response.setRespCode(ResponseCodes.NRE_ACCOUNT);
				return response;
			}
			if (sourceAccount.getAcctStat() == Byte.valueOf("9")) {
				logger.error("ACCOUNT_FREEZED");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.REM_ACCOUNT_OD_CALLED_BACK);
				response.setRespCode(ResponseCodes.ACC_STAT_CALLED_BACK);
				return response;
			}
			if (drCr.equalsIgnoreCase("D")) {
				String isOdAcc = "";
				if (DataUtils.isOverDraftAccount(sourceAccount.getId().getLbrCode(),
						sourceAccount.getId().getPrdAcctId()))
					isOdAcc = "Y";
				logger.error("isOdAcc::>>" + isOdAcc);
				if (isOdAcc.equalsIgnoreCase("Y")) {
					CustomerDetails res = DataUtils.validateOdAccount(sourceAccount.getId().getLbrCode(),
							sourceAccount.getId().getPrdAcctId());
					logger.error("res.getResponse()::>>" + res.getResponse());
					if (res.getResponse().equalsIgnoreCase(MSGConstants.SUCCESS)) {
						double transAmount = Double.parseDouble(amount);
						double availBal = Double
								.valueOf(Double.valueOf(sourceAccount.getActClrBalFcy())
										- Double.valueOf(sourceAccount.getTotalLienFcy()))
								+ res.getOdLimit().getTotSancLimit();
						logger.error("Available Bal::>>" + availBal);
						logger.error("Transaction Amount::>>>" + transAmount);
						if (availBal <= transAmount) {
							CustomerDetails resp = DataUtils.validateOdAdhoclimit(sourceAccount.getId().getLbrCode(),
									sourceAccount.getId().getPrdAcctId());
							logger.error("resp.getErrorMsg()::>>" + resp.getErrorMsg());
							logger.error("resp.getErrorMsg()::>>" + resp.getResponse());
							if (resp.getResponse().equalsIgnoreCase(MSGConstants.SUCCESS)) {
								availBal = Double
										.valueOf(Double.valueOf(sourceAccount.getActClrBalFcy())
												- Double.valueOf(sourceAccount.getTotalLienFcy()))
										+ res.getOdLimit().getTotSancLimit() + resp.getOdAdhocLimit().getTotSancLimit();
								logger.error("Available Bal::>>" + availBal);
								logger.error("Transaction Amount::>>>" + transAmount);
								if (availBal <= transAmount) {
									response.setResponse(MSGConstants.ERROR);
									response.setErrorMsg(MSGConstants.INSUFFICIENT_FUNDS);
									return response;
								}
								response.setResponse(MSGConstants.SUCCESS);
								response.setErrorMsg(MSGConstants.SUCCESS_MSG);
								return response;
							}
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMsg(resp.getErrorMsg());
							return response;
						}
						logger.error("INSUFFICIENT_FUNDS");
						response.setResponse(MSGConstants.SUCCESS);
						response.setErrorMsg(MSGConstants.SUCCESS_MSG);
						return response;
					}
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(res.getErrorMsg());
					return response;
				}
				double transAmount = Double.parseDouble(amount);
				double availBal = Double.valueOf(Double.valueOf(sourceAccount.getActClrBalFcy())
						- Double.valueOf(sourceAccount.getTotalLienFcy()));
				logger.error("Available Bal::>>" + availBal);
				logger.error("Transaction Amount::>>>" + transAmount);
				if (availBal < transAmount) {
					logger.error("INSUFFICIENT_FUNDS");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.INSUFFICIENT_FUNDS);
					return response;
				}
			}
		} else if (drCr.equalsIgnoreCase("COMMON")) {
			if (sourceAccount == null) {
				logger.error("source Account is ::>>" + sourceAccount);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
				response.setRespCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				return response;
			}
			logger.error("Account Status::>>" + sourceAccount.getAcctStat());

			if (Integer.valueOf(sourceAccount.getAcctStat()) == 3) {
				logger.error("ACCOUNT_CLOSED");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_CLOSED);
				response.setRespCode(ResponseCodes.CLOSED_ACCOUNT);
				return response;
			}
			if (Integer.valueOf(sourceAccount.getAcctStat()) == 4) {
				logger.error("ACCOUNT_INOPERATIVE");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_INOPERATIVE);
				response.setRespCode(ResponseCodes.CLOSED_ACCOUNT);
				return response;
			}
			if (Integer.valueOf(sourceAccount.getAcctStat()) == 5) {
				logger.error("ACCOUNT_INOPERATIVE");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_DORMANT);
				response.setRespCode(ResponseCodes.CLOSED_ACCOUNT);
				return response;
			}
			logger.error("Freez type::>>" + sourceAccount.getFreezeType());

			/*
			 * if(drCr.equalsIgnoreCase("D")) {
			 * if(Integer.valueOf(sourceAccount.getFreezeType()) == 2) {
			 * logger.error("ACCOUNT_FREEZED");
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.REM_ACCOUNT_DR_FREEZED);
			 * response.setRespCode(ResponseCodes.ACCOUNT_FREEZED); return response; } }
			 * if(drCr.equalsIgnoreCase("C")) {
			 * if(Integer.valueOf(sourceAccount.getFreezeType()) == 3) {
			 * logger.error("ACCOUNT_FREEZED");
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.BEN_ACCOUNT_CR_FREEZED);
			 * response.setRespCode(ResponseCodes.ACCOUNT_FREEZED); return response; } }
			 */
			/*
			 * if(Integer.valueOf(sourceAccount.getFreezeType()) == 2) {
			 * logger.error("ACCOUNT_FREEZED");
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.ACCOUNT_DR_FREEZED);
			 * response.setRespCode(ResponseCodes.ACCOUNT_FREEZED); return response; }
			 * if(Integer.valueOf(sourceAccount.getFreezeType()) == 3) {
			 * logger.error("ACCOUNT_FREEZED");
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.ACCOUNT_CR_FREEZED);
			 * response.setRespCode(ResponseCodes.ACCOUNT_FREEZED); return response; }
			 * if(Integer.valueOf(sourceAccount.getFreezeType()) == 4) {
			 * logger.error("ACCOUNT_TOTAL_FREEZED");
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.ACCOUNT_TOTAL_FREEZED);
			 * response.setRespCode(ResponseCodes.ACCOUNT_FREEZED); return response; }
			 * if(Integer.valueOf(sourceAccount.getDbtrAuthNeeded())!=0 &&
			 * Integer.valueOf(sourceAccount.getDbtrAuthDone())!=1) {
			 * logger.error("ACCOUNT_UNAUTHERISED");
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.ACCOUNT_UNAUTHERISED);
			 * response.setRespCode(ResponseCodes.ACCOUNT_UNAUTHRISED); return response; }
			 * logger.error("Before address authentication"); D010054
			 * d010054=DataUtils.getAccountAddress(sourceAccount.getId(). getLbrCode(),
			 * sourceAccount.getId().getPrdAcctId());
			 * logger.error("d010054::>>"+d010054); if(d010054==null) {
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.ACCOUNT_ADDRESS_NOT_FOUND); return
			 * response; } if(d010054!=null) {
			 * if(Integer.valueOf(d010054.getDbtrAuthNeeded())!=0 &&
			 * Integer.valueOf(d010054.getDbtrAuthDone())!=1) {
			 * logger.error("ACCOUNT_ADDRESS_UNAUTHERISED");
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.ACCOUNT_ADDRESS_UNAUTHERISED);
			 * response.setRespCode(ResponseCodes.ACCOUNT_ADDR_UNAUTHORISED); return
			 * response; } }
			 */
			if (sourceAccount.getAcctType() == Byte.valueOf("6")) {
				logger.error("NRE_ACCOUNT");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NRE_ACCOUNT);
				response.setRespCode(ResponseCodes.NRE_ACCOUNT);
				return response;
			}
			if (sourceAccount.getAcctType() == Byte.valueOf("7")) {
				logger.error("NRE_ACCOUNT");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NRO_ACCOUNT);
				response.setRespCode(ResponseCodes.NRE_ACCOUNT);
				return response;
			}
			if (sourceAccount.getAcctStat() == Byte.valueOf("9")) {
				logger.error("ACCOUNT_FREEZED");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.REM_ACCOUNT_OD_CALLED_BACK);
				response.setRespCode(ResponseCodes.ACC_STAT_CALLED_BACK);
				return response;
			}
			if (drCr.equalsIgnoreCase("D")) {
				String isOdAcc = "";
				if (DataUtils.isOverDraftAccount(sourceAccount.getId().getLbrCode(),
						sourceAccount.getId().getPrdAcctId()))
					isOdAcc = "Y";
				logger.error("isOdAcc::>>" + isOdAcc);
				if (isOdAcc.equalsIgnoreCase("Y")) {
					CustomerDetails res = DataUtils.validateOdAccount(sourceAccount.getId().getLbrCode(),
							sourceAccount.getId().getPrdAcctId());
					logger.error("res.getResponse()::>>" + res.getResponse());
					if (res.getResponse().equalsIgnoreCase(MSGConstants.SUCCESS)) {
						double transAmount = Double.parseDouble(amount);
						double availBal = Double
								.valueOf(Double.valueOf(sourceAccount.getActClrBalFcy())
										- Double.valueOf(sourceAccount.getTotalLienFcy()))
								+ res.getOdLimit().getTotSancLimit();
						logger.error("Available Bal::>>" + availBal);
						logger.error("Transaction Amount::>>>" + transAmount);
						if (availBal <= transAmount) {
							CustomerDetails resp = DataUtils.validateOdAdhoclimit(sourceAccount.getId().getLbrCode(),
									sourceAccount.getId().getPrdAcctId());
							logger.error("resp.getErrorMsg()::>>" + resp.getErrorMsg());
							logger.error("resp.getErrorMsg()::>>" + resp.getResponse());
							if (resp.getResponse().equalsIgnoreCase(MSGConstants.SUCCESS)) {
								availBal = Double
										.valueOf(Double.valueOf(sourceAccount.getActClrBalFcy())
												- Double.valueOf(sourceAccount.getTotalLienFcy()))
										+ res.getOdLimit().getTotSancLimit() + resp.getOdAdhocLimit().getTotSancLimit();
								logger.error("Available Bal::>>" + availBal);
								logger.error("Transaction Amount::>>>" + transAmount);
								if (availBal <= transAmount) {
									response.setResponse(MSGConstants.ERROR);
									response.setErrorMsg(MSGConstants.INSUFFICIENT_FUNDS);
									return response;
								}
								response.setResponse(MSGConstants.SUCCESS);
								response.setErrorMsg(MSGConstants.SUCCESS_MSG);
								return response;
							}
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMsg(resp.getErrorMsg());
							return response;
						}
						logger.error("INSUFFICIENT_FUNDS");
						response.setResponse(MSGConstants.SUCCESS);
						response.setErrorMsg(MSGConstants.SUCCESS_MSG);
						return response;
					}
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(res.getErrorMsg());
					return response;
				}
				double transAmount = Double.parseDouble(amount);
				double availBal = Double.valueOf(Double.valueOf(sourceAccount.getActClrBalFcy())
						- Double.valueOf(sourceAccount.getTotalLienFcy()));
				logger.error("Available Bal::>>" + availBal);
				logger.error("Transaction Amount::>>>" + transAmount);
				if (availBal < transAmount) {
					logger.error("INSUFFICIENT_FUNDS");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.INSUFFICIENT_FUNDS);
					return response;
				}
			}

		} else if (drCr.equalsIgnoreCase("CC")) {
			if (sourceAccount == null) {
				logger.error("source Account is ::>>" + sourceAccount);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
				response.setRespCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				return response;
			}
			logger.error("Account Status::>>" + sourceAccount.getAcctStat());

			if (Integer.valueOf(sourceAccount.getAcctStat()) == 3) {
				logger.error("ACCOUNT_CLOSED");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_CLOSED);
				response.setRespCode(ResponseCodes.CLOSED_ACCOUNT);
				return response;
			}
			if (Integer.valueOf(sourceAccount.getAcctStat()) == 4) {
				logger.error("ACCOUNT_INOPERATIVE");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_INOPERATIVE);
				response.setRespCode(ResponseCodes.CLOSED_ACCOUNT);
				return response;
			}
			if (Integer.valueOf(sourceAccount.getAcctStat()) == 5) {
				logger.error("ACCOUNT_INOPERATIVE");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_DORMANT);
				response.setRespCode(ResponseCodes.CLOSED_ACCOUNT);
				return response;
			}
			logger.error("Freez type::>>" + sourceAccount.getFreezeType());

			/*
			 * if(drCr.equalsIgnoreCase("D")) {
			 * if(Integer.valueOf(sourceAccount.getFreezeType()) == 2) {
			 * logger.error("ACCOUNT_FREEZED");
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.REM_ACCOUNT_DR_FREEZED);
			 * response.setRespCode(ResponseCodes.ACCOUNT_FREEZED); return response; } }
			 * if(drCr.equalsIgnoreCase("C")) {
			 * if(Integer.valueOf(sourceAccount.getFreezeType()) == 3) {
			 * logger.error("ACCOUNT_FREEZED");
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.BEN_ACCOUNT_CR_FREEZED);
			 * response.setRespCode(ResponseCodes.ACCOUNT_FREEZED); return response; } }
			 */
			/*
			 * if(Integer.valueOf(sourceAccount.getFreezeType()) == 2) {
			 * logger.error("ACCOUNT_FREEZED");
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.ACCOUNT_DR_FREEZED);
			 * response.setRespCode(ResponseCodes.ACCOUNT_FREEZED); return response; }
			 * if(Integer.valueOf(sourceAccount.getFreezeType()) == 3) {
			 * logger.error("ACCOUNT_FREEZED");
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.ACCOUNT_CR_FREEZED);
			 * response.setRespCode(ResponseCodes.ACCOUNT_FREEZED); return response; }
			 * if(Integer.valueOf(sourceAccount.getFreezeType()) == 4) {
			 * logger.error("ACCOUNT_TOTAL_FREEZED");
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.ACCOUNT_TOTAL_FREEZED);
			 * response.setRespCode(ResponseCodes.ACCOUNT_FREEZED); return response; }
			 */
			if (Integer.valueOf(sourceAccount.getDbtrAuthNeeded()) != 0
					&& Integer.valueOf(sourceAccount.getDbtrAuthDone()) != 1) {
				logger.error("ACCOUNT_UNAUTHERISED");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_UNAUTHERISED);
				response.setRespCode(ResponseCodes.ACCOUNT_UNAUTHRISED);
				return response;
			}
			logger.error("Before address authentication");
			D010054 d010054 = DataUtils.getAccountAddress(sourceAccount.getId().getLbrCode(),
					sourceAccount.getId().getPrdAcctId());
			logger.error("d010054::>>" + d010054);
			if (d010054 == null) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_ADDRESS_NOT_FOUND);
				return response;
			}
			/*if (d010054 != null) {
				if (Integer.valueOf(d010054.getDbtrAuthNeeded()) != 0
						&& Integer.valueOf(d010054.getDbtrAuthDone()) != 1) {
					logger.error("ACCOUNT_ADDRESS_UNAUTHERISED");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.ACCOUNT_ADDRESS_UNAUTHERISED);
					response.setRespCode(ResponseCodes.ACCOUNT_ADDR_UNAUTHORISED);
					return response;
				}
			}*/
			if (sourceAccount.getAcctType() == Byte.valueOf("6")) {
				logger.error("NRE_ACCOUNT");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NRE_ACCOUNT);
				response.setRespCode(ResponseCodes.NRE_ACCOUNT);
				return response;
			}
			if (sourceAccount.getAcctType() == Byte.valueOf("7")) {
				logger.error("NRE_ACCOUNT");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NRO_ACCOUNT);
				response.setRespCode(ResponseCodes.NRE_ACCOUNT);
				return response;
			}
			if (sourceAccount.getAcctStat() == Byte.valueOf("9")) {
				logger.error("ACCOUNT_FREEZED");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.REM_ACCOUNT_OD_CALLED_BACK);
				response.setRespCode(ResponseCodes.ACC_STAT_CALLED_BACK);
				return response;
			}
			if (drCr.equalsIgnoreCase("D")) {
				String isOdAcc = "";
				if (DataUtils.isOverDraftAccount(sourceAccount.getId().getLbrCode(),
						sourceAccount.getId().getPrdAcctId()))
					isOdAcc = "Y";
				logger.error("isOdAcc::>>" + isOdAcc);
				if (isOdAcc.equalsIgnoreCase("Y")) {
					CustomerDetails res = DataUtils.validateOdAccount(sourceAccount.getId().getLbrCode(),
							sourceAccount.getId().getPrdAcctId());
					logger.error("res.getResponse()::>>" + res.getResponse());
					if (res.getResponse().equalsIgnoreCase(MSGConstants.SUCCESS)) {
						double transAmount = Double.parseDouble(amount);
						double availBal = Double
								.valueOf(Double.valueOf(sourceAccount.getActClrBalFcy())
										- Double.valueOf(sourceAccount.getTotalLienFcy()))
								+ res.getOdLimit().getTotSancLimit();
						logger.error("Available Bal::>>" + availBal);
						logger.error("Transaction Amount::>>>" + transAmount);
						if (availBal <= transAmount) {
							CustomerDetails resp = DataUtils.validateOdAdhoclimit(sourceAccount.getId().getLbrCode(),
									sourceAccount.getId().getPrdAcctId());
							logger.error("resp.getErrorMsg()::>>" + resp.getErrorMsg());
							logger.error("resp.getErrorMsg()::>>" + resp.getResponse());
							if (resp.getResponse().equalsIgnoreCase(MSGConstants.SUCCESS)) {
								availBal = Double
										.valueOf(Double.valueOf(sourceAccount.getActClrBalFcy())
												- Double.valueOf(sourceAccount.getTotalLienFcy()))
										+ res.getOdLimit().getTotSancLimit() + resp.getOdAdhocLimit().getTotSancLimit();
								logger.error("Available Bal::>>" + availBal);
								logger.error("Transaction Amount::>>>" + transAmount);
								if (availBal <= transAmount) {
									response.setResponse(MSGConstants.ERROR);
									response.setErrorMsg(MSGConstants.INSUFFICIENT_FUNDS);
									return response;
								}
								response.setResponse(MSGConstants.SUCCESS);
								response.setErrorMsg(MSGConstants.SUCCESS_MSG);
								return response;
							}
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMsg(resp.getErrorMsg());
							return response;
						}
						logger.error("INSUFFICIENT_FUNDS");
						response.setResponse(MSGConstants.SUCCESS);
						response.setErrorMsg(MSGConstants.SUCCESS_MSG);
						return response;
					}
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(res.getErrorMsg());
					return response;
				}
				double transAmount = Double.parseDouble(amount);
				double availBal = Double.valueOf(Double.valueOf(sourceAccount.getActClrBalFcy())
						- Double.valueOf(sourceAccount.getTotalLienFcy()));
				logger.error("Available Bal::>>" + availBal);
				logger.error("Transaction Amount::>>>" + transAmount);
				if (availBal < transAmount) {
					logger.error("INSUFFICIENT_FUNDS");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.INSUFFICIENT_FUNDS);
					return response;
				}
			}
		}
		response.setResponse(MSGConstants.SUCCESS);
		response.setErrorMsg(MSGConstants.SUCCESS_MSG);
		return response;
	}

	public static IMPSTransactionResponse initiateIMPSTransaction(String accNo15digit, String destAccNo15digit,
			String amount, String narration, String mob1, String mmid1, String mob2, String mmid2, String tType) {
		logger.error("<<<<<< TransactionService.initiateIMPSTransaction >>>>>>>");
		logger.error("accNo15digit::>>" + accNo15digit + " destAccNo15digit::>>" + destAccNo15digit + " Amount::>>>"
				+ amount + " narration::>>" + narration + " mob1::>>>" + mob1 + " mmid1::>>" + mmid1 + " mob2::>>"
				+ mob2 + " mmid2::>>" + mmid2);
		IMPSTransactionResponse response = new IMPSTransactionResponse();
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		try {
			/***Added By Aniket Desai on 30th Aug, 2019 for Narration change Requested By Shailesh Sir:---Start--- ***/
			String acctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit);
			int lbrcode = Integer.parseInt(accNo15digit.substring(0, 3));
			/***Added By Aniket Desai on 30th Aug, 2019 for Narration change Requested By Shailesh Sir:---End--- ***/
			
			/***Commented By Aniket Desai on 21st Aug, 2020 for issue #7180:---Start--- ***/
			String toNBIN = "";
			if("Y".equalsIgnoreCase(ConfigurationLoader.getParameters(false).getProperty("Search_NBin"))) {
				
				List<D350038> list = DataUtils.checkNBINorIFSC(tType, mmid2, session);
				if (list == null || list.size() < 1) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.NBIN_NOT_REGISTERED);
					return response;
				}
				toNBIN = list.get(0).getNbin().toString();
			}
			
			/***Commented By Aniket Desai on 21st Aug, 2020 for issue #7180:---End--- ***/
			
			String rrn = DataUtils.getNextRRN();
			
			logger.error("toNBIN::>>" + toNBIN);
			response.setNBin(toNBIN);
			if (tType.equalsIgnoreCase(MSGConstants.P2P_TRANSACTION)) {
				CoreBankingOperationImpl impl = new CoreBankingOperationImpl();
				if (mmid2.trim().substring(0, 4).equalsIgnoreCase(MSGConstants.NBIN)) {
					logger.error("Same Bank P2P transaction.");
					if (destAccNo15digit == null || destAccNo15digit.trim().length() != 15) {
						P2PTransactionEntry.p2pTransactionEntry(mmid1, mob1, mob2, mmid2, Double.valueOf(amount), 0, 0,
								"MBTR", 0, "M1", MSGConstants.INVALID_BEN_ACC_NO, rrn);
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage(MSGConstants.INVALID_BEN_ACC_NO);
						return response;
					}
					if (accNo15digit.equalsIgnoreCase(destAccNo15digit)) {
						P2PTransactionEntry.p2pTransactionEntry(mmid1, mob1, mob2, mmid2, Double.valueOf(amount), 0, 0,
								"MBTR", 0, "94", MSGConstants.SAME_ACC_TRANSFER_NOT_ALLOWED, rrn);
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage(MSGConstants.SAME_ACC_TRANSFER_NOT_ALLOWED);
						return response;
					}
					D009022 sourceAccount = DataUtils.getAccount(destAccNo15digit.trim(), session);
					if (sourceAccount == null) {
						P2PTransactionEntry.p2pTransactionEntry(mmid1, mob1, mob2, mmid2, Double.valueOf(amount), 0, 0,
								"MBTR", 0, "M1", MSGConstants.ACCOUNT_NOT_FOUND, rrn);
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage(MSGConstants.INVALID_BEN_ACC_NO);
						return response;
					}
					TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount,
							String.valueOf(amount), "C");
					if (res == null) {
						P2PTransactionEntry.p2pTransactionEntry(mmid1, mob1, mob2, mmid2, Double.valueOf(amount), 0, 0,
								"MBTR", 0, "94", MSGConstants.TRANSACTION_VALIDATION_FAILS, rrn);
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage(MSGConstants.TRANSACTION_VALIDATION_FAILS);
						return response;
					}
					if (res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
						P2PTransactionEntry.p2pTransactionEntry(mmid1, mob1, mob2, mmid2, Double.valueOf(amount), 0, 0,
								"MBTR", 0, "94", res.getErrorMsg(), rrn);
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage(res.getErrorMsg());
						return response;
					}
					/***Added By Aniket Desai on 30th Aug, 2019 for Narration change Requested By Shailesh Sir:---Start--- ***/
					
					logger.error("Account NO 32 Digit::>>" + acctno32digit);
					logger.error("Branch Code::>>" + lbrcode);
					String benAcctno32digit = AccountDetailsUtil.get32DigitAcctNo(destAccNo15digit);
					int BenBrcode = Integer.parseInt(destAccNo15digit.substring(0, 3));
					
					String particular= destAccNo15digit;
					/***Commented By Aniket Desai on 21th Aug, 2020 for Narration change for issue 63623 ***/
						//	BenBrcode+"/"+benAcctno32digit.substring(0, 8).trim()+"/"+String.valueOf(Integer.parseInt(benAcctno32digit.substring(16, 24)));
						//+" Rem:"+lbrcode+"/"+acctno32digit.substring(0, 8).trim()+"/"+String.valueOf(Integer.parseInt(acctno32digit.substring(16, 24)));
					
					/***Added By Aniket Desai on 30th Aug, 2019 for Narration change Requested By Shailesh Sir:---End--- ***/
					
					String resp = impl.fundTransferOtherBranch(accNo15digit.trim(), destAccNo15digit.trim(),
							MSGConstants.TRANS_TYPE,
							"IMPS/P2P/" + narration.trim()+ "/"+ rrn.trim() + "/" + particular + "/"+ DateUtil.getcurrentDateStringDDMMYYYY(),
							"IMPS/P2P/" + narration.trim()+ "/"+ rrn.trim() + "/" + accNo15digit + "/"+ DateUtil.getcurrentDateStringDDMMYYYY(),
							Double.valueOf(amount), rrn, mob1, mmid1, mob2, mmid2, tType, session);
					impl = null;
					if (resp.trim().equalsIgnoreCase(ResponseCodes.SUCCESS)) {
						response.setResponse(MSGConstants.SUCCESS);
						response.setErrorMessage(MSGConstants.SUCCESS_MSG);
						response.setRrnNo(rrn);
						response.setErrorCode(resp);
						response.setAccountNo(destAccNo15digit);
						response.setNBin(toNBIN);
						D009022 acc = DataUtils.getAccount(accNo15digit);
						if (acc != null)
							response.setNickNameDebit(
									acc.getLongName().trim().length() > 9 ? acc.getLongName().trim().substring(0, 9)
											: "NONICKNAME");
						else
							response.setAccountNo("NONICKNAME");
						t.commit();
						return response;
					} else {
						if (t.isActive())
							t.rollback();
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
						response.setRrnNo(rrn);
						response.setErrorCode(resp);
						return response;
					}
				}
				/***Added By Aniket Desai on 30th Aug, 2019 for Narration change, requested By Shailesh Sir:---Start--- ***/
				String particular =destAccNo15digit;
					/***Commented By Aniket Desai on 21th Aug, 2020 for Narration change for issue 63623 ***/
						//+" Rem:"+lbrcode+"/"+acctno32digit.substring(0, 8).trim()+"/"+String.valueOf(Integer.parseInt(acctno32digit.substring(16, 24)));
				
				/***Added By Aniket Desai on 30th Aug, 2019 for Narration change, requested By Shailesh Sir:---End--- ***/
				
				String resp = impl.fundTransferOtherBank(accNo15digit, MSGConstants.TRANS_TYPE,
						/***Commented and Added By Aniket Desai on 21th Aug, 2020 for Narration change for issue 63623 ***/
						"IMPS/P2P/" + narration.trim()+ "/"+ rrn.trim() + "/" + particular + "/" + mmid2+ "/" + DateUtil.getcurrentDateStringDDMMYYYY(),
						//"IMPS-P2P-" + rrn + "-" + DateUtil.getcurrentDateString() + particular +" "+ narration,
						Double.valueOf(amount), rrn, mob1, mmid1, mob2, mmid2, tType, session);
				impl = null;
				if (resp.trim().equalsIgnoreCase(ResponseCodes.SUCCESS)) {
					response.setResponse(MSGConstants.SUCCESS);
					response.setErrorMessage(MSGConstants.SUCCESS_MSG);
					response.setRrnNo(rrn);
					response.setErrorCode(resp);

					D009022 acc = DataUtils.getAccount(accNo15digit);
					if (acc != null)
						response.setNickNameDebit(
								acc.getLongName().trim().length() > 9 ? acc.getLongName().trim().substring(0, 9)
										: "NONICKNAME");
					else
						response.setAccountNo("NONICKNAME");
					t.commit();
					return response;
				} else {
					if (t.isActive())
						t.rollback();
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
					response.setRrnNo(rrn);
					response.setErrorCode(resp);
					return response;
				}
			} else if (tType.equalsIgnoreCase(MSGConstants.P2A_TRANSACTION)) {
				CoreBankingOperationImpl impl = new CoreBankingOperationImpl();
				
				int ifscCheckLength = Integer.parseInt(ConfigurationLoader.getParameters(false).getProperty("IFSC_LENGTH"));
				
				String ifsc2 = ConfigurationLoader.getParameters(false).getProperty("IFSC1");
				if(ifsc2!=null && !"".equalsIgnoreCase(ifsc2))
					ifsc2 = ifsc2.substring(0, ifscCheckLength);
				
				if (mmid2.trim().substring(0, ifscCheckLength).equalsIgnoreCase(MSGConstants.IFSC_CODE.substring(0, ifscCheckLength))
						|| mmid2.trim().substring(0, ifscCheckLength).equalsIgnoreCase(ifsc2)){
				
				//if (mmid2.trim().substring(0, 8).equalsIgnoreCase(MSGConstants.IFSC_CODE) 
						/**Added By Aniket Desai on 13th Dec, 2019 for Allowing 2 IFSC code **/
						//|| mmid2.trim().substring(0, 8).equalsIgnoreCase(ConfigurationLoader.getParameters(false).getProperty("IFSC1"))){
					logger.error("Same Bank P2A transaction.");
					if (destAccNo15digit == null || destAccNo15digit.trim().length() != 15) {
						P2ATransaction.p2aTransactionEntry(mmid1, mob1, mob2, mmid2, Double.valueOf(amount.trim()), 0,
								0, "MBTR", 0, "M1", MSGConstants.INVALID_BEN_ACC_NO, rrn);
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage(MSGConstants.INVALID_BEN_ACC_NO);
						return response;
					}
					if (accNo15digit.equalsIgnoreCase(destAccNo15digit)) {
						P2ATransaction.p2aTransactionEntry(mmid1, mob1, mob2, mmid2, Double.valueOf(amount.trim()), 0,
								0, "MBTR", 0, "94", MSGConstants.SAME_ACC_TRANSFER_NOT_ALLOWED, rrn);
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage(MSGConstants.SAME_ACC_TRANSFER_NOT_ALLOWED);
						return response;
					}
					Criteria criteria2 = session.createCriteria(D946022.class);
					criteria2.add(Restrictions.eq("ifsccd", mmid2));
					List<D946022> lists = criteria2.list();
					if (null == lists || lists.isEmpty()) {
						P2ATransaction.p2aTransactionEntry(mmid1, mob1, mob2, mmid2, Double.valueOf(amount.trim()), 0,
								0, "MBTR", 0, "M1", MSGConstants.INVALID_BEN_IFSC, rrn);
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage(MSGConstants.INVALID_BEN_IFSC);
						return response;
					}
					D009022 sourceAccount = DataUtils.getAccount(destAccNo15digit.trim(), session);
					if (sourceAccount == null) {

						P2ATransaction.p2aTransactionEntry(mmid1, mob1, mob2, mmid2, Double.valueOf(amount.trim()), 0,
								0, "MBTR", 0, "M1", MSGConstants.INVALID_BEN_ACC_NO, rrn);
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage(MSGConstants.INVALID_BEN_ACC_NO);
						return response;
					}
					TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount,
							String.valueOf(amount), "C");
					if (res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
						P2ATransaction.p2aTransactionEntry(mmid1, mob1, mob2, mmid2, Double.valueOf(amount.trim()), 0,
								0, "MBTR", 0, "99", res.getErrorMsg(), rrn);
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage(res.getErrorMsg());
						return response;
					}
					
					
									
					logger.error("Account NO 32 Digit::>>" + acctno32digit);
					logger.error("Branch Code::>>" + lbrcode);
					String remAcctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit);
					int remBrcode = Integer.parseInt(accNo15digit.substring(0, 3));
					D009022 debiteAccount = session.get(D009022.class, new D009022Id(remBrcode,remAcctno32digit));
					
					String benAcctno32digit = AccountDetailsUtil.get32DigitAcctNo(destAccNo15digit);
					int BenBrcode = Integer.parseInt(destAccNo15digit.substring(0, 3));
					D009022 creditAccount = session.get(D009022.class, new D009022Id(BenBrcode,benAcctno32digit));
					D009021 creditProductMaster = session.get(D009021.class, new D009021Id(BenBrcode, benAcctno32digit.substring(0, 8).trim()));
					if(RtgsNeftHostToHostConstants.LOAN.getMessage().contains(creditProductMaster.getModuleType() + "")) {
						logger.error("LOAN Transaction");
						if(creditAccount.getCustNo()!=debiteAccount.getCustNo()) {
							logger.error("Transaction unsuccessful "+creditAccount.getCustNo()+"!="+sourceAccount.getCustNo());
							P2ATransaction.p2aTransactionEntry(mmid1, mob1, mob2, mmid2, Double.valueOf(amount.trim()), 0,
									0, "MBTR", 0, "99", "Loan Fund Transfer to Other Account Is Not Allowed", rrn);
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMessage("Loan Fund Transfer to Other Account Is Not Allowed");
							return response;
						}
					}
					
					/***Added By Aniket Desai on 30th Aug, 2019 for Narration change Requested By Shailesh Sir:---Start--- ***/
					
					
					
					/*String particular= " Benf:"+BenBrcode+"/"+benAcctno32digit.substring(0, 8).trim()+"/"+String.valueOf(Integer.parseInt(benAcctno32digit.substring(16, 24)))
							+" Rem:"+lbrcode+"/"+acctno32digit.substring(0, 8).trim()+"/"+String.valueOf(Integer.parseInt(acctno32digit.substring(16, 24)));*/
					/***Added By Aniket Desai on 30th Aug, 2019 for Narration change Requested By Shailesh Sir:---End--- ***/
					String resp = impl.fundTransferOtherBranch(accNo15digit.trim(), destAccNo15digit.trim(),
							MSGConstants.TRANS_TYPE,
							/***Commented and Addedd By Aniket Desai on 21th Aug, 2020 for Narration change for issue 63623 ***/
							"IMPS/P2A/"   + destAccNo15digit + "/" + mmid2+ "/" + DateUtil.getcurrentDateStringDDMMYYYY()+ "/"+ narration.trim()+ "/"+ rrn.trim(),
							"IMPS/P2A/" + accNo15digit + "/" + mmid2+ "/" + DateUtil.getcurrentDateStringDDMMYYYY()+ "/"+ narration.trim()+ "/"+ rrn.trim(),
							//"IMPS-P2A-" + rrn + "-" + DateUtil.getcurrentDateString() +particular+ narration,
							Double.valueOf(amount), rrn, mob1, mmid1, mob2, mmid2, tType, session);
					impl = null;
					if (resp.trim().equalsIgnoreCase(ResponseCodes.SUCCESS)) {

						response.setResponse(MSGConstants.SUCCESS);
						response.setErrorMessage(MSGConstants.SUCCESS_MSG);
						response.setRrnNo(rrn);
						response.setErrorCode(resp);
						response.setNickNameCredit(creditAccount.getLongName().trim());
						// response.setNBin(toNBIN);
						
						D009022 acc = DataUtils.getAccount(accNo15digit);
						/***Added By Aniket Desai on 14 July, 2020 for Balance Validation at the End ***/
						TransactionValidationResponse validateBalance = validateBalance(acc, amount,session);
						if (validateBalance != null && validateBalance.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMessage(validateBalance.getErrorMsg());
							response.setRrnNo(rrn);
							response.setErrorCode(validateBalance.getRespCode());
							return response;
						}
						
						if (acc != null)
							response.setNickNameDebit(
									acc.getLongName().trim().length() > 9 ? acc.getLongName().trim().substring(0, 9)
											: "NONICKNAME");
						else
							response.setAccountNo("NONICKNAME");
						t.commit();
						return response;
					} else {
						if (t.isActive())
							t.rollback();
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
						response.setRrnNo(rrn);
						response.setErrorCode(resp);
						return response;
					}
				}
				
				/***Added By Aniket Desai on 30th Aug, 2019 for Narration change Requested By Shailesh Sir:---Start--- ***/
				String particular = " Rem:"+lbrcode+"/"+acctno32digit.substring(0, 8).trim()+"/"+String.valueOf(Integer.parseInt(acctno32digit.substring(16, 24)))
								+" Benf:"+destAccNo15digit;
				/***Added By Aniket Desai on 30th Aug, 2019 for Narration change Requested By Shailesh Sir:---End--- ***/
				
				String resp = impl.fundTransferOtherBank(accNo15digit, MSGConstants.TRANS_TYPE,
						/***Commented By Aniket Desai on 21th Aug, 2020 for Narration change for issue 63623 ***/
						//"IMPS/P2A/" + narration.trim()+ "/"+ rrn.trim() + "/" + destAccNo15digit + "/"+ DateUtil.getcurrentDateStringDDMMYYYY(),
						//"IMPS-P2A-" + rrn + "-" + DateUtil.getcurrentDateString() + particular + narration,
						"IMPS/P2A/" + DateUtil.getcurrentDateStringDDMMYYYY() + "/"+ destAccNo15digit + "/"+ rrn.trim() + "/" + narration.trim() ,
						
						Double.valueOf(amount), rrn, mob1, mmid1, mob2, mmid2, tType, session);
				impl = null;
				
				
				if (resp.trim().equalsIgnoreCase(ResponseCodes.SUCCESS)) {
					
					response.setResponse(MSGConstants.SUCCESS);
					response.setErrorMessage(MSGConstants.SUCCESS_MSG);
					response.setRrnNo(rrn);
					response.setErrorCode(resp);

					D009022 acc = DataUtils.getAccount(accNo15digit);
					/***Added By Aniket Desai on 14 July, 2020 for Balance Validation at the End ***/
					TransactionValidationResponse validateBalance = validateBalance(acc, amount, session);
					if (validateBalance != null && validateBalance.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage(validateBalance.getErrorMsg());
						response.setRrnNo(rrn);
						response.setErrorCode(validateBalance.getRespCode());
						t.rollback();
						return response;
					}
					
					if (acc != null) {
						response.setNickNameDebit(
								acc.getLongName().trim().length() > 9 ? acc.getLongName().trim().substring(0, 9)
										: "NONICKNAME");
						logger.error("Final Balance:="+acc.getActTotBalFcy());
					}else
						response.setAccountNo("NONICKNAME");
					
					t.commit();
					return response;

				} else {
					if (t.isActive())
						t.rollback();
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
					response.setRrnNo(rrn);
					response.setErrorCode(resp);
					return response;
				}
			}
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_TRANSACTION_TYPE);
			return response;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			if (t.isActive())
				t.rollback();
			//session.close();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		} finally {
			session.close();
			session = null;
			t = null;
		}
	}

	public static IMPSTransactionResponse initiateBillPayement(String accNo15digit, String destAccNo15digit,
			String amount, String narration, String mob1, String mmid1, String mob2, String mmid2, String tType,
			IMPSTransactionRequest request) {
		logger.error("<<<<<<<<<======== initiateBillPayement.service =========>>>>>>>>>>>>");
		logger.error("accNo15digit::>>" + accNo15digit + " destAccNo15digit::>>" + destAccNo15digit + " Amount::>>>"
				+ amount + " narration::>>" + narration + " mob1::>>>" + mob1 + " mmid1::>>" + mmid1 + " mob2::>>"
				+ mob2 + " mmid2::>>" + mmid2);

		IMPSTransactionResponse response = new IMPSTransactionResponse();
		Session session = HBUtil.getSessionFactory().openSession();
		List<D350038> list = DataUtils.checkNBINorIFSC(tType, mmid2, session);
		if (list == null || list.size() < 1) {
			session.close();
			session = null;
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.NBIN_NOT_REGISTERED);
			return response;
		}
		String rrn = "";
		if("BillDesk".equalsIgnoreCase(request.getOperator()) || "VPA".equalsIgnoreCase(request.getOperator())) {
			if(request.getRRNNo()==null || request.getRRNNo().trim().equalsIgnoreCase("")) {
				response.setResponse(MSGConstants.ERROR);
				response.setResponse(MSGConstants.INVALID_RRN_NO);
				return response;
			}
			logger.error("BillDesk RRN No::>>" + request.getRRNNo());
			rrn = request.getRRNNo();
			Criteria criteria = session.createCriteria(Billpayment.class);
			criteria.add(Restrictions.eq("id.lbrcode", Integer.valueOf(accNo15digit.substring(0, 3))));
			criteria.add(Restrictions.eq("operator",
					request.getOperator() != null ? request.getOperator().trim() : request.getOperator()));
			criteria.add(Restrictions.eq("drcr", MSGConstants.DR));
			criteria.add(Restrictions.eq("amount", Double.valueOf(amount)));
			criteria.add(Restrictions.eq("responsecode", "00"));
			criteria.add(Restrictions.eq("id.rrrnno", rrn));
			List<Billpayment> list1 = criteria.list();
			if (!list1.isEmpty()) {
				response.setResponse(MSGConstants.ERROR);
				response.setResponse(MSGConstants.DUPLICATE_TRN);
				return response;
			}
			
		}else
			rrn = DataUtils.getNextRRN();
		
		String toNBIN = "" + list.get(0).getNbin();
		logger.error("toNBIN::>>" + toNBIN);
		response.setNBin(toNBIN);
		if (tType.equalsIgnoreCase(MSGConstants.BILLPAY)) {
			CoreBankingOperationImpl impl = new CoreBankingOperationImpl();
			logger.error("Other Merchant transaction");
			try {
				String resp = "";
				if("VPA".equalsIgnoreCase(request.getOperator())) {
					resp = impl.initiateBillPayment(
							accNo15digit, MSGConstants.TRANS_TYPE, "VPA-" + request.getConsumerNo().trim() + "-"
									+ DateUtil.getcurrentDateString() + "-" + narration,
							Double.valueOf(amount), rrn, mob1, mmid1, mob2, mmid2, request.getOperator(), request);
				}else
				 resp = impl.initiateBillPayment(
						accNo15digit, MSGConstants.TRANS_TYPE, "IMPS-P2M-" + request.getConsumerNo().trim() + "-"
								+ DateUtil.getcurrentDateString() + "-" + narration,
						Double.valueOf(amount), rrn, mob1, mmid1, mob2, mmid2, request.getOperator(), request);
				impl = null;
				if (resp.trim().equalsIgnoreCase(ResponseCodes.SUCCESS)) {
					response.setResponse(MSGConstants.SUCCESS);
					response.setErrorMessage(MSGConstants.SUCCESS_MSG);
					response.setRrnNo(rrn);
					response.setErrorCode(resp);
					D009022 acc = DataUtils.getAccount(accNo15digit);
					if (acc != null)
						response.setNickNameDebit(
								acc.getLongName().trim().length() > 9 ? acc.getLongName().trim().substring(0, 9)
										: "NONICKNAME");
					else
						response.setAccountNo("NONICKNAME");
					return response;
				} else {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
					response.setRrnNo(rrn);
					response.setErrorCode(resp);
					return response;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				logger.error("Exception:-",e);
				impl = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
				response.setRrnNo(rrn);
				response.setErrorCode(ResponseCodes.EXCEPTION_OCCURED);
				return response;
			}
		}
		response.setResponse(MSGConstants.ERROR);
		response.setErrorMessage(MSGConstants.INVALID_TRANSACTION_TYPE);
		return response;
	}

	public static IMPSTransactionResponse loadBalancePrepaidCard(D009022 d009022, String amount, String narration,
			String mob1, String tType, IMPSTransactionRequest request, D390077 list) {
		IMPSTransactionResponse response = new IMPSTransactionResponse();
		if (null == d009022) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
			return response;
		}
		logger.error("<<<<<<<<<======== loadBalancePrepaidCard.service =========>>>>>>>>>>>>");
		logger.error("Branch Code::>>" + d009022.getId().getLbrCode() + " accNo::>>" + d009022.getId().getPrdAcctId()
				+ " Amount::>>>" + amount + " narration::>>" + narration + " mob1::>>>" + mob1);
		String rrn = DataUtils.getNextRRN();
		if (tType.equalsIgnoreCase(MSGConstants.PREPAID_CARD)) {
			logger.error("Prepaid card load balance transaction");
			try {
				HashMap<String, String> result = VoucherCommon.prepaidCardVoucherEntry(d009022.getId().getLbrCode(),
						d009022.getId().getPrdAcctId(), MSGConstants.PREPAID, narration, Double.valueOf(amount.trim()),
						rrn);
				logger.error("Result:>>>" + result);
				if (null != result) {
					if (result.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
						PrepaidCardLoadBalanceEntry.storePrepaidcardEntry(d009022.getId().getLbrCode(),
								d009022.getId().getPrdAcctId(), request.getRemitterMobile(), request.getTransAmt(), "0",
								list.getId().getCardAlias().trim(), MSGConstants.SUCCESS_MSG, MSGConstants.SUCCESS_MSG,
								MSGConstants.SUCCESS_MSG, ResponseCodes.SUCCESS, rrn);
						response.setResponse(MSGConstants.SUCCESS);
						response.setErrorMessage(MSGConstants.SUCCESS_MSG);
						response.setRrnNo(rrn);
						response.setAccountNo(list.getRblAccNo());
						response.setCustNo(list.getRblCustId());
						return response;
					} else {
						PrepaidCardLoadBalanceEntry.storePrepaidcardEntry(d009022.getId().getLbrCode(),
								d009022.getId().getPrdAcctId(), request.getRemitterMobile(), request.getTransAmt(), "0",
								list.getId().getCardAlias().trim(), MSGConstants.TRANSACTION_DECLINED,
								MSGConstants.TRANSACTION_DECLINED, MSGConstants.TRANSACTION_DECLINED,
								ResponseCodes.SYSTEM_ERROR, rrn);
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
						response.setRrnNo(rrn);
						return response;
					}
				} else {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
					response.setRrnNo(rrn);
					return response;
				}
			} catch (Exception e) {
				e.printStackTrace();
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
				response.setRrnNo(rrn);
				response.setErrorCode(ResponseCodes.EXCEPTION_OCCURED);
				return response;
			}
		}
		response.setResponse(MSGConstants.ERROR);
		response.setErrorMessage(MSGConstants.INVALID_TRANSACTION_TYPE);
		return response;
	}

	public static IMPSTransactionResponse initiateIMPSCreditTransaction(String accNo15digit, String destAccNo15digit,
			String amount, String narration, String mob1, String mmid1, String mob2, String mmid2, String tType,
			String rrn, String stan) {
		logger.error("<<<<<<====== initiateIMPSCreditTransaction.service() =============>>>>>>>");
		logger.error("accNo15digit::>>" + accNo15digit + " destAccNo15digit::>>" + destAccNo15digit + " Amount::>>>"
				+ amount + " narration::>>" + narration + " mob1::>>>" + mob1 + " mmid1::>>" + mmid1 + " mob2::>>"
				+ mob2 + " mmid2::>>" + mmid2);
		IMPSTransactionResponse response = new IMPSTransactionResponse();
		if (rrn == null || rrn.trim().length() != 12) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_RRN_NO);
			response.setErrorCode(ResponseCodes.NO_RECORD_FOUND);
			return response;
		}
		D009022 d009022 = DataUtils.getAccount(accNo15digit);
		if (null == d009022) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
			response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
			return response;
		}
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		Criteria criteria = session.createCriteria(D350038.class);

		if (tType.equalsIgnoreCase(MSGConstants.P2P_TRANSACTION))
			criteria.add(Restrictions.eq("nbin", Integer.valueOf(mmid2.substring(0, 4))));
		if (tType.equalsIgnoreCase(MSGConstants.P2A_TRANSACTION))
			criteria.add(Restrictions.ilike("ifscode", mmid2.substring(0, 4) + "%"));
		List<D350038> list = criteria.list();
		logger.error("" + list.size());
		session.close();
		session = null;
		t = null;
		if (list == null || list.size() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.NBIN_NOT_REGISTERED);
			response.setErrorCode(ResponseCodes.INVALID_NBIN);
			return response;
		}
		session = HBUtil.getSessionFactory().openSession();
		t = session.beginTransaction();
		if (tType.equalsIgnoreCase(MSGConstants.P2P_TRANSACTION)) {
			try {
				logger.error("Branch code::>>>" + d009022.getId().getLbrCode());
				logger.error("Account No::>>>" + d009022.getId().getPrdAcctId());
				session = null;
				t = null;
				session = HBUtil.getSessionFactory().openSession();
				t = session.beginTransaction();
				HashMap<String, String> hm = new HashMap<>();
				try {
					hm = CommonVoucher.otherBankVoucherEntry(d009022.getId().getLbrCode(),
							d009022.getId().getPrdAcctId(), "IMPS", "IMPS Cr Trn.", Double.valueOf(amount), rrn,
							session, t);
					logger.error("otherBankCreditVoucherEntry Result::>>>" + hm);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (hm != null && !hm.isEmpty() && hm.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
					t.commit();
					session.close();
					session = null;
					t = null;
					response.setResponse(MSGConstants.SUCCESS);
					response.setErrorMessage(MSGConstants.SUCCESS_MSG);
					response.setRrnNo(rrn);
					response.setErrorCode(ResponseCodes.SUCCESS);
					response.setNickNameDebit(String.valueOf(d009022.getLongName().trim().length() > 9
							? d009022.getLongName().trim().substring(0, 9).trim()
							: "NONICKNAME"));
					P2PReversal.reverseTransactionEntry(Integer.valueOf(accNo15digit.substring(0, 3)) + "", mob1, mmid1,
							mob2, mmid2, amount, rrn, "C", Integer.valueOf(hm.get(Code.SETNO)),
							Integer.valueOf(hm.get(Code.SCROLLNO)));
					return response;
				} else {
					session.close();
					session = null;
					t = null;
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
					response.setRrnNo(rrn);
					response.setErrorCode("");
					return response;
				}
			} catch (Exception e) {
				e.printStackTrace();
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
				response.setRrnNo(rrn);
				response.setErrorCode(ResponseCodes.EXCEPTION_OCCURED);
				return response;
			}
		} else if (tType.equalsIgnoreCase(MSGConstants.P2A_TRANSACTION)) {
			logger.error("Other bank transaction");
			try {

				HashMap<String, String> hm = CommonVoucher.otherBankCreditVoucherEntry(d009022.getId().getLbrCode(),
						d009022.getId().getPrdAcctId(), MSGConstants.TRANS_TYPE, "IMPS P2A/" + narration,
						Double.valueOf(amount), rrn, session, t);
				if (hm != null && !hm.isEmpty() && hm.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
					t.commit();
					session.close();
					session = null;
					t = null;
					response.setResponse(MSGConstants.SUCCESS);
					response.setErrorMessage(MSGConstants.SUCCESS_MSG);
					response.setRrnNo(rrn);
					response.setErrorCode(ResponseCodes.SUCCESS);
					response.setNickNameDebit(d009022.getLongName() != null ? d009022.getLongName() : "");
					P2AReversal.transactionEntry(d009022.getId().getLbrCode() + "", mob1, mmid1, mob2, mmid2, amount,
							rrn, "C", Integer.valueOf(hm.get(Code.SETNO)), Integer.valueOf(hm.get(Code.SCROLLNO)),
							"MBTR", "00", "SUCCESSFUL TRANSACTION",stan);
					return response;
				} else {
					// t.commit();
					session.close();
					session = null;
					t = null;
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
					response.setRrnNo(rrn);
					response.setErrorCode("");
					return response;
				}
			} catch (Exception e) {
				e.printStackTrace();
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
				response.setRrnNo(rrn);
				response.setErrorCode(ResponseCodes.EXCEPTION_OCCURED);
				return response;
			}
		}
		response.setResponse(MSGConstants.ERROR);
		response.setErrorMessage(MSGConstants.INVALID_TRANSACTION_TYPE);
		return response;
	}

	public static IMPSTransactionResponse reverseIMPSTransaction(String accNo15digit, String destAccNo15digit,
			String amount, String narration, String mob1, String mmid1, String mob2, String mmid2, String tType,
			String rrn, IMPSTransactionRequest request) {
		IMPSTransactionResponse response = new IMPSTransactionResponse();
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		try {
			if (tType.equalsIgnoreCase(MSGConstants.P2P_TRANSACTION)) {
				List<D350036> list = DataUtils.getD350036List(accNo15digit, mob1, mmid1, mob2, mmid2, amount, rrn);
				logger.error("list::>>" + list);
				if (list == null || list.size() < 1) {
					response.setResponse(MSGConstants.ERROR);
					response.setResponse(MSGConstants.TRANSACTION_NOT_FOUND);
					return response;
				}
				CoreBankingOperationImpl impl = new CoreBankingOperationImpl();
				if (mmid2.trim().substring(0, 4).equalsIgnoreCase(MSGConstants.NBIN)) {
					logger.error("Same Bank P2P transaction.");
					if (destAccNo15digit == null || destAccNo15digit.trim().length() != 15) {
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage(MSGConstants.INVALID_BEN_ACC_NO);
						return response;
					}
					String resp = impl.reversefundTransfer(accNo15digit.trim(), destAccNo15digit.trim(),
							MSGConstants.TRANS_TYPE, "IMPS P2P REV/" + narration, Double.valueOf(amount), rrn, mob1,
							mmid1, mob2, mmid2, tType);
					impl = null;
					if (resp.trim().equalsIgnoreCase(ResponseCodes.SUCCESS)) {
						response.setResponse(MSGConstants.SUCCESS);
						response.setErrorMessage(MSGConstants.SUCCESS_MSG);
						response.setRrnNo(rrn);
						response.setErrorCode(resp);
						return response;
					} else {
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
						response.setRrnNo(rrn);
						response.setErrorCode(resp);
						return response;
					}
				}
				String resp = impl.reversedTransactionOtherBank(accNo15digit, MSGConstants.TRANS_TYPE,
						"IMPS P2P REV/" + accNo15digit, Double.valueOf(amount), rrn, mob1, mmid1, mob2, mmid2, tType,
						request);
				impl = null;
				if (resp.trim().equalsIgnoreCase(ResponseCodes.SUCCESS)) {
					t.commit();
					response.setResponse(MSGConstants.SUCCESS);
					response.setErrorMessage(MSGConstants.SUCCESS_MSG);
					response.setRrnNo(rrn);
					response.setErrorCode(resp);
					return response;
				} else {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
					response.setRrnNo(rrn);
					response.setErrorCode(resp);
					return response;
				}

			} else if (tType.equalsIgnoreCase(MSGConstants.P2A_TRANSACTION)) {
				Criteria criteria = session.createCriteria(D350037.class);
				criteria.add(Restrictions.eq("id.lbrCode", Integer.valueOf(accNo15digit.substring(0, 3))));
				//criteria.add(Restrictions.eq("id.batchCd", String.format("%1$-8s", "MBTR")));
				criteria.add(Restrictions.eq("mobNo1", String.format("%1$-20s", mob1)));
				criteria.add(Restrictions.eq("mmid1", String.format("%1$-15s", mmid1)));
				criteria.add(Restrictions.eq("accNo", String.format("%1$-20s", mob2)));
				criteria.add(Restrictions.eq("ifscCd", String.format("%1$-15s", mmid2)));
				criteria.add(Restrictions.eq("tranAmt", Double.valueOf(amount)));
				criteria.add(Restrictions.eq("rrnNo", String.format("%1$-20s", rrn)));
				criteria.add(Restrictions.in("drcr", "D", "DR"));

				List<D350037> list = criteria.list();

				if (list == null || list.size() < 1) {
					response.setResponse(MSGConstants.ERROR);
					response.setResponse(MSGConstants.TRANSACTION_NOT_FOUND);
					return response;
				}

				Criteria criteria1 = session.createCriteria(D350059.class);
				criteria1.add(Restrictions.eq("id.lbrCode", Integer.valueOf(accNo15digit.substring(0, 3))));
				//criteria1.add(Restrictions.eq("id.batchCd", String.format("%1$-8s", "MBTR")));
				criteria1.add(Restrictions.eq("id.mobNo1", String.format("%1$-20s", mob1)));
				criteria1.add(Restrictions.eq("id.mmid1", String.format("%1$-15s", mmid1)));
				criteria1.add(Restrictions.eq("id.accNo", String.format("%1$-20s", mob2)));
				criteria1.add(Restrictions.eq("id.ifscCd", String.format("%1$-15s", mmid2)));
				criteria1.add(Restrictions.eq("id.tranAmt", Double.valueOf(amount).intValue()));
				criteria1.add(Restrictions.eq("id.rrnNo", String.format("%1$-20s", rrn)));
				criteria1.add(Restrictions.in("id.drcr", String.format("%1$-2s", "C"), "CR"));
				List<D350037> list1 = criteria1.list();
				if (list1 != null && list1.size() > 0) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.ALREADY_REVERSED);
					return response;
				}
				CoreBankingOperationImpl impl = new CoreBankingOperationImpl();
				logger.error("P2A Transaction::>>" + mmid2);
				if (mmid2.trim().substring(0, 8).equalsIgnoreCase(MSGConstants.IFSC_CODE)) {
					logger.error("Same Bank P2P tran'saction.");
					if (destAccNo15digit == null || destAccNo15digit.trim().length() != 15) {
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage(MSGConstants.INVALID_BEN_ACC_NO);
						return response;
					}
					D009022 sourceAccount = DataUtils.getAccount(destAccNo15digit.trim());
					if (sourceAccount == null) {
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage(MSGConstants.INVALID_BEN_ACC_NO);
						return response;
					}
					TransactionValidationResponse res = validateAccount(sourceAccount, amount, "C");
					if (res.getResponse().trim().equalsIgnoreCase(MSGConstants.ERROR)) {
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage(MSGConstants.INVALID_BEN_ACC_NO);
						res = null;
						return response;
					}
					String resp = impl.reversefundTransfer(accNo15digit.trim(), destAccNo15digit.trim(),
							MSGConstants.TRANS_TYPE, "IMPS REV P2A/" + narration, Double.valueOf(amount), rrn, mob1,
							mmid1, mob2, mmid2, tType);
					impl = null;
					if (resp.trim().equalsIgnoreCase(ResponseCodes.SUCCESS)) {
						t.commit();
						response.setResponse(MSGConstants.SUCCESS);
						response.setErrorMessage(MSGConstants.SUCCESS_MSG);
						response.setRrnNo(rrn);
						response.setErrorCode(resp);
						return response;
					} else {
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
						response.setRrnNo(rrn);
						response.setErrorCode(resp);
						return response;
					}
				}
				String resp = impl.reversedTransactionOtherBank(accNo15digit, MSGConstants.TRANS_TYPE,
						"IMPS Transaction/" + accNo15digit, Double.valueOf(amount), rrn, mob1, mmid1, mob2, mmid2,
						tType, request);
				impl = null;
				if (resp.trim().equalsIgnoreCase(ResponseCodes.SUCCESS)) {
					t.commit();
					response.setResponse(MSGConstants.SUCCESS);
					response.setErrorMessage(MSGConstants.SUCCESS_MSG);
					response.setRrnNo(rrn);
					response.setErrorCode(resp);

					return response;
				} else {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
					response.setRrnNo(rrn);
					response.setErrorCode(resp);
					return response;
				}

			} else if (tType.equalsIgnoreCase(MSGConstants.BILLPAY)) {
				Criteria criteria = session.createCriteria(Billpayment.class);
				criteria.add(Restrictions.eq("id.lbrcode", Integer.valueOf(accNo15digit.substring(0, 3))));
				//criteria.add(Restrictions.eq("id.batchcode", "MBTR"));
				criteria.add(Restrictions.eq("operator",
						request.getOperator() != null ? request.getOperator().trim() : request.getOperator()));
				criteria.add(Restrictions.eq("drcr", MSGConstants.DR));
				criteria.add(Restrictions.eq("consumerno",
						request.getConsumerNo() != null ? request.getConsumerNo().trim() : request.getConsumerNo()));
				criteria.add(Restrictions.eq("amount", Double.valueOf(amount)));
				criteria.add(Restrictions.eq("id.rrrnno", rrn));
				List<Billpayment> list = criteria.list();
				if (list == null || list.size() < 1) {
					response.setResponse(MSGConstants.ERROR);
					response.setResponse(MSGConstants.TRANSACTION_NOT_FOUND);
					return response;
				}
				CoreBankingOperationImpl impl = new CoreBankingOperationImpl();
				String resp = "";
				if("VPA".equalsIgnoreCase(request.getOperator()))
					resp = impl.reversedTransactionOtherBank(accNo15digit, MSGConstants.TRANS_TYPE,
							"REV-VPA/" + accNo15digit, Double.valueOf(amount), rrn, mob1, mmid1, mob2, mmid2,
							tType, request);
				else
					resp = impl.reversedTransactionOtherBank(accNo15digit, MSGConstants.TRANS_TYPE,
						"IMPS Transaction/" + accNo15digit, Double.valueOf(amount), rrn, mob1, mmid1, mob2, mmid2,
						tType, request);
				impl = null;
				if (resp.trim().equalsIgnoreCase(ResponseCodes.SUCCESS)) {
					t.commit();
					response.setResponse(MSGConstants.SUCCESS);
					response.setErrorMessage(MSGConstants.SUCCESS_MSG);
					response.setRrnNo(rrn);
					response.setErrorCode(resp);
					return response;
				} else {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
					response.setRrnNo(rrn);
					response.setErrorCode(resp);
					return response;
				}
			}
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_TRANSACTION_TYPE);
			return response;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		} finally {
			session.close();
			session = null;
			t = null;
		}
	}

	public static IMPSTransactionResponse creditIMPSTransactionNew(String accNo15digit, String destAccNo15digit,
			String amount, String narration, String mob1, String mmid1, String mob2, String mmid2, String tType,
			String rrn) {
		logger.error("in reverseIMPSTransaction");
		IMPSTransactionResponse response = new IMPSTransactionResponse();
		/***Added By Aniket Desai on 30th Aug, 2019 for Narration change Requested By Shailesh Sir:---Start--- ***/
		String acctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit);
		int lbrcode = Integer.parseInt(accNo15digit.substring(0, 3));
		String particular=accNo15digit;
		/***Added By Aniket Desai on 30th Aug, 2019 for Narration change Requested By Shailesh Sir:---End--- ***/
		if (tType.equalsIgnoreCase(MSGConstants.P2P_TRANSACTION)) {
			CoreBankingOperationImpl impl = new CoreBankingOperationImpl();
			try {
//				IMPS-P2A-043203100006707-30-JAN-2018 
				String resp = impl.creditTransactionOtherBankNew(accNo15digit, MSGConstants.TRANS_TYPE,
						"IMPS-P2P-" + rrn + "-" +particular+ DateUtil.getcurrentDateString()+narration, Double.valueOf(amount), rrn, mob1,
						mmid1, mob2, mmid2, tType);
				impl = null;
				if (resp.trim().equalsIgnoreCase(ResponseCodes.SUCCESS)) {
					response.setResponse(MSGConstants.SUCCESS);
					response.setErrorMessage(MSGConstants.SUCCESS_MSG);
					response.setRrnNo(rrn);
					response.setErrorCode(resp);
					return response;
				} else {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
					response.setRrnNo(rrn);
					response.setErrorCode(resp);
					return response;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				impl = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
				response.setRrnNo(rrn);
				response.setErrorCode(ResponseCodes.EXCEPTION_OCCURED);
				return response;
			}
		} else if (tType.equalsIgnoreCase(MSGConstants.P2A_TRANSACTION)) {
			CoreBankingOperationImpl impl = new CoreBankingOperationImpl();
			logger.error("P2A Transaction::>>" + mmid2);
			try {
				String resp = impl.creditTransactionOtherBankNew(accNo15digit, MSGConstants.TRANS_TYPE,
						"IMPS/"+narration+"/"+particular+"/" + rrn + "/" + DateUtil.getcurrentDateString(), Double.valueOf(amount), rrn, mob1,
						mmid1, mob2, mmid2, tType);
				impl = null;
				if (resp.trim().equalsIgnoreCase(ResponseCodes.SUCCESS)) {
					response.setResponse(MSGConstants.SUCCESS);
					response.setErrorMessage(MSGConstants.SUCCESS_MSG);
					response.setRrnNo(rrn);
					response.setErrorCode(resp);
					return response;
				} else {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
					response.setRrnNo(rrn);
					response.setErrorCode(resp);
					return response;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				/*try {
					TimeUnit.MINUTES.sleep(5);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}*/
				try {
					String resp = impl.creditTransactionOtherBankNew(accNo15digit, MSGConstants.TRANS_TYPE,
							"IMPS-P2A-" + rrn + "-" + DateUtil.getcurrentDateString()+particular+narration, Double.valueOf(amount), rrn, mob1,
							mmid1, mob2, mmid2, tType);
					impl = null;
					if (resp.trim().equalsIgnoreCase(ResponseCodes.SUCCESS)) {
						response.setResponse(MSGConstants.SUCCESS);
						response.setErrorMessage(MSGConstants.SUCCESS_MSG);
						response.setRrnNo(rrn);
						response.setErrorCode(resp);
						return response;
					} else {
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
						response.setRrnNo(rrn);
						response.setErrorCode(resp);
						return response;
					}
				} catch (Exception ex) {
					// TODO: handle exception
					ex.printStackTrace();
					impl = null;
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
					response.setRrnNo(rrn);
					response.setErrorCode(ResponseCodes.EXCEPTION_OCCURED);
					P2AReversal.transactionUpdate(String.valueOf(lbrcode), mob1, mmid1, mob2, mmid2,
							String.valueOf(amount), rrn, "C", 0,0,
							"MBTR", ResponseCodes.EXCEPTION_OCCURED, MSGConstants.TRANSACTION_FAILS);
					return response;
				}
				
				/*response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
				response.setRrnNo(rrn);
				response.setErrorCode(ResponseCodes.EXCEPTION_OCCURED);
				return response;*/
			}
		}
		response.setResponse(MSGConstants.ERROR);
		response.setErrorMessage(MSGConstants.INVALID_TRANSACTION_TYPE);
		return response;
	}

	public static IMPSTransactionResponse creditOtherBankTransaction(String accNo15digit, String destAccNo15digit,
			String amount, String narration, String mob1, String mmid1, String mob2, String mmid2, String tType,
			String rrn) {
		logger.error("in creditOtherBankTransaction");
		IMPSTransactionResponse response = new IMPSTransactionResponse();
		if (tType.equalsIgnoreCase(MSGConstants.P2P_TRANSACTION)) {
			logger.error("In Credit P2P transaction");
			logger.error("Mobile1::>>" + mob1);
			logger.error("Mobile2::>>" + mob2);
			logger.error("MMID1::>>" + mmid1);
			logger.error("MMID2::>>" + mmid2);
			logger.error("Source Account::>>" + accNo15digit);
			logger.error("destAccNo15digit::>>" + destAccNo15digit);
			logger.error("Amount::>>>" + amount);
			CoreBankingOperationImpl impl = new CoreBankingOperationImpl();
			try {
				String resp = impl.creditOtherBankTransaction(accNo15digit, MSGConstants.TRANS_TYPE,
						"IMPS Credit/" + accNo15digit, Double.valueOf(amount), rrn, mob1, mmid1, mob2, mmid2, tType);
				impl = null;
				if (resp.trim().equalsIgnoreCase(ResponseCodes.SUCCESS)) {
					response.setResponse(MSGConstants.SUCCESS);
					response.setErrorMessage(MSGConstants.SUCCESS_MSG);
					response.setRrnNo(rrn);
					response.setErrorCode(resp);
					return response;
				} else {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
					response.setRrnNo(rrn);
					response.setErrorCode(resp);
					return response;
				}
			} catch (Exception e) {
				e.printStackTrace();
				impl = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
				response.setRrnNo(rrn);
				response.setErrorCode(ResponseCodes.EXCEPTION_OCCURED);
				return response;
			}
		} else if (tType.equalsIgnoreCase(MSGConstants.P2A_TRANSACTION)) {
			CoreBankingOperationImpl impl = new CoreBankingOperationImpl();
			logger.error("P2A Transaction::>>" + mmid2);
			try {
				String resp = impl.creditOtherBankTransaction(accNo15digit, MSGConstants.TRANS_TYPE,
						"IMPS Transaction/" + accNo15digit, Double.valueOf(amount), rrn, mob1, mmid1, mob2, mmid2,
						tType);
				impl = null;
				if (resp.trim().equalsIgnoreCase(ResponseCodes.SUCCESS)) {
					response.setResponse(MSGConstants.SUCCESS);
					response.setErrorMessage(MSGConstants.SUCCESS_MSG);
					response.setRrnNo(rrn);
					response.setErrorCode(resp);
					return response;
				} else {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
					response.setRrnNo(rrn);
					response.setErrorCode(resp);
					return response;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				impl = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
				response.setRrnNo(rrn);
				response.setErrorCode(ResponseCodes.EXCEPTION_OCCURED);
				return response;
			}
		}
		response.setResponse(MSGConstants.ERROR);
		response.setErrorMessage(MSGConstants.INVALID_TRANSACTION_TYPE);
		return response;
	}

	public static IMPSTransactionResponse initiateIMPSVerification(String accNo15digit, String destAccNo15digit,
			String amount, String narration, String mob1, String mmid1, String mob2, String mmid2, String tType,
			String rrn, String stan) {
		logger.error("accNo15digit::>>" + accNo15digit);
		logger.error("destAccNo15digit::>>" + destAccNo15digit);
		logger.error("Amount::>>>" + amount);
		logger.error("narration::>>" + narration);
		logger.error("mob1::>>>" + mob1);
		logger.error("mmid1::>>" + mmid1);
		logger.error("mob2::>>" + mob2);
		logger.error("mmid2::>>" + mmid2);
		IMPSTransactionResponse response = new IMPSTransactionResponse();
		if (rrn == null || rrn.trim().length() != 12) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_RRN_NO);
			return response;
		}
		D009022 d009022 = DataUtils.getAccount(accNo15digit);
		if (null == d009022) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
			response.setErrorCode(ResponseCodes.VERIFICATION_RESP_M0);
			return response;
		}
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		Criteria criteria = session.createCriteria(D350038.class);

		if (tType.equalsIgnoreCase(MSGConstants.P2P_TRANSACTION))
			criteria.add(Restrictions.eq("nbin", Integer.valueOf(mmid2.substring(0, 4))));
		if (tType.equalsIgnoreCase(MSGConstants.P2A_TRANSACTION))
			criteria.add(Restrictions.ilike("ifscode", mmid2.substring(0, 4) + "%"));
		List<D350038> list = criteria.list();
		logger.error("" + list.size());
		session.close();
		session = null;
		t = null;
		if (list == null || list.size() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorCode(ResponseCodes.INVALID_NBIN);
			response.setErrorMessage(MSGConstants.NBIN_NOT_REGISTERED);
			return response;
		}
		// String rrn=DataUtils.getNextRRN();
		session = HBUtil.getSessionFactory().openSession();
		t = session.beginTransaction();
		if (tType.equalsIgnoreCase(MSGConstants.P2P_TRANSACTION)) {
			try {
				logger.error("Branch code::>>>" + d009022.getId().getLbrCode());
				logger.error("Account No::>>>" + d009022.getId().getPrdAcctId());
				session = null;
				t = null;
				session = HBUtil.getSessionFactory().openSession();
				t = session.beginTransaction();
				HashMap<String, String> hm = new HashMap<>();
				try {
					hm = CommonVoucher.otherBankCreditVoucherEntry(d009022.getId().getLbrCode(),
							d009022.getId().getPrdAcctId(), "IMPS", "IMPS Cr Trn.", Double.valueOf(amount), rrn,
							session, t);
					logger.error("otherBankCreditVoucherEntry Result::>>>" + hm);
					logger.error("otherBankCreditVoucherEntry Result::>>>" + hm);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}

				logger.error("RRN::>>>>" + rrn);
				logger.error("Transaction Result::>>>" + hm);
				logger.error("hm::>>" + hm);//
				if (hm != null && !hm.isEmpty() && hm.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
					// t.commit();
					// session.close();
					// session=null;
					response.setResponse(MSGConstants.SUCCESS);
					response.setErrorMessage(MSGConstants.SUCCESS_MSG);
					response.setRrnNo(rrn);
					response.setErrorCode("00");
					response.setNickNameDebit(String.valueOf(d009022.getLongName().trim().length() > 9
							? d009022.getLongName().trim().substring(0, 9).trim()
							: "NONICKNAME"));
					P2PReversal.reverseTransactionEntry(Integer.valueOf(accNo15digit.substring(0, 3)) + "", mob1, mmid1,
							mob2, mmid2, amount, rrn, "C", Integer.valueOf(hm.get(Code.SETNO)),
							Integer.valueOf(hm.get(Code.SCROLLNO)));
					return response;
				} else {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
					response.setRrnNo(rrn);
					response.setErrorCode("");
					return response;
				}
			} catch (Exception e) {
				e.printStackTrace();
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
				response.setRrnNo(rrn);
				response.setErrorCode(ResponseCodes.EXCEPTION_OCCURED);
				return response;
			}
		} else if (tType.equalsIgnoreCase(MSGConstants.P2A_TRANSACTION)) {
			logger.error("Other bank transaction");
			try {
				HashMap<String, String> hm = CommonVoucher.otherBankCreditVoucherEntry(d009022.getId().getLbrCode(),
						d009022.getId().getPrdAcctId(), MSGConstants.TRANS_TYPE, "IMPS P2A/" + narration,
						Double.valueOf(amount), rrn, session, t);
				if (hm != null && !hm.isEmpty() && hm.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
					response.setResponse(MSGConstants.SUCCESS);
					response.setErrorMessage(MSGConstants.SUCCESS_MSG);
					response.setRrnNo(rrn);
					response.setErrorCode("00");
					response.setNickNameDebit(d009022.getLongName() != null ? d009022.getLongName() : "");
					P2AReversal.transactionEntry(d009022.getId().getLbrCode() + "", mob1, mmid1, mob2, mmid2, amount,
							rrn, "C", Integer.valueOf(hm.get(Code.SETNO)), Integer.valueOf(hm.get(Code.SCROLLNO)),
							"MBTR", "00", "SUCCESSFUL TRANSACTION", stan);
					return response;
				} else {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
					response.setRrnNo(rrn);
					response.setErrorCode("");
					return response;
				}
			} catch (Exception e) {
				e.printStackTrace();
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
				response.setRrnNo(rrn);
				response.setErrorCode(ResponseCodes.EXCEPTION_OCCURED);
				return response;
			}
		}
		response.setResponse(MSGConstants.ERROR);
		response.setErrorMessage(MSGConstants.INVALID_TRANSACTION_TYPE);
		return response;
	}

	public static IMPSTransactionResponse creditIMPSTransaction(String accNo15digit, String destAccNo15digit,
			String amount, String narration, String mob1, String mmid1, String mob2, String mmid2, String tType,
			String rrn, IMPSTransactionRequest request) {
		logger.error("in reverseIMPSTransaction");
		IMPSTransactionResponse response = new IMPSTransactionResponse();
		if (tType.equalsIgnoreCase(MSGConstants.P2P_TRANSACTION)) {
			Session session = HBUtil.getSessionFactory().openSession();
			Transaction t = session.beginTransaction();

			CoreBankingOperationImpl impl = new CoreBankingOperationImpl();

			if (mmid2.trim().substring(0, 4).equalsIgnoreCase(MSGConstants.NBIN)) {
				logger.error("Same Bank P2P transaction.");
				if (destAccNo15digit == null || destAccNo15digit.trim().length() != 15) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_BEN_ACC_NO);
					return response;
				}
				String resp = impl.reversefundTransfer(accNo15digit.trim(), destAccNo15digit.trim(),
						MSGConstants.TRANS_TYPE, "IMPS P2P REV/" + narration, Double.valueOf(amount), rrn, mob1, mmid1,
						mob2, mmid2, tType);
				impl = null;
				if (resp.trim().equalsIgnoreCase(ResponseCodes.SUCCESS)) {
					response.setResponse(MSGConstants.SUCCESS);
					response.setErrorMessage(MSGConstants.SUCCESS_MSG);
					response.setRrnNo(rrn);
					response.setErrorCode(resp);
					return response;
				} else {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
					response.setRrnNo(rrn);
					response.setErrorCode(resp);
					return response;
				}
			}
			try {
				String resp = impl.reversedTransactionOtherBank(accNo15digit, MSGConstants.TRANS_TYPE,
						"IMPS P2P REV/" + accNo15digit, Double.valueOf(amount), rrn, mob1, mmid1, mob2, mmid2, tType,
						request);
				impl = null;
				if (resp.trim().equalsIgnoreCase(ResponseCodes.SUCCESS)) {
					response.setResponse(MSGConstants.SUCCESS);
					response.setErrorMessage(MSGConstants.SUCCESS_MSG);
					response.setRrnNo(rrn);
					response.setErrorCode(resp);
					return response;
				} else {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
					response.setRrnNo(rrn);
					response.setErrorCode(resp);
					return response;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				impl = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
				response.setRrnNo(rrn);
				response.setErrorCode(ResponseCodes.EXCEPTION_OCCURED);
				return response;
			}
		} else if (tType.equalsIgnoreCase(MSGConstants.P2A_TRANSACTION)) {
			Session session = HBUtil.getSessionFactory().openSession();
			Transaction t = session.beginTransaction();
			Criteria criteria = session.createCriteria(D350037.class);
			criteria.add(Restrictions.eq("id.lbrCode", Integer.valueOf(accNo15digit.substring(0, 3))));
			//criteria.add(Restrictions.eq("id.batchCd", "MBTR"));
			criteria.add(Restrictions.eq("mobNo1", mob1));
			criteria.add(Restrictions.eq("mmid1", mmid1));
			criteria.add(Restrictions.eq("accNo", mob2));
			criteria.add(Restrictions.eq("ifscCd", mmid2));
			criteria.add(Restrictions.eq("tranAmt", Double.valueOf(amount)));
			criteria.add(Restrictions.eq("rrnNo", rrn));
			List<D350036> list = criteria.list();
			t.commit();
			session.close();
			session = null;
			logger.error("list::>>" + list);
			if (list == null || list.size() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setResponse(MSGConstants.TRANSACTION_NOT_FOUND);
				return response;
			}
			CoreBankingOperationImpl impl = new CoreBankingOperationImpl();
			logger.error("P2A Transaction::>>" + mmid2);
			if (mmid2.trim().substring(0, 8).equalsIgnoreCase(MSGConstants.IFSC_CODE)) {
				logger.error("Same Bank P2P tran'saction.");
				if (destAccNo15digit == null || destAccNo15digit.trim().length() != 15) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_BEN_ACC_NO);
					return response;
				}
				D009022 sourceAccount = DataUtils.getAccount(destAccNo15digit.trim());
				if (sourceAccount == null) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_BEN_ACC_NO);
					return response;
				}
				TransactionValidationResponse res = validateAccount(sourceAccount, amount, "C");
				if (res.getResponse().trim().equalsIgnoreCase(MSGConstants.ERROR)) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_BEN_ACC_NO);
					res = null;
					return response;
				}
				String resp = impl.reversefundTransfer(accNo15digit.trim(), destAccNo15digit.trim(),
						MSGConstants.TRANS_TYPE, "IMPS REV P2A/" + narration, Double.valueOf(amount), rrn, mob1, mmid1,
						mob2, mmid2, tType);
				impl = null;
				if (resp.trim().equalsIgnoreCase(ResponseCodes.SUCCESS)) {
					response.setResponse(MSGConstants.SUCCESS);
					response.setErrorMessage(MSGConstants.SUCCESS_MSG);
					response.setRrnNo(rrn);
					response.setErrorCode(resp);
					return response;
				} else {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
					response.setRrnNo(rrn);
					response.setErrorCode(resp);
					return response;
				}
			}
			try {
				String resp = impl.reversedTransactionOtherBank(accNo15digit, MSGConstants.TRANS_TYPE,
						"IMPS Transaction/" + accNo15digit, Double.valueOf(amount), rrn, mob1, mmid1, mob2, mmid2,
						tType, request);
				impl = null;
				if (resp.trim().equalsIgnoreCase(ResponseCodes.SUCCESS)) {
					response.setResponse(MSGConstants.SUCCESS);
					response.setErrorMessage(MSGConstants.SUCCESS_MSG);
					response.setRrnNo(rrn);
					response.setErrorCode(resp);
					return response;
				} else {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
					response.setRrnNo(rrn);
					response.setErrorCode(resp);
					return response;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				impl = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
				response.setRrnNo(rrn);
				response.setErrorCode(ResponseCodes.EXCEPTION_OCCURED);
				return response;
			}
		}
		response.setResponse(MSGConstants.ERROR);
		response.setErrorMessage(MSGConstants.INVALID_TRANSACTION_TYPE);
		return response;
	}

	public static void main(String[] args) {
		logger.error(updateBillPaymentRefNo(3, "SB      000000000000670700000000", "12345", "Airtel_prepaid", 100,
				"888888888888", "123456789"));
		;
		// D009042
		// doMPOSDepositTransaction("003001000006707", "", "BR", "100",
		// "123456789101");
		// D009022 sourceAccount = DataUtils.getAccount("012001400000024");
		/*
		 * Session session=HBUtil.getSessionFactory().openSession(); Criteria
		 * criteria=session.createCriteria(D350036.class);
		 * criteria.add(Restrictions.eq("id.lbrCode", Integer.valueOf("3")));
		 * criteria.add(Restrictions.eq("id.batchCd", "MBTR"));
		 * criteria.add(Restrictions.eq("mobNo1", "918691911199"));
		 * criteria.add(Restrictions.eq("mmid1", "8307001"));
		 * criteria.add(Restrictions.eq("mobNo2","919920365867"));
		 * criteria.add(Restrictions.eq("mmid2","8307001"));
		 * criteria.add(Restrictions.eq("tranAmt", Double.valueOf("100")));
		 * criteria.add(Restrictions.eq("rrnNo", "702313000012")); List<D350036>
		 * list=criteria.list(); logger.error(""+list.size());
		 * 
		 * session.close(); session=null;
		 */
		// logger.error("Account ::>>" + validateAccount(sourceAccount,
		// "50500.1", "D").getErrorMsg());
		;
	}
	/***Modified By Aniket Desai for Stop Payment ***/
	public static OtherChannelServiceResponse stopChequePayment(String custNo, String mmid, String chequeNo,
			String remark, String accNo15digit, String channel) {
		OtherChannelServiceResponse stopPaymentRes = new OtherChannelServiceResponse();
		try {
			int lbrCode = 0;
			String acctNo = "";
			D009022 drAccount = DataUtils.getAccount(accNo15digit);
			if (null == drAccount) {
				logger.error(custNo + " Account does not exist.");
				stopPaymentRes.setResponse(MSGConstants.ERROR);
				stopPaymentRes.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
				return stopPaymentRes;
			}
			TransactionValidationResponse res = TransactionServiceImpl.validateAccount(drAccount, String.valueOf("100"),
					"A");
			logger.error("res::>>>" + res);
			if (null == res) {
				stopPaymentRes.setResponse(MSGConstants.ERROR);
				stopPaymentRes.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
				return stopPaymentRes;
			}
			if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
				stopPaymentRes.setResponse(MSGConstants.ERROR);
				stopPaymentRes.setErrorMessage(res.getErrorMsg());
				return stopPaymentRes;
			}
			lbrCode = drAccount.getId().getLbrCode();
			acctNo = drAccount.getId().getPrdAcctId();
			String productNo = DataUtils.getProductCode(acctNo).trim();
			D009021 drProductMaster = DataUtils.getProductMaster(String.valueOf(lbrCode), productNo);
			IMPSTransactionResponse drProdTrnxRes = DataUtils.productTrnxValidations(drProductMaster, "Debit");
			if (!drProdTrnxRes.isValid()) {
				logger.error(drProdTrnxRes.getErrorMessage());
				stopPaymentRes.setResponse(MSGConstants.ERROR);
				stopPaymentRes.setErrorMessage(drProdTrnxRes.getErrorMessage());
				stopPaymentRes.setOutput(new String[] { "E" });
				return stopPaymentRes;
			}
			D009011 customer = RequestServiceImpl.getCustDetailsMaster(String.valueOf(drAccount.getCustNo()));
			if (null == customer) {
				logger.error("Invalid Customer Number.");
				stopPaymentRes.setResponse(MSGConstants.ERROR);
				stopPaymentRes.setErrorMessage(MSGConstants.INVALID_CUSTOMER);
				return stopPaymentRes;
			}

			if (customer.getDbtrAuthDone() != Byte.valueOf("1") || customer.getDbtrAuthNeeded() != Byte.valueOf("0")) {
				logger.error("Customer Not Authorised.");
				stopPaymentRes.setResponse(MSGConstants.ERROR);
				stopPaymentRes.setErrorMessage(MSGConstants.CUSTOMER_UNAUTH);
				return stopPaymentRes;
			}
			/*
			 * if (!custNo.equalsIgnoreCase(Long.toString(drAccount.getCustNo()))) { boolean
			 * isJointHolder = DataUtils.checkJointAccountHolder(
			 * Integer.valueOf(drAccount.getCustNo()), Integer .valueOf(custNo),
			 * drAccount.getId() .getLbrCode()); if (!isJointHolder) { logger.
			 * error("This facility is not extended to your account. Kindly contact home branch for further details."
			 * ); session.close(); session=null; t=null;
			 * stopPaymentRes.setResponse(MSGConstants.ERROR);
			 * stopPaymentRes.setErrorMessage(MSGConstants. FASCILITY_NOT_AVAILABILITY);
			 * return stopPaymentRes; } }
			 */

			int insType = Integer.valueOf(
					ConfigurationLoader.getParameters(false).getProperty("modType" + drProductMaster.getModuleType()));
			/** Check module type for OD account */
			D009044 issuedInstruments = DataUtils.fetchIssuedInstruments(lbrCode, acctNo, chequeNo);
			if (null != issuedInstruments) {
				insType = issuedInstruments.getId().getInsType();
			}
			issuedInstruments = DataUtils.getIssuedInstruments(lbrCode, acctNo, insType, chequeNo);
			if (null == issuedInstruments) {
				logger.error("Dear Customer, Instrument Not Found/Instr Does not belong to your AcctID.");
				stopPaymentRes.setResponse(MSGConstants.ERROR);
				stopPaymentRes.setErrorMessage(MSGConstants.CHEQUE_NOT_FOUND);
				return stopPaymentRes;
			} else {
				if (issuedInstruments.getStatus() == Byte.valueOf("2")) {
					logger.error("Entered cheque is already paid, Please visit your branch for more details");
					stopPaymentRes.setResponse(MSGConstants.ERROR);
					stopPaymentRes.setErrorMessage(MSGConstants.CHEQUE_ALREADY_PAID);
					return stopPaymentRes;

				} else if (issuedInstruments.getStatus() == Byte.valueOf("1")) {
					D010010 instrument = DataUtils.getInstDetailsForStop(issuedInstruments.getId().getLbrCode(),
							issuedInstruments.getId().getIssuedTo(), issuedInstruments.getId().getInsType(),
							issuedInstruments.getId().getInstruNo());
					if (null == instrument) {
						logger.error("Record not found in Instrument(D0010010).");
						stopPaymentRes.setResponse(MSGConstants.ERROR);
						stopPaymentRes.setErrorMessage("Record not found in Instrument(D0010010).");
						return stopPaymentRes;
					}
					logger.error("Entered cheque is already stopped, Please visit your branch for more details.");
					stopPaymentRes.setResponse(MSGConstants.ERROR);
					stopPaymentRes.setErrorMessage(
							"Entered cheque is already stopped, Please visit your branch for more details.");
					return stopPaymentRes;
				}
			}
			D010010 instrument = DataUtils.getInstDetailsForStop(lbrCode, acctNo, insType, chequeNo);
			if (null != instrument) {
				if (instrument.getRevokeFlag() != Byte.valueOf("1")) {
					logger.error("Entered cheque is already stopped, Please visit your branch for more details.");
					stopPaymentRes.setResponse(MSGConstants.ERROR);
					stopPaymentRes.setErrorMessage(
							"Entered cheque is already stopped, Please visit your branch for more details.");
					return stopPaymentRes;
				}
				if (instrument.getRevokeFlag() == Byte.valueOf("1")
						&& (instrument.getDbtrAuthDone() != Byte.valueOf("1")
								|| instrument.getDbtrAuthNeeded() != Byte.valueOf("0"))) {
					logger.error("Instr Revoked Or Revoke Not Authorized.");
					stopPaymentRes.setErrorMessage("Instr Revoked Or Revoke Not Authorized.");
					stopPaymentRes.setResponse(MSGConstants.ERROR);
					return stopPaymentRes;
				}
			}
			D001004 system = DataUtils.getSystemParameter(lbrCode, "LASTOPENDATE");
			Date drOperationDate = DateUtil.getDateFromStringNew(system.getValue().trim().substring(1));
			instrument = DataUtils.prepareStopPaymentObj(lbrCode, acctNo, chequeNo, Short.valueOf(insType + ""),
					"STOPED BY IMPS", remark, 10.0);
			// instrumentService.saveInstrumentObject(instrument);

			/** Update status as 1(stop payment) */

			issuedInstruments = null;

			stopPaymentRes.setResponse(MSGConstants.SUCCESS);
			stopPaymentRes.setErrorMessage(MSGConstants.CHEQUE_STOPPED_SUCCESS);
			remark = "Stop Chq Chg/" + remark;
			String mobileNo = " ";
			List<D350078> otherChannelDetailsList = DataUtils.getCustomerListD350078(custNo);
			if (null != otherChannelDetailsList && !otherChannelDetailsList.isEmpty()) {
				mobileNo = otherChannelDetailsList.get(0).getId().getMobileNo();
			}
			/*
			 * D130031 d130031 = DataUtils.getServiceCharges("20");
			 * logger.error("d130031::>>" + d130031); if (d130031 == null) {
			 * logger.error(MSGConstants.SERVICE_CHARGES_NOT_FOUND + " in D130031.");
			 * stopPaymentRes.setResponse(MSGConstants.ERROR);
			 * stopPaymentRes.setErrorMessage(MSGConstants.SERVICE_CHARGES_NOT_FOUND);
			 * return stopPaymentRes; }
			 */
			double amount = 0.0;
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
			D010004 onlineBatch=null;

			D001004 READSCHGREC = DataUtils.getSystemParameter(lbrCode, "READSCHGREC");
			String ChargeType = ConfigurationLoader.getParameters(false).getProperty("CHEQUE_STOP_CHARGE_TYPE").trim();
			if (READSCHGREC.getValue().trim().equalsIgnoreCase("N")) {

				D130001 d130001 = DataUtils.getstopChequeCharges(lbrCode, ChargeType, insType + "");// chgType=2
				// for // stop // cheque logger.error("d130001::>>>" +
				if (d130001 == null) {
					logger.error(MSGConstants.SERVICE_CHARGES_NOT_FOUND + " in D130001.");
					stopPaymentRes.setResponse(MSGConstants.ERROR);
					stopPaymentRes.setErrorMessage(MSGConstants.SERVICE_CHARGES_NOT_FOUND);
					return stopPaymentRes;
				}

				amount = d130001.getFlatRate();
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
				 * =d130031.getEduCesRate() / 100; logger.error("serTaxRate::>>" +
				 * serTaxRate); logger.error("eduCharges::>>" + eduCharges);
				 */
				List<GstChargesMaster> gstChargesMasters = DataUtils.getLatestCharge(ChargeType, drOperationDate);

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

				D130014 d130014 = DataUtils.getChequeChargeType(lbrCode, insType, acctNo.substring(0, 8).trim(),
						Integer.parseInt(ChargeType));// chgType=2
				// for
				// stop
				// cheque
				
				if (d130014 == null) {
					
					logger.error(MSGConstants.SERVICE_CHARGES_NOT_FOUND + " in D130001.");
					stopPaymentRes.setResponse(MSGConstants.ERROR);
					stopPaymentRes.setErrorMessage(MSGConstants.SERVICE_CHARGES_NOT_FOUND);
					return stopPaymentRes;
				}else {
					logger.error("d130014::>>>" + d130014);
				}
				amount = d130014.getFlatRate();
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

				/*
				 * serTaxRate = d130031.getSerTaxRate() / 100; eduCharges =
				 * d130031.getEduCesRate() / 100; logger.error("serTaxRate::>>" +
				 * serTaxRate); logger.error("eduCharges::>>" + eduCharges);
				 */

				List<GstChargesMaster> gstChargesMasters = DataUtils.getLatestCharge(ChargeType, drOperationDate);
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
			D009022 sourceAccount = DataUtils.getAccountMaster(lbrCode, acctNo);// lbrCode,
			// acctNo
			logger.error("sourceAccount ::>>" + sourceAccount);
			if (sourceAccount == null) {
				stopPaymentRes.setResponse(MSGConstants.ERROR);
				stopPaymentRes.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
				return stopPaymentRes;
			}
			TransactionValidationResponse response = TransactionServiceImpl.validateAccount(sourceAccount, "" + osAmt,
					"D");
			boolean voucherFlag = true;
			logger.error("response.getResponse():>>" + response.getResponse());
			logger.error("response.getErrorMsg()::>>>" + response.getErrorMsg());
			
			Session session = HBUtil.getSessionFactory().openSession();
			Transaction t = session.beginTransaction();
			
			if ((stat.equalsIgnoreCase("1") || stat.equalsIgnoreCase("2")) && (response.getResponse().equalsIgnoreCase(MSGConstants.SUCCESS))) {
				logger.error("Batch is open");
				
				String bal = CoreTransactionMPOS.balance(lbrCode, acctNo, osAmt, "D", session);
				logger.error("bal::>>" + bal);
			} else {
				
				logger.error("Batch is not open OR Account is not valid");
				logger.error("Online Batch not Open charges is placed in recovery master. OR Account is not valid");
				logger.error("lbrCode::>>" + lbrCode);
				logger.error("acctNo::>>>" + acctNo);
				D130008Id id = new D130008Id();
				id.setChgType(Byte.valueOf(ChargeType));
				id.setLbrCode(lbrCode);
				id.setPrdAcctId(acctNo);
				D130008 d130008Obj = session.get(D130008.class, id);
				logger.error("d130008Obj::>>" + d130008Obj);
				if (d130008Obj == null) {
					logger.error("d130008Obj is " + d130008Obj);
					D130008 d130008 = prepareRecoveryChgmaster(drOperationDate, osAmt, ChargeType, lbrCode, acctNo);
					session.save(d130008);
					// session.flush();
				} else {
					logger.error("d130008Obj is not null");
					double amt = d130008Obj.getOsAmt() + osAmt;
					d130008Obj.setOsAmt(amt);
					session.update(d130008Obj);
					// session.flush();
				}
				D010010 d010010 = prepareStopChequeEntry(lbrCode, acctNo, String.valueOf(insType), chequeNo,
						drAccount.getLongName(), "Stop Cheque/" + chequeNo, drOperationDate);
				logger.error("stopped");
				session.save(d010010);

				Query query = session.createSQLQuery(
						"UPDATE D009044 SET Status = 1 WHERE LBrCode =" + lbrCode + " AND IssuedTo = '" + acctNo
								+ "' AND InsType = '" + insType + "' AND InstruNo = '" + chequeNo + "'");
				// query2.setParameter("amt", osAmt);
				// query2.setParameter("lbrcode", lbrCode);
				// query2.setParameter("entrydate", drOperationDate);
				// query2.setParameter("batchcd", batch);
				query.executeUpdate();
				t.commit();
				session.close();
				session = null;
				t = null;
				stopPaymentRes.setResponse(MSGConstants.SUCCESS);
				stopPaymentRes.setErrorMessage(
						"Dear customer,as per your request cheque number " + chequeNo + " is stopped successfully.");//
				stopPaymentRes.setTransactionId(DataUtils.getNextRRN());
				return stopPaymentRes;
			}
			
			// logger.error("finalAmt::>>"+finalAmt);
			// logger.error("lastAmt::>>>"+lastAmt);
			
			/*
			 * String staxAccNo = d130031.getSerTaxAcctId(); String sbcAccNo =
			 * d130031.getEduCesAcctId();
			 * 
			 * logger.error("staxAccNo::>>" + staxAccNo);
			 * logger.error("sbcAccNo::>>>" + sbcAccNo);
			 */

			String sgstCreditAcctId = null, cgstCreditAcctId = null;
			String cgstCreditPrdCd = null;
			List<GstChargesMaster> gstChargesMasters = DataUtils.getLatestCharge(ChargeType, drOperationDate);
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
			String rrn = DataUtils.getNextRRN();

					
			
			D009040 d009040Dr = prepareD009040Object(batch, drOperationDate, lbrCode, setNo, scrollNo, scrollNo, acctNo,
					session, osAmt, remark, "DR", rrn);
			d009040Dr.setActivityType("CHRGDR");
			
			VoucherMPOS.updateProductBalances(osAmt, lbrCode, "D", acctNo.substring(0, 8).trim(), session);
			
			D009040 d009040CrSGST = prepareD009040Object(batch, drOperationDate, lbrCode, setNo,
					VoucherMPOS.getNextScrollNo(), scrollNo, sgstCreditAcctId, session, finalAmt, "SGST for " + remark,
					"CR", rrn);
			d009040CrSGST.setCashFlowType("CGSTCHCR");
			D009021 prdSGST = DataUtils.getProductMaster(String.valueOf(lbrCode), sgstCreditAcctId.substring(0, 8).trim());
			if('P'!=prdSGST.getAcctOpenLevel()) {
				String plSGST = CoreTransactionMPOS.balance(lbrCode, sgstCreditAcctId, finalAmt, "C", session);
				logger.error("bal::>>" + plSGST);
			}
			VoucherMPOS.updateProductBalances(finalAmt, lbrCode, "C", sgstCreditAcctId.substring(0, 8).trim(), session);
			
			
			// d009040CrSGST.setActivityType("CHRGCR");
			D009040 d009040CrCGST = prepareD009040Object(batch, drOperationDate, lbrCode, setNo,
					VoucherMPOS.getNextScrollNo(), scrollNo, cgstCreditAcctId, session, lastAmt, "CGST for " + remark,
					"CR", rrn);
			
			D009021 prdCGST = DataUtils.getProductMaster(String.valueOf(lbrCode), cgstCreditAcctId.substring(0, 8).trim());
			if('P'!=prdCGST.getAcctOpenLevel()) {
				String plCGST = CoreTransactionMPOS.balance(lbrCode, cgstCreditAcctId, lastAmt, "C", session);
				logger.error("bal::>>" + plCGST);
			}
			
			VoucherMPOS.updateProductBalances(lastAmt, lbrCode, "C", cgstCreditAcctId.substring(0, 8).trim(), session);
			d009040CrCGST.setCashFlowType("CGSTCHCR");
			D009040 d009040PLACC = prepareD009040Object(batch, drOperationDate, lbrCode, setNo,
					VoucherMPOS.getNextScrollNo(), scrollNo, plCrAccNo, session, amount, remark, "CR", rrn);
			String plAcct = CoreTransactionMPOS.balance(lbrCode, plCrAccNo, amount, "C", session);
			logger.error("bal::>>" + plAcct);
			VoucherMPOS.updateProductBalances(amount, lbrCode, "C", plCrAccNo.substring(0, 8).trim(), session);
			
			
			d009040PLACC.setActivityType("CHRGCR");
			D010010 d010010 = prepareStopChequeEntry(lbrCode, acctNo, String.valueOf(insType), chequeNo,
					drAccount.getLongName(), "Stop Cheque Request", drOperationDate);
			logger.error("d010010::>>>" + d010010);
			logger.error("voucherFlag::>>" + voucherFlag);
			session.save(d010010);
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
					tranHistory.setMainacctid(acctNo);
					tranHistory.setFromgstno(DataUtils.getSystemParameter(lbrCode, "GSTNINNO").getValue());
					tranHistory.setSaccode(DataUtils.getSystemParameter(0, "SACCODE").getValue());
					
					D009122Id id = new D009122Id(lbrCode, acctNo);
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
					try{
						String stateCd = (String) session
				
							.createNativeQuery("SELECT B.StateCd FROM D001003 A, D500028 B WHERE A.CityCd="
									+ "B.PlaceCd AND A.CityCd=(SELECT CityCd FROM D009011 WHERE CustNo="
									+ sourceAccount.getCustNo() + ") AND A.PBrCode=" + lbrCode)
							.getSingleResult();
					tranHistory.setDeststate(stateCd);
					tranHistory.setSourcestate(stateCd);
					}catch(Exception ec) {
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
						"UPDATE D010004 SET TotalCrVcrs=TotalDrVcrs+3, TotalCrAmtLcy = TotalCrAmtLcy +:amt WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd");
				query1.setParameter("amt", osAmt);
				query1.setParameter("lbrcode", lbrCode);
				query1.setParameter("entrydate", drOperationDate);
				query1.setParameter("batchcd", batch);
				query1.executeUpdate();

				Query query2 = session.createSQLQuery(
						"UPDATE D009044 SET Status = 1 WHERE LBrCode =" + lbrCode + " AND IssuedTo = '" + acctNo
								+ "' AND InsType = '" + insType + "' AND InstruNo = '" + chequeNo + "'");
				// query2.setParameter("amt", osAmt);
				// query2.setParameter("lbrcode", lbrCode);
				// query2.setParameter("entrydate", drOperationDate);
				// query2.setParameter("batchcd", batch);
				query2.executeUpdate();

			}
			t.commit();
			session.close();
			session = null;

			stopPaymentRes.setResponse(MSGConstants.SUCCESS);
			stopPaymentRes.setErrorMessage(
					"Dear customer,as per your request cheque number " + chequeNo + " is stopped successfully.");
			stopPaymentRes.setTransactionId(rrn);
			// return stopPaymentRes;

			/** Voucher Process Start */
			/*
			 * IMPSTransactionResponse voucherTrnxRes =
			 * DataUtils.calculateStopChequeChargesForAccount(instrument, drAccount,
			 * drProductMaster, drOperationDate,remark, mobileNo); if (null !=
			 * voucherTrnxRes && voucherTrnxRes.isValid()) { String refNo =
			 * DateUtil.getComboString(drOperationDate) + "/" + voucherTrnxRes.getRefNo() +
			 * "/" + voucherTrnxRes.getResponse() + "/" + voucherTrnxRes.getCategory();
			 * logger.error("Cheque Stopped Successfully :: " + refNo);
			 * stopPaymentRes.setOutput(new String[] { refNo }); stopPaymentRes
			 * .setMessage("Dear Customer, as per your request cheque no " + chequeNo +
			 * " is Stopped for payment on " + DateUtility .getDateFormat_ddMMMyyyy(new
			 * Date()) + " at " + DateUtility .getTimeForFileGeneration(new Date()) +
			 * ". Stop Payment Charges are recovered.");
			 * stopPaymentRes.setMobileNo(mobileNo); } else {
			 * transactionManager.commit(txStatus); if (null == voucherTrnxRes) {
			 * logger.error("Error in Voucher Process."); return
			 * WSUtils.getWSReturnOutput(SwiftCoreConstants.ERROR,
			 * "Error in Voucher Process.", "", custNo, lbrCode); }
			 * 
			 * logger.error(voucherTrnxRes.getErrorMessage()); stopPaymentRes =
			 * WSUtils.getWSReturnOutput(voucherTrnxRes .getResponse(),
			 * voucherTrnxRes.getErrorMessage(), voucherTrnxRes.getErrorCode(), custNo,
			 * lbrCode); stopPaymentRes.setMobileNo(mobileNo); return stopPaymentRes; }
			 */

			/** Insert into DeliverySMS for Sending SMS */
			/*
			 * String smsMsg = "Dear Customer your request for Stop Payement of Cheque no
			 * "+chequeNo+" is registered successfully on
			 * "+DateUtility.getDateFormat_ddMMMyyyy(new Date()) + " at " +
			 * DateUtility.getTimeForFileGeneration(new Date()); DeliverySMS sms =
			 * wsValidation.prepareDeliverySMSObject(drAccount.getId(). getLbrcode(),
			 * otherChannelDetailsList.get(0).getId().getMobileno(),
			 * DateUtility.convertDateFormat(new Date()), DateUtility.getFormattedTime(new
			 * Date()), "SMSSI"+custNo+DateUtility.getcurrentDateForPDF(), custNo, smsMsg,
			 * 1L); mobileRegService.saveDeliverySMS(sms);
			 */
			/* transactionManager.commit(txStatus); */
			/** Voucher Process End */

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			logger.error(e.getStackTrace().toString());
			stopPaymentRes.setResponse(MSGConstants.ERROR);
			stopPaymentRes.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			return stopPaymentRes;
		}
		return stopPaymentRes;
	}

	public static OtherChannelServiceResponse stopChequePaymentIVR(String chequeNo, String remark, String accNo15digit,
			String channel) {
		OtherChannelServiceResponse stopPaymentRes = new OtherChannelServiceResponse();
		try {
			int lbrCode = 0;
			String acctNo = "";
			D009022 drAccount = DataUtils.getAccount(accNo15digit);
			if (null == drAccount) {
				logger.error("Account does not exist.");
				stopPaymentRes.setResponse(MSGConstants.ERROR);
				stopPaymentRes.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
				return stopPaymentRes;
			}
			TransactionValidationResponse res = TransactionServiceImpl.validateAccount(drAccount, String.valueOf("100"),
					"A");
			logger.error("res::>>>" + res);
			if (null == res) {
				stopPaymentRes.setResponse(MSGConstants.ERROR);
				stopPaymentRes.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
				return stopPaymentRes;
			}
			if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
				stopPaymentRes.setResponse(MSGConstants.ERROR);
				stopPaymentRes.setErrorMessage(res.getErrorMsg());
				return stopPaymentRes;
			}

			lbrCode = drAccount.getId().getLbrCode();
			acctNo = drAccount.getId().getPrdAcctId();
			String productNo = DataUtils.getProductCode(acctNo);
			D009021 drProductMaster = DataUtils.getProductMaster(String.valueOf(lbrCode), productNo);
			IMPSTransactionResponse drProdTrnxRes = DataUtils.productTrnxValidations(drProductMaster, "Debit");
			if (!drProdTrnxRes.isValid()) {
				logger.error(drProdTrnxRes.getErrorMessage());
				stopPaymentRes.setResponse(MSGConstants.ERROR);
				stopPaymentRes.setErrorMessage(drProdTrnxRes.getErrorMessage());
				stopPaymentRes.setOutput(new String[] { "E" });
				return stopPaymentRes;
			}
			D009011 customer = RequestServiceImpl.getCustDetailsMaster(String.valueOf(drAccount.getCustNo()));
			if (null == customer) {
				logger.error("Invalid Customer Number.");
				stopPaymentRes.setResponse(MSGConstants.ERROR);
				stopPaymentRes.setErrorMessage(MSGConstants.INVALID_CUSTOMER);
				return stopPaymentRes;
			}

			if (customer.getDbtrAuthDone() != Byte.valueOf("1") || customer.getDbtrAuthNeeded() != Byte.valueOf("0")) {
				logger.error("Customer Not Authorised.");
				stopPaymentRes.setResponse(MSGConstants.ERROR);
				stopPaymentRes.setErrorMessage(MSGConstants.CUSTOMER_UNAUTH);
				return stopPaymentRes;
			}

			int insType = Integer.valueOf(
					ConfigurationLoader.getParameters(false).getProperty("modType" + drProductMaster.getModuleType()));
			/** Check module type for OD account */
			D009044 issuedInstruments = DataUtils.fetchIssuedInstruments(lbrCode, acctNo, chequeNo);
			if (null != issuedInstruments) {
				insType = issuedInstruments.getId().getInsType();
			}
			issuedInstruments = DataUtils.getIssuedInstruments(lbrCode, acctNo, insType, chequeNo);
			if (null == issuedInstruments) {
				logger.error("Dear Customer, Instrument Not Found/Instr Does not belong to your AcctID.");
				stopPaymentRes.setResponse(MSGConstants.ERROR);
				stopPaymentRes.setErrorMessage(MSGConstants.CHEQUE_NOT_FOUND);
				return stopPaymentRes;
			} else {
				if (issuedInstruments.getStatus() == Byte.valueOf("2")) {
					logger.error("Entered cheque is already paid, Please visit your branch for more details");
					stopPaymentRes.setResponse(MSGConstants.ERROR);
					stopPaymentRes.setErrorMessage(MSGConstants.CHEQUE_ALREADY_PAID);
					return stopPaymentRes;

				} else if (issuedInstruments.getStatus() == Byte.valueOf("1")) {
					D010010 instrument = DataUtils.getInstDetailsForStop(issuedInstruments.getId().getLbrCode(),
							issuedInstruments.getId().getIssuedTo(), issuedInstruments.getId().getInsType(),
							issuedInstruments.getId().getInstruNo());
					if (null == instrument) {
						logger.error("Record not found in Instrument(D0010010).");
						stopPaymentRes.setResponse(MSGConstants.ERROR);
						stopPaymentRes.setErrorMessage("Record not found in Instrument(D0010010).");
						return stopPaymentRes;
					}
					logger.error("Entered cheque is already stopped, Please visit your branch for more details.");
					stopPaymentRes.setResponse(MSGConstants.ERROR);
					stopPaymentRes.setErrorMessage(
							"Entered cheque is already stopped, Please visit your branch for more details.");
					return stopPaymentRes;
				}
			}
			D010010 instrument = DataUtils.getInstDetailsForStop(lbrCode, acctNo, insType, chequeNo);
			if (null != instrument) {
				if (instrument.getRevokeFlag() != Byte.valueOf("1")) {
					logger.error("Entered cheque is already stopped, Please visit your branch for more details.");
					stopPaymentRes.setResponse(MSGConstants.ERROR);
					stopPaymentRes.setErrorMessage(
							"Entered cheque is already stopped, Please visit your branch for more details.");
					return stopPaymentRes;
				}
				if (instrument.getRevokeFlag() == Byte.valueOf("1")
						&& (instrument.getDbtrAuthDone() != Byte.valueOf("1")
								|| instrument.getDbtrAuthNeeded() != Byte.valueOf("0"))) {
					logger.error("Instr Revoked Or Revoke Not Authorized.");
					stopPaymentRes.setErrorMessage("Instr Revoked Or Revoke Not Authorized.");
					stopPaymentRes.setResponse(MSGConstants.ERROR);
					return stopPaymentRes;
				}
			}
			D001004 system = DataUtils.getSystemParameter(lbrCode, "LASTOPENDATE");
			Date drOperationDate = DateUtil.getDateFromStringNew(system.getValue().trim().substring(1));
			instrument = DataUtils.prepareStopPaymentObj(lbrCode, acctNo, chequeNo, Short.valueOf(insType + ""),
					"STOPED BY IMPS", remark, 10.0);
			// instrumentService.saveInstrumentObject(instrument);

			/** Update status as 1(stop payment) */

			issuedInstruments = null;

			stopPaymentRes.setResponse(MSGConstants.SUCCESS);
			stopPaymentRes.setErrorMessage(MSGConstants.CHEQUE_STOPPED_SUCCESS);
			remark = "Stop Chq Chg/" + remark;
			String mobileNo = " ";
			List<D350078> otherChannelDetailsList = DataUtils.getCustomerListD350078(drAccount.getCustNo() + "");
			if (null != otherChannelDetailsList && !otherChannelDetailsList.isEmpty()) {
				mobileNo = otherChannelDetailsList.get(0).getId().getMobileNo();
			}
			D130031 d130031 = DataUtils.getServiceCharges("20");
			logger.error("d130031::>>" + d130031);
			if (d130031 == null) {
				logger.error(MSGConstants.SERVICE_CHARGES_NOT_FOUND + " in D130031.");
				stopPaymentRes.setResponse(MSGConstants.ERROR);
				stopPaymentRes.setErrorMessage(MSGConstants.SERVICE_CHARGES_NOT_FOUND);
				return stopPaymentRes;
			}
			double amount = 0.0;
			double serTaxRate = 0.0;
			double eduCharges = 0.0;
			double finalAmt = 0.0;
			double lastAmt = 0.0;
			double osAmt = 0.0;
			String batch = "";
			String plCrAccNo = "";
			String stat = "";

			if (ConfigurationLoader.getParameters(false).getProperty("IS_D30001").trim().equalsIgnoreCase("Y")) {
				D130001 d130001 = DataUtils.getstopChequeCharges(lbrCode, "2", insType + "");// chgType=2
				// for
				// stop
				// cheque
				logger.error("d130001::>>>" + d130001);
				if (d130001 == null) {
					logger.error(MSGConstants.SERVICE_CHARGES_NOT_FOUND + " in D130001.");
					stopPaymentRes.setResponse(MSGConstants.ERROR);
					stopPaymentRes.setErrorMessage(MSGConstants.SERVICE_CHARGES_NOT_FOUND);
					return stopPaymentRes;
				}
				amount = d130001.getFlatRate();
				batch = d130001.getBatchCd();
				logger.error("drOperationDate::>>" + drOperationDate);
				logger.error("batch::>>" + batch);
				logger.error("lbrCode::>>" + lbrCode);
				D010004 onlineBatch = VoucherMPOS.getD010004(lbrCode, batch.trim(), drOperationDate);
				logger.error("onlineBatch::>>" + onlineBatch);
				logger.error("onlineBatch.getStat()::>>" + onlineBatch.getStat());
				stat = onlineBatch.getStat() + "";
				logger.error("Status::>>" + stat);

				plCrAccNo = d130001.getPlCrAcctId();
				logger.error("amount::>>" + amount);
				logger.error("batch::>>" + batch);
				logger.error("plCrAccNo::>>" + plCrAccNo);

				serTaxRate = d130031.getSerTaxRate() / 100;
				eduCharges = d130031.getEduCesRate() / 100;
				logger.error("serTaxRate::>>" + serTaxRate);
				logger.error("eduCharges::>>" + eduCharges);
				finalAmt = amount * serTaxRate;
				lastAmt = finalAmt * eduCharges;
				osAmt = (finalAmt + lastAmt + amount);
			} else {
				D130014 d130014 = DataUtils.getChequeChargeType(lbrCode, insType, acctNo.substring(0, 8).trim(), 2);// chgType=2
				// for
				// stop
				// cheque
				logger.error("d130014::>>>" + d130014);
				if (d130014 == null) {
					logger.error(MSGConstants.SERVICE_CHARGES_NOT_FOUND + " in D130001.");
					stopPaymentRes.setResponse(MSGConstants.ERROR);
					stopPaymentRes.setErrorMessage(MSGConstants.SERVICE_CHARGES_NOT_FOUND);
					return stopPaymentRes;
				}
				amount = d130014.getFlatRate();
				batch = d130014.getBatchCd();
				logger.error("drOperationDate::>>" + drOperationDate);
				logger.error("batch::>>" + batch);
				logger.error("lbrCode::>>" + lbrCode);
				D010004 onlineBatch = VoucherMPOS.getD010004(lbrCode, batch.trim(), drOperationDate);
				logger.error("onlineBatch::>>" + onlineBatch);
				logger.error("onlineBatch.getStat()::>>" + onlineBatch.getStat());
				stat = onlineBatch.getStat() + "";
				logger.error("Status::>>" + stat);

				plCrAccNo = d130014.getPlCrAcctId();
				logger.error("amount::>>" + amount);
				logger.error("batch::>>" + batch);
				logger.error("plCrAccNo::>>" + plCrAccNo);

				serTaxRate = d130031.getSerTaxRate() / 100;
				eduCharges = d130031.getEduCesRate() / 100;
				logger.error("serTaxRate::>>" + serTaxRate);
				logger.error("eduCharges::>>" + eduCharges);
				finalAmt = amount * serTaxRate;
				lastAmt = finalAmt * eduCharges;
				osAmt = (finalAmt + lastAmt + amount);
			}
			logger.error("Total outstanding Amount::>>" + osAmt);
			D009022 sourceAccount = DataUtils.getAccountMaster(lbrCode, acctNo);// lbrCode,
			// acctNo
			logger.error("sourceAccount ::>>" + sourceAccount);
			if (sourceAccount == null) {
				stopPaymentRes.setResponse(MSGConstants.ERROR);
				stopPaymentRes.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
				return stopPaymentRes;
			}
			if (stat.equalsIgnoreCase("1") || stat.equalsIgnoreCase("2")) {
				logger.error("Batch is open");
			} else {
				Session session = HBUtil.getSessionFactory().openSession();
				Transaction t = session.beginTransaction();
				logger.error("Batch is not open");
				logger.error("Online Batch not Open charges is placed in recovery master.");
				logger.error("lbrCode::>>" + lbrCode);
				logger.error("acctNo::>>>" + acctNo);
				TransactionValidationResponse response = TransactionServiceImpl.validateAccount(sourceAccount,
						"" + osAmt, "A");
				if (res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
					stopPaymentRes.setResponse(MSGConstants.ERROR);
					stopPaymentRes.setErrorMessage(response.getErrorMsg());
					return stopPaymentRes;
				}

				D130008Id id = new D130008Id();
				id.setChgType(Byte.valueOf("20"));
				id.setLbrCode(lbrCode);
				id.setPrdAcctId(acctNo);
				D130008 d130008Obj = session.get(D130008.class, id);
				logger.error("d130008Obj::>>" + d130008Obj);
				if (d130008Obj == null) {
					logger.error("d130008Obj is " + d130008Obj);
					D130008 d130008 = prepareRecoveryChgmaster(drOperationDate, osAmt, "20", lbrCode, acctNo);
					session.save(d130008);
					// session.flush();
				} else {
					logger.error("d130008Obj is not null");
					double amt = d130008Obj.getOsAmt() + osAmt;
					d130008Obj.setOsAmt(amt);
					session.update(d130008Obj);
					// session.flush();
				}
				D010010 d010010 = prepareStopChequeEntry(lbrCode, acctNo, String.valueOf(insType), chequeNo,
						drAccount.getLongName(), "Stop Cheque/" + chequeNo, drOperationDate);
				logger.error("stopped");
				session.save(d010010);

				Query query = session.createSQLQuery(
						"UPDATE D009044 SET Status = 1 WHERE LBrCode =" + lbrCode + " AND IssuedTo = '" + acctNo
								+ "' AND InsType = '" + insType + "' AND InstruNo = '" + chequeNo + "'");
				query.executeUpdate();
				t.commit();
				session.close();
				session = null;
				t = null;
				stopPaymentRes.setResponse(MSGConstants.SUCCESS);
				stopPaymentRes.setErrorMessage(
						"Dear customer,as per your request cheque number " + chequeNo + " is stopped successfully.");//
				stopPaymentRes.setTransactionId(DataUtils.getNextRRN());
				return stopPaymentRes;
			}

			Session session = HBUtil.getSessionFactory().openSession();
			Transaction t = session.beginTransaction();
			/*
			 * String staxAccNo = d130031.getSerTaxAcctId(); String sbcAccNo =
			 * d130031.getEduCesAcctId(); logger.error("staxAccNo::>>" + staxAccNo);
			 * logger.error("sbcAccNo::>>>" + sbcAccNo);
			 */
			logger.error("sourceAccount::>>" + sourceAccount);
			TransactionValidationResponse response = TransactionServiceImpl.validateAccount(sourceAccount, "" + amount,
					"D");
			boolean voucherFlag = true;
			logger.error("response.getResponse():>>" + response.getResponse());
			logger.error("response.getErrorMsg()::>>>" + response.getErrorMsg());
			if (response.getResponse().equalsIgnoreCase(MSGConstants.ERROR)
					&& response.getErrorMsg().equalsIgnoreCase(MSGConstants.INSUFFICIENT_FUNDS)) {
				voucherFlag = false;
				D130008 d130008Obj = DataUtils.getD130008(lbrCode, acctNo);
				logger.error("d130008Obj::>>" + d130008Obj);
				if (d130008Obj == null) {
					d130008Obj = prepareRecoveryChgmaster(drOperationDate, osAmt, "20", lbrCode, acctNo);
					session.save(d130008Obj);
				} else {
					d130008Obj.setOsAmt(d130008Obj.getOsAmt() + osAmt);
					session.update(d130008Obj);
				}
			} else {
				String bal = CoreTransactionMPOS.balance(lbrCode, acctNo, osAmt, "D", session);
				logger.error("bal::>>" + bal);
			}
			int scrollNo = VoucherMPOS.getNextScrollNo();
			int setNo = VoucherMPOS.getNextSetNo();
			logger.error("setNo::>" + setNo);
			logger.error("scrollNo::>>" + scrollNo);
			String rrn = DataUtils.getNextRRN();

			// D009040 d009040Dr=prepareD009040Object(batch, drOperationDate,
			// lbrCode, VoucherMPOS.getNextSetNo(),
			// VoucherMPOS.getNextScrollNo(), scrollNo, acctNo, session, osAmt,
			// remark, "DR",rrn);
			// D009040 d009040CrSTAX=prepareD009040Object(batch,
			// drOperationDate, lbrCode, VoucherMPOS.getNextSetNo(),
			// VoucherMPOS.getNextScrollNo(), scrollNo, staxAccNo, session,
			// finalAmt, remark, "CR",rrn);
			// D009040 d009040CrSBC=prepareD009040Object(batch, drOperationDate,
			// lbrCode, VoucherMPOS.getNextSetNo(),
			// VoucherMPOS.getNextScrollNo(), scrollNo, sbcAccNo, session,
			// lastAmt, remark, "CR",rrn);
			// D009040 d009040PLACC=prepareD009040Object(batch, drOperationDate,
			// lbrCode, VoucherMPOS.getNextSetNo(),
			// VoucherMPOS.getNextScrollNo(), scrollNo, plCrAccNo, session,
			// amount, remark, "CR",rrn);
			// D010010 d010010=prepareStopChequeEntry(lbrCode, acctNo,
			// String.valueOf(insType), chequeNo,drAccount.getLongName(), "Stop
			// Cheque Request", drOperationDate);

			D009040 d009040Dr = prepareD009040Object(batch, drOperationDate, lbrCode, setNo,
					VoucherMPOS.getNextScrollNo(), scrollNo, acctNo, session, osAmt, remark, "DR", rrn);
			/*
			 * D009040 d009040CrSTAX = prepareD009040Object(batch, drOperationDate, lbrCode,
			 * setNo, VoucherMPOS.getNextScrollNo(), scrollNo, staxAccNo, session, finalAmt,
			 * remark, "CR", rrn); D009040 d009040CrSBC = prepareD009040Object(batch,
			 * drOperationDate, lbrCode, setNo, VoucherMPOS.getNextScrollNo(), scrollNo,
			 * sbcAccNo, session, lastAmt, remark, "CR", rrn);
			 */
			D009040 d009040PLACC = prepareD009040Object(batch, drOperationDate, lbrCode, setNo,
					VoucherMPOS.getNextScrollNo(), scrollNo, plCrAccNo, session, amount, remark, "CR", rrn);
			D010010 d010010 = prepareStopChequeEntry(lbrCode, acctNo, String.valueOf(insType), chequeNo,
					drAccount.getLongName(), "Stop Cheque Request", drOperationDate);
			logger.error("d010010::>>>" + d010010);
			logger.error("voucherFlag::>>" + voucherFlag);
			session.save(d010010);
			if (voucherFlag) {
				logger.error("d009040PLACC::>>" + d009040PLACC);
				logger.error("d009040Dr::>>" + d009040Dr);
				/*
				 * logger.error("d009040CrSTAX::>>" + d009040CrSTAX);
				 * logger.error("d009040CrSBC::>>" + d009040CrSBC);
				 */
				session.save(d009040PLACC);
				session.save(d009040Dr);
//				session.save(d009040CrSTAX);
//				session.save(d009040CrSBC);

				Query query = session.createSQLQuery(
						"UPDATE D010004 SET TotalDrVcrs=TotalDrVcrs+1, TotalDrAmtLcy = TotalDrAmtLcy +:amt WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd");
				query.setParameter("amt", osAmt);
				query.setParameter("lbrcode", lbrCode);
				query.setParameter("entrydate", drOperationDate);
				query.setParameter("batchcd", batch);
				query.executeUpdate();

				Query query1 = session.createSQLQuery(
						"UPDATE D010004 SET TotalCrVcrs=TotalDrVcrs+3, TotalCrAmtLcy = TotalCrAmtLcy +:amt WHERE LBrCode = :lbrcode AND EntryDate = :entrydate AND BatchCd = :batchcd");
				query1.setParameter("amt", osAmt);
				query1.setParameter("lbrcode", lbrCode);
				query1.setParameter("entrydate", drOperationDate);
				query1.setParameter("batchcd", batch);
				query1.executeUpdate();

				Query query2 = session.createSQLQuery(
						"UPDATE dbo.D009044 SET Status = 1 WHERE LBrCode =" + lbrCode + " AND IssuedTo = '" + acctNo
								+ "' AND InsType = '" + insType + "' AND InstruNo = '" + chequeNo + "'");
				query2.executeUpdate();
			}
			t.commit();
			session.close();
			session = null;

			stopPaymentRes.setResponse(MSGConstants.SUCCESS);
			stopPaymentRes.setErrorMessage(
					"Dear customer,as per your request cheque number " + chequeNo + " is stopped successfully.");
			stopPaymentRes.setTransactionId(rrn);

		} catch (Exception e) {
			e.printStackTrace();
			stopPaymentRes.setResponse(MSGConstants.ERROR);
			stopPaymentRes.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			return stopPaymentRes;
		}
		return stopPaymentRes;
	}

	public static D009040 prepareD009040Object(String batchCode, Date entryDate, int lbrCode, int setNo, int scrollNo,
			int mainScrollNo, String prdAcctId, Session session, double amt, String particulars, String drCr,
			String rrn) {
		logger.error("batchCode::>>" + batchCode);
		logger.error("entryDate::>>" + entryDate);
		logger.error("lbrCode::>>" + lbrCode);
		logger.error("setNo::>>" + setNo);
		logger.error("scrollNo::>>" + scrollNo);
		logger.error("mainScrollNo::>>" + mainScrollNo);
		logger.error("prdAcctId::>>" + prdAcctId);
		logger.error("Amount::>>" + amt);
		logger.error("particulars::>>" + particulars);
		logger.error("drCr::>>" + drCr);
		logger.error("rrn::>>" + rrn);
		// logger.error("prdAcctId::>>"+prdAcctId);
		char debitCredit = ' ';
		String activity = "";
		if (drCr.trim().equalsIgnoreCase("DR")) {
			debitCredit = 'D';
			activity = "DR";
		}
		if (drCr.trim().equalsIgnoreCase("CR")) {
			debitCredit = 'C';
			activity = "CR";
		}

		D009040 d40 = new D009040();
		D009040Id id40 = new D009040Id();
		id40.setBatchCd(batchCode); // SELECT * FROM D010004 WHERE LBrCode =9
		// AND EntryDate = '19-APR-2016'

		id40.setEntryDate(entryDate); // SELECT Value FROM D001004 WHERE LBrCode
		// = 9 AND Code = 'LASTOPENDATE'
		id40.setLbrCode(lbrCode);
		id40.setSetNo((int) setNo);
		id40.setScrollNo((int) scrollNo);
		d40.setId(id40);
		d40.setMainScrollNo((int) mainScrollNo); // Scroll No
		d40.setPostDate(entryDate); // SELECT * FROM D010004 WHERE LBrCode =9
		// AND EntryDate = '19-APR-2016' AND BatchCd
		// = 'ABBTR'
		d40.setFeffDate(entryDate); // SELECT * FROM D010004 WHERE LBrCode =9
		// AND EntryDate = '19-APR-2016' AND BatchCd
		// = 'ABBTR'
		d40.setActivityType(activity);
		d40.setCashFlowType(activity);
		d40.setValueDate(entryDate); // postdate SELECT * FROM D010004 WHERE
		// LBrCode =9 AND EntryDate =
		// '19-APR-2016' AND BatchCd = 'ABBTR'

		logger.error("Calling D010001");
		D010001 d001 = VoucherMPOS.getD001(lbrCode, batchCode);
		String booktype = d001.getBookType();
		d40.setBookType(booktype); // SELECT BookType FROM D010001 WHERE LBrCode
		// = 9 AND Code = 'ABBTR'
		d40.setDrCr((char) debitCredit);
		d40.setVcrAcctId(prdAcctId);
		d40.setMainAcctId(prdAcctId);
		D009021 d009021 = VoucherMPOS.getD009021(lbrCode, prdAcctId.substring(0, 8).trim());
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
		d40.setInstrBankCd((short) 0);
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

		if(d40.getInstrNo().length()>12)
			d40.setInstrNo(d40.getInstrNo().substring(d40.getInstrNo().length()-12));
		
		D002001 d002001 = VoucherMPOS.getD002001("WEB");

		d40.setMaker(d002001.getUsrCode2()); // SELECT UsrCode2 FROM D002001
		// WHERE UsrCode1 = 'WEB'
		d40.setMakerDate(entryDate); // EntryDate
		d40.setMakerTime(Integer.parseInt(sdf.format(new Date()).substring(0, 8)));
		d40.setChecker1(d002001.getUsrCode2()); // SELECT UsrCode2 FROM D002001
		// WHERE UsrCode1 = 'WEB'
		d40.setChecker2(0);
		d40.setChecker3(0);
		d40.setChecker4(0);
		d40.setCheckerDate(entryDate); // EntryDate
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

	public static D130008 prepareRecoveryChgmaster(Date opDate, double osAmt, String chgType, int lbrCode,
			String prdAcctId) {
		D130008 d130008 = new D130008();
		D130008Id id = new D130008Id();

		id.setChgType(Byte.valueOf(chgType));
		id.setLbrCode(lbrCode);
		id.setPrdAcctId(prdAcctId);

		d130008.setDbtrAddCb(0);
		d130008.setDbtrAddCd(opDate);
		d130008.setDbtrAddCk(0);
		d130008.setDbtrAddCs(Short.valueOf("0"));
		d130008.setDbtrAddCt(opDate);
		d130008.setDbtrAddMb(0);
		d130008.setDbtrAddMd(opDate);
		d130008.setDbtrAddMk(0);
		d130008.setDbtrAddMs(Short.valueOf("0"));
		d130008.setDbtrAddMt(opDate);
		d130008.setDbtrAuthDone(Byte.valueOf("1"));
		d130008.setDbtrAuthNeeded(Byte.valueOf("0"));
		d130008.setDbtrLhisTrnNo(0);
		d130008.setDbtrLupdCb(0);
		d130008.setDbtrLupdCd(opDate);
		d130008.setDbtrLupdCk(0);
		d130008.setDbtrLupdCs(Short.valueOf("0"));
		d130008.setDbtrLupdCt(opDate);
		d130008.setDbtrLupdMb(0);
		d130008.setDbtrLupdMd(opDate);
		d130008.setDbtrLupdMk(0);
		d130008.setDbtrLupdMs(Short.valueOf("0"));
		d130008.setDbtrLupdMt(opDate);
		d130008.setDbtrRecStat(Byte.valueOf("0"));
		d130008.setDbtrTauthDone(Short.valueOf("1"));
		d130008.setDbtrUpdtChkId(Short.valueOf("0"));
		d130008.setLastApplDate(opDate);
		d130008.setOsAmt(osAmt);
		d130008.setId(id);
		return d130008;
	}

	public static D010010 prepareStopChequeEntry(int lbrCode, String issuedTo, String insType, String chequeNo,
			String name, String narretion, Date opDate) {
		// D002001 d002001 = VoucherMPOS.getD002001("WEB");
		D010010 d010010 = new D010010();
		D010010Id d010010Id = new D010010Id();
		d010010Id.setLbrCode(lbrCode);
		d010010Id.setIssuedTo(issuedTo);
		d010010Id.setInsType(Short.valueOf(insType));
		d010010Id.setInsNo(chequeNo);
		d010010.setId(d010010Id);
		d010010.setInstrDate(opDate);
		d010010.setStopDate(opDate);
		d010010.setStopTime(new Date());
		d010010.setRequestDate(opDate);
		d010010.setPayeeName((name != null && name.trim().length() > 35) ? name.trim().substring(0, 35) : name.trim());
		d010010.setStopAmt(0);
		d010010.setRemarks(narretion);
		d010010.setRevokeFlag((byte) 0);
		d010010.setRevokeDate(new Date());
		// d010010.setDbtrAddMk(d002001 != null ? d002001.getUsrCode2() :
		// 99999988);
		// d010010.setDbtrAddMb(d002001 != null ? d002001.getUsrCode2() :
		// 99999988);
		// d010010.setDbtrAddMs((short) d002001.getUsrCode2());

		d010010.setDbtrAddMk(0);
		d010010.setDbtrAddMb(0);
		d010010.setDbtrAddMs(Short.valueOf("0"));
		d010010.setDbtrAddMd(opDate);
		d010010.setDbtrAddMt(opDate);
		d010010.setDbtrAddCk(0);
		d010010.setDbtrAddCb(0);
		d010010.setDbtrAddCs((short) 0);
		d010010.setDbtrAddCd(opDate);
		d010010.setDbtrAddCt(opDate);
		d010010.setDbtrLupdMk(0);
		d010010.setDbtrLupdMb(0);
		d010010.setDbtrLupdMs((short) 0);
		d010010.setDbtrLupdMd(opDate);
		d010010.setDbtrLupdMt(opDate);
		d010010.setDbtrLupdCk(0);
		d010010.setDbtrLupdCb(0);
		d010010.setDbtrLupdCd(opDate);
		d010010.setDbtrLupdCs((short) 0);
		d010010.setDbtrLupdCt(opDate);
		d010010.setDbtrTauthDone((short) 0);
		d010010.setDbtrRecStat((byte) 0);
		d010010.setDbtrAuthDone((byte) 1);
		d010010.setDbtrAuthNeeded((byte) 0);
		d010010.setDbtrUpdtChkId((byte) 0);
		d010010.setDbtrLhisTrnNo(0);
		return d010010;
	}

	public static DebitAccount debitIMPSTransaction(String accNo15digit, String amount, String narration, String mob1,
			String mmid1, String mob2, String mmid2, String tType, String reconNo, String bankCode, String version,
			DebitAccount request) {
		logger.error("<<<<<< TransactionService.debitIMPSTransaction >>>>>>>");
		logger.error("accNo15digit::>>" + accNo15digit + " Amount::>>>" + amount + " narration::>>" + narration
				+ " mob1::>>>" + mob1 + " mmid1::>>" + mmid1 + " mob2::>>" + mob2 + " mmid2::>>" + mmid2);
		DebitAccount response = new DebitAccount();
		String rrn = DataUtils.getNextRRN();
		if (tType.equalsIgnoreCase(MSGConstants.P2A_TRANSACTION)) {
			CoreBankingOperationImpl impl = new CoreBankingOperationImpl();
			logger.error("Other bank transaction");
			try {
				String resp = impl.debitAccountOtherBank(accNo15digit, MSGConstants.SIM_SAY_PAY,
						"SIMSE PAY/" + narration, Double.valueOf(amount), rrn, mob1, mmid1, mob2, mmid2, tType);
				impl = null;
				if (resp.trim().equalsIgnoreCase(ResponseCodes.SUCCESS)) {
					SimSePayTransactionEntry.simSePayTrnEntry(accNo15digit, Double.valueOf(amount),
							Integer.valueOf(
									accNo15digit.substring(0, 3) == null ? "0" : accNo15digit.trim().substring(0, 3)),
							MSGConstants.SUCCESS_MSG, MSGConstants.SUCCESS_MSG, MSGConstants.SUCCESS_MSG,
							ResponseCodes.SUCCESS, rrn, MSGConstants.DR, reconNo, bankCode);
					request.setBankTxnId(rrn);
					request.setTxnStatus(ResponseCodes.SIMSEPAY_SUCCESS);
					request.setTxnDesc(MSGConstants.SUCCESS_MSG);
					return request;
				} else {
					SimSePayTransactionEntry.simSePayTrnEntry(accNo15digit, Double.valueOf(amount),
							Integer.valueOf(
									accNo15digit.substring(0, 3) == null ? "0" : accNo15digit.trim().substring(0, 3)),
							MSGConstants.TRANSACTION_DECLINED, MSGConstants.TRANSACTION_DECLINED,
							MSGConstants.TRANSACTION_DECLINED, ResponseCodes.SUCCESS, rrn, MSGConstants.DR, reconNo,
							bankCode);

					response.setBankTxnId(rrn);
					request.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
					request.setTxnDesc(MSGConstants.TRANSACTION_DECLINED);
					return request;
				}
			} catch (Exception e) {
				e.printStackTrace();
				impl = null;
				SimSePayTransactionEntry.simSePayTrnEntry(accNo15digit, Double.valueOf(amount),
						Integer.valueOf(
								accNo15digit.substring(0, 3) == null ? "0" : accNo15digit.trim().substring(0, 3)),
						MSGConstants.WEB_SERVICE_ERROR, MSGConstants.WEB_SERVICE_ERROR, MSGConstants.WEB_SERVICE_ERROR,
						ResponseCodes.SUCCESS, rrn, MSGConstants.DR, reconNo, bankCode);
				request.setBankTxnId(rrn);
				request.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
				request.setTxnDesc(MSGConstants.WEB_SERVICE_ERROR);
				return request;
			}
		}
		request.setBankTxnId(rrn);
		request.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
		request.setTxnDesc(MSGConstants.INVALID_TRANSACTION_TYPE);
		return request;
	}

	public static ReverseDebitAccount reverseDebitAccount(String accNo15digit, String amount, String narration,
			String mob1, String mmid1, String mob2, String mmid2, String tType, String rrn,
			IMPSTransactionRequest request, String reconNo, String bankCode, String version, ReverseDebitAccount req) {
		logger.error("in reverseDebitAccount");
		ReverseDebitAccount response = new ReverseDebitAccount();
		if (tType.equalsIgnoreCase(MSGConstants.P2A_TRANSACTION)) {
			Session session = HBUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(SimSePayTrancation.class);
			criteria.add(Restrictions.eq("brcode", Integer.valueOf(accNo15digit.substring(0, 3))));
			criteria.add(Restrictions.eq("accountno", accNo15digit));
			criteria.add(Restrictions.eq("amount", Double.valueOf(amount.trim())));
			criteria.add(Restrictions.eq("rrn", rrn));
			criteria.add(Restrictions.eq("respcode", "00"));
			criteria.add(Restrictions.eq("drcr", "D"));
			List<SimSePayTrancation> list = criteria.list();

			Criteria criteria2 = session.createCriteria(SimSePayTrancation.class);
			criteria2.add(Restrictions.eq("brcode", Integer.valueOf(accNo15digit.substring(0, 3))));
			criteria2.add(Restrictions.eq("accountno", accNo15digit));
			criteria2.add(Restrictions.eq("amount", Double.valueOf(amount.trim())));
			criteria2.add(Restrictions.eq("rrn", rrn));
			criteria2.add(Restrictions.eq("respcode", "00"));
			criteria2.add(Restrictions.eq("drcr", "C"));
			List<SimSePayTrancation> list2 = criteria2.list();
			session.close();
			session = null;
			logger.error("list::>>" + list);
			logger.error("list2::>>" + list2);
			if (list == null || list.size() < 1) {
				SimSePayTransactionEntry.simSePayTrnEntry(accNo15digit, Double.valueOf(amount.trim()),
						Integer.valueOf(
								accNo15digit.substring(0, 3) == null ? "0" : accNo15digit.trim().substring(0, 3)),
						MSGConstants.TRANSACTION_NOT_FOUND, MSGConstants.TRANSACTION_NOT_FOUND,
						MSGConstants.TRANSACTION_NOT_FOUND, ResponseCodes.SIMSEPAY_FAILURE, rrn, MSGConstants.CR,
						reconNo, bankCode);
				req.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
				req.setTxnDesc(MSGConstants.TRANSACTION_NOT_FOUND);
				req.setBankTxnId(rrn);
				return req;
			}
			if (list2 != null && list2.size() > 0) {
				req.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
				req.setTxnDesc(MSGConstants.ALREADY_REVERSED);
				return req;
			}
			CoreBankingOperationImpl impl = new CoreBankingOperationImpl();
			logger.error("Sim Se pay P2A Transaction::>>" + mmid2);
			try {
				String resp = impl.reversedTransactionOtherBank(accNo15digit, MSGConstants.SIM_SAY_PAY,
						"Sim-Se-Pay Transaction/" + accNo15digit, Double.valueOf(amount), rrn, mob1, mmid1, mob2, mmid2,
						MSGConstants.SIM_SAY_PAY, request);
				impl = null;
				if (resp.trim().equalsIgnoreCase(ResponseCodes.SUCCESS)) {
					SimSePayTransactionEntry.simSePayTrnEntry(accNo15digit, Double.valueOf(amount.trim()),
							Integer.valueOf(
									accNo15digit.substring(0, 3) == null ? "0" : accNo15digit.trim().substring(0, 3)),
							MSGConstants.SUCCESS_MSG, MSGConstants.SUCCESS_MSG, MSGConstants.SUCCESS_MSG,
							ResponseCodes.SUCCESS, rrn, MSGConstants.CR, reconNo, bankCode);
					req.setTxnStatus(ResponseCodes.SIMSEPAY_SUCCESS);
					req.setTxnDesc(MSGConstants.SUCCESS_MSG);
					req.setBankTxnId(rrn);
					return req;
				} else {
					SimSePayTransactionEntry.simSePayTrnEntry(accNo15digit, Double.valueOf(amount.trim()),
							Integer.valueOf(
									accNo15digit.substring(0, 3) == null ? "0" : accNo15digit.trim().substring(0, 3)),
							MSGConstants.TRANSACTION_DECLINED, MSGConstants.TRANSACTION_DECLINED,
							MSGConstants.TRANSACTION_DECLINED, ResponseCodes.SIMSEPAY_FAILURE, rrn, MSGConstants.CR,
							reconNo, bankCode);
					req.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
					req.setTxnDesc(MSGConstants.TRANSACTION_DECLINED);
					req.setBankTxnId(rrn);
					return req;
				}
			} catch (Exception e) {
				e.printStackTrace();
				impl = null;
				SimSePayTransactionEntry.simSePayTrnEntry(accNo15digit, Double.valueOf(amount.trim()),
						Integer.valueOf(
								accNo15digit.substring(0, 3) == null ? "0" : accNo15digit.trim().substring(0, 3)),
						MSGConstants.WEB_SERVICE_ERROR, MSGConstants.WEB_SERVICE_ERROR, MSGConstants.WEB_SERVICE_ERROR,
						ResponseCodes.SIMSEPAY_FAILURE, rrn, MSGConstants.CR, reconNo, bankCode);
				req.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
				req.setTxnDesc(MSGConstants.WEB_SERVICE_ERROR);
				req.setBankTxnId(rrn);
				return req;
			}
		}
		req.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
		req.setTxnDesc(MSGConstants.INVALID_TRANSACTION_TYPE);
		req.setBankTxnId(rrn);
		return req;
	}
	// <<================== Reverse Bill Payment
	// =====================================>>>

	public static IMPSTransactionResponse reverseBillPayment(String accNo15digit, String destAccNo15digit,
			String amount, String narration, String mob1, String mmid1, String mob2, String mmid2, String tType,
			String rrn, IMPSTransactionRequest request) {
		logger.error("in reverseIMPSTransaction");
		IMPSTransactionResponse response = new IMPSTransactionResponse();
		if (tType.equalsIgnoreCase(MSGConstants.BILLPAY)) {
			Session session = HBUtil.getSessionFactory().openSession();
			Transaction t = session.beginTransaction();
			Criteria criteria = session.createCriteria(Billpayment.class);
			criteria.add(Restrictions.eq("id.lbrCode", Integer.valueOf(accNo15digit.substring(0, 3))));
			//criteria.add(Restrictions.eq("id.batchCd", "MBTR"));
			criteria.add(Restrictions.eq("mobNo1", mob1));
			criteria.add(Restrictions.eq("mmid1", mmid1));
			criteria.add(Restrictions.eq("accNo", mob2));
			criteria.add(Restrictions.eq("ifscCd", mmid2));
			criteria.add(Restrictions.eq("tranAmt", Double.valueOf(amount)));
			criteria.add(Restrictions.eq("rrnNo", rrn));
			List<Billpayment> list = criteria.list();
			t.commit();
			session.close();
			session = null;
			logger.error("list::>>" + list);
			if (list == null || list.size() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setResponse(MSGConstants.TRANSACTION_NOT_FOUND);
				return response;
			}
			CoreBankingOperationImpl impl = new CoreBankingOperationImpl();
			logger.error("P2A Transaction::>>" + mmid2);
			if (mmid2.trim().substring(0, 8).equalsIgnoreCase(MSGConstants.IFSC_CODE)) {
				logger.error("Same Bank P2P tran'saction.");
				if (destAccNo15digit == null || destAccNo15digit.trim().length() != 15) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_BEN_ACC_NO);
					return response;
				}
				D009022 sourceAccount = DataUtils.getAccount(destAccNo15digit.trim());
				if (sourceAccount == null) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_BEN_ACC_NO);
					return response;
				}
				TransactionValidationResponse res = validateAccount(sourceAccount, amount, "C");
				if (res.getResponse().trim().equalsIgnoreCase(MSGConstants.ERROR)) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_BEN_ACC_NO);
					res = null;
					return response;
				}
				String resp = impl.reversefundTransfer(accNo15digit.trim(), destAccNo15digit.trim(),
						MSGConstants.TRANS_TYPE, "IMPS REV P2A/" + narration, Double.valueOf(amount), rrn, mob1, mmid1,
						mob2, mmid2, tType);
				impl = null;
				if (resp.trim().equalsIgnoreCase(ResponseCodes.SUCCESS)) {
					response.setResponse(MSGConstants.SUCCESS);
					response.setErrorMessage(MSGConstants.SUCCESS_MSG);
					response.setRrnNo(rrn);
					response.setErrorCode(resp);
					return response;
				} else {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
					response.setRrnNo(rrn);
					response.setErrorCode(resp);
					return response;
				}
			}

			try {
				String resp = impl.reversedTransactionOtherBank(accNo15digit, MSGConstants.TRANS_TYPE,
						"IMPS Transaction/" + accNo15digit, Double.valueOf(amount), rrn, mob1, mmid1, mob2, mmid2,
						tType, request);
				impl = null;
				if (resp.trim().equalsIgnoreCase(ResponseCodes.SUCCESS)) {
					response.setResponse(MSGConstants.SUCCESS);
					response.setErrorMessage(MSGConstants.SUCCESS_MSG);
					response.setRrnNo(rrn);
					response.setErrorCode(resp);
					return response;
				} else {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
					response.setRrnNo(rrn);
					response.setErrorCode(resp);
					return response;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				impl = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
				response.setRrnNo(rrn);
				response.setErrorCode(ResponseCodes.EXCEPTION_OCCURED);
				return response;
			}
		}
		response.setResponse(MSGConstants.ERROR);
		response.setErrorMessage(MSGConstants.INVALID_TRANSACTION_TYPE);
		return response;
	}

	public static PrepaidCardLoadBalance prepaidCardLoadBalanceObj(int brcode, String accountno, String mobileno,
			double amount, String cardno, String cardinsno, String errorMessage1, String errorMessage2,
			String errorMessage3, String respcode, String rrn) {
		PrepaidCardLoadBalance balance = new PrepaidCardLoadBalance();
		balance.setBrcode(brcode);
		balance.setAccountno(accountno);
		balance.setMobileno(mobileno);
		balance.setAmount(amount);
		balance.setCardno(cardno);
		balance.setCardinsno(cardinsno);
		balance.setEntrydate(DateUtil.getcurrentDate());
		balance.setEntrytime(new Date());
		balance.setErrorMessage1(errorMessage1);
		balance.setErrorMessage2(errorMessage2);
		balance.setErrorMessage3(errorMessage3);
		balance.setRespcode(respcode);
		balance.setRrn(rrn);
		return balance;
	}

	public static ReverseLoadBalance reversePrepaidCardLoadBalanceObj(int brcode, String accountno, String mobileno,
			double amount, String cardno, String cardinsno, String errorMessage1, String errorMessage2,
			String errorMessage3, String respcode, String rrn) {
		ReverseLoadBalance balance = new ReverseLoadBalance();
		balance.setBrcode(brcode);
		balance.setAccountno(accountno);
		balance.setMobileno(mobileno);
		balance.setAmount(amount);
		balance.setCardno(cardno);
		balance.setCardinsno(cardinsno);
		balance.setEntrydate(DateUtil.getcurrentDate());
		balance.setEntrytime(new Date());
		balance.setErrorMessage1(errorMessage1);
		balance.setErrorMessage2(errorMessage2);
		balance.setErrorMessage3(errorMessage3);
		balance.setRespcode(respcode);
		balance.setRrn(rrn);
		return balance;
	}

	public static TransactionValidationResponse accDepositTransaction(String sourceAcc, String destAcc, String custType,
			String amount, String rrn) {
		logger.error("<<<<<<<<:::: TransactionServiceImpl.accDepositTransaction() ::>>>>>>>>" + rrn);
		logger.error("<<<<<<<<:::: TransactionServiceImpl.accDepositTransaction() ::>>>>>>>>" + rrn);
		logger.error("sourceAcc::>>" + sourceAcc);
		logger.error("destAcc::>>>" + destAcc);
		logger.error("sourceAcc::>>" + sourceAcc + " destAcc::>>>" + destAcc + " custType::>>" + custType
				+ " amount::>>" + amount + " RRN::>>" + rrn);
		TransactionValidationResponse res = new TransactionValidationResponse();
		D009022 sourceAccount = null;
		if (!custType.equalsIgnoreCase(MSGConstants.BRANCH_USER)) {
			sourceAccount = DataUtils.getAccount(sourceAcc);
			if (sourceAccount == null) {
				res.setResponse(MSGConstants.ERROR);
				res.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
				return res;
			}
		}
		D009022 destAccount = null;// DataUtils.getAccount(destAcc);
		if (sourceAccount != null) {
			if (custType.toUpperCase().contains("PGM")) {
				logger.error("PGM Account");
				res = validateAccount(sourceAccount, amount, "AGENT");
				if (res != null) {
					if (res.getResponse().equalsIgnoreCase(MSGConstants.ERROR))
						return res;
				}
				logger.error("PGM Account");
				destAccount = DataUtils.getAccount(destAcc);
				if (destAccount == null) {
					res.setResponse(MSGConstants.ERROR);
					res.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
					return res;
				}
				res = validateAccount(destAccount, amount, "C");
				if (res != null) {
					if (res.getResponse().equalsIgnoreCase(MSGConstants.ERROR))
						return res;
				}
				if (!custType.equalsIgnoreCase(MSGConstants.BRANCH_USER)) {
					destAccount = DataUtils.getAccount(destAcc);
					res = validateAccount(destAccount, amount, "C");
					if (res.getResponse().equalsIgnoreCase(MSGConstants.ERROR))
						return res;
				}
			} else {
				if (!custType.equalsIgnoreCase(MSGConstants.BRANCH_USER)) {
					res = validateAccount(sourceAccount, amount, "D");
					if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR))
						return res;
				}
			}
		}
		if (custType.equalsIgnoreCase(MSGConstants.BRANCH_USER)) {
			destAccount = DataUtils.getAccount(destAcc);
			if (destAccount == null) {
				res.setResponse(MSGConstants.ERROR);
				res.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
				return res;
			}
			res = validateAccount(destAccount, amount, "C");
			if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR))
				return res;
		}
		if (!custType.equalsIgnoreCase(MSGConstants.BRANCH_USER)) {
			destAccount = DataUtils.getAccount(destAcc);
			res = validateAccount(destAccount, amount, "C");
			if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR))
				return res;
		}
		if (custType != null && !custType.trim().equalsIgnoreCase("")) {
			Session session = HBUtil.getSessionFactory().openSession();
			Transaction t = session.beginTransaction();
			if (custType.equalsIgnoreCase(MSGConstants.BRANCH_USER)) {
				logger.error("Source Branch Code::>>" + Integer.valueOf(sourceAcc.substring(0, 3)));
				logger.error("Destination Branch Code::>>" + Integer.valueOf(destAcc.substring(0, 3)));
				if (Integer.valueOf(sourceAcc.substring(0, 3)) == Integer.valueOf(destAcc.substring(0, 3))) {
					logger.error("<<<<<<<<<<<::: Same branch deposit transaction :::>>>>>>>>>>>>>>");
					int setNo = VoucherMPOS.getNextSetNo();
					int scrollNo = VoucherMPOS.getNextScrollNo();

					TransactionValidationResponse response = VoucherMPOS.creditBranch(
							"" + destAccount.getId().getPrdAcctId(), destAccount.getId().getLbrCode(),
							Double.parseDouble(amount.trim()), MSGConstants.MPOS_CASH_DEPOSIT, "MPOS", setNo, scrollNo,
							scrollNo, rrn, session);
					// TransactionValidationResponse
					// response=VoucherMPOS.creditBranch(""+sourceAccount.getId().getPrdAcctId(),
					// sourceAccount.getId().getLbrCode(),
					// Double.parseDouble(amount.trim()),
					// MSGConstants.MPOS_CASH_DEPOSIT, "MPOS",
					// setNo,scrollNo,scrollNo,rrn);
					if (response.getResponse().trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
						D350078 d350078 = DataUtils.getMobNo(destAccount.getCustNo() + "", session);
						res.setMobNo(d350078 != null ? d350078.getId().getMobileNo() : "0");
						t.commit();
						session = null;
						t = null;
						String value = (String.valueOf(setNo) + "~" + String.valueOf(scrollNo) + "~"
								+ response.getBatchCode() + "~0");
						res.setOutput(value);
						res.setBatchCode(response.getBatchCode());
						res.setResponse(MSGConstants.SUCCESS);
						res.setErrorMsg(MSGConstants.SUCCESS_MSG);
						res.setSetNo(String.valueOf(setNo));
						res.setScrollNo(String.valueOf(scrollNo));
						res.setRrn(rrn);
						response = null;
						return res;
					} else {
						session.close();
						session = null;
						t = null;
						res.setResponse(MSGConstants.ERROR);
						res.setErrorMsg(MSGConstants.TRANSACTION_FAILURE);
						return res;
					}
				}
				Date opdate = DataUtils.getOpenDate(Integer.valueOf(sourceAcc.substring(0, 3)));
				logger.error("<<<<<<<<<<<::: Other branch deposit Transaction :::>>>>>>>>>>>>>>");
				int setNo = VoucherMPOS.getNextSetNo();
				int scrollNo = VoucherMPOS.getNextScrollNo();
				int reconNo = VoucherMPOS.getNextReconNo(Integer.valueOf(sourceAcc.substring(0, 3).trim()));

				int setNoABB = VoucherMPOS.getNextSetNo();
				int scrollNoAbb = VoucherMPOS.getNextScrollNo();
				int scrollNoAbb1 = VoucherMPOS.getNextScrollNo();
				int scrollNoAbb2 = VoucherMPOS.getNextScrollNo();

				logger.error("reconNo::>>" + reconNo);

				int benSetNo = VoucherMPOS.getNextSetNo();
				int benScrollNo = VoucherMPOS.getNextScrollNo();
				int scrollNoNew = VoucherMPOS.getNextScrollNo();
				String perticulars = "ReconNo =" + reconNo + " FromBrCode ="
						+ Integer.valueOf(sourceAcc.substring(0, 3).trim()) + " AcctId = "
						+ destAccount.getId().getPrdAcctId().substring(0, 24) + "";
				logger.error("perticulars::>>" + perticulars);
				// TransactionValidationResponse
				// resp2=VoucherMPOS.creditABB(""+MSGConstants.ABB_ACC,
				// Integer.valueOf(sourceAcc.substring(0,3).trim()),
				// Double.valueOf(amount), perticulars,
				// "MPOS",setNo,scrollNo,scrollNo, rrn,reconNo,"ABB");
				// TransactionValidationResponse
				// resp3=VoucherMPOS.debitABB(""+MSGConstants.ABB_ACC,
				// Integer.valueOf(destAcc.substring(0,3).trim()),
				// Double.valueOf(amount),perticulars,
				// MSGConstants.ABBMPOS,benSetNo,scrollNoNew,scrollNoNew,
				// rrn,reconNo,"DEPOSIT");
				// TransactionValidationResponse
				// resp1=VoucherMPOS.creditBranch(""+destAccount.getId().getPrdAcctId(),
				// destAccount.getId().getLbrCode(),
				// Double.parseDouble(amount.trim()),
				// "FromBrcode="+Integer.valueOf(sourceAcc.substring(0,3).trim())+"/"+MSGConstants.MPOS_CASH_DEPOSIT,
				// MSGConstants.ABBMPOS, benSetNo,benScrollNo,benScrollNo,rrn);

				TransactionValidationResponse resp2 = VoucherMPOS.creditABB("" + MSGConstants.ABB_ACC,
						Integer.valueOf(sourceAcc.substring(0, 3).trim()), Double.valueOf(amount), perticulars, "MPOS",
						setNo, scrollNoAbb, scrollNoAbb, rrn, reconNo, "ABB", session);
				TransactionValidationResponse resp3 = VoucherMPOS.debitABB("" + MSGConstants.ABB_ACC,
						Integer.valueOf(destAcc.substring(0, 3).trim()), Double.valueOf(amount), perticulars,
						MSGConstants.ABBMPOS, setNoABB, scrollNoAbb2, scrollNoAbb2, rrn, reconNo, "DEPOSIT", session);
				TransactionValidationResponse resp1 = VoucherMPOS.creditBranch("" + destAccount.getId().getPrdAcctId(),
						destAccount.getId().getLbrCode(), Double.parseDouble(amount.trim()),
						"FromBrcode=" + Integer.valueOf(sourceAcc.substring(0, 3).trim()) + "/"
								+ MSGConstants.MPOS_CASH_DEPOSIT,
						MSGConstants.ABBMPOS, setNoABB, scrollNoAbb1, scrollNoAbb1, rrn, session);

				VoucherMPOS.updateBalanceABB(Double.valueOf(amount.trim()),
						Integer.valueOf(sourceAcc.substring(0, 3).trim()), "C", "ABB");
				VoucherMPOS.updateBalanceABB(Double.valueOf(amount.trim()),
						Integer.valueOf(destAcc.substring(0, 3).trim()), "D", "ABB");
				VoucherMPOS.updateBalanceABB(Double.valueOf(amount.trim()), destAccount.getId().getLbrCode(), "C",
						"" + destAccount.getId().getPrdAcctId().substring(0, 8).trim());
				if (resp1.getResponse().trim().equalsIgnoreCase(MSGConstants.SUCCESS)
						&& resp2.getResponse().trim().equalsIgnoreCase(MSGConstants.SUCCESS)
						&& resp3.getResponse().trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
					String batchCodes[] = Props.getBatchProperty("MPOS").split("~");
					D001004 d04OnlineBatchName = VoucherMPOS.getBatchNameFromBatchCode(batchCodes[0]);
					String onlineBatchName = d04OnlineBatchName.getValue().trim();
					String batchCodes1[] = Props.getBatchProperty(MSGConstants.ABBMPOS).split("~");
					D001004 d001004 = VoucherMPOS.getBatchNameFromBatchCode(batchCodes1[0]);
					String benBatchCode = d001004.getValue().trim();
					logger.error("sourceAcc::>>" + sourceAcc);
					logger.error("DestACc::>>" + destAcc);
					logger.error("Amount::>>>" + amount);
					logger.error("reconNo::>>" + reconNo);
					logger.error("Opdate::>>>" + opdate);
					logger.error("onlineBatchName::>>" + onlineBatchName);
					logger.error("benBatchCode::>>" + benBatchCode);
					logger.error("setNo::>>" + setNo);
					logger.error("scrollNo::>>" + scrollNo);
					logger.error("benSetNo:>>" + benSetNo);
					logger.error("benScrollNo::>>" + benScrollNo);
					// logger.error("sourceAccount.getId().getPrdAcctId()::>>"+sourceAccount.getId().getPrdAcctId());
					// D100001
					// d100001=VoucherMPOS.prepareReconObj(Integer.valueOf(sourceAcc.substring(0,3)),
					// reconNo, opdate, 999999, onlineBatchName, benBatchCode,
					// setNo, scrollNo, benSetNo, scrollNoNew,
					// scrollNoNew,Integer.valueOf(destAcc.substring(0,3)),""+destAccount.getId().getPrdAcctId()
					// ,Double.valueOf(amount),"C");
					D100001 d100001 = VoucherMPOS.prepareReconObjBranch(Integer.valueOf(sourceAcc.substring(0, 3)),
							reconNo, opdate, 999999, onlineBatchName, benBatchCode, setNo, scrollNoAbb, setNoABB,
							scrollNoAbb1, scrollNoAbb2, Integer.valueOf(destAcc.substring(0, 3)),
							"" + destAccount.getId().getPrdAcctId(), Double.valueOf(amount), "C");
					logger.error("d100001::>>>" + d100001);

					D100002 d100002 = VoucherMPOS.prepareRecon2ObjBranch(Integer.valueOf(sourceAcc.substring(0, 3)),
							reconNo, opdate, 999999, onlineBatchName, benBatchCode, setNo, scrollNoAbb, setNoABB,
							scrollNoAbb1, scrollNoAbb2, Integer.valueOf(destAcc.substring(0, 3)),
							"" + destAccount.getId().getPrdAcctId(), Double.valueOf(amount), "C");
					try {
						session.save(d100001);
						session.save(d100002);
						D350078 d350078 = DataUtils.getMobNo(sourceAccount.getCustNo() + "", session);
						session.close();
						session = null;
						res.setMobNo(d350078 != null ? d350078.getId().getMobileNo() : "0");
						t.commit();
						t = null;
					} catch (Exception e) {
						e.printStackTrace();
					}
					String value = (String.valueOf(setNo) + "~" + String.valueOf(scrollNo) + "~" + resp1.getBatchCode()
							+ "~0");
					res.setOutput(value);
					res.setBatchCode(resp1.getBatchCode());
					res.setResponse(MSGConstants.SUCCESS);
					res.setErrorMsg(MSGConstants.SUCCESS_MSG);
					res.setSetNo(String.valueOf(setNo));
					res.setScrollNo(String.valueOf(scrollNo));
					res.setRrn(rrn);
					resp1 = null;
					resp2 = null;
					resp3 = null;
					t.commit();
					session.close();
					session = null;
					t = null;
					return res;
				} else {
					t.commit();
					session.close();
					session = null;
					t = null;
					res.setResponse(MSGConstants.ERROR);
					res.setErrorMsg(MSGConstants.TRANSACTION_FAILURE);
					return res;
				}
			} else if (custType.equalsIgnoreCase(MSGConstants.BUSINESS_CORRESPONDANCE)) {
				int setNo = VoucherMPOS.getNextSetNo();
				int scrollNo = VoucherMPOS.getNextScrollNo();
				TransactionValidationResponse response = new VoucherMPOS().drCrMerchantOrBC(
						sourceAccount.getId().getPrdAcctId(), sourceAccount.getId().getLbrCode(),
						destAccount.getId().getPrdAcctId(), destAccount.getId().getLbrCode(), Double.valueOf(amount),
						MSGConstants.MPOS_CASH_DEPOSIT, "MPOS", setNo, scrollNo, VoucherMPOS.getNextScrollNo(), rrn);
				// logger.error("Balance::>>"+CoreTransactionMPOS.balance(sourceAccount.getId().getLbrCode(),sourceAccount.getId().getPrdAcctId(),
				// Double.parseDouble(amount), "D"));
				// logger.error("Balance::>>"+CoreTransactionMPOS.balance(destAccount.getId().getLbrCode(),destAccount.getId().getPrdAcctId(),
				// Double.parseDouble(amount), "C"));

				if (response.getResponse().trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
					String bal = CoreTransactionMPOS.balance(sourceAccount.getId().getLbrCode(),
							sourceAccount.getId().getPrdAcctId(), Double.parseDouble(amount), "D");
					logger.error("Balance::>>" + bal);
					logger.error("Balance::>>" + CoreTransactionMPOS.balance(destAccount.getId().getLbrCode(),
							destAccount.getId().getPrdAcctId(), Double.parseDouble(amount), "C"));
					D350078 d350078 = DataUtils.getMobNo(sourceAccount.getCustNo() + "", session);
					session.close();
					session = null;
					res.setMobNo(d350078 != null ? d350078.getId().getMobileNo() : "0");
					String value = (String.valueOf(setNo) + "~" + String.valueOf(scrollNo) + "~"
							+ response.getBatchCode() + "~" + bal);
					res.setOutput(value);
					res.setBatchCode(response.getBatchCode());
					res.setResponse(MSGConstants.SUCCESS);
					res.setErrorMsg(MSGConstants.SUCCESS_MSG);
					res.setSetNo(String.valueOf(setNo));
					res.setScrollNo(String.valueOf(scrollNo));
					res.setRrn(rrn);
					response = null;
					return res;
				} else {
					res.setResponse(MSGConstants.ERROR);
					res.setErrorMsg(MSGConstants.TRANSACTION_FAILURE);
					return res;
				}
			} else if (custType.equalsIgnoreCase(MSGConstants.MERCHANT)) {
				int setNo = VoucherMPOS.getNextSetNo();
				int scrollNo = VoucherMPOS.getNextScrollNo();
				TransactionValidationResponse response = new VoucherMPOS().drCrMerchantOrBC(
						sourceAccount.getId().getPrdAcctId(), sourceAccount.getId().getLbrCode(),
						destAccount.getId().getPrdAcctId(), destAccount.getId().getLbrCode(), Double.valueOf(amount),
						MSGConstants.MPOS_CASH_DEPOSIT, "MPOS", setNo, scrollNo, VoucherMPOS.getNextScrollNo(), rrn);

				if (response.getResponse().trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
					String bal = CoreTransactionMPOS.balance(sourceAccount.getId().getLbrCode(),
							sourceAccount.getId().getPrdAcctId(), Double.parseDouble(amount), "D");
					logger.error("Balance::>>" + bal);
					logger.error("Balance::>>" + CoreTransactionMPOS.balance(destAccount.getId().getLbrCode(),
							destAccount.getId().getPrdAcctId(), Double.parseDouble(amount), "C"));

					D350078 d350078 = DataUtils.getMobNo(sourceAccount.getCustNo() + "", session);
					session.close();
					session = null;
					res.setMobNo(d350078 != null ? d350078.getId().getMobileNo() : "0");

					String value = (String.valueOf(setNo) + "~" + String.valueOf(scrollNo) + "~"
							+ response.getBatchCode() + "~" + bal);
					res.setOutput(value);
					res.setBatchCode(response.getBatchCode());
					res.setResponse(MSGConstants.SUCCESS);
					res.setErrorMsg(MSGConstants.SUCCESS_MSG);
					res.setSetNo(String.valueOf(setNo));
					res.setScrollNo(String.valueOf(scrollNo));
					res.setRrn(rrn);
					logger.error("Value::>>" + value);
					response = null;
					return res;
				} else {
					res.setResponse(MSGConstants.ERROR);
					res.setErrorMsg(MSGConstants.TRANSACTION_FAILURE);
					res.setRrn(rrn);
					return res;
				}
			} else if (custType.trim().equalsIgnoreCase(MSGConstants.PIG_ME_DEPOSIT)) {
				int setNo = VoucherMPOS.getNextSetNo();
				int scrollNo = VoucherMPOS.getNextScrollNo();
				try {
					Criteria criteria1 = session.createCriteria(D047003.class);
					criteria1.add(Restrictions.eq("id.lbrCode", destAccount.getId().getLbrCode()));
					criteria1.add(Restrictions.eq("id.custPrdAcctId", destAccount.getId().getPrdAcctId()));
					List<D047003> list = criteria1.list();
					if (list != null && list.size() > 0) {
						if (Double.valueOf(amount) % 10 != 0) {
							res.setResponse(MSGConstants.ERROR);
							res.setErrorMsg(MSGConstants.INVALID_AMOUNT_NEW);
							return res;
						}
					}
					TransactionValidationResponse response = VoucherMPOS.otherBranchVouchers(
							sourceAccount.getId().getLbrCode(), sourceAccount.getId().getPrdAcctId(),
							destAccount.getId().getLbrCode(), destAccount.getId().getPrdAcctId(), "PIGMEDEPOSIT",
							MSGConstants.MPOS_PIGME_DEPOSIT, Double.valueOf(amount.trim()), rrn);
					if (response.getResponse().trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
						String value = (response.getSetNo() + "~" + response.getScrollNo() + "~"
								+ response.getBatchCode() + "~" + response.getBalance());
						res.setOutput(value);
						res.setBatchCode(response.getBatchCode());
						res.setResponse(MSGConstants.SUCCESS);
						res.setErrorMsg(MSGConstants.SUCCESS_MSG);
						res.setSetNo(String.valueOf(setNo));
						res.setScrollNo(String.valueOf(scrollNo));
						res.setRrn(rrn);
						response = null;
						return res;
					} else {
						res.setResponse(MSGConstants.ERROR);
						res.setErrorMsg(MSGConstants.TRANSACTION_FAILURE);
						return res;
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e);
					res.setResponse(MSGConstants.ERROR);
					res.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
					res.setRrn(rrn);
					return res;
				} finally {
					if (session != null && session.isOpen())
						session.close();
					session = null;
				}
			} else if (custType.trim().equalsIgnoreCase(MSGConstants.LOAN_ACC)) {
				// int setNo=VoucherMPOS.getNextSetNo();
				// int scrollNo=VoucherMPOS.getNextScrollNo();
				try {
					HashMap<String, String> response = VoucherCommon.otherBranchLoanVouchers(
							sourceAccount.getId().getLbrCode(), sourceAccount.getId().getPrdAcctId(),
							destAccount.getId().getLbrCode(), destAccount.getId().getPrdAcctId(),
							MSGConstants.PIGMEDEPOSIT, MSGConstants.MPOS_PIGME_DEPOSIT, Double.valueOf(amount), rrn, destAcc);
					if (response.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
						String value = (response.get(Code.SETNO) + "~" + response.get(Code.SCROLLNO) + "~MPOS~" + "0");
						res.setOutput(value);
						res.setBatchCode("MPOS");
						res.setResponse(MSGConstants.SUCCESS);
						res.setErrorMsg(MSGConstants.SUCCESS_MSG);
						res.setSetNo(String.valueOf(response.get(Code.SETNO)));
						res.setScrollNo(String.valueOf(response.get(Code.SETNO)));
						res.setRrn(rrn);
						response = null;
						return res;
					} else {
						res.setResponse(MSGConstants.ERROR);
						res.setErrorMsg(MSGConstants.TRANSACTION_FAILURE);
						return res;
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e);
					res.setResponse(MSGConstants.ERROR);
					res.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
					res.setRrn(rrn);
					return res;
				} finally {
					if (session != null && session.isOpen())
						session.close();
					session = null;
				}

			}
			res.setResponse(MSGConstants.ERROR);
			res.setErrorMsg(MSGConstants.INVALID_TRANSACTION_TYPE);
			res.setRrn(rrn);
			return res;
		}
		res.setResponse(MSGConstants.ERROR);
		res.setErrorMsg(MSGConstants.INVALID_TRANSACTION_TYPE);
		res.setRrn(rrn);
		return res;
	}

	public static TransactionValidationResponse validateOrDoPayement(String sourceAcc, String destAcc, String custType,
			String amount, String paymentMode, String transType) {
		TransactionValidationResponse res = new TransactionValidationResponse();
		if (paymentMode.equalsIgnoreCase(MSGConstants.V)) {
			D009022 sourceAccount = null;
			if (!custType.equalsIgnoreCase(MSGConstants.BRANCH_USER)) {
				sourceAccount = DataUtils.getAccount(sourceAcc);
				if (sourceAccount == null) {
					res.setResponse(MSGConstants.ERROR);
					res.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
					return res;
				}
			}
			Session session = HBUtil.getSessionFactory().openSession();
			D009022 destAccount = null;// DataUtils.getAccount(destAcc);
			if (sourceAccount != null) {
				if (custType.toUpperCase().contains("PGM")) {
					logger.error("PGM Account");
					res = validateAccount(sourceAccount, amount, "AGENT");
					if (res != null) {
						if (res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
							session.close();
							session = null;
							return res;
						}
						if (res.getResponse().equalsIgnoreCase(MSGConstants.SUCCESS)) {
							res.setMobNo(
									DataUtils.getMobNo(sourceAccount.getCustNo() + "", session).getId().getMobileNo());
							res.setResponse(MSGConstants.SUCCESS);
							res.setErrorMsg(MSGConstants.SUCCESS_MSG);
						}
					}
					logger.error("PGM Account");
					destAccount = DataUtils.getAccount(destAcc);
					if (destAccount == null) {
						res.setResponse(MSGConstants.ERROR);
						res.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
						return res;
					}
					res = validateAccount(destAccount, amount, "C");
					if (res != null) {
						if (res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
							session.close();
							session = null;
							return res;
						}
						if (res.getResponse().equalsIgnoreCase(MSGConstants.SUCCESS)) {
							res.setMobNo(
									DataUtils.getMobNo(destAccount.getCustNo() + "", session).getId().getMobileNo());
							res.setResponse(MSGConstants.SUCCESS);
							res.setErrorMsg(MSGConstants.SUCCESS_MSG);
						}
					}
					if (!custType.equalsIgnoreCase(MSGConstants.BRANCH_USER)) {
						destAccount = DataUtils.getAccount(destAcc);
						res = validateAccount(destAccount, amount, "C");
						if (res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
							session.close();
							session = null;
							return res;
						}
						if (res.getResponse().equalsIgnoreCase(MSGConstants.SUCCESS)) {
							res.setMobNo(
									DataUtils.getMobNo(destAccount.getCustNo() + "", session).getId().getMobileNo());
							res.setResponse(MSGConstants.SUCCESS);
							res.setErrorMsg(MSGConstants.SUCCESS_MSG);
						}
					}
				} else {
					if (!custType.equalsIgnoreCase(MSGConstants.BRANCH_USER)) {
						res = validateAccount(sourceAccount, amount, MSGConstants.DR);
						if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
							session.close();
							session = null;
							return res;
						}
						if (res.getResponse().equalsIgnoreCase(MSGConstants.SUCCESS)) {
							res.setMobNo(
									DataUtils.getMobNo(sourceAccount.getCustNo() + "", session).getId().getMobileNo());
							res.setResponse(MSGConstants.SUCCESS);
							res.setErrorMsg(MSGConstants.SUCCESS_MSG);
						}
					}
				}
			}
			if (custType.equalsIgnoreCase(MSGConstants.BRANCH_USER)) {
				destAccount = DataUtils.getAccount(destAcc);
				if (destAccount == null) {
					session.close();
					session = null;
					res.setResponse(MSGConstants.ERROR);
					res.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
					return res;
				}
				res = validateAccount(destAccount, amount, MSGConstants.CR);
				if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
					session.close();
					session = null;
					return res;
				}
				if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.SUCCESS)) {
					res.setMobNo(DataUtils.getMobNo(destAccount.getCustNo() + "", session).getId().getMobileNo());
					res.setResponse(MSGConstants.SUCCESS);
					res.setErrorMsg(MSGConstants.SUCCESS_MSG);
				}
			}
			if (!custType.equalsIgnoreCase(MSGConstants.BRANCH_USER)) {
				destAccount = DataUtils.getAccount(destAcc);
				res = validateAccount(destAccount, amount, MSGConstants.CR);
				if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
					session.close();
					session = null;
					return res;
				}
				if (res.getResponse().equalsIgnoreCase(MSGConstants.SUCCESS)) {
					res.setMobNo(DataUtils.getMobNo(destAccount.getCustNo() + "", session).getId().getMobileNo());
					res.setResponse(MSGConstants.SUCCESS);
					res.setErrorMsg(MSGConstants.SUCCESS_MSG);
				}
			}
			if (session != null && session.isOpen())
				session.close();
			session = null;
			return res;
		}
		if (paymentMode.equalsIgnoreCase(MSGConstants.P)) {
			if (transType.equalsIgnoreCase(MSGConstants.WITHDRAW)) {
				try {
					return TransactionServiceImpl.doMPOSWithDrawalTransaction(sourceAcc, destAcc, custType, amount,
							DataUtils.getNextRRN());
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e);
					res.setResponse(MSGConstants.ERROR);
					res.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
					return res;
				}
			} else if (transType.equalsIgnoreCase(MSGConstants.DEPOSIT)) {
				try {
					return TransactionServiceImpl.doMPOSDepositTransaction(sourceAcc, destAcc, custType, amount,
							DataUtils.getNextRRN(), MSGConstants.NO, "", "");
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					logger.error(e);
					res.setResponse(MSGConstants.ERROR);
					res.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
					return res;
				}
			}
		}
		return null;
	}

	public static OtherChannelServiceResponse updateBillPaymentRefNo(int lbrCode, String accNo, String consumerNo,
			String operator, double amount, String rrn, String refNo) {
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		OtherChannelServiceResponse response = new OtherChannelServiceResponse();
		Criteria criteria = session.createCriteria(Billpayment.class);
		criteria.add(Restrictions.eq("id.lbrcode", lbrCode));
		criteria.add(Restrictions.eq("accountno", accNo.trim()));
		criteria.add(Restrictions.eq("consumerno", consumerNo.trim()));
		criteria.add(Restrictions.eq("operator", operator.trim()));
		criteria.add(Restrictions.eq("amount", amount));
		criteria.add(Restrictions.eq("id.rrrnno", rrn.trim()));
		criteria.add(Restrictions.eq("drcr", "D"));
		List<Billpayment> list = criteria.list();
		if (list == null || list.isEmpty()) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorCode(ResponseCodes.SYSTEM_ERROR);
			response.setErrorMessage(MSGConstants.RECORD_NOT_FOUND);
			return response;
		}
		Billpayment billpayment = list.get(0);
//		billpayment.setRefno(refNo);
		session.update(billpayment);
		t.commit();
		session.close();
		session = null;
		response.setResponse(MSGConstants.SUCCESS);
		response.setErrorCode(ResponseCodes.SUCCESS);
		response.setErrorMessage(MSGConstants.SUCCESS_MSG);
		return response;
	}

	public static IMPSTransactionResponse reverseLoadBalancePrepaidCard(D009022 d009022, String amount,
			String narration, String mob1, String tType, IMPSTransactionRequest request, D390077 list) {
		IMPSTransactionResponse response = new IMPSTransactionResponse();
		if (null == d009022) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
			return response;
		}
		logger.error("<<<<<<<<<======== reverseLoadBalancePrepaidCard.service =========>>>>>>>>>>>>");
		logger.error("Branch Code::>>" + d009022.getId().getLbrCode() + " accNo::>>" + d009022.getId().getPrdAcctId()
				+ " Amount::>>>" + amount + " narration::>>" + narration + " mob1::>>>" + mob1);
		String rrn = request.getRRNNo();
		if (tType.equalsIgnoreCase(MSGConstants.PREPAID_CARD)) {
			logger.error("Prepaid card load balance transaction");
			try {
				HashMap<String, String> result = VoucherCommon.reversePrepaidCardVoucherEntry(
						d009022.getId().getLbrCode(), d009022.getId().getPrdAcctId(), MSGConstants.PREPAID, narration,
						Double.valueOf(amount.trim()), rrn);
				logger.error("Result:>>>" + result);
				if (null != result) {
					if (result.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
						PrepaidCardLoadBalanceEntry.storeReversePrepaidcardEntry(d009022.getId().getLbrCode(),
								d009022.getId().getPrdAcctId(), request.getRemitterMobile(), request.getTransAmt(), "0",
								list.getId().getCardAlias().trim(), MSGConstants.SUCCESS_MSG, MSGConstants.SUCCESS_MSG,
								MSGConstants.SUCCESS_MSG, ResponseCodes.SUCCESS, rrn);
						response.setResponse(MSGConstants.SUCCESS);
						response.setErrorMessage(MSGConstants.SUCCESS_MSG);
						response.setRrnNo(rrn);
						return response;
					} else {
						PrepaidCardLoadBalanceEntry.storeReversePrepaidcardEntry(d009022.getId().getLbrCode(),
								d009022.getId().getPrdAcctId(), request.getRemitterMobile(), request.getTransAmt(), "0",
								list.getId().getCardAlias().trim(), MSGConstants.TRANSACTION_DECLINED,
								MSGConstants.TRANSACTION_DECLINED, MSGConstants.TRANSACTION_DECLINED,
								ResponseCodes.SYSTEM_ERROR, rrn);
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
						response.setRrnNo(rrn);
						return response;
					}
				} else {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
					response.setRrnNo(rrn);
					return response;
				}
			} catch (Exception e) {
				e.printStackTrace();
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
				response.setRrnNo(rrn);
				response.setErrorCode(ResponseCodes.EXCEPTION_OCCURED);
				return response;
			}
		}
		response.setResponse(MSGConstants.ERROR);
		response.setErrorMessage(MSGConstants.INVALID_TRANSACTION_TYPE);
		return response;
	}
	
	public static IMPSNewTransactionResponse initiateIMPSLoanTransaction(D009022 sourceAccount, D009022 benAcctno,
			String amount, String narration) {
		logger.error("<<<<<< TransactionService.initiateIMPSTransaction >>>>>>>");
		IMPSNewTransactionResponse response = new IMPSNewTransactionResponse();
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		try {
			
			String rrn = DataUtils.getNextRRN();
					logger.error("Same Bank P2A transaction.");
					if (benAcctno == null) {
						
						response.setResCode(MSGConstants.ERROR);
						response.setErrorMsg(MSGConstants.INVALID_BEN_ACC_NO);
						response.setSuccess(false);
						return response;
					}
					
					
					
					TransactionValidationResponse res = TransactionServiceImpl.validateAccount(benAcctno,
							String.valueOf(amount), "C");
					if (res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
						
						response.setResCode(MSGConstants.ERROR);
						response.setErrorMsg(res.getErrorMsg());
						response.setSuccess(false);
						return response;
						
					}
			
					String particular= " Benf:"+benAcctno.getId().getLbrCode()+"/"+benAcctno.getId().getPrdAcctId().substring(0, 8).trim()+"/"
							+String.valueOf(Integer.parseInt(benAcctno.getId().getPrdAcctId().substring(16, 24)))
							+" Rem:"+sourceAccount.getId().getLbrCode()+"/"+sourceAccount.getId().getPrdAcctId().substring(0, 8).trim()+"/" 
							+String.valueOf(Integer.parseInt(sourceAccount.getId().getPrdAcctId().substring(16, 24)));
			
					CoreBankingOperationImpl impl = new CoreBankingOperationImpl();
					HashMap<String, String> resp = impl.loanFundTransferBranch(sourceAccount, benAcctno,
							MSGConstants.TRANS_TYPE,
							"IMPS-P2A-" + rrn + "-" + DateUtil.getcurrentDateString() +particular+ narration,
							Double.valueOf(amount), rrn, session);
					
					if (null != resp) {
						if (resp.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
							response.setResCode("null");
							response.setErrorMsg(MSGConstants.SUCCESS_MSG);
							response.setRrn(rrn);
							response.setSetNo(resp.get(Code.SETNO));
							response.setSetNo(resp.get(Code.SCROLLNO));
							response.setSuccess(true);
							D009022 acc = sourceAccount;
							if (acc != null)
								response.setName(
										acc.getLongName().trim().length() > 9 ? acc.getLongName().trim().substring(0, 9)
												: "NONICKNAME");
							else
								response.setName("NONICKNAME");
							t.commit();
							return response;
							
						} else {
							if (t.isActive())
								t.rollback();
							response.setResCode(MSGConstants.ERROR);
							response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
							response.setRrn(rrn);
							response.setSuccess(false);
							return response;
						}
					}
					impl = null;
					
				return response;
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			if (t.isActive())
				t.rollback();
			response.setResCode(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			response.setSuccess(false);
			return response;
		} finally {
			session.close();
			session = null;
			t = null;
		}
	}
	
	public static TransactionValidationResponse validateBalance(D009022 sourceAccount, String amount) {
		TransactionValidationResponse response = new TransactionValidationResponse();

		String isOdAcc = "";
		ArrayList<Double> lists = new ArrayList<>();
		lists.add(Double.valueOf(sourceAccount.getActClrBalFcy()));
		lists.add(Double.valueOf(sourceAccount.getActTotBalFcy()));
		lists.add(Double.valueOf(sourceAccount.getActTotBalLcy()));
		lists.add(Double.valueOf(sourceAccount.getShdClrBalFcy()));
		lists.add(Double.valueOf(sourceAccount.getShdTotBalFcy()));
		double availBal = Collections.min(lists) - sourceAccount.getTotalLienFcy();
		double transAmount = Double.parseDouble(amount);
		
		if (DataUtils.isOverDraftAccount(sourceAccount.getId().getLbrCode(),
				sourceAccount.getId().getPrdAcctId()) && availBal < transAmount)
			isOdAcc = "Y";
		logger.error("isOdAcc::>>" + isOdAcc);
		if (isOdAcc.equalsIgnoreCase("Y")) {
			CustomerDetails res = DataUtils.validateOdAccount(sourceAccount.getId().getLbrCode(),
					sourceAccount.getId().getPrdAcctId());
			logger.error("res.getResponse()::>>" + res.getResponse());
			if (res.getResponse().equalsIgnoreCase(MSGConstants.SUCCESS)) {
				
				availBal = availBal
						+ res.getOdLimit().getTotSancLimit();
				;
				logger.error("Available Bal::>>" + availBal);
				logger.error("Transaction Amount::>>>" + transAmount);
				if (availBal < transAmount) {
					CustomerDetails resp = DataUtils.validateOdAdhoclimit(sourceAccount.getId().getLbrCode(),
							sourceAccount.getId().getPrdAcctId());
					logger.error("resp.getErrorMsg()::>>" + resp.getErrorMsg());
					logger.error("resp.getErrorMsg()::>>" + resp.getResponse());
					if (resp.getResponse().equalsIgnoreCase(MSGConstants.SUCCESS)) {
//						availBal = Double
//								.valueOf(Double.valueOf(sourceAccount.getActClrBalFcy())
//										- Double.valueOf(sourceAccount.getTotalLienFcy()))
//								+ res.getOdLimit().getTotSancLimit() + resp.getOdAdhocLimit().getTotSancLimit();
						availBal = availBal + resp.getOdAdhocLimit().getTotSancLimit();
						logger.error("Available Bal::>>" + availBal);
						logger.error("Transaction Amount::>>>" + transAmount);
						if (availBal < transAmount) {
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMsg(MSGConstants.INSUFFICIENT_FUNDS);
							response.setRespCode(ResponseCodes.INSUFFICIENT_FUNDS);
							return response;
						}
						response.setResponse(MSGConstants.SUCCESS);
						response.setRespCode(ResponseCodes.SIMSEPAY_SUCCESS);
						response.setErrorMsg(MSGConstants.SUCCESS_MSG);
						return response;
					}
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(resp.getErrorMsg());
					return response;
				}
				logger.error("INSUFFICIENT_FUNDS");
				response.setResponse(MSGConstants.SUCCESS);
				response.setErrorMsg(MSGConstants.SUCCESS_MSG);
				return response;
			}
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(res.getErrorMsg());
			return response;
		}
//	---------Commented Below Code----------------
		
//		double availBal = Double.valueOf(Double.valueOf(sourceAccount.getActClrBalFcy())
//				- Double.valueOf(sourceAccount.getTotalLienFcy()));
		logger.error("Available Bal::>>" + availBal + " Transaction Amount::>>>" + transAmount);

		if (availBal < transAmount) {
			logger.error("INSUFFICIENT_FUNDS");
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.INSUFFICIENT_FUNDS);
			response.setRespCode(ResponseCodes.INSUFFICIENT_FUNDS);
			return response;
		}
		response.setResponse(MSGConstants.SUCCESS);
		response.setRespCode(ResponseCodes.SIMSEPAY_SUCCESS);
		response.setErrorMsg(MSGConstants.SUCCESS_MSG);
		return response;
	
	}
	
	
	public static IMPSTransactionResponse creditUPITransaction(String accNo15digit,	Double amount, String narration,
			String rrn, UPIRequest request) throws Exception {
		IMPSTransactionResponse response = new IMPSTransactionResponse();
		String acctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit);
		int lbrcode = Integer.parseInt(accNo15digit.trim().substring(0, 3));
		logger.error("Credit Account NO 32 Digit::>>" + acctno32digit);
		logger.error("Branch Code::>>" + lbrcode);
		HashMap<String, String> result = VoucherCommon.otherBankCreditVoucherEntryNew(lbrcode, acctno32digit,
				"UPI", narration, amount, rrn);
		logger.error("Result:>>>" + result);
		if (null != result) {
			if (result.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
				UPITransactionEntry.saveUPITrans(lbrcode, acctno32digit, request, result,
						ResponseCodes.SUCCESS, ResponseCodes.SUCCESS_DESC);
				response.setResponse(MSGConstants.SUCCESS);
				response.setErrorMessage(MSGConstants.SUCCESS_MSG);
				response.setRrnNo(rrn);
				response.setErrorCode(ResponseCodes.SUCCESS);
				return response;
				
			} else {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
				response.setRrnNo(rrn);
				response.setErrorCode(ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL);
				return response;
			}
		} else {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
			response.setRrnNo(rrn);
			response.setErrorCode(ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL);
			return response;
		}
		

	}
	
	public static double getMinBal(D009022 account, D009021 productMaster) {
		try {
			/*
			 * D009021 productMaster =
			 * DataUtils.getProductMaster(account.getId().getLbrCode()+"",
			 * account.getId().getPrdAcctId().substring(0, 8).trim());
			 */
			
			if("Y".equalsIgnoreCase(ConfigurationLoader.getParameters(false).getProperty("add_min_balance_YN"))) {
				if("C".equalsIgnoreCase(ConfigurationLoader.getParameters(false).getProperty("min_balance_CBS_or_Property"))) {
					if (account.getChqBookYn() == 'Y')
						return productMaster.getMinBal();
					else
						return productMaster.getMinBalNonChq();
				}else {
					return Double.parseDouble(ConfigurationLoader.getParameters(false).getProperty("min_balance_value"));
				}
			}else return 0;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e);
			return 0;
		}
		
	}
	
	public static IMPSTransactionResponse creditQRUPITransaction(String accNo15digit, Double amount, String narration,
			String rrn, QRUPIRequest request) throws Exception {
		IMPSTransactionResponse response = new IMPSTransactionResponse();
		String acctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit);
		int lbrcode = Integer.parseInt(accNo15digit.trim().substring(0, 3));
		logger.error("Credit Account NO 32 Digit::>>" + acctno32digit);
		logger.error("Branch Code::>>" + lbrcode);
		
		String agentAcctno32digit = AccountDetailsUtil.get32DigitAcctNo(request.getAgentAcctNo());
		int agentLbrcode = Integer.parseInt(request.getAgentAcctNo().trim().substring(0, 3));
		logger.error("Agent Account NO 32 Digit::>>" + agentAcctno32digit);
		logger.error("Agent Branch Code::>>" + agentLbrcode);
		

		String debAccNoGL = VoucherCommon.get32DigitAcctNo(ConfigurationLoader.getParameters(false).getProperty("Default_MBBEN_GL"),
					0, 0);// + " 000000000000000000000000";
		try {
			
			debAccNoGL = VoucherCommon.get32DigitAcctNo(
					VoucherCommon.getSysParameter(ConfigurationLoader.getParameters(false).getProperty("UPI_QR_GL")).trim(), 0,
						0);
		} catch (Exception ex) {
			logger.error("Exception", ex);
		}
		int glBrCode = Integer.valueOf(VoucherCommon.getSysParameter(MSGConstants.MBBRANCH).trim());
		
		
		
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		
		String narrationCr = "QRUPI/" + request.getRrn() +"/"+request.getAccountNo()+"/"+request.getRemark();
		String narrationDr = "QRUPI/" + request.getRrn() +"/"+request.getAccountNo();
		
		HashMap<String, String> result1 = VoucherCommon.qrCodeUPITransactions(agentLbrcode, agentAcctno32digit, amount, rrn, session, t, 
				narrationCr, glBrCode, debAccNoGL, narrationDr, "QRUPI", "DR");
		
		if (null != result1) {
			if (result1.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
				
				narrationCr = "QRUPI/" + request.getRrn() +"/"+request.getAgentAcctNo()+"/"+request.getRemark();
				narrationDr = "QRUPI/" + request.getRrn() +"/"+request.getAccountNo()+"/"+request.getRemark();
				HashMap<String, String> result2 = VoucherCommon.qrCodeUPITransactions(lbrcode, acctno32digit, amount, rrn, session, t, 
						narrationCr, agentLbrcode, agentAcctno32digit, narrationDr, "QRUPI", "NA");
				if (null != result2) {
					if (result2.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
						UPITransactionEntry.saveQRUPITrans(lbrcode, acctno32digit, agentLbrcode, agentAcctno32digit, request, result2,
								ResponseCodes.SUCCESS, ResponseCodes.SUCCESS_DESC, t, session);
						response.setResponse(MSGConstants.SUCCESS);
						response.setErrorMessage(MSGConstants.SUCCESS_MSG);
						response.setRrnNo(rrn);
						response.setErrorCode(ResponseCodes.SUCCESS);
						return response;
						
					} else {
						if (t.isActive())
							t.rollback();
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
						response.setRrnNo(rrn);
						response.setErrorCode(ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL);
						return response;
					}
				} else {
					if (t.isActive())
						t.rollback();
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
					response.setRrnNo(rrn);
					response.setErrorCode(ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL);
					return response;
				}
			}else {
				if (t.isActive())
					t.rollback();
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
				response.setRrnNo(rrn);
				response.setErrorCode(ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL);
				return response;
			}
		}else {
			if (t.isActive())
				t.rollback();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
			response.setRrnNo(rrn);
			response.setErrorCode(ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL);
			return response;
		}
		
		
		

	}
	
	public static TransactionValidationResponse validateBalance(D009022 sourceAccount, String amount, Session session) throws Exception {
		TransactionValidationResponse response = new TransactionValidationResponse();

		String isOdAcc = "";
		ArrayList<Double> lists = new ArrayList<>();
		lists.add(Double.valueOf(sourceAccount.getActClrBalFcy()));
		lists.add(Double.valueOf(sourceAccount.getActTotBalFcy()));
		lists.add(Double.valueOf(sourceAccount.getActTotBalLcy()));
		lists.add(Double.valueOf(sourceAccount.getShdClrBalFcy()));
		lists.add(Double.valueOf(sourceAccount.getShdTotBalFcy()));
		double availBal = Collections.min(lists) - sourceAccount.getTotalLienFcy();
		double transAmount = Double.parseDouble(amount);
		D009021 creditProductMaster = session.get(D009021.class, new D009021Id(sourceAccount.getId().getLbrCode(), 
				sourceAccount.getId().getPrdAcctId().substring(0, 8).trim()));
		if (DataUtils.isOverDraftAccount(sourceAccount.getId().getLbrCode(),
				sourceAccount.getId().getPrdAcctId(),creditProductMaster) && availBal < transAmount)
			isOdAcc = "Y";
		logger.error("isOdAcc::>>" + isOdAcc);
		if (isOdAcc.equalsIgnoreCase("Y")) {
			CustomerDetails res = DataUtils.validateOdAccount(sourceAccount.getId().getLbrCode(),
					sourceAccount.getId().getPrdAcctId());
			logger.error("res.getResponse()::>>" + res.getResponse());
			if (res.getResponse().equalsIgnoreCase(MSGConstants.SUCCESS)) {
				
				availBal = availBal
						+ res.getOdLimit().getTotSancLimit();
				;
				logger.error("Available Bal::>>" + availBal);
				logger.error("Transaction Amount::>>>" + transAmount);
				if (availBal < transAmount) {
					CustomerDetails resp = DataUtils.validateOdAdhoclimit(sourceAccount.getId().getLbrCode(),
							sourceAccount.getId().getPrdAcctId());
					logger.error("resp.getErrorMsg()::>>" + resp.getErrorMsg());
					logger.error("resp.getErrorMsg()::>>" + resp.getResponse());
					if (resp.getResponse().equalsIgnoreCase(MSGConstants.SUCCESS)) {
//						availBal = Double
//								.valueOf(Double.valueOf(sourceAccount.getActClrBalFcy())
//										- Double.valueOf(sourceAccount.getTotalLienFcy()))
//								+ res.getOdLimit().getTotSancLimit() + resp.getOdAdhocLimit().getTotSancLimit();
						availBal = availBal + resp.getOdAdhocLimit().getTotSancLimit();
						logger.error("Available Bal::>>" + availBal);
						logger.error("Transaction Amount::>>>" + transAmount);
						if (availBal < transAmount) {
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMsg(MSGConstants.INSUFFICIENT_FUNDS);
							response.setRespCode(ResponseCodes.INSUFFICIENT_FUNDS);
							return response;
						}
						response.setResponse(MSGConstants.SUCCESS);
						response.setRespCode(ResponseCodes.SIMSEPAY_SUCCESS);
						response.setErrorMsg(MSGConstants.SUCCESS_MSG);
						return response;
					}
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(resp.getErrorMsg());
					return response;
				}
				logger.error("INSUFFICIENT_FUNDS");
				response.setResponse(MSGConstants.SUCCESS);
				response.setErrorMsg(MSGConstants.SUCCESS_MSG);
				return response;
			}
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(res.getErrorMsg());
			return response;
		}
//	---------Commented Below Code----------------
		
//		double availBal = Double.valueOf(Double.valueOf(sourceAccount.getActClrBalFcy())
//				- Double.valueOf(sourceAccount.getTotalLienFcy()));
		logger.error("Available Bal::>>" + availBal + " Transaction Amount::>>>" + transAmount);

		if (availBal < transAmount) {
			logger.error("INSUFFICIENT_FUNDS");
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.INSUFFICIENT_FUNDS);
			response.setRespCode(ResponseCodes.INSUFFICIENT_FUNDS);
			return response;
		}
		response.setResponse(MSGConstants.SUCCESS);
		response.setRespCode(ResponseCodes.SIMSEPAY_SUCCESS);
		response.setErrorMsg(MSGConstants.SUCCESS_MSG);
		return response;
	
	}
	

}