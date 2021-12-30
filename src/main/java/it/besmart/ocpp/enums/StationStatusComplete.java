package it.besmart.ocpp.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum StationStatusComplete {
	
	AVAILABLE("AVAILABLE"),
	UNAVAILABLE("UNAVAILABLE"),
	FAULTED("FAULTED");
	
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private StationStatusComplete(String value) {
		this.value = value;
	}

	
	@JsonCreator
    public static StationStatusComplete fromValue(String text) {
      for (StationStatusComplete b : StationStatusComplete.values()) {
        if (String.valueOf(b.value).equalsIgnoreCase(text)) {
          return b;
        }
      }
      return null;
    }

}
