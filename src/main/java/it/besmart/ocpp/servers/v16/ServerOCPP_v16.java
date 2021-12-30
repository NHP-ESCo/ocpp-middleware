package it.besmart.ocpp.servers.v16;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import eu.chargetime.ocpp.model.core.AuthorizationStatus;
import eu.chargetime.ocpp.model.core.AvailabilityStatus;
import eu.chargetime.ocpp.model.core.AvailabilityType;
import eu.chargetime.ocpp.model.core.ChangeAvailabilityConfirmation;
import eu.chargetime.ocpp.model.core.ChangeAvailabilityRequest;
import eu.chargetime.ocpp.model.core.ChangeConfigurationConfirmation;
import eu.chargetime.ocpp.model.core.ChangeConfigurationRequest;
import eu.chargetime.ocpp.model.core.ChargingProfile;
import eu.chargetime.ocpp.model.core.ChargingProfileKindType;
import eu.chargetime.ocpp.model.core.ChargingProfilePurposeType;
import eu.chargetime.ocpp.model.core.ChargingRateUnitType;
import eu.chargetime.ocpp.model.core.ChargingSchedule;
import eu.chargetime.ocpp.model.core.ChargingSchedulePeriod;
import eu.chargetime.ocpp.model.core.ClearCacheConfirmation;
import eu.chargetime.ocpp.model.core.ClearCacheRequest;
import eu.chargetime.ocpp.model.core.ClearCacheStatus;
import eu.chargetime.ocpp.model.core.ConfigurationStatus;
import eu.chargetime.ocpp.model.core.GetConfigurationConfirmation;
import eu.chargetime.ocpp.model.core.GetConfigurationRequest;
import eu.chargetime.ocpp.model.core.IdTagInfo;
import eu.chargetime.ocpp.model.core.KeyValueType;
import eu.chargetime.ocpp.model.core.RemoteStartStopStatus;
import eu.chargetime.ocpp.model.core.RemoteStartTransactionConfirmation;
import eu.chargetime.ocpp.model.core.RemoteStartTransactionRequest;
import eu.chargetime.ocpp.model.core.RemoteStopTransactionConfirmation;
import eu.chargetime.ocpp.model.core.RemoteStopTransactionRequest;
import eu.chargetime.ocpp.model.core.ResetConfirmation;
import eu.chargetime.ocpp.model.core.ResetRequest;
import eu.chargetime.ocpp.model.core.ResetStatus;
import eu.chargetime.ocpp.model.core.UnlockConnectorConfirmation;
import eu.chargetime.ocpp.model.core.UnlockConnectorRequest;
import eu.chargetime.ocpp.model.core.UnlockStatus;
import eu.chargetime.ocpp.model.firmware.GetDiagnosticsConfirmation;
import eu.chargetime.ocpp.model.firmware.GetDiagnosticsRequest;
import eu.chargetime.ocpp.model.firmware.UpdateFirmwareConfirmation;
import eu.chargetime.ocpp.model.firmware.UpdateFirmwareRequest;
import eu.chargetime.ocpp.model.localauthlist.AuthorizationData;
import eu.chargetime.ocpp.model.localauthlist.GetLocalListVersionConfirmation;
import eu.chargetime.ocpp.model.localauthlist.GetLocalListVersionRequest;
import eu.chargetime.ocpp.model.localauthlist.SendLocalListConfirmation;
import eu.chargetime.ocpp.model.localauthlist.SendLocalListRequest;
import eu.chargetime.ocpp.model.localauthlist.UpdateStatus;
import eu.chargetime.ocpp.model.localauthlist.UpdateType;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageConfirmation;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequest;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequestType;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageStatus;
import eu.chargetime.ocpp.model.reservation.CancelReservationConfirmation;
import eu.chargetime.ocpp.model.reservation.CancelReservationRequest;
import eu.chargetime.ocpp.model.reservation.CancelReservationStatus;
import eu.chargetime.ocpp.model.reservation.ReservationStatus;
import eu.chargetime.ocpp.model.reservation.ReserveNowConfirmation;
import eu.chargetime.ocpp.model.reservation.ReserveNowRequest;
import eu.chargetime.ocpp.model.smartcharging.ChargingProfileStatus;
import eu.chargetime.ocpp.model.smartcharging.SetChargingProfileConfirmation;
import eu.chargetime.ocpp.model.smartcharging.SetChargingProfileRequest;
import it.besmart.ocpp.enums.AuthorizationResponse;
import it.besmart.ocpp.enums.ErrorType;
import it.besmart.ocpp.enums.ProtocolParam;
import it.besmart.ocpp.exceptions.ConfigurationException;
import it.besmart.ocpp.exceptions.UnsupportedRequestException;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.ChargingUnit;
import it.besmart.ocpp.model.ConfigurationParam;
import it.besmart.ocpp.model.Connector;
import it.besmart.ocpp.model.DiagnosticsRecordStatus;
import it.besmart.ocpp.model.WlCard;
import it.besmart.ocpp.model.ParamType;
import it.besmart.ocpp.model.Reservation;
import it.besmart.ocpp.model.StationCapability;
import it.besmart.ocpp.model.Transaction;
import it.besmart.ocpp.servers.ServerOC;
import it.besmart.ocpp.services.ErrorService;
import it.besmart.ocpp.services.interfaces.ICUService;
import it.besmart.ocpp.services.interfaces.IConnectorService;
import it.besmart.ocpp.services.interfaces.IDiagnosticsService;
import it.besmart.ocpp.services.interfaces.IErrorService;
import it.besmart.ocpp.services.interfaces.IWlService;
import it.besmart.ocpp.utils.FileTransferUtility;
import it.besmart.ocpp.utils.ParamUtils;
import it.besmart.ocpp.services.interfaces.IFirmwareService;
import it.besmart.ocpp.services.interfaces.IStationCapabilityService;
import it.besmart.ocpp.wrappers.AuthWrapper;
import it.besmart.ocpp.wrappers.DiagnosticsWrapper;
import it.besmart.ocppLib.enumeration.ConnectionPowerType;
import it.besmart.ocppLib.enumeration.ParameterClassType;
import it.besmart.ocppLib.dto.config.ParameterKey;
import it.besmart.ocppLib.enumeration.CapabilityStatus;
import it.besmart.ocppLib.enumeration.CapabilityType;
import it.besmart.ocppLib.wrappers.Request.ResetRequest.ResetType;

