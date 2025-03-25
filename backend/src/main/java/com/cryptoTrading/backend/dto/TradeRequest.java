package com.cryptoTrading.backend.dto;

import java.math.BigDecimal;

import com.cryptoTrading.backend.enums.TradeType;

import lombok.Data;

@Data
public class TradeRequest {
    private String cryptoSymbol;
    private TradeType tradeType;
    private BigDecimal fixedPrice;
    private BigDecimal quantity;
}
