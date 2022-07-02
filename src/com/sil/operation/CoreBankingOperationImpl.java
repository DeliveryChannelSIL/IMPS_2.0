package com.sil.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import com.sil.commonswitch.BillPaymentTransactionEntry;
import com.sil.commonswitch.DataUtils;
import com.sil.commonswitch.P2AReversal;
import com.sil.commonswitch.P2ATransaction;
import com.sil.commonswitch.P2PReversal;
import com.sil.commonswitch.P2PTransactionEntry;
import com.sil.commonswitch.VoucherCommon;
import com.sil.constants.Code;
import com.sil.constants.MSGConstants;
import com.sil.constants.ResponseCodes;
import com.sil.domain.GetStatus;
import com.sil.domain.IMPSTransactionRequest;
import com.sil.domain.IMPSTransactionResponse;
import com.sil.hbm.D009022;
import com.sil.hbm.D009022Id;
import com.sil.hbm.D350044;
import com.sil.hbm.D350059;
import com.sil.hbm.SimSePayTrancation;
import com.sil.prop.ConfigurationLoader;
import com.sil.util.AccountDetailsUtil;
import com.sil.util.HBUtil;

public class CoreBankingOperationImpl implements CoreBankingOperation {
	public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(CoreBankingOperationImpl.class);

