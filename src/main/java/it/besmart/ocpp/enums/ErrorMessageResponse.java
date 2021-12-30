package it.besmart.ocpp.enums;

public enum ErrorMessageResponse {

	STATION_UNAVAILABLE("Charging station is out of service"),
	STATION_UNEXISTENT("Charging station does not exist"),
	STATION_IN_USE("Ongoing transaction on this charging station"),
	CONNECTOR_UNEXISTENT("Connector does not exist in this charging unit"),
	CONNECTOR_UNAVAILABLE("Connector is out of service or occupied in an other recharge"),
	CONNECTOR_IN_USE("Ongoing transaction on this connector"),
	UNIT_UNEXISTENT("Charging unit does not exist"),
	UNIT_UNAVAILABLE("Unit is out of service or occupied in an other recharge"),
	UNIT_FINISHING("Please remove the cable before starting a new recharge"),
	MODEL_UNEXISTENT("Requested model does not exist"),
	TX_DUPLICATED("Transaction with this sessionID already exists"),
	TX_UNEXISTENT("Transaction with this sessionID does not exist"),
	LOW_CONFIG_NETWORK("Configuration: Station did not reply to configuration request"),
	LOW_NETWORK("No reply. Station is unreachable"),
	LOST_NETWORK("Lost connection. Station is unreachable");   
	
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private ErrorMessageResponse(String value) {
		this.value = value;
	}
}
