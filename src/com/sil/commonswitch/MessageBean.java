package com.sil.commonswitch;

import com.sil.constants.SwiftCoreConstants;

public class MessageBean{
	
	private String msgCode;
	private String msgDescription;
	private String msgType;	
	private boolean errorMessage=false;
	private boolean warningMessage=false;
	private boolean confMessage=false;
	private boolean information=false;
	
	public MessageBean(String msgCode, String msgDescription, String msgType){
		super();
		this.msgCode = msgCode;
		this.msgDescription = msgDescription;
		this.msgType = msgType;
	}

	public String getMsgCode(){
		return msgCode;
	}
	
	public String getMsgDescription(){
		return msgDescription;
	}
	
	public String getMsgType(){
		return msgType;
	}

	public boolean isErrorMessage(){
		if(this.msgType != null &&
				SwiftCoreConstants.errorMessage.equalsIgnoreCase(this.msgType)){
			this.errorMessage = true;
		}else{
			this.errorMessage = false;
		}
		return this.errorMessage;
	}

	public boolean isWarningMessage(){
		if(this.msgType != null &&
				SwiftCoreConstants.warningMessage.equalsIgnoreCase(this.msgType)){
			this.warningMessage = true;
		}else{
			this.warningMessage = false;
		}
		return this.warningMessage;
	}

	public boolean isConfMessage(){
		if(this.msgType != null &&
				SwiftCoreConstants.confMessage.equalsIgnoreCase(this.msgType)){
			this.confMessage = true;
		}else{
			this.confMessage = false;
		}
		return confMessage;
	} 
	public boolean isInformation(){
		if(this.msgType != null &&
				SwiftCoreConstants.information.equalsIgnoreCase(this.msgType)){
			this.information = true;
		}else{
			this.information = false;
		}
		return information;
	} 
}