public class ServerOCPP_v16 extends ServerOC  {

	private final Logger logger = LoggerFactory.getLogger(ServerOCPP_v16.class);
	
	
	public static final int BOOT_RETRY_SEC = 15;

	
	@Autowired
	private IConnectorService connService;  // to reserve single connector
	
	@Autowired
	private ICUService cuService;
	
	@Autowired
	private IFirmwareService firmwareService;
	
	@Autowired
	private IDiagnosticsService diagnosticsService;
	
	@Autowired
	private IErrorService errorService;
	
	@Autowired
	private FileTransferUtility fileUtility;
	
	@Autowired
	private IStationCapabilityService capabilityService;
	
	@Autowired
	private IWlService customerService;
	
	private List<KeyValueType> requestConfigurationMsg(ChargingStation cs) {
		
		GetConfigurationRequest req = new GetConfigurationRequest();
		
		List<String> keysReq = requestedKeys(cs);
		if(!keysReq.isEmpty())
			req.setKey(keysReq.toArray(new String[0]));
		 
			
		GetConfigurationConfirmation conf = (GetConfigurationConfirmation) getConfirmation( cs, req );
		
		if (conf==null) {
			//Retry
			logger.debug("Request minimal parameters");
			
			List<String> keys = new ArrayList<>();
			for(ConfigurationParam par : paramService.findByStation(cs, false)) {
				if (par.getBasicParam()==null && !par.getParam().isAutoconfigured()) {
					keys.add(par.getParam().getName());
				}
					
			}
			req.setKey(keys.toArray(new String[0]));
			
			conf = (GetConfigurationConfirmation) getConfirmation( cs, req );
			if(conf!=null)
				return Arrays.asList(conf.getConfigurationKey());
			
			return null;
		}
		else {
			if(conf.getUnknownKey()!=null && conf.getUnknownKey().length>0) {
				logger.warn("Unrecognized parameters (" + conf.getUnknownKey().length + ")");
				for(String s : conf.getUnknownKey())
					logger.debug(s);
			}
			//TODO check change
			
			KeyValueType[] list = conf.getConfigurationKey();
			
			
			return Arrays.asList(list);
		}
	}
	
