package com.projectpal.service;

import java.util.List;
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
	private void clearCache() {
		try {
			redis.getCacheNames().forEach(cacheName -> redis.getCache(cacheName).clear());
		} catch (Exception ex) {

		}
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

	public <T, R extends JpaRepository<T, Long>> List<T> getCachedObjects(String cacheName, Long cacheKey, R repository,
			Function<R, Optional<List<T>>> findAllByParentId) {
		List<T> objects;

		try {
			objects = redis.getCache(cacheName).get(cacheKey, List.class);

		} catch (Exception ex) {
			objects = null;
		}
		if (objects == null || objects.isEmpty()) {

			objects = findAllByParentId.apply(repository)
					.orElseThrow(() -> new ResourceNotFoundException("no entities found"));

			redis.getCache(cacheName).put(cacheKey, objects);
		}

		return objects;
	}

	// Generic Update Property Method

	// #Method Parameters:
	// name of cache
	// cache key
	// Object T : to be updated object
	// Function to be applied on object T to get its id
	// Function to be applied on object T to update its property, e.g: (user)->
	// user.setName("..")

	// Method Explanation:
	// 1) Tries to get cache using cache name (cacheName) and cache key (cacheKey)
	// 2) If an exception is thrown then the cache is evicted
	// 3) If List is Found and not empty then it is searched for the ToBeUpdated
	// Object T
	// 4) Object T property is updated using Function
	// 5) List is put into cache to overwrite the invalid cache

	public <T> void updateObjectPropertyInCache(String cacheName, Long cacheKey, T object,
			Function<T, Long> getObjectId, Function<T, Void> updateTProperty) {

		List<T> objects;

		try {
			objects = redis.getCache(cacheName).get(cacheKey, List.class);

			if (objects != null && !objects.isEmpty()) {
				for (T object2 : objects) {
					if (getObjectId.apply(object2) == getObjectId.apply(object)) {
						updateTProperty.apply(object2);
						break;
					}
				}

				redis.getCache(cacheName).put(cacheKey, objects);
			}
		} catch (Exception ex) {
			Cache cache = redis.getCache(cacheName);
			if (cache != null)
				cache.evictIfPresent(cacheKey);
		}
	}

	// Generic Delete Object From Cache Method

	// #Method Parameters:
	// name of cache
	// cache key
	// Object T : to be Deleted object
	// Function to be applied on object T to get its id

	// Method Explanation:
	// 1) Tries to get cache using cache name (cacheName) and cache key (cacheKey)
	// 2) If an exception is thrown then the cache is evicted
	// 3) If List is Found and not empty then it is searched for the ToBeDeleted
	// Object T
	// 4) Object T is removed from list
	// 5) List is put into cache to overwrite the invalid cache

	public <T> void deleteObjectFromCache(String cacheName, Long cacheKey, T object, Function<T, Long> getObjectId) {

		List<T> objects;

		try {
			objects = redis.getCache(cacheName).get(cacheKey, List.class);

			if (objects != null && !objects.isEmpty()) {
				for (T object2 : objects) {
					if (getObjectId.apply(object2) == getObjectId.apply(object)) {
						objects.remove(object2);
						break;
					}
				}

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
