package it.besmart.ocpp.enums;

public enum AuthorizationType {

	LOCAL("LOCAL"),
	REMOTE("REMOTE");
	
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private AuthorizationType(String value) {
		this.value = value;
	}
	
}
