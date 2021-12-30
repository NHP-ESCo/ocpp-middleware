package it.besmart.ocpp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import it.besmart.ocpp.model.StoredLog;

public interface StoredLogRepository extends JpaRepository<StoredLog, Long>, JpaSpecificationExecutor<StoredLog> {
	
	
}
