package it.besmart.ocpp.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import it.besmart.ocppLib.enumeration.ConnectorStatus;

public enum ConnectorStatusComplete {

	AVAILABLE("AVAILABLE"),
	PREPARING("PREPARING"),
	CHARGING("CHARGING"),
	SUSPENDEDEV("SUSPENDEDEV"),
	SUSPENDEDEVSE("SUSPENDEDEVSE"),
	FINISHING("FINISHING"),
	RESERVED("RESERVED"),
	UNAVAILABLE("UNAVAILABLE"),
	FAULTED("FAULTED"),
	OCCUPIED("OCCUPIED"); 
	
	
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private ConnectorStatusComplete(String value) {
		this.value = value;
	}
	
	
	public ConnectorStatus simpleStatus() {
		 
		if(this==ConnectorStatusComplete.CHARGING  || this==ConnectorStatusComplete.OCCUPIED
				 || this==ConnectorStatusComplete.SUSPENDEDEV || this==ConnectorStatusComplete.SUSPENDEDEVSE )
			return ConnectorStatus.OCCUPIED;
		else if (this==ConnectorStatusComplete.RESERVED)
			return ConnectorStatus.RESERVED;
		else if (this==ConnectorStatusComplete.AVAILABLE || this==ConnectorStatusComplete.PREPARING 
				|| this==ConnectorStatusComplete.FINISHING)
			return ConnectorStatus.AVAILABLE;
		else
			return ConnectorStatus.OUTOFSERVICE;
	}
	
	@JsonCreator
    public static ConnectorStatusComplete fromValue(String text) {
      for (ConnectorStatusComplete b : ConnectorStatusComplete.values()) {
        if (String.valueOf(b.value).equalsIgnoreCase(text)) {
          return b;
        }
      }
      return null;
    }

	public boolean isStandby() {
		
		return this.equals(SUSPENDEDEV) || this.equals(SUSPENDEDEVSE);
	}
	
}
