package it.besmart.ocpp.services.interfaces;

import eu.chargetime.ocpp.model.firmware.DiagnosticsStatus;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.DiagnosticsRecordStatus;

public interface IDiagnosticsService {

	DiagnosticsRecordStatus addDiagnosticsRecord(ChargingStation station, String email, boolean sftp);
	
	DiagnosticsRecordStatus updateDiagnosticsStatus(ChargingStation station, DiagnosticsStatus status);

	DiagnosticsRecordStatus findPendingDownload(ChargingStation cs);

	DiagnosticsRecordStatus findById(long id);

	DiagnosticsRecordStatus updateDiagnosticsRecord(DiagnosticsRecordStatus record);
	
}
