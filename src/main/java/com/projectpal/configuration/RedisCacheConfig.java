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

import com.projectpal.service.CacheServiceEpicAddOn;
import com.projectpal.service.CacheServiceSprintAddOn;
import com.projectpal.service.CacheServiceUserStoryAddOn;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    @Bean
    RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
    	
        RedisCacheConfiguration cacheConfiguration1 = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(4)) 
                .disableCachingNullValues() 
                .computePrefixWith(cacheName -> "projectpal:" + cacheName);
        
        RedisCacheConfiguration cacheConfiguration2 = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30)) 
                .disableCachingNullValues() 
                .computePrefixWith(cacheName -> "projectpal:" + cacheName);

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put(CacheServiceEpicAddOn.epicListCache, cacheConfiguration1);
        cacheConfigurations.put(CacheServiceSprintAddOn.sprintListCache, cacheConfiguration1);
        cacheConfigurations.put(CacheServiceUserStoryAddOn.epicUserStoryListCache, cacheConfiguration2);
        cacheConfigurations.put(CacheServiceUserStoryAddOn.sprintUserStoryListCache, cacheConfiguration2);

        RedisCacheManager cacheManager = RedisCacheManager.builder(redisConnectionFactory)
        		.withInitialCacheConfigurations(cacheConfigurations)
                .build();

        return cacheManager;
    }
}