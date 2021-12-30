package it.besmart.ocpp.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import it.besmart.ocppLib.enumeration.CapabilityType;

@Entity
public class ModelCapability {

	@EmbeddedId
	private ModelCapabilityKey key;
	
	private boolean editable;

	
	public ModelCapability() {
		super();
	}

	public CapabilityType getCapability() {
		return key.getCapability();
	}
	public ModelCapabilityKey getKey() {
		return key;
	}

	public void setKey(ModelCapabilityKey key) {
		this.key = key;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (editable ? 1231 : 1237);
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModelCapability other = (ModelCapability) obj;
		if (editable != other.editable)
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}
	
	
}
