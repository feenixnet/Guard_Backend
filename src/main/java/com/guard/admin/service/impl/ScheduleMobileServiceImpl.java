package com.guard.admin.service.impl;

import com.guard.admin.database.entities.ScheduleMobile;
import com.guard.admin.database.entities.Car;
import com.guard.admin.database.entities.Guard;
import com.guard.admin.database.repositories.GuardRepository;
import com.guard.admin.database.repositories.ScheduleMobileRepository;
import com.guard.admin.payload.dto.Appointment;
import com.guard.admin.payload.dto.ScheduleMobileData;
import com.guard.admin.service.declaration.ScheduleMobileService;
import com.guard.admin.service.declaration.CarService;
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
public class ScheduleMobileServiceImpl implements ScheduleMobileService {
    
    @Autowired
    private ScheduleMobileRepository scheduleMobileRepository;

    @Autowired
    private CarService carService;

    @Autowired
    private GuardRepository guardRepository;


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
    public ScheduleMobileData save(ScheduleMobileData scheduleMobileData) {

        scheduleMobileRepository.deleteAllByCarId(scheduleMobileData.getCarId());

        Car car = carService.get(scheduleMobileData.getCarId());

        for(Appointment appointment : scheduleMobileData.getAppointmentList()) {
            Instant utcDateTime = appointment.getStartDate().toInstant();
            ZoneId zoneId = ZoneId.of(ZoneId.systemDefault().toString());
            ZonedDateTime localDateTime = utcDateTime.atZone(zoneId);
            Guard guard = guardRepository.findById(appointment.getGuardId()).get();
            ScheduleMobile scheduleMobile = new ScheduleMobile();
            scheduleMobile.setCar(car);
            // schedule.setRule(appointment.getDescription());
            scheduleMobile.setStartTime(Date.from(localDateTime.toInstant()));
            scheduleMobile.setEndTime(appointment.getEndDate());
            scheduleMobile.setGuard(guard);
            scheduleMobile.setHours(appointment.getHours());
            scheduleMobile.setFrequency(appointment.getFrequency());
            scheduleMobile.setAnnounces(appointment.getAnnounces());

            scheduleMobileRepository.save(scheduleMobile);
        }
        return scheduleMobileData;
    }

    @Override
    public List<ScheduleMobile> findAll() {
        return scheduleMobileRepository.findAll();
    }

    @Override
    public Set<Guard> findGuardsCarList(List<Car> carList) {
        Set<Guard> guardList = new HashSet<>();
        for(Car car: carList) {
            guardList.addAll(scheduleMobileRepository.findByCarIdAndStartTimeBetween(car.getId(), getStartOfDay(new Date()), getEndOfDay(new Date()))
                    .stream()
                    .map(ScheduleMobile::getGuard)
                    .collect(Collectors.toList()));
        }

        return guardList;
    }

    @Override
    public Set<Guard> findGuardsByCarId(Integer carId) {
        return scheduleMobileRepository.findByCarIdAndStartTimeBetween(carId, getStartOfDay(new Date()) , getEndOfDay(new Date()))
                .stream().map(ScheduleMobile::getGuard).collect(Collectors.toSet());
    }

    @Override
    public Set<Car> findSitesByGuardId(Integer guardId) {
        return scheduleMobileRepository.findByGuardIdAndStartTimeBetween(guardId, getStartOfDay(new Date()) , getEndOfDay(new Date())).stream()
                .map(ScheduleMobile::getCar)
                .collect(Collectors.toSet());
    }

    @Override
    public List<ScheduleMobile> planForManager(String role, Integer carId) {
        return scheduleMobileRepository.findByCarId(carId);
    }

    @Override
    public List<ScheduleMobile> planForGuard(Integer guardId, Date startTime, Date endTime) {
        // Date date = new Date();
//        List<Schedule> scheduleList = scheduleRepository.findByGuardIdAndStartTimeAfter(guardId, getStartOfDay(date));
//        List<Schedule> scheduleList = scheduleRepository.findByGuardId(guardId);
        List<ScheduleMobile> scheduleMobileList = scheduleMobileRepository.findByGuardIdAndStartTimeBetween(guardId, startTime, endTime);

        return scheduleMobileList;
    }
}
