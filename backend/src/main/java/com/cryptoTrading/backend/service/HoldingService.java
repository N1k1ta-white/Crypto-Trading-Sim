package com.cryptoTrading.backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cryptoTrading.backend.dto.HoldingDto;
import com.cryptoTrading.backend.entity.Crypto;
import com.cryptoTrading.backend.entity.Holding;
import com.cryptoTrading.backend.entity.User;
import com.cryptoTrading.backend.mapper.HoldingMapper;
import com.cryptoTrading.backend.repository.HoldingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HoldingService {
    
    private final HoldingRepository holdingRepository;
    private final HoldingMapper holdingMapper;

    @Transactional
    public void removeHolding(Long userId) {
        holdingRepository.deleteByUserId(userId);
    }

    public BigDecimal calculateProfit(User user, Crypto crypto, BigDecimal amount,
                                      BigDecimal fixedPrice) {
        return holdingRepository.findByUserIdAndCryptoCode(user.getId(), crypto.getCode())
            .map(holding -> amount.multiply(fixedPrice)
                            .subtract(holding.getAveragePricing().multiply(amount)))
            .orElse(BigDecimal.ZERO);
    }

    public List<HoldingDto> getHoldings(Long userId) {
        return holdingRepository.findByUserId(userId).stream()
            .map(holdingMapper::holdingToDto)
            .collect(Collectors.toList());
    }

    public void addCrypto(User user, Crypto crypto, BigDecimal amount, BigDecimal fixedPrice) {
        holdingRepository.findByUserIdAndCryptoCode(user.getId(), crypto.getCode())
            .ifPresentOrElse(
                holding -> {
                    holding.setAmount(holding.getAmount().add(amount));
                    holding.setAveragePricing(
                        holding.getAveragePricing()
                            .multiply(holding.getAmount().subtract(amount))
                            .add(fixedPrice.multiply(amount))
                            .divide(holding.getAmount(), 30, RoundingMode.HALF_UP)
                    );
                    holdingRepository.save(holding);
                },
                () -> holdingRepository.save(Holding.builder()
                    .user(user)
                    .crypto(crypto)
                    .amount(amount)
                    .averagePricing(fixedPrice)
                    .build())
            );
    }

    public void removeCrypto(User user, Crypto crypto, BigDecimal amount) {
        holdingRepository.findByUserIdAndCryptoCode(user.getId(), crypto.getCode())
            .ifPresentOrElse(
            holding -> {
                if (holding.getAmount().compareTo(amount) < 0) {
                throw new IllegalArgumentException("Insufficient crypto amount to remove");
                }
                holding.setAmount(holding.getAmount().subtract(amount));
                if (holding.getAmount().compareTo(BigDecimal.ZERO) == 0) {
                holdingRepository.delete(holding);
                } else {
                holdingRepository.save(holding);
                }
            },
            () -> {
                throw new IllegalArgumentException("Holding with the specified crypto does not exist");
            }
            );
    }
}
