package com.projectpal.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureTestEntityManager()
@ActiveProfiles("development")
@Transactional
public class InvitationTest {

	@Autowired
	private TestEntityManager em;

	
	@Test
	public void testCreate() {
		
		Invitation invitation = new Invitation();

		em.persist(invitation);
		em.flush();

		assertNotNull(invitation.getId());

	}

	@Test
	public void testRead() {

		Invitation invitation = new Invitation();

		em.persist(invitation);
		em.flush();

		Invitation foundinvitation = em.find(Invitation.class, invitation.getId());

		assertNotNull(foundinvitation);
	}

	@Test
	public void testUpdate() {
		
		Invitation invitation = new Invitation();
		em.persist(invitation);
		
		Project project = new Project("Projectpal", "Description");
		em.persist(project);
		
		em.flush();
		
		Invitation foundinvitation = em.find(Invitation.class, invitation.getId());
		foundinvitation.setProject(project);

		em.persist(foundinvitation);
		em.flush();

		Invitation updatedinvitation = em.find(Invitation.class, foundinvitation.getId());

		assertEquals(updatedinvitation.getProject().getId(), project.getId());

	}

	@Test
	public void testDelete() {

		Invitation invitation = new Invitation();

		em.persist(invitation);
		em.flush();

		em.remove(invitation);
		em.flush();

		Invitation mustBeDeletedinvitation = em.find(Invitation.class, invitation.getId());
		assertNull(mustBeDeletedinvitation);
	}
	
	@Test
	public void testCascadeRemoveIfProjectIsDeleted() {
		Project project = new Project("Projectpal", "Description");
		em.persist(project);
		
		Invitation invitation = new Invitation();
		em.persist(invitation);
		
		project.setInvitations(new ArrayList<Invitation>());
		project.addInvitation(invitation);
		em.flush();
		
		em.remove(project);
		em.flush();
		
		Invitation mustBeDeletedinvitation = em.find(Invitation.class, invitation.getId());
		assertNull(mustBeDeletedinvitation);
		
		
	}
}
