package com.sil.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;

import com.sil.constants.SwiftCoreConstants;
import com.sil.hbm.D010014;
import com.sil.hbm.D020004;
import com.sil.hbm.TDSMaintananceForTD;
import com.sil.util.DateUtility;
import com.sil.util.HBUtil;

public class TermDepositReceiptsDao {

	public static Logger logger=Logger.getLogger(TermDepositReceiptsDao.class);
	
	public static List<Object[]> getLastInterestVoucherDateAndAmt(int lbrcode, String prdacctid, Date cbldate) throws SQLException {
		String queryString = "";
		Session session = HBUtil.getSessionFactory().openSession();
		List<Object[]> allODClosingDPAdhocDPoffset = null;
		queryString = "select distinct valueDate,fcyTrnAmt,valueDate from D009040 where id.lbrCode = :lbrcode and mainAcctId = :mainacctid and id.batchCd = :batchcd " +
					  " and activityType = :activitytype and CashFlowType = :cashflowtype and id.entryDate = " +
					  " (select max(id.entryDate) from D009040 where id.lbrCode = :lbrcode and mainAcctId = :mainacctid and id.batchCd = :batchcd " +
					  " and activityType = :activitytype and CashFlowType = :cashflowtype and id.entryDate <= :entrydate_sql)";
		allODClosingDPAdhocDPoffset = session.createQuery(queryString).setParameter("lbrcode", lbrcode)
					.setParameter("mainacctid", prdacctid)
		.setParameter("batchcd", SwiftCoreConstants.INTEREST_TRANSFER)
		.setParameter("activitytype", SwiftCoreConstants.ACTIVITY_TYPE_INTEREST)
		.setParameter("cashflowtype", SwiftCoreConstants.TDINTCR)
		.setParameter("entrydate_sql", DateUtility.convertUtiltoSqlDate(cbldate)).getResultList();
		
		return allODClosingDPAdhocDPoffset;
	}
	
	public static List<TDSMaintananceForTD> getAllTDSDeducted(int lbrcode, String prdacctid, Date asoffdate, Date dateEndDate) throws SQLException {
		Session  session= HBUtil.getSessionFactory().openSession();
		String queryString = "from TDSMaintananceForTD where id.LBrCode = :lbrcode and id.prdAcctId = :prdacctid and valueDate between :asoffdate and :dateEndDate order by id.entryDate";//order by trnamount desc,drcr desc
		List<TDSMaintananceForTD> queryObject = null;
		queryObject = session.createQuery(queryString)
				.setParameter("lbrcode", lbrcode)
				.setParameter("prdacctid", prdacctid)
				.setParameter("asoffdate", asoffdate)
				.setParameter("dateEndDate", dateEndDate).getResultList();
		return queryObject;
	}
	
	public static D010014 getClosingBalanceAfterClosure(int lbrcode, String prdacctid, Date matdate, boolean premature) {
		long l_start = System.currentTimeMillis();
		D010014 closingBalance = new D010014();
		String queryString = "";
		List<D010014> cblReturn = new ArrayList<D010014>();
		if (premature) {
			queryString = "from D010014 WHERE lbrcode = :lbrcode AND prdacctid = :prdacctid and cbldate = (select max(cbldate) from D010014 where lbrcode = :lbrcode AND prdacctid = :prdacctid and cbldate >= :cbldate) ";
		} else {
			queryString = "from D010014 WHERE lbrcode = :lbrcode AND prdacctid = :prdacctid and cbldate = (select max(cbldate) from D010014 where lbrcode = :lbrcode AND prdacctid = :prdacctid and cbldate <= :cbldate) ";
		}
		Query queryObject = HBUtil.getSessionFactory().openSession().createQuery(queryString);
		queryObject.setParameter("lbrcode", lbrcode);
		queryObject.setParameter("prdacctid", prdacctid);
		queryObject.setParameter("cbldate", matdate);
		long l_end = System.currentTimeMillis();
		logger.debug("Instrumentation :<TermDepositReceiptsDAOImpl.java>:<getClosingBalanceAfterClosure>: "
				+ (l_end - l_start));
		cblReturn = queryObject.list();
		if (premature) {
			if (cblReturn.size() > 0) {
				closingBalance = (D010014) cblReturn.get(0);
				// getHibernateTemplate().evict(closingBalance);
				return closingBalance;
			} else {
				queryString = "from D010014 WHERE lbrcode = :lbrcode AND prdacctid = :prdacctid and cbldate = (select max(cbldate) from D010014 where lbrcode = :lbrcode AND prdacctid = :prdacctid and cbldate <= :cbldate) ";
				queryObject = HBUtil.getSessionFactory().openSession().createQuery(queryString);
				queryObject.setParameter("lbrcode", lbrcode);
				queryObject.setParameter("prdacctid", prdacctid);
				queryObject.setParameter("cbldate", matdate);
				List<D010014> list1 = new ArrayList<D010014>();
				list1 = queryObject.list();
				if (list1.size() > 0) {
					closingBalance = (D010014) list1.get(0);
					// getHibernateTemplate().evict(closingBalance);
					return closingBalance;
				} else {
					return new D010014();
				}
			}
		} else {
			return new D010014();
		}
	}
	
