package it.besmart.ocppLib.wrappers.Response;

import it.besmart.ocppLib.wrappers.Response.AckResponse;

public class SavePlayerResponse extends AckResponse {

	private String code;
	
	public SavePlayerResponse() {
		super();
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	
}
