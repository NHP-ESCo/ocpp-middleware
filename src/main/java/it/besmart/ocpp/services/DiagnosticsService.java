package it.besmart.ocpp.services;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.chargetime.ocpp.model.firmware.DiagnosticsStatus;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.DiagnosticsRecordStatus;
import it.besmart.ocpp.repositories.DiagnosticsRepository;
import it.besmart.ocpp.services.interfaces.IDiagnosticsService;
import it.besmart.specifications.enums.Operations;
import it.besmart.specifications.query.CustomSpecificationBuilder;

@Service
@Transactional
public class DiagnosticsService implements IDiagnosticsService {

	@Autowired
	private DiagnosticsRepository repo;
	

	@Override
	public DiagnosticsRecordStatus addDiagnosticsRecord(ChargingStation station, String localPath, boolean sftp) {
		DiagnosticsRecordStatus record = new DiagnosticsRecordStatus();
		record.setStation(station);
		record.setRequestTime(ZonedDateTime.now());
		record.setStatus(DiagnosticsStatus.Idle);
		record.setFilePath(localPath);
		record.setSftp(sftp);
		
		return repo.save(record);
	}

	
	@Override
	public DiagnosticsRecordStatus updateDiagnosticsStatus(ChargingStation station, DiagnosticsStatus status) {
		DiagnosticsRecordStatus record = findPendingDownload(station);
		
		if (record==null)
			return null;
		
		record.setStatus(status);
		if(status.equals(DiagnosticsStatus.Uploaded)) {
			record.setUploadTime(ZonedDateTime.now());
		}
		
		return repo.save(record);
	}

	@Override
	public DiagnosticsRecordStatus findPendingDownload(ChargingStation cs) {
		Set<DiagnosticsStatus> states = new HashSet<>();
		states.add(DiagnosticsStatus.Idle);
		states.add(DiagnosticsStatus.Uploading);
		
		//For other states process is ended
		
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
		builder.with("station", Operations.EQUAL, cs);
		builder.with("status", Operations.IN_SET, states);
		
		Specification<DiagnosticsRecordStatus> spec = builder.build();
		
		List<DiagnosticsRecordStatus> list = repo.findAll(spec);
		
		if(list.size()!=0)
			return list.get(0);
		else
			return null;
	}


	@Override
	public DiagnosticsRecordStatus findById(long id) {
		
		Optional<DiagnosticsRecordStatus> o = repo.findById(id);
		
		return o.isEmpty()?null : o.get();
		
	}


	@Override
	public DiagnosticsRecordStatus updateDiagnosticsRecord(DiagnosticsRecordStatus record) {
		
		return repo.save(record);
	}

}
