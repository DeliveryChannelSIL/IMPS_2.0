package com.sil.constants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.sil.util.DateUtil;
public class SwiftCoreConstants {
	
	public static String SAVING_PRODUCT_CODE="SBPUB"; 
	public static String VOUCHER_CAT="D";
	public static String SCROLL_CAT="SCROLLNO";
	public static String SET_CAT="SETNO";
	public static String LOD_CODE="LASTOPENDATE";
	public static String DEBIT="DR";
	public static String CREDIT="CR";
	public static String RRNNO_CATTYPE = "RRNNO";
	public static String RECON_NO_CAT="O";
	public static String RECON_NO_CATTYPE="ABBRECON";
	public static final String CR = "C";
	public static final String DR = "D";
	public static final String ABBDR = "ABBDR";
	public static final String ABBCR = "ABBCR";
	public static final String ABBOTRFR = "ABBOTRFR";
	public static final String ABBOTRF1 = "ABBOTRF1";
	public static final String ABBSYS = "ABBSYS";
	public static final String ABBTRFR = "ABBTRFR";
	public static final String NFADR = "NFADR";    
	public static final String NFACR = "NFACR";    
	public static final String YES = "Y";
	public static final String NO = "N";
	public static final String VALUE_DATE_FLAG="VALUEDATEFLAG"; 
	public static String PANNOLIMIT = "PANNOLIMIT";
	public static String REMADLIMITFROMDP = "REMADLIMITFROMDP";
	public static final String INTEREST_TRANSFER = "INT-TR"; 
	public static final String ERROR = "ERROR";
	public static String ABBCSLVLDYN="011ABBCSLVLDYN";
	/*batch status*/
	public static Long OPERATIVE=new Long(1);
	public static Long SUSPENDED=new Long(2);
	
	/* voucher stages */
	public static String ENTRY="E";
	public static String POSTING="P";
	public static String FUNDING="F";
	public static String AUTHORIZATION="A";
	public static String CANCELED="C";
	
	/* balance types */
	public static String SHADOWCLEAR="A";
	public static String SHADOWTOTAL="B";
	public static String ACTUALCLEAR="C";
	public static String ACTUALTOTAL="D";
	
	public static String ACCOUNTCLOSE="ACTCLOSE";
	
	public static final String SELFAUTH10A ="10A";
	public static final String SELFAUTH11A ="11A";
	
	/* authorization flags */
	public static Long AUTHORIZED=new Long(0);
	public static Long UNAUTHORIZED=new Long(1);
	public static Long AUTHNEEDED=new Long(0);
	public static Long AUTHDONE=new Long(1);

	/*batch book type*/
	public static String TRANSFER="ZZ";
	public static String CASHBOOK="XX";
	public static String CLEARING="YY";
	public static  final Long OUTWARD_2=2L;
	 

	
	/* Instrument Status flags for D009044*/
	public static String ISSUED="Issued";
	public static String STOP="stopped";
	public static String ENCASHED="Encashed";
	public static String REVOKE="Revoked";

	
	/*activity type*/
	public static final String ACTIVITY_TYPE_ABB = "ABB";
	public static final String ACTIVITY_TYPE_CRABB = "CR-ABB";
	public static final String ACTIVITY_TYPE_DRABB = "DR-ABB";
	public static final String ACTIVITY_TYPE_ABBREM = "ABBREM";
	public static final String ACTIVITY_TYPE_DR = "DR";
	public static final String ACTIVITY_TYPE_CIRD = "CIRD";
	public static final String ACTIVITY_TYPE_CDR = "CDR";
	public static final String ACTIVITY_TYPE_CR = "CR";
	public static final String ACTIVITY_TYPE_NFACASH = "NFACASH";  // by san for vaultMgt
	public static String CurrentAccount="CA";
	public static final String IRD = "IRD";
	public static final String ACTIVITY_TYPE_TDPRCR = "TDPRCR";
	public static final String ACTIVITY_TYPE_TDINSTCR = "TDINSTCR";
	public static final String ACTIVITY_TYPE_TDINSTDR = "TDINSTDR";
	public static final String ACTIVITY_TYPE_INTAPP = "INTAPP";
	public static final String ACTIVITY_TYPE_TDCLOSE = "TDCLOSE";
	public static final String ACTIVITY_TYPE_TDAUTOCE = "TDAUTOCE";
	public static final String ACTIVITY_TYPE_TDUNITWD = "TDUNITWD";
	public static final String ACTIVITY_TYPE_TDINTDR = "TDINTDR";
	public static final String ACTIVITY_TYPE_TDTDSCR = "TDTDSCR";
	public static final String ACTIVITY_TYPE_TDTDSDR = "TDTDSDR";
	public static final String ACTIVITY_TYPE_INTEREST = "INTAPP";
	public static final String ACTIVITY_TYPE_INSTLPAY = "INSTLPAY";
		
	/*voucher entry manager*/
	public static final Long EM_IW_CLG = 3L;
	public static final Long EM_OW_CLG = 2L;
	public static final Long EM_ECS_CLG = 4L;
		
	/* Instrument No. status*/
	public static long ISSUEDSTATUS=0;
	public static long STOCKSTATUS=1;
	public static long STOPPEDPAYMENTSTATUS=1;
	public static long ENCASHEDSTATUS=2;
	public static long RETURNSTATUS=3;
	
	/*Instrument Satus flags for D010010*/
	public static long REVOKESTATUS=1;

	/*Standinginstruction Status for D120001*/
	public static long STATUS=98;
	
	/*DD PO constants*/
	public static final String DD = "DD";
	public static final String PO = "PO";
	public static final String IBT = "IBT";
	public static final String CDSC = "CDSC";
	public static final String DDCTS = "DDCTS";
	public static final String PNCP = "PNCP";
	public static final String ECASH ="ECASH";
	public static final String ICIC = "ICIC";
	public static final String HDFC = "HDFC";
	public static final String SCASH = "SCASH";
	public static final String EXCCASH="EXCCASH";
	
	
	/*System parameters*/
    public static final String REMYEAR = "REMYEAR";
    
    /*TD Constants*/
    public static final String TDCLOSE = "TDCLOSE";
    public static final String FDCLG = "FDCLG";
    public static final String TDPRCR = "TDPRCR";
    
    /*Loan Constants*/
    public static final String LNCLG = "LNCLG";
    
    /*Module Type*/
    public static final Long SAVING = 11L;
    public static final Long CURRENT = 12L;
    public static final Long OD = 14L;
    public static final Long LOAN = 30L;
    public static final Long GL = 99L;
    public static final Long REMITTANCEMODULE = 71L;
    public static final Long TD = 20L;
    public static final Long CENT_OW_CLG = 947L;
    public static final Long ABB = 100L;
    public static final Long TD_LOAN_CLG = 945L;  
    public static final Long IMPORT_BILL = 540L;
    public static final Long IMPORT_BILL_LC = 520L;
    public static final Long FCN = 620L;
    public static final Long BG = 530L;
    public static final String FCBUY = "FCBUY";
    public static final String FCSELL = "FCSELL";
    public static final Long MT_100 = 560L;
    public static final Long BILLS = 60L;
    public static final Long PC = 580L; //Pre-shipment Credit or Packing Credit 
	public static final Long IMPBL = 541L;
    public static final Long FOREIGN_CHEQUE_PURCHASE = 630L;
    public static final Long INTEREST = 972L;
    public static final Long CHEQUE_RETURNS_MODULE = 98L;
    public static final Long LOCKER = 49L;
    public static final Long EXPORT_BILL = 550L;
    public static final Long FURNITURE_FIXTURE_MODULE=940L;
    public static final String CCATTRNS = "CCATTRNS";
    public static final String SL = "SL";
    
    
    /*Module Type List for DR/CR date*/
    public static final ArrayList<Long> moduleTypeListForDRCRDate = new ArrayList<Long>(Arrays.asList(10L, 11L, 12L, 13L, 14L));
    
