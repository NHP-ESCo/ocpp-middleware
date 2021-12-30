package it.besmart.ocpp.client.external;

import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import eu.chargetime.ocpp.model.firmware.DiagnosticsStatus;
import it.besmart.ocpp.enums.AuthorizationType;
import it.besmart.ocpp.model.Authorization;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.DiagnosticsRecordStatus;
import it.besmart.ocpp.model.Transaction;
import it.besmart.ocpp.services.interfaces.IWlService;
import it.besmart.ocpp.utils.FileTransferUtility;
import it.besmart.ocppLib.dto.IdTag;
import it.besmart.ocppLib.enumeration.EndTxReason;
import it.besmart.ocppLib.enumeration.IdTagType;
import it.besmart.ocppLib.wrappers.RechargeDetailsData;
import it.besmart.ocppLib.wrappers.RechargeStartData;
import it.besmart.ocppLib.wrappers.Request.AuthorizationStartRequest;
import it.besmart.ocppLib.wrappers.Request.AuthorizationStopRequest;
import it.besmart.ocppLib.wrappers.Response.AckResponse;
import it.besmart.ocppLib.wrappers.Response.AuthorizationStartResponse;
import it.besmart.ocppLib.wrappers.Response.AuthorizationStartResponse.AuthorizationStatusEnum;
import it.besmart.ocppLib.wrappers.Response.AuthorizationStopResponse;
import it.besmart.ocppLib.wrappers.Response.DiagnosticsResult;


@Service
@Transactional
public class ClientService implements IClientService {
	
	private final Logger logger = LoggerFactory.getLogger(ClientService.class);
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private FileTransferUtility fileUtility;
	
	@Autowired 
	private IWlService wlService;
	
	private String AUTHORIZE_START_URL;
	private String AUTHORIZE_STOP_URL ;
	private String STOP_TX_URL ;
	private String START_TX_URL ;
	private String DIAGN_URL ;
	
	
	
	public void configureUrl(String authStart, String authStop, String startTx, String stopTx, String diagnResult) {
		this.AUTHORIZE_START_URL = authStart;
		this.AUTHORIZE_STOP_URL = authStop;
		this.START_TX_URL = startTx;
		this.STOP_TX_URL = stopTx;
		this.DIAGN_URL = diagnResult;
		
		logger.debug(String.format("Client created %s", this.toString()));
	}
	
	
	@Override
	public AuthorizationStartResponse authorizeStart(ChargingStation cs, String idTag) {
		
		String postURL = AUTHORIZE_START_URL;
		
		 
//		logger.debug(String.format("Ask authorization to user %s (url %s) for station %s, idTag %s", 
//				user.getName(), postURL, cs.getEvseID(), idTag));
		
		if(postURL.isBlank()) {
			//Ficticious client
			/**Without external client only authorize starting idtag**/
			
			AuthorizationStartResponse result = new AuthorizationStartResponse();
			
			if(wlService.belongToLocalList(cs, idTag)) {
				result.setStatus(AuthorizationStatusEnum.AUTHORIZED);
				result.setSessionId(UUID.randomUUID().toString());
				result.setProviderId("LOCAL");
			}
			
			return result;
		}
		
		
		AuthorizationStartRequest externalRequest = new AuthorizationStartRequest();
		externalRequest.setEvseId(cs.getEvseID());
		IdTag tag = new IdTag(idTag.toUpperCase());
		tag.setTagType(IdTagType.RFID);
		
		externalRequest.setIdTag(tag);
		externalRequest.setOperatorId(cs.getOperatorCode());
		//externalRequest.setPartnerId(partnerId);  			//MY SESSION ID, Not created yet TODO change
		//externalRequest.setSessionId(); 						//NEVER USED
		
		ResponseEntity<AuthorizationStartResponse> result = null;
		
		try{
			result = restTemplate.postForEntity(postURL, externalRequest, AuthorizationStartResponse.class);
		}
		catch(RestClientException e) {
			e.printStackTrace();
			return null;
		}
		
		logger.debug(String.format("%s per la station %s", result.getBody().getStatus(), cs.getEvseID()) );
		
		if(result.getStatusCode() == HttpStatus.OK)
			return result.getBody();
		else {
			logger.warn(result.getStatusCode().toString());
			return null;
		}
		

	}


