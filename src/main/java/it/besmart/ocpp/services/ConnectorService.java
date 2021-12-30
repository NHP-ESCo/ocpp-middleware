package it.besmart.ocpp.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.besmart.ocpp.dtos.ConnectorDTO;
import it.besmart.ocpp.enums.ConnectorStatusComplete;
import it.besmart.ocpp.exceptions.StationException;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.ChargingUnit;
import it.besmart.ocpp.model.Connector;
import it.besmart.ocpp.model.ModelConnector;
import it.besmart.ocpp.repositories.ConnectorRepository;
import it.besmart.ocpp.services.interfaces.IConnectorService;
import it.besmart.ocppLib.dto.StationConnector;


@Service
@Transactional
public class ConnectorService implements IConnectorService {
	
	
	@Autowired
	ConnectorRepository repo;
	

	@Override
	public Connector findById(long id) {
		Optional<Connector> o = repo.findById(id);
        if(o.isPresent()){
            return o.get();
        }
        else{
        	return null;
        }
	}

	@Override
	public Connector findByDTO(ConnectorDTO conn) {
		
		return findById(conn.getConnectorID());
	}

	@Override
	public Connector updateStatus(Connector conn, ConnectorStatusComplete status) {
		
		conn.setStatus(status);
		
		return repo.save(conn);
	}

	@Override
	public List<Connector> findByStation(ChargingStation cs) {
		
		List<Connector> connectors =  repo.findByUnit_ChargingStation(cs);
		//logger.debug(connectors.toString());
		
		return connectors;
	}


	@Override
	public Connector findByStationAndRef(ChargingStation cs, int ref) {
		
		return repo.findByRefAndUnit_ChargingStation(ref, cs);
		
	}

	
	@Override
	public List<Connector> findByUnit(ChargingUnit cu) {
		
		return repo.findByUnit(cu);
	}

	@Override
	public Connector findByUnitAndRef(ChargingUnit cu, int ref) {
		
		return repo.findByRefAndUnit(ref, cu);
	}

	@Override
	public Connector saveConnector(ModelConnector mconn, ChargingUnit cu, StationConnector connDTO) {
		
		Connector connEntity = new Connector();
		connEntity.setRef(mconn.getRef());
		connEntity.setPlug(mconn);
		connEntity.setUnit(cu);
		connEntity.setStatus(ConnectorStatusComplete.UNAVAILABLE);
		
		connEntity.setMinPower();
		connEntity.setMaxPower();
		
		double connMaxPower = connEntity.getMaxPower();
		double connMinPower = connEntity.getMinPower();
		
		Double connPower = connDTO.getActualMaxPower();
		if (connPower == null || connPower == 0) {
			if(cu.getChargingStation().isScEnabled()) 
				connPower = connMaxPower;
			else
				throw new StationException( String.format("Max power for connector %d MUST be defined", mconn.getRef()));
		}
			
		if(connPower < connMinPower || connPower>connMaxPower ) 
			throw new StationException( String.format("Max power for connector %d MUST be chosen between %.1f kW and %.1f kW", 
					mconn.getRef(), connMinPower, connMaxPower));
		
		Double minConnPower = connDTO.getActualMinPower();
		if (minConnPower == null || minConnPower == 0) {
			minConnPower = connMinPower;
		}
			
		if(minConnPower<connMinPower || minConnPower > connPower) 
			throw new StationException( String.format("Min power for connector %d MUST be chosen between %.1f kW and %.1f kW", 
					mconn.getRef(), connMinPower, connPower));
		
		
		connEntity.setActualMaxPower(connPower);
		connEntity.setActualMinPower(minConnPower);
		
		
		return repo.save(connEntity);
		
	}

	@Override
	public Connector updateConnector(Connector conn, StationConnector connDTO) {
		
		conn.setMinPower();
		conn.setMaxPower();
		
		double connMaxPower = conn.getMaxPower();
		double connMinPower = conn.getMinPower();
		
		
		Double connPower = connDTO.getActualMaxPower();
		if (connPower != null && connPower != 0) {
			
			if(connPower>connMaxPower || connPower < connMinPower) 
				throw new StationException( String.format("Max power for connector %d MUST be chosen between %.1f kW and %.1f kW", 
						conn.getPlug().getRef(), connMinPower, connMaxPower));
			
			conn.setActualMaxPower(connPower);
			
		}
		

		Double minConnPower = connDTO.getActualMinPower();
		if (minConnPower != null && minConnPower != 0) {
			if(minConnPower<connMinPower || minConnPower > conn.getActualMaxPower()) 
				throw new StationException( String.format("Min power for connector %d MUST be chosen between %.1f kW and %.1f kW", 
						conn.getPlug().getRef(), connMinPower, connPower));
			
			conn.setActualMinPower(minConnPower);
		}
		
		
		return repo.save(conn);
	}
	
	
	@Override
	public Connector findAvailableConnectorInUnit(ChargingUnit cu) {
		for(Connector conn : findByUnit(cu) ) {
			if(conn.getStatus()==ConnectorStatusComplete.AVAILABLE) {
				return conn;
			}
		}
		return null;
	}

}
