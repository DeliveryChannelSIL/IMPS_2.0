package com.sil.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.pmw.tinylog.Logger;

import com.google.gson.Gson;
import com.sil.commonswitch.DataUtils;
import com.sil.constants.MSGConstants;
import com.sil.domain.LookUpDetails;
import com.sil.domain.LookUpResponse;
import com.sil.hbm.D001002;
import com.sil.hbm.D009021;
import com.sil.hbm.D009022;
import com.sil.hbm.D047001;
import com.sil.hbm.D500025;
import com.sil.hbm.D500027;
import com.sil.hbm.D500028;
import com.sil.util.HBUtil;

public class LookUpServiceImpl implements LookUpService{
	@SuppressWarnings({ "deprecation", "unchecked", "unused" })
	@Override
	public List<D001002> getNameTittle() {
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
//		System.out.println("session.isOpen()::>>"+session.isOpen());
		Criteria criteria = session.createCriteria(D001002.class);
		criteria.add(Restrictions.eq("id.codeType",1059));
		List<D001002> code = criteria.list();
		System.out.println("getNameTittle().size()::>>"+code.size());
		tx.commit();
		session.close();
		tx=null;
		session=null;
		criteria=null;
		if(code==null)
		{
			System.out.println("Account code not found in D001002");
			return null;
		}
		return code;
	}
	
	@Override
	public List<D001002> getArea() {
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(D001002.class);
		criteria.add(Restrictions.eq("id.codeType",101003));
		List<D001002> code = criteria.list();
		System.out.println("getArea().size()::>>"+code.size());
		tx.commit();
		session.close();
		tx=null;
		session=null;
		criteria=null;
		return code;
	}

	@SuppressWarnings({ "deprecation", "unchecked", "unused" })
	@Override
	public List<D001002> getIndividual() {
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
//		System.out.println("session.isOpen()::>>"+session.isOpen());
		Criteria criteria = session.createCriteria(D001002.class);
		criteria.add(Restrictions.eq("id.codeType",1060));
		List<D001002> code = criteria.list();
		System.out.println("getIndividual().size()::>>"+code.size());
		tx.commit();
		session.close();
		tx=null;
		session=null;
		criteria=null;
		if(code==null)
		{
			System.out.println("Account code not found in D001002");
			return null;
		}
		return code;
	}
	
	@SuppressWarnings({ "deprecation", "unchecked", "unused" })
	@Override
	public List<D001002> getPanNo() {
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		System.out.println("session.isOpen()::>>"+session.isOpen());
		Criteria criteria = session.createCriteria(D001002.class);
		criteria.add(Restrictions.eq("id.codeType",1075));
		List<D001002> code = criteria.list();
		System.out.println("getPanNo().size()::>>"+code.size());
		tx.commit();
		session.close();
		tx=null;
		session=null;
		criteria=null;
		if(code==null)
		{
			System.out.println("Account code not found in D001002");
			return null;
		}
		return code;
	}
	
	@SuppressWarnings({ "deprecation", "unchecked", "unused" })
	@Override
	public List<D001002> getProductCode() {
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
//		System.out.println("session.isOpen()::>>"+session.isOpen());
		Criteria criteria = session.createCriteria(D001002.class);
		criteria.add(Restrictions.eq("id.codeType",11080));
//		criteria.add(Restrictions.eq("id.codeType",11080));
		List<D001002> code = criteria.list();
		System.out.println("getProductCode().size()::>>"+code.size());
		tx.commit();
		session.close();
		tx=null;
		session=null;
		criteria=null;
		if(code==null)
		{
			System.out.println("Account code not found in D001002");
			return null;
		}
		return code;
	}
	
	@SuppressWarnings({ "deprecation", "unchecked", "unused" })
	@Override
	public List<D001002> getAccountType() {
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
//		System.out.println("session.isOpen()::>>"+session.isOpen());
		Criteria criteria = session.createCriteria(D001002.class);
		criteria.add(Restrictions.eq("id.codeType",1067));
		List<D001002> code = criteria.list();
		System.out.println("getAccountType().size()::>>"+code.size());
		tx.commit();
		session.close();
		tx=null;
		session=null;
		criteria=null;
		if(code==null)
		{
			System.out.println("Account code not found in D001002");
			return null;
		}
		return code;
	}
	@SuppressWarnings({ "deprecation", "unchecked", "unused" })
	@Override
	public List<D001002> getModeOfOperation() {
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(D001002.class);
		criteria.add(Restrictions.eq("id.codeType",1066));
		List<D001002> code = criteria.list();
		System.out.println("getModeOfOperation().size()::>>"+code.size());
		tx.commit();
		session.close();
		tx=null;
		session=null;
		criteria=null;
		if(code==null)
		{
			System.out.println("Account code not found in D001002");
			return null;
		}
		return code;
	}
	
	public static List<D001002> getLookUpdetails(int codeType,String code) {
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
//		System.out.println("session.isOpen()::>>"+session.isOpen());
		Criteria criteria = session.createCriteria(D001002.class);
		criteria.add(Restrictions.eq("id.codeType",codeType));
		criteria.add(Restrictions.eq("id.codeType",code));
		List<D001002> codeList = criteria.list();
		System.out.println("getModeOfOperation().size()::>>"+codeList.size());
		tx.commit();
		session.close();
		tx=null;
		session=null;
		criteria=null;
		if(code==null)
		{
			System.out.println("Account code not found in D001002");
			return null;
		}
		return codeList;
	}
	
