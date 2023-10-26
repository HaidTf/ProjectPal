package com.projectpal.service.cache;

import java.util.List;
import java.util.Optional;

public interface CacheService<T> {

	public Optional<List<T>> getObjectsFromCache(String cacheName, Long cacheKey);

	public void populateCache(String cacheName, Long cacheKey, List<T> objects);

	public void addObjectToCache(String cacheName, Long cacheKey, T object);

	public void evictListFromCache(String cacheName, Long cacheKey);

}
