package it.besmart.ocpp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.ChargingUnit;

public interface CURepository extends JpaRepository<ChargingUnit, Long>, JpaSpecificationExecutor<ChargingUnit> {

	public ChargingUnit findByChargingStationAndRef(ChargingStation cs, int ref);

	public ChargingUnit findByEvseCode(String evse);
	
	
	public List<ChargingUnit> findByChargingStation(ChargingStation cs);
	
}
