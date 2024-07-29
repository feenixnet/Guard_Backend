package com.guard.admin.service.declaration;

import com.guard.admin.database.entities.ScheduleMobile;
import com.guard.admin.database.entities.Car;
import com.guard.admin.database.entities.Guard;
import com.guard.admin.payload.dto.ScheduleMobileData;
// import com.guard.admin.payload.dto.ScheduleWithSiteHitPoints;

import java.util.*;

public interface ScheduleMobileService {

    ScheduleMobileData save(ScheduleMobileData scheduleData) ;

    List<ScheduleMobile> planForManager(String role, Integer siteId);

    List<ScheduleMobile> planForGuard(Integer guardId, Date startTime, Date endTime);

    List<ScheduleMobile> findAll() ;

    Set<Guard> findGuardsCarList(List<Car> carList) ;

    Set<Guard> findGuardsByCarId(Integer carId);

    Set<Car> findSitesByGuardId(Integer guardId);
}
