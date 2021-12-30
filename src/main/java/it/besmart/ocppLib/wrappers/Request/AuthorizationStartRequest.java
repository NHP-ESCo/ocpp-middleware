package it.besmart.ocppLib.wrappers.Request;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.besmart.ocppLib.dto.IdTag;

public class AuthorizationStartRequest {
	
	@JsonProperty(value="session_id")
	private String sessionId;
	
	@JsonProperty(value="partner_session_id")
	private String partnerId;
	
	@JsonProperty(value="operator_id")
	private String operatorId;
	
	@JsonProperty(value="evse_id")
	private String evseId;
	
	@JsonProperty(value="id_tag")
	private IdTag idTag; 
	

	public AuthorizationStartRequest() {
		super();
	}

	public String getSessionId() {
		return sessionId;
	}

	public String getPartnerId() {
		return partnerId;
	}

	public String getOperatorId() {
		return operatorId;
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

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

	public void setEvseId(String evseId) {
		this.evseId = evseId;
	}

	public void setIdTag(IdTag idTag) {
		this.idTag = idTag;
	}

	@Override
	public String toString() {
		return "AuthorizationStartRequest [sessionId=" + sessionId + ", partnerId=" + partnerId + ", operatorId="
				+ operatorId + ", evseId=" + evseId + ", idTag=" + idTag + "]";
	}

}
