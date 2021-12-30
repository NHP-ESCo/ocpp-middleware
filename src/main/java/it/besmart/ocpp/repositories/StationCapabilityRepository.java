package it.besmart.ocpp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import it.besmart.ocpp.model.StationCapability;

public interface StationCapabilityRepository extends JpaRepository<StationCapability, Long>,
	JpaSpecificationExecutor<StationCapability> {

	
	
}
