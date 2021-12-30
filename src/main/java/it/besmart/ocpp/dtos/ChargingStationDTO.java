package it.besmart.ocpp.dtos;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.besmart.ocpp.enums.CSLifeStatus;
import it.besmart.ocpp.enums.StationStatusComplete;
import it.besmart.ocppLib.dto.config.StationModel;
import it.besmart.ocppLib.enumeration.ConnectionPowerType;
import it.besmart.ocppLib.enumeration.ProtocolVersion;

public class ChargingStationDTO {
	
	@JsonIgnore
	private long csID;
	
	private String evseID; //OICP: operatorCode*serialNumber
	
	private String name;
	
	@NotEmpty
	@NotNull
	private String serialNumber;  
	
	@NotEmpty
	@NotNull
	private String identifier;
	
	
	@NotEmpty
	@NotNull
	private String operatorCode; 
	
	@JsonIgnore
	private StationModel model;
	
	@NotNull
	private String modelCode;
	
	@NotNull
	private ConnectionPowerType powerType;
	
	private Boolean smartCharging;  //Only if model is enabled
	
	private Boolean reservable; //Only if model is enabled
	
	private Double connectionPower;  
	
	@Valid
	@Size(min = 1)
	private Set<ChargingUnitDTO> cus = new HashSet<>();
	
	@JsonIgnore
	private StationStatusComplete status;
	
	private CSLifeStatus lifeStatus;
	
	private ZonedDateTime addedDate;

	private ZonedDateTime commissioningDate;
	
	@NotNull
	private ProtocolVersion protocol;
	
	private String firmware;
	
	@Valid
	private Set<ConfigurationParamDTO> parameters = new HashSet<>();
	
	private String actualSession;
	
	private String addressIP;
	
	
	public ChargingStationDTO() {
		super();
	}


	@Override
	public String toString() {
		return "\nChargingStation : \n evseID=" + evseID + ",\n identifier=" + identifier + ",\n model=" + model
				+ ",\n cus=" + cus + ",\n status=" + status
				+ ",\n addedDate=" + addedDate + ",\n commissioningDate=" + commissioningDate + ",\n protocol=" + protocol
				+ ",\n software=" + firmware + ",\n parameters=" + parameters + "\n";
	}

	public long getCsID() {
		return csID;
	}

	public void setCsID(long csID) {
		this.csID = csID;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String name) {
		this.identifier = name;
	}

	public StationModel getModel() {
		return model;
	}

	public void setModel(StationModel model) {
		this.model = model;
	}

	public Set<ChargingUnitDTO> getCus() {
		return cus;
	}

	public void setCus(Set<ChargingUnitDTO> cus) {
		this.cus = cus;
	}

	public StationStatusComplete getStatus() {
		return status;
	}

	public void setStatus(StationStatusComplete status) {
		this.status = status;
	}

	public ZonedDateTime getAddedDate() {
		return addedDate;
	}

	public void setAddedDate(ZonedDateTime addedDate) {
		this.addedDate = addedDate;
	}

	public ZonedDateTime getCommissioningDate() {
		return commissioningDate;
	}

	public void setCommissioningDate(ZonedDateTime commissioningDate) {
		this.commissioningDate = commissioningDate;
	}

	public ProtocolVersion getProtocol() {
		return protocol;
	}

	public void setProtocol(ProtocolVersion protocol) {
		this.protocol = protocol;
	}

	public String getSoftware() {
		return firmware;
	}

	public void setSoftware(String software) {
		this.firmware = software;
	}

	public Set<ConfigurationParamDTO> getParameters() {
		return parameters;
	}

	public void setParameters(Set<ConfigurationParamDTO> parameters) {
		this.parameters = parameters;
	}

	public String getEvseID() {
		return evseID;
	}

	public void setEvseID(String evseID) {
		this.evseID = evseID;
	}

	public ConnectionPowerType getPowerType() {
		return powerType;
	}

	public void setPowerType(ConnectionPowerType powerType) {
		this.powerType = powerType;
	}


	public String getActualSession() {
		return actualSession;
	}

	public void setActualSession(String actualSession) {
		this.actualSession = actualSession;
	}
	
	
	
	
	public String getAddressIP() {
		return addressIP;
	}


	public void setAddressIP(String addressIP) {
		this.addressIP = addressIP;
	}


	public CSLifeStatus getLifeStatus() {
		return lifeStatus;
	}


	public void setLifeStatus(CSLifeStatus lifeStatus) {
		this.lifeStatus = lifeStatus;
	}


	public ChargingUnitDTO getCU(int refMU) {

		for(ChargingUnitDTO cu : this.cus) {
			if(cu.getRef()==refMU) {
				return cu;
			}
		}
		
		return null; //not found
	}
	
	public ConfigurationParamDTO getParam(String name) {//, ParamDefinitionType type) {
		
		for(ConfigurationParamDTO cp : this.parameters) {
			
			if( cp.getName().equals(name)) {
				
					return cp;
				
			}
		}
		return null;
	}


	public void addCU(ChargingUnitDTO chargingUnitDTO) {
		this.cus.add(chargingUnitDTO);
		
	}


	public void addParameter(ConfigurationParamDTO configurationParamDTO) {
		this.parameters.add(configurationParamDTO);
		
	}


	public String getModelCode() {
		return modelCode;
	}


	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}


	public Double getConnectionPower() {
		return connectionPower;
	}


	public void setConnectionPower(Double connectionPower) {
		this.connectionPower = connectionPower;
	}


	public String getFirmware() {
		return firmware;
	}


	public void setFirmware(String firmware) {
		this.firmware = firmware;
	}


	public Boolean getSmartCharging() {
		return smartCharging;
	}


	public void setSmartCharging(Boolean smartCharging) {
		this.smartCharging = smartCharging;
	}


	public String getOperatorCode() {
		return operatorCode;
	}


	public void setOperatorCode(String operatorCode) {
		this.operatorCode = operatorCode;
	}


	public boolean isSmartCharging() {
		
		return smartCharging!=null && smartCharging==true;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public Boolean getReservable() {
		return reservable;
	}


	public void setReservable(Boolean reservable) {
		this.reservable = reservable;
	}

}
