package it.besmart.ocppLib.wrappers.Response;


import java.util.ArrayList;
import java.util.List;

import it.besmart.ocppLib.dto.config.StationModel;

public class ModelDataListResponse extends AckResponse {

	private List<StationModel> models = new ArrayList<>();
	
	public ModelDataListResponse() {
		super();
	}

	public List<StationModel> getModels() {
		return models;
	}

	public void setModels(List<StationModel> models) {
		this.models = models;
	}
	
	public void addModel(StationModel model) {
		this.models.add( model );
	}
	
	
	
}
