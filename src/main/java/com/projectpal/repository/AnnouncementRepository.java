package com.projectpal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectpal.entity.Announcement;
import com.projectpal.entity.Project;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement,Long>{

	Optional<List<Announcement>> findAllByProject(Project project);

}
