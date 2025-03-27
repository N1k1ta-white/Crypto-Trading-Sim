package com.cryptoTrading.backend.enums;

public enum TradeType {
    BUY, SELL, UNKNOWN;

    public static TradeType fromString(String text) {
        if (text != null) {
            try {
                return valueOf(text.toUpperCase());
            } catch (IllegalArgumentException e) {
                return UNKNOWN;
            }
        }
        return UNKNOWN;
    }
}
