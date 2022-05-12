package com.sil.dao;

import java.io.Serializable;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.sil.commonswitch.DataUtils;
import com.sil.commonswitch.OtherChannelServiceResponse;
import com.sil.commonswitch.P2ATransaction;
import com.sil.commonswitch.VoucherCommon;
import com.sil.constants.Code;
import com.sil.constants.MSGConstants;
import com.sil.constants.ResponseCodes;
import com.sil.constants.SwiftCoreConstants;
import com.sil.domain.IMPSTransactionResponse;
import com.sil.domain.RtgsNeftTransactionResponse;
import com.sil.domain.TransactionValidationResponse;
import com.sil.hbm.D001004;
import com.sil.hbm.D001004Id;
import com.sil.hbm.D001011;
import com.sil.hbm.D002011;
import com.sil.hbm.D009021;
import com.sil.hbm.D009022;
import com.sil.hbm.D009022Id;
import com.sil.hbm.D010054;
import com.sil.hbm.D350076;
import com.sil.hbm.D350077;
import com.sil.hbm.D350078;
import com.sil.hbm.D946020;
import com.sil.hbm.D946022;
import com.sil.hbm.D946120;
import com.sil.hbm.IBFundTransactionRequest;
import com.sil.hbm.RtgsCutoffParameter;
import com.sil.hbm.RtgsNeftRefNo;
import com.sil.hbm.RtgsNeftRefNoId;
import com.sil.prop.ConfigurationLoader;
import com.sil.service.TransactionServiceImpl;
import com.sil.util.DateUtil;
import com.sil.util.DateUtility;
import com.sil.util.HBUtil;
import com.sil.util.Props;

