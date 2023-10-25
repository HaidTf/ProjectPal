package com.projectpal.validation;

import java.util.Set;

import org.springframework.data.domain.Sort;

import com.projectpal.exception.BadRequestException;

public class SortObjectValidator {

	public static void validateSortObjectProperties(Set<String> allowedProperties, Sort sort) {

		for (Sort.Order order : sort) {
			if (!allowedProperties.contains(order.getProperty()))
				throw new BadRequestException("Invalid sort property");

		}

	}

}
