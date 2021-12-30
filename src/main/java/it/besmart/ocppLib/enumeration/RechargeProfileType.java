package it.besmart.ocppLib.enumeration;

public enum RechargeProfileType {
	
	PREMIUM("PREMIUM"),
	FLEXIBLE("FLEXIBLE");
	
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private RechargeProfileType(String value) {
		this.value = value;
	}
}
