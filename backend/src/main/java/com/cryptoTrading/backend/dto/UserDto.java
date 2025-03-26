package com.cryptoTrading.backend.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
    @Min(3)
    private String username;

    @NotNull
    @Min(6)
    private String password;

    @Email
    private String email;

    private String token;

    private BigDecimal balance;
}
