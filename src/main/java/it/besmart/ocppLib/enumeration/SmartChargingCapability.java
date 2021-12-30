package it.besmart.ocppLib.enumeration;

public enum SmartChargingCapability {

	Enabled("Enabled"),
	Disabled("Disabled"),
	Internal("Internal"),
	Unsupported("Unsupported");
	
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private SmartChargingCapability(String value) {
		this.value = value;
	}

	public boolean isEnabled() {
		return this.equals(Enabled);
	}

}
