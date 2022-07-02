package com.sil.service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.pmw.tinylog.Logger;

import com.google.zxing.common.Comparator;
import com.sil.commonswitch.CoreTransactionMPOS;
import com.sil.commonswitch.DataUtils;
import com.sil.commonswitch.OtherChannelServiceResponse;
import com.sil.commonswitch.SwiftcoreDateUtil;
import com.sil.commonswitch.VoucherCommon;
import com.sil.commonswitch.VoucherMPOS;
import com.sil.constants.Code;
import com.sil.constants.MSGConstants;
import com.sil.constants.ResponseCodes;
import com.sil.dao.AccountMasterServiceImpl;
import com.sil.dao.CustomerMasterServiceImpl;
import com.sil.dao.SwiftCoreUtil;
import com.sil.domain.AccountDetails;
import com.sil.domain.AccountResponse;
import com.sil.domain.BalanceResponse;
import com.sil.domain.BranchDetailsResponse;
import com.sil.domain.CustNewMobileDetails;
import com.sil.domain.CustomerDepositePrdCdList;
import com.sil.domain.CustomerDetails;
import com.sil.domain.CustomerInfo;
import com.sil.domain.DepositeAccountDetals;
import com.sil.domain.DepositeAcctOpenRequest;
import com.sil.domain.DepositeAcctOpenResponse;
import com.sil.domain.DepositeAcctOpenResponseNew;
import com.sil.domain.DepositeInterestRateDetals;
import com.sil.domain.DepositeParameters;
import com.sil.domain.DepositeReceiptDetals;
import com.sil.domain.IFSCDetailsResponse;
import com.sil.domain.IMPSFetchDepositeAccountResponse;
import com.sil.domain.IMPSFetchDepositeInterestRateResponse;
import com.sil.domain.IMPSFetchDepositeReceiptResponse;
import com.sil.domain.IMPSFetchLoanAccountDetailsResponse;
import com.sil.domain.IMPSTermDepositRequest;
import com.sil.domain.LoanAccountDetails;
import com.sil.domain.PigmeAccountsResponse;
import com.sil.domain.ProductResponse;
import com.sil.domain.StopChequeResponse;
import com.sil.domain.TransactionValidationResponse;
import com.sil.domain.WSUtils;
import com.sil.hbm.D001004;
import com.sil.hbm.D002011;
import com.sil.hbm.D009011;
import com.sil.hbm.D009012;
import com.sil.hbm.D009021;
import com.sil.hbm.D009022;
import com.sil.hbm.D009022Id;
import com.sil.hbm.D009040;
import com.sil.hbm.D009042;
import com.sil.hbm.D009044;
import com.sil.hbm.D009122;
import com.sil.hbm.D009141;
import com.sil.hbm.D010010;
import com.sil.hbm.D010054;
import com.sil.hbm.D020002;
import com.sil.hbm.D020002Id;
import com.sil.hbm.D020004;
import com.sil.hbm.D020004Id;
import com.sil.hbm.D020118;
import com.sil.hbm.D047003;
import com.sil.hbm.D100001;
import com.sil.hbm.D100002;
import com.sil.hbm.D350001;
import com.sil.hbm.D350007;
import com.sil.hbm.D350023;
import com.sil.hbm.D350031;
import com.sil.hbm.D350032;
import com.sil.hbm.D350033;
import com.sil.hbm.D350034;
import com.sil.hbm.D350035;
import com.sil.hbm.D350076;
import com.sil.hbm.D350078;
import com.sil.hbm.D350078Id;
import com.sil.hbm.D390075;
import com.sil.hbm.D946022;
import com.sil.hbm.KYCMaster;
import com.sil.loan.LoanServiceImpl;
import com.sil.prop.ConfigurationLoader;
import com.sil.security.OmniEncryptPassword;
import com.sil.util.Account;
import com.sil.util.Customer;
import com.sil.util.DateUtil;
import com.sil.util.DateUtility;
import com.sil.util.HBUtil;
import com.sil.util.Props;

public class RequestServiceImpl {
	public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(RequestServiceImpl.class);

	public static void main(String[] args) {
		getMinistatement(3, "SB      000000000000680700000000", 5);
	}

