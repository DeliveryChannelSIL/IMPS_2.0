package com.sil.loan;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.sil.commonswitch.DataUtils;
import com.sil.commonswitch.VoucherCommon;
import com.sil.constants.RtgsNeftHostToHostConstants;
import com.sil.hbm.D001004;
import com.sil.hbm.D009021;
import com.sil.hbm.D009021Id;
import com.sil.hbm.D009022;
import com.sil.hbm.D009022Id;
import com.sil.hbm.D009040;
import com.sil.hbm.D009042;
import com.sil.hbm.D010004;
import com.sil.hbm.D030002;
import com.sil.hbm.D030003;
import com.sil.hbm.IntrestOffset;
import com.sil.hbm.IntrestRate;
import com.sil.util.DateUtility;
import com.sil.util.HBUtil;
import com.sil.util.Props;

public class LoanServiceImpl {

	private static final Logger logger = LogManager.getLogger(LoanServiceImpl.class.getName());
	
	public String numberOfVoucherOnLoanType(D009022 creditAccount, D009021 creditProductMaster, int lbrCode,
			String creditPrdCd, String creditAcctId, String tType, String narration,
			Double amount, String rrn, int setNo, int mainScrollNo, Session session) throws Exception {
		
		Date openDate = VoucherCommon.getOpenDateNew(lbrCode, session);
		LoanMasterDao loanMasterDao= new LoanMasterDao();
		int usrCode2 = VoucherCommon.getUsrCode("WEB");
		if (usrCode2 == 0) {
			logger.error("UsrCode Not Found.");
			return "True";
		}
		
		System.out.println("tType:>>>" + tType);
		String batchCode = Props.getBatchProperty(tType.trim());
		logger.info("Batch Code Form Properties File : " + batchCode);
		String batchCodes[] = batchCode.split("~");
		logger.info("Number of Batches is " + batchCodes.length + "\t Names are " + Arrays.toString(batchCodes));
		if (batchCodes == null || batchCodes.length < 1) {
			logger.error("Batch Codes Not Found in Properties File.");

			return "true";
		}
		/// Get Selected Batch.
		D010004 selectedBatch = VoucherCommon.getSelectedBatchNew(creditAccount.getId().getLbrCode(), batchCodes, openDate, session);
		if (selectedBatch == null) {
			logger.error("No Active Batch Found.");
			
			return "True";
		}
		
		String bookType =VoucherCommon.getBookTypeNew(lbrCode, selectedBatch.getId().getBatchCd(), session);
		
		D030002 loanParameters = loanMasterDao.getLoanParametersObject(Long.parseLong(lbrCode + ""),
				creditPrdCd);
		String lnType = loanParameters.getLnType() + "";
		D030003 loanBalances = loanMasterDao.getDetailsLoanBalances(lbrCode, creditAcctId,session);
		LoanFundTransfer loanFundTransfer = new LoanFundTransfer();
		loanFundTransfer.setFcy(amount);
		loanFundTransfer.setLcy(amount);
		loanFundTransfer.init(creditAcctId, 1.0, amount, amount);
		String status = "";
		if (lnType.equalsIgnoreCase(RtgsNeftHostToHostConstants.lnTypeNonCapitalized.getMessage())
				|| lnType.equalsIgnoreCase(RtgsNeftHostToHostConstants.lnTypeLTCrop.getMessage())
				|| lnType.equalsIgnoreCase(RtgsNeftHostToHostConstants.lnTypeSTCrop.getMessage()))
			status = tax_OthChgs_Int_Main(creditAccount, creditProductMaster, loanParameters, loanBalances,
					loanFundTransfer, lbrCode, selectedBatch, creditAcctId, narration, amount, rrn + "",
					creditProductMaster.getModuleType(), setNo, bookType, usrCode2, mainScrollNo, session);
		else if (lnType.equalsIgnoreCase(RtgsNeftHostToHostConstants.lnTypeCapitalized.getMessage())
				|| lnType.equalsIgnoreCase(RtgsNeftHostToHostConstants.lnTypeCapNPAIntPenlOthRecv.getMessage())
				|| lnType.equalsIgnoreCase(RtgsNeftHostToHostConstants.lnTypeIntReceivable.getMessage()))
			status = tax_OthChgs_Int_Main(creditAccount, creditProductMaster, loanParameters, loanBalances,
					loanFundTransfer, lbrCode, selectedBatch, creditAcctId, narration, amount, rrn + "",
					creditProductMaster.getModuleType(), setNo, bookType, usrCode2, mainScrollNo, session);
		else if (lnType.equalsIgnoreCase(RtgsNeftHostToHostConstants.lnTypeCapOSExclOthChg.getMessage())
				|| lnType.equalsIgnoreCase(RtgsNeftHostToHostConstants.lnTypeIntReceivable1.getMessage())
				|| lnType.equalsIgnoreCase(RtgsNeftHostToHostConstants.lnTypeIntReceivable2.getMessage())
				|| lnType.equalsIgnoreCase(RtgsNeftHostToHostConstants.lnTypeOthChgTaxIntRec.getMessage())
				|| lnType.equalsIgnoreCase(RtgsNeftHostToHostConstants.lnTypeOthChgTaxIntRec16.getMessage())
				|| lnType.equalsIgnoreCase(RtgsNeftHostToHostConstants.lnTypeOthChgTaxIntRec18.getMessage()))
			status = othChgs_Tax_IntRec_Int_Main(creditAccount, creditProductMaster, loanParameters, loanBalances,
					loanFundTransfer, lbrCode, selectedBatch, creditAcctId, narration, amount, rrn + "",
					creditProductMaster.getModuleType(), setNo, bookType, usrCode2, mainScrollNo, session);
		else if (lnType.equalsIgnoreCase(RtgsNeftHostToHostConstants.lnTypeStaffLoan.getMessage())
				|| lnType.equalsIgnoreCase(RtgsNeftHostToHostConstants.lnTypeFarmLoan.getMessage()))
			status = othChgs_Main_IntRec_Tax(creditAccount, creditProductMaster, loanParameters, loanBalances,
					loanFundTransfer, lbrCode, selectedBatch, creditAcctId, narration, amount, rrn + "",
					creditProductMaster.getModuleType(), setNo, bookType, usrCode2, mainScrollNo, session);
		else if (lnType.equalsIgnoreCase(RtgsNeftHostToHostConstants.lnTypeCapitalizedIntRec.getMessage())
				|| lnType.equalsIgnoreCase(RtgsNeftHostToHostConstants.lnTypeCapitalizedIntRecV.getMessage()))
			status = tax_OthChgs_Int_Main(creditAccount, creditProductMaster, loanParameters, loanBalances,
					loanFundTransfer, lbrCode, selectedBatch, creditAcctId, narration, amount, rrn + "",
					creditProductMaster.getModuleType(), setNo, bookType, usrCode2, mainScrollNo, session);
		else if (lnType.equalsIgnoreCase(RtgsNeftHostToHostConstants.lnTypeSimpleIntIntRec.getMessage()))
			status = tax_OthChgs_Main_Int(creditAccount, creditProductMaster, loanParameters, loanBalances,
					loanFundTransfer, lbrCode, selectedBatch, creditAcctId, narration, amount, rrn + "",
					creditProductMaster.getModuleType(), setNo, bookType, usrCode2, mainScrollNo, session);
		else if (lnType.equalsIgnoreCase(RtgsNeftHostToHostConstants.lnTypeTaxIntRecOthChg.getMessage())
				|| lnType.equalsIgnoreCase(RtgsNeftHostToHostConstants.lnTypeTaxIntRecOthChg1.getMessage()))
			status = tax_IntRec_OthChgs_Main(creditAccount, creditProductMaster, loanParameters, loanBalances,
					loanFundTransfer, lbrCode, selectedBatch, creditAcctId, narration, amount, rrn + "",
					creditProductMaster.getModuleType(), setNo, bookType, usrCode2, mainScrollNo, session);
		return status;

	}
	
	
	public String tax_OthChgs_Int_Main(D009022 creditAccount, D009021 creditProductMaster,
			D030002 loanParameters, D030003 loanBalances, LoanFundTransfer loanFundTransfer, int lbrCode,
			D010004 selectedBatch, String acctId, String narration, Double amount, String rrn,
			short moduleType, int setNo, String bookType, int usrCode2, int mainScrollNo, Session session) throws Exception {

		D009040 instlPaymentTaxVoucher = null, instlPaymentOthChgsVoucher = null, instlPaymentIntVoucher = null,
				instlPaymentMainVoucher = null, instlPaymentIntRecVoucher=null;

		instlPaymentIntRecVoucher = commonLoanVoucher(creditAccount, creditProductMaster, loanParameters, loanBalances,
				loanFundTransfer, lbrCode, selectedBatch, acctId, narration, amount, rrn, moduleType,
				"FillVcrArrForInstlPaymentIntRec", setNo, bookType, usrCode2, mainScrollNo, session);
		
		instlPaymentTaxVoucher = commonLoanVoucher(creditAccount, creditProductMaster, loanParameters, loanBalances,
				loanFundTransfer, lbrCode, selectedBatch, acctId, narration, amount, rrn, moduleType,
				"FillVcrArrForInstlPaymentTax", setNo, bookType, usrCode2, mainScrollNo, session);
		instlPaymentOthChgsVoucher = commonLoanVoucher(creditAccount, creditProductMaster, loanParameters, loanBalances,
				loanFundTransfer, lbrCode, selectedBatch, acctId, narration, amount, rrn, moduleType,
				"FillVcrArrForInstlPaymentOthChgs", setNo, bookType, usrCode2, mainScrollNo, session);
		instlPaymentIntVoucher = commonLoanVoucher(creditAccount, creditProductMaster, loanParameters, loanBalances,
				loanFundTransfer, lbrCode, selectedBatch, acctId, narration, amount, rrn, moduleType,
				"FillVcrArrForInstlPaymentInt", setNo, bookType, usrCode2, mainScrollNo, session);
		instlPaymentMainVoucher = commonLoanVoucher(creditAccount, creditProductMaster, loanParameters, loanBalances,
				loanFundTransfer, lbrCode, selectedBatch, acctId, narration, amount, rrn, moduleType,
				"FillVcrArrForInstlPaymentMain", setNo, bookType, usrCode2, mainScrollNo, session);
		

		return RtgsNeftHostToHostConstants.S200.getMessage();

	}

