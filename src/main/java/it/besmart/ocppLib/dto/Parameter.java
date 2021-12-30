package it.besmart.ocppLib.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import it.besmart.ocppLib.enumeration.ParameterClassType;


public class Parameter {
	
	@NotNull
	private String name;
	
	private String description;

	private boolean editable; 
	
	private ParameterClassType type;
	
	@NotNull
	private String value; 
	
	private int minValue;
	
	private int maxValue;
	
	private List<ParameterOption> selectValues;


	public Parameter() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ParameterClassType getType() {
		return type;
	}

	public void setType(ParameterClassType type) {
		this.type = type;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}


	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getMinValue() {
		return minValue;
	}

	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Parameter [name=").append(name).append(", description=").append(description).append(", type=")
				.append(type).append(", value=").append(value).append(", minValue=").append(minValue)
				.append(", maxValue=").append(maxValue).append(", editable=").append(editable).append(", selectValues=")
				.append(selectValues).append("]");
		return builder.toString();
	}

	public List<ParameterOption> getSelectValues() {
		return selectValues;
	}

	public void setSelectValues(List<ParameterOption> selectValues) {
		this.selectValues = selectValues;
	}

	
	

}
