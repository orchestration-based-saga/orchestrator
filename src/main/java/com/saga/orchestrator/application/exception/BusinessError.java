package com.saga.orchestrator.application.exception;

public class BusinessError extends RuntimeException{

    public BusinessError(String message) {
        super(message);
    }
}