	public String tax_OthChgs_Main_Int(D009022 creditAccount, D009021 creditProductMaster,
			D030002 loanParameters, D030003 loanBalances, LoanFundTransfer loanFundTransfer, int lbrCode,
			D010004 selectedBatch, String acctId, String narration, Double amount, String rrn,
			short moduleType, int setNo, String bookType, int usrCode2,int mainScrollNo, Session session) throws Exception {

		D009040 instlPaymentTaxVoucher = null, instlPaymentOthChgsVoucher = null, instlPaymentIntVoucher = null,
				instlPaymentMainBalStaffVoucher = null;

		instlPaymentTaxVoucher = commonLoanVoucher(creditAccount, creditProductMaster, loanParameters, loanBalances,
				loanFundTransfer, lbrCode, selectedBatch, acctId, narration, amount, rrn, moduleType,
				"FillVcrArrForInstlPaymentTax", setNo, bookType, usrCode2, mainScrollNo, session);
		instlPaymentOthChgsVoucher = commonLoanVoucher(creditAccount, creditProductMaster, loanParameters, loanBalances,
				loanFundTransfer, lbrCode, selectedBatch, acctId, narration, amount, rrn, moduleType,
				"FillVcrArrForInstlPaymentOthChgs", setNo, bookType, usrCode2, mainScrollNo, session);
		instlPaymentMainBalStaffVoucher = commonLoanVoucher(creditAccount, creditProductMaster, loanParameters,
				loanBalances, loanFundTransfer, lbrCode, selectedBatch, acctId, narration, amount, rrn, moduleType,
				"FillVcrArrForInstlPaymentMainBalStaff", setNo, bookType, usrCode2, mainScrollNo, session);
		instlPaymentIntVoucher = commonLoanVoucher(creditAccount, creditProductMaster, loanParameters, loanBalances,
				loanFundTransfer, lbrCode, selectedBatch, acctId, narration, amount, rrn, moduleType,
				"FillVcrArrForInstlPaymentInt", setNo, bookType, usrCode2, mainScrollNo, session);

		return RtgsNeftHostToHostConstants.S200.getMessage();

	}

	
	public String tax_OthChgs_Main(D009022 creditAccount, D009021 creditProductMaster,
			D030002 loanParameters, D030003 loanBalances, LoanFundTransfer loanFundTransfer, int lbrCode,
			D010004 selectedBatch, String acctId, String narration, Double amount, String rrn,
			short moduleType, int setNo, String bookType, int usrCode2, int mainScrollNo, Session session) throws Exception {

		D009040 instlPaymentTaxVoucher = null, instlPaymentOthChgsVoucher = null, instlPaymentMainVoucher = null;

		instlPaymentTaxVoucher = commonLoanVoucher(creditAccount, creditProductMaster, loanParameters, loanBalances,
				loanFundTransfer, lbrCode, selectedBatch, acctId, narration, amount, rrn, moduleType,
				"FillVcrArrForInstlPaymentTax", setNo, bookType, usrCode2, mainScrollNo, session);
		instlPaymentOthChgsVoucher = commonLoanVoucher(creditAccount, creditProductMaster, loanParameters, loanBalances,
				loanFundTransfer, lbrCode, selectedBatch, acctId, narration, amount, rrn, moduleType,
				"FillVcrArrForInstlPaymentOthChgs", setNo, bookType, usrCode2, mainScrollNo, session);
		instlPaymentMainVoucher = commonLoanVoucher(creditAccount, creditProductMaster, loanParameters, loanBalances,
				loanFundTransfer, lbrCode, selectedBatch, acctId, narration, amount, rrn, moduleType,
				"FillVcrArrForInstlPaymentMain", setNo, bookType, usrCode2, mainScrollNo, session);

		return RtgsNeftHostToHostConstants.S200.getMessage();

	}

	
	public String intRec_Tax_OthChD009022ccountMaster(D009022 creditAccount, D009021 creditProductMaster,
			D030002 loanParameters, D030003 loanBalances, LoanFundTransfer loanFundTransfer, int lbrCode,
			D010004 selectedBatch, String acctId, String narration, Double amount, String rrn,
			short moduleType, int setNo, String bookType, int usrCode2, int mainScrollNo, Session session) throws Exception {

		D009040 instlPaymentIntRecVoucher = null, instlPaymentTaxVoucher = null, instlPaymentOthChgsVoucher = null,
				instlPaymentIntVoucher = null, instlPaymentMainVoucher = null;

		instlPaymentIntRecVoucher = commonLoanVoucher(creditAccount, creditProductMaster, loanParameters, loanBalances,
				loanFundTransfer, lbrCode, selectedBatch, acctId, narration, amount, rrn, moduleType,
				"FillVcrArrForInstlPaymentIntRec", setNo, bookType, usrCode2, mainScrollNo, session);
		instlPaymentTaxVoucher = commonLoanVoucher(creditAccount, creditProductMaster, loanParameters, loanBalances,
				loanFundTransfer, lbrCode, selectedBatch, acctId, narration, amount, rrn, moduleType,
				"FillVcrArrForInstlPaymentTax", setNo, bookType, usrCode2, mainScrollNo, session);
		instlPaymentOthChgsVoucher = commonLoanVoucher(creditAccount, creditProductMaster, loanParameters, loanBalances,
				loanFundTransfer, lbrCode, selectedBatch, acctId, narration, amount, rrn, moduleType,
				"FillVcrArrForInstlPaymentOthChgs", setNo, bookType, usrCode2, mainScrollNo, session);
		instlPaymentIntVoucher = commonLoanVoucher(creditAccount, creditProductMaster, loanParameters, loanBalances,
				loanFundTransfer, lbrCode, selectedBatch, acctId, narration, amount, rrn, moduleType,
				"FillVcrArrForInstlPaymentInt", setNo, bookType, usrCode2, mainScrollNo, session);
		instlPaymentMainVoucher = commonLoanVoucher(creditAccount, creditProductMaster, loanParameters, loanBalances,
				loanFundTransfer, lbrCode, selectedBatch, acctId, narration, amount, rrn, moduleType,
				"FillVcrArrForInstlPaymentMain", setNo, bookType, usrCode2, mainScrollNo, session);

		return RtgsNeftHostToHostConstants.S200.getMessage();
	}

