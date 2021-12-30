package it.besmart.ocpp.enums;

public enum ParamDefinitionType {

	Model("Model"),
	Protocol("Protocol"),
	Basic("Basic");
	
	
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private ParamDefinitionType(String value) {
		this.value = value;
	}
}
