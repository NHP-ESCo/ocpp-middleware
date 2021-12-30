package it.besmart.ocppLib.dto;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;


public class RechargeMeterData {


	@JsonProperty("meter_record_power")
	private Double meterRecordPower;

	@JsonProperty("delivered_energy")
	private Double deliveredEnergy;

	@JsonProperty("descharge_energy")
	private Double deschargeEnergy;

	@JsonProperty("system_time")
	private ZonedDateTime systemTime;

	@JsonProperty("cu_time")
	private ZonedDateTime cuTime;

	@JsonProperty("sample_time")
	private Long sampleTime; //seconds

	@JsonProperty("power_setpoint")
	private Double powerSetpoint;

	public RechargeMeterData() {
		super();
	}

	/**
	 * Power of the meter data record, expressed in kW
	 **/
	public Double getMeterRecordPower() {
		return meterRecordPower;
	}

	public void setMeterRecordPower(Double meterRecordPower) {
		this.meterRecordPower = meterRecordPower;
	}

	/**
	 * Energy in terms of kWh delivered by the charging station and notified with the related meter data
	 **/
	public Double getDeliveredEnergy() {
		return deliveredEnergy;
	}

	public void setDeliveredEnergy(Double deliveredEnergy) {
		this.deliveredEnergy = deliveredEnergy;
	}

	/**
	 * Energy in terms of kWh discharged by the electric vehicle and notified with the related meter data 
	 **/
	public Double getDeschargeEnergy() {
		return deschargeEnergy;
	}

	public void setDeschargeEnergy(Double deschargeEnergy) {
		this.deschargeEnergy = deschargeEnergy;
	}

	/**
	 * The date and time at which the meter data has been received by echargeOCPP
	 **/
	public ZonedDateTime getSystemTime() {
		return systemTime;
	}

	public void setSystemTime(ZonedDateTime systemTime) {
		this.systemTime = systemTime;
	}

	/**
	 * The date and time at which the meter data has been sent by CU
	 **/
	public ZonedDateTime getCuTime() {
		return cuTime;
	}

	public void setCuTime(ZonedDateTime cuTime) {
		this.cuTime = cuTime;
	}

	@Override
	public String toString() {
		return "\n [meterRecordPower=" + meterRecordPower + ", deliveredEnergy=" + deliveredEnergy
				+ ", deschargeEnergy=" + deschargeEnergy + ", systemTime=" + systemTime + ", cuTime=" + cuTime + "]";
	}

	public Long getSampleTime() {
		return sampleTime;
	}

	public void setSampleTime(Long sampleTime) {
		this.sampleTime = sampleTime;
	}

	public Double getPowerSetpoint() {
		return powerSetpoint;
	}

	public void setPowerSetpoint(Double powerSetpoint) {
		this.powerSetpoint = powerSetpoint;
	}
}