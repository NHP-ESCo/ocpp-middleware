package it.besmart.ocppLib.dto.config;

import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import it.besmart.ocppLib.dto.Capability;
import it.besmart.ocppLib.enumeration.ConnectionPowerType;
import it.besmart.ocppLib.enumeration.ProtocolVersion;
import it.besmart.ocppLib.enumeration.StationType;

public class StationModel {
	

	@NotEmpty
	private String name;
	
	@NotNull
	@JsonProperty(access = Access.WRITE_ONLY)
	private String brandAcronym; //reference to brand
	
	private BrandModel brand;
	
	private String brandCode; //for internal use, defined by technology producer
	
	@JsonProperty(access = Access.READ_ONLY)
	private String modelCode; //model key
	
	private StationType type;
	
	@Size(min=1)
	private Set<ProtocolVersion> protocols;
	
	@Size(min=1)
	private Set<ConnectionPowerType> powerTypes;
	
	@Valid
	@NotNull
	@Size(min = 1)
	private Set<UnitModel> units;
	
	@Valid
	private Set<ParameterKey> parameters;
	
	private Set<Capability> capabilities;
	
	private String firmwareURL;
	
	private String lastFirmware;
		
	
	public StationModel() {
		super();
	}
	
	
	@Override
	public String toString() {
		return "{  name=" + name + ", brandCode=" + brandCode + ", brand=" + brandAcronym + ", protocols=" + protocols
				+ ", parameters=" + parameters + "}";
	}
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<ParameterKey> getParameters() {
		return parameters;
	}

	
	public void setParameters(Set<ParameterKey> parameters) {
		this.parameters = parameters;
	}


	public Set<UnitModel> getUnits() {
		return units;
	}


	public void setUnits(Set<UnitModel> units) {
		this.units = units;
	}


	public Set<ProtocolVersion> getProtocols() {
		return protocols;
	}

	public void setProtocols(Set<ProtocolVersion> protocols) {
		this.protocols = protocols;
	}


	public String getBrandAcronym() {
		return brandAcronym;
	}

	public void setBrandAcronym(String brandAcronym) {
		this.brandAcronym = brandAcronym;
	}

	public String getFirmwareURL() {
		return firmwareURL;
	}

	public void setFirmwareURL(String firmwareURL) {
		this.firmwareURL = firmwareURL;
	}

	public String getLastFirmware() {
		return lastFirmware;
	}

	public void setLastFirmware(String lastFirmware) {
		this.lastFirmware = lastFirmware;
	}
	
	public Set<ConnectionPowerType> getPowerTypes() {
		return powerTypes;
	}

	public void setPowerTypes(Set<ConnectionPowerType> powerTypes) {
		this.powerTypes = powerTypes;
	}


	public UnitModel getUnit(int i) {
		if(this.units!=null) {
			for(UnitModel unit : units) {
				if(unit.getRef()==i)
					return unit;
			}
		}
		
		return null;
	}


	public String getBrandCode() {
		return brandCode;
	}


	public void setBrandCode(String brandCode) {
		this.brandCode = brandCode;
	}


	public Set<Capability> getCapabilities() {
		return capabilities;
	}


	public void setCapabilities(Set<Capability> capabilities) {
		this.capabilities = capabilities;
	}


	public String getModelCode() {
		return modelCode;
	}


	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}
	
	
	public void addUnit(UnitModel muDTO) {
		if(this.units==null)
			this.units = new HashSet<>();
		this.units.add(muDTO);
		
	}

	public void addProtocol(ProtocolVersion protocol) {
		if(this.protocols==null)
			this.protocols = new HashSet<>();
		this.protocols.add(protocol);
	}


	public void addPowerType(ConnectionPowerType powerType) {
		if(this.powerTypes==null)
			this.powerTypes = new HashSet<>();
		this.powerTypes.add(powerType);
	}
	
	public void addCapability(Capability cap) {
		if(this.capabilities==null)
			this.capabilities = new HashSet<>();
		
		this.capabilities.add(cap);
	}
	
	public void addExtraParam(ParameterKey param) {
		if(this.parameters==null)
			this.parameters = new HashSet<>();
		
		this.parameters.add(param);
	}


	public StationType getType() {
		return type;
	}


	public void setType(StationType type) {
		this.type = type;
	}


	public BrandModel getBrand() {
		return brand;
	}


	public void setBrand(BrandModel brand) {
		this.brand = brand;
	}

}
