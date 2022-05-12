package com.sil.dao;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import com.sil.commonswitch.DataUtils;
import com.sil.constants.MSGConstants;
import com.sil.domain.AccountOpenRequest;
import com.sil.domain.AccountOpenResponse;
import com.sil.domain.CustomerDetails;
import com.sil.domain.TransactionValidationResponse;
import com.sil.hbm.D001002;
import com.sil.hbm.D001004;
import com.sil.hbm.D001005;
import com.sil.hbm.D009011;
import com.sil.hbm.D009012;
import com.sil.hbm.D009022;
import com.sil.hbm.D009022Id;
import com.sil.hbm.D009037;
import com.sil.hbm.D009037Id;
import com.sil.hbm.D009122;
import com.sil.hbm.D009122Id;
import com.sil.hbm.D010053;
import com.sil.hbm.D010053Id;
import com.sil.hbm.D010054;
import com.sil.hbm.D010054Id;
import com.sil.hbm.D010153;
import com.sil.hbm.D010153Id;
import com.sil.hbm.D020118;
import com.sil.hbm.D047003;
import com.sil.hbm.D047003Id;
import com.sil.hbm.D048004;
import com.sil.hbm.D048005;
import com.sil.hbm.D350078;
import com.sil.prop.ConfigurationLoader;
import com.sil.service.TransactionServiceImpl;
import com.sil.util.DateUtil;
import com.sil.util.HBUtil;
public class AccountMasterServiceImpl {
	public static Logger logger=Logger.getLogger(AccountMasterServiceImpl.class);
	public static AccountOpenResponse insertValues(String brcode, String prodCode, String accType,
			String modeOfOperation, String nameTitle, String name, String add1, String add2, String add3, String pan,
			String panCardNo, String area, String cityCode, String pinCode, String mobNo, String emailId, String isNew,
			String custNo, String welcomeKitFlag, String welcomekitAccNo, String indOther, AccountOpenRequest req) {
		logger.error("Account Open Req::>>"+req);
		AccountOpenResponse res = new AccountOpenResponse();
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		try {
			if (mobNo != null && mobNo.length() == 10)
				mobNo = "91" + mobNo;
			String panNo = "";
			String aadharNo = "000000000000000";
			
			
			if (ConfigurationLoader.getParameters(false).getProperty("AADHAR_FLAG").trim().equalsIgnoreCase("Y")) {
				if (panCardNo != null && panCardNo.contains("##")) {
					String[] panAdharNO = panCardNo.split("##");
					System.out.println("panCardNo::>>" + panCardNo);
					if (panCardNo.toString().equalsIgnoreCase("##")) {

					} else if (panCardNo.endsWith("##"))

						panNo = (panAdharNO[0] == null || panAdharNO[0].trim().equalsIgnoreCase("")) ? panNo
								: panAdharNO[0];
					else if (panCardNo.startsWith("##"))
						aadharNo = (panAdharNO[1] == null || panAdharNO[1].trim().equalsIgnoreCase("")) ? aadharNo
								: panAdharNO[1];
					else {
						panNo = (panAdharNO[0] == null || panAdharNO[0].trim().equalsIgnoreCase("")) ? panNo
								: panAdharNO[0];
						aadharNo = (panAdharNO[1] == null || panAdharNO[1].trim().equalsIgnoreCase("")) ? aadharNo
								: panAdharNO[1];
					}
				}
			} else
				panNo = panCardNo;

			List<D001005> d001005 = null;
			D009011 d009011 = new D009011();
			D009022 d009022 = new D009022();
			D010054 d010054 = new D010054();
			
			try {
				
				/*D001004 system = DataUtils.getSystemParameter(0, "MEMBERACCTONLYM");
				String[] memberList = system.getValue().trim().split(":");
				String memberAcctOnlyM = memberList[0];
				boolean flag = false;
				if(memberAcctOnlyM.equalsIgnoreCase("MA")) {
					if(!isMemberApplicationExist((Integer.parseInt(custNo.trim())))) {
						if(isMemberExist(Integer.parseInt(custNo.trim()))) {
							flag = true;
						}else {
							res.setResponse("ERROR");
							res.setErrorMessage("Membership Application has to be Completed");
							return res;
						}
					}else flag=false;
				}
				if(!flag){
					if(!isMemberExist(Integer.parseInt(custNo.trim()))) {
						res.setResponse("ERROR");
						res.setErrorMessage("Customer is Not A Member");
						return res;
					}
				}*/
				
				if(!isMemberApplicationExist((Integer.parseInt(custNo.trim()))) || !isMemberExist(Integer.parseInt(custNo.trim()))) {
					res.setResponse("ERROR");
					res.setErrorMessage("Customer is Not A Member");
					return res;
				}
				
			}catch (Exception e) {
				// TODO: handle exception
				logger.error(e);
				res.setResponse("ERROR");
				res.setErrorMessage("Customer is Not A Member");
				return res;
			}
			if (welcomeKitFlag.trim().equalsIgnoreCase("Y") && custNo.length() > 0) {
				String acc32 = String.format("%-8s", prodCode) + "00000000"
						+ String.format("%08d", Integer.valueOf(welcomekitAccNo.trim())) + "00000000";
				D009022Id id=new D009022Id();
				id.setLbrCode(Integer.valueOf(brcode.trim()));
				id.setPrdAcctId(acc32.trim());
				D009022 list=session.get(D009022.class, id);
				if (list!=null) {
					String custId = String.valueOf(list.getCustNo());
					if (custId.trim().equalsIgnoreCase("500000")) {
						System.out.println("custNo::>>" + custNo);
						Query q = session.createQuery("UPDATE D009022 SET custNo ='" + custNo
								+ "' WHERE id.lbrCode = '" + brcode + "' AND id.prdAcctId = '" + acc32 + "'  ");
						q.executeUpdate();
						t.commit();
						res.setResponse("SUCCESS");
						res.setErrorMessage(
								"Dear Customer your account has been successfully opened under branch code "
										+ brcode + " and account number " + brcode.trim() + "/" + prodCode + "/"
										+ Integer.valueOf(welcomekitAccNo));
						res.setCustNo(custNo.trim());
						res.setAccNo(acc32.trim());
						return res;
					}
					if (t.isActive()) t.rollback();
					res.setResponse("ERROR");
					res.setErrorMessage("Invalid account number.");
					return res;
				} else {
					if (t.isActive()) t.rollback();
					res.setResponse("ERROR");
					res.setErrorMessage("Dear Customer your account unable to opened under branch code " + brcode
							+ " and account number " + brcode.trim() + "/" + prodCode + "/"
							+ Integer.valueOf(welcomekitAccNo));
					return res;
				}
			} else if (welcomeKitFlag.trim().equalsIgnoreCase("Y") && custNo.length() < 1) {
				String acc32 = String.format("%-8s", prodCode) + "00000000"
						+ String.format("%08d", Integer.valueOf(welcomekitAccNo.trim())) + "00000000";
				D009022Id id=new D009022Id();
				id.setLbrCode(Integer.valueOf(brcode.trim()));
				id.setPrdAcctId(acc32.trim());
				D009022 list=session.get(D009022.class, id);
				if (list!=null) {
					String custId = "" + list.getCustNo();
					if (custId.trim().equalsIgnoreCase("500000")) {
						custNo = DataUtils.getNextcustNo();
						d009011 = prepareCustomerMaster(add1, add2, add3, indOther, area, cityCode, custNo, name,
								pinCode, panNo, pan, emailId, brcode, mobNo, nameTitle);
						Query q = session.createQuery("UPDATE D009022 SET custNo ='" + custNo + "' WHERE id.lbrCode = '"
								+ brcode + "' AND id.prdAcctId = '" + acc32 + "'  ");
						q.executeUpdate();
						session.save(d009011);
						t.commit();
						res.setResponse("SUCCESS");
						res.setErrorMessage("Dear Customer your account has been successfully opened under branch code "
								+ brcode + " and account number " + brcode.trim() + "/" + prodCode + "/"
								+ Integer.valueOf(welcomekitAccNo));
						res.setCustNo(custNo.trim());
						res.setAccNo(acc32.trim());
						return res;
					}
					if (t.isActive()) t.rollback();
					res.setResponse("ERROR");
					res.setErrorMessage("Invalid account number.");
					return res;
				} else {
					if (t.isActive()) t.rollback();
					res.setResponse("ERROR");
					res.setErrorMessage("Dear Customer your account unable to opened under branch code " + brcode
							+ " and account number " + brcode.trim() + "/" + prodCode + "/"
							+ Integer.valueOf(welcomekitAccNo));
					return res;
				}
			}
			else if (null != req.getIsPigMeAcc()) {
				String accNo = "";
				String nextAccNo = DataUtils.getNextAccNo(Integer.valueOf(brcode.trim()), MSGConstants.ACCNO, 'O',
						prodCode.trim(), session);
				System.out.println("nextAccNo::>>" + nextAccNo);
				if (nextAccNo == null)
					accNo = CustomerMasterServiceImpl.getNextAccNo(prodCode.trim(), brcode.trim());
				else
					accNo = nextAccNo;
				if(req.getIsPigMeAcc().trim().equalsIgnoreCase("Y") && isNew.trim().equalsIgnoreCase("Y"))
					custNo = DataUtils.getNextcustNo();
				else
					custNo=req.getCustNo();
				if (req.getIsPigMeAcc().trim().equalsIgnoreCase("Y") && isNew.trim().equalsIgnoreCase("Y")) {
					if (req.getAgentAccNo() == null) {
						if (t.isActive()) t.rollback();
						res.setResponse(MSGConstants.ERROR);
						res.setErrorMessage(MSGConstants.INVALID_AGENT_ACC_NO);
						return res;
					}
					D009022 sourceAccount = DataUtils.getAccount("" + req.getAgentAccNo().trim(),session);
					if (null == sourceAccount) {
						if (t.isActive()) t.rollback();
						res.setResponse(MSGConstants.ERROR);
						res.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
						return res;
					}
					TransactionValidationResponse response = TransactionServiceImpl.validateAccount(sourceAccount, "10",
							"A");
					if (response.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
						if (t.isActive()) t.rollback();
						res.setResponse(MSGConstants.ERROR);
						res.setErrorMessage(response.getErrorMsg());
						return res;
					}
					d009011 = prepareCustomerMaster(add1, add2, add3, indOther, area, cityCode, custNo, name, pinCode,
							panNo, pan, emailId, brcode, mobNo, nameTitle);
					// ==========account object creation=====
					d009022 = prepareAccountMasterObject(accNo, brcode, accType, custNo, modeOfOperation, name,
							nameTitle,new Date("01-JAN-1900"));
					D010053 d010053 = prepareD010053Object(accNo, accType, brcode);

					D010153 d010153 = prepareD010153Obj(accNo, accType, brcode, custNo, name, nameTitle);
					D009022 d0090222 = DataUtils.getAccount(req.getAgentAccNo().trim());
					if (d0090222 == null) {
						if (t.isActive()) t.rollback();
						res.setResponse(MSGConstants.ERROR);
						res.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
						return res;
					}
					Criteria criteria2 = session.createCriteria(D020118.class);
					criteria2.add(Restrictions.eq("id.prdCd", "" + req.getProdCode()));
					criteria2.add(Restrictions.eq("days", Short.valueOf(req.getDays().trim())));
					criteria2.add(Restrictions.eq("months", Short.valueOf(req.getMonth().trim())));
					List<D020118> list = criteria2.list();
					if (list == null || list.isEmpty()) {
						if (t.isActive()) t.rollback();
						res.setResponse(MSGConstants.ERROR);
						res.setErrorMessage(MSGConstants.INT_RATE_NOT_FOUND);
						return res;
					}
					D047003 d047003 = prepareD047003Obj(accNo, brcode, req, list);
					d010054 = prepareD010054Obj(accNo, add1, add2, add3, area, brcode, cityCode, mobNo, pinCode);
					D009122 d009122 = prepareD009122Obj(accNo, accType, brcode,session);
					logger.error("d009122::>>"+d009122);
					if(d009122==null)
					{
						if (t.isActive()) t.rollback();
						res.setResponse(MSGConstants.ERROR);
						res.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
						return res;
					}
					D009012 d009012 = session.get(D009012.class, Integer.valueOf(custNo.trim()));
					if (d009012 == null)
						d009012 = prepareD009012Obj(add1, add2, add3, Integer.valueOf(brcode.trim()),
								Integer.valueOf(custNo.trim()), aadharNo, panNo, nameTitle.toUpperCase(), name);
					else {
						String query = "UPDATE D009012 SET NameTitle ='" + nameTitle + "',Longname = '" + name
								+ "',PanNo='" + panNo + "',AdharNo='" + aadharNo + "' WHERE CustNo =" + custNo.trim()
								+ "";
						int row = session.createQuery(query).executeUpdate();
						System.out.println("Row is updated" + row);
					}
					logger.error("d009012::>>"+d009012);
					if (isNew.trim().equalsIgnoreCase("Y")
							&& (req.getCustNo() == null || req.getCustNo().trim().length() < 1))
						session.save(d009011);
					session.save(d009012);
					session.save(d009022);
					session.save(d009122);
					if (modeOfOperation != null) {
						if (!modeOfOperation.trim().equalsIgnoreCase("1"))
							session.save(d010153);
					}
					session.save(d010053);
					session.save(d047003);
					session.save(d010054);
					d009011 = null;
					d009022 = null;
					d009122 = null;
					d001005 = null;
					t.commit();
					res.setResponse("SUCCESS");
					res.setErrorMessage("Dear Customer your account has been successfully opened under branch code "
							+ brcode + " and account number " + brcode.trim() + "/" + prodCode + "/"
							+ Integer.valueOf(accNo.substring(16, 24).trim()));
					res.setCustNo(custNo.trim());
					res.setAccNo(accNo.trim());
					return res;

				}
				if (req.getIsPigMeAcc().trim().equalsIgnoreCase("Y")
						&& (isNew.trim().equalsIgnoreCase("N") && req.getCustNo().trim().length() > 0)) {
					
					if (req.getAgentAccNo() == null) {
						if (t.isActive()) t.rollback();
						res.setResponse(MSGConstants.ERROR);
						res.setErrorMessage(MSGConstants.INVALID_AGENT_ACC_NO);
						return res;
					}
					D009022 sourceAccount = DataUtils.getAccount("" + req.getAgentAccNo().trim());
					if (null == sourceAccount) {
						if (t.isActive()) t.rollback();
						res.setResponse(MSGConstants.ERROR);
						res.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
						return res;
					}
					TransactionValidationResponse response = TransactionServiceImpl.validateAccount(sourceAccount, "10",
							"A");
					if (response.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
						if (t.isActive()) t.rollback();
						res.setResponse(MSGConstants.ERROR);
						res.setErrorMessage(response.getErrorMsg());
						return res;
					}
					d009011 = prepareCustomerMaster(add1, add2, add3, indOther, area, cityCode, custNo, name, pinCode,
							panNo, pan, emailId, brcode, mobNo, nameTitle);
					
					
//					As disccused with apurva on 07042018
					D009012 custmast= session.get(D009012.class, Integer.valueOf(custNo));
					
					d009022 = prepareAccountMasterObject(accNo, brcode, accType, custNo, modeOfOperation, name,
							nameTitle,custmast!=null?custmast.getDob():new Date("01-JAN-1900"));
					System.out.println("0");
					D009122 d009122 = prepareD009122Obj(accNo, accType, brcode,session);
					System.out.println("1");
					D010053 d010053 = prepareD010053Object(accNo, accType, brcode);
					System.out.println("2");
					D010153 d010153 = prepareD010153Obj(accNo, accType, brcode, custNo, name, nameTitle);
					System.out.println("3");
					D009022 d0090222 = DataUtils.getAccount(req.getAgentAccNo().trim());
					System.out.println("4");
					if (d0090222 == null) {
						if (t.isActive()) t.rollback();
						res.setResponse(MSGConstants.ERROR);
						res.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
						return res;
					}
					System.out.println("5");
					d010054 = prepareD010054Obj(accNo, add1, add2, add3, area, brcode, cityCode, mobNo, pinCode);
					Criteria criteria2 = session.createCriteria(D020118.class);
					criteria2.add(Restrictions.eq("days", Short.valueOf(req.getDays().trim())));
					criteria2.add(Restrictions.eq("months", Short.valueOf(req.getMonth().trim())));
					criteria2.add(Restrictions.eq("id.prdCd", "" + req.getProdCode()));

					List<D020118> list = criteria2.list();
					if (list == null || list.isEmpty()) {
						if (t.isActive()) t.rollback();
						res.setResponse(MSGConstants.ERROR);
						res.setErrorMessage(MSGConstants.INT_RATE_NOT_FOUND);
						return res;
					}
					System.out.println("3");
					D047003 d047003 = prepareD047003Obj(accNo, brcode, req, list);
					System.out.println("4");
					D009012 d009012 = session.get(D009012.class, Integer.valueOf(custNo.trim()));
					System.out.println("5");
					if (d009012 == null) {
						d009012 = prepareD009012Obj(add1, add2, add3, Integer.valueOf(brcode.trim()),
								Integer.valueOf(custNo.trim()), aadharNo, panNo, nameTitle.toUpperCase(), name);
						session.save(d009012);
					} else {
						/*String query = "UPDATE D009012 SET NameTitle ='" + nameTitle + "',Longname = '" + name
								+ "',PanNo='" + panNo + "',AdharNo='" + aadharNo + "' WHERE CustNo =" + custNo.trim()
								+ "";
						session.createQuery(query).executeUpdate();*/
					}
					if (isNew.trim().equalsIgnoreCase("Y"))
						session.save(d009011);
					session.save(d009022);
					// session.saveOrUpdate(d009012);
					session.save(d009122);
					if (modeOfOperation != null) {
						if (!modeOfOperation.trim().equalsIgnoreCase("1"))
							session.save(d010153);
					}
					session.save(d010053);
					session.save(d010054);
					session.save(d047003);
					d009011 = null;
					d009022 = null;
					d009122 = null;
					d001005 = null;
					t.commit();
					res.setResponse("SUCCESS");
					res.setErrorMessage("Dear Customer your account has been successfully opened under branch code "
							+ brcode + " and account number " + brcode.trim() + "/" + prodCode + "/"
							+ Integer.valueOf(accNo.substring(16, 24).trim()));//
					res.setCustNo(custNo.trim());
					res.setAccNo(accNo.trim());
					return res;
				}
			}
			if (isNew.trim().equalsIgnoreCase("Y") && welcomeKitFlag.trim().equalsIgnoreCase("N")) {
				custNo = DataUtils.getNextcustNo();
				d009011 = prepareCustomerMaster(add1, add2, add3, indOther, area, cityCode, custNo, name, pinCode,
						panNo, pan, emailId, brcode, mobNo, nameTitle);
			}
			if (accType.trim().equalsIgnoreCase("") || accType.trim().equalsIgnoreCase("0"))
				accType = "1";
			String accNo = "";
			String nextAccNo = DataUtils.getNextAccNo(Integer.valueOf(brcode.trim()), MSGConstants.ACCNO, 'O',
					prodCode.trim(), session);
			System.out.println("nextAccNo::>>" + nextAccNo);
			if (nextAccNo == null)
				accNo = CustomerMasterServiceImpl.getNextAccNo(prodCode.trim(), brcode.trim());
			else
				accNo = nextAccNo;
			d009022 = prepareAccountMasterObject(accNo, brcode, accType, custNo, modeOfOperation, name, nameTitle,new Date("01-JAN-1900"));

			D009122 d009122 = prepareD009122Obj(accNo, accType, brcode,session);
			d010054 = prepareD010054Obj(accNo, add1, add2, add3, area, brcode, cityCode, mobNo, pinCode);

			D010153 d010153 = prepareD010153Obj(accNo, accType, brcode, custNo, name, nameTitle);

			D009012 d009012 = session.get(D009012.class, Integer.valueOf(custNo.trim()));
			if (d009012 == null)
				d009012 = prepareD009012Obj(add1, add2, add3, Integer.valueOf(brcode.trim()),
						Integer.valueOf(custNo.trim()), aadharNo, panNo, nameTitle.toUpperCase(), name);
			else {
				String query = "UPDATE D009012 SET NameTitle ='" + nameTitle + "',Longname = '" + name + "',PanNo='"
						+ panNo + "',AdharNo='" + aadharNo + "' WHERE CustNo =" + custNo.trim() + "";
				int row = session.createQuery(query).executeUpdate();
				System.out.println("Row is updated" + row);
			}
			D010053 d010053 = prepareD010053Object(accNo, accType, brcode);
			if (isNew.trim().equalsIgnoreCase("Y")) {
				session.save(d009011);
				session.saveOrUpdate(d010054);
				session.saveOrUpdate(d009012);
				session.save(d010053);
			}
			if (modeOfOperation != null) {
				if (!modeOfOperation.trim().equalsIgnoreCase("1"))
					session.save(d010153);
			}
			
			if (isNew.trim().equalsIgnoreCase("N"))
				session.saveOrUpdate(d010054);
			session.save(d009022);
			session.saveOrUpdate(d009122);
			d009011 = null;
			d009022 = null;
			d009122 = null;
			d001005 = null;
			t.commit();
			res.setResponse("SUCCESS");
			res.setErrorMessage("Dear Customer your account has been successfully opened under branch code " + brcode
					+ " and account number " + brcode.trim() + "/" + prodCode + "/"
					+ Integer.valueOf(accNo.substring(16, 24).trim()));//
			res.setCustNo(custNo.trim());
			res.setAccNo(accNo.trim());
			return res;
		} catch (Exception e) {
			// TODO: handle exception
			if (t.isActive()) t.rollback();
			e.printStackTrace();
			logger.error("ERROR:", e);
			res.setResponse("ERROR");
			res.setErrorMessage("Dear customer, your account is not able to open under branch code " + brcode
					+ " due to technical reason.");
			return res;
		} finally {
			session.close();
			session = null;
			t = null;
		}
	}
	@SuppressWarnings({ "unused", "deprecation", "unchecked" })
	public static CustomerDetails validateAccNo(String accNo) {
		CustomerDetails customerDetails = new CustomerDetails();
		System.out.println("Recieved AccNo::>>>" + accNo);
		D009022 d009022 = getAccount(accNo);
		if (null == d009022) {
			customerDetails.setResponse("ERROR");
			customerDetails.setErrorMsg("No Records Found.");
			return customerDetails;
		}
		String custNo = String.valueOf(d009022.getCustNo());
		System.out.println("custNo::>>" + custNo);
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		Criteria criteria = session.createCriteria(D350078.class);
		criteria.add(Restrictions.eq("id.custNo", custNo));
		List<D350078> list = criteria.list();
		session.close();
		session = null;
		t = null;
		if (list.size() > 0) {
			customerDetails.setResponse("SUCCESS");
			customerDetails.setErrorMsg("Account is valid.");
			customerDetails.setMobileNo(list.get(0).getId().getMobileNo());
			return customerDetails;
		}
		customerDetails.setResponse("ERROR");
		customerDetails.setErrorMsg("No Records Found.");
		return customerDetails;
	}

