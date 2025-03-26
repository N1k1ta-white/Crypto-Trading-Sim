package com.cryptoTrading.backend.exceptions;

public class MarketClosedException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public MarketClosedException(String message) {
        super(message);
    }
    
    public MarketClosedException() {
        super("The market is currently closed. Trading is not available at this time.");
    }
}
