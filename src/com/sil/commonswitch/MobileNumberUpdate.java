package com.sil.commonswitch;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import com.sil.hbm.D009022;
import com.sil.hbm.D350078;
import com.sil.util.HBUtil;

public class MobileNumberUpdate 
{
	public	static org.apache.log4j.Logger logger=org.apache.log4j.Logger.getLogger(MobileNumberUpdate.class);
	@SuppressWarnings({ "deprecation", "unchecked" })
	public static String getMobileUpdate(String accNo, String mobNo)
	{
		String lbr = accNo.substring(0, 3);
		int lbrCode = Integer.parseInt(lbr);
		D009022 status = AccountStatus.getStatus(lbrCode, accNo) ;
		if(status == null)
		{
			logger.error("Account not found");
			return "09";
		}
		int custNo = status.getCustNo();
		logger.error("Cust :"+custNo);
		List<D350078> lst;
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(D350078.class);
		criteria.add(Restrictions.eq("id.custNo", ""+custNo));
		boolean bool = false;
		lst=criteria.list();
		tx.commit();
		session.close();
		session=null;
		
		if(lst.size() == 0) return "09";
		
		D350078 d78 = lst.get(0);
		logger.error("Row : "+d78);
		
		for(D350078 d : lst)
		{
			if(d.getId().getMobileNo().contains(mobNo)) bool = true;
		}
		if(bool)
		{
			logger.error("Mobile Number Already Exists");
			return "01";
		}
		else{
			logger.error("MOBILE NUMBER UPDATED SUCCESSFULLY : "+mobNo);
			session = HBUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Query query = session.createQuery("update D350078 set MobileNo = '"+mobNo+"' where MobileNo = '"+d78.getId().getMobileNo().trim()+"' and CustNo = '"+custNo+"'");
			int result = query.executeUpdate();
			tx.commit();
			session.close();	
			return "00";
		}
	}
	
	public static void main(String[] args) {
		
		String d350078 = MobileNumberUpdate.getMobileUpdate("001200100000194", "9665997897");
		
		
	}
	
	

}
