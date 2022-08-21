package com.sil.commonswitch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import com.sil.constants.MSGConstants;
import com.sil.constants.ResponseCodes;
import com.sil.dao.LookUpServiceImpl;
import com.sil.domain.TransactionValidationResponse;
import com.sil.hbm.D001002;
import com.sil.hbm.D001003;
import com.sil.hbm.D001004;
import com.sil.hbm.D009011;
import com.sil.hbm.D009021;
import com.sil.hbm.D009022;
import com.sil.hbm.D009044;
import com.sil.hbm.D010003;
import com.sil.hbm.D010009;
import com.sil.hbm.D010009Id;
import com.sil.hbm.D010053;
import com.sil.hbm.D010054;
import com.sil.hbm.D010080;
import com.sil.hbm.D010080Id;
import com.sil.hbm.D010103;
import com.sil.hbm.D350078;
import com.sil.hbm.D500028;
import com.sil.prop.ConfigurationLoader;
import com.sil.service.RequestServiceImpl;
import com.sil.service.TransactionServiceImpl;
import com.sil.util.AccountDetailsUtil;
import com.sil.util.DateUtil;
import com.sil.util.HBUtil;

public class ChequeBookRequest {
	public	static org.apache.log4j.Logger logger=org.apache.log4j.Logger.getLogger(ChequeBookRequest.class);
	private static Map<Long, String> area101003 = new HashMap<Long, String>();
	public static final ArrayList<Byte> accTypeList = new ArrayList<Byte>();
	public static void main(String[] args) {
//		logger.error(chequeBookRequest("003001000006707", "1", "1", "1"));
//		logger.error(insertD010009("002010100005792", "1", "1", "1"));
//		logger.error("SRNO::>>"+getMaxSrno("2", "SB      000000000000579200000000"));
		logger.error("Start Instr No::>>"+getStartInsNoNew("3", 10L, "SA","25","1"));
//		logger.error("Book Size::>>"+ChequeBookIssued("2", "SB      000000000000579200000000", 1L).get(0).getBooksize());
//		logger.error(getmoduleType("2", "SB"));
//		logger.error("Record Present::>>"+checkRecordPresent("3", "SB      000000000000670700000000"));
//		D010080 d01008=getStartInsNo(""+3,Long.valueOf("11"), "SB      000000000000670700000000".substring(0, 8).trim());
		
//		logger.error("d01008::>>"+d01008);
//		int value=1;
//		if(d01008!=null)
//		{
//			value=Integer.valueOf(d01008.getStartInsNo().trim());	
//		}else
//		{
//			value=Integer.valueOf(String.format("%012d", value));
//		}
//		logger.error("value::>>"+value);
	}
	public static OtherChannelServiceResponse chequeBookRequest(String acctNo15Digit,String noOfCheqBook,String addrType,String delevaryType,String addYN,String custNo,String delevaryday,String channel)
	{
		D010080  d010080=new D010080();
		D010080Id d010080Id=new D010080Id();
		OtherChannelServiceResponse response=new OtherChannelServiceResponse();
		String acctno32digit=AccountDetailsUtil.get32DigitAcctNo(acctNo15Digit);
		if (addYN.trim().equalsIgnoreCase("") || ! new ArrayList<String>(Arrays.asList(MSGConstants.YES, MSGConstants.NO)).contains(addYN)) {
			logger.error("addYN Option should be a valid value.");
			response.setResponse(MSGConstants.ERROR);
			response.setErrorCode(MSGConstants.ADDYN_IS_BLANK);
			return response;
		}
		if(acctno32digit==null || acctno32digit.trim().equalsIgnoreCase(""))
		{
			response.setResponse(MSGConstants.ERROR);
			response.setErrorCode(MSGConstants.ACCOUNT_NOT_FOUND);
			return response; 
		}
		D009022 account = DataUtils.getAccount(acctNo15Digit);
		if(account==null)
		{
			response.setResponse(MSGConstants.ERROR);
			response.setErrorCode(MSGConstants.ACCOUNT_NOT_FOUND);
			return response;
		}
		custNo=""+account.getCustNo();
		TransactionValidationResponse res=TransactionServiceImpl.validateAccount(account, String.valueOf("10"),"CB");
		if(res!=null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR))
		{
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(res.getErrorMsg());
			return response;
		}	
		int lbrcode=Integer.parseInt(acctNo15Digit.substring(0, 3));
		logger.error("lbrcode : "+lbrcode+"\n No Of Cheque Book : "+noOfCheqBook+"\n Address Type: "+addrType+"\n Delevary Type: "+delevaryType);;
//		String bookSize="30";
//		String moduleType=getModuleType(""+lbrcode, acctno32digit.substring(0,8).trim());
//		logger.error("moduleType::>>"+moduleType);
//		int startInstNo=(Integer.valueOf(bookSize) * Integer.valueOf(noOfCheqBook));
//		logger.error("startInstNo::>>"+startInstNo);
//		int value= Integer.valueOf(getStartInsNo(""+lbrcode,Long.valueOf(moduleType), acctno32digit.substring(0, 8).trim()).getStartInsNo());
//		logger.error("value::>>>"+value);
//		=================
		if (Integer.valueOf(custNo) != account.getCustNo()) {
			boolean isJointHolder = DataUtils.checkJointAccountHolder(account.getCustNo(), Integer.valueOf(custNo), account.getId().getLbrCode());
			logger.error("isJointHolder::>>>"+isJointHolder);
			if (!isJointHolder) {
				logger.error("This facility is not extended to your account. Kindly contact home branch for further details.");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.FASCILITY_NOT_AVAILABILITY);
				return response;
			}
		}
		if (account.getChqBookYn()=='N') {
			logger.error("Cheque book facility is not avail for this account ");
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage("Cheque book facility is not avail for this account.");
			return response;
		}

		String productNo = DataUtils.getProductCode(acctno32digit);
		logger.error("productNo::>>>"+productNo);
		D009021 productMaster = DataUtils.getProductMaster(String.valueOf(lbrcode), productNo);
		logger.error("productMaster::>>>"+productMaster);
		if (null == productMaster) {
			logger.error("Product not found.");
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.PRODUCT_NOT_FOUND);
			return response;
		}
		
		int moduleType = Integer.valueOf(productMaster.getModuleType());
		logger.error("moduleType::>>>"+moduleType);
		if (!(MSGConstants.MODULE_TYPE_LIST).contains(Long.valueOf(moduleType))) {
			logger.error("Invalid Product.");
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_PRODUCT);
			return response;
		}