	public static D009022 getAccount(String prdAcctId) {
		int brCode = Integer.parseInt(prdAcctId.substring(0, 3));
		String acct32 = DataUtils.get32DigitAcctNo(prdAcctId);
		if (acct32 == null)
			return null;
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		D009022Id id = new D009022Id();
		id.setLbrCode(brCode);
		id.setPrdAcctId(acct32);
		D009022 bal = session.get(D009022.class, id);
		tx.commit();
		session.close();
		session = null;
		return bal;
	}

	@SuppressWarnings("deprecation")
	public static String get32DigitAcctNo(String acctno15digit) {
		String pCode = acctno15digit.substring(3, 7);
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		D001002 code = (D001002) session.createCriteria(D001002.class).add(Restrictions.eq("id.codeType", 11080))
				.add(Restrictions.eq("codeDesc", pCode)).uniqueResult();
		tx.commit();
		session.close();
		session = null;
		if (code == null)
			return null;
		else
			System.out.println("Product Code : " + code.getId().getCode());
		String productCode = code.getId().getCode();
		String accno = acctno15digit.substring(7);
		String acc32 = String.format("%-8s", productCode.substring(0, 8)) + "00000000" + accno + "00000000";
		System.out.println("Acct32 : " + acc32);
		return acc32;
	}

