package com.sil.domain;

import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class MiniStatementResponse implements Serializable{
	private static final long serialVersionUID = -5375458840576372763L;
	private String transDate;
	private String transNarrative;
	private String transAmount;
	private String transDRCR;
	private String response;
	private String errorMSg;
	
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public String getErrorMSg() {
		return errorMSg;
	}
	public void setErrorMSg(String errorMSg) {
		this.errorMSg = errorMSg;
	}
	private ArrayList<MiniStatementResponse> miniStmts;
	
	public ArrayList<MiniStatementResponse> getMiniStmts() {
		return miniStmts;
	}
	public void setMiniStmts(ArrayList<MiniStatementResponse> miniStmts) {
		this.miniStmts = miniStmts;
	}
	public String getTransDate() {
		return transDate;
	}
	public void setTransDate(String transDate) {
		this.transDate = transDate;
	}
	public String getTransNarrative() {
		return transNarrative;
	}
	public void setTransNarrative(String transNarrative) {
		this.transNarrative = transNarrative;
	}
	public String getTransAmount() {
		return transAmount;
	}
	public void setTransAmount(String transAmount) {
		this.transAmount = transAmount;
	}
	public String getTransDRCR() {
		return transDRCR;
	}
	public void setTransDRCR(String transDRCR) {
		this.transDRCR = transDRCR;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
