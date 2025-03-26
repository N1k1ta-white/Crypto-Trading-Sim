package com.cryptoTrading.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cryptoTrading.backend.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

}
