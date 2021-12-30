package it.besmart.ocpp.controllers;

import java.time.ZonedDateTime;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.besmart.ocpp.dtos.AuthorizationDTO;
import it.besmart.ocpp.dtos.ChargingUnitDTO;
import it.besmart.ocpp.dtos.ReservationDTO;
import it.besmart.ocpp.enums.AuthorizationResponse;
import it.besmart.ocpp.enums.AuthorizationType;
import it.besmart.ocpp.enums.ConnectorStatusComplete;
import it.besmart.ocpp.enums.ErrorMessageResponse;
import it.besmart.ocpp.enums.ReserveStatus;
import it.besmart.ocpp.model.Authorization;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.ChargingUnit;
import it.besmart.ocpp.model.Connector;
import it.besmart.ocpp.model.MeterRecord;
import it.besmart.ocpp.model.Reservation;
import it.besmart.ocpp.model.Transaction;
import it.besmart.ocpp.schedulers.StationScheduler;
import it.besmart.ocpp.servers.ServerMap;
import it.besmart.ocpp.services.interfaces.IAuthorizationService;
import it.besmart.ocpp.services.interfaces.ICUService;
import it.besmart.ocpp.services.interfaces.IConnectorService;
import it.besmart.ocpp.services.interfaces.IMeterRecordService;
import it.besmart.ocpp.services.interfaces.IReservationService;
import it.besmart.ocpp.services.interfaces.ITxService;
import it.besmart.ocpp.wrappers.APIError;
import it.besmart.ocpp.wrappers.AuthWrapper;
import it.besmart.ocpp.wrappers.SCRequest;
import it.besmart.ocppLib.enumeration.EndTxReason;
import it.besmart.ocppLib.enumeration.TransactionStatus;
import it.besmart.ocppLib.wrappers.Request.CancelReservationRequest;
import it.besmart.ocppLib.wrappers.Request.ForcedStopRequest;
import it.besmart.ocppLib.wrappers.Request.RechargeRequest;
import it.besmart.ocppLib.wrappers.Request.RemoteStartRequest;
import it.besmart.ocppLib.wrappers.Request.ReserveNowRequest;
import it.besmart.ocppLib.wrappers.Response.AckResponse;
import it.besmart.ocppLib.wrappers.Response.RechargeDetailsResponse;
import it.besmart.ocppLib.wrappers.Response.ReserveNowResponse;


@RestController
@RequestMapping("api/transaction")
public class TxActionController {
	
	private final Logger logger = LoggerFactory.getLogger(TxActionController.class);
	
	@Autowired
	private ServerMap serverMap; 
	
	@Autowired
	private ICUService cuService;
	
	@Autowired
	private IConnectorService connService;
	
	@Autowired
	private ITxService txService;
	
	@Autowired
	private IAuthorizationService authService;
	
	@Autowired
	private IReservationService reserveService;

