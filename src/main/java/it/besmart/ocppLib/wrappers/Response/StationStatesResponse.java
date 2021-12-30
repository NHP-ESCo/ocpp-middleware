package it.besmart.ocppLib.wrappers.Response;

import it.besmart.ocppLib.dto.StationStates;


public class StationStatesResponse extends AckResponse {
	
	private StationStates stationStates;
	
	
	public StationStatesResponse() {
		super();
	}


	public StationStates getStationStates() {
		return stationStates;
	}


	public void setStationStates(StationStates stationStates) {
		this.stationStates = stationStates;
	}



}
