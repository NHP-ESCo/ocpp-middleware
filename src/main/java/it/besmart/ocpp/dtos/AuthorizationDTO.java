package it.besmart.ocpp.dtos;

import java.time.ZonedDateTime;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import it.besmart.ocpp.enums.AuthorizationResponse;
import it.besmart.ocpp.enums.AuthorizationType;
import it.besmart.ocpp.model.Authorization;


public class AuthorizationDTO {
		
	private long authorizationID;
	
	protected String externalSession;
	
	private String csSession;
	
	protected String idTag;
	
	protected String evcoId; //parentId
	
	protected String providerId;
	
	protected ChargingStationDTO station;
	
	protected ChargingUnitDTO cu;
	
	@Enumerated(EnumType.STRING)
	private AuthorizationType type;
	
	@Enumerated(EnumType.STRING)
	private AuthorizationResponse response;
	
	protected ZonedDateTime timestamp;
	
	protected long scProfileId;
	
	public AuthorizationDTO() {
		super();
	}


	public AuthorizationDTO(Authorization auth) {
		this.authorizationID = auth.getAuthorizationID();
		this.type = auth.getType();
		this.response = auth.getResponse();
		this.timestamp = auth.getTimestamp();
		this.externalSession = auth.getExternalSession();
		this.csSession = auth.getCsSession();
		this.idTag = auth.getIdTag();
		this.providerId = auth.getProviderId();
		
	}




	public AuthorizationDTO(ReservationDTO res) {
		this.setCu( res.getCu() );
		this.setExternalSession( res.getExternalSession() );
		this.setProviderId( res.getProviderId() );
		this.setIdTag(res.getIdTag()); 
		this.setEvcoId(res.getEvcoId());
		
		this.setResponse(AuthorizationResponse.ACCEPTED);
	}


	public long getAuthorizationID() {
		return authorizationID;
	}


	public void setAuthorizationID(long authorizationID) {
		this.authorizationID = authorizationID;
	}


	public AuthorizationResponse getResponse() {
		return response;
	}


	public void setResponse(AuthorizationResponse response) {
		this.response = response;
	}


	public ZonedDateTime getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(ZonedDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getExternalSession() {
		return externalSession;
	}




	public void setExternalSession(String externalSession) {
		this.externalSession = externalSession;
	}




	public AuthorizationType getType() {
		return type;
	}




	public void setType(AuthorizationType type) {
		this.type = type;
	}




	public ChargingUnitDTO getCu() {
		return cu;
	}




	public void setCu(ChargingUnitDTO cu) {
		this.cu = cu;
	}


	public long getScProfileId() {
		return scProfileId;
	}


	public void setScProfileId(long scProfileId) {
		this.scProfileId = scProfileId;
	}


	public String getCsSession() {
		return csSession;
	}


	public void setCsSession(String csSession) {
		this.csSession = csSession;
	}


	public String getIdTag() {
		return idTag;
	}


	public void setIdTag(String idTag) {
		this.idTag = idTag;
	}


	public ChargingStationDTO getStation() {
		return station;
	}


	public void setStation(ChargingStationDTO station) {
		this.station = station;
	}


	public String getProviderId() {
		return providerId;
	}


	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}


	public String getEvcoId() {
		return evcoId;
	}


	public void setEvcoId(String evcoId) {
		this.evcoId = evcoId;
	}
	
	
}