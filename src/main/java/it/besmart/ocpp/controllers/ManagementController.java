package it.besmart.ocpp.controllers;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequestType;
import it.besmart.ocpp.enums.AuthorizationResponse;
import it.besmart.ocpp.enums.ConnectorStatusComplete;
import it.besmart.ocpp.enums.ErrorMessageResponse;
import it.besmart.ocpp.exceptions.UnsupportedRequestException;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.Connector;
import it.besmart.ocpp.model.DiagnosticsRecordStatus;
import it.besmart.ocpp.model.Model;
import it.besmart.ocpp.model.StationCapability;
import it.besmart.ocpp.model.StoredLog;
import it.besmart.ocpp.model.Transaction;
import it.besmart.ocpp.model.WlCard;
import it.besmart.ocpp.servers.ServerMap;
import it.besmart.ocpp.services.interfaces.IConnectorService;
import it.besmart.ocpp.services.interfaces.IDiagnosticsService;
import it.besmart.ocpp.services.interfaces.IErrorService;
import it.besmart.ocpp.services.interfaces.IFirmwareService;
import it.besmart.ocpp.services.interfaces.IModelService;
import it.besmart.ocpp.services.interfaces.IStationCapabilityService;
import it.besmart.ocpp.services.interfaces.IStationService;
import it.besmart.ocpp.services.interfaces.ITxService;
import it.besmart.ocpp.services.interfaces.IWlService;
import it.besmart.ocpp.wrappers.APIError;
import it.besmart.ocpp.wrappers.DiagnosticsWrapper;
import it.besmart.ocppLib.dto.ErrorOCPP;
import it.besmart.ocppLib.enumeration.CapabilityType;
import it.besmart.ocppLib.enumeration.ErrorCode;
import it.besmart.ocppLib.wrappers.Request.ChangeAvailabilityRequest;
import it.besmart.ocppLib.wrappers.Request.ConnectorRequest;
import it.besmart.ocppLib.wrappers.Request.DiagnosticsRequest;
import it.besmart.ocppLib.wrappers.Request.FirmwareRequest;
import it.besmart.ocppLib.wrappers.Request.ForcedAvailableStatusRequest;
import it.besmart.ocppLib.wrappers.Request.ResetRequest;
import it.besmart.ocppLib.wrappers.Request.WhitelistRequest;
import it.besmart.ocppLib.wrappers.Response.AckResponse;
import it.besmart.ocppLib.wrappers.Response.ErrorListResponse;
import it.besmart.specifications.enums.Operations;
import it.besmart.specifications.query.CustomSpecificationBuilder;


@RestController
@RequestMapping("api/manage")
public class ManagementController {

	private final Logger logger = LoggerFactory.getLogger(ManagementController.class);
	
	@Autowired 
	private ServerMap serverMap;
	
	@Autowired
	private IStationService stationService;
	
	@Autowired
	private IConnectorService connService;
	
	@Autowired
	private IErrorService errorService;

	@Autowired
	private IFirmwareService firmwareService;
	
	@Autowired
	private IDiagnosticsService diagnosticsService;
	
	@Autowired
	private ITxService txService;
	
	@Autowired
	private IWlService wlService;
	
	@Autowired
	private IStationCapabilityService stationCapabilityService;

	@Autowired
	private IModelService modelService;


