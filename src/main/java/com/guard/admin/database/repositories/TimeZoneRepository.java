package com.guard.admin.database.repositories;

import com.guard.admin.database.entities.Site;
import com.guard.admin.database.entities.TimeZone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeZoneRepository extends JpaRepository<TimeZone, Integer> {
}
