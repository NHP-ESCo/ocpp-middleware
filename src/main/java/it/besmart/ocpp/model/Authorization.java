package it.besmart.ocpp.model;

import java.time.ZonedDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import it.besmart.ocpp.dtos.AuthorizationDTO;
import it.besmart.ocpp.enums.AuthorizationResponse;
import it.besmart.ocpp.enums.AuthorizationType;

@Entity
public class Authorization {
		
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "z_authorization_generator")
	@SequenceGenerator(name="z_authorization_generator", sequenceName = "z_authorization_seq", allocationSize=1)
	private long authorizationID;
	
	private String externalSession;
	
	private String csSession;
	
	private String idTag;
	
	private String evcoId; //parentId
	
	private String providerId;
	
	@ManyToOne
	private ChargingUnit cu;  
	
	@Enumerated(EnumType.STRING)
	private AuthorizationType type;
	
	@ManyToOne
	private Reservation reservation;
	
	@Enumerated(EnumType.STRING)
	private AuthorizationResponse response;
	
	private ZonedDateTime timestamp;
	
	private boolean freeMode;
	
	@ManyToOne
	private StoredLog endFault;
	
	public Authorization() {
		super();
		this.timestamp = ZonedDateTime.now();
	}


	public Authorization(AuthorizationDTO auth) {
		this.type = auth.getType();
		this.response = auth.getResponse();
		if(auth.getTimestamp()!=null)
			this.timestamp = auth.getTimestamp();
		else
			this.timestamp = ZonedDateTime.now();
		this.csSession = auth.getCsSession();
		this.idTag = auth.getIdTag();
		this.evcoId = auth.getEvcoId();
		this.providerId = auth.getProviderId();
		this.externalSession = auth.getExternalSession();
		
	}

	public Reservation getReservation() {
		return reservation;
	}


	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
	}


	public ChargingUnit getCu() {
		return cu;
	}


	public void setCu(ChargingUnit cu) {
		this.cu = cu;
	}


	public long getAuthorizationID() {
		return authorizationID;
	}
	

	public ZonedDateTime getTimestamp() {
		return timestamp;
	}

	public AuthorizationResponse getResponse() {
		return response;
	}

	public void setResponse(AuthorizationResponse response) {
		this.response = response;
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


	@Override
	public String toString() {
		return "Authorization [authorizationID=" + authorizationID + ", externalSession=" + externalSession
				+ ", type=" + type + ", response=" + response + ", timestamp="
				+ timestamp + "]";
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


	public void setTimestamp(ZonedDateTime timestamp) {
		this.timestamp = timestamp;
	}


	public String getProviderId() {
		return providerId;
	}


	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (authorizationID ^ (authorizationID >>> 32));
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Authorization other = (Authorization) obj;
		if (authorizationID != other.authorizationID)
			return false;
		return true;
	}


	public String getEvcoId() {
		return evcoId;
	}


	public void setEvcoId(String evcoId) {
		this.evcoId = evcoId;
	}


	public boolean isFreeMode() {
		return freeMode;
	}


	public void setFreeMode(boolean freeMode) {
		this.freeMode = freeMode;
	}


	public StoredLog getEndFault() {
		return endFault;
	}


	public void setEndFault(StoredLog endFault) {
		this.endFault = endFault;
	}

}
