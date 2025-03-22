package com.cryptoTrading.backend.dto;

import java.math.BigDecimal;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CryptocurrencyDto {
    private String symbol;
    private BigDecimal bid;
    @SerializedName("bid_qty")
    private BigDecimal bidQty;
    private BigDecimal ask;
    @SerializedName("ask_qty")
    private BigDecimal askQty;
    private BigDecimal last;
    private BigDecimal volume;
    private BigDecimal vwap;
    private BigDecimal low;
    private BigDecimal high;
    private BigDecimal change;
    @SerializedName("change_pct")
    private BigDecimal changePct;
}
