package it.besmart.ocpp.model;

import java.time.ZonedDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;

import it.besmart.ocpp.dtos.TransactionDTO;
import it.besmart.ocppLib.enumeration.ConnectionPowerType;
import it.besmart.ocppLib.enumeration.EndTxReason;
import it.besmart.ocppLib.enumeration.TransactionStatus;

@Entity
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "z_tx_generator")
	@SequenceGenerator(name="z_tx_generator", sequenceName = "z_tx_seq", allocationSize=1)
	private long txID;
	
	@ManyToOne
	private Connector connector;
	
	@OneToOne
	private Authorization authorization;
	
	@OneToOne
	private Reservation reservation;
	
	
	private Double meterStart;
	private Double meterStop;
	
	private ZonedDateTime startDate;
	private ZonedDateTime stopDate;
	
	private ZonedDateTime stopChargingDate;

	@Enumerated(EnumType.STRING)
	private EndTxReason endReason;
	
	private String stoppingIdTag;
	
	@Enumerated(EnumType.STRING)
	private TransactionStatus status;
	
	private boolean resetMeter = false;
	
	private double maxPower;
	
	private Double lastMaxPower;
	
	private Double lastSetpoint;
	
	@Enumerated(EnumType.STRING)
	private ConnectionPowerType powerType;
	
	private boolean reducedPhase; //monophase or unknwown on triphase plug
	
	@ManyToOne
	private StoredLog endFault;
	
	
	public Transaction() {
		super();
	}
	
	public Transaction(TransactionDTO tx) {
		this.meterStart = tx.getMeterStart();
		this.startDate = tx.getStartDate();
		this.resetMeter = tx.isResetMeter();
	}

	
	public long getTxID() {
		return txID;
	}

	public Connector getConnector() {
		return connector;
	}

	public void setConnector(Connector connector) {
		this.connector = connector;
	}

	public Double getMeterStart() {
		return meterStart;
	}

	public void setMeterStart(Double meterStart) {
		this.meterStart = meterStart;
	}

	public Double getMeterStop() {
		return meterStop;
	}

	public void setMeterStop(Double meterStop) {
		this.meterStop = meterStop;
	}

	public ZonedDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(ZonedDateTime startDate) {
		this.startDate = startDate;
	}

	public ZonedDateTime getStopDate() {
		return stopDate;
	}

	public void setStopDate(ZonedDateTime stopDate) {
		this.stopDate = stopDate;
	}

	public EndTxReason getEndReason() {
		return endReason;
	}

	public void setEndReason(EndTxReason endReason) {
		this.endReason = endReason;
	}

	public Authorization getAuthorization() {
		return authorization;
	}

	public void setAuthorization(Authorization authorization) {
		this.authorization = authorization;
	}

	public Reservation getReservation() {
		return reservation;
	}

	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
	}

	@Override
	public String toString() {
		return "Transaction [txID=" + txID + ", connector="
				+ connector.getConnectorID() + ", authorization=" + authorization + ", reservation=" + reservation + ", meterStart="
				+ meterStart + ", meterStop=" + meterStop + ", startDate=" + startDate + ", stopDate=" + stopDate
				+ ", endReason=" + endReason + "]";
	}

	public TransactionStatus getStatus() {
		return status;
	}

	public void setStatus(TransactionStatus status) {
		this.status = status;
	}

	
	public String getExternalSession() {
		if(authorization==null)
			return null;
		return authorization.getExternalSession();
	}

	public ZonedDateTime getStopChargingDate() {
		return stopChargingDate;
	}

	public void setStopChargingDate(ZonedDateTime stopChargingDate) {
		this.stopChargingDate = stopChargingDate;
	}

	public String getStoppingIdTag() {
		return stoppingIdTag;
	}

	public void setStoppingIdTag(String stoppingIdTag) {
		this.stoppingIdTag = stoppingIdTag;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (txID ^ (txID >>> 32));
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
		Transaction other = (Transaction) obj;
		if (txID != other.txID)
			return false;
		return true;
	}

	public boolean isResetMeter() {
		return resetMeter;
	}

	public void setResetMeter(boolean resetMeter) {
		this.resetMeter = resetMeter;
	}

	public double getMaxPower() {
		return maxPower;
	}

	public void setMaxPower(double maxPower) {
		this.maxPower = maxPower;
	}

	public Double getLastMaxPower() {
		return lastMaxPower;
	}

	public void setLastMaxPower(Double lastMaxPower) {
		this.lastMaxPower = lastMaxPower;
	}

	public ConnectionPowerType getPowerType() {
		return powerType;
	}

	public void setPowerType(ConnectionPowerType powerType) {
		this.powerType = powerType;
	}

	public boolean isReducedPhase() {
		return reducedPhase;
	}

	public void setReducedPhase(boolean reducedPhase) {
		this.reducedPhase = reducedPhase;
	}

	public Double getLastSetpoint() {
		return lastSetpoint;
	}

	public void setLastSetpoint(Double lastSetpoint) {
		this.lastSetpoint = lastSetpoint;
	}

	public StoredLog getEndFault() {
		return endFault;
	}

	public void setEndFault(StoredLog endFault) {
		this.endFault = endFault;
	}	
	
}
