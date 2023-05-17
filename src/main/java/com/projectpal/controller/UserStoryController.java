package com.projectpal.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.projectpal.entity.UserStory;
import com.projectpal.dto.request.UserStoryCreationRequest;
import com.projectpal.entity.Epic;
import com.projectpal.entity.Project;
import com.projectpal.entity.Sprint;
import com.projectpal.entity.enums.Progress;
import com.projectpal.exception.BadRequestException;
import com.projectpal.exception.ForbiddenException;
import com.projectpal.exception.ResourceNotFoundException;
import com.projectpal.repository.EpicRepository;
import com.projectpal.repository.SprintRepository;
import com.projectpal.repository.UserStoryRepository;
import com.projectpal.utils.ProjectUtil;

@RestController
@RequestMapping("/userstory")
public class UserStoryController {

	@Autowired
	public UserStoryController(UserStoryRepository userStoryRepo, SprintRepository sprintRepo, EpicRepository epicRepo,
			RedisCacheManager redis) {
		this.userStoryRepo = userStoryRepo;
		this.epicRepo = epicRepo;
		this.sprintRepo = sprintRepo;
		this.redis = redis;
	}

	private final EpicRepository epicRepo;

	private final UserStoryRepository userStoryRepo;

	private final SprintRepository sprintRepo;

	private final RedisCacheManager redis;

	private List<UserStory> getCachedEpicUserStoryList(Epic epic) {

		List<UserStory> userStories;

		try {
			userStories = redis.getCache("epicUserStoryListCache").get(epic.getId(), List.class);

		} catch (Exception ex) {
			userStories = null;
		}
		if (userStories == null) {

			userStories = userStoryRepo.findAllByEpic(epic)
					.orElseThrow(() -> new ResourceNotFoundException("no userStories found"));

			redis.getCache("epicUserStoryListCache").put(epic.getId(), userStories);
		}

		userStories
				.sort((userStory1, userStory2) -> Integer.compare(userStory1.getPriority(), userStory2.getPriority()));

		return userStories;
	}

	private List<UserStory> getCachedSprintUserStoryList(Sprint sprint) {

		List<UserStory> userStories;

		try {
			userStories = redis.getCache("sprintUserStoryListCache").get(sprint.getId(), List.class);

		} catch (Exception ex) {
			userStories = null;
		}
		if (userStories == null) {

			userStories = userStoryRepo.findAllBySprint(sprint)
					.orElseThrow(() -> new ResourceNotFoundException("no userStories found"));

			redis.getCache("sprintUserStoryListCache").put(sprint.getId(), userStories);
		}

		userStories
				.sort((userStory1, userStory2) -> Integer.compare(userStory1.getPriority(), userStory2.getPriority()));

		return userStories;
	}

