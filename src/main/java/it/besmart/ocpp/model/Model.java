package it.besmart.ocpp.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import it.besmart.ocppLib.enumeration.StationType;
import it.besmart.ocppLib.enumeration.ConnectionPowerType;
import it.besmart.ocppLib.enumeration.ProtocolVersion;

@Entity
public class Model {
	
	private static final Logger logger = LoggerFactory.getLogger(Model.class);
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "z_model_generator")
	@SequenceGenerator(name="z_model_generator", sequenceName = "z_model_seq", allocationSize=1)
	private long modelID;
	
	
	@NotEmpty
	private String name;
	
	private String brandCode; //for internal use, defined by producer
	
	private String externalCode; //for external use
	
	@Enumerated(EnumType.STRING)
	private StationType type;
	
	@NotNull
	@ManyToOne
	private Brand brand;
	
	private String firmwareURL;
	
	private String lastFirmware;
	
	private boolean sftp;
	
	private boolean rfidPerUnit; 
	//if false we have to configure if it's possible to stop session 
	//with the same card parent Tag OR not(default)
	

	@ElementCollection(targetClass=ProtocolVersion.class)
    @Enumerated(EnumType.STRING) 
	@CollectionTable(name="model_protocols")
	private Collection<ProtocolVersion> protocols = new ArrayList<ProtocolVersion>();
	
	
	@ElementCollection(targetClass=ConnectionPowerType.class)
    @Enumerated(EnumType.STRING) 
	@CollectionTable(name="model_power_types")
	private Collection<ConnectionPowerType> powerTypes = new ArrayList<ConnectionPowerType>();
	
	
	@JsonManagedReference //i modelli referenziano i ModelUnit (contatori)
	@OneToMany(mappedBy="model", cascade = CascadeType.ALL, orphanRemoval=true)
	private Set<ModelUnit> units = new HashSet<>();
	
	@JsonManagedReference
	@OneToMany(mappedBy="model", cascade = CascadeType.ALL, orphanRemoval=true) //parent table
	private Set<ExtraParamType> extraParams = new HashSet<>();
	
	
	
	public Model() {
		super();
	}
	

	public long getModelId() {
		return modelID;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	public String getCompleteCode() {
		
		return brand.getAcronym()+"*"+brandCode;
	}

	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}


	public Set<ExtraParamType> getExtraParams() {
		return extraParams;
	}

	public void setExtraParams(Set<ExtraParamType> extraParams) {
		this.extraParams = extraParams;
	}

	
	public void addExtraParam(ExtraParamType param) {
		if(this.extraParams==null)
			this.extraParams = new HashSet<>();
		this.extraParams.add(param);
	}

	public Set<ModelUnit> getUnits() {
		return units;
	}

	public void setUnits(Set<ModelUnit> units) {
		this.units = units;
	}
	
	public Collection<ConnectionPowerType> getPowerTypes() {
		return powerTypes;
	}


	public void setPowerTypes(Collection<ConnectionPowerType> powerTypes) {
		this.powerTypes = powerTypes;
	}


	public void addUnit(ModelUnit mu) {
		this.units.add(mu);
	}

	public Collection<ProtocolVersion> getProtocols() {
		return protocols;
	}

	public void setProtocols(Collection<ProtocolVersion> protocols) {
		this.protocols = protocols;
	}

	public void addProtocol(ProtocolVersion protocol) {
		if(this.protocols==null)
			this.protocols = new ArrayList<ProtocolVersion>();
		this.protocols.add(protocol);
	}
	
	public void addPowerType(ConnectionPowerType type) {
		if(this.powerTypes==null)
			this.powerTypes = new ArrayList<ConnectionPowerType>();
		this.powerTypes.add(type);
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


	@Override
	public String toString() {
		return "Model [modelID=" + modelID + ", name=" + name + ", brandCode=" + brandCode + ", brand=" + brand + "]";
	}

	

	//TODO: all services, because based on set
	public boolean hasProtocol(ProtocolVersion protocol) {
		for(ProtocolVersion p : this.protocols) {
			if( p.equals(protocol)) 
				return true;
		}
		return false;
	}
	
	public boolean isPowerTypeEnabled(ConnectionPowerType powerType) {
		for(ConnectionPowerType p : this.powerTypes) {
			if( p.equals(powerType)) 
				return true;
		}
		return false;
	}
	

	public double computeMaxPower(ConnectionPowerType powerType) {

		double maxPower = 0;
		for(ModelUnit mu : this.units) {
			maxPower+=mu.computeMaxPower(powerType);
		}
		return maxPower;
	}

	public double computeMinPower(ConnectionPowerType powerType) {
		double minPower = 0;
		for(ModelUnit mu : this.units) {
			double minUnit = mu.computeMinPower(powerType);
			logger.debug(String.format("Minimum for unit %s: %f", mu.getRef(), minUnit));
			
			minPower+= minUnit;
		}
		return minPower;
	}


	public void cleanParameters() {
		this.extraParams.clear();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (modelID ^ (modelID >>> 32));
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Model other = (Model) obj;
		if (modelID != other.modelID)
			return false;
		return true;
	}


	public boolean isSftp() {
		return sftp;
	}


	public void setSftp(boolean sftp) {
		this.sftp = sftp;
	}


	public boolean isRfidPerUnit() {
		return rfidPerUnit;
	}


	public void setRfidPerUnit(boolean rfidPerUnit) {
		this.rfidPerUnit = rfidPerUnit;
	}


	public StationType getType() {
		return type;
	}


	public void setType(StationType type) {
		this.type = type;
	}

	public String getBrandCode() {
		return brandCode;
	}


	public void setBrandCode(String brandCode) {
		this.brandCode = brandCode;
	}


	public String getExternalCode() {
		return externalCode;
	}


	public void setExternalCode(String externalCode) {
		this.externalCode = externalCode;
	}

}
