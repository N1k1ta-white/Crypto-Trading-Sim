package com.cryptoTrading.backend.exceptions;

import java.math.BigDecimal;

public class InvalidTradeRequestException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public InvalidTradeRequestException(String message) {
        super(message);
    }
    
    public InvalidTradeRequestException(String symbol, BigDecimal amount, String reason) {
        super(String.format("Invalid trade request for %s amount %.8f: %s", symbol, amount, reason));
    }
}
