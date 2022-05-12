package com.sil.hbm;

import javax.persistence.Column;
import javax.persistence.Embeddable;


@Embeddable
public class D390061Id implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5576192829045634170L;
	private String cardId;
	private short srNo;

	public D390061Id() {
	}

	public D390061Id(String cardId, short srNo) {
		this.cardId = cardId;
		this.srNo = srNo;
	}

	@Column(name = "CardId", nullable = false, length = 30)
	public String getCardId() {
		return this.cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	@Column(name = "SrNo", nullable = false)
	public short getSrNo() {
		return this.srNo;
	}

	public void setSrNo(short srNo) {
		this.srNo = srNo;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof D390061Id))
			return false;
		D390061Id castOther = (D390061Id) other;

		return ((this.getCardId() == castOther.getCardId()) || (this.getCardId() != null
				&& castOther.getCardId() != null && this.getCardId().equals(castOther.getCardId())))
				&& (this.getSrNo() == castOther.getSrNo());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (getCardId() == null ? 0 : this.getCardId().hashCode());
		result = 37 * result + this.getSrNo();
		return result;
	}

}