	private List<String> requestedKeys(ChargingStation cs) {
		List<String> keys = new ArrayList<>();
		
		String modelCode = cs.getModel().getCompleteCode();
		
		// some model do not send all the parameters
		switch(modelCode) {
	
				//TODO
		}

		return keys;
		
	}
	

	private ConfigurationStatus requestChangeConfigurationMsg(ChargingStation cs, String key, String value) {
		
		ChangeConfigurationRequest req = new ChangeConfigurationRequest(key,value);
		
		ChangeConfigurationConfirmation conf = (ChangeConfigurationConfirmation) getConfirmation(cs, req);
		
		if (conf==null) {
			logger.debug("No reply for change configuration");
			return ConfigurationStatus.Rejected;
		}
		else
			return conf.getStatus();
				
	}
	
	private void requestStartUpInfo(ChargingStation cs) {
		
		if (enabledProfile(cs, OcppProfile.TRIGGER)) {
			requestTrigger(cs, TriggerMessageRequestType.StatusNotification, 0);

			//TODO: scheduled service ??
			requestTrigger(cs, TriggerMessageRequestType.MeterValues, 0);
		}
		
		
	}
	
	
	//SmartCharging is added
	
	@Override
	public boolean setTxPower(Transaction tx, ChargingRateUnitType unit, double max) {
		
		Connector conn = tx.getConnector();
		double cuMax = 0;
		double cuMin = 0;
		
		
		switch(unit) {
		case A:
			cuMax = conn.computeMaxCurrent();
			cuMin = conn.computeMinCurrent();
		
			break;
		case W:
			cuMax = conn.getActualMaxPower()*1000; //transform in W
			cuMin = conn.getMinPower()*1000;
			max = max*1000; //transform in W
			
			break;
		default:
			return false;
		
		}
		
		if(max!=0) { //0 could be set for tx in standby
			if(max > cuMax) {
				logger.warn(String.format("Value %f is not in the interval %f - %f", max, cuMin, cuMax));
				max = cuMax;
			}
			if( max < cuMin) {
				logger.warn(String.format("Value %f is not in the interval %f - %f", max, cuMin, cuMax));
				max = cuMin;
			}
		}
		
		
		//One single period for the whole transaction, relative to start of charging
		ChargingSchedulePeriod[] schedulePeriods = new ChargingSchedulePeriod[1];
		
		schedulePeriods[0] = new ChargingSchedulePeriod(0, max); 
		if (conn.getUnit().getPowerType()==ConnectionPowerType.MONOPHASE)
			schedulePeriods[0].setNumberPhases(1);
		
		
		ChargingSchedule schedule = new ChargingSchedule(unit, schedulePeriods);
		
		
		ChargingProfile profile = new ChargingProfile(0,0, 
				ChargingProfilePurposeType.TxProfile, ChargingProfileKindType.Relative, schedule );
		profile.setTransactionId((int) tx.getTxID());
		
		
		ChargingStation cs = conn.getUnit().getChargingStation();
		SetChargingProfileRequest request = new SetChargingProfileRequest(conn.getRef(), profile);
//		logger.debug(request.toString());
		logger.debug(String.format("SC %s: Set %.2f %s on connector %d during tx %d" , 
				cs.getEvseID(), max, unit.name(), conn.getRef(), tx.getTxID()));
		
		SetChargingProfileConfirmation conf = (SetChargingProfileConfirmation) getConfirmation(cs, request);
		
		if(conf!=null)
			logger.debug(conf.toString());
		
		ChargingUnit cu = conn.getUnit();
		
		if (conf==null || conf.getStatus()!=ChargingProfileStatus.Accepted) {
			errorService.saveError(String.format("Station unavailable for smart charging. Requested %.1f %s on CU %d", 
					max, unit.name(), cu.getRef()), 
					ErrorType.RECHARGE_ERROR, cu.getChargingStation(), null, tx.getExternalSession());		
			return false;
		}
		else {
			return true;
		}
	}
	
	
	@Override
	public boolean setUnitSCPower(ChargingUnit cu, ChargingRateUnitType unit, double max, double min) {
		
		boolean result = false;
		
		double cuMax = 0;
		double cuMin = 0;
		
		
		switch(unit) {
		case A:
			cuMax = cu.computeMaxCurrent();
			cuMin = cu.computeMinCurrent();
			break;
		case W:
			cuMax = cu.getMaxPower()*1000; //transform in W
			cuMin = cu.getMinPower()*1000;
			max = max*1000;
			min = min*1000;
			
			break;
		default:
			return false;
		
		}
		
		if(max > cuMax || max < cuMin) {
			logger.warn(String.format("Value %f is not in the interval %f - %f", max, cuMin, cuMax));
			return false; //Exception TODO
		
		}
		
		for (Connector conn : connService.findByUnit(cu)) {
			
			ChargingSchedulePeriod[] schedulePeriods = new ChargingSchedulePeriod[1];
			
			schedulePeriods[0] = new ChargingSchedulePeriod(0, Math.min(max, cuMax)); 
			if (cu.getPowerType()==ConnectionPowerType.MONOPHASE)
				schedulePeriods[0].setNumberPhases(1);
			
			ChargingSchedule schedule = new ChargingSchedule(unit, schedulePeriods);
			if(min> 0 && min < max && min > cuMin)
				schedule.setMinChargingRate(min);
			schedule.setStartSchedule(ZonedDateTime.now());
			
			ChargingProfile profile = new ChargingProfile(0,0, 
					ChargingProfilePurposeType.TxDefaultProfile, ChargingProfileKindType.Absolute, schedule );
			
			
			SetChargingProfileRequest request = new SetChargingProfileRequest(conn.getRef(), profile);
			logger.debug(request.toString());
			
			SetChargingProfileConfirmation conf = (SetChargingProfileConfirmation) getConfirmation(cu.getChargingStation(), request);
			
			if (conf==null || conf.getStatus()!=ChargingProfileStatus.Accepted) {
				result = false;
			}
			else {
				result = true;
				logger.debug(conf.toString());
			}
			
		}
		
		return result;
		
	}
	