//		---------------
		String mobileNo = MSGConstants.NOT_USED_PROCESS;
		List<D350078> otherChannelDetailsList = DataUtils.getCustMobNo(account.getCustNo()+"");
		logger.error("otherChannelDetailsList::>>>"+otherChannelDetailsList);
		if (null != otherChannelDetailsList && !otherChannelDetailsList.isEmpty()) {
			mobileNo = otherChannelDetailsList.get(0).getId().getMobileNo();
		}
		if(addYN.equalsIgnoreCase(MSGConstants.YES))
		{
			if (!new ArrayList<String>(Arrays.asList("1", "2", "3", "4", "5")).contains(addrType)) {
				logger.error("Invalid address type.");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_ADDRESS_TYPE);
				return response;
			}
			String add1 = MSGConstants.BLANK_STRING;
			String add2 = MSGConstants.BLANK_STRING;
			String add3 = MSGConstants.BLANK_STRING;
			int area = 0;
			String cityCd = MSGConstants.BLANK_STRING;
			String pinCode = MSGConstants.BLANK_STRING;
			D009011 customer = RequestServiceImpl.getCustDetailsMaster(String.valueOf(account.getCustNo()));
			if (null == customer) {
				logger.error("Customer address not found.");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.ADDRESS_NOT_FOUND);
				return response;
			}
			logger.error("addrType::>>"+addrType);
			if (addrType.equalsIgnoreCase("1")) {	/** Customer address */
				add1 = customer.getAdd1();
				add2 = customer.getAdd2();
				add3 = customer.getAdd3();
				area = customer.getArea();
				cityCd = customer.getCityCd();
				pinCode = customer.getPinCode();
			} else if (addrType.equalsIgnoreCase("2")) {	/** Branch address */	
				D001003 branchMaster = DataUtils.getBranchMaster(Integer.valueOf(lbrcode));
				if (null == branchMaster) {
					logger.error("Branch address not found.");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.BRANCH_ADDRESS_NOT_FOUND);
					return response;
				}
				add1 = branchMaster.getAdd1();
				add2 = branchMaster.getAdd2();
				add3 = branchMaster.getAdd3();
				area = branchMaster.getArea();
				cityCd = branchMaster.getCityCd();
				pinCode = branchMaster.getPinCode()+"";
				mobileNo = branchMaster.getTele1()+"";
			} else if (addrType.equalsIgnoreCase("3")
					|| addrType.equalsIgnoreCase("4")
					|| addrType.equalsIgnoreCase("5")) {
				int subAddType = 0;
				if (addrType.equalsIgnoreCase("3")) {	/** Present address */
					subAddType = 2;
				} else if (addrType.equalsIgnoreCase("4")) {	/** Permanent address */
					subAddType = 1;
				} else if (addrType.equalsIgnoreCase("5")) {	/** Office address */
					subAddType = 6;
				}
				
				D010054 accountAddressDetails = DataUtils.getAddressDetails(account.getId().getLbrCode(), account.getId().getPrdAcctId(), subAddType);
				logger.error("accountAddressDetails::>>"+accountAddressDetails);
				if (null == accountAddressDetails) {
					logger.error("Account address not found.");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.ACCOUNT_ADDRESS_NOT_FOUND);
					return response;
				}
				add1 = accountAddressDetails.getAddr1();
				add2 = accountAddressDetails.getAddr2();
				add3 = accountAddressDetails.getAddr3();
				area = accountAddressDetails.getArea();
				cityCd = accountAddressDetails.getCityCd();
				pinCode = accountAddressDetails.getPinCd()+"";
			}
			
			response.setValid(true);
			response.setCustNo(Long.toString(account.getCustNo()));
			
			List<D001002> list=new LookUpServiceImpl().getArea();
			logger.error("getArea List::>>"+list);
			logger.error("getArea List::>>"+list);
			Map<Long, String> areaLookUp = DataUtils.fetchNumericCodeList(list);
			logger.error("areaLookUp.toString()::>>"+areaLookUp.toString());
			logger.error("areaLookUp.toString()::>>"+areaLookUp.toString());
			logger.error("area::>>>"+area);
			String areaDesc = areaLookUp.get(Long.valueOf(area));
			logger.error("areaDesc::>>"+areaDesc);
			D500028 cityDesc = DataUtils.getDetails(cityCd);
			logger.error("cityDesc::>>"+cityDesc);
			response.setResponse(MSGConstants.SUCCESS);
			response.setErrorMessage(MSGConstants.SUCCESS_MSG);
			response.setErrorCode(ResponseCodes.SUCCESS);
			response.setCustNo(custNo.trim());
			if(areaDesc == null || areaDesc.trim().equalsIgnoreCase("")) 
				areaDesc=MSGConstants.NOT_USED_PROCESS;
			response.setOutput(new String[] {add1.trim(), add2.trim(), add3.trim(), area+"", areaDesc.trim(), customer.getPagerNo().trim(), customer.getPhone().trim(), cityCd.trim(), cityDesc == null ? MSGConstants.NOT_USED_PROCESS : cityDesc.getPlaceCdDesc().trim(), pinCode.trim()});
			response.setMobileNo(mobileNo.trim());
			return response;
		}
		
