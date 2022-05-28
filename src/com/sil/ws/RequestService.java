package com.sil.ws;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.google.gson.Gson;
import com.sil.commonswitch.AccountInfo;
import com.sil.commonswitch.AccountStatement;
import com.sil.commonswitch.AcctStatDetailsStatusResponse;
import com.sil.commonswitch.ChequeBookRequest;
import com.sil.commonswitch.DataUtils;
import com.sil.commonswitch.OtherChannelServiceResponse;
import com.sil.constants.MSGConstants;
import com.sil.constants.ResponseCodes;
import com.sil.dao.AccountMasterServiceImpl;
import com.sil.dao.CustomerMasterServiceImpl;
import com.sil.dao.LookUpServiceImpl;
import com.sil.domain.AccountDetails;
import com.sil.domain.AccountOpenRequest;
import com.sil.domain.AccountOpenResponse;
import com.sil.domain.AccountResponse;
import com.sil.domain.BalanceResponse;
import com.sil.domain.BranchDetailsResponse;
import com.sil.domain.CustDetailsResponse;
import com.sil.domain.CustNewMobileDetails;
import com.sil.domain.CustomerDepositePrdCdList;
import com.sil.domain.CustomerDetails;
import com.sil.domain.CustomerInfo;
import com.sil.domain.CustomerPhysicalCardOnboardingreq;
import com.sil.domain.DepositeAcctOpenRequest;
import com.sil.domain.DepositeAcctOpenResponseNew;
import com.sil.domain.DepositeParameters;
import com.sil.domain.DepositePrdCdList;
import com.sil.domain.FetchDetailedStmtRespose;
import com.sil.domain.FetchNoOfStmtResponse;
import com.sil.domain.IMPSFetchDepositeAccountResponse;
import com.sil.domain.IMPSFetchDepositeInterestRateResponse;
import com.sil.domain.IMPSFetchDepositeReceiptResponse;
import com.sil.domain.IMPSFetchLoanAccountDetailsResponse;
import com.sil.domain.IMPSTransactionResponse;
import com.sil.domain.ImpsTransactionReport;
import com.sil.domain.LookUpDetails;
import com.sil.domain.MiniStatementResponse;
import com.sil.domain.PigmeAccountsResponse;
import com.sil.domain.RDAcctDetails;
import com.sil.domain.RDParameters;
import com.sil.domain.StopChequeResponse;
import com.sil.domain.TransactionValidationResponse;
import com.sil.domain.YouCloudWalletResponse;
import com.sil.hbm.D009011;
import com.sil.hbm.D009021;
import com.sil.hbm.D009021Id;
import com.sil.hbm.D009022;
import com.sil.hbm.D009022Id;
import com.sil.hbm.D009040;
import com.sil.hbm.D350023;
import com.sil.hbm.D350076;
import com.sil.hbm.D350078;
import com.sil.hbm.D946022;
import com.sil.prop.ConfigurationLoader;
import com.sil.service.RequestServiceImpl;
import com.sil.service.TransactionServiceImpl;
import com.sil.util.DateUtil;
import com.sil.util.HBUtil;

@Path("/request")
public class RequestService {

	static final DateFormat CRAZY_FORMAT = new SimpleDateFormat("");

	public static Logger logger = Logger.getLogger(RequestService.class);

