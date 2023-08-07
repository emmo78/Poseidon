package com.poseidoninc.poseidon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;

import com.poseidoninc.poseidon.service.RequestService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice(annotations = Controller.class)
@Slf4j
@AllArgsConstructor
public class ControllerExceptionHandler {
	
	private final RequestService requestService;
	

}
