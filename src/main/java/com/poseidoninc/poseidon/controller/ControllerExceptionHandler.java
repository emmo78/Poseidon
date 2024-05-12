package com.poseidoninc.poseidon.controller;

import com.poseidoninc.poseidon.service.RequestService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice(annotations = Controller.class)
@Slf4j
@AllArgsConstructor
public class ControllerExceptionHandler {
	
	private final RequestService requestService;

	@ExceptionHandler(UnexpectedRollbackException.class)
	public String unexpectedRollbackException(UnexpectedRollbackException ex, WebRequest request, Model model) {
		String errorMessage = ex.getMessage();
		log.error("{} : {} : {}",
				requestService.requestToString(request),
				((ServletWebRequest) request).getHttpMethod(),
				errorMessage);
		model.addAttribute("errorMessage", errorMessage);
		return "error";
	}

	@ExceptionHandler(Exception.class)
	public String unexpectedException(Exception e, WebRequest request, Model model) {
		log.error("{} : {} : {}",
				requestService.requestToString(request),
				((ServletWebRequest) request).getHttpMethod(),
				e.getMessage());
		model.addAttribute("errorMessage", "Internal Server Error");
		return "error";
	}
}
