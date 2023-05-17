package com.projectpal.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.format.annotation.DateTimeFormat;
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

import com.projectpal.entity.Sprint;
import com.projectpal.entity.Project;
import com.projectpal.entity.enums.Progress;
import com.projectpal.exception.BadRequestException;
import com.projectpal.exception.ForbiddenException;
import com.projectpal.exception.ResourceNotFoundException;
import com.projectpal.repository.SprintRepository;
import com.projectpal.utils.ProjectUtil;

@RestController
@RequestMapping("/sprint")
public class SprintController {

	@Autowired
	public SprintController(SprintRepository sprintRepo, RedisCacheManager redis) {
		this.redis = redis;
		this.sprintRepo = sprintRepo;
	}

	private final SprintRepository sprintRepo;

	private final RedisCacheManager redis;

	private List<Sprint> getCachedSprintList() {
		
		Project project = ProjectUtil.getProjectNotNull();

		List<Sprint> sprints;

		try {
			sprints = redis.getCache("sprintListCache").get(project.getId(), List.class);

		} catch (Exception ex) {
			sprints = null;
		}
		if (sprints == null) {

			sprints = sprintRepo.findAllByProject(project)
					.orElseThrow(() -> new ResourceNotFoundException("no sprints found"));

			redis.getCache("sprintListCache").put(project.getId(), sprints);
		}

		sprints.sort((sprint1, sprint2) -> sprint1.getStartDate().compareTo(sprint2.getStartDate()));

		return sprints;
	}

