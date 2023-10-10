package com.projectpal.service.cache;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;

import com.projectpal.entity.Sprint;

@Service
@Qualifier("sprintCacheService")
public class SprintCacheService extends CacheService<Sprint> {

	public SprintCacheService(RedisCacheManager redis) {
		super(redis);
	}

}
