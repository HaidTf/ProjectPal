package com.projectpal.service.unit;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;

import com.projectpal.entity.Epic;
import com.projectpal.repository.EpicRepository;
import com.projectpal.service.CacheService;

@ExtendWith(MockitoExtension.class)
public class CacheServiceUTest {

	@InjectMocks
	private CacheService cacheService;

	@Mock
	private RedisCacheManager redis;

	@Mock
	private EpicRepository epicRepo;

	@Mock
	private Cache cache;

	private String cacheName;

	private Long cacheKey;

	private Long projectId;

	private ArrayList<Epic> toBeReturnedList = new ArrayList<>();

	// * getCachedObjects() Method Tests

	@Test
	public void testGetCachedObjects_CacheReturnsCachedObjects_NoDatabaseQuery() {

		toBeReturnedList.add(new Epic());

		// NonNull and NonEmpty list returned from cache

		Mockito.when(redis.getCache(cacheName)).thenReturn(cache);

		Mockito.when(cache.get(cacheKey, List.class)).thenReturn(toBeReturnedList);

		// Test

		List<Epic> epics = cacheService.getObjectsFromCacheOrDatabase(cacheName, cacheKey, epicRepo,
				repo -> repo.findAllByProjectId(projectId));

		assertEquals(epics, toBeReturnedList);

		Mockito.verify(epicRepo, Mockito.never()).findAllByProjectId(projectId);

	}

	@Test
	public void testGetCached_CacheReturnsNullOrEmptyList_ListReturnedFromDatabase() {

		// Null List returned from cache

		Mockito.when(redis.getCache(cacheName)).thenReturn(cache);

		Mockito.when(cache.get(cacheKey, List.class)).thenReturn(null);

		// List returned from database

		Mockito.when(epicRepo.findAllByProjectId(projectId)).thenReturn(Optional.of(toBeReturnedList));

		// Test

		List<Epic> epics = cacheService.getObjectsFromCacheOrDatabase(cacheName, cacheKey, epicRepo,
				repo -> repo.findAllByProjectId(projectId));

		assertEquals(epics, toBeReturnedList);

		Mockito.verify(epicRepo, Mockito.times(1)).findAllByProjectId(projectId);

		Mockito.verify(cache, Mockito.times(1)).put(cacheKey, toBeReturnedList); 

	}

	@Test
	public void testGetCachedObjects_CacheAccessThrowsException_ListReturnedFromDatabase() {

		// Exception Thrown on Cache Access
		
		Mockito.when(redis.getCache(cacheName)).thenThrow(new RuntimeException());

		// List returned from database

		Mockito.when(epicRepo.findAllByProjectId(projectId)).thenReturn(Optional.of(toBeReturnedList));

		// Test

		List<Epic> epics = cacheService.getObjectsFromCacheOrDatabase(cacheName, cacheKey, epicRepo,
				repo -> repo.findAllByProjectId(projectId));

		assertEquals(epics, toBeReturnedList);

		Mockito.verify(epicRepo, Mockito.times(1)).findAllByProjectId(projectId);

	}

	// * addObjectToCache() Method Tests

	@Test
	public void testAddObjectToCache_CacheReturnsNonNullAndNonEmptyList_ObjectAddedToCache() {

		toBeReturnedList.add(new Epic());

		// NonNull and NonEmpty list returned from cache

		Mockito.when(redis.getCache(cacheName)).thenReturn(cache);

		Mockito.when(cache.get(cacheKey, List.class)).thenReturn(toBeReturnedList);

		// Test

		cacheService.addObjectToCache(cacheName, cacheKey, new Epic());

		Mockito.verify(cache, Mockito.times(1)).put(cacheKey, toBeReturnedList);

		assertEquals(2, toBeReturnedList.size());
	}

	@Test
	public void testAddObjectToCache_CacheReturnsNullOrEmptyList_ObjectNotAdded() {

		// NonNull and NonEmpty list returned from cache

		Mockito.when(redis.getCache(cacheName)).thenReturn(cache);

		Mockito.when(cache.get(cacheKey, List.class)).thenReturn(null);

		// Test

		cacheService.addObjectToCache(cacheName, cacheKey, new Epic());

		Mockito.verify(cache, Mockito.never()).put(cacheKey, null);

	}

	@Test
	public void testAddObjectToCache_CacheThrowsException_CacheEvicted() {

		// Exception Thrown on Cache Access
		
		Mockito.when(redis.getCache(cacheName)).thenReturn(cache);
		
		Mockito.when(cache.get(cacheKey, List.class)).thenThrow(new RuntimeException());
		
		//Test
		
		cacheService.addObjectToCache(cacheName, cacheKey, new Epic());
		
		Mockito.verify(cache,Mockito.times(1)).evictIfPresent(cacheKey);
	}

	
}