	@GetMapping("/list")
	public ResponseEntity<List<Sprint>> getsprintList() {

		return ResponseEntity.ok(getCachedSprintList());

	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PostMapping("/create")
	@Transactional
	public ResponseEntity<Void> createsprint(@RequestBody Sprint sprint) {

		if (sprint == null || sprint.getName() == null || sprint.getStartDate() == null || sprint.getEndDate() == null)
			throw new BadRequestException("sprint name or startdate or enddate is null");

		if (sprint.getStartDate().isAfter(sprint.getEndDate()))
			throw new BadRequestException("End date is before Start date");

		Project project = ProjectUtil.getProjectNotNull();

		sprint.setProject(project);

		sprintRepo.save(sprint);

		// Redis Cache Update:

		List<Sprint> sprints;

		try {
			sprints = redis.getCache("sprintListCache").get(project.getId(), List.class);

			if (sprints != null) {
				sprints.add(sprint);
				redis.getCache("sprintListCache").put(project.getId(), sprints);
			}
		} catch (Exception ex) {
			redis.getCache("sprintListCache").evictIfPresent(project.getId());
		}
		// Redis Cache Update End:

		UriComponents uriComponents = UriComponentsBuilder.fromPath("/api/sprint").build();
		URI location = uriComponents.toUri();

		return ResponseEntity.status(201).location(location).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/update/startdate/{id}")
	@Transactional
	public ResponseEntity<Void> updateStartDate(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, @PathVariable long id) {

		Sprint sprint = sprintRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("sprint does not exist"));

		Project project = ProjectUtil.getProjectNotNull();

		if (sprint.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed to update description of sprints from other projects");

		if (startDate == null)
			throw new BadRequestException("request holding startDate is null");

		if (startDate.isAfter(sprint.getEndDate()))
			throw new BadRequestException("End date is before Start date");

		sprint.setStartDate(startDate);

		sprintRepo.save(sprint);

		// Redis Cache Update:

		List<Sprint> sprints;

		try {
			sprints = redis.getCache("sprintListCache").get(project.getId(), List.class);

			if (sprints != null) {
				for (Sprint sprint1 : sprints) {
					if (sprint1.getId() == id) {
						sprint.setStartDate(startDate);
						break;
					}
				}

				redis.getCache("sprintListCache").put(project.getId(), sprints);
			}
		} catch (Exception ex) {
			redis.getCache("sprintListCache").evictIfPresent(project.getId());
		}
		// Redis Cache Update End:

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/update/enddate/{id}")
	@Transactional
	public ResponseEntity<Void> updateEndDate(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate, @PathVariable long id) {

		Sprint sprint = sprintRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("sprint does not exist"));

		Project project = ProjectUtil.getProjectNotNull();

		if (sprint.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed to update description of sprints from other projects");

		if (endDate == null)
			throw new BadRequestException("request holding endDate is null");

		if (endDate.isBefore(sprint.getStartDate()))
			throw new BadRequestException("End date is before Start date");

		sprint.setEndDate(endDate);

		sprintRepo.save(sprint);

		// Redis Cache Update:

		List<Sprint> sprints;

		try {
			sprints = redis.getCache("sprintListCache").get(project.getId(), List.class);

			if (sprints != null) {
				for (Sprint sprint1 : sprints) {
					if (sprint1.getId() == id) {
						sprint.setEndDate(endDate);
						break;
					}
				}

				redis.getCache("sprintListCache").put(project.getId(), sprints);
			}
		} catch (Exception ex) {
			redis.getCache("sprintListCache").evictIfPresent(project.getId());
		}
		// Redis Cache Update End:

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/update/description/{id}")
	@Transactional
	public ResponseEntity<Void> updateDescription(@RequestBody String description, @PathVariable long id) {

		Sprint sprint = sprintRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("sprint does not exist"));

		Project project = ProjectUtil.getProjectNotNull();

		if (sprint.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed to update description of sprints from other projects");

		sprint.setDescription(description);

		sprintRepo.save(sprint);

		// Redis Cache Update:

		List<Sprint> sprints;

		try {
			sprints = redis.getCache("sprintListCache").get(project.getId(), List.class);

			if (sprints != null) {
				for (Sprint sprint1 : sprints) {
					if (sprint1.getId() == id) {
						sprint.setDescription(description);
						break;
					}
				}

				redis.getCache("sprintListCache").put(project.getId(), sprints);
			}
		} catch (Exception ex) {
			redis.getCache("sprintListCache").evictIfPresent(project.getId());
		}
		// Redis Cache Update End:

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@PatchMapping("/update/progress/{id}")
	@Transactional
	public ResponseEntity<Void> updateProgress(@RequestParam Progress progress, @PathVariable long id) {

		Sprint sprint = sprintRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("sprint does not exist"));

		Project project = ProjectUtil.getProjectNotNull();

		if (sprint.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed to delete sprints from other projects");

		if (progress == null)
			throw new BadRequestException("request holding progress is null");

		sprint.setProgress(progress);

		sprintRepo.save(sprint);

		// Redis Cache Update:

		List<Sprint> sprints;

		try {
			sprints = redis.getCache("sprintListCache").get(project.getId(), List.class);

			if (sprints != null) {
				for (Sprint sprint1 : sprints) {
					if (sprint1.getId() == id) {
						sprint.setProgress(progress);
						break;
					}
				}

				redis.getCache("sprintListCache").put(project.getId(), sprints);
			}
		} catch (Exception ex) {
			redis.getCache("sprintListCache").evictIfPresent(project.getId());
		}
		// Redis Cache Update End:

		return ResponseEntity.status(204).build();
	}

	@PreAuthorize("hasAnyRole('USER_PROJECT_OWNER','USER_PROJECT_OPERATOR')")
	@DeleteMapping("/delete/{id}")
	@Transactional
	public ResponseEntity<Void> deletesprint(@PathVariable long id) {

		Sprint sprint = sprintRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("sprint does not exist"));

		Project project = ProjectUtil.getProjectNotNull();

		if (sprint.getProject().getId() != project.getId())
			throw new ForbiddenException("you are not allowed to delete sprints from other projects");

		sprintRepo.delete(sprint);

		List<Sprint> sprints;

		try {
			sprints = redis.getCache("sprintListCache").get(project.getId(), List.class);

			if (sprints != null) {
				for (Sprint sprint1 : sprints) {
					if (sprint1.getId() == id) {
						sprints.remove(sprint1);
						break;
					}
				}

				redis.getCache("sprintListCache").put(project.getId(), sprints);
			}
		} catch (Exception ex) {
			redis.getCache("sprintListCache").evictIfPresent(project.getId());
		}
		// Redis Cache Update End:

		return ResponseEntity.status(204).build();
	}
}
