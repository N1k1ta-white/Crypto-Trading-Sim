package com.cryptoTrading.backend.controllers;

import com.cryptoTrading.backend.clients.KrakenWebSocketClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/crypto")
@RequiredArgsConstructor
public class CryptoController {

    private final KrakenWebSocketClient krakenWebSocketClient;

    @GetMapping()
    public String getCoin() {
        krakenWebSocketClient.connect();
        return "THis is a coin";
    }

    @GetMapping("/{name}")
    public String getCoin(String name) {
        return "THis is a " + name;
    }
}
