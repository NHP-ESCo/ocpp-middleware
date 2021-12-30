package it.besmart.ocpp.dtos;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import it.besmart.ocpp.enums.MeasurandType;


public class MeterRecordDTO {
	
	@JsonIgnore
	private long recordID;
	
	private ChargingUnitDTO cu;
	
	@NotNull
	private MeasurandType measurand;

	private double value;
	
	@NotNull
	private ZonedDateTime sendTime;
	
	private ZonedDateTime timestamp;
	
	private Integer phase;
	
	private boolean computed;
	
	public MeterRecordDTO() {
		super();
		this.timestamp = ZonedDateTime.now();
	}

	public MeasurandType getMeasurand() {
		return measurand;
	}

	public void setMeasurand(MeasurandType measurand) {
		this.measurand = measurand;
	}

	public ChargingUnitDTO getCu() {
		return cu;
	}




	public void setCu(ChargingUnitDTO cu) {
		this.cu = cu;
	}


	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public ZonedDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(ZonedDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public long getRecordID() {
		return recordID;
	}

	public ZonedDateTime getSendTime() {
		return sendTime;
	}

	public void setSendTime(ZonedDateTime sendTime) {
		this.sendTime = sendTime;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MeterRecordDTO [recordID=").append(recordID).append(", measurand=").append(measurand)
				.append(", value=").append(value).append(", sendTime=").append(sendTime).append(", timestamp=")
				.append(timestamp).append(", phase=").append(phase).append("]");
		return builder.toString();
	}

	public Integer getPhase() {
		return phase;
	}

	public void setPhase(Integer phase) {
		this.phase = phase;
	}

	public boolean isEnergy() {
		
		return this.measurand.equals(MeasurandType.ENERGY);
	}
	
	public boolean isPower() {
		
		return this.measurand.equals(MeasurandType.POWER);
	}

	public boolean isComputed() {
		return computed;
	}

	public void setComputed(boolean computed) {
		this.computed = computed;
	}

}
