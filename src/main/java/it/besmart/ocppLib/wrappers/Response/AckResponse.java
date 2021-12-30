package it.besmart.ocppLib.wrappers.Response;


public class AckResponse {
	
	private boolean result;
	
	private String message;

	
	public AckResponse() {
		super();
	}
	
	
	public AckResponse(boolean b, String message) {
		result = b;
		this.message = message;
	}


	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
