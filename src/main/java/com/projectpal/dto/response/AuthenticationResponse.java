package com.projectpal.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class AuthenticationResponse {

	private final String token;

}
