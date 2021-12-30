package it.besmart.ocppLib.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import it.besmart.ocppLib.enumeration.CapabilityType;
import it.besmart.ocppLib.enumeration.ConnectionPowerType;
import it.besmart.ocppLib.enumeration.StationType;

@JsonInclude(Include.NON_NULL)
public class StationSettings { //editable data

	private String evseID; //OICP: operatorCode*serialNumber
		
	private String name; //station name
	
	@NotNull
	private ConnectionPowerType powerType; 
	
	@Valid
	private Set<StationUnit> cus = new HashSet<>();
	
	@Valid
	private Set<Parameter> parameters = new HashSet<>();
	
	//Station capabilities 
	private StationType stationType;
	
	private List<CapabilityType> modelCapabilities = new ArrayList<>(); //only editable
	
	private List<CapabilityType> enabledCapabilities; 
	
	

	public StationSettings() {
		super();
	}
	
	public Set<StationUnit> getCus() {
		return cus;
	}

	public void setCus(Set<StationUnit> cus) {
		this.cus = cus;
	}

	public ConnectionPowerType getPowerType() {
		return powerType;
	}

	public void setPowerType(ConnectionPowerType powerType) {
		this.powerType = powerType;
	}

	public Set<Parameter> getParameters() {
		return parameters;
	}



	public void setParameters(Set<Parameter> parameters) {
		this.parameters = parameters;
	}



	public void addUnit(StationUnit unit) {
		this.cus.add(unit);
	}



	public void addParameter(Parameter p) {
		this.parameters.add(p);
		
	}

	public String getEvseID() {
		return evseID;
	}

	public void setEvseID(String evseID) {
		this.evseID = evseID;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("StationSettingsData [evseID=").append(evseID)
				.append(", powerType=").append(powerType).append(", cus=")
				.append(cus).append(", parameters=").append(parameters).append("]");
		return builder.toString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public StationType getStationType() {
		return stationType;
	}

	public void setStationType(StationType stationType) {
		this.stationType = stationType;
	}

	
	public Parameter getParam(String name) {//, ParamDefinitionType type) {
		
		for(Parameter cp : this.parameters) {
			
			if( cp.getName().equals(name)) {
				
					return cp;
				
			}
		}
		return null;
	}
	
	public StationUnit getCU(int refMU) {

		for(StationUnit cu : this.cus) {
			if(cu.getRef()==refMU) {
				return cu;
			}
		}
		
		return null; //not found
	}

	public List<CapabilityType> getModelCapabilities() {
		return modelCapabilities;
	}

	public void setModelCapabilities(List<CapabilityType> modelCapabilities) {
		this.modelCapabilities = modelCapabilities;
	}

	public List<CapabilityType> getEnabledCapabilities() {
		return enabledCapabilities;
	}

	public void setEnabledCapabilities(List<CapabilityType> enabledCapabilities) {
		this.enabledCapabilities = enabledCapabilities;
	}

	public void addModelCapability(CapabilityType c) {
		if(this.modelCapabilities==null) {
			this.modelCapabilities = new ArrayList<>();
		}
		this.modelCapabilities.add(c);
		
	}
	
	public void addEnabledCapability(CapabilityType c) {
		if(this.enabledCapabilities==null) {
			this.enabledCapabilities = new ArrayList<>();
		}
		this.enabledCapabilities.add(c);
		
	}
}
