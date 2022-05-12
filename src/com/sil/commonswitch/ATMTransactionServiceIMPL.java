package com.sil.commonswitch;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import com.sil.constants.MSGConstants;
import com.sil.constants.ResponseCodes;
import com.sil.domain.ATMTransactionRequest;
import com.sil.domain.ATMTransactionResponse;
import com.sil.domain.IMPSChargesResponse;
import com.sil.domain.TransactionValidationResponse;
import com.sil.hbm.Atmcharges;
import com.sil.hbm.AtmchargesId;
import com.sil.hbm.D001004;
import com.sil.hbm.D009022;
import com.sil.hbm.D100001;
import com.sil.hbm.D100002;
import com.sil.hbm.D350066;
import com.sil.prop.ConfigurationLoader;
import com.sil.service.TransactionServiceImpl;
import com.sil.util.HBUtil;
import com.sil.util.Props;

public class ATMTransactionServiceIMPL {
	public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ATMTransactionServiceIMPL.class);

	public static ATMTransactionResponse doATMTransaction(ATMTransactionRequest request) {
		// TODO Auto-generated method stub
		ATMTransactionResponse response = new ATMTransactionResponse();
		try {
			System.out.println("request::>>>" + request);
			logger.error("request::>>>" + request);
			if (request.getTransType().equalsIgnoreCase(MSGConstants.LOCAL)) {
				Session session = HBUtil.getSessionFactory().openSession();
				Transaction t = session.beginTransaction();
				if (Integer.valueOf(request.getBrCode()) == Integer.valueOf(request.getToBrcode())) {
					// Same Branch transaction
					ATMVoucher voucher = new ATMVoucher();
					int setNo = ATMVoucher.getNextSetNo();
					int scrollNo = ATMVoucher.getNextScrollNo();
					System.out.println("setNo:>>>" + setNo);
					System.out.println("scrollNo::>>" + scrollNo);
					// request.setTransType("ATM");
					String narration = "ATM Cash Withdrawl";
					voucher.debit(Integer.valueOf(request.getBrCode().trim()), request.getAtmAccId(), MSGConstants.ATM,
							setNo, scrollNo, narration, request.getAmount(), request.getRrn(), session);
					if (!voucher.isAborted) {
						if (VoucherMPOS
								.updateProductBalances(request.getAmount(), Integer.valueOf(request.getBrCode().trim()),
										MSGConstants.DR, request.getAtmAccId().substring(0, 8).trim(), session)
								.equalsIgnoreCase(MSGConstants.SUCCESS)) {
							String balResponse = CoreTransactionMPOS.balance(
									Integer.valueOf(request.getBrCode().trim()), request.getAtmAccId().trim(),
									request.getAmount(), "D", session);
							if (balResponse != null && balResponse.trim().length() > 0
									&& !balResponse.trim().equalsIgnoreCase("99")
									&& !balResponse.trim().equalsIgnoreCase("51")) {
								D350066 d350066 = DataUtils.prepareD350066Obj(request, "D", setNo, scrollNo);
								session.save(d350066);
								t.commit();
								session.close();
								session = null;
								logger.error(MSGConstants.SUCCESFUL_TRN);
								response.setResponse(MSGConstants.SUCCESS);
								response.setErrorMsg(MSGConstants.SUCCESFUL_TRN);
								response.setRespCode(ResponseCodes.SUCCESS);
								response.setRrn(request.getRrn());
								return response;
							} else {
								t.rollback();
								session.close();
								session = null;
								logger.error(MSGConstants.TRANSACTION_DECLINED);
								response.setRrn(request.getRrn());
								response.setResponse(MSGConstants.ERROR);
								response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
								response.setRespCode(ResponseCodes.SYSTEM_ERROR);
								return response;
							}
						} else {
							t.rollback();
							session.close();
							session = null;
							response.setRrn(request.getRrn());
							logger.error(MSGConstants.TRANSACTION_DECLINED);
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
							response.setRespCode(ResponseCodes.SYSTEM_ERROR);
							return response;
						}
					} else {
						t.rollback();
						session.close();
						session = null;
						response.setRrn(request.getRrn());
						logger.error(MSGConstants.TRANSACTION_DECLINED);
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
						response.setRespCode(ResponseCodes.SYSTEM_ERROR);
						return response;
					}
				} else {
					// Other Branch transaction
					ATMVoucher voucher = new ATMVoucher();
					int setNo = ATMVoucher.getNextSetNo();
					int scrollNo = ATMVoucher.getNextScrollNo();
					int reconNo = ATMVoucher.getNextReconNo(Integer.valueOf(request.getBrCode()));
					int benSetNo = VoucherMPOS.getNextSetNo();
					int benScrollNo = VoucherMPOS.getNextScrollNo();
					int scrollNoNew = VoucherMPOS.getNextScrollNo();
					System.out.println("setNo:>>>" + setNo);
					System.out.println("scrollNo::>>" + scrollNo);
					String narration = "ATM Cash Withdrawl";
					voucher.debit(Integer.valueOf(request.getBrCode().trim()), request.getAtmAccId(), MSGConstants.ATM,
							benSetNo, scrollNoNew, narration, request.getAmount(), request.getRrn(), session);
					if (!voucher.isAborted) {
						if (VoucherMPOS
								.updateProductBalances(request.getAmount(), Integer.valueOf(request.getBrCode().trim()),
										MSGConstants.DR, request.getAtmAccId().substring(0, 8).trim(), session)
								.equalsIgnoreCase(MSGConstants.SUCCESS)) {
							voucher.creditABB(Integer.valueOf(request.getBrCode()), MSGConstants.ABB_ACC,
									MSGConstants.ABB, benSetNo, benScrollNo, narration,
									Integer.valueOf(request.getToBrcode().trim()), request.getAmount(),
									request.getRrn(), reconNo, session);
							if (!voucher.isAborted) {
								if (VoucherMPOS
										.updateProductBalances(request.getAmount(),
												Integer.valueOf(request.getBrCode().trim()), MSGConstants.CR,
												MSGConstants.ABB_ACC.substring(0, 8).trim(), session)
										.equalsIgnoreCase(MSGConstants.SUCCESS)) {

									voucher.debitABB(Integer.valueOf(request.getToBrcode()), MSGConstants.ABB_ACC,
											MSGConstants.ABB, setNo, scrollNo, narration,
											Integer.valueOf(request.getToBrcode()), request.getAmount(),
											request.getRrn(), reconNo, session);
									if (!voucher.isAborted) {
										if (VoucherMPOS
												.updateProductBalances(request.getAmount(),
														Integer.valueOf(request.getToBrcode().trim()), MSGConstants.DR,
														MSGConstants.ABB_ACC.substring(0, 8).trim(), session)
												.equalsIgnoreCase(MSGConstants.SUCCESS)) {
											String balResponse = CoreTransactionMPOS.balance(
													Integer.valueOf(request.getBrCode()), request.getAtmAccId(),
													request.getAmount(), MSGConstants.DR, session);
											if (balResponse != null && balResponse.trim().length() > 0
													&& !balResponse.trim().equalsIgnoreCase("99")
													&& !balResponse.trim().equalsIgnoreCase("51")) {
												String batchCodes[] = Props.getBatchProperty(MSGConstants.ATM)
														.split("~");
												D001004 d04OnlineBatchName = VoucherMPOS
														.getBatchNameFromBatchCode(batchCodes[0]);
												String onlineBatchName = d04OnlineBatchName.getValue().trim();
												String batchCodes1[] = Props.getBatchProperty(MSGConstants.ATM)
														.split("~");
												D001004 d001004 = VoucherMPOS.getBatchNameFromBatchCode(batchCodes1[0]);
												String benBatchCode = d001004.getValue().trim();

												D100001 d100001 = VoucherMPOS.prepareReconObj(
														Integer.valueOf(request.getToBrcode()), reconNo,
														DataUtils.getOpenDate(Integer.valueOf(request.getToBrcode())),
														999999, onlineBatchName, benBatchCode, setNo, scrollNo,
														benSetNo, benScrollNo, benScrollNo,
														Integer.valueOf(request.getBrCode()),
														"" + request.getAtmAccId(), request.getAmount(), "D");
												// System.out.println("d100001::>>>"
												// + d100001);

												D100002 d100002 = VoucherMPOS.prepareRecon2Obj(
														Integer.valueOf(request.getToBrcode()), reconNo,
														DataUtils.getOpenDate(Integer.valueOf(request.getBrCode())),
														999999, onlineBatchName, benBatchCode, setNo, scrollNo,
														benSetNo, scrollNoNew, scrollNoNew,
														Integer.valueOf(request.getBrCode()),
														"" + request.getAtmAccId(), request.getAmount(), "D");
												// System.out.println("d100001::>>>"
												// + d100002);
												try {
													D350066 d350066 = DataUtils.prepareD350066Obj(request, "D", setNo,
															scrollNo);
													session.save(d350066);
													session.save(d100001);
													session.save(d100002);
													session.flush();
													t.commit();
													session.close();
													session = null;
													t = null;
													response.setResponse(MSGConstants.SUCCESS);
													response.setErrorMsg(MSGConstants.SUCCESS_MSG);
													response.setRrn(request.getRrn());
													return response;
												} catch (Exception e) {
													session.close();
													session = null;
													t = null;
													e.printStackTrace();
													logger.error(MSGConstants.WEB_SERVICE_ERROR);
													response.setRrn(request.getRrn());
													response.setResponse(MSGConstants.ERROR);
													response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
													response.setRespCode(ResponseCodes.SYSTEM_ERROR);
													return response;
												}
											} else {
												session.close();
												session = null;
												t = null;
												logger.error(MSGConstants.WEB_SERVICE_ERROR);
												response.setRrn(request.getRrn());
												response.setResponse(MSGConstants.ERROR);
												response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
												response.setRespCode(ResponseCodes.SYSTEM_ERROR);
												return response;
											}
										} else {
											session.close();
											session = null;
											t = null;
											logger.error(MSGConstants.WEB_SERVICE_ERROR);
											response.setRrn(request.getRrn());
											response.setResponse(MSGConstants.ERROR);
											response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
											response.setRespCode(ResponseCodes.SYSTEM_ERROR);
											return response;
										}
									} else {
										logger.error(MSGConstants.TRANSACTION_DECLINED);
										t.rollback();
										session.close();
										session = null;
										response.setRrn(request.getRrn());
										response.setResponse(MSGConstants.ERROR);
										response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
										response.setRespCode(ResponseCodes.SYSTEM_ERROR);
										return response;
									}
								} else {
									logger.error(MSGConstants.TRANSACTION_DECLINED);
									t.rollback();
									session.close();
									session = null;
									response.setRrn(request.getRrn());
									response.setResponse(MSGConstants.ERROR);
									response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
									response.setRespCode(ResponseCodes.SYSTEM_ERROR);
									return response;
								}
							} else {
								logger.error(MSGConstants.TRANSACTION_DECLINED);
								t.rollback();
								session.close();
								session = null;
								response.setRrn(request.getRrn());
								response.setResponse(MSGConstants.ERROR);
								response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
								response.setRespCode(ResponseCodes.SYSTEM_ERROR);
								return response;
							}

						} else {
							t.rollback();
							session.close();
							session = null;
							response.setRrn(request.getRrn());
							logger.error(MSGConstants.TRANSACTION_DECLINED);
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
							response.setRespCode(ResponseCodes.SYSTEM_ERROR);
							return response;
						}
					} else {
						t.rollback();
						session.close();
						session = null;
						response.setRrn(request.getRrn());
						logger.error(MSGConstants.TRANSACTION_DECLINED);
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
						response.setRespCode(ResponseCodes.SYSTEM_ERROR);
						return response;
					}
				}
			} else if (request.getTransType().equalsIgnoreCase(MSGConstants.ISSUER)) {
				Session session = HBUtil.getSessionFactory().openSession();
				Transaction t = session.beginTransaction();
				if (Integer.valueOf(request.getBrCode()) == Integer.valueOf(request.getToBrcode())) {
					// Same Branch transaction

				} else {
					// Other branch transaction
					ATMVoucher voucher = new ATMVoucher();
					int setNo = ATMVoucher.getNextSetNo();
					int scrollNo = ATMVoucher.getNextScrollNo();
					int reconNo = ATMVoucher.getNextReconNo(Integer.valueOf(request.getBrCode()));
					int benSetNo = VoucherMPOS.getNextSetNo();
					int benScrollNo = VoucherMPOS.getNextScrollNo();
					int scrollNoNew = VoucherMPOS.getNextScrollNo();
					System.out.println("setNo:>>>" + setNo);
					System.out.println("scrollNo::>>" + scrollNo);
					String narration = "ATM Cash Withdrawl";
					D001004 d0010004 = DataUtils.getSystemParameter(0, MSGConstants.ISSUER_PROD_CODE);
					if (d0010004 == null) {
						t = null;
						session.close();
						session = null;
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMsg(MSGConstants.SYSTEM_PARAMETER_NOT_FOUND);
						response.setRrn(request.getRrn());
						return response;
					}
					D001004 sysParamBr = DataUtils.getSystemParameter(0, MSGConstants.ISSUER_BR_CODE);
					if (sysParamBr == null) {
						t = null;
						session.close();
						session = null;
						response.setRrn(request.getRrn());
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMsg(MSGConstants.SYSTEM_PARAMETER_NOT_FOUND);
						return response;
					}
					voucher.debit(Integer.valueOf(request.getBrCode().trim()), request.getAtmAccId(), MSGConstants.ATM,
							benSetNo, scrollNoNew, narration, request.getAmount(), request.getRrn(), session);
					if (!voucher.isAborted) {
						if (VoucherMPOS
								.updateProductBalances(request.getAmount(), Integer.valueOf(request.getBrCode().trim()),
										MSGConstants.DR, request.getAtmAccId().substring(0, 8).trim(), session)
								.equalsIgnoreCase(MSGConstants.SUCCESS)) {
							voucher.creditABB(Integer.valueOf(request.getBrCode()), MSGConstants.ABB_ACC,
									MSGConstants.ABB, benSetNo, benScrollNo, narration,
									Integer.valueOf(request.getToBrcode().trim()), request.getAmount(),
									request.getRrn(), reconNo, session);

							if (!voucher.isAborted) {
								if (VoucherMPOS
										.updateProductBalances(request.getAmount(),
												Integer.valueOf(request.getToBrcode().trim()), MSGConstants.CR,
												MSGConstants.ABB_ACC.substring(0, 8).trim(), session)
										.equalsIgnoreCase(MSGConstants.SUCCESS)) {

									voucher.debitABB(Integer.valueOf(sysParamBr.getValue().trim()),
											MSGConstants.ABB_ACC, MSGConstants.ABB, setNo, scrollNo, narration,
											Integer.valueOf(request.getToBrcode()), request.getAmount(),
											request.getRrn(), reconNo, session);
									if (!voucher.isAborted) {
										if (VoucherMPOS
												.updateProductBalances(request.getAmount(),
														Integer.valueOf(sysParamBr.getValue().trim()), MSGConstants.DR,
														MSGConstants.ABB_ACC.substring(0, 8).trim(), session)
												.equalsIgnoreCase(MSGConstants.SUCCESS)) {

											voucher.credit(Integer.valueOf(sysParamBr.getValue().trim()),
													String.format("%-8s", d0010004.getValue().trim())
															+ "000000000000000000000000",
													MSGConstants.ATM, benSetNo, benScrollNo, narration,
													request.getAmount(), request.getRrn(), session);
											if (!voucher.isAborted) {
												if (VoucherMPOS
														.updateProductBalances(request.getAmount(),
																Integer.valueOf(sysParamBr.getValue().trim()),
																MSGConstants.CR, d0010004.getValue().trim(), session)
														.equalsIgnoreCase(MSGConstants.SUCCESS)) {

													String balResponse = CoreTransactionMPOS.balance(
															Integer.valueOf(request.getBrCode().trim()),
															request.getAtmAccId().trim(), request.getAmount(), "D",
															session);
													if (balResponse != null && balResponse.trim().length() > 0
															&& !balResponse.trim().equalsIgnoreCase("99")
															&& !balResponse.trim().equalsIgnoreCase("51")) {

														D350066 d350066 = DataUtils.prepareD350066Obj(request, "D",
																setNo, scrollNo);
														session.save(d350066);
														t.commit();
														session.close();
														session = null;
														logger.error(MSGConstants.TRANSACTION_DECLINED);
														response.setRrn(request.getRrn());
														response.setResponse(MSGConstants.SUCCESS);
														response.setErrorMsg(MSGConstants.SUCCESFUL_TRN);
														response.setRespCode(ResponseCodes.SUCCESS);
														return response;
													} else {
														t.rollback();
														session.close();
														session = null;
														logger.error(MSGConstants.TRANSACTION_DECLINED);
														response.setRrn(request.getRrn());
														response.setResponse(MSGConstants.ERROR);
														response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
														response.setRespCode(ResponseCodes.SYSTEM_ERROR);
														return response;
													}
												} else {
													t.rollback();
													session.close();
													session = null;
													logger.error(MSGConstants.TRANSACTION_DECLINED);
													response.setRrn(request.getRrn());
													response.setResponse(MSGConstants.ERROR);
													response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
													response.setRespCode(ResponseCodes.SYSTEM_ERROR);
													return response;
												}
											} else {
												t.rollback();
												session.close();
												session = null;
												logger.error(MSGConstants.TRANSACTION_DECLINED);
												response.setRrn(request.getRrn());
												response.setResponse(MSGConstants.ERROR);
												response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
												response.setRespCode(ResponseCodes.SYSTEM_ERROR);
												return response;
											}
										} else {
											t.rollback();
											session.close();
											session = null;
											logger.error(MSGConstants.TRANSACTION_DECLINED);
											response.setRrn(request.getRrn());
											response.setResponse(MSGConstants.ERROR);
											response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
											response.setRespCode(ResponseCodes.SYSTEM_ERROR);
											return response;
										}
									} else {
										t.rollback();
										session.close();
										session = null;
										logger.error(MSGConstants.TRANSACTION_DECLINED);
										response.setRrn(request.getRrn());
										response.setResponse(MSGConstants.ERROR);
										response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
										response.setRespCode(ResponseCodes.SYSTEM_ERROR);
										return response;
									}
								} else {
									t.rollback();
									session.close();
									session = null;
									response.setRrn(request.getRrn());
									logger.error(MSGConstants.TRANSACTION_DECLINED);
									response.setResponse(MSGConstants.ERROR);
									response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
									response.setRespCode(ResponseCodes.SYSTEM_ERROR);
									return response;
								}
							} else {
								t.rollback();
								session.close();
								session = null;
								response.setRrn(request.getRrn());
								logger.error(MSGConstants.TRANSACTION_DECLINED);
								response.setResponse(MSGConstants.ERROR);
								response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
								response.setRespCode(ResponseCodes.SYSTEM_ERROR);
								return response;
							}
						} else {
							t.rollback();
							session.close();
							session = null;
							response.setRrn(request.getRrn());
							logger.error(MSGConstants.TRANSACTION_DECLINED);
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
							response.setRespCode(ResponseCodes.SYSTEM_ERROR);
							return response;
						}
					} else {
						t.rollback();
						session.close();
						session = null;
						logger.error(MSGConstants.TRANSACTION_DECLINED);
						response.setRrn(request.getRrn());
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
						response.setRespCode(ResponseCodes.SYSTEM_ERROR);
						return response;
					}

				}
			} else if (request.getTransType().equalsIgnoreCase(MSGConstants.ACQUIRER)) {
				Session session = HBUtil.getSessionFactory().openSession();
				Transaction t = session.beginTransaction();
				if (Integer.valueOf(request.getBrCode()) == Integer.valueOf(request.getToBrcode())) {
					// Same Branch transaction
					ATMVoucher voucher = new ATMVoucher();
					int setNo = ATMVoucher.getNextSetNo();
					int scrollNo = ATMVoucher.getNextScrollNo();
					System.out.println("setNo:>>>" + setNo);
					System.out.println("scrollNo::>>" + scrollNo);
					request.setTransType("ATM");//
					String narration = "ATM Cash Withdrawl/ACQ/";
					D001004 d001004 = DataUtils.getSystemParameter(0, MSGConstants.ACQ_PROD_CODE);
					if (d001004 == null) {
						t = null;
						session.close();
						session = null;
						response.setRrn(request.getRrn());
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMsg(MSGConstants.SYSTEM_PARAMETER_NOT_FOUND);
						return response;
					}
					voucher.debit(Integer.valueOf(request.getBrCode().trim()),
							String.format("%-8s", d001004.getValue().trim()) + "000000000000000000000000",
							MSGConstants.ATM, setNo, scrollNo, narration, request.getAmount(), request.getRrn(),
							session);
					if (!voucher.isAborted) {
						if (VoucherMPOS
								.updateProductBalances(request.getAmount(), Integer.valueOf(request.getBrCode().trim()),
										request.getTransType(), d001004.getValue().trim(), session)
								.equalsIgnoreCase(MSGConstants.SUCCESS)) {

							String balResponse = CoreTransactionMPOS.balance(
									Integer.valueOf(request.getBrCode().trim()), request.getAtmAccId(),
									request.getAmount(), MSGConstants.DR, session);

							if (balResponse != null && balResponse.trim().length() > 0
									&& !balResponse.trim().equalsIgnoreCase("99")
									&& !balResponse.trim().equalsIgnoreCase("51")) {

								D350066 d350066 = DataUtils.prepareD350066Obj(request, "D", setNo, scrollNo);
								session.save(d350066);
								t.commit();
								session.close();
								session = null;
								response.setResponse(MSGConstants.SUCCESS);
								response.setRrn(request.getRrn());
								response.setErrorMsg(MSGConstants.SUCCESFUL_TRN);
								response.setRespCode(ResponseCodes.SUCCESS);
								return response;
							} else {
								t.rollback();
								session.close();
								session = null;
								response.setResponse(MSGConstants.ERROR);
								response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
								response.setRespCode(ResponseCodes.SYSTEM_ERROR);
								return response;
							}
						} else {
							t.rollback();
							session.close();
							session = null;
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
							response.setRespCode(ResponseCodes.SYSTEM_ERROR);
							return response;
						}
					} else {
						t.rollback();
						session.close();
						session = null;
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
						response.setRespCode(ResponseCodes.SYSTEM_ERROR);
						return response;
					}
				} else {
					// Other Branch transaction
					ATMVoucher voucher = new ATMVoucher();
					int setNo = ATMVoucher.getNextSetNo();
					int scrollNo = ATMVoucher.getNextScrollNo();
					int reconNo = ATMVoucher.getNextReconNo(Integer.valueOf(request.getBrCode()));
					int benSetNo = VoucherMPOS.getNextSetNo();
					int benScrollNo = VoucherMPOS.getNextScrollNo();
					int scrollNoNew = VoucherMPOS.getNextScrollNo();
					System.out.println("setNo:>>>" + setNo);
					System.out.println("scrollNo::>>" + scrollNo);
					String narration = "ATM Cash Withdrawl";
					D001004 d0010004 = DataUtils.getSystemParameter(0, MSGConstants.ACQ_PROD_CODE);
					if (d0010004 == null) {
						t = null;
						session.close();
						session = null;
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMsg(MSGConstants.SYSTEM_PARAMETER_NOT_FOUND);
						return response;
					}
					D001004 sysBrCode = DataUtils.getSystemParameter(0, MSGConstants.ACQ_BR_CODE);
					if (sysBrCode == null) {
						t = null;
						session.close();
						session = null;
						response.setRrn(request.getRrn());
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMsg(MSGConstants.SYSTEM_PARAMETER_NOT_FOUND);
						return response;
					}
					voucher.debit(Integer.valueOf(sysBrCode.getValue().trim()),
							String.format("%-8s", d0010004.getValue().trim()) + "000000000000000000000000",
							MSGConstants.ATM, benSetNo, scrollNoNew, narration, request.getAmount(), request.getRrn(),
							session);
					if (!voucher.isAborted) {
						if (VoucherMPOS
								.updateProductBalances(request.getAmount(),
										Integer.valueOf(sysBrCode.getValue().trim()), MSGConstants.DR,
										request.getAtmAccId().substring(0, 8).trim(), session)
								.equalsIgnoreCase(MSGConstants.SUCCESS)) {
							voucher.creditABB(Integer.valueOf(sysBrCode.getValue().trim()), MSGConstants.ABB_ACC,
									MSGConstants.ABB, benSetNo, benScrollNo, narration,
									Integer.valueOf(request.getToBrcode().trim()), request.getAmount(),
									request.getRrn(), reconNo, session);

							if (!voucher.isAborted) {
								if (VoucherMPOS
										.updateProductBalances(request.getAmount(),
												Integer.valueOf(request.getBrCode().trim()), MSGConstants.CR,
												MSGConstants.ABB_ACC.substring(0, 8).trim(), session)
										.equalsIgnoreCase(MSGConstants.SUCCESS)) {

									voucher.debitABB(Integer.valueOf(request.getBrCode()), request.getAtmAccId(),
											MSGConstants.ABB, setNo, scrollNo, narration,
											Integer.valueOf(request.getToBrcode()), request.getAmount(),
											request.getRrn(), reconNo, session);
									if (!voucher.isAborted) {

										if (VoucherMPOS
												.updateProductBalances(request.getAmount(),
														Integer.valueOf(request.getBrCode().trim()), MSGConstants.DR,
														MSGConstants.ABB_ACC.substring(0, 8).trim(), session)
												.equalsIgnoreCase(MSGConstants.SUCCESS)) {

											String balResponse = CoreTransactionMPOS.balance(
													Integer.valueOf(request.getBrCode().trim()),
													request.getAtmAccId().trim(), request.getAmount(), "D", session);
											if (balResponse != null && balResponse.trim().length() > 0
													&& !balResponse.trim().equalsIgnoreCase("99")
													&& !balResponse.trim().equalsIgnoreCase("51")) {
												String batchCodes[] = Props.getBatchProperty(MSGConstants.ATM)
														.split("~");
												D001004 d04OnlineBatchName = VoucherMPOS
														.getBatchNameFromBatchCode(batchCodes[0]);
												String onlineBatchName = d04OnlineBatchName.getValue().trim();
												String batchCodes1[] = Props.getBatchProperty(MSGConstants.ATM)
														.split("~");
												D001004 d001004 = VoucherMPOS.getBatchNameFromBatchCode(batchCodes1[0]);
												String benBatchCode = d001004.getValue().trim();

												D100001 d100001 = VoucherMPOS.prepareReconObj(
														Integer.valueOf(request.getToBrcode()), reconNo,
														DataUtils.getOpenDate(Integer.valueOf(request.getToBrcode())),
														999999, onlineBatchName, benBatchCode, setNo, scrollNo,
														benSetNo, benScrollNo, benScrollNo,
														Integer.valueOf(request.getBrCode()),
														"" + request.getAtmAccId(), request.getAmount(), "D");
												System.out.println("d100001::>>>" + d100001);

												D100002 d100002 = VoucherMPOS.prepareRecon2Obj(
														Integer.valueOf(request.getToBrcode()), reconNo,
														DataUtils.getOpenDate(Integer.valueOf(request.getBrCode())),
														999999, onlineBatchName, benBatchCode, setNo, scrollNo,
														benSetNo, scrollNoNew, scrollNoNew,
														Integer.valueOf(request.getBrCode()),
														"" + request.getAtmAccId(), request.getAmount(), "D");
												System.out.println("d100001::>>>" + d100002);
												try {
													D350066 d350066 = DataUtils.prepareD350066Obj(request, "D", setNo,
															scrollNo);
													session.save(d350066);
													session.save(d100001);
													session.save(d100002);
													t.commit();
													session.close();
													session = null;
													t = null;
													response.setResponse(MSGConstants.SUCCESS);
													response.setErrorMsg(MSGConstants.SUCCESFUL_TRN);
													response.setRespCode(ResponseCodes.SUCCESS);
													response.setRrn(request.getRrn());
													return response;
												} catch (Exception e) {
													t.rollback();
													session.close();
													session = null;
													t = null;
													e.printStackTrace();
													response.setResponse(MSGConstants.ERROR);
													response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
													response.setRespCode(ResponseCodes.SYSTEM_ERROR);
													response.setRrn(request.getRrn());
													return response;
												}
											} else {
												t.rollback();
												session.close();
												session = null;
												response.setResponse(MSGConstants.ERROR);
												response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
												response.setRespCode(ResponseCodes.SYSTEM_ERROR);
												return response;
											}
										} else {
											t.rollback();
											session.close();
											session = null;
											t = null;
											response.setResponse(MSGConstants.ERROR);
											response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
											response.setRespCode(ResponseCodes.SYSTEM_ERROR);
											response.setRrn(request.getRrn());
											return response;
										}
									} else {
										t.rollback();
										session.close();
										session = null;
										t = null;
										response.setResponse(MSGConstants.ERROR);
										response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
										response.setRespCode(ResponseCodes.SYSTEM_ERROR);
										response.setRrn(request.getRrn());
										return response;
									}
								} else {
									t.rollback();
									session.close();
									session = null;
									response.setResponse(MSGConstants.ERROR);
									response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
									response.setRespCode(ResponseCodes.SYSTEM_ERROR);
									return response;
								}
							}

						} else {
							t.rollback();
							session.close();
							session = null;
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
							response.setRespCode(ResponseCodes.SYSTEM_ERROR);
							return response;
						}
					} else {
						t.rollback();
						session.close();
						session = null;
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
						response.setRespCode(ResponseCodes.SYSTEM_ERROR);
						return response;
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		} finally {

		}
		return response;
	}

	public static ATMTransactionResponse reverseATMTransaction(ATMTransactionRequest request) {
		// TODO Auto-generated method stub
		ATMTransactionResponse response = new ATMTransactionResponse();
		try {
			System.out.println("request::>>>" + request);
			if (request.getTransType().equalsIgnoreCase(MSGConstants.LOCAL)) {
				Session session = HBUtil.getSessionFactory().openSession();
				Transaction t = session.beginTransaction();
				if (Integer.valueOf(request.getBrCode()) == Integer.valueOf(request.getToBrcode())) {
					// Same Branch transaction
					ATMVoucher voucher = new ATMVoucher();
					int setNo = ATMVoucher.getNextSetNo();
					int scrollNo = ATMVoucher.getNextScrollNo();
					System.out.println("setNo:>>>" + setNo);
					System.out.println("scrollNo::>>" + scrollNo);
					// request.setTransType("ATM");
					String narration = "ATM Cash Withdrawl";
					voucher.credit(Integer.valueOf(request.getBrCode().trim()), request.getAtmAccId(), MSGConstants.ATM,
							setNo, scrollNo, narration, request.getAmount(), request.getRrn(), session);
					if (!voucher.isAborted) {
						if (VoucherMPOS
								.updateProductBalances(request.getAmount(), Integer.valueOf(request.getBrCode().trim()),
										MSGConstants.CR, request.getAtmAccId().substring(0, 8).trim(), session)
								.equalsIgnoreCase(MSGConstants.SUCCESS)) {
							String balResponse = CoreTransactionMPOS.balance(
									Integer.valueOf(request.getBrCode().trim()), request.getAtmAccId().trim(),
									request.getAmount(), "C", session);
							if (balResponse != null && balResponse.trim().length() > 0
									&& !balResponse.trim().equalsIgnoreCase("99")
									&& !balResponse.trim().equalsIgnoreCase("51")) {
								D350066 d350066 = DataUtils.prepareD350066Obj(request, "D", setNo, scrollNo);
								session.save(d350066);
								t.commit();
								session.close();
								session = null;
								logger.error(MSGConstants.SUCCESFUL_TRN);
								response.setResponse(MSGConstants.SUCCESS);
								response.setErrorMsg(MSGConstants.SUCCESFUL_TRN);
								response.setRespCode(ResponseCodes.SUCCESS);
								response.setRrn(request.getRrn());
								return response;
							} else {
								t.rollback();
								session.close();
								session = null;
								logger.error(MSGConstants.TRANSACTION_DECLINED);
								response.setRrn(request.getRrn());
								response.setResponse(MSGConstants.ERROR);
								response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
								response.setRespCode(ResponseCodes.SYSTEM_ERROR);
								return response;
							}
						} else {
							t.rollback();
							session.close();
							session = null;
							response.setRrn(request.getRrn());
							logger.error(MSGConstants.TRANSACTION_DECLINED);
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
							response.setRespCode(ResponseCodes.SYSTEM_ERROR);
							return response;
						}
					} else {
						t.rollback();
						session.close();
						session = null;
						response.setRrn(request.getRrn());
						logger.error(MSGConstants.TRANSACTION_DECLINED);
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
						response.setRespCode(ResponseCodes.SYSTEM_ERROR);
						return response;
					}
				} else {
					// Other Branch transaction
					ATMVoucher voucher = new ATMVoucher();
					int setNo = ATMVoucher.getNextSetNo();
					int scrollNo = ATMVoucher.getNextScrollNo();
					int reconNo = ATMVoucher.getNextReconNo(Integer.valueOf(request.getBrCode()));
					int benSetNo = VoucherMPOS.getNextSetNo();
					int benScrollNo = VoucherMPOS.getNextScrollNo();
					int scrollNoNew = VoucherMPOS.getNextScrollNo();
					System.out.println("setNo:>>>" + setNo);
					System.out.println("scrollNo::>>" + scrollNo);
					String narration = "ATM Cash Withdrawl";
					voucher.credit(Integer.valueOf(request.getBrCode().trim()), request.getAtmAccId(), MSGConstants.ATM,
							benSetNo, scrollNoNew, narration, request.getAmount(), request.getRrn(), session);
					if (!voucher.isAborted) {
						if (VoucherMPOS
								.updateProductBalances(request.getAmount(), Integer.valueOf(request.getBrCode().trim()),
										MSGConstants.CR, request.getAtmAccId().substring(0, 8).trim(), session)
								.equalsIgnoreCase(MSGConstants.SUCCESS)) {
							voucher.debitABB(Integer.valueOf(request.getBrCode()), MSGConstants.ABB_ACC,
									MSGConstants.ABB, benSetNo, benScrollNo, narration,
									Integer.valueOf(request.getToBrcode().trim()), request.getAmount(),
									request.getRrn(), reconNo, session);
							if (!voucher.isAborted) {
								if (VoucherMPOS
										.updateProductBalances(request.getAmount(),
												Integer.valueOf(request.getBrCode().trim()), MSGConstants.DR,
												MSGConstants.ABB_ACC.substring(0, 8).trim(), session)
										.equalsIgnoreCase(MSGConstants.SUCCESS)) {

									voucher.creditABB(Integer.valueOf(request.getToBrcode()), MSGConstants.ABB_ACC,
											MSGConstants.ABB, setNo, scrollNo, narration,
											Integer.valueOf(request.getToBrcode()), request.getAmount(),
											request.getRrn(), reconNo, session);
									if (!voucher.isAborted) {
										if (VoucherMPOS
												.updateProductBalances(request.getAmount(),
														Integer.valueOf(request.getToBrcode().trim()), MSGConstants.CR,
														MSGConstants.ABB_ACC.substring(0, 8).trim(), session)
												.equalsIgnoreCase(MSGConstants.SUCCESS)) {
											String balResponse = CoreTransactionMPOS.balance(
													Integer.valueOf(request.getBrCode()), request.getAtmAccId(),
													request.getAmount(), MSGConstants.CR, session);
											if (balResponse != null && balResponse.trim().length() > 0
													&& !balResponse.trim().equalsIgnoreCase("99")
													&& !balResponse.trim().equalsIgnoreCase("51")) {
												String batchCodes[] = Props.getBatchProperty(MSGConstants.ATM)
														.split("~");
												D001004 d04OnlineBatchName = VoucherMPOS
														.getBatchNameFromBatchCode(batchCodes[0]);
												String onlineBatchName = d04OnlineBatchName.getValue().trim();
												String batchCodes1[] = Props.getBatchProperty(MSGConstants.ATM)
														.split("~");
												D001004 d001004 = VoucherMPOS.getBatchNameFromBatchCode(batchCodes1[0]);
												String benBatchCode = d001004.getValue().trim();

												D100001 d100001 = VoucherMPOS.prepareReconObj(
														Integer.valueOf(request.getToBrcode()), reconNo,
														DataUtils.getOpenDate(Integer.valueOf(request.getToBrcode())),
														999999, onlineBatchName, benBatchCode, setNo, scrollNo,
														benSetNo, benScrollNo, benScrollNo,
														Integer.valueOf(request.getBrCode()),
														"" + request.getAtmAccId(), request.getAmount(), "D");

												D100002 d100002 = VoucherMPOS.prepareRecon2Obj(
														Integer.valueOf(request.getToBrcode()), reconNo,
														DataUtils.getOpenDate(Integer.valueOf(request.getBrCode())),
														999999, onlineBatchName, benBatchCode, setNo, scrollNo,
														benSetNo, scrollNoNew, scrollNoNew,
														Integer.valueOf(request.getBrCode()),
														"" + request.getAtmAccId(), request.getAmount(), "D");
												try {
													D350066 d350066 = DataUtils.prepareD350066Obj(request, "D", setNo,
															scrollNo);
													session.save(d350066);
													session.save(d100001);
													session.save(d100002);
													session.flush();
													t.commit();
													session.close();
													session = null;
													t = null;
													response.setResponse(MSGConstants.SUCCESS);
													response.setErrorMsg(MSGConstants.SUCCESS_MSG);
													response.setRrn(request.getRrn());
													return response;
												} catch (Exception e) {
													session.close();
													session = null;
													t = null;
													e.printStackTrace();
													logger.error(MSGConstants.WEB_SERVICE_ERROR);
													response.setRrn(request.getRrn());
													response.setResponse(MSGConstants.ERROR);
													response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
													response.setRespCode(ResponseCodes.SYSTEM_ERROR);
													return response;
												}
											} else {
												session.close();
												session = null;
												t = null;
												logger.error(MSGConstants.WEB_SERVICE_ERROR);
												response.setRrn(request.getRrn());
												response.setResponse(MSGConstants.ERROR);
												response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
												response.setRespCode(ResponseCodes.SYSTEM_ERROR);
												return response;
											}
										} else {
											session.close();
											session = null;
											t = null;
											logger.error(MSGConstants.WEB_SERVICE_ERROR);
											response.setRrn(request.getRrn());
											response.setResponse(MSGConstants.ERROR);
											response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
											response.setRespCode(ResponseCodes.SYSTEM_ERROR);
											return response;
										}
									} else {
										logger.error(MSGConstants.TRANSACTION_DECLINED);
										t.rollback();
										session.close();
										session = null;
										response.setRrn(request.getRrn());
										response.setResponse(MSGConstants.ERROR);
										response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
										response.setRespCode(ResponseCodes.SYSTEM_ERROR);
										return response;
									}
								} else {
									logger.error(MSGConstants.TRANSACTION_DECLINED);
									t.rollback();
									session.close();
									session = null;
									response.setRrn(request.getRrn());
									response.setResponse(MSGConstants.ERROR);
									response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
									response.setRespCode(ResponseCodes.SYSTEM_ERROR);
									return response;
								}
							} else {
								logger.error(MSGConstants.TRANSACTION_DECLINED);
								t.rollback();
								session.close();
								session = null;
								response.setRrn(request.getRrn());
								response.setResponse(MSGConstants.ERROR);
								response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
								response.setRespCode(ResponseCodes.SYSTEM_ERROR);
								return response;
							}

						} else {
							t.rollback();
							session.close();
							session = null;
							response.setRrn(request.getRrn());
							logger.error(MSGConstants.TRANSACTION_DECLINED);
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
							response.setRespCode(ResponseCodes.SYSTEM_ERROR);
							return response;
						}
					} else {
						t.rollback();
						session.close();
						session = null;
						response.setRrn(request.getRrn());
						logger.error(MSGConstants.TRANSACTION_DECLINED);
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
						response.setRespCode(ResponseCodes.SYSTEM_ERROR);
						return response;
					}
				}
			} else if (request.getTransType().equalsIgnoreCase(MSGConstants.ISSUER)) {
				Session session = HBUtil.getSessionFactory().openSession();
				Transaction t = session.beginTransaction();
				if (Integer.valueOf(request.getBrCode()) == Integer.valueOf(request.getToBrcode())) {
					// Same Branch transaction
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.TRANSACTION_NOT_ALLOWED);
					return response;
				} else {
					// Other branch transaction
					ATMVoucher voucher = new ATMVoucher();
					int setNo = ATMVoucher.getNextSetNo();
					int scrollNo = ATMVoucher.getNextScrollNo();
					int reconNo = ATMVoucher.getNextReconNo(Integer.valueOf(request.getBrCode()));
					int benSetNo = VoucherMPOS.getNextSetNo();
					int benScrollNo = VoucherMPOS.getNextScrollNo();
					int scrollNoNew = VoucherMPOS.getNextScrollNo();
					System.out.println("setNo:>>>" + setNo);
					System.out.println("scrollNo::>>" + scrollNo);
					String narration = "ATM Cash Withdrawl";
					D001004 d0010004 = DataUtils.getSystemParameter(0, MSGConstants.ISSUER_PROD_CODE);
					if (d0010004 == null) {
						t = null;
						session.close();
						session = null;
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMsg(MSGConstants.SYSTEM_PARAMETER_NOT_FOUND);
						response.setRrn(request.getRrn());
						return response;
					}
					D001004 sysParamBr = DataUtils.getSystemParameter(0, MSGConstants.ISSUER_BR_CODE);
					if (sysParamBr == null) {
						t = null;
						session.close();
						session = null;
						response.setRrn(request.getRrn());
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMsg(MSGConstants.SYSTEM_PARAMETER_NOT_FOUND);
						return response;
					}
					voucher.credit(Integer.valueOf(request.getBrCode().trim()), request.getAtmAccId(), MSGConstants.ATM,
							benSetNo, scrollNoNew, narration, request.getAmount(), request.getRrn(), session);
					if (!voucher.isAborted) {
						if (VoucherMPOS
								.updateProductBalances(request.getAmount(), Integer.valueOf(request.getBrCode().trim()),
										MSGConstants.CR, request.getAtmAccId().substring(0, 8).trim(), session)
								.equalsIgnoreCase(MSGConstants.SUCCESS)) {
							voucher.debitABB(Integer.valueOf(request.getBrCode()), MSGConstants.ABB_ACC,
									MSGConstants.ABB, benSetNo, benScrollNo, narration,
									Integer.valueOf(request.getToBrcode().trim()), request.getAmount(),
									request.getRrn(), reconNo, session);

							if (!voucher.isAborted) {
								if (VoucherMPOS
										.updateProductBalances(request.getAmount(),
												Integer.valueOf(request.getToBrcode().trim()), MSGConstants.DR,
												MSGConstants.ABB_ACC.substring(0, 8).trim(), session)
										.equalsIgnoreCase(MSGConstants.SUCCESS)) {

									voucher.creditABB(Integer.valueOf(sysParamBr.getValue().trim()),
											MSGConstants.ABB_ACC, MSGConstants.ABB, setNo, scrollNo, narration,
											Integer.valueOf(request.getToBrcode()), request.getAmount(),
											request.getRrn(), reconNo, session);
									if (!voucher.isAborted) {
										if (VoucherMPOS
												.updateProductBalances(request.getAmount(),
														Integer.valueOf(sysParamBr.getValue().trim()), MSGConstants.CR,
														MSGConstants.ABB_ACC.substring(0, 8).trim(), session)
												.equalsIgnoreCase(MSGConstants.SUCCESS)) {

											voucher.debit(Integer.valueOf(sysParamBr.getValue().trim()),
													String.format("%-8s", d0010004.getValue().trim())
															+ "000000000000000000000000",
													MSGConstants.ATM, benSetNo, benScrollNo, narration,
													request.getAmount(), request.getRrn(), session);
											if (!voucher.isAborted) {
												if (VoucherMPOS
														.updateProductBalances(request.getAmount(),
																Integer.valueOf(sysParamBr.getValue().trim()),
																MSGConstants.DR, d0010004.getValue().trim(), session)
														.equalsIgnoreCase(MSGConstants.SUCCESS)) {

													String balResponse = CoreTransactionMPOS.balance(
															Integer.valueOf(request.getBrCode().trim()),
															request.getAtmAccId().trim(), request.getAmount(),
															MSGConstants.CR, session);
													if (balResponse != null && balResponse.trim().length() > 0
															&& !balResponse.trim().equalsIgnoreCase("99")
															&& !balResponse.trim().equalsIgnoreCase("51")) {

														D350066 d350066 = DataUtils.prepareD350066Obj(request, "D",
																setNo, scrollNo);
														session.save(d350066);
														t.commit();
														session.close();
														session = null;
														logger.error(MSGConstants.TRANSACTION_DECLINED);
														response.setRrn(request.getRrn());
														response.setResponse(MSGConstants.SUCCESS);
														response.setErrorMsg(MSGConstants.SUCCESFUL_TRN);
														response.setRespCode(ResponseCodes.SUCCESS);
														return response;
													} else {
														t.rollback();
														session.close();
														session = null;
														logger.error(MSGConstants.TRANSACTION_DECLINED);
														response.setRrn(request.getRrn());
														response.setResponse(MSGConstants.ERROR);
														response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
														response.setRespCode(ResponseCodes.SYSTEM_ERROR);
														return response;
													}
												} else {
													t.rollback();
													session.close();
													session = null;
													logger.error(MSGConstants.TRANSACTION_DECLINED);
													response.setRrn(request.getRrn());
													response.setResponse(MSGConstants.ERROR);
													response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
													response.setRespCode(ResponseCodes.SYSTEM_ERROR);
													return response;
												}
											} else {
												t.rollback();
												session.close();
												session = null;
												logger.error(MSGConstants.TRANSACTION_DECLINED);
												response.setRrn(request.getRrn());
												response.setResponse(MSGConstants.ERROR);
												response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
												response.setRespCode(ResponseCodes.SYSTEM_ERROR);
												return response;
											}
										} else {
											t.rollback();
											session.close();
											session = null;
											logger.error(MSGConstants.TRANSACTION_DECLINED);
											response.setRrn(request.getRrn());
											response.setResponse(MSGConstants.ERROR);
											response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
											response.setRespCode(ResponseCodes.SYSTEM_ERROR);
											return response;
										}
									} else {
										t.rollback();
										session.close();
										session = null;
										logger.error(MSGConstants.TRANSACTION_DECLINED);
										response.setRrn(request.getRrn());
										response.setResponse(MSGConstants.ERROR);
										response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
										response.setRespCode(ResponseCodes.SYSTEM_ERROR);
										return response;
									}
								} else {
									t.rollback();
									session.close();
									session = null;
									response.setRrn(request.getRrn());
									logger.error(MSGConstants.TRANSACTION_DECLINED);
									response.setResponse(MSGConstants.ERROR);
									response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
									response.setRespCode(ResponseCodes.SYSTEM_ERROR);
									return response;
								}
							} else {
								t.rollback();
								session.close();
								session = null;
								response.setRrn(request.getRrn());
								logger.error(MSGConstants.TRANSACTION_DECLINED);
								response.setResponse(MSGConstants.ERROR);
								response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
								response.setRespCode(ResponseCodes.SYSTEM_ERROR);
								return response;
							}

						} else {
							t.rollback();
							session.close();
							session = null;
							response.setRrn(request.getRrn());
							logger.error(MSGConstants.TRANSACTION_DECLINED);
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
							response.setRespCode(ResponseCodes.SYSTEM_ERROR);
							return response;
						}
					} else {
						t.rollback();
						session.close();
						session = null;
						logger.error(MSGConstants.TRANSACTION_DECLINED);
						response.setRrn(request.getRrn());
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
						response.setRespCode(ResponseCodes.SYSTEM_ERROR);
						return response;
					}

				}
			} else if (request.getTransType().equalsIgnoreCase(MSGConstants.ACQUIRER)) {
				Session session = HBUtil.getSessionFactory().openSession();
				Transaction t = session.beginTransaction();
				if (Integer.valueOf(request.getBrCode()) == Integer.valueOf(request.getToBrcode())) {
					// Same Branch transaction
					ATMVoucher voucher = new ATMVoucher();
					int setNo = ATMVoucher.getNextSetNo();
					int scrollNo = ATMVoucher.getNextScrollNo();
					System.out.println("setNo:>>>" + setNo);
					System.out.println("scrollNo::>>" + scrollNo);
					request.setTransType("ATM");//
					String narration = "ATM Cash Withdrawl/ACQ/";
					D001004 d001004 = DataUtils.getSystemParameter(0, MSGConstants.ACQ_PROD_CODE);
					if (d001004 == null) {
						t = null;
						session.close();
						session = null;
						response.setRrn(request.getRrn());
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMsg(MSGConstants.SYSTEM_PARAMETER_NOT_FOUND);
						return response;
					}

					voucher.credit(Integer.valueOf(request.getBrCode().trim()),
							String.format("%-8s", d001004.getValue().trim()) + "000000000000000000000000",
							MSGConstants.ATM, setNo, scrollNo, narration, request.getAmount(), request.getRrn(),
							session);
					if (!voucher.isAborted) {
						if (VoucherMPOS
								.updateProductBalances(request.getAmount(), Integer.valueOf(request.getBrCode().trim()),
										request.getTransType(), d001004.getValue().trim(), session)
								.equalsIgnoreCase(MSGConstants.SUCCESS)) {

							String balResponse = CoreTransactionMPOS.balance(
									Integer.valueOf(request.getBrCode().trim()), request.getAtmAccId(),
									request.getAmount(), MSGConstants.CR, session);

							if (balResponse != null && balResponse.trim().length() > 0
									&& !balResponse.trim().equalsIgnoreCase("99")
									&& !balResponse.trim().equalsIgnoreCase("51")) {

								D350066 d350066 = DataUtils.prepareD350066Obj(request, "D", setNo, scrollNo);
								session.save(d350066);
								t.commit();
								session.close();
								session = null;
								response.setResponse(MSGConstants.SUCCESS);
								response.setRrn(request.getRrn());
								response.setErrorMsg(MSGConstants.SUCCESFUL_TRN);
								response.setRespCode(ResponseCodes.SUCCESS);
								return response;
							} else {
								t.rollback();
								session.close();
								session = null;
								response.setResponse(MSGConstants.ERROR);
								response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
								response.setRespCode(ResponseCodes.SYSTEM_ERROR);
								return response;
							}
						} else {
							t.rollback();
							session.close();
							session = null;
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
							response.setRespCode(ResponseCodes.SYSTEM_ERROR);
							return response;
						}
					} else {
						t.rollback();
						session.close();
						session = null;
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
						response.setRespCode(ResponseCodes.SYSTEM_ERROR);
						return response;
					}
				} else {
					// Other Branch transaction
					ATMVoucher voucher = new ATMVoucher();
					int setNo = ATMVoucher.getNextSetNo();
					int scrollNo = ATMVoucher.getNextScrollNo();
					int reconNo = ATMVoucher.getNextReconNo(Integer.valueOf(request.getBrCode()));
					int benSetNo = VoucherMPOS.getNextSetNo();
					int benScrollNo = VoucherMPOS.getNextScrollNo();
					int scrollNoNew = VoucherMPOS.getNextScrollNo();
					System.out.println("setNo:>>>" + setNo);
					System.out.println("scrollNo::>>" + scrollNo);
					String narration = "ATM Cash Withdrawl";
					D001004 d0010004 = DataUtils.getSystemParameter(0, MSGConstants.ACQ_PROD_CODE);
					if (d0010004 == null) {
						t = null;
						session.close();
						session = null;
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMsg(MSGConstants.SYSTEM_PARAMETER_NOT_FOUND);
						return response;
					}
					D001004 sysBrCode = DataUtils.getSystemParameter(0, MSGConstants.ACQ_BR_CODE);
					if (sysBrCode == null) {
						t = null;
						session.close();
						session = null;
						response.setRrn(request.getRrn());
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMsg(MSGConstants.SYSTEM_PARAMETER_NOT_FOUND);
						return response;
					}
					voucher.credit(Integer.valueOf(sysBrCode.getValue().trim()),
							String.format("%-8s", d0010004.getValue().trim()) + "000000000000000000000000",
							MSGConstants.ATM, benSetNo, scrollNoNew, narration, request.getAmount(), request.getRrn(),
							session);
					if (!voucher.isAborted) {
						if (VoucherMPOS
								.updateProductBalances(request.getAmount(),
										Integer.valueOf(sysBrCode.getValue().trim()), MSGConstants.CR,
										request.getAtmAccId().substring(0, 8).trim(), session)
								.equalsIgnoreCase(MSGConstants.SUCCESS)) {
							voucher.debitABB(Integer.valueOf(sysBrCode.getValue().trim()), MSGConstants.ABB_ACC,
									MSGConstants.ABB, benSetNo, benScrollNo, narration,
									Integer.valueOf(request.getToBrcode().trim()), request.getAmount(),
									request.getRrn(), reconNo, session);

							if (!voucher.isAborted) {
								if (VoucherMPOS
										.updateProductBalances(request.getAmount(),
												Integer.valueOf(request.getBrCode().trim()), MSGConstants.DR,
												MSGConstants.ABB_ACC.substring(0, 8).trim(), session)
										.equalsIgnoreCase(MSGConstants.SUCCESS)) {

									voucher.creditABB(Integer.valueOf(request.getBrCode()), request.getAtmAccId(),
											MSGConstants.ABB, setNo, scrollNo, narration,
											Integer.valueOf(request.getToBrcode()), request.getAmount(),
											request.getRrn(), reconNo, session);
									if (!voucher.isAborted) {

										if (VoucherMPOS
												.updateProductBalances(request.getAmount(),
														Integer.valueOf(request.getBrCode().trim()), MSGConstants.CR,
														MSGConstants.ABB_ACC.substring(0, 8).trim(), session)
												.equalsIgnoreCase(MSGConstants.SUCCESS)) {

											String balResponse = CoreTransactionMPOS.balance(
													Integer.valueOf(request.getBrCode().trim()),
													request.getAtmAccId().trim(), request.getAmount(), MSGConstants.CR,
													session);
											if (balResponse != null && balResponse.trim().length() > 0
													&& !balResponse.trim().equalsIgnoreCase("99")
													&& !balResponse.trim().equalsIgnoreCase("51")) {
												String batchCodes[] = Props.getBatchProperty(MSGConstants.ATM)
														.split("~");
												D001004 d04OnlineBatchName = VoucherMPOS
														.getBatchNameFromBatchCode(batchCodes[0]);
												String onlineBatchName = d04OnlineBatchName.getValue().trim();
												String batchCodes1[] = Props.getBatchProperty(MSGConstants.ATM)
														.split("~");
												D001004 d001004 = VoucherMPOS.getBatchNameFromBatchCode(batchCodes1[0]);
												String benBatchCode = d001004.getValue().trim();

												D100001 d100001 = VoucherMPOS.prepareReconObj(
														Integer.valueOf(request.getToBrcode()), reconNo,
														DataUtils.getOpenDate(Integer.valueOf(request.getToBrcode())),
														999999, onlineBatchName, benBatchCode, setNo, scrollNo,
														benSetNo, benScrollNo, benScrollNo,
														Integer.valueOf(request.getBrCode()),
														"" + request.getAtmAccId(), request.getAmount(), "D");
												System.out.println("d100001::>>>" + d100001);

												D100002 d100002 = VoucherMPOS.prepareRecon2Obj(
														Integer.valueOf(request.getToBrcode()), reconNo,
														DataUtils.getOpenDate(Integer.valueOf(request.getBrCode())),
														999999, onlineBatchName, benBatchCode, setNo, scrollNo,
														benSetNo, scrollNoNew, scrollNoNew,
														Integer.valueOf(request.getBrCode()),
														"" + request.getAtmAccId(), request.getAmount(), "D");
												System.out.println("d100001::>>>" + d100002);
												try {
													D350066 d350066 = DataUtils.prepareD350066Obj(request, "D", setNo,
															scrollNo);
													session.save(d350066);
													session.save(d100001);
													session.save(d100002);
													t.commit();
													session.close();
													session = null;
													t = null;
													response.setResponse(MSGConstants.SUCCESS);
													response.setErrorMsg(MSGConstants.SUCCESFUL_TRN);
													response.setRespCode(ResponseCodes.SUCCESS);
													response.setRrn(request.getRrn());
													return response;
												} catch (Exception e) {
													t.rollback();
													session.close();
													session = null;
													t = null;
													e.printStackTrace();
													response.setResponse(MSGConstants.ERROR);
													response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
													response.setRespCode(ResponseCodes.SYSTEM_ERROR);
													response.setRrn(request.getRrn());
													return response;
												}
											} else {
												t.rollback();
												session.close();
												session = null;
												response.setResponse(MSGConstants.ERROR);
												response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
												response.setRespCode(ResponseCodes.SYSTEM_ERROR);
												return response;
											}
										} else {
											t.rollback();
											session.close();
											session = null;
											t = null;
											response.setResponse(MSGConstants.ERROR);
											response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
											response.setRespCode(ResponseCodes.SYSTEM_ERROR);
											response.setRrn(request.getRrn());
											return response;
										}
									} else {
										t.rollback();
										session.close();
										session = null;
										t = null;
										response.setResponse(MSGConstants.ERROR);
										response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
										response.setRespCode(ResponseCodes.SYSTEM_ERROR);
										response.setRrn(request.getRrn());
										return response;
									}
								} else {
									t.rollback();
									session.close();
									session = null;
									response.setResponse(MSGConstants.ERROR);
									response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
									response.setRespCode(ResponseCodes.SYSTEM_ERROR);
									return response;
								}
							}
						} else {
							t.rollback();
							session.close();
							session = null;
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
							response.setRespCode(ResponseCodes.SYSTEM_ERROR);
							return response;
						}
					} else {
						t.rollback();
						session.close();
						session = null;
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
						response.setRespCode(ResponseCodes.SYSTEM_ERROR);
						return response;
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		} finally {
		}
		return response;
	}

	public static TransactionValidationResponse atmCharges(int branchCode, String accountNo, String amount,
			String narretion, String rrn,String chargeType,String cardNo) {
		logger.error("atmCharges.service::>>> parameters are :- branchCode:" + branchCode + " accountNo:" + accountNo
				+ " amount:" + amount + " RRN:" + rrn);
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		TransactionValidationResponse response = new TransactionValidationResponse();
		String PL_ACC=ATMTransactionServiceIMPL.loadHashMap().get(chargeType.trim().toUpperCase());
		try {
			D009022 sourceAccount = DataUtils.getAccountMaster(branchCode, accountNo);
			if (sourceAccount == null) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_NOT_FOUND);
				response.setRespCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				return response;
			}
			TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount, amount,
					MSGConstants.DR);
			if (res == null) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.ACCOUNT_VALIDATION_FAIL);
				response.setRespCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				return response;
			}
			if (res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(res.getErrorMsg());
				response.setRespCode(res.getRespCode());
				return response;
			}
			ATMVoucher voucher = new ATMVoucher();
			int setNo = ATMVoucher.getNextSetNo();
			int scrollNo = ATMVoucher.getNextScrollNo();
			System.out.println("setNo:>>>" + setNo);
			System.out.println("scrollNo::>>" + scrollNo);
			
			if(PL_ACC==null || PL_ACC.trim().length()<1)
			{
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.PL_ACCOUNT_NOT_MAPPED);
				return response;
			}
			voucher.debit(branchCode, accountNo, MSGConstants.ATM, setNo, scrollNo, narretion, Double.valueOf(amount),
					rrn, session);
			if (!voucher.isAborted) {
				if (VoucherMPOS.updateProductBalances(Double.valueOf(amount.trim()), branchCode, MSGConstants.DR,
						accountNo.substring(0, 8).trim(), session).equalsIgnoreCase(MSGConstants.SUCCESS)) {

					String balResponse = CoreTransactionMPOS.balance(branchCode, accountNo, Double.valueOf(amount),
							MSGConstants.DR, session);

					if (balResponse != null && balResponse.trim().length() > 0
							&& !balResponse.trim().equalsIgnoreCase("99")
							&& !balResponse.trim().equalsIgnoreCase("51")) {

						voucher.credit(branchCode, PL_ACC,
								MSGConstants.ATM, setNo, ATMVoucher.getNextScrollNo(), narretion,
								Double.valueOf(amount), rrn, session);
						if (!voucher.isAborted) {
							if (VoucherMPOS
									.updateProductBalances(Double.valueOf(amount.trim()), branchCode, MSGConstants.CR,
											PL_ACC.substring(0, 8).trim(),
											session)
									.equalsIgnoreCase(MSGConstants.SUCCESS)) {

								String balResponse1 = CoreTransactionMPOS.balance(branchCode,
											PL_ACC,	Double.valueOf(amount), MSGConstants.CR, session);

								if (balResponse1 != null && balResponse1.trim().length() > 0
										&& !balResponse1.trim().equalsIgnoreCase("99")
										&& !balResponse1.trim().equalsIgnoreCase("51")) {
									Atmcharges atmcharges=prepareATMCharges(accountNo, Double.valueOf(amount.trim()), branchCode, chargeType, PL_ACC, ResponseCodes.SUCCESS, cardNo, rrn,MSGConstants.SUCCESFUL_TRN);
									session.save(atmcharges);
									t.commit();
									response.setSetNo(setNo + "");
									response.setScrollNo(scrollNo + "");
									response.setResponse(MSGConstants.SUCCESS);
									response.setRrn(rrn);
									response.setErrorMsg(MSGConstants.SUCCESFUL_TRN);
									response.setRespCode(ResponseCodes.SUCCESS);
									return response;
								} else {
									logger.error("ERROR");
									Atmcharges atmcharges=prepareATMCharges(accountNo, Double.valueOf(amount.trim()), branchCode, chargeType, PL_ACC, ResponseCodes.SYSTEM_ERROR, cardNo, rrn,MSGConstants.PL_ACC_BAL_UPADTE_FAIl);
									session.save(atmcharges);
									t.commit();
									response.setResponse(MSGConstants.ERROR);
									response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
									response.setRespCode(ResponseCodes.SYSTEM_ERROR);
									return response;
								}
							} else {
								logger.error("ERROR");
								Atmcharges atmcharges=prepareATMCharges(accountNo, Double.valueOf(amount.trim()), branchCode, chargeType, PL_ACC, ResponseCodes.SYSTEM_ERROR, cardNo, rrn,MSGConstants.PLACC_PROD_BAL_UPDATE_FAIL);
								session.save(atmcharges);
								t.commit();
								response.setResponse(MSGConstants.ERROR);
								response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
								response.setRespCode(ResponseCodes.SYSTEM_ERROR);
								return response;
							}
						} else {
							logger.error("ERROR");
							Atmcharges atmcharges=prepareATMCharges(accountNo, Double.valueOf(amount.trim()), branchCode, chargeType, PL_ACC, ResponseCodes.SYSTEM_ERROR, cardNo, rrn,MSGConstants.PL_ACC_VOUCHER_FAIL);
							session.save(atmcharges);
							t.commit();
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
							response.setRespCode(ResponseCodes.SYSTEM_ERROR);
							return response;
						}
					} else {
						logger.error("ERROR");
						Atmcharges atmcharges=prepareATMCharges(accountNo, Double.valueOf(amount.trim()), branchCode, chargeType, PL_ACC, ResponseCodes.SYSTEM_ERROR, cardNo, rrn,MSGConstants.CUST_ACC_BAL_UPADTE_FAIL);
						session.save(atmcharges);
						t.commit();
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
						response.setRespCode(ResponseCodes.SYSTEM_ERROR);
						return response;
					}
				} else {
					logger.error("ERROR");
					Atmcharges atmcharges=prepareATMCharges(accountNo, Double.valueOf(amount.trim()), branchCode, chargeType, PL_ACC, ResponseCodes.SYSTEM_ERROR, cardNo, rrn,MSGConstants.CUST_ACC_PROD_BAL_UPDATE_FAIL);
					session.save(atmcharges);
					t.commit();
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
					response.setRespCode(ResponseCodes.SYSTEM_ERROR);
					return response;
				}
			} else {
				logger.error("ERROR");
				Atmcharges atmcharges=prepareATMCharges(accountNo, Double.valueOf(amount.trim()), branchCode, chargeType, PL_ACC, ResponseCodes.SYSTEM_ERROR, cardNo, rrn,MSGConstants.CUST_ACC_VOUCHER_FAIL);
				session.save(atmcharges);
				t.commit();
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
				response.setRespCode(ResponseCodes.SYSTEM_ERROR);
				return response;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Atmcharges atmcharges=prepareATMCharges(accountNo, Double.valueOf(amount.trim()), branchCode, chargeType, PL_ACC, ResponseCodes.SYSTEM_ERROR, cardNo, rrn,MSGConstants.WEB_SERVICE_ERROR);
			session.save(atmcharges);
			t.commit();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			response.setRespCode(ResponseCodes.SYSTEM_ERROR);
			return response;
		} finally {
			session.close();
			session = null;
			t = null;
		}
	}

	public static Map<String, String> loadHashMap() {
		Map<String, String> map = new HashMap<>();
		try {
			String value = ConfigurationLoader.getParameters(false).getProperty("ATM_CHARGES_TYPE");
			value = value.substring(1, value.length() - 1);
			String[] keyValuePairs = value.split(",");
			for (String pair : keyValuePairs) {
				String[] entry = pair.split("=");
				map.put(entry[0].trim(), entry[1].trim());
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return map;
	}
	public static Atmcharges prepareATMCharges(String accno,double amount,int branchcode,String cargetype,String placcno,String status,String cardno,String rrn,String errormsg)
	{
		try {
			Atmcharges atmcharges=new Atmcharges();
			AtmchargesId id=new AtmchargesId();
			id.setCardno(cardno);
			id.setRrn(rrn);
			id.setEntrydate(new Date());
			atmcharges.setId(id);
			atmcharges.setAccno(accno);
			atmcharges.setAmount(amount);
			atmcharges.setBranchcode(branchcode);
			atmcharges.setCargetype(cargetype);
			atmcharges.setPlaccno(placcno);
			atmcharges.setStatus(status);
			atmcharges.setErrormsg(errormsg);
			atmcharges.setEntrytime(new Date());
			return atmcharges;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}
	public static boolean validateATMChargesTransaction(int branchCode, String accountNo, String amount,
			String narretion, String rrn,String chargeType,String cardNo)
	{
		Session session=HBUtil.getSessionFactory().openSession();
		try {
			Criteria criteria=session.createCriteria(Atmcharges.class);
			criteria.add(Restrictions.eq("id.rrn", rrn.trim()));
			criteria.add(Restrictions.eq("id.cardno", cardNo.trim()));
			criteria.add(Restrictions.eq("cargetype", chargeType.trim()));
			criteria.add(Restrictions.eq("amount", Double.valueOf(amount.trim())));
			criteria.add(Restrictions.eq("accno", accountNo.trim()));
			criteria.add(Restrictions.eq("placcno", loadHashMap().get(chargeType.toUpperCase())));
			List<Atmcharges> list=criteria.list();
			if(list==null || list.isEmpty())
				return false;
			else
				return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}finally {
			session.close();
			session=null;
		}
	}
	
	
	public static IMPSChargesResponse impsCharges(int branchCode, String accountNo, String amount,
			String narretion, String rrn) {
		logger.error("atmCharges.service::>>> parameters are :- branchCode:" + branchCode + " accountNo:" + accountNo
				+ " amount:" + amount + " RRN:" + rrn);
		Session session = HBUtil.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		IMPSChargesResponse response = new IMPSChargesResponse();
		D001004 d001004 = DataUtils.getSystemParameter(0, ConfigurationLoader.getParameters(false).getProperty("chargesGL"));
		if (d001004 == null) {
			t = null;
			session.close();
			session = null;
			
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage("GL Parameter Not Found");
			response.setValid(false);
			return response;
		}
		
		String PL_ACC=String.format("%-8s", d001004.getValue().trim()) + "000000000000000000000000";
		try {
			D009022 sourceAccount = DataUtils.getAccountMaster(branchCode, accountNo);
			if (sourceAccount == null) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.ACCOUNT_NOT_FOUND);
				response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				response.setValid(false);
				return response;
			}
			TransactionValidationResponse res = TransactionServiceImpl.validateAccount(sourceAccount, amount,
					MSGConstants.DR);
			if (res == null) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.ACCOUNT_VALIDATION_FAIL);
				response.setErrorCode(ResponseCodes.INVALID_ACCOUNT_IFSC_M1);
				response.setValid(false);
				return response;
			}
			if (res.getResponse().equalsIgnoreCase(MSGConstants.ERROR)) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(res.getErrorMsg());
				response.setErrorCode(res.getRespCode());
				response.setValid(false);
				return response;
			}
			ATMVoucher voucher = new ATMVoucher();
			int setNo = ATMVoucher.getNextSetNo();
			int scrollNo = ATMVoucher.getNextScrollNo();
			System.out.println("setNo:>>>" + setNo);
			System.out.println("scrollNo::>>" + scrollNo);
			
			if(PL_ACC==null || PL_ACC.trim().length()<1)
			{
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.PL_ACCOUNT_NOT_MAPPED);
				response.setValid(false);
				return response;
			}
			voucher.debit(branchCode, accountNo, MSGConstants.IMPS_CHANNEL, setNo, scrollNo, narretion, Double.valueOf(amount),
					rrn, session);
			if (!voucher.isAborted) {
				if (VoucherMPOS.updateProductBalances(Double.valueOf(amount.trim()), branchCode, MSGConstants.DR,
						accountNo.substring(0, 8).trim(), session).equalsIgnoreCase(MSGConstants.SUCCESS)) {

					String balResponse = CoreTransactionMPOS.balance(branchCode, accountNo, Double.valueOf(amount),
							MSGConstants.DR, session);

					if (balResponse != null && balResponse.trim().length() > 0
							&& !balResponse.trim().equalsIgnoreCase("99")
							&& !balResponse.trim().equalsIgnoreCase("51")) {

						voucher.credit(branchCode, PL_ACC,
								MSGConstants.IMPS_CHANNEL, setNo, ATMVoucher.getNextScrollNo(), narretion,
								Double.valueOf(amount), rrn, session);
						if (!voucher.isAborted) {
							if (VoucherMPOS
									.updateProductBalances(Double.valueOf(amount.trim()), branchCode, MSGConstants.CR,
											PL_ACC.substring(0, 8).trim(),
											session)
									.equalsIgnoreCase(MSGConstants.SUCCESS)) {
								
								response.setErrorMessage(MSGConstants.SUCCESFUL_TRN);
								response.setErrorCode(ResponseCodes.SUCCESS);
								response.setValid(true);
								response.setRrnNo(rrn);
								t.commit();
								/*String balResponse1 = CoreTransactionMPOS.balance(branchCode,
											PL_ACC,	Double.valueOf(amount), MSGConstants.CR, session);

								if (balResponse1 != null && balResponse1.trim().length() > 0
										&& !balResponse1.trim().equalsIgnoreCase("99")
										&& !balResponse1.trim().equalsIgnoreCase("51")) {
									Atmcharges atmcharges=prepareATMCharges(accountNo, Double.valueOf(amount.trim()), branchCode, chargeType, PL_ACC, ResponseCodes.SUCCESS, cardNo, rrn,MSGConstants.SUCCESFUL_TRN);
									session.save(atmcharges);
									t.commit();
									response.setSetNo(setNo + "");
									response.setScrollNo(scrollNo + "");
									response.setResponse(MSGConstants.SUCCESS);
									response.setRrn(rrn);
									response.setErrorMsg(MSGConstants.SUCCESFUL_TRN);
									response.setRespCode(ResponseCodes.SUCCESS);
									return response;
								} else {
									logger.error("ERROR");
									Atmcharges atmcharges=prepareATMCharges(accountNo, Double.valueOf(amount.trim()), branchCode, chargeType, PL_ACC, ResponseCodes.SYSTEM_ERROR, cardNo, rrn,MSGConstants.PL_ACC_BAL_UPADTE_FAIl);
									session.save(atmcharges);
									t.commit();
									response.setResponse(MSGConstants.ERROR);
									response.setErrorMsg(MSGConstants.TRANSACTION_DECLINED);
									response.setRespCode(ResponseCodes.SYSTEM_ERROR);
									return response;
								}*/
							} else {
								logger.error("ERROR");
								//Atmcharges atmcharges=prepareATMCharges(accountNo, Double.valueOf(amount.trim()), branchCode, chargeType, PL_ACC, ResponseCodes.SYSTEM_ERROR, cardNo, rrn,MSGConstants.PLACC_PROD_BAL_UPDATE_FAIL);
								//session.save(atmcharges);
								t.commit();
								response.setResponse(MSGConstants.ERROR);
								response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
								response.setErrorCode(ResponseCodes.SYSTEM_ERROR);
								response.setValid(false);
								return response;
							}
						} else {
							logger.error("ERROR");
							//Atmcharges atmcharges=prepareATMCharges(accountNo, Double.valueOf(amount.trim()), branchCode, chargeType, PL_ACC, ResponseCodes.SYSTEM_ERROR, cardNo, rrn,MSGConstants.PL_ACC_VOUCHER_FAIL);
							//session.save(atmcharges);
							t.commit();
							response.setResponse(MSGConstants.ERROR);
							response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
							response.setErrorCode(ResponseCodes.SYSTEM_ERROR);
							response.setValid(false);
							return response;
						}
					} else {
						logger.error("ERROR");
						//Atmcharges atmcharges=prepareATMCharges(accountNo, Double.valueOf(amount.trim()), branchCode, chargeType, PL_ACC, ResponseCodes.SYSTEM_ERROR, cardNo, rrn,MSGConstants.CUST_ACC_BAL_UPADTE_FAIL);
						//session.save(atmcharges);
						t.commit();
						response.setResponse(MSGConstants.ERROR);
						response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
						response.setErrorCode(ResponseCodes.SYSTEM_ERROR);
						response.setValid(false);
						return response;
					}
				} else {
					logger.error("ERROR");
					//Atmcharges atmcharges=prepareATMCharges(accountNo, Double.valueOf(amount.trim()), branchCode, chargeType, PL_ACC, ResponseCodes.SYSTEM_ERROR, cardNo, rrn,MSGConstants.CUST_ACC_PROD_BAL_UPDATE_FAIL);
					//session.save(atmcharges);
					t.commit();
					response.setResponse(MSGConstants.ERROR);
					response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
					response.setErrorCode(ResponseCodes.SYSTEM_ERROR);
					response.setValid(false);
					return response;
				}
			} else {
				logger.error("ERROR");
				//Atmcharges atmcharges=prepareATMCharges(accountNo, Double.valueOf(amount.trim()), branchCode, chargeType, PL_ACC, ResponseCodes.SYSTEM_ERROR, cardNo, rrn,MSGConstants.CUST_ACC_VOUCHER_FAIL);
				//session.save(atmcharges);
				t.commit();
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMessage(MSGConstants.TRANSACTION_DECLINED);
				response.setErrorCode(ResponseCodes.SYSTEM_ERROR);
				response.setValid(false);
				return response;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			//Atmcharges atmcharges=prepareATMCharges(accountNo, Double.valueOf(amount.trim()), branchCode, chargeType, PL_ACC, ResponseCodes.SYSTEM_ERROR, cardNo, rrn,MSGConstants.WEB_SERVICE_ERROR);
			//session.save(atmcharges);
			t.commit();
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMessage(MSGConstants.WEB_SERVICE_ERROR);
			response.setErrorCode(ResponseCodes.SYSTEM_ERROR);
			response.setValid(false);
			return response;
		} finally {
			session.close();
			session = null;
			t = null;
		}
		return response;
	}
}
