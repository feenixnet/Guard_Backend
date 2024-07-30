package com.guard.admin.database.repositories;


import com.guard.admin.database.entities.ScheduleMobile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ScheduleMobileRepository extends JpaRepository<ScheduleMobile, Integer> {

    List<ScheduleMobile> findByGuardIdAndStartTimeBetween(Integer guardId, Date startTime, Date endTime);

    List<ScheduleMobile> findByCarIdAndStartTimeBetween(Integer carId, Date startTime, Date endTime);

    List<ScheduleMobile> findByCarId(Integer carId);

    void deleteAllByCarId(Integer carId);
    void deleteAllByGuardId(Integer guardId);
}