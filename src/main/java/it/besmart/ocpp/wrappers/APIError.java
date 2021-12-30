package it.besmart.ocpp.wrappers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import it.besmart.ocpp.enums.ErrorMessageResponse;
import it.besmart.ocppLib.wrappers.Response.AckResponse;

public class APIError {

	private final static Logger logger = LoggerFactory.getLogger(APIError.class);
	
	//TODO: generalize to extension of ackresponse
	public static ResponseEntity<AckResponse> response(ErrorMessageResponse error) {
		String message = error.getValue();
		AckResponse response = new AckResponse(false, message);
		logger.debug(message);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	public static ResponseEntity<AckResponse> response(String message) {
		AckResponse response = new AckResponse(false, message);
		logger.debug(message);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
}
