package com.guard.admin.service.impl;

import com.guard.admin.database.entities.Schedule;
import com.guard.admin.database.entities.Site;
import com.guard.admin.database.entities.Guard;
import com.guard.admin.database.repositories.GuardRepository;
import com.guard.admin.database.repositories.ScheduleRepository;
import com.guard.admin.payload.dto.Appointment;
import com.guard.admin.payload.dto.ScheduleData;
import com.guard.admin.payload.dto.ScheduleWithSiteHitPoints;
import com.guard.admin.service.declaration.ScheduleService;
import com.guard.admin.service.declaration.SiteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    
    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private SiteService siteService;

    @Autowired
    private GuardRepository guardRepository;

    private static final Logger logger = LoggerFactory.getLogger(com.guard.admin.service.declaration.ScheduleService.class);

    static Date getStartOfDay(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDateTime startOfDay = localDate.atStartOfDay();
        return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    static Date getEndOfDay(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);
        return Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    @Override
    @Transactional
    public ScheduleData save(ScheduleData scheduleData) {

        scheduleRepository.deleteAllBySiteId(scheduleData.getSiteId());

        Site site = siteService.getSite(scheduleData.getSiteId());

        for(Appointment appointment : scheduleData.getAppointmentList()) {
            Instant utcDateTime = appointment.getStartDate().toInstant();
            ZoneId zoneId = ZoneId.of(ZoneId.systemDefault().toString());
            ZonedDateTime localDateTime = utcDateTime.atZone(zoneId);
            Guard guard = guardRepository.findById(appointment.getGuardId()).get();
            Schedule schedule = new Schedule();
            schedule.setSite(site);
            // schedule.setRule(appointment.getDescription());
            schedule.setStartTime(Date.from(localDateTime.toInstant()));
            schedule.setEndTime(appointment.getEndDate());
            schedule.setGuard(guard);
            schedule.setHours(appointment.getHours());
            schedule.setFrequency(appointment.getFrequency());
            schedule.setAnnounces(appointment.getAnnounces());

            scheduleRepository.save(schedule);
        }
        return scheduleData;
    }

    @Override
    public List<Schedule> findAll() {
        return scheduleRepository.findAll();
    }

    @Override
    public Set<Guard> findGuardsSiteList(List<Site> siteList) {
        Set<Guard> guardList = new HashSet<>();
        for(Site site: siteList) {
            guardList.addAll(scheduleRepository.findBySiteIdAndStartTimeBetween(site.getId(), getStartOfDay(new Date()), getEndOfDay(new Date()))
                    .stream()
                    .map(Schedule::getGuard)
                    .collect(Collectors.toList()));
        }

        return guardList;
    }

    @Override
    public Set<Guard> findGuardsBySiteId(Integer siteId) {
        return scheduleRepository.findBySiteIdAndStartTimeBetween(siteId, getStartOfDay(new Date()) , getEndOfDay(new Date()))
                .stream().map(Schedule::getGuard).collect(Collectors.toSet());
    }

    @Override
    public Set<Site> findSitesByGuardId(Integer guardId) {
        return scheduleRepository.findByGuardIdAndStartTimeBetween(guardId, getStartOfDay(new Date()) , getEndOfDay(new Date())).stream()
                .map(Schedule::getSite)
                .collect(Collectors.toSet());
    }

    @Override
    public List<Schedule> planForManager(String role, Integer siteId) {
        return scheduleRepository.findBySiteId(siteId);
    }

    @Override
    public List<ScheduleWithSiteHitPoints> planForGuard(Integer guardId, Date startTime, Date endTime) {
        Date date = new Date();
//        List<Schedule> scheduleList = scheduleRepository.findByGuardIdAndStartTimeAfter(guardId, getStartOfDay(date));
//        List<Schedule> scheduleList = scheduleRepository.findByGuardId(guardId);
        List<Schedule> scheduleList = scheduleRepository.findByGuardIdAndStartTimeBetween(guardId, startTime, endTime);
        List<ScheduleWithSiteHitPoints> scheduleData = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        System.out.println("planForGurad");
        System.out.println(scheduleList.size());

        for (Schedule schedule : scheduleList) {
            ScheduleWithSiteHitPoints scheduleWithSiteHitPoints = new ScheduleWithSiteHitPoints();

            scheduleWithSiteHitPoints.setId(schedule.getId());
            scheduleWithSiteHitPoints.setStartTime( formatter.format(schedule.getStartTime()) );
            scheduleWithSiteHitPoints.setHours(schedule.getHours());
            scheduleWithSiteHitPoints.setFrequency(schedule.getFrequency());
            // scheduleWithSiteHitPoints.setRule(schedule.getRule());
            scheduleWithSiteHitPoints.setSite(schedule.getSite());
            scheduleWithSiteHitPoints.setHitPoints(siteService.getFull(schedule.getSite().getId()).getHitPointsList());
            scheduleWithSiteHitPoints.setAnnounces(schedule.getAnnounces());

//            scheduleWithSiteHitPoints.setShifts(siteService.getSite(schedule.getSite().getId()).getShiftList());

            scheduleData.add(scheduleWithSiteHitPoints);
        }

        return scheduleData;
    }
}
