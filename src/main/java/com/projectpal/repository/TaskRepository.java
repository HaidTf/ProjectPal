package com.projectpal.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.projectpal.dto.response.entity.TaskResponseDto;
import com.projectpal.entity.Project;
import com.projectpal.entity.Task;
import com.projectpal.entity.User;
import com.projectpal.entity.UserStory;
import com.projectpal.entity.enums.Progress;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

	Optional<Task> findByIdAndProject(long taskId, Project project);

	@Query("SELECT new com.projectpal.dto.response.entity.TaskResponseDto(t.id,t.name,t.description,t.priority,t.progress,t.report,t.creationDate,u.id,u.name) FROM Task t JOIN t.assignedUser u WHERE t.id =:id AND t.project = :project")
	Optional<TaskResponseDto> findTaskDtoByIdAndProject(@Param("id") long id, @Param("project") Project project);

	List<Task> findAllByAssignedUser(User assignedUser);

	List<Task> findAllByUserStoryId(long id);

	List<Task> findAllByAssignedUserAndProgressNot(User assignedUser, Progress progress);

	int countByUserStoryId(Long userStoryId);

	List<Task> findAllByUserStory(UserStory userStory, Sort sort);

	List<Task> findAllByUserStoryAndProgressIn(UserStory userStory, Set<Progress> progress, Sort sort);

	Page<Task> findAllByAssignedUser(User user, Pageable pageable);

	Page<Task> findAllByAssignedUserAndProgressIn(User user, Set<Progress> progress, Pageable pageable);

	@Query("SELECT new com.projectpal.dto.response.entity.TaskResponseDto(t.id,t.name,t.description,t.priority,t.progress,t.report,t.creationDate,u.id,u.name) FROM Task t JOIN t.assignedUser u WHERE t.userStory = :userStory")
	List<TaskResponseDto> findTaskDtoListByUserStory(@Param("userStory") UserStory userStory, Sort sort);

	@Query("SELECT new com.projectpal.dto.response.entity.TaskResponseDto(t.id,t.name,t.description,t.priority,t.progress,t.report,t.creationDate,u.id,u.name) FROM Task t JOIN t.assignedUser u WHERE t.userStory = :userStory and t.progress IN :progressSet")
	List<TaskResponseDto> findTaskDtoListByUserStoryAndProgressIn(@Param("userStory") UserStory userStory,
			@Param("progressSet") Set<Progress> progress, Sort sort);

}
