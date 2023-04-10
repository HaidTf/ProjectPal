package com.projectpal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projectpal.entity.Epic;

public interface EpicRepository extends JpaRepository<Epic,Long>{

}
