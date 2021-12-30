package it.besmart.ocpp.services.interfaces;

import java.util.List;

import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.ModelCapability;
import it.besmart.ocpp.model.StationCapability;
import it.besmart.ocppLib.enumeration.CapabilityType;

public interface IStationCapabilityService {

	StationCapability findCapability(ChargingStation station, CapabilityType cap);

	List<StationCapability> findAllCapabilities(ChargingStation station);

	StationCapability saveOrUpdateCapability(StationCapability cap);

	StationCapability findOrSaveByModelCapability(ModelCapability m, ChargingStation cs);

}
