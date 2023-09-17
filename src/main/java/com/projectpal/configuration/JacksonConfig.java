package com.projectpal.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class JacksonConfig {

	@Bean
	ObjectMapper objectMapper() {

		JsonMapper mapper = JsonMapper.builder().build();

		mapper.registerModule(new JavaTimeModule());

		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		
		return mapper;
	}
}
