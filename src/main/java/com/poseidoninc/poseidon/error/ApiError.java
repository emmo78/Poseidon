package com.poseidoninc.poseidon.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * ApiError class represents error response returned by the API.
 * Used to encapsulate details about the error such as the HTTP status code, timestamp, and error message.
 *
 * @author olivier morel
 */

@Getter
@Setter
public class ApiError {

    private HttpStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime timestamp;
    private String message;

    private ApiError() {
        timestamp = LocalDateTime.now();
    }

    public ApiError(HttpStatus status, Throwable ex) {
        this();
        this.status = status;
        this.message = ex.getMessage();
    }

    public ApiError(HttpStatus status, String message) {
        this();
        this.status = status;
        this.message = message;
    }
}
