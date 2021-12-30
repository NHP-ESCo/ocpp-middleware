package it.besmart.ocppLib.enumeration;

public enum PlugPowerType {
	
	AC_1_PHASE("AC_1_PHASE"),
	AC_3_PHASE("AC_3_PHASE"),
	DC("DC"),
	AC("AC"),
	CCS("CCS");  // AC1/AC3/DC plug
	
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private PlugPowerType(String value) {
		this.value = value;
	}
}
