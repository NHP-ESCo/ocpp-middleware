package it.besmart.ocppLib.enumeration;

public enum CapabilityStatus {

	Enabled("Enabled"),
	Disabled("Disabled"),
	Unsupported("Unsupported");
	
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private CapabilityStatus(String value) {
		this.value = value;
	}

	public boolean isEnabled() {
		return this.equals(Enabled);
	}

}
