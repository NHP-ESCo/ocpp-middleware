package it.besmart.ocpp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import it.besmart.ocpp.model.DiagnosticsRecordStatus;

public interface DiagnosticsRepository extends JpaRepository <DiagnosticsRecordStatus, Long>, JpaSpecificationExecutor<DiagnosticsRecordStatus> {

}
