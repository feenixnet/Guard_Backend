package com.guard.admin.service.impl;

import com.guard.admin.database.entities.TimeZone;
import com.guard.admin.database.repositories.TimeZoneRepository;
import com.guard.admin.service.declaration.TimeZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TimeZoneServiceImpl implements TimeZoneService {
    @Autowired
    TimeZoneRepository timeZoneRepository;

    @Override
    public String put(String timezone) {
        TimeZone timeZone = timeZoneRepository.findById(1).get();
        timeZone.setZone(timezone);
        timeZoneRepository.save(timeZone);

        return timezone;
    }

    @Override
    public String get() {
        TimeZone timeZone = timeZoneRepository.findById(1).get();
        return timeZone.getZone();
    }
}