	//TriggerMessage is added
	@Override
	public boolean requestTrigger(ChargingStation cs, TriggerMessageRequestType type, int connector) {
		
		if(cs.getModel().getBrand().getName().equals("ABB"))
			return true; //TODO manage not enabled profile with exception
		
		if(!enabledProfile(cs, OcppProfile.TRIGGER)) {
			logger.warn("Profile Trigger not enabled");
			return false;
		}
		
		
		if( type==TriggerMessageRequestType.MeterValues) {
			StationCapability c = capabilityService.findCapability(cs, CapabilityType.PhysicalMeter);
			
			if(c==null || !c.getValue().isEnabled()) {
				logger.warn("No Physical Meter present");
				return false;
			}
			
		}
				
			
		
		TriggerMessageRequest req = new TriggerMessageRequest(type);
		
		if (connector > 0)
			req.setConnectorId(connector);
		
		TriggerMessageConfirmation conf = (TriggerMessageConfirmation) getConfirmation(cs, req);
		
		if (conf==null ) {
			logger.debug("No reply to trigger request");
			return false;
		}
		else {
			logger.debug("Trigger confirmed with status: " + conf.getStatus());
			return conf.getStatus().equals(TriggerMessageStatus.Accepted);
		}
		
	}

	
	private ChargingStation updateCapabilityParameters(ChargingStation cs) throws ConfigurationException { 
		
		logger.debug("Check Reservation profile...");
		
		StationCapability reserve = capabilityService.findCapability(cs, CapabilityType.Reservable);
		
		if(reserve!=null ) {

			if(enabledProfile(cs, OcppProfile.RESERVATION)) {
				if(!reserve.getValue().isEnabled()) {
					reserve.setValue(CapabilityStatus.Enabled);
					reserve = capabilityService.saveOrUpdateCapability(reserve);
				}
			}
			else {
				if(reserve.getValue().isEnabled() ) {
					reserve.setValue(CapabilityStatus.Unsupported);
					reserve = capabilityService.saveOrUpdateCapability(reserve);
				}
			}
		}
		else {
			logger.warn(String.format("Capability %s not configured in station %s", CapabilityType.Reservable, cs.getEvseID()));
		}
		
		
		logger.debug("Check WhiteList enabled profile...");
		
		StationCapability whiteList = capabilityService.findCapability(cs, CapabilityType.WhiteList);
		
		if(whiteList!=null) {
			if(whiteList.getValue().isEnabled() && !enabledProfile(cs, OcppProfile.LOCAL_LIST)) {
				whiteList.setValue(CapabilityStatus.Unsupported);
				whiteList = capabilityService.saveOrUpdateCapability(whiteList);
			}
			
		}
		else {
			logger.warn(String.format("Capability %s not configured in station %s", CapabilityType.WhiteList, cs.getEvseID()));
		}
		
		logger.debug("Check Smart Charging profile...");
		
		StationCapability smartCharging = capabilityService.findCapability(cs, CapabilityType.SmartCharging);
		
		if(smartCharging!=null
				&& (smartCharging.getValue().equals(CapabilityStatus.Enabled)
					|| smartCharging.getValue().equals(CapabilityStatus.Unsupported)) ) { //retry
				
			//Set current limit if sc enabled TODO not for every model?
			for(ChargingUnit cu : cuService.findByStation(cs)) {
				double max = cu.computeMaxCurrent();
				
				if(!setUnitSCPower(cu, ChargingRateUnitType.A, max, 0)) {
					String message = "Configuration: Current limit cannot be applied on this station. "
							+ "Change configuration or install an updated firmware";
					errorService.saveError(message, 
							ErrorType.CONFIGURATION_ERROR, cs, null, null);
					
					smartCharging.setValue(CapabilityStatus.Unsupported);
					smartCharging = capabilityService.saveOrUpdateCapability(smartCharging);
					
					cs.setSmartCharging(CapabilityStatus.Unsupported);
					cs = stationService.updateStation(cs);
					
					throw new ConfigurationException(message);
				}
				else {
					smartCharging.setValue(CapabilityStatus.Enabled);
					smartCharging = capabilityService.saveOrUpdateCapability(smartCharging);
					
					cs.setSmartCharging(CapabilityStatus.Enabled);
					cs = stationService.updateStation(cs);
				}
			}
		}
		else {
			logger.warn(String.format("Capability %s not configured in station %s", CapabilityType.SmartCharging, cs.getEvseID()));
		}
		
		return cs;
		
	}


