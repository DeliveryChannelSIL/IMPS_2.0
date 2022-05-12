package com.sil.domain;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import com.sil.commonswitch.IMPSTransactionDetail;
import com.sil.hbm.AgencyBankingTrn;

@XmlRootElement
public class ImpsTransactionReport {

	private String response;
	private String errorMsg;
	private List<IMPSTransactionDetail> impsTrnReport=new ArrayList<>();
	private List<AgencyBankingTrn> agencybankingTrn=new ArrayList<>();
	private List<Object[]> agencybankingTrnSummury=null;
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public List<IMPSTransactionDetail> getImpsTrnReport() {
		return impsTrnReport;
	}
	public void setImpsTrnReport(List<IMPSTransactionDetail> impsTrnReport) {
		this.impsTrnReport = impsTrnReport;
	}
	public List<AgencyBankingTrn> getAgencybankingTrn() {
		return agencybankingTrn;
	}
	public void setAgencybankingTrn(List<AgencyBankingTrn> agencybankingTrn) {
		this.agencybankingTrn = agencybankingTrn;
	}
	public List<Object[]> getAgencybankingTrnSummury() {
		return agencybankingTrnSummury;
	}
	public void setAgencybankingTrnSummury(List<Object[]> agencybankingTrnSummury) {
		this.agencybankingTrnSummury = agencybankingTrnSummury;
	}
	@Override
	public String toString() {
		return "ImpsTransactionReport [response=" + response + ", errorMsg=" + errorMsg + ", impsTrnReport="
				+ impsTrnReport + ", agencybankingTrn=" + agencybankingTrn + ", agencybankingTrnSummury="
				+ agencybankingTrnSummury + "]";
	}
}
