package com.guard.admin.database.repositories;

import com.guard.admin.database.entities.Visitor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisitorRepository extends JpaRepository<Visitor, Integer> {
    List<Visitor> findBySiteId(Integer siteId);
}