	public static void main(String[] args) {
		// AccountMasterServiceImpl.insertValues("3",
		// CustomerMasterServiceImpl.getNextAccNo("SB", "3"), "2", "1", "Mr",
		// "Amar Wasudeorao Raut", "529 palash laine", "near ajay londry",
		// "gadge nagar Amravati", "1", "BCJPR9007C", "1", "MUM", "400703",
		// "8983389108","amar.raut12@gmail.com","","");
	}

	public static D009012 prepareD009012Obj(String add1, String add2, String add3, int brcode, int custNo,
			String aadharNo, String panNo, String nameTiitle, String name) {
		System.out.println("aadharNo::>>" + aadharNo);
		D009012 d009012 = new D009012();
		d009012.setAdharNo(aadharNo);
		d009012.setAmlrating(Short.valueOf("0"));
		d009012.setAnnualIncome(Double.valueOf("0"));
		d009012.setAssetCode(Short.valueOf("0"));
		d009012.setAtmno("");
		d009012.setBank1(" ");
		d009012.setBank2(" ");
		d009012.setBldGrp(" ");
		d009012.setBldGrp(" ");
		d009012.setBr1(" ");
		d009012.setBr2(" ");
		d009012.setCasteCode(" ");
		d009012.setCreditCard(" ");
		d009012.setCreditCards1(" ");
		d009012.setCreditCards2(" ");
		d009012.setCusIdbtrAddCb(0);
		d009012.setCusIdbtrAddCd(new Date());
		d009012.setCusIdbtrAddCk(0);
		d009012.setCusIdbtrAddCs(Short.valueOf("0"));
		d009012.setCusIdbtrAddCt(new Date());
		d009012.setCusIdbtrAddMb(0);
		d009012.setCusIdbtrAddMd(new Date());
		d009012.setCusIdbtrLupdMk(0);
		d009012.setCusIdbtrLupdMs(Short.valueOf("0"));
		d009012.setCusIdbtrAddMt(new Date());
		d009012.setCusIdbtrAuthDone(Byte.valueOf("1"));
		d009012.setCusIdbtrAuthNeeded(Byte.valueOf("0"));
		d009012.setCusIdbtrLhisTrnNo(0);
		d009012.setCusIdbtrLupdCb(0);

		d009012.setVisaValUpTo(new Date());
		d009012.setValidUpto(new Date());
		d009012.setValid2(new Date());
		d009012.setValid1(new Date());
		d009012.setStation(Short.valueOf("0"));
		d009012.setSexCode(" ");
		d009012.setRetiredDate(new Date());
		d009012.setResiStatus(Byte.valueOf("0"));
		d009012.setResidence(Short.valueOf("0"));
		d009012.setResdYn('N');
		d009012.setRelOffCode(" ");
		d009012.setReligionCd(Short.valueOf("0"));
		d009012.setRelationType(" ");
		d009012.setRelationName(" ");
		d009012.setRecovThru(Byte.valueOf("0"));
		d009012.setPwdAssigned(' ');
		d009012.setProfCd(Short.valueOf("0"));
		d009012.setPhBanking('N');
		d009012.setPassPortNo(" ");
		d009012.setPassPort(" ");
		d009012.setPanRegDate(new Date());
		d009012.setPanNo(panNo);
		d009012.setOtherBrAcctCode2(" ");
		d009012.setOtherBrAcctCode1(" ");
		d009012.setOphone(" ");
		d009012.setNoOfService(Short.valueOf("0"));
		d009012.setNoOfChildren(Byte.valueOf("0"));
		d009012.setNameTitle(nameTiitle);
		d009012.setMemberType(Byte.valueOf("0"));
		d009012.setMemberNo(0);
		d009012.setMarriageDt(new Date());
		d009012.setMaritalStatus(Short.valueOf("0"));
		d009012.setLongname(name.toUpperCase());
		d009012.setKycyn('N');
		d009012.setIsuPlace(" ");
		d009012.setIsuAuthority(Short.valueOf("0"));
		d009012.setIntroCustno(0);
		d009012.setIntrCustNo(0);
		d009012.setFreqSt(" ");
		d009012.setEmpNameTitle(" ");
		d009012.setEmployerCd(Short.valueOf("0"));
		d009012.setEmployeeNo(" ");
		d009012.setEmployedWith(Short.valueOf("0"));
		d009012.setEmpLongName(" ");
		d009012.setEmpAddr3(" ");
		d009012.setEmpAddr2(" ");
		d009012.setEmpAddr1(" ");
		d009012.setEducationalQual(" ");
		d009012.setDtFromCust(new Date());
		d009012.setDocumentsDet(" ");
		d009012.setDocuments(Short.valueOf("0"));
		d009012.setDob(new Date());
		d009012.setDesMail(Short.valueOf("0"));
		d009012.setDesignation(" ");
		d009012.setDecision(Short.valueOf("0"));
		d009012.setDebitCard("");
		d009012.setDeathDate(new Date());
		d009012.setCusType(Short.valueOf("0"));
		d009012.setCustNo(custNo);
		d009012.setCusIdbtrUpdtChkId(Short.valueOf("0"));
		d009012.setCusIdbtrTauthDone(Short.valueOf("1"));
		d009012.setCusIdbtrRecStat(Byte.valueOf("0"));
		d009012.setCusIdbtrLupdMt(new Date());
		d009012.setCusIdbtrLupdMs(Short.valueOf("0"));
		d009012.setCusIdbtrLupdMk(0);
		d009012.setCusIdbtrLupdMd(new Date());
		d009012.setCusIdbtrLupdMb(0);
		d009012.setCusIdbtrLupdCt(new Date());
		d009012.setCusIdbtrLupdMb(0);
		d009012.setCusIdbtrLupdCs(Short.valueOf("0"));
		d009012.setCusIdbtrLupdCd(new Date());
		d009012.setCusIdbtrLupdCb(0);
		return d009012;
	}

