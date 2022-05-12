package com.sil.dao;

import java.util.List;
import com.sil.domain.LookUpDetails;
import com.sil.hbm.D001002;
import com.sil.hbm.D500025;
import com.sil.hbm.D500027;
import com.sil.hbm.D500028;

public interface LookUpService {
	public List<D001002> getNameTittle();//1059
	public List<D001002> getArea();//101003
	public List<D001002> getIndividual();//1060
	public List<D001002> getPanNo();//1075
	public List<D001002> getProductCode();//11080
	public List<D001002> getAccountType();//1067
	public List<D001002> getModeOfOperation();//1066
	public List<D500028> getCity();
	public List<D500025> getState();
	public List<D500027> getCountryCode();
//	public LookUpDetails getAllLookUpValues();
//	public LookUpDetails getAllLookUpValues(String flag);
	LookUpDetails getAllLookUpValues(String flag, String agentAccNo);
}
