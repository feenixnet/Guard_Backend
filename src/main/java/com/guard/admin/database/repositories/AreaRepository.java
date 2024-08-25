package com.guard.admin.database.repositories;



import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.guard.admin.database.entities.Area;
import com.guard.admin.database.entities.Car;

public interface AreaRepository extends JpaRepository<Area, Integer> {

    List<Area> findAll(Specification<Area> spec);
    
}
