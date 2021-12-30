package it.besmart.ocpp.dtos;

import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnore;

import it.besmart.ocpp.model.ChargingUnit;
import it.besmart.ocppLib.dto.StationConnector;
import it.besmart.ocppLib.dto.StationUnit;
import it.besmart.ocppLib.enumeration.ConnectionPowerType;

public class ChargingUnitDTO {

	@JsonIgnore
	private long unitID;
	
	private ConnectionPowerType powerType;
	
	@NotNull
	private int ref; //# in the CS
	
	private Double maxPower; 
	
	private Double minPower; 

	@Valid
	private Set<ConnectorDTO> connectors = new HashSet<>();
	
	
	public ChargingUnitDTO() {
		super();
	}


	public ChargingUnitDTO(ChargingUnit cu) { 
		this.unitID = cu.getUnitID();
		this.ref = cu.getRef();
		this.maxPower = cu.getMaxPower();
		this.minPower = cu.getMinPower();
	}


	public ChargingUnitDTO(StationUnit unit) {
		this.ref = unit.getRef();
		this.maxPower = unit.getMaxPower();
		this.minPower = unit.getMinPower();
		
		for (StationConnector conn : unit.getConnectors()) {
			this.connectors.add(new ConnectorDTO(conn));
		}
				
	}


	public long getUnitID() {
		return unitID;
	}


	public void setUnitID(long unitID) {
		this.unitID = unitID;
	}

	public int getRef() {
		return ref;
	}


	public void setRef(int ref) {
		this.ref = ref;
	}
	

	public Double getMaxPower() {
		return maxPower;
	}


	public void setMaxPower(Double maxPower) {
		this.maxPower = maxPower;
	}

	
	public Set<ConnectorDTO> getConnectors() {
		return connectors;
	}


	public void setConnectors(Set<ConnectorDTO> connectors) {
		this.connectors = connectors;
	}


	public ConnectorDTO getConn(int ref) {

		for (ConnectorDTO conn : connectors) {
			if(conn.getRef() == ref) 
				return conn;
		}
		return null;
	}


	public void addConnector(ConnectorDTO connectorDTO) {
		this.connectors.add(connectorDTO);
		
	}


	public ConnectionPowerType getPowerType() {
		return powerType;
	}


	public void setPowerType(ConnectionPowerType powerType) {
		this.powerType = powerType;
	}


	public Double getMinPower() {
		return minPower;
	}


	public void setMinPower(Double minPower) {
		this.minPower = minPower;
	}
	
	@Override
	public String toString() {
		return "ChargingUnitDTO [unitID=" + unitID + ", powerType=" + powerType + ", ref=" + ref
				+ ", maxPower=" + maxPower + ", minPower=" + minPower + ", connectors=" + connectors + "]";
	}
	

}
