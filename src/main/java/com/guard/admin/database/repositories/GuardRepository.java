package com.guard.admin.database.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.guard.admin.database.entities.Guard;

public interface GuardRepository extends JpaRepository<Guard, Integer> {

    Page<Guard> findAll(Specification<Guard> spec, Pageable pageable);

    Optional<Guard> findByEmail(String email);

    List<Guard> findByTypeAndStatus(String type, Boolean status);

    boolean existsByEmail(String email);
}