package it.besmart.ocpp.wrappers;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import eu.chargetime.ocpp.model.core.ChargingRateUnitType;

public class SCRequest {

	@NotEmpty
	private String cuEvse;
	
	@Min(0)
	@NotNull
	private Double maxValue;
	
	
	private String providerCode;
	
	@NotNull
	private ChargingRateUnitType unit;
	
	public SCRequest() {
		super();
	}


	public String getCuEvse() {
		return cuEvse;
	}

	public void setCuEvse(String cuEvse) {
		this.cuEvse = cuEvse;
	}



	public String getProviderCode() {
		return providerCode;
	}



	public void setProviderCode(String providerCode) {
		this.providerCode = providerCode;
	}



	public ChargingRateUnitType getUnit() {
		return unit;
	}



	public void setUnit(ChargingRateUnitType unit) {
		this.unit = unit;
	}



	public Double getMaxValue() {
		return maxValue;
	}



	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}
	
	
}
