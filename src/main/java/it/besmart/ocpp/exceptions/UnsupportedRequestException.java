package it.besmart.ocpp.exceptions;

@SuppressWarnings("serial")
public class UnsupportedRequestException extends RuntimeException {
    public UnsupportedRequestException(final String message) {
        super(message);
    }
    
    public UnsupportedRequestException() {
        super("Unsupported request for this station");
    }
}
