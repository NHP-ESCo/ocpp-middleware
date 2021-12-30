package it.besmart.ocpp.services;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.besmart.ocpp.model.Model;
import it.besmart.ocpp.model.ModelCapability;
import it.besmart.ocpp.model.ModelCapabilityKey;
import it.besmart.ocpp.repositories.ModelCapabilityRepository;
import it.besmart.ocpp.services.interfaces.IModelCapabilityService;
import it.besmart.ocppLib.dto.Capability;
import it.besmart.ocppLib.enumeration.CapabilityType;
import it.besmart.specifications.enums.Operations;
import it.besmart.specifications.query.CustomSpecificationBuilder;

@Service
@Transactional
public class ModelCapabilityService implements IModelCapabilityService {

	@Autowired
	private ModelCapabilityRepository repo;
	
	
	@Override
	public List<ModelCapability> findAllCapabilities(Model model) {
		CustomSpecificationBuilder builder = new CustomSpecificationBuilder();
		
		builder.with("key.model", Operations.EQUAL, model);
		
		return repo.findAll(builder.build());
		
	}


	@Override
	public ModelCapability findCapability(Model model, CapabilityType type) {
		
		Optional<ModelCapability>  o = repo.findById(new ModelCapabilityKey(type, model));
		
		return o.isEmpty() ? null : o.get();
	}


	@Override
	public ModelCapability findAndUpdateCapability(Model model, Capability c) {
		ModelCapability cap = findCapability(model, c.getType());
		if(cap==null) {
			cap = new ModelCapability();
			cap.setKey(new ModelCapabilityKey(c.getType(), model));
		}
		
		cap.setEditable(c.isEditable());
		
		return repo.save(cap);
	}
	
	
	@Override
	public void saveModelCapabilities(Model entity, Set<Capability> caps) {
		List<CapabilityType> capabilities = new LinkedList<CapabilityType>(Arrays.asList(CapabilityType.values()));
		
		for(Capability c : caps) {
			findAndUpdateCapability(entity, c);
			capabilities.remove(c.getType());
		}
		
		for(CapabilityType c : capabilities) {
			Capability cap = new Capability();
			cap.setEditable(false);
			cap.setType(c);
			
			findAndUpdateCapability(entity, cap);
		}
	}
}
