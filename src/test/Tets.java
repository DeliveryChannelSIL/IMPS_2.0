package test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import com.sil.commonswitch.VoucherCommon;
import com.sil.constants.MSGConstants;
import com.sil.constants.ResponseCodes;
import com.sil.hbm.D350044;
import com.sil.hbm.D350059;
import com.sil.hbm.D390077;
import com.sil.hbm.D390077Id;
import com.sil.hbm.MBTRSETSEQ;
import com.sil.hbm.PrepaidCardLoadBalance;
import com.sil.prop.ConfigurationLoader;
import com.sil.util.DateUtil;
import com.sil.util.HBUtil;

public class Tets {
	public static Logger logger = Logger.getLogger(Tets.class);
	public static final ArrayList<Byte> accTypeList = new ArrayList<Byte>();

	public static void main(String[] args) {
		try {
			if (!"SRCB0NBL003".contains(ConfigurationLoader.getParameters(false).getProperty("IFSC"))) {
				System.out.println(true);
			}else
				System.out.println(false);
			/*
			Session session = HBUtil.getSessionFactory().openSession();
			Criteria criteria2 = session.createCriteria(D350059.class);
			criteria2.add(Restrictions.eq("id.rrnNo", "823215716655"));
			criteria2.add(Restrictions.eq("id.tranAmt", Double.valueOf("30000.0").intValue()));
			List<D350044> list2 = criteria2.list();
			session.close();
			session = null;
			criteria2 = null;
			if (!list2.isEmpty()) {
				System.out.println("DUPLICTAE Transaction");
//				response.setResponse(MSGConstants.ERROR);
//				response.setErrorMessage(MSGConstants.DUPLICATE_TRN);
//				response.setRrnNo(request.getRRNNo());
//				response.setErrorCode(ResponseCodes.ALREADY_DONE_THIS_TRANSACTION);
//				return response;
			}else
			{
				System.out.println("Transaction ");
			}
			
			session = HBUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(D350059.class);
			criteria.add(Restrictions.eq("id.rrnNo", "823215716655".trim()));
			criteria.add(Restrictions.eq("id.mobNo1", "919987506297".trim()));
			criteria.add(Restrictions.eq("id.mmid1", "9229897".trim()));
			criteria.add(Restrictions.eq("id.responseCd", "00"));
			criteria.add(Restrictions.eq("id.tranAmt", Double.valueOf("30000.0").intValue()));
			List<D350059> list = criteria.list();
			session.close();
			session = null;
			criteria2 = null;
			if (!list.isEmpty()) {
				System.out.println("DUPLICTAE");
			}else
				System.out.println("TRansav");
			*/
			/*ArrayList<Double> lists = new ArrayList<>();
			lists.add(Double.valueOf(10));
			lists.add(Double.valueOf(20));
			lists.add(Double.valueOf(5000));
			lists.add(Double.valueOf(5000));
			lists.add(Double.valueOf(5000));
			double availBal=Collections.min(lists);
			System.out.println("availBal::>>"+availBal);
			System.out.println(VoucherCommon.getNextSetNo());*/
			
			/*System.out.println(" ".trim().isEmpty());
			System.out.println(new Date("02-JUL-2018").getDay());
			System.out.println(new Date("05-JUN-2018").getMonth()+1);
			System.out.println(new Date("05-JUN-2018").getYear()+1900);*/
//			Session session=HBUtil.getSessionFactory().openSession();
//			MBTRSETSEQ setSeq = new MBTRSETSEQ();
//			session.save(setSeq);
//			System.out.println(setSeq.getId());
//			session.close();
			/*Session session = HBUtil.getSessionFactory().openSession();
			Transaction t=session.beginTransaction();
			D390077Id id = new D390077Id();
			id.setCardAlias("RP00000815");
			id.setMobileNo("9403434973");
			D390077 d390077 = session.get(D390077.class, id);
			if (d390077 == null)
				return;
			System.out.println("Limit Date::>" + d390077.getLimitdate().getTime());
			System.out.println("Current Time::>>" + DateUtil.getcurrentDate().getTime());
			if ((d390077.getLimitdate().compareTo(DateUtil.getcurrentDate()) < 0) || (d390077.getLimitdate().compareTo(DateUtil.getcurrentDate()) > 0))
			{
				d390077.setLimitdate(new Date());
				session.update(d390077);
				
				t.commit();
//				return;
			}
			else
				System.out.println("greate");

			
			org.hibernate.Query q = session.createSQLQuery(
					"SELECT * FROM  PREPAID_CARD_LOAD_BALANCE WHERE ENTRYDATE='"+DateUtil.getcurrentDateStringDDMONYYYY()+"' AND RESPCODE='00' AND RRN NOT IN (SELECT RRN FROM  REVERSE_LOAD_BALANCE WHERE ENTRYDATE='"+DateUtil.getcurrentDateStringDDMONYYYY()+"' AND RESPCODE='00')");

//			org.hibernate.Query q=session.createSQLQuery("SELECT * FROM  PREPAID_CARD_LOAD_BALANCE WHERE ENTRYDATE='30-MAR-2018' AND RESPCODE='00' AND RRN NOT IN (SELECT RRN FROM  REVERSE_LOAD_BALANCE WHERE ENTRYDATE='18-Apr-2018' AND RESPCODE='00')");
			List<Object[]> list = q.list();
			System.out.println("list.size()::>>"+list.size());
			Double amount=0.0;
			for(Object[] balance:list)
			{
				amount+=Double.valueOf(balance[5]+"");
				
			}
			System.out.println("Amount::>>"+amount);
			session.close();
			session = null;
			t=null;
*/			/*
			 * for (int i = 0; i < 1000; i++) { Session session =
			 * HBUtil.getSessionFactory().openSession(); Transaction t =
			 * session.beginTransaction(); D010004Id id = new D010004Id();
			 * id.setBatchCd("MBTR"); id.setEntryDate(new Date("16-DEC-2016"));
			 * id.setLbrCode(3); D010004 selectedBtch =
			 * session.get(D010004.class, id);
			 * 
			 * System.out .println("Response::>>" +
			 * VoucherCommon.updateBatchBalance(session, 10, selectedBtch, 3,
			 * "D")); t.commit(); session.close(); }
			 */
			// session.flush();

			// System.out.println(HBUtil.getSessionFactory().openSession().isConnected());
			// System.out.println(ATMTransactionServiceIMPL.loadHashMap());;
			// System.out.println("0002SB 00076529".substring(10));
			// Session session=HBUtil.getSessionFactory().openSession();
			// String query="SELECT DISTINCT substring(CustPrdAcctId,1,8) FROM
			// D047003 WHERE LBrCode='3' AND AgtPrdAcctId='PGM
			// 000000000000000100000000' AND AcctStat<>3";
			// Query q=session.createSQLQuery(query);
			// List<String> list=q.list();
			// System.out.println(list);

			// String productCode="3264";
			// System.out.println(;;

			/*
			 * Session session=HBUtil.getSessionFactory().openSession();
			 * Criteria criteria=session.createCriteria(D009021.class);
			 * criteria.add(Restrictions.eq("id.lbrCode",4)); ResultTransformer
			 * projection=criteria.PROJECTION;
			 * criteria.setProjection(Projections.distinct(projection)(
			 * "id.prdCd"));
			 */
			// projection.add(Projections.property("id.prdCd"));

			// List<String> list=criteria.list();
			// System.out.println("list::>>"+list);
			/*
			 * com.sil.commonswitch.CustomerPhysicalCardOnboardingreq req=new
			 * com.sil.commonswitch.CustomerPhysicalCardOnboardingreq();
			 * req.setAnnualIncome("1000"); req.setBcagent("SHANKS");
			 * req.setCardAlias("123456"); req.setCity("MUMBAI");
			 * req.setCustomerStatus("2"); req.setCustomerType("2");
			 * req.setDateofbirth("12/09/1990");
			 * req.setEmailaddress("amar.raut@sil.co.in");
			 * req.setFatcadecl("Y"); req.setFirstname("AMAR");
			 * req.setGender("MALE"); // req.setHeader();
			 * req.setLaddress1("Palash laine");
			 * req.setLaddress2("Gadage nagar"); req.setLastname("RAUT");
			 * req.setLcity("AMARAVATI"); req.setLcountry("INDIA");
			 * req.setLpincode("444603"); req.setLstate("MAHARASHTRA");
			 * req.setMiddlename("WASUDEO"); req.setMobilenumber("8983389108");
			 * req.setMothermaidenname("ALKA"); req.setNationality("INDIAN");
			 * req.setOccupation("SALARIED"); req.setPincode("444603");
			 * req.setPoliticallyExposedPerson("NO"); req.setProduct("1");
			 * req.setResaddress1("Palash Laine");
			 * req.setResaddress2("Gadge nager"); req.setRescountry("INDIA");
			 * req.setSourceIncomeType("1000"); req.setState("MAHARASHTRA");
			 * 
			 * Session session=HBUtil.getSessionFactory().openSession(); try {
			 * Transaction t=session.beginTransaction(); session.save(req);
			 * t.commit(); session.close(); session=null; } catch (Exception e)
			 * { // TODO: handle exception e.printStackTrace(); }
			 */
			/*
			 * String name="ADVANCE TAX PAID F.Y. 2013-14"; String
			 * fname="",mname="",lname=""; String [] nameArr=name.split(" ");
			 * 
			 * if(nameArr.length>=2) { fname=nameArr[0]; mname=nameArr[1];
			 * lname=nameArr[2]; }else if(nameArr.length>=1) { fname=nameArr[0];
			 * mname=nameArr[1]; }else if(nameArr.length>=0) fname=nameArr[0];
			 * else fname="NONAME"; System.out.println("fname::>>"+fname);
			 * System.out.println("mname::>>"+mname);
			 * System.out.println("lname::>>"+lname);
			 */
			// Session session= HBUtil.getSessionFactory().openSession();
			// Transaction t=session.beginTransaction();
			// System.out.println(""+VoucherMPOS.updateProductBalances(10d,
			// 3,"D", "CGST",session));
			// t.commit();
			// session.close();
			// session=null;
			// String productCode="CGST";
			// String accno="1";
			// System.out.println(""+String.format("%-8s", productCode));
			// System.out.println();
			// double amount=100;
			// System.out.println(amount%100);
			// System.out.println(amount%100);
			/*
			 * AccountInfo info=new AccountInfo();
			 * info.setAccNo("043203100003296"); info.setBrCode("3");
			 * info.setErrorMsg("SUCCESSFUL TRANSACTION");
			 * info.setResponse("SUCCESS"); StringWriter sw = new
			 * StringWriter(); JAXB.marshal(info, sw); String xmlString =
			 * sw.toString(); System.out.println("XML:::"+xmlString);
			 * AccountInfo customer = JAXB.unmarshal(new
			 * StringReader(xmlString), AccountInfo.class);
			 * System.out.println("customer::>>"+customer);
			 */
			// Session session=HBUtil.getSessionFactory().openSession();
			// Criteria criteria=session.createCriteria(D009021.class);
			// criteria.setProjection(Projections.distinct(Projections.property("id.prdCd")));
			// List<String> list=criteria.list();
			// System.out.println("list::>>"+list);
			// session.close();
			// session=null;
			// String crAccno=String.format("%-8s",
			// VoucherCommon.getSysParameter(MSGConstants.SIMPAYCRBRANCH).trim())+"000000000000000000000000";

			// System.out.println("Credit Account::>>"+crAccno);
			// Session session=HBUtil.getSessionFactory().openSession();
			// System.out.println("isLoan
			// Account::>>>"+DataUtils.isLoanAccount(3, "ARCHELN", session));
			// String beneficiaryName = (res.getNickName() == null ||
			// res.getNickName().trim().length() < 1) ? "NOBENNAME" :
			// res.getNickName();
			/*
			 * Session session=HBUtil.getSessionFactory().openSession();
			 * Transaction transaction=session.beginTransaction(); Date openDate
			 * = VoucherCommon.getOpenDateNew(3,session); // Get Open Date
			 * if(openDate == null){
			 * System.out.println("Open Date Not Found. Aborting Transaction");
			 * } System.out.println("Open Date is "+openDate); //Get BatchCodes
			 * from properties file. String batchCode =
			 * Props.getBatchProperty("PIGMEDEPOSIT");
			 * System.out.println("Batch Code Form Properties File : "+batchCode
			 * ); String batchCodes[] = batchCode.split("~");
			 * System.out.println("Number of Batches is "+batchCodes.
			 * length+"\t Names are "+Arrays.toString(batchCodes));
			 * if(batchCodes == null || batchCodes.length <1){
			 * System.out.println("Batch Codes Not Found in Properties File.");
			 * } /// Get Selected Batch. D010004 selectedBatch =
			 * VoucherCommon.getSelectedBatchNew(3, batchCodes,
			 * openDate,session);
			 * System.out.println("selectedBatch::>>>"+selectedBatch);
			 * if(selectedBatch == null){
			 * System.out.println("No Active Batch Found."); }
			 * transaction.commit(); session.close(); transaction=null;
			 * session=null;
			 */// VoucherCommon.updateBatchBalance(session, 10d, "MBTR", 3);
				// VoucherCommon.updateBatchBalance(session, 10d, "MBTR", 3);
				// System.out.println("003001000006707".substring(3,7));
				// System.out.println(""+MSGConstants.ABB);
				// System.out.println(""+MSGConstants.ABB_ACC);
			/*
			 * CriteriaBuilder builder = HibernateUtil.getCriteriaBuilder();
			 * EntityManager em = HibernateUtil.getEntityManager();
			 * CriteriaQuery<D350078> criteriaQuery =
			 * builder.createQuery(D350078.class); Root<D350078> studentRoot =
			 * criteriaQuery.from(D350078.class);
			 * criteriaQuery.select(studentRoot);
			 * criteriaQuery.where(builder.equal(studentRoot.get("id.custNo"),
			 * "Ram")); List<D350078> students =
			 * em.createQuery(criteriaQuery).getResultList(); for ( D350078
			 * student : students) {
			 * System.out.println("id:"+student.getId().getCustNo()+", age:"
			 * +student.getAtmcount());
			 */
			// }
			/*
			 * Session session=HBUtil.getSessionFactory().openSession();
			 * EntityManagerFactory
			 * entityManagerFactory=session.getEntityManagerFactory();
			 * CriteriaBuilder builder =
			 * entityManagerFactory.getCriteriaBuilder(); CriteriaQuery<D350078>
			 * criteria = builder.createQuery( D350078.class ); Root<D350078>
			 * root = criteria.from( D350078.class ); criteria.select( root );
			 * criteria.where( builder.equal( root.get( "id.custNo" ),
			 * "John Doe" ) );
			 * 
			 * List<D350078> persons = entityManager.createQuery( criteria
			 * ).getResultList();
			 */

			/*
			 * String
			 * lists=ConfigurationLoader.getParameters(false).getProperty(
			 * "CHEQUE_BOOK_ACCTYPE"); if(lists!=null ||
			 * lists.trim().length()>0) { String [] newList=lists.split(",");
			 * if(newList.length!=0) { for(int i=0;i<newList.length;i++)
			 * accTypeList.add(Byte.valueOf(newList[i])); } }
			 * System.out.println("accTypeList::>>"+accTypeList);
			 */
			// DateFormat dateTime = new SimpleDateFormat("HHmmssSSS");
			// Date date = new Date();
			// String stan= dateTime.format(date).toString();
			// System.out.println("stan::>>"+stan);

			/*
			 * for(int i=0;i<999999;i++) { // System.out.println(new
			 * Date().getTime()); logger.error(""+new Date().getTime()); }
			 */

			// String acctno32digit="SB 000000000000670700000000";
			// System.out.println(Long.parseLong("25") *
			// Long.parseLong("1".trim())+ChequeBookRequest.getStartInsNoNew(""+3,Long.valueOf(10),
			// (ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD_FLAG").trim().equalsIgnoreCase("Y"))?ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD"):acctno32digit.substring(0,8).trim()));

			// System.out.println(""+VoucherMPOS.getNextSetNo());
			// String acctno32digit="SB 000000000000670700000000";
			// System.out.println((ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD_FLAG").trim().equalsIgnoreCase("Y"))?ConfigurationLoader.getParameters(false).getProperty("CHEQUEBOOK_ALPHA_CD"):acctno32digit.substring(0,8).trim());
			// int lbrcode=3;
			// String insType="10";
			// String acctno32digit="SB 000000000000670700000000";
			// D010080
			// chequeBookFileGeneration=ChequeBookRequest.getStartInsNo(""+lbrcode,Long.valueOf(insType),
			// "SA");
			// System.out.println("Start
			// InsNo::>>>"+chequeBookFileGeneration.getStartInsNo());

			/*
			 * for(int i=0;i<10000;i++) { Session
			 * session=HBUtil.getSessionFactory().openSession(); Transaction
			 * transaction=session.beginTransaction();
			 * VoucherMPOS.updateProductBalances(10, 2,"D", "ABB", session);
			 * VoucherMPOS.updateProductBalances(10, 2,"C", "ABB", session);
			 * 
			 * VoucherMPOS.updateProductBalances(10, 1,"D", "ABB", session);
			 * VoucherMPOS.updateProductBalances(10, 1,"C", "ABB", session);
			 * 
			 * VoucherMPOS.updateProductBalances(10, 3,"D", "ABB", session);
			 * VoucherMPOS.updateProductBalances(10, 3,"C", "ABB", session);
			 * 
			 * VoucherMPOS.updateProductBalances(10, 4,"D", "ABB", session);
			 * VoucherMPOS.updateProductBalances(10, 4,"C", "ABB", session);
			 * 
			 * VoucherMPOS.updateProductBalances(10, 5,"D", "ABB", session);
			 * VoucherMPOS.updateProductBalances(10, 5,"C", "ABB", session);
			 * 
			 * VoucherMPOS.updateProductBalances(10, 6,"D", "ABB", session);
			 * VoucherMPOS.updateProductBalances(10, 6,"C", "ABB", session);
			 * 
			 * VoucherMPOS.updateProductBalances(10, 7,"D", "ABB", session);
			 * VoucherMPOS.updateProductBalances(10, 7,"C", "ABB", session);
			 * 
			 * VoucherMPOS.updateProductBalances(10, 8,"D", "ABB", session);
			 * VoucherMPOS.updateProductBalances(10, 8,"C", "ABB", session);
			 * 
			 * VoucherMPOS.updateProductBalances(10, 9,"D", "ABB", session);
			 * VoucherMPOS.updateProductBalances(10, 9,"C", "ABB", session);
			 * 
			 * transaction.commit(); session.close(); session=null; }
			 */

			// VoucherMPOS.updateProductBalances(10, 2,"D", "1", session);

			// Session session=HBUtil.getSessionFactory().openSession();
			// Criteria criteria=session.createCriteria(D350023.class);
			// criteria.add(Restrictions.eq("id.custNo", "53393"));
			// criteria.add(Restrictions.eq("id.mmid", mmid));
			// criteria.add(Restrictions.eq("id.fromDate",
			// DateUtil.getUtilDate(fromdate)));
			// criteria.add(Restrictions.eq("id.toDate",
			// DateUtil.getUtilDate(toDate)));
			// criteria.add(Restrictions.eq("id.entryDate",
			// DateUtil.getCurrentDate()));
			// criteria.add(Restrictions.eq("id.acctNo", acctno32digit));
			// List<D350023> list=criteria.list();
			// System.out.println("list.size()::>>"+list.get(0));
			// VoucherMPOS.updateProductBalances(amount, , "D", "PGM", session);
			// 53393 8307001 3 SB 000000000000670700000000 7/28/2012 12:00:00 AM
			// 4/28/2017 12:00:00 AM
			// Criteria criteria=session.createCriteria(D350023.class);
			// criteria.add(Restrictions.eq("id.custNo", "53393"));
			// criteria.add(Restrictions.eq("id.mmid", "8307001"));
			// criteria.add(Restrictions.eq("id.fromDate",
			// DateUtil.getUtilDate("20120728")));
			// criteria.add(Restrictions.eq("id.toDate",
			// DateUtil.getUtilDate("20170428")));
			// criteria.add(Restrictions.eq("id.entryDate",
			// DateUtil.getCurrentDate()));
			// criteria.add(Restrictions.eq("id.acctNo", "SB
			// 000000000000670700000000"));
			// List<D350023> list=criteria.list();
			// System.out.println("list.size()::>>"+list.size());

			// double amount=9.9997481E7;
			// BigDecimal b = new BigDecimal(amount);
			// System.out.println(b.toString());

			// String accNo="SB 000000000000670700000000";
			// stan = String.format("%06d", Long.valueOf(stan));
			// System.out.println("AccNo::>>"+(accNo.substring(0,24)+String.format("%08d",Long.valueOf(accNo.substring(24))+1l)));
			/*
			 * DateFormat dateTime = new SimpleDateFormat("HHmmss"); Date date =
			 * new Date(); String field7Value =
			 * dateTime.format(date).toString();
			 * System.out.println("field7Value::>>"+field7Value);
			 */
			// System.out.println(""+VoucherMPOS.getNextReconNo(2));
			// Session session=HBUtil.getSessionFactory().openSession();
			// Transaction t=session.beginTransaction();
			// String tType="P2A";
			// String mmid2="DLXB0000001";
			// Criteria criteria=session.createCriteria(D350038.class);
			// Criteria c=session.createCriteria(D350038.class);
			// c.add(Restrictions.ilike("ifscode",mmid2.substring(0,4)+"%"));
			// c.setProjection(Projections.property("nbin"));
			// List list=c.list();
			//
			// System.out.println("list::>>"+list.get(0));
			// /*if(tType.equalsIgnoreCase(MSGConstants.P2P_TRANSACTION))
			// criteria.add(Restrictions.eq("nbin",
			// Integer.valueOf(mmid2.substring(0,4))));
			// if(tType.equalsIgnoreCase(MSGConstants.P2A_TRANSACTION))
			// criteria.add(Restrictions.ilike("ifscode",
			// mmid2.substring(0,4)+"%"));
			// List<D350038> list=criteria.list();
			// System.out.println(""+list.size());
			// System.out.println(""+list.get(0).getNbin());*/
			// session.close();
			// session=null;
			// D009022 sourceAccount = DataUtils.getAccount("005001000019694");
			// System.out.println("sourceAccount::>>"+sourceAccount);
			/*
			 * Session session=HBUtil.getSessionFactory().openSession();
			 * Criteria criteria=session.createCriteria(D350044.class);
			 * criteria.add(Restrictions.eq("id.lbrCode", ));
			 * criteria.add(Restrictions.eq("id.batchCd", "MBTR"));
			 * criteria.add(Restrictions.eq("id.entryDate",
			 * DataUtils.getOpenDate(lbrCode)));
			 * criteria.add(Restrictions.eq("id.rrnNo",rrn));
			 * criteria.add(Restrictions.eq("id.mobNo1", mob1));
			 * criteria.add(Restrictions.eq("id.mmid1",mmid1));
			 * criteria.add(Restrictions.eq("id.mobNo2",mob2));
			 * criteria.add(Restrictions.eq("id.mmid2", mmid2));
			 * criteria.add(Restrictions.eq("id.tranAmt", amount));
			 * criteria.add(Restrictions.eq("id.drcr", "C")); List<D350044>
			 * list=criteria.list(); session.close(); session=null;
			 * if(list==null || list.isEmpty()) {
			 * response.setResponse(MSGConstants.ERROR);
			 * response.setErrorMessage(MSGConstants.RECORD_NOT_FOUND);
			 * response.setErrorCode(ResponseCodes.VERIFICATION_RESP_M0);
			 * response.setRrnNo(rrn); return response; }
			 */

			// System.out.println(Double.valueOf(amount)%list.get(0).getDepositAmt()!=0);
			// String accNo=CustomerMasterServiceImpl.getNextAccNo("3485","3");
			// System.out.println("accNo::>>>"+accNo);
			// Long recieptNo=Long.valueOf(accNo.substring(16,32))+1l;
			// System.out.println("recieptNo:>>>>"+recieptNo);

			// String startInsNo="1244545";
			// System.out.println("Final Reciept
			// Number::>>>>>"+String.format("%016d", recieptNo));
			// CustomerMasterServiceImpl.getNextAccNo("FRR",3+"");
			/*
			 * int n=12/12;
			 * 
			 * int noOfDays, year, month = 12, week, days;
			 * 
			 * System.out.print("Enter Number of Days: "); noOfDays = 365;
			 * 
			 * year = noOfDays/365; noOfDays=noOfDays%365;
			 * 
			 * // month = noOfDays/30; // noOfDays=noOfDays%30; month=month*31;
			 * System.out.println("month::>>>"+month);
			 * 
			 * 
			 * 
			 * week = noOfDays/7; noOfDays=noOfDays%7;
			 * 
			 * 
			 * System.out.println("Year: " + year); System.out.println("Month: "
			 * + month); System.out.println("Week: " + week);
			 * System.out.println("Day: " + noOfDays);
			 */
			// System.out.println("Rate of
			// intrest::>>>"+DataUtils.getMatValue(50000, 8.5, 365));
			/*
			 * String example =
			 * "amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12";
			 * byte[] bytes = example.getBytes();
			 * 
			 * System.out.println("Text : " + example);
			 * System.out.println("Text [Byte Format] : " + bytes);
			 * System.out.println("Text [Byte Format] : " + bytes.toString());
			 * 
			 * String a ="[B@2a139a55"; String s = new String(bytes);
			 * System.out.println("Text Decryted : " + s);
			 */

			//
			// String
			// string="amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12amarraut12";
			// System.out.println("string.getBytes()::>>"+string.getBytes());
			//
			// System.out.println("Bytes to
			// string.getBytes()::>>"+string.getBytes().toString());
			/*
			 * // for (int i = 1; i <= 50; i++) // {
			 * 
			 * if ( 500%1000 == 0) System.out.print("amouny"); else
			 * System.out.println("sdhdh"); // if(i % 15==
			 * 1)System.out.println();
			 */

			// }

			// String bookSize=""+30;
			// String noOfCheqBook="1";

			// String startInsNo = Long.toString((Long.parseLong(bookSize) *
			// Long.parseLong(noOfCheqBook.trim())) +
			// (Long.parseLong("26".trim())));
			// System.out.println("startInsNo::>>"+startInsNo);
			// System.out.println(String.format("%012d", 1));
			// Session session=HBUtil.getSessionFactory().openSession();
			// Transaction t=session.beginTransaction();
			// Criteria criteria=session.createCriteria(D350032.class);
			// criteria.add(Restrictions.eq("id.custNo", "53393"));
			// criteria.add(Restrictions.eq("acctNo",sourceAccount.getId().getPrdAcctId()));
			// criteria.add(Restrictions.eq("brCode",
			// sourceAccount.getId().getLbrCode()));
			// List<D350032> list= criteria.list();

			// System.out.println("list.size()::>>"+list.size());
			// Query q=session.createSQLQuery("select max(MMID)+1 from
			// dbo.D350032 where CustNo='"+53393+"'");
			//// q.setParameter(0, custNo);
			// System.out.println("q::>>"+q);
			// q.getResultList();
			// System.out.println("MMID::>>"+q.getResultList().get(0));
			// session.close();
			// t.commit();
			// t=null;
			// D001004 system = DataUtils.getSystemParameter(0,
			// MSGConstants.NOOFTRNS);
			// System.out.println("system::>>"+system);
			// int noOfTrns =system == null ? 0 :
			// Integer.valueOf(system.getValue());
			// System.out.println("noOfTrns::>>"+noOfTrns);
			//
			// D001004 parameter = DataUtils.getSystemParameter(0,
			// MSGConstants.SETOTPEXPTIME);
			// System.out.println("parameter::>>"+parameter);
			//
			// int setOtpExpTime =parameter == null ? 0 :
			// Integer.valueOf(parameter.getValue());
			// System.out.println("setOtpExpTime::>>"+setOtpExpTime);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		// String mmid = MSGConstants.NBIN+String.format("%03d", 1);
		// System.out.println("MMID::>>"+mmid);
		// System.out.println(DateUtil.compareIBnextPwdDate("2016-11-08
		// 00:00:00.0", ""));

		// System.out.println(""+Integer.parseInt("100.0"));
		// System.out.println(Integer.valueOf("SBW
		// 000000000000000800000000".substring(16,24).trim()));
		// System.out.println(new Date("01-JAN-1900"));
		/*
		 * ArrayList<Double> list=new ArrayList<>(); list.add(100.0);
		 * list.add(100.0); list.add(100.0); list.add(100.0); list.add(50.0);
		 * Collections.sort(list); System.out.println(list.get(0));
		 */

		// String prodCode="SBW";
		// String welcomekitAccNo="1";
		// String acc32 = String.format("%-8s",
		// prodCode)+"00000000"+String.format("%08d",
		// Integer.valueOf(welcomekitAccNo.trim()))+"00000000";
	}
}
