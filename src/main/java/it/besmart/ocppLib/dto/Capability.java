package it.besmart.ocppLib.dto;

import it.besmart.ocppLib.enumeration.CapabilityStatus;
import it.besmart.ocppLib.enumeration.CapabilityType;

public class Capability {

	private CapabilityType type;
	
	private CapabilityStatus status;
	
	private boolean editable;
	
	public Capability() {
		super();
	}

	public Capability(CapabilityType cap) {
		this.type = cap;
	}

	public CapabilityType getType() {
		return type;
	}

	public void setType(CapabilityType type) {
		this.type = type;
	}

	public CapabilityStatus getStatus() {
		return status;
	}

	public void setStatus(CapabilityStatus status) {
		this.status = status;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
}