	public static D009012 prepareD009012ObjNew(String add1, String add2, String add3, int brcode, int custNo,
			String aadharNo, String panNo, String nameTiitle, String name, D009012 d009012Obj) {
		System.out.println("aadharNo::>>" + aadharNo);

		D009012 d009012 = new D009012();
		d009012.setAdharNo(aadharNo);
		d009012.setAmlrating(d009012Obj.getAmlrating());
		d009012.setAnnualIncome(d009012Obj.getAnnualIncome());
		d009012.setAssetCode(d009012Obj.getAssetCode());
		d009012.setAtmno(d009012Obj.getAtmno());
		d009012.setBank1(d009012Obj.getBank1());
		d009012.setBank2(d009012Obj.getBank2());
		d009012.setBldGrp(d009012Obj.getBldGrp());
		d009012.setBr1(d009012Obj.getBr1());
		d009012.setBr2(d009012Obj.getBr2());
		d009012.setCasteCode(d009012Obj.getCasteCode());
		d009012.setCreditCard(d009012Obj.getCreditCard());
		d009012.setCreditCards1(d009012Obj.getCreditCards1());
		d009012.setCreditCards2(d009012Obj.getCreditCards2());
		d009012.setCusIdbtrAddCb(d009012Obj.getCusIdbtrAddCb());
		d009012.setCusIdbtrAddCd(d009012Obj.getCusIdbtrAddCd());
		d009012.setCusIdbtrAddCk(d009012Obj.getCusIdbtrAddCk());
		d009012.setCusIdbtrAddCs(d009012Obj.getCusIdbtrAddCs());
		d009012.setCusIdbtrAddCt(d009012Obj.getCusIdbtrAddCt());
		d009012.setCusIdbtrAddMb(d009012Obj.getCusIdbtrAddMb());
		d009012.setCusIdbtrAddMd(d009012Obj.getCusIdbtrAddMd());
		d009012.setCusIdbtrLupdMk(d009012Obj.getCusIdbtrLupdMk());
		d009012.setCusIdbtrLupdMs(d009012Obj.getCusIdbtrLupdMs());
		d009012.setCusIdbtrAddMt(d009012Obj.getCusIdbtrAddMt());
		d009012.setCusIdbtrAuthDone(Byte.valueOf("1"));
		d009012.setCusIdbtrAuthNeeded(Byte.valueOf("0"));
		d009012.setCusIdbtrLhisTrnNo(0);
		d009012.setCusIdbtrLupdCb(0);

		d009012.setVisaValUpTo(d009012Obj.getVisaValUpTo());
		d009012.setValidUpto(d009012Obj.getValidUpto());
		d009012.setValid2(d009012Obj.getValid2());
		d009012.setValid1(d009012Obj.getValid1());
		d009012.setStation(d009012Obj.getStation());
		d009012.setSexCode(d009012Obj.getSexCode());
		d009012.setRetiredDate(d009012Obj.getRetiredDate());
		d009012.setResiStatus(d009012Obj.getResiStatus());
		d009012.setResidence(d009012Obj.getResidence());
		d009012.setResdYn(d009012Obj.getResdYn());
		d009012.setRelOffCode(d009012Obj.getRelOffCode());
		d009012.setReligionCd(d009012Obj.getReligionCd());
		d009012.setRelationType(d009012Obj.getRelationType());
		d009012.setRelationName(d009012Obj.getRelationName());
		d009012.setRecovThru(d009012Obj.getRecovThru());
		d009012.setPwdAssigned(d009012Obj.getPwdAssigned());
		d009012.setProfCd(d009012Obj.getProfCd());
		d009012.setPhBanking(d009012Obj.getPhBanking());
		d009012.setPassPortNo(d009012Obj.getPassPortNo());
		d009012.setPassPort(d009012Obj.getPassPort());
		d009012.setPanRegDate(d009012Obj.getPanRegDate());
		d009012.setPanNo(panNo);
		d009012.setOtherBrAcctCode2(d009012Obj.getOtherBrAcctCode2());
		d009012.setOtherBrAcctCode1(d009012Obj.getOtherBrAcctCode1());
		d009012.setOphone(d009012Obj.getOphone());
		d009012.setNoOfService(d009012Obj.getNoOfService());
		d009012.setNoOfChildren(d009012Obj.getNoOfChildren());
		d009012.setNameTitle(nameTiitle);
		d009012.setMemberType(d009012Obj.getMemberType());
		d009012.setMemberNo(d009012Obj.getMemberNo());
		d009012.setMarriageDt(d009012Obj.getMarriageDt());
		d009012.setMaritalStatus(d009012Obj.getMaritalStatus());
		d009012.setLongname(name.toUpperCase());
		d009012.setKycyn(d009012Obj.getKycyn());
		d009012.setIsuPlace(d009012Obj.getIsuPlace());
		d009012.setIsuAuthority(d009012Obj.getIsuAuthority());
		d009012.setIntroCustno(d009012Obj.getIntroCustno());
		d009012.setIntrCustNo(d009012Obj.getIntrCustNo());
		d009012.setFreqSt(d009012Obj.getFreqSt());
		d009012.setEmpNameTitle(d009012Obj.getEmpNameTitle());
		d009012.setEmployerCd(d009012Obj.getEmployerCd());
		d009012.setEmployeeNo(d009012Obj.getEmployeeNo());
		d009012.setEmployedWith(d009012Obj.getEmployedWith());
		d009012.setEmpLongName(d009012Obj.getEmpLongName());
		d009012.setEmpAddr3(d009012Obj.getEmpAddr3());
		d009012.setEmpAddr2(d009012Obj.getEmpAddr2());
		d009012.setEmpAddr1(d009012Obj.getEmpAddr1());
		d009012.setEducationalQual(d009012Obj.getEducationalQual());
		d009012.setDtFromCust(d009012Obj.getDtFromCust());
		d009012.setDocumentsDet(d009012Obj.getDocumentsDet());
		d009012.setDocuments(d009012Obj.getDocuments());
		d009012.setDob(d009012Obj.getDob());
		d009012.setDesMail(d009012Obj.getDesMail());
		d009012.setDesignation(d009012Obj.getDesignation());
		d009012.setDecision(d009012Obj.getDecision());
		d009012.setDebitCard(d009012Obj.getDebitCard());
		d009012.setDeathDate(d009012Obj.getDeathDate());
		d009012.setCusType(d009012Obj.getCusType());
		// d009012.setCustNo(custNo);
		d009012.setCusIdbtrUpdtChkId(d009012Obj.getCusIdbtrUpdtChkId());
		d009012.setCusIdbtrTauthDone(d009012Obj.getCusIdbtrTauthDone());
		d009012.setCusIdbtrRecStat(d009012Obj.getCusIdbtrRecStat());
		d009012.setCusIdbtrLupdMt(d009012Obj.getCusIdbtrLupdMt());
		d009012.setCusIdbtrLupdMs(d009012Obj.getCusIdbtrLupdMs());
		d009012.setCusIdbtrLupdMk(d009012Obj.getCusIdbtrLupdMk());
		d009012.setCusIdbtrLupdMd(d009012Obj.getCusIdbtrLupdMd());
		d009012.setCusIdbtrLupdMb(d009012Obj.getCusIdbtrLupdMb());
		d009012.setCusIdbtrLupdCt(d009012Obj.getCusIdbtrLupdCt());
		d009012.setCusIdbtrLupdMb(d009012Obj.getCusIdbtrLupdMb());
		d009012.setCusIdbtrLupdCs(d009012Obj.getCusIdbtrLupdCs());
		d009012.setCusIdbtrLupdCd(d009012Obj.getCusIdbtrLupdCd());
		d009012.setCusIdbtrLupdCb(d009012Obj.getCusIdbtrLupdCb());
		return d009012;
	}

