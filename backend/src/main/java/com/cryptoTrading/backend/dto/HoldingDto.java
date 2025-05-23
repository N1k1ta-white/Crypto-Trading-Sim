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
public class HoldingDto {
    BigDecimal averagePricing;
    String cryptoCode;
    BigDecimal amount;
}
