package com.sil.commonswitch;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import com.sil.hbm.D009044;
import com.sil.util.HBUtil;

public class ChequeStatus
{
	public	static org.apache.log4j.Logger logger=org.apache.log4j.Logger.getLogger(ChequeStatus.class);	
	@SuppressWarnings({ "deprecation", "unchecked" })
	public static D009044 getChequeStatus(int lbr, String accNo, String chequeNo)
	{
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		List<D009044> lst;
		Criteria criteria = session.createCriteria(D009044.class);
		criteria.add(Restrictions.eq("id.lbrCode", lbr));
		criteria.add(Restrictions.eq("id.issuedTo", ""+accNo));
		criteria.add(Restrictions.eq("id.instruNo", ""+chequeNo));
		lst = criteria.list();

		D009044 d009044;
		if(lst.size()==0)
		{
			return null;
		}
		
		d009044 = lst.get(0);
		tx.commit();
		
		session.close();
		session=null;
		return d009044;
		
	}
	
	public static void main(String[] args) 
	{
		
		D009044 d009044 = ChequeStatus.getChequeStatus(1,"001200100000194","000000102564");
		if(d009044==null)
		{
			logger.error("INVALID DETAILS:");
		}else
		{
		logger.error("STATUS : "+d009044.getStatus());
		}
	}

}
