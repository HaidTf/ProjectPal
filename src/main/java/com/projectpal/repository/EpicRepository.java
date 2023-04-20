package com.projectpal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectpal.entity.Epic;

@Repository
public interface EpicRepository extends JpaRepository<Epic, Long> {

}
