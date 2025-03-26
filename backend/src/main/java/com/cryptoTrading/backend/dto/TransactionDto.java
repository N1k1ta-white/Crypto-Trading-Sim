package com.cryptoTrading.backend.dto;

import java.math.BigDecimal;
import java.time.Instant;

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
    private String currency;
    private BigDecimal fixedPrice;
    private BigDecimal amount;
    private Instant createdAt;
}
