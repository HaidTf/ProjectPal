package com.projectpal.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class JacksonConfig {

	private final Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder;
	
	@Bean
	ObjectMapper objectMapper() {

		ObjectMapper mapper = jackson2ObjectMapperBuilder.build();

		mapper.registerModule(new JavaTimeModule());

		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		
		return mapper;
	}
}
