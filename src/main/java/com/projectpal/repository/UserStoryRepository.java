package com.projectpal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectpal.entity.UserStory;
@Repository
public interface UserStoryRepository extends JpaRepository<UserStory,Long>{

}
