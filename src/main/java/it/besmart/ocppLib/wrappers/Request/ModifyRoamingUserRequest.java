package it.besmart.ocppLib.wrappers.Request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


public class ModifyRoamingUserRequest {

	@NotNull
	@NotEmpty
	private String evseId;
	
	@NotNull
	private String roamingUserCode;
	
	@NotNull
	private ProviderStatusEnum providerStatus;
	
	
	public ModifyRoamingUserRequest() {
		super();
	}

	public String getEvseId() {
		return evseId;
	}

	public void setEvseId(String evseId) {
		this.evseId = evseId;
	}

	public ProviderStatusEnum getProviderStatus() {
		return providerStatus;
	}

	public void setProviderStatus(ProviderStatusEnum providerStatus) {
		this.providerStatus = providerStatus;
	}


	public String getRoamingUserCode() {
		return roamingUserCode;
	}

	public void setRoamingUserCode(String roamingUserCode) {
		this.roamingUserCode = roamingUserCode;
	}


	public enum ProviderStatusEnum {
		  
	    ENABLED("ENABLED"),
	    
	    DISABLED("DISABLED");

	    private String value;

	    ProviderStatusEnum(String value) {
	      this.value = value;
	    }

	    public String getValue() {
	      return value;
	    }

	    @Override
	    public String toString() {
	      return String.valueOf(value);
	    }

	  
	  }


	@Override
	public String toString() {
		return "ModifyRoamingUserRequest [evseId=" + evseId + ", roamingUserCode=" + roamingUserCode
				+ ", providerStatus=" + providerStatus + "]";
	}
	
}
