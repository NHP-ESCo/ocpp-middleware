package it.besmart.ocpp.services.interfaces;

import java.util.List;

import it.besmart.ocpp.dtos.ConnectorDTO;
import it.besmart.ocpp.enums.ConnectorStatusComplete;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.ChargingUnit;
import it.besmart.ocpp.model.Connector;
import it.besmart.ocpp.model.ModelConnector;
import it.besmart.ocppLib.dto.StationConnector;

public interface IConnectorService {

	public Connector findById(long id);
	
	public Connector findByDTO(ConnectorDTO cs);
	
	public Connector saveConnector(ModelConnector mconn, ChargingUnit cu, StationConnector conn);
	
	public Connector updateConnector(Connector conn, StationConnector connDTO);
	
	
	public Connector updateStatus(Connector conn, ConnectorStatusComplete status);
	
	public List<Connector> findByStation(ChargingStation cs);
	
	public List<Connector> findByUnit(ChargingUnit cu);
	
	public Connector findAvailableConnectorInUnit(ChargingUnit cu);
	
	public Connector findByStationAndRef(ChargingStation cs, int ref);
	
	public Connector findByUnitAndRef(ChargingUnit cu, int ref);
	
}
