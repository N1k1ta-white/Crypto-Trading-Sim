package com.cryptoTrading.backend.exceptions;

import java.math.BigDecimal;

public class InsufficientFundsException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public InsufficientFundsException(String message) {
        super(message);
    }
    
    public InsufficientFundsException(BigDecimal required, BigDecimal available) {
        super(String.format("Insufficient funds: Required %.2f but only %.2f available", 
              required, available));
    }
}