    /*Module Type List for exception code*/
    public static final ArrayList<Long> moduleTypeListForExceptionCode = new ArrayList<Long>(Arrays.asList(10L, 11L, 12L, 13L, 14L, 20L, 30L,60L,530L,972L));
    
    
    /*Batch Type List */
    public static final ArrayList<String> batchTypeList = new ArrayList<String>(Arrays.asList("50","50W","50S","50SN","50WN"));
    public static final ArrayList<String> batchTypeList60 = new ArrayList<String>(Arrays.asList("60","60W","60S","60SN","60WN"));
    
    /*Remittance parameters*/
    public static final String ISS="ISS";
    public static final String PAID="PAID";
    
    /*Interest Credit*/
    public static final String INTEREST_CREDIT="INTCR";
    public static final String INTEREST_DEBIT="INTDR";
    
    /*For Batch Type*/
    public static final long ENTRYMANAGERIW = 3;
    public static final long ENTRYMANAGEROW = 2;
    
    public static final String DDPRCD = "3646";
    public static final String POPRCD = "3201";
    public static final String SA = "3511";
    public static final String NM = "3512";
    
    public static final String DOYOUWANTTOCONTINUE="Do you want to ignore following warning(s)?";
    
    /*Loan activities*/
    public static final String LNCLOSE = "LNCLOSE";
    public static final String LNPDR = "LNPDR";
    public static final String LNPINTDR = "LNPINTDR";
    public static final String LNTAXDR = "LNTAXDR";
    public static final String LNOCHDR = "LNOCHDR";
    public static final String INTRECDR = "INTRECDR";
    public static final String OTHCHGDR = "OTHCHGDR";
    public static final String INSTLPAY = "INSTLPAY";
    public static final String RDISB = "RDISB";
    public static final String PRNBALDR = "PRNBALDR";
    public static final String ROTCHGDR = "ROTCHGDR";
    public static final String CBLINTRV = "CBLINTRV";
    public static final String LNPRCIDR = "LNPRCIDR";
    public static final String CALLBKCR = "CALLBKCR";
    public static final String LNOCHCR = "LNOCHCR";
    public static final String LNPINTCR = "LNPINTCR";
    public static final String LNINTCR = "LNINTCR";
    public static final String LNPCR = "LNPCR";
    public static final String CBLINTBK = "CBLINTBK";
    public static final String AWDINTBK = "AWDINTBK";
    public static final String AWDINTRV = "AWDINTRV";
    public static final String DISBURSE ="DISBURSE";
    public static final String RLNPDR = "RLNPDR";
    public static final String LNPRNCDR="LNPRNCDR";
    public static final String RLNOCHDR="RLNOCHDR";
    public static final String LNIRCR = "LNIRCR";
    public static final Long STAFFLOAN = 4L;
    public static final String RLNPINDR = "RLNPINDR";
    
   /*Lien Product*/
    public static final String NONLIENPRODUCT = "NOLIENANDPRECLOS";
   
    /*ABB Batch Code*/
    public static final String ABBOSYSTRBH = "ABBOSYSTRBH";
    public static final String ABBNOSYSTRBH="ABBNOSYSTRBH";
    public static final String ABBPSYSTRBH = "ABBPSYSTRBH";
    public static final String ABBNPSYSTRBH = "ABBNPSYSTRBH";
    
    /*DEMAT Batch Code*/
    public static final String DEMATBATCHCD = "DEMATBATCHCD";
    
    /*Account Status*/
    public static final long CALLBACK = 9;
    public static final long AWARDED_BY_GOVT = 18;
    public static final long AWARDED_BY_COURT = 19;
    public static final long AWARDED_REMOTE_CHANCES_OF_RECOVERY = 20;
    public static final Long CLOSED= 3L;
    
    public static final String CALLBACKAWDPRD = "CALLBACKAWDPRD";
    public static final String AWARDCR = "AWARDCR";
    /*OD activities-Amit*/
    public static final String CBLINTBKParticulars ="Booking of Call Back Interest";
    public static final String CBLINTRVParticulars ="Reversal of Call Back Interest";
    public static final String AWDINTBKParticulars ="Booking of Awarded Interest";
    public static final String AWDINTRVParticulars ="Reversal of Awarded Interest";
	public static final String CALLBKDR = "CALLBKDR";
	public static final List<Long> AwdAccountStatusList = new ArrayList<Long>(
			Arrays.asList(18L,19L,20L));
	public static final List<String> odActivitiesList=new ArrayList<String>(Arrays.asList("AWDINTBK","AWDINTRV","CBLINTRV","CBLINTBK"));
	/*OD activities-Amit */
	
	/*Centralized TL/TL Outward Return Process*/
	public static final String CENTORDCON = "CENTORDCON";
	public static final String NFAPRDCD = "NFAPRDCD";
    public static final String PASS = "PASS";
    public static final Long PARTIAL_FUNDING = 4L;
    public static final String ORD = "ORD";
     
    /*TD Voucher Entry*/
    public static final String TDTDSCR = "TDTDSCR";
    public static final String TDINTDR="TDINTDR";    
    public static final String TDTDSDR="TDTDSDR";    
    public static final String TDAUTOCE="TDAUTOCE";
    public static final String TDINSTCR="TDINSTCR";
    public static final String TDINSTDR="TDINSTDR";
    public static final String TDUNITWD="TDUNITWD";
    public static final String TDINTCR="TDINTCR";
    public static final String TDPRDR="TDPRDR";
    public static final String RTDINTDR = "RTDINTDR";
    public static final String RTDINTCR = "RTDINTCR";
    public static final String ATDINTCR = "ATDINTCR";
    public static final String ATDINTDR = "ATDINTDR";
    public static final String FRDSWEEP = "FRDSWEEP";
    
    /*TD Receipt status*/
    public static final Long FUNDEDRECEIPT = 51L;
    public static final Long CLOSEDRECEIPT = 99L;
    public static final Long CANCELEDRECEIPT = 98L;
    

	// Jobs Schedule Batch..
	public static final String SAVINGSINTEREST ="SavingsInterest.Class";
	public static final String ODINTEREST ="ODInterest.Class";
	public static final String STANDINGINSTRUCTION ="StandingInstruction.Class";
	public static final String TDINTEREST ="TDInterest.Class";
	public static final String MINIMUMBALANCECHARGE ="MinimumBalanceCharge.Class";
	public static final String FOLIOCHARGES ="FolioCharges.Class";
	public static final String INWARDCHARGE ="InwardCharge.Class";
	public static final String LOANVOUCHERSERVICEIMPL ="LoanVoucherServiceImpl.Class";
	public static final String TDAUTORENEWAL ="TDAutoRenewal.Class";
	public static final String TDSPROJECTION ="TDSProjection.Class";
	public static final String TDSINITIZATION ="TDSInitialization.Class";
	public static final String LOANIDEALBALANCE ="LoanIdealBalance.Class";
	public static final String LOANINTERESTCALCULATION ="LoanInterest.Class";
	public static final String LOANNOTICSERVICE ="LoanNoticeServiceImpl.Class";
	public static final String UPDATEDPSHARE ="UpdateDpAgainstShareImpl.Class";
	public static final String SWEEPINSWEEPOUT="SweepInSweepOut.Class";
	public static final String DISSOLVEPROCESS ="DISSOLVEPROCESS.Class";
	public static final String WEEKLYCHARGE ="WeeklyChgProcess.Class";
	public static final String IWECSRETURN ="IwECSReturnChg.Class";
	public static final String CENTRALIZEDDAYBEGIN ="DailyBatchesDirectory.Class";
	public static final String CENTRALIZEDDAYENDPROCESS ="DayEndBatch.Class";
	public static final String CENTRALIZEDREOPENDAYPROCESS ="DayReopenBatch.Class";
	public static final String BRANCHHANDOVERPROCESS ="BrHandOverProcess.Class";
	public static final String INWARDCLEARING ="InwardClearingFile.Class";
	public static final String CANCELVOUCHER ="cancelVoucher.Class";
	public static final String NPAIDENTIFICATION ="NPAIdentificationProcess.Class";
	public static final String NPAPROVISIONING ="NPAProvisioning.Class";	
	public static final String FOREXMAILSERVICE="ForexMailServiceImpl.Class";
	public static final String PCINTERESTPROCESS="PCInterestProcess.Class";
	public static final String HOINTPROCESS ="HOInterestServiceImpl.Class";
	public static final String TDSPROJECTION_BANK_WISE ="BankWiseTDSProjection.Class";
	public static final String INTERESTAPPROPRIATIONREPORT ="InterestAppropriationReportServiceImpl.class";
	public static final String ADVANCEOVERDUEPROCESS ="AdvancesOverdueServiceImpl.Class";
	
