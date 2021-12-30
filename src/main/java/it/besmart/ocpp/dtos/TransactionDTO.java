package it.besmart.ocpp.dtos;

import java.time.ZonedDateTime;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

import it.besmart.ocpp.model.Transaction;
import it.besmart.ocppLib.enumeration.EndTxReason;

public class TransactionDTO {

	private long txID;
	
	private String externalSession;
	
	@NotNull
	private ConnectorDTO connector;
	
	private AuthorizationDTO authorization;
	
	private ReservationDTO reservation;
	
	@NotNull
	private Double meterStart;
	private Double meterStop;
	
	@NotNull
	private ZonedDateTime startDate;
	private ZonedDateTime stopDate;

	@Enumerated(EnumType.STRING)
	private EndTxReason endReason;
	
	private String stoppingIdTag;
	
	private boolean resetMeter = false;
	
	public TransactionDTO() {
		super();
	}
	
	public TransactionDTO(Integer txID) {
		this.txID = txID;
	}

	public TransactionDTO(Transaction tx) {
		this.txID = tx.getTxID();
		this.externalSession = tx.getAuthorization().getExternalSession();
		this.connector = new ConnectorDTO(tx.getConnector());
		this.meterStart = tx.getMeterStart();
		this.meterStop = tx.getMeterStart();
		this.startDate = tx.getStartDate();
		this.stopDate = tx.getStopDate();
		this.endReason = tx.getEndReason();
		this.stoppingIdTag = tx.getStoppingIdTag();
		
		if(tx.getAuthorization()!=null)
			this.authorization = new AuthorizationDTO(tx.getAuthorization());
		if(tx.getReservation()!=null)
			this.reservation = new ReservationDTO(tx.getReservation());
	}

	public long getTxID() {
		return txID;
	}

	public void setTxID(long txID) {
		this.txID = txID;
	}

	public ConnectorDTO getConnector() {
		return connector;
	}

	public void setConnector(ConnectorDTO connector) {
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

	public AuthorizationDTO getAuthorization() {
		return authorization;
	}

	public void setAuthorization(AuthorizationDTO authorization) {
		this.authorization = authorization;
	}

	public ReservationDTO getReservation() {
		return reservation;
	}

	public void setReservation(ReservationDTO reservation) {
		this.reservation = reservation;
	}

	public String getExternalSession() {
		return externalSession;
	}

	public void setExternalSession(String externalSession) {
		this.externalSession = externalSession;
	}

	public String getStoppingIdTag() {
		return stoppingIdTag;
	}

	public void setStoppingIdTag(String stoppingIdTag) {
		this.stoppingIdTag = stoppingIdTag;
	}

	public boolean isResetMeter() {
		return resetMeter;
	}

	public void setResetMeter(boolean resetMeter) {
		this.resetMeter = resetMeter;
	}
	
	
	
	
}
