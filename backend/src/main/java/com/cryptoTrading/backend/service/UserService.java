package com.cryptoTrading.backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.BadCredentialsException;

import com.cryptoTrading.backend.dto.UserDto;
import com.cryptoTrading.backend.entity.User;
import com.cryptoTrading.backend.mapper.UserMapper;
import com.cryptoTrading.backend.repository.UserRepository;
import com.cryptoTrading.backend.config.JwtService;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public UserDto registerUser(UserDto userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = userMapper.dtoToUser(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        return userMapper.userToDto(userRepository.save(user));
    }
    
    public UserDto authenticateUser(UserDto userDto) {
        User user = userRepository.findByUsername(userDto.getUsername())
            .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));
            
        if (!passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
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
                throw new IllegalArgumentException("Username already exists");
            }
            existingUser.setUsername(userDto.getUsername());
        }
        
        if (userDto.getEmail() != null && !userDto.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(userDto.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
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
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    public BigDecimal getUserBalance(Long id) {
        return getUserByIdInternal(id).getBalance();
    }

    public User getUserByIdInternal(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }
}
