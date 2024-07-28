package com.guard.admin.database.repositories;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.guard.admin.database.entities.Car;

public interface CarRepository extends JpaRepository<Car, Integer> {

    Page<Car> findAll(Specification<Car> spec, Pageable pageable);
    
}