	@Autowired
	private IMeterRecordService meterService;
	

	
	@PostMapping("/startRemote") 
	public ResponseEntity<AckResponse> remoteStart(HttpServletRequest request, 
			 @Valid @RequestBody RemoteStartRequest bodyRequest) {
		
		AckResponse response = new AckResponse(false, null);
		
		
		logger.debug( String.format("Remote start sulla unit %s", 
				 bodyRequest.getCuEvse()));
		
		//CU existence
		ChargingUnit cu = cuService.findByEvse(bodyRequest.getCuEvse());
		if(cu==null) {
			return APIError.response(ErrorMessageResponse.UNIT_UNEXISTENT);
		}
		ChargingStation cs = cu.getChargingStation();
				
		//SessionID validation
		String sessionID = bodyRequest.getSessionId();
		
		if (authService.findByExternalSession(sessionID)!=null) {
			return APIError.response(ErrorMessageResponse.TX_DUPLICATED);
		}
		
		//Controllable station: connected + active 
		if( ! cs.isControllable() ) {
			return APIError.response(ErrorMessageResponse.STATION_UNAVAILABLE);
		}
				
		
		//Check active reservation //CHECK UNIT STATUS
		int connRef = 0;
		
		Random rand = new Random();
		String idTag = bodyRequest.getParentIdTag()
				+"-"+rand.nextInt(999); //CREATE idTag from evcoId (max 20 chars TODO)
		
		
		Authorization auth = authService.findActiveReservation(cu);
		if (auth!=null) {
			//Booked Unit
			logger.debug("Unit reserved. Check provider");
			
			if(!auth.getEvcoId().equals(bodyRequest.getParentIdTag() )) {
				return APIError.response(ErrorMessageResponse.UNIT_UNAVAILABLE);
			}
			
			//Correct reservation
			//Select connector
			int ref = auth.getReservation().getConnectorRef();
			if(ref!=0)
				connRef = ref;
			else {
				for (Connector conn : connService.findByUnit(cu)) {
					if(conn.getStatus().equals(ConnectorStatusComplete.RESERVED))
						connRef = conn.getRef();
				}
			}
			idTag = auth.getIdTag();
			
		}
		else {
			/** NO RESERVATION **/
			
			//Check pending authorization: local on station or remote on cu
			if (  authService.findPendingAuth(cu, null, null) != null )
				return APIError.response(ErrorMessageResponse.UNIT_UNAVAILABLE);
			
			//Check available connector
			for (Connector conn : connService.findByUnit(cu)) {
				if(conn.isCharging())
					return APIError.response(ErrorMessageResponse.UNIT_UNAVAILABLE);
				else if (conn.isFinishing()) {
					connService.updateStatus(conn, ConnectorStatusComplete.AVAILABLE); //force availability after first command
					return APIError.response(ErrorMessageResponse.UNIT_FINISHING);
				}
				else if (conn.isReady())
					connRef = conn.getRef();
			}
			
			if(connRef==0) 
				return APIError.response(ErrorMessageResponse.UNIT_UNAVAILABLE);
			
			//Remote authorization
			//Start to save before communicating to the station
			AuthorizationDTO authDTO = new AuthorizationDTO();
			authDTO.setCu( new ChargingUnitDTO(cu) );
			authDTO.setExternalSession( sessionID );
			authDTO.setProviderId( bodyRequest.getProviderCode() );
			authDTO.setIdTag(idTag); 
			authDTO.setEvcoId(bodyRequest.getParentIdTag());
			authDTO.setResponse(AuthorizationResponse.ACCEPTED);
			authDTO.setType(AuthorizationType.REMOTE);
			
			//All validated: authorize request

			auth = authService.addAuthorization(authDTO, cu); 
		}
		
		
		//Wait for station reply
		AuthWrapper authResponse = serverMap.getServer(cs.getProtocol())
					.requestRemoteStart(cs, connRef, idTag);
		
		if(authResponse.getResponse() == AuthorizationResponse.ACCEPTED) {
			response.setResult(true);
			response.setMessage("Request accepted. Transaction is authorized");
		}
		else {
			response.setResult(false);
			response.setMessage(authResponse.getDescription());
		}

		auth.setResponse(authResponse.getResponse());
		auth.setCsSession(authResponse.getSessionId());
		authService.updateAuthorization(auth);
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	
	}
	
	

