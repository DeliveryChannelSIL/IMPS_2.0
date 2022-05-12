package com.sil.commonswitch;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.sil.hbm.D009022;
import com.sil.hbm.D009022Id;
import com.sil.util.HBUtil;
public class AccountStatus 
{
	public	static org.apache.log4j.Logger logger=org.apache.log4j.Logger.getLogger(AccountStatus.class);
	static int acctNo;
	static String lbr ;
	
	/*public static String get32DigitAcctNo(String acctno15digit) 
	{
		
		String pCode = acctno15digit.substring(3, 7);
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		D001002 code = (D001002) session.createCriteria(D001002.class).add(Restrictions.eq("id.codeType", 11080)).add(Restrictions.eq("codeDesc", pCode)).uniqueResult();
		tx.commit();
		session.close();
		
		if(code==null)
		{
			return null;
		}
		else logger.error("Product Code : "+code.getId().getCode());
		String productCode = code.getId().getCode();		
		String accno = acctno15digit.substring(7);
		String acc32 = String.format("%-8s", productCode.substring(0, 8))+"00000000"+accno+"00000000"; 
		return acc32;
	
	}*/
	
	
	public static D009022 getStatus(int lbrCode, String prdAcctId )
	{
		acctNo = prdAcctId.length();
		String acct32 = DataUtils.get32DigitAcctNo(prdAcctId);
		logger.error("ACCT32 : "+acct32);
		if(acct32 == null)
		{
			
			return null;
		}
		
		D009022Id d009022id = new D009022Id();
		D009022 d009022 = new D009022();
		d009022id.setLbrCode(lbrCode);
		d009022id.setPrdAcctId(acct32);
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		d009022 = session.get(D009022.class, d009022id);
	
		tx.commit();
		session.close();
		session=null;
		return d009022; 	
	}

	public static void main(String[] args) 
	{
		//D009022 d009022 = AccountStatus.getStatus(1, "001200100000194");
		D009022 d009022 = AccountStatus.getStatus(1, "001200100000003");
		
		logger.error("ACCOUNT STATUS : "+d009022.getAcctStat());
		HBUtil.getSessionFactory().close();
	}

}
