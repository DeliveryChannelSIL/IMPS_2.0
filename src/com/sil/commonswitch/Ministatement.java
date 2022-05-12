package com.sil.commonswitch;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import com.sil.hbm.D009040;
import com.sil.util.HBUtil;


public class Ministatement 
{
	public	static org.apache.log4j.Logger logger=org.apache.log4j.Logger.getLogger(Ministatement.class);
	@SuppressWarnings("deprecation")
	public static List<D009040> getMinistatement(int lbrCode, String prdAcctId, int noofstmts)
	{
		List<D009040> lst;
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria=session.createCriteria(D009040.class);
		criteria.add(Restrictions.eq("vcrAcctId", prdAcctId));
		criteria.add(Restrictions.eq("id.lbrCode", lbrCode));
		criteria.add(Restrictions.ne("canceledFlag", 'C'));
		criteria.addOrder(Order.desc("postTime"));	
		criteria.addOrder(Order.desc("postDate"));
		//criteria.setMaxResults(noofstmts);
		lst=criteria.list();
		session.close();
		session=null;
		return lst;	
	}
	public static String get35DigitMiniStatementString(java.util.Date date,char drcr, String narration, String amt)
	{
		String str="";
		String dt=date.toString().split(" ")[0];
		String d[]=dt.split("-");
		narration=narration.substring(0,13);
		
		String s1=d[2]+d[1]+d[0];		// DDMMYYYY
		char s2=drcr;
		String s3=String.format("%-14s", narration);
		String s4=String.format("%12s", amt).replace(" ","0");
		
		str=s1+s2+s3+s4;
		
		return str;
	}

	public static void main(String[] args)
	{
		int s;
		List<D009040> lst= getMinistatement(1, "23      000000000000833800000000",10);
		
		for(int i=0;i<lst.size();i++)
		{
			logger.error("Mini Stmt : "+i+" : "+get35DigitMiniStatementString(lst.get(i).getId().getEntryDate(), lst.get(i).getDrCr(), lst.get(i).getParticulars(), ""+lst.get(i).getFcyTrnAmt()));
		}
		
		//logger.error(BalanceEnquiry.getMinistatement(1, "1 000000000000000600000000"));
	

	}
	
}
