package it.besmart.ocpp.dtos;

import java.time.ZonedDateTime;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import it.besmart.ocpp.enums.ReserveStatus;
import it.besmart.ocpp.model.Reservation;


public class ReservationDTO extends AuthorizationDTO { 

	private long resID;
	
	private int connRef;
	
	private ZonedDateTime startDate;
	
	private ZonedDateTime expiryDate;
	
	private ZonedDateTime cancelDate;
	
	@Enumerated(EnumType.STRING)
	private ReserveStatus status;


	
	public ReservationDTO() {
		super();

	}

	public ReservationDTO(Reservation res) {
		this.resID = res.getResID();
		this.providerId = res.getProviderId();
		this.cu = new ChargingUnitDTO(res.getUnit());
		this.connRef = res.getConnectorRef();
		this.startDate = res.getStartDate();
		this.expiryDate = res.getExpiryDate();
		this.cancelDate = res.getCancelDate();
		this.status = res.getStatus();
		this.timestamp = res.getTimestamp();
		this.idTag = res.getIdTag();

	}

	public ReservationDTO(long resID) {
		this.resID = resID;
	}
	
	public ZonedDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(ZonedDateTime startDate) {
		this.startDate = startDate;
	}

	public ZonedDateTime getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(ZonedDateTime expiryDate) {
		this.expiryDate = expiryDate;
	}

	public ZonedDateTime getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(ZonedDateTime cancelDate) {
		this.cancelDate = cancelDate;
	}

	public ReserveStatus getStatus() {
		return status;
	}

	public void setStatus(ReserveStatus status) {
		this.status = status;
	}

	public long getResID() {
		return resID;
	}

	public void setResID(long resID) {
		this.resID = resID;
	}

	public int getConnRef() {
		return connRef;
	}

	public void setConnRef(int connRef) {
		this.connRef = connRef;
	}

	@Override
	public String toString() {
		return "[cu=" + cu
				+ ", startDate=" + startDate + ", expiryDate=" + expiryDate + ", cancelDate=" + cancelDate + ", status="
				+ status + ", timestamp=" + timestamp + "]";
	}
	
}
