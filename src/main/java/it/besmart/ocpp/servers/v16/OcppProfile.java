package it.besmart.ocpp.servers.v16;

public enum OcppProfile {

	CORE("Core"),
	FIRMWARE("FirmwareManagement"),
	LOCAL_LIST("LocalAuthListManagement"),
	RESERVATION("Reservation"),
	TRIGGER("RemoteTrigger"),
	SMART_CHARGING("SmartCharging");
	
	
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private OcppProfile(String value) {
		this.value = value;
	}

}
