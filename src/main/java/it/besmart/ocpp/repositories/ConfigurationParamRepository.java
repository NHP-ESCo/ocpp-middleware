package it.besmart.ocpp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.ConfigurationParam;

public interface ConfigurationParamRepository extends JpaRepository<ConfigurationParam, Long>, JpaSpecificationExecutor<ConfigurationParam> {
	
	public List<ConfigurationParam> findByStation(ChargingStation cs);

	public ConfigurationParam findByStationAndProtocolParam_Name(ChargingStation cs, String name);
	
	public ConfigurationParam findByStationAndModelParam_Name(ChargingStation cs, String name);
	
	public ConfigurationParam findByStationAndBasicParam_Name(ChargingStation cs, String name);
}
