package it.besmart.ocpp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import it.besmart.ocpp.model.FirmwareRecordStatus;

public interface FirmwareRepository extends JpaRepository <FirmwareRecordStatus, Long>, JpaSpecificationExecutor<FirmwareRecordStatus> {

}
