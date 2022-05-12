package com.sil.domain;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Channelpartnerloginreq {
	private String username;
	private String bcagent;
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getBcagent() {
		return bcagent;
	}

	public void setBcagent(String bcagent) {
		this.bcagent = bcagent;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "ClassPojo [username = " + username + ", bcagent = " + bcagent + ", password = " + password + "]";
	}

	
}