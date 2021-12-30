package it.besmart.ocpp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import it.besmart.ocpp.model.ChargingUnit;
import it.besmart.ocpp.model.RecordSCLimit;
import it.besmart.ocpp.model.Transaction;

public interface RecordSCLimitRepository extends JpaRepository<RecordSCLimit, Long>{

	RecordSCLimit findFirstByUnitAndTransactionAndAcceptedCommandIsTrueOrderByTimestampDesc(ChargingUnit cu, Transaction tx);

}
