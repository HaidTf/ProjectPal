package com.projectpal.validation;

import org.springframework.data.domain.Pageable;

import com.projectpal.controller.APIConstants;
import com.projectpal.exception.ConflictException;

public class PageValidator {

	public static void validatePage(int index, int size) {
		if (index < 0)
			throw new ConflictException("Page index is under 0");
		if (size > APIConstants.MAX_PAGE_SIZE)
			throw new ConflictException("Page size exceeded maximum");
	}

	public static void validatePageable(Pageable pageable) {
		if (pageable.getPageNumber() < 0)
			throw new ConflictException("Page index is under 0");
		if (pageable.getPageSize() > APIConstants.MAX_PAGE_SIZE)
			throw new ConflictException("Page size exceeded maximum");
	}
}
