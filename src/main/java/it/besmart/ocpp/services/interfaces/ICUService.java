package it.besmart.ocpp.services.interfaces;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import it.besmart.ocpp.dtos.ChargingUnitDTO;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.ChargingUnit;
import it.besmart.ocpp.model.ModelUnit;
import it.besmart.ocppLib.dto.StationUnit;
import it.besmart.ocppLib.dto.UnitCompleteStatus;

public interface ICUService {

	public ChargingUnit findByStationAndRef(ChargingStation cs, int ref);
	
	public ChargingUnit findByEvse(String evse);
	
	public ChargingUnit findById(long id);

	public ChargingUnit findByDTO(ChargingUnitDTO cu);
	
	public List<ChargingUnit> findByStation(ChargingStation cs);
	
	public List<ChargingUnit> findBySpec(Specification<ChargingUnit> spec);

	
	public ChargingUnit updateCU(ChargingUnit cu, StationUnit cuDTO);

	public UnitCompleteStatus getUnitStatus(ChargingUnit unit);

	public boolean isReserved(ChargingUnit cu);

	public boolean isReady(ChargingUnit cu);

	
	public ChargingUnit saveCU(StationUnit cuDTO, ChargingStation station, ModelUnit mu);

	
	
	

}
