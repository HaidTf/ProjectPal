package com.projectpal.entity;

import static org.junit.jupiter.api.Assertions.*;
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
public class UserStoryTest {
	
	@Autowired
	private TestEntityManager em;

	@Test
	public void testCreate() {

		UserStory userStory = new UserStory("userstory1", "description", (byte) 5);

		em.persist(userStory);
		em.flush();

		assertNotNull(userStory.getId());
	}

	@Test
	public void testRead() {
		UserStory userStory = new UserStory("userstory1", "description", (byte) 5);

		em.persist(userStory);
		em.flush();

		assertNotNull(em.find(UserStory.class, userStory.getId()));
	}

	@Test
	public void testUpdate() {
		UserStory userStory = new UserStory("userstory1", "description", (byte) 5);

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
		UserStory userStory = new UserStory("userstory1", "description", (byte) 5);

		em.persist(userStory);
		em.flush();

		em.remove(userStory);
		em.flush();

		assertNull(em.find(UserStory.class, userStory.getId()));
	}
	
	@Test
	public void testCascadeRemoveIfEpicIsDeleted() {
	Epic epic = new Epic("epic1", "description", (byte) 5);
	
	UserStory userStory = new UserStory("userstory1", "description", (byte) 5);
	
	epic.setUserStories(new ArrayList<UserStory>());
	epic.addUserStory(userStory);
	
	em.persist(userStory);
	em.persist(epic);
	em.flush();
	
	em.remove(epic);
	em.flush();
	
	assertNull(em.find(UserStory.class, userStory.getId()));
	
	
	}
	
}
