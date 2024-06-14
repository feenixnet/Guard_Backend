package com.guard.admin.database.repositories;

import com.guard.admin.database.entities.Guard;
import com.guard.admin.database.entities.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    List<Schedule> findByGuardIdAndStartTimeBetween(Integer guardId, Date startTime, Date endTime);

    List<Schedule> findBySiteIdAndStartTimeBetween(Integer siteId, Date startTime, Date endTime);

    List<Schedule> findBySiteId(Integer siteId);

    void deleteAllBySiteId(Integer siteId);
}