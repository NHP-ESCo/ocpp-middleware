package it.besmart.ocpp.servers.v16;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import eu.chargetime.ocpp.model.core.AuthorizationStatus;
import eu.chargetime.ocpp.model.core.AuthorizeConfirmation;
import eu.chargetime.ocpp.model.core.AuthorizeRequest;
import eu.chargetime.ocpp.model.core.BootNotificationConfirmation;
import eu.chargetime.ocpp.model.core.BootNotificationRequest;
import eu.chargetime.ocpp.model.core.ChargePointErrorCode;
import eu.chargetime.ocpp.model.core.ChargePointStatus;
import eu.chargetime.ocpp.model.core.DataTransferConfirmation;
import eu.chargetime.ocpp.model.core.DataTransferRequest;
import eu.chargetime.ocpp.model.core.DataTransferStatus;
import eu.chargetime.ocpp.model.core.HeartbeatConfirmation;
import eu.chargetime.ocpp.model.core.HeartbeatRequest;
import eu.chargetime.ocpp.model.core.IdTagInfo;
import eu.chargetime.ocpp.model.core.MeterValue;
import eu.chargetime.ocpp.model.core.MeterValuesConfirmation;
import eu.chargetime.ocpp.model.core.MeterValuesRequest;
import eu.chargetime.ocpp.model.core.Reason;
import eu.chargetime.ocpp.model.core.RegistrationStatus;
import eu.chargetime.ocpp.model.core.SampledValue;
import eu.chargetime.ocpp.model.core.StartTransactionConfirmation;
import eu.chargetime.ocpp.model.core.StartTransactionRequest;
import eu.chargetime.ocpp.model.core.StatusNotificationConfirmation;
import eu.chargetime.ocpp.model.core.StatusNotificationRequest;
import eu.chargetime.ocpp.model.core.StopTransactionConfirmation;
import eu.chargetime.ocpp.model.core.StopTransactionRequest;
import it.besmart.ocpp.client.external.IClientService;
import it.besmart.ocpp.dtos.AuthorizationDTO;
import it.besmart.ocpp.dtos.ChargingUnitDTO;
import it.besmart.ocpp.dtos.RecordStationStatusDTO;
import it.besmart.ocpp.dtos.MeterRecordDTO;
import it.besmart.ocpp.dtos.ReservationDTO;
import it.besmart.ocpp.dtos.TransactionDTO;
import it.besmart.ocpp.enums.AuthorizationResponse;
import it.besmart.ocpp.enums.AuthorizationType;
import it.besmart.ocpp.enums.CSLifeStatus;
import it.besmart.ocpp.enums.StationStatusComplete;
import it.besmart.ocpp.enums.ConnectorStatusComplete;
import it.besmart.ocpp.enums.ErrorType;
import it.besmart.ocpp.enums.MeasurandType;
import it.besmart.ocpp.enums.ProtocolParam;
import it.besmart.ocpp.enums.ReserveStatus;
import it.besmart.ocpp.model.Authorization;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.ChargingUnit;
import it.besmart.ocpp.model.Connector;
import it.besmart.ocpp.model.MeterRecord;
import it.besmart.ocpp.model.Reservation;
import it.besmart.ocpp.model.StationCapability;
import it.besmart.ocpp.model.StoredLog;
import it.besmart.ocpp.model.Transaction;
import it.besmart.ocpp.servers.ServerMap;
import it.besmart.ocpp.services.ErrorService;
import it.besmart.ocpp.services.interfaces.IAuthorizationService;
import it.besmart.ocpp.services.interfaces.ICUService;
import it.besmart.ocpp.services.interfaces.IConnectorService;
import it.besmart.ocpp.services.interfaces.IErrorService;
import it.besmart.ocpp.services.interfaces.IMeterRecordService;
import it.besmart.ocpp.services.interfaces.IConfigParamService;
import it.besmart.ocpp.services.interfaces.IReservationService;
import it.besmart.ocpp.services.interfaces.IStationCapabilityService;
import it.besmart.ocpp.services.interfaces.IStationService;
import it.besmart.ocpp.services.interfaces.IStatusService;
import it.besmart.ocpp.services.interfaces.ITxService;
import it.besmart.ocppLib.enumeration.CapabilityType;
import it.besmart.ocppLib.enumeration.ConnectorStatus;
import it.besmart.ocppLib.enumeration.EndTxReason;
import it.besmart.ocppLib.wrappers.Response.AuthorizationStartResponse.AuthorizationStatusEnum;
import it.besmart.ocppLib.wrappers.Response.AuthorizationStopResponse;


