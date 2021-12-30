package it.besmart.ocppLib.dto;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import it.besmart.ocppLib.enumeration.ProtocolVersion;

@JsonInclude(Include.NON_NULL)
public class StationData {
	
	@NotEmpty
	@NotNull 
	private String operatorCode;
	
	//TODO: validate sn e identifier
	@NotEmpty
	@NotNull  
	private String serialNumber; 
	
	@NotEmpty
	@NotNull  
	private String identifier; //Unique for CPO,Station
	
	@NotNull
	private String modelCode; 
	
	@NotNull
	private ProtocolVersion protocol;


	private StationStates states; //ONLY READ
	
	@Valid
	@NotNull
	private StationSettings settings; //WRITE SETTINGS
	
	
	public StationStates getStates() {
		return states;
	}

	public void setStates(StationStates states) {
		this.states = states;
	}

	public StationSettings getSettings() {
		return settings;
	}

	public void setSettings(StationSettings settings) {
		this.settings = settings;
	}
	
	public StationData() {
		super();
	}
	
	@Override
	public String toString() {
		return "StationData [operatorCode=" + operatorCode + ", serialNumber=" + serialNumber + ", identifier="
				+ identifier + ", modelCode=" + modelCode + ", protocol=" + protocol + ", states=" + states
				+ ", settings=" + settings + "]";
	}
	

	public ProtocolVersion getProtocol() {
		return protocol;
	}

	public void setProtocol(ProtocolVersion protocol) {
		this.protocol = protocol;
	}
	
	
	public String getSerialNumber() {
		return serialNumber;
	}


	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}


	public String getOperatorCode() {
		return operatorCode;
	}


	public void setOperatorCode(String operatorCode) {
		this.operatorCode = operatorCode;
	}

	public String getModelCode() {
		return modelCode;
	}

	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

}
