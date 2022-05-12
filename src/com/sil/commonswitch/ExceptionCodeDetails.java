package com.sil.commonswitch;


public class ExceptionCodeDetails{
	
	private Long exceptionCode;
	private Double exceptionAmount;
	
	public ExceptionCodeDetails(){
		
	}

	public ExceptionCodeDetails(Long exceptionCode,Double exceptionAmount){
		this.exceptionCode = exceptionCode;
		this.exceptionAmount = exceptionAmount;
	}

	public Long getExceptionCode() {
		return exceptionCode;
	}

	public void setExceptionCode(Long exceptionCode) {
		this.exceptionCode = exceptionCode;
	}

	public Double getExceptionAmount() {
		return exceptionAmount;
	}

	public void setExceptionAmount(Double exceptionAmount) {
		this.exceptionAmount = exceptionAmount;
	}
}