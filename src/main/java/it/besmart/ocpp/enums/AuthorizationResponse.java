package it.besmart.ocpp.enums;

public enum AuthorizationResponse {
	
	ACCEPTED("ACCEPTED"),
	BLOCKED("BLOCKED"), //blocked by station (remote)
	INVALID("INVALID"), //blocked by provider (local)
	EXPIRED("EXPIRED"), //customer expired (? ocpp)
	//extra to ocpp
	STARTED("STARTED"), //tx started
	ABORTED("ABORTED"); //accepted but expired
	
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private AuthorizationResponse(String value) {
		this.value = value;
	}
	
}
