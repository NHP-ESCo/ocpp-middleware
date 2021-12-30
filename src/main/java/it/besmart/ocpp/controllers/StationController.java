package it.besmart.ocpp.controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.besmart.ocpp.enums.CSLifeStatus;
import it.besmart.ocpp.enums.ErrorMessageResponse;
import it.besmart.ocpp.exceptions.ConfigurationException;
import it.besmart.ocpp.exceptions.ModelException;
import it.besmart.ocpp.exceptions.ParameterException;
import it.besmart.ocpp.exceptions.StationException;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.ChargingUnit;
import it.besmart.ocpp.model.ConfigurationParam;
import it.besmart.ocpp.model.Connector;
import it.besmart.ocpp.model.MeterRecord;
import it.besmart.ocpp.model.Model;
import it.besmart.ocpp.model.ModelCapability;
import it.besmart.ocpp.model.ModelConnector;
import it.besmart.ocpp.model.ModelUnit;
import it.besmart.ocpp.model.ParamSelectValue;
import it.besmart.ocpp.model.ParamType;
import it.besmart.ocpp.model.StationCapability;
import it.besmart.ocpp.servers.ServerMap;
import it.besmart.ocpp.services.interfaces.ICUService;
import it.besmart.ocpp.services.interfaces.IConfigParamService;
import it.besmart.ocpp.services.interfaces.IConnectorService;
import it.besmart.ocpp.services.interfaces.IMeterRecordService;
import it.besmart.ocpp.services.interfaces.IModelCapabilityService;
import it.besmart.ocpp.services.interfaces.IModelService;
import it.besmart.ocpp.services.interfaces.IParameterSelectService;
import it.besmart.ocpp.services.interfaces.IParameterTypeService;
import it.besmart.ocpp.services.interfaces.IStationCapabilityService;
import it.besmart.ocpp.services.interfaces.IStationService;
import it.besmart.ocpp.services.interfaces.ITxService;
import it.besmart.ocpp.utils.ParamUtils;
import it.besmart.ocpp.wrappers.APIError;
import it.besmart.ocppLib.dto.Capability;
import it.besmart.ocppLib.dto.Parameter;
import it.besmart.ocppLib.dto.ParameterOption;
import it.besmart.ocppLib.dto.StationConnector;
import it.besmart.ocppLib.dto.StationData;
import it.besmart.ocppLib.dto.StationSettings;
import it.besmart.ocppLib.dto.StationStates;
import it.besmart.ocppLib.dto.StationUnit;
import it.besmart.ocppLib.dto.UnitCompleteStatus;
import it.besmart.ocppLib.enumeration.CapabilityStatus;
import it.besmart.ocppLib.enumeration.CapabilityType;
import it.besmart.ocppLib.enumeration.ConnectionPowerType;
import it.besmart.ocppLib.enumeration.ConnectorStatus;
import it.besmart.ocppLib.enumeration.ParameterClassType;
import it.besmart.ocppLib.enumeration.ProtocolVersion;
import it.besmart.ocppLib.enumeration.UnitStatus;
import it.besmart.ocppLib.wrappers.Request.SettingsInfoRequest;
import it.besmart.ocppLib.wrappers.Request.StationRequest;
import it.besmart.ocppLib.wrappers.Response.AckResponse;
import it.besmart.ocppLib.wrappers.Response.SaveStationResponse;
import it.besmart.ocppLib.wrappers.Response.StationDataListResponse;
import it.besmart.ocppLib.wrappers.Response.StationDataResponse;
import it.besmart.ocppLib.wrappers.Response.StationEnergyResponse;
import it.besmart.ocppLib.wrappers.Response.StationSettingsResponse;
import it.besmart.ocppLib.wrappers.Response.StationStatesResponse;


@RestController
@RequestMapping("api/station")
public class StationController {

	private final Logger logger = LoggerFactory.getLogger(StationController.class);
	
	@Autowired 
	private ServerMap serverMap;
	
	@Autowired
	private IMeterRecordService meterService;
	
	@Autowired
	private IStationService stationService;
	
	@Autowired
	private IConnectorService connService;
	
	@Autowired
	private ITxService txService;
	
	@Autowired
	private IModelCapabilityService modelCapabilityService;

	@Autowired
	private IModelService modelService;

	@Autowired
	private IParameterSelectService paramSelectService;

	@Autowired
	private IConfigParamService paramService;
	