	public String othChgs_Main_IntRec_Tax(D009022 creditAccount, D009021 creditProductMaster,
			D030002 loanParameters, D030003 loanBalances, LoanFundTransfer loanFundTransfer, int lbrCode,
			D010004 selectedBatch, String acctId, String narration, Double amount, String rrn,
			short moduleType, int setNo, String bookType, int usrCode2, int mainScrollNo, Session session) throws Exception {
		D009040 instlPaymentIntRecVoucher = null, instlPaymentTaxVoucher = null, instlPaymentOthChgsVoucher = null,
				instlPaymentMainVoucher = null;

		instlPaymentOthChgsVoucher = commonLoanVoucher(creditAccount, creditProductMaster, loanParameters, loanBalances,
				loanFundTransfer, lbrCode, selectedBatch, acctId, narration, amount, rrn, moduleType,
				"FillVcrArrForInstlPaymentOthChgs", setNo, bookType, usrCode2, mainScrollNo, session);
		Map<String, String> loanOtherDetails = getLoanOtherDetails("ReduceFcyByMainBal", loanParameters, loanBalances,
				loanFundTransfer, session);
		Double e = Double.parseDouble(loanOtherDetails.get("result"));
		instlPaymentIntRecVoucher = commonLoanVoucher(creditAccount, creditProductMaster, loanParameters, loanBalances,
				loanFundTransfer, lbrCode, selectedBatch, acctId, narration, amount, rrn, moduleType,
				"FillVcrArrForInstlPaymentIntRec", setNo, bookType, usrCode2, mainScrollNo, session);
		instlPaymentTaxVoucher = commonLoanVoucher(creditAccount, creditProductMaster, loanParameters, loanBalances,
				loanFundTransfer, lbrCode, selectedBatch, acctId, narration, amount, rrn, moduleType,
				"FillVcrArrForInstlPaymentTax", setNo, bookType, usrCode2, mainScrollNo, session);
		loanFundTransfer.setFcy(loanFundTransfer.getFcy() + e);
		instlPaymentMainVoucher = commonLoanVoucher(creditAccount, creditProductMaster, loanParameters, loanBalances,
				loanFundTransfer, lbrCode, selectedBatch, acctId, narration, amount, rrn, moduleType,
				"FillVcrArrForInstlPaymentMain", setNo, bookType, usrCode2, mainScrollNo, session);

		return RtgsNeftHostToHostConstants.S200.getMessage();

	}

