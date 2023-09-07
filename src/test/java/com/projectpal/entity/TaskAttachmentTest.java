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
public class TaskAttachmentTest {
	@Autowired
	private TestEntityManager em;

	@Test
	public void testCreate() {
		TaskAttachment attachment = new TaskAttachment("img.png");

		em.persist(attachment);
		em.flush();

		assertNotNull(attachment.getId());
	}

	@Test
	public void testRead() {
		TaskAttachment attachment = new TaskAttachment("img.png");

		em.persist(attachment);
		em.flush();
		
		assertNotNull(em.find(TaskAttachment.class, attachment.getId()));
		
	}

	@Test
	public void testUpdate() {
		TaskAttachment attachment = new TaskAttachment("img.png");

		em.persist(attachment);
		em.flush();
		
		TaskAttachment foundAttachment = em.find(TaskAttachment.class, attachment.getId());
		foundAttachment.setFileName("video.mp4");
		em.persist(foundAttachment);
		em.flush();
		
		TaskAttachment mustBeUpdatedAttachment = em.find(TaskAttachment.class, foundAttachment.getId());
		assertEquals(mustBeUpdatedAttachment.getFileName(),"video.mp4");
	}

	@Test
	public void testDelete() {
		TaskAttachment attachment = new TaskAttachment("img.png");

		em.persist(attachment);
		em.flush();
		
		em.remove(attachment);
		em.flush();
		
		assertNull(em.find(TaskAttachment.class, attachment.getId()));
		
	}
	
	@Test
	public void testCascadeRemoveIfTaskIsDeleted() {
		TaskAttachment attachment = new TaskAttachment("img.png");
		em.persist(attachment);
		
		Task task = new Task("implement testing", "test business logic", (byte) 5);
		
		task.setTaskAttachments(new ArrayList<TaskAttachment>());
		task.addTaskAttachment(attachment);
		em.persist(task);
		
		em.flush();
		
		em.remove(task);
		em.flush();
		
		TaskAttachment mustBeDeletedTaskAttachment = em.find(TaskAttachment.class, attachment.getId());
		
		assertNull(mustBeDeletedTaskAttachment);
		
		
	}
}
