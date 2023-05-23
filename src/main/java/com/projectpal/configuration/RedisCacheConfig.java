package com.projectpal.configuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import com.projectpal.service.CacheService;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    @Bean
    RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
    	
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30)) 
                .disableCachingNullValues() 
                .computePrefixWith(cacheName -> "projectpal:" + cacheName);

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put(CacheService.epicListCache, cacheConfiguration);
        cacheConfigurations.put(CacheService.sprintListCache, cacheConfiguration);
        cacheConfigurations.put(CacheService.epicUserStoryListCache, cacheConfiguration);
        cacheConfigurations.put(CacheService.sprintUserStoryListCache, cacheConfiguration);

        RedisCacheManager cacheManager = RedisCacheManager.builder(redisConnectionFactory)
        		.withInitialCacheConfigurations(cacheConfigurations)
                .build();

        return cacheManager;
    }
}