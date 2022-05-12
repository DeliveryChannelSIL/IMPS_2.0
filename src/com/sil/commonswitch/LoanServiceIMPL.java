package com.sil.commonswitch;
public class LoanServiceIMPL {
	public static void main(String[] args) {
		System.out.println("");
	}
	/*
	  public Voucher saveLoanVoucherForINSTLPAY4ForLoanWS(
				D030003 loanBalances, D030002 loanParameters,
				D009040 voucher, boolean isNewSet) {
			long l_start = System.currentTimeMillis();
			D009040 loanVoucherForChrg = new D009040();
			D009040 loanVoucherCr = new D009040();
			D009040 loanVoucherForPenalchrg = new D009040();
			D009040 customerVoucher = new D009040();
			Double otherCharges = 0.0;
			Double chrgAmount = 0.0;
			Double penaltyCharges = 0.0;
			Double penalChrg = 0.0;
			Double amount = 0D;
			String narration = " ";
			Long setNo = 0L;
			Long mainScrollNo = 0L;
			String irPrdAcctId = loanParameters.getIrPrdAcctId();
			
			ValidationResult validationResult = new ValidationResult();
//			voucher.setValidationResult(validationResult);
//			customerVoucher.setValidationResult(validationResult);
			amount = voucher.getFcyTrnAmt();
			try {
				
				if (loanBalances.getOthChgPrvdFcy() > loanBalances.getOthChgPaidFcy()) {
					otherCharges = loanBalances.getOthChgPrvdFcy()
							- loanBalances.getOthChgPaidFcy();
					if (amount > otherCharges) {
						chrgAmount = otherCharges;
						amount = amount - chrgAmount;
					} else {
						chrgAmount = amount;
						amount = amount - chrgAmount;
					}

					if (chrgAmount > 0) {
						loanVoucherForChrg = voucherProcess.setVoucherParamters(
								voucher.getLbrcode(), voucher.getBatchcd(), voucher
										.getProductNo(), voucher.getAccountNo());
						voucherProcess
								.setVoucherData(loanVoucherForChrg, voucher
										.getProductNo(), voucher.getAccountNo(),
										chrgAmount);
						voucherProcess.setDrCr(loanVoucherForChrg,
								SwiftCoreConstants.CREDIT);

						loanVoucherForChrg
								.setActivitytype(SwiftCoreConstants.INSTLPAY);
						loanVoucherForChrg
								.setCashflowtype(SwiftCoreConstants.LNOCHCR);// "LNOCHCR");
						loanVoucherForChrg.setFeffdate(getFormattedDateLocal(new Date()));
						// narration = loanBalances.getNarration().trim()+" Other
						// Charges";
						narration = voucher.getParticulars();
						//narration = "Other Charges"; // by prashant
						if (narration.length() >= 70) {
							narration = narration.substring(0, 70);
						}
						loanVoucherForChrg.setPostflag(SwiftCoreConstants.POSTING);
						loanVoucherForChrg.setPostdate(loanVoucherForChrg.getDailyBatchesDirectory().getPostdate());
						loanVoucherForChrg.setParticulars(narration);
						loanVoucherForChrg.setInstrtype(voucher.getInstrtype());
						//loanVoucherForChrg.setInstrno(SwiftCoreUtil
						//		.prependZeroInsNo(loanBalances.getInstrno()));
						loanVoucherForChrg.setInstrno(voucher.getInstrno());
						loanVoucherForChrg.setMaker(999998L);
						loanVoucherForChrg.setCheckerdate(getFormattedDateLocal(new Date()));
						loanVoucherForChrg.setCheckertime(DateUtility.getMakerTime(loanVoucherForChrg.getCheckerdate()));
						SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MMM/yyyy");
						Date makerDate = dateFormat1.parse(dateFormat1.format(new Date()));
						loanVoucherForChrg.setMakerdate(makerDate);
						loanVoucherForChrg.setValuedate(getFormattedDateLocal(new Date()));// loanBalances.getValuedate());
						loanVoucherForChrg.setInstrdate(getFormattedDateLocal(new Date()));
						loanVoucherForChrg.setAccount(voucherProcess
								.getAccountDao().getAccountInfo(
										voucher.getProductNo(),
										voucher.getAccountNo(),
										voucher.getLbrcode()));

						loanVoucherForChrg = batchProcess.updateAccountBalance(
								loanVoucherForChrg, 1);
						loanVoucherForChrg = batchProcess.updateAccountBalance(
								loanVoucherForChrg, 2);
						loanVoucherForChrg = voucherProcess.setLastAssignNumber(
								loanVoucherForChrg, isNewSet);
						if (isNewSet == true) {
							setNo = loanVoucherForChrg.getSetno();
							mainScrollNo = loanVoucherForChrg.getMainscrollno();
							voucher.setSetno(setNo);
							isNewSet = false;
						} else {
							loanVoucherForChrg.setSetno(voucher.getSetno());
							//loanVoucherForChrg.setInstrno(voucher.getInstrno());
							loanVoucherForChrg.setMainscrollno(voucher.getMainscrollno());
						}

						loanVoucherForChrg.setSystemFlag(voucher.getSystemFlag());
						loanVoucherForChrg.setChecker1(999998L);
						mainScrollNo = loanVoucherForChrg.getMainscrollno();
						try {
							voucherDao.saveVoucher(loanVoucherForChrg);
						} catch (Exception e) {
							transactionManager.rollback(transactionStatus);
							voucher.getValidationResult().setErrorMessage(
									"Error Generating Loan Chrg CR Voucher.");
							voucher.getValidationResult().setErrorCode("LN01");
							voucher.getValidationResult().setValid(false);
							logger.error("Error Generating Loan Chrg CR Voucher.",
									e);
							return voucher;
						}
						try {
							accountDao.updateBalances(loanVoucherForChrg
									.getAccount());
						} catch (Exception e) {
							transactionManager.rollback(transactionStatus);
							voucher
									.getValidationResult()
									.setErrorMessage(
											"Error Updating A/C Bal For Loan Chrg CR Voucher.");
							voucher.getValidationResult().setErrorCode("LN02");
							voucher.getValidationResult().setValid(false);
							logger
									.error(
											"Error Updating A/C Bal. For Loan Chrg CR Voucher.",
											e);
							return voucher;
						}
						try {
							systemparamDao.updateDailyBatchesDirecotry(
									loanVoucherForChrg.getDailyBatchesDirectory(),
									loanVoucherForChrg.getFcytrnamt(),
									loanVoucherForChrg.getDrcr(), 1);
						} catch (Exception e) {
							transactionManager.rollback(transactionStatus);
							voucher
									.getValidationResult()
									.setErrorMessage(
											"Error Updating Batch Bal For Loan Chrg CR Voucher.");
							voucher.getValidationResult().setErrorCode("LN03");
							voucher.getValidationResult().setValid(false);
							logger
									.error(
											"Error Updating Batch Bal For Loan Chrg CR Voucher.",
											e);
							return voucher;
						}
						loanBalances.setOthchgpaidfcy(loanBalances
								.getOthchgpaidfcy()
								+ chrgAmount);
						loanBalances.setOthchglcy(loanBalances.getOthchglcy()
								- chrgAmount);
					}
				}
				// Principal amount
				Double mainbalfcy = loanBalances.getMainbalfcy();
				if (mainbalfcy < 0) {
					mainbalfcy = mainbalfcy * (-1);
					if (amount > 0 && mainbalfcy != 0.0) {
						Double prinVchrAmount = 0.0;
						if (mainbalfcy - amount > 0) {
							prinVchrAmount = amount;
							amount = 0.0;
						} else if (mainbalfcy - amount < 0) {
							prinVchrAmount = mainbalfcy;
							amount = amount - mainbalfcy;
						}
						if (prinVchrAmount > 0) {
							loanVoucherCr = voucherProcess.setVoucherParamters(
									voucher.getLbrcode(), voucher.getBatchcd(),
									voucher.getProductNo(), voucher.getAccountNo());
							voucherProcess.setVoucherData(loanVoucherCr, voucher
									.getProductNo(), voucher.getAccountNo(),
									prinVchrAmount);
							voucherProcess.setDrCr(loanVoucherCr,
									SwiftCoreConstants.CREDIT);

							loanVoucherCr
									.setActivitytype(SwiftCoreConstants.INSTLPAY);
							loanVoucherCr.setCashflowtype(SwiftCoreConstants.LNPCR);
							loanVoucherCr.setFeffdate(getFormattedDateLocal(new Date()));
							// String naration = loanBalances.getNarration()+ " Main
							// Balance";
							narration = voucher.getParticulars();
							//narration = "Main Balance"; // by prashant
							if (narration.length() >= 70) {
								narration = narration.substring(0, 70);
							}
							loanVoucherCr.setPostflag(SwiftCoreConstants.POSTING);
							loanVoucherCr.setPostdate(loanVoucherCr.getDailyBatchesDirectory().getPostdate());
							loanVoucherCr.setParticulars(narration);
							loanVoucherCr.setInstrtype(voucher.getInstrtype());
							//loanVoucherCr.setInstrno(SwiftCoreUtil
							//		.prependZeroInsNo(loanBalances.getInstrno()));
							loanVoucherCr.setInstrno(voucher.getInstrno());
							loanVoucherCr.setMaker(999998L);
							loanVoucherCr.setCheckerdate(getFormattedDateLocal(new Date()));
							loanVoucherCr.setCheckertime(DateUtility.getMakerTime(loanVoucherCr.getCheckerdate()));
							SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MMM/yyyy");
							Date makerDate = dateFormat1.parse(dateFormat1.format(new Date()));
							loanVoucherCr.setMakerdate(makerDate);
							// loanVoucherCr.setValuedate(loanBalances.getValuedate());
							loanVoucherCr.setValuedate(getFormattedDateLocal(new Date()));
							loanVoucherCr.setInstrdate(getFormattedDateLocal(new Date()));
							loanVoucherCr.setAccount(voucherProcess.getAccountDao()
									.getAccountInfo(voucher.getProductNo(),
											voucher.getAccountNo(),
											voucher.getLbrcode()));
							loanVoucherCr = batchProcess.updateAccountBalance(
									loanVoucherCr, 1);
							loanVoucherCr = batchProcess.updateAccountBalance(
									loanVoucherCr, 2);
							loanVoucherCr = voucherProcess.setLastAssignNumber(
									loanVoucherCr, isNewSet);
							if (isNewSet == true) {
								setNo = loanVoucherCr.getSetno();
								mainScrollNo = loanVoucherCr.getMainscrollno();
								voucher.setSetno(setNo);
								isNewSet = false;
							} else {
								//loanVoucherCr.setSetno(setNo);
								loanVoucherCr.setSetno(voucher.getSetno());
								//loanVoucherCr.setInstrno(voucher.getInstrno());
								loanVoucherCr.setMainscrollno(voucher.getMainscrollno());
							}
							if (mainScrollNo != 0)
								loanVoucherCr.setMainscrollno(mainScrollNo);
							else
								mainScrollNo = loanVoucherCr.getMainscrollno();

							loanVoucherCr.setSystemFlag(voucher.getSystemFlag());
							loanVoucherCr.setChecker1(999998L);
							try {
								voucherDao.saveVoucher(loanVoucherCr);
							} catch (Exception e) {
								transactionManager.rollback(transactionStatus);
								voucher.getValidationResult().setErrorMessage(
										"Error Generating Loan Princ CR Voucher.");
								voucher.getValidationResult().setErrorCode("LN04");
								voucher.getValidationResult().setValid(false);
								logger
										.error("Error Generating Loan CR Voucher.",
												e);
								return voucher;
							}
							try {
								accountDao.updateBalances(loanVoucherCr
										.getAccount());
							} catch (Exception e) {
								transactionManager.rollback(transactionStatus);
								voucher
										.getValidationResult()
										.setErrorMessage(
												"Error Updating A/C Bal For Loan Princ CR Voucher");
								voucher.getValidationResult().setErrorCode("LN05");
								voucher.getValidationResult().setValid(false);
								logger
										.error(
												"Error Updating A/C Bal For Loan Princ CR Voucher",
												e);
								return voucher;
							}
							try {
								systemparamDao.updateDailyBatchesDirecotry(
										loanVoucherCr.getDailyBatchesDirectory(),
										loanVoucherCr.getFcytrnamt(), loanVoucherCr
												.getDrcr(), 1);
							} catch (Exception e) {
								transactionManager.rollback(transactionStatus);
								voucher
										.getValidationResult()
										.setErrorMessage(
												"Error Updating Batch Bal For Loan Princ CR Voucher.");
								voucher.getValidationResult().setErrorCode("LN06");
								voucher.getValidationResult().setValid(false);
								logger
										.error(
												"Error Updating Batch Bal For Loan Princ CR Voucher.",
												e);
								return voucher;
							}
						}
						loanBalances.setMainbalfcy(loanBalances.getMainbalfcy()
								+ prinVchrAmount);
					}
				}
				if (amount > 0) {
					// for penal charges
					if (loanBalances.getPenalprvdfcy() > loanBalances
							.getPenalpaidfcy()) {
						penaltyCharges = loanBalances.getPenalprvdfcy()
								- loanBalances.getPenalpaidfcy();
						if (amount > penaltyCharges) {
							penalChrg = penaltyCharges;
							amount = amount - penalChrg;
						} else {
							penalChrg = amount;
							amount = amount - penalChrg;
						}

						if (penalChrg > 0) {
							loanVoucherForPenalchrg = voucherProcess
									.setVoucherParamters(voucher.getLbrcode(),
											voucher.getBatchcd(), SwiftCoreUtil
													.getProductNumber(irPrdAcctId)
													.trim(), SwiftCoreUtil
													.getAccountNumber(irPrdAcctId)
													.trim());
							voucherProcess.setVoucherData(loanVoucherForPenalchrg,
									SwiftCoreUtil.getProductNumber(irPrdAcctId)
											.trim(), SwiftCoreUtil
											.getAccountNumber(irPrdAcctId).trim(),
									penalChrg);
							voucherProcess.setDrCr(loanVoucherForPenalchrg,
									SwiftCoreConstants.CREDIT);
							loanVoucherForPenalchrg
									.setActivitytype(SwiftCoreConstants.INSTLPAY);
							loanVoucherForPenalchrg
									.setCashflowtype(SwiftCoreConstants.LNPINTCR);
							loanVoucherForPenalchrg.setMainacctid(SwiftCoreUtil
									.getAccountNumber(voucher.getProductNo(),
											voucher.getAccountNo(), ""));
							loanVoucherForPenalchrg
									.setMainmodtype(SwiftCoreConstants.LOAN);
							loanVoucherForPenalchrg
									.setFeffdate(getFormattedDateLocal(new Date()));
							narration = voucher.getParticulars();
							//narration = "Interest Receivable"; // by prashant
							if (narration.length() >= 70) {
								narration = narration.substring(0, 70);
							}
							loanVoucherForPenalchrg.setPostflag(SwiftCoreConstants.POSTING);
							loanVoucherForPenalchrg.setPostdate(loanVoucherForPenalchrg.getDailyBatchesDirectory().getPostdate());
							loanVoucherForPenalchrg.setParticulars(narration);
							loanVoucherForPenalchrg.setInstrtype(voucher
									.getInstrtype());
							//loanVoucherForPenalchrg.setInstrno(SwiftCoreUtil
								//	.prependZeroInsNo(loanBalances.getInstrno()));
							loanVoucherForPenalchrg.setInstrno(voucher.getInstrno());
							loanVoucherForPenalchrg.setMaker(999998L);
							loanVoucherForPenalchrg.setCheckerdate(getFormattedDateLocal(new Date()));
							loanVoucherForPenalchrg.setCheckertime(DateUtility.getMakerTime(loanVoucherForPenalchrg.getCheckerdate()));
							SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MMM/yyyy");
							Date makerDate = dateFormat1.parse(dateFormat1.format(new Date()));
							loanVoucherForPenalchrg.setMakerdate(makerDate);
							loanVoucherForPenalchrg.setValuedate(getFormattedDateLocal(new Date()));
							loanVoucherForPenalchrg.setInstrdate(getFormattedDateLocal(new Date()));
							loanVoucherForPenalchrg.setAccount(voucherProcess
									.getAccountDao().getAccountInfo(
											voucher.getProductNo(),
											voucher.getAccountNo(),
											voucher.getLbrcode()));

							loanVoucherForPenalchrg = batchProcess
									.updateAccountBalance(loanVoucherForPenalchrg,
											1);
							loanVoucherForPenalchrg = batchProcess.updateAccountBalance(
									loanVoucherForPenalchrg, 2);
							loanVoucherForPenalchrg = voucherProcess
									.setLastAssignNumber(loanVoucherForPenalchrg,
											isNewSet);
							if (isNewSet == true) {
								setNo = loanVoucherForPenalchrg.getSetno();
								mainScrollNo = loanVoucherForPenalchrg
										.getMainscrollno();
								voucher.setSetno(setNo);
								isNewSet = false;
							} else {
								//loanVoucherForPenalchrg.setSetno(setNo);
								loanVoucherForPenalchrg.setSetno(voucher.getSetno());
								//loanVoucherForPenalchrg.setInstrno(voucher.getInstrno());
								loanVoucherForPenalchrg.setMainscrollno(voucher.getMainscrollno());
							}
							if (mainScrollNo != 0)
								loanVoucherForPenalchrg
										.setMainscrollno(mainScrollNo);
							else
								mainScrollNo = loanVoucherForPenalchrg
										.getMainscrollno();

							loanVoucherForPenalchrg.setSystemFlag(voucher.getSystemFlag());
							loanVoucherForPenalchrg.setChecker1(999998L);
							try {
								voucherDao.saveVoucher(loanVoucherForPenalchrg);
							} catch (Exception e) {
								transactionManager.rollback(transactionStatus);
								voucher
										.getValidationResult()
										.setErrorMessage(
												"Error Generating Loan Penal Chrg CR Voucher.");
								voucher.getValidationResult().setErrorCode("LN07");
								voucher.getValidationResult().setValid(false);
								logger
										.error(
												"Error Generating Loan Penal Chrg CR Voucher.",
												e);
								return voucher;
							}
							try {
								accountDao.updateBalances(loanVoucherForPenalchrg
										.getAccount());
							} catch (Exception e) {
								transactionManager.rollback(transactionStatus);
								voucher
										.getValidationResult()
										.setErrorMessage(
												"Error Updating A/C Bal For Loan Penal CR Voucher");
								voucher.getValidationResult().setErrorCode("LN08");
								voucher.getValidationResult().setValid(false);
								logger
										.error(
												"Error Updating A/C Bal. For Loan Penal CR Voucher",
												e);
								return voucher;
							}
							try {
								systemparamDao.updateDailyBatchesDirecotry(
										loanVoucherForPenalchrg
												.getDailyBatchesDirectory(),
										loanVoucherForPenalchrg.getFcytrnamt(),
										loanVoucherForPenalchrg.getDrcr(), 1);
							} catch (Exception e) {
								transactionManager.rollback(transactionStatus);
								voucher
										.getValidationResult()
										.setErrorMessage(
												"Error Updating Batch Bal For Loan Penal CR Voucher.");
								voucher.getValidationResult().setErrorCode("LN09");
								voucher.getValidationResult().setValid(false);
								logger
										.error(
												"Error Updating Batch Bal For Loan Penal CR Voucher.",
												e);
								return voucher;
							}
							loanBalances.setPenalpaidfcy(loanBalances
									.getPenalpaidfcy()
									+ penalChrg);
						}
					}
				}

				// Principal amount

				if (amount > 0) {

					loanVoucherCr = voucherProcess.setVoucherParamters(voucher
							.getLbrcode(), voucher.getBatchcd(), voucher
							.getProductNo(), voucher.getAccountNo());
					voucherProcess.setVoucherData(loanVoucherCr, voucher
							.getProductNo(), voucher.getAccountNo(), amount);
					voucherProcess
							.setDrCr(loanVoucherCr, SwiftCoreConstants.CREDIT);

					loanVoucherCr.setActivitytype(SwiftCoreConstants.INSTLPAY);
					loanVoucherCr.setCashflowtype(SwiftCoreConstants.LNPCR);
					loanVoucherCr.setFeffdate(getFormattedDateLocal(new Date()));
					// String naration = loanBalances.getNarration()+ " Main
					// Balance";
					narration = voucher.getParticulars();
					//narration = "Main Balance"; // by prashant
					if (narration.length() >= 70) {
						narration = narration.substring(0, 70);
					}
					loanVoucherCr.setPostflag(SwiftCoreConstants.POSTING);
					loanVoucherCr.setPostdate(loanVoucherCr.getDailyBatchesDirectory().getPostdate());
					loanVoucherCr.setParticulars(narration);
					loanVoucherCr.setInstrtype(voucher.getInstrtype());
					//loanVoucherCr.setInstrno(SwiftCoreUtil
					//		.prependZeroInsNo(loanBalances.getInstrno()));
					loanVoucherCr.setInstrno(voucher.getInstrno());
					loanVoucherCr.setMaker(999998L);
					loanVoucherCr.setCheckerdate(getFormattedDateLocal(new Date()));
					loanVoucherCr.setCheckertime(DateUtility.getMakerTime(loanVoucherCr.getCheckerdate()));
					SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MMM/yyyy");
					Date makerDate = dateFormat1.parse(dateFormat1.format(new Date()));
					loanVoucherCr.setMakerdate(makerDate);
					loanVoucherCr.setValuedate(getFormattedDateLocal(new Date()));
					loanVoucherCr.setInstrdate(getFormattedDateLocal(new Date()));
					loanVoucherCr.setAccount(voucherProcess.getAccountDao()
							.getAccountInfo(voucher.getProductNo(),
									voucher.getAccountNo(), voucher.getLbrcode()));
					loanVoucherCr = batchProcess.updateAccountBalance(
							loanVoucherCr, 1);
					loanVoucherCr = batchProcess.updateAccountBalance(
							loanVoucherCr, 2);
					loanVoucherCr = voucherProcess.setLastAssignNumber(
							loanVoucherCr, isNewSet);
					if (isNewSet == true) {
						setNo = loanVoucherCr.getSetno();
						mainScrollNo = loanVoucherCr.getMainscrollno();
						voucher.setSetno(setNo);
						isNewSet = false;
					} else {
						//loanVoucherCr.setSetno(setNo);
						loanVoucherCr.setSetno(voucher.getSetno());
						//loanVoucherCr.setInstrno(voucher.getInstrno());
						loanVoucherCr.setMainscrollno(voucher.getMainscrollno());
					}
					if (mainScrollNo != 0)
						loanVoucherCr.setMainscrollno(mainScrollNo);
					else
						mainScrollNo = loanVoucherCr.getMainscrollno();

					loanVoucherCr.setSystemFlag(voucher.getSystemFlag());
					loanVoucherCr.setChecker1(999998L);
					try {
						voucherDao.saveVoucher(loanVoucherCr);
					} catch (Exception e) {
						transactionManager.rollback(transactionStatus);
						voucher.getValidationResult().setErrorMessage(
								"Error Generating Loan Princ CR Voucher.");
						voucher.getValidationResult().setErrorCode("LN10");
						voucher.getValidationResult().setValid(false);
						logger.error("Error Generating Loan Princ CR Voucher.", e);
						return voucher;
					}
					try {
						accountDao.updateBalances(loanVoucherCr.getAccount());
					} catch (Exception e) {
						transactionManager.rollback(transactionStatus);
						voucher.getValidationResult().setErrorMessage(
								"Error Updating A/C Bal For Loan Princ CR Voucher");
						voucher.getValidationResult().setErrorCode("LN11");
						voucher.getValidationResult().setValid(false);
						logger
								.error(
										"Error Updating A/C Bal. For Loan Princ CR Voucher",
										e);
						return voucher;
					}
					try {
						systemparamDao.updateDailyBatchesDirecotry(loanVoucherCr
								.getDailyBatchesDirectory(), loanVoucherCr
								.getFcytrnamt(), loanVoucherCr.getDrcr(), 1);
					} catch (Exception e) {
						transactionManager.rollback(transactionStatus);
						voucher
								.getValidationResult()
								.setErrorMessage(
										"Error Updating Batch Bal For Loan Princ CR Voucher.");
						voucher.getValidationResult().setErrorCode("LN12");
						voucher.getValidationResult().setValid(false);
						logger
								.error(
										"Error Updating Batch Bal For Loan Princ CR Voucher.",
										e);
						return voucher;
					}
					loanBalances.setMainbalfcy(loanBalances.getMainbalfcy()
							+ amount);
				}
				// Saving Loan Balances

				loanBalances.setMainballcy(loanBalances.getMainbalfcy()
						* loanBalances.getConvrate());
				loanBalances.setDisbursedamtlcy(loanBalances.getDisbursedamtfcy()
						* loanBalances.getConvrate());
				loanBalances.setDbtrupdtchkid(loanBalances.getDbtrupdtchkid() + 1);
				try {
					loanDao.updateLoanBalances(loanBalances);
				} catch (Exception e) {
					transactionManager.rollback(transactionStatus);
					voucher.getValidationResult().setErrorMessage(
							"Error Updating Loan Balances CR Voucher.");
					voucher.getValidationResult().setErrorCode("LN13");
					voucher.getValidationResult().setValid(false);
					logger.error("Error Updating Loan Balances CR Voucher.", e);
					return voucher;
				}
				if (chrgAmount > 0) {
					customerVoucher = loanVoucherForChrg;
				} else if (mainbalfcy != 0) {
					customerVoucher = loanVoucherCr;
				} else if (penalChrg > 0) {
					customerVoucher = loanVoucherForPenalchrg;
				} else {
					customerVoucher = loanVoucherCr;
				}
				customerVoucher.getValidationResult().setValid(true);
				transactionManager.commit(transactionStatus);

			} catch (Exception ex) {
				transactionManager.rollback(transactionStatus);
				voucher.getValidationResult().setErrorMessage(
						"Error Setting Voucher Params For Loan Type04 CR Voucher.");
				voucher.getValidationResult().setErrorCode("LN14");
				voucher.getValidationResult().setValid(false);
				logger
						.error(
								"Error While Setting Voucher Params For Loan Type04 CR Voucher.",
								ex);
				return voucher;
			}
			long l_end = System.currentTimeMillis();
			logger
					.debug("Instrumentation :<VoucherServiceImpl.java>:<saveLoanVoucherForINSTLPAY4ForNEFT>: "
							+ (l_end - l_start));
			return customerVoucher;
		}

		public Voucher setVoucherParamters(Long branchCode,String batchCode,String productCode,String accountNo) throws DataAccessException, SQLException{
			long l_start = System.currentTimeMillis();
			Voucher voucher = new Voucher();
			BatchCodeMaintenance batchCodeMaintenance = voucherDao.getBatchMaintenanceData(branchCode,batchCode.trim());
			ProductMaster productMaster = accountDao.getProductAccountBalance(branchCode, productCode.trim());
			SystemParameter systemParameter = systemparamDao.getSystemParameter(branchCode, SwiftCoreConstants.LOD_CODE);
			//Date branchOperationDate = DateUtility.getDateFromString(systemParameter.getValue().trim().substring(1));
			Date branchOperationDate=getDateFromStringLocal(systemParameter.getValue().trim().substring(1));
			DailyBatchesDirectory dailyBatchesDirectory = voucherDao.getDailyBatchData(branchCode, batchCode, branchOperationDate);
			voucher.setBatchCodeMaintenance(batchCodeMaintenance);
			voucher.setProductMaster(productMaster);
			voucher.setDailyBatchesDirectory(dailyBatchesDirectory);
			voucher.setEntrydate(branchOperationDate);
			voucher.setValuedate(branchOperationDate);
			voucher.setInstrdate(branchOperationDate);
			*//***commented by Shubhra on June 01, 2012*for bug 3720**start**//*
			//voucher.setSysgenvcr(2L);
			*//***commented by Shubhra on June 01, 2012*for bug 3720**end**//*
			*//****Modified BY Indrasena on Aug 06,2014**For NGRTGS/NEFT Maker date Issue****//*
			//voucher.setMakerdate(DateUtility.getFormattedDate(new Date()));//DateUtility.getcurrentDate()));
			Date makerDate =getFormattedDateLocal(new Date());
			voucher.setMakerdate(makerDate);
			voucher.setMakertime(DateUtility.getMakerTime(voucher.getMakerdate()));
			voucher.setBatchcd(batchCode.trim());
			voucher.setLbrcode(branchCode);
			voucher.setMaker(voucher.getUser().getUserDetails().getUsrcode2());
			long l_end = System.currentTimeMillis();
			logger.debug("Instrumentation :<VoucherProcess.java>:<setVoucherParamters>: " + (l_end - l_start));
			return voucher;
		}
*/}
