package com.poseidoninc.poseidon.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.poseidoninc.poseidon.exception.ResourceConflictException;
import com.poseidoninc.poseidon.services.RequestService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice(annotations = Controller.class)
@Slf4j
@AllArgsConstructor
public class ControllerExceptionHandler {
	
	private final RequestService requestService;
	
	@ExceptionHandler(ResourceConflictException.class)
    public String ressourceConflictException(ResourceConflictException ex, WebRequest request, RedirectAttributes attributes) {
		String errorMessage = ex.getMessage();
		String requestStr = requestService.requestToString(request);
		log.error("{} : {} : {}",
				requestStr,
				((ServletWebRequest) request).getHttpMethod(),
				errorMessage);
		attributes.addAttribute("errorMessage", errorMessage);
        return "redirect:"+requestStr.split("uri=")[0];
    }
}
