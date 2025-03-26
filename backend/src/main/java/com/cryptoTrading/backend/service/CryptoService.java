package com.cryptoTrading.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cryptoTrading.backend.entity.Crypto;
import com.cryptoTrading.backend.repository.CryptoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CryptoService {

    @Value("${crypto.pair.currency}")
    private String currency;
    
    private final CryptoRepository cryptoRepository;

    public List<Crypto> getAllCryptos() {
        return cryptoRepository.findAll();
    }

    public String[] getAllCryptoPairs() {
        return cryptoRepository.findAll().stream()
        .map(Crypto::getCode)
        .map(code -> code + "/" + currency)
        .toArray(String[]::new);
    }

    public Crypto getCryptoByCode(String code) {
        return cryptoRepository.findById(code)
        .orElseThrow(() -> new IllegalArgumentException("Crypto with code " + code + " not found"));
    }
}