	@Override
	public boolean requestResetMsg(ChargingStation cs, ResetType type) {
		
		eu.chargetime.ocpp.model.core.ResetType reset;
		
		if (type==ResetType.Hard)
			reset = eu.chargetime.ocpp.model.core.ResetType.Hard;
		else
			reset = eu.chargetime.ocpp.model.core.ResetType.Soft;
		
		ResetRequest req = new ResetRequest(reset);
		
		ResetConfirmation conf = (ResetConfirmation) getConfirmation(cs, req);
	
		if (conf == null) 
			return false;
		
		logger.debug("Reply -> " + conf.getStatus());
		if ( conf.getStatus() == ResetStatus.Rejected ) 
			return false;
		else 
			return true;

	}
	
	@Override
	public AuthWrapper requestRemoteStart(ChargingStation cs, int connector, String idTag) {
		//TODO: set chargingProfile (if smart charging is set)* 
		
		RemoteStartTransactionRequest req = new RemoteStartTransactionRequest(idTag);
		req.setConnectorId(connector);
		
		RemoteStartTransactionConfirmation conf = (RemoteStartTransactionConfirmation) getConfirmation(cs, req);
		
		if (conf != null) 
			logger.debug("Reply -> " + conf.getStatus());
		
		if(conf == null || conf.getStatus() == RemoteStartStopStatus.Rejected) {
			AuthWrapper auth = new AuthWrapper(AuthorizationResponse.BLOCKED);
			
			return auth;
		}
		else {
			return new AuthWrapper(AuthorizationResponse.ACCEPTED);
			
		}
	}
	
	@Override
	public AuthorizationResponse requestRemoteStop(ChargingStation cs, long txID) {
		
		RemoteStopTransactionRequest req = new RemoteStopTransactionRequest((int)txID);
		
		RemoteStopTransactionConfirmation conf = (RemoteStopTransactionConfirmation) getConfirmation(cs, req);
	
		if (conf != null) 
			logger.debug("Reply -> " + conf.getStatus());
		
		if(conf == null || conf.getStatus() == RemoteStartStopStatus.Rejected) {
			return AuthorizationResponse.BLOCKED;
		}
		else {
			return AuthorizationResponse.ACCEPTED;
			
		}
	}

