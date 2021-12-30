package it.besmart.ocppLib.wrappers.Request;

public class FirmwareRequest {

	private String evseId;
	
	private String modelCode; //if model code update all stations 
	private String operatorCode;
	
	private String externalUri; //if not set default location in server
	
	public FirmwareRequest() {
		super();
	}

	public String getEvseId() {
		return evseId;
	}

	public void setEvseId(String evseId) {
		this.evseId = evseId;
	}

	@Override
	public String toString() {
		return "StationRequest [evseId=" + evseId + "]";
	}

	public String getExternalUri() {
		return externalUri;
	}

	public void setExternalUri(String externalUri) {
		this.externalUri = externalUri;
	}

	public String getModelCode() {
		return modelCode;
	}

	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}

	public String getOperatorCode() {
		return operatorCode;
	}

	public void setOperatorCode(String operatorCode) {
		this.operatorCode = operatorCode;
	}
	
	
}
