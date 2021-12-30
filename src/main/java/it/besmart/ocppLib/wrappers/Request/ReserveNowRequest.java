package it.besmart.ocppLib.wrappers.Request;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class ReserveNowRequest {

	@NotEmpty
	private String cuEvse;
	
	@NotEmpty
	private String providerCode;
	
	@NotEmpty
	private String sessionId;
	
	private int connector;
	
	@NotEmpty
	private String parentIdTag;
	
	@NotNull
	private ZonedDateTime expiryDate;
	
	private ZonedDateTime startDate;

	
	public String getCuEvse() {
		return cuEvse;
	}

	public void setCuEvse(String cuEvse) {
		this.cuEvse = cuEvse;
	}

	public int getConnector() {
		return connector;
	}

	public void setConnector(int connector) {
		this.connector = connector;
	}

	public ZonedDateTime getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(ZonedDateTime expiryDate) {
		this.expiryDate = expiryDate;
	}

	public ZonedDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(ZonedDateTime startDate) {
		this.startDate = startDate;
	}

	public String getProviderCode() {
		return providerCode;
	}

	public void setProviderCode(String providerCode) {
		this.providerCode = providerCode;
	}

	@Override
	public String toString() {
		return "ReserveNowRequest [cuEvse=" + cuEvse + ", providerCode=" + providerCode + ", connector=" + connector
				+ ", expiryDate=" + expiryDate + ", startDate=" + startDate + "]";
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getParentIdTag() {
		return parentIdTag;
	}

	public void setParentIdTag(String parentIdTag) {
		this.parentIdTag = parentIdTag;
	}
	
}
