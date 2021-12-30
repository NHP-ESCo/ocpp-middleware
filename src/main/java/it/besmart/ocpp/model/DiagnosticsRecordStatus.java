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

import eu.chargetime.ocpp.model.firmware.DiagnosticsStatus;

@Entity
public class DiagnosticsRecordStatus {


	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "z_diagnostics_generator")
	@SequenceGenerator(name="z_diagnostics_generator", sequenceName = "z_diagnostics_seq", allocationSize=1)
	private long id;	
		
	@ManyToOne
	private ChargingStation station;
	
	@Enumerated(EnumType.STRING)
	private DiagnosticsStatus status;
	
	private ZonedDateTime requestTime;
	
	private ZonedDateTime uploadTime;
	
	private String filePath;
	
	private boolean sftp;
	
	private String email;
	
	private String externalLink;

	public DiagnosticsRecordStatus() {
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

	public long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setId(long id) {
		this.id = id;
	}

	public DiagnosticsStatus getStatus() {
		return status;
	}

	public void setStatus(DiagnosticsStatus status) {
		this.status = status;
	}

	public ZonedDateTime getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(ZonedDateTime uploadTime) {
		this.uploadTime = uploadTime;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String localPath) {
		this.filePath = localPath;
	}

	public boolean isSftp() {
		return sftp;
	}

	public void setSftp(boolean sftp) {
		this.sftp = sftp;
	}

	public String getExternalLink() {
		return externalLink;
	}

	public void setExternalLink(String externalLink) {
		this.externalLink = externalLink;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DiagnosticsRecordStatus [status=").append(status).append(", localPath=").append(filePath)
				.append(", sftp=").append(sftp).append(", email=").append(email).append(", externalLink=")
				.append(externalLink).append("]");
		return builder.toString();
	}
	
	
}
