package com.projectpal.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;

import com.projectpal.exception.ResourceNotFoundException;

import jakarta.annotation.PostConstruct;

@Service
public class CacheService {

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
		redis.setTransactionAware(true);
	}

	private final RedisCacheManager redis;

	// Generic Get Cached Objects Method

	// #Method Parameters:
	// name of cache
	// cache key
	// R : Repository of the cached object T
	// Function to be applied on the Repository to find the list of Objects of type
	// T

	// #Method Explanation:
	// 1) Tries to get cache using cache name (cacheName) and cache key (cacheKey),
	// if the method is successful and value is found then the remaining of the
	// method is NOT executed
	// 2) If an exception is thrown then the list is set to null
	// 3) If list is Null or Empty then the list is queried from the database and
	// put into cache
	// 4) List is returned

	public <T, R extends JpaRepository<T, Long>> List<T> getObjectsFromCacheOrDatabase(String cacheName, Long cacheKey, R repository,
			Function<R, Optional<List<T>>> findAllByParentId) {
		List<T> objects;

		try {
			objects = redis.getCache(cacheName).get(cacheKey, List.class);

		} catch (Exception ex) {
			objects = null;
		}

		if (objects == null || objects.isEmpty()) {

			objects = findAllByParentId.apply(repository).orElse(new ArrayList<T>(0));
					

			try {
				redis.getCache(cacheName).put(cacheKey, objects);
			} catch (Exception ex) {
			}
		}

		return objects;
	}

	// Generic Add Object To Cache Method

	// #Method Parameters:
	// name of cache
	// cache key
	// Object T : to be added object

	// Method Explanation:
	// 1) Tries to get cache using cache name (cacheName) and cache key (cacheKey)
	// 2) If an exception is thrown then the cache is evicted
	// 3) If List is Found and not empty then object T is added to it
	// 4) List is put into cache to overwrite the invalid cache

	public <T> void addObjectToCache(String cacheName, Long cacheKey, T object) {

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

	// Evict List From Cache Method:

	public void evictListFromCache(String cacheName, Long cacheKey) {
		Cache cache = redis.getCache(cacheName);
		if (cache != null)
			cache.evictIfPresent(cacheKey);
	}

}
