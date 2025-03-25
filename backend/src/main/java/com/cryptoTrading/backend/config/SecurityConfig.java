package com.cryptoTrading.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorizeHttpRequests ->
                authorizeHttpRequests
                    .requestMatchers("/api/user/register").permitAll()
                    .requestMatchers("/api/user/login").permitAll()
                    .requestMatchers("/api/crypto/**").permitAll()
                    .requestMatchers("/ws").permitAll()
                    .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf.disable())
            .formLogin(login -> login.disable())
            .httpBasic(httpBasic -> httpBasic.disable());
        return http.build();
    }
}
