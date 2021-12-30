package it.besmart.ocppLib.dto.config;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import it.besmart.ocppLib.dto.ParameterOption;
import it.besmart.ocppLib.enumeration.ParameterClassType;


public class ParameterKey {
	
	@JsonIgnore
	private long paramID;
	
	@NotNull
	protected String name;
	
	protected String description;
	
	@NotNull
	protected ParameterClassType type;
	
	protected boolean editable; // write option
	
	protected boolean visible; //if can be modified by cpo
	
	protected boolean autoconfigured; //value set by station
	
	protected String defaultValue;

	
	private int minValue;
	
	private int maxValue;
	
	private List<ParameterOption> selectValues;


	public ParameterKey() {
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
	
	public long getParamID() {
		return paramID;
	}

	
	public boolean isVisible() {
		return visible;
	}


	public void setVisible(boolean visible) {
		this.visible = visible;
	}


	public boolean isAutoconfigured() {
		return autoconfigured;
	}


	public void setAutoconfigured(boolean autoconfigured) {
		this.autoconfigured = autoconfigured;
	}


	public String getDefaultValue() {
		return defaultValue;
	}


	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}


	@Override
	public String toString() {
		return "{name=" + name + ", type=" + type
				+ ", editable=" + editable + ", visible=" + visible + "}";
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

	public List<ParameterOption> getSelectValues() {
		return selectValues;
	}

	public void setSelectValues(List<ParameterOption> selectValues) {
		this.selectValues = selectValues;
	}

	public void setParamID(long paramID) {
		this.paramID = paramID;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	

}