	public String tax_IntRec_OthChgs_Main(D009022 creditAccount, D009021 creditProductMaster,
			D030002 loanParameters, D030003 loanBalances, LoanFundTransfer loanFundTransfer, int lbrCode,
			D010004 selectedBatch, String acctId, String narration, Double amount, String rrn,
			short moduleType, int setNo, String bookType, int usrCode2, int mainScrollNo, Session session) throws Exception {

		D009040 instlPaymentIntRecVoucher = null, instlPaymentTaxVoucher = null, instlPaymentOthChgsVoucher = null,
				instlPaymentMainVoucher = null;

		instlPaymentIntRecVoucher = commonLoanVoucher(creditAccount, creditProductMaster, loanParameters, loanBalances,
				loanFundTransfer, lbrCode, selectedBatch, acctId, narration, amount, rrn, moduleType,
				"FillVcrArrForInstlPaymentIntRec", setNo, bookType, usrCode2, mainScrollNo, session);
		instlPaymentTaxVoucher = commonLoanVoucher(creditAccount, creditProductMaster, loanParameters, loanBalances,
				loanFundTransfer, lbrCode, selectedBatch, acctId, narration, amount, rrn, moduleType,
				"FillVcrArrForInstlPaymentTax", setNo, bookType, usrCode2, mainScrollNo, session);
		instlPaymentOthChgsVoucher = commonLoanVoucher(creditAccount, creditProductMaster, loanParameters, loanBalances,
				loanFundTransfer, lbrCode, selectedBatch, acctId, narration, amount, rrn, moduleType,
				"FillVcrArrForInstlPaymentOthChgs", setNo, bookType, usrCode2, mainScrollNo, session);
		instlPaymentMainVoucher = commonLoanVoucher(creditAccount, creditProductMaster, loanParameters, loanBalances,
				loanFundTransfer, lbrCode, selectedBatch, acctId, narration, amount, rrn, moduleType,
				"FillVcrArrForInstlPaymentMain", setNo, bookType, usrCode2, mainScrollNo, session);

		return RtgsNeftHostToHostConstants.S200.getMessage();

	}

	public String othChgs_Tax_IntRec_Int_Main(D009022 creditAccount, D009021 creditProductMaster,
			D030002 loanParameters, D030003 loanBalances, LoanFundTransfer loanFundTransfer, int lbrCode,
			D010004 selectedBatch, String acctId, String narration, Double amount, String rrn,
			short moduleType, int setNo, String bookType, int usrCode2, int mainScrollNo, Session session) throws Exception {

		D009040 instlPaymentIntRecVoucher = null, instlPaymentTaxVoucher = null, instlPaymentOthChgsVoucher = null,
				instlPaymentIntVoucher = null, instlPaymentMainVoucher = null;

		instlPaymentOthChgsVoucher = commonLoanVoucher(creditAccount, creditProductMaster, loanParameters, loanBalances,
				loanFundTransfer, lbrCode, selectedBatch, acctId, narration, amount, rrn, moduleType,
				"FillVcrArrForInstlPaymentOthChgs", setNo, bookType, usrCode2, mainScrollNo, session);
		instlPaymentTaxVoucher = commonLoanVoucher(creditAccount, creditProductMaster, loanParameters, loanBalances,
				loanFundTransfer, lbrCode, selectedBatch, acctId, narration, amount, rrn, moduleType,
				"FillVcrArrForInstlPaymentTax", setNo, bookType, usrCode2, mainScrollNo, session);
		instlPaymentIntRecVoucher = commonLoanVoucher(creditAccount, creditProductMaster, loanParameters, loanBalances,
				loanFundTransfer, lbrCode, selectedBatch, acctId, narration, amount, rrn, moduleType,
				"FillVcrArrForInstlPaymentIntRec", setNo, bookType, usrCode2, mainScrollNo, session);
		instlPaymentIntVoucher = commonLoanVoucher(creditAccount, creditProductMaster, loanParameters, loanBalances,
				loanFundTransfer, lbrCode, selectedBatch, acctId, narration, amount, rrn, moduleType,
				"FillVcrArrForInstlPaymentInt", setNo, bookType, usrCode2, mainScrollNo, session);
		instlPaymentMainVoucher = commonLoanVoucher(creditAccount, creditProductMaster, loanParameters, loanBalances,
				loanFundTransfer, lbrCode, selectedBatch, acctId, narration, amount, rrn, moduleType,
				"FillVcrArrForInstlPaymentMain", setNo, bookType, usrCode2, mainScrollNo, session);

		return RtgsNeftHostToHostConstants.S200.getMessage();

	}

