package com.flab.oasis.config;

import com.flab.oasis.service.OasisKeyGenerator;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class EhCacheConfig {
    @Bean("oasisKeyGenerator")
    public KeyGenerator keyGenerator() {
        return new OasisKeyGenerator();
    }
}
