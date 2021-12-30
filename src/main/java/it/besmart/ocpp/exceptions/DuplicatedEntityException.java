package it.besmart.ocpp.exceptions;

@SuppressWarnings("serial")
public class DuplicatedEntityException extends RuntimeException {

	public DuplicatedEntityException(String message) {
		super(message);
	}
	
}