	//forex processes
	/** 17 DEC 2013, START Modify as per discuss with VAIBHAV SIR **/
//	public static final String TCSTOCKREVALUATION ="TCRevaluationProcess.Class";
	public static final String TCSTOCKREVALUATION ="TCRevaluationProcessServiceImpl.Class";
	/** 17 DEC 2013, END Modify as per discuss with VAIBHAV SIR **/
	public static final String FIRCREMITTANCEPROCESS ="FIRCRemittanceProcess.class";
	public static final String FIRCFORENCHEQUEPROCESS ="FIRCForenChequeProcess.class";
	public static final String DUPLICATEFIRCREMITTANCEPROCESS ="DuplicateFIRCRemittanceProcess.class";
	public static final String DUPLICATEFIRCFORENCHEQUEPROCESS ="DuplicateFIRCForenChequeProcess.class";
	public static final String ECSVOUCHERPROCESSING ="ECSVoucherProcessService.Class";
	
	public static final String DUPLICATEFIRC ="D";
	
	public static final String ALL ="A";
	public static final String SELECT ="S";
	public static final String RANGE ="R";
	public static final String READYSTATUS ="Ready";
	public static final String FAILEDSTATUS ="Failed";
	public static final String NPAPASSVOUCHER ="NPAPassVoucherService.Class";
	public static final String NPAREVERSEVOUCHER ="NPAReverseVoucherService.Class";
	public static final String PASSINTOSVOUCHER ="PassNPAInterestOutstandingVouchers.Class";
	public static final String PAPA1NPAACCOUNT ="NPAPAPA1ReportService.Class";
	public static final String CUSTCATEGORYREPORT ="CustomerCategoryReport.Class";
	public static final String INPROGRESSSTATUS ="In Progress";
	public static final String CLOSEDSTATUS ="Completed";
	public static final String COMPLETEDSTATUS ="Completed";
	public static final String ASBASEQCREATION = "AsbaSQLFileCreation.class";
	public static final String LIENRELEASEFORASBA = "AsbaLeanProcess.class";
	public static final String BGChargesAppropriation = "BGAppropriationServiceImpl.class";
	public static final String ASBAALLOTTMENT = "AsbaAllottmentProcess.class";
	public static final String BillInterestCharges = "BillInterestChargesServiceImpl.class";
	//Modified by Waheed Muhammad as guided by Mr Rushikesh on 29/04/2014
	//public static final String IDLEBALSERVICE = "IdlebalanceServiceImpl.Class";
	public static final String IDLEBALSERVICE = "IdealBalProcess.class";
	public static final String LOCKERSI = "LockerService.Class";
	public static final String LOCKERTRANSFER = "LockerServiceImplforAdvtoRent.Class";
	public static final String CUSTUNIFICATION = "CustomerUnification.Class";
	public static final String BRANCH2BARNCHCOPY="BRtoBRCopy.Class";
	public static final String REACTIVATION="ReActivationService.Class";
	public static final String EMAILACCTSTATEMENT="SendEmailAccountStatementServiceImpl.Class";
	public static final String NRDCSR="NrdScrServiceImpl.Class";
	public static final String POLICYRENWWAL="PoilcyrenewalServiceImpl.class";
	public static final String Insurance_Seq_File_Gen="InsuraceFileGen.process";
	public static final String Trading_Account_Upload ="TradingAcct_Upload.class";
	public static final String Commitment_Charges="LoanDetailsReport.class";
	public static final String ClearingSeqfileExtraction ="ClearingSeqfileExtraction.class";
	//added by Rupesh Pawar for CustomInstructionVoucherProcess
	public static final String  CustomInstructionVoucherProcess="CustomInstructionVoucherProcess.class";
	
	public static final String BLANK_STRING = "";
	public static final long JOINTHOLDER = 1L;
	public static final long AUTHOROZED = 5L;
	
	//Node Master
	public static final Long CASHCOUNTER = 3L;
	public static final Long STATIONTYPE = 1L;
	
	//Customer Type
	public static final Long INDIVIDUALCUSTOMER = 1L;
	public static final Long OTHERCUSTOMER = 2L;
	//for Individual Customer
	public static final Long MINORCUSTOMER = 9L;
	
	//Product Name Address Def
	public static final Long JOINTNAMESSRNO = 2L;
	
	//Customer From 15H
	public static final Long CUSTOMERFORM15H = 2L;
	
	//For State code
	public static final String RBICLGSTATECD = "RBICLGSTATECD";
	
	//for Vault No
	public static final Long VAULTNO = 8001L;
	
	//for ECS
	public static final String OWECSBATCH = "OWECSBATCH";
	public static final String OWECSCRGLHEAD = "OWECSCRGLHEAD";
	public static final String OWECSDRGLHEAD = "OWECSDRGLHEAD";
	public static final String OWECSRBIGLHEAD = "OWECSRBIGLHEAD";
	public static final String OWECSRBIGLHEAD2 = "OWECSRBIGLHEAD2";
	public static final String ECSNPASUSPRD = "030ECSNPASUSPRD";
	public static final String ECSCRBATCH = "ECSCRBATCH";
	public static final String ECSDRBATCH = "ECSDRBATCH";
	public static final String ECSRCR = "ECSRCR";
	public static final String ECSRTDA = "ECSRTDA";
	public static final String ECSCR = "ECSCR";
	public static final String ECSRDR= "ECSRDR";
	public static final String ECSRTNGLHEAD = "ECSRTNGLHEAD";
	public static final String ECSDR = "ECSDR";
	public static final String ECSNFAPRDCD = "ECSNFAPRDCD";
	public static final Long CENTECSTYPECODE = 182111L;
	public static final Long CENTECSREASONCODE = 182006L;
	
	//for DD/PO bank code
	public static final String SARCO = "SARCO";
	
	 /*Reverse Interest Credit*/
    public static final String REV_INTEREST_CREDIT="REVINTCR";
    
    //for vault master
    public static final String CURRENCY = "INR";
    
    //forex voucher
    public static final String FIBNCR = "FIBNCR";
    public static final String FIBNDR = "FIBNDR";
    public static final Long NETTING = 2l;
    public static final Long NO_NETTING = 1l;
    public static final String FBDIRECT = "FBDIRECT";
    public static final String FIBCBOOK = "FIBCBOOK";
    public static final String FIBDISHN = "FIBDISHN";
    public static final String FIBEXTDT = "FIBEXTDT";
    public static final String FIBCREAL = "FIBCREAL";
    public static final String FOREX_RATE_TYPE_STMP = "STMP";
    public static final Long FOREX_HISTORY_RECSTATUS = 99L;
    public static final String FIBCMISC = "FIBCMISC";
    public static final Long CCFOREIGNBANK = 1l;
    public static final Long CCIMPORTER = 2l;
    public static final Long CCSELLER = 3l;
    public static final Long CCBRKUSED = 22l;
    public static final String LCISSUE = "LCISSUE";
    public static final String LCBILLBO = "LCBILLBO";
    public static String LCDEBIT = "LCISSD";
	public static String LCCREDIT = "LCISSC";
	public static final String FIBDR = "FIBDR";
	public static final String FIBCR = "FIBCR";
	public static final String LCCLOSE = "LCCLOSE";
	public static final String LCDUEDT = "LCDUEDT";
	public static final String LCCLOSED = "LCCLOSED";
	public static final String LCMISC = "LCMISC";
	public static final String LCBRECOV = "LCBRECOV";
	public static final String LCBLREVL = "LCBLREVL";
	public static final String LCAMEND = "LCAMEND";
	public static final String LCREAL = "LCREAL";
	public static final String MT100 = "MT100";
	public static final String MTDR = "MTDR";
	public static final String MTCR = "MTCR";
	public static final String LCAMENDD = "LCAMENDD";
	public static final String LCAMENDC = "LCAMENDC";
	public static final String ASSIGN = "ASSIGN";
	public static final String REINSTATE = "REINSTAT";
	
