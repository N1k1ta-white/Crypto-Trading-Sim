package com.cryptoTrading.backend.exceptions;

public class PriceExpiredException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public PriceExpiredException(String message) {
        super(message);
    }
    
    public PriceExpiredException(String cryptoSymbol, long expirationTimeSeconds) {
        super(String.format("Price for %s has expired. Quotes are valid for %d seconds", 
              cryptoSymbol, expirationTimeSeconds));
    }
}
