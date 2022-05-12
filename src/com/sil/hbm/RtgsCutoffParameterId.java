package com.sil.hbm;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the RTGS_CUTOFF_PARAMETER database table.
 * 
 */
@Embeddable
public class RtgsCutoffParameterId implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="MESSAGETYPE")
	private String messagetype;

	@Column(name="WORKINGDAYS")
	private long workingdays;

	public RtgsCutoffParameterId() {
	}
	public String getMessagetype() {
		return this.messagetype;
	}
	public void setMessagetype(String messagetype) {
		this.messagetype = messagetype;
	}
	public long getWorkingdays() {
		return this.workingdays;
	}
	public void setWorkingdays(long workingdays) {
		this.workingdays = workingdays;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RtgsCutoffParameterId)) {
			return false;
		}
		RtgsCutoffParameterId castOther = (RtgsCutoffParameterId)other;
		return 
			this.messagetype.equals(castOther.messagetype)
			&& (this.workingdays == castOther.workingdays);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.messagetype.hashCode();
		hash = hash * prime + ((int) (this.workingdays ^ (this.workingdays >>> 32)));
		
		return hash;
	}
}