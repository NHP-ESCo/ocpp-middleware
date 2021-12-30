package it.besmart.ocppLib.enumeration;

public enum ChargingMode {

	MODE_1("MODE_1"),

	MODE_2("MODE_2"),

	MODE_3("MODE_3"),

	MODE_4("MODE_4");
	
	private String value;

	private ChargingMode(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	

}
