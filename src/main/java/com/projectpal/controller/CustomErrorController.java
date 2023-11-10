package com.projectpal.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectpal.exception.client.ResourceNotFoundException;
import com.projectpal.exception.server.InternalServerErrorException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class CustomErrorController implements ErrorController {

	@RequestMapping("/error")
	public void handleError(HttpServletRequest request) {

		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

		if (status != null) {
			Integer statusCode = Integer.valueOf(status.toString());

			log.debug("CustomErrorController invoked with status {}", statusCode);

			if (statusCode == 404)
				throw new ResourceNotFoundException("Endpoint not found");
		}
		
		log.debug("CustomErrorController invoked");
		
		throw new InternalServerErrorException("Unknown error occured");
	}

}
