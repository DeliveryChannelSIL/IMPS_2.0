package com.sil.loan;

import java.text.ParseException;
import java.util.Date;
import com.sil.hbm.D009040;
import com.sil.hbm.D009040Id;
import com.sil.hbm.D010004;
import com.sil.util.DateUtility;


public class VoucherUtil{

	

	public D009040 generateVoucher(int lbrCode, D010004 selectedBatch, String acctId, int setNo,
			int scrollNo, String narration, Double amount, String rrn, int mainScrollNo, short moduleType,
			String bookType, int usrCode2) throws NumberFormatException, ParseException {

		int timeWithoutDate = Integer
				.parseInt(DateUtility.getDateFromDateAsString(new Date(), "HHmmssSS").substring(0, 8));

		D009040 voucher = new D009040();
		D009040Id voucherId = new D009040Id();
		voucherId.setBatchCd(selectedBatch.getId().getBatchCd());
		voucherId.setEntryDate(selectedBatch.getId().getEntryDate());
		voucherId.setLbrCode(lbrCode);
		voucherId.setSetNo(setNo);
		voucherId.setScrollNo(scrollNo);
		voucher.setId(voucherId);
		voucher.setMainScrollNo(mainScrollNo);
		voucher.setPostDate(selectedBatch.getPostDate());
		voucher.setFeffDate(selectedBatch.getFeffDate());
		voucher.setValueDate(selectedBatch.getPostDate());

		voucher.setBookType(bookType);
		voucher.setVcrAcctId(acctId);
		voucher.setMainAcctId(acctId);

		voucher.setMainModType(moduleType);
		voucher.setVcrModType(moduleType);
		voucher.setTrnCurCd("INR");
		voucher.setFcyTrnAmt(amount);
		voucher.setLcyConvRate(1);
		voucher.setLcyTrnAmt(amount);
		voucher.setInstrBankCd((short) 0);
		voucher.setInstrBranchCd((short) 0);
		voucher.setInstrType((short) 99);
		voucher.setInstrNo(rrn + "");
		voucher.setInstrDate(new Date());
		voucher.setParticulars(narration);
		voucher.setSysGenVcr((byte) 0);
		voucher.setShTotFlag('Y');
		voucher.setShClrFlag('Y');
		voucher.setAcTotFlag('Y');
		voucher.setAcClrFlag('Y');
		voucher.setMaker(usrCode2);
		voucher.setMakerDate(selectedBatch.getId().getEntryDate());
		voucher.setMakerTime(timeWithoutDate);
		voucher.setChecker1(usrCode2);
		voucher.setChecker2(0);
		voucher.setChecker3(0);
		voucher.setChecker4(0);
		voucher.setCheckerDate(selectedBatch.getId().getEntryDate());
		voucher.setCheckerTime(timeWithoutDate);
		voucher.setNoAuthPending((byte) 0);
		voucher.setNoAuthOver((byte) 1);
		voucher.setPostFlag('P');
		voucher.setAuthFlag('A');
		voucher.setFeffFlag('F');
		voucher.setCanceledFlag(' ');
		voucher.setPostAuthFeffCncl((byte) 0);
		voucher.setUpdtChkId((short) 0);
		voucher.setPartClearAmt(0);
		voucher.setPostTime(timeWithoutDate);

		return voucher;

	}
	
	public D009040 generateAbbVoucher(D009040 voucher) {

		short modeType = 100;
		D009040Id voucherPk = voucher.getId();
		
		voucherPk.setScrollNo(voucherPk.getScrollNo());
		voucher.setId(voucherPk);
		voucher.setMainScrollNo(voucher.getMainScrollNo() + 1);
		
		if ((voucher.getDrCr() + "").equalsIgnoreCase("D")) {
			voucher.setCashFlowType("ABBCR");
			voucher.setDrCr('C');
			voucher.setActivityType("ABB");
		} else {
			voucher.setCashFlowType("ABBDR");
			voucher.setDrCr('D');
			voucher.setActivityType("ABBREM");
		}
		voucher.setVcrAcctId("ABB     000000000000000000000000");
		voucher.setVcrModType(modeType);
		voucher.setMainAcctId("ABB     000000000000000000000000");
		voucher.setMainModType(modeType);
		return voucher;
	}

}