	@GetMapping("/list/epic/{epicId}")
	public ResponseEntity<List<UserStory>> getEpicUserStoryList(@PathVariable long epicId) {

		Epic epic = epicRepo.findById(epicId).orElseThrow(() -> new ResourceNotFoundException("epic does not exist"));

		if (epic.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed access to other projects");

		return ResponseEntity.ok(getCachedEpicUserStoryList(epic));
	}

	@GetMapping("/list/sprint/{sprintId}")
	public ResponseEntity<List<UserStory>> getSprintUserStoryList(@PathVariable long sprintId) {

		Sprint sprint = sprintRepo.findById(sprintId)
				.orElseThrow(() -> new ResourceNotFoundException("sprint does not exist"));

		if (sprint.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed access to other projects");

		return ResponseEntity.ok(getCachedSprintUserStoryList(sprint));
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PostMapping("/create")
	@Transactional
	public ResponseEntity<Void> createUserStory(@RequestBody UserStoryCreationRequest request) {

		long epicId = request.getEpicId();

		UserStory userStory = request.getUserStory();

		Epic epic = epicRepo.findById(epicId).orElseThrow(() -> new ResourceNotFoundException("epic does not exist"));

		if (epic.getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed access to other projects");

		if (userStory.getName() == null || userStory.getPriority() == null)
			throw new BadRequestException("userStory name or priority is null");

		userStory.setEpic(epic);

		userStoryRepo.save(userStory);

		// Redis Cache Update:

		List<UserStory> userStories;

		try {
			userStories = redis.getCache("epicUserStoryListCache").get(epic.getId(), List.class);

			if (userStories != null) {
				userStories.add(userStory);
				redis.getCache("epicUserStoryListCache").put(epic.getId(), userStories);
			}
		} catch (Exception ex) {
			redis.getCache("epicUserStiryListCache").evictIfPresent(epic.getId());
		}
		// Redis Cache Update End:

		UriComponents uriComponents = UriComponentsBuilder.fromPath("/api/userstory").build();
		URI location = uriComponents.toUri();

		return ResponseEntity.status(201).location(location).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/update/addtosprint/{sprintId}/{userStoryId}")
	@Transactional
	public ResponseEntity<Void> addUserStoryToSprint(@PathVariable long sprintId, @PathVariable long userStoryId) {

		Project project = ProjectUtil.getProjectNotNull();

		Sprint sprint = sprintRepo.findById(sprintId)
				.orElseThrow(() -> new ResourceNotFoundException("sprint does not exist"));

		if (sprint.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed access to other projects");

		UserStory userStory = userStoryRepo.findById(userStoryId)
				.orElseThrow(() -> new ResourceNotFoundException("userStory does not exist"));

		if (userStory.getEpic().getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed access to other projects");

		userStory.setSprint(sprint);

		userStoryRepo.save(userStory);

		// Redis Cache Update:

		List<UserStory> userStories;

		try {
			userStories = redis.getCache("sprintUserStoryListCache").get(sprint.getId(), List.class);

			if (userStories != null) {
				userStories.add(userStory);
				redis.getCache("sprintUserStoryListCache").put(sprint.getId(), userStories);
			}
		} catch (Exception ex) {
			redis.getCache("sprintUserStoryListCache").evictIfPresent(sprint.getId());
		}
		// Redis Cache Update End:

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/update/description/{id}")
	@Transactional
	public ResponseEntity<Void> updateDescription(@RequestParam String description, @PathVariable long id) {

		UserStory userStory = userStoryRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("userStory does not exist"));

		if (userStory.getEpic().getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed access to other projects");

		userStory.setDescription(description);

		userStoryRepo.save(userStory);

		// Redis Cache Update:

		List<UserStory> userStories;

		try {
			userStories = redis.getCache("epicUserStoryListCache").get(userStory.getEpic().getId(), List.class);

			if (userStories != null) {
				for (UserStory userStory1 : userStories) {
					if (userStory1.getId() == id) {
						userStory1.setDescription(description);
						break;
					}
				}

				redis.getCache("epicUserStoryListCache").put(userStory.getEpic().getId(), userStories);
			}
		} catch (Exception ex) {
			redis.getCache("epicUserStoryListCache").evictIfPresent(userStory.getEpic().getId());
		}
		// Redis Cache Update End:

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/update/priority/{id}")
	@Transactional
	public ResponseEntity<Void> updatePriority(@RequestParam Byte priority, @PathVariable long id) {

		UserStory userStory = userStoryRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("userStory does not exist"));

		if (userStory.getEpic().getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed access to other projects");

		if (priority == null)
			throw new BadRequestException("priority is null");

		if (priority < 0 || priority > 255)
			throw new BadRequestException("value is too large or too small");

		userStory.setPriority(priority);

		userStoryRepo.save(userStory);

		// Redis Cache Update:

		List<UserStory> userStories;

		try {
			userStories = redis.getCache("epicUserStoryListCache").get(userStory.getEpic().getId(), List.class);

			if (userStories != null) {
				for (UserStory userStory1 : userStories) {
					if (userStory1.getId() == id) {
						userStory1.setPriority(priority);
						break;
					}
				}

				redis.getCache("epicUserStoryListCache").put(userStory.getEpic().getId(), userStories);
			}
		} catch (Exception ex) {
			redis.getCache("epicUserStoryListCache").evictIfPresent(userStory.getEpic().getId());
		}
		// Redis Cache Update End:

		return ResponseEntity.status(204).build();

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/update/progress/{id}")
	@Transactional
	public ResponseEntity<Void> updateProgress(@RequestParam Progress progress, @PathVariable long id) {

		UserStory userStory = userStoryRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("userStory does not exist"));

		if (userStory.getEpic().getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed access to other projects");

		if (progress == null)
			throw new BadRequestException("request holding progress is null");

		userStory.setProgress(progress);

		userStoryRepo.save(userStory);

		// Redis Cache Update:

		List<UserStory> userStories;

		try {
			userStories = redis.getCache("epicUserStoryListCache").get(userStory.getEpic().getId(), List.class);

			if (userStories != null) {
				for (UserStory userStory1 : userStories) {
					if (userStory1.getId() == id) {
						userStory1.setProgress(progress);
						break;
					}
				}

				redis.getCache("epicUserStoryListCache").put(userStory.getEpic().getId(), userStories);
			}
		} catch (Exception ex) {
			redis.getCache("epicUserStoryListCache").evictIfPresent(userStory.getEpic().getId());
		}
		// Redis Cache Update End:

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@DeleteMapping("/delete/{id}")
	@Transactional
	public ResponseEntity<Void> deleteUserStory(@PathVariable long id) {

		UserStory userStory = userStoryRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("userStory does not exist"));

		if (userStory.getEpic().getProject().getId() != ProjectUtil.getProjectNotNull().getId())
			throw new ForbiddenException("you are not allowed access to other projects");

		userStoryRepo.delete(userStory);

		// Redis Cache Update:

		List<UserStory> userStories;

		try {
			userStories = redis.getCache("epicUserStoryListCache").get(userStory.getEpic().getId(), List.class);

			if (userStories != null) {
				for (UserStory userStory1 : userStories) {
					if (userStory1.getId() == id) {
						userStories.remove(userStory1);
						break;
					}
				}

				redis.getCache("epicUserStoryListCache").put(userStory.getEpic().getId(), userStories);
			}
		} catch (Exception ex) {
			redis.getCache("epicUserStoryListCache").evictIfPresent(userStory.getEpic().getId());
		}
		// Redis Cache Update End:

		return ResponseEntity.status(204).build();
	}
}
