package com.cryptoTrading.backend.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.cryptoTrading.backend.entity.User;

import com.cryptoTrading.backend.exceptions.InsufficientFundsException;
import com.cryptoTrading.backend.exceptions.InvalidTradeRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cryptoTrading.backend.dto.TradeRequest;
import com.cryptoTrading.backend.dto.TransactionDto;
import com.cryptoTrading.backend.entity.Crypto;
import com.cryptoTrading.backend.entity.Transaction;
import com.cryptoTrading.backend.enums.TradeType;
import com.cryptoTrading.backend.mapper.TransactionMapper;
import com.cryptoTrading.backend.repository.TransactionRepository;
import com.cryptoTrading.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TradeService {

    @Value("${crypto.pair.currency}")
    private String currency;

    private final TransactionRepository transactionRepository;
    private final CryptoService cryptoService;
    private final TransactionMapper transactionMapper;
    private final HoldingService holdingService;
    private final UserRepository userRepository;

    @Transactional
    public void removeTransactions(Long userId) {
        transactionRepository.deleteByUserId(userId);
    }
    
    public TransactionDto trade(TradeRequest tradeRequest, User user) {
        Transaction res = null;
        TradeType tradeType = TradeType.valueOf(tradeRequest.getTradeType());
        res = switch (tradeType) {
            case BUY -> buy(tradeRequest, user);
            case SELL -> sell(tradeRequest, user);
            default -> throw new InvalidTradeRequestException("Invalid trade type");
        };

        return transactionMapper.transactionToDto(res);
    }

    public Transaction buy(TradeRequest tradeRequest, User user) {
        BigDecimal total = tradeRequest.getFixedPrice().multiply(tradeRequest.getAmount());

        if (user.getBalance().compareTo(total) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }
        user.setBalance(user.getBalance().subtract(total));
        userRepository.save(user);

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

    public Transaction sell(TradeRequest tradeRequest, User user) {
        String code = tradeRequest.getSymbol().replace("/" + currency, "");

        BigDecimal amount = tradeRequest.getAmount();
        BigDecimal fixedPrice = tradeRequest.getFixedPrice();
        BigDecimal total = fixedPrice.multiply(amount);
        Crypto crypto = cryptoService.getCryptoByCode(code);

        holdingService.removeCrypto(user, crypto, amount);

        BigDecimal profit = holdingService.calculateProfit(user, crypto, amount, fixedPrice);

        user.setBalance(user.getBalance().add(total));
        userRepository.save(user);

        Transaction transaction = Transaction.builder()
            .user(user)
            .crypto(cryptoService.getCryptoByCode(code))
            .tradeType(TradeType.SELL)
            .amount(amount)
            .fixedPrice(fixedPrice)
            .profit(profit)
            .build();

        return transactionRepository.save(transaction);
    }

    public List<TransactionDto> getTransactions(Long userId) {
        return transactionRepository.findByUserId(userId).stream()
            .map(transactionMapper::transactionToDto)
            .collect(Collectors.toList());
    }
}
