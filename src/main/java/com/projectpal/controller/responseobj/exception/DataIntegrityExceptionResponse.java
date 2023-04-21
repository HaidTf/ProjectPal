package com.projectpal.controller.responseobj.exception;


import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;




public class DataIntegrityExceptionResponse {

	public DataIntegrityExceptionResponse(DataIntegrityViolationException ex) {
		Throwable cause = ex.getCause();
		if (cause instanceof ConstraintViolationException) {
			setConstraintViolation(true);
			ConstraintViolationException constraintEx = (ConstraintViolationException) cause;
			String constraint = constraintEx.getConstraintName();
			setConstraintViolated(constraint);
		}
		else {
			setConstraintViolation(false);
		}
	}
	
	private String constraintViolated;

	private boolean isConstraintViolation;
	

	
	public String getConstraintViolated() {
		return constraintViolated;
	}

	public void setConstraintViolated(String constraintViolated) {
		this.constraintViolated = constraintViolated;
	}

	public boolean isConstraintViolation() {
		return isConstraintViolation;
	}

	public void setConstraintViolation(boolean isConstraintViolation) {
		this.isConstraintViolation = isConstraintViolation;
	}

	
}
