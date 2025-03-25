package com.cryptoTrading.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cryptoTrading.backend.entity.Crypto;

@Repository
public interface CryptoRepository extends JpaRepository<Crypto, String> {
}