	@Override
	public void sendRechargeDetails(Transaction tx) {
		String postURL = STOP_TX_URL;
		
		if(postURL.isBlank()) {
			return;
		}
		
		RechargeDetailsData data = new RechargeDetailsData();
	
		data.setEvseId(tx.getConnector().getUnit().getEvseCode());
		data.setConnector(tx.getConnector().getRef());
		
		if(tx.getAuthorization().getType()!= AuthorizationType.REMOTE) {
			data.setIdTag(tx.getAuthorization().getIdTag()); 
		}
		
		if(tx.getStoppingIdTag()!=null)
			data.setStoppingIdTag(tx.getStoppingIdTag());
		
		data.setSessionStart(tx.getStartDate());
		data.setChargingStart(tx.getStartDate());
		data.setSessionEnd(tx.getStopDate().withZoneSameInstant(ZoneId.systemDefault()));
		data.setChargingEnd(tx.getStopChargingDate().withZoneSameInstant(ZoneId.systemDefault())); 
		
		//All in kWh
		data.setMeterValueStart( tx.getMeterStart() );
		data.setMeterValueEnd( tx.getMeterStop() );
		data.setConsumedEnergy( tx.getMeterStop() - tx.getMeterStart() );

		data.setEndSessionReasonCode(tx.getEndReason().ordinal());
		if(tx.getEndFault()!=null) {
			data.setEndSessionReasonFault(tx.getEndFault().getLogID());
			data.setEndSessionReasonInfo(tx.getEndFault().getMessage());
		}
		
		
		data.setSessionId(tx.getAuthorization().getExternalSession());
		
		logger.debug("End of charge details :\n" + data.toString());
		
		sendCDR(data, postURL);
	
	}
	
	@Override
	public void sendRechargeDetails(Authorization auth, EndTxReason reason) {
		String postURL = STOP_TX_URL;
		
		if(postURL.isBlank()) {
			return;
		}
		
		RechargeDetailsData data = new RechargeDetailsData();
	
		data.setEvseId(auth.getCu().getEvseCode());
		
		data.setIdTag(auth.getIdTag());  
		
		data.setMeterValueStart(0.0);
		data.setMeterValueEnd(0.0);
		data.setSessionStart(auth.getTimestamp());
		data.setSessionEnd(ZonedDateTime.now());
//		data.setChargingStart(tx.getStartDate());
//		data.setChargingEnd(tx.getStopChargingDate().withZoneSameInstant(ZoneId.systemDefault())); 
		
		//All in kWh
		data.setConsumedEnergy( 0.0 );
		
		data.setEndSessionReasonCode(reason.ordinal());
		if(auth.getEndFault()!=null) {
			data.setEndSessionReasonFault(auth.getEndFault().getLogID());
			data.setEndSessionReasonInfo(auth.getEndFault().getMessage());
		}
		
		data.setSessionId(auth.getExternalSession());
		
		logger.debug("End of charge details :\n" + data.toString());
		
		sendCDR(data, postURL);
		
	}
	
	
	private void sendCDR(RechargeDetailsData data, String postURL) {
		
		if(postURL!=null) {
			
			logger.debug("Invio CDR a " + postURL);
			ResponseEntity<AckResponse> result = null;
			
			try{
				result = restTemplate.postForEntity(postURL, data, AckResponse.class);		
			}
			catch(RestClientException e) {
				e.printStackTrace();
				return;
			}
			
			if(result.getStatusCode() != HttpStatus.OK)
				logger.warn(result.getStatusCode().toString());
			
			
		}
		
	}

	
	
