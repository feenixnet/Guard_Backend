package com.guard.admin.database.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.guard.admin.database.entities.Site;

public interface SiteRepository extends JpaRepository<Site, Integer> {

    Page<Site> findAll(Specification<Site> spec, Pageable pageable);

    List<Site> findAllByClientId(Integer id);

    List<Site> findAllByUserId(Integer id);

    List<Site> findAllByCarId(Integer id);

    long countByUserId(Integer id);
    long countByClientId(Integer id);
}