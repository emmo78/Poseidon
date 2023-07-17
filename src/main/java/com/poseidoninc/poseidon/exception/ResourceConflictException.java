package com.poseidoninc.poseidon.exception;

import lombok.Getter;

/**
 * Thrown when a new user try to register with a userName already known
 * @author olivi
 *
 */
@Getter
public class ResourceConflictException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private Object object;

	public ResourceConflictException(String message) { //, Object object
		super(message);
		this.object = object;
	}
}