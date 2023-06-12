package com.projectpal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectpal.entity.Epic;
import com.projectpal.entity.Sprint;
import com.projectpal.entity.UserStory;
@Repository
public interface UserStoryRepository extends JpaRepository<UserStory,Long>{

	Optional<List<UserStory>> findAllByEpic(Epic epic);
	
	Optional<List<UserStory>> findAllBySprint(Sprint sprint);

	Optional<List<UserStory>> findAllByEpicId(Long id);
	
	Optional<List<UserStory>> findAllBySprintId(Long id);
	
	int countByEpicId(Long projectId);
	
	int countBySprintId(Long sprintId);
	
}
