package com.sil.constants;

public enum RtgsNeftHostToHostConstants {
	
	SENT_TO_BENEFICIARY("SENT_TO_BENEFICIARY"),COMPLETED("COMPLETED"),IN_PROCESS("IN_PROCESS"), FAILED("FAILED"), INVALID_IFSC("Invalid Ifsc Code For Benificiary"), 
	INVALID_BEN_ACCOUNT("Invalid Account Number For Benificiary!"), SUCCESS("Success"), IFSC_NOT_BELONG_CUSTOMER_BRANCH("Ifsc Code Not Belong To Customer Branch!"),
	INVALID_ACCOUNT("Invalid Account Number!"), AMOUNT_INSUFFICIENT("Amount Is Insufficient!"), ACCOUNT_NOT_FOUND("Account Not Found."), ACCOUNT_CLOSED("Account Is Closed!"),
	ACCOUNT_INOPPERATIVE("Account Is Inoperative!"), ACCOUNT_DORMANT("Account Is Dormant!"), ACCOUNT_DEBIT_FREEZED("Debit Account Is Debit Freezed!"),
	ACCOUNT_DEBIT_TOTAL_FREEZED("Debit Account Is Total Freezed!"), ACCOUNT_DEBIT_ENTRY_FREEZED("Debit Account Is Entry Freezed!"), 
	ACCOUNT_UNAUTHORISED("Benificiary Account Is Unauthorised!"), ACCOUNT_CREDIT_FREEZED("Benificiary Account Is Credit Freezed!"), ACCOUNT_CREDIT_TOTAL_FREEZED("Benificiary Account Is Total Freezed!"),
	ACCOUNT_CREDIT_ENTRY_FREEZED("Benificiary Account Is Entry Freezed!"), CUSTOMER_NOT_FOUND("Customer Number Not Found!"), INVALID_DEBIT_ACCOUNT("Debit Account Number Not Exist!"),
	DEBIT_ACCOUNT_CLOSED("Debit Account Is Closed!"), DEBIT_ACCOUNT_INOPPERATIVE("Debit Account Is Inoperative!"), DEBIT_ACCOUNT_DORMANT("Debit Account Is Dormant!"),
	DEBIT_ACCOUNT_UNAUTHORISED("Debit Account Is Unauthorised!"), INVALID_CREDIT_ACCOUNT("Benificiary Account Number Not Exist!"), CREDIT_ACCOUNT_CLOSED("Benificiary Account Is Closed!"), 
	CREDIT_ACCOUNT_INOPPERATIVE("Benificiary Account Is Inoperative!"), CREDIT_ACCOUNT_DORMANT("Benificiary Account Is Dormant!"), CREDIT_ACCOUNT_UNAUTHORISED("Benificiary Account Is Unauthorised!"),
	INVALID_PRODUCT_CODE("Invalid Benificiary Product Code"), NO_OPENDATE_FOR_BEN_ACCT("Open Date Not Found For Benificiary Account Number"), 
	NO_BATCH_FOR_BEN_ACCT("Batch Not Found For Benificiary Account Number"),NO_BATCHCODE_FOR_DEF_Batch("Batch code Not Found For defined batch"), NO_MODULE_TYPE_FOR_BEN_ACCT("Module Type Not Found For Benificiary Account Number"), 
	NO_USERCD_FOR_BEN_ACCT("User Code Not Found For Benificiary Account Number"), NO_BOOKTYPE_FOR_BEN_ACCT("Book Type Not Found For Benificiary Account Number"),
	LOAN("30,31"), cflLnTaxCr("LNTAXCR"), cflRevLnPDr("RLNPDR"), wwOthChgDesc("Other Charges"), wwTaxDesc("Penal Int"),
	cflLnOchCr("LNOCHCR"), cflLnIntCr("LNINTCR"), wwIntDesc("Interest"), cflLnPIntCr("LNPINTCR"), wwIntRcvblDesc("Interest Receivable"), cflLnPCr("LNPCR"), wwMainBalDesc("Principal Credit"),
	lnTypeNonCapitalized("1"), lnTypeCapitalized("2"), lnTypeIntReceivable("3"), lnTypeStaffLoan("4"), lnTypeCapitalizedIntRec("5"), lnTypeSimpleIntIntRec("6"), lnTypeOthChgTaxIntRec("10"),
	lnTypeTaxIntRecOthChg("11"), lnTypeCapitalizedIntRecV("13"), lnTypeTaxIntRecOthChg1("15"), lnTypeOthChgTaxIntRec16("16"), lnTypeOthChgTaxIntRec18("18"), lnTypeFarmLoan("20"),
	lnTypeSTCrop("21"), lnTypeLTCrop("22"), lnTypeCapOSExclOthChg("31"), lnTypeHirePurchase("50"), lnTypeIntReceivable1("32"), lnTypeIntReceivable2("33"), lnTypeCapNPAIntPenlOthRecv("34"),
	lnTypeHPLoan("50"),cflLnPrincDr("LNPRNCDR"),cflLnOchDr("LNOCHDR"),cflRevLnOchDr("RLNOCHDR"),actLnTaxDr("LNTAXDR"),actRevLnTaxDr("RLNTAXDR"),cflLnIntDr("cflLnIntDr"),
	cflRevLnIntDr("cflRevLnIntDr"),cflLnPIntDr("LNPINTDR"),cflLnPIntRecDr("LNPRCIDR"),cflRevLnPIntDr("RLNPINDR"),cflLnPRevDr("RLNINTPD"),cflCallBackDr("CALLBKDR"),cflCallBackRevDr("RCALBKDR"),
	cflAwardDr("AWARDDR"),cflAwardRevDr("RAWARDDR"),cflLnNIntDr("LNNINTDR"),cflLnNTaxDr("LNNTAXDR"),cflLnNOchDr("LNNOCHDR"),cflLnIrCr("LNIRCR"),cflLnOChCr("LNOCHCR"),cflCallBackCr("CALLBKCR"),
	cflAwardCr("AWARDCR"),cflLnNIntCr("LNNINTCR"),cflLnNTaxCr("LNNTAXCR"),cflLnNOchCr("LNNOCHCR"),actLnInstlPay("INSTLPAY"),actLnSi("LNSI"),actLnIntRecCr("LNPINTCR"),actLnIntCr("LNINTCR"),
	cflLnPDr("LNPDR"), S200("S200"), RTGS_CUTOFF_AMOUNT("Amount Is Less Than Rtgs Cut Off Amount : "), DUPLICATE_REQUEST("Request With Same Transfer Unique Number Already Completed"),
	VALIDATION_ERROR("Benificiary Detail Is InCorrect"), INVALID_AMOUNT("Amount Should Be Greater Than 0 For Transfer"),
	;
	
	private String message;

	private RtgsNeftHostToHostConstants(String message) {
		this.message = message;
	}
	
	public String getMessage(){
		return message;
	}

}
