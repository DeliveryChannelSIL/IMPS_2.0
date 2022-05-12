package com.sil.operation;

import com.sil.domain.IMPSTransactionRequest;

public interface CoreBankingOperation {
	
	
	public String initiateBillPayment(String consumerNo,String opearator,String accNo15digit,String transType,String narration,double amount,String rrn);
	public String reverseBillPayment(String consumerNo,String opearator,String accNo15digit,String transType,String narration,double amount,String rrn);
	public String fundTransferOtherBranch(String accNo15digit, String benAccNo15digit, String transType,String narration, double amount, String rrn,String mob1,String mmid1,String mob2,String mmid2,String tType);
	public String fundTransferOtherBank(String accNo15digit,String transType,String narration, double amount, String rrn,String mob1,String mmid1,String mob2,String mmid2,String tType);
	public String reversedTransactionOtherBank(String accNo15digit,String transType,String narration, double amount, String rrn,String mob1,String mmid1,String mob2,String mmid2,String tType,IMPSTransactionRequest request);
	public String mposVoucher(String accNo15digit,String benAcc,int brCode,int tobrCode,double amount,String narretion,String reqFlag);
	public String reversefundTransfer(String accNo15digit,String benAccNo15digit,String transType,String narration, double amount, String rrn,String mob1,String mmid1,String mob2,String mmid2,String tType);
}
