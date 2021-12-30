package it.besmart.ocppLib.dto;

import java.time.ZonedDateTime;

import it.besmart.ocppLib.enumeration.ErrorCode;

public class ErrorOCPP {

	private long id;
	
	private String evseId;
	
	private int connectorId;
	
	private String txSession;
	
	private ErrorCode code;
	
	private String message;
	
	private ZonedDateTime timestamp;
	

	public ErrorOCPP() {
		super();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ZonedDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(ZonedDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public ErrorCode getCode() {
		return code;
	}

	public void setCode(ErrorCode code) {
		this.code = code;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEvseId() {
		return evseId;
	}

	public void setEvseId(String evseId) {
		this.evseId = evseId;
	}

	public int getConnectorId() {
		return connectorId;
	}

	public void setConnectorId(int connectorId) {
		this.connectorId = connectorId;
	}

	@Override
	public String toString() {
		return "ErrorOCPP [id=" + id + ", evseId=" + evseId + ", message=" + message + ", timestamp=" + timestamp
				+ ", code=" + code + "]";
	}

	public String getTxSession() {
		return txSession;
	}

	public void setTxSession(String txSession) {
		this.txSession = txSession;
	}
	
	
	
}
