package it.besmart.ocpp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import it.besmart.ocpp.enums.MeasurandType;
import it.besmart.ocpp.model.ChargingUnit;
import it.besmart.ocpp.model.MeterRecord;
import it.besmart.ocpp.model.Transaction;

public interface MeterRecordRepository extends JpaRepository<MeterRecord, Long>, JpaSpecificationExecutor<MeterRecord> {
	
	MeterRecord findFirstByTxAndMeasurandAndSentOrderBySendTimeDesc(Transaction tx, MeasurandType measurand, boolean sent);
}
