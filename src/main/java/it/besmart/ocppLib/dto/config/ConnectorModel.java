package it.besmart.ocppLib.dto.config;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;


import it.besmart.ocppLib.enumeration.PlugType;

public class ConnectorModel {

	@JsonIgnore
	private long connectorID;
	
	@NotNull
	private int ref;
	
	@NotNull
	private PlugType plugType;
	
	
	private Double maxCurrent;
	
	private Double minCurrentM;

	private Double minCurrentT;
	
	
	private Double minPower;

	private Double maxPower;
	
	public ConnectorModel() {
		super();
	}

	public int getRef() {
		return ref;
	}

	public void setRef(int ref) {
		this.ref = ref;
	}

	public long getConnectorID() {
		return connectorID;
	}

	public PlugType getPlugType() {
		return plugType;
	}

	public void setPlugType(PlugType plugType) {
		this.plugType = plugType;
	}

	public Double getMaxPower() {
		return maxPower;
	}

	public void setMaxPower(Double maxPower) {
		this.maxPower = maxPower;
	}

	public Double getMaxCurrent() {
		return maxCurrent;
	}

	public void setMaxCurrent(Double maxCurrent) {
		this.maxCurrent = maxCurrent;
	}

	public Double getMinCurrentM() {
		return minCurrentM;
	}

	public void setMinCurrentM(Double minCurrentM) {
		this.minCurrentM = minCurrentM;
	}

	public Double getMinCurrentT() {
		return minCurrentT;
	}

	public void setMinCurrentT(Double minCurrentT) {
		this.minCurrentT = minCurrentT;
	}

	public Double getMinPower() {
		return minPower;
	}

	public void setMinPower(Double minPower) {
		this.minPower = minPower;
	}
	
}
