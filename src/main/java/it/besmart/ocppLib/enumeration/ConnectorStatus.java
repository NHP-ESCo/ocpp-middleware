package it.besmart.ocppLib.enumeration;

public enum ConnectorStatus {

	AVAILABLE("AVAILABLE"),
	OCCUPIED("OCCUPIED"),
	RESERVED("RESERVED"),
	OUTOFSERVICE("OUTOFSERVICE"); 
	
	
	private String value;

	public String getValue() {
		return value;
	}

	private ConnectorStatus(String value) {
		this.value = value;
	}
}