	public String othChgs_IntRec_Int_Main(D009022 creditAccount, D009021 creditProductMaster,
			D030002 loanParameters, D030003 loanBalances, LoanFundTransfer loanFundTransfer, int lbrCode,
			D010004 selectedBatch, String acctId, String narration, Double amount, String rrn,
			short moduleType, int setNo, String bookType, int usrCode2, int mainScrollNo, Session session) throws Exception {

		D009040 instlPaymentIntRecVoucher = null, instlPaymentOthChgsVoucher = null, instlPaymentIntVoucher = null,
				instlPaymentMainVoucher = null;

		instlPaymentOthChgsVoucher = commonLoanVoucher(creditAccount, creditProductMaster, loanParameters, loanBalances,
				loanFundTransfer, lbrCode, selectedBatch, acctId, narration, amount, rrn, moduleType,
				"FillVcrArrForInstlPaymentOthChgs", setNo, bookType, usrCode2, mainScrollNo, session);
		instlPaymentIntRecVoucher = commonLoanVoucher(creditAccount, creditProductMaster, loanParameters, loanBalances,
				loanFundTransfer, lbrCode, selectedBatch, acctId, narration, amount, rrn, moduleType,
				"FillVcrArrForInstlPaymentIntRec", setNo, bookType, usrCode2, mainScrollNo, session);
		instlPaymentIntVoucher = commonLoanVoucher(creditAccount, creditProductMaster, loanParameters, loanBalances,
				loanFundTransfer, lbrCode, selectedBatch, acctId, narration, amount, rrn, moduleType,
				"FillVcrArrForInstlPaymentInt", setNo, bookType, usrCode2, mainScrollNo, session);
		instlPaymentMainVoucher = commonLoanVoucher(creditAccount, creditProductMaster, loanParameters, loanBalances,
				loanFundTransfer, lbrCode, selectedBatch, acctId, narration, amount, rrn, moduleType,
				"FillVcrArrForInstlPaymentMain", setNo, bookType, usrCode2, mainScrollNo, session);

		return RtgsNeftHostToHostConstants.S200.getMessage();

	}

