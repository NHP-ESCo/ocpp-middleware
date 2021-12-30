package it.besmart.ocppLib.enumeration;

public enum PlayerType {

	CPO("CPO"),
	EMP("EMP");
	
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private PlayerType(String value) {
		this.value = value;
	}
}
