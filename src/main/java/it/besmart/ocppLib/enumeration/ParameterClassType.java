package it.besmart.ocppLib.enumeration;

public enum ParameterClassType {
	
	String("String"),
	Boolean("Boolean"),
	Integer("Integer"),
	Select("Select"),
	MultiSelect("MultiSelect"), //Comma separated list
	Unknown("Unknown");  
	
	
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private ParameterClassType(String value) {
		this.value = value;
	}
	
	public static ParameterClassType fromValue(String text) {
		
      for (ParameterClassType b : ParameterClassType.values()) {
        if ( b.getValue().equalsIgnoreCase(text)) {
          return b;
        }
      }
      return null;
    }

}
