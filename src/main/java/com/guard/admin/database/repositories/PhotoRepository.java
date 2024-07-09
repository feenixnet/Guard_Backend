package com.guard.admin.database.repositories;

import com.guard.admin.database.entities.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Integer> {
    List<Photo> findBySiteId(Integer siteId);
    Page<Photo> findAll(Specification<Photo> spec, Pageable pageable);

}