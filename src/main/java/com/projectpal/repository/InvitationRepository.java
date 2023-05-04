package com.projectpal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectpal.entity.Invitation;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation,Long> {

}
