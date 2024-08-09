package com.guard.admin.database.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.guard.admin.database.entities.Client;

public interface ClientRepository extends JpaRepository<Client, Integer> {

    Page<Client> findAll(Specification<Client> spec, Pageable pageable);

    Optional<Client> findByEmail(String email);

    boolean existsByEmail(String email);
}