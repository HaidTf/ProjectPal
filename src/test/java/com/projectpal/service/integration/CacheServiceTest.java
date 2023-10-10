package com.projectpal.service.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.projectpal.entity.Epic;
import com.projectpal.entity.Project;
import com.projectpal.repository.EpicRepository;
import com.projectpal.repository.ProjectRepository;
import com.projectpal.service.cache.CacheService;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("development")
@Transactional
public class CacheServiceTest {
	
	@Autowired
	public CacheServiceTest(CacheService cacheService, ProjectRepository projectRepo) {
		this.cacheService = cacheService;
		this.projectRepo = projectRepo;
	}

	@Autowired
	private final CacheService cacheService;

	@SpyBean
	private RedisCacheManager redis;

	@SpyBean
	private EpicRepository epicRepo;

	@Autowired
	private final ProjectRepository projectRepo;

	@Test
	public void testGetCachedObjects() {

		Project project = new Project("project", "description");
		projectRepo.save(project);

		Epic epic1 = new Epic("epic1", "description1", 5);
		epic1.setProject(project);

		Epic epic2 = new Epic("epic2", "description2", 6);
		epic2.setProject(project);

		epicRepo.save(epic1);
		epicRepo.save(epic2);

		// 1st method call

		List<Epic> epics = cacheService.getObjectsFromCacheOrDatabase("epicListCache", project.getId(), epicRepo,
				repo -> repo.findAllByProjectId(project.getId()));

		assertEquals(2, epics.size());

		Mockito.verify(epicRepo, Mockito.times(1)).findAllByProjectId(project.getId()); // Database Accessed

		// 2nd method call

		epics = cacheService.getObjectsFromCacheOrDatabase("epicListCache", project.getId(), epicRepo,
				repo -> repo.findAllByProjectId(project.getId()));

		assertEquals(2, epics.size());

		Mockito.verify(epicRepo, Mockito.times(1)).findAllByProjectId(project.getId());// Database Not Accessed
	}

	@Test
	public void testAddObjectToCache() {

		Project project = new Project("project", "description");
		projectRepo.save(project);

		Epic epic1 = new Epic("epic1", "description1", 5);
		epic1.setProject(project);

		Epic epic2 = new Epic("epic2", "description2", 6);
		epic2.setProject(project);

		epicRepo.save(epic1);
		epicRepo.save(epic2);

		// Cache is not populated yet

		cacheService.addObjectToCache("epicListCache", project.getId(), epic1);

		Mockito.verify(redis, Mockito.times(1)).getCache("epicListCache");// object not added verification

		// Cache population:

		cacheService.getObjectsFromCacheOrDatabase("epicListCache", project.getId(), epicRepo,
				repo -> repo.findAllByProjectId(project.getId()));

		// method call after cache population

		cacheService.addObjectToCache("epicListCache", project.getId(), epic1);// object must be added

		Mockito.verify(redis, Mockito.times(5)).getCache("epicListCache");// object addition verification

		// 5 invocations explanation:
		// 1st time : 1st invocation of addObjectToCache
		// 2nd and 3rd : getCachedObjects method invocation
		// 4th and 5th : addObjectToCache method calls getCache 2 times if object is
		// added

	}

}
