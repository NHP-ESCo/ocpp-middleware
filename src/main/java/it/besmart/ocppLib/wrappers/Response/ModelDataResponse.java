package it.besmart.ocppLib.wrappers.Response;


import it.besmart.ocppLib.dto.config.StationModel;

public class ModelDataResponse extends AckResponse {

	private StationModel model;
	
	public ModelDataResponse() {
		super();
	}

	public StationModel getModel() {
		return model;
	}

	public void setModel(StationModel model) {
		this.model = model;
	}

	
	
	
}