	@SuppressWarnings({ "deprecation", "unchecked", "unused" })
	@Override
	public List<D500028> getCity() {
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
//		System.out.println("session.isOpen()::>>"+session.isOpen());
		Criteria criteria = session.createCriteria(D500028.class);
//		criteria.add(Restrictions.eq("codeType",1066));
		List<D500028> code = criteria.list();
		System.out.println("getCity().size()::>>"+code.size());
		tx.commit();
		session.close();
		tx=null;
		session=null;
		criteria=null;
		if(code==null)
		{
			System.out.println("Account code not found in D001002");
			return null;
		}
		return code;
	}
	
	@SuppressWarnings({ "deprecation", "unchecked", "unused" })
	@Override
	public List<D500025> getState() {
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		System.out.println("session.isOpen()::>>"+session.isOpen());
		Criteria criteria = session.createCriteria(D500025.class);
//		criteria.add(Restrictions.eq("codeType",1066));
		List<D500025> code = criteria.list();
		System.out.println("getState().size()::>>"+code.size());
		tx.commit();
		session.close();
		tx=null;
		session=null;
		criteria=null;
		if(code==null)
		{
			System.out.println("Account code not found in D500025");
			return null;
		}
		return code;
	}
	
	@SuppressWarnings({ "deprecation", "unchecked", "unused" })
	@Override
	public List<D500027> getCountryCode() {
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
//		System.out.println("session.isOpen()::>>"+session.isOpen());
		Criteria criteria = session.createCriteria(D500027.class);
//		criteria.add(Restrictions.eq("codeType",1066));
		List<D500027> code = criteria.list();
		System.out.println("getCountryCode().size()::>>"+code.size());
		tx.commit();
		session.close();
		tx=null;
		session=null;
		criteria=null;
		if(code==null)
		{
			System.out.println("Account code not found in D500027");
			return null;
		}
		return code;
	}
	