	public static final String RBIADCODE = "RBIADCODE";	
	
    //by sandesh for cheque file generation
    public static final String RBICLGBRANCHCD = "RBICLGBRANCHCD";
    // by sandesh for FQMADU printing
    public static final String FQMADHU = "FQMADHU";
    public static final String DPTYPE_NSDL = "NSDL";
    public static final String DPTYPE_CDSL = "CDSL";
    public static String ASBA_CAT="O";
    public static String ASBA_CATTYPE="ASBAAPNO";
	public static final String TOTALDEBITEXCEEDSCREDIT = "Total Debit Exceeds Credit";
	
	public static final String FR_PRD = "FR";
	
	//for cash vault
	public static String DENSCASHBATCH="DENSCASHBATCH";
	public static Long EXCHNGTOSTNACCEPT = 3L;
	public static Long EXCHNGTOSTNREJECT = 99L;
	public static String DEBITED = "Debited";
	public static String CREDITED = "Credited";
	
	
	//by sandesh on 25 April 2012
	public static String ATMCASH01 = "ATMCASH01";
	public static String ATMCASH02 = "ATMCASH02";
	public static String DEFCASHACCTATM = "DEFCASHACCTATM";
	public static String ATMTRF1 = "ATMTRF1";
	public static String ATMTRF2 = "ATMTRF2";
	
	public static long ATMCARDTEMPBLOCK = 2;
	public static long ATMCARDNORMAL = 1;
	public static long ATMCARDEXPIRED = 4;
	public static long ATMCARDPERMBLOCK = 3;
	public static long ATMCARDLOST = 5;
	public static long AMCPENDINGBLOCKED = 6;
	
	//Saraswat Bank IFSC Code
	public static String IFSCCODESCB = "PASSBOOKIFSCCD";	
	//Last Close Date
	public static String LCD_CODE="LASTCLOSDATE";
	//RBI Bank code
	public static String RBIBANKCD = "RBIBANKCD";
	
	//For IRD Activity
	public static String CHQRTDACCT = "CHQRTDACCT";	
	// added by sandesh 5 may 2012
	public static String ASBAMAXLIMIT="ASBAMAXLIMIT";
	public static String ASBAAPPCNT="ASBAAPPCNT";
	
	//Traveller's Cheque Activity
	public static String TCISS = "TCISS";
	public static String TCRECON = "TCRECON";
	public static String TCSTK = "TCSTK";
	public static String TCSTKRVS = "TCSTKRVS";
	public static Long TCMODULETTYPE = 670L;
	
	//For Passbook Printing
	public static String BALANCEBROUGHTFORWARD="Balance Brought Forward ";
	public static String BALANCECARRIEDFORWARD="Balance Carried Forward ";
	public static String BYOPENINGBALANCE="By Opening Balance";
	public static String SBJAN = "SBJAN";
	
	//BG Voucher
	public static final String BGCLAIM="BGCLAIM";
	public static final String BGISSUE="BGISSUE";
	public static final String BGEXPIRY="BGEXPIRY";
    public static final String BGPREMAT="BGPREMAT";
    public static final String BGAMENDM="BGAMENDM";
    public static final String BGINVOKE="BGINVOKE";
    public static final String BGRECOVR="BGRECOVR";
    
	//LC Status
	public static final Long LCSTATUS_BOOK = 1L;
	public static final Long LCSTATUS_OPEN = 11L;
	public static final Long LCSTATUS_ISSUE = 21L;
	public static final Long LCSTATUS_HALFAMEND = 49L;
	public static final Long LCSTATUS_AMEND = 70L;
	public static final Long LCSTATUS_REAL = 80L;
	public static final Long LCSTATUS_REVERSAL = 71L;
	public static final Long LCSTATUS_HISTCANCEL = 99L;
	public static final Long LCSTATUS_CANCELLED = 99L;
	public static final Long LCSTATUS_CLOSE = 10L;
	public static final Long LCSTATUS_BOOKUNDERLC = 2L;
	public static final Long ZERO = 0L;
	
	//Import Bill Status
	public static final Long FIBBILLBOOKULC = 33L;
	
	//for visa card
	public static String VISA_CARD_CAT="O";
	public static String VISA_CARD_CATTYPE="VAPPNO";
	public static String SYSCASH="SYSCASH";
	
	//Sauarabh
	public static String CAT_TYPE_TC_ISSUE="670BLNO";
	public static String CAT_TYPE_FCN="620BLNO";
	
	//LC Realization
	public static final String FIMPBILL_WITHHOLDING_TAX_PARMETER="FIMPBILLWHTRATE";
	
	
	public static final String NRDCSRRBICD="NRDCSRRBICD";
	
	//od reversal
	public static final List<String> intReversalODActivities=new ArrayList<String>(Arrays.asList("AWDINTRV","CBLINTRV"));
	public static final Long SICHARGETYPE = 14L;
	
	//BILLS Voucher
	public static final String OBMARPUR="OBMARPUR";
	public static final String OBREAL="OBREAL";
	public static final String OBCOLL="OBCOLL";
	public static final String OBPURC="OBPURC";
	public static final String OBLCBKRL="OBLCBKRL";
	public static final String OBLCBKPC="OBLCBKPC";
	//Pre-shipment Credit or Packing Credit
	public static final String PCCRYREC = "PCCRYREC";
	public static final String PCCRYST = "PCCRYST";
	public static final String PCDISB = "PCDISB";
	public static final String PCEXTDT = "PCEXTDT";
	public static final String PCLUTIL = "PCLUTIL";
	public static final String PCRECOVR = "PCRECOVR";
	public static final String PCDR = "PCDR";
	public static final String PCCR = "PCCR";
	public static final String PCCHGDR = "PCCHGDR";
	public static final String PCCHGCR = "PCCHGCR";
	public static final Long PCRECSTATUS = 1L;
	public static final String NOSTROCR = "NOSTROCR";
	public static final Long PCHISTORY_RECSTATUS =99L;
	public static final String PCL = "PCL";
	public static final String PCF = "PCF";
	public static final String NOSTRODR = "NOSTRODR";
	public static final String PCINTCR = "PCINTCR";
	public static final String PCECGC ="PCECGC";
	
	public static final String PCINT = "PCINT";
	public static final String PCINTDR = "PCINTDR";
	
	public static final String PCUTIL = "PCUTIL";
	
	public static final String BATCH ="B";
	public static final String OLTP ="O";

	//For PC Disbursement Details
	public static final String PCWCCYCLE = "PCWCCYCLE";
	public static final String ELCEXDAYS = "ELCEXDAYS";

	//For All PDfs
	public static final String BankServiceTaxNo ="AAACT5543LST081 (Category: Banking And Financial Services.)";
	public static final String BankPanNo ="AABAT4497Q";
	
