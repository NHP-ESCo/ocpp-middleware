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

import it.besmart.ocpp.dtos.RecordConnectorStatusDTO;
import it.besmart.ocpp.enums.ConnectorStatusComplete;

@Entity
public class RecordConnectorStatus {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "z_curecordstatus_generator")
	@SequenceGenerator(name="z_curecordstatus_generator", sequenceName = "z_curecordstatus_seq", allocationSize=1)
	private long recordID;
	
	@ManyToOne
	private Connector connector;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private ConnectorStatusComplete status;
	
	private ZonedDateTime sendTime;
	
	private ZonedDateTime timestamp;
	
	
	public RecordConnectorStatus() {
		super();
		this.timestamp = ZonedDateTime.now();
	}

	
	public RecordConnectorStatus(RecordConnectorStatusDTO newStatus) {
		this.setStatus(newStatus.getStatus());
		this.setSendTime(newStatus.getSendTime());
		if(newStatus.getTimestamp()!=null)
			this.timestamp = newStatus.getTimestamp();
		else
			this.timestamp = ZonedDateTime.now();
	}


	public long getRecordId() {
		return recordID;
	}

	public Connector getConnector() {
		return connector;
	}

	public void setConnector(Connector conn) {
		this.connector = conn;
	}

	public ConnectorStatusComplete getStatus() {
		return status;
	}

	public void setStatus(ConnectorStatusComplete status) {
		this.status = status;
	}

	public ZonedDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(ZonedDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public ZonedDateTime getSendTime() {
		return sendTime;
	}

	public void setSendTime(ZonedDateTime sendTime) {
		this.sendTime = sendTime;
	}
	

	@Override
	public String toString() {
		return "RecordConnectorStatus [recordID=" + recordID + ", status=" + status
				+ ", sendTime=" + sendTime + ", timestamp=" + timestamp + "]";
	}


}