	public LookUpDetails getAllLookUpValues(String flag) {
		if(flag.equalsIgnoreCase("PGM"))
		{
			List<D001002> title=new LookUpServiceImpl().getNameTittle();
			List<D001002> area=new LookUpServiceImpl().getArea();
			List<D500028> city=new LookUpServiceImpl().getCity();
			List<D500027> contryCode=new LookUpServiceImpl().getCountryCode();
			List<D001002> individual=new LookUpServiceImpl().getIndividual();
			List<D001002> modeOfOperation= new LookUpServiceImpl().getModeOfOperation();
			List<D001002> panNo=new LookUpServiceImpl().getPanNo();
			List<D001002> prodCode=new LookUpServiceImpl().getProductCode();
			List<D500025> state=new LookUpServiceImpl().getState();
			List<D001002> accType=new LookUpServiceImpl().getAccountType();
			
			ArrayList<LookUpResponse> titleList=new ArrayList<>();
			ArrayList<LookUpResponse> areaList=new ArrayList<>();
			ArrayList<LookUpResponse> cityList=new ArrayList<>();
			ArrayList<LookUpResponse> contryList=new ArrayList<>();
			ArrayList<LookUpResponse> individualList=new ArrayList<>();
			ArrayList<LookUpResponse> modeOfOperationList=new ArrayList<>();
			ArrayList<LookUpResponse> panList=new ArrayList<>();
			ArrayList<LookUpResponse> prodCodeList=new ArrayList<>();
			ArrayList<LookUpResponse> stateList=new ArrayList<>();
			ArrayList<LookUpResponse> acctypeList=new ArrayList<>();
			
			HashMap<String, String> lookUpList=new HashMap<>();
			
			if(title!=null && title.size()>0)
			{
				for(int i=0;i<title.size();i++)
				{
					LookUpResponse response=new LookUpResponse();
					response.setCode(""+title.get(i).getId().getCodeType());
					response.setValue(""+title.get(i).getId().getCode().trim());
					titleList.add(response);
				}
			}
			if(area!=null && area.size()>0)
			{
				for(int i=0;i<area.size();i++)
				{
					LookUpResponse response=new LookUpResponse();
					response.setCode(""+area.get(i).getId().getCode().trim());
					response.setValue(""+area.get(i).getCodeDesc().trim());
					areaList.add(response);
				}
			}
			if(contryCode!=null && contryCode.size()>0)
			{
				for(int i=0;i<contryCode.size();i++)
				{
					LookUpResponse response=new LookUpResponse();
					response.setCode(""+contryCode.get(i).getCntryCd().trim());
					response.setValue(""+contryCode.get(i).getCountryName().trim());
					contryList.add(response);
				}
			}
			if(city!=null && city.size()>0)
			{
				for(int i=0;i<city.size();i++)
				{
					LookUpResponse response=new LookUpResponse();
					response.setCode(""+city.get(i).getPlaceCd().trim());
					response.setValue(""+city.get(i).getPlaceCdDesc().trim());
					cityList.add(response);
				}
			}
			/*if(individual!=null && individual.size()>0)
			{
				for(int i=0;i<individual.size();i++)
				{
					LookUpResponse response=new LookUpResponse();
					response.setCode(""+individual.get(i).getId().getCode().trim());
					response.setValue(""+individual.get(i).getCodeDesc().trim());
					individualList.add(response);
				}
			}*/
			/*if(modeOfOperation!=null && modeOfOperation.size()>0)
			{
				for(int i=0;i<modeOfOperation.size();i++)
				{
					LookUpResponse response=new LookUpResponse();
					response.setCode(""+modeOfOperation.get(i).getId().getCode().trim());
					response.setValue(""+modeOfOperation.get(i).getCodeDesc().trim());
					modeOfOperationList.add(response);
				}
			}*/
			if(panNo!=null && panNo.size()>0)
			{
				for(int i=0;i<panNo.size();i++)
				{
					LookUpResponse response=new LookUpResponse();
					response.setCode(""+panNo.get(i).getId().getCode().trim());
					response.setValue(""+panNo.get(i).getCodeDesc().trim());
					panList.add(response);
				}
			}
			/*if(prodCode!=null && prodCode.size()>0)
			{
				for(int i=0;i<prodCode.size();i++)
				{
					LookUpResponse response=new LookUpResponse();
					response.setCode(""+prodCode.get(i).getId().getCode().trim());
					response.setValue(""+prodCode.get(i).getId().getCode().trim());
					prodCodeList.add(response);
				}
			}
	*/		/*if(state!=null && state.size()>0)
			{
				for(int i=0;i<state.size();i++)
				{
					LookUpResponse response=new LookUpResponse();
					response.setCode(""+state.get(i).getStateCd().trim());
					response.setValue(""+state.get(i).getStateDesc().trim());
					stateList.add(response);
				}
			}*/
			if(accType!=null && accType.size()>0)
			{
				for(int i=0;i<accType.size();i++)
				{
					LookUpResponse response=new LookUpResponse();
					response.setCode(""+accType.get(i).getId().getCode().trim());
					response.setValue(""+accType.get(i).getCodeDesc().trim());
					acctypeList.add(response);
				}
			}
//			========added for Shivkrupa MPOS on 18/04/2017
			
			LookUpResponse response=new LookUpResponse();
			response.setCode("MH");
			response.setValue("MAHARASHTRA");
			stateList.add(response);
		
			LookUpResponse response1=new LookUpResponse();
			response1.setCode("1");
			response1.setValue("INDIVIDUAL");
			individualList.add(response1);
			
			LookUpResponse response2=new LookUpResponse();
			response2.setCode("PGM");
			response2.setValue("PGM");
			prodCodeList.add(response2);
			
			LookUpResponse response3=new LookUpResponse();
			response3.setCode("1");
			response3.setValue("SELF");
			modeOfOperationList.add(response3);
			
			LookUpResponse response4=new LookUpResponse();
			response4.setCode("2");
			response4.setValue("EITHER OR SURVIVOR");
			modeOfOperationList.add(response4);
			
			LookUpDetails  details=new LookUpDetails();
			details.setAcctypeList(acctypeList);
			details.setAreaList(areaList);
			details.setCityList(cityList);
			details.setContryList(contryList);
			details.setIndividualList(individualList);
			details.setModeOfOperationList(modeOfOperationList);
			details.setPanList(panList);
			details.setProdCodeList(prodCodeList);
			details.setStateList(stateList);
			details.setTitleList(titleList);
//			lookUpList.put("acctypeList", new Gson().toJson(acctypeList));
//			lookUpList.put("titleList", new Gson().toJson(titleList));
//			lookUpList.put("areaList", new Gson().toJson(areaList));
//			lookUpList.put("cityList", new Gson().toJson(cityList));
//			lookUpList.put("contryList", new Gson().toJson(contryList));
//			lookUpList.put("individualList", new Gson().toJson(individualList));
//			lookUpList.put("modeOfOperationList", new Gson().toJson(modeOfOperationList));
//			lookUpList.put("panList", new Gson().toJson(panList));
//			lookUpList.put("prodCodeList", new Gson().toJson(prodCodeList));
//			lookUpList.put("stateList", new Gson().toJson(stateList));
			
			acctypeList=null;
			areaList=null;
			cityList=null;
			contryList=null;
			individualList=null;
			modeOfOperationList=null;
			panList=null;
			prodCodeList=null;
			stateList=null;
			titleList=null;
			System.out.println("Final JSON RESPONSE::>>"+new Gson().toJson(lookUpList));
			return details;
		}
		List<D001002> title=new LookUpServiceImpl().getNameTittle();
		List<D001002> area=new LookUpServiceImpl().getArea();
		List<D500028> city=new LookUpServiceImpl().getCity();
		List<D500027> contryCode=new LookUpServiceImpl().getCountryCode();
		List<D001002> individual=new LookUpServiceImpl().getIndividual();
		List<D001002> modeOfOperation= new LookUpServiceImpl().getModeOfOperation();
		List<D001002> panNo=new LookUpServiceImpl().getPanNo();
		List<D001002> prodCode=new LookUpServiceImpl().getProductCode();
		List<D500025> state=new LookUpServiceImpl().getState();
		List<D001002> accType=new LookUpServiceImpl().getAccountType();
		
		ArrayList<LookUpResponse> titleList=new ArrayList<>();
		ArrayList<LookUpResponse> areaList=new ArrayList<>();
		ArrayList<LookUpResponse> cityList=new ArrayList<>();
		ArrayList<LookUpResponse> contryList=new ArrayList<>();
		ArrayList<LookUpResponse> individualList=new ArrayList<>();
		ArrayList<LookUpResponse> modeOfOperationList=new ArrayList<>();
		ArrayList<LookUpResponse> panList=new ArrayList<>();
		ArrayList<LookUpResponse> prodCodeList=new ArrayList<>();
		ArrayList<LookUpResponse> stateList=new ArrayList<>();
		ArrayList<LookUpResponse> acctypeList=new ArrayList<>();
		
		HashMap<String, String> lookUpList=new HashMap<>();
		
		if(title!=null && title.size()>0)
		{
			for(int i=0;i<title.size();i++)
			{
				LookUpResponse response=new LookUpResponse();
				response.setCode(""+title.get(i).getId().getCodeType());
				response.setValue(""+title.get(i).getId().getCode().trim());
				titleList.add(response);
			}
		}
		if(area!=null && area.size()>0)
		{
			for(int i=0;i<area.size();i++)
			{
				LookUpResponse response=new LookUpResponse();
				response.setCode(""+area.get(i).getId().getCode().trim());
				response.setValue(""+area.get(i).getCodeDesc().trim());
				areaList.add(response);
			}
		}
		if(contryCode!=null && contryCode.size()>0)
		{
			for(int i=0;i<contryCode.size();i++)
			{
				LookUpResponse response=new LookUpResponse();
				response.setCode(""+contryCode.get(i).getCntryCd().trim());
				response.setValue(""+contryCode.get(i).getCountryName().trim());
				contryList.add(response);
			}
		}
		if(city!=null && city.size()>0)
		{
			for(int i=0;i<city.size();i++)
			{
				LookUpResponse response=new LookUpResponse();
				response.setCode(""+city.get(i).getPlaceCd().trim());
				response.setValue(""+city.get(i).getPlaceCdDesc().trim());
				cityList.add(response);
			}
		}
		if(individual!=null && individual.size()>0)
		{
			for(int i=0;i<individual.size();i++)
			{
				LookUpResponse response=new LookUpResponse();
				response.setCode(""+individual.get(i).getId().getCode().trim());
				response.setValue(""+individual.get(i).getCodeDesc().trim());
				individualList.add(response);
			}
		}
		if(modeOfOperation!=null && modeOfOperation.size()>0)
		{
			for(int i=0;i<modeOfOperation.size();i++)
			{
				LookUpResponse response=new LookUpResponse();
				response.setCode(""+modeOfOperation.get(i).getId().getCode().trim());
				response.setValue(""+modeOfOperation.get(i).getCodeDesc().trim());
				modeOfOperationList.add(response);
			}
		}
		if(panNo!=null && panNo.size()>0)
		{
			for(int i=0;i<panNo.size();i++)
			{
				LookUpResponse response=new LookUpResponse();
				response.setCode(""+panNo.get(i).getId().getCode().trim());
				response.setValue(""+panNo.get(i).getCodeDesc().trim());
				panList.add(response);
			}
		}
		if(prodCode!=null && prodCode.size()>0)
		{
			for(int i=0;i<prodCode.size();i++)
			{
				LookUpResponse response=new LookUpResponse();
				response.setCode(""+prodCode.get(i).getId().getCode().trim());
				response.setValue(""+prodCode.get(i).getId().getCode().trim());
				prodCodeList.add(response);
			}
		}
		if(state!=null && state.size()>0)
		{
			for(int i=0;i<state.size();i++)
			{
				LookUpResponse response=new LookUpResponse();
				response.setCode(""+state.get(i).getStateCd().trim());
				response.setValue(""+state.get(i).getStateDesc().trim());
				stateList.add(response);
			}
		}
		if(accType!=null && accType.size()>0)
		{
			for(int i=0;i<accType.size();i++)
			{
				LookUpResponse response=new LookUpResponse();
				response.setCode(""+accType.get(i).getId().getCode().trim());
				response.setValue(""+accType.get(i).getCodeDesc().trim());
				acctypeList.add(response);
			}
		}
		LookUpDetails  details=new LookUpDetails();
		details.setAcctypeList(acctypeList);
		details.setAreaList(areaList);
		details.setCityList(cityList);
		details.setContryList(contryList);
		details.setIndividualList(individualList);
		details.setModeOfOperationList(modeOfOperationList);
		details.setPanList(panList);
		details.setProdCodeList(prodCodeList);
		details.setStateList(stateList);
		details.setTitleList(titleList);

		acctypeList=null;
		areaList=null;
		cityList=null;
		contryList=null;
		individualList=null;
		modeOfOperationList=null;
		panList=null;
		prodCodeList=null;
		stateList=null;
		titleList=null;
		System.out.println("Final JSON RESPONSE::>>"+new Gson().toJson(lookUpList));
		return details;
	}
	
