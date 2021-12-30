package it.besmart.ocppLib.enumeration;

public enum ConnectionPowerType {

	MONOPHASE("MONOPHASE"),
	TRIPHASE("TRIPHASE"),
	DC("DC");  
	
	private String value;

	public String getValue() {
		return value;
	}

	private ConnectionPowerType(String value) {
		this.value = value;
	}
}
