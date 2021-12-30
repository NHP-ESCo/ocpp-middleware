package it.besmart.ocpp.services;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.chargetime.ocpp.model.firmware.FirmwareStatus;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.FirmwareRecordStatus;
import it.besmart.ocpp.repositories.FirmwareRepository;
import it.besmart.ocpp.services.interfaces.IErrorService;
import it.besmart.ocpp.services.interfaces.IFirmwareService;
import it.besmart.specifications.enums.Operations;
import it.besmart.specifications.query.CustomSpecificationBuilder;

@Service
@Transactional
public class FirmwareService implements IFirmwareService {

	@Autowired
	FirmwareRepository repo;
	
	@Autowired
	IErrorService errorService;
	
	@Override
	public FirmwareRecordStatus addFirmwareRecord(ChargingStation station) {
		FirmwareRecordStatus record = new FirmwareRecordStatus();
		record.setStation(station);
		record.setRequestTime(ZonedDateTime.now());
		record.setStatus(FirmwareStatus.Idle);
		
		return repo.save(record);
	}

	@Override
	public FirmwareRecordStatus findPendingInstallation(ChargingStation station) {
		
		Set<FirmwareStatus> states = new HashSet<>();
		states.add(FirmwareStatus.Idle);
		states.add(FirmwareStatus.Downloading);
		states.add(FirmwareStatus.Downloaded);
		states.add(FirmwareStatus.Installing);
		//For other states process is ended
		
		
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
		builder.with("station", Operations.EQUAL, station);
		builder.with("status", Operations.IN_SET, states);
		
		Specification<FirmwareRecordStatus> spec = builder.build();
		
		List<FirmwareRecordStatus> list = repo.findAll(spec);
		
		if(list.size()!=0)
			return list.get(0);
		else
			return null;
	}

	@Override
	public FirmwareRecordStatus updateFirmwareStatus(ChargingStation station, FirmwareStatus status) {

		FirmwareRecordStatus record = findPendingInstallation(station);
		
		if (record==null)
			record = addFirmwareRecord(station);
		
		record.setStatus(status);
		if(status.equals(FirmwareStatus.Installed)) {
			record.setInstalledTime(ZonedDateTime.now());
		}
		
		return repo.save(record);
	}

}
