package it.besmart.ocppLib.wrappers.Response;

public class SaveStationResponse extends AckResponse {

	private String evseId;
	
	public SaveStationResponse() {
		super();
	}

	public String getEvseId() {
		return evseId;
	}

	public void setEvseId(String evseId) {
		this.evseId = evseId;
	}
	
	
}
