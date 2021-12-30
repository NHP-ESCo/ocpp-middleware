package it.besmart.ocppLib.dto;

import javax.validation.constraints.NotNull;

import it.besmart.ocppLib.enumeration.ChargingMode;
import it.besmart.ocppLib.enumeration.PlugPowerType;
import it.besmart.ocppLib.enumeration.PlugType;

public class PlugData {

	@NotNull
	private PlugType name;
	
	@NotNull
	private PlugPowerType powerType;
	
	private ChargingMode mode;
	

	public PlugData() {
		super();
	}
	
	public PlugData(PlugType p) {
		
		this.name = p;
		this.powerType = p.getPlugPowerType();
		this.mode = p.getMode();
	}
	
	
	
	public PlugPowerType getPowerType() {
		return powerType;
	}

	public void setPowerType(PlugPowerType powerType) {
		this.powerType = powerType;
	}

	public PlugType getName() {
		return name;
	}

	public void setName(PlugType name) {
		this.name = name;
	}

	public ChargingMode getMode() {
		return mode;
	}

	public void setMode(ChargingMode mode) {
		this.mode = mode;
	}

	@Override
	public String toString() {
		return "Plug {name=" + name + ", powerType=" + powerType + ", mode=" + mode + "}";
	}
	
	
	
	
}
