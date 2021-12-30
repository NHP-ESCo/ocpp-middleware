package it.besmart.ocpp.model;

import java.time.ZonedDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import it.besmart.ocpp.dtos.MeterRecordDTO;
import it.besmart.ocpp.enums.MeasurandType;

@Entity
public class MeterRecord {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "z_meterrec_generator")
	@SequenceGenerator(name="z_meterrec_generator", sequenceName = "z_meterrec_seq", allocationSize=1)
	private long recordID;
	
	@ManyToOne
	private ChargingUnit cu;
	
	@ManyToOne
	private ChargingStation cs;  // not null only if cu is not defined
	
	@ManyToOne
	private Transaction tx; //could be null if record is received in clock-aligned samples
	
	@Enumerated(EnumType.STRING)
	private MeasurandType measurand;

	private Double value;
	
	private Double setValue;
	
	private ZonedDateTime sendTime;
	
	private ZonedDateTime timestamp;
	
	private boolean computed;
	
	private boolean sent = false;
	
	private Integer phase;
	
	public MeterRecord() {
		super();
	}

	public MeterRecord(MeterRecordDTO record) {
		this.measurand = record.getMeasurand();
		this.value = record.getValue();
		this.sendTime = record.getSendTime();
		this.timestamp = ZonedDateTime.now();
		this.phase = record.getPhase();
		this.computed = record.isComputed();
	}


	public ChargingUnit getCu() {
		return cu;
	}

	public void setCu(ChargingUnit cu) {
		this.cu = cu;
	}

	public Transaction getTx() {
		return tx;
	}

	public void setTx(Transaction tx) {
		this.tx = tx;
	}

	public MeasurandType getMeasurand() {
		return measurand;
	}

	public void setMeasurand(MeasurandType measurand) {
		this.measurand = measurand;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
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

	public ChargingStation getCs() {
		return cs;
	}

	public void setCs(ChargingStation cs) {
		this.cs = cs;
	}

	public boolean isSent() {
		return sent;
	}

	public void setSent(boolean sent) {
		this.sent = sent;
	}

	public Integer getPhase() {
		return phase;
	}

	public void setPhase(Integer phase) {
		this.phase = phase;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MeterRecord [recordID=").append(recordID).append(", measurand=").append(measurand)
				.append(", value=").append(value).append(", sendTime=").append(sendTime).append(", timestamp=")
				.append(timestamp).append(", sent=").append(sent).append(", phase=").append(phase).append("]");
		return builder.toString();
	}

	public boolean isComputed() {
		return computed;
	}

	public void setComputed(boolean computed) {
		this.computed = computed;
	}

	public Double getSetValue() {
		return setValue;
	}

	public void setSetValue(Double setValue) {
		this.setValue = setValue;
	}

}
