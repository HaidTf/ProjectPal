package com.projectpal.service.cache;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;

import com.projectpal.entity.Epic;

@Service
@Qualifier("epicCacheService")
public class EpicCacheService extends CacheService<Epic> {

	public EpicCacheService(RedisCacheManager redis) {
		super(redis);
	}

}
