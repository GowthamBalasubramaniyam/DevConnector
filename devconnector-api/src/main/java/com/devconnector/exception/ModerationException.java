package com.devconnector.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// This tells Spring to automatically send a 400 Bad Request when this is thrown
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ModerationException extends RuntimeException {
    public ModerationException(String message) {
        super(message);
    }
}