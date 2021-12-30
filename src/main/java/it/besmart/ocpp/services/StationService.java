package it.besmart.ocpp.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.besmart.ocpp.dtos.RecordStationStatusDTO;
import it.besmart.ocpp.enums.CSLifeStatus;
import it.besmart.ocpp.enums.ConnectorStatusComplete;
import it.besmart.ocpp.enums.StationStatusComplete;
import it.besmart.ocpp.exceptions.ParameterException;
import it.besmart.ocpp.exceptions.StationException;
import it.besmart.ocpp.enums.ProtocolParam;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.ChargingUnit;
import it.besmart.ocpp.model.ConfigurationParam;
import it.besmart.ocpp.model.Connector;
import it.besmart.ocpp.model.MeterRecord;
import it.besmart.ocpp.model.Model;
import it.besmart.ocpp.model.ModelCapability;
import it.besmart.ocpp.model.ModelUnit;
import it.besmart.ocpp.model.ParamSelectValue;
import it.besmart.ocpp.model.ParamType;
import it.besmart.ocpp.model.StationCapability;
import it.besmart.ocpp.model.Transaction;
import it.besmart.ocpp.repositories.StationRepository;
import it.besmart.ocpp.services.interfaces.ICUService;
import it.besmart.ocpp.services.interfaces.IConnectorService;
import it.besmart.ocpp.services.interfaces.IMeterRecordService;
import it.besmart.ocpp.services.interfaces.IModelCapabilityService;
import it.besmart.ocpp.services.interfaces.IModelService;
import it.besmart.ocpp.services.interfaces.IParameterSelectService;
import it.besmart.ocpp.services.interfaces.IParameterTypeService;
import it.besmart.ocpp.services.interfaces.IStationCapabilityService;
import it.besmart.ocpp.services.interfaces.IConfigParamService;
import it.besmart.ocpp.services.interfaces.IStationService;
import it.besmart.ocpp.services.interfaces.ITxService;
import it.besmart.ocpp.utils.ParamUtils;
import it.besmart.ocppLib.dto.Parameter;
import it.besmart.ocppLib.dto.StationData;
import it.besmart.ocppLib.dto.StationSettings;
import it.besmart.ocppLib.dto.StationUnit;
import it.besmart.ocppLib.enumeration.CapabilityStatus;
import it.besmart.ocppLib.enumeration.CapabilityType;
import it.besmart.specifications.enums.Operations;
import it.besmart.specifications.query.CustomSpecificationBuilder;

@Service
@Transactional
public class StationService implements IStationService {
	
	private final Logger logger = LoggerFactory.getLogger(StationService.class);
	
	@Autowired
	private StationRepository repo;
	
	@Autowired
	private IModelService modelService;
	
	@Autowired
	private IParameterTypeService paramTypeService;
	
	@Autowired
	private IParameterSelectService paramSelectService;
	
	@Autowired
	private IModelCapabilityService modelCapabilityService;
	
	@Autowired
	private IStationCapabilityService stationCapabilityService;
	
	@Autowired
	private ICUService cuService;
	
	@Autowired
	private IConnectorService connService;
	
	@Autowired
	private IConfigParamService paramService;
	
	@Autowired
	private ITxService txService;
	
