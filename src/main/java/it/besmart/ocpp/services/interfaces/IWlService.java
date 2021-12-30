package it.besmart.ocpp.services.interfaces;

import java.util.List;
import java.util.Set;

import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.WlCard;

public interface IWlService {

	
	WlCard findByIdTag(String tag);
	
	List<WlCard> getLocalList(ChargingStation cs);
	
	Set<WlCard> cleanLocalList(ChargingStation entity);
	
	WlCard addRFID(String tag);


	boolean belongToLocalList(ChargingStation cs, String idTag);
	
}
