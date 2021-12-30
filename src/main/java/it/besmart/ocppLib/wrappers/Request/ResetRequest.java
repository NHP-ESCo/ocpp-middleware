package it.besmart.ocppLib.wrappers.Request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class ResetRequest {

	@NotNull
	@NotEmpty
	private String evseId;
	
	@NotNull
	private ResetType resetType;

	
	public ResetType getResetType() {
		return resetType;
	}

	public void setResetType(ResetType resetType) {
		this.resetType = resetType;
	}

	public String getEvseId() {
		return evseId;
	}

	public void setEvseId(String evseId) {
		this.evseId = evseId;
	}
	
	
	public enum ResetType {
		  Hard,
		  Soft
	}


	@Override
	public String toString() {
		return "ResetRequest [evseId=" + evseId + ", resetType=" + resetType + "]";
	}
	
}
