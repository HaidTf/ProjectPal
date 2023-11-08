CREATE TABLE announcement
(
   id BIGINT NOT NULL,
   description VARCHAR (300),
   issue_date DATE NOT NULL,
   title VARCHAR (100) NOT NULL,
   announcer_id BIGINT,
   project_id BIGINT,
   PRIMARY KEY (id)
);

CREATE TABLE epic
(
   id BIGINT NOT NULL,
   creation_date DATE NOT NULL,
   description VARCHAR (300),
   name VARCHAR (60) NOT NULL,
   priority TINYINT NOT NULL,
   progress VARCHAR (255) NOT NULL,
   project_id BIGINT,
   PRIMARY KEY (id)
);

CREATE TABLE invitation
(
   id BIGINT NOT NULL,
   issue_date DATE NOT NULL,
   invited_user_id BIGINT,
   project_id BIGINT,
   PRIMARY KEY (id)
);

 CREATE TABLE project
(
   id BIGINT NOT NULL,
   creation_date DATE NOT NULL,
   description VARCHAR (300),
   name VARCHAR (60) NOT NULL,
   owner_id BIGINT,
   PRIMARY KEY (id)
);

CREATE TABLE sprint
(
   id BIGINT NOT NULL,
   creation_date DATE NOT NULL,
   description VARCHAR (300),
   end_date DATE NOT NULL,
   name VARCHAR (60) NOT NULL,
   progress VARCHAR (255) NOT NULL,
   start_date DATE NOT NULL,
   project_id BIGINT,
   PRIMARY KEY (id)
);

CREATE TABLE task
(
   id BIGINT NOT NULL,
   creation_date DATE NOT NULL,
   description VARCHAR (300),
   name VARCHAR (60) NOT NULL,
   priority TINYINT NOT NULL,
   progress VARCHAR (255) NOT NULL,
   report VARCHAR (500),
   assigned_user_id BIGINT,
   project_id BIGINT,
   user_story_id BIGINT,
   PRIMARY KEY (id)
);

CREATE TABLE task_attachment
(
   id BIGINT NOT NULL,
   creation_date DATE NOT NULL,
   file_name VARCHAR (500) NOT NULL,
   file_size BIGINT NOT NULL,
   mime_type VARCHAR (100) NOT NULL,
   task_id BIGINT,
   PRIMARY KEY (id)
);

CREATE TABLE users
(
   id BIGINT NOT NULL,
   creation_date DATE NOT NULL,
   email VARCHAR (320) NOT NULL,
   name VARCHAR (20) NOT NULL,
   password VARCHAR (127) NOT NULL,
   role VARCHAR (255) NOT NULL,
   project_id BIGINT,
   PRIMARY KEY (id)
);

CREATE TABLE user_story
(
   id BIGINT NOT NULL,
   creation_date DATE NOT NULL,
   description VARCHAR (300),
   name VARCHAR (60) NOT NULL,
   priority TINYINT NOT NULL,
   progress VARCHAR (255) NOT NULL,
   epic_id BIGINT,
   sprint_id BIGINT,
   PRIMARY KEY (id)
);

CREATE TABLE model_sequence
(
   next_val BIGINT
);

insert into model_sequence values (1);

ALTER TABLE users ADD CONSTRAINT UK_6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email);

ALTER TABLE users ADD CONSTRAINT UK_3g1j96g94xpk3lpxl2qbl985x UNIQUE (name);
 
ALTER TABLE announcement ADD CONSTRAINT FKm5wltgk5voes17xoyi9fs3e16 FOREIGN KEY (announcer_id) REFERENCES users (id);
  
ALTER TABLE announcement ADD CONSTRAINT FKpyfdmg34m231qu9k7mna6jfc4 FOREIGN KEY (project_id) REFERENCES project (id);
   
ALTER TABLE epic ADD CONSTRAINT FKj6wn7xcnmotfjj5tkpq2b1qkh FOREIGN KEY (project_id) REFERENCES project (id) ;
    
ALTER TABLE invitation ADD CONSTRAINT FK8hd0i7xj9gy3hahlyk85qc58r FOREIGN KEY (invited_user_id) REFERENCES users (id) ;
    
ALTER TABLE invitation ADD CONSTRAINT FKrnqpgfqkk0oux4i1nw4jtoulf FOREIGN KEY (project_id) REFERENCES project (id) ;
    
ALTER TABLE project ADD CONSTRAINT FK7tetln4r9qig7tp05lsdqe8xo FOREIGN KEY (owner_id) REFERENCES users (id);
    
ALTER TABLE sprint ADD CONSTRAINT FKerwve0blrvfhqm1coxo69f0xr FOREIGN KEY (project_id) REFERENCES project (id); 

ALTER TABLE task ADD CONSTRAINT FKg2fon1f6hw8y0g6sl4gvp0vmf FOREIGN KEY (assigned_user_id) REFERENCES users (id);

ALTER TABLE task ADD CONSTRAINT FKk8qrwowg31kx7hp93sru1pdqa FOREIGN KEY (project_id) REFERENCES project (id) ;
 
ALTER TABLE task ADD CONSTRAINT FKlso25fkuj3mijovbxi6md7c39 FOREIGN KEY (user_story_id) REFERENCES user_story (id);
 
ALTER TABLE task_attachment ADD CONSTRAINT FKkhw6fprv9kv6uio43mem40px6 FOREIGN KEY (task_id) REFERENCES task (id) ;

ALTER TABLE users ADD CONSTRAINT FKdbmgdlpkonl25aiqghmhywhlg FOREIGN KEY (project_id) REFERENCES project (id);

ALTER TABLE user_story ADD CONSTRAINT FKd7pdcvsqyka7y7i6usss5awcy FOREIGN KEY (epic_id) REFERENCES epic (id) ;
 
ALTER TABLE user_story ADD CONSTRAINT FKb1dan6s8h4tdply2gbj0qat4d FOREIGN KEY (sprint_id) REFERENCES sprint (id);
    
    