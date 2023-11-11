package com.projectpal.service.cache;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCacheServiceImpl<T> implements CacheService<T> {

	@PostConstruct
	private void clearCache() {
		try {
			redis.getCacheNames().forEach(cacheName -> redis.getCache(cacheName).clear());
		} catch (Exception ex) {
			log.warn("Cache: Cache clearing failed; {}", ex.getMessage());
		}
	}

	private final RedisCacheManager redis;

	@Override
	public Optional<List<T>> getListFromCache(String cacheName, Long cacheKey) {

		List<T> objects;

		try {
			objects = redis.getCache(cacheName).get(cacheKey, List.class);

		} catch (Exception ex) {

			log.warn("Cache: List retrieval failed for {} cache with {} key; {}", cacheName, cacheKey, ex.getMessage());

			return Optional.empty();
		}

		return Optional.ofNullable(objects);
	}

	@Override
	public void putListInCache(String cacheName, Long cacheKey, List<T> objects) {
		try {
			redis.getCache(cacheName).putIfAbsent(cacheKey, objects);
		} catch (Exception ex) {
			log.warn("Cache: List storage in cache of name {} and key {} failed; ", cacheName, cacheKey,
					ex.getMessage());
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

			log.error("Cache: Failed to add object to List in Cache of name {} and key {}; {}", cacheName, cacheKey,
					ex.getMessage());

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
