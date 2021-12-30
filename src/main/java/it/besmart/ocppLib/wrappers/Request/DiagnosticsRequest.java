package it.besmart.ocppLib.wrappers.Request;

import javax.validation.constraints.NotEmpty;

public class DiagnosticsRequest {

	@NotEmpty
	private String evseId;
	
	private String startTime; //ISO_OFFSET_DATE_TIME
	
	private int hours;
	
	
	@NotEmpty
	private String email;
	
	
	//used here

	private String downloadUri; //where station upload
	

	
	
	public DiagnosticsRequest() {
		super();
	}

	public String getEvseId() {
		return evseId;
	}

	public void setEvseId(String evseId) {
		this.evseId = evseId;
	}

	@Override
	public String toString() {
		return "StationRequest [evseId=" + evseId + "]";
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public int getHours() {
		return hours;
	}

	public void setHours(int hours) {
		this.hours = hours;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDownloadUri() {
		return downloadUri;
	}

	public void setDownloadUri(String downloadUri) {
		this.downloadUri = downloadUri;
	}
	
	
}
