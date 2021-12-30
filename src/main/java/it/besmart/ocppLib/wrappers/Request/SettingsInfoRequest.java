package it.besmart.ocppLib.wrappers.Request;

import it.besmart.ocppLib.enumeration.ConnectionPowerType;
import it.besmart.ocppLib.enumeration.ProtocolVersion;

import javax.validation.constraints.NotNull;


public class SettingsInfoRequest {

	@NotNull
	private String modelCode; //external ref
	
	@NotNull
	private ConnectionPowerType powerType;

	@NotNull
	private ProtocolVersion protocol;
	
	
	public SettingsInfoRequest() {
		super();
	}


	public ConnectionPowerType getPowerType() {
		return powerType;
	}

	public void setPowerType(ConnectionPowerType powerType) {
		this.powerType = powerType;
	}


	public String getModelCode() {
		return modelCode;
	}


	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}


	public ProtocolVersion getProtocol() {
		return protocol;
	}


	public void setProtocol(ProtocolVersion protocol) {
		this.protocol = protocol;
	}


	@Override
	public String toString() {
		return "SettingsInfoRequest [modelCode=" + modelCode + ", powerType=" + powerType + ", protocol=" + protocol
				+ "]";
	}
	
	
}
