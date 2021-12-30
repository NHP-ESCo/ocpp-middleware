package it.besmart.ocppLib.enumeration;


public enum StationLifeStatus {
	
	INSTALLED("INSTALLED"),
	DISMISSED("DISMISSED"),
	ACTIVE("ACTIVE");


	private String value;

	public String getValue() {
		return value;
	}

	private StationLifeStatus(String value) {
		this.value = value;
	}
	
	public static StationLifeStatus fromValue(String text) {
	      for (StationLifeStatus b : StationLifeStatus.values()) {
	        if ( b.getValue().equalsIgnoreCase(text)) {
	          return b;
	        }
	      }
	      return null;
	}
}