//		========================
		
		String maxNoOfChqBookPerReq = ConfigurationLoader.getParameters(false).getProperty("maxNoOfChqBook_Per_Req_moduleType_"+moduleType);
		logger.error("maxNoOfChqBookPerReq::>>"+maxNoOfChqBookPerReq);
		logger.error("maxNoOfChqBookPerReq::>>"+maxNoOfChqBookPerReq);
		if (!maxNoOfChqBookPerReq.trim().equalsIgnoreCase("")
				&& moduleType == MSGConstants.SAVING
				&& Integer.parseInt(noOfCheqBook) >Integer.parseInt(maxNoOfChqBookPerReq)) {
			logger.error("You are requesting more than prescribed limit.");
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.CHQ_LIMIT_EXCEED);
			return response;
		}
		if (delevaryday.trim().equalsIgnoreCase("") || !new ArrayList<String>(Arrays.asList("1", "6")).contains(delevaryday)) {
			logger.error("Day is not in valid form.");
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_DELEVERY_DAY);
			return response;
		}
		

		/** Calculate one month before date from current date */
		int incrementVal = Integer.parseInt(ConfigurationLoader.getParameters(false).getProperty("maxNoOfDays_For_Unused_Chq"));
		Calendar cal = DateUtil.addingDateTime(new Date(), -incrementVal, "Days");
		Date result = cal.getTime();
		Date fromDate =new Date() ;//result;
		Date toDate = new Date();
	
		int chqBookCount = 0;
		List<D010080> cheqBookFileGenList = DataUtils.ChequeBookFileGenerationList(String.valueOf(lbrcode), acctno32digit, fromDate, toDate);
		if (null != cheqBookFileGenList && !cheqBookFileGenList.isEmpty()) {
			for (D010080 chqBookFileGen: cheqBookFileGenList) {
				chqBookCount += chqBookFileGen.getNoofCheqBook();
			}
		}
		
		String maxNoOfChqBook = ConfigurationLoader.getParameters(false).getProperty("maxNoOfChqBook_for_moduleType_"+moduleType);
		logger.error("maxNoOfChqBook::>>>"+maxNoOfChqBook);
		logger.error("maxNoOfChqBook::>>>"+maxNoOfChqBook);
		logger.error("chqBookCount::>>>"+chqBookCount);
		logger.error("noOfCheqBook::>>>"+noOfCheqBook);
		if (Integer.parseInt(maxNoOfChqBook) == 0) {
			logger.error("No of Cheque Book Greater than the defined limit");
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.NO_OF_CHQ_GREATER);
			return response;
		}
		if ((Integer.parseInt(noOfCheqBook) + chqBookCount) > Integer.parseInt(maxNoOfChqBook)) {
			logger.error("No of Cheque Book Greater than the defined limit.");
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.NO_OF_CHQ_GREATER);
			return response;
		}

		int unprocessLeaf = 0;
		int maxNoOfLeaves = Integer.valueOf(ConfigurationLoader.getParameters(false).getProperty("maxNoOfLeaves_for_moduleType_"+moduleType));
		if (moduleType != MSGConstants.SAVING) {
			unprocessLeaf = chqBookCount * 50;
			if (unprocessLeaf > maxNoOfLeaves) {
				logger.error("You have more than "+maxNoOfLeaves+" leaves availiable in your cheque book. Kindly visit your home branch for further queries");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage("You have more than "+maxNoOfLeaves+" leaves availiable in your cheque book. Kindly visit your home branch for further queries");
				return response;
			}
			Long chqReqCount = Long.parseLong(noOfCheqBook) * 50;
			unprocessLeaf += chqReqCount;
			if (unprocessLeaf > maxNoOfLeaves) {
				logger.error("You are requesting more than prescribed limit. Kindly visit your home branch for further queries.");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage("You are requesting more than prescribed limit. Kindly visit your home branch for further queries.");
				return response;
			}
		} else {
			unprocessLeaf = chqBookCount * 25;
			if (unprocessLeaf > maxNoOfLeaves) {
				logger.error("You have more than or equal to "+maxNoOfLeaves+" leaves availiable in your cheque book. Kindly visit your home branch for further queries");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage("You have more than or equal to "+maxNoOfLeaves+" leaves availiable in your cheque book. Kindly visit your home branch for further queries");
				return response;
			}
			int chqReqCount = Integer.valueOf(noOfCheqBook) * 25;
			unprocessLeaf += chqReqCount;
			if (unprocessLeaf > maxNoOfLeaves) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage("You are requesting more than prescribed limit. Kindly visit your home branch for further queries.");
				return response;
			}
		}
		
		Integer cheqBookLimit = 100;
		Integer chkMaxLeafLmt = 0;
		if (moduleType!= MSGConstants.SAVING) {
			chkMaxLeafLmt = cheqBookLimit * 2;
		} else {
			chkMaxLeafLmt = cheqBookLimit;
		}
		String insType = ConfigurationLoader.getParameters(false).getProperty("insType_for_moduleType_"+moduleType);
		List<D009044> IssuedInstList = DataUtils.fetchIssuedInstrumentsList(lbrcode, acctno32digit, insType, 0);
		int listCount = IssuedInstList == null ? 0 : IssuedInstList.size();
		logger.error("listCount::>>"+listCount);
		logger.error("listCount::>>"+listCount);
		logger.error("chkMaxLeafLmt::>>"+chkMaxLeafLmt);
		if (listCount >= chkMaxLeafLmt.intValue()) {
			logger.error("You are requesting more than prescribed limit.Kindly visit your home branch for further queries.");
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage("You are requesting more than prescribed limit.Kindly visit your home branch for further queries.");
			return response;
		}
		D010003 instrMaster = DataUtils.getInstrumentMasterDetails(Integer.valueOf(insType));
		if (null == instrMaster) {
			logger.error("Invalid code.");
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.RECORD_NOT_FOUND);
			return response;
		}
		
		if (instrMaster.getStockedYn()=='N') {
			logger.error("Instrument type is not stocked.");
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage("Instrument type is not stocked.");
			return response;
		}
		
		String bookSize = ConfigurationLoader.getParameters(false).getProperty("bookSize_for_moduleType_"+moduleType);
		List<D010103> InstrTypeList = DataUtils.fetchInstrumentTypeMaintenance(Integer.valueOf(insType), Integer.valueOf(bookSize));
		if (null == InstrTypeList || InstrTypeList.isEmpty()) {
			logger.error("Book Size Not Found In The Instrument Type Master");
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage("Book Size Not Found In The Instrument Type Master");
			return response;
		}
		
		List<D010009> chequeBookIssuedList = DataUtils.ChequeBookIssued(lbrcode, acctno32digit, 1);
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), Calendar.APRIL, 1);
		int chkLeafCount = 0;
		if (null != chequeBookIssuedList && !chequeBookIssuedList.isEmpty()) {
			for (D010009 chequeBookIssued : chequeBookIssuedList) {
				if (chequeBookIssued.getIssuedDate().compareTo(calendar.getTime()) < 0
						|| (chequeBookIssued.getDbtrAuthDone() != 1 || chequeBookIssued.getDbtrAuthNeeded() != 0)) {
					continue;
				}
				Long maxStartInsNo = Long.valueOf(chequeBookIssued.getId().getStartInsNo()) + chequeBookIssued.getBooksize();
				List<D009044> issuedInstrumentsList = DataUtils.fetchIssuedInstrumentsList(lbrcode, acctno32digit, insType,Long.valueOf(chequeBookIssued.getId().getStartInsNo()), maxStartInsNo, 0);
//				chkLeafCount += issuedInstrumentsList.size();
				chkLeafCount += (issuedInstrumentsList==null ||issuedInstrumentsList.isEmpty())?0:issuedInstrumentsList.size() ;
			}
		}
		chkLeafCount = chkLeafCount + unprocessLeaf;
		if (moduleType == MSGConstants.SAVING && chkLeafCount > 100) {
			logger.error("Your request could not be completed. Please contact your branch.");
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage("Your request could not be completed. Please contact your branch.");
			return response;
		} else if ((moduleType == MSGConstants.CURRENT
				|| moduleType == MSGConstants.OD) && chkLeafCount > 250) {
			logger.error("Your request could not be completed. Please contact your branch.");
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage("Your request could not be completed. Please contact your branch.");
			return response;		
		}
		
