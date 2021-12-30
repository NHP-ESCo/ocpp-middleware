package it.besmart.ocpp.enums;

public enum ReserveStatus {

	WAITING("WAITING"),
	//ocpp response
	ACCEPTED("ACCEPTED"),
	REJECTED("REJECTED"),  //because occupied/faulted/unavailable or ReserveConnectorZeroSupported (always reserve a connector)
	
	//second stage
	ABORTED("ABORTED"),
	CANCELED("CANCELED"),
	EXPIRED("EXPIRED"),
	STARTED("STARTED");
	
	
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private ReserveStatus(String value) {
		this.value = value;
	}
}
