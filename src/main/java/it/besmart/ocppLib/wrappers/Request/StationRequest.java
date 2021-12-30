package it.besmart.ocppLib.wrappers.Request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class StationRequest {

	@NotNull
	@NotEmpty
	private String evseId;
	
	
	public StationRequest() {
		super();
	}

	public String getEvseId() {
		return evseId;
	}

	public void setEvseId(String evseId) {
		this.evseId = evseId;
	}

	@Override
	public String toString() {
		return "StationRequest [evseId=" + evseId + "]";
	}
	
	
}
