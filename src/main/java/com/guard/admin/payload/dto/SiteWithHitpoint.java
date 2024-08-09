package com.guard.admin.payload.dto;

import com.guard.admin.database.entities.HitPoints;
import com.guard.admin.database.entities.Shift;
import com.guard.admin.database.entities.Site;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SiteWithHitpoint {
    Site site;
    List<HitPoints> hitPointsList;
    List<Shift> shiftList;
}
