package com.projectpal.service.cache;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class CacheService<T> {

	@Autowired
	public CacheService(RedisCacheManager redis) {
		this.redis = redis;
	}

	@PostConstruct
	private void clearCacheAndSetTransactionAware() {
		try {
			redis.getCacheNames().forEach(cacheName -> redis.getCache(cacheName).clear());
		} catch (Exception ex) {

		}
	}

	private final RedisCacheManager redis;

	public Optional<List<T>> getObjectsFromCache(String cacheName, Long cacheKey) {

		List<T> objects;

		try {
			objects = redis.getCache(cacheName).get(cacheKey, List.class);

		} catch (Exception ex) {
			objects = null;
		}

		return Optional.ofNullable(objects);
	}

	public void populateCache(String cacheName, Long cacheKey, List<T> objects) {
		try {
			redis.getCache(cacheName).put(cacheKey, objects);
		} catch (Exception ex) {
		}
	}

	public void addObjectToCache(String cacheName, Long cacheKey, T object) {

		List<T> objects;

		try {
			objects = redis.getCache(cacheName).get(cacheKey, List.class);

			if (objects != null && !objects.isEmpty()) {
				objects.add(object);
				redis.getCache(cacheName).put(cacheKey, objects);
			}

		} catch (Exception ex) {
			Cache cache = redis.getCache(cacheName);
			if (cache != null)
				cache.evictIfPresent(cacheKey);

		}
	}

	public void evictListFromCache(String cacheName, Long cacheKey) {
		Cache cache = redis.getCache(cacheName);
		if (cache != null)
			cache.evictIfPresent(cacheKey);
	}

}