	@Autowired
	private IMeterRecordService meterService;

	
	@Override
	public ChargingStation createStation(StationData cs) 
			throws StationException, ParameterException {  
		
		ChargingStation station = new ChargingStation();
		
		station.setSerialNumber(cs.getSerialNumber());
		station.setOperatorCode(cs.getOperatorCode());
		station.setIdentifier(cs.getIdentifier()); //TODO create it here
		station.createEvseID(); //operatorCode*E-identifier
		station.setName(cs.getSettings().getName());
		
		// Unique Evse for non-dismissed station 
		if(	findByEvseID(station.getEvseID()) != null)
			throw new StationException( String.format("An active station with this identifier %s is already registered", 
					station.getIdentifier()) );
		
		//Set Model
		Model model = modelService.findByExternalCode(cs.getModelCode());
		if (model == null)
			throw new StationException(String.format("Model %s does not exist", cs.getModelCode()) );
		
		station.setModel(model);
		
		//VALIDATION w.r.t. Model
		
		if( ! model.hasProtocol(cs.getProtocol())) 
			throw new StationException("Invalid protocol for the selected model");
		station.setProtocol(cs.getProtocol());
		
		StationSettings sett = cs.getSettings();
		
		if( ! model.isPowerTypeEnabled( sett.getPowerType() ) ) 
			throw new StationException("Invalid power type connection for the selected model");
		station.setPowerType( sett.getPowerType() );
		station.setSmartCharging(CapabilityStatus.Disabled);
		
		//SAVE Station
		station = repo.save(station);
		logger.debug(String.format("Salvataggio stazione con modello: %s, %s", model.getName(), model.getBrand().getName()));
		
		/** CAPABILITIES **/
		station = updateStationCapabilities(station, sett.getEnabledCapabilities());
		
	
		/** PARAMETERS **/
		//Create parameters and set values of explicit parameters
		List<ParamType> parameters = new ArrayList<ParamType>();
		parameters.addAll( paramTypeService.findByProtocol( cs.getProtocol()) );
		parameters.addAll( paramTypeService.findByModel(model) );
//		parameters.addAll( paramTypeService.findBasicParams() );
		
		for(ParamType param : parameters  ) {
			
			ConfigurationParam paramEntity = new ConfigurationParam(param);
			paramEntity.setStation(station);
			
			if (param.isVisible() && param.isEditable() ) {
				Parameter paramDTO = sett.getParam(param.getName());
				if(paramDTO!=null && ! paramDTO.getValue().isEmpty()) {
					logger.debug(paramDTO.toString());
					List<String> acceptedValues = new ArrayList<>();
					if(param.isSelectable()) {
						for(ParamSelectValue v : paramSelectService.findByParamAndModel(param, model)) {
							acceptedValues.add(v.getValue());
						}
						if(param.getName().equals(ProtocolParam.MeterValuesSampledData.getValue()) 
								&& !acceptedValues.contains("Energy.Active.Import.Register")) {
							acceptedValues.add("Energy.Active.Import.Register");
						}
					}
					paramEntity.setValidatedValue( paramDTO.getValue(), acceptedValues );
				}
			}
			if (paramEntity.getValue()==null && param.getDefaultValue() != null)
				paramEntity.setValue(param.getDefaultValue());
			
			paramService.addOrUpdateParam(paramEntity);
		}
		
		//Create parameters and set values of IMPLICIT parameters 
		paramService.writeImplicitParam(station);
		
		/** POWER CHECK **/
		//UNITS: Create the whole structure via model
		
		station = updateStationPower(station, sett, true);
		
		return station;
	}
	
	
	//TODO: protocol update -> reconfigure parameters and reset		
	
	@Override
	public ChargingStation updateStation(StationSettings cs) 
			throws StationException, ParameterException {

		ChargingStation station = findByEvseID(cs.getEvseID());
		Model model = station.getModel();
		
		station.setName(cs.getName());
		
		/** CAPABILITIES **/
		station = updateStationCapabilities(station, cs.getEnabledCapabilities());
		
		
		/** OTHER PARAMETERS **/
		//Update parameters: TODO difference with admin

		for (Parameter paramDTO : cs.getParameters()) {
			
			ConfigurationParam paramEntity = paramService.findParameterInStation(paramDTO.getName(), station);
			if( paramEntity == null)
				throw new ParameterException(String.format("Parameter %s does not exist", paramDTO.getName()));
			
			//Here
			ParamType param = paramEntity.getParam();
			if (param.isVisible() && param.isEditable() ) {
				
				if(!ParamUtils.equalParams(paramDTO.getValue(), paramEntity.getValue())) { //changed value
					
					List<String> acceptedValues = new ArrayList<>();
					if(param.isSelectable()) {
						for(ParamSelectValue v : paramSelectService.findByParamAndModel(param, model)) {
							acceptedValues.add(v.getValue());
						}
						if(param.getName().equals(ProtocolParam.MeterValuesSampledData.getValue()) 
								&& !acceptedValues.contains("Energy.Active.Import.Register")) {
							acceptedValues.add("Energy.Active.Import.Register");
						}
					}
	//				logger.debug(String.format("Set %s (%s) : %s", param.getName(), param.getType().toString(), paramDTO.getValue()));
					paramEntity.setValidatedValue( paramDTO.getValue(), acceptedValues );
				}
				
			}
			else {
				throw new ParameterException(String.format("Parameter %s is not editable", paramDTO.getName()));
			}
			
			paramService.addOrUpdateParam(paramEntity);

		}
		
		//Set values of IMPLICIT parameters
		paramService.writeImplicitParam(station);
		
		/** POWER CHECK **/
		//VALIDATION w.r.t. Model
		
		if (cs.getPowerType() != null ) {
			if( ! model.isPowerTypeEnabled( cs.getPowerType() ) ) 
				throw new StationException("Invalid power type connection for the selected model");
			station.setPowerType( cs.getPowerType() );
		}
				
		station = updateStationPower(station, cs, false);
		
		station = repo.save(station);
		
		return station;
	}