	@Override
	public boolean requestReserveNow(Reservation res) {
		
		//Select connector: from unit to single connector
		int connRef = res.getConnectorRef();
	
		if (connRef==0) {
			Connector conn = connService.findAvailableConnectorInUnit(res.getUnit());
			if(conn==null)
				return false;
			else
				connRef = conn.getRef();
		}	
		
		ReserveNowRequest req = new ReserveNowRequest(connRef, res.getExpiryDate(), 
				res.getIdTag(), Integer.valueOf( (int) res.getResID() ) );
			
		if(res.getParentIdTag()!=null)
			req.setParentIdTag(res.getParentIdTag());
		
		ReserveNowConfirmation conf = (ReserveNowConfirmation) getConfirmation(res.getUnit().getChargingStation(), req);
		
		
		if(conf == null || conf.getStatus() != ReservationStatus.Accepted) { 
			if (conf != null)  //TODO: deal with status 
				logger.warn("Reply -> " + conf.getStatus());
			return false;
		}
		else { 
			return true;
		}

	}

	@Override
	public boolean requestCancelReservation(Reservation res) {

		CancelReservationRequest req = new CancelReservationRequest( (int) res.getResID() );
		
		CancelReservationConfirmation conf = (CancelReservationConfirmation) getConfirmation(res.getUnit().getChargingStation(), req);
		
		if (conf != null) 
			logger.debug("Reply -> " + conf.getStatus());
		
		if(conf== null || conf.getStatus() == CancelReservationStatus.Rejected)
			return false;
		else
			return true;
		
	}
	
	
	@Override
	public boolean requestChangeAvailabilityMsg(ChargingStation cs, boolean active, int connector) {
		
		AvailabilityType type;
		
		if (active) 
			type = AvailabilityType.Operative;
		else
			type = AvailabilityType.Inoperative;
		
		ChangeAvailabilityRequest req = new ChangeAvailabilityRequest(connector, type);
		ChangeAvailabilityConfirmation conf = (ChangeAvailabilityConfirmation) getConfirmation(cs, req);
		
		
		if (conf != null) 
			logger.debug("Reply -> " + conf.getStatus());
			
		if(conf == null || conf.getStatus()== AvailabilityStatus.Rejected)
			return false;
		else 
			return true; //Accepted or scheduled
		
	}

	@Override
	public boolean requestUnlockConnectorMsg(ChargingStation cs, int connector) {

		UnlockConnectorRequest req = new UnlockConnectorRequest(connector);
		UnlockConnectorConfirmation conf = (UnlockConnectorConfirmation) getConfirmation(cs, req);
		
		if (conf != null) 
			logger.debug("Reply -> " + conf.getStatus());
		
		if (conf == null || conf.getStatus()!=UnlockStatus.Unlocked )
			return false;
		else //notSupported or Failed
			return true;
	}


	@Override
	public boolean requestClearAuthorizationCacheMsg(ChargingStation cs) {
		
		ClearCacheRequest req = new ClearCacheRequest();
				
		ClearCacheConfirmation conf = (ClearCacheConfirmation) getConfirmation(cs, req);
		
		if (conf!=null) {
			logger.debug("Reply -> " + conf.getStatus());
		}
		
		if (conf == null || conf.getStatus() == ClearCacheStatus.Rejected)
			return false;
		else 
			return true;
	}

	@Override
	public DiagnosticsWrapper requestDiagnosticsMsg(ChargingStation cs, ZonedDateTime startTime, int hours, String uri) {
		
		DiagnosticsWrapper wrapper = new DiagnosticsWrapper();
		
		if( ! enabledProfile(cs, OcppProfile.FIRMWARE)) {
			
			wrapper.setMessage("Diagnostics update not supported");
			return wrapper;
		}
		
		
		String localPath = "diagnostics/";
		
		//transfer protocol depending on model
		if(uri==null) {

			uri = fileUtility.getServerPath(cs.getModel().isSftp()) + localPath;			
		}
		
		GetDiagnosticsRequest req = new GetDiagnosticsRequest(uri);
		req.setRetries(1);
		req.setStartTime(startTime);
		req.setStopTime(startTime.plusHours(hours));
		
		GetDiagnosticsConfirmation conf = (GetDiagnosticsConfirmation) getConfirmation(cs, req);

		if (conf!=null) {
			logger.debug("File " + conf.getFileName() + " is uploading");
			
			wrapper.setResult(conf.getFileName()!=null);
			
			if(wrapper.isResult()) {
				DiagnosticsRecordStatus rec = diagnosticsService.addDiagnosticsRecord(cs, localPath + conf.getFileName(), cs.getModel().isSftp());
				wrapper.setId(rec.getId());
			}
			
			return wrapper;
		}
		else 
			return wrapper;
	}

