package it.besmart.ocpp.services;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.besmart.ocpp.enums.ErrorType;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.ChargingUnit;
import it.besmart.ocpp.model.Connector;
import it.besmart.ocpp.model.StoredLog;
import it.besmart.ocpp.repositories.StoredLogRepository;
import it.besmart.ocpp.services.interfaces.IErrorService;
import it.besmart.specifications.enums.Operations;
import it.besmart.specifications.query.CustomSpecificationBuilder;

@Service
@Transactional
public class ErrorService implements IErrorService {
	
	private final Logger logger = LoggerFactory.getLogger(ErrorService.class);
	
	public final static String deltaButtonError = "OtherError EMERGENCY STOP ACTIV";
	
	
	@Autowired
	private StoredLogRepository repo;

	@Override
	public void saveError(String message, ErrorType type, ChargingStation cs, ZonedDateTime timestamp, String session) {
		logger.warn(String.format("%s [%s]: %s", cs!=null?cs.getEvseID():"", type.toString(), message));
		
		if(cs!=null && session==null) { //errors related to recharge are always stored
			if(message != null && message.contains("WeakSignal")) {
				
				solveStationErrors(cs, type, "WeakSignal");
			}
			else if(!findStationErrors(cs, type, message).isEmpty()) //already present
				return;
		}
			
		StoredLog log = new StoredLog(message,type);
		log.setStation(cs);
		if(timestamp!=null)
			log.setTimestamp(timestamp);
		log.setSession(session);
		
		repo.save(log);
	}

	@Override
	public void saveConnectorError(String message, ErrorType type, Connector conn, String session) {
		
		logger.warn(String.format("%s [%s]: %s", conn!=null?conn.getUnit().getEvseCode():"", type.toString(), message));
		
		if(session == null && !findConnectorErrors(conn, message).isEmpty())
			return; //already present
		
		
		StoredLog log = new StoredLog(message, type);
		log.setConnector(conn);
		log.setStation(conn.getUnit().getChargingStation());
		log.setSession(session);
		
		repo.save(log);
	}
	
	
	@Override
	public StoredLog storeConnectorError(String message, ErrorType type, Connector conn, String session) {
		
		logger.warn(String.format("%s [%s]: %s", conn!=null?conn.getUnit().getEvseCode():"", type.toString(), message));
		
		if(session == null && !findConnectorErrors(conn, message).isEmpty())
			return null; //already present
		
		
		StoredLog log = new StoredLog(message, type);
		log.setConnector(conn);
		log.setStation(conn.getUnit().getChargingStation());
		log.setSession(session);
		
		return repo.save(log);
	}

	
	@Override
	public void solveConnectorErrors(Connector conn, String message) {
		
		for(StoredLog error : findConnectorErrors(conn, message)) {
			if(message==null && error.getMessage().contains("ConnectorLockFailure"))
				break; //Do not solve connector lock, if not explicit
			
			error.setSolved(true);
			logger.debug(String.format("%s: Solve error %s", conn.getUnit().getEvseCode(), error.getMessage()));
			
			repo.save(error);
		}
	}
	
	@Override
	public void solveSessionErrors(ChargingUnit unit, String externalSession) {
		for(StoredLog error : findSessionErrors(unit, externalSession)) {
			
			error.setSolved(true);
			logger.debug(String.format("%s: Solve error %s", unit.getEvseCode(), error.getMessage()));
			
			repo.save(error);
		}
		
	}
	
	@Override
	public List<StoredLog> findConnectorErrors(Connector conn, String message) {
		
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
		builder.with("type", Operations.EQUAL, ErrorType.STATION_ERROR);
		builder.with("solved", Operations.EQUAL, false);
		builder.with("connector", Operations.EQUAL, conn);
		if(message!=null)
			builder.with("message", Operations.LIKE, message);
		
		Specification<StoredLog> spec = builder.build();
		return repo.findAll(spec);
	}
	
	
	@Override
	public List<StoredLog> findSessionErrors(ChargingUnit unit, String externalSession) {
		
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
		builder.with("solved", Operations.EQUAL, false);
		builder.with("session", Operations.EQUAL, externalSession);
		builder.with("unit", Operations.JOIN, unit, "connector");
		
		Specification<StoredLog> spec = builder.build();
		return repo.findAll(spec);
	}

	
	@Override
	public void solveStationErrors(ChargingStation station, ErrorType type, String message) {
		
		for(StoredLog error : findStationErrors(station, type, message)) {
			error.setSolved(true);
			logger.debug(String.format("%s: Solve error %s", station.getEvseID(), message!=null?message:type.getValue()));
			repo.save(error);
		}
	}


	/** Retrieve both station and connector erros **/
	@Override
	public List<StoredLog> findErrors(ChargingStation station) {
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
		
		Set<ErrorType> errorTypes = new HashSet<ErrorType>();
		errorTypes.add(ErrorType.STATION_ERROR);
		errorTypes.add(ErrorType.CONFIGURATION_ERROR);
		errorTypes.add(ErrorType.FIRMWARE_ERROR);
		errorTypes.add(ErrorType.NETWORK_ERROR);
		errorTypes.add(ErrorType.RECHARGE_ERROR);
		builder.with("type", Operations.IN_SET, errorTypes);
		
		builder.with("solved", Operations.EQUAL, false);
		builder.with("station", Operations.EQUAL, station);
		
		return repo.findAll(builder.build());
	}
	
	
	@Override
	public List<StoredLog> findStationErrors(ChargingStation station, ErrorType type, String message) {
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
		if(type!=null)
			builder.with("type", Operations.EQUAL, type);
		else {
			Set<ErrorType> errorTypes = new HashSet<ErrorType>();
			errorTypes.add(ErrorType.STATION_ERROR);
			errorTypes.add(ErrorType.CONFIGURATION_ERROR);
			errorTypes.add(ErrorType.FIRMWARE_ERROR);
			errorTypes.add(ErrorType.NETWORK_ERROR);
			builder.with("type", Operations.IN_SET, errorTypes);
			
		}
		builder.with("solved", Operations.EQUAL, false);
		builder.with("station", Operations.EQUAL, station);
		builder.with("connector", Operations.IS_NULL, null);
		
		if(message!=null) {
			
			if(message.contains("Key")) //remove info from ecpp
				message = message.substring(0, message.indexOf(":"));
			
			builder.with("message", Operations.LIKE, message);
		}
		Specification<StoredLog> spec = builder.build();
		return repo.findAll(spec);
	}



	@Override
	public void solveErrors(List<Long> errors) {
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
		builder.with("logID", Operations.IN, errors);
		
		for(StoredLog error : repo.findAll(builder.build())) {
			error.setSolved(true);
			repo.save(error);
		}
	}

	
	

}
