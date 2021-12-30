package it.besmart.ocppLib.enumeration;

public enum ProtocolVersion {
	// implemented protocols received in the Sec-Websocket-Protocol in the opening Handshake  
	
	
	OCPPJ16("OCPPJ16"),
	OCPPS16("OCPPS16"),
	OCPP15("OCPP15"),
	OCPP20("OCPP20");
	
	
	private String value;

	public String getValue() {
		return value;
	}

	private ProtocolVersion(String value) {
		this.value = value;
	}
	
	 public static ProtocolVersion fromValue(String text) {
      for (ProtocolVersion b : ProtocolVersion.values()) {
        if (String.valueOf(b.value).equalsIgnoreCase(text)) {
          return b;
        }
      }
      return null;
    }
}
