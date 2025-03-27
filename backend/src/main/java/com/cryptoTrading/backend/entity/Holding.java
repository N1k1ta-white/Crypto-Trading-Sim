package com.cryptoTrading.backend.entity;

import java.math.BigDecimal;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name = "holdings")
public class Holding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 30, scale = 20)
    private BigDecimal averagePricing;

    @ManyToOne(targetEntity = Crypto.class, optional = false, fetch = FetchType.LAZY, cascade = {})
    @JoinColumn(nullable = false)
    private Crypto crypto;

    @Column(nullable = false, precision = 30, scale = 20)
    private BigDecimal amount;

    @ManyToOne(targetEntity = User.class, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;
}