public class RTGSNEFTServiceImpl implements Serializable{
	private static final SimpleDateFormat sdf = new SimpleDateFormat("0yyyyMMdd");
	private static final long serialVersionUID = -7996894945815479073L;
	public static Logger  logger=Logger.getLogger(RTGSNEFTServiceImpl.class);
	public static RtgsNeftTransactionResponse ibFundTransferToOthrBank(
			IBFundTransactionRequest transactionRequest)
	{
		RtgsNeftTransactionResponse fundTxRes = new RtgsNeftTransactionResponse();
		
		Session  session=HBUtil.getSessionFactory().openSession();
		Transaction  t=session.beginTransaction();
		String mobileNo = " ";
		Boolean cutofftimeTF=false;
		Date futureDt=null;
		try {
			System.out.println("RTGSNEFT.ibFundTransferToOthrBank::>>"+transactionRequest.getToAccNo());
			logger.error("RTGSNEFT.ibFundTransferToOthrBank::>>"+transactionRequest.getFromAccNo());
			/*String[] strArr = transactionRequest.getFromBrAccNo().split("~");
			logger.error("strArr[0]::>>>"+strArr[0]);
			logger.error("strArr[1]::>>"+strArr[1]);
			Long drLbrCode = Long.valueOf(strArr[0].trim());*/
			
			String drAccountNo = DataUtils.get32DigitAcctNo(transactionRequest.getFromAccNo().trim());
			Long drLbrCode = Long.parseLong(transactionRequest.getFromAccNo().substring(0, 3));
			String drAccountNo15 = drAccountNo;
			Long acctLbrCode = 0l;
			acctLbrCode = drLbrCode;
			logger.error("drAccountNo::>>>"+drAccountNo);
			logger.error("drLbrCode::>>"+drLbrCode);
			D009021 d009021=DataUtils.getProductMaster(String.valueOf(drLbrCode),drAccountNo.substring(0,8).trim());
			System.out.println("d009021::>>>"+d009021);
			logger.error("d009021::>>"+d009021);
//			prodNo = product.get(prodNo);
			if (d009021 == null) {
				logger.error("Product not found.");
				session.close();
				session=null;
				t=null;
				fundTxRes.setResponseMessage(MSGConstants.ERROR);
				fundTxRes.setInnerErrorMessage(MSGConstants.PRODUCT_NOT_FOUND);
				fundTxRes.setSuccess(false);
				fundTxRes.setResponseCode("01");
				fundTxRes.setMessage("Transaction Failed");
				return fundTxRes;
			}
//			drAccountNo = SwiftCoreUtil.getAccountNumber(prodNo, drAccountNo, "");
			if (drLbrCode.longValue() != acctLbrCode.longValue()) {
				logger.error("Account Branch code and input branch code not match.");
				session.close();
				session=null;
				t=null;
				fundTxRes.setResponseMessage(MSGConstants.ERROR);
				fundTxRes.setInnerErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
				fundTxRes.setSuccess(false);
				fundTxRes.setResponseCode("01");
				fundTxRes.setMessage("Transaction Failed");
				return fundTxRes;
			}
			Date drOperationDate =getOpenDate(drLbrCode.intValue()); 
			System.out.println("Operation Date::>>"+drOperationDate);
			logger.error("Operation Date::>>>"+drOperationDate);
			if(drOperationDate==null)
			{
				session.close();
				session=null;
				t=null;
				fundTxRes.setResponseMessage(MSGConstants.ERROR);
				fundTxRes.setInnerErrorMessage(MSGConstants.OPERATION_DATE_NOT_FOUND);
				fundTxRes.setSuccess(false);
				fundTxRes.setResponseCode("01");
				fundTxRes.setMessage("Transaction Failed");
				return fundTxRes;
			}
			D001004 systemParameter = DataUtils.getSystemParameter(0, MSGConstants.TODAYSDATE);
			if(systemParameter==null)
			{
				session.close();
				session=null;
				t=null;
				fundTxRes.setResponseMessage(MSGConstants.ERROR);
				fundTxRes.setInnerErrorMessage(MSGConstants.PARAMETER_NOT_FOUND_946TODAYSDATE);
				fundTxRes.setSuccess(false);
				fundTxRes.setResponseCode("01");
				fundTxRes.setMessage("Transaction Failed");
				return fundTxRes;
			}
			String conTodaysDate = systemParameter == null ? "N": systemParameter.getValue();
			transactionRequest.setCutOffYN("N");
			D001004 parameter = DataUtils.getSystemParameter(
					0, MSGConstants.IBRN24BY7YN);
			String rn24BY7YN = parameter == null ? "N" : parameter.getValue();
			String crAccountNo = "";
			Date tranDate = null;
			//modified below code by manish as told by aniket.(tranDate was not getting reflected if rn24BY7YN is Y as property UseOperationDate was  used in else part.change on Date 08-012020. 
			
		/*	if (rn24BY7YN.trim().equalsIgnoreCase("Y")) {
				String constant = "";
				if (DateUtil.getEffDate(transactionRequest.getNextTrnxDate())
						.compareTo(DateUtil.convertDateFormat(new Date())) == 0) {
					constant = MSGConstants.RTGSIBCRACT;
				} else {
					constant = MSGConstants.RTGSIB24BY7CRACT;
				}

				D001004 system = DataUtils.getSystemParameter(0, constant);
				crAccountNo = system == null ? "00000000000000000000000000000000"
						: SwiftCoreUtil.getAccountNumber(system.getValue(),
								null, null);
				if (transactionRequest.getCutOffYN().trim().equalsIgnoreCase("N")) {
					tranDate = DateUtil.getEffDate(transactionRequest
							.getNextTrnxDate());
				}
			} else {
				D001004 system = DataUtils.getSystemParameter(0, MSGConstants.RTGSIBCRACT);
				crAccountNo = system == null ? "00000000000000000000000000000000"
						: SwiftCoreUtil.getAccountNumber(system.getValue(),
								null, null);
				//Modifed By Aniket Desai on 11th Dec, 2019 For NEFT 24/7  
				if ("N".equalsIgnoreCase(ConfigurationLoader.getParameters(false).getProperty("UseOperationDate"))) {
					tranDate = DateUtil.convertDateFormat(new Date());
				} else {
					tranDate = DateUtil.convertDateFormat(drOperationDate);
				}
			}*/
			
			D001004 system = DataUtils.getSystemParameter(0, MSGConstants.RTGSIBCRACT);
			crAccountNo = system == null ? "00000000000000000000000000000000"
					: SwiftCoreUtil.getAccountNumber(system.getValue(),
							null, null);
			//Modifed By Aniket Desai on 11th Dec, 2019 For NEFT 24/7  
			if ("N".equalsIgnoreCase(ConfigurationLoader.getParameters(false).getProperty("UseOperationDate"))) {
				tranDate = DateUtil.convertDateFormat(new Date());
			} else {
				tranDate = DateUtil.convertDateFormat(drOperationDate);
				System.out.println("");
				System.out.println("---------------------------UseOperationDate set to Y:  "+tranDate); //change by manish commit on 28-Jan-2020
			}
			if("D".equalsIgnoreCase(ConfigurationLoader.getParameters(false).getProperty("NEFTBankType"))) {
				
				char rtgsNEFT='N';
				
				Calendar cal = Calendar.getInstance();
				cal.setTime(tranDate); // don't forget this if date is arbitrary e.g. 01-01-2014
				//cal.add(Calendar.DATE);
			
				int day = cal.get(Calendar.DAY_OF_MONTH); 
			
				int month = cal.get(Calendar.MONTH+1); 
				int year = cal.get(Calendar.YEAR); 
				
				
				D001011 holidayMaster = DateUtil.getRtgsHolidayDetailsObject("MAH", rtgsNEFT, Long
						.valueOf(year), Long.valueOf(month), Long
						.valueOf(day));
				if (null == holidayMaster || "N".equalsIgnoreCase(holidayMaster.getNeftDayBegin()+"")) {
					fundTxRes.setMessage("Neft is closed for the Day!!!Try Again Tomorrow");
					fundTxRes.setInnerErrorMessage("NEFT Closed for the Day");
					fundTxRes.setResponseCode("01");
					fundTxRes.setResponseMessage("NEFT Closed for the Day");
					return fundTxRes;
					
				}
			}
			D946022 ifscMaster = DataUtils.getIFSCCodeDetail(transactionRequest.getToIfscCode());
			if (null == ifscMaster) {
				session.close();
				session=null;
				t=null;
				logger.error("Payee IFSC Code not Registered.");
				fundTxRes.setResponseMessage(MSGConstants.ERROR);
				fundTxRes.setInnerErrorMessage(MSGConstants.PAYEE_IFSC_CODE_NOT_REGISTERED);
				//fundTxRes.setOutput(new String[] { "E" });
				fundTxRes.setSuccess(false);
				fundTxRes.setResponseCode("01");
				fundTxRes.setMessage("Transaction Failed");
				return fundTxRes;
			}
			D001004 param = DataUtils.getSystemParameter(drLbrCode.intValue(), MSGConstants.PASSBOOKIFSCCD);
			String ordIFSCCode = param == null ? "" : param.getValue();
			D946022 ifscCodeMaster = DataUtils.getIFSCCodeDetail(ordIFSCCode);
			if (null == ifscCodeMaster) {
				logger.error("Branch IFSC Code not Registered.");
				session.close();
				session=null;
				t=null;
				fundTxRes.setResponseMessage(MSGConstants.ERROR);
				fundTxRes.setInnerErrorMessage(MSGConstants.IFS_CODE_NOT_REG);
				//fundTxRes.setOutput(new String[] { "E" });
				fundTxRes.setSuccess(false);
				fundTxRes.setResponseCode("01");
				fundTxRes.setMessage("Transaction Failed");
				return fundTxRes;
			}
			D350076 custDelivyChannelMaster = DataUtils
					.getCustDelivyChannelMasterObject(transactionRequest
							.getCustNo().trim());
			if (null == custDelivyChannelMaster) {
				logger.error("Invalid Customer Number.");
				session.close();
				session=null;
				t=null;
				fundTxRes.setResponseMessage(MSGConstants.ERROR);
				fundTxRes.setInnerErrorMessage(MSGConstants.INVALID_CUSTOMER);
				//fundTxRes.setOutput(new String[] { "E" });
				fundTxRes.setSuccess(false);
				fundTxRes.setResponseCode("01");
				fundTxRes.setMessage("Transaction Failed");
				return fundTxRes;
			}
			if (custDelivyChannelMaster.getDbtrAuthDone()!= 1
					|| custDelivyChannelMaster.getDbtrAuthNeeded() != 0) {
				logger.error("IB Customer Not Authorised.");
				session.close();
				session=null;
				t=null;
				fundTxRes.setResponseMessage(MSGConstants.ERROR);
				fundTxRes.setInnerErrorMessage(MSGConstants.IB_CUST_NOT_AUTH);
				//fundTxRes.setOutput(new String[] { "E" });
				fundTxRes.setSuccess(false);
				fundTxRes.setResponseCode("01");
				fundTxRes.setMessage("Transaction Failed");
				return fundTxRes;
			}
			if (custDelivyChannelMaster.getRtgsneftyn()=='N') {
				logger.error("IB Customer Not Registered for NEFT/RTGS.");
				session.close();
				session=null;
				t=null;
				fundTxRes.setResponseMessage(MSGConstants.ERROR);
				fundTxRes.setInnerErrorMessage(MSGConstants.CUST_NOT_REG_FOR_RTGS_NEFT);
				//fundTxRes.setOutput(new String[] { "E" });
				fundTxRes.setSuccess(false);
				fundTxRes.setResponseCode("01");
				fundTxRes.setMessage("Transaction Failed");
				return fundTxRes;
			}
			String ifscCode = "";
			String ifscCodeMast = "";
			Double transLimit = 0D;
			//Double rtgsNeftLimit = 0D;
			Double minAmt = 0D;
			Double maxAmt = 0D;
			if (transactionRequest.getRtgsNEFT().equalsIgnoreCase("NEFT")) {
				ifscCode = ifscMaster.getIfsccd();
				ifscCodeMast = ifscCodeMaster.getIfsccd();
				//rtgsNeftLimit = custDelivyChannelMaster.getNeftlimit();
				D001004 system0 = DataUtils.getSystemParameter(
						0, MSGConstants.NEFTIBMINAMT);
				minAmt = system0 == null ? 0D : Double
						.valueOf(system0.getValue());
				D001004 sysParam = DataUtils
						.getSystemParameter(0, MSGConstants.NEFTIBMAXAMT);
				maxAmt = sysParam == null ? 0D : Double.valueOf(sysParam
						.getValue());
				if (ifscMaster.getRtgsNeftCd() == Byte.valueOf("1")) {
					logger.error("Payee IFSC Code not Registered For NEFT.");
					session.close();
					session=null;
					t=null;
					fundTxRes.setResponseMessage(MSGConstants.ERROR);
					fundTxRes.setInnerErrorMessage(MSGConstants.PAYEE_IFS_CODE_NOT_REG_FOR_NEFT);
					//fundTxRes.setOutput(new String[] { "E" });
					fundTxRes.setSuccess(false);
					fundTxRes.setResponseCode("01");
					fundTxRes.setMessage("Transaction Failed");
					return fundTxRes;
				}

				if (ifscCodeMaster.getRtgsNeftCd() == Byte.valueOf("1")) {
					logger.error("Branch IFSC Code not Registered For NEFT.");
					session.close();
					session=null;
					t=null;
					fundTxRes.setResponseMessage(MSGConstants.ERROR);
					fundTxRes.setInnerErrorMessage(MSGConstants.BRANCH_IFS_CODE_NOT_REG_NEFT);
					//fundTxRes.setOutput(new String[] { "E" });
					fundTxRes.setSuccess(false);
					fundTxRes.setResponseCode("01");
					fundTxRes.setMessage("Transaction Failed");
					return fundTxRes;
				}
				if (custDelivyChannelMaster.getNefttransDate().compareTo(
						DateUtil.convertDateFormat(new Date())) >= 0) {
					transLimit = custDelivyChannelMaster.getNefttranAmt()
							+ transactionRequest.getTransAmnt();
				}
			}
			if (transactionRequest.getRtgsNEFT().equalsIgnoreCase("RTGS")) {
				ifscCode = ifscMaster.getIfsccd().substring(0, 4);
				ifscCodeMast = ifscCodeMaster.getIfsccd().substring(0, 4);
				//rtgsNeftLimit = custDelivyChannelMaster.getRtgslimit();
				D001004 system1 = DataUtils.getSystemParameter(               
						0, MSGConstants.RTGSIBMINAMT);
				minAmt = system1 == null ? 0D : Double
						.valueOf(system1.getValue());        //change by manish.commit on 28-Jan-2020
				D001004 sysParam = DataUtils.getSystemParameter(0, MSGConstants.RTGSIBMAXAMT);
				maxAmt = sysParam == null ? 0D : Double.valueOf(sysParam
						.getValue());
				if (ifscMaster.getRtgsNeftCd() == Byte.valueOf("2")) {
					logger.error("Payee IFSC Code not Registered For RTGS.");
					session.close();
					session=null;
					t=null;
					fundTxRes.setResponseMessage(MSGConstants.ERROR);
					fundTxRes.setInnerErrorMessage(MSGConstants.PAYEE_IFSC_CODE_NOT_REGISTERED_FOR_RTGS);
					//fundTxRes.setOutput(new String[] { "E" });
					fundTxRes.setSuccess(false);
					fundTxRes.setResponseCode("01");
					fundTxRes.setMessage("Transaction Failed");
					return fundTxRes;
				}
				if (ifscCodeMaster.getRtgsNeftCd() == Byte.valueOf("2")) {
					logger.error("Branch IFSC Code not Registered For RTGS.");
					session.close();
					session=null;
					t=null;
					fundTxRes.setResponseMessage(MSGConstants.ERROR);
					fundTxRes.setInnerErrorMessage(MSGConstants.BRANCH_IFS_CODE_NOT_REG_RTGS);
					//fundTxRes.setOutput(new String[] { "E" });
					fundTxRes.setSuccess(false);
					fundTxRes.setResponseCode("01");
					fundTxRes.setMessage("Transaction Failed");
					return fundTxRes;
				}
				if (custDelivyChannelMaster.getRtgstransDate().compareTo(
						DateUtil.convertDateFormat(new Date())) >= 0) {
					transLimit = custDelivyChannelMaster.getRtgstranAmt()
							+ transactionRequest.getTransAmnt();
				}
			}
			
			if (transactionRequest.getTransAmnt() < minAmt) {
				logger.error("Amount Should be greater than-" + minAmt);
				session.close();
				session=null;
				t=null;
				fundTxRes.setResponseMessage(MSGConstants.ERROR);
				fundTxRes.setInnerErrorMessage(MSGConstants.AMT_SHOULD_GREATER_THAN+minAmt);
				//fundTxRes.setOutput(new String[] { "E" });
				fundTxRes.setSuccess(false);
				fundTxRes.setResponseCode("01");
				fundTxRes.setMessage("Transaction Failed");
				return fundTxRes;
			}
			System.out.println("Minimum Amount::>>>"+minAmt);
			
			System.out.println("Maximum Amount::>>>"+maxAmt);
			System.out.println("transactionRequest.getTransAmnt()::>>"+transactionRequest.getTransAmnt());
			if (transactionRequest.getTransAmnt() > maxAmt) {
				logger.error("Amount Should be Less than-" + maxAmt);
				session.close();
				session=null;
				t=null;
				fundTxRes.setResponseMessage(MSGConstants.ERROR);
				fundTxRes.setInnerErrorMessage(MSGConstants.AMT_SHOULD_LESS_THAN+maxAmt);
				//fundTxRes.setOutput(new String[] { "E" });
				fundTxRes.setSuccess(false);
				fundTxRes.setResponseCode("01");
				fundTxRes.setMessage("Transaction Failed");
				return fundTxRes;
			}

			/*if (transLimit >= rtgsNeftLimit) {
			logger.error("Customer Exceeds daily transaction Limit.");
			fundTxRes = WSUtils.getWSReturnOutput(
					SwiftCoreConstants.ERROR,
					"Customer Exceeds daily transaction Limit.", "",
					transactionRequest.getCustNo(), drLbrCode);
			fundTxRes.setOutput(new String[] { "E" });
			return fundTxRes;
		}
*/
			if (ifscMaster.getRtgsNeftCd() == Byte.valueOf("9")) {
				logger.error("Payee IFSC Code deactivated for RTGS/NEFT.");
				session.close();
				session=null;
				t=null;
				fundTxRes.setResponseMessage(MSGConstants.ERROR);
				fundTxRes.setInnerErrorMessage(MSGConstants.IFS_CODE_DEACTIVIATED_RTGSNEFT); 
				//fundTxRes.setOutput(new String[] { "E" });
				fundTxRes.setSuccess(false);
				fundTxRes.setResponseCode("01");
				fundTxRes.setMessage("Transaction Failed");
				return fundTxRes;
			}

			if (ifscCode.trim().equalsIgnoreCase(ifscCodeMast)) {
				logger.error("Same branch transfer not allowed through "
						+ transactionRequest.getRtgsNEFT().toUpperCase() + ".");
				session.close();
				session=null;
				t=null;
				fundTxRes.setResponseMessage(MSGConstants.ERROR);
				fundTxRes.setInnerErrorMessage(MSGConstants.SAME_BRANCH_TRANSFER_RTGSNEFT+ transactionRequest.getRtgsNEFT().toUpperCase() + ".");
				//fundTxRes.setOutput(new String[] { "E" });
				fundTxRes.setSuccess(false);
				fundTxRes.setResponseCode("01");
				fundTxRes.setMessage("Transaction Failed");
				return fundTxRes;
			}

			/*D002011 ibMaster = DataUtils.getICustomerDetails(transactionRequest.getCustNo());
			if (null == ibMaster) {
				logger.error("Invalid IB Customer.");
				session.close();
				session=null;
				t=null;
				fundTxRes.setResponseMessage(MSGConstants.ERROR);
				fundTxRes.setInnerErrorMessage(MSGConstants.INVALID_IB_CUSTOMER);
				//fundTxRes.setOutput(new String[] { "E" });
				fundTxRes.setSuccess(false);
				fundTxRes.setResponseCode("01");
				fundTxRes.setMessage("Transaction Failed");
				return fundTxRes;
			}*/

			/** Commented. As discussed by Rupesh and Vinayak on 04 Nov 2016 */
			/*if (StringUtils.isBlank(transactionRequest.getPassOption())
				|| transactionRequest.getPassOption().equalsIgnoreCase(SwiftCoreConstants.YES)) {
			SARSecurityUtils inst = new SARSecurityUtils();
			String password = inst.doDecrypt(transactionRequest
					.getEncryptedOltpPwd(), transactionRequest.getCustNo());
			String encryptedPwd = OmniEncryptPassword.getEncryptedPwd(
					transactionRequest.getCustNo().toUpperCase(), password);
			if (!StringUtils.equals(custDelivyChannelMaster.getOltppin(),
					encryptedPwd)) {
				wsValidation.keepTrackOfBadLogins(ibMaster,
						SwiftCoreConstants.BADLILIMDAY);
				if (ibMaster.getStatus() == 2L) {
					logger.error("User is locked.");
					ibFundTxRes = WSUtils.getWSReturnOutput(
							SwiftCoreConstants.ERROR, "User is locked.", "",
							transactionRequest.getCustNo(), drLbrCode);
					ibFundTxRes.setOutput(new String[] { "L" });
				} else {
					logger.error("Invalid Dynamic Password.");
					ibFundTxRes = WSUtils.getWSReturnOutput(
							SwiftCoreConstants.ERROR,
							"Invalid Dynamic Password.", "", transactionRequest
									.getCustNo(), drLbrCode);
					ibFundTxRes.setOutput(new String[] { "E" });
				}

				return ibFundTxRes;
			}
		}

			*//** Commented. As discussed by Rupesh and Vinayak on 04 Nov 2016 *//*
			if (custDelivyChannelMaster.getOltppinflag() == 1L) {
			custDelivyChannelMaster.setOltppinflag(2L);
			custDelivyChannelMaster.setOltpchangedate(DateUtility
					.convertDateFormat(new Date()));
			custDelivyChannelMaster.setOltpchangetime(DateUtility
					.getFormattedTime(new Date()));

			 *//** Update InternetBankingMaster *//*
			ibMaster.setNoofbadloginsfndtr(0L);
			internetBankDAO.updateInternetBNK(ibMaster);

			  *//** Update CustDelivyChannelMaster *//*
			internetBankDAO
					.saveUpdateCustDelivyChannelMasterWS(custDelivyChannelMaster);
		}*/

			/*D350077 otherChannelFunds = DataUtils.fetchOtherChannelFundsObject(transactionRequest
							.getCustNo(), drAccountNo, drLbrCode);
			if (null == otherChannelFunds) {
				logger.error("Debit Account Not Registered.");
				logger.error("No entry of Debit Account in D350077");
				session.close();
				session=null;
				t=null;
				fundTxRes.setResponseMessage(MSGConstants.ERROR);
				fundTxRes.setInnerErrorMessage(MSGConstants.DEBIT_ACC_NOT_REG);
				//fundTxRes.setOutput(new String[] { "E" });
				fundTxRes.setSuccess(false);
				fundTxRes.setResponseCode("01");
				fundTxRes.setMessage("Transaction Failed");
				return fundTxRes;
			}*/

			D009022 drAccount = DataUtils.getAccountDetails(drLbrCode.intValue(),drAccountNo);
//			IMPSTransactionResponse accTrnxRes = wsValidation
//					.accountTrnxValidations(drAccount, SwiftCoreConstants.NO,
//							"Remitter");
			
			TransactionValidationResponse accTrnxRes=TransactionServiceImpl.validateAccount(drAccount, String.valueOf(transactionRequest.getTransAmnt()), "D");// 
			logger.error("accTrnxRes::>>>"+accTrnxRes);//
			if (accTrnxRes==null) {
				logger.error(MSGConstants.ACCOUNT_ADDRESS_NOT_FOUND);
				session.close();
				session=null;
				t=null;
				fundTxRes.setResponseMessage(MSGConstants.ERROR);
				fundTxRes.setInnerErrorMessage(MSGConstants.ACCOUNT_ADDRESS_NOT_FOUND);
				//fundTxRes.setOutput(new String[] { "E" });
				fundTxRes.setSuccess(false);
				fundTxRes.setResponseCode("01");
				fundTxRes.setMessage("Transaction Failed");
				return fundTxRes;
			}else if(!accTrnxRes.getResponse().equalsIgnoreCase(MSGConstants.SUCCESS)) {
				fundTxRes.setResponseMessage(MSGConstants.ERROR);
				fundTxRes.setInnerErrorMessage(accTrnxRes.getErrorMsg());
				//fundTxRes.setOutput(new String[] { "E" });
				fundTxRes.setSuccess(false);
				fundTxRes.setResponseCode("01");
				fundTxRes.setMessage("Transaction Failed");
				return fundTxRes;
			}

			if (drAccount.getCustNo() != Integer.valueOf(transactionRequest.getCustNo().trim())) {
				boolean isJointHolder = DataUtils.checkJointAccountHolder(
						drAccount.getCustNo(), Integer.valueOf(transactionRequest
								.getCustNo()), drAccount.getId().getLbrCode());
				if (!isJointHolder) {
					logger
					.error("This facility is not extended to your account. Kindly contact home branch for further details.");
					session.close();
					session=null;
					t=null;
					fundTxRes.setResponseMessage(MSGConstants.ERROR);
					fundTxRes.setInnerErrorMessage(MSGConstants.FASCILITY_NOT_AVAILABILITY);
					//fundTxRes.setOutput(new String[] { "E" });
					fundTxRes.setSuccess(false);
					fundTxRes.setResponseCode("01");
					fundTxRes.setMessage("Transaction Failed");
					return fundTxRes;
				}
			}

			String drProductNo = DataUtils.getProductNumber(drAccountNo);
			D009021 drProductMaster = DataUtils.getProductMaster(drLbrCode.toString(), drProductNo);
			//IMPSTransactionResponse drProdTrnxRes = DataUtils.productTrnxValidations(drProductMaster, "Debit");
//			if (!drProdTrnxRes.isValid()) {
//				logger.error(drProdTrnxRes.getErrorMessage());
//				session.close();
//				session=null;
//				t=null;
//				fundTxRes.setResponseMessage(MSGConstants.ERROR);
//				fundTxRes.setInnerErrorMessage(drProdTrnxRes.getErrorMessage());
//				//fundTxRes.setOutput(new String[] { "E" });
//				fundTxRes.setSuccess(false);
//				fundTxRes.setResponseCode("01");
//				fundTxRes.setMessage("Transaction Failed");
//				return fundTxRes;
//			}

			if (transactionRequest.getToAccNo().contains(" ")) {
				logger.error("Spaces not allowed in the Beneficiary Account No...");
				session.close();
				session=null;
				t=null;
				fundTxRes.setResponseMessage(MSGConstants.ERROR);
				fundTxRes.setInnerErrorMessage(MSGConstants.SPACES_NOT_ALLOWED_IN_ACCNO);
				//fundTxRes.setOutput(new String[] { "E" });
				fundTxRes.setSuccess(false);
				fundTxRes.setResponseCode("01");
				fundTxRes.setMessage("Transaction Failed");
				return fundTxRes;
			}

		/*	if (!StringUtils.isAlphanumeric(transactionRequest.getToAccNo())) {
				logger
				.error("Beneficiary Account No should be only A-Z and 0-9. No other characters are allowed..");
				fundTxRes = WSUtils
						.getWSReturnOutput(
								SwiftCoreConstants.ERROR,
								"Beneficiary Account No should be only A-Z and 0-9. No other characters are allowed..",
								"", transactionRequest.getCustNo(), drLbrCode);
				fundTxRes.setOutput(new String[] { "E" });
				return fundTxRes;
			}
*/
			String benDesc2 = " ";
			String benDesc3 = " ";
			String benDesc4 = " ";
			String benDesc5 = " ";
			
//		================Below code is commented as we are storing payee in middleware database.===============================
//		you must be add validation for payee.	
			
		/*	D350234Domain payeeDetails = internetBankDAO.getD350234DomainList(transactionRequest.getCustNo(), transactionRequest.getBenNickName());
			if (null == payeeDetails) {
				logger.error("Payee Not Registered.");
				fundTxRes = WSUtils.getWSReturnOutput(SwiftCoreConstants.ERROR, "Payee Not Registered.", "", transactionRequest.getCustNo(),
						drLbrCode);
				fundTxRes.setOutput(new String[] { "E" });
				return fundTxRes;
			}

			if (StringUtils.equalsIgnoreCase("2", payeeDetails.getStatus())) {
				logger.error("Please Confirm Payee Account.");
				fundTxRes = WSUtils.getWSReturnOutput(SwiftCoreConstants.ERROR, "Please Confirm Payee Account.", "",
						transactionRequest.getCustNo(), drLbrCode);
				fundTxRes.setOutput(new String[] { "E" });
				return fundTxRes;
			}*/

			RtgsCutoffParameter rtgsCutoffParameter = DataUtils.getRtgsCutoffParameter(session);
			String benfAcctId="";
			int benLbrCode=0;
			if (rtgsCutoffParameter != null) {
				benLbrCode = rtgsCutoffParameter.getTrsrybrcode();
				benfAcctId=SwiftCoreUtil.getAccountNumber(rtgsCutoffParameter.getPrdcd(),
						"0", null);
			}else {
				
			}
			
			//benDesc2 = "AMAR";/////payeeDetails.getFullname();      commented by manish
			//Long crLbrCode = Long.valueOf(ConfigurationLoader.getParameters(false).getProperty("rtgsSettingTrsryBrCode"));
			String emailId = " ";
			List<D350078> otherChannelDetailsList = DataUtils.getCustomerListD350078(transactionRequest.getCustNo());
			if (null != otherChannelDetailsList
					&& !otherChannelDetailsList.isEmpty()) {
				mobileNo = otherChannelDetailsList.get(0).getId().getMobileNo();
				emailId = otherChannelDetailsList.get(0).getEmailId();
			}
			logger.error("transactionRequest.getTrType()::>>"+transactionRequest.getTrType());
			if (transactionRequest.getTrType().trim().equalsIgnoreCase("PAYNOW")) {
				System.out.println("<<<<<<:::Transaction Type PAYNOW::>>>>>>>>>");
				logger.error("<<<<<<:::Transaction Type PAYNOW::>>>>>>>>>");
				Calendar calendarN = Calendar.getInstance();
				transactionRequest.setStartDate(transactionRequest.getNextTrnxDate());
				Date startDateParseN = DateUtil.getEffDate(transactionRequest.getStartDate().trim());
				calendarN.setTime(startDateParseN);
				Character charneftrtgs=transactionRequest.getRtgsNEFT().charAt(0);
				
			}
			D010054 accountAddressDetails = DataUtils.getAccountAddress(drLbrCode, drAccountNo, 1L);
			if (accountAddressDetails==null) {
				logger.error("Address Not availabe for Type 1");
				session.close();
				session=null;
				t=null;
				fundTxRes.setResponseMessage(MSGConstants.ERROR);
				fundTxRes.setInnerErrorMessage(MSGConstants.ADDRESS_NOT_FOUND);
				//fundTxRes.setOutput(new String[] { "E" });
				fundTxRes.setSuccess(false);
				fundTxRes.setResponseCode("01");
				fundTxRes.setMessage("Transaction Failed");
				return fundTxRes;
			}
			System.out.println("transactionRequest.getTrType()::>>>"+transactionRequest.getTrType());
			if ("PAYNOW".equalsIgnoreCase(transactionRequest
					.getTrType())) {
				logger.error("In PAYNOW::>>>transactionRequest.getTrType()::>>>"+transactionRequest.getTrType());
				/*IMPSTransactionResponse amountValid = DataUtils
						.AmountValidation(drAccount, transactionRequest
								.getTransAmnt(), drOperationDate,
								drProductMaster.getModuleType(),
								MSGConstants.NO);
				if (!amountValid.isValid()) {
					logger.error(amountValid.getErrorMessage());
					session.close();
					session=null;
					t=null;
					fundTxRes.setResponse(MSGConstants.ERROR);
					fundTxRes.setErrorMessage(amountValid.getErrorMessage());
					fundTxRes.setOutput(new String[] { "E" });
					return fundTxRes;
				}*/

				String crProductNo = DataUtils
						.getProductCode(crAccountNo);
				D009021 crProductMaster = DataUtils
						.getProductMaster(benLbrCode+"", crProductNo);
//				IMPSTransactionResponse crProdTrnxRes = DataUtils
//					.productTrnxValidations(crProductMaster, "Credit");
				if(null == crProductMaster){
					logger.error("Product not found.");
					session.close();
					session=null;
					t=null;
					fundTxRes.setResponseMessage(MSGConstants.ERROR);
					fundTxRes.setInnerErrorMessage(MSGConstants.PRODUCT_NOT_FOUND);
					//fundTxRes.setOutput(new String[] { "E" });
					fundTxRes.setSuccess(false);
					fundTxRes.setResponseCode("01");
					fundTxRes.setMessage("Transaction Failed");
					return fundTxRes;
				}

				String batch = Props.getBatchProperty("RTGSNEFT");
				
				String batchCodes[] = batch.split("~");
				IMPSTransactionResponse batchCodeTrnxRes = DataUtils
						.batchCodeValidations(drLbrCode.intValue(), drOperationDate,
								batchCodes[0].toString(),
								batchCodes[1].toString());
				if (!batchCodeTrnxRes.isValid()) {
					logger.error(batchCodeTrnxRes.getErrorMessage());
					session.getTransaction().rollback();
					session.close();
					session=null;
					t=null;
					fundTxRes.setResponseMessage(batchCodeTrnxRes.getErrorMessage());
					
					fundTxRes.setInnerErrorMessage("Invalid Batch Error for Branch");
					//fundTxRes.setOutput(new String[] { "E" });
					fundTxRes.setSuccess(false);
					fundTxRes.setResponseCode("01");
					fundTxRes.setMessage("Transaction Failed");
					return fundTxRes;
				}

				String batchCode = batchCodeTrnxRes.getResponse();
				String msgSType = "";
				String msgType = "0";
				Long lbrCode = 0L;
				String refNo = "";
				Long utrSeqNo= DataUtils.getNextUtrSeqNo(tranDate, session)+1L;
				if (transactionRequest.getRtgsNEFT().equalsIgnoreCase("RTGS")) {
					msgSType = "R41";
					msgType = "4";
					lbrCode = 0L;
				} else {
					msgSType = "N06";
					msgType = "3";
					lbrCode = drLbrCode;
				}

				/*DomainD946007 domainD946007 = internetBankDAO
						.getDomainD946007Details(lbrCode, "O", "REFNO",
								msgType, " ", tranDate);
				Long seqNo = 0L;
				if (null != domainD946007) {
					seqNo = domainD946007.getLastno() + 1L;
					domainD946007.setLastno(seqNo);
				} else {
					D001004 sysParam = DataUtils.getSystemParameter(0,
									MSGConstants.IBREFNOSTART);
					seqNo = sysParam == null ? 0L : Long.valueOf(sysParam
							.getValue());
					domainD946007 = wsValidation.prepareDomainD946007Object(
							lbrCode, "O", "REFNO", msgType, " ", tranDate, " ",
							seqNo);
				}

				txStatus = transactionManager.getTransaction(SwiftCoreUtil
						.getDefaultTxnDefinition());
				internetBankDAO.saveOrUpdateDomainD946007(domainD946007);*/
				
				//Commented by Waheed on 29/11/2016 as discussed with Rupesh sir.
//				refNo = StringUtils.leftPad(drLbrCode + "", 4, "0")
					 DateUtil.getJulianDateForCurrentDate(tranDate);
//					+ StringUtils.leftPad(seqNo + "", 7, "0");
				String rRefNo =MSGConstants.NOT_USED_PROCESS;
				Long msgTrfType = 0L;
				//Added by Muhammad Waheed on 29/11/2016 as discussed with Rupesh sir.
//				IRtgsService rtgsService = (IRtgsService)ServiceFinder.findBean("rtgsService");
				String utrnoN=" ";
				String julDays = DataUtils.checkLnoRecord(tranDate, msgSType);					
				String usrcd =drLbrCode.toString();			
				usrcd = SwiftCoreUtil.appendZeroPadding(usrcd, 3l);
				
				Long refSeq=getNextRefrenceNo(tranDate,drLbrCode,"2",session);
				
				String refSeqNo=generateReferenceNumber(tranDate,drLbrCode,refSeq,"5");
				refNo="0"+refSeqNo;
				//end waheed change.
				String narration = transactionRequest.getNarration();
				if (transactionRequest.getRtgsNEFT().equalsIgnoreCase("RTGS")) {
					msgTrfType = 1L;
					Long  tempUtrNo = Long.parseLong(julDays);
					utrnoN="SRCBH"+SwiftCoreUtil.appendZeroPadding(tempUtrNo.toString(),4l);
					narration="RTGS "+narration;
				} else {
					msgTrfType = 2L;
					rRefNo = "N" + refSeqNo;
					Long tempUtrNo =DataUtils.srnoForUtrnoForNeft(tranDate, msgSType);
					logger.error("Temp No:-"+tempUtrNo);
					utrnoN="SRCB"+SwiftCoreUtil.appendZeroPadding(tempUtrNo.toString(),5l);	
					if(narration==null || "".equalsIgnoreCase(narration))
						narration="NEFT/"+transactionRequest.getToAccNo().trim()+"/"+transactionRequest.getToIfscCode().trim()+rRefNo+"/";
					else
					    narration="NEFT/"+transactionRequest.getToAccNo().trim()+"/"+transactionRequest.getToIfscCode().trim()+"/"+rRefNo+"/"+narration;   //change by Manish.Commit on 28-Jan-2020
				}
				logger.error("Temp No:-"+narration);
				String accountNo8 = drAccountNo.substring(16,24);
				String accountNo15 = DataUtils.generateECSAccountNoFull(
						drLbrCode, drProductNo, accountNo8);
				
				
				
				D946020 rtgsMessages = DataUtils.prepareRtgsMessagesObject(drLbrCode, 'O', msgSType, tranDate, refNo, rRefNo, utrnoN, utrSeqNo,
						ordIFSCCode.trim(), transactionRequest.getToIfscCode(), tranDate, ConfigurationLoader.getParameters(false).getProperty("countryCurrency"),
						transactionRequest.getTransAmnt(), 4L, 20L, drAccountNo, drAccount.getLongName(), accountAddressDetails.getAddr1(),
						accountAddressDetails.getAddr2(), accountAddressDetails.getAddr3(), accountNo15, "5561", transactionRequest.getToAccNo(), 
						transactionRequest.getBenNickName(), transactionRequest.getBenAdd1(), transactionRequest.getBenAdd2(), 
						transactionRequest.getBenMobileNo(), "1", ' ', MSGConstants.NOT_USED_PROCESS, 0L, 
						0L, 0L, msgTrfType, 0L, 99l, "0", transactionRequest.getChrgAmnt()==null ? 0D:transactionRequest.getChrgAmnt(), 1L, mobileNo, MSGConstants.NOT_USED_PROCESS);
				session.save(rtgsMessages);
				logger.error(rtgsMessages.toString());
				//session.flush();
				D946120 rtgsMessageListDatFile = DataUtils.prepareRtgsMessageListDatFile(drLbrCode, 'O', msgSType, tranDate, refNo,
						utrSeqNo, "7495", 0l, ConfigurationLoader.getParameters(false).getProperty("rtgsModeFast"), MSGConstants.NOT_USED_PROCESS,
						MSGConstants.NOT_USED_PROCESS, MSGConstants.NOT_USED_PROCESS, MSGConstants.NOT_USED_PROCESS, 0d, 0d, 0l,
						MSGConstants.NOT_USED_PROCESS, MSGConstants.NOT_USED_PROCESS, MSGConstants.NOT_USED_PROCESS);
				session.save(rtgsMessageListDatFile);
				logger.error(rtgsMessageListDatFile.toString());
				//session.flush();               //by Manish.commit on 28-Jan-2020
				/** Voucher Process Start */
//				rtgsMessages.setBatch(batchCode);
//				rtgsMessages.setProductNo(drProductNo);
//				rtgsMessages.setAccountNo(accountNo8);
//				rtgsMessages.getUser().getUserDetails().setUsrcode1("WEB");
				//IMPSTransactionResponse voucherTrnxRes = rtgsOutwardVoucher(rtgsMessages);
				
			
				
				
				D001004 parameter1 = DataUtils.getSystemParameter(benLbrCode, MSGConstants.LOD_CODE);
				Date crOperationDate = DateUtil.getDateFromStringNew(parameter1.getValue().trim().substring(1));
				String abbBatchCode = Props.getBatchProperty("ABB");
				
				String crBatchCodes[] = abbBatchCode.split("~");
				if (drLbrCode.compareTo((long) benLbrCode) != 0) {
					IMPSTransactionResponse abbBatchCodeTrnxRes = DataUtils.batchCodeValidations(benLbrCode, crOperationDate,
							crBatchCodes[0].toString(),crBatchCodes[1].toString());
					if (!abbBatchCodeTrnxRes.isValid()) {
						logger.error(abbBatchCodeTrnxRes.getErrorMessage());
						session.getTransaction().rollback();
						session.close();
						session=null;
						//t=null;
						fundTxRes.setResponseMessage(batchCodeTrnxRes.getErrorMessage());
						
						fundTxRes.setInnerErrorMessage("Invalid Batch Error for Branch");
						//fundTxRes.setOutput(new String[] { "E" });
						fundTxRes.setSuccess(false);
						fundTxRes.setResponseCode("01");
						fundTxRes.setMessage("Transaction Failed");
						return fundTxRes;
					
					}

//					abbBatchCode = abbBatchCodeTrnxRes.getResponse();
				}
				

				
			
				
				HashMap<String, String> rtgsMessagesVoucher =VoucherCommon.rtgsNeftVouchers(rtgsMessages.getId().getObrCode(),drAccountNo,benLbrCode,benfAcctId
						,"RTGSNEFT", narration,rtgsMessages.getAmount(), rtgsMessages.getId().getRefNo(),session, rtgsMessages); //DataUtils.saveRtgsNeftVoucherWS(rtgsMessages, abbBatchCode,session);
				
				/** Voucher Process End */
				Double amount = 0.0D;
				if(rtgsMessagesVoucher.get("result").equalsIgnoreCase("success")) {
					String referenceNo = "";
					if (transactionRequest.getRtgsNEFT().equalsIgnoreCase("NEFT")) {
						amount = Double.valueOf(transactionRequest
								.getTransAmnt());
						if (custDelivyChannelMaster.getNefttransDate().compareTo(
								DateUtil.convertDateFormat(new Date())) >= 0) {
							amount += custDelivyChannelMaster.getNefttranAmt();
						}
				
						custDelivyChannelMaster.setNefttranAmt(amount);
						custDelivyChannelMaster.setNefttransDate(DateUtil
								.convertDateFormat(new Date()));
						referenceNo = rtgsMessages.getRrefNo();
					} else if (transactionRequest.getRtgsNEFT().equalsIgnoreCase("RTGS")) {
						amount = Double.valueOf(transactionRequest
								.getTransAmnt());
						if (custDelivyChannelMaster.getRtgstransDate().compareTo(
								DateUtil.convertDateFormat(new Date())) >= 0) {
							amount += custDelivyChannelMaster.getRtgstranAmt();
						}
				
						custDelivyChannelMaster.setRtgstranAmt(amount);
						custDelivyChannelMaster.setRtgstransDate(DateUtil
								.convertDateFormat(new Date()));
						referenceNo = rtgsMessages.getNutrno();
					}
				
					/** Update CustDelivyChannelMaster */
					
					//session.save(custDelivyChannelMaster);
				//	.saveUpdateCustDelivyChannelMasterWS(custDelivyChannelMaster);
					int lbrcode = rtgsMessages.getId().getObrCode();
					String[] skipPrds = ConfigurationLoader.getParameters(false).getProperty("NEFT_SKIP_CHG_PRD").split(",");
					ArrayList<String> skipList = new ArrayList<String>(Arrays.asList(skipPrds));
					
					if ("Y".equalsIgnoreCase(
							ConfigurationLoader.getParameters(false).getProperty("NEFT_CHG_P2A_YN").trim())
							&& !skipList.contains(drAccountNo.substring(0, 8).trim())) {
						HashMap<String, String> result2 = null;
						D009022Id remId = new D009022Id();
						remId.setLbrCode(lbrcode);
						remId.setPrdAcctId(drAccountNo);
						D009022 remAcct = session.get(D009022.class, remId);
												
							Double chgAmount = Double.valueOf(
									ConfigurationLoader.getParameters(false).getProperty("NEFT_P2A_CHG_AMOUNT").trim());
							if("S".equalsIgnoreCase(ConfigurationLoader.getParameters(false).getProperty("NEFT_SER_OR_GST_CHG"))) {
								result2 = VoucherCommon.serviceChargeVoucherEntry(lbrcode, referenceNo, session, drAccountNo,
										"NEFT Charges/" + referenceNo, chgAmount, amount);
							}else {
								result2 = VoucherCommon.gstVoucherEntry(lbrcode, referenceNo, session, drAccountNo,
										"NEFT Charges/" + referenceNo, chgAmount, amount);
							}
							if(result2.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
								t.commit();
								
							}else {
								
							}
						
					}else {
						t.commit();
					}
					
					logger.error("Transaction Successful. Ref No- " + referenceNo);
					session.close();
					session=null;
					t=null;
					fundTxRes.setResponseMessage(MSGConstants.SUCCESS);
					//fundTxRes.setMessage(MSGConstants.SUCCESS_MSG);
					//fundTxRes.setOutput(new String[] { referenceNo });
					fundTxRes.setRRN(referenceNo);
					fundTxRes.setSuccess(true);
					fundTxRes.setResponseCode("00");
					//fundTxRes.setMessage("Transaction Successful. Ref No- "+ referenceNo);
					NumberFormat numberAmount = new DecimalFormat("0.00");//
					drAccountNo15 = "XXXXX"+drAccountNo15.substring((drAccountNo15.length() - 6), drAccountNo15.length());
					fundTxRes.setMessage("Your a/c no. "+drAccountNo15+" is debited for Rs. "+numberAmount.format(transactionRequest.getTransAmnt())+" on "+DateUtil.getStringDate_ddMMyy(new Date())+" at "+DateUtil.getStringTime_hhmmss_a(new Date())+" and NEFT transaction is successful. (UTR no "+referenceNo+").");
				
				}else  {
					if(rtgsMessagesVoucher.get("result").equalsIgnoreCase("postDateUnmatched")) {
						logger.error("Post date of two branches in case of abb doesnt match");  //Change By Manish.Commit On 28-Jan-2020
						fundTxRes.setInnerErrorMessage("Technical Error");
					    fundTxRes.setResponseMessage(MSGConstants.ERROR);
					//fundTxRes.setOutput(new String[] { "E" });
					fundTxRes.setSuccess(false);
					fundTxRes.setResponseCode("01");
					fundTxRes.setMessage("Transaction Failed");
					return fundTxRes;
					}
						//logger.error("Post date of two branches in case of abb doesnt match");
						fundTxRes.setInnerErrorMessage("Technical Error");
					    fundTxRes.setResponseMessage(MSGConstants.ERROR);
					//fundTxRes.setOutput(new String[] { "E" });
					fundTxRes.setSuccess(false);
					fundTxRes.setResponseCode("01");
					fundTxRes.setMessage("Transaction Failed");
					return fundTxRes;
				}
			} else {
				fundTxRes.setResponseMessage(MSGConstants.ERROR);
				fundTxRes.setInnerErrorMessage("Only PAYNOW transaction allowed");
				//fundTxRes.setOutput(new String[] { "E" });
				fundTxRes.setSuccess(false);
				fundTxRes.setResponseCode("01");
				fundTxRes.setMessage("Transaction Failed");
				return fundTxRes;
			}
				/*else if ("scheduleSI".equalsIgnoreCase(transactionRequest.getTrType())
					||"SCHEDULED".equalsIgnoreCase(transactionRequest.getTrType())) {
				logger.error("In SCHEDULED::>>>transactionRequest.getTrType()::>>>"+transactionRequest.getTrType());
				String msgSType = "";
				char rtgsNEFT = ' ';
				if (transactionRequest.getRtgsNEFT().equalsIgnoreCase("RTGS")) {
					msgSType = "R41";
					rtgsNEFT = 'R';
				} else {
					msgSType = "N06";
					rtgsNEFT = 'N';
				}

				Long maxSiNo = DateUtil.getSINOSEQ();
				if (maxSiNo == null || maxSiNo == 0) {
					maxSiNo = 1l;
				}

			/*	RNStandingInstructMaster instructMaster = internetBankDAO
						.getRNStandingInstructMasterDetails(drLbrCode, maxSiNo);
				if (null != instructMaster) {
					logger.error("Record Already Exist.");
					return WSUtils.getWSReturnOutput(SwiftCoreConstants.ERROR,
							"Record Already Exist.", "", transactionRequest
							.getCustNo(), drLbrCode);
				}*/

				/*Date startDateParse = DateUtil.getDateParse_ddMMMyyyy(transactionRequest
								.getStartDate());
				Date startDate = DateUtil.convertDateFormat(startDateParse);
				Date endDateParse = DateUtil.getDateParse_ddMMMyyyy(transactionRequest.getStartDate());
				Date endDate = DateUtil.convertDateFormat(endDateParse);
				System.out.println("endDate::>>>"+endDate);
				String sccessMsg = "You Have Successfully Created standing instrucution no :: "
						+ maxSiNo;
				if ("scheduled".equalsIgnoreCase(transactionRequest.getTrType())) {
					if(cutofftimeTF){
						Calendar endDateCal = DateUtil.addingDateTime(endDateParse, 1, "Days");
						endDate = DateUtil.convertDateFormat(endDateCal.getTime());
						sccessMsg = "Dear Customer,Your transaction will be processed today and the amount will be remitted on:: "+DateUtil.getString(futureDt);
					}else{
						Calendar endDateCal = DateUtil.addingDateTime(endDateParse, 1, "Days");
						endDate = DateUtil.convertDateFormat(endDateCal.getTime());
						sccessMsg = "You have successfully created future payment ref. no :: "
								+ maxSiNo;
					}
				}

				Calendar calendarInstance = Calendar.getInstance();
				calendarInstance.setTime(startDate);
				List<D001011> holidayMasterList = DateUtil.getRtgsHolidayDetailsList("MAH", rtgsNEFT, Long
						.valueOf(calendarInstance.get(Calendar.YEAR)), Long.valueOf(calendarInstance.get(Calendar.MONTH) + 1), Long
						.valueOf(calendarInstance.get(Calendar.DATE)));
				if (null != holidayMasterList && !holidayMasterList.isEmpty() && holidayMasterList.get(0).getDayType()=='H') {

					logger.error("Can not transafer amount due to holiday. Please change date.");
					fundTxRes.setResponse(MSGConstants.ERROR);
					fundTxRes.setErrorMessage(MSGConstants.FUND_NOT_ALLOWED_ON_HOLIDAY);
					
					D001011 cutoffParameter2=DataUtils.getRtgsHolidays("MAH", rtgsNEFT, calendarInstance.get(Calendar.YEAR),calendarInstance.get(Calendar.MONTH),calendarInstance.get(Calendar.DATE),"D");
					if(cutoffParameter2!=null){
						cutofftimeTF=true;
						futureDt=DateUtil.formatDate(Long.valueOf(cutoffParameter2.getId().getDay()).intValue(), Long.valueOf(cutoffParameter2.getId().getCalMonth()).intValue(),Long.valueOf(cutoffParameter2.getId().getCalYear()).intValue());
						//futureDt=DateUtility.getDateParse_ddMMMyyyy(DateUtility.getString(futureDt));
						SimpleDateFormat sdfN = new SimpleDateFormat("dd-MMM-yyyy");
						String dateformatN= sdfN.format(futureDt);
						transactionRequest.setStartDate(dateformatN);
						transactionRequest.setEndDate(dateformatN);
						sccessMsg = "Dear Customer, Your Transaction will be processed today and the amount will be remitted on :: "+DateUtil.getString(futureDt);
					}
				}

//				txStatus = transactionManager.getTransaction(SwiftCoreUtil.getDefaultTxnDefinition());
				Calendar cal = Calendar.getInstance();
				cal.setTime(startDate);
				cal.add(Calendar.MONTH, -1);
				Date lastExecDate = DateUtil
						.convertDateFormat(cal.getTime());

				Calendar calendar = Calendar.getInstance();
				calendar.setTime(startDate);
				calendar.add(Calendar.DATE, 1);
				Date siClosedDate = DateUtil.convertDateFormat(calendar
						.getTime());*/
				/*RNStandingInstructMaster standingInstructMaster = wsValidation
					.prepareRNStandingInstructMasterObject(drLbrCode,
							transactionRequest.getFreqType(),
							transactionRequest.getFreqInDays(),
							transactionRequest.getNoOfPayments(), msgSType,
							startDate, startDate, endDate, drAccountNo,
							crLbrCode, crAccountNo, transactionRequest
									.getTransAmnt(), "N", "N", "N", " ",
							0d, transactionRequest.getChrgAmnt(),
							transactionRequest.getBenIfscCode(),
							transactionRequest.getToAccNo(), mobileNo,
							emailId, ordIFSCCode, 4l, 1l, 0d, 0d, 1l,
							maxSiNo, lastExecDate, siClosedDate, benDesc2,
							benDesc3, benDesc4, benDesc5);
			internetBankDAO
					.saveRNStandingInstructMaster(standingInstructMaster);

				NeftMsgFutureDate neftMsgFutureDate = wsValidation.prepareNeftMsgFutureDateObject(drLbrCode, msgSType, startDate, drAccountNo,
						transactionRequest.getTransAmnt(), transactionRequest.getBenIfscCode(), transactionRequest.getToAccNo(), mobileNo, emailId,
						ordIFSCCode, 1l, maxSiNo, benDesc2, transactionRequest.getBenNickName(), benDesc4, benDesc5, accountAddressDetails, drAccount.getLongname(), 
						SwiftCoreProperties.getProperty("neftRtgsBatchCode"), "IM");
				if(cutofftimeTF){
					neftMsgFutureDate.getId().setStartdt(futureDt);
				}

				rtgsService.saveFuturdateNeft(neftMsgFutureDate);*/
//				transactionManager.commit(txStatus);
				/*logger.error(sccessMsg);
				session.close();
				session=null;
				t=null;
				fundTxRes.setResponse(MSGConstants.SUCCESS);
				fundTxRes.setErrorMessage(MSGConstants.SUCCESS_MSG);
				fundTxRes.setOutput(new String[] { Long.toString(maxSiNo) });
				fundTxRes.setMessage(sccessMsg);
			}*/
		} catch (Exception e) {
			e.printStackTrace();
			t.rollback();
			session.close();
			session=null;
			t=null;
			fundTxRes.setResponseMessage(MSGConstants.ERROR);
			fundTxRes.setInnerErrorMessage("Transaction not completed successfully please try again");
			fundTxRes.setSuccess(false);
			fundTxRes.setResponseCode("01");
			fundTxRes.setMessage(MSGConstants.WEB_SERVICE_ERROR);
			logger.error("WSTransactionServiceImpl.ibFundTransferToOthrBank().Exception",e);
		}

		//fundTxRes.setMobileNo(mobileNo);
		return fundTxRes;
	}
	public static Date getOpenDate(int brCode)
	{
		try(Session session = HBUtil.getSessionFactory().openSession()) {
			D001004Id id = new D001004Id();
			id.setCode("LASTOPENDATE");
			id.setLbrCode(brCode);			
			D001004 d04 = session.get(D001004.class, id);
			session.close();
			if(d04 != null) {
				return sdf.parse(d04.getValue().trim());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String generateReferenceNumber(Date msgDate,Long lbrCode,Long utrSeqNo, String number) throws ParseException {
		String message = null;
		message = String.format("%03d", lbrCode)
				+ String.format("%05d",
						DateUtility.getJulianDay(DateUtility
						.getDateFormat(msgDate)))+String.format("%02d", Long.valueOf(ConfigurationLoader.getParameters(false).getProperty("BankCd")))
				+ String.format("%0" + number + "d", utrSeqNo);
		return message;
	}
	
	public static Long getNextRefrenceNo(Date msgDate, Long lbrCode, String type, Session session) throws ParseException{
		RtgsNeftRefNo rtgsNeftRef = new RtgsNeftRefNo();
		RtgsNeftRefNoId id = new RtgsNeftRefNoId();
		Long setOrScrollNo = null;
		Date date = DateUtility.getFormattedDate(msgDate);//getDateOrTimeFromDate(msgDate, "dd-MM-yyyy");
		String hql = "select lastNo from RtgsNeftRefNo where id.LBrCode =:lbrCode and id.catType='REFNO' and id.lnodate=:lnodate and id.code1=:type";
		List<Integer> seqNo =(List<Integer>) session.createQuery(hql).setParameter("lbrCode", lbrCode.intValue())
				.setParameter("lnodate", date).setParameter("type", type).getResultList();
		
		if (seqNo != null && seqNo.size() > 0)
		{
			String hqlUpdate = "update RtgsNeftRefNo set  lastNo=:lastNo where id.LBrCode =:lbrCode and id.catType='REFNO' and id.lnodate=:lnodate and id.code1=:type";
			Integer result = session.createQuery(hqlUpdate).setParameter("lastNo", seqNo.get(0)+1)
					.setParameter("lbrCode", lbrCode.intValue()).setParameter("lnodate", date).setParameter("type", type).executeUpdate();
			return seqNo.get(0)+1L;
		}else{
			rtgsNeftRef.setLastNo(1);
			
			id.setCat("D");
			id.setCatType("REFNO");
			id.setCode1(type);
			id.setLBrCode(lbrCode.intValue());
			id.setCode2(" ");
			id.setLnodate(date);
			rtgsNeftRef.setId(id);
			rtgsNeftRef.setDescr(" ");
			
			session.save(rtgsNeftRef);
			
			return 1L;
		}
		
	}
	
	
}