	@SuppressWarnings({ })
	@Override
	public LookUpDetails getAllLookUpValues(String flag,String agentAccNo) {
		LookUpDetails responseNew=new LookUpDetails();
		if(flag.equalsIgnoreCase("PGM"))
		{
			D009022 sourceAccount = DataUtils.getAccount(agentAccNo.trim());
			if(sourceAccount==null)
			{
				responseNew.setResponse(MSGConstants.ERROR);
				responseNew.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
				return responseNew;
			}
			List<D001002> title=new LookUpServiceImpl().getNameTittle();
			List<D001002> area=new LookUpServiceImpl().getArea();
			List<D500028> city=new LookUpServiceImpl().getCity();
			List<D500027> contryCode=new LookUpServiceImpl().getCountryCode();
			List<D001002> individual=new LookUpServiceImpl().getIndividual();
			List<D001002> modeOfOperation= new LookUpServiceImpl().getModeOfOperation();
			List<D001002> panNo=new LookUpServiceImpl().getPanNo();
			List<D001002> prodCode=new LookUpServiceImpl().getProductCode();
			List<D500025> state=new LookUpServiceImpl().getState();
			List<D001002> accType=new LookUpServiceImpl().getAccountType();
			
			ArrayList<LookUpResponse> titleList=new ArrayList<>();
			ArrayList<LookUpResponse> areaList=new ArrayList<>();
			ArrayList<LookUpResponse> cityList=new ArrayList<>();
			ArrayList<LookUpResponse> contryList=new ArrayList<>();
			ArrayList<LookUpResponse> individualList=new ArrayList<>();
			ArrayList<LookUpResponse> modeOfOperationList=new ArrayList<>();
			ArrayList<LookUpResponse> panList=new ArrayList<>();
			ArrayList<LookUpResponse> prodCodeList=new ArrayList<>();
			ArrayList<LookUpResponse> stateList=new ArrayList<>();
			ArrayList<LookUpResponse> acctypeList=new ArrayList<>();
			
			HashMap<String, String> lookUpList=new HashMap<>();
			
			if(title!=null && title.size()>0)
			{
				for(int i=0;i<title.size();i++)
				{
					LookUpResponse response=new LookUpResponse();
					response.setCode(""+title.get(i).getId().getCodeType());
					response.setValue(""+title.get(i).getId().getCode().trim());
					titleList.add(response);
				}
			}
			if(area!=null && area.size()>0)
			{
				for(int i=0;i<area.size();i++)
				{
					LookUpResponse response=new LookUpResponse();
					response.setCode(""+area.get(i).getId().getCode().trim());
					response.setValue(""+area.get(i).getCodeDesc().trim());
					areaList.add(response);
				}
			}
			if(contryCode!=null && contryCode.size()>0)
			{
				for(int i=0;i<contryCode.size();i++)
				{
					LookUpResponse response=new LookUpResponse();
					response.setCode(""+contryCode.get(i).getCntryCd().trim());
					response.setValue(""+contryCode.get(i).getCountryName().trim());
					contryList.add(response);
				}
			}
			if(city!=null && city.size()>0)
			{
				for(int i=0;i<city.size();i++)
				{
					LookUpResponse response=new LookUpResponse();
					response.setCode(""+city.get(i).getPlaceCd().trim());
					response.setValue(""+city.get(i).getPlaceCdDesc().trim());
					cityList.add(response);
				}
			}
			/*if(individual!=null && individual.size()>0)
			{
				for(int i=0;i<individual.size();i++)
				{
					LookUpResponse response=new LookUpResponse();
					response.setCode(""+individual.get(i).getId().getCode().trim());
					response.setValue(""+individual.get(i).getCodeDesc().trim());
					individualList.add(response);
				}
			}*/
			/*if(modeOfOperation!=null && modeOfOperation.size()>0)
			{
				for(int i=0;i<modeOfOperation.size();i++)
				{
					LookUpResponse response=new LookUpResponse();
					response.setCode(""+modeOfOperation.get(i).getId().getCode().trim());
					response.setValue(""+modeOfOperation.get(i).getCodeDesc().trim());
					modeOfOperationList.add(response);
				}
			}*/
			if(panNo!=null && panNo.size()>0)
			{
				for(int i=0;i<panNo.size();i++)
				{
					LookUpResponse response=new LookUpResponse();
					response.setCode(""+panNo.get(i).getId().getCode().trim());
					response.setValue(""+panNo.get(i).getCodeDesc().trim());
					panList.add(response);
				}
			}
			/*if(prodCode!=null && prodCode.size()>0)
			{
				for(int i=0;i<prodCode.size();i++)
				{
					LookUpResponse response=new LookUpResponse();
					response.setCode(""+prodCode.get(i).getId().getCode().trim());
					response.setValue(""+prodCode.get(i).getId().getCode().trim());
					prodCodeList.add(response);
				}
			}
	*/		/*if(state!=null && state.size()>0)
			{
				for(int i=0;i<state.size();i++)
				{
					LookUpResponse response=new LookUpResponse();
					response.setCode(""+state.get(i).getStateCd().trim());
					response.setValue(""+state.get(i).getStateDesc().trim());
					stateList.add(response);
				}
			}*/
			if(accType!=null && accType.size()>0)
			{
				for(int i=0;i<accType.size();i++)
				{
					LookUpResponse response=new LookUpResponse();
					response.setCode(""+accType.get(i).getId().getCode().trim());
					response.setValue(""+accType.get(i).getCodeDesc().trim());
					acctypeList.add(response);
				}
			}
//			========added for Shivkrupa MPOS on 18/04/2017
			LookUpResponse response=new LookUpResponse();
			response.setCode("MH");
			response.setValue("MAHARASHTRA");
			stateList.add(response);
		
			LookUpResponse response1=new LookUpResponse();
			response1.setCode("1");
			response1.setValue("INDIVIDUAL");
			individualList.add(response1);
			
			D047001 d047001=DataUtils.getDDSProductCode(sourceAccount.getId().getLbrCode(), sourceAccount.getId().getPrdAcctId());
			System.out.println("d047001::>>"+d047001);
			if(d047001==null)
			{
				responseNew.setResponse(MSGConstants.ERROR);
				responseNew.setErrorMsg(MSGConstants.AGENT_PRODUCT_CODES_NOT_MAPPED);
				return responseNew;
			}
			LookUpResponse response2=new LookUpResponse();
			response2.setCode(""+d047001.getCustPrdCd().trim());
			response2.setValue(""+d047001.getCustPrdCd().trim());
			prodCodeList.add(response2);
			
			/*LookUpResponse response2=new LookUpResponse();
			response2.setCode(sourceAccount.getId().getPrdAcctId().substring(0,8).trim());
			response2.setValue(sourceAccount.getId().getPrdAcctId().substring(0,8).trim());
			prodCodeList.add(response2);*/
			
//			================added for temp
			LookUpResponse response3=new LookUpResponse();
			response3.setCode("1");
			response3.setValue("SELF");
			modeOfOperationList.add(response3);
			
			LookUpResponse response4=new LookUpResponse();
			response4.setCode("2");
			response4.setValue("EITHER OR SURVIVOR");
			modeOfOperationList.add(response4);
			
			LookUpDetails  details=new LookUpDetails();
			details.setAcctypeList(acctypeList);
			details.setAreaList(areaList);
			details.setCityList(cityList);
			details.setContryList(contryList);
			details.setIndividualList(individualList);
			details.setModeOfOperationList(modeOfOperationList);
			details.setPanList(panList);
			details.setProdCodeList(prodCodeList);
			details.setStateList(stateList);
			details.setTitleList(titleList);
			details.setResponse(MSGConstants.SUCCESS);
			details.setErrorMsg(MSGConstants.SUCCESS_MSG);

			acctypeList=null;
			areaList=null;
			cityList=null;
			contryList=null;
			individualList=null;
			modeOfOperationList=null;
			panList=null;
			prodCodeList=null;
			stateList=null;
			titleList=null;
			System.out.println("Final JSON RESPONSE::>>"+new Gson().toJson(lookUpList));
			return details;
		}
		D009022 sourceAccount = DataUtils.getAccount(agentAccNo.trim());
		if(sourceAccount==null)
		{
			responseNew.setResponse(MSGConstants.ERROR);
			responseNew.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
			return responseNew;
		}
		List<D001002> title=new LookUpServiceImpl().getNameTittle();
		List<D001002> area=new LookUpServiceImpl().getArea();
		List<D500028> city=new LookUpServiceImpl().getCity();
		List<D500027> contryCode=new LookUpServiceImpl().getCountryCode();
		List<D001002> individual=new LookUpServiceImpl().getIndividual();
		List<D001002> modeOfOperation= new LookUpServiceImpl().getModeOfOperation();
		List<D001002> panNo=new LookUpServiceImpl().getPanNo();
		List<D001002> prodCode=new LookUpServiceImpl().getProductCode();
		List<D500025> state=new LookUpServiceImpl().getState();
		List<D001002> accType=new LookUpServiceImpl().getAccountType();
		
		ArrayList<LookUpResponse> titleList=new ArrayList<>();
		ArrayList<LookUpResponse> areaList=new ArrayList<>();
		ArrayList<LookUpResponse> cityList=new ArrayList<>();
		ArrayList<LookUpResponse> contryList=new ArrayList<>();
		ArrayList<LookUpResponse> individualList=new ArrayList<>();
		ArrayList<LookUpResponse> modeOfOperationList=new ArrayList<>();
		ArrayList<LookUpResponse> panList=new ArrayList<>();
		ArrayList<LookUpResponse> prodCodeList=new ArrayList<>();
		ArrayList<LookUpResponse> stateList=new ArrayList<>();
		ArrayList<LookUpResponse> acctypeList=new ArrayList<>();
		
		HashMap<String, String> lookUpList=new HashMap<>();
		
		if(title!=null && title.size()>0)
		{
			for(int i=0;i<title.size();i++)
			{
				LookUpResponse response=new LookUpResponse();
				response.setCode(""+title.get(i).getId().getCodeType());
				response.setValue(""+title.get(i).getId().getCode().trim());
				titleList.add(response);
			}
		}
		if(area!=null && area.size()>0)
		{
			for(int i=0;i<area.size();i++)
			{
				LookUpResponse response=new LookUpResponse();
				response.setCode(""+area.get(i).getId().getCode().trim());
				response.setValue(""+area.get(i).getCodeDesc().trim());
				areaList.add(response);
			}
		}
		if(contryCode!=null && contryCode.size()>0)
		{
			for(int i=0;i<contryCode.size();i++)
			{
				LookUpResponse response=new LookUpResponse();
				response.setCode(""+contryCode.get(i).getCntryCd().trim());
				response.setValue(""+contryCode.get(i).getCountryName().trim());
				contryList.add(response);
			}
		}
		if(city!=null && city.size()>0)
		{
			for(int i=0;i<city.size();i++)
			{
				LookUpResponse response=new LookUpResponse();
				response.setCode(""+city.get(i).getPlaceCd().trim());
				response.setValue(""+city.get(i).getPlaceCdDesc().trim());
				cityList.add(response);
			}
		}
		if(individual!=null && individual.size()>0)
		{
			for(int i=0;i<individual.size();i++)
			{
				LookUpResponse response=new LookUpResponse();
				response.setCode(""+individual.get(i).getId().getCode().trim());
				response.setValue(""+individual.get(i).getCodeDesc().trim());
				individualList.add(response);
			}
		}
		if(modeOfOperation!=null && modeOfOperation.size()>0)
		{
			for(int i=0;i<modeOfOperation.size();i++)
			{
				LookUpResponse response=new LookUpResponse();
				response.setCode(""+modeOfOperation.get(i).getId().getCode().trim());
				response.setValue(""+modeOfOperation.get(i).getCodeDesc().trim());
				modeOfOperationList.add(response);
			}
		}
		if(panNo!=null && panNo.size()>0)
		{
			for(int i=0;i<panNo.size();i++)
			{
				LookUpResponse response=new LookUpResponse();
				response.setCode(""+panNo.get(i).getId().getCode().trim());
				response.setValue(""+panNo.get(i).getCodeDesc().trim());
				panList.add(response);
			}
		}
		if(prodCode!=null && prodCode.size()>0)
		{
			prodCodeList=getProductList(Integer.valueOf(agentAccNo.substring(0,3).trim()));
					/*for(int i=0;i<prodCode.size();i++)
					{
						LookUpResponse response=new LookUpResponse();
						response.setCode(""+prodCode.get(i).getId().getCode().trim());
						response.setValue(""+prodCode.get(i).getId().getCode().trim());
						prodCodeList.add(response);
					}*/	
		}
		if(state!=null && state.size()>0)
		{
			for(int i=0;i<state.size();i++)
			{
				LookUpResponse response=new LookUpResponse();
				response.setCode(""+state.get(i).getStateCd().trim());
				response.setValue(""+state.get(i).getStateDesc().trim());
				stateList.add(response);
			}
		}
		if(accType!=null && accType.size()>0)
		{
			for(int i=0;i<accType.size();i++)
			{
				LookUpResponse response=new LookUpResponse();
				response.setCode(""+accType.get(i).getId().getCode().trim());
				response.setValue(""+accType.get(i).getCodeDesc().trim());
				acctypeList.add(response);
			}
		}
//		========added for Shivkrupa MPOS on 18/04/2017
		/*
		LookUpResponse response=new LookUpResponse();
		response.setCode("MH");
		response.setValue("MAHARASHTRA");
		stateList.add(response);
	
		LookUpResponse response1=new LookUpResponse();
		response1.setCode("1");
		response1.setValue("INDIVIDUAL");
		individualList.add(response1);
		
		LookUpResponse response2=new LookUpResponse();
		response2.setCode("PGM");
		response2.setValue("PGM");
		prodCodeList.add(response2);
		
		LookUpResponse response3=new LookUpResponse();
		response3.setCode("1");
		response3.setValue("SELF");
		modeOfOperationList.add(response3);
		
		LookUpResponse response4=new LookUpResponse();
		response4.setCode("2");
		response4.setValue("EITHER OR SURVIVOR");
		modeOfOperationList.add(response4);*/
		
		LookUpDetails  details=new LookUpDetails();
		details.setAcctypeList(acctypeList);
		details.setAreaList(areaList);
		details.setCityList(cityList);
		details.setContryList(contryList);
		details.setIndividualList(individualList);
		details.setModeOfOperationList(modeOfOperationList);
		details.setPanList(panList);
		details.setProdCodeList(prodCodeList);
		details.setStateList(stateList);
		details.setTitleList(titleList);
//		lookUpList.put("acctypeList", new Gson().toJson(acctypeList));
//		lookUpList.put("titleList", new Gson().toJson(titleList));
//		lookUpList.put("areaList", new Gson().toJson(areaList));
//		lookUpList.put("cityList", new Gson().toJson(cityList));
//		lookUpList.put("contryList", new Gson().toJson(contryList));
//		lookUpList.put("individualList", new Gson().toJson(individualList));
//		lookUpList.put("modeOfOperationList", new Gson().toJson(modeOfOperationList));
//		lookUpList.put("panList", new Gson().toJson(panList));
//		lookUpList.put("prodCodeList", new Gson().toJson(prodCodeList));
//		lookUpList.put("stateList", new Gson().toJson(stateList));
		
		acctypeList=null;
		areaList=null;
		cityList=null;
		contryList=null;
		individualList=null;
		modeOfOperationList=null;
		panList=null;
		prodCodeList=null;
		stateList=null;
		titleList=null;
		System.out.println("Final JSON RESPONSE::>>"+new Gson().toJson(lookUpList));
		
		return details;
//		System.out.println("new Gson().toJson(acctypeList)::>>"+new Gson().toJson(acctypeList));
//		System.out.println("new Gson().toJson(titleList)::>>"+new Gson().toJson(titleList));
//		System.out.println("new Gson().toJson(areaList)::>>"+new Gson().toJson(areaList));
//		System.out.println("new Gson().toJson(cityList)::>>"+new Gson().toJson(cityList));
//		System.out.println("new Gson().toJson(contryList)::>>"+new Gson().toJson(contryList));
//		System.out.println("new Gson().toJson(individualList)::>>"+new Gson().toJson(individualList));
//		System.out.println("new Gson().toJson(modeOfOperationList)::>>"+new Gson().toJson(modeOfOperationList));
//		System.out.println("new Gson().toJson(panList)::>>"+new Gson().toJson(panList));
//		System.out.println("new Gson().toJson(prodCodeList)::>>"+new Gson().toJson(prodCodeList));
//		System.out.println("new Gson().toJson(stateList)::>>"+new Gson().toJson(stateList));
	}
	
