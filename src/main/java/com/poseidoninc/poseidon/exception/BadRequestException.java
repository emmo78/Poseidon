package com.poseidoninc.poseidon.exception;

/**
 * Exception that is thrown on an empty Optional @RequestBody in some RestController method  argument
 * It extends the RuntimeException class.
 *
 * @author oliver morel
 */
public class BadRequestException  extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
