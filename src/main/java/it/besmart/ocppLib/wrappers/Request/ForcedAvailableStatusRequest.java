package it.besmart.ocppLib.wrappers.Request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


public class ForcedAvailableStatusRequest {

	@NotEmpty
	private String evseId;
	
	@NotNull
	private Integer connectorId;

	
	public ForcedAvailableStatusRequest() {
		super();
	}

	
	public String getEvseId() {
		return evseId;
	}

	public void setEvseId(String evseId) {
		this.evseId = evseId;
	}

	public Integer getConnectorId() {
		return connectorId;
	}

	public void setConnectorId(Integer connectorId) {
		this.connectorId = connectorId;
	}
	
	
}