	//Foreign Cheques
	public static final String FCADV = "FCADV";
	public static final String COLLTPUR = "COLLTPUR";
	public static final String FCDISHR = "FCDISHR";
	public static final String FCPURC = "FCPURC";
	public static final String FCCOLL = "FCCOLL";
	public static final String FCMISC = "FCMISC";
	public static final String FCREAL = "FCREAL";
	public static final String FCDREAL = "FCDREAL";
	public static final String FCCHQ = "FCCHQ"; 
	public static final String FCHCR = "FCHCR";
	public static final String BY = "BY";
	public static final String PURCHASE = "P";
	public static final String COLLECTION = "C";
	public static final Long FOREIGN_CHEQUES_RECSTATUS = 99L;
	public static final String FOREX_BATHCODE = "20FX";
    //Forex Export Collection Advice Constant
	public static final String COLLECTIONADVICE_FOBCOLL = "FOBCOLL";
	public static final Long COLLECTIONADVICE_RECSTATUS = 99L;
	public static final String CHANGE_DUE_DATE = "CHGDUEDT";
	
    //Forex Export Purchase Advice Constant
	public static final String PURCHASEADVICE_FOBPURC = "FOBPURC";
	public static final Long PURCHASEADVICE_RECSTATUS = 99L;
	public static final Long PURCHASEADVICE_INTRECOVERAMT=0L;
	public static final String PURCHASEADVICE_MAINACTION ="P";
	
	
	 //Forex Export Purchase Collection Advice Constant
	public static final Long PURCHASECOLLECTIONADVICE_RECSTATUS = 99L;
	public static final String PURCHASECOLLECTIONADVICE_ACTIVITY = "COLTOPUR";
	
	
	//Forex Export Crystallisation Advice Constant
	public static final Long CRYSTADVICE_RECSTATUS = 99L;
	public static final String CRYSTADVICE_FOBCRYST = "FOBCRYST";
	
	//Forex Export Crystallisation Reallisation Advice Constant
	public static final String FORELREV_ACTIVITY = "FORELREV";
	public static final Long CRYSTREAL_RECSTATUS = 99L;
	public static final String CRYSTREAL_CR ="Cr";
	public static final String CRYSTREAL_CR_CAPS ="CR";
	public static final String CRYSTREAL_DR_CAPS ="DR";
	
	
	//Forex Export Drawee change Advice Constant
	public static final Long DRAWEECHANGE_RECSTATUS = 99L;
	public static final String DRAWEECHANGE_CHGDRWEE ="CHGDRWEE";
	public static final String DRAWEECHANGE_D ="D";
	public static final String DRAWEECHANGE_B ="B";
	public static final String DRAWEECHANGE_T ="T";
	
	//Forex Export Misc. charges Advice Constant
	public static final Long MISCE_RECSTATUS = 99L;
	public static final String MISCE_FOBMISC ="FOBMISC";
	
	//Forex Export DueDate change Advice Constant
	public static final Long DUEDATECHANGE_RECSTATUS = 99L;
	public static final String DUEDATECHANGE_CHGDUEDT ="CHGDUEDT";
	
	//Forex Export FOCOReal Advice Constant
	public static final String FOCOREALADV_ACTIVITY = "FOCOREAL";
	public static final Long FOCOREALADV_RECSTATUS = 99L;
	public static final Long FOCOREALADV_FULLREAL = 51L;
	public static final Long FOCOREALADV_FULLREAL_52 = 52L;
	public static final Long FOCOREALADV_PARTREAL = 53L;
	
	//Forex Export Local Recovery Advice Constant
	public static final Long LOCALREV_RECSTATUS = 99L;
	public static final String LOCALREV_FOBOVDUE ="FOBOVDUE";
	public static final String LOCALREV_Y ="Y";
	public static final String LOCALREV_N ="N";
	public static final String INTSIGNTYPE_MINUS ="-";
	public static final String INTSIGNTYPE_PLUS ="+";
	
	//Forex Export Cross Currency Realization Advice Constant
	public static final String CROSSCURREAL_FOBREALC = "FOBREALC";
	
	
	public static String errorMessage="ERROR";
	public static String warningMessage="WARNING";
	public static String confMessage="CONFORMATION";
	public static String information="INFORMATION";
	
	//RTGS Message Type
	public static String R41 = "R41";
	public static String R42 = "R42";
	public static String N06 = "N06";
	public static String N02 = "N02";
	public static String N02_RETURN = "N02_RETURN";
	public static Long RTGSNEFTCD_STATUS=9L;
	public static String RTGS = "RTGS";
	public static String NEFT = "NEFT";
	
	//station no for vault
	public static final Long STATIONFORVAULT = 9000L;
	public static final List<Long> ODAccountStatusList = new ArrayList<Long>(Arrays.asList(9L,18L,19L,20L));
	
	//Inward clearing batch
	public static final String INWARDBATCHCD="INWARDBATCHCD";
	
	//Product status
	public static final Long FREEZE_BLOCKED_ACCT = new Long(6);
	
	public static final String TODAYSDATE = "946TODAYSDATE";
	public static String ALOWASOFDTRD="ALOWASOFDTRD";
	
	// for RTGS
	public static String NEFTCASHPRD="NEFTCASHPRD";
	public static String NEFTCASHLMT="946NEFTCASHLMT";
	
	//For Database Type
	public static String DATABASE_TYPE ="DATABASE_TYPE";
	
	public static String ORACLE = "ORACLE";
	public static String MSSQL = "MSSQL";
	
	public static String FAILURE = "FAILURE";
	public static String SUCCESS = "SUCCESS";
	public static String PENDING = "PENDING";
	
	public static String ADD = "ADD";
	public static String UPDATE = "UPDATE";
	public static String DELETE = "DELETE";
	
	//For Loanee Surety
	public static String LOANEE = "L";	
	
	//for ImportBill Retirement Memo
	public static final Long IMPBRETIREMENT_RECSTATUS = 99L;
	public static String IMPBRETIREMENT_ACTIVITY="FIBCREAL";
	public static String IMPBRETIREMENT_MLBRKAMTRTTYPE="P1  ";
	public static String IMPBL_ACTIVITY="LCREAL";
		
	//For Misc Charges Advice for Plain Import Bills
	public static final Long IMPBMISCCHARGES_RECSTATUS = 99L;
	public static final String INR = "INR";
	public static String IMPBMISCCHARGES_ACTIVITY="FIBCMISC";
	public static String  IMPBMISCCHARGES_DISHONOURYNREFUND_R="R";
	public static String  IMPBMISCCHARGES_DISHONOURYNCHARGED_C="C";
	public static String  IMPBMISCCHARGES_DISHONOURYNREFUND="Refunded Amount";
	public static String  IMPBMISCCHARGES_DISHONOURYNCHARGED="Charged Amount";
	public static String  IMPBMISCCHARGES_DISHONOURYNREFUND_TOTCRETAMT="Total Credit Amount";
	public static String  IMPBMISCCHARGES_DISHONOURYNREFUND_TOTDEBITAMT="Total Debit Amount";
	public static String  IMPBMISCCHARGES_NETAMT="Net Amount";
	
	//For IMPBL Devolved recovery advice
	public static String DEVOLVEDRECOVERY_ACTIVITY="LCBRECOV";
	
	
	//For IMPBL BOEntry Reminder advice
	public static String IMPBL_LCNO ="0";
	public static String IMPBL_LCBACKYN ="Y";
	
	public static Long IMPBC_FREMSTATUS =0L;
	public static String BOENTRY_FIRSTREMDAYS="FIRSTREMDAYS";
	public static String BOENTRY_SECONDREMDAYS="SECONDREMDAYS";
	
	public static String FIR="FIR";
	public static String FOR="FOR";

	//For PCOverdueReminderAdvice
	public static String DISBAMTRTTYPE="T1  ";
	
		
	
	public static Long  BILLREALISATION_RECSTATUS=99L;
	
	
	//NFA Voucher
	public static String  NFA="NFA";
	
	
	//For Forex Customer Limit Validations
	public static String LC_LIMIT="LC";
	public static String BG_LIMIT="BG";
	public static String PC_LIMIT="PC";
	public static String EXPORT_LIMIT="EXPORT";
	
