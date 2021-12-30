package it.besmart.ocpp.services.interfaces;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import eu.chargetime.ocpp.model.core.KeyValueType;
import it.besmart.ocpp.dtos.ConfigurationParamDTO;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.ConfigurationParam;

public interface IConfigParamService {
	
	public ConfigurationParam findByDTO(ConfigurationParamDTO p);
	
	public ConfigurationParam findById(long id);
	
	public ConfigurationParam findParameterInStation(String name, ChargingStation cs);
	
	public List<ConfigurationParam> findByStation(ChargingStation cs, boolean onlyVisible);
	
	
	public void deleteParam(ConfigurationParam param);
	
	public void readImplicitParam(ChargingStation cs, KeyValueType kv);
	
	public void writeImplicitParam(ChargingStation station);
	
	public ConfigurationParam addOrUpdateParam(ConfigurationParam p);

	public List<ConfigurationParam> findBySpec(Specification<ConfigurationParam> spec);

	
}
