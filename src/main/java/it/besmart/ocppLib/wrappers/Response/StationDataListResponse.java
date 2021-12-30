package it.besmart.ocppLib.wrappers.Response;


import it.besmart.ocppLib.dto.StationData;

import java.util.ArrayList;
import java.util.List;


public class StationDataListResponse extends AckResponse {

	private List<StationData> stationData = new ArrayList<StationData>();

	public StationDataListResponse() {
		super();
	}

	public List<StationData> getStationData() {
		return stationData;
	}

	public void setStationData(List<StationData> stationData) {
		this.stationData = stationData;
	}
	
	public void addStationData(StationData stationData) {
		this.stationData.add(stationData);
	}

	
}
