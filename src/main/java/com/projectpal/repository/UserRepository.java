package com.projectpal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projectpal.entity.User;

public interface UserRepository extends JpaRepository<User,Long> {

}
