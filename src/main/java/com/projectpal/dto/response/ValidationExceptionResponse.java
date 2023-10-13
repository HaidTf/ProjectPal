package com.projectpal.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class ValidationExceptionResponse {

	private final List<String> errors;

}
