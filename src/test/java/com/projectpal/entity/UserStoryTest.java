package com.projectpal.entity;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
public class UserStoryTest {
	
	@Autowired
	private TestEntityManager em;

	@Test
	public void testCreate() {

		UserStory userStory = new UserStory("userstory1", "description", (byte) 5, null, null);

		em.persist(userStory);
		em.flush();

		assertNotNull(userStory.getId());
	}

	@Test
	public void testRead() {
		UserStory userStory = new UserStory("userstory1", "description", (byte) 5, null, null);

		em.persist(userStory);
		em.flush();

		assertNotNull(em.find(UserStory.class, userStory.getId()));
	}

	@Test
	public void testUpdate() {
		UserStory userStory = new UserStory("userstory1", "description", (byte) 5, null, null);

		em.persist(userStory);
		em.flush();

		UserStory foundUserStory = em.find(UserStory.class, userStory.getId());
		foundUserStory.setName("updateduserstory");

		em.persist(foundUserStory);
		em.flush();

		UserStory updatedUserStory = em.find(UserStory.class, userStory.getId());
		assertEquals(updatedUserStory.getName(), "updateduserstory");

	}

	@Test
	public void testDelete() {
		UserStory userStory = new UserStory("userstory1", "description", (byte) 5, null, null);

		em.persist(userStory);
		em.flush();

		em.remove(userStory);
		em.flush();

		assertNull(em.find(UserStory.class, userStory.getId()));
	}
	
	@Test
	public void testCascadeRemoveIfEpicIsDeleted() {
	Epic epic = new Epic("epic1", "description", (byte) 5, null);
	
	UserStory userStory = new UserStory("userstory1", "description", (byte) 5, null, null);
	
	epic.addUserStory(userStory);
	
	em.persist(userStory);
	em.persist(epic);
	em.flush();
	
	em.remove(epic);
	em.flush();
	
	assertNull(em.find(UserStory.class, userStory.getId()));
	
	
	}
	
}