	public static D010153 getD010153Obj(String brcode, String accType, String accNo, String custNo, String name,
			String nameTitle) {
		D010153 d010153 = new D010153();
		D010153Id d010053Id = new D010153Id();
		d010053Id.setLbrCode(Integer.valueOf(brcode.trim()));
		d010053Id.setNameType(Byte.valueOf(accType.trim()));
		d010053Id.setPrdAcctId(accNo);
		d010053Id.setSrNo(Short.valueOf("1"));
		d010153.setId(d010053Id);
		d010153.setCusNo(Integer.valueOf(custNo.trim()));
		d010153.setMlDbtrBijIdx(Short.valueOf("0"));
		d010153.setMlDbtrUaeIdx(Short.valueOf("0"));
		d010153.setName(name.trim());
		d010153.setNameTitle(nameTitle);
		return d010153;
	}

	public static D009011 prepareCustomerMaster(String add1, String add2, String add3, String indOther, String area,
			String cityCode, String custNo, String name, String pinCode, String panNo, String pan, String emailId,
			String brcode, String mobNo, String nameTitle) {
		try {
			D009011 d009011 = new D009011();
			d009011.setAdd1(add1);
			d009011.setAdd2(add2);
			d009011.setAdd3(add3);
			d009011.setArea(Integer.valueOf(area));
			d009011.setBsrCode(" ");
			d009011.setCityCd(cityCode);
			d009011.setCounCd("IN");
			d009011.setCustAccessCat(Short.valueOf("0"));
			d009011.setCustCategory(Byte.valueOf("0"));
			d009011.setCustId(custNo);
			d009011.setCustIdSrNo(Short.valueOf("0"));
			d009011.setCustNo(Integer.valueOf(custNo));
			d009011.setDbtrAddCb(0);
			d009011.setDbtrAddCd(new Date());
			d009011.setDbtrAddCk(0);
			d009011.setDbtrAddCs(Short.valueOf("0"));
			d009011.setDbtrAddCt(new Date());
			d009011.setDbtrAddMb(0);
			d009011.setDbtrAddMd(new Date());
			d009011.setDbtrAddMk(0);
			d009011.setDbtrAddMt(new Date());
			d009011.setDbtrAuthDone(Byte.valueOf("1"));
			d009011.setDbtrAuthNeeded(Byte.valueOf("0"));
			d009011.setDbtrLhisTrnNo(0);
			d009011.setDbtrLupdCb(0);
			d009011.setDbtrLupdCd(new Date());
			d009011.setDbtrLupdCk(0);
			d009011.setDbtrLupdCs(Short.valueOf("0"));
			d009011.setDbtrLupdCt(new Date());
			d009011.setDbtrLupdMb(0);
			d009011.setDbtrLupdMd(new Date());
			d009011.setDbtrLupdMk(0);
			d009011.setDbtrLupdMs(Short.valueOf("0"));
			d009011.setDbtrLupdMt(new Date());
			d009011.setDbtrRecStat(Byte.valueOf("0"));
			d009011.setDbtrTauthDone(Short.valueOf("1"));
			d009011.setTdsYn('N');
			d009011.setTdsReasonCd(Short.valueOf("0"));
			d009011.setTdsProvision(0);
			d009011.setTdsProjected(0);
			d009011.setTdsPercentage(0);
			d009011.setTdsFrm15subDt(new Date());
			d009011.setSplInstr2(" ");
			d009011.setSplInstr1(" ");
			if(name.length()>3)
				d009011.setShortName(name.substring(0, 3));
			else
				d009011.setShortName(name);
			d009011.setRelOffCode(Short.valueOf("0"));
			d009011.setRelOff(" ");
			d009011.setRating(Byte.valueOf("0"));
			d009011.setPinCode(pinCode);
			d009011.setPhone(" ");
			d009011.setPanNoDesc(panNo);
			d009011.setPanNo(Byte.valueOf(pan));
			d009011.setPagerNo(mobNo);
			d009011.setNameTitle(nameTitle);
			d009011.setMainCustNo(Integer.valueOf(custNo));
			d009011.setLongname(name.toUpperCase());
			d009011.setLbrCode(Integer.valueOf(brcode));
			d009011.setIntroducerCon('N');
			d009011.setIntProvision(0);
			d009011.setIntProjected(0);
			d009011.setIndOth(Byte.valueOf(String.valueOf(Integer.valueOf(indOther))));
			d009011.setFrzReasonCd(Byte.valueOf("0"));
			d009011.setFreezeType(Byte.valueOf("1"));
			d009011.setFax(" ");
			d009011.setEmailId(emailId);
			return d009011;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("ERROR:", e);
			return null;
		}
	}

