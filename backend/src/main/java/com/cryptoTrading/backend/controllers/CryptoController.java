package com.cryptoTrading.backend.controllers;

import lombok.RequiredArgsConstructor;

import java.util.Collection;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cryptoTrading.backend.clients.KrakenWebSocketClient;
import com.cryptoTrading.backend.dto.CryptocurrencyDto;
import com.cryptoTrading.backend.repository.CryptoInfoRepository;

import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/crypto")
@RequiredArgsConstructor
public class CryptoController {

    private final CryptoInfoRepository cryptoInfoRepository;

    @GetMapping
    public ResponseEntity<Collection<CryptocurrencyDto>> getCurrency() {
        return ResponseEntity.ok(cryptoInfoRepository.getAllCryptocurrencyInfo());
    }

    @GetMapping("/{code}")
    public ResponseEntity<CryptocurrencyDto> getMethodName(@RequestParam String code) {
        return cryptoInfoRepository.getCryptocurrencyInfo(code) != null ? 
            ResponseEntity.ok(cryptoInfoRepository.getCryptocurrencyInfo(code)) : 
            ResponseEntity.notFound().build();
    }
    

}