	@PostMapping("/reserveNow") //Same of remoteStart
	public ResponseEntity<ReserveNowResponse> reserveNow(HttpServletRequest request, 
			@Valid @RequestBody ReserveNowRequest bodyRequest) {
		// Does not mind if ReserveZeroNotSupported -> always reserve a connector
		
		ReserveNowResponse response = new ReserveNowResponse(false, null);
		String message = "";

	
		logger.debug( String.format("Reservation sulla unit %s", 
				 bodyRequest.getCuEvse()));
		
		//Validate expiry date
		if(bodyRequest.getExpiryDate().isBefore(ZonedDateTime.now())){
			message = "Reservation has already expired";
			response.setMessage(message);
			logger.debug(message);
			return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
		}
		
				
		//CU existence
		ChargingUnit cu = cuService.findByEvse(bodyRequest.getCuEvse());
		if(cu==null) {
			message = ErrorMessageResponse.UNIT_UNEXISTENT.getValue();
			response.setMessage(message);
			logger.debug(message);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		ChargingStation cs = cu.getChargingStation();
	
		//Controllable station
		if( ! cs.isControllable() ) {
			message = ErrorMessageResponse.STATION_UNAVAILABLE.getValue();
			response.setMessage(message);
			logger.debug(message);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		
		//Check actual reservation
		Authorization reserve = authService.findActiveReservation(cu);
		
		if (reserve!=null) {
			//Booked Unit
			logger.debug("Unit already reserved");
			
			message = ErrorMessageResponse.UNIT_UNAVAILABLE.getValue();
			response.setMessage(message);
			logger.debug(message);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		
		
		//All validated : request reservation
		Random rand = new Random();
		String idTag = bodyRequest.getParentIdTag()
				+"-"+rand.nextInt(999); //CREATE idTag from evcoId (max 20 chars TODO)
		
		ReservationDTO reservation = new ReservationDTO();
		
		reservation.setCu(new ChargingUnitDTO(cu));
		reservation.setExpiryDate(bodyRequest.getExpiryDate());
		reservation.setIdTag(idTag); 
		reservation.setEvcoId(bodyRequest.getParentIdTag());
		reservation.setExternalSession(bodyRequest.getSessionId());
		reservation.setStartDate(ZonedDateTime.now());
		
		//Validity of startDate (reservation in the future)
		if( bodyRequest.getStartDate()!= null && 
				bodyRequest.getStartDate().isAfter(ZonedDateTime.now().plusMinutes(StationScheduler.RESERVE_BEFORE_MIN)) ) {
	
			reservation.setStatus(ReserveStatus.WAITING);
			reservation.setStartDate(bodyRequest.getStartDate());
			reserveService.addReservation(reservation, cu);
			
			// no specific connector booked
			
			response.setResult(true);
			response.setMessage(String.format("Unit will be reserved by %s", bodyRequest.getStartDate().toString()));
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		
		/**RESERVE NOW **/
		
		//Define connector for the unit 
		Connector connEntity=null;
		if(bodyRequest.getConnector()==0) {  //NOT SET CONNECTOR
			
			connEntity = connService.findAvailableConnectorInUnit(cu);
			
			if(connEntity==null) {
				message = ErrorMessageResponse.UNIT_UNAVAILABLE.getValue();
				response.setMessage(message);
				logger.debug(message);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		}
		else {
			connEntity = connService.findByUnitAndRef(cu, bodyRequest.getConnector());
			
			if (connEntity==null ) {
				message = ErrorMessageResponse.CONNECTOR_UNEXISTENT.getValue();
				response.setMessage(message);
				logger.debug(message);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			//Available connector
			if ( ! connEntity.isReady() ) {
				message = ErrorMessageResponse.CONNECTOR_UNAVAILABLE.getValue();
				response.setMessage(message);
				logger.debug(message);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		}
		reservation.setConnRef(connEntity.getRef());
		
		Authorization auth = authService.addReservation(reservation, cu);
		Reservation resEntity = auth.getReservation();
		//logger.debug("Added reservation: " + reservation.toString());
		
		boolean result = serverMap.getServer(cs.getProtocol()).requestReserveNow(resEntity);
		
		if(result) {
			response.setResult(true);
			response.setMessage("Unit was succesfully reserved");
			response.setReservationId( String.valueOf(resEntity.getResID()) ); 
			reserveService.updateReservationStatus(resEntity, ReserveStatus.ACCEPTED);
		}
		else {
			response.setMessage("Request of reservation was rejected");
			reserveService.updateReservationStatus(resEntity, ReserveStatus.REJECTED);
		}
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	
	@PostMapping("/cancelReservation")
	public ResponseEntity<AckResponse> cancelReservation(HttpServletRequest request,
			@Valid @RequestBody CancelReservationRequest bodyRequest) {
		
		AckResponse response = new AckResponse(false, null);
		
		
		logger.debug( String.format("Cancellare la reservation con id %s", 
				 bodyRequest.getReservationId()));
		
		
		//Reservation validation
		long resId = Long.valueOf(bodyRequest.getReservationId());
		
		Reservation reservation = reserveService.findById( resId );
		
		if (reservation == null ) 
			return APIError.response("Reservation does not exist");
		
		
		if( reservation.getStatus() != ReserveStatus.ACCEPTED ) {
			
			switch(reservation.getStatus()) {
				case WAITING: //not used
					reserveService.updateReservationStatus(reservation, ReserveStatus.CANCELED);
					response.setResult(true);
					response.setMessage("Reservation canceled");
					break;
				case CANCELED:
					response.setResult(true);
					response.setMessage("Reservation has already been canceled!");
					break;
				case EXPIRED:
					response.setMessage("Reservation already expired!");
					break;
				case STARTED:
					response.setMessage("Transaction already started!");
					break;
				default:
					response.setMessage("Reservation has never been accepted");;
			}
			return new ResponseEntity<>(response, HttpStatus.OK); 
		}

		//Fetch Station
		ChargingStation cs = reservation.getUnit().getChargingStation();
	
		//Controllable station
		if( ! cs.isControllable() ) {
			response.setMessage(ErrorMessageResponse.STATION_UNAVAILABLE.getValue());
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
				
		//All validated: request cancelation
		boolean result = serverMap.getServer(cs.getProtocol()).requestCancelReservation(reservation );
		
		if (result) {
			reserveService.updateReservationStatus(reservation, ReserveStatus.CANCELED);
			authService.abortReservationAuthorization(reservation);
			response.setResult(true);
			response.setMessage("Reservation cancelled");
		}
		else {
			response.setMessage("Station rejected to cancel the reservation");
		}
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}	
	
	
	
	@PostMapping("/setTransactionMaxPower")
	public ResponseEntity<AckResponse> setTransactionMaxPower(HttpServletRequest request, 
			@RequestBody @Valid SCRequest bodyRequest) {
		
		AckResponse response = new AckResponse(false, null);
		
		
		ChargingUnit cu = cuService.findByEvse(bodyRequest.getCuEvse());
		
		if (cu==null)
			return APIError.response(ErrorMessageResponse.UNIT_UNEXISTENT);
		
		
		Transaction tx = null;
		for (Connector conn : connService.findByUnit(cu)) {
			
			tx = txService.findOngoingTransaction(conn);
			
			if(tx!=null)
				break;
		}
		
		if(tx==null )
			return APIError.response("No active transactions on cu");
		
		
		double maxValue = bodyRequest.getMaxValue();
		
		logger.debug(String.format("Setting max to %f %s on unit %s", 
				maxValue, bodyRequest.getUnit().name(), bodyRequest.getCuEvse()));
		
		ChargingStation cs = cu.getChargingStation();
		boolean result = serverMap.getServer(cs.getProtocol()).setTxPower(tx, bodyRequest.getUnit(), maxValue);
		
	
		response.setResult(result);
		response.setMessage("");
		return new ResponseEntity<>(response, HttpStatus.OK);

	}



	@PostMapping("/stopRemote")
	public ResponseEntity<AckResponse> remoteStop(HttpServletRequest request, 
			@Valid @RequestBody RechargeRequest bodyRequest) {
		
		AckResponse response = new AckResponse(false, null);
		
		
		logger.debug( String.format("Terminare la transazione %s", 
				 bodyRequest.getSessionId()));
	
		String extSession = bodyRequest.getSessionId();
		
		//Tx existence
		Authorization auth = authService.findByExternalSession(extSession);
		if(auth==null) {
			response.setMessage("Transaction does not exist");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		Transaction tx = txService.findByAuthorization(auth);
		if(tx==null) {
			authService.abortAuthorization(auth, EndTxReason.REMOTE, null);
			response.setResult(true);
			response.setMessage("Authorization aborted");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		
		if( tx.getStatus().equals(TransactionStatus.FINISHED)) {
			response.setResult(true);
			response.setMessage("Transaction already ended");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		else if(tx.getStatus().equals(TransactionStatus.MANUAL_CHECKOUT)) {
			response.setResult(true);
			response.setMessage("Transaction can only be stopped by an operator");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		
		//Controllable station
		ChargingStation cs = auth.getCu().getChargingStation();
		if( ! cs.isConnected() ) 
			return APIError.response(ErrorMessageResponse.STATION_UNAVAILABLE);
			
		//All validated: request to stop
		AuthorizationResponse authResponse = serverMap.getServer(cs.getProtocol())
								.requestRemoteStop(cs, (int) tx.getTxID() );
		
		if(authResponse == AuthorizationResponse.ACCEPTED) {
			response.setResult(true);
			response.setMessage("Request accepted. Stopping transaction...");
		}
		else {
			response.setResult(false);
			response.setMessage("Request rejected from the station");
		}
	
		return new ResponseEntity<>(response, HttpStatus.OK);
	
	}



	@PostMapping("/pullData")
		public ResponseEntity<RechargeDetailsResponse> getTransactionData(HttpServletRequest request, 
				@RequestBody @Valid RechargeRequest bodyRequest) {
			
			RechargeDetailsResponse response = new RechargeDetailsResponse(false, null);
	
			String extSession = bodyRequest.getSessionId();
			
			//Tx existence
			Authorization auth = authService.findByExternalSession(extSession);
			if(auth==null) {
				response.setMessage("Transaction does not exist");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			response.setResult(true);
			
			Transaction tx = txService.findByAuthorization(auth);
			if(tx==null) {
				logger.debug(String.format("Transaction %s never started", auth.getExternalSession()));
				if (auth.getResponse()==AuthorizationResponse.ACCEPTED)
					response.setStatus(TransactionStatus.AUTHORIZED);
				else //if (auth.getResponse()==AuthorizationResponse.ABORTED)
					response.setStatus(TransactionStatus.EXPIRED);
			}
			else {
				response.setConnRef(tx.getConnector().getRef());
				response.setStatus(tx.getStatus());
				response.setParkDate(tx.getStopChargingDate());
				response.setData( meterService.getRechargeDetails( tx, false ) );
			}
			
			response.setProviderCode(auth.getProviderId());
	
	//		logger.debug(response.toString());
	
			response.setMessage("Retrieved recharge details");
			return new ResponseEntity<>(response, HttpStatus.OK);
	
		}



	@PostMapping("/simulateStopTransaction")
	public ResponseEntity<AckResponse> simulateStopTransaction(HttpServletRequest request, 
			@RequestBody ForcedStopRequest bodyRequest) {
		
		logger.debug(String.format("Checkout: %s", bodyRequest.toString()));
		
		AckResponse response = new AckResponse(false, null);
		
	
		//Tx existence
		Authorization auth = authService.findByExternalSession(bodyRequest.getSessionId());
		if(auth==null) {
			response.setMessage("Transaction does not exist");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		
		EndTxReason reason = EndTxReason.Unknown;
		if(bodyRequest.getReason()!=null)
			reason = bodyRequest.getReason();
		ZonedDateTime stopTime = bodyRequest.getTimestamp();
		
		Transaction tx = txService.findByAuthorization(auth);
		if(tx==null) {
			
			authService.abortAuthorization(auth, reason, null);
			response.setMessage("Recharge correctly stopped");
			response.setResult(true);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		
		
		if(stopTime==null) {
			stopTime = ZonedDateTime.now();
			MeterRecord lastRec = meterService.findLastEnergyMeter(tx, false);
			if(lastRec!=null)
				stopTime = lastRec.getSendTime();
		}
		if(stopTime.isBefore(tx.getStartDate()) || stopTime.isAfter(ZonedDateTime.now())) {
			response.setMessage("Wrong stop date");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
			
			
		
		if(tx.getMeterStop()==null && bodyRequest.getConsumedEnergy()!=null)
			tx.setMeterStop(tx.getMeterStart() + bodyRequest.getConsumedEnergy());
		
		txService.stopTransaction(tx, stopTime, reason, true, true); //TODO deal with exception on stopping
		if(txService.findOngoingTransaction(tx.getConnector())==null)
			connService.updateStatus(tx.getConnector(), ConnectorStatusComplete.AVAILABLE);
		
		response.setResult(true);
		response.setMessage("Recharge correctly stopped");
		return new ResponseEntity<>(response, HttpStatus.OK);
	
	}
	
}



