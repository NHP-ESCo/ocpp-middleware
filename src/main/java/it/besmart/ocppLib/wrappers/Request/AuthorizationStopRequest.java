package it.besmart.ocppLib.wrappers.Request;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.besmart.ocppLib.dto.IdTag;

public class AuthorizationStopRequest {
	
	@JsonProperty(value="session_id")
	private String sessionId;
	
	@JsonProperty(value="evse_id")
	private String evseId;
	
	@JsonProperty(value="id_tag")
	private IdTag idTag; 
	

	public AuthorizationStopRequest() {
		super();
	}

	public String getSessionId() {
		return sessionId;
	}

	public String getEvseId() {
		return evseId;
	}

	public IdTag getIdTag() {
		return idTag;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public void setEvseId(String evseId) {
		this.evseId = evseId;
	}

	public void setIdTag(IdTag idTag) {
		this.idTag = idTag;
	}

	@Override
	public String toString() {
		return "AuthorizationStartRequest [sessionId=" + sessionId + ", evseId=" + evseId + ", idTag=" + idTag + "]";
	}

}
