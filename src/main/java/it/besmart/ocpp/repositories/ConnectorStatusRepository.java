package it.besmart.ocpp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import it.besmart.ocpp.model.Connector;
import it.besmart.ocpp.model.RecordConnectorStatus;

public interface ConnectorStatusRepository extends JpaRepository<RecordConnectorStatus, Long>, JpaSpecificationExecutor<RecordConnectorStatus> {

	RecordConnectorStatus findFirstByConnectorOrderBySendTimeDesc(Connector conn);
	
}
