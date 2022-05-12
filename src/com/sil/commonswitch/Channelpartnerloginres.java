package com.sil.commonswitch;

public class Channelpartnerloginres {
	private String sessiontoken;
	private String status;
	private String timeout;
	public String getSessiontoken() {
		return sessiontoken;
	}
	public void setSessiontoken(String sessiontoken) {
		this.sessiontoken = sessiontoken;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTimeout() {
		return timeout;
	}
	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}
	@Override
	public String toString() {
		return "ClassPojo [sessiontoken = " + sessiontoken + ", status = " + status + ", timeout = " + timeout + "]";
	}
}
