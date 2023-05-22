package com.projectpal.service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;

import com.projectpal.entity.Epic;
import com.projectpal.entity.Project;
import com.projectpal.entity.Sprint;
import com.projectpal.entity.UserStory;
import com.projectpal.exception.ResourceNotFoundException;
import com.projectpal.repository.EpicRepository;
import com.projectpal.repository.SprintRepository;
import com.projectpal.repository.UserStoryRepository;

@Service
public class CacheService {

	@Autowired
	public CacheService(EpicRepository epicRepo, SprintRepository sprintRepo, UserStoryRepository userStoryRepo,
			RedisCacheManager redis) {
		this.epicRepo = epicRepo;
		this.sprintRepo = sprintRepo;
		this.userStoryRepo = userStoryRepo;
		this.redis = redis;
	}

	private final RedisCacheManager redis;

	private final EpicRepository epicRepo;

	private final SprintRepository sprintRepo;

	private final UserStoryRepository userStoryRepo;

	public static final String epicListCache = "epicListCache";

	public static final String sprintListCache = "sprintListCache";

	public static final String epicUserStoryListCache = "epicUserStoryListCache";

	public static final String sprintUserStoryListCache = "sprintUserStoryListCache";

	// Generic Get Cached Objects Method

	// #Method Parameters:
	// name of cache
	// cache key
	// R : Repository of the cached object T
	// Function to be applied on the Repository to find the list of Objects of type T
	

	// #Method Explanation:
	// 1) Tries to get cache using cache name (cacheName) and cache key (cacheKey),
	// if the method is successful and value is found then the remaining of the method is NOT executed
	// 2) If an exception is thrown then the list is set to null
	// 3) If list is Null or Empty then the list is queried from the database and put into cache
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
					.orElseThrow(() -> new ResourceNotFoundException("no epics found"));

			redis.getCache(cacheName).put(cacheKey, objects);
		}

		return objects;
	}

	// Generic Update Property Method

	// #Method Parameters:
	// name of cache
	// cache key
	// Object T : to be updated object
	// Function to be applied on object T to update its property, e.g: (user)-> user.setName("..")

	// Method Explanation:
	// 1) Tries to get cache using cache name (cacheName) and cache key (cacheKey)
	// 2) If an exception is thrown then the cache is evicted
	// 3) If List is Found and not empty then it is searched for the ToBeUpdated Object T
	// 4) Object T property is updated using Function
	// 5) List is put into cache to overwrite the invalid cache

	public <P, T> void updateObjectPropertyInCache(String cacheName, Long cacheKey, T object,
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
			redis.getCache(cacheName).evictIfPresent(cacheKey);
		}
	}

	// Epic

	public List<Epic> getCachedEpicList(Project project) {

		return this.getCachedObjects(epicListCache, project.getId(), epicRepo,
				repo -> repo.findAllByProjectId(project.getId()));
	}

	public void updateEpicProperty(Epic epic, Function<Epic, Void> updateEpicProperty) {

		this.updateObjectPropertyInCache(epicListCache, epic.getProject().getId(), epic, Epic::getId,
				updateEpicProperty);
	}

	// Sprint

	public List<Sprint> getCachedSprintList(Project project) {

		return this.getCachedObjects(sprintListCache, project.getId(), sprintRepo,
				repo -> repo.findAllByProjectId(project.getId()));
	}

	public void updateSprintProperty(Sprint sprint, Function<Sprint, Void> updateSprintProperty) {
		
		this.updateObjectPropertyInCache(sprintListCache, sprint.getProject().getId(), sprint, Sprint::getId,
				updateSprintProperty);
	}

	// UserStory

	public List<UserStory> getCachedEpicUserStoryList(Epic epic) {
		
		return this.getCachedObjects(epicUserStoryListCache, epic.getId(), userStoryRepo,
				repo -> repo.findAllByEpicId(epic.getId()));
	}

	public List<UserStory> getCachedSprintUserStoryList(Sprint sprint) {
		
		return this.getCachedObjects(sprintUserStoryListCache, sprint.getId(), userStoryRepo,
				repo -> repo.findAllBySprintId(sprint.getId()));
	}

	public void updateUserStoryProperty(UserStory userStory, Function<UserStory, Void> updateUserStoryProperty) {
		
		this.updateObjectPropertyInCache(epicUserStoryListCache, userStory.getEpic().getId(), userStory,
				UserStory::getId, updateUserStoryProperty);
		
		this.updateObjectPropertyInCache(sprintUserStoryListCache, userStory.getSprint().getId(), userStory,
				UserStory::getId, updateUserStoryProperty);
	}

}
