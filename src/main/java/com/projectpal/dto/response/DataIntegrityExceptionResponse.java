package com.projectpal.dto.response;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

import lombok.Getter;

@Getter
public final class DataIntegrityExceptionResponse {

	public DataIntegrityExceptionResponse(DataIntegrityViolationException ex) {
		Throwable cause = ex.getCause();
		if (cause instanceof ConstraintViolationException) {
			isConstraintViolation = true;
			ConstraintViolationException constraintEx = (ConstraintViolationException) cause;
			String constraint = constraintEx.getConstraintName();
			constraintViolated = constraint;
		} else {
			isConstraintViolation = true;
			constraintViolated = null;
		}
	}

	private final String constraintViolated;

	private final boolean isConstraintViolation;

	public boolean isConstraintViolation() {
		return isConstraintViolation;
	}

}
