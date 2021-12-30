package it.besmart.ocppLib.wrappers.Response;

import java.util.ArrayList;
import java.util.List;

import it.besmart.ocppLib.dto.ErrorOCPP;

public class ErrorListResponse extends AckResponse  {

	
	private List<ErrorOCPP> errors = new ArrayList<>();
	
	public ErrorListResponse() {
		super();
	}

	public List<ErrorOCPP> getErrors() {
		return errors;
	}

	public void setErrors(List<ErrorOCPP> errors) {
		this.errors = errors;
	}

	@Override
	public String toString() {
		return "ErrorListResponse [errors=" + errors + "]";
	}

	
}