	private ChargingStation updateStationCapabilities(ChargingStation station, List<CapabilityType> list) {
		
		for(ModelCapability m : modelCapabilityService.findAllCapabilities(station.getModel())) {
			logger.debug(String.format("Check model capability %s", m.getKey().getCapability()));
			
			StationCapability cap = stationCapabilityService.findOrSaveByModelCapability(m, station);
			
			if(list!=null) {
				if(list.contains(m.getCapability())) {
					//Enabled capability 
					
					if(!m.isEditable())
						throw new StationException(String.format("Capability %s is not configurable for this model",
								m.getCapability().name()));
					
					cap.setValue(CapabilityStatus.Enabled);
				}
				else {
					if(m.isEditable())
						cap.setValue(CapabilityStatus.Disabled);
					
				}
				
				cap = stationCapabilityService.saveOrUpdateCapability(cap);
				
				/** Treat Smart Charging explicitly **/
				if(cap.getCapability().getCapability().equals(CapabilityType.SmartCharging)) {
					station.setSmartCharging(cap.getValue());
					
					station = repo.save(station);
				}
			}
			
		}
		
		//TODO conflict between smart charging and internal load balancing?
		
		return station;
	}


	@Override
	public ChargingStation updateStationPower(ChargingStation station, StationSettings cs, boolean newStation) {
		Model model = station.getModel();
		double modelMaxPower = model.computeMaxPower( station.getPowerType() );
		
		double unitsMaxPower = 0;
		double unitsMinPower = 0;
		for(ModelUnit mu : model.getUnits()) {
			
			StationUnit cuDTO = cs.getCU( mu.getRef() );
			
			ChargingUnit cuEntity = null;
			
			if(newStation) {
				if(cuDTO == null ) 
					throw new StationException( String.format("Charging Unit %d has to be defined", mu.getRef()));
				
				cuEntity = cuService.saveCU(cuDTO, station, mu);
				logger.debug("Saved " + cuEntity.toString());
			}
			else {
				cuEntity = cuService.findByStationAndRef(station, mu.getRef());
				if(cuDTO != null)
					cuEntity = cuService.updateCU(cuEntity, cuDTO);
			}
			
			StationCapability c = stationCapabilityService.findCapability(station, CapabilityType.InternalLoadBalancing);
			if(c!=null && c.getValue().isEnabled()) {
				//max of station is highest among maxima
				unitsMaxPower = Math.max(cuEntity.getMaxPower(), unitsMaxPower);
				unitsMinPower = Math.max(cuEntity.getMinPower(), unitsMinPower);
			}
			else {
				unitsMaxPower+= cuEntity.getMaxPower();
				unitsMinPower += cuEntity.getMinPower();
			}
			
			
		}
		
		if( unitsMaxPower > modelMaxPower)
			throw new StationException( String.format("Power of CUs exceed available power for this model of %.1f kW", 
					modelMaxPower));
		
		station.setMaxPower(unitsMaxPower);
		station.setMinPower(unitsMinPower);
		
		return repo.save(station);
	}

	@Override
	public List<ChargingStation> findAll() {
		
		return repo.findAll();
	}


	@Override
	public ChargingStation updateStatus(ChargingStation cs, RecordStationStatusDTO status) {
		
		if(status!=null)
			cs.setStatus(status.getStatus());
		cs.setLastUpdate(status.getSendTime());
		
//		if(status.getStatus() != StationStatusComplete.AVAILABLE) {
//			for (Connector conn : connService.findByStation(cs)) {
//				connService.updateStatus(conn, ConnectorStatusComplete.fromValue(status.getStatus().getValue()));
//			}
//		}
		
		return repo.save(cs);
	}

	private ChargingStation findById(long id) {
		Optional<ChargingStation> o = repo.findById(id);
        if(o.isPresent())
            return o.get();
        else
        	return null;
	}

