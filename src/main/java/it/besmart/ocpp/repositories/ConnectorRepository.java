package it.besmart.ocpp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.ChargingUnit;
import it.besmart.ocpp.model.Connector;

public interface ConnectorRepository extends JpaRepository<Connector, Long> {
	
	
	public List<Connector> findByUnit_ChargingStation(ChargingStation cs);
	
	public List<Connector> findByUnit(ChargingUnit cu);
	
	public Connector findByRefAndUnit_ChargingStation( int ref, ChargingStation cs);

	public Connector findByRefAndUnit( int ref, ChargingUnit cu);

}
