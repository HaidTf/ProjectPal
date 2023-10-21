package com.projectpal.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.projectpal.dto.response.entity.ProjectInvitationResponseDto;
import com.projectpal.dto.response.entity.UserInvitationResponseDto;
import com.projectpal.entity.Invitation;
import com.projectpal.entity.Project;
import com.projectpal.entity.User;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {

	List<Invitation> findAllByInvitedUser(User invitedUser);

	List<Invitation> findAllByProject(Project project);

	void deleteByIssueDateBefore(LocalDate xDateAgo);

	@Query("SELECT new com.projectpal.dto.response.entity.ProjectInvitationResponseDto(i.id,i.issueDate,u.id,u.name) FROM Invitation i JOIN i.invitedUser u WHERE i.project = :project")
	Page<ProjectInvitationResponseDto> findProjectInvitationDtoPageByProject(@Param("project") Project project, Pageable pageable);

	@Query("SELECT new com.projectpal.dto.response.entity.UserInvitationResponseDto(i.id,i.issueDate,p.id,p.name) FROM Invitation i JOIN i.project p WHERE i.invitedUser = :user")
	List<UserInvitationResponseDto> findUserInvitationDtoListByInvitedUser(@Param("user") User user, Sort sort);

	@EntityGraph(attributePaths = { "invitedUser.id", "invitedUser.name" })
	Optional<Invitation> findSentInvitationById(long invitationId);

	@EntityGraph(attributePaths = { "project.id", "project.name" })
	Optional<Invitation> findReceivedInvitationById(long invitationId);

}
