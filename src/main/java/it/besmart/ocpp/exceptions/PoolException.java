package it.besmart.ocpp.exceptions;

@SuppressWarnings("serial")
public class PoolException extends RuntimeException {
    public PoolException(final String message) {
        super(message);
    }
}
