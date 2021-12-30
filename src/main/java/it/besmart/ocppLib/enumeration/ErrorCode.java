package it.besmart.ocppLib.enumeration;

public enum ErrorCode {
	
	CONFIGURATION_ERROR("CONFIGURATION_ERROR"),
	FIRMWARE_ERROR("FIRMWARE_ERROR"),
	STATION_ERROR("STATION_ERROR"),
	RECHARGE_ERROR("RECHARGE_ERROR"),
	NETWORK_ERROR("NETWORK_ERROR");  
	
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private ErrorCode(String value) {
		this.value = value;
	}
	
	
	public static ErrorCode fromValue(String text) {
		
	      for (ErrorCode b : ErrorCode.values()) {
	        if ( b.getValue().equalsIgnoreCase(text)) {
	          return b;
	        }
	      }
	      return null;
	}

}
