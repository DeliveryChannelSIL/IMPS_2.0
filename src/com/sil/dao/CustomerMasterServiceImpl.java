package com.sil.dao;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.pmw.tinylog.Logger;
import com.sil.constants.MSGConstants;
import com.sil.domain.CustDetailsResponse;
import com.sil.domain.CustomerDetails;
import com.sil.hbm.D001005;
import com.sil.hbm.D009011;
import com.sil.hbm.D009012;
import com.sil.hbm.D009022;
import com.sil.hbm.D020004;
import com.sil.prop.ConfigurationLoader;
import com.sil.util.HBUtil;
public class CustomerMasterServiceImpl {
	public static org.apache.log4j.Logger logger=org.apache.log4j.Logger.getLogger(CustomerMasterServiceImpl.class);
	public static CustDetailsResponse getCustomerDetails(String custNo, String mobNo, String panNo, String name,
			String aadhar) {
		List<CustomerDetails> customerDetails = new ArrayList<CustomerDetails>();
		CustDetailsResponse details = new CustDetailsResponse();
		Session session = HBUtil.getSessionFactory().openSession();
		try {
			Criteria criteria = session.createCriteria(D009011.class);
			if (custNo.trim() != null && !custNo.trim().equalsIgnoreCase(""))
				criteria.add(Restrictions.eq("custNo", Integer.valueOf(custNo)));
			if (mobNo != null && !mobNo.trim().equalsIgnoreCase(""))
				criteria.add(Restrictions.eq("pagerNo", mobNo.trim()));
			if (panNo != null && !panNo.trim().equalsIgnoreCase(""))
				criteria.add(Restrictions.eq("panNoDesc", panNo.trim()));
			if (name != null && !name.trim().equalsIgnoreCase(""))
				criteria.add(Restrictions.ilike("longname", "%" + name + "%"));
			List<D009012> list = null;
			ArrayList<Integer> arrayList=new ArrayList<>();
			if (aadhar != null && aadhar.trim().length()==12) {
				if (String.valueOf(Long.valueOf(aadhar.trim())).length() == 12) {
					Criteria criteria2 = session.createCriteria(D009012.class);
					criteria2.add(Restrictions.eq("adharNo", aadhar.trim()));
					list = criteria2.list();
					if (list.isEmpty() || list.size() < 1) {
						Criteria criteria3 = session.createCriteria(D009012.class);
						criteria3.add(Restrictions.ilike("adharNo", "%" + aadhar.trim() + "%"));
						list = criteria3.list();
						if (list.isEmpty() || list.size() < 1) {
							details.setResponse("ERROR");
							details.setErrorMsg("Customer not found.");
							// details.setCustList(customerDetails);
							return details;	
						}
					}
					for(D009012 d009012:list)
						arrayList.add(d009012.getCustNo());
//					criteria.add(Restrictions.in("custNo", Integer.valueOf(list.get(0).getCustNo())));
					criteria.add(Restrictions.in("custNo", arrayList));
				}
			}
			List<D009011> code = criteria.list();
			if (code != null && code.size() > 0) {
				for (int i = 0; i < code.size(); i++) {
					CustomerDetails details1 = new CustomerDetails();
					details1.setAdd1(code.get(i).getAdd1().trim());
					details1.setAdd2(code.get(i).getAdd2().trim());
					details1.setAdd3(code.get(i).getAdd3().trim());
					details1.setCityCode(code.get(i).getCityCd().trim());
					details1.setCountryCode(code.get(i).getCounCd().trim());
					details1.setCustNo(code.get(i).getCustId().trim());
					details1.setEmailId(code.get(i).getEmailId().trim());
					details1.setLbrCode("" + code.get(i).getLbrCode());
					details1.setLongName(code.get(i).getLongname().trim());
					details1.setMainCustNo("" + code.get(i).getMainCustNo());
					details1.setCustNo("" + code.get(i).getCustNo());
					details1.setMobileNo(code.get(i).getPagerNo().trim());
					details1.setNameTitle(code.get(i).getNameTitle().trim());
					details1.setPanNo(code.get(i).getPanNoDesc().trim());
					details1.setPinCode(code.get(i).getPinCode().trim());
					customerDetails.add(details1);
				}
				details.setCustList(customerDetails);
				details.setResponse("SUCCESS");
				details.setErrorMsg(MSGConstants.SUCCESS_MSG);
				logger.error("Search customer Response::>>"+details);
				return details;
			} else {
				details.setResponse("ERROR");
				details.setErrorMsg("Customer not found.");
				// details.setCustList(customerDetails);
				return details;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Logger.error("ERROR:",e);
			details.setResponse("ERROR");
			details.setErrorMsg("Customer not found.");
			customerDetails = null;
			return details;
		} finally {
			session.close();
			session = null;
		}
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	public static String getNextCustNo() {
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		Criteria criteria = session.createCriteria(D001005.class);
		criteria.add(Restrictions.eq("id.lbrCode", 0));
		criteria.add(Restrictions.eq("id.catType", "GENCUSTN"));
		criteria.addOrder(Order.desc("id.lnodate"));
		List<D001005> d001005 = criteria.list();
		d001005.get(0).setLastNo(d001005.get(0).getLastNo() + 1);
		t.commit();
		session.close();
		t = null;
		session = null;
		return "" + Long.valueOf(d001005.get(0).getLastNo() + 1l);// 221484
	}

	public static String getNextAccNo(String prodCode, String lbrCode) {
		Session session = HBUtil.getSessionFactory().openSession();
		String query = " "; 
		
		if (ConfigurationLoader.getParameters(false).getProperty("DATABASE").equalsIgnoreCase("ORACLE"))
		query = "select max(substr(PrdAcctId,17,8))+1 FROM D009022 WHERE substr(PrdAcctId,1,8)='"
				+ prodCode.trim() + "' and  LBrCode='" + lbrCode.trim() + "'";
		else
			query = "select max(substring(PrdAcctId,17,8))+1 FROM D009022 WHERE substring(PrdAcctId,1,8)='"
					+ prodCode.trim() + "' and  LBrCode='" + lbrCode.trim() + "'";
		Query q = session.createSQLQuery(query);
		String maxAccNo = "";
		if (null == q.getResultList().get(0))
			maxAccNo = "00000001";
		else
			maxAccNo = String.format("%08d", Long.valueOf("" + q.getResultList().get(0)));
		System.out.println("q.getResultList().get(0):::>>>" + q.getResultList().get(0));
		Logger.error("maxAccNo.length()::>>>" + maxAccNo.length());
		String acc32 = String.format("%-8s", prodCode) + "00000000" + maxAccNo + "00000000";
		Logger.error("Account Number::>>>" + acc32);
		// System.out.println("acc32::>>"+acc32);
		session.close();
		session = null;
		return acc32;
	}

	public static String getNextAccNo(String prodCode, String lbrCode, Session session) {
		String query =" "; 
		if (ConfigurationLoader.getParameters(false).getProperty("DATABASE").equalsIgnoreCase("ORACLE"))
			query = "select max(substr(PrdAcctId,17,8))+1 FROM D009022 WHERE substr(PrdAcctId,1,8)='"
				+ prodCode.trim() + "' and  LBrCode='" + lbrCode.trim() + "'";
		else
			query = "select max(substring(PrdAcctId,17,8))+1 FROM D009022 WHERE substring(PrdAcctId,1,8)='"
					+ prodCode.trim() + "' and  LBrCode='" + lbrCode.trim() + "'";
		Query q = session.createSQLQuery(query);
		String maxAccNo = "";
		if (null == q.getResultList().get(0))
			maxAccNo = "00000001";
		else
			maxAccNo = String.format("%08d", Long.valueOf("" + q.getResultList().get(0)));
		String acc32 = String.format("%-8s", prodCode) + "00000000" + maxAccNo + "00000000";
		Logger.error("Account Number::>>>" + acc32);
		return acc32;
	}

	public static String getNextTDAccNo(String prodCode, String lbrCode) {
		Session session = HBUtil.getSessionFactory().openSession();
		String query="";
		
		if (ConfigurationLoader.getParameters(false).getProperty("DATABASE").equalsIgnoreCase("ORACLE")) {
			query = "select max(PrdAcctId) FROM D009022 WHERE PrdAcctId like'" + String.format("%-8s", prodCode.trim())
			+ "%' and  LBrCode='" + lbrCode.trim() + "'";
		}else		
			query = "select max(PrdAcctId) FROM D009022 WHERE substring(PrdAcctId,1,8)='" + prodCode.trim()
				+ "' and  LBrCode='" + lbrCode.trim() + "'";
		Query q = session.createSQLQuery(query);
		String maxAccNo = "";
		String receiptNo = "";
		if (null == q.getResultList().get(0) || "00000000".equalsIgnoreCase(q.getResultList().get(0).toString())) {
			maxAccNo = "00000001";
			receiptNo = "00000001";
		}else {
			maxAccNo = String.format("%08d",
					1l + Long.valueOf("" + q.getResultList().get(0).toString().substring(16, 24)));
			System.out.println("maxAccNo:::>>" + maxAccNo);
			System.out.println("q.getResultList().get(0):::>>>" + q.getResultList().get(0));
			receiptNo = String.format("%08d", Long.valueOf(q.getResultList().get(0).toString().substring(24, 32)));
		}
		System.out.println("receiptNo::>>>" + receiptNo);
		Logger.error("maxAccNo.length()::>>>" + maxAccNo.length());
		String acc32 = String.format("%-8s", prodCode) + "00000000" + maxAccNo + receiptNo;
		Logger.error("Account Number::>>>" + acc32);
		session.close();
		session = null;
		return acc32;
	}

	
	public static D009022 getTDAccNo(String prodCode, int lbrCode, int custNo,Session session ) {
		//Session session = HBUtil.getSessionFactory().openSession();
		String query = "FROM D009022 WHERE id.prdAcctId='" + prodCode.trim()
				+ "' and  id.lbrCode=" + lbrCode + " and custNo="+ custNo;
		Query q = session.createQuery(query);
		String maxAccNo = "";
		if (q.getResultList().isEmpty())
			return null;
		else
			return (D009022) q.getResultList().get(0);
	}
	
	public static String getNextTDRecipt(String prodCode, int lbrCode) {
		long l_start = System.currentTimeMillis();

		List<String> termList = null;
		String queryString=" ";
		
		if (ConfigurationLoader.getParameters(false).getProperty("DATABASE").equalsIgnoreCase("ORACLE")) 
			queryString = "select id.prdAcctId from D020004 where id.lbrCode=?" + "and substr(id.prdAcctId, 1 ,24) LIKE '%" + prodCode.substring(0, 24)
					+ "%' order by id.prdAcctId desc";
		else
			queryString = "select id.prdAcctId from D020004 where id.lbrCode=?" + "and substring(id.prdAcctId, 1 ,24) LIKE '%" + prodCode.substring(0, 24)
			+ "%' order by id.prdAcctId desc";
		Query queryObject = HBUtil.getSessionFactory().openSession().createQuery(queryString);
		queryObject.setParameter(0, lbrCode);

		long l_end = System.currentTimeMillis();
		logger.error("Instrumentation :<TermDepositReceiptsDAOImpl.java>:<updateSweepLinkSrNoClosure>: "
				+ (l_end - l_start));
		termList = queryObject.getResultList();
		if (termList != null && termList.size() > 0)
			return termList.get(0);
		return null;
	
	}

	
	public static void main(String[] args) {
		// getCustomerDetails("53393","919930611095","","");
		// getCustomerDetails("53393","919930611095","","");
		// System.out.println(getCustomerDetails("","","","").getResponse());
//		System.out.println(getNextCustNo());
		System.out.println(getCustomerDetails("", "", "", "", "123456789012"));;
		// getNextAccNo("CA", "2");
	}
}