	public D030003 updateLoanBalancesOnCashFlow(D009040 voucher, D030003 loanBalances, Session session) throws Exception {

		String cf = voucher.getCashFlowType().trim();
		String activity = voucher.getActivityType().trim();
		String storeTotCreditYN = "";
		D001004 creditYN= DataUtils.getSystemParameter(0,  "STORETOTCREDITYN");
		if (creditYN != null)
			storeTotCreditYN = creditYN.getValue().trim();
		Double fcyAmt = voucher.getFcyTrnAmt();
		Double lcyAmt = voucher.getLcyTrnAmt();

		if (cf.equalsIgnoreCase(RtgsNeftHostToHostConstants.cflLnPDr.getMessage())
				|| cf.equalsIgnoreCase(RtgsNeftHostToHostConstants.cflRevLnPDr.getMessage())) {
			if (cf.equalsIgnoreCase(RtgsNeftHostToHostConstants.cflRevLnPDr.getMessage())) {
				fcyAmt = 0 - fcyAmt;
				lcyAmt = 0 - lcyAmt;
			}
			loanBalances.setMainBalFcy(loanBalances.getMainBalFcy() + 0 - fcyAmt);
			loanBalances.setMainBalLcy(loanBalances.getMainBalLcy() + 0 - lcyAmt);
			loanBalances.setDisbursedAmtFcy(loanBalances.getDisbursedAmtFcy() + fcyAmt);
			loanBalances.setDisbursedAmtLcy(loanBalances.getDisbursedAmtLcy() + lcyAmt);
		} else if (cf.equalsIgnoreCase(RtgsNeftHostToHostConstants.cflLnPrincDr.getMessage())) {
			loanBalances.setMainBalFcy(loanBalances.getMainBalFcy() + 0 - fcyAmt);
			loanBalances.setMainBalLcy(loanBalances.getMainBalLcy() + 0 - lcyAmt);
		} else if (cf.equalsIgnoreCase(RtgsNeftHostToHostConstants.cflLnOchDr.getMessage())
				|| cf.equalsIgnoreCase(RtgsNeftHostToHostConstants.cflRevLnOchDr.getMessage())) {
			if (cf.equalsIgnoreCase(RtgsNeftHostToHostConstants.cflRevLnOchDr.getMessage())) {
				fcyAmt = 0 - fcyAmt;
				lcyAmt = 0 - lcyAmt;
			}
			loanBalances.setOthChgPrvdFcy(loanBalances.getOthChgPrvdFcy() + fcyAmt);
			loanBalances.setOthChgLcy(loanBalances.getOthChgLcy() + lcyAmt);
		} else if (cf.equalsIgnoreCase(RtgsNeftHostToHostConstants.actLnTaxDr.getMessage())
				|| cf.equalsIgnoreCase(RtgsNeftHostToHostConstants.actRevLnTaxDr.getMessage())) {
			if (cf.equalsIgnoreCase(RtgsNeftHostToHostConstants.actRevLnTaxDr.getMessage())) {
				fcyAmt = 0 - fcyAmt;
				lcyAmt = 0 - lcyAmt;
			}
			loanBalances.setTaxPrvdFcy(loanBalances.getTaxPrvdFcy() + fcyAmt);
			loanBalances.setTaxLcy(loanBalances.getTaxLcy() + lcyAmt);
		} else if (cf.equalsIgnoreCase(RtgsNeftHostToHostConstants.cflLnIntDr.getMessage())
				|| cf.equalsIgnoreCase(RtgsNeftHostToHostConstants.cflRevLnIntDr.getMessage())) {
			if (cf.equalsIgnoreCase(RtgsNeftHostToHostConstants.cflRevLnIntDr.getMessage())) {
				fcyAmt = 0 - fcyAmt;
				lcyAmt = 0 - lcyAmt;
			}
			loanBalances.setIntPrvdFcy(loanBalances.getIntPrvdFcy() + fcyAmt);
		} else if (cf.equalsIgnoreCase(RtgsNeftHostToHostConstants.cflLnPIntDr.getMessage())
				|| cf.equalsIgnoreCase(RtgsNeftHostToHostConstants.cflLnPIntRecDr.getMessage())) {
			if (cf.equalsIgnoreCase(RtgsNeftHostToHostConstants.cflRevLnPIntDr.getMessage())) {
				fcyAmt = 0 - fcyAmt;
				lcyAmt = 0 - lcyAmt;
			}
			loanBalances.setPenalPrvdFcy(loanBalances.getPenalPrvdFcy() + fcyAmt);
		} else if (cf.equalsIgnoreCase(RtgsNeftHostToHostConstants.cflLnPCr.getMessage())) {
			loanBalances.setMainBalFcy(loanBalances.getMainBalFcy() + fcyAmt);
			loanBalances.setMainBalLcy(loanBalances.getMainBalLcy() + lcyAmt);
		} else if (cf.equalsIgnoreCase(RtgsNeftHostToHostConstants.cflLnIrCr.getMessage())) {
			loanBalances.setMainBalFcy(loanBalances.getMainBalFcy() + fcyAmt);
			loanBalances.setMainBalLcy(loanBalances.getMainBalLcy() + lcyAmt);
			loanBalances.setIntPrvdFcy(loanBalances.getIntPrvdFcy() + fcyAmt);
			loanBalances.setMainBalLcy(loanBalances.getIntPaidFcy() + fcyAmt);
		} else if (cf.equalsIgnoreCase(RtgsNeftHostToHostConstants.cflLnOChCr.getMessage())) {
			loanBalances.setOthChgPaidFcy(loanBalances.getOthChgPaidFcy() + fcyAmt);
			loanBalances.setOthChgLcy(loanBalances.getOthChgLcy() + 0 - lcyAmt);
		} else if (cf.equalsIgnoreCase(RtgsNeftHostToHostConstants.cflLnTaxCr.getMessage())) {
			loanBalances.setTaxPaidFcy(loanBalances.getTaxPaidFcy() + fcyAmt);
			loanBalances.setTaxLcy(loanBalances.getTaxLcy() + 0 - lcyAmt);
		} else if (cf.equalsIgnoreCase(RtgsNeftHostToHostConstants.cflLnIntCr.getMessage())) {
			loanBalances.setIntPaidFcy(loanBalances.getIntPaidFcy() + fcyAmt);
			loanBalances.setIntLcy(loanBalances.getIntLcy() + 0 - lcyAmt);
		} else if (cf.equalsIgnoreCase(RtgsNeftHostToHostConstants.cflLnPIntCr.getMessage()))
			loanBalances.setPenalPaidFcy(loanBalances.getPenalPaidFcy() + fcyAmt);
		else if (cf.equalsIgnoreCase(RtgsNeftHostToHostConstants.cflCallBackCr.getMessage()))
			loanBalances.setInClrgFcy(loanBalances.getInClrgFcy() + 0.0 - fcyAmt);
		else if (cf.equalsIgnoreCase(RtgsNeftHostToHostConstants.cflLnPRevDr.getMessage()))
			loanBalances.setPenalPaidFcy(loanBalances.getPenalPaidFcy() + 0.0 - fcyAmt);
		else if (cf.equalsIgnoreCase(RtgsNeftHostToHostConstants.cflCallBackDr.getMessage()))
			loanBalances.setInClrgFcy(loanBalances.getInClrgFcy() + fcyAmt);
		else if (cf.equalsIgnoreCase(RtgsNeftHostToHostConstants.cflCallBackRevDr.getMessage()))
			loanBalances.setInClrgFcy(loanBalances.getInClrgFcy() + 0 - fcyAmt);
		else if (cf.equalsIgnoreCase(RtgsNeftHostToHostConstants.cflAwardCr.getMessage()))
			loanBalances.setUnClearFcy(loanBalances.getUnClearFcy() + 0 - fcyAmt);
		else if (cf.equalsIgnoreCase(RtgsNeftHostToHostConstants.cflAwardDr.getMessage()))
			loanBalances.setUnClearFcy(loanBalances.getUnClearFcy() + fcyAmt);
		else if (cf.equalsIgnoreCase(RtgsNeftHostToHostConstants.cflAwardRevDr.getMessage()))
			loanBalances.setUnClearFcy(loanBalances.getUnClearFcy() + 0 - fcyAmt);
		else if (cf.equalsIgnoreCase(RtgsNeftHostToHostConstants.cflLnNIntDr.getMessage()))
			loanBalances.setInClrgFcy(loanBalances.getInClrgFcy() + fcyAmt);
		else if (cf.equalsIgnoreCase(RtgsNeftHostToHostConstants.cflLnNIntCr.getMessage()))
			loanBalances.setUnClearFcy(loanBalances.getUnClearFcy() + fcyAmt);
		else if (cf.equalsIgnoreCase(RtgsNeftHostToHostConstants.cflLnNTaxDr.getMessage()))
			loanBalances.setIntLcy(loanBalances.getIntLcy() + fcyAmt);
		else if (cf.equalsIgnoreCase(RtgsNeftHostToHostConstants.cflLnNTaxCr.getMessage()))
			loanBalances.setPenalLcy(loanBalances.getPenalLcy() + fcyAmt);
		else if (cf.equalsIgnoreCase(RtgsNeftHostToHostConstants.cflLnNOchDr.getMessage()))
			loanBalances.setExcessCredit(loanBalances.getExcessCredit() + fcyAmt);
		else if (cf.equalsIgnoreCase(RtgsNeftHostToHostConstants.cflLnNOchCr.getMessage()))
			loanBalances.setReserve2(loanBalances.getReserve2() + fcyAmt);
		else if (storeTotCreditYN.equalsIgnoreCase("Y")) {
			if (voucher.getDrCr() == 'C') {
				if (activity.equalsIgnoreCase(RtgsNeftHostToHostConstants.actLnInstlPay.getMessage())
						|| activity.equalsIgnoreCase(RtgsNeftHostToHostConstants.actLnSi.getMessage())
						|| activity.equalsIgnoreCase(RtgsNeftHostToHostConstants.actLnIntRecCr.getMessage())
						|| activity.equalsIgnoreCase(RtgsNeftHostToHostConstants.actLnIntCr.getMessage())) {
					loanBalances.setTotalCredit(loanBalances.getTotalCredit() + fcyAmt);
				}
			}
		}
		return loanBalances;
	}
	
