package it.besmart.ocppLib.wrappers.Request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class ConnectorRequest {

	@NotNull
	@NotEmpty
	private String evseId;
	
	@NotNull
	@Min(1)
	private int connector;
	
	
	public ConnectorRequest() {
		super();
	}


	public int getConnector() {
		return connector;
	}

	public void setConnector(int connector) {
		this.connector = connector;
	}

	public String getEvseId() {
		return evseId;
	}

	public void setEvseId(String evseId) {
		this.evseId = evseId;
	}


	@Override
	public String toString() {
		return "UnlockSocketRequest [evseId=" + evseId + ", connector=" + connector + "]";
	}
}
