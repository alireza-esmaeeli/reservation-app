package com.azki.reservation.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.support.locks.DefaultLockRegistry;
import org.springframework.integration.support.locks.LockRegistry;

@Configuration
public class InMemoryLockRegistryConfig {

    @Bean
    LockRegistry lockRegistry() {
        return new DefaultLockRegistry();
    }
}
