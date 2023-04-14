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
public class ProjectTest {

    @Autowired
    private TestEntityManager em;

    @Test
    public void testCreate() {
        Project project = new Project("Projectpal", "Description", null);
        em.persist(project);
        em.flush();

        assertNotNull(project.getId());
    }

    @Test
    public void testRead() {
        Project project = new Project("Projectpal", "Description", null);
        em.persist(project);
        em.flush();

        Project foundProject = em.find(Project.class, project.getId());

        assertNotNull(foundProject);
        assertEquals(project.getId(), foundProject.getId());
    }
    
    @Test
    public void testUpdate() {
    	Project project = new Project("Projectpal", "Description", null);
        em.persist(project);
        em.flush();
        
        Project foundProject = em.find(Project.class, project.getId());
        
        foundProject.setName("Projectpal2");
        foundProject.setDescription("Description2");
        
        em.persist(foundProject);
        em.flush();
        
        Project updatedProject = em.find(Project.class, project.getId());
        
        
        assertEquals(updatedProject.getName(),"Projectpal2");
        assertEquals(updatedProject.getDescription(),"Description2");
    }
    
    @Test
    public void testDelete() {
    	Project project = new Project("Projectpal", "Description", null);
        em.persist(project);
        em.flush();
        
        em.remove(project);
        em.flush();
        
        assertNull(em.find(Project.class, project.getId()));
    }
    
}

    