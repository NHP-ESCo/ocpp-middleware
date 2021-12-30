package it.besmart.ocppLib.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum EndTxReason {

	LOCAL("Local"),
	REMOTE("Remote"),  					//remoteStop
	DEAUTHORIZED("DeAuthorized"),
	EVDISCONNECTED("EVDisconnected"),   // not only for Mode 2...
	
	EMERGENCYSTOP("EmergencyStop"),
	POWERLOSS("PowerLoss"),
	REBOOT("Reboot"),
	SOFTRESET("SoftReset"),
	HARDRESET("HardReset"),
	OTHER("Other"), //from ocpp
	UNLOCKCOMMAND("UnlockCommand"),
	Unknown("Unknown"), 
	
	EXPIRED("EXPIRED"),  //tx never actually started
	CANCELLED_RESERVATION("CANCELLED_RESERVATION"), 
	LACK_OF_NETWORK("LACK_OF_NETWORK"), 
	FAULT("FAULT"); 
	
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private EndTxReason(String value) {
		this.value = value;
	}
	
	@JsonCreator
    public static EndTxReason fromValue(String text) {
      for (EndTxReason b : EndTxReason.values()) {
        if (String.valueOf(b.value).equalsIgnoreCase(text)) {
          return b;
        }
      }
      return EndTxReason.Unknown;
    }

	public boolean isFault() {
		
		return this.equals(FAULT) || this.equals(POWERLOSS) || this.equals(REBOOT)
				|| this.equals(Unknown);
	}
	
}
