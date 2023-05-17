package com.projectpal.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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

import com.projectpal.entity.Epic;
import com.projectpal.entity.Project;
import com.projectpal.entity.enums.Progress;
import com.projectpal.exception.BadRequestException;
import com.projectpal.exception.ForbiddenException;
import com.projectpal.exception.ResourceNotFoundException;
import com.projectpal.repository.EpicRepository;
import com.projectpal.utils.ProjectUtil;

@RestController
@RequestMapping("/epic")
public class EpicController {

	@Autowired
	public EpicController(EpicRepository epicRepo, RedisCacheManager redis) {
		this.epicRepo = epicRepo;
		this.redis = redis;
	}

	private final EpicRepository epicRepo;

	private final RedisCacheManager redis;

	private List<Epic> getCachedEpicList() {

		Project project = ProjectUtil.getProjectNotNull();

		List<Epic> epics;

		try {
			epics = redis.getCache("epicListCache").get(project.getId(), List.class);

		} catch (Exception ex) {
			epics = null;
		}
		if (epics == null) {

			epics = epicRepo.findAllByProject(project)
					.orElseThrow(() -> new ResourceNotFoundException("no epics found"));

			redis.getCache("epicListCache").put(project.getId(), epics);
		}

		epics.sort((epic1, epic2) -> Integer.compare(epic1.getPriority(), epic2.getPriority()));

		return epics;
	}

	@GetMapping("/list")
	public ResponseEntity<List<Epic>> getEpicList() {

		return ResponseEntity.ok(getCachedEpicList());

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PostMapping("/create")
	@Transactional
	public ResponseEntity<Void> createEpic(@RequestBody Epic epic) {

		if (epic == null || epic.getName() == null || epic.getPriority() == null)
			throw new BadRequestException("request body is null");

		Project project = ProjectUtil.getProjectNotNull();

		epic.setProject(project);

		epicRepo.save(epic);

		// Redis Cache Update:

		List<Epic> epics;

		try {
			epics = redis.getCache("epicListCache").get(project.getId(), List.class);

			if (epics != null) {
				epics.add(epic);
				redis.getCache("epicListCache").put(project.getId(), epics);
			}
		} catch (Exception ex) {
			redis.getCache("epicListCache").evictIfPresent(project.getId());
		}
		// Redis Cache Update End:
		
		UriComponents uriComponents = UriComponentsBuilder.fromPath("/api/epic").build();
		URI location = uriComponents.toUri();

		return ResponseEntity.status(201).location(location).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/update/description/{id}")
	@Transactional
	public ResponseEntity<Void> updateDescription(@RequestBody String description, @PathVariable long id) {

		Epic epic = epicRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("epic does not exist"));

		Project project = ProjectUtil.getProjectNotNull();

		if (epic.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed to update description of epics from other projects");

		epic.setDescription(description);

		epicRepo.save(epic);

		// Redis Cache Update:

		List<Epic> epics;

		try {
			epics = redis.getCache("epicListCache").get(project.getId(), List.class);

			if (epics != null) {
				for (Epic epic1 : epics) {
					if (epic1.getId() == id) {
						epic.setDescription(description);
						break;
					}
				}

				redis.getCache("epicListCache").put(project.getId(), epics);
			}
		} catch (Exception ex) {
			redis.getCache("epicListCache").evictIfPresent(project.getId());
		}
		// Redis Cache Update End:
		
		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/update/priority/{id}")
	@Transactional
	public ResponseEntity<Void> updatePriority(@RequestParam Byte priority, @PathVariable long id) {

		Epic epic = epicRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("epic does not exist"));

		Project project = ProjectUtil.getProjectNotNull();

		if (epic.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed to update priority of epics from other projects");

		if (priority == null)
			throw new BadRequestException("priority is null");

		if (priority < 0 || priority > 255)
			throw new BadRequestException("value is too large or too small");

		epic.setPriority(priority);

		epicRepo.save(epic);

		// Redis Cache Update:

		List<Epic> epics;

		try {
			epics = redis.getCache("epicListCache").get(project.getId(), List.class);

			if (epics != null) {
				for (Epic epic1 : epics) {
					if (epic1.getId() == id) {
						epic.setPriority(priority);
						break;
					}
				}

				redis.getCache("epicListCache").put(project.getId(), epics);
			}
		} catch (Exception ex) {
			redis.getCache("epicListCache").evictIfPresent(project.getId());
		}
		// Redis Cache Update End:
		
		return ResponseEntity.status(204).build();

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/update/progress/{id}")
	@Transactional
	public ResponseEntity<Void> updateProgress(@RequestParam Progress progress, @PathVariable long id) {

		Epic epic = epicRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("epic does not exist"));

		Project project = ProjectUtil.getProjectNotNull();

		if (epic.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed to delete epics from other projects");

		if (progress == null)
			throw new BadRequestException("request is null");

		epic.setProgress(progress);

		epicRepo.save(epic);

		// Redis Cache Update:

		List<Epic> epics;

		try {
			epics = redis.getCache("epicListCache").get(project.getId(), List.class);

			if (epics != null) {
				for (Epic epic1 : epics) {
					if (epic1.getId() == id) {
						epic.setProgress(progress);
						break;
					}
				}

				redis.getCache("epicListCache").put(project.getId(), epics);
			}
		} catch (Exception ex) {
			redis.getCache("epicListCache").evictIfPresent(project.getId());
		}
		// Redis Cache Update End:
		
		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@DeleteMapping("/delete/{id}")
	@Transactional
	public ResponseEntity<Void> deleteEpic(@PathVariable long id) {

		Epic epic = epicRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("epic does not exist"));

		Project project = ProjectUtil.getProjectNotNull();

		if (epic.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed to delete epics from other projects");

		epicRepo.delete(epic);

		// Redis Cache Update:

		List<Epic> epics;

		try {
			epics = redis.getCache("epicListCache").get(project.getId(), List.class);

			if (epics != null) {
				for (Epic epic1 : epics) {
					if (epic1.getId() == id) {
						epics.remove(epic1);
						break;
					}
				}

				redis.getCache("epicListCache").put(project.getId(), epics);
			}
		} catch (Exception ex) {
			redis.getCache("epicListCache").evictIfPresent(project.getId());
		}
		// Redis Cache Update End:
		
		return ResponseEntity.status(204).build();
	}
}
