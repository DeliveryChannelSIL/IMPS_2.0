package com.sil.exception;

public class WSException extends Exception {
	public WSException(String pMessage) {
		super(pMessage);
	}

	public WSException(String pMessage, Throwable pCause) {
		super(pMessage, pCause);
	}

	public WSException(Throwable pCause) {
		super(pCause);
	}
}
