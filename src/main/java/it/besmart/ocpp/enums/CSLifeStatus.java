package it.besmart.ocpp.enums;

import it.besmart.ocppLib.enumeration.StationLifeStatus;

public enum CSLifeStatus {

	INSTALLED("INSTALLED"),
	FIRST_CONFIGURATION("FIRST_CONFIGURATION"),
	TO_CONFIGURE("TO_CONFIGURE"),
	ACTIVE("ACTIVE"),
	DISMISSED("DISMISSED");
	
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private CSLifeStatus(String value) {
		this.value = value;
	}
	
	
	// INSTALLED/ACTIVE/DISMISSED
	public StationLifeStatus simpleStatus() {
		if(this==CSLifeStatus.FIRST_CONFIGURATION || this==CSLifeStatus.INSTALLED)
			return StationLifeStatus.INSTALLED;
		else if (this==CSLifeStatus.DISMISSED)
			return StationLifeStatus.DISMISSED;
		else 
			return StationLifeStatus.ACTIVE;
	}

	public boolean isInConfiguration() {
		if (this.equals(FIRST_CONFIGURATION) || this.equals(TO_CONFIGURE))
			return true;
		
		return false;
	}
	
	
}
