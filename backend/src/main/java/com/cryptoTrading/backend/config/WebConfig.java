package com.cryptoTrading.backend.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


import com.cryptoTrading.backend.resolver.UserIdResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final UserIdResolver userIdResolver;

    public WebConfig(UserIdResolver userIdResolver) {
        this.userIdResolver = userIdResolver;
    }

   @Override
   public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
       resolvers.add(userIdResolver);
   }
}
