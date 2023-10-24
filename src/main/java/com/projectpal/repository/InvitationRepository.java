package com.projectpal.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.projectpal.dto.response.entity.SentInvitationResponseDto;
import com.projectpal.dto.response.entity.ReceivedInvitationResponseDto;
import com.projectpal.entity.Invitation;
import com.projectpal.entity.Project;
import com.projectpal.entity.User;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {

	Optional<Invitation> findByIdAndInvitedUser(long invitationId, User currentUser);
	
	@Query("SELECT new com.projectpal.dto.response.entity.SentInvitationResponseDto(i.id,i.issueDate,u.id,u.name) FROM Invitation i JOIN i.invitedUser u WHERE i.id = :id AND i.project = :project")
	Optional<SentInvitationResponseDto> findSentInvitationDtoByIdAndProject(@Param("id") long id,
			@Param("project") Project project);

	@Query("SELECT new com.projectpal.dto.response.entity.ReceivedInvitationResponseDto(i.id,i.issueDate,p.id,p.name) FROM Invitation i JOIN i.project p WHERE i.id = :id AND i.invitedUser = :invitedUser")
	Optional<ReceivedInvitationResponseDto> findReceivedInvitationDtoByIdAndUser(@Param("id") long id,
			@Param("invitedUser") User invitedUser);

	List<Invitation> findAllByInvitedUser(User invitedUser);

	List<Invitation> findAllByProject(Project project);

	void deleteByIssueDateBefore(LocalDate xDateAgo);

	@Query("SELECT new com.projectpal.dto.response.entity.SentInvitationResponseDto(i.id,i.issueDate,u.id,u.name) FROM Invitation i JOIN i.invitedUser u WHERE i.project = :project")
	Page<SentInvitationResponseDto> findSentInvitationDtoPageByProject(@Param("project") Project project,
			Pageable pageable);

	@Query("SELECT new com.projectpal.dto.response.entity.ReceivedInvitationResponseDto(i.id,i.issueDate,p.id,p.name) FROM Invitation i JOIN i.project p WHERE i.invitedUser = :user")
	List<ReceivedInvitationResponseDto> findReceivedInvitationDtoListByInvitedUser(@Param("user") User user, Sort sort);

	

}
