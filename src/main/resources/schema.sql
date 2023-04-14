
--CREATIONS:

create table epic (
       id bigint not null,
        description varchar(255),
        name varchar(255),
        priority TINYINT,
        project_id bigint,
        primary key (id)
    );

    
    create table model_sequence (
       next_val bigint
    );
 
    
    insert into model_sequence values ( 1 );
 
    
    create table project (
       id bigint not null,
        description varchar(255),
        name varchar(255),
        owner_id bigint,
        primary key (id)
    ) ;

    
    create table sprint (
       id bigint not null,
        description varchar(255),
        end_date date,
        name varchar(255),
        start_date date,
        project_id bigint,
        primary key (id)
    ) ;

    
    create table task (
       id bigint not null,
        description varchar(255),
        name varchar(255),
        priority TINYINT,
        assigned_user_id bigint,
        user_story_id bigint,
        primary key (id)
    ) ;

    
    create table task_attachment (
       id bigint not null,
        file_name varchar(255),
        task_id bigint,
        primary key (id)
    ) ;

    
    create table users (
       id bigint not null,
        email varchar(100),
        password varchar(100),
        role varchar(10),
        username varchar(100),
        project_id bigint,
        primary key (id)
    ) ;

    
    create table user_story (
       id bigint not null,
        description varchar(255),
        name varchar(255),
        priority TINYINT,
        epic_id bigint,
        sprint_id bigint,
        primary key (id)
    ) ;

    --ALTERS:
    
    alter table users 
       add constraint UK_email unique (email);

    
    alter table users
       add constraint UK_username unique (username);
       
       alter table users
       	add constraint FKu_project_id
       	foreign key(project_id)
       	references project(id);

    
    alter table epic 
       add constraint FKe_project_id
       foreign key (project_id) 
       references project (id);

    
    alter table project 
       add constraint FKp_users_id
       foreign key (owner_id) 
       references users (id);

    
    alter table sprint 
       add constraint FKs_project_id
       foreign key (project_id) 
       references project (id);

    
    alter table task 
       add constraint FKt_users_id
       foreign key (assigned_user_id) 
       references users (id);

    
    alter table task 
       add constraint FK_usersStory_id
       foreign key (user_story_id) 
       references user_story (id);

    
    alter table task_attachment 
       add constraint FK_task_id
       foreign key (task_id) 
       references task (id);

    
    alter table user_story 
       add constraint FK_epic_id
       foreign key (epic_id) 
       references epic (id);
       
       alter table user_story 
       add constraint FK_sprint_id
       foreign key (sprint_id) 
       references sprint(id);
       
       --TRIGGERS:
       
	/*  CREATE TRIGGER delete_user_stories
			AFTER DELETE ON project
			FOR EACH ROW
			BEGIN
			  DELETE FROM user_story WHERE epic_id IN (
			    SELECT id FROM epic WHERE project_id = OLD.id
			  );
			END;  */
			
/* DELIMITER //

CREATE TRIGGER delete_user_stories
    BEFORE DELETE ON epic
    FOR EACH ROW
    BEGIN
        DELETE FROM user_story WHERE epic_id IN (
            SELECT id FROM epic WHERE project_id = OLD.project_id
        );
    END//
    
    DELIMITER ; */
    
    
    