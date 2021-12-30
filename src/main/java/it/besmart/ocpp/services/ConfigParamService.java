package it.besmart.ocpp.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.chargetime.ocpp.model.core.KeyValueType;
import it.besmart.ocpp.dtos.ConfigurationParamDTO;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.ChargingUnit;
import it.besmart.ocpp.model.ConfigurationParam;
import it.besmart.ocpp.model.Model;
import it.besmart.ocpp.model.ModelCapability;
import it.besmart.ocpp.model.ParamType;
import it.besmart.ocpp.model.StationCapability;
import it.besmart.ocpp.repositories.ConfigurationParamRepository;
import it.besmart.ocpp.services.interfaces.ICUService;
import it.besmart.ocpp.services.interfaces.IConfigParamService;
import it.besmart.ocpp.services.interfaces.IModelCapabilityService;
import it.besmart.ocpp.services.interfaces.IParameterSelectService;
import it.besmart.ocpp.services.interfaces.IParameterTypeService;
import it.besmart.ocpp.services.interfaces.IStationCapabilityService;
import it.besmart.ocppLib.dto.Capability;
import it.besmart.ocppLib.enumeration.CapabilityStatus;
import it.besmart.ocppLib.enumeration.CapabilityType;
import it.besmart.ocppLib.enumeration.ProtocolVersion;

@Service
@Transactional
public class ConfigParamService implements IConfigParamService {
	
	private final Logger logger = LoggerFactory.getLogger(ConfigParamService.class);
	
	@Autowired 
	private ConfigurationParamRepository repo;
	
	@Autowired
	private ICUService cuService;
	
	@Autowired
	private IStationCapabilityService capabilityService;
	
	@Autowired
	private IModelCapabilityService modelCapabilityService;
	
	@Autowired
	private IParameterTypeService paramTypeService;
	
	@Autowired
	private IParameterSelectService paramSelectService;
	
	@Override
	public ConfigurationParam findByDTO(ConfigurationParamDTO p) {
		
		return findById(p.getParamId());
	}

	@Override
	public ConfigurationParam findById(long id) {
		return repo.getOne(id);
	}

	@Override
	public ConfigurationParam findParameterInStation(String name, ChargingStation cs) {
//		logger.debug(name);
		
//		if (name.charAt(0)=='_')
//			return repo.findByStationAndBasicParam_Name(cs, name);
	
		//TODO: and ProtocolParam_Protocol (cs.getProtocol())
		ConfigurationParam param = repo.findByStationAndProtocolParam_Name(cs, name);
			
		if (param == null) 
			return repo.findByStationAndModelParam_Name(cs, name);
		else 
			return param;
	}

	
	@Override
	public List<ConfigurationParam> findByStation(ChargingStation cs, boolean onlyVisible) {
		
		List<ConfigurationParam> params = repo.findByStation(cs);
		
		Collections.sort(params);
		
		if(!onlyVisible)
			return params;
		else { //only visible
			List<ConfigurationParam> visibleParams = new ArrayList<ConfigurationParam>();
			for(ConfigurationParam p : params) {
				if( p.getParam().isVisible())
					visibleParams.add(p);
			}
			return visibleParams;
		}
		
	}

	
	@Override
	public void deleteParam(ConfigurationParam param) {
		
		repo.delete(param);
		
	}


	@Override
	public ConfigurationParam addOrUpdateParam(ConfigurationParam p) {
		
		return  repo.save(p);
	}

	//TODO: different implementations of model services for read/write?
	