	public D009040 commonLoanVoucher(D009022 creditAccount, D009021 creditProductMaster,
			D030002 loanParameters, D030003 loanBalances, LoanFundTransfer loanFundTransfer, int lbrCode,
			D010004 selectedBatch, String acctId, String narration, Double amount, String rrn,
			short moduleType, String type, int setNo, String bookType, int usrCode2,int mainScrollNo, Session session) throws Exception {

		D009040 voucher = null;
		int scrollNo;
		Map<String, String> loanOtherDetails = getLoanOtherDetails(type, loanParameters, loanBalances,
				loanFundTransfer, session);

		if (loanOtherDetails != null && !"".equalsIgnoreCase(loanOtherDetails.get("fVcrAcctId").trim()) && !"".equalsIgnoreCase(loanOtherDetails.get("bookType").trim())
				&& !"".equalsIgnoreCase(loanOtherDetails.get("narration").trim())) {
			logger.info(loanOtherDetails.toString());
			if (loanFundTransfer.getfLcyTrnAmt() >= 1) {
				logger.info(loanFundTransfer.getfLcyTrnAmt());
				logger.error(loanFundTransfer.getfLcyTrnAmt());
				scrollNo = VoucherCommon.getNextScrollNo();;
				VoucherUtil voucherUtil =new VoucherUtil();
				LoanMasterDao loanMasterDao = new LoanMasterDao();
				logger.error("Entry into Loan Voucher");
				voucher = voucherUtil.generateVoucher(lbrCode, selectedBatch, acctId, setNo, scrollNo, narration,
						amount, rrn, mainScrollNo, moduleType, bookType, usrCode2);
				logger.error("Exists into Loan Voucher"+ voucher.toString());
				voucher.setVcrAcctId(loanOtherDetails.get("fVcrAcctId"));
				if (!loanOtherDetails.get("bookType").trim().equalsIgnoreCase(""))
					voucher.setCashFlowType(loanOtherDetails.get("bookType"));
				else	voucher.setCashFlowType("");
				voucher.setParticulars(narration + "/" + loanOtherDetails.get("narration"));
				voucher.setFcyTrnAmt(loanFundTransfer.getfFcyTrnAmt());
				voucher.setActivityType("INSTLPAY");
				voucher.setLcyTrnAmt(loanFundTransfer.getfLcyTrnAmt());
				voucher.setLcyConvRate(loanFundTransfer.getfLcyConvRate());
				logger.error("Loan Voucher");
				String refrenceNo1 = DateUtility.getDateFromDateAsString(voucher.getId().getEntryDate(),
						"ddMMyy") + "/" + voucher.getId().getBatchCd() + "/" + voucher.getId().getSetNo() + "/"
						+ voucher.getId().getScrollNo();
				logger.info("refrenceNo1 : " + refrenceNo1);
				loanBalances = updateLoanBalancesOnCashFlow(voucher, loanBalances, session);
				D009022 creditAcctId = session.get(D009022.class, new D009022Id(lbrCode, loanOtherDetails.get("fVcrAcctId")));
				D009021 creditProduct = session.get(D009021.class, new D009021Id(lbrCode, loanOtherDetails.get("fVcrAcctId").substring(0, 8).trim()));
				loanMasterDao.saveVoucherAndUpdateAcount(voucher, creditAcctId, creditProduct, selectedBatch, voucher.getFcyTrnAmt(), "C",session);
				session.saveOrUpdate(loanBalances);
			}
		}
		return voucher;
	}
	
	
	public Map<String, String> getLoanOtherDetails(String paymentType, D030002 loanParameters,
			D030003 loanBalances, LoanFundTransfer loanFundTransfer, Session session) {

		Map<String, String> map = new LinkedHashMap<>();
		String fVcrAcctId = "", bookType = "", narration = "";
		Double result = 0.0;

		if (paymentType.trim().equalsIgnoreCase("FillVcrArrForInstlPaymentTax")) {
			loanFundTransfer.setFcyLcyTrnAmt(loanBalances.getTaxPrvdFcy() - loanBalances.getTaxPaidFcy());
			if (loanFundTransfer.getfFcyTrnAmt() <= 0.0) {
				return null;
			} else {
				bookType = RtgsNeftHostToHostConstants.cflLnTaxCr.getMessage();
				narration = RtgsNeftHostToHostConstants.wwTaxDesc.getMessage();
				if (!loanParameters.getPenalChgDrId().trim().equalsIgnoreCase("000000000000000000000000"))
					fVcrAcctId = loanParameters.getPenalChgDrId().trim();
				else
					fVcrAcctId = loanFundTransfer.getfVcrAcctId();
			}
		} else if (paymentType.trim().equalsIgnoreCase("FillVcrArrForInstlPaymentOthChgs")) {
			loanFundTransfer.setFcyLcyTrnAmt(loanBalances.getOthChgPrvdFcy() - loanBalances.getOthChgPaidFcy());
			if (loanFundTransfer.getfFcyTrnAmt() <= 0.0) {
				return null;
			} else {
				bookType = RtgsNeftHostToHostConstants.cflLnOchCr.getMessage();
				narration = RtgsNeftHostToHostConstants.wwOthChgDesc.getMessage();
				fVcrAcctId = loanFundTransfer.getfVcrAcctId();
			}
		} else if (paymentType.trim().equalsIgnoreCase("FillVcrArrForInstlPaymentInt")) {
			//Added by Aniket Desai For Loan Voucher on 8th Aug, 2019 
			//loanFundTransfer.setFcyLcyTrnAmt(loanBalances.getIntPrvdFcy() - loanBalances.getMainBalLcy());
			loanFundTransfer.setFcyLcyTrnAmt(loanBalances.getIntPrvdFcy() - loanBalances.getIntPaidFcy());
			
			if (loanFundTransfer.getfFcyTrnAmt() <= 0.0) {
				return null;
			} else {
				bookType = RtgsNeftHostToHostConstants.cflLnIntCr.getMessage();
				narration = RtgsNeftHostToHostConstants.wwIntDesc.getMessage();
				fVcrAcctId = loanFundTransfer.getfVcrAcctId();
			}
		} else if (paymentType.trim().equalsIgnoreCase("FillVcrArrForInstlPaymentIntRec")) {
			loanFundTransfer.setFcyLcyTrnAmt(loanBalances.getPenalPrvdFcy() - loanBalances.getPenalPaidFcy());
			if (loanFundTransfer.getfFcyTrnAmt() <= 0.0) {
				return null;
			} else {
				bookType = RtgsNeftHostToHostConstants.cflLnPIntCr.getMessage();
				narration = RtgsNeftHostToHostConstants.wwIntRcvblDesc.getMessage();
				fVcrAcctId = loanParameters.getIrPrdAcctId().trim();
			}
		} else if (paymentType.trim().equalsIgnoreCase("ReduceFcyByMainBal")) {
			loanFundTransfer.setFcyLcyTrnAmt(0.0 - loanBalances.getMainBalFcy());
			loanFundTransfer.setLcy(loanFundTransfer.getLcy() + loanFundTransfer.getfLcyTrnAmt());
			result = loanFundTransfer.getfFcyTrnAmt();
		} else if (paymentType.trim().equalsIgnoreCase("FillVcrArrForInstlPaymentMain")) {
			if (loanFundTransfer.getFcy() > 0) {
				loanFundTransfer.setfFcyTrnAmt(loanFundTransfer.getFcy());
				loanFundTransfer.setfLcyTrnAmt(loanFundTransfer.getLcy());
				bookType = RtgsNeftHostToHostConstants.cflLnPCr.getMessage();
				narration = RtgsNeftHostToHostConstants.wwMainBalDesc.getMessage();
				fVcrAcctId = loanFundTransfer.getfVcrAcctId();
			} else
				return null;
		} else if (paymentType.trim().equalsIgnoreCase("FillVcrArrForInstlPaymentMainBalStaff")) {
			loanFundTransfer.setFcyLcyTrnAmt(0.0 - loanBalances.getMainBalFcy());
			if (loanFundTransfer.getfFcyTrnAmt() <= 0.0) {
				return null;
			} else {
				bookType = RtgsNeftHostToHostConstants.cflLnPCr.getMessage();
				narration = RtgsNeftHostToHostConstants.wwMainBalDesc.getMessage();
				fVcrAcctId = loanFundTransfer.getfVcrAcctId();
			}
		}
		map.put("fVcrAcctId", fVcrAcctId);
		map.put("bookType", bookType);
		map.put("narration", narration);
		map.put("result", result + "");

		return map;

	}
	
	
	public Double getInterestRate(D009042 odlimit,Date AEffDate,int ABranchCd,String AAcctId)
	 {
		 Double intrest = 0.0;
		 Date oppertaionDate=new Date();
			if ((odlimit.getPlrLinkYn() + "").equalsIgnoreCase("N"))
				{
				oppertaionDate = odlimit.getId().getEffFromDate();
				}
			else{
				oppertaionDate = AEffDate;
			    }
			 List<IntrestRate> intrestRateList = getIntrestRateList(
					odlimit.getId().getPrdAcctId().trim().substring(0, 8).trim(), odlimit.getCurCd().trim(),
					oppertaionDate);
			 IntrestRate intrestRate = null;
			 if (intrestRateList != null) {
				 if (intrestRateList != null) {
						Iterator it= intrestRateList.iterator();
						
						while(it.hasNext()){
							 intrestRate=(IntrestRate) it.next();
							intrest = intrestRate.getIntrate();
							 if (odlimit.getTotSancLimit() <= intrestRate.getToamt()){ break;}
						}
				
		            IntrestOffset intrestOffset = getIntrestOffset(AAcctId,
							Long.parseLong(ABranchCd + ""), oppertaionDate);
					if (intrestOffset != null)
						intrest = intrestRate.getIntrate() + intrestOffset.getOffset();
					//oirmOffSetIntRate=intrestOffset.getOffset();
				}
				
				
			}
			return intrest;
	    }
	 
	 
	 
