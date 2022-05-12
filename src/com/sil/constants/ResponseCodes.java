package com.sil.constants;
public class ResponseCodes {
    public static final String TIME_OUT = "91" ; //  05
    public static final String SYSTEM_ERROR = "93";
    public static final String INVALID_PIN = "55"; //55 Orginal value was 13
    public static final String INVALID_ACCOUNT_IFSC_M1="M1";
    public static final String INSUFFICIENT_FUNDS="51";
    public static final String ALREADY_DONE_THIS_TRANSACTION="94";
    public static final String SUCCESS="00";
    public static final String EXEED_DAILY_LIMIT="M2";
    public static final String CARD_NOT_FOUND="14";
    public static final String ACCOUNT_FREEZED="M3";
    public static final String NRE_ACCOUNT="M4";
    public static final String INVALID_NBIN="92";
    public static final String CLOSED_ACCOUNT="M5";
    public static final String CARD_BLOCKED="41";
	public static final String ACC_NOT_FOUND = "M1";
	public static final String BEN_ACC_NOT_FOUND = "M1";
	public static final String ACCOUNT_UNAUTHRISED = "M3";
	public static final String DEPOSIT_TYPE_NOT_FOUND = "52";
	public static final String ACCOUNT_NOT_NORMAL = "M3";
	public static final String NO_RECORD_FOUND = "99";
	public static final String EXCEPTION_OCCURED = "98";
	public static final String INVALID_PARAMS = "11";
	public static final String BILL_PAYMENT_NOT_SUCCESSFUL = "20";
	public static final String SUCCESS_DESC="SUCCESSFUL TRANSACTION";
	public static final String ACCOUNT_ADDR_UNAUTHORISED = "M3";
	public static final String ACC_STAT_CALLED_BACK = "M3";
	public static final String VERIFICATION_RESP_M0 = "M0";

	public static final String SIMSEPAY_SUCCESS = "0";
	public static final String SIMSEPAY_FAILURE = "9";
	public static final String SIMSEPAY_INSUFFICIAENT_BALANCE = "1";
	public static final String SIMSEPAY_CLOSED_ACCOUNT = "2";
	public static final String SIMSEPAY_ACCOUNT_FREEZED = "3";
	public static final String SIMSEPAY_ACCOUNT_LEIN_MASRKED = "4";
//	public static final String TECHNICAL_ERROR = "99";
	
}
