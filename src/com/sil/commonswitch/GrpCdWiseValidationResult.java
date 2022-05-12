package com.sil.commonswitch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class GrpCdWiseValidationResult implements Serializable{
	
	private boolean valid;
	private boolean severe;
	private boolean warn;
	private boolean forConformation;
	
	private List<MessageBean> errorList = new ArrayList<MessageBean>();
	private List<MessageBean> warningList = new ArrayList<MessageBean>();
	private List<MessageBean> conformationList = new ArrayList<MessageBean>();
	private List<MessageBean> allMessageList = new ArrayList<MessageBean>();
	
	private Map<Long,Double> exceptionCodes; //(for all & incase of transfer for DR account)
	private Map<Long,Double> exceptionCodesCR; // incase of transfer for CR account
	
	private List<ExceptionCodeDetails> multipleExceptionCodes; //(for all & incase of transfer for DR account)
	private List<ExceptionCodeDetails> multipleExceptionCodesCR; // incase of transfer for CR account


	public GrpCdWiseValidationResult(){
		valid = true;
		severe = false;
		warn = false;
		forConformation = false;		
		exceptionCodes=new HashMap<Long, Double>();
		exceptionCodesCR=new HashMap<Long, Double>();
		multipleExceptionCodes = new ArrayList<ExceptionCodeDetails>();
		multipleExceptionCodesCR = new ArrayList<ExceptionCodeDetails>();
	}


	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean isValid) {
		this.valid = isValid;
	}
	
	public boolean isSevere() {
		return severe;
	}
	public void setSevere(boolean severe) {
		this.severe = severe;
	}

	public boolean isWarn() {
		return warn;
	}
	public void setWarn(boolean warn) {
		this.warn = warn;
	}

	public boolean isForConformation() {
		return forConformation;
	}
	public void setForConformation(boolean forConformation) {
		this.forConformation = forConformation;
	}


	public Map<Long, Double> getExceptionCodes() {
		return exceptionCodes;
	}
	public void setExceptionCodes(Map<Long, Double> exceptionCodes) {
		this.exceptionCodes = exceptionCodes;
	}


	public Map<Long, Double> getExceptionCodesCR() {
		return exceptionCodesCR;
	}
	public void setExceptionCodesCR(Map<Long, Double> exceptionCodesCR) {
		this.exceptionCodesCR = exceptionCodesCR;
	}

	public List<MessageBean> getErrorList() {
		return errorList;
	}
	public void setErrorList(List<MessageBean> errorList) {
		this.errorList = errorList;
	}

	public List<MessageBean> getWarningList() {
		return warningList;
	}
	public void setWarningList(List<MessageBean> warningList) {
		this.warningList = warningList;
	}

	public List<MessageBean> getConformationList() {
		return conformationList;
	}
	public void setConformationList(List<MessageBean> conformationList) {
		this.conformationList = conformationList;
	}
	
	public List<MessageBean> getAllMessageList() {
		allMessageList = new ArrayList<MessageBean>();
		if(this.getErrorList() != null && this.getErrorList().size()>0){
			allMessageList.addAll(this.getErrorList());
		}
		if(this.getWarningList() != null && this.getWarningList().size()>0){
			allMessageList.addAll(this.getWarningList());
		}
		if(this.getConformationList() != null && this.getConformationList().size()>0){
			allMessageList.addAll(this.getConformationList());
		}
		
		return allMessageList;
	}
	public void setAllMessageList(List<MessageBean> allMessageList) {
		this.allMessageList = allMessageList;
	}
//End of class
//------------------------------------------------------------------------------


	public List<ExceptionCodeDetails> getMultipleExceptionCodes() {
		return multipleExceptionCodes;
	}


	public void setMultipleExceptionCodes(
			List<ExceptionCodeDetails> multipleExceptionCodes) {
		this.multipleExceptionCodes = multipleExceptionCodes;
	}


	public List<ExceptionCodeDetails> getMultipleExceptionCodesCR() {
		return multipleExceptionCodesCR;
	}


	public void setMultipleExceptionCodesCR(
			List<ExceptionCodeDetails> multipleExceptionCodesCR) {
		this.multipleExceptionCodesCR = multipleExceptionCodesCR;
	}
	/**
	 * 
	 * @date Feb 22,2013
	 * @author Venu
	 * @param tempValidation
	 * @description method for addMultipleException get the MultiExceptions. 
	 * @return 
	 * @version $Revision$
	 */
	public GrpCdWiseValidationResult addMultipleException(GrpCdWiseValidationResult multiValidation){
		if(multiValidation != null){
			if(multiValidation.getExceptionCodes() != null && multiValidation.getExceptionCodes().size()>0){
				Collection<Long> exceptionCds =multiValidation.getExceptionCodes().keySet();
				for(Long objTemp:exceptionCds){
					this.getMultipleExceptionCodes().
					add(new ExceptionCodeDetails(objTemp, multiValidation.getExceptionCodes().get(objTemp)));
				}
			}			
			if(multiValidation.getExceptionCodesCR() != null && multiValidation.getExceptionCodesCR().size()>0){
				Collection<Long> exceptionCdsCR =multiValidation.getExceptionCodesCR().keySet();
				for(Long objCRTemp:exceptionCdsCR){
					this.getMultipleExceptionCodesCR().
					add(new ExceptionCodeDetails(objCRTemp, multiValidation.getExceptionCodesCR().get(objCRTemp)));
				}
			}			
			this.errorList.addAll(multiValidation.errorList);
			this.warningList.addAll(multiValidation.warningList);
			this.conformationList.addAll(multiValidation.conformationList);
			this.allMessageList.addAll(multiValidation.allMessageList);
			if(this.errorList != null && this.errorList.size()>0){
				this.severe = true;
			}
			if(this.warningList != null && this.warningList.size()>0){
				this.warn = true;
			}
			if(this.conformationList != null && this.conformationList.size()>0){
				this.forConformation = true;
			}
			if(this.getAllMessageList() != null && this.getAllMessageList().size()>0){
				this.valid = false;
			}
		}
		return this;
	}

//End of File <GrpCdWiseValidationResult.java>

//------------------------------------------------------------------------------

}
