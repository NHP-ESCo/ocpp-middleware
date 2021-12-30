package it.besmart.ocpp.services.interfaces;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import it.besmart.ocpp.dtos.RecordStationStatusDTO;
import it.besmart.ocpp.enums.CSLifeStatus;
import it.besmart.ocpp.exceptions.ParameterException;
import it.besmart.ocpp.exceptions.StationException;
import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocppLib.dto.StationData;
import it.besmart.ocppLib.dto.StationSettings;

public interface IStationService {

	public ChargingStation createStation(StationData cs) throws StationException, ParameterException;

	public ChargingStation updateStation(StationSettings csDTO) throws StationException, ParameterException;
	
	public ChargingStation updateStation(ChargingStation cs);
	
	public ChargingStation updateStationPower(ChargingStation station, StationSettings cs, boolean newStation);

	
	public List<ChargingStation> findAll();
	
	public List<ChargingStation> findBySpecification(Specification<ChargingStation> spec);
	
	public ChargingStation findByEvseID(String s); //unique for not dismissed stations
	
	public ChargingStation findBySession(UUID sessionIndex);
	
	public List<ChargingStation> findConnectedStations();

	
	public double computeDeliveredEnergy(ChargingStation station);
	

	
	public ChargingStation updateLifeStatus(long csID, CSLifeStatus status);

	public ChargingStation updateStatus(ChargingStation cs, RecordStationStatusDTO status);
	
	public ChargingStation connectStation(ChargingStation cs, String session, String addressIP);
	
	public ChargingStation disconnectStation(ChargingStation station, boolean restartApp);

	public void delete(ChargingStation station);

	public ChargingStation dismissStation(ChargingStation cs);

	public ChargingStation findBySN(String evse);

	
	
}