	public static D009022 prepareAccountMasterObject(String accNo, String brcode, String accType, String custNo,
			String modeOfOperation, String name, String nameTitle,Date dob) {
		try {
			D009022 d009022 = new D009022();
			D009022Id d009022Id = new D009022Id();
			d009022Id.setLbrCode(Integer.valueOf(brcode));
			d009022Id.setPrdAcctId(accNo);
			d009022.setId(d009022Id);
			d009022.setAcctStat(Byte.valueOf("2"));// new
			if (accType.trim().equalsIgnoreCase("0") || accType.trim().equalsIgnoreCase(""))
				accType = "1";//
			System.out.println("accType::>>>" + accType);

			d009022.setAcctType(Byte.valueOf("" + Integer.valueOf(accType)));
			d009022.setActClrBalFcy(0);
			d009022.setActTotBalFcy(0);
			d009022.setActTotBalLcy(0);
			d009022.setChgHoldAmtFcy(0);
			d009022.setChqBookYn('N');
			d009022.setClosedUser("");
			d009022.setCurCd("INR");
			d009022.setCustAccessCat(Short.valueOf("0"));
			d009022.setCustNo(Integer.valueOf(custNo));
			d009022.setDateClosed(new Date("01-JAN-1900"));
			d009022.setDateOpen(new Date());
			d009022.setDbtrAddCb(0);
			d009022.setDbtrAddCd(new Date());
			d009022.setDbtrAddCk(0);
			d009022.setDbtrAddCs(Short.valueOf("0"));
			d009022.setDbtrAddCt(new Date());
			d009022.setDbtrAddMb(0);
			d009022.setDbtrAddMd(new Date());
			d009022.setDbtrAddMk(0);
			d009022.setDbtrAddMs(Short.valueOf("0"));
			d009022.setDbtrAddMt(new Date());
			d009022.setDbtrAuthDone(Byte.valueOf("1"));
			d009022.setDbtrAuthNeeded(Byte.valueOf("0"));
			d009022.setDbtrUpdtChkId(Short.valueOf("0"));
			d009022.setDbtrLhisTrnNo(0);
			d009022.setDbtrLupdCb(0);
			d009022.setDbtrLupdCd(new Date());
			d009022.setDbtrLupdCk(0);
			d009022.setDbtrLupdCs(Short.valueOf("0"));
			d009022.setDbtrLupdCt(new Date());
			d009022.setDbtrLupdMb(0);
			d009022.setDbtrLupdMd(new Date());
			d009022.setDbtrLupdMk(0);
			d009022.setFreezeType(Byte.valueOf("2"));
			d009022.setDbtrLupdMs(Short.valueOf("0"));
			d009022.setDbtrLupdMt(new Date());
			d009022.setDbtrRecStat(Byte.valueOf("0"));
			d009022.setDbtrTauthDone(Short.valueOf("1"));
			d009022.setDbtrUpdtChkId(Short.valueOf("0"));
			d009022.setDocFileNo("0");
			d009022.setDpYn('N');
			d009022.setFcyScheme(" ");
			d009022.setFlexiBalance(0);
			d009022.setFlexiLienBal(0);
			d009022.setUnClrEffFcy(0);
			d009022.setTotalLienFcy(0);
			d009022.setShdTotBalFcy(0);
			d009022.setSplInstr2(" ");
			d009022.setSplInstr1(" ");
			d009022.setShdTotBalFcy(0);
			d009022.setShdClrBalFcy(0);
			d009022.setPlrLinkYn('N');
			d009022.setPenalOffSet(0);
			d009022.setOpenUser("MPOS");
			d009022.setNameTitle(nameTitle.trim().toUpperCase());// name
			d009022.setMstrAuthMask(0);
			d009022.setModeOprn(Byte.valueOf(modeOfOperation));// mode
			// of
			// operation
			d009022.setMinorType(Byte.valueOf("0"));// major minor
			d009022.setLongName(name.trim().toUpperCase());// name
			d009022.setMstrAuthMask(0);
//			d009022.setDtOfBirth(new Date());
			d009022.setDtOfBirth(dob);
			d009022.setLastCrDate(new Date());
			d009022.setLastDrDate(new Date());
			d009022.setLastCustCrDate(new Date());
			d009022.setLastCustDrDate(new Date());
			d009022.setLastIntAppDate(new Date());
			return d009022;
		} catch (Exception e) {
			// TODO: handl exception
			e.printStackTrace();
			return null;
		}
	}

	public static D010053 prepareD010053Object(String accNo, String accType, String brcode) {
		try {
			D010053 d010053 = new D010053();
			D010053Id id = new D010053Id();
			id.setLbrCode(Integer.valueOf(brcode.trim()));
			id.setPrdAcctId(accNo);
			id.setNameType(Byte.valueOf(accType.trim()));

			d010053.setDbtrAddCb(0);
			d010053.setDbtrAddCd(new Date());
			d010053.setDbtrAddCk(0);
			d010053.setDbtrAddCs(Short.valueOf("0"));
			d010053.setDbtrAddCt(new Date());
			d010053.setDbtrAddMb(0);
			d010053.setDbtrAddMd(new Date());
			d010053.setDbtrAddMk(0);
			d010053.setDbtrAddMs(Short.valueOf("0"));
			d010053.setDbtrAddMt(new Date());
			d010053.setDbtrAuthDone(Byte.valueOf("1"));
			d010053.setDbtrAuthNeeded(Byte.valueOf("0"));
			d010053.setDbtrLhisTrnNo(0);
			d010053.setDbtrLupdCb(0);
			d010053.setDbtrLupdCd(new Date());
			d010053.setDbtrLupdCk(0);
			d010053.setDbtrLupdCs(Short.valueOf("0"));
			d010053.setDbtrLupdCt(new Date());
			d010053.setDbtrLupdMb(0);
			d010053.setDbtrLupdMd(new Date());
			d010053.setDbtrLupdMk(0);
			d010053.setDbtrLupdMs(Short.valueOf("0"));
			d010053.setDbtrLupdMt(new Date());
			d010053.setDbtrRecStat(Byte.valueOf("0"));
			d010053.setDbtrTauthDone(Short.valueOf("1"));
			d010053.setDbtrUpdtChkId(Short.valueOf("0"));
			d010053.setId(id);
			return d010053;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("ERROR:", e);
			return null;
		}
	}

