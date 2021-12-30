package it.besmart.ocpp.model;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import it.besmart.ocpp.enums.ParamDefinitionType;
import it.besmart.ocppLib.dto.config.ParameterKey;
import it.besmart.ocppLib.enumeration.ParameterClassType;


//Configuration parameters related either to protocol or to model

@MappedSuperclass //sub-classes will be entities which inherit fields of this class
public abstract class ParamType {
	
	@NotNull
	@NotEmpty
	protected String name;
	
	protected String defaultValue;

	@NotNull
	@Enumerated(EnumType.STRING)
	protected ParameterClassType type;
	
	protected boolean editable; // write option
	
	protected boolean visible; 
	
	protected boolean autoconfigured;  //for unknown parameters: editable but set by station
	
	@Column(columnDefinition="TEXT")
	protected String description;
	
	protected int minValue;
	
	protected int maxValue;
	

	public ParamType(ParameterKey p) {
		super();
		setProperties(p);
	}

	public ParamType() {
		super();
	}

	public void setProperties(ParameterKey p) {
		this.autoconfigured = p.isAutoconfigured();
		this.defaultValue = p.getDefaultValue();
		this.description = p.getDescription();
		this.editable = p.isEditable();
		this.maxValue = p.getMaxValue();
		this.minValue = p.getMinValue();
		this.name = p.getName();
		this.type = p.getType();
		this.visible = p.isVisible();
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


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	public ParameterClassType getType() {
		return type;
	}


	public void setType(ParameterClassType type) {
		this.type = type;
	}


	public String getDefaultValue() {
		return defaultValue;
	}


	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
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


	public static Object convertParamValue(String value, ParameterClassType type) {
		Object realValue = null;
		
		if(value==null)
			return null;
		
		switch(type) {
		case Boolean:
			realValue = Boolean.valueOf( value );
			break;
		case Integer:
			realValue = Integer.valueOf( value );
			break;
		default:
			realValue = value;
			break;
		}
		
		return realValue;
	}


	public boolean isSelectable() {
		
		return this.type.equals(ParameterClassType.Select) || this.type.equals(ParameterClassType.MultiSelect);
	}
	
	public abstract ParamDefinitionType getDefType();
	
}
