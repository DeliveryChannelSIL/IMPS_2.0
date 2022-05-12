package com.sil.ws;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.util.nanolog.LogWriter;
import org.util.nanolog.LoggerType;

import com.google.gson.Gson;
import com.sil.commonswitch.ATMTransactionServiceIMPL;
import com.sil.commonswitch.BillPaymentTransactionEntry;
import com.sil.commonswitch.DataUtils;
import com.sil.commonswitch.OtherChannelServiceResponse;
import com.sil.commonswitch.P2AReversal;
import com.sil.commonswitch.P2ATransaction;
import com.sil.commonswitch.P2PTransactionEntry;
import com.sil.commonswitch.PrepaidCardLoadBalanceEntry;
import com.sil.commonswitch.SimSePayTransactionEntry;
import com.sil.constants.MSGConstants;
import com.sil.constants.ResponseCodes;
import com.sil.constants.RtgsNeftHostToHostConstants;
import com.sil.dao.AccountMasterServiceImpl;
import com.sil.dao.CustomerMasterServiceImpl;
import com.sil.dao.RTGSNEFTServiceImpl;
import com.sil.domain.ATMTransactionRequest;
import com.sil.domain.ATMTransactionResponse;
import com.sil.domain.AccountOpenRequest;
import com.sil.domain.AccountOpenResponse;
import com.sil.domain.CheckAccountStatus;
import com.sil.domain.CustDetailsResponse;
import com.sil.domain.CustomerDetails;
import com.sil.domain.CustomerPhysicalCardOnboardingreq;
import com.sil.domain.DebitAccount;
import com.sil.domain.GetStatus;
import com.sil.domain.IMPSChargesRequest;
//import com.sil.domain.IMPSChargesRequest;
import com.sil.domain.IMPSChargesResponse;
import com.sil.domain.IMPSNewTransactionResponse;
import com.sil.domain.IMPSTransactionRequest;
import com.sil.domain.IMPSTransactionResponse;
import com.sil.domain.QRUPIRequest;
import com.sil.domain.ReverseDebitAccount;
import com.sil.domain.RtgsNeftTransactionResponse;
import com.sil.domain.TransactionValidationResponse;
import com.sil.domain.UPIRequest;
import com.sil.hbm.Billpayment;
import com.sil.hbm.D009022;
import com.sil.hbm.D350044;
import com.sil.hbm.D350059;
import com.sil.hbm.D350078;
import com.sil.hbm.D390077;
import com.sil.hbm.D946022;
import com.sil.hbm.IBFundTransactionRequest;
import com.sil.hbm.UPITransaction;
import com.sil.operation.CoreBankingOperationImpl;
import com.sil.prop.ConfigurationLoader;
import com.sil.service.RequestServiceImpl;
import com.sil.service.TransactionServiceImpl;
import com.sil.util.DateUtil;
import com.sil.util.DateUtility;
import com.sil.util.HBUtil;

@Path("/transaction")
public class TransactionService {
	public static Logger logger = Logger.getLogger(TransactionService.class);

	private static final LogWriter logWriter = new LogWriter(null, "trlog", true);

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/mposCashWithDrawal")
	public TransactionValidationResponse mposCashWithDrawl(@QueryParam(value = "param1") String sourceAcc,
			@QueryParam(value = "param2") String destAcc, @QueryParam(value = "param3") String custType,
			@QueryParam(value = "param4") String amount) {
		TransactionValidationResponse response = new TransactionValidationResponse();
		logger.error("<<<<:: CASH WITHDRWAL REQUEST ::>>>>");
		logger.error("cash Withdrawal Parameters::>> sourceAcc::>>" + sourceAcc + " destAcc::>>" + destAcc
				+ " custType::>>" + custType + " amount::>>" + amount);
		try {
			if (sourceAcc == null || sourceAcc.trim().length() != 15) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_SOURCE_ACCOUNT_NO);
				return response;
			}
			if (custType == null || custType.trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_CUST_TYPE);
				return response;
			}
			if (!custType.equalsIgnoreCase(MSGConstants.BRANCH_USER)) {
				if (destAcc == null || destAcc.trim().length() != 15) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.INVALID_DEST_ACCOUNT_NO);
					return response;
				}
			}
			if (amount == null || amount.trim().length() < 1 || Double.valueOf(amount) == 0d) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_AMOUNT);
				return response;
			}
			response = TransactionServiceImpl.doMPOSWithDrawalTransaction(sourceAcc, destAcc, custType, amount,
					DataUtils.getNextRRN());
			logger.error("Cash Withdrawal Response::>>" + new Gson().toJson(response));
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ERROR:", e);
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/mposCashDeposit")
	public TransactionValidationResponse mposCashDeposit(@QueryParam(value = "param1") String sourceAcc,
			@QueryParam(value = "param2") String destAcc, @QueryParam(value = "param3") String custType,
			@QueryParam(value = "param4") String amount, @QueryParam(value = "param5") String forceTransactionYn,
			@QueryParam(value = "param6") String ownerId, @QueryParam(value = "param7") String deviceId) {
		logger.error("<<<:: MPOS CASH DEPOSIT ::>>>");
		logger.error("Cash Deposit Parameters:>> sourceAcc::>>" + sourceAcc + " destAcc::>>" + destAcc + " custType::>>"
				+ custType + " amount::>>" + amount);
		TransactionValidationResponse response = new TransactionValidationResponse();
		try {
			if (!custType.equalsIgnoreCase("BR")) {
				if (sourceAcc == null || sourceAcc.trim().length() != 15) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.INVALID_SOURCE_ACCOUNT_NO);
					return response;
				}
			}
			if (custType == null || custType.trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_CUST_TYPE);
				return response;
			}
			if (!custType.equalsIgnoreCase(MSGConstants.BRANCH_USER)) {
				if (destAcc == null || destAcc.trim().length() != 15) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.INVALID_DEST_ACCOUNT_NO);
					return response;
				}
			}
			if (amount == null || amount.trim().length() < 1 || Double.valueOf(amount) == 0d) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_AMOUNT);
				return response;
			}
			response = TransactionServiceImpl.doMPOSDepositTransaction(sourceAcc, destAcc, custType, amount,
					DataUtils.getNextRRN(), forceTransactionYn, ownerId, deviceId);
			logger.error("MPOS Cash deposit Response ::>>" + new Gson().toJson(response));
			return response;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("ERROR:", e);
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/impsTransactionOtherBank")
	public IMPSTransactionResponse impsTransactionOtherBank(IMPSTransactionRequest request) {
		IMPSTransactionResponse response = new IMPSTransactionResponse();
		try (org.util.nanolog.Logger trLogger = org.util.nanolog.Logger.getLogger(LoggerType.BUFFERED, logWriter)) {
			logger.error("<<<<:: IMPS TRANSACTION ::>>>>>");
			logger.error("IMPS Request Received::>>" + new Gson().toJson(request));
			trLogger.info("IMPS Request Received::>>" + new Gson().toJson(request));
			System.out.println("TransactionService.impsTransactionOtherBank() " + new Gson().toJson(request));
			if (request == null) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_REQUEST);
				trLogger.info("IMPS P2P Response::>>" + new Gson().toJson(response));
				return response;
			}
			if (request.getTransType().equalsIgnoreCase(MSGConstants.P2P_TRANSACTION)) {
				if (request.getRemitterMobile().trim().length() != 12) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorCode(MSGConstants.INVALID_REMITTER_MOB_NO);
					trLogger.info("IMPS P2P Response::>>" + new Gson().toJson(response));
					return response;
				}
				if (request.getRemitterMMID().trim().length() != 7) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorCode(MSGConstants.INVALID_REMITTER_MMID);
					trLogger.info("IMPS P2P Response::>>" + new Gson().toJson(response));
					return response;
				}
				if (request.getRemitterAccNo().trim().length() < 1) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorCode(MSGConstants.INVALID_REMITTER_ACC_NO);
					trLogger.info("IMPS P2P Response::>>" + new Gson().toJson(response));
					return response;
				}
				if (request.getBenfMobile().trim().length() != 12) {
					response.setErrorCode(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_BEN_MOB_NO);
					trLogger.info("IMPS P2P Response::>>" + new Gson().toJson(response));
					return response;
				}
				if (request.getBenfMMID().trim().length() != 7) {
					response.setErrorCode(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_BEN_MMID);
					trLogger.info("IMPS P2P Response::>>" + new Gson().toJson(response));
					return response;
				}
				if (Double.parseDouble("" + request.getTransAmt()) == 0) {
					response.setErrorCode(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_AMOUNT);
					trLogger.info("IMPS P2P Response::>>" + new Gson().toJson(response));
					return response;
				}
				D009022 sourceAccount = DataUtils.getAccount(request.getRemitterAccNo());
				if (sourceAccount == null) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
					trLogger.info("IMPS P2P Response::>>" + new Gson().toJson(response));
					return response;
				}
				if ((request.getRemitterAccNo() != null && !request.getRemitterAccNo().trim().isEmpty())
						&& (request.getBenfAccNo() != null && !request.getBenfAccNo().trim().isEmpty())) {
					if (request.getRemitterAccNo().equalsIgnoreCase(request.getBenfAccNo().trim())) {
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage(MSGConstants.SAME_ACC_TRANSFER_NOT_ALLOWED);
						response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
						trLogger.info("IMPS P2P Response::>>" + new Gson().toJson(response));
						return response;
					}
				}
				TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount,
						String.valueOf(request.getTransAmt()), "D");
				if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
					P2PTransactionEntry.p2pTransactionEntry(request.getRemitterMMID(),
							request.getRemitterMobile().trim(), request.getBenfMobile(), request.getBenfMMID().trim(),
							Double.valueOf(request.getTransAmt()), 0, 0, "MBTR", 0, "99", res.getErrorMsg(), " ");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(res.getErrorMsg());
					trLogger.info("IMPS P2P Response::>>" + new Gson().toJson(response));
					return response;
				}
				response = TransactionServiceImpl.initiateIMPSTransaction(request.getRemitterAccNo().trim(),
						request.getBenfAccNo().trim(), String.valueOf(request.getTransAmt()), request.getNarration(),
						request.getRemitterMobile().trim(), request.getRemitterMMID().trim(),
						request.getBenfMobile().trim(), request.getBenfMMID().trim(), request.getTransType());
				logger.error("IMPS P2P Response::>>" + new Gson().toJson(response));
				trLogger.info("IMPS P2P Response::>>" + new Gson().toJson(response));
				return response;
			}
			if (request.getTransType().equalsIgnoreCase(MSGConstants.P2A_TRANSACTION)) {
				System.out.println("P2A Transaction");
				if (request.getRemitterMobile().trim().length() != 12) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_REMITTER_MOB_NO);
					response.setErrorCode(MSGConstants.INVALID_REMITTER_MOB_NO);
					trLogger.info("IMPS P2A Response::>>" + new Gson().toJson(response));
					return response;
				}
				if (request.getRemitterMMID().trim().length() != 7) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_REMITTER_MMID);
					response.setErrorCode(MSGConstants.INVALID_REMITTER_MMID);
					trLogger.info("IMPS P2A Response::>>" + new Gson().toJson(response));
					return response;
				}
				if (!request.getRemitterMMID().trim().substring(0, 4).equalsIgnoreCase(MSGConstants.NBIN)) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.NBIN_NOT_BELONGS_BANK);
					response.setErrorCode(MSGConstants.NBIN_NOT_BELONGS_BANK);
					trLogger.info("IMPS P2A Response::>>" + new Gson().toJson(response));
					return response;
				}

				if (null == request.getRemitterAccNo() || request.getRemitterAccNo().trim().length() < 10
						|| request.getRemitterAccNo().trim().length() > 20) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_REMITTER_ACC_NO);
					response.setErrorCode(MSGConstants.INVALID_REMITTER_ACC_NO);
					trLogger.info("IMPS P2A Response::>>" + new Gson().toJson(response));
					return response;
				}
				if (null == request.getBenfAccNo() || request.getBenfAccNo().trim().length() > 20) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage("Beneficiary Account Number Length Greater Than 20 digit Not Allowed");
					response.setErrorCode("Beneficiary Account Number Length Greater Than 20 digit Not Allowed");
					trLogger.info("IMPS P2A Response::>>" + new Gson().toJson(response));
					return response;
				}
				if (request.getBenfIFSC().trim().length() != 11) {
					response.setErrorCode(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_BEN_IFSC);
					trLogger.info("IMPS P2A Response::>>" + new Gson().toJson(response));
					return response;
				}
				if (Double.parseDouble("" + request.getTransAmt()) == 0) {
					response.setErrorCode(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_AMOUNT);
					trLogger.info("IMPS P2A Response::>>" + new Gson().toJson(response));
					return response;
				}
				D009022 sourceAccount = DataUtils.getAccount(request.getRemitterAccNo());
				if (RtgsNeftHostToHostConstants.LOAN.getMessage()
						.contains(DataUtils
								.getProductMaster(sourceAccount.getId().getLbrCode() + "",
										sourceAccount.getId().getPrdAcctId().substring(0, 8).trim())
								.getModuleType() + "")) {
					response.setErrorCode(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.LOAN_TRN_NOT_ALLOWED);
					trLogger.info("IMPS P2A Response::>>" + new Gson().toJson(response));
					return response;
				}
				TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount,
						String.valueOf(request.getTransAmt()), "D");
				if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
					P2ATransaction.p2aTransactionEntry(request.getRemitterMMID().trim(),
							request.getRemitterMobile().trim(), request.getBenfAccNo().trim(),
							request.getBenfIFSC().trim(), Double.valueOf(request.getTransAmt()), 0, 0, "MBTR",
							sourceAccount.getId().getLbrCode(), "99", res.getErrorMsg(), " ");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(res.getErrorMsg());
					trLogger.info("IMPS P2A Response::>>" + new Gson().toJson(response));
					return response;
				}
				try {
					// System.out.println("hello");
					response = TransactionServiceImpl.initiateIMPSTransaction(request.getRemitterAccNo().trim(),
							request.getBenfAccNo().trim(), String.valueOf(request.getTransAmt()),
							request.getNarration().trim(), request.getRemitterMobile().trim(),
							request.getRemitterMMID().trim(), request.getBenfAccNo().trim(),
							request.getBenfIFSC().trim(), request.getTransType());
					logger.error("IMPS P2A Response::>>" + new Gson().toJson(response));
					trLogger.info("IMPS P2A Response::>>" + new Gson().toJson(response));
					return response;
				} catch (Exception e) {
					// TODO: handle exception
					logger.error("ERROR:", e);
					e.printStackTrace();
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
					return response;
				}
			}
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_REQUEST);
			trLogger.info("IMPS P2A Response::>>" + new Gson().toJson(response));
			return response;

		} catch (Exception e) {
			logger.error("ERROR:", e);
			// TODO: handle exception
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/initiateBillPayment")
	public IMPSTransactionResponse initiateBillPayment(IMPSTransactionRequest request) {
		logger.error("<<<<<<<---------- impsTransactionOtherBank ------------->>>>>>>");
		logger.error("Request Received::>>" + request);
		IMPSTransactionResponse response = new IMPSTransactionResponse();
		if (request == null) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_REQUEST);
			return response;
		}
		if (request.getTransType().equalsIgnoreCase(MSGConstants.BILLPAY)) {
			if (request.getRemitterMobile().trim().length() != 12) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_REMITTER_MOB_NO);
				response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				return response;
			}
			if (null == request.getRemitterAccNo() || request.getRemitterAccNo().trim().length() != 15) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_REMITTER_ACC_NO);
				response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				return response;
			}
			if (Double.parseDouble("" + request.getTransAmt()) == 0) {
				response.setErrorCode(ResponseCodes.INSUFFICIENT_FUNDS);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_AMOUNT);
				return response;
			}
			if (request.getOperator() == null || request.getOperator().trim().isEmpty()) {
				response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_OPERATOR);
				return response;
			}
			D009022 sourceAccount = DataUtils.getAccount(request.getRemitterAccNo());
			if (sourceAccount == null) {
				response.setErrorCode(ResponseCodes.ACCOUNT_NOT_NORMAL);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
				return response;
			}
			TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount,
					String.valueOf(request.getTransAmt()), "D");
			if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
				BillPaymentTransactionEntry.transactionEntryBillPay(sourceAccount.getId().getLbrCode(),
						sourceAccount.getId().getPrdAcctId(), "MBTR", 0, 0, request.getConsumerNo(),
						request.getOperator(), request.getTransAmt(), ResponseCodes.INVALID_NBIN, res.getErrorMsg(),
						request.getRRNNo(), "D");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(res.getErrorMsg());
				response.setErrorCode(res.getRespCode());
				return response;
			}
			try {
				response = TransactionServiceImpl.initiateBillPayement(request.getRemitterAccNo(),
						request.getRemitterAccNo(), String.valueOf(request.getTransAmt()), request.getNarration(),
						request.getRemitterMobile(), request.getRemitterMMID(), request.getBenfMobile(),
						request.getBenfMMID(), request.getTransType(), request);
				logger.error("Response Received::>>" + response);
				return response;
			} catch (Exception e) {
				logger.error("ERROR:", e);
				e.printStackTrace();
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
				return response;
			}
		}
		response.setResponse(MSGConstants.ERROR);
		response.setErrorMessage(MSGConstants.INVALID_REQUEST);
		return response;
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/impsCreditTransaction")
	public IMPSTransactionResponse impsCreditTransaction(IMPSTransactionRequest request) {

		logger.error("<<<<<<<<<::: IMPS Credit Transaction ::::>>>>>>>>>>");
		// logger.error("Request Received::>>" + request);
		IMPSTransactionResponse response = new IMPSTransactionResponse();
		if (request == null) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorCode(MSGConstants.INVALID_REQUEST);
			return response;
		}
		try (org.util.nanolog.Logger trLogger = org.util.nanolog.Logger.getLogger(LoggerType.BUFFERED, logWriter)) {
			if (request.getTransType().equalsIgnoreCase(MSGConstants.P2P_TRANSACTION)) {
				logger.error("<<<<<<<----------P2P Transaction-------------->>>>>>");
				logger.error("IMPS P2P Credit Request Received ::>>" + new Gson().toJson(request));
				trLogger.info("IMPS P2P Credit Request Received ::>>" + new Gson().toJson(request));
				if (request.getRemitterMobile().trim().length() != 12) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
					response.setErrorMessage(MSGConstants.INVALID_REMITTER_MOB_NO);
					trLogger.info("IMPS P2P Credit Response::>>" + new Gson().toJson(response));
					return response;
				}
				D946022 ifscMaster = DataUtils.getIFSCCodeDetail(request.getBenfIFSC().trim());
				if (ifscMaster == null) {
					response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_BEN_IFSC);
					logger.error(MSGConstants.INVALID_BEN_IFSC);
					trLogger.info("IMPS P2P Credit Response::>>" + new Gson().toJson(response));
					return response;
				}
				if (request.getRemitterMMID().trim().length() != 7) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_REMITTER_MMID);
					response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
					trLogger.info("IMPS P2P Credit Response::>>" + new Gson().toJson(response));
					return response;
				}
				if (request.getRemitterAccNo().trim().length() < 1) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_REMITTER_ACC_NO);
					response.setErrorCode(MSGConstants.INVALID_REMITTER_ACC_NO);
					trLogger.info("IMPS P2P Credit Response::>>" + new Gson().toJson(response));
					return response;
				}
				if (request.getBenfMobile().trim().length() != 12) {
					System.out.println(MSGConstants.INVALID_BEN_MOB_NO);
					logger.error(MSGConstants.INVALID_BEN_MOB_NO);
					response.setResponse(MSGConstants.ERROR);
					response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
					response.setErrorMessage(MSGConstants.INVALID_BEN_MOB_NO);
					response.setRrnNo(request.getRRNNo() + "");
					trLogger.info("IMPS P2P Credit Response::>>" + new Gson().toJson(response));
					return response;
				}
				if (request.getBenfMMID().trim().length() != 7) {
					response.setErrorCode(MSGConstants.ERROR);
					response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
					response.setErrorMessage(MSGConstants.INVALID_BEN_MMID);
					trLogger.info("IMPS P2P Credit Response::>>" + new Gson().toJson(response));
					return response;
				}
				if (Double.parseDouble("" + request.getTransAmt()) == 0) {
					response.setErrorCode(ResponseCodes.INSUFFICIENT_FUNDS);
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_AMOUNT);
					trLogger.info("IMPS P2P Credit Response::>>" + new Gson().toJson(response));
					return response;
				}
				if (request.getTransAmt() > Double
						.valueOf(ConfigurationLoader.getParameters(false).getProperty("MAX_AMOUNT"))) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.EXCEED_DAILY_LIMIT);
					response.setRrnNo(request.getRRNNo());
					response.setErrorCode("M2");
					trLogger.info("IMPS P2P Credit Response::>>" + new Gson().toJson(response));
					return response;
				}
				D009022 sourceAccount = DataUtils.getAccount(request.getBenfAccNo());
				TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount,
						String.valueOf(request.getTransAmt()), "C");
				if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(res.getErrorMsg());
					response.setErrorCode(res.getRespCode());
					trLogger.info("IMPS P2P Credit Response::>>" + new Gson().toJson(response));
					return response;
				}
				Session session = HBUtil.getSessionFactory().openSession();
				Criteria criteria2 = session.createCriteria(D350044.class);
				criteria2.add(Restrictions.eq("id.rrnNo", request.getRRNNo().trim()));
				// criteria2.add(Restrictions.eq("id.mobNo1",
				// request.getRemitterMobile().trim()));
				List<D350044> list2 = criteria2.list();
				session.close();
				session = null;
				criteria2 = null;
				if (!list2.isEmpty()) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.DUPLICATE_TRN);
					response.setRrnNo(request.getRRNNo());
					response.setErrorCode(ResponseCodes.ALREADY_DONE_THIS_TRANSACTION);
					trLogger.info("IMPS P2P Credit Response::>>" + new Gson().toJson(response));
					return response;
				}
				response = TransactionServiceImpl.creditIMPSTransactionNew(request.getBenfAccNo().trim(),
						request.getRemitterAccNo().trim(), String.valueOf(request.getTransAmt()), request.getNarration(),
						request.getRemitterMobile().trim(), request.getRemitterMMID().trim(),
						request.getBenfMobile().trim(), request.getBenfMMID().trim(), request.getTransType(),
						request.getRRNNo());
				response.setNickNameCredit(
						sourceAccount != null ? sourceAccount.getLongName().substring(0, 10) : "ARTHUR FUR");
				response.setNickNameDebit(
						sourceAccount != null ? sourceAccount.getLongName().substring(0, 10) : "ARTHUR FUR");
				logger.error("IMPS P2P Credit Response Received::>>" + response);
				trLogger.info("IMPS P2P Credit Response::>>" + new Gson().toJson(response));
				return response;
			}
			if (request.getTransType().equalsIgnoreCase(MSGConstants.P2A_TRANSACTION)) {
				logger.error("<<<<<<<----------P2A Transaction-------------->>>>>>");
				trLogger.info("IMPS P2A Credit Request Received ::>>" + new Gson().toJson(request));
				logger.error("IMPS P2A Credit Request Received ::>>" + new Gson().toJson(request));
				if (request.getRemitterMobile().trim().length() != 12) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
					response.setErrorMessage(MSGConstants.INVALID_BEN_MOB_NO);
					logger.error(MSGConstants.INVALID_BEN_MOB_NO);
					trLogger.info("IMPS P2A Credit Response::>>" + new Gson().toJson(response));
					return response;
				}

				if (request.getRemitterMMID().trim().length() != 7) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_REMITTER_MMID);
					response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
					logger.error(MSGConstants.INVALID_REMITTER_MMID);
					trLogger.info("IMPS P2A Credit Response::>>" + new Gson().toJson(response));
					return response;
				}
				if (null == request.getRemitterAccNo() || request.getRemitterAccNo().trim().length() < 10) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_REMITTER_ACC_NO);
					response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
					logger.error(MSGConstants.INVALID_REMITTER_ACC_NO);
					trLogger.info("IMPS P2A Credit Response::>>" + new Gson().toJson(response));
					return response;
				}
				if (request.getBenfIFSC().trim().length() != 11) {
					response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_BEN_IFSC);
					logger.error(MSGConstants.INVALID_BEN_IFSC);
					trLogger.info("IMPS P2A Credit Response::>>" + new Gson().toJson(response));
					return response;
				}
				// Add by Aniket Desai on 10th Dec, 2019 -Ben Tran declined with M1 due to IFSC
				if (!request.getBenfIFSC().trim().contains(ConfigurationLoader.getParameters(false).getProperty("IFSC"))
						&& !request.getBenfIFSC().trim()
								.contains(ConfigurationLoader.getParameters(false).getProperty("IFSC1"))) {
					response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_BEN_IFSC);
					logger.error(MSGConstants.INVALID_BEN_IFSC);
					trLogger.info("IMPS P2A Credit Response::>>" + new Gson().toJson(response));
					return response;
				}

				if (Double.parseDouble("" + request.getTransAmt()) == 0) {
					response.setErrorCode(ResponseCodes.INSUFFICIENT_FUNDS);
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_AMOUNT);
					logger.error(MSGConstants.INVALID_AMOUNT);
					return response;
				}
				if (request.getTransAmt() > Double
						.valueOf(ConfigurationLoader.getParameters(false).getProperty("MAX_AMOUNT"))) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.EXCEED_DAILY_LIMIT);
					response.setRrnNo(request.getRRNNo());
					response.setErrorCode("M2");
					trLogger.info("IMPS P2A Credit Response::>>" + new Gson().toJson(response));
					return response;
				}

				D009022 sourceAccount = DataUtils.getAccount(request.getRemitterAccNo());
				if (sourceAccount == null) {
					response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
					logger.error(MSGConstants.ACCOUNT_NOT_FOUND);
					trLogger.info("IMPS P2A Credit Response::>>" + new Gson().toJson(response));
					return response;
				}
				TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount,
						String.valueOf(request.getTransAmt()), "C");

				if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {

					P2AReversal.transactionEntry(String.valueOf(sourceAccount.getId().getLbrCode()),
							request.getBenfMobile(), request.getBenfMMID(), request.getRemitterMobile(),
							request.getRemitterMMID(), String.valueOf(request.getTransAmt()), request.getRRNNo().trim(),
							"C", 0, 0, "MBTR", ResponseCodes.EXCEPTION_OCCURED, MSGConstants.TRANSACTION_FAILS,
							request.getOperator());

					/*
					 * P2ATransaction.p2aTransactionEntry(request.getRemitterMMID().trim(),
					 * request.getRemitterMobile().trim(), request.getBenfAccNo().trim(),
					 * request.getBenfIFSC().trim(), Double.valueOf(request.getTransAmt()), 0, 0,
					 * "MBTR", 0, "99", res.getErrorMsg(), " ");
					 */
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(res.getErrorMsg());

					response.setErrorCode(res.getRespCode());
					trLogger.info("IMPS P2A Credit Response::>>" + new Gson().toJson(response));
					return response;
				}

				Session session = HBUtil.getSessionFactory().openSession();
				Criteria criteria2 = session.createCriteria(D350059.class);
				criteria2.add(Restrictions.eq("id.rrnNo", String.format("%1$-20s", request.getRRNNo().trim())));
				criteria2.add(Restrictions.eq("id.accNo", String.format("%1$-20s", request.getRemitterAccNo())));
				criteria2.add(Restrictions.eq("id.stan", String.format("%1$-20s", request.getOperator())));
				//criteria2.add(Restrictions.eq("id.entryDate", DateUtil.getCurrentDateNew(DataUtils.getOpenDate(Integer.valueOf(sourceAccount.getId().getLbrCode())))));
