package it.besmart.ocppLib.enumeration;

public enum UnitStatus {

	AVAILABLE("AVAILABLE"),
	PARTIALLY_AVAILABLE("PARTIALLY_AVAILABLE"),
	OCCUPIED("OCCUPIED"),
	RESERVED("RESERVED"),
	OUTOFSERVICE("OUTOFSERVICE"); 
	
	
	private String value;

	public String getValue() {
		return value;
	}

	private UnitStatus(String value) {
		this.value = value;
	}
}
