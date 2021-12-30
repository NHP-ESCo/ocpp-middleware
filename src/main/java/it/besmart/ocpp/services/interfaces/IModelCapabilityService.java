package it.besmart.ocpp.services.interfaces;

import java.util.List;
import java.util.Set;

import it.besmart.ocpp.model.Model;
import it.besmart.ocpp.model.ModelCapability;
import it.besmart.ocppLib.dto.Capability;
import it.besmart.ocppLib.enumeration.CapabilityType;

public interface IModelCapabilityService {

	List<ModelCapability> findAllCapabilities(Model model);

	ModelCapability findCapability(Model model, CapabilityType type);

	ModelCapability findAndUpdateCapability(Model entity, Capability c);

	void saveModelCapabilities(Model entity, Set<Capability> caps);

}
