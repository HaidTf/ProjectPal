package com.projectpal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projectpal.entity.UserStory;

public interface UserStoryRepository extends JpaRepository<UserStory,Long>{

}
