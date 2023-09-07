package com.projectpal.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;


@SpringBootTest
@AutoConfigureTestEntityManager()
@ActiveProfiles("development")
@Transactional
public class EpicTest {

	@Autowired
	private TestEntityManager em;

	@Test
	public void testCreate() {
		
		Epic epic = new Epic("epic1", "description", (byte) 5);

		em.persist(epic);
		em.flush();

		assertNotNull(epic.getId());

	}

	@Test
	public void testRead() {

		Epic epic = new Epic("epic1", "description", (byte) 5);

		em.persist(epic);
		em.flush();

		Epic foundEpic = em.find(Epic.class, epic.getId());

		assertNotNull(foundEpic);
	}

	@Test
	public void testUpdate() {
		
		Epic epic = new Epic("epic1", "description", (byte) 5);

		em.persist(epic);
		em.flush();

		Epic foundEpic = em.find(Epic.class, epic.getId());
		foundEpic.setName("updatedName");
		foundEpic.setDescription("updatedDescription");

		em.persist(foundEpic);
		em.flush();

		Epic updatedEpic = em.find(Epic.class, foundEpic.getId());

		assertEquals(updatedEpic.getName(), "updatedName");

	}

	@Test
	public void testDelete() {

		Epic epic = new Epic("epic1", "description", (byte) 5);

		em.persist(epic);
		em.flush();

		em.remove(epic);
		em.flush();

		Epic mustBeDeletedEpic = em.find(Epic.class, epic.getId());
		assertNull(mustBeDeletedEpic);
	}
	
	@Test
	public void testCascadeRemoveIfProjectIsDeleted() {
		Project project = new Project("Projectpal", "Description");
		em.persist(project);
		
		Epic epic = new Epic("epic1", "description", (byte) 5);
		em.persist(epic);
		
		project.setEpics(new ArrayList<Epic>());
		project.addEpic(epic);
		em.flush();
		
		em.remove(project);
		em.flush();
		
		Epic mustBeDeletedEpic = em.find(Epic.class, epic.getId());
		assertNull(mustBeDeletedEpic);
		
		
	}
}
