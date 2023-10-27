package com.projectpal.service.cache;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisCacheServiceImpl<T> implements CacheService<T> {

	@PostConstruct
	private void clearCacheAndSetTransactionAware() {
		try {
			redis.getCacheNames().forEach(cacheName -> redis.getCache(cacheName).clear());
		} catch (Exception ex) {

		}
	}

	private final RedisCacheManager redis;

	@Override
	public Optional<List<T>> getListFromCache(String cacheName, Long cacheKey) {

		List<T> objects;

		try {
			objects = redis.getCache(cacheName).get(cacheKey, List.class);

		} catch (Exception ex) {
			objects = null;
		}

		return Optional.ofNullable(objects);
	}

	@Override
	public void putListInCache(String cacheName, Long cacheKey, List<T> objects) {
		try {
			redis.getCache(cacheName).putIfAbsent(cacheKey, objects);
		} catch (Exception ex) {
		}
	}

	@Override
	public void addObjectToListInCache(String cacheName, Long cacheKey, T object) {

		List<T> objects;

		try {
			objects = redis.getCache(cacheName).get(cacheKey, List.class);

			if (objects != null && !objects.isEmpty()) {
				objects.add(object);
				redis.getCache(cacheName).putIfAbsent(cacheKey, objects);
			}

		} catch (Exception ex) {
			Cache cache = redis.getCache(cacheName);
			if (cache != null)
				cache.evictIfPresent(cacheKey);

		}
	}

	@Override
	public void evictCache(String cacheName, Long cacheKey) {
		Cache cache = redis.getCache(cacheName);
		if (cache != null)
			cache.evictIfPresent(cacheKey);
	}

}
