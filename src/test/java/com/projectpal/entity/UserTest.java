package com.projectpal.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.projectpal.entity.enums.Role;

@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
public class UserTest {

	@Autowired
	private TestEntityManager em;

	@Test
	public void testCreate() {
		User user = new User("haid", "haidar@gmail.com", "1234");
		user.setRole(Role.ROLE_USER);
		em.persist(user);
		em.flush();

		assertNotNull(user.getId());
	}

	@Test
	public void testRead() {
		User user = new User("haid", "haidar@gmail.com", "1234");
		user.setRole(Role.ROLE_USER);
		em.persist(user);
		em.flush();

		User foundUser = em.find(User.class, user.getId());

		assertNotNull(foundUser);
		assertEquals(user.getId(), foundUser.getId());
	}

	@Test
	public void testUpdate() {
		User user = new User("haid", "haidar@gmail.com", "1234");
		user.setRole(Role.ROLE_USER);
		em.persist(user);
		em.flush();

		User foundUser = em.find(User.class, user.getId());
		assertNotNull(foundUser);
		
		Project project = new Project("Projectpal", "Description");
		project.setOwner(foundUser);
		em.persist(project);
		
		foundUser.setProject(project);

		em.persist(foundUser);
		em.flush();

		User updatedUser = em.find(User.class, user.getId());
		assertEquals(updatedUser.getProject().getName(), "Projectpal");
	}

	@Test
	public void testDelete() {
		User user = new User("haid", "haidar@gmail.com", "1234");
		user.setRole(Role.ROLE_USER);
		em.persist(user);
		em.flush();

		em.remove(user);
		em.flush();

		User mustBeDeletedUser = em.find(User.class, user.getId());

		assertNull(mustBeDeletedUser);

	}

}
