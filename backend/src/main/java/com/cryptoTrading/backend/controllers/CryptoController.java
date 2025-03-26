package com.cryptoTrading.backend.controllers;

import lombok.RequiredArgsConstructor;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cryptoTrading.backend.dto.CryptocurrencyDto;
import com.cryptoTrading.backend.exceptions.ResourceNotFoundException;
import com.cryptoTrading.backend.repository.CryptoInfoRepository;

@RestController
@RequestMapping("/api/crypto")
@RequiredArgsConstructor
public class CryptoController {

    private final CryptoInfoRepository cryptoInfoRepository;

    @Value("${crypto.pair.currency}")
    private String currency;

    @GetMapping
    public ResponseEntity<Collection<CryptocurrencyDto>> getCurrency() {
        return ResponseEntity.ok(cryptoInfoRepository.getAllCryptocurrencyInfo());
    }

    @GetMapping("/{code}")
    public ResponseEntity<CryptocurrencyDto> getCryptoInfo(@PathVariable String code) {
        code = code.concat("/" + currency);
        CryptocurrencyDto cryptoInfo = cryptoInfoRepository.getCryptocurrencyInfo(code);
        
        if (cryptoInfo == null) {
            throw new ResourceNotFoundException("Cryptocurrency", "code", code);
        }
        
        return ResponseEntity.ok(cryptoInfo);
    }
}
