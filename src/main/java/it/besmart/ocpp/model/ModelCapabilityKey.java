package it.besmart.ocpp.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import it.besmart.ocppLib.enumeration.CapabilityType;

@Embeddable
public class ModelCapabilityKey implements Serializable {

	@Enumerated(EnumType.STRING)
	private CapabilityType capability;
	
	@ManyToOne
	private Model model;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2213314240692369074L;

	
	public ModelCapabilityKey() {
		super();
	}

	
	public ModelCapabilityKey(CapabilityType capability, Model model) {
		super();
		this.capability = capability;
		this.model = model;
	}


	public CapabilityType getCapability() {
		return capability;
	}

	public void setCapability(CapabilityType capability) {
		this.capability = capability;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	@Override
	public int hashCode() {
		return Objects.hash(capability, model);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModelCapabilityKey other = (ModelCapabilityKey) obj;
		return capability == other.capability && Objects.equals(model, other.model);
	}
	
	
}
