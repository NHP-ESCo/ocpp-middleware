package it.besmart.ocpp.services;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.besmart.ocpp.dtos.ChargingUnitDTO;
import it.besmart.ocpp.enums.ConnectorStatusComplete;
import it.besmart.ocpp.exceptions.StationException;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.ChargingUnit;
import it.besmart.ocpp.model.Connector;
import it.besmart.ocpp.model.ModelConnector;
import it.besmart.ocpp.model.ModelUnit;
import it.besmart.ocpp.repositories.CURepository;
import it.besmart.ocpp.services.interfaces.ICUService;
import it.besmart.ocpp.services.interfaces.IConnectorService;
import it.besmart.ocppLib.dto.StationConnector;
import it.besmart.ocppLib.dto.StationUnit;
import it.besmart.ocppLib.dto.UnitCompleteStatus;
import it.besmart.ocppLib.enumeration.ConnectorStatus;
import it.besmart.ocppLib.enumeration.UnitStatus;

@Service
@Transactional
public class CUService implements ICUService {

	private final Logger logger = LoggerFactory.getLogger(CUService.class);
	
	
	@Autowired 
	private CURepository repo;
	
	@Autowired
	private IConnectorService connService;
	
	@Override
	public ChargingUnit findByStationAndRef(ChargingStation cs, int ref) {
		
		return repo.findByChargingStationAndRef(cs, ref);
	}

	@Override
	public List<ChargingUnit> findByStation(ChargingStation cs) {
		
		return repo.findByChargingStation(cs);
	}


	@Override
	public ChargingUnit findByEvse(String evse) {
		
		return repo.findByEvseCode(evse);
	}


	@Override
	public ChargingUnit findByDTO(ChargingUnitDTO cu) {
		
		return findById(cu.getUnitID());
	}

	@Override
	public ChargingUnit findById(long id) {
		Optional<ChargingUnit> o = repo.findById(id);
        if(o.isPresent())
            return o.get();
        else
        	return null;
	}
	
	@Override
	public boolean isReserved(ChargingUnit cu) {
		for (Connector conn : connService.findByUnit(cu)) {
			if (conn.getStatus()==ConnectorStatusComplete.RESERVED)
				return true;
		}
		return false;
	}
	
	@Override
	public boolean isReady(ChargingUnit cu) {
		UnitStatus status = getUnitStatus(cu).getStatus();
		return status.equals(UnitStatus.AVAILABLE) || status.equals(UnitStatus.PARTIALLY_AVAILABLE);
	}


	@Override
	public ChargingUnit saveCU(StationUnit cu, ChargingStation station, ModelUnit mu) {
		ChargingUnit cuEntity = new ChargingUnit();
		cuEntity.setRef(cu.getRef());
		cuEntity.setPowerType(station.getPowerType());
		cuEntity.setChargingStation(station);
		cuEntity.createEvse();	
		
		cuEntity = repo.save(cuEntity);
		
		//Save connectors
		for(ModelConnector modelConn : mu.getConnectors()) {
			
			StationConnector conn = cu.getConn(modelConn.getRef());
			if(conn==null)
				throw new StationException( String.format("Connector %d has to be defined in CU %d", 
						modelConn.getRef(), cu.getRef()));
			
			Connector connEntity = connService.saveConnector(modelConn, cuEntity, conn);
			
			cuEntity.addConnector(connEntity);
		}
		
		cuEntity.setMaxPower();
		cuEntity.setMinPower();
		
		return repo.save(cuEntity);
	}
	
	
	@Override
	public ChargingUnit updateCU(ChargingUnit cu, StationUnit cuDTO) {
		
		cu.setPowerType(cu.getChargingStation().getPowerType());
			
		for(Connector connEntity : connService.findByUnit(cu)) {
			
			StationConnector connDTO = cuDTO.getConn(connEntity.getRef());
			
			if(connDTO!=null) {
				logger.debug("Update connector " + connDTO.toString());
				connEntity = connService.updateConnector(connEntity, connDTO);
				logger.debug("Updated connector " + connEntity.toString());
			}
			cu.addConnector(connEntity);
		}
		
		cu.setMaxPower();
		cu.setMinPower();
		
		return repo.save(cu);
		
	}

	
	@Override
	public UnitCompleteStatus getUnitStatus(ChargingUnit unit) {
		//Unit status
		UnitCompleteStatus completeStatus = new UnitCompleteStatus();
		completeStatus.setEvseId(unit.getEvseCode());
		
		ConnectorStatus connStatus = ConnectorStatus.OUTOFSERVICE;
		UnitStatus unitStatus = UnitStatus.OUTOFSERVICE;
		boolean statusSet = false;
		Boolean faulted = null;
		for (Connector conn : connService.findByUnit(unit)) {
			
			connStatus =  conn.getStatus().simpleStatus();
			completeStatus.addConnectorState(conn.getRef(), connStatus);
			
			if(!statusSet) {
				switch(connStatus) {
				
					case OCCUPIED:
						unitStatus = UnitStatus.OCCUPIED;
						statusSet = true;
						break;
					case RESERVED:
						unitStatus = UnitStatus.RESERVED;
						statusSet = true;
						break;
					case AVAILABLE:
					case OUTOFSERVICE:
						if (faulted==null) { //available or out of service
							if(connStatus == ConnectorStatus.AVAILABLE) {
								faulted = false;
								unitStatus = UnitStatus.AVAILABLE;
							}
							else {
								faulted = true;
								unitStatus = UnitStatus.OUTOFSERVICE;
							}
						}
						else {
							if( (faulted && connStatus == ConnectorStatus.AVAILABLE)  || ( !faulted && connStatus == ConnectorStatus.OUTOFSERVICE) ) {
								unitStatus = UnitStatus.PARTIALLY_AVAILABLE;
							}
						}	
						break;
				}
			}
			
		}
		
		completeStatus.setStatus(unitStatus);
		
		return completeStatus;
	}

	@Override
	public List<ChargingUnit> findBySpec(Specification<ChargingUnit> spec) {
		
		return repo.findAll(spec);
	}
	

}
