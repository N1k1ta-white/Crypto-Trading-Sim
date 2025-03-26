package com.cryptoTrading.backend.exceptions;

public class UnauthorizedOperationException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public UnauthorizedOperationException(String message) {
        super(message);
    }
    
    public UnauthorizedOperationException(String operation, String resourceId) {
        super(String.format("You are not authorized to %s resource with ID: %s", operation, resourceId));
    }
}
