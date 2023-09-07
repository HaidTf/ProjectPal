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
public class TaskTest {
	@Autowired
	private TestEntityManager em;

	@Test
	public void testCreate() {
		Task task = new Task("implement testing", "test business logic", (byte) 5);
		em.persist(task);
		em.flush();

		assertNotNull(task.getId());
	}

	@Test
	public void testRead() {
		Task task = new Task("implement testing", "test business logic", (byte) 5);
		em.persist(task);
		em.flush();
		
		assertNotNull(em.find(Task.class, task.getId()));
		
	}

	@Test
	public void testUpdate() {
		Task task = new Task("implement testing", "test business logic", (byte) 5);
		em.persist(task);
		em.flush();
		
		Task foundTask = em.find(Task.class, task.getId());
		foundTask.setName("implement api");
		em.persist(foundTask);
		em.flush();
		
		Task mustBeUpdatedTask = em.find(Task.class, foundTask.getId());
		assertEquals(mustBeUpdatedTask.getName(),"implement api");
	}

	@Test
	public void testDelete() {
		Task task = new Task("implement testing", "test business logic", (byte) 5);
		em.persist(task);
		em.flush();
		
		em.remove(task);
		em.flush();
		
		assertNull(em.find(Task.class, task.getId()));
	}
	
	@Test
	public void testCascadeRemoveIfUserStoryIsDeleted() {
		Task task = new Task("implement testing", "test business logic", (byte) 5);
		em.persist(task);
		
		UserStory userStory = new UserStory("userstory1", "description", (byte) 5);
		
		userStory.setTasks(new ArrayList<Task>());
		userStory.addTask(task);
		em.persist(userStory);
		em.flush();
		
		em.remove(userStory);
		em.flush();
		
		assertNull(em.find(Task.class, task.getId()));
		
		
		
		
	}
}