	public static D010014 getClosingBalanceForLastInterestRunDateNew(D020004 termDepositReceipts, Calendar operDate, boolean newReceipt) throws SQLException {
		String queryString = "";
		Session  session= HBUtil.getSessionFactory().openSession();
		List<D010014>  closingBalance ;
		if((newReceipt==true) && (termDepositReceipts.getReceiptStatus()!=99)){
			queryString = "from D010014 WHERE id.lbrCode =:lbrcode AND id.prdAcctId = :prdacctid and id.cblDate = (select max(id.cblDate) from D010014 where id.lbrCode = :lbrcode AND id.prdAcctId = :prdacctid and id.cblDate <= :cbldate) ";
		}else{
			queryString = "from D010014 WHERE id.lbrCode = :lbrcode AND id.prdAcctId = :prdacctid and id.cblDate = (select max(id.cblDate) from D010014 where id.lbrCode = :lbrcode AND id.prdAcctId = :prdacctid and id.cblDate <= :cbldate and balance2 > 0) ";
		}
	 closingBalance= (List<D010014>)session.createQuery(queryString)
				.setParameter("lbrcode", termDepositReceipts.getId().getLbrCode())
				.setParameter("prdacctid", termDepositReceipts.getId().getPrdAcctId())
				.setParameter("cbldate", operDate.getTime()).getResultList();
		
		if(closingBalance==null||closingBalance.size()<=0)
		{
			return null;
		}else {
		return closingBalance.get(0);
		}
	}
	
	
	public static D010014 getClosingBalanceForLastInterestRunDate(
			D020004 termDepositReceipts, Calendar startDate) {
		Session  session= HBUtil.getSessionFactory().openSession();
		String queryString = "from D010014 WHERE id.lbrcode = :lbrcode AND id.prdAcctId = :prdacctid and id.cbldate = (select max(id.cbldate) from D010014 where id.lbrcode = :lbrcode AND prdacctid = :prdacctid and cbldate < :cbldate) ";
		List<D010014> closingBalance=(List<D010014>) session.createQuery(queryString)
			.setParameter("lbrcode", termDepositReceipts.getId().getLbrCode())
			.setParameter("prdacctid", termDepositReceipts.getTrfrAcctId())
			.setParameter("cbldate", startDate.getTime()).getResultList();
		if(closingBalance.size()>0)
		{
			return closingBalance.get(0);
		}else
		{
			return null;
		}
	}
	

	public static D010014 getClosingBalanceForLastInterestRunDate(D020004 termDepositReceipts, Calendar startDate,boolean newReceipt) throws SQLException {
		String queryString = "";
		List<D010014> closingBalance;
		Session  session= HBUtil.getSessionFactory().openSession();
		queryString = "from D010014 WHERE id.lbrCode = :lbrcode AND id.prdAcctId = :prdacctid and id.cblDate = (select max(id.cblDate) from D010014 where id.lbrCode = :lbrcode AND id.prdAcctId = :prdacctid and id.cblDate < :cbldate) ";
		closingBalance= (List<D010014>) session.createQuery(queryString)
			.setParameter("lbrcode", termDepositReceipts.getId().getLbrCode())
			.setParameter("prdacctid", termDepositReceipts.getId().getPrdAcctId())
			.setParameter("cbldate", startDate.getTime()).getResultList();
		if(closingBalance.size()>0)
		{
			return closingBalance.get(0);
		}else
		{
			return null;
		}
	}
}