	public static D010153 prepareD010153Obj(String accNo, String accType, String brcode, String custNo, String name,
			String nameTitle) {
		try {
			D010153 d010153 = new D010153();
			D010153Id d010053Id = new D010153Id();
			d010053Id.setLbrCode(Integer.valueOf(brcode.trim()));
			d010053Id.setNameType(Byte.valueOf(accType.trim()));
			d010053Id.setPrdAcctId(accNo);
			d010053Id.setSrNo(Short.valueOf("1"));
			d010153.setId(d010053Id);

			d010153.setCusNo(Integer.valueOf(custNo.trim()));
			d010153.setMlDbtrBijIdx(Short.valueOf("0"));
			d010153.setMlDbtrUaeIdx(Short.valueOf("0"));
			d010153.setName(name.trim());
			d010153.setNameTitle(nameTitle);
			return d010153;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("ERROR", e);
			return null;
		}
	}

	public static D047003 prepareD047003Obj(String accNo, String brcode, AccountOpenRequest req, List<D020118> list) {
		try {
			D047003 d047003 = new D047003();
			D047003Id id2 = new D047003Id();
			id2.setCustPrdAcctId(accNo);
			id2.setLbrCode(Integer.valueOf(brcode));
			d047003.setAcctStat(Byte.valueOf("2"));
			logger.error("15 Digit AcctId="+ req.getAgentAccNo().trim());
			d047003.setAgtPrdAcctId(DataUtils.get32DigitAcctNo(req.getAgentAccNo().trim()));
			logger.error("15 Digit AcctId 2="+ req.getAgentAccNo().trim());
			d047003.setDbtrAddCb(0);
			d047003.setDays(Short.valueOf(req.getDays() + ""));
			d047003.setDbtrAddCb(0);
			d047003.setDbtrAddCd(new Date());
			d047003.setDbtrAddCk(0);
			d047003.setDbtrAddCs(Short.valueOf("0"));
			d047003.setDbtrLupdCt(new Date());
			d047003.setDbtrAddMb(0);
			d047003.setDbtrAddMd(new Date());
			d047003.setDbtrAddMk(0);
			d047003.setDbtrAddMs(Short.valueOf("0"));
			d047003.setDbtrAddMt(new Date());
			d047003.setDbtrAuthDone(Byte.valueOf("1"));
			d047003.setDbtrAuthNeeded(Byte.valueOf("0"));
			d047003.setDbtrLhisTrnNo(0);
			d047003.setDbtrLupdCb(0);
			d047003.setDbtrLupdCd(new Date());
			d047003.setDbtrLupdCk(0);
			d047003.setDbtrLupdCs(Short.valueOf("0"));
			d047003.setDbtrLupdCt(new Date());
			d047003.setDbtrLupdCs(Short.valueOf("0"));
			d047003.setDbtrLupdCt(new Date());
			d047003.setDbtrLupdMb(0);
			d047003.setDbtrLupdMd(new Date());
			d047003.setDbtrLupdMk(0);
			d047003.setDbtrLupdMs(Short.valueOf("0"));
			d047003.setDbtrLupdMt(new Date());
			d047003.setDbtrRecStat(Byte.valueOf("0"));
			d047003.setDbtrTauthDone(Short.valueOf("1"));
			d047003.setDbtrUpdtChkId(Short.valueOf("0"));
			d047003.setDelayMonths(Short.valueOf(req.getMonth().trim()));
			d047003.setDepositAmt(Double.valueOf(req.getDepositAmount().trim()));
			d047003.setId(id2);
			d047003.setIntRate(list.get(0).getIntRate());
			d047003.setMatAmount(0);
			d047003.setMatDate(DateUtil.addDays(365, new Date()));
			d047003.setMatTrfrDate(new Date());
			d047003.setMonths(Short.valueOf(req.getMonth()));
			d047003.setOpenDate(new Date());
			d047003.setPopCode(Short.valueOf("0"));
			d047003.setRemarks(" ");
			d047003.setSrNo(Byte.valueOf("1"));
			
			d047003.setTransferredAmt(0);
			d047003.setDbtrAddCt(new Date());
			
			d047003.setTranDate(new Date());
			
			d047003.setTranAmt(0);
			
			return d047003;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("ERROR", e);
			return null;
		}
	}

