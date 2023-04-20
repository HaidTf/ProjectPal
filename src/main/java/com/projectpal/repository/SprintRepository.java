package com.projectpal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectpal.entity.Sprint;
@Repository
public interface SprintRepository extends JpaRepository<Sprint,Long>{

}
