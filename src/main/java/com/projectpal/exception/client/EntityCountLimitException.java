package com.projectpal.exception.client;

public class EntityCountLimitException extends ConflictException {

	private static final long serialVersionUID = 11L;

	public EntityCountLimitException(Class<?> type) {
		super(type.getSimpleName()+"count exceeded max limit");
	}

}
