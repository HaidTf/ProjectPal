package com.projectpal.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.ArrayList;

@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
public class SprintTest {
	@Autowired
	private TestEntityManager em;

	@Test
	public void testCreate() {
		Sprint sprint = new Sprint("sprint1", "description", LocalDate.of(2023, 11, 2), LocalDate.of(2023, 11, 10));

		em.persist(sprint);
		em.flush();

		assertNotNull(sprint.getId());

	}

	@Test
	public void testRead() {
		Sprint sprint = new Sprint("sprint1", "description", LocalDate.of(2023, 11, 2), LocalDate.of(2023, 11, 10));

		em.persist(sprint);
		em.flush();

		assertNotNull(em.find(Sprint.class, sprint.getId()));

	}

	@Test
	public void testUpdate() {
		Sprint sprint = new Sprint("sprint1", "description", LocalDate.of(2023, 11, 2), LocalDate.of(2023, 11, 10));

		em.persist(sprint);
		em.flush();

		Sprint foundSprint = em.find(Sprint.class, sprint.getId());

		foundSprint.setName("updatedSprint");

		em.persist(foundSprint);
		em.flush();

		Sprint updatedSprint = em.find(Sprint.class, sprint.getId());
		assertEquals(updatedSprint.getName(), "updatedSprint");

	}

	@Test
	public void testDelete() {
		Sprint sprint = new Sprint("sprint1", "description", LocalDate.of(2023, 11, 2), LocalDate.of(2023, 11, 10));

		em.persist(sprint);
		em.flush();

		em.remove(sprint);

		Sprint mustBeDeletedSprint = em.find(Sprint.class, sprint.getId());
		assertNull(mustBeDeletedSprint);
	}

	@Test
	public void testCascadeRemoveIfProjectIsDeleted() {
		Project project = new Project("Projectpal", "Description");
		em.persist(project);

		Sprint sprint = new Sprint("sprint1", "description", LocalDate.of(2023, 11, 2), LocalDate.of(2023, 11, 10));
		em.persist(sprint);

		project.setSprints(new ArrayList<Sprint>());
		project.addSprint(sprint);
		em.flush();

		em.remove(project);
		em.flush();

		Sprint mustBeDeletedSprint = em.find(Sprint.class, sprint.getId());
		assertNull(mustBeDeletedSprint);

	}
}
