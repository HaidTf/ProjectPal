package com.projectpal.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectpal.entity.Epic;
import com.projectpal.entity.Sprint;
import com.projectpal.entity.UserStory;
import com.projectpal.entity.enums.Progress;
@Repository
public interface UserStoryRepository extends JpaRepository<UserStory,Long>{

	List<UserStory> findAllByEpic(Epic epic);
	
	List<UserStory> findAllBySprint(Sprint sprint);

	List<UserStory> findAllByEpicId(Long id);
	
	List<UserStory> findAllBySprintId(Long id);
	
	int countByEpicId(Long projectId);
	
	int countBySprintId(Long sprintId);

	List<UserStory> findAllByEpicAndProgressIn(Epic epic, Set<Progress> progress);

	List<UserStory> findAllByEpic(Epic epic, Sort sort);

	List<UserStory> findAllByEpicAndProgressIn(Epic epic, Set<Progress> progress, Sort sort);

	List<UserStory> findAllBySprint(Sprint sprint, Sort sort);

	List<UserStory> findAllBySprintAndProgressIn(Sprint sprint, Set<Progress> progress, Sort sort);

	List<UserStory> findAllBySprintAndProgressIn(Sprint sprint, Set<Progress> progress);
	
}
