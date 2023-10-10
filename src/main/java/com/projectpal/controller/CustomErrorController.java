package com.projectpal.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projectpal.exception.InternalServerErrorException;
import com.projectpal.exception.ResourceNotFoundException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class CustomErrorController implements ErrorController {

	@RequestMapping("/error")
	public void handleError(HttpServletRequest request) {

		System.out.println("custom error controller called");
		
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

		if (status != null) {
			Integer statusCode = Integer.valueOf(status.toString());

			if (statusCode == 404)
				throw new ResourceNotFoundException("Endpoint not found");
		}
		throw new InternalServerErrorException("Unknown error occured");
	}

}
