package it.besmart.ocppLib.dto;

public class ParameterOption {

	private String optionName;
	
	private String optionValue;
	
	public ParameterOption() {
		super();
	}

	public ParameterOption(String value) {
		this.optionValue = value;
		this.optionName = value;
	}

	public String getOptionName() {
		return optionName;
	}

	public void setOptionName(String optionName) {
		this.optionName = optionName;
	}

	public String getOptionValue() {
		return optionValue;
	}

	public void setOptionValue(String optionValue) {
		this.optionValue = optionValue;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ParameterOption [optionName=").append(optionName).append(", optionValue=").append(optionValue)
				.append("]");
		return builder.toString();
	}
	
}