@Service("coreProfileHandler_16")
@Transactional
@DependsOn("serverMap")
public class CoreProfileHandler_v16 implements ICoreProfileHandler {

	private final Logger logger = LoggerFactory.getLogger(CoreProfileHandler_v16.class);
	
	@Autowired
	private IErrorService errorService;
	
	@Autowired
	private ServerMap serverMap;
	
	@Autowired
	private IStationService stationService;
	
	@Autowired
	private ICUService cuService;
	
	@Autowired
	private IConnectorService connService;
	
	@Autowired
	private IConfigParamService paramService;
	
	@Autowired
	private IStatusService statusService;
	
	@Autowired
	private IAuthorizationService authService;
	
	@Autowired
	private ITxService txService;
	
	@Autowired
	private IClientService clientService;
	
	@Autowired
	private IReservationService reservationService;
	
	@Autowired
	private IMeterRecordService meterService;
	
	@Autowired
	private IStationCapabilityService capabilityService;
	
	
	public CoreProfileHandler_v16() {
		super();
		logger.debug("Create CoreProfileHandler for 1.6");
	}
	
	
	@Override
	public AuthorizeConfirmation handleAuthorizeRequest(UUID sessionIndex, AuthorizeRequest request) {
		
		ChargingStation cs = stationService.findBySession(sessionIndex);
		
		logger.debug("Received : " + request.toString() + " from Station " + cs.getEvseID());
		
		AuthorizationStatus status = AuthorizationStatus.Invalid;
		
		boolean start = true;
		String idTag = request.getIdTag();
		String parentId = null;
		
		if(cs.getModel().isRfidPerUnit()) { 
			//one rfid reader per unit, station can distinguish authorization start/stop
			
			//Check END SESSION: when ongoing tx has the same PARENT tag of starting rfid 
			
			for (Transaction tx : txService.findOngoingTransactions(cs)) { 
				if(tx.getAuthorization()==null)
					break;
				
				if ( tx.getAuthorization().getIdTag().equals(request.getIdTag())) {
					//Same customer who started the transaction
					logger.debug("Requested authorization to STOP recharge");
					
					status = AuthorizationStatus.Accepted;
					start = false;
				}
				else {
					
					logger.debug(String.format("Customer %s could try to stop transaction %s, started by customer: %s", 
							request.getIdTag(), tx.getAuthorization().getExternalSession(), tx.getAuthorization().getIdTag()));
					
					if(tx.getAuthorization().getEvcoId()!=null) { // customer was stored explicitly
						
						AuthorizationStopResponse result = clientService.authorizeStop(tx, request.getIdTag());
						
						if(result.getStatus().equals(AuthorizationStatusEnum.AUTHORIZED)) {
							
							start = false;
							parentId = tx.getAuthorization().getEvcoId();
							status = AuthorizationStatus.Accepted;
							
							logger.debug(String.format("End tx authorized with card %s", request.getIdTag()));
						}
						else {
							
							//stopping not authorized
							//if not valid and some unit is available proceed with starting procedure
							start = false;
							for(Connector conn : connService.findByStation(cs)) {
								if(!conn.isCharging())
									start = true;
							}
							
						}
					}
					
				}
			}
		}
		
		if(start) {
			//remote authorization or local list -> automatically authorize
			// in this case we can preauthorize when cu is reserved with idTag-parentId
			boolean availableUnit = false;
			for(Connector conn : connService.findByStation(cs)) {
				if(conn.getStatus().simpleStatus().equals(ConnectorStatus.AVAILABLE)) {
					availableUnit = true;
					break;
				}
			}
			
			if(availableUnit) {
				
				StationCapability c = capabilityService.findCapability(cs, CapabilityType.FreeCharging);
				
				boolean freeMode = c!=null && c.getValue().isEnabled();
				
				if (freeMode || authService.isPreAuthorized(cs, idTag)) {  	
					//remote start automatically authorized 
					//automatically accept also idTag in current reservation? should directly start tx
					
					logger.debug("Already authorized remotely");
					
					//authorizeRemote should be true
					boolean authorizeRemote = Boolean.valueOf( paramService
							.findParameterInStation(ProtocolParam.AuthorizeRemoteTxRequests.getValue(), cs)
							.getValue() );
					
					if(!authorizeRemote) 
						logger.warn("Parameter AuthorizeRemote is NOT true");
					
					//Automatically accepted without resending to provider, auth already in table
					status = AuthorizationStatus.Accepted;
				}
				else { //START NEW SESSION: local authorization
					
					logger.debug("Ask authorization for starting new transaction");
					
					Authorization auth = authService.askLocalAuthorization(cs, null, idTag, null, ZonedDateTime.now());
					
					if(auth.getResponse() == AuthorizationResponse.ACCEPTED) { //Only accepted or invalid
						status = AuthorizationStatus.Accepted;
						parentId = auth.getEvcoId();
					}
					
				}
				
			}
			
			
		}
		
		if(!cs.getModel().isRfidPerUnit())
			parentId = "";
		
		AuthorizeConfirmation conf = new AuthorizeConfirmation( customerInfo(parentId, status) );
		logger.debug("Reply : " + conf.toString());
		
		return conf;
	}

	
	@Override
	public BootNotificationConfirmation handleBootNotificationRequest(UUID sessionIndex,
			BootNotificationRequest request) {

		ChargingStation station = stationService.findBySession(sessionIndex);
		logger.debug("Received : " + request.toString() + " from Station " + station.getEvseID());
		
		//for confirmation
		Integer interval;  //retry of boot notification chosen by station
		RegistrationStatus status = RegistrationStatus.Pending;
		
		
		station.setFirmware(request.getFirmwareVersion());
		ChargingStation cs = stationService.updateStation(station);
		
		
		if(cs.getLifeStatus() == CSLifeStatus.INSTALLED) {  //commissioning

			//Check vendor, model and serial number
			if ( checkNewStationValidity(cs, request) ) {
				
				status =  RegistrationStatus.Accepted ;
				
				//async to first reply to boot notification
				new Thread(() -> {
					
					serverMap.getServer(cs.getProtocol()).configureStation(cs, true, true, true);
					
				}).start();
				
			}
			else {
				
				String msg = String.format("Tentativo di commissioning con vendor %s e model %s", request.getChargePointVendor(), request.getChargePointModel());
				
				errorService.saveError(msg, ErrorType.FAULTED_COMMISSIONING, cs, null, null);
				
				status = RegistrationStatus.Rejected;
				
			}
			
		}
		else { 
			
			status =  RegistrationStatus.Accepted ;
			
			if( ! cs.getLifeStatus().isInConfiguration() ) {
				//async to first reply to boot notification
				new Thread(() -> {
					
					//to configure after reboot
					//Automatically done at connection
					serverMap.getServer(cs.getProtocol()).configureStation(cs, cs.getLifeStatus().equals(CSLifeStatus.FIRST_CONFIGURATION), true, true);
					
				}).start();
			}
			
		}
		
		
		if(status == RegistrationStatus.Accepted) 
			interval = Integer.valueOf( paramService.findParameterInStation(ProtocolParam.HeartbeatInterval.getValue(), cs).getValue() );			
		else
			interval = ServerOCPP_v16.BOOT_RETRY_SEC;
		
		BootNotificationConfirmation conf = new BootNotificationConfirmation( ZonedDateTime.now(ZoneOffset.UTC), interval, status );
		logger.debug("Reply : " + conf.toString());
		
		return conf;
	}