	@Autowired
	private IParameterTypeService paramTypeService;

	@Autowired
	private IStationCapabilityService capabilityService;

	@Autowired
	private ICUService cuService;

	
	@PostMapping("/saveStation")
	public ResponseEntity<SaveStationResponse> saveNewStation(HttpServletRequest request, 
			@RequestBody @Valid StationData station ) {
		
		SaveStationResponse response = new SaveStationResponse();
		response.setResult(false);
		
		
		//Authorization
		
		logger.debug(String.format("Trying to create a new station"));
		
		ChargingStation entity = null;
		
		try {   
			entity = stationService.createStation(station);
		} catch (StationException | ParameterException e) {
			logger.warn(e.getMessage());
			response.setMessage(e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		logger.debug(String.format("Succesfully created the new station %s", entity.getEvseID()));
		
		
		response.setResult(true);
		response.setMessage("Station was succesfully saved");
		response.setEvseId(entity.getEvseID());
		return new ResponseEntity<>(response, HttpStatus.OK);
	
	}
	
	@PostMapping("/deleteStation")
	public ResponseEntity<AckResponse> deleteStation(HttpServletRequest request) {
		
		AckResponse res = new AckResponse(false, null);
		
		
		String evseID = request.getParameter("evseID");
		
		ChargingStation entity = stationService.findByEvseID(evseID);
		
		if (entity == null) {
			res.setMessage(ErrorMessageResponse.STATION_UNEXISTENT.getValue());
			return new ResponseEntity<>(res, HttpStatus.OK);
		}
		stationService.delete(entity);
		
		res.setResult(true);
		res.setMessage(String.format("Deleted station %s", evseID));
		
		return new ResponseEntity<>(res, HttpStatus.OK);
	}
	
	@PostMapping("/dismissStation")
	public ResponseEntity<AckResponse> dismissStation(HttpServletRequest request,
			@Valid @RequestBody StationRequest bodyRequest) {
		
		AckResponse response = new AckResponse(false, null);
		
		//Station validation
		ChargingStation cs = stationService.findByEvseID(bodyRequest.getEvseId());
		
		if (cs==null) 
			return APIError.response(ErrorMessageResponse.STATION_UNEXISTENT);
		

		logger.debug(String.format("Dismissing station %s", bodyRequest.getEvseId()));
		
		
		if (cs.isConnected()) {
			
			//Station not in use
			if ( stationInUse(cs) ) 
				return APIError.response(ErrorMessageResponse.STATION_IN_USE);
			
			boolean result = serverMap.getServer(cs.getProtocol()).requestChangeAvailabilityMsg(cs, false, 0);
			
			if(!result) {
				logger.warn("Station did not change status");
			}
		}
	
		stationService.dismissStation(cs);
		
		response.setResult(true);
		response.setMessage("Station was succesfully dismissed.");
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	
	private StationSettings getSettings(Model model,
				ConnectionPowerType powerType, ProtocolVersion protocol) {
			
		StationSettings settings = new StationSettings();
		
		if(!model.isPowerTypeEnabled(powerType))
			throw new ModelException(String.format("%s is not enabled on this model", powerType.getValue()));
		
		if(!model.hasProtocol(protocol))
			throw new ModelException(String.format("Protocol %s is not enabled on this model", protocol.getValue()));
		
		settings.setStationType(model.getType());
		settings.setPowerType(powerType);
		
		for(ModelCapability c : modelCapabilityService.findAllCapabilities(model)) {
			if(c.isEditable()) {
				settings.addModelCapability(c.getCapability());
			}
		}
		settings.setEnabledCapabilities(new ArrayList<>());
		
		
		for (ModelUnit mu : model.getUnits()) {
			StationUnit unit = new StationUnit();
			unit.setRef(mu.getRef());
			
			for (ModelConnector modelConn : mu.getConnectors() ) {
				StationConnector conn = new StationConnector();
				conn.setPlugType(modelConn.getPlug());
				conn.setRef(modelConn.getRef());
				conn.setMaxPower(modelConn.computeMaxPower(powerType));
				conn.setMinPower(modelConn.computeMinPower(powerType));
				unit.addConnector(conn);
			}

			settings.addUnit(unit);
				
		}
		
		
		List<ParamType> parameters = new ArrayList<>();
		
		parameters.addAll(paramTypeService.findByProtocol(protocol));
		parameters.addAll(paramTypeService.findByModel(model));
		
		for (ParamType param :  parameters) {
			if (param.isVisible()) {
				Parameter p = new Parameter();
				p.setName(param.getName());
				p.setType(param.getType());
				p.setEditable(param.isEditable());
				p.setValue(param.getDefaultValue());
				p.setDescription(param.getDescription());
				switch(p.getType()) {
				case Integer:
					p.setMinValue(param.getMinValue());
					p.setMaxValue(param.getMaxValue());
					break;
				case MultiSelect:
				case Select:
					List<ParameterOption> values = new ArrayList<>();
					for(ParamSelectValue s : paramSelectService.findByParamAndModel(param, model)) {
						ParameterOption o = new ParameterOption(s.getValue());
						if(s.getOptionName()!=null)
							o.setOptionName(s.getOptionName());
						
						values.add(o);
					}
					p.setSelectValues(values);
					break;
				default:
					break;
				
				}
				
				
				settings.addParameter(p);
			}
		}
		

		return settings;
	}

	@PostMapping("/pullSettingsInfo")
	public ResponseEntity<StationSettingsResponse> getSettingsInfo( HttpServletRequest request, 
			@RequestBody @Valid SettingsInfoRequest bodyRequest) {
		
		StationSettingsResponse response = new StationSettingsResponse();
		response.setResult(false);
		
		Model entity = modelService.findByExternalCode(bodyRequest.getModelCode());
		
		if(entity == null) {
			response.setMessage(String.format("Model %s does not exist", bodyRequest.getModelCode()));
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		
		StationSettings settings = getSettings(entity, bodyRequest.getPowerType(), bodyRequest.getProtocol());
		
		response.setData( settings );
		
		response.setResult(true);
		response.setMessage("Retrieved settings data");
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/updateStation") //configuration units/parameters
	public ResponseEntity<AckResponse> updateStation(HttpServletRequest request, 
			@RequestBody @Valid StationSettings bodyRequest) {
		
		AckResponse response = new AckResponse(false, null);
		
		//Station validation for operator
		ChargingStation csEntity = stationService.findByEvseID(bodyRequest.getEvseID());
		
		if(csEntity == null ) 
			return APIError.response(ErrorMessageResponse.STATION_UNEXISTENT);
		
		
		logger.debug(String.format("Starting station %s update", bodyRequest.getEvseID()));
		
		boolean previousSc = csEntity.isScEnabled();
		
		try {
			csEntity = stationService.updateStation(bodyRequest);
		} catch (StationException | ParameterException e) {
			return APIError.response(e.getMessage());
		}
		
		logger.debug(String.format("Updated station %s", 
				 bodyRequest.getEvseID()));
		
		boolean changedSc = previousSc != csEntity.isScEnabled();
		logger.debug("SmartCharging changed: " + changedSc);
		
		if(bodyRequest.getCus()==null && bodyRequest.getParameters()==null
				&& !changedSc) {
			
			//to not send anything to station
			response.setResult(true);
			response.setMessage("Station was correctly updated");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		
		
		boolean result = true;
		boolean firstConfig = csEntity.getLifeStatus().equals(CSLifeStatus.INSTALLED) ||
					csEntity.getLifeStatus().equals(CSLifeStatus.FIRST_CONFIGURATION);
		
		if ( csEntity.isConnected() ) {
			//Station not in use
			if ( !stationInUse(csEntity) ) {
				try { 
					serverMap.getServer(csEntity.getProtocol()).configureStation(csEntity, firstConfig, false, false);
					response.setMessage("Station has been configured");
				}
				catch (ConfigurationException e) {
					if(!firstConfig)
						stationService.updateLifeStatus(csEntity.getCSId(), CSLifeStatus.TO_CONFIGURE);
					result = false;
					response.setMessage(e.getMessage());
				}
			}
			else {
				csEntity.setConfigureAtStopTx(true);
				stationService.updateStation(csEntity);
				response.setMessage("Station will be updated when transactions end");
				
			}
			
		}
		else {//If not connected, configure at reconnection
			if(!firstConfig) {
				response.setMessage("Station will be updated at reconnection");
				stationService.updateLifeStatus(csEntity.getCSId(), CSLifeStatus.TO_CONFIGURE);
			}
			else
				response.setMessage("Station was correctly updated");
		}
	
		response.setResult(result);
		logger.debug(response.getMessage());
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	
	}

	private StationStates getStates(ChargingStation entity) {
		StationStates states = new StationStates();
		states.setLifeStatus( entity.getLifeStatus().simpleStatus() );
		states.setConnected(entity.isConnected());
		states.setConfigured(entity.getLifeStatus().equals(CSLifeStatus.ACTIVE));
		states.setMaxPower(entity.getMaxPower());
		states.setMinPower(entity.getMinPower());
		
		for(StationCapability c : capabilityService.findAllCapabilities(entity)) {
			Capability cap = new Capability();
			cap.setType(c.getCapability().getCapability());
			cap.setStatus(c.getValue());
			cap.setEditable(c.getCapability().isEditable());
			
			states.addCapabilityState(cap);
		}
	
		states.setAddressIP(entity.getAddressIP());
		states.setCommissioningDate(entity.getCommissioningDate());
		states.setFirmware(entity.getFirmware());
		states.setLastFirmwareVersion(entity.getModel().getLastFirmware());
		
		if (entity.isControllable()) {
			
			for (ChargingUnit unit: cuService.findByStation(entity)) {
				
				states.addUnitState( cuService.getUnitStatus(unit) );
			}
		}
		else {
	
			for (ChargingUnit unit: cuService.findByStation(entity)) {
				UnitCompleteStatus status = new UnitCompleteStatus();
				status.setEvseId(unit.getEvseCode());
				status.setStatus(UnitStatus.OUTOFSERVICE);
				
				for (Connector conn : connService.findByUnit(unit)) {
					status.addConnectorState(conn.getRef(), ConnectorStatus.OUTOFSERVICE);
				}
				
				states.addUnitState(status);
			}	
		}
		return states;
	}

	private StationData getStation(ChargingStation entity, boolean detailed, boolean params) {
			
			StationData station = new StationData();
			
			station.setOperatorCode(entity.getOperatorCode());
			station.setProtocol(entity.getProtocol());
			station.setSerialNumber(entity.getSerialNumber());
			station.setIdentifier(entity.getIdentifier());
			
			station.setStates(getStates(entity));
			
			
			Model model = entity.getModel();
			station.setModelCode(model.getExternalCode());
			
			StationSettings settings = new StationSettings();
			settings.setEvseID(entity.getEvseID());
			settings.setStationType(model.getType());
			settings.setPowerType(entity.getPowerType());
			
			
			if (detailed) {
				
				settings.setEnabledCapabilities(new ArrayList<>());
				
				for(ModelCapability c : modelCapabilityService.findAllCapabilities(model)) {
					if(c.isEditable()) {
						settings.addModelCapability(c.getCapability());
						
						StationCapability cap = capabilityService.findCapability(entity, c.getCapability());
						if(cap!=null && cap.getValue().equals(CapabilityStatus.Enabled))
							settings.addEnabledCapability(c.getCapability());
					}
				}
				
				
				for(ChargingUnit cu : cuService.findByStation(entity)) {
					StationUnit unit = new StationUnit();
					unit.setRef(cu.getRef());
					unit.setMaxPower(cu.getMaxPower());
					unit.setMinPower(cu.getMinPower());
					for(Connector conn : cu.getConnectors()) {
						StationConnector connector = new StationConnector();
						connector.setRef(conn.getRef());
						connector.setPlugType(conn.getPlug().getPlug());
						connector.setActualMaxPower(conn.getActualMaxPower());
						connector.setActualMinPower(conn.getActualMinPower());
						
						ModelConnector modelConn = conn.getPlug();
						connector.setMaxPower(modelConn.computeMaxPower(cu.getPowerType()));
						connector.setMinPower(modelConn.computeMinPower(cu.getPowerType()));
						unit.addConnector(connector);
					}
					settings.addUnit(unit);
				}
			}
			
			if(params) {
				for(ConfigurationParam param : paramService.findByStation(entity, true) ) {  //only visible
					Parameter parameter = new Parameter();
					parameter.setName(param.getParam().getName());
					parameter.setEditable(param.getParam().isEditable());
					parameter.setType(param.getParam().getType());
					parameter.setValue( param.getValue());
					parameter.setDescription(param.getParam().getDescription());
					
					ParamType type = param.getParam();
					
					if(type.getType().equals(ParameterClassType.Integer)) {
						parameter.setMinValue(type.getMinValue());
						parameter.setMaxValue(type.getMaxValue());
					}
					else if(type.isSelectable()) {
						List<ParameterOption> values = new ArrayList<>();
						for(ParamSelectValue s : paramSelectService.findByParamAndModel(type, entity.getModel())) {
							ParameterOption o = new ParameterOption(s.getValue());
							if(s.getOptionName()!=null)
								o.setOptionName(s.getOptionName());
							
							values.add(o);
						}
						parameter.setSelectValues(values);
						if(parameter.getValue()==null)
							parameter.setValue("");
	//					logger.debug(parameter.toString());
					}
					else if(type.getType().equals(ParameterClassType.Boolean)) {
						if(ParamUtils.equalParams(param.getValue(), "true"))
							parameter.setValue("true");
						else
							parameter.setValue("false");
					}
					
					settings.addParameter(parameter);
				}
			}
			
			station.setSettings(settings);
			
			return station;
		}

	@PostMapping("/pullStationData")
	public ResponseEntity<StationDataResponse> getStation(HttpServletRequest request, 
			@RequestBody @Valid StationRequest bodyRequest) {
		
		
		StationDataResponse response = new StationDataResponse();
		response.setResult(false);
		
		ChargingStation entity = stationService.findByEvseID(bodyRequest.getEvseId());
		
		if(	entity==null ) {
			response.setMessage(ErrorMessageResponse.STATION_UNEXISTENT.getValue());
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		
		
		StationData station = getStation(entity, true, true);
		
		response.setResult(true);
		response.setMessage("Retrieved data from station");
		response.setStationData( station );
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/pullStationEnergy")
	public ResponseEntity<StationEnergyResponse> getStationEnergy(HttpServletRequest request, 
			@RequestBody @Valid StationRequest bodyRequest) {
		
		logger.debug(String.format("Asked ENERGY data of station %s", bodyRequest.getEvseId()));
		
		StationEnergyResponse response = new StationEnergyResponse();
		response.setResult(false);
		
		ChargingStation entity = stationService.findByEvseID(bodyRequest.getEvseId());
		
		if(	entity==null ) {
			response.setMessage(ErrorMessageResponse.STATION_UNEXISTENT.getValue());
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		
		
		response.setRechargeEnergy(stationService.computeDeliveredEnergy(entity));
		
		StationCapability c = capabilityService.findCapability(entity, CapabilityType.PhysicalMeter);
		
		if(c!=null && c.getValue().isEnabled()) {
			
			Double totalEnergy = 0.0;
			for(ChargingUnit cu : entity.getCUs()) {
				Double energyCU = 0.0;
				MeterRecord lastRecord = meterService.findLastEnergyMeter(cu);
				if (lastRecord!=null)
					energyCU = lastRecord.getValue();
				logger.debug(String.format("CU %d : %.3f kWh", cu.getRef(), energyCU));
				totalEnergy += energyCU;
			}
			response.setTotalEnergy(totalEnergy);
			
		}
		
		response.setResult(true);
		response.setMessage("Retrieved data from station");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/getStationList") 
	public ResponseEntity<StationDataListResponse> getStationList(HttpServletRequest request) {
		
		logger.debug(String.format("Richiesti dati sulle stazioni"));
		
		StationDataListResponse response = new StationDataListResponse();
		response.setResult(false);
		
		Set <ChargingStation> entities = new HashSet<>();
		
		entities.addAll(stationService.findAll());
		
		 
		for(ChargingStation p : entities ) {
			
			response.addStationData( getStation(p, true, false) );
		}
	
		response.setResult(true);
		response.setMessage("Retrieved data from stations");
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/pullStationStates")
	public ResponseEntity<StationStatesResponse> getStationStates(HttpServletRequest request, 
			@RequestBody @Valid StationRequest bodyRequest) {
		
		
		StationStatesResponse response = new StationStatesResponse();
		response.setResult(false);
		
		ChargingStation entity = stationService.findByEvseID(bodyRequest.getEvseId());
		
		if(	entity==null ) {
			response.setMessage(ErrorMessageResponse.STATION_UNEXISTENT.getValue());
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		
		
		response.setResult(true);
		response.setMessage("Retrieved states from station");
		
	
		response.setStationStates(getStates(entity));
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/pullStates")
	public ResponseEntity<List<StationStatesResponse>> getAllStates(HttpServletRequest request) {
		
		List<StationStatesResponse> response = new ArrayList<>();
		
		for(ChargingStation entity : stationService.findAll()) {
			StationStatesResponse res = new StationStatesResponse();
			
			res.setStationStates(getStates(entity));
			
			response.add(res);
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