	/*
	 * @POST
	 * 
	 * @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	 * 
	 * @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	 * 
	 * @Path("/getLookUp") public LookUpDetails
	 * getLookUp(@QueryParam(value="param1") String flag) { LookUpDetails
	 * response=new LookUpDetails();
	 * 
	 * try { return new LookUpServiceImpl().getAllLookUpValues(flag); } catch
	 * (Exception e) { // TODO: handle exception e.printStackTrace();
	 * logger.error(e); response.setResponse(MSGConstants.ERROR);
	 * response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR); return response; } }
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/getLookUp")
	public LookUpDetails getLookUp(@QueryParam(value = "param1") String flag,
			@QueryParam(value = "param2") String agentAccNo) {
		LookUpDetails response = new LookUpDetails();
		if (agentAccNo == null || agentAccNo.trim().length() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.INVALID_AGENT_ACC_NO);
			return response;
		}
		try {
			return new LookUpServiceImpl().getAllLookUpValues(flag, agentAccNo);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
	}

	/*
	 * @POST
	 * 
	 * @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	 * 
	 * @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	 * 
	 * @Path("/getLookUp") public LookUpDetails getLookUp() { return new
	 * LookUpServiceImpl().getAllLookUpValues(""); }
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/searchCustomer")
	public CustDetailsResponse searchCustomer(@QueryParam(value = "param1") String custNo,
			@QueryParam(value = "param2") String mobNo, @QueryParam(value = "param3") String panNo,
			@QueryParam(value = "param4") String name, @QueryParam(value = "param5") String aadhar) {
		logger.error("search Customer ::>>Recieved Cust No::>>" + custNo + " Mobile No::>>" + mobNo + " PAN No::>>"
				+ panNo + " name::>>" + name);
		CustDetailsResponse customerDetails = null;
		if (null != mobNo && mobNo.length() == 10)
			mobNo = "91" + mobNo;
		customerDetails = CustomerMasterServiceImpl.getCustomerDetails(custNo, mobNo, panNo, name, aadhar);
		logger.error("Search customer Response::>>" + customerDetails);
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
		logger.error("openAccount parameters::>>" + request);
		AccountOpenResponse response = new AccountOpenResponse();
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
		if (null != request.getIsPigMeAcc()) {
			if (request.getIsPigMeAcc().trim().equalsIgnoreCase("Y")) {
				if (Double.valueOf(request.getDepositAmount()) % 10 != 0) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_AMOUNT);
					return response;
				}
			}
		}
		AccountOpenResponse accountOpenResponse = new AccountOpenResponse();
		try {
			accountOpenResponse = AccountMasterServiceImpl.insertValues(
					request.getBrcode() == null ? "" : request.getBrcode(),
					request.getProdCode() == null ? "" : request.getProdCode(),
					request.getAccType() == null ? "" : Integer.valueOf(request.getAccType()) + "",
					request.getModeOfOperation() == null ? "" : Integer.valueOf(request.getModeOfOperation()) + "",
					request.getNameTitle() == null ? "" : request.getNameTitle(),
					request.getName() == null ? "" : request.getName(),
					request.getAdd1() == null ? "" : request.getAdd1(),
					request.getAdd2() == null ? "" : request.getAdd2(),
					request.getAdd3() == null ? "" : request.getAdd3(),
					request.getPanCode() == null ? "" : Integer.valueOf(request.getPanCode()) + "",
					request.getPanCardNo() == null ? "" : request.getPanCardNo(),
					request.getAreaCode() == null ? "" : Integer.valueOf(request.getAreaCode()) + "",
					request.getCityCode() == null ? "" : request.getCityCode(),
					request.getPinCode() == null ? "" : request.getPinCode(),
					request.getMobNo() == null ? "" : request.getMobNo(),
					request.getEmail() == null ? "" : request.getEmail(),
					request.getIsNew() == null ? "" : request.getIsNew(),
					request.getCustNo() == null ? "" : request.getCustNo(),
					request.getWelcomeKitFlag() == null ? "" : request.getWelcomeKitFlag(),
					request.getAccNo() == null ? "" : request.getAccNo(),
					request.getAccType() == null ? "" : Integer.valueOf(request.getAccType()) + "", request);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("ERROR:", e);
			accountOpenResponse.setResponse(MSGConstants.ERROR);
			accountOpenResponse.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
		}
		logger.error("openAccount Response::>>" + accountOpenResponse);
		return accountOpenResponse;
	}

	@POST
	@Path("/accountValidate")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static CustomerDetails accountValidate(@QueryParam(value = "param1") String accNo15Digit) {
		CustomerDetails customerDetails = new CustomerDetails();
		System.out.println("Recived Accno::>>" + accNo15Digit);
		if (null == accNo15Digit || accNo15Digit.trim().length() < 15) {
			customerDetails.setResponse("ERROR");
			customerDetails.setErrorMsg("INVALID PARAMETERS.");
			return customerDetails;
		}
		try {
			return AccountMasterServiceImpl.validateAccNo(accNo15Digit.trim());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
			customerDetails.setResponse(MSGConstants.ERROR);
			customerDetails.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			return customerDetails;
		}

	}

	@POST
	@Path("/chequeBookRequest")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static OtherChannelServiceResponse chequeBookRequest(@QueryParam(value = "param1") String acctNo15Digit,
			@QueryParam(value = "param2") String noOfCheqBook, @QueryParam(value = "param3") String addrType,
			@QueryParam(value = "param4") String delevaryType, @QueryParam(value = "param5") String addYN,
			@QueryParam(value = "param6") String delevaryday, @QueryParam(value = "param7") String custNo,
			@QueryParam(value = "param8") String channel) {
		OtherChannelServiceResponse customerDetails = new OtherChannelServiceResponse();
		if (null == channel || channel == "") {
			customerDetails.setResponse(MSGConstants.ERROR);
			customerDetails.setErrorMessage(MSGConstants.INVALID_CHANNEL);
			return customerDetails;
		}
		if (null == acctNo15Digit || acctNo15Digit.trim().length() != 15) {
			customerDetails.setResponse(MSGConstants.ERROR);
			customerDetails.setErrorMessage(MSGConstants.INVALID_ACCOUNT_NO);
			return customerDetails;
		}
		if (null == noOfCheqBook || noOfCheqBook.trim().length() < 1) {
			customerDetails.setResponse(MSGConstants.ERROR);
			customerDetails.setErrorMessage(MSGConstants.INVALID_PARAMETERS);
			return customerDetails;
		}
		if (null == addrType || addrType.trim().length() < 1) {
			customerDetails.setResponse(MSGConstants.ERROR);
			customerDetails.setErrorMessage(MSGConstants.INVALID_PARAMETERS);
			return customerDetails;
		}
		/*
		 * if(null==custNo || custNo.trim().length()<1) {
		 * customerDetails.setResponse(MSGConstants.ERROR);
		 * customerDetails.setErrorMessage(MSGConstants.INVALID_PARAMETERS); return
		 * customerDetails; }
		 */
		if (null == delevaryday || delevaryday.trim().length() < 1) {
			customerDetails.setResponse(MSGConstants.ERROR);
			customerDetails.setErrorMessage(MSGConstants.INVALID_PARAMETERS);
			return customerDetails;
		}
		if (null == acctNo15Digit || acctNo15Digit.trim().length() < 1) {
			customerDetails.setResponse(MSGConstants.ERROR);
			customerDetails.setErrorMessage(MSGConstants.INVALID_PARAMETERS);
			return customerDetails;
		}
		if (null == delevaryType || delevaryType.trim().length() < 1) {
			customerDetails.setResponse(MSGConstants.ERROR);
			customerDetails.setErrorMessage(MSGConstants.INVALID_PARAMETERS);
			return customerDetails;
		}
		OtherChannelServiceResponse res = null;
		try {
			res = ChequeBookRequest.chequeBookRequest(acctNo15Digit, noOfCheqBook, addrType, delevaryType, addYN,
					custNo, delevaryday, channel);
			return res;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
			customerDetails.setResponse(MSGConstants.ERROR);
			customerDetails.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			return customerDetails;
		}
	}

	@POST
	@Path("/miniStatement")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static MiniStatementResponse miniStatement(@QueryParam(value = "param1") String brCode,
			@QueryParam(value = "param2") String accNo, @QueryParam(value = "param3") String custId) {
		MiniStatementResponse stmt = new MiniStatementResponse();
		if (accNo == null || accNo.trim().isEmpty()) {
			stmt.setResponse(MSGConstants.ERROR);
			stmt.setErrorMSg(MSGConstants.INVALID_ACCOUNT_NO);
			return stmt;
		}
		if (brCode == null || brCode.trim().isEmpty()) {
			stmt.setResponse(MSGConstants.ERROR);
			stmt.setErrorMSg(MSGConstants.INVALID_BR_CODE);
			return stmt;
		}
		D009022 sourceAccount = DataUtils.getAccountDetails(Integer.valueOf(brCode.trim()), accNo);
		if (sourceAccount == null) {
			stmt.setResponse(MSGConstants.ERROR);
			stmt.setErrorMSg(MSGConstants.ACCOUNT_NOT_FOUND);
			return stmt;
		}
		TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount, "0", "AS");
		if (res != null) {
			if (res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
				stmt.setResponse(MSGConstants.ERROR);
				stmt.setErrorMSg(res.getErrorMsg());
				return stmt;
			}
		}
		ArrayList<MiniStatementResponse> ministatements = new ArrayList<MiniStatementResponse>();
		List<D009040> list = RequestServiceImpl.getMinistatement(Integer.valueOf(brCode), accNo, 10);
		if (list != null && !list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				MiniStatementResponse response = new MiniStatementResponse();
				response.setTransAmount("" + list.get(i).getFcyTrnAmt());
				response.setTransDate("" + list.get(i).getPostDate());
				response.setTransDRCR("" + list.get(i).getDrCr());
				response.setTransNarrative("" + list.get(i).getParticulars().trim());
				ministatements.add(response);
			}
			stmt.setMiniStmts(ministatements);
			stmt.setResponse(MSGConstants.SUCCESS);
			stmt.setErrorMSg(MSGConstants.SUCCESS_MSG);
			ministatements = null;
		} else {
			stmt.setResponse(MSGConstants.ERROR);
			stmt.setErrorMSg(MSGConstants.NO_TRANSACTION_FOUND);
			return stmt;
		}
		try {

			stmt.setResponse(MSGConstants.SUCCESS);
			stmt.setErrorMSg(MSGConstants.SUCCESS_MSG);
			return stmt;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
			stmt.setErrorMSg(MSGConstants.WEB_SERVICE_ERROR);
			stmt.setResponse(MSGConstants.ERROR);
			return stmt;
		}
	}

	@POST
	@Path("/fetchAccounts")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static AccountResponse fetchAccounts(@QueryParam(value = "param1") String custNo) throws Exception {
		System.out.println("Fetch Accounts" + custNo);
		AccountResponse response = new AccountResponse();
		try {
			if (custNo == null || custNo.trim().length() < 1) {
				response.setReponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_CUSTOMER);
				return response;
			}
			ArrayList<AccountDetails> accounts = new ArrayList<>();
			List<D009022> list = DataUtils.getValidAccList(custNo);

			if (list == null || list.size() < 1) {
				response.setReponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.RECORD_NOT_FOUND);
				return response;
			}
			for (int i = 0; i < list.size(); i++) {
				AccountDetails details = new AccountDetails();
				details.setAccCode(list.get(i).getId().getPrdAcctId());
				details.setBrachCode(String.valueOf(list.get(i).getId().getLbrCode()));
				details.setFormattedAccount(list.get(i).getId().getLbrCode() + "/"
						+ list.get(i).getId().getPrdAcctId().substring(0, 8).trim() + "/"
						+ Long.valueOf(list.get(i).getId().getPrdAcctId().substring(16, 24)));
				details.setName(list.get(i).getLongName().trim());
				accounts.add(details);
			}
			response.setAccountDetails(accounts);
			Session session = HBUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(D009011.class);
			criteria.add(Restrictions.eq("custNo", Integer.valueOf(custNo.trim())));
			List<D009011> listCust = criteria.list();
			if (listCust == null || listCust.isEmpty()) {
				session.close();
				session = null;
				response.setReponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.CUSTOMER_NOT_FOUND);
				return response;
			}
			Criteria criteria2 = session.createCriteria(D350078.class);
			criteria2.add(Restrictions.eq("id.custNo", custNo.trim()));
			List<D350078> listD78 = criteria2.list();
			if (listD78 == null || listD78.isEmpty()) {
				session.close();//
				session = null;
				response.setReponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.CUSTOMER_NOT_FOUND);
				return response;
			}
			session.close();
			session = null;
			response.setAdd1("" + listCust.get(0).getAdd1());
			response.setAdd2("" + listCust.get(0).getAdd2());
			response.setAdd3("" + listCust.get(0).getAdd3());
			response.setEmail("" + listD78.get(0).getEmailId());
			response.setMobNo("" + listD78.get(0).getId().getMobileNo().trim());
			response.setReponse(MSGConstants.SUCCESS);
			response.setErrorMsg(MSGConstants.SUCCESS_MSG);
			return response;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
			response.setReponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
	}

	@POST
	@Path("/checkIBCredentials")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static CustomerDetails checkIBCredentials(@QueryParam(value = "param1") String custNo,
			@QueryParam(value = "param2") String password) {
		CustomerDetails customerDetails = new CustomerDetails();
		System.out.println("Recieved Customer No::>>" + custNo);
		System.out.println("Recieved Password::>>" + password);
		if (custNo == null || password == null || custNo.trim().equalsIgnoreCase("")
				|| password.trim().equalsIgnoreCase("")) {
			customerDetails.setResponse("ERROR");
			customerDetails.setErrorMsg("Enter Valid Password");
			return customerDetails;
		}
		try {
			return RequestServiceImpl.validateIBCustomer(custNo.trim(), password.trim());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
			customerDetails.setResponse(MSGConstants.ERROR);
			customerDetails.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			return customerDetails;
		}
	}

	@POST
	@Path("/validateCard")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static CustomerDetails validateCard(@QueryParam(value = "param1") String custNo,
			@QueryParam(value = "param2") String cardNo) {
		CustomerDetails customerDetails = new CustomerDetails();
		System.out.println("Recieved Customer No::>>" + custNo);
		System.out.println("Recieved Password::>>" + cardNo);
		if (custNo == null || custNo.trim().length() < 1) {
			customerDetails.setResponse(MSGConstants.ERROR);
			customerDetails.setErrorMsg(MSGConstants.INVALID_CUSTOMER);
			return customerDetails;
		}
		if (cardNo == null || cardNo.trim().length() != 16) {
			customerDetails.setResponse(MSGConstants.ERROR);
			customerDetails.setErrorMsg(MSGConstants.INVALID_CARD_NO);
			return customerDetails;
		}
		if (Long.valueOf(cardNo.trim()) == 0) {
			customerDetails.setResponse(MSGConstants.ERROR);
			customerDetails.setErrorMsg(MSGConstants.INVALID_CARD_NO);
			return customerDetails;
		}
		try {
			return RequestServiceImpl.validateCardAndCustId(custNo.trim(), cardNo.trim());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			customerDetails.setResponse(MSGConstants.ERROR);
			customerDetails.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			return customerDetails;
		}
	}

	@POST
	@Path("/balanceEnq")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static BalanceResponse balanceEnq(@QueryParam(value = "param1") String lbrCode,
			@QueryParam(value = "param2") String accno) {
		try {
			return RequestServiceImpl.fetchBalance(lbrCode, accno);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new BalanceResponse();
		}
	}

	@POST
	@Path("/mPOSBalanceEnq")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static BalanceResponse mPOSBalanceEnq(@QueryParam(value = "param1") String accno) {
		try {
			D009022 d009022 = DataUtils.getAccount(accno);
			return RequestServiceImpl.fetchBalance("" + d009022.getId().getLbrCode(), d009022.getId().getPrdAcctId());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			return new BalanceResponse();
		}

	}

	// below service is implemeneted for MPOS (SILKAYPAY)
	@POST
	@Path("/balanceEnqNew")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static BalanceResponse balanceEnqNew(@QueryParam(value = "param1") String accNo15digit) {
		D009022 d009022 = null;
		System.out.println("accNo15digit:>>" + accNo15digit);
		if (accNo15digit == null || accNo15digit.trim().length() == 15)
			d009022 = DataUtils.getAccount(accNo15digit);
		try {
			return RequestServiceImpl.fetchBalance("" + d009022.getId().getLbrCode(), d009022.getId().getPrdAcctId());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
			BalanceResponse response = new BalanceResponse();
			response.setErrorMsg("Account Not Found");
			response.setResponse("Failed");
			return response;
		}
	}

	@POST
	@Path("/accountStatement")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static CustomerDetails accountStatement(@QueryParam(value = "param1") String accNo,
			@QueryParam(value = "param2") String fromDate, @QueryParam(value = "param3") String toDate,
			@QueryParam(value = "param4") String custNo, @QueryParam(value = "param5") String MMID,
			@QueryParam(value = "param6") String channel) {
		CustomerDetails details = new CustomerDetails();
		if (channel == null || channel.trim().length() < 1) {
			details.setResponse(MSGConstants.ERROR);
			details.setErrorMsg(MSGConstants.INVALID_CHANNEL);
			return details;
		}
		if (accNo == null || accNo.length() != 15) {
			details.setResponse(MSGConstants.ERROR);
			details.setErrorMsg(MSGConstants.INVALID_ACCOUNT_NO);
			return details;
		}
		if (fromDate == null || fromDate.length() != 8) {
			details.setResponse(MSGConstants.ERROR);
			details.setErrorMsg(MSGConstants.INVALID_FROM_DATE);
			return details;
		}
		if (toDate == null || toDate.length() != 8) {
			details.setResponse(MSGConstants.ERROR);
			details.setErrorMsg(MSGConstants.INVALID_TO_DATE);
			return details;
		}
		if (DateUtil.compareFromDateToDate(toDate, DateUtil.getcurrentDateyyyyMMdd())) {
			details.setResponse(MSGConstants.ERROR);
			details.setErrorMsg(MSGConstants.TO_DATE_TODATE_NOT_SAME);
			return details;
		}
		if (DateUtil.compareFromDateToDate(fromDate, DateUtil.getcurrentDateyyyyMMdd())) {
			details.setResponse(MSGConstants.ERROR);
			details.setErrorMsg(MSGConstants.FROM_DATE_TODATE_NOT_SAME);
			return details;
		}
		if (channel.equalsIgnoreCase(MSGConstants.IMPS_CHANNEL)) {
			if (MMID == null || MMID.trim().length() != 7) {
				details.setResponse(MSGConstants.ERROR);
				details.setErrorMsg(MSGConstants.INVALID_MMID);
				return details;
			}
		}
		try {
			return AccountStatement.accountStmtRequest(accNo, fromDate, toDate, custNo, MMID, channel);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
			details.setResponse(MSGConstants.ERROR);
			details.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			return details;
		}
	}

	@POST
	@Path("/loanStatement")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static CustomerDetails loanStatement(@QueryParam(value = "param1") String accNo,
			@QueryParam(value = "param2") String fromDate, @QueryParam(value = "param3") String toDate,
			@QueryParam(value = "param4") String custNo, @QueryParam(value = "param5") String MMID,
			@QueryParam(value = "param6") String channel) {
		CustomerDetails details = new CustomerDetails();
		if (channel == null || channel.trim().length() < 1) {
			details.setResponse(MSGConstants.ERROR);
			details.setErrorMsg(MSGConstants.INVALID_CHANNEL);
			return details;
		}
		
		if (accNo != null && accNo.length() == 16) {
			if(accNo.substring(0, 4).equalsIgnoreCase("9999")) {
				accNo = accNo.substring(1);
			}else {
				details.setResponse(MSGConstants.ERROR);
				details.setErrorMsg(MSGConstants.INVALID_ACCOUNT_NO);
				return details;
			}
		}else if (accNo == null || accNo.length() != 15) {
			details.setResponse(MSGConstants.ERROR);
			details.setErrorMsg(MSGConstants.INVALID_ACCOUNT_NO);
			return details;
		}
		if (fromDate == null || fromDate.length() != 8) {
			details.setResponse(MSGConstants.ERROR);
			details.setErrorMsg(MSGConstants.INVALID_FROM_DATE);
			return details;
		}
		if (toDate == null || toDate.length() != 8) {
			details.setResponse(MSGConstants.ERROR);
			details.setErrorMsg(MSGConstants.INVALID_TO_DATE);
			return details;
		}
		if (DateUtil.compareFromDateToDate(fromDate, DateUtil.getcurrentDateyyyyMMdd())) {
			details.setResponse(MSGConstants.ERROR);
			details.setErrorMsg(MSGConstants.FROM_DATE_TODATE_NOT_SAME);
			return details;
		}
		if (DateUtil.compareFromDateToDate(toDate, DateUtil.getcurrentDateyyyyMMdd())) {
			details.setResponse(MSGConstants.ERROR);
			details.setErrorMsg(MSGConstants.TO_DATE_TODATE_NOT_SAME);
			return details;
		}
		if (channel.equalsIgnoreCase(MSGConstants.IMPS_CHANNEL)) {
			if (MMID == null || MMID.trim().length() != 7) {
				details.setResponse(MSGConstants.ERROR);
				details.setErrorMsg(MSGConstants.INVALID_MMID);
				return details;
			}
		}
		try {
			return AccountStatement.loanStmtRequest(accNo, fromDate, toDate, custNo, MMID, channel);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			details.setResponse(MSGConstants.ERROR);
			details.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			return details;
		}

	}

	@POST
	@Path("/validateCustomer")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static CustomerDetails validateCustomer(@QueryParam(value = "param1") String custNo) {
		CustomerDetails response = new CustomerDetails();
		try {
			return RequestServiceImpl.validCustomer(custNo);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
	}

	@POST
	@Path("/fetchMMID")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static AccountResponse fecthMMID(@QueryParam(value = "param1") String custNo) {
		System.out.println("Recieved Customer No::>>>" + custNo);
		AccountResponse response = new AccountResponse();
		if (null == custNo || custNo.trim().length() < 1) {
			response.setReponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.INVALID_PARAMETERS);
			return response;
		}
		try {
			return RequestServiceImpl.fetchAllMMID(custNo);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			response.setReponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}

	}

	@POST
	@Path("/generateMMID")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static OtherChannelServiceResponse genMMID(@QueryParam(value = "param1") String custNo,
			@QueryParam(value = "param2") String accNo) {
		OtherChannelServiceResponse response = new OtherChannelServiceResponse();
		if (null == custNo || null == accNo) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_PARAMETERS);
			return response;
		}
		if (custNo.trim().length() < 1 || accNo.length() != 15) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_CUSTNO_OR_ACCOUNT_NO);
			return response;
		}
		try {
			return RequestServiceImpl.genMMID(custNo, accNo);
		} catch (Exception e) {
			// TODO: handle exception
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
		CustomerDetails resp = new CustomerDetails();
		if (mobNo == null || mobNo.trim().length() < 10) {
			resp.setResponse(MSGConstants.ERROR);
			resp.setErrorMsg(MSGConstants.INVALID_PARAMETERS);
			return resp;
		}
		try {
			return RequestServiceImpl.getCustomerDetails(mobNo.trim());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			resp.setResponse(MSGConstants.ERROR);
			resp.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			return resp;
		}

	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/fetchPinOffset")
	public OtherChannelServiceResponse fetchPinOffset(@QueryParam(value = "param1") String custId,
			@QueryParam(value = "param2") String pinType) {
		OtherChannelServiceResponse response = new OtherChannelServiceResponse();
		if (custId == null || custId.length() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_CUSTOMER);
			return response;
		}
		if (pinType == null || pinType.length() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_PIN_TYPE);
			return response;
		}
		if (!new ArrayList<String>(Arrays.asList("L", "T")).contains(pinType)) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_PIN_TYPE);
			return response;
		}
		try {
			return RequestServiceImpl.fetchPinOffset(custId, pinType);
		} catch (Exception e) {
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
	@Path("/pinOffsetStore")
	public OtherChannelServiceResponse pinOffsetStore(@QueryParam(value = "param1") String custId,
			@QueryParam(value = "param2") String pinType, @QueryParam(value = "param3") String offset,
			@QueryParam(value = "param4") String pin, @QueryParam(value = "param5") String channel) {
		System.out.println("channel::>>" + channel);
		OtherChannelServiceResponse response = new OtherChannelServiceResponse();
		if (channel == null || channel.trim().length() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_CHANNEL);
			return response;
		}
		if (!new ArrayList<String>(Arrays.asList(MSGConstants.IMPS_CHANNEL, MSGConstants.OTHER_CHANNEL))
				.contains(channel.trim())) {
			System.out.println("Invalid chaneel");
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_CHANNEL);
			return response;
		}
		if (custId == null || custId.length() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_CUSTOMER);
			return response;
		}
		if (pinType == null || pinType.length() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_PIN_TYPE);
			return response;
		}
		if ((!new ArrayList<String>(Arrays.asList("L", "T")).contains(pinType))) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_PIN_TYPE);
			return response;
		}
		if (offset == null || offset.length() != 12) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_PIN_OFFSET);
			return response;
		}
		if (pin == null || pin.length() != 4) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_PIN);
			return response;
		}
		try {
			return RequestServiceImpl.storePinOffset(custId, pinType, offset, pin, channel.trim());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
	}

	@POST
	@Path("/cancelMMID")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static OtherChannelServiceResponse cancelMMID(@QueryParam(value = "param1") String custNo,
			@QueryParam(value = "param2") String mmid) {
		OtherChannelServiceResponse response = new OtherChannelServiceResponse();
		System.out.println("Recived Accno::>>" + custNo);
		System.out.println("Recived MMID::>>" + mmid);
		if (null == custNo || custNo.trim().length() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_PARAMETERS);
			return response;
		}
		if (null == mmid || mmid.trim().length() != 7) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_PARAMETERS);
			return response;
		}
		try {
			return RequestServiceImpl.cancelMMID(custNo, mmid);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}

	}

	@POST
	@Path("/impsLogin")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static OtherChannelServiceResponse impsLogin(@QueryParam(value = "param1") String custNo,
			@QueryParam(value = "param2") String pass) {
		OtherChannelServiceResponse response = new OtherChannelServiceResponse();
		System.out.println("Recived Accno::>>" + custNo);
		System.out.println("Recived Pass::>>" + pass);
		if (null == custNo || custNo.trim().length() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_PARAMETERS);
			return response;
		}
		if (null == pass || pass.trim().length() != 4) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_PARAMETERS);
			return response;
		}
		try {
			return RequestServiceImpl.impsLogin(custNo, pass);
		} catch (Exception e) {
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
	@Path("/stopCheque")
	public StopChequeResponse stopCheque(@QueryParam(value = "param1") String custNo,
			@QueryParam(value = "param2") String mmid, @QueryParam(value = "param3") String chequeNo,
			@QueryParam(value = "param4") String remark, @QueryParam(value = "param5") String accNo15digit,
			@QueryParam(value = "param6") String channel) {
		StopChequeResponse response = new StopChequeResponse();

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
			return RequestServiceImpl.stopCheque(custNo, mmid, chequeNo, remark, accNo15digit, channel);
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
			return RequestServiceImpl.stopCheque(custNo, mmid, chequeNo, remark, accNo15digit, channel);
		}
		response.setResponse(MSGConstants.ERROR);
		response.setErrorMessage(MSGConstants.INVALID_PARAMETERS);
		return response;
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/getChequeStatus")
	public OtherChannelServiceResponse getChequeStatus(@QueryParam(value = "param1") String custNo,
			@QueryParam(value = "param2") String mmid, @QueryParam(value = "param3") String chequeNo,
			@QueryParam(value = "param4") String accNo15digit, @QueryParam(value = "param5") String channel) {
		System.out.println("In Get Cheque Status");
		OtherChannelServiceResponse response = new OtherChannelServiceResponse();
		if (channel == null || channel.trim().length() < 1
				|| !new ArrayList<String>(Arrays.asList(MSGConstants.IMPS_CHANNEL, MSGConstants.OTHER_CHANNEL))
						.contains(channel)) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INAVLID_CHANNEL);
			return response;
		}
		if (null == custNo || custNo.trim().length() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_CUSTOMER);
			return response;
		}
		if (chequeNo == null || chequeNo.trim().length() != 12 || Long.valueOf(chequeNo.trim()) == 0L) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_CHEQUE_NO);
			return response;
		}
		if (channel.trim().equalsIgnoreCase(MSGConstants.IMPS_CHANNEL)) {
			if (null == custNo || custNo.trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_CUSTOMER);
				return response;
			}
			if (null == mmid || mmid.trim().length() != 7) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_CUST_NO_MMID);
				return response;
			}
		} else if (channel.equalsIgnoreCase(MSGConstants.OTHER_CHANNEL)) {
			if (accNo15digit == null || Long.valueOf(accNo15digit) == 0l || accNo15digit.trim().length() != 15) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_ACCOUNT_NO);
				return response;
			}
		}
		try {
			return RequestServiceImpl.getChequeStatus(custNo, mmid, chequeNo, accNo15digit, channel);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			return response;
		}

	}

	@POST
	@Path("/fetchLoanAccounts")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static AccountResponse fetchLoanAccounts(@QueryParam(value = "param1") String custNo) {
		System.out.println("Fetch Loan Accounts ::>>" + custNo);
		AccountResponse response = new AccountResponse();
		if (custNo == null || custNo.trim().length() < 1 || Long.valueOf(custNo.trim()) == 0l) {
			System.out.println("Invalid customer");
			response.setReponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.INVALID_CUSTOMER);
			return response;
		}
		ArrayList<AccountDetails> accounts = new ArrayList<>();
		List<D009022> list = DataUtils.getLoanAccList(custNo);
		System.out.println("list::>>>" + list);
		if (list == null || list.isEmpty()) {
			response.setReponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.RECORD_NOT_FOUND);
			return response;
		}
		System.out.println("list:>>>" + list);
		System.out.println("list.size()::>>" + list.size());

		for (int i = 0; i < list.size(); i++) {
			AccountDetails details = new AccountDetails();
			details.setAccCode(list.get(i).getId().getPrdAcctId());
			String branch = String.valueOf(list.get(i).getId().getLbrCode());
			/*if (branch.length() > 3)
				branch = branch.substring(0, 3);*/

			details.setBrachCode(branch);
			accounts.add(details);
		}
		response.setReponse(MSGConstants.SUCCESS);
		response.setErrorMsg(MSGConstants.SUCCESS_MSG);
		response.setAccountDetails(accounts);
		return response;
	}

	@POST
	@Path("/loanAccountDetails")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static IMPSFetchLoanAccountDetailsResponse loanAccountDetails(@QueryParam(value = "param1") String accNo,
			@QueryParam(value = "param2") String lbrcode) {

		logger.error("Request Loan Accounts Details:-");
		logger.error("Fetch Loan Accounts" + accNo);
		logger.error("Fetch Loan Branch" + lbrcode);
		IMPSFetchLoanAccountDetailsResponse response = new IMPSFetchLoanAccountDetailsResponse();
		try {
			if (accNo == null || accNo.trim().length() != 32) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_ACCOUNT_NO);
				return response;
			}
			if (lbrcode == null || lbrcode.trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_BR_CODE);
				return response;
			}
			if (lbrcode.equalsIgnoreCase("999"))
				lbrcode = "9999";
			D009022 sourceAccount = DataUtils.getAccountDetails(Integer.valueOf(lbrcode), accNo);
			logger.error("sourceAccount::>>>" + sourceAccount.toString());
			TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount,
					String.valueOf("10"), "A");
			logger.error("res::>>>" + res);
			if (null == res) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
				return response;
			}
			if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
				response.setResponse(MSGConstants.ERROR);
				response.setResponse(res.getErrorMsg());
				return response;
			}
			try {

				response = RequestServiceImpl.loanAccountDetails(sourceAccount.getId().getLbrCode(),
						sourceAccount.getId().getPrdAcctId());
				logger.error("Response::>>" + response);
			} catch (Exception e) {
				// TODO: handle exception
				logger.error("Exception::>>" ,e);
				e.printStackTrace();
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
				return response;
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("Exception::>>" ,e);
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
		return response;

	}

	@POST
	@Path("/fetchDepositAccount")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static IMPSFetchDepositeAccountResponse fetchDepositAccount(@QueryParam(value = "param1") String custNo,
			@QueryParam(value = "param2") String depositType) {
		System.out.println("Fetch Deposit Accounts" + custNo);
		IMPSFetchDepositeAccountResponse response = new IMPSFetchDepositeAccountResponse();
		if (custNo == null || custNo.trim().length() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_CUSTOMER);
			return response;
		}
		if (depositType == null || depositType.trim().length() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_DEPOSIT_TYPE);
			return response;
		}
		try {
			response = RequestServiceImpl.depositAccountList(custNo.trim(), depositType.trim());
			System.out.println("response::>>" + response);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}

		return response;
	}

	@POST
	@Path("/fetchDepositAccountReciept")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static IMPSFetchDepositeReceiptResponse fetchDepositAccountReciept(
			@QueryParam(value = "param1") String lbrCode, @QueryParam(value = "param2") String accNo) {
		System.out.println("Received Branch code::>>" + lbrCode);
		System.out.println("Receieved Account Number::>>" + accNo);
		IMPSFetchDepositeReceiptResponse response = new IMPSFetchDepositeReceiptResponse();
		if (lbrCode == null || lbrCode.trim().length() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_BR_CODE);
			return response;
		}
		if (accNo == null || accNo.trim().length() != 32) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_ACCOUNT_NO);
			return response;
		}
		try {
			Session session = HBUtil.getSessionFactory().openSession();
			D009021 DepositPrd = session.get(D009021.class,
					new D009021Id(Integer.parseInt(lbrCode), accNo.substring(0, 8).trim()));
			if (47 == DepositPrd.getModuleType())
				response = RequestServiceImpl.fetchDDSAccReceipt(Integer.parseInt(lbrCode), accNo);
			else
				response = RequestServiceImpl.fetchDepositAccReceipt(Integer.valueOf(lbrCode.trim()), accNo.trim());
			System.out.println("response::>>" + response);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
		return response;
	}

	@POST
	@Path("/fetchDepositIntRates")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static IMPSFetchDepositeInterestRateResponse fetchDepositIntRates(
			@QueryParam(value = "param1") String custNo, @QueryParam(value = "param2") String depositType) {
		System.out.println("Received Customer NO::>>" + custNo);
		System.out.println("Receieved Deposit Type::>>" + depositType);
		IMPSFetchDepositeInterestRateResponse response = new IMPSFetchDepositeInterestRateResponse();
		if (custNo == null || custNo.trim().length() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_CUSTOMER);
			return response;
		}
		if (depositType == null || depositType.trim().length() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_DEPOSIT_TYPE);
			return response;
		}

		try {

			response = RequestServiceImpl.fetchInterestRateDetails(custNo.trim(), depositType.trim());
			System.out.println("response::>>" + response);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
		return response;
	}

	@POST
	@Path("/fetchMinMaxTDParam")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static CustomerInfo fetchMinMaxTDParam(@QueryParam(value = "param1") String depositType,
			@QueryParam(value = "param2") String accNo15digit) {
		System.out.println("Received Account No::>>" + accNo15digit);
		System.out.println("Receieved Deposit Type::>>" + depositType);
		CustomerInfo response = new CustomerInfo();
		if (accNo15digit == null || accNo15digit.trim().length() != 15) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_ACCOUNT_NO);
			return response;
		}
		if (depositType == null || depositType.trim().length() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_DEPOSIT_TYPE);
			return response;
		}
		try {
			response = RequestServiceImpl.getMinMaxValue(depositType, accNo15digit);
			System.out.println("response::>>" + response);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
		return response;
	}

	@POST
	@Path("/searchIfSC")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static BranchDetailsResponse searchIfSC(@QueryParam(value = "param1") String bankName,
			@QueryParam(value = "param2") String city, @QueryParam(value = "param3") String area) {
		System.out.println("Received Bank Name::>>" + bankName);
		System.out.println("Received City::>>" + city);
		System.out.println("Receieved Area::>>" + area);
		BranchDetailsResponse response = new BranchDetailsResponse();
		if (bankName == null || bankName.trim().length() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_BANK_NAME);
			return response;
		}
		if (city == null || city.trim().length() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_CITY);
			return response;
		}
		try {
			response = RequestServiceImpl.getIFSCDetails(bankName, city, area);
			System.out.println("response::>>" + response);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}

		return response;
	}

	@POST
	@Path("/generateOTP")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static OtherChannelServiceResponse generateOTP(@QueryParam(value = "param1") String custNo,
			@QueryParam(value = "param2") String mmid, @QueryParam(value = "param3") String transAmount,
			@QueryParam(value = "param4") String accNo15digit, @QueryParam(value = "param5") String channel) {
		System.out.println("Received Customer No::>>" + custNo);
		System.out.println("Received MMID::>>" + mmid);
		System.out.println("Receieved Amount::>>" + transAmount);
		System.out.println("Receieved Account number::>>" + accNo15digit);
		System.out.println("Recieved Request from::>>" + channel);
		OtherChannelServiceResponse response = new OtherChannelServiceResponse();
		if (channel == null || channel.trim().length() < 1
				|| !new ArrayList<String>(Arrays.asList(MSGConstants.IMPS_CHANNEL, MSGConstants.OTHER_CHANNEL))
						.contains(channel)) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INAVLID_CHANNEL);
			return response;
		}
		if (channel.trim().equalsIgnoreCase(MSGConstants.IMPS_CHANNEL)) {
			if (null == custNo || custNo.trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_CUSTOMER);
				return response;
			}
			if (null == mmid || mmid.trim().length() != 7) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_CUST_NO_MMID);
				return response;
			}
			if (transAmount == null || transAmount.trim().length() < 1 || Double.valueOf(transAmount.trim()) == 0.0) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_AMOUNT);
				return response;
			}
		} else if (channel.equalsIgnoreCase(MSGConstants.OTHER_CHANNEL)) {
			if (accNo15digit == null || Long.valueOf(accNo15digit) == 0l || accNo15digit.trim().length() != 15) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_ACCOUNT_NO);
				return response;
			}
			if (transAmount == null || transAmount.trim().length() < 1 || Double.valueOf(transAmount.trim()) == 0.0) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_AMOUNT);
				return response;
			}
		}
		try {
			response = RequestServiceImpl.impsOTPGeneration(custNo, mmid, transAmount, accNo15digit, channel);
			System.out.println("response::>>" + response);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}

		return response;
	}

	@POST
	@Path("/validateOTP")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static OtherChannelServiceResponse validateOTP(@QueryParam(value = "param1") String custNo,
			@QueryParam(value = "param2") String mmid, @QueryParam(value = "param3") String transAmount,
			@QueryParam(value = "param4") String accNo15digit, @QueryParam(value = "param5") char successMark,
			@QueryParam(value = "param6") String otp, @QueryParam(value = "param7") String channel) {
		System.out.println("Received Customer No::>>" + custNo);
		System.out.println("Received MMID::>>" + mmid);
		System.out.println("Receieved Amount::>>" + transAmount);
		System.out.println("Receieved Account number::>>" + accNo15digit);
		System.out.println("Recieved Request from::>>" + channel);
		System.out.println("Recieved Succes Mark::>>" + successMark);
		System.out.println("Recieved OTP::>>" + otp);

		OtherChannelServiceResponse response = new OtherChannelServiceResponse();
		if (channel == null || channel.trim().length() < 1
				|| !new ArrayList<String>(Arrays.asList(MSGConstants.IMPS_CHANNEL, MSGConstants.OTHER_CHANNEL))
						.contains(channel)) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INAVLID_CHANNEL);
			return response;
		}
		if (channel.trim().equalsIgnoreCase(MSGConstants.IMPS_CHANNEL)) {
			if (null == custNo || custNo.trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_CUSTOMER);
				return response;
			}
			if (null == mmid || mmid.trim().length() != 7) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_CUST_NO_MMID);
				return response;
			}
			if (transAmount == null || transAmount.trim().length() < 1 || Double.valueOf(transAmount.trim()) == 0.0) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_AMOUNT);
				return response;
			}
			if (transAmount == null || transAmount.trim().length() < 1 || Double.valueOf(transAmount.trim()) == 0.0) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_AMOUNT);
				return response;
			}
			/*
			 * if(otp==null || otp.trim().length()<6) {
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMessage(MSGConstants.INVALID_OTP); return response; }
			 */
		} else if (channel.equalsIgnoreCase(MSGConstants.OTHER_CHANNEL)) {
			if (accNo15digit == null || Long.valueOf(accNo15digit) == 0l || accNo15digit.trim().length() != 15) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_ACCOUNT_NO);
				return response;
			}
			if (transAmount == null || transAmount.trim().length() < 1 || Double.valueOf(transAmount.trim()) == 0.0) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_AMOUNT);
				return response;
			}
			/*
			 * if(otp==null || otp.trim().length()<6) {
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMessage(MSGConstants.INVALID_OTP); return response; }
			 */
		}
		response = RequestServiceImpl.impsOTPValidation(custNo, mmid, otp, transAmount, successMark, accNo15digit,
				channel);
		System.out.println("response::>>" + response);
		return response;
	}

	@POST
	@Path("/pinOffsetVerification")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static OtherChannelServiceResponse pinOffsetVerification(@QueryParam(value = "param1") String custNo,
			@QueryParam(value = "param2") String offsetFlag) {
		OtherChannelServiceResponse response = new OtherChannelServiceResponse();
		if (custNo == null || custNo.length() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_CUSTOMER);
			return response;
		}

		if (offsetFlag == null || offsetFlag.length() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_CUSTOMER);
			return response;
		}
		if (!new ArrayList<String>(Arrays.asList("F", "S", "P")).contains(offsetFlag)) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_OFFSET_FLAG);
		}
		return RequestServiceImpl.pinOffsetVerification(custNo, offsetFlag);
	}

	@POST
	@Path("/fetchPigMeAcc")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static PigmeAccountsResponse fetchPigMeAcc(@QueryParam(value = "param1") String accNo) {
		PigmeAccountsResponse response = new PigmeAccountsResponse();
		if (accNo == null || accNo.trim().length() != 15) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.INVALID_ACCOUNT_NO);
			return response;
		}
		return RequestServiceImpl.fetchPigMeAcc(accNo);
	}

	@POST
	@Path("/custDetails")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static CustomerDetails custDetails(@QueryParam(value = "param1") String custNo) {
		System.out.println("Customer number recieved::>>>" + custNo);
		CustomerDetails response = new CustomerDetails();
		response.setResponse(MSGConstants.ERROR);
		response.setResponse(MSGConstants.INVALID_CUSTOMER);
		if (custNo == null || custNo.trim().length() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.INVALID_CUSTOMER);
			return response;
		}
		D350076 d350076 = null;
		try {
			d350076 = (D350076) HBUtil.getSessionFactory().openSession().createCriteria(D350076.class)
					.add(Restrictions.eq("custNo", custNo.trim())).uniqueResult();
			System.out.println("d350076:>>>" + d350076);
			// System.out.println("Mobile
			// NO::>>>"+d350078.getId().getMobileNo());
			if (d350076 == null || d350076.getInterNetBankingSrYn() == 'N') {
				System.out.println("Mobile NO::>>>" + d350076.getCustNo());
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NETBANKING_NOT_AVAILABLE);
				return response;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.INVALID_CUSTOMER);
		}
		D350078 d350078 = null;
		try {
			d350078 = (D350078) HBUtil.getSessionFactory().openSession().createCriteria(D350078.class)
					.add(Restrictions.eq("id.custNo", custNo.trim())).uniqueResult();
			System.out.println("d350078:>>>" + d350078);
			// System.out.println("Mobile
			// NO::>>>"+d350078.getId().getMobileNo());
			if (d350078 != null) {
				System.out.println("Mobile NO::>>>" + d350078.getId().getMobileNo());
				response.setMobileNo(d350078.getId().getMobileNo() == null ? "" : d350078.getId().getMobileNo().trim());
				response.setEmailId(null == d350078.getEmailId() ? "" : d350078.getEmailId().trim());
				response.setCustNo(custNo);
				response.setResponse(MSGConstants.SUCCESS);
				response.setErrorMsg(MSGConstants.SUCCESS_MSG);

			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.INVALID_CUSTOMER);
		}

		return response;
	}

	@POST
	@Path("/searchPigMeAcc")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static PigmeAccountsResponse searchPigMeAcc(@QueryParam(value = "param1") String accNo,
			@QueryParam(value = "param2") String name, @QueryParam(value = "param3") String depositAccNo) {
		PigmeAccountsResponse response = new PigmeAccountsResponse();
		if (RequestServiceImpl.isBlankOrNull(accNo)) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.INVALID_ACCOUNT_NO);
			return response;
		}
		if (RequestServiceImpl.isBlankOrNull(name) && RequestServiceImpl.isBlankOrNull(depositAccNo)) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.INVALID_PARAMETERS);
			return response;
		}
		if (!RequestServiceImpl.isBlankOrNull(depositAccNo)) {
			if (depositAccNo.length() > 8) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_DEPOSIT_ACC_NO);
				return response;
			}
		}
		try {
			return RequestServiceImpl.searchPigMeAcc(accNo, name != null ? name.trim() : "", depositAccNo.trim());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}

	}

	@POST
	@Path("/getCityState")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static BranchDetailsResponse getCityState(@QueryParam(value = "param1") String cityCode,
			@QueryParam(value = "param2") String stateCode) {
		BranchDetailsResponse response = new BranchDetailsResponse();
		if (stateCode == null || stateCode.trim().length() < 1
				|| !new ArrayList<String>(Arrays.asList("S", "C")).contains(stateCode)) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_STATE_CODE);
			return response;
		}
		return RequestServiceImpl.getCityState(cityCode, stateCode);
	}

	@POST
	@Path("/getATMBranchLocator")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static BranchDetailsResponse getATMBranchLocator(@QueryParam(value = "param1") String stateCode,
			@QueryParam(value = "param2") String cityCode, @QueryParam(value = "param3") String custNo) {
		System.out.println("cityCode::>>" + cityCode);
		System.out.println("stateCode::>>>" + stateCode);
		System.out.println("custNo::>>>" + custNo);
		BranchDetailsResponse response = new BranchDetailsResponse();
		if (stateCode == null || stateCode.trim().length() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_STATE_CODE);
			return response;
		}

		if (cityCode == null || cityCode.trim().length() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_STATE_CODE);
			return response;
		}
		System.out.println("below validation");
		return RequestServiceImpl.getATMBranchLocator(cityCode, stateCode, custNo);
	}

	/*
	 * @POST
	 * 
	 * @Path("/openTermDepositAccount")
	 * 
	 * @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	 * 
	 * @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML }) public
	 * static OtherChannelServiceResponse
	 * openTermDepositAccount(IMPSTermDepositRequest request) {
	 * System.out.println("request.getAccountNo()::>>" + request.getAccountNo());
	 * System.out.println("request.getActivity()::>>" + request.getActivity());
	 * System.out.println("request.getCustomerNo():>" + request.getCustomerNo());
	 * System.out.println("request.getProductCode()::>>" +
	 * request.getProductCode()); System.out.println("request.getAmount():>>" +
	 * request.getAmount()); System.out.println("request.getLbrCode()::>>" +
	 * request.getLbrCode()); System.out.println("request.getNoOfDays()::>>" +
	 * request.getNoOfDays()); System.out.println("request.getNoOfMonths()::>>" +
	 * request.getNoOfMonths()); OtherChannelServiceResponse response = new
	 * OtherChannelServiceResponse();
	 * 
	 * if (request.getNoOfDays() == null) {
	 * response.setResponse(MSGConstants.ERROR);
	 * response.setErrorMessage(MSGConstants.INVALID_NO_OF_DAYS); return response; }
	 * if (request.getNoOfMonths() == null) {
	 * response.setResponse(MSGConstants.ERROR);
	 * response.setErrorMessage(MSGConstants.INVALID_NO_OF_MONTHS); return response;
	 * } if (request.getAmount() == null || request.getAmount() == 0) {
	 * response.setResponse(MSGConstants.ERROR);
	 * response.setErrorMessage(MSGConstants.INVALID_AMOUNT); return response; } if
	 * (request.getActivity() == null || request.getActivity().trim().length() < 1)
	 * { response.setResponse(MSGConstants.ERROR);
	 * response.setErrorMessage(MSGConstants.INVALID_ACTIVITY); return response; }
	 * if (request.getProductCode() == null ||
	 * request.getProductCode().trim().length() < 1) {
	 * response.setResponse(MSGConstants.ERROR);
	 * response.setErrorMessage(MSGConstants.PRODUCT_NOT_FOUND); return response; }
	 * if (request.getCustomerNo() == null ||
	 * request.getCustomerNo().trim().length() < 1) {
	 * response.setResponse(MSGConstants.ERROR);
	 * response.setErrorMessage(MSGConstants.INVALID_CUSTOMER); return response; }
	 * if (request.getAmount() == 0) { response.setResponse(MSGConstants.ERROR);
	 * response.setErrorMessage(MSGConstants.INVALID_AMOUNT); return response; }
	 * return RequestServiceImpl.openTermDepositAccount(request); }
	 */

	@POST
	@Path("/balEnqService")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static YouCloudWalletResponse balEnqService(@QueryParam(value = "param1") String mobNo) {
		YouCloudWalletResponse response = new YouCloudWalletResponse();
		if (mobNo == null || mobNo.trim().length() != 10) {
			response.setResponse(MSGConstants.ERROR);
			response.setRespDesc(MSGConstants.INVALID_MOBILE_NO);
			response.setRespCode("M1");
			return response;
		}
		if (mobNo.equalsIgnoreCase("8983389108")) {
			response.setResponse(MSGConstants.SUCCESS);
			response.setRespDesc(MSGConstants.SUCCESS_MSG);
			response.setRespCode(ResponseCodes.SUCCESS);
			response.setBalance("100000 CR");
			return response;
		}
		return null;
	}

	@POST
	@Path("/fetchCustDetails")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static CustomerDetails fetchCustDetails(@QueryParam(value = "param1") String accNo) {
		CustomerDetails response = new CustomerDetails();
		if (accNo == null || accNo.trim().length() != 15) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.INVALID_ACCOUNT_NO);
			return response;
		}
		try {
			return RequestServiceImpl.fetchMobno(accNo);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}

	}

	@POST
	@Path("/validateAccount")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static CustomerDetails validateAccount(@QueryParam(value = "param1") String accNo,
			@QueryParam(value = "param2") String custNo) {
		CustomerDetails response = new CustomerDetails();
		if (accNo == null || accNo.trim().length() != 15) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.INVALID_ACCOUNT_NO);
			return response;
		}
		if (custNo == null || custNo.trim().length() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.INVALID_CUSTOMER);
			return response;
		}
		try {
			response = RequestServiceImpl.validAccount(accNo.trim(), custNo.trim());
			System.out.println("response::>>" + response);
			return response;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
	}

	@POST
	@Path("/updateMobileNo")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static CustomerDetails updateMobileNo(@QueryParam(value = "param1") String mobNo,
			@QueryParam(value = "param2") String accNo) {
		CustomerDetails response = new CustomerDetails();
		System.out.println("Recieved MobNo::>>" + mobNo);
		if (mobNo == null || mobNo.trim().length() != 10) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.INVALID_MOB_NO);
			return response;
		}
		if (accNo == null || accNo.trim().length() != 15) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.INVALID_ACCOUNT_NO);
			return response;
		}
		D009022 sourceAccount = DataUtils.getAccount(accNo.trim());
		if (sourceAccount == null) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
			return response;
		}
		TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount, String.valueOf("0"),
				"MB");
		if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(res.getErrorMsg());
			return response;
		}
		return RequestServiceImpl.updateMobileNo(mobNo.trim(), accNo);
	}

	@POST
	@Path("/validateBinIfsc")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static CustomerDetails validateBinIfsc(@QueryParam(value = "param1") String accNo,
			@QueryParam(value = "param2") String ifsc, @QueryParam(value = "param3") String flag) {
		CustomerDetails response = new CustomerDetails();
		System.out.println("Recieved Account No::>>" + accNo);
		System.out.println("IFSC::>>" + ifsc);
		System.out.println("flag::>>" + flag + "--");
		System.out.println("111 ---");
		try {
			/*
			 * if (!new ArrayList<>(Arrays.asList(MSGConstants.P2P_TRANSACTION,
			 * MSGConstants.P2A_TRANSACTION)) .contains(flag.trim())) {
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.INVALID_TRANSACTION_TYPE); return response;
			 * }
			 */
			if (MSGConstants.P2P_TRANSACTION.equalsIgnoreCase(flag.trim())
					|| MSGConstants.P2A_TRANSACTION.equalsIgnoreCase(flag.trim())) {
				System.out.println("flag::>>" + flag + "---");
			} else {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_TRANSACTION_TYPE);
				return response;
			}
			if (flag.trim().equalsIgnoreCase("P2A")) {
				System.out.println("Inside::>>" + flag + "---");
				if (accNo == null || accNo.trim().length() < 10 || accNo.trim().length() > 20) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.INVALID_ACCOUNT_NO);
					return response;
				}
				/*
				 * try { Integer.valueOf(accNo.substring(0, 3)); } catch (Exception e) { //
				 * TODO: handle exception response.setResponse(MSGConstants.ERROR);
				 * response.setErrorMsg(MSGConstants.INVALID_ACCOUNT_NO); return response; }
				 */
				if (ifsc == null || ifsc.trim().length() != 11) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.INVALID_IFS_CODE);
					return response;
				}
					int ifscCheckLength = Integer.parseInt(ConfigurationLoader.getParameters(false).getProperty("IFSC_LENGTH"));
					
					String ifsc2 = ConfigurationLoader.getParameters(false).getProperty("IFSC1");
					if(ifsc2!=null && !"".equalsIgnoreCase(ifsc2))
						ifsc2 = ifsc2.substring(0, ifscCheckLength);
				
				
				//if (MSGConstants.IFSC_CODE.contains(ifsc.substring(0, 4))) {
					//if (MSGConstants.IFSC_CODE.contains(ifsc.substring(0, 8))) {
					if (ifsc.trim().substring(0, ifscCheckLength).equalsIgnoreCase(MSGConstants.IFSC_CODE.substring(0, ifscCheckLength))
							|| ifsc.trim().substring(0, ifscCheckLength).equalsIgnoreCase(ifsc2)){
					
						/*** Added by Aniket on 29th July,2019 to cheque IFSC Code from D946022 ***/
						System.out.println("BRANCH_WISE_IFS1 ---");
						if (ConfigurationLoader.getParameters(false).getProperty("BRANCH_WISE_IFS")
								.equalsIgnoreCase("Y")) {
							System.out.println("BRANCH_WISE_IFS ---");
							D946022 ifscMaster = DataUtils.getIFSCCodeDetail(ifsc);
							if (Integer.valueOf(accNo.trim().substring(0, 3)) != Integer
									.valueOf(ifscMaster.getBranchRbicd())) {
								response.setResponse(MSGConstants.ERROR);
								response.setErrorMsg(MSGConstants.INVALID_ACCNO_IFSC);
								return response;
							}
						}
						System.out.println("aT GetAcc---");
						D009022 sourceAccount = DataUtils.getAccount(accNo.trim());
						if (sourceAccount == null) {
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
							return response;
						}
						try {
							System.out.println("aT validateIfsc---");
							return DataUtils.validateIfsc(ifsc);
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
							return response;
						}
					//}
				} else {
					try {
						return DataUtils.validateAccNoIfsc(MSGConstants.P2A_TRANSACTION, ifsc);
					} catch (Exception e) {
						e.printStackTrace();
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
						return response;
					}
				}
			} else if (flag.equalsIgnoreCase("P2P")) {
				try {
					return DataUtils.validateAccNoIfsc(MSGConstants.P2P_TRANSACTION, ifsc + "001");
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
					return response;
				}
			}
			/*
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMsg(MSGConstants.INVALID_TRANSACTION_TYPE);
			 */
			return response;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/updateEmailId")
	public OtherChannelServiceResponse updateEmailId(@QueryParam(value = "param1") String custNo,
			@QueryParam(value = "param2") String emailId) {
		OtherChannelServiceResponse response = new OtherChannelServiceResponse();
		if (custNo == null || custNo.trim().length() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_CUSTOMER);
			return response;
		}
		if (emailId == null || emailId.trim().length() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_EMAIL_ID);
			return response;
		}
		try {
			return new RequestServiceImpl().updateEmailId(custNo, emailId);
		} catch (Exception e) {
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
	}

	@POST
	@Path("/searchAllPigMeAcc")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static PigmeAccountsResponse searchPigMeAcc(@QueryParam(value = "param1") String accNo) {
		PigmeAccountsResponse response = new PigmeAccountsResponse();
		if (RequestServiceImpl.isBlankOrNull(accNo) || accNo.trim().length() != 15) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.INVALID_ACCOUNT_NO);
			return response;
		}

		try {
			return RequestServiceImpl.searchPigMeAccCust(accNo);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}

	}

	@POST
	@Path("/miniStatementIVR")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static MiniStatementResponse miniStatement(@QueryParam(value = "param1") String mobNo,
			@QueryParam(value = "param2") String accNo) {
		MiniStatementResponse stmt = new MiniStatementResponse();
		if (accNo == null || accNo.trim().isEmpty() || accNo.trim().length() != 15) {
			stmt.setResponse(MSGConstants.ERROR);
			stmt.setErrorMSg(MSGConstants.INVALID_ACCOUNT_NO);
			return stmt;
		}
		if (mobNo == null || mobNo.trim().isEmpty() || mobNo.trim().length() != 10) {
			stmt.setResponse(MSGConstants.ERROR);
			stmt.setErrorMSg(MSGConstants.INVALID_MOB_NO);
			return stmt;
		}
		D009022 sourceAccount = DataUtils.getAccount(accNo);
		// D009022
		// sourceAccount=DataUtils.getAccountDetails(Integer.valueOf(mobNo.trim()),
		// accNo);
		if (sourceAccount == null) {
			stmt.setResponse(MSGConstants.ERROR);
			stmt.setErrorMSg(MSGConstants.ACCOUNT_NOT_FOUND);
			return stmt;
		}
		TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount, "0", "AS");
		if (res != null) {
			if (res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
				stmt.setResponse(MSGConstants.ERROR);
				stmt.setErrorMSg(res.getErrorMsg());
				return stmt;
			}
		}
		ArrayList<MiniStatementResponse> ministatements = new ArrayList<MiniStatementResponse>();
		List<D009040> list = RequestServiceImpl.getMinistatement(Integer.valueOf(sourceAccount.getId().getLbrCode()),
				sourceAccount.getId().getPrdAcctId(), 5);
		if (list != null && !list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				MiniStatementResponse response = new MiniStatementResponse();
				response.setTransAmount("" + list.get(i).getFcyTrnAmt());
				response.setTransDate("" + list.get(i).getPostDate());
				response.setTransDRCR("" + list.get(i).getDrCr());
				response.setTransNarrative("" + list.get(i).getParticulars().trim());
				ministatements.add(response);
			}
			stmt.setMiniStmts(ministatements);
			stmt.setResponse(MSGConstants.SUCCESS);
			stmt.setErrorMSg(MSGConstants.SUCCESS_MSG);
			ministatements = null;
		} else {
			stmt.setResponse(MSGConstants.ERROR);
			stmt.setErrorMSg(MSGConstants.NO_TRANSACTION_FOUND);
			return stmt;
		}
		try {
			stmt.setResponse(MSGConstants.SUCCESS);
			stmt.setErrorMSg(MSGConstants.SUCCESS_MSG);
			return stmt;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
			stmt.setErrorMSg(MSGConstants.WEB_SERVICE_ERROR);
			stmt.setResponse(MSGConstants.ERROR);
			return stmt;
		}
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/updateEmailIdIVR")
	public OtherChannelServiceResponse updateEmailIdIVR(@QueryParam(value = "param1") String mobNo,
			@QueryParam(value = "param2") String emailId) {
		OtherChannelServiceResponse response = new OtherChannelServiceResponse();
		if (mobNo == null || mobNo.trim().length() != 10) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_MOB_NO);
			return response;
		}
		if (emailId == null || emailId.trim().length() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_EMAIL_ID);
			return response;
		}
		try {
			return new RequestServiceImpl().updateEmailIdIVR(mobNo, emailId);
		} catch (Exception e) {
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
	}

	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/getAccountIVR")
	public AccountInfo getAccountIVR(@QueryParam(value = "param1") String accNo) {
		AccountInfo response = new AccountInfo();
		if (accNo == null || accNo.trim().length() != 15) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.INVALID_ACCOUNT_NO);
			return response;
		}
		try {
			D009022 d009022 = DataUtils.getAccount(accNo);
			if (d009022 == null) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
				return response;
			}
			response.setAccNo(d009022.getId().getPrdAcctId());
			response.setBrCode(d009022.getId().getLbrCode() + "");
			response.setResponse(MSGConstants.SUCCESS);
			response.setErrorMsg(MSGConstants.SUCCESS_MSG);
			return response;

		} catch (Exception e) {
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
	}

	@POST
	@Path("/fetchAccountsIVR")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static AccountResponse fetchAccountsIVR(@QueryParam(value = "param1") String mobNo) throws Exception {
		System.out.println("Fetch Accounts::>>" + mobNo);
		AccountResponse response = new AccountResponse();
		try {
			if (mobNo == null || mobNo.trim().length() != 10) {
				response.setReponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_MOB_NO);
				return response;
			}
			Session session = HBUtil.getSessionFactory().openSession();
			Criteria criteria2 = session.createCriteria(D350078.class);
			criteria2.add(Restrictions.ilike("id.mobileNo", "%" + mobNo.trim() + "%"));
			List<D350078> listD78 = criteria2.list();
			System.out.println("Size::>>" + listD78.size());
			if (listD78 == null || listD78.isEmpty()) {
				session.close();
				session = null;
				response.setReponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.MOBILE_NO_NOT_REGISTERED);
				return response;
			}
			session.close();
			session = null;
			ArrayList<AccountDetails> accounts = new ArrayList<>();
			List<D009022> list = DataUtils.getValidAccList(listD78.get(0).getId().getCustNo());
			System.out.println("list::>>" + list);
			if (list == null || list.size() < 1) {
				response.setReponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.RECORD_NOT_FOUND);
				return response;
			}
			for (int i = 0; i < list.size(); i++) {
				AccountDetails details = new AccountDetails();
				details.setAccCode(list.get(i).getId().getPrdAcctId());
				details.setBrachCode(String.valueOf(list.get(i).getId().getLbrCode()));
				details.setFormattedAccount(list.get(i).getId().getLbrCode() + "/"
						+ list.get(i).getId().getPrdAcctId().substring(0, 8).trim() + "/"
						+ Long.valueOf(list.get(i).getId().getPrdAcctId().substring(16, 24)));
				details.setName(list.get(i).getLongName().trim());
				accounts.add(details);
			}
			response.setReponse(MSGConstants.SUCCESS);
			response.setErrorMsg(MSGConstants.SUCCESS_MSG);
			response.setAccountDetails(accounts);
			return response;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
			response.setReponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
	}

	@POST
	@Path("/getLoanAccList")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static PigmeAccountsResponse getLoanAccList(@QueryParam(value = "param1") String custNo,
			@QueryParam(value = "param2") String accNo, @QueryParam(value = "param3") String name,
			@QueryParam(value = "param4") String brCode) {
		logger.error("<<<<:::== getLoanAccList.service() ==::>>>>>");
		logger.error("custNo::>>" + custNo + " accNo::>>" + accNo + " Name:>>>" + name);
		PigmeAccountsResponse response = new PigmeAccountsResponse();
		try {
			ArrayList<PigmeAccountsResponse> accList = new ArrayList<>();
			if ((custNo == null || custNo.trim().length() < 1 || Long.valueOf(custNo.trim()) == 0l)
					&& (accNo == null || accNo.trim().length() < 1) && (name == null || name.trim().length() < 1)
					&& (brCode == null || Long.valueOf(brCode.trim()) == 0)) {
				logger.error("<<<:: Invalid parameters ::>>>");
				System.out.println("<<<:: Invalid parameters ::>>>");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_PARAMETERS);
				return response;
			}
			if (brCode == null || brCode.trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_BR_CODE);
				return response;
			}
			List<D009022> list = DataUtils.getLoanAccListPGM(custNo, accNo, name, Integer.valueOf(brCode));
			logger.error("list::>>" + list);
			System.out.println("list::>>" + list);
			if (list == null || list.isEmpty()) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.RECORD_NOT_FOUND);
				return response;
			}
			for (D009022 d009022 : list) {
				PigmeAccountsResponse response2 = new PigmeAccountsResponse();
				response2.setAccNo(d009022.getId().getPrdAcctId());
				response2.setBrCode(d009022.getId().getLbrCode() + "");
				response2.setCustNo(d009022.getCustNo() + "");
				response2.setName(d009022.getLongName().trim());
				try {
					response2.setFormattedAcc(
							d009022.getId().getLbrCode() + "/" + d009022.getId().getPrdAcctId().substring(0, 8).trim()
									+ "/" + Long.valueOf(d009022.getId().getPrdAcctId().substring(16, 24)));
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				response2.setBalance("" + Double.valueOf(
						Double.valueOf(d009022.getActClrBalFcy()) - Double.valueOf(d009022.getTotalLienFcy())));
				accList.add(response2);
				response2 = null;
			}
			response.setResponse(MSGConstants.SUCCESS);
			response.setErrorMsg(MSGConstants.SUCCESS_MSG);
			response.setAccList(accList);
			return response;
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
	@Path("/validateAccPrePaidCard")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static CustomerPhysicalCardOnboardingreq validateAccPrePaidCard(@QueryParam(value = "param1") String accNo) {
		CustomerPhysicalCardOnboardingreq response = new CustomerPhysicalCardOnboardingreq();
		try {
			if (accNo == null || accNo.trim().length() != 15) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_ACCOUNT_NO);
				return response;
			}
			D009022 d009022 = DataUtils.getAccount(accNo);
			if (d009022 == null) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_ACCOUNT_NO);
				return response;
			}
			TransactionValidationResponse response2 = TransactionServiceImpl.validateAccount(d009022, "0", "A");
			if (response2 == null) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_VALIDATION_FAIL);
				return response;
			}
			if (response2.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(response2.getErrorMsg());
				return response;
			}
			return DataUtils.getAccDetails(d009022);
		} catch (Exception e) {
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
	}

	@POST
	@Path("/fetchAdharNo")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static AccountInfo fetchAdharNo(@QueryParam(value = "param1") String accNo,
			@QueryParam(value = "param2") String custNo) {
		AccountInfo response = new AccountInfo();
		try {
			if ((accNo == null || accNo.trim().length() < 1) && (custNo == null || custNo.trim().length() < 1)) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_PARAMETERS);
				return response;
			}
			if (accNo.trim().equalsIgnoreCase(MSGConstants.BLANK))
				return DataUtils.fetchAdharNo(custNo, MSGConstants.YES);
			if (custNo.trim().equalsIgnoreCase(MSGConstants.BLANK))
				return DataUtils.fetchAdharNo(custNo, MSGConstants.NO);

			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.INVALID_PARAMETERS);
			return response;
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
	@Path("/aadharSeeding")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static OtherChannelServiceResponse aadharSeeding(@QueryParam(value = "param1") String custNo,
			@QueryParam(value = "param2") String accNo15digit, @QueryParam(value = "param3") String aadharNo,
			@QueryParam(value = "param4") String channel) {
		OtherChannelServiceResponse response = new OtherChannelServiceResponse();
		try {
			if (channel == null || channel.trim().length() < 1
					|| !new ArrayList<String>(Arrays.asList(MSGConstants.IMPS_CHANNEL, MSGConstants.OTHER_CHANNEL))
							.contains(channel)) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INAVLID_CHANNEL);
				return response;
			}
			if (accNo15digit == null || accNo15digit.trim().length() != 15) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_ACCOUNT_NO);
				return response;
			}
			if (aadharNo == null || String.valueOf(Long.valueOf(aadharNo.trim().length() + "")).length() != 12) {

				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_AADHAR_NO);
				return response;
			}
			if (channel.trim().equalsIgnoreCase(MSGConstants.IMPS_CHANNEL)) {
				if (custNo == null || custNo.trim().length() < 1) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_CUSTOMER);
					return response;
				}
				return DataUtils.aadharSeeding(custNo, accNo15digit, aadharNo, channel);
			}
			if (channel.trim().equalsIgnoreCase(MSGConstants.OTHER_CHANNEL)) {
				return DataUtils.aadharSeeding(custNo, accNo15digit, aadharNo, channel);
			}
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_REQUEST);
			return response;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
	}

	@POST
	@Path("/aadharSeedingIVR")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static OtherChannelServiceResponse aadharSeeding(@QueryParam(value = "param1") String mobNo,
			@QueryParam(value = "param2") String aadharNo // ,
															// @QueryParam(value="param3")String
															// aadharNo,
															// @QueryParam(value="param4")String
															// channel
	) {
		OtherChannelServiceResponse response = new OtherChannelServiceResponse();
		try {
			if (aadharNo == null || aadharNo.trim().length() != 12) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_AADHAR_NO);
				return response;
			}
			return DataUtils.aadharSeedingIVR(mobNo, aadharNo);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
	}

	@POST
	@Path("/prepaidCardOnBoarding")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static OtherChannelServiceResponse prepaidCardOnBoarding(@QueryParam(value = "param1") String mobNo,
			@QueryParam(value = "param2") String aadharNo) {
		OtherChannelServiceResponse response = new OtherChannelServiceResponse();
		try {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_REQUEST);
			return response;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
	}

	@POST
	@Path("/getIMPSTransactions")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static ImpsTransactionReport getIMPSTransactions(@QueryParam(value = "param1") String transType,
			@QueryParam(value = "param2") String flag, @QueryParam(value = "param3") String fromDate,
			@QueryParam(value = "param4") String toDate) {
		System.out.println("flag::>>" + flag);
		System.out.println("transType::>>" + transType);
		ImpsTransactionReport response = new ImpsTransactionReport();
		try {

			if (transType.equalsIgnoreCase(MSGConstants.P2P_TRANSACTION))
				return DataUtils.getP2PTransaction(flag, fromDate, toDate);

			if (transType.equalsIgnoreCase(MSGConstants.P2A_TRANSACTION))
				return DataUtils.getP2ATransaction(flag, fromDate, toDate);

			if (transType.equalsIgnoreCase(MSGConstants.P2P_REV))
				return DataUtils.getp2pRevTrn(flag, fromDate, toDate);

			if (transType.equalsIgnoreCase(MSGConstants.P2AREV))
				return DataUtils.getp2aRevTrn(flag, fromDate, toDate);

			if (transType.equalsIgnoreCase(MSGConstants.P2A_CREDIT))
				return DataUtils.getp2aCreditTrn(flag, fromDate, toDate);

			if (transType.equalsIgnoreCase(MSGConstants.P2P_CREDIT))
				return DataUtils.getp2pCreditTrn(flag, fromDate, toDate);

			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.INVALID_REQUEST);
			return response;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
	}

	@POST
	@Path("/miniStatementMPOS")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static MiniStatementResponse miniStatementMPOS(@QueryParam(value = "param1") String brCode,
			@QueryParam(value = "param2") String accNo) {
		MiniStatementResponse stmt = new MiniStatementResponse();
		if (accNo == null || accNo.trim().isEmpty()) {
			stmt.setResponse(MSGConstants.ERROR);
			stmt.setErrorMSg(MSGConstants.INVALID_ACCOUNT_NO);
			return stmt;
		}
		if (brCode == null || brCode.trim().isEmpty()) {
			stmt.setResponse(MSGConstants.ERROR);
			stmt.setErrorMSg(MSGConstants.INVALID_BR_CODE);
			return stmt;
		}
		D009022 sourceAccount = DataUtils.getAccountDetails(Integer.valueOf(brCode.trim()), accNo);
		if (sourceAccount == null) {
			stmt.setResponse(MSGConstants.ERROR);
			stmt.setErrorMSg(MSGConstants.ACCOUNT_NOT_FOUND);
			return stmt;
		}
		TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount, "0", "AS");
		if (res != null) {
			if (res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
				stmt.setResponse(MSGConstants.ERROR);
				stmt.setErrorMSg(res.getErrorMsg());
				return stmt;
			}
		}
		ArrayList<MiniStatementResponse> ministatements = new ArrayList<MiniStatementResponse>();
		List<D009040> list = RequestServiceImpl.getMinistatement(Integer.valueOf(brCode), accNo, 7);
		if (list != null && !list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				MiniStatementResponse response = new MiniStatementResponse();
				response.setTransAmount("" + list.get(i).getFcyTrnAmt());
				response.setTransDate("" + list.get(i).getPostDate());
				response.setTransDRCR("" + list.get(i).getDrCr());
				response.setTransNarrative("" + list.get(i).getParticulars().trim());
				ministatements.add(response);
			}
			stmt.setMiniStmts(ministatements);
			stmt.setResponse(MSGConstants.SUCCESS);
			stmt.setErrorMSg(MSGConstants.SUCCESS_MSG);
			ministatements = null;
		} else {
			stmt.setResponse(MSGConstants.ERROR);
			stmt.setErrorMSg(MSGConstants.NO_TRANSACTION_FOUND);
			return stmt;
		}
		try {

			stmt.setResponse(MSGConstants.SUCCESS);
			stmt.setErrorMSg(MSGConstants.SUCCESS_MSG);
			return stmt;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
			stmt.setErrorMSg(MSGConstants.WEB_SERVICE_ERROR);
			stmt.setResponse(MSGConstants.ERROR);
			return stmt;
		}
	}

	@POST
	@Path("/billPayementStatus")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static OtherChannelServiceResponse billPayementStatus(@QueryParam(value = "param1") String rrn,
			@QueryParam(value = "param2") String status) {
		OtherChannelServiceResponse response = new OtherChannelServiceResponse();
		logger.error("billPayementStatus.service()--> RRN::>>" + rrn + " STATUS::>" + status);
		try {
			if (rrn == null || rrn.trim().length() != 12) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_RRN_NO);
				return response;
			}
			response = DataUtils.updateBillPaymentStatus(rrn, status);
			logger.error("billPayementStatus.service()--> Response::>>" + response.toString());
			return response;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("", e);
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
	}

	@POST
	@Path("/mposTrnReport")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static ImpsTransactionReport mposTrnReport(@QueryParam(value = "param1") String agentAccNo,
			@QueryParam(value = "param2") String fromdate, @QueryParam(value = "param3") String toDate,
			@QueryParam(value = "param4") String flag) {
		ImpsTransactionReport response = new ImpsTransactionReport();
		System.out.println("agentAccNo::>>" + agentAccNo + " fromdate::>>" + fromdate + " toDate::>> " + toDate
				+ " Flag::>>" + flag);
		logger.error("agentAccNo::>>" + agentAccNo + " fromdate::>>" + fromdate + " toDate::>> " + toDate + " Flag::>>"
				+ flag);
		try {
			if (agentAccNo == null || agentAccNo.trim().length() != 15) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_AGENT_ACC_NO);
				return response;
			}
			if (fromdate == null || fromdate.trim().length() != 8) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_FROM_DATE);
				return response;
			}
			if (toDate == null || toDate.trim().length() != 8) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_TO_DATE);
				return response;
			}
			response = DataUtils.mposTrnReport(agentAccNo, fromdate, toDate, flag);
			logger.error("billPayementStatus.service()--> Response::>>" + response.toString());
			return response;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("", e);
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
	}

	@POST
	@Path("/mposTrnSummary")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static ImpsTransactionReport mposTrnSummary(@QueryParam(value = "param1") String agentAccNo,
			@QueryParam(value = "param2") String fromdate, @QueryParam(value = "param3") String toDate,
			@QueryParam(value = "param4") String flag) {
		ImpsTransactionReport response = new ImpsTransactionReport();
		System.out.println("agentAccNo::>>" + agentAccNo + " fromdate::>>" + fromdate + " toDate::>> " + toDate
				+ " Flag::>>" + flag);
		logger.error("agentAccNo::>>" + agentAccNo + " fromdate::>>" + fromdate + " toDate::>> " + toDate + " Flag::>>"
				+ flag);
		try {
			if (agentAccNo == null || agentAccNo.trim().length() != 15) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_AGENT_ACC_NO);
				return response;
			}
			if (fromdate == null || fromdate.trim().length() != 8) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_FROM_DATE);
				return response;
			}
			if (toDate == null || toDate.trim().length() != 8) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_TO_DATE);
				return response;
			}
			response.setResponse(MSGConstants.SUCCESS);
			response.setAgencybankingTrnSummury(DataUtils.getTRNSummary(fromdate, toDate, "00", agentAccNo));
			logger.error("billPayementStatus.service()--> Response::>>" + new Gson().toJson(response));
			return response;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("", e);
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
	}

	@POST
	@Path("/updateContactDetails")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static CustomerDetails newMobileNo(@QueryParam(value = "param1") String custNo,
			@QueryParam(value = "param2") String mobNo, @QueryParam(value = "param3") String email) {
		CustomerDetails response = new CustomerDetails();
		CustNewMobileDetails res = new CustNewMobileDetails();

		logger.error("Recieved MobNo::>>" + mobNo);
		if ((mobNo == null || mobNo.trim().length() != 10) && (email == null || email.trim().equalsIgnoreCase(""))) {

			response.setErrorMsg(MSGConstants.INVALID_MOB_NO);
			response.setResponse(MSGConstants.ERROR + "");
			return response;
		}

		response = RequestServiceImpl.validCustomer(custNo);

		if (response != null && response.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {

			res.setSuccess(false);
			res.setMessage(response.getErrorMsg());
			return response;
		}

		if (mobNo != null && !"".equalsIgnoreCase(mobNo.toString().trim())) {
			res = RequestServiceImpl.newMobileNo(mobNo.trim(), custNo);
			response.setErrorMsg(res.getMessage());
			response.setResponse(res.isSuccess() ? MSGConstants.SUCCESS : MSGConstants.ERROR + "");
			return response;
		} else if (email != null && !"".equalsIgnoreCase(email.toString().trim())) {
			res = RequestServiceImpl.newEmailId(email.trim(), custNo);
			response.setErrorMsg(res.getMessage());
			response.setResponse(res.isSuccess() ? MSGConstants.SUCCESS : MSGConstants.ERROR + "");
		}
		return response;

	}

	@POST
	@Path("/fetchDepositScheme")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static DepositePrdCdList fetchDepositScheme(@QueryParam(value = "param1") String custNo) {

		DepositePrdCdList list = new DepositePrdCdList();

		CustomerDetails response = RequestServiceImpl.validCustomer(custNo);

		if (response != null && response.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {

			list.setSuccess(false);
			list.setResponse(response.getResponse());
			list.setErrorMsg(response.getErrorMsg());
			return list;
		}

		List<Object[]> newList = new ArrayList<Object[]>();
		newList = RequestServiceImpl.getFDProductList(custNo);
		List<DepositeParameters> productList = new ArrayList<DepositeParameters>();
		if (newList != null) {
			try {
				for (int i = 0; i < newList.size(); i++) {

					int type = 2;
					Object[] product = newList.get(i);

					Session session = HBUtil.getSessionFactory().openSession();
					String query = "select id.lbrCode from D009022 where custNo=" + custNo + " And id.prdAcctId Like '"
							+ product[0] + "%' and actTotBalFcy > 0";

					Query q2 = session.createQuery(query);
					if (q2.getResultList() != null) {

						String queryString1 = "SELECT lbrCode from D009011 where custNo=" + custNo;
						Query q1 = session.createQuery(queryString1);

						D009021 DepositPrd = session.get(D009021.class,
								new D009021Id((int) q1.getSingleResult(), (product[0] + "")));
						logger.error("INT RATEModuleType()::>>" + DepositPrd.getModuleType());
						short module = DepositPrd.getModuleType();
						int types = Integer.valueOf(module);
						if (20 == types) {
							if ("B".equalsIgnoreCase(product[6].toString())
									|| "M".equalsIgnoreCase(product[6].toString()))
								type = 2;
							else if ("D".equalsIgnoreCase(product[6].toString()))
								type = 1;
							Long max = 0L;
							Double maxAmt = (Double) product[3];
							try {
								max = maxAmt.longValue();
								// maxAmt = max.doubleValue();
							} catch (Exception ex) {

							}
							DepositeParameters prodMap = new DepositeParameters();
							prodMap.setCode((product[0] + "").trim());
							prodMap.setValue(product[1] + "");
							prodMap.setProductType((product[0] + "").trim());
							prodMap.setMaxTenure(((Short) product[5]).intValue());
							prodMap.setMaxTenureType(type);
							prodMap.setMinAmount((Math.round(((Double) product[2]) * 100.0) / 100.0));
							prodMap.setMaxAmount(max);
							prodMap.setMinTenure(((Short) product[4]).intValue());
							prodMap.setMinTenureType(type);
							productList.add(prodMap);
						}
						System.out.println("response::>>" + response);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				list.setSchemeCodelist(productList);
				list.setSuccess(false);
				list.setResponse(MSGConstants.ERROR);
				list.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			}
			list.setSchemeCodelist(productList);
			list.setSuccess(true);
			list.setResponse("SUCCESS");
			list.setErrorMsg(MSGConstants.SUCCESS_MSG);

		} else {
			list.setSchemeCodelist(productList);
			list.setSuccess(false);
			list.setResponse(MSGConstants.ERROR);
			list.setErrorMsg(MSGConstants.RECORD_NOT_FOUND);
		}
		return list;
	}

	@POST
	@Path("/openTDAccount")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static DepositeAcctOpenResponseNew openTermDepositAccount(DepositeAcctOpenRequest request) {
		System.out.println("request.getAccountNo()::>>" + request.getDebitAccount());
		System.out.println("request.getActivity()::>>" + request.getAccType());
		System.out.println("request.getCustomerNo():>" + request.getCustNo());
		System.out.println("request.getProductCode()::>>" + request.getProdCode());
		System.out.println("request.getAmount():>>" + request.getDepositAmount());
		System.out.println("request.getLbrCode()::>>" + request.getBrcode());
		System.out.println("request.getNoOfDays()::>>" + request.getDays());
		System.out.println("request.getNoOfMonths()::>>" + request.getMonth());
		DepositeAcctOpenResponseNew response = new DepositeAcctOpenResponseNew();

		if (request.getDays() == null) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_NO_OF_DAYS);
			response.setSuccess(false);
			return response;
		}
		if (request.getMonth() == null) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_NO_OF_MONTHS);
			response.setSuccess(false);
			return response;
		}
		if (request.getDepositAmount() == null || Double.parseDouble(request.getDepositAmount()) == 0) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_AMOUNT);
			response.setSuccess(false);
			return response;
		}
		if (request.getAccType() == null || request.getAccType().trim().length() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_ACTIVITY);
			response.setSuccess(false);
			return response;
		}
		if (request.getProdCode() == null || request.getProdCode().trim().length() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.PRODUCT_NOT_FOUND);
			response.setSuccess(false);
			return response;
		}
		if (request.getCustNo() == null || request.getCustNo().trim().length() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_CUSTOMER);
			response.setSuccess(false);
			return response;
		}
		if (Double.parseDouble(request.getDepositAmount()) == 0) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_AMOUNT);
			response.setSuccess(false);
			return response;
		}
		try {
			return RequestServiceImpl.openTermDepositAccount(request);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(e.getMessage());
			response.setSuccess(false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(e.getMessage());
			response.setSuccess(false);
		}
		return response;
	}

	@GET
	@Path("/get32DigitAcctNo")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static DepositePrdCdList get32DigitAcctNo(@QueryParam(value = "param1") String acctNo15Digit) {
		DepositePrdCdList list = new DepositePrdCdList();
		list.setSuccess(true);
		list.setResponse(DataUtils.get32DigitAcctNo(acctNo15Digit));
		return list;
	}

	@GET
	@Path("/get15DigitAcctNo")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static DepositePrdCdList get15DigitAcctNo(@QueryParam(value = "param2") String acctNo15Digit,
			@QueryParam(value = "param1") int lbrCode) {
		DepositePrdCdList list = new DepositePrdCdList();
		list.setSuccess(true);
		list.setResponse(DataUtils.get15DigitAccountNumber(lbrCode, acctNo15Digit));
		return list;
	}

	@POST
	@Path("/CustomerDepostProducts")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static CustomerDepositePrdCdList fetchCustDeposite(@QueryParam(value = "param1") String custNo) {

		return RequestServiceImpl.getCustDepositeProductList(custNo);
	}

	@POST
	@Path("/fetchAccStatDtlToGen")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static FetchNoOfStmtResponse fetchAccStatDtlToGen() {
		FetchNoOfStmtResponse response = new FetchNoOfStmtResponse();
		try {
			Session session = HBUtil.getSessionFactory().openSession();
			@SuppressWarnings("deprecation")
			Criteria criteria = session.createCriteria(D350023.class);
			criteria.add(Restrictions.eq("status", 0));
			List<D350023> result = criteria.list();
			List<D350023> finalResult = new ArrayList<D350023>();
			for (D350023 data : result) {
				if (data != null) {
					data.getId().setAcctNo(
							DataUtils.get15DigitAccountNumber(data.getId().getBrCode(), data.getId().getAcctNo()));
					finalResult.add(data);
				}

			}
			// String queryString1 ="SELECT * from D350023";
			// Query q1 = session.createQuery(queryString1);
			// List<D350023> result = q1.list();
			response.setD350023s(finalResult);
			response.setResp("Sucess");
			response.setErrorMsg("Sucess");
			response.setFlag(true);
			session.close();
		} catch (Exception sql) {
			response.setResp("Faile");
			response.setErrorMsg("Faile");
			response.setFlag(false);
			sql.printStackTrace();
		}
		return response;
	}

	@SuppressWarnings("deprecation")
	@POST
	@Path("/fetchStatRecord")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static FetchDetailedStmtRespose fetchDetailedStmt(@QueryParam(value = "acctNo") String accNo,
			@QueryParam(value = "fromDate") String fromDates, @QueryParam(value = "toDate") String toDates,
			@QueryParam(value = "lbrCd") String lbrCd, @QueryParam(value = "custId6") String custNo)
			throws ParseException {
		FetchDetailedStmtRespose details = new FetchDetailedStmtRespose();
		Date fromDate = new Date();
		Date toDate = new Date();
		try {
			fromDate = new Date(Long.parseLong(fromDates)); // yes, I know this is a deprecated method
			toDate = new Date(Long.parseLong(toDates)); // yes, I know this is a deprecated method
			System.out.println(fromDate + " " + toDate);
		} catch (Exception e) {
			// fromDate = CRAZY_FORMAT.parse(fromDates);
			// toDate = CRAZY_FORMAT.parse(fromDates);
			e.printStackTrace();
		}

		if (accNo == null || accNo.length() > 16 || accNo.length() < 15) {
			details.setResp(MSGConstants.ERROR);
			details.setErrorMsg(MSGConstants.INVALID_ACCOUNT_NO);
			return details;
		}

		if (DateUtil.getDateDiff(toDate, new java.util.Date())) {
			details.setResp(MSGConstants.ERROR);
			details.setErrorMsg(MSGConstants.TO_DATE_TODATE_NOT_SAME);
			return details;
		}
		if (DateUtil.getDateDiff(fromDate, new java.util.Date())) {
			details.setResp(MSGConstants.ERROR);
			details.setErrorMsg(MSGConstants.FROM_DATE_TODATE_NOT_SAME);
			return details;
		}

		try {
			return AccountStatement.fetchAccountStmtRequest(accNo, fromDate, toDate, custNo);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
			details.setResp(MSGConstants.ERROR);
			details.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			return details;
		}

	}

	@POST
	@Path("/updateAccStatDetails")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static AcctStatDetailsStatusResponse acctStatDetailsStatus(@QueryParam(value = "refNo") String rrn,
			@QueryParam(value = "status") int status, @QueryParam(value = "fileName") String fileName) {
		AcctStatDetailsStatusResponse response = new AcctStatDetailsStatusResponse();
		logger.error("billPayementStatus.service()--> RRN::>>" + rrn + " STATUS::>" + status);
		try {
			if (rrn == null) {
				response.setResp(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_RRN_NO);
				response.setFlag(false);
				return response;
			}
			response = DataUtils.updateAcctStatDetailsStatus(rrn, status, fileName);
			logger.error("acctStatDetailsStatus.service()--> Response::>>" + new Gson().toJson(response));
			return response;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("", e);
			response.setResp(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
	}

	@POST
	@Path("/fetchRDAcctDetails")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static RDAcctDetails fetchRDAcctDetails(@QueryParam(value = "param1") String custNo,
			@QueryParam(value = "param2") int acctNo) {

		RDAcctDetails list = new RDAcctDetails();

		CustomerDetails response = RequestServiceImpl.validCustomer(custNo);

		if (response != null && response.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {

			list.setSuccess(false);
			list.setResponse(response.getResponse());
			list.setErrorMsg(response.getErrorMsg());
			return list;
		}

		List<Object[]> newList = new ArrayList<Object[]>();
		newList = RequestServiceImpl.getRDAcctList(custNo, acctNo);
		List<RDParameters> productList = new ArrayList<RDParameters>();
		if (newList != null) {
			try {
				for (int i = 0; i < newList.size(); i++) {

					Object[] product = newList.get(i);

					RDParameters prodMap = new RDParameters();
					prodMap.setInstAmt((Double) product[5]);
					prodMap.setLbrCode((int) product[0]);
					prodMap.setPrcdAcctId(DataUtils.get15DigitAccountNumber((int) product[0], product[1] + "00000000"));
					prodMap.setIntRate((Double) product[4]);
					prodMap.setMainBal((Double) product[7]);
					prodMap.setMatDate((Date) product[6]);
					prodMap.setNoInst(((Short) product[3]).intValue());
					prodMap.setPendInst((((Double) product[8]).intValue()));
					prodMap.setReciptNo((int) product[2]);
					prodMap.setCustName(product[9].toString().trim());
					prodMap.setCustNo(custNo);
					productList.add(prodMap);

				}
			} catch (Exception e) {
				e.printStackTrace();
				list.setSchemeCodelist(productList);
				list.setSuccess(false);
				list.setResponse(MSGConstants.ERROR);
				list.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			}
			list.setSchemeCodelist(productList);
			list.setSuccess(true);
			list.setResponse("SUCCESS");
			list.setErrorMsg(MSGConstants.SUCCESS_MSG);

		} else {
			list.setSchemeCodelist(productList);
			list.setSuccess(false);
			list.setResponse(MSGConstants.ERROR);
			list.setErrorMsg(MSGConstants.RECORD_NOT_FOUND);
		}
		return list;
	}

	@POST
	@Path("/searchRDAcct")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static PigmeAccountsResponse searchRDAcct(@QueryParam(value = "param1") String accNo,
			@QueryParam(value = "param2") String name, @QueryParam(value = "param3") String depositAccNo) {
		PigmeAccountsResponse response = new PigmeAccountsResponse();
		if (RequestServiceImpl.isBlankOrNull(accNo)) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.INVALID_ACCOUNT_NO);
			return response;
		}
		if (RequestServiceImpl.isBlankOrNull(name) && RequestServiceImpl.isBlankOrNull(depositAccNo)) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.INVALID_PARAMETERS);
			return response;
		}
		if (!RequestServiceImpl.isBlankOrNull(depositAccNo)) {
			if (depositAccNo.length() > 8) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_DEPOSIT_ACC_NO);
				return response;
			}
		}
		try {
			response = RequestServiceImpl.searchRDAcc(accNo, name != null ? name.trim() : "", depositAccNo.trim());
			List<Object[]> newList = new ArrayList<Object[]>();
			newList = RequestServiceImpl.getRDAcctDetails(Integer.parseInt(response.getAccList().get(0).getBrCode()),
					response.getAccList().get(0).getAccNo());
			List<RDParameters> productList = new ArrayList<RDParameters>();
			if (newList != null) {
				try {
					for (int i = 0; i < newList.size(); i++) {

						Object[] product = newList.get(i);
					}
				} catch (Exception ex) {

				}
			}
			return response;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}

	}

	@POST
	@Path("/creditAccountValidate")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public IMPSTransactionResponse creditAccountValidate(@QueryParam(value = "param1") String accNo15Digit,
			@QueryParam(value = "param2") String ifscCode, @QueryParam(value = "param3") String remitterMobile,
			@QueryParam(value = "param4") String remitterMMID, @QueryParam(value = "param5") String remitterAccNo,
			@QueryParam(value = "param6") String transAmt) {
		IMPSTransactionResponse response = new IMPSTransactionResponse();
		System.out.println("Recived Accno::>>" + accNo15Digit);
		if (null == accNo15Digit || accNo15Digit.trim().length() < 15) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
			response.setErrorMessage("INVALID PARAMETERS.");
			return response;
		}
		if (remitterMobile.trim().length() != 12) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
			response.setErrorMessage(MSGConstants.INVALID_BEN_MOB_NO);
			logger.error(MSGConstants.INVALID_BEN_MOB_NO);
			logger.info("IMPS P2A Account Validation Response::>>" + new Gson().toJson(response));
			return response;
		}

		if (remitterMMID.trim().length() != 7) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_REMITTER_MMID);
			response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
			logger.error(MSGConstants.INVALID_REMITTER_MMID);
			logger.info("IMPS P2A Account Validation Response::>>" + new Gson().toJson(response));
			return response;
		}
		if (null == remitterAccNo || remitterAccNo.trim().length() < 10) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_REMITTER_ACC_NO);
			response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
			logger.error(MSGConstants.INVALID_REMITTER_ACC_NO);
			logger.info("IMPS P2A Account Validation Response::>>" + new Gson().toJson(response));
			return response;
		}

		if (ifscCode.trim().length() != 11) {
			response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_BEN_IFSC);
			logger.error(MSGConstants.INVALID_BEN_IFSC);
			logger.info("IMPS P2A Account Validation Response::>>" + new Gson().toJson(response));
			return response;
		}
		// Add by Aniket Desai on 10th Dec, 2019 -Ben Tran declined with M1 due to IFSC
		if (!ifscCode.trim().contains(ConfigurationLoader.getParameters(false).getProperty("IFSC"))
				&& !ifscCode.trim().contains(ConfigurationLoader.getParameters(false).getProperty("IFSC1"))) {
			response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_BEN_IFSC);
			logger.error(MSGConstants.INVALID_BEN_IFSC);
			logger.info("IMPS P2A Account Validation Response::>>" + new Gson().toJson(response));
			return response;
		}

		if (Double.parseDouble("" + transAmt) == 0) {
			response.setErrorCode(ResponseCodes.INSUFFICIENT_FUNDS);
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_AMOUNT);
			logger.error(MSGConstants.INVALID_AMOUNT);
			logger.info("IMPS P2A Account Validation Response::>>" + new Gson().toJson(response));
			return response;
		}
		if ((Double.parseDouble("" + transAmt)) > Double
				.valueOf(ConfigurationLoader.getParameters(false).getProperty("MAX_AMOUNT"))) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.EXCEED_DAILY_LIMIT);
			// response.setRrnNo(request.getRRNNo());
			response.setErrorCode("M2");
			logger.info("IMPS P2A Account Validation Response::>>" + new Gson().toJson(response));
			return response;
		}
		D009022 d009022 = null;
		try {
			d009022 = DataUtils.getAccount(accNo15Digit);
			if (d009022 == null) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				response.setErrorMessage(MSGConstants.INVALID_REMITTER_ACC_NO);
				logger.info("IMPS Account Validation Response::>>" + new Gson().toJson(response));
				return response;

			}

		} catch (Exception e) {
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
			response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			logger.info("IMPS Account Validation Response::>>" + new Gson().toJson(response));
			return response;
		}

		try {

			TransactionValidationResponse res = TransactionServiceImpl.validateAccount(d009022, "10", "C");
			if (res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorCode(res.getRespCode());
				response.setErrorMessage(res.getErrorMsg());
				logger.info("IMPS Account Validation Response::>>" + new Gson().toJson(response));
				return response;
			}
			response.setResponse(MSGConstants.SUCCESS);
			response.setErrorMessage(MSGConstants.SUCCESS_MSG);
			response.setErrorCode("00");
			logger.info("IMPS Account Validation Response::>>" + new Gson().toJson(response));
			// response.setAccNo(d009022.getId().getPrdAcctId());
			// response.setLongName(d009022.getLongName().trim());
			response.setNickNameCredit(d009022.getLongName().trim());
			// return AccountMasterServiceImpl.validateAccNo(accNo15Digit.trim());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
			response.setResponse(MSGConstants.ERROR);
			response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
			response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			// return response;
		}
		return response;
	}

	@POST
	@Path("/upiValidateAccount")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static CustomerDetails upiValidateAccount(@QueryParam(value = "param1") String accNo) {
		
		logger.error("UPI Account Validation Request::>>" + accNo);
		
		CustomerDetails response = new CustomerDetails();
		if (accNo == null || accNo.trim().length() != 15) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.INVALID_ACCOUNT_NO);
			return response;
		}

		D009022 d009022 = null;
		try {
			d009022 = DataUtils.getAccount(accNo);
			if (d009022 == null) {
				response.setResponse(MSGConstants.ERROR);
				// response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				response.setErrorMsg(MSGConstants.INVALID_REMITTER_ACC_NO);
				logger.error("UPI Account Validation Response::>>" + response.toString());
				return response;

			}

		} catch (Exception e) {
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			// response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			logger.error("UPI Account Validation Response::>>" + response.toString());
			return response;
		}

		try {

			TransactionValidationResponse res = TransactionServiceImpl.validateAccount(d009022, "10", "C");
			if (res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
				response.setResponse(MSGConstants.ERROR);
				// response.setErrorCode(res.getRespCode());
				response.setErrorMsg(res.getErrorMsg());
				logger.error("UPI Account Validation Response::>>" + response.toString());
				return response;
			}
			response.setResponse(MSGConstants.SUCCESS);
			response.setErrorMsg(MSGConstants.SUCCESS_MSG);
			// response.setErrorCode("00");
			logger.error("UPI Account Validation Response::>>" + response.toString());
			// response.setAccNo(d009022.getId().getPrdAcctId());
			response.setLongName(d009022.getLongName().trim());
			// response.setNickNameCredit(d009022.getLongName().trim());
			// return AccountMasterServiceImpl.validateAccNo(accNo15Digit.trim());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
			response.setResponse(MSGConstants.ERROR);
			// response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			// return response;
		}
		return response;
	}

	@POST
	@Path("/loanValidation")
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static IMPSFetchLoanAccountDetailsResponse loanAccountDetails(@QueryParam(value = "param1") String accNo) {
		System.out.println("Fetch Loan Accounts" + accNo);
		IMPSFetchLoanAccountDetailsResponse response = new IMPSFetchLoanAccountDetailsResponse();
		if (accNo == null || accNo.trim().length() != 15) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_ACCOUNT_NO);
			return response;
		}

		D009022 sourceAccount = DataUtils.getAccount(accNo);
		;
		// System.out.println("sourceAccount::>>>" + sourceAccount.toString());
		TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount, String.valueOf("10"),
				"A");
		System.out.println("res::>>>" + res);
		if (null == res) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
		if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage("Loan " + res.getErrorMsg());
			return response;
		}
		try {

			response = RequestServiceImpl.loanAccountDetailsForRDCC(sourceAccount.getId().getLbrCode(),
					sourceAccount.getId().getPrdAcctId());
			String balance = response.getAccountList().get(0).getBalance() + " DR";
			if (response.getAccountList().get(0).getBalance().contains("-"))
				response.getAccountList().get(0).setBalance(balance.replace("DR", "CR"));
			else
				response.getAccountList().get(0).setBalance(balance);
			System.out.println("response::>>" + response);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
		return response;
	}
}