	public static D500027 getCountryCode(String countryCode) {
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
//		System.out.println("session.isOpen()::>>"+session.isOpen());
		Criteria criteria = session.createCriteria(D500027.class);
		criteria.add(Restrictions.eq("cntryCd",countryCode));
		List<D500027> code = criteria.list();
		System.out.println("getCountryCode().size()::>>"+code.size());
		tx.commit();
		session.close();
		tx=null;
		session=null;
		criteria=null;
		if(code==null || code.isEmpty())
			return null;
		return code.get(0);
	}
	public static D500025 getState(String stateCode) {
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		System.out.println("session.isOpen()::>>"+session.isOpen());
		Criteria criteria = session.createCriteria(D500025.class);
		criteria.add(Restrictions.eq("stateCd",stateCode));
		List<D500025> code = criteria.list();
		System.out.println("getState().size()::>>"+code.size());
		tx.commit();
		session.close();
		tx=null;
		session=null;
		criteria=null;
		if(code==null || code.isEmpty())
			return null;
		return code.get(0);
	}
	public static D500028 getCity(String cityCode) {
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
//		System.out.println("session.isOpen()::>>"+session.isOpen());
		Criteria criteria = session.createCriteria(D500028.class);
		criteria.add(Restrictions.eq("placeCd",cityCode.trim()));
		List<D500028> code = criteria.list();
		System.out.println("getCity().size()::>>"+code.size());
		tx.commit();
		session.close();
		tx=null;
		session=null;
		criteria=null;
		if(code==null || code.isEmpty())
			return null;
		return code.get(0);
	}
//	===below loo
	public static D001002 getLookUp(int codeType,String custType) {
		Session session = HBUtil.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(D001002.class);
		criteria.add(Restrictions.eq("id.codeType",codeType));
		criteria.add(Restrictions.eq("id.code",String.format("%09d",Long.valueOf(custType))));
		List<D001002> code = criteria.list();
		System.out.println("getIndividual().size()::>>"+code.size());
		session.close();
		session=null;
		criteria=null;
		if(code==null || code.isEmpty())
			return null;
		return code.get(0);
	}
	public static ArrayList<LookUpResponse> getProductList(int brCode)
	{
		ArrayList<LookUpResponse> prodCodeList=new ArrayList<>();
		Session session=HBUtil.getSessionFactory().openSession();
		try {
			Criteria criteria=session.createCriteria(D009021.class);
			criteria.add(Restrictions.eq("id.lbrCode", brCode));
			criteria.add(Restrictions.in("moduleType",new ArrayList<Short>(Arrays.asList(Short.valueOf("11"),Short.valueOf("12")))));
			List<D009021> list=criteria.list();
			if(list==null || list.isEmpty())
				return prodCodeList;
			for(D009021 d009021:list)
			{
				LookUpResponse response=new LookUpResponse();
				response.setCode(d009021.getModuleType()+"");
				response.setValue(d009021.getId().getPrdCd().trim()+"");
				prodCodeList.add(response);
			}
			return prodCodeList;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Logger.error("",e);
			return prodCodeList;
		}finally {
			session.close();
			session=null;
		}
	}
	public static void main(String[] args) {
		System.out.println(getProductList(3));
//		System.out.println(""+getCountryCode("IN"));
//		System.out.println(""+getCity("MUM"));
//		System.out.println(""+getState("MH"));
//		System.out.println(getLookUp(1061, "2"));
		/*System.out.println(new LookUpServiceImpl().getNameTittle());
		System.out.println(new LookUpServiceImpl().getArea());
		System.out.println(new LookUpServiceImpl().getCity());
		System.out.println(new LookUpServiceImpl().getCountryCode());
		System.out.println(new LookUpServiceImpl().getIndividual());
		System.out.println(new LookUpServiceImpl().getModeOfOperation());
		System.out.println(new LookUpServiceImpl().getPanNo());
		System.out.println(new LookUpServiceImpl().getProductCode());
		System.out.println(new LookUpServiceImpl().getState());
		System.out.println(new LookUpServiceImpl().getAccountType());*/
//		new LookUpServiceImpl().getAllLookUpValues("PGM");
	}
}
	