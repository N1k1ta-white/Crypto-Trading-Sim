package com.cryptoTrading.backend.service;

import java.math.BigDecimal;

import com.cryptoTrading.backend.entity.User;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cryptoTrading.backend.dto.TradeRequest;
import com.cryptoTrading.backend.dto.TransactionDto;
import com.cryptoTrading.backend.entity.Crypto;
import com.cryptoTrading.backend.entity.Holding;
import com.cryptoTrading.backend.entity.Transaction;
import com.cryptoTrading.backend.enums.TradeType;
import com.cryptoTrading.backend.mapper.TransactionMapper;
import com.cryptoTrading.backend.repository.HoldingRepository;
import com.cryptoTrading.backend.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TradeService {

    @Value("${crypto.pair.currency}")
    private String currency;

    private final UserService userService;
    private final TransactionRepository transactionRepository;
    private final CryptoService cryptoService;
    private final TransactionMapper transactionMapper;
    private final HoldingService holdingService;

    // TODO: Implement trade logic
    // Add assets to Holdings
    
    public TransactionDto trade(TradeRequest tradeRequest, Long userId) {
        Transaction res = null;
        if (tradeRequest.getTradeType().equals(TradeType.BUY)) {
            res = buy(tradeRequest, userId);
        } else {
            // Sell logic
        }
        
        if (res == null) {
            throw new IllegalArgumentException("Invalid trade type");
        }

        return transactionMapper.transactionToDto(res);
    }

    public Transaction buy(TradeRequest tradeRequest, Long userId) {
        User user = userService.getUserByIdInternal(userId);

        BigDecimal balance = user.getBalance();
        BigDecimal total = tradeRequest.getFixedPrice().multiply(tradeRequest.getAmount());
        if (balance.compareTo(total) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        String code = tradeRequest.getSymbol().replace("/" + currency, "");
        Crypto crypto = cryptoService.getCryptoByCode(code);

        holdingService.addCrypto(user, crypto, tradeRequest.getAmount(),
         tradeRequest.getFixedPrice());
        
        Transaction transaction = Transaction.builder()
            .user(user)
            .crypto(crypto)
            .tradeType(TradeType.BUY)
            .amount(tradeRequest.getAmount())
            .fixedPrice(tradeRequest.getFixedPrice())
            .build();
    
        return transactionRepository.save(transaction);
    }

    public Transaction sell(TradeRequest tradeRequest, Long userId) {
        User user = userService.getUserByIdInternal(userId);
        String code = tradeRequest.getSymbol().replace("/" + currency, "");

        BigDecimal amount = tradeRequest.getAmount();
        BigDecimal fixedPrice = tradeRequest.getFixedPrice();
        BigDecimal total = fixedPrice.multiply(amount);
        Crypto crypto = cryptoService.getCryptoByCode(code);

        holdingService.removeCrypto(user, crypto, amount);
        user.setBalance(user.getBalance().add(total));

        Transaction transaction = Transaction.builder()
            .user(user)
            .crypto(cryptoService.getCryptoByCode(code))
            .tradeType(TradeType.SELL)
            .amount(amount)
            .fixedPrice(fixedPrice)
            .build();

        return transactionRepository.save(transaction);
    }
}