	// Export Voucher Activity
	public static final String FOBOVERDUE = "FOBOVDUE";
	public static final String SELL = "SL";
	public static final String CHGDRWEE = "CHGDRWEE";
	public static final String FOBCR = "FOBCR";
	public static final String FOBDR = "FOBDR";
	public static final String WRITEOFF = "WRITEOFF";
	public static final String FOBCRYCR = "FOBCRYCR";
	public static final String FOBCRYDR = "FOBCRYDR";
	public static final String FOBCRYST = "FOBCRYST";
	public static final String FOBREAL = "FOBREAL";
	public static final String FORELREV = "FORELREV";
	public static final String FOBMISC = "FOBMISC";
	public static final String COLTOPUR = "COLTOPUR";
	public static final String FOBCOLL = "FOBCOLL";
	public static final String FOCOREAL = "FOCOREAL";
	public static final String FOBPURC = "FOBPURC";
	public static final String FORELREY = "FORELREY";
	public static final String FOBREALC = "FOBREALC";
	public static final String FOBADV = "FOBADV";
	public static final String CHGDUEDT = "CHGDUEDT";
	public static final String FOCR = "FOCR";
	public static final String FODR = "FODR";
	public static final String FBADVDR = "FBADVDR";
	public static final String COLTOEBD = "COLTOEBD";
	public static final String EEFCCOMMRATETYPE = "EEFCCOMMRATETYPE";
	public static final String NORMAL = "N";
	public static final String Penal = "P";
	public static final String Overdue = "O";
	public static final String EBAV = "EBAV";
	public static final String EBAC = "EBAC";
	public static final String EBAN = "EBAN";
	/*Get data base SIZEOFRECORDS*/
	public static final Long LIMITEDRECORDS = 500L;
	
	public static final String FXNFAACCOUNT = "FXNFAACCOUNT";
	public static final String PCLIQUDT = "PCLIQUDT";
	
	/* FOR PC_ECGC PROCESS */
	public static final String PCECGCPREMIUN ="PC_ECGCProcess.class";
	/* FOR PC_INT PROCESS */
	public static final String PCINTEREST ="PackingCreditInterest.class";
	/* FOR EXPORT_INT PROCESS */
	public static final String EXPORTINTEREST ="ExportIntProcess.class";
	public static final String PSECGCPREMIUM="PS_ECGCProcess.class";
	public static final String PSECGCPROCESS="PSECGCProcess.Class";
	
	
	public static String ECGCCUSTINT="I";
	public static String ECGCMOD="99";
	
	/* For pcliquidation */
	public static final String EXPBL = "EXPBL";
	public static final String INRPC = "INRPC";

	/* For Forex Limit */
    //For Lc
	public static final String GL_944 = "944";
    public static final String GL_943 = "943";
    public static final String GL_952 = "952";
    public static final String GL_U_681 = "681";
    public static final String GL_S_655 = "655";
    public static final String GL_928 = "928";
    public static final String GL_929 = "929";
    public static final String GL_614 = "614";
    	
  //For Pc
	public static final String GL_INRPC = "INRPC";
    public static final String GL_PCUSD = "PCUSD";
    public static final String GL_PCEUR = "PCEUR";
    public static final String GL_PCGBP = "PCGBP";
    public static final String GL_564   = "564";
    
    //For Export
	public static final String GL_651 = "651";
    public static final String GL_679 = "679";
    public static final String GL_682EUR = "682EUR";
    public static final String GL_682GBP = "682GBP";
    public static final String GL_682USD = "682USD";
    public static final String GL_677 = "677";
	public static final String GL_676 = "676";
	public static final String GL_678 = "678";
	
	// FOR FCCHQ REALIZATION ADVICE 
	public static final String REALIZATIONADVICE_MAINACTION ="C";
	public static final List<String> REALIZATIONADVICE_MAINACTION_EC =new ArrayList<String>(Arrays.asList("C","E"));
	
	public static final String NGRTGSBT ="NGRTGSBT";
	public static final String NGRTGSGL = "NGRTGSGL";
	public static final String NEFTBT = "NEFTBATCH";
	public static final String NEFT_N07_AUTU_RETURN = "N07AUTOYN";
	public static final String RTGS_R04_AUTU_RETURN = "R04AUTOYN";
	public static final String NEFTSGL = "NEFTGL";
	public static final String NEFT_N07_RTN_BATCH = "RTGSOWRTNACCT";
	public static final String RETNBATCH = "RETNBATCH";
	public static final String RTGSHEAD = "946042RTGSHEAD";  
	public static final String RTGSRTNGL = "RTGSRTNGL";
	public static final String NEFTRTNGL = "946049RTGSHEAD";  
	public static final String NONCTS_2010_STOPMSG ="INSTRUMENT STOPPED : CTS 2010";
	public static final String RTGSCONTRA ="RtgsContraServiceImpl.Class";
	public static final String OPERATIVECUSTOMER ="OperativeCustomer.Class";
	public static final String weeklychgscentralize ="WeeklyChgProcess.Class";
	public static final String SCHEDULE_POSTING="SchedulePostingServiceImpl.Class";
	public static final String SCHEDULE_POSTING_FILE_UPLOAD="SchedulePostingFileUploadServiceImpl.Class";
	public static final String LC_BALANCE_REVERSAL_PROCESS="LcBalanceReversal.Class";
	public static final String Vault_Wise_Denomination_Value="VaultWiseDenomination";
	public static final String Exchange_With_Customer_Value="ExchangeWithCustomer";
	public static final String Exchange_With_Customer_Value_New="ExchangeWithCustomerNew";
	public static final String Exchange_With_Station_Value="ExchangeWithStation";
	public static final String Transfer_Denomination_To_Vault_Value="TransferDenominationToVault";
	public static final String Transfer_Denomination_to_Teller_Value="TransferredDenominationtoTeller";
	public static final String Accept_Reject_Cancel_Denomination_exchange_Value="AcceptRejectCancelDenomination";
	public static final String Accept_Reject_Transferred_Denomination_Value="AcceptTransferredDenomination";
	public static final String Transfer_Of_Denomination_From_Vault_Value="TransferDenominationFromVault";
	public static final String Cancel_Transfer_Of_Denomination_To_Station_Value="CancelTransferOfDenominationToStation";
	
	public static final String SCHPAY = "SCHPAY";
	public static final String SCHCR = "SCHCR";
	public static final Double Forex_Grid_INR_PURC_COLLTPUR_ADV = 200000D;
	public static final Long CENTRAL_LBRCODE=56L;
	public static String  REFUND_R="R";
	public static String  CHARGED_C="C";
	public static final String N10_STATUS_PROCESS ="N10StatusProcess.Class";
	public static final String PCDISBDATE="20150331";
	
	public static final Long MT707=4L;
	public static final String MT707_39C="39C";
	public static final String MT707_44A="44A";
	public static final String MT707_44D="44D";
	public static final String MT707_79="79";
	public static final String MT707_72="72";
	
	public static final String CHECKPENDTDLNCLG="CHECKPENDTDLNCLG";
	public static final String CHKPRINTSTATYN="CHKPRINTSTATYN";
	public static final String DENSENABLED="DENSENABLED";
	public static final String DEFCASHACCT="DEFCASHACCT";
	public static final String CHECKFRALLOK="CHECKFRALLOK";
	public static final String CHECKUNSCNSIG="CHECKUNSCNSIG";
	public static final String CHKCRINLOANYN="CHKCRINLOANYN";
	public static final String CHKPNDECSCLGYN="CHKPNDECSCLGYN";
	public static final String SKIPINWCLGPEND="SKIPINWCLGPEND";
	public static final String CHECKUNRECON="CHECKUNRECON";
	public static final String UNSEALDAYS="UNSEALDAYS";
	
	//LOAN SI VOUCHER
	public static final String LNSI="LNSI";
	
	//RateType for Export
	public static final String P1_RATETYPE="P1";
	public static final String sight="S";
	public static final String usance="U";
	
