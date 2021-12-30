package it.besmart.ocpp.dtos;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.besmart.ocpp.enums.ConnectorStatusComplete;
import it.besmart.ocpp.model.Connector;
import it.besmart.ocppLib.dto.StationConnector;
import it.besmart.ocppLib.enumeration.PlugType;

public class ConnectorDTO {

	@JsonIgnore
	private long connectorID;
	
	@NotNull
	private int ref; //# in the CS
	
	
	private Double maxPower; 
	
	@NotNull
	private Double actualMaxPower;
	
	private Double minPower; 
	
	@Enumerated(EnumType.STRING)
	private PlugType plugType;
	
	@JsonIgnore
	@Enumerated(EnumType.STRING)
	private ConnectorStatusComplete status;
	
	public ConnectorDTO(Connector conn) {
		connectorID = conn.getConnectorID();
		ref = conn.getRef();
		plugType = conn.getPlug().getPlug();
		maxPower = conn.getMaxPower();
		minPower = conn.getMinPower();
		actualMaxPower = conn.getActualMaxPower();
		status = conn.getStatus();
		
	}
	

	public ConnectorDTO() {
		super();
	}

	public ConnectorDTO(StationConnector conn) {
		ref = conn.getRef();
		actualMaxPower = conn.getActualMaxPower();
	}


	public int getRef() {
		return ref;
	}

	public void setConnectorID(long connectorID) {
		this.connectorID = connectorID;
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

	public ConnectorStatusComplete getStatus() {
		return status;
	}

	public void setStatus(ConnectorStatusComplete status) {
		this.status = status;
	}

	public long getConnectorID() {
		return connectorID;
	}
	
	
	public PlugType getPlugType() {
		return plugType;
	}

	public void setPlugType(PlugType plugType) {
		this.plugType = plugType;
	}


	public Double getMinPower() {
		return minPower;
	}


	public void setMinPower(Double minPower) {
		this.minPower = minPower;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ConnectorDTO [connectorID=").append(connectorID).append(", ref=").append(ref)
				.append(", maxPower=").append(maxPower).append(", actualMaxPower=").append(actualMaxPower)
				.append(", minPower=").append(minPower).append(", plugType=").append(plugType).append(", status=")
				.append(status).append("]");
		return builder.toString();
	}


	public Double getActualMaxPower() {
		return actualMaxPower;
	}


	public void setActualMaxPower(Double actualMaxPower) {
		this.actualMaxPower = actualMaxPower;
	}


}
