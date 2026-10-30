package com.cricketpulse.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class ScoringException extends RuntimeException {
    public ScoringException(String message) {
        super(message);
    }
}