	@Override
	public AuthorizationStopResponse authorizeStop(Transaction tx, String idTag) {
		
		String postURL = AUTHORIZE_STOP_URL;
		
		
		if(postURL.isBlank()) {
			/**Without external client only authorize starting idtag**/
			
			AuthorizationStopResponse result = new AuthorizationStopResponse();
			if(tx.getAuthorization().getIdTag().equals(idTag))
				result.setStatus(AuthorizationStatusEnum.AUTHORIZED);
			
			return result;
		}
		
//		logger.debug(String.format("Ask authorization to user %s (url %s) for station %s, idTag %s", 
//				user.getName(), postURL, cs.getEvseID(), idTag));
		
		
		AuthorizationStopRequest externalRequest = new AuthorizationStopRequest();
		externalRequest.setEvseId(tx.getConnector().getUnit().getEvseCode());
		IdTag tag = new IdTag(idTag.toUpperCase());
		tag.setTagType(IdTagType.RFID);
		
		externalRequest.setIdTag(tag);
		externalRequest.setSessionId(tx.getAuthorization().getExternalSession()); 						
		
		ResponseEntity<AuthorizationStopResponse> result = null;
		
		try{
			result = restTemplate.postForEntity(postURL, externalRequest, AuthorizationStopResponse.class);
		}
		catch(RestClientException e) {
			e.printStackTrace();
			return new AuthorizationStopResponse();
		}
		
		logger.debug(String.format("Stopping %s per la tx %s", result.getBody().getStatus(),  
				tx.getTxID()) );
		
		if(result.getStatusCode() == HttpStatus.OK)
			return result.getBody();
		else {
			logger.warn(result.getStatusCode().toString());
			return new AuthorizationStopResponse();
		}
		

	}


	@Override
	public void sendRechargeStart(Transaction tx) {
	
		String postURL = START_TX_URL;
		
		if(postURL.isBlank()) {
			return;
		}
		
		
		RechargeStartData data = new RechargeStartData();
		data.setSessionId(tx.getExternalSession());
		data.setEvseId(tx.getConnector().getUnit().getEvseCode());
		data.setConnector(tx.getConnector().getRef());
		data.setFreeMode(tx.getAuthorization().isFreeMode());
		
		ResponseEntity<AckResponse> result = restTemplate.postForEntity(postURL, 
				data, AckResponse.class);		
		
		if(result.getStatusCode() != HttpStatus.OK)
			logger.warn("Invio start recharge. Error: " + result.getStatusCode().toString());
			
		
		logger.debug("Start of charge details: \n" + data);
		
		
	}


	@Override
	public void sendDiagnosticsResult(DiagnosticsRecordStatus record) {
		
		String postURL = DIAGN_URL;
		
		if(postURL.isBlank()) {
			return;
		}
		
		logger.debug(String.format("Send result: %s", record.toString()));
		
		DiagnosticsResult data = new DiagnosticsResult();
		
		data.setResult(record.getStatus().equals(DiagnosticsStatus.Uploaded));
		data.setEvseId(record.getStation().getEvseID());
		data.setEmail(record.getEmail());
		
		if(data.isResult() && record.getFilePath()!=null) {
			
			File file;
			try {
				
				String uri = fileUtility.getServerPath(record.getStation().getModel().isSftp()) + record.getFilePath();
				
				file = fileUtility.downloadFile(record.getFilePath(), record.isSftp());
			
				//TODO file could be sent to email
				
				data.setLink(uri);
				
				
			} catch (IOException e) {
				
				e.printStackTrace();
				
				logger.error(e.getLocalizedMessage());
				
				data.setResult(false);
				data.setMessage("File Transfer Error");
			}
			
		}
		
		ResponseEntity<AckResponse> result = null;		
		
		logger.debug(data.toString());
		
		try {
			result = restTemplate.postForEntity(postURL, 
					data, AckResponse.class);
			
			logger.debug(result.getBody().toString());
			
		}
		catch(RestClientException e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage());
		}
		
		
	}

	@Override
	public String toString() {
		return "ClientService [AUTHORIZE_START_URL=" + AUTHORIZE_START_URL + ", AUTHORIZE_STOP_URL="
				+ AUTHORIZE_STOP_URL + ", STOP_TX_URL=" + STOP_TX_URL + ", START_TX_URL=" + START_TX_URL
				+ ", DIAGN_URL=" + DIAGN_URL + "]";
	}


}
