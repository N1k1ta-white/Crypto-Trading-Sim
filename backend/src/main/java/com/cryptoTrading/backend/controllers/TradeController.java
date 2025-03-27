package com.cryptoTrading.backend.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cryptoTrading.backend.annotation.CurrentUserId;
import com.cryptoTrading.backend.dto.TradeRequest;
import com.cryptoTrading.backend.dto.TransactionDto;
import com.cryptoTrading.backend.entity.User;
import com.cryptoTrading.backend.service.TradeService;
import com.cryptoTrading.backend.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/trade")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;
    private final UserService userService;
    
    @PostMapping
    public ResponseEntity<TransactionDto> trade(@Valid @RequestBody TradeRequest tradeRequest,
                                                @CurrentUserId Long userId) {
        User user = userService.getUserByIdInternal(userId);
        TransactionDto transaction = tradeService.trade(tradeRequest, user);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TransactionDto>> getTransactions(@CurrentUserId Long userId) {
        return ResponseEntity.ok(tradeService.getTransactions(userId));
    }
}