	 	public List<IntrestRate> getIntrestRateList(String prdCd, String curCd, Date intEffDt) {

	 		Session session = HBUtil.getSessionFactory().openSession();
			String hql = "Select new IntrestRate(toamt,intrate) From IntrestRate where id.prdcd=:prdcd and id.curcd=:curcd and id.inteffdt<=:inteffdt Order By id.inteffdt Desc";
			List<IntrestRate> intrestRate=session.createQuery(hql, IntrestRate.class).setParameter("prdcd", prdCd)
					.setParameter("curcd", curCd).setParameter("inteffdt", intEffDt).getResultList();
			
			if (intrestRate.isEmpty()) {
				session.close();
				return null;
			}
			session.close();
			return intrestRate;
		}
		
		
		
		
			public IntrestOffset getIntrestOffset(String prdAcctId, Long lbrCd, Date intEffDt) {

				Session session = HBUtil.getSessionFactory().openSession();
			String hql = "Select new IntrestOffset(offset) From IntrestOffset where id.lbrcode=:lbrcode and id.prdacctid=:prdacctid and id.efffromdate<=:efffromdate Order By id.efffromdate Desc";
			List<IntrestOffset> intrestOffset = session.createQuery(hql, IntrestOffset.class)
					.setParameter("lbrcode", lbrCd).setParameter("prdacctid", prdAcctId)
					.setParameter("efffromdate", intEffDt).getResultList();
			if (intrestOffset.isEmpty()) {
				session.close();
				return null;
			}
			session.close();
			return intrestOffset.get(0);
		}
		
		

}
