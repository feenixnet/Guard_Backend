package com.guard.admin.payload.dto;

import com.guard.admin.database.entities.HitPoints;
import com.guard.admin.database.entities.Shift;
import com.guard.admin.database.entities.Site;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ScheduleWithSiteHitPoints {

    private Integer id;
    private String startTime;
    private Double hours;
    private Double frequency;
    private String rule;
    private Site site;
    private List<HitPoints> hitPoints;
    private String announces;

    public ScheduleWithSiteHitPoints() {

    }
}