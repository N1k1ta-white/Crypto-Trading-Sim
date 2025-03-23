package com.cryptoTrading.backend.controllers;

import lombok.RequiredArgsConstructor;

import java.util.Collection;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cryptoTrading.backend.clients.KrakenWebSocketClient;
import com.cryptoTrading.backend.dto.CryptocurrencyDto;

@RestController
@RequestMapping("/api/crypto")
@RequiredArgsConstructor
public class CryptoController {

    private final KrakenWebSocketClient krakenWebSocketClient;

    @GetMapping
    public ResponseEntity<Collection<CryptocurrencyDto>> getCurrency() {
        return ResponseEntity.ok(krakenWebSocketClient.fetchTradingPairs());
    }

}
