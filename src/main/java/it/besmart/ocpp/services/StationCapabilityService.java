package it.besmart.ocpp.services;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.ModelCapability;
import it.besmart.ocpp.model.ModelCapabilityKey;
import it.besmart.ocpp.model.StationCapability;
import it.besmart.ocpp.repositories.StationCapabilityRepository;
import it.besmart.ocpp.services.interfaces.IStationCapabilityService;
import it.besmart.ocppLib.enumeration.CapabilityStatus;
import it.besmart.ocppLib.enumeration.CapabilityType;
import it.besmart.specifications.enums.Operations;
import it.besmart.specifications.query.CustomSpecificationBuilder;

@Service
@Transactional
public class StationCapabilityService implements IStationCapabilityService {

	@Autowired
	private StationCapabilityRepository repo;
	
	private final Logger logger = LoggerFactory.getLogger(StationCapabilityService.class);
	
	@Override
	public StationCapability findCapability(ChargingStation station, CapabilityType cap) {
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
		
		builder.with("station", Operations.EQUAL, station);
		builder.with("key", Operations.JOIN, new ModelCapabilityKey(cap, station.getModel()), "capability");
		
		Optional<StationCapability> o = repo.findOne(builder.build());
		
		return o.isEmpty() ? null : o.get();
		
	}
	
	@Override
	public List<StationCapability> findAllCapabilities(ChargingStation station) {
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
		
		builder.with("station", Operations.EQUAL, station);
		
		
		return repo.findAll(builder.build());
		
	}

	@Override
	public StationCapability saveOrUpdateCapability(StationCapability cap) {
		logger.debug(String.format("%s: Set %s to %s", 
				cap.getStation().getEvseID(), cap.getCapability().getCapability(), cap.getValue().name()));
		return repo.save(cap);
	}

	@Override
	public StationCapability findOrSaveByModelCapability(ModelCapability m, ChargingStation station) {
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
		
		builder.with("capability", Operations.EQUAL, m);
		builder.with("station", Operations.EQUAL, station);
		
		Optional<StationCapability> o = repo.findOne(builder.build());
		
		StationCapability cap = null;
		if(o.isEmpty()) {
			cap = new StationCapability();
			cap.setCapability(m);
			cap.setStation(station);
			cap.setValue(CapabilityStatus.Disabled);
			cap = repo.save(cap);
		}
		else
			cap = o.get();
		
		return cap;
	}
}
