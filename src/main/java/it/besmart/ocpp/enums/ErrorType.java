package it.besmart.ocpp.enums;

public enum ErrorType {

	FAULTED_COMMISSIONING("FAULTED_COMMISSIONING"),
	CONFIGURATION_ERROR("CONFIGURATION_ERROR"),
	FIRMWARE_ERROR("FIRMWARE_ERROR"),
	STATION_ERROR("STATION_ERROR"),
	DATA_TRANSFER("DATA_TRANSFER"), 
	PROTOCOL_ERROR("PROTOCOL_ERROR"),
	SMART_CHARGING("SMART_CHARGING"),
	CUSTOMER_CONFLICT("CUSTOMER_CONFLICT"),
	NETWORK_ERROR("NETWORK_ERROR"),
	RECHARGE_ERROR("RECHARGE_ERROR");  
	
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private ErrorType(String value) {
		this.value = value;
	}
	
}
