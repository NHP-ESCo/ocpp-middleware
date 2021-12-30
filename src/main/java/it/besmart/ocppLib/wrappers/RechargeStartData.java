package it.besmart.ocppLib.wrappers;


public class RechargeStartData {

	private String evseId;
	
	private int connector;
	
	private String roamingUserCode;
	
	private String providerCode;
	
	private String sessionId;
	
	private String profileCode; 

	private boolean freeMode;

	public RechargeStartData() {
		super();
	}
	

	public String getProviderCode() {
		return providerCode;
	}

	public void setProviderCode(String providerCode) {
		this.providerCode = providerCode;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Override
	public String toString() {
		return "RechargeStartData [evseId=" + evseId + ", connector=" + connector + ", roamingUserCode="
				+ roamingUserCode + ", providerCode=" + providerCode + ", sessionId=" + sessionId + "]";
	}

	public String getRoamingUserCode() {
		return roamingUserCode;
	}

	public void setRoamingUserCode(String roamingUserCode) {
		this.roamingUserCode = roamingUserCode;
	}

	public String getEvseId() {
		return evseId;
	}


	public void setEvseId(String evseId) {
		this.evseId = evseId;
	}

	public int getConnector() {
		return connector;
	}

	public void setConnector(int connector) {
		this.connector = connector;
	}


	public String getProfileCode() {
		return profileCode;
	}


	public void setProfileCode(String profileCode) {
		this.profileCode = profileCode;
	}


	public boolean isFreeMode() {
		return freeMode;
	}


	public void setFreeMode(boolean freeMode) {
		this.freeMode = freeMode;
	}
	
	
}
