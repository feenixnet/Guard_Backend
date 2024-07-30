package com.guard.admin.database.repositories;

import com.guard.admin.database.entities.ReportPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportPhotoRepository extends JpaRepository<ReportPhoto, Integer> {
    List<ReportPhoto> findByReportId(Integer reportId);
    void deleteByReportId(Integer reportId);
}