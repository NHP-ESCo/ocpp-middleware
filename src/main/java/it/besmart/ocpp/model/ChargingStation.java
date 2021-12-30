package it.besmart.ocpp.model;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import it.besmart.ocpp.enums.CSLifeStatus;
import it.besmart.ocpp.enums.StationStatusComplete;
import it.besmart.ocppLib.enumeration.ConnectionPowerType;
import it.besmart.ocppLib.enumeration.ProtocolVersion;
import it.besmart.ocppLib.enumeration.CapabilityStatus;


@Entity
public class ChargingStation {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "z_chargingstation_generator")
	@SequenceGenerator(name="z_chargingstation_generator", sequenceName = "z_chargingstation_seq", allocationSize=1)
	private long csID;  
	
	private String name;
	
	@NotEmpty
	@NotNull
	private String serialNumber;
	
	@NotEmpty
	@NotNull
	private String identifier;
	
	@NotEmpty
	@NotNull
	private String evseID; // countryCode*operatorCode*identifier
	
	
	
	/** STATES **/
	@NotNull
	@Enumerated(EnumType.STRING)
	private StationStatusComplete status;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private CSLifeStatus lifeStatus;
	
	private String actualSession; //Connected or not
	
	private boolean configureAtStopTx;
	
	
	/** SMART CHARGING **/

	@Enumerated(EnumType.STRING)
	private CapabilityStatus smartCharging;

	
	/** OTHER INFO **/
	
	private String operatorCode;
	
	@NotNull
	@ManyToOne
	private Model model; 
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private ProtocolVersion protocol;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private ConnectionPowerType powerType;
	
	private Double maxPower;
	
	private Double minPower;
	
	
	private ZonedDateTime lastUpdate;
	
	private ZonedDateTime addedDate;
	
	private ZonedDateTime commissioningDate;
	
	private String firmware;
	
	private String addressIP;
	
	
	@JsonManagedReference
	@ManyToMany(cascade = CascadeType.ALL)
	private Set<WlCard> localList = new HashSet<>();
	
	@JsonManagedReference
	@OneToMany(mappedBy="chargingStation", cascade = CascadeType.ALL)
	private Set<ChargingUnit> cus = new HashSet<>();
	
	@JsonManagedReference
	@OneToMany(mappedBy="station", cascade = CascadeType.ALL)
	private Set<ConfigurationParam> parameters = new HashSet<>();
	
	
	public ChargingStation() {
		super();
		this.addedDate = ZonedDateTime.now();
		this.commissioningDate = null;
		this.firmware = null;
		this.lifeStatus = CSLifeStatus.INSTALLED;
		this.status = StationStatusComplete.UNAVAILABLE;
	}

	
	@Override
	public String toString() {
		return "ChargingStation [csID=" + csID + ", serialNumber=" + serialNumber + ", evseID=" + evseID + ", identifier="
				+ identifier + ", model=" + model + ", protocol=" + protocol + ", powerType=" + powerType + ", status=" + status + ", addedDate=" + addedDate + ", commissioningDate=" + commissioningDate
				+ ", software=" + firmware + ", actualSession=" + actualSession + "]";
	}


	public long getCSId() {
		return csID;
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


	public Model getModel() {
		return model;
	}


	public void setModel(Model model) {
		this.model = model;
	}

	public Set<ChargingUnit> getCUs() {
		return cus;
	}


	public void setCUs(Set<ChargingUnit> cus) {
		this.cus = cus;
	}


	public StationStatusComplete getStatus() {
		return status;
	}


	public void setSmartCharging(CapabilityStatus smartCharging) {
		this.smartCharging = smartCharging;
	}


	public Set<ChargingUnit> getCus() {
		return cus;
	}


	public void setCus(Set<ChargingUnit> cus) {
		this.cus = cus;
	}


	public void setStatus(StationStatusComplete status) {
		this.status = status;
	}


	public Set<WlCard> getLocalList() {
		return localList;
	}


	public void setLocalList(Set<WlCard> localList) {
		this.localList = localList;
	}


	public CSLifeStatus getLifeStatus() {
		return lifeStatus;
	}


	public void setLifeStatus(CSLifeStatus lifeStatus) {
		this.lifeStatus = lifeStatus;
	}


//	public Set<EMP> getProviders() {
//		return providers;
//	}
//
//
//	public void setProviders(Set<EMP> providers) {
//		this.providers = providers;
//	}


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


	public String getFirmware() {
		return firmware;
	}


	public void setFirmware(String software) {
		this.firmware = software;
	}


	public Set<ConfigurationParam> getParameters() {
		return parameters;
	}


	public void setParameters(Set<ConfigurationParam> parameters) {
		this.parameters = parameters;
	}
	
	
	// methods to add entities to sets
//	public void addProvider(EMP p) {
//		this.providers.add(p);
//	}

	public ConnectionPowerType getPowerType() {
		return powerType;
	}


	public void setPowerType(ConnectionPowerType powerType) {
		this.powerType = powerType;
	}


	public ProtocolVersion getProtocol() {
		return protocol;
	}


	public void setProtocol(ProtocolVersion protocol) {
		this.protocol = protocol;
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


	public String getEvseID() {
		return evseID;
	}


	public void setEvseID(String evseID) {
		this.evseID = evseID;
	}
	

	public boolean isDismissed() {
		return this.getLifeStatus()==CSLifeStatus.DISMISSED;
	}


	public void createEvseID() {
		evseID = getOperatorCode() + "*E" + identifier;
	}


	public boolean isConnected() {
		return actualSession!=null;
	}
	
	public boolean isControllable() {
		return this.isConnected() 
				&& lifeStatus.equals(CSLifeStatus.ACTIVE) 
				&& !status.equals(StationStatusComplete.UNAVAILABLE);
	}


	public boolean isScEnabled() {
		return this.smartCharging!=null && this.smartCharging.equals(CapabilityStatus.Enabled);
	}


	public String getOperatorCode() {
		return operatorCode;
	}


	public void setOperatorCode(String operatorCode) {
		this.operatorCode = operatorCode;
	}


	public void addLocalCustomer(WlCard customer) {
		this.localList.add(customer);
	}


	public ZonedDateTime getLastUpdate() {
		return lastUpdate;
	}


	public void setLastUpdate(ZonedDateTime lastUpdate) {
		this.lastUpdate = lastUpdate;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (csID ^ (csID >>> 32));
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
		ChargingStation other = (ChargingStation) obj;
		if (csID != other.csID)
			return false;
		return true;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public boolean isConfigureAtStopTx() {
		return configureAtStopTx;
	}


	public void setConfigureAtStopTx(boolean configureAtStopTx) {
		this.configureAtStopTx = configureAtStopTx;
	}


	public Double getMaxPower() {
		return maxPower;
	}


	public void setMaxPower(Double maxPower) {
		this.maxPower = maxPower;
	}


	public Double getMinPower() {
		return minPower;
	}


	public void setMinPower(Double minPower) {
		this.minPower = minPower;
	}


}