	//SBU - Treasury Branch IFSC Code For NGRTGS
	public static final String SRCB0NGRTGS = "SRCB0NGRTGS";
	public static final String RTGSSUSPENSEACCT = "3493";
	//Date -For DraweeBeneficiaryMaster for maker date
	public static final Date DraweeBeneficiary_MKDATE=DateUtil.getDateFromString("20120401");
	
	//Loan Installment Frequency
	public static final String MONTHLY="M";
	public static final String QUATERLY="Q";
	public static final String HALFYEARLY="H";
	public static final String YEARLY="Y";
	public static final String USER_DEFINED="U";

	//For FOBPURC DDUS RateType Implementation
	public static final String DDUS="DDUS";
	public static final String EBUSD="EBUSD";
	
	public static final String LOCK="LOCK";
	
	//RTGS/NEFT Fld Length
	public static final Long NEFT_LENGTH=34L;
	public static final Long RTGS_LENGTH=130L;
	
	public enum MonthService {
		JANUARY(0),FEBUARY(1),MARCH(2),APRIL(3),MAY(4),JUNE(5),JULY(6),AUGUST(7),
		SEPTEMBER(8),OCTOBER(9),NOVEMBER(10),DECEMBER(11);
		private int value;

		private MonthService(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}
	public static final String SBMMS="SBMMS";
	public static final String INTERNAL="I";
	public static final String OFFICIAL="O";
	
	public static final String NeftBulkUpload_PROCESS="NeftBulkUpload.process";
	//added for document code value
    public static final ArrayList<Long> doc_code_list = new ArrayList<Long>(Arrays.asList(2L,3L,4L,5L,7L));
    
    //ignore prdcd list
    public static final ArrayList<String> IGNORED_PRDCDLIST=new ArrayList<String>(Arrays.asList("021ADD","021ADDX","021CLAIM","021COLL","022","022X","023","023X","043","043X","100","100X","1000","1000X"));
    
    public enum OtherChannelType {
		SMSBANKING(1), INTERNETBANKING(2);
		private int value;

		private OtherChannelType(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}
    
    
    public enum OtherServiceType {
		BLOCK(1), UNBLOCK(2);
		private int value;

		private OtherServiceType(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}
	//CBWTprocessReport added by Rohit 
    public static final String CBWTREPORT = "CBWTprocess.Class";
    public static final String JOBDISCRIPTION = "CBWTProcess";
	//DealRateReport added by Rohit 
    public static final String DEALRATE = "DealRate.class";
	//SocietyReceiptsReport added by Rohit 
    public static final String SOCIETYRECEIPTS = "SocietyReceipts.class";
	//OverdueListingReport added by Rohit 
    public static final String OVERDUELISTING = "OverdueListing.class";
    /*** added by suresh dhakar on 5 feb 2016 for bug# 13188 *** statrt***/ 
    public static final String SOCIETYBILLRECEIPTS = "SocietyBills.class";
    /*** added by suresh dhakar on 5 feb 2016 for bug# 13188 *** End***/
    //CHGDR Activity
	public static final String CHGDR = "CHGDR";
	  //TRAN Activity
	public static final String TRAN = "TRAN";
	
	public static final String STKCR ="STKCR";
	
	
	//Perquisite Report
	public static final String PERQUISITE = "PerquisiteReport.class";
	/*** For CKYCR : Added by Venu ***/
	public static final String Cust_Type="CUST_TYPE";
	public static final String Name_Title="TITLE";
	public static final String Occupation="OCCUPATION";
	public static final String MinorType="MINORTYPE";
	public static final String Address_Type="ADDRESSTYPE";
	public static final String Address_Proof="ADDRESSPROOFI";
	public static final String Us_Reportable="US_REPORTABLE";
	public static final String Other_Reportable="OTHER_REPORTABLE";
	public static final String ProofIdentity1="PROOFIDENTITY1";
	public static final String ProofIdentity2="PROOFIDENTITY2";
	public static final String ProofIdentity3="PROOFIDENTITY3";
	public static final String ProofIdentity4="PROOFIDENTITY4";
	public static final String Identification_Type="IDENTIFICATIONTYPE";

	/** Changes added by Sudarshan Maheshwari */
	/** WS validation Messages */
	public static final String SUCCESSFUL_QUERY = "Successful Query.";
	public static final String ERRORS_REGARDING_CUSTOMER_ID = "Errors Regarding Customer Id.";
	public static final String ERRORS_REGARDING_CUSTOMER_ACCOUNT = "Errors Regarding Customer Account.";
	public static final String PRODUCT_CODE_NOT_FOUND = "Product Code not found.";
	public static final String INVALID_BRANCH_CODE = "InValid Branch code.";
	public static final String ACCOUNT_NOT_MAPPED_UNDER_THIS_CUSTOMER = "Account Not mapped under this customer.";
	public static final String CUSTOMER_NOT_REGISTERED_WITH_MOBILE_BANKING = "Customer Not registered with mobile banking.";
	public static final String ACCOUNT_STATUS_IS_NOT_NORMAL = "Account status is not normal.";
	public static final String ALREADY_REGISTERED_FOR_THIS_CUSTOMERNUM_ACCOUNTNUM = "Already Registered for this CustomerNum / AccountNum";
	public static final String NOT_SINGLE_MMID_PRESENT_FOR_THIS_CUSTOMER = "Not Single MMID present for this customer";
	public static final String RECORD_NOT_FOUND = "Record not found";
	public static final String WEBSERVICE_SERVER_ERROR = "WebService Server error";
	
	/** WS Cheque Status */
	public static final String ISSUED_WS = "Issued";
	public static final String UNPAID_WS = "Unpaid";
	public static final String STOPPED_WS = "Stopped";
	public static final String PAID_WS = "Paid";
	public static final String RETURNED_WS = "Returned";
	public static final String REPRESENTED_WS = "Represented";
	public static final String IBCHQREQLMT = "IBCHQREQLMT";
	
	public static final String LOGIN_PWD = "L";
	public static final String TRAN_PWD = "T";
	public static final String BADLILIMDAY = "BADLILIMDAY";
	
