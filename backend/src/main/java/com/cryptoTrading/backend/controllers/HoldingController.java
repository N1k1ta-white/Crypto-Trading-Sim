package com.cryptoTrading.backend.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cryptoTrading.backend.annotation.CurrentUserId;
import com.cryptoTrading.backend.dto.HoldingDto;
import com.cryptoTrading.backend.service.HoldingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/holding")
@RequiredArgsConstructor
public class HoldingController {
    
    private final HoldingService holdingService;
    
    @GetMapping
    public ResponseEntity<List<HoldingDto>> getHoldings(@CurrentUserId Long userId) {
        return ResponseEntity.ok(holdingService.getHoldings(userId));
    }
}
