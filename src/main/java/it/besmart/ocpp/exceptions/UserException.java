package it.besmart.ocpp.exceptions;

@SuppressWarnings("serial")
public class UserException extends RuntimeException {

	public UserException(String message) {
		super(message);
	}
}