package it.besmart.ocppLib.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum IdTagType {
	
	  RFID("RFID"),
	  
	  UID("UID"),
	  
	  CONTRACT_ID("CONTRACT_ID");
	
	  private String value;
	
	  private IdTagType(String value) {
	    this.value = value;
	  }
	  
	  @JsonValue
	  public String getValue() {
			return value;
	  }
	  
	  @JsonCreator
	    public static IdTagType fromValue(String text) {
	      for (IdTagType b : IdTagType.values()) {
	        if (String.valueOf(b.value).equals(text)) {
	          return b;
	        }
	      }
	      return null;
	    }
}
