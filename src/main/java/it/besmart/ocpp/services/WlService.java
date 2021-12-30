package it.besmart.ocpp.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.besmart.ocpp.model.ChargingStation;
import it.besmart.ocpp.model.WlCard;
import it.besmart.ocpp.repositories.WlCardRepository;
import it.besmart.ocpp.services.interfaces.IWlService;
import it.besmart.specifications.enums.Operations;
import it.besmart.specifications.query.CustomSpecificationBuilder;

@Service
@Transactional
public class WlService implements IWlService {
	
	private final Logger logger = LoggerFactory.getLogger(WlService.class);
	
	@Autowired
	private WlCardRepository repo;
	
	
	@Override
	public WlCard addRFID(String tag) {
		WlCard entity = new WlCard();
		entity.setIdTag(tag);
		
		//entity.setParent(emp.getMainCustomer());
		
		logger.debug("New RFID: " + entity.toString());
		
		return repo.save(entity);
	}

	@Override
	public WlCard findByIdTag(String tag) {
		
		return repo.findByIdTag(tag);
	}


	@Override
	public List<WlCard> getLocalList(ChargingStation cs) {
		
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
		builder.with("authorizedStations", Operations.IN_MANYTOMANY, cs, "authorizedStations");
		
		Specification<WlCard> spec = builder.build();
		
		return repo.findAll(spec);

	}

	@Override
	public boolean belongToLocalList(ChargingStation cs, String idTag) {
		for( WlCard cust : getLocalList(cs) ) {
			if(cust.getIdTag().equals(idTag)) {
				return true;
			}
		}
		
		return false;
	}	

	@Override
	public Set<WlCard> cleanLocalList(ChargingStation entity) {
		
		List<WlCard> list = getLocalList(entity);
		Set<WlCard> cleanedSet = new HashSet<>();
		
		for (WlCard cust : list) {
			cleanedSet.add(cust);
		}
		return cleanedSet;
	}
	
}
