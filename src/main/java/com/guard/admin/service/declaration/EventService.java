package com.guard.admin.service.declaration;

import com.guard.admin.database.entities.Event;

import java.util.Date;
import java.util.List;

public interface EventService {

    Event create(Event event);

    List<Event> getPage(Integer siteId, Integer userId, Integer guardId , int pageNum, int pageSize, String search, Date startDate, Date endDate);
}
