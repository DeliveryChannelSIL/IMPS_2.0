package com.sil.loan;

public class LoanFundTransfer {

	private String fVcrAcctId;
	private Double fLcyConvRate;
	private Double fFcyTrnAmt;
	private Double fLcyTrnAmt;
	private Double fcy;
	private Double lcy;

	public String getfVcrAcctId() {
		return fVcrAcctId;
	}

	public void setfVcrAcctId(String fVcrAcctId) {
		this.fVcrAcctId = fVcrAcctId;
	}

	public Double getfLcyConvRate() {
		return fLcyConvRate;
	}

	public void setfLcyConvRate(Double fLcyConvRate) {
		this.fLcyConvRate = fLcyConvRate;
	}

	public Double getfFcyTrnAmt() {
		return fFcyTrnAmt;
	}

	public void setfFcyTrnAmt(Double fFcyTrnAmt) {
		this.fFcyTrnAmt = fFcyTrnAmt;
	}

	public Double getfLcyTrnAmt() {
		return fLcyTrnAmt;
	}

	public void setfLcyTrnAmt(Double fLcyTrnAmt) {
		this.fLcyTrnAmt = fLcyTrnAmt;
	}

	public Double getFcy() {
		return fcy;
	}

	public void setFcy(Double fcy) {
		this.fcy = fcy;
	}

	public Double getLcy() {
		return lcy;
	}

	public void setLcy(Double lcy) {
		this.lcy = lcy;
	}

	public void init(String fVcrAcctId, Double fLcyConvRate, Double fFcyTrnAmt, Double fLcyTrnAmt) {
		this.fVcrAcctId = fVcrAcctId;
		this.fLcyConvRate = fLcyConvRate;
		this.fFcyTrnAmt = fFcyTrnAmt;
		this.fLcyTrnAmt = fLcyTrnAmt;
	}

	public void setFcyLcyTrnAmt(Double prvdPaidDiff) {
		Double e = 0.0;
		Double fFcyTrnAmt = 0.0;
		Double fLcyTrnAmt = 0.0;
		if (fcy <= 0.0) {
			this.setfFcyTrnAmt(fcy);
		} else {
			e = fcy <= prvdPaidDiff ? fcy : prvdPaidDiff;
			if (e > 0.0) {
				fFcyTrnAmt = e;
				this.setfFcyTrnAmt(fFcyTrnAmt);
				if (fFcyTrnAmt == fcy) {
					fFcyTrnAmt = lcy;
					fLcyTrnAmt = lcy;
				} else {
					fLcyTrnAmt = fFcyTrnAmt;
				}
				this.setfLcyTrnAmt(fLcyTrnAmt);
				fcy = fcy - fFcyTrnAmt;
				lcy = lcy - fLcyTrnAmt;
			} else {
				this.setfFcyTrnAmt(e);
				this.setfLcyTrnAmt(e);
			}
		}

	}

}
