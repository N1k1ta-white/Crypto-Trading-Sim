package com.cryptoTrading.backend.dto;

import java.math.BigDecimal;

import com.cryptoTrading.backend.enums.TradeType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TradeRequest {
    private String symbol;
    private TradeType tradeType;
    private BigDecimal fixedPrice;
    private BigDecimal amount;
}
