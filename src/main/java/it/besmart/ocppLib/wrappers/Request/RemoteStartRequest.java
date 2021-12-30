package it.besmart.ocppLib.wrappers.Request;

import javax.validation.constraints.NotEmpty;

public class RemoteStartRequest {

	@NotEmpty
	private String sessionId;
	
	@NotEmpty
	private String cuEvse;
	
	@NotEmpty
	private String parentIdTag; //unique for the customer
	
	private String providerCode;
	
	private String rechargeProfileCode;
	

	public RemoteStartRequest() {
		super();
	}
	
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getCuEvse() {
		return cuEvse;
	}

	public void setCuEvse(String cuEvse) {
		this.cuEvse = cuEvse;
	}

	public String getProviderCode() {
		return providerCode;
	}

	public void setProviderCode(String providerCode) {
		this.providerCode = providerCode;
	}

	public String getRechargeProfileCode() {
		return rechargeProfileCode;
	}

	public void setRechargeProfileCode(String rechargeProfileCode) {
		this.rechargeProfileCode = rechargeProfileCode;
	}

	@Override
	public String toString() {
		return "RemoteStartRequest [sessionId=" + sessionId + ", cuEvse=" + cuEvse 
				+ ", parentIdTag=" + parentIdTag + ", rechargeProfileCode=" + rechargeProfileCode + "]";
	}


	public String getParentIdTag() {
		return parentIdTag;
	}

	public void setParentIdTag(String parentIdTag) {
		this.parentIdTag = parentIdTag;
	}
	

}
