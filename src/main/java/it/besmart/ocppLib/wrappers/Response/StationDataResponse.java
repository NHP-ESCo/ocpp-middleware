package it.besmart.ocppLib.wrappers.Response;


import it.besmart.ocppLib.dto.StationData;

public class StationDataResponse extends AckResponse {

	private StationData stationData;

	
	public StationDataResponse() {
		super();
	}
	
	public StationData getStationData() {
		return stationData;
	}

	public void setStationData(StationData stationData) {
		this.stationData = stationData;
	}
	
}
