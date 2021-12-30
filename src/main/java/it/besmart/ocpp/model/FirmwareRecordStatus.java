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

import eu.chargetime.ocpp.model.firmware.FirmwareStatus;

@Entity
public class FirmwareRecordStatus {


	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "z_firmware_generator")
	@SequenceGenerator(name="z_firmware_generator", sequenceName = "z_firmware_seq", allocationSize=1)
	private long id;	
		
	@ManyToOne
	private ChargingStation station;
	
	private ZonedDateTime requestTime;
	
	//TODO: our status different from OCPP
	@Enumerated(EnumType.STRING)
	private FirmwareStatus status;
	
	private ZonedDateTime installedTime;

	
	public FirmwareRecordStatus() {
		super();
		
	}

	public ChargingStation getStation() {
		return station;
	}

	public void setStation(ChargingStation station) {
		this.station = station;
	}

	public ZonedDateTime getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(ZonedDateTime requestTime) {
		this.requestTime = requestTime;
	}

	public FirmwareStatus getStatus() {
		return status;
	}

	public void setStatus(FirmwareStatus status) {
		this.status = status;
	}

	public ZonedDateTime getInstalledTime() {
		return installedTime;
	}

	public void setInstalledTime(ZonedDateTime installedTime) {
		this.installedTime = installedTime;
	}

	public long getId() {
		return id;
	}
	
	
}
