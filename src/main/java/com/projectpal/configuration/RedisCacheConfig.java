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

import com.projectpal.service.cache.CacheConstants;

@Configuration
@EnableCaching
public class RedisCacheConfig {

	@Bean
	RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {

		RedisCacheConfiguration cacheConfiguration1 = RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(Duration.ofHours(2)).disableCachingNullValues()
				.computePrefixWith(cacheName -> "projectpal:" + cacheName);

		RedisCacheConfiguration cacheConfiguration2 = RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(Duration.ofMinutes(30)).disableCachingNullValues()
				.computePrefixWith(cacheName -> "projectpal:" + cacheName);

		Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
		cacheConfigurations.put(CacheConstants.EPIC_CACHE, cacheConfiguration1);
		cacheConfigurations.put(CacheConstants.SPRINT_CACHE, cacheConfiguration1);
		cacheConfigurations.put(CacheConstants.EPIC_USERSTORY_CACHE, cacheConfiguration2);
		cacheConfigurations.put(CacheConstants.SPRINT_USERSTORY_CACHE, cacheConfiguration2);

		RedisCacheManager cacheManager = RedisCacheManager.builder(redisConnectionFactory)
				.withInitialCacheConfigurations(cacheConfigurations).build();

		cacheManager.setTransactionAware(true);

		return cacheManager;
	}
}