	@Override
	public DataTransferConfirmation handleDataTransferRequest(UUID sessionIndex, DataTransferRequest request) {
		//TODO : manage w.r.t model
		ChargingStation cs = stationService.findBySession(sessionIndex); 
		errorService.saveError(request.toString(), ErrorType.DATA_TRANSFER, cs, null, null);
		return new DataTransferConfirmation(DataTransferStatus.Accepted);
	}

	@Override
	public HeartbeatConfirmation handleHeartbeatRequest(UUID sessionIndex, HeartbeatRequest request) {
		
		ChargingStation cs = stationService.findBySession(sessionIndex); 
		
		RecordStationStatusDTO record = new RecordStationStatusDTO(cs.getStatus(), ZonedDateTime.now());
		
		statusService.updateCSStatus(record, cs); 
		
		HeartbeatConfirmation conf = new HeartbeatConfirmation( ZonedDateTime.now(ZoneOffset.UTC) );
		//logger.debug("Reply : " + conf.toString());
		
		return conf;
	}

	
	@Override
	public MeterValuesConfirmation handleMeterValuesRequest(UUID sessionIndex, MeterValuesRequest request) {
		//either during transaction or clockalignedsample
		
		ChargingStation cs = stationService.findBySession(sessionIndex); 
//		logger.debug(String.format("%s : %s ", cs.getEvseID(), request.toString() ) );
		
		Integer txId = request.getTransactionId();
		if ( txId!= null && txId !=0) 
			storeMeterRecords(request.getMeterValue(), cs, txService.findById(request.getTransactionId()), null );
		else 
			storeMeterRecords(request.getMeterValue(), cs, null, request.getConnectorId() );
	
		
		MeterValuesConfirmation conf  = new MeterValuesConfirmation();
		
		return conf;
	}

	
	@Override
	public StartTransactionConfirmation handleStartTransactionRequest(UUID sessionIndex,
			StartTransactionRequest request) {

		ChargingStation cs = stationService.findBySession(sessionIndex); 
		logger.debug("Received : " + request.toString() + " from Station " + cs.getEvseID());
		
		// init for confirmation
		boolean resetMeter = false;
		AuthorizationStatus status =  AuthorizationStatus.Invalid;
		
		Connector conn = connService.findByStationAndRef(cs, request.getConnectorId());
		errorService.solveConnectorErrors(conn, "ConnectorLockFailure"); // a way to solve this error: a recharge is starting
		
		//Check protocol error
		Transaction currentTx = txService.findOngoingTransaction(conn);
		if(currentTx!=null) {
			Authorization oldAuth = currentTx.getAuthorization();
			if(oldAuth.getIdTag().equals(request.getIdTag()) && 
					Duration.between(oldAuth.getTimestamp(), request.getTimestamp()).getSeconds() < 60) {
				errorService.saveError(String.format("Request to start transaction with idTag %s was duplicated",
						request.getIdTag()), ErrorType.PROTOCOL_ERROR, cs, null, oldAuth.getExternalSession());
				
				StartTransactionConfirmation conf = new StartTransactionConfirmation(customerInfo(null, status), (int) currentTx.getTxID());
				return conf;
			}
				
		}
		
		Double meterStart = request.getMeterStart().doubleValue()/1000;
		if(meterStart==0 ) {
			logger.warn("Tx starts with meter set to 0");
			StationCapability c = capabilityService.findCapability(cs, CapabilityType.PhysicalMeter);
		
			if(c!=null && c.getValue().isEnabled()) {
				logger.warn("Physical meter is present");
				MeterRecord lastMeter = meterService.findLastEnergyMeter(conn.getUnit());
				if (lastMeter!=null) {
//					meterStart = lastMeter.getValue();
					resetMeter = true;
				}
			}
			else {
				logger.debug("Physical meter is absent");
			}
		}
		
		TransactionDTO txDTO = new TransactionDTO();
		txDTO.setMeterStart( meterStart );
		txDTO.setStartDate(request.getTimestamp());
		txDTO.setResetMeter(resetMeter);
		
		boolean reserved = false;
		if(request.getReservationId() != null && request.getReservationId() != 0) {
			//Only set id
			Reservation reserve = reservationService.findById(request.getReservationId());
			if(reserve!=null) {
				//Check status and idTag of reservation
				if( reserve.getIdTag().equals(request.getIdTag()) 
						&& reserve.getStatus().equals(ReserveStatus.ACCEPTED) ) {
					txDTO.setReservation(new ReservationDTO(reserve.getResID()));
					reserved = true;
				}
				else {
					errorService.saveError(String.format("Request to start transaction with idTag %s. Reservation conflict %d: actual status is %s and actual idTag is %s",
										request.getIdTag(), reserve.getResID(), reserve.getStatus(), reserve.getIdTag()), ErrorType.PROTOCOL_ERROR, cs, null, null);
				}
			}
			else
				errorService.saveError(String.format("Started transaction, but station sent unknown reservation %d",
						request.getReservationId()), ErrorType.PROTOCOL_ERROR, cs, null, null);
		}
		
		StationCapability c = capabilityService.findCapability(cs, CapabilityType.FreeCharging);
		
		boolean freeMode = c!=null && c.getValue().isEnabled();
		
		Authorization auth = null;
		if(freeMode) {
			status = AuthorizationStatus.Accepted;
			
			AuthorizationDTO authorization = new AuthorizationDTO();
			authorization.setType(AuthorizationType.LOCAL);
			authorization.setIdTag(request.getIdTag());
			authorization.setResponse(AuthorizationResponse.ACCEPTED);
			authorization.setExternalSession(UUID.randomUUID().toString());
			
			auth = authService.saveFreeModeAuthorization(authorization, conn.getUnit());
		}
		else {
			
			//TODO check if idTag is evcoId composed
			auth = authService.findPendingAuth(conn.getUnit(), null, request.getIdTag());
			if (auth == null ) {
				// no authorization if: local authorization with localPreAuthorize, or delayed authorization
				// or automatic start after reservation or freeMode
				
				if(!freeMode) {
					//TODO: configure parameters when ok or not
					if(!reserved ) {
						logger.warn("Unauthorized transaction. Ask authorization");
					}
					
					auth = authService.askLocalAuthorization(cs, conn.getUnit(), request.getIdTag(), null, request.getTimestamp());
						
				}
			}
			
			if (auth.getResponse()==AuthorizationResponse.ACCEPTED) {
				status = AuthorizationStatus.Accepted;
			}
		}
		
		Transaction tx = txService.addTransaction(txDTO, auth, conn);
		
		//Add first record of tx
		meterService.addStartTxRecord(tx);
		
		if(resetMeter) {
			errorService.saveConnectorError("Meter started from 0", ErrorType.RECHARGE_ERROR, conn, tx.getExternalSession());
			
		}
		
		StartTransactionConfirmation conf = new StartTransactionConfirmation(customerInfo(null, status), (int) tx.getTxID());
//		logger.debug("Reply : " + conf.toString());
		
		return conf;
	
	}

	
	@Override
	public StatusNotificationConfirmation handleStatusNotificationRequest(UUID sessionIndex,
			StatusNotificationRequest request) {
		
		ChargingStation cs = stationService.findBySession(sessionIndex); 
		Connector conn = null;
		
		if(request.getTimestamp()==null)
			request.setTimestamp(ZonedDateTime.now());
		
		//Manage status
		ChargePointStatus newStatus = request.getStatus();
		Integer cuRef = request.getConnectorId();
		
		// Manage error 
		String errorMsg = getErrorMsg(request.getErrorCode(), request.getVendorErrorCode(), request.getInfo());
		
		//delta button error, specific case
		boolean emergencyButton = errorMsg.equalsIgnoreCase(ErrorService.deltaButtonError); 
		ConnectorStatusComplete status = null;
		StoredLog error = null;
		
		if (cuRef == 0) {
			
			StationStatusComplete stSatus = StationStatusComplete.fromValue(newStatus.name());
			
			RecordStationStatusDTO record = new RecordStationStatusDTO(stSatus, request.getTimestamp());
			
			statusService.updateCSStatus(record, cs);
		}
		else {
			
			status = ConnectorStatusComplete.fromValue(newStatus.name());
			conn = connService.findByStationAndRef(cs, cuRef);
			
			if(conn==null) {
				//OCPP error
				errorService.saveError(request.toString(), ErrorType.PROTOCOL_ERROR, cs, null, null);
				return new StatusNotificationConfirmation();
			}
			
			/** Change status **/
			ZonedDateTime sendTime = request.getTimestamp();
			if(sendTime==null) {
				logger.warn(request.toString());
				sendTime = ZonedDateTime.now();
			}
			
			if(!emergencyButton)
				statusService.changeConnectorStatus(conn, status, sendTime); 
			
		}
			
		/** Store error **/
		if(!errorMsg.isEmpty()) { 
			
			if(!newStatus.equals(ChargePointStatus.Faulted))
				logger.warn(String.format("%s Received error %s with status %s", 
						cs.getEvseID(), errorMsg, newStatus.name()));
			
			if (conn!=null 
					&& !emergencyButton) {  
				/** Fault on specific connector **/
				
				String session = txService.findOngoingSession(conn);
				
				error = errorService.storeConnectorError( errorMsg, ErrorType.STATION_ERROR, conn, session);
			
				if(session!=null)
					authService.abortPendingAuthorization(conn.getUnit(), EndTxReason.FAULT, error);
				
			}
			else {
				//TODO I Cannot relate the error to session
				errorService.saveError(errorMsg, ErrorType.STATION_ERROR, cs, null, null);
			}
		} 
		else if (newStatus.equals(ChargePointStatus.Available)) {	//delete errors related to connectors
			if (cuRef!=0 ) {
				errorService.solveStationErrors(cs, ErrorType.STATION_ERROR, ErrorService.deltaButtonError);
				errorService.solveConnectorErrors(connService.findByStationAndRef(cs, cuRef), null);
			}
		}
		
		
		return new StatusNotificationConfirmation();
	}

	
	private String getErrorMsg(ChargePointErrorCode errorCode, String vendorErrorCode, String info) {
		String errorMsg = "";
		if(StringUtils.isAllBlank(info))
			info = "";
		
		
		if(errorCode!=null && errorCode != ChargePointErrorCode.NoError) {
			
			errorMsg = errorCode.toString() + " " + info;
			
			logger.warn(String.format("%s %s %s", errorCode.toString(), vendorErrorCode, info));
			
		}
		else if (vendorErrorCode!=null && !vendorErrorCode.isEmpty() ) {
			
			String cleanCode = vendorErrorCode.replaceAll("0", "");
			
			if(cleanCode.isBlank() || cleanCode.equals("x")) {
				return "";
			}
			
			errorMsg = vendorErrorCode + " " + info;
			
			logger.warn(String.format("%s %s", vendorErrorCode, info));
			
		}
		
		return errorMsg;
	}


