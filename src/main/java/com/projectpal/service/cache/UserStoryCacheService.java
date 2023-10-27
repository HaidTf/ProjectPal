package com.projectpal.service.cache;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;

import com.projectpal.entity.UserStory;

@Service
@Qualifier("userStoryCacheService")
public class UserStoryCacheService extends RedisCacheServiceImpl<UserStory> {

	public UserStoryCacheService(RedisCacheManager redis) {
		super(redis);
	}

	public void evictCachesWhereUserStoryIsPresent(UserStory userStory) {
		
		this.evictCache(CacheConstants.EPIC_USERSTORY_CACHE, userStory.getEpic().getId());

		if (userStory.getSprint() != null)
			this.evictCache(CacheConstants.SPRINT_USERSTORY_CACHE, userStory.getSprint().getId());
		
	}

}