	@Override
	public String initiateBillPayment(String consumerNo, String opearator, String accNo15digit, String transType,
			String narration, double amount, String rrn) {
		String acctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit);
		int lbrcode = Integer.parseInt(accNo15digit.substring(0, 3));
		logger.error("Account NO 32 Digit::>>" + acctno32digit);
		logger.error("Branch Code::>>" + lbrcode);
		HashMap<String, String> result = VoucherCommon.otherBankVoucherEntry(lbrcode, acctno32digit, "IMPS",
				"Bill Payment/" + opearator, amount, rrn);
		logger.error("Result:>>>" + result);
		if (null != result) {
			if (result.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
				BillPaymentTransactionEntry.transactionEntryBillPay(lbrcode, acctno32digit, "MBTR",
						Integer.valueOf(result.get(Code.SETNO)), Integer.valueOf(result.get(Code.SCROLLNO)), consumerNo,
						opearator, amount, ResponseCodes.SUCCESS, ResponseCodes.SUCCESS_DESC, rrn, "D");
				return ResponseCodes.SUCCESS;
			} else {
				BillPaymentTransactionEntry.transactionEntryBillPay(lbrcode, acctno32digit, "MBTR",
						Integer.valueOf(result.get(Code.SETNO)), Integer.valueOf(result.get(Code.SCROLLNO)), consumerNo,
						opearator, amount, ResponseCodes.SUCCESS, ResponseCodes.SUCCESS_DESC, rrn, "D");
				return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
			}
		}
		return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
	}

	/*
	 * @Author:Amar W. Raut Description: Method for reverse bill payment transaction
	 * 
	 */

	@Override
	public String reverseBillPayment(String consumerNo, String opearator, String accNo15digit, String transType,
			String narration, double amount, String rrn) {
		// TODO Auto-generated method stub
		String acctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit);
		int lbrcode = Integer.parseInt(accNo15digit.substring(0, 3));
		logger.error("Account NO 32 Digit::>>" + acctno32digit);
		logger.error("Branch Code::>>" + lbrcode);
		HashMap<String, String> result = VoucherCommon.otherBankReverseVoucherEntry(lbrcode, acctno32digit, "IMPS",
				"Reverse/ Bill Payment/" + opearator, amount, rrn);
		logger.error("Result:>>>" + result);
		if (null != result) {
			if (result.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
				BillPaymentTransactionEntry.transactionEntryBillPay(lbrcode, acctno32digit, "MBTR",
						Integer.valueOf(result.get(Code.SETNO)), Integer.valueOf(result.get(Code.SCROLLNO)), consumerNo,
						opearator, amount, ResponseCodes.SUCCESS, ResponseCodes.SUCCESS_DESC, rrn, "C");
				return ResponseCodes.SUCCESS;
			} else {
				BillPaymentTransactionEntry.transactionEntryBillPay(lbrcode, acctno32digit, "MBTR",
						Integer.valueOf(result.get(Code.SETNO)), Integer.valueOf(result.get(Code.SCROLLNO)), consumerNo,
						opearator, amount, ResponseCodes.SUCCESS, ResponseCodes.SUCCESS_DESC, rrn, "C");
				return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
			}
		}
		return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
	}

	/*
	 * @Author:Amar W. Raut Common method for same branch and Other Branch voucher
	 * an transaction tType:P2P for P2P Transaction tType:P2A for P2P Transaction
	 */
	@Override
	public String fundTransferOtherBranch(String accNo15digit, String benAccNo15digit, String transType,
			String narration, double amount, String rrn, String mob1, String mmid1, String mob2, String mmid2,
			String tType) {
		// TODO Auto-generated method stub
		if (tType.equalsIgnoreCase("P2P")) {
			String acctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit);
			int lbrcode = Integer.parseInt(accNo15digit.substring(0, 3));
			logger.error("Account NO 32 Digit::>>" + acctno32digit);
			logger.error("Branch Code::>>" + lbrcode);

			String benAcctno32digit = AccountDetailsUtil.get32DigitAcctNo(benAccNo15digit);
			int BenBrcode = Integer.parseInt(benAccNo15digit.substring(0, 3));

			HashMap<String, String> result = VoucherCommon.otherBranchVouchers(lbrcode, acctno32digit, BenBrcode,
					benAcctno32digit, transType, narration, amount, rrn);
			logger.error("Result:>>>" + result);
			if (null != result) {
				if (result.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
					P2PTransactionEntry.p2pTransactionEntry(mmid1, mob1, mob2, mmid2, amount,
							Integer.valueOf(result.get(Code.SETNO)), Integer.valueOf(result.get(Code.SCROLLNO)), "MBTR",
							lbrcode, ResponseCodes.SUCCESS, ResponseCodes.SUCCESS_DESC, rrn);
					return ResponseCodes.SUCCESS;
				} else {
					P2PTransactionEntry.p2pTransactionEntry(mmid1, mob1, mob2, mmid2, amount, Integer.valueOf(
							(result.get(Code.SETNO) == null || result.get(Code.SETNO).trim().equalsIgnoreCase("")) ? "0"
									: result.get(Code.SETNO)),
							Integer.valueOf((result.get(Code.SCROLLNO) == null
									|| result.get(Code.SCROLLNO).trim().equalsIgnoreCase("")) ? "0"
											: result.get(Code.SCROLLNO)),
							"MBTR", lbrcode, "99", "Transaction Failure", rrn);
					return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
				}
			}
			return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
		} else if (tType.equalsIgnoreCase("P2A")) {
			String acctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit);
			int lbrcode = Integer.parseInt(accNo15digit.substring(0, 3));
			logger.error("Account NO 32 Digit::>>" + acctno32digit);
			logger.error("Branch Code::>>" + lbrcode);
			String benAcctno32digit = AccountDetailsUtil.get32DigitAcctNo(benAccNo15digit);
			int BenBrcode = Integer.parseInt(benAccNo15digit.substring(0, 3));
			HashMap<String, String> result = VoucherCommon.otherBranchVouchers(lbrcode, acctno32digit, BenBrcode,
					benAcctno32digit, transType, narration, amount, rrn);
			logger.error("Result:>>>" + result);
			if (null != result) {
				if (result.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
					P2ATransaction.p2aTransactionEntry(mmid1, mob1, mob2, mmid2, amount,
							Integer.valueOf(result.get(Code.SETNO)), Integer.valueOf(result.get(Code.SCROLLNO)), "MBTR",
							lbrcode, ResponseCodes.SUCCESS, ResponseCodes.SUCCESS_DESC, rrn);
					return ResponseCodes.SUCCESS;
				} else {
					P2ATransaction.p2aTransactionEntry(mmid1, mob1, mob2, mmid2, amount,
							Integer.valueOf(
									(result.get(Code.SETNO) == null || result.get(Code.SETNO).trim() == "") ? "0"
											: result.get(Code.SETNO)),
							Integer.valueOf(
									(result.get(Code.SCROLLNO) == null || result.get(Code.SCROLLNO).trim() == "") ? "0"
											: result.get(Code.SCROLLNO)),
							"MBTR", lbrcode, "99", "Transaction Failure", rrn);
					return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
				}
			}
			return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
		}
		return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
	}

	public String fundTransferOtherBranch(String accNo15digit, String benAccNo15digit, String transType,
			String narration, String narrationCr, double amount, String rrn, String mob1, String mmid1, String mob2,
			String mmid2, String tType, Session session) throws Exception {
		// TODO Auto-generated method stub
		if (tType.equalsIgnoreCase("P2P")) {
			String acctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit, session);
			int lbrcode = Integer.parseInt(accNo15digit.substring(0, 3));
			logger.error("Account NO 32 Digit::>>" + acctno32digit);
			logger.error("Branch Code::>>" + lbrcode);

			String benAcctno32digit = AccountDetailsUtil.get32DigitAcctNo(benAccNo15digit, session);
			int BenBrcode = Integer.parseInt(benAccNo15digit.substring(0, 3));

			HashMap<String, String> result = VoucherCommon.otherBranchVouchers(lbrcode, acctno32digit, BenBrcode,
					benAcctno32digit, transType, narration, narrationCr, amount, rrn, session);
			logger.error("Result:>>>" + result);
			if (null != result) {
				if (result.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
					P2PTransactionEntry.p2pTransactionEntry(mmid1, mob1, mob2, mmid2, amount,
							Integer.valueOf(result.get(Code.SETNO)), Integer.valueOf(result.get(Code.SCROLLNO)), "MBTR",
							lbrcode, ResponseCodes.SUCCESS, ResponseCodes.SUCCESS_DESC, rrn);
					return ResponseCodes.SUCCESS;
				} else {
					P2PTransactionEntry.p2pTransactionEntry(mmid1, mob1, mob2, mmid2, amount, Integer.valueOf(
							(result.get(Code.SETNO) == null || result.get(Code.SETNO).trim().equalsIgnoreCase("")) ? "0"
									: result.get(Code.SETNO)),
							Integer.valueOf((result.get(Code.SCROLLNO) == null
									|| result.get(Code.SCROLLNO).trim().equalsIgnoreCase("")) ? "0"
											: result.get(Code.SCROLLNO)),
							"MBTR", lbrcode, "99", "Transaction Failure", rrn);
					return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
				}
			}
			return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
		} else if (tType.equalsIgnoreCase("P2A")) {
			String acctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit);
			int lbrcode = Integer.parseInt(accNo15digit.substring(0, 3));
			logger.error("Account NO 32 Digit::>>" + acctno32digit);
			logger.error("Branch Code::>>" + lbrcode);
			String benAcctno32digit = AccountDetailsUtil.get32DigitAcctNo(benAccNo15digit);
			int BenBrcode = Integer.parseInt(benAccNo15digit.substring(0, 3));
			HashMap<String, String> result = VoucherCommon.otherBranchVouchers(lbrcode, acctno32digit, BenBrcode,
					benAcctno32digit, transType, narration, narrationCr, amount, rrn, session);
			logger.error("Result:>>>" + result);
			if (null != result) {
				if (result.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
					P2ATransaction.p2aTransactionEntry(mmid1, mob1, mob2, mmid2, amount,
							Integer.valueOf(result.get(Code.SETNO)), Integer.valueOf(result.get(Code.SCROLLNO)), "MBTR",
							lbrcode, ResponseCodes.SUCCESS, ResponseCodes.SUCCESS_DESC, rrn);

					return ResponseCodes.SUCCESS;
				} else {
					P2ATransaction.p2aTransactionEntry(mmid1, mob1, mob2, mmid2, amount,
							Integer.valueOf(
									(result.get(Code.SETNO) == null || result.get(Code.SETNO).trim() == "") ? "0"
											: result.get(Code.SETNO)),
							Integer.valueOf(
									(result.get(Code.SCROLLNO) == null || result.get(Code.SCROLLNO).trim() == "") ? "0"
											: result.get(Code.SCROLLNO)),
							"MBTR", lbrcode, "99", "Transaction Failure", rrn);
					return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
				}
			}
			return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
		}
		return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
	}

	/*
	 * @Author:Amar W. Raut Common method for Other bank voucher and transaction
	 * tType:P2P for P2P Transaction tType:P2A for P2P Transaction
	 */

	@Override
	public String fundTransferOtherBank(String accNo15digit, String transType, String narration, double amount,
			String rrn, String mob1, String mmid1, String mob2, String mmid2, String tType) {
		// TODO Auto-generated method stub
		if (tType.equalsIgnoreCase("P2P")) {
			String acctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit);
			int lbrcode = Integer.parseInt(accNo15digit.substring(0, 3));
			logger.error("Account NO 32 Digit::>>" + acctno32digit);
			logger.error("Branch Code::>>" + lbrcode);

			HashMap<String, String> result = VoucherCommon.otherBankVoucherEntry(lbrcode, acctno32digit, "IMPS",
					narration, amount, rrn);
			logger.error("Result:>>>" + result);
			if (null != result) {
				if (result.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
					P2PTransactionEntry.p2pTransactionEntry(mmid1, mob1, mob2, mmid2, amount,
							Integer.valueOf((result.get(Code.SETNO) == null || result.get(Code.SETNO) == "") ? "0"
									: result.get(Code.SETNO)),
							Integer.valueOf((result.get(Code.SCROLLNO) == null || result.get(Code.SCROLLNO) == "") ? "0"
									: result.get(Code.SCROLLNO)),
							"MBTR", lbrcode, ResponseCodes.SUCCESS, ResponseCodes.SUCCESS_DESC, rrn);
					return ResponseCodes.SUCCESS;
				} else {
					P2PTransactionEntry.p2pTransactionEntry(mmid1, mob1, mob2, mmid2, amount,
							Integer.valueOf((result.get(Code.SETNO) == null || result.get(Code.SETNO) == "") ? "0"
									: result.get(Code.SETNO)),
							Integer.valueOf((result.get(Code.SCROLLNO) == null || result.get(Code.SCROLLNO) == "") ? "0"
									: result.get(Code.SCROLLNO)),
							"MBTR", lbrcode, "99", "Transaction Failure", rrn);
					return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
				}
			}
			return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
		} else if (tType.equalsIgnoreCase("P2A")) {
			String acctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit);
			int lbrcode = Integer.parseInt(accNo15digit.substring(0, 3));
			logger.error("Account NO 32 Digit::>>" + acctno32digit);
			logger.error("Branch Code::>>" + lbrcode);
			HashMap<String, String> result = VoucherCommon.otherBankVoucherEntry(lbrcode, acctno32digit, "IMPS",
					narration, amount, rrn);
			logger.error("Result:>>>" + result);
			if (null != result) {
				if (result.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
					P2ATransaction.p2aTransactionEntry(mmid1, mob1, mob2, mmid2, amount,
							Integer.valueOf(result.get(Code.SETNO)), Integer.valueOf(result.get(Code.SCROLLNO)), "MBTR",
							lbrcode, ResponseCodes.SUCCESS, ResponseCodes.SUCCESS_DESC, rrn);
					return ResponseCodes.SUCCESS;
				} else {
					P2ATransaction.p2aTransactionEntry(mmid1, mob1, mob2, mmid2, amount,
							Integer.valueOf((result.get(Code.SETNO) == null || result.get(Code.SETNO) == "") ? "0"
									: result.get(Code.SETNO)),
							Integer.valueOf((result.get(Code.SCROLLNO) == null || result.get(Code.SCROLLNO) == "") ? "0"
									: result.get(Code.SCROLLNO)),
							"MBTR", lbrcode, "99", "Transaction Failure", rrn);
					return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
				}
			}
			return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
		}
		return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
	}

	public String debitAccountOtherBank(String accNo15digit, String transType, String narration, double amount,
			String rrn, String mob1, String mmid1, String mob2, String mmid2, String tType) {

		if (tType.equalsIgnoreCase("P2P")) {
			String acctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit);
			int lbrcode = Integer.parseInt(accNo15digit.substring(0, 3));
			logger.error("Account NO 32 Digit::>>" + acctno32digit);
			logger.error("Branch Code::>>" + lbrcode);

			HashMap<String, String> result = VoucherCommon.debitBankVoucherEntry(lbrcode, acctno32digit,
					MSGConstants.SIM_SAY_PAY, narration, amount, rrn);
			logger.error("Result:>>>" + result);
			if (null != result) {
				if (result.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
					return ResponseCodes.SUCCESS;
				} else {
					return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
				}
			}
			return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
		} else if (tType.equalsIgnoreCase("P2A")) {
			String acctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit);
			int lbrcode = Integer.parseInt(accNo15digit.substring(0, 3));
			logger.error("Account NO 32 Digit::>>" + acctno32digit);
			logger.error("Branch Code::>>" + lbrcode);
			HashMap<String, String> result = VoucherCommon.debitBankVoucherEntry(lbrcode, acctno32digit,
					MSGConstants.SIM_SAY_PAY, narration, amount, rrn);
			logger.error("Result:>>>" + result);
			if (null != result) {
				if (result.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
					return ResponseCodes.SUCCESS;
				} else {
					return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
				}
			}
			return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
		}
		return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
	}

	public String initiateBillPayment(String accNo15digit, String transType, String narration, double amount,
			String rrn, String mob1, String mmid1, String mob2, String mmid2, String tType,
			IMPSTransactionRequest request) {
		// TODO Auto-generated method stub
		if (tType.equalsIgnoreCase("P2P")) {
			String acctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit);
			int lbrcode = Integer.parseInt(accNo15digit.substring(0, 3));
			logger.error("Account NO 32 Digit::>>" + acctno32digit);
			logger.error("Branch Code::>>" + lbrcode);

			HashMap<String, String> result = VoucherCommon.billPaymentVoucherEntry(lbrcode, acctno32digit, "IMPS",
					narration, amount, rrn);
			logger.error("Result:>>>" + result);
			if (null != result) {
				if (result.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
					P2PTransactionEntry.p2pTransactionEntry(mmid1, mob1, mob2, mmid2, amount,
							Integer.valueOf(result.get(Code.SETNO)), Integer.valueOf(result.get(Code.SCROLLNO)), "MBTR",
							lbrcode, ResponseCodes.SUCCESS, ResponseCodes.SUCCESS_DESC, rrn);
					return ResponseCodes.SUCCESS;
				} else {
					P2PTransactionEntry.p2pTransactionEntry(mmid1, mob1, mob2, mmid2, amount,
							Integer.valueOf(result.get(Code.SETNO)), Integer.valueOf(result.get(Code.SCROLLNO)), "MBTR",
							lbrcode, "99", "Transaction Failure", rrn);
					return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
				}
			}
			return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
		} else if (tType.equalsIgnoreCase("P2A")) {
			String acctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit);
			int lbrcode = Integer.parseInt(accNo15digit.substring(0, 3));
			logger.error("Account NO 32 Digit::>>" + acctno32digit);
			logger.error("Branch Code::>>" + lbrcode);
			System.out.println("Account NO 32 Digit::>>" + acctno32digit);
			System.out.println("Branch Code::>>" + lbrcode);
			HashMap<String, String> result = VoucherCommon.billPaymentVoucherEntry(lbrcode, acctno32digit, "IMPS",
					narration, amount, rrn);
			logger.error("Result:>>>" + result);
			if (null != result) {
				if (result.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
					P2ATransaction.p2aTransactionEntry(mmid1, mob1, mob2, mmid2, amount,
							Integer.valueOf(result.get(Code.SETNO)), Integer.valueOf(result.get(Code.SCROLLNO)), "MBTR",
							lbrcode, ResponseCodes.SUCCESS, ResponseCodes.SUCCESS_DESC, rrn);
					return ResponseCodes.SUCCESS;
				} else {
					P2ATransaction.p2aTransactionEntry(mmid1, mob1, mob2, mmid2, amount,
							Integer.valueOf(result.get(Code.SETNO)), Integer.valueOf(result.get(Code.SCROLLNO)), "MBTR",
							lbrcode, "99", "Transaction Failure", rrn);
					return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
				}
			}
			return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
		} else if (tType.equalsIgnoreCase(MSGConstants.BILLPAY)) {
			String acctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit);
			int lbrcode = Integer.parseInt(accNo15digit.substring(0, 3));
			logger.error("Account NO 32 Digit::>>" + acctno32digit);
			logger.error("Branch Code::>>" + lbrcode);
			HashMap<String, String> result = VoucherCommon.billPaymentVoucherEntry(lbrcode, acctno32digit, "IMPS",
					narration, amount, rrn);
			logger.error("Result:>>>" + result);// {result=success,
												// setno=500819,
												// scrollno=501769}
			if (null != result) {
				if (result.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
					BillPaymentTransactionEntry.transactionEntryBillPay(lbrcode, acctno32digit, "MBTR",
							Integer.valueOf(
									(result.get(Code.SETNO) == null || result.get(Code.SETNO).trim() == "") ? "0"
											: result.get(Code.SETNO).trim()),
							Integer.valueOf(
									(result.get(Code.SETNO) == null || result.get(Code.SETNO).trim() == "") ? "0"
											: result.get(Code.SETNO).trim()),
							request.getConsumerNo(), request.getOperator(), request.getTransAmt(),
							ResponseCodes.SUCCESS, MSGConstants.SUCCESS_MSG, rrn, "D");
					return ResponseCodes.SUCCESS;
				} else {
					BillPaymentTransactionEntry.transactionEntryBillPay(lbrcode, acctno32digit, "MBTR",
							Integer.valueOf(
									(result.get(Code.SETNO) == null || result.get(Code.SETNO).trim() == "") ? "0"
											: result.get(Code.SETNO).trim()),
							Integer.valueOf(
									(result.get(Code.SETNO) == null || result.get(Code.SETNO).trim() == "") ? "0"
											: result.get(Code.SETNO).trim()),
							request.getConsumerNo(), request.getOperator(), request.getTransAmt(),
							ResponseCodes.NO_RECORD_FOUND, MSGConstants.TRANSACTION_DECLINED, rrn, "D");
					return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
				}
			}
			return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
		} else if (tType.equalsIgnoreCase("BILLDESK")) {
			String acctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit);
			int lbrcode = Integer.parseInt(accNo15digit.substring(0, 3));
			logger.error("Account NO 32 Digit::>>" + acctno32digit);
			logger.error("Branch Code::>>" + lbrcode);
			HashMap<String, String> result = VoucherCommon.billDeskVoucherEntry(lbrcode, acctno32digit, "IMPS",
					narration, amount, rrn);
			logger.error("Result:>>>" + result);// {result=success,
												// setno=500819,
												// scrollno=501769}
			if (null != result) {
				if (result.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
					BillPaymentTransactionEntry.transactionEntryBillPay(lbrcode, acctno32digit, "MBTR",
							Integer.valueOf(
									(result.get(Code.SETNO) == null || result.get(Code.SETNO).trim() == "") ? "0"
											: result.get(Code.SETNO).trim()),
							Integer.valueOf(
									(result.get(Code.SETNO) == null || result.get(Code.SETNO).trim() == "") ? "0"
											: result.get(Code.SETNO).trim()),
							request.getConsumerNo(), request.getOperator(), request.getTransAmt(),
							ResponseCodes.SUCCESS, MSGConstants.SUCCESS_MSG, rrn, "D");
					return ResponseCodes.SUCCESS;
				} else {
					BillPaymentTransactionEntry.transactionEntryBillPay(lbrcode, acctno32digit, "MBTR",
							Integer.valueOf(
									(result.get(Code.SETNO) == null || result.get(Code.SETNO).trim() == "") ? "0"
											: result.get(Code.SETNO).trim()),
							Integer.valueOf(
									(result.get(Code.SETNO) == null || result.get(Code.SETNO).trim() == "") ? "0"
											: result.get(Code.SETNO).trim()),
							request.getConsumerNo(), request.getOperator(), request.getTransAmt(),
							ResponseCodes.NO_RECORD_FOUND, MSGConstants.TRANSACTION_DECLINED, rrn, "D");
					return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
				}
			}
		return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
		}else if (tType.equalsIgnoreCase("VPA")) {
			String acctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit);
			int lbrcode = Integer.parseInt(accNo15digit.substring(0, 3));
			logger.error("Account NO 32 Digit::>>" + acctno32digit);
			logger.error("Branch Code::>>" + lbrcode);
			HashMap<String, String> result = VoucherCommon.vpaVoucherEntry(lbrcode, acctno32digit, "IMPS",
					narration, amount, rrn);
			logger.error("Result:>>>" + result);// {result=success,
												// setno=500819,
												// scrollno=501769}
			if (null != result) {
				if (result.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
					BillPaymentTransactionEntry.transactionEntryBillPay(lbrcode, acctno32digit, "MBTR",
							Integer.valueOf(
									(result.get(Code.SETNO) == null || result.get(Code.SETNO).trim() == "") ? "0"
											: result.get(Code.SETNO).trim()),
							Integer.valueOf(
									(result.get(Code.SETNO) == null || result.get(Code.SETNO).trim() == "") ? "0"
											: result.get(Code.SETNO).trim()),
							request.getConsumerNo(), request.getOperator(), request.getTransAmt(),
							ResponseCodes.SUCCESS, MSGConstants.SUCCESS_MSG, rrn, "D");
					return ResponseCodes.SUCCESS;
				} else {
					BillPaymentTransactionEntry.transactionEntryBillPay(lbrcode, acctno32digit, "MBTR",
							Integer.valueOf(
									(result.get(Code.SETNO) == null || result.get(Code.SETNO).trim() == "") ? "0"
											: result.get(Code.SETNO).trim()),
							Integer.valueOf(
									(result.get(Code.SETNO) == null || result.get(Code.SETNO).trim() == "") ? "0"
											: result.get(Code.SETNO).trim()),
							request.getConsumerNo(), request.getOperator(), request.getTransAmt(),
							ResponseCodes.NO_RECORD_FOUND, MSGConstants.TRANSACTION_DECLINED, rrn, "D");
					return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
				}
			}
		return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
		}
		return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
	}

	@Override
	public String reversedTransactionOtherBank(String accNo15digit, String transType, String narration, double amount,
			String rrn, String mob1, String mmid1, String mob2, String mmid2, String tType,
			IMPSTransactionRequest request) {
		// TODO Auto-generated method stub
		if (tType.equalsIgnoreCase("P2P")) {
			String acctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit);
			int lbrcode = Integer.parseInt(accNo15digit.substring(0, 3));
			logger.error("Account NO 32 Digit::>>" + acctno32digit);
			logger.error("Branch Code::>>" + lbrcode);
			HashMap<String, String> result = VoucherCommon.otherBankReverseVoucherEntry(lbrcode, acctno32digit, "IMPS",
					narration, amount, rrn);
			logger.error("Result:>>>" + result);
			if (null != result) {
				if (result.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
					P2PReversal.reverseTransactionEntry(String.valueOf(lbrcode), mob1, mmid1, mob2, mmid2,
							String.valueOf(amount), rrn, "C",
							Integer.valueOf(
									result.get(Code.SETNO) == null || result.get(Code.SETNO).trim().isEmpty() ? "0"
											: result.get(Code.SETNO)),
							Integer.valueOf(
									result.get(Code.SCROLLNO) == null || result.get(Code.SCROLLNO).trim().isEmpty()
											? "0"
											: result.get(Code.SCROLLNO)));

					return ResponseCodes.SUCCESS;
				} else {
					// P2AReversal.transactionEntry(String.valueOf(lbrcode),
					// mob1, mmid1, mob2, mmid2,String.valueOf(amount), rrn,
					// "C", Integer.valueOf(result.get(Code.SETNO)),
					// Integer.valueOf(result.get(Code.SCROLLNO)), "MBTR",
					// "99","Transaction Failure");
					P2PReversal.reverseTransactionEntry(String.valueOf(lbrcode), mob1, mmid1, mob2, mmid2,
							String.valueOf(amount), rrn, "C", 0, 0);
					return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
				}
			}
			return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
		} else if (tType.equalsIgnoreCase("P2A")) {
			String acctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit);
			int lbrcode = Integer.parseInt(accNo15digit.substring(0, 3));
			logger.error("Account NO 32 Digit::>>" + acctno32digit);
			logger.error("Branch Code::>>" + lbrcode);
			HashMap<String, String> result = VoucherCommon.otherBankReverseVoucherEntry(lbrcode, acctno32digit, "IMPS",
					narration, amount, rrn);
			logger.error("Result:>>>" + result);
			if (null != result) {
				if (result.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
					String[] skipPrds = ConfigurationLoader.getParameters(false).getProperty("IMPS_SKIP_CHG_PRD").split(",");
					ArrayList<String> skipList = new ArrayList<String>(Arrays.asList(skipPrds));
					if ("Y".equalsIgnoreCase(ConfigurationLoader.getParameters(false).getProperty("IMPS_CHG_P2A_YN").trim())
							&& !skipList.contains(acctno32digit.substring(0, 8).trim())) {
						HashMap<String, String> result2 = null;
						Session session = HBUtil.getSessionFactory().openSession();
						Transaction t = session.beginTransaction();
						D009022Id remId = new D009022Id();
						remId.setLbrCode(lbrcode);
						remId.setPrdAcctId(acctno32digit);
						D009022 remAcct = session.get(D009022.class, remId);
						int count = Integer
								.parseInt(ConfigurationLoader.getParameters(false).getProperty("IMPS_CHG_TRN_LIMIT"));
						if ((VoucherCommon.getTransactionsInAMonth(mob1, mmid1, session)) >= count
								&& !MSGConstants.acctTypeList.contains((long) remAcct.getAcctType())) {
							Double chgAmount = 0.0;
							if("Y".equalsIgnoreCase(ConfigurationLoader.getParameters(false).getProperty("IMPS_P2A_CHG_AMOUNT_SLAB_YN"))) {
								chgAmount = getFlatAmtForMsgType(amount,"P2A",session);
							}else
								chgAmount = Double.valueOf(
									ConfigurationLoader.getParameters(false).getProperty("IMPS_P2A_CHG_AMOUNT").trim());
							if ("S".equalsIgnoreCase(
									ConfigurationLoader.getParameters(false).getProperty("IMPS_SER_OR_GST_CHG"))) {
								result2 = VoucherCommon.serviceChgRevVoucherEntry(lbrcode, rrn, session, acctno32digit,
										"REV/IMPS Charges/P2A/" + rrn, chgAmount);
							} else {
								result2 = VoucherCommon.gstRevVoucherEntry(lbrcode, rrn, session, acctno32digit,
										"REV/IMPS Charges/P2A/" + rrn, chgAmount);
							}

							if (result2.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
								t.commit();
								P2AReversal.transactionEntry(String.valueOf(lbrcode), mob1, mmid1, mob2, mmid2,
										String.valueOf(amount), rrn, "C",
										Integer.valueOf(result.get(Code.SETNO) == null
												|| result.get(Code.SETNO).trim().isEmpty() ? "0"
														: result.get(Code.SETNO)),
										Integer.valueOf(result.get(Code.SCROLLNO) == null
												|| result.get(Code.SCROLLNO).trim().isEmpty() ? "0"
														: result.get(Code.SCROLLNO)),
										"MBTR", ResponseCodes.SUCCESS, ResponseCodes.SUCCESS_DESC, "");
							} else {
								P2AReversal.transactionEntry(String.valueOf(lbrcode), mob1, mmid1, mob2, mmid2,
										String.valueOf(amount), rrn, "C",
										Integer.valueOf(result.get(Code.SETNO) == null
												|| result.get(Code.SETNO).trim().length() < 1 ? "0"
														: result.get(Code.SETNO)),
										Integer.valueOf(result.get(Code.SCROLLNO) == null
												|| result.get(Code.SCROLLNO).trim().length() < 1 ? "0"
														: result.get(Code.SCROLLNO)),
										"MBTR", ResponseCodes.EXCEPTION_OCCURED, "Transaction failure", "");
							}
						} else {
							P2AReversal
									.transactionEntry(String.valueOf(lbrcode), mob1, mmid1, mob2, mmid2,
											String.valueOf(amount), rrn, "C",
											Integer.valueOf(result.get(Code.SETNO) == null
													|| result.get(Code.SETNO).trim().isEmpty() ? "0"
															: result.get(Code.SETNO)),
											Integer.valueOf(result.get(Code.SCROLLNO) == null
													|| result.get(Code.SCROLLNO).trim().isEmpty() ? "0"
															: result.get(Code.SCROLLNO)),
											"MBTR", ResponseCodes.SUCCESS, ResponseCodes.SUCCESS_DESC, "");
						}
					} else {
						P2AReversal.transactionEntry(String.valueOf(lbrcode), mob1, mmid1, mob2, mmid2,
								String.valueOf(amount), rrn, "C",
								Integer.valueOf(
										result.get(Code.SETNO) == null || result.get(Code.SETNO).trim().isEmpty() ? "0"
												: result.get(Code.SETNO)),
								Integer.valueOf(
										result.get(Code.SCROLLNO) == null || result.get(Code.SCROLLNO).trim().isEmpty()
												? "0"
												: result.get(Code.SCROLLNO)),
								"MBTR", ResponseCodes.SUCCESS, ResponseCodes.SUCCESS_DESC, "");
					}
					return ResponseCodes.SUCCESS;

					/*
					 * P2AReversal.transactionEntry(String.valueOf(lbrcode), mob1, mmid1, mob2,
					 * mmid2, String.valueOf(amount), rrn, "C",
					 * Integer.valueOf(result.get(Code.SETNO)==null ||
					 * result.get(Code.SETNO).trim().isEmpty() ?"0":result.get(Code.SETNO)),
					 * Integer.valueOf(result.get(Code.SCROLLNO)==null ||
					 * result.get(Code.SCROLLNO).trim().isEmpty()?"0":result.get(Code.SCROLLNO)),
					 * "MBTR", ResponseCodes.SUCCESS, ResponseCodes.SUCCESS_DESC,""); return
					 * ResponseCodes.SUCCESS;
					 */
				} else {
					P2AReversal.transactionEntry(String.valueOf(lbrcode), mob1, mmid1, mob2, mmid2,
							String.valueOf(amount), rrn, "C",
							Integer.valueOf(
									result.get(Code.SETNO) == null || result.get(Code.SETNO).trim().isEmpty() ? "0"
											: result.get(Code.SETNO)),
							Integer.valueOf(
									result.get(Code.SCROLLNO) == null || result.get(Code.SCROLLNO).trim().isEmpty()
											? "0"
											: result.get(Code.SCROLLNO)),
							"MBTR", ResponseCodes.EXCEPTION_OCCURED, MSGConstants.TRANSACTION_FAILS, "");
					return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
				}
			}
			return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
		} else if (request.getOperator().equalsIgnoreCase("BILLDESK")) {
			String acctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit);
			int lbrcode = Integer.parseInt(accNo15digit.substring(0, 3));
			logger.error("Account NO 32 Digit::>>" + acctno32digit);
			logger.error("Branch Code::>>" + lbrcode);
			HashMap<String, String> result = VoucherCommon.reverseBillDesk(lbrcode, acctno32digit, "IMPS", narration,
					amount, rrn);
			logger.error("Result:>>>" + result);
			if (null != result) {
				if (result.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
					BillPaymentTransactionEntry.transactionEntryBillPay(lbrcode, acctno32digit, "MBTR",
							Integer.valueOf(
									(result.get(Code.SETNO) == null || result.get(Code.SETNO).trim() == "") ? "0"
											: result.get(Code.SETNO).trim()),
							Integer.valueOf(
									(result.get(Code.SETNO) == null || result.get(Code.SETNO).trim() == "") ? "0"
											: result.get(Code.SETNO).trim()),
							request.getConsumerNo(), request.getOperator(), request.getTransAmt(),
							ResponseCodes.SUCCESS, MSGConstants.SUCCESS_MSG, request.getRRNNo(), "C");
					return ResponseCodes.SUCCESS;
				} else {
					BillPaymentTransactionEntry.transactionEntryBillPay(lbrcode, acctno32digit, "MBTR",
							Integer.valueOf(
									(result.get(Code.SETNO) == null || result.get(Code.SETNO).trim() == "") ? "0"
											: result.get(Code.SETNO).trim()),
							Integer.valueOf(
									(result.get(Code.SETNO) == null || result.get(Code.SETNO).trim() == "") ? "0"
											: result.get(Code.SETNO).trim()),
							request.getConsumerNo(), request.getOperator(), request.getTransAmt(),
							ResponseCodes.NO_RECORD_FOUND, MSGConstants.TRANSACTION_DECLINED, request.getRRNNo(), "C");
					return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
				}
			}
			return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
		} else if (tType.equalsIgnoreCase(MSGConstants.SIM_SAY_PAY)) {
			String acctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit);
			int lbrcode = Integer.parseInt(accNo15digit.substring(0, 3));
			logger.error("Account NO 32 Digit::>>" + acctno32digit);
			logger.error("Branch Code::>>" + lbrcode);
			HashMap<String, String> result = VoucherCommon.simSePayReverseVoucherEntry(lbrcode, acctno32digit,
					MSGConstants.SIM_SAY_PAY, narration, amount, rrn);
			logger.error("Result:>>>" + result);
			if (null != result) {
				if (result.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS))
					return ResponseCodes.SUCCESS;
				else
					return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
			}
			return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
		}else if (tType.equalsIgnoreCase(MSGConstants.BILLPAY)) {
			String acctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit);
			int lbrcode = Integer.parseInt(accNo15digit.substring(0, 3));
			logger.error("Account NO 32 Digit::>>" + acctno32digit);
			logger.error("Branch Code::>>" + lbrcode);
			HashMap<String, String> result = VoucherCommon.reverseBillpayment(lbrcode, acctno32digit, "IMPS", narration,
					amount, rrn);
			logger.error("Result:>>>" + result);
			if (null != result) {
				if (result.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
					BillPaymentTransactionEntry.transactionEntryBillPay(lbrcode, acctno32digit, "MBTR",
							Integer.valueOf(
									(result.get(Code.SETNO) == null || result.get(Code.SETNO).trim() == "") ? "0"
											: result.get(Code.SETNO).trim()),
							Integer.valueOf(
									(result.get(Code.SETNO) == null || result.get(Code.SETNO).trim() == "") ? "0"
											: result.get(Code.SETNO).trim()),
							request.getConsumerNo(), request.getOperator(), request.getTransAmt(),
							ResponseCodes.SUCCESS, MSGConstants.SUCCESS_MSG, request.getRRNNo(), "C");
					return ResponseCodes.SUCCESS;
				} else {
					BillPaymentTransactionEntry.transactionEntryBillPay(lbrcode, acctno32digit, "MBTR",
							Integer.valueOf(
									(result.get(Code.SETNO) == null || result.get(Code.SETNO).trim() == "") ? "0"
											: result.get(Code.SETNO).trim()),
							Integer.valueOf(
									(result.get(Code.SETNO) == null || result.get(Code.SETNO).trim() == "") ? "0"
											: result.get(Code.SETNO).trim()),
							request.getConsumerNo(), request.getOperator(), request.getTransAmt(),
							ResponseCodes.NO_RECORD_FOUND, MSGConstants.TRANSACTION_DECLINED, request.getRRNNo(), "C");
					return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
				}
			}
			return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
		} else if (request.getOperator().equalsIgnoreCase("VPA")) {
			String acctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit);
			int lbrcode = Integer.parseInt(accNo15digit.substring(0, 3));
			logger.error("Account NO 32 Digit::>>" + acctno32digit);
			logger.error("Branch Code::>>" + lbrcode);
			HashMap<String, String> result = VoucherCommon.reverseVPAPayment(lbrcode, acctno32digit, "IMPS", narration,
					amount, rrn);
			logger.error("Result:>>>" + result);
			if (null != result) {
				if (result.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
					BillPaymentTransactionEntry.transactionEntryBillPay(lbrcode, acctno32digit, "MBTR",
							Integer.valueOf(
									(result.get(Code.SETNO) == null || result.get(Code.SETNO).trim() == "") ? "0"
											: result.get(Code.SETNO).trim()),
							Integer.valueOf(
									(result.get(Code.SETNO) == null || result.get(Code.SETNO).trim() == "") ? "0"
											: result.get(Code.SETNO).trim()),
							request.getConsumerNo(), request.getOperator(), request.getTransAmt(),
							ResponseCodes.SUCCESS, MSGConstants.SUCCESS_MSG, request.getRRNNo(), "C");
					return ResponseCodes.SUCCESS;
				} else {
					BillPaymentTransactionEntry.transactionEntryBillPay(lbrcode, acctno32digit, "MBTR",
							Integer.valueOf(
									(result.get(Code.SETNO) == null || result.get(Code.SETNO).trim() == "") ? "0"
											: result.get(Code.SETNO).trim()),
							Integer.valueOf(
									(result.get(Code.SETNO) == null || result.get(Code.SETNO).trim() == "") ? "0"
											: result.get(Code.SETNO).trim()),
							request.getConsumerNo(), request.getOperator(), request.getTransAmt(),
							ResponseCodes.NO_RECORD_FOUND, MSGConstants.TRANSACTION_DECLINED, request.getRRNNo(), "C");
					return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
				}
			}
			return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
		}
		return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
	}

	public String creditTransactionOtherBankNew(String accNo15digit, String transType, String narration, double amount,
			String rrn, String mob1, String mmid1, String mob2, String mmid2, String tType) throws Exception {
		// TODO Auto-generated method stub
		if (tType.equalsIgnoreCase(MSGConstants.P2P_TRANSACTION)) {
			String acctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit);
			int lbrcode = Integer.parseInt(accNo15digit.substring(0, 3));
			logger.error("Account NO 32 Digit::>>" + acctno32digit);
			logger.error("Branch Code::>>" + lbrcode);
			HashMap<String, String> result = null;
			try {
				result = VoucherCommon.otherBankCreditVoucherEntryNew(lbrcode, acctno32digit, "IMPS", narration, amount,
						rrn);
				logger.error("Result:>>>" + result);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			if (null != result) {
				if (result.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
					P2PReversal.reverseTransactionEntry(String.valueOf(lbrcode), mob1, mmid1, mob2, mmid2,
							String.valueOf(amount), rrn, "C",
							Integer.valueOf(result.get(Code.SETNO) == null ? "0" : result.get(Code.SETNO)),
							Integer.valueOf(result.get(Code.SCROLLNO) == null ? "0" : result.get(Code.SCROLLNO)));
					return ResponseCodes.SUCCESS;
				} else {
					// P2AReversal.transactionEntry(String.valueOf(lbrcode),
					// mob1, mmid1, mob2, mmid2,String.valueOf(amount), rrn,
					// "C", Integer.valueOf(result.get(Code.SETNO)),
					// Integer.valueOf(result.get(Code.SCROLLNO)), "MBTR",
					// "99","Transaction Failure");
					P2PReversal.reverseTransactionEntry(String.valueOf(lbrcode), mob1, mmid1, mob2, mmid2,
							String.valueOf(amount), rrn, "C",
							Integer.valueOf((result.get("0") == null || result.get("0").trim().isEmpty()) ? "0"
									: result.get("0")),
							Integer.valueOf((result.get("0") == null || result.get("0").trim().isEmpty()) ? "0"
									: result.get("0")));
					return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
				}
			}
			return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
		} else if (tType.equalsIgnoreCase(MSGConstants.P2A_TRANSACTION)) {
			String acctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit);
			int lbrcode = Integer.parseInt(accNo15digit.trim().substring(0, 3));
			logger.error("Credit Account NO 32 Digit::>>" + acctno32digit);
			logger.error("Branch Code::>>" + lbrcode);
			HashMap<String, String> result = VoucherCommon.otherBankCreditVoucherEntryNew(lbrcode, acctno32digit,
					"IMPS", narration, amount, rrn);
			logger.error("Result:>>>" + result);
			if (null != result) {
				if (result.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
					P2AReversal.transactionUpdate(String.valueOf(lbrcode), mob1, mmid1, mob2, mmid2,
							String.valueOf(amount), rrn, "C",
							Integer.valueOf((result.get(Code.SETNO) == null
									|| result.get(Code.SETNO).trim().equalsIgnoreCase("")) ? "0"
											: result.get(Code.SETNO)),
							Integer.valueOf((result.get(Code.SCROLLNO) == null
									|| result.get(Code.SCROLLNO).trim().equalsIgnoreCase("")) ? "0"
											: result.get(Code.SCROLLNO)),
							String.valueOf(
									(result.get("Batch") == null || result.get("Batch").trim().equalsIgnoreCase(""))
											? "MBTR"
											: result.get("Batch")),
							ResponseCodes.SUCCESS, ResponseCodes.SUCCESS_DESC);
					return ResponseCodes.SUCCESS;
				} else {
					P2AReversal.transactionUpdate(String.valueOf(lbrcode), mob1, mmid1, mob2, mmid2,
							String.valueOf(amount), rrn, "C",
							Integer.valueOf((result.get(Code.SETNO) == null
									|| result.get(Code.SETNO).trim().equalsIgnoreCase("")) ? "0"
											: result.get(Code.SETNO)),
							Integer.valueOf((result.get(Code.SCROLLNO) == null
									|| result.get(Code.SCROLLNO).trim().equalsIgnoreCase("")) ? "0"
											: result.get(Code.SCROLLNO)),
							"MBTR", ResponseCodes.EXCEPTION_OCCURED, MSGConstants.TRANSACTION_FAILS);
					return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
				}
			}
			return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
		}
		return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
	}

	public String creditOtherBankTransaction(String accNo15digit, String transType, String narration, double amount,
			String rrn, String mob1, String mmid1, String mob2, String mmid2, String tType) {
		// TODO Auto-generated method stub
		if (tType.equalsIgnoreCase("P2P")) {
			String acctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit);
			int lbrcode = Integer.parseInt(accNo15digit.substring(0, 3));
			logger.error("Account NO 32 Digit::>>" + acctno32digit);
			logger.error("Branch Code::>>" + lbrcode);
			HashMap<String, String> result = VoucherCommon.otherBankCreditVoucherEntry(lbrcode, acctno32digit, "IMPS",
					narration, amount, rrn);// VoucherEntry();
			logger.error("Result:>>>" + result);
			if (null != result) {
				if (result.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
					P2PReversal.reverseTransactionEntry(String.valueOf(lbrcode), mob1, mmid1, mob2, mmid2,
							String.valueOf(amount), rrn, "C", Integer.valueOf(result.get(Code.SETNO)),
							Integer.valueOf(result.get(Code.SCROLLNO)));
					return ResponseCodes.SUCCESS;
				} else {
					// P2AReversal.transactionEntry(String.valueOf(lbrcode),
					// mob1, mmid1, mob2, mmid2,String.valueOf(amount), rrn,
					// "C", Integer.valueOf(result.get(Code.SETNO)),
					// Integer.valueOf(result.get(Code.SCROLLNO)), "MBTR",
					// "99","Transaction Failure");
					P2PReversal.reverseTransactionEntry(String.valueOf(lbrcode), mob1, mmid1, mob2, mmid2,
							String.valueOf(amount), rrn, "C", Integer.valueOf(result.get("0")),
							Integer.valueOf(result.get("0")));
					return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
				}
			}
			return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
		} else if (tType.equalsIgnoreCase("P2A")) {
			String acctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit);
			int lbrcode = Integer.parseInt(accNo15digit.substring(0, 3));
			logger.error("Account NO 32 Digit::>>" + acctno32digit);
			logger.error("Branch Code::>>" + lbrcode);
			HashMap<String, String> result = VoucherCommon.otherBankCreditVoucherEntry(lbrcode, acctno32digit, "IMPS",
					narration, amount, rrn);
			logger.error("Result:>>>" + result);
			if (null != result) {
				if (result.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
					P2AReversal.transactionEntry(String.valueOf(lbrcode), mob1, mmid1, mob2, mmid2,
							String.valueOf(amount), rrn, "C", Integer.valueOf(result.get(Code.SETNO)),
							Integer.valueOf(result.get(Code.SCROLLNO)), "MBTR", ResponseCodes.SUCCESS,
							ResponseCodes.SUCCESS_DESC, "");
					return ResponseCodes.SUCCESS;
				} else {
					P2AReversal.transactionEntry(String.valueOf(lbrcode), mob1, mmid1, mob2, mmid2,
							String.valueOf(amount), rrn, "C", Integer.valueOf(result.get(Code.SETNO)),
							Integer.valueOf(result.get(Code.SCROLLNO)), "MBTR", ResponseCodes.EXCEPTION_OCCURED,
							MSGConstants.TRANSACTION_FAILS, "");
					return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
				}
			}
			return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
		}
		return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
	}

	@Override
	public String mposVoucher(String accNo15digit, String benAcc, int brCode, int tobrCode, double amount,
			String narretion, String reqFlag) {
		// TODO Auto-generated method stub

		return null;
	}

	public static void main(String[] args) {
		// OtherChannelServiceResponse response=verificationReq(3,
		// "003001000006707", "P2P", "", 101, "702412000001", "918691911199",
		// "8307001", "919920365867", "8307001");
		IMPSTransactionResponse response = verificationReq(3, "003001000006707", "P2A", "", 101, "702313111211",
				"918691911199", "8307001", "004001000000286", "YESB0NCB004");
		System.out.println("response.getResponse()::>>" + response.getResponse());
		System.out.println("response.getErrorMessage()::>>" + response.getErrorMessage());
		System.out.println("response.getErrorCode()::>>" + response.getErrorCode());
		// new
		// CoreBankingOperationImpl().fundTransferOtherBank("003001000006707",
		// "IMPS", "tt", 21, "111122224444", "918983389108", "9088001",
		// "9920209434", "9088002", "P2A");
	}

	@Override
	public String reversefundTransfer(String accNo15digit, String benAccno15digit, String transType, String narration,
			double amount, String rrn, String mob1, String mmid1, String mob2, String mmid2, String tType) {
		// TODO Auto-generated method stub
		if (tType.equalsIgnoreCase(MSGConstants.P2P_TRANSACTION)) {
			String acctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit);
			int lbrcode = Integer.parseInt(accNo15digit.substring(0, 3));
			logger.error("Account NO 32 Digit::>>" + acctno32digit);
			logger.error("Branch Code::>>" + lbrcode);

			String benAcc32digit = AccountDetailsUtil.get32DigitAcctNo(benAccno15digit);
			int benBrcode = Integer.parseInt(benAccno15digit.substring(0, 3));
			logger.error("Account NO 32 Digit::>>" + acctno32digit);
			logger.error("Branch Code::>>" + lbrcode);

			HashMap<String, String> result = VoucherCommon.otherBranchReversal(lbrcode, acctno32digit, benBrcode,
					benAcc32digit, "IMPS", narration, amount, rrn);
			logger.error("Result:>>>" + result);
			if (null != result) {
				if (result.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
					P2PReversal.reverseTransactionEntry(String.valueOf(lbrcode), mob1, mmid1, mob2, mmid2,
							String.valueOf(amount), rrn, "C", Integer.valueOf(result.get(Code.SETNO)),
							Integer.valueOf(result.get(Code.SCROLLNO)));
					return ResponseCodes.SUCCESS;
				} else {
					// P2AReversal.transactionEntry(String.valueOf(lbrcode),
					// mob1, mmid1, mob2, mmid2,String.valueOf(amount), rrn,
					// "C", Integer.valueOf(result.get(Code.SETNO)),
					// Integer.valueOf(result.get(Code.SCROLLNO)), "MBTR",
					// "99","Transaction Failure");
					P2PReversal.reverseTransactionEntry(String.valueOf(lbrcode), mob1, mmid1, mob2, mmid2,
							String.valueOf(amount), rrn, "C", 0, 0);
					return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
				}
			}
			return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
		} else if (tType.equalsIgnoreCase(MSGConstants.P2A_TRANSACTION)) {
			String acctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit);
			int lbrcode = Integer.parseInt(accNo15digit.substring(0, 3));
			logger.error("Account NO 32 Digit::>>" + acctno32digit);
			logger.error("Branch Code::>>" + lbrcode);

			String benAcc32digit = AccountDetailsUtil.get32DigitAcctNo(benAccno15digit);
			int benBrcode = Integer.parseInt(benAccno15digit.substring(0, 3));
			logger.error("Beneficiary Account NO 32 Digit::>>" + acctno32digit);
			logger.error("Beneficiary Branch Code::>>" + lbrcode);

			HashMap<String, String> result = VoucherCommon.otherBranchReversal(lbrcode, acctno32digit, benBrcode,
					benAcc32digit, "IMPS", narration, amount, rrn);
			logger.error("Result:>>>" + result);
			if (null != result) {

				if (result.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {

					String[] skipPrds = ConfigurationLoader.getParameters(false).getProperty("IMPS_SKIP_CHG_PRD").split(",");
					ArrayList<String> skipList = new ArrayList<String>(Arrays.asList(skipPrds));
					if ("Y".equalsIgnoreCase(ConfigurationLoader.getParameters(false).getProperty("IMPS_CHG_P2A_YN").trim())
							&& !skipList.contains(acctno32digit.substring(0, 8).trim())) {
						HashMap<String, String> result2 = null;
						Session session = HBUtil.getSessionFactory().openSession();
						Transaction t = session.beginTransaction();
						D009022Id remId = new D009022Id();
						remId.setLbrCode(lbrcode);
						remId.setPrdAcctId(acctno32digit);
						D009022 remAcct = session.get(D009022.class, remId);
						int count = Integer
								.parseInt(ConfigurationLoader.getParameters(false).getProperty("IMPS_CHG_TRN_LIMIT"));
						if ((VoucherCommon.getTransactionsInAMonth(mob1, mmid1, session)) >= count
								&& !MSGConstants.acctTypeList.contains((long) remAcct.getAcctType())) {
							Double chgAmount = Double.valueOf(
									ConfigurationLoader.getParameters(false).getProperty("IMPS_P2A_CHG_AMOUNT").trim());
							if ("S".equalsIgnoreCase(
									ConfigurationLoader.getParameters(false).getProperty("IMPS_SER_OR_GST_CHG"))) {
								result2 = VoucherCommon.serviceChgRevVoucherEntry(lbrcode, rrn, session, acctno32digit,
										"IMPS Charges/P2A/" + rrn, chgAmount);
							} else {
								result2 = VoucherCommon.gstRevVoucherEntry(lbrcode, rrn, session, acctno32digit,
										"REV/IMPS Charges/P2A/" + rrn, chgAmount);
							}

							if (result2.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
								t.commit();
								P2AReversal.transactionEntry(String.valueOf(lbrcode), mob1, mmid1, mob2, mmid2,
										String.valueOf(amount), rrn, "C",
										Integer.valueOf(result.get(Code.SETNO) == null
												|| result.get(Code.SETNO).trim().isEmpty() ? "0"
														: result.get(Code.SETNO)),
										Integer.valueOf(result.get(Code.SCROLLNO) == null
												|| result.get(Code.SCROLLNO).trim().isEmpty() ? "0"
														: result.get(Code.SCROLLNO)),
										"MBTR", ResponseCodes.SUCCESS, ResponseCodes.SUCCESS_DESC, "");
							} else {
								P2AReversal.transactionEntry(String.valueOf(lbrcode), mob1, mmid1, mob2, mmid2,
										String.valueOf(amount), rrn, "C",
										Integer.valueOf(result.get(Code.SETNO) == null
												|| result.get(Code.SETNO).trim().length() < 1 ? "0"
														: result.get(Code.SETNO)),
										Integer.valueOf(result.get(Code.SCROLLNO) == null
												|| result.get(Code.SCROLLNO).trim().length() < 1 ? "0"
														: result.get(Code.SCROLLNO)),
										"MBTR", ResponseCodes.EXCEPTION_OCCURED, "Transaction failure", "");
							}
						} else {
							P2AReversal
									.transactionEntry(String.valueOf(lbrcode), mob1, mmid1, mob2, mmid2,
											String.valueOf(amount), rrn, "C",
											Integer.valueOf(result.get(Code.SETNO) == null
													|| result.get(Code.SETNO).trim().isEmpty() ? "0"
															: result.get(Code.SETNO)),
											Integer.valueOf(result.get(Code.SCROLLNO) == null
													|| result.get(Code.SCROLLNO).trim().isEmpty() ? "0"
															: result.get(Code.SCROLLNO)),
											"MBTR", ResponseCodes.SUCCESS, ResponseCodes.SUCCESS_DESC, "");
						}
					} else {
						P2AReversal.transactionEntry(String.valueOf(lbrcode), mob1, mmid1, mob2, mmid2,
								String.valueOf(amount), rrn, "C",
								Integer.valueOf(
										result.get(Code.SETNO) == null || result.get(Code.SETNO).trim().isEmpty() ? "0"
												: result.get(Code.SETNO)),
								Integer.valueOf(
										result.get(Code.SCROLLNO) == null || result.get(Code.SCROLLNO).trim().isEmpty()
												? "0"
												: result.get(Code.SCROLLNO)),
								"MBTR", ResponseCodes.SUCCESS, ResponseCodes.SUCCESS_DESC, "");
					}

					/*
					 * P2AReversal.transactionEntry(String.valueOf(lbrcode), mob1, mmid1, mob2,
					 * mmid2, String.valueOf(amount), rrn, "C",
					 * Integer.valueOf(result.get(Code.SETNO)),
					 * Integer.valueOf(result.get(Code.SCROLLNO)), "MBTR", ResponseCodes.SUCCESS,
					 * ResponseCodes.SUCCESS_DESC,"");
					 */
					return ResponseCodes.SUCCESS;
				} else {
					P2AReversal.transactionEntry(String.valueOf(lbrcode), mob1, mmid1, mob2, mmid2,
							String.valueOf(amount), rrn, "C",
							Integer.valueOf(
									result.get(Code.SETNO) == null || result.get(Code.SETNO).trim().length() < 1 ? "0"
											: result.get(Code.SETNO)),
							Integer.valueOf(
									result.get(Code.SCROLLNO) == null || result.get(Code.SCROLLNO).trim().length() < 1
											? "0"
											: result.get(Code.SCROLLNO)),
							"MBTR", ResponseCodes.EXCEPTION_OCCURED, "Transaction failure", "");
					return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
				}
			}
			return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
		}
		return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
	}

	public static IMPSTransactionResponse verificationReq(int lbrCode, String accNo, String transType, String narration,
			int amount, String rrn, String mob1, String mmid1, String mob2, String mmid2) {
		logger.error("<<<<<<<<< ::: In verificationReq():::>>>>>>>>");
		logger.error("Prameters Are::>> lbrCode" + lbrCode + " accNo::>>" + accNo + " transType::>>>" + transType
				+ " narrationP::>>" + narration + " amount::>>" + amount + " RRN::>>" + rrn + " mob1::>>" + mob1
				+ " mmid1::>>" + mmid1 + " mob2::>>" + mob2 + " mmid2::>" + mmid2);
		System.out.println("Prameters Are::>> lbrCode" + lbrCode + " accNo::>>" + accNo + " transType::>>>" + transType
				+ " narrationP::>>" + narration + " amount::>>" + amount + " RRN::>>" + rrn + " mob1::>>" + mob1
				+ " mmid1::>>" + mmid1 + " mob2::>>" + mob2 + " mmid2::>" + mmid2);
		IMPSTransactionResponse response = new IMPSTransactionResponse();
		if (transType.equalsIgnoreCase(MSGConstants.P2P_TRANSACTION)) {

			Session session = HBUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(D350044.class);
			criteria.add(Restrictions.eq("id.lbrCode", lbrCode));
			criteria.add(Restrictions.eq("id.batchCd", "MBTR"));
			criteria.add(Restrictions.eq("id.entryDate", DataUtils.getOpenDate(lbrCode)));
			criteria.add(Restrictions.eq("id.rrnNo", rrn));
			criteria.add(Restrictions.eq("id.mobNo1", mob1));
			criteria.add(Restrictions.eq("id.mmid1", mmid1));
			criteria.add(Restrictions.eq("id.mobNo2", mob2));
			criteria.add(Restrictions.eq("id.mmid2", mmid2));
			criteria.add(Restrictions.eq("id.tranAmt", amount));
			criteria.add(Restrictions.eq("id.drcr", "C"));
			List<D350044> list = criteria.list();
			session.close();
			session = null;
			System.out.println("list::>>>" + list);
			System.out.println("list.isEmpty()::>>>" + list.isEmpty());
			if (list == null || list.isEmpty()) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.RECORD_NOT_FOUND);
				response.setErrorCode(ResponseCodes.VERIFICATION_RESP_M0);
				response.setRrnNo(rrn);
				return response;
			}
			if (list.get(0).getId().getResponseCd().trim().equalsIgnoreCase("00")
					|| list.get(0).getId().getResponseCd().trim().equalsIgnoreCase("0")) {
				response.setResponse(MSGConstants.SUCCESS);
				response.setErrorMessage(MSGConstants.SUCCESS_MSG);
				response.setErrorCode(ResponseCodes.SUCCESS);
				response.setRrnNo(rrn);
				return response;
			} else {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.RECORD_NOT_FOUND);
				response.setErrorCode(ResponseCodes.VERIFICATION_RESP_M0);
				response.setRrnNo(rrn);
				return response;
			}
		} else if (transType.trim().equalsIgnoreCase(MSGConstants.P2A_TRANSACTION)) {
			logger.error("CoreBankingOperationImpl.verificationReq() mob1 " + mob1 + " len " + mob1.length());
			System.out.println("CoreBankingOperationImpl.verificationReq() mob1 " + mob1 + " len " + mob1.length());
			if (mob1.length() < 11) {
				mob1 = "91" + mob1;
			}
			logger.error("CoreBankingOperationImpl.verificationReq() mob1 after " + mob1 + " len " + mob1.length());
			System.out.println("CoreBankingOperationImpl.verificationReq() mob1 " + mob1 + " len " + mob1.length());
			Session session = HBUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(D350059.class);
			criteria.add(Restrictions.eq("id.lbrCode", lbrCode));
			// criteria.add(Restrictions.eq("id.batchCd", "MBTR"));
			criteria.add(Restrictions.eq("id.rrnNo", String.format("%1$-20s", rrn)));
			criteria.add(Restrictions.eq("id.tranAmt", amount));
			criteria.add(Restrictions.eq("id.drcr", String.format("%1$-2s", "C")));
			criteria.add(Restrictions.eq("id.mobNo1", String.format("%1$-20s", mob1)));
			criteria.add(Restrictions.eq("id.mmid1", String.format("%1$-15s", mmid1)));
			criteria.add(Restrictions.eq("id.accNo", String.format("%1$-20s", accNo)));
			criteria.add(Restrictions.eq("id.ifscCd", String.format("%1$-15s", mmid2)));
			// criteria.add(Restrictions.eq("id.responseCd", "00"));
			criteria.add(Restrictions.eq("id.entryDate", DataUtils.getOpenDate(lbrCode)));
			List<D350059> list = criteria.list();
			session.close();
			session = null;
			if (list == null || list.isEmpty()) {
				logger.error("LIST BLANK ");
				System.out.println("LIST BLANK ");
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.RECORD_NOT_FOUND);
				response.setErrorCode(ResponseCodes.VERIFICATION_RESP_M0);
				response.setRrnNo(rrn);
				return response;
			}
			if (list.get(0).getId().getResponseCd().trim().equalsIgnoreCase("00")) {
				response.setResponse(MSGConstants.SUCCESS);
				response.setErrorMessage(MSGConstants.SUCCESS_MSG);
				response.setErrorCode(ResponseCodes.SUCCESS);
				response.setRrnNo(rrn);
				return response;
			} else if (list.get(0).getId().getResponseCd().trim().equalsIgnoreCase("91")) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage("PENDING");
				response.setErrorCode(ResponseCodes.TIME_OUT);
				response.setRrnNo(rrn);
				return response;
			} else {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.RECORD_NOT_FOUND);
				response.setErrorCode(ResponseCodes.VERIFICATION_RESP_M0);
				response.setRrnNo(rrn);
				return response;
			}
		} else {
			logger.error("INVALID TRANSACTION TYPE ");
			System.out.println("INVALID TRANSACTION TYPE ");
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.INVALID_TRANSACTION_TYPE);
			response.setErrorCode(ResponseCodes.VERIFICATION_RESP_M0);
			return response;
		}
	}

	public String reverseDebitAccount(String accNo15digit, String transType, String narration, double amount,
			String rrn, String mob1, String mmid1, String mob2, String mmid2, String tType) {
		if (tType.equalsIgnoreCase("P2P")) {
			String acctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit);
			int lbrcode = Integer.parseInt(accNo15digit.substring(0, 3));
			logger.error("Account NO 32 Digit::>>" + acctno32digit);
			logger.error("Branch Code::>>" + lbrcode);
			HashMap<String, String> result = VoucherCommon.otherBankReverseVoucherEntry(lbrcode, acctno32digit, "IMPS",
					narration, amount, rrn);
			logger.error("Result:>>>" + result);
			if (null != result) {
				if (result.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
					P2PReversal.reverseTransactionEntry(String.valueOf(lbrcode), mob1, mmid1, mob2, mmid2,
							String.valueOf(amount), rrn, "C", Integer.valueOf(result.get(Code.SETNO)),
							Integer.valueOf(result.get(Code.SCROLLNO)));
					return ResponseCodes.SUCCESS;
				} else {
					P2PReversal.reverseTransactionEntry(String.valueOf(lbrcode), mob1, mmid1, mob2, mmid2,
							String.valueOf(amount), rrn, "C", Integer.valueOf(result.get("0")),
							Integer.valueOf(result.get("0")));
					return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
				}
			}
			return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
		} else if (tType.equalsIgnoreCase("P2A")) {
			String acctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit);
			int lbrcode = Integer.parseInt(accNo15digit.substring(0, 3));
			logger.error("Account NO 32 Digit::>>" + acctno32digit);
			logger.error("Branch Code::>>" + lbrcode);
			HashMap<String, String> result = VoucherCommon.otherBankReverseVoucherEntry(lbrcode, acctno32digit, "IMPS",
					narration, amount, rrn);
			logger.error("Result:>>>" + result);
			if (null != result) {
				if (result.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
					P2AReversal.transactionEntry(String.valueOf(lbrcode), mob1, mmid1, mob2, mmid2,
							String.valueOf(amount), rrn, "C", Integer.valueOf(result.get(Code.SETNO)),
							Integer.valueOf(result.get(Code.SCROLLNO)), "MBTR", ResponseCodes.SUCCESS,
							ResponseCodes.SUCCESS_DESC, "");
					return ResponseCodes.SUCCESS;
				} else {
					P2AReversal.transactionEntry(String.valueOf(lbrcode), mob1, mmid1, mob2, mmid2,
							String.valueOf(amount), rrn, "C", Integer.valueOf(result.get(Code.SETNO)),
							Integer.valueOf(result.get(Code.SCROLLNO)), "MBTR", ResponseCodes.EXCEPTION_OCCURED,
							MSGConstants.TRANSACTION_FAILS, "");
					return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
				}
			}
			return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
		}
		return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
	}

	public static GetStatus getStatusReq(int lbrCode, String accNo, String transType, String narration, Double amount,
			String rrn, String mob1, String mmid1, String mob2, String mmid2, String accNo15digit, GetStatus request) {
		logger.error("<<<<<<<<< ::: In getStatusReq():::>>>>>>>>");
		logger.error(" lbrCode" + lbrCode + " accNo::>>" + accNo + " transType::>>>" + transType + " narrationP::>>"
				+ narration + " amount::>>" + amount + " RRN::>>" + rrn + " mob1::>>" + mob1 + " mmid1::>>" + mmid1
				+ " mob2::>>" + mob2 + " mmid2::>" + mmid2);
		if (transType.trim().equalsIgnoreCase(MSGConstants.P2A_TRANSACTION)) {
			Session session = HBUtil.getSessionFactory().openSession();
			Criteria criteria = session.createCriteria(SimSePayTrancation.class);
			criteria.add(Restrictions.eq("brcode", Integer.valueOf(request.getAccountNo().substring(0, 3))));
			criteria.add(Restrictions.eq("accountno", request.getAccountNo()));
			criteria.add(Restrictions.eq("amount", Double.valueOf(amount)));
			criteria.add(Restrictions.eq("reconno", request.getTxnId().trim()));
			// criteria.add(Restrictions.eq("rrn",
			// request.getBankTxnId().trim()));
			criteria.add(Restrictions.eq("respcode", "00"));
			criteria.add(Restrictions.eq("drcr", "D"));
			List<SimSePayTrancation> list = criteria.list();
			session.close();
			session = null;
			if (list == null || list.isEmpty()) {
				request.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
				request.setTxnDesc(MSGConstants.TRANSACTION_NOT_FOUND);
				return request;
			} else {
				request.setTxnStatus(ResponseCodes.SIMSEPAY_SUCCESS);
				request.setTxnDesc(MSGConstants.SUCCESS_MSG);
				return request;
			}
		} else {
			request.setTxnStatus(ResponseCodes.SIMSEPAY_FAILURE);
			request.setTxnDesc(MSGConstants.INVALID_TRANSACTION_TYPE);
			return request;
		}
	}

	public String fundTransferOtherBank(String accNo15digit, String transType, String narration, double amount,
			String rrn, String mob1, String mmid1, String mob2, String mmid2, String tType, Session session) {
		// TODO Auto-generated method stub
		if (tType.equalsIgnoreCase("P2P")) {
			String acctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit);
			int lbrcode = Integer.parseInt(accNo15digit.substring(0, 3));
			logger.error("Account NO 32 Digit::>>" + acctno32digit);
			logger.error("Branch Code::>>" + lbrcode);

			HashMap<String, String> result = VoucherCommon.otherBankVoucherEntry(lbrcode, acctno32digit, "IMPS",
					narration, amount, rrn, session);
			logger.error("Result:>>>" + result);
			if (null != result) {
				if (result.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
					P2PTransactionEntry.p2pTransactionEntry(mmid1, mob1, mob2, mmid2, amount,
							Integer.valueOf((result.get(Code.SETNO) == null || result.get(Code.SETNO) == "") ? "0"
									: result.get(Code.SETNO)),
							Integer.valueOf((result.get(Code.SCROLLNO) == null || result.get(Code.SCROLLNO) == "") ? "0"
									: result.get(Code.SCROLLNO)),
							"MBTR", lbrcode, ResponseCodes.SUCCESS, ResponseCodes.SUCCESS_DESC, rrn);
					return ResponseCodes.SUCCESS;
				} else {
					P2PTransactionEntry.p2pTransactionEntry(mmid1, mob1, mob2, mmid2, amount,
							Integer.valueOf((result.get(Code.SETNO) == null || result.get(Code.SETNO) == "") ? "0"
									: result.get(Code.SETNO)),
							Integer.valueOf((result.get(Code.SCROLLNO) == null || result.get(Code.SCROLLNO) == "") ? "0"
									: result.get(Code.SCROLLNO)),
							"MBTR", lbrcode, "99", "Transaction Failure", rrn);
					return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
				}
			}
			return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
		} else if (tType.equalsIgnoreCase("P2A")) {
			String acctno32digit = AccountDetailsUtil.get32DigitAcctNo(accNo15digit);
			int lbrcode = Integer.parseInt(accNo15digit.substring(0, 3));
			logger.error("Account NO 32 Digit::>>" + acctno32digit);
			logger.error("Branch Code::>>" + lbrcode);
			HashMap<String, String> result = VoucherCommon.otherBankVoucherEntry(lbrcode, acctno32digit, "IMPS",
					narration, amount, rrn, session);
			logger.error("Result:>>>" + result);
			if (null != result) {
				if (result.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {

					String[] skipPrds = ConfigurationLoader.getParameters(false).getProperty("IMPS_SKIP_CHG_PRD").split(",");
					ArrayList<String> skipList = new ArrayList<String>(Arrays.asList(skipPrds));
					if ("Y".equalsIgnoreCase(ConfigurationLoader.getParameters(false).getProperty("IMPS_CHG_P2A_YN").trim())
							&& !skipList.contains(acctno32digit.substring(0, 8).trim())) {
						HashMap<String, String> result2 = null;
						D009022Id remId = new D009022Id();
						remId.setLbrCode(lbrcode);
						remId.setPrdAcctId(acctno32digit);
						D009022 remAcct = session.get(D009022.class, remId);

						int count = Integer
								.parseInt(ConfigurationLoader.getParameters(false).getProperty("IMPS_CHG_TRN_LIMIT"));
						if (VoucherCommon.getTransactionsInAMonth(mob1, mmid1, session) >= count
								&& !MSGConstants.acctTypeList.contains((long) remAcct.getAcctType())) {
							Double chgAmount = 0.0;
							if("Y".equalsIgnoreCase(ConfigurationLoader.getParameters(false).getProperty("IMPS_P2A_CHG_AMOUNT_SLAB_YN"))) {
								chgAmount = getFlatAmtForMsgType(amount,"P2A",session);
							}else
								chgAmount = Double.valueOf(
									ConfigurationLoader.getParameters(false).getProperty("IMPS_P2A_CHG_AMOUNT").trim());
							if ("S".equalsIgnoreCase(
									ConfigurationLoader.getParameters(false).getProperty("IMPS_SER_OR_GST_CHG"))) {
								result2 = VoucherCommon.serviceChargeVoucherEntry(lbrcode, rrn, session, acctno32digit,
										"IMPS Charges/P2A/" + rrn, chgAmount, amount);
							} else {
								result2 = VoucherCommon.gstVoucherEntry(lbrcode, rrn, session, acctno32digit,
										"IMPS Charges/P2A/" + rrn, chgAmount, amount);
							}
							if (result2.get(Code.RESULT).equalsIgnoreCase(Code.SUCCESS)) {
								P2ATransaction.p2aTransactionEntry(mmid1, mob1, mob2, mmid2, amount,
										Integer.valueOf(result.get(Code.SETNO)),
										Integer.valueOf(result.get(Code.SCROLLNO)), "MBTR", lbrcode,
										ResponseCodes.SUCCESS, ResponseCodes.SUCCESS_DESC, rrn);
								return ResponseCodes.SUCCESS;
							} else {
								P2ATransaction.p2aTransactionEntry(mmid1, mob1, mob2, mmid2, amount,
										Integer.valueOf(
												(result.get(Code.SETNO) == null || result.get(Code.SETNO) == "") ? "0"
														: result.get(Code.SETNO)),
										Integer.valueOf(
												(result.get(Code.SCROLLNO) == null || result.get(Code.SCROLLNO) == "")
														? "0"
														: result.get(Code.SCROLLNO)),
										"MBTR", lbrcode, "99", "Transaction Failure", rrn);
								return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
							}
						} else {
							P2ATransaction.p2aTransactionEntry(mmid1, mob1, mob2, mmid2, amount,
									Integer.valueOf(result.get(Code.SETNO)), Integer.valueOf(result.get(Code.SCROLLNO)),
									"MBTR", lbrcode, ResponseCodes.SUCCESS, ResponseCodes.SUCCESS_DESC, rrn);
							return ResponseCodes.SUCCESS;
						}
					} else {
						P2ATransaction.p2aTransactionEntry(mmid1, mob1, mob2, mmid2, amount,
								Integer.valueOf(result.get(Code.SETNO)), Integer.valueOf(result.get(Code.SCROLLNO)),
								"MBTR", lbrcode, ResponseCodes.SUCCESS, ResponseCodes.SUCCESS_DESC, rrn);
						return ResponseCodes.SUCCESS;
					}

				} else {
					P2ATransaction.p2aTransactionEntry(mmid1, mob1, mob2, mmid2, amount,
							Integer.valueOf((result.get(Code.SETNO) == null || result.get(Code.SETNO) == "") ? "0"
									: result.get(Code.SETNO)),
							Integer.valueOf((result.get(Code.SCROLLNO) == null || result.get(Code.SCROLLNO) == "") ? "0"
									: result.get(Code.SCROLLNO)),
							"MBTR", lbrcode, "99", "Transaction Failure", rrn);
					return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
				}
			}
			return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
		}
		return ResponseCodes.BILL_PAYMENT_NOT_SUCCESSFUL;
	}

	public HashMap<String, String> loanFundTransferBranch(D009022 accNo, D009022 benAccNo, String transType,
			String narration, double amount, String rrn, Session session) throws Exception {

		HashMap<String, String> result = VoucherCommon.otherBranchVouchers(accNo.getId().getLbrCode(),
				accNo.getId().getPrdAcctId(), benAccNo.getId().getLbrCode(), benAccNo.getId().getPrdAcctId(), transType,
				narration, narration, amount, rrn, session);
		logger.error("Result:>>>" + result);
		return result;

	}
	
	public Double getFlatAmtForMsgType(Double transAmt, String msgType, Session session) {
		Double amt = 0.0;
		
		/*String queryString = "\r\n" + 
				"SELECT t.flatRateAmt\r\n" + 
				"from D946124 t\r\n" + 
				"inner join (\r\n" + 
				"select id.msgSType, max(id.effDate) as MaxDate\r\n" + 
				"    from D946124 WHERE id.msgSType=:msgType\r\n" +  
				"    group by id.msgSType\r\n" +  
				") tm on t.id.msgSType = tm.id.msgSType and t.id.effDate = tm.MaxDate \r\n" +  
				"WHERE\r\n" + 
				" 	t.id.msgSType=:msgType AND uptoAmt>:amt ORDER BY uptoAmt ASC";*/
		
		String queryString = "SELECT TOP 1 t.FlatRateAmt\r\n" + 
				"\r\n" + 
				"from D946124 t\r\n" + 
				"\r\n" + 
				"inner join (\r\n" + 
				"\r\n" + 
				"    select MsgSType, max(EffDate) as MaxDate\r\n" + 
				"\r\n" + 
				"    from D946124 WHERE MsgSType='"+msgType+"'\r\n" + 
				"\r\n" + 
				"    group by MsgSType\r\n" + 
				"\r\n" + 
				") tm on t.MsgSType = tm.MsgSType and t.EffDate = tm.MaxDate \r\n" + 
				"\r\n" + 
				"WHERE\r\n" + 
				" 	t.MsgSType='"+msgType+"' AND UptoAmt>"+transAmt+" ORDER BY UptoAmt ASC";
		
		Query q = session.createSQLQuery(queryString);
		amt = (Double) q.getSingleResult();
		
		return amt;
	}
}
