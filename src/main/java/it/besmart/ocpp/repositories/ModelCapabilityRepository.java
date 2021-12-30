package it.besmart.ocpp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import it.besmart.ocpp.model.ModelCapability;
import it.besmart.ocpp.model.ModelCapabilityKey;

public interface ModelCapabilityRepository extends JpaRepository<ModelCapability, ModelCapabilityKey>,
	JpaSpecificationExecutor<ModelCapability> {

	
	
}