	public static CustomerDetails validCustomer(String custNo) {
		CustomerDetails details = new CustomerDetails();
		System.out.println("Cust NO : " + custNo);
		if (custNo == null || custNo.length() < 1) {
			details.setResponse(MSGConstants.ERROR);
			details.setErrorMsg(MSGConstants.INVALID_CUSTOMER);
			return details;
		}
		D009011 d009011 = getCustDetailsMaster(custNo);
		if (null != d009011)// && null!=getAccountFromCustNo(custNo))
		{
			if (d009011.getDbtrAuthNeeded() != 0 && d009011.getDbtrAuthDone() != 1) {
				details.setResponse(MSGConstants.ERROR);
				details.setErrorMsg(MSGConstants.CUSTOMER_UNAUTH);
				return details;
			}
			D350076 d350076 = null;
			try {
				d350076 = (D350076) HBUtil.getSessionFactory().openSession().createCriteria(D350076.class)
						.add(Restrictions.eq("custNo", custNo.trim())).uniqueResult();
				System.out.println("d350078:>>>" + d350076);
				// System.out.println("Mobile
				// NO::>>>"+d350078.getId().getMobileNo());

				if (d350076 == null || d350076.getInterNetBankingSrYn() == 'N') {
					// System.out.println("Mobile NO::>>>" + d350076.getCustNo());
					details.setResponse(MSGConstants.ERROR);
					details.setErrorMsg(MSGConstants.NETBANKING_NOT_AVAILABLE);
					return details;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				details.setResponse(MSGConstants.ERROR);
				details.setErrorMsg(MSGConstants.INVALID_CUSTOMER);
			}
			details.setResponse(MSGConstants.SUCCESS);
			details.setErrorMsg("Customer Validated.");
			details.setPanNo(d009011.getPanNoDesc());
			try {
				D009141 d009141 = (D009141) HBUtil.getSessionFactory().openSession().createCriteria(D009141.class)
						.add(Restrictions.eq("custNo", Integer.parseInt(custNo.trim()))).uniqueResult();
				
				if (d009141!=null && d009141.getAdharNo() != null && !d009141.getAdharNo().trim().equals("")) {
					System.out.println(d009141.toString());
					details.setAdharNo(d009141.getAdharNo().trim());
				}else
					details.setAdharNo("");
				
				KYCMaster kyc = (KYCMaster) HBUtil.getSessionFactory().openSession().createCriteria(KYCMaster.class)
						.add(Restrictions.eq("custNo", Integer.parseInt(custNo.trim()))).uniqueResult();
				
				if(kyc!=null) {
					if(kyc.getNextKYCDueDate().before(new Date())) {
						details.setKycStatus("N");
						details.setErrorMsg("Customer KYC Not Updated");
					}else {
						details.setKycStatus("Y");
					}
					details.setKycDate(kyc.getEntryDate().toString());
				}else {
					details.setKycStatus("N");
					details.setKycDate("");
					details.setErrorMsg("Customer KYC Not Found");
				}
			
			}catch (Exception ex) {
				// TODO: handle exception
				ex.printStackTrace();
				System.out.println(ex);
			}
			return details;
		} else {
			details.setResponse("ERROR");
			details.setErrorMsg("Invalid Customer Number ");
			return details;
		}
	}

	public static CustomerDetails validIBCustomer(String custNo, String pass) {
		CustomerDetails details = new CustomerDetails();
		System.out.println("Cust NO : " + custNo);
		if (null != getAccountFromCustNo(custNo)) {
			details.setResponse("SUCCESS");
			details.setErrorMsg("Customer Validated.");
			return details;
		} else {
			details.setResponse("ERROR");
			details.setErrorMsg("Customer not validated.");
		}
		Customer customer = getCustomer(custNo);
		System.out.println("Customer : " + customer);
		if (customer != null) {
			String str = "001" + String.format("%03d", (customer.getCustno() + "").length()) + customer.getCustno()
					+ "~002" + String.format("%03d", customer.getName().length()) + customer.getName() + "~003"
					+ String.format("%03d", (customer.getPan() + "").length()) + customer.getPan() + "~004"
					+ String.format("%03d", customer.getAddr1().length()) + customer.getAddr1() + "~005"
					+ String.format("%03d", customer.getAddr2().length()) + customer.getAddr2() + "~006"
					+ String.format("%03d", customer.getAddr3().length()) + customer.getAddr3();
			System.out.println("Acc15 : " + customer.getAccounts().get(0).getAcc15());
			if (customer.getAccounts().size() > 0)
				str = str + "~007015" + customer.getAccounts().get(0).getAcc15();
			str = str + "~008" + String.format("%03d", customer.getMobno().trim().length())
					+ customer.getMobno().trim();
			str = str + String.format("%03d", str.length()) + str;
			details.setOutput(str);
			details.setResponse("SUCCESS");
			details.setErrorMsg("Customer Validated.");
			return details;
		} else {
			details.setResponse("ERROR");
			details.setErrorMsg("Customer Details not found");
			return details;
		}
	}

	@SuppressWarnings("deprecation")
	public static D009022 getAccountFromCustNo(String custNo) {
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		D009022 d22 = (D009022) session.createCriteria(D009022.class)
				.add(Restrictions.eq("custNo", Integer.parseInt(custNo))).setMaxResults(1).uniqueResult();
		tx.commit();
		session.close();
		System.out.println("D77 0 : " + d22);
		if (d22 == null) {
			return null;
		} else {
			return d22;
		}
	}

	public static D009011 getCustDetailsMaster(String custNo) {
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		D009011 d22 = (D009011) session.createCriteria(D009011.class)
				.add(Restrictions.eq("custNo", Integer.parseInt(custNo))).setMaxResults(1).uniqueResult();
		tx.commit();
		session.close();
		System.out.println("D77 0 : " + d22);
		if (d22 == null) {
			return null;
		} else {
			return d22;
		}
	}

	public static D009022 validateAccountNoCustNo(String custNo, String lbrcode, String accno) {
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(D009022.class);
		criteria.add(Restrictions.eq("custNo", Integer.parseInt(custNo)));
		criteria.add(Restrictions.eq("id.lbrCode", Integer.parseInt(lbrcode)));
		criteria.add(Restrictions.eq("id.prdAcctId", accno.trim()));
		List<D009022> d22 = criteria.list();
		tx.commit();
		session.close();
		System.out.println("D77 0 : " + d22);
		if (d22 == null || d22.size() < 1) {
			return null;
		} else {
			return d22.get(0);
		}
	}

	public static List<D009022> fetchAllAccounts(String custNo) {
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(D009022.class);
		criteria.add(Restrictions.eq("custNo", Integer.valueOf(custNo.trim())));
		criteria.add(Restrictions.ilike("id.prdAcctId", "SB%"));
		List<D009022> list = criteria.list();
		tx.commit();
		session.close();
		System.out.println("D77 0 : " + list);
		if (list == null) {
			return null;
		} else {
			return list;
		}
	}

	public static AccountResponse fetchAllMMID(String custNo) {
		AccountResponse accountResponse = new AccountResponse();
		ArrayList<AccountDetails> detailsList = new ArrayList<>();
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(D350032.class);
		criteria.add(Restrictions.eq("id.custNo", custNo.trim()));
		List<D350032> list = criteria.list();
		tx.commit();
		session.close();
		System.out.println("D350032 : " + list);
		if (list == null || list.size() == 0) {
			accountResponse.setReponse(MSGConstants.ERROR);
			accountResponse.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
			return accountResponse;
		} else {
			for (int i = 0; i < list.size(); i++) {
				AccountDetails details = new AccountDetails();
				details.setMobNo(list.get(i).getId().getMobileNo());
				details.setAccCode(list.get(i).getAcctNo());
				details.setBrachCode(String.valueOf(list.get(i).getBrCode()));
				details.setMmid(list.get(i).getId().getMmid());
				details.setFormattedAccount(list.get(i).getBrCode() + "/"
						+ list.get(i).getAcctNo().substring(0, 8).trim() + "/"
						+ Integer.valueOf(list.get(i).getAcctNo()
								.substring(list.get(i).getAcctNo().length() - 16, list.get(i).getAcctNo().length() - 8)
								.trim()));
				detailsList.add(details);
				details = null;
			}
			accountResponse.setReponse(MSGConstants.SUCCESS);
			accountResponse.setErrorMsg(MSGConstants.SUCCESS_MSG);
			accountResponse.setAccountDetails(detailsList);
			return accountResponse;
		}
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	public static Customer getCustomer(String custNo) {
		Customer customer = new Customer();
		try (Session session = HBUtil.getSessionFactory().openSession()) {
			Transaction tx = session.beginTransaction();
			D009011 d11 = session.get(D009011.class, Integer.parseInt(custNo));
			tx.commit();
			session.close();
			if (d11 == null) {
				System.out.println("Customer not found in D009011");
				return null;
			} else {
				customer.setCustno(custNo);
				customer.setAddr1(d11.getAdd1());
				customer.setAddr2(d11.getAdd2());
				customer.setAddr3(d11.getAdd3());
				customer.setName(d11.getLongname());
				customer.setPan(d11.getPanNoDesc());
				customer.setPinCode(d11.getPinCode());

				tx = session.beginTransaction();
				List<D009022> d22List = session.createCriteria(D009022.class)
						.add(Restrictions.eq("custNo", Integer.parseInt(custNo))).list();
				tx.commit();
				System.out.println("No of Accounts of Customer " + custNo + " is " + d22List.size());

				List<Account> accList = new ArrayList<>();

				for (D009022 d22 : d22List) {

					Account account = new Account();
					account.setLbrCode(d22.getId().getLbrCode());
					account.setAcc32(d22.getId().getPrdAcctId());
					account.setActClrBalFcy(d22.getActClrBalFcy());
					account.setAuthNeeded(d22.getDbtrAuthNeeded());
					account.setCustNo(d22.getCustNo());
					account.setDtOfBirth(d22.getDtOfBirth());
					account.setFreezeStatus(d22.getFreezeType());
					account.setName(d22.getLongName());
					account.setStatus(d22.getAcctStat());
					account.setTotLienBalFcy(d22.getTotalLienFcy());
					account.setUnClrEffFcy(d22.getUnClrEffFcy());
					accList.add(account);
					System.out.println(account.toString());
				}
				customer.setAccounts(accList);
				tx = session.beginTransaction();
				List<D350078> d78List = session.createCriteria(D350078.class).add(Restrictions.eq("id.custNo", custNo))
						.list();
				tx.commit();
				System.out.println("Mobiles List Size : " + d78List.size());
				if (d78List.size() != 0) {
					customer.setMobno(d78List.get(0).getId().getMobileNo());
					customer.setEmail(d78List.get(0).getEmailId());
				}
				return customer;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	public static List<D009022> fatchAccount(String custNo) {
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		Criteria criteria = session.createCriteria(D009022.class);
		criteria.add(Restrictions.eq("custNo", Integer.valueOf(custNo.trim())));
		List<D009022> list = criteria.list();
		t.commit();
		session.close();
		session = null;
		t = null;
		return list;
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	public static List<D009040> getMinistatement(int lbrCode, String prdAcctId, int noofstmts) {
		System.out.println("LObrCode : " + lbrCode);
		System.out.println("PrdAcctID : " + prdAcctId);
		System.out.println("NoofRec : " + noofstmts);
		System.out.println("Date : " + new Date());
		List<D009040> lst;
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D009040.class);
		criteria.add(Restrictions.eq("vcrAcctId", prdAcctId));
		criteria.add(Restrictions.eq("id.lbrCode", lbrCode));
		criteria.add(Restrictions.ne("canceledFlag", 'C'));
		criteria.add(Restrictions.le("id.entryDate", DateUtil.getFormattedDateNew(new Date())));
		criteria.addOrder(Order.desc("id.entryDate"));
		criteria.addOrder(Order.desc("postTime"));
		criteria.setMaxResults(noofstmts);
		lst = criteria.list();
		session.close();
		return lst;
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	public static CustomerDetails validateIBCustomer(String custNo, String pass) {
		CustomerDetails customerDetails = new CustomerDetails();
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		Criteria criteria = session.createCriteria(D350076.class);
		criteria.add(Restrictions.eq("custNo", custNo));
		List<D350076> list = criteria.list();
		logger.error("D350076 List::>>> " + list);

		Criteria crit = session.createCriteria(D002011.class);
		crit.add(Restrictions.eq("custNo", custNo));
		List<D002011> lists = crit.list();
		logger.error("D002011 List::>>> " + lists);
		if (lists == null || lists.size() < 1) {
			session.close();
			session = null;
			customerDetails.setResponse(MSGConstants.ERROR);
			customerDetails.setErrorMsg("Customer is not registered for Internet Banking");
			return customerDetails;
		}
		if (lists != null || lists.size() > 0) {
			if (lists.get(0).getStatus() == Byte.valueOf("2")) {
				session.close();
				session = null;
				customerDetails.setResponse(MSGConstants.ERROR);
				customerDetails.setErrorMsg(MSGConstants.IB_CUSTOMER_LOCKED);
				return customerDetails;
			}
			System.out.println("Next password change Date::>>>" + lists.get(0).getNextPwdChgDt());
			logger.error("Next password change Date::>>>" + lists.get(0).getNextPwdChgDt());
			if (!DateUtil.compareIBnextPwdDate(String.valueOf(lists.get(0).getNextPwdChgDt()), "")) {
				session.close();
				session = null;
				customerDetails.setResponse(MSGConstants.ERROR);
				customerDetails.setErrorMsg(MSGConstants.IB_PASS_EXP);
				return customerDetails;
			}
			logger.error("Next password::>>>");
		}
		if (list != null && list.size() > 0) {
			try {
				if (list.get(0).getInterNetBankingSrYn() == MSGConstants.IS_N)// ||
																				// list.get(0).getRtgsneftyn()==MSGConstants.IS_N)
				{
					session.close();
					session = null;
					customerDetails.setResponse(MSGConstants.ERROR);
					customerDetails.setErrorMsg(MSGConstants.NETBANKING_NOT_AVAILABLE);
					return customerDetails;
				}
				if (WSUtils.verifyHash(pass.trim(), "SHA1", list.get(0).getIbqueryPin().trim())) {
					customerDetails.setAdd1(list.get(0).getAdd1());
					customerDetails.setAdd2(list.get(0).getAdd2());
					customerDetails.setAdd3(list.get(0).getAdd3());
					Criteria criteria2 = session.createCriteria(D350078.class);
					criteria2.add(Restrictions.eq("id.custNo", custNo));
					List<D350078> list2 = criteria2.list();

					if (list2 != null && list2.size() > 0) {
						customerDetails.setMobileNo(list2.get(0).getId().getMobileNo());
						customerDetails.setEmailId(list2.get(0).getEmailId());
					}
					Criteria criteria3 = session.createCriteria(D009022.class);
					criteria3.add(Restrictions.eq("custNo", Integer.valueOf(custNo)));
					// criteria3.add(Restrictions.ilike("id.prdAcctId",
					// "SB%"));//("custNo", custNo));
					List<D009022> list3 = criteria3.list();
					if (null != list3 && list3.size() > 0) {
						customerDetails.setLbrCode(String.valueOf(list3.get(0).getId().getLbrCode()));
						customerDetails.setAccNo(list3.get(0).getId().getPrdAcctId());
						customerDetails.setNameTitle(list3.get(0).getNameTitle());
						customerDetails.setLongName(list3.get(0).getLongName());
						customerDetails.setResponse(MSGConstants.SUCCESS);
						customerDetails.setErrorMsg(MSGConstants.CUSTOMER_VERIFIED);
						t.commit();
						t = null;
						session.close();
						session = null;
						return customerDetails;
					}
					return customerDetails;
				} else {
					session.close();
					session = null;
					customerDetails.setResponse(MSGConstants.ERROR);
					customerDetails.setErrorMsg(MSGConstants.INVALID_NETBANKING_PASSWORD);
					return customerDetails;
				}
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				customerDetails.setResponse(MSGConstants.ERROR);
				customerDetails.setErrorMsg(MSGConstants.CUSTOMER_NOT_FOUND);
				return customerDetails;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				customerDetails.setResponse(MSGConstants.ERROR);
				customerDetails.setErrorMsg(MSGConstants.CUSTOMER_NOT_FOUND);
				return customerDetails;
			}
		}
		customerDetails.setResponse(MSGConstants.ERROR);
		customerDetails.setErrorMsg(MSGConstants.CUSTOMER_NOT_FOUND);
		return customerDetails;
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	public static CustomerDetails validateCardAndCustId(String custId, String cardId) {
		CustomerDetails customerDetails = new CustomerDetails();
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		Criteria criteria = session.createCriteria(D390075.class);
		criteria.add(Restrictions.eq("custNo", Integer.valueOf(custId.trim())));
		criteria.add(Restrictions.eq("cardNo", cardId.trim()));
		List<D390075> list = criteria.list();
		t.commit();
		session.close();
		session = null;
		t = null;
		if (list != null && list.size() > 0) {
			customerDetails.setResponse(MSGConstants.SUCCESS);
			customerDetails.setErrorMsg(MSGConstants.SUCCESS_MSG);
			return customerDetails;
		}
		customerDetails.setResponse(MSGConstants.ERROR);
		customerDetails.setErrorMsg(MSGConstants.RECORD_NOT_FOUND);
		return customerDetails;
	}

	public static BalanceResponse fetchBalance(String brCode, String accNo) {
		BalanceResponse balanceResponse = new BalanceResponse();
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D009022.class);
		criteria.add(Restrictions.eq("id.lbrCode", Integer.valueOf(brCode.trim())));
		criteria.add(Restrictions.eq("id.prdAcctId", accNo.trim()));
		List<D009022> list = criteria.list();
		session.close();
		session = null;
		ArrayList<Double> lists = new ArrayList<>();
		if (list != null && list.size() > 0) {
			lists.add(Double.valueOf(list.get(0).getActClrBalFcy()));
			lists.add(Double.valueOf(list.get(0).getActTotBalFcy()));
			lists.add(Double.valueOf(list.get(0).getActTotBalLcy()));
			lists.add(Double.valueOf(list.get(0).getShdClrBalFcy()));
			lists.add(Double.valueOf(list.get(0).getShdTotBalFcy()));

			Collections.sort(lists);

			System.out.println("minimum Bal::>>" + lists.get(0));
			if (list.get(0).getFcyScheme().trim().equalsIgnoreCase(MSGConstants.FCY_SCHEME)) {
				balanceResponse.setAccActualBalance(String.valueOf(new BigDecimal(Double.valueOf(lists.get(0))
						+ Double.valueOf(list.get(0).getFlexiBalance()) - Double.valueOf(list.get(0).getTotalLienFcy()))
								.setScale(2, BigDecimal.ROUND_HALF_UP)));
				balanceResponse.setAccClearBalance(String.valueOf(new BigDecimal(
						(list.get(0).getActTotBalFcy() - list.get(0).getTotalLienFcy()) + list.get(0).getFlexiBalance())
								.setScale(2, BigDecimal.ROUND_HALF_UP)));
			} else {
				balanceResponse.setAccActualBalance(
						String.valueOf(new BigDecimal((lists.get(0)) - (Double.valueOf(list.get(0).getTotalLienFcy())))
								.setScale(2, BigDecimal.ROUND_HALF_UP)));
				balanceResponse.setAccClearBalance(
						String.valueOf(new BigDecimal((list.get(0).getActTotBalFcy() - list.get(0).getTotalLienFcy()))
								.setScale(2, BigDecimal.ROUND_HALF_UP)));
			}
			// balanceResponse.setAccActualBalance(""+(list.get(0).getActClrBalFcy()-list.get(0).getTotalLienFcy()));
			// balanceResponse.setAccClearBalance(""+(list.get(0).getActClrBalFcy()-list.get(0).getTotalLienFcy()));
			balanceResponse.setAccName(" " + list.get(0).getLongName().trim());
			// balanceResponse.setAccTitle(Integer.valueOf(brCode.trim())+"/"+accNo.substring(0,8).trim()+"/"+Integer.valueOf(accNo.substring(accNo.length()-16,accNo.length()-8)));
			balanceResponse.setAccTitle("" + list.get(0).getNameTitle().trim());
			balanceResponse.setFormattedAcc(
					brCode + "/" + accNo.substring(0, 8).trim() + "/" + Long.valueOf(accNo.substring(16, 24)));
		}
		return balanceResponse;
	}

	public static CustomerDetails getCustomerDetails(String mobno) {
		CustomerDetails customerDetails = new CustomerDetails();
		if (null == mobno || mobno.trim().length() < 10) {
			customerDetails.setResponse(MSGConstants.ERROR);
			customerDetails.setErrorMsg(MSGConstants.INVALID_MOB_NO);
		}

		Session session = HBUtil.getSessionFactory().openSession();
		try {
			Criteria criteria = session.createCriteria(D350078.class);
			criteria.add(Restrictions.ilike("id.mobileNo", "%" + mobno + "%"));
			criteria.addOrder(Order.desc("dbtrAddMd"));
			List<D350078> list = criteria.list();
			if (list == null || list.isEmpty()) {
				customerDetails.setResponse(MSGConstants.ERROR);
				customerDetails.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
				return customerDetails;
			}
			List<D009022> validAccList = DataUtils.getValidAccListIVR(list.get(0).getId().getCustNo());
			if (validAccList == null || validAccList.isEmpty()) {
				customerDetails.setResponse(MSGConstants.ERROR);
				customerDetails.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
				return customerDetails;
			}
			String miniStmt = "";
			List<D009040> stmt = RequestServiceImpl.getMinistatement(
					Integer.valueOf(validAccList.get(0).getId().getLbrCode()),
					validAccList.get(0).getId().getPrdAcctId(), 3);
			String balnce = DataUtils.getAccountFormMobileNo(mobno);
			if (stmt != null && !stmt.isEmpty()) {
				for (int i = 0; i < stmt.size(); i++) {
					miniStmt += (DateUtil.convertDateFormatIVR(stmt.get(i).getPostDate())) + ": "
							+ stmt.get(i).getFcyTrnAmt() + " " + stmt.get(i).getDrCr() + ","
							+ stmt.get(i).getParticulars().trim() + "\n";//
				}
				customerDetails.setResponse(MSGConstants.SUCCESS);
				customerDetails.setErrorMsg(balnce + "\n " + miniStmt);
			} else {
				customerDetails.setResponse(MSGConstants.SUCCESS);
				customerDetails.setErrorMsg(balnce + "\n " + MSGConstants.NO_TRANSACTION_FOUND);
				return customerDetails;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("", e);
			customerDetails.setResponse(MSGConstants.ERROR);
			customerDetails.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			return customerDetails;
		} finally {
			session.close();
			session = null;
		}

		return customerDetails;
	}

	public static OtherChannelServiceResponse fetchPinOffset(String custId, String pinType) {
		OtherChannelServiceResponse response = new OtherChannelServiceResponse();
		try (Session session = HBUtil.getSessionFactory().openSession()) {
			Criteria criteria = session.createCriteria(D350031.class);
			criteria.add(Restrictions.eq("custNo", custId));
			List<D350031> list = criteria.list();
			Criteria crit = session.createCriteria(D350034.class);
			crit.add(Restrictions.eq("custNo", custId));
			List<D350034> list2 = crit.list();
			System.out.println("list.size()::>>" + list.size());
			System.out.println("list2.size()::>>" + list2.size());
			if (list != null && list.size() > 0 && list2 != null && list2.size() > 0) {
				if (list.get(0).getMobBankingYn() == MSGConstants.MOB_YN) {
					session.close();
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.MOB_BANKING_NOT_ACTIVATED);
					return response;
				}
				if (list.get(0).getDbtrAuthNeeded() != 0 && list.get(0).getDbtrAuthDone() != 1) {
					session.close();
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.MOBILE_BANKING_REG_UNAUTH);
					return response;
				}
				if (list2.get(0).getStatus() == 2) {
					session.close();
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.MOBILE_BANKING_CUST_LOCKED);
					return response;
				}

				if (list2.get(0).getStatus() == 7) {
					session.close();
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.CHANGE_LOGIN_PIN);
					return response;
				}

				if (pinType.equalsIgnoreCase(MSGConstants.LOGIN_PWD)) {
					String[] output = new String[4];
					output[0] = "Y";
					if (null == list.get(0).getNewQueryPin()
							|| list.get(0).getNewQueryPin().trim().equalsIgnoreCase("")) {
						output[0] = "N";
					}
					output[1] = list.get(0).getNewQueryPin().trim();
					output[2] = list2.get(0).getStatus() + "";
					response.setErrorCode("" + list2.get(0).getStatus());
					response.setResponse(MSGConstants.SUCCESS);
					response.setErrorMessage(MSGConstants.SUCCESS_MSG);
					response.setOutput(output);
					session.close();
					criteria = null;
					crit = null;
					return response;
				} else if (pinType.equalsIgnoreCase(MSGConstants.TRANS_PWD)) {
					String[] output = new String[4];
					output[0] = "Y";
					if (null == list.get(0).getNewTransPin()
							|| list.get(0).getNewTransPin().trim().equalsIgnoreCase("")) {
						output[0] = "N";
					}
					output[1] = list.get(0).getNewTransPin().trim();
					output[2] = list2.get(0).getStatus() + "";
					response.setErrorCode("" + list2.get(0).getStatus());
					response.setResponse(MSGConstants.SUCCESS);
					response.setErrorMessage(MSGConstants.SUCCESS_MSG);
					response.setOutput(output);
					session.close();
					criteria = null;
					crit = null;

					return response;
				}
			}
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.CUSTOMER_NOT_FOUND);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	public static OtherChannelServiceResponse storePinOffset(String custNo, String pinType, String offset, String pin,
			String channel) {
		OtherChannelServiceResponse response = new OtherChannelServiceResponse();
		if (channel.equalsIgnoreCase(MSGConstants.IMPS_CHANNEL)) {
			Session session = HBUtil.getSessionFactory().openSession();
			Transaction t = session.beginTransaction();
			Criteria criteria = session.createCriteria(D350031.class);
			criteria.add(Restrictions.eq("custNo", custNo));
			List<D350031> list = criteria.list();
			if (list == null || list.size() < 1) {
				// ========customer is not registered for mobile banking=====
				D009011 d009011 = getCustDetailsMaster(custNo);
				System.out.println("Customer not found in d009011::>>" + d009011);
				Logger.error("Customer not found in d009011::>>" + d009011);
				if (null == d009011) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.CUSTOMER_NOT_FOUND);
					return response;
				}
				String quryPin = MSGConstants.NOT_USED_PROCESS;
				String transPin = MSGConstants.NOT_USED_PROCESS;
				String newQuryPin = MSGConstants.NOT_USED_PROCESS;
				String newTransPin = MSGConstants.NOT_USED_PROCESS;
				Date pinChangeDate = DateUtil.getDateFromStringYYYYMMDD("19000101");
				Date pinChangeTime = DateUtil.getDateFromStringYYYYMMDD("19000101");
				Date tPinChangeDate = DateUtil.getDateFromStringYYYYMMDD("19000101");
				Date tPinChangeTime = DateUtil.getDateFromStringYYYYMMDD("19000101");
				Calendar currentTimeCal = DateUtil.addingDateTime(new Date(), 90, "Days");
				Date nextTransPassPwdChgDate = currentTimeCal.getTime();
				Date nextTranPwdChgDate = currentTimeCal.getTime();
				Date nextTransPwdChgDt = currentTimeCal.getTime();

				String password = OmniEncryptPassword.getEncryptedPwd(custNo, pin);
				if (pinType.equalsIgnoreCase(MSGConstants.LOGIN_PWD)) {
					quryPin = password;
					newQuryPin = offset;
					pinChangeDate = DateUtil.convertDateFormat(new Date());
					pinChangeTime = DateUtil.getFormattedTime(new Date());
				} else if (pinType.equalsIgnoreCase(MSGConstants.TRANS_PWD)) {
					transPin = password;
					newTransPin = offset;
					tPinChangeDate = DateUtil.convertDateFormat(new Date());
					tPinChangeTime = DateUtil.getFormattedTime(new Date());
				}

				String add1 = d009011.getAdd1();
				String add2 = d009011.getAdd2();
				String add3 = d009011.getAdd3();
				if (!add1.equalsIgnoreCase(MSGConstants.BLANK_STRING)) {
					add1 = add1.length() > 30 ? add1.substring(0, 30) : add1;
				}

				if (!add2.equalsIgnoreCase(MSGConstants.BLANK_STRING)) {
					add2 = add2.length() > 30 ? add2.substring(0, 30) : add2;
				}

				if (!add3.equalsIgnoreCase(MSGConstants.BLANK_STRING)) {
					add3 = add3.length() > 30 ? add3.substring(0, 30) : add3;
				}
				// List<D009022> accountList=DataUtils.getValidAccList(custNo);
				List<D009022> accountList = DataUtils.getAccountsFromCustNo(custNo);

				if (null == accountList || accountList.isEmpty()) {
					System.out.println("Invalid Customer Number.");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_CUSTOMER);
					return response;
				}
				String mobileNo = MSGConstants.BLANK_STRING;
				List<D350078> otherChannelDetailsList = DataUtils.getCustMobNo(custNo);
				if (null == otherChannelDetailsList || otherChannelDetailsList.isEmpty()) {
					System.out.println("Mobile number is not valid. Please contact to branch.");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.MOBILE_NO_NOT_FOUND);
					return response;
				} else {
					mobileNo = otherChannelDetailsList.get(0).getId().getMobileNo();
				}
				System.out.println("Mobile Number::>>" + mobileNo);
				if (mobileNo.trim().length() != 10 && mobileNo.trim().length() != 12) {
					System.out.println("Mobile number is not valid. Please contact to branch.");
					Logger.error("Mobile number is not valid. Please contact to branch.");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_MOBILE_NO);
					return response;
				}

				D350031 d350031 = DataUtils.prepareMobileBankingRegObject(custNo, d009011.getLongname(), mobileNo,
						quryPin, transPin, d009011.getLbrCode(), DateUtil.convertDateFormat(new Date()),
						MSGConstants.MOB_BANKING_YES, pinChangeDate, pinChangeTime, tPinChangeDate, tPinChangeTime,
						add1, add2, add3, 0d, 0d, d009011.getShortName(), newQuryPin, newTransPin);
				System.out.println("d350031::>>" + d350031);
				System.out.println("accountList.size()::>>>" + accountList.size());
				int inc = 1;
				for (int count = 0; count < accountList.size(); count++) {

					System.out.println("in for loop ::>>" + count);
					logger.error("in for loop ::>>" + count);
					String productCode = DataUtils.getProductCode(accountList.get(count).getId().getPrdAcctId());
					D009021 productMaster = DataUtils
							.getProductMaster(String.valueOf(accountList.get(count).getId().getLbrCode()), productCode);
					if (!MSGConstants.MODULE_TYPE_LIST.contains(Long.valueOf(productMaster.getModuleType()))) {
						continue;
					}
					if (!new ArrayList<Long>(Arrays.asList(1l, 2l, 12l))
							.contains(Long.valueOf(accountList.get(count).getAcctStat()))) {
						continue;
					}
					System.out.println("after continue");
					logger.error("after continue");
					String mmid = MSGConstants.NBIN + String.format("%03d", inc);
					System.out.println("MMID::>>" + mmid);
					logger.error("MMID:>>" + mmid);
					D350032 custxTreDtls = DataUtils.fetchMMID(custNo, mmid);
					System.out.println("custxTreDtls::>>" + custxTreDtls);
					// logger.error("custxTreDtls::>>"+custxTreDtls.toString());
					custxTreDtls = DataUtils.prepareMMIDMasterObject(custNo, d009011.getLongname(), mobileNo, mmid,
							accountList.get(count).getId().getLbrCode(), accountList.get(count).getId().getPrdAcctId());
					// DataUtils.storeD350032(custxTreDtls);
					System.out.println("custxTreDtls::>>>" + custxTreDtls);
					logger.error("custxTreDtls::>>>" + custxTreDtls);
					D350001 otpObj = DataUtils.searchOTP(accountList.get(count).getId().getLbrCode(),
							accountList.get(count).getId().getPrdAcctId());
					System.out.println("otpObj::>>>" + otpObj);
					if (null == otpObj) {
						D001004 systemParameter = DataUtils.getSystemParameter(0, MSGConstants.SETOTPTRNLMT);
						Double setOtpTrnLmt = systemParameter == null ? 0d : Double.valueOf(systemParameter.getValue());
						D001004 sysPara = DataUtils.getSystemParameter(0, MSGConstants.SETDAILYTRNLMT);
						System.out.println("sysPara::>>>" + sysPara);
						Double setDailyTrnLmt = sysPara == null ? 0d : Double.valueOf(sysPara.getValue());
						System.out.println("setDailyTrnLmt::>>>" + setDailyTrnLmt);

						D001004 system = DataUtils.getSystemParameter(0, MSGConstants.NOOFTRNS);
						System.out.println("system::>>" + system);

						int noOfTrns = 10;// system == null ? 0 :
											// Integer.valueOf(system.getValue());
						System.out.println("noOfTrns::>>" + noOfTrns);

						D001004 parameter = DataUtils.getSystemParameter(0, MSGConstants.SETOTPEXPTIME);
						System.out.println("parameter::>>" + parameter);

						int setOtpExpTime = 5;// parameter == null ? 0 :
												// Integer.valueOf(parameter.getValue());
						System.out.println("setOtpExpTime::>>" + setOtpExpTime);
						otpObj = DataUtils.prepareOtpObject(accountList.get(count).getId().getLbrCode(),
								accountList.get(count).getId().getPrdAcctId(), setOtpTrnLmt, setDailyTrnLmt, noOfTrns,
								setOtpExpTime, "Created By IMPS Application", custNo);
					}
					try {
						// Transaction transaction=session.beginTransaction();

						System.out.println("storing objects");
						logger.error("storing objects");
						session.save(custxTreDtls);
						session.save(otpObj);
						session.flush();

					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					inc++;
				}
				D350034 custOtherInfo = DataUtils.getMobCustStatus(custNo);

				if (null == custOtherInfo) {
					custOtherInfo = DataUtils.prepareCustOtherInfoObject(custNo, d009011.getLbrCode(), 1, 1, 0, 0, 0, 0,
							3, 3, nextTransPwdChgDt, nextTranPwdChgDate, nextTransPassPwdChgDate);
				} else {
					custOtherInfo.setNextTransPassPwdChgDt(nextTransPassPwdChgDate);
					custOtherInfo.setNextTranPwdChgDt(nextTranPwdChgDate);
					custOtherInfo.setNextTransPwdChgDt(nextTransPwdChgDt);
				}
				// save obj

				D350033 pinStatus = DataUtils.preparePinStatusObject(custNo, 1, DateUtil.convertDateFormat(new Date()),
						1, DateUtil.convertDateFormat(new Date()));
				// impsService.savePinStatusWS(pinStatus);

				D350035 custOtherDtls = DataUtils.prepareCustOtherDtls(custNo, DateUtil.convertDateFormat(new Date()),
						DateUtil.getFormattedTime(new Date()), 'N', "New Pin Generated");

				try {
					session.save(d350031);
					session.save(pinStatus);
					session.save(custOtherInfo);
					session.save(custOtherDtls);
					session.flush();
					t.commit();
					session.close();
					response.setResponse(MSGConstants.SUCCESS);
					response.setErrorMessage(MSGConstants.SUCCESS_MSG);
					return response;
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				// =======================

				if (pinType.equalsIgnoreCase(MSGConstants.LOGIN_PWD)) {
					if (null == list.get(0).getNewQueryPin()
							|| list.get(0).getNewQueryPin().trim().equalsIgnoreCase("")) {
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage(MSGConstants.PIN_OFFSET_STORE_FAILED);
						session.close();
						session = null;
						criteria = null;
						return response;
					} else {
						Query<D350031> q = session.createQuery("UPDATE D350031 SET NewQueryPin =? WHERE custNo =?");
						q.setParameter(0, offset);
						q.setParameter(1, custNo);
						int i = q.executeUpdate();
						t.commit();
						session.close();
						session = null;
						t = null;
						if (i > 0) {
							response.setResponse(MSGConstants.SUCCESS);
							response.setErrorMessage(MSGConstants.SUCCESS_MSG);
							session = null;
							criteria = null;
							return response;
						} else {
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMessage(MSGConstants.PIN_OFFSET_STORE_FAILED);
							session.close();
							session = null;
							criteria = null;
							return response;
						}
					}
				} else if (pinType.equalsIgnoreCase(MSGConstants.TRANS_PWD)) {
					if (null == list.get(0).getNewTransPin()
							|| list.get(0).getNewTransPin().trim().equalsIgnoreCase("")) {
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage(MSGConstants.PIN_OFFSET_STORE_FAILED);
						session.close();
						session = null;
						criteria = null;

						return response;
					} else {
						Query<D350031> q = session.createQuery("UPDATE D350031 SET NewTransPin =? WHERE custNo =?");
						q.setParameter(0, offset);
						q.setParameter(1, custNo);
						int i = q.executeUpdate();
						t.commit();
						session.close();
						session = null;
						t = null;
						if (i > 0) {
							response.setResponse(MSGConstants.SUCCESS);
							response.setErrorMessage(MSGConstants.SUCCESS_MSG);
							session = null;
							criteria = null;
							return response;
						} else {
							System.out.println("response::>>1");
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMessage(MSGConstants.PIN_OFFSET_STORE_FAILED);
							session.close();
							session = null;
							criteria = null;
							return response;
						}
					}
				}
				System.out.println("response::>>");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.PIN_OFFSET_STORE_FAILED);
				return response;
			}
			Criteria crit = session.createCriteria(D350034.class);
			crit.add(Restrictions.eq("custNo", custNo));
			List<D350034> list2 = crit.list();
			if (list2 == null || list2.size() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.CUSTOMER_NOT_FOUND);
				return response;
			}
			Criteria critList = session.createCriteria(D350032.class);
			critList.add(Restrictions.eq("id.custNo", custNo));
			List<D350032> list3 = critList.list();
			System.out.println("list.size()::>>" + list.size());
			System.out.println("list2.size()::>>" + list2.size());
			System.out.println("list3.size()::>>" + list3.size());

			if (list3 == null || list3.size() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.CUSTOMER_NOT_FOUND);
				return response;
			}
			if (list.get(0).getMobBankingYn() == MSGConstants.MOB_YN) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.MOB_BANKING_NOT_ACTIVATED);
				return response;
			}
			if (list.get(0).getDbtrAuthNeeded() != 0 && list.get(0).getDbtrAuthDone() != 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.MOBILE_BANKING_REG_UNAUTH);
				return response;
			}
			if (list2.get(0).getStatus() == 2) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.MOBILE_BANKING_CUST_LOCKED);
				return response;
			}

			if (list2.get(0).getStatus() == 7) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.CHANGE_LOGIN_PIN);
				return response;
			}
			if (pinType.equalsIgnoreCase(MSGConstants.LOGIN_PWD)) {
				Query<D350031> q = session.createSQLQuery("UPDATE D350031 SET NewQueryPin =? WHERE CustNo =?");
				q.setParameter(0, offset);
				q.setParameter(1, custNo);
				int i = q.executeUpdate();
				System.out.println("i::>>" + i);
				Query<D350031> q1 = session.createSQLQuery("UPDATE D350031 SET TransPin =? WHERE CustNo = ?");
				q1.setParameter(0, OmniEncryptPassword.getEncryptedPwd(custNo.trim(), pin.trim()));
				q1.setParameter(1, custNo);
				int j = q1.executeUpdate();
				System.out.println("j::>>" + j);
				t.commit();
				session.close();
				session = null;
				t = null;
				if (i > 0 && j > 0) {
					response.setResponse(MSGConstants.SUCCESS);
					response.setErrorMessage(MSGConstants.SUCCESS_MSG);
					session = null;
					crit = null;
					criteria = null;
					critList = null;
					return response;
				} else {
					System.out.println("response::>>4");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.PIN_OFFSET_STORE_FAILED);
					session.close();
					session = null;
					crit = null;
					criteria = null;
					critList = null;
					return response;
				}
				// }
			} else if (pinType.equalsIgnoreCase(MSGConstants.TRANS_PWD)) {
				Query<D350031> q = session.createSQLQuery("UPDATE D350031 SET NewTransPin =? WHERE CustNo =?");
				q.setParameter(0, offset);
				q.setParameter(1, custNo);
				int i = q.executeUpdate();
				System.out.println("i::>>" + i);
				Query<D350031> q1 = session.createSQLQuery("UPDATE D350031 SET TransPassPin =? WHERE CustNo = ?");
				q1.setParameter(0, OmniEncryptPassword.getEncryptedPwd(custNo.trim(), pin.trim()));
				q1.setParameter(1, custNo);
				int j = q1.executeUpdate();
				System.out.println("j::>>" + j);
				t.commit();
				session.close();
				session = null;
				t = null;
				if (i > 0 && j > 0) {
					response.setResponse(MSGConstants.SUCCESS);
					response.setErrorMessage(MSGConstants.SUCCESS_MSG);
					session = null;
					crit = null;
					criteria = null;
					critList = null;
					return response;
				} else {
					System.out.println("response::>>1");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.PIN_OFFSET_STORE_FAILED);
					session.close();
					session = null;
					crit = null;
					criteria = null;
					critList = null;
					return response;
				}
				// }
			}
			System.out.println("response::>>");
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.PIN_OFFSET_STORE_FAILED);
			return response;
		} else if (channel.trim().equalsIgnoreCase(MSGConstants.OTHER_CHANNEL)) {
			Session session = HBUtil.getSessionFactory().openSession();
			Transaction t = session.beginTransaction();
			Criteria criteria = session.createCriteria(D350031.class);
			criteria.add(Restrictions.eq("custNo", custNo));
			List<D350031> list = criteria.list();
			AccountResponse resp = null;
			if (list == null || list.size() < 1) {
				// ========customer is not registered for mobile banking=====
				// D009011 d009011=getCustDetailsMaster(custNo);
				// System.out.println("Customer not found in
				// d009011::>>"+d009011);
				// Logger.error("Customer not found in d009011::>>"+d009011);
				// if(null==d009011)
				// {
				// response.setResponse(MSGConstants.ERROR);
				// response.setErrorMessage(MSGConstants.CUSTOMER_NOT_FOUND);
				// return response;
				// }
				try {
					Client client = ClientBuilder.newClient(new ClientConfig().register(LoggingFilter.class));
					WebTarget webTarget = client.target(ConfigurationLoader.getParameters(false).getProperty("MW_URI"))
							.path("request").path("fetchAccounts").queryParam("param1", custNo);
					Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_XML);
					Response res = invocationBuilder.post(Entity.entity(null, MediaType.APPLICATION_XML));
					resp = res.readEntity(AccountResponse.class);
					System.out.println("resp::>>" + resp);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}

				String quryPin = MSGConstants.NOT_USED_PROCESS;
				String transPin = MSGConstants.NOT_USED_PROCESS;
				String newQuryPin = MSGConstants.NOT_USED_PROCESS;
				String newTransPin = MSGConstants.NOT_USED_PROCESS;
				Date pinChangeDate = DateUtil.getDateFromStringYYYYMMDD("19000101");
				Date pinChangeTime = DateUtil.getDateFromStringYYYYMMDD("19000101");
				Date tPinChangeDate = DateUtil.getDateFromStringYYYYMMDD("19000101");
				Date tPinChangeTime = DateUtil.getDateFromStringYYYYMMDD("19000101");
				Calendar currentTimeCal = DateUtil.addingDateTime(new Date(), 90, "Days");
				Date nextTransPassPwdChgDate = currentTimeCal.getTime();
				Date nextTranPwdChgDate = currentTimeCal.getTime();
				Date nextTransPwdChgDt = currentTimeCal.getTime();

				String password = OmniEncryptPassword.getEncryptedPwd(custNo, pin);
				if (pinType.equalsIgnoreCase(MSGConstants.LOGIN_PWD)) {
					quryPin = password;
					newQuryPin = offset;
					pinChangeDate = DateUtil.convertDateFormat(new Date());
					pinChangeTime = DateUtil.getFormattedTime(new Date());
				} else if (pinType.equalsIgnoreCase(MSGConstants.TRANS_PWD)) {
					transPin = password;
					newTransPin = offset;
					tPinChangeDate = DateUtil.convertDateFormat(new Date());
					tPinChangeTime = DateUtil.getFormattedTime(new Date());
				}

				String add1 = resp.getAdd1();
				String add2 = resp.getAdd2();
				String add3 = resp.getAdd3();
				if (!add1.equalsIgnoreCase(MSGConstants.BLANK_STRING)) {
					add1 = add1.length() > 30 ? add1.substring(0, 30) : add1;
				}

				if (!add2.equalsIgnoreCase(MSGConstants.BLANK_STRING)) {
					add2 = add2.length() > 30 ? add2.substring(0, 30) : add2;
				}

				if (!add3.equalsIgnoreCase(MSGConstants.BLANK_STRING)) {
					add3 = add3.length() > 30 ? add3.substring(0, 30) : add3;
				}
				// List<D009022> accountList=DataUtils.getValidAccList(custNo);
				// List<D009022>
				// accountList=DataUtils.getAccountsFromCustNo(custNo);

				List<AccountDetails> accountList = resp.getAccountDetails();
				// ==================

				/*
				 * Client client = ClientBuilder.newClient( new ClientConfig().register(
				 * LoggingFilter.class ) ); WebTarget webTarget =
				 * client.target("http://10.100.5.238:9090/AccountOpening/").
				 * path("request").path("fetchAccounts").queryParam("param1", custNo.trim());
				 * 
				 * Invocation.Builder invocationBuilder =
				 * webTarget.request(MediaType.APPLICATION_XML);
				 */

				// =================
				/*
				 * if (null == accountList || accountList.isEmpty()) {
				 * System.out.println("Invalid Customer Number.");
				 * response.setResponse(MSGConstants.ERROR);
				 * response.setErrorMessage(MSGConstants.INVALID_CUSTOMER); return response; }
				 */
				String mobileNo = MSGConstants.BLANK_STRING;
				// List<D350078> otherChannelDetailsList =
				// DataUtils.getCustMobNo(custNo);
				// if (null == otherChannelDetailsList ||
				// otherChannelDetailsList.isEmpty()) {
				// System.out.println("Mobile number is not valid. Please
				// contact to branch.");
				// response.setResponse(MSGConstants.ERROR);
				// response.setErrorMessage(MSGConstants.MOBILE_NO_NOT_FOUND);
				// return response;
				// } else {
				// mobileNo =
				// otherChannelDetailsList.get(0).getId().getMobileNo();
				// }
				mobileNo = resp.getMobNo();
				System.out.println("Mobile Number::>>" + mobileNo);
				if (mobileNo.trim().length() != 10 && mobileNo.trim().length() != 12) {
					System.out.println("Mobile number is not valid. Please contact to branch.");
					Logger.error("Mobile number is not valid. Please contact to branch.");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.INVALID_MOBILE_NO);
					return response;
				}

				D350031 d350031 = DataUtils.prepareMobileBankingRegObject(custNo,
						resp.getAccountDetails().get(0).getName(), mobileNo, quryPin, transPin,
						Integer.valueOf(resp.getAccountDetails().get(0).getBrachCode()),
						DateUtil.convertDateFormat(new Date()), MSGConstants.MOB_BANKING_YES, pinChangeDate,
						pinChangeTime, tPinChangeDate, tPinChangeTime, add1, add2, add3, 0d, 0d,
						resp.getAccountDetails().get(0).getName().substring(0, 7), newQuryPin, newTransPin);
				System.out.println("d350031::>>" + d350031);
				System.out.println("accountList.size()::>>>" + accountList.size());
				int inc = 1;
				for (int count = 0; count < accountList.size(); count++) {

					System.out.println("in for loop ::>>" + count);
					logger.error("in for loop ::>>" + count);
					String productCode = DataUtils.getProductCode(accountList.get(count).getAccCode());
					// D009021 productMaster =
					// DataUtils.getProductMaster(String.valueOf(accountList.get(count).getId().getLbrCode()),
					// productCode);
					// if
					// (!MSGConstants.MODULE_TYPE_LIST.contains(Long.valueOf(productMaster.getModuleType())))
					// {
					// continue;
					// }
					// if (!new ArrayList<Long>(Arrays.asList(1l, 2l,
					// 12l)).contains(Long.valueOf(accountList.get(count).getAcctStat())))
					// {
					// continue;
					// }
					System.out.println("after continue");
					logger.error("after continue");
					String mmid = MSGConstants.NBIN + String.format("%03d", inc);
					System.out.println("MMID::>>" + mmid);
					logger.error("MMID:>>" + mmid);
					D350032 custxTreDtls = DataUtils.fetchMMID(custNo, mmid);
					System.out.println("custxTreDtls::>>" + custxTreDtls);
					// logger.error("custxTreDtls::>>"+custxTreDtls.toString());
					custxTreDtls = DataUtils.prepareMMIDMasterObject(custNo, resp.getAccountDetails().get(0).getName(),
							mobileNo, mmid, Integer.valueOf(accountList.get(count).getBrachCode().trim()),
							accountList.get(count).getAccCode());
					// DataUtils.storeD350032(custxTreDtls);
					System.out.println("custxTreDtls::>>>" + custxTreDtls);
					logger.error("custxTreDtls::>>>" + custxTreDtls);
					D350001 otpObj = DataUtils.searchOTP(Integer.valueOf(accountList.get(count).getBrachCode()),
							accountList.get(count).getAccCode());
					System.out.println("otpObj::>>>" + otpObj);
					if (null == otpObj) {
						D001004 systemParameter = DataUtils.getSystemParameter(0, MSGConstants.SETOTPTRNLMT);
						Double setOtpTrnLmt = systemParameter == null ? 0d : Double.valueOf(systemParameter.getValue());
						D001004 sysPara = DataUtils.getSystemParameter(0, MSGConstants.SETDAILYTRNLMT);
						System.out.println("sysPara::>>>" + sysPara);
						Double setDailyTrnLmt = sysPara == null ? 0d : Double.valueOf(sysPara.getValue());
						System.out.println("setDailyTrnLmt::>>>" + setDailyTrnLmt);

						D001004 system = DataUtils.getSystemParameter(0, MSGConstants.NOOFTRNS);
						System.out.println("system::>>" + system);

						int noOfTrns = 10;// system == null ? 0 :
											// Integer.valueOf(system.getValue());
						System.out.println("noOfTrns::>>" + noOfTrns);

						D001004 parameter = DataUtils.getSystemParameter(0, MSGConstants.SETOTPEXPTIME);
						System.out.println("parameter::>>" + parameter);

						int setOtpExpTime = 5;// parameter == null ? 0 :
												// Integer.valueOf(parameter.getValue());
						System.out.println("setOtpExpTime::>>" + setOtpExpTime);
						otpObj = DataUtils.prepareOtpObject(Integer.valueOf(accountList.get(count).getBrachCode()),
								accountList.get(count).getAccCode(), setOtpTrnLmt, setDailyTrnLmt, noOfTrns,
								setOtpExpTime, "Created By IMPS Application", custNo);
					}
					try {
						// Transaction transaction=session.beginTransaction();

						System.out.println("storing objects");
						logger.error("storing objects");
						session.save(custxTreDtls);
						session.save(otpObj);
						session.flush();

					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					inc++;
				}
				D350034 custOtherInfo = DataUtils.getMobCustStatus(custNo);

				if (null == custOtherInfo) {
					custOtherInfo = DataUtils.prepareCustOtherInfoObject(custNo,
							Integer.valueOf(resp.getAccountDetails().get(0).getBrachCode()), 1, 1, 0, 0, 0, 0, 3, 3,
							nextTransPwdChgDt, nextTranPwdChgDate, nextTransPassPwdChgDate);
				} else {
					custOtherInfo.setNextTransPassPwdChgDt(nextTransPassPwdChgDate);
					custOtherInfo.setNextTranPwdChgDt(nextTranPwdChgDate);
					custOtherInfo.setNextTransPwdChgDt(nextTransPwdChgDt);
				}
				// save obj

				D350033 pinStatus = DataUtils.preparePinStatusObject(custNo, 1, DateUtil.convertDateFormat(new Date()),
						1, DateUtil.convertDateFormat(new Date()));
				// impsService.savePinStatusWS(pinStatus);

				D350035 custOtherDtls = DataUtils.prepareCustOtherDtls(custNo, DateUtil.convertDateFormat(new Date()),
						DateUtil.getFormattedTime(new Date()), 'N', "New Pin Generated");

				try {
					session.save(d350031);
					session.save(pinStatus);
					session.save(custOtherInfo);
					session.save(custOtherDtls);
					session.flush();
					t.commit();
					session.close();
					response.setResponse(MSGConstants.SUCCESS);
					response.setErrorMessage(MSGConstants.SUCCESS_MSG);
					return response;
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				// =======================

				if (pinType.equalsIgnoreCase(MSGConstants.LOGIN_PWD)) {
					if (null == list.get(0).getNewQueryPin()
							|| list.get(0).getNewQueryPin().trim().equalsIgnoreCase("")) {
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage(MSGConstants.PIN_OFFSET_STORE_FAILED);
						session.clear();
						session = null;
						criteria = null;
						return response;
					} else {
						Query<D350031> q = session.createQuery("UPDATE D350031 SET NewQueryPin =? WHERE custNo =?");
						q.setParameter(0, offset);
						q.setParameter(1, custNo);
						int i = q.executeUpdate();
						t.commit();
						session.close();
						session = null;
						t = null;
						if (i > 0) {
							response.setResponse(MSGConstants.SUCCESS);
							response.setErrorMessage(MSGConstants.SUCCESS_MSG);
							session = null;
							criteria = null;
							return response;
						} else {
							System.out.println("response::>>4");
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMessage(MSGConstants.PIN_OFFSET_STORE_FAILED);
							session.close();
							session = null;
							criteria = null;
							return response;
						}
					}
				} else if (pinType.equalsIgnoreCase(MSGConstants.TRANS_PWD)) {
					if (null == list.get(0).getNewTransPin()
							|| list.get(0).getNewTransPin().trim().equalsIgnoreCase("")) {
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage(MSGConstants.PIN_OFFSET_STORE_FAILED);
						session.close();
						session = null;
						criteria = null;

						return response;
					} else {
						Query<D350031> q = session.createQuery("UPDATE D350031 SET NewTransPin =? WHERE custNo =?");
						q.setParameter(0, offset);
						q.setParameter(1, custNo);
						int i = q.executeUpdate();
						t.commit();
						session.close();
						session = null;
						t = null;
						if (i > 0) {
							response.setResponse(MSGConstants.SUCCESS);
							response.setErrorMessage(MSGConstants.SUCCESS_MSG);
							session = null;
							criteria = null;
							return response;
						} else {
							System.out.println("response::>>1");
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMessage(MSGConstants.PIN_OFFSET_STORE_FAILED);
							session = null;
							criteria = null;
							return response;
						}
					}

				}
				System.out.println("response::>>");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.PIN_OFFSET_STORE_FAILED);
				return response;
			}
			Criteria crit = session.createCriteria(D350034.class);
			crit.add(Restrictions.eq("custNo", custNo));
			List<D350034> list2 = crit.list();
			if (list2 == null || list2.size() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.CUSTOMER_NOT_FOUND);
				return response;
			}
			Criteria critList = session.createCriteria(D350032.class);
			critList.add(Restrictions.eq("id.custNo", custNo));
			List<D350032> list3 = critList.list();
			System.out.println("list.size()::>>" + list.size());
			System.out.println("list2.size()::>>" + list2.size());
			System.out.println("list3.size()::>>" + list3.size());

			if (list3 == null || list3.size() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.CUSTOMER_NOT_FOUND);
				return response;
			}
			if (list.get(0).getMobBankingYn() == MSGConstants.MOB_YN) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.MOB_BANKING_NOT_ACTIVATED);
				return response;
			}
			if (list.get(0).getDbtrAuthNeeded() != 0 && list.get(0).getDbtrAuthDone() != 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.MOBILE_BANKING_REG_UNAUTH);
				return response;
			}
			if (list2.get(0).getStatus() == 2) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.MOBILE_BANKING_CUST_LOCKED);
				return response;
			}

			if (list2.get(0).getStatus() == 7) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.CHANGE_LOGIN_PIN);
				return response;
			}
			if (pinType.equalsIgnoreCase(MSGConstants.LOGIN_PWD)) {
				Query<D350031> q = session.createSQLQuery("UPDATE D350031 SET NewQueryPin =? WHERE CustNo =?");
				q.setParameter(0, offset);
				q.setParameter(1, custNo);
				int i = q.executeUpdate();
				System.out.println("i::>>" + i);
				Query<D350031> q1 = session.createSQLQuery("UPDATE D350031 SET TransPin =? WHERE CustNo = ?");
				q1.setParameter(0, OmniEncryptPassword.getEncryptedPwd(custNo.trim(), pin.trim()));
				q1.setParameter(1, custNo);
				int j = q1.executeUpdate();
				System.out.println("j::>>" + j);
				t.commit();
				session.close();
				session = null;
				t = null;
				if (i > 0 && j > 0) {
					response.setResponse(MSGConstants.SUCCESS);
					response.setErrorMessage(MSGConstants.SUCCESS_MSG);
					session = null;
					crit = null;
					criteria = null;
					critList = null;
					return response;
				} else {
					System.out.println("response::>>4");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.PIN_OFFSET_STORE_FAILED);
					session.close();
					session = null;
					crit = null;
					criteria = null;
					critList = null;
					return response;
				}
				// }
			} else if (pinType.equalsIgnoreCase(MSGConstants.TRANS_PWD)) {
				Query<D350031> q = session.createSQLQuery("UPDATE D350031 SET NewTransPin =? WHERE CustNo =?");
				q.setParameter(0, offset);
				q.setParameter(1, custNo);
				int i = q.executeUpdate();
				System.out.println("i::>>" + i);
				Query<D350031> q1 = session.createSQLQuery("UPDATE D350031 SET TransPassPin =? WHERE CustNo = ?");
				q1.setParameter(0, OmniEncryptPassword.getEncryptedPwd(custNo.trim(), pin.trim()));
				q1.setParameter(1, custNo);
				int j = q1.executeUpdate();
				System.out.println("j::>>" + j);
				t.commit();
				session.close();
				session = null;
				t = null;
				if (i > 0 && j > 0) {
					response.setResponse(MSGConstants.SUCCESS);
					response.setErrorMessage(MSGConstants.SUCCESS_MSG);
					session = null;
					crit = null;
					criteria = null;
					critList = null;
					return response;
				} else {
					System.out.println("response::>>1");
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.PIN_OFFSET_STORE_FAILED);
					session = null;
					crit = null;
					criteria = null;
					critList = null;
					return response;
				}

			}
			System.out.println("response::>>");
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.PIN_OFFSET_STORE_FAILED);
			return response;
		}
		response.setResponse(MSGConstants.ERROR);
		response.setErrorMessage(MSGConstants.INAVLID_CHANNEL);
		return response;
	}

	public D350034 prepareCustOtherInfoObject(String custNo, int lbrCode, int status, int transPassPinStatus,
			int noOfBadLogins, int noOfBadLoginsFndTr, int activeInStn, int activeSrNoInd, int maxBadLiPerDay,
			int maxBadLiPerInst, Date nextTransPwdChgDt, Date nextTranPwdChgDate, Date nextTransPassPwdChgDate) {
		D350034 custOtherInfo = new D350034();
		custOtherInfo.setCustNo(custNo);
		custOtherInfo.setUsrBrCode(lbrCode);
		custOtherInfo.setStatus(status);
		custOtherInfo.setAutoLogoutAfterSecs(0);
		custOtherInfo.setPwdChgPeriodDays(0);
		custOtherInfo.setMinLiPeriodDays(0);
		custOtherInfo.setMaxBadLiPerDay(maxBadLiPerDay);
		custOtherInfo.setMaxBadLiPerInst(maxBadLiPerInst);
		custOtherInfo.setPwdNegativesMod(0);
		custOtherInfo.setNextTransPwdChgDt(nextTransPwdChgDt);
		custOtherInfo.setNextTransPassPwdChgDt(nextTransPassPwdChgDate);
		custOtherInfo.setLastSysLiDt(DateUtil.getCurrentDate());
		custOtherInfo.setLastSysLiTime(DateUtil.getDateFromStringYYYYMMDD("19000101"));
		custOtherInfo.setNextSysLiDt(DateUtil.getDateFromStringYYYYMMDD("19000101"));
		custOtherInfo.setBadLoginsDt(DateUtil.getDateFromStringYYYYMMDD("19000101"));
		custOtherInfo.setNoOfBadLogins(noOfBadLogins);
		custOtherInfo.setNoOfBadLoginsFndTr(noOfBadLoginsFndTr);
		custOtherInfo.setActiveInStn(activeInStn);
		custOtherInfo.setActiveSrNoInd(activeSrNoInd);
		custOtherInfo.setDbtrAddMk(999998);
		custOtherInfo.setDbtrAddMb(lbrCode);
		custOtherInfo.setDbtrAddMs(0);
		custOtherInfo.setDbtrAddMd(DateUtil.convertDateFormat(new Date()));
		custOtherInfo.setDbtrAddMt(DateUtil.getFormattedTime(new Date()));
		custOtherInfo.setDbtrAddCk(999998);
		custOtherInfo.setDbtrAddCb(lbrCode);
		custOtherInfo.setDbtrAddCs(0);
		custOtherInfo.setDbtrAddCd(DateUtil.convertDateFormat(new Date()));
		custOtherInfo.setDbtrAddCt(DateUtil.getFormattedTime(new Date()));
		custOtherInfo.setDbtrLupdMk(999998);
		custOtherInfo.setDbtrLupdMb(lbrCode);
		custOtherInfo.setDbtrLupdMs(0);
		custOtherInfo.setDbtrLupdMd(DateUtil.convertDateFormat(new Date()));
		custOtherInfo.setDbtrLupdMt(DateUtil.getFormattedTime(new Date()));
		custOtherInfo.setDbtrLupdCk(999998);
		custOtherInfo.setDbtrLupdCb(lbrCode);
		custOtherInfo.setDbtrLupdCs(0);
		custOtherInfo.setDbtrLupdCd(DateUtil.convertDateFormat(new Date()));
		custOtherInfo.setDbtrLupdCt(DateUtil.getFormattedTime(new Date()));
		custOtherInfo.setDbtrTauthDone(1);
		custOtherInfo.setDbtrRecStat(1);
		custOtherInfo.setDbtrAuthDone(1);
		custOtherInfo.setDbtrAuthNeeded(0);
		custOtherInfo.setDbtrUpdtChkId(1);
		custOtherInfo.setDbtrLhisTrnNo(0);
		custOtherInfo.setNextTranPwdChgDt(nextTranPwdChgDate);
		custOtherInfo.setNextMobPwdChgDt(DateUtil.getDateFromStringYYYYMMDD("19000101"));
		custOtherInfo.setTranPinStatus(1);
		custOtherInfo.setTransPassPinStatus(transPassPinStatus);
		return custOtherInfo;
	}

	public static OtherChannelServiceResponse genMMID(String custNo, String accNo) {
		OtherChannelServiceResponse response = new OtherChannelServiceResponse();
		D009022 sourceAccount = DataUtils.getAccount(accNo.trim());
		if (null == sourceAccount) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
			return response;
		}
		TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount, String.valueOf(10),
				"C");
		if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(res.getErrorMsg());
			return response;
		}
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		Criteria criteria = session.createCriteria(D350032.class);
		criteria.add(Restrictions.eq("id.custNo", custNo));

		List<D350032> list = criteria.list();
		System.out.println("list.size()::>>" + list.size());
		Query q = session.createSQLQuery("select max(MMID)+1 from dbo.D350032 where CustNo='" + custNo + "'");
		String mmid = "" + q.getResultList().get(0);
		criteria.add(Restrictions.eq("id.custNo", custNo));
		criteria.add(Restrictions.eq("acctNo", sourceAccount.getId().getPrdAcctId()));
		criteria.add(Restrictions.eq("brCode", sourceAccount.getId().getLbrCode()));

		List<D350032> list1 = criteria.list();
		System.out.println("list1.size()::>>" + list1.size());
		if (list1 == null || list1.size() < 1) {
			D350001 otpObj = DataUtils.searchOTP(sourceAccount.getId().getLbrCode(),
					sourceAccount.getId().getPrdAcctId());
			System.out.println("otpObj::>>>" + otpObj);
			if (null == otpObj) {
				D001004 systemParameter = DataUtils.getSystemParameter(0, MSGConstants.SETOTPTRNLMT);
				Double setOtpTrnLmt = systemParameter == null ? 0d : Double.valueOf(systemParameter.getValue());
				D001004 sysPara = DataUtils.getSystemParameter(0, MSGConstants.SETDAILYTRNLMT);
				System.out.println("sysPara::>>>" + sysPara);
				Double setDailyTrnLmt = sysPara == null ? 0d : Double.valueOf(sysPara.getValue());
				System.out.println("setDailyTrnLmt::>>>" + setDailyTrnLmt);

				D001004 system = DataUtils.getSystemParameter(0, MSGConstants.NOOFTRNS);
				System.out.println("system::>>" + system);

				int noOfTrns = 10;// system == null ? 0 :
									// Integer.valueOf(system.getValue());
				System.out.println("noOfTrns::>>" + noOfTrns);

				D001004 parameter = DataUtils.getSystemParameter(0, MSGConstants.SETOTPEXPTIME);
				System.out.println("parameter::>>" + parameter);

				int setOtpExpTime = 5;// parameter == null ? 0 :
										// Integer.valueOf(parameter.getValue());
				System.out.println("setOtpExpTime::>>" + setOtpExpTime);
				otpObj = DataUtils.prepareOtpObject(sourceAccount.getId().getLbrCode(),
						sourceAccount.getId().getPrdAcctId(), setOtpTrnLmt, setDailyTrnLmt, noOfTrns, setOtpExpTime,
						"Created By IMPS Application", custNo);
			}
			D350032 d350032 = DataUtils.prepareMMIDMasterObject(custNo, sourceAccount.getLongName().trim(),
					list.get(0).getId().getMobileNo(), String.valueOf(mmid), sourceAccount.getId().getLbrCode(),
					sourceAccount.getId().getPrdAcctId());
			System.out.println("d350032::>>" + d350032);
			try {
				session.save(d350032);
				session.save(otpObj);
				t.commit();
				session.close();
				session = null;
				t = null;
				response.setResponse(MSGConstants.SUCCESS);
				response.setErrorMessage("Dear Customer, your have successfully generated mmid as " + mmid
						+ " for an account " + sourceAccount.getId().getLbrCode() + "/"
						+ sourceAccount.getId().getPrdAcctId().substring(0, 8).trim() + "/"
						+ Integer.valueOf(sourceAccount.getId().getPrdAcctId().substring(
								sourceAccount.getId().getPrdAcctId().length() - 16,
								sourceAccount.getId().getPrdAcctId().length() - 8)));
				return response;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				session = null;
				t = null;
				response.setResponse(MSGConstants.ERROR);
				response.setResponse(MSGConstants.GEN_MMID_FAILURE);
				return response;
			}
		}
		response.setResponse(MSGConstants.ERROR);
		response.setErrorMessage(MSGConstants.MMID_EXIST);
		session.close();
		t = null;
		session = null;
		return response;
	}

	public static OtherChannelServiceResponse cancelMMID(String custNo, String mmid) {
		OtherChannelServiceResponse response = new OtherChannelServiceResponse();
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D350032.class);
		criteria.add(Restrictions.eq("id.custNo", custNo));
		List<D350032> list = criteria.list();
		criteria.add(Restrictions.eq("id.mmid", mmid));
		List<D350032> list2 = criteria.list();
		System.out.println("list.size()::>>>" + list.size());
		if (list == null || list.size() < 1) {
			session.close();
			session = null;
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.RECORD_NOT_FOUND);
			return response;
		}
		if (list2 == null || list2.size() < 1) {
			session.close();
			session = null;
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.RECORD_NOT_FOUND);
			return response;
		}
		if (list.size() == 1) {
			session.close();
			session = null;
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.SINGLE_MMID);
			return response;
		}
		Transaction t = session.beginTransaction();
		Query q = session.createSQLQuery("DELETE FROM dbo.D350032 WHERE CustNo =? AND MMID = ?");
		q.setParameter(0, custNo);
		q.setParameter(1, mmid);
		int result = q.executeUpdate();
		t.commit();
		session.close();
		session = null;
		t = null;
		if (result > 0) {
			session.close();
			session = null;
			response.setResponse(MSGConstants.SUCCESS);
			response.setErrorMessage(
					"Dear Customer,As per your request your MMID " + mmid + " is cancelled successfully.");
			return response;
		}

		response.setResponse(MSGConstants.ERROR);
		response.setErrorMessage(MSGConstants.MMID_CANCELATION_FAIL);
		return response;

	}

	public static OtherChannelServiceResponse impsLogin(String custNo, String pass) {
		OtherChannelServiceResponse response = new OtherChannelServiceResponse();
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D350031.class);
		criteria.add(Restrictions.eq("custNo", custNo));
		List<D350031> list = criteria.list();
		if (null == list || list.size() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_CUSTOMER);
			session.close();
			session = null;
			criteria = null;
			list = null;
			return response;
		}
		Criteria criteria2 = session.createCriteria(D350034.class);
		criteria2.add(Restrictions.eq("custNo", custNo));
		List<D350034> list2 = criteria2.list();
		System.out.println("list.size()::>>>" + list.size());
		if (list2 == null || list2.size() < 1) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.RECORD_NOT_FOUND);
			session.close();
			session = null;
			criteria = null;
			criteria2 = null;
			list = null;
			list2 = null;
			return response;
		}
		if (list2.get(0).getStatus() == 2) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.MOBILE_BANKING_CUST_LOCKED);
			session.close();
			session = null;
			criteria = null;
			criteria2 = null;
			list = null;
			list2 = null;
			return response;
		}

		if (list2.get(0).getStatus() == 7 && list2.get(0).getTranPinStatus() == 7
				&& list2.get(0).getTransPassPinStatus() == 7) {
			String str = list.get(0).getRegNo().substring(4, 12);
			str = (Integer.valueOf(str)).toString();
			if (!list.get(0).getTransPin().trim()
					.equals(OmniEncryptPassword.getEncryptedPwd(str.toUpperCase(), pass))) {
				logger.error("Invalid Login Password. :: " + custNo);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_LOGIN_PASS);
				session.close();
				session = null;
				criteria = null;
				criteria2 = null;
				list = null;
				list2 = null;
				return response;
			}
		} else {
			if (!list.get(0).getTransPin().trim().equalsIgnoreCase(
					String.valueOf(OmniEncryptPassword.getEncryptedPwd(custNo.toUpperCase(), pass).trim()))) {
				logger.error("Invalid Login Password. :: " + custNo);
				logger.error("list.get(0).getTransPin() :: " + list.get(0).getTransPin());
				logger.error("Encryted::>>" + OmniEncryptPassword.getEncryptedPwd(custNo.toUpperCase(), pass).trim());
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_LOGIN_PASS);
				session.close();
				session = null;
				criteria = null;
				criteria2 = null;
				list = null;
				list2 = null;
				return response;
			}
			if (list2.get(0).getTranPinStatus() == 7) {
				logger.error("Please Change your Login Password. :: " + custNo);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.FORCE_CHANGE_PASSWORD);
				session.close();
				session = null;
				criteria = null;
				criteria2 = null;
				list = null;
				list2 = null;
				return response;
			}
		}

		CustomerDetails custValid = validCustomer(custNo);
		if (custValid.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
			response.setResponse(custValid.getResponse());
			response.setErrorMessage(custValid.getErrorMsg());
			session.close();
			session = null;
			criteria = null;
			criteria2 = null;
			list = null;
			list2 = null;
		} else {
			response.setResponse(MSGConstants.SUCCESS);
			response.setErrorMessage(MSGConstants.SUCCESS_MSG);
			session.close();
			session = null;
			criteria = null;
			criteria2 = null;
			list = null;
			list2 = null;
		}
		return response;
	}

	public static StopChequeResponse stopCheque(String custNo, String mmid, String chequeNo, String remark,
			String accNo15digit, String channel) {
		// TODO Auto-generated method stub
		StopChequeResponse stopChqRes = new StopChequeResponse();
		if (channel.equalsIgnoreCase(MSGConstants.IMPS_CHANNEL)) {
			D350032 custxTreDtls = DataUtils.fetchMMID(custNo, mmid);
			logger.error("custxTreDtls::>>>" + custxTreDtls);
			if (null == custxTreDtls) {
				logger.error("Invalid Custno/MMID.");
				stopChqRes.setErrorCode(MSGConstants.NOT_USED_PROCESS);
				stopChqRes.setErrorMessage("Invalid Custno/MMID.");
				stopChqRes.setResponse(MSGConstants.ERROR);
				return stopChqRes;
			}
			D009022 account = DataUtils.getAccountDetails(custxTreDtls.getBrCode(), custxTreDtls.getAcctNo());
			if (account != null) {
				String issuedTo = custxTreDtls.getAcctNo();
				D009044 issuedInstruments = DataUtils.getChequeStatus(custxTreDtls.getBrCode(), issuedTo, chequeNo);
				logger.error("issuedInstruments::>>>" + issuedInstruments.toString());
				// need to discuss issuetype
				if (issuedInstruments != null) {
					if (issuedInstruments.getStatus() == MSGConstants.STOPPEDPAYMENTSTATUS) {
						logger.error("Cheque is already stopped found for ChequeNo :: " + chequeNo);
						stopChqRes.setErrorCode("9");
						stopChqRes.setErrorMessage("Cheque is already stopped.");
						stopChqRes.setResponse(MSGConstants.ERROR);
						return stopChqRes;
					} else if (issuedInstruments.getStatus() == MSGConstants.ENCASHEDSTATUS) {
						logger.error("Cheque is Encashed for ChequeNo :: " + chequeNo);
						stopChqRes.setErrorCode("10");
						stopChqRes.setErrorMessage("Cheque is Encashed.");
						stopChqRes.setResponse(MSGConstants.ERROR);
						return stopChqRes;
					} else if (issuedInstruments.getStatus() == MSGConstants.ENCASHEDSTATUS) {
						logger.error("Cheque is Encashed for ChequeNo :: " + chequeNo);
						stopChqRes.setErrorCode("10");
						stopChqRes.setErrorMessage("Cheque is Encashed.");
						stopChqRes.setResponse(MSGConstants.ERROR);
						return stopChqRes;
					} else if (issuedInstruments.getStatus() == MSGConstants.ISSUEDSTATUS) {
						// stop cheque
						D010010 d010010 = DataUtils.prepareStopPaymentObj(issuedInstruments.getId().getLbrCode(),
								issuedInstruments.getId().getIssuedTo(), chequeNo,
								issuedInstruments.getId().getInsType(), account.getLongName().trim(), remark, 0d);
						Session session = HBUtil.getSessionFactory().openSession();
						Transaction transaction = session.beginTransaction();
						try {
							session.saveOrUpdate(d010010);
							Query<D009044> query = session.createQuery(
									"UPDATE D009044 SET Status = ? WHERE LBrCode = ? AND IssuedTo = ?  AND InstruNo = ?");
							query.setParameter(0, MSGConstants.STOPPEDPAYMENTSTATUS);
							query.setParameter(1, account.getId().getLbrCode());
							query.setParameter(2, account.getId().getPrdAcctId());
							query.setParameter(3, chequeNo);
							query.executeUpdate();
							transaction.commit();
							session.close();
							session = null;
							transaction = null;
							stopChqRes.setErrorCode("0");
							stopChqRes.setErrorMessage(MSGConstants.SUCCESS_MSG);
							stopChqRes.setResponse(MSGConstants.SUCCESS);
							return stopChqRes;
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
							stopChqRes.setErrorMessage(MSGConstants.ERROR_MSG);
							stopChqRes.setResponse(MSGConstants.ERROR);
							return stopChqRes;
						}

					} else {
						// need to discuss
					}
				} else {
					logger.error("Cheque number not found for ChequeNo :: " + chequeNo);
					stopChqRes.setErrorCode("11");
					stopChqRes.setErrorMessage("Cheque number not found.");
					stopChqRes.setResponse(MSGConstants.ERROR);
					return stopChqRes;
				}
			} else {
				logger.error("Account not found for ChequeNo :: " + chequeNo);
				stopChqRes.setErrorCode("2");
				stopChqRes.setErrorMessage(MSGConstants.ERRORS_REGARDING_CUSTOMER_ACCOUNT);
				stopChqRes.setResponse(MSGConstants.ERROR);
				return stopChqRes;
			}
		}
		// ======================below code for OTHER
		// Channels=============================
		/*
		 * D350032 custxTreDtls = DataUtils.fetchMMID(custNo, mmid); if(null ==
		 * custxTreDtls){ logger.error("Invalid Custno/MMID.");
		 * stopChqRes.setErrorCode(MSGConstants.NOT_USED_PROCESS);
		 * stopChqRes.setErrorMessage("Invalid Custno/MMID.");
		 * stopChqRes.setResponse(MSGConstants.ERROR); return stopChqRes; }
		 */
		D009022 account = DataUtils.getAccount(accNo15digit);
		logger.error("account::>>>" + account);
		if (account != null) {
			D009044 issuedInstruments = DataUtils.getChequeStatus(account.getId().getLbrCode(),
					account.getId().getPrdAcctId(), chequeNo);
			// need to discuss issuetype
			if (issuedInstruments != null) {
				if (issuedInstruments.getStatus() == MSGConstants.STOPPEDPAYMENTSTATUS) {
					logger.error("Cheque is already stopped found for ChequeNo :: " + chequeNo);
					stopChqRes.setErrorCode("9");
					stopChqRes.setErrorMessage("Cheque is already stopped.");
					stopChqRes.setResponse(MSGConstants.ERROR);
					return stopChqRes;
				} else if (issuedInstruments.getStatus() == MSGConstants.ENCASHEDSTATUS) {
					logger.error("Cheque is Encashed for ChequeNo :: " + chequeNo);
					stopChqRes.setErrorCode("10");
					stopChqRes.setErrorMessage("Cheque is Encashed.");
					stopChqRes.setResponse(MSGConstants.ERROR);
					return stopChqRes;
				} else if (issuedInstruments.getStatus() == MSGConstants.ISSUEDSTATUS) {
					// stop cheque

					D010010 d010010 = DataUtils.prepareStopPaymentObj(issuedInstruments.getId().getLbrCode(),
							issuedInstruments.getId().getIssuedTo(), chequeNo, issuedInstruments.getId().getInsType(),
							account.getLongName().trim(), remark, 0d);
					Session session = HBUtil.getSessionFactory().openSession();
					Transaction transaction = session.beginTransaction();
					try {
						session.saveOrUpdate(d010010);
						Query<D009044> query = session.createQuery(
								"UPDATE D009044 SET Status = ? WHERE LBrCode = ? AND IssuedTo = ?  AND InstruNo = ?");
						query.setParameter(0, MSGConstants.STOPPEDPAYMENTSTATUS);
						query.setParameter(1, account.getId().getLbrCode());
						query.setParameter(2, account.getId().getPrdAcctId());
						query.setParameter(3, chequeNo);
						query.executeUpdate();
						transaction.commit();
						session.close();
						session = null;
						transaction = null;
						stopChqRes.setErrorCode("0");
						stopChqRes.setErrorMessage(MSGConstants.SUCCESS_MSG);
						stopChqRes.setResponse(MSGConstants.SUCCESS);
						return stopChqRes;
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						stopChqRes.setErrorMessage(MSGConstants.ERROR_MSG);
						stopChqRes.setResponse(MSGConstants.ERROR);
						return stopChqRes;
					}
				} else {
					// need to discuss
				}
			} else {
				logger.error("Cheque number not found for ChequeNo :: " + chequeNo);
				stopChqRes.setErrorCode("11");
				stopChqRes.setErrorMessage("Cheque number not found.");
				stopChqRes.setResponse(MSGConstants.ERROR);
				return stopChqRes;
			}
		} else {
			logger.error("Account not found for ChequeNo :: " + chequeNo);
			stopChqRes.setErrorCode("2");
			stopChqRes.setErrorMessage(MSGConstants.ERRORS_REGARDING_CUSTOMER_ACCOUNT);
			stopChqRes.setResponse(MSGConstants.ERROR);
			return stopChqRes;
		}
		return stopChqRes;
	}

	public static OtherChannelServiceResponse getChequeStatus(String custNo, String mmidNo, String chequeNo,
			String accountNo, String channel) {
		OtherChannelServiceResponse chequeStatus = new OtherChannelServiceResponse();

		int lbrCode = 0;
		String acctNo = MSGConstants.BLANK_STRING;
		if (channel.equalsIgnoreCase(MSGConstants.IMPS_CHANNEL)) {
			D350032 d350032 = DataUtils.fetchMMID(custNo, mmidNo);
			if (null == d350032) {
				logger.error("Account not found");
				chequeStatus.setResponse(MSGConstants.ERROR);
				chequeStatus.setErrorMessage(MSGConstants.INVALID_ACCOUNT_NO);
				return chequeStatus;
			}
			lbrCode = d350032.getBrCode();
			acctNo = d350032.getAcctNo();
		} else {
			D009022 account = DataUtils.getAccount(accountNo);
			if (null == account) {
				logger.error("Account not found");
				chequeStatus.setResponse(MSGConstants.ERROR);
				chequeStatus.setErrorMessage(MSGConstants.INVALID_ACCOUNT_NO);
				return chequeStatus;
			}
			lbrCode = account.getId().getLbrCode();
			acctNo = account.getId().getPrdAcctId();
		}
		D009044 issuedInstruments = DataUtils.fetchIssuedInstruments(lbrCode, acctNo, chequeNo);
		if (null == issuedInstruments) {
			logger.error("Cheque No not found");
			chequeStatus.setResponse(MSGConstants.ERROR);
			chequeStatus.setErrorMessage("Cheque No not found");
			return chequeStatus;
		}
		String particular = MSGConstants.BLANK_STRING;
		/*
		 * if(issuedInstruments.getStatus() == Byte.valueOf("2")){ D09040 voucher =
		 * DataUtils.getVouchersList(issuedInstruments.getId().getLbrCode(),
		 * issuedInstruments.getEncashDate(), issuedInstruments.getBatchCode(),
		 * issuedInstruments.getSetNo(), issuedInstruments.getScrollNo()); if(null !=
		 * voucher){ particular = voucher.getParticulars(); } }
		 */
		chequeStatus.setResponse(MSGConstants.SUCCESS);
		chequeStatus.setErrorMessage(MSGConstants.SUCCESS_MSG);
		chequeStatus.setOutput(new String[] { particular, chequeNo,
				DateUtil.getDateStringFormat(issuedInstruments.getEncashDate()),
				DataUtils.getStatusDescriptionWS(issuedInstruments.getStatus()), issuedInstruments.getStatus() + "" });
		return chequeStatus;
	}

	public static IMPSFetchLoanAccountDetailsResponse loanAccountDetails(int lbrCode, String accNo) throws Exception {
		IMPSFetchLoanAccountDetailsResponse accountDetailsList = new IMPSFetchLoanAccountDetailsResponse();
		List<Object[]> loanList = DataUtils.getLoanAccountDetails(accNo, lbrCode);
		logger.error("Loan Account List::>>>" + loanList);
		if (loanList == null) {
			logger.error("Loan account not found.");
			accountDetailsList.setResponse(MSGConstants.ERROR);
			accountDetailsList.setErrorMessage(MSGConstants.LOAN_ACCOUNT_NOT_FOUND);
			return accountDetailsList;
		}
		DecimalFormat format = new DecimalFormat("0.00");
		// String formatted = format.format(number);
		List<LoanAccountDetails> accountList = new ArrayList<LoanAccountDetails>();
		Object[] object = new Object[1];
		for (Iterator<Object[]> iterator = loanList.iterator(); iterator.hasNext();) {
			BigDecimal three, four, seven;

			object = iterator.next();
			// Byte status= (Byte) object[7];
			String one = (String) object[0];
			String two = (String) object[1];
			if (!ConfigurationLoader.getParameters(false).getProperty("DATABASE").equalsIgnoreCase("ORACLE")) {
				three = BigDecimal.valueOf((Double) object[2]);
				if (object[3] == null)
					four = new BigDecimal(0.0);
				else
					four = BigDecimal.valueOf((Double) object[3]);
				seven = BigDecimal.valueOf((Double) object[6]);
			} else {
				three = (BigDecimal) object[2];
				if (object[3] == null)
					four = new BigDecimal(0.0);
				else
					four = (BigDecimal) object[3];
				seven = (BigDecimal) object[6];
			}
			Date five = (Date) object[4];
			Date eigth = (Date) object[7];
			BigDecimal six = BigDecimal.valueOf(Long.valueOf(object[5] + ""));
			LoanAccountDetails acc = new LoanAccountDetails();
			if (null == three)
				three = new BigDecimal(0);
			acc.setTotalSansactionLimit(format.format(three.doubleValue()));
			if (null == four)
				four = new BigDecimal(0);
			acc.setInstlAmt(format.format(four.doubleValue()));
			acc.setExpDate(DateUtil.getStringDateNew(five));
			acc.setCustomerno(six.longValue());
			acc.setLongName(two);
			acc.setAccountNo(DataUtils.get15DigitAccountNumber(lbrCode, one));
			acc.setAccStatus(1L);
			acc.setEffDate(DateUtil.getStringDateNew(eigth));
			acc.setLbrCode(new Long(lbrCode));
			D009042 odlimit = DataUtils.getOverdraftLimit(eigth, lbrCode, one).get(0);
			
			LoanServiceImpl loanDeatils = new LoanServiceImpl();
			Double intRate = loanDeatils.getInterestRate(odlimit, eigth, lbrCode, one);
			
			acc.setIntRate(format.format(intRate));
			
			if (null == seven)
				seven = new BigDecimal(0);
			
			acc.setBalance(format.format(Double.valueOf(seven + "")));
			logger.error("ExpDate::>>" + DateUtil.getString(five));
			accountList.add(acc);
		}
		accountDetailsList.setResponse(MSGConstants.SUCCESS);
		accountDetailsList.setErrorCode("00");
		accountDetailsList.setErrorMessage(MSGConstants.SUCCESS_MSG);
		accountDetailsList.setAccountList(accountList);
		return accountDetailsList;
	}

	public static IMPSFetchDepositeAccountResponse depositAccountList(String customerNo, String depositeType) {
		IMPSFetchDepositeAccountResponse accountDetailsList = new IMPSFetchDepositeAccountResponse();
		List<D009022> accList = DataUtils.getDepositeAccountList(Integer.valueOf(customerNo), depositeType);
		if (accList == null) {
			logger.error("Account details not found.");
			accountDetailsList.setResponse(MSGConstants.ERROR);
			accountDetailsList.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
			return accountDetailsList;
		}
		List<DepositeAccountDetals> accountList = new ArrayList<DepositeAccountDetals>();
		for (D009022 account : accList) {
			DepositeAccountDetals accountDest = new DepositeAccountDetals();
			accountDest.setLbrCode(Long.valueOf(account.getId().getLbrCode()));
			accountDest.setAccountNo(account.getId().getPrdAcctId());

			D009021 productMaster = DataUtils.getProductMaster(account.getId().getLbrCode() + "", depositeType);
			if (productMaster.getModuleType() == 20) {
				List<D020004> receiptList = DataUtils.getDepositeReceiptDetails(account.getId().getLbrCode(),
						account.getId().getPrdAcctId());
				logger.error("receiptList::>>>" + receiptList);
				if (null != receiptList && !receiptList.isEmpty()) {
					accountList.add(accountDest);
				}
			} else if (productMaster.getModuleType() == 47) {
				List<D047003> ddsAcctList = DataUtils.getDDSDetails(account.getId().getLbrCode(),
						account.getId().getPrdAcctId());
				if (null != ddsAcctList && !ddsAcctList.isEmpty()) {
					accountList.add(accountDest);
				}
			}
		}
		accountDetailsList.setAccountList(accountList);
		accountDetailsList.setResponse(MSGConstants.SUCCESS);
		accountDetailsList.setCustomerNo(customerNo);
		accountDetailsList.setErrorCode("00");
		accountDetailsList.setErrorMessage(MSGConstants.SUCCESS_MSG);
		return accountDetailsList;
	}

	public static IMPSFetchDepositeReceiptResponse fetchDepositAccReceipt(int lbrCode, String accNo) {
		IMPSFetchDepositeReceiptResponse receiptDetailsList = new IMPSFetchDepositeReceiptResponse();
		List<D020004> receiptList = DataUtils.getDepositeReceiptDetails(lbrCode, accNo);
		logger.error("receiptList::>>>" + receiptList);
		if (null == receiptList) {
			logger.error("No Receipts found.");
			receiptDetailsList.setResponse(MSGConstants.ERROR);
			receiptDetailsList.setErrorMessage(MSGConstants.NO_RECIEPT_FOUND);
			return receiptDetailsList;
		}
		List<DepositeReceiptDetals> receiptLists = new ArrayList<DepositeReceiptDetals>();
		for (D020004 termDepositReceipts : receiptList) {
			Session session = HBUtil.getSessionFactory().openSession();
			D020002 tdParameter = session.get(D020002.class, new D020002Id(termDepositReceipts.getId().getLbrCode(),
					termDepositReceipts.getId().getPrdAcctId().substring(0, 8).trim()));

			DepositeReceiptDetals depositeRec = new DepositeReceiptDetals();
			depositeRec.setReceiptNo(SwiftCoreUtil.getAccountNoFormatted(termDepositReceipts.getId().getPrdAcctId()));
			if ('I' == tdParameter.getInstOrPrinc())
				depositeRec.setBalance(termDepositReceipts.getMainBalFcy());
			else
				depositeRec.setBalance(termDepositReceipts.getInstOrPrincAmt());
			depositeRec.setDateOfReceipt(termDepositReceipts.getCertDate());
			depositeRec.setInterestRate(termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate());
			depositeRec.setMaturityAmt(termDepositReceipts.getMatVal());
			depositeRec.setMaturityDate(termDepositReceipts.getMatDate());
			depositeRec.setLienAmount(termDepositReceipts.getTotalLien());
			depositeRec.setAsOfDate(termDepositReceipts.getAsOffdate());
			receiptLists.add(depositeRec);
			session.close();
		}
		receiptDetailsList.setResponse(MSGConstants.SUCCESS);
		receiptDetailsList.setErrorCode(ResponseCodes.SUCCESS);
		receiptDetailsList.setErrorMessage(MSGConstants.SUCCESS_MSG);
		receiptDetailsList.setReceiptList(receiptLists);
		return receiptDetailsList;
	}

	public static IMPSFetchDepositeInterestRateResponse fetchInterestRateDetails(String customerNo,
			String depositeType) {
		IMPSFetchDepositeInterestRateResponse receiptDetailsList = new IMPSFetchDepositeInterestRateResponse();
		List<Object[]> tdProductList = DataUtils.getTDProductInterestList(depositeType, "INR", null);
		if (tdProductList == null) {
			logger.error("Interest Rate not found.");
			receiptDetailsList.setResponse(MSGConstants.ERROR);
			receiptDetailsList.setErrorMessage(MSGConstants.INT_RATE_NOT_FOUND);
			return receiptDetailsList;
		}
		List<DepositeInterestRateDetals> interestList = new ArrayList<DepositeInterestRateDetals>();
		Object[] object = new Object[1];

		for (Iterator<Object[]> iterator = tdProductList.iterator(); iterator.hasNext();) {
			object = iterator.next();
			String one = (String) object[0];
			System.out.println("One::>>" + one);
			Date date = (Date) object[1];
			System.out.println("Date::>>" + date);
			long four = Long.valueOf(object[3] + "");
			System.out.println("four::>>" + four);
			long five = Long.valueOf(object[4] + "");
			System.out.println("five::>>" + five);
			String six = "";
			try {
				six = String.valueOf(object[5]);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			System.out.println("six::>>" + six);
			String seven = (String) object[6];
			System.out.println("seven::>>" + seven);
			DepositeInterestRateDetals inter = new DepositeInterestRateDetals();
			inter.setDepositeType(one);
			inter.setInterestDate(DateUtil.getFormattedDateNew(date));
			inter.setMonth(four);
			inter.setDays(five);
			inter.setInterestRate(Double.valueOf(six));
			inter.setDesc(seven);
			interestList.add(inter);
		}

		receiptDetailsList.setResponse(MSGConstants.SUCCESS);
		receiptDetailsList.setCustomerNo(customerNo);
		receiptDetailsList.setErrorCode(ResponseCodes.SUCCESS);
		receiptDetailsList.setErrorMessage(MSGConstants.SUCCESS_MSG);
		receiptDetailsList.setInterestList(interestList);
		return receiptDetailsList;
	}

	public static CustomerInfo getMinMaxValue(String depositType, String accNo15digit) {
		CustomerInfo customerInfo = new CustomerInfo();
		String accountNo = "";
		int lbrCode = 0;
		D009022 sourceAccount = DataUtils.getAccount(accNo15digit.trim());
		if (null == sourceAccount) {
			customerInfo.setResponse(MSGConstants.ERROR);
			customerInfo.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
			return customerInfo;
		}
		TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount, String.valueOf("10"),
				"C");

		if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
			customerInfo.setResponse(MSGConstants.ERROR);
			customerInfo.setErrorMessage(res.getErrorMsg());
			return customerInfo;
		}

		accountNo = sourceAccount.getId().getPrdAcctId();
		lbrCode = sourceAccount.getId().getLbrCode();
		D009022 account = sourceAccount;
		if (account.getAcctStat() == Byte.valueOf("3")) {
			logger.error("Account is closed.");
			customerInfo.setResponse(MSGConstants.ERROR);
			customerInfo.setErrorMessage(MSGConstants.ACCOUNT_CLOSED);
			return customerInfo;
		}

		Long lbrCodeMinTDParameters = Long
				.valueOf(ConfigurationLoader.getParameters(false).getProperty("lbrCodeMinTDParameters"));
		D020002 tdParameter = DataUtils.getTdParameter(lbrCodeMinTDParameters, depositType);
		if (null == tdParameter) {
			logger.error("Term Deposite product not found for branch code " + lbrCodeMinTDParameters
					+ " and product code" + accountNo.substring(0, 8).trim());
			customerInfo.setResponse(MSGConstants.ERROR);
			customerInfo.setErrorMessage("Term Deposite product not found for branch code " + lbrCodeMinTDParameters
					+ " and product code" + accountNo.substring(0, 8).trim());
			return customerInfo;
		}
		NumberFormat numberAmount1 = new DecimalFormat("0.00");
		BigDecimal bd = new BigDecimal(numberAmount1.format(tdParameter.getMinDepAmt()));
		BigDecimal bigDecimal = new BigDecimal(numberAmount1.format(tdParameter.getMaxDepAmt()));
		customerInfo.setResponse(MSGConstants.SUCCESS);
		customerInfo.setErrorMessage(MSGConstants.SUCCESS_MSG);
		customerInfo.setMinDepAmt(bd.toString());
		customerInfo.setMaxDepAmt(bigDecimal.toString());
		String minPeriod = MSGConstants.NOT_USED_PROCESS;
		String maxPeriod = MSGConstants.NOT_USED_PROCESS;
		String str = MSGConstants.NOT_USED_PROCESS;
		if (tdParameter.getPeriodType() == 'D') {
			minPeriod = tdParameter.getMinPeriod() + "";// +" Day/s";
			maxPeriod = tdParameter.getMaxPeriod() + "";// +" Day/s";
			str = "Days";
		} else if (tdParameter.getPeriodType() == 'M') {
			minPeriod = tdParameter.getMinPeriod() + "";// +" Month/s";
			maxPeriod = tdParameter.getMaxPeriod() + "";// +" Month/s";
			str = "Months";
		} else if (tdParameter.getPeriodType() == 'B') {
			minPeriod = tdParameter.getMinPeriod() + "";// +" Day/s";
			maxPeriod = tdParameter.getMaxPeriod() + "";// +" Month/s";
			str = "Both";
		}
		customerInfo.setMinPeriod(minPeriod);
		customerInfo.setMaxPeriod(maxPeriod);
		customerInfo.setPeriodType(str);
		return customerInfo;
	}

	public static BranchDetailsResponse getIFSCDetails(String bankName, String city, String area) {
		BranchDetailsResponse ifscDetailsRes = new BranchDetailsResponse();
		try {
			List<D946022> ifscMasterList = DataUtils.getIFSCCodeDetailList(bankName.trim(), city.trim(), area.trim());
			if (null == ifscMasterList || ifscMasterList.isEmpty()) {
				logger.error("No record found.");
				ifscDetailsRes.setResponse(MSGConstants.ERROR);
				ifscDetailsRes.setErrorMessage(MSGConstants.RECORD_NOT_FOUND);
				return ifscDetailsRes;
			}
			ifscDetailsRes.setResponse(MSGConstants.SUCCESS);
			ifscDetailsRes.setErrorMessage(MSGConstants.SUCCESS_MSG);
			List<IFSCDetailsResponse> ifscDetailsResponseList = new ArrayList<IFSCDetailsResponse>();
			for (D946022 codeMaster : ifscMasterList) {
				IFSCDetailsResponse detailsResponse = new IFSCDetailsResponse();
				detailsResponse.setIfsccd(codeMaster.getIfsccd());
				detailsResponse.setBankname(codeMaster.getBankName());
				detailsResponse.setBranchname(codeMaster.getBranchName());
				detailsResponse.setBankrbicd(Long.valueOf(codeMaster.getBankRbicd() + ""));
				detailsResponse.setBranchrbicd(Long.valueOf(codeMaster.getBranchRbicd() + ""));
				detailsResponse.setTr(codeMaster.getTr());
				detailsResponse.setUploaddate(codeMaster.getUploadDate());
				detailsResponse.setRtgsneftcd(Long.valueOf(codeMaster.getRtgsNeftCd()));
				detailsResponse.setAddr1(codeMaster.getAddr1());
				detailsResponse.setAddr2(codeMaster.getAddr2());
				detailsResponse.setAddr3(codeMaster.getAddr3());
				detailsResponse.setCity(codeMaster.getCity());
				detailsResponse.setState(codeMaster.getState());
				// detailsResponse.setArea(codeMaster.getArea());
				ifscDetailsResponseList.add(detailsResponse);
			}
			ifscDetailsRes.setIfscDetailsResponseList(ifscDetailsResponseList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ifscDetailsRes;
	}

	public static OtherChannelServiceResponse impsOTPGeneration(String custNo, String mmid, String transAmount,
			String accNo15digit, String channel) {
		OtherChannelServiceResponse otpGenerateRes = new OtherChannelServiceResponse();
		if (channel.trim().equalsIgnoreCase(MSGConstants.IMPS_CHANNEL)) {
			try {
				D350032 custxTreDtls = DataUtils.fetchMMID(custNo, mmid);
				if (null == custxTreDtls) {
					otpGenerateRes.setResponse(MSGConstants.ERROR);
					otpGenerateRes.setErrorMessage(MSGConstants.INVALID_CUST_NO_MMID);
					return otpGenerateRes;
				}

				D009022 account = DataUtils.getAccountDetails(custxTreDtls.getBrCode(), custxTreDtls.getAcctNo());
				if (account == null) {
					otpGenerateRes.setResponse(MSGConstants.ERROR);
					otpGenerateRes.setErrorMessage(MSGConstants.INVALID_ACCOUNT_NO);
					return otpGenerateRes;
				}
				TransactionValidationResponse res = TransactionServiceImpl.validateAccount(account,
						String.valueOf(transAmount), "D");

				if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
					otpGenerateRes.setResponse(MSGConstants.ERROR);
					otpGenerateRes.setErrorMessage(res.getErrorMsg());
					return otpGenerateRes;
				}

				String productCode = DataUtils.getProductCode(account.getId().getPrdAcctId());
				D009021 productMaster = DataUtils.getProductMaster(String.valueOf(account.getId().getLbrCode()),
						productCode);
				if (null == productMaster) {
					logger.error("Product not found.");
					otpGenerateRes.setResponse(MSGConstants.ERROR);
					otpGenerateRes.setErrorMessage(MSGConstants.PRODUCT_NOT_FOUND);
					return otpGenerateRes;
				}

				if ((productMaster.getModuleType() == MSGConstants.OD
						|| productMaster.getModuleType() == MSGConstants.LOAN)
						&& account.getAcctStat() == Byte.valueOf("9")) {
					logger.error("Account status is called back.");
					otpGenerateRes.setResponse(MSGConstants.ERROR);
					otpGenerateRes.setErrorMessage(MSGConstants.ACCOUNT_STAT_CALLED_BACK);
					return otpGenerateRes;
				}

				if (productMaster.getModuleType() == MSGConstants.LOAN) {
					logger.error("Loan Account Transaction not allowed.");
					otpGenerateRes.setResponse(MSGConstants.ERROR);
					otpGenerateRes.setErrorMessage(MSGConstants.LOAN_TRN_NOT_ALLOWED);
				}

				List<D350007> mobileOTPDomainList = DataUtils.fetchMobileOTPDomainList(account.getId().getLbrCode(),
						account.getId().getPrdAcctId(), DateUtil.convertDateFormat(new Date()));
				Long noOfTrns = 0L;
				Double totTrnsAmt = 0D;
				logger.error("mobileOTPDomainList::>>>>" + mobileOTPDomainList);
				if (null != mobileOTPDomainList && !mobileOTPDomainList.isEmpty()) {
					System.out.println("mobileOTPDomainList::>>" + mobileOTPDomainList);
					logger.error("mobileOTPDomainList::>>>" + mobileOTPDomainList);
					for (D350007 mobileOTPDomain : mobileOTPDomainList) {
						if (mobileOTPDomain.getStatus() != 'P') {
							continue;
						}

						noOfTrns++;
						totTrnsAmt += mobileOTPDomain.getTransAmt();
					}
				}

				totTrnsAmt += Double.valueOf(transAmount);
				logger.error("totTrnsAmt::>>>" + totTrnsAmt);
				noOfTrns++;
				logger.error("noOfTrns::>>>" + noOfTrns);
				D350001 otpObj = DataUtils.searchOTP(account.getId().getLbrCode(), account.getId().getPrdAcctId());
				if (null == otpObj) {
					logger.error("Transaction Limits Not Set Properly, Please Contact Home Branch.");
					otpGenerateRes.setResponse(MSGConstants.ERROR);
					otpGenerateRes.setErrorMessage(MSGConstants.TRANSACTION_LIMIT_NOT_SET);
					return otpGenerateRes;
				}
				logger.error("noOfTrns.intValue()::>>" + noOfTrns.intValue());
				System.out.println("noOfTrns.intValue()::>>" + noOfTrns.intValue());
				logger.error("otpObj.getNoTrans()::>>" + otpObj.getNoTrans());
				System.out.println("otpObj.getNoTrans()::>>" + otpObj.getNoTrans());
				if (otpObj.getNoTrans() < noOfTrns.intValue()) {
					logger.error("Exceeds Daily Limit.");
					otpGenerateRes.setResponse(MSGConstants.ERROR);
					otpGenerateRes.setErrorMessage(MSGConstants.EXCEED_DAILY_LIMIT);
					return otpGenerateRes;
				}
				if (otpObj.getDailyTrnslmt() < totTrnsAmt) {
					logger.error("Exceeds Daily Transaction Limit.");
					otpGenerateRes.setResponse(MSGConstants.ERROR);
					otpGenerateRes.setErrorMessage(MSGConstants.EXCEED_DAILY_TRN_LIMIT);
					return otpGenerateRes;

				}
				String encryptedOtp = MSGConstants.NOT_USED_PROCESS;
				int otp = 0;
				if (Double.valueOf(otpObj.getTrnsactionlmt()) < Double.valueOf(transAmount).doubleValue()) {
					otp = WSUtils.generateRandomNumber(111111, 999999);
					encryptedOtp = OmniEncryptPassword.getEncryptedPwd(custNo.toUpperCase(), Integer.toString(otp));
				}
				D350007 mobileOTPDomain = DataUtils.prepareMobileOTPDomainObject(account.getId().getLbrCode(),
						account.getId().getPrdAcctId(), DateUtil.convertDateFormat(new Date()),
						DateUtil.getFormattedTime(new Date()), encryptedOtp,
						DateUtil.getDateFromStringYYYYMMDD("19000101"), account.getCustNo() + "", mmid,
						MSGConstants.OTP_FLAG_F, transAmount);

				if (null == mobileOTPDomain) {
					otpGenerateRes.setResponse(MSGConstants.ERROR);
					otpGenerateRes.setErrorMessage(MSGConstants.FAIL_TO_PREPARE_OBJECT);
					;
					return otpGenerateRes;
				}
				Session session = HBUtil.getSessionFactory().openSession();
				Transaction t = session.beginTransaction();
				try {
					session.save(mobileOTPDomain);
					t.commit();
					session.close();
					session = null;
					t = null;

					logger.error("otpObj.getTrnsactionlmt():>>>" + otpObj.getTrnsactionlmt());
					logger.error("transAmount:>>>" + transAmount);
					if (otpObj.getTrnsactionlmt() > Double.valueOf(transAmount.trim()).intValue()) {
						logger.error(MSGConstants.SUCCESS_MSG);
						otpGenerateRes.setResponse(MSGConstants.SUCCESS);
						otpGenerateRes.setErrorMessage(MSGConstants.SUCCESS_MSG);
						otpGenerateRes.setErrorCode(ResponseCodes.SUCCESS);
						otpGenerateRes.setMessage("Successful Transaction.");
						return otpGenerateRes;
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					session.close();
					session = null;
					t = null;
					otpGenerateRes.setResponse(MSGConstants.ERROR);
					otpGenerateRes.setErrorMessage(MSGConstants.FAIL_TO_STORE_OBJ);
					return otpGenerateRes;
				}

				/** Insert into DeliverySMS for Sending SMS */
				/*
				 * String smsMsg = "Dear Customer, Your Dynamic PIN for Transaction is "
				 * +otp+". The Dynamic PIN is valid for 5 mins."; DeliverySMS sms =
				 * wsValidation.prepareDeliverySMSObject(account.getId(). getLbrcode(),
				 * custxTreDtls.getId().getMobileno(), DateUtility.convertDateFormat(new
				 * Date()), DateUtility.getFormattedTime(new Date()),
				 * "OtpGen_"+DateUtility.getcurrentDateForPDF(), custNo, smsMsg, 1L);
				 * mobileRegService.saveDeliverySMS(sms);
				 */
				logger.error("OTP not required.");
				otpGenerateRes.setResponse(MSGConstants.SUCCESS);
				otpGenerateRes.setErrorMessage(MSGConstants.SUCCESS_MSG);
				otpGenerateRes.setErrorCode(ResponseCodes.SUCCESS);
				otpGenerateRes.setOutput(new String[] { Integer.toString(otp) });
				otpGenerateRes.setMessage("Successful Browse.");

			} catch (Exception e) {
				e.printStackTrace();
				logger.error("WSRequestServiceImpl.impsOTPGeneration().Exception", e);
				otpGenerateRes.setResponse(MSGConstants.ERROR);
				otpGenerateRes.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
				return otpGenerateRes;
			}
		} else if (channel.trim().equalsIgnoreCase(MSGConstants.OTHER_CHANNEL)) {
			try {
				D009022 account = DataUtils.getAccount(accNo15digit);
				if (account == null) {
					otpGenerateRes.setResponse(MSGConstants.ERROR);
					otpGenerateRes.setErrorMessage(MSGConstants.INVALID_ACCOUNT_NO);
					return otpGenerateRes;
				}
				TransactionValidationResponse res = TransactionServiceImpl.validateAccount(account,
						String.valueOf(transAmount), "D");
				if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
					otpGenerateRes.setResponse(MSGConstants.ERROR);
					otpGenerateRes.setErrorMessage(res.getErrorMsg());
					return otpGenerateRes;
				}
				String productCode = DataUtils.getProductCode(account.getId().getPrdAcctId());
				D009021 productMaster = DataUtils.getProductMaster(String.valueOf(account.getId().getLbrCode()),
						productCode);
				if (null == productMaster) {
					logger.error("Product not found.");
					otpGenerateRes.setResponse(MSGConstants.ERROR);
					otpGenerateRes.setErrorMessage(MSGConstants.PRODUCT_NOT_FOUND);
					return otpGenerateRes;
				}

				if ((productMaster.getModuleType() == MSGConstants.OD
						|| productMaster.getModuleType() == MSGConstants.LOAN)
						&& account.getAcctStat() == Byte.valueOf("9")) {
					logger.error("Account status is called back.");
					otpGenerateRes.setResponse(MSGConstants.ERROR);
					otpGenerateRes.setErrorMessage(MSGConstants.ACCOUNT_STAT_CALLED_BACK);
					return otpGenerateRes;
				}

				if (productMaster.getModuleType() == MSGConstants.LOAN) {
					logger.error("Loan Account Transaction not allowed.");
					otpGenerateRes.setResponse(MSGConstants.ERROR);
					otpGenerateRes.setErrorMessage(MSGConstants.LOAN_TRN_NOT_ALLOWED);
				}

				List<D350007> mobileOTPDomainList = DataUtils.fetchMobileOTPDomainList(account.getId().getLbrCode(),
						account.getId().getPrdAcctId(), DateUtil.convertDateFormat(new Date()));
				Long noOfTrns = 0L;
				Double totTrnsAmt = 0D;
				if (null != mobileOTPDomainList && !mobileOTPDomainList.isEmpty()) {
					for (D350007 mobileOTPDomain : mobileOTPDomainList) {
						if (mobileOTPDomain.getStatus() != 'P') {
							continue;
						}

						noOfTrns++;
						totTrnsAmt += mobileOTPDomain.getTransAmt();
					}
				}
				totTrnsAmt += Double.valueOf(transAmount);
				noOfTrns++;
				logger.error("totTrnsAmt::>>>" + totTrnsAmt);
				logger.error("noOfTrns::>>>" + noOfTrns);
				D350001 otpObj = DataUtils.searchOTP(account.getId().getLbrCode(), account.getId().getPrdAcctId());
				logger.error("otpObj::>>>>" + otpObj);
				if (null == otpObj) {
					logger.error("Transaction Limits Not Set Properly, Please Contact Home Branch.");
					otpGenerateRes.setResponse(MSGConstants.ERROR);
					otpGenerateRes.setErrorMessage(MSGConstants.TRANSACTION_LIMIT_NOT_SET);
					return otpGenerateRes;
				}

				if (otpObj.getNoTrans() < noOfTrns.intValue()) {
					logger.error("Exceeds Daily Limit.");
					otpGenerateRes.setResponse(MSGConstants.ERROR);
					otpGenerateRes.setErrorMessage(MSGConstants.EXCEED_DAILY_LIMIT);
					return otpGenerateRes;
				}

				if (otpObj.getDailyTrnslmt() < totTrnsAmt) {
					logger.error("Exceeds Daily Transaction Limit.");
					otpGenerateRes.setResponse(MSGConstants.ERROR);
					otpGenerateRes.setErrorMessage(MSGConstants.EXCEED_DAILY_TRN_LIMIT);
					return otpGenerateRes;
				}
				String encryptedOtp = MSGConstants.NOT_USED_PROCESS;
				int otp = 0;
				if (Double.valueOf(otpObj.getTrnsactionlmt()) < Double.valueOf(transAmount).doubleValue()) {
					otp = WSUtils.generateRandomNumber(111111, 999999);
					encryptedOtp = OmniEncryptPassword.getEncryptedPwd(custNo.toUpperCase(), Integer.toString(otp));
				}
				D350007 mobileOTPDomain = DataUtils.prepareMobileOTPDomainObject(account.getId().getLbrCode(),
						account.getId().getPrdAcctId(), DateUtil.convertDateFormat(new Date()),
						DateUtil.getFormattedTime(new Date()), encryptedOtp,
						DateUtil.getDateFromStringYYYYMMDD("19000101"), "" + account.getCustNo(), mmid,
						MSGConstants.OTP_FLAG_F, transAmount);

				if (null == mobileOTPDomain) {
					otpGenerateRes.setResponse(MSGConstants.ERROR);
					otpGenerateRes.setErrorMessage(MSGConstants.FAIL_TO_PREPARE_OBJECT);
					;
					return otpGenerateRes;
				}
				Session session = HBUtil.getSessionFactory().openSession();
				Transaction t = session.beginTransaction();
				try {
					session.save(mobileOTPDomain);
					t.commit();
					session.close();
					session = null;
					t = null;
					if (otpObj.getTrnsactionlmt() > Double.valueOf(transAmount)) {
						logger.error(MSGConstants.SUCCESS_MSG);
						otpGenerateRes.setResponse(MSGConstants.SUCCESS);
						otpGenerateRes.setErrorMessage(MSGConstants.SUCCESS_MSG);
						otpGenerateRes.setErrorCode(ResponseCodes.SUCCESS);
						otpGenerateRes.setMessage("Successful Transaction.");
						return otpGenerateRes;
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					session.close();
					session = null;
					t = null;
					otpGenerateRes.setResponse(MSGConstants.ERROR);
					otpGenerateRes.setErrorMessage(MSGConstants.FAIL_TO_STORE_OBJ);
					return otpGenerateRes;
				}

				/** Insert into DeliverySMS for Sending SMS */
				/*
				 * String smsMsg = "Dear Customer, Your Dynamic PIN for Transaction is "
				 * +otp+". The Dynamic PIN is valid for 5 mins."; DeliverySMS sms =
				 * wsValidation.prepareDeliverySMSObject(account.getId(). getLbrcode(),
				 * custxTreDtls.getId().getMobileno(), DateUtility.convertDateFormat(new
				 * Date()), DateUtility.getFormattedTime(new Date()),
				 * "OtpGen_"+DateUtility.getcurrentDateForPDF(), custNo, smsMsg, 1L);
				 * mobileRegService.saveDeliverySMS(sms);
				 */
				logger.error("OTP not required.");
				otpGenerateRes.setResponse(MSGConstants.SUCCESS);
				otpGenerateRes.setErrorMessage(MSGConstants.SUCCESS_MSG);
				otpGenerateRes.setErrorCode(ResponseCodes.SUCCESS);
				otpGenerateRes.setOutput(new String[] { Integer.toString(otp) });
				otpGenerateRes.setMessage("Successful Browse.");

			} catch (Exception e) {
				e.printStackTrace();
				logger.error("WSRequestServiceImpl.impsOTPGeneration().Exception", e);
				otpGenerateRes.setResponse(MSGConstants.ERROR);
				otpGenerateRes.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
				return otpGenerateRes;
			}
		}
		return otpGenerateRes;
	}

	public static OtherChannelServiceResponse impsOTPValidation(String custNo, String mmid, String otp,
			String transAmount, char successMark, String accNo15digit, String channel) {
		OtherChannelServiceResponse otpValidationRes = new OtherChannelServiceResponse();

		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		if (channel.trim().equalsIgnoreCase(MSGConstants.IMPS_CHANNEL)) {
			D350032 custxTreDtls = DataUtils.fetchMMID(custNo, mmid);
			if (null == custxTreDtls) {
				session.close();
				session = null;
				logger.error("Invalid CustNo/MMID.");
				otpValidationRes.setResponse(MSGConstants.ERROR);
				otpValidationRes.setErrorMessage(MSGConstants.INVALID_CUST_NO_MMID);
				return otpValidationRes;
			}
			D009022 account = DataUtils.getAccountDetails(custxTreDtls.getBrCode(), custxTreDtls.getAcctNo());
			if (null == account) {
				session.close();
				session = null;
				logger.error("Account Not Found.");
				otpValidationRes.setResponse(MSGConstants.ERROR);
				otpValidationRes.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
				return otpValidationRes;
			}

			List<D350007> mobileOTPDomainList = DataUtils.fetchMobileOTPDomainList(account.getId().getLbrCode(),
					account.getId().getPrdAcctId(), DateUtil.convertDateFormat(new Date()));
			if (null == mobileOTPDomainList || mobileOTPDomainList.isEmpty()) {
				session.close();
				session = null;
				logger.error("OTP Not Found.");
				otpValidationRes.setResponse(MSGConstants.ERROR);
				otpValidationRes.setErrorMessage(MSGConstants.OTP_NOT_FOUND);
				return otpValidationRes;
			}

			D350007 mobileOTPDomain = mobileOTPDomainList.get(0);
			if (successMark != ' ') {
				/** Update status in MobileOTPDomain */
				mobileOTPDomain.setStatus(successMark);
				session.saveOrUpdate(mobileOTPDomain);
				t.commit();
				session.close();
				session = null;
				t = null;
				otpValidationRes.setResponse(MSGConstants.SUCCESS);
				otpValidationRes.setErrorMessage(MSGConstants.SUCCESS_MSG);
				otpValidationRes.setErrorCode(ResponseCodes.SUCCESS);
				return otpValidationRes;
			}

			D350001 otpObj = DataUtils.searchOTP(account.getId().getLbrCode(), account.getId().getPrdAcctId());
			if (null == otpObj) {
				session.close();
				session = null;
				logger.error("Parameters Not Set....");
				otpValidationRes.setResponse(MSGConstants.ERROR);
				otpValidationRes.setErrorMessage(MSGConstants.OTP_LIMIT_NOT_SET);
				return otpValidationRes;
			}
			if (mobileOTPDomain.getTransAmt().doubleValue() != Double.valueOf(transAmount).doubleValue()) {
				session.close();
				session = null;
				logger.error("Transaction Amount Not Matched...");
				otpValidationRes.setResponse(MSGConstants.ERROR);
				otpValidationRes.setErrorMessage(MSGConstants.TRN_AMOUNT_NOT_MATCHED);
				return otpValidationRes;
			}

			String encryptedOtp = OmniEncryptPassword.getEncryptedPwd(custNo.toUpperCase(), otp);
			if (!mobileOTPDomain.getOtp().equals(encryptedOtp)) {
				/** Update status as F(Failure) in MobileOTPDomain */
				mobileOTPDomain.setStatus(MSGConstants.OTP_FLAG_F);
				session.saveOrUpdate(mobileOTPDomain);
				t.commit();
				session.close();
				session = null;
				t = null;
				logger.error("OTP Not Matched...");
				otpValidationRes.setResponse(MSGConstants.ERROR);
				otpValidationRes.setErrorMessage(MSGConstants.OTP_NOT_MATCHED);
				return otpValidationRes;
			}

			if (null == mobileOTPDomain.getReceivingTime()) {
				session.close();
				session = null;
				logger.error("OTP Already Used.");
				otpValidationRes.setResponse(MSGConstants.ERROR);
				otpValidationRes.setErrorMessage(MSGConstants.OTP_ALREADY_USED);
				return otpValidationRes;
			}

			String[] sendingTime = DateUtil.getTimeForFileGeneration(mobileOTPDomain.getId().getSendingTime())
					.split(":");
			int sendingHr = Integer.parseInt(sendingTime[0]);
			int sendingMin = Integer.parseInt(sendingTime[1]);
			int sendingSec = Integer.parseInt(sendingTime[2]);
			if (sendingSec > 1) {
				sendingMin++;
			}

			String[] recvTime = DateUtil.getTimeForFileGeneration(new Date()).split(":");
			int recvHr = Integer.parseInt(recvTime[0]);
			int recvMin = Integer.parseInt(recvTime[1]);
			int recvSec = Integer.parseInt(recvTime[2]);
			if (recvSec > 1) {
				recvMin++;
			}
			int ct = (sendingHr * 60) + sendingMin;
			int dt = (recvHr * 60) + recvMin;
			if (Math.abs(dt - ct) > otpObj.getExpTimeOtp()) {
				logger.error("OTP Expired...");
				session.close();
				session = null;
				otpValidationRes.setResponse(MSGConstants.ERROR);
				otpValidationRes.setErrorMessage(MSGConstants.OTP_EXPIRED);
				return otpValidationRes;
			}
			mobileOTPDomain.setReceivingTime(DateUtil.getFormattedTime(new Date()));
			mobileOTPDomain.setStatus(MSGConstants.OTP_FLAG_S);
			session.saveOrUpdate(mobileOTPDomain);
			t.commit();
			session.close();
			session = null;
			t = null;
			otpValidationRes.setResponse(MSGConstants.ERROR);
			otpValidationRes.setErrorMessage(MSGConstants.OTP_VERIFICATION_SUCCESS);
			otpValidationRes.setMessage("OTP Verified Successfully.");
			return otpValidationRes;

		} else if (channel.trim().equalsIgnoreCase(MSGConstants.OTHER_CHANNEL)) {
			D009022 account = DataUtils.getAccount(accNo15digit.trim());
			if (null == account) {
				logger.error("Account Not Found.");
				session.close();
				session = null;
				t = null;
				otpValidationRes.setResponse(MSGConstants.ERROR);
				otpValidationRes.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
				return otpValidationRes;
			}
			List<D350007> mobileOTPDomainList = DataUtils.fetchMobileOTPDomainList(account.getId().getLbrCode(),
					account.getId().getPrdAcctId(), DateUtil.convertDateFormat(new Date()));
			if (null == mobileOTPDomainList || mobileOTPDomainList.isEmpty()) {
				logger.error("OTP Not Found.");
				session.close();
				session = null;
				t = null;
				otpValidationRes.setResponse(MSGConstants.ERROR);
				otpValidationRes.setErrorMessage(MSGConstants.OTP_NOT_FOUND);
				return otpValidationRes;
			}
			D350007 mobileOTPDomain = mobileOTPDomainList.get(0);
			if (successMark != ' ') {
				/** Update status in MobileOTPDomain */
				mobileOTPDomain.setStatus(successMark);
				session.saveOrUpdate(mobileOTPDomain);
				t.commit();
				session.close();
				session = null;
				t = null;
				otpValidationRes.setResponse(MSGConstants.SUCCESS);
				otpValidationRes.setErrorMessage(MSGConstants.SUCCESS_MSG);
				otpValidationRes.setErrorCode(ResponseCodes.SUCCESS);
				return otpValidationRes;
			}

			D350001 otpObj = DataUtils.searchOTP(account.getId().getLbrCode(), account.getId().getPrdAcctId());
			if (null == otpObj) {
				logger.error("Parameters Not Set....");
				session.close();
				session = null;
				t = null;
				otpValidationRes.setResponse(MSGConstants.ERROR);
				otpValidationRes.setErrorMessage(MSGConstants.OTP_LIMIT_NOT_SET);
				return otpValidationRes;
			}
			if (mobileOTPDomain.getTransAmt().doubleValue() != Double.valueOf(transAmount).doubleValue()) {
				logger.error("Transaction Amount Not Matched...");
				session.close();
				session = null;
				t = null;
				otpValidationRes.setResponse(MSGConstants.ERROR);
				otpValidationRes.setErrorMessage(MSGConstants.TRN_AMOUNT_NOT_MATCHED);
				return otpValidationRes;
			}

			String encryptedOtp = OmniEncryptPassword.getEncryptedPwd(custNo.toUpperCase(), otp);
			if (!mobileOTPDomain.getOtp().equals(encryptedOtp)) {
				/** Update status as F(Failure) in MobileOTPDomain */
				mobileOTPDomain.setStatus(MSGConstants.OTP_FLAG_F);
				session.saveOrUpdate(mobileOTPDomain);
				t.commit();
				session.close();
				session = null;
				t = null;
				logger.error("OTP Not Matched...");
				otpValidationRes.setResponse(MSGConstants.ERROR);
				otpValidationRes.setErrorMessage(MSGConstants.OTP_NOT_MATCHED);
				return otpValidationRes;
			}

			if (null == mobileOTPDomain.getReceivingTime()) {
				logger.error("OTP Already Used.");
				session.close();
				session = null;
				t = null;
				otpValidationRes.setResponse(MSGConstants.ERROR);
				otpValidationRes.setErrorMessage(MSGConstants.OTP_ALREADY_USED);
				return otpValidationRes;
			}

			String[] sendingTime = DateUtil.getTimeForFileGeneration(mobileOTPDomain.getId().getSendingTime())
					.split(":");
			System.out.println("sendingTime::>>" + sendingTime);
			int sendingHr = Integer.parseInt(sendingTime[0]);
			int sendingMin = Integer.parseInt(sendingTime[1]);
			int sendingSec = Integer.parseInt(sendingTime[2]);
			System.out.println("sendingHr::>>>" + sendingHr);
			System.out.println("sendingMin::>>>" + sendingMin);
			System.out.println("sendingSec::>>>" + sendingSec);
			if (sendingSec > 1) {
				sendingMin++;
			}

			String[] recvTime = DateUtil.getTimeForFileGeneration(new Date()).split(":");
			int recvHr = Integer.parseInt(recvTime[0]);
			int recvMin = Integer.parseInt(recvTime[1]);
			int recvSec = Integer.parseInt(recvTime[2]);

			System.out.println("recvHr::>>>" + recvHr);
			System.out.println("recvMin::>>>" + recvMin);
			System.out.println("recvSec::>>>" + recvSec);

			if (recvSec > 1) {
				recvMin++;
			}
			int ct = (sendingHr * 60) + sendingMin;
			int dt = (recvHr * 60) + recvMin;
			if (Math.abs(dt - ct) > otpObj.getExpTimeOtp()) {
				logger.error("OTP Expired...");
				otpValidationRes.setResponse(MSGConstants.ERROR);
				otpValidationRes.setErrorMessage(MSGConstants.OTP_EXPIRED);
				return otpValidationRes;
			}
			mobileOTPDomain.setReceivingTime(DateUtil.getFormattedTime(new Date()));
			mobileOTPDomain.setStatus(MSGConstants.OTP_FLAG_S);
			session.saveOrUpdate(mobileOTPDomain);
			t.commit();
			session.close();
			session = null;
			t = null;
			otpValidationRes.setResponse(MSGConstants.ERROR);
			otpValidationRes.setErrorMessage(MSGConstants.OTP_VERIFICATION_SUCCESS);
			otpValidationRes.setMessage("OTP Verified Successfully.");
			return otpValidationRes;
		}
		otpValidationRes.setResponse(MSGConstants.ERROR);
		otpValidationRes.setErrorMessage(MSGConstants.INVALID_CHANNEL);
		return otpValidationRes;
	}

	public static OtherChannelServiceResponse pinOffsetVerification(String custNo, String offsetFlag) {
		OtherChannelServiceResponse pinOffsetverify = new OtherChannelServiceResponse();
		try {
			D350034 custOtherInfo = DataUtils.getMobCustStatus(custNo);
			if (null == custOtherInfo) {
				logger.error("Customer Number not valid.");
				pinOffsetverify.setResponse(MSGConstants.ERROR);
				pinOffsetverify.setErrorMessage(MSGConstants.INVALID_CUSTOMER);
				return pinOffsetverify;
			}
			pinOffsetverify.setResponse(MSGConstants.SUCCESS);
			pinOffsetverify.setErrorMessage(MSGConstants.SUCCESS_MSG);
			if (offsetFlag.equalsIgnoreCase("F")) {
				D001004 parameter = DataUtils.getSystemParameter(0, MSGConstants.BADLILIMDAY);
				System.out.println("parameter.getValue()::>>>" + parameter.getValue());
				int maxBadLoginPerDay = parameter == null ? 0 : Integer.valueOf(parameter.getValue().trim());
				DataUtils.impsKeepTrackOfBadLogins(custOtherInfo, maxBadLoginPerDay);
				if (custOtherInfo.getStatus() == 2) {
					pinOffsetverify.setMessage("Mobile banking has locked.");
					pinOffsetverify.setErrorMessage("Mobile banking has locked.");
				}
			} else if (offsetFlag.equalsIgnoreCase("S")) {
				/** Update noofbadlogins as 0 and status as 1 */
				DataUtils.updateBadLoginsWS(0, custNo, 1);
			} else if (offsetFlag.equalsIgnoreCase("N")) {
				/** Update noofbadlogins as 0 and status as 7 */
				DataUtils.updateBadLoginsWS(0, custNo, 7);
			}

		} catch (Exception e) {
			logger.error("WSRequestServiceImpl.pinOffsetVerification().Exception", e);
			e.printStackTrace();
			pinOffsetverify.setResponse(MSGConstants.ERROR);
			pinOffsetverify.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			return pinOffsetverify;
		}
		return pinOffsetverify;
	}

	public static PigmeAccountsResponse fetchPigMeAcc(String accNo) {
		PigmeAccountsResponse response = new PigmeAccountsResponse();
		ArrayList<PigmeAccountsResponse> accList = new ArrayList<>();
		D009022 sourceAccount = DataUtils.getAccount(accNo.trim());
		if (sourceAccount == null) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
			return response;
		}
		TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount, String.valueOf("10"),
				"D");
		System.out.println("res::>>>" + res);
		if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(res.getErrorMsg());
			return response;
		}
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D047003.class);
		criteria.add(Restrictions.eq("id.lbrCode", sourceAccount.getId().getLbrCode()));
		criteria.add(Restrictions.eq("agtPrdAcctId", sourceAccount.getId().getPrdAcctId()));
		List<D047003> list = criteria.list();
		logger.error("list::>>>" + list);
		if (list == null || list.isEmpty()) {
			session.close();
			session = null;
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
			return response;
		}
		for (D047003 d047003 : list) {
			int lBrCode = d047003.getId().getLbrCode();
			String accountNo = d047003.getId().getCustPrdAcctId();
			Criteria criteria2 = session.createCriteria(D009022.class);
			criteria2.add(Restrictions.eq("id.lbrCode", d047003.getId().getLbrCode()));
			criteria2.add(Restrictions.eq("id.prdAcctId", d047003.getId().getCustPrdAcctId()));
			criteria2.add(Restrictions.in("acctStat", Arrays.asList(Byte.valueOf("1"), Byte.valueOf("2"))));
			List<D009022> listt = criteria2.list();
			logger.error("listtt::>>>" + listt);
			if (listt != null || list.size() > 0) {
				PigmeAccountsResponse response2 = new PigmeAccountsResponse();
				for (D009022 d009022 : listt) {
					response2.setAccNo(d047003.getId().getCustPrdAcctId());
					response2.setBrCode(d047003.getId().getLbrCode() + "");
					response2.setCustNo(d009022.getCustNo() + "");
					response2.setName(d009022.getLongName().trim());
					response2.setFormattedAcc(d047003.getId().getLbrCode() + "/"
							+ d047003.getId().getCustPrdAcctId().substring(0, 8).trim() + "/"
							+ Long.valueOf(d047003.getId().getCustPrdAcctId().substring(16, 24)));
					accList.add(response2);
				}
			}
		}
		session.close();
		session = null;
		response.setAccList(accList);
		return response;
	}

	public static PigmeAccountsResponse searchPigMeAcc(String accNo, String name, String depositAccNo) {
		PigmeAccountsResponse response = new PigmeAccountsResponse();
		ArrayList<PigmeAccountsResponse> accList = new ArrayList<>();
		Session session = HBUtil.getSessionFactory().openSession();
		try {
			logger.error("RequestServiceImpl.searchPigMeAcc() " + accNo + " ~ " + name + " ~ " + depositAccNo);

			D009022 sourceAccount = DataUtils.getAccount(accNo.trim());
			if (sourceAccount == null) {
				logger.error("Amar1");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
				logger.error("Pigme Search Accounts::>>" + response.toString());
				return response;
			}
			TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount,
					String.valueOf("10"), "AGENT");
			logger.error("res::>>>" + res);
			if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(res.getErrorMsg());
				logger.error("Pigme Search Accounts::>>" + response.toString());
				return response;
			}

			Criteria criteria = session.createCriteria(D047003.class);
			criteria.add(Restrictions.eq("id.lbrCode", sourceAccount.getId().getLbrCode()));
			criteria.add(Restrictions.eq("agtPrdAcctId", sourceAccount.getId().getPrdAcctId()));
			// criteria.add(Restrictions.ne("id.acctStat",Byte.valueOf("3")));
			criteria.add(Restrictions.ne("acctStat", Byte.valueOf("3")));
			/*
			 * if(ConfigurationLoader.getParameters(false).getProperty(
			 * "AG_Maturity_Date_Check_YN") .equalsIgnoreCase(MSGConstants.YES))
			 * criteria.add(Restrictions.gt("matDate", new Date()));
			 */
			
			List<D047003> list = criteria.list();
			logger.error("list::>>>" + list);
			if (list == null || list.isEmpty()) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
				logger.error("Pigme Search Accounts::>>" + response.toString());
				return response;
			}
			ArrayList<String> acList = new ArrayList<String>();
			ArrayList<String> acList2 = new ArrayList<String>();
			for (D047003 d047003 : list) {
				acList.add(d047003.getId().getCustPrdAcctId());
				acList2.add(d047003.getId().getCustPrdAcctId().substring(8));
			}
			String accNo32 = "";
			String product = "";
			if (depositAccNo != null && depositAccNo.trim().length() > 0) {
				if (ConfigurationLoader.getParameters(false).getProperty("D047001_MULTI_PRODUCT_YN")
						.equalsIgnoreCase(MSGConstants.YES))
					product = "00000000" + String.format("%08d", Long.valueOf(depositAccNo.trim())) + "00000000";
				else
					accNo32 = list.get(0).getId().getCustPrdAcctId().substring(0, 8) + "00000000"
							+ String.format("%08d", Long.valueOf(depositAccNo.trim())) + "00000000";
				// }
				List<D047003> list5 = null, list6 = null;
				if (ConfigurationLoader.getParameters(false).getProperty("D047001_MULTI_PRODUCT_YN")
						.equalsIgnoreCase(MSGConstants.YES)) {
					Criteria criteria5 = session.createCriteria(D047003.class);
					criteria5.add(Restrictions.eq("id.lbrCode", sourceAccount.getId().getLbrCode()));
					criteria5.add(Restrictions.eq("agtPrdAcctId", sourceAccount.getId().getPrdAcctId()));
					criteria5.add(Restrictions.ilike("id.custPrdAcctId", "%" + product));
					// criteria5.add(Restrictions.ne("acctStat", Byte.valueOf("3")));
					// criteria5.add(Restrictions.gt("matDate", new Date()));
					list5 = criteria5.list();
				}
				if (ConfigurationLoader.getParameters(false).getProperty("D047001_MULTI_PRODUCT_YN")
						.equalsIgnoreCase(MSGConstants.NO)) {
					if (depositAccNo != null && depositAccNo.trim().length() > 0) {
						if (!acList.contains(accNo32)) {
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMsg(MSGConstants.INVALID_ACCOUNT_NO);
							logger.error("Pigme Search Accounts::>>" + response.toString());
							return response;
						}
						
						Criteria criteria6 = session.createCriteria(D047003.class);
						criteria6.add(Restrictions.eq("id.lbrCode", sourceAccount.getId().getLbrCode()));
						criteria6.add(Restrictions.eq("agtPrdAcctId", sourceAccount.getId().getPrdAcctId()));
						criteria6.add(Restrictions.eq("id.custPrdAcctId", accNo32));
						// criteria5.add(Restrictions.ne("acctStat", Byte.valueOf("3")));
						// criteria5.add(Restrictions.gt("matDate", new Date()));
						list6 = criteria6.list();

					}
				}
				if (ConfigurationLoader.getParameters(false).getProperty("D047001_MULTI_PRODUCT_YN")
						.equalsIgnoreCase(MSGConstants.YES)) {
					if (list5 == null || list5.isEmpty()) {
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMsg(MSGConstants.PRODUCT_NOT_FOUND);
						logger.error("Pigme Search Accounts::>>" + response.toString());
						return response;
					}
					/*
					 if (list5.get(0).getMatDate().before(new Date())) {
					 		response.setResponse(MSGConstants.ERROR);
					 		response.setErrorMsg("Maturity Date of the Account has Expired"); return
					 		response; 
					 	}
					 */
					if (list5.get(0).getAcctStat() == 3) {
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMsg("Account is Closed");
						logger.error("Pigme Search Accounts::>>" + response.toString());
						return response;
					}
					product = list5.get(0).getId().getCustPrdAcctId();
				}
				
				
			}
			Criteria criteria2 = session.createCriteria(D009022.class);
			criteria2.add(Restrictions.eq("id.lbrCode", sourceAccount.getId().getLbrCode()));
			criteria2.add(Restrictions.in("id.prdAcctId", acList));
			criteria2.add(Restrictions.ilike("longName", "%" + name.trim().toUpperCase() + "%"));
			criteria2.add(Restrictions.in("acctStat",
					Arrays.asList(Byte.valueOf("1"), Byte.valueOf("2"), Byte.valueOf("12"))));
			logger.error("accNo32::>>" + accNo32);
			logger.error("accNo32::>>" + accNo32);
			Criteria criteria3 = session.createCriteria(D009022.class);
			criteria3.add(Restrictions.eq("id.lbrCode", sourceAccount.getId().getLbrCode()));
			if (ConfigurationLoader.getParameters(false).getProperty("D047001_MULTI_PRODUCT_YN")
					.equalsIgnoreCase(MSGConstants.YES))
				criteria3.add(Restrictions.ilike("id.prdAcctId", "%" + product));
			else
				criteria3.add(Restrictions.eq("id.prdAcctId", accNo32));

			criteria3.add(Restrictions.in("acctStat",
					Arrays.asList(Byte.valueOf("1"), Byte.valueOf("2"), Byte.valueOf("12"))));

			List<D009022> listt = null;
			if (depositAccNo.trim().length() < 1)
				listt = criteria2.list();
			else
				listt = criteria3.list();
			if (listt != null || listt.size() > 0) {
				SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
				for (D009022 d009022 : listt) {
					PigmeAccountsResponse response2 = new PigmeAccountsResponse();
					response2.setAccNo(d009022.getId().getPrdAcctId());
					response2.setBrCode(d009022.getId().getLbrCode() + "");
					response2.setCustNo(d009022.getCustNo() + "");
					response2.setName(d009022.getLongName().trim());
					response2.setFormattedAcc(
							d009022.getId().getLbrCode() + "/" + d009022.getId().getPrdAcctId().substring(0, 8).trim()
									+ "/" + Long.valueOf(d009022.getId().getPrdAcctId().substring(16, 24)));
					
					response2.setAccNo15(DataUtils.get15DigitAccountNumber(d009022.getId().getLbrCode(), d009022.getId().getPrdAcctId()));
					response2.setBalance("" + Double.valueOf(Double.valueOf(d009022.getActClrBalFcy())));
					// added to give date of opening
					response2.setAccOpnDt(d009022.getDateOpen());
					response2.setAccOpnDtStr(simpleDateFormat.format(d009022.getDateOpen()));
					accList.add(response2);
					logger.error("PigmeAccounts::>>" + response2.toString());
					//response2 = null;
				}
			} else {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
				logger.error("Pigme Search Accounts::>>" + response.toString());
				return response;
			}
			if (accList == null || accList.isEmpty()) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg("Account not present Or account status is not normal.");
				logger.error("Pigme Search Accounts::>>" + response.toString());
				return response;
			}
			response.setResponse(MSGConstants.SUCCESS);
			response.setErrorMsg(MSGConstants.SUCCESS_MSG);
			response.setAccList(accList);
			logger.error("Pigme Search Accounts::>>" + response.toString());
			return response;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			response.setAccList(accList);
			logger.error("Pigme Search Accounts::>>" + response.toString());
			return response;
		} finally {
			session.close();
			session = null;
		}
	}

	public static PigmeAccountsResponse searchPigMeAccCust(String accNo) {
		PigmeAccountsResponse response = new PigmeAccountsResponse();
		ArrayList<PigmeAccountsResponse> accList = new ArrayList<>();
		Session session = HBUtil.getSessionFactory().openSession();
		try {
			D009022 sourceAccount = DataUtils.getAccount(accNo.trim());
			if (sourceAccount == null) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
				return response;
			}
			TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount,
					String.valueOf("10"), "AGENT");
			logger.error("res::>>>" + res);
			if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(res.getErrorMsg());
				return response;
			}

			Criteria criteria = session.createCriteria(D047003.class);
			criteria.add(Restrictions.eq("id.lbrCode", sourceAccount.getId().getLbrCode()));
			criteria.add(Restrictions.eq("agtPrdAcctId", sourceAccount.getId().getPrdAcctId()));
			// criteria.add(Restrictions.ne("id.acctStat",Byte.valueOf("3")));
			criteria.add(Restrictions.ne("acctStat", Byte.valueOf("3")));
			List<D047003> list = criteria.list();
			logger.error("list::>>>" + list);
			if (list == null || list.isEmpty()) {
				session.close();
				session = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NO_LINKED_ACCOUNT_FOUND);
				return response;
			}
			ArrayList<String> acList = new ArrayList<String>();
			for (D047003 d047003 : list) {
				logger.error("d047003.getId().getCustPrdAcctId():::>>" + d047003.getId().getCustPrdAcctId());
				acList.add(d047003.getId().getCustPrdAcctId());
				D009022 d009022 = DataUtils.getAccountDetails(d047003.getId().getLbrCode(),
						d047003.getId().getCustPrdAcctId());
				if (d009022 != null) {
					PigmeAccountsResponse response2 = new PigmeAccountsResponse();
					response2.setAccNo(d009022.getId().getPrdAcctId());
					response2.setBrCode(d009022.getId().getLbrCode() + "");
					response2.setCustNo(d009022.getCustNo() + "");
					response2.setName(d009022.getLongName().trim());
					response2.setFormattedAcc(
							d009022.getId().getLbrCode() + "/" + d009022.getId().getPrdAcctId().substring(0, 8).trim()
									+ "/" + Long.valueOf(d009022.getId().getPrdAcctId().substring(16, 24)));
					/*
					 * response2.setBalance("" + Double.valueOf(
					 * Double.valueOf(d009022.getActClrBalFcy()) -
					 * Double.valueOf(d009022.getTotalLienFcy())));
					 */
					response2.setBalance(Double.valueOf(d009022.getActClrBalFcy()) + "");// removed Lien Balance
					accList.add(response2);
					response2 = null;
				}
			}
			if (accList != null & accList.size() > 0) {
				response.setResponse(MSGConstants.SUCCESS);
				response.setErrorMsg(MSGConstants.SUCCESS_MSG);
				session.close();
				session = null;
				response.setAccList(accList);
				return response;
			}

			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.NO_LINKED_ACCOUNT_FOUND);
			session.close();
			session = null;
			response.setAccList(accList);
			return response;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("Exception= ", e);
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			session.close();
			session = null;
			response.setAccList(accList);
			return response;
		}

	}

	public static BranchDetailsResponse getCityState(String cityCode, String stateCode) {
		BranchDetailsResponse res = DataUtils.getCityState(cityCode, stateCode);
		if (res == null) {
			BranchDetailsResponse resp = new BranchDetailsResponse();
			resp.setResponse(MSGConstants.ERROR);
			resp.setErrorMessage(MSGConstants.RECORD_NOT_FOUND);
			return resp;
		}
		return res;
	}

	public static BranchDetailsResponse getATMBranchLocator(String cityCode, String stateCode, String custNo) {
		BranchDetailsResponse res = DataUtils.getATMBranchLocator(cityCode, stateCode);
		logger.error("res::>>" + res);
		if (res == null) {
			BranchDetailsResponse response = new BranchDetailsResponse();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.RECORD_NOT_FOUND);
			return response;
		}
		return res;
	}

	public static DepositeAcctOpenResponseNew openTermDepositAccount(DepositeAcctOpenRequest request)
			throws NumberFormatException, Exception {
		DepositeAcctOpenResponseNew response = new DepositeAcctOpenResponseNew();
		VoucherCommon common = new VoucherCommon();

		D001004 system = DataUtils.getSystemParameter(Integer.parseInt(request.getBrcode()), "LASTOPENDATE");
		Date drOperationDate = DateUtil.getDateFromStringNew(system.getValue().trim().substring(1));
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();

		Criteria criteria = session.createCriteria(D009022.class);
		criteria.add(Restrictions.eq("custNo", Integer.valueOf(request.getCustNo().trim())));
		List<D009022> list = criteria.list();
		logger.error("List of Accounts ::>>>" + list);
		logger.error("List Of accounts::>>>" + list);
		if (list == null || list.size() < 1) {
			session.close();
			session = null;
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_CUSTOMER);
			response.setSuccess(false);
			return response;
		}
		Criteria criteria2 = session.createCriteria(D009021.class);
		if (request.getAccType().equalsIgnoreCase("RE")) {
			// criteria2.add(Restrictions.eq("id.prdCd", request.getProdCode().trim()));
		} else {
			criteria2.add(Restrictions.eq("id.prdCd", request.getProdCode().trim()));

			if (criteria2.list().size() < 1) {
				session.close();
				session = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.PRODUCT_NOT_FOUND);
				response.setSuccess(false);
				return response;
			}
		}

		Criteria criteria4 = session.createCriteria(D009012.class);
		criteria4.add(Restrictions.eq("custNo", Integer.valueOf(request.getCustNo().trim())));
		List<D009012> custDetails = criteria4.list();
		logger.error("List of Accounts ::>>>" + custDetails);
		logger.error("List Of accounts::>>>" + custDetails);
		if (custDetails == null || custDetails.size() < 1) {
			session.close();
			session = null;
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_CUSTOMER);
			response.setSuccess(false);
			return response;
		}

		Criteria criteria5 = session.createCriteria(D009011.class);
		criteria5.add(Restrictions.eq("custNo", Integer.valueOf(request.getCustNo().trim())));
		List<D009011> custAddrDetls = criteria5.list();
		logger.error("List of Accounts ::>>>" + custAddrDetls);
		logger.error("List Of accounts::>>>" + custAddrDetls);
		if (custAddrDetls == null || custAddrDetls.size() < 1) {
			session.close();
			session = null;
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_CUSTOMER);
			response.setSuccess(false);
			return response;
		}

		if (request.getAccType().trim().equalsIgnoreCase("CR")) {
			response.setRrn(null);
			logger.error("request.getAccountNo()::>>" + request.getDebitAccount());
			logger.error("request.getActivity()::>>" + request.getAccType());
			logger.error("request.getCustomerNo():>" + request.getCustNo());
			logger.error("request.getProductCode()::>>" + request.getProdCode());
			logger.error("request.getAmount():>>" + request.getDepositAmount());
			logger.error("request.getLbrCode()::>>" + request.getBrcode());
			logger.error("request.getNoOfDays()::>>" + request.getDays());
			logger.error("request.getNoOfMonths()::>>" + request.getMonth());
			TransactionValidationResponse res = new TransactionValidationResponse();
			/***
			 * Added By Aniket Desai on 30th May, 2020 for Tenure Validation: ---Start---
			 ***/
			List<Object[]> newList = new ArrayList<Object[]>();
			newList = RequestServiceImpl.getFDProductList(request.getCustNo());
			List<DepositeParameters> productList = new ArrayList<DepositeParameters>();
			if (newList != null) {
				try {
					for (int i = 0; i < newList.size(); i++) {

						int type = 2;
						Object[] product = newList.get(i);

						if ((product[0] + "").trim().equalsIgnoreCase(request.getProdCode().trim())) {
							if ("B".equalsIgnoreCase(product[6].toString())
									|| "M".equalsIgnoreCase(product[6].toString())) {
								type = 2;
								Double totalMonths = (Math.round(((Double) (Double.parseDouble(request.getDays()) / 30)
										+ Double.parseDouble(request.getMonth())) * 100.0) / 100.0);

								Double maxMonths = ((Short) product[5]).doubleValue();
								Double minMonths = ((Short) product[4]).doubleValue();

								if (Double.compare(maxMonths, totalMonths) < 0
										|| Double.compare(minMonths, totalMonths) > 0) {

									logger.error("maxMonths<totalMonths=" + maxMonths + "< " + totalMonths);

									logger.error("minMonths<totalMonths=" + minMonths + "< " + totalMonths);
									response.setResponse(MSGConstants.ERROR);
									response.setErrorMessage("Invalid Tenure");
									response.setSuccess(false);
									return response;
								}
							} else if ("D".equalsIgnoreCase(product[6].toString())) {
								type = 1;
								Long totalDays = (Long.parseLong(request.getMonth()) * 30)
										+ Long.parseLong(request.getDays());

								Long maxMonths = ((Short) product[5]).longValue();
								Long minMonths = ((Short) product[4]).longValue();

								if (totalDays > maxMonths || minMonths > totalDays) {
									logger.error("minMonths<totalMonths");
									response.setResponse(MSGConstants.ERROR);
									response.setErrorMessage("Invalid Tenure");
									response.setSuccess(false);
									return response;
								}
							}

						}

					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("Exception= ", e);
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage("Technical Error");
					response.setSuccess(false);
					return response;
				}

			} else {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.RECORD_NOT_FOUND);
				response.setSuccess(false);
				return response;

			}
			/***
			 * Added By Aniket Desai on 30th May, 2020 for Tenure Validation: ---End---
			 ***/
			D009022 sourceAccount = DataUtils.getAccount(// DataUtils.getAccountDetails(request.getLbrCode().intValue(),
					request.getDebitAccount());
			res = TransactionServiceImpl.validateAccount(sourceAccount, request.getDepositAmount().toString(), "D");
			logger.error("Response of validate account::>>>" + res);
			logger.error("Response of validate account::>>>" + res);
			if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
				response.setResponse(MSGConstants.ERROR);
				t.rollback();
				session.close();
				response.setErrorMessage(res.getErrorMsg());
				response.setSuccess(false);
				return response;
			} else
				request.setDebitAccount(sourceAccount.getId().getPrdAcctId());
			// =========================================

			int usrCode2 = Integer.parseInt(ConfigurationLoader.getParameters(false).getProperty("IMPS_USER"));
			if (usrCode2 == 0) {
				logger.error("UsrCode Not Found.");
				t.rollback();
				session.close();
				session = null;
				t = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.ERROR_MSG);
				response.setSuccess(false);
				return response;
			}
			logger.error("UsrCode is : " + usrCode2);

			// if (isValid) {
			String accNo = "";
			D009022 d009022 = null;
			if (!"".equalsIgnoreCase(request.getAccNo()) && request.getAccNo().contains("#")) {
				String[] acctId = request.getAccNo().split("#");

				d009022 = CustomerMasterServiceImpl.getTDAccNo(acctId[1], Integer.parseInt(acctId[0]),
						Integer.valueOf(request.getCustNo().trim()), session);
				if (d009022 != null) {
					accNo = d009022.getId().getPrdAcctId();

					accNo = CustomerMasterServiceImpl.getNextTDRecipt(accNo, d009022.getId().getLbrCode());

					d009022.setShdTotBalFcy(d009022.getShdTotBalFcy() + Double.parseDouble(request.getDepositAmount()));
					d009022.setShdClrBalFcy(d009022.getShdClrBalFcy() + Double.parseDouble(request.getDepositAmount()));
					d009022.setActClrBalFcy(d009022.getActClrBalFcy() + Double.parseDouble(request.getDepositAmount()));
					d009022.setActTotBalFcy(d009022.getActTotBalFcy() + Double.parseDouble(request.getDepositAmount()));
					d009022.setActTotBalLcy(d009022.getActTotBalLcy() + Double.parseDouble(request.getDepositAmount()));
					session.update(d009022);
				}
				// d009022 = tdAcct;
			} else {
				accNo = CustomerMasterServiceImpl.getNextTDAccNo(request.getProdCode().trim(),
						Integer.parseInt(request.getBrcode()) + "");
				logger.error("accNo::>>>" + accNo);
				logger.error("accNo::>>>" + accNo);
				logger.error("Account number::>>" + accNo);
				logger.error("request.getLbrCode()::>>>" + Integer.parseInt(request.getBrcode()));
				d009022 = new D009022();
				D009022Id d009022Id = new D009022Id();
				d009022Id.setLbrCode(Integer.valueOf(request.getBrcode() + ""));
				d009022Id.setPrdAcctId(accNo);
				d009022.setId(d009022Id);
				d009022.setAcctStat(Byte.valueOf("1"));
				d009022.setAcctType(Byte.valueOf("" + custDetails.get(0).getCusType()));
				d009022.setActClrBalFcy(Double.parseDouble(request.getDepositAmount()));
				d009022.setActTotBalFcy(Double.parseDouble(request.getDepositAmount()));
				d009022.setActTotBalLcy(Double.parseDouble(request.getDepositAmount()));
				d009022.setChgHoldAmtFcy(Double.parseDouble(request.getDepositAmount()));
				d009022.setChqBookYn('N');
				d009022.setClosedUser(" ");
				d009022.setCurCd("INR");
				d009022.setCustAccessCat(Short.valueOf("0"));
				d009022.setCustNo(Integer.valueOf(request.getCustNo().trim()));
				d009022.setDateClosed(new Date("01-JAN-1900"));
				d009022.setDateOpen(new Date());
				d009022.setDbtrAddCb(0);
				d009022.setDbtrAddCd(new Date());
				d009022.setDbtrAddCk(0);
				d009022.setDbtrAddCs(Short.valueOf("0"));
				d009022.setDbtrAddCt(new Date());
				d009022.setDbtrAddMb(0);
				d009022.setDbtrAddMd(new Date());
				d009022.setDbtrAddMk(usrCode2);
				d009022.setDbtrAddMs(Short.valueOf("0"));
				d009022.setDbtrAddMt(new Date());
				d009022.setDbtrAuthDone(Byte.valueOf("1"));
				d009022.setDbtrAuthNeeded(Byte.valueOf("0"));
				d009022.setDbtrUpdtChkId(Short.valueOf("0"));
				d009022.setDbtrLhisTrnNo(0);
				d009022.setDbtrLupdCb(0);
				d009022.setDbtrLupdCd(new Date());
				d009022.setDbtrLupdCk(0);
				d009022.setDbtrLupdCs(Short.valueOf("0"));
				d009022.setDbtrLupdCt(new Date());
				d009022.setDbtrLupdMb(0);
				d009022.setDbtrLupdMd(new Date());
				d009022.setDbtrLupdMk(0);
				d009022.setFreezeType(Byte.valueOf("1"));
				d009022.setDbtrLupdMs(Short.valueOf("0"));
				d009022.setDbtrLupdMt(new Date());
				d009022.setDbtrRecStat(Byte.valueOf("0"));
				d009022.setDbtrTauthDone(Short.valueOf("1"));
				d009022.setDbtrUpdtChkId(Short.valueOf("0"));
				d009022.setDocFileNo("0");
				d009022.setDpYn('N');
				d009022.setFcyScheme(" ");
				d009022.setFlexiBalance(0);
				d009022.setFlexiLienBal(0);
				d009022.setUnClrEffFcy(0);
				d009022.setTotalLienFcy(0);
				d009022.setShdTotBalFcy(Double.parseDouble(request.getDepositAmount()));
				d009022.setSplInstr2(" ");
				d009022.setSplInstr1(" ");
				d009022.setShdClrBalFcy(Double.parseDouble(request.getDepositAmount()));
				d009022.setPlrLinkYn('N');
				d009022.setPenalOffSet(0);
				d009022.setOpenUser("IMPS");
				logger.error("Name Tiitle::>>>>" + list.get(0).getNameTitle().trim().toUpperCase());
				d009022.setNameTitle(list.get(0).getNameTitle().trim().toUpperCase());// name
				d009022.setMstrAuthMask(0);
				d009022.setModeOprn(Byte.valueOf("1"));// mode of operation
				d009022.setMinorType(Byte.valueOf("0"));// major minor
				d009022.setLongName(list.get(0).getLongName().trim().toUpperCase());// name
				logger.error("Long Name::>>>" + list.get(0).getLongName().trim().toUpperCase());
				d009022.setMstrAuthMask(0);
				d009022.setDtOfBirth(new Date());
				d009022.setLastCrDate(new Date());
				d009022.setLastDrDate(new Date());
				d009022.setLastCustCrDate(new Date());
				d009022.setLastCustDrDate(new Date());
				d009022.setLastIntAppDate(new Date());

				D010054 d010054 = AccountMasterServiceImpl.prepareD010054Obj(accNo,
						custAddrDetls.get(0).getAdd1().trim(), custAddrDetls.get(0).getAdd2().trim(),
						custAddrDetls.get(0).getAdd3().trim(), custAddrDetls.get(0).getArea() + "",
						request.getBrcode() + "", custAddrDetls.get(0).getCityCd(), custAddrDetls.get(0).getPagerNo(),
						custAddrDetls.get(0).getPinCode());

				session.save(d009022);
				session.save(d010054);
			}
			/*
			 * Criteria criteria3 = session.createCriteria(D020118.class);
			 * criteria3.add(Restrictions.eq("days",
			 * Short.valueOf(request.getDays().toString())));
			 * criteria3.add(Restrictions.eq("months",
			 * Short.valueOf(request.getMonth().toString())));
			 * criteria3.add(Restrictions.eq("id.prdCd", request.getProdCode().trim()));
			 * criteria3.addOrder(Order.desc("id.intEffDt")); List<D020118> list2 =
			 * criteria3.list(); if (list2 == null || list2.isEmpty()) {
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMessage(MSGConstants.INT_RATE_NOT_FOUND); return response; }
			 */
			// double rateOfint = list2.get(0).getIntRate();
			int noOfDays = Integer.parseInt(request.getDays());
			int actNoOfDays = noOfDays + Double.valueOf(Integer.parseInt(request.getMonth()) * 30.5d).intValue();
			// logger.error("Rate Of Intrest:::>>>>" + rateOfint);
			int lbrcode = d009022.getId().getLbrCode();
			D020004 d020004 = new D020004();
			D020004Id id = new D020004Id();
			id.setLbrCode(d009022.getId().getLbrCode());
			id.setPrdAcctId(accNo.substring(0, 24) + String.format("%08d", Long.valueOf(accNo.substring(24)) + 1l));
			d020004.setId(id);
			// double matValue =
			// DataUtils.getMatValue(Double.parseDouble(request.getDepositAmount()),
			// rateOfint, actNoOfDays);
			D020002 tdParameter = session.get(D020002.class, new D020002Id(lbrcode, request.getProdCode()));

			/***
			 * Added by Aniket Desai on 20th Feb, 2020 as suggested by Prashant
			 * Sir:---Start---
			 ***/
			if (tdParameter != null) {
				Long minPeriodInDays = 0L;
				Long maxPeriodInDays = 0L;
				if (tdParameter.getPeriodType() == 'B') {
					minPeriodInDays = (long) tdParameter.getMinPeriod();
					maxPeriodInDays = (long) (tdParameter.getMaxPeriod() * 30);
				} else if (tdParameter.getPeriodType() == 'D') {
					minPeriodInDays = (long) tdParameter.getMinPeriod();
					maxPeriodInDays = (long) tdParameter.getMaxPeriod();
				} else if (tdParameter.getPeriodType() == 'M') {
					minPeriodInDays = (long) (tdParameter.getMinPeriod() * 30);
					maxPeriodInDays = (long) (tdParameter.getMaxPeriod() * 30);
				}

				if ('I' == tdParameter.getInstOrPrinc() && Long.parseLong(request.getDays()) > 0) {
					t.rollback();
					session.close();
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage("Days Must be '0'");
					response.setSuccess(false);
					return response;
				}

				Long userEnteredPeriod = Long.parseLong(request.getMonth()) * 30L + Long.parseLong(request.getDays());

				if (userEnteredPeriod < minPeriodInDays || userEnteredPeriod > maxPeriodInDays) {
					t.rollback();
					session.close();
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage("Minimum and Maximum Period Violation.");
					response.setSuccess(false);
					return response;
				}
			}
			/***
			 * Added by Aniket Desai on 20th Feb, 2020 as suggested by Prashant
			 * Sir:---End---
			 ***/

			if (Double.parseDouble(request.getDepositAmount()) > tdParameter.getMaxDepAmt()
					|| Double.parseDouble(request.getDepositAmount()) < tdParameter.getMinDepAmt()) {
				t.rollback();
				session.close();
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage("Exceeds Max Amount Of Product");
				response.setSuccess(false);
				return response;
			}

			d020004 = DataUtils.getMatValue(d020004, sourceAccount.getId().getPrdAcctId().trim(), d009022,
					drOperationDate, Long.parseLong(request.getBrcode()), request.getProdCode(),
					Long.parseLong(request.getMonth()), Long.parseLong(request.getDays()), request.getProdCode(),
					Double.parseDouble(request.getDepositAmount()), tdParameter);
			// logger.error("Maturity value::>>" + matValue);
			d020004.setAccPrvdAmtFcy(0);
			d020004.setAccPrvdAmtLcy(0);
			// d020004.setAsOffdate(DateUtil.getCurrentDate());
			// d020004.setBrokenPeriodInt(0);
			// d020004.setCertDate(DateUtil.getCurrentDate());
			// d020004.setClIntPaidAmt(0);
			d020004.setClIntRate(0);
			// d020004.setClNotionalRate(0);
			// d020004.setClNotionalRate(0);
			d020004.setClosedDate(new Date("01-JAN-1900"));
			// d020004.setCurCd("INR");
			d020004.setDbtrAddCb(0);
			d020004.setDbtrAddCd(new Date("01-JAN-1900"));
			d020004.setDbtrAddCk(0);
			d020004.setDbtrAddCs(Short.valueOf("0"));
			d020004.setDbtrAddMb(0);
			d020004.setDbtrAddMd(new Date());
			d020004.setDbtrAddMk(usrCode2);
			d020004.setDbtrAddMs(Short.valueOf("0"));
			d020004.setDbtrAddMt(new Date("01-JAN-1900"));
			d020004.setDbtrAuthDone(Byte.valueOf("1"));
			d020004.setDbtrAuthNeeded(Byte.valueOf("0"));
			d020004.setDbtrLhisTrnNo(0);
			d020004.setDbtrLupdCb(0);
			d020004.setDbtrLupdCd(new Date("01-JAN-1900"));
			d020004.setDbtrAddCk(0);
			d020004.setDbtrAddCs(Short.valueOf("0"));
			d020004.setDbtrAddCt(new Date("01-JAN-1900"));
			d020004.setDbtrAddMb(0);
			d020004.setDbtrAddMd(new Date("01-JAN-1900"));
			// d020004.setDbtrAddMk(0);
			d020004.setDbtrLupdMs(Short.valueOf("0"));
			d020004.setDbtrLupdMt(new Date("01-JAN-1900"));
			d020004.setDbtrRecStat(Byte.valueOf("0"));
			// d020004.setUnClBalFcy(0);
			// d020004.setTrfrLbrCode(0);
			d020004.setTrfrAcctId("        000000000000000000000000");
			// d020004.setTotAmtPaid(0);
			// d020004.setTotalLien(0);
			// d020004.setTdsYn('Y');
			// d020004.setTdsReasonCd(Short.valueOf("1"));
			// d020004.setTdsAmtLcy(0);
			// d020004.setTdsAmtFcy(0);
			d020004.setSourceOfFunds(Byte.valueOf("1"));
			d020004.setRemarks("IMPS-TD");
			d020004.setReceiptStatus(Byte.valueOf("51"));
			d020004.setPrnSrNo(0);
			// d020004.setPeriodicIntAmt(0);
			// d020004.setOffSetRate(0);
			d020004.setNoticeType(Byte.valueOf("1"));
			d020004.setNoOfUnits(0);
			d020004.setNoOfRenewals(Short.valueOf("0"));
			// d020004.setNoOfMonths(Short.valueOf(request.getMonth().toString()));
			// d020004.setNoOfDays(Short.valueOf(request.getDays().toString()));
			if ('I' == tdParameter.getInstOrPrinc())
				d020004.setNoInst(Short.valueOf(request.getMonth().toString()));
			else
				d020004.setNoInst(Short.valueOf("0"));

			d020004.setNoCertPrint(Byte.valueOf("0"));
			d020004.setNextPayOutDt(new Date("01-JAN-1900"));
			d020004.setNameTitle(list.get(0).getNameTitle().trim());
			// d020004.setMonthsDelay(Short.valueOf("0"));
			// d020004.setMatVal(matValue);
			// d020004.setMatDate(new Date());
			d020004.setMaskRecieptNo("1");
			d020004.setMainBalLcy(Double.parseDouble(request.getDepositAmount()));
			d020004.setMainBalFcy(Double.parseDouble(request.getDepositAmount()));
			d020004.setLongName(list.get(0).getLongName().trim());
			d020004.setLockPeriod(Short.valueOf("0"));
			d020004.setLockEndDate(new Date());
			d020004.setLastUnitPaidDt(new Date("01-JAN-1900"));
			d020004.setLastRepayDate(new Date("01-JAN-1900"));
			d020004.setLastPayOutDate(new Date("01-JAN-1900"));
			d020004.setLastAmtPaid(0);
			// d020004.setIntRate(rateOfint);
			d020004.setDbtrTauthDone(new Byte("1"));
			d020004.setIntPrvdAmtLcy(0);
			d020004.setIntPayPer(0);
			d020004.setIntPayFreq(' ');
			d020004.setIntPayAmt(0);
			d020004.setIntPayableType('1');
			// d020004.setIntPaidAmtLcy(0);
			// d020004.setIntPaidAmtFcy(0);
			// d020004.setInstOrPrincAmt(Double.parseDouble(request.getDepositAmount()));
			// d020004.setInClBalFcy(0);
			// d020004.setId(id);
			d020004.setDbtrLupdCt(new Date("01-JAN-1900"));
			d020004.setDbtrLupdMd(new Date("01-JAN-1900"));
			// d020004.setAsOffdate(drOperationDate);
			try {

				boolean isValid = false;
				String acct = "";
				int setNo = VoucherCommon.getNextSetNo();
				int mainScrollNo = VoucherCommon.getNextScrollNo();
				if (d020004.getId().getLbrCode() == sourceAccount.getId().getLbrCode()) {

					logger.error("Same Branch Fund Transfer....SetNo:-" + setNo);
					logger.error("Same Branch Fund Transfer...." + setNo);
					String rrn = DataUtils.getNextRRN();
					common.debitSameBranch(Integer.parseInt(request.getBrcode()), request.getDebitAccount(),
							MSGConstants.IMPS_CHANNEL, setNo, mainScrollNo, "IMPS/TD/" + request.getProdCode(),
							Double.parseDouble(request.getDepositAmount()), rrn, mainScrollNo, session);
					// common.balance(Integer.parseInt(request.getBrcode()),request.getDebitAccount(),
					// Double.parseDouble(request.getDepositAmount()), "D");
					if (!common.isAborted) {
						if (VoucherMPOS
								.updateProductBalances(Double.parseDouble(request.getDepositAmount()),
										Integer.parseInt(request.getBrcode()), "D",
										sourceAccount.getId().getPrdAcctId().substring(0, 8).trim(), session)
								.equalsIgnoreCase(MSGConstants.SUCCESS))
							CoreTransactionMPOS.balance(Integer.parseInt(request.getBrcode()),
									sourceAccount.getId().getPrdAcctId().trim(),
									Double.parseDouble(request.getDepositAmount()), "D", session);
						else {
							logger.error("Transaction unsuccessful");
							t.rollback();
							session.close();
							session = null;
							t = null;
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMessage(MSGConstants.ERROR_MSG);
							response.setSuccess(false);
							return response;
						}
						logger.error("Transaction successful");
						// common.creditSameBranch(request.getLbrCode().intValue(),
						// MSGConstants.ABB_ACC, MSGConstants.IMPS_CHANNEL, setNo ,
						// VoucherCommon.getNextScrollNo(),
						// "IMPS/TD/"+request.getProductCode(), request.getAmount(),
						// rrn,mainScrollNo);
						// CoreTransactionMPOS.balance(request.getLbrCode().intValue(),MSGConstants.ABB_ACC,
						// request.getAmount(), "C");//
						/*
						 * t.commit(); response.setResponse(MSGConstants.SUCCESS);
						 * response.setErrorMessage(MSGConstants.SUCCESS_MSG);
						 * response.setErrorCode(ResponseCodes.SUCCESS);
						 */
						isValid = true;
					} else {
						logger.error("Transaction unsuccessful");
						t.rollback();
						session.close();
						session = null;
						t = null;
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage("No Active Batch Found");
						response.setSuccess(false);
						return response;
					}
					// =========================================

					common.creditSameBranchDPA(d009022.getId().getLbrCode(), d020004.getId().getPrdAcctId(),
							MSGConstants.IMPS_CHANNEL, setNo, VoucherCommon.getNextScrollNo(),
							"IMPS/TD/" + request.getProdCode(), Double.parseDouble(request.getDepositAmount()), rrn,
							mainScrollNo, session);

					if (!common.isAborted) {
						if (VoucherMPOS
								.updateProductBalances(Double.parseDouble(request.getDepositAmount()),
										d009022.getId().getLbrCode(), "C",
										d009022.getId().getPrdAcctId().substring(0, 8).trim(), session)
								.equalsIgnoreCase(MSGConstants.SUCCESS)) {
							logger.error("success");
							/*
							 * CoreTransactionMPOS.balance(d009022.getId().getLbrCode(),
							 * d009022.getId().getPrdAcctId().trim(),
							 * Double.parseDouble(request.getDepositAmount()), "C", session);
							 */
						} else {
							logger.error("Transaction unsuccessful");
							t.rollback();
							session.close();
							session = null;
							t = null;
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMessage(MSGConstants.ERROR_MSG);
							response.setSuccess(false);
							return response;
						}
						D009122 d009122 = AccountMasterServiceImpl.prepareD009122Obj(d009022.getId().getPrdAcctId(),
								" ", d009022.getId().getLbrCode() + "", session);

						if (!request.getAccNo().contains("#"))
							session.save(d009122);
						session.save(d020004);
						if (d020004.getCentrelisedBrwiseCustTDSFile() != null)
							session.saveOrUpdate(d020004.getCentrelisedBrwiseCustTDSFile());
						if (d020004.getCurrentProjection() != null)
							session.saveOrUpdate(d020004.getCurrentProjection());

						logger.error("Transaction successful");

					} else {
						logger.error("Transaction unsuccessful");
						t.rollback();
						session.close();
						session = null;
						t = null;
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage(MSGConstants.ERROR_MSG);
						response.setSuccess(false);
						return response;
					}

					// CoreTransactionMPOS.balance(d009022.getId().getLbrCode(),d009022.getId().getPrdAcctId(),request.getAmount(),
					// "C");
					acct = DataUtils.get15DigitAccountNumber(lbrcode, accNo);
					t.commit();
					session.close();
					session = null;
					t = null;
					response.setResponse(MSGConstants.SUCCESS);
				} else {

					logger.error("Other Branch Fund Transfer....SetNo:-" + setNo);
					logger.error("Other Branch Fund Transfer...." + setNo);
					String rrn = DataUtils.getNextRRN();
					int lbrCode = sourceAccount.getId().getLbrCode();
					int reconNo = common.getNextReconNo(lbrCode);
					common.debitSameBranch(sourceAccount.getId().getLbrCode(), request.getDebitAccount(),
							MSGConstants.IMPS_CHANNEL, setNo, mainScrollNo, "IMPS/TD/" + request.getProdCode(),
							Double.parseDouble(request.getDepositAmount()), rrn, mainScrollNo, session);

					if (!common.isAborted) {
						if (VoucherMPOS
								.updateProductBalances(Double.parseDouble(request.getDepositAmount()),
										sourceAccount.getId().getLbrCode(), "D",
										sourceAccount.getId().getPrdAcctId().substring(0, 8).trim(), session)
								.equalsIgnoreCase(MSGConstants.SUCCESS)) {
							/*
							 * CoreTransactionMPOS.balanceForAjantha(Integer.parseInt(request.getBrcode()),
							 * sourceAccount.getId().getPrdAcctId().trim(),
							 * Double.parseDouble(request.getDepositAmount()), "D", session,
							 * creditProductMaster);
							 */
							if (!common.isAborted) {
								logger.error("Transaction successful");
								int setNoABB = VoucherCommon.getNextSetNo();
								int scrollNoAbb = VoucherCommon.getNextScrollNo();
								int scrollNoAbb1 = VoucherCommon.getNextScrollNo();
								int scrollNoAbb2 = VoucherCommon.getNextScrollNo();
								common.creditABB(lbrCode, MSGConstants.ABB_ACC, MSGConstants.IMPS_CHANNEL, setNo,
										scrollNoAbb, "IMPS/TD/", lbrCode,
										Double.parseDouble(request.getDepositAmount()), rrn, reconNo, session);
								if (!common.isAborted) {
									if (VoucherMPOS
											.updateProductBalances(Double.parseDouble(request.getDepositAmount()),
													lbrCode, "C", MSGConstants.ABB, session)
											.trim().equalsIgnoreCase(MSGConstants.SUCCESS)) {
										if (!common.isAborted) {
											int crBrCode = d020004.getId().getLbrCode();
											common.creditSameBranchDPA(d020004.getId().getLbrCode(),
													d020004.getId().getPrdAcctId(), MSGConstants.ABB, setNoABB,
													scrollNoAbb1, "IMPS/TD/" + request.getProdCode(),
													Double.parseDouble(request.getDepositAmount()), rrn, mainScrollNo,
													session);

											if (!common.isAborted) {
												if (VoucherMPOS.updateProductBalances(
														Double.parseDouble(request.getDepositAmount()),
														d009022.getId().getLbrCode(), "C",
														d009022.getId().getPrdAcctId().substring(0, 8).trim(), session)
														.equalsIgnoreCase(MSGConstants.SUCCESS)) {
													logger.error("success");
													if (!common.isAborted) {
														logger.error("Other Bank GL Transaction successful");
														common.debitABB(crBrCode, MSGConstants.ABB_ACC,
																MSGConstants.ABB, setNoABB, scrollNoAbb2, "IMPS/TD/", 2,
																Double.parseDouble(request.getDepositAmount()), rrn,
																reconNo, session);
														if (!common.isAborted) {
															if (VoucherMPOS
																	.updateProductBalances(
																			Double.parseDouble(
																					request.getDepositAmount()),
																			crBrCode, "D",
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
																		scrollNoAbb1, scrollNoAbb2, crBrCode,
																		d020004.getId().getPrdAcctId(),
																		Double.parseDouble(request.getDepositAmount()),
																		"D");
																System.out.println("d100001::>>>" + d100001);

																D100002 d100002 = VoucherMPOS.prepareRecon2Obj(lbrCode,
																		reconNo, opdate, 999999, onlineBatchName,
																		benBatchCode, setNo, scrollNoAbb, setNoABB,
																		scrollNoAbb1, scrollNoAbb2, crBrCode,
																		d020004.getId().getPrdAcctId(),
																		Double.parseDouble(request.getDepositAmount()),
																		"D");//
																System.out.println("d100002::>>>" + d100002);
																String finalBalannce = CoreTransactionMPOS.balanceOld(
																		sourceAccount.getId().getLbrCode(),
																		sourceAccount.getId().getPrdAcctId(),
																		Double.parseDouble(request.getDepositAmount()),
																		"D", session);
																logger.error("Final Balance:=" + finalBalannce);
																try {
																	session.save(d100001);
																	session.save(d100002);
																	// session.flush();

																} catch (Exception e) {
																	// TODO: handle
																	// exception
																	e.printStackTrace();
																	t.rollback();
																	session.close();
																	session = null;
																	t = null;
																	response.setResponse(MSGConstants.ERROR);
																	response.setErrorMessage(MSGConstants.ERROR_MSG);
																	response.setSuccess(false);
																	return response;
																}
															} else {
																logger.error("ABB Transaction unsuccessful");
																t.rollback();
																session.close();
																session = null;
																t = null;
																response.setResponse(MSGConstants.ERROR);
																response.setErrorMessage(MSGConstants.ERROR_MSG);
																response.setSuccess(false);
																return response;
															}
														} else {
															logger.error("ABB Transaction unsuccessful");
															t.rollback();
															session.close();
															session = null;
															t = null;
															response.setResponse(MSGConstants.ERROR);
															response.setErrorMessage(MSGConstants.ERROR_MSG);
															response.setSuccess(false);
															return response;
														}
													} else {
														logger.error("ABB Transaction unsuccessful");

														t.rollback();
														session.close();
														session = null;
														t = null;
														response.setResponse(MSGConstants.ERROR);
														response.setErrorMessage(MSGConstants.ERROR_MSG);
														response.setSuccess(false);
														return response;
													}
												} else {
													logger.error("Transaction unsuccessful");
													t.rollback();
													session.close();
													session = null;
													t = null;
													response.setResponse(MSGConstants.ERROR);
													response.setErrorMessage(MSGConstants.ERROR_MSG);
													response.setSuccess(false);
													return response;
												}

												D009122 d009122 = AccountMasterServiceImpl.prepareD009122Obj(
														d009022.getId().getPrdAcctId(), " ",
														d009022.getId().getLbrCode() + "", session);

												if (!request.getAccNo().contains("#"))
													session.save(d009122);

												session.save(d020004);
												if (d020004.getCentrelisedBrwiseCustTDSFile() != null)
													session.saveOrUpdate(d020004.getCentrelisedBrwiseCustTDSFile());
												if (d020004.getCurrentProjection() != null)
													session.saveOrUpdate(d020004.getCurrentProjection());

												logger.error("Transaction successful");

											} else {
												logger.error("Transaction unsuccessful");
												t.rollback();
												session.close();
												session = null;
												t = null;
												response.setResponse(MSGConstants.ERROR);
												response.setErrorMessage(MSGConstants.ERROR_MSG);
												response.setSuccess(false);
												return response;
											}

											// CoreTransactionMPOS.balance(d009022.getId().getLbrCode(),d009022.getId().getPrdAcctId(),request.getAmount(),
											// "C");
											acct = DataUtils.get15DigitAccountNumber(lbrcode, accNo);
											t.commit();
											session.close();
											session = null;
											t = null;
											response.setResponse(MSGConstants.SUCCESS);
										}
									}
								}
							} else {
								logger.error("Transaction unsuccessful");
								t.rollback();
								session.close();
								session = null;
								t = null;
								response.setResponse(MSGConstants.ERROR);
								response.setErrorMessage(MSGConstants.ERROR_MSG);
								response.setSuccess(false);
								return response;

							}
						} else {
							logger.error("Transaction unsuccessful");
							t.rollback();
							session.close();
							session = null;
							t = null;
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMessage(MSGConstants.ERROR_MSG);
							response.setSuccess(false);
							return response;
						}
						logger.error("Transaction successful");
						// common.creditSameBranch(request.getLbrCode().intValue(),
						// MSGConstants.ABB_ACC, MSGConstants.IMPS_CHANNEL, setNo ,
						// VoucherCommon.getNextScrollNo(),
						// "IMPS/TD/"+request.getProductCode(), request.getAmount(),
						// rrn,mainScrollNo);
						// CoreTransactionMPOS.balance(request.getLbrCode().intValue(),MSGConstants.ABB_ACC,
						// request.getAmount(), "C");//
						/*
						 * t.commit(); response.setResponse(MSGConstants.SUCCESS);
						 * response.setErrorMessage(MSGConstants.SUCCESS_MSG);
						 * response.setErrorCode(ResponseCodes.SUCCESS);
						 */
						isValid = true;
					} else {
						logger.error("Transaction unsuccessful");
						t.rollback();
						session.close();
						session = null;
						t = null;
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage(MSGConstants.ERROR_MSG);
						response.setSuccess(false);
						return response;
					}

				}
				logger.error("Account No:" + acct);
				response.setErrorMessage("Dear Customer your account has been successfully opened under branch code "
						+ lbrcode + " and account number is "
						+ SwiftCoreUtil.getAccountNoFormatted(d020004.getId().getPrdAcctId()));
				response.setAccNo(acct);
				response.setCustNo(request.getCustNo().trim());
				response.setRrn(null);

				response.setSuccess(true);
				return response;
			} catch (Exception e) {
				// TODO: handle exception
				logger.error("Exception= ", e);
				e.printStackTrace();
				t.rollback();
				session.close();
				session = null;
				logger.error("Exception= ", e);
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
				response.setSuccess(false);
				return response;
			}

		} else if (request.getAccType().equalsIgnoreCase("RE")) {
			String accNo = request.getDebitAccount();
			int lbrcode = Integer.parseInt(request.getDebitAccount().substring(0, 3));
			logger.error("accNo::>>>" + accNo);
			logger.error("lbrcode::>>>" + lbrcode);
			TransactionValidationResponse res = new TransactionValidationResponse();
			D009022 sourceAccount = DataUtils.getAccount(// DataUtils.getAccountDetails(request.getLbrCode().intValue(),
					request.getDebitAccount());

			Criteria criteria3 = session.createCriteria(D047003.class);
			criteria3.add(Restrictions.eq("id.lbrCode", sourceAccount.getId().getLbrCode()));
			criteria3.add(Restrictions.eq("agtPrdAcctId", sourceAccount.getId().getPrdAcctId()));
			List<D047003> list3 = criteria3.list();

			if (list3 == null || list3.isEmpty()) {
				res = TransactionServiceImpl.validateAccount(sourceAccount, request.getDepositAmount(), "D");
			} else {
				logger.error("Agent::>>>" + sourceAccount.toString());
				res = TransactionServiceImpl.validateAccount(sourceAccount, request.getDepositAmount(), "AGENT");
			}

			if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(res.getErrorMsg());
				response.setSuccess(false);
				return response;
			}
			int destLbrcode = Integer.parseInt(request.getAccNo().substring(0, 3));
			System.out.println("accNo::>>>" + request.getAccNo());
			System.out.println("lbrcode::>>>" + destLbrcode);
			D009022 destiAccount = DataUtils.getAccount(request.getAccNo());
			System.out.println("Destination Account::>>>" + destiAccount);
			res = TransactionServiceImpl.validateAccount(destiAccount, request.getDepositAmount(), "C");
			if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)
					&& !("RD").equalsIgnoreCase(res.getOutput())) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(res.getErrorMsg());
				response.setSuccess(false);
				return response;
			}
			logger.error("" + Long.valueOf(destiAccount.getId().getPrdAcctId().substring(16, 32))
					+ Long.parseLong(request.getAdd1()));
			Long recieptNo = Long.valueOf(destiAccount.getId().getPrdAcctId().substring(16, 32))
					+ Long.parseLong(request.getAdd1());
			System.out.println("recieptNo::>>" + recieptNo);
			logger.error("recieptNo::>>>" + recieptNo);
			String recNo = destiAccount.getId().getPrdAcctId().substring(0, 16) + String.format("%016d", recieptNo);
			System.out.println("Final Account Number::>>>>" + recNo);
			logger.error("Final Reciept Number::>>" + recNo);
			D020004 rdAcct = session.get(D020004.class, new D020004Id(destLbrcode, recNo));
			if (rdAcct == null) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage("RD Account Not Found");
				response.setSuccess(false);
				return response;
			} else {
				if (rdAcct.getReceiptStatus() != 51) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage("RD Receipt Not Open");
					response.setSuccess(false);
					return response;
				}
				if ((rdAcct.getNoInst() * rdAcct.getInstOrPrincAmt()) < (rdAcct.getMainBalFcy()
						+ Double.parseDouble(request.getDepositAmount()))) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage("RD Amount Greater than Receipt Amount");
					response.setSuccess(false);
					return response;
				}

				if (rdAcct.getInstOrPrincAmt() % Double.parseDouble(request.getDepositAmount()) != 0) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage("RD Amount Not in Multiple of Receipt Amount");
					response.setSuccess(false);
					return response;
				}
			}

			/*
			 * TransactionValidationResponse res1 = new TransactionValidationResponse();
			 * res1 = TransactionServiceImpl.validateAccount(destiAccount,
			 * request.getDepositAmount(), "C"); if (res1 != null &&
			 * res1.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMessage(res1.getErrorMsg()); response.setSuccess(false);
			 * return response; }
			 */
			String rrn = DataUtils.getNextRRN();
			HashMap<String, String> result = VoucherCommon.otherBranchVouchers(lbrcode,
					sourceAccount.getId().getPrdAcctId(), destLbrcode, recNo, "RD~MPOS", "RD/" + rrn,
					Double.parseDouble(request.getDepositAmount()), rrn);
			logger.error("Result:>>>" + result);
			if (null != result) {
				if (result.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {

					try {
						int resu = session
								.createSQLQuery("update D020004 set LastRepayDate='"
										+ DateUtility.getDateFromDateAsString(new Date(), "ddMMMyyyy")
										+ "',MainBalFcy=MainBalFcy+" + Double.parseDouble(request.getDepositAmount())
										+ ", MainBalLcy=MainBalLcy+" + Double.parseDouble(request.getDepositAmount())
										+ " where LBrCode=" + destLbrcode + " AND PrdAcctId='" + recNo + "'")
								.executeUpdate();

						t.commit();
						session.close();
						session = null;
						t = null;
						response.setRrn(rrn);
						response.setResponse(MSGConstants.SUCCESS);
						response.setErrorMessage(MSGConstants.SUCCESS_MSG);
						response.setSuccess(true);
						return response;
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						session.close();
						session = null;
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
						response.setSuccess(false);
						return response;
					}
				}
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.TRANSACTION_PROCESS_FAILED);
				return response;
			}
		}
		response.setResponse(MSGConstants.ERROR);
		response.setErrorMessage(MSGConstants.INVALID_ACTIVITY);
		return response;
	}

	public static CustomerDetails fetchMobno(String accNo) {
		// TODO Auto-generated method stub
		CustomerDetails details = new CustomerDetails();
		D009022 d009022 = DataUtils.getAccount(accNo.trim());
		logger.error("d009022::>>>" + d009022);
		if (d009022 == null) {
			details.setResponse(MSGConstants.ERROR);
			details.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
			return details;
		}
		Session session = HBUtil.getSessionFactory().openSession();

		Criteria criteria = session.createCriteria(D350078.class);
		criteria.add(Restrictions.eq("id.custNo", d009022.getCustNo() + ""));
		List<D350078> list = criteria.list();
		System.out.println("list::>>>" + list);
		session.close();
		session = null;
		if (list == null || list.size() < 1) {

			details.setResponse(MSGConstants.ERROR);
			details.setErrorMsg(MSGConstants.MOBILE_NO_NOT_FOUND);
			details.setMobileNo("123456789100");
			return details;
		}
		System.out.println("list.get(0).getId().getMobileNo()::>>>" + list.get(0).getId().getMobileNo());
		details.setResponse(MSGConstants.SUCCESS);
		details.setErrorMsg(MSGConstants.SUCCESS_MSG);
		details.setMobileNo(list.get(0).getId().getMobileNo().trim());
		details.setCustNo(d009022.getCustNo() + "");
		return details;
	}

	public static CustomerDetails validAccount(String accNo, String custNo) {
		CustomerDetails response = new CustomerDetails();
		D009022 d009022 = DataUtils.validateAccountCustNo(accNo, custNo);
		System.out.println("d009022::>>" + d009022);

		if (d009022 == null) {
			System.out.println("D009022 " + d009022);
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
			return response;
		} else {
			System.out.println("In Else");
			TransactionValidationResponse res = TransactionServiceImpl.validateAccount(d009022, "10", "C");
			if (res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(res.getErrorMsg());
				return response;
			}
			response.setResponse(MSGConstants.SUCCESS);
			response.setErrorMsg(MSGConstants.SUCCESS_MSG);
			response.setAccNo(d009022.getId().getPrdAcctId());
			response.setLongName(d009022.getLongName().trim());
			return response;
		}
	}

	public static CustomerDetails updateMobileNo(String mobNo, String accNo15digit) {
		CustomerDetails response = new CustomerDetails();
		Session session = HBUtil.getSessionFactory().openSession();
		D009022 d009022 = DataUtils.getAccount(accNo15digit);
		if (d009022 == null) {
			session.close();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
			return response;
		}
		Transaction t = session.beginTransaction();
		Query<D350078> query = session.createQuery("UPDATE D350078 SET MobileNo = '" + "91" + mobNo.trim()
				+ "' WHERE CustNo = '" + d009022.getCustNo() + "'");
		int i = query.executeUpdate();

		Query<D350078> query1 = session.createQuery(
				"UPDATE D009011 SET PagerNo = '" + "91" + mobNo + "' WHERE CustNo = '" + d009022.getCustNo() + "'");
		int j = query1.executeUpdate();
		t.commit();
		session.close();
		session = null;
		t = null;
		if (i > 0 && j > 0) {
			response.setResponse(MSGConstants.SUCCESS);
			response.setErrorMsg(MSGConstants.SUCCESS_MSG);
			response.setErrorMsg(ResponseCodes.SUCCESS);
			return response;
		} else {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.MOB_NO_NOT_UPDATED);
			return response;
		}
	}

	public OtherChannelServiceResponse updateEmailId(String custNo, String emailId) {
		OtherChannelServiceResponse response = new OtherChannelServiceResponse();
		try {
			Session session = HBUtil.getSessionFactory().openSession();
			Transaction t = session.beginTransaction();
			Query<D350078> q = session.createQuery("UPDATE D350078 SET EmailId = ? WHERE CustNo = ? ");
			q.setParameter(0, emailId.trim());
			q.setParameter(1, custNo.trim());
			int rows = q.executeUpdate();
			t.commit();
			if (rows > 0) {
				response.setResponse(MSGConstants.SUCCESS);
				response.setErrorMessage(MSGConstants.SUCCESS_MSG);
				return response;
			}
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.FAIL_TO_UPDATE_EMAIL);
			return response;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.FAIL_TO_UPDATE_EMAIL);
			return response;
		}
	}

	public OtherChannelServiceResponse updateEmailIdIVR(String mobNo, String emailId) {
		OtherChannelServiceResponse response = new OtherChannelServiceResponse();
		try {
			Session session = HBUtil.getSessionFactory().openSession();
			Transaction t = session.beginTransaction();
			Query<D350078> q = session.createQuery("UPDATE D350078 SET EmailId = ? WHERE MobileNo = ? ");
			List<D009022> d009022 = DataUtils.getAccountFormMobile(mobNo);

			Query<D009011> q1 = session.createQuery("UPDATE D009011 SET EmailId = '" + emailId + "' WHERE CustNo = '"
					+ d009022.get(0).getCustNo() + "'");
			q.setParameter(0, emailId.trim());
			q.setParameter(1, "91" + mobNo.trim());
			int row1 = q1.executeUpdate();
			int rows = q.executeUpdate();
			t.commit();
			if (rows > 0 && row1 > 0) {
				response.setResponse(MSGConstants.SUCCESS);
				response.setErrorMessage("Dear Customer, as per your request your email id " + emailId
						+ " has been updated successfully.");
				return response;
			}
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.FAIL_TO_UPDATE_EMAIL);
			return response;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.FAIL_TO_UPDATE_EMAIL);
			return response;
		}
	}

	public static boolean isBlankOrNull(String value) {
		return value == null || value.trim().length() == 0;
	}

	public static CustNewMobileDetails newMobileNo(String mobNo, String custNo) {
		CustomerDetails response = new CustomerDetails();
		int i = 0;
		int j = 0;
		Customer d009011 = null;
		CustNewMobileDetails res = new CustNewMobileDetails();

		try (Session session = HBUtil.getSessionFactory().openSession()) {
			logger.error("Inside Mobile Update Fro Customer " + custNo);
			d009011 = DataUtils.getCustomer(custNo);
			if (d009011 == null) {
				res.setSuccess(false);
				res.setMessage(MSGConstants.CUSTOMER_NOT_FOUND);
				return res;
			}

			List<String> dupNo = session.createQuery("select id.custNo FROM D350078 WHERE id.mobileNo='91" + mobNo
					+ "' and id.custNo !='" + custNo + "'").getResultList();
			if (dupNo.size() != 0) {
				res.setSuccess(false);
				res.setMessage("Dear Customer , we are unable to update your mobile no " + mobNo
						+ " due to mobile no is already present for another customer");
				return res;
			}

			Transaction t = session.beginTransaction();
			D350078Id id = new D350078Id(custNo, d009011.getMobno());
			D350078 d350078 = session.get(D350078.class, id);
			short zero = 0;
			if (d350078 == null) {
				id = new D350078Id(custNo, mobNo);
				d350078 = new D350078(id, "IN", " ", " ", " ", zero, zero, zero, zero, zero, zero, new Date(), zero);

				d350078.setDbtrAddMk(zero);
				d350078.setDbtrAddMb(zero);
				d350078.setDbtrAddMs(zero);
				d350078.setDbtrAddMd(new Date());
				d350078.setDbtrAddMt(new Date());
				d350078.setDbtrAddCk(zero);
				d350078.setDbtrAddCb(zero);
				d350078.setDbtrAddCs(zero);
				d350078.setDbtrAddCd(new Date());
				d350078.setDbtrAddCt(new Date());
				d350078.setDbtrLupdMk(zero);
				d350078.setDbtrLupdMb(zero);
				d350078.setDbtrLupdMs(zero);
				d350078.setDbtrLupdMd(new Date());
				d350078.setDbtrLupdMt(new Date());
				d350078.setDbtrLupdCk(zero);
				d350078.setDbtrLupdCb(zero);
				d350078.setDbtrLupdCs(zero);
				d350078.setDbtrLupdCd(new Date());
				d350078.setDbtrLupdCt(new Date());
				d350078.setDbtrTauthDone(new Byte("1"));
				d350078.setDbtrRecStat(new Byte("0"));
				d350078.setDbtrAuthDone(new Byte("1"));
				d350078.setDbtrAuthNeeded(new Byte("0"));
				d350078.setDbtrUpdtChkId(new Byte("1"));
				d350078.setDbtrLhisTrnNo(zero);
				session.save(d350078);
				i = 1;
			} else {
				Query<D350078> query = session.createQuery("UPDATE D350078 SET MobileNo = '" + "91" + mobNo.trim()
						+ "' WHERE CustNo = '" + d009011.getCustno() + "'");
				i = query.executeUpdate();
			}
			Query<D350078> query1 = session.createQuery(
					"UPDATE D009011 SET PagerNo = '" + "91" + mobNo + "' WHERE CustNo = '" + d009011.getCustno() + "'");
			j = query1.executeUpdate();
			t.commit();
			session.close();
			logger.error("Mobile Updated to " + mobNo + " Fro Customer " + custNo);
			// session = null;
			t = null;
		} catch (Exception sql) {
			sql.printStackTrace();
		}

		if (i > 0 && j > 0) {

			res.setSuccess(true);
			res.setMessage(MSGConstants.SUCCESS_MSG);

			return res;
		} else {
			res.setSuccess(false);
			res.setMessage(MSGConstants.MOB_NO_NOT_UPDATED);

			return res;
		}

	}

	public static CustNewMobileDetails newEmailId(String emailId, String custNo) {
		CustomerDetails response = new CustomerDetails();
		int i = 0;
		int j = 0;
		Customer d009011 = null;
		CustNewMobileDetails res = new CustNewMobileDetails();

		try (Session session = HBUtil.getSessionFactory().openSession()) {
			logger.error("Inside Email Update Fro Customer " + custNo);
			d009011 = DataUtils.getCustomer(custNo);
			if (d009011 == null) {
				res.setSuccess(true);
				res.setMessage(MSGConstants.CUSTOMER_NOT_FOUND);
				return res;
			}

			Transaction t = session.beginTransaction();
			D350078Id id = new D350078Id(custNo, d009011.getMobno());
			D350078 d350078 = session.get(D350078.class, id);
			short zero = 0;
			if (d350078 == null) {
				id = new D350078Id(custNo, d009011.getMobno());
				d350078 = new D350078(id, "IN", " ", " ", emailId, zero, zero, zero, zero, zero, zero, new Date(),
						zero);

				d350078.setDbtrAddMk(zero);
				d350078.setDbtrAddMb(zero);
				d350078.setDbtrAddMs(zero);
				d350078.setDbtrAddMd(new Date());
				d350078.setDbtrAddMt(new Date());
				d350078.setDbtrAddCk(zero);
				d350078.setDbtrAddCb(zero);
				d350078.setDbtrAddCs(zero);
				d350078.setDbtrAddCd(new Date());
				d350078.setDbtrAddCt(new Date());
				d350078.setDbtrLupdMk(zero);
				d350078.setDbtrLupdMb(zero);
				d350078.setDbtrLupdMs(zero);
				d350078.setDbtrLupdMd(new Date());
				d350078.setDbtrLupdMt(new Date());
				d350078.setDbtrLupdCk(zero);
				d350078.setDbtrLupdCb(zero);
				d350078.setDbtrLupdCs(zero);
				d350078.setDbtrLupdCd(new Date());
				d350078.setDbtrLupdCt(new Date());
				d350078.setDbtrTauthDone(new Byte("1"));
				d350078.setDbtrRecStat(new Byte("0"));
				d350078.setDbtrAuthDone(new Byte("1"));
				d350078.setDbtrAuthNeeded(new Byte("0"));
				d350078.setDbtrUpdtChkId(new Byte("1"));
				d350078.setDbtrLhisTrnNo(zero);
				session.save(d350078);
				i = 1;
			} else {
				Query<D350078> query = session.createQuery("UPDATE D350078 SET EmailId = '" + emailId.trim()
						+ "' WHERE CustNo = '" + d009011.getCustno() + "'");
				i = query.executeUpdate();
			}
			Query<D350078> query1 = session.createQuery("UPDATE D009011 SET EmailId = '" + emailId.trim()
					+ "' WHERE CustNo = '" + d009011.getCustno() + "'");
			j = query1.executeUpdate();
			t.commit();
			session.close();
			logger.error("EmailId Updated to " + emailId + " Fro Customer " + custNo);
			// session = null;
			t = null;
		} catch (Exception sql) {
			sql.printStackTrace();
		}

		if (i > 0 && j > 0) {

			res.setSuccess(true);
			res.setMessage(MSGConstants.SUCCESS_MSG);

			return res;
		} else {
			res.setSuccess(true);
			res.setMessage(MSGConstants.MOB_NO_NOT_UPDATED);

			return res;
		}

	}

	public static List<Object[]> getFDProductList(String custno) {

		int brCode;
		try (Session session = HBUtil.getSessionFactory().openSession()) {

			Transaction t = session.beginTransaction();
			String queryString1 = "SELECT lbrCode from D009011 where custNo=" + custno;
			Query q1 = session.createQuery(queryString1);

			brCode = (int) q1.getSingleResult();

			List<Object[]> list = new ArrayList<Object[]>();
			if (ConfigurationLoader.getParameters(false).getProperty("DDS_YN").equalsIgnoreCase("Y")) {
				if ("Y".equalsIgnoreCase(ConfigurationLoader.getParameters(false).getProperty("TD_Products_All_YN"))) {
					String queryString = "select A.id.prdCd ,A.name, B.minDepAmt, B.maxDepAmt, B.minPeriod, B.maxPeriod, B.periodType FROM D009021 A, D020002 B WHERE A.id.lbrCode=B.id.lbrCode AND A.id.prdCd=B.id.prdCd and A.moduleType in(:moduleType) "
							+ "AND A.moduleType in(:moduleType2) AND A.acctStat=:accstat  AND A.id.lbrCode=:brcode";
					short mtype = 20;
					short mtype2 = 47;
					int lbrcode = (int) brCode;
					Byte accstat = 1;
					Query q = session.createQuery(queryString).setParameter("moduleType", mtype)
							.setParameter("moduleType2", mtype).setParameter("brcode", lbrcode)
							.setParameter("accstat", accstat);
					list = q.getResultList();
				} else {

					String queryString = "select A.id.prdCd ,A.name, B.minDepAmt, B.maxDepAmt, B.minPeriod, B.maxPeriod, B.periodType FROM D009021 A, D020002 B WHERE A.id.lbrCode=B.id.lbrCode AND "
							+ "A.id.prdCd=B.id.prdCd and A.moduleType in(:moduleType) AND A.moduleType in(:moduleType2) AND A.acctStat=:accstat  AND A.id.lbrCode=:brcode AND B.id.prdCd in (:prdCd)";
					// short[] mtype = {20,47};
					short mtype = 20;
					short mtype2 = 47;
					int lbrcode = (int) brCode;
					String[] prdCd = ConfigurationLoader.getParameters(false).getProperty("TD_Products_List")
							.split(",");
					Byte accstat = 1;
					Query q = session.createQuery(queryString).setParameter("moduleType", mtype)
							.setParameter("moduleType2", mtype).setParameter("brcode", lbrcode)
							.setParameter("accstat", accstat).setParameterList("prdCd", prdCd);
					list = q.getResultList();
				}
				// queryObject.setLong(4, noofdays);
			} else {
				if ("Y".equalsIgnoreCase(ConfigurationLoader.getParameters(false).getProperty("TD_Products_All_YN"))) {
					String queryString = "select A.id.prdCd ,A.name, B.minDepAmt, B.maxDepAmt, B.minPeriod, B.maxPeriod, B.periodType FROM D009021 A, D020002 B WHERE A.id.lbrCode=B.id.lbrCode AND A.id.prdCd=B.id.prdCd and A.moduleType in(:moduleType) AND A.acctStat=:accstat  AND A.id.lbrCode=:brcode";
					short mtype = 20;
					int lbrcode = (int) brCode;
					Byte accstat = 1;
					Query q = session.createQuery(queryString).setParameter("moduleType", mtype)
							.setParameter("brcode", lbrcode).setParameter("accstat", accstat);
					list = q.getResultList();
				} else {
					String queryString = "select A.id.prdCd ,A.name, B.minDepAmt, B.maxDepAmt, B.minPeriod, B.maxPeriod, B.periodType FROM D009021 A, D020002 B WHERE A.id.lbrCode=B.id.lbrCode AND "
							+ "A.id.prdCd=B.id.prdCd and A.moduleType=:moduleType AND A.acctStat=:accstat  AND A.id.lbrCode=:brcode AND B.id.prdCd in (:prdCd)";
					short mtype = 20;
					int lbrcode = (int) brCode;
					String[] prdCd = ConfigurationLoader.getParameters(false).getProperty("TD_Products_List")
							.split(",");
					Byte accstat = 1;
					Query q = session.createQuery(queryString).setParameter("moduleType", mtype)
							.setParameter("brcode", lbrcode).setParameter("accstat", accstat)
							.setParameterList("prdCd", prdCd);
					list = q.getResultList();
				}
			}
			if (list != null && list.size() != 0) {
				t.commit();
				session.close();
				t = null;
				return list;
			} else {
				return null;
			}

			// session = null;

		} catch (Exception sql) {
			sql.printStackTrace();
		}
		return null;
	}

	public static CustomerDepositePrdCdList getCustDepositeProductList(String custno) {

		int brCode;
		CustomerDepositePrdCdList custList = new CustomerDepositePrdCdList();

		try (Session session = HBUtil.getSessionFactory().openSession()) {

			Transaction t = session.beginTransaction();
			String queryString1 = "SELECT lbrCode from D009011 where custNo=" + custno;
			Query q1 = session.createQuery(queryString1);
			try {
				brCode = (int) q1.getSingleResult();
			} catch (Exception ne) {
				custList.setCustomerNo(custno);
				custList.setResponse("Failed");
				custList.setErrorMessage("Customer Not valid");
				custList.setErrorCode("53");
				return custList;
			}
			List<Object[]> list = new ArrayList<Object[]>();
			String queryString = " ";

			if (ConfigurationLoader.getParameters(false).getProperty("DATABASE").equalsIgnoreCase("ORACLE")) {
				if ("Y".equalsIgnoreCase(ConfigurationLoader.getParameters(false).getProperty("DDS_YN")))
					queryString = "select distinct A.id.prdCd ,A.name FROM D009021 A WHERE A.moduleType in (20, 47) and A.id.prdCd in (SELECT trim(substr(B.id.prdAcctId,0,8)) FROM D009022 B WHERE B.custNo=:cust and B.acctStat not in(3 ,4 ,5 ,97))";
				else
					queryString = "select distinct A.id.prdCd ,A.name FROM D009021 A WHERE A.moduleType=20 and A.id.prdCd in (SELECT trim(substr(B.id.prdAcctId,0,8)) FROM D009022 B WHERE B.custNo=:cust and B.acctStat not in(3 ,4 ,5 ,97))";
			} else {
				if ("Y".equalsIgnoreCase(ConfigurationLoader.getParameters(false).getProperty("DDS_YN")))
					queryString = "select distinct A.id.prdCd ,(Case when  A.moduleType = 47 then 'DDS Account' else Name end)as Name FROM D009021 A WHERE A.moduleType in (20, 47) and A.id.prdCd in (SELECT substring(B.id.prdAcctId,0,8) FROM D009022 B WHERE B.custNo=:cust and B.acctStat not in(3 ,4 ,5 ,97))";
				else
					queryString = "select distinct A.id.prdCd ,A.name FROM D009021 A WHERE A.moduleType=20 and A.id.prdCd in (SELECT substring(B.id.prdAcctId,0,8) FROM D009022 B WHERE B.custNo=:cust and B.acctStat not in(3 ,4 ,5 ,97))";
			}
			int lbrcode = (int) brCode;
			int custNo = Integer.parseInt(custno);
			Query q = session.createQuery(queryString).setParameter("cust", custNo);
			list = q.getResultList();
			// queryObject.setLong(4, noofdays);
			List<ProductResponse> productResp = new ArrayList<ProductResponse>();
			if (list != null && list.size() != 0) {
				for (Object[] prd : list) {
					ProductResponse data = new ProductResponse();
					data.setCode(prd[0].toString().trim());
					data.setName(prd[1].toString().trim());
					productResp.add(data);
				}
				List<ProductResponse> productResp1 = productResp.stream()
						.collect(Collectors.toCollection(
								() -> new TreeSet<>(java.util.Comparator.comparing(ProductResponse::getCode))))
						.stream().collect(Collectors.toList());

				custList.setProducts(productResp1);
				custList.setCustomerNo(custno);
				custList.setResponse("Sucess");
				custList.setErrorMessage("Sucess");
				custList.setErrorCode("00");
				t.commit();
				session.close();
				t = null;
				return custList;
			} else {
				custList.setCustomerNo(custno);
				custList.setResponse("Failed");
				custList.setErrorMessage("No Record Found");
				custList.setErrorCode("02");
				return custList;
			}

			// session = null;

		} catch (Exception sql) {
			custList.setCustomerNo(custno);
			custList.setResponse("Failed");
			custList.setErrorMessage("No Record Found");
			custList.setErrorCode("51");
			sql.printStackTrace();
		}
		return null;
	}

	public static List<Object[]> getRDAcctList(String custno, int acctNo) {

		int brCode;
		try (Session session = HBUtil.getSessionFactory().openSession()) {

			String queryString1 = "SELECT lbrCode from D009011 where custNo=" + custno;
			Query q1 = session.createQuery(queryString1);

			brCode = (int) q1.getSingleResult();

			List<Object[]> list = new ArrayList<Object[]>();
			String queryString = "";

			if (ConfigurationLoader.getParameters(false).getProperty("DATABASE").equalsIgnoreCase("ORACLE")) {
				queryString = "SELECT LBrCode , subStr(PrdAcctId,0,25),CAST(subString(PrdAcctId,25,32)AS INT) AS ReciptNo , NoInst, IntRate, InstOrPrincAmt, MatDate, MainBalFcy, ((InstOrPrincAmt*NoInst)-MainBalLcy)/InstOrPrincAmt \r\n"
						+ "AS PendingInst, LongName FROM D020004 WHERE LBrCode =" + brCode + "and MatDate > sysdate"
						+ " AND subStr(PrdAcctId,0,25) in (SELECT subStr(PrdAcctId,0,25) FROM D009022 WHERE \r\n"
						+ "LBrCode=" + brCode + " AND CustNo=" + custno
						+ " AND subStr(PrdAcctId,0,8) IN (SELECT PrdCd FROM D020002 WHERE InstOrPrinc='I' AND LBrCode ="
						+ brCode + ")) AND ReceiptStatus=51\r\n" + "";
				if (acctNo != 0) {
					queryString += " AND CAST(subString(PrdAcctId,17,8)AS INT) = " + acctNo;
				}
			} else {
				queryString = "SELECT LBrCode , subString(PrdAcctId,0,25),CAST(subString(PrdAcctId,25,32)AS INT) AS ReciptNo , NoInst, IntRate, InstOrPrincAmt, MatDate, MainBalFcy, ((InstOrPrincAmt*NoInst)-MainBalLcy)/InstOrPrincAmt \r\n"
						+ "AS PendingInst, LongName FROM D020004 WHERE LBrCode =" + brCode + " and MatDate > getdate()"
						+ " AND subString(PrdAcctId,0,25) in (SELECT subString(PrdAcctId,0,25) FROM D009022 WHERE \r\n"
						+ "LBrCode=" + brCode + " AND CustNo=" + custno
						+ " AND subString(PrdAcctId,0,8) IN (SELECT PrdCd FROM D020002 WHERE InstOrPrinc='I' AND LBrCode ="
						+ brCode + ")) AND ReceiptStatus=51\r\n" + "";
				if (acctNo != 0) {
					queryString += " AND CAST(subString(PrdAcctId,17,8)AS INT) = " + acctNo;
				}
			}
			short mtype = 20;
			int lbrcode = (int) brCode;
			Byte accstat = 1;
			Query q = session.createSQLQuery(queryString);
			list = q.getResultList();
			// queryObject.setLong(4, noofdays);

			if (list != null && list.size() != 0) {
				session.close();

				return list;
			} else {
				return null;
			}

			// session = null;

		} catch (Exception sql) {
			sql.printStackTrace();
		}
		return null;
	}

	public static PigmeAccountsResponse searchRDAcc(String accNo, String name, String depositAccNo) {
		PigmeAccountsResponse response = new PigmeAccountsResponse();
		ArrayList<PigmeAccountsResponse> accList = new ArrayList<>();
		Session session = HBUtil.getSessionFactory().openSession();
		try {
			System.out.println("RequestServiceImpl.searchPigMeAcc() " + accNo + " ~ " + name + " ~ " + depositAccNo);

			D009022 sourceAccount = DataUtils.getAccount(accNo.trim());
			if (sourceAccount == null) {
				System.out.println("Amar1");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
				return response;
			}
			TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount,
					String.valueOf("10"), "AGENT");
			System.out.println("res::>>>" + res);
			if (res != null && res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(res.getErrorMsg());
				return response;
			}

			Criteria criteria = session.createCriteria(D047003.class);
			criteria.add(Restrictions.eq("id.lbrCode", sourceAccount.getId().getLbrCode()));
			criteria.add(Restrictions.eq("agtPrdAcctId", sourceAccount.getId().getPrdAcctId()));
			// criteria.add(Restrictions.ne("id.acctStat",Byte.valueOf("3")));
			criteria.add(Restrictions.ne("acctStat", Byte.valueOf("3")));
			criteria.add(Restrictions.gt("matDate", new Date()));
			List<D047003> list = criteria.list();
			logger.error("list::>>>" + list);
			if (list == null || list.isEmpty()) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
				return response;
			}
			ArrayList<String> acList = new ArrayList<String>();
			ArrayList<String> acList2 = new ArrayList<String>();
			for (D047003 d047003 : list) {
				acList.add(d047003.getId().getCustPrdAcctId());
				acList2.add(d047003.getId().getCustPrdAcctId().substring(8));
			}
			String accNo32 = "";
			String product = "";
			if (depositAccNo != null && depositAccNo.trim().length() > 0) {
				if (ConfigurationLoader.getParameters(false).getProperty("D047001_MULTI_PRODUCT_YN")
						.equalsIgnoreCase(MSGConstants.YES))
					product = "00000000" + String.format("%08d", Long.valueOf(depositAccNo.trim())) + "00000000";
				else
					accNo32 = list.get(0).getId().getCustPrdAcctId().substring(0, 8) + "00000000"
							+ String.format("%08d", Long.valueOf(depositAccNo.trim())) + "00000000";
			}
			List<D047003> list5 = null;
			if (ConfigurationLoader.getParameters(false).getProperty("D047001_MULTI_PRODUCT_YN")
					.equalsIgnoreCase(MSGConstants.YES)) {
				Criteria criteria5 = session.createCriteria(D047003.class);
				criteria5.add(Restrictions.eq("id.lbrCode", sourceAccount.getId().getLbrCode()));
				criteria5.add(Restrictions.eq("agtPrdAcctId", sourceAccount.getId().getPrdAcctId()));
				criteria5.add(Restrictions.ilike("id.custPrdAcctId", "%" + product));
				criteria5.add(Restrictions.ne("acctStat", Byte.valueOf("3")));
				criteria5.add(Restrictions.gt("matDate", new Date()));
				list5 = criteria5.list();
			}
			if (ConfigurationLoader.getParameters(false).getProperty("D047001_MULTI_PRODUCT_YN")
					.equalsIgnoreCase(MSGConstants.NO)) {
				if (depositAccNo != null && depositAccNo.trim().length() > 0) {
					if (!acList.contains(accNo32)) {
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMsg(MSGConstants.INVALID_ACCOUNT_NO);
						return response;
					}

				}
			}
			if (ConfigurationLoader.getParameters(false).getProperty("D047001_MULTI_PRODUCT_YN")
					.equalsIgnoreCase(MSGConstants.YES)) {
				if (list5 == null || list5.isEmpty()) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.PRODUCT_NOT_FOUND);
					return response;
				}
				product = list5.get(0).getId().getCustPrdAcctId();
			}
			Criteria criteria2 = session.createCriteria(D009022.class);
			criteria2.add(Restrictions.eq("id.lbrCode", sourceAccount.getId().getLbrCode()));
			criteria2.add(Restrictions.in("id.prdAcctId", acList));
			criteria2.add(Restrictions.ilike("longName", "%" + name.trim().toUpperCase() + "%"));
			criteria2.add(Restrictions.in("acctStat",
					Arrays.asList(Byte.valueOf("1"), Byte.valueOf("2"), Byte.valueOf("12"))));
			logger.error("accNo32::>>" + accNo32);
			System.out.println("accNo32::>>" + accNo32);
			Criteria criteria3 = session.createCriteria(D009022.class);
			criteria3.add(Restrictions.eq("id.lbrCode", sourceAccount.getId().getLbrCode()));
			if (ConfigurationLoader.getParameters(false).getProperty("D047001_MULTI_PRODUCT_YN")
					.equalsIgnoreCase(MSGConstants.YES))
				criteria3.add(Restrictions.ilike("id.prdAcctId", "%" + product));
			else
				criteria3.add(Restrictions.eq("id.prdAcctId", accNo32));

			criteria3.add(Restrictions.in("acctStat",
					Arrays.asList(Byte.valueOf("1"), Byte.valueOf("2"), Byte.valueOf("12"))));

			List<D009022> listt = null;
			if (depositAccNo.trim().length() < 1)
				listt = criteria2.list();
			else
				listt = criteria3.list();
			if (listt != null || listt.size() > 0) {
				SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
				for (D009022 d009022 : listt) {
					PigmeAccountsResponse response2 = new PigmeAccountsResponse();
					response2.setAccNo(d009022.getId().getPrdAcctId());
					response2.setBrCode(d009022.getId().getLbrCode() + "");
					response2.setCustNo(d009022.getCustNo() + "");
					response2.setName(d009022.getLongName().trim());
					response2.setFormattedAcc(
							d009022.getId().getLbrCode() + "/" + d009022.getId().getPrdAcctId().substring(0, 8).trim()
									+ "/" + Long.valueOf(d009022.getId().getPrdAcctId().substring(16, 24)));
					response2.setBalance("" + Double.valueOf(Double.valueOf(d009022.getActClrBalFcy())));
					// added to give date of opening
					response2.setAccOpnDt(d009022.getDateOpen());
					response2.setAccOpnDtStr(simpleDateFormat.format(d009022.getDateOpen()));
					accList.add(response2);
					System.out.println("PigmeAccounts::>>" + response2.toString());
					response2 = null;
				}
			} else {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
				return response;
			}
			if (accList == null || accList.isEmpty()) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg("Account not present Or account status is not normal.");
				return response;
			}
			response.setResponse(MSGConstants.SUCCESS);
			response.setErrorMsg(MSGConstants.SUCCESS_MSG);
			response.setAccList(accList);
			return response;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			response.setAccList(accList);
			return response;
		} finally {
			session.close();
			session = null;
		}
	}

	public static List<Object[]> getRDAcctDetails(int brCode, String prdcode) {

		// int brCode;
		try (Session session = HBUtil.getSessionFactory().openSession()) {

			List<Object[]> list = new ArrayList<Object[]>();
			String queryString = "";

			if (ConfigurationLoader.getParameters(false).getProperty("DATABASE").equalsIgnoreCase("ORACLE"))
				queryString = "SELECT LBrCode , subString(PrdAcctId,0,25),CAST(subString(PrdAcctId,25,32)AS INT) AS ReciptNo , NoInst, IntRate, InstOrPrincAmt, MatDate, MainBalFcy, ((InstOrPrincAmt*NoInst)-MainBalLcy)/InstOrPrincAmt \r\n"
						+ "AS PendingInst FROM D020004 WHERE LBrCode =" + brCode
						+ " AND subStr(PrdAcctId,0,25) in (SELECT subStr(PrdAcctId,0,25) FROM D009022 WHERE \r\n"
						+ "LBrCode=" + brCode
						+ " AND subString(PrdAcctId,0,8) IN (SELECT PrdCd FROM D020002 WHERE InstOrPrinc='I' AND "
						+ "LBrCode =" + brCode + ")AND subString(PrdAcctId,0,25) = " + prdcode.substring(0, 24)
						+ ") AND ReceiptStatus=51\r\n" + "";
			else
				queryString = "SELECT LBrCode , subString(PrdAcctId,0,25),CAST(subString(PrdAcctId,25,32)AS INT) AS ReciptNo , NoInst, IntRate, InstOrPrincAmt, MatDate, MainBalFcy, ((InstOrPrincAmt*NoInst)-MainBalLcy)/InstOrPrincAmt \r\n"
						+ "AS PendingInst FROM D020004 WHERE LBrCode =" + brCode
						+ " AND subString(PrdAcctId,0,25) in (SELECT subString(PrdAcctId,0,25) FROM D009022 WHERE \r\n"
						+ "LBrCode=" + brCode
						+ " AND subString(PrdAcctId,0,8) IN (SELECT PrdCd FROM D020002 WHERE InstOrPrinc='I' AND LBrCode ="
						+ brCode + ") AND subString(PrdAcctId,0,25) = " + prdcode.substring(0, 24)
						+ ") AND ReceiptStatus=51\r\n" + "";
			short mtype = 20;
			int lbrcode = (int) brCode;
			Byte accstat = 1;
			Query q = session.createSQLQuery(queryString);
			list = q.getResultList();
			// queryObject.setLong(4, noofdays);

			if (list != null && list.size() != 0) {
				session.close();

				return list;
			} else {
				return null;
			}

			// session = null;

		} catch (Exception sql) {
			sql.printStackTrace();
		}
		return null;
	}

	public static IMPSFetchLoanAccountDetailsResponse loanAccountDetailsForRDCC(int lbrCode, String accNo)
			throws Exception {
		IMPSFetchLoanAccountDetailsResponse accountDetailsList = new IMPSFetchLoanAccountDetailsResponse();
		List<Object[]> loanList = DataUtils.getLoanAccountDetails(accNo, lbrCode);
		System.out.println("Loan Account List::>>>" + loanList);
		if (loanList == null) {
			logger.error("Loan account not found.");
			accountDetailsList.setResponse(MSGConstants.ERROR);
			accountDetailsList.setErrorMessage(MSGConstants.LOAN_ACCOUNT_NOT_FOUND);
			return accountDetailsList;
		}
		DecimalFormat format = new DecimalFormat("0.00");
		// String formatted = format.format(number);
		List<LoanAccountDetails> accountList = new ArrayList<LoanAccountDetails>();
		Object[] object = new Object[1];
		for (Iterator<Object[]> iterator = loanList.iterator(); iterator.hasNext();) {
			BigDecimal three, four, seven, eigth;

			object = iterator.next();
			// Byte status= (Byte) object[7];
			String one = (String) object[0];
			String two = (String) object[1];
			if (!ConfigurationLoader.getParameters(false).getProperty("DATABASE").equalsIgnoreCase("ORACLE")) {
				three = BigDecimal.valueOf((Double) object[2]);
				four = BigDecimal.valueOf((Double) object[3]);
				seven = BigDecimal.valueOf((Double) object[6]);
			} else {
				three = (BigDecimal) object[2];
				four = (BigDecimal) object[3];
				seven = (BigDecimal) object[6];
			}
			Date five = (Date) object[4];
			BigDecimal six = BigDecimal.valueOf(Long.valueOf(object[5] + ""));
			LoanAccountDetails acc = new LoanAccountDetails();
			if (null == three)
				three = new BigDecimal(0);
			acc.setTotalSansactionLimit(format.format(three.doubleValue()));
			if (null == four)
				four = new BigDecimal(0);
			acc.setInstlAmt(format.format(four.doubleValue()));
			acc.setExpDate(DateUtil.getStringDateNew(five));
			acc.setCustomerno(six.longValue());
			acc.setLongName(two);
			acc.setAccountNo(one);
			acc.setAccStatus(1L);
			if (null == seven)
				seven = new BigDecimal(0);
			acc.setBalance(format.format(Double.valueOf(seven + "")));
			System.out.println("ExpDate::>>" + DateUtil.getString(five));
			accountList.add(acc);
		}
		accountDetailsList.setResponse(MSGConstants.SUCCESS);
		accountDetailsList.setErrorCode("00");
		accountDetailsList.setErrorMessage(MSGConstants.SUCCESS_MSG);
		accountDetailsList.setAccountList(accountList);
		return accountDetailsList;
	}

	public static IMPSFetchDepositeReceiptResponse fetchDDSAccReceipt(int lbrCode, String accNo) {
		IMPSFetchDepositeReceiptResponse receiptDetailsList = new IMPSFetchDepositeReceiptResponse();
		List<D047003> receiptList = DataUtils.getDDSReceiptDetails(lbrCode, accNo);
		logger.error("receiptList::>>>" + receiptList);
		if (null == receiptList) {
			logger.error("No Receipts found.");
			receiptDetailsList.setResponse(MSGConstants.ERROR);
			receiptDetailsList.setErrorMessage(MSGConstants.NO_RECIEPT_FOUND);
			return receiptDetailsList;
		}
		List<DepositeReceiptDetals> receiptLists = new ArrayList<DepositeReceiptDetals>();
		for (D047003 termDepositReceipts : receiptList) {
			Session session = HBUtil.getSessionFactory().openSession();
			D009022 ddsAcct = session.get(D009022.class, new D009022Id(termDepositReceipts.getId().getLbrCode(),
					termDepositReceipts.getId().getCustPrdAcctId().trim()));

			DepositeReceiptDetals depositeRec = new DepositeReceiptDetals();
			depositeRec
					.setReceiptNo(SwiftCoreUtil.getAccountNoFormatted(termDepositReceipts.getId().getCustPrdAcctId()));
			depositeRec.setBalance(ddsAcct.getActTotBalFcy());

			depositeRec.setDateOfReceipt(termDepositReceipts.getOpenDate());
			depositeRec.setInterestRate(termDepositReceipts.getIntRate());
			depositeRec.setMaturityAmt(termDepositReceipts.getMatAmount());
			depositeRec.setMaturityDate(termDepositReceipts.getMatDate());
			//depositeRec.setLienAmount(termDepositReceipts.getDepositAmt());
			depositeRec.setLienAmount(ddsAcct.getFlexiLienBal());
			depositeRec.setAsOfDate(new Date());
			receiptLists.add(depositeRec);
			session.close();
		}
		receiptDetailsList.setResponse(MSGConstants.SUCCESS);
		receiptDetailsList.setErrorCode(ResponseCodes.SUCCESS);
		receiptDetailsList.setErrorMessage(MSGConstants.SUCCESS_MSG);
		receiptDetailsList.setReceiptList(receiptLists);
		return receiptDetailsList;
	}
}
