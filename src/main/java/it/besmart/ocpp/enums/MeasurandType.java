package it.besmart.ocpp.enums;

public enum MeasurandType {

	
	CURRENT("CURRENT"),  		// A
	ENERGY("ENERGY"),	 		// kWh (or Wh) if active
	ENERGY_REACTIVE("ENERGY_REACTIVE"),
	POWER("POWER"),      		// kW (or W) if active
	POWER_REACTIVE("POWER_REACTIVE"),
	VOLTAGE("VOLTAGE"),			// V
	TEMPERATURE("TEMPERATURE"), // K (or F, C) 
	FREQUENCY("FREQUENCY"),		// Hz
	RPM("RPM"),					
	SOC("SOC"),					// %
	UNKNOWN("UNKNOWN");
	
	
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private MeasurandType(String value) {
		this.value = value;
	}
	
	
  public static MeasurandType fromValue(String text) {
      for (MeasurandType b : MeasurandType.values()) {
        if (String.valueOf(b.value).equalsIgnoreCase(text)) {
          return b;
        }
      }
      return MeasurandType.UNKNOWN;
    }

}
