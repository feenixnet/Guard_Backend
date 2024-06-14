package com.guard.admin.service.declaration;

import com.guard.admin.database.entities.Schedule;
import com.guard.admin.database.entities.Site;
import com.guard.admin.database.entities.Guard;
import com.guard.admin.payload.dto.ScheduleData;
import com.guard.admin.payload.dto.ScheduleWithSiteHitPoints;

import java.util.*;

public interface ScheduleService {

    ScheduleData save(ScheduleData scheduleData) ;

    List<Schedule> planForManager(String role, Integer siteId);

    List<ScheduleWithSiteHitPoints> planForGuard(Integer guardId, Date startTime, Date endTime);

    List<Schedule> findAll() ;

    Set<Guard> findGuardsSiteList(List<Site> siteList) ;

    Set<Guard> findGuardsBySiteId(Integer siteId);

    Set<Site> findSitesByGuardId(Integer guardId);
}
