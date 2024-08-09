package com.guard.admin.database.repositories;

import com.guard.admin.database.entities.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, Integer> {
    List<Shift> findAllBySiteId(Integer id);
    void deleteAllBySiteId(Integer siteId);
}
