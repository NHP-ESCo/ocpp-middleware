package it.besmart.ocpp.servers.v16;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import eu.chargetime.ocpp.feature.profile.ServerFirmwareManagementEventHandler;
import eu.chargetime.ocpp.model.firmware.DiagnosticsStatusNotificationConfirmation;
import eu.chargetime.ocpp.model.firmware.DiagnosticsStatusNotificationRequest;
import eu.chargetime.ocpp.model.firmware.FirmwareStatusNotificationConfirmation;
import eu.chargetime.ocpp.model.firmware.FirmwareStatusNotificationRequest;
import it.besmart.ocpp.client.external.IClientService;
import it.besmart.ocpp.enums.ErrorType;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.DiagnosticsRecordStatus;
import it.besmart.ocpp.services.interfaces.IDiagnosticsService;
import it.besmart.ocpp.services.interfaces.IErrorService;
import it.besmart.ocpp.services.interfaces.IFirmwareService;
import it.besmart.ocpp.services.interfaces.IStationService;

@Component("firmwareProfileHandler_16")
@DependsOn("serverMap")
public class FirmwareManagementProfileHandler_v16 implements ServerFirmwareManagementEventHandler {
	
	private final Logger logger = LoggerFactory.getLogger(FirmwareManagementProfileHandler_v16.class);
	
	@Autowired
	private IStationService stationService;
	
	@Autowired
	private IFirmwareService firmwareService;
	
	@Autowired
	private IDiagnosticsService diagnosticsService;
	
	@Autowired
	private IErrorService errorService;
	
	@Autowired
	private IClientService clientService;
	
	public FirmwareManagementProfileHandler_v16() {
		super();
		logger.debug("Create FirmwareProfileHandler for 1.6");
	}
	
	@Override
	public DiagnosticsStatusNotificationConfirmation handleDiagnosticsStatusNotificationRequest(UUID sessionIndex,
			DiagnosticsStatusNotificationRequest request) {
		
		ChargingStation station = stationService.findBySession(sessionIndex);
		
		logger.debug(String.format("Diagnostics %s : %s", station.getEvseID(), request.getStatus()));
		
		DiagnosticsRecordStatus record = diagnosticsService.updateDiagnosticsStatus(station, request.getStatus());
		
		if(record!=null) {
			switch(request.getStatus()) {
				case UploadFailed:
				case Uploaded:
					clientService.sendDiagnosticsResult(record);
					break;
				default:
					break;
			
			}
		}
		
		
		return new DiagnosticsStatusNotificationConfirmation();
	}

	@Override
	public FirmwareStatusNotificationConfirmation handleFirmwareStatusNotificationRequest(UUID sessionIndex,
			FirmwareStatusNotificationRequest request) {
		
		ChargingStation station = stationService.findBySession(sessionIndex);
		
		logger.debug("Firmware : " + request.getStatus());
		
		firmwareService.updateFirmwareStatus(station, request.getStatus());
		
		switch(request.getStatus()) {
		case DownloadFailed:
		case InstallationFailed:
			errorService.saveError(String.format("Firmware not updated. Reason : %s", request.getStatus()), ErrorType.FIRMWARE_ERROR, station, null, null);
			break;
		case Installed:
			errorService.solveStationErrors(station, ErrorType.FIRMWARE_ERROR, null);
			break;
		case Downloaded:
		case Downloading:
		case Idle:
		case Installing:
		default:
			break;
		
		}
		
		return new FirmwareStatusNotificationConfirmation();
	}

}
