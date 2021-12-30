package it.besmart.ocpp.model;

import java.time.ZonedDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;

@Entity
public class RecordSCLimit {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "z_record_sc_limit_generator")
	@SequenceGenerator(name="z_record_sc_limit_generator", sequenceName = "z_record_sc_limit_seq", allocationSize=1)
	private long recordID;
	
	@NotNull
	@ManyToOne
	private ChargingUnit unit;
	
	@ManyToOne
	private Transaction transaction;
	
	@NotNull
	private Double maxPower;
	
	private Double minPower;
	
	private Double maxCurrent;
	
	@NotNull
	private ZonedDateTime timestamp;

	private boolean acceptedCommand;
	
	
	public RecordSCLimit() {
		super();
		timestamp = ZonedDateTime.now();
	}

	public long getRecordID() {
		return recordID;
	}

	public void setRecordID(long recordID) {
		this.recordID = recordID;
	}

	public ChargingUnit getUnit() {
		return unit;
	}

	public void setUnit(ChargingUnit unit) {
		this.unit = unit;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public ZonedDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(ZonedDateTime timestamp) {
		this.timestamp = timestamp;
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

	public Double getMaxCurrent() {
		return maxCurrent;
	}

	public void setMaxCurrent(Double maxCurrent) {
		this.maxCurrent = maxCurrent;
	}

	public boolean isAcceptedCommand() {
		return acceptedCommand;
	}

	public void setAcceptedCommand(boolean acceptedCommand) {
		this.acceptedCommand = acceptedCommand;
	}
	
	
	
	
}
