package it.besmart.ocppLib.enumeration;

public enum PlugType {
	
	SCHUKO("SCHUKO"),
	TYPE2_OUTLET("TYPE2_OUTLET"),
	TYPE2_CABLE("TYPE2_CABLE"),
	TYPE3A("TYPE3A"),
	TYPE3C("TYPE3C"),
	CHADEMO("CHADEMO"),
	CCS("CCS"), 
	Unspecified("Unspecified");  
	
	private String value;

	public String getValue() {
		return value;
	}

	private PlugType(String value) {
		this.value = value;
	}

	public boolean isDC() {
		
		return this.equals(CCS) || this.equals(CHADEMO);
	}


	public PlugPowerType getPlugPowerType() {
		switch(this) {
		case CCS:
			return PlugPowerType.CCS;
		case CHADEMO: 
			return PlugPowerType.DC;
		case TYPE2_CABLE:
		case TYPE2_OUTLET:
			return PlugPowerType.AC;
		case SCHUKO:
		case TYPE3A:
		case TYPE3C:
			return PlugPowerType.AC_1_PHASE;
		default:
			return null;
		}
	}

	public ChargingMode getMode() {
		switch(this) {
		case CCS:
		case CHADEMO: 
			return ChargingMode.MODE_4;
		case TYPE2_CABLE:
		case TYPE2_OUTLET:
			return ChargingMode.MODE_3;
		case SCHUKO:
			return ChargingMode.MODE_1;
		case TYPE3A:
		case TYPE3C:
			return ChargingMode.MODE_2;
		default:
			return null;
		}
	}
}
