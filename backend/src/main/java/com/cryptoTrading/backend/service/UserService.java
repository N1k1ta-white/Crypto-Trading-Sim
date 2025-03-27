package com.cryptoTrading.backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import com.cryptoTrading.backend.dto.UserDto;
import com.cryptoTrading.backend.entity.User;
import com.cryptoTrading.backend.exceptions.AuthenticationException;
import com.cryptoTrading.backend.exceptions.DuplicateResourceException;
import com.cryptoTrading.backend.exceptions.ResourceNotFoundException;
import com.cryptoTrading.backend.mapper.UserMapper;
import com.cryptoTrading.backend.repository.UserRepository;
import com.cryptoTrading.backend.config.JwtService;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
public class UserService {

    @Value("${reset.balance}")
    private BigDecimal resetBalance;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final HoldingService holdingService;
    private final TradeService tradeService;

    @Transactional
    public UserDto resetUser(Long userId) {
        User user = getUserByIdInternal(userId);
        
        tradeService.removeTransactions(userId);
        
        holdingService.removeHolding(userId);
        
        user.setBalance(resetBalance);
        userRepository.save(user);
        
        return userMapper.userToDto(user);
    }

    public UserDto registerUser(UserDto userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new DuplicateResourceException("User", "username", userDto.getUsername());
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new DuplicateResourceException("User", "email", userDto.getEmail());
        }

        User user = userMapper.dtoToUser(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        return userMapper.userToDto(userRepository.save(user));
    }
    
    public UserDto authenticateUser(UserDto userDto) {
        User user = userRepository.findByUsername(userDto.getUsername())
            .orElseThrow(() -> new AuthenticationException("Invalid username or password"));
            
        if (!passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Invalid username or password");
        }
        
        String token = jwtService.generateToken(user, user.getId());

        userDto = userMapper.userToDto(user);
        userDto.setToken(token);

        return userDto;
    }
    
    public UserDto fetchUserById(Long id) {
        return userMapper.userToDto(getUserByIdInternal(id));
    }
    
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
            .map(userMapper::userToDto)
            .collect(Collectors.toList());
    }
    
    public UserDto updateUser(Long id, UserDto userDto) {
        User existingUser = getUserByIdInternal(id);
        
        if (userDto.getUsername() != null && 
            !userDto.getUsername().equals(existingUser.getUsername())) {
            if (userRepository.existsByUsername(userDto.getUsername())) {
                throw new DuplicateResourceException("User", "username", userDto.getUsername());
            }
            existingUser.setUsername(userDto.getUsername());
        }
        
        if (userDto.getEmail() != null && !userDto.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(userDto.getEmail())) {
                throw new DuplicateResourceException("User", "email", userDto.getEmail());
            }
            existingUser.setEmail(userDto.getEmail());
        }
        
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        
        return userMapper.userToDto(userRepository.save(existingUser));
    }
    
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
    }

    public BigDecimal getUserBalance(Long id) {
        return getUserByIdInternal(id).getBalance();
    }

    public User getUserByIdInternal(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }
}
