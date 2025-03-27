package com.cryptoTrading.backend.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    @NotNull
    @NotBlank
    private String symbol;

    @NotNull
    private String tradeType;

    @NotNull
    @Positive
    private BigDecimal fixedPrice;

    @NotNull
    @Positive
    private BigDecimal amount;
}