//====================
		Long startNo =0l;
		String alphCd="";
		try {
			D001004 systemParameter = DataUtils.getSystemParameter(lbrcode, "IMPSCHQALPHBRCD");
			if(systemParameter!=null) {
				String[] values = systemParameter.getValue().trim().split("~");
				for(String data: values) {
					String[] productData = data.split(":");
					if(productData[0].trim().equalsIgnoreCase(moduleType+"")) {
						alphCd = productData[1].trim();
						break;
					}
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		if(alphCd.equalsIgnoreCase(""))
		alphCd=(ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD_FLAG").trim().equalsIgnoreCase("Y"))
				?ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD")
						:ConfigurationLoader.getParameters(false).getProperty("alphcd_for_moduleType_"+moduleType).trim();
		D010080 chequeBookFileGeneration =null;
		if(ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_STARTINSNO_WITH_ACCTNO_YN").trim().equalsIgnoreCase("N"))
			chequeBookFileGeneration=getStartInsNo(""+lbrcode,Long.valueOf(insType), alphCd);
		else
			chequeBookFileGeneration = getStartInsNoWithAcct(""+lbrcode,Long.valueOf(insType), String.format("%1$-4s", alphCd), acctno32digit);
		
		logger.error("d01008::>>"+chequeBookFileGeneration);
		Long value=1l;
		Long startInsNoNew=1l,bSize = 1l,noofCheqBook=1l;
		if(chequeBookFileGeneration==null)
			startInsNoNew=1l;
		else
		{
			bSize=Long.valueOf(chequeBookFileGeneration.getBooksize()+"");
			noofCheqBook=Long.valueOf(chequeBookFileGeneration.getNoofCheqBook()+"");
			startNo = Long.valueOf(chequeBookFileGeneration.getStartInsNo().trim());
			System.out.println("startNo::>>"+startNo);
			System.out.println("bSize::>>"+bSize);
			System.out.println("noofCheqBook::>>"+noOfCheqBook);
		}
		//Long startNo=ChequeBookRequest.getStartInsNoNew(""+lbrcode,Long.valueOf(insType), (ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD_FLAG").trim().equalsIgnoreCase("Y"))?ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD"):acctno32digit.substring(0,8).trim(), bookSize, noOfCheqBook);
		
		/*if(chequeBookFileGeneration==null)
			startInsNoNew=1l;
		else*/
			startInsNoNew= startNo+(bSize*noofCheqBook);
		
		String startInsNo=""+startInsNoNew;	
//		ChequeBookFileGeneration chequeBookFileGeneration = welcomeKitParameterService.getStartInsNo1(Long.valueOf(brCode), Long.parseLong(insType), alphaCode);
//		String startInsNo = "1";
		
		logger.error("New startInsNo::>>"+startInsNo);
//		logger.error("New InsNO::>>"+getStartInsNoNew(""+lbrcode,Long.valueOf(insType), acctno32digit.substring(0, 8).trim()));
		logger.error("bookSize::>>"+bookSize);
		logger.error("noOfCheqBook::>>"+noOfCheqBook);
//		startInsNo=""+getStartInsNoNew(""+lbrcode,Long.valueOf(insType), (ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD_FLAG").trim().equalsIgnoreCase("Y"))?ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD"):acctno32digit.substring(0,8).trim());
		/*if(chequeBookFileGeneration != null) {
			try{
				startInsNo = String.valueOf(Long.parseLong(bookSize) * Long.parseLong(noOfCheqBook.trim()) +(Long.parseLong(chequeBookFileGeneration.getStartInsNo().trim())));
				logger.error("startInsNo::>>"+startInsNo);
				logger.error("====================================");
				logger.error("startInsNo::>>"+startInsNo);
				logger.error("bookSize::>>"+bookSize);
				logger.error("noOfCheqBook::>"+noOfCheqBook);
				logger.error("Start InsNo"+chequeBookFileGeneration.getStartInsNo());
				logger.error("====================================");
			}catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		String modeOfOperation= String.format("%09d",Long.valueOf(account.getModeOprn()));
		logger.error("modeOfOperation::>>"+modeOfOperation);
		System.out.println("modeOfOperation::>>"+modeOfOperation);
		D001002 d001002= DataUtils.getLookUp(1066, modeOfOperation);
		boolean isjointHolder=true;
		if(modeOfOperation.trim().equalsIgnoreCase("000000001"))
			isjointHolder=false;
		if(d001002==null)
		{
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage("Look up parameters are not set properly.");
			return response;
		}
		if(d001002.getSecYn()=='N')
		{
			logger.error("This facility is not extended to your account. Kindly contact home branch for further details.");
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.FASCILITY_NOT_AVAILABILITY);
			return response;
		}
		char isJointHolder = MSGConstants.N;
		Long jtNameSrNo = 0L;
		char atpar = MSGConstants.N;
		if(isjointHolder)
		{
			D010053 d010053 = DataUtils.isJointHolder(lbrcode, acctno32digit, "1");//(Long.valueOf(brCode), accountNo, 1L);
			if (null != d010053) {
				isJointHolder = MSGConstants.Y;
				atpar = MSGConstants.Y;
				jtNameSrNo = 1L;
			}	
		}
		
//		logger.error("chequeBookFileGeneration.getStartInsNo()::>>"+chequeBookFileGeneration.getStartInsNo());
		logger.error("noOfCheqBook::>>>"+noOfCheqBook);
		logger.error("startInsNo::>>"+startInsNo);
		logger.error("AMAR startInsNo::>>"+startInsNo);
		logger.error("bookSize::>>"+bookSize);
		/*if(d01008!=null)
		{
			value= Integer.valueOf(d01008.getStartInsNo().trim());	
		}else
		{
			value=Integer.valueOf(String.format("%012d", value));
		}*/
		logger.error("value::>>"+value);
		Long startCheqInstrNo=Long.valueOf(startInsNo.trim());
		logger.error("startCheqInstrNo::>>"+startCheqInstrNo);
//		startCheqInstrNo=String.format("%60d", startCheqInstrNo);
		if(checkRecordPresent(""+lbrcode, acctno32digit))
		{
			logger.error("<<<< Record Present >>>>>");
			d010080Id.setAcctId(acctno32digit);
			d010080Id.setEffDate(DateUtil.getCurrentDate());
			d010080Id.setLbrCode(lbrcode);
			d010080Id.setSrNo(Integer.valueOf(noOfCheqBook)+getMaxSrno(""+lbrcode, acctno32digit));
			d010080.setAcctAddrType(Byte.valueOf(addrType));
			d010080.setAddrType(Byte.valueOf(addrType));
			d010080.setAlphaCd(alphCd);
			//d010080.setAlphaCd((ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD_FLAG").trim().equalsIgnoreCase("Y"))?ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD"):ConfigurationLoader.getParameters(false).getProperty("alphcd_for_moduleType_"+moduleType));
			//d010080.setAlphaCd((ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD_FLAG").trim().equalsIgnoreCase("Y"))?ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD"):acctno32digit.substring(0,8).trim());
			d010080.setAtParYn(atpar);
			d010080.setBooksize(Short.valueOf(bookSize));
			d010080.setCourierOnDay(Byte.valueOf(delevaryType));
			d010080.setCreateDate(DateUtil.getCurrentDate());
			d010080.setDbtrAddCb(0);
			d010080.setDbtrAddCd(DateUtil.getCurrentDate());
			d010080.setDbtrAddCk(0);
			d010080.setDbtrAddCs(Short.valueOf("0"));
			d010080.setDbtrAddCt(new Date());
			d010080.setDbtrAddMb(0);
			d010080.setDbtrAddMd(new Date());
			d010080.setDbtrAddMk(0);
			d010080.setDbtrAddMs(Short.valueOf("0"));
			d010080.setStatus(Byte.valueOf("1"));
			d010080.setDbtrAddMt(new Date());
			d010080.setDbtrAuthDone(Byte.valueOf("1"));
			d010080.setDbtrAuthNeeded(Byte.valueOf("0"));
			d010080.setDbtrLhisTrnNo(0);
			d010080.setDbtrLupdCb(0);
			d010080.setDbtrLupdCd(new Date());
			d010080.setDbtrLupdCk(0);
			d010080.setDbtrLupdCs(Short.valueOf("0"));
			d010080.setDbtrLupdCt(new Date());
			d010080.setDbtrLupdMb(0);
			d010080.setDbtrLupdMd(new Date());
			d010080.setDbtrLupdMk(0);
			d010080.setDbtrLupdMs(Short.valueOf("0"));
			d010080.setDbtrLupdMt(new Date());
			d010080.setDbtrRecStat(Byte.valueOf("0"));
			d010080.setStartInsNo(String.format("%012d",startCheqInstrNo));/// start
			d010080.setSignTitle(Byte.valueOf("0"));
			d010080.setOstartInsNo(String.format("%012d",Long.valueOf(startInsNo.trim()))); //instno
			d010080.setOlbrCode(lbrcode);
			if(chequeBookFileGeneration!=null) {
				d010080.setOissuedTo(chequeBookFileGeneration.getId().getAcctId());
				d010080.setOissuedDate(chequeBookFileGeneration.getId().getEffDate());
				d010080.setOalphaCd(chequeBookFileGeneration.getAlphaCd());
			}else {
				d010080.setOissuedTo(acctno32digit);
				d010080.setOissuedDate(new Date());
				d010080.setOalphaCd((ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD_FLAG").trim().equalsIgnoreCase("Y"))?ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD"):acctno32digit.substring(0,8).trim());
			}
			//d010080.setOissuedTo(acctno32digit);
			//d010080.setOissuedDate(new Date());
			d010080.setOinsType(Short.valueOf(""+insType));
			d010080.setOendInsNo(String.format("%012d", (startCheqInstrNo.intValue()-1l+(Integer.valueOf(bookSize)*Integer.valueOf(noOfCheqBook)))));//
			//d010080.setOalphaCd((ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD_FLAG").trim().equalsIgnoreCase("Y"))?ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD"):acctno32digit.substring(0,8).trim());
			d010080.setNoofCheqBook(Integer.valueOf(noOfCheqBook));
			d010080.setJtNameSrNo2(Short.valueOf("0"));
			d010080.setJtNameSrNo1(Short.valueOf("0"));
			d010080.setJtNameSrNo(Short.valueOf(""+jtNameSrNo));
			d010080.setJtHolderYn(isJointHolder);
			d010080.setInsType(Short.valueOf(""+insType));
			d010080.setInsPrdCd((ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD_FLAG").trim().equalsIgnoreCase("Y"))?ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD"):acctno32digit.substring(0,8).trim());
			d010080.setId(d010080Id);
			d010080.setDbtrUpdtChkId(Short.valueOf("0"));
			int oLbrCode = 0;
			String oIssuedTo =MSGConstants.NOT_USED_PROCESS;
			Date oIssuedDate = DateUtil.getDateFromStringNew("19000101");
			int oInsType = 0;
			String oAlphaCd = MSGConstants.NOT_USED_PROCESS;
			String oStartInsNo = "000000000000";
			String oEndInsNo = "000000000000";
		
			if (null != cheqBookFileGenList && !cheqBookFileGenList.isEmpty()) {
				oLbrCode = cheqBookFileGenList.get(0).getId().getLbrCode();
				oIssuedTo = cheqBookFileGenList.get(0).getId().getAcctId();
				oIssuedDate = cheqBookFileGenList.get(0).getId().getEffDate();
				oInsType = cheqBookFileGenList.get(0).getInsType();
				oAlphaCd = cheqBookFileGenList.get(0).getAlphaCd();
				oStartInsNo = cheqBookFileGenList.get(0).getStartInsNo();
				oEndInsNo = Long.toString(Long.parseLong(cheqBookFileGenList.get(0).getStartInsNo()) - 1 + cheqBookFileGenList.get(0).getBooksize());
				logger.error("oStartInsNo::>>"+oStartInsNo);
				logger.error("oEndInsNo::>>"+oEndInsNo);
				
//				d010080.setOstartInsNo(String.format("%012d",Long.valueOf(oStartInsNo))); //instno
//				d010080.setOlbrCode(oLbrCode);
//				d010080.setOissuedTo(oIssuedTo);
//				d010080.setOissuedDate(oIssuedDate);
//				d010080.setOinsType(Short.valueOf(""+oInsType));
//				d010080.setOendInsNo(String.format("%012d", Long.valueOf(oEndInsNo)));
//				d010080.setOalphaCd(oAlphaCd);
			} else if (null != chequeBookIssuedList && !chequeBookIssuedList.isEmpty()) {
				oLbrCode = chequeBookIssuedList.get(0).getId().getLbrCode();
				oIssuedTo = chequeBookIssuedList.get(0).getIssuedTo();
				oIssuedDate = chequeBookIssuedList.get(0).getIssuedDate();
				oInsType = chequeBookIssuedList.get(0).getId().getInsType();
				oAlphaCd = chequeBookIssuedList.get(0).getId().getAlphaCd();
				oStartInsNo = chequeBookIssuedList.get(0).getId().getStartInsNo();
				oEndInsNo = Long.toString(Long.parseLong(chequeBookIssuedList.get(0).getId().getStartInsNo()) - 1 + chequeBookIssuedList.get(0).getBooksize());
				logger.error("oEndInsNo::>>"+oEndInsNo);
//				d010080.setOstartInsNo(String.format("%012d",Long.valueOf(oStartInsNo))); //instno
//				d010080.setOlbrCode(oLbrCode);
//				d010080.setOissuedTo(oIssuedTo);
//				d010080.setOissuedDate(oIssuedDate);
//				d010080.setOinsType(Short.valueOf(""+oInsType));
//				d010080.setOendInsNo(String.format("%012d", Long.valueOf(oEndInsNo)));//
//				d010080.setOalphaCd(oAlphaCd);
			}
			String lists=MSGConstants.ACC_TYPE_LIST;
			if(lists!=null || lists.trim().length()>0)
			{
				String [] newList=lists.split(",");
				if(newList.length!=0)
				{
					for(int i=0;i<newList.length;i++)
						accTypeList.add(Byte.valueOf(newList[i]));
				}
			}
			if(accTypeList.contains(account.getAcctType()))
			{
				d010080.setSignTitle(Byte.valueOf("99"));
			}
			
			logger.error(d010080.toString());
			Session session = HBUtil.getSessionFactory().openSession();
			Transaction tx = session.beginTransaction();
			try{
				session.save(d010080);
				tx.commit();
				session.close();
				session=null;
				response.setResponse(MSGConstants.SUCCESS);
				response.setErrorMessage(MSGConstants.CHQ_BOOK_MSG);
				return response;
			} catch (Exception e) {
				e.printStackTrace();
				tx.rollback();
				tx=null;
				session=null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.ERROR_MSG);
				return response;
			}		
		}else
		{
			try {
				logger.error("<<<< Record Not Present >>>>>");
//				startInsNo=""+(Long.parseLong(bookSize) * Long.parseLong(noOfCheqBook.trim())+ChequeBookRequest.getStartInsNoNew(""+3,Long.valueOf(10), (ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD_FLAG").trim().equalsIgnoreCase("Y"))?ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD"):acctno32digit.substring(0,8).trim()));
//				startCheqInstrNo=Long.valueOf(startInsNo.trim());
//				startInsNo = Long.toString((Long.parseLong(bookSize) * Long.parseLong(noOfCheqBook.trim())) +(Long.parseLong(chequeBookFileGeneration.getStartInsNo().trim())));

				String res1= insertD010009(acctNo15Digit, noOfCheqBook, addrType, delevaryType,bookSize,insType,String.format("%012d", Long.valueOf(startInsNo.trim())));//
				if(res1.equalsIgnoreCase("00"))
				{
					d010080Id.setAcctId(acctno32digit);
					d010080Id.setEffDate(DateUtil.getCurrentDate());
					d010080Id.setLbrCode(lbrcode);
					d010080Id.setSrNo(Integer.valueOf(noOfCheqBook)+getMaxSrno(""+lbrcode, acctno32digit));
					d010080.setAcctAddrType(Byte.valueOf(addrType));
					d010080.setAddrType(Byte.valueOf(addrType));
					d010080.setAlphaCd(alphCd);
					//d010080.setAlphaCd((ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD_FLAG").trim().equalsIgnoreCase("Y"))?ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD"):acctno32digit.substring(0,8).trim());
					d010080.setAtParYn(atpar);
					d010080.setBooksize(Short.valueOf(bookSize));
					d010080.setCourierOnDay(Byte.valueOf(delevaryType));
					d010080.setCreateDate(DateUtil.getCurrentDate());
					d010080.setDbtrAddCb(0);
					d010080.setDbtrAddCd(DateUtil.getCurrentDate());
					d010080.setDbtrAddCk(0);
					d010080.setDbtrAddCs(Short.valueOf("0"));
					d010080.setDbtrAddCt(new Date());
					d010080.setDbtrAddMb(0);
					d010080.setDbtrAddMd(new Date());
					d010080.setDbtrAddMk(0);
					d010080.setDbtrAddMs(Short.valueOf("0"));
					d010080.setStatus(Byte.valueOf("1"));
					d010080.setDbtrAddMt(new Date());
					d010080.setDbtrAuthDone(Byte.valueOf("1"));
					d010080.setDbtrAuthNeeded(Byte.valueOf("0"));
					d010080.setDbtrLhisTrnNo(0);
					d010080.setDbtrLupdCb(0);
					d010080.setDbtrLupdCd(new Date());
					d010080.setDbtrLupdCk(0);
					d010080.setDbtrLupdCs(Short.valueOf("0"));
					d010080.setDbtrLupdCt(new Date());
					d010080.setDbtrLupdMb(0);
					d010080.setDbtrLupdMd(new Date());
					d010080.setDbtrLupdMk(0);
					d010080.setDbtrLupdMs(Short.valueOf("0"));
					d010080.setDbtrLupdMt(new Date());
					d010080.setDbtrRecStat(Byte.valueOf("0"));
					d010080.setStartInsNo(String.format("%012d",startCheqInstrNo));/// start
					d010080.setSignTitle(Byte.valueOf("0"));
					d010080.setOstartInsNo(String.format("%012d",startCheqInstrNo)); //instno
					d010080.setOlbrCode(lbrcode);
					d010080.setOissuedTo(acctno32digit);
					d010080.setOissuedDate(new Date());
					d010080.setOinsType(Short.valueOf(""+insType));
					d010080.setOendInsNo(String.format("%012d", (startCheqInstrNo.intValue()-1l+(Integer.valueOf(bookSize)*Integer.valueOf(noOfCheqBook)))));//
					d010080.setOalphaCd((ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD_FLAG").trim().equalsIgnoreCase("Y"))?ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD"):acctno32digit.substring(0,8).trim());
					d010080.setNoofCheqBook(Integer.valueOf(noOfCheqBook));
					d010080.setJtNameSrNo2(Short.valueOf("0"));
					d010080.setJtNameSrNo1(Short.valueOf("0"));
//					d010080.setJtNameSrNo(Short.valueOf("0"));
//					d010080.setJtHolderYn('N');
					
					d010080.setJtNameSrNo(Short.valueOf(""+jtNameSrNo));
					d010080.setJtHolderYn(isJointHolder);
					d010080.setInsType(Short.valueOf(""+insType));
					d010080.setInsPrdCd((ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD_FLAG").trim().equalsIgnoreCase("Y"))?ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD"):acctno32digit.substring(0,8).trim());
					d010080.setId(d010080Id);
					d010080.setDbtrUpdtChkId(Short.valueOf("0"));

					int oLbrCode = 0;
					String oIssuedTo =MSGConstants.NOT_USED_PROCESS;
					Date oIssuedDate = DateUtil.getDateFromStringNew("19000101");
					int oInsType = 0;
					String oAlphaCd = MSGConstants.NOT_USED_PROCESS;
					String oStartInsNo = "000000000000";
					String oEndInsNo = "000000000000";
					
					if (null != cheqBookFileGenList && !cheqBookFileGenList.isEmpty()) {
						oLbrCode = cheqBookFileGenList.get(0).getId().getLbrCode();
						oIssuedTo = cheqBookFileGenList.get(0).getId().getAcctId();
						oIssuedDate = cheqBookFileGenList.get(0).getId().getEffDate();
						oInsType = cheqBookFileGenList.get(0).getInsType();
						oAlphaCd = cheqBookFileGenList.get(0).getAlphaCd();
						oStartInsNo = cheqBookFileGenList.get(0).getStartInsNo();
						oEndInsNo = Long.toString(Long.parseLong(cheqBookFileGenList.get(0).getStartInsNo()) - 1 + cheqBookFileGenList.get(0).getBooksize());
	
//						d010080.setStartInsNo(String.format("%012d",oStartInsNo));/// start
//						d010080.setOstartInsNo(String.format("%012d",Long.valueOf(oStartInsNo))); //instno
//						d010080.setOlbrCode(oLbrCode);
//						d010080.setOissuedTo(oIssuedTo);
//						d010080.setOissuedDate(oIssuedDate);
//						d010080.setOinsType(Short.valueOf(""+oInsType));
//						d010080.setOendInsNo(String.format("%012d", Long.valueOf(oEndInsNo)));
//						d010080.setOalphaCd(oAlphaCd);
//						d010080.setAlphaCd((ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD_FLAG").trim().equalsIgnoreCase("Y"))?ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD"):acctno32digit.substring(0,8).trim());
						
					} else if (null != chequeBookIssuedList && !chequeBookIssuedList.isEmpty()) {
						oLbrCode = chequeBookIssuedList.get(0).getId().getLbrCode();
						oIssuedTo = chequeBookIssuedList.get(0).getIssuedTo();
						oIssuedDate = chequeBookIssuedList.get(0).getIssuedDate();
						oInsType = chequeBookIssuedList.get(0).getId().getInsType();
						oAlphaCd = chequeBookIssuedList.get(0).getId().getAlphaCd();
						oStartInsNo = chequeBookIssuedList.get(0).getId().getStartInsNo();
						oEndInsNo = Long.toString(Long.parseLong(chequeBookIssuedList.get(0).getId().getStartInsNo()) - 1 + chequeBookIssuedList.get(0).getBooksize());
						
//						d010080.setOstartInsNo(String.format("%012d",Long.valueOf(oStartInsNo))); //instno
//						d010080.setOlbrCode(oLbrCode);
//						d010080.setOissuedTo(oIssuedTo);
//						d010080.setOissuedDate(oIssuedDate);
//						d010080.setOinsType(Short.valueOf(""+oInsType));
//						d010080.setOendInsNo(String.format("%012d", Long.valueOf(oEndInsNo)));//
//						d010080.setOalphaCd(oAlphaCd);
//						d010080.setAlphaCd((ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD_FLAG").trim().equalsIgnoreCase("Y"))?ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD"):acctno32digit.substring(0,8).trim());
					}

					if(account.getAcctType()==Byte.valueOf("1") || account.getAcctType()==Byte.valueOf("2") || account.getAcctType()==Byte.valueOf("8") || account.getAcctType()==Byte.valueOf("10") || account.getAcctType()==Byte.valueOf("20") || account.getAcctType()==Byte.valueOf("47"))
					{
						d010080.setSignTitle(Byte.valueOf("99"));
					}
					Session session = HBUtil.getSessionFactory().openSession();
					Transaction tx = session.beginTransaction();
					try{
						session.save(d010080);
						tx.commit();
						session.close();
						session=null;
						response.setResponse(MSGConstants.SUCCESS);
//						response.setErrorMessage(MSGConstants.SUCCESS_MSG);
						response.setErrorMessage(MSGConstants.CHQ_BOOK_MSG);
						return response;
					} catch (Exception e) {
						e.printStackTrace();
						tx.rollback();
						tx=null;
						session=null;
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage(MSGConstants.ERROR_MSG);
						return response;
					}		
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				response.setResponse(MSGConstants.ERROR);
				response.setResponse(MSGConstants.ERROR_MSG);
				return response;
			}
		}
		return response;
	}
	
	public static String insertD010009(String acctNo15Digit,String noOfCheqBook,String addrType,String delevaryType,String bookSize,String insType,String startInsNo )
	{
		String acctno32digit=AccountDetailsUtil.get32DigitAcctNo(acctNo15Digit);
//		String insType = ConfigurationLoader.getParameters(false).getProperty("insType_for_moduleType_"+moduleType);
		int lbrcode=Integer.parseInt(acctNo15Digit.substring(0, 3));
		D010009Id  d010009Id=new D010009Id();
		D010009  d010009=new D010009();
//		String bookSize="30";
		String moduleType=getModuleType(""+lbrcode, acctno32digit.substring(0,8).trim());
//		int startInstNo=(Integer.valueOf(bookSize) * Integer.valueOf(noOfCheqBook));
//		D010080 d010080=getStartInsNo(""+lbrcode,Long.valueOf(moduleType), acctno32digit.substring(0, 8).trim());
//		D010080 chequeBookFileGeneration=getStartInsNo(""+lbrcode,Long.valueOf(insType), acctno32digit.substring(0, 8).trim());
		/*logger.error("d01008::>>"+chequeBookFileGeneration);
		String startInsNo = "1";
		if (chequeBookFileGeneration != null) {
			try{
				startInsNo = Long.toString((Long.parseLong(bookSize) * Long.parseLong(noOfCheqBook.trim())) + 1l+(Long.parseLong(chequeBookFileGeneration.getStartInsNo().trim())));
				logger.error("startInsNo::>>"+startInsNo);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		
//		logger.error("d010080::>>"+chequeBookFileGeneration);
		
//		int startCheqInstrNo=startInstNo+Long.valueOf(startInsNo).intValue();
//		logger.error("startCheqInstrNo::>>>"+startCheqInstrNo);
		d010009Id.setAlphaCd((ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD_FLAG").trim().equalsIgnoreCase("Y"))?ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD").trim():ConfigurationLoader.getParameters(false).getProperty("alphcd_for_moduleType_"+moduleType).trim());
		//d010009Id.setAlphaCd((ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD_FLAG").trim().equalsIgnoreCase("Y"))?ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD"):acctno32digit.substring(0,8).trim());
		d010009Id.setInsType(Short.valueOf(insType.trim()));
		d010009Id.setLbrCode(lbrcode);
		d010009Id.setStartInsNo(startInsNo);
		d010009.setBooksize(Short.valueOf(bookSize));//book size
		d010009.setCreateDate(new Date());
		d010009.setDbtrAddCb(0);
		d010009.setDbtrAddCd(new Date());
		d010009.setDbtrAddCk(0);
		d010009.setDbtrAddCs(Short.valueOf("0"));
		d010009.setDbtrAddCt(new Date());
		d010009.setDbtrAddMb(0);
		d010009.setDbtrAddMd(new Date());
		d010009.setDbtrAddMk(0);
		d010009.setDbtrAddMs(Short.valueOf("0"));
		d010009.setDbtrAddMt(new Date());
		d010009.setDbtrAuthDone(Byte.valueOf("1"));
		d010009.setDbtrAuthNeeded(Byte.valueOf("0"));
		d010009.setDbtrLhisTrnNo(0);
		d010009.setDbtrLupdCb(0);
		d010009.setDbtrLupdCd(new Date());
		d010009.setDbtrLupdCk(0);
		d010009.setDbtrLupdCs(Short.valueOf("0"));
		d010009.setDbtrLupdCt(new Date());
		d010009.setDbtrLupdMb(0);
		d010009.setDbtrLupdMd(new Date());
		d010009.setDbtrLupdMk(0);
		d010009.setDbtrLupdMs(Short.valueOf("0"));
		d010009.setDbtrLupdMt(new Date());
		d010009.setDbtrRecStat(Byte.valueOf("0"));
		d010009.setDbtrTauthDone(Short.valueOf("1"));
		d010009.setDbtrUpdtChkId(Short.valueOf("0"));
		d010009.setIssuedTo(acctno32digit);
		d010009.setId(d010009Id);
		d010009.setIssuedDate(new Date());
		d010009.setSplSeries(0);
		d010009.setStatus(Byte.valueOf("1"));
		Session session = HBUtil.getSessionFactory().openSession();	
		Transaction tx = session.beginTransaction();
		try{
			session.save(d010009);
			tx.commit();
			session.close();
			return "00";
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
			tx=null;
			session=null;
			return "99";
		}
	}
	@SuppressWarnings({ "deprecation", "unused", "unchecked" })
	public static Integer getMaxSrno(String lbrCode,String accNo)
	{
		D010080Id d010080Id=new D010080Id(Integer.valueOf(lbrCode),new Date(),accNo,0);
		List<D010080> lst;
		Integer srNo = null;
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(D010080.class);
		criteria.add(Restrictions.eq("id.lbrCode",Integer.valueOf(lbrCode)));
		criteria.add(Restrictions.eq("id.effDate", DateUtil.getCurrentDate()));
		criteria.add(Restrictions.eq("id.acctId", accNo));

		ProjectionList proj = Projections.projectionList();
		proj = proj.add(Projections.max("id.srNo"));
		criteria =criteria.setProjection(proj);
		lst = criteria.list();
		logger.error("lst.size()::>>"+lst.size());
		if(lst!=null && lst.size()>0)
		{
			if(null==lst.get(0))
				srNo=0;
			else
				srNo=Integer.valueOf(""+lst.get(0));
		}
		tx.commit();
		session.close();
		session=null;
		logger.error("srNo::>>>"+srNo);
		return srNo;
	}
	@SuppressWarnings({ "deprecation", "unchecked" })
	public static D010080 getStartInsNo(String lbrcode, Long instype, String alphacd)
	{
		Session session=HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D010080.class);
		criteria.add(Restrictions.eq("id.lbrCode",Integer.valueOf(lbrcode)));
		criteria.add(Restrictions.eq("insType", Short.valueOf(instype.toString())));
		criteria.add(Restrictions.eq("alphaCd", alphacd));
		//criteria.addOrder(Order.desc("id.lbrCode"));
		//criteria.addOrder(Order.desc("insType"));
		criteria.addOrder(Order.desc("startInsNo"));
		List<D010080> list =criteria.list();
		session.close();
		session=null;
		if (!list.isEmpty()) {
			logger.error("list.get(0).getOendInsNo()::>>>"+list.get(0).getStartInsNo());
			if(list.get(0).getStartInsNo()==null)
				return null;
			else
				return list.get(0);
		}
		return null;
	}
	public static Long getStartInsNoNew(String lbrcode, Long instype, String alphacd,String bookSize,String noOfCheqBook)
	{
		System.out.println("lbrcode::>>"+lbrcode);
		System.out.println("instype::>>"+instype);
		System.out.println("alphacd::>>"+alphacd);
		System.out.println("bookSize::>>"+bookSize);
		System.out.println("noOfCheqBook::>>>"+noOfCheqBook);
		
		Session session=HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D010080.class);
		criteria.setProjection(Projections.max("startInsNo"));
		criteria.add(Restrictions.eq("id.lbrCode",Integer.valueOf(lbrcode)));
		criteria.add(Restrictions.eq("insType", Short.valueOf(instype.toString())));
		criteria.add(Restrictions.eq("alphaCd", alphacd));
		/****Added By Aniket Desai On 29th July, 2019 for Start Inst. no. not proper issue***/
		criteria.add(Restrictions.eq("booksize", Short.valueOf(bookSize)));
		
		List<D010080> list =criteria.list();
		logger.error("List::>>"+list.get(0).getNoofCheqBook());
		session.close();
		session=null;
		if (!list.isEmpty() && list.get(0)!=null) {
			return Long.valueOf(list.get(0)+""); //Long.valueOf(list.get(0).getStartInsNo()==null?1l:Long.valueOf(list.get(0).getStartInsNo()+""));
		}
		return 1l;
	}
	@SuppressWarnings({ "deprecation", "unchecked" })
	public static List<D010009> ChequeBookIssued(String lbrCode, String prdAcctId, Long status)  {
		Session session=HBUtil.getSessionFactory().openSession();
		Criteria criteria=session.createCriteria(D010009.class);
		criteria.add(Restrictions.eq("id.lbrCode", Integer.valueOf(lbrCode)));
		criteria.add(Restrictions.eq("issuedTo", prdAcctId));
		criteria.add(Restrictions.eq("status", Byte.valueOf(status.toString())));
		List<D010009> chequeBookIssuedList = criteria.list();
		session.close();
		session=null;
		return chequeBookIssuedList;
	}
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	public static String getModuleType(String lbrCode,String prdCode)
	{
		Session session=HBUtil.getSessionFactory().openSession();
		Criteria criteria=session.createCriteria(D009021.class);
		criteria.add(Restrictions.eq("id.lbrCode",Integer.valueOf(lbrCode)));
		criteria.add(Restrictions.eq("id.prdCd", prdCode));
		List<D009021> lst= criteria.list();
		session.close();
		session=null;
		if(lst!=null && !lst.isEmpty())
			return ""+lst.get(0).getModuleType();
		else
			return null;
	}
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	public static boolean checkRecordPresent(String lbrCode,String accNo)
	{
		List<D010009> lst=null;
		Session session=HBUtil.getSessionFactory().openSession();
		Criteria criteria=session.createCriteria(D010009.class);
		criteria.add(Restrictions.eq("id.lbrCode", Integer.valueOf(lbrCode)));
		criteria.add(Restrictions.eq("issuedTo", accNo));
		lst=criteria.list();
		session.close();
		session=null;
		if(lst==null || lst.isEmpty())
			return false;
		else
			return true;
	}
	@SuppressWarnings({ "deprecation", "unchecked" })
	public static D010080 getStartInsNoWithAcct(String lbrcode, Long instype, String alphacd, String acctId)
	{
		try {
		Session session=HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D010080.class);
		criteria.add(Restrictions.eq("id.lbrCode",Integer.valueOf(lbrcode)));
		criteria.add(Restrictions.eq("insType", Short.valueOf(instype.toString())));
		criteria.add(Restrictions.eq("alphaCd", alphacd));
		if(ConfigurationLoader.getParameters(false).getProperty("DATABASE").equalsIgnoreCase("ORACLE")) {
			criteria.add(Restrictions.sqlRestriction("substr(this_.AcctId,17,8)='"+acctId.substring(16, 24)+"'"));
		}else {
			criteria.add(Restrictions.sqlRestriction("substring(this_.AcctId,17,8)='"+acctId.substring(16, 24)+"'"));
		}
		//criteria.addOrder(Order.desc("id.lbrCode"));
		//criteria.addOrder(Order.desc("insType"));
		criteria.addOrder(Order.desc("startInsNo"));
		List<D010080> list =criteria.list();
		session.close();
		session=null;
		if (!list.isEmpty()) {
			logger.error("list.get(0).getOendInsNo()::>>>"+list.get(0).getStartInsNo());
			if(list.get(0).getStartInsNo()==null)
				return null;
			else
				return list.get(0);
		}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

}
