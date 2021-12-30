package it.besmart.ocpp.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import it.besmart.ocpp.enums.ParamDefinitionType;
import it.besmart.ocppLib.dto.config.ParameterKey;
import it.besmart.ocppLib.enumeration.ProtocolVersion;

@Entity
public class ProtocolParamType extends ParamType {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "z_protocolparamtype_generator")
	@SequenceGenerator(name="z_protocolparamtype_generator", sequenceName = "z_protocolparamtype_seq", allocationSize=1)
	private long paramID;

	@Enumerated(EnumType.STRING)
	private ProtocolVersion protocol;
	
	private boolean required;  //only descriptive

	private boolean modelSpecific;
	
	public ProtocolParamType() {
		super();
	}
	
	public ProtocolParamType(ParameterKey param) {
		super(param);
	}

	public long getParamID() {
		return paramID;
	}

	public ProtocolVersion getProtocol() {
		return protocol;
	}

	public void setProtocol(ProtocolVersion protocol) {
		this.protocol = protocol;
	}
	
	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	@Override
	public String toString() {
		return "ProtocolParamType [name=" + name + ", protocol=" + protocol + ",  type=" + type
				+ ", editable=" + editable + ", visible=" + visible + ", required=" + required + "]";
	}

	public boolean isModelSpecific() {
		return modelSpecific;
	}

	public void setModelSpecific(boolean modelSpecific) {
		this.modelSpecific = modelSpecific;
	}

	@Override
	public ParamDefinitionType getDefType() {
		return ParamDefinitionType.Protocol;
	}
	
}