	@Override
	public ChargingStation updateLifeStatus(long csID, CSLifeStatus status) {
		
		ChargingStation entity = findById(csID);
		
		logger.debug(String.format("Station %s status: %s", entity.getEvseID(), status.getValue()));
		
		entity.setLifeStatus(status);
		
		return repo.saveAndFlush(entity);
		
	}


	@Override
	public ChargingStation connectStation(ChargingStation cs, String session, String addressIP) {
		
		cs.setActualSession(session);
		cs.setAddressIP(addressIP);
		cs.setStatus(StationStatusComplete.AVAILABLE);
		
		return repo.saveAndFlush(cs);
		
	}

	

	@Override
	public ChargingStation disconnectStation(ChargingStation station, boolean restartedApp) {
		
		station.setActualSession(null);
		station.setAddressIP(null);
		if(!restartedApp)
			station.setStatus(StationStatusComplete.UNAVAILABLE);

		return repo.save(station);
	}
	

	@Override
	public ChargingStation findBySession(UUID sessionIndex) {
		
		return repo.findByActualSession(sessionIndex.toString());
	}
	


	@Override
	public ChargingStation findByEvseID(String evseStation) {
		//Unique id
		return repo.findByEvseID(evseStation);
		
	}


	@Override
	public void delete(ChargingStation entity) {

//		for (Authorization auth: authService.findByStation(entity) ) {
//			authService.delete(auth);
//		}
//		
//		statusService.deleteStationStates(entity);
//		
		repo.delete(entity);
		
	}


	
	@Override
	public List<ChargingStation> findConnectedStations() {
		
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
		builder.with("actualSession", Operations.NOT_NULL, null);
		builder.with("lifeStatus", Operations.EQUAL, CSLifeStatus.ACTIVE);
		
		Specification<ChargingStation> spec = builder.build();
		
		
		return repo.findAll(spec);
	}
	
	
	@Override
	public double computeDeliveredEnergy(ChargingStation station) {

		double deliveredEnergy = 0;
		for (Connector conn : connService.findByStation(station)) {
			
			deliveredEnergy += computeDeliveredEnergy(conn);
		}
		return deliveredEnergy;
	}

	
	
	private Double computeDeliveredEnergy(Connector connector) {
		//TODO: SUM query
		
		Double deliveredEnergy = (double) 0;
		for (Transaction tx : txService.findPastTransactions(connector)) {
			
			if (tx.getMeterStop()!=null)
				deliveredEnergy += tx.getMeterStop() - tx.getMeterStart();
		}
		
		//Add ongoing tx
		Transaction tx = txService.findOngoingTransaction(connector);
		if (tx!=null) {
			MeterRecord lastRecord = meterService.findLastEnergyMeter(tx, false);
			if ( lastRecord != null ) {
				deliveredEnergy += lastRecord.getValue() - tx.getMeterStart();
			}
		}
		return deliveredEnergy;
	}



	@Override
	public ChargingStation updateStation(ChargingStation cs) {
		logger.debug(String.format("Station %s status: %s firmware: %s", 
				cs.getEvseID(), cs.getLifeStatus().getValue(), cs.getFirmware()));
		
		return repo.saveAndFlush(cs);
	}


	@Override
	public List<ChargingStation> findBySpecification(Specification<ChargingStation> spec) {
		
		return repo.findAll(spec);
	}


	@Override
	public ChargingStation dismissStation(ChargingStation cs) {
		if(cs!=null) {
			logger.debug(String.format("Station %s status: %s", cs.getEvseID(), CSLifeStatus.DISMISSED.getValue()));
			
			cs.setLifeStatus(CSLifeStatus.DISMISSED);
			cs.setStatus(StationStatusComplete.UNAVAILABLE);
			cs.setActualSession(null);
			cs.setAddressIP(null);
			
			for(Connector conn : connService.findByStation(cs)) {
				connService.updateStatus(conn, ConnectorStatusComplete.UNAVAILABLE);
			}
			
			return repo.save(cs);
		}
		return null;
	}


	@Override
	public ChargingStation findBySN(String evse) {
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
		builder.with("serialNumber", Operations.LIKE, evse);
		
		logger.debug(String.format("Find SN %s among stations", evse));
		Optional <ChargingStation> o = repo.findOne(builder.build());
		
		return o.isEmpty() ? null : o.get();
	}

}
