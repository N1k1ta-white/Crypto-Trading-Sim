package com.cryptoTrading.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cryptoTrading.backend.entity.Holding;

public interface HoldingRepository extends JpaRepository<Holding, Long> {
    Optional<Holding> findByUserIdAndCryptoCode(Long userId, String cryptoCode);
    List<Holding> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}