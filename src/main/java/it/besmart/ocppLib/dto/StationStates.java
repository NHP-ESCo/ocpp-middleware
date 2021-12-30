package it.besmart.ocppLib.dto;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import it.besmart.ocppLib.enumeration.StationLifeStatus;

@JsonInclude(Include.NON_NULL)
public class StationStates { //Read operation

	private StationLifeStatus lifeStatus;
	
	private boolean connected;
	
	private boolean configured;

	private ZonedDateTime commissioningDate;

	private String addressIP;
	
	private String firmware;
	
	private String lastFirmwareVersion;
	
	private Double maxPower;
	
	private Double minPower;
	
	private List<UnitCompleteStatus> unitStates = new ArrayList<>();

	private List<Capability> capabilityStates = new ArrayList<>();
	
	
	public StationStates() {
		super();
	}

	public StationLifeStatus getLifeStatus() {
		return lifeStatus;
	}

	public void setLifeStatus(StationLifeStatus lifeStatus) {
		this.lifeStatus = lifeStatus;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public boolean isConfigured() {
		return configured;
	}

	public void setConfigured(boolean configured) {
		this.configured = configured;
	}

	public ZonedDateTime getCommissioningDate() {
		return commissioningDate;
	}

	public void setCommissioningDate(ZonedDateTime commissioningDate) {
		this.commissioningDate = commissioningDate;
	}

	public String getAddressIP() {
		return addressIP;
	}

	public void setAddressIP(String addressIP) {
		this.addressIP = addressIP;
	}

	public String getFirmware() {
		return firmware;
	}

	public void setFirmware(String firmware) {
		this.firmware = firmware;
	}


	public List<UnitCompleteStatus> getUnitStates() {
		return unitStates;
	}

	public void setUnitStates(List<UnitCompleteStatus> unitStates) {
		this.unitStates = unitStates;
	}

	public String getLastFirmwareVersion() {
		return lastFirmwareVersion;
	}

	public void setLastFirmwareVersion(String lastFirmwareVersion) {
		this.lastFirmwareVersion = lastFirmwareVersion;
	}

	public void addUnitState(UnitCompleteStatus status) {
		this.unitStates.add(status);
		
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

	public List<Capability> getCapabilityStates() {
		return capabilityStates;
	}

	public void setCapabilityStates(List<Capability> capabilityStates) {
		this.capabilityStates = capabilityStates;
	}

	public void addCapabilityState(Capability cap) {
		if(this.capabilityStates==null)
			this.capabilityStates = new ArrayList<>();
		
		this.capabilityStates.add(cap);

	}

	
	
}
