package com.sil.commonswitch;

import com.sil.hbm.D009022;
import com.sil.util.HBUtil;

public class BalanceEnquiry 
{
	public	static org.apache.log4j.Logger logger=org.apache.log4j.Logger.getLogger(BalanceEnquiry.class);
	/*public static D009022 getAccount(String prdAcctId )
	{
		int brCode = Integer.parseInt(prdAcctId.substring(0,3));
		String acct32 = AccountStatus.get32DigitAcctNo(prdAcctId);
		
		if(acct32 == null) return null;
		
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();		
		D009022Id id = new D009022Id();
		id.setLbrCode(brCode);
		id.setPrdAcctId(acct32);		
		D009022 bal = session.get(D009022.class, id);
		tx.commit();
		session.close();
		
		return bal;		
	}*/
	public static void main(String[] args) 
	{
		D009022 d009022 = DataUtils.getAccount("001200100000194");
		double unClrEffFcy = d009022.getUnClrEffFcy();
		double TotalLienFcy = d009022.getTotalLienFcy();
		double ActClrBalFcy = d009022.getActClrBalFcy();
		double avlBal = (ActClrBalFcy-TotalLienFcy);
		int a = d009022.getCustNo();
		logger.error("MOBILE NUMBER"+a);
		logger.error("TOTAL UNCLEAREBALC EFFFY : " +unClrEffFcy);
		logger.error("TOTAL AVAILBALE BALANACE : " +avlBal);
		HBUtil.getSessionFactory().close();
	}
}
