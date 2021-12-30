package it.besmart.ocpp.exceptions;

@SuppressWarnings("serial")
public class SmartProfileException extends RuntimeException {
	public SmartProfileException(final String message) {
        super(message);
    }
}
