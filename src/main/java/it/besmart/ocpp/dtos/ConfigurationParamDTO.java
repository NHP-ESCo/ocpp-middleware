package it.besmart.ocpp.dtos;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import it.besmart.ocpp.model.ConfigurationParam;
import it.besmart.ocpp.model.ParamType;
import it.besmart.ocppLib.dto.Parameter;
import it.besmart.ocppLib.dto.config.ParameterKey;

public class ConfigurationParamDTO extends ParameterKey {

	@JsonIgnore
	private long paramId;
	
	@NotNull
	private String value;
	

	public ConfigurationParamDTO() {
		super();
	}
	
	public ConfigurationParamDTO(ConfigurationParam p) {
		ParamType param = p.getParam();
		this.name = param.getName();
		this.type = param.getType();
		this.editable = param.isEditable();
		this.visible = param.isVisible();
		this.defaultValue = param.getDefaultValue();
		
		this.paramId = p.getParamId();
		this.value = p.getValue();
		
	}


	public ConfigurationParamDTO(Parameter param) {
		
		this.name = param.getName();
		this.value = param.getValue();
	}

	
	public long getParamId() {
		return paramId;
	}


	public void setParamId(long paramId) {
		this.paramId = paramId;
	}


	public String getValue() {
		return value;
	}


	@Override
	public String toString() {
		return "ConfigurationParamDTO [paramId=" + paramId + ", value=" + value + ", name="
				+ name + ", type=" + type + ", editable=" + editable + ", visible=" + visible + ", defaultValue=" + defaultValue + "]";
	}

}
