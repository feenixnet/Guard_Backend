package com.guard.admin.database.repositories;


import org.springframework.data.jpa.repository.JpaRepository;

import com.guard.admin.database.entities.Bug;


public interface BugRepository extends JpaRepository<Bug, Integer>  {
    
}
