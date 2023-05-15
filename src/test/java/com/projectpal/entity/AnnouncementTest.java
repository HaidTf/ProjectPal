package com.projectpal.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
public class AnnouncementTest {

	@Autowired
	private TestEntityManager em;
	
	
	@Test
	public void testCreate() {
		
		Announcement announcement = new Announcement("announcement1", "description");

		em.persist(announcement);
		em.flush();

		assertNotNull(announcement.getId());

	}

	@Test
	public void testRead() {

		Announcement announcement = new Announcement("announcement1", "description");

		em.persist(announcement);
		em.flush();

		Announcement foundannouncement = em.find(Announcement.class, announcement.getId());

		assertNotNull(foundannouncement);
	}

	@Test
	public void testUpdate() {
		
		Announcement announcement = new Announcement("announcement1", "description");

		em.persist(announcement);
		em.flush();

		Announcement foundannouncement = em.find(Announcement.class, announcement.getId());
		foundannouncement.setDescription("updatedDescription");

		em.persist(foundannouncement);
		em.flush();

		Announcement updatedannouncement = em.find(Announcement.class, foundannouncement.getId());

		assertEquals(updatedannouncement.getDescription(), "updatedDescription");

	}

	@Test
	public void testDelete() {

		Announcement announcement = new Announcement("announcement1", "description");

		em.persist(announcement);
		em.flush();

		em.remove(announcement);
		em.flush();

		Announcement mustBeDeletedannouncement = em.find(Announcement.class, announcement.getId());
		assertNull(mustBeDeletedannouncement);
	}
	
	@Test
	public void testCascadeRemoveIfProjectIsDeleted() {
		
		Project project = new Project("Projectpal", "Description");
		em.persist(project);
		
		Announcement announcement = new Announcement("announcement1", "description");
		em.persist(announcement);
		
		project.setAnnouncements(new ArrayList<Announcement>());
		project.addAnnouncement(announcement);
		em.flush();
		
		em.remove(project);
		em.flush();
		
		Announcement mustBeDeletedannouncement = em.find(Announcement.class, announcement.getId());
		assertNull(mustBeDeletedannouncement);
		
		
	}
}
