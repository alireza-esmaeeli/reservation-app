package com.azki.reservation.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.integration.support.locks.LockRegistry;

import static org.springframework.integration.redis.util.RedisLockRegistry.RedisLockType.*;

@Configuration
public class DistributedLockRegistryConfig {

    @Value("${spring.application.name:reservation-api}")
    private String applicationName;

    @Bean
    @Primary
    LockRegistry redisLockRegistry(RedisConnectionFactory connectionFactory) {
        var lockRegistry = new RedisLockRegistry(connectionFactory, applicationName);
        lockRegistry.setRedisLockType(PUB_SUB_LOCK);
        return lockRegistry;
    }
}
