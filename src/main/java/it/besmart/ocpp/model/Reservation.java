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
import javax.validation.constraints.NotNull;

import it.besmart.ocpp.dtos.ReservationDTO;
import it.besmart.ocpp.enums.ReserveStatus;

@Entity
public class Reservation {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "z_reservation_generator")
	@SequenceGenerator(name="z_reservation_generator", sequenceName = "z_reservation_seq", allocationSize=1)
	private long resID;
	
	private String idTag;
	
	private String parentIdTag;
	
	private String providerId;

	
	@ManyToOne
	private ChargingUnit unit;
	
	
	private int connectorRef;
	
	@NotNull
	private ZonedDateTime startDate;
	
	private ZonedDateTime expiryDate;
	
	private ZonedDateTime timestamp;
	
	private ZonedDateTime cancelDate;
	
	
	@Enumerated(EnumType.STRING)
	private ReserveStatus status;

	
	public Reservation() {
		super();

	}

	public Reservation(ReservationDTO res) {
		this.startDate = res.getStartDate();
		this.expiryDate = res.getExpiryDate();
		this.timestamp = ZonedDateTime.now();
		this.status = res.getStatus();
		this.connectorRef = res.getConnRef();
		this.idTag = res.getIdTag();
		this.providerId = res.getProviderId();
	}

	public ChargingUnit getUnit() {
		return unit;
	}

	public void setUnit(ChargingUnit unit) {
		this.unit = unit;
	}

	public int getConnectorRef() {
		return connectorRef;
	}

	public void setConnectorRef(int connectorRef) {
		this.connectorRef = connectorRef;
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

	public ZonedDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(ZonedDateTime timestamp) {
		this.timestamp = timestamp;
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


	@Override
	public String toString() {
		return "Reservation [resID=" + resID 
				+ ", unit=" + unit + ", startDate=" + startDate + ", expiryDate=" + expiryDate + ", timestamp=" + timestamp
				+ ", cancelDate=" + cancelDate + ", status=" + status + "]";
	}

	public String getIdTag() {
		return idTag;
	}

	public void setIdTag(String idTag) {
		this.idTag = idTag;
	}

	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (resID ^ (resID >>> 32));
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
		Reservation other = (Reservation) obj;
		if (resID != other.resID)
			return false;
		return true;
	}

	public String getParentIdTag() {
		return parentIdTag;
	}

	public void setParentIdTag(String parentIdTag) {
		this.parentIdTag = parentIdTag;
	}
	
	
	
	
	
}
