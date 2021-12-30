package it.besmart.ocpp.dtos;

import java.time.ZonedDateTime;

import it.besmart.ocpp.enums.StationStatusComplete;

public class RecordStationStatusDTO {

	private long recordID;
	
	private ChargingStationDTO station;
	
	private StationStatusComplete status;
	
	private ZonedDateTime sendTime;
	
	private ZonedDateTime timestamp;
	
	public RecordStationStatusDTO() {
		super();
	}
	
	
	public RecordStationStatusDTO(StationStatusComplete status, ZonedDateTime sendTime) {
		super();
		this.status = status;
		this.sendTime = sendTime;
		this.timestamp = ZonedDateTime.now();
	}

	
	@Override
	public String toString() {
		return "CSRecordStatusDTO [recordID=" + recordID + ", station=" + station + ", status=" + status
				+ ", timestamp=" + timestamp + "]";
	}

	public long getRecordID() {
		return recordID;
	}

	public void setRecordID(long recordID) {
		this.recordID = recordID;
	}

	public ZonedDateTime getTimestamp() {
		return timestamp;
	}

	public StationStatusComplete getStatus() {
		return status;
	}

	public void setStatus(StationStatusComplete status) {
		this.status = status;
	}

	public ChargingStationDTO getStation() {
		return station;
	}

	public void setStation(ChargingStationDTO cs) {
		this.station = cs;
	}

	public ZonedDateTime getSendTime() {
		return sendTime;
	}

	public void setSendTime(ZonedDateTime sendTime) {
		this.sendTime = sendTime;
	}
	
}
