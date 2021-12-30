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

import it.besmart.ocpp.enums.ErrorType;

@Entity
public class StoredLog {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "z_log_generator")
	@SequenceGenerator(name="z_log_generator", sequenceName = "z_log_seq", allocationSize=1)
	private long logID;
	
	
	private ZonedDateTime timestamp;
	
	private String message;
	
	@ManyToOne
	private ChargingStation station;
	
	@ManyToOne
	private Connector connector;
	
	private String session;
	
	@Enumerated(EnumType.STRING)
	private ErrorType type;
	
	private boolean solved;
	
	public StoredLog() {
		super();
		this.timestamp = ZonedDateTime.now();
		this.solved = false;
	}
	
	public StoredLog(String message, ErrorType type) {
		this.message = message;
		this.type = type;
		this.timestamp = ZonedDateTime.now();
		this.solved = false;
	}
	

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getLogID() {
		return logID;
	}

	public ZonedDateTime getTimestamp() {
		return timestamp;
	}


	public ErrorType getType() {
		return type;
	}


	public void setType(ErrorType type) {
		this.type = type;
	}

	public ChargingStation getStation() {
		return station;
	}

	public void setStation(ChargingStation station) {
		this.station = station;
	}

	public boolean isSolved() {
		return solved;
	}

	public void setSolved(boolean solved) {
		this.solved = solved;
	}

	public Connector getConnector() {
		return connector;
	}

	public void setConnector(Connector connector) {
		this.connector = connector;
	}

	@Override
	public String toString() {
		return "StoredLog [message=" + message + ", type=" + type
				+ "]";
	}

	public void setTimestamp(ZonedDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}
	
	
}
