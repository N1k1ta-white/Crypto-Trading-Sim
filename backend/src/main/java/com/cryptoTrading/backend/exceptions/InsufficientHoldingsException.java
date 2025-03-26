package com.cryptoTrading.backend.exceptions;

import java.math.BigDecimal;

public class InsufficientHoldingsException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public InsufficientHoldingsException(String message) {
        super(message);
    }
    
    public InsufficientHoldingsException(String cryptoSymbol, BigDecimal requested, BigDecimal available) {
        super(String.format("Insufficient %s holdings: Requested %.8f but only %.8f available", 
              cryptoSymbol, requested, available));
    }
}
