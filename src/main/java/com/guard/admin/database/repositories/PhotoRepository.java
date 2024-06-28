package com.guard.admin.database.repositories;

import com.guard.admin.database.entities.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Integer> {
    List<Photo> findBySiteId(Integer reportId);
}