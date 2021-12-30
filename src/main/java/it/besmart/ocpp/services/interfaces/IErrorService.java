package it.besmart.ocpp.services.interfaces;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Async;

import it.besmart.ocpp.enums.ErrorType;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.ChargingUnit;
import it.besmart.ocpp.model.Connector;
import it.besmart.ocpp.model.StoredLog;

public interface IErrorService {

	
	List<StoredLog> findErrors(ChargingStation station);

	@Async
	void saveConnectorError(String string, ErrorType stationError, Connector conn, String session);

	StoredLog storeConnectorError(String message, ErrorType type, Connector conn, String session);
	
	@Async
	void solveConnectorErrors(Connector conn, String message);
	
	@Async
	void saveError(String message, ErrorType type, ChargingStation cs, ZonedDateTime timestamp, String session);
	
	@Async
	void solveStationErrors(ChargingStation station, ErrorType type, String message);

	@Async
	void solveSessionErrors(ChargingUnit unit, String externalSession);

	
	List<StoredLog> findStationErrors(ChargingStation station, ErrorType type, String message);

	List<StoredLog> findSessionErrors(ChargingUnit unit, String externalSession);

	List<StoredLog> findConnectorErrors(Connector conn, String message);

	void solveErrors(List<Long> errors);
	
}
