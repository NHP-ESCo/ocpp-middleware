package it.besmart.ocpp.exceptions;

@SuppressWarnings("serial")
public class StationException extends RuntimeException {
    public StationException(final String message) {
        super(message);
    }
}