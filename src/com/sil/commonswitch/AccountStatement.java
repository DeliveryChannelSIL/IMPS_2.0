package com.sil.commonswitch;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sil.constants.MSGConstants;
import com.sil.constants.RtgsNeftHostToHostConstants;
import com.sil.domain.CustomerDetails;
import com.sil.domain.FetchDetailedStmtRespose;
import com.sil.domain.TransactionValidationResponse;
import com.sil.hbm.D001003;
import com.sil.hbm.D001004;
import com.sil.hbm.D009011;
import com.sil.hbm.D009021;
import com.sil.hbm.D009021Id;
import com.sil.hbm.D009022;
import com.sil.hbm.D030002;
import com.sil.hbm.D030002Id;
import com.sil.hbm.D350023;
import com.sil.hbm.D350023Id;
import com.sil.hbm.D350078;
import com.sil.prop.ConfigurationLoader;
import com.sil.service.RequestServiceImpl;
import com.sil.service.TransactionServiceImpl;
import com.sil.util.AccountDetailsUtil;
import com.sil.util.Customer;
import com.sil.util.DateUtil;
import com.sil.util.DateUtility;
import com.sil.util.HBUtil;
import com.sil.util.PrintUtils;

public class AccountStatement {
	public	static org.apache.log4j.Logger logger=org.apache.log4j.Logger.getLogger(AccountStatement.class);
	public static void main(String[] args) {
		logger.error("Result::>>"+AccountStatement.accountStmtRequest("003001000006707", "20160101", "20160404","","",""));
	}
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static CustomerDetails accountStmtRequest(String acctNo15Digit, String fromdate ,String toDate,String custNo,String mmid,String channel)
	{
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		CustomerDetails customerDetails=new CustomerDetails();
		String acctno32digit=AccountDetailsUtil.get32DigitAcctNo(acctNo15Digit);
		int lbrcode=Integer.parseInt(acctNo15Digit.substring(0, 3));
		if(null==acctno32digit || acctno32digit.trim().equalsIgnoreCase(""))
		{
			session.close();
			session=null;
			customerDetails.setResponse(MSGConstants.ERROR);
			customerDetails.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
			return customerDetails;
		}
		if(channel.equalsIgnoreCase(MSGConstants.OTHER_CHANNEL))
		{
			mmid=MSGConstants.DEFAULT_MMID;
			D009022 sourceAccount=DataUtils.getAccount(acctNo15Digit.trim());
			TransactionValidationResponse res=TransactionServiceImpl.validateAccount(sourceAccount, String.valueOf("10"),"AS");
			if(res!=null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR))
			{
				session.close();
				session=null;
				customerDetails.setResponse(MSGConstants.ERROR);
				customerDetails.setErrorMsg(res.getErrorMsg());
				return customerDetails;
			}	
		}
		if(channel.equalsIgnoreCase(MSGConstants.IMPS_CHANNEL))
		{
			if(null==RequestServiceImpl.validateAccountNoCustNo(custNo,String.valueOf(lbrcode), acctno32digit))
			{
				session.close();
				session=null;
				customerDetails.setResponse(MSGConstants.ERROR);
				customerDetails.setErrorMsg(MSGConstants.INVALID_CUSTNO_ACCNO);
				return customerDetails;		
			}
		}
		logger.error("lbrcode : "+lbrcode+"\n from Date : "+fromdate+"\n 32 Digit AcctNo: "+acctno32digit);
		if(custNo==null || custNo.length()<1)
			custNo=getCustNo(lbrcode, acctno32digit);
		Criteria criteria=session.createCriteria(D350023.class);
		criteria.add(Restrictions.eq("id.custNo", custNo));
		criteria.add(Restrictions.eq("id.mmid", mmid));
		criteria.add(Restrictions.eq("id.fromDate", DateUtil.getUtilDate(fromdate)));
		criteria.add(Restrictions.eq("id.toDate", DateUtil.getUtilDate(toDate)));
		criteria.add(Restrictions.eq("id.entryDate", DateUtil.getCurrentDate()));
		criteria.add(Restrictions.eq("id.acctNo", acctno32digit));
		//criteria.add(Restrictions.eq("id.acctNo", acctno32digit));
		List<D350023> list = criteria.list();
		System.out.println("list.size()::>>"+list.size());
		if(list!=null && list.size()>0)
		{
			session.close();
			session=null;
			customerDetails.setResponse(MSGConstants.ERROR);
			customerDetails.setErrorMsg(MSGConstants.ALREADY_REQUESTED);
			return customerDetails;
		}
		Criteria crit=session.createCriteria(D350023.class);
		crit.add(Restrictions.eq("id.custNo", custNo));
		crit.add(Restrictions.eq("id.entryDate", DateUtil.getCurrentDate()));
		crit.add(Restrictions.eq("id.brCode",lbrcode ));
		crit.add(Restrictions.eq("id.acctNo", acctno32digit));
		crit.add(Restrictions.eq("id.entryDate", DateUtil.getCurrentDate()));
		List<D350023> list1=crit.list();
		System.out.println("list1.size()::>>"+list1.size());
		if(list1.size()>=Integer.valueOf(MSGConstants.ACCOUNT_STMT_LIMIT))
		{	
			session.close();
			session=null;
			customerDetails.setResponse(MSGConstants.ERROR);
			customerDetails.setErrorMsg(MSGConstants.EXCEED_DAILY_STMT_LIMIT);
			return customerDetails;
		}
		Customer customer=null;
		customer=getCustDetails(custNo);
		System.out.println("customer::>>"+customer);
		if(null==customer || null==customer.getEmail() || customer.getEmail().trim().equalsIgnoreCase(""))
		{
			session.close();
			session=null;
			customerDetails.setResponse(MSGConstants.ERROR);
			customerDetails.setErrorMsg(MSGConstants.EMAIL_NOT_UPDATED);
			return customerDetails;
		}
		D350023Id id=new D350023Id();
		id.setCustNo(custNo);
		id.setEntryDate(DateUtil.getCurrentDate());
		id.setFromDate(DateUtil.getUtilDate(fromdate));
		id.setToDate(DateUtil.getUtilDate(toDate));
		id.setMmid(mmid);
		D350023 d350023Id=new D350023();
		id.setAcctNo(acctno32digit);
		id.setBrCode(lbrcode);

