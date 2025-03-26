package com.cryptoTrading.backend.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cryptoTrading.backend.annotation.CurrentUserId;
import com.cryptoTrading.backend.dto.TradeRequest;
import com.cryptoTrading.backend.dto.TransactionDto;
import com.cryptoTrading.backend.exceptions.InvalidTradeRequestException;
import com.cryptoTrading.backend.service.TradeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/trade")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;
    
    @PostMapping
    public ResponseEntity<TransactionDto> trade(@Valid @RequestBody TradeRequest tradeRequest,
                                                @CurrentUserId Long userId) {
        TransactionDto transaction = tradeService.trade(tradeRequest, userId);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }
}
