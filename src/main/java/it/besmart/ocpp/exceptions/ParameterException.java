package it.besmart.ocpp.exceptions;

@SuppressWarnings("serial")
public class ParameterException extends RuntimeException {
	public ParameterException(final String message) {
        super(message);
    }
}
