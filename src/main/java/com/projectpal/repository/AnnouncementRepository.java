package com.projectpal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectpal.entity.Announcement;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement,Long>{

}
