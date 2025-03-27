package com.cryptoTrading.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cryptoTrading.backend.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
