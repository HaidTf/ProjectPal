package com.projectpal.exception.client;

public class EntityNotFoundException extends ResourceNotFoundException {

	private static final long serialVersionUID = 10L;

	public EntityNotFoundException(Class<?> type) {
		super(type.getSimpleName() + "not found");
	}

}
