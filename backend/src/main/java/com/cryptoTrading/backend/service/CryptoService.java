package com.cryptoTrading.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cryptoTrading.backend.entity.Crypto;
import com.cryptoTrading.backend.repository.CryptoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CryptoService {
    
    private final CryptoRepository cryptoRepository;

    public List<Crypto> getAllCryptos() {
        return cryptoRepository.findAll();
    }
}
