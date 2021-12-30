package it.besmart.ocpp.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import it.besmart.ocppLib.wrappers.Response.AckResponse;



@ControllerAdvice("it.besmart.ocpp")
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
    	//ex.printStackTrace();
    	
    	String errorMessage = "";
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {

        	errorMessage = String.format("Field %s %s", error.getField(), error.getDefaultMessage());
            logger.debug(error);

            break;
        }

        AckResponse apiError = new AckResponse(false, errorMessage);

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
			HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    	
    	String message =  ex.getMostSpecificCause().getMessage();
    	int endMessage = message.indexOf(" at ");
    	
    	logger.error("error:" + message);
    	message =  message.substring(0, endMessage);
    	

        AckResponse apiError = new AckResponse(false, message);
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
	}
    
    
    @ExceptionHandler(value = {PoolException.class, StationException.class, UnsupportedRequestException.class, DuplicatedEntityException.class, 
    		SmartProfileException.class, ParameterException.class, ConfigurationException.class, ModelException.class, UserException.class})
    public ResponseEntity<AckResponse> handleGenericExceptions(RuntimeException ex) {
    	logger.debug("Exception : " + ex.toString());
    	
    	AckResponse apiError = new AckResponse(false, ex.getLocalizedMessage());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);

    }
//    
//    
//    @ExceptionHandler(value = {SecurityException.class})
//
//    public ResponseEntity<AckResponse> handleSpecificExceptions(RuntimeException ex) {
//
//    	AckResponse apiError = new AckResponse(false, ex.getLocalizedMessage());
//        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.CONFLICT);
//
//    }
    
}