		d350023Id.setDbtrAddCb(0);
		d350023Id.setDbtrAddCd(new Date());
		d350023Id.setDbtrAddCk(0);
		d350023Id.setDbtrAddCs(0);
		d350023Id.setDbtrAddCt(new Date());
		d350023Id.setDbtrAddMb(lbrcode);
		d350023Id.setDbtrAddMd(new Date());
		d350023Id.setDbtrAddMk(0);
		d350023Id.setDbtrAddMs(0);
		d350023Id.setDbtrAddMt(new Date());
		d350023Id.setDbtrAuthDone(1);
		d350023Id.setDbtrAuthNeeded(0);
		d350023Id.setDbtrLhisTrnNo(0);
		d350023Id.setDbtrLupdCb(0);
		d350023Id.setDbtrLupdCd(new Date());
		d350023Id.setDbtrLupdCk(0);
		d350023Id.setDbtrLupdCs(0);
		d350023Id.setDbtrAddCt(new Date());
		d350023Id.setDbtrLupdMb(0);
		d350023Id.setDbtrLupdMd(new Date());
		d350023Id.setDbtrLupdMk(0);
		d350023Id.setDbtrLupdMs(0);
		d350023Id.setDbtrLupdMt(new Date());
		d350023Id.setDbtrRecStat(0);
		d350023Id.setDbtrTauthDone(1);
		d350023Id.setDbtrUpdtChkId(0);
		d350023Id.setFileName(" ");
		id.setEmailId(customer!=null?customer.getEmail():" ");//email
		d350023Id.setEntryTime(new Date());
		id.setMobileNo(customer!=null?customer.getMobno():" ");//mobno
		d350023Id.setStatus(0);
		d350023Id.setDbtrLupdCt(new Date());
		System.out.println("Julian::>>"+DateUtility.getJulianDay(DateUtility.getDateFormat(new Date())));
		String refNo = DateUtility.getJulianDay(DateUtility.getDateFormat(new Date())) + DateUtility.getPostTime(new Date())+"";
		logger.error("Refrence No : "+refNo+"\n");
		id.setRefNo(refNo);
		d350023Id.setId(id);
		try{
			session.save(d350023Id);
			tx.commit();
			session.close();
			session=null;
			customerDetails.setResponse(MSGConstants.SUCCESS);
			customerDetails.setErrorMsg(MSGConstants.ACCOUNT_STMT_MSG +customer.getEmail().trim()+".");
			customerDetails.setEmailId(customer!=null?customer.getEmail().trim():"");
			System.out.println(MSGConstants.ACCOUNT_STMT_MSG +customer.getEmail());
			return customerDetails;
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
			tx=null;
			session.close();
			session=null;
			customerDetails.setResponse(MSGConstants.SUCCESS);
			customerDetails.setErrorMsg(MSGConstants.ACCOUNT_STMT_FAILS);
			return customerDetails;
		}
	}
	public static CustomerDetails loanStmtRequest(String acctNo15Digit, String fromdate ,String toDate,String custNo,String mmid,String channel)
	{
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		CustomerDetails customerDetails=new CustomerDetails();
		String acctno32digit=AccountDetailsUtil.get32DigitAcctNo(acctNo15Digit);
		String branch = acctNo15Digit.substring(0, 3);
		if(branch.equalsIgnoreCase("999"))
			branch = "9999";
		int lbrcode=Integer.parseInt(branch);
		if(null==acctno32digit || acctno32digit.trim().equalsIgnoreCase(""))
		{
			tx.commit();
			session.close();
			session=null;
			tx=null;
			customerDetails.setResponse(MSGConstants.ERROR);
			customerDetails.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
			return customerDetails;
		}
		if(channel.equalsIgnoreCase(MSGConstants.OTHER_CHANNEL))
		{
			mmid=MSGConstants.DEFAULT_MMID;
			D009022 sourceAccount=DataUtils.getAccount(acctNo15Digit.trim());
			TransactionValidationResponse res=TransactionServiceImpl.validateAccount(sourceAccount, String.valueOf("10"),"A");
			if(res!=null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR))
			{
				tx.commit();
				session.close();
				session=null;
				tx=null;
				customerDetails.setResponse(MSGConstants.ERROR);
				customerDetails.setErrorMsg(res.getErrorMsg());
				return customerDetails;
			}	
		}
		if(channel.equalsIgnoreCase(MSGConstants.IMPS_CHANNEL))
		{
			if(null==RequestServiceImpl.validateAccountNoCustNo(custNo,String.valueOf(lbrcode), acctno32digit))
			{
				session.close();
				session=null;
				tx=null;
				customerDetails.setResponse(MSGConstants.ERROR);
				customerDetails.setErrorMsg(MSGConstants.INVALID_CUSTNO_ACCNO);
				return customerDetails;		
			}
		}

		logger.error("lbrcode : "+lbrcode+"\n from Date : "+fromdate+"\n 32 Digit AcctNo: "+acctno32digit);
		if(custNo==null || custNo.length()<1)
			custNo=getCustNo(lbrcode, acctno32digit);

		Criteria criteria=session.createCriteria(D350023.class);
		criteria.add(Restrictions.eq("id.custNo", custNo));
		criteria.add(Restrictions.eq("id.mmid", mmid));
		criteria.add(Restrictions.eq("id.fromDate", DateUtil.getUtilDate(fromdate)));
		criteria.add(Restrictions.eq("id.toDate", DateUtil.getUtilDate(toDate)));
		criteria.add(Restrictions.eq("id.entryDate", DateUtil.getCurrentDate()));
		criteria.add(Restrictions.eq("id.acctNo", acctno32digit));
		List<D350023> list=criteria.list();
		System.out.println("list.size()::>>"+list.size());
		if(list!=null && list.size()>0)
		{
			session.close();
			session=null;
			tx=null;
			customerDetails.setResponse(MSGConstants.ERROR);
			customerDetails.setErrorMsg(MSGConstants.ALREADY_REQUESTED);
			return customerDetails;
		}
		Criteria crit=session.createCriteria(D350023.class);
		crit.add(Restrictions.eq("id.custNo", custNo));
		crit.add(Restrictions.eq("id.entryDate", DateUtil.getCurrentDate()));
		crit.add(Restrictions.eq("id.brCode",lbrcode ));
		crit.add(Restrictions.eq("id.acctNo", acctno32digit));
		crit.add(Restrictions.eq("id.entryDate", DateUtil.getCurrentDate()));
		List<D350023> list1=crit.list();
		System.out.println("list1.size()::>>"+list1.size());
		if(list1.size()>=Integer.valueOf(MSGConstants.ACCOUNT_STMT_LIMIT))
		{
			session.close();
			session=null;
			tx=null;
			customerDetails.setResponse(MSGConstants.ERROR);
			customerDetails.setErrorMsg(MSGConstants.EXCEED_DAILY_STMT_LIMIT);
			return customerDetails;
		}
		Customer customer=null;
		customer=getCustDetails(custNo);
		System.out.println("customer::>>"+customer);
		if(null==customer || null==customer.getEmail() || customer.getEmail().trim().equalsIgnoreCase(""))
		{
			session.close();
			session=null;
			tx=null;
			customerDetails.setResponse(MSGConstants.ERROR);
			customerDetails.setErrorMsg(MSGConstants.EMAIL_NOT_UPDATED);
			return customerDetails;
		}
		D350023 d350023Id=new D350023();
		D350023Id id=new D350023Id();
		id.setCustNo(custNo);
		id.setEntryDate(DateUtil.getCurrentDate());
		id.setFromDate(DateUtil.getUtilDate(fromdate));
		id.setToDate(DateUtil.getUtilDate(toDate));
		id.setMmid(mmid);

		id.setAcctNo(acctno32digit);
		id.setBrCode(lbrcode);
		d350023Id.setDbtrAddCb(0);
		d350023Id.setDbtrAddCd(new Date());
		d350023Id.setDbtrAddCk(0);
		d350023Id.setDbtrAddCs(0);
		d350023Id.setDbtrAddCt(new Date());
		d350023Id.setDbtrAddMb(lbrcode);
		d350023Id.setDbtrAddMd(new Date());
		d350023Id.setDbtrAddMk(0);
		d350023Id.setDbtrAddMs(0);
		d350023Id.setDbtrAddMt(new Date());
		d350023Id.setDbtrAuthDone(1);
		d350023Id.setDbtrAuthNeeded(0);
		d350023Id.setDbtrLhisTrnNo(0);
		d350023Id.setDbtrLupdCb(0);
		d350023Id.setDbtrLupdCd(new Date());
		d350023Id.setDbtrLupdCk(0);
		d350023Id.setDbtrLupdCs(0);
		d350023Id.setDbtrAddCt(new Date());
		d350023Id.setDbtrLupdMb(0);
		d350023Id.setDbtrLupdMd(new Date());
		d350023Id.setDbtrLupdMk(0);
		d350023Id.setDbtrLupdMs(0);
		d350023Id.setDbtrLupdMt(new Date());
		d350023Id.setDbtrRecStat(0);
		d350023Id.setDbtrTauthDone(1);
		d350023Id.setDbtrUpdtChkId(0);
		d350023Id.setFileName(" ");
		id.setEmailId(customer!=null?customer.getEmail():" ");//email
		d350023Id.setEntryTime(new Date());
		id.setMobileNo(customer!=null?customer.getMobno():" ");//mobno
		System.out.println("Julian::>>"+DateUtility.getJulianDay(DateUtility.getDateFormat(new Date())));
		String refNo = DateUtility.getJulianDay(DateUtility.getDateFormat(new Date())) + DateUtility.getPostTime(new Date())+"";
		logger.error("Refrence No : "+refNo+"\n");
		id.setRefNo(refNo);
		d350023Id.setStatus(0);
		d350023Id.setDbtrLupdCt(new Date());
		d350023Id.setId(id);
		try{
			session.save(d350023Id);
			tx.commit();
			session.close();
			customerDetails.setResponse(MSGConstants.SUCCESS);
			customerDetails.setErrorMsg(MSGConstants.LOAN_STMT_MSG +customer.getEmail().trim()+".");
			customerDetails.setEmailId(customer!=null?customer.getEmail().trim():"");
			System.out.println(MSGConstants.LOAN_STMT_MSG +customer.getEmail());
			return customerDetails;
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
			tx=null;
			session=null;
			customerDetails.setResponse(MSGConstants.ERROR);
			customerDetails.setErrorMsg(MSGConstants.ACCOUNT_STMT_FAILS);
			return customerDetails;
		}
	}
	@SuppressWarnings({ "deprecation", "unchecked" })
	public static String getCustNo(int lbrcode,String accNo)
	{
		List<D009022> lst;
		String custNo = null;
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(D009022.class);
		criteria.add(Restrictions.eq("id.lbrCode",lbrcode));
		criteria.add(Restrictions.eq("id.prdAcctId", ""+accNo));
		lst=criteria.list();
		if(lst!=null && !lst.isEmpty())
			custNo=String.valueOf(lst.get(0).getCustNo());
		tx.commit();
		session.close();
		session=null;
		return custNo;
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	public static Customer getCustDetails(String custno)
	{
		List<D350078> lst=null;
		Customer customer=null;
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(D350078.class);
		criteria.add(Restrictions.eq("id.custNo",custno));
		lst=criteria.list();
		logger.error("lst.size()"+lst.size());
		if(lst.size()>0)
		{
			customer=new Customer();
			logger.error("email::>>"+lst.get(0).getEmailId());
			customer.setCustno(lst.get(0).getId().getCustNo());
			customer.setMobno(lst.get(0).getId().getMobileNo());
			customer.setEmail(lst.get(0).getEmailId());
		}
		tx.commit();
		session.close();
		session=null;
		tx=null;
		return customer;
	}
	
	public static FetchDetailedStmtRespose fetchAccountStmtRequest(String acctNo15Digit, Date fromdate ,Date toDate,String custNo)
	{
		FetchDetailedStmtRespose customerDetails=new FetchDetailedStmtRespose();
		Session session = null;
		try{
			session = HBUtil.getSessionFactory().openSession();
			Transaction tx = session.beginTransaction();
			customerDetails.setAcctNo(acctNo15Digit);
			String acctno32digit=AccountDetailsUtil.get32DigitAcctNo(acctNo15Digit);
			int lbrcode=Integer.parseInt(acctNo15Digit.substring(0, 3));
			String branch = acctNo15Digit.substring(0, 3);
			if(branch.equalsIgnoreCase("999"))
				lbrcode = 9999;
			if(null==acctno32digit || acctno32digit.trim().equalsIgnoreCase(""))
			{
				tx.commit();
				session.close();
				session=null;
				customerDetails.setResp(MSGConstants.ERROR);
				customerDetails.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
				customerDetails.setFlag(false);
				return customerDetails;
			}
			
			D009022 sourceAccount=DataUtils.getAccount(acctNo15Digit.trim());
			
			TransactionValidationResponse res=TransactionServiceImpl.validateAccount(sourceAccount, String.valueOf("10"),"AS");
			if(res!=null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR))
			{
				tx.commit();
				session.close();
				session=null;
				customerDetails.setResp(MSGConstants.ERROR);
				customerDetails.setErrorMsg(res.getErrorMsg());
				customerDetails.setFlag(false);
				return customerDetails;
			}	
			
			
				if(null==RequestServiceImpl.validateAccountNoCustNo(custNo,String.valueOf(lbrcode), acctno32digit))
				{
					tx.commit();
					session.close();
					session=null;
					customerDetails.setResp(MSGConstants.ERROR);
					customerDetails.setErrorMsg(MSGConstants.INVALID_CUSTNO_ACCNO);
					customerDetails.setFlag(false);
					return customerDetails;		
				}
			
			
			D009011 d11 = session.get(D009011.class, Integer.parseInt(custNo));
			
			d11.setAdd1(d11.getAdd1().trim()+" "+d11.getAdd2().trim()+" "+d11.getAdd3().trim()+" \nIFSC :"+DataUtils.getSystemParameter(lbrcode, MSGConstants.IFSCCODESCB).getValue().trim());
			
			customerDetails.setCustomerDtls(d11);
			
			D001003 d03 = session.get(D001003.class, lbrcode);
			customerDetails.setStmtDtls(d03);
			
			logger.error("lbrcode : "+lbrcode+"\n from Date : "+fromdate+"\n 32 Digit AcctNo: "+acctno32digit);
			
			List<Object[]> request = getCustStatmReciept(lbrcode, acctno32digit, fromdate, toDate);
			List<String> details = new ArrayList<String>();
			Double totalBalance = getCblBalance(lbrcode, acctno32digit, fromdate);
			Double totalCredit = 0D;
			Double totalDebite = 0D;
			String stmDtls="";
			  GsonBuilder gsonBuilder = new GsonBuilder();
		      Gson gson = gsonBuilder.create();
		      System.out.println(totalBalance);
		      if(totalBalance<0)
		    	  customerDetails.setOpStr(String.valueOf(Math.round(totalBalance*(-1) * 100.0) / 100.0)+ " DR");
		      else
		    	  customerDetails.setOpStr(String.valueOf(Math.round(totalBalance * 100.0) / 100.0)+" CR");
			for(Object[] dtls: request) {
				totalBalance =  ((Double)dtls[4] - (Double)dtls[3])+ totalBalance;
				String crdr="C";
				Double balance = 0D;
				if(totalBalance<0) {
					crdr="D";
					balance= totalBalance*(-1);
				}else {
					balance=totalBalance;
				}
				totalCredit = totalCredit + (Double)dtls[4];
				totalDebite = totalDebite + (Double)dtls[3];
				String[] statDtls =new String[]{dtls[0].toString(),dtls[1].toString(),dtls[2].toString(),String.valueOf(Math.round((Double)dtls[3]*100.0) / 100.0),String.valueOf(Math.round((Double)dtls[3]*100.0) / 100.0),String.valueOf(Math.round(balance*100.0) / 100.0)+" "+crdr};
				//PrintUtils.print(statDtls);
				System.out.println("JSON String Array "+gson.toJson(statDtls));
				System.out.println(Arrays.toString(statDtls));
				
				details.add(dtls[0].toString().substring(0, 10));
				details.add(dtls[1].toString().trim());
				details.add(dtls[2].toString());
				details.add(String.valueOf(Math.round((Double)dtls[3]*100.0) / 100.0));
				details.add(String.valueOf(Math.round((Double)dtls[4]*100.0) / 100.0));
				details.add(String.valueOf(Math.round(balance*100.0) / 100.0)+" "+crdr+"R");
				//details.add(dtls[0].toString());
				
			}
			//customerDetails.setAcctNo(acctno32digit);
			customerDetails.setCustId(custNo);
			customerDetails.setLbrCd(lbrcode+"");
			customerDetails.setFrmdt(fromdate);
			customerDetails.setToDt(toDate);
			customerDetails.setStmtLst(details);
			customerDetails.setCreditTtl(String.valueOf(Math.round(totalCredit * 100.0) / 100.0));
			customerDetails.setDebitTtl(String.valueOf(Math.round(totalDebite * 100.0) / 100.0));
			customerDetails.setResp(MSGConstants.SUCCESS);
			customerDetails.setErrorMsg(MSGConstants.ACCOUNT_STMT_MSG);
			System.out.println(MSGConstants.ACCOUNT_STMT_MSG);
			//tx.commit();
			session.close();
			session=null;
			return customerDetails;
		} catch (Exception e) {
			session.close();
			e.printStackTrace();
			customerDetails.setResp(MSGConstants.SUCCESS);
			customerDetails.setErrorMsg(MSGConstants.ACCOUNT_STMT_FAILS);
			return customerDetails;
		}
	}
	
	public static List<Object[]> getCustStatmReciept(int lbrcode, String prdacctid, Date fromDate, Date toDate) throws SQLException {
		//String queryString = "FROM D009022 a,D009500 b, D009021 c" + " WHERE a.id.lbrCode =:lbrcode AND b.id.lbrCode = :lbrcode" + " AND a.custNo = c.custInt AND b.id.mainCustNo = c.custInt AND a.id.prdAcctId like :prdacctid";
		
		String queryString=" "; 
		D009021 prdCd = DataUtils.getProductMaster(lbrcode+"", prdacctid.substring(0, 8).trim());
		/*if (ConfigurationLoader.getParameters(false).getProperty("DATABASE").equalsIgnoreCase("ORACLE"))
			queryString = "SELECT id.entryDate, particulars, instrNo, (CASE WHEN drCr = 'D' THEN fcyTrnAmt ELSE 0 END) AS Dr, \r\n" + 
					"(CASE WHEN drCr = 'C' THEN fcyTrnAmt ELSE 0 END) AS Cr, "
					+ "sum(COALESCE((CASE WHEN drCr = 'C' THEN fcyTrnAmt ELSE 0 END), 0) - COALESCE((CASE WHEN drCr = 'D' THEN fcyTrnAmt ELSE 0 END),0)) AS Balance"
					+ " , drCr From D009040 "
					+ "WHERE id.lbrCode=:lbrcode AND id.entryDate BETWEEN :fromDate AND :toDate AND substr(mainAcctId,0,25)=:acctId "
					+ "group by id.entryDate, particulars,instrNo,fcyTrnAmt, drCr order by id.entryDate asc";
		else
			queryString = "SELECT id.entryDate, particulars, instrNo, (CASE WHEN drCr = 'D' THEN fcyTrnAmt ELSE 0 END) AS Dr, \r\n" + 
					"(CASE WHEN drCr = 'C' THEN fcyTrnAmt ELSE 0 END) AS Cr, "
					+ "sum(COALESCE((CASE WHEN drCr = 'C' THEN fcyTrnAmt ELSE 0 END), 0) - COALESCE((CASE WHEN drCr = 'D' THEN fcyTrnAmt ELSE 0 END),0)) AS Balance"
					+ " , drCr From D009040 "
					+ "WHERE id.lbrCode=:lbrcode AND id.entryDate BETWEEN :fromDate AND :toDate AND substring(mainAcctId,0,25)=:acctId "
					+ "group by id.entryDate, particulars,instrNo,fcyTrnAmt, drCr order by id.entryDate asc";*/
		if(RtgsNeftHostToHostConstants.LOAN.getMessage().contains(prdCd.getModuleType() + "")) {
			logger.error("LOAN Statment");
			if (ConfigurationLoader.getParameters(false).getProperty("DATABASE").equalsIgnoreCase("ORACLE"))
				queryString = "SELECT postDate, particulars, instrNo, (CASE WHEN drCr = 'D' THEN fcyTrnAmt ELSE 0 END) AS Dr, \r\n" + 
						"(CASE WHEN drCr = 'C' THEN fcyTrnAmt ELSE 0 END) AS Cr, "
						+ "sum(COALESCE((CASE WHEN drCr = 'C' THEN fcyTrnAmt ELSE 0 END), 0) - COALESCE((CASE WHEN drCr = 'D' THEN fcyTrnAmt ELSE 0 END),0)) AS Balance"
						+ " , drCr, postTime From D009040 "
						+ "WHERE id.lbrCode=:lbrcode AND postDate BETWEEN :fromDate AND :toDate AND mainAcctId=:acctId and canceledFlag<>'C' and vcrModType in (30,971)"
						+ "group by postDate, particulars,instrNo,fcyTrnAmt, drCr,postTime order by postDate, postTime asc";
			else
				queryString = "SELECT postDate, particulars, instrNo, (CASE WHEN drCr = 'D' THEN fcyTrnAmt ELSE 0 END) AS Dr, \r\n" + 
						"(CASE WHEN drCr = 'C' THEN fcyTrnAmt ELSE 0 END) AS Cr, "
						+ "sum(COALESCE((CASE WHEN drCr = 'C' THEN fcyTrnAmt ELSE 0 END), 0) - COALESCE((CASE WHEN drCr = 'D' THEN fcyTrnAmt ELSE 0 END),0)) AS Balance"
						+ " , drCr, postTime From D009040 "
						+ "WHERE id.lbrCode=:lbrcode AND postDate BETWEEN :fromDate AND :toDate AND vcrAcctId=:acctId and canceledFlag<>'C'"
						+ "group by postDate, particulars,instrNo,fcyTrnAmt, drCr,postTime order by postDate, postTime asc";
		}else {
		if (ConfigurationLoader.getParameters(false).getProperty("DATABASE").equalsIgnoreCase("ORACLE"))
			queryString = "SELECT postDate, particulars, instrNo, (CASE WHEN drCr = 'D' THEN fcyTrnAmt ELSE 0 END) AS Dr, \r\n" + 
					"(CASE WHEN drCr = 'C' THEN fcyTrnAmt ELSE 0 END) AS Cr, "
					+ "sum(COALESCE((CASE WHEN drCr = 'C' THEN fcyTrnAmt ELSE 0 END), 0) - COALESCE((CASE WHEN drCr = 'D' THEN fcyTrnAmt ELSE 0 END),0)) AS Balance"
					+ " , drCr, postTime From D009040 "
					+ "WHERE id.lbrCode=:lbrcode AND postDate BETWEEN :fromDate AND :toDate AND substr(vcrAcctId,0,24)=:acctId and canceledFlag<>'C'"
					+ "group by postDate, particulars,instrNo,fcyTrnAmt, drCr,postTime order by postDate, postTime asc";
		else
			queryString = "SELECT postDate, particulars, instrNo, (CASE WHEN drCr = 'D' THEN fcyTrnAmt ELSE 0 END) AS Dr, \r\n" + 
					"(CASE WHEN drCr = 'C' THEN fcyTrnAmt ELSE 0 END) AS Cr, "
					+ "sum(COALESCE((CASE WHEN drCr = 'C' THEN fcyTrnAmt ELSE 0 END), 0) - COALESCE((CASE WHEN drCr = 'D' THEN fcyTrnAmt ELSE 0 END),0)) AS Balance"
					+ " , drCr, postTime From D009040 "
					+ "WHERE id.lbrCode=:lbrcode AND postDate BETWEEN :fromDate AND :toDate AND substring(vcrAcctId,0,25)=:acctId and canceledFlag<>'C'"
					+ "group by postDate, particulars,instrNo,fcyTrnAmt, drCr,postTime order by postDate, postTime asc";
		}
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		 System.out.println("Statment_Query : " + queryString);
		
		if(RtgsNeftHostToHostConstants.LOAN.getMessage().contains(prdCd.getModuleType() + "")) {
		@SuppressWarnings("unchecked")
		List<Object[]> list = session.createQuery(queryString)
				.setParameter("lbrcode", lbrcode)
				.setParameter("fromDate", fromDate)
				.setParameter("toDate", toDate)
				.setParameter("acctId", prdacctid.trim()).getResultList();
				
				
			if(list.size()>0)
				{
					tx.commit();
					session.close();
					session=null;
					return list;
				}else {
					tx.commit();
					session.close();
					session=null;
					return new ArrayList<Object[]>();
				}
		}else {
			@SuppressWarnings("unchecked")
			List<Object[]> list = session.createQuery(queryString)
					.setParameter("lbrcode", lbrcode)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate)
					.setParameter("acctId", prdacctid.substring(0, 24).trim()).getResultList();
					
					
				if(list.size()>0)
					{
						tx.commit();
						session.close();
						session=null;
						return list;
					}else {
						tx.commit();
						session.close();
						session=null;
						return new ArrayList<Object[]>();
					}
		}
	}
	
	public static Double getCblBalance(int lbrcode, String prdacctid, Date fromDate) {
		
		
		 // convert date to localdatetime
        /*LocalDateTime localDateTime = fromDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        System.out.println("localDateTime : " + localDateTime);*/

        // plus one
        //localDateTime = localDateTime.minusDays(1);

        // convert LocalDateTime to date
        Date currentDatePlusOneDay =DateUtility.convertDateFormat(fromDate);// Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        System.out.println("\nOutput : " + currentDatePlusOneDay);
        
        
        
		Double cblBalance = 0D;
		
		
		
		String cblBalanceQuery ="SELECT balance4 FROM D010014 WHERE id.lbrCode=:lbrcode AND id.prdAcctId=:acctId AND id.cblDate<:fromDate "
				+ "ORDER BY id.cblDate DESC";
		try(Session session = HBUtil.getSessionFactory().openSession()) {
		
		Transaction tx = session.beginTransaction();
		
		/***Added By Aniket Desai on 27 Feb, 2020 for Loan Account:---Starts--- ***/
		D009021 creditProductMaster = session.get(D009021.class, new D009021Id(lbrcode, prdacctid.substring(0, 8).trim()));
		if(RtgsNeftHostToHostConstants.LOAN.getMessage().contains(creditProductMaster.getModuleType() + "")) {
			//cblBalanceQuery = cblBalanceQuery.replace("balance4", "balance1");
			String openingBalance = "(balance1-(balance2-balance3)-(balance4-balance5))";
			D030002 loanMaster = session.get(D030002.class, new D030002Id(lbrcode, prdacctid.substring(0, 8).trim()));
			logger.error(loanMaster.toString());
			if(loanMaster.getLnType()==32)
			{
				openingBalance = openingBalance + "-(balance6 - balance7 + "
						+ "(balance8 - balance9) +"
						+ "(balance11 - balance12) +"
						+ "(balance14 - balance15))";
								
			}else {
				openingBalance =  openingBalance + "-(balance6-balance7 +"
							+ "balance8-balance9)";
			}
			D001004 value = DataUtils.getSystemParameter(0, "DSPUNAPPINTYN");
			if(value!=null && "Y".equalsIgnoreCase(value.getValue().trim()))
				openingBalance =  openingBalance + "-(balance12-balance12 )";
			
			cblBalanceQuery = cblBalanceQuery.replace("balance4", openingBalance);
		}	
		/***Added By Aniket Desai on 27 Feb, 2020 for Loan Account:---End--- ***/
		
		cblBalance = (Double)session.createQuery(cblBalanceQuery)
			.setParameter("lbrcode", lbrcode)
			.setParameter("fromDate", currentDatePlusOneDay)
			.setParameter("acctId", prdacctid)
			.setMaxResults(1).getSingleResult();
		
		System.out.println("Opening Balance="+cblBalance);
		}catch(Exception ex) {
			ex.printStackTrace();
			
		}
		return cblBalance;
	}
	
}
