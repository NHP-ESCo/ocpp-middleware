package it.besmart.ocppLib.enumeration;

public enum StationType {
	
	DOMESTIC_3KW("DOMESTIC_3KW"),
	QUICK_22KW("QUICK_22KW"),
	FAST_50KW("FAST_50KW");

	private String stationType;

	public String getStationType() { return stationType; }

	public void setStationType(String stationType) { this.stationType = stationType; }

	private StationType(String stationType){this.stationType=stationType;}

	public static StationType fromPowerValue(double maxPower) {

		if(maxPower>=50)
			return StationType.FAST_50KW;
		if(maxPower>=22)
			return StationType.QUICK_22KW;
		
		return StationType.DOMESTIC_3KW;
	}

	public String getName() {
		switch(this) {
		case DOMESTIC_3KW:
			return "Wallbox";
		case FAST_50KW:
			return "Fast";
		case QUICK_22KW:
			return "Quick";
		default:
			return "";
		
		}
	}
}
