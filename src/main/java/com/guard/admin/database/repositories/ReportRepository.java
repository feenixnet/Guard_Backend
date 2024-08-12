package com.guard.admin.database.repositories;

import com.guard.admin.database.entities.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Integer> {
    Page<Report> findAll(Specification<Report> spec, Pageable pageable);
    void deleteAllBySiteId(Integer siteId);
    void deleteAllByGuardId(Integer guardId);

    List<Report> findAllBySiteId(Integer siteId);
    Optional<Report> findById(Integer reportId);
}
