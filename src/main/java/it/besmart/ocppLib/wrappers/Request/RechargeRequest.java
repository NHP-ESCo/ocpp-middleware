package it.besmart.ocppLib.wrappers.Request;

import javax.validation.constraints.NotEmpty;

public class RechargeRequest {

	@NotEmpty
	private String sessionId;
	
	
	private String roamingUserCode;
	

	public RechargeRequest() {
		super();
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	
	public String getRoamingUserCode() {
		return roamingUserCode;
	}

	public void setRoamingUserCode(String roamingUserCode) {
		this.roamingUserCode = roamingUserCode;
	}

	@Override
	public String toString() {
		return "RechargeRequest [sessionId=" + sessionId + ", roamingUserCode=" + roamingUserCode + "]";
	}
	
	
}
