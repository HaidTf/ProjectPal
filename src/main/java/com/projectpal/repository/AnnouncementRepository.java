package com.projectpal.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.projectpal.dto.response.entity.AnnouncementResponseDto;
import com.projectpal.entity.Announcement;
import com.projectpal.entity.Project;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

	List<Announcement> findAllByProject(Project project);

	void deleteByIssueDateBefore(LocalDate xDateAgo);

	Page<Announcement> findAllByProject(Project project, Pageable pageable);

	@Query("SELECT new com.projectpal.dto.response.entity.AnnouncementResponseDto(a.id,a.title,a.description,a.issueDate,u.id,u.name) FROM Announcement a JOIN a.announcer u WHERE a.project = :project")
	Page<AnnouncementResponseDto> findAnnouncementDtoPageByProject(@Param("project") Project project,
			Pageable pageable);

}