	@Override
	public StopTransactionConfirmation handleStopTransactionRequest(UUID sessionIndex,
			StopTransactionRequest request) {
		
		ChargingStation cs = stationService.findBySession(sessionIndex); 
		logger.debug("Received : " + request.toString() + " from Station " + cs.getEvseID());
		
		// for confirmation
		AuthorizationStatus status =  AuthorizationStatus.Accepted;
		
		Transaction tx = txService.findById(request.getTransactionId());
		
		if (tx==null || ! tx.getConnector().getUnit().getChargingStation().equals(cs)) {  //Station error
			errorService.saveError(String.format("Request to stop unexistent transaction %d", request.getTransactionId()), ErrorType.PROTOCOL_ERROR, cs, null, null);
			return new StopTransactionConfirmation();
		}
		
		if (request.getIdTag() != null && ! request.getIdTag().isEmpty()) {
			
			if (tx.getAuthorization()==null)
				logger.warn("Unknown customer recharged his vehicle");
			else if( ! tx.getAuthorization().getIdTag().equals(request.getIdTag()) ) {
				//Different customer  w.r.t. start transaction. IdTags with same parentIdTag
				logger.warn("Different customer w.r.t. start of transaction.\n Starting customer: " + tx.getAuthorization().getIdTag()
						+ "\n Stopping customer: " + request.getIdTag() );
				
				tx.setStoppingIdTag(request.getIdTag());
			}
		}
		
		//Store last meter record
		boolean lastMeter = false;
		if (request.getTransactionData() != null)
			storeMeterRecords(request.getTransactionData(), cs, tx, null);
		else  //Store last record of energy
			lastMeter = true;
		
		tx.setMeterStop( request.getMeterStop().doubleValue() /1000 );
		tx = txService.stopTransaction(tx, request.getTimestamp(), getEndReason(request.getReason()), lastMeter, false);
		
		
		StopTransactionConfirmation conf = new StopTransactionConfirmation();
		conf.setIdTagInfo(customerInfo(null, status));
		
		return conf;
	}
		
	
	private EndTxReason getEndReason(Reason reason) {
		if(reason!=null)
			return EndTxReason.fromValue(reason.toString());
		else
			return EndTxReason.LOCAL;
	}


