package it.besmart.ocpp.exceptions;

@SuppressWarnings("serial")
public class ConfigurationException extends RuntimeException {

	public ConfigurationException(String message) {
		super(message);
	}
}
