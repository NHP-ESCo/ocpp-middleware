package it.besmart.ocppLib.dto;

import java.util.HashMap;

import it.besmart.ocppLib.enumeration.ConnectorStatus;
import it.besmart.ocppLib.enumeration.UnitStatus;

public class UnitCompleteStatus {
	
	private String evseId;
	
	private UnitStatus status;
	
	private HashMap<Integer, ConnectorStatus> connectorStates = new HashMap<>();
	
	
	public UnitCompleteStatus() {
		super();
	}

	public String getEvseId() {
		return evseId;
	}

	public void setEvseId(String evseId) {
		this.evseId = evseId;
	}

	public UnitStatus getStatus() {
		return status;
	}

	public void setStatus(UnitStatus status) {
		this.status = status;
	}

	public HashMap<Integer, ConnectorStatus> getConnectorStates() {
		return connectorStates;
	}

	public void setConnectorStates(HashMap<Integer, ConnectorStatus> connectorStates) {
		this.connectorStates = connectorStates;
	}
	
	public void addConnectorState(Integer ref, ConnectorStatus status) {
		this.connectorStates.put(ref, status);
	}

	@Override
	public String toString() {
		return "UnitCompleteStatus [evseId=" + evseId + ", status=" + status + ", connectorStates=" + connectorStates
				+ "]";
	}
	
}
