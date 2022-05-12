package com.sil.ws;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.sil.commonswitch.ATMTransactionServiceIMPL;
import com.sil.constants.MSGConstants;
import com.sil.domain.ATMTransactionRequest;
import com.sil.domain.ATMTransactionResponse;

@Path("/atm")
public class ATMTransactionService {
	public static Logger logger=Logger.getLogger(ATMTransactionService.class);
	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Path("/doATMTransaction")
	public static ATMTransactionResponse doATMTransaction(ATMTransactionRequest request) {
		ATMTransactionResponse response = new ATMTransactionResponse();
		try {
			if (request == null) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_REQUEST);
				return response;
			}
			if (request.getCardNo() == null || request.getCardNo().trim().length() != 16) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_CARD_NO);
				return response;
			}
			if (request.getAcqId() == null || request.getAcqId().trim().length() != 6) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_ACQ_ID);
				return response;
			}
			if (request.getAtmAccId() == null || request.getAtmAccId().trim().length() != 32) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_ATM_ACCID);
				return response;
			}
			if (request.getAmount() == 0) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_AMOUNT);
				return response;
			}
			if (request.getBrCode() == null || request.getBrCode().trim().equalsIgnoreCase("0")) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_BR_CODE);
				return response;
			}
			if (request.getNetworkId() == null || request.getNetworkId().trim().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_NETWORK_ID);
				return response;
			}
			if (request.getRrn() == null || request.getRrn().trim().length() != 12) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_RRN_NO);
				return response;
			}
			if (request.getToAccId() == null || request.getToAccId().trim().length() != 32) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_TOATM_ID);
				return response;
			}
			if (request.getToBrcode() == null || request.getToBrcode().length() < 1) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_TOATM_BR_CODE);
				return response;
			}
			if (request.getAtmAuthNo() == null || request.getAtmAuthNo().trim().length() != 6) {
				response.setResponse(MSGConstants.ERROR);
				response.setErrorMsg(MSGConstants.INVALID_ATM_AUTH_NO);
				return response;
			}
			return ATMTransactionServiceIMPL.doATMTransaction(request);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("ERROR:", e);
			response.setResponse(MSGConstants.ERROR);
			response.setErrorMsg(MSGConstants.WEB_SERVICE_ERROR);
			return response;
		}
	}
}
