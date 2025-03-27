package com.cryptoTrading.backend.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cryptoTrading.backend.annotation.CurrentUserId;
import com.cryptoTrading.backend.dto.UserDto;
import com.cryptoTrading.backend.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody @Valid UserDto userDto) {
        return new ResponseEntity<>(userService.registerUser(userDto), HttpStatus.CREATED);
    }
    
    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.authenticateUser(userDto));
    }

    @PostMapping("/reset")
    public ResponseEntity<UserDto> reset(@CurrentUserId Long currentUserId) {
        return ResponseEntity.ok(userService.resetUser(currentUserId));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@CurrentUserId Long currentUserId) {
        return ResponseEntity.ok(userService.fetchUserById(currentUserId));
    }
}
