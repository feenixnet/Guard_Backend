package com.guard.admin.database.repositories;

import com.guard.admin.database.entities.Visitor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisitorRepository extends JpaRepository<Visitor, Integer> {
    List<Visitor> findBySiteId(Integer siteId);
    Page<Visitor> findAll(Specification<Visitor> spec, Pageable pageable);
}