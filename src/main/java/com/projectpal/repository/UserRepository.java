package com.projectpal.repository;

import java.util.List;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectpal.entity.Project;
import com.projectpal.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
	Optional<User> findUserByEmail(String email);
	
	Optional<User> findUserByName(String name);

	Optional<List<User>> findAllByProject(Project project);
}