	@Override /** Read specific model parameters **/
	public void readImplicitParam(ChargingStation cs, KeyValueType kv) {
		
		Model model = cs.getModel();
		
		switch(model.getCompleteCode()) {
		
		/**Example: model specific params 
		case "BTI*059049": 
		case "BTI*059048":
			if(kv.getKey().contains("Supported")) {
				
				ParamType selectParam = null;
				switch(kv.getKey()) {
			
				case "SupportedMeterValuesAlignedData":
					selectParam = paramTypeService.findByNameInStation("MeterValuesAlignedData", cs);
					paramSelectService.saveOptions(selectParam, kv.getValue().replace(" ",  "").split(","), 
							cs.getModel());
					break;
				case "SupportedMeterValuesSampledData":
					selectParam = paramTypeService.findByNameInStation("MeterValuesSampledData", cs);
					paramSelectService.saveOptions(selectParam, kv.getValue().replace(" ",  "").split(","), 
							cs.getModel());
					break;
					
				case "SupportedStopTxnAlignedData":
					selectParam = paramTypeService.findByNameInStation("StopTxnAlignedData", cs);
					paramSelectService.saveOptions(selectParam, kv.getValue().replace(" ",  "").split(","), 
							cs.getModel());
					break;
				
				case "SupportedStopTxnSampledData":
					selectParam = paramTypeService.findByNameInStation("StopTxnSampledData", cs);
					paramSelectService.saveOptions(selectParam, kv.getValue().replace(" ",  "").split(","), 
							cs.getModel());
					break;
				
				}
				
			}
			break;
		
		case "DLT*10566": 
			if(kv.getKey().equals("PowerUnits/PowerMerging")) {
				
				StationCapability internalLoadBal = findOrSaveCapability(cs, CapabilityType.InternalLoadBalancing, false);
				
				internalLoadBal.setValue(kv.getValue().equals("None")?CapabilityStatus.Disabled:CapabilityStatus.Enabled);
				internalLoadBal = capabilityService.saveOrUpdateCapability(internalLoadBal);
			}
			**/
		}
		
		
	}
	
	
	@Override /** store model specific param **/
	public void writeImplicitParam(ChargingStation station) {
		
		String key;
		ConfigurationParam param;
		Model model = station.getModel();
		
		
		switch(model.getCompleteCode()) {
		/**EXAMPLE: for model specific param
			case "ABB*CDT_TACW22::NET_ETH":
				StationCapability sc = findOrSaveCapability(station, CapabilityType.SmartCharging, false);
				sc.setValue(CapabilityStatus.Enabled);
				capabilityService.saveOrUpdateCapability(sc);
				
				break;
			case "BTI*059049": 
			case "BTI*059048":
				
				key = "MeterType";
				param = findParameterInStation(key, station);
				
				if (param!=null && param.getValue()!=null) {
					Boolean physicalMeter = param.getValue().equals("External");
					
					StationCapability c = findOrSaveCapability(station, CapabilityType.PhysicalMeter, false);
					
					c.setValue(physicalMeter.booleanValue()?CapabilityStatus.Enabled:CapabilityStatus.Disabled);
				
					capabilityService.saveOrUpdateCapability(c);
				}
				
				
				key = "CurrentManagementLeft";
				param = findParameterInStation(key, station);
				
				if (param!=null) {
					Integer maxCurrent = (int) cuService.findByStationAndRef(station, 2).computeMaxCurrent();
					param.setValue( maxCurrent.toString());
					repo.save(param);
				}
				
			
			case "BTI*059004": 	//single side	
				key = "CurrentManagementRight";
				param = findParameterInStation(key, station);
				
				if (param!=null) {
					Integer maxCurrent = (int) cuService.findByStationAndRef(station, 1).computeMaxCurrent();
					param.setValue( maxCurrent.toString());
					repo.save(param);
				}
				
				
				key = "OCPPPlugAndCharge";
				param = findParameterInStation(key, station);
				
				if (param!=null) {
					Boolean freeCharging = param.getValue().equals("true");
					StationCapability c = findOrSaveCapability(station, CapabilityType.FreeCharging, true);
					
					c.setValue(freeCharging.booleanValue()?CapabilityStatus.Enabled:CapabilityStatus.Disabled);
					
					capabilityService.saveOrUpdateCapability(c);
				}
				
				break;
				
			
			case "ENS*EVF200": {
				key = "OperatorCurrentLimit";
				param = findParameterInStation(key, station);
				
				if (param!=null) {
					Integer maxCurrent = (int) cuService.findByStationAndRef(station, 1).computeMaxCurrent();
					param.setValue( maxCurrent.toString());
					repo.save(param);
				}
				
				key = "OperatorCurrentLimit_2";
				param = findParameterInStation(key, station);
				
				if (param!=null) {
					Integer maxCurrent = (int) cuService.findByStationAndRef(station, 2).computeMaxCurrent();
					param.setValue( maxCurrent.toString());
					repo.save(param);
				}
				break;
			}
			
			case "DLT*10566": {
				
				key = "Device/GridCurrent";
				param = findParameterInStation(key, station);
				
				if (param!=null) {
					double max = 0;
					
					for(ChargingUnit cu : cuService.findByStation(station)) {
						max = Math.max(max, cu.computeMaxCurrent());
					}
					Integer maxCurrent = (int) max;
					logger.debug("Set grid current to " + maxCurrent);
					param.setValue( maxCurrent.toString());
					repo.save(param);
				}
				
				break;
			}
			**/
		}
		
	}


	@Override
	public List<ConfigurationParam> findBySpec(Specification<ConfigurationParam> spec) {
		
		return repo.findAll(spec);
	}


	private StationCapability findOrSaveCapability(ChargingStation cs, CapabilityType cap, boolean editable) {
		
		Capability c = new Capability(cap);
		c.setEditable(editable); 
		
		ModelCapability modelCap = modelCapabilityService.findAndUpdateCapability(cs.getModel(), c);
		
		return capabilityService.findOrSaveByModelCapability(modelCap, cs);
	}

}

