package it.besmart.ocppLib.dto;

import javax.validation.constraints.NotNull;

import it.besmart.ocppLib.enumeration.PlugType;


public class StationConnector {
	
	@NotNull
	private int ref; //# in the CS
	
	private PlugType plugType;
	
	private Double maxPower; 
	
	private Double minPower; 
	
	private Double actualMaxPower; 
	
	private Double actualMinPower; 
	
	
	public StationConnector() {
		super();
	}

	public int getRef() {
		return ref;
	}

	public void setRef(int ref) {
		this.ref = ref;
	}

	public Double getMaxPower() {
		return maxPower;
	}

	public void setMaxPower(Double maxPower) {
		this.maxPower = maxPower;
	}

	
	public PlugType getPlugType() {
		return plugType;
	}

	public void setPlugType(PlugType plugType) {
		this.plugType = plugType;
	}

	public Double getMinPower() {
		return minPower;
	}

	public void setMinPower(Double minPower) {
		this.minPower = minPower;
	}

	@Override
	public String toString() {
		return "StationConnector [ref=" + ref + ", plugType=" + plugType + ", maxPower=" + maxPower + ", minPower="
				+ minPower + ", actualMaxPower=" + actualMaxPower + ", actualMinPower=" + actualMinPower + "]";
	}

	public Double getActualMaxPower() {
		return actualMaxPower;
	}

	public void setActualMaxPower(Double actualMaxPower) {
		this.actualMaxPower = actualMaxPower;
	}

	public Double getActualMinPower() {
		return actualMinPower;
	}

	public void setActualMinPower(Double actualMinPower) {
		this.actualMinPower = actualMinPower;
	}

}