	public static D010054 prepareD010054Obj(String accNo, String add1, String add2, String add3, String area,
			String brcode, String cityCode, String mobNo, String pinCode) {
		try {
			D010054 d010054 = new D010054();
			D010054Id id3 = new D010054Id();
			id3.setAddrType(Byte.valueOf("1"));
			id3.setLbrCode(Integer.valueOf(brcode.trim()));
			id3.setPrdAcctId(accNo);

			d010054.setAddr1(add1);
			d010054.setAddr2(add2);
			d010054.setAddr3(add3);
			d010054.setArea(Integer.valueOf(area.trim()));
			d010054.setCityCd(cityCode.trim());
			d010054.setCountryCd("IN");
			d010054.setDbtrAddCb(0);
			d010054.setDbtrAddCd(new Date());
			d010054.setDbtrAddCk(0);
			d010054.setDbtrAddCs(Short.valueOf("0"));
			d010054.setDbtrAddCt(new Date());
			d010054.setDbtrAddMb(0);
			d010054.setDbtrAddMd(new Date());
			d010054.setDbtrAddMk(0);
			d010054.setDbtrAddMs(Short.valueOf("0"));
			d010054.setDbtrAddMt(new Date());
			d010054.setDbtrAuthDone(Byte.valueOf("1"));
			d010054.setDbtrAuthNeeded(Byte.valueOf("0"));
			d010054.setDbtrLhisTrnNo(0);
			d010054.setDbtrLupdCb(0);
			d010054.setDbtrLupdCd(new Date());
			d010054.setDbtrLupdCk(0);
			d010054.setDbtrLupdCs(Short.valueOf("0"));
			d010054.setDbtrLupdCt(new Date());
			d010054.setDbtrLupdMb(0);
			d010054.setDbtrLupdMd(new Date());
			d010054.setDbtrLupdMk(0);
			d010054.setDbtrLupdMs(Short.valueOf("0"));
			d010054.setDbtrLupdMt(new Date());
			d010054.setDbtrRecStat(Byte.valueOf("0"));
			d010054.setDbtrTauthDone(Short.valueOf("1"));
			d010054.setDbtrUpdtChkId(Short.valueOf("0"));
			d010054.setFax1(" ");
			d010054.setFax2(" ");
			d010054.setId(id3);
			d010054.setPinCd(Integer.valueOf((pinCode==null || pinCode.trim().equalsIgnoreCase(""))?"0":pinCode.trim()));
			d010054.setTel1(mobNo);
			d010054.setTel2(mobNo);
			return d010054;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ERROR", e);
			return null;
		}
	}
	public static D009122 prepareD009122Obj(String accNo, String accType, String brcode,Session session) {
		try {
			System.out.println("accNo::>>"+accNo);
			D009037Id id=new D009037Id();
			id.setLbrCode(Integer.valueOf(brcode.trim()));
			id.setPrdCd(accNo.trim().substring(0,8).trim());
			D009037 d009037=session.get(D009037.class, id);
			//
			if(d009037==null)
			{
				D009122 d009122 = new D009122();
				D009122Id d009122Id = new D009122Id();
				d009122Id.setLbrCode(Integer.valueOf(brcode));
				d009122Id.setPrdAcctId(accNo);

				d009122.setAccountType(accType);
				d009122.setAcctOpnType(Byte.valueOf("0"));
				d009122.setAtmYn(d009037==null?'N':d009037.getAtmYn());
				d009122.setBicCode(Short.valueOf("0"));
				d009122.setBusinessCat(" ");
				d009122.setBusinessInd(" ");
				d009122.setChqBookChgYn(d009037==null?'N':d009037.getChqBookChgYn());
				d009122.setDematNo(" ");
				d009122.setDematYn(d009037==null?'N':d009037.getDematYn());
				d009122.setDirRelation(Byte.valueOf("0"));
				d009122.setEmailDayInFreq(Byte.valueOf("0"));
				d009122.setEmpId("0");
				d009122.setEmpName(" ");
				d009122.setFolioChgsYn(d009037==null?'N':d009037.getFolioChgsYn());
				d009122.setFreqStmtOfAcctYn(d009037==null?'N':d009037.getFreqStmtOfAcctYn());
				d009122.setGraceDp(Byte.valueOf("0"));
				d009122.setId(d009122Id);
				d009122.setInDirRelation(' ');
				d009122.setInspChgsYn(d009037==null?'N':d009037.getInspChgsYn());
				d009122.setInspecYn(d009037==null?'N':d009037.getInspecYn());
				d009122.setLastPrintDt(new Date());
				d009122.setMemberNo(0);
				d009122.setMemberType(Byte.valueOf("0"));
				d009122.setMemReasonCd(Short.valueOf("0"));
				d009122.setMinBalChgYn(d009037==null?'N':d009037.getMinBalChgYn());
				d009122.setPassBookChgYn(d009037==null?'N':d009037.getPassBookChgYn());
				d009122.setWriteOffYn('N');
				d009122.setTrFrAcctYn(d009037==null?'N':d009037.getTrfrAcctYn());
				d009122.setTodYn('N');
				d009122.setTdsYn(d009037==null?'N':d009037.getTdsYn());
				d009122.setTdsReasonCd(Short.valueOf("0"));
				d009122.setStmtType(Byte.valueOf("0"));
				d009122.setSicCode(Short.valueOf("0"));
				d009122.setServiceChgsYn(d009037==null?'N':d009037.getServiceChgsYn());
				d009122.setRebateYn('N');
				d009122.setPensionAcyn('N');
				d009122.setPayInAcctId("        000000000000000000000000");
				d009122.setPassBookChgYn(d009037==null?'N':d009037.getPassBookChgYn());
				d009122.setMinBalChgYn(d009037==null?'N':d009037.getMinBalChgYn());
				d009122.setGstInNo(" ");
//				d009122.setAcctOpnType(Byte.valueOf("0"));
				return d009122;
			}
			System.out.println("D009037::>>"+d009037.toString());
			D009122 d009122 = new D009122();
			D009122Id d009122Id = new D009122Id();
			d009122Id.setLbrCode(Integer.valueOf(brcode));
			d009122Id.setPrdAcctId(accNo);

			d009122.setAccountType(accType);
			d009122.setAcctOpnType(Byte.valueOf("0"));
			d009122.setAtmYn(d009037==null?'N':d009037.getAtmYn());
			d009122.setBicCode(Short.valueOf("0"));
			d009122.setBusinessCat(" ");
			d009122.setBusinessInd(" ");
			d009122.setChqBookChgYn(d009037==null?'N':d009037.getChqBookChgYn());
			d009122.setDematNo(" ");
			d009122.setDematYn(d009037==null?'N':d009037.getDematYn());
			d009122.setDirRelation(Byte.valueOf("0"));
			d009122.setEmailDayInFreq(Byte.valueOf("0"));
			d009122.setEmpId("0");
			d009122.setEmpName(" ");
			d009122.setFolioChgsYn(d009037==null?'N':d009037.getFolioChgsYn());
			d009122.setFreqStmtOfAcctYn(d009037==null?'N':d009037.getFreqStmtOfAcctYn());
			d009122.setGraceDp(Byte.valueOf("0"));
			d009122.setId(d009122Id);
			d009122.setInDirRelation(' ');
			d009122.setInspChgsYn(d009037==null?'N':d009037.getInspChgsYn());
			d009122.setInspecYn(d009037==null?'N':d009037.getInspecYn());
			d009122.setLastPrintDt(new Date());
			d009122.setMemberNo(0);
			d009122.setMemberType(Byte.valueOf("0"));
			d009122.setMemReasonCd(Short.valueOf("0"));
			d009122.setMinBalChgYn(d009037==null?'N':d009037.getMinBalChgYn());
			d009122.setPassBookChgYn(d009037==null?'N':d009037.getPassBookChgYn());
			d009122.setWriteOffYn('N');
			d009122.setTrFrAcctYn(d009037==null?'N':d009037.getTrfrAcctYn());
			d009122.setTodYn('N');
			d009122.setTdsYn(d009037==null?'N':d009037.getTdsYn());
			d009122.setTdsReasonCd(Short.valueOf("0"));
			d009122.setStmtType(Byte.valueOf("0"));
			d009122.setSicCode(Short.valueOf("0"));
			d009122.setServiceChgsYn(d009037==null?'N':d009037.getServiceChgsYn());
			d009122.setRebateYn('N');
			d009122.setPensionAcyn('N');
			d009122.setPayInAcctId("        000000000000000000000000");
			d009122.setPassBookChgYn(d009037==null?'N':d009037.getPassBookChgYn());
			d009122.setMinBalChgYn(d009037==null?'N':d009037.getMinBalChgYn());
			d009122.setGstInNo(" ");
//			d009122.setAcctOpnType(Byte.valueOf("0"));
			System.out.println("D009122::>>"+d009122.toString());
			return d009122;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("ERROR:", e);
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static boolean isMemberExist(int custNo) {
		boolean result = false;
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		//List<D048004> code = session.createCriteria(D048004.class).add(Restrictions.eq("custNo", custNo)).list();
		List<D048004> code = session.createQuery("From D048004 where custNo=:custNo").setParameter("custNo", custNo).getResultList();
		try {
			logger.error("Member Data:-"+code.size()+"\n Data :-"+code.toString());
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Member Data:-NULL");
		}
		tx.commit();
		session.close();
		session = null;
		short x=99, y=77,z=15;
		Short sValue1 = new Short(x);          
		Short sValue2 = new Short(y);
		if (!code.isEmpty())
		{
			logger.error("Member Data:-"+code);
			for(D048004 data : code) {
				logger.error("Member="+custNo +" :- "+data.getMemType());
				//if(Short.compare(x, data.getShrStatus())!=0 && Short.compare(y, data.getShrStatus())!=0) {
				logger.error("Member="+custNo +" :- "+data.getShrStatus());
				if(Short.compare(z, data.getShrStatus())==0) {
					logger.error("Member Inside="+custNo);
					if(data.getMemType().equalsIgnoreCase("1")) {
						result = true;
						break;
					}
				}
			}
		}
		
		return result;
		
	}
	
	public static boolean isMemberApplicationExist(int custNo) throws Exception {
		boolean result = false;
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		//List<D048005> code = session.createCriteria(D048005.class).add(Restrictions.eq("custNo", custNo)).list();
		@SuppressWarnings("unchecked")
		List<D048005> code = session.createQuery("From D048005 where custNo=:custNo").setParameter("custNo", custNo).getResultList();
		try {
			logger.error("Member Data:-"+code.size()+"\n Data :-"+code.toString());
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("Member Data:-NULL");
		}
		tx.commit();
		session.close();
		session = null;
		
		if (!code.isEmpty())
		{
			logger.error("Member Data:-"+code);
			for(D048005 data : code) {
				logger.error("Member="+custNo +" -"+data.getMemType());
				/*if(data.getMemNo().trim().equalsIgnoreCase("000000000000000000000000")) {
					result = true;
					break;
				}else {
					result = false;	
				}*/
				result = true;
				break;
			}
		}
		
		return result;
		
	}
	
	
}
