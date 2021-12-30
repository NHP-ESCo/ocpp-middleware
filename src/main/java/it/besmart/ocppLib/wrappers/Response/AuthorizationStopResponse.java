package it.besmart.ocppLib.wrappers.Response;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.besmart.ocppLib.wrappers.Response.AuthorizationStartResponse.AuthorizationStatusEnum;

public class AuthorizationStopResponse {

	@JsonProperty(value="authorization_status")
	private AuthorizationStatusEnum status;

	public AuthorizationStopResponse() {
		this.status = AuthorizationStatusEnum.NOTAUTHORIZED;
	}

	public AuthorizationStatusEnum getStatus() {
		return status;
	}

	public void setStatus(AuthorizationStatusEnum status) {
		this.status = status;
	}
	
	
}
