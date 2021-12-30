package it.besmart.ocpp.wrappers;

import it.besmart.ocpp.enums.AuthorizationResponse;

public class AuthWrapper {
	
	private AuthorizationResponse response;
	
	private String sessionId;
	
	private String description;
	
	public AuthWrapper(AuthorizationResponse res) {
		super();
		response = res;
		if(res.equals(AuthorizationResponse.BLOCKED))
			description = "Request rejected from the station";
	}

	public AuthorizationResponse getResponse() {
		return response;
	}

	public void setResponse(AuthorizationResponse response) {
		this.response = response;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}