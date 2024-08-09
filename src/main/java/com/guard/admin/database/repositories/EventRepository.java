package com.guard.admin.database.repositories;

import com.guard.admin.database.entities.Event;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Integer> {
    Page<Event> findAll(Specification<Event> spec, Pageable pageable);
    List<Event> findTop100BySiteIdInOrderByTimestampDesc(List<Integer> siteIds);
}