	@PostMapping("/reset")
	public ResponseEntity<AckResponse> resetStation(HttpServletRequest request, 
			@Valid @RequestBody ResetRequest bodyRequest) {
		
		AckResponse response = new AckResponse(false, null);
		
		
		//Entities existence
		ChargingStation cs = stationService.findByEvseID(bodyRequest.getEvseId());
		if(cs==null ) 
			return APIError.response(ErrorMessageResponse.STATION_UNEXISTENT);
		
		logger.debug(String.format("Trying to reset station %s", cs.getEvseID()));
		
		//Connected station
		if( ! cs.isConnected() ) 
			return APIError.response(ErrorMessageResponse.STATION_UNAVAILABLE);

		//Station not in use
		if ( stationInUse(cs) ) 
			return APIError.response(ErrorMessageResponse.STATION_IN_USE);
		
		
		//All validated
		boolean result = serverMap.getServer(cs.getProtocol()).requestResetMsg(cs, bodyRequest.getResetType());
		
		if(result) {
			response.setResult(true);
			response.setMessage("Station is going to reset");
		}
		else {
			response.setMessage("Station rejected the request");
		}
			
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	
	@PostMapping("/simulateChangeStatus")
	public ResponseEntity<AckResponse> simulateChangeStatus(HttpServletRequest request, 
			@RequestBody ForcedAvailableStatusRequest bodyRequest) { //for ecpp
		
		AckResponse response = new AckResponse(false, null);
		
		ChargingStation cs = stationService.findByEvseID(bodyRequest.getEvseId());
		
		if(cs==null ) {
			response.setMessage("Station does not exist");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
				
		Connector conn = connService.findByStationAndRef(cs, bodyRequest.getConnectorId());
		
		connService.updateStatus(conn, ConnectorStatusComplete.AVAILABLE);
		
		response.setResult(true);
		response.setMessage("Status changed correctly");
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	@PostMapping("/updateMeter")
	public ResponseEntity<AckResponse> updateMeter(HttpServletRequest request, 
			@Valid @RequestBody ConnectorRequest bodyRequest) {
		
		AckResponse response = new AckResponse(false, null);
		
		//Entities existence
		ChargingStation cs = stationService.findByEvseID(bodyRequest.getEvseId());
		if(cs==null ) 
			return APIError.response(ErrorMessageResponse.STATION_UNEXISTENT);
		
		logger.debug(String.format("Trying to read meter data of station %s", cs.getEvseID()));
		
		//Connected station
		if( ! cs.isConnected() ) 
			return APIError.response(ErrorMessageResponse.STATION_UNAVAILABLE);

		if(connService.findByStationAndRef(cs, bodyRequest.getConnector())==null) {
			return APIError.response(ErrorMessageResponse.CONNECTOR_UNEXISTENT);
		}
		
		//All validated
		boolean result = serverMap.getServer(cs.getProtocol())
				.requestTrigger(cs, TriggerMessageRequestType.MeterValues, bodyRequest.getConnector());
		
		if(result) {
			response.setResult(true);
			response.setMessage("Station is updating meter");
		}
		else {
			response.setMessage("Station rejected the request");
		}
			
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping("/updateFirmware")
	public ResponseEntity<AckResponse> updateFirmware(HttpServletRequest request, 
			@RequestBody FirmwareRequest bodyRequest) {
		
		AckResponse response = new AckResponse(false, null);
		
		//Entities existence
		List<ChargingStation> list = new ArrayList<>();
		
		if(!StringUtils.isAllBlank(bodyRequest.getEvseId())) {
			ChargingStation cs = stationService.findByEvseID(bodyRequest.getEvseId());
			if(cs==null ) 
				return APIError.response(ErrorMessageResponse.STATION_UNEXISTENT);
			
			
			logger.debug(String.format("Trying to update firmware for station %s", cs.getEvseID()));
			
			//Check last version
			if(cs.getFirmware().equals(cs.getModel().getLastFirmware())) {
				String message = "Firmware already updated";
				logger.debug(message);
				response.setResult(true);
				response.setMessage(message);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			
			if (firmwareService.findPendingInstallation(cs)!=null) 
				return APIError.response("Firmware is already updating");
			
			//Connected station
			if( ! cs.isControllable() ) 
				return APIError.response(ErrorMessageResponse.STATION_UNAVAILABLE);
			
			
			//All validated
			boolean result = false;
			
			try {
				result = serverMap.getServer(cs.getProtocol()).requestUpdateFirmwareMsg(cs, bodyRequest.getExternalUri());
				
				if(result) {
					response.setResult(true);
					response.setMessage("Station is going to update firmware");
				}
				else {
					response.setMessage("Station rejected the request");
				}
			}
			catch(UnsupportedRequestException e) {
				response.setMessage(e.getLocalizedMessage());
			}
		}
		else {
			
			Model m = modelService.findByExternalCode(bodyRequest.getModelCode());
			
			if(m==null) {
				return APIError.response(ErrorMessageResponse.MODEL_UNEXISTENT);
			}
			CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
			builder.with("model", Operations.EQUAL, m);
			builder.with("operatorCode", Operations.EQUAL, bodyRequest.getOperatorCode());
			
			list = stationService.findBySpecification(builder.build());
			
			
			int stations = list.size();
			int toUpdate = 0;
			
			
			for(ChargingStation cs : list) {
				
				//Check last version
				if(cs.getFirmware().equals(cs.getModel().getLastFirmware())) {
					stations--;
					continue;
				}
				
				if (firmwareService.findPendingInstallation(cs)!=null) {
					stations--;
					continue;
				}
				
				//Connected station
				if( ! cs.isControllable() ) 
					continue;
				
				//All validated
				boolean result = false;
				
				try {
					result = serverMap.getServer(cs.getProtocol()).requestUpdateFirmwareMsg(cs, bodyRequest.getExternalUri());
					
					if(result) {
						toUpdate++;
					}
					else {
						response.setMessage("Station rejected the request");
					}
				}
				catch(UnsupportedRequestException e) {
					response.setMessage(e.getLocalizedMessage());
				}
				
			}
			
			if(toUpdate==0) {
				if(stations==0) {
					response.setResult(true);
					response.setMessage(String.format("All the stations are already updated to last firmware version"));
				}
				else {
					response.setResult(false);
					response.setMessage(String.format("All the stations (%d) rejected the request of firmware update", 
							stations));
				}
			}
			else {
				response.setResult(true);
				response.setMessage(String.format("%d stations (out of %d) are going to update their firmware", 
						toUpdate, stations));
			}
			
		}
		
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/getDiagnostics")
	public ResponseEntity<AckResponse> getDiagnostics(HttpServletRequest request, 
			@Valid @RequestBody DiagnosticsRequest bodyRequest) {
		
		AckResponse response = new AckResponse(false, null);
		
		//Entities existence
		ChargingStation cs = stationService.findByEvseID(bodyRequest.getEvseId());
		if(cs==null ) 
			return APIError.response(ErrorMessageResponse.STATION_UNEXISTENT);
		
		
		logger.debug(String.format("Trying to download diagnostics of station %s", cs.getEvseID()));
		
		//Connected station
		if( ! cs.isControllable() ) 
			return APIError.response(ErrorMessageResponse.STATION_UNAVAILABLE);
		
		if (diagnosticsService.findPendingDownload(cs)!=null) 
			return APIError.response("Diagnostics is already downloading");
		
		ZonedDateTime startTime = ZonedDateTime.now().minusDays(1);
		int hours = 24;
		
		if(bodyRequest.getStartTime()!=null) {
			
			DateTimeFormatter dtf = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
			
			try {
				startTime = ZonedDateTime.parse(bodyRequest.getStartTime(), dtf);
			}
			catch(DateTimeParseException e) {
				
				response.setMessage("Correct startTime format is yyyy-mm-ddThh:mm:ssZ");
				return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
			}
			hours = bodyRequest.getHours();
		}
		
		//All validated 
		DiagnosticsWrapper res = serverMap.getServer(cs.getProtocol()).requestDiagnosticsMsg(cs, startTime, hours, bodyRequest.getDownloadUri());
		
		if(res==null || !res.isResult()) {
			response.setMessage("Station rejected the request");
		}
		else {
			
			
			if(bodyRequest.getDownloadUri()==null ) { //else directly uploaded to client server
				
				DiagnosticsRecordStatus record = diagnosticsService.findById(res.getId());
				record.setEmail(bodyRequest.getEmail()); 
				diagnosticsService.updateDiagnosticsRecord(record);
				
			}
			
			
			response.setResult(true);
			response.setMessage("Last diagnostics is downloading..");
			
		}			
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	
	@PostMapping("/changeConnectorState")
	public ResponseEntity<AckResponse> changeConnectorState(HttpServletRequest request, 
			@Valid @RequestBody ChangeAvailabilityRequest bodyRequest) {
		
		AckResponse response = new AckResponse(false, null);
		
		//Entities existence / validation 
		ChargingStation cs = stationService.findByEvseID(bodyRequest.getEvseId());
		if(cs==null ) 
			return APIError.response(ErrorMessageResponse.STATION_UNEXISTENT);
		
		logger.debug(String.format("Asked station %s (connector %d) to change status", 
				 bodyRequest.getEvseId(), bodyRequest.getConnector()));

		
		//Connected station
		if( ! cs.isConnected() ) 
			return APIError.response(ErrorMessageResponse.STATION_UNAVAILABLE);
		
		
		Connector conn = connService.findByStationAndRef(cs, bodyRequest.getConnector());
		
		//connector not in use
		if ( connInUse(conn) ) 
			return APIError.response(ErrorMessageResponse.CONNECTOR_IN_USE);

		boolean active = bodyRequest.getStatus() == ChangeAvailabilityRequest.AvailabilityType.AVAILABLE ? true : false;
		
		//All validated
		boolean result = serverMap.getServer(cs.getProtocol())
							.requestChangeAvailabilityMsg(cs, active, bodyRequest.getConnector());
		
		if(result) {
			response.setResult(true);
			response.setMessage("Station is changing availability");
		}
		else {
			response.setMessage("Station rejected the request");
		}
			
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	
	private boolean connInUse(Connector conn) {
		
		return txService.findOngoingTransaction(conn)!=null;
	}

	
	@PostMapping("/pullStationErrors")
	public ResponseEntity<ErrorListResponse> pullStationErrors(HttpServletRequest request) {
		
		ErrorListResponse response = new ErrorListResponse();
		
		
		Set <ChargingStation> entities = new HashSet<>();
		
		entities.addAll(stationService.findAll());
		
//		logger.debug(String.format("User %s asked active errors on his stations", 
//				user.getName()));

		List<Integer> ids = new ArrayList<>();
		List<ErrorOCPP> list = new ArrayList<>();
		for(ChargingStation cs : entities) {
			for(StoredLog error : errorService.findErrors(cs) ) {
				ErrorOCPP e = new ErrorOCPP();
				e.setMessage(error.getMessage());
				e.setCode(ErrorCode.fromValue(error.getType().toString()));
				e.setTimestamp(error.getTimestamp());
				e.setId(error.getLogID());
				e.setEvseId(cs.getEvseID());
				e.setTxSession(error.getSession());
				
				ids.add((int) error.getLogID());
				if(error.getConnector()!=null)
					e.setConnectorId(error.getConnector().getRef());
				
				list.add(e);
			}
		}
		long count = ids.stream().distinct().count();
		//logger.debug(String.format("Values: %d, Distinct: %d", ids.size(), count));
		if(ids.size()!=count)
			logger.warn(ids.toString());
		
		response.setResult(true);
		response.setErrors(list);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping("/solveStationErrors")
	public ResponseEntity<AckResponse> solveStationErrors(HttpServletRequest request, @RequestBody List<Long> errors) {
		
		AckResponse response = new AckResponse(false, null);

		errorService.solveErrors(errors);
		
		response.setResult(true);
		response.setMessage("Errors correctly solved");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}


	@PostMapping("/updateWhitelist")
	public ResponseEntity<AckResponse> updateLocalList(HttpServletRequest request, 
			@RequestBody @Valid WhitelistRequest bodyRequest) {
		
		AckResponse res = new AckResponse(false, null);
		
	
		ChargingStation entity = stationService.findByEvseID(bodyRequest.getEvseID());
		
		if (entity == null) 
			return APIError.response(ErrorMessageResponse.STATION_UNEXISTENT);
		
		StationCapability c = stationCapabilityService.findCapability(entity, CapabilityType.WhiteList);
		if (c==null || !c.getValue().isEnabled())
			return APIError.response("WhiteList not supported");
		//TODO: Check list limit
		
		if (! entity.isConnected()) 
			return APIError.response(ErrorMessageResponse.STATION_UNAVAILABLE);
		
		
		//Local list is resetted for this actor
		Set<WlCard> partialList = wlService.cleanLocalList(entity);
		for(String idTag : bodyRequest.getCardIDs()) {
			WlCard customer = wlService.findByIdTag(idTag);
			if (customer==null) 
				customer = wlService.addRFID(idTag);
			
			partialList.add(customer);
		}
		entity.setLocalList(partialList);
		stationService.updateStation(entity);
	
	
		boolean result = serverMap.getServer(entity.getProtocol()).sendLocalList(entity);
		
		if(result) {
			res.setResult(true);
			res.setMessage("Whitelist was succesfully updated");
		}
		else
			res.setMessage("Station rejected the request");
		
		return new ResponseEntity<>(res, HttpStatus.OK);
	}
	
	
	@PostMapping("/unlockSocket")
	public ResponseEntity<AckResponse> unblockSocket(HttpServletRequest request, 
			@Valid @RequestBody ConnectorRequest bodyRequest) {
		
		AckResponse response = new AckResponse(false, null);
		
		//Station validation 
		ChargingStation station = stationService.findByEvseID(bodyRequest.getEvseId());
		if( station==null ) 
			return APIError.response(ErrorMessageResponse.STATION_UNEXISTENT);
		
		
		logger.debug( String.format("Richiesta di sbloccare la presa %d della stazione %s", 
				bodyRequest.getConnector(), bodyRequest.getEvseId()));
	
		
		//Connector validation
		Connector conn = connService.findByStationAndRef(station, bodyRequest.getConnector() );
		if (conn == null ) 
			return APIError.response(ErrorMessageResponse.CONNECTOR_UNEXISTENT);
		
		//Connected station
		if( ! station.isConnected() ) 
			return APIError.response(ErrorMessageResponse.STATION_UNAVAILABLE);
	
		
		//All validated: request of unlock
		boolean result = false;
		Transaction tx = txService.findOngoingTransaction(conn.getUnit());
		if(tx!=null) {
			AuthorizationResponse authResponse = serverMap.getServer(station.getProtocol())
					.requestRemoteStop(station, (int) tx.getTxID() );
			
			result = authResponse.equals(AuthorizationResponse.ACCEPTED);
		}
		else {
			result = serverMap.getServer(station.getProtocol()).requestUnlockConnectorMsg(station, bodyRequest.getConnector());
		}
		
		if(result) {
			response.setResult(true);
			response.setMessage("Connector was succesfully unblocked");
		}
		else {
			response.setMessage("Station rejected the request");
		}
			
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}


	/*** PRIVATE METHODS ***/
	
	
	private boolean stationInUse(ChargingStation cs) {
		
		if(!cs.isConnected() )
			return false;
		
		return ! txService.findOngoingTransactions(cs).isEmpty();
		
	}
	
}
