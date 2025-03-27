package com.cryptoTrading.backend.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    @NotNull
    @Size(min = 3, message = "Username must be at least 3 characters")
    private String username;

    @NotNull
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Email
    private String email;

    private String token;

    @Builder.Default
    private BigDecimal balance = BigDecimal.valueOf(10000);
}
