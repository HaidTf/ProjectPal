package com.projectpal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectpal.entity.Task;
import com.projectpal.entity.User;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long>{

	Optional<List<Task>> findAllByAssignedUser(User assignedUser);
	
	Optional<List<Task>> findAllByUserStoryId(long id);
	
}
