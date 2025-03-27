package com.cryptoTrading.backend.dto;

import java.math.BigDecimal;

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
public class TransactionDto {
    private String uuid;
    private String cryptoCode;
    private String tradeType;
    private BigDecimal fixedPrice;
    private BigDecimal amount;
    private BigDecimal profit;
    private BigDecimal createdAt;
}
