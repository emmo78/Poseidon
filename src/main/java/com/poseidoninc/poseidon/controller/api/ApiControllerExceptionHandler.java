package com.poseidoninc.poseidon.controller.api;

import com.poseidoninc.poseidon.error.ApiError;
import com.poseidoninc.poseidon.exception.BadRequestException;
import com.poseidoninc.poseidon.service.RequestService;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice(annotations = RestController.class)
@Slf4j
@RequiredArgsConstructor
public class ApiControllerExceptionHandler {
	
	private final RequestService requestService;

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> methodArgumentNotValidException(MethodArgumentNotValidException manvex, WebRequest request) {
		ApiError error = new ApiError(HttpStatus.BAD_REQUEST, manvex);
        log.error("{} : {} : {}",
                requestService.requestToString(request),
                ((ServletWebRequest) request).getHttpMethod(),
                error.getMessage());
        return new ResponseEntity<>(error, error.getStatus());
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ApiError> badRequestException(BadRequestException brex, WebRequest request) {
		ApiError error = new ApiError(HttpStatus.BAD_REQUEST, brex);
        log.error("{} : {} : {}",
                requestService.requestToString(request),
                ((ServletWebRequest) request).getHttpMethod(),
                error.getMessage());
        return new ResponseEntity<>(error, error.getStatus());
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiError> constraintViolationException(MethodArgumentNotValidException manvex, WebRequest request) {
		ApiError error = new ApiError(HttpStatus.BAD_REQUEST, manvex);
		log.error("{} : {} : {}",
				requestService.requestToString(request),
				((ServletWebRequest) request).getHttpMethod(),
				error.getMessage());
		return new ResponseEntity<>(error, error.getStatus());
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiError> dataIntegrityViolationException(DataIntegrityViolationException dive, WebRequest request) {
		ApiError error = new ApiError(HttpStatus.CONFLICT, dive);
		log.error("{} : {} : {}",
				requestService.requestToString(request),
				((ServletWebRequest) request).getHttpMethod(),
				error.getMessage());
		return new ResponseEntity<>(error, error.getStatus());

	}

	@ExceptionHandler(UnexpectedRollbackException.class)
	public ResponseEntity<ApiError> unexpectedRollbackException(UnexpectedRollbackException urex, WebRequest request) {
		ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, urex);
		log.error("{} : {} : {}",
				requestService.requestToString(request),
				((ServletWebRequest) request).getHttpMethod(),
				error.getMessage());
		return new ResponseEntity<>(error, error.getStatus());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> unexpectedException(Exception e, WebRequest request) {
		ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, e);
		log.error("{} : {} : {}",
				requestService.requestToString(request),
				((ServletWebRequest) request).getHttpMethod(),
				error.getMessage());
		return new ResponseEntity<>(error, error.getStatus());
	}
}
