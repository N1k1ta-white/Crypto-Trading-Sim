package com.cryptoTrading.backend.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import com.cryptoTrading.backend.annotation.CurrentUserId;
import com.cryptoTrading.backend.dto.UserDto;
import com.cryptoTrading.backend.exceptions.UnauthorizedOperationException;
import com.cryptoTrading.backend.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

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

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@CurrentUserId Long currentUserId) {
        return ResponseEntity.ok(userService.fetchUserById(currentUserId));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(
            @PathVariable Long id, 
            @CurrentUserId Long currentUserId) {
        // Check if user is accessing their own record or is an admin
        if (!id.equals(currentUserId)) {
            throw new UnauthorizedOperationException("access", "user/" + id);
        }
        return ResponseEntity.ok(userService.fetchUserById(id));
    }
    
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        // In a real app, this would likely be restricted to admins
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id, 
            @RequestBody UserDto userDto,
            @CurrentUserId Long currentUserId) {
        // Check if user is updating their own record
        if (!id.equals(currentUserId)) {
            throw new UnauthorizedOperationException("update", "user/" + id);
        }
        return ResponseEntity.ok(userService.updateUser(id, userDto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id,
            @CurrentUserId Long currentUserId) {
        // Check if user is deleting their own record
        if (!id.equals(currentUserId)) {
            throw new UnauthorizedOperationException("delete", "user/" + id);
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
