package com.guard.admin.database.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.guard.admin.database.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    Page<User> findAll(Specification<User> spec, Pageable pageable);

    List<User> findAllByRole(String role);

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);
}