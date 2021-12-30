package it.besmart.ocpp.wrappers;

public class DiagnosticsWrapper {

	
	private boolean result;
	
	private String message;
	
	private long id;

	
	public DiagnosticsWrapper() {
		super();
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DiagnosticsWrapper [result=").append(result).append(", message=").append(message)
				.append(", id=").append(id).append("]");
		return builder.toString();
	}
	
	
}
