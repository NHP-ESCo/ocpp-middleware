package it.besmart.ocpp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import it.besmart.ocpp.model.Authorization;
import it.besmart.ocpp.model.Connector;
import it.besmart.ocpp.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

	Transaction findByAuthorization_ExternalSession(String session);
	
	List<Transaction> findByConnector(Connector conn);

	List<Transaction> findByAuthorization_CsSession(String txId);

	Transaction findByAuthorization(Authorization auth);
	
	//TODO
	//@Query(value = "SELECT sum(MeterStop) FROM Transaction")
	//BigDecimal sumMeterStop();
}
