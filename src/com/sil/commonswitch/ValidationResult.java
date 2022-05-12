package com.sil.commonswitch;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 
 *  @version 0.1, 18/12/10
 */
@SuppressWarnings("serial")
public class ValidationResult implements Serializable {

	private boolean isValid;
	private String errorCode;
	private String errorMessage;
	private String errorMessage1;
	private StringBuffer warnMsgBuff;
	private boolean isWarn;
	private boolean isSevere;
	private Map<String,Double> exceptionCodes; //(for all & incase of transfer for DR account)
	private Map<String,Double> exceptionCodesCR; // incase of transfer for CR account
	
	//for bug 9009
	private List<MessageBean> warnList = new ArrayList<MessageBean>();
	private List<MessageBean> allMessageList = new ArrayList<MessageBean>();
	private String focusField="";
	
	//for end of day handover
	private List<MessageBean> errorList = new ArrayList<MessageBean>();
	
	public ValidationResult(){
		isValid = true;
		errorCode = "";
		errorMessage = "";
		warnMsgBuff = new StringBuffer();
		isWarn = false;
		isSevere = false;
		exceptionCodes=new HashMap<String, Double>();
		exceptionCodesCR=new HashMap<String, Double>();
	}
	
	
	public List<MessageBean> getErrorList() {
		return errorList;
	}

	public void setErrorList(List<MessageBean> errorList) {
		this.errorList = errorList;
	}

	public Map<String, Double> getExceptionCodesCR() {
		return exceptionCodesCR;
	}
	public void setExceptionCodesCR(Map<String, Double> exceptionCodesCR) {
		this.exceptionCodesCR = exceptionCodesCR;
	}
	public Map<String, Double> getExceptionCodes() {
		return exceptionCodes;
	}
	public void setExceptionCodes(Map<String, Double> exceptionCodes) {
		this.exceptionCodes = exceptionCodes;
	}
	public boolean isValid() {
		return isValid;
	}
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public boolean isWarn() {
		return isWarn;
	}
	public void setWarn(boolean isWarn) {
		this.isWarn = isWarn;
	}
	public boolean isSevere() {
		return isSevere;
	}
	public void setSevere(boolean isSevere) {
		this.isSevere = isSevere;
	}

	public StringBuffer getWarnMsgBuff() {
		return warnMsgBuff;
	}

	public void setWarnMsgBuff(StringBuffer warnMsgBuff) {
		this.warnMsgBuff = warnMsgBuff;
	}

	public List<MessageBean> getWarnList() {
		return warnList;
	}

	public void setWarnList(List<MessageBean> warnList) {
		this.warnList = warnList;
	}	
	public List<MessageBean> getAllMessageList() {
		allMessageList = new ArrayList<MessageBean>();
		if(this.getWarnList() != null && this.getWarnList().size()>0){
			allMessageList.addAll(this.getWarnList());
		}
		/****Added by Shubhra for bug 10433, on Jan. 31, 2014**start***/
		if(this.getErrorList() != null && this.getErrorList().size()>0){
			allMessageList.addAll(this.getErrorList());
		}
		/****Added by Shubhra for bug 10433, on Jan. 31, 2014**end***/
		return allMessageList;
	}
	public void setAllMessageList(List<MessageBean> allMessageList) {
		this.allMessageList = allMessageList;
	}

	public String getFocusField() {
		return focusField;
	}

	public void setFocusField(String focusField) {
		this.focusField = focusField;
	}


	public String getErrorMessage1() {
		return errorMessage1;
	}


	public void setErrorMessage1(String errorMessage1) {
		this.errorMessage1 = errorMessage1;
	}
	
	
}

