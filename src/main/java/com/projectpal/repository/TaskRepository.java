package com.projectpal.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectpal.entity.Task;
import com.projectpal.entity.User;
import com.projectpal.entity.UserStory;
import com.projectpal.entity.enums.Progress;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long>{

	List<Task> findAllByAssignedUser(User assignedUser);
	
	List<Task> findAllByUserStoryId(long id);
	
	List<Task> findAllByAssignedUserAndProgressNot(User assignedUser, Progress progress);
	
	int countByUserStoryId(Long userStoryId);

	List<Task> findAllByUserStory(UserStory userStory, Sort sort);

	List<Task> findAllByUserStoryAndProgressIn(UserStory userStory, Set<Progress> progress, Sort sort);

	Page<Task> findAllByAssignedUser(User user, Pageable pageable);

	Page<Task> findAllByAssignedUserAndProgressIn(User user, Set<Progress> progress, Pageable pageable);
	
}
