package com.sil.loan;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.sil.hbm.D009021;
import com.sil.hbm.D009022;
import com.sil.hbm.D009040;
import com.sil.hbm.D010004;
import com.sil.hbm.D030002;
import com.sil.hbm.D030003;
import com.sil.hbm.D030003Id;
import com.sil.util.HBUtil;

@SuppressWarnings("unchecked")
public class LoanMasterDao{

	private static final Logger logger = LogManager.getLogger(LoanMasterDao.class.getName());
	
	public D030002 getLoanParametersObject(Long lbrcode, String prdcd) {

		D030002 loanParameters = null;
		Session session = HBUtil.getSessionFactory().openSession();
		String hql = "From D030002 where id.lbrCode=:lbrcode and id.prdCd=:prdcd";
		loanParameters = (D030002) session.createQuery(hql).setParameter("lbrcode", lbrcode.intValue())
				.setParameter("prdcd", prdcd).getResultList().get(0);
		session.close();
		return loanParameters;
	}

	
	public D030003 getDetailsLoanBalances(Integer lbrcode, String prdacctid,Session session) {

		D030003 loanBalances = null;
		
		String hql = "From D030003 where id.lbrCode=:lbrcode and id.prdAcctId=:prdacctid";
		loanBalances = (D030003) session.createQuery(hql).setParameter("lbrcode", lbrcode)
				.setParameter("prdacctid", prdacctid).getResultList().get(0);
		return loanBalances;
	}

	
	public Double getLoanBalanceDetails(int lbrCode, String accountNumber, Session session) {

		Double overdue = 0D;
		String query = "select mainbalfcy-(intprvdfcy-intpaidfcy)-(othchgprvdfcy-othchgpaidfcy)-(penalprvdfcy- penalpaidfcy)-(taxprvdfcy- taxpaidfcy)"
				+ " -(inclrgfcy -unclearfcy)" + " -(intlcy -penallcy)"
				+ "FROM D030003 where  id.lbrcode=:lbrcode and id.prdacctid=:prdacctid";
		List<Double> overdues = session.createQuery(query).setParameter("lbrcode", lbrCode).setParameter("prdacctid", accountNumber)
				.getResultList();
		if (!overdues.isEmpty())
			overdue = overdues.get(0);

		return overdue;
	}

	

	public D030003 getLoanBalancesOnLbrAndPrdAcct(int lbrCode, String prdAcctid ) throws Exception {
		Session session = HBUtil.getSessionFactory().openSession();
		D030003Id id = new D030003Id(lbrCode,prdAcctid);
		D030003 loanBalances = session.get(D030003.class, id);
		return loanBalances;
	}
	
	
	public void saveVoucherAndUpdateAcount(D009040 voucher, D009022 account, D009021 productMaster,
			D010004 batch, double amount, String type, Session session) throws Exception {

		logger.debug("******************saveVoucherAndUpdateAcount voucher******************* " + voucher);
		logger.debug("******************account******************* " + account);
		logger.debug("******************productMaster******************* " + productMaster);
		logger.debug("******************batch******************* " + batch);
		logger.debug("******************amount******************* " + voucher.getFcyTrnAmt());
		logger.debug("******************type******************* " + type);
		if (type.equalsIgnoreCase("D")) {
			voucher.setDrCr('D');
			/***Added By Aniket 22th NOV, 2018 for Inward Loan Tra. CashFlowType issue No:34749 ontack Portal ***/
			if(voucher.getActivityType()==null || voucher.getActivityType().equalsIgnoreCase(""))
				voucher.setActivityType("DR");
			if(voucher.getCashFlowType()==null || voucher.getCashFlowType().equalsIgnoreCase(""))
				voucher.setCashFlowType("DR");
			/***Added By Aniket Desai 9th Sep, 2019 for Product level Account ***/
			if(!"P".equalsIgnoreCase(productMaster.getAcctOpenLevel()+""))
				account = balance(amount, "D", account);
			productMaster.setLcyBal(productMaster.getLcyBal() - amount);
			productMaster.setFcyBal(productMaster.getFcyBal() - amount);
			batch.setTotalDrVcrs(batch.getTotalDrVcrs() + 1);
			batch.setTotalDrAmtLcy(batch.getTotalDrAmtLcy() + amount);
		}
		if (type.equalsIgnoreCase("C")) {
			voucher.setDrCr('C');
			/***Added By Aniket 22th NOV, 2018 for Inward Loan Tra. CashFlowType issue No:34749 ontack Portal ***/
			if(voucher.getActivityType()==null || voucher.getActivityType().equalsIgnoreCase(""))
				voucher.setActivityType("CR");
			if(voucher.getCashFlowType()==null || voucher.getCashFlowType().equalsIgnoreCase(""))
				voucher.setCashFlowType("CR");
			//voucher.setActivityType("CR");
			//voucher.setCashFlowType("CR");
			
			voucher.setParticulars(voucher.getParticulars().length()>70? voucher.getParticulars().substring(0, 70):voucher.getParticulars().trim());
			/***Added By Aniket Desai 9th Sep, 2019 for Product level Account ***/
			if(!"P".equalsIgnoreCase(productMaster.getAcctOpenLevel()+""))
				account = balance(amount, "C", account);
			productMaster.setLcyBal(productMaster.getLcyBal() + amount);
			productMaster.setFcyBal(productMaster.getFcyBal() + amount);
			batch.setTotalCrVcrs(batch.getTotalCrVcrs() + 1);
			batch.setTotalCrAmtLcy(batch.getTotalCrAmtLcy() + amount);
		}

		logger.info("-------setNo : " + voucher.getId().getSetNo()
				+ "---------------------------------------------scrollNo : " + voucher.getId().getScrollNo()
				+ ", openDate :" + voucher.getId().getEntryDate() + ", batchcd :" + voucher.getId().getBatchCd()
				+ ", acctId :" + voucher.getMainAcctId() + ", lbrCode :" + voucher.getId().getLbrCode());

		System.out.println(voucher.toString());
		session.save(voucher);
		/***Added By Aniket Desai 9th Sep, 2019 for Product level Account ***/
		if(!"P".equalsIgnoreCase(productMaster.getAcctOpenLevel()+""))
			session.saveOrUpdate(account);
		session.saveOrUpdate(productMaster);
		session.saveOrUpdate(batch);

	}
	public D009022 balance(double amount, String type, D009022 account) {

		if (type.equalsIgnoreCase("D")) {

			logger.info("Final Debit Amount : " + (account.getActClrBalFcy() - amount));

			account.setActClrBalFcy(account.getActClrBalFcy() - amount);
			account.setShdClrBalFcy(account.getShdClrBalFcy() - amount);
			account.setShdTotBalFcy(account.getShdTotBalFcy() - amount);
			account.setActTotBalFcy(account.getActTotBalFcy() - amount);
			account.setActTotBalLcy(account.getActTotBalLcy() - amount);
		} else {
			account.setActClrBalFcy(account.getActClrBalFcy() + amount);
			account.setShdClrBalFcy(account.getShdClrBalFcy() + amount);
			account.setShdTotBalFcy(account.getShdTotBalFcy() + amount);
			account.setActTotBalFcy(account.getActTotBalFcy() + amount);
			account.setActTotBalLcy(account.getActTotBalLcy() + amount);
		}
		return account;
	}
}
