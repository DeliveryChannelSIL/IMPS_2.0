package com.sil.commonswitch;

import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import com.sil.hbm.D001002;
import com.sil.hbm.D009011;
import com.sil.hbm.D009022;
import com.sil.hbm.D009022Id;
import com.sil.hbm.D350078;
import com.sil.util.AcctDetailsPojo;
import com.sil.util.HBUtil;

public class AccountDetails {

	public	static org.apache.log4j.Logger logger=org.apache.log4j.Logger.getLogger(AccountDetails.class);
	@SuppressWarnings({ "deprecation", "unchecked" })
	public static AcctDetailsPojo getAcctDetails(String acctNo15Digit, String mobileNo)
	{
		logger.error("acctNo15Digit received in AcctDetailsPojo.getAcctDetails()::>>"+acctNo15Digit);
		String acctno32digit=get32DigitAcctNo(acctNo15Digit);
	
		int lbrcode=Integer.parseInt(acctNo15Digit.substring(0, 3));
		logger.error("lbrcode : "+lbrcode+"\n Mob : "+mobileNo+"\n 32 Digit AcctNo: "+acctno32digit);
		
		AcctDetailsPojo result = null;
		Session session = HBUtil.getSessionFactory().openSession();
		D009022Id id = new D009022Id();
		id.setLbrCode(lbrcode);
		id.setPrdAcctId(acctno32digit);
		
		boolean match = false;
		D009022 d22 = session.get(D009022.class, id);	
		D009011 d11 = session.get(D009011.class, d22.getCustNo());
		
		Criteria criteria = session.createCriteria(D350078.class);
		Disjunction dj = Restrictions.disjunction();
		dj.add(Restrictions.eq("id.mobileNo", mobileNo));
		dj.add(Restrictions.eq("id.mobileNo", "91"+mobileNo));		
		criteria.add(dj);
		List<D350078> d78List = criteria.list();
		session.close();
		session=null;
		if(d78List.size() == 0) return null;
		else{
			for(D350078 d78 : d78List){
				String d78cust = d78.getId().getCustNo().trim();
				String d11cust = (""+d11.getCustNo()).trim();
				
				if(d78cust.equalsIgnoreCase(d11cust)){
					match = true;
				}
			}
		}
		
		if(!match) logger.error("Mobile Number Did not match");
		
		if(d22 != null && d11 != null && match){
			logger.error("\n\n\n Setting values");
			
			result=new AcctDetailsPojo(d22.getId().getLbrCode(), d22.getId().getPrdAcctId(), d22.getLongName(), d22.getCustNo(), d11.getPanNoDesc(), d11.getAdd1(), d11.getAdd2(), d11.getAdd3(), d11.getPinCode(), mobileNo, d22.getUnClrEffFcy(),(d22.getActClrBalFcy()-d22.getTotalLienFcy()), d22.getAcctStat());
		}
	
		return result;
		
	}  
	
	@SuppressWarnings("deprecation")
	public static String get32DigitAcctNo(String acctno15digit) 
	{
		String pCode = acctno15digit.substring(3, 7);
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(D001002.class);
		criteria.add(Restrictions.eq("id.codeType", 11080));
		criteria.add(Restrictions.eq("codeDesc", pCode));
		D001002 code = (D001002) criteria.uniqueResult();
		tx.commit();
		session.close();
		if(code==null)
		{
			logger.error("Account code not found in D001002");
			return null;
		}
		String productCode = code.getId().getCode();
		
		String prCode = String.format("%-8s", productCode.substring(0, 8));
		String accno = acctno15digit.substring(7);
		String acc32 = prCode+"00000000"+accno+"00000000"; 
				
		return acc32;
	
	}

	public static void main(String args[]) {

		AcctDetailsPojo acct= getAcctDetails("001200100000003","9028680026");
		
		logger.error("Avail Bal : "+acct.getAvailableBal()+"\nAcctno: "+acct.getPrdAcctid()+"\nLbrcode :"+acct.getLbrCode()+"\nLong name :"+acct.getLongName()+"\nCustNo : "+acct.getCustNo()+"\n");
		
		
	/*	String acctno32digit=get32DigitAcctNo("001200100000003");
		
		logger.error("No : "+acctno32digit);*/
	}
}