package it.besmart.ocpp.dtos;

import java.time.ZonedDateTime;

import it.besmart.ocpp.enums.ConnectorStatusComplete;
import it.besmart.ocpp.model.RecordConnectorStatus;

public class RecordConnectorStatusDTO {
	
	private long recordID;
	
	private ConnectorDTO connector;
	
	private ConnectorStatusComplete status;
	
	private ZonedDateTime sendTime;
	
	private ZonedDateTime timestamp;

	
	public RecordConnectorStatusDTO() {
		super();
		this.timestamp = ZonedDateTime.now();
	}
	
	
	public RecordConnectorStatusDTO(RecordConnectorStatus record) {
		
		this.recordID = record.getRecordId();
		
		this.connector = new ConnectorDTO(record.getConnector());
		this.status = record.getStatus();
		this.sendTime = record.getSendTime();
		this.timestamp = record.getTimestamp();
	}
	
	
	public long getRecordID() {
		return recordID;
	}

	public void setRecordID(long recordID) {
		this.recordID = recordID;
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


	public ConnectorDTO getConnector() {
		return connector;
	}


	public void setConnector(ConnectorDTO connector) {
		this.connector = connector;
	}


	@Override
	public String toString() {
		return "RecordConnectorStatusDTO [recordID=" + recordID + ", connector=" + connector + ", status=" + status
				+ ", sendTime=" + sendTime + ", timestamp=" + timestamp + "]";
	}
	
	
	
}
