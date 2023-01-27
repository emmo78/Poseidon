package com.poseidoninc.poseidon.exception;

/**
 * Thrown when user not found in database
 * @author Olivier MOREL
 *
 */
public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ResourceNotFoundException(String message) {
		super(message);
	}
}