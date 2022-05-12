package com.sil.commonswitch;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
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
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.hibernate.transform.Transformers;
import org.pmw.tinylog.Logger;
import com.sil.constants.Code;
import com.sil.constants.MSGConstants;
import com.sil.constants.ResponseCodes;
import com.sil.constants.SwiftCoreConstants;
import com.sil.dao.LookUpServiceImpl;
import com.sil.dao.RTGSNEFTServiceImpl;
import com.sil.dao.SwiftCoreUtil;
import com.sil.domain.ATMTransactionRequest;
import com.sil.domain.Accountloadreq;
import com.sil.domain.Accountloadres;
import com.sil.domain.BranchDetailsResponse;
import com.sil.domain.Channelpartnerloginreq;
import com.sil.domain.CustomerDetails;
import com.sil.domain.CustomerPhysicalCardOnboardingreq;
import com.sil.domain.CustomerPhysicalCardOnboardingres;
import com.sil.domain.Header;
import com.sil.domain.IMPSTransactionRequest;
import com.sil.domain.IMPSTransactionResponse;
import com.sil.domain.ImpsTransactionReport;
import com.sil.domain.PigmeAccountsResponse;
import com.sil.domain.RtgsMsgFieldRows;
import com.sil.domain.TransactionValidationResponse;
import com.sil.domain.WSUtils;
import com.sil.hbm.*;
import com.sil.prop.ConfigurationLoader;
import com.sil.service.TermDepositeServiceImpl;
import com.sil.service.TransactionServiceImpl;
import com.sil.util.Account;
import com.sil.util.AcctDetailsPojo;
import com.sil.util.Customer;
import com.sil.util.DateUtil;
import com.sil.util.DateUtility;
import com.sil.util.EnglishNumberToWords;
import com.sil.util.HBUtil;

public class DataUtils {
	public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(DataUtils.class);
	public DecimalFormat twoDecPlaces = new DecimalFormat("#.##");
	private static final SimpleDateFormat sdf = new SimpleDateFormat("0yyyyMMdd");
	private static final Customerdetails customerdetails = null;
	public DecimalFormat fourDecPlaces = new DecimalFormat("#.####");
	public static String ip = ConfigurationLoader.getParameters(false).getProperty("CBS_IP");
	public static int port = Integer.valueOf(ConfigurationLoader.getParameters(false).getProperty("PORT"));

	public static D009022 getAccount(String prdAcctId) {
		logger.error("In GetAcc---");
		String branch = prdAcctId.substring(0, 3);
		if(branch.equalsIgnoreCase("999"))
			branch = "9999";
		int brCode = Integer.parseInt(branch);
		String acct32 = DataUtils.get32DigitAcctNo(prdAcctId);
		logger.error("acct32::>>>" + acct32);
		if (acct32 == null)
			return null;
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		D009022Id id = new D009022Id();
		id.setLbrCode(brCode);
		id.setPrdAcctId(acct32);
		D009022 bal = session.get(D009022.class, id);
		tx.commit();
		session.close();
		return bal;
	}

	public static D009022 getAccount(String prdAcctId, Session session) {
		try {
			int brCode = Integer.parseInt(prdAcctId.substring(0, 3));
			String acct32 = DataUtils.get32DigitAcctNo(prdAcctId);
			logger.error("acct32::>>>" + acct32);
			if (acct32 == null)
				return null;
			D009022Id id = new D009022Id();
			id.setLbrCode(brCode);
			id.setPrdAcctId(acct32);
			D009022 bal = session.get(D009022.class, id);
			return bal;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("ERROR:", e);
			return null;
		}

	}

	public static D009022 validateAccountCustNo(String prdAcctId, String custNo) {
		int brCode = Integer.parseInt(prdAcctId.substring(0, 3));
		String acct32 = DataUtils.get32DigitAcctNo(prdAcctId);
		if (acct32 == null)
			return null;
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D009022.class);
		criteria.add(Restrictions.eq("id.lbrCode", brCode));
		criteria.add(Restrictions.eq("id.prdAcctId", acct32));
		criteria.add(Restrictions.eq("custNo", Integer.valueOf(custNo.trim())));
		List<D009022> list = criteria.list();
		session.close();
		session = null;
		if (list == null || list.isEmpty())
			return null;
		else
			return list.get(0);
	}

	public static D009022 getAccountDetails(int brCode, String acct32) {
		if (acct32 == null)
			return null;
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		D009022Id id = new D009022Id();
		id.setLbrCode(brCode);
		id.setPrdAcctId(acct32);
		D009022 bal = session.get(D009022.class, id);
		tx.commit();
		session.close();
		return bal;
	}

	@SuppressWarnings("deprecation")
	public static String get32DigitAcctNo(String acctno15digit) {
		if(acctno15digit.length()==16)
			acctno15digit = acctno15digit.substring(1);
		String pCode = acctno15digit.substring(3, 7);
		logger.error("pCode::>>" + pCode);
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		D001002 code = (D001002) session.createCriteria(D001002.class).add(Restrictions.eq("id.codeType", 11080))
				.add(Restrictions.eq("codeDesc", pCode.trim())).uniqueResult();
		tx.commit();
		session.close();
		logger.error("---In 32 digit");
		if (code == null) {
			return null;
		} else
			logger.error("Product Code : " + code.getId().getCode());
		
		String productCode = code.getId().getCode();
		if(productCode.length()>8)
			productCode=productCode.substring(0, 8);
		String accno = acctno15digit.substring(7);
		String acc32 = String.format("%-8s", productCode) + "00000000" + accno + "00000000";
		logger.error("Acct32 : " + acc32);
		return acc32;
	}

	public static String get15DigitAccountNumber(int brCode, String acc32) {
		String lbrCode = String.format("%03d", brCode);
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		D001002Id id = new D001002Id();
		id.setCodeType(11080);
		logger.error("Code : " + acc32.substring(0, 8).trim());
		id.setCode(acc32.substring(0, 8).trim());
		D001002 code = session.get(D001002.class, id);
		tx.commit();
		session.close();
		
		if(lbrCode.equalsIgnoreCase("9999")) {
			return "999" + code.getCodeDesc().substring(0, 4) + acc32.substring(16, 24);
		}else
			return lbrCode + code.getCodeDesc().substring(0, 4) + acc32.substring(16, 24);
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	public static List<D009040> getMinistatement(int lbrCode, String prdAcctId, int noofstmts) {
		logger.error("LObrCode : " + lbrCode);
		logger.error("PrdAcctID : " + prdAcctId);
		logger.error("NoofRec : " + noofstmts);
		logger.error("Date : " + new Date());
		List<D009040> lst;
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D009040.class);
		criteria.add(Restrictions.eq("vcrAcctId", prdAcctId));
		criteria.add(Restrictions.eq("id.lbrCode", lbrCode));
		criteria.add(Restrictions.ne("canceledFlag", 'C'));
		criteria.add(Restrictions.le("postDate", new Date()));

		criteria.addOrder(Order.desc("postDate"));
		criteria.addOrder(Order.desc("postTime"));
		criteria.setMaxResults(noofstmts);
		lst = criteria.list();
		session.close();
		return lst;
	}

	public static D009044 getChequeStatus(int lbr, String accNo, String chequeNo) {
		Session session = HBUtil.getSessionFactory().openSession();
		List<D009044> lst;
		Criteria criteria = session.createCriteria(D009044.class);
		criteria.add(Restrictions.eq("id.lbrCode", lbr));
		criteria.add(Restrictions.eq("id.issuedTo", accNo));
		criteria.add(Restrictions.eq("id.instruNo", chequeNo));
		lst = criteria.list();
		logger.error("lst.size()::>>>" + lst.size());
		session.close();
		session = null;
		if (lst.size() == 0)
			return null;
		return lst.get(0);
	}

	@SuppressWarnings({ "deprecation", "unused" })
	public static String updateMobileNo(String acctNo, String mobNo) {
		int brCode = Integer.parseInt(acctNo.substring(0, 3));
		String acc32 = get32DigitAcctNo(acctNo);
		Session session = HBUtil.getSessionFactory().openSession();
		D009022 d22 = getAccount(acctNo);

		if (d22 != null) {
			int custNo = d22.getCustNo();
			Transaction tx = session.beginTransaction();
			D350078 d78 = (D350078) session.createCriteria(D350078.class).add(Restrictions.eq("id.custNo", "" + custNo))
					.uniqueResult();
			tx.commit();
			if (d78 != null) {
				tx = session.beginTransaction();
				int status = session.createQuery("update D350078 set MobileNo = '" + mobNo + "' where MobileNo = '"
						+ d78.getId().getMobileNo().trim() + "' and CustNo = '" + custNo + "'").executeUpdate();
				tx.commit();
				session.close();
				return "00";

			} else {
				session.close();
				return "10"; // Record in 78 Not Found
			}
		} else {
			session.close();
			return "09";
		}

	}

	@SuppressWarnings("deprecation")
	public static String isValidIBCustomer(String custNo) {
		Session session = HBUtil.getSessionFactory().openSession();
		try {
			Transaction tx = session.beginTransaction();
			D350076 d76 = session.get(D350076.class, custNo);
			tx.commit();
			if (d76 == null)
				return "09";

			tx = session.beginTransaction();
			D350077 d77 = (D350077) session.createCriteria(D350077.class).add(Restrictions.eq("id.custNo", custNo))
					.setMaxResults(1).uniqueResult();
			tx.commit();
			if (d77 == null)
				return "09";

			tx = session.beginTransaction();
			D002011 d11 = session.get(D002011.class, custNo);
			tx.commit();

			if (d11 == null) {
				return "09";
			} else {
				if (d11.getStatus() == 1) {
					return "00";
				} else if (d11.getStatus() == 7) {
					return "07";
				} else {
					return "01";
				}
			}
		} finally {
			session.close();
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
		logger.error("D77 0 : " + d22);

		if (d22 == null) {
			return null;
		} else {
			return d22;
		}
	}

	public static List<D009022> getAccountsFromCustNo(String custNo) {
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		List<D009022> list = session.createCriteria(D009022.class)
				.add(Restrictions.eq("custNo", Integer.parseInt(custNo.trim()))).list();
		tx.commit();
		session.close();
		session = null;
		if (list == null)
			return null;
		else
			return list;
	}

	public static List<D009022> getAccountsFromAccNo(String accNo, int brCode) {
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D009022.class);
		criteria.add(Restrictions.eq("id.lbrCode", brCode));
		String formatedAccNo = "00000000" + String.format("%08d", Long.valueOf(accNo)) + "00000000";
		logger.error(formatedAccNo);
		criteria.add(Restrictions.ilike("id.prdAcctId", "%" + formatedAccNo + "%"));
		List<D009022> list = criteria.list();
		session.close();
		session = null;
		if (list == null)
			return null;
		else
			return list;
	}

	public static List<D009022> getAccountsFromName(String name, int brCode) {
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D009022.class);
		criteria.add(Restrictions.eq("id.lbrCode", brCode));
		criteria.add(Restrictions.ilike("longName", "%" + name.trim() + "%"));
		criteria.add(Restrictions.ne("acctStat", Byte.valueOf("3")));
		List<D009022> list = criteria.list();
		session.close();
		session = null;
		if (list == null || list.isEmpty())
			return null;
		else
			return list;
	}

	@SuppressWarnings({ "deprecation", "rawtypes" })
	public static AcctDetailsPojo getAcctDetails(int lbrCode, String acctNo32Digit) {
		// String acctno32digit=get32DigitAcctNo(acctNo15Digit);

		// int lbrcode=Integer.parseInt(acctNo15Digit.substring(0, 3));

		AcctDetailsPojo result = null;
		Session session = HBUtil.getSessionFactory().openSession();

		String hql = "FROM D009022 A, D009011 B WHERE A.custNo = B.custNo AND A.id.prdAcctId = '" + acctNo32Digit
				+ "' AND A.id.lbrCode=" + lbrCode + "";
		// String hql = "FROM D009022 A, D009011 B WHERE A.custNo = B.custNo AND
		// A.id.prdAcctId = '"+acctno32digit+"' AND A.id.lbrCode="+lbrcode;
		Query query = session.createQuery(hql);

		List<?> list = query.list();
		session.close();
		int size = list.size();
		if (size > 0) {

			Object[] objects = (Object[]) list.get(0);
			D009022 d22 = (D009022) objects[0];
			D009011 d11 = (D009011) objects[1];
			result = new AcctDetailsPojo(d22.getId().getLbrCode(), d22.getId().getPrdAcctId(), d22.getLongName(),
					d22.getCustNo(), d11.getPanNoDesc(), d11.getAdd1(), d11.getAdd2(), d11.getAdd3(), d11.getPinCode(),
					"", d22.getUnClrEffFcy(), (d22.getActClrBalFcy() - d22.getTotalLienFcy()), d22.getAcctStat());
		} else {
			return null;
		}
		return result;

	}

	@SuppressWarnings("deprecation")
	public static List<D009022> getAccountFormMobile(String MobileNo) {
		try (Session session = HBUtil.getSessionFactory().openSession()) {
			Transaction tx = session.beginTransaction();
			D350078 d78 = (D350078) session.createCriteria(D350078.class)
					.add(Restrictions.ilike("id.mobileNo", "%" + MobileNo + "%")).setMaxResults(1).uniqueResult();
			tx.commit();
			if (d78 == null)
				return null;
			else {
				tx = session.beginTransaction();
				List<D350077> d77 = (List<D350077>) session.createCriteria(D350077.class)
						.add(Restrictions.eq("id.custNo", d78.getId().getCustNo())).setMaxResults(3).list();
				tx.commit();
				List<D009022> list = new ArrayList<>();
				String msg = "";
				if (d77 == null)
					return null;
				else {
					for (int i = 0; i < d77.size(); i++) {
						D009022Id id = new D009022Id();
						id.setLbrCode(d77.get(i).getId().getBrCode());
						id.setPrdAcctId(d77.get(i).getId().getAcctNo());
						D009022 d22 = session.get(D009022.class, id);
						if (msg.equalsIgnoreCase(""))
							msg = msg + "Balance in A/C " + Integer.valueOf(d22.getId().getLbrCode()) + "/"
									+ d22.getId().getPrdAcctId().substring(0, 8).trim() + "/"
									+ d22.getId().getPrdAcctId().substring(d22.getId().getPrdAcctId().length() - 16,
											d22.getId().getPrdAcctId().length() - 8)
									+ " is " + d22.getActClrBalFcy() + " CR";
						else
							msg = msg + " ,Balance in A/C " + Integer.valueOf(d22.getId().getLbrCode()) + "/"
									+ d22.getId().getPrdAcctId().substring(0, 8).trim() + "/"
									+ d22.getId().getPrdAcctId().substring(d22.getId().getPrdAcctId().length() - 16,
											d22.getId().getPrdAcctId().length() - 8)
									+ " is " + d22.getActClrBalFcy() + " CR";

						list.add(d22);
					}
					logger.error("" + msg);
					session.close();
					tx = null;
					return list;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getAccountFormMobileNo(String MobileNo) {
		String msg = "";
		try (Session session = HBUtil.getSessionFactory().openSession()) {
			Transaction tx = session.beginTransaction();
			D350078 d78 = (D350078) session.createCriteria(D350078.class)
					.add(Restrictions.ilike("id.mobileNo", "%" + MobileNo + "%")).setMaxResults(1).uniqueResult();
			tx.commit();
			if (d78 == null)
				return "Account no found for mobile number " + MobileNo + ".";
			else {
				tx = session.beginTransaction();
				tx.commit();
				List<D009022> list = getValidAccListIVR(String.valueOf(d78.getId().getCustNo()));
				logger.error("list.size:>>" + list.size());
				int len = 0;
				if (list != null && list.size() > 0)
					len = list.size() > 3 ? 3 : list.size();
				String drcr = "CR";
				if (list == null)
					return null;
				else {
					for (int i = 0; i < len; i++) {
						if (list.get(i).getActClrBalFcy() < 0.0d)
							drcr = "DR";
						else
							drcr = "CR";
						if (msg.equalsIgnoreCase("")) {
							msg = msg + "Balance in A/C:" + "XXXX"
									+ Long.valueOf(list.get(i).getId().getPrdAcctId().substring(
											list.get(i).getId().getPrdAcctId().length() - 16,
											list.get(i).getId().getPrdAcctId().length() - 8))
									+ " is " + list.get(i).getActClrBalFcy() + " " + drcr;

						} else {
							msg = msg + ", Balance in A/C: XXXX"
									+ Long.valueOf(list.get(i).getId().getPrdAcctId().substring(
											list.get(i).getId().getPrdAcctId().length() - 16,
											list.get(i).getId().getPrdAcctId().length() - 8))
									+ " is " + list.get(i).getActClrBalFcy() + " " + drcr;
						}
					}
					session.close();
					tx = null;
				}
				return msg;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "An error has occured during balance fetching, kindly retry after some time.";
		}
	}

	/*
	 * public static AccountDetails getAccoutDetails(String custNo) {
	 * com.sil.commonswitch.util.AccountDetails accountDetails = new
	 * com.sil.commonswitch.util.AccountDetails(); try (Session session =
	 * HBUtil.getSessionFactory().openSession()){ Transaction tx =
	 * session.beginTransaction(); D009011 d11 = session.get(D009011.class,
	 * Integer.parseInt(custNo)); tx.commit();
	 * 
	 * if(d11 == null){ logger.error("Customer in D009011 not found"); return null;
	 * } else{ accountDetails.setCustno(custNo);
	 * accountDetails.setAddr1(d11.getAdd1());
	 * accountDetails.setAddr2(d11.getAdd2());
	 * accountDetails.setAddr3(d11.getAdd3());
	 * accountDetails.setName(d11.getLongname());
	 * accountDetails.setPan(d11.getPanNoDesc());
	 * accountDetails.setPinCode(d11.getPinCode());
	 * 
	 * tx= session.beginTransaction(); List<D009022> d22List =
	 * session.createCriteria(D009022.class).add(Restrictions.eq("custNo",
	 * custNo)).list(); tx.commit(); logger.error(); if()
	 * 
	 * }
	 * 
	 * } catch (Exception e) { // TODO: handle exception }
	 * 
	 * 
	 * 
	 * return null; }
	 */

	@SuppressWarnings({ "unchecked", "deprecation" })
	public static Customer getCustomer(String custNo) {
		Customer customer = new Customer();
		try (Session session = HBUtil.getSessionFactory().openSession()) {
			Transaction tx = session.beginTransaction();
			D009011 d11 = session.get(D009011.class, Integer.parseInt(custNo));
			tx.commit();

			if (d11 == null) {
				session.close();

				logger.error("Customer in D009011 not found");
				return null;
			} else {
				customer.setCustno(custNo);
				customer.setAddr1(d11.getAdd1());
				customer.setAddr2(d11.getAdd2());
				customer.setAddr3(d11.getAdd3());
				customer.setName(d11.getLongname());
				customer.setPan(d11.getPanNoDesc());
				customer.setPinCode(d11.getPinCode());
				customer.setTdsYn(d11.getTdsYn());
				customer.setTdsPercentage(d11.getTdsPercentage());
				
				tx = session.beginTransaction();
				List<D009022> d22List = session.createCriteria(D009022.class)
						.add(Restrictions.eq("custNo", Integer.parseInt(custNo))).list();
				tx.commit();
				logger.error("No of Accounts of Customer " + custNo + " is " + d22List.size());

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
				}
				customer.setAccounts(accList);
				tx = session.beginTransaction();
				List<D350078> d78List = session.createCriteria(D350078.class).add(Restrictions.eq("id.custNo", custNo))
						.list();
				tx.commit();
				session.close();
				logger.error("Mobiles List Size : " + d78List.size());
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

	@SuppressWarnings({ "unused", "deprecation" })
	public static D350078 getCustomerDetails(String accNo15digit) {
		D009022 d009022 = getAccount(accNo15digit);
		logger.error("d009022.getCustNo():>>" + d009022.getCustNo());
		if (d009022 == null)
			return null;

		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		D350078Id id = new D350078Id();
		id.setCustNo("" + d009022.getCustNo());
		D350078 d78 = (D350078) session.createCriteria(D350078.class)
				.add(Restrictions.eq("id.custNo", "" + d009022.getCustNo())).uniqueResult();

		tx.commit();
		session.close();
		return d78;
	}

	public static List<D350078> getCustomerListD350078(String custNo) {
		Session session = HBUtil.getSessionFactory().openSession();
		List<D350078> d78 = session.createCriteria(D350078.class).add(Restrictions.eq("id.custNo", custNo)).list();
		session.close();
		session = null;
		return d78;
	}

	public static void P2PTransact() {
		D350036 d36 = new D350036();
		D350036Id id = new D350036Id();

		id.setBatchCd("IMPS");
		id.setEntryDate(new Date());
		id.setEntryTime(new Date());
		id.setLbrCode(1);
		id.setScrollNo(1234);
		id.setSetNo(5678);
		d36.setId(id);

		d36.setDrcr("D");
		d36.setMerchMessage("Hi Hello");
		d36.setMerchRespCd("00");
		d36.setMmid1("9876543");
		d36.setMmid2("9876542");
		d36.setMobNo1("8983290664");
		d36.setMobNo2("8055543246");
		d36.setResponseCd("00");
		d36.setResponseDesc("Hello Again");
		d36.setRrnNo("1234556789121");
		d36.setStan("123456");
		d36.setTranAmt(100.00);
		d36.setTransactionDate(new Date());

		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		session.save(d36);
		tx.commit();
		session.close();

	}

	public static void P2ATransact() {
		D350037 d37 = new D350037();
		D350037Id id = new D350037Id();
		id.setBatchCd("IMPS");
		id.setEntryDate(new Date());
		id.setEntryTime(new Date());
		id.setLbrCode(1);
		id.setScrollNo(1234);
		id.setSetNo(5678);
		d37.setId(id);
		d37.setDrcr("D");
		d37.setMmid1("9876543");
		d37.setIfscCd("012345678912");
		d37.setMobNo1("8983290664");
		d37.setAccNo("4135080750032932");
		d37.setResponseCd("00");
		d37.setResponseDesc("Hello Again");
		d37.setRrnNo("1234556789121");
		d37.setStan("123456");
		d37.setTranAmt(100.00);
		d37.setTransactionDate(new Date());
		d37.setRrsponseCd("00");

		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		session.save(d37);
		tx.commit();
		session.close();
	}

	public static String getNextRRN() {
		String stan = "";
		DateFormat dateTime = new SimpleDateFormat("HHmmss");
		Date date = new Date();
		stan = dateTime.format(date).toString();
		Calendar cal = Calendar.getInstance();
		String julian = DateUtil.getJulianDateForCurrentDate1();
		julian = String.format("%03d", new Object[] { Integer.valueOf(Integer.parseInt(julian)) });
		int year = Calendar.getInstance().get(1) % 10;
		String rrnNo = "";
		if (ConfigurationLoader.getParameters(false).getProperty("SQL_SERVER_VERSION").equalsIgnoreCase("2008")) {
			Session session = HBUtil.getSessionFactory().openSession();
			Transaction t = session.beginTransaction();
			try {
				D001004Id id = new D001004Id();
				id.setCode(MSGConstants.RRNNOSEQ);
				D001004 d001004 = session.get(D001004.class, id);
				if (d001004 == null)
					return year + julian + String.format("%08d", 1);
				;
				d001004.setValue(String.valueOf(Integer.valueOf(d001004.getValue().trim()) + 1));
				session.update(d001004);
				t.commit();
				return year + julian + String.format("%08d", Long.valueOf(d001004.getValue().trim()));
			} catch (Exception e) {
				// TODO: handle exception
				session.close();
				session = null;
				t = null;
				e.printStackTrace();
				return year + julian + String.format("%08d", 1);
			} finally {
				session.close();
				session = null;
				t = null;
			}

		}
		rrnNo = year + julian + String.format("%08d", getNextRRNNo());
		return rrnNo;
	}

	public static String getNextRRN(Session session) {
		String stan = "";
		DateFormat dateTime = new SimpleDateFormat("HHmmss");
		Date date = new Date();
		stan = dateTime.format(date).toString();
		Calendar cal = Calendar.getInstance();
		String julian = DateUtil.getJulianDateForCurrentDate1();
		julian = String.format("%03d", new Object[] { Integer.valueOf(Integer.parseInt(julian)) });
		int year = Calendar.getInstance().get(1) % 10;
		String rrnNo = "";
		if (ConfigurationLoader.getParameters(false).getProperty("SQL_SERVER_VERSION").equalsIgnoreCase("2008")) {
			try {
				D001004Id id = new D001004Id();
				id.setCode(MSGConstants.RRNNOSEQ);
				D001004 d001004 = session.get(D001004.class, id);
				if (d001004 == null)
					return year + julian + String.format("%08d", 1);
				;
				d001004.setValue(String.valueOf(Integer.valueOf(d001004.getValue().trim()) + 1));
				session.update(d001004);
				return year + julian + String.format("%08d", Long.valueOf(d001004.getValue().trim()));
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return year + julian + String.format("%08d", 1);
			}

		}
		rrnNo = year + julian + String.format("%08d", getNextRRNNo());
		return rrnNo;
	}

	public static long getNextRRNNo() {
		
		
		RRNNOSEQ setSeq = new RRNNOSEQ();
		try (Session session = HBUtil.getSessionFactory().openSession()) {
			session.save(setSeq);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return setSeq.getId();
		
	}
	
	/*
	 * public static String getNextRRN(Session session) { String stan = "";
	 * Transaction t = session.beginTransaction(); Criteria criteria =
	 * session.createCriteria(D001005.class);
	 * criteria.add(Restrictions.eq("id.lbrCode", 0));
	 * criteria.add(Restrictions.eq("id.catType", MSGConstants.RRNNO));
	 * criteria.add(Restrictions.eq("id.lnodate", DateUtil.getCurrentDate()));
	 * List<D001005> list = criteria.list(); if (list == null || list.size() == 0) {
	 * logger.error("size::>" + list.size()); logger.error(); D001005
	 * d001005 = new D001005(); D001005Id id = new D001005Id(); id.setCat('0');
	 * id.setCatType(MSGConstants.RRNNO); id.setCode1(" "); id.setCode2(" ");
	 * id.setLbrCode(0); id.setLnodate(DateUtil.getCurrentDate());
	 * 
	 * d001005.setId(id); d001005.setDbtrAddCb(0); d001005.setDbtrAddCd(new Date());
	 * d001005.setDbtrAddCk(0); d001005.setDbtrAddCs(Short.valueOf("0"));
	 * d001005.setDbtrAddCt(new Date()); d001005.setDbtrAddMb(0);
	 * d001005.setDbtrAddMd(new Date()); d001005.setDbtrAddMk(0);
	 * d001005.setDbtrAddMs(Short.valueOf("0")); d001005.setDbtrAddMt(new Date());
	 * d001005.setDbtrAuthDone(Byte.valueOf("1"));
	 * d001005.setDbtrAuthNeeded(Byte.valueOf("0")); d001005.setDbtrLhisTrnNo(0);
	 * d001005.setDbtrLupdCb(0); d001005.setDbtrLupdCd(new Date());
	 * d001005.setDbtrLupdCk(0); d001005.setDbtrLupdCs(Short.valueOf("0"));
	 * d001005.setDbtrLupdCt(new Date()); d001005.setDbtrLupdMb(0);
	 * d001005.setLastNo(1); d001005.setDescr(" ");
	 * d001005.setDbtrUpdtChkId(Short.valueOf("0"));
	 * d001005.setDbtrTauthDone(Short.valueOf("1"));
	 * d001005.setDbtrRecStat(Byte.valueOf("0")); d001005.setDbtrLupdMt(new Date());
	 * d001005.setDbtrLupdMs(Short.valueOf("0")); d001005.setDbtrLupdMk(0);
	 * d001005.setDbtrLupdMd(new Date()); stan = "1"; session.saveOrUpdate(d001005);
	 * t.commit(); session.close(); session = null; t = null; d001005 = null; id =
	 * null; } else { stan = String.valueOf(1 + list.get(0).getLastNo());
	 * Query<D001005> q = session.createQuery(
	 * "UPDATE D001005 SET LastNo =? WHERE LBrCode =? AND Cat = '0' AND CatType =? AND  Lnodate =?"
	 * ); q.setParameter(0, Integer.valueOf(stan)); q.setParameter(1, 0);
	 * q.setParameter(2, MSGConstants.RRNNO); q.setParameter(3,
	 * DateUtil.getCurrentDate()); q.executeUpdate(); t.commit(); session.close();
	 * session = null; t = null; } logger.error(); stan =
	 * String.format("%06d", Long.valueOf(stan)); Calendar cal =
	 * Calendar.getInstance(); String hour = String.format("%02d",
	 * cal.get(Calendar.HOUR_OF_DAY)); String julian =
	 * DateUtil.getJulianDateForCurrentDate1(); julian = String.format("%03d",
	 * Integer.parseInt(julian)); int year =
	 * Calendar.getInstance().get(Calendar.YEAR) % 10; String rrnNo = year + julian
	 * + hour + stan; return rrnNo; }
	 */

	public static String getNextRandomRRN() {
		String stan = "";
		DateFormat dateTime = new SimpleDateFormat("ddMMyymmssSSS");
		Date date = new Date();
		stan = dateTime.format(date).toString();
		logger.error("stan::>>" + stan);
		Calendar cal = Calendar.getInstance();
		String hour = String.format("%02d", cal.get(Calendar.HOUR_OF_DAY));
		String julian = DateUtil.getJulianDateForCurrentDate1();
		julian = String.format("%03d", Integer.parseInt(julian));
		int year = Calendar.getInstance().get(Calendar.YEAR) % 10;
		String rrnNo = year + julian + hour + stan;
		return rrnNo;
	}

	public static D009021 getProductMaster(String lbrCode, String prodCode) {
		logger.error("lbrCode::>>" + lbrCode);
		logger.error("prodCode::>>>" + prodCode);
		Session session = HBUtil.getSessionFactory().openSession();
		D009021Id id = new D009021Id();
		id.setLbrCode(Integer.valueOf(lbrCode));
		id.setPrdCd(prodCode);
		D009021 list = session.get(D009021.class, id);
		id = null;
		session.close();
		session = null;
		return list;
	}

	public static boolean isLoanAccount(int lbrCode, String prodCode, Session session) {
		logger.error("lbrCode::>>" + lbrCode);
		logger.error("prodCode::>>>" + prodCode);
		D009021Id id = new D009021Id();
		id.setLbrCode(Integer.valueOf(lbrCode));
		id.setPrdCd(prodCode);
		D009021 list = session.get(D009021.class, id);
		id = null;
		logger.error("list::>>" + list);
		if (list == null)
			return false;
		if (MSGConstants.LOAN_TYPE_LIST.contains(Long.valueOf(list.getModuleType() + "")))
			return true;
		else
			return false;
	}

	public static String getProductCode(String accNo) {
		if (accNo != null && accNo.length() > 0)
			return accNo.substring(0, 8).trim();
		return null;
	}

	public static List<D009022> getValidAccList(String custNo) {
		List<D009022> list = getAccountsFromCustNo(custNo);
		logger.error(list.size());
		List<D009022> accList = new ArrayList<>();

		for (int count = 0; count < list.size(); count++) {
			String productCode = DataUtils.getProductCode(list.get(count).getId().getPrdAcctId());
			logger.error(productCode);
			D009021 productMaster = DataUtils.getProductMaster(String.valueOf(list.get(count).getId().getLbrCode()),
					productCode);
			if (productMaster != null) {
				if (!MSGConstants.MODULE_TYPE_LIST.contains(Long.valueOf(productMaster.getModuleType()))) {
					continue;
				}
			}
			if (!new ArrayList<Long>(Arrays.asList(1l, 2l, 12l))
					.contains(Long.valueOf(list.get(count).getAcctStat()))) {
				continue;
			}
			accList.add(list.get(count));
		}
		return accList;
	}

	public static List<D009022> getValidAccListIVR(String custNo) {
		List<D009022> list = getAccountsFromCustNo(custNo);
		logger.error(list.size());
		List<D009022> accList = new ArrayList<>();
		for (int count = 0; count < list.size(); count++) {
			String productCode = DataUtils.getProductCode(list.get(count).getId().getPrdAcctId());
			logger.error(productCode);
			D009021 productMaster = DataUtils.getProductMaster(String.valueOf(list.get(count).getId().getLbrCode()),
					productCode);
			if (productMaster != null) {
				if (!new ArrayList<Long>(Arrays.asList(11l, 12l))
						.contains(Long.valueOf(productMaster.getModuleType()))) {
					continue;
				}
			}
			if (!new ArrayList<Long>(Arrays.asList(1l, 2l, 12l))
					.contains(Long.valueOf(list.get(count).getAcctStat()))) {
				continue;
			}
			accList.add(list.get(count));
		}
		return accList;
	}

	public static List<D009022> getLoanAccList(String custNo) {
		List<D009022> list = getAccountsFromCustNo(custNo);
		logger.error(list.size());
		List<D009022> accList = new ArrayList<>();
		for (int count = 0; count < list.size(); count++) {
			String productCode = DataUtils.getProductCode(list.get(count).getId().getPrdAcctId());
			logger.error(productCode);
			D009021 productMaster = DataUtils.getProductMaster(String.valueOf(list.get(count).getId().getLbrCode()),
					productCode.trim());
			logger.error("productMaster.getModuleType()::>>>"+productMaster.getModuleType());
			if (!Long.valueOf(productMaster.getModuleType()).equals(Long.valueOf("30")) && !Long.valueOf(productMaster.getModuleType()).equals(Long.valueOf("31"))) {
				continue;
			}
			if (!new ArrayList<Long>(Arrays.asList(1l, 2l, 12l))
					.contains(Long.valueOf(list.get(count).getAcctStat()))) {
				continue;
			}

			accList.add(list.get(count));
		}
		logger.error("accList.size()" + accList.size());
		return accList;
	}

	public static List<D009022> getLoanAccListPGM(String custNo, String accNo, String name, int brCode) {
		List<D009022> list = null;

		if (custNo != null && custNo.trim().length() > 0)
			list = getAccountsFromCustNo(custNo);
		else if (accNo != null && accNo.trim().length() > 0)
			list = getAccountsFromAccNo(accNo, brCode);
		else if (name != null && name.trim().length() > 0)
			list = getAccountsFromName(name, brCode);
		logger.error("Amarlist::>>" + list);
		logger.error("Amarlist::>>" + list);
		List<D009022> accList = new ArrayList<>();
		if (list != null) {
			for (int count = 0; count < list.size(); count++) {
				String productCode = DataUtils.getProductCode(list.get(count).getId().getPrdAcctId());
				D009021 productMaster = DataUtils.getProductMaster(String.valueOf(list.get(count).getId().getLbrCode()),
						productCode);
				if (!new ArrayList<Long>(Arrays.asList(1l, 2l, 12l))
						.contains(Long.valueOf(list.get(count).getAcctStat()))) {
					continue;
				}
				if (productMaster.getModuleType() == Short.valueOf("30")
						|| productMaster.getModuleType() == Short.valueOf("31")) {
					accList.add(list.get(count));
				}
			}
		}

		return accList;
	}

	public static boolean isOverDraftAccount(int lbrCode, String accNo) {
		Session session = HBUtil.getSessionFactory().openSession();
		D009022Id id = new D009022Id();
		id.setLbrCode(lbrCode);
		id.setPrdAcctId(accNo);
		D009022 d009022 = session.get(D009022.class, id);
		id = null;
		session.close();
		session = null;
		if (d009022 != null) {
			String productCode = DataUtils.getProductCode(d009022.getId().getPrdAcctId()).trim();
			logger.error(productCode);
			D009021 productMaster = DataUtils.getProductMaster(String.valueOf(d009022.getId().getLbrCode()),
					productCode);
			if (productMaster.getModuleType() == 14 || productMaster.getModuleType() == 13) {
				productMaster = null;
				return true;
			} else {
				productMaster = null;
				return false;
			}
		}
		return false;
	}

	public static CustomerDetails validateOdAccount(int brCode, String accNo) {
		CustomerDetails details = new CustomerDetails();
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D009042.class);
		criteria.add(Restrictions.eq("id.lbrCode", brCode));
		criteria.add(Restrictions.eq("id.prdAcctId", accNo));
		criteria.add(Restrictions.le("id.effFromDate", getOpenDate(brCode)));
		criteria.addOrder(Order.desc("id.effFromDate"));
		List<D009042> list = criteria.list();
		if (list != null && list.size() > 0) {
			logger.error("" + list.get(0).getExpDate());
			if (DateUtil.compareODExpDate(String.valueOf(list.get(0).getExpDate()), "")) {
				session.close();
				session = null;
				details.setResponse(MSGConstants.SUCCESS);
				details.setErrorMsg(MSGConstants.SUCCESS_MSG);
				details.setOdLimit(list.get(0));
				return details;
			} else {
				session.close();
				session = null;
				details.setResponse(MSGConstants.ERROR);
				details.setErrorMsg(MSGConstants.OD_LIMIT_EXPIRED);
				return details;
			}
		}
		session.close();
		session = null;
		criteria = null;
		details.setResponse(MSGConstants.ERROR);
		details.setErrorMsg(MSGConstants.TRANSACTION_LIMIT_NOT_SET);
		return details;
	}

	public static CustomerDetails validateOdAdhoclimit(int brCode, String accNo) {
		CustomerDetails details = new CustomerDetails();
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D009047.class);
		criteria.add(Restrictions.eq("id.lbrCode", brCode));
		criteria.add(Restrictions.eq("id.prdAcctId", accNo));

		List<D009047> list = criteria.list();
		if (list != null && list.size() > 0) {
			logger.error("" + list.get(0).getExpDate());
			if (DateUtil.compareODExpDate(String.valueOf(list.get(0).getExpDate()), "")) {
				session.close();
				session = null;
				details.setResponse(MSGConstants.SUCCESS);
				details.setErrorMsg(MSGConstants.SUCCESS_MSG);
				details.setOdAdhocLimit(list.get(0));
				return details;
			} else {
				session.close();
				session = null;
				details.setResponse(MSGConstants.ERROR);
				details.setErrorMsg(MSGConstants.OD_LIMIT_EXPIRED);
				return details;
			}
		}
		session.close();
		session = null;
		criteria = null;
		details.setResponse(MSGConstants.ERROR);
		details.setErrorMsg(MSGConstants.LIMIT_IS_COMPLETELY_UTILIZED);
		return details;
	}

	public static void main(String[] args) {
		logger.error("DataUtils.main() Double.valueOf(list.get(0).getActClrBalFcy()) " + getNextRRN());

//		String.valueOf(new BigDecimal(
//				(18427.400000000001 - 0) + 0).setScale(2, 4)));

		System.exit(0);
		// logger.error(getAccount("001000600000015"));
//		logger.error("" + storeSMSEntries("CNSB", "RP000000001", "8983389108", "CBLK RP000000001 UNBLOCK"));
		logger.error(getTRNSummary("20170505", "20180514", "00", "003000600000001"));
		// logger.error(getNextRRN());
		// logger.error(getAccountsFromAccNo("1", 3).size());
		// logger.error("ACCLIST::>>"+getLoanAccListPGM("", "1", "", 3));
		// logger.error("AccNo::>>"+getAccountsFromName("A", 3));

		// logger.error(getLoanAccListPGM("", "", "a", 3).size());;
		// logger.error(getAccountFormMobile("8888888888"));
		// logger.error(getSimSePayTransactionStatus("123456789", "SRCB",
		// "100"));
		/*
		 * Session session=HBUtil.getSessionFactory().openSession(); Transaction
		 * t=session.beginTransaction();
		 * logger.error("Account No::>>"+getNextAccNo(3, "ACCTNUM", 'O',
		 * "CA",session)); t.commit(); session.close(); session=null;
		 */

		// String maxAccNo=String.format("%08d",Long.valueOf(getNextAccNo(3,
		// "ACCTNUM", 'O', "CA")));
		//// logger.error("q.getResultList().get(0):::>>>"+q.getResultList().get(0));
		// Logger.error("maxAccNo.length()::>>>"+maxAccNo.length());
		// String acc32 = String.format("%-8s",
		// "CA")+"00000000"+maxAccNo+"00000000";
		// logger.error("Acc32::>"+acc32);
		// logger.error("ACCNO::>>"+);
		// logger.error("AccNo::>>"+CustomerMasterServiceImpl.getNextAccNo("SB",
		// "3"));
		// for(int i=0;i<999999;i++)
		// logger.error(""+getNextRRN());
		// logger.error((""+getNextRRN()));

		// logger.error(""+DataUtils.getNextRRN());
		// logger.error(isJointHolder(3, "SB 000000000000670700000000",
		// "2"));
		// logger.error(getNextRRN());
		// logger.error("date ::>>"+DateUtil.addDays(365, new Date()));
		// logger.error("RRN::>>>"+getNextRRN());
		// logger.error(""+getLoanAccountDetails("GOLD
		// 000000000000000300000000", 3));
		// logger.error(""+getCityState("MH", "C").getMapOutput());//
		// logger.error("RRN::>>"+DataUtils.getNextRRN());
		// logger.error(getAccount("003001100006707"));
		// logger.error(getNextcustNo());
		// logger.error(getInstDetailsForStop(3, "SB
		// 000000000000670700000000", 10, "000000179581"));
		// logger.error("Service Charges::>>>"+getServiceCharges("20"));
		// logger.error(""+getIssuedInstruments(3, "SB
		// 000000000000670700000000", 10, "000000179590"));//
		// logger.error("getCustMataster()::>>>"+getCustMataster());

		// logger.error(""+getChequeChargeType(12, 10, "SB", 1));//

		// logger.error("getNextcustNo()::>>"+getNextcustNo());
		// fetchIssuedInstrumentsList(3, "SB 000000000000670700000000", "11",
		// 1l,5l,0);
		// logger.error(pingHost("10.100.5.238", 9093, 30));
		// logger.error("MUB:>>>"+getDetails("MUM"));
		// logger.error(getCityListFromBranchMaster("MH"));
		// logger.error("Get Loan Type
		// List::::>>>>>>>>"+getLoanAccList("53393").size());
		// logger.error(getChequeStatus(3, "003001000006707",
		// "000000293751"));
		// logger.error(getAccountAddress(3, "PGM
		// 000000000000000100000000"));
		// logger.error(updateBadLoginsWS(1, "53393", 1));

		// int incrementVal =
		// Integer.parseInt(ConfigurationLoader.getParameters(false).getProperty("maxNoOfDays_For_Unused_Chq"));
		// Calendar cal = DateUtil.addingDateTime(new Date(), -incrementVal,
		// "Days");
		// Date result = cal.getTime();
		// Date fromDate = result;
		// Date toDate = new Date();
		// List<D010080> list=new DataUtils().ChequeBookFileGenerationList("3",
		// "SB 000000000000670700000000", fromDate, toDate);
		// logger.error("list::>>"+list.size());

		// logger.error(fetchMMID("48943", "8307001"));

		// logger.error(prepareMMIDMasterObject("48943", "AMAR",
		// "8983389108", "8307001", 2, "SBPUB 000000000000329600000000"));;

		// if(null==getCustMobNo("208620") || getCustMobNo("208620").isEmpty())
		// logger.error("null or empty");
		// else
		// logger.error("has values");

		// logger.error(getCustMobNo("208620").size());
		// validateOdAccount(12, "OD 000000000000002400000000");
		// getValidAccList("53393");

		// logger.error(""+accList.size());

		// logger.error(getNextRRN());
		// logger.error(getAccountFormMobileNo("919820709854"));
		// logger.error(getCustomerDetails("003001000006707").getId().getMobileNo().trim());

		// P2ATransact();
		// HBUtil.getSessionFactory().close();
		// getCustomerDetails("003010000006707");
		// logger.error(getMinistatement(3, "SB 000000000000670700000000",
		// 5).size());;
	}

	public static List<D350078> getCustMobNo(String custNo) {
		// TODO Auto-generated method stub
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D350078.class);
		criteria.add(Restrictions.eq("id.custNo", custNo));
		List<D350078> list = criteria.list();
		session.close();
		session = null;
		criteria = null;
		if (list != null && list.size() > 0)
			return list;

		return null;
	}

	public static D350031 prepareMobileBankingRegObject(String custNo, String longName, String pagerNo, String queryPin,
			String transPin, int lbrCode, Date createDate, char mobBankingYN, Date pinChangeDate, Date pinChangeTime,
			Date tPinChangeDate, Date tPinChangeTime, String add1, String add2, String add3, Double appLmtAmt,
			Double smsLmtAmt, String shortName, String newQueryPin, String newTransPin) {
		D350031 mobileRegistration = new D350031();
		mobileRegistration.setCustNo(custNo);
		mobileRegistration.setCustName(longName.length() > 30 ? longName.substring(0, 30) : longName);
		mobileRegistration.setMobileNo(pagerNo.length() == 10 ? "91" + pagerNo : pagerNo);
		mobileRegistration.setTransPin(queryPin);
		mobileRegistration.setQueryPin(" ");
		mobileRegistration.setBrCode(lbrCode);
		mobileRegistration.setCreateDate(createDate);
		mobileRegistration.setMobBankingYn(mobBankingYN);
		mobileRegistration.setLastAccessDate(DateUtil.getDateFromStringYYYYMMDD("1990-01-01"));
		mobileRegistration.setLastAccessTime(DateUtil.getDateFromStringYYYYMMDD("1990-01-01"));
		mobileRegistration.setPinChangeDate(pinChangeDate);
		mobileRegistration.setPinChangeTime(pinChangeTime);
		mobileRegistration.setTpinChangeDate(tPinChangeDate == null ? new Date() : tPinChangeDate);
		mobileRegistration.setTpinChangeTime(tPinChangeTime == null ? new Date() : tPinChangeTime);
		mobileRegistration.setPinPrintDate(DateUtil.getDateFromStringYYYYMMDD("1990-01-01"));
		mobileRegistration.setPinPrintCount(0);
		mobileRegistration.setAdd1(add1);
		mobileRegistration.setAdd2(add2);
		mobileRegistration.setAdd3(add3);
		mobileRegistration.setDbtrAddMk(999998);
		mobileRegistration.setDbtrAddMb(lbrCode);
		mobileRegistration.setDbtrAddMs(0);
		mobileRegistration.setDbtrAddMd(DateUtil.convertDateFormat(new Date()));
		mobileRegistration.setDbtrAddMt(DateUtil.getFormattedTime(new Date()));
		mobileRegistration.setDbtrAddCk(999998);
		mobileRegistration.setDbtrAddCb(lbrCode);
		mobileRegistration.setDbtrAddCs(0);
		mobileRegistration.setDbtrAddCd(DateUtil.convertDateFormat(new Date()));
		mobileRegistration.setDbtrAddCt(DateUtil.getFormattedTime(new Date()));
		mobileRegistration.setDbtrLupdMk(999998);
		mobileRegistration.setDbtrLupdMb(lbrCode);
		mobileRegistration.setDbtrLupdMs(0);
		mobileRegistration.setDbtrLupdMd(DateUtil.convertDateFormat(new Date()));
		mobileRegistration.setDbtrLupdMt(DateUtil.getFormattedTime(new Date()));
		mobileRegistration.setDbtrLupdCk(999998);
		mobileRegistration.setDbtrLupdCb(lbrCode);
		mobileRegistration.setDbtrLupdCs(0);
		mobileRegistration.setDbtrLupdCd(DateUtil.convertDateFormat(new Date()));
		mobileRegistration.setDbtrLupdCt(DateUtil.getFormattedTime(new Date()));
		mobileRegistration.setDbtrTauthDone(1);
		mobileRegistration.setDbtrRecStat(1);
		mobileRegistration.setDbtrAuthDone(1);
		mobileRegistration.setDbtrAuthNeeded(0);
		mobileRegistration.setDbtrUpdtChkId(1);
		mobileRegistration.setDbtrLhisTrnNo(0);
		mobileRegistration.setTransPassPin(transPin);
		mobileRegistration.setSmsTrnDate(DateUtil.getDateFromStringYYYYMMDD("1990-01-01"));
		mobileRegistration.setSmsLmtAmt(smsLmtAmt);
		mobileRegistration.setAppTrnDate(DateUtil.getDateFromStringYYYYMMDD("1990-01-01"));
		mobileRegistration.setAppLmtAmt(appLmtAmt);
		mobileRegistration.setNickName(shortName);
		mobileRegistration.setRegNo(" ");
		mobileRegistration.setNewTransPin(newTransPin);
		mobileRegistration.setNewQueryPin(newQueryPin);
		return mobileRegistration;
	}

	public static D350032 prepareMMIDMasterObject(String custNo, String longName, String pagerNo, String mmid,
			int lbrCode, String prdAcctId) {
		D350032Id id = new D350032Id();
		id.setCustNo(custNo);
		id.setMobileNo(pagerNo.length() == 10 ? "91" + pagerNo : pagerNo);
		id.setMmid(mmid);

		D350032 custxTreDtls = new D350032();
		custxTreDtls.setId(id);
		custxTreDtls.setAcctNo(prdAcctId);
		custxTreDtls.setBrCode(lbrCode);
		custxTreDtls.setAcctName(longName);
		logger.error("custxTreDtls amar::>>" + custxTreDtls);
		logger.error("custxTreDtls amar::>>" + custxTreDtls);
		return custxTreDtls;
	}

	public static D350001 prepareOtpObject(int lbrCode, String prdAcctId, Double setOtpTrnLmt, Double setDailyTrnLmt,
			int noOfTrns, int setOtpExpTime, String remarks, String custNo) {
		D350001Id id = new D350001Id();
		id.setLbrCode(lbrCode);
		id.setPrdAcctId(prdAcctId);
		D350001 otp = new D350001();
		otp.setId(id);
		otp.setTrnsactionlmt(setOtpTrnLmt.intValue());
		otp.setDailyTrnslmt(setDailyTrnLmt);
		otp.setNoTrans(noOfTrns);
		otp.setExpTimeOtp(setOtpExpTime);
		otp.setRemarks(remarks);
		otp.setDbtrAddMk(999998);
		otp.setDbtrAddMb(lbrCode);
		otp.setDbtrAddMs(0);
		otp.setDbtrAddMd(DateUtil.convertDateFormat(new Date()));
		otp.setDbtrAddMt(DateUtil.getFormattedTime(new Date()));
		otp.setDbtrAddCk(999998);
		otp.setDbtrAddCb(lbrCode);
		otp.setDbtrAddCs(0);
		otp.setDbtrAddCd(DateUtil.convertDateFormat(new Date()));
		otp.setDbtrAddCt(DateUtil.getFormattedTime(new Date()));
		otp.setDbtrLupdMk(999998);
		otp.setDbtrLupdMb(lbrCode);
		otp.setDbtrLupdMs(0);
		otp.setDbtrLupdMd(DateUtil.convertDateFormat(new Date()));
		otp.setDbtrLupdMt(DateUtil.getFormattedTime(new Date()));
		otp.setDbtrLupdCk(999998);
		otp.setDbtrLupdCb(lbrCode);
		otp.setDbtrLupdCs(0);
		otp.setDbtrLupdCd(DateUtil.convertDateFormat(new Date()));
		otp.setDbtrLupdCt(DateUtil.getFormattedTime(new Date()));
		otp.setDbtrTauthDone(1);
		otp.setDbtrRecStat(1);
		otp.setDbtrAuthDone(1);
		otp.setDbtrAuthNeeded(0);
		otp.setDbtrUpdtChkId(1);
		otp.setDbtrLhisTrnNo(0);
		otp.setCustNo(custNo);
		return otp;
	}

	public static D350032 fetchMMID(String custNo, String mmid) {
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D350032.class);
		criteria.add(Restrictions.eq("id.custNo", custNo));
		criteria.add(Restrictions.eq("id.mmid", mmid));
		List<D350032> list = criteria.list();
		session.close();
		criteria = null;
		session = null;
		if (list != null && !list.isEmpty())
			return list.get(0);
		return null;
	}

	public static D350001 searchOTP(int lbrCode, String prdAcctId) {
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D350001.class);
		criteria.add(Restrictions.eq("id.lbrCode", lbrCode));
		criteria.add(Restrictions.eq("id.prdAcctId", prdAcctId));
		List<D350001> list = criteria.list();
		session.close();
		criteria = null;
		session = null;
		if (list != null && !list.isEmpty())
			return list.get(0);
		return null;
	}

	public static D001004 getSystemParameter(int l, String setotptrnlmt) {
		try (Session session = HBUtil.getSessionFactory().openSession()) {
			D001004Id id = new D001004Id();
			id.setCode(setotptrnlmt);
			id.setLbrCode(l);
			D001004 d04 = session.get(D001004.class, id);
			session.close();
			if (d04 != null) {
				return d04;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static D350034 getMobCustStatus(String custNo) {
		try (Session session = HBUtil.getSessionFactory().openSession()) {
			Criteria criteria = session.createCriteria(D350034.class);
			criteria.add(Restrictions.eq("custNo", custNo));
			List<D350034> list = criteria.list();
			session.close();
			criteria = null;
			if (list != null && !list.isEmpty()) {
				return list.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static D350034 prepareCustOtherInfoObject(String custNo, int lbrCode, int status, int transPassPinStatus,
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
		custOtherInfo.setLastSysLiTime(DateUtil.getDateFromStringYYYYMMDD("1990-01-01"));
		custOtherInfo.setNextSysLiDt(DateUtil.getDateFromStringYYYYMMDD("1990-01-01"));
		custOtherInfo.setBadLoginsDt(DateUtil.getDateFromStringYYYYMMDD("1990-01-01"));
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
		custOtherInfo.setNextMobPwdChgDt(DateUtil.getDateFromStringYYYYMMDD("1990-01-01"));
		custOtherInfo.setTranPinStatus(1);
		custOtherInfo.setTransPassPinStatus(transPassPinStatus);
		return custOtherInfo;
	}

	public static D350033 preparePinStatusObject(String custNo, int mobPinPrint, Date mobPinPrintDate,
			int transPassPinPrint, Date transPassPinPrintDate) {
		D350033Id pinStatus = new D350033Id();
		pinStatus.setCustNo(custNo);
		pinStatus.setMobPinPrint(mobPinPrint);
		pinStatus.setMobPinPrintDate(mobPinPrintDate);
		pinStatus.setTransPassPinPrint(transPassPinPrint);
		pinStatus.setTransPassPinPrintDate(transPassPinPrintDate);
		D350033 d350033 = new D350033();
		d350033.setId(pinStatus);
		pinStatus = null;
		return d350033;
	}

	public static D350035 prepareCustOtherDtls(String custNo, Date genDate, Date genTime, char isDuplicate,
			String remarks) {
		D350035Id custOtherDtls = new D350035Id();
		D350035 d350035 = new D350035();
		custOtherDtls.setCustNo(custNo);
		custOtherDtls.setGenDate(genDate);
		custOtherDtls.setGenTime(genTime);
		d350035.setIsDuplicate(isDuplicate);
		d350035.setRemarks(remarks);
		d350035.setId(custOtherDtls);
		custOtherDtls = null;
		return d350035;
	}

	public static int updateTransPinStatusWS(int transPinStatus, int status, String custNo) {
		long startTime = System.currentTimeMillis();
		String hql = "update D350034 set tranPinStatus = ?, status = ? where custNo = ?";
		Query query = HBUtil.getSessionFactory().openSession().createQuery(hql);
		query.setInteger(0, transPinStatus);
		query.setInteger(1, status);
		query.setString(2, custNo);
		int row = query.executeUpdate();

		if (row > 0) {
			long endTime = System.currentTimeMillis();

			return row;
		}

		long endTime = System.currentTimeMillis();
		logger.error(
				"Instrumentation :<MobileRegistrationDAOImpl.java>:<updateTransPinStatusWS>: " + (endTime - startTime));
		return 0;
	}

	public static int updateNewQueryPinPwdWS(int transPinStatus, int status, String custNo) {
		long startTime = System.currentTimeMillis();
		String hql = "update D350034 set tranPinStatus = ?, status = ? where custNo = ?";
		Query query = HBUtil.getSessionFactory().openSession().createQuery(hql);
		query.setInteger(0, transPinStatus);
		query.setInteger(1, status);
		query.setString(2, custNo);
		int row = query.executeUpdate();
		if (row > 0) {
			long endTime = System.currentTimeMillis();

			return row;
		}

		long endTime = System.currentTimeMillis();
		logger.error(
				"Instrumentation :<MobileRegistrationDAOImpl.java>:<updateTransPinStatusWS>: " + (endTime - startTime));
		return 0;
	}

	public int updateTransPinPwdWS(String transPin, String custNo, String pinType) {
		long startTime = System.currentTimeMillis();
		String hqlQuery = "";
		if (pinType.equalsIgnoreCase(MSGConstants.TRANS_PWD))
			hqlQuery = "update D350031 set transPin = ? where custno = ?";
		else
			hqlQuery = "update D350031 set queryPin = ? where custno = ?";
		Query query = HBUtil.getSessionFactory().openSession().createQuery(hqlQuery);
		query.setString(0, transPin);
		query.setString(1, custNo);
		int row = query.executeUpdate();
		if (row > 0) {
			long endTime = System.currentTimeMillis();
			logger.error("Instrumentation :<MobileRegistrationDAOImpl.java>:<updateTransPinPwdWS>: "
					+ (endTime - startTime));
			return row;
		}

		long endTime = System.currentTimeMillis();
		logger.debug(
				"Instrumentation :<MobileRegistrationDAOImpl.java>:<updateTransPinPwdWS>: " + (endTime - startTime));
		return 0;
	}

	public static String storeD350032(D350032 obj) {
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		try {
			session.save(obj);
			t.commit();
			session.close();
			session = null;
			t = null;
			return MSGConstants.SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			session.close();
			t = null;
			session = null;
			return MSGConstants.ERROR;
		}
	}

	public static String storeD350001(D350001 obj) {
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		try {
			session.save(obj);
			t.commit();
			session.close();
			session = null;
			t = null;
			return MSGConstants.SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			session.close();
			t = null;
			session = null;
			return MSGConstants.ERROR;
		}
	}

	public static boolean checkJointAccountHolder(int custNo9022, int custNo0032, int lbrcode9022) {
		// 9022
		List<D009022> accountList = DataUtils.accountListForTdsProjection(lbrcode9022, custNo9022);
		if (null == accountList || accountList.isEmpty()) {
			return false;
		}
		boolean modeOfOprnFlag = false;
		boolean isJointHolder = false;
		for (D009022 account : accountList) {
			/*
			 * String modeOfOprn = SwiftCoreProperties.getProperty("modeOfOprn"); String[]
			 * modeOfOprnArr = modeOfOprn.split(",");
			 * if(Arrays.asList(modeOfOprnArr).contains(Long.toString(account.
			 * getModeoprn()))){
			 */
			String code = String.format("%09s", account.getModeOprn());
			D001002 lookUp = LookUpServiceImpl.getLookUpdetails(1066, code).get(0);
			if (null != lookUp && lookUp.getSecYn() == 'Y') {
				modeOfOprnFlag = true;
				// 9021
				String productNo = DataUtils.getProductCode(account.getId().getPrdAcctId());
				D009021 productMaster = DataUtils.getProductMaster(String.valueOf(account.getId().getLbrCode()),
						productNo.trim());
				if (null == productMaster) {
					continue;
				}
				if (!MSGConstants.MODULE_TYPE_LIST.contains(productMaster.getModuleType())) {
					continue;
				}
				List<D010153> jointHolderlist = DataUtils.getDetailsForNameType(account.getId().getLbrCode(),
						account.getId().getPrdAcctId(), 1);
				if (null == jointHolderlist || jointHolderlist.isEmpty()) {
					continue;
				}
				for (D010153 jointHolders : jointHolderlist) {
					if (jointHolders.getCusNo() == custNo0032) {
						isJointHolder = true;
						break;
					}
				}
				if (isJointHolder) {
					break;
				}
			}
		}
		if (!modeOfOprnFlag || !isJointHolder) {
			return false;
		}
		return true;
	}

	public static List<D009022> accountListForTdsProjection(int lbrcode, int custno) {
		long l_start = System.currentTimeMillis();
		String hql = "from D009022 where custno=:custno and lbrcode=:lbrcode ";
		Query query = HBUtil.getSessionFactory().openSession().createQuery(hql);
		query.setParameter("custno", custno);
		query.setParameter("lbrcode", lbrcode);
		long l_end = System.currentTimeMillis();
		logger.error("Instrumentation :<AccountDAOImpl.java>:<accountListForTdsProjection>: " + (l_end - l_start));
		return query.list();
	}

	public static List<D010153> getDetailsForNameType(int lbrcode, String prdacctid, int nameType) {
		long l_start = System.currentTimeMillis();
		String queryString = " from JointHolders where id.lbrcode=? and id.prdacctid=? and id.nametype=? ";
		Query queryObject = HBUtil.getSessionFactory().openSession().createQuery(queryString);
		queryObject.setLong(0, lbrcode);
		queryObject.setString(1, prdacctid);
		queryObject.setLong(2, nameType);
		List<D010153> list = queryObject.list();
		if (list != null && list.size() != 0) {
			return list;
		}
		long l_end = System.currentTimeMillis();
		logger.debug("Instrumentation :<AccountDAOImpl.java>:<getDetailsForNameType>: " + (l_end - l_start));
		return null;
	}

	public static D001003 getBranchMaster(Integer brcode) {
		// TODO Auto-generated method stub
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D001003.class);
		criteria.add(Restrictions.eq("pbrCode", brcode));
		List<D001003> list = criteria.list();
		session.close();
		session = null;
		criteria = null;
		if (list != null && list.size() > 0)
			return list.get(0);
		return null;
	}

	public static D010054 getAddressDetails(int lbrCode, String prdAcctId, int subAddType) {
		// TODO Auto-generated method stub

		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D010054.class);
		criteria.add(Restrictions.eq("id.lbrCode", lbrCode));
		criteria.add(Restrictions.eq("id.prdAcctId", prdAcctId));
		criteria.add(Restrictions.eq("id.addrType", Byte.valueOf(subAddType + "")));
		List<D010054> list = criteria.list();
		session.close();
		session = null;
		criteria = null;

		if (list != null && list.size() > 0)
			return list.get(0);

		return null;
	}

	public static Map<Long, String> fetchNumericCodeList(List<D001002> lookupList) {
		logger.error("lookupList:::>>>" + lookupList);
		long l_start = System.currentTimeMillis();
		Map<Long, String> map = new LinkedHashMap<Long, String>();
		for (Iterator<D001002> iterator = lookupList.iterator(); iterator.hasNext();) {
			D001002 lookup = iterator.next();
			map.put(Long.valueOf(lookup.getId().getCode().trim()), lookup.getCodeDesc().trim());
		}
		logger.error("MAP::>>>" + map);
		logger.error("MAP::>>>" + map);
		long l_end = System.currentTimeMillis();
		logger.error("Instrumentation :<LookupDAOImpl.java>:<fetchNumericCodeList>: " + (l_end - l_start));
		return map;
	}

	/*
	 * public static D500028 getDetails(String placecd) { long l_start =
	 * System.currentTimeMillis(); D500028 placeMaster=null; while (placecd.length()
	 * < 3) { placecd = placecd + " "; } Session
	 * session=HBUtil.getSessionFactory().openSession(); Criteria
	 * queryObject=session.createCriteria(D500028.class);
	 * queryObject.add(Restrictions.eq("placeCd", placecd.trim()));
	 * 
	 * List<D500028> placeMasterList = queryObject.list(); if (placeMasterList !=
	 * null&& placeMasterList.size() != 0) { placeMaster = placeMasterList.get(0);
	 * return placeMaster; } long l_end = System.currentTimeMillis();
	 * logger.error("Instrumentation :<PlaceMasterDAOImpl.java>:<getDetails>: "+
	 * (l_end - l_start)); return placeMaster; }
	 */
	public static List<D010080> ChequeBookFileGenerationList(String lbrCode, String prdAcctId, Date fromDate,
			Date toDate) {
		logger.error("From Date:>>>" + fromDate);
		logger.error("To Date::>>" + toDate);
		long startTime = System.currentTimeMillis();
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D010080.class);
		criteria.add(Restrictions.eq("id.lbrCode", Integer.valueOf(lbrCode)));
		criteria.add(Restrictions.between("id.effDate", DateUtil.getFormattedDateNew(fromDate),
				DateUtil.getFormattedDateNew(toDate)));
		criteria.add(Restrictions.eq("id.acctId", prdAcctId));
		criteria.add(Restrictions.eq("status", Byte.valueOf("1")));
		List<D010080> cheqBookFileGenList = criteria.list();
		session.close();
		session = null;
		criteria = null;
		long endTime = System.currentTimeMillis();
		logger.error(
				"Instrumentation :<ChequebookDAOImpl.java>:<ChequeBookFileGenerationList>: " + (endTime - startTime));
		return cheqBookFileGenList;
	}

	public static List<D009044> fetchIssuedInstrumentsList(int lbrcode, String acctno32digit, String insType,
			int status) {
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D009044.class);
		criteria.add(Restrictions.eq("id.lbrCode", lbrcode));
		criteria.add(Restrictions.eq("id.issuedTo", acctno32digit));
		criteria.add(Restrictions.eq("id.insType", Short.valueOf(insType)));
		criteria.add(Restrictions.eq("status", Byte.valueOf("" + status)));
		List<D009044> list = criteria.list();
		session.close();
		session = null;
		criteria = null;
		if (list != null && list.size() > 0)
			return list;
		return null;
	}

	public static D009044 fetchIssuedInstruments(int lbrcode, String acctno32digit, String chequeNo) {
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D009044.class);
		criteria.add(Restrictions.eq("id.lbrCode", lbrcode));
		criteria.add(Restrictions.eq("id.issuedTo", acctno32digit));
		criteria.add(Restrictions.eq("id.instruNo", chequeNo));
		List<D009044> list = criteria.list();
		session.close();
		session = null;
		criteria = null;
		if (list != null && list.size() > 0)
			return list.get(0);
		return null;
	}

	public static List<D009044> fetchIssuedInstrumentsList(int lbrCode, String accountNo, String insType,
			Long startInsNo, Long maxStartInsNo, int status) {
		Session session = HBUtil.getSessionFactory().openSession();
		logger.error("lbrCode::>>" + lbrCode);
		logger.error("accountNo::>>" + accountNo);
		logger.error("insType::>>" + insType);
		logger.error("startInsNo:>>>" + startInsNo);
		logger.error("maxStartInsNo::>>" + maxStartInsNo);
		Criteria criteria = session.createCriteria(D009044.class);
		criteria.add(Restrictions.eq("id.lbrCode", lbrCode));
		criteria.add(Restrictions.eq("id.issuedTo", accountNo));
		criteria.add(Restrictions.eq("id.insType", Short.valueOf(insType)));
		criteria.add(Restrictions.gt("id.instruNo", "" + startInsNo.intValue()));
		criteria.add(Restrictions.lt("id.instruNo", "" + maxStartInsNo.intValue()));
		// criteria.add(Restrictions.sizeGt("id.instruNo",
		// startInsNo.intValue()));
		// criteria.add(Restrictions.sizeLt("id.instruNo",
		// maxStartInsNo.intValue()));
		criteria.add(Restrictions.eq("status", Byte.valueOf("" + status)));
		List<D009044> list = criteria.list();
		session.close();
		session = null;
		criteria = null;
		if (list != null && list.size() > 0)
			return list;
		return null;
	}

	public static D010003 getInstrumentMasterDetails(int insType) {
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D010003.class);
		criteria.add(Restrictions.eq("itpType", Short.valueOf(insType + "")));
		List<D010003> list = criteria.list();
		session.close();
		session = null;
		criteria = null;
		if (list != null && list.size() > 0)
			return list.get(0);
		return null;
	}

	public static List<D010103> fetchInstrumentTypeMaintenance(int insType, int bookSize) {
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D010103.class);
		criteria.add(Restrictions.eq("id.itpType", Short.valueOf(insType + "")));
		criteria.add(Restrictions.eq("validBookSize", Short.valueOf(bookSize + "")));
		List<D010103> list = criteria.list();
		session.close();
		session = null;
		criteria = null;

		if (list != null && list.size() > 0)
			return list;
		return null;
	}

	public static List<D010009> ChequeBookIssued(int lbrCode, String prdAcctId, int status) {
		long startTime = System.currentTimeMillis();
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D010009.class);
		criteria.add(Restrictions.eq("id.lbrCode", lbrCode));
		criteria.add(Restrictions.eq("issuedTo", prdAcctId));
		criteria.add(Restrictions.eq("status", Byte.valueOf(status + "")));
		List<D010009> list = criteria.list();
		session.close();
		session = null;
		criteria = null;
		if (list != null && list.size() > 0)
			return list;
		long endTime = System.currentTimeMillis();
		logger.error("Instrumentation :<ChequebookDAOImpl.java>:<ChequeBookIssued>: " + (endTime - startTime));
		return null;
	}

	public static D010010 prepareStopPaymentObj(int lbr, String IssueTo, String chequeNo, short insType, String name,
			String remark, double amount) {
		D010010 d010010 = new D010010();
		D010010Id d010010Id = new D010010Id();
		d010010Id.setLbrCode(lbr);
		d010010Id.setIssuedTo(IssueTo);
		d010010Id.setInsType(insType);
		d010010Id.setInsNo(chequeNo);
		d010010.setId(d010010Id);
		d010010.setInstrDate(new Date());
		d010010.setStopDate(new Date());
		d010010.setStopTime(new Date());
		d010010.setRequestDate(new Date());
		d010010.setPayeeName(name);
		d010010.setStopAmt(amount);
		d010010.setRemarks(remark);
		d010010.setRevokeFlag((byte) 0);
		d010010.setRevokeDate(new Date());
		d010010.setDbtrAddMk(164);
		d010010.setDbtrAddMb(1);
		d010010.setDbtrAddMs((short) 1);
		d010010.setDbtrAddMd(new Date());
		d010010.setDbtrAddMt(new Date());
		d010010.setDbtrAddCk(0);
		d010010.setDbtrAddCb(0);
		d010010.setDbtrAddCs((short) 1);
		d010010.setDbtrAddCd(new Date());
		d010010.setDbtrAddCt(new Date());
		d010010.setDbtrLupdMk(1);
		d010010.setDbtrLupdMb(1);
		d010010.setDbtrLupdMs((short) 1);
		d010010.setDbtrLupdMd(new Date());
		d010010.setDbtrLupdMt(new Date());
		d010010.setDbtrLupdCk(1);
		d010010.setDbtrLupdCb(1);
		d010010.setDbtrLupdCd(new Date());
		d010010.setDbtrLupdCs((short) 0);
		d010010.setDbtrLupdCt(new Date());
		d010010.setDbtrTauthDone((short) 0);
		d010010.setDbtrRecStat((byte) 0);
		d010010.setDbtrAuthDone((byte) 1);
		d010010.setDbtrAuthNeeded((byte) 0);
		d010010.setDbtrUpdtChkId((byte) 0);
		d010010.setDbtrLhisTrnNo(0);
		return d010010;
	}

	/*
	 * public static IMPSTransactionResponse productTrnxValidations(D009021
	 * productMaster, String drCr) { IMPSTransactionResponse trnxRes = new
	 * IMPSTransactionResponse(); if(null == productMaster){
	 * logger.error("Product not found."); trnxRes.setResponse(MSGConstants.ERROR);
	 * trnxRes.setErrorMessage(MSGConstants.PRODUCT_NOT_FOUND); return trnxRes; }
	 * 
	 * if(drCr.equalsIgnoreCase("Debit") && productMaster.getModuleType() !=
	 * MSGConstants.SAVING && productMaster.getModuleType() != MSGConstants.CURRENT
	 * && productMaster.getModuleType() != MSGConstants.GL &&
	 * productMaster.getModuleType() != MSGConstants.OD){
	 * logger.error("Product not found."); trnxRes.setResponse(MSGConstants.ERROR);
	 * trnxRes.setErrorMessage(MSGConstants.PRODUCT_NOT_FOUND); return trnxRes; }
	 * else if(drCr.equalsIgnoreCase("Credit") && productMaster.getModuleType() !=
	 * MSGConstants.SAVING && productMaster.getModuleType() != MSGConstants.CURRENT
	 * && productMaster.getModuleType() != MSGConstants.GL &&
	 * productMaster.getModuleType() != MSGConstants.OD &&
	 * productMaster.getModuleType() != MSGConstants.LOAN){
	 * logger.error("Product not found.");
	 * 
	 * logger.error("Product not found."); trnxRes.setResponse(MSGConstants.ERROR);
	 * trnxRes.setErrorMessage(MSGConstants.PRODUCT_NOT_FOUND); return trnxRes; }
	 * 
	 * //if("Y".equalsIgnoreCase(chkAllValidation)){////As discussed with PRashant
	 * Bharambe, removed this condition. if(productMaster.getAcctStat() ==
	 * Byte.valueOf("3")){ logger.error("Product Status is Closed.");
	 * trnxRes.setResponse(MSGConstants.ERROR);
	 * trnxRes.setErrorMessage("Product Status is Closed."); return trnxRes; }
	 * 
	 * if(productMaster.getAcctStat() == Byte.valueOf("4")){
	 * logger.error("Product status is inoperative.");
	 * trnxRes.setResponse(MSGConstants.ERROR);
	 * trnxRes.setErrorMessage("Product status is inoperative."); return trnxRes; }
	 * 
	 * if(productMaster.getAcctStat() == Byte.valueOf("5")){
	 * logger.error("Product status is dormant.");
	 * trnxRes.setResponse(MSGConstants.ERROR);
	 * trnxRes.setErrorMessage("Product status is dormant."); return trnxRes; }
	 * if(productMaster.getAcctStat() == Byte.valueOf("6")){
	 * logger.error("Product status is blocked or freezed.");
	 * trnxRes.setResponse(MSGConstants.ERROR);
	 * trnxRes.setErrorMessage("Product status is blocked or freezed."); return
	 * trnxRes; } trnxRes.setValid(true); return trnxRes; }
	 */
	public static D009044 getIssuedInstruments(int lbrCode, String acctNo, int insType, String chequeNo) {
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D009044.class);
		criteria.add(Restrictions.eq("id.lbrCode", lbrCode));
		criteria.add(Restrictions.eq("id.issuedTo", acctNo));
		criteria.add(Restrictions.eq("id.insType", Short.valueOf(insType + "")));
		criteria.add(Restrictions.eq("id.instruNo", chequeNo));
		List<D009044> list = criteria.list();
		session.close();
		session = null;
		criteria = null;
		if (list != null && list.size() > 0)
			return list.get(0);
		return null;
	}

	public static D010010 getInstDetailsForStop(int lbrCode, String issuedTo, int insType, String insNo) {
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D010010.class);
		criteria.add(Restrictions.eq("id.lbrCode", lbrCode));
		criteria.add(Restrictions.eq("id.issuedTo", issuedTo));
		criteria.add(Restrictions.eq("id.insType", Short.valueOf(insType + "")));
		criteria.add(Restrictions.eq("id.insNo", insNo + ""));//
		List<D010010> list = criteria.list();
		session.close();
		session = null;
		criteria = null;
		if (null != list && list.size() > 0)
			return list.get(0);
		return null;
	}

	private IMPSTransactionResponse calculateStopChequeChargesForAccount(D010010 instrument, D009022 account,
			D009021 productMaster, Date operationDate, String remark, String mobileNo) throws Exception {
		IMPSTransactionResponse trnxRes = new IMPSTransactionResponse();
		int lbrcode = instrument.getId().getLbrCode();
		/**
		 * To get charge type for stopped cheques here we are passing parameter
		 * 'chargeType' as 2.(D130014)
		 */
		D130014 chequeChargesMasterForStopCheques = DataUtils.getChequeChargeType(lbrcode,
				instrument.getId().getInsType(), productMaster.getId().getPrdCd(), 2);

		/**
		 * To get service tax and edn cess for stopped cheques here passing parameter
		 * 'chargeType' as 2.(D130031)
		 */
		D130031 serviceChargeMasterForStopChqs = DataUtils.getServiceTaxAndEdnCess(20);
		Double chargeForStopCheques = 1 * chequeChargesMasterForStopCheques.getFlatRate();

		/**
		 * if charge is less than min chg amt
		 */
		if (chargeForStopCheques < chequeChargesMasterForStopCheques.getMinChgAmt()) {
			chargeForStopCheques = chequeChargesMasterForStopCheques.getMinChgAmt();
		}

		/**
		 * if charge is greater than max chg amt
		 */
		if (chargeForStopCheques > chequeChargesMasterForStopCheques.getMaxChgAmt()) {
			chargeForStopCheques = chequeChargesMasterForStopCheques.getMaxChgAmt();
		}

		String batchCode = "CHG";
		List<D130001> chargesMasterList = DataUtils.fetchChargesMasterDomain(lbrcode, 2,
				instrument.getId().getInsType(), account.getAcctType() + "", operationDate);

		if (null == chargesMasterList || chargesMasterList.isEmpty()) {
			chargesMasterList = DataUtils.fetchChargesMasterDomain(lbrcode, 2, instrument.getId().getInsType(), null,
					operationDate);
		}

		if (null != chargesMasterList && !chargesMasterList.isEmpty()) {
			batchCode = chargesMasterList.get(0).getBatchCd();
		}

		IMPSTransactionResponse batchCodeTrnxRes = DataUtils.validateBatchCode(lbrcode, batchCode, operationDate);
		if (!batchCodeTrnxRes.isValid()) {
		
			updateOsCharges(lbrcode, account.getId().getPrdAcctId(), chargeForStopCheques);
			logger.error(MSGConstants.SUCCESS + " but " + batchCodeTrnxRes.getErrorMessage());
			trnxRes.setResponse(MSGConstants.SUCCESS);
			trnxRes.setErrorMessage(MSGConstants.SUCCESS_MSG);
			trnxRes.setMobileNo(mobileNo);
			return trnxRes;
		}

		if (account.getFreezeType() == Byte.valueOf("2") || account.getFreezeType() == Byte.valueOf("4")
				|| account.getFreezeType() == Byte.valueOf("5") || account.getAcctStat() > Byte.valueOf("2")) {
			updateOsCharges(lbrcode, account.getId().getPrdAcctId(), chargeForStopCheques);
			logger.error(MSGConstants.SUCCESS + " but account Freeze type is 2, 4, 5 or Acct stat is greater then 2.");
			trnxRes.setResponse(MSGConstants.SUCCESS);
			trnxRes.setErrorMessage(MSGConstants.SUCCESS_MSG);
			trnxRes.setMobileNo(mobileNo);
			return trnxRes;
		}

		IMPSTransactionResponse amountValid = DataUtils.AmountValidation(account, chargeForStopCheques, operationDate,
				productMaster.getModuleType(), MSGConstants.NO);
		if (!amountValid.isValid()) {
			updateOsCharges(lbrcode, account.getId().getPrdAcctId(), chargeForStopCheques);
			logger.error(MSGConstants.SUCCESS + " but " + amountValid.getErrorMessage());

			trnxRes.setResponse(MSGConstants.SUCCESS);
			trnxRes.setErrorMessage(MSGConstants.SUCCESS);
			trnxRes.setMobileNo(mobileNo);
			return trnxRes;
		}

		Double servTaxForStopCheques = chargeForStopCheques * serviceChargeMasterForStopChqs.getSerTaxRate() / 100;
		servTaxForStopCheques = round2Dec(servTaxForStopCheques);
		Double ednCessForStopCheques = servTaxForStopCheques * serviceChargeMasterForStopChqs.getEduCesRate() / 100;
		ednCessForStopCheques = round2Dec(ednCessForStopCheques);

		/**
		 * calculate balance of account modified for F.S. dated Nov. 14th ,2011
		 */
		D130003 minimumBal = null;
		if (productMaster.getModuleType() != Short.valueOf("14")) {
			minimumBal = getMinBal(instrument.getId().getLbrCode(), productMaster.getId().getPrdCd());
		}

		/*
		 * instrument.calcDate(); Date startDate = instrument.getQuarterStartDate();
		 * Date endDate = instrument.getQuarterEndDate(); IAccountBalanceService
		 * accountBalance = (IAccountBalanceService) ServiceFinder
		 * .findBean("accountBalance"); Double tempWithdrawBalance =
		 * accountBalance.getWithdrawableBalance( lbrcode, productMaster.getPrdcd(),
		 * SwiftCoreUtil .getAccountNumber(account.getId().getPrdacctid()) .trim(),
		 * instrument.getUser().getOperationDate()); finalCharge = chargeForStopCheques
		 * + servTaxForStopCheques + ednCessForStopCheques; Double closingBalance = 0.0;
		 * if (productMaster.getModuletype().longValue() != 14L) {// Bug #12245
		 * IChequebookService chequebookService = (IChequebookService) ServiceFinder
		 * .findBean("chequebookService"); closingBalance =
		 * chequebookService.getAvgBalanceOfQuarter(lbrcode,
		 * account.getId().getPrdAcctId(), startDate, endDate); }
		 */

		/**
		 * if balance <= 0 & allowtodyn = 'N' or blank
		 */
		Double minimumBalance = 0.0;
		if (account.getChqBookYn() == 'Y' && minimumBal != null) {
			minimumBalance = minimumBal.getMinBal();
		} else if (account.getChqBookYn() == 'N' && minimumBal != null) {
			minimumBalance = minimumBal.getMinBalNonChq();
		}

		if (minimumBal == null) {
			minimumBalance = 0.00;
		}
		String rrnNo = DataUtils.getNextRRN();
		/*
		 * if (tempWithdrawBalance != null && closingBalance < minimumBalance &&
		 * closingBalance > 0) { if
		 * (chequeChargesMasterForStopCheques.getAllowTodyn()=='N' ||
		 * chequeChargesMasterForStopCheques .getAllowTodyn()==' ') { Voucher
		 * forOutstanding = new Voucher();
		 *//** *For OutStanding Charges** */
		/*
		 * forOutstanding = getOutstandingChargesAmtStopInstrument(
		 * chequeChargesMasterForStopCheques.getId().getLbrCode(), chargeForStopCheques,
		 * instrument, forOutstanding, account.getId().getPrdAcctId());
		 * trnxRes.setResponse(Long.toString(forOutstanding.getNextSetNo()));
		 * trnxRes.setCategory(Long.toString(forOutstanding.getNextScrollNo()));
		 * 
		 * } else { Voucher fromAccountCheqChrg = new Voucher();
		 *//** *For -Ve Amount(Vouchers)** */
		/*
		 * fromAccountCheqChrg = getVouchersStopInstruments(
		 * chequeChargesMasterForStopCheques.getId().getLbrCode(),
		 * account.getId().getPrdAcctId(), chequeChargesMasterForStopCheques,
		 * serviceChargeMasterForStopChqs, chargeForStopCheques, servTaxForStopCheques,
		 * ednCessForStopCheques, instrument, batchCode, remark, rrnNo);
		 * trnxRes.setResponse(Long.toString(fromAccountCheqChrg .getNextSetNo()));
		 * trnxRes.setCategory(Long.toString(fromAccountCheqChrg .getNextScrollNo())); }
		 * 
		 * trnxRes.setRefNo(batchCode); trnxRes.setValid(true); return trnxRes; } else
		 * if (tempWithdrawBalance != null && closingBalance >= minimumBalance ||
		 * closingBalance == 0) { if (tempWithdrawBalance.doubleValue() < finalCharge) {
		 * Voucher forOutstanding = new Voucher();
		 *//** *For OutStanding Charges** */
		/*
		 * forOutstanding = getOutstandingChargesAmtStopInstrument(
		 * chequeChargesMasterForStopCheques.getId().getLbrCode(), chargeForStopCheques,
		 * instrument, forOutstanding, account.getId().getPrdAcctId());
		 * trnxRes.setResponse(Long.toString(forOutstanding)); trnxRes .setCategory(Long
		 * .toString(forOutstanding.getScrollno())); } else { if
		 * (tempWithdrawBalance.doubleValue() > finalCharge .doubleValue()) { Voucher
		 * fromAccountCheqChrg = new Voucher();
		 *//** *For -Ve Amount(Vouchers)** *//*
												 * fromAccountCheqChrg = getVouchersStopInstruments(
												 * chequeChargesMasterForStopCheques .getId() .getLbrcode(),
												 * account.getId() .getPrdacctid(), chequeChargesMasterForStopCheques,
												 * serviceChargeMasterForStopChqs, chargeForStopCheques,
												 * servTaxForStopCheques, ednCessForStopCheques, instrument, batchCode,
												 * remark, rrnNo); trnxRes.setResponse(Long.toString
												 * (fromAccountCheqChrg .getSetno())); trnxRes.setCategory(Long.toString
												 * (fromAccountCheqChrg .getScrollno())); } }
												 */

		trnxRes.setRefNo(batchCode);
		trnxRes.setValid(true);
		return trnxRes;
	}
	// return null;
	// }

	private D130003 getMinBal(int lbrCode, String prdCd) {
		List<D130003> list = new ArrayList<D130003>();
		String queryString = "from D130003 WHERE id.lbrcode=:lbrcode AND  id.prdcd=:prdcd AND "
				+ "id.accttype=0 order by id.effdate desc ";
		Query queryObject = null;
		queryObject = HBUtil.getSessionFactory().openSession().createQuery(queryString);
		queryObject.setParameter("lbrcode", lbrCode);
		queryObject.setParameter("prdcd", prdCd);
		list = queryObject.list();
		if (list != null && list.size() != 0)

			return (D130003) list.get(0);
		else
			return null;
	}

	public static IMPSTransactionResponse validateBatchCode(int lbrcode, String batchCode, Date operationDate) {
		// TODO Auto-generated method stub
		IMPSTransactionResponse response = new IMPSTransactionResponse();
		D010001 batchMaster = getBatchMasterDetails(lbrcode, batchCode);
		if (null == batchMaster) {
			logger.error("Batch Code not found.");
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.BATCH_CODE_NOT_FOUND);
			return response;
		}
		if (batchMaster.getDbtrAuthDone() != Byte.valueOf("1")
				|| batchMaster.getDbtrAuthNeeded() != Byte.valueOf("0")) {
			logger.error("Batch Master not authorised.");
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.BATCCH_MASTER_UNAUTH);
			return response;
		}
		D010004 batchesDirectory = getBatch(lbrcode, batchCode, operationDate);

		if (null == batchesDirectory) {
			logger.error("dbdRec read error.");
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage("dbdRec read error.");
			return response;
		}

		if (batchesDirectory.getDbtrAuthDone() != Byte.valueOf("1")
				|| batchesDirectory.getDbtrAuthNeeded() != Byte.valueOf("0")) {
			logger.error("Daily Batch not authorized.");
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage("Daily Batch not authorized.");
			return response;
		}

		if (batchesDirectory.getInProcFlag() == 'Y') {
			logger.error("Batch in process.");
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage("Batch in process.");
			return response;
		}

		if (batchesDirectory.getStat() != Byte.valueOf("1") && batchesDirectory.getStat() != Byte.valueOf("2")) {
			logger.error("Batch not opened.");
			response.setResponse(MSGConstants.ERROR);
			response.setErrorCode("001");                  //change by Manish 
			response.setErrorMessage("Batch not opened.");
			return response;
		}
		D010007 dayOpenCloseFile = DataUtils.getOpenDayForVoucherBrowse(lbrcode, operationDate);
		if (null == dayOpenCloseFile) {
			logger.error("Day Is Closed.");
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage("Day Is Closed.");
		}
		response.setValid(true);
		return response;
	}

	private static List<D130001> fetchChargesMasterDomain(int lbrcode, int i, short insType, String accType,
			Date operationDate) {
		// TODO Auto-generated method stub
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D130001.class);
		criteria.add(Restrictions.eq("id.lbrCode", lbrcode));
		criteria.add(Restrictions.eq("id.chgType", Short.valueOf(i + "")));
		criteria.add(Restrictions.eq("id.insType", insType));
		criteria.add(Restrictions.eq("id.acctType", accType));
		criteria.add(Restrictions.eq("id.effDate", operationDate));
		criteria.addOrder(Order.desc("id.effdate"));
		List<D130001> list = criteria.list();
		session.close();
		session = null;
		if (list != null && list.size() > 0)
			return list;
		return null;
	}

	private static D130031 getServiceTaxAndEdnCess(int chgtype) {
		String queryString = "FROM D130031 WHERE id.chgType=:chgtype AND id.effDate = "
				+ "(SELECT max(id.effdate) FROM D130031 WHERE id.chgType=:chgtype )";
		Query queryObject = null;
		queryObject = HBUtil.getSessionFactory().openSession().createQuery(queryString);
		queryObject.setParameter("chgtype", Byte.valueOf(chgtype + ""));
		return (D130031) queryObject.list();
	}

	public static D130014 getChequeChargeType(int lbrcode, int instrumentType, String prdcd, int chargeType) {
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D130014.class);
		criteria.add(Restrictions.eq("id.lbrCode", lbrcode));
		criteria.add(Restrictions.eq("id.insType", Short.valueOf(instrumentType + "")));
		criteria.add(Restrictions.eq("id.prdCd", prdcd));
		criteria.add(Restrictions.eq("id.chgType", Byte.valueOf(chargeType + "")));
		/***Added by Aniket Desai on 22nd Aug,2019 for Stop Payment ***/
		criteria.addOrder(Order.desc("id.effDate"));
		List<D130014> list = criteria.list();
		session.close();
		session = null;
		if (list != null && list.size() > 0)
			return list.get(0);
		else
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

	public static D010001 getBatchMasterDetails(int lbrCode, String BatchCd) {
		// SELECT BookType FROM D010001 WHERE LBrCode = 9 AND Code = 'ABBTR'
		D010001 d001 = null;
		try (Session session = HBUtil.getSessionFactory().openSession()) {
			D010001Id id = new D010001Id();
			id.setCode(BatchCd);
			id.setLbrCode(lbrCode);
			d001 = session.get(D010001.class, id);
			session.close();
			if (d001 != null)
				return d001;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static D010007 getOpenDayForVoucherBrowse(int lbrCode, Date batchCode) {
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D010007.class);
		criteria.add(Restrictions.eq("id.lbrCode", lbrCode));
		criteria.add(Restrictions.eq("id.dyfDate", batchCode));
		List<D010007> list = criteria.list();
		session.close();
		session = null;
		if (list != null && list.size() > 0)
			return list.get(0);
		return null;
	}

	public static void updateOsCharges(int lbrCode, String accountNo, Double charges) throws Exception {
		D130008 chargesViewForAccount = DataUtils.fetchChargesViewForAccount(lbrCode, accountNo, 20);
		if (null != chargesViewForAccount) {
			Double osAmt = chargesViewForAccount.getOsAmt() + charges;
			chargesViewForAccount.setOsAmt(osAmt);
		} else {
			chargesViewForAccount = DataUtils.prepareChargesViewForAccountObject(lbrCode, accountNo, 20, charges,
					DateUtil.convertDateFormat(new Date()));
		}
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();
		try {
			session.saveOrUpdate(chargesViewForAccount);
			transaction.commit();
			session.close();
			session = null;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			session.close();
			session = null;
			transaction = null;
		}
	}

	private static D130008 prepareChargesViewForAccountObject(int lbrCode, String accountNo, int chgType,
			Double charges, Date convertDateFormat) {

		D130008Id id = new D130008Id();
		id.setLbrCode(lbrCode);
		id.setPrdAcctId(accountNo);
		id.setChgType(Byte.valueOf(chgType + ""));

		D130008 chargesViewForAccount = new D130008();
		chargesViewForAccount.setId(id);
		chargesViewForAccount.setOsAmt(charges);
		chargesViewForAccount.setLastApplDate(convertDateFormat);
		chargesViewForAccount.setDbtrAddMk(999998);
		chargesViewForAccount.setDbtrAddMb(lbrCode);
		chargesViewForAccount.setDbtrAddMs(Short.valueOf("0"));
		chargesViewForAccount.setDbtrAddMd(DateUtil.convertDateFormat(new Date()));
		chargesViewForAccount.setDbtrAddMt(DateUtil.getFormattedTime(new Date()));
		chargesViewForAccount.setDbtrAddCk(999998);
		chargesViewForAccount.setDbtrAddCb(lbrCode);
		chargesViewForAccount.setDbtrAddCs(Short.valueOf("0"));
		chargesViewForAccount.setDbtrAddCd(DateUtil.convertDateFormat(new Date()));
		chargesViewForAccount.setDbtrAddCt(DateUtil.getFormattedTime(new Date()));
		chargesViewForAccount.setDbtrLupdMk(999998);
		chargesViewForAccount.setDbtrLupdMb(lbrCode);
		chargesViewForAccount.setDbtrLupdMs(Short.valueOf("0"));
		chargesViewForAccount.setDbtrLupdMd(DateUtil.convertDateFormat(new Date()));
		chargesViewForAccount.setDbtrLupdMt(DateUtil.getFormattedTime(new Date()));
		chargesViewForAccount.setDbtrLupdCk(999998);
		chargesViewForAccount.setDbtrLupdCb(lbrCode);
		chargesViewForAccount.setDbtrLupdCs(Short.valueOf("0"));
		chargesViewForAccount.setDbtrLupdCd(DateUtil.convertDateFormat(new Date()));
		chargesViewForAccount.setDbtrLupdCt(DateUtil.getFormattedTime(new Date()));
		chargesViewForAccount.setDbtrTauthDone(Short.valueOf("1"));
		chargesViewForAccount.setDbtrRecStat(Byte.valueOf("1"));
		chargesViewForAccount.setDbtrAuthDone(Byte.valueOf("1"));
		chargesViewForAccount.setDbtrAuthNeeded(Byte.valueOf("0"));
		chargesViewForAccount.setDbtrUpdtChkId(Short.valueOf("1"));
		chargesViewForAccount.setDbtrLhisTrnNo(Short.valueOf("0"));
		return chargesViewForAccount;

	}

	private static D130008 fetchChargesViewForAccount(int lbrCode, String accountNo, int chgType) {
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D130008.class);
		criteria.add(Restrictions.eq("id.lbrCode", lbrCode));
		criteria.add(Restrictions.eq("id.prdAcctId", accountNo));
		criteria.add(Restrictions.eq("id.chgType", Byte.valueOf(chgType + "")));
		List<D130008> list = criteria.list();
		session.close();
		session = null;
		if (list != null && list.size() > 0)
			return list.get(0);
		return null;
	}

	public static double round2Dec(double a) {
		double result = a * 100;
		result = Math.round(result);
		result = result / 100;
		return result;
	}

	public static D009042 getTotSancLimit(int lbrcode, String prdAcctId, Date operationDate) {
		long l_start = System.currentTimeMillis();
		String queryString = "from D009042 where id.lbrcode=? and id.prdacctid=? and id.efffromdate<=? and  expdate>=? order by id.efffromdate desc";
		Query queryObject = HBUtil.getSessionFactory().openSession().createQuery(queryString);
		queryObject.setLong(0, lbrcode);
		queryObject.setString(1, prdAcctId);
		queryObject.setDate(2, operationDate);
		queryObject.setDate(3, operationDate);
		List list = queryObject.list();
		if (list.size() != 0) {
			long l_end = System.currentTimeMillis();
			logger.debug("Instrumentation :<CustomerQueryDAOImpl.java>:<getTotSancLimit>: " + (l_end - l_start));
			return (D009042) list.get(0);
		} else
			return null;

	}

	public static IMPSTransactionResponse AmountValidation(D009022 account, Double transAmnt, Date operationDate,
			int moduleType, String chkAdhocLmt) throws Exception {
		IMPSTransactionResponse trnxRes = new IMPSTransactionResponse();
		if (moduleType == MSGConstants.OD) {
			D009042 overdraftLimit = DataUtils.getTotSancLimit(account.getId().getLbrCode(),
					account.getId().getPrdAcctId(), operationDate);
			if (null == overdraftLimit) {
				logger.error("Your account limit is expired.");
				trnxRes.setResponse(MSGConstants.ERROR);
				trnxRes.setErrorMessage("Your account limit is expired.");
				return trnxRes;
			}
		}
		double availBal = Double
				.valueOf(Double.valueOf(account.getActClrBalFcy()) - Double.valueOf(account.getTotalLienFcy()));
		logger.error("Available Bal::>>" + availBal);
		logger.error("Transaction Amount::>>>" + transAmnt);
		if (availBal <= transAmnt) {
			logger.error("INSUFFICIENT_FUNDS");
			trnxRes.setResponse(MSGConstants.ERROR);
			trnxRes.setErrorMessage(MSGConstants.INSUFFICIENT_FUNDS);
			return trnxRes;
		}

		trnxRes.setValid(true);
		return trnxRes;
	}

	public static String getStatusDescriptionWS(int status) {
		String statusDesc = "";
		if (status == 0) {
			statusDesc = MSGConstants.ISSUED_WS;
		} else if (status == 1) {
			statusDesc = MSGConstants.STOPPED_WS;
		} else if (status == 2) {
			statusDesc = MSGConstants.PAID_WS;
		} else if (status == 3) {
			statusDesc = MSGConstants.RETURNED_WS;
		} else if (status == 4) {
			statusDesc = MSGConstants.REPRESENTED_WS;
		}

		return statusDesc;
	}

	public static List<Object[]> getLoanAccountDetails(String accountNo, int lbrCode) {
		String sqlquery = "SELECT A.PrdAcctId, C.LongName, A.TotSancLimit, "
				+ "(SELECT InstlAmt FROM D009142 B WHERE "
					+ "A.LBrCode = B.LBrCode AND A.PrdAcctId = B.PrdAcctId AND A.EffFromDate = B.EffFromDate AND B.InstlStartDate in "
					+ "(SELECT max(InstlStartDate) FROM D009142 C WHERE "
						+ "C.LBrCode = B.LBrCode AND C.PrdAcctId = B.PrdAcctId)) AS instlamt, A.ExpDate , C.CustNo ,((D.MainBalLcy*-1) + D.IntPrvdFcy - D.IntPaidFcy + D.OthChgPrvdFcy - D.OthChgPaidFcy +"
				+ " D.PenalPrvdFcy - D.PenalPaidFcy + D.TaxPrvdFcy - D.TaxPaidFcy) BALANCE, A.EffFromDate FROM D009042 A, D009022 C , D030003 D WHERE A.LBrCode = C.LBrCode AND A.PrdAcctId = C.PrdAcctId "
				+ " AND A.LBrCode = D.LBrCode AND A.PrdAcctId = D.PrdAcctId  AND A.LBrCode= ? AND A.PrdAcctId = ? AND C.AcctStat IN ('1','2')";
		
		
		sqlquery = "SELECT A.PrdAcctId, C.LongName, A.TotSancLimit,\r\n" + 
				"(SELECT InstlAmt FROM D009142 B WHERE\r\n" + 
				"A.LBrCode = B.LBrCode AND A.PrdAcctId = B.PrdAcctId AND A.EffFromDate = B.EffFromDate AND B.DisbursedDate in\r\n" + 
				"(SELECT max(C.DisbursedDate) FROM D009142 C WHERE\r\n" + 
				"C.LBrCode = B.LBrCode AND C.PrdAcctId = B.PrdAcctId)) AS instlamt, A.ExpDate , C.CustNo ,((D.MainBalLcy*-1) + D.IntPrvdFcy - D.IntPaidFcy + D.OthChgPrvdFcy - D.OthChgPaidFcy +\r\n" + 
				"D.PenalPrvdFcy - D.PenalPaidFcy + D.TaxPrvdFcy - D.TaxPaidFcy) BALANCE, A.EffFromDate FROM D009042 A, D009022 C , D030003 D WHERE A.LBrCode = C.LBrCode AND A.PrdAcctId = C.PrdAcctId\r\n" + 
				"AND A.LBrCode = D.LBrCode AND A.PrdAcctId = D.PrdAcctId AND A.LBrCode= ? AND A.PrdAcctId = ? AND C.AcctStat IN ('1','2')\r\n" + 
				"";
		Query query = HBUtil.getSessionFactory().openSession().createSQLQuery(sqlquery);
		query.setLong(0, lbrCode);
		query.setString(1, accountNo);
		List<Object[]> insList = query.list();
		return insList;
	}

	public static List<D009022> getDepositeAccountList(Integer lbrCode, String depositeType) {
		Session session = HBUtil.getSessionFactory().openSession();
		Query<D009022> q = null;
		
		
		if (ConfigurationLoader.getParameters(false).getProperty("DATABASE").equalsIgnoreCase("ORACLE"))
			q = session
				.createQuery("from D009022 where CustNo='" + lbrCode + "' and rtrim(substr(PrdAcctId, 0 ,8)) in ('"
						+ depositeType + "') and AcctStat not in(3 ,4 ,5 ,97) order by LBrCode, PrdAcctId ");
		else
			q = session
			.createQuery("from D009022 where CustNo='" + lbrCode + "' and rtrim(substring(PrdAcctId, 0 ,8)) in ('"
					+ depositeType + "') and AcctStat not in(3 ,4 ,5 ,97) order by LBrCode, PrdAcctId ");
		List<D009022> list = q.list();
		session.close();
		session = null;
		if (list != null && list.size() > 0)
			return list;
		return null;
	}

	public static List<D020004> getDepositeReceiptDetails(int lbrcode, String productNo) {
		long l_start = System.currentTimeMillis();
		String prodcode = productNo.substring(0, 24);
		List<D020004> termList = null;
		String queryString = " ";
		if (ConfigurationLoader.getParameters(false).getProperty("DATABASE").equalsIgnoreCase("ORACLE"))
			queryString = "from D020004 where id.lbrCode=?" + "and substr(id.prdAcctId, 1 ,24) LIKE '%" + prodcode
				+ "%'and receiptStatus =51 and mainBalLcy > 0 order by id.prdAcctId";
		else
			queryString = "from D020004 where id.lbrCode=?" + "and substring(id.prdAcctId, 1 ,24) LIKE '%" + prodcode
			+ "%'and receiptStatus =51 and mainBalLcy > 0 order by id.prdAcctId";
		
		Session session = HBUtil.getSessionFactory().openSession();
		Query queryObject = session.createQuery(queryString);
		queryObject.setLong(0, lbrcode);

		long l_end = System.currentTimeMillis();
		logger.error("Instrumentation :<TermDepositReceiptsDAOImpl.java>:<updateSweepLinkSrNoClosure>: "
				+ (l_end - l_start));
		termList = queryObject.list();
		session.close();
		if (termList != null && termList.size() > 0)
			return termList;
		return null;
	}

	public static List<Object[]> getTDProductInterestList(String prdcd, String curcd, Date date) {

		String queryString ="";
		if (ConfigurationLoader.getParameters(false).getProperty("DATABASE").equalsIgnoreCase("ORACLE")) {
			queryString = "SELECT X.prdcd , X.inteffdt , X.SRNO, X.months , X.days ,x.INTRATE , \r\n"  
					+ "	(CASE WHEN (X.MONTHS = 0 AND X.DAYS <> 0 AND X.MONTHS1 = 0 AND X.DAYS1 <> 0) \r\n"  
					+ " THEN X.DAYS||' Day/s to '||X.DAYS1||' Day/s'  \r\n" 
					+ "	WHEN (X.MONTHS = 0 AND X.DAYS <> 0 AND X.MONTHS1 <> 0 AND X.DAYS1 = 0) "
					+ " THEN X.DAYS||' Day/s to '||X.MONTHS1||' Month/s '  \r\n"  
					+ "	WHEN (X.MONTHS = 0 AND X.DAYS <> 0 AND X.MONTHS1 <> 0 AND X.DAYS1 <> 0)"
					+ " THEN X.DAYS||' Day/s to '||X.MONTHS1||' Month/s and'  ||X.DAYS1||' Days'  \r\n"  
					+ "	WHEN (X.MONTHS <> 0 AND X.DAYS = 0 AND X.MONTHS1 = 0 AND X.DAYS1 <> 0) "
					+ " THEN X.MONTHS||' Month/s to '||X.DAYS1||' Day/s'  \r\n" 
					+ "	WHEN (X.MONTHS <> 0 AND X.DAYS = 0 AND X.MONTHS1 <> 0 AND X.DAYS1 = 0) "
					+ " THEN X.MONTHS||' Month/s to '||X.MONTHS1||' Month/s '  \r\n"  
					+ "	WHEN (X.MONTHS <> 0 AND X.DAYS = 0 AND X.MONTHS1 <> 0 AND X.DAYS1 <> 0) "
					+ " THEN X.MONTHS||' Month/s to '||X.MONTHS1||' Month/s and'  ||X.DAYS1||' Day/s'  \r\n"  
					+ "	WHEN (X.MONTHS <> 0 AND X.DAYS <> 0 AND X.MONTHS1 = 0 AND X.DAYS1 <> 0) "
					+ " THEN X.MONTHS||' Month/s and '||X.DAYS||  ' Day/s to '||X.DAYS1||' Day/s'  \r\n" 
					+ "	WHEN (X.MONTHS <> 0 AND X.DAYS <> 0 AND X.MONTHS1 <> 0 AND X.DAYS1 = 0) "
					+ " THEN X.MONTHS||' Month/s and '||X.DAYS||  ' Day/s to '||X.MONTHS1||' Month/s '  \r\n"  
					+ "	WHEN (X.MONTHS <> 0 AND X.DAYS <> 0 AND X.MONTHS1 <> 0 AND X.DAYS1 <> 0) "
					+ " THEN X.MONTHS||' Month/s and '||X.DAYS||  ' Day/s to '||X.MONTHS1||' Month/s and'||X.DAYS1||' Day/s' \r\n"  
					
					+ " WHEN (X.Months <> 0 AND X.Days <> 0 AND X.Months1 = 0 AND X.Days1 = 0)"
					+ " THEN 'Above '||X.Months ||' Month/s and '||X.Days||  ' Day/s ' "
					+ " WHEN ( X.Months <> 0 AND  X.Days = 0 AND  X.Months1 = 0 AND  X.Days1 = 0)"
					+ " THEN 'Above '|| X.Months||' Month/s ' "
					+ " WHEN ( X.Months <> 0 AND  X.Days <> 0 AND  X.Months1 <> 0 AND  X.Days1 <> 0)"
					+ " THEN  X.Months||' Month/s and '|| X.Days||  ' Day/s to '|| X.Months1||' Month/s and'+ X.Days1||' Day/s' "
					+ " WHEN ( X.Months = 0 AND  X.Days <> 0 AND  X.Months1 = 0 AND  X.Days1 = 0)"
					+ " THEN 'Above '|| X.Days||' Day/s ' "
					+ "  ELSE '0' END)DESCR"
					+ " FROM (SELECT A.PrdCd, A.CurCd, A.IntEffDt,  A.SrNo, A.Months, A.Days, A.IntRate,(NVL(B.Months,0)) Months1, (NVL(B.Days,0)) Days1 FROM D020118 A"
					+ " LEFT OUTER JOIN D020118 B ON A.PrdCd=B.PrdCd  AND A.CurCd=B.CurCd AND A.IntEffDt=B.IntEffDt AND A.SrNo=B.SrNo-1 INNER  JOIN D020118 C ON A.PrdCd=C.PrdCd  "
					+ " AND A.CurCd=C.CurCd AND A.IntEffDt=C.IntEffDt AND A.SrNo=C.SrNo WHERE A.PrdCd =? AND A.IntEffDt  =(select max(D.IntEffDt) FROM D020118 D WHERE D.PrdCd = C.PrdCd))X ";
				 	
					
					/*+ "	FROM \r\n" 
					+ "	(SELECT A.PRDCD, A.CURCD, A.INTEFFDT,  A.SRNO, A.MONTHS, A.DAYS, A.INTRATE, B.MONTHS MONTHS1, (B.DAYS-1) DAYS1 FROM D020118 A, D020118 B \r\n" 
					+ "		WHERE A.PRDCD = ? AND A.INTEFFDT = (select max(C.inteffdt) FROM D020118 C WHERE C.PRDCD = B.PRDCD AND C.inteffdt < TO_CHAR(SYSDATE,'DD-MON-YYYY')) \r\n" 
					+ "		AND A.PRDCD = B.PRDCD AND A.CURCD = B.CURCD AND A.INTEFFDT = B.INTEFFDT  AND A.SRNO = B.SRNO-1) X"; */
		}else {
			queryString = " SELECT X.PrdCd , X.IntEffDt , X.SrNo, convert(Varchar,X.Months) AS months , convert(Varchar,X.Days) AS days ,X.IntRate,"
					+ " (CASE WHEN (convert(VARCHAR,X.Months) = 0 AND convert(VARCHAR,X.Days) <> 0 AND convert(VARCHAR,X.Months1) = 0 AND convert(VARCHAR,X.Days1) <> 0) "
					+ " THEN convert(VARCHAR,X.Days) +' Day/s to '+convert(VARCHAR,X.Days1-1)+' Day/s' "
					+ " WHEN (convert(VARCHAR,X.Months) = 0 AND convert(VARCHAR,X.Days) <> 0 AND convert(VARCHAR,X.Months1) <> 0 AND convert(VARCHAR,X.Days1) = 0)"
					+ " THEN convert(VARCHAR,X.Days)+' Day/s to '+convert(VARCHAR,X.Months1)+' Month/s '  "
					+ " WHEN (convert(Varchar,X.Months) = 0 AND X.Days <> 0 AND convert(Varchar,X.Months1) <> 0 AND X.Days1 <> 0)"
					+ " THEN convert(VARCHAR,X.Days)+' Day/s to '+convert(Varchar,X.Months1)+' Month/s and'  +convert(VARCHAR,X.Days1)+' Days'"
					+ " WHEN (convert(Varchar,X.Months) <> 0 AND convert(Varchar,X.Days) = 0 AND convert(Varchar,X.Months1) = 0 AND convert(Varchar,X.Days1) <> 0)"
					+ " THEN convert(Varchar,X.Months)+' Month/s to '+convert(Varchar,X.Days1)+' Day/s'  "
					+ " WHEN (convert(Varchar,X.Months) <> 0 AND convert(Varchar,X.Days) = 0 AND convert(Varchar,X.Months1) <> 0 AND convert(Varchar,X.Days1) = 0)"
					+ " THEN convert(Varchar,X.Months)+' Month/s to '+convert(Varchar,X.Months1)+' Month/s '  "
					+ " WHEN (convert(Varchar,X.Months) <> 0 AND convert(Varchar,X.Days) = 0 AND convert(Varchar,X.Months1) <> 0 AND convert(Varchar,X.Days1) <> 0)"
					+ " THEN convert(Varchar,X.Months)+' Month/s to '+convert(Varchar,X.Months1)+' Month/s and'  +convert(Varchar,X.Days1)+' Day/s'  "
					+ " WHEN (convert(Varchar,X.Months) <> 0 AND convert(Varchar,X.Days) <> 0 AND convert(Varchar,X.Months1) = 0 AND convert(Varchar,X.Days1) <> 0)"
					+ " THEN convert(Varchar,X.Months)+' Month/s and '+convert(Varchar,X.Days)+  ' Day/s to '+convert(Varchar,X.Days1)+' Day/s'  "
					+ " WHEN (convert(Varchar,X.Months) <> 0 AND convert(Varchar,X.Days) <> 0 AND convert(Varchar,X.Months1) <> 0 AND convert(Varchar,X.Days1) = 0)"
					+ " THEN convert(Varchar,X.Months)+' Month/s and '+convert(Varchar,X.Days)+  ' Day/s to '+convert(Varchar,X.Months1)+' Month/s '  "
					+ " WHEN (convert(Varchar,X.Months) <> 0 AND convert(Varchar,X.Days) <> 0 AND convert(Varchar,X.Months1) = 0 AND convert(Varchar,X.Days1) = 0)"
					+ " THEN 'Above '+convert(Varchar,X.Months)+' Month/s and '+convert(Varchar,X.Days)+  ' Day/s ' "
					+ " WHEN (convert(Varchar,X.Months) <> 0 AND convert(Varchar,X.Days) = 0 AND convert(Varchar,X.Months1) = 0 AND convert(Varchar,X.Days1) = 0) THEN 'Above '+convert(Varchar,X.Months)+' Month/s ' "
					+ " WHEN (convert(Varchar,X.Months) <> 0 AND convert(Varchar,X.Days) <> 0 AND convert(Varchar,X.Months1) <> 0 AND convert(Varchar,X.Days1) <> 0)"
					+ " THEN convert(Varchar,X.Months)+' Month/s and '+convert(Varchar,X.Days)+  ' Day/s to '+convert(Varchar,X.Months1)+' Month/s and'+convert(Varchar,X.Days1)+' Day/s'  "
					+ " WHEN ( X.Months = 0 AND  X.Days <> 0 AND  X.Months1 = 0 AND  X.Days1 = 0)"
					+ " THEN 'Above '+convert(Varchar,X.Days)+  ' Day/s ' "
					+ "ELSE '0' END)DESCR"
					//+ " FROM (SELECT A.PrdCd, A.CurCd, A.IntEffDt,  A.SrNo, A.Months, A.Days, A.IntRate, B.Months Months1, (B.Days-1) Days1  FROM D020118 A, D020118 B  WHERE A.PrdCd =? AND A.IntEffDt = (select max(C.IntEffDt) FROM D020118 C WHERE C.PrdCd = B.PrdCd)"
					//+ " AND A.PrdCd = B.PrdCd AND A.CurCd = B.CurCd AND A.IntEffDt = B.IntEffDt  AND A.SrNo = B.SrNo-1) X";
					+" FROM (SELECT A.PrdCd, A.CurCd, A.IntEffDt,  A.SrNo, A.Months, A.Days, A.IntRate,(isnull(B.Months,0)) Months1, (isnull(B.Days,0)) Days1 FROM D020118 A"
					+" LEFT OUTER JOIN D020118 B ON A.PrdCd=B.PrdCd  AND A.CurCd=B.CurCd AND A.IntEffDt=B.IntEffDt AND A.SrNo=B.SrNo-1 INNER  JOIN D020118 C ON A.PrdCd=C.PrdCd  AND A.CurCd=C.CurCd AND A.IntEffDt=C.IntEffDt AND A.SrNo=C.SrNo WHERE A.PrdCd =? AND A.IntEffDt  =(select max(D.IntEffDt) FROM D020118 D WHERE D.PrdCd = C.PrdCd))X ";
		
		}
		logger.error("Query= " + queryString);
		Session session = HBUtil.getSessionFactory().openSession();
		List<Object[]> insList = null;
		try {
			Query q = session.createSQLQuery(queryString);
			q.setParameter(0, prdcd);
			insList = q.list();
			session.close();
			session = null;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			session.close();
		}
		logger.error("insList.size()::>>" + insList.size());
		logger.error("insList.size()::>>>" + insList.size());
		return insList;
	}

	public static D020002 getMinNMaxTdParameters(int lbrCode, String prdCode) {
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D020002.class);
		criteria.add(Restrictions.eq("id.lbrCode", lbrCode));
		criteria.add(Restrictions.eq("id.prdCd", prdCode));
		List<D020002> list = criteria.list();
		session.close();
		session = null;
		criteria = null;
		if (list != null && list.size() > 0)
			return list.get(0);
		return null;
	}

	public static D020002 getTdParameter(Long lbrCodeMinTDParameters, String prdCode) {
		// TODO Auto-generated method stub
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D020002.class);
		criteria.add(Restrictions.eq("id.lbrCode", lbrCodeMinTDParameters.intValue()));
		criteria.add(Restrictions.eq("id.prdCd", prdCode));
		List<D020002> list = criteria.list();
		session.close();
		session = null;
		if (list.size() > 0)
			return list.get(0);
		return null;
	}

	public static List<D946022> getIFSCCodeDetailList(String bankName, String city, String area) {
		long startTime = System.currentTimeMillis();
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D946022.class);
		criteria.add(Restrictions.or(Expression.like("bankName", "%" + bankName.toUpperCase() + "%").ignoreCase(),
				Expression.like("bankShortCd", "%" + bankName.toUpperCase() + "%").ignoreCase()));
		criteria.add(Expression.like("city", "%" + city.toUpperCase() + "%").ignoreCase());
		criteria.add(Expression.like("area", "%" + area.toUpperCase() + "%").ignoreCase());
		List<D946022> ifscCodeMasterList = criteria.list();
		session.close();
		session = null;
		if (ifscCodeMasterList != null && ifscCodeMasterList.size() > 0)
			return ifscCodeMasterList;
		long endTime = System.currentTimeMillis();
		logger.error("Instrumentation :<RtgsDAOImpl.java>:<getIFSCCodeDetailList>: " + (endTime - startTime));
		return ifscCodeMasterList;
	}

	/*
	 * public List<String> getCityListFromBranchMaster(String stateCode) { long
	 * startTime = System.currentTimeMillis(); Session
	 * session=HBUtil.getSessionFactory().openSession(); Criteria criteria =
	 * session.createCriteria(D001003.class); //
	 * criteria.add(Expression.eq("stateCode", stateCode));
	 * criteria.setProjection(Projections.distinct(Projections.property("cityCd"
	 * ))); criteria.addOrder(Order.asc("cityCd")); List<String> cityList
	 * =criteria.list(); session.close(); session=null; if(cityList!=null &&
	 * cityList.size()>0) return cityList; long endTime =
	 * System.currentTimeMillis(); logger.
	 * debug("Instrumentation :<MobileRegistrationDAOImpl.java>:<getCityListFromBranchMaster>: "
	 * + (endTime - startTime)); return null; }
	 */
	/*
	 * public List<String> getCityListFromBranchMaster(String stateCode) { long
	 * startTime = System.currentTimeMillis(); Session
	 * session=HBUtil.getSessionFactory().openSession(); Criteria criteria =
	 * session.createCriteria(D001003.class); //
	 * criteria.add(Expression.eq("stateCode", stateCode));
	 * criteria.setProjection(Projections.distinct(Projections.property("cityCd"
	 * ))); criteria.addOrder(Order.asc("cityCd")); List<String> cityList
	 * =criteria.list(); if(cityList!=null && cityList.size()>0) return cityList;
	 * long endTime = System.currentTimeMillis(); logger.
	 * debug("Instrumentation :<MobileRegistrationDAOImpl.java>:<getCityListFromBranchMaster>: "
	 * + (endTime - startTime)); return null; }
	 */

	public static List<D350007> fetchMobileOTPDomainList(int lbrCode, String prdAcctId, Date otpGenDate) {
		long startTime = System.currentTimeMillis();
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D350007.class);
		criteria.add(Expression.eq("id.lbrCode", lbrCode));
		criteria.add(Expression.eq("id.prdAcctId", prdAcctId));
		if (null != otpGenDate)
			criteria.add(Expression.eq("id.otpgenDate", otpGenDate));
		criteria.addOrder(Order.desc("id.sendingTime"));
		List<D350007> mobileOTPDomainList = criteria.list();
		session.close();
		session = null;
		if (mobileOTPDomainList != null && mobileOTPDomainList.size() > 0)
			return mobileOTPDomainList;
		long endTime = System.currentTimeMillis();
		logger.error("Instrumentation :<MobileRegistrationDAOImpl.java>:<fetchMobileOTPDomainList>: "
				+ (endTime - startTime));
		return null;
	}

	public static D350007 prepareMobileOTPDomainObject(int lbrCode, String prdAcctId, Date otpGenDate, Date sendingTime,
			String encryptedOtp, Date receivingTime, String custNo, String mmid, char status, String transAmount) {
		// TODO Auto-generated method stub
		D350007Id id = new D350007Id();
		id.setLbrCode(lbrCode);
		id.setPrdAcctId(prdAcctId);
		id.setOtpgenDate(otpGenDate);
		id.setSendingTime(sendingTime);

		D350007 mobileOTPDomain = new D350007();
		mobileOTPDomain.setId(id);
		mobileOTPDomain.setOtp(encryptedOtp);
		mobileOTPDomain.setReceivingTime(receivingTime);
		mobileOTPDomain.setCustNo(Integer.valueOf(custNo));
		mobileOTPDomain.setMmid(mmid);
		mobileOTPDomain.setStatus(status);
		mobileOTPDomain.setTransAmt(Double.valueOf(transAmount));
		mobileOTPDomain.setDbtrAddMk(999998);
		mobileOTPDomain.setDbtrAddMb(lbrCode);
		mobileOTPDomain.setDbtrAddMs(0);
		mobileOTPDomain.setDbtrAddMd(DateUtil.convertDateFormat(new Date()));
		mobileOTPDomain.setDbtrAddMt(DateUtil.getFormattedTime(new Date()));
		mobileOTPDomain.setDbtrAddCk(999998);
		mobileOTPDomain.setDbtrAddCb(lbrCode);
		mobileOTPDomain.setDbtrAddCs(0);
		mobileOTPDomain.setDbtrAddCd(DateUtil.convertDateFormat(new Date()));
		mobileOTPDomain.setDbtrAddCt(DateUtil.getFormattedTime(new Date()));
		mobileOTPDomain.setDbtrLupdMk(999998);
		mobileOTPDomain.setDbtrLupdMb(lbrCode);
		mobileOTPDomain.setDbtrLupdMs(0);
		mobileOTPDomain.setDbtrLupdMd(DateUtil.convertDateFormat(new Date()));
		mobileOTPDomain.setDbtrLupdMt(DateUtil.getFormattedTime(new Date()));
		mobileOTPDomain.setDbtrLupdCk(999998);
		mobileOTPDomain.setDbtrLupdCb(lbrCode);
		mobileOTPDomain.setDbtrLupdCs(0);
		mobileOTPDomain.setDbtrLupdCd(DateUtil.convertDateFormat(new Date()));
		mobileOTPDomain.setDbtrLupdCt(DateUtil.getFormattedTime(new Date()));
		mobileOTPDomain.setDbtrTauthDone(1);
		mobileOTPDomain.setDbtrRecStat(1);
		mobileOTPDomain.setDbtrAuthDone(1);
		mobileOTPDomain.setDbtrAuthNeeded(0);
		mobileOTPDomain.setDbtrUpdtChkId(1);
		mobileOTPDomain.setDbtrLhisTrnNo(0);
		return mobileOTPDomain;
	}

	public static void impsKeepTrackOfBadLogins(D350034 custOtherInfo, int maxBadLoginPerDay) {
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		int noOfBadLogin = 1;
		if (custOtherInfo.getBadLoginsDt().compareTo(DateUtil.convertDateFormat(new Date())) == 0) {
			noOfBadLogin = custOtherInfo.getNoOfBadLogins() + 1;
		}
		if (maxBadLoginPerDay > 0) {
			if (noOfBadLogin >= maxBadLoginPerDay) {
				custOtherInfo.setStatus(2);
			}
		}
		/**
		 * Update noofbadlogins, badloginsdt as current date in CustOtherInfo
		 */
		custOtherInfo.setNoOfBadLogins(noOfBadLogin);
		custOtherInfo.setBadLoginsDt(DateUtil.convertDateFormat(new Date()));
		try {
			session.saveOrUpdate(custOtherInfo);
			t.commit();
			session.close();
			session = null;
			t = null;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			session.close();
			session = null;
			t = null;
		}
	}

	public static int updateBadLoginsWS(int noOfBadLogin, String custNo, int status) {
		long startTime = System.currentTimeMillis();
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		String hql = "update D350034 set status = ?, noOfBadLogins = ? where custNo = ?";
		Query query = session.createQuery(hql);
		query.setLong(0, status);
		query.setLong(1, noOfBadLogin);
		query.setString(2, custNo);
		int row = query.executeUpdate();
		session.close();
		session = null;
		t = null;
		if (row > 0) {
			long endTime = System.currentTimeMillis();
			logger.error(
					"Instrumentation :<MobileRegistrationDAOImpl.java>:<updateBadLoginsWS>: " + (endTime - startTime));
			return row;
		}

		long endTime = System.currentTimeMillis();
		logger.error("Instrumentation :<MobileRegistrationDAOImpl.java>:<updateBadLoginsWS>: " + (endTime - startTime));
		return 0;
	}

	public static D010054 getAccountAddress(int lbrCode, String accNo) {
		D010054 d010054 = null;
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D010054.class);
		criteria.add(Restrictions.eq("id.lbrCode", lbrCode));
		criteria.add(Restrictions.eq("id.prdAcctId", accNo.trim()));
		List<D010054> list = criteria.list();
		logger.error("D010054 list::>>" + list);
		logger.error("D010054 list::>>" + list.size());
		session.close();
		session = null;
		if (list == null || list.isEmpty())
			return null;
		else
			return list.get(0);

	}

	public static BranchDetailsResponse getCityState(String stateCode, String cityOrState) {
		logger.error("cityOrState:>>>" + cityOrState);
		logger.error("stateCode::>>" + stateCode);
		BranchDetailsResponse cityStateRes = new BranchDetailsResponse();
		if (cityOrState.equalsIgnoreCase("S")) {
			List<D500025> stateList = new LookUpServiceImpl().getState();
			Map<String, String> stateMap = new TreeMap<String, String>();
			if (null == stateList || stateList.size() < 1) {
				cityStateRes.setResponse(MSGConstants.ERROR);
				cityStateRes.setErrorMessage(MSGConstants.STATE_LIST_NOT_FOUND);
				return cityStateRes;
			}
			for (D500025 stateMaster : stateList)
				stateMap.put(stateMaster.getStateCd().trim(), stateMaster.getStateDesc().trim());
			if (stateMap == null || stateMap.isEmpty()) {
				cityStateRes.setResponse(MSGConstants.ERROR);
				cityStateRes.setErrorMessage("No record found for State.");
				return cityStateRes;
			}
			cityStateRes.setResponse(MSGConstants.SUCCESS);
			cityStateRes.setErrorMessage(MSGConstants.SUCCESS_MSG);
			cityStateRes.setMapOutput(stateMap);
		} else if (cityOrState.equalsIgnoreCase("C")) {
			List<String> cityList = DataUtils.getCityListFromBranchMaster(stateCode);
			if (null == cityList || cityList.isEmpty()) {
				logger.error("No record found for stateCode :: " + stateCode);
				cityStateRes.setResponse(MSGConstants.ERROR);
				cityStateRes.setErrorMessage("No record found for stateCode :: " + stateCode);
				return cityStateRes;
			}
			Map<String, String> cityMap = new LinkedHashMap();
			try {

				for (String cityCode : cityList) {
					D500028 cityDesc = null;
					if (!cityCode.trim().equalsIgnoreCase(""))
						cityDesc = DataUtils.getDetails(cityCode);
					if (cityDesc != null)
						cityMap.put(cityCode.trim(), cityDesc.getPlaceCdDesc().trim());
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			logger.error("cityMap::>>>" + cityMap);
			cityStateRes.setResponse(MSGConstants.SUCCESS);
			cityStateRes.setErrorMessage(MSGConstants.SUCCESS_MSG);
			cityStateRes.setMapOutput(cityMap);
		}
		return cityStateRes;
	}

	public static List<String> getCityListFromBranchMaster(String stateCode) {
		long startTime = System.currentTimeMillis();
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria detachedCriteria = session.createCriteria(D001003.class);
		detachedCriteria.add(Expression.eq("stateCode", stateCode));
		detachedCriteria.setProjection(Projections.distinct(Projections.property("cityCd")));
		detachedCriteria.addOrder(Order.asc("cityCd"));
		List<String> cityList = detachedCriteria.list();
		session.close();
		session = null;
		long endTime = System.currentTimeMillis();
		logger.error("cityList:::>>>" + cityList);
		logger.error("Instrumentation :<MobileRegistrationDAOImpl.java>:<getCityListFromBranchMaster>: "
				+ (endTime - startTime));
		if (cityList != null || cityList.size() > 0)
			return cityList;
		return null;
	}

	public static D500028 getDetails(String placecd) {
		long l_start = System.currentTimeMillis();
		while (placecd.length() < 3) {
			placecd = placecd + " ";
		}
		D500028 placeMaster = null;
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D500028.class);
		criteria.add(Restrictions.eq("placeCd", placecd));
		List<D500028> list = criteria.list();
		logger.error("list::>>>" + list);
		logger.error("list.size()::>>" + list.size());
		session.close();
		session = null;
		/*
		 * String queryString = "from PlaceMaster where placecd=? "; Query queryObject =
		 * HBUtil.getSessionFactory().openSession().createQuery(queryString);
		 * queryObject.setString(0, placecd); List<PlaceMaster> placeMasterList =
		 * queryObject.list(); if (placeMasterList != null&& placeMasterList.size() !=
		 * 0) { placeMaster = placeMasterList.get(0); return placeMaster; }
		 */
		long l_end = System.currentTimeMillis();
		logger.debug("Instrumentation :<PlaceMasterDAOImpl.java>:<getDetails>: " + (l_end - l_start));
		if (list != null && !list.isEmpty())
			return list.get(0);
		else
			return null;
	}

	public static BranchDetailsResponse getATMBranchLocator(String cityCode, String stateCode) {
		BranchDetailsResponse branchDetailsRes = new BranchDetailsResponse();
		List<D001003> branchMasterList = DataUtils.getBranchMasterList(cityCode, stateCode);
		logger.error("branchMasterList::>>>" + branchMasterList);
		if (null == branchMasterList || branchMasterList.isEmpty()) {
			branchDetailsRes.setResponse(MSGConstants.ERROR);
			branchDetailsRes.setErrorMessage(MSGConstants.RECORD_NOT_FOUND);
			return branchDetailsRes;
		}
		List<BranchDetailsResponse> branchDetailsResponseList = new ArrayList<BranchDetailsResponse>();
		Long bankRBICode = Long.valueOf(ConfigurationLoader.getParameters(false).getProperty("bankRBICode"));
		logger.error("bankRBICode:>>>" + bankRBICode);
		for (D001003 branchMaster : branchMasterList) {
			BranchDetailsResponse detailsResponse = new BranchDetailsResponse();
			D500028 cityDesc = DataUtils.getDetails(cityCode.trim());
			logger.error("cityDesc:::>>" + cityDesc);
			if (null == cityDesc) {
				continue;
			}

			D946022 ifscMaster = DataUtils.getIFSCCodeMasterDetail(bankRBICode,
					Long.valueOf(branchMaster.getPbrCode()));
			logger.error("ifscMaster:::>>" + ifscMaster);
			if (null == ifscMaster) {
				branchDetailsRes.setResponse(MSGConstants.ERROR);
				branchDetailsRes.setErrorMessage(MSGConstants.IFSC_CODE_NOT_FOUND);
				// return branchDetailsRes;
			}
			if (ifscMaster != null) {
				String bankName = ifscMaster.getBankName() + ", " + branchMaster.getAdd1() + ", "
						+ branchMaster.getAdd2() + ", " + branchMaster.getAdd3() + ", " + cityDesc.getPlaceCdDesc()
						+ ", " + branchMaster.getPinCode();
				logger.error("bankName::>>" + bankName);
				detailsResponse.setBankName(bankName);
				detailsResponse.setCity(cityDesc.getPlaceCdDesc());
				detailsResponse.setContactNo(branchMaster.getTele1() + "");
				// detailsResponse.setContactPerson(branchMaster.getContactPerson());
				// detailsResponse.setEmail(branchMaster.getEmailid());
				// detailsResponse.setIfscCode(ifscMaster.getIfsccd());
				// detailsResponse.setLanguage(branchMaster.getLanguage());
				// detailsResponse.setLatitude(branchMaster.getLatitude()+"");
				// detailsResponse.setLongitude(branchMaster.getLongitude()+"");
				// detailsResponse.setTelephone(branchMaster.getTele2()+"");
				// detailsResponse.setTitle(branchMaster.getTitle());
				branchDetailsResponseList.add(detailsResponse);
			}
		}
		branchDetailsRes.setResponse(MSGConstants.SUCCESS);
		branchDetailsRes.setErrorMessage(MSGConstants.SUCCESS_MSG);
		branchDetailsRes.setBranchDetailsResponseList(branchDetailsResponseList);
		return branchDetailsRes;
	}

	public static List<D001003> getBranchMasterList(String cityCode, String stateCode) {
		long startTime = System.currentTimeMillis();
		logger.error("cityCode:>>>" + cityCode);
		logger.error("stateCode::>>" + stateCode);
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria detachedCriteria = session.createCriteria(D001003.class);
		detachedCriteria.add(Restrictions.eq("stateCode", stateCode));
		detachedCriteria.add(Restrictions.eq("cityCd", cityCode));
		// detachedCriteria.add(Expression.or(Expression.like("faxNo2", "Z%"),
		// Expression.eq("faxNo2", "SME")));
		detachedCriteria.addOrder(Order.asc("name"));
		List<D001003> branchMasterList = detachedCriteria.list();
		logger.error("branchMasterList:::>>>" + branchMasterList);
		session.close();
		session = null;
		long endTime = System.currentTimeMillis();
		logger.debug(
				"Instrumentation :<MobileRegistrationDAOImpl.java>:<getBranchMasterList>: " + (endTime - startTime));
		if (branchMasterList == null || branchMasterList.size() < 1)
			return null;
		return branchMasterList;
	}

	public static D946022 getIFSCCodeMasterDetail(Long bankRBICode, Long pbrcode) {
		long startTime = System.currentTimeMillis();
		logger.error("bankRBICode::>>>" + bankRBICode);
		logger.error("pbrcode::>>>" + pbrcode);
		D946022 ifscCodeMaster = null;
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria detachedCriteria = session.createCriteria(D946022.class);
		detachedCriteria.add(Restrictions.eq("bankRbicd", Short.valueOf(bankRBICode + "")));
		detachedCriteria.add(Restrictions.eq("branchRbicd", Short.valueOf(pbrcode + "")));//
		List<D946022> ifscCodeMasterList = detachedCriteria.list();
		logger.error("ifscCodeMasterList::>>>" + ifscCodeMasterList);
		logger.error("ifscCodeMasterList.size()::>>>" + ifscCodeMasterList.size());
		session.close();
		session = null;
		if (ifscCodeMasterList.size() > 0) {
			ifscCodeMaster = ifscCodeMasterList.get(0);
			return ifscCodeMaster;
		}
		long endTime = System.currentTimeMillis();
		logger.debug("Instrumentation :<RtgsDAOImpl.java>:<getIFSCCodeMasterDetail>: " + (endTime - startTime));
		return ifscCodeMaster;
	}

	public void calculateMaturityValueForTDProducts(D020004 termDepositReceipts, Long noOfDays, D020002 parameter,
			String cumintyn, String instorprinc, String intfreq, Calendar asOfDate, Calendar originalMatDate,
			Calendar matDate) {
		Long remainderRemainingMonths = 0l;
		Long noOfCompletedMonths = 0l;
		Long noOfMonths = 0l;
		Double intAmountDouble = 0.0;
		Double allQuarter = 0.0;
		Double oneQuarter = 0.0;
		Double forMonth = 0.0;
		Double forDay = 0.0;
		Long noOfQuarter = 0l;
		long dateDiff = 0;
		/***************************************************************
		 * FOR FQ and FR RECIEPTS
		 **************************************************************/
		D009021 product = DataUtils.getProductMaster(termDepositReceipts.getId().getLbrCode() + "",
				parameter.getId().getPrdCd());
		if (noOfDays < parameter.getShortTermDays() + 1 && !("M".equals(intfreq))) {
			intAmountDouble = (termDepositReceipts.getInstOrPrincAmt() * (termDepositReceipts.getIntRate())
					+ termDepositReceipts.getOffSetRate()) * noOfDays / 36500;
			if ("N".equalsIgnoreCase(cumintyn) && "P".equalsIgnoreCase(instorprinc)) {
				// termDepositReceipts.setIntEarnedAmount(Math.round(intAmountDouble)
				// + 0.0);
				noOfMonths = noOfDays / 30;
				remainderRemainingMonths = noOfMonths % 3;
				if (remainderRemainingMonths > 0) {
					noOfCompletedMonths = noOfMonths - remainderRemainingMonths;
					noOfQuarter = noOfCompletedMonths / 3;
					allQuarter = (termDepositReceipts.getInstOrPrincAmt()
							* (termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate())
							* noOfCompletedMonths) / 1200;
				} else {
					noOfQuarter = noOfMonths / 3;
					allQuarter = (termDepositReceipts.getInstOrPrincAmt()
							* (termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate()) * noOfMonths)
							/ 1200;
				}
				oneQuarter = allQuarter / noOfQuarter;
				if ("H".equals(intfreq)) {
					termDepositReceipts.setPeriodicIntAmt(Math.round(oneQuarter * 2.0) + 0.0);
				} else {
					termDepositReceipts.setPeriodicIntAmt(Math.round(oneQuarter) + 0.0);
				}
				Double matvalDouble = termDepositReceipts.getInstOrPrincAmt();
				termDepositReceipts.setMatVal(matvalDouble);
			} else {
				Double matvalDouble = termDepositReceipts.getInstOrPrincAmt() + Math.round(intAmountDouble);
				// termDepositReceipts.setIntEarnedAmount(Math.round(intAmountDouble)+0.0);
				termDepositReceipts.setMatVal(matvalDouble);
			}
		} else {
			if ("N".equalsIgnoreCase(cumintyn) && "P".equalsIgnoreCase(instorprinc)) {
				if ("M".equals(intfreq)) {
					Double periodicInterest = 0.0;
					Double allMonthsInterest = 0.0;
					Double interestEarned = 0.0;
					if (noOfDays < parameter.getShortTermDays() + 1) {
						// Double discountIntrate = (this.getIntrate() +
						// this.getOffsetrate())/(1 + ((this.getIntrate() +
						// this.getOffsetrate())/36600));
						if (parameter.getClIntCalcType() == 6) {
							periodicInterest = ((termDepositReceipts.getInstOrPrincAmt()
									* (termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate())) / 1200);
							interestEarned = (termDepositReceipts.getInstOrPrincAmt()
									* (termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate())
									* noOfDays) / 36500;
						} else {
							periodicInterest = (termDepositReceipts.getInstOrPrincAmt()
									* (termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate()))
									/ ((termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate()) + 1200);
							interestEarned = (periodicInterest) * noOfDays / 30;
						}
						// interestEarned = (noOfDays * this.getInstorprincamt()
						// * (this.getIntrate() + this.getOffsetrate()))/36500;
						// commented above line and added belo for calc as
						// discussed with sanjay sir on 17 mar 2012
						// termDepositReceipts.setIntEarnedAmoun(Double.valueOf(twoDecPlaces.format(interestEarned)));
						termDepositReceipts.setPeriodicIntAmt(Double.valueOf(twoDecPlaces.format(periodicInterest)));
						termDepositReceipts.setMatVal(
								Double.valueOf(twoDecPlaces.format(termDepositReceipts.getInstOrPrincAmt())));
					} else {
						if (parameter.getClIntCalcType() == 6) {
							periodicInterest = (termDepositReceipts.getInstOrPrincAmt()
									* (termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate())) / 1200;
						} else {
							periodicInterest = (termDepositReceipts.getInstOrPrincAmt()
									* (termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate()))
									/ ((termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate()) + 1200);
						}
						if (termDepositReceipts.getNoOfMonths() > 0 && termDepositReceipts.getNoOfDays() == 0) {
							allMonthsInterest = periodicInterest * termDepositReceipts.getNoOfMonths();
							forDay = 0.0;
						} else {
							noOfMonths = noOfDays / 30;
							asOfDate.add(Calendar.MONTH, noOfMonths.intValue());
							while (asOfDate.after(originalMatDate)) {
								asOfDate.setTime(termDepositReceipts.getAsOffdate());
								noOfMonths = noOfMonths - 1;
								asOfDate.add(Calendar.MONTH, noOfMonths.intValue());
							}
							asOfDate.setTime(termDepositReceipts.getAsOffdate());
							allMonthsInterest = periodicInterest * noOfMonths;
							asOfDate.add(Calendar.MONTH, noOfMonths.intValue());
							// asOfDate.add(Calendar.DAY_OF_MONTH, -1);
							if (asOfDate.after(matDate)) {
								dateDiff = 0;
							} else {
								dateDiff = SwiftcoreDateUtil.getDateDiff(asOfDate, matDate);
							}
							if (asOfDate.compareTo(matDate) == 0) {
								dateDiff = 1;
							}
							if (parameter.getClIntCalcType() == 6) {
								forDay = (termDepositReceipts.getInstOrPrincAmt()
										* (termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate())
										* dateDiff) / 36500;
							} else {
								forDay = (periodicInterest * dateDiff) / 30;
							}
						}
						interestEarned = allMonthsInterest + forDay;

						// termDepositReceipts.setIntEarnedAmount((Double.valueOf(twoDecPlaces.format(interestEarned)))*1D);
						termDepositReceipts.setPeriodicIntAmt(Double.valueOf(twoDecPlaces.format(periodicInterest)));
						// Maturity Value
						termDepositReceipts.setMatVal(
								Double.valueOf(twoDecPlaces.format(termDepositReceipts.getInstOrPrincAmt())));
					}

				} else {
					noOfCompletedMonths = noOfDays / 30;
					asOfDate.add(Calendar.MONTH, noOfCompletedMonths.intValue());
					while (asOfDate.after(originalMatDate)) {
						asOfDate.setTime(termDepositReceipts.getAsOffdate());
						noOfCompletedMonths = noOfCompletedMonths - 1;
						asOfDate.add(Calendar.MONTH, noOfCompletedMonths.intValue());
					}
					asOfDate.setTime(termDepositReceipts.getAsOffdate());
					remainderRemainingMonths = noOfCompletedMonths % 3;
					if (remainderRemainingMonths > 0) {
						noOfCompletedMonths = noOfCompletedMonths - remainderRemainingMonths;
					}
					noOfQuarter = noOfCompletedMonths / 3;
					allQuarter = (termDepositReceipts.getInstOrPrincAmt()
							* (termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate())
							* noOfCompletedMonths) / 1200;
					oneQuarter = allQuarter / noOfQuarter;
					asOfDate.add(Calendar.MONTH, noOfCompletedMonths.intValue());
					// asOfDate.add(Calendar.DAY_OF_MONTH, -1);
					if (asOfDate.after(matDate)) {
						dateDiff = 0;
					} else {
						dateDiff = SwiftcoreDateUtil.getDateDiff(asOfDate, matDate);
					}
					if (asOfDate.compareTo(matDate) == 0) {
						dateDiff = 1;
					}
					forDay = (((termDepositReceipts.getInstOrPrincAmt()
							* (termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate())) * (dateDiff))
							/ 36500);
					intAmountDouble = allQuarter + forDay;
					Long fqSum = Math.round(intAmountDouble);
					Long periodicInt = 0L;
					// termDepositReceipts.setIntEarnedAmount(fqSum.doubleValue());
					if ("H".equals(intfreq)) {
						if (product.getCurCd().equalsIgnoreCase(MSGConstants.INR)) {
							Double periodicAmount = oneQuarter * 2;
							periodicInt = Math.round(periodicAmount);
							termDepositReceipts.setPeriodicIntAmt(periodicInt.doubleValue());
						} else {
							/***************************************************************
							 * FOR NRFUS
							 **************************************************************/
							asOfDate.setTime(termDepositReceipts.getAsOffdate());
							dateDiff = SwiftcoreDateUtil.getDateDiff(asOfDate, matDate);
							forDay = (((termDepositReceipts.getInstOrPrincAmt()
									* (termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate()))
									* (dateDiff)) / (parameter.getNoOfDaysInYear() * 100));
							// termDepositReceipts.setIntEarnedAmount(new
							// BigDecimal(forDay).setScale(2,
							// RoundingMode.HALF_UP).doubleValue());
							termDepositReceipts.setPeriodicIntAmt(0.0);
						}
					} else {
						periodicInt = Math.round(oneQuarter);
						termDepositReceipts.setPeriodicIntAmt(periodicInt.doubleValue());
					}

					// Maturity Value
					Double matval = termDepositReceipts.getInstOrPrincAmt();
					Double matvalDouble = matval.doubleValue();
					termDepositReceipts.setMatVal(matvalDouble);
				}
			} else if ("Y".equalsIgnoreCase(cumintyn) && "P".equalsIgnoreCase(instorprinc)) {
				Double power = 0.0;

				noOfCompletedMonths = noOfDays / 30;
				asOfDate.add(Calendar.MONTH, noOfCompletedMonths.intValue());
				while (asOfDate.after(originalMatDate)) {
					asOfDate.setTime(termDepositReceipts.getAsOffdate());
					noOfCompletedMonths = noOfCompletedMonths - 1;
					asOfDate.add(Calendar.MONTH, noOfCompletedMonths.intValue());
				}
				asOfDate.setTime(termDepositReceipts.getAsOffdate());
				remainderRemainingMonths = noOfCompletedMonths % 3;
				if (remainderRemainingMonths > 0) {
					noOfCompletedMonths = noOfCompletedMonths - remainderRemainingMonths;
				}
				noOfQuarter = noOfCompletedMonths / 3;
				power = Math.pow((1 + ((termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate()) / 400)),
						noOfQuarter);
				allQuarter = termDepositReceipts.getInstOrPrincAmt() * power;
				asOfDate.add(Calendar.MONTH, noOfCompletedMonths.intValue());

				if (asOfDate.after(matDate)) {
					dateDiff = 0;
				} else {
					dateDiff = SwiftcoreDateUtil.getDateDiff(asOfDate, matDate);
				}
				if (asOfDate.compareTo(matDate) == 0) {
					dateDiff = 1;
				}
				forDay = (((allQuarter * (termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate()))
						* (dateDiff)) / 36500);

				intAmountDouble = allQuarter + forDay;
				// Long fqSum = Math.round(intAmountDouble);
				// termDepositReceipts.setIntEarnedAmount(Math.round(intAmountDouble
				// - termDepositReceipts.getInstOrPrincAmt())*1D);

				if ("H".equals(intfreq)) {
					if (!product.getCurCd().equalsIgnoreCase(MSGConstants.INR)) {
						/***************************************************************
						 ***************************************************************/
						Long days = 0L;
						Long noofhfyr = 0L;
						Double hfyrIntrate = 0.0;
						Double noofhfyrInt = 0.0;
						asOfDate.setTime(termDepositReceipts.getAsOffdate());
						dateDiff = SwiftcoreDateUtil.getDateDiff(asOfDate, matDate);
						noofhfyr = dateDiff / 180;
						days = dateDiff % 180;

						hfyrIntrate = (termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate()) / 2;
						power = Math.pow((1 + ((hfyrIntrate) / 100)), noofhfyr);
						noofhfyrInt = termDepositReceipts.getInstOrPrincAmt() * power;
						forDay = (noofhfyrInt * days
								* (termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate()))
								/ (parameter.getNoOfDaysInYear() * 100);
						noofhfyrInt = noofhfyrInt + forDay;
						// termDepositReceipts.setIntEarnedAmount(new
						// BigDecimal(noofhfyrInt-termDepositReceipts.getInstorprincamt()).setScale(2,
						// RoundingMode.HALF_UP).doubleValue());
						termDepositReceipts.setPeriodicIntAmt(0.0);
						/***************************************************************
						 ***************************************************************/
					}
				}

				// this.setMatval(fqSum.doubleValue());
				termDepositReceipts.setMatVal(termDepositReceipts.getInstOrPrincAmt());
			} /***************************************************************
				 * RD CODE FOR RECEIPT CREATION
				 **************************************************************/
			else if ("Y".equalsIgnoreCase(cumintyn) && "I".equalsIgnoreCase(instorprinc)) {
				Double rateOfInterest = 0.0;
				Double factor1 = 0.0;
				Double factor2 = 0.0;
				Double interestEarnedAmt = 0.0;

				/*******************************************************
				 * adding the offset to the receipt rate of interest
				 ******************************************************/
				rateOfInterest = (termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate());
				noOfCompletedMonths = noOfDays / 30;
				asOfDate.add(Calendar.MONTH, noOfCompletedMonths.intValue());
				while (asOfDate.after(originalMatDate)) {
					asOfDate.setTime(termDepositReceipts.getAsOffdate());
					noOfCompletedMonths = noOfCompletedMonths - 1;
					asOfDate.add(Calendar.MONTH, noOfCompletedMonths.intValue());
				}
				asOfDate.setTime(termDepositReceipts.getAsOffdate());
				remainderRemainingMonths = noOfCompletedMonths % 3;
				if (remainderRemainingMonths > 0) {
					noOfCompletedMonths = noOfCompletedMonths - remainderRemainingMonths;
				}
				noOfQuarter = noOfCompletedMonths / 3;
				factor1 = (5 * rateOfInterest * 4) / 1200;
				factor1 = factor1 + ((5 * rateOfInterest * 2) / (1200 + rateOfInterest));
				factor2 = factor1;

				/*******************************************************
				 * code to calculate the interest for completed quarters
				 ******************************************************/
				for (int i = 1; i <= noOfQuarter.intValue() - 1; i++) {
					factor2 = factor2 + factor1 + ((((3 * 5 * i) + factor2) * rateOfInterest * 3) / 1200);
				}
				factor1 = (termDepositReceipts.getInstOrPrincAmt() / 5) * ((5 * noOfQuarter * 3) + factor2);
				interestEarnedAmt = factor1;
				/*******************************************************
				 * code to calculate interest for broken months in incomplete quarters
				 ******************************************************/
				if (remainderRemainingMonths > 0) {
					/***************************************************
					 * looping through remainder months and compunding the interest
					 **************************************************/
					for (int j = 1; j <= remainderRemainingMonths.intValue(); j++) {
						factor1 = factor1 + termDepositReceipts.getInstOrPrincAmt();
						interestEarnedAmt = interestEarnedAmt + termDepositReceipts.getInstOrPrincAmt()
								+ ((factor1 * rateOfInterest) / 1200);
					}
				}
				termDepositReceipts.setMatVal(Double.valueOf(twoDecPlaces.format(interestEarnedAmt)));
				if (termDepositReceipts.getNoOfMonths() > 0) {
					// termDepositReceipts.setIntEarnedAmount(Double.valueOf(Math.round(termDepositReceipts.getMatval()
					// - (termDepositReceipts.getNoofmonths() *
					// termDepositReceipts.getInstorprincamt()))));
					termDepositReceipts
							.setMatVal(termDepositReceipts.getNoOfMonths() * termDepositReceipts.getInstOrPrincAmt());
				}
			}
		}

	}
	/*
	 * public D009500 runProjectionForSingleReceipt(D020004 termDepositReceipts,
	 * D020002 tdParamForeachReceipt,D009011 customer) { try{ Double tdFMsum = 0.0;
	 * Double interestProjected = 0.0; Double tdsOnEachReceipt = 0.0; Double
	 * tdsPercentage = 0.0; Double vouchersOnOpdate = 0.0; Date startDate1 = new
	 * Date(); Date endDate1 = new Date(); Calendar asoffDate =
	 * Calendar.getInstance(); Calendar startDate = Calendar.getInstance(); Calendar
	 * origStartDate = Calendar.getInstance(); Date operationDate = new
	 * Date();//termDepositReceipts.getOperationDate(); Calendar endDate =
	 * Calendar.getInstance(); Calendar operDate = Calendar.getInstance();
	 * operDate.setTime(operationDate); List<D020004> todisplay = new
	 * ArrayList<D020004>();
	 * 
	 * startDate =
	 * SwiftcoreDateUtil.getFinancialYearStartDateByOperationDate(operDate); endDate
	 * = SwiftcoreDateUtil.getFinancialYearEndDateByOperationDate(operDate);
	 * startDate1 = startDate.getTime(); endDate1 = endDate.getTime(); tdsPercentage
	 * = customer.getTdsPercentage();
	 * 
	 * Calendar originalAsoffDate = Calendar.getInstance(); Calendar originalMatDate
	 * = Calendar.getInstance(); Calendar oriMaturityDate = Calendar.getInstance();
	 * Calendar matDate = Calendar.getInstance(); Calendar lastInterestRunDate =
	 * Calendar.getInstance(); Calendar cbldate = Calendar.getInstance(); Calendar
	 * quarterStartDate = Calendar.getInstance(); Calendar quarterEndDate =
	 * Calendar.getInstance(); Calendar ogDate = Calendar.getInstance();
	 * startDate.setTime(startDate1); ogDate.setTime(startDate1);
	 * origStartDate.setTime(startDate1);
	 * lastInterestRunDate.set(startDate.get(Calendar.YEAR), Calendar.MARCH, 31); //
	 * ITermDepositReceiptsService receiptsService = (ITermDepositReceiptsService)
	 * ServiceFinder.findBean("termDepositReceiptsService");
	 *//***************************************************************************************
		 * iterating through the list of accounts to find all receipts associated with
		 * each customer account
		 **************************************************************************************/
	/*
	 * 
	 * try { D009500 brwiseCustTDSFile = null; D010014 closingBalance = new
	 * D010014(); D010014 closingBalanceForPremature = new D010014(); D010014
	 * closingBalanceOnOprDate = new D010014(); Double amtForTDUNITWD = 0.0; Double
	 * lastInterestAmt = 0.0; Calendar dateOfTDUNITWD = Calendar.getInstance(); Date
	 * maxCblDateBeforeOpdate = new Date(); endDate.setTime(endDate1); //
	 * termDepositReceipts.setIntrateCalc(termDepositReceipts.getIntRate() +
	 * termDepositReceipts.getOffSetRate());
	 * asoffDate.setTime(termDepositReceipts.getAsOffdate());
	 * originalAsoffDate.setTime(termDepositReceipts.getAsOffdate());
	 * originalMatDate.setTime(termDepositReceipts.getMatDate());
	 * oriMaturityDate.setTime(termDepositReceipts.getMatDate());
	 * originalMatDate.add(Calendar.DAY_OF_MONTH, -1); Long daysDiffForReceipt =
	 * SwiftcoreDateUtil.getDateDiff(originalAsoffDate, originalMatDate); boolean
	 * newReceipt = false; boolean premature = false; boolean isMaturing = false;//
	 * to check if the receipt matures in the boolean isKdProduct = false; boolean
	 * isRdProduct = false; boolean isClosingBalanceNull = false; Double
	 * remainingInterestForMDS = 0.0; Double interestBeforeFinYear = 0.0; Double
	 * periodicInterest = 0.0; BigDecimal periodInterest = new BigDecimal(0.0);
	 * Double kdInterest = 0.0; boolean isKDreceiptCblNull = false; // financial
	 * year if (asoffDate.before(startDate)) { if
	 * (asoffDate.getTime().before(termDepositReceipts.getCertDate()) &&
	 * termDepositReceipts.getCertDate().after(startDate.getTime())) {
	 * origStartDate.setTime(termDepositReceipts.getCertDate()); newReceipt = true;
	 * startDate.setTime(asoffDate.getTime()); } else { newReceipt = false; }
	 *//***********************************************************************
		 * Get the interest Voucher before startdate
		 **********************************************************************/
	/*
	 * List<Object[]> lastInterestDetails =
	 * DataUtils.getLastInterestVoucherDateAndAmt(Long.valueOf(
	 * termDepositReceipts.getId().getLbrCode()),
	 * termDepositReceipts.getId().getPrdAcctId(), startDate1); if
	 * (lastInterestDetails.size() == 0) { interestBeforeFinYear =
	 * DataUtils.calculateInterestForTDProducts(termDepositReceipts,
	 * tdParamForeachReceipt,
	 * startDate1,endDate1,1L,termDepositReceipts.getInstorprincamt(),false, false,
	 * daysDiffForReceipt,""); } } else if (asoffDate.after(startDate)) {
	 * startDate.setTime(asoffDate.getTime());
	 * origStartDate.setTime(asoffDate.getTime()); newReceipt = true; }
	 *//***************************************************************************
		 * Condition to check if the receipt matures before the anual start date
		 **************************************************************************/
	/*
	 * 
	 * if (termDepositReceipts.getMatDate().before(startDate.getTime())) {
	 * 
	 * } else { long noOfQuarters = 0l; Long noOfMonths = 0l; Long daysDiff = 0l;
	 * Long monthDiff = 0l; Long daysDiffKd = 0l; double interestForKDLastFincal =
	 * 0.0; long daysDiffForUnitwd = 0l; long remainderRemainingMonths = 0l;
	 *//***********************************************************************
		 * Condition to check if the receipt matures before the anual end date
		 **********************************************************************/
	/*
	 * if(termDepositReceipts.getClosedDate() != null){ if
	 * ((!(DateUtil.getFormattedDate(termDepositReceipts.getClosedDate()).equals
	 * (DateUtil.getDateFromString("1990-01-01")))) &&
	 * (termDepositReceipts.getCloseddate().compareTo(termDepositReceipts.
	 * getMatDate()) < 0)) { if
	 * (termDepositReceipts.getClosedDate().compareTo(startDate.getTime()) >= 0 &&
	 * termDepositReceipts.getClosedDate().before(endDate.getTime())) {
	 * termDepositReceipts.setMatDate(termDepositReceipts.getClosedDate());
	 * endDate.setTime(termDepositReceipts.getClosedDate()); premature = true; if
	 * (tdParamForeachReceipt.getUnitsAllowYn()=='Y') { closingBalanceForPremature =
	 * DataUtils.getClosingBalanceAfterClosure(termDepositReceipts.getId().
	 * getLbrcode(), termDepositReceipts.getId().getPrdacctid(),
	 * termDepositReceipts.getCloseddate(), premature); } } } }
	 * 
	 * closingBalanceOnOprDate =
	 * termDepositReceiptsDao.getClosingBalanceForLastInterestRunDateNew(
	 * termDepositReceipts, operDate, newReceipt); if
	 * (tdParamForeachReceipt.getUnitsallowyn().equals("Y")) { closingBalance =
	 * termDepositReceiptsDao.getClosingBalanceForLastInterestRunDate(
	 * termDepositReceipts, origStartDate, newReceipt); }else { closingBalance =
	 * termDepositReceiptsDao.getClosingBalanceForLastInterestRunDate(
	 * termDepositReceipts, origStartDate, newReceipt); }
	 * 
	 * if (closingBalance == null) { closingBalance = new ClosingBalance();
	 * closingBalance.setBalance1(0.0); closingBalance.setBalance2(0.0);
	 * closingBalance.setBalance3(0.0); closingBalance.setBalance4(0.0);
	 * isClosingBalanceNull = true; if
	 * (termDepositReceipts.getMatdate().before(endDate.getTime()) && newReceipt ==
	 * false) { startDate.setTime(startDate1);
	 * closingBalance.setCbldate(startDate1); } else if
	 * (termDepositReceipts.getMatdate().before(endDate.getTime()) && newReceipt ==
	 * true) { startDate.setTime(asoffDate.getTime());
	 * closingBalance.setCbldate(asoffDate.getTime()); } else {
	 * closingBalance.setBalance1(termDepositReceipts.getInstorprincamt());
	 * startDate.setTime(asoffDate.getTime());
	 * closingBalance.setCbldate(asoffDate.getTime()); } } else {
	 * cbldate.setTime(closingBalance.getCbldate()); } if (closingBalanceOnOprDate
	 * == null) { closingBalanceOnOprDate = new ClosingBalance();
	 * closingBalanceOnOprDate.setBalance1(0.0);
	 * closingBalanceOnOprDate.setBalance2(0.0);
	 * closingBalanceOnOprDate.setBalance3(0.0);
	 * closingBalanceOnOprDate.setBalance4(0.0);
	 * closingBalanceOnOprDate.setCbldate(termDepositReceipts.getAsoffdate()); }
	 * 
	 *//***********************************************************************
		 * if the receipt is both opened and closed in the same period, the interest
		 * projected is the diff in the mat value and the principal amount
		 **********************************************************************/
	/*
	 * startDate.set(Calendar.HOUR_OF_DAY, 0); startDate.set(Calendar.MINUTE, 0);
	 * startDate.set(Calendar.SECOND, 0); startDate.set(Calendar.MILLISECOND, 0);
	 * 
	 * endDate.set(Calendar.HOUR_OF_DAY, 0); endDate.set(Calendar.MINUTE, 0);
	 * endDate.set(Calendar.SECOND, 0); endDate.set(Calendar.MILLISECOND, 0);
	 * 
	 * if (!premature) {
	 * if((termDepositReceipts.getMatdate().before(endDate.getTime())) &&
	 * (termDepositReceipts.getMatdate().before(operationDate))){ if (newReceipt ==
	 * true && (termDepositReceipts.getMatdate().before(endDate.getTime()))) {
	 * premature = true; isMaturing = true; } else if (newReceipt == false &&
	 * (termDepositReceipts.getMatdate().compareTo(endDate.getTime()) <= 0)) {
	 * endDate.setTime(termDepositReceipts.getMatdate()); if
	 * (tdParamForeachReceipt.getUnitsallowyn().equals("Y")) {
	 * closingBalanceForPremature =
	 * termDepositReceiptsDao.getClosingBalanceAfterClosure(termDepositReceipts.
	 * getId().getLbrcode(), termDepositReceipts.getId().getPrdacctid(),
	 * termDepositReceipts.getMatdate(), premature); } premature = true; isMaturing
	 * = true; } } else {
	 * if(termDepositReceipts.getMatdate().compareTo(endDate.getTime())<=0){
	 * endDate.setTime(termDepositReceipts.getMatdate());
	 * endDate.add(Calendar.DAY_OF_MONTH, -1); isMaturing = true; } if (!isMaturing)
	 * { originalMatDate.setTime(endDate.getTime());
	 * //endDate.add(Calendar.DAY_OF_MONTH, -1); if(startDate.compareTo(ogDate)<0){
	 * startDate.setTime(ogDate.getTime()); } if
	 * (tdParamForeachReceipt.getIntfreq().equals("M") &&
	 * tdParamForeachReceipt.getIntpaidyn().equals("Y")) {
	 * quarterEndDate.setTime(startDate.getTime());
	 * quarterEndDate.set(Calendar.DAY_OF_MONTH,
	 * startDate.getActualMaximum(Calendar.DAY_OF_MONTH)); if
	 * (startDate.get(Calendar.DAY_OF_MONTH) == 1) { daysDiff = 0L; monthDiff =
	 * SwiftcoreDateUtil.getDateDiffForTDSProjection(startDate, endDate); } else {
	 * daysDiff = SwiftcoreDateUtil.getDateDiffForTDSProjection(startDate,
	 * quarterEndDate); monthDiff =
	 * SwiftcoreDateUtil.getDateDiffForTDSProjection(quarterEndDate, endDate); }
	 * noOfMonths = (monthDiff + 1) / 30; periodicInterest =
	 * (termDepositReceipts.getInstorprincamt() *
	 * termDepositReceipts.getIntrateCalc())/(termDepositReceipts.getIntrateCalc ()
	 * + 1200); periodInterest = new BigDecimal(periodicInterest).setScale(2,
	 * RoundingMode.HALF_UP); } else if
	 * (tdParamForeachReceipt.getIntfreq().equals("Q") &&
	 * tdParamForeachReceipt.getIntpaidyn().equals("Y")) { periodicInterest =
	 * ((termDepositReceipts.getInstorprincamt() *
	 * termDepositReceipts.getIntrateCalc()) / (1200 / 3)); periodInterest =
	 * BigDecimal.valueOf(Math.round(periodicInterest));
	 *//*******************************************************
		 * To find the quarter start date and quarter end date based on the start date
		 ******************************************************/
	/*
	 * if (startDate.get(Calendar.MONTH) == Calendar.JANUARY ||
	 * startDate.get(Calendar.MONTH) == Calendar.FEBRUARY ||
	 * startDate.get(Calendar.MONTH) == Calendar.MARCH) {
	 * quarterEndDate.set(startDate.get(Calendar.YEAR), Calendar.MARCH, 31);
	 * quarterStartDate.set(startDate.get(Calendar.YEAR), Calendar.JANUARY, 1); }
	 * else if (startDate.get(Calendar.MONTH) == Calendar.APRIL ||
	 * startDate.get(Calendar.MONTH) == Calendar.MAY ||
	 * startDate.get(Calendar.MONTH) == Calendar.JUNE) {
	 * quarterEndDate.set(startDate.get(Calendar.YEAR), Calendar.JUNE, 30);
	 * quarterStartDate.set(startDate.get(Calendar.YEAR), Calendar.APRIL, 1); } else
	 * if (startDate.get(Calendar.MONTH) == Calendar.JULY ||
	 * startDate.get(Calendar.MONTH) == Calendar.AUGUST ||
	 * startDate.get(Calendar.MONTH) == Calendar.SEPTEMBER) {
	 * quarterEndDate.set(startDate.get(Calendar.YEAR), Calendar.SEPTEMBER, 30);
	 * quarterStartDate.set(startDate.get(Calendar.YEAR), Calendar.JULY, 1); } else
	 * { quarterEndDate.set(startDate.get(Calendar.YEAR), Calendar.DECEMBER, 31);
	 * quarterStartDate.set(startDate.get(Calendar.YEAR), Calendar.OCTOBER, 1); } if
	 * (DateUtility.getFormattedDate(startDate.getTime()).compareTo(DateUtility.
	 * getFormattedDate(quarterStartDate.getTime())) == 0) { monthDiff =
	 * SwiftcoreDateUtil.getDateDiffForTDSProjection(startDate, endDate); daysDiff =
	 * 0l; } else { daysDiff =
	 * SwiftcoreDateUtil.getDateDiffForTDSProjection(startDate, quarterEndDate);
	 * monthDiff = SwiftcoreDateUtil.getDateDiffForTDSProjection(quarterEndDate,
	 * endDate); } noOfMonths = (monthDiff + 1) / 30; if
	 * (tdParamForeachReceipt.getUnitsallowyn().equals("Y") && amtForTDUNITWD > 0) {
	 * daysDiffForUnitwd = SwiftcoreDateUtil.getDateDiffForTDSProjection(startDate,
	 * dateOfTDUNITWD); } } else if (tdParamForeachReceipt.getIntfreq().equals("H")
	 * && tdParamForeachReceipt.getIntpaidyn().equals("N")) { if
	 * (tdParamForeachReceipt.getCumintyn().equals("Y")) { monthDiff =
	 * SwiftcoreDateUtil.getDateDiffForTDSProjection(startDate, endDate);
	 * isKdProduct = true; } else { periodicInterest =
	 * ((termDepositReceipts.getInstorprincamt() *
	 * termDepositReceipts.getIntrateCalc()) / (1200 / 3)); periodInterest =
	 * BigDecimal.valueOf(periodicInterest); periodInterest =
	 * periodInterest.multiply(new BigDecimal(2.0)); periodInterest =
	 * BigDecimal.valueOf(Math.round(periodInterest.doubleValue())); if
	 * (startDate.get(Calendar.MONTH) == Calendar.JANUARY ||
	 * startDate.get(Calendar.MONTH) == Calendar.FEBRUARY ||
	 * startDate.get(Calendar.MONTH) == Calendar.MARCH) {
	 * quarterEndDate.set(startDate.get(Calendar.YEAR), Calendar.MARCH, 31);
	 * quarterStartDate.set(startDate.get(Calendar.YEAR), Calendar.JANUARY, 1); }
	 * else if (startDate.get(Calendar.MONTH) == Calendar.APRIL ||
	 * startDate.get(Calendar.MONTH) == Calendar.MAY ||
	 * startDate.get(Calendar.MONTH) == Calendar.JUNE) {
	 * quarterEndDate.set(startDate.get(Calendar.YEAR), Calendar.JUNE, 30);
	 * quarterStartDate.set(startDate.get(Calendar.YEAR), Calendar.APRIL, 1); } else
	 * if (startDate.get(Calendar.MONTH) == Calendar.JULY ||
	 * startDate.get(Calendar.MONTH) == Calendar.AUGUST ||
	 * startDate.get(Calendar.MONTH) == Calendar.SEPTEMBER) {
	 * quarterEndDate.set(startDate.get(Calendar.YEAR), Calendar.SEPTEMBER, 30);
	 * quarterStartDate.set(startDate.get(Calendar.YEAR), Calendar.JULY, 1); } else
	 * { quarterEndDate.set(startDate.get(Calendar.YEAR), Calendar.DECEMBER, 31);
	 * quarterStartDate.set(startDate.get(Calendar.YEAR), Calendar.OCTOBER, 1); } if
	 * (DateUtility.getFormattedDate(startDate.getTime()).compareTo(DateUtility.
	 * getFormattedDate(quarterStartDate.getTime())) == 0) { monthDiff =
	 * SwiftcoreDateUtil.getDateDiffForTDSProjection(startDate, endDate); daysDiff =
	 * 0l; } else { daysDiff =
	 * SwiftcoreDateUtil.getDateDiffForTDSProjection(startDate, quarterEndDate);
	 * monthDiff = SwiftcoreDateUtil.getDateDiffForTDSProjection(quarterEndDate,
	 * endDate); } noOfMonths = (monthDiff + 1) / 30; } } else
	 * if((tdParamForeachReceipt.getIntfreq().equals("Q") &&
	 * tdParamForeachReceipt.getCumintyn().equals("Y")) &&
	 * (tdParamForeachReceipt.getInstorprinc().equals("P"))) { monthDiff =
	 * SwiftcoreDateUtil.getDateDiffForTDSProjection(startDate, endDate); noOfMonths
	 * = (monthDiff + 1) / 30; isKdProduct = true; if(newReceipt==true){
	 * if((asoffDate.compareTo(startDate)<=0) && (interestBeforeFinYear>0)){
	 * interestForKDLastFincal = interestBeforeFinYear; } }else{
	 * if((asoffDate.compareTo(startDate)<0) && (interestBeforeFinYear>0)){
	 * interestForKDLastFincal = interestBeforeFinYear; } } }else
	 * if((tdParamForeachReceipt.getIntfreq().equals("Q") &&
	 * tdParamForeachReceipt.getCumintyn().equals("Y")) &&
	 * (tdParamForeachReceipt.getInstorprinc().equals("I"))){ monthDiff =
	 * SwiftcoreDateUtil.getDateDiffForTDSProjection(startDate, endDate); noOfMonths
	 * = (monthDiff + 1) / 30; isRdProduct = true; if(newReceipt==true){
	 * if((asoffDate.compareTo(startDate)<=0) && (interestBeforeFinYear>0)){
	 * interestForKDLastFincal = interestBeforeFinYear; } }else{
	 * if((asoffDate.compareTo(startDate)<0) && (interestBeforeFinYear>0)){
	 * interestForKDLastFincal = interestBeforeFinYear; } } }
	 * 
	 * quarterEndDate.set(Calendar.HOUR_OF_DAY, 0);
	 * quarterEndDate.set(Calendar.MINUTE, 0); quarterEndDate.set(Calendar.SECOND,
	 * 0); quarterEndDate.set(Calendar.MILLISECOND, 0);
	 * 
	 * if (startDate.compareTo(quarterEndDate) == 0 &&
	 * tdParamForeachReceipt.getCumintyn().equals("N")) { daysDiff = daysDiff + 1; }
	 * } } } else {
	 * 
	 * } if (premature == false) { if(!isMaturing){ Long daysIntheYear =
	 * SwiftcoreDateUtil.getDateDiff(startDate, endDate); if(newReceipt==false ||
	 * isKdProduct==true || isRdProduct==true){ startDate.add(Calendar.MONTH,
	 * noOfMonths.intValue()); startDate.add(Calendar.DAY_OF_MONTH, -1); startDate =
	 * SwiftcoreDateUtil.clearMilliSeconds(startDate);
	 * while(startDate.compareTo(endDate)>0){ if(newReceipt==false){
	 * startDate.setTime(ogDate.getTime()); }else{
	 * startDate.setTime(termDepositReceipts.getAsoffdate()); } noOfMonths =
	 * noOfMonths - 1; startDate.add(Calendar.MONTH, noOfMonths .intValue());
	 * startDate = SwiftcoreDateUtil.clearMilliSeconds(startDate); }
	 * if(newReceipt==false){ startDate.setTime(ogDate.getTime()); }else{
	 * startDate.setTime(termDepositReceipts.getAsoffdate()); } } if
	 * (tdParamForeachReceipt.getIntfreq().equals("Q")) { remainderRemainingMonths =
	 * noOfMonths % 3; if (remainderRemainingMonths > 0 && isKdProduct==false) {
	 * noOfMonths = noOfMonths - remainderRemainingMonths; } if (isKdProduct==true
	 * || isRdProduct == true) { if((asoffDate.compareTo(startDate)<=0) &&
	 * (interestBeforeFinYear>0)){ kdInterest =
	 * receiptsService.calculateInterestForTDProducts(termDepositReceipts,
	 * tdParamForeachReceipt, ogDate.getTime(),
	 * endDate.getTime(),0L,0.0,isMaturing,false,daysDiffForReceipt,"");
	 * isKDreceiptCblNull=true; }else if((asoffDate.compareTo(startDate)<=0) &&
	 * (interestBeforeFinYear==0)){ kdInterest =
	 * receiptsService.calculateInterestForTDProducts(termDepositReceipts,
	 * tdParamForeachReceipt, ogDate.getTime(),
	 * endDate.getTime(),0L,0.0,isMaturing,false,daysDiffForReceipt,""); }else{
	 * kdInterest =
	 * receiptsService.calculateInterestForTDProducts(termDepositReceipts,
	 * tdParamForeachReceipt, termDepositReceipts.getAsoffdate(),
	 * endDate.getTime(),0L,0.0,isMaturing,false,daysDiffForReceipt,""); } }else{
	 * if(tdParamForeachReceipt.getUnitsallowyn().equals("Y")){ if(!newReceipt){
	 * noOfQuarters = noOfMonths / 3; startDate.add(Calendar.MONTH,
	 * noOfMonths.intValue()); daysDiff = SwiftcoreDateUtil.getDateDiff(startDate,
	 * endDate); }else{ noOfQuarters = noOfMonths / 3; } }else{ if(!newReceipt){
	 * noOfQuarters = noOfMonths / 3; startDate.add(Calendar.MONTH,
	 * noOfMonths.intValue()); daysDiff = SwiftcoreDateUtil.getDateDiff(startDate,
	 * endDate); }else{ noOfQuarters = noOfMonths / 3; } } } } else if
	 * (tdParamForeachReceipt.getIntfreq().equals("H") &&
	 * tdParamForeachReceipt.getCumintyn().equals("N")) { remainderRemainingMonths =
	 * noOfMonths % 3; if (remainderRemainingMonths > 0) { noOfMonths = noOfMonths -
	 * remainderRemainingMonths; } noOfQuarters = noOfMonths / 3; } if
	 * (tdParamForeachReceipt.getIntpaidyn().equals("Y")) { if
	 * (tdParamForeachReceipt.getIntfreq().equals("M") &&
	 * tdParamForeachReceipt.getCumintyn().equals("N")) { if (daysDiffForReceipt <
	 * (tdParamForeachReceipt.getShorttermdays() + 1)) { interestProjected =
	 * ((daysIntheYear) * periodInterest.doubleValue()) / 30; } else { tdFMsum =
	 * (noOfMonths * periodInterest.doubleValue()) + ((daysDiff *
	 * periodInterest.doubleValue()) / 30); interestProjected = tdFMsum; }
	 * interestProjected = Double.valueOf(twoDecPlaces.format(interestProjected)) +
	 * interestBeforeFinYear; } else { if
	 * (tdParamForeachReceipt.getIntfreq().equals("Q")&&
	 * tdParamForeachReceipt.getCumintyn().equals("N")) { if (daysDiffForReceipt <
	 * (tdParamForeachReceipt.getShorttermdays() + 1)) { tdFMsum = ((daysIntheYear)
	 * * termDepositReceipts.getIntrateCalc() *
	 * termDepositReceipts.getInstorprincamt()) / 36500; interestProjected =
	 * Double.valueOf(Math.round(tdFMsum)); if
	 * (tdParamForeachReceipt.getUnitsallowyn().equals("Y")) { interestProjected =
	 * interestProjected + amtForTDUNITWD; } } else { if
	 * (tdParamForeachReceipt.getUnitsallowyn().equals("Y")) { tdFMsum =
	 * (noOfQuarters * ((termDepositReceipts.getInstorprincamt() *
	 * termDepositReceipts.getIntrateCalc()) / (1200 / 3))) + ((daysDiff *
	 * termDepositReceipts.getIntrateCalc() *
	 * termDepositReceipts.getInstorprincamt()) / 36500); if (amtForTDUNITWD != 0) {
	 * tdFMsum = tdFMsum + amtForTDUNITWD; } } else { tdFMsum = (noOfQuarters *
	 * ((termDepositReceipts.getInstorprincamt() *
	 * termDepositReceipts.getIntrateCalc()) / (1200 / 3))) + ((daysDiff *
	 * termDepositReceipts.getIntrateCalc() *
	 * termDepositReceipts.getInstorprincamt()) / 36500); //tdFMsum = (noOfMonths *
	 * (periodInterest.doubleValue() / 3)) + ((daysDiff *
	 * termDepositReceipts.getIntrateCalc() * closingBalance.getBalance1()) /
	 * 36500); } } if (noOfMonths == 0) { tdFMsum = tdFMsum + ((monthDiff *
	 * termDepositReceipts.getIntrateCalc() * closingBalance.getBalance1()) /
	 * 36500); } interestProjected = Double.valueOf(Math.round(tdFMsum)) +
	 * interestBeforeFinYear; }else
	 * if(tdParamForeachReceipt.getIntfreq().equals("H") &&
	 * tdParamForeachReceipt.getCumintyn().equals("N")){
	 * 
	 * if(isKdProduct){ kdInterest =
	 * receiptsService.calculateInterestForTDProducts(termDepositReceipts,
	 * tdParamForeachReceipt, ogDate.getTime(),
	 * endDate.getTime(),0L,0.0,isMaturing,false,daysDiffForReceipt,"");
	 * interestProjected = kdInterest - closingBalance.getBalance2(); }else{ if
	 * (daysDiffForReceipt < (tdParamForeachReceipt.getShorttermdays() + 1)) {
	 * tdFMsum = ((daysIntheYear) * termDepositReceipts.getIntrateCalc() *
	 * termDepositReceipts.getInstorprincamt()) / 36500; interestProjected =
	 * Double.valueOf(Math.round(tdFMsum)) + interestBeforeFinYear; } else { tdFMsum
	 * = (((termDepositReceipts.getInstorprincamt() *
	 * termDepositReceipts.getIntrateCalc()) / 1200) * noOfMonths) + ((daysDiff *
	 * termDepositReceipts.getIntrateCalc() *
	 * termDepositReceipts.getInstorprincamt()) / 36500) + 0.0; if (noOfMonths == 0)
	 * { tdFMsum = tdFMsum + ((monthDiff * termDepositReceipts.getIntrateCalc() *
	 * termDepositReceipts.getInstorprincamt()) / 36500); } if (isClosingBalanceNull
	 * == false) { if
	 * (closingBalance.getCbldate().compareTo(origStartDate.getTime()) >= 0 &&
	 * closingBalance.getCbldate().compareTo(endDate.getTime()) <= 0) {
	 * interestProjected = Double.valueOf(Math.round(tdFMsum)) +
	 * interestBeforeFinYear; } else { interestProjected =
	 * Double.valueOf(Math.round(tdFMsum)) + interestBeforeFinYear; } } else {
	 * interestProjected = Double.valueOf(Math.round(tdFMsum)) +
	 * interestBeforeFinYear; } } } } }
	 * 
	 * } else if ((tdParamForeachReceipt.getIntpaidyn().equals("N")) &&
	 * (tdParamForeachReceipt.getInstorprinc().equals("P")) &&
	 * (tdParamForeachReceipt.getIntfreq().equals("Q"))) { if (daysDiffForReceipt <
	 * (tdParamForeachReceipt.getShorttermdays() + 1)) { tdFMsum = ((daysIntheYear)
	 * * termDepositReceipts.getIntrateCalc() * (closingBalance.getBalance1() +
	 * closingBalance.getBalance2() + interestForKDLastFincal)) / 36500;
	 * interestProjected = Double.valueOf(Math.round(tdFMsum)) +
	 * interestBeforeFinYear; } else { interestProjected = kdInterest -
	 * closingBalance.getBalance2(); } }// end KD condition else
	 * if((tdParamForeachReceipt.getIntpaidyn().equals("N")) &&
	 * (tdParamForeachReceipt.getInstorprinc().equals("I")) &&
	 * (tdParamForeachReceipt.getIntfreq().equals("Q"))){ if (daysDiffForReceipt <
	 * (tdParamForeachReceipt.getShorttermdays() + 1)) { Double interestRD =
	 * (termDepositReceipts.getInstorprincamt() *
	 * termDepositReceipts.getIntrateCalc() * daysDiffForReceipt)/36500; Long
	 * interestForDays =
	 * Math.round(Double.valueOf(twoDecPlaces.format(interestRD)));
	 * interestProjected = Double.valueOf(interestForDays) + interestBeforeFinYear;
	 * } else { interestProjected = kdInterest - closingBalance.getBalance2(); }
	 * }//end RD condition else if
	 * ((tdParamForeachReceipt.getIntpaidyn().equals("N")) &&
	 * (tdParamForeachReceipt.getIntfreq().equals("H"))) { if(isKdProduct){
	 * kdInterest =
	 * receiptsService.calculateInterestForTDProducts(termDepositReceipts,
	 * tdParamForeachReceipt, ogDate.getTime(),
	 * endDate.getTime(),0L,0.0,isMaturing,false,daysDiffForReceipt,"");
	 * interestProjected = kdInterest - closingBalance.getBalance2(); }else{ if
	 * (daysDiffForReceipt < (tdParamForeachReceipt.getShorttermdays() + 1)) {
	 * tdFMsum = ((daysIntheYear) * termDepositReceipts.getIntrateCalc() *
	 * termDepositReceipts.getInstorprincamt()) / 36500; interestProjected =
	 * Double.valueOf(Math.round(tdFMsum)) + interestBeforeFinYear; } else { tdFMsum
	 * = (((termDepositReceipts.getInstorprincamt() *
	 * termDepositReceipts.getIntrateCalc()) / 1200) * noOfMonths) + ((daysDiff *
	 * termDepositReceipts.getIntrateCalc() *
	 * termDepositReceipts.getInstorprincamt()) / 36500) + 0.0; if (noOfMonths == 0)
	 * { tdFMsum = tdFMsum + ((monthDiff * termDepositReceipts.getIntrateCalc() *
	 * termDepositReceipts.getInstorprincamt()) / 36500); } if (isClosingBalanceNull
	 * == false) { if
	 * (closingBalance.getCbldate().compareTo(origStartDate.getTime()) >= 0 &&
	 * closingBalance.getCbldate().compareTo(endDate.getTime()) <= 0) {
	 * interestProjected = Double.valueOf(Math.round(tdFMsum)) +
	 * interestBeforeFinYear; } else { interestProjected =
	 * Double.valueOf(Math.round(tdFMsum)) + interestBeforeFinYear; } } else {
	 * interestProjected = Double.valueOf(Math.round(tdFMsum)) +
	 * interestBeforeFinYear; } } }
	 * 
	 * }// end FR condition. FR now treated as a non pay out product. }else{
	 * if(newReceipt == true){
	 * if(tdParamForeachReceipt.getUnitsallowyn().equals("Y")){
	 * if(amtForTDUNITWD!=0){ interestProjected =
	 * receiptsService.calculateInterestForTDProducts(termDepositReceipts,
	 * tdParamForeachReceipt, startDate.getTime(),
	 * endDate.getTime(),2L,closingBalanceOnOprDate.getBalance1(),isMaturing,
	 * false,daysDiffForReceipt,""); interestProjected = interestProjected +
	 * amtForTDUNITWD; }else{ interestProjected = termDepositReceipts.getMatval() -
	 * ((closingBalance.getBalance1() + closingBalance.getBalance2())); } }else{
	 * interestProjected =
	 * receiptsService.calculateInterestForTDProducts(termDepositReceipts,
	 * tdParamForeachReceipt, termDepositReceipts.getAsoffdate(),
	 * endDate.getTime(),0L,0.0,isMaturing,false,daysDiffForReceipt,""); } }else{
	 * if(tdParamForeachReceipt.getUnitsallowyn().equals("Y")){ interestProjected =
	 * closingBalanceOnOprDate.getBalance2() - closingBalance.getBalance2();
	 * remainingInterestForMDS =
	 * calculateRemainderInterestForMDS(closingBalanceOnOprDate,
	 * termDepositReceipts,endDate); interestProjected = interestProjected +
	 * remainingInterestForMDS; if(amtForTDUNITWD!=0){ interestProjected =
	 * receiptsService.calculateInterestForTDProducts(termDepositReceipts,
	 * tdParamForeachReceipt, termDepositReceipts.getAsoffdate(),
	 * endDate.getTime(),2L,closingBalanceOnOprDate.getBalance1(),isMaturing,
	 * false,daysDiffForReceipt,""); if(termDepositReceipts.getReceiptstatus()==99){
	 * interestProjected = amtForTDUNITWD; }else{ interestProjected = amtForTDUNITWD
	 * + (termDepositReceipts.getMatval()- termDepositReceipts.getInstorprincamt())
	 * - termDepositReceipts.getIntprvdamtfcy(); } }else{ interestProjected =
	 * receiptsService.calculateInterestForTDProducts(termDepositReceipts,
	 * tdParamForeachReceipt, termDepositReceipts.getAsoffdate(),
	 * endDate.getTime(),2L,closingBalanceOnOprDate.getBalance1(),isMaturing,
	 * false,daysDiffForReceipt,""); interestProjected =
	 * (termDepositReceipts.getMatval()- termDepositReceipts.getInstorprincamt()) -
	 * closingBalance.getBalance2(); } }else{ interestProjected =
	 * receiptsService.calculateInterestForTDProducts(termDepositReceipts,
	 * tdParamForeachReceipt, termDepositReceipts.getAsoffdate(),
	 * endDate.getTime(),0L,0.0,isMaturing,false,daysDiffForReceipt,"");
	 * interestProjected = interestProjected - closingBalance.getBalance2(); } }
	 * 
	 * } } else if (premature == true && isMaturing == true) { if
	 * (isClosingBalanceNull == false) { if
	 * (tdParamForeachReceipt.getUnitsallowyn().equals("Y")) { if
	 * (closingBalanceForPremature.getBalance1() == 0) { interestProjected =
	 * closingBalanceForPremature.getBalance2() - closingBalance.getBalance2(); }
	 * else { interestProjected = termDepositReceipts.getMatval() -
	 * (closingBalance.getBalance1() + closingBalance.getBalance2()); } } else {
	 *//***********************************************************
		 * changed to facilitate proj for receipts which mature before the financial
		 * year but are older receipts
		 **********************************************************/
	/*
	 * interestProjected = termDepositReceipts.getMatval() -
	 * (closingBalance.getBalance1() +
	 * closingBalance.getBalance2());//closingBalanceOnOprDate.getBalance2() -
	 * closingBalance.getBalance2(); } } else { if
	 * (termDepositReceipts.getInstorprincamt() > 0) { interestProjected =
	 * termDepositReceipts.getMatval() - termDepositReceipts.getInstorprincamt(); }
	 * else { interestProjected = termDepositReceipts.getMatval() -
	 * termDepositReceipts.getInstorprincamt(); } } } else if (premature == true &&
	 * isMaturing == false) { if (premature == true && newReceipt == true) {
	 *//***************************************************************
		 * if the receipt is and older receipt and it prematurely closes in this
		 * calender year
		 **************************************************************/
	/*
	 * if (operationDate.compareTo(termDepositReceipts.getCloseddate()) == 0) {
	 * interestProjected = termDepositReceipts.getIntprvdamtfcy() -
	 * closingBalance.getBalance2(); } else { if
	 * (tdParamForeachReceipt.getUnitsallowyn().equals("Y")) { interestProjected =
	 * closingBalanceForPremature.getBalance2(); } else { interestProjected =
	 * closingBalanceOnOprDate.getBalance2(); } }
	 * 
	 * } else if (premature == true && newReceipt == false) { if
	 * (operationDate.compareTo(termDepositReceipts.getCloseddate()) == 0) {
	 * interestProjected = termDepositReceipts.getIntprvdamtfcy() -
	 * closingBalance.getBalance2(); } else { if
	 * (tdParamForeachReceipt.getUnitsallowyn().equals("Y")) { interestProjected =
	 * closingBalanceForPremature.getBalance2() - closingBalance.getBalance2(); }
	 * else { interestProjected = closingBalanceOnOprDate.getBalance2() -
	 * closingBalance.getBalance2(); } }
	 * 
	 * } }
	 * 
	 * }
	 * 
	 * if (interestProjected == null) { interestProjected = 0.0; } if (tdsPercentage
	 * == 0) { tdsPercentage = tdParamForeachReceipt.getInttaxper(); }
	 * brwiseCustTDSFile =
	 * termDepositReceiptsDao.getBrwiseCustTDSFile(termDepositReceipts.getId().
	 * getLbrcode(), customer.getMaincustno());
	 *//***************************************************************************
		 * if no branch wise details found, create new details else set int proj, int
		 * prov, tds proj and tds prov to 0
		 **************************************************************************/

	/*
	 * if (brwiseCustTDSFile == null) { brwiseCustTDSFile = new BrwiseCustTDSFile();
	 * } else { // termDepositReceiptsDao.initializeTdsDetails(brwiseCustTDSFile); }
	 * 
	 *//***************************************************************************
		 * calculating the interest projection and the tds projection for the current
		 * financial year
		 **************************************************************************//*
																					 * if (tdParamForeachReceipt .
																					 * getTaxprojection ( ) . equals (
																					 * "Y" ) ) { tdsOnEachReceipt =
																					 * (interestProjected *
																					 * tdsPercentage) / 100;
																					 * brwiseCustTDSFile .
																					 * setTdsprojected (
																					 * brwiseCustTDSFile .
																					 * getTdsprojected ( ) + Double .
																					 * valueOf ( Math . round (
																					 * tdsOnEachReceipt ) ) ) ;
																					 * brwiseCustTDSFile .
																					 * setIntprojected (
																					 * brwiseCustTDSFile .
																					 * getIntprojected ( ) + Double .
																					 * valueOf ( twoDecPlaces . format (
																					 * interestProjected ) ) ) ;
																					 * brwiseCustTDSFile .
																					 * setTdsprojected ( Double .
																					 * valueOf ( Math . round (
																					 * tdsOnEachReceipt ) ) ) ;
																					 * brwiseCustTDSFile .
																					 * setIntprojected ( Double .
																					 * valueOf ( twoDecPlaces . format (
																					 * interestProjected ) ) ) ; if (
																					 * isMaturing ) { brwiseCustTDSFile
																					 * . setFincalEndDate (
																					 * oriMaturityDate . getTime ( ) ) ;
																					 * }else{ brwiseCustTDSFile .
																					 * setFincalEndDate (
																					 * originalMatDate . getTime ( ) ) ;
																					 * } } else { brwiseCustTDSFile .
																					 * setIntprojected ( Double .
																					 * valueOf ( twoDecPlaces . format (
																					 * interestProjected ) ) ) ; }
																					 * return brwiseCustTDSFile; } catch
																					 * (Exception e) { e .
																					 * printStackTrace ( ) ; }
																					 * 
																					 * } catch (Exception e) { e .
																					 * printStackTrace ( ) ; } return
																					 * null; }
																					 */
	public static List<Object[]> getLastInterestVoucherDateAndAmt(Long lbrcode, String prdacctid, Date cbldate) {
		String queryString = "";
		Query queryObject = null;
		List<Object[]> allODClosingDPAdhocDPoffset = null;
		queryString = "select distinct valuedate,fcytrnamt,valuedate from Voucher where lbrcode = :lbrcode and mainacctid = :mainacctid and batchcd = :batchcd "
				+ " and activitytype = :activitytype and cashflowtype = :cashflowtype and entrydate_sql = "
				+ " (select max(entrydate_sql) from Voucher where lbrcode = :lbrcode and mainacctid = :mainacctid and batchcd = :batchcd "
				+ " and activitytype = :activitytype and cashflowtype = :cashflowtype and entrydate_sql <= :entrydate_sql)";
		queryObject = HBUtil.getSessionFactory().openSession().createQuery(queryString);
		queryObject.setParameter("lbrcode", lbrcode);
		queryObject.setParameter("mainacctid", prdacctid);
		queryObject.setParameter("batchcd", MSGConstants.INTEREST_TRANSFER);
		queryObject.setParameter("activitytype", MSGConstants.ACTIVITY_TYPE_INTEREST);
		queryObject.setParameter("cashflowtype", MSGConstants.TDINTCR);
		queryObject.setParameter("entrydate_sql", DateUtil.convertUtiltoSqlDate(cbldate));
		allODClosingDPAdhocDPoffset = queryObject.list();
		return allODClosingDPAdhocDPoffset;
	}

	

	public static double getMatValue(double amount, double rateOfint, int noOfDays) {
		try {
			double intRate = amount * (rateOfint / 100) * (noOfDays / 365);
			return amount + intRate;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return 0.0;
		}
	}

	public static D946022 getIFSCCodeDetail(String ifscCode) {
		String queryString = "from D946022 where ifsccd=?";
		Query queryObject = HBUtil.getSessionFactory().openSession().createQuery(queryString);
		queryObject.setString(0, ifscCode);
		List<D946022> ifscList = queryObject.list();
		if (!ifscList.isEmpty()) {
			return ifscList.get(0);
		}
		return null;
	}

	public static D350076 getCustDelivyChannelMasterObject(String custNo) {
		D350076 custDelivyChannelMaster = null;
		Session session = HBUtil.getSessionFactory().openSession();

		Criteria criteria = session.createCriteria(D350076.class);
		criteria.add(Expression.eq("custNo", custNo));
		List<D350076> custDelivyChannelList = criteria.list();//
		session.close();
		session = null;
		if (null != custDelivyChannelList && !custDelivyChannelList.isEmpty()) {
			custDelivyChannelMaster = custDelivyChannelList.get(0);
		}

		return custDelivyChannelMaster;
	}

	public static D002011 getICustomerDetails(String custNo) {
		Session session = HBUtil.getSessionFactory().getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D002011.class);
		criteria.add(Restrictions.eq("custNo", custNo.trim()));
		List<D002011> list = criteria.list();
		session.close();
		session = null;
		if (list == null || list.isEmpty())
			return null;
		return list.get(0);
	}

	public static D350077 fetchOtherChannelFundsObject(String custNo, String accountNo, Long lbrCode) {
		D350077 otherChannelFunds = null;
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D350077.class);
		criteria.add(Restrictions.eq("id.custNo", custNo));
		criteria.add(Restrictions.eq("id.acctNo", accountNo));
		criteria.add(Restrictions.eq("id.brCode", Short.valueOf(lbrCode.toString())));
		List<D350077> otherChannelFundsList = criteria.list();
		session.close();
		session = null;
		if (null != otherChannelFundsList && !otherChannelFundsList.isEmpty()) {
			otherChannelFunds = otherChannelFundsList.get(0);
		}
		return otherChannelFunds;
	}

	public static String getProductNumber(String productNo) {
		if (productNo != null && productNo.length() >= 8) {
			productNo = productNo.substring(0, 8).trim();
		}
		return productNo;
	}

	public static IMPSTransactionResponse productTrnxValidations(D009021 productMaster, String drCr) {
		IMPSTransactionResponse trnxRes = new IMPSTransactionResponse();
		if (null == productMaster) {
			logger.error("Product not found.");
			trnxRes.setResponse(MSGConstants.ERROR);
			trnxRes.setErrorMessage(MSGConstants.PRODUCT_NOT_FOUND);
			return trnxRes;
		}
		if ("Debit".equalsIgnoreCase(drCr)
				&& !(MSGConstants.MODULE_TYPE_LIST.contains(Long.valueOf(productMaster.getModuleType())))) {
			logger.error("Product not found.");
			trnxRes.setResponse(MSGConstants.ERROR);
			trnxRes.setErrorMessage(MSGConstants.PRODUCT_NOT_FOUND);
			return trnxRes;
		} else if ("Credit".equalsIgnoreCase(drCr)
				&& !(MSGConstants.MODULE_TYPE_LIST.contains(Long.valueOf(productMaster.getModuleType())))) {
			logger.error("Product not found.");
			trnxRes.setResponse(MSGConstants.ERROR);
			trnxRes.setErrorMessage(MSGConstants.PRODUCT_NOT_FOUND);
			return trnxRes;

		}

		// if("Y".equalsIgnoreCase(chkAllValidation)){////As discussed with
		// PRashant Bharambe, removed this condition.
		if (productMaster.getAcctStat() == 3) {
			logger.error("Product Status is Closed.");
			trnxRes.setResponse(MSGConstants.ERROR);
			trnxRes.setErrorMessage(MSGConstants.PRODUCT_STATUS_IS_CLOSED);
			return trnxRes;
		}

		if (productMaster.getAcctStat() == 4) {
			logger.error("Product status is inoperative.");
			trnxRes.setResponse(MSGConstants.ERROR);
			trnxRes.setErrorMessage(MSGConstants.PRODUCT_STATUS_IS_INOOPERATIVE);
			return trnxRes;
		}

		if (productMaster.getAcctStat() == 5) {
			logger.error("Product status is dormant.");
			trnxRes.setResponse(MSGConstants.ERROR);
			trnxRes.setErrorMessage(MSGConstants.PRODUCT_STATUS_IS_DORMANT);
			return trnxRes;
		}

		if (productMaster.getAcctStat() == 6) {
			logger.error("Product status is blocked or freezed.");
			trnxRes.setResponse(MSGConstants.ERROR);
			trnxRes.setErrorMessage(MSGConstants.PRODUCT_STATUS_IS_BLOCKED_OR_FREEZED);
			return trnxRes;
		}
		trnxRes.setValid(true);
		return trnxRes;
	}

	public static D001011 getRtgsHolidays(String statcode, char rtgsneft, Integer year, Integer month, Integer day,
			String flag) {
		logger.error("statcode::>>" + statcode);
		logger.error("rtgsneft:>>>" + rtgsneft);
		logger.error("Year::>>" + year);
		logger.error("month::>>" + month);
		logger.error("day::>>" + day);
		logger.error("flag::>>" + flag);
		Query queryobj = null;
		String sqlquery = " ";
		if ("T".equalsIgnoreCase(flag)) {
			sqlquery = "from D001011 where id.stateCode='MAH' and id.rtgsneft=? and id.calYear=? and id.calMonth=? and id.day=? and dayType='W' ";
			queryobj = HBUtil.getSessionFactory().openSession().createQuery(sqlquery);
			// queryobj.setString(0, statcode);
			queryobj.setCharacter(0, rtgsneft);
			queryobj.setShort(1, Short.valueOf(year + ""));
			queryobj.setByte(2, Byte.valueOf(month + 1 + ""));
			queryobj.setLong(3, Byte.valueOf(day + ""));
			List<D001011> results = queryobj.list();
			if (results.size() != 0) {
				return results.get(0);
			}
		} else {
			sqlquery = "from D001011 where id.stateCode='MAH' and  id.rtgsneft=? and id.calYear=? and id.calMonth=? and id.day>? and dayType='W' ORDER BY id.day ASC";
			queryobj = HBUtil.getSessionFactory().openSession().createQuery(sqlquery);
			// queryobj.setString(0, statcode);
			queryobj.setCharacter(0, rtgsneft);
			queryobj.setShort(1, Short.valueOf(year + ""));
			queryobj.setByte(2, Byte.valueOf(month + 1 + ""));
			queryobj.setLong(3, Byte.valueOf(day + ""));
			logger.error("D001011 list::>>" + queryobj.list());
			List<D001011> results = queryobj.list();
			if (results.size() != 0) {
				return results.get(0);
			} else {
				sqlquery = "from D001011 where id.stateCode='MAH' and  id.rtgsneft=? and id.calYear=? and id.calMonth=?  and dayType='W' ORDER BY id.day asc";
				month = month + 1;
				queryobj = HBUtil.getSessionFactory().openSession().createQuery(sqlquery);
				// queryobj.setString(0, statcode);
				queryobj.setCharacter(0, rtgsneft);
				queryobj.setShort(1, Short.valueOf(year + ""));
				queryobj.setByte(2, Byte.valueOf(month + 1 + ""));
				List<D001011> results2 = queryobj.list();
				if (results2.size() != 0) {
					return results2.get(0);
				} else {
					sqlquery = "from D001011 where id.stateCode='MAH' and  id.rtgsneft=? and id.calYear=? and id.calMonth=?  and dayType='W' ORDER BY id.day ASC";
					year = year + 1;
					queryobj = HBUtil.getSessionFactory().openSession().createQuery(sqlquery);
					// queryobj.setString(0, statcode);
					queryobj.setCharacter(0, rtgsneft);
					queryobj.setShort(1, Short.valueOf(year + ""));
					queryobj.setByte(2, Byte.valueOf("1"));
					List<D001011> results3 = queryobj.list();
					if (results3.size() != 0) {
						return results3.get(0);
					}
				}
			}
		}
		return null;
	}

	public static D010054 getAccountAddress(Long drLbrCode, String drAccountNo, long addrType) {
		D010054 d010054 = null;
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D010054.class);
		criteria.add(Restrictions.eq("id.lbrCode", drLbrCode.intValue()));
		criteria.add(Restrictions.eq("id.prdAcctId", drAccountNo.trim()));
		criteria.add(Restrictions.eq("id.addrType", Byte.valueOf(addrType + "")));
		List<D010054> list = criteria.list();
		logger.error("D010054 list::>>" + list);
		logger.error("D010054 list::>>" + list.size());
		session.close();
		session = null;
		if (list == null || list.isEmpty())
			return null;
		else
			return list.get(0);

	}

	public static IMPSTransactionResponse batchCodeValidations(int lbrCode, Date operationDate, String batch1,
			String batch2) throws SQLException {
		IMPSTransactionResponse batchCodeTrnxRes = new IMPSTransactionResponse();
		D001004 systemParameter = getSystemParameter(0, batch1);
		String batchCode1 = systemParameter == null ? "" : systemParameter.getValue();
		String batchCode = batchCode1;
		IMPSTransactionResponse firstBatchValid = validateBatchCode(lbrCode, batchCode1, operationDate);
		if (!firstBatchValid.isValid()) {
			D001004 sysPara = getSystemParameter(0, batch2);
			if (null == sysPara) {
				return WSUtils.getReturnWSTxOutput(SwiftCoreConstants.ERROR,
						"Off Batch not found at system parameter level.", "", "", "", "", "0", 0l);
			}

			String batchCode2 = sysPara.getValue();
			batchCode = batchCode2;
			IMPSTransactionResponse secondBatchValid = validateBatchCode(lbrCode, batchCode2, operationDate);
			if (!secondBatchValid.isValid()) {
				String err = "Invalid Batch For Branch :: " + lbrCode + " and batch :: " + batchCode;
				secondBatchValid.setErrorMessage(err + " :: " + secondBatchValid.getErrorMessage());
				logger.error(secondBatchValid.getErrorMessage());
				return WSUtils.getReturnWSTxOutput("Technical Error", secondBatchValid.getErrorMessage(),    //change by Manish.
						secondBatchValid.getErrorCode(), "", "", "", "0", 0l);
			}
		}

		batchCodeTrnxRes.setValid(true);
		batchCodeTrnxRes.setResponse(batchCode);
		return batchCodeTrnxRes;
	}

	public static String prependZeros(String str, int requiredLength) {
		while (str.length() < requiredLength) {
			str = "0" + str;
		}
		return str;
	}

	public static String checkLnoRecord(Date oprDate, String msgstype) {
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();

		Calendar cal = Calendar.getInstance();
		Date calDate = new Date();
		// check for System Date or Operation Date...from d001004 table with
		// parameter '946TODAYSDATE'
		D001004 systemParameter = DataUtils.getSystemParameter(0, MSGConstants.TODAYSDATE);
		if (systemParameter != null) {
			if ("Y".equals(systemParameter.getValue().trim())) {
				calDate = new Date();
			} else {
				calDate = oprDate;
			}
		}
		cal.setTime(calDate);
		String days = String.valueOf(cal.get(Calendar.DAY_OF_YEAR));
		String year = (String.valueOf(cal.get(Calendar.YEAR))).substring(2);
		days = prependZeros(days, 3);
		String value = year + days;

		D001005 lnoRecord = new D001005();
		D001005Id id = new D001005Id();
		id.setLbrCode(0);
		id.setCatType("UTRNO");
		id.setCat('O');
		id.setCode1(" ");
		id.setCode2(" ");
		id.setLnodate(calDate);

		lnoRecord.setId(id);
		try {
			// value = lnoDao.getLNORecordLastno(lnoRecord).toString();
			D001005 record = DataUtils.getLNORecordForLnodateRefNo(lnoRecord, msgstype);

			if (record == null) {
				D001005 lnoRcrd = new D001005();
				D001005Id id2 = new D001005Id();
				id2.setLbrCode(0);
				id2.setCatType("UTRNO");
				id2.setCat('O');
				id2.setCode1(" ");
				id2.setCode2(" ");
				id2.setLnodate(lnoRecord.getId().getLnodate());
				lnoRcrd.setId(id2);
				lnoRcrd.setDescr(" ");
				lnoRcrd.setLastNo(Integer.valueOf(value));
				lnoRcrd.setDbtrAddMk(999998);
				lnoRcrd.setDbtrAddMb(0);
				lnoRcrd.setDbtrAddMs(Short.valueOf("0"));
				lnoRcrd.setDbtrAddMd(DateUtil.convertDateFormat(new Date()));
				lnoRcrd.setDbtrAddMt(DateUtil.getFormattedTime(new Date()));
				lnoRcrd.setDbtrAddCk(999998);
				lnoRcrd.setDbtrAddCb(0);
				lnoRcrd.setDbtrAddCs(Short.valueOf("0"));
				lnoRcrd.setDbtrAddCd(DateUtil.convertDateFormat(new Date()));
				lnoRcrd.setDbtrAddCt(DateUtil.getFormattedTime(new Date()));
				lnoRcrd.setDbtrLupdMk(999998);
				lnoRcrd.setDbtrLupdMb(0);
				lnoRcrd.setDbtrLupdMs(Short.valueOf("0"));
				lnoRcrd.setDbtrLupdMd(DateUtil.convertDateFormat(new Date()));
				lnoRcrd.setDbtrLupdMt(DateUtil.getFormattedTime(new Date()));
				lnoRcrd.setDbtrLupdCk(999998);
				lnoRcrd.setDbtrLupdCb(0);
				lnoRcrd.setDbtrLupdCs(Short.valueOf("0"));
				lnoRcrd.setDbtrLupdCd(DateUtil.convertDateFormat(new Date()));
				lnoRcrd.setDbtrLupdCt(DateUtil.getFormattedTime(new Date()));
				lnoRcrd.setDbtrTauthDone(Short.valueOf("1"));
				lnoRcrd.setDbtrRecStat(Byte.valueOf("1"));
				lnoRcrd.setDbtrAuthDone(Byte.valueOf("1"));
				lnoRcrd.setDbtrAuthNeeded(Byte.valueOf("0"));
				lnoRcrd.setDbtrUpdtChkId(Short.valueOf("1"));
				lnoRcrd.setDbtrLhisTrnNo(0);
				session.save(lnoRcrd);
				t.commit();
				session.close();
				session = null;
				t = null;
			} else
				value = record.getLastNo() + "";
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return value;
	}

	public static D001005 getLNORecordForLnodateRefNo(D001005 lnoRecord, String msgstype) {
		String queryString = "";
		Query queryObject = null;
		Session session = HBUtil.getSessionFactory().openSession();
		/* LNO Date is branch op date not TrsryBr date */
		queryString = "from D001005 where id.lbrCode=? and id.cat=? and id.catType=? and id.lnodate=? order by id.lnodate desc";
		queryObject = session.createQuery(queryString);
		queryObject.setParameter(0, lnoRecord.getId().getLbrCode());
		queryObject.setParameter(1, lnoRecord.getId().getCat());
		queryObject.setParameter(2, lnoRecord.getId().getCatType());
		queryObject.setParameter(3, DateUtil.getFormattedDate(lnoRecord.getId().getLnodate()));
		List<D001005> lnoRecordList = queryObject.list();
		session.close();
		session = null;
		if (lnoRecordList != null && lnoRecordList.size() > 0) {
			return lnoRecordList.get(0);
		}
		return null;
	}

	public static String generateECSAccountNoFull(Long lbrcode, String productNo, String accountNo) {
		D001002 lookup2 = DataUtils.getECSProductCode(productNo.trim());
		String ecsAccountNo = "";
		if (lookup2 != null && lookup2.getCodeDesc().trim().length() > 0) {
			ecsAccountNo = generateECSAccountNo(lbrcode, lookup2.getCodeDesc().trim(), accountNo.trim());
		}
		return ecsAccountNo;
	}

	public static D001002 getECSProductCode(String productNo) {
		long l_start = System.currentTimeMillis();
		String queryString = " from D001002 where id.codeType=? and id.code=? ";
		List<D001002> list = new ArrayList<D001002>();
		Query queryObject = HBUtil.getSessionFactory().openSession().createQuery(queryString);
		queryObject.setLong(0, 11080L);
		queryObject.setString(1, productNo.trim());
		list = queryObject.list();
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		long l_end = System.currentTimeMillis();
		logger.debug("Instrumentation :<PassbookPrintingDAOImpl.java>:<getECSProductCode>: " + (l_end - l_start));
		return null;
	}

	public static String generateECSAccountNo(Long lbrcode, String productNo, String accountNo) {
		String ecsAccountNo = " ";
		String branchCode = SwiftCoreUtil.appendZeroPadding(lbrcode.toString(), 3L);
		String product = SwiftCoreUtil.appendZeroPadding(productNo, 4L);
		String account = SwiftCoreUtil.appendZeroPadding(accountNo, 8L);
		ecsAccountNo = branchCode + product + account;
		return ecsAccountNo;
	}

	public static Long srnoForUtrnoForNeft(Date OprDate, String msgstype) throws Exception {
		Long lastLnoNumber = 1L;
		D001005 lnoRecord = new D001005();
		D001005Id id = new D001005Id();
		int lbrcode=DataUtils.getTrsryBrCode(msgstype).intValue();
		id.setCat('O');
		id.setCatType("NEFT");
		id.setCode1("DAYSRNO");
		id.setCode2(" ");
		id.setLnodate(OprDate);
		id.setLbrCode(lbrcode);
		if ("N06".equalsIgnoreCase(msgstype) || "N07".equalsIgnoreCase(msgstype)) {
			id.setCatType("UTRNO");
		}
		lnoRecord.setId(id);
		D001005 record = new D001005();
		record = DataUtils.getLNORecordForLnodate(lnoRecord, msgstype);
		
		if (record != null) {
			lastLnoNumber = Long.valueOf(record.getLastNo());
			logger.error("LastNo:-"+lastLnoNumber);
		} else {// insert new record with next number with lastno+1(of last
				// date)
			logger.error("LastNo:"+lastLnoNumber);
			D001005 record2 = DataUtils.getLNORecordOfLastDate(lnoRecord, msgstype);
			D001005 lnoRcrd = new D001005();
			D001005Id id2 = new D001005Id();
			id2.setLbrCode(lbrcode);
			id2.setCat('O');
			id2.setCode1("DAYSRNO");
			id2.setCode2(" ");

			id.setCatType("NEFT");
			// lnoRcrd.setLbrcode(0L);
			// lnoRcrd.setCat("O");
			// lnoRcrd.setCattype("NEFT");
			// lnoRcrd.setCode1("DAYSRNO");
			// lnoRcrd.setCode2(" ");
			if ("N06".equalsIgnoreCase(msgstype) || "N07".equalsIgnoreCase(msgstype)) {
				id2.setCatType("UTRNO");
			}
			lnoRcrd.setId(id2);
			id2.setLnodate(getOpenDate(getTrsryBrCode(msgstype).intValue()));
			lnoRcrd.setDescr(" ");
			lnoRcrd.setId(id2);
			lnoRcrd.setDescr(" ");
			lnoRcrd.setLastNo(Integer.valueOf(0));
			lnoRcrd.setDbtrAddMk(999998);
			lnoRcrd.setDbtrAddMb(0);
			lnoRcrd.setDbtrAddMs(Short.valueOf("0"));
			lnoRcrd.setDbtrAddMd(DateUtil.convertDateFormat(new Date()));
			lnoRcrd.setDbtrAddMt(DateUtil.getFormattedTime(new Date()));
			lnoRcrd.setDbtrAddCk(999998);
			lnoRcrd.setDbtrAddCb(0);
			lnoRcrd.setDbtrAddCs(Short.valueOf("0"));
			lnoRcrd.setDbtrAddCd(DateUtil.convertDateFormat(new Date()));
			lnoRcrd.setDbtrAddCt(DateUtil.getFormattedTime(new Date()));
			lnoRcrd.setDbtrLupdMk(999998);
			lnoRcrd.setDbtrLupdMb(0);
			lnoRcrd.setDbtrLupdMs(Short.valueOf("0"));
			lnoRcrd.setDbtrLupdMd(DateUtil.convertDateFormat(new Date()));
			lnoRcrd.setDbtrLupdMt(DateUtil.getFormattedTime(new Date()));
			lnoRcrd.setDbtrLupdCk(999998);
			lnoRcrd.setDbtrLupdCb(0);
			lnoRcrd.setDbtrLupdCs(Short.valueOf("0"));
			lnoRcrd.setDbtrLupdCd(DateUtil.convertDateFormat(new Date()));
			lnoRcrd.setDbtrLupdCt(DateUtil.getFormattedTime(new Date()));
			lnoRcrd.setDbtrTauthDone(Short.valueOf("1"));
			lnoRcrd.setDbtrRecStat(Byte.valueOf("1"));
			lnoRcrd.setDbtrAuthDone(Byte.valueOf("1"));
			lnoRcrd.setDbtrAuthNeeded(Byte.valueOf("0"));
			lnoRcrd.setDbtrUpdtChkId(Short.valueOf("1"));
			lnoRcrd.setDbtrLhisTrnNo(0);
			if (record2 != null) {
				lnoRcrd.setLastNo(record2.getLastNo() + 1);
			} else {
				lnoRcrd.setLastNo(1);
			}
			Session session = HBUtil.getSessionFactory().openSession();
			Transaction transaction = session.beginTransaction();
			try {
				session.save(lnoRcrd);
				transaction.commit();
				session.close();
				session = null;
			} catch (Exception e) {
				// TODO: handle exception
				session.close();
				session = null;
				e.printStackTrace();
			}

			lastLnoNumber = Long.valueOf(lnoRcrd.getLastNo());
		}
		return lastLnoNumber;
	}

	public static D001005 getLNORecordForLnodate(D001005 lnoRecord, String msgstype) {
		String queryString = "";
		Query queryObject = null;
		/* Geting TrsryBrCode and Date dayOpenCloseFile.getDyfdate() */
		//D010007 dayOpenCloseFile = getOpenDays(getTrsryBrCode(msgstype));
		/*** Added By Aniket Desai for 24X7 on 3rd Jan, 2020 ***/
		Date drOperationDate =RTGSNEFTServiceImpl.getOpenDate(getTrsryBrCode(msgstype).intValue());
		queryString = "from D001005 where id.lbrCode=? and id.cat=? and id.catType=? and id.lnodate<=? order by id.lnodate desc";
		Session session = HBUtil.getSessionFactory().openSession();
		queryObject = session.createQuery(queryString);
		queryObject.setParameter(0, lnoRecord.getId().getLbrCode());
		queryObject.setParameter(1, lnoRecord.getId().getCat());
		queryObject.setParameter(2, lnoRecord.getId().getCatType().trim());
		queryObject.setParameter(3, DateUtil.getFormattedDate(drOperationDate));
		List<D001005> lnoRecordList = queryObject.list();
		try {
		if (lnoRecordList != null && lnoRecordList.size() > 0) {
			return lnoRecordList.get(0);
		}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
		}finally {
			session.close();
			session= null;
		}
		return null;
	}

	public static Long getTrsryBrCode(String messageType) {
		int trsryBrCode;
		String queryString = "select TRSRYBRCODE from RTGS_CUTOFF_PARAMETER where MESSAGETYPE=? and WORKINGDAYS = 1 ";
		Query queryObject = HBUtil.getSessionFactory().openSession().createSQLQuery(queryString);
		queryObject.setString(0, messageType);
		if(ConfigurationLoader.getParameters(false).getProperty("DATABASE").equalsIgnoreCase("ORACLE")) {
			trsryBrCode = ((BigDecimal) queryObject.uniqueResult()).intValue();
		}else {
			trsryBrCode = (int) queryObject.uniqueResult();
		}
		return Long.valueOf(trsryBrCode);
	}

	public static D001005 getLNORecordOfLastDate(D001005 lnoRecord, String msgstype) {
		String queryString = "";
		Query queryObject = null;
		/* Geting TrsryBrCode and Date dayOpenCloseFile.getDyfdate() */
		D010007 dayOpenCloseFile = getOpenDays(getTrsryBrCode(msgstype));
		queryString = "from D001005 where id.lbrCode=? and id.cat=? and id.catType=? and id.code1=? and id.code2=? and id.lnodate<=? order by id.lnodate desc";
		Session session = HBUtil.getSessionFactory().openSession();
		queryObject = session.createQuery(queryString);
		queryObject.setParameter(0, lnoRecord.getId().getLbrCode());
		queryObject.setParameter(1, lnoRecord.getId().getCat());
		queryObject.setParameter(2, lnoRecord.getId().getCatType());
		queryObject.setParameter(3, lnoRecord.getId().getCode1());
		queryObject.setParameter(4, " ");
		queryObject.setParameter(5, DateUtil.getFormattedDate(dayOpenCloseFile.getId().getDyfDate()));
		List<D001005> lnoRecordList = queryObject.list();
		try {
			if (lnoRecordList != null && lnoRecordList.size() > 0) {
				return lnoRecordList.get(0);
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			session.close();
			session= null;
		}
		return null;
	}

	public static D010007 getOpenDays(Long lbrcode) {
		byte beginflag = 1;
		byte endflag = 0;
		List<D010007> list = new ArrayList<D010007>();
		String queryString = "from D010007 where id.lbrCode=? and dayBeginFlag=? and dayEndFlag=? order by id.dyfDate desc";
		Query queryObject = HBUtil.getSessionFactory().openSession().createQuery(queryString);
		queryObject.setLong(0, lbrcode);
		queryObject.setLong(1, beginflag);
		queryObject.setLong(2, endflag);
		list = queryObject.list();
		if (list != null && list.size() != 0)
			return (D010007) list.get(0);
		else
			return null;
	}

	public static D946020 prepareRtgsMessagesObject(Long drLbrCode, char iwOwMsg, String msgSType, Date tranDate,
			String refNo, String rRefNo, String utrNo, Long utrSeqNo, String ordIFSCCode, String benIfscCode,
			Date valueDate, String currCode, Double transAmnt, Long msgStat, Long msgPriority, String drAccountNo,
			String longName, String addr1, String addr2, String addr3, String accountNo15, String benFldNo,
			String crAccountNo, String benNickName, String benAdd1, String benAdd2, String benMobileNo, String chrgType,
			char ackIdentifier, String reasonCode, Long reconNo, Long reqAuthNos, Long preAuthNos, Long msgTrfType,
			Long rtnRsnCode, Long insType, String insNo, double chrgAmnt, Long mobEmlType, String mobileNo,
			String nutrNo) {
		logger.error("D946020");
		D946020Id id = new D946020Id();
		id.setObrCode(drLbrCode.intValue());
		id.setIwOwMsg(iwOwMsg);
		id.setMsgStype(msgSType);
		id.setMsgDate(tranDate);
		id.setRefNo(refNo);

		D946020 rtgsMessages = new D946020();
		rtgsMessages.setId(id);
		rtgsMessages.setRrefNo(rRefNo);
		rtgsMessages.setUtrno(utrNo);
		rtgsMessages.setUtrseqNo(utrSeqNo.intValue());
		rtgsMessages.setOrdIfsccd(ordIFSCCode);
		rtgsMessages.setBenIfsccd(benIfscCode);
		rtgsMessages.setValueDate(valueDate);
		rtgsMessages.setCurrCd(currCode);
		rtgsMessages.setAmount(transAmnt);
		rtgsMessages.setMsgStat(Byte.valueOf(msgStat + ""));
		rtgsMessages.setMsgPriority(Byte.valueOf(msgPriority + ""));
		rtgsMessages.setOrdAcctId(drAccountNo);
		if ("N06".equalsIgnoreCase(msgSType)) {
			longName = WSUtils.checkSpecialChar(longName);
			addr1 = WSUtils.checkSpecialChar(addr1 == null || addr1.trim().equals("") ? longName : addr1);
			addr2 = WSUtils.checkSpecialChar(addr2);
			addr3 = WSUtils.checkSpecialChar(addr3);
			nutrNo = accountNo15;
		} else {
			longName = accountNo15;
			addr1 = WSUtils.checkSpecialChar(longName);
			addr2 = WSUtils.checkSpecialChar(addr2 == null || addr2.trim().equals("") ? "." : addr2);
			addr3 = WSUtils.checkSpecialChar(addr3 == null || addr3.trim().equals("") ? "." : addr3);
		}

		rtgsMessages.setOrdDesc1(longName == null || longName.trim().equals("") ? MSGConstants.DOT
				: (longName.trim().length() > 34 ? longName.substring(0, 33) : longName.trim()));
		
		rtgsMessages.setOrdDesc2(addr1 == null || addr1.trim().equals("") ? MSGConstants.DOT
				: (addr1.trim().length() > 34 ? addr1.substring(0, 33) : addr1.trim()));
		rtgsMessages.setOrdDesc3(addr2 == null || addr2.trim().equals("") ? MSGConstants.DOT
				: (addr2.trim().length() > 34 ? addr2.substring(0, 33) : addr2.trim()));
		rtgsMessages.setOrdDesc4(addr3 == null || addr3.trim().equals("") ? MSGConstants.DOT
				: (addr3.trim().length() > 34 ? addr3.substring(0, 33) : addr3.trim()));
		rtgsMessages.setBenFldNo(benFldNo == null || benFldNo.trim().equals("") ? MSGConstants.DOT
				: (benFldNo.trim().length() > 34 ? benFldNo.substring(0, 33) : benFldNo.trim()));
		rtgsMessages.setBenDesc1(crAccountNo == null || crAccountNo.trim().equals("") ? MSGConstants.DOT
				: (crAccountNo.trim().length() > 34 ? crAccountNo.substring(0, 33) : crAccountNo.trim()));
		rtgsMessages.setBenDesc2(benNickName == null || benNickName.trim().equals("") ? MSGConstants.DOT
				: (benNickName.trim().length() > 34 ? benNickName.substring(0, 33) : benNickName.trim()));
		rtgsMessages.setBenDesc3(benAdd1 == null || benAdd1.trim().equals("") ? MSGConstants.DOT
				: (benAdd1.length() > 34 ? benAdd1.substring(0, 33) : benAdd1));
		rtgsMessages.setBenDesc4(benAdd2 == null || benAdd2.trim().equals("") ? MSGConstants.DOT
				: (benAdd2.length() > 34 ? benAdd2.substring(0, 33) : benAdd2));
		rtgsMessages.setBenDesc5(benMobileNo == null || benMobileNo.trim().equals("") ? MSGConstants.DOT
				: (benMobileNo.length() > 34 ? benMobileNo.substring(0, 33) : benMobileNo));
		rtgsMessages.setChrgType(chrgType);
		rtgsMessages.setAckIdentifier(ackIdentifier);
		rtgsMessages.setReasonCd(reasonCode);
		rtgsMessages.setReconNo(reconNo.intValue());
		rtgsMessages.setReqAuthNos(Byte.valueOf(reqAuthNos + ""));
		rtgsMessages.setPreAuthNos(Byte.valueOf(preAuthNos + ""));
		rtgsMessages.setMsgTrfType(Byte.valueOf(msgTrfType + ""));
		rtgsMessages.setRtnRsnCode(Byte.valueOf(rtnRsnCode + ""));
		rtgsMessages.setInsType(Byte.valueOf(insType + ""));
		rtgsMessages.setInsNo(insNo);
		rtgsMessages.setChrgsAmt(chrgAmnt);
		rtgsMessages.setMobEmlType(Byte.valueOf(mobEmlType + ""));
		rtgsMessages.setMobileEmail(mobileNo);
		rtgsMessages.setNutrno(nutrNo);
		rtgsMessages.setRreasondesc(" ");
		rtgsMessages.setOnutrno(" ");
		rtgsMessages.setMsgId(" ");
		rtgsMessages.setRmsgType(" ");
		rtgsMessages.setTransId(" ");
		rtgsMessages.setOmsgDate(DateUtil.getDateFromString("1990-01-01"));
		rtgsMessages.setOmsgTime(DateUtil.getDateFromString("1990-01-01"));
		rtgsMessages.setInterfaceDate(DateUtil.getDateFromString("1990-01-01"));
		rtgsMessages.setInterfaceTime(DateUtil.getDateFromString("1990-01-01"));
		rtgsMessages.setOrgRefNo(" ");
		rtgsMessages.setDbtrAddMk(999998);
		rtgsMessages.setDbtrAddMb(drLbrCode.intValue());
		rtgsMessages.setDbtrAddMs(Short.valueOf("0"));
		rtgsMessages.setDbtrAddMd(DateUtil.convertDateFormat(new Date()));
		rtgsMessages.setDbtrAddMt(DateUtil.getFormattedTime(new Date()));
		rtgsMessages.setDbtrAddCk(999998);
		rtgsMessages.setDbtrAddCb(drLbrCode.intValue());
		rtgsMessages.setDbtrAddCs(Short.valueOf("0"));
		rtgsMessages.setDbtrAddCd(DateUtil.convertDateFormat(new Date()));
		rtgsMessages.setDbtrAddCt(DateUtil.getFormattedTime(new Date()));
		rtgsMessages.setDbtrLupdMk(999998);
		rtgsMessages.setDbtrLupdMb(drLbrCode.intValue());
		rtgsMessages.setDbtrLupdMs(Short.valueOf("0"));
		rtgsMessages.setDbtrLupdMd(DateUtil.convertDateFormat(new Date()));
		rtgsMessages.setDbtrLupdMt(DateUtil.getFormattedTime(new Date()));
		rtgsMessages.setDbtrLupdCk(999998);
		rtgsMessages.setDbtrLupdCb(drLbrCode.intValue());
		rtgsMessages.setDbtrLupdCs(Short.valueOf("0"));
		rtgsMessages.setDbtrLupdCd(DateUtil.convertDateFormat(new Date()));
		rtgsMessages.setDbtrLupdCt(DateUtil.getFormattedTime(new Date()));
		rtgsMessages.setDbtrTauthDone(Short.valueOf("1"));
		rtgsMessages.setDbtrRecStat(Byte.valueOf("1"));
		rtgsMessages.setDbtrAuthDone(Byte.valueOf("1"));
		rtgsMessages.setDbtrAuthNeeded(Byte.valueOf("0"));
		rtgsMessages.setDbtrUpdtChkId(Short.valueOf("1"));
		rtgsMessages.setDbtrLhisTrnNo(0);
		return rtgsMessages;
	}

	public static D946120 prepareRtgsMessageListDatFile(Long drLbrCode, char iwOwMsg, String msgSType, Date tranDate,
			String refNo, Long srNo, String fldNo, Long catType, String fld1, String fld2, String fld3, String fld4,
			String fld5, Double sbiChrgsAmt, Double sbiTotalAmt, Long benifBankCode, String benifBankName,
			String nepalBenifAcNo, String benifMobileNo) {
		D946120 rtgsMessageListDatFile = new D946120();

		D946120Id id = new D946120Id();
		id.setObrCode(drLbrCode.intValue());
		id.setIwOwMsg(iwOwMsg);
		id.setMsgDate(tranDate);
		id.setMsgStype(msgSType);
		id.setRefNo(refNo);
		id.setSrNo(srNo.intValue());
		rtgsMessageListDatFile.setId(id);
		rtgsMessageListDatFile.setFldNo(fldNo);
		rtgsMessageListDatFile.setCatType(Byte.valueOf(catType + ""));
		rtgsMessageListDatFile.setFld1(fld1);
		rtgsMessageListDatFile.setFld2(fld2);
		rtgsMessageListDatFile.setFld3(fld3);
		rtgsMessageListDatFile.setFld4(fld4);
		rtgsMessageListDatFile.setFld5(fld5);
		rtgsMessageListDatFile.setSbichrgsamt(sbiChrgsAmt);
		// rtgsMessageListDatFile.setSbitotalamt(sbiTotalAmt);
		rtgsMessageListDatFile.setBenifbankcd(benifBankCode.intValue());
		rtgsMessageListDatFile.setBenifbankname(benifBankName);
		rtgsMessageListDatFile.setNepalbenifacno(nepalBenifAcNo);
		rtgsMessageListDatFile.setBenifmobileno(benifMobileNo);
		rtgsMessageListDatFile.setDbtrAddMk(999998);
		rtgsMessageListDatFile.setDbtrAddMb(drLbrCode.intValue());
		rtgsMessageListDatFile.setDbtrAddMs(Short.valueOf("0"));
		rtgsMessageListDatFile.setDbtrAddMd(DateUtil.convertDateFormat(new Date()));
		rtgsMessageListDatFile.setDbtrAddMt(DateUtil.getFormattedTime(new Date()));
		rtgsMessageListDatFile.setDbtrAddCk(999998);
		rtgsMessageListDatFile.setDbtrAddCb(drLbrCode.intValue());
		rtgsMessageListDatFile.setDbtrAddCs(Short.valueOf("0"));
		rtgsMessageListDatFile.setDbtrAddCd(DateUtil.convertDateFormat(new Date()));
		rtgsMessageListDatFile.setDbtrAddCt(DateUtil.getFormattedTime(new Date()));
		rtgsMessageListDatFile.setDbtrLupdMk(999998);
		rtgsMessageListDatFile.setDbtrLupdMb(drLbrCode.intValue());
		rtgsMessageListDatFile.setDbtrLupdMs(Short.valueOf("0"));
		rtgsMessageListDatFile.setDbtrLupdMd(DateUtil.convertDateFormat(new Date()));
		rtgsMessageListDatFile.setDbtrLupdMt(DateUtil.getFormattedTime(new Date()));
		rtgsMessageListDatFile.setDbtrLupdCk(999998);
		rtgsMessageListDatFile.setDbtrLupdCb(drLbrCode.intValue());
		rtgsMessageListDatFile.setDbtrLupdCs(Short.valueOf("0"));
		rtgsMessageListDatFile.setDbtrLupdCd(DateUtil.convertDateFormat(new Date()));
		rtgsMessageListDatFile.setDbtrLupdCt(DateUtil.getFormattedTime(new Date()));
		rtgsMessageListDatFile.setDbtrTauthDone(Short.valueOf("1"));
		rtgsMessageListDatFile.setDbtrRecStat(Byte.valueOf("1"));
		rtgsMessageListDatFile.setDbtrAuthDone(Byte.valueOf("0"));
		rtgsMessageListDatFile.setDbtrAuthNeeded(Byte.valueOf("0"));
		rtgsMessageListDatFile.setDbtrUpdtChkId(Short.valueOf("1"));
		rtgsMessageListDatFile.setDbtrLhisTrnNo(0);
		return rtgsMessageListDatFile;
	}

	
	public D946020 validateOrdAcct(D946020 rtgsMessages) {

		rtgsMessages.getOrdDesc1();
		rtgsMessages.getOrdDesc2();
		rtgsMessages.getOrdDesc3();
		rtgsMessages.getOrdDesc4();

		rtgsMessages.setOrdDesc1(validateOrdAcctProcess(rtgsMessages.getOrdDesc1()));
		rtgsMessages.setOrdDesc2(validateOrdAcctProcess(rtgsMessages.getOrdDesc2()));
		rtgsMessages.setOrdDesc3(validateOrdAcctProcess(rtgsMessages.getOrdDesc3()));
		rtgsMessages.setOrdDesc4(validateOrdAcctProcess(rtgsMessages.getOrdDesc4()));
		return rtgsMessages;

	}

	String validateOrdAcctProcess(String ordesc) {
		StringBuffer newordescSB = new StringBuffer();
		if (ordesc != null) {
			for (int i = 0; i < ordesc.length(); i++) {
				int asciiOfOrddec = (int) ordesc.charAt(i);
				if (i == 0) {
					if (asciiOfOrddec == 58) {

					}
					if (asciiOfOrddec == 32) {
					}
				}
				for (int j = 32; j <= 126; j++) {
					if (asciiOfOrddec >= 65 && asciiOfOrddec <= 90) {
						newordescSB.append(ordesc.charAt(i));
						break;
					}
					if (asciiOfOrddec >= 97 && asciiOfOrddec <= 122) {
						newordescSB.append(ordesc.charAt(i));
						break;
					}
					if (asciiOfOrddec >= 48 && asciiOfOrddec <= 58) {
						newordescSB.append(ordesc.charAt(i));
						break;
					}
					if (asciiOfOrddec == 46) {
						newordescSB.append(ordesc.charAt(i));
						break;
					}
					if (asciiOfOrddec == 32) {
						newordescSB.append(ordesc.charAt(i));
						break;
					} else {
						newordescSB.append(" ");
						break;
					}
				}
			}
		}
		return newordescSB.toString();
	}

	public IMPSTransactionResponse validateBatchCode(Long lbrCode, String batchCode, Date operationDate) {
		IMPSTransactionResponse trnxRes = new IMPSTransactionResponse();
		if (batchCode == null || batchCode.trim().equalsIgnoreCase("")) {
			logger.error("Batch Code can not be blank.");
			trnxRes.setResponse(MSGConstants.ERROR);
			trnxRes.setErrorMessage("Batch Code can not be blank.");
			return trnxRes;
		}

		// D010001
		D010001 batchMaster = DataUtils.getBatchMasterDetails(lbrCode.intValue(), batchCode);
		if (null == batchMaster) {
			logger.error("Batch Code not found.");
			trnxRes.setResponse(MSGConstants.ERROR);
			trnxRes.setErrorMessage("Batch Code not found.");
			return trnxRes;
		}

		if (batchMaster.getDbtrAuthDone() != 1 || batchMaster.getDbtrAuthNeeded() != 0) {
			logger.error("Batch Master not authorised.");
			trnxRes.setResponse(MSGConstants.ERROR);
			trnxRes.setErrorMessage("Batch Master not authorised.");
			return trnxRes;
		}
		// TODO temporary commented.
		/*
		 * if(!batchMaster.getBaludttyp1dr().equalsIgnoreCase("ABCD")){
		 * logger.error("All Stages in Entry Stage not defined (Debit)."); return
		 * WSUtils.getReturnWSTxOutput(SwiftCoreConstants.ERROR,
		 * "All Stages in Entry Stage not defined (Debit).", "101", "", "", ""); }
		 * 
		 * if(!batchMaster.getBaludttyp1cr().equalsIgnoreCase("ABCD")){
		 * logger.error("All Stages in Entry Stage not defined (Credit)."); return
		 * WSUtils.getReturnWSTxOutput(SwiftCoreConstants.ERROR,
		 * "All Stages in Entry Stage not defined (Credit).", "101", "", "", ""); }
		 */

		// D010004
		D010004 batchesDirectory = DataUtils.getDailyBatchesDirectoryDetails(lbrCode, operationDate, batchCode);
		if (null == batchesDirectory) {
			logger.error("dbdRec read error.");
			trnxRes.setResponse(MSGConstants.ERROR);
			trnxRes.setErrorMessage("dbdRec read error.");
			return trnxRes;
		}

		if (batchesDirectory.getDbtrAuthDone() != 1 || batchesDirectory.getDbtrAuthNeeded() != 0) {
			logger.error("Daily Batch not authorized.");
			trnxRes.setResponse(MSGConstants.ERROR);
			trnxRes.setErrorMessage("Daily Batch not authorized.");
			return trnxRes;
		}

		if (batchesDirectory.getInProcFlag() == 'Y') {
			logger.error("Batch in process.");
			trnxRes.setResponse(MSGConstants.ERROR);
			trnxRes.setErrorMessage("Batch in process.");
			return trnxRes;
		}

		if (batchesDirectory.getStat() != 1L && batchesDirectory.getStat() != 2L) {
			logger.error("Batch not opened.");
			trnxRes.setResponse(MSGConstants.ERROR);
			trnxRes.setErrorMessage("Batch not opened.");
			return trnxRes;
		}

		// D010007
		D010007 dayOpenCloseFile = DataUtils.getOpenDayForVoucherBrowse(lbrCode.intValue(), operationDate);
		if (null == dayOpenCloseFile) {
			logger.error("Day Is Closed.");
			trnxRes.setResponse(MSGConstants.ERROR);
			trnxRes.setErrorMessage("Day Is Closed.");
			return trnxRes;
		}
		// TODO temporary commented.
		/*
		 * if(dayOpenCloseFile.getDayendflag() != 1L){ logger.error("Day Is Closed.");
		 * return WSUtils.getReturnWSTxOutput(SwiftCoreConstants.ERROR,
		 * "Day Is Closed.", "101", "", "", ""); }
		 */

		trnxRes.setValid(true);
		return trnxRes;
	}

	private static D010004 getDailyBatchesDirectoryDetails(Long lbrCode, Date operationDate, String batchCode) {
		long startTime = System.currentTimeMillis();
		D010004 dailyBatchesDirectory = null;
		String queryString = "from D010004 where id.lbrCode=? and id.entryDate=? and id.batchCd=?";
		Query queryObject = HBUtil.getSessionFactory().openSession().createQuery(queryString);
		queryObject.setLong(0, lbrCode);
		queryObject.setDate(1, operationDate);
		queryObject.setString(2, batchCode.trim());
		List<D010004> list = queryObject.list();
		if (null != list && !list.isEmpty()) {
			dailyBatchesDirectory = list.get(0);
		}

		long endTime = System.currentTimeMillis();
		logger.debug("Instrumentation :<MobileRegistrationDAOImpl.java>:<getDailyBatchesDirectoryDetails>: "
				+ (endTime - startTime));
		return dailyBatchesDirectory;
	}

	public static Date getOpenDate(int brCode) {
		Session session2 = HBUtil.getSessionFactory().openSession();
		try {
			D001004Id id = new D001004Id();
			id.setCode("LASTOPENDATE");
			id.setLbrCode(brCode);
			D001004 d04 = session2.get(D001004.class, id);
			logger.error("d04::>>" + d04);
			session2.close();
			session2 = null;

			if (d04 != null) {
				return sdf.parse(d04.getValue().trim());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean pingHost(String host, int port, int timeout) {
		try (Socket socket = new Socket()) {
			socket.connect(new InetSocketAddress(host, port), timeout);
			return true;
		} catch (IOException e) {
			return false; // Either timeout or unreachable or failed DNS lookup.
		}
	}

	public static String getNextcustNo() {
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		Criteria criteria = session.createCriteria(D001005.class);
		criteria.add(Restrictions.eq("id.lbrCode", 0));
		criteria.add(Restrictions.eq("id.catType", "GENCUSTN"));
		criteria.addOrder(Order.desc("id.lnodate"));
		List<D001005> d001005 = criteria.list();
		d001005.get(0).setLastNo(d001005.get(0).getLastNo() + 1);
		String custNo = "" + Long.valueOf(d001005.get(0).getLastNo());
		Query query = session.createQuery("UPDATE D001005 SET LastNo = '" + custNo
				+ "' WHERE LBrCode = 0 AND Cat = 'O' AND CatType = 'GENCUSTN' AND Lnodate = '"
				+ d001005.get(0).getId().getLnodate() + "'");
		int rows = query.executeUpdate();
		t.commit();
		session.close();
		session = null;
		logger.error("rows::>>" + rows);
		return custNo;
	}

	public static D009011 getCustMataster() {
		Session session = HBUtil.getSessionFactory().openSession();
		D009011 d009011 = session.load(D009011.class, 53393);
		session.close();
		// session=null;
		logger.error("d009011::>>" + d009011);
		return d009011;
	}

	public static D009022 getAccountMaster(int lbrCode, String accNo) {
		Session session = HBUtil.getSessionFactory().openSession();
		D009022Id id = new D009022Id();
		id.setLbrCode(lbrCode);
		id.setPrdAcctId(accNo);
		D009022 d009022 = session.get(D009022.class, id);
		return d009022;
	}

	public static D130001 getstopChequeCharges(int lbrCode, String chgType, String insType) {
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D130001.class);
		criteria.add(Restrictions.eq("id.lbrCode", lbrCode));
		criteria.add(Restrictions.eq("id.chgType", Byte.valueOf(chgType)));
		criteria.add(Restrictions.eq("id.insType", Short.valueOf(insType)));
		criteria.addOrder(Order.desc("id.effDate"));
		List<D130001> list = criteria.list();
		session.close();
		session = null;
		if (list == null || list.isEmpty())
			return null;
		else
			return list.get(0);
	}

	public static List<GstChargesMaster> getLatestCharge(String chgType, Date effDate) {
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(GstChargesMaster.class);
		criteria.add(Restrictions.eq("id.chgtype", Long.valueOf(chgType)));
		criteria.add(Restrictions.le("id.effdate", effDate));
		List<GstChargesMaster> list = criteria.list();
		session.close();
		session = null;
		if (list == null || list.isEmpty())
			return null;
		else
			return list;
	}

	public static D130031 getServiceCharges(String chgtype) {
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D130031.class);
		criteria.add(Restrictions.eq("id.chgType", Byte.valueOf(chgtype)));
		criteria.addOrder(Order.desc("id.effDate"));
		List<D130031> list = criteria.list();
		session.close();
		session = null;
		if (list == null || list.isEmpty())
			return null;
		else
			return list.get(0);
	}

	public static HashMap<String, String> stopChequeTransactionProcess(int lbrCode, String accNo, int benBrCode,
			String benAccNo, String transType, String narration, double amount, String rrn) {
		VoucherCommon common = new VoucherCommon();
		HashMap<String, String> resultMap = new HashMap<>();
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		if (lbrCode == benBrCode) {
			logger.error("Same Branch Fund Transfer....");
			int setNo = VoucherCommon.getNextSetNo();
			int mainScrollNo = VoucherCommon.getNextScrollNo();
			common.stopChequeDr(lbrCode, accNo, transType.toUpperCase(), setNo, VoucherCommon.getNextScrollNo(),
					narration, amount, rrn, mainScrollNo, session);
			// common.balance(lbrCode, accNo, amount, "D");
			CoreTransactionMPOS.balance(lbrCode, accNo.trim(), amount, "D", session);
			if (!common.isAborted) {
				logger.error("Transaction successful");
				common.creditSameBranch(benBrCode, benAccNo, transType, setNo, VoucherCommon.getNextScrollNo(),
						narration, amount, rrn, mainScrollNo, session);
				CoreTransactionMPOS.balance(benBrCode, benAccNo.trim(), amount, "C", session);
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
				t.commit();
				session.close();
				session = null;
				t = null;
				resultMap.put(Code.RESULT, Code.ERROR);
				resultMap.put(Code.SETNO, "");
				resultMap.put(Code.SCROLLNO, "");
				return resultMap;
			}
		}
		return null;
	}

	/*
	 * public D130014 getChequeChargeType(Long lbrcode, Long instrumentType, String
	 * prdcd, Long chargeType) throws Exception { List<D130014>list=new
	 * ArrayList<D130014>(); String queryString =
	 * "FROM ChequeBookChargeMaster WHERE lbrcode=:lbrcode AND PRDCD=:prdcd AND" +
	 * " instype=:instrumentType AND chgtype=:chargeType AND dbtrauthdone=0 AND effdate IN (SELECT max(effdate) "
	 * + " FROM ChequeBookChargeMaster WHERE lbrcode=:lbrcode AND prdcd=:prdcd AND "
	 * + " instype=:instrumentType AND chgtype=:chargeType)"; String queryString =
	 * "from D130014 where id.lbrCode=:lbrcode and id.prdCd=:prdcd and id.insType=:instrumentType and id.chgType=:chargeType and id.acctType=0 order by id.effDate desc"
	 * ; Query queryObject = null; queryObject =
	 * HBUtil.getSessionFactory().openSession().createQuery(queryString);
	 * queryObject.setParameter("lbrcode", lbrcode.intValue());
	 * queryObject.setParameter("prdcd", prdcd);
	 * queryObject.setParameter("chargeType", chargeType.intValue());
	 * queryObject.setParameter("instrumentType", instrumentType.intValue());
	 * //return (ChequeBookChargeMaster) queryObject.uniqueResult();
	 * list=queryObject.list(); if(list!=null && list.size()>0){ return list.get(0);
	 * }else{ return null; } }
	 */
	public static CustomerDetails validateAccNoIfsc(String tType, String accNoIfc) {
		CustomerDetails response = new CustomerDetails();
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		Criteria criteria = session.createCriteria(D350038.class);
		if (tType.equalsIgnoreCase(MSGConstants.P2P_TRANSACTION))
			criteria.add(Restrictions.eq("nbin", Integer.valueOf(accNoIfc.substring(0, 4))));
		if (tType.equalsIgnoreCase(MSGConstants.P2A_TRANSACTION))
			criteria.add(Restrictions.ilike("ifscode", accNoIfc.substring(0, 4) + "%"));
		List<D350038> list = criteria.list();
		logger.error("" + list.size());
		session.close();
		session = null;
		t = null;
		if (list == null || list.isEmpty()) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.NBIN_NOT_REGISTERED);
			return response;
		}
		response.setResponse(MSGConstants.SUCCESS);
		response.setErrorMsg(MSGConstants.SUCCESS_MSG);
		return response;
	}

	public static CustomerDetails validateIfsc(String ifsc) {
		CustomerDetails response = new CustomerDetails();
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria2 = session.createCriteria(D946022.class);
		criteria2.add(Restrictions.eq("ifsccd", ifsc));
		List<D946022> lists = criteria2.list();
		session.close();
		session = null;
		if (lists == null || lists.isEmpty()) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.INVALID_IFS_CODE);
			return response;
		} else {
			response.setResponse(MSGConstants.SUCCESS);
			response.setErrorMsg(MSGConstants.SUCCESS_MSG);
			return response;
		}
	}

	public static List<D350038> checkNBINorIFSC(String tType, String nbinOrIfsc, Session session) {
		try {
			Criteria criteria = session.createCriteria(D350038.class);
			if (tType.equalsIgnoreCase(MSGConstants.P2P_TRANSACTION))
				criteria.add(Restrictions.eq("nbin", Integer.valueOf(nbinOrIfsc.substring(0, 4))));
			if (tType.equalsIgnoreCase(MSGConstants.P2A_TRANSACTION))
				criteria.add(Restrictions.ilike("ifscode", nbinOrIfsc.substring(0, 4) + "%"));
			List<D350038> list = criteria.list();
			logger.error("" + list.size());
			return list;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}

	public static List<D350036> getD350036List(String accNo15digit, String mob1, String mmid1, String mob2,
			String mmid2, String amount, String rrn) {
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D350036.class);
		criteria.add(Restrictions.eq("id.lbrCode", Integer.valueOf(accNo15digit.substring(0, 3))));
		/*criteria.add(Restrictions.eq("id.batchCd", "MBTR"));
		criteria.add(Restrictions.eq("mobNo1", mob1));
		criteria.add(Restrictions.eq("mmid1", mmid1));
		criteria.add(Restrictions.eq("mobNo2", mob2));
		criteria.add(Restrictions.eq("mmid2", mmid2));
		criteria.add(Restrictions.eq("tranAmt", Double.valueOf(amount)));
		criteria.add(Restrictions.eq("rrnNo", rrn));*/
		
		criteria.add(Restrictions.eq("id.batchCd", String.format("%1$-8s","MBTR")));
		criteria.add(Restrictions.eq("mobNo1", String.format("%1$-12s",mob1)));
		criteria.add(Restrictions.eq("mmid1", String.format("%1$-8s",mmid1)));
		criteria.add(Restrictions.eq("mobNo2", String.format("%1$-12s",mob2)));
		criteria.add(Restrictions.eq("mmid2", String.format("%1$-8s",mmid2)));
		criteria.add(Restrictions.eq("tranAmt", Double.valueOf(amount)));
		criteria.add(Restrictions.eq("rrnNo", String.format("%1$-20s",rrn)));
		
		List<D350036> list = criteria.list();
		session.close();
		session = null;
		return list;
	}

	public static D047001 getDDSProductCode(int brCode, String accNo) {
		try {
			Session session = HBUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(D047001.class);
			criteria.add(Restrictions.eq("id.lbrCode", brCode));
			criteria.add(Restrictions.eq("id.prdAcctId", accNo));
			List<D047001> list = criteria.list();
			session.close();
			session = null;
			if (list == null || list.isEmpty())
				return null;
			return list.get(0);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static D010053 isJointHolder(int lbrCode, String accNo, String nameType) {
		Session session = HBUtil.getSessionFactory().openSession();
		D010053Id id = new D010053Id();
		id.setLbrCode(lbrCode);
		id.setNameType(Byte.valueOf(nameType.trim()));
		id.setPrdAcctId(accNo.trim());
		D010053 d010053 = session.get(D010053.class, id);
		session.close();
		session = null;
		return d010053;
	}

	public static D001002 getLookUp(int codeType, String code) {
		try {
			Session session = HBUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(D001002.class);
			criteria.add(Restrictions.eq("id.codeType", codeType));
			criteria.add(Restrictions.eq("id.code", code));
			List<D001002> list = criteria.list();
			session.close();
			session = null;
			if (list.size() > 0)
				return list.get(0);
			else
				return null;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}

	// SELECT * FROM D001005 WHERE LBrCode='3' AND CatType='ACCTNUM' AND Cat='O'
	public static String getNextAccNo(int brCode, String catType, char cat, String prodCode, Session session) {
		logger.error("brCode::>>" + brCode);//
		logger.error("cat::>>" + cat);
		logger.error("catType::>>" + catType);
		logger.error("prodCode::>>" + prodCode);
		// Session session=session;//HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D001005.class);
		criteria.add(Restrictions.eq("id.lbrCode", brCode));
		criteria.add(Restrictions.eq("id.catType", catType.trim()));
		criteria.add(Restrictions.eq("id.cat", cat));
		criteria.add(Restrictions.eq("id.code1", prodCode.trim()));
		criteria.addOrder(Order.desc("id.lnodate"));
		List<D001005> list = criteria.list();
		if (list == null || list.isEmpty()) {
			logger.error("amar");
			// session.close();
			// session=null;

			return null;
		}
		D001005 d001005 = list.get(0);
		if (d001005 == null) {
			logger.error("amar1");
			return null;
		}
		int accNo = Integer.valueOf(d001005.getLastNo()) + 1;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
		String dateStr = simpleDateFormat.format(DataUtils.getOpenDate(brCode));

		Query q = session.createSQLQuery("UPDATE dbo.D001005 SET LastNo = " + accNo + " WHERE LBrCode = " + brCode
				+ " AND Cat = '" + cat + "' AND CatType = '" + catType + "' AND Code1 = '" + prodCode
				+ "' AND Lnodate < '" + dateStr + "'");
		int row = q.executeUpdate();
		if (row > 0)
			logger.error("Row updated successfully.....");
		String maxAccNo = String.format("%08d", Long.valueOf(accNo));
		Logger.error("maxAccNo.length()::>>>" + maxAccNo.length());
		String acc32 = String.format("%-8s", prodCode.trim()) + "00000000" + maxAccNo + "00000000";
		return acc32 + "";
	}

	public static D130008 getD130008(int lbrCode, String acctNo) {
		D130008 d130008Obj = null;
		Session session = HBUtil.getSessionFactory().openSession();
		try {
			D130008Id id = new D130008Id();
			id.setChgType(Byte.valueOf("20"));
			id.setLbrCode(lbrCode);
			id.setPrdAcctId(acctNo);
			d130008Obj = session.get(D130008.class, id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
			session = null;
		}
		return d130008Obj;
	}

	public static D030003 getLoanCharges(int lbrCode, String accNo, Session session) {
		D030003Id id = new D030003Id();
		id.setLbrCode(lbrCode);
		id.setPrdAcctId(accNo);
		D030003 d030003 = session.get(D030003.class, id);
		if (d030003 == null)
			return null;
		else
			return d030003;
	}

	public static D030002 getLoanChargesAcc(int lbrCode, String prodCode, Session session) {
		D030002Id id = new D030002Id();
		id.setLbrCode(lbrCode);
		id.setPrdCd(prodCode);
		D030002 d030002 = session.get(D030002.class, id);
		if (d030002 == null)
			return null;
		else
			return d030002;
	}

	public static D350078 getMobNo(String custNo, Session session) {
		Criteria criteria = session.createCriteria(D350078.class);
		criteria.add(Restrictions.eq("id.custNo", custNo.trim()));
		List<D350078> list = criteria.list();
		criteria = null;
		if (list == null || list.isEmpty())
			return null;
		else
			return list.get(0);
	}

	public static D390077 getPrepaidcardObject(String mobileNo, String cardAliasNo) {
		Session session = HBUtil.getSessionFactory().openSession();
		try {
			/*
			 * D390077Id id=new D390077Id(); id.setCardAlias(cardAliasNo.trim());
			 * id.setMobileNo(mobileNo.trim()); return session.get(D390077.class, id);
			 */

			Criteria criteria = session.createCriteria(D390077.class);
			criteria.add(Restrictions.eq("id.mobileNo", mobileNo));
			criteria.add(Restrictions.eq("id.cardAlias", cardAliasNo));
			criteria.add(Restrictions.eq("onboardflag", "Y"));
			criteria.add(Restrictions.eq("kycflag", "Y"));
			List<D390077> list = criteria.list();
			if (list == null || list.isEmpty())
				return null;
			else
				return list.get(0);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("ERROR", e);
			return null;
		} finally {
			session.close();
			session = null;
		}
	}

	public static CustomerPhysicalCardOnboardingreq getAccDetails(D009022 d009022) {
		// TODO Auto-generated method stub
		logger.error("d009022::>>" + d009022);
		CustomerPhysicalCardOnboardingreq res = new CustomerPhysicalCardOnboardingreq();
		Session session = HBUtil.getSessionFactory().openSession();
		try {
			Criteria criteria = session.createCriteria(Customerdetails.class);
			criteria.add(Restrictions.eq("id.custNo", d009022.getCustNo()));
			List<Customerdetails> list = criteria.list();
			logger.error(list);
			criteria = null;

			String fname = "", mname = "", lname = "";
			String[] nameArr = d009022.getLongName().trim().split(" ");
			if (nameArr.length >= 2) {
				fname = nameArr[0];
				mname = nameArr[1];
				lname = nameArr[2];
			} else if (nameArr.length >= 1) {
				fname = nameArr[0];
				mname = nameArr[1];
			} else if (nameArr.length >= 0)
				fname = nameArr[0];
			else
				fname = "NONAME";
			Customerdetails customerdetails = list.get(0);
			logger.error("customerdetails::>>" + customerdetails);
			D500028 cityCode = LookUpServiceImpl.getCity(customerdetails.getId().getCityCd());
			String city = "";
			if (cityCode != null)
				city = cityCode.getPlaceCdDesc() != null ? cityCode.getPlaceCdDesc().trim() : cityCode.getPlaceCdDesc();

			D500025 stateCode = LookUpServiceImpl.getState(cityCode.getStateCd());
			String state = "";
			if (stateCode != null)
				state = stateCode.getStateDesc() != null ? stateCode.getStateDesc().trim() : stateCode.getStateDesc();

			D500027 countryCode = LookUpServiceImpl.getCountryCode(cityCode.getCntryCd().trim());
			String country = "";
			if (countryCode != null)
				country = countryCode.getCountryName() != null ? countryCode.getCountryName().trim()
						: countryCode.getCountryName();
			D001002 d001002 = LookUpServiceImpl.getLookUp(MSGConstants.OCCUPATION_LOOKUP,
					customerdetails.getId().getProfCd() + "");
			String ocupation = "";
			if (d001002 != null)
				ocupation = d001002.getCodeDesc();

			String annualIncome = "0";
			if (customerdetails.getId().getAnnualIncome() >= 0 && customerdetails.getId().getAnnualIncome() <= 200000)
				annualIncome = "6";// 1--> For Production
			else if (customerdetails.getId().getAnnualIncome() > 200000
					&& customerdetails.getId().getAnnualIncome() <= 500000)
				annualIncome = "7";// 2--> For Production
			else if (customerdetails.getId().getAnnualIncome() > 500000
					&& customerdetails.getId().getAnnualIncome() <= 1000000)
				annualIncome = "8";// 3--> For Production
			else if (customerdetails.getId().getAnnualIncome() > 500000
					&& customerdetails.getId().getAnnualIncome() <= 1000000)
				annualIncome = "9";// 4--> For Production

			int ocupationCode = 0;
			if (customerdetails.getId().getProfCd() == 1)
				ocupationCode = 16; // 1-->> For production
			else if (customerdetails.getId().getProfCd() == 12)
				ocupationCode = 15; // 2-->> For production
			else if (customerdetails.getId().getProfCd() == 2)
				ocupationCode = 17; // 4-->> For production
			else if (customerdetails.getId().getProfCd() == 20)
				ocupationCode = 12; // 5-->> For production
			else if (customerdetails.getId().getProfCd() == 20)
				ocupationCode = 12; // 5-->> For production
			else
				ocupationCode = 14; // 3-->> For production

			res.setAnnualIncome(annualIncome);
			res.setBcagent(d009022.getLongName());
			res.setCardAlias("");// Sent by portal
			res.setCity(city);
			res.setCustomerStatus(customerdetails.getId().getIndOth() + "");
			res.setCustomerType(customerdetails.getId().getProfCd() + "");//// cuistomerType
			res.setEmailaddress(customerdetails.getId().getEmailId());
			res.setDateofbirth(DateUtil.getStringDate(customerdetails.getId().getDob()));
			res.setErrorMsg(MSGConstants.SUCCESS);
			res.setFatcadecl((customerdetails.getId().getTdsYn() == 'Y') ? "1" : "1");
			res.setFirstname(fname);
			res.setMiddlename(mname);
			res.setLastname(lname);
			res.setGender(customerdetails.getId().getSexCode() == "M" ? "MALE" : "FEMALE");
			res.setHeader(new Header());
			res.setLaddress1(customerdetails.getId().getAdd1());
			res.setLaddress2(customerdetails.getId().getAdd2());
			res.setLcity(city);
			res.setLcountry(country);
			res.setLpincode(customerdetails.getId().getPinCode());
			res.setLstate(state);// State
			res.setState(state);
			res.setMobilenumber(customerdetails.getId().getMobileNo());
			res.setMothermaidenname(" ");// Mother Name
			res.setNationality(customerdetails.getId().getRelOff());
			res.setOccupation(ocupation);
			res.setPincode(customerdetails.getId().getPinCode());
			res.setPoliticallyExposedPerson("N");
			res.setProduct(" ");// Card Bin
			res.setResaddress1(customerdetails.getId().getAdd1());
			res.setResaddress2(customerdetails.getId().getAdd2());
			res.setRescountry(customerdetails.getId().getRelOff());
			res.setResponse(MSGConstants.SUCCESS);
			res.setSourceIncomeType(ocupationCode + "");
			return res;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
			res.setResponse(MSGConstants.ERROR);
			res.setEmailaddress(MSGConstants.WEB_SERVICE_ERROR);
			return res;
		} finally {
			session.close();
			session = null;
		}
	}

	public static D350066 prepareD350066Obj(ATMTransactionRequest request, String drCr, int setNo, int scrollNo) {
		D350066 d350066 = new D350066();
		D350066Id id = new D350066Id();
		id.setAquirerId(request.getAcqId());
		id.setAtmdate(DateUtil.getCurrentDate());
		id.setAtmid(request.getAtmid());
		id.setAtmtime(new Date());
		id.setAtmTransType(request.getTransType());// BCUST CUST
		id.setBrCode(Integer.valueOf(request.getBrCode().trim()));
		id.setDestBrCode(Integer.valueOf(request.getBrCode().trim()));
		if (drCr.equalsIgnoreCase(MSGConstants.DR))
			id.setDrCr('D');
		else if (drCr.equalsIgnoreCase(MSGConstants.CR))
			id.setDrCr('C');
		id.setRetrRefNo(request.getRrn());

		d350066.setId(id);
		d350066.setAcctNo102(request.getAtmAccId().substring(0, 28));
		d350066.setAcctNo103(request.getAtmAccId().substring(0, 28));
		d350066.setAcqTerminalBankName(MSGConstants.BANK_NAME);
		d350066.setAtmAcctId(request.getAtmAccId());
		d350066.setAtmauthNo(request.getAtmAuthNo());
		d350066.setAtmcontrollerSuspect('S');// default S
		d350066.setBatchCd(request.getAtmAuthNo());
		d350066.setCaptureDate(DateUtil.getCurrentDate());
		d350066.setEntryDate(DateUtil.getCurrentDate());
		d350066.setErrLeg2(0);
		d350066.setError(0);
		d350066.setFiller("");
		d350066.setIsotransactionCurr(MSGConstants.CURRENCY);// 356 default
		d350066.setIssuerId("");
		d350066.setLbrCode(Integer.valueOf(request.getBrCode()));//
		d350066.setMerchantId(" ");
		d350066.setMsgType(0);
		d350066.setNetworkId(request.getNetworkId());
		d350066.setPan(request.getCardNo());
		d350066.setProcessCode("");
		d350066.setReconNo(0);
		d350066.setResponse(Short.valueOf("0"));
		d350066.setReversal(Byte.valueOf("0"));
		d350066.setScroll(scrollNo);
		d350066.setSetNo(setNo);
		d350066.setOriginalDataElements(MSGConstants.BLANK_STRING);
		d350066.setStatus(Byte.valueOf("0"));
		d350066.setSysAutNo(Integer.valueOf(request.getAtmAuthNo()));
		d350066.setTerminalName(MSGConstants.BANK_NAME);
		d350066.setToAcctId(request.getToAccId());
		d350066.setTransAmt(request.getAmount());
		d350066.setVcrEntryDate(DateUtil.getCurrentDate());
		d350066.setErrLeg2(0);
		d350066.setVcrInstrumentType(11);
		return d350066;
	}

	public static AccountInfo fetchAdharNo(String custNoAccNo, String custNoYN) {
		// TODO Auto-generated method stub
		AccountInfo response = new AccountInfo();
		if (custNoYN.trim().equalsIgnoreCase(MSGConstants.YES)) {
			Session session = HBUtil.getSessionFactory().openSession();
			D009012 d009012 = session.get(D009012.class, Integer.valueOf(custNoAccNo.trim()));
			session.close();
			session = null;
			if (d009012 == null) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.RECORD_NOT_FOUND);
				return response;
			}
			if (d009012.getAdharNo() == null || d009012.getAdharNo().trim().length() < 1
					|| Integer.valueOf(d009012.getAdharNo().trim()) == 0) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_AADHAR_NO);
				return response;
			}
			response.setResponse(MSGConstants.SUCCESS);
			response.setAadharNo(d009012.getAdharNo().trim());
			response.setAccNo(custNoAccNo);
			response.setErrorMsg(MSGConstants.SUCCESS_MSG);
			return response;
		}
		if (custNoYN.trim().equalsIgnoreCase(MSGConstants.NO)) {
			if (custNoAccNo.trim().length() != 15) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_ACCOUNT_NO);
				return response;
			}
			D009022 d009022 = DataUtils.getAccount(custNoAccNo);
			if (d009022 == null) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
				return response;
			}
			TransactionValidationResponse resp = TransactionServiceImpl.validateAccount(d009022, "0", "A");
			if (resp == null) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
				return response;
			}
			if (resp.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(resp.getErrorMsg());
				return response;
			}
			Session session = HBUtil.getSessionFactory().openSession();
			D009012 d009012 = session.get(D009012.class, d009022.getCustNo());
			session.close();
			session = null;
			if (d009012 == null) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.RECORD_NOT_FOUND);
				return response;
			}
			if (d009012.getAdharNo() == null || d009012.getAdharNo().trim().length() < 1
					|| Integer.valueOf(d009012.getAdharNo().trim()) == 0) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_AADHAR_NO);
				return response;
			}
			response.setResponse(MSGConstants.SUCCESS);
			response.setAadharNo(d009012.getAdharNo().trim());
			response.setAccNo(custNoAccNo);
			response.setErrorMsg(MSGConstants.SUCCESS_MSG);
			return response;
		}
		response.setResponse(MSGConstants.ERROR);
		response.setErrorMsg(MSGConstants.INVALID_CONSUMER_NO);
		return response;
	}

	public static OtherChannelServiceResponse aadharSeeding(String custNo, String accNo15digit, String aadharNo,
			String channel) {
		// TODO Auto-generated method stub
		OtherChannelServiceResponse response = new OtherChannelServiceResponse();
		if (channel.trim().equalsIgnoreCase(MSGConstants.IMPS_CHANNEL)) {
			Session session = HBUtil.getSessionFactory().openSession();
			Transaction t = session.beginTransaction();
			D009012 d009012 = session.get(D009012.class, Integer.valueOf(custNo.trim()));
			if (d009012 == null) {
				session.close();
				session = null;
				t = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.RECORD_NOT_FOUND);
				return response;
			}
			if (d009012.getAdharNo() == null || d009012.getAdharNo().trim().length() < 1
					|| Integer.valueOf(d009012.getAdharNo().trim()) == 0) {
				session.close();
				session = null;
				t = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_AADHAR_NO);
				return response;
			}
			if (String.valueOf(Long.valueOf(aadharNo.trim())) != String
					.valueOf(Long.valueOf(d009012.getAdharNo().trim()))) {
				session.close();
				session = null;
				t = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.DATABASE_ADHAR_NO_N_ENTERED_ADHARNO_NOT_MATCHED);
				return response;
			}
			D009022 d009022 = DataUtils.getAccount(accNo15digit);
			if (d009022 == null) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
				return response;
			}
			TransactionValidationResponse resp = TransactionServiceImpl.validateAccount(d009022, "0", "A");
			if (resp == null) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
				return response;
			}
			D009141 d009141 = DataUtils.prepareD009141Obj(aadharNo, d009022.getCustNo(), d009022.getId().getLbrCode(),
					d009022.getId().getPrdAcctId());
			if (d009141 == null) {
				session.close();
				session = null;
				t = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.FAIL_TO_REGISTER_AADHAR_NO);
				return response;
			}
			try {
				session.save(d009141);
				t.commit();
				session.close();
				session = null;
				t = null;
			} catch (Exception e) {
				// TODO: handle exception
				if (t.isActive())
					t.rollback();
				e.printStackTrace();
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
				return response;
			}
		} else if (channel.trim().equalsIgnoreCase(MSGConstants.OTHER_CHANNEL)) {
			if (accNo15digit.trim().length() != 15) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_ACCOUNT_NO);
				return response;
			}
			D009022 d009022 = DataUtils.getAccount(accNo15digit);
			if (d009022 == null) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
				return response;
			}
			TransactionValidationResponse resp = TransactionServiceImpl.validateAccount(d009022, "0", "A");
			if (resp == null) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
				return response;
			}
			if (resp.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(resp.getErrorMsg());
				return response;
			}
			Session session = HBUtil.getSessionFactory().openSession();
			Transaction t = session.beginTransaction();
			D009012 d009012 = session.get(D009012.class, d009022.getCustNo());
			if (d009012 == null) {
				session.close();
				session = null;
				t = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.RECORD_NOT_FOUND);
				return response;
			}
			if (d009012.getAdharNo() == null || d009012.getAdharNo().trim().length() < 1
					|| Long.valueOf(d009012.getAdharNo().trim()) == 0) {
				// logger.error("Adhar No not found in D009012");
				session.close();
				session = null;
				t = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_AADHAR_NO);
				return response;
			}
			D009141_TEST d009141_TEST = session.get(D009141_TEST.class, d009022.getCustNo());
			Criteria criteria = session.createCriteria(D009141_TEST.class);
			criteria.add(Restrictions.eq("custNo", d009022.getCustNo()));
			// criteria.add(Restrictions.eq("adharNo", aadharNo.trim()));
			List<D009141_TEST> list = criteria.list();
			if (list != null && !list.isEmpty()) {
				session.close();
				session = null;
				t = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.AADHAR_NO_ALREADY_REGISTERED);
				return response;
			}
			D009141 d009141 = DataUtils.prepareD009141Obj(aadharNo, d009022.getCustNo(), d009022.getId().getLbrCode(),
					d009022.getId().getPrdAcctId());
			if (d009141 == null) {
				session.close();
				session = null;
				t = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.FAIL_TO_REGISTER_AADHAR_NO);
				return response;
			}
			try {
				session.save(d009141);
				t.commit();
				session.close();
				session = null;
				t = null;
			} catch (Exception e) {
				// TODO: handle exception
				if (t.isActive())
					t.rollback();
				e.printStackTrace();
				response.setResponse(MSGConstants.ERROR);
				resp.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
				return response;
			}
			response.setResponse(MSGConstants.SUCCESS);
			response.setErrorMessage("Dear Customer,As per your request your aadhar card number " + aadharNo
					+ " is registered successfully.");
			return response;
		}
		return null;
	}

	public static D009141 prepareD009141Obj(String adharNo, int custNo, int lbrCode, String prdAcctId) {
		D009141 d009141 = new D009141();
		d009141.setAdharNo(adharNo);
		d009141.setCustNo(custNo);
		d009141.setDbtrAddCb(0);
		d009141.setDbtrAddCd(new Date());
		d009141.setDbtrAddCk(0);
		d009141.setDbtrAddCs(Short.valueOf("0"));
		d009141.setDbtrAddCt(new Date());
		d009141.setDbtrAddMb(0);
		d009141.setDbtrAddMd(new Date());
		d009141.setDbtrAddMk(0);
		d009141.setDbtrAddMs(Short.valueOf("0"));
		d009141.setDbtrAddMt(new Date());
		d009141.setDbtrAuthDone(Byte.valueOf("1"));
		d009141.setDbtrAuthNeeded(Byte.valueOf("0"));
		d009141.setDbtrLhisTrnNo(0);
		d009141.setDbtrLupdCb(0);
		d009141.setDbtrLupdCd(new Date());
		d009141.setDbtrLupdCk(0);
		d009141.setDbtrLupdCs(Short.valueOf("0"));
		d009141.setDbtrLupdCt(new Date());
		d009141.setDbtrLupdMb(0);
		d009141.setDbtrLupdMd(new Date());
		d009141.setDbtrLupdMk(0);
		d009141.setDbtrLupdMs(Short.valueOf("0"));
		d009141.setDbtrLupdMt(new Date());
		d009141.setDbtrRecStat(Byte.valueOf("0"));
		d009141.setDbtrTauthDone(Short.valueOf("1"));
		d009141.setDbtrUpdtChkId(Short.valueOf("0"));
		d009141.setDeactDate(new Date());
		d009141.setDeactReason(Byte.valueOf("0"));
		d009141.setEffFromDate(new Date());
		d009141.setLbrCode(lbrCode);
		d009141.setPrdAcctId(prdAcctId);
		d009141.setRemarks("Registered on " + DateUtil.getcurrentDateString());
		d009141.setStatus(Byte.valueOf("1"));
		return d009141;//
	}

	public static OtherChannelServiceResponse aadharSeedingIVR(String mobileNo, String aadharNo) {
		// TODO Auto-generated method stub
		OtherChannelServiceResponse response = new OtherChannelServiceResponse();

		if (mobileNo == null || mobileNo.trim().length() != 10) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_MOBILE_NO);
			return response;
		}
		List<D009022> d009022 = DataUtils.getAccountFormMobile(mobileNo);
		if (d009022 == null) {
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
			return response;
		}
		/*
		 * TransactionValidationResponse
		 * resp=TransactionServiceImpl.validateAccount(d009022.get(0), "0", "A");
		 * if(resp==null) { response.setResponse(MSGConstants.ERROR);
		 * response.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND); return response; }
		 * if(resp.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
		 * response.setResponse(MSGConstants.ERROR);
		 * response.setErrorMessage(resp.getErrorMsg()); return response; }
		 */
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		D009012 d009012 = session.get(D009012.class, d009022.get(0).getCustNo());
		if (d009012 == null) {
			session.close();
			session = null;
			t = null;
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.RECORD_NOT_FOUND);
			return response;
		}
		if (d009012.getAdharNo() == null || d009012.getAdharNo().trim().length() < 1
				|| Long.valueOf(d009012.getAdharNo().trim()) == 0) {
			// logger.error("Adhar No not found in D009012");
			session.close();
			session = null;
			t = null;
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_AADHAR_NO);
			return response;
		}
		D009141_TEST d009141_TEST = session.get(D009141_TEST.class, d009022.get(0).getCustNo());
		Criteria criteria = session.createCriteria(D009141_TEST.class);
		criteria.add(Restrictions.eq("custNo", d009022.get(0).getCustNo()));
		// criteria.add(Restrictions.eq("adharNo", aadharNo.trim()));
		List<D009141_TEST> list = criteria.list();
		if (list != null && !list.isEmpty()) {
			session.close();
			session = null;
			t = null;
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.AADHAR_NO_ALREADY_REGISTERED);
			return response;
		}
		D009141 d009141 = DataUtils.prepareD009141Obj(aadharNo, d009022.get(0).getCustNo(),
				d009022.get(0).getId().getLbrCode(), d009022.get(0).getId().getPrdAcctId());
		if (d009141 == null) {
			session.close();
			session = null;
			t = null;
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.FAIL_TO_REGISTER_AADHAR_NO);
			return response;
		}
		try {
			session.save(d009141);
			t.commit();
			session.close();
			session = null;
			t = null;
		} catch (Exception e) {
			// TODO: handle exception
			if (t.isActive())
				t.rollback();
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
		response.setResponse(MSGConstants.SUCCESS);
		response.setErrorMessage("Dear Customer,As per your request your aadhar card number " + aadharNo
				+ " is registered successfully.");
		return response;
	}

	public static OtherChannelServiceResponse onBoardPrepaidCardCustomer(CustomerPhysicalCardOnboardingreq request) {
		// TODO Auto-generated method stub
		OtherChannelServiceResponse response = new OtherChannelServiceResponse();
		com.sil.commonswitch.CustomerPhysicalCardOnboardingreq req = preparePrepaidcardOnBoardingOb(request);
		if (req != null) {
			PrepaidCardOnBoarding obj = preparePrepaidcardOnBoardingDatabaseObj(request);
			if (obj == null) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.UNABLE_TO_CREATE_PREPAID_CARD_OBJ);
				return response;
			}
			if (DataUtils.storePrepaidCardObj(obj)) {
				// ===Call RBL Web Service for On Boarding
				if (DataUtils.onBoardRBLCustomer(req)) {
					response.setResponse(MSGConstants.SUCCESS);
					response.setErrorMessage(MSGConstants.PREPAID_CARD_ONBOARDING_SUCCESS);
					return response;
				} else {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.UNABLE_TO_REGISTER_CUSTOMER_FOR_ONBOARDING);
					return response;
				}
			}
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.UNABLE_TO_CREATE_PREPAID_CARD_OBJ);
			return response;
		}
		response.setResponse(MSGConstants.ERROR);
		response.setErrorMessage(MSGConstants.UNABLE_TO_CREATE_PREPAID_CARD_OBJ);
		return response;
	}

	private static boolean onBoardRBLCustomer(com.sil.commonswitch.CustomerPhysicalCardOnboardingreq req) {
		// TODO Auto-generated method stub
		try {
			Client client = ClientBuilder.newClient(new ClientConfig().register(LoggingFilter.class));
			WebTarget webTarget = client.target(ConfigurationLoader.getParameters(false).getProperty("RBL_WS_URI"));
			Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_XML);
			Response res = invocationBuilder.post(Entity.entity(req, MediaType.APPLICATION_XML));
			CustomerPhysicalCardOnboardingres response = res.readEntity(CustomerPhysicalCardOnboardingres.class);
			if (response == null)
				return false;
			if (response.getStatus().equalsIgnoreCase(MSGConstants.SUCCESS))
				return true;
			return false;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}

	public static Header getHeader(String bcagent, String userName, String password) {
		Channelpartnerloginreq req = new Channelpartnerloginreq();
		try {
			req.setBcagent(bcagent);
			req.setPassword(password);
			req.setUsername(userName);
			Header resp = new Header();
			Client client = ClientBuilder.newClient(new ClientConfig().register(LoggingFilter.class));
			WebTarget webTarget = client.target(ConfigurationLoader.getParameters(false).getProperty("RBL_WS_URI"));
			Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_XML);
			Response res = invocationBuilder.post(Entity.entity(req, MediaType.APPLICATION_XML));
			Channelpartnerloginres response = res.readEntity(Channelpartnerloginres.class);
			if (response == null)
				return null;
			resp.setSessiontoken(response.getSessiontoken());
			return resp;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}

	public static com.sil.commonswitch.CustomerPhysicalCardOnboardingreq preparePrepaidcardOnBoardingOb(
			CustomerPhysicalCardOnboardingreq request) {
		com.sil.commonswitch.CustomerPhysicalCardOnboardingreq req = new com.sil.commonswitch.CustomerPhysicalCardOnboardingreq();
		Header header = getHeader(MSGConstants.RBL_BCAGENT, MSGConstants.RBL_USRNAME, MSGConstants.RBL_PASSWORD);
		if (header == null)
			return null;
		req.setAnnualIncome(request.getAnnualIncome());
		req.setBcagent(request.getBcagent());
		req.setCardAlias(request.getCardAlias());
		req.setCity(request.getCity());
		req.setCustomerStatus(request.getCustomerStatus());
		req.setCustomerType(request.getCustomerType());
		req.setDateofbirth(request.getDateofbirth());
		req.setEmailaddress(request.getEmailaddress());
		req.setFatcadecl(request.getFatcadecl());
		req.setFirstname(request.getFirstname());
		req.setGender(request.getGender());
		req.setHeader(header);
		req.setLaddress1(request.getLaddress1());
		req.setLaddress2(request.getLaddress2());
		req.setLastname(request.getLastname());
		req.setLcity(request.getLcity());
		req.setLcountry(request.getLcountry());
		req.setLpincode(req.getLpincode());
		req.setLstate(req.getLstate());
		req.setMiddlename(request.getMiddlename());
		req.setMobilenumber(request.getMobilenumber());
		req.setMothermaidenname(request.getMothermaidenname());
		req.setNationality(request.getNationality());
		req.setOccupation(request.getOccupation());
		req.setPincode(request.getPincode());
		req.setPoliticallyExposedPerson(request.getPoliticallyExposedPerson());
		req.setProduct(request.getProduct());
		req.setResaddress1(request.getResaddress1());
		req.setResaddress2(request.getResaddress2());
		req.setRescountry(request.getRescountry());
		req.setSourceIncomeType(request.getSourceIncomeType());
		req.setState(request.getState());
		return req;
	}

	public static com.sil.commonswitch.PrepaidCardOnBoarding preparePrepaidcardOnBoardingDatabaseObj(
			CustomerPhysicalCardOnboardingreq request) {
		com.sil.commonswitch.PrepaidCardOnBoarding req = new com.sil.commonswitch.PrepaidCardOnBoarding();
		req.setAnnualIncome(request.getAnnualIncome());
		req.setBcagent(request.getBcagent());
		req.setCardAlias(request.getCardAlias());
		req.setCity(request.getCity());
		req.setCustomerStatus(request.getCustomerStatus());
		req.setCustomerType(request.getCustomerType());
		req.setDateofbirth(request.getDateofbirth());
		req.setEmailaddress(request.getEmailaddress());
		req.setFatcadecl(request.getFatcadecl());
		req.setFirstname(request.getFirstname());
		req.setGender(request.getGender());
		req.setLaddress1(request.getLaddress1());
		req.setLaddress2(request.getLaddress2());
		req.setLastname(request.getLastname());
		req.setLcity(request.getLcity());
		req.setLcountry(request.getLcountry());
		req.setLpincode(req.getLpincode());
		req.setLstate(req.getLstate());
		req.setMiddlename(request.getMiddlename());
		req.setMobilenumber(request.getMobilenumber());
		req.setMothermaidenname(request.getMothermaidenname());
		req.setNationality(request.getNationality());
		req.setOccupation(request.getOccupation());
		req.setPincode(request.getPincode());
		req.setPoliticallyExposedPerson(request.getPoliticallyExposedPerson());
		req.setProduct(request.getProduct());
		req.setResaddress1(request.getResaddress1());
		req.setResaddress2(request.getResaddress2());
		req.setRescountry(request.getRescountry());
		req.setSourceIncomeType(request.getSourceIncomeType());
		req.setState(request.getState());
		// req.setBranchno(branchno);
		// req.setAccno(accno);
		return req;
	}

	public static boolean storePrepaidCardObj(PrepaidCardOnBoarding obj) {
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		try {
			session.saveOrUpdate(obj);
			t.commit();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}

	public static boolean getSimSePayTransactionStatus(String reconno, String bankCode, String amount) {
		try {
			Session session = HBUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(SimSePayTrancation.class);
			criteria.add(Restrictions.eq("reconno", reconno));
			criteria.add(Restrictions.eq("bankcode", bankCode));
			criteria.add(Restrictions.eq("amount", Double.valueOf(amount.trim())));
			criteria.add(Restrictions.eq("respcode", ResponseCodes.SUCCESS));
			criteria.add(Restrictions.eq("drcr", "D"));
			List<SimSePayTrancation> list = criteria.list();
			session.close();
			session = null;
			if (list == null || list.isEmpty())
				return false;
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}

	public static ImpsTransactionReport getP2PTransaction(String flag, String fromDate, String toDate) {
		// TODO Auto-generated method stub
		ImpsTransactionReport response = new ImpsTransactionReport();
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D350036.class);
		criteria.add(Restrictions.between("id.entryDate", DateUtil.getDateFromStringDate(fromDate),
				DateUtil.getDateFromStringDate(toDate)));
		List<IMPSTransactionDetail> list = new ArrayList<>();

		criteria.setProjection(Projections.projectionList().add(Projections.property("id.entryDate"), "entryDate")
				.add(Projections.property("id.batchCd"), "batchCd").add(Projections.property("id.setNo"), "setNo")
				.add(Projections.property("id.scrollNo"), "scrollNo").add(Projections.property("mobNo1"), "fromMobNo")
				.add(Projections.property("mmid1"), "fromMMID").add(Projections.property("mobNo2"), "toMobno")
				.add(Projections.property("mmid2"), "toMMID")

				.add(Projections.property("tranAmt"), "amount").add(Projections.property("responseCd"), "responseCode")
				.add(Projections.property("rrnNo"), "refNo").add(Projections.property("drcr"), "drcr"))
				.setResultTransformer(Transformers.aliasToBean(IMPSTransactionDetail.class));

		if (flag.equalsIgnoreCase("ALL")) {
			list = criteria.list();
			if (list.isEmpty() || list.size() < 1) {
				session.close();
				session = null;
				criteria = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NO_TRANSACTION_FOUND);
				return response;
			}
			session.close();
			session = null;
			criteria = null;
			response.setResponse(MSGConstants.SUCCESS);
			response.setErrorMsg(MSGConstants.SUCCESS_MSG);
			response.setImpsTrnReport(list);
			return response;
		}
		if (flag.equalsIgnoreCase(MSGConstants.SUCCESS)) {
			criteria.add(Restrictions.eq("responseCd", ResponseCodes.SUCCESS));
			list = criteria.list();
			if (list.isEmpty() || list.size() < 1) {
				session.close();
				session = null;
				criteria = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NO_TRANSACTION_FOUND);
				return response;
			}
			session.close();
			session = null;
			criteria = null;
			response.setResponse(MSGConstants.SUCCESS);
			response.setErrorMsg(MSGConstants.SUCCESS_MSG);
			response.setImpsTrnReport(list);
			return response;
		}
		if (flag.equalsIgnoreCase(MSGConstants.ERROR)) {
			criteria.add(Restrictions.ne("responseCd", ResponseCodes.SUCCESS));
			list = criteria.list();
			if (list.isEmpty() || list.size() < 1) {
				session.close();
				session = null;
				criteria = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NO_TRANSACTION_FOUND);
				return response;
			}
			session.close();
			session = null;
			criteria = null;
			response.setResponse(MSGConstants.SUCCESS);
			response.setErrorMsg(MSGConstants.SUCCESS_MSG);
			response.setImpsTrnReport(list);
			return response;
		}
		response.setResponse(MSGConstants.ERROR);
		response.setErrorMsg(MSGConstants.INVALID_REQUEST);
		return response;
	}

	public static ImpsTransactionReport getP2ATransaction(String flag, String fromDate, String toDate) {
		// TODO Auto-generated method stub
		ImpsTransactionReport response = new ImpsTransactionReport();
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D350037.class);
		criteria.add(Restrictions.between("id.entryDate", DateUtil.getDateFromStringDate(fromDate),
				DateUtil.getDateFromStringDate(toDate)));
		List<IMPSTransactionDetail> list = new ArrayList<>();
		criteria.setProjection(Projections.projectionList().add(Projections.property("id.entryDate"), "entryDate")
				.add(Projections.property("id.batchCd"), "batchCd").add(Projections.property("id.setNo"), "setNo")
				.add(Projections.property("id.scrollNo"), "scrollNo").add(Projections.property("mobNo1"), "fromMobNo")
				.add(Projections.property("mmid1"), "fromMMID").add(Projections.property("accNo"), "toMobno")
				.add(Projections.property("ifscCd"), "toMMID").add(Projections.property("tranAmt"), "amount")
				.add(Projections.property("responseCd"), "responseCode").add(Projections.property("rrnNo"), "refNo")
				.add(Projections.property("drcr"), "drcr"))
				.setResultTransformer(Transformers.aliasToBean(IMPSTransactionDetail.class));

		if (flag.equalsIgnoreCase("ALL")) {
			list = criteria.list();
			if (list.isEmpty() || list.size() < 1) {
				session.close();
				session = null;
				criteria = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NO_TRANSACTION_FOUND);
				return response;
			}
			session.close();
			session = null;
			criteria = null;
			response.setResponse(MSGConstants.SUCCESS);
			response.setErrorMsg(MSGConstants.SUCCESS_MSG);
			response.setImpsTrnReport(list);
			return response;
		}
		if (flag.equalsIgnoreCase(MSGConstants.SUCCESS)) {
			criteria.add(Restrictions.eq("responseCd", ResponseCodes.SUCCESS));
			list = criteria.list();
			if (list.isEmpty() || list.size() < 1) {
				session.close();
				session = null;
				criteria = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NO_TRANSACTION_FOUND);
				return response;
			}
			session.close();
			session = null;
			criteria = null;
			response.setResponse(MSGConstants.SUCCESS);
			response.setErrorMsg(MSGConstants.SUCCESS_MSG);
			response.setImpsTrnReport(list);
			return response;
		}
		if (flag.equalsIgnoreCase(MSGConstants.ERROR)) {
			criteria.add(Restrictions.ne("responseCd", ResponseCodes.SUCCESS));
			list = criteria.list();
			if (list.isEmpty() || list.size() < 1) {
				session.close();
				session = null;
				criteria = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NO_TRANSACTION_FOUND);
				return response;
			}
			session.close();
			session = null;
			criteria = null;
			response.setResponse(MSGConstants.SUCCESS);
			response.setErrorMsg(MSGConstants.SUCCESS_MSG);
			response.setImpsTrnReport(list);
			return response;
		}
		response.setResponse(MSGConstants.ERROR);
		response.setErrorMsg(MSGConstants.INVALID_REQUEST);
		return response;
	}

	public static ImpsTransactionReport getp2aRevTrn(String flag, String fromDate, String toDate) {
		// TODO Auto-generated method stub
		ImpsTransactionReport response = new ImpsTransactionReport();
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D350059.class);
		criteria.add(Restrictions.between("id.entryDate", DateUtil.getDateFromStringDate(fromDate),
				DateUtil.getDateFromStringDate(toDate)));
		List<IMPSTransactionDetail> list = new ArrayList<>();
		criteria.setProjection(Projections.projectionList().add(Projections.property("id.entryDate"), "entryDate")
				.add(Projections.property("id.batchCd"), "batchCd").add(Projections.property("id.setNo"), "setNo")
				.add(Projections.property("id.scrollNo"), "scrollNo")
				.add(Projections.property("id.mobNo1"), "fromMobNo").add(Projections.property("id.mmid1"), "fromMMID")
				.add(Projections.property("id.accNo"), "toMobno").add(Projections.property("id.ifscCd"), "toMMID")

				.add(Projections.property("id.tranAmt"), "amount")
				.add(Projections.property("id.responseCd"), "responseCode")
				.add(Projections.property("id.rrnNo"), "refNo").add(Projections.property("id.drcr"), "drcr"))
				.setResultTransformer(Transformers.aliasToBean(IMPSTransactionDetail.class));

		if (flag.equalsIgnoreCase("ALL")) {
			criteria.add(Restrictions.ilike("id.mmid1", MSGConstants.NBIN + "%"));
			list = criteria.list();
			if (list.isEmpty() || list.size() < 1) {
				session.close();
				session = null;
				criteria = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NO_TRANSACTION_FOUND);
				return response;
			}
			session.close();
			session = null;
			criteria = null;
			response.setResponse(MSGConstants.SUCCESS);
			response.setErrorMsg(MSGConstants.SUCCESS_MSG);
			response.setImpsTrnReport(list);
			return response;
		}
		if (flag.equalsIgnoreCase(MSGConstants.SUCCESS)) {
			criteria.add(Restrictions.ilike("id.mmid1", MSGConstants.NBIN + "%"));
			criteria.add(Restrictions.eq("responseCd", ResponseCodes.SUCCESS));
			list = criteria.list();
			if (list.isEmpty() || list.size() < 1) {
				session.close();
				session = null;
				criteria = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NO_TRANSACTION_FOUND);
				return response;
			}
			session.close();
			session = null;
			criteria = null;
			response.setResponse(MSGConstants.SUCCESS);
			response.setErrorMsg(MSGConstants.SUCCESS_MSG);
			response.setImpsTrnReport(list);
			return response;
		}
		if (flag.equalsIgnoreCase(MSGConstants.ERROR)) {
			criteria.add(Restrictions.ilike("id.mmid1", MSGConstants.NBIN + "%"));
			criteria.add(Restrictions.ne("responseCd", ResponseCodes.SUCCESS));
			list = criteria.list();
			if (list.isEmpty() || list.size() < 1) {
				session.close();
				session = null;
				criteria = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NO_TRANSACTION_FOUND);
				return response;
			}
			session.close();
			session = null;
			criteria = null;
			response.setResponse(MSGConstants.SUCCESS);
			response.setErrorMsg(MSGConstants.SUCCESS_MSG);
			response.setImpsTrnReport(list);
			return response;
		}
		response.setResponse(MSGConstants.ERROR);
		response.setErrorMsg(MSGConstants.INVALID_REQUEST);
		return response;
	}

	public static ImpsTransactionReport getp2aCreditTrn(String flag, String fromDate, String toDate) {
		// TODO Auto-generated method stub
		ImpsTransactionReport response = new ImpsTransactionReport();
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D350059.class);
		criteria.add(Restrictions.between("id.entryDate", DateUtil.getDateFromStringDate(fromDate),
				DateUtil.getDateFromStringDate(toDate)));
		List<IMPSTransactionDetail> list = new ArrayList<>();
		criteria.setProjection(Projections.projectionList().add(Projections.property("id.entryDate"), "entryDate")
				.add(Projections.property("id.batchCd"), "batchCd").add(Projections.property("id.setNo"), "setNo")
				.add(Projections.property("id.scrollNo"), "scrollNo")
				.add(Projections.property("id.mobNo1"), "fromMobNo").add(Projections.property("id.mmid1"), "fromMMID")
				.add(Projections.property("id.accNo"), "toMobno").add(Projections.property("id.ifscCd"), "toMMID")

				.add(Projections.property("id.tranAmt"), "amount")
				.add(Projections.property("id.responseCd"), "responseCode")
				.add(Projections.property("id.rrnNo"), "refNo").add(Projections.property("id.drcr"), "drcr"))
				.setResultTransformer(Transformers.aliasToBean(IMPSTransactionDetail.class));

		if (flag.equalsIgnoreCase("ALL")) {
			criteria.add(Restrictions.not(Restrictions.ilike("id.mmid1", MSGConstants.NBIN + "%")));
			list = criteria.list();
			if (list.isEmpty() || list.size() < 1) {
				session.close();
				session = null;
				criteria = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NO_TRANSACTION_FOUND);
				return response;
			}
			session.close();
			session = null;
			criteria = null;
			response.setResponse(MSGConstants.SUCCESS);
			response.setErrorMsg(MSGConstants.SUCCESS_MSG);
			response.setImpsTrnReport(list);
			return response;
		}
		if (flag.equalsIgnoreCase(MSGConstants.SUCCESS)) {
			criteria.add(Restrictions.not(Restrictions.ilike("id.mmid1", MSGConstants.NBIN + "%")));
			criteria.add(Restrictions.eq("responseCd", ResponseCodes.SUCCESS));
			list = criteria.list();
			if (list.isEmpty() || list.size() < 1) {
				session.close();
				session = null;
				criteria = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NO_TRANSACTION_FOUND);
				return response;
			}
			session.close();
			session = null;
			criteria = null;
			response.setResponse(MSGConstants.SUCCESS);
			response.setErrorMsg(MSGConstants.SUCCESS_MSG);
			response.setImpsTrnReport(list);
			return response;
		}
		if (flag.equalsIgnoreCase(MSGConstants.ERROR)) {
			criteria.add(Restrictions.not(Restrictions.ilike("id.mmid1", MSGConstants.NBIN + "%")));
			criteria.add(Restrictions.ne("responseCd", ResponseCodes.SUCCESS));
			list = criteria.list();
			if (list.isEmpty() || list.size() < 1) {
				session.close();
				session = null;
				criteria = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NO_TRANSACTION_FOUND);
				return response;
			}
			session.close();
			session = null;
			criteria = null;
			response.setResponse(MSGConstants.SUCCESS);
			response.setErrorMsg(MSGConstants.SUCCESS_MSG);
			response.setImpsTrnReport(list);
			return response;
		}
		response.setResponse(MSGConstants.ERROR);
		response.setErrorMsg(MSGConstants.INVALID_REQUEST);
		return response;
	}

	public static ImpsTransactionReport getp2pRevTrn(String flag, String fromDate, String toDate) {
		// TODO Auto-generated method stub
		ImpsTransactionReport response = new ImpsTransactionReport();
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D350044.class);
		criteria.add(Restrictions.between("id.entryDate", DateUtil.getDateFromStringDate(fromDate),
				DateUtil.getDateFromStringDate(toDate)));
		List<IMPSTransactionDetail> list = new ArrayList<>();
		criteria.setProjection(Projections.projectionList().add(Projections.property("id.entryDate"), "entryDate")
				.add(Projections.property("id.batchCd"), "batchCd").add(Projections.property("id.setNo"), "setNo")
				.add(Projections.property("id.scrollNo"), "scrollNo")
				.add(Projections.property("id.mobNo1"), "fromMobNo").add(Projections.property("id.mmid1"), "fromMMID")
				.add(Projections.property("id.mobNo2"), "toMobno").add(Projections.property("id.mmid2"), "toMMID")

				.add(Projections.property("id.tranAmt"), "amount")
				.add(Projections.property("id.responseCd"), "responseCode")
				.add(Projections.property("id.rrnNo"), "refNo").add(Projections.property("id.drcr"), "drcr"))
				.setResultTransformer(Transformers.aliasToBean(IMPSTransactionDetail.class));

		if (flag.equalsIgnoreCase("ALL")) {
			criteria.add(Restrictions.ilike("id.mmid1", MSGConstants.NBIN + "%"));
			list = criteria.list();
			if (list.isEmpty() || list.size() < 1) {
				session.close();
				session = null;
				criteria = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NO_TRANSACTION_FOUND);
				return response;
			}
			session.close();
			session = null;
			criteria = null;
			response.setResponse(MSGConstants.SUCCESS);
			response.setErrorMsg(MSGConstants.SUCCESS_MSG);
			response.setImpsTrnReport(list);
			return response;
		}
		if (flag.equalsIgnoreCase(MSGConstants.SUCCESS)) {
			criteria.add(Restrictions.ilike("id.mmid1", MSGConstants.NBIN + "%"));
			criteria.add(Restrictions.eq("id.responseCd", ResponseCodes.SUCCESS));
			list = criteria.list();
			if (list.isEmpty() || list.size() < 1) {
				session.close();
				session = null;
				criteria = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NO_TRANSACTION_FOUND);
				return response;
			}
			session.close();
			session = null;
			criteria = null;
			response.setResponse(MSGConstants.SUCCESS);
			response.setErrorMsg(MSGConstants.SUCCESS_MSG);
			response.setImpsTrnReport(list);
			return response;
		}
		if (flag.equalsIgnoreCase(MSGConstants.ERROR)) {
			criteria.add(Restrictions.ilike("id.mmid1", MSGConstants.NBIN + "%"));
			criteria.add(Restrictions.ne("id.responseCd", ResponseCodes.SUCCESS));
			list = criteria.list();
			if (list.isEmpty() || list.size() < 1) {
				session.close();
				session = null;
				criteria = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NO_TRANSACTION_FOUND);
				return response;
			}
			session.close();
			session = null;
			criteria = null;
			response.setResponse(MSGConstants.SUCCESS);
			response.setErrorMsg(MSGConstants.SUCCESS_MSG);
			response.setImpsTrnReport(list);
			return response;
		}
		response.setResponse(MSGConstants.ERROR);
		response.setErrorMsg(MSGConstants.INVALID_REQUEST);
		return response;
	}

	public static ImpsTransactionReport getp2pCreditTrn(String flag, String fromDate, String toDate) {
		// TODO Auto-generated method stub
		ImpsTransactionReport response = new ImpsTransactionReport();
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D350044.class);
		criteria.add(Restrictions.between("id.entryDate", DateUtil.getDateFromStringDate(fromDate),
				DateUtil.getDateFromStringDate(toDate)));
		List<IMPSTransactionDetail> list = new ArrayList<>();
		criteria.setProjection(Projections.projectionList().add(Projections.property("id.entryDate"), "entryDate")
				.add(Projections.property("id.batchCd"), "batchCd").add(Projections.property("id.setNo"), "setNo")
				.add(Projections.property("id.scrollNo"), "scrollNo")
				.add(Projections.property("id.mobNo1"), "fromMobNo").add(Projections.property("id.mmid1"), "fromMMID")
				.add(Projections.property("id.mobNo2"), "toMobno").add(Projections.property("id.mmid2"), "toMMID")

				.add(Projections.property("id.tranAmt"), "amount")
				.add(Projections.property("id.responseCd"), "responseCode")
				.add(Projections.property("id.rrnNo"), "refNo").add(Projections.property("id.drcr"), "drcr"))
				.setResultTransformer(Transformers.aliasToBean(IMPSTransactionDetail.class));

		if (flag.equalsIgnoreCase("ALL")) {
			criteria.add(Restrictions.not(Restrictions.ilike("id.mmid1", MSGConstants.NBIN + "%")));
			list = criteria.list();
			if (list.isEmpty() || list.size() < 1) {
				session.close();
				session = null;
				criteria = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NO_TRANSACTION_FOUND);
				return response;
			}
			session.close();
			session = null;
			criteria = null;
			response.setResponse(MSGConstants.SUCCESS);
			response.setErrorMsg(MSGConstants.SUCCESS_MSG);
			response.setImpsTrnReport(list);
			return response;
		}
		if (flag.equalsIgnoreCase(MSGConstants.SUCCESS)) {
			criteria.add(Restrictions.not(Restrictions.ilike("id.mmid1", MSGConstants.NBIN + "%")));
			criteria.add(Restrictions.eq("id.responseCd", ResponseCodes.SUCCESS));
			list = criteria.list();
			if (list.isEmpty() || list.size() < 1) {
				session.close();
				session = null;
				criteria = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NO_TRANSACTION_FOUND);
				return response;
			}
			session.close();
			session = null;
			criteria = null;
			response.setResponse(MSGConstants.SUCCESS);
			response.setErrorMsg(MSGConstants.SUCCESS_MSG);
			response.setImpsTrnReport(list);
			return response;
		}
		if (flag.equalsIgnoreCase(MSGConstants.ERROR)) {
			criteria.add(Restrictions.not(Restrictions.ilike("id.mmid1", MSGConstants.NBIN + "%")));
			criteria.add(Restrictions.ne("id.responseCd", ResponseCodes.SUCCESS));
			list = criteria.list();
			if (list.isEmpty() || list.size() < 1) {
				session.close();
				session = null;
				criteria = null;
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.NO_TRANSACTION_FOUND);
				return response;
			}
			session.close();
			session = null;
			criteria = null;
			response.setResponse(MSGConstants.SUCCESS);
			response.setErrorMsg(MSGConstants.SUCCESS_MSG);
			response.setImpsTrnReport(list);
			return response;
		}
		response.setResponse(MSGConstants.ERROR);
		response.setErrorMsg(MSGConstants.INVALID_REQUEST);
		return response;
	}

	public static GetCustDetailsres getPrepaidCustDetails(String mobileNo) {
		GetCustDetailsres resp = new GetCustDetailsres();
		try {
			GetCustDetailsreq req = new GetCustDetailsreq();
			req.setHeader(getHeader(MSGConstants.RBL_BCAGENT, MSGConstants.RBL_USRNAME, MSGConstants.RBL_PASSWORD));
			req.setMobilenumber(mobileNo);
			Client client = ClientBuilder.newClient(new ClientConfig().register(LoggingFilter.class));
			WebTarget webTarget = client.target(ConfigurationLoader.getParameters(false).getProperty("RBL_WS_URI"));
			Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_XML);
			Response res = invocationBuilder.post(Entity.entity(req, MediaType.APPLICATION_XML));
			resp = res.readEntity(GetCustDetailsres.class);
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
			resp.setStatus(MSGConstants.ERROR);
			// resp.set(MSGConstants.WEB_SERVICE_ERROR);
			return resp;
		}
	}

	public static Accountloadres loadPrepaidCardBal(String accnumber, String amount, String customerid, String RefNo) {
		Accountloadres resp = new Accountloadres();
		try {
			Accountloadreq req = new Accountloadreq();
			req.setHeader(getHeader(MSGConstants.RBL_BCAGENT, MSGConstants.RBL_USRNAME, MSGConstants.RBL_PASSWORD));
			req.setAccnumber(accnumber);
			req.setAmount(amount);
			req.setCustomerid(customerid);
			req.setRefNo(RefNo);
			Client client = ClientBuilder.newClient(new ClientConfig().register(LoggingFilter.class));
			WebTarget webTarget = client.target(ConfigurationLoader.getParameters(false).getProperty("RBL_WS_URI"));
			Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_XML);
			Response res = invocationBuilder.post(Entity.entity(req, MediaType.APPLICATION_XML));
			resp = res.readEntity(Accountloadres.class);
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
			resp.setStatus(MSGConstants.ERROR);
			resp.setDesc(MSGConstants.WEB_SERVICE_ERROR);
			return resp;
		}
	}

	public static List<String> getProductCode(int lbrCode, String accNo) {
		Session session = HBUtil.getSessionFactory().openSession();
		try {
			String query = "SELECT DISTINCT substring(CustPrdAcctId,1,8) FROM D047003 WHERE LBrCode='" + lbrCode
					+ "' AND AgtPrdAcctId='" + accNo + "' AND AcctStat<>3";
			Query q = session.createSQLQuery(query);
			List<String> list = q.list();
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			session.close();
			session = null;
		}
	}

	public static boolean validateRequest(IMPSTransactionRequest req, D390077 d390077) {
		Session session = HBUtil.getSessionFactory().openSession();
		try {
			Criteria criteria = session.createCriteria(PrepaidCardLoadBalance.class);
			criteria.add(Restrictions.eq("rrn", req.getRRNNo()));
			criteria.add(Restrictions.eq("amount", req.getTransAmt()));
			criteria.add(Restrictions.eq("cardinsno", req.getCardAliaceNo().trim()));
			criteria.add(Restrictions.eq("mobileno", req.getRemitterMobile()));
			criteria.add(Restrictions.eq("brcode", d390077.getLbrCode()));
			criteria.add(Restrictions.eq("accountno", d390077.getPrdAcctId()));
			List<PrepaidCardLoadBalance> list = criteria.list();
			if (list == null || list.isEmpty())
				return false;
			else
				return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("ERROR:", e);
			return false;
		} finally {
			session.close();
			session = null;
		}
	}

	public static IMPSTransactionResponse updateRBLRRN(String rrn, String rblRrn) {
		// TODO Auto-generated method stub
		IMPSTransactionResponse response = new IMPSTransactionResponse();
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		try {
			PrepaidCardLoadBalance balance = session.get(PrepaidCardLoadBalance.class, rrn.trim());
			if (balance == null) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.INVALID_RRN_NO);
				return response;
			}
			balance.setRblrefno(rblRrn.trim());
			session.update(balance);
			t.commit();
			response.setResponse(MSGConstants.SUCCESS);
			response.setErrorMessage(MSGConstants.SUCCESFUL_TRN);
			return response;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("ERROR:", e);
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		} finally {
			session.close();
			t = null;
		}
	}

	public static OtherChannelServiceResponse updateBillPaymentStatus(String rrn, String status) {
		// TODO Auto-generated method stub
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		OtherChannelServiceResponse response = new OtherChannelServiceResponse();
		try {
			Criteria criteria = session.createCriteria(Billpayment.class);
			criteria.add(Restrictions.eq("id.rrrnno", rrn));
			if (status.equalsIgnoreCase("P"))
				criteria.add(Restrictions.eq("drcr", "D"));
			else
				criteria.add(Restrictions.eq("drcr", "C"));
			criteria.add(Restrictions.eq("responsecode", "00"));
			List<Billpayment> list = criteria.list();
			if (list == null || list.isEmpty()) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.RECORD_NOT_FOUND);
				return response;
			}
			Billpayment billpayment = list.get(0);
			billpayment.setStatus(status.trim());
			session.update(billpayment);
			t.commit();
			response.setResponse(MSGConstants.SUCCESS);
			response.setErrorMessage(MSGConstants.SUCCESFUL_TRN);
			return response;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("ERROR:", e);
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		} finally {
			session.close();
			session = null;
			t = null;
		}
	}

	public static boolean storeSMSEntries(String bankCode, String cardAliaceNo, String mobileNo, String sms) {
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		try {
			SmsRequest request = new SmsRequest();
			SmsRequestId id = new SmsRequestId();
			id.setBankcode(bankCode);
			id.setCardAliaceNo(cardAliaceNo);
			id.setMobileNo(mobileNo);
			id.setReqDate(new Date());
			id.setReqTime(new Date());

			request.setId(id);
			request.setSms(sms);
			session.save(request);
			t.commit();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("", e);
			return false;
		} finally {
			session.close();
			session = null;
			t = null;
		}
	}

	public static IMPSTransactionResponse validatePrepaidCardLimit(String cardAlias, String mobileNo, double amt) {
		IMPSTransactionResponse response = new IMPSTransactionResponse();
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		try {
			D390077Id id = new D390077Id();
			id.setCardAlias(cardAlias.trim());
			id.setMobileNo(mobileNo.trim());
			D390077 d390077 = session.get(D390077.class, id);
			if (d390077 == null) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
				return response;
			}
			if (amt > d390077.getMaxTrnAmt()) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.EXCEED_DAILY_TRN_LIMIT);
				return response;
			}
			logger.error("Limit Date::>" + d390077.getLimitdate().getTime());
			logger.error("Current Time::>>" + DateUtil.getcurrentDate().getTime());
			if ((d390077.getLimitdate().compareTo(DateUtil.getcurrentDate()) < 0)
					|| (d390077.getLimitdate().compareTo(DateUtil.getcurrentDate()) > 0)) {
				d390077.setLimitdate(new Date());
				session.update(d390077);
				t.commit();
			}
			org.hibernate.Query q = session.createSQLQuery("SELECT * FROM  PREPAID_CARD_LOAD_BALANCE WHERE ENTRYDATE='"
					+ DateUtil.getcurrentDateStringDDMONYYYY()
					+ "' AND RESPCODE='00' AND RRN NOT IN (SELECT RRN FROM  REVERSE_LOAD_BALANCE WHERE ENTRYDATE='"
					+ DateUtil.getcurrentDateStringDDMONYYYY() + "' AND RESPCODE='00')");
			List<Object[]> list = q.list();
			if (list.size() >= d390077.getDailyTrnLimit()) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.EXCEED_DAILY_LIMIT);
				return response;
			}
			Double amount = 0.0;
			for (Object[] balance : list)
				amount += amt + Double.valueOf(balance[5] + "");
			if (amount > d390077.getMaxTrnAmt()) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.EXCEED_DAILY_TRN_LIMIT);
				return response;
			}
			response.setResponse(MSGConstants.SUCCESS);
			response.setErrorMessage(MSGConstants.SUCCESFUL_TRN);
			return response;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("", e);
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		} finally {
			session.close();
			session = null;
			t = null;
		}
	}

	public static AgencyBankingTrn getAgencyBankiTrnObj(String amount, String errorDesc, String name, String fromacc,
			String respCode, String rrn, String toacc, String ownerId, String deviceId) {
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		try {
			AgencyBankingTrn obj = new AgencyBankingTrn();
			obj.setAmount(new BigDecimal(amount));
			obj.setErrorDesc(errorDesc);
			obj.setFromacc(fromacc);
			obj.setName(name);
			obj.setRespCode(respCode);
			obj.setToacc(toacc);
			obj.setTransTime(new Date());
			obj.setDeviceId(deviceId == null ? "" : deviceId);
			obj.setOwnerId(ownerId == null ? "" : ownerId);
			AgencyBankingTrnId id = new AgencyBankingTrnId();
			id.setRrn(rrn);
			id.setTransDate(new Date());
			obj.setId(id);
			session.save(obj);
			t.commit();
			return obj;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("", e);
			return null;
		} finally {
			session.close();
			session = null;
			t = null;
		}
	}

	public static boolean checkAgencyBankingTrn(String fromacc, String toAcc, String amount) {
		Session session = HBUtil.getSessionFactory().openSession();
		try {
			Criteria criteria = session.createCriteria(AgencyBankingTrn.class);
			criteria.add(Restrictions.eq("fromacc", fromacc.trim()));
			criteria.add(Restrictions.eq("toacc", toAcc.trim()));
			criteria.add(Restrictions.eq("id.transDate", DateUtil.getCurrentDate()));
			criteria.add(Restrictions.eq("respCode", "00"));
			List<AgencyBankingTrn> list = criteria.list();
			if (list == null || list.isEmpty())
				return false;
			else
				return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("", e);
			return false;
		} finally {
			session.close();
			session = null;
		}
	}

	public static ImpsTransactionReport mposTrnReport(String agentAccNo, String fromdate, String toDate, String flag) {
		ImpsTransactionReport response = new ImpsTransactionReport();
		Session session = HBUtil.getSessionFactory().openSession();
		try {
			if (flag.equalsIgnoreCase(MSGConstants.SUCCESS)) {
				Criteria criteria = session.createCriteria(AgencyBankingTrn.class);
				criteria.add(Restrictions.eq("fromacc", agentAccNo));
				criteria.add(Restrictions.between("id.transDate", DateUtil.getDateFromStringinDateFormat(fromdate),
						DateUtil.getDateFromStringinDateFormat(toDate)));
				criteria.add(Restrictions.eq("respCode", "00"));
				criteria.addOrder(Order.desc("id.transDate"));
				criteria.addOrder(Order.desc("transTime"));
				List<AgencyBankingTrn> list = criteria.list();
				if (list == null || list.isEmpty()) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.NO_TRANSACTION_FOUND);
					response.setAgencybankingTrn(list);
					return response;
				}
				response.setResponse(MSGConstants.SUCCESS);
				response.setErrorMsg(MSGConstants.SUCCESS_MSG);
				response.setAgencybankingTrn(list);
				return response;
			} else if (flag.equalsIgnoreCase(MSGConstants.FAILURE)) {
				Criteria criteria = session.createCriteria(AgencyBankingTrn.class);
				criteria.add(Restrictions.eq("fromacc", agentAccNo));
				criteria.add(Restrictions.between("id.transDate", DateUtil.getDateFromStringinDateFormat(fromdate),
						DateUtil.getDateFromStringinDateFormat(toDate)));
				criteria.add(Restrictions.ne("respCode", "00"));
				criteria.addOrder(Order.desc("id.transDate"));
				criteria.addOrder(Order.desc("transTime"));
				List<AgencyBankingTrn> list = criteria.list();
				if (list == null || list.isEmpty()) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.NO_TRANSACTION_FOUND);
					response.setAgencybankingTrn(list);
					return response;
				}
				response.setResponse(MSGConstants.SUCCESS);
				response.setErrorMsg(MSGConstants.SUCCESS_MSG);
				response.setAgencybankingTrn(list);
				return response;
			} else {
				Criteria criteria = session.createCriteria(AgencyBankingTrn.class);
				criteria.add(Restrictions.eq("fromacc", agentAccNo));
				criteria.add(Restrictions.between("id.transDate", DateUtil.getDateFromStringinDateFormat(fromdate),
						DateUtil.getDateFromStringinDateFormat(toDate)));
				criteria.addOrder(Order.desc("id.transDate"));
				criteria.addOrder(Order.desc("transTime"));
				List<AgencyBankingTrn> list = criteria.list();
				if (list == null || list.isEmpty()) {
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.NO_TRANSACTION_FOUND);
					response.setAgencybankingTrn(list);
					return response;
				}
				response.setResponse(MSGConstants.SUCCESS);
				response.setErrorMsg(MSGConstants.SUCCESS_MSG);
				response.setAgencybankingTrn(list);
				return response;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("", e);
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		} finally {
			session.close();
			session = null;
		}
	}

	public static List<Object[]> getTRNSummary(String fromDate, String toDate, String respCode, String agentAccNo) {
		Session session = HBUtil.getSessionFactory().openSession();
		try {
			Query q = session.createSQLQuery(
					"select sum(AMOUNT)AS TOTAL_AMOUNT,count (1) AS TOTAL_TRN_NO, RESP_CODE,TRANS_DATE FROM AGENCY_BANKING_TRN  WHERE TRANS_DATE between  '"
							+ fromDate + "' AND '" + toDate + "' AND RESP_CODE='" + respCode + "' AND FROMACC='"
							+ agentAccNo.trim() + "' GROUP BY TRANS_DATE,RESP_CODE");
			List<Object[]> list = q.list();
			if (list == null || list.isEmpty())
				return null;
			return list;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("", e);
			return null;
		} finally {
			session.close();
			session = null;
		}
	}
	/*
	 * public Voucher saveLoanVoucherForINSTLPAY4ForLoanWS( D030003 loanBalances,
	 * D030002 loanParameters, Voucher voucher, boolean isNewSet) {
	 * 
	 * long l_start = System.currentTimeMillis(); Voucher loanVoucherForChrg = new
	 * Voucher(); Voucher loanVoucherCr = new Voucher(); Voucher
	 * loanVoucherForPenalchrg = new Voucher(); Voucher customerVoucher = new
	 * Voucher(); Double otherCharges = 0.0; Double chrgAmount = 0.0; Double
	 * penaltyCharges = 0.0; Double penalChrg = 0.0; Double amount = 0D; String
	 * narration = " "; Long setNo = 0L; Long mainScrollNo = 0L; String irPrdAcctId
	 * = loanParameters.getIrPrdAcctId();
	 * 
	 * ValidationResult validationResult = new ValidationResult(); //
	 * voucher.setValidationResult(validationResult); //
	 * customerVoucher.setValidationResult(validationResult); // amount =
	 * voucher.getF(); try { // transactionStatus =
	 * transactionManager.getTransaction(SwiftCoreUtil // .getTxnDefinition()); if
	 * (loanBalances.getOthChgPrvdFcy() > loanBalances .getOthChgPaidFcy()) {
	 * otherCharges = loanBalances.getOthChgPrvdFcy() -
	 * loanBalances.getOthChgPaidFcy(); if (amount > otherCharges) { chrgAmount =
	 * otherCharges; amount = amount - chrgAmount; } else { chrgAmount = amount;
	 * amount = amount - chrgAmount; }
	 * 
	 * if (chrgAmount > 0) { // loanVoucherForChrg =
	 * voucherProcess.setVoucherParamters( // voucher.getLbrcode(),
	 * voucher.getBatchcd(), voucher // .getProductNo(), voucher.getAccountNo()); //
	 * voucherProcess // .setVoucherData(loanVoucherForChrg, voucher //
	 * .getProductNo(), voucher.getAccountNo(), // chrgAmount); //
	 * voucherProcess.setDrCr(loanVoucherForChrg, // SwiftCoreConstants.CREDIT); //
	 * // loanVoucherForChrg // .setActivitytype(SwiftCoreConstants.INSTLPAY); //
	 * loanVoucherForChrg // .setCashflowtype(SwiftCoreConstants.LNOCHCR);//
	 * "LNOCHCR"); // loanVoucherForChrg.setFeffdate(getFormattedDateLocal(new
	 * Date())); // narration = loanBalances.getNarration().trim()+" Other //
	 * Charges"; // narration = voucher.getParticulars(); //narration =
	 * "Other Charges"; // by prashant if (narration.length() >= 70) { narration =
	 * narration.substring(0, 70); } loanVoucherForChrg.setPostflag("P");
	 * loanVoucherForChrg.setPostdate(loanVoucherForChrg.getDailyBatchesDirectory().
	 * getPostdate()); loanVoucherForChrg.setParticulars(narration);
	 * loanVoucherForChrg.setInstrtype(voucher.getInstrtype());
	 * //loanVoucherForChrg.setInstrno(SwiftCoreUtil //
	 * .prependZeroInsNo(loanBalances.getInstrno()));
	 * loanVoucherForChrg.setInstrno(voucher.getInstrno());
	 * loanVoucherForChrg.setMaker(999998L);
	 * loanVoucherForChrg.setCheckerdate(getFormattedDateLocal(new Date()));
	 * loanVoucherForChrg.setCheckertime(DateUtility.getMakerTime(loanVoucherForChrg
	 * .getCheckerdate())); SimpleDateFormat dateFormat1 = new
	 * SimpleDateFormat("dd/MMM/yyyy"); Date makerDate =
	 * dateFormat1.parse(dateFormat1.format(new Date()));
	 * loanVoucherForChrg.setMakerdate(makerDate);
	 * loanVoucherForChrg.setValuedate(getFormattedDateLocal(new Date()));//
	 * loanBalances.getValuedate());
	 * loanVoucherForChrg.setInstrdate(getFormattedDateLocal(new Date()));
	 * loanVoucherForChrg.setAccount(voucherProcess .getAccountDao().getAccountInfo(
	 * voucher.getProductNo(), voucher.getAccountNo(), voucher.getLbrcode()));
	 * 
	 * loanVoucherForChrg = batchProcess.updateAccountBalance( loanVoucherForChrg,
	 * 1); loanVoucherForChrg = batchProcess.updateAccountBalance(
	 * loanVoucherForChrg, 2); loanVoucherForChrg =
	 * voucherProcess.setLastAssignNumber( loanVoucherForChrg, isNewSet); if
	 * (isNewSet == true) { setNo = loanVoucherForChrg.getSetno(); mainScrollNo =
	 * loanVoucherForChrg.getMainscrollno(); voucher.setSetno(setNo); isNewSet =
	 * false; } else { loanVoucherForChrg.setSetno(voucher.getSetno());
	 * //loanVoucherForChrg.setInstrno(voucher.getInstrno());
	 * loanVoucherForChrg.setMainscrollno(voucher.getMainscrollno()); }
	 * 
	 * loanVoucherForChrg.setSystemFlag(voucher.getSystemFlag());
	 * loanVoucherForChrg.setChecker1(999998L); mainScrollNo =
	 * loanVoucherForChrg.getMainscrollno(); try {
	 * voucherDao.saveVoucher(loanVoucherForChrg); } catch (Exception e) {
	 * transactionManager.rollback(transactionStatus);
	 * voucher.getValidationResult().setErrorMessage(
	 * "Error Generating Loan Chrg CR Voucher.");
	 * voucher.getValidationResult().setErrorCode("LN01");
	 * voucher.getValidationResult().setValid(false);
	 * logger.error("Error Generating Loan Chrg CR Voucher.", e); return voucher; }
	 * try { accountDao.updateBalances(loanVoucherForChrg .getAccount()); } catch
	 * (Exception e) { transactionManager.rollback(transactionStatus); voucher
	 * .getValidationResult() .setErrorMessage(
	 * "Error Updating A/C Bal For Loan Chrg CR Voucher.");
	 * voucher.getValidationResult().setErrorCode("LN02");
	 * voucher.getValidationResult().setValid(false); logger .error(
	 * "Error Updating A/C Bal. For Loan Chrg CR Voucher.", e); return voucher; }
	 * try { systemparamDao.updateDailyBatchesDirecotry(
	 * loanVoucherForChrg.getDailyBatchesDirectory(),
	 * loanVoucherForChrg.getFcytrnamt(), loanVoucherForChrg.getDrcr(), 1); } catch
	 * (Exception e) { transactionManager.rollback(transactionStatus); voucher
	 * .getValidationResult() .setErrorMessage(
	 * "Error Updating Batch Bal For Loan Chrg CR Voucher.");
	 * voucher.getValidationResult().setErrorCode("LN03");
	 * voucher.getValidationResult().setValid(false); logger .error(
	 * "Error Updating Batch Bal For Loan Chrg CR Voucher.", e); return voucher; }
	 * loanBalances.setOthchgpaidfcy(loanBalances .getOthchgpaidfcy() + chrgAmount);
	 * loanBalances.setOthchglcy(loanBalances.getOthchglcy() - chrgAmount); } } //
	 * Principal amount Double mainbalfcy = loanBalances.getMainBalFcy(); if
	 * (mainbalfcy < 0) { mainbalfcy = mainbalfcy * (-1); if (amount > 0 &&
	 * mainbalfcy != 0.0) { Double prinVchrAmount = 0.0; if (mainbalfcy - amount >
	 * 0) { prinVchrAmount = amount; amount = 0.0; } else if (mainbalfcy - amount <
	 * 0) { prinVchrAmount = mainbalfcy; amount = amount - mainbalfcy; } if
	 * (prinVchrAmount > 0) { loanVoucherCr = voucherProcess.setVoucherParamters(
	 * voucher.getLbrcode(), voucher.getBatchcd(), voucher.getProductNo(),
	 * voucher.getAccountNo()); voucherProcess.setVoucherData(loanVoucherCr, voucher
	 * .getProductNo(), voucher.getAccountNo(), prinVchrAmount);
	 * voucherProcess.setDrCr(loanVoucherCr, SwiftCoreConstants.CREDIT);
	 * 
	 * loanVoucherCr .setActivitytype(SwiftCoreConstants.INSTLPAY);
	 * loanVoucherCr.setCashflowtype(SwiftCoreConstants.LNPCR);
	 * loanVoucherCr.setFeffdate(getFormattedDateLocal(new Date())); // String
	 * naration = loanBalances.getNarration()+ " Main // Balance"; narration =
	 * voucher.getParticulars(); //narration = "Main Balance"; // by prashant if
	 * (narration.length() >= 70) { narration = narration.substring(0, 70); }
	 * loanVoucherCr.setPostflag(SwiftCoreConstants.POSTING);
	 * loanVoucherCr.setPostdate(loanVoucherCr.getDailyBatchesDirectory().
	 * getPostdate()); loanVoucherCr.setParticulars(narration);
	 * loanVoucherCr.setInstrtype(voucher.getInstrtype());
	 * //loanVoucherCr.setInstrno(SwiftCoreUtil //
	 * .prependZeroInsNo(loanBalances.getInstrno()));
	 * loanVoucherCr.setInstrno(voucher.getInstrno());
	 * loanVoucherCr.setMaker(999998L);
	 * loanVoucherCr.setCheckerdate(getFormattedDateLocal(new Date()));
	 * loanVoucherCr.setCheckertime(DateUtility.getMakerTime(loanVoucherCr.
	 * getCheckerdate())); SimpleDateFormat dateFormat1 = new
	 * SimpleDateFormat("dd/MMM/yyyy"); Date makerDate =
	 * dateFormat1.parse(dateFormat1.format(new Date()));
	 * loanVoucherCr.setMakerdate(makerDate); //
	 * loanVoucherCr.setValuedate(loanBalances.getValuedate());
	 * loanVoucherCr.setValuedate(getFormattedDateLocal(new Date()));
	 * loanVoucherCr.setInstrdate(getFormattedDateLocal(new Date()));
	 * loanVoucherCr.setAccount(voucherProcess.getAccountDao()
	 * .getAccountInfo(voucher.getProductNo(), voucher.getAccountNo(),
	 * voucher.getLbrcode())); loanVoucherCr = batchProcess.updateAccountBalance(
	 * loanVoucherCr, 1); loanVoucherCr = batchProcess.updateAccountBalance(
	 * loanVoucherCr, 2); loanVoucherCr = voucherProcess.setLastAssignNumber(
	 * loanVoucherCr, isNewSet); if (isNewSet == true) { setNo =
	 * loanVoucherCr.getSetno(); mainScrollNo = loanVoucherCr.getMainscrollno();
	 * voucher.setSetno(setNo); isNewSet = false; } else {
	 * //loanVoucherCr.setSetno(setNo); loanVoucherCr.setSetno(voucher.getSetno());
	 * //loanVoucherCr.setInstrno(voucher.getInstrno());
	 * loanVoucherCr.setMainscrollno(voucher.getMainscrollno()); } if (mainScrollNo
	 * != 0) loanVoucherCr.setMainscrollno(mainScrollNo); else mainScrollNo =
	 * loanVoucherCr.getMainscrollno();
	 * 
	 * loanVoucherCr.setSystemFlag(voucher.getSystemFlag());
	 * loanVoucherCr.setChecker1(999998L); try {
	 * voucherDao.saveVoucher(loanVoucherCr); } catch (Exception e) {
	 * transactionManager.rollback(transactionStatus);
	 * voucher.getValidationResult().setErrorMessage(
	 * "Error Generating Loan Princ CR Voucher.");
	 * voucher.getValidationResult().setErrorCode("LN04");
	 * voucher.getValidationResult().setValid(false); logger
	 * .error("Error Generating Loan CR Voucher.", e); return voucher; } try {
	 * accountDao.updateBalances(loanVoucherCr .getAccount()); } catch (Exception e)
	 * { transactionManager.rollback(transactionStatus); voucher
	 * .getValidationResult() .setErrorMessage(
	 * "Error Updating A/C Bal For Loan Princ CR Voucher");
	 * voucher.getValidationResult().setErrorCode("LN05");
	 * voucher.getValidationResult().setValid(false); logger .error(
	 * "Error Updating A/C Bal For Loan Princ CR Voucher", e); return voucher; } try
	 * { systemparamDao.updateDailyBatchesDirecotry(
	 * loanVoucherCr.getDailyBatchesDirectory(), loanVoucherCr.getFcytrnamt(),
	 * loanVoucherCr .getDrcr(), 1); } catch (Exception e) {
	 * transactionManager.rollback(transactionStatus); voucher
	 * .getValidationResult() .setErrorMessage(
	 * "Error Updating Batch Bal For Loan Princ CR Voucher.");
	 * voucher.getValidationResult().setErrorCode("LN06");
	 * voucher.getValidationResult().setValid(false); logger .error(
	 * "Error Updating Batch Bal For Loan Princ CR Voucher.", e); return voucher; }
	 * } loanBalances.setMainbalfcy(loanBalances.getMainbalfcy() + prinVchrAmount);
	 * } } if (amount > 0) { // for penal charges if (loanBalances.getPenalprvdfcy()
	 * > loanBalances .getPenalpaidfcy()) { penaltyCharges =
	 * loanBalances.getPenalprvdfcy() - loanBalances.getPenalpaidfcy(); if (amount >
	 * penaltyCharges) { penalChrg = penaltyCharges; amount = amount - penalChrg; }
	 * else { penalChrg = amount; amount = amount - penalChrg; }
	 * 
	 * if (penalChrg > 0) { loanVoucherForPenalchrg = voucherProcess
	 * .setVoucherParamters(voucher.getLbrcode(), voucher.getBatchcd(),
	 * SwiftCoreUtil .getProductNumber(irPrdAcctId) .trim(), SwiftCoreUtil
	 * .getAccountNumber(irPrdAcctId) .trim());
	 * voucherProcess.setVoucherData(loanVoucherForPenalchrg,
	 * SwiftCoreUtil.getProductNumber(irPrdAcctId) .trim(), SwiftCoreUtil
	 * .getAccountNumber(irPrdAcctId).trim(), penalChrg);
	 * voucherProcess.setDrCr(loanVoucherForPenalchrg, SwiftCoreConstants.CREDIT);
	 * loanVoucherForPenalchrg .setActivitytype(SwiftCoreConstants.INSTLPAY);
	 * loanVoucherForPenalchrg .setCashflowtype(SwiftCoreConstants.LNPINTCR);
	 * loanVoucherForPenalchrg.setMainacctid(SwiftCoreUtil
	 * .getAccountNumber(voucher.getProductNo(), voucher.getAccountNo(), ""));
	 * loanVoucherForPenalchrg .setMainmodtype(SwiftCoreConstants.LOAN);
	 * loanVoucherForPenalchrg .setFeffdate(getFormattedDateLocal(new Date()));
	 * narration = voucher.getParticulars(); //narration = "Interest Receivable"; //
	 * by prashant if (narration.length() >= 70) { narration =
	 * narration.substring(0, 70); }
	 * loanVoucherForPenalchrg.setPostflag(SwiftCoreConstants.POSTING);
	 * loanVoucherForPenalchrg.setPostdate(loanVoucherForPenalchrg.
	 * getDailyBatchesDirectory().getPostdate());
	 * loanVoucherForPenalchrg.setParticulars(narration);
	 * loanVoucherForPenalchrg.setInstrtype(voucher .getInstrtype());
	 * //loanVoucherForPenalchrg.setInstrno(SwiftCoreUtil //
	 * .prependZeroInsNo(loanBalances.getInstrno()));
	 * loanVoucherForPenalchrg.setInstrno(voucher.getInstrno());
	 * loanVoucherForPenalchrg.setMaker(999998L);
	 * loanVoucherForPenalchrg.setCheckerdate(getFormattedDateLocal(new Date()));
	 * loanVoucherForPenalchrg.setCheckertime(DateUtility.getMakerTime(
	 * loanVoucherForPenalchrg.getCheckerdate())); SimpleDateFormat dateFormat1 =
	 * new SimpleDateFormat("dd/MMM/yyyy"); Date makerDate =
	 * dateFormat1.parse(dateFormat1.format(new Date()));
	 * loanVoucherForPenalchrg.setMakerdate(makerDate);
	 * loanVoucherForPenalchrg.setValuedate(getFormattedDateLocal(new Date()));
	 * loanVoucherForPenalchrg.setInstrdate(getFormattedDateLocal(new Date()));
	 * loanVoucherForPenalchrg.setAccount(voucherProcess
	 * .getAccountDao().getAccountInfo( voucher.getProductNo(),
	 * voucher.getAccountNo(), voucher.getLbrcode()));
	 * 
	 * loanVoucherForPenalchrg = batchProcess
	 * .updateAccountBalance(loanVoucherForPenalchrg, 1); loanVoucherForPenalchrg =
	 * batchProcess.updateAccountBalance( loanVoucherForPenalchrg, 2);
	 * loanVoucherForPenalchrg = voucherProcess
	 * .setLastAssignNumber(loanVoucherForPenalchrg, isNewSet); if (isNewSet ==
	 * true) { setNo = loanVoucherForPenalchrg.getSetno(); mainScrollNo =
	 * loanVoucherForPenalchrg .getMainscrollno(); voucher.setSetno(setNo); isNewSet
	 * = false; } else { //loanVoucherForPenalchrg.setSetno(setNo);
	 * loanVoucherForPenalchrg.setSetno(voucher.getSetno());
	 * //loanVoucherForPenalchrg.setInstrno(voucher.getInstrno());
	 * loanVoucherForPenalchrg.setMainscrollno(voucher.getMainscrollno()); } if
	 * (mainScrollNo != 0) loanVoucherForPenalchrg .setMainscrollno(mainScrollNo);
	 * else mainScrollNo = loanVoucherForPenalchrg .getMainscrollno();
	 * 
	 * loanVoucherForPenalchrg.setSystemFlag(voucher.getSystemFlag());
	 * loanVoucherForPenalchrg.setChecker1(999998L); try {
	 * voucherDao.saveVoucher(loanVoucherForPenalchrg); } catch (Exception e) {
	 * transactionManager.rollback(transactionStatus); voucher
	 * .getValidationResult() .setErrorMessage(
	 * "Error Generating Loan Penal Chrg CR Voucher.");
	 * voucher.getValidationResult().setErrorCode("LN07");
	 * voucher.getValidationResult().setValid(false); logger .error(
	 * "Error Generating Loan Penal Chrg CR Voucher.", e); return voucher; } try {
	 * accountDao.updateBalances(loanVoucherForPenalchrg .getAccount()); } catch
	 * (Exception e) { transactionManager.rollback(transactionStatus); voucher
	 * .getValidationResult() .setErrorMessage(
	 * "Error Updating A/C Bal For Loan Penal CR Voucher");
	 * voucher.getValidationResult().setErrorCode("LN08");
	 * voucher.getValidationResult().setValid(false); logger .error(
	 * "Error Updating A/C Bal. For Loan Penal CR Voucher", e); return voucher; }
	 * try { systemparamDao.updateDailyBatchesDirecotry( loanVoucherForPenalchrg
	 * .getDailyBatchesDirectory(), loanVoucherForPenalchrg.getFcytrnamt(),
	 * loanVoucherForPenalchrg.getDrcr(), 1); } catch (Exception e) {
	 * transactionManager.rollback(transactionStatus); voucher
	 * .getValidationResult() .setErrorMessage(
	 * "Error Updating Batch Bal For Loan Penal CR Voucher.");
	 * voucher.getValidationResult().setErrorCode("LN09");
	 * voucher.getValidationResult().setValid(false); logger .error(
	 * "Error Updating Batch Bal For Loan Penal CR Voucher.", e); return voucher; }
	 * loanBalances.setPenalpaidfcy(loanBalances .getPenalpaidfcy() + penalChrg); }
	 * } }
	 * 
	 * // Principal amount
	 * 
	 * if (amount > 0) {
	 * 
	 * loanVoucherCr = voucherProcess.setVoucherParamters(voucher .getLbrcode(),
	 * voucher.getBatchcd(), voucher .getProductNo(), voucher.getAccountNo());
	 * voucherProcess.setVoucherData(loanVoucherCr, voucher .getProductNo(),
	 * voucher.getAccountNo(), amount); voucherProcess .setDrCr(loanVoucherCr,
	 * SwiftCoreConstants.CREDIT);
	 * 
	 * loanVoucherCr.setActivitytype(SwiftCoreConstants.INSTLPAY);
	 * loanVoucherCr.setCashflowtype(SwiftCoreConstants.LNPCR);
	 * loanVoucherCr.setFeffdate(getFormattedDateLocal(new Date())); // String
	 * naration = loanBalances.getNarration()+ " Main // Balance"; narration =
	 * voucher.getParticulars(); //narration = "Main Balance"; // by prashant if
	 * (narration.length() >= 70) { narration = narration.substring(0, 70); }
	 * loanVoucherCr.setPostflag(SwiftCoreConstants.POSTING);
	 * loanVoucherCr.setPostdate(loanVoucherCr.getDailyBatchesDirectory().
	 * getPostdate()); loanVoucherCr.setParticulars(narration);
	 * loanVoucherCr.setInstrtype(voucher.getInstrtype());
	 * //loanVoucherCr.setInstrno(SwiftCoreUtil //
	 * .prependZeroInsNo(loanBalances.getInstrno()));
	 * loanVoucherCr.setInstrno(voucher.getInstrno());
	 * loanVoucherCr.setMaker(999998L);
	 * loanVoucherCr.setCheckerdate(getFormattedDateLocal(new Date()));
	 * loanVoucherCr.setCheckertime(DateUtility.getMakerTime(loanVoucherCr.
	 * getCheckerdate())); SimpleDateFormat dateFormat1 = new
	 * SimpleDateFormat("dd/MMM/yyyy"); Date makerDate =
	 * dateFormat1.parse(dateFormat1.format(new Date()));
	 * loanVoucherCr.setMakerdate(makerDate);
	 * loanVoucherCr.setValuedate(getFormattedDateLocal(new Date()));
	 * loanVoucherCr.setInstrdate(getFormattedDateLocal(new Date()));
	 * loanVoucherCr.setAccount(voucherProcess.getAccountDao()
	 * .getAccountInfo(voucher.getProductNo(), voucher.getAccountNo(),
	 * voucher.getLbrcode())); loanVoucherCr = batchProcess.updateAccountBalance(
	 * loanVoucherCr, 1); loanVoucherCr = batchProcess.updateAccountBalance(
	 * loanVoucherCr, 2); loanVoucherCr = voucherProcess.setLastAssignNumber(
	 * loanVoucherCr, isNewSet); if (isNewSet == true) { setNo =
	 * loanVoucherCr.getSetno(); mainScrollNo = loanVoucherCr.getMainscrollno();
	 * voucher.setSetno(setNo); isNewSet = false; } else {
	 * //loanVoucherCr.setSetno(setNo); loanVoucherCr.setSetno(voucher.getSetno());
	 * //loanVoucherCr.setInstrno(voucher.getInstrno());
	 * loanVoucherCr.setMainscrollno(voucher.getMainscrollno()); } if (mainScrollNo
	 * != 0) loanVoucherCr.setMainscrollno(mainScrollNo); else mainScrollNo =
	 * loanVoucherCr.getMainscrollno();
	 * 
	 * loanVoucherCr.setSystemFlag(voucher.getSystemFlag());
	 * loanVoucherCr.setChecker1(999998L); try {
	 * voucherDao.saveVoucher(loanVoucherCr); } catch (Exception e) {
	 * transactionManager.rollback(transactionStatus);
	 * voucher.getValidationResult().setErrorMessage(
	 * "Error Generating Loan Princ CR Voucher.");
	 * voucher.getValidationResult().setErrorCode("LN10");
	 * voucher.getValidationResult().setValid(false);
	 * logger.error("Error Generating Loan Princ CR Voucher.", e); return voucher; }
	 * try { accountDao.updateBalances(loanVoucherCr.getAccount()); } catch
	 * (Exception e) { transactionManager.rollback(transactionStatus);
	 * voucher.getValidationResult().setErrorMessage(
	 * "Error Updating A/C Bal For Loan Princ CR Voucher");
	 * voucher.getValidationResult().setErrorCode("LN11");
	 * voucher.getValidationResult().setValid(false); logger .error(
	 * "Error Updating A/C Bal. For Loan Princ CR Voucher", e); return voucher; }
	 * try { systemparamDao.updateDailyBatchesDirecotry(loanVoucherCr
	 * .getDailyBatchesDirectory(), loanVoucherCr .getFcytrnamt(),
	 * loanVoucherCr.getDrcr(), 1); } catch (Exception e) {
	 * transactionManager.rollback(transactionStatus); voucher
	 * .getValidationResult() .setErrorMessage(
	 * "Error Updating Batch Bal For Loan Princ CR Voucher.");
	 * voucher.getValidationResult().setErrorCode("LN12");
	 * voucher.getValidationResult().setValid(false); logger .error(
	 * "Error Updating Batch Bal For Loan Princ CR Voucher.", e); return voucher; }
	 * loanBalances.setMainbalfcy(loanBalances.getMainbalfcy() + amount); } //
	 * Saving Loan Balances
	 * 
	 * loanBalances.setMainballcy(loanBalances.getMainbalfcy()
	 * loanBalances.getConvrate());
	 * loanBalances.setDisbursedamtlcy(loanBalances.getDisbursedamtfcy()
	 * loanBalances.getConvrate());
	 * loanBalances.setDbtrupdtchkid(loanBalances.getDbtrupdtchkid() + 1); try {
	 * loanDao.updateLoanBalances(loanBalances); } catch (Exception e) {
	 * transactionManager.rollback(transactionStatus);
	 * voucher.getValidationResult().setErrorMessage(
	 * "Error Updating Loan Balances CR Voucher.");
	 * voucher.getValidationResult().setErrorCode("LN13");
	 * voucher.getValidationResult().setValid(false);
	 * logger.error("Error Updating Loan Balances CR Voucher.", e); return voucher;
	 * } if (chrgAmount > 0) { customerVoucher = loanVoucherForChrg; } else if
	 * (mainbalfcy != 0) { customerVoucher = loanVoucherCr; } else if (penalChrg >
	 * 0) { customerVoucher = loanVoucherForPenalchrg; } else { customerVoucher =
	 * loanVoucherCr; } customerVoucher.getValidationResult().setValid(true);
	 * transactionManager.commit(transactionStatus);
	 * 
	 * } catch (Exception ex) { transactionManager.rollback(transactionStatus);
	 * voucher.getValidationResult().setErrorMessage(
	 * "Error Setting Voucher Params For Loan Type04 CR Voucher.");
	 * voucher.getValidationResult().setErrorCode("LN14");
	 * voucher.getValidationResult().setValid(false); logger .error(
	 * "Error While Setting Voucher Params For Loan Type04 CR Voucher.", ex); return
	 * voucher; } long l_end = System.currentTimeMillis(); logger
	 * .debug("Instrumentation :<VoucherServiceImpl.java>:<saveLoanVoucherForINSTLPAY4ForNEFT>: "
	 * + (l_end - l_start)); return customerVoucher;
	 * 
	 * }
	 */

	public static RtgsCutoffParameter getRtgsCutoffParameter(Session session)	throws SQLException {
		String queryString = "from RtgsCutoffParameter"; 
		Query queryObject = session.createQuery(queryString);				
		List<RtgsCutoffParameter> rtgsCutoffList = queryObject.list();
		if(!rtgsCutoffList.isEmpty()){ 
			return rtgsCutoffList.get(0); 
		}	
		return null;
	}
	
	public static Long getNextUtrSeqNo(Date msgDate,Session session) {
		String queryString="select max(utrseqNo) from D946020 where id.msgDate =:msgDate";
		int seqNo = 0;
		try {
		Query queryObject = session.createQuery(queryString).setParameter("msgDate", msgDate);			
		seqNo = (int)queryObject.getSingleResult();
		
		
		}catch(NullPointerException ne) {
			
				seqNo=1; 
			
		}catch(Exception ex) {
			
				seqNo=1; 
			
		}
		return (long) seqNo;
		
	}
	
	
	public static D020004 getMatValue(D020004 termDepositReceipts, String accountNo, D009022 account,
			Date operationDate, Long lbrCode, String productNo, Long noOfMonths, Long noOfDays,
			String crAccountType, Double transAmnt, D020002 tdParameter) throws Exception {
		Object[] custTDSFile =getCustTDFileForReciept(termDepositReceipts.getId().getLbrCode(), termDepositReceipts.getId().getPrdAcctId().substring(0, 24).trim());
		D009500 brwiseCustTDSFile = null;
		//String accountNo8 = SwiftCoreUtil.getAccountNumber(accountNo);
		//Account account = accountService.getAccountDetails(drLbrCode, fdOpenRequest.getCrAccountType(), accountNo8);
		Customer cust = null;
		if (custTDSFile == null) {
			brwiseCustTDSFile = new D009500();
			cust = getCustomer(account.getCustNo()+"");
		} else {
			brwiseCustTDSFile = (D009500) custTDSFile[1];
			cust = getCustomer(account.getCustNo()+"");
		}
		
		if (cust != null) {
			String date = DateUtility.getDateString(operationDate);
			String date1[] = date.split("/");
			Calendar calendar = Calendar.getInstance();
			Calendar fincal = Calendar.getInstance();
			calendar.setTime(operationDate);
			fincal.setTime(DateUtility.getDateFromStringinDateFormat("31/MAR/"+date1[2]));
			if ((calendar.getTime().after(fincal.getTime()))) {
				long year = Long.parseLong(date1[2]) + 1;
				termDepositReceipts.setTdsDate(DateUtility.getDateFromStringinDateFormat("31/MAR/"+year));
			} else {
				termDepositReceipts.setTdsDate(DateUtility.getDateFromStringinDateFormat("31/MAR/"+date1[2]));
			}
			
			termDepositReceipts.setIntprojected(brwiseCustTDSFile.getIntProjected());
			
			termDepositReceipts.setTdsYn(cust.getTdsYn());
		}
		
		return termDepositReceipts = calculateMaturityValue(operationDate, lbrCode, 
				account.getCurCd(), productNo, accountNo, account.getAcctType(), noOfMonths,
				noOfDays, crAccountType, transAmnt, termDepositReceipts, tdParameter, cust);
	}
	
	public static Object[] getCustTDFileForReciept(int lbrcode, String prdacctid) throws SQLException {
		//String queryString = "FROM D009022 a,D009500 b, D009021 c" + " WHERE a.id.lbrCode =:lbrcode AND b.id.lbrCode = :lbrcode" + " AND a.custNo = c.custInt AND b.id.mainCustNo = c.custInt AND a.id.prdAcctId like :prdacctid";
		
		String queryString = "FROM D009022 a,D009500 b, D009021 c" + " WHERE a.id.lbrCode =:lbrcode AND b.id.lbrCode = :lbrcode AND c.id.lbrCode =:lbrcode" + " AND a.custNo = b.id.mainCustNo AND c.id.prdCd =:prdCd AND a.id.prdAcctId like :prdacctid";
		
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		
		List<Object[]> list = session.createQuery(queryString).setParameter("lbrcode", lbrcode)
				.setParameter("prdCd", prdacctid.substring(0, 8).trim())
				.setParameter("prdacctid", prdacctid + "%").getResultList();
				
				
			if(list.size()>0)
				{Object [] objects=list.get(0);
		return objects;
				}else {
					return null;
				}
	}
	
	
	public static D020004 calculateMaturityValue(Date drOperationDate, Long drLbrCode, String curCd, 
			String drProductNo, String drAccountNo, byte accountType, Long noOfMonths, Long noOfDays,
			String crAccountType, Double transAmnt, D020004 termDepositReceipts, 
			D020002 parameter, Customer customer) throws Exception {
		Date matDate = getMaturityDate(drOperationDate, noOfMonths, noOfDays);
		Long acctType =(long) accountType;
		String intOffsetRateForTDValues = TermDepositeServiceImpl.getIntOffsetRateForTD(matDate, crAccountType, 
				noOfMonths, noOfDays, drLbrCode, drOperationDate, curCd, drProductNo, acctType);
		if (!intOffsetRateForTDValues.contains("~")) {
			termDepositReceipts.setRemarks("Error in calculate interest Offset Rate For TD.");
			return termDepositReceipts;
		}
		
		Double intRate = Double.valueOf(intOffsetRateForTDValues.split("~")[0]);
		Double offSetRate = Double.valueOf(intOffsetRateForTDValues.split("~")[1]);
		Double clIntRate = Double.valueOf(intOffsetRateForTDValues.split("~")[2]);
		Double totalRate = clIntRate;
		termDepositReceipts.setInstOrPrincAmt(transAmnt);
		termDepositReceipts.setIntRate(intRate);
		termDepositReceipts.setClIntRate(clIntRate);
		termDepositReceipts.setOffSetRate(offSetRate);
		termDepositReceipts.setTotalRate(totalRate);
		termDepositReceipts.setNoOfMonths(noOfMonths.shortValue());
		termDepositReceipts.setNoOfDays(noOfDays.shortValue());
		termDepositReceipts.setAsOffdate(drOperationDate); 
		//termDepositReceipts.getUser().setOperationDate(drOperationDate);
		termDepositReceipts.setMatDate(matDate);
		termDepositReceipts.setCertDate(drOperationDate);
		//termDepositReceipts.getId().setLbrCode(drLbrCode.intValue());
		//termDepositReceipts.getId().setPrdAcctId(drAccountNo);
		termDepositReceipts.setMainBalFcy(0d);
		termDepositReceipts.setIntrateCalc(0d);
		termDepositReceipts.setCurCd(curCd);
		termDepositReceipts.setClosedDate(null);
		termDepositReceipts.setReceiptStatus((byte) 0);
		termDepositReceipts.setIntPrvdAmtFcy(0d);		
		Calendar matDateCal = Calendar.getInstance();
		matDateCal.setTime(matDate);
		matDateCal.add(Calendar.DAY_OF_MONTH, -1);
		Calendar originalMatDateCal = Calendar.getInstance();
		originalMatDateCal.setTime(matDate);
		Calendar asOfDateCal = Calendar.getInstance();
		asOfDateCal.setTime(drOperationDate);
		String intfreq =TermDepositeServiceImpl.getInterestFreq(drLbrCode, crAccountType);
		Long noOfDaysDiff = DateUtility.getDateDiff(asOfDateCal, matDateCal);
		String cumintyn = parameter.getCumIntYn()+"";
		String instorprinc = parameter.getInstOrPrinc()+"";
		TermDepositeServiceImpl.calculateMaturityValueForTDProducts(termDepositReceipts, noOfDaysDiff, parameter, cumintyn, instorprinc, intfreq, asOfDateCal, originalMatDateCal, matDateCal);
		D009500 currentProjection = new D009500();
		CentrelisedBrwiseCustTDSFile centrelisedBrwiseCustTDSFile = new CentrelisedBrwiseCustTDSFile();
		if (parameter.getTaxProjection()=='Y') {
			try {
				centrelisedBrwiseCustTDSFile = TermDepositeServiceImpl.getCentrelisedBrwiseCustTDSFileForCust(customer, true);
				currentProjection = TermDepositeServiceImpl.runProjectionForSingleReceipt(termDepositReceipts, parameter, customer);
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			if (null != customer && customer.getTdsYn()=='Y'
					&& ((currentProjection.getIntProjected() + centrelisedBrwiseCustTDSFile.getIntProjected()) >= parameter
							.getTdsAmount())) {
				termDepositReceipts.setTdsprojected(currentProjection.getIntProjected() + centrelisedBrwiseCustTDSFile.getIntProjected());
									
				termDepositReceipts.setTdsRate(customer.getTdsPercentage());
				termDepositReceipts.setTdsDefAmt(parameter.getTdsAmount());
				termDepositReceipts.setTdsDate(DateUtility.getFormattedDate(currentProjection.getFincalEndDate()));
				
				currentProjection.setIntProjected(currentProjection.getIntProjected() + centrelisedBrwiseCustTDSFile.getIntProjected());
				currentProjection.setTdsProjected(currentProjection.getTdsProjected() + centrelisedBrwiseCustTDSFile.getTdsProjected());
				currentProjection.setFincalEndDate(DateUtility.getFormattedDate(currentProjection.getFincalEndDate()));
				
			}
			termDepositReceipts.setCentrelisedBrwiseCustTDSFile(centrelisedBrwiseCustTDSFile);
			termDepositReceipts.setCurrentProjection(currentProjection);
		}

		try {
			if (termDepositReceipts.getInstOrPrincAmt()!=0) {
				termDepositReceipts.setRenewalAmtWords(EnglishNumberToWords.convertDouble(termDepositReceipts.getInstOrPrincAmt()));
				if (termDepositReceipts.getRenewalAmtWords() != null && 
						!SwiftCoreConstants.BLANK_STRING.equalsIgnoreCase(termDepositReceipts.getRenewalAmtWords())) {
					termDepositReceipts.setRenewalAmtWords(termDepositReceipts.getRenewalAmtWords()+" Only.");
					logger.error("Reneval Amt Words="+termDepositReceipts.getRenewalAmtWords());
				}
			}
		}catch(Exception e) {
			
		}
		
		return termDepositReceipts;
	}
	
	public static Date getMaturityDate(Date drOperationDate, Long noOfMonths, Long noOfDays) {
		Calendar cal = Calendar.getInstance();		
		cal.setTime(drOperationDate);
		cal.add(Calendar.MONTH, noOfMonths.intValue());
		cal.add(Calendar.DAY_OF_MONTH, (noOfDays.intValue()));
		Date matdate = cal.getTime();
		return matdate;
	}

	public static AcctStatDetailsStatusResponse updateAcctStatDetailsStatus(String rrn, int status, String fileName) {
		// TODO Auto-generated method stub
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		AcctStatDetailsStatusResponse response = new AcctStatDetailsStatusResponse();
		try {
			Criteria criteria = session.createCriteria(D350023.class);
			criteria.add(Restrictions.eq("id.refNo", rrn));
			//criteria.add(Restrictions.eq("fileName", fileName));
			List<D350023> list = criteria.list();
			if (list == null || list.isEmpty()) {
				response.setResp(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.RECORD_NOT_FOUND);
				response.setFlag(false);
				return response;
			}
			D350023 acctStatus = list.get(0);
			acctStatus.setStatus(status);
			session.update(acctStatus);
			t.commit();
			response.setResp(MSGConstants.SUCCESS);
			response.setErrorMsg(MSGConstants.SUCCESFUL_TRN);
			response.setFlag(true);
			return response;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("ERROR:", e);
			response.setResp(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			response.setFlag(false);
			return response;
		} finally {
			session.close();
			session = null;
			t = null;
		}
	}

	public static CustomerDetails validateOdAccountDp(int brCode, String accNo) {
		try {
		CustomerDetails details = new CustomerDetails();
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria1 = session.createCriteria(D009046.class);
		criteria1.add(Restrictions.eq("id.lbrCode", brCode));
		criteria1.add(Restrictions.eq("id.prdAcctId", accNo.trim()));
		criteria1.addOrder(Order.desc("id.effFromDate"));
		//criteria1.setMaxResults(1);
		logger.error("" + criteria1.toString());
		//String query = "From D009046 where id.lBrCode =:lbrCode and id.prdAcctId =:prdAcctId order by id.dpDate desc";
		
		
		//List<D009046> list = session.createQuery(query).setParameter("lbrCode", brCode).setParameter("prdAcctId", accNo).setMaxResults(1).getResultList();
		
		List<D009046> list = criteria1.list();
		if (list != null && list.size() > 0) {
			logger.error("" + list.get(0).getExpiryDate());
			if (DateUtil.compareODExpDate(String.valueOf(list.get(0).getExpiryDate()), "")) {
				session.close();
				session = null;
				details.setResponse(MSGConstants.SUCCESS);
				details.setErrorMsg(MSGConstants.SUCCESS_MSG);
				//details.setOdLimit(list.get(0));
				return details;
			} else {
				session.close();
				session = null;
				details.setResponse(MSGConstants.ERROR);
				details.setErrorMsg("OD's DP Date is expired.");
				return details;
			}
		}
		session.close();
		session = null;
		criteria1 = null;
		details.setResponse(MSGConstants.ERROR);
		details.setErrorMsg("DP Not Set");
		return details;
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			
			// TODO: handle exception
		}
		return null;
	}
	
	public static D130014 getIMPSChargeType(int lbrcode, int chargeType) {
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D130014.class);
		criteria.add(Restrictions.eq("id.lbrCode", lbrcode));
		//criteria.add(Restrictions.eq("id.insType", Short.valueOf(instrumentType + "")));
		//criteria.add(Restrictions.eq("id.prdCd", prdcd));
		criteria.add(Restrictions.eq("id.chgType", Byte.valueOf(chargeType + "")));
		/***Added by Aniket Desai on 22nd Aug,2019 for Stop Payment ***/
		criteria.addOrder(Order.desc("id.effDate"));
		List<D130014> list = criteria.list();
		session.close();
		session = null;
		if (list != null && list.size() > 0)
			return list.get(0);
		else
			return null;
	}
	
	public static List<D130031> getLatestServiceCharge(String chgtype, Date effDate) {
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D130031.class);
		criteria.add(Restrictions.eq("id.chgType", Byte.valueOf(chgtype)));
		criteria.add(Restrictions.le("id.effDate", effDate));
		criteria.addOrder(Order.desc("id.effDate"));
		List<D130031> list = criteria.list();
		session.close();
		session = null;
		if (list == null || list.isEmpty())
			return null;
		else
			return list;

	}
	
	public static boolean isOverDraftAccount(int lbrCode, String accNo, D009021 productMaster) throws Exception{
		Session session = HBUtil.getSessionFactory().openSession();
		D009022Id id = new D009022Id();
		id.setLbrCode(lbrCode);
		id.setPrdAcctId(accNo);
		D009022 d009022 = session.get(D009022.class, id);
		id = null;
		session.close();
		session = null;
		if (d009022 != null) {
						
			if (productMaster.getModuleType() == 14 || productMaster.getModuleType() == 13) {
				productMaster = null;
				return true;
			} else {
				productMaster = null;
				return false;
			}
		}
		return false;
	}
	
	public static List<D047003> getDDSReceiptDetails(int lbrCode, String acctNo){
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria2 = session.createCriteria(D047003.class);
		criteria2.add(Restrictions.eq("id.lbrCode", lbrCode));
		criteria2.add(Restrictions.eq("id.custPrdAcctId", acctNo));
		criteria2.add(Restrictions.in("acctStat", Arrays.asList(Byte.valueOf("1"), Byte.valueOf("2"))));
		List<D047003> listt = criteria2.list();
		session.close();
		return listt;
	}
	
	public static List<D047003> getDDSDetails(int lbrcode, String productNo) {
		long l_start = System.currentTimeMillis();
		String prodcode = productNo.substring(0, 24);
		List<D047003> termList = null;
		String queryString = " ";
		if (ConfigurationLoader.getParameters(false).getProperty("DATABASE").equalsIgnoreCase("ORACLE"))
			queryString = "from D047003 where id.lbrCode=?" + "and substr(id.custPrdAcctId, 1 ,24) LIKE '%" + prodcode
				+ "%'and acctStat in(1,2)order by id.custPrdAcctId";
		else
			queryString = "from D047003 where id.lbrCode=?" + "and substring(id.custPrdAcctId, 1 ,24) LIKE '%" + prodcode
			+ "%'and acctStat in (1,2) order by id.custPrdAcctId";
		
		Session session = HBUtil.getSessionFactory().openSession();
		Query queryObject = session.createQuery(queryString);
		queryObject.setLong(0, lbrcode);

		long l_end = System.currentTimeMillis();
		logger.error("Instrumentation :<TermDepositReceiptsDAOImpl.java>:<updateSweepLinkSrNoClosure>: "
				+ (l_end - l_start));
		termList = queryObject.list();
		session.close();
		if (termList != null && termList.size() > 0)
			return termList;
		return null;
	}
	
	public static List<D009042> getOverdraftLimit(Date AEffDate,int ABranchCd,String AAcctId){
		
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D009042.class);
		criteria.add(Restrictions.eq("id.lbrCode", ABranchCd));
		criteria.add(Restrictions.eq("id.prdAcctId", AAcctId));
		criteria.add(Restrictions.eq("id.effFromDate", AEffDate));
		criteria.addOrder(Order.desc("id.effFromDate"));
		List<D009042> list = criteria.list();
		
		return list;
	}
	
	
	public static CustomerDetails validateDpExpGrace(int brCode, String accNo) {
		try {
		CustomerDetails details = new CustomerDetails();
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria1 = session.createCriteria(D009046.class);
		criteria1.add(Restrictions.eq("id.lbrCode", brCode));
		criteria1.add(Restrictions.eq("id.prdAcctId", accNo.trim()));
		criteria1.addOrder(Order.desc("id.effFromDate"));
		//criteria1.setMaxResults(1);
		logger.error("" + criteria1.toString());
		
		List<D009046> list = criteria1.list();
		if (list != null && list.size() > 0) {
			D001004 systemParameter = getSystemParameter(list.get(0).getId().getLbrCode(), "GRACEDAYSFORDP");
			String graceDays = systemParameter == null ? "0" : systemParameter.getValue();
			logger.error("" + list.get(0).getExpiryDate());
			long diff = DateUtil.getDateDiffrence(list.get(0).getExpiryDate(), new Date());
			
			if(diff<Long.parseLong(graceDays.trim())) {
			//if (DateUtil.getDateDiff(DateUtil.dateToCalendar(new Date()), DateUtil.dateToCalendar(list.get(0).getExpiryDate()))*-1< Long.parseLong(graceDays.trim())) {
				
				session.close();
				session = null;
				details.setResponse(MSGConstants.SUCCESS);
				details.setErrorMsg(MSGConstants.SUCCESS_MSG);
				//details.setOdLimit(list.get(0));
				return details;
			} else {
				session.close();
				session = null;
				details.setResponse(MSGConstants.ERROR);
				details.setErrorMsg("OD's DP Date is expired.");
				return details;
			}
		}
		session.close();
		session = null;
		criteria1 = null;
		details.setResponse(MSGConstants.ERROR);
		details.setErrorMsg("DP Not Set");
		return details;
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			
			// TODO: handle exception
		}
		return null;
	}
}