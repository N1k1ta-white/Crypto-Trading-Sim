package com.cryptoTrading.backend.entity;

import java.math.BigDecimal;
import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import com.cryptoTrading.backend.enums.TradeType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String uuid;

    @ManyToOne(cascade = {})
    private User user;

    @ManyToOne(cascade = {})
    private Crypto crypto;

    @Column(nullable = false)
    private TradeType tradeType;

    @Column(nullable = false, precision = 30, scale = 20)
    private BigDecimal fixedPrice;

    @Column(nullable = false, precision = 30, scale = 20)
    private BigDecimal amount;

    @Column(nullable = true, updatable = false)
    private BigDecimal profit;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
