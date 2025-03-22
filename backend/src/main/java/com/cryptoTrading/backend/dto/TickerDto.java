package com.cryptoTrading.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TickerDto {
    private String channel;
    private String type;
    private CryptocurrencyDto[] data;
}
