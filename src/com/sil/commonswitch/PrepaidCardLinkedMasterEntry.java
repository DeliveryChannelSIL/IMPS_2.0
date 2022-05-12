package com.sil.commonswitch;

import org.hibernate.Session;
import org.hibernate.Transaction;
import com.sil.hbm.PrepaidCardLinkedMaster;
import com.sil.hbm.PrepaidCardLinkedMasterId;
import com.sil.util.HBUtil;
public class PrepaidCardLinkedMasterEntry {
	public static PrepaidCardLinkedMaster getPrepaidCardLinkedMasterObj(String custNo,String cardNo,String insno,String mobileNo,String acctName,String acctNo,int brCode)
	{
		PrepaidCardLinkedMaster master=new PrepaidCardLinkedMaster();
		PrepaidCardLinkedMasterId id=new PrepaidCardLinkedMasterId();
		id.setCustNo(custNo);
		id.setInsno(insno);
		id.setMobileNo(mobileNo);

		master.setAcctName(acctName);
		master.setAcctNo(acctNo);
		master.setCardno(cardNo);
		master.setBrCode(brCode);
		master.setId(id);
		return master;
	}
	
	public static void main(String[] args) {
		PrepaidCardLinkedMaster obj=getPrepaidCardLinkedMasterObj("53393","652122XXXXXX5432", "0000001", "918983389108", "AMAR W RAUT", "SBVIS   000000000000329600000000", 3);
		try {
			Session session=HBUtil.getSessionFactory().openSession();
			Transaction t=session.beginTransaction();
			session.save(obj);	
			t.commit();
			session.close();
			session=null;
			t=null;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
	}
}
