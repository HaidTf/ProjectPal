package com.projectpal.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectpal.entity.Invitation;
import com.projectpal.entity.Project;
import com.projectpal.entity.User;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation,Long> {

	Optional<List<Invitation>> findAllByInvitedUser(User invitedUser);

	Optional<List<Invitation>> findAllByProject(Project project);

	void deleteByIssueDateBefore(LocalDate xDateAgo);

	Page<Invitation> findAllByProject(Project project, Pageable pageable);

	List<Invitation> findAllByInvitedUser(User user, Sort sort);
	
}
