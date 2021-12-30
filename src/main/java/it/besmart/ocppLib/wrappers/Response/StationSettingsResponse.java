package it.besmart.ocppLib.wrappers.Response;

import it.besmart.ocppLib.dto.StationSettings;

public class StationSettingsResponse extends AckResponse {
	
	private StationSettings data;
	
	public StationSettingsResponse() {
		super();
	}

	public StationSettings getData() {
		return data;
	}

	public void setData(StationSettings data) {
		this.data = data;
	}

}
