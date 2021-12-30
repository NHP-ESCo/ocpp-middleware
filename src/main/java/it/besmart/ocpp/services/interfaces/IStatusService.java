package it.besmart.ocpp.services.interfaces;

import it.besmart.ocpp.dtos.RecordStationStatusDTO;
import it.besmart.ocpp.enums.ConnectorStatusComplete;

import java.time.ZonedDateTime;

import org.springframework.scheduling.annotation.Async;

import it.besmart.ocpp.dtos.RecordConnectorStatusDTO;
import it.besmart.ocpp.model.RecordConnectorStatus;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.Connector;

public interface IStatusService {

	@Async
	public void updateCSStatus(RecordStationStatusDTO status, ChargingStation station);
	
//	@Async
	public void updateConnectorStatus(RecordConnectorStatusDTO status, Connector conn);

	public RecordConnectorStatus findLastConnnectorStatus(Connector conn);
	
	public ZonedDateTime findLastStationUpdate(ChargingStation cs);

	void changeConnectorStatus(Connector conn, ConnectorStatusComplete status, ZonedDateTime sendTime);
	
}
