package com.sil.util;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import com.sil.hbm.D001002;

public class AccountDetailsUtil {

	public	static org.apache.log4j.Logger logger=org.apache.log4j.Logger.getLogger(AccountDetailsUtil.class);
	public static void main(String[] args) {
		logger.error(get32DigitAcctNo("003001000006707"));
	}

	@SuppressWarnings("deprecation")
	public static String get32DigitAcctNo(String acctno15digit) 
	{
		logger.error("acctno15digit recieved in AccountDetailsUtil.get32DigitAcctNo()::>>"+acctno15digit);
		if(acctno15digit.length()==16)
			acctno15digit = acctno15digit.substring(1);
		String pCode = acctno15digit.substring(3, 7);
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(D001002.class);
		criteria.add(Restrictions.eq("id.codeType", 11080));
		criteria.add(Restrictions.eq("codeDesc", pCode));
		D001002 code = (D001002) criteria.uniqueResult();
		tx.commit();
		session.close();
		session=null;
		tx=null;
		if(code==null)
		{
			logger.error("Account code not found in D001002");
			return null;
		}
		String productCode = code.getId().getCode();
		if(productCode.length()>8)
			productCode=productCode.substring(0, 8);
		String prCode = String.format("%-8s", productCode);
		String accno = acctno15digit.substring(7);
		String acc32 = prCode+"00000000"+accno+"00000000"; 
		return acc32;
	
	}
	
	public static String get32DigitAcctNo(String acctno15digit,Session session) 
	{
		logger.error("acctno15digit recieved in AccountDetailsUtil.get32DigitAcctNo()::>>"+acctno15digit);
		String pCode = acctno15digit.substring(3, 7);
		Criteria criteria = session.createCriteria(D001002.class);
		criteria.add(Restrictions.eq("id.codeType", 11080));
		criteria.add(Restrictions.eq("codeDesc", pCode));
		D001002 code = (D001002) criteria.uniqueResult();
		if(code==null)
		{
			logger.error("Account code not found in D001002");
			return null;
		}
		String productCode = code.getId().getCode();
		if(productCode.length()>8)
			productCode=productCode.substring(0, 8);
		
		String prCode = String.format("%-8s", productCode);
		String accno = acctno15digit.substring(7);
		String acc32 = prCode+"00000000"+accno+"00000000"; 
		return acc32;
	
	}
	
	public static String getProductAccountNumber(String productAccountNo){
		if(productAccountNo!=null && productAccountNo.trim().length()==32){
		    String productNo="";
		    String accountNo="";
		    productNo = productAccountNo.substring(0, 8).trim();
			accountNo = String.valueOf(Integer.parseInt(productAccountNo.substring(16, 24)));
			if(!accountNo.equalsIgnoreCase("0")){
			    productNo = productNo+"/ "+accountNo;
	            if(Integer.parseInt(productAccountNo.substring(24, 32))!=0){
	                productNo = productNo+"/ "+String.valueOf(Integer.parseInt(productAccountNo.substring(24, 32)));
	            }
			}
			//return productCode+"/"+accountId;
			return productNo;
		}
		return null;
	}

}