	/** Module Type List for WS Transaction */
    public static final ArrayList<Long> MODULE_TYPE_LIST_FOR_WS_TRANS = new ArrayList<Long>(Arrays.asList(11L, 12L, 13L, 14L));
    public static final String SWEP = "SWEP";
	public static final String MBAPPLIMIT = "MBAPPLIMIT";
	public static final String MBBATCH = "MBBATCH";
	public static final String MBOFFBATCH = "MBOFFBATCH";
	public static final String MBP2MCRACT = "MBP2MCRACT";
	public static final String MBBRANCH = "MBBRANCH";
	public static final String MBRNBRANCH = "MBRNBRANCH";
	public static final String MBRNCRACT = "MBRNCRACT";
	public static final String MBDRBRANCH = "MBDRBRANCH";
	public static final String MBCRACT = "MBCRACT";
	public static final String ATMCHRGBATCH = "ATMCHRGBATCH";
	public static final String ATMCHRGOFFBATCH = "ATMCHRGOFFBATCH";
	public static final String ATMCHRGACCTCD = "ATMCHRGACCTCD";
	public static final String BILLDESKLBRCD = "BILLDESKLBRCD";
	public static final String BILLDESKBATCHCD = "BILLDESKBATCHCD";
	public static final String BADLILIMFUNDTRFR = "BADLILIMFUNDTRFR";
	public static final String SBIACCTCD = "SBIACCTCD";
	public static final String BILLDESKOFFBATCH = "BILLDESKOFFBATCH";
	public static final String BILLDESKACCTCD = "BILLDESKACCTCD";
	public static final String IBACCTCD = "IBACCTCD";
	public static final String EBACCTCD = "EBACCTCD";
	public static final String AVACCTCD = "AVACCTCD";
	public static final String ATACCTCD = "ATACCTCD";
	public static final String PYTMACCTCD = "PYTMACCTCD";
	public static final String IBAUTOCLRSTN = "IBAUTOCLRSTN";
	public static final String IBPWDCHNGPERIOD = "IBPWDCHNGPERIOD";
	public static final String ALLACCTVIEW = "ALLACCTVIEW";
	public static final Long CC = 13L;
	public static final String IBSTATFRDT = "IBSTATFRDT";
	public static final String IBSTOTNODAYS = "IBSTOTNODAYS";
	public static final String PWDCHKSRNO = "PWDCHKSRNO";
	public static final String DISONLAUTVCR = "DISONLAUTVCR";
	public static final String PASSBOOKIFSCCD = "PASSBOOKIFSCCD";
	public static final String INTCUSTFNDLMT = "INTCUSTFNDLMT";
	public static final String IBBATCHCODE = "IBBATCHCODE";
	public static final String IBNBATCHCODE = "IBNBATCHCODE";
	public static final String PAYEE_ADD = "1";
	public static final String PAYEE_UPDATE = "2";
	public static final String PAYEE_DELETE = "3";
	public static final String INTRABNKFNDLMT = "INTRABNKFNDLMT";
	public static final String IBRNPAYCOOLPRD = "IBRNPAYCOOLPRD";
	public static final String IBRNPAYEEDAYLMT = "IBRNPAYEEDAYLMT";
	public static final String IBRN24BY7YN = "IBRN24BY7YN";
	public static final String IBRN24BY7MAXD = "IBRN24BY7MAXD";
	public static final String INTRABNKOFFBATCH = "INTRABNKOFFBATCH";
	public static final String INTRABNKBATCH = "INTRABNKBATCH";
	public static final String MBRNDRACT = "MBRNDRACT";
	public static final String RTGSIBCRACT = "RTGSIBCRACT";
	public static final String RTGSIB24BY7CRACT = "RTGSIB24BY7CRACT";
	public static final String NEFTIBMINAMT = "NEFTIBMINAMT";
	public static final String NEFTIBMAXAMT = "NEFTIBMAXAMT";
	public static final String RTGSIBMINAMT = "RTGSIBMINAMT";
	public static final String RTGSIBMAXAMT = "RTGSIBMAXAMT";
	public static final String RTGSIBBATCH = "RTGSIBBATCH";
	public static final String RTGSIBOFFBATCH = "RTGSIBOFFBATCH";
	public static final String IBREFNOSTART = "IBREFNOSTART";
	public static final String FDIBMAXLMT = "FDIBMAXLMT";
	public static final String GENSUBACTDRYN = "020GENSUBACTDRYN";
	public static final String SETOTPTRNLMT = "SETOTPTRNLMT";
	public static final String SETDAILYTRNLMT = "SETDAILYTRNLMT";
	public static final String NOOFTRNS = "NOOFTRNS";
	public static final String SETOTPEXPTIME = "SETOTPEXPTIME";
	public static final String IMPSREGMBFRCUST = "IMPSREGMBFRCUST";
	public static final String FUNDTRFLIMIT = "FUNDTRFLIMIT";
	public static final String PAYEEPERDAYLMT = "PAYEEPERDAYLMT";
	public static final String FDTRFPDAYLIMIT = "FDTRFPDAYLIMIT";
	public static final String NEW = "New";
	public static final String PROCESSED = "Processed";
	public static final String CANCELLED = "Cancelled";
	public static final String EXPIRED = "Expired";
	public static final String P2UCRBRCODE = "P2UCRBRCODE";
	public static final String P2UCRGLHEAD = "P2UCRGLHEAD";
	public static final String P2UDRBRCODE = "P2UDRBRCODE";
	public static final String P2UDRGLHEAD = "P2UDRGLHEAD";
	public static final String SOURCE_I = "I";
	public static final String REVERSAL = "Rev";
	public static final String MBDRACT = "MBDRACT";
	public static final ArrayList<String> IMPS_ERROR_CODE_LIST = new ArrayList<String>(Arrays.asList("M1", "M2", "M3", "M4", "M5", "M6"));
	public static final String DOT = ".";
	public static final String MPOSBATCH = "MPOSBATCH";
	public static final String MPOSBATCH1 = "MPOSBATCH1";
	public static final String MBBATCH1 = "MBBATCH1";
	
	/** Changes ended by Sudarshan Maheshwari */
	
	//Cash batches for PAN Validation
	public static final ArrayList<String> CASHBATCHES_LIST=new ArrayList<String>(Arrays.asList("10","10A","11","11A"));
	
	//TL Monthly Report added by Rohit 
	public static final String TLMONTHLY = "TLMonthlyOverdue.class";
	
	//Added By Onkar Rane For ODMonthlyReportsGenration...On 18/12/2015
	public static final String ODMONTHLYREPORT = "ODMonthlyOverdue.class";
	
	public static final Long ALLPRODUCTSBRANCH=778L;
	public static final Long FOREXBRANCH990=990L;
	
	public static final String CHKGACCR = "ChkgAcCr";
	public static final String SBPMJD_PRODUCT ="SBPMJD";
	//Added by onkar Rane for NRDCSR Process...
	public static final String BANKCODE="08709501";
	public static final String NAMEOFREPORTING="The Saraswat Co-op. Bank Ltd.";
	public static final String VALIDATIONSTATUSITEM="Validated";
	public static final String REPORTSTATUS="Audited";
	public static final String NAMEOFSIGNATORY="H V SHIRODKAR";
	public static final String DESIGNATIONOFSIGNATORY="CHIEF MANAGER";
	public static final String PLACEOFSIGNATURE="MUMBAI";
	
	//Added by onkar Rane for NACH VRC Process...
	public static final String BATCHCODE="PLINI";
	public static final String TRANSFERCREDITBRANCH="46";
	public static final String ECSNPDRBATCH = "ECSNPDRBATCH";
	public static final String ECSNPCRBATCH = "ECSNPCRBATCH";
	public static final String NPCIECSRTNPRDCD = "NPCIECSRTNPRDCD";
    public static final String NOT_USED_PROCESS = " ";
	
	//For FR Product AutoRenewal 
	public static Long FRPRODUCT_MONTHS=12L;
	public static Long FRPRODUCT_DAYS=365L;
	
	public static final String FIRASSIGNFILE ="FIRAssignFileProcess.class";
	
	//added by Rupesh Pawar share Divident FileUpload Process
	//public static final String shareDeptProductCode="3529";
	
	//added by Rupesh Pawar Share DEPT 
	public static final String SHARE_DEPT_DR_CODE="SHAREDRPRDCD";
	public static final String SHARE_DEPT_CR_CODE="SHARECRPRDCD";
	public static final String SHARE_DEPT_BATCH_CODE="SHAREBTCHCD";
	public static final String SHAREDIVYEAR="SHAREDIVYEAR";
	
	//added by Rupesh Pawar for CustomInstructionVoucherProcess
	public static final String CUSTOM_INSTR_BTCH_CODE="CUSTOMINSTBTCHCD";
	
	public static final String CUR_USD="USD";
	public static final String CUR_GBP="GBP";
	public static final String CUR_EUR="EUR";
		
	public static final String EXPORT_PRN_REALC_FILE_GEN="ExportPRNRealFileGenProcess.class";
	
	public static final String e_BRC_Generation_Process="EbrcGenerationProcess.Class";
	
	public static final String IDPMS_ORM_NOTIFICATION="IdpmsOrmNotificationProcess.class";
	
	public static final String IDPMS_BOE_SETTLEMENT="IdpmsBoeSettlementProcess.class";
	
	/***Venu BY Venu***/
	public static final String DAILY_LIMIT="DAILY_LIMIT";
	public static final String WEEKLY_LIMIT="WEEKLY_LIMIT";
	public static final String WITHDRAWAL_ST_DT="WITHDRAWAL_ST_DT";
	public static final String WITHDRAWAL_ED_DT="WITHDRAWAL_ED_DT";
	public static final String EXCHG_DENOM="EXCHG_DENOM";
	public static final String CA_WEEKLY_LIMIT="CA_WEEKLY_LIMIT";
	public static final String CA_90DAYS="CA_90DAYS";
	
}