	/**** PRIVATE METHODS ****/
	
	@SuppressWarnings("deprecation")
	private boolean checkNewStationValidity(ChargingStation cs, BootNotificationRequest request) {
		
		boolean valid = true;
		
		if ( ! cs.getModel().getBrand().getSupplier().equals(request.getChargePointVendor()) ) {
			valid = false;
			logger.warn("Invalid supplier");
		}
		
		if ( ! cs.getModel().getBrandCode().equals(request.getChargePointModel())) {
			valid = false;
			logger.warn("Invalid model code");
		}
		
		//Chargebox or charge point
		String serialNumber = request.getChargePointSerialNumber();
		if (serialNumber== null)
			serialNumber = request.getChargeBoxSerialNumber();
		if (serialNumber!= null && ! cs.getSerialNumber().equals(serialNumber) ) {
			//valid = false;
			//logger.warn("Invalid serial number"); //TODO is not serial number
		}
			
		return  valid;
	}
	
	//Either during recharge or clock-aligned
	@Async
	private void storeMeterRecords(MeterValue[] values, ChargingStation cs, 
			Transaction tx, Integer cuRef) {
		
		ChargingUnitDTO cuDTO = new ChargingUnitDTO();
		MeterRecord lastRec = null; 
		ChargingUnit cu = null;
		
		if(tx!=null)
			cu = tx.getConnector().getUnit();
		else if(cuRef != null) {
			cu = connService.findByStationAndRef(cs, cuRef).getUnit();
//			tx = txService.findOngoingTransaction(cu); //clockaligned could be related to tx 
		}
		else {
			//unknown meter, only one?
			List<ChargingUnit> cus = cuService.findByStation(cs);
			
			if(cus.size()==1)
				cu = cus.get(0);
		}
		
		if(cu!=null) {
			cuDTO = new ChargingUnitDTO( cu );	
		}
		
		
		if(tx!=null)
			lastRec = meterService.findLastEnergyMeter(tx, false);
		else if(cu!=null) {
			lastRec = meterService.findLastEnergyMeter(cu);
		}
		
		
		boolean savePower = true;
		MeterRecordDTO powerRecord = new MeterRecordDTO();
		powerRecord.setMeasurand(MeasurandType.POWER);
		
		for (MeterValue mv : values ) {
			for (SampledValue sample : mv.getSampledValue()) {
				
				MeterRecordDTO record = new MeterRecordDTO();
				record.setSendTime(mv.getTimestamp());
				
				if (cu != null) 
					record.setCu(cuDTO);
				else
					logger.warn(String.format("Meter record for station %s: %s", cs.getEvseID(), sample.toString()));
				
				
				//Check measurand
				//logger.debug("Sample : " + sample.toString() );
				
				boolean storeRecord = true;
				if(sample.getContext().equals("Sample.Clock"))
					storeRecord = false;
				
				
				//Manage measurands 
				String[] comps = sample.getMeasurand().split("\\.");
				MeasurandType measurand =  MeasurandType.fromValue( comps[0] );
				record.setMeasurand(  measurand  );
				String phase = sample.getPhase();
				if(phase!=null && !phase.isEmpty()) {
//					logger.debug(sample.toString());
					record.setPhase(getPhase(phase));
				}
				
				Double value = Double.valueOf(sample.getValue());
				
				//Correct value
				switch (measurand) {
				
					case ENERGY: {
						if(sample.getUnit().charAt(0) != 'k' ) {
							value = value/1000;
						}
						if( comps[1].equals("Reactive")) {
							record.setMeasurand(MeasurandType.ENERGY_REACTIVE);
						}
						if( comps[2].equals("Export") ) {
							value = - value;
							storeRecord = false; //TODO: deal with negative energy flow
						}
						if (comps[3].equals("Interval") ) {
							//Look for last record of energy: alway store as register
							
							if(lastRec!=null) {
								value += lastRec.getValue();
								
								logger.debug("Energy interval " + tx.getTxID());
							}
						}
						
						if(lastRec!=null && phase==null && storeRecord) {
							double hours = ((double) Duration.between(lastRec.getSendTime(), record.getSendTime()).getSeconds()) / 3600;
							if(hours!=0) {
								double power = (value - lastRec.getValue())/hours;
								powerRecord.setValue(power);
								powerRecord.setSendTime(record.getSendTime());
								powerRecord.setCu(record.getCu());
								powerRecord.setComputed(true);
							}
							
							
						}
							
						break;
					}
					case POWER: {
						if(record.getPhase()==null)
							savePower = false;
						
						if(sample.getUnit().charAt(0) != 'k' )
							value = value/1000;
						if( comps[1].equals("Reactive")) {
							record.setMeasurand(MeasurandType.POWER_REACTIVE);
						}
						else if (! comps[1].equals("Active")) {
							storeRecord = false;   //TODO: power factor or power offered not stored
						}
						if( comps.length > 2 && comps[2] == "Export" ){
							value = - value;
						}
						
						
						break;
					}
					case CURRENT: {
						if( comps[1].equals("Export") )
							value = - value;
						break;
					}
					case TEMPERATURE:
						break;  // convert from farenheit or celsius
					default:
						break;
				}
				
				record.setValue( value ); 
				

				if(lastRec!=null && tx!=null 
						&& lastRec.getMeasurand().equals(record.getMeasurand()) 
						&& lastRec.getSendTime().equals(record.getSendTime()) 
						&& lastRec.getPhase() == record.getPhase() ) {
					
					logger.warn(String.format("Received duplicate meter value for tx %d", tx.getTxID()));
					
					if( ! lastRec.getValue().equals( value ) ) {
						logger.warn(String.format("Duplicate meter value with different values: last %s current %s",
								lastRec.toString(), record.toString()));
					}
					else
						storeRecord = false;
				}
				
				if ( storeRecord ) {
						
					lastRec = meterService.addRecord(record, cs, tx);
					
				}
				else
					logger.warn(String.format("%s not stored sample: %s", cs.getEvseID(), sample.toString()));
			}
		}
		
		//SAVE POWER IF NOT SENT BY CU
		if(tx!=null && savePower && powerRecord.isComputed()) {
			logger.debug(String.format("%s: Computed power %.2f kW for tx %d", 
					cu.getEvseCode(), powerRecord.getValue(), tx.getTxID()));
			meterService.addRecord(powerRecord, cs, tx);
		}
	}
	
	
	private Integer getPhase(String phase) {
		if(phase.contains("1"))
			return 1;
		else if (phase.contains("2"))
			return 2;
		else if (phase.contains("3"))
			return 3;
		
		return null;
	}


	private IdTagInfo customerInfo(String parentId, AuthorizationStatus status) {
		
		IdTagInfo info = new IdTagInfo(status);  
		
		info.setStatus(status);
		info.setParentIdTag(parentId);
		if(parentId!= null) {
			
//			info.setExpiryDate( parentId.getExpiryDate() ); 
		}
		
		return info;
	}
	
	
}
	