	@Override
	public boolean requestUpdateFirmwareMsg(ChargingStation cs, String externalUri) 
			throws UnsupportedRequestException {
			
		if(! enabledProfile(cs, OcppProfile.FIRMWARE)) {
			String msg = "Firmware update not supported";
			logger.warn(msg);
			throw new UnsupportedRequestException(msg);
		}
		
		String uri = externalUri;
		
		if(StringUtils.isAllBlank(uri)) {
			//Default location in ftp server
			
//			s3Client.downloadFile(uri) //TODO download from s3, model folder
			
			String dir= "fw_updates/"+ cs.getModel().getCompleteCode().replaceAll("\\*", "");
			logger.debug("Download firmware from "+ dir);
			
			try {
				//TODO does not find files in folder
				uri = fileUtility.getServerFile(dir, false); //cs.getModel().isSftp());
				
			} catch (IOException e) {
				
				throw new UnsupportedRequestException(e.getLocalizedMessage());
				
			}
		}
		
		if(uri==null) {
			String msg = "Download failed";
			logger.warn(msg);
			throw new UnsupportedRequestException(msg);
		}
		
			
		UpdateFirmwareRequest req = new UpdateFirmwareRequest(uri, ZonedDateTime.now(ZoneOffset.UTC));
		//req.setRetries(3);
		
		UpdateFirmwareConfirmation conf = (UpdateFirmwareConfirmation) getConfirmation(cs, req);

		if (conf != null) {
			logger.debug("Reply -> " + conf.toString());
			
			firmwareService.addFirmwareRecord(cs);
			
			return true;
		}
		else 
			return false;
		
	}
	
	
	
	
	@Override
	public boolean sendLocalList(ChargingStation cs) {
		
		if( ! enabledProfile(cs, OcppProfile.LOCAL_LIST) 
				|| ! paramService.findParameterInStation(ProtocolParam.LocalAuthListEnabled.getValue(), cs).getValue().equals("true") )
			return false;
		
		int currentVersion = requestLocalListVersion(cs);
		if (currentVersion==-1)
			return false;
		else
			logger.debug("Current list version: " + currentVersion);
		
		int maxLength = Integer.valueOf(paramService.findParameterInStation(ProtocolParam.LocalAuthListMaxLength.getValue(), cs).getValue());
		logger.debug("Max length of list accepted: " + maxLength);
		
		
		List<WlCard> localList = customerService.getLocalList(cs);
		
		AuthorizationData[] localAuthorizationList = new AuthorizationData[localList.size()];
		
		int i=0;
		for(WlCard cust : localList) {
			
			if (!cust.isExpired()) {
				AuthorizationData data = new AuthorizationData(cust.getIdTag());
				
				data.setIdTagInfo(new IdTagInfo(AuthorizationStatus.Accepted));
				
				localAuthorizationList[i] = data;
				i++;
			}
			
		}
		
		//TODO: store version number for a differential update
		
		SendLocalListRequest req = new SendLocalListRequest(currentVersion+1, UpdateType.Full);
		req.setLocalAuthorizationList(localAuthorizationList);
		
		SendLocalListConfirmation conf = (SendLocalListConfirmation) getConfirmation(cs, req);
		
		if (conf!=null && conf.getStatus().equals(UpdateStatus.Accepted))
			return true;
		else {
			
			if(conf!=null)
				logger.warn("Reply -> " + conf.getStatus());
			return false;
		}
		
		
	}

	
	@Override
	public int requestLocalListVersion(ChargingStation cs) {

//		if(! enabledProfile(cs, OcppProfile.LOCAL_LIST)) {
//			return -1;
//		}
		
		GetLocalListVersionRequest req = new GetLocalListVersionRequest();
		
		GetLocalListVersionConfirmation conf = (GetLocalListVersionConfirmation) getConfirmation(cs, req);
		
		if (conf!=null)
			return conf.getListVersion();
		else 
			return -1;
		
	}

	
	/** PRIVATE METHODS **/
	
	public boolean enabledProfile(ChargingStation station, OcppProfile profile) {
		
		ConfigurationParam supportedProfiles = paramService.findParameterInStation(ProtocolParam.SupportedFeatureProfiles.getValue(), station);
		
		for (String p : supportedProfiles.getValue().replaceAll(" ", "").split(",")) {
			if(p.equals(profile.getValue()))
				return true;
		}
		
		return false;
		
	}

