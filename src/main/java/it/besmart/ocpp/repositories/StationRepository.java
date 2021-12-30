package it.besmart.ocpp.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import it.besmart.ocpp.enums.StationStatusComplete;
import it.besmart.ocpp.model.Brand;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.Model;

public interface StationRepository extends JpaRepository<ChargingStation, Long>, JpaSpecificationExecutor<ChargingStation> {
	
	public ChargingStation findByEvseID(String s);
	public ChargingStation findByActualSession(String session);


	public List<ChargingStation> findByModel(Model m);
	public List<ChargingStation> findByModel_Brand(Brand b);
	
	public List<ChargingStation> findByStatus(StationStatusComplete s);
	
}
