package it.besmart.ocppLib.wrappers.Response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;


public class AuthorizationStartResponse {
	
	@JsonProperty(value="session_id")
	private String sessionId;
	
	@JsonProperty(value="evco_id")
	private String evcoId;
	
	@JsonProperty(value="partner_session_id")
	private String partnerSessionId;
	
	@JsonProperty(value="provider_id")
	private String providerId;
	
	@JsonProperty(value="authorization_status")
	private AuthorizationStatusEnum status;
	
	@JsonProperty(value="smart_charging_enabled")
	private boolean smartCharging;
	
	private String profileCode; 

	private Long reservationId; // related to reservation
	
	public AuthorizationStartResponse() {
		super();
		this.smartCharging = false;
		
	}

	public String getSessionId() {
		return sessionId;
	}

	public String getPartnerSessionId() {
		return partnerSessionId;
	}

	public AuthorizationStatusEnum getStatus() {
		return status;
	}

	public boolean isSmartCharging() {
		return smartCharging;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public void setPartnerSessionId(String partnerSessionId) {
		this.partnerSessionId = partnerSessionId;
	}

	public void setStatus(AuthorizationStatusEnum status) {
		this.status = status;
	}

	public void setSmartCharging(boolean smartCharging) {
		this.smartCharging = smartCharging;
	}
	

	public String getProfileCode() {
		return profileCode;
	}

	public void setProfileCode(String profileCode) {
		this.profileCode = profileCode;
	}


	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}


	public enum AuthorizationStatusEnum {
	  
	    AUTHORIZED("AUTHORIZED"),
	    
	    NOTAUTHORIZED("NOTAUTHORIZED");

	    private String value;

	    AuthorizationStatusEnum(String value) {
	      this.value = value;
	    }

	    @JsonValue
	    public String getValue() {
	      return value;
	    }

	    @Override
	    public String toString() {
	      return String.valueOf(value);
	    }

	    @JsonCreator
	    public static AuthorizationStatusEnum fromValue(String text) {
	      for (AuthorizationStatusEnum b : AuthorizationStatusEnum.values()) {
	        if (String.valueOf(b.value).equals(text)) {
	          return b;
	        }
	      }
	      return null;
	    }
	  }


	@Override
	public String toString() {
		return "AuthorizationStartResponse [sessionId=" + sessionId + ", partnerSessionId=" + partnerSessionId
				 + ", status=" + status + ", smartCharging=" + smartCharging
				+ ", profileCode=" + profileCode + "]";
	}

	public String getEvcoId() {
		return evcoId;
	}

	public void setEvcoId(String evcoId) {
		this.evcoId = evcoId;
	}

	public Long getReservationId() {
		return reservationId;
	}

	public void setReservationId(Long reservationId) {
		this.reservationId = reservationId;
	}

}