	@Override
	public ChargingStation updateStationConfiguration(ChargingStation cs) throws ConfigurationException {

		logger.debug("Configuration....");
		
		boolean reqReset = false;
		
		//EXPLICIT PARAMETER CONFIGURATION
		List<KeyValueType> parameters = requestConfigurationMsg(cs) ;

		if(parameters == null) {

			throw new ConfigurationException(lowNetworkMsg);
		}

		for ( KeyValueType kv : parameters ) {	
			
			ConfigurationParam param = paramService.findParameterInStation(kv.getKey(), cs);

			if(param==null) {
				logger.debug("Parametro da aggiungere:" + kv.toString());
				ParamType p = paramTypeService.findByNameInStation(kv.getKey(), cs);

				if (p == null) {
					logger.warn(String.format("Il parametro di modello %s viene salvato", kv.getKey()));
					ParameterKey modelParam = new ParameterKey();
					modelParam.setName(kv.getKey());
					modelParam.setAutoconfigured(true);
					modelParam.setVisible(false);
					modelParam.setEditable(!kv.getReadonly());
					modelParam.setType(ParameterClassType.Unknown);
					p = paramTypeService.saveModelParameter(modelParam, cs.getModel());

				}

				param = new ConfigurationParam(p);
				param.setValue(kv.getValue());
				param.setStation(cs);

				paramService.addOrUpdateParam(param);

			}
			else { //Già configurato
				// Confronto kv.getValue() e param.getValue()
				//logger.debug(kv.getKey());
				
//				if(param.getParam().isSelectable()) {
//					logger.debug(String.format("Parameter %s. Station value: %s, Server value: %s", 
//							kv.getKey(), kv.getValue(), param.getValue()));
//				}
				
				
				if ( kv.getValue()!=null && ! ParamUtils.equalParams(kv.getValue(), param.getValue()) ){ //conflitto tra i valori
					logger.debug(String.format("%s: %s -> %s", kv.getKey(), kv.getValue(), param.getValue()) );
					
					if ( kv.getReadonly() || param.getValue() == null || 
							param.getParam().isAutoconfigured() )
					{   //imposto dalla station se
						//non modificabile, oppure mai configurato, oppure parametro autoconfigurabile

						param.setValue(kv.getValue());
						paramService.addOrUpdateParam(param);
						logger.debug(String.format("%s fissato al valore della station: %s [readonly=%b, autoconfigured=%b]", 
								kv.getKey(), kv.getValue(), kv.getReadonly(), param.getParam().isAutoconfigured() ));
						
					}
					else { //imposto dal cpo/admin
						
						logger.debug(String.format("%s : %s -> %s", kv.getKey(), kv.getValue(), param.getValue() ));
						ConfigurationStatus status = requestChangeConfigurationMsg(cs, kv.getKey(), param.getValue());
						
						//Rejected change: TODO error not visible if parameter not visible
						
						switch(status) {
						case Accepted:
							if(param.isUnsupported()) {
								param.setUnsupported(false);
								paramService.addOrUpdateParam(param);
								errorService.solveStationErrors(cs, ErrorType.CONFIGURATION_ERROR, String.format("Parameter %s", kv.getKey()));
							}
							break;
						case NotSupported:
							param.setUnsupported(true);
							paramService.addOrUpdateParam(param);
							String message = String.format("Parameter %s was rejected because unsupported", kv.getKey());
							errorService.saveError(message, ErrorType.CONFIGURATION_ERROR, cs, null, null);
							break;
						case Rejected:
							message = String.format("Configuration: Change of parameter %s (%s -> %s) was %s", kv.getKey(),  kv.getValue(), 
									param.getValue(), status.name().toLowerCase());
							errorService.saveError(message, ErrorType.CONFIGURATION_ERROR, cs, null, null);

							throw new ConfigurationException(message);
							//break;
						case RebootRequired:
							reqReset = true;
							
							break;
						
						default:
							break;
						
						}
					
					}
					
					paramService.readImplicitParam(cs, kv);
				}
				
			}
		}
		
		
		cs = updateCapabilityParameters(cs);
		
		logger.debug("Configured " + cs.getEvseID());
		
		if(reqReset) {
			logger.warn("Reset is required");
			requestResetMsg(cs, ResetType.Soft);
		}
		
		return cs;
	}

	
	
	
}
