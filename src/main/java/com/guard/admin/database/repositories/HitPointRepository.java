package com.guard.admin.database.repositories;

import com.guard.admin.database.entities.HitPoints;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HitPointRepository extends JpaRepository<HitPoints, Integer> {
    List<HitPoints> findAllBySiteId(Integer id);

    void deleteAllBySiteId(Integer siteId);
}
