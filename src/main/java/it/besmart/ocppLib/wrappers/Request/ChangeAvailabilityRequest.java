package it.besmart.ocppLib.wrappers.Request;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


public class ChangeAvailabilityRequest {

	@NotNull
	@NotEmpty
	private String evseId;
	
	private int connector;
	
	@NotNull
	private AvailabilityType status;  
	
	public ChangeAvailabilityRequest() {
		super();
	}


	public AvailabilityType getStatus() {
		return status;
	}

	public void setStatus(AvailabilityType status) {
		this.status = status;
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


	public enum AvailabilityType {
		
		AVAILABLE,
		UNAVAILABLE;

	}


	@Override
	public String toString() {
		return "ChangeAvailabilityRequest [evseId=" + evseId + ", connector=" + connector + ", status=" + status + "]";
	}

}
