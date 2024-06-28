package com.guard.admin.payload.response;

import java.util.List;

import com.guard.admin.database.entities.Event;
import com.guard.admin.payload.dto.SiteWithHitpoint;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ClientDashboardResponse {
    List<SiteWithHitpoint> siteList;
    List<Event> eventList;
}
