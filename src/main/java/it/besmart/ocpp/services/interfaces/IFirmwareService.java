package it.besmart.ocpp.services.interfaces;

import eu.chargetime.ocpp.model.firmware.FirmwareStatus;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.FirmwareRecordStatus;

public interface IFirmwareService {

	FirmwareRecordStatus addFirmwareRecord(ChargingStation station);
	
	FirmwareRecordStatus findPendingInstallation(ChargingStation station);
	
	FirmwareRecordStatus updateFirmwareStatus(ChargingStation station, FirmwareStatus status);
	
	
}
