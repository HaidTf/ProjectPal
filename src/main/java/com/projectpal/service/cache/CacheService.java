package com.projectpal.service.cache;

import java.util.List;
import java.util.Optional;

public interface CacheService<T> {

	public Optional<List<T>> getListFromCache(String cacheName, Long cacheKey);

	public void putListInCache(String cacheName, Long cacheKey, List<T> objects);

	public void addObjectToListInCache(String cacheName, Long cacheKey, T object);

	public void evictCache(String cacheName, Long cacheKey);

}