//			criteria2.add(Restrictions.eq("id.mobNo1", request.getAccountNo().trim()));
//			criteria2.add(Restrictions.eq("id.mmid1", request.getRemitterMMID().trim()));
//			criteria2.add(Restrictions.eq("id.responseCd", "00"));
//			criteria2.add(Restrictions.eq("id.tranAmt", request.getTransAmt().intValue()));
				List<D350059> list2 = criteria2.list();
				session.close();
				session = null;
				criteria2 = null;
				if (!list2.isEmpty()) {
					if(list2.get(0).getId().getResponseCd().trim().equalsIgnoreCase("98") || list2.get(0).getId().getResponseCd().trim().equalsIgnoreCase("20")) {
						P2AReversal.transactionUpdateToPending(sourceAccount.getId().getLbrCode()+"",request.getBenfMobile(), request.getBenfMMID(), request.getRemitterMobile(),
							request.getRemitterMMID(), String.valueOf(request.getTransAmt()), request.getRRNNo().trim(),
							"C", 0, 0, "MBTR","91", "PENDING");
					}/*if(list2.get(0).getId().getResponseCd().trim().equalsIgnoreCase("91") ){
						P2AReversal.transactionUpdateToPending(sourceAccount.getId().getLbrCode()+"",request.getBenfMobile(), request.getBenfMMID(), request.getRemitterMobile(),
							request.getRemitterMMID(), String.valueOf(request.getTransAmt()), request.getRRNNo().trim(),
							"C", 0, 0, "MBTR","91", "PENDING");
						}*/
					else {
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage(MSGConstants.DUPLICATE_TRN);
						response.setRrnNo(request.getRRNNo());
						response.setErrorCode(ResponseCodes.ALREADY_DONE_THIS_TRANSACTION);
						trLogger.info("IMPS P2A Credit Response::>>" + new Gson().toJson(response));
						return response;
					}
				} else {
					P2AReversal.transactionEntry(String.valueOf(sourceAccount.getId().getLbrCode()),
							request.getRemitterMobile(), request.getRemitterMMID(), request.getBenfAccNo(),
							request.getBenfIFSC(), String.valueOf(request.getTransAmt()), request.getRRNNo().trim(),
							"C", 0, 0, "MBTR", "91", "PENDING", request.getOperator());

				} 
				response = TransactionServiceImpl.creditIMPSTransactionNew(request.getRemitterAccNo().trim(),
						request.getBenfAccNo().trim(), String.valueOf(request.getTransAmt()),
						request.getNarration().trim(), request.getRemitterMobile().trim(),
						request.getRemitterMMID().trim(), request.getRemitterAccNo().trim(),
						request.getBenfIFSC().trim(), request.getTransType(), request.getRRNNo());
				response.setNickNameCredit(
						sourceAccount != null ? sourceAccount.getLongName().substring(0, 10) : "ARTHUR FUR");
				response.setNickNameDebit(
						sourceAccount != null ? sourceAccount.getLongName().substring(0, 10) : "ARTHUR FUR");
				logger.error("IMPS P2A Credit Response Received ::>>" + new Gson().toJson(response));
				trLogger.info("IMPS P2A Credit Response::>>" + new Gson().toJson(response));

				return response;
			}
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_REQUEST);
			trLogger.info("IMPS P2A Credit Response::>>" + new Gson().toJson(response));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return response;
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/reverseImpsTransaction")
	public IMPSTransactionResponse reverseImpsTransaction(IMPSTransactionRequest request) {
		logger.error("<<:: IMPS REVERSAL SERVICE ::>>");
		logger.error("IMPS Reversal Request Received::>>>" + new Gson().toJson(request));
		IMPSTransactionResponse response = new IMPSTransactionResponse();
		if (request == null) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_REQUEST);
			return response;
		}
		if (request.getTransType().equalsIgnoreCase(MSGConstants.P2P_TRANSACTION)) {
			if (request.getRemitterMobile().trim().length() != 12) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_REMITTER_MOB_NO);
				response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				return response;
			}
			if (null == request.getRRNNo() || request.getRRNNo().trim().length() != 12) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorCode(ResponseCodes.VERIFICATION_RESP_M0);
				response.setErrorMessage(MSGConstants.INVALID_REMITTER_MOB_NO);
				return response;
			}
			if (request.getRemitterMMID().trim().length() != 7) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_REMITTER_MMID);
				response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				return response;
			}
			if (null == request.getRemitterAccNo() || request.getRemitterAccNo().trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_REMITTER_ACC_NO);
				response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				return response;
			}
			if (request.getBenfMobile().trim().length() != 12) {
				System.out.println(MSGConstants.INVALID_BEN_MOB_NO);
				logger.error(MSGConstants.INVALID_BEN_MOB_NO);
				response.setErrorCode(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_BEN_MOB_NO);
				return response;
			}
			if (request.getBenfMMID().trim().length() != 7) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				response.setErrorMessage(MSGConstants.INVALID_BEN_MMID);
				return response;
			}
			if (Double.parseDouble("" + request.getTransAmt()) == 0) {
				response.setErrorCode(ResponseCodes.INSUFFICIENT_FUNDS);
				response.setErrorMessage(MSGConstants.INVALID_AMOUNT);
				response.setResponse(MSGConstants.ERROR);
				return response;
			}
			D009022 sourceAccount = DataUtils.getAccount(request.getRemitterAccNo());
			if (sourceAccount == null) {
				response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				response.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
				response.setResponse(MSGConstants.ERROR);
				return response;
			}
			TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount,
					String.valueOf(request.getTransAmt()), "C");
			System.out.println("res.getErrorMsg()::>>" + res.getErrorMsg());
			if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(res.getErrorMsg());
				response.setErrorCode(res.getRespCode());
				return response;
			}
			Session session = HBUtil.getSessionFactory().openSession();
			Criteria criteria1 = session.createCriteria(D350044.class);
			criteria1.add(Restrictions.eq("id.lbrCode", Integer.valueOf(request.getRemitterAccNo().substring(0, 3))));
			// criteria1.add(Restrictions.eq("id.batchCd", String.format("%1$-8s","MBTR")));
			criteria1.add(Restrictions.eq("id.mobNo1", String.format("%1$-12s", request.getRemitterMobile().trim())));
			criteria1.add(Restrictions.eq("id.mmid1", String.format("%1$-8s", request.getRemitterMMID())));
			criteria1.add(Restrictions.eq("id.mobNo2", String.format("%1$-12s", request.getBenfMobile().trim())));
			criteria1.add(Restrictions.eq("id.mmid2", String.format("%1$-8s", request.getBenfMMID())));
			criteria1.add(Restrictions.eq("id.tranAmt", Double.valueOf(request.getTransAmt()).intValue()));
			criteria1.add(Restrictions.eq("id.rrnNo", String.format("%1$-20s", request.getRRNNo())));
			criteria1.add(Restrictions.in("id.drcr", String.format("%1$-2s", "C"), "CR"));
			List<D350044> list1 = criteria1.list();
			session.close();
			session = null;
			if (list1 != null && list1.size() > 0) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorCode(ResponseCodes.ALREADY_DONE_THIS_TRANSACTION);
				response.setErrorMessage(MSGConstants.ALREADY_REVERSED);
				return response;
			}
			response = TransactionServiceImpl.reverseIMPSTransaction(request.getRemitterAccNo().trim(),
					request.getBenfAccNo().trim(), String.valueOf(request.getTransAmt()), request.getNarration(),
					request.getRemitterMobile().trim(), request.getRemitterMMID().trim(),
					request.getBenfMobile().trim(), request.getBenfMMID().trim(), request.getTransType(),
					request.getRRNNo(), request);
			logger.error("IMPS P2P Reversal Response received::>>" + new Gson().toJson(response));
			return response;
		}
		if (request.getTransType().equalsIgnoreCase(MSGConstants.P2A_TRANSACTION)) {
			if (request.getRemitterMobile().trim().length() < 10) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_REMITTER_MOB_NO);
				response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				return response;
			}
			if (null == request.getRemitterAccNo() || request.getRemitterAccNo().trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				response.setErrorMessage(MSGConstants.INVALID_ACCOUNT_NO);
				return response;
			}
			if (null == request.getRRNNo() || request.getRRNNo().trim().length() != 12) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_RRN_NO);
				return response;
			}
			if (request.getRemitterMMID().trim().length() != 7) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_REMITTER_MMID);
				return response;
			}
			if (!request.getRemitterMMID().trim().substring(0, 4).equalsIgnoreCase(MSGConstants.NBIN)) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.NBIN_NOT_BELONGS_BANK);
				return response;
			}
			if (request.getBenfAccNo().trim().length() < 10) {
				response.setErrorCode(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_BEN_ACC_NO);
				return response;
			}
			if (request.getBenfIFSC().trim().length() != 11) {
				response.setErrorCode(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_BEN_IFSC);
				return response;
			}
			if (Double.parseDouble("" + request.getTransAmt()) == 0) {
				response.setErrorCode(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_AMOUNT);
				return response;
			}
			D009022 sourceAccount = DataUtils.getAccount(request.getRemitterAccNo());
			TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount,
					String.valueOf(request.getTransAmt()), "C");

			if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(res.getErrorMsg());
				return response;
			}
			try {
				response = TransactionServiceImpl.reverseIMPSTransaction(request.getRemitterAccNo().trim(),
						request.getBenfAccNo().trim(), String.valueOf(request.getTransAmt()), request.getNarration(),
						request.getRemitterMobile().trim(), request.getRemitterMMID().trim(),
						request.getBenfAccNo().trim(), request.getBenfIFSC().trim(), request.getTransType(),
						request.getRRNNo(), request);
				logger.error("P2A Reversal Response received::>>" + new Gson().toJson(response));
				return response;
			} catch (Exception e) {
				// TODO: handle exception
				logger.error("ERROR:", e);
				e.printStackTrace();
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
				return response;
			}
		}
		if (request.getTransType().equalsIgnoreCase(MSGConstants.BILLPAY)) {
			if (null == request.getRemitterAccNo() || request.getRemitterAccNo().trim().length() != 15) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				response.setErrorMessage(MSGConstants.INVALID_ACCOUNT_NO);
				return response;
			}
			if (null == request.getRRNNo() || request.getRRNNo().trim().length() != 12) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_RRN_NO);
				return response;
			}
			if (null == request.getConsumerNo() || request.getConsumerNo().trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_CONSUMER_NO);
				return response;
			}
			if (null == request.getOperator() || request.getOperator().trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_OPERATOR);
				return response;
			}
			if (Double.parseDouble("" + request.getTransAmt()) == 0) {
				response.setErrorCode(MSGConstants.ERROR);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_AMOUNT);
				return response;
			}
			D009022 sourceAccount = DataUtils.getAccount(request.getRemitterAccNo());
			if (sourceAccount == null) {
				response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_AMOUNT);
				return response;
			}
			Session session = HBUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(Billpayment.class);
			criteria.add(Restrictions.eq("consumerno", request.getConsumerNo().trim()));
			criteria.add(Restrictions.eq("operator", request.getOperator().trim()));
			criteria.add(Restrictions.eq("amount", request.getTransAmt()));
			criteria.add(Restrictions.eq("responsecode", "00"));
			criteria.add(Restrictions.eq("id.rrrnno", request.getRRNNo()));
			criteria.add(Restrictions.eq("drcr", "C"));
			List<Billpayment> list = criteria.list();
			session.close();
			session = null;
			if (list != null && list.size() > 0) {
				response.setErrorCode(ResponseCodes.ALREADY_DONE_THIS_TRANSACTION);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.ALREADY_REVERSED);
				return response;
			}
			TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount,
					String.valueOf(request.getTransAmt()), "C");
			if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
				response.setResponse(MSGConstants.ERROR);
				BillPaymentTransactionEntry.transactionEntryBillPay(sourceAccount.getId().getLbrCode(),
						sourceAccount.getId().getPrdAcctId(), "MBTR", 0, 0, request.getConsumerNo(),
						request.getOperator(), request.getTransAmt(), ResponseCodes.NO_RECORD_FOUND, res.getErrorMsg(),
						request.getRRNNo(), "C");
				response.setErrorMessage(res.getErrorMsg());
				return response;
			}
			try {
				response = TransactionServiceImpl.reverseIMPSTransaction(request.getRemitterAccNo().trim(),
						request.getBenfAccNo().trim(), String.valueOf(request.getTransAmt()), request.getNarration(),
						request.getRemitterMobile().trim(), request.getRemitterMMID().trim(),
						request.getBenfAccNo().trim(), request.getBenfIFSC().trim(), request.getTransType(),
						request.getRRNNo(), request);
				logger.error("BILL PAYMENT REVERSAL Response Received::>>" + new Gson().toJson(response));
				return response;
			} catch (Exception e) {
				// TODO: handle exception
				logger.error("ERROR:", e);
				e.printStackTrace();
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
				return response;
			}
		}
		response.setResponse(MSGConstants.ERROR);
		response.setErrorMessage(MSGConstants.INVALID_REQUEST);
		return response;
	}

	@SuppressWarnings("unused")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/searchCustomer")
	public CustDetailsResponse searchCustomer(@QueryParam(value = "param1") String custNo,
			@QueryParam(value = "param2") String mobNo, @QueryParam(value = "param3") String panNo,
			@QueryParam(value = "param4") String name, @QueryParam(value = "param5") String aadhar) {
		logger.error("<<<<<:: searchCustomer().service ::>>>>>");
		logger.error("Parameters Received are::>> custNo=" + custNo + " mobNo::>>" + mobNo + " panNo::>>" + panNo
				+ " name::>>" + name);
		CustDetailsResponse customerDetails = null;
		if (null != mobNo && mobNo.length() == 10)
			mobNo = "91" + mobNo;

		customerDetails = CustomerMasterServiceImpl.getCustomerDetails(custNo, mobNo, panNo, name, aadhar);
		System.out.println("customerDetails::>>" + customerDetails.getResponse());
		if (null != customerDetails)
			return customerDetails;
		else {
			CustDetailsResponse details = new CustDetailsResponse();
			details.setResponse("ERROR");
			details.setErrorMsg("Customer not Found.");
			return details;
		}
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/openAccount")
	public static AccountOpenResponse openAccount(AccountOpenRequest request) {
		AccountOpenResponse response = new AccountOpenResponse();
		logger.error("<<<:: OPEN ACCOUNT REQUEST ::>>>");
		logger.error("openAccount() Request Received :>>" + new Gson().toJson(request));
		try {
			if (null == request) {
				response.setResponse("ERROR");
				response.setErrorMessage("Invalid request");
				return response;
			}
			if (null == request.getProdCode() || request.getProdCode().trim().equalsIgnoreCase("")) {
				response.setResponse("ERROR");
				response.setErrorMessage("Invalid request");
				return response;
			}
			if (request.getIsNew().trim().equalsIgnoreCase("N")) {
				if (null == request.getCustNo() || request.getCustNo().trim().equalsIgnoreCase("")) {
					response.setResponse("ERROR");
					response.setErrorMessage("Invalid request");
					return response;
				}
			}
			if (null == request.getBrcode() || request.getBrcode().trim().equalsIgnoreCase("")) {
				response.setResponse("ERROR");
				response.setErrorMessage("Invalid request");
				return response;
			}
			if (null == request.getWelcomeKitFlag() || request.getWelcomeKitFlag().equalsIgnoreCase("Y")) {
				if (null == request.getAccNo() || request.getAccNo().trim().equalsIgnoreCase("")) {
					response.setResponse("ERROR");
					response.setErrorMessage("Invalid request");
					return response;
				}
			}

			response = AccountMasterServiceImpl.insertValues(request.getBrcode().trim(), request.getProdCode(),
					request.getAccHolderType().trim(), request.getModeOfOperation().trim(),
					request.getNameTitle().trim(), request.getName().trim(), request.getAdd1().trim(),
					request.getAdd2(), request.getAdd3(), request.getPanCode().trim(), request.getPanCardNo().trim(),
					request.getAreaCode().trim(), request.getCityCode().trim(), request.getPinCode().trim(),
					request.getMobNo().trim(), request.getEmail(), request.getIsNew().trim(), request.getCustNo(),
					request.getWelcomeKitFlag().trim(), request.getAccNo().trim(), request.getAccType().trim(),
					request);
			logger.error("Account Open Response::>>" + response);
			return response;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("ERROR:", e);
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}

	}

	@POST
	@Path("/fetchAllBalance")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static CustomerDetails fetchAllBalance(@QueryParam(value = "param1") String mobNo) {
		return RequestServiceImpl.getCustomerDetails(mobNo.trim());
	}

	@POST
	@Path("/neftFundTransfer")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public RtgsNeftTransactionResponse rtgsNeftFundTransfer(@QueryParam(value = "param1") String remAccount,
			@QueryParam(value = "param2") String remBranchCode, @QueryParam(value = "param3") String benAccount,
			@QueryParam(value = "param4") String benIfsc, @QueryParam(value = "param5") String transAmt,
			@QueryParam(value = "param6") String remark, @QueryParam(value = "param7") String trnType,
			@QueryParam(value = "param8") String benName) throws ParseException {
		IBFundTransactionRequest request = new IBFundTransactionRequest();
		RtgsNeftTransactionResponse response = new RtgsNeftTransactionResponse();
		logger.error("<<<<::: rtgsNeftFundTransfer.service()::>>>>");

		if (!new ArrayList<String>(Arrays.asList(MSGConstants.RTGS, MSGConstants.NEFT)).contains(trnType)) {
			response.setResponseMessage(MSGConstants.ERROR);
			response.setInnerErrorMessage(MSGConstants.INVALID_TRANSACTION_TYPE);
			response.setSuccess(false);
			response.setResponseCode("01");
			response.setMessage("Transaction Failed");
			return response;
		} else
			request.setRtgsNEFT(trnType);
		if (benAccount == null || benAccount.trim().equalsIgnoreCase("")) {
			response.setResponseMessage(MSGConstants.ERROR);
			response.setInnerErrorMessage(MSGConstants.INVALID_ACCOUNT_NO);
			response.setSuccess(false);
			response.setResponseCode("01");
			return response;
		} else {
			request.setToAccNo(benAccount);
		}
		request.setTrType("PAYNOW");

		if (benIfsc == null || benIfsc.trim().length() < 1) {
			response.setResponseMessage(MSGConstants.ERROR);
			response.setInnerErrorMessage(MSGConstants.INVALID_BEN_IFSC);
			response.setSuccess(false);
			response.setResponseCode("01");
			return response;
		} else {
			request.setToIfscCode(benIfsc);
		}
		if (benName == null || benName.trim().length() < 1) {
			response.setResponseMessage(MSGConstants.ERROR);
			response.setInnerErrorMessage(MSGConstants.INAVLID_BEN_NICKNAME);
			response.setSuccess(false);
			response.setResponseCode("01");
			return response;
		} else {
			request.setBenNickName(benName);
		}

		if (remAccount == null || remAccount.trim().length() != 15) {
			response.setResponseMessage(MSGConstants.ERROR);
			response.setInnerErrorMessage(MSGConstants.INVALID_FROM_ACC_BR_CODE);
			response.setSuccess(false);
			response.setResponseCode("01");
			return response;
		} else {
			request.setFromAccNo(remAccount);
			D009022 fromAcct = DataUtils.getAccount(remAccount);
			if (fromAcct != null)
				request.setCustNo(fromAcct.getCustNo() + "");
		}
		request.setNextTrnxDate(DateUtility.getDateFromDateAsString(new Date(), "dd/MM/yyyy"));

		if (transAmt == null || Double.parseDouble(transAmt) == 0d) {
			response.setResponseMessage(MSGConstants.ERROR);
			response.setInnerErrorMessage(MSGConstants.INVALID_TRN_AMOUNT);
			response.setSuccess(false);
			response.setResponseCode("01");
			return response;
		} else {
			request.setTransAmnt(Double.parseDouble(transAmt));
		}
		if (remark != null && !"".equalsIgnoreCase(remark))
			request.setNarration(remark);

		try {
			response = RTGSNEFTServiceImpl.ibFundTransferToOthrBank(request);
			logger.error("RTGS/NEFT Response Received::>>" + new Gson().toJson(response));
			System.out.println(response);
			return response;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("ERROR:", e);
			response.setResponseMessage(MSGConstants.ERROR);
			response.setInnerErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			response.setSuccess(false);
			response.setResponseCode("01");
			return response;
		}
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/impsCreditVerification")
	public IMPSTransactionResponse impsCreditVerification(IMPSTransactionRequest request) {
		logger.error("<<<<<<<<<::: IMPS Credit Transaction Verification ::::>>>>>>>>>>");
		// logger.error("IMPS Verification Request::>>" + new Gson().toJson(request));
		IMPSTransactionResponse response = new IMPSTransactionResponse();
		try (org.util.nanolog.Logger trLogger = org.util.nanolog.Logger.getLogger(LoggerType.BUFFERED, logWriter)) {
			if (request.getTransType().equalsIgnoreCase(MSGConstants.P2P_TRANSACTION)) {
				System.out.println("P2P Transaction");
				logger.error("IMPS P2P Credit Verification Request::>>" + new Gson().toJson(request));
				trLogger.error("IMPS P2P Credit Verification Request::>>" + new Gson().toJson(request));
				response = CoreBankingOperationImpl.verificationReq(
						Integer.valueOf(request.getRemitterAccNo().trim().substring(0, 3)), request.getRemitterAccNo(),
						request.getTransType().trim(), request.getNarration(), request.getTransAmt().intValue(),
						request.getRRNNo(), request.getRemitterMobile(), request.getRemitterMMID(),
						request.getBenfMobile(), request.getBenfMMID());
				if (!response.getErrorCode().equalsIgnoreCase("00")) {
					trLogger.error("IMPS P2P Credit Verification Response Received::>>" + new Gson().toJson(response));
					return response;
				}
				if (request.getRRNNo().trim().length() != 12) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorCode(ResponseCodes.ALREADY_DONE_THIS_TRANSACTION);
					response.setErrorMessage(MSGConstants.INVALID_RRN_NO);
					trLogger.error("IMPS P2P Credit Verification Response Received::>>" + new Gson().toJson(response));
					return response;
				}
				if (request.getRemitterMobile().trim().length() != 12) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorCode(MSGConstants.INVALID_REMITTER_MOB_NO);
					response.setErrorMessage(MSGConstants.INVALID_REMITTER_MOB_NO);

					trLogger.error("IMPS P2P Credit Verification Response Received::>>" + new Gson().toJson(response));
					return response;
				}
				if (request.getRemitterMMID().trim().length() != 7) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorCode(MSGConstants.INVALID_REMITTER_MMID);
					trLogger.error("IMPS P2P Credit Verification Response Received::>>" + new Gson().toJson(response));
					return response;
				}
				if (request.getRemitterAccNo().trim().length() < 1) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorCode(MSGConstants.INVALID_REMITTER_ACC_NO);
					return response;
				}
				if (request.getBenfMobile().trim().length() != 12) {
					System.out.println(MSGConstants.INVALID_BEN_MOB_NO);
					logger.error(MSGConstants.INVALID_BEN_MOB_NO);
					response.setResponse(MSGConstants.ERROR);
					response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
					response.setErrorMessage(MSGConstants.INVALID_BEN_MOB_NO);
					response.setRrnNo(request.getRRNNo() + "");
					trLogger.error("IMPS P2P Credit Verification Response Received::>>" + new Gson().toJson(response));
					return response;
				}
				if (request.getBenfMMID().trim().length() != 7) {
					response.setErrorCode(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_BEN_MMID);
					trLogger.error("IMPS P2P Credit Verification Response Received::>>" + new Gson().toJson(response));
					return response;
				}
				if (Double.parseDouble("" + request.getTransAmt()) == 0) {
					response.setErrorCode(ResponseCodes.INSUFFICIENT_FUNDS);
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_AMOUNT);
					trLogger.error("IMPS P2P Credit Verification Response Received::>>" + new Gson().toJson(response));
					return response;
				}
				D009022 sourceAccount = DataUtils.getAccount(request.getRemitterAccNo());
				if (sourceAccount == null) {
					response.setErrorCode(ResponseCodes.VERIFICATION_RESP_M0);
					response.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
					response.setResponse(MSGConstants.ERROR);
					trLogger.error("IMPS P2P Credit Verification Response Received::>>" + new Gson().toJson(response));
					return response;
				}
				TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount,
						String.valueOf(request.getTransAmt()), "C");
				if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
					P2PTransactionEntry.p2pTransactionEntry(request.getRemitterMMID(),
							request.getRemitterMobile().trim(), request.getBenfMobile(), request.getBenfMMID().trim(),
							Double.valueOf(request.getTransAmt()), 0, 0, "MBTR", 0, "99", res.getErrorMsg(), " ");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(res.getErrorMsg());
					response.setErrorCode(res.getRespCode());
					logger.error("IMPS P2P Credit Verification Response Received::>>" + new Gson().toJson(response));
					trLogger.error("IMPS P2P Credit Verification Response Received::>>" + new Gson().toJson(response));
					return response;
				}
				/*
				 * response = CoreBankingOperationImpl.verificationReq(
				 * Integer.valueOf(request.getRemitterAccNo().trim().substring(0, 3)),
				 * request.getRemitterAccNo(), request.getTransType().trim(),
				 * request.getNarration(), request.getTransAmt().intValue(), request.getRRNNo(),
				 * request.getRemitterMobile(), request.getRemitterMMID(),
				 * request.getBenfMobile(), request.getBenfMMID());
				 */
				logger.error("IMPS P2P Credit Verification Response Received::>>" + new Gson().toJson(response));
				trLogger.error("IMPS P2P Credit Verification Response Received::>>" + new Gson().toJson(response));
				return response;
			}

			if (request.getTransType().equalsIgnoreCase(MSGConstants.P2A_TRANSACTION)) {
				System.out.println("P2A Transaction");
				logger.error("IMPS P2A Credit Verification Request::>>" + new Gson().toJson(request));
				trLogger.error("IMPS P2A Credit Verification Request::>>" + new Gson().toJson(request));
				response = CoreBankingOperationImpl.verificationReq(
						Integer.valueOf(request.getRemitterAccNo().trim().substring(0, 3)), request.getRemitterAccNo(),
						request.getTransType().trim(), request.getNarration(), request.getTransAmt().intValue(),
						request.getRRNNo(), request.getRemitterMobile(), request.getRemitterMMID(),
						request.getBenfAccNo(), request.getBenfIFSC());
				if (!response.getErrorCode().equalsIgnoreCase("00")) {
					if (ResponseCodes.VERIFICATION_RESP_M0.equalsIgnoreCase(response.getErrorCode())) {
						/*
						 * P2ATransaction.p2aTransactionEntryM0(request.getRemitterMMID().trim(),
						 * request.getRemitterMobile().trim(), request.getBenfAccNo().trim(),
						 * request.getBenfIFSC().trim(), Double.valueOf(request.getTransAmt()), 0, 0,
						 * "MBTR", 0, response.getErrorCode(), response.getErrorMessage(), " ");
						 */
						response.setErrorCode(ResponseCodes.TIME_OUT);
					}
					trLogger.error("IMPS P2A Credit Verification Response Received::>>" + new Gson().toJson(response));
					return response;
				}
				if (request.getRRNNo().trim().length() != 12) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorCode(ResponseCodes.ALREADY_DONE_THIS_TRANSACTION);
					response.setErrorMessage(MSGConstants.INVALID_RRN_NO);
					trLogger.error("IMPS P2A Credit Verification Response Received::>>" + new Gson().toJson(response));
					return response;
				}
				if (request.getRemitterMobile().trim().length() != 12) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_REMITTER_MOB_NO);
					response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
					trLogger.error("IMPS P2A Credit Verification Response Received::>>" + new Gson().toJson(response));
					return response;
				}
				if (request.getRemitterMMID().trim().length() != 7) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_REMITTER_MMID);
					response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
					trLogger.error("IMPS P2A Credit Verification Response Received::>>" + new Gson().toJson(response));
					return response;
				}
				if (null == request.getRemitterAccNo() || request.getRemitterAccNo().trim().length() < 10) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorCode(ResponseCodes.VERIFICATION_RESP_M0);
					response.setErrorMessage(MSGConstants.INVALID_REMITTER_ACC_NO);
					trLogger.error("IMPS P2A Credit Verification Response Received::>>" + new Gson().toJson(response));
					return response;
				}
				if (request.getBenfIFSC().trim().length() != 11) {
					response.setErrorCode(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_BEN_IFSC);
					response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
					trLogger.error("IMPS P2A Credit Verification Response Received::>>" + new Gson().toJson(response));
					return response;
				} /*
					 * else { D946022 ifscMaster =
					 * DataUtils.getIFSCCodeDetail(request.getBenfIFSC().trim()); if
					 * (ifscMaster==null) {
					 * response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
					 * response.setResponse(MSGConstants.ERROR);
					 * response.setErrorMessage(MSGConstants.INVALID_BEN_IFSC);
					 * logger.error(MSGConstants.INVALID_BEN_IFSC);
					 * trLogger.error("IMPS P2A Credit Verification Response Received::>>" + new
					 * Gson().toJson(response)); return response; } }
					 */
				// Add by Aniket Desai on 10th Dec, 2019 -Ben Tran declined with M1 due to IFSC
				if (!request.getBenfIFSC().trim().contains(ConfigurationLoader.getParameters(false).getProperty("IFSC"))
						&& !request.getBenfIFSC().trim()
								.contains(ConfigurationLoader.getParameters(false).getProperty("IFSC1"))) {
					response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_BEN_IFSC);
					logger.error(MSGConstants.INVALID_BEN_IFSC);
					trLogger.info("IMPS P2A Credit Verification Response::>>" + new Gson().toJson(response));
					return response;
				}
				if (Double.parseDouble("" + request.getTransAmt()) == 0) {
					response.setErrorCode(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_AMOUNT);
					trLogger.error("IMPS P2A Credit Verification Response Received::>>" + new Gson().toJson(response));
					return response;
				}
				D009022 sourceAccount = DataUtils.getAccount(request.getRemitterAccNo());
				if (sourceAccount == null) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
					response.setErrorCode(ResponseCodes.VERIFICATION_RESP_M0);
					trLogger.error("IMPS P2A Credit Verification Response Received::>>" + new Gson().toJson(response));
					return response;
				}
				TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount,
						String.valueOf(request.getTransAmt()), "D");

				if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
					P2ATransaction.p2aTransactionEntry(request.getRemitterMMID().trim(),
							request.getRemitterMobile().trim(), request.getBenfAccNo().trim(),
							request.getBenfIFSC().trim(), Double.valueOf(request.getTransAmt()), 0, 0, "MBTR", 0, "99",
							res.getErrorMsg(), " ");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(res.getErrorMsg());
					response.setErrorCode(res.getRespCode());
					logger.error("IMPS P2A Credit Verification Response Received::>>" + new Gson().toJson(response));
					trLogger.error("IMPS P2A Credit Verification Response Received::>>" + new Gson().toJson(response));
					return response;
				}

				response.setNickNameCredit(
						sourceAccount != null ? sourceAccount.getLongName().substring(0, 10) : "ARTHUR FUR");
				response.setNickNameDebit(
						sourceAccount != null ? sourceAccount.getLongName().substring(0, 10) : "ARTHUR FUR");
				logger.error("IMPS P2A Credit Verification Response Received::>>" + new Gson().toJson(response));
				trLogger.error("IMPS P2A Credit Verification Response Received::>>" + new Gson().toJson(response));
				return response;
			}
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_REQUEST);
			trLogger.error("IMPS P2A Credit Verification Response Received::>>" + new Gson().toJson(response));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return response;
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/stopCheque")
	public OtherChannelServiceResponse stopChequePayment(@QueryParam(value = "param1") String custNo,
			@QueryParam(value = "param2") String mmid, @QueryParam(value = "param3") String chequeNo,
			@QueryParam(value = "param4") String remark, @QueryParam(value = "param5") String accNo15digit,
			@QueryParam(value = "param6") String channel) {
		logger.error("<<<:: STOP CHEQUE PAYMENT REQUEST ::>>>");
		logger.error("Stop Cheque Parameters::>> custNo::>>" + custNo + " mmid::>" + mmid + " chequeNo::>>" + chequeNo
				+ " remark:>>" + remark + " accNo15digit::>>" + accNo15digit + " channel::>>" + channel);
		OtherChannelServiceResponse response = new OtherChannelServiceResponse();
		if (channel == null
				|| !new ArrayList<String>(Arrays.asList(MSGConstants.IMPS_CHANNEL, MSGConstants.OTHER_CHANNEL))
						.contains(channel.trim())) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_CHANNEL);
			return response;
		}
		if (channel.equalsIgnoreCase(MSGConstants.IMPS_CHANNEL)) {
			if (custNo == null || custNo.trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_CUSTOMER);
				return response;
			}
			if (mmid == null || mmid.trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_MMID);
				return response;
			}
			if (chequeNo == null || chequeNo.trim().length() != 12) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_CHEQUE_NO);
				return response;
			}
			try {
				response = TransactionServiceImpl.stopChequePayment(custNo, mmid, chequeNo, remark, accNo15digit,
						channel);
				logger.error("Sto Cheque Response::>>" + new Gson().toJson(response));
				return response;
			} catch (Exception e) {
				logger.error("ERROR:", e);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
				return response;
			}
		}
		if (channel.equalsIgnoreCase(MSGConstants.OTHER_CHANNEL)) {
			if (accNo15digit == null || accNo15digit.trim().length() != 15) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_ACCOUNT_NO);
				return response;
			}
			if (chequeNo == null || chequeNo.trim().length() != 12) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_CHEQUE_NO);
				return response;
			}
			response = TransactionServiceImpl.stopChequePayment(custNo, mmid, chequeNo, remark, accNo15digit, channel);
			logger.error("Stop Cheque Response received::>>" + new Gson().toJson(response));
			return response;
		}
		response.setResponse(MSGConstants.ERROR);
		response.setErrorMessage(MSGConstants.INVALID_PARAMETERS);
		return response;
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/stopChequeIVR")
	public OtherChannelServiceResponse stopChequeIVR(@QueryParam(value = "param1") String mobNo,
			@QueryParam(value = "param2") String accNo15digit, @QueryParam(value = "param3") String chequeNo,
			@QueryParam(value = "param4") String remark, @QueryParam(value = "param5") String channel) {
		logger.error("<<<:: STOP CHEQUE IVR REQUEST ::>>>");
		logger.error("Sop cheque IVR Params::>> mobNo::>>" + mobNo + " accNo15digit:>>" + accNo15digit + " chequeNo:>>"
				+ chequeNo + " remark::>>" + remark + " channel::>>" + channel);
		OtherChannelServiceResponse response = new OtherChannelServiceResponse();
		if (channel == null
				|| !new ArrayList<String>(Arrays.asList(MSGConstants.IMPS_CHANNEL, MSGConstants.OTHER_CHANNEL))
						.contains(channel.trim())) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_CHANNEL);
			return response;
		}
		if (channel.equalsIgnoreCase(MSGConstants.IMPS_CHANNEL)) {
			if (chequeNo == null || chequeNo.trim().length() != 12) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_CHEQUE_NO);
				return response;
			}
			try {
				response = TransactionServiceImpl.stopChequePaymentIVR(chequeNo, remark, accNo15digit, channel);
				logger.error("Stop cheque IVR Response Recieved::>>" + new Gson().toJson(response));
				return response;
			} catch (Exception e) {
				// TODO: handle exception
				logger.error("ERROR:", e);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
				return response;
			}

		}
		if (channel.equalsIgnoreCase(MSGConstants.OTHER_CHANNEL)) {
			if (accNo15digit == null || accNo15digit.trim().length() != 15) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_ACCOUNT_NO);
				return response;
			}
			if (chequeNo == null || chequeNo.trim().length() != 12) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_CHEQUE_NO);
				return response;
			}
			response = TransactionServiceImpl.stopChequePaymentIVR(chequeNo, remark, accNo15digit, channel);
			logger.error("Stop cheque IVR Response Recieved::>>" + new Gson().toJson(response));
			return response;
		}
		response.setResponse(MSGConstants.ERROR);
		response.setErrorMessage(MSGConstants.INVALID_PARAMETERS);
		return response;
	}

	// =======below services are added for SimSePaye by Yes bank
	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/debitAccount")
	public DebitAccount debitAccount(DebitAccount request) {
		logger.error("<<<<<:: SIM SE PAY debitAccount.service ::::>>>>");
		logger.error("SIM SE PAY Debit request Received:>>" + new Gson().toJson(request));
		DebitAccount response = new DebitAccount();
		String rrn = DataUtils.getNextRRN();
		if (request == null) {
			response.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
			response.setBankTxnId(rrn);
			response.setTxnDesc(MSGConstants.INVALID_REQUEST);
			return response;
		}
		if (request.getBankCode() == null || request.getBankCode().trim().length() < 1) {
			request.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
			request.setBankTxnId(rrn);
			request.setTxnDesc(MSGConstants.INVALID_BANK_CODE);
			return request;
		}
		if (request.getTxnId() == null || request.getTxnId().trim().length() < 1) {
			request.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
			request.setBankTxnId(rrn);
			request.setTxnDesc(MSGConstants.INVALID_TXN_ID);
			return request;
		}
		if (request.getVersion() == null || request.getVersion().trim().length() < 1) {
			request.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
			request.setTxnDesc(MSGConstants.INVALID_VERSION_CODE);
			return request;
		}
		try {
			if (request.getAccountNo() == null || request.getAccountNo().trim().length() != 15) {
				request.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
				request.setTxnDesc(MSGConstants.INVALID_ACCOUNT_NO);
				return request;
			}
			if (Double.parseDouble("" + request.getTxnAmount()) <= 0) {
				SimSePayTransactionEntry.simSePayTrnEntry(request.getAccountNo(),
						Double.valueOf(request.getTxnAmount().trim()),
						Integer.valueOf(request.getAccountNo().substring(0, 3) == null ? "0"
								: request.getAccountNo().trim().substring(0, 3)),
						MSGConstants.INVALID_TRN_AMOUNT, MSGConstants.INVALID_TRN_AMOUNT,
						MSGConstants.INVALID_TRN_AMOUNT, ResponseCodes.INSUFFICIENT_FUNDS, rrn, MSGConstants.DR,
						request.getTxnId(), request.getBankCode());
				request.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
				request.setTxnDesc(MSGConstants.INVALID_TRN_AMOUNT);
				return request;
			}
			if (DataUtils.getSimSePayTransactionStatus(request.getTxnId(), request.getBankCode().trim(),
					request.getTxnAmount())) {
				SimSePayTransactionEntry.simSePayTrnEntry(request.getAccountNo(),
						Double.valueOf(request.getTxnAmount().trim()),
						Integer.valueOf(request.getAccountNo().substring(0, 3) == null ? "0"
								: request.getAccountNo().trim().substring(0, 3)),
						MSGConstants.DUPLICATE_TRN, MSGConstants.DUPLICATE_TRN, MSGConstants.DUPLICATE_TRN,
						ResponseCodes.ALREADY_DONE_THIS_TRANSACTION, rrn, MSGConstants.DR, request.getTxnId(),
						request.getBankCode());
				request.setBankTxnId(request.getBankTxnId());
				request.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
				request.setTxnDesc(MSGConstants.ALREADY_DONE_THIS_TRN);
				return request;
			}
			D009022 sourceAccount = DataUtils.getAccount(request.getAccountNo().trim());
			if (sourceAccount == null) {
				SimSePayTransactionEntry.simSePayTrnEntry(request.getAccountNo(),
						Double.valueOf(request.getTxnAmount().trim()),
						Integer.valueOf(request.getAccountNo().substring(0, 3) == null ? "0"
								: request.getAccountNo().trim().substring(0, 3)),
						MSGConstants.ACCOUNT_NOT_FOUND, MSGConstants.ACCOUNT_NOT_FOUND, MSGConstants.ACCOUNT_NOT_FOUND,
						ResponseCodes.ACC_NOT_FOUND, rrn, MSGConstants.DR, request.getTxnId(), request.getBankCode());
				request.setBankTxnId(rrn);
				request.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
				request.setTxnDesc(MSGConstants.ACCOUNT_NOT_FOUND);
				return request;
			}
			TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount,
					String.valueOf(request.getTxnAmount()), "D");
			if (res == null) {
				SimSePayTransactionEntry.simSePayTrnEntry(request.getAccountNo(),
						Double.valueOf(request.getTxnAmount().trim()),
						Integer.valueOf(request.getAccountNo().substring(0, 3) == null ? "0"
								: request.getAccountNo().trim().substring(0, 3)),
						MSGConstants.TRANSACTION_VALIDATION_FAILS, MSGConstants.TRANSACTION_VALIDATION_FAILS,
						MSGConstants.TRANSACTION_VALIDATION_FAILS, ResponseCodes.INVALID_PARAMS, rrn, MSGConstants.DR,
						request.getTxnId(), request.getBankCode());
				request.setBankTxnId(rrn);
				request.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
				request.setTxnDesc(MSGConstants.TRANSACTION_VALIDATION_FAILS);
				return request;
			}
			if (res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
				request.setBankTxnId(rrn);
				request.setTxnDesc(res.getErrorMsg());

				if (res.getRespCode().equalsIgnoreCase(ResponseCodes.CLOSED_ACCOUNT))
					request.setTxnStatus(ResponseCodes.SIMSEPAY_CLOSED_ACCOUNT);

				else if (res.getRespCode().equalsIgnoreCase(ResponseCodes.INSUFFICIENT_FUNDS))
					request.setTxnStatus(ResponseCodes.SIMSEPAY_INSUFFICIAENT_BALANCE);

				else if (res.getRespCode().equalsIgnoreCase(ResponseCodes.ACCOUNT_FREEZED))
					request.setTxnStatus(ResponseCodes.SIMSEPAY_ACCOUNT_FREEZED);
				else
					request.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);

				SimSePayTransactionEntry.simSePayTrnEntry(request.getAccountNo(),
						Double.valueOf(request.getTxnAmount().trim()),
						Integer.valueOf(request.getAccountNo().substring(0, 3) == null ? "0"
								: request.getAccountNo().trim().substring(0, 3)),
						res.getErrorMsg(), res.getErrorMsg(), res.getErrorMsg(), response.getTxnStatus(), rrn,
						MSGConstants.DR, request.getTxnId(), request.getBankCode());

				request.setBankTxnId(rrn);
				return request;
			}
			request = TransactionServiceImpl.debitIMPSTransaction(request.getAccountNo().trim(),
					String.valueOf(request.getTxnAmount()), MSGConstants.SIMSAYPAY_TRN, MSGConstants.BLANK_STRING,
					MSGConstants.BLANK_STRING, MSGConstants.BLANK_STRING, MSGConstants.BLANK_STRING,
					MSGConstants.P2A_TRANSACTION, request.getTxnId(), request.getBankCode(), request.getVersion(),
					request);
			logger.error("SIM SE PAY Debit Response Received:>>" + new Gson().toJson(request));
			return request;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ERROR:", e);
			SimSePayTransactionEntry.simSePayTrnEntry(request.getAccountNo(),
					Double.valueOf(request.getTxnAmount().trim()),
					Integer.valueOf(request.getAccountNo().substring(0, 3) == null ? "0"
							: request.getAccountNo().trim().substring(0, 3)),
					MSGConstants.WEB_SERVICE_ERROR, MSGConstants.WEB_SERVICE_ERROR, MSGConstants.WEB_SERVICE_ERROR,
					ResponseCodes.SYSTEM_ERROR, rrn, MSGConstants.DR, request.getTxnId(), request.getBankCode());

			request.setBankTxnId(rrn);
			request.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
			return request;
		}
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/reverseDebitAccount")
	public ReverseDebitAccount reverseDebitAccount(ReverseDebitAccount request) {
		ReverseDebitAccount response = new ReverseDebitAccount();
		try {
			logger.error("<<<<<:: SIM SE PAY REVERSE debitAccount.service ::::>>>>");
			logger.error("SIM SE PAY REVERSSE Debit Account request Received:>>" + new Gson().toJson(request));
			if (request == null) {
				response.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
				response.setTxnDesc(MSGConstants.INVALID_REQUEST);
				return response;
			}
			if (request.getBankTxnId() == null || request.getBankTxnId().trim().length() < 1) {
				request.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
				request.setTxnDesc(MSGConstants.INVALID_RRN_NO);
				return request;
			}
			if (request.getBankCode() == null || request.getBankCode().trim().length() < 1) {
				request.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
				request.setTxnDesc(MSGConstants.INVALID_BANK_CODE);
				return request;
			}
			if (request.getTxnId() == null || request.getTxnId().trim().length() < 1) {
				request.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
				request.setTxnDesc(MSGConstants.INVALID_TXN_ID);
				return request;
			}
			if (request.getVersion() == null || request.getVersion().trim().length() < 1) {
				request.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
				request.setTxnDesc(MSGConstants.INVALID_VERSION_CODE);
				return request;
			}
			if (request.getBankTxnId() == null || request.getBankTxnId().trim().length() < 1) {
				SimSePayTransactionEntry.simSePayTrnEntry(request.getAccountNo(),
						Double.valueOf(request.getTxnAmount().trim()),
						Integer.valueOf(request.getAccountNo().substring(0, 3) == null ? "0"
								: request.getAccountNo().trim().substring(0, 3)),
						MSGConstants.INVALID_REF_NO, MSGConstants.INVALID_REF_NO, MSGConstants.INVALID_REF_NO,
						ResponseCodes.SIMSEPAY_FAILURE, request.getTxnId(), MSGConstants.CR, request.getTxnId(),
						request.getBankCode());
				request.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
				request.setTxnDesc(MSGConstants.INVALID_REF_NO);
				return request;
			}
			if (request.getAccountNo() == null || request.getAccountNo().trim().length() != 15) {
				SimSePayTransactionEntry.simSePayTrnEntry(request.getAccountNo(),
						Double.valueOf(request.getTxnAmount().trim()),
						Integer.valueOf(request.getAccountNo().substring(0, 3) == null ? "0"
								: request.getAccountNo().trim().substring(0, 3)),
						MSGConstants.INVALID_ACCOUNT_NO, MSGConstants.INVALID_ACCOUNT_NO,
						MSGConstants.INVALID_ACCOUNT_NO, ResponseCodes.SIMSEPAY_FAILURE, request.getTxnId(),
						MSGConstants.CR, request.getTxnId(), request.getBankCode());
				request.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
				request.setTxnDesc(MSGConstants.INVALID_ACCOUNT_NO);
				return request;
			}
			if (Double.parseDouble("" + request.getTxnAmount()) == 0) {
				SimSePayTransactionEntry.simSePayTrnEntry(request.getAccountNo(),
						Double.valueOf(request.getTxnAmount().trim()),
						Integer.valueOf(request.getAccountNo().substring(0, 3) == null ? "0"
								: request.getAccountNo().trim().substring(0, 3)),
						MSGConstants.INVALID_TRN_AMOUNT, MSGConstants.INVALID_TRN_AMOUNT,
						MSGConstants.INVALID_TRN_AMOUNT, ResponseCodes.SIMSEPAY_FAILURE, request.getTxnId(),
						MSGConstants.CR, request.getTxnId(), request.getBankCode());
				request.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
				request.setTxnDesc(MSGConstants.INVALID_TRN_AMOUNT);
				return request;
			}
			D009022 sourceAccount = DataUtils.getAccount(request.getAccountNo().trim());
			if (sourceAccount == null) {
				SimSePayTransactionEntry.simSePayTrnEntry(request.getAccountNo(),
						Double.valueOf(request.getTxnAmount().trim()),
						Integer.valueOf(request.getAccountNo().substring(0, 3) == null ? "0"
								: request.getAccountNo().trim().substring(0, 3)),
						MSGConstants.ACCOUNT_NOT_FOUND, MSGConstants.ACCOUNT_NOT_FOUND, MSGConstants.ACCOUNT_NOT_FOUND,
						ResponseCodes.SIMSEPAY_FAILURE, request.getTxnId(), MSGConstants.CR, request.getTxnId(),
						request.getBankCode());
				request.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
				request.setTxnDesc(MSGConstants.ACCOUNT_NOT_FOUND);
				return request;
			}
			TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount,
					String.valueOf(request.getTxnAmount()), "C");
			if (res == null) {
				SimSePayTransactionEntry.simSePayTrnEntry(request.getAccountNo(),
						Double.valueOf(request.getTxnAmount().trim()),
						Integer.valueOf(request.getAccountNo().substring(0, 3) == null ? "0"
								: request.getAccountNo().trim().substring(0, 3)),
						MSGConstants.TRANSACTION_VALIDATION_FAILS, MSGConstants.TRANSACTION_VALIDATION_FAILS,
						MSGConstants.TRANSACTION_VALIDATION_FAILS, ResponseCodes.SIMSEPAY_FAILURE, request.getTxnId(),
						MSGConstants.CR, request.getTxnId(), request.getBankCode());
				request.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
				request.setTxnDesc(MSGConstants.TRANSACTION_VALIDATION_FAILS);
				return request;
			}
			if (res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
				request.setBankTxnId(request.getTxnId());

				if (res.getRespCode().equalsIgnoreCase(ResponseCodes.CLOSED_ACCOUNT))
					request.setTxnStatus(ResponseCodes.SIMSEPAY_CLOSED_ACCOUNT);

				else if (res.getRespCode().equalsIgnoreCase(ResponseCodes.INSUFFICIENT_FUNDS))
					request.setTxnStatus(ResponseCodes.SIMSEPAY_INSUFFICIAENT_BALANCE);

				else if (res.getRespCode().equalsIgnoreCase(ResponseCodes.ACCOUNT_FREEZED))
					request.setTxnStatus(ResponseCodes.SIMSEPAY_ACCOUNT_FREEZED);
				else
					request.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
				SimSePayTransactionEntry.simSePayTrnEntry(request.getAccountNo(),
						Double.valueOf(request.getTxnAmount().trim()),
						Integer.valueOf(request.getAccountNo().substring(0, 3) == null ? "0"
								: request.getAccountNo().trim().substring(0, 3)),
						MSGConstants.TRANSACTION_VALIDATION_FAILS, MSGConstants.TRANSACTION_VALIDATION_FAILS,
						MSGConstants.TRANSACTION_VALIDATION_FAILS, ResponseCodes.SIMSEPAY_FAILURE, request.getTxnId(),
						MSGConstants.CR, request.getTxnId(), request.getBankCode());
				return request;
			}
			return TransactionServiceImpl.reverseDebitAccount(request.getAccountNo().trim(),
					String.valueOf(request.getTxnAmount()), MSGConstants.SIMSAYPAY_TRN, MSGConstants.BLANK_STRING,
					MSGConstants.BLANK_STRING, request.getAccountNo(), MSGConstants.BLANK_STRING,
					MSGConstants.P2A_TRANSACTION, request.getBankTxnId(), new IMPSTransactionRequest(),
					request.getTxnId(), request.getBankCode(), request.getVersion(), request);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("ERROR:", e);
			e.printStackTrace();
			SimSePayTransactionEntry.simSePayTrnEntry(request.getAccountNo(),
					Double.valueOf(request.getTxnAmount().trim()),
					Integer.valueOf(request.getAccountNo().substring(0, 3) == null ? "0"
							: request.getAccountNo().trim().substring(0, 3)),
					MSGConstants.WEB_SERVICE_ERROR, MSGConstants.WEB_SERVICE_ERROR, MSGConstants.WEB_SERVICE_ERROR,
					ResponseCodes.SIMSEPAY_FAILURE, request.getTxnId(), MSGConstants.CR, request.getTxnId(),
					request.getBankCode());
			request.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
			response.setTxnDesc(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/getStatus")
	public GetStatus getStatus(GetStatus request) {
		logger.error("<<<<<:: getStatus.service ::::>>>>");
		logger.error("getStatus.service() Request::>>" + request);
		if (request == null) {
			GetStatus response = new GetStatus();
			response.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
			response.setTxnDesc(MSGConstants.INVALID_REQUEST);
			return response;
		}
		if (request.getBankCode() == null || request.getBankCode().trim().length() < 1) {
			request.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
			request.setTxnId(request.getTxnId());
			request.setTxnDesc(MSGConstants.INVALID_BANK_CODE);
			return request;
		}
		if (request.getTxnId() == null || request.getTxnId().trim().length() < 1) {
			request.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
			request.setTxnDesc(MSGConstants.INVALID_TXN_ID);
			return request;
		}
		if (request.getVersion() == null || request.getVersion().trim().length() < 1) {
			request.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
			request.setTxnDesc(MSGConstants.INVALID_VERSION_CODE);
			return request;
		}
		if (request.getAccountNo() == null || request.getAccountNo().trim().length() != 15) {
			request.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
			request.setTxnDesc(MSGConstants.INVALID_ACCOUNT_NO);
			return request;
		}
		/*
		 * if (request.getBankTxnId() == null || request.getBankTxnId().trim().length()
		 * != 12) { request.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
		 * request.setTxnDesc(MSGConstants.INVALID_REF_NO); return request; }
		 */
		if (Double.parseDouble("" + request.getTxnAmount()) <= 0) {
			request.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
			request.setTxnDesc(MSGConstants.INVALID_TRN_AMOUNT);
			return request;
		}
		D009022 sourceAccount = DataUtils.getAccount(request.getAccountNo());
		if (sourceAccount == null) {
			request.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
			request.setTxnDesc(MSGConstants.ACCOUNT_NOT_FOUND);
			return request;
		}
		TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount,
				String.valueOf(request.getTxnAmount()), "C");
		if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
			request.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
			request.setTxnDesc(res.getErrorMsg());
			return request;
		}
		GetStatus status = CoreBankingOperationImpl.getStatusReq(Integer.valueOf(sourceAccount.getId().getLbrCode()),
				sourceAccount.getId().getPrdAcctId(), MSGConstants.P2A_TRANSACTION, MSGConstants.SIMSAYPAY_TRN,
				Double.valueOf(request.getTxnAmount()), request.getBankTxnId(), MSGConstants.BLANK_STRING,
				MSGConstants.BLANK_STRING, request.getAccountNo(), MSGConstants.BLANK_STRING, request.getAccountNo(),
				request);
		System.out.println("Final Response status:>>>" + status);
		logger.error("Final Response status:>>>" + status);
		return status;
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/accountStatus")
	public CheckAccountStatus accountStatus(CheckAccountStatus request) {
		logger.error("<<<<<:: accountStatus.service ::::>>>>");
		logger.error("Check Account Staus Request::>>" + new Gson().toJson(request));
		if (request == null) {
			CheckAccountStatus response = new CheckAccountStatus();
			response.setAccountStatus(ResponseCodes.SIMSEPAY_FAILURE);
			return response;
		}

		if (request.getBankCode() == null || request.getBankCode().trim().length() < 1) {
			request.setAccountStatus(ResponseCodes.SIMSEPAY_FAILURE);
			return request;
		}
		if (request.getTxnId() == null || request.getTxnId().trim().length() < 1) {
			request.setAccountStatus(ResponseCodes.SIMSEPAY_FAILURE);
			return request;
		}
		if (request.getVersion() == null || request.getVersion().trim().length() < 1) {
			request.setAccountStatus(ResponseCodes.SIMSEPAY_FAILURE);
			return request;
		}

		if (request.getAccountNo() == null || request.getAccountNo().trim().length() != 15) {
			request.setAccountStatus(ResponseCodes.SIMSEPAY_FAILURE);
			return request;
		}
		D009022 sourceAccount = DataUtils.getAccount(request.getAccountNo());
		if (sourceAccount == null) {
			request.setAccountStatus(ResponseCodes.SIMSEPAY_FAILURE);
			return request;
		}
		TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount, String.valueOf("10"),
				"D");
		if (res == null) {
			request.setAccountStatus(ResponseCodes.SIMSEPAY_FAILURE);
			return request;
		}
		if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
			request.setAccountStatus(ResponseCodes.SIMSEPAY_FAILURE);
			if (res.getRespCode().equalsIgnoreCase(ResponseCodes.CLOSED_ACCOUNT))
				request.setAccountStatus(ResponseCodes.SIMSEPAY_CLOSED_ACCOUNT);

			else if (res.getRespCode().equalsIgnoreCase(ResponseCodes.INSUFFICIENT_FUNDS))
				request.setAccountStatus(ResponseCodes.SIMSEPAY_INSUFFICIAENT_BALANCE);

			else if (res.getRespCode().equalsIgnoreCase(ResponseCodes.ACCOUNT_FREEZED))
				request.setAccountStatus(ResponseCodes.SIMSEPAY_ACCOUNT_FREEZED);
			else
				request.setAccountStatus(ResponseCodes.SIMSEPAY_FAILURE);
			return request;
		}
		D350078 d350078 = DataUtils.getCustomerDetails(request.getAccountNo());
		request.setAccountStatus(ResponseCodes.SIMSEPAY_SUCCESS);
		request.setCustomerName(
				sourceAccount.getLongName() != null ? sourceAccount.getLongName().trim() : sourceAccount.getLongName());
		request.setMobileNumber(d350078 != null ? d350078.getId().getMobileNo() : MSGConstants.BLANK_STRING);
		return request;
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/prepaidCardPayment")
	public IMPSTransactionResponse prepaidCardPayment(IMPSTransactionRequest request) {
		logger.error("<<<<<<<---------- prepaidCardPayment ------------->>>>>>>");
		logger.error("PrepaidCardPayment Parameters::>>" + new Gson().toJson(request));
		IMPSTransactionResponse response = new IMPSTransactionResponse();
		if (request.getTransType().equalsIgnoreCase(MSGConstants.PREPAID_CARD)) {
			if (request.getRemitterMobile() == null || request.getRemitterMobile().trim().length() != 12) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_MOB_NO);
				response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				return response;
			}
			D390077 d390077 = DataUtils.getPrepaidcardObject(request.getRemitterMobile().trim().substring(2),
					request.getCardAliaceNo().trim());
			if (d390077 == null) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_MOB_NO);
				response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				return response;
			}
			response = DataUtils.validatePrepaidCardLimit(request.getCardAliaceNo().trim(),
					request.getRemitterMobile().trim().substring(2), request.getTransAmt());
			System.out.println("validatePrepaidCardLimit().Response::>>" + response);
			if (response != null && response.getResponse().equalsIgnoreCase(MSGConstants.ERROR))
				return response;
			if (Double.parseDouble("" + request.getTransAmt()) == 0) {
				response.setErrorCode(ResponseCodes.INSUFFICIENT_FUNDS);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_AMOUNT);
				return response;
			}
			D009022 sourceAccount = DataUtils.getAccountDetails(d390077.getLbrCode(), d390077.getPrdAcctId().trim());
			if (sourceAccount == null) {
				response.setErrorCode(ResponseCodes.ACCOUNT_NOT_NORMAL);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_AMOUNT);
				return response;
			}
			TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount,
					String.valueOf(request.getTransAmt()), "D");
			if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
				PrepaidCardLoadBalanceEntry.storePrepaidcardEntry(sourceAccount.getId().getLbrCode(),
						sourceAccount.getId().getPrdAcctId(), request.getRemitterMobile(), request.getTransAmt(), "0",
						d390077.getId().getCardAlias().trim(), res.getErrorMsg(), res.getErrorMsg(), res.getErrorMsg(),
						res.getRespCode(), DataUtils.getNextRRN());
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(res.getErrorMsg());
				response.setErrorCode(res.getRespCode());
				return response;
			}
			try {
				response = TransactionServiceImpl.loadBalancePrepaidCard(sourceAccount, "" + request.getTransAmt(),
						request.getNarration(), request.getRemitterMobile(), request.getTransType(), request, d390077);
				logger.error("PREPAID CARD RESPONSE LOAD BALANCE RESPONSE::>>" + new Gson().toJson(response));
				return response;
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("ERROR:", e);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
				return response;
			}
		}
		response.setResponse(MSGConstants.ERROR);
		response.setErrorMessage(MSGConstants.INVALID_REQUEST);
		return response;
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/accBasedTransaction")
	public TransactionValidationResponse accBasedTransaction(@QueryParam(value = "param1") String sourceAcc,
			@QueryParam(value = "param2") String destAcc, @QueryParam(value = "param3") String custType,
			@QueryParam(value = "param4") String amount, @QueryParam(value = "param5") String paymentMode,
			@QueryParam(value = "param6") String transType) {
		TransactionValidationResponse response = new TransactionValidationResponse();
		if (!new ArrayList<>(Arrays.asList(MSGConstants.BR, MSGConstants.MC, MSGConstants.BC))
				.contains(custType.trim())) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.INVALID_TRANSACTION_TYPE);
			return response;
		}
		if (!new ArrayList<>(Arrays.asList(MSGConstants.P, MSGConstants.V)).contains(paymentMode.trim())) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.INVALID_PAYMENT_MODE);
			return response;
		}
		if (!custType.equalsIgnoreCase(MSGConstants.BR)) {
			if (sourceAcc == null || sourceAcc.trim().length() != 15) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_SOURCE_ACCOUNT_NO);
				return response;
			}
		}
		if (custType == null || custType.trim().length() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.INVALID_CUST_TYPE);
			return response;
		}
		if (!custType.equalsIgnoreCase(MSGConstants.BRANCH_USER)) {
			if (destAcc == null || destAcc.trim().length() != 15) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_DEST_ACCOUNT_NO);
				return response;
			}
		}
		if (amount == null || amount.trim().length() < 1 || Double.valueOf(amount) == 0d) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.INVALID_AMOUNT);
			return response;
		}
		try {
			return TransactionServiceImpl.validateOrDoPayement(sourceAcc, destAcc, custType, amount, paymentMode,
					transType);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("ERROR:", e);
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/initiateBillPaymentMPOS")
	public IMPSTransactionResponse initiateBillPaymentMPOS(IMPSTransactionRequest request) {
		logger.error("<<<<<<<---------- initiateBillPaymentMPOS ------------->>>>>>>");
		logger.error("Transaction Type::>>" + request.getTransType() + " request.getAccountNo()::>>"
				+ request.getRemitterAccNo() + " MMID::>>" + request.getRemitterMMID() + " MOBILE NO::>>"
				+ request.getRemitterMobile() + " Ben Accno::>>" + request.getBenfAccNo() + " Ben IFSC::>>"
				+ request.getBenfIFSC() + " Ben MMID::>>" + request.getBenfMMID() + " Ben Mobileno::>>"
				+ request.getBenfMobile() + " Narretion::>>" + request.getNarration() + " Amount::>>"
				+ request.getTransAmt());
		IMPSTransactionResponse response = new IMPSTransactionResponse();
		System.out.println("request.getTransType()::>>" + request.getTransType());
		if (request.getTransType().equalsIgnoreCase(MSGConstants.BILLPAY)) {

			if (null == request.getRemitterAccNo() || request.getRemitterAccNo().trim().length() != 15) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_REMITTER_ACC_NO);
				response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				return response;
			}
			if (Double.parseDouble("" + request.getTransAmt()) == 0) {
				response.setErrorCode(ResponseCodes.INSUFFICIENT_FUNDS);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_AMOUNT);
				return response;
			}
			if (request.getConsumerNo() == null || request.getConsumerNo().trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_CONSUMER_NO);
				return response;
			}
			if (request.getOperator() == null || request.getOperator().trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_OPERATOR);
				return response;
			}
			D009022 sourceAccount = DataUtils.getAccount(request.getRemitterAccNo());
			if (sourceAccount == null) {
				response.setErrorCode(ResponseCodes.ACCOUNT_NOT_NORMAL);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
				return response;
			}
			TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount,
					String.valueOf(request.getTransAmt()), "D");
			if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
				BillPaymentTransactionEntry.transactionEntryBillPay(sourceAccount.getId().getLbrCode(),
						sourceAccount.getId().getPrdAcctId(), "MBTR", 0, 0, request.getConsumerNo(),
						request.getOperator(), request.getTransAmt(), ResponseCodes.INVALID_NBIN, res.getErrorMsg(),
						request.getRRNNo(), "D");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(res.getErrorMsg());
				response.setErrorCode(res.getRespCode());
				return response;
			}
			try {
				return TransactionServiceImpl.initiateBillPayement(request.getRemitterAccNo(),
						request.getRemitterAccNo(), String.valueOf(request.getTransAmt()), request.getNarration(),
						request.getRemitterMobile(), request.getRemitterMMID(), request.getBenfMobile(),
						request.getBenfMMID(), request.getTransType(), request);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("ERROR:", e);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
				return response;
			}
		}
		response.setResponse(MSGConstants.ERROR);
		response.setErrorMessage(MSGConstants.INVALID_REQUEST);
		return response;
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/doATMTransaction")
	public static ATMTransactionResponse doATMTransaction(ATMTransactionRequest request) {
		ATMTransactionResponse response = new ATMTransactionResponse();
		try {
			if (request == null) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_REQUEST);
				return response;
			}
			if (request.getCardNo() == null || request.getCardNo().trim().length() != 16) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_CARD_NO);
				return response;
			}
			if (request.getAcqId() == null || request.getAcqId().trim().length() != 6) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_ACQ_ID);
				return response;
			}
			if (request.getAtmAccId() == null || request.getAtmAccId().trim().length() != 32) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_ATM_ACCID);
				return response;
			}
			if (request.getAmount() == 0) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_AMOUNT);
				return response;
			}
			if (request.getBrCode() == null || request.getBrCode().trim().equalsIgnoreCase("0")) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_BR_CODE);
				return response;
			}
			if (request.getNetworkId() == null || request.getNetworkId().trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_NETWORK_ID);
				return response;
			}
			if (request.getRrn() == null || request.getRrn().trim().length() != 12) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_RRN_NO);
				return response;
			}
			if (request.getToAccId() == null || request.getToAccId().trim().length() != 32) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_TOATM_ID);
				return response;
			}
			if (request.getToBrcode() == null || request.getToBrcode().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_TOATM_BR_CODE);
				return response;
			}
			if (request.getAtmAuthNo() == null || request.getAtmAuthNo().trim().length() != 6) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_ATM_AUTH_NO);
				return response;
			}
			return ATMTransactionServiceIMPL.doATMTransaction(request);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("ERROR:", e);
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
	}

	@POST
	@Path("/rblCardBoarding")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public static OtherChannelServiceResponse rblCardBoarding(CustomerPhysicalCardOnboardingreq request) {
		OtherChannelServiceResponse response = new OtherChannelServiceResponse();
		try {
			if (request == null) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_REQUEST);
				return response;
			}
			if (request.getAnnualIncome() == null || request.getAnnualIncome().trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_ANNUAL_INCOME);
				return response;
			}
			if (request.getCardAlias() == null || request.getCardAlias().trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_CARD_ALIAS_NO);
				return response;
			}
			if (request.getCardNo() == null || request.getCardNo().trim().length() != 16) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_CARD_NO);
				return response;
			}
			if (request.getCity() == null || request.getCity().trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_CITY);
				return response;
			}
			if (request.getCustomerStatus() == null || request.getCustomerStatus().trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_CUST_STATUS);
				return response;
			}
			if (request.getCustomerType() == null || request.getCustomerType().trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_CUST_TYPE);
				return response;
			}
			if (request.getDateofbirth() == null || request.getDateofbirth().trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_DOB);
				return response;
			}
			if (request.getEmailaddress() == null || request.getEmailaddress().trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_EMAIL_ID);
				return response;
			}
			if (request.getFatcadecl() == null || request.getFatcadecl().trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_TAX_RESIDENCE_TYPE);
				return response;
			}
			if (request.getFirstname() == null || request.getFirstname().trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.FIRST_NAME_MANDETARY);
				return response;
			}
			if (request.getGender() == null || request.getGender().trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.GENDOR_IS_MANDETARORY);
				return response;
			}
			if (request.getLaddress1() == null || request.getLaddress1().trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.ADDRESS_IS_MANDETARY);
				return response;
			}
			if (request.getLcity() == null || request.getLcity().trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_CITY);
				return response;
			}
			if (request.getNationality() == null || request.getNationality().trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.NATIONALITY_IS_MANDETARY);
				return response;
			}
			if (request.getOccupation() == null || request.getOccupation().trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.OCCUPATION_FEILD_MANDETARY);
				return response;
			}
			if (request.getPincode() == null || request.getPincode().trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_PINCODE);
				return response;
			}
			if (request.getPoliticallyExposedPerson() == null
					|| request.getPoliticallyExposedPerson().trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_POLITICALLY_EXPOSED_PERSON_TYPE);
				return response;
			}
			if (request.getProduct() == null || request.getProduct().trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_PRODUCT);
				return response;

			}
			if (request.getResaddress1() == null || request.getResaddress1().trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.ADDRESS_IS_MANDETARY);
				return response;
			}
			if (request.getRescountry() == null || request.getRescountry().trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.COUNTRY_IS_MANDETORY);
				return response;
			}
			if (request.getSourceIncomeType() == null || request.getSourceIncomeType().trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_SOURCE_INCOME_TYPE);
				return response;
			}
			return DataUtils.onBoardPrepaidCardCustomer(request);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("ERROR:", e);
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/reverseATMTransaction")
	public static ATMTransactionResponse reverseATMTransaction(ATMTransactionRequest request) {
		ATMTransactionResponse response = new ATMTransactionResponse();
		try {
			if (request == null) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_REQUEST);
				return response;
			}
			if (request.getCardNo() == null || request.getCardNo().trim().length() != 16) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_CARD_NO);
				return response;
			}
			if (request.getAcqId() == null || request.getAcqId().trim().length() != 6) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_ACQ_ID);
				return response;
			}
			if (request.getAtmAccId() == null || request.getAtmAccId().trim().length() != 32) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_ATM_ACCID);
				return response;
			}
			if (request.getAmount() == 0) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_AMOUNT);
				return response;
			}
			if (request.getBrCode() == null || request.getBrCode().trim().equalsIgnoreCase("0")) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_BR_CODE);
				return response;
			}
			if (request.getNetworkId() == null || request.getNetworkId().trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_NETWORK_ID);
				return response;
			}
			if (request.getRrn() == null || request.getRrn().trim().length() != 12) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_RRN_NO);
				return response;
			}
			if (request.getToAccId() == null || request.getToAccId().trim().length() != 32) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_TOATM_ID);
				return response;
			}
			if (request.getToBrcode() == null || request.getToBrcode().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_TOATM_BR_CODE);
				return response;
			}
			if (request.getAtmAuthNo() == null || request.getAtmAuthNo().trim().length() != 6) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_ATM_AUTH_NO);
				return response;
			}
			return ATMTransactionServiceIMPL.reverseATMTransaction(request);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/storeBillPaymentRRN")
	public OtherChannelServiceResponse storeBillPaymentRRN(IMPSTransactionRequest request) {
		logger.error("<<<<<<<---------- impsTransactionOtherBank ------------->>>>>>>");
		logger.error("Request Received::>>" + request);
		OtherChannelServiceResponse response = new OtherChannelServiceResponse();
		if (request == null) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_REQUEST);
			return response;
		}
		if (request.getTransType().equalsIgnoreCase(MSGConstants.BILLPAY)) {
			try {
				if (request.getAccountNo() == null || request.getAccountNo().trim().length() != 32) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
					response.setErrorMessage(MSGConstants.INVALID_ACCOUNT_NO);
					return response;
				}
				if (request.getRemitterAccNo() == null || request.getRemitterAccNo().trim().length() != 15) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
					response.setErrorMessage(MSGConstants.INVALID_ACCOUNT_NO);
					return response;
				}
				if (request.getConsumerNo() == null) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_CONSUMER_NO);
					return response;
				}
				if (request.getOperator() == null) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_OPERATOR);
					return response;
				}
				if (request.getTransAmt() == null || request.getTransAmt() == 0) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_AMOUNT);
					return response;
				}
				if (request.getRRNNo() == null || request.getRRNNo().trim().length() != 12) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_RRN_NO);
					return response;
				}
				response = TransactionServiceImpl.updateBillPaymentRefNo(
						Integer.valueOf(request.getRemitterAccNo().substring(0, 3)), request.getAccountNo(),
						request.getConsumerNo(), request.getOperator(), request.getTransAmt(), request.getRRNNo(),
						request.getIfscCode());
				logger.error("Response Received::>>" + response);
				return response;
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("ERROR:", e);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
				return response;
			}
		}
		response.setResponse(MSGConstants.ERROR);
		response.setErrorMessage(MSGConstants.INVALID_REQUEST);
		return response;
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/atmCharges")
	public TransactionValidationResponse atmCharges(@QueryParam(value = "param1") String accNo,
			@QueryParam(value = "param2") String narretion, @QueryParam(value = "param3") String amount,
			@QueryParam(value = "param4") String rrn, @QueryParam(value = "param5") String chargeType,
			@QueryParam(value = "param6") String cardNo) {
		System.out.println("accNo::>>" + accNo + " narretion:" + narretion + " amount::>>" + amount + " rrn::>>" + rrn
				+ " chargeType::>>" + chargeType + " cardNo::>>" + cardNo);
		TransactionValidationResponse response = new TransactionValidationResponse();
		if (accNo == null || accNo.trim().length() != 18) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.INVALID_SOURCE_ACCOUNT_NO);
			return response;
		}
		/*
		 * if (narretion == null || narretion.trim().length() < 1) {
		 * response.setResponse(MSGConstants.ERROR);
		 * response.setErrorMsg(MSGConstants.NA); return response; }
		 */
		if (amount == null || amount.trim().length() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.INVALID_AMOUNT);
			return response;
		}
		if (rrn == null || rrn.trim().length() != 12) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.INVALID_RRN_NO);
			return response;
		}
		if (cardNo == null || cardNo.trim().length() != 16) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.INVALID_CARD_NO);
			return response;
		}
		if (chargeType == null || chargeType.trim().length() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.INVALID_CHARGE_TYPE);
			return response;
		}
		if (!ATMTransactionServiceIMPL.loadHashMap().containsKey(chargeType.trim().toUpperCase())) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.INVALID_CHARGE_TYPE);
			return response;
		}
		int branchCode = Integer.valueOf(accNo.substring(0, 4));
		String accountNo = String.format("%-8s", accNo.substring(4, 10)) + "00000000" + accNo.substring(10)
				+ "00000000";
		if (ATMTransactionServiceIMPL.validateATMChargesTransaction(branchCode, accountNo, amount, narretion, rrn,
				chargeType, cardNo)) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.DUPLICATE_TRN);
			return response;
		}
		try {
			return ATMTransactionServiceIMPL.atmCharges(branchCode, accountNo, amount, narretion, rrn, chargeType,
					cardNo);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ERROR:", e);
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/reversePrepaidCardPayment")
	public IMPSTransactionResponse reversePrepaidCardPayment(IMPSTransactionRequest request) {
		logger.error("<<<<<<<---------- prepaidCardPayment ------------->>>>>>>");
		logger.error("PrepaidCardPayment Reversal Parameters::>>" + new Gson().toJson(request));
		IMPSTransactionResponse response = new IMPSTransactionResponse();
		if (request.getTransType().equalsIgnoreCase(MSGConstants.PREPAID_CARD)) {
			if (request.getRemitterMobile() == null || request.getRemitterMobile().trim().length() != 12) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_MOB_NO);
				response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				return response;
			}
			if (request.getCardAliaceNo() == null || request.getCardAliaceNo().trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_CARD_ALIAS_NO);
				response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				return response;
			}

			D390077 d390077 = DataUtils.getPrepaidcardObject(request.getRemitterMobile().trim().substring(2),
					request.getCardAliaceNo().trim());
			if (d390077 == null) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_MOB_NO);
				response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				return response;
			}
			if (Double.parseDouble("" + request.getTransAmt()) == 0) {
				response.setErrorCode(ResponseCodes.INSUFFICIENT_FUNDS);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_AMOUNT);
				return response;
			}
			if (DataUtils.validateRequest(request, d390077)) {
			} else {
				response.setErrorCode(ResponseCodes.NO_RECORD_FOUND);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.TRANSACTION_NOT_FOUND);
				return response;
			}
			D009022 sourceAccount = DataUtils.getAccountDetails(d390077.getLbrCode(), d390077.getPrdAcctId().trim());
			if (sourceAccount == null) {
				response.setErrorCode(ResponseCodes.ACCOUNT_NOT_NORMAL);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_AMOUNT);
				return response;
			}
			TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount,
					String.valueOf(request.getTransAmt()), "C");
			if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
				PrepaidCardLoadBalanceEntry.storeReversePrepaidcardEntry(sourceAccount.getId().getLbrCode(),
						sourceAccount.getId().getPrdAcctId(), request.getRemitterMobile(), request.getTransAmt(), "0",
						d390077.getId().getCardAlias().trim(), res.getErrorMsg(), res.getErrorMsg(), res.getErrorMsg(),
						res.getRespCode(), res.getRrn());
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(res.getErrorMsg());
				response.setErrorCode(res.getRespCode());
				return response;
			}
			try {
				return TransactionServiceImpl.reverseLoadBalancePrepaidCard(sourceAccount, request.getTransAmt() + "",
						"PREPAID_REVERSAL/" + request.getRemitterMobile() + "/" + request.getCardAliaceNo(),
						request.getRemitterAccNo(), MSGConstants.PREPAID_CARD, request, d390077);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("ERROR:", e);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
				return response;
			}
		}
		response.setResponse(MSGConstants.ERROR);
		response.setErrorMessage(MSGConstants.INVALID_REQUEST);
		return response;
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/updateLoadBalStatus")
	public IMPSTransactionResponse updateLoadBalStatus(@QueryParam(value = "param1") String rrn,
			@QueryParam(value = "param2") String rblRrn) {
		logger.error("<<<<<<<---------- updateLoadBalStatus ------------->>>>>>>");
		logger.error("RRN::>>" + rrn);
		IMPSTransactionResponse response = new IMPSTransactionResponse();
		try {
			if (rrn == null || rrn.trim().length() != 12) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_RRN_NO);
				response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				return response;
			}
			return DataUtils.updateRBLRRN(rrn, rblRrn);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("", e);
			response.setResponse(MSGConstants.ERROR);
			response.setRrnNo(rrn);
			response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/storeSMSRequest")
	public OtherChannelServiceResponse updateLoadBalStatus(@QueryParam(value = "param1") String bankCode,
			@QueryParam(value = "param2") String cardAliaceNo, @QueryParam(value = "param3") String mobileNo,
			@QueryParam(value = "param4") String sms) {
		logger.error("<<<<<<<---------- updateLoadBalStatus ------------->>>>>>>");
		logger.error("bankCode::>>" + bankCode + " cardAliaceNo::>>" + cardAliaceNo + " mobileNo::>>" + mobileNo
				+ " SMS::>>" + sms);
		OtherChannelServiceResponse response = new OtherChannelServiceResponse();
		try {
			if (bankCode == null || bankCode.trim().length() < 4) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_BANK_CODE);
				response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				return response;
			}
			if (cardAliaceNo == null || cardAliaceNo.trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_BANK_CODE);
				response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				return response;
			}
			if (mobileNo == null || mobileNo.trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_MOB_NO);
				response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				return response;
			}
			if (sms == null || sms.trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_MOB_NO);
				response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				return response;
			}
			if (DataUtils.storeSMSEntries(bankCode, cardAliaceNo, mobileNo, sms)) {
				response.setResponse(MSGConstants.SUCCESS);
				response.setErrorMessage(MSGConstants.SUCCESFUL_TRN);
				response.setErrorCode(ResponseCodes.SUCCESS);
				return response;
			}
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_REQUEST);
			response.setErrorCode(ResponseCodes.SYSTEM_ERROR);
			return response;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("", e);
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/loanDeposit")
	public IMPSNewTransactionResponse impsLoanOtherBank(@QueryParam(value = "param1") String debitAcct,
			@QueryParam(value = "param2") String loanAcct, @QueryParam(value = "param3") String amt,
			@QueryParam(value = "param4") String remark) {
		IMPSNewTransactionResponse response = new IMPSNewTransactionResponse();
		try {
			logger.error("<<<<:: IMPS TRANSACTION ::>>>>>");

			if (null == debitAcct || debitAcct.trim().length() < 10) {
				response.setResCode(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_REMITTER_ACC_NO);
				response.setSuccess(false);
				return response;
			}

			if (Double.parseDouble(amt) == 0) {
				response.setErrorMsg(MSGConstants.INVALID_AMOUNT);
				response.setResCode(MSGConstants.ERROR);
				response.setSuccess(false);
				return response;
			}
			D009022 sourceAccount = DataUtils.getAccount(debitAcct);
			D009022 loanAccount = DataUtils.getAccount(loanAcct);
			if (!RtgsNeftHostToHostConstants.LOAN.getMessage()
					.contains(
							DataUtils
									.getProductMaster(loanAccount.getId().getLbrCode() + "",
											loanAccount.getId().getPrdAcctId().substring(0, 8).trim())
									.getModuleType() + "")) {
				response.setResCode(MSGConstants.ERROR);
				response.setErrorMsg("Benificery must be Loan Account");
				response.setSuccess(false);
				return response;
			}
			TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount, amt, "D");
			if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
				response.setResCode(MSGConstants.ERROR);
				response.setErrorMsg(res.getErrorMsg());
				response.setSuccess(false);
				return response;
			}
			try {
				// System.out.println("hello");
				response = TransactionServiceImpl.initiateIMPSLoanTransaction(sourceAccount, loanAccount, amt,
						remark.trim());
				logger.error("IMPS P2A Response::>>" + new Gson().toJson(response));
				return response;
			} catch (Exception e) {
				// TODO: handle exception
				logger.error("ERROR:", e);
				e.printStackTrace();
				response.setResCode(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
				response.setSuccess(false);
				return response;
			}
			/*
			 * } response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMessage(MSGConstants.INVALID_REQUEST); return response;
			 */

		} catch (Exception e) {
			logger.error("ERROR:", e);
			// TODO: handle exception
			e.printStackTrace();
			response.setResCode(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			response.setSuccess(false);
			return response;
		}
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/impsFacilityChargesTransaction")
	public IMPSChargesResponse impsCharges(IMPSChargesRequest request) {
		logger.error("<<<<<<<---------- prepaidCardPayment ------------->>>>>>>");
		logger.error("PrepaidCardPayment Reversal Parameters::>>" + new Gson().toJson(request));
		IMPSChargesResponse response = new IMPSChargesResponse();

		if (request.getRemitterMobile() == null || request.getRemitterMobile().trim().length() < 10) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_MOB_NO);
			response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
			response.setValid(false);
			return response;
		}

		if (request.getRemitterMMID().trim().length() != 7) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorCode(MSGConstants.INVALID_REMITTER_MMID);
			response.setValid(false);
			return response;
		}
		if (request.getRemitterAccNo().trim().length() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorCode(MSGConstants.INVALID_REMITTER_ACC_NO);
			response.setValid(false);
			return response;
		}
		/*
		 * if (request.getBenfMobile().trim().length() != 12) {
		 * response.setErrorCode(MSGConstants.ERROR);
		 * response.setErrorMessage(MSGConstants.INVALID_BEN_MOB_NO); return response; }
		 * if (request.getBenfMMID().trim().length() != 7) {
		 * response.setErrorCode(MSGConstants.ERROR);
		 * response.setErrorMessage(MSGConstants.INVALID_BEN_MMID); return response; }
		 */
		if (request.getChargesAmount() == 0D) {
			response.setErrorCode(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_AMOUNT);
			response.setValid(false);
			return response;
		}
		D009022 sourceAccount = DataUtils.getAccount(request.getRemitterAccNo());
		if (sourceAccount == null) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
			response.setValid(false);
			return response;
		}

		try {
			response = ATMTransactionServiceIMPL.impsCharges(sourceAccount.getId().getLbrCode(),
					sourceAccount.getId().getPrdAcctId(), request.getChargesAmount().toString(), request.getNarration(),
					request.getRRNNo());
			response.setAccountNo(request.getRemitterAccNo());
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ERROR:", e);
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}

	}

	@SuppressWarnings("deprecation")
	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/UPICreditTransaction")
	public IMPSTransactionResponse upiCreditTransaction(UPIRequest request) {

		IMPSTransactionResponse response = new IMPSTransactionResponse();
		if (request == null) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorCode(MSGConstants.INVALID_REQUEST);
			return response;
		}
		try (org.util.nanolog.Logger trLogger = org.util.nanolog.Logger.getLogger(LoggerType.BUFFERED, logWriter)) {
			if (null == request.getAccountNo() || request.getAccountNo().trim().length() < 10) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_REMITTER_ACC_NO);
				response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				logger.error(MSGConstants.INVALID_REMITTER_ACC_NO);
				trLogger.info("IMPS P2A Credit Response::>>" + new Gson().toJson(response));
				return response;
			}

			if (request.getAmt() == 0) {
				response.setErrorCode(ResponseCodes.INSUFFICIENT_FUNDS);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_AMOUNT);
				logger.error(MSGConstants.INVALID_AMOUNT);
				return response;
			}
			if (request.getAmt() > Double
					.valueOf(ConfigurationLoader.getParameters(false).getProperty("MAX_AMOUNT"))) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.EXCEED_DAILY_LIMIT);
				response.setRrnNo(request.getRrn());
				response.setErrorCode("M2");
				trLogger.info("IMPS P2A Credit Response::>>" + new Gson().toJson(response));
				return response;
			}

			D009022 sourceAccount = DataUtils.getAccount(request.getAccountNo());
			if (sourceAccount == null) {
				response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
				logger.error(MSGConstants.ACCOUNT_NOT_FOUND);
				trLogger.info("IMPS P2A Credit Response::>>" + new Gson().toJson(response));
				return response;
			}
			TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount,
					String.valueOf(request.getAmt()), "C");

			if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {

				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(res.getErrorMsg());

				response.setErrorCode(res.getRespCode());
				trLogger.info("UPI Credit Response::>>" + new Gson().toJson(response));
				return response;
			}

			Session session = HBUtil.getSessionFactory().openSession();
			Criteria criteria2 = session.createCriteria(UPITransaction.class);
			/*criteria2.add(Restrictions.eq("rrnNo", String.format("%1$-20s", request.getRrn().trim())));
			criteria2.add(Restrictions.eq("crAcctId", String.format("%1$-20s", request.getAccountNo())));
			criteria2.add(Restrictions.eq("txnid", String.format("%1$-20s", request.getTxnId())));
			*/
			criteria2.add(Restrictions.eq("rrnNo", request.getRrn().trim()));
			criteria2.add(Restrictions.eq("crAcctId", sourceAccount.getId().getPrdAcctId().trim()));
			criteria2.add(Restrictions.eq("txnid", request.getTxnId()));
			List<UPITransaction> list2 = criteria2.list();
			session.close();
			session = null;
			criteria2 = null;
			if (!list2.isEmpty()) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.DUPLICATE_TRN);
				response.setRrnNo(request.getRrn());
				response.setErrorCode(ResponseCodes.ALREADY_DONE_THIS_TRANSACTION);
				trLogger.info("IMPS P2A Credit Response::>>" + new Gson().toJson(response));
				return response;
			}
			response = TransactionServiceImpl.creditUPITransaction(request.getAccountNo(), request.getAmt(), "UPI/" + request.getRrn() + "/" + DateUtil.getcurrentDateString()+"/"+request.getAccountNo()+"/"+request.getRemark(), request.getRrn(), request);
			response.setNickNameCredit(
					sourceAccount != null ? sourceAccount.getLongName().substring(0, 10) : "ARTHUR FUR");
			response.setNickNameDebit(
					sourceAccount != null ? sourceAccount.getLongName().substring(0, 10) : "ARTHUR FUR");
			logger.error("UPI Credit Response Received ::>>" + new Gson().toJson(response));
			trLogger.info("UPI Credit Response::>>" + new Gson().toJson(response));

			trLogger.info("IMPS P2A Credit Response::>>" + new Gson().toJson(response));
			return response;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return response;

	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/ownAcctFundTransfer")
	public IMPSTransactionResponse ownAcctFundTransfer(IMPSTransactionRequest request) {
		IMPSTransactionResponse response = new IMPSTransactionResponse();
		try (org.util.nanolog.Logger trLogger = org.util.nanolog.Logger.getLogger(LoggerType.BUFFERED, logWriter)) {
			logger.error("<<<<:: IMPS Own Acct TRANSACTION ::>>>>>");
			logger.error("IMPS Own Acct Request Received::>>" + new Gson().toJson(request));
			trLogger.info("IMPS Own Acct Request Received::>>" + new Gson().toJson(request));
			System.out.println("TransactionService.Own Acct " + new Gson().toJson(request));
			if (request == null) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_REQUEST);
				trLogger.info("IMPS P2P Response::>>" + new Gson().toJson(response));
				return response;
			}
			if (request.getTransType().equalsIgnoreCase(MSGConstants.P2P_TRANSACTION)) {
				if (request.getRemitterMobile().trim().length() != 12) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorCode(MSGConstants.INVALID_REMITTER_MOB_NO);
					trLogger.info("IMPS P2P Response::>>" + new Gson().toJson(response));
					return response;
				}
				if (request.getRemitterMMID().trim().length() != 7) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorCode(MSGConstants.INVALID_REMITTER_MMID);
					trLogger.info("IMPS P2P Response::>>" + new Gson().toJson(response));
					return response;
				}
				if (request.getRemitterAccNo().trim().length() < 1) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorCode(MSGConstants.INVALID_REMITTER_ACC_NO);
					trLogger.info("IMPS P2P Response::>>" + new Gson().toJson(response));
					return response;
				}
				if (request.getBenfMobile().trim().length() != 12) {
					response.setErrorCode(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_BEN_MOB_NO);
					trLogger.info("IMPS P2P Response::>>" + new Gson().toJson(response));
					return response;
				}
				if (request.getBenfMMID().trim().length() != 7) {
					response.setErrorCode(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_BEN_MMID);
					trLogger.info("IMPS P2P Response::>>" + new Gson().toJson(response));
					return response;
				}
				if (Double.parseDouble("" + request.getTransAmt()) == 0) {
					response.setErrorCode(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_AMOUNT);
					trLogger.info("IMPS P2P Response::>>" + new Gson().toJson(response));
					return response;
				}
				D009022 sourceAccount = DataUtils.getAccount(request.getRemitterAccNo());
				if (sourceAccount == null) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
					trLogger.info("IMPS P2P Response::>>" + new Gson().toJson(response));
					return response;
				}
				if ((request.getRemitterAccNo() != null && !request.getRemitterAccNo().trim().isEmpty())
						&& (request.getBenfAccNo() != null && !request.getBenfAccNo().trim().isEmpty())) {
					if (request.getRemitterAccNo().equalsIgnoreCase(request.getBenfAccNo().trim())) {
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage(MSGConstants.SAME_ACC_TRANSFER_NOT_ALLOWED);
						response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
						trLogger.info("IMPS P2P Response::>>" + new Gson().toJson(response));
						return response;
					}
				}
				TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount,
						String.valueOf(request.getTransAmt()), "D");
				if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
					P2PTransactionEntry.p2pTransactionEntry(request.getRemitterMMID(),
							request.getRemitterMobile().trim(), request.getBenfMobile(), request.getBenfMMID().trim(),
							Double.valueOf(request.getTransAmt()), 0, 0, "MBTR", 0, "99", res.getErrorMsg(), " ");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(res.getErrorMsg());
					trLogger.info("IMPS P2P Response::>>" + new Gson().toJson(response));
					return response;
				}
				response = TransactionServiceImpl.initiateIMPSTransaction(request.getRemitterAccNo().trim(),
						request.getBenfAccNo().trim(), String.valueOf(request.getTransAmt()), request.getNarration(),
						request.getRemitterMobile().trim(), request.getRemitterMMID().trim(),
						request.getBenfMobile().trim(), request.getBenfMMID().trim(), request.getTransType());
				logger.error("IMPS P2P Response::>>" + new Gson().toJson(response));
				trLogger.info("IMPS P2P Response::>>" + new Gson().toJson(response));
				return response;
			}
			if (request.getTransType().equalsIgnoreCase(MSGConstants.P2A_TRANSACTION)) {
				System.out.println("P2A Own Acct Transaction");
				
				
				

				if (null == request.getRemitterAccNo() || request.getRemitterAccNo().trim().length() < 10
						|| request.getRemitterAccNo().trim().length() > 20) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_REMITTER_ACC_NO);
					response.setErrorCode(MSGConstants.INVALID_REMITTER_ACC_NO);
					trLogger.info("IMPS P2A Response::>>" + new Gson().toJson(response));
					return response;
				}
				if (null == request.getBenfAccNo() || request.getBenfAccNo().trim().length() > 20) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage("Beneficiary Account Number Length Greater Than 20 digit Not Allowed");
					response.setErrorCode("Beneficiary Account Number Length Greater Than 20 digit Not Allowed");
					trLogger.info("IMPS P2A Response::>>" + new Gson().toJson(response));
					return response;
				}
				
				if (Double.parseDouble("" + request.getTransAmt()) == 0) {
					response.setErrorCode(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_AMOUNT);
					trLogger.info("IMPS P2A Response::>>" + new Gson().toJson(response));
					return response;
				}
				D009022 sourceAccount = DataUtils.getAccount(request.getRemitterAccNo());
				D009022 destAccount = DataUtils.getAccount(request.getBenfAccNo());
				if (sourceAccount.getCustNo()!=destAccount.getCustNo()) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage("Beneficiary And Destination Account Number does not belown to same Customer");
					response.setErrorCode("Beneficiary And Destination Account Number does not belown to same Customer");
					trLogger.info("IMPS P2A Response::>>" + new Gson().toJson(response));
					return response;
				}
				
				if (RtgsNeftHostToHostConstants.LOAN.getMessage()
						.contains(DataUtils
								.getProductMaster(sourceAccount.getId().getLbrCode() + "",
										sourceAccount.getId().getPrdAcctId().substring(0, 8).trim())
								.getModuleType() + "")) {
					response.setErrorCode(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.LOAN_TRN_NOT_ALLOWED);
					trLogger.info("IMPS P2A Response::>>" + new Gson().toJson(response));
					return response;
				}
				TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount,
						String.valueOf(request.getTransAmt()), "D");
				if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
					P2ATransaction.p2aTransactionEntry(request.getRemitterMMID().trim(),
							request.getRemitterMobile().trim(), request.getBenfAccNo().trim(),
							request.getBenfIFSC().trim(), Double.valueOf(request.getTransAmt()), 0, 0, "MBTR",
							sourceAccount.getId().getLbrCode(), "99", res.getErrorMsg(), " ");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(res.getErrorMsg());
					trLogger.info("IMPS P2A Response::>>" + new Gson().toJson(response));
					return response;
				}
				try {
					// System.out.println("hello");
					response = TransactionServiceImpl.initiateIMPSTransaction(request.getRemitterAccNo().trim(),
							request.getBenfAccNo().trim(), String.valueOf(request.getTransAmt()),
							request.getNarration().trim(), request.getRemitterMobile().trim(),
							request.getRemitterMMID().trim(), request.getBenfAccNo().trim(),
							ConfigurationLoader.getParameters(false).getProperty("OWN_IFSC"), request.getTransType());
					logger.error("IMPS P2A Response::>>" + new Gson().toJson(response));
					trLogger.info("IMPS P2A Response::>>" + new Gson().toJson(response));
					return response;
				} catch (Exception e) {
					// TODO: handle exception
					logger.error("ERROR:", e);
					e.printStackTrace();
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
					return response;
				}
			}
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_REQUEST);
			trLogger.info("IMPS P2A Response::>>" + new Gson().toJson(response));
			return response;

		} catch (Exception e) {
			logger.error("ERROR:", e);
			// TODO: handle exception
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
	}
	
	
	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/QRUPICreditTransaction")
	public IMPSTransactionResponse upiQRCreditTransaction(QRUPIRequest request) {

		IMPSTransactionResponse response = new IMPSTransactionResponse();
		if (request == null) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorCode(MSGConstants.INVALID_REQUEST);
			return response;
		}
		try (org.util.nanolog.Logger trLogger = org.util.nanolog.Logger.getLogger(LoggerType.BUFFERED, logWriter)) {
			trLogger.info("QR UPI Credit Request::>>" + request.toString());
			logger.error("QR UPI Credit Request ::>>" + new Gson().toJson(request));
			
			if (null == request.getAccountNo() || request.getAccountNo().trim().length() < 10) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_REMITTER_ACC_NO);
				response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				logger.error(MSGConstants.INVALID_REMITTER_ACC_NO);
				trLogger.info("QR UPI Credit  Response::>>" + new Gson().toJson(response));
				return response;
			}

			if (request.getAmt() == 0) {
				response.setErrorCode(ResponseCodes.INSUFFICIENT_FUNDS);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_AMOUNT);
				logger.error(MSGConstants.INVALID_AMOUNT);
				return response;
			}
			if (request.getAmt() > Double
					.valueOf(ConfigurationLoader.getParameters(false).getProperty("MAX_AMOUNT"))) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.EXCEED_DAILY_LIMIT);
				response.setRrnNo(request.getRrn());
				response.setErrorCode("M2");
				trLogger.info("QR UPI Credit  Response::>>" + new Gson().toJson(response));
				return response;
			}

			D009022 sourceAccount = DataUtils.getAccount(request.getAccountNo());
			if (sourceAccount == null) {
				response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
				logger.error(MSGConstants.ACCOUNT_NOT_FOUND);
				trLogger.info("QR UPI Credit  Response::>>" + new Gson().toJson(response));
				return response;
			}
			TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount,
					String.valueOf(request.getAmt()), "C");

			if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {

				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(res.getErrorMsg());

				response.setErrorCode(res.getRespCode());
				trLogger.info("QR UPI Credit Response::>>" + new Gson().toJson(response));
				return response;
			}

			Session session = HBUtil.getSessionFactory().openSession();
			Criteria criteria2 = session.createCriteria(UPITransaction.class);
			/*criteria2.add(Restrictions.eq("rrnNo", String.format("%1$-20s", request.getRrn().trim())));
			criteria2.add(Restrictions.eq("crAcctId", String.format("%1$-20s", request.getAccountNo())));
			criteria2.add(Restrictions.eq("txnid", String.format("%1$-20s", request.getTxnId())));
			*/
			criteria2.add(Restrictions.eq("rrnNo", request.getRrn().trim()));
			criteria2.add(Restrictions.eq("crAcctId", sourceAccount.getId().getPrdAcctId().trim()));
			criteria2.add(Restrictions.eq("txnid", request.getVpa()));
			List<UPITransaction> list2 = criteria2.list();
			session.close();
			session = null;
			criteria2 = null;
			if (!list2.isEmpty()) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.DUPLICATE_TRN);
				response.setRrnNo(request.getRrn());
				response.setErrorCode(ResponseCodes.ALREADY_DONE_THIS_TRANSACTION);
				trLogger.info("QR UPI Credit  Response::>>" + new Gson().toJson(response));
				return response;
			}
			response = TransactionServiceImpl.creditQRUPITransaction(request.getAccountNo(), request.getAmt(), 
					"UPI/" + request.getRrn() + "/" + DateUtil.getcurrentDateString()+"/"+request.getAccountNo()+"/"+request.getRemark(), request.getRrn(), request);
			response.setNickNameCredit(
					sourceAccount != null ? sourceAccount.getLongName().substring(0, 10) : "ARTHUR FUR");
			response.setNickNameDebit(
					sourceAccount != null ? sourceAccount.getLongName().substring(0, 10) : "ARTHUR FUR");
			logger.error("QR UPI Credit Response Received ::>>" + new Gson().toJson(response));
			trLogger.info("QR UPI Credit Response::>>" + new Gson().toJson(response));

			//trLogger.info("IMPS P2A Credit Response::>>" + new Gson().toJson(response));
			return response;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return response;

	}
	
	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/initiateBillPaymentV2")
	public IMPSTransactionResponse initiateBillPaymentV2(IMPSTransactionRequest request) {
		logger.error("<<<<<<<---------- impsTransactionOtherBank ------------->>>>>>>");
		logger.error("Request Received::>>" + request);
		IMPSTransactionResponse response = new IMPSTransactionResponse();
		if (request == null) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_REQUEST);
			return response;
		}
		
			
			if (null == request.getRemitterAccNo() || request.getRemitterAccNo().trim().length() != 15) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_REMITTER_ACC_NO);
				response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				return response;
			}
			if (Double.parseDouble("" + request.getTransAmt()) == 0) {
				response.setErrorCode(ResponseCodes.INSUFFICIENT_FUNDS);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_TRN_AMOUNT);
				return response;
			}
			if (request.getOperator() == null || request.getOperator().trim().isEmpty()) {
				response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_OPERATOR);
				return response;
			}
			D009022 sourceAccount = DataUtils.getAccount(request.getRemitterAccNo());
			if (sourceAccount == null) {
				response.setErrorCode(ResponseCodes.ACCOUNT_NOT_NORMAL);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
				return response;
			}
			TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount,
					String.valueOf(request.getTransAmt()), "D");
			if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
				BillPaymentTransactionEntry.transactionEntryBillPay(sourceAccount.getId().getLbrCode(),
						sourceAccount.getId().getPrdAcctId(), "MBTR", 0, 0, "",
						"BillDesk", request.getTransAmt(), ResponseCodes.INVALID_NBIN, res.getErrorMsg(),
						request.getRRNNo(), "D");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(res.getErrorMsg());
				response.setErrorCode(res.getRespCode());
				return response;
			}
			try {
				response = TransactionServiceImpl.initiateBillPayement(request.getRemitterAccNo(),
						request.getRemitterAccNo(), String.valueOf(request.getTransAmt()), request.getNarration(),
						"", "", "",
						"", MSGConstants.BILLPAY, request);
				logger.error("Response Received::>>" + response);
				return response;
			} catch (Exception e) {
				logger.error("ERROR:", e);
				e.printStackTrace();
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
				return response;
			}
	}

}
