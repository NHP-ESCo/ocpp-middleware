package it.besmart.ocppLib.wrappers.Response;

public class DiagnosticsResult {

	private boolean result;
	
	private String evseId;
	
	private String message;
	
	private String email;
	
	private String link;

	
	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getEvseId() {
		return evseId;
	}

	public void setEvseId(String evseId) {
		this.evseId = evseId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DiagnosticsResult [result=").append(result).append(", evseId=").append(evseId)
				.append(", message=").append(message).append(", email=").append(email).append(", link=").append(link)
				.append("]");
		return builder.toString();
	}
	
	
}
