package com.recipe.app.src.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    /**
     * Access Token 블랙리스트 전용 Caffeine Cache
     * JWT exp와 동일하게 24시간 유지
     */
    @Bean(name = "accessTokenBlacklistCache")
    public com.github.benmanes.caffeine.cache.Cache<String, String> accessTokenBlacklistCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)
                .maximumSize(10000)
                .build();
    }
}